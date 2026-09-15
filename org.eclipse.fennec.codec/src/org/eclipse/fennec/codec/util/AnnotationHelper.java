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
package org.eclipse.fennec.codec.util;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.constants.AnnotationSources;

/**
 * Helper class for extracting metadata from EMF EAnnotations.
 * <p>
 * Provides utility methods for common annotation lookups, including
 * ExtendedMetaData annotations used for XSD-generated models.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public final class AnnotationHelper {

    /**
     * The annotation source for EMF ExtendedMetaData.
     * Used by XSD-to-Ecore generated models.
     */
    public static final String EXTENDED_METADATA_SOURCE = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";

    private AnnotationHelper() {
        // Utility class, not instantiable
    }

    /**
     * Gets the ExtendedMetaData name for a structural feature.
     * <p>
     * This is used when EMF models are generated from XSD and the original
     * XML element/attribute name differs from the Java-friendly EMF feature name.
     * </p>
     *
     * @param feature the structural feature
     * @return the ExtendedMetaData name, or null if not present
     */
    public static String getExtendedMetaDataName(EStructuralFeature feature) {
        return getAnnotationDetail(feature, EXTENDED_METADATA_SOURCE, "name");
    }

    /**
     * Gets the ExtendedMetaData name for an EClass.
     *
     * @param eClass the EClass
     * @return the ExtendedMetaData name, or null if not present
     */
    public static String getExtendedMetaDataName(EClass eClass) {
        return getAnnotationDetail(eClass, EXTENDED_METADATA_SOURCE, "name");
    }

    /**
     * Gets a detail value from an EAnnotation.
     *
     * @param element the model element to get the annotation from
     * @param source the annotation source URI
     * @param key the detail key
     * @return the detail value, or null if the annotation or key is not present
     */
    public static String getAnnotationDetail(EModelElement element, String source, String key) {
        if (element == null || source == null || key == null) {
            return null;
        }
        EAnnotation annotation = element.getEAnnotation(source);
        if (annotation != null) {
            String value = annotation.getDetails().get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }

    /**
     * Tells whether an EClass declares a feature under the given JSON key, and so owns that
     * key wherever the codec would otherwise reserve it (issue #217).
     * <p>
     * A declared feature beats a reserved key - the rule the reference key already follows for
     * OpenAPI's {@code $ref}. Both sides of the codec have to answer this question the same
     * way: the reader, to hand the value to the model rather than to the fingerprint carrier,
     * and the writer, to leave the key to the model rather than colliding with it.
     * </p>
     * <p>
     * The key may be a feature's name or its configured {@code key} - the two are the same
     * question to a document.
     * </p>
     *
     * @param eClass the type in question, may be {@code null}
     * @param key the JSON key the codec would reserve, may be {@code null}
     * @return true if the type declares a feature under that key
     */
    public static boolean declaresKey(EClass eClass, String key) {
        if (eClass == null || key == null || key.isEmpty()) {
            return false;
        }
        if (eClass.getEStructuralFeature(key) != null) {
            return true;
        }
        return eClass.getEAllStructuralFeatures().stream()
                .map(feature -> getAnnotationDetail(feature, AnnotationSources.CODEC, "key"))
                .anyMatch(key::equals);
    }

    /**
     * Checks if an EAnnotation with the given source exists on the element.
     *
     * @param element the model element
     * @param source the annotation source URI
     * @return true if the annotation exists
     */
    public static boolean hasAnnotation(EModelElement element, String source) {
        return element != null && element.getEAnnotation(source) != null;
    }
}
