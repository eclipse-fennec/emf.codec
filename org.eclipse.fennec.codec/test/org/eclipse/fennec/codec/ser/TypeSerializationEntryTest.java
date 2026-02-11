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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.SuperTypeSelection;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import tools.jackson.core.JsonGenerator;

/**
 * Tests for {@link TypeSerializationEntry}.
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#61-type-serialization">Spec 6.1: Type Serialization</a>
 */
@DisplayName("TypeSerializationEntry")
class TypeSerializationEntryTest {

    private EClass testEClass;
    private EPackage testPackage;
    private JsonGenerator generator;

    private SerializationState createState(EObject eObject) {
        return new SerializationState(eObject);
    }

    @BeforeEach
    void setUp() {
        generator = mock(JsonGenerator.class);

        // Create a test EPackage and EClass
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("testpackage");
        testPackage.setNsURI("http://example.org/test");
        testPackage.setNsPrefix("test");

        testEClass = EcoreFactory.eINSTANCE.createEClass();
        testEClass.setName("Person");
        testPackage.getEClassifiers().add(testEClass);
    }

    private TypeConfig createDefaultConfig() {
        return TypeConfig.builder()
                .include(true)
                .typeKey("_type")
                .build();
    }

    @Test
    @DisplayName("getKey returns default _type key")
    void getKeyReturnsDefaultTypeKey() {
        TypeConfig config = createDefaultConfig();
        TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
        assertEquals("_type", entry.getKey());
    }

    @Test
    @DisplayName("getKey returns custom key from config")
    void getKeyReturnsCustomKeyFromConfig() {
        TypeConfig config = TypeConfig.builder()
                .include(true)
                .typeKey("@class")
                .build();

        TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
        assertEquals("@class", entry.getKey());
    }

    @Test
    @DisplayName("shouldSerialize returns true when enabled")
    void shouldSerializeReturnsTrueWhenEnabled() {
        TypeConfig config = createDefaultConfig();
        TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
        EObject eObject = mock(EObject.class);

        assertTrue(entry.shouldSerialize(createState(eObject)));
    }

    @Test
    @DisplayName("shouldSerialize returns false when not enabled")
    void shouldSerializeReturnsFalseWhenNotEnabled() {
        TypeConfig config = TypeConfig.builder()
                .include(false)
                .typeKey("_type")
                .build();

        TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
        EObject eObject = mock(EObject.class);

        assertFalse(entry.shouldSerialize(createState(eObject)));
    }

    @Test
    @DisplayName("serialize writes EClass URI")
    void serializeWritesEClassUri() {
        TypeConfig config = createDefaultConfig();
        TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
        EObject eObject = mock(EObject.class);

        entry.serialize(createState(eObject), generator, null);

        verify(generator).writeStringProperty("_type", "http://example.org/test#//Person");
    }

    @Test
    @DisplayName("serialize writes discriminator value from config")
    void serializeWritesDiscriminatorValueFromConfig() {
        TypeConfig config = TypeConfig.builder()
                .include(true)
                .typeKey("_type")
                .discriminatorValue("person")
                .build();

        TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
        EObject eObject = mock(EObject.class);

        entry.serialize(createState(eObject), generator, null);

        verify(generator).writeStringProperty("_type", "person");
    }

    @Test
    @DisplayName("serialize uses EClass URI when discriminator is empty")
    void serializeUsesEClassUriWhenDiscriminatorEmpty() {
        TypeConfig config = TypeConfig.builder()
                .include(true)
                .typeKey("_type")
                .discriminatorValue("")
                .build();

        TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
        EObject eObject = mock(EObject.class);

        entry.serialize(createState(eObject), generator, null);

        verify(generator).writeStringProperty("_type", "http://example.org/test#//Person");
    }

    // ========================================================================
    // PLAIN SCHEMA_AND_TYPE Format Tests
    // ========================================================================

    @Nested
    @DisplayName("PLAIN SCHEMA_AND_TYPE format")
    class PlainSchemaAndTypeTests {

        @Test
        @DisplayName("SCHEMA_AND_TYPE strategy: writes two separate fields (_schema and _type)")
        void schemaAndTypeStrategy_writesTwoFields() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.PLAIN)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")  // Will be prefixed with _ for PLAIN
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify TWO properties are written: _schema and _type
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeStringProperty("_schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("_type", "Person");
        }

        @Test
        @DisplayName("SCHEMA_AND_TYPE strategy: uses custom schema key with prefix")
        void schemaAndTypeStrategy_usesCustomSchemaKeyWithPrefix() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.PLAIN)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("@type")
                    .schemaKey("@vocab")  // Already has @ prefix, won't add _
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Custom keys should be used as-is
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeStringProperty("@vocab", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("@type", "Person");
        }

        @Test
        @DisplayName("SCHEMA_AND_TYPE strategy: already-prefixed schema key preserved")
        void schemaAndTypeStrategy_preservesAlreadyPrefixedSchemaKey() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.PLAIN)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("_schema")  // Already has _ prefix
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeStringProperty("_schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("_type", "Person");
        }
    }

    // ========================================================================
    // STRUCTURED Format Tests
    // ========================================================================

    @Nested
    @DisplayName("STRUCTURED format")
    class StructuredFormatTests {

        @Test
        @DisplayName("SCHEMA_AND_TYPE strategy: serializes type as nested object with schema and type")
        void schemaAndTypeStrategy_serializesTypeAsNestedObject() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"schema": "http://example.org/test", "type": "Person"}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("type", "Person");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("URI strategy: serializes type as nested object with type key containing full URI")
        void uriStrategy_serializesTypeWithUri() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.URI)
                    .typeKey("_type")
                    .nameKey("type")
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"type": "http://example.org/test#//Person"}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("type", "http://example.org/test#//Person");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("NAME strategy: serializes type as nested object with simple class name")
        void nameStrategy_serializesTypeWithName() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.NAME)
                    .typeKey("_type")
                    .nameKey("type")
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"type": "Person"}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("type", "Person");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("NUMERIC strategy: serializes type with schema and classifier ID")
        void numericStrategy_serializesTypeWithClassifierId() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.NUMERIC)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"schema": "http://example.org/test", "classifier": N}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("schema", "http://example.org/test");
            inOrder.verify(generator).writeNumberProperty("classifier", testEClass.getClassifierID());
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("SCHEMA_AND_TYPE strategy: uses custom keys for structured output")
        void schemaAndTypeStrategy_usesCustomKeysForStructuredOutput() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("@context")
                    .schemaKey("@vocab")
                    .nameKey("@type")
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("@context");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("@vocab", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("@type", "Person");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("URI strategy: uses custom type key for structured output")
        void uriStrategy_usesCustomTypeKey() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.URI)
                    .typeKey("@context")
                    .nameKey("@type")
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify custom keys are used: {"@type": "http://example.org/test#//Person"}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("@context");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("@type", "http://example.org/test#//Person");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("getKey returns root type key for STRUCTURED format")
        void getKeyReturnsRootTypeKeyForStructured() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .typeKey("_type")
                    .build();

            TypeSerializationEntry entry = new TypeSerializationEntry(config, testEClass);
            assertEquals("_type", entry.getKey());
        }
    }

    // ========================================================================
    // STRUCTURED Format with SuperType Tests
    // ========================================================================

    @Nested
    @DisplayName("STRUCTURED format with SuperType")
    class StructuredFormatWithSuperTypeTests {

        private EClass superClass;
        private EClass testClassWithSuper;
        private EPackage anotherPackage;
        private EClass externalSuperClass;

        @BeforeEach
        void setUpInheritance() {
            // Create a superclass in the same package
            superClass = EcoreFactory.eINSTANCE.createEClass();
            superClass.setName("Entity");
            testPackage.getEClassifiers().add(superClass);

            // Create test class that extends superClass
            testClassWithSuper = EcoreFactory.eINSTANCE.createEClass();
            testClassWithSuper.setName("Customer");
            testClassWithSuper.getESuperTypes().add(superClass);
            testPackage.getEClassifiers().add(testClassWithSuper);

            // Create an external package with another superclass
            anotherPackage = EcoreFactory.eINSTANCE.createEPackage();
            anotherPackage.setName("auditpackage");
            anotherPackage.setNsURI("http://audit.org/1.0");
            anotherPackage.setNsPrefix("audit");

            externalSuperClass = EcoreFactory.eINSTANCE.createEClass();
            externalSuperClass.setName("Auditable");
            anotherPackage.getEClassifiers().add(externalSuperClass);
        }

        @Test
        @DisplayName("STRUCTURED with SuperType ARRAY: includes supertype array inside _type object")
        void structuredWithSuperTypeArray() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.ALL)
                    .asArray(true)
                    .superTypeKey("supertype")
                    .build();

            SuperTypeSerializationEntry superTypeEntry = new SuperTypeSerializationEntry(superTypeConfig, testClassWithSuper, true);
            TypeSerializationEntry entry = new TypeSerializationEntry(typeConfig, testClassWithSuper, superTypeEntry);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"schema": "...", "type": "Customer", "supertype": ["Entity"]}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("type", "Customer");
            inOrder.verify(generator).writeArrayPropertyStart("supertype");
            inOrder.verify(generator).writeString("Entity");  // Same namespace, smart compression
            inOrder.verify(generator).writeEndArray();
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("STRUCTURED with SuperType STRING: includes supertype string inside _type object")
        void structuredWithSuperTypeString() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.ALL)
                    .asArray(false)
                    .separator(",")
                    .superTypeKey("supertype")
                    .build();

            SuperTypeSerializationEntry superTypeEntry = new SuperTypeSerializationEntry(superTypeConfig, testClassWithSuper, true);
            TypeSerializationEntry entry = new TypeSerializationEntry(typeConfig, testClassWithSuper, superTypeEntry);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"schema": "...", "type": "Customer", "supertype": "Entity"}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("type", "Customer");
            inOrder.verify(generator).writeStringProperty("supertype", "Entity");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("STRUCTURED with SuperType: multiple supertypes with different namespaces")
        void structuredWithMultipleSuperTypes() {
            // Add external supertype
            testClassWithSuper.getESuperTypes().add(externalSuperClass);

            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.ALL)
                    .asArray(true)
                    .superTypeKey("supertype")
                    .build();

            SuperTypeSerializationEntry superTypeEntry = new SuperTypeSerializationEntry(superTypeConfig, testClassWithSuper, true);
            TypeSerializationEntry entry = new TypeSerializationEntry(typeConfig, testClassWithSuper, superTypeEntry);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"schema": "...", "type": "Customer", "supertype": ["Entity", "http://audit.org/1.0#//Auditable"]}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("type", "Customer");
            inOrder.verify(generator).writeArrayPropertyStart("supertype");
            inOrder.verify(generator).writeString("Entity");  // Same namespace
            inOrder.verify(generator).writeString("http://audit.org/1.0#//Auditable");  // Different namespace
            inOrder.verify(generator).writeEndArray();
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("STRUCTURED with no supertypes: does not write supertype field")
        void structuredWithNoSuperTypes() {
            // testEClass has no supertypes
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.ALL)
                    .asArray(true)
                    .superTypeKey("supertype")
                    .build();

            SuperTypeSerializationEntry superTypeEntry = new SuperTypeSerializationEntry(superTypeConfig, testEClass);
            TypeSerializationEntry entry = new TypeSerializationEntry(typeConfig, testEClass, superTypeEntry);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"schema": "...", "type": "Person"} - no supertype field
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("type", "Person");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("STRUCTURED with null superTypeEntry: no supertype field")
        void structuredWithNullSuperTypeEntry() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            // No superTypeEntry passed
            TypeSerializationEntry entry = new TypeSerializationEntry(typeConfig, testClassWithSuper);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"schema": "...", "type": "Customer"} - no supertype field
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("type", "Customer");
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("STRUCTURED URI strategy with SuperType: includes supertype")
        void structuredUriWithSuperType() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.URI)
                    .typeKey("_type")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.SINGLE)
                    .asArray(true)
                    .superTypeKey("supertype")
                    .build();

            SuperTypeSerializationEntry superTypeEntry = new SuperTypeSerializationEntry(superTypeConfig, testClassWithSuper, true);
            TypeSerializationEntry entry = new TypeSerializationEntry(typeConfig, testClassWithSuper, superTypeEntry);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify: {"type": "http://example.org/test#//Customer", "supertype": ["Entity"]}
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("type", "http://example.org/test#//Customer");
            inOrder.verify(generator).writeArrayPropertyStart("supertype");
            inOrder.verify(generator).writeString("Entity");
            inOrder.verify(generator).writeEndArray();
            inOrder.verify(generator).writeEndObject();
        }

        @Test
        @DisplayName("STRUCTURED with custom supertype key")
        void structuredWithCustomSuperTypeKey() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .format(SerializationFormat.STRUCTURED)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .serialize(true)
                    .strategy(SuperTypeSelection.ALL)
                    .asArray(true)
                    .superTypeKey("extends")
                    .build();

            SuperTypeSerializationEntry superTypeEntry = new SuperTypeSerializationEntry(superTypeConfig, testClassWithSuper, true);
            TypeSerializationEntry entry = new TypeSerializationEntry(typeConfig, testClassWithSuper, superTypeEntry);
            EObject eObject = mock(EObject.class);

            entry.serialize(createState(eObject), generator, null);

            // Verify custom key "extends" is used
            InOrder inOrder = inOrder(generator);
            inOrder.verify(generator).writeName("_type");
            inOrder.verify(generator).writeStartObject();
            inOrder.verify(generator).writeStringProperty("schema", "http://example.org/test");
            inOrder.verify(generator).writeStringProperty("type", "Customer");
            inOrder.verify(generator).writeArrayPropertyStart("extends");
            inOrder.verify(generator).writeString("Entity");
            inOrder.verify(generator).writeEndArray();
            inOrder.verify(generator).writeEndObject();
        }
    }
}
