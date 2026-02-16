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
package org.eclipse.fennec.codec.cbor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Round-trip tests for {@link CborFormatProvider} through {@link CodecResource}.
 */
@DisplayName("CborFormatProvider")
class CborFormatProviderTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/cbor/test-format-parity.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass itemClass;
    private EClass detailClass;
    private EClass containerClass;

    private EAttribute idAttr;
    private EAttribute labelAttr;
    private EAttribute countAttr;
    private EAttribute amountAttr;
    private EAttribute ratioAttr;
    private EAttribute enabledAttr;
    private EAttribute priorityAttr;
    private EAttribute tagsAttr;

    private EReference detailRef;
    private EReference subItemsRef;
    private EReference linksRef;

    private EAttribute detailNameAttr;
    private EAttribute detailTextAttr;

    private EAttribute containerNameAttr;
    private EReference containerItemsRef;
    private EReference containerPrimaryRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(CborFormatProviderTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        itemClass = ecoreHelper.getEClass(testPackage, "Item");
        detailClass = ecoreHelper.getEClass(testPackage, "Detail");
        containerClass = ecoreHelper.getEClass(testPackage, "Container");

        idAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "id");
        labelAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "label");
        countAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "count");
        amountAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "amount");
        ratioAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "ratio");
        enabledAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "enabled");
        priorityAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "priority");
        tagsAttr = (EAttribute) ecoreHelper.getFeature(itemClass, "tags");

        detailRef = (EReference) ecoreHelper.getFeature(itemClass, "detail");
        subItemsRef = (EReference) ecoreHelper.getFeature(itemClass, "subItems");
        linksRef = (EReference) ecoreHelper.getFeature(itemClass, "links");

        detailNameAttr = (EAttribute) ecoreHelper.getFeature(detailClass, "detailName");
        detailTextAttr = (EAttribute) ecoreHelper.getFeature(detailClass, "detailText");

        containerNameAttr = (EAttribute) ecoreHelper.getFeature(containerClass, "name");
        containerItemsRef = (EReference) ecoreHelper.getFeature(containerClass, "items");
        containerPrimaryRef = (EReference) ecoreHelper.getFeature(containerClass, "primary");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createItem() {
        return testPackage.getEFactoryInstance().create(itemClass);
    }

    private EObject createItem(String id, String label) {
        EObject item = createItem();
        item.eSet(idAttr, id);
        item.eSet(labelAttr, label);
        return item;
    }

    private EObject createDetail(String name, String text) {
        EObject detail = testPackage.getEFactoryInstance().create(detailClass);
        detail.eSet(detailNameAttr, name);
        detail.eSet(detailTextAttr, text);
        return detail;
    }

    private EObject createContainer(String name) {
        EObject container = testPackage.getEFactoryInstance().create(containerClass);
        container.eSet(containerNameAttr, name);
        return container;
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://parity.cbor"),
                metadataService,
                ConfigurationResolver.defaults(),
                null, null, new CborFormatProvider());
    }

    @Nested
    @DisplayName("Attribute types")
    class AttributeTypes {

        @Test
        @DisplayName("round-trips string attribute")
        void stringAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(labelAttr, "Hello CBOR");
            EObject loaded = roundTrip(item, itemClass);
            assertEquals("Hello CBOR", loaded.eGet(labelAttr));
        }

        @Test
        @DisplayName("round-trips int attribute")
        void intAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(countAttr, 42);
            EObject loaded = roundTrip(item, itemClass);
            assertEquals(42, loaded.eGet(countAttr));
        }

        @Test
        @DisplayName("round-trips long attribute")
        void longAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(amountAttr, 9876543210L);
            EObject loaded = roundTrip(item, itemClass);
            assertEquals(9876543210L, loaded.eGet(amountAttr));
        }

        @Test
        @DisplayName("round-trips double attribute")
        void doubleAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(ratioAttr, 3.14159);
            EObject loaded = roundTrip(item, itemClass);
            assertEquals(3.14159, (Double) loaded.eGet(ratioAttr), 0.00001);
        }

        @Test
        @DisplayName("round-trips boolean")
        void booleanAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(enabledAttr, true);
            EObject loaded = roundTrip(item, itemClass);
            assertEquals(true, loaded.eGet(enabledAttr));
        }

        @Test
        @DisplayName("round-trips ID attribute")
        void idAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(idAttr, "cbor-001");
            EObject loaded = roundTrip(item, itemClass);
            assertEquals("cbor-001", loaded.eGet(idAttr));
        }
    }

    @Nested
    @DisplayName("Enum attributes")
    class EnumAttributes {

        @Test
        @DisplayName("round-trips enum")
        void enumAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(priorityAttr, EcoreUtil.createFromString(priorityAttr.getEAttributeType(), "HIGH"));
            EObject loaded = roundTrip(item, itemClass);
            assertEquals("HIGH", loaded.eGet(priorityAttr).toString());
        }
    }

    @Nested
    @DisplayName("Multi-valued attributes")
    class MultiValuedAttributes {

        @Test
        @DisplayName("round-trips string list")
        @SuppressWarnings("unchecked")
        void stringList() throws IOException {
            EObject item = createItem();
            List<String> tags = (List<String>) item.eGet(tagsAttr);
            tags.add("alpha");
            tags.add("beta");
            EObject loaded = roundTrip(item, itemClass);
            List<String> loadedTags = (List<String>) loaded.eGet(tagsAttr);
            assertEquals(2, loadedTags.size());
            assertEquals("alpha", loadedTags.get(0));
            assertEquals("beta", loadedTags.get(1));
        }
    }

    @Nested
    @DisplayName("Containment references")
    class ContainmentReferences {

        @Test
        @DisplayName("round-trips single containment")
        void singleContainment() throws IOException {
            EObject item = createItem();
            item.eSet(labelAttr, "parent");
            item.eSet(detailRef, createDetail("color", "blue"));
            EObject loaded = roundTrip(item, itemClass);
            EObject loadedDetail = (EObject) loaded.eGet(detailRef);
            assertNotNull(loadedDetail);
            assertEquals("color", loadedDetail.eGet(detailNameAttr));
            assertEquals("blue", loadedDetail.eGet(detailTextAttr));
        }

        @Test
        @DisplayName("round-trips multi containment")
        @SuppressWarnings("unchecked")
        void multiContainment() throws IOException {
            EObject parent = createItem("p", "Parent");
            List<EObject> subs = (List<EObject>) parent.eGet(subItemsRef);
            subs.add(createItem("c1", "Child A"));
            subs.add(createItem("c2", "Child B"));
            EObject loaded = roundTrip(parent, itemClass);
            List<EObject> loadedSubs = (List<EObject>) loaded.eGet(subItemsRef);
            assertEquals(2, loadedSubs.size());
            assertEquals("Child A", loadedSubs.get(0).eGet(labelAttr));
            assertEquals("Child B", loadedSubs.get(1).eGet(labelAttr));
        }
    }

    @Nested
    @DisplayName("Non-containment references")
    class NonContainmentReferences {

        @Test
        @DisplayName("round-trips non-containment reference")
        @SuppressWarnings("unchecked")
        void nonContainment() throws IOException {
            EObject container = createContainer("Test");
            EObject itemA = createItem("a", "Item A");
            EObject itemB = createItem("b", "Item B");
            List<EObject> items = (List<EObject>) container.eGet(containerItemsRef);
            items.add(itemA);
            items.add(itemB);
            container.eSet(containerPrimaryRef, itemA);

            EObject loaded = roundTrip(container, containerClass);
            List<EObject> loadedItems = (List<EObject>) loaded.eGet(containerItemsRef);
            assertEquals(2, loadedItems.size());
            EObject loadedPrimary = (EObject) loaded.eGet(containerPrimaryRef);
            assertNotNull(loadedPrimary);
            assertSame(loadedItems.get(0), loadedPrimary);
        }
    }

    @Nested
    @DisplayName("Complex round-trip")
    class ComplexRoundTrip {

        @Test
        @DisplayName("round-trips complex object graph")
        @SuppressWarnings("unchecked")
        void complexGraph() throws IOException {
            EObject container = createContainer("Full CBOR");
            EObject item1 = createItem("i1", "First");
            item1.eSet(countAttr, 10);
            item1.eSet(ratioAttr, 1.5);
            item1.eSet(enabledAttr, true);
            item1.eSet(priorityAttr, EcoreUtil.createFromString(priorityAttr.getEAttributeType(), "HIGH"));
            item1.eSet(detailRef, createDetail("status", "active"));
            List<String> tags = (List<String>) item1.eGet(tagsAttr);
            tags.add("important");

            EObject item2 = createItem("i2", "Second");
            item2.eSet(countAttr, 20);

            List<EObject> items = (List<EObject>) container.eGet(containerItemsRef);
            items.add(item1);
            items.add(item2);
            container.eSet(containerPrimaryRef, item1);

            List<EObject> links = (List<EObject>) item1.eGet(linksRef);
            links.add(item2);

            EObject loaded = roundTrip(container, containerClass);
            assertEquals("Full CBOR", loaded.eGet(containerNameAttr));

            List<EObject> loadedItems = (List<EObject>) loaded.eGet(containerItemsRef);
            assertEquals(2, loadedItems.size());

            EObject l1 = loadedItems.get(0);
            assertEquals("First", l1.eGet(labelAttr));
            assertEquals(10, l1.eGet(countAttr));
            assertEquals("HIGH", l1.eGet(priorityAttr).toString());

            EObject l1Detail = (EObject) l1.eGet(detailRef);
            assertNotNull(l1Detail);
            assertEquals("status", l1Detail.eGet(detailNameAttr));

            assertSame(l1, loaded.eGet(containerPrimaryRef));

            List<EObject> l1Links = (List<EObject>) l1.eGet(linksRef);
            assertEquals(1, l1Links.size());
            assertSame(loadedItems.get(1), l1Links.get(0));
        }
    }

    @Nested
    @DisplayName("Provider metadata")
    class ProviderMetadata {

        @Test
        @DisplayName("returns correct format ID")
        void formatId() {
            assertEquals("cbor", new CborFormatProvider().getFormatId());
        }

        @Test
        @DisplayName("returns correct file extensions")
        void fileExtensions() {
            assertArrayEquals(new String[] { "cbor" }, new CborFormatProvider().getFileExtensions());
        }

        @Test
        @DisplayName("returns correct content types")
        void contentTypes() {
            assertArrayEquals(new String[] { "application/cbor" }, new CborFormatProvider().getContentTypes());
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject roundTrip(EObject object, EClass rootEClass) throws IOException {
        byte[] serialized = serialize(object);
        return deserialize(serialized, rootEClass);
    }

    private byte[] serialize(EObject object) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toByteArray();
    }

    private EObject deserialize(byte[] data, EClass rootEClass) throws IOException {
        CodecResource resource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        resource.load(in, options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
