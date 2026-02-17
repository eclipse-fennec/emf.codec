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
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for ID strategy tests.
 * <p>
 * Tests ID_FIELD, COMBINED, and IdKeyMode (BOTH, NONE) strategies.
 */
public abstract class AbstractIdStrategyTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass idHolderClass;
    private EAttribute entityIdAttr;
    private EAttribute labelAttr;

    private EClass multiIdHolderClass;
    private EAttribute firstNameAttr;
    private EAttribute lastNameAttr;
    private EAttribute sequenceAttr;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(AbstractIdStrategyTCK.class);
        testPackage = ecoreHelper.loadEcoreAbsolute(TEST_ECORE);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        idHolderClass = ecoreHelper.getEClass(testPackage, "IdHolder");
        entityIdAttr = (EAttribute) ecoreHelper.getFeature(idHolderClass, "entityId");
        labelAttr = (EAttribute) ecoreHelper.getFeature(idHolderClass, "label");

        multiIdHolderClass = ecoreHelper.getEClass(testPackage, "MultiIdHolder");
        firstNameAttr = (EAttribute) ecoreHelper.getFeature(multiIdHolderClass, "firstName");
        lastNameAttr = (EAttribute) ecoreHelper.getFeature(multiIdHolderClass, "lastName");
        sequenceAttr = (EAttribute) ecoreHelper.getFeature(multiIdHolderClass, "sequence");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("ID_FIELD strategy preserves ID")
    void idFieldStrategy() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .idStrategy("ID_FIELD")
                .build();

        EObject holder = testPackage.getEFactoryInstance().create(idHolderClass);
        holder.eSet(entityIdAttr, "entity-42");
        holder.eSet(labelAttr, "Test Entity");

        EObject loaded = roundTrip(holder, idHolderClass, config);

        assertNotNull(loaded);
        assertEquals("entity-42", loaded.eGet(entityIdAttr));
        assertEquals("Test Entity", loaded.eGet(labelAttr));
    }

    @Test
    @DisplayName("COMBINED ID strategy round-trip")
    void combinedIdStrategy() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .idStrategy("COMBINED")
                .idFeatures("firstName", "lastName", "sequence")
                .idSeparator("-")
                .build();

        EObject holder = testPackage.getEFactoryInstance().create(multiIdHolderClass);
        holder.eSet(firstNameAttr, "John");
        holder.eSet(lastNameAttr, "Doe");
        holder.eSet(sequenceAttr, 1);

        EObject loaded = roundTrip(holder, multiIdHolderClass, config);

        assertNotNull(loaded);
        assertEquals("John", loaded.eGet(firstNameAttr));
        assertEquals("Doe", loaded.eGet(lastNameAttr));
        assertEquals(1, loaded.eGet(sequenceAttr));
    }

    @Test
    @DisplayName("IdKeyMode BOTH preserves ID and attributes")
    void idKeyModeBoth() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .idStrategy("ID_FIELD")
                .idKeyMode("BOTH")
                .build();

        EObject holder = testPackage.getEFactoryInstance().create(idHolderClass);
        holder.eSet(entityIdAttr, "both-id");
        holder.eSet(labelAttr, "Both Mode");

        EObject loaded = roundTrip(holder, idHolderClass, config);

        assertNotNull(loaded);
        assertEquals("both-id", loaded.eGet(entityIdAttr));
        assertEquals("Both Mode", loaded.eGet(labelAttr));
    }

    @Test
    @DisplayName("IdKeyMode NONE preserves attributes without ID key")
    void idKeyModeNone() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .idStrategy("ID_FIELD")
                .idKeyMode("NONE")
                .build();

        EObject holder = testPackage.getEFactoryInstance().create(idHolderClass);
        holder.eSet(entityIdAttr, "none-id");
        holder.eSet(labelAttr, "None Mode");

        EObject loaded = roundTrip(holder, idHolderClass, config);

        assertNotNull(loaded);
        assertEquals("none-id", loaded.eGet(entityIdAttr));
        assertEquals("None Mode", loaded.eGet(labelAttr));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://idstrategy." + getFileExtension()),
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
