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
package org.eclipse.fennec.codec.ser;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.context.CodecWriteContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for cross-document containment serialization.
 * <p>
 * Cross-document containment occurs when a containment reference points to an
 * object that is stored in a different EMF Resource. In this case, the reference
 * should be serialized as a reference (like non-containment) rather than inline.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/07-reference.md#6-cross-document-containment">Spec: Cross-Document Containment</a>
 */
@DisplayName("Cross-Document Containment Serialization")
class CrossDocumentContainmentTest extends SerializationEntryTestBase {

    private ResourceSet resourceSet;
    private Resource sourceResource;
    private Resource targetResource;
    private CodecWriteContext writeContext;
    private EffectiveCodecConfig effectiveConfig;

    @BeforeEach
    void setUp() {
        resourceSet = new ResourceSetImpl();

        // Create two resources in the same ResourceSet
        sourceResource = new ResourceImpl(URI.createURI("source.json"));
        targetResource = new ResourceImpl(URI.createURI("target.json"));
        resourceSet.getResources().add(sourceResource);
        resourceSet.getResources().add(targetResource);

        // Create a real EffectiveCodecConfig (it's a final class, can't be mocked)
        effectiveConfig = EffectiveCodecConfig.builder()
                .resolver(org.eclipse.fennec.codec.config.ConfigurationResolver.defaults())
                .diagnostics(new org.eclipse.fennec.codec.diagnostic.DiagnosticCollector())
                .build();

        // Create write context with source resource
        writeContext = CodecWriteContext.createRootContext(null, effectiveConfig);
        writeContext.setResource(sourceResource);

        // Configure generator to return the write context
        when(generator.streamWriteContext()).thenReturn(writeContext);
    }

    private FeatureConfig createConfig(String key) {
        return FeatureConfig.builder()
                .key(key)
                .serializeNull(false)
                .serializeEmpty(false)
                .build();
    }

    @Nested
    @DisplayName("Same-document containment")
    class SameDocumentTests {

        @Test
        @DisplayName("serializes containment inline when target is in same resource")
        void serializesContainmentInlineWhenSameResource() {
            FeatureConfig config = createConfig("address");
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(config, addressRef, "_ref");

            // Create person and address in the same resource
            EObject person = createPerson("John");
            EObject address = createAddress("123 Main St", "Springfield");

            sourceResource.getContents().add(person);
            person.eSet(addressRef, address);
            // Address is contained in person, so it's in the same resource

            entry.serialize(createState(person), generator, serializationContext);

            // Should serialize inline (writeValue), not as a reference
            verify(generator).writeName("address");
            verify(serializationContext).writeValue(generator, address);
            // Should NOT write as reference
            verify(generator, never()).writeStartObject();
        }
    }

    @Nested
    @DisplayName("Cross-document containment")
    class CrossDocumentTests {

        @Test
        @DisplayName("serializes containment as reference when target is in different resource")
        void serializesContainmentAsRefWhenDifferentResource() {
            FeatureConfig config = createConfig("address");
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(config, addressRef, "_ref");

            // Create person in source resource
            EObject person = createPerson("John");
            sourceResource.getContents().add(person);

            // Create address in target resource (cross-document containment)
            EObject address = createAddress("123 Main St", "Springfield");
            targetResource.getContents().add(address);

            // Set the reference (this simulates cross-document containment)
            person.eSet(addressRef, address);

            entry.serialize(createState(person), generator, serializationContext);

            // Should serialize as reference object, not inline
            verify(generator).writeName("address");
            verify(generator).writeStartObject();
            verify(generator).writeEndObject();

            // Should NOT call writeValue (inline serialization)
            verify(serializationContext, never()).writeValue(generator, address);
        }

        @Test
        @DisplayName("includes type in cross-document reference when smart compression is off")
        void includesTypeWhenSmartCompressionOff() {
            FeatureConfig config = createConfig("address");
            // Smart compression is off by default (false in constructor)
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(config, addressRef, "_ref", false);

            EObject person = createPerson("John");
            sourceResource.getContents().add(person);

            EObject address = createAddress("123 Main St", "Springfield");
            targetResource.getContents().add(address);
            person.eSet(addressRef, address);

            entry.serialize(createState(person), generator, serializationContext);

            // Should include _type in the reference object (called twice: once for _type, once for _ref)
            // When smart compression is off, both _type and _ref are written
            verify(generator).writeStartObject();
            verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("omits type in cross-document reference when smart compression matches type")
        void omitsTypeWhenSmartCompressionMatchesType() {
            FeatureConfig config = createConfig("address");
            // Smart compression ON
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(config, addressRef, "_ref", true);

            EObject person = createPerson("John");
            sourceResource.getContents().add(person);

            // Create address (same type as reference type)
            EObject address = createAddress("123 Main St", "Springfield");
            targetResource.getContents().add(address);
            person.eSet(addressRef, address);

            entry.serialize(createState(person), generator, serializationContext);

            // Should write reference object with just _ref (type omitted)
            verify(generator).writeStartObject();
            verify(generator).writeEndObject();
        }
    }

    @Nested
    @DisplayName("Non-containment references")
    class NonContainmentTests {

        @Test
        @DisplayName("serializes non-containment as reference regardless of resource")
        void serializesNonContainmentAsRef() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .build();
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(config, managerRef, "_ref");

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            sourceResource.getContents().add(person);
            sourceResource.getContents().add(manager);
            person.eSet(managerRef, manager);

            entry.serialize(createState(person), generator, serializationContext);

            // Non-containment is always serialized as reference
            verify(generator).writeName("manager");
            verify(generator).writeStartObject();
            verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("serializes cross-resource non-containment as reference with relative URI")
        void serializesCrossResourceNonContainmentWithRelativeUri() {
            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .build();
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(config, managerRef, "_ref");

            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            sourceResource.getContents().add(person);
            targetResource.getContents().add(manager); // Manager in different resource
            person.eSet(managerRef, manager);

            entry.serialize(createState(person), generator, serializationContext);

            // Should serialize as reference with cross-resource URI
            verify(generator).writeName("manager");
            verify(generator).writeStartObject();
            verify(generator).writeEndObject();
        }
    }
}
