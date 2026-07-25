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
package org.eclipse.fennec.codec.metadata.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.metadata.model.codec.CodecClassProfile;
import org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode;
import org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig;
import org.eclipse.fennec.model.metadata.ClassProfile;
import org.eclipse.fennec.model.metadata.SerializationFormat;
import org.eclipse.fennec.model.metadata.TypeStrategy;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.service.MetadataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * EPackage-level codec annotations as package-wide defaults (issue #75).
 * <p>
 * The package level is an <b>annotation-internal</b> layer, pre-merged into each class profile —
 * the model the profile documentation already describes: "class inherits from package defaults".
 * It is therefore not a new runtime configuration level; the cascading merge of options, resource,
 * factory and module keeps seeing exactly one annotation layer.
 * </p>
 * <p>
 * Precedence inside that layer runs from specific to general, so a class states an exception to
 * its package rather than being overruled by it. Merging is per property: a class that sets one
 * key must not discard the package's other keys.
 * </p>
 */
@DisplayName("EPackage-level annotations (#75)")
class CodecPackageLevelAnnotationTest {

    private MetadataWhiteboard service;
    private EPackage testPackage;
    private EClass personClass;
    private EClass addressClass;

    @BeforeEach
    void setUp() {
        service = new MetadataServiceImpl();

        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("pkglevel");
        testPackage.setNsURI("http://test.example.org/pkglevel/1.0");
        testPackage.setNsPrefix("pkglevel");

        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        testPackage.getEClassifiers().add(personClass);
        EAttribute nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(nameAttr);

        addressClass = EcoreFactory.eINSTANCE.createEClass();
        addressClass.setName("Address");
        testPackage.getEClassifiers().add(addressClass);
        EAttribute streetAttr = EcoreFactory.eINSTANCE.createEAttribute();
        streetAttr.setName("street");
        streetAttr.setEType(EcorePackage.Literals.ESTRING);
        addressClass.getEStructuralFeatures().add(streetAttr);
    }

    private void annotate(org.eclipse.emf.ecore.EModelElement target, String... keyValuePairs) {
        EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
        ann.setSource(CodecAnnotationConstants.CODEC_SOURCE);
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            ann.getDetails().put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        target.getEAnnotations().add(ann);
    }

    private CodecClassProfile profileOf(EClass eClass) {
        service.registerAspectProvider(new CodecAspectProvider());
        service.registerPackage(testPackage);

        ClassProfile classProfile = service.getClassProfile(eClass, "codec");
        assertNotNull(classProfile, "a class profile must be built for " + eClass.getName());
        return (CodecClassProfile) classProfile;
    }

    // ========================================================================
    // Section 1: The package default reaches every class
    // ========================================================================

    @Nested
    @DisplayName("1. Package defaults apply to all classes")
    class PackageDefaults {

        @Test
        @DisplayName("1.1 a package-level typeStrategy applies to every class in the package")
        void packageStrategyAppliesToAllClasses() {
            annotate(testPackage, "typeStrategy", "NAME");

            assertSame(TypeStrategy.NAME, profileOf(personClass).getTypeConfig().getStrategy());
            assertSame(TypeStrategy.NAME, profileOf(addressClass).getTypeConfig().getStrategy());
        }

        @Test
        @DisplayName("1.2 several package-level keys all arrive")
        void severalPackageKeysArrive() {
            annotate(testPackage, "typeStrategy", "NAME", "typeKey", "kind", "typeFormat", "STRUCTURED");

            TypeSerializationConfig config = profileOf(personClass).getTypeConfig();

            assertSame(TypeStrategy.NAME, config.getStrategy());
            assertEquals("kind", config.getTypeKey());
            assertSame(SerializationFormat.STRUCTURED, config.getFormat());
        }

        @Test
        @DisplayName("1.3 no package annotation leaves the previous behavior untouched (R1)")
        void noPackageAnnotationChangesNothing() {
            annotate(personClass, "typeStrategy", "NAME");

            TypeSerializationConfig config = profileOf(personClass).getTypeConfig();

            assertSame(TypeStrategy.NAME, config.getStrategy());
            // Address has no annotation anywhere and must keep the model defaults
            assertNotNull(profileOf(addressClass).getTypeConfig());
        }
    }

    // ========================================================================
    // Section 2: Precedence and per-property merging
    // ========================================================================

    @Nested
    @DisplayName("2. Class overrides package, per property")
    class Precedence {

        @Test
        @DisplayName("2.1 a class-level value wins over the package-level one")
        void classWinsOverPackage() {
            annotate(testPackage, "typeStrategy", "NAME");
            annotate(personClass, "typeStrategy", "URI");

            assertSame(TypeStrategy.URI, profileOf(personClass).getTypeConfig().getStrategy(),
                    "a class states an exception to its package, not the other way round");
        }

        @Test
        @DisplayName("2.2 the override is per property - the package's other keys survive")
        void overrideIsPerProperty() {
            annotate(testPackage, "typeStrategy", "NAME", "typeKey", "kind");
            annotate(personClass, "typeStrategy", "URI");

            TypeSerializationConfig config = profileOf(personClass).getTypeConfig();

            assertSame(TypeStrategy.URI, config.getStrategy(), "class value applies");
            assertEquals("kind", config.getTypeKey(),
                    "a class overriding one key must not discard the package's others");
        }

        @Test
        @DisplayName("2.3 an unannotated class in an annotated package still gets the defaults")
        void unannotatedClassInheritsPackage() {
            annotate(testPackage, "typeStrategy", "NAME", "typeKey", "kind");
            annotate(personClass, "typeStrategy", "URI");

            TypeSerializationConfig config = profileOf(addressClass).getTypeConfig();

            assertSame(TypeStrategy.NAME, config.getStrategy());
            assertEquals("kind", config.getTypeKey());
        }
    }

    // ========================================================================
    // Section 3: The fingerprint - the consumer this level was missing for
    // ========================================================================

    @Nested
    @DisplayName("3. Fingerprint opt-in at package level (#73)")
    class FingerprintAtPackageLevel {

        @Test
        @DisplayName("3.1 fingerprintMode can be opted into for a whole package")
        void fingerprintModeAtPackageLevel() {
            // The fingerprint's currency is the EPackage, so this is its natural home - it was
            // class-level only because this level did not exist.
            annotate(testPackage, "fingerprintMode", "FIRST_TOUCH");

            assertSame(FingerprintMode.FIRST_TOUCH,
                    profileOf(personClass).getTypeConfig().getFingerprintMode());
            assertSame(FingerprintMode.FIRST_TOUCH,
                    profileOf(addressClass).getTypeConfig().getFingerprintMode());
        }

        @Test
        @DisplayName("3.2 a class can opt out again")
        void classCanOptOut() {
            annotate(testPackage, "fingerprintMode", "FIRST_TOUCH");
            annotate(addressClass, "fingerprintMode", "NONE");

            assertSame(FingerprintMode.FIRST_TOUCH,
                    profileOf(personClass).getTypeConfig().getFingerprintMode());
            assertSame(FingerprintMode.NONE,
                    profileOf(addressClass).getTypeConfig().getFingerprintMode(),
                    "an explicit NONE at class level must beat the package opt-in");
        }

        @Test
        @DisplayName("3.3 the fingerprint key can be set package-wide")
        void fingerprintKeyAtPackageLevel() {
            annotate(testPackage, "fingerprintMode", "FIRST_TOUCH", "fingerprintKey", "modelVersion");

            TypeSerializationConfig config = profileOf(personClass).getTypeConfig();

            assertSame(FingerprintMode.FIRST_TOUCH, config.getFingerprintMode());
            assertEquals("modelVersion", config.getFingerprintKey());
        }
    }
}
