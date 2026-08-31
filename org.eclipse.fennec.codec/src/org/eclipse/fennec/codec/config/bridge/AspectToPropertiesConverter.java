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
package org.eclipse.fennec.codec.config.bridge;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackage;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;
import org.eclipse.fennec.emf.osgi.model.metadata.ClassMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.FeatureMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataRegistry;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

/**
 * Converts MetadataService aspects (ClassCodecAspect, FeatureCodecAspect, etc.)
 * into property maps that {@link org.eclipse.fennec.codec.config.ConfigurationResolver}
 * can consume as annotation properties.
 * <p>
 * This bridges the gap between the EMF-based metadata model and the
 * property-map-based configuration system.
 * </p>
 * <p>
 * The resulting property map is <b>instance-keyed</b> (issue #54 / A.1): class- and
 * feature-level config is stored under the concrete {@code EClass} /
 * {@code EStructuralFeature} instance, using the same {@code eClassConfig} /
 * {@code eReferenceConfig} / {@code eAttributeConfig} shape the
 * {@link org.eclipse.fennec.codec.config.ConfigurationResolver} already consumes
 * (Pattern 1). This keeps config correct when several package versions share one
 * nsURI (same-named classes are distinct instances → own config).
 * <pre>
 * {
 *   "eClassConfig":     { &lt;EClass&gt;             -&gt; { "typeStrategy": "URI", "typeKey": "_type" } },
 *   "eAttributeConfig": { &lt;EAttribute&gt;         -&gt; { "ignore": true, "key": "custom_name" } },
 *   "eReferenceConfig": { &lt;EReference&gt;         -&gt; { "refFormat": "STRUCTURED" } }
 * }
 * </pre>
 *
 * @see org.eclipse.fennec.codec.config.ConfigurationResolver
 * @see ClassCodecAspect
 * @see FeatureCodecAspect
 */
public final class AspectToPropertiesConverter {

    private AspectToPropertiesConverter() {
        // utility class
    }

    /**
     * Builds annotation properties from all packages registered in the MetadataService.
     *
     * @param metadataService the metadata service containing parsed aspects
     * @return a property map suitable for ConfigurationResolver.annotationProperties()
     */
    public static Map<String, Object> buildAnnotationProperties(MetadataService metadataService) {
        if (metadataService == null) {
            return Map.of();
        }

        Map<String, Object> properties = new HashMap<>();

        MetadataRegistry registry = metadataService.getRegistry();
        if (registry == null) {
            return properties;
        }

        // Instance-based annotation config (issue #54 / A.1): keyed by the concrete
        // EClass / EStructuralFeature INSTANCE, never by bare class name. Same-named
        // classes from two package versions sharing one nsURI are distinct instances
        // and therefore keep their own config (the F6 name collision is removed at the
        // source). The MetadataService is read once here; the resolver consumes these
        // maps via its instance-keyed Pattern 1 (ECLASS_CONFIG / EREFERENCE_CONFIG /
        // EATTRIBUTE_CONFIG) and memoizes per instance — no per-call service query.
        Map<EClass, Map<String, Object>> eClassConfig = new HashMap<>();
        Map<EReference, Map<String, Object>> eReferenceConfig = new HashMap<>();
        Map<EAttribute, Map<String, Object>> eAttributeConfig = new HashMap<>();

        for (PackageMetadata packageMeta : registry.getPackages()) {
            for (ClassMetadata classMeta : packageMeta.getClasses()) {
                EClass eClass = classMeta.getEClass();
                if (eClass == null) {
                    continue;
                }

                // Class-level aspect properties (type/id/supertype/discriminator).
                Map<String, Object> classProps = new HashMap<>();
                ClassCodecAspect classAspect =
                        CodecAspectProvider.codecAspect(classMeta.getAspects(), ClassCodecAspect.class);
                if (classAspect != null) {
                    extractClassAspectProperties(classAspect, classProps);
                }
                if (!classProps.isEmpty()) {
                    eClassConfig.put(eClass, classProps);
                }

                // Feature-level aspect properties, keyed by feature instance.
                for (FeatureMetadata featureMeta : classMeta.getFeatures()) {
                    EStructuralFeature feature = featureMeta.getEFeature();
                    if (feature == null) {
                        continue;
                    }

                    Map<String, Object> featureProps = new HashMap<>();

                    FeatureCodecAspect featureAspect =
                            CodecAspectProvider.codecAspect(featureMeta.getAspects(), FeatureCodecAspect.class);
                    if (featureAspect != null) {
                        extractFeatureAspectProperties(featureAspect, featureProps);
                    }

                    // Also check for ReferenceCodecAspect
                    ReferenceCodecAspect refAspect =
                            CodecAspectProvider.codecAspect(featureMeta.getAspects(), ReferenceCodecAspect.class);
                    if (refAspect != null) {
                        extractReferenceAspectProperties(refAspect, featureProps);
                    }

                    if (featureProps.isEmpty()) {
                        continue;
                    }
                    if (feature instanceof EReference reference) {
                        eReferenceConfig.put(reference, featureProps);
                    } else if (feature instanceof EAttribute attribute) {
                        eAttributeConfig.put(attribute, featureProps);
                    }
                }
            }
        }

        if (!eClassConfig.isEmpty()) {
            properties.put(ConfigProperty.ECLASS_CONFIG.getKey(), eClassConfig);
        }
        if (!eReferenceConfig.isEmpty()) {
            properties.put(ConfigProperty.EREFERENCE_CONFIG.getKey(), eReferenceConfig);
        }
        if (!eAttributeConfig.isEmpty()) {
            properties.put(ConfigProperty.EATTRIBUTE_CONFIG.getKey(), eAttributeConfig);
        }

        return properties;
    }

    // ========================================================================
    // ClassCodecAspect extraction
    // ========================================================================

    private static void extractClassAspectProperties(ClassCodecAspect aspect, Map<String, Object> props) {
        // Type config
        TypeSerializationConfig typeConfig = aspect.getTypeConfig();
        if (typeConfig != null) {
            if (typeConfig.getStrategy() != null) {
                props.put("typeStrategy", typeConfig.getStrategy().name());
            }
            putIfNotDefault(props, "typeKey", typeConfig.getTypeKey(), "_type");
            putIfNotDefault(props, "typeFormat", typeConfig.getFormat() != null ? typeConfig.getFormat().name() : null, null);
            putIfNotDefault(props, "typeSchemaKey", typeConfig.getSchemaKey(), "schema");
            putIfNotDefault(props, "typeNameKey", typeConfig.getNameKey(), "name");
            putIfNotNull(props, "typeMapId", typeConfig.getMapId());
            putIfNotNull(props, "typeDiscriminatorPath", typeConfig.getDiscriminatorPath());
            putIfNotNull(props, "typeDiscriminator", typeConfig.getDiscriminatorValue());
            // In-band EPackage fingerprint (issue #73, B.1). NONE is the default and carries
            // no information, so only an actual opt-in is forwarded.
            putIfNotDefault(props, "fingerprintMode",
                    typeConfig.getFingerprintMode() != null ? typeConfig.getFingerprintMode().name() : null,
                    FingerprintMode.NONE.name());
            putIfNotDefault(props, "fingerprintKey", typeConfig.getFingerprintKey(), "fingerprint");
        }

        // ID config
        IdSerializationConfig idConfig = aspect.getIdConfig();
        if (idConfig != null) {
            if (idConfig.getStrategy() != null) {
                props.put("idStrategy", idConfig.getStrategy().name());
            }
            // idKey and idFormat are unsettable in codec.ecore, so presence is exact here too -
            // an explicit idKey="_id" used to look like silence (issues #175/#176).
            if (idConfig.isSetIdKey()) {
                putIfNotNull(props, "idKey", idConfig.getIdKey());
            }
            if (idConfig.isSetFormat()) {
                putIfNotNull(props, "idFormat", literal(idConfig.getFormat()));
            }
            putIfNotDefault(props, "idKeyMode", idConfig.getKeyMode() != null ? idConfig.getKeyMode().name() : null, null);
            putIfNotDefault(props, "idValueKey", idConfig.getValueKey(), "id");
            putIfNotDefault(props, "idSeparator", idConfig.getSeparator(), "-");
            putIfNotDefault(props, "idSeparatorKey", idConfig.getSeparatorKey(), "separator");
            putIfNotDefault(props, "idSeparatorSerialize", idConfig.isSerializeSeparator(), Boolean.TRUE);
            putIfNotDefault(props, "idOnTop", idConfig.isOnTop(), Boolean.TRUE);
            putIfNotNull(props, "idValueReaderName", idConfig.getIdValueReaderName());
            putIfNotNull(props, "idValueWriterName", idConfig.getIdValueWriterName());
            if (!idConfig.getIdFeatures().isEmpty()) {
                props.put("idFeatures", idConfig.getIdFeatures());
            }
        }

        // SuperType config
        SuperTypeSerializationConfig superConfig = aspect.getSuperTypeConfig();
        if (superConfig != null) {
            if (superConfig.isEnabled()) {
                props.put("superTypeSerialize", true);
            }
            putIfNotDefault(props, "superTypeStrategy",
                    superConfig.getSelection() != null ? superConfig.getSelection().name() : null, null);
            putIfNotDefault(props, "superTypeKey", superConfig.getSuperTypeKey(), "_supertype");
            putIfNotDefault(props, "superTypeAsArray", superConfig.isAsArray(), Boolean.TRUE);
            putIfNotDefault(props, "superTypeSeparator", superConfig.getSeparator(), ",");
            // The runtime default is null (inherit from typeFormat), but the generated enum
            // getter can never return null - only an explicitly set format may be forwarded.
            // Requires the attribute to be unsettable in codec.ecore (issue #106).
            if (superConfig.eIsSet(CodecPackage.Literals.BASE_SUPER_TYPE_CONFIG__FORMAT)
                    && superConfig.getFormat() != null) {
                props.put("superTypeFormat", superConfig.getFormat().name());
            }
        }

        // Discriminator value
        putIfNotNull(props, "typeDiscriminator", aspect.getDiscriminatorValue());

        // Strictness
        if (aspect.isStrictOnUnknown()) {
            props.put("strictOnUnknown", true);
        }
        if (aspect.isStrictOnMissing()) {
            props.put("strictOnMissing", true);
        }

        // Metadata merge
        if (aspect.isMetadataMerge()) {
            props.put("metadataMerge", true);
            putIfNotDefault(props, "metadataKey", aspect.getMetadataKey(), "_metadata");
        }
    }

    // ========================================================================
    // FeatureCodecAspect extraction
    // ========================================================================

    /**
     * Extracts the properties a feature annotation carries.
     * <p>
     * The boolean flags are emitted whenever the annotation <em>wrote</em> them, value and all -
     * not only when they are {@code true} (issue #175). Emitting a key only for {@code true}
     * made {@code false} unrepresentable, so the most specific scope of the hierarchy could not
     * opt out of what a wider one had turned on. {@code isSetX()} is the exact question here:
     * {@code CodecAspectProvider.populateFeatureAspect} calls a setter only for a key the
     * annotation actually contains, and the attributes are unsettable in {@code codec.ecore}.
     * </p>
     */
    private static void extractFeatureAspectProperties(FeatureCodecAspect aspect, Map<String, Object> props) {
        putIfNotNull(props, "key", aspect.getEffectiveKey());

        if (aspect.isSetIgnore()) {
            props.put("ignore", aspect.isIgnore());
        }
        if (aspect.isSetIgnoreRead()) {
            props.put("ignoreRead", aspect.isIgnoreRead());
        }
        if (aspect.isSetIgnoreWrite()) {
            props.put("ignoreWrite", aspect.isIgnoreWrite());
        }
        if (aspect.isSetForceRead()) {
            props.put("forceRead", aspect.isForceRead());
        }
        if (aspect.isSetForceWrite()) {
            props.put("forceWrite", aspect.isForceWrite());
        }
        if (aspect.isSetSerializeNull()) {
            props.put("serializeNull", aspect.isSerializeNull());
        }
        if (aspect.isSetSerializeEmpty()) {
            props.put("serializeEmpty", aspect.isSerializeEmpty());
        }
        if (aspect.isSetSerializeDefaults()) {
            props.put("serializeDefault", aspect.isSerializeDefaults());
        }

        putIfNotNull(props, "valueWriterName", aspect.getValueWriterName());
        putIfNotNull(props, "valueReaderName", aspect.getValueReaderName());
        putIfNotNull(props, "dateFormat", aspect.getDateFormat());

        if (aspect.getEnumSerialization() != null) {
            props.put("enumSerialization", aspect.getEnumSerialization().name());
        }
    }

    // ========================================================================
    // ReferenceCodecAspect extraction
    // ========================================================================

    /**
     * Extracts what only a reference annotation carries: its type config and its reference
     * config (issue #175).
     * <p>
     * The feature properties of the same aspect are extracted by the caller, since
     * {@code ReferenceCodecAspect} extends {@code FeatureCodecAspect}. What is left is the part
     * that used to be dropped here: nine properties documented as feature-level in
     * 02-config-resolution.md §11.2 and §11.6 were parsed onto the aspect by
     * {@code CodecAspectProvider} and then never forwarded, so an {@code EReference} annotation
     * configuring {@code refKey} or {@code typeStrategy} had no effect at all.
     * </p>
     * <p>
     * Presence is asked of the aspect, not guessed from the value: these attributes are
     * unsettable in {@code codec.ecore}, and {@code CodecAspectProvider} writes one only for a
     * key the annotation actually contains, so {@code isSetX()} means exactly "the annotation
     * said this". Comparing against the model default instead - the proxy this used to use -
     * dropped precisely the values that equal it, and for references those are the ones a user
     * is most likely to write: {@code BaseReferenceConfig} defaults to {@code _ref} and
     * {@code PLAIN} where the codec defaults to {@code $ref} and {@code STRUCTURED}, so asking
     * for the model's own documented behaviour was the request that got lost.
     * </p>
     */
    private static void extractReferenceAspectProperties(ReferenceCodecAspect aspect, Map<String, Object> props) {
        TypeSerializationConfig typeConfig = aspect.getTypeConfig();
        if (typeConfig != null) {
            // References carry a subset of the type keys; the class-only ones (typeMapId,
            // typeDiscriminatorPath) are deliberately not parsed for a reference, so there is
            // nothing to forward for them either - see 08-discriminator-mapping.md §7.
            if (typeConfig.isSetStrategy()) {
                putIfNotNull(props, "typeStrategy", literal(typeConfig.getStrategy()));
            }
            if (typeConfig.isSetTypeKey()) {
                putIfNotNull(props, "typeKey", typeConfig.getTypeKey());
            }
            if (typeConfig.isSetFormat()) {
                putIfNotNull(props, "typeFormat", literal(typeConfig.getFormat()));
            }
            if (typeConfig.isSetSchemaKey()) {
                putIfNotNull(props, "typeSchemaKey", typeConfig.getSchemaKey());
            }
            if (typeConfig.isSetNameKey()) {
                putIfNotNull(props, "typeNameKey", typeConfig.getNameKey());
            }
        }

        ReferenceSerializationConfig referenceConfig = aspect.getReferenceConfig();
        if (referenceConfig != null) {
            if (referenceConfig.isSetFormat()) {
                putIfNotNull(props, "refFormat", literal(referenceConfig.getFormat()));
            }
            if (referenceConfig.isSetRefKey()) {
                putIfNotNull(props, "refKey", referenceConfig.getRefKey());
            }
            if (referenceConfig.isSetTypeKey()) {
                putIfNotNull(props, "refTypeKey", referenceConfig.getTypeKey());
            }
            if (referenceConfig.isSetExpand()) {
                props.put("expand", referenceConfig.isExpand());
            }
        }

        // The two id keys a reference may carry (issue #176). Only these two describe how an
        // identity is written rather than how it is built, so only these two can belong to the
        // place it is written in; the provider does not parse the others for a reference.
        IdSerializationConfig idConfig = aspect.getIdConfig();
        if (idConfig != null) {
            if (idConfig.isSetIdKey()) {
                putIfNotNull(props, "idKey", idConfig.getIdKey());
            }
            if (idConfig.isSetFormat()) {
                putIfNotNull(props, "idFormat", literal(idConfig.getFormat()));
            }
        }

        // The expand key is parsed onto the aspect as well as onto its reference config, so
        // both are consulted; they carry the same value when both were written.
        if (aspect.isSetExpand()) {
            props.put("expand", aspect.isExpand());
        }
    }

    // ========================================================================
    // Utility methods
    // ========================================================================

    private static void putIfNotNull(Map<String, Object> props, String key, Object value) {
        if (value != null) {
            props.put(key, value);
        }
    }

    private static void putIfNotDefault(Map<String, Object> props, String key, Object value, Object defaultValue) {
        if (value != null && !value.equals(defaultValue)) {
            props.put(key, value);
        }
    }

    /** The literal name of an enum value, or {@code null} when there is none. */
    private static String literal(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
