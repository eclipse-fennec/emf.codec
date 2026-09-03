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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;

/**
 * The write side of prefix keys (issue #193 / #196, spec 14-custom-values.md §13.4): position
 * after the metadata block, registration order, declining writers, clashes, options.
 */
@DisplayName("Prefix writers")
class PrefixWriteTest extends PrefixTestSupport {

    @BeforeEach
    void setUp() {
        buildModel();
    }

    @AfterEach
    void tearDown() {
        releaseModel();
    }

    @Test
    @DisplayName("two keys land after the metadata block and before the first feature, in registration order")
    void prefixAfterMetadataBeforeFeatures() throws IOException {
        CodecPrefixRegistry registry = registryWith(standardWriter(), null, "_tenant", "_owner");
        CodecResource resource = resource(registry, null);

        JsonNode root = tree(save(resource, order("o1", "A-1", "i1", "i2"), Map.of()));

        // root: _owner declined (no container), _tenant present, between the metadata and orderNo
        assertEquals("t1", root.get("_tenant").stringValue());
        assertFalse(root.has("_owner"), "the writer declined for the root");
        assertTrue(pos(root, "_type") < pos(root, "_tenant"), "prefix follows the metadata: " + keys(root));
        assertTrue(pos(root, "_tenant") < pos(root, "orderNo"), "prefix precedes the features: " + keys(root));

        for (JsonNode item : root.get("items")) {
            assertEquals("t1", item.get("_tenant").stringValue());
            assertEquals("o1", item.get("_owner").stringValue(), "contained objects get their container's id");
            List<String> k = keys(item);
            assertTrue(k.indexOf("_type") < k.indexOf("_tenant"), "after metadata: " + k);
            assertEquals(k.indexOf("_tenant") + 1, k.indexOf("_owner"), "registration order, contiguous: " + k);
            assertTrue(k.indexOf("_owner") < k.indexOf("label"), "before the features: " + k);
        }
        assertTrue(warnings(resource).isEmpty(), "was: " + warnings(resource));
    }

    @Test
    @DisplayName("idOnTop=false moves _id, not the prefix - it still follows the last metadata field")
    void idOnTopFalse() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of("idOnTop", false)).build();
        CodecResource resource = resource(registryWith(standardWriter(), null, "_tenant", "_owner"), resolver);

        JsonNode item = tree(save(resource, order("o1", "A-1", "i1"), Map.of())).get("items").get(0);

        List<String> k = keys(item);
        assertTrue(k.indexOf("_type") < k.indexOf("_id"), "idOnTop=false puts the type first: " + k);
        assertTrue(k.indexOf("_id") < k.indexOf("_tenant"), "prefix after the id, the last metadata field: " + k);
        assertTrue(k.indexOf("_owner") < k.indexOf("label"), "prefix before the features: " + k);
    }

    @Test
    @DisplayName("STRUCTURED type: the prefix follows the type object and is never inside it")
    void structuredTypeKeepsPrefixOutside() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of("typeFormat", "STRUCTURED")).build();
        CodecResource resource = resource(registryWith(standardWriter(), null, "_tenant", "_owner"), resolver);

        JsonNode item = tree(save(resource, order("o1", "A-1", "i1"), Map.of())).get("items").get(0);

        assertTrue(item.get("_type").isObject(), "precondition: STRUCTURED type, was " + item);
        assertFalse(item.get("_type").has("_tenant"), "never inside the metadata object");
        assertTrue(pos(item, "_type") < pos(item, "_tenant") && pos(item, "_owner") < pos(item, "label"),
                "after the type object, before the features: " + keys(item));
    }

    @Test
    @DisplayName("sortPropertiesAlphabetically sorts the features, the prefix keeps its slot and order")
    void alphabeticalSortLeavesThePrefixAlone() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of("sortPropertiesAlphabetically", true)).build();
        // registration order deliberately not alphabetical
        CodecResource resource = resource(registryWith(standardWriter(), null, "_tenant", "_owner"), resolver);

        JsonNode item = tree(save(resource, order("o1", "A-1", "i1"), Map.of())).get("items").get(0);

        List<String> k = keys(item);
        assertEquals(k.indexOf("_tenant") + 1, k.indexOf("_owner"), "registration order kept: " + k);
        assertTrue(k.indexOf("_owner") < k.indexOf("label") && k.indexOf("label") < k.indexOf("tenant"),
                "prefix before the (sorted) features: " + k);
    }

    @Test
    @DisplayName("a key that equals a feature name is not written as a prefix; the feature wins, one warning per class")
    void featureWinsTheClash() throws IOException {
        CodecPrefixWriter clashing = new KeyedWriter(Map.of("tenant", o -> "PREFIX", "_owner", o -> "x"));
        CodecResource resource = resource(registryWith(clashing, null, "tenant", "_owner"), null);

        JsonNode root = tree(save(resource, order("o1", "A-1", "i1", "i2"), Map.of()));

        for (JsonNode item : root.get("items")) {
            assertEquals("feat-" + item.get("_id").stringValue(), item.get("tenant").stringValue(),
                    "the feature value, not the prefix writer's");
            assertEquals("x", item.get("_owner").stringValue(), "the other key is unaffected");
        }
        List<String> clashWarnings = warnings(resource).stream()
                .filter(w -> w.contains("tenant") && w.contains("Item")).toList();
        assertEquals(1, clashWarnings.size(), "once per class and key, not per object: " + warnings(resource));
        // Order has no 'tenant' feature: the key would be written there, and the writer declines
        // for nothing, so the root carries it
        assertEquals("PREFIX", root.get("tenant").stringValue());
    }

    @Test
    @DisplayName("an instance bound through the save options beats the registry for its key")
    void optionInstanceBeatsRegistry() throws IOException {
        CodecResource resource = resource(registryWith(standardWriter(), null, "_tenant", "_owner"), null);
        CodecPrefixWriter override = new KeyedWriter(Map.of("_tenant", o -> "from-options"));

        JsonNode root = tree(save(resource, order("o1", "A-1", "i1"),
                Map.of(CodecOptions.CODEC_PREFIX_WRITER_INSTANCES, Map.of("_tenant", override))));

        assertEquals("from-options", root.get("_tenant").stringValue());
        assertEquals("o1", root.get("items").get(0).get("_owner").stringValue(), "registry keys not overridden stay");
    }

    @Test
    @DisplayName("a key known only from the save options is written too, after the registry's keys")
    void optionOnlyKeyIsWritten() throws IOException {
        CodecResource resource = resource(registryWith(standardWriter(), null, "_tenant"), null);
        CodecPrefixWriter extra = new KeyedWriter(Map.of("_extra", o -> "e"));

        JsonNode root = tree(save(resource, order("o1", "A-1"),
                Map.of(CodecOptions.CODEC_PREFIX_WRITER_INSTANCES, Map.of("_extra", extra))));

        assertEquals("e", root.get("_extra").stringValue());
        assertEquals(pos(root, "_tenant") + 1, pos(root, "_extra"), keys(root).toString());
    }

    @Test
    @DisplayName("a writer that throws after the name is a warning, the field becomes null, the document stays parseable")
    void writerThrowsAfterName() throws IOException {
        CodecPrefixWriter broken = (key, object, ctx) -> {
            if ("_broken".equals(key)) {
                ctx.getGenerator().writeName(key);
                throw new IOException("boom");
            }
            ctx.getGenerator().writeName(key);
            ctx.getGenerator().writeString("ok");
            return true;
        };
        CodecResource resource = resource(registryWith(broken, null, "_broken", "_fine"), null);

        JsonNode root = tree(save(resource, order("o1", "A-1"), Map.of()));

        assertTrue(root.get("_broken").isNull(), "the dangling name got a null: " + root);
        assertEquals("ok", root.get("_fine").stringValue(), "the next writer still ran");
        assertTrue(warnings(resource).stream().anyMatch(w -> w.contains("_broken") && w.contains("boom")),
                "was: " + warnings(resource));
    }

    @Test
    @DisplayName("no registry, no options: the output is exactly as before")
    void nothingRegisteredNothingWritten() throws IOException {
        CodecResource resource = resource(null, null);

        JsonNode root = tree(save(resource, order("o1", "A-1", "i1"), Map.of()));

        assertFalse(keys(root).stream().anyMatch(k -> k.startsWith("_") && !k.equals("_id") && !k.equals("_type")),
                keys(root).toString());
    }
}
