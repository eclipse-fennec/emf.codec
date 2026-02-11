/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.model.metadata.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Helper class for loading and managing ecore files in tests.
 * <p>
 * Loads ecore files from the classpath using getResourceAsStream and provides
 * a simple API for accessing the loaded EPackages. This utility class can be
 * used across different test projects.
 * </p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Create helper with context class for resource loading
 * EcoreTestHelper helper = new EcoreTestHelper(MyTest.class);
 *
 * // Load ecore file from same package as context class
 * EPackage pkg = helper.loadEcore("test-model.ecore");
 *
 * // Get EClass by name
 * EClass personClass = helper.getEClass(pkg, "Person");
 *
 * // Get feature by name
 * EStructuralFeature nameAttr = helper.getFeature(personClass, "name");
 * }</pre>
 *
 * @author Mark Hoffmann
 * @since 2025-12-04
 */
public class EcoreHelper {

    private final ResourceSet resourceSet;
    private final Map<String, EPackage> loadedPackages = new HashMap<>();
    private final Set<String> registeredNsURIs = new HashSet<>();
    private final Class<?> contextClass;

    /**
     * Creates a new EcoreTestHelper with the given context class.
     * <p>
     * The context class is used to resolve resource paths - ecore files
     * are loaded relative to the context class's package location.
     * </p>
     *
     * @param contextClass the class to use for resource loading
     */
    public EcoreHelper(Class<?> contextClass) {
        this.contextClass = contextClass;
        this.resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put("ecore", new XMIResourceFactoryImpl());
    }

    /**
     * Creates a new EcoreTestHelper using this class as context.
     * <p>
     * Ecore files will be loaded from the same package as EcoreTestHelper.
     * Use {@link #EcoreTestHelper(Class)} if you need to load from a different location.
     * </p>
     */
    public EcoreHelper() {
        this(EcoreHelper.class);
    }

    /**
     * Loads an ecore file from the same package as the context class.
     *
     * @param fileName the ecore file name (e.g., "test-model.ecore")
     * @return the loaded EPackage
     * @throws IOException if loading fails
     */
    public EPackage loadEcore(String fileName) throws IOException {
        String key = contextClass.getName() + "/" + fileName;
        if (loadedPackages.containsKey(key)) {
            return loadedPackages.get(key);
        }

        try (InputStream is = contextClass.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new IOException("Could not find ecore file '" + fileName +
                    "' relative to " + contextClass.getName());
            }

            String uniqueUri = "platform:/test/" + contextClass.getPackageName().replace('.', '/') + "/" + fileName;
            URI uri = URI.createURI(uniqueUri);
            Resource resource = resourceSet.createResource(uri);
            resource.load(is, null);

            if (resource.getContents().isEmpty()) {
                throw new IOException("Ecore file is empty: " + fileName);
            }

            EPackage ePackage = (EPackage) resource.getContents().get(0);
            loadedPackages.put(key, ePackage);

            // Set resource URI to match package nsURI for EcoreUtil.getURI() to return nsURI#//ClassName
            resource.setURI(URI.createURI(ePackage.getNsURI()));

            // Also register in the global package registry
            String nsURI = ePackage.getNsURI();
            EPackage.Registry.INSTANCE.put(nsURI, ePackage);
            registeredNsURIs.add(nsURI);

            return ePackage;
        }
    }

    /**
     * Loads an ecore file from the classpath using an absolute path.
     *
     * @param absolutePath the absolute classpath path (e.g., "/org/example/test/model.ecore")
     * @return the loaded EPackage
     * @throws IOException if loading fails
     */
    public EPackage loadEcoreAbsolute(String absolutePath) throws IOException {
        if (loadedPackages.containsKey(absolutePath)) {
            return loadedPackages.get(absolutePath);
        }

        try (InputStream is = contextClass.getResourceAsStream(absolutePath)) {
            if (is == null) {
                throw new IOException("Could not find ecore file at absolute path: " + absolutePath);
            }

            URI uri = URI.createURI("platform:/test" + absolutePath);
            Resource resource = resourceSet.createResource(uri);
            resource.load(is, null);

            if (resource.getContents().isEmpty()) {
                throw new IOException("Ecore file is empty: " + absolutePath);
            }

            EPackage ePackage = (EPackage) resource.getContents().get(0);
            loadedPackages.put(absolutePath, ePackage);

            // Set resource URI to match package nsURI for EcoreUtil.getURI() to return nsURI#//ClassName
            resource.setURI(URI.createURI(ePackage.getNsURI()));

            // Also register in the global package registry
            String nsURI = ePackage.getNsURI();
            EPackage.Registry.INSTANCE.put(nsURI, ePackage);
            registeredNsURIs.add(nsURI);

            return ePackage;
        }
    }

    /**
     * Gets an EClass by name from the given package.
     *
     * @param ePackage the package to search
     * @param className the class name
     * @return the EClass
     * @throws IllegalArgumentException if the class is not found
     */
    public EClass getEClass(EPackage ePackage, String className) {
        EClass eClass = (EClass) ePackage.getEClassifier(className);
        if (eClass == null) {
            throw new IllegalArgumentException("EClass '" + className +
                "' not found in package " + ePackage.getNsURI());
        }
        return eClass;
    }

    /**
     * Gets an EStructuralFeature by name from the given class.
     *
     * @param eClass the class to search
     * @param featureName the feature name
     * @return the EStructuralFeature
     * @throws IllegalArgumentException if the feature is not found
     */
    public EStructuralFeature getFeature(EClass eClass, String featureName) {
        EStructuralFeature feature = eClass.getEStructuralFeature(featureName);
        if (feature == null) {
            throw new IllegalArgumentException("Feature '" + featureName +
                "' not found in class " + eClass.getName());
        }
        return feature;
    }

    /**
     * Gets an already loaded package by file name.
     *
     * @param fileName the ecore file name
     * @return the loaded EPackage, or null if not loaded
     */
    public EPackage getPackage(String fileName) {
        String key = contextClass.getName() + "/" + fileName;
        return loadedPackages.get(key);
    }

    /**
     * Gets a loaded package by its namespace URI.
     *
     * @param nsURI the namespace URI
     * @return the EPackage, or null if not found
     */
    public EPackage getPackageByNsURI(String nsURI) {
        return (EPackage) resourceSet.getPackageRegistry().get(nsURI);
    }

    /**
     * Releases all loaded ecore resources and unregisters packages from the global registry.
     */
    public void releaseAll() {
        // Unregister from global package registry
        for (String nsURI : registeredNsURIs) {
            EPackage.Registry.INSTANCE.remove(nsURI);
        }
        registeredNsURIs.clear();

        for (Resource resource : resourceSet.getResources()) {
            resource.unload();
        }
        resourceSet.getResources().clear();
        loadedPackages.clear();
    }

    /**
     * Gets the ResourceSet used by this helper.
     *
     * @return the ResourceSet
     */
    public ResourceSet getResourceSet() {
        return resourceSet;
    }

    /**
     * Gets the context class used for resource loading.
     *
     * @return the context class
     */
    public Class<?> getContextClass() {
        return contextClass;
    }
}
