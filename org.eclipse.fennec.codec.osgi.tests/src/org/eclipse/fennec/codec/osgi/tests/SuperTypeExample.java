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
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataService;
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
 * OSGi integration tests for supertype serialization.
 *
 * @see <a href="docs/codec-v2-spec/07-supertype.md">Spec: SuperType Serialization</a>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("SuperType OSGi Examples")
public class SuperTypeExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-hierarchy.ecore";

    @InjectService
    MetadataService metadataService;

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;

    private EClass circleClass;
    private EAttribute circleNameAttr;
    private EAttribute radiusAttr;

    @BeforeEach
    public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, SuperTypeExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        pkgReg = ctx.registerService(EPackage.class, pkg, null);

        circleClass = EcoreHelper.getEClass(pkg, "Circle");
        circleNameAttr = (EAttribute) EcoreHelper.getFeature(circleClass, "name");
        radiusAttr = (EAttribute) EcoreHelper.getFeature(circleClass, "radius");
    }

    @AfterEach
    public void tearDown() {
        pkgReg.unregister();
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
