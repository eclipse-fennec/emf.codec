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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
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
 * @since 1.0
 */
public class IdSerializationEntry implements SerializationEntry {

    private static final Logger LOGGER = Logger.getLogger(IdSerializationEntry.class.getName());

    private final IdConfig config;
    private final EClass eClass;
    private final CodecEntryContext entryContext;
    private final CodecValueWriter<Object, EAttribute> customWriter;

    /**
     * Creates a new IdSerializationEntry with the ID configuration.
     *
     * @param config the ID configuration
     * @param eClass the EClass being serialized (used to resolve features)
     */
    public IdSerializationEntry(IdConfig config, EClass eClass) {
        this(config, eClass, null);
    }

    /**
     * Creates a new IdSerializationEntry with custom id value writer support (issue #104).
     *
     * @param config the ID configuration
     * @param eClass the EClass being serialized (used to resolve features)
     * @param entryContext the codec entry context for custom writers (may be null)
     */
    @SuppressWarnings("unchecked")
    public IdSerializationEntry(IdConfig config, EClass eClass, CodecEntryContext entryContext) {
        this.config = config;
        this.eClass = eClass;
        this.entryContext = entryContext;

        String writerName = config.getValueWriterName();
        CodecValueRegistry valueRegistry = entryContext != null ? entryContext.getValueRegistry() : null;
        if (writerName != null && !writerName.isEmpty() && valueRegistry != null) {
            CodecValueWriter<?, ?> writer = valueRegistry.getWriter(writerName).orElse(null);
            if (writer == null) {
                LOGGER.warning("Id value writer '" + writerName + "' is not registered - "
                        + "using default id serialization for " + eClass.getName());
            }
            this.customWriter = (CodecValueWriter<Object, EAttribute>) writer;
        } else {
            this.customWriter = null;
        }
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
            serializePlain(gen, idValues, ctxt);
        }
    }

    /**
     * Serializes ID in PLAIN format (combined string with separator).
     * <p>
     * A configured id value writer ({@code idValueWriterName}, issue #104) takes over writing
     * the scalar id value - e.g. the BSON module's "objectId" writer emits a native ObjectId.
     * </p>
     */
    private void serializePlain(JsonGenerator gen, Map<String, Object> idValues, SerializationContext ctxt) {
        String combinedValue = combineValues(idValues);
        if (combinedValue != null) {
            if (customWriter != null && entryContext != null) {
                gen.writeName(config.getKey());
                try {
                    CodecWriterContext writerCtx = entryContext.createWriterContext(gen, ctxt);
                    customWriter.write(combinedValue, singleIdAttribute(), writerCtx);
                } catch (IOException e) {
                    throw new UncheckedIOException(
                            "Custom id value writer failed for " + eClass.getName(), e);
                }
            } else {
                gen.writeStringProperty(config.getKey(), combinedValue);
            }

            // Write separator field if enabled and multiple features
            if (config.isSerializeSeparator() && idValues.size() > 1) {
                gen.writeStringProperty(getPlainSeparatorKey(), config.getSeparator());
            }
        }
    }

    /**
     * Resolves the single id attribute handed to a custom id value writer, or {@code null}
     * for compound ids (the writer receives the already combined scalar value).
     */
    private EAttribute singleIdAttribute() {
        List<String> names = getIdFeatureNames();
        if (names.size() != 1) {
            return null;
        }
        return eClass.getEStructuralFeature(names.get(0)) instanceof EAttribute attribute
                ? attribute : null;
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

        if (idValues.size() == 1) {
            // One id value has one inner key, and its name comes from idValueKey - the
            // counterpart of typeNameKey for the type container (spec 03 §naming, issue #119)
            Object onlyValue = idValues.values().iterator().next();
            writeValue(gen, config.getValueKey(), onlyValue);
        } else {
            // Several components need their own names to stay distinguishable
            for (Map.Entry<String, Object> entry : idValues.entrySet()) {
                writeValue(gen, entry.getKey(), entry.getValue());
            }
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
     * Builds the id of an object referenced by an id feature, using <b>that object's</b> id
     * configuration - its features and its separator (spec 09-id.md §4).
     *
     * @param reference the id reference
     * @param contained the referenced object
     * @return the id string, or null when the contained type defines no id
     */
    private String idOfContainedObject(EReference reference, EObject contained) {
        EClass containedClass = contained.eClass();
        IdConfig containedConfig = entryContext != null && entryContext.getEffectiveConfig() != null
                ? entryContext.getEffectiveConfig().resolveIdConfig(containedClass)
                : null;
        if (containedConfig == null) {
            LOGGER.warning("No id configuration for '" + containedClass.getName()
                    + "' referenced by id feature '" + reference.getName()
                    + "' - writing no id rather than a guessed one");
            return null;
        }

        IdSerializationEntry containedEntry =
                new IdSerializationEntry(containedConfig, containedClass, entryContext);
        Map<String, Object> containedValues = containedEntry.resolveIdValues(contained);
        if (containedValues.isEmpty()) {
            return null;
        }
        return containedEntry.combineValues(containedValues);
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

        String separator = config.getSeparator();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : idValues.entrySet()) {
            Object value = entry.getValue();
            // A component containing the separator makes the PLAIN combined id ambiguous on
            // deserialization — the split becomes the only source under keyMode=ID_ONLY (#101).
            if (value != null && value.toString().contains(separator)) {
                LOGGER.warning("Id component '" + entry.getKey() + "' of " + eClass.getName()
                        + " contains the id separator '" + separator
                        + "' — the combined id will not round-trip correctly. "
                        + "Use STRUCTURED idFormat or a different idSeparator.");
            }
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
                if (feature == null) {
                    continue;
                }
                Object value = eObject.eGet(feature);
                if (value == null) {
                    continue;
                }
                if (feature instanceof EReference reference && value instanceof EObject contained) {
                    // The identity lives in the contained object and is built from its own id
                    // configuration (spec §4, issue #120). Taking the reference value as-is
                    // put the object's toString() - identity hash included - into the id.
                    String containedId = idOfContainedObject(reference, contained);
                    if (containedId != null) {
                        result.put(featureName, containedId);
                    }
                    continue;
                }
                result.put(featureName, value);
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
