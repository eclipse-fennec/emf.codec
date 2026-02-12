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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.deser.SuperTypeDeserializationEntry.SuperTypeValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;

/**
 * Tests for {@link SuperTypeDeserializationEntry}.
 *
 * @see <a href="docs/codec-v2-spec/06-supertype.md#8-deserialization">Spec: SuperType Deserialization</a>
 */
@DisplayName("SuperTypeDeserializationEntry")
class SuperTypeDeserializationEntryTest {

    private EPackage testPackage;
    private EClass baseClass;
    private EClass derivedClass;
    private EPackage externalPackage;
    private EClass externalClass;
    private DeserializationState state;
    private DeserializationContext ctxt;

    @BeforeEach
    void setUp() {
        // Create test package
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("testpackage");
        testPackage.setNsURI("http://example.org/test");
        testPackage.setNsPrefix("test");

        // Create base class
        baseClass = EcoreFactory.eINSTANCE.createEClass();
        baseClass.setName("Entity");
        testPackage.getEClassifiers().add(baseClass);

        // Create derived class
        derivedClass = EcoreFactory.eINSTANCE.createEClass();
        derivedClass.setName("Person");
        derivedClass.getESuperTypes().add(baseClass);
        testPackage.getEClassifiers().add(derivedClass);

        // Create external package
        externalPackage = EcoreFactory.eINSTANCE.createEPackage();
        externalPackage.setName("auditpackage");
        externalPackage.setNsURI("http://audit.org/1.0");
        externalPackage.setNsPrefix("audit");

        // Create external class
        externalClass = EcoreFactory.eINSTANCE.createEClass();
        externalClass.setName("Auditable");
        externalPackage.getEClassifiers().add(externalClass);

        // Setup mock state and context
        state = mock(DeserializationState.class);
        ctxt = mock(DeserializationContext.class);
    }

    @Test
    @DisplayName("getKey returns configured supertype key")
    void getKeyReturnsConfiguredKey() {
        SuperTypeConfig config = SuperTypeConfig.builder()
                .superTypeKey("_supertype")
                .build();

        SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);
        assertEquals("_supertype", entry.getKey());
    }

    @Test
    @DisplayName("getKey returns custom key")
    void getKeyReturnsCustomKey() {
        SuperTypeConfig config = SuperTypeConfig.builder()
                .superTypeKey("extends")
                .build();

        SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);
        assertEquals("extends", entry.getKey());
    }

    @Nested
    @DisplayName("Validation disabled (default)")
    class ValidationDisabledTests {

        @Test
        @DisplayName("ignores supertypes when validation disabled")
        void ignoresSuperTypesWhenValidationDisabled() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken())
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.END_ARRAY);
            when(parser.getString()).thenReturn("InvalidType");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, false);

            // Should not throw even with invalid supertype
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }
    }

    @Nested
    @DisplayName("Validation enabled")
    class ValidationEnabledTests {

        @Test
        @DisplayName("validates valid supertype (simple name)")
        void validatesValidSuperTypeSimpleName() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken())
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.END_ARRAY);
            when(parser.getString()).thenReturn("Entity");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw with valid supertype
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }

        @Test
        @DisplayName("validates valid supertype (full URI)")
        void validatesValidSuperTypeFullUri() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken())
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.END_ARRAY);
            when(parser.getString()).thenReturn("http://example.org/test#//Entity");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw with valid supertype URI
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }

        @Test
        @DisplayName("throws on invalid supertype")
        void throwsOnInvalidSuperType() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken())
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.END_ARRAY);
            when(parser.getString()).thenReturn("InvalidType");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            SuperTypeValidationException ex = assertThrows(
                    SuperTypeValidationException.class,
                    () -> entry.deserialize(state, parser, ctxt));

            assertEquals("Person", ex.getEClassName());
            assertTrue(ex.getInvalidSuperTypes().contains("InvalidType"));
        }

        @Test
        @DisplayName("validates multiple supertypes")
        void validatesMultipleSuperTypes() {
            // Add external supertype to derived class
            derivedClass.getESuperTypes().add(externalClass);

            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken())
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.END_ARRAY);
            when(parser.getString())
                    .thenReturn("Entity")
                    .thenReturn("Auditable");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw with both valid supertypes
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }

        @Test
        @DisplayName("throws when one of multiple supertypes is invalid")
        void throwsWhenOneOfMultipleSuperTypesInvalid() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken())
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.END_ARRAY);
            when(parser.getString())
                    .thenReturn("Entity")
                    .thenReturn("InvalidType");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            SuperTypeValidationException ex = assertThrows(
                    SuperTypeValidationException.class,
                    () -> entry.deserialize(state, parser, ctxt));

            assertTrue(ex.getInvalidSuperTypes().contains("InvalidType"));
        }

        @Test
        @DisplayName("validates STRING presentation")
        void validatesStringPresentation() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .separator(",")
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.VALUE_STRING);
            when(parser.getString()).thenReturn("Entity");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw with valid supertype in STRING format
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }

        @Test
        @DisplayName("validates STRING presentation with multiple values")
        void validatesStringPresentationMultiple() {
            // Add external supertype to derived class
            derivedClass.getESuperTypes().add(externalClass);

            SuperTypeConfig config = SuperTypeConfig.builder()
                    .separator(",")
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.VALUE_STRING);
            when(parser.getString()).thenReturn("Entity,Auditable");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw with valid supertypes
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }

        @Test
        @DisplayName("validates STRING presentation with custom separator")
        void validatesStringPresentationCustomSeparator() {
            // Add external supertype to derived class
            derivedClass.getESuperTypes().add(externalClass);

            SuperTypeConfig config = SuperTypeConfig.builder()
                    .separator("|")
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.VALUE_STRING);
            when(parser.getString()).thenReturn("Entity|Auditable");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw with valid supertypes using custom separator
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }

        @Test
        @DisplayName("allows empty supertypes")
        void allowsEmptySuperTypes() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken()).thenReturn(JsonToken.END_ARRAY);

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw with empty supertypes
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }

        @Test
        @DisplayName("allows subset of actual supertypes")
        void allowsSubsetOfActualSuperTypes() {
            // Create a deeper hierarchy: Person extends Entity extends BaseObject
            EClass baseObject = EcoreFactory.eINSTANCE.createEClass();
            baseObject.setName("BaseObject");
            testPackage.getEClassifiers().add(baseObject);
            baseClass.getESuperTypes().add(baseObject);

            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken())
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.END_ARRAY);
            // Only declare Entity, not BaseObject
            when(parser.getString()).thenReturn("Entity");

            when(state.getResolvedEClass()).thenReturn(derivedClass);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw - subset is acceptable
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }

        @Test
        @DisplayName("skips validation when no resolved EClass")
        void skipsValidationWhenNoResolvedEClass() {
            SuperTypeConfig config = SuperTypeConfig.builder()
                    .build();

            JsonParser parser = mock(JsonParser.class);
            when(parser.currentToken()).thenReturn(JsonToken.START_ARRAY);
            when(parser.nextToken())
                    .thenReturn(JsonToken.VALUE_STRING)
                    .thenReturn(JsonToken.END_ARRAY);
            when(parser.getString()).thenReturn("InvalidType");

            when(state.getResolvedEClass()).thenReturn(null);

            SuperTypeDeserializationEntry entry = new SuperTypeDeserializationEntry(config, true);

            // Should not throw when EClass not resolved yet
            assertDoesNotThrow(() -> entry.deserialize(state, parser, ctxt));
        }
    }
}
