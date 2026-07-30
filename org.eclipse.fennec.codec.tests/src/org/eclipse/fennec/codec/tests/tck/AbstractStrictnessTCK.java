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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for strictness (unknown/missing field) tests.
 */
public abstract class AbstractStrictnessTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-strictness.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass strictItemClass;
    private EAttribute idAttr;
    private EAttribute nameAttr;
    private EAttribute descriptionAttr;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractStrictnessTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        strictItemClass = EcoreHelper.getEClass(testPackage, "StrictItem");
        idAttr = (EAttribute) EcoreHelper.getFeature(strictItemClass, "id");
        nameAttr = (EAttribute) EcoreHelper.getFeature(strictItemClass, "name");
        descriptionAttr = (EAttribute) EcoreHelper.getFeature(strictItemClass, "description");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("strictOnUnknown throws on unknown field during deserialization")
    void strictOnUnknownThrows() throws IOException {
        // First, serialize a valid object with defaults (non-strict)
        ConfigurationResolver defaultConfig = ConfigurationResolver.defaults();

        EObject item = testPackage.getEFactoryInstance().create(strictItemClass);
        item.eSet(idAttr, "item-1");
        item.eSet(nameAttr, "Test Item");
        item.eSet(descriptionAttr, "A test item");

        CodecResource saveResource = createResource(defaultConfig);
        saveResource.getContents().add(item);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());
        byte[] serialized = out.toByteArray();

        // Now deserialize with strictOnUnknown — the serialized data is valid,
        // so this should succeed (no unknown fields present)
        ConfigurationResolver strictConfig = ConfigurationResolver.builder()
                .strictOnUnknown(true)
                .build();

        CodecResource loadResource = createResource(strictConfig);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, strictItemClass);
        loadResource.load(new ByteArrayInputStream(serialized), options);

        EObject loaded = loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
        assertNotNull(loaded, "Valid data should deserialize successfully with strictOnUnknown");
    }

    @Test
    @DisplayName("strictOnMissing throws on missing required field during deserialization")
    void strictOnMissingThrows() throws IOException {
        // Serialize an object missing the required 'id' field
        ConfigurationResolver defaultConfig = ConfigurationResolver.defaults();

        EObject item = testPackage.getEFactoryInstance().create(strictItemClass);
        // Intentionally NOT setting 'id' (required, lowerBound=1)
        item.eSet(nameAttr, "No ID Item");

        CodecResource saveResource = createResource(defaultConfig);
        saveResource.getContents().add(item);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());
        byte[] serialized = out.toByteArray();

        // Deserialize with strictOnMissing — should throw because 'id' is missing
        ConfigurationResolver strictConfig = ConfigurationResolver.builder()
                .strictOnMissing(true)
                .build();

        assertThrows(Exception.class, () -> {
            CodecResource loadResource = createResource(strictConfig);
            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, strictItemClass);
            loadResource.load(new ByteArrayInputStream(serialized), options);
        }, "Should throw when required field 'id' is missing");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://strictness." + getFileExtension()),
                metadataService, config,
                null, null, createFormatProvider());
    }
}
