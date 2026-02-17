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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for SuperType serialization.
 *
 * @see <a href="docs/codec-v2-spec/05-supertype.md">Spec: SuperType Serialization</a>
 */
@DisplayName("SuperType Serialization Tests")
class CodecResourceSuperTypeTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-annotated.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // Classes
    private EClass customerClass;
    private EClass vipCustomerClass;

    // Attributes
    private EAttribute emailAttribute;
    private EAttribute firstNameAttribute;
    private EAttribute lastNameAttribute;
    private EAttribute vipLevelAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CodecResourceSuperTypeTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        customerClass = ecoreHelper.getEClass(testPackage, "Customer");
        vipCustomerClass = ecoreHelper.getEClass(testPackage, "VIPCustomer");

        emailAttribute = (EAttribute) ecoreHelper.getFeature(customerClass, "email");
        firstNameAttribute = (EAttribute) ecoreHelper.getFeature(customerClass, "firstName");
        lastNameAttribute = (EAttribute) ecoreHelper.getFeature(customerClass, "lastName");
        vipLevelAttribute = (EAttribute) ecoreHelper.getFeature(vipCustomerClass, "vipLevel");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createCustomer(String email, String firstName, String lastName) {
        EObject customer = testPackage.getEFactoryInstance().create(customerClass);
        customer.eSet(emailAttribute, email);
        customer.eSet(firstNameAttribute, firstName);
        customer.eSet(lastNameAttribute, lastName);
        return customer;
    }

    private EObject createVipCustomer(String email, String firstName, String lastName, int vipLevel) {
        EObject vip = testPackage.getEFactoryInstance().create(vipCustomerClass);
        vip.eSet(emailAttribute, email);
        vip.eSet(firstNameAttribute, firstName);
        vip.eSet(lastNameAttribute, lastName);
        vip.eSet(vipLevelAttribute, vipLevel);
        return vip;
    }

    // ========================================================================
    // Annotation-Based SuperType Tests
    // ========================================================================

    @Nested
    @DisplayName("Annotation-based supertype serialization")
    class AnnotationBasedSuperType {

        @Test
        @DisplayName("Customer class has supertype enabled via annotation")
        void customerHasSuperTypeEnabled() throws IOException {
            EObject customer = createCustomer("john@example.com", "John", "Doe");

            String json = serialize(customer);
            System.out.println("Customer JSON:\n" + json);

            assertFalse(json.contains("\"_extends\""),
                    "Customer has no supertypes, so _extends should not appear");
        }

        @Test
        @DisplayName("VIPCustomer serializes supertype from parent class")
        void vipCustomerSerializesSuperType() throws IOException {
            EObject vip = createVipCustomer("vip@example.com", "Jane", "VIP", 5);

            String json = serialize(vip);
            System.out.println("VIPCustomer JSON:\n" + json);

            assertTrue(json.contains("\"email\""), "JSON should contain email");
            assertTrue(json.contains("\"vipLevel\""), "JSON should contain vipLevel");
        }
    }

    // ========================================================================
    // Configuration-Based SuperType Tests
    // ========================================================================

    @Nested
    @DisplayName("Configuration-based supertype serialization")
    class ConfigurationBasedSuperType {

        @Test
        @DisplayName("supertype disabled by default")
        void superTypeDisabledByDefault() throws IOException {
            EObject vip = createVipCustomer("vip@example.com", "Jane", "VIP", 5);

            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            String json = serializeWithResolver(vip, resolver);
            System.out.println("VIPCustomer (default config) JSON:\n" + json);

            assertFalse(json.contains("\"_supertype\""),
                    "Supertype should not appear when disabled");
        }

        @Test
        @DisplayName("supertype enabled with ALL selection")
        void superTypeEnabledWithAllSelection() throws IOException {
            EObject vip = createVipCustomer("vip@example.com", "Jane", "VIP", 5);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of(
                            "superTypeSerialize", true,
                            "superTypeStrategy", "ALL"
                    ))
                    .build();

            String json = serializeWithResolver(vip, resolver);
            System.out.println("VIPCustomer (supertype ALL) JSON:\n" + json);

            assertTrue(json.contains("\"_supertype\""),
                    "Supertype should appear when enabled");
            assertTrue(json.contains("Customer"),
                    "Supertype array should contain Customer");
        }

        @Test
        @DisplayName("supertype enabled with SINGLE selection")
        void superTypeEnabledWithSingleSelection() throws IOException {
            EObject vip = createVipCustomer("vip@example.com", "Jane", "VIP", 5);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of(
                            "superTypeSerialize", true,
                            "superTypeStrategy", "SINGLE",
                            "superTypeAsArray", false
                    ))
                    .build();

            String json = serializeWithResolver(vip, resolver);
            System.out.println("VIPCustomer (supertype SINGLE) JSON:\n" + json);

            assertTrue(json.contains("\"_supertype\""),
                    "Supertype should appear when enabled");
            assertTrue(json.contains("\"_supertype\":\""),
                    "SINGLE selection should write supertype as string");
        }

        @Test
        @DisplayName("supertype with custom key")
        void superTypeWithCustomKey() throws IOException {
            EObject vip = createVipCustomer("vip@example.com", "Jane", "VIP", 5);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of(
                            "superTypeSerialize", true,
                            "superTypeStrategy", "ALL",
                            "superTypeKey", "extends"
                    ))
                    .build();

            String json = serializeWithResolver(vip, resolver);
            System.out.println("VIPCustomer (custom key) JSON:\n" + json);

            assertTrue(json.contains("\"extends\""),
                    "Should use custom supertype key");
            assertFalse(json.contains("\"_supertype\""),
                    "Should not use default _supertype key");
        }

        @Test
        @DisplayName("supertype not serialized for class without supertypes")
        void superTypeNotSerializedForRootClass() throws IOException {
            EObject customer = createCustomer("john@example.com", "John", "Doe");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .moduleProperties(Map.of(
                            "superTypeSerialize", true,
                            "superTypeStrategy", "ALL"
                    ))
                    .build();

            String json = serializeWithResolver(customer, resolver);
            System.out.println("Customer (no supertypes) JSON:\n" + json);

            assertFalse(json.contains("\"_supertype\""),
                    "Should not serialize _supertype for class without supertypes");
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object) throws IOException {
        return serializeWithResolver(object, ConfigurationResolver.defaults());
    }

    private String serializeWithResolver(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://supertype-test.json"),
                metadataService,
                resolver,
                null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());

        return out.toString(StandardCharsets.UTF_8);
    }
}
