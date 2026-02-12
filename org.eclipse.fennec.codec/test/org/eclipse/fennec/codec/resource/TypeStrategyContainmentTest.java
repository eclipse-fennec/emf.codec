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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Investigative test to clarify how type strategies apply to containments.
 * <p>
 * Questions to answer:
 * <ul>
 *   <li>Does SCHEMA_AND_TYPE apply only to root objects or to all objects?</li>
 *   <li>Do contained objects use the same strategy as root?</li>
 *   <li>What should the expected behavior be?</li>
 * </ul>
 * </p>
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.TypeStrategyContainmentTest}
 * </p>
 */
@DisplayName("Type Strategy Containment Behavior Test")
class TypeStrategyContainmentTest {

    private static final String TEST_ECORE = "test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EClass addressClass;
    private EClass companyClass;
    private EAttribute nameAttribute;
    private EReference addressRef;
    private EReference employeesRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(TypeStrategyContainmentTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute("/org/eclipse/fennec/codec/resource/" + TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = ecoreHelper.getEClass(testPackage, "Person");
        addressClass = ecoreHelper.getEClass(testPackage, "Address");
        companyClass = ecoreHelper.getEClass(testPackage, "Company");
        nameAttribute = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        addressRef = (EReference) ecoreHelper.getFeature(personClass, "address");
        employeesRef = (EReference) ecoreHelper.getFeature(companyClass, "employees");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private String serialize(EObject object, ConfigurationResolver config) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://strategy-test.json"),
                metadataService,
                config,
                null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Default URI strategy: what does _type look like for root and containments?")
    void defaultUriStrategy_rootAndContainments() throws IOException {
        // Create Company with Person containment
        // With default strategy (URI), we expect full URIs for all objects
        EObject company = testPackage.getEFactoryInstance().create(companyClass);
        company.eSet(companyClass.getEStructuralFeature("name"), "Acme");

        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttribute, "Alice");

        @SuppressWarnings("unchecked")
        List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
        employees.add(person);

        // Use defaults - should use URI strategy
        ConfigurationResolver config = ConfigurationResolver.defaults();

        String json = serialize(company, config);
        System.out.println("=== Default (URI Strategy) ===");
        System.out.println(json);
        System.out.println();

        // Both root and contained should use full URIs
        assertTrue(json.contains("\"_type\""), "Should have _type field");
        // Verify full URIs are used
        assertTrue(json.contains(testPackage.getNsURI()), "Should contain package nsURI for type");
    }

    @Test
    @DisplayName("Person with Address containment - default strategy")
    void personWithAddress_defaultStrategy() throws IOException {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttribute, "Alice");

        EObject address = testPackage.getEFactoryInstance().create(addressClass);
        address.eSet(addressClass.getEStructuralFeature("street"), "123 Main St");
        address.eSet(addressClass.getEStructuralFeature("city"), "Springfield");
        person.eSet(addressRef, address);

        ConfigurationResolver config = ConfigurationResolver.defaults();

        String json = serialize(person, config);
        System.out.println("=== Person with Address - Default Strategy ===");
        System.out.println(json);
        System.out.println();

        // Count _type occurrences - should be 2 (root Person + contained Address)
        int typeCount = countOccurrences(json, "\"_type\"");
        assertEquals(2, typeCount, "Both Person and Address should have _type");
    }

    @Test
    @DisplayName("Verify type strategy applies consistently to all objects")
    void verifyTypeStrategyAppliesGlobally() throws IOException {
        // Create Company with multiple Persons
        EObject company = testPackage.getEFactoryInstance().create(companyClass);
        company.eSet(companyClass.getEStructuralFeature("name"), "Acme");

        EObject person1 = testPackage.getEFactoryInstance().create(personClass);
        person1.eSet(nameAttribute, "Alice");

        EObject person2 = testPackage.getEFactoryInstance().create(personClass);
        person2.eSet(nameAttribute, "Bob");

        @SuppressWarnings("unchecked")
        List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
        employees.add(person1);
        employees.add(person2);

        ConfigurationResolver config = ConfigurationResolver.defaults();

        String json = serialize(company, config);
        System.out.println("=== Company with 2 Employees - Type Strategy Check ===");
        System.out.println(json);
        System.out.println();

        // Count _type occurrences - should be 3 (Company + Person1 + Person2)
        int typeCount = countOccurrences(json, "\"_type\"");
        assertEquals(3, typeCount, "Company and both Persons should have _type");

        // All _type values should be full URIs
        int uriCount = countOccurrences(json, testPackage.getNsURI() + "#//");
        assertEquals(3, uriCount, "All _type values should use full URI format");
    }

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }
}
