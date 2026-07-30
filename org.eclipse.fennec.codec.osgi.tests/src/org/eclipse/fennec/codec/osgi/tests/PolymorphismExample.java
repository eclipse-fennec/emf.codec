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
package org.eclipse.fennec.codec.osgi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * OSGi integration tests for polymorphic containment and non-containment references.
 *
 * @see <a href="docs/codec-v2-spec/06-type.md">Spec: Type Strategy</a>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("Polymorphism OSGi Examples")
public class PolymorphismExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-hierarchy.ecore";

    @InjectService
    MetadataService metadataService;

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;

    private EClass canvasClass;
    private EClass circleClass;
    private EClass rectangleClass;

    private EAttribute titleAttr;
    private EReference shapesRef;
    private EReference featuredRef;

    @BeforeEach
    public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, PolymorphismExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        pkgReg = ctx.registerService(EPackage.class, pkg, null);

        canvasClass = EcoreHelper.getEClass(pkg, "Canvas");
        circleClass = EcoreHelper.getEClass(pkg, "Circle");
        rectangleClass = EcoreHelper.getEClass(pkg, "Rectangle");

        titleAttr = (EAttribute) EcoreHelper.getFeature(canvasClass, "title");
        shapesRef = (EReference) EcoreHelper.getFeature(canvasClass, "shapes");
        featuredRef = (EReference) EcoreHelper.getFeature(canvasClass, "featured");
    }

    @AfterEach
    public void tearDown() {
        pkgReg.unregister();
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject roundTrip(EObject object, EClass rootType) throws IOException {
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://poly.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://poly.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    @Test
    @DisplayName("Polymorphic containment list — mixed Circle and Rectangle")
    @SuppressWarnings("unchecked")
    void polymorphicContainmentList() throws IOException {
        EObject canvas = pkg.getEFactoryInstance().create(canvasClass);
        canvas.eSet(titleAttr, "Shapes Demo");

        EObject circle = pkg.getEFactoryInstance().create(circleClass);
        circle.eSet(circleClass.getEStructuralFeature("name"), "Sun");
        circle.eSet(circleClass.getEStructuralFeature("color"), "yellow");
        circle.eSet(circleClass.getEStructuralFeature("radius"), 5.0);

        EObject rect = pkg.getEFactoryInstance().create(rectangleClass);
        rect.eSet(rectangleClass.getEStructuralFeature("name"), "Box");
        rect.eSet(rectangleClass.getEStructuralFeature("color"), "blue");
        rect.eSet(rectangleClass.getEStructuralFeature("width"), 10.0);
        rect.eSet(rectangleClass.getEStructuralFeature("height"), 6.0);

        List<EObject> shapes = (List<EObject>) canvas.eGet(shapesRef);
        shapes.add(circle);
        shapes.add(rect);

        EObject loaded = roundTrip(canvas, canvasClass);

        assertNotNull(loaded);
        assertEquals("Shapes Demo", loaded.eGet(titleAttr));

        List<EObject> loadedShapes = (List<EObject>) loaded.eGet(shapesRef);
        assertEquals(2, loadedShapes.size());

        assertEquals(circleClass, loadedShapes.get(0).eClass());
        assertEquals("Sun", loadedShapes.get(0).eGet(circleClass.getEStructuralFeature("name")));
        assertEquals(5.0, (Double) loadedShapes.get(0).eGet(circleClass.getEStructuralFeature("radius")), 0.001);

        assertEquals(rectangleClass, loadedShapes.get(1).eClass());
        assertEquals("Box", loadedShapes.get(1).eGet(rectangleClass.getEStructuralFeature("name")));
        assertEquals(10.0, (Double) loadedShapes.get(1).eGet(rectangleClass.getEStructuralFeature("width")), 0.001);
    }

    @Test
    @DisplayName("Polymorphic non-containment reference — featured shape")
    @SuppressWarnings("unchecked")
    void polymorphicNonContainmentRef() throws IOException {
        EObject canvas = pkg.getEFactoryInstance().create(canvasClass);
        canvas.eSet(titleAttr, "Featured");

        EObject circle = pkg.getEFactoryInstance().create(circleClass);
        circle.eSet(circleClass.getEStructuralFeature("name"), "Star");
        circle.eSet(circleClass.getEStructuralFeature("radius"), 3.0);

        List<EObject> shapes = (List<EObject>) canvas.eGet(shapesRef);
        shapes.add(circle);
        canvas.eSet(featuredRef, circle);

        EObject loaded = roundTrip(canvas, canvasClass);

        assertNotNull(loaded);
        List<EObject> loadedShapes = (List<EObject>) loaded.eGet(shapesRef);
        EObject loadedFeatured = (EObject) loaded.eGet(featuredRef);

        assertNotNull(loadedFeatured);
        assertSame(loadedShapes.get(0), loadedFeatured,
                "Featured should be same instance as contained shape");
    }
}
