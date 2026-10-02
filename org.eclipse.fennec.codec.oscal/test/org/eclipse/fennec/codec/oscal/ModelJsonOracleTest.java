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

import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gov.nist.csrc.ns.oscal.Catalog;
import gov.nist.csrc.ns.oscal.Control;
import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.Metadata;
import gov.nist.csrc.ns.oscal.OSCALFactory;
import gov.nist.csrc.ns.oscal.Part;
import gov.nist.csrc.ns.oscal.Property;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * The oracle of the round-trip tests has to see a wrong model, or a passing round trip proves
 * nothing. This test hands it models that differ from their JSON in one place each.
 */
@DisplayName("Model/JSON oracle")
class ModelJsonOracleTest {

	private static final JsonMapper JSON = RoundTrip.JSON;

	private static final String DOCUMENT = """
			{"catalog":{"uuid":"c1","metadata":{"title":"T","last-modified":"2026-09-24T07:12:47.278531946Z",\
			"version":"1","oscal-version":"1.1.3"},"controls":[{"id":"a.1","title":"A","props":[\
			{"name":"sec_level","value":"normal","ns":"https://example.org/ns"}],\
			"parts":[{"name":"statement","prose":"Text {{ insert: param, a.1-prm1 }}"}]}]}}""";

	private static DocumentRoot model() {
		OSCALFactory f = OSCALFactory.eINSTANCE;
		DocumentRoot root = f.createDocumentRoot();
		Catalog catalog = f.createCatalog();
		catalog.setUuid("c1");
		Metadata metadata = f.createMetadata();
		metadata.setTitle("T");
		metadata.setLastModified(DatatypeFactory.newDefaultInstance()
				.newXMLGregorianCalendar("2026-09-24T07:12:47.278531946Z"));
		metadata.setVersion("1");
		metadata.setOscalVersion("1.1.3");
		catalog.setMetadata(metadata);
		Control control = f.createControl();
		control.setId("a.1");
		control.setTitle("A");
		Property prop = f.createProperty();
		prop.setName("sec_level");
		prop.setValue("normal");
		prop.setNs("https://example.org/ns");
		control.getProp().add(prop);
		Part part = f.createPart();
		part.setName("statement");
		part.setProse("Text {{ insert: param, a.1-prm1 }}");
		control.getPart().add(part);
		catalog.getControl().add(control);
		root.setCatalog(catalog);
		return root;
	}

	private static List<String> findings(DocumentRoot root) {
		JsonNode json = JSON.readTree(DOCUMENT);
		return ModelJsonOracle.check(json, root).findings();
	}

	@Test
	void matchingModelHasNoFindings() {
		assertEquals(List.of(), findings(model()));
		assertEquals(RoundTrip.scalarCount(JSON.readTree(DOCUMENT)),
				ModelJsonOracle.check(JSON.readTree(DOCUMENT), model()).checkedValues());
	}

	@Test
	void changedProseIsFound() {
		DocumentRoot root = model();
		root.getCatalog().getControl().get(0).getPart().get(0).setProse("Text {{ insert: param, a.1-prm2 }}");
		assertEquals(1, findings(root).size(), findings(root).toString());
	}

	@Test
	void lostFractionDigitIsFound() {
		DocumentRoot root = model();
		root.getCatalog().getMetadata().setLastModified(DatatypeFactory.newDefaultInstance()
				.newXMLGregorianCalendar("2026-09-24T07:12:47.278531Z"));
		assertEquals(1, findings(root).size(), findings(root).toString());
	}

	@Test
	void missingPropertyIsFound() {
		DocumentRoot root = model();
		root.getCatalog().getControl().get(0).getProp().clear();
		assertEquals(List.of("SIZE /catalog/controls[0]/props json 1 model 0"), findings(root));
	}

	@Test
	void valueInAnotherFeatureIsFound() {
		DocumentRoot root = model();
		Property prop = root.getCatalog().getControl().get(0).getProp().get(0);
		prop.setClass("https://example.org/ns");
		prop.setNs(null);
		List<String> findings = findings(root);
		assertEquals(2, findings.size(), findings.toString());
	}
}
