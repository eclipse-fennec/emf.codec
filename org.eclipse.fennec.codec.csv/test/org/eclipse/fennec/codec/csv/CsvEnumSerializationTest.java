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
package org.eclipse.fennec.codec.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the CSV exporter (now on the shared tabular pipeline) honors the
 * {@code enumSerialization} codec option just like the Jackson pipeline does:
 * {@code LITERAL} (default), {@code NAME}, and {@code VALUE}.
 * <p>
 * The {@code Status} enum is designed so all three differ for {@code ACTIVE}:
 * name {@code ACTIVE}, literal {@code Active}, value {@code 1}.
 */
@DisplayName("CSV enumSerialization option")
class CsvEnumSerializationTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";
    private static final String ENUM_SERIALIZATION_KEY = "enumSerialization";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass taskClass;
    private EAttribute taskNameAttr;
    private EAttribute statusAttr;
    private Object activeLiteral;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        taskClass = EcoreHelper.getEClass(testPackage, "Task");
        taskNameAttr = (EAttribute) EcoreHelper.getFeature(taskClass, "name");
        statusAttr = (EAttribute) EcoreHelper.getFeature(taskClass, "status");
        EEnum statusEnum = (EEnum) testPackage.getEClassifier("Status");
        // ACTIVE: name=ACTIVE, literal="Active", value=1 — non-default (PENDING is the default).
        activeLiteral = statusEnum.getEEnumLiteral("ACTIVE").getInstance();
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createTask() {
        EObject task = testPackage.getEFactoryInstance().create(taskClass);
        task.eSet(taskNameAttr, "Todo");
        task.eSet(statusAttr, activeLiteral);
        return task;
    }

    private String save(EObject task, ConfigurationResolver config) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("enum.csv"),
                metadataService, config,
                null, null, new CsvFormatProvider(taskClass));
        resource.getContents().add(task);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, Collections.emptyMap());
        return baos.toString(StandardCharsets.UTF_8);
    }

    /** Returns the data-row cell value for the given header column. */
    private String statusCell(String csv) {
        List<String> rows = List.of(
                (csv.endsWith("\n") ? csv.substring(0, csv.length() - 1) : csv).split("\n", -1));
        assertEquals(3, rows.size(), () -> "expected header + types + 1 data row: " + csv);
        List<String> header = List.of(rows.get(0).split(",", -1));
        int idx = header.indexOf("status");
        assertTrue(idx >= 0, () -> "status column should be present: " + rows.get(0));
        return List.of(rows.get(2).split(",", -1)).get(idx);
    }

    @Test
    @DisplayName("LITERAL (default) emits the enum literal")
    void literalByDefault() throws IOException {
        String csv = save(createTask(), ConfigurationResolver.defaults());
        assertEquals("Active", statusCell(csv));
    }

    @Test
    @DisplayName("NAME emits the enum constant name")
    void name() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .optionsProperties(Map.of(ENUM_SERIALIZATION_KEY, "NAME"))
                .build();
        String csv = save(createTask(), config);
        assertEquals("ACTIVE", statusCell(csv));
    }

    @Test
    @DisplayName("VALUE emits the enum integer value")
    void value() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .optionsProperties(Map.of(ENUM_SERIALIZATION_KEY, "VALUE"))
                .build();
        String csv = save(createTask(), config);
        assertEquals("1", statusCell(csv));
    }
}
