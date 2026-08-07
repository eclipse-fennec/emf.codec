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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Escalating a failed value conversion (issue #131).
 * <p>
 * By default a value that cannot be converted is reported as a diagnostic and the feature
 * keeps its default — indistinguishable from the value being absent, because {@code 0} and
 * {@code 0.0} are values a model may legitimately hold. {@code strictOnConversion} lets a
 * caller demand a failure instead.
 * </p>
 */
@DisplayName("strictOnConversion")
class StrictOnConversionTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/ser/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;
    private EAttribute ageAttribute;
    private EAttribute nameAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, StrictOnConversionTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        ageAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "age");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    /** An age that is not a number - the conversion cannot succeed. */
    private String brokenValue() {
        return """
                {
                  "_type": "%s#//Person",
                  "name": "Alice",
                  "age": "not-a-number"
                }
                """.formatted(testPackage.getNsURI());
    }

    @Test
    @DisplayName("by default the value is dropped and the load still succeeds")
    void lenientByDefault() throws IOException {
        CodecResource resource = load(brokenValue(), ConfigurationResolver.defaults());

        EObject person = resource.getContents().get(0);
        assertEquals("Alice", person.eGet(nameAttribute), "the rest of the object survives");
        assertEquals(0, person.eGet(ageAttribute),
                "the failed conversion leaves the default - indistinguishable from absent");
        assertFalse(resource.getWarnings().isEmpty(),
                "the only trace is a diagnostic the caller has to look for");
    }

    @Test
    @DisplayName("strictOnConversion turns the dropped value into a failure")
    void strictFailsTheLoad() {
        ConfigurationResolver strict = ConfigurationResolver.builder()
                .resourceProperties(Map.of("strictOnConversion", true))
                .build();

        assertThrows(Exception.class, () -> load(brokenValue(), strict),
                "a caller can demand a failure instead of a silent default");
    }

    @Test
    @DisplayName("a sound document is unaffected by strictOnConversion")
    void strictLeavesValidDocumentsAlone() throws IOException {
        ConfigurationResolver strict = ConfigurationResolver.builder()
                .resourceProperties(Map.of("strictOnConversion", true))
                .build();

        String json = """
                {
                  "_type": "%s#//Person",
                  "name": "Alice",
                  "age": 42
                }
                """.formatted(testPackage.getNsURI());

        EObject person = load(json, strict).getContents().get(0);
        assertEquals("Alice", person.eGet(nameAttribute));
        assertEquals(42, person.eGet(ageAttribute));
    }

    private CodecResource load(String json, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://strict.json"),
                metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource;
    }

    @Test
    @DisplayName("an id that cannot be converted is escalated too")
    void strictCoversTheIdPath() {
        ConfigurationResolver strict = ConfigurationResolver.builder()
                .resourceProperties(Map.of("strictOnConversion", true))
                .build();

        // Person.id is an EString, so use a class whose id is numeric: the age attribute
        // stands in as a compound id feature that cannot take a non-numeric value
        String json = """
                {
                  "_type": "%s#//Person",
                  "_id": "not-a-number",
                  "name": "Alice"
                }
                """.formatted(testPackage.getNsURI());

        ConfigurationResolver numericId = ConfigurationResolver.builder()
                .resourceProperties(Map.of(
                        "strictOnConversion", true,
                        "idFeatures", java.util.List.of("age")))
                .build();

        assertThrows(Exception.class, () -> load(json, numericId),
                "a failed id conversion loses identity and must be escalatable");

        // and without strictness the same document still loads
        ConfigurationResolver lenientNumericId = ConfigurationResolver.builder()
                .resourceProperties(Map.of("idFeatures", java.util.List.of("age")))
                .build();
        assertDoesNotThrow(() -> load(json, lenientNumericId),
                "the default stays lenient");
        assertNotNull(strict, "resolver built");
    }
}
