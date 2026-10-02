/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.codec.oscal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import gov.nist.csrc.ns.oscal.Catalog;
import gov.nist.csrc.ns.oscal.CatalogGroup;
import gov.nist.csrc.ns.oscal.Control;
import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.OSCALPackage;
import gov.nist.csrc.ns.oscal.Parameter;
import gov.nist.csrc.ns.oscal.Part;
import gov.nist.csrc.ns.oscal.Profile;
import gov.nist.csrc.ns.oscal.ProfileSetParameter;
import gov.nist.csrc.ns.oscal.Property;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * The Grundschutz++ catalog and profile through the generated OSCAL API, the way a consumer reads
 * them.
 * <p>
 * Where {@link BsiRoundTripTest} checks reflectively, this test walks every group, control, part,
 * parameter and property of the resolved catalog with the typed getters and compares it with the
 * JSON. A few literal values of control GC.1.1 anchor the test to the data at the BSI commit in
 * {@code test-data/README.md}.
 * </p>
 */
@DisplayName("Grundschutz++ content through the OSCAL API")
@EnabledIf("org.eclipse.fennec.codec.oscal.BsiTestData#present")
class GrundschutzContentTest {

	private static final JsonMapper JSON = RoundTrip.JSON;

	private static MetadataWhiteboard metadataService;

	@BeforeAll
	static void setUp() {
		EPackage.Registry.INSTANCE.put(OSCALPackage.eNS_URI, OSCALPackage.eINSTANCE);
		metadataService = MetadataServiceFactory.create();
		metadataService.registerPackage(OSCALPackage.eINSTANCE);
	}

	private static DocumentRoot load(Path file) throws IOException {
		OscalResourceImpl resource = new OscalResourceImpl(URI.createFileURI(file.toString()), metadataService);
		try (InputStream in = Files.newInputStream(file)) {
			resource.load(in, Map.of());
		}
		return (DocumentRoot) resource.getContents().get(0);
	}

	@Test
	void resolvedCatalogMatchesJsonControlByControl() throws IOException {
		Path file = BsiTestData.file("Grundschutz++-resolved_catalog.json");
		JsonNode json = JSON.readTree(file.toFile()).get("catalog");
		Catalog catalog = load(file).getCatalog();

		assertEquals(json.get("uuid").asString(), catalog.getUuid());
		assertEquals(json.get("metadata").get("title").asString(), catalog.getMetadata().getTitle());
		assertEquals(json.get("metadata").get("oscal-version").asString(), catalog.getMetadata().getOscalVersion());

		List<String> checked = new ArrayList<>();
		compareGroups(json.get("groups"), catalog.getGroup(), checked);
		assertEquals(20, catalog.getGroup().size());
		// every control of the file, nested ones included, went through compareControl
		assertEquals(countControls(json), checked.size());
		assertTrue(checked.size() > 500, "only " + checked.size() + " controls");
	}

	@Test
	void controlGc11() throws IOException {
		Catalog catalog = load(BsiTestData.file("Grundschutz++-resolved_catalog.json")).getCatalog();
		Control gc11 = catalog.getGroup().stream().filter(g -> g.getId().equals("GC")).findFirst().orElseThrow()
				.getGroup().stream().flatMap(g -> g.getControl().stream()).filter(c -> c.getId().equals("GC.1.1"))
				.findFirst().orElseThrow();

		assertEquals("Errichtung und Aufrechterhaltung eines ISMS", gc11.getTitle());
		assertEquals("BSI-Methodik-Grundschutz-plus-plus", gc11.getClass_());

		Property secLevel = gc11.getProp().stream().filter(p -> p.getName().equals("sec_level")).findFirst()
				.orElseThrow();
		assertEquals("normal-SdT", secLevel.getValue());
		assertEquals("https://github.com/BSI-Bund/Stand-der-Technik-Bibliothek/tree/main/documentation/namespaces/security_level.csv",
				secLevel.getNs());

		Parameter param = gc11.getParam().get(0);
		assertEquals("gc.1.1-prm1", param.getId());
		assertEquals("BSI Grundschutz++", param.getLabel());
		assertEquals(List.of("BSI Grundschutz++"), param.getValue());

		Part statement = gc11.getPart().get(0);
		assertEquals("statement", statement.getName());
		assertEquals("GC.1.1_stm", statement.getId());
		assertTrue(statement.getProse().contains("nach {{ insert: param, gc.1.1-prm1 }} verankern"),
				statement.getProse());
		assertEquals("guidance", gc11.getPart().get(1).getName());
	}

	@Test
	void profileImportsAndModifications() throws IOException {
		Path file = BsiTestData.file("Grundschutz++-profile.json");
		JsonNode json = JSON.readTree(file.toFile()).get("profile");
		Profile profile = load(file).getProfile();

		assertEquals(json.get("imports").size(), profile.getImport().size());
		for (int i = 0; i < profile.getImport().size(); i++) {
			JsonNode imp = json.get("imports").get(i);
			assertEquals(imp.get("href").asString(), profile.getImport().get(i).getHref());
			// include-all is an empty object: present in the JSON means set in the model
			assertEquals(imp.has("include-all"), profile.getImport().get(i).getIncludeAll() != null, "import " + i);
			assertEquals(imp.has("include-controls") ? imp.get("include-controls").size() : 0,
					profile.getImport().get(i).getIncludeControls().size(), "import " + i);
		}
		assertEquals(List.of("RISK.1.1", "RISK.1.3", "RISK.1.5", "RISK.1.10"),
				profile.getImport().get(1).getIncludeControls().get(0).getWithId());
		assertEquals("no", profile.getImport().get(1).getIncludeControls().get(0).getWithChildControls());
		assertTrue(profile.getMerge().isSetAsIs());
		assertTrue(profile.getMerge().isAsIs());

		List<ProfileSetParameter> setParameters = profile.getModify().getSetParameter();
		assertEquals(json.get("modify").get("set-parameters").size(), setParameters.size());
		assertEquals("gc.1.1-prm1", setParameters.get(0).getParamId());
		assertEquals(List.of("BSI Grundschutz++"), setParameters.get(0).getValue());
	}

	private static void compareGroups(JsonNode groups, List<CatalogGroup> model, List<String> checked) {
		int size = groups == null ? 0 : groups.size();
		assertEquals(size, model.size());
		for (int i = 0; i < size; i++) {
			JsonNode group = groups.get(i);
			CatalogGroup modelGroup = model.get(i);
			assertEquals(group.get("id").asString(), modelGroup.getId());
			assertEquals(group.get("title").asString(), modelGroup.getTitle());
			compareProps(group.get("props"), modelGroup.getProp(), modelGroup.getId());
			compareGroups(group.get("groups"), modelGroup.getGroup(), checked);
			compareControls(group.get("controls"), modelGroup.getControl(), checked);
		}
	}

	private static void compareControls(JsonNode controls, List<Control> model, List<String> checked) {
		int size = controls == null ? 0 : controls.size();
		assertEquals(size, model.size());
		for (int i = 0; i < size; i++) {
			compareControl(controls.get(i), model.get(i), checked);
		}
	}

	private static void compareControl(JsonNode control, Control model, List<String> checked) {
		String id = control.get("id").asString();
		assertEquals(id, model.getId());
		assertEquals(control.get("title").asString(), model.getTitle(), id);
		assertEquals(text(control, "class"), model.getClass_(), id);
		compareProps(control.get("props"), model.getProp(), id);

		JsonNode params = control.get("params");
		assertEquals(params == null ? 0 : params.size(), model.getParam().size(), id);
		for (int i = 0; i < model.getParam().size(); i++) {
			JsonNode param = params.get(i);
			Parameter modelParam = model.getParam().get(i);
			assertEquals(param.get("id").asString(), modelParam.getId(), id);
			assertEquals(text(param, "label"), modelParam.getLabel(), id);
			assertEquals(strings(param.get("values")), modelParam.getValue(), id);
			compareProps(param.get("props"), modelParam.getProp(), id);
		}

		JsonNode parts = control.get("parts");
		assertEquals(parts == null ? 0 : parts.size(), model.getPart().size(), id);
		for (int i = 0; i < model.getPart().size(); i++) {
			comparePart(parts.get(i), model.getPart().get(i), id);
		}

		JsonNode links = control.get("links");
		assertEquals(links == null ? 0 : links.size(), model.getLink().size(), id);
		for (int i = 0; i < model.getLink().size(); i++) {
			assertEquals(links.get(i).get("href").asString(), model.getLink().get(i).getHref(), id);
			assertEquals(text(links.get(i), "rel"), model.getLink().get(i).getRel(), id);
		}
		checked.add(id);
		compareControls(control.get("controls"), model.getControl(), checked);
	}

	private static void comparePart(JsonNode part, Part model, String control) {
		String where = control + "/" + text(part, "id");
		assertEquals(part.get("name").asString(), model.getName(), where);
		assertEquals(text(part, "id"), model.getId(), where);
		assertEquals(text(part, "title"), model.getTitle(), where);
		assertEquals(text(part, "prose"), model.getProse(), where);
		compareProps(part.get("props"), model.getProp(), where);
		JsonNode parts = part.get("parts");
		assertEquals(parts == null ? 0 : parts.size(), model.getPart().size(), where);
		for (int i = 0; i < model.getPart().size(); i++) {
			comparePart(parts.get(i), model.getPart().get(i), control);
		}
	}

	private static void compareProps(JsonNode props, List<Property> model, String where) {
		assertEquals(props == null ? 0 : props.size(), model.size(), where);
		for (int i = 0; i < model.size(); i++) {
			JsonNode prop = props.get(i);
			Property modelProp = model.get(i);
			assertEquals(prop.get("name").asString(), modelProp.getName(), where);
			assertEquals(prop.get("value").asString(), modelProp.getValue(), where);
			assertEquals(text(prop, "ns"), modelProp.getNs(), where);
			assertEquals(text(prop, "class"), modelProp.getClass_(), where);
			assertEquals(text(prop, "remarks"), modelProp.getRemarks(), where);
		}
	}

	private static int countControls(JsonNode node) {
		int count = 0;
		if (node.isObject()) {
			for (String name : node.propertyNames()) {
				JsonNode value = node.get(name);
				if (name.equals("controls")) {
					count += value.size();
				}
				count += countControls(value);
			}
		} else if (node.isArray()) {
			for (JsonNode item : node) {
				count += countControls(item);
			}
		}
		return count;
	}

	private static String text(JsonNode node, String name) {
		return node.has(name) ? node.get(name).asString() : null;
	}

	private static List<String> strings(JsonNode array) {
		return array == null ? List.of() : array.valueStream().map(JsonNode::asString).toList();
	}
}
