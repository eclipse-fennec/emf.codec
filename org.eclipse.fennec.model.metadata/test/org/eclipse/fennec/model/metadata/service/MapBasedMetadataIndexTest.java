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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.FeatureMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataIndex;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MapBasedMetadataIndex}.
 * <p>
 * Tests the index functionality for fast metadata lookups by various criteria:
 * URI, className, instanceClassName, and annotations.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2026-01-26
 */
class MapBasedMetadataIndexTest {

    private MetadataWhiteboard service;
    private MetadataIndex index;

    // Test package 1
    private EPackage testPackage1;
    private EClass personClass;
    private EClass addressClass;
    private EAttribute nameAttr;
    private EReference addressRef;

    // Test package 2 (for cross-package queries)
    private EPackage testPackage2;
    private EClass employeeClass;
    private EClass companyClass;

    @BeforeEach
    void setUp() {
        service = new MetadataServiceImpl();
        index = (MetadataIndex) service.getIndexReader();
        createTestPackage1();
        createTestPackage2();
    }

    private void createTestPackage1() {
        testPackage1 = EcoreFactory.eINSTANCE.createEPackage();
        testPackage1.setName("test1");
        testPackage1.setNsURI("http://test1.example.org/1.0");
        testPackage1.setNsPrefix("test1");

        // Person class with instanceClassName
        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        personClass.setInstanceClassName("org.example.Person");
        testPackage1.getEClassifiers().add(personClass);

        nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(nameAttr);

        // Address class
        addressClass = EcoreFactory.eINSTANCE.createEClass();
        addressClass.setName("Address");
        addressClass.setInstanceClassName("org.example.Address");
        testPackage1.getEClassifiers().add(addressClass);

        addressRef = EcoreFactory.eINSTANCE.createEReference();
        addressRef.setName("address");
        addressRef.setEType(addressClass);
        addressRef.setContainment(true);
        personClass.getEStructuralFeatures().add(addressRef);
    }

    private void createTestPackage2() {
        testPackage2 = EcoreFactory.eINSTANCE.createEPackage();
        testPackage2.setName("test2");
        testPackage2.setNsURI("http://test2.example.org/1.0");
        testPackage2.setNsPrefix("test2");

        // Employee class - same instanceClassName as Person (simulating java.util.Map$Entry pattern)
        employeeClass = EcoreFactory.eINSTANCE.createEClass();
        employeeClass.setName("Employee");
        employeeClass.setInstanceClassName("org.example.Person"); // Intentionally same as Person
        testPackage2.getEClassifiers().add(employeeClass);

        // Company class - unique instanceClassName
        companyClass = EcoreFactory.eINSTANCE.createEClass();
        companyClass.setName("Company");
        companyClass.setInstanceClassName("org.example.Company");
        testPackage2.getEClassifiers().add(companyClass);
    }

    // ========================================================================
    // Query by instanceClassName Tests
    // ========================================================================

    @Nested
    @DisplayName("findByInstanceClassName(nsURI, instanceClassName)")
    class FindByInstanceClassNameWithNsURI {

        @Test
        @DisplayName("should find class by instanceClassName within specific package")
        void testFindByInstanceClassName() {
            service.registerPackage(testPackage1);

            ClassMetadata found = service.getIndexReader().findByInstanceClassName(
                "http://test1.example.org/1.0", "org.example.Person");

            assertNotNull(found);
            assertEquals("Person", found.getName());
            assertSame(personClass, found.getEClass());
        }

        @Test
        @DisplayName("should return null for non-existent instanceClassName")
        void testFindByInstanceClassNameNotFound() {
            service.registerPackage(testPackage1);

            ClassMetadata found = service.getIndexReader().findByInstanceClassName(
                "http://test1.example.org/1.0", "org.example.NonExistent");

            assertNull(found);
        }

        @Test
        @DisplayName("should return null for wrong package nsURI")
        void testFindByInstanceClassNameWrongPackage() {
            service.registerPackage(testPackage1);

            ClassMetadata found = service.getIndexReader().findByInstanceClassName(
                "http://wrong.example.org/1.0", "org.example.Person");

            assertNull(found);
        }

        @Test
        @DisplayName("should return null for null parameters")
        void testFindByInstanceClassNameNullParams() {
            service.registerPackage(testPackage1);

            assertNull(service.getIndexReader().findByInstanceClassName(null, "org.example.Person"));
            assertNull(service.getIndexReader().findByInstanceClassName("http://test1.example.org/1.0", null));
            assertNull(service.getIndexReader().findByInstanceClassName(null, null));
        }

        @Test
        @DisplayName("should distinguish between packages with same instanceClassName")
        void testFindByInstanceClassNameMultiplePackages() {
            service.registerPackage(testPackage1);
            service.registerPackage(testPackage2);

            ClassMetadata fromPkg1 = service.getIndexReader().findByInstanceClassName(
                "http://test1.example.org/1.0", "org.example.Person");
            ClassMetadata fromPkg2 = service.getIndexReader().findByInstanceClassName(
                "http://test2.example.org/1.0", "org.example.Person");

            assertNotNull(fromPkg1);
            assertNotNull(fromPkg2);
            assertEquals("Person", fromPkg1.getName());
            assertEquals("Employee", fromPkg2.getName());
        }
    }

    @Nested
    @DisplayName("findAllByInstanceClassName(instanceClassName)")
    class FindAllByInstanceClassName {

        @Test
        @DisplayName("should find all classes with same instanceClassName across packages")
        void testFindAllByInstanceClassName() {
            service.registerPackage(testPackage1);
            service.registerPackage(testPackage2);

            EList<ClassMetadata> found = service.getIndexReader()
                .findAllByInstanceClassName("org.example.Person");

            assertNotNull(found);
            assertEquals(2, found.size());
            // Should contain both Person and Employee
            assertTrue(found.stream().anyMatch(m -> "Person".equals(m.getName())));
            assertTrue(found.stream().anyMatch(m -> "Employee".equals(m.getName())));
        }

        @Test
        @DisplayName("should return single result for unique instanceClassName")
        void testFindAllByInstanceClassNameUnique() {
            service.registerPackage(testPackage1);
            service.registerPackage(testPackage2);

            EList<ClassMetadata> found = service.getIndexReader()
                .findAllByInstanceClassName("org.example.Company");

            assertEquals(1, found.size());
            assertEquals("Company", found.get(0).getName());
        }

        @Test
        @DisplayName("should return empty list for non-existent instanceClassName")
        void testFindAllByInstanceClassNameNotFound() {
            service.registerPackage(testPackage1);

            EList<ClassMetadata> found = service.getIndexReader()
                .findAllByInstanceClassName("org.example.NonExistent");

            assertNotNull(found);
            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("should return empty list for null instanceClassName")
        void testFindAllByInstanceClassNameNull() {
            service.registerPackage(testPackage1);

            EList<ClassMetadata> found = service.getIndexReader().findAllByInstanceClassName(null);

            assertNotNull(found);
            assertTrue(found.isEmpty());
        }
    }

    // ========================================================================
    // Query by className Tests
    // ========================================================================

    @Nested
    @DisplayName("findByClassName(nsURI, className)")
    class FindByClassName {

        @Test
        @DisplayName("should find class by EClass name within specific package")
        void testFindByClassName() {
            service.registerPackage(testPackage1);

            ClassMetadata found = service.getIndexReader().findByClassName(
                "http://test1.example.org/1.0", "Person");

            assertNotNull(found);
            assertEquals("Person", found.getName());
            assertSame(personClass, found.getEClass());
        }

        @Test
        @DisplayName("should return null for non-existent className")
        void testFindByClassNameNotFound() {
            service.registerPackage(testPackage1);

            ClassMetadata found = service.getIndexReader().findByClassName(
                "http://test1.example.org/1.0", "NonExistent");

            assertNull(found);
        }

        @Test
        @DisplayName("should return null for null parameters")
        void testFindByClassNameNullParams() {
            service.registerPackage(testPackage1);

            assertNull(service.getIndexReader().findByClassName(null, "Person"));
            assertNull(service.getIndexReader().findByClassName("http://test1.example.org/1.0", null));
        }
    }

    @Nested
    @DisplayName("findAllByClassName(className)")
    class FindAllByClassName {

        @Test
        @DisplayName("should find all classes with same name across packages")
        void testFindAllByClassName() {
            // Create another package with a class named "Address"
            EPackage pkg3 = EcoreFactory.eINSTANCE.createEPackage();
            pkg3.setName("test3");
            pkg3.setNsURI("http://test3.example.org/1.0");
            pkg3.setNsPrefix("test3");

            EClass address2 = EcoreFactory.eINSTANCE.createEClass();
            address2.setName("Address"); // Same name as in testPackage1
            pkg3.getEClassifiers().add(address2);

            service.registerPackage(testPackage1);
            service.registerPackage(pkg3);

            EList<ClassMetadata> found = service.getIndexReader().findAllByClassName("Address");

            assertEquals(2, found.size());
        }

        @Test
        @DisplayName("should return empty list for non-existent className")
        void testFindAllByClassNameNotFound() {
            service.registerPackage(testPackage1);

            EList<ClassMetadata> found = service.getIndexReader().findAllByClassName("NonExistent");

            assertNotNull(found);
            assertTrue(found.isEmpty());
        }
    }

    // ========================================================================
    // Query by URI Tests
    // ========================================================================

    @Nested
    @DisplayName("findClassByURI(uri)")
    class FindClassByURI {

        @Test
        @DisplayName("should find class by typeURI")
        void testFindClassByURI() {
            service.registerPackage(testPackage1);

            ClassMetadata personMeta = service.getClassMetadata(personClass);
            String uri = personMeta.getTypeURI();

            ClassMetadata found = service.getIndexReader().findClassByURI(uri);

            assertNotNull(found);
            assertSame(personMeta, found);
        }

        @Test
        @DisplayName("should return null for non-existent URI")
        void testFindClassByURINotFound() {
            service.registerPackage(testPackage1);

            ClassMetadata found = service.getIndexReader().findClassByURI(
                "http://nonexistent.example.org#Person");

            assertNull(found);
        }

        @Test
        @DisplayName("should return null for null URI")
        void testFindClassByURINull() {
            service.registerPackage(testPackage1);

            ClassMetadata found = service.getIndexReader().findClassByURI(null);

            assertNull(found);
        }
    }

    @Nested
    @DisplayName("findFeatureByURI(uri)")
    class FindFeatureByURI {

        @Test
        @DisplayName("should find feature by URI")
        void testFindFeatureByURI() {
            service.registerPackage(testPackage1);

            FeatureMetadata nameMeta = service.getFeatureMetadata(nameAttr);
            String uri = org.eclipse.emf.ecore.util.EcoreUtil.getURI(nameAttr).toString();

            FeatureMetadata found = service.getIndexReader().findFeatureByURI(uri);

            assertNotNull(found);
            assertSame(nameMeta, found);
        }

        @Test
        @DisplayName("should return null for non-existent URI")
        void testFindFeatureByURINotFound() {
            service.registerPackage(testPackage1);

            FeatureMetadata found = service.getIndexReader().findFeatureByURI(
                "http://nonexistent.example.org#Person/name");

            assertNull(found);
        }

        @Test
        @DisplayName("should return null for null URI")
        void testFindFeatureByURINull() {
            service.registerPackage(testPackage1);

            FeatureMetadata found = service.getIndexReader().findFeatureByURI(null);

            assertNull(found);
        }
    }

    // ========================================================================
    // Query by Annotation Tests
    // ========================================================================

    @Nested
    @DisplayName("findClassesByAnnotation()")
    class FindClassesByAnnotation {

        @Test
        @DisplayName("should find classes by annotation key and value")
        void testFindClassesByAnnotation() {
            // Add annotation to personClass
            EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
            annotation.setSource("http://test.annotation");
            annotation.getDetails().put("key", "value1");
            personClass.getEAnnotations().add(annotation);

            service.registerPackage(testPackage1);

            EList<ClassMetadata> found = service.getIndexReader()
                .findClassesByAnnotation("http://test.annotation", "key", "value1");

            assertEquals(1, found.size());
            assertEquals("Person", found.get(0).getName());
        }

        @Test
        @DisplayName("should find classes by annotation key with any value")
        void testFindClassesByAnnotationAnyValue() {
            // Add annotations to both classes
            EAnnotation ann1 = EcoreFactory.eINSTANCE.createEAnnotation();
            ann1.setSource("http://test.annotation");
            ann1.getDetails().put("key", "value1");
            personClass.getEAnnotations().add(ann1);

            EAnnotation ann2 = EcoreFactory.eINSTANCE.createEAnnotation();
            ann2.setSource("http://test.annotation");
            ann2.getDetails().put("key", "value2");
            addressClass.getEAnnotations().add(ann2);

            service.registerPackage(testPackage1);

            EList<ClassMetadata> found = service.getIndexReader()
                .findClassesByAnnotation("http://test.annotation", "key", null);

            assertEquals(2, found.size());
        }

        @Test
        @DisplayName("should return empty list for non-matching annotation")
        void testFindClassesByAnnotationNotFound() {
            service.registerPackage(testPackage1);

            EList<ClassMetadata> found = service.getIndexReader()
                .findClassesByAnnotation("http://nonexistent", "key", "value");

            assertNotNull(found);
            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("should return empty list for null parameters")
        void testFindClassesByAnnotationNullParams() {
            service.registerPackage(testPackage1);

            EList<ClassMetadata> found1 = service.getIndexReader()
                .findClassesByAnnotation(null, "key", "value");
            EList<ClassMetadata> found2 = service.getIndexReader()
                .findClassesByAnnotation("http://test", null, "value");

            assertTrue(found1.isEmpty());
            assertTrue(found2.isEmpty());
        }
    }

    @Nested
    @DisplayName("findFeaturesByAnnotation()")
    class FindFeaturesByAnnotation {

        @Test
        @DisplayName("should find features by annotation key and value")
        void testFindFeaturesByAnnotation() {
            // Add annotation to nameAttr
            EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
            annotation.setSource("http://test.annotation");
            annotation.getDetails().put("transient", "true");
            nameAttr.getEAnnotations().add(annotation);

            service.registerPackage(testPackage1);

            EList<FeatureMetadata> found = service.getIndexReader()
                .findFeaturesByAnnotation("http://test.annotation", "transient", "true");

            assertEquals(1, found.size());
            assertEquals("name", found.get(0).getName());
        }

        @Test
        @DisplayName("should return empty list for non-matching annotation")
        void testFindFeaturesByAnnotationNotFound() {
            service.registerPackage(testPackage1);

            EList<FeatureMetadata> found = service.getIndexReader()
                .findFeaturesByAnnotation("http://nonexistent", "key", "value");

            assertNotNull(found);
            assertTrue(found.isEmpty());
        }
    }

    // ========================================================================
    // Index Maintenance Tests
    // ========================================================================

    @Nested
    @DisplayName("Index maintenance operations")
    class IndexMaintenanceTests {

        @Test
        @DisplayName("should remove class from index when package is unregistered")
        void testUnregisterPackageRemovesFromIndex() {
            service.registerPackage(testPackage1);

            // Verify class is indexed
            ClassMetadata found = service.getIndexReader().findByClassName(
                "http://test1.example.org/1.0", "Person");
            assertNotNull(found);

            // Unregister package
            service.unregisterPackage(testPackage1);

            // Verify class is no longer indexed
            ClassMetadata afterUnregister = service.getIndexReader().findByClassName(
                "http://test1.example.org/1.0", "Person");
            assertNull(afterUnregister);
        }

        @Test
        @DisplayName("should remove from global indexes when package is unregistered")
        void testUnregisterPackageRemovesFromGlobalIndexes() {
            service.registerPackage(testPackage1);
            service.registerPackage(testPackage2);

            // Both packages have classes with instanceClassName "org.example.Person"
            EList<ClassMetadata> before = service.getIndexReader()
                .findAllByInstanceClassName("org.example.Person");
            assertEquals(2, before.size());

            // Unregister testPackage1
            service.unregisterPackage(testPackage1);

            // Only testPackage2's Employee should remain
            EList<ClassMetadata> after = service.getIndexReader()
                .findAllByInstanceClassName("org.example.Person");
            assertEquals(1, after.size());
            assertEquals("Employee", after.get(0).getName());
        }

        @Test
        @DisplayName("should clear all indexes")
        void testClearIndex() {
            service.registerPackage(testPackage1);
            service.registerPackage(testPackage2);

            // Verify classes are indexed
            assertNotNull(service.getIndexReader().findByClassName(
                "http://test1.example.org/1.0", "Person"));

            // Clear index directly
            index.clear();

            // Verify all indexes are empty
            assertNull(service.getIndexReader().findByClassName(
                "http://test1.example.org/1.0", "Person"));
            assertTrue(service.getIndexReader().findAllByInstanceClassName("org.example.Person").isEmpty());
            assertTrue(service.getIndexReader().findAllByClassName("Person").isEmpty());
        }
    }

    // ========================================================================
    // Integration with MetadataService Tests
    // ========================================================================

    @Nested
    @DisplayName("MetadataService integration")
    class MetadataServiceIntegrationTests {

        @Test
        @DisplayName("getIndexReader() should return non-null reader")
        void testGetIndexReaderNotNull() {
            assertNotNull(service.getIndexReader());
        }

        @Test
        @DisplayName("index should be automatically populated on package registration")
        void testIndexPopulatedOnRegistration() {
            // Before registration, nothing should be found
            assertNull(service.getIndexReader().findByClassName(
                "http://test1.example.org/1.0", "Person"));

            // Register package
            service.registerPackage(testPackage1);

            // Now it should be found
            ClassMetadata found = service.getIndexReader().findByClassName(
                "http://test1.example.org/1.0", "Person");
            assertNotNull(found);
        }

        @Test
        @DisplayName("features should be indexed when package is registered")
        void testFeaturesIndexedOnRegistration() {
            service.registerPackage(testPackage1);

            String uri = org.eclipse.emf.ecore.util.EcoreUtil.getURI(nameAttr).toString();
            FeatureMetadata found = service.getIndexReader().findFeatureByURI(uri);

            assertNotNull(found);
            assertEquals("name", found.getName());
        }
    }

    // ========================================================================
    // Edge Cases and Concurrency Tests
    // ========================================================================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle class without instanceClassName")
        void testClassWithoutInstanceClassName() {
            EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
            pkg.setName("noinst");
            pkg.setNsURI("http://noinst.example.org/1.0");
            pkg.setNsPrefix("noinst");

            EClass noInstClass = EcoreFactory.eINSTANCE.createEClass();
            noInstClass.setName("NoInstance");
            // No instanceClassName set
            pkg.getEClassifiers().add(noInstClass);

            service.registerPackage(pkg);

            // Should still be findable by className
            ClassMetadata found = service.getIndexReader().findByClassName(
                "http://noinst.example.org/1.0", "NoInstance");
            assertNotNull(found);

            // But not by instanceClassName
            EList<ClassMetadata> byInstance = service.getIndexReader()
                .findAllByInstanceClassName("NoInstance");
            assertTrue(byInstance.isEmpty());
        }

        @Test
        @DisplayName("should handle registering same package multiple times")
        void testRegisterSamePackageTwice() {
            service.registerPackage(testPackage1);
            service.registerPackage(testPackage1); // Register again

            // Should still find only one entry
            EList<ClassMetadata> found = service.getIndexReader()
                .findAllByClassName("Person");
            assertEquals(1, found.size());
        }
    }
}
