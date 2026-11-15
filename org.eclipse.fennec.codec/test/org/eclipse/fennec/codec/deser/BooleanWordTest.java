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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** The YAML 1.1 boolean words read into a boolean feature (issue #297). */
@DisplayName("YAML 1.1 boolean words")
class BooleanWordTest {

	@ParameterizedTest
	@ValueSource(strings = { "y", "Y", "yes", "Yes", "YES", "on", "On", "ON" })
	@DisplayName("true words")
	void trueWords(String word) {
		assertEquals(Boolean.TRUE, AttributeDeserializationEntry.booleanWord(word, boolean.class));
		assertEquals(Boolean.TRUE, AttributeDeserializationEntry.booleanWord(word, Boolean.class));
	}

	@ParameterizedTest
	@ValueSource(strings = { "n", "N", "no", "No", "NO", "off", "Off", "OFF" })
	@DisplayName("false words")
	void falseWords(String word) {
		assertEquals(Boolean.FALSE, AttributeDeserializationEntry.booleanWord(word, boolean.class));
	}

	@ParameterizedTest
	@ValueSource(strings = { "yEs", "oN", "true", "false", "1", "", "maybe" })
	@DisplayName("anything else is left to the regular conversion")
	void otherValues(String value) {
		assertNull(AttributeDeserializationEntry.booleanWord(value, boolean.class));
	}

	@ParameterizedTest
	@ValueSource(strings = { "yes", "on" })
	@DisplayName("only for a boolean target")
	void onlyBooleanTargets(String word) {
		assertNull(AttributeDeserializationEntry.booleanWord(word, String.class));
	}
}
