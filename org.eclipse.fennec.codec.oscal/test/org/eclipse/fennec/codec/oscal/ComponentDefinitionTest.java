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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import gov.nist.csrc.ns.oscal.Capability;
import gov.nist.csrc.ns.oscal.ComponentControlImplementation;
import gov.nist.csrc.ns.oscal.ComponentDefinition;
import gov.nist.csrc.ns.oscal.ComponentImplementedRequirement;
import gov.nist.csrc.ns.oscal.ComponentStatement;
import gov.nist.csrc.ns.oscal.DefinedComponent;
import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.ResponsibleRole;
import gov.nist.csrc.ns.oscal.SetParameter;
import tools.jackson.databind.JsonNode;

/**
 * Component definitions through the generated API (issue #253): every component, control
 * implementation, implemented requirement and statement compared with the JSON, the way a consumer
 * reads the TOMs of a component. Control implementations hang off components or, as in the
 * BSI GA-Lotse file, off capabilities. Runs on the NIST examples and, when present, on every BSI file
 * under {@code implementation_layer/}.
 */
@DisplayName("OSCAL component-definition through the API")
class ComponentDefinitionTest {

	static Stream<Path> files() {
		List<Path> files = new ArrayList<>(List.of(Path.of("test-data/nist/example-component-definition.json"),
				Path.of("test-data/nist/example-component.json")));
		BsiTestData.files().stream().filter(p -> p.toString().contains("implementation_layer")).forEach(files::add);
		return files.stream();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("files")
	void componentsMatchJson(Path file) throws IOException {
		byte[] bytes = Files.readAllBytes(file);
		JsonNode json = RoundTrip.JSON.readTree(bytes).get("component-definition");
		ComponentDefinition definition = ((DocumentRoot) RoundTrip.load(file.toString(), bytes).getContents().get(0))
				.getComponentDefinition();

		assertEquals(json.get("uuid").asString(), definition.getUuid());
		List<JsonNode> components = list(json, "components");
		assertEquals(components.size(), definition.getComponent().size());
		for (int i = 0; i < components.size(); i++) {
			compareComponent(components.get(i), definition.getComponent().get(i));
		}

		List<JsonNode> capabilities = list(json, "capabilities");
		assertEquals(capabilities.size(), definition.getCapability().size());
		for (int i = 0; i < capabilities.size(); i++) {
			JsonNode capability = capabilities.get(i);
			Capability model = definition.getCapability().get(i);
			assertEquals(capability.get("uuid").asString(), model.getUuid());
			assertEquals(capability.get("name").asString(), model.getName(), model.getUuid());
			assertEquals(capability.get("description").asString(), model.getDescription(), model.getUuid());
			List<JsonNode> incorporates = list(capability, "incorporates-components");
			assertEquals(incorporates.size(), model.getIncorporatesComponent().size(), model.getUuid());
			for (int c = 0; c < incorporates.size(); c++) {
				assertEquals(incorporates.get(c).get("component-uuid").asString(),
						model.getIncorporatesComponent().get(c).getComponentUuid(), model.getUuid());
			}
			compareImplementations(list(capability, "control-implementations"),
					model.getControlImplementation(), model.getUuid());
		}
		// GA-Lotse has components and a capability but no requirement yet
		assertTrue(components.size() + capabilities.size() > 0, "empty component definition " + file);
	}

	private static int compareComponent(JsonNode json, DefinedComponent component) {
		String where = component.getUuid();
		assertEquals(json.get("uuid").asString(), component.getUuid());
		assertEquals(json.get("type").asString(), component.getType(), where);
		assertEquals(json.get("title").asString(), component.getTitle(), where);
		assertEquals(json.get("description").asString(), component.getDescription(), where);
		assertEquals(text(json, "purpose"), component.getPurpose(), where);
		assertEquals(list(json, "props").size(), component.getProp().size(), where);
		compareRoles(list(json, "responsible-roles"), component.getResponsibleRole(), where);

		return compareImplementations(list(json, "control-implementations"), component.getControlImplementation(), where);
	}

	private static int compareImplementations(List<JsonNode> implementations,
			List<ComponentControlImplementation> models, String where) {
		assertEquals(implementations.size(), models.size(), where);
		int requirements = 0;
		for (int i = 0; i < implementations.size(); i++) {
			JsonNode implementation = implementations.get(i);
			ComponentControlImplementation model = models.get(i);
			assertEquals(implementation.get("uuid").asString(), model.getUuid());
			assertEquals(implementation.get("source").asString(), model.getSource(), model.getUuid());
			assertEquals(implementation.get("description").asString(), model.getDescription(), model.getUuid());
			compareSetParameters(list(implementation, "set-parameters"), model.getSetParameter(), model.getUuid());

			List<JsonNode> implemented = list(implementation, "implemented-requirements");
			assertEquals(implemented.size(), model.getImplementedRequirement().size(), model.getUuid());
			for (int r = 0; r < implemented.size(); r++) {
				compareRequirement(implemented.get(r), model.getImplementedRequirement().get(r));
				requirements++;
			}
		}
		return requirements;
	}

	private static void compareRequirement(JsonNode json, ComponentImplementedRequirement requirement) {
		String where = requirement.getControlId() + " " + requirement.getUuid();
		assertEquals(json.get("uuid").asString(), requirement.getUuid());
		assertEquals(json.get("control-id").asString(), requirement.getControlId(), where);
		assertEquals(json.get("description").asString(), requirement.getDescription(), where);
		assertEquals(text(json, "remarks"), requirement.getRemarks(), where);
		assertEquals(list(json, "props").size(), requirement.getProp().size(), where);
		assertEquals(list(json, "links").size(), requirement.getLink().size(), where);
		compareSetParameters(list(json, "set-parameters"), requirement.getSetParameter(), where);
		compareRoles(list(json, "responsible-roles"), requirement.getResponsibleRole(), where);

		List<JsonNode> statements = list(json, "statements");
		assertEquals(statements.size(), requirement.getStatement().size(), where);
		for (int s = 0; s < statements.size(); s++) {
			JsonNode statement = statements.get(s);
			ComponentStatement model = requirement.getStatement().get(s);
			assertEquals(statement.get("statement-id").asString(), model.getStatementId(), where);
			assertEquals(statement.get("uuid").asString(), model.getUuid(), where);
			assertEquals(statement.get("description").asString(), model.getDescription(), where);
			compareRoles(list(statement, "responsible-roles"), model.getResponsibleRole(), where);
		}
	}

	private static void compareRoles(List<JsonNode> json, List<ResponsibleRole> roles, String where) {
		assertEquals(json.size(), roles.size(), where);
		for (int i = 0; i < json.size(); i++) {
			assertEquals(json.get(i).get("role-id").asString(), roles.get(i).getRoleId(), where);
			assertEquals(strings(json.get(i), "party-uuids"), roles.get(i).getPartyUuid(), where);
		}
	}

	private static void compareSetParameters(List<JsonNode> json, List<SetParameter> parameters, String where) {
		assertEquals(json.size(), parameters.size(), where);
		for (int i = 0; i < json.size(); i++) {
			assertEquals(json.get(i).get("param-id").asString(), parameters.get(i).getParamId(), where);
			assertEquals(strings(json.get(i), "values"), parameters.get(i).getValue(), where);
		}
	}

	static List<JsonNode> list(JsonNode node, String name) {
		JsonNode value = node.get(name);
		if (value == null) {
			return List.of();
		}
		// a single object where OSCAL wants an array is read as a list of one
		return value.isArray() ? value.valueStream().toList() : List.of(value);
	}

	static List<String> strings(JsonNode node, String name) {
		return list(node, name).stream().map(JsonNode::asString).toList();
	}

	static String text(JsonNode node, String name) {
		return node.has(name) ? node.get(name).asString() : null;
	}
}
