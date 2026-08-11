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
import org.eclipse.fennec.codec.format.jackson.JacksonFormatProvider;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tools.jackson.core.json.JsonFactory;

/**
 * Cross-resource reference round trips (issues #113, #123, #124).
 * <p>
 * Every case runs against <b>both write paths</b> — plain JSON and the FormatDelegate path —
 * because they resolve the source resource differently. The tests write and read <b>real
 * files</b> and use a <b>separate ResourceSet</b> for saving and loading, so nothing is
 * pre-populated: the target document is only found if the written URI actually names it, and
 * resolving really goes to disk. Proxies are deliberately not resolved by the codec — the
 * caller resolves them, here via {@link EcoreUtil#resolve(EObject, ResourceSet)}.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/10-reference.md">Spec: Reference Serialization</a>
 */
@DisplayName("Cross-resource references (real files, both write paths)")
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
    private EReference friendsRef;

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
        friendsRef = (EReference) EcoreHelper.getFeature(personClass, "friends");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("non-containment reference names the target file and resolves from a fresh set [plain JSON]")
    void plainNonContainmentReferenceResolvesAcrossFiles() throws IOException {
        nonContainmentReferenceResolvesAcrossFiles(false);
    }

    @Test
    @DisplayName("non-containment reference names the target file and resolves from a fresh set [format delegate]")
    void delegateNonContainmentReferenceResolvesAcrossFiles() throws IOException {
        nonContainmentReferenceResolvesAcrossFiles(true);
    }

    private void nonContainmentReferenceResolvesAcrossFiles(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
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
        ResourceSet readSet = newResourceSet(withFormatProvider);
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
    @DisplayName("cross-document containment is referenced, not inlined [plain JSON]")
    void plainCrossDocumentContainmentIsReferencedNotInlined() throws IOException {
        crossDocumentContainmentIsReferencedNotInlined(false);
    }

    @Test
    @DisplayName("cross-document containment is referenced, not inlined [format delegate]")
    void delegateCrossDocumentContainmentIsReferencedNotInlined() throws IOException {
        crossDocumentContainmentIsReferencedNotInlined(true);
    }

    private void crossDocumentContainmentIsReferencedNotInlined(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
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
    @DisplayName("cross-document containment resolves from a fresh set [plain JSON]")
    void plainCrossDocumentContainmentResolvesAcrossFiles() throws IOException {
        crossDocumentContainmentResolvesAcrossFiles(false);
    }

    @Test
    @DisplayName("cross-document containment resolves from a fresh set [format delegate]")
    void delegateCrossDocumentContainmentResolvesAcrossFiles() throws IOException {
        crossDocumentContainmentResolvesAcrossFiles(true);
    }

    private void crossDocumentContainmentResolvesAcrossFiles(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
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

        ResourceSet readSet = newResourceSet(withFormatProvider);
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
    @DisplayName("reference into a hierarchy in another file resolves to the very object [plain JSON]")
    void plainReferenceIntoHierarchyResolvesToTheRightObject() throws IOException {
        referenceIntoHierarchyResolvesToTheRightObject(false);
    }

    @Test
    @DisplayName("reference into a hierarchy in another file resolves to the very object [format delegate]")
    void delegateReferenceIntoHierarchyResolvesToTheRightObject() throws IOException {
        referenceIntoHierarchyResolvesToTheRightObject(true);
    }

    private void referenceIntoHierarchyResolvesToTheRightObject(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
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

        ResourceSet readSet = newResourceSet(withFormatProvider);
        Resource loadedPersonRes = readSet.getResource(fileUri("person.json"), true);

        EObject loadedAlice = loadedPersonRes.getContents().get(0);
        EObject manager = EcoreUtil.resolve((EObject) loadedAlice.eGet(managerRef, false), readSet);

        assertFalse(manager.eIsProxy(), "proxy into another file must resolve");
        assertEquals("Employee-1", manager.eGet(nameAttribute),
                "the fragment path must address the very object that was referenced, "
                        + "not another one at a similar position");
    }

    @Test
    @DisplayName("same-document reference stays document-internal and resolves locally [plain JSON]")
    void plainSameDocumentReferenceResolvesLocally() throws IOException {
        sameDocumentReferenceResolvesLocally(false);
    }

    @Test
    @DisplayName("same-document reference stays document-internal and resolves locally [format delegate]")
    void delegateSameDocumentReferenceResolvesLocally() throws IOException {
        sameDocumentReferenceResolvesLocally(true);
    }

    private void sameDocumentReferenceResolvesLocally(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
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

        ResourceSet readSet = newResourceSet(withFormatProvider);
        Resource loaded = readSet.getResource(fileUri("person.json"), true);

        EObject loadedAlice = loaded.getContents().get(0);
        EObject manager = EcoreUtil.resolve((EObject) loadedAlice.eGet(managerRef, false), readSet);
        assertFalse(manager.eIsProxy(), "same-document reference must resolve within the document");
        assertEquals("Boss", manager.eGet(nameAttribute));
        assertEquals(loaded.getContents().get(1), manager,
                "must resolve to the very object in this resource, not a copy");
    }

    @Test
    @DisplayName("same-document reference into a hierarchy resolves to the very object [plain JSON]")
    void plainSameDocumentReferenceIntoHierarchyResolvesToTheRightObject() throws IOException {
        sameDocumentReferenceIntoHierarchyResolvesToTheRightObject(false);
    }

    @Test
    @DisplayName("same-document reference into a hierarchy resolves to the very object [format delegate]")
    void delegateSameDocumentReferenceIntoHierarchyResolvesToTheRightObject() throws IOException {
        sameDocumentReferenceIntoHierarchyResolvesToTheRightObject(true);
    }

    private void sameDocumentReferenceIntoHierarchyResolvesToTheRightObject(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
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

        ResourceSet readSet = newResourceSet(withFormatProvider);
        Resource loaded = readSet.getResource(fileUri("company.json"), true);

        EObject loadedAlice = loaded.getContents().get(1);
        EObject manager = EcoreUtil.resolve((EObject) loadedAlice.eGet(managerRef, false), readSet);

        assertFalse(manager.eIsProxy(), "same-document reference must resolve within the document");
        assertEquals("Employee-1", manager.eGet(nameAttribute),
                "the fragment path must address the very object inside the hierarchy");
        assertEquals(employeesOf(loaded.getContents().get(0)).get(1), manager,
                "must resolve to the very object in this resource, not a copy");
    }

    @Test
    @DisplayName("multi-valued references across files keep order and resolve individually [plain JSON]")
    void plainMultiValuedCrossResourceReferencesKeepOrderAndResolve() throws IOException {
        multiValuedCrossResourceReferencesKeepOrderAndResolve(false);
    }

    @Test
    @DisplayName("multi-valued references across files keep order and resolve individually [format delegate]")
    void delegateMultiValuedCrossResourceReferencesKeepOrderAndResolve() throws IOException {
        multiValuedCrossResourceReferencesKeepOrderAndResolve(true);
    }

    private void multiValuedCrossResourceReferencesKeepOrderAndResolve(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
        Resource personRes = writeSet.createResource(fileUri("person.json"));
        Resource companyRes = writeSet.createResource(fileUri("company.json"));

        EObject company = buildCompany();
        companyRes.getContents().add(company);

        // point into the other file, deliberately out of natural order
        EObject alice = createPerson("Alice");
        List<EObject> friends = friendsOf(alice);
        friends.add(employeesOf(company).get(2));
        friends.add(employeesOf(company).get(0));
        friends.add(employeesOf(company).get(1));
        personRes.getContents().add(alice);

        companyRes.save(Collections.emptyMap());
        personRes.save(Collections.emptyMap());

        ResourceSet readSet = newResourceSet(withFormatProvider);
        Resource loaded = readSet.getResource(fileUri("person.json"), true);
        List<EObject> loadedFriends = friendsOf(loaded.getContents().get(0));

        assertEquals(3, loadedFriends.size(), "every element must survive the round trip");
        assertEquals(List.of("Employee-2", "Employee-0", "Employee-1"),
                loadedFriends.stream()
                        .map(f -> EcoreUtil.resolve(f, readSet).eGet(nameAttribute))
                        .toList(),
                "order and identity of each element must be preserved");
    }

    @Test
    @DisplayName("mixed array of expanded and referenced elements resolves to three values [plain JSON]")
    void plainMixedArrayResolvesToThreeValues() throws IOException {
        mixedArrayResolvesToThreeValues(false);
    }

    @Test
    @DisplayName("mixed array of expanded and referenced elements resolves to three values [format delegate]")
    void delegateMixedArrayResolvesToThreeValues() throws IOException {
        mixedArrayResolvesToThreeValues(true);
    }

    /**
     * An array mixing expanded elements with a reference must keep all three positions, and
     * once the middle proxy resolves, every element carries a real value (issue #114).
     */
    private void mixedArrayResolvesToThreeValues(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
        Resource otherRes = writeSet.createResource(fileUri("other.json"));
        EObject target = createPerson("Target");
        otherRes.getContents().add(target);
        otherRes.save(Collections.emptyMap());

        // Build the mixed document by hand: expanded, referenced, expanded
        String refUri = EcoreUtil.getURI(target).deresolve(fileUri("person.json")).toString();
        String nsUri = testPackage.getNsURI();
        Files.writeString(tempDir.resolve("person.json"), """
                {
                  "_type": "%s#//Person",
                  "name": "Alice",
                  "friends": [
                    { "_type": "%s#//Person", "name": "Bob" },
                    { "_type": "%s#//Person", "$ref": "%s" },
                    { "_type": "%s#//Person", "name": "Charlie" }
                  ]
                }
                """.formatted(nsUri, nsUri, nsUri, refUri, nsUri));

        ResourceSet readSet = newResourceSet(withFormatProvider);
        Resource loaded = readSet.getResource(fileUri("person.json"), true);
        List<EObject> friends = friendsOf(loaded.getContents().get(0));

        assertEquals(3, friends.size(), "no element may be lost");
        assertEquals(List.of("Bob", "Target", "Charlie"),
                friends.stream()
                        .map(f -> EcoreUtil.resolve(f, readSet).eGet(nameAttribute))
                        .toList(),
                "every element must carry its own value once the proxy resolves");
    }

    @Test
    @DisplayName("multi-valued cross-document containment comes back as a proxy [plain JSON]")
    void plainMultiValuedCrossDocumentContainment() throws IOException {
        multiValuedCrossDocumentContainment(false);
    }

    @Test
    @DisplayName("multi-valued cross-document containment comes back as a proxy [format delegate]")
    void delegateMultiValuedCrossDocumentContainment() throws IOException {
        multiValuedCrossDocumentContainment(true);
    }

    /**
     * The #123 proxy branch only covered single-valued containment; an element of a
     * containment <b>list</b> living in another resource still became an empty object
     * (issue #128).
     */
    private void multiValuedCrossDocumentContainment(boolean withFormatProvider) throws IOException {
        ResourceSet writeSet = newResourceSet(withFormatProvider);
        Resource companyRes = writeSet.createResource(fileUri("company.json"));
        Resource externalRes = writeSet.createResource(fileUri("external.json"));

        EObject company = testPackage.getEFactoryInstance().create(companyClass);
        company.eSet(companyNameAttribute, "ACME");

        EObject local = createPerson("Local");
        EObject external = createPerson("External");
        employeesOf(company).add(local);
        employeesOf(company).add(external);
        companyRes.getContents().add(company);
        // the second employee stays contained but lives in its own resource
        externalRes.getContents().add(external);

        externalRes.save(Collections.emptyMap());
        companyRes.save(Collections.emptyMap());

        String json = Files.readString(tempDir.resolve("company.json"));
        assertTrue(json.contains("external.json"),
                "the cross-document element must reference its file, was: " + json);
        assertFalse(json.contains("\"External\""),
                "the cross-document element must not be inlined, was: " + json);

        ResourceSet readSet = newResourceSet(withFormatProvider);
        Resource loaded = readSet.getResource(fileUri("company.json"), true);

        // read unresolved: eGet with resolve=true would make EMF resolve the containment
        // proxy right away, hiding what the codec actually produced
        @SuppressWarnings("unchecked")
        List<EObject> raw = (List<EObject>) loaded.getContents().get(0).eGet(employeesRef, false);

        assertEquals(2, raw.size(), "both employees must survive");
        assertEquals("Local", raw.get(0).eGet(nameAttribute));

        // The element must carry the identity of the object in the other file - either as a
        // proxy pointing there, or already resolved when the target resource was reachable.
        // What it must never be is a new empty object, which is what issue #128 produced.
        EObject restored = EcoreUtil.resolve(raw.get(1), readSet);
        assertFalse(restored.eIsProxy(), "the cross-document element must be resolvable");
        assertEquals("External", restored.eGet(nameAttribute),
                "the element must be the object from the other file, not an empty one");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    /** Both write paths: plain JSON (no provider) and the FormatDelegate path. */
    private ResourceSet newResourceSet(boolean withFormatProvider) {
        ResourceSet rs = new ResourceSetImpl();
        Resource.Factory factory = withFormatProvider
                ? new CodecFormatResourceFactory(metadataService,
                        new JacksonFormatProvider("json", new JsonFactory()),
                        ConfigurationResolver.defaults())
                : new CodecResourceFactory(metadataService, ConfigurationResolver.defaults());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("json", factory);
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
    private List<EObject> friendsOf(EObject person) {
        return (List<EObject>) person.eGet(friendsRef);
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
