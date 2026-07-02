/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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
package org.eclipse.fennec.codec.jsonschema.v2.converter;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * Diagnostic for JSON Schema conversion warnings and errors.
 * <p>
 * This class implements {@link Resource.Diagnostic} to allow conversion
 * issues to be reported through the standard EMF resource diagnostics
 * mechanism ({@code resource.getWarnings()} and {@code resource.getErrors()}).
 * </p>
 *
 * @author Data In Motion
 * @since 2025
 */
public class JsonSchemaConversionDiagnostic implements Resource.Diagnostic {

	/** Value indicating that line/column is not specified */
	private static final int UNSPECIFIED = -1;

	/**
	 * Diagnostic codes for different types of conversion issues.
	 */
	public enum Code {
		/** A $ref could not be resolved */
		UNRESOLVED_REFERENCE,
		/** Complex anyOf with different schemas */
		COMPLEX_ANYOF,
		/** Feature not supported by EMF */
		UNSUPPORTED_FEATURE,
		/** Feature partially supported (preserved as annotation) */
		PARTIAL_SUPPORT,
		/** A JSON Schema keyword could not be compiled into an OCL invariant */
		OCL_GENERATION_SKIPPED,
		/** General conversion warning */
		GENERAL_WARNING,
		/** General conversion error */
		GENERAL_ERROR
	}

	private final String message;
	private final String location;
	private final int line;
	private final int column;
	private final Code code;

	/**
	 * Creates a diagnostic with the given message and location.
	 *
	 * @param message the diagnostic message
	 * @param location the location (e.g., JSON path or schema name)
	 * @param code the diagnostic code
	 */
	public JsonSchemaConversionDiagnostic(String message, String location, Code code) {
		this(message, location, UNSPECIFIED, UNSPECIFIED, code);
	}

	/**
	 * Creates a diagnostic with full location information.
	 *
	 * @param message the diagnostic message
	 * @param location the location (e.g., JSON path or schema name)
	 * @param line the line number (or UNSPECIFIED_LINE)
	 * @param column the column number (or UNSPECIFIED_COLUMN)
	 * @param code the diagnostic code
	 */
	public JsonSchemaConversionDiagnostic(String message, String location, int line, int column, Code code) {
		this.message = message;
		this.location = location;
		this.line = line;
		this.column = column;
		this.code = code;
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
	 * Returns the diagnostic code.
	 *
	 * @return the diagnostic code
	 */
	public Code getCode() {
		return code;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(code).append("] ");
		sb.append(message);
		if (location != null && !location.isEmpty()) {
			sb.append(" at ").append(location);
		}
		if (line != UNSPECIFIED) {
			sb.append(" (line ").append(line);
			if (column != UNSPECIFIED) {
				sb.append(", column ").append(column);
			}
			sb.append(")");
		}
		return sb.toString();
	}

	// Factory methods for common diagnostics

	/**
	 * Creates a diagnostic for an unresolved reference.
	 *
	 * @param refPath the reference path that could not be resolved
	 * @return the diagnostic
	 */
	public static JsonSchemaConversionDiagnostic unresolvedReference(String refPath) {
		return new JsonSchemaConversionDiagnostic(
			"Could not resolve reference: " + refPath,
			refPath,
			Code.UNRESOLVED_REFERENCE
		);
	}

	/**
	 * Creates a diagnostic for complex anyOf.
	 *
	 * @param location the schema location
	 * @return the diagnostic
	 */
	public static JsonSchemaConversionDiagnostic complexAnyOf(String location) {
		return new JsonSchemaConversionDiagnostic(
			"Complex anyOf with different schemas detected. May require manual modeling.",
			location,
			Code.COMPLEX_ANYOF
		);
	}

	/**
	 * Creates a diagnostic for unsupported JSON Schema feature.
	 *
	 * @param keyword the unsupported keyword
	 * @param location the location where the keyword was found
	 * @return the diagnostic
	 */
	public static JsonSchemaConversionDiagnostic unsupportedFeature(String keyword, String location) {
		return new JsonSchemaConversionDiagnostic(
			"Unsupported JSON Schema keyword '" + keyword + "' - cannot be mapped to EMF",
			location,
			Code.UNSUPPORTED_FEATURE
		);
	}

	/**
	 * Creates a diagnostic for partially supported feature.
	 *
	 * @param keyword the keyword
	 * @param location the location
	 * @param detail additional detail about the limitation
	 * @return the diagnostic
	 */
	public static JsonSchemaConversionDiagnostic partialSupport(String keyword, String location, String detail) {
		return new JsonSchemaConversionDiagnostic(
			"Keyword '" + keyword + "' is partially supported: " + detail,
			location,
			Code.PARTIAL_SUPPORT
		);
	}

	/**
	 * Creates a diagnostic for a keyword that could not be compiled into an OCL invariant.
	 *
	 * @param keyword the JSON Schema keyword that was skipped
	 * @param location the location (e.g., EClass/feature name) where the keyword was found
	 * @param reason why OCL generation was skipped
	 * @return the diagnostic
	 */
	public static JsonSchemaConversionDiagnostic oclGenerationSkipped(String keyword, String location, String reason) {
		return new JsonSchemaConversionDiagnostic(
			"OCL invariant generation skipped for keyword '" + keyword + "': " + reason,
			location,
			Code.OCL_GENERATION_SKIPPED
		);
	}
}
