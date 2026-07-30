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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.util.TypeResolutionHelper;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for S-4: Type Resolution Scoping.
 * <p>
 * Verifies that NAME, CLASS, and NUMERIC type strategies do not scan all
 * registered EPackages when no schema hint is provided. Without scoping,
 * an attacker could exploit non-deterministic resolution to cause type
 * confusion by registering a malicious EPackage with conflicting class names.
 * </p>
 * <p>
 * Security: CWE-843 (Access of Resource Using Incompatible Type).
 * </p>
 */
@DisplayName("S-4: Type Resolution Scoping")
class TypeResolutionScopingTest {

    private static final String TEST_ECORE = "test-deserialization.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private EClass personClass;

    // Second package with a conflicting "Person" class
    private EPackage conflictingPackage;
    private EClass conflictingPersonClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, DeserializationEntryTestBase.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        personClass = EcoreHelper.getEClass(testPackage, "Person");

        // Create a conflicting package with a "Person" class that has different attributes
        conflictingPackage = EcoreFactory.eINSTANCE.createEPackage();
        conflictingPackage.setName("malicious");
        conflictingPackage.setNsURI("http://malicious.example.org/1.0");
        conflictingPackage.setNsPrefix("mal");

        conflictingPersonClass = EcoreFactory.eINSTANCE.createEClass();
        conflictingPersonClass.setName("Person");
        EAttribute evilAttr = EcoreFactory.eINSTANCE.createEAttribute();
        evilAttr.setName("secret");
        evilAttr.setEType(EcorePackage.Literals.ESTRING);
        conflictingPersonClass.getEStructuralFeatures().add(evilAttr);
        conflictingPackage.getEClassifiers().add(conflictingPersonClass);

        EPackage.Registry.INSTANCE.put(conflictingPackage.getNsURI(), conflictingPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        EPackage.Registry.INSTANCE.remove(conflictingPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Nested
    @DisplayName("TypeResolutionHelper scoping")
    class HelperScoping {

        @Test
        @DisplayName("resolveFromSimpleName with context package resolves within package")
        void simpleNameWithContext_resolvesInPackage() {
            EClass resolved = TypeResolutionHelper.resolveFromSimpleName("Person", testPackage);
            assertEquals(personClass, resolved);
        }

        @Test
        @DisplayName("resolveFromSimpleName with context package does not cross-resolve")
        void simpleNameWithContext_doesNotCrossResolve() {
            EClass resolved = TypeResolutionHelper.resolveFromSimpleName("Person", conflictingPackage);
            assertEquals(conflictingPersonClass, resolved,
                    "Should resolve to the conflicting package's Person, not the test package's");
        }

        @Test
        @DisplayName("resolveFromSimpleName without context package returns null")
        void simpleNameWithoutContext_returnsNull() {
            EClass resolved = TypeResolutionHelper.resolveFromSimpleName("Person", null);
            assertNull(resolved,
                    "Without context package, resolution should fail rather than scan all packages");
        }

        @Test
        @DisplayName("resolveFromClassName with context package resolves within package")
        void classNameWithContext_resolvesInPackage() {
            // Person in test package has no instanceClassName set, so this tests the
            // simple name fallback within the context package
            EClass resolved = TypeResolutionHelper.resolveFromClassName("Person", testPackage);
            assertEquals(personClass, resolved);
        }

        @Test
        @DisplayName("resolveFromClassName without context package returns null")
        void classNameWithoutContext_returnsNull() {
            EClass resolved = TypeResolutionHelper.resolveFromClassName("Person", null);
            assertNull(resolved,
                    "Without context package, resolution should fail rather than scan all packages");
        }

        @Test
        @DisplayName("resolveFromNumeric without hints returns null")
        void numericWithoutHints_returnsNull() {
            int classifierId = personClass.getClassifierID();
            EClass resolved = TypeResolutionHelper.resolveFromNumeric(
                    String.valueOf(classifierId), null, null);
            assertNull(resolved,
                    "Without hint or schema, resolution should fail rather than scan all packages");
        }

        @Test
        @DisplayName("resolveFromNumeric with hint resolves correctly")
        void numericWithHint_resolvesCorrectly() {
            int classifierId = personClass.getClassifierID();
            EClass resolved = TypeResolutionHelper.resolveFromNumeric(
                    String.valueOf(classifierId), personClass, null);
            assertEquals(personClass, resolved);
        }

        @Test
        @DisplayName("resolveFromNumeric with schema URI resolves correctly")
        void numericWithSchema_resolvesCorrectly() {
            int classifierId = personClass.getClassifierID();
            EClass resolved = TypeResolutionHelper.resolveFromNumeric(
                    String.valueOf(classifierId), null, testPackage.getNsURI());
            assertEquals(personClass, resolved);
        }
    }

    @Nested
    @DisplayName("End-to-end: NAME strategy with schema hint")
    class NameStrategyE2E {

        @Test
        @DisplayName("NAME strategy with CODEC_ROOT_TYPE resolves correctly")
        void nameWithRootType_resolves() throws IOException {
            ConfigurationResolver config = ConfigurationResolver.builder()
                    .typeStrategy(TypeStrategy.NAME)
                    .build();

            // Serialize with NAME strategy
            MetadataWhiteboard metadataService = MetadataServiceFactory.create();
            metadataService.registerPackage(testPackage);
            CodecResource saveResource = new CodecResource(
                    URI.createURI("test://type-scoping-save.json"), metadataService, config, null);

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(EcoreHelper.getFeature(personClass, "name"), "Alice");
            saveResource.getContents().add(person);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            saveResource.save(out, Map.of());

            // Deserialize with CODEC_ROOT_TYPE hint — should resolve "Person" in test package
            CodecResource loadResource = new CodecResource(
                    URI.createURI("test://type-scoping-load.json"), metadataService, config, null);
            loadResource.load(new ByteArrayInputStream(out.toByteArray()),
                    Map.of(CodecResource.CODEC_ROOT_TYPE, personClass));

            assertFalse(loadResource.getContents().isEmpty(), "Should load successfully with schema hint");
            assertEquals(personClass, loadResource.getContents().get(0).eClass());
        }

        @Test
        @DisplayName("NAME strategy without schema hint produces warning diagnostic")
        void nameWithoutHint_producesWarning() throws IOException {
            ConfigurationResolver config = ConfigurationResolver.builder()
                    .typeStrategy(TypeStrategy.NAME)
                    .build();

            // JSON with simple name but no schema context
            String json = """
                    {"_type": "Person", "name": "Alice"}
                    """;

            MetadataWhiteboard metadataService = MetadataServiceFactory.create();
            metadataService.registerPackage(testPackage);
            CodecResource resource = new CodecResource(
                    URI.createURI("test://no-hint.json"), metadataService, config, null);

            // Load WITHOUT CODEC_ROOT_TYPE or CODEC_ROOT_SCHEMA
            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), Map.of());

            // Should have a warning about missing schema hint
            boolean hasWarning = resource.getWarnings().stream()
                    .anyMatch(d -> d.getMessage() != null
                            && d.getMessage().contains("requires a schema hint"));
            assertTrue(hasWarning,
                    "Expected 'requires a schema hint' warning, got warnings: "
                    + resource.getWarnings() + ", errors: " + resource.getErrors());
        }
    }

    @Nested
    @DisplayName("End-to-end: NUMERIC strategy with schema hint")
    class NumericStrategyE2E {

        @Test
        @DisplayName("NUMERIC strategy with CODEC_ROOT_TYPE resolves correctly")
        void numericWithRootType_resolves() throws IOException {
            ConfigurationResolver config = ConfigurationResolver.builder()
                    .typeStrategy(TypeStrategy.NUMERIC)
                    .build();

            // Serialize with NUMERIC strategy
            MetadataWhiteboard metadataService = MetadataServiceFactory.create();
            metadataService.registerPackage(testPackage);
            CodecResource saveResource = new CodecResource(
                    URI.createURI("test://numeric-scoping-save.json"), metadataService, config, null);

            EObject person = testPackage.getEFactoryInstance().create(personClass);
            person.eSet(EcoreHelper.getFeature(personClass, "name"), "Bob");
            saveResource.getContents().add(person);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            saveResource.save(out, Map.of());

            // Deserialize with CODEC_ROOT_TYPE hint
            CodecResource loadResource = new CodecResource(
                    URI.createURI("test://numeric-scoping-load.json"), metadataService, config, null);
            loadResource.load(new ByteArrayInputStream(out.toByteArray()),
                    Map.of(CodecResource.CODEC_ROOT_TYPE, personClass));

            assertFalse(loadResource.getContents().isEmpty(), "Should load successfully with schema hint");
            assertEquals(personClass, loadResource.getContents().get(0).eClass());
        }
    }
}
