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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataIndex;

/**
 * In-memory Map-based implementation of {@link MetadataIndex}.
 * <p>
 * Uses ConcurrentHashMaps for thread-safe indexed lookups. Maintains multiple
 * indexes for different query patterns:
 * <ul>
 *   <li>By URI (for URI TypeStrategy)</li>
 *   <li>By class name + nsURI (for NAME TypeStrategy)</li>
 *   <li>By instanceClassName + nsURI (for CLASS TypeStrategy)</li>
 *   <li>By instanceClassName globally (for CLASS TypeStrategy without context)</li>
 *   <li>By class name globally</li>
 * </ul>
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2026-01-26
 */
public class MapBasedMetadataIndex implements MetadataIndex {

    // Primary indexes
    private final Map<String, ClassMetadata> classesByURI = new ConcurrentHashMap<>();
    private final Map<String, FeatureMetadata> featuresByURI = new ConcurrentHashMap<>();

    // Composite key indexes: "nsURI::name" -> ClassMetadata
    private final Map<String, ClassMetadata> classesByNsURIAndName = new ConcurrentHashMap<>();
    private final Map<String, ClassMetadata> classesByNsURIAndInstanceClassName = new ConcurrentHashMap<>();

    // Global indexes for cross-package queries: name -> List<ClassMetadata>
    private final Map<String, List<ClassMetadata>> classesByName = new ConcurrentHashMap<>();
    private final Map<String, List<ClassMetadata>> classesByInstanceClassName = new ConcurrentHashMap<>();

    // ========================================================================
    // MetadataIndexWriter - Index operations
    // ========================================================================

    @Override
    public void indexPackage(PackageMetadata packageMetadata) {
        if (packageMetadata == null) {
            return;
        }
        for (ClassMetadata classMetadata : packageMetadata.getClasses()) {
            indexClass(classMetadata);
        }
    }

    @Override
    public void indexClass(ClassMetadata classMetadata) {
        if (classMetadata == null) {
            return;
        }

        String typeURI = classMetadata.getTypeURI();
        String name = classMetadata.getName();
        EClass eClass = classMetadata.getEClass();
        String nsURI = eClass != null && eClass.getEPackage() != null
            ? eClass.getEPackage().getNsURI()
            : null;
        String instanceClassName = eClass != null ? eClass.getInstanceClassName() : null;

        // Index by URI
        if (typeURI != null) {
            classesByURI.put(typeURI, classMetadata);
        }

        // Index by nsURI + name
        if (nsURI != null && name != null) {
            classesByNsURIAndName.put(compositeKey(nsURI, name), classMetadata);
        }

        // Index by nsURI + instanceClassName
        if (nsURI != null && instanceClassName != null) {
            classesByNsURIAndInstanceClassName.put(compositeKey(nsURI, instanceClassName), classMetadata);
        }

        // Index globally by name
        if (name != null) {
            classesByName.computeIfAbsent(name, k -> new ArrayList<>()).add(classMetadata);
        }

        // Index globally by instanceClassName
        if (instanceClassName != null) {
            classesByInstanceClassName.computeIfAbsent(instanceClassName, k -> new ArrayList<>()).add(classMetadata);
        }

        // Index all features
        for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
            indexFeature(featureMetadata);
        }
    }

    @Override
    public void indexFeature(FeatureMetadata featureMetadata) {
        if (featureMetadata == null) {
            return;
        }
        EStructuralFeature eFeature = featureMetadata.getEFeature();
        if (eFeature != null) {
            String uri = org.eclipse.emf.ecore.util.EcoreUtil.getURI(eFeature).toString();
            featuresByURI.put(uri, featureMetadata);
        }
    }

    // ========================================================================
    // MetadataIndexWriter - Remove operations
    // ========================================================================

    @Override
    public void removePackage(PackageMetadata packageMetadata) {
        if (packageMetadata == null) {
            return;
        }
        for (ClassMetadata classMetadata : packageMetadata.getClasses()) {
            removeClass(classMetadata);
        }
    }

    @Override
    public void removeClass(ClassMetadata classMetadata) {
        if (classMetadata == null) {
            return;
        }

        String typeURI = classMetadata.getTypeURI();
        String name = classMetadata.getName();
        EClass eClass = classMetadata.getEClass();
        String nsURI = eClass != null && eClass.getEPackage() != null
            ? eClass.getEPackage().getNsURI()
            : null;
        String instanceClassName = eClass != null ? eClass.getInstanceClassName() : null;

        // Remove from URI index
        if (typeURI != null) {
            classesByURI.remove(typeURI);
        }

        // Remove from composite key indexes
        if (nsURI != null && name != null) {
            classesByNsURIAndName.remove(compositeKey(nsURI, name));
        }
        if (nsURI != null && instanceClassName != null) {
            classesByNsURIAndInstanceClassName.remove(compositeKey(nsURI, instanceClassName));
        }

        // Remove from global indexes
        if (name != null) {
            List<ClassMetadata> list = classesByName.get(name);
            if (list != null) {
                list.remove(classMetadata);
                if (list.isEmpty()) {
                    classesByName.remove(name);
                }
            }
        }
        if (instanceClassName != null) {
            List<ClassMetadata> list = classesByInstanceClassName.get(instanceClassName);
            if (list != null) {
                list.remove(classMetadata);
                if (list.isEmpty()) {
                    classesByInstanceClassName.remove(instanceClassName);
                }
            }
        }

        // Remove all features
        for (FeatureMetadata featureMetadata : classMetadata.getFeatures()) {
            removeFeature(featureMetadata);
        }
    }

    @Override
    public void removeFeature(FeatureMetadata featureMetadata) {
        if (featureMetadata == null) {
            return;
        }
        EStructuralFeature eFeature = featureMetadata.getEFeature();
        if (eFeature != null) {
            String uri = org.eclipse.emf.ecore.util.EcoreUtil.getURI(eFeature).toString();
            featuresByURI.remove(uri);
        }
    }

    @Override
    public void clear() {
        classesByURI.clear();
        featuresByURI.clear();
        classesByNsURIAndName.clear();
        classesByNsURIAndInstanceClassName.clear();
        classesByName.clear();
        classesByInstanceClassName.clear();
    }

    // ========================================================================
    // MetadataIndexReader - Query operations
    // ========================================================================

    @Override
    public ClassMetadata findByInstanceClassName(String nsURI, String instanceClassName) {
        if (nsURI == null || instanceClassName == null) {
            return null;
        }
        return classesByNsURIAndInstanceClassName.get(compositeKey(nsURI, instanceClassName));
    }

    @Override
    public EList<ClassMetadata> findAllByInstanceClassName(String instanceClassName) {
        if (instanceClassName == null) {
            return new BasicEList<>();
        }
        List<ClassMetadata> results = classesByInstanceClassName.get(instanceClassName);
        return results != null ? new BasicEList<>(results) : new BasicEList<>();
    }

    @Override
    public ClassMetadata findByClassName(String nsURI, String className) {
        if (nsURI == null || className == null) {
            return null;
        }
        return classesByNsURIAndName.get(compositeKey(nsURI, className));
    }

    @Override
    public EList<ClassMetadata> findAllByClassName(String className) {
        if (className == null) {
            return new BasicEList<>();
        }
        List<ClassMetadata> results = classesByName.get(className);
        return results != null ? new BasicEList<>(results) : new BasicEList<>();
    }

    @Override
    public ClassMetadata findClassByURI(String uri) {
        if (uri == null) {
            return null;
        }
        return classesByURI.get(uri);
    }

    @Override
    public FeatureMetadata findFeatureByURI(String uri) {
        if (uri == null) {
            return null;
        }
        return featuresByURI.get(uri);
    }

    @Override
    public EList<ClassMetadata> findClassesByAnnotation(String annotationSource, String key, String value) {
        if (annotationSource == null || key == null) {
            return new BasicEList<>();
        }
        List<ClassMetadata> results = new ArrayList<>();
        for (ClassMetadata classMetadata : classesByURI.values()) {
            EClass eClass = classMetadata.getEClass();
            if (eClass != null && hasAnnotation(eClass.getEAnnotation(annotationSource), key, value)) {
                results.add(classMetadata);
            }
        }
        return new BasicEList<>(results);
    }

    @Override
    public EList<FeatureMetadata> findFeaturesByAnnotation(String annotationSource, String key, String value) {
        if (annotationSource == null || key == null) {
            return new BasicEList<>();
        }
        List<FeatureMetadata> results = new ArrayList<>();
        for (FeatureMetadata featureMetadata : featuresByURI.values()) {
            EStructuralFeature eFeature = featureMetadata.getEFeature();
            if (eFeature != null && hasAnnotation(eFeature.getEAnnotation(annotationSource), key, value)) {
                results.add(featureMetadata);
            }
        }
        return new BasicEList<>(results);
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================

    /**
     * Creates a composite key from nsURI and name/className.
     */
    private String compositeKey(String nsURI, String name) {
        return nsURI + "::" + name;
    }

    /**
     * Checks if an annotation has the specified key and optionally value.
     *
     * @param annotation the annotation to check (may be null)
     * @param key the required key
     * @param value the required value, or null to match any value
     * @return true if the annotation has the key (and value if specified)
     */
    private boolean hasAnnotation(EAnnotation annotation, String key, String value) {
        if (annotation == null) {
            return false;
        }
        String actualValue = annotation.getDetails().get(key);
        if (actualValue == null) {
            return false;
        }
        // If value is null, match any value
        return value == null || value.equals(actualValue);
    }
}
