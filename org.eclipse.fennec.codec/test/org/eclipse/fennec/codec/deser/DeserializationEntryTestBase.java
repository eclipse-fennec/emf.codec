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
package org.eclipse.fennec.codec.deser;

import java.io.IOException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import tools.jackson.core.JsonParser;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;

/**
 * Base class for deserialization entry tests.
 * <p>
 * Provides common setup for loading the test ecore model and accessing
 * EClasses, EAttributes, and EReferences for testing deserialization entries.
 * Uses real EMF objects and real JSON parsing for more realistic testing.
 * </p>
 */
public abstract class DeserializationEntryTestBase {

    protected static final String TEST_ECORE = "test-deserialization.ecore";

    protected EcoreHelper ecoreHelper;
    protected EPackage testPackage;

    // EClasses
    protected EClass personClass;
    protected EClass addressClass;
    protected EClass multiIdPersonClass;
    protected EClass intIdEntityClass;
    protected EClass longIdEntityClass;
    protected EClass orderLineClass;
    protected EClass derivedKeyPersonClass;

    // EAttributes on Person
    protected EAttribute idAttribute;
    protected EAttribute nameAttribute;
    protected EAttribute ageAttribute;
    protected EAttribute activeAttribute;
    protected EAttribute scoreAttribute;
    protected EAttribute tagsAttribute;
    protected EAttribute metadataAttribute;  // EJavaObject type

    // EAttributes on MultiIdPerson (for combined ID tests)
    protected EAttribute firstNameAttribute;
    protected EAttribute lastNameAttribute;
    protected EAttribute emailAttribute;

    // EAttributes on IntIdEntity / LongIdEntity (for type conversion tests)
    protected EAttribute intIdAttribute;
    protected EAttribute longIdAttribute;

    // EAttributes on OrderLine (eID attribute is itself an id feature)
    protected EAttribute orderIdAttribute;
    protected EAttribute lineNoAttribute;

    // EAttributes on DerivedKeyPerson (separate derived eID key)
    protected EAttribute derivedKeyAttribute;
    protected EAttribute derivedFirstNameAttribute;
    protected EAttribute derivedLastNameAttribute;

    // EReferences on Person
    protected EReference addressRef;       // containment, single
    protected EReference managerRef;       // non-containment, single
    protected EReference colleaguesRef;    // non-containment, multi

    // Jackson objects for real parsing
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUpBase() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, DeserializationEntryTestBase.class);

        // Load EClasses
        personClass = EcoreHelper.getEClass(testPackage, "Person");
        addressClass = EcoreHelper.getEClass(testPackage, "Address");
        multiIdPersonClass = EcoreHelper.getEClass(testPackage, "MultiIdPerson");
        intIdEntityClass = EcoreHelper.getEClass(testPackage, "IntIdEntity");
        longIdEntityClass = EcoreHelper.getEClass(testPackage, "LongIdEntity");
        orderLineClass = EcoreHelper.getEClass(testPackage, "OrderLine");
        derivedKeyPersonClass = EcoreHelper.getEClass(testPackage, "DerivedKeyPerson");

        // Load EAttributes on Person
        idAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "id");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "age");
        activeAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "active");
        scoreAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "score");
        tagsAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "tags");
        metadataAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "metadata");

        // Load EAttributes on MultiIdPerson
        firstNameAttribute = (EAttribute) EcoreHelper.getFeature(multiIdPersonClass, "firstName");
        lastNameAttribute = (EAttribute) EcoreHelper.getFeature(multiIdPersonClass, "lastName");
        emailAttribute = (EAttribute) EcoreHelper.getFeature(multiIdPersonClass, "email");

        // Load EAttributes on IntIdEntity / LongIdEntity
        intIdAttribute = (EAttribute) EcoreHelper.getFeature(intIdEntityClass, "id");
        longIdAttribute = (EAttribute) EcoreHelper.getFeature(longIdEntityClass, "id");

        // Load EAttributes on OrderLine / DerivedKeyPerson
        orderIdAttribute = (EAttribute) EcoreHelper.getFeature(orderLineClass, "orderId");
        lineNoAttribute = (EAttribute) EcoreHelper.getFeature(orderLineClass, "lineNo");
        derivedKeyAttribute = (EAttribute) EcoreHelper.getFeature(derivedKeyPersonClass, "key");
        derivedFirstNameAttribute = (EAttribute) EcoreHelper.getFeature(derivedKeyPersonClass, "firstName");
        derivedLastNameAttribute = (EAttribute) EcoreHelper.getFeature(derivedKeyPersonClass, "lastName");

        // Load EReferences
        addressRef = (EReference) EcoreHelper.getFeature(personClass, "address");
        managerRef = (EReference) EcoreHelper.getFeature(personClass, "manager");
        colleaguesRef = (EReference) EcoreHelper.getFeature(personClass, "colleagues");

        // Create real Jackson objects
        objectMapper = new ObjectMapper(new JsonFactory());
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
     * Creates a new MultiIdPerson EObject instance.
     *
     * @return a new MultiIdPerson instance
     */
    protected EObject createMultiIdPerson() {
        return testPackage.getEFactoryInstance().create(multiIdPersonClass);
    }

    /**
     * Creates a new IntIdEntity EObject instance.
     *
     * @return a new IntIdEntity instance
     */
    protected EObject createIntIdEntity() {
        return testPackage.getEFactoryInstance().create(intIdEntityClass);
    }

    /**
     * Creates a new LongIdEntity EObject instance.
     *
     * @return a new LongIdEntity instance
     */
    protected EObject createLongIdEntity() {
        return testPackage.getEFactoryInstance().create(longIdEntityClass);
    }

    /**
     * Creates a new OrderLine EObject instance.
     *
     * @return a new OrderLine instance
     */
    protected EObject createOrderLine() {
        return testPackage.getEFactoryInstance().create(orderLineClass);
    }

    /**
     * Creates a new DerivedKeyPerson EObject instance.
     *
     * @return a new DerivedKeyPerson instance
     */
    protected EObject createDerivedKeyPerson() {
        return testPackage.getEFactoryInstance().create(derivedKeyPersonClass);
    }

    /**
     * Creates a DeserializationState for the given EClass.
     *
     * @param eClass the EClass for the state
     * @return a new DeserializationState with the EClass set
     */
    protected DeserializationState createState(EClass eClass) {
        DeserializationState state = new DeserializationState(null);
        state.setResolvedEClass(eClass);
        return state;
    }

    /**
     * Creates a DeserializationState with an EObject already created.
     *
     * @param eObject the EObject
     * @return a new DeserializationState with the EObject set
     */
    protected DeserializationState createStateWithObject(EObject eObject) {
        DeserializationState state = new DeserializationState(null);
        state.setResolvedEClass(eObject.eClass());
        state.setEObject(eObject);
        return state;
    }

    /**
     * Creates a JsonParser from a JSON string and advances to the first value token.
     * <p>
     * For simple values like "hello", 42, true, etc., it parses the value directly.
     * The parser is positioned at the value token after this call.
     * </p>
     *
     * @param json the JSON string to parse
     * @return a JsonParser positioned at the first value
     */
    protected JsonParser createParser(String json) {
        try {
            JsonParser parser = objectMapper.createParser(json);
            parser.nextToken(); // Advance to first token
            return parser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create parser for: " + json, e);
        }
    }

}
