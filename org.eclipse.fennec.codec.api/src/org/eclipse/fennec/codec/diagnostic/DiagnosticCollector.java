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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;

import tools.jackson.core.TokenStreamLocation;

/**
 * Collects diagnostics during serialization/deserialization operations.
 * This collector aggregates errors and warnings that can then be added
 * to the EMF Resource's diagnostic lists.
 *
 * <p>Usage pattern:
 * <pre>
 * DiagnosticCollector collector = new DiagnosticCollector();
 * // ... perform serialization/deserialization ...
 * collector.addWarning("Could not resolve type", parserLocation, "TypeEntry");
 * // ... after operation completes ...
 * collector.addToResource(resource);
 * </pre>
 *
 * <p>The collector is thread-safe for adding diagnostics from multiple sources,
 * but typically used single-threaded within one load/save operation.
 *
 * @see <a href="docs/codec-v2-spec/15-error-handling.md">Spec 15: Error Handling</a>
 */
public class DiagnosticCollector {

    private final List<CodecDiagnostic> errors = Collections.synchronizedList(new ArrayList<>());
    private final List<CodecDiagnostic> warnings = Collections.synchronizedList(new ArrayList<>());

    /**
     * Adds an error diagnostic.
     *
     * @param message the error message
     * @param source the source component
     */
    public void addError(String message, String source) {
        errors.add(CodecDiagnostic.error(message, source));
    }

    /**
     * Adds an error diagnostic with parser location.
     *
     * @param message the error message
     * @param location the parser location (may be null)
     * @param source the source component
     */
    public void addError(String message, TokenStreamLocation location, String source) {
        errors.add(CodecDiagnostic.error(message, location, source));
    }

    /**
     * Adds an error diagnostic with a custom location string.
     *
     * @param message the error message
     * @param locationString the location description
     * @param source the source component
     */
    public void addError(String message, String locationString, String source) {
        errors.add(new CodecDiagnostic(message, locationString, CodecDiagnostic.Severity.ERROR, source));
    }

    /**
     * Adds a warning diagnostic.
     *
     * @param message the warning message
     * @param source the source component
     */
    public void addWarning(String message, String source) {
        warnings.add(CodecDiagnostic.warning(message, source));
    }

    /**
     * Adds a warning diagnostic with parser location.
     *
     * @param message the warning message
     * @param location the parser location (may be null)
     * @param source the source component
     */
    public void addWarning(String message, TokenStreamLocation location, String source) {
        warnings.add(CodecDiagnostic.warning(message, location, source));
    }

    /**
     * Adds a warning diagnostic with a custom location string.
     *
     * @param message the warning message
     * @param locationString the location description
     * @param source the source component
     */
    public void addWarning(String message, String locationString, String source) {
        warnings.add(new CodecDiagnostic(message, locationString, CodecDiagnostic.Severity.WARNING, source));
    }

    /**
     * Adds an informational diagnostic.
     * <p>
     * INFO diagnostics are treated as warnings for EMF Resource compatibility,
     * but represent informational messages about configuration normalization.
     *
     * @param message the info message
     * @param source the source component
     */
    public void addInfo(String message, String source) {
        warnings.add(CodecDiagnostic.info(message, source));
    }

    /**
     * Adds a pre-built diagnostic.
     * <p>
     * INFO and WARNING diagnostics are both added to warnings list for
     * EMF Resource compatibility.
     *
     * @param diagnostic the diagnostic to add
     */
    public void add(CodecDiagnostic diagnostic) {
        if (diagnostic.isError()) {
            errors.add(diagnostic);
        } else {
            // INFO and WARNING both go to warnings list
            warnings.add(diagnostic);
        }
    }

    /**
     * Returns all collected errors.
     *
     * @return unmodifiable list of error diagnostics
     */
    public List<CodecDiagnostic> getErrors() {
        return Collections.unmodifiableList(new ArrayList<>(errors));
    }

    /**
     * Returns all collected warnings.
     *
     * @return unmodifiable list of warning diagnostics
     */
    public List<CodecDiagnostic> getWarnings() {
        return Collections.unmodifiableList(new ArrayList<>(warnings));
    }

    /**
     * Returns the total number of errors collected.
     *
     * @return error count
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Returns the total number of warnings collected.
     *
     * @return warning count
     */
    public int getWarningCount() {
        return warnings.size();
    }

    /**
     * Returns whether any errors have been collected.
     *
     * @return true if at least one error exists
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Returns whether any warnings have been collected.
     *
     * @return true if at least one warning exists
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Returns whether any diagnostics (errors or warnings) have been collected.
     *
     * @return true if at least one diagnostic exists
     */
    public boolean hasDiagnostics() {
        return hasErrors() || hasWarnings();
    }

    /**
     * Adds all collected diagnostics to an EMF Resource.
     * Errors are added to resource.getErrors(), warnings to resource.getWarnings().
     *
     * @param resource the EMF resource to add diagnostics to
     */
    public void addToResource(Resource resource) {
        if (resource == null) {
            return;
        }
        resource.getErrors().addAll(errors);
        resource.getWarnings().addAll(warnings);
    }

    /**
     * Clears all collected diagnostics.
     */
    public void clear() {
        errors.clear();
        warnings.clear();
    }

    /**
     * Merges diagnostics from another collector into this one.
     *
     * @param other the collector to merge from
     */
    public void merge(DiagnosticCollector other) {
        if (other != null) {
            errors.addAll(other.errors);
            warnings.addAll(other.warnings);
        }
    }

    @Override
    public String toString() {
        return String.format("DiagnosticCollector[errors=%d, warnings=%d]", errors.size(), warnings.size());
    }
}
