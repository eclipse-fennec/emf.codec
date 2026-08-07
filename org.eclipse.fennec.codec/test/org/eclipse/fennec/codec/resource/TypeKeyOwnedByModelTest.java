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
 * A model feature carrying the same name as the type key (issue #134, spec 06-type.md §6.1).
 * <p>
 * GeoJSON is the case the spec names: {@code "type": "Point"} is both the discriminator and a
 * property of the model. When {@code typeKey} is configured to {@code type}, the value has two
 * jobs - it selects the class <b>and</b> it is data the model declares a feature for.
 * </p>
 * <p>
 * The codec fills that feature in addition to resolving the type, so a round trip does not
 * lose it. This was implemented but untested, and its failure path only reached the logger.
 * </p>
 */
@DisplayName("Type key owned by the model")
class TypeKeyOwnedByModelTest {

    private static final String NS_URI = "http://test.example.org/geo/1.0";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass pointClass;
    private EAttribute typeAttribute;
    private EAttribute nameAttribute;

    @BeforeEach
    void setUp() {
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("geo");
        testPackage.setNsPrefix("geo");
        testPackage.setNsURI(NS_URI);

        pointClass = EcoreFactory.eINSTANCE.createEClass();
        pointClass.setName("Point");
        testPackage.getEClassifiers().add(pointClass);

        // The model declares 'type' itself - as GeoJSON does
        typeAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        typeAttribute.setName("type");
        typeAttribute.setEType(EcorePackage.Literals.ESTRING);
        pointClass.getEStructuralFeatures().add(typeAttribute);

        nameAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttribute.setName("name");
        nameAttribute.setEType(EcorePackage.Literals.ESTRING);
        pointClass.getEStructuralFeatures().add(nameAttribute);

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
    @DisplayName("the discriminator value also lands in the model's own feature")
    void typeValueFillsTheModelFeature() throws IOException {
        String json = "{\"type\":\"Point\",\"name\":\"somewhere\"}";

        EObject loaded = load(json);

        assertEquals("Point", loaded.eGet(typeAttribute),
                "the value selects the class and is data - it has to survive both jobs");
        assertEquals("somewhere", loaded.eGet(nameAttribute));
    }

    @Test
    @DisplayName("a model that owns the type key does not report it as unknown")
    void typeKeyIsNotUnknown() throws IOException {
        String json = "{\"type\":\"Point\",\"name\":\"somewhere\"}";

        CodecResource resource = loadInto(json);

        assertTrue(resource.getWarnings().stream()
                        .noneMatch(w -> w.getMessage().contains("Unknown feature 'type'")),
                "the model declares it, was: " + resource.getWarnings());
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject load(String json) throws IOException {
        return loadInto(json).getContents().get(0);
    }

    private CodecResource loadInto(String json) throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of("typeKey", "type", "typeStrategy", "NAME"))
                .build();

        CodecResource resource = new CodecResource(URI.createURI("test://geo.json"),
                metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, pointClass);
        options.put(CodecResource.CODEC_ROOT_SCHEMA, NS_URI);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource;
    }
}
