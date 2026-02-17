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
package org.eclipse.fennec.model.metadata.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.metadata.AttributeMetadata;
import org.eclipse.fennec.model.metadata.ClassAspect;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.ClassProfile;
import org.eclipse.fennec.model.metadata.FeatureAspect;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.MetadataFactory;
import org.eclipse.fennec.model.metadata.MetadataRegistry;
import org.eclipse.fennec.model.metadata.PackageAspect;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.PackageProfile;
import org.eclipse.fennec.model.metadata.ReferenceMetadata;
import org.eclipse.fennec.model.metadata.api.AspectProvider;
import org.eclipse.fennec.model.metadata.api.MetadataHandler;
import org.eclipse.fennec.model.metadata.api.MetadataIndex;
import org.eclipse.fennec.model.metadata.api.MetadataIndexReader;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;

/**
 * Default implementation of the {@link MetadataWhiteboard}.
 * <p>
 * This service maintains a registry of pre-computed metadata for registered EPackages.
 * When an EPackage is registered, metadata is built for all its classes and features,
 * all registered AspectProviders are called to contribute their aspects, and then
 * each provider's buildProfiles is called with a filtered metadata copy.
 * </p>
 * <p>
 * Uses a {@link MetadataIndex} internally for fast indexed lookups. The index is
 * automatically updated when packages are registered or unregistered. The index
 * can be swapped at runtime via setMetadataIndex/unsetMetadataIndex (OSGi DS).
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2025-12-09
 */
public class MetadataServiceImpl implements MetadataWhiteboard {

    private final MetadataRegistry registry;
    private final List<AspectProvider> aspectProviders = new CopyOnWriteArrayList<>();
    private final List<MetadataHandler> metadataHandlers = new CopyOnWriteArrayList<>();
    private volatile MetadataIndex index;

    // Fast lookup maps for EClass/EStructuralFeature -> Metadata (not indexed by string)
    private final Map<String, PackageMetadata> packagesByNsURI = new ConcurrentHashMap<>();
    private final Map<EClass, ClassMetadata> classesByEClass = new ConcurrentHashMap<>();
    private final Map<EStructuralFeature, FeatureMetadata> featuresByEFeature = new ConcurrentHashMap<>();

    /**
     * Creates a new MetadataServiceImpl with an empty registry and default Map-based index.
     */
    public MetadataServiceImpl() {
        this(new MapBasedMetadataIndex());
    }

    /**
     * Creates a new MetadataServiceImpl with an empty registry and the specified index.
     *
     * @param index the metadata index to use
     */
    public MetadataServiceImpl(MetadataIndex index) {
        this.registry = MetadataFactory.eINSTANCE.createMetadataRegistry();
        this.index = index;
    }

    /**
     * Creates a new MetadataServiceImpl with the given registry and default Map-based index.
     * Use this constructor to load a pre-computed registry.
     *
     * @param registry the pre-computed registry
     */
    public MetadataServiceImpl(MetadataRegistry registry) {
        this(registry, new MapBasedMetadataIndex());
    }

    /**
     * Creates a new MetadataServiceImpl with the given registry and index.
     * Use this constructor to load a pre-computed registry with a custom index.
     *
     * @param registry the pre-computed registry
     * @param index the metadata index to use
     */
    public MetadataServiceImpl(MetadataRegistry registry, MetadataIndex index) {
        this.registry = registry;
        this.index = index;
        // Rebuild lookup maps and index from registry
        rebuildLookupMaps();
    }

    // ========================================================================
    // MetadataService (consumer read-only) methods
    // ========================================================================

    @Override
    public MetadataIndexReader getIndexReader() {
        return index;
    }

    @Override
    public PackageMetadata getPackageMetadata(String nsURI) {
        return packagesByNsURI.get(nsURI);
    }

    @Override
    public ClassMetadata getClassMetadata(EClass eClass) {
        if (eClass == null) {
            return null;
        }
        return classesByEClass.get(eClass);
    }

    @Override
    public ClassMetadata getClassMetadataByURI(String uri) {
        MetadataIndex idx = this.index;
        return idx != null ? idx.findClassByURI(uri) : null;
    }

    @Override
    public ClassMetadata getClassMetadataByName(String className, String nsURI) {
        MetadataIndex idx = this.index;
        return idx != null ? idx.findByClassName(nsURI, className) : null;
    }

    @Override
    public FeatureMetadata getFeatureMetadata(EStructuralFeature feature) {
        if (feature == null) {
            return null;
        }
        return featuresByEFeature.get(feature);
    }

    @Override
    public FeatureMetadata getFeatureMetadataByURI(String uri) {
        MetadataIndex idx = this.index;
        return idx != null ? idx.findFeatureByURI(uri) : null;
    }

    @Override
    public FeatureMetadata getFeatureMetadataByName(String featureName, String className, String nsURI) {
        ClassMetadata classMetadata = getClassMetadataByName(className, nsURI);
        if (classMetadata != null) {
            return getFeatureMetadataFromClass(featureName, classMetadata);
        }
        return null;
    }

    @Override
    public FeatureMetadata getFeatureMetadataFromClass(String featureName, ClassMetadata classMetadata) {
        if (classMetadata == null || featureName == null) {
            return null;
        }
        for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
            if (featureName.equals(featureMetadata.getName())) {
                return featureMetadata;
            }
        }
        return null;
    }

    @Override
    public PackageAspect getPackageAspect(EPackage ePackage, String aspectTypeId) {
        if (ePackage == null) {
            return null;
        }
        PackageMetadata pkgMetadata = packagesByNsURI.get(ePackage.getNsURI());
        if (pkgMetadata != null) {
            for (PackageAspect aspect : pkgMetadata.getAspects()) {
                if (aspectTypeId.equals(aspect.getTypeId())) {
                    return aspect;
                }
            }
        }
        return null;
    }

    @Override
    public ClassAspect getClassAspect(EClass eClass, String aspectTypeId) {
        ClassMetadata classMetadata = getClassMetadata(eClass);
        if (classMetadata != null) {
            for (ClassAspect aspect : classMetadata.getAspects()) {
                if (aspectTypeId.equals(aspect.getTypeId())) {
                    return aspect;
                }
            }
        }
        return null;
    }

    @Override
    public FeatureAspect getFeatureAspect(EStructuralFeature feature, String aspectTypeId) {
        FeatureMetadata featureMetadata = getFeatureMetadata(feature);
        if (featureMetadata != null) {
            for (FeatureAspect aspect : featureMetadata.getAspects()) {
                if (aspectTypeId.equals(aspect.getTypeId())) {
                    return aspect;
                }
            }
        }
        return null;
    }

    @Override
    public PackageProfile getPackageProfile(EPackage ePackage, String typeId) {
        if (ePackage == null || typeId == null) {
            return null;
        }
        PackageMetadata pkgMetadata = packagesByNsURI.get(ePackage.getNsURI());
        return findPackageProfile(pkgMetadata, typeId);
    }

    @Override
    public PackageProfile getPackageProfileByNsURI(String nsURI, String typeId) {
        if (nsURI == null || typeId == null) {
            return null;
        }
        PackageMetadata pkgMetadata = packagesByNsURI.get(nsURI);
        return findPackageProfile(pkgMetadata, typeId);
    }

    @Override
    public ClassProfile getClassProfile(EClass eClass, String typeId) {
        if (eClass == null || typeId == null) {
            return null;
        }
        ClassMetadata classMetadata = classesByEClass.get(eClass);
        if (classMetadata == null) {
            return null;
        }
        PackageMetadata pkgMetadata = classMetadata.getPackage();
        PackageProfile pkgProfile = findPackageProfile(pkgMetadata, typeId);
        if (pkgProfile != null) {
            for (ClassProfile cp : pkgProfile.getClassProfiles()) {
                if (eClass.equals(cp.getEClass())) {
                    return cp;
                }
            }
        }
        return null;
    }

    @Override
    public ClassProfile getClassProfileByURI(String eClassURI, String typeId) {
        if (eClassURI == null || typeId == null) {
            return null;
        }
        MetadataIndex idx = this.index;
        ClassMetadata classMetadata = idx != null ? idx.findClassByURI(eClassURI) : null;
        if (classMetadata == null) {
            return null;
        }
        PackageMetadata pkgMetadata = classMetadata.getPackage();
        PackageProfile pkgProfile = findPackageProfile(pkgMetadata, typeId);
        if (pkgProfile != null) {
            EClass eClass = classMetadata.getEClass();
            for (ClassProfile cp : pkgProfile.getClassProfiles()) {
                if (eClass.equals(cp.getEClass())) {
                    return cp;
                }
            }
        }
        return null;
    }

    @Override
    public MetadataRegistry getRegistry() {
        return registry;
    }

    // ========================================================================
    // MetadataWhiteboard (admin/lifecycle) methods
    // ========================================================================

    @Override
    public PackageMetadata registerPackage(EPackage ePackage) {
        if (ePackage == null) {
            return null;
        }

        String nsURI = ePackage.getNsURI();

        // Check if already registered
        PackageMetadata existing = packagesByNsURI.get(nsURI);
        if (existing != null) {
            return existing;
        }

        // Create package metadata
        PackageMetadata pkgMetadata = MetadataFactory.eINSTANCE.createPackageMetadata();
        pkgMetadata.setEPackage(ePackage);
        pkgMetadata.setNsURI(nsURI);

        // Apply all aspect providers to package
        for (AspectProvider provider : aspectProviders) {
            PackageAspect aspect = provider.buildPackageAspect(pkgMetadata);
            if (aspect != null) {
                aspect.setTypeId(provider.getAspectTypeId());
                pkgMetadata.getAspects().add(aspect);
            }
        }

        // Process all EClasses
        for (EClassifier classifier : ePackage.getEClassifiers()) {
            if (classifier instanceof EClass eClass) {
                ClassMetadata classMetadata = buildClassMetadata(eClass, pkgMetadata);
                pkgMetadata.getClasses().add(classMetadata);
            }
        }

        // Resolve cross-references (supertypes, target classes)
        resolveReferences(pkgMetadata);

        // Build profiles for each provider
        buildProfilesForAllProviders(pkgMetadata);

        // Add to registry and lookup maps
        registry.getPackages().add(pkgMetadata);
        packagesByNsURI.put(nsURI, pkgMetadata);

        // Index the package
        MetadataIndex idx = this.index;
        if (idx != null) {
            idx.indexPackage(pkgMetadata);
        }

        // Notify metadata handlers
        for (MetadataHandler handler : metadataHandlers) {
            handler.onPackageRegistered(pkgMetadata);
        }

        return pkgMetadata;
    }

    @Override
    public void unregisterPackage(EPackage ePackage) {
        if (ePackage == null) {
            return;
        }

        String nsURI = ePackage.getNsURI();
        PackageMetadata pkgMetadata = packagesByNsURI.get(nsURI);

        if (pkgMetadata != null) {
            // Notify metadata handlers before removing
            for (MetadataHandler handler : metadataHandlers) {
                handler.onPackageUnregistered(pkgMetadata);
            }

            packagesByNsURI.remove(nsURI);

            // Remove from index first
            MetadataIndex idx = this.index;
            if (idx != null) {
                idx.removePackage(pkgMetadata);
            }

            // Remove from lookup maps
            for (ClassMetadata classMetadata : pkgMetadata.getClasses()) {
                EClass eClass = classMetadata.getEClass();
                if (eClass != null) {
                    classesByEClass.remove(eClass);
                }

                for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
                    EStructuralFeature feature = featureMetadata.getEFeature();
                    if (feature != null) {
                        featuresByEFeature.remove(feature);
                    }
                }
            }

            // Remove from registry
            registry.getPackages().remove(pkgMetadata);
        }
    }

    @Override
    public void registerAspectProvider(AspectProvider provider) {
        if (provider != null && !aspectProviders.contains(provider)) {
            aspectProviders.add(provider);

            // Apply provider to all existing metadata and build profiles
            for (PackageMetadata pkgMetadata : registry.getPackages()) {
                applyProviderToPackage(provider, pkgMetadata);
                buildProfilesForProvider(provider, pkgMetadata);
            }
        }
    }

    @Override
    public void unregisterAspectProvider(AspectProvider provider) {
        if (provider != null) {
            aspectProviders.remove(provider);

            // Remove aspects and profiles from this provider
            String typeId = provider.getAspectTypeId();
            for (PackageMetadata pkgMetadata : registry.getPackages()) {
                removeAspectsFromPackage(typeId, pkgMetadata);
                removeProfileFromPackage(typeId, pkgMetadata);
            }
        }
    }

    @Override
    public EList<AspectProvider> getAspectProviders() {
        return new BasicEList<>(aspectProviders);
    }

    @Override
    public MetadataIndex getMetadataIndex() {
        return index;
    }

    @Override
    public void setMetadataIndex(MetadataIndex index) {
        this.index = index;
        if (index != null) {
            // Populate the new index with all existing metadata
            for (PackageMetadata pkgMetadata : registry.getPackages()) {
                index.indexPackage(pkgMetadata);
            }
        }
    }

    @Override
    public void unsetMetadataIndex(MetadataIndex index) {
        if (index != null && index == this.index) {
            index.clear();
            this.index = null;
        }
    }

    @Override
    public void addMetadataHandler(MetadataHandler handler) {
        if (handler != null && !metadataHandlers.contains(handler)) {
            metadataHandlers.add(handler);

            // Late binding: notify handler about all existing packages
            for (PackageMetadata pkgMetadata : registry.getPackages()) {
                handler.onPackageRegistered(pkgMetadata);
            }
        }
    }

    @Override
    public void removeMetadataHandler(MetadataHandler handler) {
        if (handler != null) {
            metadataHandlers.remove(handler);
            handler.clear();
        }
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    private ClassMetadata buildClassMetadata(EClass eClass, PackageMetadata pkgMetadata) {
        ClassMetadata classMetadata = MetadataFactory.eINSTANCE.createClassMetadata();
        classMetadata.setEClass(eClass);
        classMetadata.setName(eClass.getName());
        classMetadata.setClassifierID(eClass.getClassifierID());
        classMetadata.setTypeURI(EcoreUtil.getURI(eClass).toString());

        // Determine if class has ID
        EAttribute idAttribute = eClass.getEIDAttribute();
        classMetadata.setHasId(idAttribute != null);

        // Process features first (before class aspects — providers may need feature metadata)
        for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
            FeatureMetadata featureMetadata = buildFeatureMetadata(feature, classMetadata);
            classMetadata.getFeatures().add(featureMetadata);

            // Track ID features
            if (feature instanceof EAttribute attr && attr.isID()) {
                classMetadata.getIdFeatures().add(featureMetadata);
            }
        }

        // Add to EClass lookup map
        classesByEClass.put(eClass, classMetadata);

        // Apply all aspect providers (after features are built)
        for (AspectProvider provider : aspectProviders) {
            ClassAspect aspect = provider.buildClassAspect(classMetadata);
            if (aspect != null) {
                aspect.setTypeId(provider.getAspectTypeId());
                classMetadata.getAspects().add(aspect);
            }
        }

        return classMetadata;
    }

    private FeatureMetadata buildFeatureMetadata(EStructuralFeature feature, ClassMetadata classMetadata) {
        FeatureMetadata featureMetadata;

        if (feature instanceof EAttribute attr) {
            AttributeMetadata attrMetadata = MetadataFactory.eINSTANCE.createAttributeMetadata();
            attrMetadata.setEAttribute(attr);
            attrMetadata.setIsId(attr.isID());
            attrMetadata.setDefaultValue(attr.getDefaultValue());
            featureMetadata = attrMetadata;
        } else if (feature instanceof EReference ref) {
            ReferenceMetadata refMetadata = MetadataFactory.eINSTANCE.createReferenceMetadata();
            refMetadata.setEReference(ref);
            refMetadata.setContainment(ref.isContainment());
            refMetadata.setHasBidirectional(ref.getEOpposite() != null);
            featureMetadata = refMetadata;
        } else {
            // Should not happen, but handle gracefully
            return null;
        }

        // Common properties
        featureMetadata.setEFeature(feature);
        featureMetadata.setName(feature.getName());
        featureMetadata.setFeatureID(feature.getFeatureID());
        featureMetadata.setExtendedMetaDataName(getExtendedMetaDataName(feature));

        // Add to EFeature lookup map
        featuresByEFeature.put(feature, featureMetadata);

        // Apply all aspect providers
        for (AspectProvider provider : aspectProviders) {
            FeatureAspect aspect;
            if (featureMetadata instanceof AttributeMetadata attrMd) {
                aspect = provider.buildAttributeAspect(attrMd);
            } else if (featureMetadata instanceof ReferenceMetadata refMd) {
                aspect = provider.buildReferenceAspect(refMd);
            } else {
                aspect = provider.buildFeatureAspect(featureMetadata);
            }

            if (aspect != null) {
                aspect.setTypeId(provider.getAspectTypeId());
                featureMetadata.getAspects().add(aspect);
            }
        }

        return featureMetadata;
    }

    private void resolveReferences(PackageMetadata pkgMetadata) {
        for (ClassMetadata classMetadata : pkgMetadata.getClasses()) {
            EClass eClass = classMetadata.getEClass();

            // Resolve supertypes
            for (EClass superType : eClass.getESuperTypes()) {
                ClassMetadata superMetadata = classesByEClass.get(superType);
                if (superMetadata != null) {
                    classMetadata.getSuperTypes().add(superMetadata);
                }
            }

            // Resolve all supertypes (transitive)
            for (EClass superType : eClass.getEAllSuperTypes()) {
                ClassMetadata superMetadata = classesByEClass.get(superType);
                if (superMetadata != null) {
                    classMetadata.getAllSuperTypes().add(superMetadata);
                }
            }

            // Resolve reference targets
            for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
                if (featureMetadata instanceof ReferenceMetadata refMetadata) {
                    EReference ref = refMetadata.getEReference();

                    // Target class
                    EClass targetClass = ref.getEReferenceType();
                    ClassMetadata targetMetadata = classesByEClass.get(targetClass);
                    refMetadata.setTargetClassMetadata(targetMetadata);

                    // Opposite reference
                    EReference opposite = ref.getEOpposite();
                    if (opposite != null) {
                        FeatureMetadata oppositeMetadata = featuresByEFeature.get(opposite);
                        if (oppositeMetadata instanceof ReferenceMetadata oppRefMetadata) {
                            refMetadata.setOppositeMetadata(oppRefMetadata);
                        }
                    }
                }
            }
        }
    }

    private void buildProfilesForAllProviders(PackageMetadata pkgMetadata) {
        for (AspectProvider provider : aspectProviders) {
            buildProfilesForProvider(provider, pkgMetadata);
        }
    }

    private void buildProfilesForProvider(AspectProvider provider, PackageMetadata pkgMetadata) {
        String typeId = provider.getAspectTypeId();
        PackageMetadata filteredCopy;
        try {
            // Create filtered copy with only this provider's aspects
            filteredCopy = EcoreUtil.copy(pkgMetadata);
            filterAspectsByTypeId(filteredCopy, typeId);
        } catch (IllegalArgumentException e) {
            // EcoreUtil.copy fails if aspect classes are abstract (e.g., in tests with
            // non-EMF-registered aspect subclasses). In production, concrete EMF aspect
            // classes (e.g., ClassCodecAspect) will work fine.
            return;
        }

        PackageProfile profile = provider.buildProfiles(filteredCopy);
        if (profile != null) {
            profile.setTypeId(typeId);
            pkgMetadata.getProfiles().add(profile);
        }
    }

    /**
     * Removes all aspects from the copied metadata tree whose typeId does NOT match
     * the given typeId. This ensures the provider sees only its own aspects.
     */
    private void filterAspectsByTypeId(PackageMetadata copy, String typeId) {
        copy.getAspects().removeIf(a -> !typeId.equals(a.getTypeId()));
        for (ClassMetadata classMetadata : copy.getClasses()) {
            classMetadata.getAspects().removeIf(a -> !typeId.equals(a.getTypeId()));
            for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
                featureMetadata.getAspects().removeIf(a -> !typeId.equals(a.getTypeId()));
            }
        }
    }

    private void applyProviderToPackage(AspectProvider provider, PackageMetadata pkgMetadata) {
        // Build package aspect
        PackageAspect pkgAspect = provider.buildPackageAspect(pkgMetadata);
        if (pkgAspect != null) {
            pkgAspect.setTypeId(provider.getAspectTypeId());
            pkgMetadata.getAspects().add(pkgAspect);
        }

        for (ClassMetadata classMetadata : pkgMetadata.getClasses()) {
            // Build feature aspects first (before class aspects)
            for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
                FeatureAspect featureAspect;

                if (featureMetadata instanceof AttributeMetadata attrMd) {
                    featureAspect = provider.buildAttributeAspect(attrMd);
                } else if (featureMetadata instanceof ReferenceMetadata refMd) {
                    featureAspect = provider.buildReferenceAspect(refMd);
                } else {
                    featureAspect = provider.buildFeatureAspect(featureMetadata);
                }

                if (featureAspect != null) {
                    featureAspect.setTypeId(provider.getAspectTypeId());
                    featureMetadata.getAspects().add(featureAspect);
                }
            }

            // Build class aspect (after features)
            ClassAspect classAspect = provider.buildClassAspect(classMetadata);
            if (classAspect != null) {
                classAspect.setTypeId(provider.getAspectTypeId());
                classMetadata.getAspects().add(classAspect);
            }
        }
    }

    private void removeAspectsFromPackage(String typeId, PackageMetadata pkgMetadata) {
        // Remove package aspects
        pkgMetadata.getAspects().removeIf(a -> typeId.equals(a.getTypeId()));

        for (ClassMetadata classMetadata : pkgMetadata.getClasses()) {
            classMetadata.getAspects().removeIf(a -> typeId.equals(a.getTypeId()));

            for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
                featureMetadata.getAspects().removeIf(a -> typeId.equals(a.getTypeId()));
            }
        }
    }

    private void removeProfileFromPackage(String typeId, PackageMetadata pkgMetadata) {
        pkgMetadata.getProfiles().removeIf(p -> typeId.equals(p.getTypeId()));
    }

    private PackageProfile findPackageProfile(PackageMetadata pkgMetadata, String typeId) {
        if (pkgMetadata == null) {
            return null;
        }
        for (PackageProfile profile : pkgMetadata.getProfiles()) {
            if (typeId.equals(profile.getTypeId())) {
                return profile;
            }
        }
        return null;
    }

    private void rebuildLookupMaps() {
        packagesByNsURI.clear();
        classesByEClass.clear();
        featuresByEFeature.clear();
        MetadataIndex idx = this.index;
        if (idx != null) {
            idx.clear();
        }

        for (PackageMetadata pkgMetadata : registry.getPackages()) {
            packagesByNsURI.put(pkgMetadata.getNsURI(), pkgMetadata);

            for (ClassMetadata classMetadata : pkgMetadata.getClasses()) {
                EClass eClass = classMetadata.getEClass();
                if (eClass != null) {
                    classesByEClass.put(eClass, classMetadata);
                }

                for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
                    EStructuralFeature feature = featureMetadata.getEFeature();
                    if (feature != null) {
                        featuresByEFeature.put(feature, featureMetadata);
                    }
                }
            }

            // Index the package
            if (idx != null) {
                idx.indexPackage(pkgMetadata);
            }
        }
    }

    /**
     * Extracts the ExtendedMetaData name from a feature's annotation.
     * <p>
     * ExtendedMetaData annotations are used by XSD-generated models where
     * the XML element/attribute name differs from the Java-friendly EMF feature name.
     * </p>
     *
     * @param feature the structural feature
     * @return the ExtendedMetaData name, or null if not present
     */
    private String getExtendedMetaDataName(EStructuralFeature feature) {
        EAnnotation annotation = feature.getEAnnotation(
                "http:///org/eclipse/emf/ecore/util/ExtendedMetaData");
        if (annotation != null) {
            String name = annotation.getDetails().get("name");
            if (name != null && !name.isEmpty()) {
                return name;
            }
        }
        return null;
    }
}
