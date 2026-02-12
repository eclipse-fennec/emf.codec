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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.deser.SuperTypeDeserializationEntry.SuperTypeValidationException;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;

/**
 * Tests for {@link TypeDeserializationEntry}.
 * <p>
 * Tests the Type deserialization for both PLAIN and STRUCTURED formats:
 * <ul>
 *   <li>PLAIN format: URI, NAME, CLASS, MAPPED, NUMERIC strategies</li>
 *   <li>STRUCTURED format: All strategies with unified "type" key</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/05-type.md">Spec: Type Serialization</a>
 */
@DisplayName("TypeDeserializationEntry")
class TypeDeserializationEntryTest extends DeserializationEntryTestBase {

    @BeforeEach
    void registerPackage() {
        // Register the test package in the global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
    }

    @AfterEach
    void unregisterPackage() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
    }

    // ========================================================================
    // PLAIN Format Tests
    // ========================================================================

    @Nested
    @DisplayName("PLAIN format")
    class PlainFormatTests {

        @Test
        @DisplayName("URI strategy: deserializes full EClass URI")
        void uriStrategy_deserializesFullUri() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.URI)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            assertEquals("_type", entry.getKey());

            DeserializationState state = createState(null);

            // Person class URI
            String uri = testPackage.getNsURI() + "#//Person";
            try (JsonParser parser = createParser("\"" + uri + "\"")) {
                entry.deserialize(state, parser, null);
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("NAME strategy: deserializes simple class name")
        void nameStrategy_deserializesSimpleName() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.NAME)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            try (JsonParser parser = createParser("\"Person\"")) {
                entry.deserialize(state, parser, null);
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("NUMERIC strategy: deserializes classifier ID as string with hint")
        void numericStrategy_deserializesClassifierId() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.NUMERIC)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // PLAIN NUMERIC: "_type": "N" where N is the classifier ID
            // Note: NUMERIC strategy requires a hint EClass to determine the package,
            // since classifier IDs are only unique within a package.
            int classifierId = personClass.getClassifierID();
            try (JsonParser parser = createParser("\"" + classifierId + "\"")) {
                // Use personClass as hint to provide package context
                entry.deserializeWithHint(state, parser, null, personClass);
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("NUMERIC strategy: uses hint package for disambiguation")
        void numericStrategy_usesHintPackageForDisambiguation() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.NUMERIC)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // Use Address class's classifier ID with Person as hint (same package)
            int addressClassifierId = addressClass.getClassifierID();
            try (JsonParser parser = createParser("\"" + addressClassifierId + "\"")) {
                // With personClass as hint, lookup should use its package
                entry.deserializeWithHint(state, parser, null, personClass);
                assertEquals(addressClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("NUMERIC strategy: handles invalid classifier ID gracefully")
        void numericStrategy_handlesInvalidClassifierId() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.NUMERIC)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // Invalid: non-numeric value
            try (JsonParser parser = createParser("\"not-a-number\"")) {
                entry.deserialize(state, parser, null);
                assertNull(state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("NUMERIC strategy: handles non-existent classifier ID gracefully")
        void numericStrategy_handlesNonExistentClassifierId() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.NUMERIC)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // Non-existent classifier ID (very high number)
            try (JsonParser parser = createParser("\"99999\"")) {
                entry.deserialize(state, parser, null);
                assertNull(state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("getKey returns configured type key")
        void getKeyReturnsConfiguredTypeKey() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .typeKey("@class")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            assertEquals("@class", entry.getKey());
        }

        @Test
        @DisplayName("handles unknown type gracefully")
        void handlesUnknownTypeGracefully() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.NAME)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            try (JsonParser parser = createParser("\"NonExistentClass\"")) {
                entry.deserialize(state, parser, null);
                assertNull(state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("SCHEMA_AND_TYPE strategy: deserializes with separate schema hint")
        void schemaAndTypeStrategy_deserializesWithSchemaHint() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // PLAIN SCHEMA_AND_TYPE: schema provided separately, type is simple name
            try (JsonParser parser = createParser("\"Person\"")) {
                entry.deserializeWithSchemaHint(state, parser, null, null, testPackage.getNsURI());
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("SCHEMA_AND_TYPE strategy: schema hint ignored for full URI")
        void schemaAndTypeStrategy_schemaHintIgnoredForFullUri() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // If type is already a full URI, schema hint should be ignored
            String fullUri = testPackage.getNsURI() + "#//Person";
            try (JsonParser parser = createParser("\"" + fullUri + "\"")) {
                entry.deserializeWithSchemaHint(state, parser, null, null, "http://other.schema/1.0");
                assertEquals(personClass, state.getResolvedEClass());
            }
        }
    }

    // ========================================================================
    // STRUCTURED Format Tests
    // ========================================================================

    @Nested
    @DisplayName("STRUCTURED format")
    class StructuredFormatTests {

        @Test
        @DisplayName("URI strategy: deserializes nested object with type key containing URI")
        void uriStrategy_deserializesStructuredUri() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.URI)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // STRUCTURED URI: {"type": "http://...#//Person"}
            String uri = testPackage.getNsURI() + "#//Person";
            String json = "{\"type\": \"" + uri + "\"}";
            try (JsonParser parser = createParser(json)) {
                entry.deserialize(state, parser, null);
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("NAME strategy: deserializes nested object with type key containing simple name")
        void nameStrategy_deserializesStructuredName() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.NAME)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // STRUCTURED NAME: {"type": "Person"}
            String json = "{\"type\": \"Person\"}";
            try (JsonParser parser = createParser(json)) {
                entry.deserialize(state, parser, null);
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("SCHEMA_AND_TYPE strategy: deserializes nested object with schema and type")
        void schemaAndTypeStrategy_deserializesStructured() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // STRUCTURED SCHEMA_AND_TYPE: {"schema": "...", "type": "Person"}
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Person\"}";
            try (JsonParser parser = createParser(json)) {
                entry.deserialize(state, parser, null);
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("NUMERIC strategy: deserializes nested object with schema and classifier")
        void numericStrategy_deserializesStructuredClassifier() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.NUMERIC)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // STRUCTURED NUMERIC: {"schema": "...", "classifier": N}
            int classifierId = personClass.getClassifierID();
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"classifier\": " + classifierId + "}";
            try (JsonParser parser = createParser(json)) {
                entry.deserialize(state, parser, null);
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("uses custom keys for structured parsing")
        void usesCustomKeysForStructuredParsing() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("@context")
                    .schemaKey("@vocab")
                    .nameKey("@type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            assertEquals("@context", entry.getKey());

            DeserializationState state = createState(null);

            // Custom keys: {"@vocab": "...", "@type": "Person"}
            String json = "{\"@vocab\": \"" + testPackage.getNsURI() + "\", \"@type\": \"Person\"}";
            try (JsonParser parser = createParser(json)) {
                entry.deserialize(state, parser, null);
                assertEquals(personClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("handles unknown structured type gracefully")
        void handlesUnknownStructuredTypeGracefully() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // Unknown class in structured format
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"NonExistentClass\"}";
            try (JsonParser parser = createParser(json)) {
                entry.deserialize(state, parser, null);
                assertNull(state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("handles empty structured object gracefully")
        void handlesEmptyStructuredObjectGracefully() {
            TypeConfig config = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(config);
            DeserializationState state = createState(null);

            // Empty object
            String json = "{}";
            try (JsonParser parser = createParser(json)) {
                entry.deserialize(state, parser, null);
                assertNull(state.getResolvedEClass());
            }
        }
    }

    // ========================================================================
    // STRUCTURED Format with SuperType Tests
    // ========================================================================

    @Nested
    @DisplayName("STRUCTURED format with supertype")
    class StructuredFormatWithSuperTypeTests {

        private EClass baseClass;
        private EClass derivedClass;
        private EPackage externalPackage;
        private EClass externalClass;

        @BeforeEach
        void setUpHierarchy() {
            // Create base class
            baseClass = EcoreFactory.eINSTANCE.createEClass();
            baseClass.setName("Entity");
            testPackage.getEClassifiers().add(baseClass);

            // Create derived class that extends Person and Entity
            derivedClass = EcoreFactory.eINSTANCE.createEClass();
            derivedClass.setName("Employee");
            derivedClass.getESuperTypes().add(personClass);
            derivedClass.getESuperTypes().add(baseClass);
            testPackage.getEClassifiers().add(derivedClass);

            // Create external package and class
            externalPackage = EcoreFactory.eINSTANCE.createEPackage();
            externalPackage.setName("auditpackage");
            externalPackage.setNsURI("http://audit.org/1.0");
            externalPackage.setNsPrefix("audit");

            externalClass = EcoreFactory.eINSTANCE.createEClass();
            externalClass.setName("Auditable");
            externalPackage.getEClassifiers().add(externalClass);

            // Register external package
            EPackage.Registry.INSTANCE.put(externalPackage.getNsURI(), externalPackage);
        }

        @AfterEach
        void cleanUpHierarchy() {
            EPackage.Registry.INSTANCE.remove(externalPackage.getNsURI());
            testPackage.getEClassifiers().remove(baseClass);
            testPackage.getEClassifiers().remove(derivedClass);
        }

        @Test
        @DisplayName("parses supertype array in STRUCTURED format (validation disabled)")
        void parsesSuperTypeArrayValidationDisabled() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .superTypeKey("_supertype")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(
                    typeConfig, null, superTypeConfig);
            DeserializationState state = createState(null);

            // STRUCTURED with supertype array: {"schema": "...", "type": "Employee", "supertype": ["Person", "Entity"]}
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Employee\", \"supertype\": [\"Person\", \"Entity\"]}";
            try (JsonParser parser = createParser(json)) {
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
                assertEquals(derivedClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("validates valid supertype hierarchy in STRUCTURED format")
        void validatesValidSuperTypeHierarchy() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .superTypeKey("_supertype")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(
                    typeConfig, null, superTypeConfig);
            DeserializationState state = createState(null);

            // Valid supertypes
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Employee\", \"supertype\": [\"Person\", \"Entity\"]}";
            try (JsonParser parser = createParser(json)) {
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
                assertEquals(derivedClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("throws on invalid supertype in STRUCTURED format when validation enabled")
        void throwsOnInvalidSuperTypeWhenValidationEnabled() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .superTypeKey("_supertype")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(
                    typeConfig, null, superTypeConfig);
            DeserializationState state = createState(null);

            // Invalid supertype "NonExistent" not in hierarchy
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Employee\", \"supertype\": [\"Person\", \"NonExistent\"]}";
            try (JsonParser parser = createParser(json)) {
                SuperTypeValidationException ex = assertThrows(
                        SuperTypeValidationException.class,
                        () -> entry.deserialize(state, parser, null));
                assertTrue(ex.getInvalidSuperTypes().contains("NonExistent"));
            }
        }

        @Test
        @DisplayName("ignores invalid supertype in STRUCTURED format when validation disabled")
        void ignoresInvalidSuperTypeWhenValidationDisabled() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            // Pass null superTypeConfig to disable validation
            TypeDeserializationEntry entry = new TypeDeserializationEntry(
                    typeConfig, null, null);
            DeserializationState state = createState(null);

            // Invalid supertype but validation disabled (superTypeConfig is null)
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Employee\", \"supertype\": [\"NonExistent\"]}";
            try (JsonParser parser = createParser(json)) {
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
                assertEquals(derivedClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("parses supertype STRING presentation in STRUCTURED format")
        void parsesSuperTypeStringPresentation() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .superTypeKey("_supertype")
                    .separator(",")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(
                    typeConfig, null, superTypeConfig);
            DeserializationState state = createState(null);

            // STRING presentation: "Person,Entity"
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Employee\", \"supertype\": \"Person,Entity\"}";
            try (JsonParser parser = createParser(json)) {
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
                assertEquals(derivedClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("validates full URI supertypes in STRUCTURED format")
        void validatesFullUriSuperTypes() {
            // Add external class as supertype
            derivedClass.getESuperTypes().add(externalClass);

            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .superTypeKey("_supertype")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(
                    typeConfig, null, superTypeConfig);
            DeserializationState state = createState(null);

            // Mix of simple names and full URIs
            String externalUri = externalPackage.getNsURI() + "#//Auditable";
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Employee\", \"supertype\": [\"Person\", \"" + externalUri + "\"]}";
            try (JsonParser parser = createParser(json)) {
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
                assertEquals(derivedClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("handles empty supertype array in STRUCTURED format")
        void handlesEmptySuperTypeArray() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .superTypeKey("_supertype")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(
                    typeConfig, null, superTypeConfig);
            DeserializationState state = createState(null);

            // Empty supertype array
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Employee\", \"supertype\": []}";
            try (JsonParser parser = createParser(json)) {
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
                assertEquals(derivedClass, state.getResolvedEClass());
            }
        }

        @Test
        @DisplayName("handles missing supertype field in STRUCTURED format")
        void handlesMissingSuperTypeField() {
            TypeConfig typeConfig = TypeConfig.builder()
                    .include(true)
                    .strategy(TypeStrategy.SCHEMA_AND_TYPE)
                    .typeKey("_type")
                    .schemaKey("schema")
                    .nameKey("type")
                    .build();

            SuperTypeConfig superTypeConfig = SuperTypeConfig.builder()
                    .superTypeKey("_supertype")
                    .build();

            TypeDeserializationEntry entry = new TypeDeserializationEntry(
                    typeConfig, null, superTypeConfig);
            DeserializationState state = createState(null);

            // No supertype field at all
            String json = "{\"schema\": \"" + testPackage.getNsURI() + "\", \"type\": \"Employee\"}";
            try (JsonParser parser = createParser(json)) {
                assertDoesNotThrow(() -> entry.deserialize(state, parser, null));
                assertEquals(derivedClass, state.getResolvedEClass());
            }
        }
    }
}
