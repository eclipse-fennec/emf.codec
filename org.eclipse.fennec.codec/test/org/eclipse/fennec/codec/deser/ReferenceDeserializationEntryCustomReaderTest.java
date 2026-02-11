/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;

/**
 * Tests for custom value reader support in {@link ReferenceDeserializationEntry}.
 *
 * @see <a href="docs/codec-v2-spec/10-custom-values.md#5-reference-value-readerswriters">Spec: Reference Value Readers</a>
 */
@DisplayName("ReferenceDeserializationEntry Custom Reader")
class ReferenceDeserializationEntryCustomReaderTest extends DeserializationEntryTestBase {

    private static final String DEFAULT_REF_KEY = "_ref";

    @Nested
    @DisplayName("Custom reader configuration")
    class CustomReaderConfiguration {

        @Test
        @DisplayName("uses custom reader when configured")
        void usesCustomReaderWhenConfigured() {
            // Custom reader transforms ObjectId to EMF URI
            CodecValueReader<String, EReference> customReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "customRefReader";
                }

                @Override
                public String read(CodecReaderContext ctx, EReference ref) throws IOException {
                    String objectId = ctx.getParser().getString();
                    return "#/persons/" + objectId;  // Transform to EMF URI
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("customRefReader", customReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("customRefReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // JSON with ObjectId instead of URI
            try (JsonParser parser = createParser("{\"_ref\": \"507f1f77bcf86cd799439011\"}")) {
                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                // Custom reader transforms to EMF URI
                assertEquals("#/persons/507f1f77bcf86cd799439011",
                        state.getUnresolvedReferences().get(0).getTargetUri());
            }
        }

        @Test
        @DisplayName("falls back to default when reader not found")
        void fallsBackToDefaultWhenReaderNotFound() {
            CodecValueRegistry registry = new CodecValueRegistry();
            // Don't register the reader

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("nonExistentReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"_ref\": \"#/persons/1\"}")) {
                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                // Default: reads URI as-is
                assertEquals("#/persons/1", state.getUnresolvedReferences().get(0).getTargetUri());
            }
        }

        @Test
        @DisplayName("does not use reader when registry is null")
        void doesNotUseReaderWhenRegistryIsNull() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("customRefReader")
                    .build();

            // Pass null context
            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, null);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"_ref\": \"#/persons/1\"}")) {
                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                assertEquals("#/persons/1", state.getUnresolvedReferences().get(0).getTargetUri());
            }
        }

        @Test
        @DisplayName("does not use reader when reader name is empty")
        void doesNotUseReaderWhenReaderNameIsEmpty() {
            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("customRefReader", new CodecValueReader<String, EReference>() {
                @Override
                public String getName() {
                    return "customRefReader";
                }

                @Override
                public String read(CodecReaderContext ctx, EReference ref) {
                    return "transformed";
                }
            });

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("")  // Empty reader name
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"_ref\": \"#/persons/1\"}")) {
                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                // Default: reads URI as-is (not transformed)
                assertEquals("#/persons/1", state.getUnresolvedReferences().get(0).getTargetUri());
            }
        }
    }

    @Nested
    @DisplayName("Custom reader for multi-valued references")
    class CustomReaderMultiValued {

        @Test
        @DisplayName("uses custom reader for array elements")
        void usesCustomReaderForArrayElements() {
            CodecValueReader<String, EReference> customReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "customRefReader";
                }

                @Override
                public String read(CodecReaderContext ctx, EReference ref) throws IOException {
                    String objectId = ctx.getParser().getString();
                    return "#/colleagues/" + objectId;
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("customRefReader", customReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("colleagues")
                    .valueReaderName("customRefReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, colleaguesRef, DEFAULT_REF_KEY, entryContext);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // Array of ObjectIds
            try (JsonParser parser = createParser("[{\"_ref\": \"abc123\"}, {\"_ref\": \"def456\"}]")) {
                entry.deserialize(state, parser, null);

                assertEquals(2, state.getUnresolvedReferences().size());
                assertEquals("#/colleagues/abc123", state.getUnresolvedReferences().get(0).getTargetUri());
                assertEquals("#/colleagues/def456", state.getUnresolvedReferences().get(1).getTargetUri());
            }
        }

        @Test
        @DisplayName("uses custom reader for PLAIN format array elements")
        void usesCustomReaderForPlainFormatArrayElements() {
            CodecValueReader<String, EReference> customReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "customRefReader";
                }

                @Override
                public String read(CodecReaderContext ctx, EReference ref) throws IOException {
                    String objectId = ctx.getParser().getString();
                    return "#/colleagues/" + objectId;
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("customRefReader", customReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("colleagues")
                    .valueReaderName("customRefReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, colleaguesRef, DEFAULT_REF_KEY, entryContext);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // Direct URI strings (PLAIN format)
            try (JsonParser parser = createParser("[\"abc123\", \"def456\"]")) {
                entry.deserialize(state, parser, null);

                assertEquals(2, state.getUnresolvedReferences().size());
                assertEquals("#/colleagues/abc123", state.getUnresolvedReferences().get(0).getTargetUri());
                assertEquals("#/colleagues/def456", state.getUnresolvedReferences().get(1).getTargetUri());
            }
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("wraps IOException from custom reader")
        void wrapsIOExceptionFromCustomReader() {
            CodecValueReader<String, EReference> failingReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "failingReader";
                }

                @Override
                public String read(CodecReaderContext ctx, EReference ref) throws IOException {
                    throw new IOException("Test IO error");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("failingReader", failingReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("failingReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"_ref\": \"some-id\"}")) {
                UncheckedIOException thrown = assertThrows(UncheckedIOException.class,
                        () -> entry.deserialize(state, parser, null));

                // Verify error message contains reference name
                assert thrown.getMessage().contains("manager");
            }
        }
    }

    @Nested
    @DisplayName("Custom reader with different formats")
    class CustomReaderFormats {

        @Test
        @DisplayName("reads MongoDB ObjectId format")
        void readsMongoDbObjectIdFormat() {
            CodecValueReader<String, EReference> mongoIdReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "mongoId";
                }

                @Override
                public String read(CodecReaderContext ctx, EReference ref) throws IOException {
                    String objectId = ctx.getParser().getString();
                    // Transform MongoDB ObjectId to EMF URI format
                    return "mongodb://mydb/persons/" + objectId;
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("mongoId", mongoIdReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("mongoId")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"_ref\": \"507f1f77bcf86cd799439011\"}")) {
                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                assertEquals("mongodb://mydb/persons/507f1f77bcf86cd799439011",
                        state.getUnresolvedReferences().get(0).getTargetUri());
            }
        }

        @Test
        @DisplayName("reads custom URI scheme")
        void readsCustomUriScheme() {
            CodecValueReader<String, EReference> customUriReader = new CodecValueReader<>() {
                @Override
                public String getName() {
                    return "customUri";
                }

                @Override
                public String read(CodecReaderContext ctx, EReference ref) throws IOException {
                    String customUri = ctx.getParser().getString();
                    // Transform urn:myapp:Person/42 to EMF URI
                    if (customUri.startsWith("urn:myapp:")) {
                        String path = customUri.substring("urn:myapp:".length());
                        return "#//" + path.replace("/", "/@");
                    }
                    return customUri;
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("customUri", customUriReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("customUri")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"_ref\": \"urn:myapp:Person/42\"}")) {
                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                assertEquals("#//Person/@42", state.getUnresolvedReferences().get(0).getTargetUri());
            }
        }
    }
}
