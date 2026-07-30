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
package org.eclipse.fennec.codec.osgi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * OSGi integration tests for basic round-trip serialization.
 * <p>
 * Mirrors {@code org.eclipse.fennec.codec.examples.BasicRoundTripExample} but
 * uses OSGi service injection instead of manual wiring:
 * <ul>
 *   <li>The {@link MetadataService} is injected automatically by DS (via
 *       {@code MetadataServiceComponent}).</li>
 *   <li>The test's {@link EPackage} is registered as an OSGi service in
 *       {@code setUp}, so the {@code MetadataServiceComponent} whiteboard picks
 *       it up and applies the {@code CodecAspectProviderComponent} automatically
 *       — no manual {@code registerPackage()} call is needed.</li>
 * </ul>
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Serialization</a>
 * @see <a href="docs/codec-v2-spec/09-id.md">Spec: ID Serialization</a>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("Basic Round-Trip OSGi Examples")
public class BasicRoundTripExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-basic.ecore";

    @InjectService(filter = ("(emf.fileExtension=json)"))
    ResourceSet resourceSet;  

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;

    private EClass personClass;
    private EClass addressClass;
    private EClass teamClass;
    private EEnum priorityEnum;

    private EAttribute personId;
    private EAttribute nameAttr;
    private EAttribute ageAttr;
    private EAttribute salaryAttr;
    private EAttribute ratingAttr;
    private EAttribute activeAttr;
    private EAttribute priorityAttr;
    private EAttribute tagsAttr;
    private EReference addressRef;
    private EReference friendsRef;

    private EAttribute teamNameAttr;
    private EReference membersRef;
    private EReference leadRef;

    @BeforeEach
    public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, BasicRoundTripExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        // Registering as OSGi service triggers MetadataServiceComponent.addEPackage()
        // which applies CodecAspectProviderComponent automatically.
        pkgReg = ctx.registerService(EPackage.class, pkg, null);

        personClass = EcoreHelper.getEClass(pkg, "Person");
        addressClass = EcoreHelper.getEClass(pkg, "Address");
        teamClass = EcoreHelper.getEClass(pkg, "Team");
        priorityEnum = (EEnum) pkg.getEClassifier("Priority");

        personId = (EAttribute) EcoreHelper.getFeature(personClass, "personId");
        nameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttr = (EAttribute) EcoreHelper.getFeature(personClass, "age");
        salaryAttr = (EAttribute) EcoreHelper.getFeature(personClass, "salary");
        ratingAttr = (EAttribute) EcoreHelper.getFeature(personClass, "rating");
        activeAttr = (EAttribute) EcoreHelper.getFeature(personClass, "active");
        priorityAttr = (EAttribute) EcoreHelper.getFeature(personClass, "priority");
        tagsAttr = (EAttribute) EcoreHelper.getFeature(personClass, "tags");
        addressRef = (EReference) EcoreHelper.getFeature(personClass, "address");
        friendsRef = (EReference) EcoreHelper.getFeature(personClass, "friends");

        teamNameAttr = (EAttribute) EcoreHelper.getFeature(teamClass, "name");
        membersRef = (EReference) EcoreHelper.getFeature(teamClass, "members");
        leadRef = (EReference) EcoreHelper.getFeature(teamClass, "lead");
    }

    @AfterEach
    public void tearDown() {
        pkgReg.unregister();
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Round-trip helper
    // ========================================================================

    private EObject roundTrip(EObject object, EClass rootType) throws IOException {
        Resource saveResource = resourceSet.createResource(URI.createURI("example.json"));
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);

        Resource loadResource = resourceSet.createResource(URI.createURI("example.json"));
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    private EObject createPerson(String id, String name) {
        EObject p = pkg.getEFactoryInstance().create(personClass);
        p.eSet(personId, id);
        p.eSet(nameAttr, name);
        return p;
    }

    // ========================================================================
    // Tests
    // ========================================================================

    @Test
    @DisplayName("String attribute round-trip")
    void stringAttribute() throws IOException {
        EObject person = createPerson("p1", "Alice");

        EObject loaded = roundTrip(person, personClass);

        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
    }

    @Test
    @DisplayName("Int attribute round-trip")
    void intAttribute() throws IOException {
        EObject person = createPerson("p1", "Bob");
        person.eSet(ageAttr, 30);

        EObject loaded = roundTrip(person, personClass);

        assertEquals(30, loaded.eGet(ageAttr));
    }

    @Test
    @DisplayName("Long attribute round-trip")
    void longAttribute() throws IOException {
        EObject person = createPerson("p1", "Carol");
        person.eSet(salaryAttr, 75000L);

        EObject loaded = roundTrip(person, personClass);

        assertEquals(75000L, loaded.eGet(salaryAttr));
    }

    @Test
    @DisplayName("Double attribute round-trip")
    void doubleAttribute() throws IOException {
        EObject person = createPerson("p1", "Dave");
        person.eSet(ratingAttr, 4.7);

        EObject loaded = roundTrip(person, personClass);

        assertEquals(4.7, (Double) loaded.eGet(ratingAttr), 0.001);
    }

    @Test
    @DisplayName("Boolean attribute round-trip")
    void booleanAttribute() throws IOException {
        EObject person = createPerson("p1", "Eve");
        person.eSet(activeAttr, true);

        EObject loaded = roundTrip(person, personClass);

        assertEquals(true, loaded.eGet(activeAttr));
    }

    @Test
    @DisplayName("Enum attribute round-trip")
    void enumAttribute() throws IOException {
        EObject person = createPerson("p1", "Frank");
        person.eSet(priorityAttr, priorityEnum.getEEnumLiteral("HIGH").getInstance());

        EObject loaded = roundTrip(person, personClass);

        assertEquals(priorityEnum.getEEnumLiteral("HIGH").getInstance(), loaded.eGet(priorityAttr));
    }

    @Test
    @DisplayName("Multi-valued attribute round-trip")
    @SuppressWarnings("unchecked")
    void multiValuedAttribute() throws IOException {
        EObject person = createPerson("p1", "Grace");
        List<String> tags = (List<String>) person.eGet(tagsAttr);
        tags.add("java");
        tags.add("emf");
        tags.add("codec");

        EObject loaded = roundTrip(person, personClass);

        List<String> loadedTags = (List<String>) loaded.eGet(tagsAttr);
        assertEquals(3, loadedTags.size());
        assertEquals("java", loadedTags.get(0));
        assertEquals("emf", loadedTags.get(1));
        assertEquals("codec", loadedTags.get(2));
    }

    @Test
    @DisplayName("Single containment reference round-trip")
    void singleContainment() throws IOException {
        EObject person = createPerson("p1", "Hank");
        EObject address = pkg.getEFactoryInstance().create(addressClass);
        address.eSet(addressClass.getEStructuralFeature("street"), "123 Main St");
        address.eSet(addressClass.getEStructuralFeature("city"), "Springfield");
        address.eSet(addressClass.getEStructuralFeature("zip"), "62704");
        person.eSet(addressRef, address);

        EObject loaded = roundTrip(person, personClass);

        EObject loadedAddr = (EObject) loaded.eGet(addressRef);
        assertNotNull(loadedAddr);
        assertEquals("123 Main St", loadedAddr.eGet(addressClass.getEStructuralFeature("street")));
        assertEquals("Springfield", loadedAddr.eGet(addressClass.getEStructuralFeature("city")));
        assertEquals("62704", loadedAddr.eGet(addressClass.getEStructuralFeature("zip")));
    }

    @Test
    @DisplayName("Multi containment reference round-trip")
    @SuppressWarnings("unchecked")
    void multiContainment() throws IOException {
        EObject team = pkg.getEFactoryInstance().create(teamClass);
        team.eSet(teamNameAttr, "Engineering");

        EObject alice = createPerson("p1", "Alice");
        EObject bob = createPerson("p2", "Bob");
        List<EObject> members = (List<EObject>) team.eGet(membersRef);
        members.add(alice);
        members.add(bob);

        EObject loaded = roundTrip(team, teamClass);

        assertEquals("Engineering", loaded.eGet(teamNameAttr));
        List<EObject> loadedMembers = (List<EObject>) loaded.eGet(membersRef);
        assertEquals(2, loadedMembers.size());
        assertEquals("Alice", loadedMembers.get(0).eGet(nameAttr));
        assertEquals("Bob", loadedMembers.get(1).eGet(nameAttr));
    }

    @Test
    @DisplayName("Non-containment reference round-trip")
    @SuppressWarnings("unchecked")
    void nonContainmentReference() throws IOException {
        EObject team = pkg.getEFactoryInstance().create(teamClass);
        team.eSet(teamNameAttr, "Engineering");

        EObject alice = createPerson("p1", "Alice");
        EObject bob = createPerson("p2", "Bob");
        List<EObject> members = (List<EObject>) team.eGet(membersRef);
        members.add(alice);
        members.add(bob);
        team.eSet(leadRef, alice);

        EObject loaded = roundTrip(team, teamClass);

        List<EObject> loadedMembers = (List<EObject>) loaded.eGet(membersRef);
        EObject loadedLead = (EObject) loaded.eGet(leadRef);
        assertNotNull(loadedLead, "Lead reference should be resolved");
        assertEquals("Alice", loadedLead.eGet(nameAttr));
        assertSame(loadedMembers.get(0), loadedLead, "Lead should be same instance as first member");
    }

    @Test
    @DisplayName("Complex graph with all attribute types round-trip")
    @SuppressWarnings("unchecked")
    void complexGraph() throws IOException {
        EObject team = pkg.getEFactoryInstance().create(teamClass);
        team.eSet(teamNameAttr, "Dream Team");

        EObject alice = createPerson("p1", "Alice");
        alice.eSet(ageAttr, 28);
        alice.eSet(salaryAttr, 90000L);
        alice.eSet(ratingAttr, 4.9);
        alice.eSet(activeAttr, true);
        alice.eSet(priorityAttr, priorityEnum.getEEnumLiteral("HIGH").getInstance());
        List<String> aliceTags = (List<String>) alice.eGet(tagsAttr);
        aliceTags.add("lead");
        aliceTags.add("architect");

        EObject addr = pkg.getEFactoryInstance().create(addressClass);
        addr.eSet(addressClass.getEStructuralFeature("street"), "456 Oak Ave");
        addr.eSet(addressClass.getEStructuralFeature("city"), "Metropolis");
        addr.eSet(addressClass.getEStructuralFeature("zip"), "10001");
        alice.eSet(addressRef, addr);

        EObject bob = createPerson("p2", "Bob");
        bob.eSet(ageAttr, 32);

        List<EObject> members = (List<EObject>) team.eGet(membersRef);
        members.add(alice);
        members.add(bob);
        team.eSet(leadRef, alice);

        List<EObject> friends = (List<EObject>) alice.eGet(friendsRef);
        friends.add(bob);

        EObject loaded = roundTrip(team, teamClass);

        assertNotNull(loaded);
        assertEquals("Dream Team", loaded.eGet(teamNameAttr));

        List<EObject> loadedMembers = (List<EObject>) loaded.eGet(membersRef);
        assertEquals(2, loadedMembers.size());

        EObject loadedAlice = loadedMembers.get(0);
        assertEquals("Alice", loadedAlice.eGet(nameAttr));
        assertEquals(28, loadedAlice.eGet(ageAttr));
        assertEquals(90000L, loadedAlice.eGet(salaryAttr));
        assertEquals(4.9, (Double) loadedAlice.eGet(ratingAttr), 0.001);
        assertEquals(true, loadedAlice.eGet(activeAttr));

        EObject loadedAddr = (EObject) loadedAlice.eGet(addressRef);
        assertNotNull(loadedAddr);
        assertEquals("456 Oak Ave", loadedAddr.eGet(addressClass.getEStructuralFeature("street")));

        List<EObject> loadedFriends = (List<EObject>) loadedAlice.eGet(friendsRef);
        assertEquals(1, loadedFriends.size());
        assertSame(loadedMembers.get(1), loadedFriends.get(0));

        assertSame(loadedAlice, loaded.eGet(leadRef));
    }
}
