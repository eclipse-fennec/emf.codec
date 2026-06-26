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
package org.eclipse.fennec.codec.constants;

/**
 * Constants for codec configuration via property maps and Load/Save options.
 * <p>
 * These constants define the property keys used for programmatic codec configuration.
 * They follow a consistent naming convention:
 * </p>
 * <ul>
 *   <li>Java constant: {@code CODEC_TYPE_STRATEGY}</li>
 *   <li>Property key: {@code "codec.typeStrategy"}</li>
 *   <li>EAnnotation detail key: {@code "typeStrategy"} (without prefix)</li>
 * </ul>
 * <p>
 * Property keys can be used in:
 * </p>
 * <ul>
 *   <li>Load/Save options: {@code resource.load(inputStream, options)}</li>
 *   <li>Resource configuration</li>
 *   <li>ResourceFactory configuration</li>
 *   <li>Jackson Module configuration</li>
 * </ul>
 *
 * @see <a href="docs/codec-v2-spec/16-annotation-reference.md">Spec: Annotation Reference</a>
 * @since 1.0
 */
public final class CodecOptions {

    private CodecOptions() {
        // Utility class - no instantiation
    }

    // ========================================================================
    // COMMON PREFIX
    // ========================================================================

    /**
     * Common prefix for all codec property keys.
     */
    public static final String CODEC_PREFIX = "codec.";

    // ========================================================================
    // LOAD/SAVE OPTIONS
    // Runtime-only options that can be passed to load() and save()
    // ========================================================================

    /**
     * Load option: Type hint for root object.
     * <p>Value: {@code EClass} or EClass URI string</p>
     */
    public static final String CODEC_ROOT_TYPE = "codec.rootType";

    /**
     * Load option: Schema context for NAME strategy.
     * <p>Value: {@code EPackage} or namespace URI string</p>
     */
    public static final String CODEC_ROOT_SCHEMA = "codec.rootSchema";

    /**
     * Load option: Per-feature type hints.
     * <p>Value: {@code Map<EStructuralFeature, EClass>}</p>
     */
    public static final String CODEC_FEATURE_TYPE_HINTS = "codec.featureTypeHints";

    /**
     * Load option: How type hints are treated during deserialization.
     * <p>Values: "HINT" (default), "OVERRIDE"</p>
     */
    public static final String CODEC_TYPE_HINT_MODE = "codec.typeHintMode";

    /**
     * Load option: Type resolution strictness.
     * <p>Values: "LENIENT" (default), "STRICT", "AUTO_DETECT"</p>
     */
    public static final String CODEC_DESERIALIZATION_MODE = "codec.deserializationMode";

    /**
     * Load option: Value readers to register.
     * <p>Value: {@code Collection<CodecValueReader>}</p>
     */
    public static final String CODEC_VALUE_READERS = "codec.valueReaders";

    /**
     * Save option: Value writers to register.
     * <p>Value: {@code Collection<CodecValueWriter>}</p>
     */
    public static final String CODEC_VALUE_WRITERS = "codec.valueWriters";

    /**
     * Load option: Per-feature value reader names.
     * <p>Value: {@code Map<EStructuralFeature, String>}</p>
     */
    public static final String CODEC_FEATURE_VALUE_READERS = "codec.featureValueReaders";

    /**
     * Save option: Per-feature value writer names.
     * <p>Value: {@code Map<EStructuralFeature, String>}</p>
     */
    public static final String CODEC_FEATURE_VALUE_WRITERS = "codec.featureValueWriters";

    /**
     * Load option: Per-feature value reader instances.
     * <p>Value: {@code Map<EStructuralFeature, CodecValueReader>}</p>
     */
    public static final String CODEC_FEATURE_VALUE_READER_INSTANCES = "codec.featureValueReaderInstances";

    /**
     * Save option: Per-feature value writer instances.
     * <p>Value: {@code Map<EStructuralFeature, CodecValueWriter>}</p>
     */
    public static final String CODEC_FEATURE_VALUE_WRITER_INSTANCES = "codec.featureValueWriterInstances";

    /**
     * Load/Save option: Throw on first error.
     * <p>Value: {@code Boolean}, default: false</p>
     */
    public static final String CODEC_FAIL_FAST = "codec.failFast";

    /**
     * Load/Save option: Suppress all warnings.
     * <p>Value: {@code Boolean}, default: false</p>
     */
    public static final String CODEC_SUPPRESS_WARNINGS = "codec.suppressWarnings";

    /**
     * Load/Save option: Suppress warnings by source.
     * <p>Value: {@code Set<String>}</p>
     */
    public static final String CODEC_SUPPRESS_WARNING_SOURCES = "codec.suppressWarningSources";

    /**
     * Load/Save option: Custom diagnostic handler.
     * <p>Value: {@code DiagnosticHandler}</p>
     */
    public static final String CODEC_DIAGNOSTIC_HANDLER = "codec.diagnosticHandler";

    // ========================================================================
    // SCOPE CONFIGURATION KEYS
    // Used to provide per-EClass/EReference/EAttribute configuration
    // ========================================================================

    /**
     * Per-EClass configuration map.
     * <p>Value: {@code Map<EClass, Map<String, Object>>}</p>
     */
    public static final String CODEC_ECLASS_CONFIG = "codec.eClassConfig";

    /**
     * Per-EReference configuration map.
     * <p>Value: {@code Map<EReference, Map<String, Object>>}</p>
     */
    public static final String CODEC_EREFERENCE_CONFIG = "codec.eReferenceConfig";

    /**
     * Per-EAttribute configuration map.
     * <p>Value: {@code Map<EAttribute, Map<String, Object>>}</p>
     */
    public static final String CODEC_EATTRIBUTE_CONFIG = "codec.eAttributeConfig";

    // ========================================================================
    // TYPE CONFIGURATION
    // ========================================================================

    /**
     * Type serialization strategy.
     * <p>Values: "URI" (default), "NAME", "CLASS", "SCHEMA_AND_TYPE", "NUMERIC", "NONE"</p>
     */
    public static final String CODEC_TYPE_STRATEGY = "codec.typeStrategy";

    /**
     * Type serialization format.
     * <p>Values: "PLAIN" (default), "STRUCTURED"</p>
     */
    public static final String CODEC_TYPE_FORMAT = "codec.typeFormat";

    /**
     * JSON key for type field.
     * <p>Default: "_type"</p>
     */
    public static final String CODEC_TYPE_KEY = "codec.typeKey";

    /**
     * Inner name key in STRUCTURED format.
     * <p>Default: "type"</p>
     */
    public static final String CODEC_TYPE_NAME_KEY = "codec.typeNameKey";

    /**
     * Inner schema key in STRUCTURED format.
     * <p>Default: "schema"</p>
     */
    public static final String CODEC_TYPE_SCHEMA_KEY = "codec.typeSchemaKey";

    /**
     * Whether to include type information.
     * <p>Default: true</p>
     */
    public static final String CODEC_TYPE_INCLUDE = "codec.typeInclude";

    /**
     * Custom value reader for type deserialization.
     */
    public static final String CODEC_TYPE_VALUE_READER_NAME = "codec.typeValueReaderName";

    /**
     * Custom value writer for type serialization.
     */
    public static final String CODEC_TYPE_VALUE_WRITER_NAME = "codec.typeValueWriterName";

    /**
     * Strategy scope for type (runtime-only).
     * <p>Values: "ALL" (default), "ROOT_ONLY", "ROOT_CONTAINMENT", "ROOT_NON_CONTAINMENT"</p>
     */
    public static final String CODEC_TYPE_SCOPE = "codec.typeScope";

    /**
     * Format scope for type (runtime-only).
     * <p>Values: "ALL" (default), "ROOT_ONLY", "ROOT_CONTAINMENT", "ROOT_NON_CONTAINMENT"</p>
     */
    public static final String CODEC_TYPE_FORMAT_SCOPE = "codec.typeFormatScope";

    // ========================================================================
    // SUPERTYPE CONFIGURATION
    // ========================================================================

    /**
     * Enable supertype serialization.
     * <p>Default: false</p>
     */
    public static final String CODEC_SUPERTYPE_SERIALIZE = "codec.superTypeSerialize";

    /**
     * JSON key for supertype field.
     * <p>Default: "_superTypes"</p>
     */
    public static final String CODEC_SUPERTYPE_KEY = "codec.superTypeKey";

    /**
     * Which supertypes to include.
     * <p>Values: "ALL" (default), "ALL_EMF", "SINGLE", "NONE"</p>
     */
    public static final String CODEC_SUPERTYPE_STRATEGY = "codec.superTypeStrategy";

    /**
     * Serialize supertypes as array (true) or joined string (false).
     * <p>Default: true</p>
     */
    public static final String CODEC_SUPERTYPE_AS_ARRAY = "codec.superTypeAsArray";

    /**
     * Separator when serializing supertypes as string.
     * <p>Default: ","</p>
     */
    public static final String CODEC_SUPERTYPE_SEPARATOR = "codec.superTypeSeparator";

    /**
     * Supertype serialization format.
     * <p>Values: "PLAIN" (default), "STRUCTURED"</p>
     */
    public static final String CODEC_SUPERTYPE_FORMAT = "codec.superTypeFormat";

    // Note: superTypeSchemaKey not needed - SuperTypeConfig inherits from TypeConfig
    // Note: superTypeNameKey removed - superTypeKey has format-dependent default:
    //       PLAIN format → "_supertype", STRUCTURED format → "supertype"

    /**
     * Custom value reader for supertype deserialization.
     */
    public static final String CODEC_SUPERTYPE_VALUE_READER_NAME = "codec.superTypeValueReaderName";

    /**
     * Custom value writer for supertype serialization.
     */
    public static final String CODEC_SUPERTYPE_VALUE_WRITER_NAME = "codec.superTypeValueWriterName";

    // ========================================================================
    // ID CONFIGURATION
    // ========================================================================

    /**
     * ID serialization strategy.
     * <p>Values: "ID_FIELD" (default), "COMBINED", "NONE"</p>
     */
    public static final String CODEC_ID_STRATEGY = "codec.idStrategy";

    /**
     * ID serialization format.
     * <p>Values: "PLAIN" (default), "STRUCTURED"</p>
     */
    public static final String CODEC_ID_FORMAT = "codec.idFormat";

    /**
     * JSON key for ID field.
     * <p>Default: "_id"</p>
     */
    public static final String CODEC_ID_KEY = "codec.idKey";

    /**
     * Inner value key in STRUCTURED ID format.
     * <p>Default: "id"</p>
     */
    public static final String CODEC_ID_VALUE_KEY = "codec.idValueKey";

    /**
     * Feature names for COMBINED strategy (comma-separated).
     */
    public static final String CODEC_ID_FEATURES = "codec.idFeatures";

    /**
     * Separator for COMBINED ID strategy.
     * <p>Default: "-"</p>
     */
    public static final String CODEC_ID_SEPARATOR = "codec.idSeparator";

    /**
     * Key for separator field in STRUCTURED format.
     * <p>Default: "separator"</p>
     */
    public static final String CODEC_ID_SEPARATOR_KEY = "codec.idSeparatorKey";

    /**
     * Include separator in STRUCTURED output.
     * <p>Default: true</p>
     */
    public static final String CODEC_ID_SERIALIZE_SEPARATOR = "codec.idSerializeSeparator";

    /**
     * ID key mode.
     * <p>Values: "ID_ONLY" (default), "BOTH", "FEATURE_ONLY", "NONE"</p>
     */
    public static final String CODEC_ID_KEY_MODE = "codec.idKeyMode";

    /**
     * ID appears before type in output.
     * <p>Default: false</p>
     */
    public static final String CODEC_ID_ON_TOP = "codec.idOnTop";

    /**
     * Custom value reader for ID deserialization.
     */
    public static final String CODEC_ID_VALUE_READER_NAME = "codec.idValueReaderName";

    /**
     * Custom value writer for ID serialization.
     */
    public static final String CODEC_ID_VALUE_WRITER_NAME = "codec.idValueWriterName";

    /**
     * Strategy scope for ID (runtime-only).
     * <p>Values: "ALL" (default), "ROOT_ONLY", "ROOT_CONTAINMENT", "ROOT_NON_CONTAINMENT"</p>
     */
    public static final String CODEC_ID_SCOPE = "codec.idScope";

    /**
     * Format scope for ID (runtime-only).
     * <p>Values: "ALL" (default), "ROOT_ONLY", "ROOT_CONTAINMENT", "ROOT_NON_CONTAINMENT"</p>
     */
    public static final String CODEC_ID_FORMAT_SCOPE = "codec.idFormatScope";

    // ========================================================================
    // REFERENCE CONFIGURATION
    // ========================================================================

    /**
     * Reference serialization format.
     * <p>Values: "PLAIN" (default), "STRUCTURED"</p>
     */
    public static final String CODEC_REF_FORMAT = "codec.refFormat";

    /**
     * Reference value key in STRUCTURED format.
     * <p>Default: "_ref"</p>
     */
    public static final String CODEC_REF_KEY = "codec.refKey";

    /**
     * Reference type key in STRUCTURED format.
     * <p>Default: "_type"</p>
     */
    public static final String CODEC_REF_TYPE_KEY = "codec.refTypeKey";

    /**
     * Expand (inline) referenced objects.
     * <p>Default: false</p>
     */
    public static final String CODEC_EXPAND = "codec.expand";

    // ========================================================================
    // FEATURE CONFIGURATION
    // ========================================================================

    /**
     * Custom JSON property key for a feature.
     */
    public static final String CODEC_KEY = "codec.key";

    /**
     * Mark feature as transient (not serialized).
     * <p>Default: false</p>
     */
    public static final String CODEC_TRANSIENT = "codec.transient";

    /**
     * Whether to serialize this feature.
     * <p>Default: true</p>
     */
    public static final String CODEC_SERIALIZE = "codec.serialize";

    /**
     * Whether to serialize null values.
     * <p>Default: false</p>
     */
    public static final String CODEC_SERIALIZE_NULL = "codec.serializeNull";

    /**
     * Whether to serialize empty collections.
     * <p>Default: false</p>
     */
    public static final String CODEC_SERIALIZE_EMPTY = "codec.serializeEmpty";

    /**
     * Whether to serialize default values.
     * <p>Default: false</p>
     */
    public static final String CODEC_SERIALIZE_DEFAULTS = "codec.serializeDefaults";

    /**
     * Enum serialization strategy.
     * <p>Values: "LITERAL" (default), "VALUE", "NAME"</p>
     */
    public static final String CODEC_ENUM_SERIALIZATION = "codec.enumSerialization";

    /**
     * Date format pattern (SimpleDateFormat) for serializing/deserializing Date values.
     * <p>Value: {@code String} (e.g., "yyyy-MM-dd'T'HH:mm:ss'Z'"). Default: null (use EMF default)</p>
     */
    public static final String CODEC_DATE_FORMAT = "codec.dateFormat";

    /**
     * Custom value reader for a feature.
     */
    public static final String CODEC_VALUE_READER_NAME = "codec.valueReaderName";

    /**
     * Custom value writer for a feature.
     */
    public static final String CODEC_VALUE_WRITER_NAME = "codec.valueWriterName";

    // ========================================================================
    // DISCRIMINATOR / TYPE MAPPING CONFIGURATION
    // ========================================================================

    /**
     * Type mapping registry ID.
     */
    public static final String CODEC_TYPE_MAP_ID = "codec.typeMapId";

    /**
     * JSON path to discriminator value.
     */
    public static final String CODEC_TYPE_DISCRIMINATOR_PATH = "codec.typeDiscriminatorPath";

    /**
     * Discriminator value for this class.
     */
    public static final String CODEC_TYPE_DISCRIMINATOR = "codec.typeDiscriminator";

    /**
     * Type mappings as nested map.
     * <p>Value: {@code Map<String, String>} (discriminator value to EClass URI)</p>
     */
    public static final String CODEC_TYPE_MAPPINGS = "codec.typeMappings";

    /**
     * Fallback strategy when discriminator value not found.
     * <p>Values: "FALLBACK" (default), "ERROR", "SKIP"</p>
     */
    public static final String CODEC_FALLBACK_STRATEGY = "codec.fallbackStrategy";

    /**
     * Explicit fallback EClass URI.
     */
    public static final String CODEC_FALLBACK_ECLASS = "codec.fallbackEClass";

    // ========================================================================
    // CODEC-WIDE DEFAULTS
    // ========================================================================

    /**
     * Codec-wide default serialization format.
     * <p>Values: "PLAIN" (default), "STRUCTURED"</p>
     */
    public static final String CODEC_FORMAT = "codec.format";

    /**
     * Use numeric classifier IDs instead of names.
     * <p>Default: false</p>
     */
    public static final String CODEC_USE_NUMERIC_IDS = "codec.useNumericIds";

    /**
     * Maximum depth for nested reference expansion.
     * <p>Default: 1</p>
     */
    public static final String CODEC_EXPAND_DEPTH = "codec.expandDepth";

    /**
     * Skip bidirectional references when expanding.
     * <p>Default: true</p>
     */
    public static final String CODEC_EXPAND_IGNORE_BIDIRECTIONAL = "codec.expandIgnoreBidirectional";

    /**
     * Enable smart compression (omit type when inferable).
     * <p>Default: false</p>
     */
    public static final String CODEC_SMART_COMPRESSION = "codec.smartCompression";

    /**
     * Enable cross-package annotation inheritance.
     * <p>Default: false</p>
     */
    public static final String CODEC_INHERIT = "codec.inherit";

    // ========================================================================
    // SECURITY / LIMITS
    // ========================================================================

    /**
     * Maximum payload size in bytes for format providers that buffer the entire
     * input stream in memory (e.g., BSON).
     * <p>
     * Prevents denial-of-service attacks via oversized payloads that would cause
     * {@code OutOfMemoryError}.
     * </p>
     * <p>Value: {@code Long} or {@code Integer}, default: 104,857,600 (100 MB)</p>
     */
    public static final String CODEC_MAX_PAYLOAD_SIZE = "codec.maxPayloadSize";

    /**
     * Default maximum payload size: 100 MB.
     */
    public static final long DEFAULT_MAX_PAYLOAD_SIZE = 100L * 1024 * 1024;

    /**
     * Save option: how to react when a format provider's
     * {@code validateSaveOptions(URI, options)} returns one or more inconsistency
     * warnings.
     * <p>
     * Currently used to surface mismatches between the resource URI and the
     * save-time option values (e.g. a {@code .RData} URI saved with
     * {@code codec.rlang.dataframePerFile=true}, which would produce a ZIP
     * archive named {@code .RData}).
     * <p>
     * When {@code false} (default), warnings are logged at WARN level via
     * {@code java.util.logging} and the save proceeds.
     * <p>
     * When {@code true}, the same messages are aggregated and an
     * {@link IllegalStateException} is thrown <em>before</em> the writer
     * delegate runs, aborting the save.
     * <p>
     * Value: {@code Boolean} or {@code "true"}/{@code "false"}. Default:
     * {@code false}.
     *
     * @since 2026-06
     */
    public static final String CODEC_THROW_ON_VALIDATION_WARNINGS =
            "codec.throwOnValidationWarnings";

    /**
     * Internal: {@code CodecValueRegistry} instance forwarded from {@code CodecResource} to
     * format delegates (e.g. tabular) so that named value writers can be resolved at
     * serialization time. Not a user-facing option — set automatically by the infrastructure.
     */
    public static final String INTERNAL_VALUE_REGISTRY = "codec.internal.valueRegistry";
}
