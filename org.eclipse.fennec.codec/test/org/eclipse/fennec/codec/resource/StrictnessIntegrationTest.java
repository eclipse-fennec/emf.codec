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
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigProperty;
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
 * Integration tests for strictness configuration: strictOnUnknown and strictOnMissing.
 * <p>
 * These tests verify that:
 * <ul>
 *   <li>{@code strictOnUnknown=true}: ERROR on unknown JSON field</li>
 *   <li>{@code strictOnUnknown=false}: WARNING + skip unknown field (default)</li>
 *   <li>{@code strictOnMissing=true}: ERROR on missing required feature</li>
 *   <li>{@code strictOnMissing=false}: WARNING + use default value (default)</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Strictness</a>
 */
@DisplayName("Strictness Integration Tests (strictOnUnknown/strictOnMissing)")
class StrictnessIntegrationTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-strictness.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass productClass;

    // EAttributes on Product
    private EAttribute idAttribute;
    private EAttribute nameAttribute;
    private EAttribute descriptionAttribute;
    private EAttribute priceAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(StrictnessIntegrationTest.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        productClass = ecoreHelper.getEClass(testPackage, "Product");

        idAttribute = (EAttribute) ecoreHelper.getFeature(productClass, "id");
        nameAttribute = (EAttribute) ecoreHelper.getFeature(productClass, "name");
        descriptionAttribute = (EAttribute) ecoreHelper.getFeature(productClass, "description");
        priceAttribute = (EAttribute) ecoreHelper.getFeature(productClass, "price");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Loads JSON into an EObject using the given resolver.
     */
    private EObject load(String json, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://strictness.json"),
                metadataService,
                resolver,
                null);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, productClass);

        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        if (!resource.getContents().isEmpty()) {
            return resource.getContents().get(0);
        }
        return null;
    }

    /**
     * Creates a resolver with global strictOnUnknown setting.
     */
    private ConfigurationResolver resolverWithStrictOnUnknown(boolean strict) {
        return ConfigurationResolver.builder()
                .strictOnUnknown(strict)
                .build();
    }

    /**
     * Creates a resolver with global strictOnMissing setting.
     */
    private ConfigurationResolver resolverWithStrictOnMissing(boolean strict) {
        return ConfigurationResolver.builder()
                .strictOnMissing(strict)
                .build();
    }

    // ========================================================================
    // strictOnUnknown Tests
    // ========================================================================

    @Nested
    @DisplayName("strictOnUnknown")
    class StrictOnUnknownTests {

        @Test
        @DisplayName("Default (false): skip unknown fields with warning")
        void strictOnUnknown_default_skipsUnknownFields() throws IOException {
            String json = """
                    {
                      "id": "PROD-001",
                      "name": "Widget",
                      "unknownField": "should be skipped",
                      "anotherUnknown": 123
                    }
                    """;

            ConfigurationResolver resolver = ConfigurationResolver.defaults();
            EObject product = load(json, resolver);

            // Should succeed and set known fields
            assertNotNull(product);
            assertEquals("PROD-001", product.eGet(idAttribute));
            assertEquals("Widget", product.eGet(nameAttribute));
        }

        @Test
        @DisplayName("strictOnUnknown=true: error on unknown field")
        void strictOnUnknown_true_throwsOnUnknownField() throws IOException {
            String json = """
                    {
                      "id": "PROD-001",
                      "name": "Widget",
                      "unknownField": "should cause error"
                    }
                    """;

            ConfigurationResolver resolver = resolverWithStrictOnUnknown(true);

            Exception exception = assertThrows(Exception.class, () -> load(json, resolver),
                    "Should throw when unknown field encountered with strictOnUnknown=true");

            String message = exception.getMessage();
            if (exception.getCause() != null) {
                message = message + " / " + exception.getCause().getMessage();
            }
            assertTrue(message.contains("Unknown") || message.contains("unknown"),
                    "Exception should mention unknown feature: " + message);
        }

        @Test
        @DisplayName("strictOnUnknown=false: skip unknown fields")
        void strictOnUnknown_false_skipsUnknownFields() throws IOException {
            String json = """
                    {
                      "id": "PROD-001",
                      "name": "Widget",
                      "unknownField": "should be skipped"
                    }
                    """;

            ConfigurationResolver resolver = resolverWithStrictOnUnknown(false);
            EObject product = load(json, resolver);

            // Should succeed
            assertNotNull(product);
            assertEquals("PROD-001", product.eGet(idAttribute));
        }

        @Test
        @DisplayName("strictOnUnknown at class level (in resourceProperties) overrides global")
        void strictOnUnknown_classLevel_overridesGlobal() throws IOException {
            String json = """
                    {
                      "id": "PROD-001",
                      "name": "Widget",
                      "unknownField": "should be skipped"
                    }
                    """;

            // Global = true, but class-level = false (both in resourceProperties for same priority)
            // Class-specific settings within the same source override global settings
            Map<String, Object> classProps = Map.of(
                    ConfigProperty.STRICT_ON_UNKNOWN.getKey(), false
            );
            Map<String, Object> resourceProps = Map.of(
                    ConfigProperty.STRICT_ON_UNKNOWN.getKey(), true,  // global
                    "Product", classProps                              // class-specific
            );

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .resourceProperties(resourceProps)
                    .build();

            // Class-level false should allow skipping unknown fields
            EObject product = load(json, resolver);
            assertNotNull(product);
            assertEquals("PROD-001", product.eGet(idAttribute));
        }
    }

    // ========================================================================
    // strictOnMissing Tests
    // ========================================================================

    @Nested
    @DisplayName("strictOnMissing")
    class StrictOnMissingTests {

        @Test
        @DisplayName("Default (false): use default value for missing required features")
        void strictOnMissing_default_usesDefaultValue() throws IOException {
            // Missing required 'name' feature
            String json = """
                    {
                      "id": "PROD-001"
                    }
                    """;

            ConfigurationResolver resolver = ConfigurationResolver.defaults();
            EObject product = load(json, resolver);

            // Should succeed with default value for name
            assertNotNull(product);
            assertEquals("PROD-001", product.eGet(idAttribute));
            // name should be null (EMF default for String)
            assertEquals(null, product.eGet(nameAttribute));
        }

        @Test
        @DisplayName("strictOnMissing=true: error on missing required feature")
        void strictOnMissing_true_throwsOnMissingRequired() {
            // Missing required 'name' feature
            String json = """
                    {
                      "id": "PROD-001"
                    }
                    """;

            ConfigurationResolver resolver = resolverWithStrictOnMissing(true);

            Exception exception = assertThrows(Exception.class, () -> load(json, resolver));
            assertTrue(exception.getMessage().contains("Missing required")
                    || exception.getCause().getMessage().contains("Missing required"),
                    "Exception should mention missing required: " + exception.getMessage());
        }

        @Test
        @DisplayName("strictOnMissing=true: success when all required features present")
        void strictOnMissing_true_successWhenComplete() throws IOException {
            String json = """
                    {
                      "id": "PROD-001",
                      "name": "Widget"
                    }
                    """;

            ConfigurationResolver resolver = resolverWithStrictOnMissing(true);
            EObject product = load(json, resolver);

            // Should succeed because all required features are present
            assertNotNull(product);
            assertEquals("PROD-001", product.eGet(idAttribute));
            assertEquals("Widget", product.eGet(nameAttribute));
        }

        @Test
        @DisplayName("strictOnMissing=false: allows missing required features")
        void strictOnMissing_false_allowsMissing() throws IOException {
            // Missing both required features
            String json = """
                    {
                      "description": "Optional field only"
                    }
                    """;

            ConfigurationResolver resolver = resolverWithStrictOnMissing(false);
            EObject product = load(json, resolver);

            // Should succeed with default values
            assertNotNull(product);
            assertEquals("Optional field only", product.eGet(descriptionAttribute));
        }

        @Test
        @DisplayName("strictOnMissing at class level (in resourceProperties) overrides global")
        void strictOnMissing_classLevel_overridesGlobal() throws IOException {
            String json = """
                    {
                      "id": "PROD-001"
                    }
                    """;

            // Global = true, but class-level = false (both in resourceProperties for same priority)
            // Class-specific settings within the same source override global settings
            Map<String, Object> classProps = Map.of(
                    ConfigProperty.STRICT_ON_MISSING.getKey(), false
            );
            Map<String, Object> resourceProps = Map.of(
                    ConfigProperty.STRICT_ON_MISSING.getKey(), true,  // global
                    "Product", classProps                              // class-specific
            );

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .resourceProperties(resourceProps)
                    .build();

            // Class-level false should allow missing required features
            EObject product = load(json, resolver);
            assertNotNull(product);
            assertEquals("PROD-001", product.eGet(idAttribute));
        }
    }

    // ========================================================================
    // Combined Strictness Tests
    // ========================================================================

    @Nested
    @DisplayName("Combined Strictness")
    class CombinedStrictnessTests {

        @Test
        @DisplayName("Both strict: error on unknown before checking missing")
        void bothStrict_errorOnUnknownFirst() {
            // Has unknown field AND missing required field
            String json = """
                    {
                      "id": "PROD-001",
                      "unknownField": "should trigger error"
                    }
                    """;

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .strictOnUnknown(true)
                    .strictOnMissing(true)
                    .build();

            Exception exception = assertThrows(Exception.class, () -> load(json, resolver));
            // Should fail on unknown field (encountered first during parsing)
            assertTrue(exception.getMessage().contains("Unknown")
                    || exception.getCause().getMessage().contains("Unknown"),
                    "Exception should mention unknown field: " + exception.getMessage());
        }

        @Test
        @DisplayName("Both strict: success with complete valid JSON")
        void bothStrict_successWithCompleteJson() throws IOException {
            String json = """
                    {
                      "id": "PROD-001",
                      "name": "Widget",
                      "description": "A widget",
                      "price": 9.99
                    }
                    """;

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .strictOnUnknown(true)
                    .strictOnMissing(true)
                    .build();

            EObject product = load(json, resolver);

            assertNotNull(product);
            assertEquals("PROD-001", product.eGet(idAttribute));
            assertEquals("Widget", product.eGet(nameAttribute));
            assertEquals("A widget", product.eGet(descriptionAttribute));
            assertEquals(9.99, product.eGet(priceAttribute));
        }
    }
}
