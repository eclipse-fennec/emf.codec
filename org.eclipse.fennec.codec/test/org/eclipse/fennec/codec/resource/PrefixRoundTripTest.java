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
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;

/**
 * The matrix of 14-custom-values.md §13.6 for JSON, end to end through {@link CodecResource}
 * (issue #193 / #199), plus the position assertions a text format allows. The format-agnostic
 * version of the matrix is {@code AbstractPrefixTCK}, run by every format bundle.
 */
@DisplayName("Prefix round trips (JSON)")
class PrefixRoundTripTest extends PrefixTestSupport {

    private static final ConfigurationResolver STRICT_ON_UNKNOWN = ConfigurationResolver.builder()
            .moduleProperties(Map.of("strictOnUnknown", true)).build();

    @BeforeEach
    void setUp() {
        buildModel();
    }

    @AfterEach
    void tearDown() {
        releaseModel();
    }

    @Test
    @DisplayName("reader and writer: the document, the reader's view and the model all survive")
    void readerAndWriter() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecPrefixRegistry registry = registryWith(standardWriter(), reader, "_tenant", "_owner");

        String first = save(resource(registry, null), order("o1", "A-1", "i1", "i2"), Map.of());
        CodecResource loaded = resource(registry, STRICT_ON_UNKNOWN);
        EObject root = load(loaded, first, Map.of());
        String second = save(resource(registry, null), root, Map.of());

        assertEquals(tree(first), tree(second));
        assertEquals(List.of("_owner@Item=o1", "_owner@Item=o1", "_tenant@Item=t1", "_tenant@Item=t1", "_tenant@Order=t1"),
                reader.sorted());
        assertEquals(2, ((List<?>) root.eGet(items)).size());
        assertTrue(warnings(loaded).isEmpty(), warnings(loaded).toString());
    }

    @Test
    @DisplayName("writer only: written, then unknown on read - warning lenient, failure under strictOnUnknown")
    void writerOnly() throws IOException {
        String document = save(resource(registryWith(standardWriter(), null, "_tenant", "_owner"), null),
                order("o1", "A-1", "i1"), Map.of());
        assertEquals("t1", tree(document).get("_tenant").stringValue());

        CodecResource lenient = resource(null, null);
        assertNotNull(load(lenient, document, Map.of()));
        assertTrue(warnings(lenient).stream().anyMatch(w -> w.contains("Unknown feature '_tenant'")), warnings(lenient).toString());

        CodecResource strict = resource(null, STRICT_ON_UNKNOWN);
        assertThrows(Exception.class, () -> load(strict, document, Map.of()));
    }

    @Test
    @DisplayName("reader only: a foreign document loads silently in both modes; saving it writes no prefix")
    void readerOnly() throws IOException {
        String foreign = save(resource(registryWith(standardWriter(), null, "_tenant", "_owner"), null),
                order("o1", "A-1", "i1"), Map.of());
        RecordingReader reader = new RecordingReader();
        CodecPrefixRegistry readerOnly = registryWith(null, reader, "_tenant", "_owner");

        CodecResource loaded = resource(readerOnly, STRICT_ON_UNKNOWN);
        EObject root = load(loaded, foreign, Map.of());
        assertEquals(3, reader.seen.size(), reader.seen.toString());
        assertTrue(warnings(loaded).isEmpty(), warnings(loaded).toString());

        JsonNode written = tree(save(resource(readerOnly, null), root, Map.of()));
        assertFalse(written.has("_tenant") || written.get("items").get(0).has("_owner"), written.toString());
    }

    @Test
    @DisplayName("neither: today's document, today's read - the regression pin")
    void neither() throws IOException {
        String withNone = save(resource(null, null), order("o1", "A-1", "i1"), Map.of());
        String withEmpty = save(resource(new CodecPrefixRegistry(), null), order("o1", "A-1", "i1"), Map.of());

        assertEquals(tree(withNone), tree(withEmpty));
        CodecResource loaded = resource(null, STRICT_ON_UNKNOWN);
        assertNotNull(load(loaded, withNone, Map.of()));
        assertTrue(warnings(loaded).isEmpty());
    }

    @Test
    @DisplayName("a declining writer: absent where declined, present elsewhere, and the round trip is clean")
    void decliningWriter() throws IOException {
        // _owner declines for the root (no container); _tenant declines for items with an odd id
        CodecPrefixWriter picky = new KeyedWriter(Map.of(
                "_owner", o -> o.eContainer() == null ? null : idOf(o.eContainer()),
                "_tenant", o -> idOf(o).endsWith("1") ? null : "t1"));
        RecordingReader reader = new RecordingReader();
        CodecPrefixRegistry registry = registryWith(picky, reader, "_tenant", "_owner");

        String first = save(resource(registry, null), order("o1", "A-1", "i1", "i2"), Map.of());
        JsonNode root = tree(first);
        assertFalse(root.has("_owner") || root.has("_tenant"), "root: both declined - " + root);
        assertFalse(root.get("items").get(0).has("_tenant"), "i1: declined");
        assertEquals("t1", root.get("items").get(1).get("_tenant").stringValue(), "i2: written");

        CodecResource loaded = resource(registry, STRICT_ON_UNKNOWN);
        String second = save(resource(registry, null), load(loaded, first, Map.of()), Map.of());
        assertEquals(root, tree(second));
        assertEquals(List.of("_owner@Item=o1", "_owner@Item=o1", "_tenant@Item=t1"), reader.sorted());
    }

    @Test
    @DisplayName("STRUCTURED type and id with idOnTop both ways: the prefix sits after the metadata block, never inside, and round-trips")
    void structuredAndIdOnTopVariants() throws IOException {
        for (boolean idOnTop : new boolean[] { true, false }) {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of("idOnTop", idOnTop, "typeFormat", "STRUCTURED", "idFormat", "STRUCTURED"))
                    .build();
            RecordingReader reader = new RecordingReader();
            CodecPrefixRegistry registry = registryWith(standardWriter(), reader, "_tenant", "_owner");
            String variant = "idOnTop=" + idOnTop;

            String first = save(resource(registry, resolver), order("o1", "A-1", "i1"), Map.of());
            JsonNode item = tree(first).get("items").get(0);
            List<String> k = keys(item);
            assertTrue(item.get("_type").isObject() && item.get("_id").isObject(), variant + ": precondition " + item);
            assertFalse(item.get("_type").has("_tenant") || item.get("_id").has("_tenant"), variant + ": never inside");
            int lastMetadata = Math.max(k.indexOf("_type"), k.indexOf("_id"));
            assertEquals(lastMetadata + 1, k.indexOf("_tenant"), variant + ": directly after the metadata block " + k);
            assertEquals(k.indexOf("_tenant") + 1, k.indexOf("_owner"), variant + ": registration order " + k);
            assertTrue(k.indexOf("_owner") < k.indexOf("label"), variant + ": before the features " + k);

            CodecResource loaded = resource(registry, resolver);
            String second = save(resource(registry, resolver), load(loaded, first, Map.of()), Map.of());
            assertEquals(tree(first), tree(second), variant);
            assertTrue(warnings(loaded).isEmpty(), variant + ": " + warnings(loaded));
        }
    }
}
