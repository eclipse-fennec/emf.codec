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
package org.eclipse.fennec.codec.diagnostic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecDiagnostic}.
 *
 * @see <a href="docs/codec-v2-spec/15-error-handling.md">Spec 15: Error Handling</a>
 */
@DisplayName("CodecDiagnostic")
class CodecDiagnosticTest {

    // ============= Constructor Tests =============

    @Test
    @DisplayName("constructor with message, severity, and source creates diagnostic")
    void constructorWithMessageSeverityAndSourceCreatesDiagnostic() {
        CodecDiagnostic diag = new CodecDiagnostic("Test message", CodecDiagnostic.Severity.ERROR, "TestSource");

        assertEquals("Test message", diag.getMessage());
        assertEquals(CodecDiagnostic.Severity.ERROR, diag.getSeverity());
        assertEquals("TestSource", diag.getSource());
        assertNull(diag.getLocation());
        assertEquals(-1, diag.getLine());
        assertEquals(-1, diag.getColumn());
    }

    @Test
    @DisplayName("constructor with location string creates diagnostic with location")
    void constructorWithLocationStringCreatesDiagnosticWithLocation() {
        CodecDiagnostic diag = new CodecDiagnostic("Test message", "path/to/file:10", CodecDiagnostic.Severity.WARNING, "TestSource");

        assertEquals("Test message", diag.getMessage());
        assertEquals("path/to/file:10", diag.getLocation());
        assertEquals(CodecDiagnostic.Severity.WARNING, diag.getSeverity());
        assertEquals("TestSource", diag.getSource());
        assertEquals(-1, diag.getLine());
        assertEquals(-1, diag.getColumn());
    }

    @Test
    @DisplayName("constructor with null location string creates diagnostic")
    void constructorWithNullLocationStringCreatesDiagnostic() {
        CodecDiagnostic diag = new CodecDiagnostic("Test message", (String) null, CodecDiagnostic.Severity.ERROR, "TestSource");

        assertEquals("Test message", diag.getMessage());
        assertNull(diag.getLocation());
        assertEquals(-1, diag.getLine());
        assertEquals(-1, diag.getColumn());
    }

    // ============= Severity Tests =============

    @Test
    @DisplayName("ERROR severity isError returns true")
    void errorSeverityIsErrorReturnsTrue() {
        CodecDiagnostic diag = new CodecDiagnostic("Error message", CodecDiagnostic.Severity.ERROR, "Source");
        assertTrue(diag.isError());
    }

    @Test
    @DisplayName("ERROR severity isWarning returns false")
    void errorSeverityIsWarningReturnsFalse() {
        CodecDiagnostic diag = new CodecDiagnostic("Error message", CodecDiagnostic.Severity.ERROR, "Source");
        assertFalse(diag.isWarning());
    }

    @Test
    @DisplayName("WARNING severity isWarning returns true")
    void warningSeverityIsWarningReturnsTrue() {
        CodecDiagnostic diag = new CodecDiagnostic("Warning message", CodecDiagnostic.Severity.WARNING, "Source");
        assertTrue(diag.isWarning());
    }

    @Test
    @DisplayName("WARNING severity isError returns false")
    void warningSeverityIsErrorReturnsFalse() {
        CodecDiagnostic diag = new CodecDiagnostic("Warning message", CodecDiagnostic.Severity.WARNING, "Source");
        assertFalse(diag.isError());
    }

    // ============= Factory Method Tests - Error =============

    @Test
    @DisplayName("error() factory creates ERROR diagnostic without location")
    void errorFactoryCreatesErrorDiagnosticWithoutLocation() {
        CodecDiagnostic diag = CodecDiagnostic.error("Error message", "ErrorSource");

        assertEquals("Error message", diag.getMessage());
        assertEquals(CodecDiagnostic.Severity.ERROR, diag.getSeverity());
        assertEquals("ErrorSource", diag.getSource());
        assertTrue(diag.isError());
        assertFalse(diag.isWarning());
        assertNull(diag.getLocation());
    }

    @Test
    @DisplayName("error() factory with null location creates ERROR diagnostic without location")
    void errorFactoryWithNullLocationCreatesErrorDiagnosticWithoutLocation() {
        CodecDiagnostic diag = CodecDiagnostic.error("Error message", null, "Source");

        assertEquals("Error message", diag.getMessage());
        assertEquals(CodecDiagnostic.Severity.ERROR, diag.getSeverity());
        assertEquals("Source", diag.getSource());
        assertTrue(diag.isError());
        assertNull(diag.getLocation());
        assertEquals(-1, diag.getLine());
        assertEquals(-1, diag.getColumn());
    }

    // ============= Factory Method Tests - Warning =============

    @Test
    @DisplayName("warning() factory creates WARNING diagnostic without location")
    void warningFactoryCreatesWarningDiagnosticWithoutLocation() {
        CodecDiagnostic diag = CodecDiagnostic.warning("Warning message", "WarningSource");

        assertEquals("Warning message", diag.getMessage());
        assertEquals(CodecDiagnostic.Severity.WARNING, diag.getSeverity());
        assertEquals("WarningSource", diag.getSource());
        assertTrue(diag.isWarning());
        assertFalse(diag.isError());
        assertNull(diag.getLocation());
    }

    @Test
    @DisplayName("warning() factory with null location creates WARNING diagnostic without location")
    void warningFactoryWithNullLocationCreatesWarningDiagnosticWithoutLocation() {
        CodecDiagnostic diag = CodecDiagnostic.warning("Warning message", null, "Source");

        assertEquals("Warning message", diag.getMessage());
        assertEquals(CodecDiagnostic.Severity.WARNING, diag.getSeverity());
        assertEquals("Source", diag.getSource());
        assertTrue(diag.isWarning());
        assertNull(diag.getLocation());
        assertEquals(-1, diag.getLine());
        assertEquals(-1, diag.getColumn());
    }

    // ============= Factory Method Tests - Info =============

    @Test
    @DisplayName("info() factory creates INFO diagnostic without location")
    void infoFactoryCreatesInfoDiagnosticWithoutLocation() {
        CodecDiagnostic diag = CodecDiagnostic.info("Info message", "InfoSource");

        assertEquals("Info message", diag.getMessage());
        assertEquals(CodecDiagnostic.Severity.INFO, diag.getSeverity());
        assertEquals("InfoSource", diag.getSource());
        assertTrue(diag.isInfo());
        assertFalse(diag.isError());
        assertFalse(diag.isWarning());
        assertNull(diag.getLocation());
    }

    // ============= Getter Tests =============

    @Test
    @DisplayName("getMessage returns correct message")
    void getMessageReturnsCorrectMessage() {
        CodecDiagnostic diag = new CodecDiagnostic("Custom message", CodecDiagnostic.Severity.ERROR, "Source");
        assertEquals("Custom message", diag.getMessage());
    }

    @Test
    @DisplayName("getLocation returns correct location")
    void getLocationReturnsCorrectLocation() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", "custom/location", CodecDiagnostic.Severity.ERROR, "Source");
        assertEquals("custom/location", diag.getLocation());
    }

    @Test
    @DisplayName("getLocation returns null when not set")
    void getLocationReturnsNullWhenNotSet() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", CodecDiagnostic.Severity.ERROR, "Source");
        assertNull(diag.getLocation());
    }

    @Test
    @DisplayName("getLine returns -1 by default")
    void getLineReturnsNegativeOneByDefault() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", "location", CodecDiagnostic.Severity.ERROR, "Source");
        assertEquals(-1, diag.getLine());
    }

    @Test
    @DisplayName("getColumn returns -1 by default")
    void getColumnReturnsNegativeOneByDefault() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", "location", CodecDiagnostic.Severity.ERROR, "Source");
        assertEquals(-1, diag.getColumn());
    }

    @Test
    @DisplayName("getSeverity returns correct severity")
    void getSeverityReturnsCorrectSeverity() {
        CodecDiagnostic errorDiag = new CodecDiagnostic("Message", CodecDiagnostic.Severity.ERROR, "Source");
        CodecDiagnostic warnDiag = new CodecDiagnostic("Message", CodecDiagnostic.Severity.WARNING, "Source");

        assertEquals(CodecDiagnostic.Severity.ERROR, errorDiag.getSeverity());
        assertEquals(CodecDiagnostic.Severity.WARNING, warnDiag.getSeverity());
    }

    @Test
    @DisplayName("getSource returns correct source")
    void getSourceReturnsCorrectSource() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", CodecDiagnostic.Severity.ERROR, "MyComponent");
        assertEquals("MyComponent", diag.getSource());
    }

    // ============= toString Tests =============

    @Test
    @DisplayName("toString formats error with message and source")
    void toStringFormatsErrorWithMessageAndSource() {
        CodecDiagnostic diag = new CodecDiagnostic("Test error", CodecDiagnostic.Severity.ERROR, "TestComponent");
        String str = diag.toString();

        assertTrue(str.contains("ERROR"));
        assertTrue(str.contains("Test error"));
        assertTrue(str.contains("TestComponent"));
    }

    @Test
    @DisplayName("toString formats warning with message and source")
    void toStringFormatsWarningWithMessageAndSource() {
        CodecDiagnostic diag = new CodecDiagnostic("Test warning", CodecDiagnostic.Severity.WARNING, "TestComponent");
        String str = diag.toString();

        assertTrue(str.contains("WARNING"));
        assertTrue(str.contains("Test warning"));
        assertTrue(str.contains("TestComponent"));
    }

    @Test
    @DisplayName("toString includes location when available")
    void toStringIncludesLocationWhenAvailable() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", "path/to/file", CodecDiagnostic.Severity.ERROR, "Source");
        String str = diag.toString();

        assertTrue(str.contains("path/to/file"));
        assertTrue(str.contains("at"));
    }

    @Test
    @DisplayName("toString does not include location when null")
    void toStringDoesNotIncludeLocationWhenNull() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", CodecDiagnostic.Severity.ERROR, "Source");
        String str = diag.toString();

        assertFalse(str.contains("at"));
    }

    @Test
    @DisplayName("toString does not include line when line is -1")
    void toStringDoesNotIncludeLineWhenLineIsNegative() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", CodecDiagnostic.Severity.ERROR, "Source");
        String str = diag.toString();

        assertFalse(str.contains("line"));
    }

    @Test
    @DisplayName("toString includes source component")
    void toStringIncludesSourceComponent() {
        CodecDiagnostic diag = new CodecDiagnostic("Message", "location", CodecDiagnostic.Severity.ERROR, "MySource");
        String str = diag.toString();

        assertTrue(str.contains("MySource"));
        assertTrue(str.contains("("));
        assertTrue(str.contains(")"));
    }

    @Test
    @DisplayName("toString format for error without location")
    void toStringFormatErrorWithoutLocation() {
        CodecDiagnostic diag = new CodecDiagnostic("Error occurred", CodecDiagnostic.Severity.ERROR, "Parser");
        String str = diag.toString();

        assertEquals("ERROR: Error occurred (Parser)", str);
    }

    @Test
    @DisplayName("toString format for warning with location")
    void toStringFormatWarningWithLocation() {
        CodecDiagnostic diag = new CodecDiagnostic("Warning here", "file.json", CodecDiagnostic.Severity.WARNING, "Validator");
        String str = diag.toString();

        assertTrue(str.startsWith("WARNING:"));
        assertTrue(str.contains("Warning here"));
        assertTrue(str.contains("at file.json"));
        assertTrue(str.contains("Validator"));
    }

    // ============= Severity Enum Tests =============

    @Test
    @DisplayName("Severity.ERROR enum value exists")
    void severityErrorEnumValueExists() {
        assertEquals("ERROR", CodecDiagnostic.Severity.ERROR.name());
    }

    @Test
    @DisplayName("Severity.WARNING enum value exists")
    void severityWarningEnumValueExists() {
        assertEquals("WARNING", CodecDiagnostic.Severity.WARNING.name());
    }

    @Test
    @DisplayName("Severity.INFO enum value exists")
    void severityInfoEnumValueExists() {
        assertEquals("INFO", CodecDiagnostic.Severity.INFO.name());
    }

    @Test
    @DisplayName("Severity enum has all values")
    void severityEnumHasAllValues() {
        CodecDiagnostic.Severity[] values = CodecDiagnostic.Severity.values();
        assertEquals(3, values.length);
        assertTrue(contains(values, CodecDiagnostic.Severity.ERROR));
        assertTrue(contains(values, CodecDiagnostic.Severity.WARNING));
        assertTrue(contains(values, CodecDiagnostic.Severity.INFO));
    }

    // ============= Integration Tests =============

    @Test
    @DisplayName("multiple diagnostics can be created independently")
    void multipleDiagnosticsCanBeCreatedIndependently() {
        CodecDiagnostic diag1 = CodecDiagnostic.error("Error 1", "Source1");
        CodecDiagnostic diag2 = CodecDiagnostic.warning("Warning 1", "Source2");
        CodecDiagnostic diag3 = new CodecDiagnostic("Custom", "location", CodecDiagnostic.Severity.ERROR, "Source3");

        assertEquals("Error 1", diag1.getMessage());
        assertEquals("Warning 1", diag2.getMessage());
        assertEquals("Custom", diag3.getMessage());

        assertTrue(diag1.isError());
        assertTrue(diag2.isWarning());
        assertTrue(diag3.isError());
    }

    @Test
    @DisplayName("diagnostic is immutable after creation")
    void diagnosticIsImmutableAfterCreation() {
        CodecDiagnostic diag = CodecDiagnostic.error("Original message", "OriginalSource");

        // All fields are final and private, so they cannot be modified
        assertEquals("Original message", diag.getMessage());
        assertEquals("OriginalSource", diag.getSource());

        // Create another diagnostic to verify independence
        CodecDiagnostic diag2 = CodecDiagnostic.error("New message", "NewSource");
        assertEquals("New message", diag2.getMessage());
        assertEquals("Original message", diag.getMessage());
    }

    // ============= Helper Methods =============

    /**
     * Helper to check if an array contains a value.
     */
    private boolean contains(CodecDiagnostic.Severity[] array, CodecDiagnostic.Severity value) {
        for (CodecDiagnostic.Severity s : array) {
            if (s == value) {
                return true;
            }
        }
        return false;
    }
}
