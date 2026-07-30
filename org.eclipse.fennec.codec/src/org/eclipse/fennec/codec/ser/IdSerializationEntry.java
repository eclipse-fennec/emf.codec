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
package org.eclipse.fennec.codec.ser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serialization entry for EObject ID field.
 * <p>
 * Handles the serialization of the ID property based on the
 * ID configuration. Supports:
 * <ul>
 *   <li>PLAIN format: combined string value with separator</li>
 *   <li>STRUCTURED format: nested object with individual fields</li>
 *   <li>Multiple ID features (combined ID)</li>
 *   <li>KeyMode: ID_ONLY, BOTH, FEATURE_ONLY</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-id.md">Spec: ID Serialization</a>
 * @author Mark Hoffmann
 * @since 2025-12-16
 */
public class IdSerializationEntry implements SerializationEntry {

    private final IdConfig config;
    private final EClass eClass;

    /**
     * Creates a new IdSerializationEntry with the ID configuration.
     *
     * @param config the ID configuration
     * @param eClass the EClass being serialized (used to resolve features)
     */
    public IdSerializationEntry(IdConfig config, EClass eClass) {
        this.config = config;
        this.eClass = eClass;
    }

    @Override
    public String getKey() {
        return config.getKey();
    }

    @Override
    public boolean shouldSerialize(SerializationState state) {
        // NONE keyMode means ID serialization is completely disabled (spec §2, §8.6)
        if (config.getKeyMode() == IdKeyMode.NONE) {
            return false;
        }
        // For FEATURE_ONLY mode, we don't serialize the _id field
        if (config.getKeyMode() == IdKeyMode.FEATURE_ONLY) {
            return false;
        }
        Map<String, Object> idValues = resolveIdValues(state.getEObject());
        return !idValues.isEmpty();
    }

    @Override
    public void serialize(SerializationState state, JsonGenerator gen, SerializationContext ctxt) {
        Map<String, Object> idValues = resolveIdValues(state.getEObject());
        if (idValues.isEmpty()) {
            return;
        }

        if (config.getFormat() == SerializationFormat.STRUCTURED) {
            serializeStructured(gen, idValues);
        } else {
            serializePlain(gen, idValues);
        }
    }

    /**
     * Serializes ID in PLAIN format (combined string with separator).
     */
    private void serializePlain(JsonGenerator gen, Map<String, Object> idValues) {
        String combinedValue = combineValues(idValues);
        if (combinedValue != null) {
            gen.writeStringProperty(config.getKey(), combinedValue);

            // Write separator field if enabled and multiple features
            if (config.isSerializeSeparator() && idValues.size() > 1) {
                gen.writeStringProperty(getPlainSeparatorKey(), config.getSeparator());
            }
        }
    }

    /**
     * Gets the separator key for PLAIN format.
     * Per spec: PLAIN format uses underscore prefix (e.g., "_separator").
     *
     * @return the separator key with underscore prefix for PLAIN format
     */
    private String getPlainSeparatorKey() {
        String separatorKey = config.getSeparatorKey();
        if (separatorKey.startsWith("_") || separatorKey.startsWith("@")) {
            return separatorKey;
        }
        return "_" + separatorKey;
    }

    /**
     * Serializes ID in STRUCTURED format (nested object).
     */
    private void serializeStructured(JsonGenerator gen, Map<String, Object> idValues) {
        gen.writeName(config.getKey());
        gen.writeStartObject();

        // Write separator first if enabled and multiple features
        if (config.isSerializeSeparator() && idValues.size() > 1) {
            gen.writeStringProperty(config.getSeparatorKey(), config.getSeparator());
        }

        // Write each ID feature
        for (Map.Entry<String, Object> entry : idValues.entrySet()) {
            writeValue(gen, entry.getKey(), entry.getValue());
        }

        gen.writeEndObject();
    }

    /**
     * Writes a single value to the generator with appropriate type handling.
     */
    private void writeValue(JsonGenerator gen, String key, Object value) {
        if (value == null) {
            gen.writeNullProperty(key);
        } else if (value instanceof String s) {
            gen.writeStringProperty(key, s);
        } else if (value instanceof Integer i) {
            gen.writeNumberProperty(key, i);
        } else if (value instanceof Long l) {
            gen.writeNumberProperty(key, l);
        } else if (value instanceof Double d) {
            gen.writeNumberProperty(key, d);
        } else if (value instanceof Boolean b) {
            gen.writeBooleanProperty(key, b);
        } else {
            gen.writeStringProperty(key, value.toString());
        }
    }

    /**
     * Combines multiple ID values into a single string with separator.
     */
    private String combineValues(Map<String, Object> idValues) {
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
                sb.append(config.getSeparator());
            }
            sb.append(value != null ? value.toString() : "");
            first = false;
        }
        return sb.toString();
    }

    /**
     * Resolves the ID values for the given EObject.
     *
     * @param eObject the EObject to get the ID from
     * @return map of feature name to value (preserves order), empty if no ID features
     */
    private Map<String, Object> resolveIdValues(EObject eObject) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. Check for configured idFeatures
        List<String> configuredFeatures = config.getIdFeatures();
        if (configuredFeatures != null && !configuredFeatures.isEmpty()) {
            for (String featureName : configuredFeatures) {
                EStructuralFeature feature = eClass.getEStructuralFeature(featureName);
                if (feature != null) {
                    Object value = eObject.eGet(feature);
                    if (value != null) {
                        result.put(featureName, value);
                    }
                }
            }
            if (!result.isEmpty()) {
                return result;
            }
        }

        // 2. Look for eID="true" features
        List<EStructuralFeature> idFeatures = findIdFeatures();
        for (EStructuralFeature feature : idFeatures) {
            Object value = eObject.eGet(feature);
            if (value != null) {
                result.put(feature.getName(), value);
            }
        }

        return result;
    }

    /**
     * Finds all features marked with eID="true" in the EClass.
     */
    private List<EStructuralFeature> findIdFeatures() {
        List<EStructuralFeature> result = new ArrayList<>();
        EStructuralFeature idAttribute = eClass.getEIDAttribute();
        if (idAttribute != null) {
            result.add(idAttribute);
        }
        return result;
    }

    /**
     * Returns the configured key mode.
     *
     * @return the IdKeyMode
     */
    public IdKeyMode getKeyMode() {
        return config.getKeyMode();
    }

    /**
     * Returns whether this entry is for BOTH mode (needs feature values serialized too).
     *
     * @return true if keyMode is BOTH
     */
    public boolean isBothMode() {
        return config.getKeyMode() == IdKeyMode.BOTH;
    }

    /**
     * Returns the list of ID feature names being used.
     *
     * @return list of feature names
     */
    public List<String> getIdFeatureNames() {
        List<String> configuredFeatures = config.getIdFeatures();
        if (configuredFeatures != null && !configuredFeatures.isEmpty()) {
            return configuredFeatures;
        }
        List<EStructuralFeature> idFeatures = findIdFeatures();
        return idFeatures.stream().map(EStructuralFeature::getName).toList();
    }
}
