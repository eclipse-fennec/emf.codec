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

import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.CODEC_SOURCE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_DATE_FORMAT;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ENUM_SERIALIZATION;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_EXPAND;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_FINGERPRINT_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_FINGERPRINT_MODE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_FORCE_READ;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_FORCE_WRITE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_FEATURES;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_FORMAT;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_FORMAT_SCOPE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_KEY_MODE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_ON_TOP;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_SCOPE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_SEPARATOR;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_SEPARATOR_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_SERIALIZE_SEPARATOR;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_STRATEGY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_VALUE_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_VALUE_READER_NAME;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_ID_VALUE_WRITER_NAME;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_IGNORE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_IGNORE_READ;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_IGNORE_WRITE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_INHERIT;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_REF_FORMAT;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_REF_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_REF_TYPE_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SERIALIZE_DEFAULTS;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SERIALIZE_EMPTY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SERIALIZE_NULL;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_METADATA_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_METADATA_MERGE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_STRICT_ON_MISSING;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_STRICT_ON_UNKNOWN;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SUPERTYPE_AS_ARRAY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SUPERTYPE_FORMAT;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SUPERTYPE_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SUPERTYPE_SEPARATOR;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SUPERTYPE_SERIALIZE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_SUPERTYPE_STRATEGY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_DISCRIMINATOR;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_DISCRIMINATOR_PATH;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_FORMAT;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_FORMAT_SCOPE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_INCLUDE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_MAP_ID;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_NAME_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_SCHEMA_KEY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_SCOPE;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_STRATEGY;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_VALUE_READER_NAME;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_TYPE_VALUE_WRITER_NAME;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_VALUE_READER_NAME;
import static org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants.KEY_VALUE_WRITER_NAME;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile;
import org.eclipse.fennec.codec.metadata.model.codec.CodecFactory;
import org.eclipse.fennec.codec.metadata.model.codec.CodecPackageProfile;
import org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode;
import org.eclipse.fennec.codec.metadata.model.codec.FeatureSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode;
import org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.IdStrategy;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSelection;
import org.eclipse.fennec.codec.metadata.model.codec.SuperTypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.codec.metadata.util.AnnotationParseHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataHandler;
import org.eclipse.fennec.emf.osgi.model.metadata.AspectEntry;
import org.eclipse.fennec.emf.osgi.model.metadata.AttributeMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.ClassMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.DiagnosticSeverity;
import org.eclipse.fennec.emf.osgi.model.metadata.FeatureMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataDiagnostic;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataFactory;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.ReferenceMetadata;

/**
 * {@link MetadataHandler} implementation for codec serialization metadata.
 * <p>
 * Parses EAnnotations from EMF model elements and creates codec-specific
 * aspects (ClassCodecAspect, FeatureCodecAspect, ReferenceCodecAspect)
 * with serialization configuration. The aspects are attached to the metadata
 * tree as {@link AspectEntry} instances with type id {@value #ASPECT_TYPE_ID};
 * diagnostics collected while parsing go to {@link AspectEntry#getDiagnostics()}.
 * The package-level entry carries the pre-computed {@link CodecPackageProfile}.
 * </p>
 * <p>
 * All annotations use the unified source {@code http://eclipse.org/fennec/codec}
 * with configuration specified through detail key-value pairs.
 * </p>
 * <p>
 * {@link #onPackageRegistered(PackageMetadata)} is invoked once per model version
 * (fingerprint) while the metadata tree is being built, before publication. The
 * provider is stateless and never calls back into the {@code MetadataService}.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public class CodecAspectProvider implements MetadataHandler {

    /** Aspect type identifier for codec aspect entries. */
    public static final String ASPECT_TYPE_ID = "codec";

    private final CodecFactory factory = CodecFactory.eINSTANCE;

    @Override
    public void onPackageRegistered(PackageMetadata packageMetadata) {
        Objects.requireNonNull(packageMetadata, "packageMetadata must not be null");

        for (ClassMetadata classMetadata : packageMetadata.getClasses()) {
            classMetadata.getAspects().add(buildClassAspectEntry(classMetadata));
            for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
                featureMetadata.getAspects().add(buildFeatureAspectEntry(featureMetadata));
            }
            // The codec does not serialize EOperations, so there is no
            // operation-level configuration to contribute (issue #53).
        }

        // Codec does not define package-level configuration of its own; the package
        // entry carries the pre-computed profile built over the finished class entries.
        packageMetadata.getAspects().add(buildProfileEntry(packageMetadata));
    }

    /**
     * Creates a codec {@link AspectEntry} wrapping the given content.
     */
    private static AspectEntry newEntry(EObject content) {
        AspectEntry entry = MetadataFactory.eINSTANCE.createAspectEntry();
        entry.setTypeId(ASPECT_TYPE_ID);
        entry.setContent(content);
        return entry;
    }

    /**
     * Finds the codec aspect content of the given type among aspect entries.
     * <p>
     * The single supported way for consumers to read a codec aspect out of a metadata
     * element: {@link AspectEntry#getContent()} is a bare {@link EObject}, so filtering the
     * entries themselves by aspect type silently yields nothing — it compiles, because the
     * type check runs against {@code Object}. This method matches on {@link #ASPECT_TYPE_ID}
     * and on the content type.
     * </p>
     *
     * @param <T> the aspect type
     * @param aspects the aspect entries of a package, class or feature
     * @param type the aspect type to look for
     * @return the matching aspect content, or {@code null} if this element has none
     */
    public static <T> T codecAspect(EList<AspectEntry> aspects, Class<T> type) {
        for (AspectEntry entry : aspects) {
            if (ASPECT_TYPE_ID.equals(entry.getTypeId()) && type.isInstance(entry.getContent())) {
                return type.cast(entry.getContent());
            }
        }
        return null;
    }

    /**
     * Builds the codec aspect entry for an EClass.
     */
    private AspectEntry buildClassAspectEntry(ClassMetadata classMetadata) {
        Objects.requireNonNull(classMetadata, "classMetadata must not be null");
        EClass eClass = classMetadata.getEClass();
        ClassCodecAspect aspect = factory.createClassCodecAspect();
        AspectEntry entry = newEntry(aspect);
        EList<MetadataDiagnostic> diagnostics = entry.getDiagnostics();

        EAnnotation codecAnnotation = eClass.getEAnnotation(CODEC_SOURCE);
        if (codecAnnotation != null) {
            parseClassAnnotation(aspect, diagnostics, codecAnnotation, eClass);
        }

        // Scan for typeMapping/{mapId} dedicated annotation sources
        parseTypeMappingAnnotations(aspect, eClass);

        // Misconfig: inlineMapping annotation on EClass → WARNING
        EAnnotation inlineMappingOnClass = eClass.getEAnnotation(CodecAnnotationConstants.INLINE_MAPPING_SOURCE);
        if (inlineMappingOnClass != null) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "inlineMapping annotation on EClass '" + eClass.getName()
                            + "' is invalid — inline mappings are per-reference only (use on EReference)",
                    CodecAnnotationConstants.INLINE_MAPPING_SOURCE);
        }

        return entry;
    }

    /**
     * Builds the codec aspect entry for a feature, dispatching on the concrete
     * {@link FeatureMetadata} subtype.
     */
    private AspectEntry buildFeatureAspectEntry(FeatureMetadata featureMetadata) {
        Objects.requireNonNull(featureMetadata, "featureMetadata must not be null");
        if (featureMetadata instanceof ReferenceMetadata refMd) {
            return buildReferenceAspectEntry(refMd);
        } else if (featureMetadata instanceof AttributeMetadata attrMd) {
            return buildAttributeAspectEntry(attrMd);
        }
        throw new IllegalArgumentException("Unsupported feature metadata type: " + featureMetadata.getClass().getName());
    }

    /**
     * Builds the codec aspect entry for an EAttribute.
     */
    private AspectEntry buildAttributeAspectEntry(AttributeMetadata attributeMetadata) {
        EAttribute attribute = attributeMetadata.getEAttribute();
        FeatureCodecAspect aspect = factory.createFeatureCodecAspect();
        AspectEntry entry = newEntry(aspect);
        EList<MetadataDiagnostic> diagnostics = entry.getDiagnostics();

        populateFeatureAspect(aspect, attribute);

        // T-V5: Any type* key on EAttribute → ERROR
        // ST-V2: Any superType* key on EAttribute → ERROR
        // ID-V13: Any id* key on EAttribute → ERROR
        // R-V2/R-V4: ref*/expand on EAttribute → ERROR
        // Strictness keys on EAttribute → WARNING (class-only)
        EAnnotation codecAnnotation = attribute.getEAnnotation(CODEC_SOURCE);
        if (codecAnnotation != null) {
            Map<String, String> details = codecAnnotation.getDetails().map();
            checkForTypeKeysOnAttribute(diagnostics, details, attribute);
            checkForSuperTypeKeysOnAttribute(diagnostics, details, attribute);
            checkForIdKeysOnAttribute(diagnostics, details, attribute);
            checkForReferenceOnlyKeysOnAttribute(diagnostics, details, attribute);
            checkForStrictnessKeysOnFeature(diagnostics, details, attribute);
            checkForMetadataMergeKeysOnFeature(diagnostics, details, attribute);
        }

        return entry;
    }

    /**
     * Builds the codec aspect entry for an EReference.
     */
    private AspectEntry buildReferenceAspectEntry(ReferenceMetadata referenceMetadata) {
        EReference reference = referenceMetadata.getEReference();
        ReferenceCodecAspect aspect = factory.createReferenceCodecAspect();
        AspectEntry entry = newEntry(aspect);
        EList<MetadataDiagnostic> diagnostics = entry.getDiagnostics();

        populateFeatureAspect(aspect, reference);

        // Reference-specific parsing
        EAnnotation codecAnnotation = reference.getEAnnotation(CODEC_SOURCE);
        if (codecAnnotation != null) {
            Map<String, String> details = codecAnnotation.getDetails().map();

            // Check for class-only keys and add diagnostics (keys are ignored but logged)
            checkForClassOnlyKeys(diagnostics, details, reference);

            // T-V3/T-V4: runtime-only keys in EAnnotation
            checkForRuntimeOnlyKeys(diagnostics, details, reference);

            // ST-V1: superType* keys on EReference → ERROR (class-intrinsic)
            checkForSuperTypeKeysOnReference(diagnostics, details, reference);

            // ID-V1 through ID-V10: class-only ID keys on EReference → ERROR
            checkForIdClassOnlyKeysOnReference(diagnostics, details, reference);

            // ID-V11/ID-V12: runtime-only ID keys in EAnnotation
            checkForIdRuntimeOnlyKeys(diagnostics, details, reference);

            // Parse type config (for polymorphic references)
            // Note: References use a subset of type config keys - class-only keys are ignored
            if (hasReferenceTypeConfig(details)) {
                aspect.setTypeConfig(buildReferenceTypeConfig(details));
            }

            // Parse reference config
            if (hasReferenceConfig(details)) {
                aspect.setReferenceConfig(buildReferenceConfig(details));
            }

            // Parse the two id keys a reference may carry (issue #176). The class-only ones are
            // reported above by checkForIdClassOnlyKeysOnReference and are not parsed here.
            if (hasReferenceIdConfig(details)) {
                aspect.setIdConfig(buildReferenceIdConfig(details));
            }

            // Parse expand flag
            AnnotationParseHelper.ifBooleanPresent(details, KEY_EXPAND, aspect::setExpand);

            // Note: inline type mappings are now on dedicated inlineMapping annotation source,
            // parsed by the caller via TypeDiscriminatorService.registerInlineMappings().
            // Fallback strategy and fallbackEClass are also on the inlineMapping source.

            // Strictness keys on EReference → WARNING (class-only)
            checkForStrictnessKeysOnFeature(diagnostics, details, reference);

            // Metadata merge keys on EReference → ERROR (class-only)
            checkForMetadataMergeKeysOnFeature(diagnostics, details, reference);
        }

        // Misconfig: typeMapping/{mapId} annotation on EReference → ERROR
        for (EAnnotation ann : reference.getEAnnotations()) {
            if (CodecAnnotationConstants.isTypeMappingSource(ann.getSource())) {
                addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                        "typeMapping annotation on EReference '" + reference.getName()
                                + "' is invalid — type mapping registry is class-level (use on EClass)",
                        ann.getSource());
            }
        }

        // Note: inlineMapping annotation on EReference is valid and expected.
        // It is parsed externally by TypeDiscriminatorService.registerInlineMappings().

        return entry;
    }

    /**
     * Builds the package-level codec aspect entry carrying the {@link CodecPackageProfile}.
     * <p>
     * Reads the class and feature entries attached by {@link #buildClassAspectEntry} and
     * {@link #buildFeatureAspectEntry}, so it must run after the class traversal.
     * </p>
     */
    private AspectEntry buildProfileEntry(PackageMetadata packageMetadata) {
        CodecPackageProfile pkgProfile = factory.createCodecPackageProfile();

        // Package-wide defaults from a codec annotation on the EPackage itself (issue #75).
        // The package level is annotation-INTERNAL: it is merged into every class profile here
        // rather than becoming a further runtime configuration level, so the cascading merge of
        // options, resource, factory and module keeps seeing exactly one annotation layer.
        ClassCodecAspect packageDefaults = buildPackageDefaults(packageMetadata.getEPackage());

        for (ClassMetadata classMeta : packageMetadata.getClasses()) {
            CodecClassProfile classProfile = buildClassProfile(classMeta, packageDefaults);
            pkgProfile.getClassProfiles().add(classProfile);
        }

        return newEntry(pkgProfile);
    }

    // ========================================================================
    // Profile Building
    // ========================================================================

    /**
     * Reads package-wide codec defaults from an annotation on the EPackage itself (issue #75).
     * <p>
     * Reuses {@link ClassCodecAspect} as the carrier because it already holds exactly the
     * type/ID/supertype configs a package default can express — no separate model element is
     * needed for a layer that is merged away at profile-build time.
     * </p>
     *
     * @param ePackage the package, may be {@code null}
     * @return the defaults, or {@code null} if the package carries no codec annotation
     */
    private ClassCodecAspect buildPackageDefaults(EPackage ePackage) {
        if (ePackage == null) {
            return null;
        }
        EAnnotation annotation = ePackage.getEAnnotation(CODEC_SOURCE);
        if (annotation == null) {
            return null;
        }
        Map<String, String> details = annotation.getDetails().map();

        ClassCodecAspect defaults = factory.createClassCodecAspect();
        if (hasTypeConfig(details)) {
            defaults.setTypeConfig(buildTypeConfig(details));
        }
        if (hasIdConfig(details)) {
            defaults.setIdConfig(buildIdConfig(details));
        }
        if (hasSuperTypeConfig(details)) {
            defaults.setSuperTypeConfig(buildSuperTypeConfig(details));
        }
        return defaults;
    }

    /**
     * Returns the codec annotation details of a model element, or an empty map if it carries none.
     *
     * @param element the model element, may be {@code null}
     * @return the details, never {@code null}
     */
    private static Map<String, String> codecAnnotationDetails(EModelElement element) {
        if (element == null) {
            return Map.of();
        }
        EAnnotation annotation = element.getEAnnotation(CODEC_SOURCE);
        return annotation != null ? annotation.getDetails().map() : Map.of();
    }

    /**
     * Carries over the type-config fields that do not come from the main codec annotation.
     * <p>
     * {@code mapId}, {@code discriminatorPath} and {@code discriminatorValue} are parsed from the
     * dedicated {@code typeMapping/&lt;mapId&gt;} annotation sources, so re-applying the main
     * annotation's details would drop them.
     * </p>
     */
    private static void copyTypeMappingFields(TypeSerializationConfig from, TypeSerializationConfig to) {
        if (from.getMapId() != null) {
            to.setMapId(from.getMapId());
        }
        if (from.getDiscriminatorPath() != null) {
            to.setDiscriminatorPath(from.getDiscriminatorPath());
        }
        if (from.getDiscriminatorValue() != null) {
            to.setDiscriminatorValue(from.getDiscriminatorValue());
        }
    }

    /**
     * Builds a CodecClassProfile for a single EClass from its metadata.
     * <p>
     * Copies class-level configs (type, id, supertype) from ClassCodecAspect
     * if present, or creates default configs via factory (EMF defaults apply).
     * Builds one FeatureSerializationConfig per feature.
     * </p>
     */
    private CodecClassProfile buildClassProfile(ClassMetadata classMeta, ClassCodecAspect packageDefaults) {
        CodecClassProfile classProfile = factory.createCodecClassProfile();
        classProfile.setEClass(classMeta.getEClass());

        // Find the ClassCodecAspect attached by buildClassAspectEntry
        ClassCodecAspect classAspect = codecAspect(classMeta.getAspects(), ClassCodecAspect.class);

        // Each config starts from the package default (if any); the class then states its
        // exceptions on top, key by key. The class layer is applied from its own annotation
        // details rather than from the parsed aspect, because only the details say which keys the
        // class actually wrote - see the note on buildTypeConfig(Map, TypeSerializationConfig).
        Map<String, String> classDetails = codecAnnotationDetails(classMeta.getEClass());

        TypeSerializationConfig typeConfig = packageDefaults != null && packageDefaults.getTypeConfig() != null
                ? EcoreUtil.copy(packageDefaults.getTypeConfig())
                : factory.createTypeSerializationConfig();
        classProfile.setTypeConfig(buildTypeConfig(classDetails, typeConfig));

        IdSerializationConfig idConfig = packageDefaults != null && packageDefaults.getIdConfig() != null
                ? EcoreUtil.copy(packageDefaults.getIdConfig())
                : factory.createIdSerializationConfig();
        classProfile.setIdConfig(buildIdConfig(classDetails, idConfig));

        SuperTypeSerializationConfig superTypeConfig =
                packageDefaults != null && packageDefaults.getSuperTypeConfig() != null
                ? EcoreUtil.copy(packageDefaults.getSuperTypeConfig())
                : factory.createSuperTypeSerializationConfig();
        classProfile.setSuperTypeConfig(buildSuperTypeConfig(classDetails, superTypeConfig));

        // Keep whatever the aspect resolved beyond the annotation keys (e.g. mapId and
        // discriminators parsed from the dedicated typeMapping sources).
        if (classAspect != null && classAspect.getTypeConfig() != null) {
            copyTypeMappingFields(classAspect.getTypeConfig(), classProfile.getTypeConfig());
        }

        // Build FeatureSerializationConfig for each feature
        for (FeatureMetadata featureMeta : classMeta.getFeatures()) {
            FeatureSerializationConfig featureConfig = buildFeatureConfig(featureMeta);
            classProfile.getFeatureConfigs().add(featureConfig);
        }

        return classProfile;
    }

    /**
     * Builds a FeatureSerializationConfig from feature metadata.
     * <p>
     * Populates from the FeatureCodecAspect (or ReferenceCodecAspect)
     * attached to the feature. If no aspect exists, creates a config
     * with defaults.
     * </p>
     */
    private FeatureSerializationConfig buildFeatureConfig(FeatureMetadata featureMeta) {
        FeatureSerializationConfig config = factory.createFeatureSerializationConfig();
        config.setFeatureName(featureMeta.getName());

        // Find the FeatureCodecAspect (or ReferenceCodecAspect) attached by buildFeatureAspectEntry
        FeatureCodecAspect featureAspect = codecAspect(featureMeta.getAspects(), FeatureCodecAspect.class);

        if (featureAspect != null) {
            // Key: use effectiveKey from aspect if set, otherwise use feature name
            config.setKey(featureAspect.getEffectiveKey() != null
                    ? featureAspect.getEffectiveKey()
                    : featureMeta.getName());

            // Visibility flags
            config.setIgnore(featureAspect.isIgnore());
            config.setIgnoreRead(featureAspect.isIgnoreRead());
            config.setIgnoreWrite(featureAspect.isIgnoreWrite());
            config.setForceRead(featureAspect.isForceRead());
            config.setForceWrite(featureAspect.isForceWrite());
            config.setSerializeNull(featureAspect.isSerializeNull());
            config.setSerializeEmpty(featureAspect.isSerializeEmpty());
            config.setSerializeDefaults(featureAspect.isSerializeDefaults());

            // Value reader/writer
            config.setValueWriterName(featureAspect.getValueWriterName());
            config.setValueReaderName(featureAspect.getValueReaderName());

            // Enum serialization
            config.setEnumSerialization(featureAspect.getEnumSerialization());

            // Reference-specific properties
            if (featureAspect instanceof ReferenceCodecAspect refAspect) {
                if (refAspect.getReferenceConfig() != null) {
                    config.setReferenceConfig(EcoreUtil.copy(refAspect.getReferenceConfig()));
                }
                if (refAspect.getTypeConfig() != null) {
                    config.setTypeConfig(EcoreUtil.copy(refAspect.getTypeConfig()));
                }
                config.setExpand(refAspect.isExpand());
            }
        } else {
            // No aspect: apply defaults (all ignore/force flags default to false)
            config.setKey(featureMeta.getName());
        }

        return config;
    }

    // ========================================================================
    // Class Annotation Parsing
    // ========================================================================

    /**
     * Parses codec annotation and populates the class aspect. Diagnostics go to the
     * owning entry's list.
     */
    private void parseClassAnnotation(ClassCodecAspect aspect, EList<MetadataDiagnostic> diagnostics,
            EAnnotation annotation, EClass eClass) {
        Map<String, String> details = annotation.getDetails().map();

        // Validation: T-V3/T-V4 - runtime-only keys in EAnnotation
        checkForRuntimeOnlyKeys(diagnostics, details, eClass);

        // Validation: T-V30/T-V31 - deprecated typeInclude
        checkForDeprecatedTypeInclude(diagnostics, details, eClass);

        // Validation: ID-V11/ID-V12 - runtime-only ID keys in EAnnotation
        checkForIdRuntimeOnlyKeys(diagnostics, details, eClass);

        // Validation: R-V1/R-V3 - reference-only keys on EClass
        checkForReferenceOnlyKeysOnClass(diagnostics, details, eClass);

        // Parse ID configuration (id* keys)
        if (hasIdConfig(details)) {
            aspect.setIdConfig(buildIdConfig(details));
        }

        // Parse type configuration (type* keys)
        if (hasTypeConfig(details)) {
            aspect.setTypeConfig(buildTypeConfig(details));
        }

        // Parse supertype configuration (superType* keys)
        if (hasSuperTypeConfig(details)) {
            aspect.setSuperTypeConfig(buildSuperTypeConfig(details));
        }

        // Parse inherit flag
        AnnotationParseHelper.ifBooleanPresent(details, KEY_INHERIT, aspect::setInheritFromParent);

        // Note: typeDiscriminator is now parsed from typeMapping/{mapId} annotation source
        // in parseTypeMappingAnnotations(), not from the main codec annotation.

        // Parse strictness flags (class-level deserialization behavior)
        AnnotationParseHelper.ifBooleanPresent(details, KEY_STRICT_ON_UNKNOWN, aspect::setStrictOnUnknown);
        AnnotationParseHelper.ifBooleanPresent(details, KEY_STRICT_ON_MISSING, aspect::setStrictOnMissing);

        // Parse metadata merge flags (class-level output assembly)
        AnnotationParseHelper.ifBooleanPresent(details, KEY_METADATA_MERGE, aspect::setMetadataMerge);
        AnnotationParseHelper.ifStringPresent(details, KEY_METADATA_KEY, aspect::setMetadataKey);
    }

    /**
     * Parses {@code typeMapping/{mapId}} dedicated annotation sources on an EClass.
     * <p>
     * Extracts mapId from the annotation source URI, sets it on the type config,
     * and parses discriminatorPath, typeDiscriminator, and discriminatorValue.
     * Static mappings and fallback config are NOT stored on the model — they are
     * registered directly with {@link TypeDiscriminatorService} by the caller.
     * </p>
     */
    private void parseTypeMappingAnnotations(ClassCodecAspect aspect, EClass eClass) {
        for (EAnnotation ann : eClass.getEAnnotations()) {
            String mapId = CodecAnnotationConstants.extractMapIdFromSource(ann.getSource());
            if (mapId == null) {
                continue;
            }

            Map<String, String> details = ann.getDetails().map();

            // Ensure type config exists
            TypeSerializationConfig typeConfig = aspect.getTypeConfig();
            if (typeConfig == null) {
                typeConfig = factory.createTypeSerializationConfig();
                aspect.setTypeConfig(typeConfig);
            }

            // Set mapId from annotation source URI
            typeConfig.setMapId(mapId);

            // Parse discriminator path (base class defines where to find discriminator in JSON)
            AnnotationParseHelper.ifStringPresent(details, KEY_TYPE_DISCRIMINATOR_PATH, typeConfig::setDiscriminatorPath);

            // Parse discriminator value (concrete class registers itself)
            String discriminatorValue = details.get(KEY_TYPE_DISCRIMINATOR);
            if (discriminatorValue != null && !discriminatorValue.isEmpty()) {
                typeConfig.setDiscriminatorValue(discriminatorValue);
                aspect.setDiscriminatorValue(discriminatorValue);
            }

            // Note: static mappings (other key/value pairs) and fallback config
            // (fallbackStrategy, fallbackEClass) are NOT stored on the model.
            // They are registered with TypeDiscriminatorService during metadata processing.
        }
    }

    // ========================================================================
    // Feature Annotation Parsing
    // ========================================================================

    /**
     * Populates common feature aspect properties from annotation.
     * <p>
     * Note: effectiveKey is only set if there's an explicit "key" annotation.
     * If not set, the ConfigurationMerger will use ExtendedMetaData name (if enabled)
     * or fall back to the feature name.
     * </p>
     */
    private void populateFeatureAspect(FeatureCodecAspect aspect, EStructuralFeature feature) {
        // Defaults: all ignore/force flags are false (set by ecore default)

        EAnnotation codecAnnotation = feature.getEAnnotation(CODEC_SOURCE);
        if (codecAnnotation != null) {
            Map<String, String> details = codecAnnotation.getDetails().map();

            // Parse explicit key override - only set effectiveKey if explicitly specified
            AnnotationParseHelper.ifStringPresent(details, KEY_KEY, aspect::setEffectiveKey);

            // Parse directional visibility flags
            AnnotationParseHelper.ifBooleanPresent(details, KEY_IGNORE, aspect::setIgnore);
            AnnotationParseHelper.ifBooleanPresent(details, KEY_IGNORE_READ, aspect::setIgnoreRead);
            AnnotationParseHelper.ifBooleanPresent(details, KEY_IGNORE_WRITE, aspect::setIgnoreWrite);
            AnnotationParseHelper.ifBooleanPresent(details, KEY_FORCE_READ, aspect::setForceRead);
            AnnotationParseHelper.ifBooleanPresent(details, KEY_FORCE_WRITE, aspect::setForceWrite);

            // Parse serializeNull, serializeEmpty, serializeDefaults
            AnnotationParseHelper.ifBooleanPresent(details, KEY_SERIALIZE_NULL, aspect::setSerializeNull);
            AnnotationParseHelper.ifBooleanPresent(details, KEY_SERIALIZE_EMPTY, aspect::setSerializeEmpty);
            AnnotationParseHelper.ifBooleanPresent(details, KEY_SERIALIZE_DEFAULTS, aspect::setSerializeDefaults);

            // Parse value writer/reader names
            AnnotationParseHelper.ifStringPresent(details, KEY_VALUE_WRITER_NAME, aspect::setValueWriterName);
            AnnotationParseHelper.ifStringPresent(details, KEY_VALUE_READER_NAME, aspect::setValueReaderName);

            // Parse date format - only valid for EAttributes per spec
            if (feature instanceof EAttribute) {
                AnnotationParseHelper.ifStringPresent(details, KEY_DATE_FORMAT, aspect::setDateFormat);
            }

            // Parse enum serialization strategy - only valid for EAttributes per spec
            // See annotation-scope-reference.md: enumSerialization is ❌ on EReference, ✅ on EAttribute
            if (feature instanceof EAttribute) {
                AnnotationParseHelper.ifEnumPresent(details, KEY_ENUM_SERIALIZATION,
                        EnumSerializationStrategy.class, aspect::setEnumSerialization);
            }
        }
    }

    // ========================================================================
    // Configuration Builders
    // ========================================================================

    /**
     * Builds IdSerializationConfig from annotation details.
     */
    private IdSerializationConfig buildIdConfig(Map<String, String> details) {
        return buildIdConfig(details, factory.createIdSerializationConfig());
    }

    /**
     * Applies the annotation details onto an existing config (issue #75).
     * <p>
     * Only keys actually <b>present</b> in {@code details} are written, which is what layers a
     * class over its package correctly. {@code eIsSet} cannot serve here: for a generated EMF
     * attribute that is not {@code unsettable} it means "differs from the default", so a class
     * explicitly restating the default value would be indistinguishable from saying nothing.
     * </p>
     *
     * @param details the annotation details of this level
     * @param config the config to apply them to, typically a copy of the more general level
     * @return {@code config}, for chaining
     */
    private IdSerializationConfig buildIdConfig(Map<String, String> details, IdSerializationConfig config) {

        // Strategy
        AnnotationParseHelper.ifEnumPresent(details, KEY_ID_STRATEGY, IdStrategy.class, config::setStrategy);

        // Key (property name)
        AnnotationParseHelper.ifStringPresent(details, KEY_ID_KEY, config::setIdKey);

        // Separator for combined IDs
        AnnotationParseHelper.ifStringPresent(details, KEY_ID_SEPARATOR, config::setSeparator);

        // ID features for combined strategy. A level that states idFeatures REPLACES the
        // inherited list — restating the key is an override, not an addition (issue #102).
        String idFeatures = details.get(KEY_ID_FEATURES);
        if (idFeatures != null) {
            config.getIdFeatures().clear();
            for (String token : idFeatures.split(",")) {
                String featureName = token.trim();
                if (!featureName.isEmpty()) {
                    config.getIdFeatures().add(featureName);
                }
            }
        }

        // Custom reader/writer
        AnnotationParseHelper.ifStringPresent(details, KEY_ID_VALUE_READER_NAME, config::setIdValueReaderName);
        AnnotationParseHelper.ifStringPresent(details, KEY_ID_VALUE_WRITER_NAME, config::setIdValueWriterName);

        // Format (PLAIN/STRUCTURED)
        AnnotationParseHelper.ifEnumPresent(details, KEY_ID_FORMAT, SerializationFormat.class, config::setFormat);

        // Key mode (ID_ONLY, BOTH, FEATURE_ONLY)
        AnnotationParseHelper.ifEnumPresent(details, KEY_ID_KEY_MODE, IdKeyMode.class, config::setKeyMode);

        // On top (ID before type)
        AnnotationParseHelper.ifBooleanPresent(details, KEY_ID_ON_TOP, config::setOnTop);

        // Serialize separator
        AnnotationParseHelper.ifBooleanPresent(details, KEY_ID_SERIALIZE_SEPARATOR, config::setSerializeSeparator);

        // Separator key
        AnnotationParseHelper.ifStringPresent(details, KEY_ID_SEPARATOR_KEY, config::setSeparatorKey);

        // Value key (inner key in STRUCTURED format)
        AnnotationParseHelper.ifStringPresent(details, KEY_ID_VALUE_KEY, config::setValueKey);

        return config;
    }

    /**
     * Builds TypeSerializationConfig from annotation details for EClass.
     * <p>
     * Note: Discriminator-based type resolution is now orthogonal to type strategy.
     * The discriminatorPath specifies an additional resolution mechanism that works
     * alongside the primary type strategy, not as a replacement.
     * </p>
     */
    private TypeSerializationConfig buildTypeConfig(Map<String, String> details) {
        return buildTypeConfig(details, factory.createTypeSerializationConfig());
    }

    /**
     * Applies the annotation details onto an existing config (issue #75).
     * <p>
     * Only keys actually <b>present</b> in {@code details} are written, which is what layers a
     * class over its package correctly. {@code eIsSet} cannot serve here: for a generated EMF
     * attribute that is not {@code unsettable} it means "differs from the default", so a class
     * explicitly restating the default value would be indistinguishable from saying nothing.
     * </p>
     *
     * @param details the annotation details of this level
     * @param config the config to apply them to, typically a copy of the more general level
     * @return {@code config}, for chaining
     */
    private TypeSerializationConfig buildTypeConfig(Map<String, String> details, TypeSerializationConfig config) {

        // Strategy - explicit strategy only, discriminator is orthogonal
        AnnotationParseHelper.ifEnumPresent(details, KEY_TYPE_STRATEGY, TypeStrategy.class, config::setStrategy);

        // Type key
        AnnotationParseHelper.ifStringPresent(details, KEY_TYPE_KEY, config::setTypeKey);

        // Note: discriminatorPath and mapId are now parsed from typeMapping/{mapId}
        // annotation source in parseTypeMappingAnnotations(), not from main codec annotation.

        // Note: fallbackStrategy and fallbackEClass are now managed by
        // TypeDiscriminatorRegistry/TypeDiscriminatorService, not stored on the model.

        // Format (PLAIN/STRUCTURED)
        AnnotationParseHelper.ifEnumPresent(details, KEY_TYPE_FORMAT, SerializationFormat.class, config::setFormat);

        // Schema key
        AnnotationParseHelper.ifStringPresent(details, KEY_TYPE_SCHEMA_KEY, config::setSchemaKey);

        // Name key
        AnnotationParseHelper.ifStringPresent(details, KEY_TYPE_NAME_KEY, config::setNameKey);

        // In-band EPackage fingerprint (issue #73, B.1). Write-side configuration only:
        // the read key comes from caller-side sources, never from the model (see
        // CodecAnnotationConstants.KEY_FINGERPRINT_KEY).
        AnnotationParseHelper.ifEnumPresent(details, KEY_FINGERPRINT_MODE, FingerprintMode.class,
                config::setFingerprintMode);
        AnnotationParseHelper.ifStringPresent(details, KEY_FINGERPRINT_KEY, config::setFingerprintKey);

        return config;
    }

    /**
     * Builds IdSerializationConfig from annotation details for EReference (issue #176).
     * <p>
     * Only {@code idKey} and {@code idFormat} are read: they describe how an identity is
     * <em>written</em>, which is a property of the place it is written in, so they may be scoped
     * to a containment reference. Everything else about an identity is class-intrinsic and is
     * an error on an EReference - see 09-id.md section 4.4.
     * </p>
     */
    private IdSerializationConfig buildReferenceIdConfig(Map<String, String> details) {
        IdSerializationConfig config = factory.createIdSerializationConfig();
        AnnotationParseHelper.ifStringPresent(details, KEY_ID_KEY, config::setIdKey);
        AnnotationParseHelper.ifEnumPresent(details, KEY_ID_FORMAT, SerializationFormat.class,
                config::setFormat);
        return config;
    }

    /**
     * Builds TypeSerializationConfig from annotation details for EReference.
     * <p>
     * References support a subset of type config keys. Class-only keys like
     * typeMapId and typeDiscriminatorPath are NOT parsed here - they are ignored
     * per spec (see 08-discriminator-mapping.md section 7).
     * </p>
     */
    private TypeSerializationConfig buildReferenceTypeConfig(Map<String, String> details) {
        TypeSerializationConfig config = factory.createTypeSerializationConfig();

        // Strategy
        AnnotationParseHelper.ifEnumPresent(details, KEY_TYPE_STRATEGY, TypeStrategy.class, config::setStrategy);

        // Type key - valid on references for inline mappings
        AnnotationParseHelper.ifStringPresent(details, KEY_TYPE_KEY, config::setTypeKey);

        // Format (PLAIN/STRUCTURED)
        AnnotationParseHelper.ifEnumPresent(details, KEY_TYPE_FORMAT, SerializationFormat.class, config::setFormat);

        // Schema key
        AnnotationParseHelper.ifStringPresent(details, KEY_TYPE_SCHEMA_KEY, config::setSchemaKey);

        // Name key
        AnnotationParseHelper.ifStringPresent(details, KEY_TYPE_NAME_KEY, config::setNameKey);

        // NOTE: The following are intentionally NOT parsed for references:
        // - typeMapId: class-only (type mapping registry is class-level)
        // - typeDiscriminatorPath: class-only (discriminator path is defined on base class)
        // See 08-discriminator-mapping.md section 7 "Invalid Configurations"

        return config;
    }

    /**
     * Builds SuperTypeSerializationConfig from annotation details.
     */
    private SuperTypeSerializationConfig buildSuperTypeConfig(Map<String, String> details) {
        return buildSuperTypeConfig(details, factory.createSuperTypeSerializationConfig());
    }

    /**
     * Applies the annotation details onto an existing config (issue #75).
     * <p>
     * Only keys actually <b>present</b> in {@code details} are written, which is what layers a
     * class over its package correctly. {@code eIsSet} cannot serve here: for a generated EMF
     * attribute that is not {@code unsettable} it means "differs from the default", so a class
     * explicitly restating the default value would be indistinguishable from saying nothing.
     * </p>
     *
     * @param details the annotation details of this level
     * @param config the config to apply them to, typically a copy of the more general level
     * @return {@code config}, for chaining
     */
    private SuperTypeSerializationConfig buildSuperTypeConfig(Map<String, String> details, SuperTypeSerializationConfig config) {

        // Enabled
        AnnotationParseHelper.ifBooleanPresent(details, KEY_SUPERTYPE_SERIALIZE, config::setEnabled);

        // Key
        AnnotationParseHelper.ifStringPresent(details, KEY_SUPERTYPE_KEY, config::setSuperTypeKey);

        // Strategy/Selection
        AnnotationParseHelper.ifEnumPresent(details, KEY_SUPERTYPE_STRATEGY, SuperTypeSelection.class, config::setSelection);

        // As array
        AnnotationParseHelper.ifBooleanPresent(details, KEY_SUPERTYPE_AS_ARRAY, config::setAsArray);

        // Separator
        AnnotationParseHelper.ifStringPresent(details, KEY_SUPERTYPE_SEPARATOR, config::setSeparator);

        // Format (PLAIN/STRUCTURED)
        AnnotationParseHelper.ifEnumPresent(details, KEY_SUPERTYPE_FORMAT, SerializationFormat.class, config::setFormat);

        // Note: schemaKey is inherited from TypeConfig, not configured separately for supertype
        // Note: nameKey removed - superTypeKey has format-dependent default

        return config;
    }

    /**
     * Builds ReferenceSerializationConfig from annotation details.
     */
    private ReferenceSerializationConfig buildReferenceConfig(Map<String, String> details) {
        ReferenceSerializationConfig config = factory.createReferenceSerializationConfig();

        // Format (PLAIN/STRUCTURED)
        AnnotationParseHelper.ifEnumPresent(details, KEY_REF_FORMAT, SerializationFormat.class, config::setFormat);

        // Ref key
        AnnotationParseHelper.ifStringPresent(details, KEY_REF_KEY, config::setRefKey);

        // Type key
        AnnotationParseHelper.ifStringPresent(details, KEY_REF_TYPE_KEY, config::setTypeKey);

        // Expand
        AnnotationParseHelper.ifBooleanPresent(details, KEY_EXPAND, config::setExpand);

        return config;
    }

    // ========================================================================
    // Config Detection Helpers
    // ========================================================================

    /**
     * Checks if the details map contains any ID configuration keys.
     */
    private boolean hasIdConfig(Map<String, String> details) {
        return details.containsKey(KEY_ID_STRATEGY)
                || details.containsKey(KEY_ID_KEY)
                || details.containsKey(KEY_ID_SEPARATOR)
                || details.containsKey(KEY_ID_FEATURES)
                || details.containsKey(KEY_ID_VALUE_READER_NAME)
                || details.containsKey(KEY_ID_VALUE_WRITER_NAME)
                || details.containsKey(KEY_ID_FORMAT)
                || details.containsKey(KEY_ID_KEY_MODE)
                || details.containsKey(KEY_ID_ON_TOP)
                || details.containsKey(KEY_ID_SERIALIZE_SEPARATOR)
                || details.containsKey(KEY_ID_SEPARATOR_KEY)
                || details.containsKey(KEY_ID_VALUE_KEY);
    }

    /**
     * Checks if the details map contains any type configuration keys (for EClass).
     */
    private boolean hasTypeConfig(Map<String, String> details) {
        return details.containsKey(KEY_TYPE_STRATEGY)
                || details.containsKey(KEY_TYPE_KEY)
                || details.containsKey(KEY_TYPE_FORMAT)
                || details.containsKey(KEY_TYPE_SCHEMA_KEY)
                || details.containsKey(KEY_TYPE_NAME_KEY)
                || details.containsKey(KEY_FINGERPRINT_MODE)
                || details.containsKey(KEY_FINGERPRINT_KEY);
        // Note: typeMapId, typeDiscriminatorPath, fallbackStrategy, fallbackEClass
        // are now on typeMapping/{mapId} dedicated annotation source, not main codec
    }

    /**
     * Checks if the details map contains any type configuration keys valid for EReference.
     * <p>
     * Note: typeMapId and typeDiscriminatorPath are class-only and not checked here.
     * </p>
     */
    private boolean hasReferenceIdConfig(Map<String, String> details) {
        return details.containsKey(KEY_ID_KEY) || details.containsKey(KEY_ID_FORMAT);
        // NOTE: every other id key is class-intrinsic on an EReference and is reported by
        // checkForIdClassOnlyKeysOnReference - see 09-id.md section 4.4
    }

    private boolean hasReferenceTypeConfig(Map<String, String> details) {
        return details.containsKey(KEY_TYPE_STRATEGY)
                || details.containsKey(KEY_TYPE_KEY)
                || details.containsKey(KEY_TYPE_FORMAT)
                || details.containsKey(KEY_TYPE_SCHEMA_KEY)
                || details.containsKey(KEY_TYPE_NAME_KEY);
        // NOTE: KEY_TYPE_MAP_ID and KEY_TYPE_DISCRIMINATOR_PATH are NOT checked
        // as they are class-only per spec (08-discriminator-mapping.md section 7)
    }

    /**
     * Checks if the details map contains any supertype configuration keys.
     */
    private boolean hasSuperTypeConfig(Map<String, String> details) {
        return details.containsKey(KEY_SUPERTYPE_SERIALIZE)
                || details.containsKey(KEY_SUPERTYPE_KEY)
                || details.containsKey(KEY_SUPERTYPE_STRATEGY)
                || details.containsKey(KEY_SUPERTYPE_AS_ARRAY)
                || details.containsKey(KEY_SUPERTYPE_FORMAT);
        // Note: KEY_SUPERTYPE_SCHEMA_KEY removed - supertype inherits schemaKey from Type config
        // Note: KEY_SUPERTYPE_NAME_KEY removed - superTypeKey has format-dependent default
    }

    /**
     * Checks if the details map contains any reference configuration keys.
     */
    private boolean hasReferenceConfig(Map<String, String> details) {
        return details.containsKey(KEY_REF_FORMAT)
                || details.containsKey(KEY_REF_KEY)
                || details.containsKey(KEY_REF_TYPE_KEY);
    }

    // ========================================================================
    // Diagnostic Helpers
    // ========================================================================

    /**
     * Checks for class-only annotation keys on a reference and adds diagnostics.
     * <p>
     * Per spec (06-type.md section 7, 08-discriminator-mapping.md section 7):
     * </p>
     * <ul>
     *   <li>typeMapId (D-3): class-only → WARNING</li>
     *   <li>typeDiscriminatorPath (D-2): class-only → WARNING</li>
     *   <li>typeValueReaderName (T-V1): class-intrinsic → ERROR</li>
     *   <li>typeValueWriterName (T-V2): class-intrinsic → ERROR</li>
     *   <li>typeDiscriminator (T-V7): class-only → ERROR</li>
     * </ul>
     */
    private void checkForClassOnlyKeys(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EReference reference) {
        if (details.containsKey(KEY_TYPE_MAP_ID)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_TYPE_MAP_ID + "' is not valid on EReference '" +
                    reference.getName() + "', ignored (class-only key)",
                    KEY_TYPE_MAP_ID);
        }
        if (details.containsKey(KEY_TYPE_DISCRIMINATOR_PATH)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_TYPE_DISCRIMINATOR_PATH + "' is not valid on EReference '" +
                    reference.getName() + "', ignored (class-only key)",
                    KEY_TYPE_DISCRIMINATOR_PATH);
        }
        // T-V1: typeValueReaderName on EReference → ERROR (class-intrinsic)
        if (details.containsKey(KEY_TYPE_VALUE_READER_NAME)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                    "Annotation key '" + KEY_TYPE_VALUE_READER_NAME + "' is not valid on EReference '" +
                    reference.getName() + "', ignored (class-intrinsic: applies to objects of that EClass, not per-reference)",
                    KEY_TYPE_VALUE_READER_NAME);
        }
        // T-V2: typeValueWriterName on EReference → ERROR (class-intrinsic)
        if (details.containsKey(KEY_TYPE_VALUE_WRITER_NAME)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                    "Annotation key '" + KEY_TYPE_VALUE_WRITER_NAME + "' is not valid on EReference '" +
                    reference.getName() + "', ignored (class-intrinsic: applies to objects of that EClass, not per-reference)",
                    KEY_TYPE_VALUE_WRITER_NAME);
        }
        // T-V7: typeDiscriminator on EReference → ERROR (class-only)
        if (details.containsKey(KEY_TYPE_DISCRIMINATOR)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                    "Annotation key '" + KEY_TYPE_DISCRIMINATOR + "' is not valid on EReference '" +
                    reference.getName() + "', ignored (class-only key, see 08-discriminator-mapping.md)",
                    KEY_TYPE_DISCRIMINATOR);
        }
    }

    /**
     * Checks for runtime-only keys in any EAnnotation and adds diagnostics.
     * <p>
     * Per spec (06-type.md section 7):
     * </p>
     * <ul>
     *   <li>typeScope (T-V3): runtime-only → WARNING</li>
     *   <li>typeFormatScope (T-V4): runtime-only → WARNING</li>
     * </ul>
     */
    private void checkForRuntimeOnlyKeys(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EStructuralFeature feature) {
        if (details.containsKey(KEY_TYPE_SCOPE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_TYPE_SCOPE + "' on feature '" +
                    feature.getName() + "' is a runtime-only property (set via load/save options), ignored in EAnnotation",
                    KEY_TYPE_SCOPE);
        }
        if (details.containsKey(KEY_TYPE_FORMAT_SCOPE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_TYPE_FORMAT_SCOPE + "' on feature '" +
                    feature.getName() + "' is a runtime-only property (set via load/save options), ignored in EAnnotation",
                    KEY_TYPE_FORMAT_SCOPE);
        }
    }

    /**
     * Checks for runtime-only keys in class-level EAnnotation and adds diagnostics.
     * <p>
     * Per spec (06-type.md section 7):
     * </p>
     * <ul>
     *   <li>typeScope (T-V3): runtime-only → WARNING</li>
     *   <li>typeFormatScope (T-V4): runtime-only → WARNING</li>
     * </ul>
     */
    private void checkForRuntimeOnlyKeys(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EClass eClass) {
        if (details.containsKey(KEY_TYPE_SCOPE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_TYPE_SCOPE + "' on EClass '" +
                    eClass.getName() + "' is a runtime-only property (set via load/save options), ignored in EAnnotation",
                    KEY_TYPE_SCOPE);
        }
        if (details.containsKey(KEY_TYPE_FORMAT_SCOPE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_TYPE_FORMAT_SCOPE + "' on EClass '" +
                    eClass.getName() + "' is a runtime-only property (set via load/save options), ignored in EAnnotation",
                    KEY_TYPE_FORMAT_SCOPE);
        }
    }

    /**
     * Checks for type keys on EAttribute and adds ERROR diagnostics.
     * <p>
     * Per spec (06-type.md section 7, T-V5):
     * Type configuration is not applicable to attributes - only to EClass and EReference.
     * </p>
     */
    private void checkForTypeKeysOnAttribute(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EAttribute attribute) {
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("type") && isTypeConfigKey(key)) {
                addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                        "Annotation key '" + key + "' is not valid on EAttribute '" +
                        attribute.getName() + "', ignored (type configuration is not applicable to attributes)",
                        key);
            }
        }
    }

    /**
     * Checks for superType keys on EReference and adds ERROR diagnostics.
     * <p>
     * Per spec (16-annotation-reference.md, SuperType Misconfigurations, ST-V1):
     * SuperType is class-intrinsic, not reference-specific.
     * </p>
     */
    private void checkForSuperTypeKeysOnReference(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EReference reference) {
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("superType") && isSuperTypeConfigKey(key)) {
                addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                        "Annotation key '" + key + "' is not valid on EReference '" +
                        reference.getName() + "', ignored (superType configuration is class-intrinsic, not reference-specific)",
                        key);
            }
        }
    }

    /**
     * Checks for superType keys on EAttribute and adds ERROR diagnostics.
     * <p>
     * Per spec (16-annotation-reference.md, SuperType Misconfigurations, ST-V2):
     * SuperType is not applicable to attributes.
     * </p>
     */
    private void checkForSuperTypeKeysOnAttribute(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EAttribute attribute) {
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("superType") && isSuperTypeConfigKey(key)) {
                addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                        "Annotation key '" + key + "' is not valid on EAttribute '" +
                        attribute.getName() + "', ignored (superType configuration is not applicable to attributes)",
                        key);
            }
        }
    }

    /**
     * Checks for class-only ID keys on EReference and adds ERROR diagnostics.
     * <p>
     * Per spec (09-id.md section 11, ID-V1 through ID-V10):
     * Most id* keys are class-intrinsic. Only idFormat and idKey are valid on EReference.
     * </p>
     */
    private void checkForIdClassOnlyKeysOnReference(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EReference reference) {
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("id") && isIdClassOnlyKey(key)) {
                addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                        "Annotation key '" + key + "' is not valid on EReference '" +
                        reference.getName() + "', ignored (ID configuration is class-intrinsic)",
                        key);
            }
        }
    }

    /**
     * Checks for id* keys on EAttribute and adds ERROR diagnostics.
     * <p>
     * Per spec (09-id.md section 11, ID-V13):
     * ID configuration is not applicable to attributes.
     * </p>
     */
    private void checkForIdKeysOnAttribute(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EAttribute attribute) {
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("id") && isIdConfigKey(key)) {
                addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                        "Annotation key '" + key + "' is not valid on EAttribute '" +
                        attribute.getName() + "', ignored (ID configuration is not applicable to attributes)",
                        key);
            }
        }
    }

    /**
     * Checks for runtime-only ID keys in EAnnotation and adds WARNING diagnostics.
     * <p>
     * Per spec (09-id.md section 11):
     * </p>
     * <ul>
     *   <li>idScope (ID-V11): runtime-only → WARNING</li>
     *   <li>idFormatScope (ID-V12): runtime-only → WARNING</li>
     * </ul>
     */
    private void checkForIdRuntimeOnlyKeys(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EStructuralFeature feature) {
        if (details.containsKey(KEY_ID_SCOPE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_ID_SCOPE + "' on feature '" +
                    feature.getName() + "' is a runtime-only property (set via load/save options), ignored in EAnnotation",
                    KEY_ID_SCOPE);
        }
        if (details.containsKey(KEY_ID_FORMAT_SCOPE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_ID_FORMAT_SCOPE + "' on feature '" +
                    feature.getName() + "' is a runtime-only property (set via load/save options), ignored in EAnnotation",
                    KEY_ID_FORMAT_SCOPE);
        }
    }

    /**
     * Checks for runtime-only ID keys in class-level EAnnotation and adds WARNING diagnostics.
     * <p>
     * Per spec (09-id.md section 11):
     * </p>
     * <ul>
     *   <li>idScope (ID-V11): runtime-only → WARNING</li>
     *   <li>idFormatScope (ID-V12): runtime-only → WARNING</li>
     * </ul>
     */
    private void checkForIdRuntimeOnlyKeys(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EClass eClass) {
        if (details.containsKey(KEY_ID_SCOPE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_ID_SCOPE + "' on EClass '" +
                    eClass.getName() + "' is a runtime-only property (set via load/save options), ignored in EAnnotation",
                    KEY_ID_SCOPE);
        }
        if (details.containsKey(KEY_ID_FORMAT_SCOPE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_ID_FORMAT_SCOPE + "' on EClass '" +
                    eClass.getName() + "' is a runtime-only property (set via load/save options), ignored in EAnnotation",
                    KEY_ID_FORMAT_SCOPE);
        }
    }

    /**
     * Checks for reference-only keys on EClass and adds WARNING diagnostics.
     * <p>
     * Per spec (10-reference.md section 10):
     * </p>
     * <ul>
     *   <li>R-V1: ref* keys → WARNING (per-reference only)</li>
     *   <li>R-V3: expand → WARNING (per-reference only)</li>
     * </ul>
     * <p>
     * Note: inlineMapping.* and fallback* belong to Discriminator Mapping (08-discriminator-mapping.md),
     * NOT to Reference Configuration. Their validation is defined there.
     * </p>
     */
    private void checkForReferenceOnlyKeysOnClass(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EClass eClass) {
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (isRefConfigKey(key)) {
                addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                        "Annotation key '" + key + "' is not valid on EClass '" +
                        eClass.getName() + "', ignored (reference configuration is per-reference only)",
                        key);
            }
        }
        if (details.containsKey(KEY_EXPAND)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_EXPAND + "' is not valid on EClass '" +
                    eClass.getName() + "', ignored (expand is per-reference only)",
                    KEY_EXPAND);
        }
    }

    /**
     * Checks for reference-only keys on EAttribute and adds ERROR diagnostics.
     * <p>
     * Per spec (10-reference.md section 10):
     * </p>
     * <ul>
     *   <li>R-V2: ref* keys → ERROR (not applicable to attributes)</li>
     *   <li>R-V4: expand → ERROR (not applicable to attributes)</li>
     * </ul>
     * <p>
     * Note: inlineMapping.* and fallback* belong to Discriminator Mapping (08-discriminator-mapping.md),
     * NOT to Reference Configuration. Their validation is defined there.
     * </p>
     */
    private void checkForReferenceOnlyKeysOnAttribute(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EAttribute attribute) {
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            if (isRefConfigKey(key)) {
                addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                        "Annotation key '" + key + "' is not valid on EAttribute '" +
                        attribute.getName() + "', ignored (reference configuration is not applicable to attributes)",
                        key);
            }
        }
        if (details.containsKey(KEY_EXPAND)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                    "Annotation key '" + KEY_EXPAND + "' is not valid on EAttribute '" +
                    attribute.getName() + "', ignored (expand is not applicable to attributes)",
                    KEY_EXPAND);
        }
    }

    /**
     * Checks for strictness keys (strictOnUnknown, strictOnMissing) on feature-level annotations.
     * <p>
     * Strictness is a class-level property (Global + EClass only). Placing these keys on
     * EAttribute or EReference is a misconfiguration — the keys are ignored with a WARNING.
     * </p>
     */
    private void checkForStrictnessKeysOnFeature(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EStructuralFeature feature) {
        if (details.containsKey(KEY_STRICT_ON_UNKNOWN)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_STRICT_ON_UNKNOWN + "' is not valid on " +
                    (feature instanceof EReference ? "EReference" : "EAttribute") + " '" +
                    feature.getName() + "', ignored (class-level property only, use on EClass instead)",
                    KEY_STRICT_ON_UNKNOWN);
        }
        if (details.containsKey(KEY_STRICT_ON_MISSING)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                    "Annotation key '" + KEY_STRICT_ON_MISSING + "' is not valid on " +
                    (feature instanceof EReference ? "EReference" : "EAttribute") + " '" +
                    feature.getName() + "', ignored (class-level property only, use on EClass instead)",
                    KEY_STRICT_ON_MISSING);
        }
    }

    /**
     * Checks for metadata merge keys (metadataMerge, metadataKey) on feature-level annotations.
     * <p>
     * Metadata merge is a class-level property (Global + EClass only). Placing these keys on
     * EAttribute or EReference is a misconfiguration — the keys are ignored with an ERROR.
     * </p>
     */
    private void checkForMetadataMergeKeysOnFeature(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EStructuralFeature feature) {
        if (details.containsKey(KEY_METADATA_MERGE)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                    "Annotation key '" + KEY_METADATA_MERGE + "' is not valid on " +
                    (feature instanceof EReference ? "EReference" : "EAttribute") + " '" +
                    feature.getName() + "', ignored (class-level property only, use on EClass instead)",
                    KEY_METADATA_MERGE);
        }
        if (details.containsKey(KEY_METADATA_KEY)) {
            addDiagnostic(diagnostics, DiagnosticSeverity.ERROR,
                    "Annotation key '" + KEY_METADATA_KEY + "' is not valid on " +
                    (feature instanceof EReference ? "EReference" : "EAttribute") + " '" +
                    feature.getName() + "', ignored (class-level property only, use on EClass instead)",
                    KEY_METADATA_KEY);
        }
    }

    /**
     * Checks for deprecated typeInclude annotation and adds WARNING diagnostics.
     * <p>
     * Per spec (06-type.md section 7):
     * </p>
     * <ul>
     *   <li>T-V30: typeInclude present → WARNING (use typeStrategy=NONE instead)</li>
     *   <li>T-V31: Both typeInclude and typeStrategy set → WARNING (typeStrategy takes precedence)</li>
     * </ul>
     */
    private void checkForDeprecatedTypeInclude(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, EClass eClass) {
        if (details.containsKey(KEY_TYPE_INCLUDE)) {
            if (details.containsKey(KEY_TYPE_STRATEGY)) {
                // T-V31: Both present - typeStrategy takes precedence
                addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                        "Annotation key '" + KEY_TYPE_INCLUDE + "' on EClass '" + eClass.getName() +
                        "' is DEPRECATED and ignored because '" + KEY_TYPE_STRATEGY + "' is also set. " +
                        "Use typeStrategy=NONE instead of typeInclude=false",
                        KEY_TYPE_INCLUDE);
            } else {
                // T-V30: Only typeInclude present - deprecated
                addDiagnostic(diagnostics, DiagnosticSeverity.WARNING,
                        "Annotation key '" + KEY_TYPE_INCLUDE + "' on EClass '" + eClass.getName() +
                        "' is DEPRECATED. Use typeStrategy=NONE instead of typeInclude=false",
                        KEY_TYPE_INCLUDE);
            }
        }
    }

    /**
     * Checks if the given key is a type configuration key.
     * Used by T-V5 validation.
     */
    private boolean isTypeConfigKey(String key) {
        return KEY_TYPE_STRATEGY.equals(key)
                || KEY_TYPE_KEY.equals(key)
                || KEY_TYPE_MAP_ID.equals(key)
                || KEY_TYPE_DISCRIMINATOR_PATH.equals(key)
                || KEY_TYPE_DISCRIMINATOR.equals(key)
                || KEY_TYPE_VALUE_READER_NAME.equals(key)
                || KEY_TYPE_VALUE_WRITER_NAME.equals(key)
                || KEY_TYPE_FORMAT.equals(key)
                || KEY_TYPE_SCHEMA_KEY.equals(key)
                || KEY_TYPE_NAME_KEY.equals(key)
                || KEY_TYPE_SCOPE.equals(key)
                || KEY_TYPE_FORMAT_SCOPE.equals(key)
                || KEY_TYPE_INCLUDE.equals(key);
    }

    /**
     * Checks if the given key is a superType configuration key.
     * Used by ST-V1/ST-V2 validation.
     */
    private boolean isSuperTypeConfigKey(String key) {
        return KEY_SUPERTYPE_SERIALIZE.equals(key)
                || KEY_SUPERTYPE_KEY.equals(key)
                || KEY_SUPERTYPE_STRATEGY.equals(key)
                || KEY_SUPERTYPE_AS_ARRAY.equals(key)
                || KEY_SUPERTYPE_SEPARATOR.equals(key)
                || KEY_SUPERTYPE_FORMAT.equals(key);
    }

    /**
     * Checks if the given key is a reference configuration key (ref*).
     * Used by R-V1/R-V2 validation.
     * <p>
     * Note: Does NOT include expand, inlineMapping.*, or fallback* - those
     * have separate ownership (expand is standalone, the others belong to
     * Discriminator Mapping per 08-discriminator-mapping.md).
     * </p>
     */
    private boolean isRefConfigKey(String key) {
        return KEY_REF_FORMAT.equals(key)
                || KEY_REF_KEY.equals(key)
                || KEY_REF_TYPE_KEY.equals(key);
    }

    /**
     * Checks if the given key is any ID configuration key.
     * Used by ID-V13 validation (id* on EAttribute).
     */
    private boolean isIdConfigKey(String key) {
        return KEY_ID_STRATEGY.equals(key)
                || KEY_ID_KEY.equals(key)
                || KEY_ID_SEPARATOR.equals(key)
                || KEY_ID_FEATURES.equals(key)
                || KEY_ID_VALUE_READER_NAME.equals(key)
                || KEY_ID_VALUE_WRITER_NAME.equals(key)
                || KEY_ID_FORMAT.equals(key)
                || KEY_ID_KEY_MODE.equals(key)
                || KEY_ID_ON_TOP.equals(key)
                || KEY_ID_SERIALIZE_SEPARATOR.equals(key)
                || KEY_ID_SEPARATOR_KEY.equals(key)
                || KEY_ID_VALUE_KEY.equals(key)
                || KEY_ID_SCOPE.equals(key)
                || KEY_ID_FORMAT_SCOPE.equals(key);
    }

    /**
     * Checks if the given key is an ID class-only key (not valid on EReference).
     * <p>
     * Note: idFormat and idKey ARE valid on EReference (presentation can vary by context).
     * </p>
     * Used by ID-V1 through ID-V10 validation.
     */
    private boolean isIdClassOnlyKey(String key) {
        return KEY_ID_STRATEGY.equals(key)
                || KEY_ID_SEPARATOR.equals(key)
                || KEY_ID_FEATURES.equals(key)
                || KEY_ID_VALUE_READER_NAME.equals(key)
                || KEY_ID_VALUE_WRITER_NAME.equals(key)
                || KEY_ID_KEY_MODE.equals(key)
                || KEY_ID_ON_TOP.equals(key)
                || KEY_ID_SERIALIZE_SEPARATOR.equals(key)
                || KEY_ID_SEPARATOR_KEY.equals(key)
                || KEY_ID_VALUE_KEY.equals(key);
        // NOTE: KEY_ID_FORMAT and KEY_ID_KEY are NOT class-only - valid on EReference
    }

    /**
     * Adds a diagnostic to an aspect entry's diagnostics list (D2: diagnostics live on
     * the {@link AspectEntry}, not on the codec aspect content).
     */
    private static void addDiagnostic(EList<MetadataDiagnostic> diagnostics, DiagnosticSeverity severity,
            String message, String key) {
        MetadataDiagnostic diagnostic = MetadataFactory.eINSTANCE.createMetadataDiagnostic();
        diagnostic.setSeverity(severity);
        diagnostic.setMessage(message);
        diagnostic.setKey(key);
        diagnostics.add(diagnostic);
    }

}
