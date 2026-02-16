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
package org.eclipse.fennec.codec.deser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.ClassConfig;
import org.eclipse.fennec.codec.config.DiscriminatorConfig;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.context.EMFCodecReadContext;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Main Jackson deserializer for EObjects.
 * <p>
 * Orchestrates the deserialization process using the entry-based pattern:
 * <ol>
 *   <li>Read type information first (if present)</li>
 *   <li>Resolve EClass (from type info or CODEC_ROOT_TYPE option)</li>
 *   <li>Create EObject instance</li>
 *   <li>Build deserialization entries for the EClass</li>
 *   <li>Read remaining properties and deserialize</li>
 *   <li>Handle unresolved references</li>
 * </ol>
 * </p>
 * <p>
 * Uses {@link EffectiveCodecConfig} which wraps
 * {@link org.eclipse.fennec.codec.config.ConfigurationResolver} for on-demand
 * configuration resolution with caching.
 * </p>
 *
 * @see EffectiveCodecConfig
 * @see DeserializationEntry
 * @see <a href="docs/codec-v2-spec/15-deserialization.md">Spec 15: Deserialization</a>
 * @author Mark Hoffmann
 * @since 2026-02-01
 */
public class CodecEObjectDeserializer extends ValueDeserializer<EObject> {

    private static final Logger LOGGER = Logger.getLogger(CodecEObjectDeserializer.class.getName());

    /** Context attribute key for CODEC_ROOT_TYPE option (user-provided root hint) */
    public static final String CODEC_ROOT_TYPE = "CODEC_ROOT_TYPE";

    /** Default reference key for non-containment references */
    private static final String DEFAULT_REF_KEY = "$ref";

    /** Default type key */
    private static final String DEFAULT_TYPE_KEY = "_type";

    /** Default schema key for PLAIN SCHEMA_AND_TYPE format */
    private static final String DEFAULT_SCHEMA_KEY = "_schema";

    private final EffectiveCodecConfig config;
    private final CodecEntryContext entryContext;

    /**
     * Creates a new CodecEObjectDeserializer.
     *
     * @param config the effective codec configuration
     */
    public CodecEObjectDeserializer(EffectiveCodecConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.entryContext = CodecEntryContext.builder()
                .effectiveConfig(config)
                .diagnostics(config.getDiagnostics())
                .valueRegistry(config.getValueRegistry())
                .build();
    }

    @Override
    public Class<?> handledType() {
        return EObject.class;
    }

    @Override
    public EObject deserialize(JsonParser parser, DeserializationContext ctxt) {
        JsonToken token = parser.currentToken();

        // Handle null
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        // Must be at START_OBJECT
        if (token != JsonToken.START_OBJECT) {
            String msg = "Expected START_OBJECT, got: " + token;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
            return null;
        }

        // Try to get EMF context from parser's stream context (preferred)
        // This provides access to resource, type hints, and metadata service
        EMFCodecReadContext emfContext = null;
        if (parser.streamReadContext() instanceof EMFCodecReadContext ctx) {
            emfContext = ctx;
        }

        // Create deserialization state with resource from context
        // Primary: EMFCodecReadContext (for CodecJsonParser path)
        // Fallback: ContextHelper.RESOURCE attribute (for FormatDelegateParser path)
        Resource resource = emfContext != null ? emfContext.getResource() : null;
        if (resource == null) {
            resource = ContextHelper.getResource(ctxt);
        }
        DeserializationState state = new DeserializationState(resource);

        // Get or create shared unresolved references list from context
        @SuppressWarnings("unchecked")
        List<DeserializationState.UnresolvedReference> unresolvedRefs =
                (List<DeserializationState.UnresolvedReference>) ctxt.getAttribute(ContextHelper.UNRESOLVED_REFERENCES);
        if (unresolvedRefs != null) {
            // Use shared list for collecting unresolved references
            state.setSharedUnresolvedReferences(unresolvedRefs);
        }

        // Get the expected type hint (must be EClass if set)
        // Priority:
        // 1. Parser's stream context (CodecJsonReadContext) - for nested objects
        // 2. Jackson's DeserializationContext.getAttribute() - for backwards compatibility
        EClass hintEClass = null;
        if (emfContext != null) {
            hintEClass = emfContext.getCurrentTypeHint();
        }
        if (hintEClass == null) {
            // Fall back to ContextHelper for backwards compatibility
            hintEClass = ContextHelper.getExpectedType(ctxt);
        }

        // Check if we need to use featurePath-based type resolution.
        // FeaturePathTypeResolver is only needed when the discriminator path is a
        // nested/non-standard path (e.g., "info.profileName") that differs from the
        // type key. When discriminatorPath matches the type key (e.g., both "_type"),
        // the standard flow handles it — TypeDeserializationEntry.resolveEClass()
        // performs targeted discriminator lookup via resolve(mapId, ...).
        // See spec 08-discriminator-mapping.md §7.1.1 vs §7.1.2.
        String discriminatorPath = getDiscriminatorPath(hintEClass);

        // If no hint provided, try to find ANY discriminatorPath from registered types
        if (!FeaturePathTypeResolver.hasDiscriminatorPath(discriminatorPath)
                && hintEClass == null
                && config.getTypeDiscriminatorService() != null) {
            discriminatorPath = config.getTypeDiscriminatorService().getAnyDiscriminatorPath();
        }

        if (FeaturePathTypeResolver.hasDiscriminatorPath(discriminatorPath)
                && config.getTypeDiscriminatorService() != null
                && !isTypeKey(discriminatorPath)) {
            return deserializeWithFeaturePath(parser, ctxt, state, hintEClass, discriminatorPath);
        }

        // Standard deserialization flow
        Map<String, Object> deferredProperties = new HashMap<>();
        EClass resolvedEClass = null;
        EObject eObject = null;
        boolean typeFieldProcessed = false;
        String schemaValue = null;  // For PLAIN SCHEMA_AND_TYPE format

        // Read properties
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String propertyName = parser.currentName();
            parser.nextToken(); // Move to value

            // Check if this is the schema property (for PLAIN SCHEMA_AND_TYPE)
            if (isSchemaKey(propertyName)) {
                schemaValue = parser.getString();
                continue;
            }

            // Check if this is the type property - ALWAYS process it when present
            if (isTypeKey(propertyName)) {
                // Read the raw type value BEFORE consuming it for type resolution
                String rawTypeValue = readTypeValueAsString(parser, ctxt);

                // Now resolve the type using the raw value
                resolvedEClass = resolveTypeFromValue(rawTypeValue, state, hintEClass, schemaValue, ctxt, emfContext);
                state.setResolvedEClass(resolvedEClass);
                typeFieldProcessed = true;

                // Now we can create the object and process deferred properties
                if (resolvedEClass != null) {
                    eObject = state.createEObject();
                    processDeferredProperties(state, deferredProperties, ctxt);

                    // Also set the type value as an attribute if a matching feature exists
                    setTypeAsAttributeIfExists(eObject, propertyName, rawTypeValue);
                }
                continue;
            }

            // If we don't have the type yet, defer this property
            if (resolvedEClass == null) {
                deferredProperties.put(propertyName, readCurrentValue(parser));
                continue;
            }

            // Create object if not yet created
            if (eObject == null) {
                eObject = state.createEObject();
                processDeferredProperties(state, deferredProperties, ctxt);
            }

            // Deserialize the property
            deserializeProperty(state, propertyName, parser, ctxt);
        }

        // If no _type field was found, fall back to hint
        if (!typeFieldProcessed && hintEClass != null) {
            resolvedEClass = hintEClass;
            state.setResolvedEClass(resolvedEClass);
        }

        // Handle case where type was never found
        if (eObject == null && resolvedEClass != null) {
            eObject = state.createEObject();
            processDeferredProperties(state, deferredProperties, ctxt);
        }

        if (eObject == null) {
            String msg = "Cannot deserialize: no type information found and no CODEC_ROOT_TYPE hint";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
        }

        // Check strictOnMissing for required features
        if (eObject != null) {
            checkStrictOnMissing(eObject, state, ctxt, parser);
        }

        return eObject;
    }

    /**
     * Checks if the property name is a type key.
     */
    private boolean isTypeKey(String propertyName) {
        if (DEFAULT_TYPE_KEY.equals(propertyName)
            || "_class".equals(propertyName)
            || "@type".equals(propertyName)
            || "eClass".equals(propertyName)) {
            return true;
        }
        // Check against resolved global type key
        TypeConfig globalTypeConfig = config.resolveGlobalTypeConfig();
        String configuredTypeKey = globalTypeConfig != null ? globalTypeConfig.getTypeKey() : null;
        return configuredTypeKey != null && configuredTypeKey.equals(propertyName);
    }

    /**
     * Checks if the property name is a schema key (for PLAIN SCHEMA_AND_TYPE format).
     */
    private boolean isSchemaKey(String propertyName) {
        return DEFAULT_SCHEMA_KEY.equals(propertyName)
            || "@vocab".equals(propertyName);
    }

    /**
     * Reads the type value as a string from the current parser position.
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context (for adding diagnostics)
     * @return the type value as a string, or null if not a string/structured format
     */
    private String readTypeValueAsString(JsonParser parser, DeserializationContext ctxt) {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_STRING) {
            return parser.getString();
        }
        if (token == JsonToken.START_OBJECT) {
            // For STRUCTURED format, we can't easily extract - return null
            // The TypeDeserializationEntry will handle structured format parsing
            return null;
        }
        // Unexpected token (e.g., number, boolean, etc.) - report based on mode
        String msg = "Unexpected token for _type field: " + token + ". Expected STRING or OBJECT.";
        if (ContextHelper.isStrictMode(ctxt)) {
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
        } else {
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
        }
        return null;
    }

    /**
     * Resolves an EClass from a pre-read type value string.
     *
     * @param typeValue the raw type value from JSON
     * @param state the deserialization state
     * @param hintEClass optional hint EClass
     * @param schemaValue optional schema value for SCHEMA_AND_TYPE format
     * @param ctxt the Jackson deserialization context
     * @param emfContext optional EMF context for inline mapping resolution (may be null)
     */
    private EClass resolveTypeFromValue(String typeValue, DeserializationState state,
            EClass hintEClass, String schemaValue, DeserializationContext ctxt,
            EMFCodecReadContext emfContext) {
        if (typeValue == null) {
            return hintEClass;
        }

        // Build type config from resolved global config
        TypeConfig globalTypeConfig = config.resolveGlobalTypeConfig();
        TypeConfig typeConfig = TypeConfig.builder()
                .include(true)
                .typeKey(globalTypeConfig != null ? globalTypeConfig.getTypeKey() : DEFAULT_TYPE_KEY)
                .strategy(globalTypeConfig != null ? globalTypeConfig.getStrategy() : null)
                .build();

        // Build supertype config for validation (null means no validation)
        SuperTypeConfig globalSuperTypeConfig = config.resolveGlobalSuperTypeConfig();

        // Extract mapId from the hint EClass's DiscriminatorConfig for targeted
        // registry resolution. When a mapId is present, the TypeDeserializationEntry
        // uses resolve(mapId, ...) instead of resolveFromAny(), ensuring the correct
        // fallback strategy is applied. See spec 08-discriminator-mapping.md §7.1.
        String discriminatorMapId = getDiscriminatorMapId(hintEClass);

        TypeDeserializationEntry typeEntry = new TypeDeserializationEntry(
                typeConfig, config.getTypeDiscriminatorService(),
                globalSuperTypeConfig, discriminatorMapId);

        // Compose type value with schema if needed (SCHEMA_AND_TYPE format)
        String effectiveTypeValue = typeValue;
        if (schemaValue != null && !schemaValue.isEmpty() && !typeValue.contains("#//")) {
            effectiveTypeValue = schemaValue + "#//" + typeValue;
        }

        // Extract current reference from EMF context for inline mapping resolution.
        // When deserializing a contained object under an EReference with an inlineMapping
        // annotation, this allows the type resolver to check the reference-scoped registry first.
        EReference currentReference = null;
        if (emfContext != null && emfContext.getCurrentFeature() instanceof EReference ref) {
            currentReference = ref;
        }

        // Delegate to TypeDeserializationEntry for consistent resolution logic
        EClass resolved = typeEntry.resolveEClass(effectiveTypeValue, hintEClass, ctxt, currentReference);
        if (resolved != null) {
            state.setResolvedEClass(resolved);
            return resolved;
        }

        // Type resolution failed - behavior depends on DeserializationMode
        if (ContextHelper.isStrictMode(ctxt)) {
            // STRICT mode: Error when type cannot be resolved
            String msg = "Could not resolve EClass from type value: " + typeValue;
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, null, "CodecEObjectDeserializer");
            // Still return hint to allow partial parsing, but error is recorded
            return hintEClass;
        } else {
            // LENIENT/AUTO_DETECT mode: Warning and fall back to hint
            if (hintEClass != null) {
                String msg = String.format(
                        "Type resolved via fallback. Could not resolve '%s', using hint '%s'",
                        typeValue, hintEClass.getName());
                LOGGER.warning(msg);
                ContextHelper.addWarning(ctxt, msg, null, "CodecEObjectDeserializer");
            }
            return hintEClass;
        }
    }

    /**
     * Sets the type value as an attribute if a matching feature exists.
     */
    private void setTypeAsAttributeIfExists(EObject eObject, String propertyName, String typeValue) {
        if (eObject == null || propertyName == null || typeValue == null) {
            return;
        }

        EClass eClass = eObject.eClass();
        EStructuralFeature feature = eClass.getEStructuralFeature(propertyName);

        if (feature instanceof EAttribute attr) {
            if (attr.getEType().getInstanceClass() == String.class ||
                "EString".equals(attr.getEType().getName())) {
                try {
                    eObject.eSet(feature, typeValue);
                    LOGGER.fine(() -> "Set type key '" + propertyName + "' as attribute with value: " + typeValue);
                } catch (Exception e) {
                    LOGGER.warning("Failed to set type as attribute: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reads the current value from the parser into a simple Java object.
     */
    private Object readCurrentValue(JsonParser parser) {
        JsonToken token = parser.currentToken();

        switch (token) {
            case VALUE_STRING:
                return parser.getString();
            case VALUE_NUMBER_INT:
                return parser.getLongValue();
            case VALUE_NUMBER_FLOAT:
                return parser.getDoubleValue();
            case VALUE_TRUE:
                return Boolean.TRUE;
            case VALUE_FALSE:
                return Boolean.FALSE;
            case VALUE_NULL:
                return null;
            case START_OBJECT:
                return readObjectAsMap(parser);
            case START_ARRAY:
                return readArrayAsList(parser);
            default:
                return null;
        }
    }

    /**
     * Reads a JSON object into a Map.
     */
    private Map<String, Object> readObjectAsMap(JsonParser parser) {
        Map<String, Object> result = new LinkedHashMap<>();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            parser.nextToken(); // Move to value
            Object value = readCurrentValue(parser);
            result.put(fieldName, value);
        }

        return result;
    }

    /**
     * Reads a JSON array into a List.
     */
    private List<Object> readArrayAsList(JsonParser parser) {
        List<Object> result = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            Object value = readCurrentValue(parser);
            result.add(value);
        }

        return result;
    }

    /**
     * Processes deferred properties after the type is resolved.
     * <p>
     * Checks {@code strictOnUnknown} configuration for deferred properties that
     * don't match any known feature.
     * </p>
     */
    private void processDeferredProperties(DeserializationState state,
            Map<String, Object> deferredProperties, DeserializationContext ctxt) {
        EClass eClass = state.getResolvedEClass();
        if (eClass == null) {
            return;
        }

        Map<String, DeserializationEntry> entries = buildDeserializationEntries(eClass);

        for (Map.Entry<String, Object> entry : deferredProperties.entrySet()) {
            String propertyName = entry.getKey();
            Object value = entry.getValue();

            DeserializationEntry deserEntry = entries.get(propertyName);
            if (deserEntry != null && value != null) {
                replayDeferredValue(state, deserEntry, value, ctxt);
            } else if (deserEntry == null) {
                // Unknown property in deferred list - check strictOnUnknown config
                ClassConfig classConfig = config.resolveClassConfig(eClass);
                if (classConfig != null && classConfig.isStrictOnUnknown()) {
                    String msg = "Unknown feature '" + propertyName + "' for EClass " + eClass.getName();
                    LOGGER.warning(msg);
                    ContextHelper.addError(ctxt, msg, null, "CodecEObjectDeserializer");
                    throw new IllegalStateException(msg);
                } else {
                    LOGGER.fine("Skipping unknown deferred property: " + propertyName);
                    ContextHelper.addWarning(ctxt,
                        "Unknown feature '" + propertyName + "' for EClass " + eClass.getName(),
                        null, "CodecEObjectDeserializer");
                }
            }
        }
    }

    /**
     * Replays a deferred value through a TokenBuffer for proper deserialization.
     */
    private void replayDeferredValue(DeserializationState state, DeserializationEntry entry,
            Object value, DeserializationContext ctxt) {
        try {
            tools.jackson.databind.util.TokenBuffer buffer = ctxt.bufferForInputBuffering(ctxt.getParser());

            if (value instanceof String s) {
                buffer.writeString(s);
            } else if (value instanceof Number n) {
                if (n instanceof Integer i) {
                    buffer.writeNumber(i);
                } else if (n instanceof Long l) {
                    buffer.writeNumber(l);
                } else if (n instanceof Double d) {
                    buffer.writeNumber(d);
                } else {
                    buffer.writeNumber(n.doubleValue());
                }
            } else if (value instanceof Boolean b) {
                buffer.writeBoolean(b);
            } else if (value instanceof Map<?, ?> map) {
                buffer.writeStartObject();
                for (var e : map.entrySet()) {
                    buffer.writeName(String.valueOf(e.getKey()));
                    writeValueToBuffer(buffer, e.getValue());
                }
                buffer.writeEndObject();
            } else if (value instanceof List<?> list) {
                buffer.writeStartArray();
                for (Object item : list) {
                    writeValueToBuffer(buffer, item);
                }
                buffer.writeEndArray();
            } else if (value == null) {
                buffer.writeNull();
            } else {
                buffer.writeString(value.toString());
            }

            try (tools.jackson.core.JsonParser bufferParser = buffer.asParser(ctxt)) {
                bufferParser.nextToken();
                entry.deserialize(state, bufferParser, ctxt);
            }
        } catch (IllegalStateException e) {
            // Propagate IllegalStateException (e.g., from discriminator ERROR strategy)
            throw e;
        } catch (Exception e) {
            LOGGER.fine("Could not replay deferred value for " + entry.getKey() + ": " + e.getMessage());
        }
    }

    /**
     * Writes a value to the TokenBuffer.
     */
    private void writeValueToBuffer(tools.jackson.databind.util.TokenBuffer buffer, Object value) {
        if (value instanceof String s) {
            buffer.writeString(s);
        } else if (value instanceof Number n) {
            if (n instanceof Integer i) {
                buffer.writeNumber(i);
            } else if (n instanceof Long l) {
                buffer.writeNumber(l);
            } else if (n instanceof Double d) {
                buffer.writeNumber(d);
            } else {
                buffer.writeNumber(n.doubleValue());
            }
        } else if (value instanceof Boolean b) {
            buffer.writeBoolean(b);
        } else if (value instanceof Map<?, ?> map) {
            buffer.writeStartObject();
            for (var e : map.entrySet()) {
                buffer.writeName(String.valueOf(e.getKey()));
                writeValueToBuffer(buffer, e.getValue());
            }
            buffer.writeEndObject();
        } else if (value instanceof List<?> list) {
            buffer.writeStartArray();
            for (Object item : list) {
                writeValueToBuffer(buffer, item);
            }
            buffer.writeEndArray();
        } else if (value == null) {
            buffer.writeNull();
        } else {
            buffer.writeString(value.toString());
        }
    }

    /**
     * Deserializes a single property.
     * <p>
     * Checks {@code strictOnUnknown} configuration: when true, unknown fields
     * cause an error; when false (default), unknown fields are skipped with a warning.
     * </p>
     */
    private void deserializeProperty(DeserializationState state, String propertyName,
            JsonParser parser, DeserializationContext ctxt) {
        EClass eClass = state.getResolvedEClass();
        Map<String, DeserializationEntry> entries = buildDeserializationEntries(eClass);

        DeserializationEntry entry = entries.get(propertyName);
        if (entry != null) {
            entry.deserialize(state, parser, ctxt);
        } else {
            // Unknown property - check strictOnUnknown config
            ClassConfig classConfig = config.resolveClassConfig(eClass);
            if (classConfig != null && classConfig.isStrictOnUnknown()) {
                // Strict mode: throw error on unknown field
                String msg = "Unknown feature '" + propertyName + "' for EClass " + eClass.getName();
                LOGGER.warning(msg);
                ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
                throw new IllegalStateException(msg);
            } else {
                // Lenient mode (default): skip with warning
                LOGGER.fine("Unknown property: " + propertyName);
                ContextHelper.addWarning(ctxt,
                    "Unknown feature '" + propertyName + "' for EClass " + eClass.getName(),
                    parser, "CodecEObjectDeserializer");
                parser.skipChildren();
            }
        }
    }

    /**
     * Checks strictOnMissing for required features.
     * <p>
     * When {@code strictOnMissing=true}, this method checks all required features
     * (lowerBound >= 1) and throws an error if any are not set.
     * When {@code strictOnMissing=false} (default), missing required features
     * generate a warning and use the EMF default value.
     * </p>
     *
     * @param eObject the deserialized object to check
     * @param state the deserialization state
     * @param ctxt the Jackson deserialization context
     * @param parser the JSON parser (for location info)
     */
    private void checkStrictOnMissing(EObject eObject, DeserializationState state,
            DeserializationContext ctxt, JsonParser parser) {
        EClass eClass = eObject.eClass();
        ClassConfig classConfig = config.resolveClassConfig(eClass);

        // Only check if strictOnMissing is enabled
        if (classConfig == null || !classConfig.isStrictOnMissing()) {
            return;
        }

        List<String> missingFeatures = new ArrayList<>();

        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            // Check if feature is required (lowerBound >= 1)
            if (feature.getLowerBound() < 1) {
                continue;
            }

            // Skip features that are not deserialized (transient, derived, ignored)
            FeatureConfig featureConfig = config.resolveFeatureConfig(feature);
            if (!featureConfig.shouldDeserialize()) {
                continue;
            }

            // Skip non-changeable features
            if (!feature.isChangeable()) {
                continue;
            }

            // Check if the feature is set
            if (!eObject.eIsSet(feature)) {
                missingFeatures.add(feature.getName());
            }
        }

        if (!missingFeatures.isEmpty()) {
            String msg = "Missing required feature(s) for EClass " + eClass.getName() + ": "
                    + String.join(", ", missingFeatures);
            LOGGER.warning(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Builds deserialization entries for the given EClass.
     *
     * @param eClass the EClass to build entries for
     * @return map of property name to deserialization entry
     */
    private Map<String, DeserializationEntry> buildDeserializationEntries(EClass eClass) {
        Map<String, DeserializationEntry> entries = new HashMap<>();

        // Resolve configs for this EClass
        TypeConfig typeConfig = config.resolveTypeConfig(eClass);
        IdConfig idConfig = config.resolveIdConfig(eClass);
        SuperTypeConfig superTypeConfig = config.resolveSuperTypeConfig(eClass);

        // Add type entry (with supertype config for STRUCTURED format validation)
        if (typeConfig != null && typeConfig.isInclude()) {
            TypeDeserializationEntry typeEntry = new TypeDeserializationEntry(
                    typeConfig, config.getTypeDiscriminatorService(),
                    superTypeConfig);
            entries.put(typeEntry.getKey(), typeEntry);
        }

        // Add ID entry (the entry handles FEATURE_ONLY mode internally)
        if (idConfig != null) {
            IdDeserializationEntry idEntry = new IdDeserializationEntry(idConfig, eClass);
            entries.put(idEntry.getKey(), idEntry);
        }

        // Add supertype entry (parses supertype info; validation depends on config)
        if (superTypeConfig != null) {
            SuperTypeDeserializationEntry superTypeEntry = new SuperTypeDeserializationEntry(superTypeConfig);
            entries.put(superTypeEntry.getKey(), superTypeEntry);
        }

        // Add feature entries
        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            FeatureConfig featureConfig = config.resolveFeatureConfig(feature);

            // Skip features that should not be deserialized
            // shouldDeserialize() considers ignore, ignoreRead, forceRead flags
            if (!featureConfig.shouldDeserialize()) {
                continue;
            }

            // Skip non-changeable features (can't set values on them)
            if (!feature.isChangeable()) {
                continue;
            }

            DeserializationEntry entry;
            if (feature instanceof EAttribute) {
                entry = new AttributeDeserializationEntry(featureConfig, (EAttribute) feature,
                        entryContext);
            } else if (feature instanceof EReference) {
                // Resolve per-reference refKey from ReferenceConfig
                ReferenceConfig refConfig = config.resolveReferenceConfig(feature);
                String refKey = refConfig != null ? refConfig.getRefKey() : DEFAULT_REF_KEY;

                entry = new ReferenceDeserializationEntry(featureConfig, (EReference) feature,
                        refKey, entryContext);
            } else {
                continue;
            }

            entries.put(entry.getKey(), entry);
        }

        return entries;
    }

    // ========================================================================
    // FeaturePath-based deserialization
    // ========================================================================

    /**
     * Gets the discriminator path from the EClass's type configuration.
     */
    private String getDiscriminatorPath(EClass eClass) {
        if (eClass == null) {
            return null;
        }
        TypeConfig typeConfig = config.resolveTypeConfig(eClass);
        if (typeConfig == null) {
            return null;
        }
        return typeConfig.getDiscriminatorPath();
    }

    /**
     * Gets the typeMapId from the EClass's discriminator configuration.
     * <p>
     * Used to target a specific registry during featurePath-based type resolution,
     * ensuring the correct fallback strategy is applied.
     * </p>
     */
    private String getDiscriminatorMapId(EClass eClass) {
        if (eClass == null) {
            return null;
        }
        // Try DiscriminatorConfig first (from ConfigurationResolver)
        DiscriminatorConfig discriminatorConfig = config.resolveDiscriminatorConfig(eClass);
        if (discriminatorConfig != null) {
            String mapId = discriminatorConfig.getTypeMapId();
            if (mapId != null) {
                return mapId;
            }
        }
        // Fall back to direct annotation scanning via TypeDiscriminatorService
        // (walks up supertypes looking for typeMapping/{mapId} annotations)
        TypeDiscriminatorService tds = config.getTypeDiscriminatorService();
        if (tds != null) {
            return tds.getMapIdForEClass(eClass);
        }
        return null;
    }

    /**
     * Deserializes an EObject using featurePath-based type resolution.
     */
    private EObject deserializeWithFeaturePath(
            JsonParser parser,
            DeserializationContext ctxt,
            DeserializationState state,
            EClass hintEClass,
            String discriminatorPath) {

        LOGGER.fine("Using featurePath-based type resolution: " + discriminatorPath);

        // Extract mapId from DiscriminatorConfig for targeted registry resolution
        String mapId = getDiscriminatorMapId(hintEClass);

        FeaturePathTypeResolver resolver = new FeaturePathTypeResolver(
                discriminatorPath, config.getTypeDiscriminatorService(), mapId);
        resolver.scan(parser, ctxt);

        EClass resolvedEClass = resolver.getResolvedEClass();
        if (resolvedEClass == null) {
            if (hintEClass != null && !hintEClass.isAbstract()) {
                LOGGER.fine("FeaturePath resolution failed, using hint class: " + hintEClass.getName());
                resolvedEClass = hintEClass;
            } else {
                String msg = "Cannot deserialize: featurePath resolution failed for '" +
                        discriminatorPath + "' and no concrete fallback available";
                LOGGER.severe(msg);
                ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
                return null;
            }
        }

        state.setResolvedEClass(resolvedEClass);

        JsonParser bufferedParser = resolver.getBufferedParser(ctxt, parser);
        if (bufferedParser == null) {
            String msg = "No buffered content available for deserialization";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
            return null;
        }

        return deserializeFromBufferedParser(bufferedParser, ctxt, state);
    }

    /**
     * Deserializes an EObject from a buffered parser.
     */
    private EObject deserializeFromBufferedParser(
            JsonParser parser,
            DeserializationContext ctxt,
            DeserializationState state) {

        EClass resolvedEClass = state.getResolvedEClass();
        if (resolvedEClass == null) {
            String msg = "No resolved EClass in deserialization state";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
            return null;
        }

        EObject eObject = state.createEObject();
        if (eObject == null) {
            String msg = "Failed to create EObject for: " + resolvedEClass.getName();
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
            return null;
        }

        JsonToken token = parser.currentToken();
        if (token != JsonToken.START_OBJECT) {
            String msg = "Expected START_OBJECT in buffered parser, got: " + token;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
            return eObject;
        }

        while ((token = parser.nextToken()) != JsonToken.END_OBJECT && token != null) {
            if (token == JsonToken.PROPERTY_NAME) {
                String propertyName = parser.currentName();
                parser.nextToken();
                deserializeProperty(state, propertyName, parser, ctxt);
            }
        }

        // Check strictOnMissing for required features
        checkStrictOnMissing(eObject, state, ctxt, parser);

        return eObject;
    }
}
