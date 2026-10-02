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

/**
 * Round trip over the OSCAL examples of NIST (usnistgov/oscal-content, CC0), one or more per
 * model: catalog, profile, component-definition, system-security-plan, assessment-plan,
 * assessment-results and plan-of-action-and-milestones, in OSCAL 1.1.2 to 1.2.2.
 * <p>
 * The files are committed in {@code test-data/nist}, SP 800-53 rev5 (10 MB) is local test data in
 * {@code test-data/nist-large}; the checks are those of {@link BsiRoundTripTest}.
 * </p>
 */
@DisplayName("NIST OSCAL examples round trip")
class NistRoundTripTest {

	static Stream<Path> files() throws IOException {
		List<Path> files = new ArrayList<>();
		for (String dir : List.of("test-data/nist", "test-data/nist-large")) {
			if (Files.isDirectory(Path.of(dir))) {
				try (Stream<Path> paths = Files.list(Path.of(dir))) {
					paths.filter(p -> p.toString().endsWith(".json")).sorted().forEach(files::add);
				}
			}
		}
		return files.stream();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("files")
	void roundTrip(Path file) throws IOException {
		RoundTrip roundTrip = RoundTrip.of(file);

		assertEquals(List.of(), roundTrip.errors(), "load errors");
		assertEquals(List.of(), roundTrip.warnings(), "load warnings");
		assertEquals(List.of(), roundTrip.oracle().findings(), "model vs. JSON");
		assertEquals(RoundTrip.scalarCount(roundTrip.original()), roundTrip.oracle().checkedValues(),
				"not every JSON value was compared with the model");
		assertEquals(List.of(), roundTrip.diffs(), "round trip");
		assertEquals(List.of(), roundTrip.reloadDiagnostics(), "reload diagnostics");
		assertTrue(roundTrip.stable(), "second round trip is not stable");
	}
}
