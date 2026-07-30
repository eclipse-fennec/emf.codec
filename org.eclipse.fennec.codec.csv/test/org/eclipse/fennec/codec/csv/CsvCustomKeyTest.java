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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the per-feature {@code key} override (via {@code @codec(key="...")}
 * EAnnotation) renames the CSV column header.
 */
@DisplayName("CSV CustomKey option")
class CsvCustomKeyTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass productClass;
    private EAttribute productIdAttr;
    private EAttribute productNameAttr;
    private EAttribute customKeyAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        productClass = EcoreHelper.getEClass(testPackage, "Product");
        productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
        productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");
        customKeyAttr = (EAttribute) EcoreHelper.getFeature(productClass, "customKeyAttr");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private List<String> lines(String csv) {
        String trimmed = csv.endsWith("\n") ? csv.substring(0, csv.length() - 1) : csv;
        return List.of(trimmed.split("\n", -1));
    }

    @Test
    @DisplayName("renames column via @codec(key=...) annotation")
    void renamesColumnViaAnnotation() throws IOException {
        EObject product = testPackage.getEFactoryInstance().create(productClass);
        product.eSet(productIdAttr, "p-001");
        product.eSet(productNameAttr, "Custom Key Product");
        product.eSet(customKeyAttr, "custom-value");

        CodecResource resource = new CodecResource(
                URI.createURI("test://customkey.csv"),
                metadataService, ConfigurationResolver.defaults(),
                null, null, new CsvFormatProvider(productClass));
        resource.getContents().add(product);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.save(baos, Collections.emptyMap());

        List<String> rows = lines(baos.toString(StandardCharsets.UTF_8));
        assertEquals(3, rows.size());

        String header = rows.get(0);
        assertTrue(header.contains("custom_name"), () -> "expected renamed column 'custom_name' in header: " + header);
        assertFalse(header.contains("customKeyAttr"),
                () -> "EMF feature name 'customKeyAttr' should be replaced: " + header);

        // Data row should still carry the value (under the renamed column)
        String data = rows.get(2);
        assertTrue(data.contains("custom-value"), data);
    }
}
