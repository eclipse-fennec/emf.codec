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
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Exploration test to understand how type strategies behave with containments.
 * <p>
 * This test uses an ecore model where:
 * <ul>
 *   <li>Company: has SCHEMA_AND_TYPE strategy</li>
 *   <li>Department: has URI strategy (explicit)</li>
 *   <li>Person: no annotation (uses default)</li>
 *   <li>Address: no annotation (uses default)</li>
 * </ul>
 * </p>
 * <p>
 * Questions to answer:
 * <ul>
 *   <li>When Company uses SCHEMA_AND_TYPE, what happens to contained Persons?</li>
 *   <li>Do contained objects inherit the parent's strategy?</li>
 *   <li>Do contained objects use their own configured/default strategy?</li>
 * </ul>
 * </p>
 */
@DisplayName("Type Strategy Exploration Tests")
class TypeStrategyExplorationTest {

    private static final String TEST_ECORE = "test-type-strategy.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass companyClass;
    private EClass departmentClass;
    private EClass personClass;
    private EClass addressClass;

    private EAttribute companyNameAttr;
    private EAttribute deptNameAttr;
    private EAttribute personNameAttr;
    private EAttribute streetAttr;
    private EAttribute cityAttr;

    private EReference employeesRef;
    private EReference membersRef;
    private EReference personAddressRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore("/org/eclipse/fennec/codec/resource/" + TEST_ECORE, TypeStrategyExplorationTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        companyClass = EcoreHelper.getEClass(testPackage, "Company");
        departmentClass = EcoreHelper.getEClass(testPackage, "Department");
        personClass = EcoreHelper.getEClass(testPackage, "Person");
        addressClass = EcoreHelper.getEClass(testPackage, "Address");

        companyNameAttr = (EAttribute) EcoreHelper.getFeature(companyClass, "name");
        deptNameAttr = (EAttribute) EcoreHelper.getFeature(departmentClass, "name");
        personNameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        streetAttr = (EAttribute) EcoreHelper.getFeature(addressClass, "street");
        cityAttr = (EAttribute) EcoreHelper.getFeature(addressClass, "city");

        employeesRef = (EReference) EcoreHelper.getFeature(companyClass, "employees");
        membersRef = (EReference) EcoreHelper.getFeature(departmentClass, "members");
        personAddressRef = (EReference) EcoreHelper.getFeature(personClass, "address");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private String serialize(EObject object, ConfigurationResolver config) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://type-strategy-test.json"),
                metadataService,
                config,
                null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toString(StandardCharsets.UTF_8);
    }

    @Nested
    @DisplayName("SCHEMA_AND_TYPE strategy on root")
    class SchemaAndTypeOnRoot {

        @Test
        @DisplayName("Company (SCHEMA_AND_TYPE) with no containments")
        void companyAlone() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme Inc");

            String json = serialize(company, ConfigurationResolver.defaults());

            System.out.println("=== Company (SCHEMA_AND_TYPE) alone ===");
            System.out.println(json);
            System.out.println();

            // Company should have _schema and _type as separate fields
            assertTrue(json.contains("\"_schema\""), "Company should have _schema field");
            assertTrue(json.contains("\"_type\""), "Company should have _type field");
        }

        @Test
        @DisplayName("Company (SCHEMA_AND_TYPE) with contained Person")
        void companyWithPerson() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme Inc");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person);

            String json = serialize(company, ConfigurationResolver.defaults());

            System.out.println("=== Company (SCHEMA_AND_TYPE) with Person ===");
            System.out.println(json);
            System.out.println();

            // Key question: What does Person's _type look like?
            // Does it use SCHEMA_AND_TYPE too? Or URI? Or something else?
        }

        @Test
        @DisplayName("Company (SCHEMA_AND_TYPE) with Person containing Address")
        void companyWithPersonAndAddress() throws IOException {
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme Inc");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Alice");

            EObject address = testPackage.getEFactoryInstance().create(addressClass);
            address.eSet(streetAttr, "123 Main St");
            address.eSet(cityAttr, "Springfield");
            person.eSet(personAddressRef, address);

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person);

            String json = serialize(company, ConfigurationResolver.defaults());

            System.out.println("=== Company (SCHEMA_AND_TYPE) with Person+Address ===");
            System.out.println(json);
            System.out.println();

            // Check: What does nested Address _type look like?
        }
    }

    @Nested
    @DisplayName("URI strategy on root (explicit)")
    class UriOnRoot {

        @Test
        @DisplayName("Department (URI) with contained Person")
        void departmentWithPerson() throws IOException {
            EObject dept = testPackage.getEFactoryInstance().create(departmentClass);
            dept.eSet(deptNameAttr, "Engineering");

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(personNameAttr, "Bob");

            @SuppressWarnings("unchecked")
            List<EObject> members = (List<EObject>) dept.eGet(membersRef);
            members.add(person);

            String json = serialize(dept, ConfigurationResolver.defaults());

            System.out.println("=== Department (URI) with Person ===");
            System.out.println(json);
            System.out.println();

            // Department and Person should both use full URIs
            assertTrue(json.contains(testPackage.getNsURI() + "#//Department"),
                    "Department should use full URI");
        }
    }

    @Nested
    @DisplayName("Comparison tests")
    class ComparisonTests {

        @Test
        @DisplayName("Compare Company vs Department with same structure")
        void compareStrategies() throws IOException {
            System.out.println("====================================================");
            System.out.println("COMPARING SCHEMA_AND_TYPE vs URI FOR SAME STRUCTURE");
            System.out.println("====================================================\n");

            // Company with SCHEMA_AND_TYPE
            EObject company = testPackage.getEFactoryInstance().create(companyClass);
            company.eSet(companyNameAttr, "Acme");

            EObject person1 = testPackage.getEFactoryInstance().create(personClass);
            person1.eSet(personNameAttr, "Alice");

            @SuppressWarnings("unchecked")
            List<EObject> employees = (List<EObject>) company.eGet(employeesRef);
            employees.add(person1);

            String companyJson = serialize(company, ConfigurationResolver.defaults());
            System.out.println("--- Company (SCHEMA_AND_TYPE strategy) ---");
            System.out.println(companyJson);
            System.out.println();

            // Department with URI
            EObject dept = testPackage.getEFactoryInstance().create(departmentClass);
            dept.eSet(deptNameAttr, "Acme");

            EObject person2 = testPackage.getEFactoryInstance().create(personClass);
            person2.eSet(personNameAttr, "Alice");

            @SuppressWarnings("unchecked")
            List<EObject> members = (List<EObject>) dept.eGet(membersRef);
            members.add(person2);

            String deptJson = serialize(dept, ConfigurationResolver.defaults());
            System.out.println("--- Department (URI strategy) ---");
            System.out.println(deptJson);
            System.out.println();

            // Analysis: How do the Person _type fields differ?
        }
    }
}
