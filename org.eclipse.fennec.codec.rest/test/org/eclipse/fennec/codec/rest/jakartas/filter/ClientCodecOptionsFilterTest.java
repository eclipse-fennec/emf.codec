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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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

    // ------------------------------------------------------------------
    // The request property is a shared channel (issue #170)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("no earlier value ⇒ the client options become the property")
    void mergeOntoNothing() {
        Map<String, Object> client = Map.of("codec.csv.delimiter", "|");

        Map<String, Object> merged = ClientCodecOptionsFilter.mergeClientOptions(null, client);

        assertEquals(client, merged);
    }

    @Test
    @DisplayName("server-side values written earlier survive; the client wins on a shared key")
    void mergeOntoServerValues() {
        // A server-side filter with a higher priority may have filled the property already.
        // Its keys stay, and a whitelisted client key beats it - whitelisting a key is the
        // decision to let the client set it.
        Map<String, Object> server = Map.of("codec.csv.delimiter", ";", "codec.csv.dataTypeInSecondRow", false);
        Map<String, Object> client = Map.of("codec.csv.delimiter", "|");

        Map<String, Object> merged = ClientCodecOptionsFilter.mergeClientOptions(server, client);

        assertEquals("|", merged.get("codec.csv.delimiter"), "client wins for whitelisted keys");
        assertEquals(false, merged.get("codec.csv.dataTypeInSecondRow"), "server-only keys stay");
        assertEquals(";", server.get("codec.csv.delimiter"), "the earlier map is not mutated");
    }

    @Test
    @DisplayName("a value that is not a map is not a channel ⇒ replaced")
    void mergeOntoGarbage() {
        Map<String, Object> client = Map.of("codec.csv.delimiter", "|");

        Map<String, Object> merged = ClientCodecOptionsFilter.mergeClientOptions("not a map", client);

        assertEquals(client, merged);
    }

    // ------------------------------------------------------------------
    // The codec. prefix is optional on either side (issue #223)
    // ------------------------------------------------------------------

    /** A whitelist spelled the way every module contribution spells it. */
    private static final Map<String, Class<?>> PREFIXED_WHITELIST = Map.of(
            "codec.tabular.referenceMode", String.class,
            "codec.serializeDefault", Boolean.class);

    @Test
    @DisplayName("a prefixed client key matches a bare whitelist entry")
    void prefixedKeyAgainstBareWhitelist() {
        // CodecOptions.CODEC_SERIALIZE_DEFAULT is what a caller has in hand; the entry is bare.
        Map<String, Object> opts = ClientCodecOptionsFilter.parseClientOptions(WHITELIST,
                List.of("codec.serializeDefault=true"));

        assertEquals(Boolean.TRUE, opts.get("serializeDefault"), "stored under the whitelist's own key");
        assertEquals(1, opts.size());
    }

    @Test
    @DisplayName("a bare client key matches a prefixed whitelist entry")
    void bareKeyAgainstPrefixedWhitelist() {
        Map<String, Object> opts = ClientCodecOptionsFilter.parseClientOptions(PREFIXED_WHITELIST,
                List.of("serializeDefault=true, tabular.referenceMode=SQL_TABLES"));

        assertEquals(Boolean.TRUE, opts.get("codec.serializeDefault"));
        assertEquals("SQL_TABLES", opts.get("codec.tabular.referenceMode"));
        assertEquals(2, opts.size());
    }

    @Test
    @DisplayName("normalising the prefix does not widen the allow-list")
    void normalisationDoesNotWiden() {
        Map<String, Object> opts = ClientCodecOptionsFilter.parseClientOptions(PREFIXED_WHITELIST,
                List.of("codec.typeStrategy=NONE, expand=address, codec.expand=address, codec.=x"));

        assertTrue(opts.isEmpty(), "a key whitelisted in neither spelling stays out: " + opts);
    }

    @Test
    @DisplayName("an exact match wins over the alternate spelling")
    void exactMatchWins() {
        // Both spellings whitelisted with different types: the key as sent decides.
        Map<String, Class<?>> both = Map.of(
                "serializeDefault", Boolean.class,
                "codec.serializeDefault", String.class);

        Map<String, Object> opts = ClientCodecOptionsFilter.parseClientOptions(both,
                List.of("codec.serializeDefault=true"));

        assertEquals("true", opts.get("codec.serializeDefault"));
        assertFalse(opts.containsKey("serializeDefault"));
    }

    // ------------------------------------------------------------------
    // A dropped key is reported, not swallowed (issue #223)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("keys no module offers are reported to the caller-supplied collector")
    void dropsAreReported() {
        List<String> dropped = new ArrayList<>();

        ClientCodecOptionsFilter.parseClientOptions(PREFIXED_WHITELIST,
                List.of("codec.serializeDefault=true, expandDepth=3, typeStrategy=NONE"), dropped::add);

        assertEquals(List.of("expandDepth", "typeStrategy"), dropped);
    }

    @Test
    @DisplayName("a key that only needed its prefix normalised is not reported")
    void normalisedKeyIsNotReported() {
        List<String> dropped = new ArrayList<>();

        ClientCodecOptionsFilter.parseClientOptions(PREFIXED_WHITELIST,
                List.of("serializeDefault=true"), dropped::add);

        assertTrue(dropped.isEmpty(), "reported: " + dropped);
    }

    @Test
    @DisplayName("the report names the keys and points at the allow-list")
    void reportNamesTheKeys() {
        String message = ClientCodecOptionsFilter.describeDropped(List.of("typeStrategy", "expandDepth"));

        assertTrue(message.contains("typeStrategy"), message);
        assertTrue(message.contains("expandDepth"), message);
        assertTrue(message.contains("RestOverridableCodecOptions"), message);
    }

    @Test
    @DisplayName("the report is bounded: control characters out, long keys and long lists cut")
    void reportIsBounded() {
        String message = ClientCodecOptionsFilter.describeDropped(List.of("in\njected: X-Evil"));
        assertFalse(message.contains("\n"), "a header value must not break the log line: " + message);

        String longKey = "k".repeat(200);
        assertFalse(ClientCodecOptionsFilter.describeDropped(List.of(longKey)).contains(longKey));

        List<String> many = IntStream.range(0, 40).mapToObj(i -> "key" + i).toList();
        String bounded = ClientCodecOptionsFilter.describeDropped(many);
        assertFalse(bounded.contains("key39"), bounded);
        assertTrue(bounded.contains("30 more"), bounded);
    }
}
