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
package org.eclipse.fennec.codec.config;

import static org.eclipse.fennec.codec.config.ConfigDirection.READ;
import static org.eclipse.fennec.codec.config.ConfigDirection.WRITE;
import static org.eclipse.fennec.codec.config.ConfigLevel.ECLASS;
import static org.eclipse.fennec.codec.config.ConfigLevel.EPACKAGE;
import static org.eclipse.fennec.codec.config.ConfigLevel.FEATURE;
import static org.eclipse.fennec.codec.config.ConfigLevel.GLOBAL;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All codec configuration properties with their metadata.
 * <p>
 * Each property defines:
 * <ul>
 *   <li>{@link #getKey()} - The property key (used in annotations and property maps)</li>
 *   <li>{@link #getType()} - The Java type of the property value</li>
 *   <li>{@link #getDefaultValue()} - The default value when not configured</li>
 *   <li>{@link #getValidLevels()} - Which levels this property can be configured at</li>
 *   <li>{@link #getDirections()} - Which directions this property applies to</li>
 *   <li>{@link #getFutureDirections()} - Directions marked as future/potential</li>
 * </ul>
 * <p>
 * Direction notation from spec:
 * <ul>
 *   <li><b>RW</b> - Both read and write (primary)</li>
 *   <li><b>R</b> - Read only</li>
 *   <li><b>W</b> - Write only</li>
 *   <li><b>(R)W</b> - Write primary, read is future/potential</li>
 *   <li><b>R(W)</b> - Read primary, write is future/potential</li>
 * </ul>
 *
 * @see ConfigLevel
 * @see ConfigDirection
 * @see ConfigurationResolver
 */
public enum ConfigProperty {

    // ========================================================================
    // Type Properties (11.3)
    // ========================================================================

    TYPE_STRATEGY("typeStrategy", String.class, "URI",
        levels(GLOBAL, ECLASS, EPACKAGE, FEATURE), directions(READ, WRITE)),

    TYPE_KEY("typeKey", String.class, "_type",
        levels(GLOBAL, ECLASS, EPACKAGE, FEATURE), directions(READ, WRITE)),

    TYPE_FORMAT("typeFormat", String.class, "PLAIN",
        levels(GLOBAL, ECLASS, EPACKAGE, FEATURE), directions(READ, WRITE)),

    TYPE_INCLUDE("typeInclude", Boolean.class, true,
        levels(GLOBAL, ECLASS, FEATURE), directions(READ, WRITE)),

    TYPE_SCHEMA_KEY("typeSchemaKey", String.class, "schema",
        levels(GLOBAL, ECLASS, EPACKAGE, FEATURE), directions(READ, WRITE)),

    TYPE_NAME_KEY("typeNameKey", String.class, "type",
        levels(GLOBAL, ECLASS, EPACKAGE, FEATURE), directions(READ, WRITE)),

    TYPE_SCOPE("typeScope", String.class, "ALL",
        levels(GLOBAL), directions(READ, WRITE)),

    TYPE_FORMAT_SCOPE("typeFormatScope", String.class, "ALL",
        levels(GLOBAL), directions(READ, WRITE)),

    TYPE_VALUE_READER_NAME("typeValueReaderName", String.class, null,
        levels(GLOBAL, ECLASS), directions(READ)),

    TYPE_VALUE_WRITER_NAME("typeValueWriterName", String.class, null,
        levels(GLOBAL, ECLASS), directions(WRITE)),

    /**
     * Opt-in for writing the in-band EPackage fingerprint (issue #73, B.1).
     * <p>
     * {@code NONE} (default) writes none; {@code FIRST_TOUCH} writes it at the first
     * occurrence of each distinct package instance in document order. Reading is
     * <b>always</b> liberal and does not consult this property — hence WRITE only.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/06-type.md#8-in-band-epackage-fingerprint">Spec 06 §8</a>
     */
    FINGERPRINT_MODE("fingerprintMode", String.class, "NONE",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE)),

    /**
     * Key carrying the in-band EPackage fingerprint (issue #73, B.1).
     * <p>
     * The configured value is the <b>inner</b> (unprefixed) key used inside a STRUCTURED
     * type object; the PLAIN sibling derives from it by prefixing {@code _}. Values already
     * starting with {@code _} or {@code @} are taken as-is.
     * </p>
     * <p>
     * <b>On the read side this key may only come from caller-side levels</b> (options,
     * resource, factory, module), never from a model annotation, and the default key is
     * always accepted in addition. Reading has to know the key before the model version is
     * selected, but an annotation-configured key only exists after that selection — a
     * genuine cycle, broken here deliberately.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/06-type.md#85--the-fingerprintkey-chicken-and-egg-problem">Spec 06 §8.5</a>
     */
    FINGERPRINT_KEY("fingerprintKey", String.class, "fingerprint",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(READ, WRITE)),

    // ========================================================================
    // ID Properties (11.4)
    // ========================================================================

    ID_STRATEGY("idStrategy", String.class, "ID_FIELD",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(READ, WRITE)),

    ID_KEY("idKey", String.class, "_id",
        levels(GLOBAL, ECLASS, EPACKAGE, FEATURE), directions(READ, WRITE)),

    ID_VALUE_KEY("idValueKey", String.class, "id",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(READ, WRITE)),

    ID_FORMAT("idFormat", String.class, "PLAIN",
        levels(GLOBAL, ECLASS, EPACKAGE, FEATURE), directions(READ, WRITE)),

    ID_KEY_MODE("idKeyMode", String.class, "ID_ONLY",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(READ, WRITE)),

    @SuppressWarnings("unchecked")
    ID_FEATURES("idFeatures", (Class<List<String>>) (Class<?>) List.class, List.of(),
        levels(ECLASS, EPACKAGE), directions(READ, WRITE)),

    ID_SEPARATOR("idSeparator", String.class, "-",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(READ, WRITE)),

    ID_SEPARATOR_KEY("idSeparatorKey", String.class, "separator",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(READ, WRITE)),

    ID_SEPARATOR_SERIALIZE("idSeparatorSerialize", Boolean.class, true,
        levels(GLOBAL, ECLASS), directions(WRITE), directions(READ)),  // (R)W

    ID_ON_TOP("idOnTop", Boolean.class, true,
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE), directions(READ)),  // (R)W

    ID_SCOPE("idScope", String.class, "ALL",
        levels(GLOBAL), directions(READ, WRITE)),

    ID_FORMAT_SCOPE("idFormatScope", String.class, "ALL",
        levels(GLOBAL), directions(READ, WRITE)),

    ID_VALUE_READER_NAME("idValueReaderName", String.class, null,
        levels(GLOBAL, ECLASS, EPACKAGE), directions(READ)),

    ID_VALUE_WRITER_NAME("idValueWriterName", String.class, null,
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE)),

    // ========================================================================
    // Feature Properties (11.5)
    // ========================================================================

    KEY("key", String.class, null,  // null means use feature name
        levels(FEATURE), directions(READ, WRITE)),

    IGNORE("ignore", Boolean.class, false,
        levels(GLOBAL, ECLASS, FEATURE), directions(READ, WRITE)),

    IGNORE_READ("ignoreRead", Boolean.class, false,
        levels(GLOBAL, ECLASS, FEATURE), directions(READ)),

    IGNORE_WRITE("ignoreWrite", Boolean.class, false,
        levels(GLOBAL, ECLASS, FEATURE), directions(WRITE)),

    FORCE_READ("forceRead", Boolean.class, false,
        levels(GLOBAL, ECLASS, FEATURE), directions(READ)),

    FORCE_WRITE("forceWrite", Boolean.class, false,
        levels(GLOBAL, ECLASS, FEATURE), directions(WRITE)),

    SERIALIZE_NULL("serializeNull", Boolean.class, false,
        levels(GLOBAL, ECLASS, FEATURE), directions(WRITE), directions(READ)),  // (R)W

    SERIALIZE_EMPTY("serializeEmpty", Boolean.class, false,
        levels(GLOBAL, ECLASS, FEATURE), directions(WRITE), directions(READ)),  // (R)W

    SERIALIZE_DEFAULT("serializeDefault", Boolean.class, false,
        levels(GLOBAL, ECLASS, FEATURE), directions(WRITE), directions(READ)),  // (R)W

    ENUM_SERIALIZATION("enumSerialization", String.class, "LITERAL",
        levels(GLOBAL, FEATURE), directions(READ, WRITE)),

    DATE_FORMAT("dateFormat", String.class, null,
        levels(GLOBAL, ECLASS, FEATURE), directions(READ, WRITE)),

    VALUE_READER_NAME("valueReaderName", String.class, null,
        levels(GLOBAL, ECLASS, FEATURE), directions(READ)),

    VALUE_WRITER_NAME("valueWriterName", String.class, null,
        levels(GLOBAL, ECLASS, FEATURE), directions(WRITE)),

    FLATTEN("flatten", Boolean.class, false,
        levels(FEATURE), directions(WRITE)),

    // ========================================================================
    // Reference Properties (11.6)
    // ========================================================================

    REF_FORMAT("refFormat", String.class, "STRUCTURED",
        levels(GLOBAL, FEATURE), directions(READ, WRITE)),

    REF_KEY("refKey", String.class, "$ref",
        levels(GLOBAL, FEATURE), directions(READ, WRITE)),

    REF_TYPE_KEY("refTypeKey", String.class, "_type",
        levels(GLOBAL, FEATURE), directions(READ, WRITE)),

    PROXY_KEY("proxyKey", String.class, "$proxy",
        levels(GLOBAL, FEATURE), directions(READ, WRITE)),

    EXPAND("expand", List.class, null,
        levels(GLOBAL, ECLASS, FEATURE), directions(READ, WRITE)),

    EXPAND_GLOBAL("expandGlobal", Boolean.class, false,
        levels(GLOBAL, ECLASS), directions(READ, WRITE)),

    EXPAND_DEPTH("expandDepth", Integer.class, 1,
        levels(GLOBAL, ECLASS, FEATURE), directions(READ, WRITE)),

    EXPAND_IGNORE_BIDIRECTIONAL("expandIgnoreBidirectional", Boolean.class, true,
        levels(GLOBAL, ECLASS, FEATURE), directions(WRITE), directions(READ)),  // (R)W

    SERIALIZE_INSTANCE_TYPE("serializeInstanceType", Boolean.class, true,
        levels(GLOBAL, FEATURE), directions(WRITE)),

    /**
     * Opt-in for loading the resource a cross-document reference names.
     * <p>
     * Off by default: a reference URI is data, so reading a document must not make the codec
     * open the location that document chose. What resolution finds is what is already in
     * memory; the rest stays a proxy the embedder resolves deliberately - which is the
     * reference contract anyway. Turn this on only where the input is trusted, and prefer
     * narrowing it with {@link #REF_URI_SCHEMES}.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/10-reference.md#93-cross-resource-references">Spec 10 §9.3</a>
     */
    LOAD_REFERENCED_RESOURCES("loadReferencedResources", Boolean.class, false,
        levels(GLOBAL), directions(READ)),

    /**
     * URI schemes a reference in the document may name.
     * <p>
     * Empty by default, which means "unrestricted": every URI is accepted, and one carrying a
     * scheme that can reach out of the process ({@code http}, {@code file}, {@code jar}, ...)
     * is reported as a warning so it is visible rather than silent. Listing schemes turns that
     * into enforcement - a reference URI with any other scheme is refused, and no proxy is
     * created for it. Relative and fragment-only URIs carry no scheme and are always allowed.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/10-reference.md#93-cross-resource-references">Spec 10 §9.3</a>
     */
    @SuppressWarnings("unchecked")
    REF_URI_SCHEMES("refUriSchemes", (Class<List<String>>) (Class<?>) List.class, List.of(),
        levels(GLOBAL), directions(READ)),

    // ========================================================================
    // Discriminator Mapping Properties (11.8)
    // ========================================================================

    TYPE_MAP_ID("typeMapId", String.class, null,
        levels(ECLASS), directions(READ, WRITE)),

    TYPE_DISCRIMINATOR_PATH("typeDiscriminatorPath", String.class, null,
        levels(ECLASS), directions(READ, WRITE)),

    TYPE_DISCRIMINATOR("typeDiscriminator", String.class, null,
        levels(ECLASS), directions(READ, WRITE)),

    @SuppressWarnings("unchecked")
    TYPE_MAPPINGS("typeMappings", (Class<Map<String, String>>) (Class<?>) Map.class, null,
        levels(ECLASS), directions(READ, WRITE)),

    @SuppressWarnings("unchecked")
    INLINE_MAPPINGS("inlineMappings", (Class<Map<String, String>>) (Class<?>) Map.class, null,
        levels(FEATURE), directions(READ, WRITE)),

    DISCRIMINATOR_PATH("discriminatorPath", String.class, null,
        levels(FEATURE), directions(READ, WRITE)),

    DISCRIMINATOR_VALUE("discriminatorValue", String.class, null,
        levels(FEATURE), directions(READ, WRITE)),

    FALLBACK_STRATEGY("fallbackStrategy", String.class, "SKIP",  // Spec default: SKIP
        levels(GLOBAL, ECLASS, FEATURE), directions(READ), directions(WRITE)),  // R(W)

    FALLBACK_ECLASS("fallbackEClass", String.class, null,
        levels(FEATURE), directions(READ), directions(WRITE)),  // R(W)

    // ========================================================================
    // SuperType Properties (11.10)
    // ========================================================================

    SUPERTYPE_SERIALIZE("superTypeSerialize", Boolean.class, false,
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE), directions(READ)),  // (R)W

    // Note: superTypeKey default is format-dependent (PLAIN → "_supertype", STRUCTURED → "supertype")
    // See SuperTypeConfig.getEffectiveSuperTypeKey(SerializationFormat)
    SUPERTYPE_KEY("superTypeKey", String.class, null,  // format-dependent default
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE), directions(READ)),  // (R)W

    SUPERTYPE_STRATEGY("superTypeStrategy", String.class, "ALL",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE), directions(READ)),  // (R)W

    SUPERTYPE_AS_ARRAY("superTypeAsArray", Boolean.class, true,
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE), directions(READ)),  // (R)W

    SUPERTYPE_SEPARATOR("superTypeSeparator", String.class, ",",
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE), directions(READ)),  // (R)W

    SUPERTYPE_FORMAT("superTypeFormat", String.class, null,  // inherits from typeFormat
        levels(GLOBAL, ECLASS, EPACKAGE), directions(WRITE), directions(READ)),  // (R)W

    // Note: superTypeSchemaKey not needed - SuperTypeConfig inherits from TypeConfig
    // Note: superTypeNameKey removed - superTypeKey has format-dependent default:
    //       PLAIN format → "_supertype", STRUCTURED format → "supertype"

    SUPERTYPE_VALUE_READER_NAME("superTypeValueReaderName", String.class, null,
        levels(GLOBAL, ECLASS), directions(READ)),

    SUPERTYPE_VALUE_WRITER_NAME("superTypeValueWriterName", String.class, null,
        levels(GLOBAL, ECLASS), directions(WRITE)),

    // ========================================================================
    // Strictness Properties (11.11)
    // ========================================================================

    STRICT_ON_UNKNOWN("strictOnUnknown", Boolean.class, false,
        levels(GLOBAL, ECLASS), directions(READ)),

    STRICT_ON_MISSING("strictOnMissing", Boolean.class, false,
        levels(GLOBAL, ECLASS), directions(READ)),

    STRICT_ON_CONVERSION("strictOnConversion", Boolean.class, false,
        levels(GLOBAL, ECLASS), directions(READ)),

    DESERIALIZATION_MODE("deserializationMode", String.class, "LENIENT",
        levels(GLOBAL, ECLASS), directions(READ)),

    // ========================================================================
    // Global Options (11.12)
    // ========================================================================

    SMART_COMPRESSION("smartCompression", Boolean.class, false,
        levels(GLOBAL), directions(READ, WRITE)),

    @SuppressWarnings("unchecked")
    IGNORE_FEATURES("ignoreFeatures", (Class<List<String>>) (Class<?>) List.class, List.of(),
        levels(GLOBAL, ECLASS), directions(READ, WRITE)),

    FIELD_ORDER("fieldOrder", String.class, "DECLARATION",
        levels(GLOBAL), directions(WRITE), directions(READ)),  // (R)W

    METADATA_FIELDS_FIRST("metadataFieldsFirst", Boolean.class, true,
        levels(GLOBAL), directions(WRITE), directions(READ)),  // (R)W

    METADATA_MERGE("metadataMerge", Boolean.class, false,
        levels(GLOBAL, ECLASS), directions(READ, WRITE)),

    METADATA_KEY("metadataKey", String.class, "_metadata",
        levels(GLOBAL, ECLASS), directions(READ, WRITE)),

    USE_NAMES_FROM_EXTENDED_METADATA("useNamesFromExtendedMetadata", Boolean.class, false,
        levels(GLOBAL), directions(READ, WRITE)),

    // ========================================================================
    // Load/Save Options - Runtime Only (11.13)
    // ========================================================================

    ROOT_TYPE("rootType", String.class, null,
        levels(GLOBAL), directions(READ)),

    ROOT_SCHEMA("rootSchema", String.class, null,
        levels(GLOBAL), directions(READ)),

    ROOT_FINGERPRINT("rootFingerprint", String.class, null,
        levels(GLOBAL), directions(READ)),

    @SuppressWarnings("unchecked")
    FEATURE_TYPE_HINTS("featureTypeHints", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(READ)),

    TYPE_HINT_MODE("typeHintMode", String.class, "HINT",
        levels(GLOBAL), directions(READ)),

    @SuppressWarnings("unchecked")
    VALUE_READERS("valueReaders", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(READ)),

    @SuppressWarnings("unchecked")
    VALUE_WRITERS("valueWriters", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(WRITE)),

    @SuppressWarnings("unchecked")
    FEATURE_VALUE_READERS("featureValueReaders", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(READ)),

    @SuppressWarnings("unchecked")
    FEATURE_VALUE_WRITERS("featureValueWriters", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(WRITE)),

    @SuppressWarnings("unchecked")
    FEATURE_VALUE_READER_INSTANCES("featureValueReaderInstances", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(READ)),

    @SuppressWarnings("unchecked")
    FEATURE_VALUE_WRITER_INSTANCES("featureValueWriterInstances", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(WRITE)),

    // ========================================================================
    // Scope Configuration (for runtime options)
    // ========================================================================

    /**
     * Per-EClass configuration map.
     * <p>
     * Value type: {@code Map<EClass, Map<String, Object>>}
     * </p>
     */
    @SuppressWarnings("unchecked")
    ECLASS_CONFIG("eClassConfig", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(READ, WRITE)),

    /**
     * Per-EReference configuration map.
     * <p>
     * Value type: {@code Map<EReference, Map<String, Object>>}
     * </p>
     */
    @SuppressWarnings("unchecked")
    EREFERENCE_CONFIG("eReferenceConfig", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(READ, WRITE)),

    /**
     * Per-EAttribute configuration map.
     * <p>
     * Value type: {@code Map<EAttribute, Map<String, Object>>}
     * </p>
     */
    @SuppressWarnings("unchecked")
    EATTRIBUTE_CONFIG("eAttributeConfig", (Class<Map<?, ?>>) (Class<?>) Map.class, null,
        levels(GLOBAL), directions(READ, WRITE));

    // ========================================================================
    // Fields and Constructor
    // ========================================================================

    private final String key;
    private final Class<?> type;
    private final Object defaultValue;
    private final Set<ConfigLevel> validLevels;
    private final Set<ConfigDirection> directions;
    private final Set<ConfigDirection> futureDirections;

    <T> ConfigProperty(String key, Class<T> type, T defaultValue,
                       Set<ConfigLevel> validLevels, Set<ConfigDirection> directions) {
        this(key, type, defaultValue, validLevels, directions, EnumSet.noneOf(ConfigDirection.class));
    }

    <T> ConfigProperty(String key, Class<T> type, T defaultValue,
                       Set<ConfigLevel> validLevels, Set<ConfigDirection> directions,
                       Set<ConfigDirection> futureDirections) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
        this.validLevels = validLevels;
        this.directions = directions;
        this.futureDirections = futureDirections;
    }

    // ========================================================================
    // Accessors
    // ========================================================================

    /**
     * Returns the property key used in annotations and property maps.
     * For property maps, prefix with "codec." (e.g., "codec.typeStrategy").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the full property key with "codec." prefix.
     */
    public String getPropertyKey() {
        return "codec." + key;
    }

    /**
     * Returns the Java type of this property's value.
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Returns the default value when not configured.
     */
    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue() {
        return (T) defaultValue;
    }

    /**
     * Returns the levels at which this property can be configured.
     */
    public Set<ConfigLevel> getValidLevels() {
        return validLevels;
    }

    /**
     * Returns the directions this property applies to (primary).
     */
    public Set<ConfigDirection> getDirections() {
        return directions;
    }

    /**
     * Returns directions marked as future/potential (not yet implemented).
     */
    public Set<ConfigDirection> getFutureDirections() {
        return futureDirections;
    }

    /**
     * Checks if this property is valid at the given level.
     */
    public boolean isValidAt(ConfigLevel level) {
        return validLevels.contains(level);
    }

    /**
     * Checks if this property applies to the given direction.
     */
    public boolean appliesTo(ConfigDirection direction) {
        return directions.contains(direction);
    }

    /**
     * Checks if this property applies to the given direction (including future).
     */
    public boolean appliesToIncludingFuture(ConfigDirection direction) {
        return directions.contains(direction) || futureDirections.contains(direction);
    }

    // ========================================================================
    // Lookup
    // ========================================================================

    /**
     * Finds a property by its key.
     *
     * @param key the property key (without "codec." prefix)
     * @return the property, or null if not found
     */
    public static ConfigProperty byKey(String key) {
        for (ConfigProperty prop : values()) {
            if (prop.key.equals(key)) {
                return prop;
            }
        }
        return null;
    }

    /**
     * Finds a property by its full property key (with "codec." prefix).
     *
     * @param propertyKey the full property key (e.g., "codec.typeStrategy")
     * @return the property, or null if not found
     */
    public static ConfigProperty byPropertyKey(String propertyKey) {
        if (propertyKey != null && propertyKey.startsWith("codec.")) {
            return byKey(propertyKey.substring(6));
        }
        return null;
    }

    // ========================================================================
    // Helper methods for enum construction
    // ========================================================================

    private static Set<ConfigLevel> levels(ConfigLevel... levels) {
        return levels.length == 0 ? EnumSet.noneOf(ConfigLevel.class) : EnumSet.of(levels[0], levels);
    }

    private static Set<ConfigDirection> directions(ConfigDirection... directions) {
        return directions.length == 0 ? EnumSet.noneOf(ConfigDirection.class) : EnumSet.of(directions[0], directions);
    }
}
