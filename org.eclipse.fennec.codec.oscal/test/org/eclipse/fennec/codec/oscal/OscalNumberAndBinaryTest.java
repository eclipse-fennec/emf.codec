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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * OSCAL values that the generic JSON mapping of the codec did not round-trip: Base64 content,
 * decimals with more digits than a double holds and integers beyond {@code long}
 * (issues #258, #259, #260). Every OSCAL object is read without a type key, so every property
 * goes through the deferred buffer of the deserializer.
 */
@DisplayName("OSCAL numbers and binary content")
class OscalNumberAndBinaryTest {

	private static final Path FIXTURES = Path.of("test-data/fixtures/numbers");

	private static void assertExactRoundTrip(RoundTrip roundTrip) {
		assertEquals(List.of(), roundTrip.warnings(), "load warnings");
		assertEquals(List.of(), roundTrip.oracle().findings(), "model vs. JSON");
		assertEquals(List.of(), roundTrip.diffs(), "round trip");
		assertTrue(roundTrip.stable(), "second round trip is not stable");
	}

	/** {@code base64/value} is XML Schema {@code base64Binary}, written as Base64 again (issue #260). */
	@Test
	void base64IsWrittenAsBase64() throws IOException {
		assertExactRoundTrip(RoundTrip.of(FIXTURES.resolve("back-matter-base64.json")));
	}

	/** {@code target-coverage} is a decimal: all digits are read (#258) and written as a number (#259). */
	@Test
	void decimalKeepsAllDigits() throws IOException {
		assertExactRoundTrip(RoundTrip.of(FIXTURES.resolve("decimal-precision.json")));
	}

	/** {@code port-ranges/end} is a {@code nonNegativeInteger}: beyond {@code long} it loads (#258) and is written as a number (#259). */
	@Test
	void bigIntegerLoadsAndIsWrittenAsNumber() throws IOException {
		assertExactRoundTrip(RoundTrip.of(FIXTURES.resolve("big-integer.json")));
	}
}
