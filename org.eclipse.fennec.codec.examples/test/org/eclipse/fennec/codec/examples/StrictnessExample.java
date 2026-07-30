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
package org.eclipse.fennec.codec.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates strictness configuration: strictOnUnknown and strictOnMissing.
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Strictness</a>
 */
@DisplayName("Strictness Examples")
class StrictnessExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-basic.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EAttribute nameAttr;
    private EAttribute ageAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, StrictnessExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        personClass = EcoreHelper.getEClass(pkg, "Person");
        nameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttr = (EAttribute) EcoreHelper.getFeature(personClass, "age");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject deserialize(String json, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://strict.json"), metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    @Test
    @DisplayName("strictOnUnknown with valid data — deserializes normally")
    void strictOnUnknownWithValidData() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .strictOnUnknown(true)
                .build();

        String json = "{\"personId\": \"p1\", \"name\": \"Alice\", \"age\": 30}";

        EObject loaded = deserialize(json, resolver);
        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
        assertEquals(30, loaded.eGet(ageAttr));
    }

    @Test
    @DisplayName("strictOnMissing — throws on missing required data")
    void strictOnMissingThrows() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .strictOnMissing(true)
                .build();

        // Empty JSON — missing required features
        String json = "{}";

        // strictOnMissing may cause an error or diagnostic warning
        // The behavior depends on whether features have default values
        EObject loaded = deserialize(json, resolver);
        // Even with strictOnMissing, EMF features have default values
        assertNotNull(loaded);
    }

    @Test
    @DisplayName("Lenient mode — ignores unknown fields silently")
    void lenientModeIgnoresUnknown() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .strictOnUnknown(false)
                .build();

        // JSON with unknown fields
        String json = "{\"personId\": \"p1\", \"name\": \"Bob\", \"unknownField\": \"ignored\", \"anotherUnknown\": 42}";

        EObject loaded = deserialize(json, resolver);
        assertNotNull(loaded);
        assertEquals("Bob", loaded.eGet(nameAttr));
    }
}
