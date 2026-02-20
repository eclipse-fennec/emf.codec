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

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Base class for serialization entry tests.
 * <p>
 * Provides common setup for loading the test ecore model and accessing
 * EClasses, EAttributes, and EReferences for testing serialization entries.
 * Uses real EMF objects instead of mocks for more realistic testing.
 * </p>
 */
abstract class SerializationEntryTestBase {

    protected static final String TEST_ECORE = "test-serialization.ecore";

    protected EcoreHelper ecoreHelper;
    protected EPackage testPackage;

    // EClasses
    protected EClass personClass;
    protected EClass addressClass;
    protected EClass namedElementClass;
    protected EClass companyClass;

    // EAttributes on Person
    protected EAttribute idAttribute;
    protected EAttribute nameAttribute;
    protected EAttribute ageAttribute;
    protected EAttribute activeAttribute;
    protected EAttribute scoreAttribute;
    protected EAttribute tagsAttribute;
    protected EAttribute fullNameAttribute; // derived

    // EReferences on Person
    protected EReference addressRef;       // containment, single
    protected EReference friendsRef;       // containment, multi
    protected EReference managerRef;       // non-containment, single
    protected EReference colleaguesRef;    // non-containment, multi

    // Mock objects for Jackson
    protected JsonGenerator generator;
    protected SerializationContext serializationContext;

    @BeforeEach
    void setUpBase() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, SerializationEntryTestBase.class);

        // Load EClasses
        personClass = EcoreHelper.getEClass(testPackage, "Person");
        addressClass = EcoreHelper.getEClass(testPackage, "Address");
        namedElementClass = EcoreHelper.getEClass(testPackage, "NamedElement");
        companyClass = EcoreHelper.getEClass(testPackage, "Company");

        // Load EAttributes
        idAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "id");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "age");
        activeAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "active");
        scoreAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "score");
        tagsAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "tags");
        fullNameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "fullName");

        // Load EReferences
        addressRef = (EReference) EcoreHelper.getFeature(personClass, "address");
        friendsRef = (EReference) EcoreHelper.getFeature(personClass, "friends");
        managerRef = (EReference) EcoreHelper.getFeature(personClass, "manager");
        colleaguesRef = (EReference) EcoreHelper.getFeature(personClass, "colleagues");

        // Create mock Jackson objects
        generator = mock(JsonGenerator.class);
        serializationContext = mock(SerializationContext.class);
    }

    @AfterEach
    void tearDownBase() {
        ecoreHelper.releaseAll();
    }

    /**
     * Creates a new Person EObject instance.
     *
     * @return a new Person instance
     */
    protected EObject createPerson() {
        return testPackage.getEFactoryInstance().create(personClass);
    }

    /**
     * Creates a new Person EObject with the given name.
     *
     * @param name the person's name
     * @return a new Person instance with the name set
     */
    protected EObject createPerson(String name) {
        EObject person = createPerson();
        person.eSet(nameAttribute, name);
        return person;
    }

    /**
     * Creates a new Address EObject instance.
     *
     * @return a new Address instance
     */
    protected EObject createAddress() {
        return testPackage.getEFactoryInstance().create(addressClass);
    }

    /**
     * Creates a new Address EObject with the given street and city.
     *
     * @param street the street address
     * @param city the city
     * @return a new Address instance with the values set
     */
    protected EObject createAddress(String street, String city) {
        EObject address = createAddress();
        address.eSet(addressClass.getEStructuralFeature("street"), street);
        address.eSet(addressClass.getEStructuralFeature("city"), city);
        return address;
    }

    /**
     * Creates a new Company EObject instance.
     *
     * @return a new Company instance
     */
    protected EObject createCompany() {
        return testPackage.getEFactoryInstance().create(companyClass);
    }

    /**
     * Creates a SerializationState for the given EObject.
     *
     * @param eObject the EObject to wrap
     * @return a new SerializationState
     */
    protected SerializationState createState(EObject eObject) {
        return new SerializationState(eObject);
    }
}
