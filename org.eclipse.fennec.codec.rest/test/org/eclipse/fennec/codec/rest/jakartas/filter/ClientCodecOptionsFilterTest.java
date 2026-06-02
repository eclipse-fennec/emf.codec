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
package org.eclipse.fennec.codec.rest.jakartas.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the header → whitelisted-options parsing of {@link ClientCodecOptionsFilter}.
 */
@DisplayName("ClientCodecOptionsFilter — header parsing")
class ClientCodecOptionsFilterTest {

    private static final Map<String, Class<?>> WHITELIST = Map.of(
            "codec.tabular.referenceMode", String.class,
            "codec.csv.dataTypeInSecondRow", Boolean.class,
            "serializeDefault", Boolean.class);

    @Test
    @DisplayName("parses whitelisted keys and coerces to the declared type")
    void parsesWhitelisted() {
        Map<String, Object> opts = ClientCodecOptionsFilter.parseClientOptions(WHITELIST,
                List.of("codec.tabular.referenceMode=FLAT, codec.csv.dataTypeInSecondRow=false"));

        assertEquals("FLAT", opts.get("codec.tabular.referenceMode"));
        assertEquals(Boolean.FALSE, opts.get("codec.csv.dataTypeInSecondRow"));
        assertEquals(Boolean.class, opts.get("codec.csv.dataTypeInSecondRow").getClass());
    }

    @Test
    @DisplayName("ignores keys that are not whitelisted")
    void ignoresNonWhitelisted() {
        Map<String, Object> opts = ClientCodecOptionsFilter.parseClientOptions(WHITELIST,
                List.of("codec.tabular.referenceMode=FLAT, expand=address, typeStrategy=NONE"));

        assertEquals(1, opts.size());
        assertTrue(opts.containsKey("codec.tabular.referenceMode"));
        assertFalse(opts.containsKey("expand"));
        assertFalse(opts.containsKey("typeStrategy"));
    }

    @Test
    @DisplayName("tolerates whitespace, empty entries, and missing '='")
    void tolerantParsing() {
        Map<String, Object> opts = ClientCodecOptionsFilter.parseClientOptions(WHITELIST,
                List.of("  serializeDefault = true ,, garbage , =novalue "));

        assertEquals(Boolean.TRUE, opts.get("serializeDefault"));
        assertEquals(1, opts.size());
    }

    @Test
    @DisplayName("empty whitelist ⇒ nothing parsed (secure by default)")
    void emptyWhitelist() {
        Map<String, Object> opts = ClientCodecOptionsFilter.parseClientOptions(Map.of(),
                List.of("codec.tabular.referenceMode=FLAT"));
        assertTrue(opts.isEmpty());
    }

    @Test
    @DisplayName("null header ⇒ empty result")
    void nullHeader() {
        assertTrue(ClientCodecOptionsFilter.parseClientOptions(WHITELIST, null).isEmpty());
    }
}
