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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import gov.nist.csrc.ns.oscal.AssessmentPlan;
import gov.nist.csrc.ns.oscal.AssessmentResults;
import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.Finding;
import gov.nist.csrc.ns.oscal.Observation;
import gov.nist.csrc.ns.oscal.PlanOfActionAndMilestones;
import gov.nist.csrc.ns.oscal.PoamItem;
import gov.nist.csrc.ns.oscal.Result;
import gov.nist.csrc.ns.oscal.Risk;
import tools.jackson.databind.JsonNode;

/**
 * Assessment plan, assessment results and POA&M through the generated API (issue #256), on the
 * NIST examples: the parts a compliance report maps to - tasks, results with findings,
 * observations and risks, POA&M items.
 */
@DisplayName("OSCAL assessment-plan, assessment-results, POA&M through the API")
class AssessmentTest {

	private static DocumentRoot load(Path file) throws IOException {
		return (DocumentRoot) RoundTrip.load(file.toString(), Files.readAllBytes(file)).getContents().get(0);
	}

	private static List<String> titles(JsonNode node, String name) {
		return ComponentDefinitionTest.list(node, name).stream().map(n -> ComponentDefinitionTest.text(n, "title"))
				.toList();
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = { "ifa_assessment-plan.json", "ifa_assessment-plan-example.json" })
	void assessmentPlan(String name) throws IOException {
		Path file = Path.of("test-data/nist", name);
		JsonNode json = RoundTrip.JSON.readTree(file.toFile()).get("assessment-plan");
		AssessmentPlan plan = load(file).getAssessmentPlan();

		assertEquals(json.get("import-ssp").get("href").asString(), plan.getImportSsp().getHref());
		assertEquals(titles(json, "tasks"), plan.getTask().stream().map(t -> t.getTitle()).toList());
		assertEquals(ComponentDefinitionTest.list(json, "assessment-subjects").size(), plan.getAssessmentSubject().size());
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = { "ifa_assessment-results.json", "ifa_assessment-results-example.json" })
	void assessmentResults(String name) throws IOException {
		Path file = Path.of("test-data/nist", name);
		JsonNode json = RoundTrip.JSON.readTree(file.toFile()).get("assessment-results");
		AssessmentResults results = load(file).getAssessmentResults();

		List<JsonNode> jsonResults = ComponentDefinitionTest.list(json, "results");
		assertEquals(jsonResults.size(), results.getResult().size());
		for (int i = 0; i < jsonResults.size(); i++) {
			JsonNode result = jsonResults.get(i);
			Result model = results.getResult().get(i);
			assertEquals(result.get("title").asString(), model.getTitle());
			assertEquals(result.get("start").asString(), model.getStart().toXMLFormat());
			assertEquals(titles(result, "findings"), model.getFinding().stream().map(Finding::getTitle).toList());
			assertEquals(titles(result, "observations"),
					model.getObservation().stream().map(Observation::getTitle).toList());
			assertEquals(titles(result, "risks"), model.getRisk().stream().map(Risk::getTitle).toList());
			for (int f = 0; f < model.getFinding().size(); f++) {
				JsonNode target = ComponentDefinitionTest.list(result, "findings").get(f).get("target");
				assertEquals(target.get("target-id").asString(), model.getFinding().get(f).getTarget().getTargetId());
				assertEquals(target.get("status").get("state").asString(),
						model.getFinding().get(f).getTarget().getStatus().getState());
			}
		}
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = { "ifa_plan-of-action-and-milestones.json" })
	void poam(String name) throws IOException {
		Path file = Path.of("test-data/nist", name);
		JsonNode json = RoundTrip.JSON.readTree(file.toFile()).get("plan-of-action-and-milestones");
		PlanOfActionAndMilestones poam = load(file).getPlanOfActionAndMilestones();

		assertEquals(titles(json, "poam-items"), poam.getPoamItem().stream().map(PoamItem::getTitle).toList());
		assertEquals(titles(json, "risks"), poam.getRisk().stream().map(Risk::getTitle).toList());
		List<JsonNode> risks = ComponentDefinitionTest.list(json, "risks");
		for (int r = 0; r < risks.size(); r++) {
			assertEquals(risks.get(r).get("status").asString(), poam.getRisk().get(r).getStatus());
			assertEquals(ComponentDefinitionTest.list(risks.get(r), "remediations").size(),
					poam.getRisk().get(r).getResponse().size());
		}
	}
}
