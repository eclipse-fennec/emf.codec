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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.prefix.CodecPrefixReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The read side of prefix keys (issue #193 / #197, spec 14-custom-values.md §13.5–§13.6):
 * routing, deferral, the diagnostics rule, clashes, options.
 */
@DisplayName("Prefix readers")
class PrefixReadTest extends PrefixTestSupport {

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

    /** Prefix keys before the type on every object - the case the deferral exists for. */
    private static String documentKeysFirst() {
        return "{\"_tenant\":\"t1\"," + type("Order") + ",\"_id\":\"o1\",\"orderNo\":\"A-1\","
                + "\"items\":["
                + "{\"_tenant\":\"t1\",\"_owner\":\"o1\"," + type("Item") + ",\"_id\":\"i1\",\"label\":\"L1\",\"tenant\":\"f1\"},"
                + "{\"_owner\":\"o1\",\"_tenant\":\"t1\"," + type("Item") + ",\"_id\":\"i2\",\"label\":\"L2\",\"tenant\":\"f2\"}"
                + "]}";
    }

    /** The same document with the prefix keys after the features. */
    private static String documentKeysLast() {
        return "{" + type("Order") + ",\"_id\":\"o1\",\"orderNo\":\"A-1\",\"_tenant\":\"t1\","
                + "\"items\":["
                + "{" + type("Item") + ",\"_id\":\"i1\",\"label\":\"L1\",\"tenant\":\"f1\",\"_tenant\":\"t1\",\"_owner\":\"o1\"},"
                + "{" + type("Item") + ",\"_id\":\"i2\",\"label\":\"L2\",\"tenant\":\"f2\",\"_owner\":\"o1\",\"_tenant\":\"t1\"}"
                + "]}";
    }

    @Test
    @DisplayName("keys met before the type reach the reader with the EObject created, for root and children")
    void keysBeforeTheType() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecResource resource = resource(registryWith(null, reader, "_tenant", "_owner"), null);

        EObject root = load(resource, documentKeysFirst(), Map.of());

        assertNotNull(root);
        assertEquals("A-1", root.eGet(orderNo));
        assertEquals(List.of("_owner@Item=o1", "_owner@Item=o1", "_tenant@Item=t1", "_tenant@Item=t1", "_tenant@Order=t1"),
                reader.sorted(), "every key on every object");
        List<?> loadedItems = (List<?>) root.eGet(items);
        assertTrue(reader.targets.contains(root) && reader.targets.containsAll(loadedItems),
                "the targets are the very objects that end up in the resource");
        assertTrue(warnings(resource).isEmpty(), "known keys are not reported: " + warnings(resource));
    }

    @Test
    @DisplayName("keys met after the features are read in place, same result")
    void keysAfterTheFeatures() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecResource resource = resource(registryWith(null, reader, "_tenant", "_owner"), null);

        load(resource, documentKeysLast(), Map.of());

        assertEquals(List.of("_tenant@Order=t1", "_tenant@Item=t1", "_owner@Item=o1", "_owner@Item=o1", "_tenant@Item=t1"),
                reader.seen, "read in place: document order");
        assertTrue(warnings(resource).isEmpty(), "was: " + warnings(resource));
    }

    @Test
    @DisplayName("a reader registered under strictOnUnknown: the key is known, the load succeeds silently")
    void readerOnlyIsSilentUnderStrictOnUnknown() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecResource resource = resource(registryWith(null, reader, "_tenant", "_owner"), STRICT_ON_UNKNOWN);

        EObject root = load(resource, documentKeysFirst(), Map.of());

        assertNotNull(root);
        assertEquals(5, reader.seen.size());
        assertTrue(warnings(resource).isEmpty() && errors(resource).isEmpty(),
                "was: " + warnings(resource) + " / " + errors(resource));
    }

    @Test
    @DisplayName("writer only: a key nobody reads is unknown - a warning, and a failure under strictOnUnknown")
    void keyWithoutReaderIsUnknown() throws IOException {
        CodecResource lenient = resource(null, null);

        EObject root = load(lenient, documentKeysFirst(), Map.of());

        assertNotNull(root, "lenient: the model is otherwise intact");
        assertEquals(2, ((List<?>) root.eGet(items)).size());
        List<String> unknown = warnings(lenient).stream().filter(w -> w.contains("Unknown feature")).toList();
        assertTrue(unknown.stream().anyMatch(w -> w.contains("'_tenant'") && w.contains("Order")), "was: " + unknown);
        assertTrue(unknown.stream().anyMatch(w -> w.contains("'_owner'") && w.contains("Item")), "was: " + unknown);

        CodecResource strict = resource(null, STRICT_ON_UNKNOWN);
        assertThrows(Exception.class, () -> load(strict, documentKeysFirst(), Map.of()),
                "strictOnUnknown turns the unread key into a hard error");
    }

    @Test
    @DisplayName("an unknown key beside registered ones: only the unknown one is reported")
    void unknownKeyBesideRegisteredOnes() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecResource resource = resource(registryWith(null, reader, "_tenant", "_owner"), null);
        String json = "{\"_tenant\":\"t1\",\"_stray\":1," + type("Order") + ",\"_id\":\"o1\"}";

        load(resource, json, Map.of());

        assertEquals(List.of("_tenant@Order=t1"), reader.seen);
        List<String> w = warnings(resource);
        assertEquals(1, w.stream().filter(m -> m.contains("Unknown feature")).count(), "was: " + w);
        assertTrue(w.stream().anyMatch(m -> m.contains("'_stray'")), "was: " + w);
    }

    @Test
    @DisplayName("an object value is delivered whole, live and deferred alike")
    void structuredValueDeliveredWhole() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecResource resource = resource(registryWith(null, reader, "_meta"), null);
        String json = "{\"_meta\":{\"a\":[1,2],\"b\":{\"c\":true}}," + type("Order") + ",\"_id\":\"o1\","
                + "\"items\":[{" + type("Item") + ",\"_id\":\"i1\",\"_meta\":{\"a\":[3]}}]}";

        load(resource, json, Map.of());

        assertEquals(List.of("_meta@Order={\"a\":[1,2],\"b\":{\"c\":true}}", "_meta@Item={\"a\":[3]}"), reader.seen);
    }

    @Test
    @DisplayName("a reader that throws: lenient reports an error and reads on, STRICT fails the load")
    void readerThrows() throws IOException {
        CodecPrefixReader broken = (key, target, ctx) -> {
            if ("_owner".equals(key)) {
                throw new IOException("boom");
            }
            ctx.getParser().skipChildren();
        };
        CodecResource lenient = resource(registryWith(null, broken, "_tenant", "_owner"), null);

        EObject root = load(lenient, documentKeysFirst(), Map.of());

        assertNotNull(root);
        assertEquals("L1", ((EObject) ((List<?>) root.eGet(items)).get(0)).eGet(itemLabel),
                "the rest of the object is read");
        assertTrue(errors(lenient).stream().anyMatch(e -> e.contains("_owner") && e.contains("boom")),
                "an ERROR reaches the resource: " + errors(lenient));

        CodecResource strict = resource(registryWith(null, broken, "_tenant", "_owner"), null);
        assertThrows(Exception.class, () -> load(strict, documentKeysFirst(),
                Map.of(CodecOptions.CODEC_DESERIALIZATION_MODE, "STRICT")));
    }

    @Test
    @DisplayName("a key that equals a feature name is read as the feature; the reader is skipped, one warning per class")
    void featureWinsTheClash() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecResource resource = resource(registryWith(null, reader, "tenant", "_owner"), null);

        EObject root = load(resource, documentKeysFirst(), Map.of());

        List<?> loadedItems = (List<?>) root.eGet(items);
        assertEquals("f1", ((EObject) loadedItems.get(0)).eGet(itemTenant), "the feature got its value");
        assertEquals("f2", ((EObject) loadedItems.get(1)).eGet(itemTenant));
        assertTrue(reader.seen.stream().noneMatch(s -> s.startsWith("tenant@")), "reader not called: " + reader.seen);
        assertEquals(List.of("_owner@Item=o1", "_owner@Item=o1"), reader.seen, "the other key still works");
        List<String> clash = warnings(resource).stream().filter(w -> w.contains("'tenant'") && w.contains("Item")).toList();
        assertEquals(1, clash.size(), "once per class and key: " + warnings(resource));
    }

    @Test
    @DisplayName("an instance bound through the load options beats the registry for its key")
    void optionInstanceBeatsRegistry() throws IOException {
        RecordingReader registryReader = new RecordingReader();
        RecordingReader optionReader = new RecordingReader();
        CodecResource resource = resource(registryWith(null, registryReader, "_tenant", "_owner"), null);

        load(resource, documentKeysFirst(),
                Map.of(CodecOptions.CODEC_PREFIX_READER_INSTANCES, Map.of("_tenant", optionReader)));

        assertEquals(List.of("_tenant@Item=t1", "_tenant@Item=t1", "_tenant@Order=t1"), optionReader.sorted());
        assertEquals(List.of("_owner@Item=o1", "_owner@Item=o1"), registryReader.seen, "keys not overridden stay with the registry");
    }

    @Test
    @DisplayName("write → read → write with a writer/reader pair reproduces the document")
    void roundTrip() throws IOException {
        RecordingReader reader = new RecordingReader();
        var registry = registryWith(standardWriter(), reader, "_tenant", "_owner");

        String first = save(resource(registry, null), order("o1", "A-1", "i1", "i2"), Map.of());
        CodecResource loaded = resource(registry, null);
        EObject root = load(loaded, first, Map.of());
        String second = save(resource(registry, null), root, Map.of());

        assertEquals(tree(first), tree(second), "field order and values survive the round trip");
        assertEquals(List.of("_owner@Item=o1", "_owner@Item=o1", "_tenant@Item=t1", "_tenant@Item=t1", "_tenant@Order=t1"),
                reader.sorted());
        assertTrue(warnings(loaded).isEmpty(), "was: " + warnings(loaded));
    }
}
