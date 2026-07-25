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

import java.util.List;
import java.util.Objects;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorReader;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.eclipse.fennec.model.metadata.api.MetadataService;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serialization entry for EObject type information.
 * <p>
 * Handles the serialization of the type property based on the
 * type configuration. No fallback logic is needed as all
 * configuration resolution happens in the {@link org.eclipse.fennec.codec.config.ConfigurationResolver}.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#3-type-serialization">Spec 3: Type Serialization</a>
 * @author Mark Hoffmann
 * @since 2025-12-16
 */
public class TypeSerializationEntry implements SerializationEntry {

    private final TypeConfig config;
    private final EffectiveCodecConfig codecConfig;
    private final EClass eClass;
    private final String typeValue;
    private final SuperTypeSerializationEntry superTypeEntry;

    /**
     * Creates a new TypeSerializationEntry with the type configuration.
     *
     * @param config the type configuration
     * @param eClass the EClass being serialized (for computing type value if no discriminator)
     */
    public TypeSerializationEntry(TypeConfig config, EClass eClass) {
        this(config, eClass, null, null);
    }

    /**
     * Creates a new TypeSerializationEntry with the type configuration
     * and optional supertype entry for STRUCTURED format embedding.
     *
     * @param config the type configuration
     * @param eClass the EClass being serialized (for computing type value if no discriminator)
     * @param superTypeEntry optional supertype entry to embed in STRUCTURED format
     */
    public TypeSerializationEntry(TypeConfig config, EClass eClass, SuperTypeSerializationEntry superTypeEntry) {
        this(config, eClass, superTypeEntry, null);
    }

    /**
     * Creates a new TypeSerializationEntry with the type configuration,
     * optional supertype entry, and codec configuration for smart compression.
     *
     * @param config the type configuration
     * @param eClass the EClass being serialized (for computing type value if no discriminator)
     * @param superTypeEntry optional supertype entry to embed in STRUCTURED format
     * @param codecConfig the codec configuration for smart compression (may be null)
     */
    public TypeSerializationEntry(TypeConfig config, EClass eClass,
            SuperTypeSerializationEntry superTypeEntry, EffectiveCodecConfig codecConfig) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.eClass = Objects.requireNonNull(eClass, "eClass must not be null");
        this.typeValue = resolveTypeValue(eClass);
        this.superTypeEntry = superTypeEntry;
        this.codecConfig = codecConfig;
    }

    @Override
    public String getKey() {
        return config.getTypeKey();
    }

    @Override
    public boolean shouldSerialize(SerializationState state) {
        // Don't serialize when strategy is NONE (spec: "no type information should be written")
        if (config.getStrategy() == TypeStrategy.NONE) {
            return false;
        }
        // Don't serialize _type when discriminatorPath is configured
        // because the type info is already embedded in the content at the feature path
        if (hasDiscriminatorPath()) {
            return false;
        }
        return config.isInclude();
    }

    @Override
    public boolean shouldSerialize(SerializationState state, SerializationContext ctxt) {
        // Check for smart compression suppress flag (instance type == reference type)
        if (ContextHelper.isSuppressType(ctxt)) {
            // Clear the flag so it doesn't affect subsequent objects
            ContextHelper.clearSuppressType(ctxt);
            // A due fingerprint outweighs the omission: the carrier lives in the type
            // context, so dropping the type here would silently lose the version
            // information the caller asked to record (spec 06 §8.3).
            if (isFingerprintPending(ctxt)) {
                return shouldSerialize(state);
            }
            return false;
        }
        return shouldSerialize(state);
    }

    /**
     * Checks if a non-standard discriminator path is configured.
     * <p>
     * When a discriminator path is set to a nested/non-standard path (e.g.,
     * "info.profileName"), the type information is already embedded in the
     * content at that path. In this case, we should NOT write a separate
     * "_type" field.
     * </p>
     * <p>
     * However, when the discriminator path equals the type key (e.g., both
     * are "_type"), this is the standard discriminator mapping flow — the
     * type entry should still serialize but will use the mapped discriminator
     * value (e.g., "temp") instead of the strategy-based value.
     * See spec 08-discriminator-mapping.md §8.1.1 vs §8.1.2.
     * </p>
     *
     * @return true if discriminatorPath is a nested path different from typeKey
     */
    private boolean hasDiscriminatorPath() {
        String path = config.getDiscriminatorPath();
        if (path == null || path.isEmpty()) {
            return false;
        }
        // Standard path: discriminatorPath matches typeKey — don't suppress
        String typeKey = config.getTypeKey();
        if (path.equals(typeKey)) {
            return false;
        }
        return true;
    }

    @Override
    public void serialize(SerializationState state, JsonGenerator gen, SerializationContext ctxt) {
        SerializationFormat format = config.getFormat();

        if (format == SerializationFormat.STRUCTURED) {
            serializeStructured(gen, ctxt);
        } else {
            serializePlain(gen, ctxt);
        }
    }

    /**
     * Serializes type information in PLAIN format.
     *
     * @param gen the JSON generator
     * @param ctxt the serialization context (for smart compression)
     */
    private void serializePlain(JsonGenerator gen, SerializationContext ctxt) {
        TypeStrategy strategy = config.getStrategy();
        if (strategy == TypeStrategy.SCHEMA_AND_TYPE) {
            // SCHEMA_AND_TYPE in PLAIN format writes TWO separate fields
            EPackage ePackage = eClass.getEPackage();
            if (ePackage != null) {
                String plainSchemaKey = getPlainSchemaKey();
                gen.writeStringProperty(plainSchemaKey, ePackage.getNsURI());
            }
            gen.writeStringProperty(config.getTypeKey(), eClass.getName());
        } else {
            // Check for inline mapping discriminator override first (reference-scoped),
            // then type mapping registry (class-scoped), then fall back to strategy value.
            // See spec 08-discriminator-mapping.md §8.3 for combined serialization order.
            String effectiveValue = resolveInlineMappingDiscriminator(ctxt);
            if (effectiveValue == null) {
                effectiveValue = resolveTypeMappingDiscriminator();
            }
            if (effectiveValue == null) {
                effectiveValue = applySmartCompression(typeValue, ctxt);
            }
            gen.writeStringProperty(config.getTypeKey(), effectiveValue);
        }
        writeFingerprintIfDue(gen, ctxt, config.getPlainFingerprintKey());
    }

    /**
     * Writes the in-band EPackage fingerprint when one is due at this site (issue #73, B.1).
     * <p>
     * Nothing is written unless {@code fingerprintMode} opted in and the first-touch rule
     * says this site is the one that has to announce the version. In PLAIN format the value
     * becomes a sibling of the type key; in STRUCTURED format an inner key of the type
     * object — both are the same type context, so the caller only decides which key form to
     * pass.
     * </p>
     *
     * @param gen the JSON generator
     * @param ctxt the serialization context holding the per-save pins
     * @param key the key to write under (PLAIN sibling form or STRUCTURED inner form)
     */
    private void writeFingerprintIfDue(JsonGenerator gen, SerializationContext ctxt, String key) {
        if (!config.isFingerprintWriteEnabled() || ctxt == null) {
            return;
        }
        EPackage ePackage = eClass.getEPackage();
        if (ePackage == null) {
            return;
        }
        if (!ContextHelper.getFingerprintPins(ctxt).isDue(ePackage)) {
            return;
        }
        String fingerprint = resolveFingerprint(ePackage);
        if (fingerprint != null && !fingerprint.isEmpty()) {
            gen.writeStringProperty(key, fingerprint);
        }
    }

    /**
     * Looks up the package fingerprint via the MetadataService.
     *
     * @param ePackage the package to fingerprint
     * @return the fingerprint, or {@code null} if no metadata is available for the package
     */
    private String resolveFingerprint(EPackage ePackage) {
        if (codecConfig == null) {
            return null;
        }
        MetadataService metadataService = codecConfig.getMetadataService();
        if (metadataService == null) {
            return null;
        }
        PackageMetadata metadata = metadataService.getPackageMetadata(ePackage);
        return metadata != null ? metadata.getModelFingerprint() : null;
    }

    /**
     * Reports whether a fingerprint would be due for this object's package, without
     * advancing the first-touch state.
     * <p>
     * Used to keep type omission from swallowing the carrier: where a fingerprint has to be
     * written, the type context must exist to hold it.
     * </p>
     *
     * @param ctxt the serialization context
     * @return true if the fingerprint is opted in and this package has not been announced yet
     */
    private boolean isFingerprintPending(SerializationContext ctxt) {
        if (!config.isFingerprintWriteEnabled() || ctxt == null) {
            return false;
        }
        EPackage ePackage = eClass.getEPackage();
        if (ePackage == null) {
            return false;
        }
        EPackage pinned = ContextHelper.getFingerprintPins(ctxt).pinnedVersion(ePackage.getNsURI());
        return pinned != ePackage;
    }

    /**
     * Gets the schema key for PLAIN format.
     *
     * @return the schema key with underscore prefix for PLAIN format
     */
    private String getPlainSchemaKey() {
        String schemaKey = config.getSchemaKey();
        if (schemaKey.startsWith("_") || schemaKey.startsWith("@")) {
            return schemaKey;
        }
        return "_" + schemaKey;
    }

    /**
     * Serializes type information in STRUCTURED format.
     *
     * @param gen the JSON generator
     * @param ctxt the serialization context (for smart compression)
     */
    private void serializeStructured(JsonGenerator gen, SerializationContext ctxt) {
        gen.writeName(config.getTypeKey());
        gen.writeStartObject();

        TypeStrategy strategy = config.getStrategy();
        if (strategy == null) {
            strategy = TypeStrategy.URI;
        }

        switch (strategy) {
            case NUMERIC:
                EPackage numericPkg = eClass.getEPackage();
                if (numericPkg != null) {
                    gen.writeStringProperty(config.getSchemaKey(), numericPkg.getNsURI());
                }
                gen.writeNumberProperty("classifier", eClass.getClassifierID());
                break;

            case SCHEMA_AND_TYPE:
                EPackage schemaPkg = eClass.getEPackage();
                if (schemaPkg != null) {
                    gen.writeStringProperty(config.getSchemaKey(), schemaPkg.getNsURI());
                }
                gen.writeStringProperty(config.getNameKey(), eClass.getName());
                break;

            default:
                // Check for discriminator override: inline mapping first, then type mapping registry
                String discriminatorValue = resolveInlineMappingDiscriminator(ctxt);
                if (discriminatorValue == null) {
                    discriminatorValue = resolveTypeMappingDiscriminator();
                }
                String effectiveTypeValue = discriminatorValue != null
                        ? discriminatorValue
                        : applySmartCompression(typeValue, ctxt);
                gen.writeStringProperty(config.getNameKey(), effectiveTypeValue);
                break;
        }

        // Version identity belongs next to the type identity, before the supertype.
        writeFingerprintIfDue(gen, ctxt, config.getFingerprintKey());

        // Include supertype inside the _type object when STRUCTURED format
        serializeSuperTypeInStructured(gen);

        gen.writeEndObject();
    }

    /**
     * Serializes supertype information inside the STRUCTURED _type object.
     *
     * @param gen the JSON generator
     */
    private void serializeSuperTypeInStructured(JsonGenerator gen) {
        if (superTypeEntry == null) {
            return;
        }

        List<String> superTypes = superTypeEntry.getSuperTypeValues();
        if (superTypes.isEmpty()) {
            return;
        }

        SuperTypeConfig superTypeConfig = superTypeEntry.getConfig();
        String superTypeKey = superTypeConfig.getSuperTypeKey();

        if (superTypeConfig.isAsArray()) {
            gen.writeArrayPropertyStart(superTypeKey);
            for (String superType : superTypes) {
                gen.writeString(superType);
            }
            gen.writeEndArray();
        } else {
            String joined = String.join(superTypeConfig.getSeparator(), superTypes);
            gen.writeStringProperty(superTypeKey, joined);
        }
    }

    /**
     * Resolves the type value to serialize based on the configured strategy.
     *
     * @param eClass the EClass
     * @return the type value string
     */
    private String resolveTypeValue(EClass eClass) {
        String discriminator = config.getDiscriminatorValue();
        if (discriminator != null && !discriminator.isEmpty()) {
            return discriminator;
        }

        TypeStrategy strategy = config.getStrategy();
        if (strategy == null) {
            strategy = TypeStrategy.URI;
        }

        switch (strategy) {
            case NAME:
                return eClass.getName();
            case CLASS:
                Class<?> instanceClass = eClass.getInstanceClass();
                return instanceClass != null ? instanceClass.getName() : eClass.getName();
            case NUMERIC:
                return String.valueOf(eClass.getClassifierID());
            case URI:
            default:
                return EcoreUtil.getURI(eClass).toString();
        }
    }

    /**
     * Resolves the discriminator value for inline mapping reverse lookup.
     * <p>
     * When serializing an EObject contained in an EReference with an inlineMapping
     * annotation, this method looks up the correct discriminator value from the
     * reference-scoped registry in {@link TypeDiscriminatorReader}.
     * </p>
     *
     * @param ctxt the serialization context
     * @return the inline mapping discriminator value, or null if not in an inline mapping context
     */
    private String resolveInlineMappingDiscriminator(SerializationContext ctxt) {
        if (codecConfig == null) {
            return null;
        }
        EReference currentRef = ContextHelper.getCurrentSerializationReference(ctxt);
        if (currentRef == null) {
            return null;
        }
        TypeDiscriminatorReader typeService = codecConfig.getTypeDiscriminatorReader();
        if (typeService == null) {
            return null;
        }
        return typeService.getDiscriminatorValueForReference(currentRef, eClass);
    }

    /**
     * Resolves the discriminator value for type mapping registry lookup.
     * <p>
     * When the EClass being serialized belongs to a type mapping registry
     * (identified by a mapId from the typeMapping annotation), this method
     * looks up the reverse mapping to get the discriminator value string
     * (e.g., "temp" for TempSensor).
     * </p>
     * <p>
     * See spec 08-discriminator-mapping.md §8.1 for the type mapping
     * registry serialization flow.
     * </p>
     *
     * @return the type mapping discriminator value, or null if not in a type mapping context
     */
    private String resolveTypeMappingDiscriminator() {
        if (codecConfig == null) {
            return null;
        }
        TypeDiscriminatorReader typeService = codecConfig.getTypeDiscriminatorReader();
        if (typeService == null) {
            return null;
        }
        String mapId = typeService.getMapIdForEClass(eClass);
        if (mapId == null) {
            return null;
        }
        return typeService.getDiscriminatorValue(mapId, eClass);
    }

    /**
     * Applies smart compression to a type value if enabled.
     *
     * @param typeValue the original type value (may be a full URI)
     * @param ctxt the serialization context
     * @return the compressed type value (simple name) or original value
     */
    private String applySmartCompression(String typeValue, SerializationContext ctxt) {
        if (codecConfig == null || !codecConfig.isSmartCompression()) {
            return typeValue;
        }

        if (!ContextHelper.isRootSerialized(ctxt)) {
            ContextHelper.setRootSerialized(ctxt);
            return typeValue;
        }

        if (ContextHelper.isSameSchema(ctxt, typeValue)) {
            return ContextHelper.extractSimpleName(typeValue);
        }

        return typeValue;
    }
}
