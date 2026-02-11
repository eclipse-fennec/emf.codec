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
package org.eclipse.fennec.codec.diagnostic;

import org.eclipse.emf.ecore.resource.Resource;

import tools.jackson.core.TokenStreamLocation;

/**
 * A diagnostic implementation for codec.v2 that implements EMF's Resource.Diagnostic interface.
 * This allows codec errors and warnings to be collected into the standard EMF resource
 * diagnostics (resource.getErrors() and resource.getWarnings()).
 *
 * <p>Diagnostics include:
 * <ul>
 *   <li>Message describing the issue</li>
 *   <li>Location in source (JSON path or resource URI)</li>
 *   <li>Line and column numbers from the parser (when available)</li>
 *   <li>Severity level (ERROR, WARNING, or INFO)</li>
 *   <li>Source component that raised the issue</li>
 * </ul>
 *
 * @see <a href="docs/codec-v2-spec/15-error-handling.md">Spec 15: Error Handling</a>
 */
public class CodecDiagnostic implements Resource.Diagnostic {

    /**
     * Severity level for diagnostics.
     */
    public enum Severity {
        /** Error - operation may fail or produce incorrect results */
        ERROR,
        /** Warning - operation continues but user should be aware */
        WARNING,
        /** Info - informational messages about configuration normalization */
        INFO
    }

    private final String message;
    private final String location;
    private final int line;
    private final int column;
    private final Severity severity;
    private final String source;

    /**
     * Creates a diagnostic with parser location information.
     *
     * @param message the diagnostic message
     * @param parserLocation the Jackson parser location (may be null)
     * @param severity the diagnostic severity
     * @param source the component that raised the diagnostic (e.g., "TypeDeserializationEntry")
     */
    public CodecDiagnostic(String message, TokenStreamLocation parserLocation, Severity severity, String source) {
        this.message = message;
        this.severity = severity;
        this.source = source;
        if (parserLocation != null) {
            this.location = parserLocation.toString();
            this.line = parserLocation.getLineNr();
            this.column = parserLocation.getColumnNr();
        } else {
            this.location = null;
            this.line = -1;
            this.column = -1;
        }
    }

    /**
     * Creates a diagnostic with a custom location string.
     *
     * @param message the diagnostic message
     * @param location the location description (e.g., JSON path or URI)
     * @param severity the diagnostic severity
     * @param source the component that raised the diagnostic
     */
    public CodecDiagnostic(String message, String location, Severity severity, String source) {
        this.message = message;
        this.location = location;
        this.line = -1;
        this.column = -1;
        this.severity = severity;
        this.source = source;
    }

    /**
     * Creates a diagnostic without location information.
     *
     * @param message the diagnostic message
     * @param severity the diagnostic severity
     * @param source the component that raised the diagnostic
     */
    public CodecDiagnostic(String message, Severity severity, String source) {
        this(message, (String) null, severity, source);
    }

    /**
     * Convenience factory method for creating an error diagnostic.
     *
     * @param message the error message
     * @param source the component that raised the error
     * @return a new error diagnostic
     */
    public static CodecDiagnostic error(String message, String source) {
        return new CodecDiagnostic(message, Severity.ERROR, source);
    }

    /**
     * Convenience factory method for creating an error diagnostic with parser location.
     *
     * @param message the error message
     * @param parserLocation the Jackson parser location
     * @param source the component that raised the error
     * @return a new error diagnostic
     */
    public static CodecDiagnostic error(String message, TokenStreamLocation parserLocation, String source) {
        return new CodecDiagnostic(message, parserLocation, Severity.ERROR, source);
    }

    /**
     * Convenience factory method for creating a warning diagnostic.
     *
     * @param message the warning message
     * @param source the component that raised the warning
     * @return a new warning diagnostic
     */
    public static CodecDiagnostic warning(String message, String source) {
        return new CodecDiagnostic(message, Severity.WARNING, source);
    }

    /**
     * Convenience factory method for creating a warning diagnostic with parser location.
     *
     * @param message the warning message
     * @param parserLocation the Jackson parser location
     * @param source the component that raised the warning
     * @return a new warning diagnostic
     */
    public static CodecDiagnostic warning(String message, TokenStreamLocation parserLocation, String source) {
        return new CodecDiagnostic(message, parserLocation, Severity.WARNING, source);
    }

    /**
     * Convenience factory method for creating an info diagnostic.
     * <p>
     * INFO diagnostics represent informational messages about configuration
     * normalization or non-critical observations.
     *
     * @param message the info message
     * @param source the component that raised the info
     * @return a new info diagnostic
     */
    public static CodecDiagnostic info(String message, String source) {
        return new CodecDiagnostic(message, Severity.INFO, source);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    /**
     * Returns the severity of this diagnostic.
     *
     * @return the severity (ERROR, WARNING, or INFO)
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Returns the source component that raised this diagnostic.
     *
     * @return the source component name
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns whether this is an error diagnostic.
     *
     * @return true if severity is ERROR
     */
    public boolean isError() {
        return severity == Severity.ERROR;
    }

    /**
     * Returns whether this is a warning diagnostic.
     *
     * @return true if severity is WARNING
     */
    public boolean isWarning() {
        return severity == Severity.WARNING;
    }

    /**
     * Returns whether this is an info diagnostic.
     *
     * @return true if severity is INFO
     */
    public boolean isInfo() {
        return severity == Severity.INFO;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(severity.name()).append(": ").append(message);
        if (location != null) {
            sb.append(" at ").append(location);
        }
        if (line > 0) {
            sb.append(" [line ").append(line);
            if (column > 0) {
                sb.append(", column ").append(column);
            }
            sb.append("]");
        }
        if (source != null) {
            sb.append(" (").append(source).append(")");
        }
        return sb.toString();
    }
}
