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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.jackson.JacksonFormatProvider;
import org.eclipse.fennec.codec.resource.CodecFormatResourceFactory;
import org.eclipse.fennec.codec.resource.CodecResourceFactory;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tools.jackson.core.json.JsonFactory;

/**
 * Containment children whose concrete type is a <em>subtype</em> of the containment feature's
 * declared type must still be inlined, not written as a reference.
 * <p>
 * Reproduces what the Model Atlas REST API returns for {@code GET /scopes/{scope}}: the
 * {@code registries} feature is declared as {@code RegistryInfo} while the instances are
 * {@code Registry} (a subtype living in another EPackage), and the children come out as
 * {@code {"_type": "…#//Registry", "$ref": "#//"}} instead of their fields.
 * </p>
 */
@DisplayName("Containment of subtype instances (both write paths)")
class SubtypeContainmentSerializationTest {

    private static final String BASE_NS = "http://example.org/codec/subtype/base/1.0";
    private static final String EXTENSION_NS = "http://example.org/codec/subtype/extension/1.0";

    @TempDir
    Path tempDir;

    private MetadataWhiteboard metadataService;
    private final List<EPackage> registeredPackages = new ArrayList<>();

    private EPackage basePackage;
    private EPackage extensionPackage;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();
        basePackage = createBasePackage();
        extensionPackage = createExtensionPackage(baseClass());
        register(basePackage);
        register(extensionPackage);
    }

    @AfterEach
    void tearDown() {
        registeredPackages.forEach(p -> EPackage.Registry.INSTANCE.remove(p.getNsURI()));
        registeredPackages.clear();
    }

    @Test
    @DisplayName("same-package subtype is inlined [plain JSON]")
    void plainSamePackageSubtypeIsInlined() throws IOException {
        assertItemInlined(newItem(samePackageSubtypeClass(), "kept-same-package"), false);
    }

    @Test
    @DisplayName("same-package subtype is inlined [format delegate]")
    void delegateSamePackageSubtypeIsInlined() throws IOException {
        assertItemInlined(newItem(samePackageSubtypeClass(), "kept-same-package"), true);
    }

    @Test
    @DisplayName("subtype from another package is inlined [plain JSON]")
    void plainForeignPackageSubtypeIsInlined() throws IOException {
        assertItemInlined(newItem(foreignPackageSubtypeClass(), "kept-foreign-package"), false);
    }

    @Test
    @DisplayName("subtype from another package is inlined [format delegate]")
    void delegateForeignPackageSubtypeIsInlined() throws IOException {
        assertItemInlined(newItem(foreignPackageSubtypeClass(), "kept-foreign-package"), true);
    }

    @Test
    @DisplayName("containment child without a container is inlined [plain JSON]")
    void plainChildWithoutContainerIsInlined() throws IOException {
        assertItemWithoutContainerInlined(false);
    }

    @Test
    @DisplayName("containment child without a container is inlined [format delegate]")
    void delegateChildWithoutContainerIsInlined() throws IOException {
        assertItemWithoutContainerInlined(true);
    }

    @Test
    @DisplayName("containment child without a container survives a round trip [plain JSON]")
    void plainChildWithoutContainerSurvivesRoundTrip() throws IOException {
        assertChildWithoutContainerSurvivesRoundTrip(false);
    }

    @Test
    @DisplayName("containment child without a container survives a round trip [format delegate]")
    void delegateChildWithoutContainerSurvivesRoundTrip() throws IOException {
        assertChildWithoutContainerSurvivesRoundTrip(true);
    }

    /**
     * The inlined child must come back with its data - this is the actual data-loss guarantee
     * behind the Model Atlas symptom (RegistryInfo stubs with null name/type on the client).
     */
    @SuppressWarnings("unchecked")
    private void assertChildWithoutContainerSurvivesRoundTrip(boolean withFormatProvider) throws IOException {
        EObject root = basePackage.getEFactoryInstance().create(rootClass());
        EObject item = newItem(samePackageSubtypeClass(), "kept-round-trip");
        ((InternalEList<EObject>) root.eGet(itemsReference())).basicAdd(item, null);

        writeJson(root, withFormatProvider);

        ResourceSet readSet = newResourceSet(withFormatProvider);
        Resource loaded = readSet.getResource(
                URI.createFileURI(tempDir.resolve("root.json").toAbsolutePath().toString()), true);
        EObject loadedRoot = loaded.getContents().get(0);
        List<EObject> items = (List<EObject>) loadedRoot.eGet(itemsReference());
        assertEquals(1, items.size(), "the contained child must be read back as a real object");
        EObject loadedItem = items.get(0);
        assertEquals(samePackageSubtypeClass().getName(), loadedItem.eClass().getName(),
                "the child must keep its concrete type");
        assertEquals("kept-round-trip", loadedItem.eGet(nameAttributeOf(loadedItem)),
                "the child's data must survive the round trip");
    }

    /**
     * Models generated with {@code suppressNotification="true"} back their containment features
     * with a {@code BasicInternalEList}, which never sets the child's container. The child is in
     * the feature but reports no container and no resource - it is still part of this document.
     * {@code basicAdd} reproduces that state without generated code.
     */
    @SuppressWarnings("unchecked")
    private void assertItemWithoutContainerInlined(boolean withFormatProvider) throws IOException {
        EObject root = basePackage.getEFactoryInstance().create(rootClass());
        EObject item = newItem(samePackageSubtypeClass(), "kept-without-container");
        ((InternalEList<EObject>) root.eGet(itemsReference())).basicAdd(item, null);

        assertNull(item.eContainer(), "test setup: the child must have no container");

        String json = writeJson(root, withFormatProvider);

        assertTrue(json.contains("kept-without-container"),
                "a child that owns no resource is part of this document and must be inlined, was: " + json);
        assertFalse(json.contains("$ref"),
                "a child that owns no resource must not be written as a reference, was: " + json);
    }

    @SuppressWarnings("unchecked")
    private void assertItemInlined(EObject item, boolean withFormatProvider) throws IOException {
        EObject root = basePackage.getEFactoryInstance().create(rootClass());
        ((List<EObject>) root.eGet(itemsReference())).add(item);

        String json = writeJson(root, withFormatProvider);

        assertTrue(json.contains(item.eGet(nameAttributeOf(item)).toString()),
                "contained subtype instance must be inlined with its fields, was: " + json);
        assertFalse(json.contains("$ref"),
                "a contained subtype instance is part of this document, not a reference, was: " + json);
    }

    private String writeJson(EObject root, boolean withFormatProvider) throws IOException {
        ResourceSet resourceSet = newResourceSet(withFormatProvider);
        Resource resource = resourceSet.createResource(
                URI.createFileURI(tempDir.resolve("root.json").toAbsolutePath().toString()));
        resource.getContents().add(root);
        resource.save(Collections.emptyMap());
        return Files.readString(tempDir.resolve("root.json"));
    }

    private ResourceSet newResourceSet(boolean withFormatProvider) {
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource.Factory factory = withFormatProvider
                ? new CodecFormatResourceFactory(metadataService,
                        new JacksonFormatProvider("json", new JsonFactory()),
                        ConfigurationResolver.defaults())
                : new CodecResourceFactory(metadataService, ConfigurationResolver.defaults());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("json", factory);
        return resourceSet;
    }

    private EObject newItem(EClass itemClass, String name) {
        EObject item = itemClass.getEPackage().getEFactoryInstance().create(itemClass);
        item.eSet(nameAttributeOf(item), name);
        return item;
    }

    private EAttribute nameAttributeOf(EObject item) {
        return (EAttribute) item.eClass().getEStructuralFeature("name");
    }

    private EPackage createBasePackage() {
        EPackage pkg = newPackage("base", BASE_NS);

        EClass base = newClass("Base");
        base.getEStructuralFeatures().add(newNameAttribute());

        EClass samePackageSubtype = newClass("SamePackageItem");
        samePackageSubtype.getESuperTypes().add(base);

        EClass root = newClass("Root");
        EReference items = EcoreFactory.eINSTANCE.createEReference();
        items.setName("items");
        items.setEType(base);
        items.setUpperBound(-1);
        items.setContainment(true);
        root.getEStructuralFeatures().add(items);

        pkg.getEClassifiers().addAll(List.of(base, samePackageSubtype, root));
        return pkg;
    }

    private EPackage createExtensionPackage(EClass base) {
        EPackage pkg = newPackage("extension", EXTENSION_NS);
        EClass foreignSubtype = newClass("ForeignPackageItem");
        foreignSubtype.getESuperTypes().add(base);
        pkg.getEClassifiers().add(foreignSubtype);
        return pkg;
    }

    private void register(EPackage pkg) {
        // Give the package a resource, otherwise the written type URI has no schema part
        // and the reader cannot resolve the EClass on load
        new ResourceImpl(URI.createURI(pkg.getNsURI())).getContents().add(pkg);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        metadataService.registerPackage(pkg);
        registeredPackages.add(pkg);
    }

    private EPackage newPackage(String name, String nsUri) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName(name);
        pkg.setNsPrefix(name);
        pkg.setNsURI(nsUri);
        return pkg;
    }

    private EClass newClass(String name) {
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(name);
        return eClass;
    }

    private EAttribute newNameAttribute() {
        EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
        name.setName("name");
        name.setEType(EcorePackage.Literals.ESTRING);
        return name;
    }

    private EClass baseClass() {
        return (EClass) basePackage.getEClassifier("Base");
    }

    private EClass rootClass() {
        return (EClass) basePackage.getEClassifier("Root");
    }

    private EReference itemsReference() {
        return (EReference) rootClass().getEStructuralFeature("items");
    }

    private EClass samePackageSubtypeClass() {
        return (EClass) basePackage.getEClassifier("SamePackageItem");
    }

    private EClass foreignPackageSubtypeClass() {
        return (EClass) extensionPackage.getEClassifier("ForeignPackageItem");
    }
}
