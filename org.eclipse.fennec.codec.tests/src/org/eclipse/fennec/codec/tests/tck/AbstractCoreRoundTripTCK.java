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
package org.eclipse.fennec.codec.tests.tck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK base class for format provider round-trip tests.
 * <p>
 * Subclasses provide a {@link CodecFormatProvider} via {@link #createFormatProvider()}.
 * The suite verifies that the format can correctly round-trip all EMF feature types:
 * attributes (all data types), enums, multi-valued attributes, containment references,
 * non-containment references, and IDs.
 * <p>
 * This ensures any new format implementation has feature parity with the baseline
 * JSON format.
 */
public abstract class AbstractCoreRoundTripTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-format-parity.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass itemClass;
    private EClass detailClass;
    private EClass containerClass;

    // Item attributes
    private EAttribute idAttr;
    private EAttribute labelAttr;
    private EAttribute countAttr;
    private EAttribute amountAttr;
    private EAttribute ratioAttr;
    private EAttribute enabledAttr;
    private EAttribute priorityAttr;
    private EAttribute tagsAttr;

    // Item references
    private EReference detailRef;
    private EReference subItemsRef;
    private EReference linksRef;

    // Detail attributes
    private EAttribute detailNameAttr;
    private EAttribute detailTextAttr;

    // Container
    private EAttribute containerNameAttr;
    private EReference containerItemsRef;
    private EReference containerPrimaryRef;

    /**
     * Subclasses provide the format provider to test.
     */
    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    /**
     * Returns the file extension for URIs (e.g., "json", "cbor").
     */
    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // EClasses
        itemClass = EcoreHelper.getEClass(testPackage, "Item");
        detailClass = EcoreHelper.getEClass(testPackage, "Detail");
        containerClass = EcoreHelper.getEClass(testPackage, "Container");

        // Item attributes
        idAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "id");
        labelAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "label");
        countAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "count");
        amountAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "amount");
        ratioAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "ratio");
        enabledAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "enabled");
        priorityAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "priority");
        tagsAttr = (EAttribute) EcoreHelper.getFeature(itemClass, "tags");

        // Item references
        detailRef = (EReference) EcoreHelper.getFeature(itemClass, "detail");
        subItemsRef = (EReference) EcoreHelper.getFeature(itemClass, "subItems");
        linksRef = (EReference) EcoreHelper.getFeature(itemClass, "links");

        // Detail attributes
        detailNameAttr = (EAttribute) EcoreHelper.getFeature(detailClass, "detailName");
        detailTextAttr = (EAttribute) EcoreHelper.getFeature(detailClass, "detailText");

        // Container
        containerNameAttr = (EAttribute) EcoreHelper.getFeature(containerClass, "name");
        containerItemsRef = (EReference) EcoreHelper.getFeature(containerClass, "items");
        containerPrimaryRef = (EReference) EcoreHelper.getFeature(containerClass, "primary");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Factory helpers
    // ========================================================================

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
                URI.createURI("test://parity." + getFileExtension()),
                metadataService,
                ConfigurationResolver.defaults(),
                null, null, createFormatProvider());
    }

    // ========================================================================
    // Tests
    // ========================================================================

    @Nested
    @DisplayName("Attribute types")
    class AttributeTypes {

        @Test
        @DisplayName("round-trips string attribute")
        void stringAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(labelAttr, "Hello World");

            EObject loaded = roundTrip(item, itemClass);
            assertEquals("Hello World", loaded.eGet(labelAttr));
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
        @DisplayName("round-trips boolean true")
        void booleanTrue() throws IOException {
            EObject item = createItem();
            item.eSet(enabledAttr, true);

            EObject loaded = roundTrip(item, itemClass);
            assertEquals(true, loaded.eGet(enabledAttr));
        }

        @Test
        @DisplayName("round-trips boolean false")
        void booleanFalse() throws IOException {
            EObject item = createItem();
            item.eSet(enabledAttr, false);

            EObject loaded = roundTrip(item, itemClass);
            assertEquals(false, loaded.eGet(enabledAttr));
        }

        @Test
        @DisplayName("round-trips ID attribute")
        void idAttribute() throws IOException {
            EObject item = createItem();
            item.eSet(idAttr, "item-001");

            EObject loaded = roundTrip(item, itemClass);
            assertEquals("item-001", loaded.eGet(idAttr));
        }
    }

    @Nested
    @DisplayName("Enum attributes")
    class EnumAttributes {

        @Test
        @DisplayName("round-trips enum LOW")
        void enumLow() throws IOException {
            EObject item = createItem();
            item.eSet(priorityAttr, EcoreUtil.createFromString(priorityAttr.getEAttributeType(), "LOW"));

            EObject loaded = roundTrip(item, itemClass);
            assertNotNull(loaded.eGet(priorityAttr));
            assertEquals("LOW", loaded.eGet(priorityAttr).toString());
        }

        @Test
        @DisplayName("round-trips enum HIGH")
        void enumHigh() throws IOException {
            EObject item = createItem();
            item.eSet(priorityAttr, EcoreUtil.createFromString(priorityAttr.getEAttributeType(), "HIGH"));

            EObject loaded = roundTrip(item, itemClass);
            assertNotNull(loaded.eGet(priorityAttr));
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
            tags.add("gamma");

            EObject loaded = roundTrip(item, itemClass);
            List<String> loadedTags = (List<String>) loaded.eGet(tagsAttr);
            assertEquals(3, loadedTags.size());
            assertEquals("alpha", loadedTags.get(0));
            assertEquals("beta", loadedTags.get(1));
            assertEquals("gamma", loadedTags.get(2));
        }

        @Test
        @DisplayName("round-trips empty list")
        @SuppressWarnings("unchecked")
        void emptyList() throws IOException {
            EObject item = createItem();
            item.eSet(labelAttr, "no-tags");

            EObject loaded = roundTrip(item, itemClass);
            List<String> loadedTags = (List<String>) loaded.eGet(tagsAttr);
            assertTrue(loadedTags.isEmpty());
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
            assertEquals("parent", loaded.eGet(labelAttr));

            EObject loadedDetail = (EObject) loaded.eGet(detailRef);
            assertNotNull(loadedDetail);
            assertEquals("color", loadedDetail.eGet(detailNameAttr));
            assertEquals("blue", loadedDetail.eGet(detailTextAttr));
        }

        @Test
        @DisplayName("round-trips multi containment")
        @SuppressWarnings("unchecked")
        void multiContainment() throws IOException {
            EObject parent = createItem("parent-1", "Parent");
            EObject child1 = createItem("child-1", "Child A");
            EObject child2 = createItem("child-2", "Child B");

            List<EObject> subItems = (List<EObject>) parent.eGet(subItemsRef);
            subItems.add(child1);
            subItems.add(child2);

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
        @DisplayName("round-trips single non-containment reference")
        @SuppressWarnings("unchecked")
        void singleNonContainment() throws IOException {
            EObject container = createContainer("Test");
            EObject itemA = createItem("a", "Item A");
            EObject itemB = createItem("b", "Item B");

            List<EObject> items = (List<EObject>) container.eGet(containerItemsRef);
            items.add(itemA);
            items.add(itemB);

            container.eSet(containerPrimaryRef, itemA);

            EObject loaded = roundTrip(container, containerClass);
            assertNotNull(loaded);

            List<EObject> loadedItems = (List<EObject>) loaded.eGet(containerItemsRef);
            assertEquals(2, loadedItems.size());

            EObject loadedPrimary = (EObject) loaded.eGet(containerPrimaryRef);
            assertNotNull(loadedPrimary, "Primary reference should be resolved");
            assertEquals("Item A", loadedPrimary.eGet(labelAttr));
            assertSame(loadedItems.get(0), loadedPrimary,
                    "Primary should be same instance as first contained item");
        }

        @Test
        @DisplayName("round-trips multi non-containment reference")
        @SuppressWarnings("unchecked")
        void multiNonContainment() throws IOException {
            EObject container = createContainer("Links Test");
            EObject itemA = createItem("a", "A");
            EObject itemB = createItem("b", "B");
            EObject itemC = createItem("c", "C");

            List<EObject> items = (List<EObject>) container.eGet(containerItemsRef);
            items.add(itemA);
            items.add(itemB);
            items.add(itemC);

            List<EObject> aLinks = (List<EObject>) itemA.eGet(linksRef);
            aLinks.add(itemB);
            aLinks.add(itemC);

            EObject loaded = roundTrip(container, containerClass);
            List<EObject> loadedItems = (List<EObject>) loaded.eGet(containerItemsRef);
            assertEquals(3, loadedItems.size());

            EObject loadedA = loadedItems.get(0);
            EObject loadedB = loadedItems.get(1);
            EObject loadedC = loadedItems.get(2);

            List<EObject> loadedLinks = (List<EObject>) loadedA.eGet(linksRef);
            assertEquals(2, loadedLinks.size(), "A should have 2 links");
            assertSame(loadedB, loadedLinks.get(0), "First link should be B");
            assertSame(loadedC, loadedLinks.get(1), "Second link should be C");
        }
    }

    @Nested
    @DisplayName("Complex round-trip")
    class ComplexRoundTrip {

        @Test
        @DisplayName("round-trips complex object graph")
        @SuppressWarnings("unchecked")
        void complexGraph() throws IOException {
            EObject container = createContainer("Full Test");

            EObject item1 = createItem("item-1", "First");
            item1.eSet(countAttr, 10);
            item1.eSet(ratioAttr, 1.5);
            item1.eSet(enabledAttr, true);
            item1.eSet(priorityAttr, EcoreUtil.createFromString(priorityAttr.getEAttributeType(), "HIGH"));
            item1.eSet(detailRef, createDetail("status", "active"));

            List<String> tags1 = (List<String>) item1.eGet(tagsAttr);
            tags1.add("important");
            tags1.add("urgent");

            EObject item2 = createItem("item-2", "Second");
            item2.eSet(countAttr, 20);
            item2.eSet(enabledAttr, false);
            item2.eSet(priorityAttr, EcoreUtil.createFromString(priorityAttr.getEAttributeType(), "LOW"));

            List<EObject> items = (List<EObject>) container.eGet(containerItemsRef);
            items.add(item1);
            items.add(item2);

            container.eSet(containerPrimaryRef, item1);

            List<EObject> item1Links = (List<EObject>) item1.eGet(linksRef);
            item1Links.add(item2);

            EObject loaded = roundTrip(container, containerClass);
            assertNotNull(loaded);
            assertEquals("Full Test", loaded.eGet(containerNameAttr));

            List<EObject> loadedItems = (List<EObject>) loaded.eGet(containerItemsRef);
            assertEquals(2, loadedItems.size());

            EObject l1 = loadedItems.get(0);
            assertEquals("First", l1.eGet(labelAttr));
            assertEquals(10, l1.eGet(countAttr));
            assertEquals(1.5, (Double) l1.eGet(ratioAttr), 0.001);
            assertEquals(true, l1.eGet(enabledAttr));
            assertEquals("HIGH", l1.eGet(priorityAttr).toString());

            EObject l1Detail = (EObject) l1.eGet(detailRef);
            assertNotNull(l1Detail);
            assertEquals("status", l1Detail.eGet(detailNameAttr));
            assertEquals("active", l1Detail.eGet(detailTextAttr));

            List<String> l1Tags = (List<String>) l1.eGet(tagsAttr);
            assertEquals(2, l1Tags.size());
            assertEquals("important", l1Tags.get(0));

            EObject l2 = loadedItems.get(1);
            assertEquals("Second", l2.eGet(labelAttr));
            assertEquals(20, l2.eGet(countAttr));
            assertEquals(false, l2.eGet(enabledAttr));
            assertEquals("LOW", l2.eGet(priorityAttr).toString());

            EObject loadedPrimary = (EObject) loaded.eGet(containerPrimaryRef);
            assertSame(l1, loadedPrimary);

            List<EObject> l1Links = (List<EObject>) l1.eGet(linksRef);
            assertEquals(1, l1Links.size());
            assertSame(l2, l1Links.get(0));
        }
    }

    // ========================================================================
    // Helper Methods
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
        assertNoDiagnostics(resource);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    /**
     * Fails when a round trip reported problems (issue #131).
     * <p>
     * Deserialization catches, logs and continues, so a load succeeds even when a value was
     * dropped. The diagnostics are the only trace - a test that ignores them cannot tell a
     * clean round trip from a lossy one.
     * </p>
     */
    private static void assertNoDiagnostics(CodecResource resource) {
        assertTrue(resource.getErrors().isEmpty(),
                "round trip reported errors: " + resource.getErrors());
        assertTrue(resource.getWarnings().isEmpty(),
                "round trip reported warnings: " + resource.getWarnings());
    }
}
