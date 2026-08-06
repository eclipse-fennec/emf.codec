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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import tools.jackson.core.JsonGenerator;

/**
 * Tests for {@link IdSerializationEntry}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#62-id-serialization">Spec 6.2: ID Serialization</a>
 */
@DisplayName("IdSerializationEntry")
class IdSerializationEntryTest {

    private EClass testEClass;
    private EAttribute idAttribute;
    private JsonGenerator generator;

    @BeforeEach
    void setUp() {
        generator = mock(JsonGenerator.class);

        // Create a test EClass with an ID attribute
        testEClass = EcoreFactory.eINSTANCE.createEClass();
        testEClass.setName("TestClass");

        idAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        idAttribute.setName("id");
        idAttribute.setEType(EcorePackage.Literals.ESTRING);
        idAttribute.setID(true);
        testEClass.getEStructuralFeatures().add(idAttribute);
    }

    private SerializationState createState(EObject eObject) {
        return new SerializationState(eObject);
    }

    private IdConfig createDefaultConfig() {
        return IdConfig.builder()
                .key("_id")
                .strategy(org.eclipse.fennec.codec.metadata.model.codec.IdStrategy.ID_FIELD)
                .onTop(true)
                .build();
    }

    @Test
    @DisplayName("getKey returns default _id key")
    void getKeyReturnsDefaultIdKey() {
        IdConfig config = createDefaultConfig();
        IdSerializationEntry entry = new IdSerializationEntry(config, testEClass);
        assertEquals("_id", entry.getKey());
    }

    @Test
    @DisplayName("getKey returns custom key from config")
    void getKeyReturnsCustomKeyFromConfig() {
        IdConfig config = IdConfig.builder()
                .key("customId")
                .build();

        IdSerializationEntry entry = new IdSerializationEntry(config, testEClass);
        assertEquals("customId", entry.getKey());
    }

    @Test
    @DisplayName("shouldSerialize returns false when keyMode is FEATURE_ONLY")
    void shouldSerializeReturnsFalseWhenFeatureOnly() {
        IdConfig config = IdConfig.builder()
                .key("_id")
                .keyMode(org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode.FEATURE_ONLY)
                .build();

        IdSerializationEntry entry = new IdSerializationEntry(config, testEClass);
        EObject eObject = mock(EObject.class);
        when(eObject.eClass()).thenReturn(testEClass);
        when(eObject.eGet(idAttribute)).thenReturn("test-id");

        assertFalse(entry.shouldSerialize(createState(eObject)));
    }

    @Test
    @DisplayName("shouldSerialize returns true when ID value exists")
    void shouldSerializeReturnsTrueWhenIdValueExists() {
        EObject eObject = mock(EObject.class);
        when(eObject.eClass()).thenReturn(testEClass);
        when(eObject.eGet(idAttribute)).thenReturn("test-id-123");

        IdConfig config = createDefaultConfig();
        IdSerializationEntry entry = new IdSerializationEntry(config, testEClass);

        assertTrue(entry.shouldSerialize(createState(eObject)));
    }

    @Test
    @DisplayName("shouldSerialize returns false when ID value is null")
    void shouldSerializeReturnsFalseWhenIdValueNull() {
        EObject eObject = mock(EObject.class);
        when(eObject.eClass()).thenReturn(testEClass);
        when(eObject.eGet(idAttribute)).thenReturn(null);
        when(eObject.eResource()).thenReturn(null);

        IdConfig config = createDefaultConfig();
        IdSerializationEntry entry = new IdSerializationEntry(config, testEClass);

        assertFalse(entry.shouldSerialize(createState(eObject)));
    }

    @Test
    @DisplayName("shouldSerialize returns false when EClass has no ID attribute")
    void shouldSerializeReturnsFalseWhenNoIdAttribute() {
        // EClass without ID attribute - should not serialize even if there's a resource fragment
        // This is intentional: there's no attribute to deserialize a fragment back into
        EClass noIdClass = EcoreFactory.eINSTANCE.createEClass();
        noIdClass.setName("NoIdClass");

        EObject eObject = mock(EObject.class);
        Resource resource = mock(Resource.class);
        when(eObject.eClass()).thenReturn(noIdClass);
        when(eObject.eResource()).thenReturn(resource);
        when(resource.getURIFragment(eObject)).thenReturn("//@items.0");

        IdConfig config = createDefaultConfig();
        IdSerializationEntry entry = new IdSerializationEntry(config, noIdClass);

        // No ID attribute means no ID should be serialized
        assertFalse(entry.shouldSerialize(createState(eObject)));
    }

    @Test
    @DisplayName("serialize writes ID from attribute")
    void serializeWritesIdFromAttribute() {
        EObject eObject = mock(EObject.class);
        when(eObject.eClass()).thenReturn(testEClass);
        when(eObject.eGet(idAttribute)).thenReturn("my-id-value");

        IdConfig config = createDefaultConfig();
        IdSerializationEntry entry = new IdSerializationEntry(config, testEClass);
        entry.serialize(createState(eObject), generator, null);

        verify(generator).writeStringProperty("_id", "my-id-value");
    }

    @Test
    @DisplayName("serialize does nothing when EClass has no ID attribute")
    void serializeDoesNothingWhenNoIdAttribute() {
        // EClass without ID attribute - serialize should not write anything
        EClass noIdClass = EcoreFactory.eINSTANCE.createEClass();
        noIdClass.setName("NoIdClass");

        EObject eObject = mock(EObject.class);
        when(eObject.eClass()).thenReturn(noIdClass);

        IdConfig config = createDefaultConfig();
        IdSerializationEntry entry = new IdSerializationEntry(config, noIdClass);
        entry.serialize(createState(eObject), generator, null);

        // Should not write anything since there's no ID attribute
        // Verify no _id field was written (the only key IdSerializationEntry uses)
        verify(generator, never()).writeStringProperty(eq("_id"), anyString());
        verify(generator, never()).writeName("_id");
    }

    // ========================================================================
    // Multiple ID Features Tests
    // ========================================================================

    @Nested
    @DisplayName("Multiple ID Features")
    class MultipleIdFeaturesTests {

        private EClass multiIdClass;
        private EAttribute firstNameAttr;
        private EAttribute lastNameAttr;

        @BeforeEach
        void setUpMultiId() {
            multiIdClass = EcoreFactory.eINSTANCE.createEClass();
            multiIdClass.setName("Person");

            firstNameAttr = EcoreFactory.eINSTANCE.createEAttribute();
            firstNameAttr.setName("firstName");
            firstNameAttr.setEType(EcorePackage.Literals.ESTRING);
            multiIdClass.getEStructuralFeatures().add(firstNameAttr);

            lastNameAttr = EcoreFactory.eINSTANCE.createEAttribute();
            lastNameAttr.setName("lastName");
            lastNameAttr.setEType(EcorePackage.Literals.ESTRING);
            multiIdClass.getEStructuralFeatures().add(lastNameAttr);
        }

        @Test
        @DisplayName("PLAIN format combines multiple features with separator")
        void plainFormatCombinesMultipleFeaturesWithSeparator() {
            EObject eObject = mock(EObject.class);
            when(eObject.eClass()).thenReturn(multiIdClass);
            when(eObject.eGet(firstNameAttr)).thenReturn("John");
            when(eObject.eGet(lastNameAttr)).thenReturn("Doe");

            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdSerializationEntry entry = new IdSerializationEntry(config, multiIdClass);
            entry.serialize(createState(eObject), generator, null);

            verify(generator).writeStringProperty("_id", "John-Doe");
        }

        @Test
        @DisplayName("PLAIN format writes a single-entry idFeatures value, not the eID attribute")
        void plainFormatWritesSingleEntryIdFeature() {
            // issue #112: a one-entry idFeatures list wins over the eID attribute on both sides
            EClass keyedClass = EcoreFactory.eINSTANCE.createEClass();
            keyedClass.setName("KeyedPerson");

            EAttribute personIdAttr = EcoreFactory.eINSTANCE.createEAttribute();
            personIdAttr.setName("personId");
            personIdAttr.setEType(EcorePackage.Literals.ESTRING);
            personIdAttr.setID(true);
            keyedClass.getEStructuralFeatures().add(personIdAttr);

            EAttribute nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
            nameAttr.setName("name");
            nameAttr.setEType(EcorePackage.Literals.ESTRING);
            keyedClass.getEStructuralFeatures().add(nameAttr);

            EObject eObject = mock(EObject.class);
            when(eObject.eClass()).thenReturn(keyedClass);
            when(eObject.eGet(personIdAttr)).thenReturn("p-123");
            when(eObject.eGet(nameAttr)).thenReturn("John Doe");

            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .idFeatures(List.of("name"))
                    .build();

            IdSerializationEntry entry = new IdSerializationEntry(config, keyedClass);
            entry.serialize(createState(eObject), generator, null);

            verify(generator).writeStringProperty("_id", "John Doe");
        }

        @Test
        @DisplayName("PLAIN format writes separator field when serializeSeparator=true")
        void plainFormatWritesSeparatorField() {
            EObject eObject = mock(EObject.class);
            when(eObject.eClass()).thenReturn(multiIdClass);
            when(eObject.eGet(firstNameAttr)).thenReturn("John");
            when(eObject.eGet(lastNameAttr)).thenReturn("Doe");

            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .serializeSeparator(true)
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdSerializationEntry entry = new IdSerializationEntry(config, multiIdClass);
            entry.serialize(createState(eObject), generator, null);

            verify(generator).writeStringProperty("_id", "John-Doe");
            verify(generator).writeStringProperty("_separator", "-");
        }

        @Test
        @DisplayName("PLAIN format does not write separator field when serializeSeparator=false")
        void plainFormatDoesNotWriteSeparatorFieldWhenDisabled() {
            EObject eObject = mock(EObject.class);
            when(eObject.eClass()).thenReturn(multiIdClass);
            when(eObject.eGet(firstNameAttr)).thenReturn("John");
            when(eObject.eGet(lastNameAttr)).thenReturn("Doe");

            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.PLAIN)
                    .separator("-")
                    .serializeSeparator(false)
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdSerializationEntry entry = new IdSerializationEntry(config, multiIdClass);
            entry.serialize(createState(eObject), generator, null);

            verify(generator).writeStringProperty("_id", "John-Doe");
            verify(generator, never()).writeStringProperty("_separator", "-");
        }
    }

    // ========================================================================
    // STRUCTURED Format Tests
    // ========================================================================

    @Nested
    @DisplayName("STRUCTURED format")
    class StructuredFormatTests {

        private EClass multiIdClass;
        private EAttribute firstNameAttr;
        private EAttribute lastNameAttr;

        @BeforeEach
        void setUpMultiId() {
            multiIdClass = EcoreFactory.eINSTANCE.createEClass();
            multiIdClass.setName("Person");

            firstNameAttr = EcoreFactory.eINSTANCE.createEAttribute();
            firstNameAttr.setName("firstName");
            firstNameAttr.setEType(EcorePackage.Literals.ESTRING);
            multiIdClass.getEStructuralFeatures().add(firstNameAttr);

            lastNameAttr = EcoreFactory.eINSTANCE.createEAttribute();
            lastNameAttr.setName("lastName");
            lastNameAttr.setEType(EcorePackage.Literals.ESTRING);
            multiIdClass.getEStructuralFeatures().add(lastNameAttr);
        }

        @Test
        @DisplayName("serializes single ID as nested object")
        void serializesSingleIdAsNestedObject() {
            EObject eObject = mock(EObject.class);
            when(eObject.eClass()).thenReturn(testEClass);
            when(eObject.eGet(idAttribute)).thenReturn("john");

            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .build();

            IdSerializationEntry entry = new IdSerializationEntry(config, testEClass);
            entry.serialize(createState(eObject), generator, null);

            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_id");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("id", "john");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("serializes multiple IDs as nested object with separator")
        void serializesMultipleIdsAsNestedObjectWithSeparator() {
            EObject eObject = mock(EObject.class);
            when(eObject.eClass()).thenReturn(multiIdClass);
            when(eObject.eGet(firstNameAttr)).thenReturn("John");
            when(eObject.eGet(lastNameAttr)).thenReturn("Doe");

            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .separator("-")
                    .serializeSeparator(true)
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdSerializationEntry entry = new IdSerializationEntry(config, multiIdClass);
            entry.serialize(createState(eObject), generator, null);

            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_id");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("separator", "-");
            inOrder.verify(generator).writeStringProperty("firstName", "John");
            inOrder.verify(generator).writeStringProperty("lastName", "Doe");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("does not write separator when serializeSeparator=false")
        void doesNotWriteSeparatorWhenDisabled() {
            EObject eObject = mock(EObject.class);
            when(eObject.eClass()).thenReturn(multiIdClass);
            when(eObject.eGet(firstNameAttr)).thenReturn("John");
            when(eObject.eGet(lastNameAttr)).thenReturn("Doe");

            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .separator("-")
                    .serializeSeparator(false)
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdSerializationEntry entry = new IdSerializationEntry(config, multiIdClass);
            entry.serialize(createState(eObject), generator, null);

            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_id");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("firstName", "John");
            inOrder.verify(generator).writeStringProperty("lastName", "Doe");
            inOrder.verify(generator).writeEndObject();

            verify(generator, never()).writeStringProperty("separator", "-");
        }

        @Test
        @DisplayName("uses custom separator key")
        void usesCustomSeparatorKey() {
            EObject eObject = mock(EObject.class);
            when(eObject.eClass()).thenReturn(multiIdClass);
            when(eObject.eGet(firstNameAttr)).thenReturn("John");
            when(eObject.eGet(lastNameAttr)).thenReturn("Doe");

            IdConfig config = IdConfig.builder()
                    .key("_id")
                    .format(SerializationFormat.STRUCTURED)
                    .separator("-")
                    .serializeSeparator(true)
                    .separatorKey("sep")
                    .idFeatures(List.of("firstName", "lastName"))
                    .build();

            IdSerializationEntry entry = new IdSerializationEntry(config, multiIdClass);
            entry.serialize(createState(eObject), generator, null);

            verify(generator).writeStringProperty("sep", "-");
        }
    }
}
