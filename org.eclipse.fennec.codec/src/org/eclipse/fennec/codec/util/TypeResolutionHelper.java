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

import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

/**
 * Helper class for resolving EMF EClasses from various type representations.
 * <p>
 * Supports resolution by:
 * <ul>
 *   <li>Simple name: {@code "Person"} — searches all registered EPackages</li>
 *   <li>Java class name: {@code "com.example.Person"} — matches instanceClassName</li>
 *   <li>Numeric classifier ID: {@code "3"} — looks up by classifier ID (with optional package hint)</li>
 *   <li>Full URI: {@code "http://example.org/1.0#//Person"} — direct nsURI + fragment lookup</li>
 * </ul>
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2026-02-05
 */
public final class TypeResolutionHelper {

    private static final Logger LOGGER = Logger.getLogger(TypeResolutionHelper.class.getName());

    private TypeResolutionHelper() {
        // Utility class, not instantiable
    }

    /**
     * Resolves an EClass by its simple name, scoped to a context package.
     * <p>
     * If a context package is provided, only that package is searched.
     * If no context package is provided, falls back to scanning all registered
     * EPackages (non-deterministic — logs a warning).
     * </p>
     * <p>
     * Security: CWE-843 (S-4). The NAME type strategy should always be used with
     * a schema hint ({@code CODEC_ROOT_SCHEMA} or {@code CODEC_ROOT_TYPE}) to
     * avoid non-deterministic resolution across multiple registered EPackages.
     * </p>
     *
     * @param className the simple class name (e.g. "Person")
     * @param contextPackage the EPackage to scope resolution to (may be null)
     * @return the resolved EClass, or null if not found
     */
    public static EClass resolveFromSimpleName(String className, EPackage contextPackage) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        // If context package is provided, scope resolution to it
        if (contextPackage != null) {
            EClassifier classifier = contextPackage.getEClassifier(className);
            if (classifier instanceof EClass) {
                return (EClass) classifier;
            }
            LOGGER.warning("Could not resolve EClass '" + className
                    + "' in context package: " + contextPackage.getNsURI());
            return null;
        }
        // S-4: No global scan — NAME strategy requires a schema hint.
        LOGGER.warning("Could not resolve EClass '" + className
                + "' — no context package provided. "
                + "NAME strategy requires CODEC_ROOT_SCHEMA or CODEC_ROOT_TYPE.");
        return null;
    }

    /**
     * Resolves an EClass by its simple name by scanning all registered EPackages.
     * <p>
     * <b>Warning:</b> This method scans all registered EPackages and returns the
     * first match. Resolution order is undefined, making this non-deterministic
     * when multiple packages define classes with the same name. Prefer
     * {@link #resolveFromSimpleName(String, EPackage)} with a context package.
     * </p>
     *
     * @param className the simple class name (e.g. "Person")
     * @return the resolved EClass, or null if not found
     */
    public static EClass resolveFromSimpleName(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        for (Object key : EPackage.Registry.INSTANCE.keySet()) {
            EPackage pkg = EPackage.Registry.INSTANCE.getEPackage((String) key);
            if (pkg != null) {
                EClassifier classifier = pkg.getEClassifier(className);
                if (classifier instanceof EClass) {
                    return (EClass) classifier;
                }
            }
        }
        LOGGER.warning("Could not resolve EClass from simple name: " + className);
        return null;
    }

    /**
     * Resolves an EClass by its Java instance class name, scoped to a context package.
     * <p>
     * If a context package is provided, only that package is searched for a classifier
     * whose {@code instanceClassName} matches. If no context package is provided, resolution
     * fails with a warning.
     * </p>
     * <p>
     * Security: CWE-843 (S-4). The CLASS type strategy should always be used with
     * a schema hint ({@code CODEC_ROOT_SCHEMA} or {@code CODEC_ROOT_TYPE}) to
     * avoid scanning all registered EPackages.
     * </p>
     *
     * @param className the fully qualified Java class name (e.g. "com.example.Person")
     * @param contextPackage the EPackage to scope resolution to (may be null)
     * @return the resolved EClass, or null if not found
     */
    public static EClass resolveFromClassName(String className, EPackage contextPackage) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        if (contextPackage != null) {
            for (EClassifier classifier : contextPackage.getEClassifiers()) {
                if (classifier instanceof EClass eClass) {
                    Class<?> instanceClass = eClass.getInstanceClass();
                    if (instanceClass != null && className.equals(instanceClass.getName())) {
                        return eClass;
                    }
                }
            }
            // Fallback to simple name within same context package
            String simpleName = className.contains(".")
                    ? className.substring(className.lastIndexOf('.') + 1)
                    : className;
            return resolveFromSimpleName(simpleName, contextPackage);
        }
        // S-4: No global scan — CLASS strategy requires a schema hint.
        LOGGER.warning("Could not resolve EClass from class name '" + className
                + "' — no context package provided. "
                + "CLASS strategy requires CODEC_ROOT_SCHEMA or CODEC_ROOT_TYPE.");
        return null;
    }

    /**
     * Resolves an EClass by its Java instance class name by scanning all registered EPackages.
     * <p>
     * <b>Warning:</b> This method scans all registered EPackages. Resolution order is
     * undefined, making this non-deterministic. Prefer
     * {@link #resolveFromClassName(String, EPackage)} with a context package.
     * </p>
     *
     * @param className the fully qualified Java class name (e.g. "com.example.Person")
     * @return the resolved EClass, or null if not found
     */
    public static EClass resolveFromClassName(String className) {
        if (className == null || className.isEmpty()) {
            return null;
        }
        for (Object key : EPackage.Registry.INSTANCE.keySet()) {
            EPackage pkg = EPackage.Registry.INSTANCE.getEPackage((String) key);
            if (pkg != null) {
                for (EClassifier classifier : pkg.getEClassifiers()) {
                    if (classifier instanceof EClass eClass) {
                        Class<?> instanceClass = eClass.getInstanceClass();
                        if (instanceClass != null && className.equals(instanceClass.getName())) {
                            return eClass;
                        }
                    }
                }
            }
        }
        // Fallback to simple name (use last segment of className)
        String simpleName = className.contains(".")
                ? className.substring(className.lastIndexOf('.') + 1)
                : className;
        return resolveFromSimpleName(simpleName);
    }

    /**
     * Resolves an EClass by its classifier ID.
     * <p>
     * For PLAIN NUMERIC format, the classifier ID alone is ambiguous since
     * different packages can have the same classifier IDs. If a hint EClass
     * is provided, its package is used for lookup first.
     * </p>
     *
     * @param numericValue the classifier ID as string
     * @param hintEClass optional hint EClass for package context (may be null)
     * @return the resolved EClass, or null if not found or not a valid number
     */
    public static EClass resolveFromNumeric(String numericValue, EClass hintEClass) {
        return resolveFromNumeric(numericValue, hintEClass, null);
    }

    /**
     * Resolves an EClass by its classifier ID using hint EClass or context schema.
     * <p>
     * Resolution order:
     * <ol>
     *   <li>Hint EClass package (if provided)</li>
     *   <li>Context schema URI (if provided) — looks up EPackage by nsURI</li>
     * </ol>
     * If neither hint nor schema URI is provided, resolution fails with a warning (S-4).
     * Per the spec (§9.3), NUMERIC strategy <b>requires</b> a schema hint
     * ({@code CODEC_ROOT_SCHEMA} or {@code CODEC_ROOT_TYPE}) for deserialization.
     * </p>
     *
     * @param numericValue the classifier ID as string
     * @param hintEClass optional hint EClass for package context (may be null)
     * @param contextSchemaUri optional context schema URI for package lookup (may be null)
     * @return the resolved EClass, or null if not found or not a valid number
     */
    public static EClass resolveFromNumeric(String numericValue, EClass hintEClass, String contextSchemaUri) {
        if (numericValue == null || numericValue.isEmpty()) {
            return null;
        }
        try {
            int classifierId = Integer.parseInt(numericValue);

            // If we have a hint, try its package first (most reliable)
            if (hintEClass != null && hintEClass.getEPackage() != null) {
                EClass resolved = findClassifierInPackage(hintEClass.getEPackage(), classifierId);
                if (resolved != null) {
                    return resolved;
                }
            }

            // Try context schema URI to locate the package
            if (contextSchemaUri != null && !contextSchemaUri.isEmpty()) {
                EPackage pkg = EPackage.Registry.INSTANCE.getEPackage(contextSchemaUri);
                if (pkg != null) {
                    EClass resolved = findClassifierInPackage(pkg, classifierId);
                    if (resolved != null) {
                        return resolved;
                    }
                }
            }

            // S-4: No global scan fallback — NUMERIC requires a schema hint.
            // Classifier IDs are package-specific and non-deterministic without context.
            LOGGER.warning("Could not resolve numeric classifier ID " + numericValue
                    + " — no schema hint or context package provided. "
                    + "NUMERIC strategy requires CODEC_ROOT_SCHEMA or CODEC_ROOT_TYPE.");
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid numeric classifier ID: " + numericValue);
        }
        return null;
    }

    /**
     * Finds an EClass by classifier ID within a specific package.
     *
     * @param pkg the EPackage to search
     * @param classifierId the classifier ID
     * @return the EClass if found, null otherwise
     */
    public static EClass findClassifierInPackage(EPackage pkg, int classifierId) {
        if (pkg == null) {
            return null;
        }
        for (EClassifier classifier : pkg.getEClassifiers()) {
            if (classifier instanceof EClass && classifier.getClassifierID() == classifierId) {
                return (EClass) classifier;
            }
        }
        return null;
    }

    /**
     * Resolves an EClass from a full EMF URI.
     * <p>
     * Expected format: {@code "nsURI#//ClassName"}, e.g.
     * {@code "http://example.org/model/1.0#//Person"}.
     * </p>
     *
     * @param uri the URI string
     * @return the resolved EClass, or null if not found or URI is invalid
     */
    public static EClass resolveFromUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            return null;
        }
        try {
            URI emfUri = URI.createURI(uri);
            String nsUri = emfUri.trimFragment().toString();
            String fragment = emfUri.fragment();

            if (fragment == null || !fragment.startsWith("//")) {
                LOGGER.warning("Invalid EClass URI fragment: " + uri);
                return null;
            }

            String className = fragment.substring(2); // Remove "//"

            EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsUri);
            if (ePackage == null) {
                LOGGER.warning("EPackage not found for URI: " + nsUri);
                return null;
            }

            Object classifier = ePackage.getEClassifier(className);
            if (classifier instanceof EClass) {
                return (EClass) classifier;
            } else {
                LOGGER.warning("Classifier is not an EClass: " + className);
                return null;
            }
        } catch (Exception e) {
            LOGGER.warning("Error resolving EClass from URI: " + uri + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if a type value string looks like a full URI (contains "#").
     *
     * @param typeValue the type value to check
     * @return true if the value appears to be a URI
     */
    public static boolean isUri(String typeValue) {
        return typeValue != null && typeValue.contains("#");
    }
}
