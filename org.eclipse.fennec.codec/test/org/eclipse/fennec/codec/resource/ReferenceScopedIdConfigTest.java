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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Reference-scoped {@code idKey} and {@code idFormat} (issue #176, spec 09-id.md §4.4).
 * <p>
 * The two are documented as the only ID properties configurable at the feature level
 * (02-config-resolution.md §11.4, 16-annotation-reference.md), and they were the only ones the
 * annotation parser deliberately allowed on an EReference - and then nothing read them. They
 * rename the id key, and choose its format, for the objects written <b>through one reference</b>,
 * leaving the same class's id alone everywhere else.
 * </p>
 * <p>
 * Containment only. A non-containment reference writes a {@code _ref} or a bare URI, not the
 * target's body, so there is no id key in that position to rename; identifying a target by its
 * id instead of by URI is a different feature and not this one.
 * </p>
 */
@DisplayName("Reference-scoped id configuration")
class ReferenceScopedIdConfigTest {

    private static final String NS_URI = "urn:codec:refscoped:id:test";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass itemClass;
    private EClass orderClass;
    private EAttribute itemId;
    private EAttribute itemLabel;
    private EAttribute orderNo;
    private EReference items;
    private EReference archived;

    @BeforeEach
    void setUp() {
        EcoreFactory f = EcoreFactory.eINSTANCE;

        itemClass = f.createEClass();
        itemClass.setName("Item");
        itemId = f.createEAttribute();
        itemId.setName("id");
        itemId.setEType(EcorePackage.Literals.ESTRING);
        itemId.setID(true);
        itemClass.getEStructuralFeatures().add(itemId);
        itemLabel = f.createEAttribute();
        itemLabel.setName("label");
        itemLabel.setEType(EcorePackage.Literals.ESTRING);
        itemClass.getEStructuralFeatures().add(itemLabel);

        orderClass = f.createEClass();
        orderClass.setName("Order");
        orderNo = f.createEAttribute();
        orderNo.setName("orderNo");
        orderNo.setEType(EcorePackage.Literals.ESTRING);
        orderClass.getEStructuralFeatures().add(orderNo);

        items = f.createEReference();
        items.setName("items");
        items.setEType(itemClass);
        items.setContainment(true);
        items.setUpperBound(-1);
        orderClass.getEStructuralFeatures().add(items);

        archived = f.createEReference();
        archived.setName("archived");
        archived.setEType(itemClass);
        archived.setContainment(true);
        archived.setUpperBound(-1);
        orderClass.getEStructuralFeatures().add(archived);

        testPackage = f.createEPackage();
        testPackage.setName("refscopedid");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("refscopedid");
        testPackage.getEClassifiers().add(itemClass);
        testPackage.getEClassifiers().add(orderClass);

        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(testPackage);
        EPackage.Registry.INSTANCE.put(NS_URI, testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    @Test
    @DisplayName("a reference-scoped idKey renames the id of the objects it contains")
    void referenceScopedIdKeyRenamesTheContainedId() throws IOException {
        String json = save(featureScoped("items", Map.of("idKey", "itemId")));

        assertTrue(json.contains("\"itemId\":\"i1\""),
                "the item reached through 'items' carries the configured key, was: " + json);
    }

    @Test
    @DisplayName("a sibling reference to the same class keeps the class-level id key")
    void siblingReferenceToTheSameClassIsUnaffected() throws IOException {
        String json = save(featureScoped("items", Map.of("idKey", "itemId")));

        assertTrue(json.contains("\"_id\":\"i2\""),
                "'archived' was not configured, so its items keep _id, was: " + json);
    }

    @Test
    @DisplayName("a class-level idKey still applies to every reference")
    void classLevelIdKeyStillApplies() throws IOException {
        String json = save(Map.of("Item", Map.of("idKey", "itemId")));

        assertTrue(json.contains("\"itemId\":\"i1\"") && json.contains("\"itemId\":\"i2\""),
                "the class scope reaches both references, was: " + json);
        assertFalse(json.contains("\"_id\""), "nothing should be left on the default key");
    }

    @Test
    @DisplayName("a reference-scoped idFormat writes the id as an object")
    void referenceScopedIdFormatWritesStructured() throws IOException {
        String json = save(featureScoped("items", Map.of("idFormat", "STRUCTURED")));

        assertTrue(json.contains("\"_id\":{"),
                "STRUCTURED writes the id as a nested object, was: " + json);
        assertTrue(json.contains("\"_id\":\"i2\""),
                "'archived' keeps PLAIN, was: " + json);
    }

    @Test
    @DisplayName("a reference-scoped idKey reads back")
    @SuppressWarnings("unchecked")
    void referenceScopedIdKeyRoundTrips() throws IOException {
        Map<String, Object> options = featureScoped("items", Map.of("idKey", "itemId"));

        String json = save(options);
        EObject loaded = load(json, options);

        List<EObject> loadedItems = (List<EObject>) loaded.eGet(items);
        assertEquals(1, loadedItems.size());
        assertEquals("i1", loadedItems.get(0).eGet(itemId),
                "writing itemId and reading _id would lose the identity, was: " + json);
    }

    @Test
    @DisplayName("an idKey annotation on the reference reaches the wire")
    void idKeyFromAnnotation() throws IOException {
        annotate(items, "idKey", "itemId");

        String json = save(Map.of());

        assertTrue(json.contains("\"itemId\":\"i1\""),
                "the annotated reference renames the id, was: " + json);
        assertTrue(json.contains("\"_id\":\"i2\""),
                "the unannotated sibling keeps _id, was: " + json);
    }

    @Test
    @DisplayName("an idKey annotation reads back")
    @SuppressWarnings("unchecked")
    void idKeyFromAnnotationRoundTrips() throws IOException {
        annotate(items, "idKey", "itemId");

        EObject loaded = load(save(Map.of()), Map.of());

        List<EObject> loadedItems = (List<EObject>) loaded.eGet(items);
        assertEquals(1, loadedItems.size());
        assertEquals("i1", loadedItems.get(0).eGet(itemId));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void annotate(EReference reference, String... keyValuePairs) {
        org.eclipse.emf.ecore.EAnnotation annotation =
                EcoreFactory.eINSTANCE.createEAnnotation();
        annotation.setSource("http://eclipse.org/fennec/codec");
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            annotation.getDetails().put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        reference.getEAnnotations().add(annotation);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    private Map<String, Object> featureScoped(String featureName, Map<String, Object> props) {
        Map<String, Object> classScoped = new HashMap<>();
        classScoped.put(featureName, props);
        return Map.of("Order", classScoped);
    }

    @SuppressWarnings("unchecked")
    private String save(Map<String, Object> optionsProperties) throws IOException {
        EObject bolt = testPackage.getEFactoryInstance().create(itemClass);
        bolt.eSet(itemId, "i1");
        bolt.eSet(itemLabel, "Bolt");
        EObject nut = testPackage.getEFactoryInstance().create(itemClass);
        nut.eSet(itemId, "i2");
        nut.eSet(itemLabel, "Nut");

        EObject order = testPackage.getEFactoryInstance().create(orderClass);
        order.eSet(orderNo, "A1");
        ((List<EObject>) order.eGet(items)).add(bolt);
        ((List<EObject>) order.eGet(archived)).add(nut);

        CodecResource resource = newResource(optionsProperties);
        resource.getContents().add(order);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return out.toString(UTF_8);
    }

    private EObject load(String json, Map<String, Object> optionsProperties) throws IOException {
        CodecResource resource = newResource(optionsProperties);
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)),
                Map.of(CodecResource.CODEC_ROOT_TYPE, orderClass));
        return resource.getContents().get(0);
    }

    private CodecResource newResource(Map<String, Object> optionsProperties) {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(optionsProperties)
                .build();
        return new CodecResource(URI.createURI("refscopedid.json"), metadataService, resolver, null);
    }
}
