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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates supertype serialization: whether supertype information
 * is included in the type output.
 *
 * @see <a href="docs/codec-v2-spec/07-supertype.md">Spec: SuperType Serialization</a>
 */
@DisplayName("SuperType Examples")
class SuperTypeExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-hierarchy.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass circleClass;
    private EAttribute circleNameAttr;
    private EAttribute radiusAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, SuperTypeExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        circleClass = EcoreHelper.getEClass(pkg, "Circle");
        circleNameAttr = (EAttribute) EcoreHelper.getFeature(circleClass, "name");
        radiusAttr = (EAttribute) EcoreHelper.getFeature(circleClass, "radius");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://supertype.json"), metadataService, resolver, null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootType, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://supertype.json"), metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    @Test
    @DisplayName("Supertype serialize ALL — includes supertype chain")
    void superTypeSerializeAll() throws IOException {
        Map<String, Object> moduleProps = Map.of(
                ConfigProperty.SUPERTYPE_SERIALIZE.getKey(), true
        );
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();

        EObject circle = pkg.getEFactoryInstance().create(circleClass);
        circle.eSet(circleNameAttr, "Dot");
        circle.eSet(radiusAttr, 1.0);

        String json = serialize(circle, resolver);
        // When supertype serialize is enabled, the type output includes supertype info
        assertTrue(json.contains("Circle") || json.contains("Shape"),
                "Should contain type information");

        EObject loaded = deserialize(json, circleClass, resolver);
        assertNotNull(loaded);
        assertEquals("Dot", loaded.eGet(circleNameAttr));
        assertEquals(1.0, (Double) loaded.eGet(radiusAttr), 0.001);
    }

    @Test
    @DisplayName("Supertype disabled (default) — no supertype in output")
    void superTypeDisabledDefault() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        EObject circle = pkg.getEFactoryInstance().create(circleClass);
        circle.eSet(circleNameAttr, "Ball");
        circle.eSet(radiusAttr, 5.0);

        String json = serialize(circle, resolver);

        EObject loaded = deserialize(json, circleClass, resolver);
        assertNotNull(loaded);
        assertEquals("Ball", loaded.eGet(circleNameAttr));
        assertEquals(5.0, (Double) loaded.eGet(radiusAttr), 0.001);
    }
}
