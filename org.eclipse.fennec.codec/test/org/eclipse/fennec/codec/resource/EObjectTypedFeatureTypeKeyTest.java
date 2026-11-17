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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * An {@code EObject}-typed containment is typed by the {@code _type} in the data alone,
 * without a {@code CODEC_FEATURE_TYPE_HINTS} entry (issue #328, spec 13 §6.1).
 */
@DisplayName("EObject-typed feature typed by _type without hint (#328)")
class EObjectTypedFeatureTypeKeyTest {

    private static final String TEST_ECORE = "test-feature-type-hints.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass exampleClass;
    private EClass containerClass;
    private EClass addressClass;
    private EClass personClass;
    private EReference exampleValueRef;
    private EReference containerItemsRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore("/org/eclipse/fennec/codec/resource/" + TEST_ECORE,
                EObjectTypedFeatureTypeKeyTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        exampleClass = EcoreHelper.getEClass(testPackage, "Example");
        containerClass = EcoreHelper.getEClass(testPackage, "Container");
        addressClass = EcoreHelper.getEClass(testPackage, "Address");
        personClass = EcoreHelper.getEClass(testPackage, "Person");
        exampleValueRef = (EReference) EcoreHelper.getFeature(exampleClass, "value");
        containerItemsRef = (EReference) EcoreHelper.getFeature(containerClass, "items");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("single-valued: _type as simple name, no hint")
    void singleValuedSimpleName() throws IOException {
        CodecResource resource = load("""
                {
                  "summary": "Typed by the data alone",
                  "value": { "_type": "Address", "street": "Main Street", "city": "Springfield" }
                }
                """, exampleClass);

        EObject value = (EObject) root(resource).eGet(exampleValueRef);
        assertNotNull(value, "value must be read from _type: " + resource.getWarnings());
        assertEquals(addressClass, value.eClass());
        assertEquals("Main Street", value.eGet(addressClass.getEStructuralFeature("street")));
        assertEquals("Springfield", value.eGet(addressClass.getEStructuralFeature("city")));
        assertTrue(resource.getWarnings().isEmpty(), "unexpected warnings: " + resource.getWarnings());
    }

    @Test
    @DisplayName("single-valued: _type as full URI, after the data fields, no hint")
    void singleValuedUriAfterData() throws IOException {
        CodecResource resource = load("""
                {
                  "value": { "street": "Main Street", "_type": "%s#//Address" }
                }
                """.formatted(testPackage.getNsURI()), exampleClass);

        EObject value = (EObject) root(resource).eGet(exampleValueRef);
        assertNotNull(value, "value must be read from _type: " + resource.getWarnings());
        assertEquals(addressClass, value.eClass());
        assertEquals("Main Street", value.eGet(addressClass.getEStructuralFeature("street")));
    }

    @Test
    @DisplayName("multi-valued: each element typed by its own _type, no hint")
    void multiValued() throws IOException {
        CodecResource resource = load("""
                {
                  "name": "c",
                  "items": [
                    { "_type": "Address", "city": "Springfield" },
                    { "_type": "Person", "name": "Homer", "age": 39 }
                  ]
                }
                """, containerClass);

        @SuppressWarnings("unchecked")
        List<EObject> items = (List<EObject>) root(resource).eGet(containerItemsRef);
        assertEquals(2, items.size(), "both elements must be read: " + resource.getWarnings());
        assertEquals(addressClass, items.get(0).eClass());
        assertEquals(personClass, items.get(1).eClass());
        assertEquals(39, items.get(1).eGet(personClass.getEStructuralFeature("age")));
    }

    @Test
    @DisplayName("without _type the feature is still skipped with a warning")
    void withoutTypeStillSkipped() throws IOException {
        CodecResource resource = load("""
                {
                  "summary": "s",
                  "value": { "street": "Main Street" },
                  "description": "after"
                }
                """, exampleClass);

        EObject example = root(resource);
        assertNull(example.eGet(exampleValueRef));
        assertEquals("after", example.eGet(exampleClass.getEStructuralFeature("description")));
        assertFalse(resource.getWarnings().isEmpty(), "the skip must be reported");
        assertTrue(resource.getWarnings().toString().contains("'value'"),
                "the warning must name the feature: " + resource.getWarnings());
    }

    @Test
    @DisplayName("an unresolvable _type does not produce a bare EObject")
    void unresolvableTypeGivesNoBareEObject() throws IOException {
        CodecResource resource = load("""
                {
                  "value": { "_type": "NoSuchClass", "street": "Main Street" },
                  "description": "after"
                }
                """, exampleClass);

        EObject example = root(resource);
        assertNull(example.eGet(exampleValueRef));
        assertEquals("after", example.eGet(exampleClass.getEStructuralFeature("description")));
        assertFalse(resource.getWarnings().isEmpty() && resource.getErrors().isEmpty(),
                "the unresolvable type must be reported");
    }

    private CodecResource load(String json, EClass rootType) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://eobject-type-key.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource;
    }

    private static EObject root(CodecResource resource) {
        assertFalse(resource.getContents().isEmpty(), "root must be read");
        return resource.getContents().get(0);
    }
}
