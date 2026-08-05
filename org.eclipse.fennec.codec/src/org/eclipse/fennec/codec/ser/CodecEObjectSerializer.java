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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.context.EMFCodecWriteContext;
import org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Jackson serializer for EMF EObjects using effective configuration.
 * <p>
 * This serializer orchestrates the serialization of EObjects by delegating to
 * specialized {@link SerializationEntry} implementations for each concern:
 * <ul>
 *   <li>{@link IdSerializationEntry} - ID field</li>
 *   <li>{@link TypeSerializationEntry} - Type information</li>
 *   <li>{@link SuperTypeSerializationEntry} - Supertype information</li>
 *   <li>{@link AttributeSerializationEntry} - Attribute values</li>
 *   <li>{@link ReferenceSerializationEntry} - Reference values</li>
 * </ul>
 * </p>
 * <p>
 * The serialization order follows the spec:
 * <ol>
 *   <li>ID (if enabled and idOnTop)</li>
 *   <li>Type (if enabled)</li>
 *   <li>SuperTypes (if enabled)</li>
 *   <li>Features (attributes and references)</li>
 * </ol>
 * </p>
 * <p>
 * Configuration is provided via {@link EffectiveCodecConfig} which wraps
 * {@link org.eclipse.fennec.codec.config.ConfigurationResolver} for on-demand
 * configuration resolution with caching.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-eobject-serialization.md">Spec 6: EObject Serialization</a>
 * @see <a href="docs/codec-v2-spec/16-annotation-reference.md#field-ordering">Spec 16: Field Ordering</a>
 * @author Mark Hoffmann
 * @since 2026-02-01
 */
public class CodecEObjectSerializer extends ValueSerializer<EObject> {

    private final EffectiveCodecConfig config;
    private final CodecEntryContext entryContext;

    /**
     * Creates a new CodecEObjectSerializer with the effective codec configuration.
     *
     * @param config the effective codec configuration
     */
    public CodecEObjectSerializer(EffectiveCodecConfig config) {
        this.config = config;
        // Create entry context once for all entries
        this.entryContext = CodecEntryContext.builder()
                .effectiveConfig(config)
                .diagnostics(config.getDiagnostics())
                .valueRegistry(config.getValueRegistry())
                .build();
    }

    @Override
    public Class<EObject> handledType() {
        return EObject.class;
    }

    @Override
    public void serialize(EObject value, JsonGenerator gen, SerializationContext ctxt) {
        if (value == null) {
            gen.writeNull();
            return;
        }

        // Update context if available
        if (gen.streamWriteContext() instanceof EMFCodecWriteContext emfContext) {
            emfContext.setCurrentEObject(value);
        }

        EClass eClass = value.eClass();

        // Each resource root establishes its own smart-compression context (issue #76): a root
        // writes a full URI, nested objects consume the schema it established. With several
        // roots, every one of them is a root - not nested content of the first.
        if (isResourceRoot(gen)) {
            ContextHelper.resetRootContext(ctxt);
        }

        // Set context schema for smart compression (only for root object)
        initializeContextSchemaIfNeeded(eClass, ctxt);

        // Resolve configurations for this EClass, considering per-reference overrides
        EReference currentRef = ContextHelper.getCurrentSerializationReference(ctxt);
        TypeConfig typeConfig = currentRef != null
                ? config.resolveTypeConfig(eClass, currentRef)
                : config.resolveTypeConfig(eClass);
        IdConfig idConfig = config.resolveIdConfig(eClass);
        SuperTypeConfig superTypeConfig = config.resolveSuperTypeConfig(eClass);

        // Build ordered serialization entries
        Map<String, SerializationEntry> entries = buildSerializationEntries(
                eClass, typeConfig, idConfig, superTypeConfig);

        // Apply ordering
        entries = applyOrdering(entries, idConfig, eClass);

        // Create serialization state with value cache
        SerializationState state = new SerializationState(value);

        // Write object
        gen.writeStartObject(value);
        for (Map.Entry<String, SerializationEntry> entry : entries.entrySet()) {
            SerializationEntry serEntry = entry.getValue();
            if (serEntry.shouldSerialize(state, ctxt)) {
                serEntry.serialize(state, gen, ctxt);
            }
        }
        gen.writeEndObject();
    }

    /**
     * Builds the serialization entries for the given EClass.
     *
     * @param eClass the EClass of the object
     * @param typeConfig the effective type configuration
     * @param idConfig the effective ID configuration
     * @param superTypeConfig the effective supertype configuration
     * @return map of key to serialization entry
     */
    private Map<String, SerializationEntry> buildSerializationEntries(
            EClass eClass,
            TypeConfig typeConfig,
            IdConfig idConfig,
            SuperTypeConfig superTypeConfig) {

        Map<String, SerializationEntry> entries = new LinkedHashMap<>();

        // Determine if type format is STRUCTURED
        boolean isStructuredFormat = typeConfig != null && typeConfig.isInclude()
                && typeConfig.getFormat() == SerializationFormat.STRUCTURED;

        // Create supertype entry if enabled (needed before type entry for STRUCTURED format)
        SuperTypeSerializationEntry superTypeEntry = null;
        if (superTypeConfig != null && superTypeConfig.isSerialize()) {
            superTypeEntry = new SuperTypeSerializationEntry(
                    superTypeConfig, eClass, config.isSmartCompression());
        }

        // Default order: _type → _supertype → _id → features  (spec §8.7, idOnTop=false)
        // With idOnTop=true, applyOrdering() promotes _id before _type.

        // Add type entry (if include is enabled)
        if (typeConfig != null && typeConfig.isInclude()) {
            TypeSerializationEntry typeEntry;
            if (isStructuredFormat && superTypeEntry != null) {
                // STRUCTURED format: supertype is embedded inside _type object
                typeEntry = new TypeSerializationEntry(
                        typeConfig, eClass, superTypeEntry, config);
            } else {
                // PLAIN format: supertype is a separate field
                typeEntry = new TypeSerializationEntry(
                        typeConfig, eClass, null, config);
            }
            entries.put(typeEntry.getKey(), typeEntry);
        }

        // Add supertype entry as separate field only when Type format is PLAIN
        // (for STRUCTURED format, supertype is embedded inside _type object)
        if (superTypeEntry != null && !isStructuredFormat) {
            entries.put(superTypeEntry.getKey(), superTypeEntry);
        }

        // Add ID entry after type/supertype (the entry itself handles FEATURE_ONLY/NONE via shouldSerialize())
        Set<String> idOnlyComponents = Set.of();
        if (idConfig != null) {
            IdSerializationEntry idEntry = new IdSerializationEntry(idConfig, eClass, entryContext);
            entries.put(idEntry.getKey(), idEntry);
            if (idConfig.getKeyMode() == IdKeyMode.ID_ONLY) {
                idOnlyComponents = Set.copyOf(idEntry.getIdFeatureNames());
            }
        }

        // Add feature entries
        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            // Get effective feature configuration (cached by resolver)
            FeatureConfig featureConfig = config.resolveFeatureConfig(feature);

            // Skip features that should not be serialized
            // shouldSerialize() considers ignore, ignoreWrite, forceWrite flags
            if (!featureConfig.shouldSerialize()) {
                continue;
            }

            // keyMode=ID_ONLY: id component attributes live only under the id key (spec 09-id.md
            // §2/§7, issue #101). EReference id sources keep their payload entry (spec §4), and an
            // explicit forceWrite wins over the suppression.
            if (feature instanceof EAttribute && idOnlyComponents.contains(feature.getName())
                    && !featureConfig.isForceWrite()) {
                continue;
            }

            SerializationEntry featureEntry;
            if (feature instanceof EAttribute attribute) {
                featureEntry = new AttributeSerializationEntry(featureConfig, attribute, entryContext);
            } else if (feature instanceof EReference reference) {
                // Resolve per-reference config from ReferenceConfig
                ReferenceConfig refConfig = config.resolveReferenceConfig(reference);

                featureEntry = new ReferenceSerializationEntry(
                        featureConfig, reference, refConfig, config.isSmartCompression(), config,
                        entryContext);
            } else {
                continue;
            }

            entries.put(featureEntry.getKey(), featureEntry);
        }

        return entries;
    }

    /**
     * Applies ordering rules to the serialization entries.
     * <p>
     * Handles:
     * <ul>
     *   <li>Alphabetical sorting (if configured)</li>
     *   <li>ID on top (if configured)</li>
     * </ul>
     * </p>
     *
     * @param entries the unordered entries
     * @param idConfig the effective ID configuration (may be null)
     * @return ordered entries
     */
    private Map<String, SerializationEntry> applyOrdering(
            Map<String, SerializationEntry> entries,
            IdConfig idConfig,
            EClass eClass) {

        boolean sortAlphabetically = config.isSortPropertiesAlphabetically();
        boolean idOnTop = idConfig != null && idConfig.isOnTop();
        String idKey = idConfig != null ? idConfig.getKey() : null;

        // Resolve the EIDAttribute feature key (e.g. "id") so idOnTop floats it as well.
        // This is distinct from idKey ("_id") — the synthetic codec key.
        String idFeatureKey = null;
        if (idOnTop && eClass != null) {
            EAttribute eIdAttr = eClass.getEIDAttribute();
            if (eIdAttr != null) {
                FeatureConfig fConfig = config.resolveFeatureConfig(eIdAttr);
                String fKey = fConfig.getKey() != null ? fConfig.getKey() : eIdAttr.getName();
                if (!fKey.equals(idKey) && entries.containsKey(fKey)) {
                    idFeatureKey = fKey;
                }
            }
        }

        if (!sortAlphabetically && !idOnTop) {
            return entries;
        }

        LinkedHashMap<String, SerializationEntry> ordered = new LinkedHashMap<>();

        if (sortAlphabetically) {
            TreeMap<String, SerializationEntry> sorted = new TreeMap<>(entries);
            if (idOnTop) {
                if (idKey != null && sorted.containsKey(idKey)) {
                    ordered.put(idKey, sorted.remove(idKey));
                }
                if (idFeatureKey != null && sorted.containsKey(idFeatureKey)) {
                    ordered.put(idFeatureKey, sorted.remove(idFeatureKey));
                }
            }
            ordered.putAll(sorted);
        } else if (idOnTop) {
            if (idKey != null && entries.containsKey(idKey)) {
                ordered.put(idKey, entries.get(idKey));
            }
            if (idFeatureKey != null) {
                ordered.put(idFeatureKey, entries.get(idFeatureKey));
            }
            for (Map.Entry<String, SerializationEntry> entry : entries.entrySet()) {
                String key = entry.getKey();
                if (!key.equals(idKey) && !key.equals(idFeatureKey)) {
                    ordered.put(key, entry.getValue());
                }
            }
        } else {
            return entries;
        }

        return ordered;
    }

    /**
     * Initializes the context schema URI for smart compression.
     * <p>
     * The context schema is set once from the first (root) object and used
     * to determine if subsequent objects belong to the same schema.
     * Only initializes if smart compression is enabled and context schema
     * is not already set.
     * </p>
     *
     * @param eClass the EClass of the current object
     * @param ctxt the serialization context
     * @see <a href="docs/codec-v2-spec/04-global-options.md#1-smart-compression">Spec: Smart Compression</a>
     */
    /**
     * Reports whether the object about to be written is a <b>resource root</b> rather than
     * nested content (issue #76).
     * <p>
     * Determined from the generator's write context, which at this point still describes the
     * <em>surroundings</em> of the object — {@code writeStartObject} has not happened yet. A
     * resource root is therefore either written directly at the root, or is an element of the
     * document's top-level array. A nested array (a multi-valued containment) is an array whose
     * parent is an object, and is correctly not a root.
     * </p>
     *
     * @param gen the JSON generator
     * @return true if this object is a root of the resource
     */
    private static boolean isResourceRoot(JsonGenerator gen) {
        TokenStreamContext context = gen.streamWriteContext();
        if (context == null) {
            // No structural information: the object can only be the root.
            return true;
        }
        if (context.inRoot()) {
            return true;
        }
        if (context.inArray()) {
            TokenStreamContext parent = context.getParent();
            return parent == null || parent.inRoot();
        }
        return false;
    }

    private void initializeContextSchemaIfNeeded(EClass eClass, SerializationContext ctxt) {
        // Only initialize if smart compression is enabled
        if (!config.isSmartCompression()) {
            return;
        }

        // Only set context schema once (for root object)
        if (ContextHelper.getContextSchemaUri(ctxt) != null) {
            return;
        }

        // Get schema URI from the EClass's package
        EPackage ePackage = eClass.getEPackage();
        if (ePackage != null) {
            String schemaUri = ePackage.getNsURI();
            ContextHelper.setContextSchemaUri(ctxt, schemaUri);
        }
    }
}
