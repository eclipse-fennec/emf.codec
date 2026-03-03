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
package org.eclipse.fennec.codec.jsonschema.v2.constants;

/**
 * 
 * @author ilenia
 * @since Mar 2, 2026
 */
public interface CodecJsonSchemaOptions {
	
	/**
	 * Option key to enable anchor-based references instead of JSON Pointer references.
	 * <p>
	 * When set to {@code true}, the converter will:
	 * <ul>
	 *   <li>Generate {@code $anchor} for each EClass definition</li>
	 *   <li>Use {@code #anchorName} instead of {@code #/definitions/Name} for references</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Default: {@code false} (use JSON Pointer references)
	 * </p>
	 * <p>
	 * Can be overridden per EClass using the annotation:
	 * {@code @http://fennec.eclipse.org/jsonschema(useAnchor="true")}
	 * </p>
	 */
	public static final String OPTION_USE_ANCHOR_REFS = "codec.jsonschema.useAnchorRefs";

	/**
	 * When set to {@code true}, every structural feature is added to the
	 * {@code required} array regardless of its lowerBound.
	 * <p>
	 * Useful when generating schemas for AI structured-output requests, where all
	 * fields must be declared required so the model is forced to populate them.
	 * </p>
	 * <p>
	 * Default: {@code false} (only features with lowerBound &gt;= 1 are required)
	 * </p>
	 */
	public static final String OPTION_ALL_FIELDS_REQUIRED = "codec.jsonschema.allFieldsRequired";

	/**
	 * When set to {@code true}, inheritance hierarchies are flattened: instead of
	 * {@code allOf} with {@code $ref} to parent definitions, all inherited properties
	 * are inlined directly into the child object definition.
	 * <p>
	 * This is useful for AI structured-output APIs that do not support
	 * {@code $ref} inside {@code allOf}.
	 * </p>
	 * <p>
	 * Default: {@code false} (use {@code allOf} with {@code $ref} for inheritance)
	 * </p>
	 */
	public static final String OPTION_FLAT_ALL_OF = "codec.jsonschema.flatAllOf";

	/**
	 * When set to {@code true}, property names are resolved from ExtendedMetaData
	 * annotations instead of EMF feature names.
	 * <p>
	 * Default: {@code false}
	 * </p>
	 */
	public static final String OPTION_USE_NAMES_FROM_EXTENDED_METADATA = "codec.jsonschema.useNamesFromExtendedMetadata";

	/**
	 * A set of JSON Schema keywords to suppress in the generated output.
	 * <p>
	 * When a keyword is in this set, the converter will skip writing it.
	 * Useful for generating schemas compatible with AI structured-output APIs
	 * that do not support certain keywords.
	 * </p>
	 * <p>
	 * Example usage:
	 * <pre>
	 * Map&lt;String, Object&gt; options = Map.of(
	 *     CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS,
	 *     Set.of("maxItems", "minItems", "description", "additionalProperties")
	 * );
	 * </pre>
	 * </p>
	 * <p>
	 * Value: {@code Collection<String>} of keyword names to suppress
	 * </p>
	 */
	public static final String OPTION_SUPPRESS_KEYWORDS = "codec.jsonschema.suppressKeywords";
	
	/**
	 * Option key for the schema feature/definitions key.
	 * Values: "definitions", "$defs", "schemas", or null for auto-detection.
	 */
	public static final String OPTION_SCHEMA_FEATURE = "codec.jsonschema.feature.key";

	/**
	 * Option key to enable pretty printing of output.
	 */
	public static final String OPTION_PRETTY_PRINT = "codec.jsonschema.pretty.print";

	/**
	 * Option key for the JSON Schema draft version to use when serializing.
	 * Values: "draft-04", "draft-06", "draft-07", "2019-09", "2020-12"
	 */
	public static final String OPTION_SCHEMA_DRAFT = "codec.jsonschema.draft";

}
