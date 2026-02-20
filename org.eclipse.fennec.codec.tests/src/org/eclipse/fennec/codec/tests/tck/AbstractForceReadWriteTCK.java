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
package org.eclipse.fennec.codec.tests.tck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for force read/write tests.
 * <p>
 * Tests that volatile/transient/derived features can be serialized
 * and deserialized when explicitly forced.
 */
public abstract class AbstractForceReadWriteTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-force.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass computedClass;
    private EAttribute nameAttr;
    private EAttribute derivedAttr;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractForceReadWriteTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        computedClass = EcoreHelper.getEClass(testPackage, "Computed");
        nameAttr = (EAttribute) EcoreHelper.getFeature(computedClass, "name");
        derivedAttr = (EAttribute) EcoreHelper.getFeature(computedClass, "derived");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("forceWrite serializes volatile/transient/derived feature")
    void forceWriteVolatileFeature() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .forceWrite(derivedAttr)
                .forceRead(derivedAttr)
                .serializeNull(true)
                .build();

        EObject computed = testPackage.getEFactoryInstance().create(computedClass);
        computed.eSet(nameAttr, "test-item");
        computed.eSet(derivedAttr, "computed-value");

        EObject loaded = roundTrip(computed, computedClass, config);

        assertNotNull(loaded);
        assertEquals("test-item", loaded.eGet(nameAttr));
        assertEquals("computed-value", loaded.eGet(derivedAttr));
    }

    @Test
    @DisplayName("forceRead deserializes volatile/transient/derived feature")
    void forceReadVolatileFeature() throws IOException {
        // Serialize with forceWrite
        ConfigurationResolver writeConfig = ConfigurationResolver.builder()
                .forceWrite(derivedAttr)
                .build();

        EObject computed = testPackage.getEFactoryInstance().create(computedClass);
        computed.eSet(nameAttr, "read-test");
        computed.eSet(derivedAttr, "forced-value");

        CodecResource saveResource = createResource(writeConfig);
        saveResource.getContents().add(computed);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        // Deserialize with forceRead
        ConfigurationResolver readConfig = ConfigurationResolver.builder()
                .forceRead(derivedAttr)
                .build();

        CodecResource loadResource = createResource(readConfig);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, computedClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);

        EObject loaded = loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
        assertNotNull(loaded);
        assertEquals("read-test", loaded.eGet(nameAttr));
        assertEquals("forced-value", loaded.eGet(derivedAttr));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://force." + getFileExtension()),
                metadataService, config,
                null, null, createFormatProvider());
    }

    private EObject roundTrip(EObject object, EClass rootEClass, ConfigurationResolver config) throws IOException {
        CodecResource saveResource = createResource(config);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = createResource(config);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }
}
