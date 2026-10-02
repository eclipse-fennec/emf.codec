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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gov.nist.csrc.ns.oscal.AuthorizationBoundary;
import gov.nist.csrc.ns.oscal.ByComponent;
import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.Impact;
import gov.nist.csrc.ns.oscal.ImportProfile;
import gov.nist.csrc.ns.oscal.InformationType;
import gov.nist.csrc.ns.oscal.Metadata;
import gov.nist.csrc.ns.oscal.OSCALFactory;
import gov.nist.csrc.ns.oscal.SspControlImplementation;
import gov.nist.csrc.ns.oscal.SspImplementedRequirement;
import gov.nist.csrc.ns.oscal.SystemCharacteristics;
import gov.nist.csrc.ns.oscal.SystemComponent;
import gov.nist.csrc.ns.oscal.SystemComponentStatus;
import gov.nist.csrc.ns.oscal.SystemId;
import gov.nist.csrc.ns.oscal.SystemImplementation;
import gov.nist.csrc.ns.oscal.SystemInformation;
import gov.nist.csrc.ns.oscal.SystemSecurityPlan;
import gov.nist.csrc.ns.oscal.SystemStatus;
import gov.nist.csrc.ns.oscal.SystemUser;
import tools.jackson.databind.JsonNode;

/**
 * System security plans (issue #255). The SSP is the export target of the compliance inventory,
 * so writing matters most: a plan built through the API is written and compared with the OSCAL JSON
 * it has to give. Reading is checked on the NIST example.
 */
@DisplayName("OSCAL system-security-plan")
class SystemSecurityPlanTest {

	private static final OSCALFactory F = OSCALFactory.eINSTANCE;

	/** The smallest SSP OSCAL 1.2.3 accepts, plus one implemented requirement by a component. */
	private static final String EXPECTED = """
			{"system-security-plan":{
			  "uuid":"a1b2c3d4-0000-4000-8000-000000000001",
			  "metadata":{"title":"GA-Lotse SSP","last-modified":"2026-10-02T12:00:00.123456789+02:00",
			    "version":"1.0","oscal-version":"1.2.3"},
			  "import-profile":{"href":"https://example.org/Grundschutz++-profile.json"},
			  "system-characteristics":{
			    "system-ids":[{"identifier-type":"https://ietf.org/rfc/rfc4122","id":"ga-lotse"}],
			    "system-name":"GA-Lotse","description":"Fachanwendung für **Gesundheitsämter**.",
			    "system-information":{"information-types":[{"title":"Gesundheitsdaten","description":"Art. 9 DSGVO.",
			      "confidentiality-impact":{"base":"fips-199-high"},"integrity-impact":{"base":"fips-199-moderate"},
			      "availability-impact":{"base":"fips-199-moderate"}}]},
			    "status":{"state":"operational"},
			    "authorization-boundary":{"description":"Rechenzentrum und Clients."}},
			  "system-implementation":{
			    "users":[{"uuid":"a1b2c3d4-0000-4000-8000-000000000002","title":"Sachbearbeitung","role-ids":["user"]}],
			    "components":[{"uuid":"a1b2c3d4-0000-4000-8000-000000000003","type":"this-system","title":"GA-Lotse",
			      "description":"Die Anwendung.","status":{"state":"operational"}}]},
			  "control-implementation":{"description":"Umsetzung nach Grundschutz++.",
			    "implemented-requirements":[{"uuid":"a1b2c3d4-0000-4000-8000-000000000004","control-id":"GC.1.1",
			      "by-components":[{"component-uuid":"a1b2c3d4-0000-4000-8000-000000000003",
			        "uuid":"a1b2c3d4-0000-4000-8000-000000000005","description":"Das ISMS nach {{ insert: param, gc.1.1-prm1 }}."}]}]}}}""";

	private static SystemSecurityPlan buildPlan() {
		SystemSecurityPlan ssp = F.createSystemSecurityPlan();
		ssp.setUuid("a1b2c3d4-0000-4000-8000-000000000001");
		Metadata metadata = F.createMetadata();
		metadata.setTitle("GA-Lotse SSP");
		metadata.setLastModified(DatatypeFactory.newDefaultInstance()
				.newXMLGregorianCalendar("2026-10-02T12:00:00.123456789+02:00"));
		metadata.setVersion("1.0");
		metadata.setOscalVersion("1.2.3");
		ssp.setMetadata(metadata);
		ImportProfile profile = F.createImportProfile();
		profile.setHref("https://example.org/Grundschutz++-profile.json");
		ssp.setImportProfile(profile);

		SystemCharacteristics characteristics = F.createSystemCharacteristics();
		SystemId systemId = F.createSystemId();
		systemId.setIdentifierType("https://ietf.org/rfc/rfc4122");
		systemId.setValue("ga-lotse");
		characteristics.getSystemId().add(systemId);
		characteristics.setSystemName("GA-Lotse");
		characteristics.setDescription("Fachanwendung für **Gesundheitsämter**.");
		SystemInformation information = F.createSystemInformation();
		InformationType type = F.createInformationType();
		type.setTitle("Gesundheitsdaten");
		type.setDescription("Art. 9 DSGVO.");
		type.setConfidentialityImpact(impact("fips-199-high"));
		type.setIntegrityImpact(impact("fips-199-moderate"));
		type.setAvailabilityImpact(impact("fips-199-moderate"));
		information.getInformationType().add(type);
		characteristics.setSystemInformation(information);
		SystemStatus status = F.createSystemStatus();
		status.setState("operational");
		characteristics.setStatus(status);
		AuthorizationBoundary boundary = F.createAuthorizationBoundary();
		boundary.setDescription("Rechenzentrum und Clients.");
		characteristics.setAuthorizationBoundary(boundary);
		ssp.setSystemCharacteristics(characteristics);

		SystemImplementation implementation = F.createSystemImplementation();
		SystemUser user = F.createSystemUser();
		user.setUuid("a1b2c3d4-0000-4000-8000-000000000002");
		user.setTitle("Sachbearbeitung");
		user.getRoleId().add("user");
		implementation.getUser().add(user);
		SystemComponent component = F.createSystemComponent();
		component.setUuid("a1b2c3d4-0000-4000-8000-000000000003");
		component.setType("this-system");
		component.setTitle("GA-Lotse");
		component.setDescription("Die Anwendung.");
		SystemComponentStatus componentStatus = F.createSystemComponentStatus();
		componentStatus.setState("operational");
		component.setStatus(componentStatus);
		implementation.getComponent().add(component);
		ssp.setSystemImplementation(implementation);

		SspControlImplementation controls = F.createSspControlImplementation();
		controls.setDescription("Umsetzung nach Grundschutz++.");
		SspImplementedRequirement requirement = F.createSspImplementedRequirement();
		requirement.setUuid("a1b2c3d4-0000-4000-8000-000000000004");
		requirement.setControlId("GC.1.1");
		ByComponent byComponent = F.createByComponent();
		byComponent.setComponentUuid("a1b2c3d4-0000-4000-8000-000000000003");
		byComponent.setUuid("a1b2c3d4-0000-4000-8000-000000000005");
		byComponent.setDescription("Das ISMS nach {{ insert: param, gc.1.1-prm1 }}.");
		requirement.getByComponent().add(byComponent);
		controls.getImplementedRequirement().add(requirement);
		ssp.setControlImplementation(controls);
		return ssp;
	}

	private static Impact impact(String base) {
		Impact impact = F.createImpact();
		impact.setBase(base);
		return impact;
	}

	@Test
	void planBuiltThroughTheApiIsWrittenAsOscal() throws IOException {
		Resource resource = new OscalResourceImpl(URI.createURI("ssp.json"), RoundTrip.metadataService());
		resource.getContents().add(buildPlan());
		byte[] written = RoundTrip.save(resource);

		assertEquals(List.of(), JsonDiff.diff(RoundTrip.JSON.readTree(EXPECTED), RoundTrip.JSON.readTree(written)));
		// the written plan reads back with every value in its feature
		Resource reloaded = RoundTrip.load("ssp.json", written);
		assertEquals(List.of(), RoundTrip.messages(reloaded.getWarnings()));
		ModelJsonOracle.Result oracle = ModelJsonOracle.check(RoundTrip.JSON.readTree(EXPECTED),
				(DocumentRoot) reloaded.getContents().get(0));
		assertEquals(List.of(), oracle.findings());
		assertEquals(RoundTrip.scalarCount(RoundTrip.JSON.readTree(EXPECTED)), oracle.checkedValues());
	}

	@Test
	void nistExampleThroughTheApi() throws IOException {
		Path file = Path.of("test-data/nist/ssp-example.json");
		JsonNode json = RoundTrip.JSON.readTree(file.toFile()).get("system-security-plan");
		SystemSecurityPlan ssp = ((DocumentRoot) RoundTrip.load(file.toString(), Files.readAllBytes(file))
				.getContents().get(0)).getSystemSecurityPlan();

		JsonNode characteristics = json.get("system-characteristics");
		assertEquals(characteristics.get("system-name").asString(), ssp.getSystemCharacteristics().getSystemName());
		assertEquals(characteristics.get("status").get("state").asString(),
				ssp.getSystemCharacteristics().getStatus().getState());
		JsonNode systemIds = characteristics.get("system-ids");
		assertEquals(systemIds.size(), ssp.getSystemCharacteristics().getSystemId().size());
		assertEquals(systemIds.get(0).get("id").asString(), ssp.getSystemCharacteristics().getSystemId().get(0).getValue());
		assertEquals(json.get("system-implementation").get("components").size(),
				ssp.getSystemImplementation().getComponent().size());
		assertEquals(json.get("system-implementation").get("users").size(), ssp.getSystemImplementation().getUser().size());

		JsonNode requirements = json.get("control-implementation").get("implemented-requirements");
		assertEquals(requirements.size(), ssp.getControlImplementation().getImplementedRequirement().size());
		for (int i = 0; i < requirements.size(); i++) {
			SspImplementedRequirement requirement = ssp.getControlImplementation().getImplementedRequirement().get(i);
			assertEquals(requirements.get(i).get("control-id").asString(), requirement.getControlId());
			assertEquals(ComponentDefinitionTest.list(requirements.get(i), "by-components").size(),
					requirement.getByComponent().size(), requirement.getControlId());
			assertEquals(ComponentDefinitionTest.list(requirements.get(i), "statements").size(),
					requirement.getStatement().size(), requirement.getControlId());
		}
	}
}
