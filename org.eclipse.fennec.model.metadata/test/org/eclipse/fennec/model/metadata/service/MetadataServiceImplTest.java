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
package org.eclipse.fennec.model.metadata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.metadata.AttributeMetadata;
import org.eclipse.fennec.model.metadata.ClassAspect;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureAspect;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.PackageAspect;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.PackageProfile;
import org.eclipse.fennec.model.metadata.ReferenceMetadata;
import org.eclipse.fennec.model.metadata.api.AspectProvider;
import org.eclipse.fennec.model.metadata.api.MetadataHandler;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.impl.ClassAspectImpl;
import org.eclipse.fennec.model.metadata.impl.FeatureAspectImpl;
import org.eclipse.fennec.model.metadata.impl.PackageAspectImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MetadataServiceImpl}.
 */
class MetadataServiceImplTest {

    private MetadataWhiteboard service;
    private EPackage testPackage;
    private EClass personClass;
    private EClass addressClass;
    private EAttribute nameAttr;
    private EAttribute idAttr;
    private EReference addressRef;
    private EReference personRef;

    @BeforeEach
    void setUp() {
        service = new MetadataServiceImpl();
        createTestPackage();
    }

    private void createTestPackage() {
        // Create test package
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("test");
        testPackage.setNsURI("http://test.example.org/1.0");
        testPackage.setNsPrefix("test");

        // Create Person class
        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        testPackage.getEClassifiers().add(personClass);

        // Create ID attribute
        idAttr = EcoreFactory.eINSTANCE.createEAttribute();
        idAttr.setName("id");
        idAttr.setEType(EcorePackage.Literals.ESTRING);
        idAttr.setID(true);
        personClass.getEStructuralFeatures().add(idAttr);

        // Create name attribute
        nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(nameAttr);

        // Create Address class
        addressClass = EcoreFactory.eINSTANCE.createEClass();
        addressClass.setName("Address");
        testPackage.getEClassifiers().add(addressClass);

        // Create bidirectional references
        addressRef = EcoreFactory.eINSTANCE.createEReference();
        addressRef.setName("address");
        addressRef.setEType(addressClass);
        addressRef.setContainment(true);
        personClass.getEStructuralFeatures().add(addressRef);

        personRef = EcoreFactory.eINSTANCE.createEReference();
        personRef.setName("person");
        personRef.setEType(personClass);
        addressClass.getEStructuralFeatures().add(personRef);

        // Set opposites
        addressRef.setEOpposite(personRef);
        personRef.setEOpposite(addressRef);
    }

    // ========================================================================
    // Package Registration Tests
    // ========================================================================

    @Test
    void testRegisterPackage() {
        PackageMetadata pkgMetadata = service.registerPackage(testPackage);

        assertNotNull(pkgMetadata);
        assertEquals("http://test.example.org/1.0", pkgMetadata.getNsURI());
        assertEquals(testPackage, pkgMetadata.getEPackage());
        assertEquals(2, pkgMetadata.getClasses().size());
    }

    @Test
    void testRegisterPackageNull() {
        PackageMetadata pkgMetadata = service.registerPackage(null);
        assertNull(pkgMetadata);
    }

    @Test
    void testRegisterPackageTwice() {
        PackageMetadata first = service.registerPackage(testPackage);
        PackageMetadata second = service.registerPackage(testPackage);

        assertSame(first, second, "Registering same package twice should return same metadata");
    }

    @Test
    void testUnregisterPackage() {
        service.registerPackage(testPackage);
        assertNotNull(service.getPackageMetadata("http://test.example.org/1.0"));

        service.unregisterPackage(testPackage);
        assertNull(service.getPackageMetadata("http://test.example.org/1.0"));
    }

    // ========================================================================
    // Package Lookup Tests
    // ========================================================================

    @Test
    void testGetPackageMetadata() {
        service.registerPackage(testPackage);

        PackageMetadata found = service.getPackageMetadata("http://test.example.org/1.0");
        assertNotNull(found);
        assertEquals(testPackage, found.getEPackage());
    }

    @Test
    void testGetPackageMetadataNotFound() {
        PackageMetadata found = service.getPackageMetadata("http://nonexistent.org/1.0");
        assertNull(found);
    }

    // ========================================================================
    // Class Metadata Tests
    // ========================================================================

    @Test
    void testClassMetadataCreation() {
        service.registerPackage(testPackage);

        ClassMetadata personMeta = service.getClassMetadata(personClass);
        assertNotNull(personMeta);
        assertEquals("Person", personMeta.getName());
        assertEquals(personClass, personMeta.getEClass());
        assertTrue(personMeta.isHasId());
        assertEquals(3, personMeta.getFeatures().size()); // id, name, address
    }

    @Test
    void testClassMetadataIdFeatures() {
        service.registerPackage(testPackage);

        ClassMetadata personMeta = service.getClassMetadata(personClass);
        assertEquals(1, personMeta.getIdFeatures().size());

        FeatureMetadata idFeatureMeta = personMeta.getIdFeatures().get(0);
        assertEquals("id", idFeatureMeta.getName());
        assertTrue(idFeatureMeta instanceof AttributeMetadata);
        assertTrue(((AttributeMetadata) idFeatureMeta).isIsId());
    }

    @Test
    void testGetClassMetadataByURI() {
        service.registerPackage(testPackage);

        ClassMetadata personMeta = service.getClassMetadata(personClass);
        String uri = personMeta.getTypeURI();

        ClassMetadata found = service.getClassMetadataByURI(uri);
        assertSame(personMeta, found);
    }

    @Test
    void testGetClassMetadataByName() {
        service.registerPackage(testPackage);

        ClassMetadata found = service.getClassMetadataByName("Person", "http://test.example.org/1.0");
        assertNotNull(found);
        assertEquals("Person", found.getName());
    }

    @Test
    void testGetClassMetadataByNameNotFound() {
        service.registerPackage(testPackage);

        ClassMetadata found = service.getClassMetadataByName("NonExistent", "http://test.example.org/1.0");
        assertNull(found);
    }

    // ========================================================================
    // Feature Metadata Tests
    // ========================================================================

    @Test
    void testAttributeMetadataCreation() {
        service.registerPackage(testPackage);

        FeatureMetadata nameMeta = service.getFeatureMetadata(nameAttr);
        assertNotNull(nameMeta);
        assertTrue(nameMeta instanceof AttributeMetadata);
        assertEquals("name", nameMeta.getName());
        assertEquals(nameAttr, nameMeta.getEFeature());
    }

    @Test
    void testReferenceMetadataCreation() {
        service.registerPackage(testPackage);

        FeatureMetadata addressMeta = service.getFeatureMetadata(addressRef);
        assertNotNull(addressMeta);
        assertTrue(addressMeta instanceof ReferenceMetadata);
        assertEquals("address", addressMeta.getName());

        ReferenceMetadata refMeta = (ReferenceMetadata) addressMeta;
        assertTrue(refMeta.isContainment());
        assertTrue(refMeta.isHasBidirectional());
    }

    @Test
    void testGetFeatureMetadataByName() {
        service.registerPackage(testPackage);

        FeatureMetadata found = service.getFeatureMetadataByName("name", "Person", "http://test.example.org/1.0");
        assertNotNull(found);
        assertEquals("name", found.getName());
    }

    @Test
    void testGetFeatureMetadataFromClass() {
        service.registerPackage(testPackage);

        ClassMetadata personMeta = service.getClassMetadata(personClass);
        FeatureMetadata found = service.getFeatureMetadataFromClass("address", personMeta);
        assertNotNull(found);
        assertEquals("address", found.getName());
    }

    // ========================================================================
    // Reference Resolution Tests
    // ========================================================================

    @Test
    void testReferenceTargetResolution() {
        service.registerPackage(testPackage);

        FeatureMetadata addressMeta = service.getFeatureMetadata(addressRef);
        assertTrue(addressMeta instanceof ReferenceMetadata);

        ReferenceMetadata refMeta = (ReferenceMetadata) addressMeta;
        assertNotNull(refMeta.getTargetClassMetadata());
        assertEquals("Address", refMeta.getTargetClassMetadata().getName());
    }

    @Test
    void testReferenceOppositeResolution() {
        service.registerPackage(testPackage);

        ReferenceMetadata addressMeta = (ReferenceMetadata) service.getFeatureMetadata(addressRef);
        ReferenceMetadata personMeta = (ReferenceMetadata) service.getFeatureMetadata(personRef);

        assertNotNull(addressMeta.getOppositeMetadata());
        assertSame(personMeta, addressMeta.getOppositeMetadata());

        assertNotNull(personMeta.getOppositeMetadata());
        assertSame(addressMeta, personMeta.getOppositeMetadata());
    }

    // ========================================================================
    // Bidirectional Aspect-Metadata References Tests
    // ========================================================================

    @Test
    void testClassAspectBackReference() {
        TestAspectProvider provider = new TestAspectProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        ClassMetadata personMeta = service.getClassMetadata(personClass);
        ClassAspect aspect = personMeta.getAspects().get(0);
        assertSame(personMeta, aspect.getClassMetadata(),
                "ClassAspect should have back-reference to its ClassMetadata");
    }

    @Test
    void testFeatureAspectBackReference() {
        TestAspectProvider provider = new TestAspectProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        FeatureMetadata nameMeta = service.getFeatureMetadata(nameAttr);
        FeatureAspect aspect = nameMeta.getAspects().get(0);
        assertSame(nameMeta, aspect.getFeatureMetadata(),
                "FeatureAspect should have back-reference to its FeatureMetadata");
    }

    @Test
    void testPackageAspectBackReference() {
        TestAspectProviderWithPackageSupport provider = new TestAspectProviderWithPackageSupport();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageMetadata pkgMeta = service.getPackageMetadata("http://test.example.org/1.0");
        PackageAspect aspect = pkgMeta.getAspects().get(0);
        assertSame(pkgMeta, aspect.getPackageMetadata(),
                "PackageAspect should have back-reference to its PackageMetadata");
    }

    // ========================================================================
    // AspectProvider Tests
    // ========================================================================

    @Test
    void testRegisterAspectProvider() {
        TestAspectProvider provider = new TestAspectProvider();

        service.registerAspectProvider(provider);
        assertEquals(1, service.getAspectProviders().size());
        assertSame(provider, service.getAspectProviders().get(0));
    }

    @Test
    void testAspectProviderAppliedOnRegistration() {
        // Register provider before package
        TestAspectProvider provider = new TestAspectProvider();
        service.registerAspectProvider(provider);

        // Now register package
        service.registerPackage(testPackage);

        // Check that aspects were created
        ClassAspect classAspect = service.getClassAspect(personClass, "test");
        assertNotNull(classAspect);
        assertEquals("test", classAspect.getTypeId());

        FeatureAspect featureAspect = service.getFeatureAspect(nameAttr, "test");
        assertNotNull(featureAspect);
        assertEquals("test", featureAspect.getTypeId());
    }

    @Test
    void testAspectProviderAppliedToExistingMetadata() {
        // Register package first
        service.registerPackage(testPackage);

        // Now register provider - should be applied to existing metadata
        TestAspectProvider provider = new TestAspectProvider();
        service.registerAspectProvider(provider);

        ClassAspect classAspect = service.getClassAspect(personClass, "test");
        assertNotNull(classAspect);
    }

    @Test
    void testUnregisterAspectProvider() {
        TestAspectProvider provider = new TestAspectProvider();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        // Verify aspect exists
        assertNotNull(service.getClassAspect(personClass, "test"));

        // Unregister provider
        service.unregisterAspectProvider(provider);

        // Aspect should be removed
        assertNull(service.getClassAspect(personClass, "test"));
        assertEquals(0, service.getAspectProviders().size());
    }

    // ========================================================================
    // Registry Tests
    // ========================================================================

    @Test
    void testGetRegistry() {
        service.registerPackage(testPackage);

        assertNotNull(service.getRegistry());
        assertEquals(1, service.getRegistry().getPackages().size());
    }

    // ========================================================================
    // Package Aspect Tests
    // ========================================================================

    @Test
    void testGetPackageAspectWithProvider() {
        TestAspectProviderWithPackageSupport provider = new TestAspectProviderWithPackageSupport();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        PackageAspect pkgAspect = service.getPackageAspect(testPackage, "test-pkg");
        assertNotNull(pkgAspect);
        assertEquals("test-pkg", pkgAspect.getTypeId());
        // Navigate via bidirectional ref to verify package access
        assertNotNull(pkgAspect.getPackageMetadata());
        assertSame(testPackage, pkgAspect.getPackageMetadata().getEPackage());
    }

    @Test
    void testGetPackageAspectNotFound() {
        service.registerPackage(testPackage);
        PackageAspect aspect = service.getPackageAspect(testPackage, "nonexistent");
        assertNull(aspect);
    }

    @Test
    void testGetPackageAspectNullPackage() {
        PackageAspect aspect = service.getPackageAspect(null, "test");
        assertNull(aspect);
    }

    @Test
    void testPackageAspectAppliedToExistingMetadata() {
        // Register package first
        service.registerPackage(testPackage);

        // Now register provider with package support - should be applied to existing metadata
        TestAspectProviderWithPackageSupport provider = new TestAspectProviderWithPackageSupport();
        service.registerAspectProvider(provider);

        PackageAspect pkgAspect = service.getPackageAspect(testPackage, "test-pkg");
        assertNotNull(pkgAspect);
    }

    @Test
    void testUnregisterAspectProviderRemovesPackageAspects() {
        TestAspectProviderWithPackageSupport provider = new TestAspectProviderWithPackageSupport();
        service.registerAspectProvider(provider);
        service.registerPackage(testPackage);

        // Verify aspect exists
        assertNotNull(service.getPackageAspect(testPackage, "test-pkg"));

        // Unregister provider
        service.unregisterAspectProvider(provider);

        // Package aspect should be removed
        assertNull(service.getPackageAspect(testPackage, "test-pkg"));
    }

    // ========================================================================
    // Index Management Tests
    // ========================================================================

    @Test
    void testSetMetadataIndex() {
        service.registerPackage(testPackage);

        // Replace with new index
        MapBasedMetadataIndex newIndex = new MapBasedMetadataIndex();
        service.setMetadataIndex(newIndex);

        assertSame(newIndex, service.getMetadataIndex());
        // New index should be populated with existing metadata
        assertNotNull(service.getClassMetadataByName("Person", "http://test.example.org/1.0"));
    }

    @Test
    void testUnsetMetadataIndex() {
        service.registerPackage(testPackage);
        assertNotNull(service.getMetadataIndex());

        service.unsetMetadataIndex(service.getMetadataIndex());

        assertNull(service.getMetadataIndex());
        // Index-based lookups should return null now
        assertNull(service.getClassMetadataByURI("anything"));
    }

    @Test
    void testUnsetMetadataIndexWrongInstance() {
        MapBasedMetadataIndex originalIndex = new MapBasedMetadataIndex();
        MetadataServiceImpl svc = new MetadataServiceImpl(originalIndex);

        // Try to unset with a different index — should be no-op
        MapBasedMetadataIndex otherIndex = new MapBasedMetadataIndex();
        svc.unsetMetadataIndex(otherIndex);

        assertSame(originalIndex, svc.getMetadataIndex(),
                "Unset with wrong instance should be a no-op");
    }

    // ========================================================================
    // MetadataHandler Tests
    // ========================================================================

    @Test
    void testAddMetadataHandlerNotifiedOnRegister() {
        RecordingMetadataHandler handler = new RecordingMetadataHandler();
        service.addMetadataHandler(handler);

        service.registerPackage(testPackage);

        assertEquals(1, handler.registered.size());
        assertSame(testPackage, handler.registered.get(0).getEPackage());
    }

    @Test
    void testAddMetadataHandlerNotifiedOnUnregister() {
        RecordingMetadataHandler handler = new RecordingMetadataHandler();
        service.addMetadataHandler(handler);
        service.registerPackage(testPackage);

        service.unregisterPackage(testPackage);

        assertEquals(1, handler.unregistered.size());
        assertSame(testPackage, handler.unregistered.get(0).getEPackage());
    }

    @Test
    void testAddMetadataHandlerLateBinding() {
        // Register package BEFORE adding handler
        service.registerPackage(testPackage);

        RecordingMetadataHandler handler = new RecordingMetadataHandler();
        service.addMetadataHandler(handler);

        // Handler should be notified about already-registered package
        assertEquals(1, handler.registered.size());
        assertSame(testPackage, handler.registered.get(0).getEPackage());
    }

    @Test
    void testAddMetadataHandlerLateBindingMultiplePackages() {
        EPackage pkg2 = EcoreFactory.eINSTANCE.createEPackage();
        pkg2.setName("test2");
        pkg2.setNsURI("http://test2.example.org/1.0");
        pkg2.setNsPrefix("test2");

        service.registerPackage(testPackage);
        service.registerPackage(pkg2);

        RecordingMetadataHandler handler = new RecordingMetadataHandler();
        service.addMetadataHandler(handler);

        assertEquals(2, handler.registered.size());
    }

    @Test
    void testRemoveMetadataHandlerClearsHandler() {
        RecordingMetadataHandler handler = new RecordingMetadataHandler();
        service.addMetadataHandler(handler);

        service.removeMetadataHandler(handler);

        assertTrue(handler.cleared);
    }

    @Test
    void testRemoveMetadataHandlerStopsNotifications() {
        RecordingMetadataHandler handler = new RecordingMetadataHandler();
        service.addMetadataHandler(handler);
        service.removeMetadataHandler(handler);

        service.registerPackage(testPackage);

        // Should NOT be notified after removal
        assertTrue(handler.registered.isEmpty());
    }

    @Test
    void testAddMetadataHandlerNull() {
        // Should not throw
        service.addMetadataHandler(null);
    }

    @Test
    void testRemoveMetadataHandlerNull() {
        // Should not throw
        service.removeMetadataHandler(null);
    }

    @Test
    void testAddMetadataHandlerDuplicate() {
        RecordingMetadataHandler handler = new RecordingMetadataHandler();
        service.addMetadataHandler(handler);
        service.addMetadataHandler(handler);

        service.registerPackage(testPackage);

        // Should only be notified once (not added twice)
        assertEquals(1, handler.registered.size());
    }

    @Test
    void testMultipleHandlersNotified() {
        RecordingMetadataHandler handler1 = new RecordingMetadataHandler();
        RecordingMetadataHandler handler2 = new RecordingMetadataHandler();
        service.addMetadataHandler(handler1);
        service.addMetadataHandler(handler2);

        service.registerPackage(testPackage);

        assertEquals(1, handler1.registered.size());
        assertEquals(1, handler2.registered.size());
    }

    @Test
    void testHandlerNotifiedBeforeRemovalOnUnregister() {
        RecordingMetadataHandler handler = new RecordingMetadataHandler();
        service.addMetadataHandler(handler);
        service.registerPackage(testPackage);

        service.unregisterPackage(testPackage);

        // Handler should have received unregistered callback with valid metadata
        assertEquals(1, handler.unregistered.size());
        PackageMetadata unregisteredPkg = handler.unregistered.get(0);
        assertNotNull(unregisteredPkg.getEPackage());
        assertFalse(unregisteredPkg.getClasses().isEmpty());
    }

    // ========================================================================
    // Test AspectProvider Implementations
    // ========================================================================

    /**
     * Simple test AspectProvider that creates dummy aspects.
     */
    private static class TestAspectProvider implements AspectProvider {

        @Override
        public String getAspectTypeId() {
            return "test";
        }

        @Override
        public PackageAspect buildPackageAspect(PackageMetadata packageMetadata) {
            return null;
        }

        @Override
        public ClassAspect buildClassAspect(ClassMetadata classMetadata) {
            return new TestClassAspect();
        }

        @Override
        public FeatureAspect buildFeatureAspect(FeatureMetadata featureMetadata) {
            return new TestFeatureAspect();
        }

        @Override
        public FeatureAspect buildAttributeAspect(AttributeMetadata attributeMetadata) {
            return buildFeatureAspect(attributeMetadata);
        }

        @Override
        public FeatureAspect buildReferenceAspect(ReferenceMetadata referenceMetadata) {
            return buildFeatureAspect(referenceMetadata);
        }

        @Override
        public PackageProfile buildProfiles(PackageMetadata filteredMetadataCopy) {
            return null;
        }
    }

    /**
     * Test ClassAspect implementation.
     */
    private static class TestClassAspect extends ClassAspectImpl {
        // Uses default implementation
    }

    /**
     * Test FeatureAspect implementation.
     */
    private static class TestFeatureAspect extends FeatureAspectImpl {
        // Uses default implementation
    }

    /**
     * Test AspectProvider that also creates PackageAspects.
     */
    private static class TestAspectProviderWithPackageSupport implements AspectProvider {

        @Override
        public String getAspectTypeId() {
            return "test-pkg";
        }

        @Override
        public PackageAspect buildPackageAspect(PackageMetadata packageMetadata) {
            return new TestPackageAspect();
        }

        @Override
        public ClassAspect buildClassAspect(ClassMetadata classMetadata) {
            return null;
        }

        @Override
        public FeatureAspect buildFeatureAspect(FeatureMetadata featureMetadata) {
            return null;
        }

        @Override
        public FeatureAspect buildAttributeAspect(AttributeMetadata attributeMetadata) {
            return null;
        }

        @Override
        public FeatureAspect buildReferenceAspect(ReferenceMetadata referenceMetadata) {
            return null;
        }

        @Override
        public PackageProfile buildProfiles(PackageMetadata filteredMetadataCopy) {
            return null;
        }
    }

    /**
     * Test PackageAspect implementation.
     */
    private static class TestPackageAspect extends PackageAspectImpl {
        // Uses default implementation
    }

    /**
     * Recording MetadataHandler for testing handler notifications.
     */
    private static class RecordingMetadataHandler implements MetadataHandler {
        final List<PackageMetadata> registered = new ArrayList<>();
        final List<PackageMetadata> unregistered = new ArrayList<>();
        boolean cleared = false;

        @Override
        public void onPackageRegistered(PackageMetadata packageMetadata) {
            registered.add(packageMetadata);
        }

        @Override
        public void onPackageUnregistered(PackageMetadata packageMetadata) {
            unregistered.add(packageMetadata);
        }

        @Override
        public void clear() {
            cleared = true;
        }
    }
}
