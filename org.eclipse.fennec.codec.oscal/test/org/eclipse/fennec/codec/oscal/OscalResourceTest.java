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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gov.nist.csrc.ns.oscal.Catalog;
import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.Metadata;
import gov.nist.csrc.ns.oscal.OSCALFactory;
import gov.nist.csrc.ns.oscal.Part;
import tools.jackson.databind.JsonNode;

/**
 * The OSCAL resource on fixtures written for it: OSCAL members the BSI and NIST files do not use,
 * markup edge cases, the document wrapper and how content that is no OSCAL document is handled.
 */
@DisplayName("OSCAL resource")
class OscalResourceTest {

	private static final Path FIXTURES = Path.of("test-data/fixtures");

	private static void assertExactRoundTrip(RoundTrip roundTrip) {
		assertEquals(List.of(), roundTrip.errors(), "load errors");
		assertEquals(List.of(), roundTrip.warnings(), "load warnings");
		assertEquals(List.of(), roundTrip.oracle().findings(), "model vs. JSON");
		assertEquals(RoundTrip.scalarCount(roundTrip.original()), roundTrip.oracle().checkedValues(),
				"not every JSON value was compared with the model");
		assertEquals(List.of(), roundTrip.diffs(), "round trip");
		assertEquals(List.of(), roundTrip.reloadDiagnostics(), "reload diagnostics");
		assertTrue(roundTrip.stable(), "second round trip is not stable");
	}

	@Test
	void catalogMembers() throws IOException {
		RoundTrip roundTrip = RoundTrip.of(FIXTURES.resolve("catalog-members.json"));
		assertExactRoundTrip(roundTrip);

		DocumentRoot root = roundTrip.root();
		assertEquals("https://raw.githubusercontent.com/usnistgov/OSCAL/v1.2.3/json/schema/oscal_catalog_schema.json",
				root.getSchema());
		Metadata metadata = root.getCatalog().getMetadata();
		assertEquals("Fixture catalog with **markup** and `code`", metadata.getTitle());
		assertEquals("Line one.\n\nLine two with trailing spaces   \n- item ä\n- item ✓\n\n> quoted \"text\" and a back\\slash",
				metadata.getRemarks());
		// nanoseconds and the zone offset survive in the XMLGregorianCalendar
		assertEquals("2026-09-24T07:12:47.278531946Z", metadata.getLastModified().toXMLFormat());
		assertEquals("2026-03-01T08:00:00+02:00", metadata.getPublished().toXMLFormat());
		assertEquals("10.1000/xyz123", metadata.getDocumentId().get(0).getValue());
		assertEquals("+49 3641 0000", metadata.getLocation().get(0).getTelephoneNumber().get(0).getValue());
		assertEquals("0000-0002-1825-0097", metadata.getParty().get(0).getExternalId().get(0).getValue());
		assertEquals("Draft", metadata.getRevision().get(0).getTitle());

		Part statement = root.getCatalog().getControl().get(0).getPart().get(0);
		assertEquals("The organization reviews the policy {{ insert: param, ctl-1_prm_1 }}.", statement.getProse());
		assertEquals("Second item\nwith two lines.", statement.getPart().get(1).getProse());
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
				root.getCatalog().getBackMatter().getResource().get(0).getRlink().get(0).getHash().get(0).getValue());
	}

	@Test
	void profileMembers() throws IOException {
		RoundTrip roundTrip = RoundTrip.of(FIXTURES.resolve("profile-members.json"));
		assertExactRoundTrip(roundTrip);
		assertTrue(roundTrip.root().getProfile().getImport().get(0).getIncludeAll() != null, "include-all");
		assertTrue(roundTrip.root().getProfile().getMerge().getFlat() != null, "flat");
	}

	/** OSCAL allows a single object for {@code mappings}; it is read as one mapping and written as an array. */
	@Test
	void singleMappingIsWrittenAsArray() throws IOException {
		RoundTrip roundTrip = RoundTrip.of(FIXTURES.resolve("mapping-single-mapping.json"));
		assertEquals(List.of(), roundTrip.warnings());
		assertEquals(List.of(), roundTrip.oracle().findings());
		assertEquals(1, roundTrip.root().getMappingCollection().getMapping().size());
		assertEquals(1, roundTrip.diffs().size(), roundTrip.diffs().toString());
		JsonNode mapping = roundTrip.original().get("mapping-collection").get("mappings");
		JsonNode written = roundTrip.written().get("mapping-collection").get("mappings");
		assertTrue(written.isArray() && written.size() == 1, written.toString());
		assertEquals(List.of(), JsonDiff.diff(mapping, written.get(0)));
		assertTrue(roundTrip.stable());
	}

	@Test
	void bareModelObjectIsWrittenInsideItsDocumentMember() throws IOException {
		Catalog catalog = OSCALFactory.eINSTANCE.createCatalog();
		catalog.setUuid("6f1d2a4e-8c3b-4e5f-9a7b-1c2d3e4f5a6b");
		Metadata metadata = OSCALFactory.eINSTANCE.createMetadata();
		metadata.setTitle("Bare");
		metadata.setVersion("1");
		metadata.setOscalVersion("1.2.3");
		catalog.setMetadata(metadata);
		Resource resource = new OscalResourceImpl(URI.createURI("bare.json"), RoundTrip.metadataService());
		resource.getContents().add(catalog);

		byte[] written = RoundTrip.save(resource);
		assertEquals(RoundTrip.JSON.readTree("""
				{"catalog":{"uuid":"6f1d2a4e-8c3b-4e5f-9a7b-1c2d3e4f5a6b",\
				"metadata":{"title":"Bare","version":"1","oscal-version":"1.2.3"}}}"""),
				RoundTrip.JSON.readTree(written));

		Resource reloaded = RoundTrip.load("bare.json", written);
		DocumentRoot root = assertInstanceOf(DocumentRoot.class, reloaded.getContents().get(0));
		assertEquals("Bare", root.getCatalog().getMetadata().getTitle());
	}

	@Test
	void contentThatIsNoOscalModelIsRejected() {
		Resource resource = new OscalResourceImpl(URI.createURI("other.json"), RoundTrip.metadataService());
		resource.getContents().add(EcoreFactory.eINSTANCE.createEClass());
		IOException e = assertThrows(IOException.class, () -> RoundTrip.save(resource));
		assertTrue(e.getMessage().contains("must be a DocumentRoot or one of the OSCAL models"), e.getMessage());
	}

	@Test
	void unknownDocumentMemberIsReported() {
		Resource resource = RoundTrip.load("unknown.json", """
				{"catalogue":{"uuid":"6f1d2a4e-8c3b-4e5f-9a7b-1c2d3e4f5a6b"}}""".getBytes(StandardCharsets.UTF_8));
		assertEquals(List.of("Unknown feature 'catalogue' for EClass DocumentRoot"),
				RoundTrip.messages(resource.getWarnings()));
		DocumentRoot root = assertInstanceOf(DocumentRoot.class, resource.getContents().get(0));
		assertNull(root.getCatalog());
	}

	@Test
	void standaloneFactory() throws IOException {
		OscalResourceFactoryImpl factory = new OscalResourceFactoryImpl();
		Resource resource = factory.createResource(URI.createURI("catalog.json"));
		assertInstanceOf(OscalResourceImpl.class, resource);
		resource.load(new ByteArrayInputStream(Files.readAllBytes(FIXTURES.resolve("catalog-members.json"))), Map.of());
		assertEquals(List.of(), resource.getWarnings());
		assertEquals("ctl-1", ((DocumentRoot) resource.getContents().get(0)).getCatalog().getControl().get(0).getId());

		Map<String, Object> properties = factory.getServiceProperties();
		assertEquals("FennecCodecOscal", properties.get(EMFNamespaces.EMF_CONFIGURATOR_NAME));
		assertEquals(OscalResourceFactoryImpl.CONTENT_TYPE_OSCAL_JSON, properties.get(EMFNamespaces.EMF_MODEL_CONTENT_TYPE));
	}
}
