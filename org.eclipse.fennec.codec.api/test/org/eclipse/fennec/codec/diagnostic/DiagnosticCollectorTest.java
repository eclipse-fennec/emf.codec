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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DiagnosticCollector}.
 *
 * @see <a href="docs/codec-v2-spec/15-error-handling.md">Spec 15: Error Handling</a>
 */
@DisplayName("DiagnosticCollector")
class DiagnosticCollectorTest {

    private DiagnosticCollector collector;

    @BeforeEach
    void setUp() {
        collector = new DiagnosticCollector();
    }

    // ============= Constructor Tests =============

    @Test
    @DisplayName("constructor creates empty collector")
    void constructorCreatesEmptyCollector() {
        assertFalse(collector.hasErrors());
        assertFalse(collector.hasWarnings());
        assertFalse(collector.hasDiagnostics());
        assertEquals(0, collector.getErrorCount());
        assertEquals(0, collector.getWarningCount());
    }

    // ============= addError Tests =============

    @Test
    @DisplayName("addError() adds error without location")
    void addErrorAddsErrorWithoutLocation() {
        collector.addError("Error message", "ErrorSource");

        assertTrue(collector.hasErrors());
        assertEquals(1, collector.getErrorCount());
        assertEquals(0, collector.getWarningCount());

        List<CodecDiagnostic> errors = collector.getErrors();
        assertEquals(1, errors.size());
        assertEquals("Error message", errors.get(0).getMessage());
        assertEquals("ErrorSource", errors.get(0).getSource());
        assertTrue(errors.get(0).isError());
    }

    @Test
    @DisplayName("addError() with location string adds error with location")
    void addErrorWithLocationStringAddsErrorWithLocation() {
        collector.addError("Error at location", "path/to/file:10", "Source");

        assertTrue(collector.hasErrors());
        assertEquals(1, collector.getErrorCount());

        List<CodecDiagnostic> errors = collector.getErrors();
        assertEquals("path/to/file:10", errors.get(0).getLocation());
    }

    @Test
    @DisplayName("addError() with null location adds error without location")
    void addErrorWithNullLocationAddsErrorWithoutLocation() {
        collector.addError("Error", (String) null, "Source");

        assertTrue(collector.hasErrors());
        assertEquals(1, collector.getErrorCount());
        List<CodecDiagnostic> errors = collector.getErrors();
        assertEquals(-1, errors.get(0).getLine());
        assertEquals(-1, errors.get(0).getColumn());
    }

    @Test
    @DisplayName("addError() multiple times adds multiple errors")
    void addErrorMultipleTimesAddsMultipleErrors() {
        collector.addError("Error 1", "Source1");
        collector.addError("Error 2", "Source2");
        collector.addError("Error 3", "Source3");

        assertEquals(3, collector.getErrorCount());
        assertEquals(0, collector.getWarningCount());
    }

    // ============= addWarning Tests =============

    @Test
    @DisplayName("addWarning() adds warning without location")
    void addWarningAddsWarningWithoutLocation() {
        collector.addWarning("Warning message", "WarningSource");

        assertTrue(collector.hasWarnings());
        assertEquals(1, collector.getWarningCount());
        assertEquals(0, collector.getErrorCount());

        List<CodecDiagnostic> warnings = collector.getWarnings();
        assertEquals(1, warnings.size());
        assertEquals("Warning message", warnings.get(0).getMessage());
        assertEquals("WarningSource", warnings.get(0).getSource());
        assertTrue(warnings.get(0).isWarning());
    }

    @Test
    @DisplayName("addWarning() with location string adds warning with location")
    void addWarningWithLocationStringAddsWarningWithLocation() {
        collector.addWarning("Warning at location", "path/to/file:20", "Source");

        assertTrue(collector.hasWarnings());
        assertEquals(1, collector.getWarningCount());

        List<CodecDiagnostic> warnings = collector.getWarnings();
        assertEquals("path/to/file:20", warnings.get(0).getLocation());
    }

    @Test
    @DisplayName("addWarning() with null location adds warning without location")
    void addWarningWithNullLocationAddsWarningWithoutLocation() {
        collector.addWarning("Warning", (String) null, "Source");

        assertTrue(collector.hasWarnings());
        assertEquals(1, collector.getWarningCount());
        List<CodecDiagnostic> warnings = collector.getWarnings();
        assertEquals(-1, warnings.get(0).getLine());
        assertEquals(-1, warnings.get(0).getColumn());
    }

    @Test
    @DisplayName("addWarning() multiple times adds multiple warnings")
    void addWarningMultipleTimesAddsMultipleWarnings() {
        collector.addWarning("Warning 1", "Source1");
        collector.addWarning("Warning 2", "Source2");
        collector.addWarning("Warning 3", "Source3");

        assertEquals(3, collector.getWarningCount());
        assertEquals(0, collector.getErrorCount());
    }

    // ============= add Tests =============

    @Test
    @DisplayName("add() adds error diagnostic to errors")
    void addAddsErrorDiagnosticToErrors() {
        CodecDiagnostic errorDiag = CodecDiagnostic.error("Error", "Source");
        collector.add(errorDiag);

        assertTrue(collector.hasErrors());
        assertEquals(1, collector.getErrorCount());
        assertEquals(0, collector.getWarningCount());
        assertEquals(errorDiag, collector.getErrors().get(0));
    }

    @Test
    @DisplayName("add() adds warning diagnostic to warnings")
    void addAddsWarningDiagnosticToWarnings() {
        CodecDiagnostic warnDiag = CodecDiagnostic.warning("Warning", "Source");
        collector.add(warnDiag);

        assertTrue(collector.hasWarnings());
        assertEquals(1, collector.getWarningCount());
        assertEquals(0, collector.getErrorCount());
        assertEquals(warnDiag, collector.getWarnings().get(0));
    }

    @Test
    @DisplayName("add() multiple diagnostics adds all correctly")
    void addMultipleDiagnosticsAddsAllCorrectly() {
        CodecDiagnostic error1 = CodecDiagnostic.error("Error 1", "Source");
        CodecDiagnostic warn1 = CodecDiagnostic.warning("Warning 1", "Source");
        CodecDiagnostic error2 = CodecDiagnostic.error("Error 2", "Source");

        collector.add(error1);
        collector.add(warn1);
        collector.add(error2);

        assertEquals(2, collector.getErrorCount());
        assertEquals(1, collector.getWarningCount());
    }

    // ============= Getter Tests =============

    @Test
    @DisplayName("getErrors() returns unmodifiable list of errors")
    void getErrorsReturnsUnmodifiableListOfErrors() {
        collector.addError("Error 1", "Source");
        collector.addError("Error 2", "Source");

        List<CodecDiagnostic> errors = collector.getErrors();
        assertEquals(2, errors.size());

        // Verify list is unmodifiable by trying to modify it
        try {
            errors.clear();
            assertTrue(false, "Should have thrown exception");
        } catch (UnsupportedOperationException e) {
            // Expected
        }

        // Verify original collector is unaffected
        assertEquals(2, collector.getErrorCount());
    }

    @Test
    @DisplayName("getWarnings() returns unmodifiable list of warnings")
    void getWarningsReturnsUnmodifiableListOfWarnings() {
        collector.addWarning("Warning 1", "Source");
        collector.addWarning("Warning 2", "Source");

        List<CodecDiagnostic> warnings = collector.getWarnings();
        assertEquals(2, warnings.size());

        // Verify list is unmodifiable
        try {
            warnings.clear();
            assertTrue(false, "Should have thrown exception");
        } catch (UnsupportedOperationException e) {
            // Expected
        }

        // Verify original collector is unaffected
        assertEquals(2, collector.getWarningCount());
    }

    @Test
    @DisplayName("getErrorCount() returns correct count")
    void getErrorCountReturnsCorrectCount() {
        assertEquals(0, collector.getErrorCount());
        collector.addError("Error 1", "Source");
        assertEquals(1, collector.getErrorCount());
        collector.addError("Error 2", "Source");
        assertEquals(2, collector.getErrorCount());
    }

    @Test
    @DisplayName("getWarningCount() returns correct count")
    void getWarningCountReturnsCorrectCount() {
        assertEquals(0, collector.getWarningCount());
        collector.addWarning("Warning 1", "Source");
        assertEquals(1, collector.getWarningCount());
        collector.addWarning("Warning 2", "Source");
        assertEquals(2, collector.getWarningCount());
    }

    // ============= hasErrors/hasWarnings/hasDiagnostics Tests =============

    @Test
    @DisplayName("hasErrors() returns false when no errors")
    void hasErrorsReturnsFalseWhenNoErrors() {
        assertFalse(collector.hasErrors());
    }

    @Test
    @DisplayName("hasErrors() returns true after adding error")
    void hasErrorsReturnsTrueAfterAddingError() {
        collector.addError("Error", "Source");
        assertTrue(collector.hasErrors());
    }

    @Test
    @DisplayName("hasWarnings() returns false when no warnings")
    void hasWarningsReturnsFalseWhenNoWarnings() {
        assertFalse(collector.hasWarnings());
    }

    @Test
    @DisplayName("hasWarnings() returns true after adding warning")
    void hasWarningsReturnsTrueAfterAddingWarning() {
        collector.addWarning("Warning", "Source");
        assertTrue(collector.hasWarnings());
    }

    @Test
    @DisplayName("hasDiagnostics() returns false when empty")
    void hasDiagnosticsReturnsFalseWhenEmpty() {
        assertFalse(collector.hasDiagnostics());
    }

    @Test
    @DisplayName("hasDiagnostics() returns true with errors only")
    void hasDiagnosticsReturnsTrueWithErrorsOnly() {
        collector.addError("Error", "Source");
        assertTrue(collector.hasDiagnostics());
    }

    @Test
    @DisplayName("hasDiagnostics() returns true with warnings only")
    void hasDiagnosticsReturnsTrueWithWarningsOnly() {
        collector.addWarning("Warning", "Source");
        assertTrue(collector.hasDiagnostics());
    }

    @Test
    @DisplayName("hasDiagnostics() returns true with both errors and warnings")
    void hasDiagnosticsReturnsTrueWithBothErrorsAndWarnings() {
        collector.addError("Error", "Source");
        collector.addWarning("Warning", "Source");
        assertTrue(collector.hasDiagnostics());
    }

    // ============= clear Tests =============

    @Test
    @DisplayName("clear() removes all diagnostics")
    void clearRemovesAllDiagnostics() {
        collector.addError("Error 1", "Source");
        collector.addError("Error 2", "Source");
        collector.addWarning("Warning 1", "Source");
        collector.addWarning("Warning 2", "Source");

        assertTrue(collector.hasDiagnostics());
        assertEquals(2, collector.getErrorCount());
        assertEquals(2, collector.getWarningCount());

        collector.clear();

        assertFalse(collector.hasDiagnostics());
        assertEquals(0, collector.getErrorCount());
        assertEquals(0, collector.getWarningCount());
        assertTrue(collector.getErrors().isEmpty());
        assertTrue(collector.getWarnings().isEmpty());
    }

    @Test
    @DisplayName("clear() can be called multiple times")
    void clearCanBeCalledMultipleTimes() {
        collector.addError("Error", "Source");
        collector.clear();
        collector.clear(); // Should not throw

        assertFalse(collector.hasDiagnostics());
    }

    @Test
    @DisplayName("clear() on empty collector does nothing")
    void clearOnEmptyCollectorDoesNothing() {
        collector.clear();

        assertFalse(collector.hasDiagnostics());
        assertEquals(0, collector.getErrorCount());
        assertEquals(0, collector.getWarningCount());
    }

    // ============= merge Tests =============

    @Test
    @DisplayName("merge() adds all diagnostics from other collector")
    void mergeAddsAllDiagnosticsFromOtherCollector() {
        DiagnosticCollector other = new DiagnosticCollector();
        other.addError("Error 1", "Source");
        other.addError("Error 2", "Source");
        other.addWarning("Warning 1", "Source");

        collector.merge(other);

        assertEquals(2, collector.getErrorCount());
        assertEquals(1, collector.getWarningCount());
    }

    @Test
    @DisplayName("merge() preserves existing diagnostics")
    void mergePreservesExistingDiagnostics() {
        collector.addError("Existing Error", "Source");
        collector.addWarning("Existing Warning", "Source");

        DiagnosticCollector other = new DiagnosticCollector();
        other.addError("New Error", "Source");
        other.addWarning("New Warning", "Source");

        collector.merge(other);

        assertEquals(2, collector.getErrorCount());
        assertEquals(2, collector.getWarningCount());
    }

    @Test
    @DisplayName("merge() with null collector does nothing")
    void mergeWithNullCollectorDoesNothing() {
        collector.addError("Error", "Source");
        collector.addWarning("Warning", "Source");

        collector.merge(null); // Should not throw

        assertEquals(1, collector.getErrorCount());
        assertEquals(1, collector.getWarningCount());
    }

    @Test
    @DisplayName("merge() with empty collector does nothing")
    void mergeWithEmptyCollectorDoesNothing() {
        collector.addError("Error", "Source");
        DiagnosticCollector other = new DiagnosticCollector();

        collector.merge(other);

        assertEquals(1, collector.getErrorCount());
        assertEquals(0, collector.getWarningCount());
    }

    @Test
    @DisplayName("merge() does not modify source collector")
    void mergeDoesNotModifySourceCollector() {
        DiagnosticCollector other = new DiagnosticCollector();
        other.addError("Error", "Source");
        other.addWarning("Warning", "Source");

        collector.merge(other);

        // Verify other is unchanged
        assertEquals(1, other.getErrorCount());
        assertEquals(1, other.getWarningCount());
    }

    // ============= addToResource Tests =============

    @Test
    @DisplayName("addToResource() adds errors to resource.getErrors()")
    void addToResourceAddsErrorsToResourceErrors() {
        Resource resource = new ResourceImpl();
        collector.addError("Error 1", "Source");
        collector.addError("Error 2", "Source");

        collector.addToResource(resource);

        assertEquals(2, resource.getErrors().size());
        assertEquals(0, resource.getWarnings().size());
    }

    @Test
    @DisplayName("addToResource() adds warnings to resource.getWarnings()")
    void addToResourceAddsWarningsToResourceWarnings() {
        Resource resource = new ResourceImpl();
        collector.addWarning("Warning 1", "Source");
        collector.addWarning("Warning 2", "Source");

        collector.addToResource(resource);

        assertEquals(0, resource.getErrors().size());
        assertEquals(2, resource.getWarnings().size());
    }

    @Test
    @DisplayName("addToResource() adds both errors and warnings")
    void addToResourceAddsBothErrorsAndWarnings() {
        Resource resource = new ResourceImpl();
        collector.addError("Error 1", "Source");
        collector.addError("Error 2", "Source");
        collector.addWarning("Warning 1", "Source");

        collector.addToResource(resource);

        assertEquals(2, resource.getErrors().size());
        assertEquals(1, resource.getWarnings().size());
    }

    @Test
    @DisplayName("addToResource() with null resource does nothing")
    void addToResourceWithNullResourceDoesNothing() {
        collector.addError("Error", "Source");
        collector.addWarning("Warning", "Source");

        collector.addToResource(null); // Should not throw

        assertEquals(1, collector.getErrorCount());
        assertEquals(1, collector.getWarningCount());
    }

    @Test
    @DisplayName("addToResource() with empty collector does nothing")
    void addToResourceWithEmptyCollectorDoesNothing() {
        Resource resource = new ResourceImpl();

        collector.addToResource(resource);

        assertEquals(0, resource.getErrors().size());
        assertEquals(0, resource.getWarnings().size());
    }

    @Test
    @DisplayName("addToResource() preserves existing resource diagnostics")
    void addToResourcePreservesExistingResourceDiagnostics() {
        Resource resource = new ResourceImpl();
        // Add some existing diagnostics
        resource.getErrors().add(new MockDiagnostic("Existing error"));

        collector.addError("New error", "Source");

        collector.addToResource(resource);

        assertEquals(2, resource.getErrors().size());
    }

    @Test
    @DisplayName("addToResource() can be called multiple times")
    void addToResourceCanBeCalledMultipleTimes() {
        Resource resource = new ResourceImpl();
        collector.addError("Error", "Source");

        collector.addToResource(resource);
        collector.addToResource(resource);

        // Diagnostics are added again, so we have duplicates
        assertEquals(2, resource.getErrors().size());
    }

    // ============= toString Tests =============

    @Test
    @DisplayName("toString formats collector state correctly")
    void toStringFormatsCollectorStateCorrectly() {
        collector.addError("Error", "Source");
        collector.addWarning("Warning 1", "Source");
        collector.addWarning("Warning 2", "Source");

        String str = collector.toString();

        assertTrue(str.contains("DiagnosticCollector"));
        assertTrue(str.contains("errors=1"));
        assertTrue(str.contains("warnings=2"));
    }

    @Test
    @DisplayName("toString shows empty collector correctly")
    void toStringShowsEmptyCollectorCorrectly() {
        String str = collector.toString();

        assertTrue(str.contains("DiagnosticCollector"));
        assertTrue(str.contains("errors=0"));
        assertTrue(str.contains("warnings=0"));
    }

    // ============= Mixed Operations Tests =============

    @Test
    @DisplayName("collector handles mixed add, clear, and merge operations")
    void collectorHandlesMixedOperations() {
        // Add some diagnostics
        collector.addError("Error 1", "Source");
        collector.addWarning("Warning 1", "Source");
        assertEquals(1, collector.getErrorCount());
        assertEquals(1, collector.getWarningCount());

        // Merge another collector
        DiagnosticCollector other = new DiagnosticCollector();
        other.addError("Error 2", "Source");
        other.addWarning("Warning 2", "Source");
        collector.merge(other);
        assertEquals(2, collector.getErrorCount());
        assertEquals(2, collector.getWarningCount());

        // Clear and verify
        collector.clear();
        assertEquals(0, collector.getErrorCount());
        assertEquals(0, collector.getWarningCount());

        // Add more
        collector.addError("Error 3", "Source");
        assertEquals(1, collector.getErrorCount());
    }

    @Test
    @DisplayName("diagnostic content is preserved through collector operations")
    void diagnosticContentIsPreservedThroughCollectorOperations() {
        CodecDiagnostic diag = CodecDiagnostic.error("Specific error message", "SpecificSource");
        collector.add(diag);

        List<CodecDiagnostic> errors = collector.getErrors();
        CodecDiagnostic retrieved = errors.get(0);

        assertEquals("Specific error message", retrieved.getMessage());
        assertEquals("SpecificSource", retrieved.getSource());
        assertTrue(retrieved.isError());
    }

    // ============= Mock Classes =============

    /**
     * Mock implementation of Resource.Diagnostic for testing.
     */
    static class MockDiagnostic implements Resource.Diagnostic {
        private final String message;

        MockDiagnostic(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getLocation() {
            return null;
        }

        @Override
        public int getLine() {
            return 0;
        }

        @Override
        public int getColumn() {
            return 0;
        }
    }
}
