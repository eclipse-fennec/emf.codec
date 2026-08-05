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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.buffer.CodecTokenBuffer;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.DeserializationContext;

/**
 * Deserialization entry that handles ID property (_id).
 * <p>
 * Reads the ID value and sets it on the EObject's ID attribute(s).
 * Supports:
 * <ul>
 *   <li>PLAIN format: single string value (optionally combined with separator)</li>
 *   <li>STRUCTURED format: nested object with individual fields</li>
 *   <li>Multiple ID features (combined ID)</li>
 * </ul>
 * </p>
 * <p>
 * The ID attribute is determined by:
 * <ol>
 *   <li>If idFeatures is configured → use those features in order</li>
 *   <li>Otherwise → use the EClass's {@code eIDAttribute}</li>
 *   <li>Fallback → the first attribute with {@code isID=true}</li>
 * </ol>
 * </p>
 *
 * @see IdConfig
 * @see <a href="docs/codec-v2-spec/06-id.md">Spec: ID Serialization</a>
 * @author Mark Hoffmann
 * @since 2025-12-16
 */
public class IdDeserializationEntry implements DeserializationEntry {

    private static final Logger LOGGER = Logger.getLogger(IdDeserializationEntry.class.getName());

    private final IdConfig config;
    private final EClass eClass;
    private final EAttribute idAttribute;
    private final CodecEntryContext entryContext;
    private final CodecValueReader<Object, EAttribute> customReader;

    /**
     * Creates a new IdDeserializationEntry.
     *
     * @param config the effective ID configuration
     * @param eClass the EClass to find the ID attribute from
     */
    public IdDeserializationEntry(IdConfig config, EClass eClass) {
        this(config, eClass, null);
    }

    /**
     * Creates a new IdDeserializationEntry with custom id value reader support (issue #104).
     *
     * @param config the effective ID configuration
     * @param eClass the EClass to find the ID attribute from
     * @param entryContext the codec entry context for custom readers (may be null)
     */
    @SuppressWarnings("unchecked")
    public IdDeserializationEntry(IdConfig config, EClass eClass, CodecEntryContext entryContext) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.eClass = Objects.requireNonNull(eClass, "eClass must not be null");
        this.idAttribute = findIdAttribute(eClass);
        this.entryContext = entryContext;

        String readerName = config.getValueReaderName();
        CodecValueRegistry valueRegistry = entryContext != null ? entryContext.getValueRegistry() : null;
        if (readerName != null && !readerName.isEmpty() && valueRegistry != null) {
            CodecValueReader<?, ?> reader = valueRegistry.getReader(readerName).orElse(null);
            if (reader == null) {
                LOGGER.warning("Id value reader '" + readerName + "' is not registered - "
                        + "using default id deserialization for " + eClass.getName());
            }
            this.customReader = (CodecValueReader<Object, EAttribute>) reader;
        } else {
            this.customReader = null;
        }
    }

    @Override
    public String getKey() {
        return config.getKey();
    }

    @Override
    public void deserialize(DeserializationState state, JsonParser parser, DeserializationContext ctxt) {
        EObject eObject = state.getEObject();
        if (eObject == null) {
            String msg = "Cannot set ID: EObject not yet created";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "IdDeserializationEntry");
            return;
        }

        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_NULL) {
            // Null ID - nothing to set
            return;
        }

        if (config.getFormat() == SerializationFormat.STRUCTURED) {
            deserializeStructured(eObject, parser, ctxt);
        } else {
            deserializePlain(eObject, parser, ctxt);
        }
    }

    /**
     * Deserializes ID in PLAIN format (single string, optionally combined with separator).
     * <p>
     * For a combined ID the parts are mapped onto the ID features; a separate derived key
     * attribute additionally receives the whole combined value (see
     * {@link #isDerivedKeyAttribute(List)}).
     * </p>
     */
    private void deserializePlain(EObject eObject, JsonParser parser, DeserializationContext ctxt) {
        List<String> configuredFeatures = config.getIdFeatures();

        if (configuredFeatures != null && configuredFeatures.size() > 1) {
            // Multiple ID features - split by separator
            String combinedValue = readPlainIdString(parser, ctxt);
            if (combinedValue == null) {
                return;
            }

            String separator = config.getSeparator();
            String[] parts = combinedValue.split(java.util.regex.Pattern.quote(separator), -1);

            for (int i = 0; i < Math.min(parts.length, configuredFeatures.size()); i++) {
                String featureName = configuredFeatures.get(i);
                EStructuralFeature feature = eClass.getEStructuralFeature(featureName);
                if (feature instanceof EAttribute attr) {
                    Object value = convertValue(parts[i], attr);
                    if (value != null) {
                        eObject.eSet(feature, value);
                    }
                }
            }

            if (isDerivedKeyAttribute(configuredFeatures)) {
                Object derivedKey = convertValue(combinedValue, idAttribute);
                if (derivedKey != null) {
                    eObject.eSet(idAttribute, derivedKey);
                }
            }
        } else if (idAttribute != null) {
            // Single ID attribute
            Object idValue;
            if (customReader != null && entryContext != null) {
                String raw = readPlainIdString(parser, ctxt);
                idValue = raw != null ? convertValue(raw, idAttribute) : null;
            } else {
                idValue = readIdValue(parser, idAttribute);
            }
            if (idValue != null) {
                eObject.eSet(idAttribute, idValue);
            }
        } else {
            // No ID attribute - ID may be used for resource URI fragment
            String idString = parser.getString();
            LOGGER.fine("ID value without ID attribute: " + idString + " (may be used for URI fragment)");
        }
    }

    /**
     * Deserializes ID in STRUCTURED format (nested object with individual fields).
     * <p>
     * Uses TokenBuffer to capture the nested object content, then processes it to:
     * <ol>
     *   <li>Extract individual field values (and optional separator)</li>
     *   <li>Set corresponding feature values on the EObject</li>
     *   <li>Optionally set the EIDAttribute with the combined value - only when it is a
     *       separate derived key and not one of the ID features itself</li>
     * </ol>
     * </p>
     * <p>
     * If the JSON contains a separator field (e.g., "_separator": "-"), that value
     * is used for combining ID values. Otherwise, the configured separator is used.
     * </p>
     */
    private void deserializeStructured(EObject eObject, JsonParser parser, DeserializationContext ctxt) {
        JsonToken token = parser.currentToken();
        if (token != JsonToken.START_OBJECT) {
            String msg = "Expected START_OBJECT for STRUCTURED ID, got: " + token;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "IdDeserializationEntry");
            return;
        }

        // Use ObjectReadContext - prefer ctxt if available, otherwise use empty context
        ObjectReadContext readContext = ctxt != null ? ctxt : ObjectReadContext.empty();

        // Capture the _id object content into a CodecTokenBuffer
        CodecTokenBuffer buffer = CodecTokenBuffer.forBuffering(parser, readContext);
        try {
            buffer.copyCurrentStructure(parser);
        } catch (Exception e) {
            String msg = "Error buffering STRUCTURED ID content: " + e.getMessage();
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "IdDeserializationEntry");
            return;
        }

        // Parse the buffered content to extract field values
        Map<String, Object> idValues = new LinkedHashMap<>();
        List<String> featuresToRead = getIdFeatureNames();
        String separatorKey = config.getSeparatorKey();
        String effectiveSeparator = config.getSeparator(); // Default from config

        try (JsonParser bufferParser = buffer.asParser(readContext)) {
            bufferParser.nextToken(); // Move to START_OBJECT

            while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = bufferParser.currentName();
                bufferParser.nextToken(); // Move to value

                // Check for separator field in JSON
                if (fieldName.equals(separatorKey)) {
                    if (bufferParser.currentToken() == JsonToken.VALUE_STRING) {
                        effectiveSeparator = bufferParser.getString();
                    }
                    continue; // Don't add separator to idValues
                }

                EStructuralFeature feature = eClass.getEStructuralFeature(fieldName);
                if (feature instanceof EAttribute attr && featuresToRead.contains(fieldName)) {
                    Object value = readIdValue(bufferParser, attr);
                    if (value != null) {
                        idValues.put(fieldName, value);
                    }
                }
                // Skip unknown fields (bufferParser will advance past them)
            }
        } catch (Exception e) {
            String msg = "Error parsing STRUCTURED ID content: " + e.getMessage();
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "IdDeserializationEntry");
            return;
        }

        // Set the feature values on the EObject
        for (Map.Entry<String, Object> entry : idValues.entrySet()) {
            EStructuralFeature feature = eClass.getEStructuralFeature(entry.getKey());
            if (feature != null && feature.isChangeable()) {
                eObject.eSet(feature, entry.getValue());
            }
        }

        // A separate derived key attribute carries the combined value of all components
        if (idValues.size() > 1 && isDerivedKeyAttribute(featuresToRead)
                && !idValues.containsKey(idAttribute.getName())) {
            String combinedValue = combineIdValues(idValues, effectiveSeparator);
            if (combinedValue != null) {
                try {
                    Object convertedValue = EcoreUtil.createFromString(
                            idAttribute.getEAttributeType(), combinedValue);
                    eObject.eSet(idAttribute, convertedValue);
                } catch (Exception e) {
                    LOGGER.fine("Could not set combined ID value: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Tells whether the EIDAttribute is a separate derived key, i.e. it is not one of the ID
     * features itself.
     * <p>
     * Only such an attribute receives the combined ID value. An EIDAttribute that is listed
     * among the ID features already holds its own component value and must not be overwritten
     * by the combination (issue #108).
     * </p>
     *
     * @param idFeatureNames the resolved ID feature names
     * @return {@code true} if the combined value belongs on the EIDAttribute
     */
    private boolean isDerivedKeyAttribute(List<String> idFeatureNames) {
        return idAttribute != null
                && idAttribute.isChangeable()
                && (idFeatureNames == null || !idFeatureNames.contains(idAttribute.getName()));
    }

    /**
     * Combines multiple ID values into a single string using the given separator.
     *
     * @param idValues the map of field name to value (preserves order)
     * @param separator the separator to use between values
     * @return the combined string value
     */
    private String combineIdValues(Map<String, Object> idValues, String separator) {
        if (idValues.isEmpty()) {
            return null;
        }
        if (idValues.size() == 1) {
            Object value = idValues.values().iterator().next();
            return value != null ? value.toString() : null;
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object value : idValues.values()) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(value != null ? value.toString() : "");
            first = false;
        }
        return sb.toString();
    }

    /**
     * Returns the list of ID feature names being used.
     *
     * @return list of feature names
     */
    private List<String> getIdFeatureNames() {
        List<String> configuredFeatures = config.getIdFeatures();
        if (configuredFeatures != null && !configuredFeatures.isEmpty()) {
            return configuredFeatures;
        }
        List<EStructuralFeature> idFeatures = findIdFeatures();
        return idFeatures.stream().map(EStructuralFeature::getName).toList();
    }

    /**
     * Finds all features marked with eID="true" in the EClass.
     */
    private List<EStructuralFeature> findIdFeatures() {
        List<EStructuralFeature> result = new ArrayList<>();
        EStructuralFeature idAttr = eClass.getEIDAttribute();
        if (idAttr != null) {
            result.add(idAttr);
        }
        return result;
    }

    /**
     * Reads the raw PLAIN id value as string, routing through the configured custom id
     * value reader ({@code idValueReaderName}, issue #104) when present.
     */
    private String readPlainIdString(JsonParser parser, DeserializationContext ctxt) {
        if (customReader != null && entryContext != null) {
            try {
                CodecReaderContext readerCtx = entryContext.createReaderContext(parser, ctxt);
                Object raw = customReader.read(readerCtx, idAttribute);
                return raw != null ? raw.toString() : null;
            } catch (IOException e) {
                throw new UncheckedIOException(
                        "Custom id value reader failed for " + eClass.getName(), e);
            }
        }
        return parser.getString();
    }

    /**
     * Converts a string value to the appropriate attribute type.
     */
    private Object convertValue(String stringValue, EAttribute attribute) {
        if (stringValue == null || stringValue.isEmpty()) {
            return null;
        }
        try {
            return EcoreUtil.createFromString(attribute.getEAttributeType(), stringValue);
        } catch (Exception e) {
            LOGGER.warning("Error converting value '" + stringValue + "' for attribute "
                    + attribute.getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Reads the ID value and converts it to the appropriate type.
     *
     * @param parser the JSON parser
     * @param attribute the ID attribute
     * @return the converted ID value, or null if conversion fails
     */
    private Object readIdValue(JsonParser parser, EAttribute attribute) {
        JsonToken token = parser.currentToken();
        Class<?> instanceClass = attribute.getEAttributeType().getInstanceClass();

        try {
            if (token == JsonToken.VALUE_STRING) {
                String stringValue = parser.getString();
                // Use EMF's conversion for the attribute type
                return EcoreUtil.createFromString(attribute.getEAttributeType(), stringValue);
            } else if (token == JsonToken.VALUE_NUMBER_INT) {
                if (instanceClass == Integer.class || instanceClass == int.class) {
                    return parser.getIntValue();
                } else if (instanceClass == Long.class || instanceClass == long.class) {
                    return parser.getLongValue();
                } else {
                    // Convert to string and use EMF conversion
                    return EcoreUtil.createFromString(attribute.getEAttributeType(),
                            String.valueOf(parser.getLongValue()));
                }
            } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                return EcoreUtil.createFromString(attribute.getEAttributeType(),
                        String.valueOf(parser.getDoubleValue()));
            } else {
                LOGGER.warning("Unexpected token type for ID: " + token);
                return null;
            }
        } catch (Exception e) {
            LOGGER.warning("Error converting ID value: " + e.getMessage());
            return null;
        }
    }

    /**
     * Finds the ID attribute for an EClass.
     *
     * @param eClass the EClass
     * @return the ID attribute, or null if none found
     */
    private EAttribute findIdAttribute(EClass eClass) {
        if (eClass == null) {
            return null;
        }

        // First check for explicit eIDAttribute
        EAttribute eIdAttr = eClass.getEIDAttribute();
        if (eIdAttr != null) {
            return eIdAttr;
        }

        // Then look for any attribute with isID=true
        for (EAttribute attr : eClass.getEAllAttributes()) {
            if (attr.isID()) {
                return attr;
            }
        }

        return null;
    }

    /**
     * Returns the ID attribute being used.
     *
     * @return the ID attribute, or null if URI-based ID
     */
    public EAttribute getIdAttribute() {
        return idAttribute;
    }
}
