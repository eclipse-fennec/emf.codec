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
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Cross-resource reference tests on the plain JSON path (no format provider), issue #113.
 * <p>
 * These tests write and read <b>real files</b> and use a <b>separate ResourceSet</b> for
 * saving and loading, so nothing is pre-populated: the target document is only found if
 * the written URI actually names it, and resolving really goes to disk. Proxies are
 * deliberately not resolved by the codec — the caller resolves them, here via
 * {@link EcoreUtil#resolve(EObject, ResourceSet)}.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/10-reference.md">Spec: Reference Serialization</a>
 */
@DisplayName("Cross-resource references (plain JSON path, real files)")
class CrossResourceReferenceTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/ser/test-roundtrip.ecore";

    @TempDir
    Path tempDir;

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EClass addressClass;
    private EClass companyClass;
    private EAttribute nameAttribute;
    private EAttribute streetAttribute;
    private EAttribute companyNameAttribute;
    private EReference managerRef;
    private EReference addressRef;
    private EReference employeesRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, CrossResourceReferenceTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        addressClass = EcoreHelper.getEClass(testPackage, "Address");
        companyClass = EcoreHelper.getEClass(testPackage, "Company");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        streetAttribute = (EAttribute) EcoreHelper.getFeature(addressClass, "street");
        companyNameAttribute = (EAttribute) EcoreHelper.getFeature(companyClass, "name");
        managerRef = (EReference) EcoreHelper.getFeature(personClass, "manager");
        addressRef = (EReference) EcoreHelper.getFeature(personClass, "address");
        employeesRef = (EReference) EcoreHelper.getFeature(companyClass, "employees");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("non-containment reference names the target file and resolves from a fresh set")
    void nonContainmentReferenceResolvesAcrossFiles() throws IOException {
        ResourceSet writeSet = newResourceSet();
        Resource personRes = writeSet.createResource(fileUri("person.json"));
        Resource managerRes = writeSet.createResource(fileUri("manager.json"));

        EObject boss = createPerson("Boss");
        managerRes.getContents().add(boss);
        EObject alice = createPerson("Alice");
        alice.eSet(managerRef, boss);
        personRes.getContents().add(alice);

        managerRes.save(Collections.emptyMap());
        personRes.save(Collections.emptyMap());

        String json = Files.readString(tempDir.resolve("person.json"));
        assertTrue(json.contains("manager.json"),
                "cross-resource reference must name the target file, was: " + json);

        // Fresh set: nothing is pre-populated, manager.json has to be found via the URI
        ResourceSet readSet = newResourceSet();
        Resource loadedPersonRes = readSet.getResource(fileUri("person.json"), true);

        EObject loadedAlice = loadedPersonRes.getContents().get(0);
        EObject manager = (EObject) loadedAlice.eGet(managerRef, false);
        assertNotNull(manager, "manager reference must be set");
        assertTrue(manager.eIsProxy(), "the codec leaves cross-resource targets as proxies");
        assertEquals(fileUri("manager.json"),
                ((InternalEObject) manager).eProxyURI().trimFragment(),
                "proxy URI must point at the target file");

        EObject resolved = EcoreUtil.resolve(manager, readSet);
        assertFalse(resolved.eIsProxy(), "proxy must resolve by loading the target file");
        assertEquals("Boss", resolved.eGet(nameAttribute));
    }

    @Test
    @DisplayName("cross-document containment is referenced, not inlined")
    void crossDocumentContainmentIsReferencedNotInlined() throws IOException {
        ResourceSet writeSet = newResourceSet();
        Resource personRes = writeSet.createResource(fileUri("person.json"));
        Resource addressRes = writeSet.createResource(fileUri("address.json"));

        EObject address = testPackage.getEFactoryInstance().create(addressClass);
        address.eSet(streetAttribute, "Main Street 1");
        EObject alice = createPerson("Alice");
        alice.eSet(addressRef, address);
        personRes.getContents().add(alice);
        // cross-resource containment: contained by alice, but living in its own resource
        addressRes.getContents().add(address);

        assertEquals(alice, address.eContainer(), "test setup: address must stay contained");
        assertNotNull(((InternalEObject) address).eDirectResource(),
                "test setup: address must own its resource");

        addressRes.save(Collections.emptyMap());
        personRes.save(Collections.emptyMap());

        String json = Files.readString(tempDir.resolve("person.json"));
        assertTrue(json.contains("address.json"),
                "cross-document containment must reference the target file, was: " + json);
        assertFalse(json.contains("Main Street 1"),
                "cross-document containment must not be inlined, was: " + json);
    }

    @Test
    @Disabled("#123 - cross-document containment still builds an empty object instead of a proxy")
    @DisplayName("cross-document containment resolves from a fresh set")
    void crossDocumentContainmentResolvesAcrossFiles() throws IOException {
        ResourceSet writeSet = newResourceSet();
        Resource personRes = writeSet.createResource(fileUri("person.json"));
        Resource addressRes = writeSet.createResource(fileUri("address.json"));

        EObject address = testPackage.getEFactoryInstance().create(addressClass);
        address.eSet(streetAttribute, "Main Street 1");
        EObject alice = createPerson("Alice");
        alice.eSet(addressRef, address);
        personRes.getContents().add(alice);
        // cross-resource containment: contained by alice, but living in its own resource
        addressRes.getContents().add(address);

        assertEquals(alice, address.eContainer(), "test setup: address must stay contained");
        assertNotNull(((InternalEObject) address).eDirectResource(),
                "test setup: address must own its resource");

        addressRes.save(Collections.emptyMap());
        personRes.save(Collections.emptyMap());

        String json = Files.readString(tempDir.resolve("person.json"));
        assertTrue(json.contains("address.json"),
                "cross-document containment must reference the target file, was: " + json);
        assertFalse(json.contains("Main Street 1"),
                "cross-document containment must not be inlined, was: " + json);

        ResourceSet readSet = newResourceSet();
        Resource loadedPersonRes = readSet.getResource(fileUri("person.json"), true);

        EObject person = loadedPersonRes.getContents().get(0);
        EObject addr = (EObject) person.eGet(addressRef, false);
        assertNotNull(addr, "containment reference must be set");
        assertTrue(addr.eIsProxy(), "cross-document containment comes back as a proxy");

        EObject resolved = EcoreUtil.resolve(addr, readSet);
        assertFalse(resolved.eIsProxy(), "proxy must resolve by loading the target file");
        assertEquals("Main Street 1", resolved.eGet(streetAttribute));
    }

    @Test
    @DisplayName("reference into a hierarchy in another file resolves to the very object")
    void referenceIntoHierarchyResolvesToTheRightObject() throws IOException {
        ResourceSet writeSet = newResourceSet();
        Resource companyRes = writeSet.createResource(fileUri("company.json"));
        Resource personRes = writeSet.createResource(fileUri("person.json"));

        EObject company = buildCompany();
        companyRes.getContents().add(company);

        // the target is at index 1 of a containment list, not a document root
        EObject target = employeesOf(company).get(1);
        EObject alice = createPerson("Alice");
        alice.eSet(managerRef, target);
        personRes.getContents().add(alice);

        companyRes.save(Collections.emptyMap());
        personRes.save(Collections.emptyMap());

        String json = Files.readString(tempDir.resolve("person.json"));
        assertTrue(json.contains("company.json"),
                "reference must name the target file, was: " + json);

        ResourceSet readSet = newResourceSet();
        Resource loadedPersonRes = readSet.getResource(fileUri("person.json"), true);

        EObject loadedAlice = loadedPersonRes.getContents().get(0);
        EObject manager = EcoreUtil.resolve((EObject) loadedAlice.eGet(managerRef, false), readSet);

        assertFalse(manager.eIsProxy(), "proxy into another file must resolve");
        assertEquals("Employee-1", manager.eGet(nameAttribute),
                "the fragment path must address the very object that was referenced, "
                        + "not another one at a similar position");
    }

    @Test
    @DisplayName("same-document reference stays document-internal and resolves locally")
    void sameDocumentReferenceResolvesLocally() throws IOException {
        ResourceSet writeSet = newResourceSet();
        Resource personRes = writeSet.createResource(fileUri("person.json"));

        EObject boss = createPerson("Boss");
        EObject alice = createPerson("Alice");
        alice.eSet(managerRef, boss);
        personRes.getContents().add(alice);
        personRes.getContents().add(boss);

        personRes.save(Collections.emptyMap());

        String json = Files.readString(tempDir.resolve("person.json"));
        assertFalse(json.contains("person.json"),
                "a same-document reference must not name its own file, was: " + json);

        ResourceSet readSet = newResourceSet();
        Resource loaded = readSet.getResource(fileUri("person.json"), true);

        EObject loadedAlice = loaded.getContents().get(0);
        EObject manager = EcoreUtil.resolve((EObject) loadedAlice.eGet(managerRef, false), readSet);
        assertFalse(manager.eIsProxy(), "same-document reference must resolve within the document");
        assertEquals("Boss", manager.eGet(nameAttribute));
        assertEquals(loaded.getContents().get(1), manager,
                "must resolve to the very object in this resource, not a copy");
    }

    @Test
    @DisplayName("same-document reference into a hierarchy resolves to the very object")
    void sameDocumentReferenceIntoHierarchyResolvesToTheRightObject() throws IOException {
        ResourceSet writeSet = newResourceSet();
        Resource res = writeSet.createResource(fileUri("company.json"));

        EObject company = buildCompany();
        res.getContents().add(company);

        // Alice is a second root in the same file, pointing into the employee hierarchy
        EObject target = employeesOf(company).get(1);
        EObject alice = createPerson("Alice");
        alice.eSet(managerRef, target);
        res.getContents().add(alice);

        res.save(Collections.emptyMap());

        String json = Files.readString(tempDir.resolve("company.json"));
        assertFalse(json.contains("company.json"),
                "a same-document reference must not name its own file, was: " + json);

        ResourceSet readSet = newResourceSet();
        Resource loaded = readSet.getResource(fileUri("company.json"), true);

        EObject loadedAlice = loaded.getContents().get(1);
        EObject manager = EcoreUtil.resolve((EObject) loadedAlice.eGet(managerRef, false), readSet);

        assertFalse(manager.eIsProxy(), "same-document reference must resolve within the document");
        assertEquals("Employee-1", manager.eGet(nameAttribute),
                "the fragment path must address the very object inside the hierarchy");
        assertEquals(employeesOf(loaded.getContents().get(0)).get(1), manager,
                "must resolve to the very object in this resource, not a copy");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private ResourceSet newResourceSet() {
        ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("json", new CodecResourceFactory(metadataService, ConfigurationResolver.defaults()));
        return rs;
    }

    private URI fileUri(String fileName) {
        return URI.createFileURI(tempDir.resolve(fileName).toAbsolutePath().toString());
    }

    private EObject createPerson(String name) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttribute, name);
        return person;
    }

    @SuppressWarnings("unchecked")
    private List<EObject> employeesOf(EObject company) {
        return (List<EObject>) company.eGet(employeesRef);
    }

    /** Company with three contained employees - the reference target is not a root object. */
    private EObject buildCompany() {
        EObject company = testPackage.getEFactoryInstance().create(companyClass);
        company.eSet(companyNameAttribute, "ACME");
        for (int i = 0; i < 3; i++) {
            employeesOf(company).add(createPerson("Employee-" + i));
        }
        return company;
    }
}
