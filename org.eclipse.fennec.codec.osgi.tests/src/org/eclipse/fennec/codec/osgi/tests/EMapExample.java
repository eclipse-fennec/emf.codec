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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
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
 * OSGi integration tests for EMap serialization.
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Serialization (EMap)</a>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("EMap OSGi Examples")
public class EMapExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-emap.ecore";

    @InjectService
    MetadataService metadataService;

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;

    private EClass configClass;
    private EAttribute configNameAttr;
    private EReference propertiesRef;

    @BeforeEach
    public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, EMapExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        pkgReg = ctx.registerService(EPackage.class, pkg, null);

        configClass = EcoreHelper.getEClass(pkg, "Config");
        configNameAttr = (EAttribute) EcoreHelper.getFeature(configClass, "name");
        propertiesRef = (EReference) EcoreHelper.getFeature(configClass, "properties");
    }

    @AfterEach
    public void tearDown() {
        pkgReg.unregister();
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject roundTrip(EObject object, EClass rootType) throws IOException {
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://emap.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://emap.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    @Test
    @DisplayName("String map round-trip")
    @SuppressWarnings("unchecked")
    void stringMapRoundTrip() throws IOException {
        EObject config = pkg.getEFactoryInstance().create(configClass);
        config.eSet(configNameAttr, "AppConfig");

        EMap<String, String> props = (EMap<String, String>) config.eGet(propertiesRef);
        props.put("host", "localhost");
        props.put("port", "8080");
        props.put("debug", "true");

        EObject loaded = roundTrip(config, configClass);

        assertNotNull(loaded);
        assertEquals("AppConfig", loaded.eGet(configNameAttr));

        EMap<String, String> loadedProps = (EMap<String, String>) loaded.eGet(propertiesRef);
        assertEquals(3, loadedProps.size());
        assertEquals("localhost", loadedProps.get("host"));
        assertEquals("8080", loadedProps.get("port"));
        assertEquals("true", loadedProps.get("debug"));
    }

    @Test
    @DisplayName("Flatten EMap entries into parent object")
    @SuppressWarnings("unchecked")
    void flattenedMapSerialization() throws IOException {
        EObject config = pkg.getEFactoryInstance().create(configClass);
        config.eSet(configNameAttr, "llm-request");

        EMap<String, String> props = (EMap<String, String>) config.eGet(propertiesRef);
        props.put("reasoning_effort", "high");
        props.put("provider", "openai");

        Map<EReference, Map<String, Object>> refConfig = new HashMap<>();
        refConfig.put(propertiesRef, Map.of("flatten", "true"));

        CodecResource resource = new CodecResource(
                URI.createURI("out.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        resource.getContents().add(config);

        Map<String, Object> saveOptions = new HashMap<>();
        saveOptions.put("codec.eReferenceConfig", refConfig);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, saveOptions);
        String json = out.toString(StandardCharsets.UTF_8);

        assertTrue(json.contains("\"reasoning_effort\""),
                "Expected 'reasoning_effort' at root level in: " + json);
        assertTrue(json.contains("\"provider\""),
                "Expected 'provider' at root level in: " + json);
        assertFalse(json.contains("\"properties\""),
                "Expected no 'properties' wrapper key in: " + json);
    }

    @Test
    @DisplayName("Empty map round-trip")
    @SuppressWarnings("unchecked")
    void emptyMapRoundTrip() throws IOException {
        EObject config = pkg.getEFactoryInstance().create(configClass);
        config.eSet(configNameAttr, "EmptyConfig");

        EObject loaded = roundTrip(config, configClass);

        assertNotNull(loaded);
        assertEquals("EmptyConfig", loaded.eGet(configNameAttr));

        EMap<String, String> loadedProps = (EMap<String, String>) loaded.eGet(propertiesRef);
        assertTrue(loadedProps.isEmpty(), "Properties should be empty");
    }
}
