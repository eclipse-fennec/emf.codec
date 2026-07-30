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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates the three main type strategies: NAME, URI, and NONE.
 * <p>
 * TypeStrategy controls how type information is written to JSON for each EObject.
 *
 * @see <a href="docs/codec-v2-spec/06-type.md">Spec: Type Strategy</a>
 */
@DisplayName("Type Strategy Examples")
class TypeStrategyExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-hierarchy.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass canvasClass;
    private EClass circleClass;
    private EClass rectangleClass;

    private EAttribute titleAttr;
    private EReference shapesRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, TypeStrategyExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        canvasClass = EcoreHelper.getEClass(pkg, "Canvas");
        circleClass = EcoreHelper.getEClass(pkg, "Circle");
        rectangleClass = EcoreHelper.getEClass(pkg, "Rectangle");

        titleAttr = (EAttribute) EcoreHelper.getFeature(canvasClass, "title");
        shapesRef = (EReference) EcoreHelper.getFeature(canvasClass, "shapes");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    @SuppressWarnings("unchecked")
    private EObject createCanvas() {
        EObject canvas = pkg.getEFactoryInstance().create(canvasClass);
        canvas.eSet(titleAttr, "My Drawing");

        EObject circle = pkg.getEFactoryInstance().create(circleClass);
        circle.eSet(circleClass.getEStructuralFeature("name"), "Sun");
        circle.eSet(circleClass.getEStructuralFeature("color"), "yellow");
        circle.eSet(circleClass.getEStructuralFeature("radius"), 5.0);

        EObject rect = pkg.getEFactoryInstance().create(rectangleClass);
        rect.eSet(rectangleClass.getEStructuralFeature("name"), "House");
        rect.eSet(rectangleClass.getEStructuralFeature("color"), "red");
        rect.eSet(rectangleClass.getEStructuralFeature("width"), 10.0);
        rect.eSet(rectangleClass.getEStructuralFeature("height"), 8.0);

        List<EObject> shapes = (List<EObject>) canvas.eGet(shapesRef);
        shapes.add(circle);
        shapes.add(rect);

        return canvas;
    }

    private EObject roundTrip(EObject object, EClass rootType, ConfigurationResolver resolver) throws IOException {
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://type.json"), metadataService, resolver, null);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://type.json"), metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    @Test
    @DisplayName("TypeStrategy.NAME — uses simple class name")
    @SuppressWarnings("unchecked")
    void typeStrategyName() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .typeStrategy(TypeStrategy.NAME)
                .build();

        EObject canvas = createCanvas();
        EObject loaded = roundTrip(canvas, canvasClass, resolver);

        assertNotNull(loaded);
        assertEquals("My Drawing", loaded.eGet(titleAttr));
        List<EObject> shapes = (List<EObject>) loaded.eGet(shapesRef);
        assertEquals(2, shapes.size());
        assertEquals(circleClass, shapes.get(0).eClass());
        assertEquals(rectangleClass, shapes.get(1).eClass());
    }

    @Test
    @DisplayName("TypeStrategy.URI — uses full EClass URI")
    @SuppressWarnings("unchecked")
    void typeStrategyUri() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .typeStrategy(TypeStrategy.URI)
                .build();

        EObject canvas = createCanvas();
        EObject loaded = roundTrip(canvas, canvasClass, resolver);

        assertNotNull(loaded);
        List<EObject> shapes = (List<EObject>) loaded.eGet(shapesRef);
        assertEquals(2, shapes.size());
        assertEquals(circleClass, shapes.get(0).eClass());
        assertEquals(rectangleClass, shapes.get(1).eClass());
        assertEquals("Sun", shapes.get(0).eGet(circleClass.getEStructuralFeature("name")));
    }

    @Test
    @DisplayName("TypeStrategy.NONE — no type info, single non-polymorphic type")
    void typeStrategyNone() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .typeStrategy(TypeStrategy.NONE)
                .build();

        EObject circle = pkg.getEFactoryInstance().create(circleClass);
        circle.eSet(circleClass.getEStructuralFeature("name"), "Ball");
        circle.eSet(circleClass.getEStructuralFeature("radius"), 3.0);

        // Serialize
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://none.json"), metadataService, resolver, null);
        saveResource.getContents().add(circle);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);

        // Verify no _type in output
        assertTrue(!json.contains("\"_type\""), "NONE strategy should not emit _type field");

        // Deserialize with explicit root type
        CodecResource loadResource = new CodecResource(
                URI.createURI("test://none.json"), metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, circleClass);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        EObject loaded = loadResource.getContents().get(0);
        assertEquals("Ball", loaded.eGet(circleClass.getEStructuralFeature("name")));
        assertEquals(3.0, (Double) loaded.eGet(circleClass.getEStructuralFeature("radius")), 0.001);
    }
}
