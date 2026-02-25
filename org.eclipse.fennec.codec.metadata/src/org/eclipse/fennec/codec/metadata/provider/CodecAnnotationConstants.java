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
package org.eclipse.fennec.codec.metadata.provider;

import java.util.Set;

/**
 * Constants for codec-related EAnnotation sources and detail keys.
 * <p>
 * These constants define the annotation source and detail keys used in EMF models
 * to configure codec serialization and deserialization behavior. EAnnotations are
 * the standard EMF mechanism for attaching metadata to model elements (EPackage,
 * EClass, EStructuralFeature).
 * </p>
 *
 * <h2>Unified Annotation Source</h2>
 * <p>
 * All codec annotations use a single source URI: {@code http://eclipse.org/fennec/codec}.
 * Configuration is specified entirely through detail key-value pairs. This allows
 * combining multiple configurations (ID, type, supertype) in a single annotation.
 * </p>
 *
 * <h2>Example in Ecore XML</h2>
 * <pre>{@code
 * <eClassifiers xsi:type="ecore:EClass" name="Person">
 *   <eAnnotations source="http://eclipse.org/fennec/codec">
 *     <!-- ID configuration -->
 *     <details key="idStrategy" value="ID_FIELD"/>
 *     <details key="idKey" value="_id"/>
 *     <!-- Type configuration -->
 *     <details key="typeStrategy" value="URI"/>
 *     <details key="typeKey" value="_type"/>
 *   </eAnnotations>
 * </eClassifiers>
 * }</pre>
 *
 * <h2>MAPPED Type Strategy Example</h2>
 * <pre>{@code
 * <!-- Base class - defines the mapping -->
 * <eClassifiers xsi:type="ecore:EClass" name="Device" abstract="true">
 *   <eAnnotations source="http://eclipse.org/fennec/codec">
 *     <details key="typeMapId" value="iot-sensors"/>
 *     <details key="typeKey" value="_type"/>
 *   </eAnnotations>
 * </eClassifiers>
 *
 * <!-- Concrete class - registers with the mapping -->
 * <eClassifiers xsi:type="ecore:EClass" name="TemperatureSensor" eSuperTypes="#//Device">
 *   <eAnnotations source="http://eclipse.org/fennec/codec">
 *     <details key="typeMapId" value="iot-sensors"/>
 *     <details key="typeDiscriminator" value="temp-sensor"/>
 *   </eAnnotations>
 * </eClassifiers>
 * }</pre>
 *
 * <h2>Annotation Hierarchy</h2>
 * <p>
 * Annotations can be placed at different levels:
 * </p>
 * <ul>
 *   <li><b>EClass level:</b> Defines default behavior for all instances of that class</li>
 *   <li><b>EStructuralFeature level:</b> Overrides or extends class-level settings for specific features</li>
 *   <li><b>EReference level:</b> Can define type resolution for polymorphic references</li>
 * </ul>
 *
 * @author Mark Hoffmann
 * @since 2025-12-04
 */
public final class CodecAnnotationConstants {

    private CodecAnnotationConstants() {
        // Constants class - prevent instantiation
    }

    // ========================================================================
    // ANNOTATION SOURCE
    // Single unified source for all codec annotations
    // ========================================================================

    /**
     * The unified annotation source for all codec configuration.
     * <p>
     * All codec-related configuration is placed in a single EAnnotation with this
     * source URI. Configuration is specified through detail key-value pairs.
     * </p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * <eAnnotations source="http://eclipse.org/fennec/codec">
     *   <!-- ID configuration -->
     *   <details key="idStrategy" value="ID_FIELD"/>
     *   <details key="idKey" value="_id"/>
     *   <!-- Type configuration -->
     *   <details key="typeStrategy" value="URI"/>
     *   <details key="typeKey" value="_type"/>
     *   <!-- SuperType configuration -->
     *   <details key="superTypeSerialize" value="true"/>
     * </eAnnotations>
     * }</pre>
     *
     * @see org.eclipse.fennec.codec.constants.AnnotationSources#CODEC
     */
    public static final String CODEC_SOURCE = "http://eclipse.org/fennec/codec";

    /**
     * Annotation source prefix for type mapping registries.
     * <p>
     * The full source is: {@code http://eclipse.org/fennec/codec/typeMapping/{mapId}}
     * </p>
     *
     * @see org.eclipse.fennec.codec.constants.AnnotationSources#TYPE_MAPPING_PREFIX
     */
    public static final String TYPE_MAPPING_SOURCE_PREFIX = "http://eclipse.org/fennec/codec/typeMapping/";

    /**
     * Annotation source for inline type mappings on EReferences.
     *
     * @see org.eclipse.fennec.codec.constants.AnnotationSources#INLINE_MAPPING
     */
    public static final String INLINE_MAPPING_SOURCE = "http://eclipse.org/fennec/codec/inlineMapping";

    // ========================================================================
    // DETAIL KEYS - Unified format
    // All keys use consistent prefixes: id*, type*, superType*, value*
    // ========================================================================

    // ------------------------------------------------------------------------
    // General detail keys
    // ------------------------------------------------------------------------

    /**
     * Detail key for marking a feature as transient (not serialized).
     * Value: "true" or "false". Default: "false".
     * @deprecated Use {@link #KEY_IGNORE} instead for bidirectional control.
     */
    @Deprecated()
    public static final String KEY_TRANSIENT = "transient";

    /**
     * Detail key for ignoring a feature in both serialization and deserialization.
     * Value: "true" or "false". Default: "false".
     */
    public static final String KEY_IGNORE = "ignore";

    /**
     * Detail key for ignoring a feature during deserialization only.
     * Value: "true" or "false". Default: "false".
     */
    public static final String KEY_IGNORE_READ = "ignoreRead";

    /**
     * Detail key for ignoring a feature during serialization only.
     * Value: "true" or "false". Default: "false".
     */
    public static final String KEY_IGNORE_WRITE = "ignoreWrite";

    /**
     * Detail key for forcing deserialization of EMF transient/volatile features.
     * Value: "true" or "false". Default: "false".
     */
    public static final String KEY_FORCE_READ = "forceRead";

    /**
     * Detail key for forcing serialization of EMF transient/volatile/derived features.
     * Value: "true" or "false". Default: "false".
     */
    public static final String KEY_FORCE_WRITE = "forceWrite";

    /**
     * Detail key for enabling cross-package annotation inheritance.
     * Value: "true" or "false". Default: "false".
     */
    public static final String KEY_INHERIT = "inherit";

    /**
     * Detail key for strict handling of unknown JSON fields during deserialization.
     * When true, unknown fields cause ERROR instead of WARNING.
     * Value: "true" or "false". Default: "false" (LENIENT).
     * Valid on: EClass (Global is runtime-only via CodecConfig).
     */
    public static final String KEY_STRICT_ON_UNKNOWN = "strictOnUnknown";

    /**
     * Detail key for strict handling of missing required features during deserialization.
     * When true, missing required features cause ERROR instead of WARNING.
     * Value: "true" or "false". Default: "false" (LENIENT).
     * Valid on: EClass (Global is runtime-only via CodecConfig).
     */
    public static final String KEY_STRICT_ON_MISSING = "strictOnMissing";

    /**
     * Detail key for merging type + id (+ supertype) into a single metadata object.
     * Only applies when both typeFormat=STRUCTURED and idFormat=STRUCTURED.
     * Value: "true" or "false". Default: "false".
     * Valid on: EClass (Global is runtime-only via CodecConfig).
     */
    public static final String KEY_METADATA_MERGE = "metadataMerge";

    /**
     * Detail key for the JSON key used for the merged metadata object.
     * Only meaningful when metadataMerge=true.
     * Value: string. Default: "_metadata".
     * Valid on: EClass (Global is runtime-only via CodecConfig).
     */
    public static final String KEY_METADATA_KEY = "metadataKey";

    // ------------------------------------------------------------------------
    // ID configuration detail keys (prefix: id*)
    // ------------------------------------------------------------------------

    /**
     * Detail key for ID serialization strategy.
     * Values: "ID_FIELD", "COMBINED", "NONE". Default: "ID_FIELD".
     */
    public static final String KEY_ID_STRATEGY = "idStrategy";

    /**
     * Detail key for ID property name in serialized output.
     * Default: "_id".
     */
    public static final String KEY_ID_KEY = "idKey";

    /**
     * Detail key for separator in combined IDs.
     * Used when idStrategy is COMBINED. Default: "-".
     */
    public static final String KEY_ID_SEPARATOR = "idSeparator";

    /**
     * Detail key for features used in combined ID.
     * Comma-separated list of feature names. Used when idStrategy is COMBINED.
     */
    public static final String KEY_ID_FEATURES = "idFeatures";

    /**
     * Detail key for custom ID value reader name.
     */
    public static final String KEY_ID_VALUE_READER_NAME = "idValueReaderName";

    /**
     * Detail key for custom ID value writer name.
     */
    public static final String KEY_ID_VALUE_WRITER_NAME = "idValueWriterName";

    /**
     * Detail key for ID serialization format.
     * Values: "PLAIN", "STRUCTURED". Default: "PLAIN".
     */
    public static final String KEY_ID_FORMAT = "idFormat";

    /**
     * Detail key for ID key mode.
     * Values: "ID_ONLY", "BOTH", "FEATURE_ONLY". Default: "ID_ONLY".
     */
    public static final String KEY_ID_KEY_MODE = "idKeyMode";

    /**
     * Detail key for whether ID appears before type in output.
     * Value: "true" or "false". Default: "true".
     */
    public static final String KEY_ID_ON_TOP = "idOnTop";

    /**
     * Detail key for whether to serialize separator in STRUCTURED ID format.
     * Value: "true" or "false". Default: "true".
     */
    public static final String KEY_ID_SERIALIZE_SEPARATOR = "idSerializeSeparator";

    /**
     * Detail key for separator field name in STRUCTURED ID format.
     * Default: "separator".
     */
    public static final String KEY_ID_SEPARATOR_KEY = "idSeparatorKey";

    /**
     * Detail key for ID value field name in STRUCTURED ID format.
     * Default: "id".
     */
    public static final String KEY_ID_VALUE_KEY = "idValueKey";

    /**
     * Detail key for ID strategy scope (runtime-only, not valid in EAnnotations).
     * Values: "ALL", "ROOT_ONLY", "ROOT_CONTAINMENT", "ROOT_NON_CONTAINMENT". Default: "ALL".
     * <p>
     * This is a runtime-only property. If found in an EAnnotation, it is ignored
     * with a WARNING diagnostic (ID-V11).
     * </p>
     */
    public static final String KEY_ID_SCOPE = "idScope";

    /**
     * Detail key for ID format scope (runtime-only, not valid in EAnnotations).
     * Values: "ALL", "ROOT_ONLY", "ROOT_CONTAINMENT", "ROOT_NON_CONTAINMENT". Default: "ALL".
     * <p>
     * This is a runtime-only property. If found in an EAnnotation, it is ignored
     * with a WARNING diagnostic (ID-V12).
     * </p>
     */
    public static final String KEY_ID_FORMAT_SCOPE = "idFormatScope";

    // ------------------------------------------------------------------------
    // Type configuration detail keys (prefix: type*)
    // ------------------------------------------------------------------------

    /**
     * Detail key for type serialization strategy.
     * Values: "URI", "NAME", "CLASS", "NUMERIC", "MAPPED". Default: "URI".
     */
    public static final String KEY_TYPE_STRATEGY = "typeStrategy";

    /**
     * Detail key for type property name in serialized output.
     * Default: "_type".
     */
    public static final String KEY_TYPE_KEY = "typeKey";

    /**
     * Detail key for type mapping ID (MAPPED strategy).
     * <p>
     * Identifies a specific type discrimination context. Both base class and
     * concrete classes must use the same mapId to participate in the mapping.
     * </p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * <!-- Base class defines the mapping -->
     * <details key="typeMapId" value="iot-sensors"/>
     *
     * <!-- Concrete class registers with the same mapping -->
     * <details key="typeMapId" value="iot-sensors"/>
     * <details key="typeDiscriminator" value="temp-sensor"/>
     * }</pre>
     */
    public static final String KEY_TYPE_MAP_ID = "typeMapId";

    /**
     * Detail key for type discriminator value (MAPPED strategy).
     * <p>
     * Registers a concrete class with a discriminator value in the mapping
     * identified by {@link #KEY_TYPE_MAP_ID}.
     * </p>
     */
    public static final String KEY_TYPE_DISCRIMINATOR = "typeDiscriminator";

    /**
     * Detail key for feature path to discriminator value (MAPPED strategy).
     * <p>
     * Specifies which feature (or nested path like "info.sensorType") contains
     * the discriminator value. Used on base class.
     * </p>
     */
    public static final String KEY_TYPE_DISCRIMINATOR_PATH = "typeDiscriminatorPath";

    /**
     * Detail key prefix for static type discriminator mappings.
     * <p>
     * Used to define complete type mappings upfront on the base class.
     * Format: typeDiscriminator.{value} = {EClass URI}
     * </p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * <details key="typeDiscriminator.temp-sensor" value="http://example.org#//TemperatureSensor"/>
     * <details key="typeDiscriminator.humidity-sensor" value="http://example.org#//HumiditySensor"/>
     * }</pre>
     */
    public static final String KEY_TYPE_DISCRIMINATOR_PREFIX = KEY_TYPE_DISCRIMINATOR + ".";

    /**
     * Detail key for custom type value reader name.
     */
    public static final String KEY_TYPE_VALUE_READER_NAME = "typeValueReaderName";

    /**
     * Detail key for custom type value writer name.
     */
    public static final String KEY_TYPE_VALUE_WRITER_NAME = "typeValueWriterName";

    /**
     * Detail key for type serialization format.
     * Values: "PLAIN", "STRUCTURED". Default: "PLAIN".
     */
    public static final String KEY_TYPE_FORMAT = "typeFormat";

    /**
     * Detail key for schema key in STRUCTURED/SCHEMA_AND_TYPE format.
     * Default: "schema".
     */
    public static final String KEY_TYPE_SCHEMA_KEY = "typeSchemaKey";

    /**
     * Detail key for name key in STRUCTURED format.
     * Default: "name".
     */
    public static final String KEY_TYPE_NAME_KEY = "typeNameKey";

    /**
     * Detail key for type strategy scope (runtime-only, not valid in EAnnotations).
     * Values: "ALL", "ROOT_ONLY", "ROOT_CONTAINMENT", "ROOT_NON_CONTAINMENT". Default: "ALL".
     * <p>
     * This is a runtime-only property. If found in an EAnnotation, it is ignored
     * with a WARNING diagnostic (T-V3).
     * </p>
     */
    public static final String KEY_TYPE_SCOPE = "typeScope";

    /**
     * Detail key for type format scope (runtime-only, not valid in EAnnotations).
     * Values: "ALL", "ROOT_ONLY", "ROOT_CONTAINMENT", "ROOT_NON_CONTAINMENT". Default: "ALL".
     * <p>
     * This is a runtime-only property. If found in an EAnnotation, it is ignored
     * with a WARNING diagnostic (T-V4).
     * </p>
     */
    public static final String KEY_TYPE_FORMAT_SCOPE = "typeFormatScope";

    /**
     * Detail key for deprecated type include flag.
     * <p>
     * DEPRECATED: Use {@code typeStrategy=NONE} instead.
     * If found, a WARNING diagnostic is generated (T-V30).
     * If both typeInclude and typeStrategy are set, typeStrategy takes precedence (T-V31).
     * </p>
     */
    public static final String KEY_TYPE_INCLUDE = "typeInclude";

    /**
     * Detail key for fallback strategy when discriminator value is not found.
     * Values: "FALLBACK", "ERROR", "SKIP". Default: "FALLBACK".
     * <p>
     * Used with discriminator-based type resolution (Type Mapping Registry or Inline Mapping).
     * </p>
     */
    public static final String KEY_FALLBACK_STRATEGY = "fallbackStrategy";

    /**
     * Detail key for explicit fallback EClass URI when discriminator value is not found.
     * <p>
     * Used when fallbackStrategy is "FALLBACK". If not specified, the fallback chain
     * continues with feature type hint, then reference type.
     * </p>
     */
    public static final String KEY_FALLBACK_ECLASS = "fallbackEClass";

    // ------------------------------------------------------------------------
    // SuperType configuration detail keys (prefix: superType*)
    // ------------------------------------------------------------------------

    /**
     * Detail key for enabling supertype serialization.
     * Value: "true" or "false". Default: "false".
     */
    public static final String KEY_SUPERTYPE_SERIALIZE = "superTypeSerialize";

    /**
     * Detail key for supertype property name in serialized output.
     * Default: "_superTypes".
     */
    public static final String KEY_SUPERTYPE_KEY = "superTypeKey";

    /**
     * Detail key for supertype serialization strategy.
     * Values: "ALL", "ALL_EMF", "SINGLE", "NONE". Default: "ALL".
     */
    public static final String KEY_SUPERTYPE_STRATEGY = "superTypeStrategy";

    /**
     * Detail key for serializing supertypes as array vs string.
     * Value: "true" or "false". Default: "true".
     */
    public static final String KEY_SUPERTYPE_AS_ARRAY = "superTypeAsArray";

    /**
     * Detail key for separator when serializing supertypes as string.
     * Default: ",".
     */
    public static final String KEY_SUPERTYPE_SEPARATOR = "superTypeSeparator";

    /**
     * Detail key for custom supertype value writer name.
     */
    public static final String KEY_SUPERTYPE_WRITER_NAME = "superTypeWriterName";

    /**
     * Detail key for supertype serialization format.
     * Values: "PLAIN", "STRUCTURED". Default: "PLAIN".
     */
    public static final String KEY_SUPERTYPE_FORMAT = "superTypeFormat";

    // Note: superTypeSchemaKey not needed - SuperTypeConfig inherits from TypeConfig
    // Note: superTypeNameKey removed - superTypeKey has format-dependent default:
    //       PLAIN format → "_supertype", STRUCTURED format → "supertype"

    // ------------------------------------------------------------------------
    // Reference configuration detail keys (prefix: ref*)
    // ------------------------------------------------------------------------

    /**
     * Detail key for reference serialization format.
     * Values: "PLAIN", "STRUCTURED". Default: "PLAIN".
     */
    public static final String KEY_REF_FORMAT = "refFormat";

    /**
     * Detail key for reference value key in STRUCTURED format.
     * Default: "_ref".
     */
    public static final String KEY_REF_KEY = "refKey";

    /**
     * Detail key for reference type key in STRUCTURED format.
     * Default: "_type".
     */
    public static final String KEY_REF_TYPE_KEY = "refTypeKey";

    /**
     * Detail key for expanding (inlining) referenced objects.
     * Value: "true" or "false". Default: "false".
     */
    public static final String KEY_EXPAND = "expand";

    /**
     * Detail key prefix for inline type mappings on references.
     * <p>
     * Used to define discriminator-to-EClass mappings directly on an EReference.
     * Format: inlineMapping.{discriminatorValue} = {EClass URI}
     * </p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * <eStructuralFeatures xsi:type="ecore:EReference" name="contacts">
     *   <eAnnotations source="http://eclipse.org/fennec/codec">
     *     <details key="inlineMapping.friend" value="http://example.org#//Friend"/>
     *     <details key="inlineMapping.enemy" value="http://example.org#//Enemy"/>
     *   </eAnnotations>
     * </eStructuralFeatures>
     * }</pre>
     */
    public static final String KEY_INLINE_MAPPING_PREFIX = "inlineMapping.";

    // ------------------------------------------------------------------------
    // Feature configuration detail keys
    // ------------------------------------------------------------------------

    /**
     * Detail key for custom JSON property name override on a feature.
     * <p>
     * If specified, this key is used instead of the feature name or ExtendedMetaData name.
     * This is the highest priority source for the JSON property key.
     * </p>
     */
    public static final String KEY_KEY = "key";

    /**
     * Detail key for custom value reader name on a feature.
     */
    public static final String KEY_VALUE_READER_NAME = "valueReaderName";

    /**
     * Detail key for custom value writer name on a feature.
     */
    public static final String KEY_VALUE_WRITER_NAME = "valueWriterName";

    /**
     * Detail key for whether to serialize this feature.
     * Value: "true" or "false". Default: "true".
     * @deprecated Use {@link #KEY_IGNORE} instead. serialize=false maps to ignore=true.
     */
    @Deprecated()
    public static final String KEY_SERIALIZE = "serialize";

    /**
     * Detail key for whether to serialize null values for this feature.
     * Value: "true" or "false". Default: "false" (codec default).
     */
    public static final String KEY_SERIALIZE_NULL = "serializeNull";

    /**
     * Detail key for whether to serialize empty collections for this feature.
     * Value: "true" or "false". Default: "false" (codec default).
     */
    public static final String KEY_SERIALIZE_EMPTY = "serializeEmpty";

    /**
     * Detail key for whether to serialize default values for this feature.
     * Value: "true" or "false". Default: "false" (codec default).
     */
    public static final String KEY_SERIALIZE_DEFAULTS = "serializeDefaults";

    /**
     * Detail key for enum serialization strategy on enum-typed features.
     * Values: "LITERAL", "VALUE", "NAME". Default: "LITERAL".
     */
    public static final String KEY_ENUM_SERIALIZATION = "enumSerialization";

    // ========================================================================
    // STRATEGY VALUES
    // These are valid values for the strategy detail keys
    // ========================================================================

    /**
     * ID strategy: Use a single designated ID field from the EClass.
     * <p>
     * The codec looks for an EAttribute marked as ID (via ecore:id="true")
     * or the first suitable attribute.
     * </p>
     */
    public static final String STRATEGY_ID_FIELD = "ID_FIELD";

    /**
     * ID strategy: Combine multiple feature values to create a composite ID.
     * <p>
     * Feature values are concatenated using the separator (default: "-").
     * Useful when no single field uniquely identifies an object.
     * </p>
     */
    public static final String STRATEGY_COMBINED = "COMBINED";

    /**
     * ID strategy: No ID serialization.
     * <p>
     * The object is serialized without an _id field. Use when identity
     * is not needed or is handled externally.
     * </p>
     */
    public static final String STRATEGY_NONE = "NONE";

    /**
     * Type strategy: Use the simple EClass name.
     * <p>
     * Produces compact output like {@code "_type": "Person"}.
     * Requires all class names to be unique within the context.
     * </p>
     */
    public static final String STRATEGY_NAME = "NAME";

    /**
     * Type strategy: Use the fully qualified Java class name.
     * <p>
     * Produces output like {@code "_type": "org.example.model.impl.PersonImpl"}.
     * Useful for Java-centric serialization.
     * </p>
     */
    public static final String STRATEGY_CLASS = "CLASS";

    /**
     * Type strategy: Use the EClass URI.
     * <p>
     * Produces output like {@code "_type": "http://example.org/model#//Person"}.
     * Most precise and portable across different runtime environments.
     * Enables deserialization without prior knowledge of the EPackage.
     * </p>
     */
    public static final String STRATEGY_URI = "URI";

    /**
     * Type strategy: Use a mapped discriminator value.
     * <p>
     * Instead of storing type information explicitly, uses an existing field
     * value to determine the concrete type. Configuration uses:
     * </p>
     * <ul>
     *   <li>{@link #KEY_TYPE_MAP_ID} - identifies the mapping context</li>
     *   <li>{@link #KEY_TYPE_DISCRIMINATOR_PATH} - which field contains the discriminator (base class)</li>
     *   <li>{@link #KEY_TYPE_DISCRIMINATOR} - the discriminator value (concrete classes)</li>
     *   <li>{@link #KEY_TYPE_DISCRIMINATOR_PREFIX} - static mappings (base class)</li>
     * </ul>
     * <p>
     * The MetadataService builds a reverse lookup map from discriminator values
     * to EClasses, enabling dynamic polymorphic deserialization.
     * </p>
     *
     * <h3>Example</h3>
     * <pre>{@code
     * <!-- Base class -->
     * <eAnnotations source="http://eclipse.org/fennec/codec">
     *   <details key="typeMapId" value="iot-sensors"/>
     *   <details key="typeKey" value="_type"/>
     * </eAnnotations>
     *
     * <!-- Concrete class -->
     * <eAnnotations source="http://eclipse.org/fennec/codec">
     *   <details key="typeMapId" value="iot-sensors"/>
     *   <details key="typeDiscriminator" value="temp-sensor"/>
     * </eAnnotations>
     * }</pre>
     */
    public static final String STRATEGY_MAPPED = "MAPPED";

    /**
     * Supertype strategy: Serialize all supertypes in the hierarchy.
     * <p>
     * For a class hierarchy A → B → C, serializing C would include [B, A].
     * This enables queries at any level of the hierarchy in document databases.
     * </p>
     */
    public static final String STRATEGY_SUPERTYPE_ALL = "ALL";

    /**
     * Supertype strategy: Serialize only the immediate (direct) supertype.
     * <p>
     * For a class hierarchy A → B → C, serializing C would only include [B].
     * More compact but limits querying to direct parents only.
     * </p>
     */
    public static final String STRATEGY_SUPERTYPE_SINGLE = "SINGLE";

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Checks if an annotation source is the codec source.
     *
     * @param source the annotation source to check
     * @return true if the source equals {@link #CODEC_SOURCE}
     */
    public static boolean isCodecAnnotation(String source) {
        return CODEC_SOURCE.equals(source);
    }

    /**
     * Extracts the discriminator value from a static type discriminator key.
     * <p>
     * For example, given {@code "typeDiscriminator.Dragino_LSE01"}, returns {@code "Dragino_LSE01"}.
     * </p>
     *
     * @param key the detail key (e.g., "typeDiscriminator.Dragino_LSE01")
     * @return the discriminator value, or null if the key is not a static discriminator key
     */
    public static String extractStaticDiscriminatorValue(String key) {
        if (key == null || !key.startsWith(KEY_TYPE_DISCRIMINATOR_PREFIX)) {
            return null;
        }
        String value = key.substring(KEY_TYPE_DISCRIMINATOR_PREFIX.length());
        return value.isEmpty() ? null : value;
    }

    /**
     * Extracts the discriminator value from an inline mapping key.
     * <p>
     * For example, given {@code "inlineMapping.friend"}, returns {@code "friend"}.
     * </p>
     *
     * @param key the detail key (e.g., "inlineMapping.friend")
     * @return the discriminator value, or null if the key is not an inline mapping key
     */
    public static String extractInlineMappingValue(String key) {
        if (key == null || !key.startsWith(KEY_INLINE_MAPPING_PREFIX)) {
            return null;
        }
        String value = key.substring(KEY_INLINE_MAPPING_PREFIX.length());
        return value.isEmpty() ? null : value;
    }

    /**
     * Checks if the given key is an inline mapping key.
     *
     * @param key the detail key to check
     * @return true if the key starts with "inlineMapping."
     */
    public static boolean isInlineMappingKey(String key) {
        return key != null && key.startsWith(KEY_INLINE_MAPPING_PREFIX);
    }

    /**
     * Checks if the given key is a static discriminator key.
     *
     * @param key the detail key to check
     * @return true if the key starts with "typeDiscriminator."
     */
    public static boolean isStaticDiscriminatorKey(String key) {
        return key != null && key.startsWith(KEY_TYPE_DISCRIMINATOR_PREFIX);
    }

    // ========================================================================
    // Dedicated annotation source helpers
    // ========================================================================

    /**
     * Known configuration keys for {@code typeMapping/{mapId}} annotations.
     * Any key NOT in this set is treated as a discriminator-to-EClass mapping entry.
     */
    public static final Set<String> TYPE_MAPPING_KNOWN_KEYS = Set.of(
            KEY_TYPE_DISCRIMINATOR_PATH,
            KEY_TYPE_DISCRIMINATOR,
            KEY_FALLBACK_STRATEGY,
            KEY_FALLBACK_ECLASS
    );

    /**
     * Known configuration keys for {@code inlineMapping} annotations.
     * Any key NOT in this set is treated as a discriminator-to-EClass mapping entry.
     */
    public static final Set<String> INLINE_MAPPING_KNOWN_KEYS = Set.of(
            KEY_FALLBACK_STRATEGY,
            KEY_FALLBACK_ECLASS
    );

    /**
     * Extracts the mapId from a {@code typeMapping/{mapId}} annotation source URI.
     *
     * @param source the annotation source (e.g., "http://eclipse.org/fennec/codec/typeMapping/iot-sensors")
     * @return the mapId (e.g., "iot-sensors"), or null if the source is not a typeMapping source
     */
    public static String extractMapIdFromSource(String source) {
        if (source == null || !source.startsWith(TYPE_MAPPING_SOURCE_PREFIX)) {
            return null;
        }
        String mapId = source.substring(TYPE_MAPPING_SOURCE_PREFIX.length());
        return mapId.isEmpty() ? null : mapId;
    }

    /**
     * Checks if the given annotation source is a typeMapping source.
     *
     * @param source the annotation source to check
     * @return true if the source starts with the typeMapping prefix
     */
    public static boolean isTypeMappingSource(String source) {
        return source != null && source.startsWith(TYPE_MAPPING_SOURCE_PREFIX);
    }
}
