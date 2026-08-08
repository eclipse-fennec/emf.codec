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
 * @since 1.0
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

	/**
	 * When set to {@code true}, references to abstract EClasses use {@code anyOf}
	 * instead of the default {@code oneOf} for polymorphic subclass references.
	 * <p>
	 * Default: {@code false} (use {@code oneOf})
	 * </p>
	 */
	public static final String OPTION_USE_ANY_OF_FOR_ABSTRACT = "codec.jsonschema.useAnyOfForAbstract";

	/**
	 * When set to {@code true}, vendor extension properties ({@code x-abstract},
	 * {@code x-interface}, {@code x-containment}) are suppressed from the output.
	 * <p>
	 * Use this when the target API or validator does not accept vendor extensions.
	 * Note that suppressing these properties means some EMF metadata will be lost
	 * during round-trip (abstract/interface flags, containment vs non-containment).
	 * </p>
	 * <p>
	 * Default: {@code false} (vendor extensions are written)
	 * </p>
	 */
	public static final String OPTION_SUPPRESS_VENDOR_EXTENSIONS = "codec.jsonschema.suppressVendorExtensions";

	/**
	 * When set to {@code true}, references that would normally use {@code $ref}
	 * are inlined: the full object definition is written directly at the reference
	 * site instead of pointing to a shared definition in {@code $defs}.
	 * <p>
	 * This is required for APIs (e.g., some AI structured-output endpoints) that
	 * do not accept JSON Schema with {@code $ref} references.
	 * </p>
	 * <p>
	 * When enabled:
	 * <ul>
	 *   <li>Both containment and non-containment references are inlined</li>
	 *   <li>The {@code $defs}/{@code definitions} section is omitted</li>
	 *   <li>Abstract type references inline each concrete subclass in {@code oneOf}/{@code anyOf}</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Default: {@code false} (use {@code $ref} references)
	 * </p>
	 */
	public static final String OPTION_INLINE_REFS = "codec.jsonschema.inlineRefs";

	/**
	 * When set to {@code true}, JSON Schema validation keywords that restrict a
	 * value (e.g. {@code minLength}, {@code pattern}, {@code minimum},
	 * {@code multipleOf}, {@code uniqueItems}) and OpenAPI discriminator/format
	 * extensions are compiled into OCL invariants and attached to the generated
	 * {@code EClass}es using EMF's standard validation-delegate annotation
	 * convention.
	 * <p>
	 * Keywords that are already enforced structurally (e.g. {@code minItems}/
	 * {@code maxItems} via {@code lowerBound}/{@code upperBound}) are unaffected.
	 * Keywords that cannot be compiled into OCL are skipped with a warning
	 * diagnostic (see {@code resource.getWarnings()}).
	 * </p>
	 * <p>
	 * fennec-codec does not depend on any OCL engine to generate these
	 * annotations; see {@link #OPTION_OCL_DELEGATE_URI} for how the actual
	 * evaluation engine is selected at runtime.
	 * </p>
	 * <p>
	 * Default: {@code false} (no OCL invariants are generated)
	 * </p>
	 */
	public static final String OPTION_GENERATE_OCL_CONSTRAINTS = "codec.jsonschema.generateOclConstraints";

	/**
	 * The EMF validation-delegate URI to register generated OCL invariants
	 * under. Only relevant when {@link #OPTION_GENERATE_OCL_CONSTRAINTS} is
	 * enabled.
	 * <p>
	 * Any URI can be supplied as long as a corresponding
	 * {@code EValidator.ValidationDelegate} is registered for it at runtime
	 * (Eclipse OCL, its Pivot dialect, or a third-party engine such as
	 * {@code org.eclipse.fennec.m2x.ocl.engine}, which serves both its native
	 * URI and the legacy Pivot URI).
	 * </p>
	 * <p>
	 * Default: {@value #DEFAULT_OCL_DELEGATE_URI}
	 * </p>
	 */
	public static final String OPTION_OCL_DELEGATE_URI = "codec.jsonschema.oclDelegateUri";

	/**
	 * Default value for {@link #OPTION_OCL_DELEGATE_URI}: the Eclipse OCL
	 * Pivot validation-delegate URI.
	 */
	public static final String DEFAULT_OCL_DELEGATE_URI = "http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot";

}
