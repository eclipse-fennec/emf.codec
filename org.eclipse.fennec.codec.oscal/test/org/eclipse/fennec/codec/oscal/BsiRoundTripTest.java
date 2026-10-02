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
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import tools.jackson.databind.JsonNode;

/**
 * Round trip over every OSCAL file of the BSI Stand-der-Technik-Bibliothek.
 * <p>
 * The files are not part of this repository (CC BY-SA 4.0); the test runs only when they are
 * present, see {@link BsiTestData}. For every file it checks
 * <ol>
 * <li>the load reports no error and no warning,</li>
 * <li>every JSON value is in the right feature of the model ({@link ModelJsonOracle}), and the
 * oracle compared as many values as the file has,</li>
 * <li>the written document equals the original ({@link JsonDiff}),</li>
 * <li>the written document loads without diagnostics and writing it again gives the same bytes.</li>
 * </ol>
 * Two files are not valid OSCAL. Their deviations are listed here one by one; anything beyond them
 * fails.
 * </p>
 */
@DisplayName("BSI OSCAL round trip")
@EnabledIf("org.eclipse.fennec.codec.oscal.BsiTestData#present")
class BsiRoundTripTest {

	private static final String ISO_MAPPING = "ISO27001-AnnexA-to-GS++-mapping_collection.json";
	private static final String SUPPLY_CHAIN_COMPONENT = "Lieferkettensicherheit-component_definition.json";

	static Stream<Path> files() {
		return BsiTestData.files().stream();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("files")
	void roundTrip(Path file) throws IOException {
		String name = file.getFileName().toString();
		RoundTrip roundTrip = RoundTrip.of(file);

		assertEquals(List.of(), roundTrip.errors(), "load errors");
		assertEquals(expectedWarnings(name), roundTrip.warnings(), "load warnings");
		assertEquals(expectedOracleFindings(name), roundTrip.oracle().findings(), "model vs. JSON");
		assertEquals(RoundTrip.scalarCount(roundTrip.original()) - expectedUnmappedScalars(name),
				roundTrip.oracle().checkedValues(), "not every JSON value was compared with the model");
		assertEquals(expectedDiffs(name, roundTrip.original()), roundTrip.diffs(), "round trip");
		assertEquals(List.of(), roundTrip.reloadDiagnostics(), "reload diagnostics");
		assertTrue(roundTrip.stable(), "second round trip is not stable");
	}

	/** {@code qa-note} and {@code qa-reviewed} are no members of OSCAL mapping provenance. */
	private static List<String> expectedWarnings(String file) {
		if (ISO_MAPPING.equals(file)) {
			return List.of("Unknown feature 'qa-reviewed' for EClass MappingProvenance",
					"Unknown feature 'qa-note' for EClass MappingProvenance");
		}
		return List.of();
	}

	private static List<String> expectedOracleFindings(String file) {
		if (ISO_MAPPING.equals(file)) {
			return List.of("NO FEATURE /mapping-collection/provenance/qa-reviewed in MappingProvenance",
					"NO FEATURE /mapping-collection/provenance/qa-note in MappingProvenance");
		}
		return List.of();
	}

	private static int expectedUnmappedScalars(String file) {
		return ISO_MAPPING.equals(file) ? 2 : 0;
	}

	private static List<String> expectedDiffs(String file, JsonNode original) {
		if (ISO_MAPPING.equals(file)) {
			JsonNode provenance = original.get("mapping-collection").get("provenance");
			return List.of(
					"MISSING /mapping-collection/provenance/qa-reviewed = " + JsonDiff.abbrev(provenance.get("qa-reviewed")),
					"MISSING /mapping-collection/provenance/qa-note = " + JsonDiff.abbrev(provenance.get("qa-note")));
		}
		if (SUPPLY_CHAIN_COMPONENT.equals(file)) {
			// three implemented requirements carry a single link object where OSCAL wants an
			// array; it is read as an array of one and written as such
			JsonNode requirements = original.get("component-definition").get("components").get(1)
					.get("control-implementations").get(0).get("implemented-requirements");
			return Stream.of(0, 1, 2).map(i -> {
				JsonNode link = requirements.get(i).get("links");
				String path = "/component-definition/components[1]/control-implementations[0]/implemented-requirements["
						+ i + "]/links";
				return "VALUE " + path + " " + JsonDiff.abbrev(link) + " vs "
						+ JsonDiff.abbrev(RoundTrip.JSON.createArrayNode().add(link));
			}).toList();
		}
		return List.of();
	}
}
