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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
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
 * Abstract TCK for enum serialization strategy tests.
 * <p>
 * Tests VALUE (ordinal) and NAME enum strategies.
 */
public abstract class AbstractEnumStrategyTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass taskClass;
    private EAttribute nameAttr;
    private EAttribute statusAttr;
    private EEnum statusEnum;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractEnumStrategyTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        taskClass = EcoreHelper.getEClass(testPackage, "Task");
        nameAttr = (EAttribute) EcoreHelper.getFeature(taskClass, "name");
        statusAttr = (EAttribute) EcoreHelper.getFeature(taskClass, "status");
        statusEnum = (EEnum) testPackage.getEClassifier("Status");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("enum VALUE (ordinal) strategy round-trip")
    void enumValueStrategy() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .moduleProperties(Map.of("enumSerialization", "VALUE"))
                .build();

        EObject task = testPackage.getEFactoryInstance().create(taskClass);
        task.eSet(nameAttr, "Test Task");
        EEnumLiteral activeLiteral = statusEnum.getEEnumLiteral("ACTIVE");
        task.eSet(statusAttr, activeLiteral.getInstance());

        EObject loaded = roundTrip(task, taskClass, config);

        assertNotNull(loaded);
        assertEquals("Test Task", loaded.eGet(nameAttr));
        Object loadedStatus = loaded.eGet(statusAttr);
        assertNotNull(loadedStatus);
        assertSame(activeLiteral.getInstance(), loadedStatus,
                "Enum should round-trip to same ACTIVE instance via VALUE strategy");
    }

    @Test
    @DisplayName("enum NAME strategy round-trip")
    void enumNameStrategy() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .moduleProperties(Map.of("enumSerialization", "NAME"))
                .build();

        EObject task = testPackage.getEFactoryInstance().create(taskClass);
        task.eSet(nameAttr, "Test Task");
        EEnumLiteral activeLiteral = statusEnum.getEEnumLiteral("ACTIVE");
        task.eSet(statusAttr, activeLiteral.getInstance());

        EObject loaded = roundTrip(task, taskClass, config);

        assertNotNull(loaded);
        assertEquals("Test Task", loaded.eGet(nameAttr));
        Object loadedStatus = loaded.eGet(statusAttr);
        assertNotNull(loadedStatus);
        assertSame(activeLiteral.getInstance(), loadedStatus,
                "Enum should round-trip to same ACTIVE instance via NAME strategy");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://enum." + getFileExtension()),
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
        assertNoDiagnostics(loadResource);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    /**
     * Fails when a round trip reported problems (issue #131).
     * <p>
     * Deserialization catches, logs and continues, so a load succeeds even when a value was
     * dropped. The diagnostics are the only trace - a test that ignores them cannot tell a
     * clean round trip from a lossy one.
     * </p>
     */
    private static void assertNoDiagnostics(CodecResource resource) {
        assertTrue(resource.getErrors().isEmpty(),
                "round trip reported errors: " + resource.getErrors());
        assertTrue(resource.getWarnings().isEmpty(),
                "round trip reported warnings: " + resource.getWarnings());
    }
}
