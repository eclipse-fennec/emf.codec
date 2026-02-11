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
package org.eclipse.fennec.codec.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for URI-based type resolution.
 * <p>
 * Tests that _type field with full EClass URI resolves correctly
 * for root objects and nested objects.
 * </p>
 */
@DisplayName("Type Resolution: URI Strategy")
class TypeResolutionUriTest {

    private static final String TEST_ECORE = "test-uri.ecore";

    private MetadataWhiteboard metadataService;
    private EcoreHelper ecoreHelper;

    private EPackage testPackage;
    private EClass personClass;
    private EClass addressClass;
    private EClass personWithAddressClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(TypeResolutionUriTest.class);
        testPackage = ecoreHelper.loadEcore(TEST_ECORE);

        // Register in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service with CodecAspectProvider
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = ecoreHelper.getEClass(testPackage, "Person");
        addressClass = ecoreHelper.getEClass(testPackage, "Address");
        personWithAddressClass = ecoreHelper.getEClass(testPackage, "PersonWithAddress");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("root object with _type URI resolves correctly")
    void rootObjectWithTypeUri() throws IOException {
        String json = """
            {
                "_type": "http://test.example.org/uri/1.0#//Person",
                "name": "John"
            }
            """;

        EObject result = deserialize(json);

        assertNotNull(result, "Should deserialize with URI type");
        assertEquals(personClass, result.eClass());
        assertEquals("John", result.eGet(personClass.getEStructuralFeature("name")));
    }

    @Test
    @DisplayName("nested object with _type URI resolves correctly")
    void nestedObjectWithTypeUri() throws IOException {
        String json = """
            {
                "_type": "http://test.example.org/uri/1.0#//PersonWithAddress",
                "name": "Jane",
                "address": {
                    "_type": "http://test.example.org/uri/1.0#//Address",
                    "city": "Springfield"
                }
            }
            """;

        EObject result = deserialize(json);

        assertNotNull(result, "Should deserialize container");
        assertEquals(personWithAddressClass, result.eClass());
        assertEquals("Jane", result.eGet(personWithAddressClass.getEStructuralFeature("name")));

        EObject address = (EObject) result.eGet(personWithAddressClass.getEStructuralFeature("address"));
        assertNotNull(address, "Should have nested address");
        assertEquals(addressClass, address.eClass());
        assertEquals("Springfield", address.eGet(addressClass.getEStructuralFeature("city")));
    }

    @Test
    @DisplayName("invalid URI returns null")
    void invalidUriReturnsNull() throws IOException {
        String json = """
            {
                "_type": "http://nonexistent.example.org/1.0#//Unknown",
                "name": "Ghost"
            }
            """;

        EObject result = deserialize(json);

        assertNull(result, "Should return null for unresolvable URI");
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://uri-test.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    private EObject deserialize(String json) throws IOException {
        CodecResource resource = createResource();
        Map<String, Object> options = new HashMap<>();

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
