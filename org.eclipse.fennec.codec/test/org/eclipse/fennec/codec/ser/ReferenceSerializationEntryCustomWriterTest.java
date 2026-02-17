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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

/**
 * Tests for custom value writer support in {@link ReferenceSerializationEntry}.
 *
 * @see <a href="docs/codec-v2-spec/10-custom-values.md#5-reference-value-readerswriters">Spec: Reference Value Writers</a>
 */
@DisplayName("ReferenceSerializationEntry Custom Writer")
class ReferenceSerializationEntryCustomWriterTest extends SerializationEntryTestBase {

    @Nested
    @DisplayName("Custom writer configuration")
    class CustomWriterConfiguration {

        @Test
        @DisplayName("uses custom writer when configured")
        void usesCustomWriterWhenConfigured() throws IOException {
            CodecValueWriter<EObject, EReference> customWriter = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "customRefWriter";
                }

                @Override
                public void write(EObject target, EReference ref, CodecWriterContext ctx) throws IOException {
                    // Custom writer extracts ID from target
                    Object id = target.eGet(idAttribute);
                    ctx.getGenerator().writeString(id != null ? id.toString() : "unknown");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("customRefWriter", customWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueWriterName("customRefWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, managerRef, "_ref", false, null, entryContext);

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            manager.eSet(idAttribute, "boss-123");
            person.eSet(managerRef, manager);

            entry.serialize(createState(person), generator, serializationContext);

            // Verify the custom writer was used (writes "boss-123" instead of URI)
            verify(generator).writeName("manager");
            verify(generator).writeStartObject();
            verify(generator).writeString("boss-123");
            verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("falls back to default when writer not found")
        void fallsBackToDefaultWhenWriterNotFound() {
            CodecValueRegistry registry = new CodecValueRegistry();
            // Don't register the writer

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueWriterName("nonExistentWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, managerRef, "_ref", false, null, entryContext);

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            person.eSet(managerRef, manager);

            entry.serialize(createState(person), generator, serializationContext);

            // Should use default STRUCTURED format: { "_type": typeUri, "_ref": refUri }
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("manager");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("_type", "http://test.example.org/serialization/1.0#//Person");
            inOrder.verify(generator).writeName("_ref");
            inOrder.verify(generator).writeString("http://test.example.org/serialization/1.0#//Person");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("does not use writer when registry is null")
        void doesNotUseWriterWhenRegistryIsNull() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueWriterName("customRefWriter")
                    .build();

            // Pass null context
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, managerRef, "_ref", false, null, null);

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            person.eSet(managerRef, manager);

            entry.serialize(createState(person), generator, serializationContext);

            // Should use default STRUCTURED format with _type and _ref
            verify(generator).writeStringProperty("_type", "http://test.example.org/serialization/1.0#//Person");
            verify(generator).writeName("_ref");
            verify(generator).writeString("http://test.example.org/serialization/1.0#//Person");
        }

        @Test
        @DisplayName("does not use writer when writer name is empty")
        void doesNotUseWriterWhenWriterNameIsEmpty() {
            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("customRefWriter", new CodecValueWriter<EObject, EReference>() {
                @Override
                public String getName() {
                    return "customRefWriter";
                }

                @Override
                public void write(EObject target, EReference ref, CodecWriterContext ctx) throws IOException {
                    // no-op
                }
            });

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueWriterName("")  // Empty writer name
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, managerRef, "_ref", false, null, entryContext);

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            person.eSet(managerRef, manager);

            entry.serialize(createState(person), generator, serializationContext);

            // Should use default STRUCTURED format with _type and _ref
            verify(generator).writeStringProperty("_type", "http://test.example.org/serialization/1.0#//Person");
            verify(generator).writeName("_ref");
            verify(generator).writeString("http://test.example.org/serialization/1.0#//Person");
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("wraps IOException from custom writer")
        void wrapsIOExceptionFromCustomWriter() throws IOException {
            CodecValueWriter<EObject, EReference> failingWriter = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "failingWriter";
                }

                @Override
                public void write(EObject target, EReference ref, CodecWriterContext ctx) throws IOException {
                    throw new IOException("Test IO error");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("failingWriter", failingWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueWriterName("failingWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, managerRef, "_ref", false, null, entryContext);

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            person.eSet(managerRef, manager);

            UncheckedIOException thrown = assertThrows(UncheckedIOException.class,
                    () -> entry.serialize(createState(person), generator, serializationContext));

            // Verify error message contains reference name
            assert thrown.getMessage().contains("manager");
        }
    }

    @Nested
    @DisplayName("Custom writer with different formats")
    class CustomWriterFormats {

        @Test
        @DisplayName("writes MongoDB ObjectId format")
        void writesMongoDbObjectIdFormat() throws IOException {
            CodecValueWriter<EObject, EReference> mongoIdWriter = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "mongoId";
                }

                @Override
                public void write(EObject target, EReference ref, CodecWriterContext ctx) throws IOException {
                    Object id = target.eGet(target.eClass().getEStructuralFeature("id"));
                    if (id != null) {
                        ctx.getGenerator().writeString(id.toString());
                    } else {
                        ctx.getGenerator().writeString("unknown");
                    }
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("mongoId", mongoIdWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueWriterName("mongoId")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, managerRef, "_ref", false, null, entryContext);

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            manager.eSet(idAttribute, "507f1f77bcf86cd799439011");
            person.eSet(managerRef, manager);

            entry.serialize(createState(person), generator, serializationContext);

            verify(generator).writeString("507f1f77bcf86cd799439011");
        }

        @Test
        @DisplayName("writes custom URI scheme")
        void writesCustomUriScheme() throws IOException {
            CodecValueWriter<EObject, EReference> customUriWriter = new CodecValueWriter<>() {
                @Override
                public String getName() {
                    return "customUri";
                }

                @Override
                public void write(EObject target, EReference ref, CodecWriterContext ctx) throws IOException {
                    String typeName = target.eClass().getName();
                    Object id = target.eGet(target.eClass().getEStructuralFeature("id"));
                    ctx.getGenerator().writeString("urn:myapp:" + typeName + "/" + (id != null ? id : "0"));
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("customUri", customUriWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueWriterName("customUri")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, managerRef, "_ref", false, null, entryContext);

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            manager.eSet(idAttribute, "42");
            person.eSet(managerRef, manager);

            entry.serialize(createState(person), generator, serializationContext);

            verify(generator).writeString("urn:myapp:Person/42");
        }
    }
}
