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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import tools.jackson.databind.JsonNode;

/**
 * Utility class for JSON Schema keyword classification and validation.
 * <p>
 * This class categorizes JSON Schema keywords by their support level in EMF mapping:
 * <ul>
 *   <li><b>Fully supported:</b> Keywords that map directly to EMF concepts</li>
 *   <li><b>Partially supported:</b> Keywords preserved as annotations but without semantic EMF equivalent</li>
 *   <li><b>Unsupported:</b> Keywords that cannot be represented in EMF</li>
 * </ul>
 * </p>
 *
 * @author Data In Motion
 * @since 1.0
 */
public final class JsonSchemaKeywords {

	private JsonSchemaKeywords() {
		// Utility class - no instantiation
	}

	// ========================================================================
	// Fully Supported Keywords
	// ========================================================================

	/** Core keywords fully supported */
	public static final Set<String> CORE_SUPPORTED = Set.of(
		"$schema", "$id", "$ref", "$defs", "definitions", "$anchor"
	);

	/** Type keywords fully supported */
	public static final Set<String> TYPE_SUPPORTED = Set.of(
		"type", "enum", "const"
	);

	/** Object keywords fully supported */
	public static final Set<String> OBJECT_SUPPORTED = Set.of(
		"properties", "required", "additionalProperties",
		"minProperties", "maxProperties"
	);

	/** Array keywords fully supported */
	public static final Set<String> ARRAY_SUPPORTED = Set.of(
		"items", "minItems", "maxItems", "uniqueItems"
	);

	/** Composition keywords fully supported */
	public static final Set<String> COMPOSITION_SUPPORTED = Set.of(
		"allOf", "anyOf", "oneOf"
	);

	/** Validation keywords fully supported (preserved as annotations) */
	public static final Set<String> VALIDATION_SUPPORTED = Set.of(
		"minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum",
		"multipleOf", "minLength", "maxLength", "pattern", "format"
	);

	/** Annotation keywords fully supported */
	public static final Set<String> ANNOTATION_SUPPORTED = Set.of(
		"title", "description", "default", "examples",
		"readOnly", "writeOnly", "deprecated", "$comment"
	);

	/** Vendor extension keywords fully supported */
	public static final Set<String> EXTENSION_SUPPORTED = Set.of(
		"x-abstract", "x-interface", "x-containment"
	);

	/** Content keywords fully supported (preserved as annotations) */
	public static final Set<String> CONTENT_SUPPORTED = Set.of(
		"contentEncoding", "contentMediaType"
	);

	// ========================================================================
	// Partially Supported Keywords
	// ========================================================================

	/** Keywords preserved as annotations but without full EMF semantics */
	public static final Set<String> PARTIALLY_SUPPORTED = Set.of(
		"patternProperties",      // Preserved but no EMF equivalent
		"unevaluatedProperties"   // Preserved, used for discriminated unions
	);

	// ========================================================================
	// Unsupported Keywords
	// ========================================================================

	/** Keywords that cannot be mapped to EMF */
	public static final Set<String> UNSUPPORTED = Set.of(
		// Negation and conditionals
		"not",
		"if", "then", "else",

		// Tuple validation (Draft 2020-12)
		"prefixItems",

		// Array contains constraints
		"contains", "minContains", "maxContains",

		// Object constraints without EMF equivalent
		"propertyNames",
		"dependentRequired", "dependentSchemas",

		// Dynamic references (Draft 2020-12)
		"$dynamicRef", "$dynamicAnchor",

		// Unevaluated (Draft 2019-09/2020-12)
		"unevaluatedItems",

		// Content schema (complex, contentEncoding/contentMediaType are supported)
		"contentSchema",

		// Meta-schema
		"$vocabulary"
	);

	// ========================================================================
	// Public API
	// ========================================================================

	/**
	 * Checks a schema node for unsupported keywords and returns diagnostics.
	 *
	 * @param schemaNode the JSON Schema node to check
	 * @param location the location for diagnostic messages (e.g., schema path)
	 * @return list of diagnostics for unsupported/partially supported keywords
	 */
	public static List<JsonSchemaConversionDiagnostic> checkForUnsupportedKeywords(
			JsonNode schemaNode, String location) {

		if (schemaNode == null || !schemaNode.isObject()) {
			return Collections.emptyList();
		}

		List<JsonSchemaConversionDiagnostic> diagnostics = new ArrayList<>();

		// Check for completely unsupported keywords
		for (String keyword : UNSUPPORTED) {
			if (schemaNode.has(keyword)) {
				diagnostics.add(JsonSchemaConversionDiagnostic.unsupportedFeature(keyword, location));
			}
		}

		// Check for partially supported keywords
		if (schemaNode.has("patternProperties")) {
			diagnostics.add(JsonSchemaConversionDiagnostic.partialSupport(
				"patternProperties", location, "preserved as annotation but no EMF equivalent"));
		}

		return diagnostics;
	}

	/**
	 * Checks if a keyword is fully supported.
	 *
	 * @param keyword the keyword to check
	 * @return true if fully supported
	 */
	public static boolean isFullySupported(String keyword) {
		return CORE_SUPPORTED.contains(keyword)
			|| TYPE_SUPPORTED.contains(keyword)
			|| OBJECT_SUPPORTED.contains(keyword)
			|| ARRAY_SUPPORTED.contains(keyword)
			|| COMPOSITION_SUPPORTED.contains(keyword)
			|| VALIDATION_SUPPORTED.contains(keyword)
			|| ANNOTATION_SUPPORTED.contains(keyword)
			|| CONTENT_SUPPORTED.contains(keyword)
			|| EXTENSION_SUPPORTED.contains(keyword);
	}

	/**
	 * Checks if a keyword is partially supported.
	 *
	 * @param keyword the keyword to check
	 * @return true if partially supported
	 */
	public static boolean isPartiallySupported(String keyword) {
		return PARTIALLY_SUPPORTED.contains(keyword);
	}

	/**
	 * Checks if a keyword is unsupported.
	 *
	 * @param keyword the keyword to check
	 * @return true if unsupported
	 */
	public static boolean isUnsupported(String keyword) {
		return UNSUPPORTED.contains(keyword);
	}

	/**
	 * Returns the support level for a keyword.
	 *
	 * @param keyword the keyword to check
	 * @return the support level
	 */
	public static SupportLevel getSupportLevel(String keyword) {
		if (isFullySupported(keyword)) {
			return SupportLevel.FULL;
		} else if (isPartiallySupported(keyword)) {
			return SupportLevel.PARTIAL;
		} else if (isUnsupported(keyword)) {
			return SupportLevel.NONE;
		}
		return SupportLevel.UNKNOWN;
	}

	/**
	 * Support level for JSON Schema keywords.
	 */
	public enum SupportLevel {
		/** Fully supported - maps directly to EMF */
		FULL,
		/** Partially supported - preserved as annotation */
		PARTIAL,
		/** Not supported - cannot be represented in EMF */
		NONE,
		/** Unknown keyword */
		UNKNOWN
	}
}
