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
package org.eclipse.fennec.codec.context;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;

/**
 * Tests for {@link ContextHelper}.
 */
@DisplayName("ContextHelper")
class ContextHelperTest {

    private DeserializationContext deserCtxt;
    private SerializationContext serCtxt;
    private EClass testEClass;

    @BeforeEach
    void setUp() {
        deserCtxt = mock(DeserializationContext.class);
        serCtxt = mock(SerializationContext.class);
        testEClass = EcoreFactory.eINSTANCE.createEClass();
        testEClass.setName("TestClass");
    }

    @Nested
    @DisplayName("Constants")
    class ConstantsTest {

        @Test
        @DisplayName("EXPECTED_TYPE constant is defined")
        void expectedTypeConstant() {
            assertEquals("CODEC_EXPECTED_TYPE", ContextHelper.EXPECTED_TYPE);
        }

        @Test
        @DisplayName("UNRESOLVED_REFERENCES constant is defined")
        void unresolvedReferencesConstant() {
            assertEquals("CODEC_UNRESOLVED_REFERENCES", ContextHelper.UNRESOLVED_REFERENCES);
        }
    }

    @Nested
    @DisplayName("getExpectedType(DeserializationContext)")
    class GetExpectedTypeDeserializationTest {

        @Test
        @DisplayName("returns null when attribute is not set")
        void returnsNullWhenNotSet() {
            when(deserCtxt.getAttribute(ContextHelper.EXPECTED_TYPE)).thenReturn(null);

            EClass result = ContextHelper.getExpectedType(deserCtxt);

            assertNull(result);
            verify(deserCtxt).getAttribute(ContextHelper.EXPECTED_TYPE);
        }

        @Test
        @DisplayName("returns EClass when attribute is EClass")
        void returnsEClassWhenSet() {
            when(deserCtxt.getAttribute(ContextHelper.EXPECTED_TYPE)).thenReturn(testEClass);

            EClass result = ContextHelper.getExpectedType(deserCtxt);

            assertSame(testEClass, result);
        }

        @Test
        @DisplayName("returns EcorePackage EClass")
        void returnsEcorePackageEClass() {
            EClass eObjectClass = EcorePackage.eINSTANCE.getEObject();
            when(deserCtxt.getAttribute(ContextHelper.EXPECTED_TYPE)).thenReturn(eObjectClass);

            EClass result = ContextHelper.getExpectedType(deserCtxt);

            assertSame(eObjectClass, result);
        }

        @Test
        @DisplayName("throws IllegalStateException when attribute is wrong type")
        void throwsExceptionWhenWrongType() {
            when(deserCtxt.getAttribute(ContextHelper.EXPECTED_TYPE)).thenReturn("NotAnEClass");

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> ContextHelper.getExpectedType(deserCtxt));

            assertTrue(exception.getMessage().contains("CODEC_EXPECTED_TYPE"));
            assertTrue(exception.getMessage().contains("EClass"));
            assertTrue(exception.getMessage().contains("String"));
        }
    }

    @Nested
    @DisplayName("getExpectedType(SerializationContext)")
    class GetExpectedTypeSerializationTest {

        @Test
        @DisplayName("returns null when attribute is not set")
        void returnsNullWhenNotSet() {
            when(serCtxt.getAttribute(ContextHelper.EXPECTED_TYPE)).thenReturn(null);

            EClass result = ContextHelper.getExpectedType(serCtxt);

            assertNull(result);
            verify(serCtxt).getAttribute(ContextHelper.EXPECTED_TYPE);
        }

        @Test
        @DisplayName("returns EClass when attribute is EClass")
        void returnsEClassWhenSet() {
            when(serCtxt.getAttribute(ContextHelper.EXPECTED_TYPE)).thenReturn(testEClass);

            EClass result = ContextHelper.getExpectedType(serCtxt);

            assertSame(testEClass, result);
        }

        @Test
        @DisplayName("throws IllegalStateException when attribute is wrong type")
        void throwsExceptionWhenWrongType() {
            when(serCtxt.getAttribute(ContextHelper.EXPECTED_TYPE)).thenReturn(42);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> ContextHelper.getExpectedType(serCtxt));

            assertTrue(exception.getMessage().contains("CODEC_EXPECTED_TYPE"));
            assertTrue(exception.getMessage().contains("EClass"));
            assertTrue(exception.getMessage().contains("Integer"));
        }
    }

    @Nested
    @DisplayName("setExpectedType(DeserializationContext, EClass)")
    class SetExpectedTypeDeserializationTest {

        @Test
        @DisplayName("sets attribute on context")
        void setsAttribute() {
            when(deserCtxt.setAttribute(any(), any())).thenReturn(deserCtxt);

            ContextHelper.setExpectedType(deserCtxt, testEClass);

            verify(deserCtxt).setAttribute(ContextHelper.EXPECTED_TYPE, testEClass);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when eClass is null")
        void throwsExceptionWhenNull() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ContextHelper.setExpectedType(deserCtxt, null));

            assertEquals("eClass must not be null", exception.getMessage());
            verify(deserCtxt, never()).setAttribute(any(), any());
        }
    }

    @Nested
    @DisplayName("setExpectedType(SerializationContext, EClass)")
    class SetExpectedTypeSerializationTest {

        @Test
        @DisplayName("sets attribute on context")
        void setsAttribute() {
            when(serCtxt.setAttribute(any(), any())).thenReturn(serCtxt);

            ContextHelper.setExpectedType(serCtxt, testEClass);

            verify(serCtxt).setAttribute(ContextHelper.EXPECTED_TYPE, testEClass);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when eClass is null")
        void throwsExceptionWhenNull() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ContextHelper.setExpectedType(serCtxt, null));

            assertEquals("eClass must not be null", exception.getMessage());
            verify(serCtxt, never()).setAttribute(any(), any());
        }
    }

    @Nested
    @DisplayName("clearExpectedType(DeserializationContext)")
    class ClearExpectedTypeDeserializationTest {

        @Test
        @DisplayName("sets attribute to null")
        void clearsAttribute() {
            when(deserCtxt.setAttribute(any(), any())).thenReturn(deserCtxt);

            ContextHelper.clearExpectedType(deserCtxt);

            verify(deserCtxt).setAttribute(ContextHelper.EXPECTED_TYPE, null);
        }
    }

    @Nested
    @DisplayName("clearExpectedType(SerializationContext)")
    class ClearExpectedTypeSerializationTest {

        @Test
        @DisplayName("sets attribute to null")
        void clearsAttribute() {
            when(serCtxt.setAttribute(any(), any())).thenReturn(serCtxt);

            ContextHelper.clearExpectedType(serCtxt);

            verify(serCtxt).setAttribute(ContextHelper.EXPECTED_TYPE, null);
        }
    }

    // ========================================================================
    // Diagnostic Collector Tests
    // ========================================================================

    @Nested
    @DisplayName("DiagnosticCollector constant")
    class DiagnosticCollectorConstantTest {

        @Test
        @DisplayName("DIAGNOSTIC_COLLECTOR constant is defined")
        void diagnosticCollectorConstant() {
            assertEquals("CODEC_DIAGNOSTIC_COLLECTOR", ContextHelper.DIAGNOSTIC_COLLECTOR);
        }
    }

    @Nested
    @DisplayName("getDiagnosticCollector(DeserializationContext)")
    class GetDiagnosticCollectorDeserializationTest {

        @Test
        @DisplayName("returns null when attribute is not set")
        void returnsNullWhenNotSet() {
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(null);

            DiagnosticCollector result = ContextHelper.getDiagnosticCollector(deserCtxt);

            assertNull(result);
        }

        @Test
        @DisplayName("returns DiagnosticCollector when attribute is set")
        void returnsDiagnosticCollectorWhenSet() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            DiagnosticCollector result = ContextHelper.getDiagnosticCollector(deserCtxt);

            assertSame(collector, result);
        }

        @Test
        @DisplayName("returns null when attribute is wrong type")
        void returnsNullWhenWrongType() {
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn("NotACollector");

            DiagnosticCollector result = ContextHelper.getDiagnosticCollector(deserCtxt);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("getDiagnosticCollector(SerializationContext)")
    class GetDiagnosticCollectorSerializationTest {

        @Test
        @DisplayName("returns null when attribute is not set")
        void returnsNullWhenNotSet() {
            when(serCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(null);

            DiagnosticCollector result = ContextHelper.getDiagnosticCollector(serCtxt);

            assertNull(result);
        }

        @Test
        @DisplayName("returns DiagnosticCollector when attribute is set")
        void returnsDiagnosticCollectorWhenSet() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(serCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            DiagnosticCollector result = ContextHelper.getDiagnosticCollector(serCtxt);

            assertSame(collector, result);
        }
    }

    @Nested
    @DisplayName("setDiagnosticCollector(DeserializationContext)")
    class SetDiagnosticCollectorDeserializationTest {

        @Test
        @DisplayName("sets attribute on context")
        void setsAttribute() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(deserCtxt.setAttribute(any(), any())).thenReturn(deserCtxt);

            ContextHelper.setDiagnosticCollector(deserCtxt, collector);

            verify(deserCtxt).setAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR, collector);
        }
    }

    @Nested
    @DisplayName("setDiagnosticCollector(SerializationContext)")
    class SetDiagnosticCollectorSerializationTest {

        @Test
        @DisplayName("sets attribute on context")
        void setsAttribute() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(serCtxt.setAttribute(any(), any())).thenReturn(serCtxt);

            ContextHelper.setDiagnosticCollector(serCtxt, collector);

            verify(serCtxt).setAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR, collector);
        }
    }

    @Nested
    @DisplayName("addWarning(DeserializationContext)")
    class AddWarningDeserializationTest {

        @Test
        @DisplayName("does nothing when context is null")
        void doesNothingWhenContextNull() {
            // Should not throw
            assertDoesNotThrow(() ->
                ContextHelper.addWarning((DeserializationContext) null, "message", "source"));
        }

        @Test
        @DisplayName("does nothing when collector is not set")
        void doesNothingWhenCollectorNotSet() {
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(null);

            // Should not throw
            assertDoesNotThrow(() ->
                ContextHelper.addWarning(deserCtxt, "message", "source"));
        }

        @Test
        @DisplayName("adds warning to collector")
        void addsWarningToCollector() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            ContextHelper.addWarning(deserCtxt, "Test warning", "TestSource");

            assertEquals(1, collector.getWarningCount());
            assertEquals("Test warning", collector.getWarnings().get(0).getMessage());
        }

        @Test
        @DisplayName("adds warning with parser location")
        void addsWarningWithParserLocation() {
            DiagnosticCollector collector = new DiagnosticCollector();
            JsonParser parser = mock(JsonParser.class);
            TokenStreamLocation location = mock(TokenStreamLocation.class);
            when(location.getLineNr()).thenReturn(10);
            when(location.getColumnNr()).thenReturn(5);
            when(parser.currentLocation()).thenReturn(location);
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            ContextHelper.addWarning(deserCtxt, "Test warning", parser, "TestSource");

            assertEquals(1, collector.getWarningCount());
            assertEquals(10, collector.getWarnings().get(0).getLine());
            assertEquals(5, collector.getWarnings().get(0).getColumn());
        }

        @Test
        @DisplayName("handles null parser gracefully")
        void handlesNullParser() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            assertDoesNotThrow(() ->
                ContextHelper.addWarning(deserCtxt, "Test warning", (JsonParser) null, "TestSource"));

            assertEquals(1, collector.getWarningCount());
        }
    }

    @Nested
    @DisplayName("addError(DeserializationContext)")
    class AddErrorDeserializationTest {

        @Test
        @DisplayName("does nothing when context is null")
        void doesNothingWhenContextNull() {
            assertDoesNotThrow(() ->
                ContextHelper.addError((DeserializationContext) null, "message", "source"));
        }

        @Test
        @DisplayName("does nothing when collector is not set")
        void doesNothingWhenCollectorNotSet() {
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(null);

            assertDoesNotThrow(() ->
                ContextHelper.addError(deserCtxt, "message", "source"));
        }

        @Test
        @DisplayName("adds error to collector")
        void addsErrorToCollector() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            ContextHelper.addError(deserCtxt, "Test error", "TestSource");

            assertEquals(1, collector.getErrorCount());
            assertEquals("Test error", collector.getErrors().get(0).getMessage());
        }

        @Test
        @DisplayName("adds error with parser location")
        void addsErrorWithParserLocation() {
            DiagnosticCollector collector = new DiagnosticCollector();
            JsonParser parser = mock(JsonParser.class);
            TokenStreamLocation location = mock(TokenStreamLocation.class);
            when(location.getLineNr()).thenReturn(20);
            when(location.getColumnNr()).thenReturn(15);
            when(parser.currentLocation()).thenReturn(location);
            when(deserCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            ContextHelper.addError(deserCtxt, "Test error", parser, "TestSource");

            assertEquals(1, collector.getErrorCount());
            assertEquals(20, collector.getErrors().get(0).getLine());
            assertEquals(15, collector.getErrors().get(0).getColumn());
        }
    }

    @Nested
    @DisplayName("addWarning(SerializationContext)")
    class AddWarningSerializationTest {

        @Test
        @DisplayName("does nothing when context is null")
        void doesNothingWhenContextNull() {
            assertDoesNotThrow(() ->
                ContextHelper.addWarning((SerializationContext) null, "message", "source"));
        }

        @Test
        @DisplayName("does nothing when collector is not set")
        void doesNothingWhenCollectorNotSet() {
            when(serCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(null);

            assertDoesNotThrow(() ->
                ContextHelper.addWarning(serCtxt, "message", "source"));
        }

        @Test
        @DisplayName("adds warning to collector")
        void addsWarningToCollector() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(serCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            ContextHelper.addWarning(serCtxt, "Serialization warning", "SerSource");

            assertEquals(1, collector.getWarningCount());
            assertEquals("Serialization warning", collector.getWarnings().get(0).getMessage());
        }
    }

    @Nested
    @DisplayName("addError(SerializationContext)")
    class AddErrorSerializationTest {

        @Test
        @DisplayName("does nothing when context is null")
        void doesNothingWhenContextNull() {
            assertDoesNotThrow(() ->
                ContextHelper.addError((SerializationContext) null, "message", "source"));
        }

        @Test
        @DisplayName("does nothing when collector is not set")
        void doesNothingWhenCollectorNotSet() {
            when(serCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(null);

            assertDoesNotThrow(() ->
                ContextHelper.addError(serCtxt, "message", "source"));
        }

        @Test
        @DisplayName("adds error to collector")
        void addsErrorToCollector() {
            DiagnosticCollector collector = new DiagnosticCollector();
            when(serCtxt.getAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR)).thenReturn(collector);

            ContextHelper.addError(serCtxt, "Serialization error", "SerSource");

            assertEquals(1, collector.getErrorCount());
            assertEquals("Serialization error", collector.getErrors().get(0).getMessage());
        }
    }

    @Nested
    @DisplayName("Resource methods")
    class ResourceTest {

        @Test
        @DisplayName("getResource returns null when not set")
        void getResourceNull() {
            when(deserCtxt.getAttribute(ContextHelper.RESOURCE)).thenReturn(null);
            assertNull(ContextHelper.getResource(deserCtxt));
        }

        @Test
        @DisplayName("getResource returns null for null context")
        void getResourceNullContext() {
            assertNull(ContextHelper.getResource((DeserializationContext) null));
        }

        @Test
        @DisplayName("getResource returns resource when set")
        void getResourceSet() {
            Resource resource =
                    mock(Resource.class);
            when(deserCtxt.getAttribute(ContextHelper.RESOURCE)).thenReturn(resource);
            assertSame(resource, ContextHelper.getResource(deserCtxt));
        }

        @Test
        @DisplayName("setResource stores resource in context")
        void setResource() {
            Resource resource =
                    mock(Resource.class);
            ContextHelper.setResource(deserCtxt, resource);
            verify(deserCtxt).setAttribute(ContextHelper.RESOURCE, resource);
        }

        @Test
        @DisplayName("setResource does nothing for null context")
        void setResourceNullContext() {
            Resource resource =
                    mock(Resource.class);
            assertDoesNotThrow(() -> ContextHelper.setResource((DeserializationContext) null, resource));
        }

        @Test
        @DisplayName("RESOURCE constant is defined")
        void resourceConstant() {
            assertEquals("CODEC_RESOURCE", ContextHelper.RESOURCE);
        }
    }
}
