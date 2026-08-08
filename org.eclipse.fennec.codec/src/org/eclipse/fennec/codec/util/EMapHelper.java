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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Helper class for detecting and working with EMF EMap references.
 * <p>
 * EMF models EMap as a multi-valued containment reference to a class
 * that implements {@code java.util.Map$Entry}. This helper provides
 * utility methods for detecting such references and extracting key/value
 * features from map entry classes.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public final class EMapHelper {

    /** The instance class name used by EMF for Map.Entry types. */
    public static final String MAP_ENTRY_CLASS_NAME = "java.util.Map$Entry";

    private EMapHelper() {
        // Utility class, not instantiable
    }

    /**
     * Checks if the given EReference points to a Map.Entry type (i.e., represents an EMap).
     * <p>
     * Detection is performed by:
     * <ol>
     *   <li>Checking if the reference type's {@code instanceClassName} equals {@code "java.util.Map$Entry"}</li>
     *   <li>Falling back to checking if the reference type has both {@code "key"} and {@code "value"} features</li>
     * </ol>
     * </p>
     *
     * @param reference the EReference to check
     * @return true if the reference is to a Map.Entry type
     */
    public static boolean isMapEntryReference(EReference reference) {
        if (reference == null) {
            return false;
        }
        EClass entryClass = reference.getEReferenceType();
        return isMapEntryClass(entryClass);
    }

    /**
     * Checks if the given EClass represents a Map.Entry type.
     * <p>
     * Detection is performed by:
     * <ol>
     *   <li>Checking if {@code instanceClassName} equals {@code "java.util.Map$Entry"}</li>
     *   <li>Falling back to checking if the class has both {@code "key"} and {@code "value"} features</li>
     * </ol>
     * </p>
     *
     * @param eClass the EClass to check
     * @return true if the class represents a Map.Entry type
     */
    public static boolean isMapEntryClass(EClass eClass) {
        if (eClass == null) {
            return false;
        }

        String instanceClassName = eClass.getInstanceClassName();
        if (MAP_ENTRY_CLASS_NAME.equals(instanceClassName)) {
            return true;
        }

        // Fallback: check for key and value features
        return eClass.getEStructuralFeature("key") != null
                && eClass.getEStructuralFeature("value") != null;
    }

    /**
     * Returns the "key" feature of a map entry class.
     *
     * @param entryClass the map entry EClass
     * @return the key feature, or null if not found
     */
    public static EStructuralFeature getKeyFeature(EClass entryClass) {
        if (entryClass == null) {
            return null;
        }
        return entryClass.getEStructuralFeature("key");
    }

    /**
     * Returns the "value" feature of a map entry class.
     *
     * @param entryClass the map entry EClass
     * @return the value feature, or null if not found
     */
    public static EStructuralFeature getValueFeature(EClass entryClass) {
        if (entryClass == null) {
            return null;
        }
        return entryClass.getEStructuralFeature("value");
    }
}
