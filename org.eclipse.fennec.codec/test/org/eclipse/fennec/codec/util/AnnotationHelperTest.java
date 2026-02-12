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
package org.eclipse.fennec.codec.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AnnotationHelper}.
 */
@DisplayName("AnnotationHelper Tests")
class AnnotationHelperTest {

    private static final String TEST_ECORE = "../ser/test-extended-metadata.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private EClass documentClass;
    private EAttribute documentTitleAttribute;
    private EAttribute pageCountAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(AnnotationHelperTest.class);
        testPackage = ecoreHelper.loadEcore(TEST_ECORE);

        documentClass = ecoreHelper.getEClass(testPackage, "Document");
        documentTitleAttribute = (EAttribute) ecoreHelper.getFeature(documentClass, "documentTitle");
        pageCountAttribute = (EAttribute) ecoreHelper.getFeature(documentClass, "pageCount");
    }

    @AfterEach
    void tearDown() {
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("getExtendedMetaDataName returns name from annotation")
    void getExtendedMetaDataNameReturnsNameFromAnnotation() {
        // documentTitle has ExtendedMetaData annotation with name="title"
        String name = AnnotationHelper.getExtendedMetaDataName(documentTitleAttribute);
        assertEquals("title", name);
    }

    @Test
    @DisplayName("getExtendedMetaDataName returns null when no annotation")
    void getExtendedMetaDataNameReturnsNullWhenNoAnnotation() {
        // pageCount has no ExtendedMetaData annotation
        String name = AnnotationHelper.getExtendedMetaDataName(pageCountAttribute);
        assertNull(name);
    }

    @Test
    @DisplayName("getExtendedMetaDataName returns null for null input")
    void getExtendedMetaDataNameReturnsNullForNullInput() {
        String name = AnnotationHelper.getExtendedMetaDataName((EAttribute) null);
        assertNull(name);
    }

    @Test
    @DisplayName("getAnnotationDetail returns value from annotation")
    void getAnnotationDetailReturnsValueFromAnnotation() {
        String name = AnnotationHelper.getAnnotationDetail(
                documentTitleAttribute,
                AnnotationHelper.EXTENDED_METADATA_SOURCE,
                "name");
        assertEquals("title", name);
    }

    @Test
    @DisplayName("getAnnotationDetail returns null for missing key")
    void getAnnotationDetailReturnsNullForMissingKey() {
        String value = AnnotationHelper.getAnnotationDetail(
                documentTitleAttribute,
                AnnotationHelper.EXTENDED_METADATA_SOURCE,
                "nonexistent");
        assertNull(value);
    }

    @Test
    @DisplayName("getAnnotationDetail returns null for missing annotation")
    void getAnnotationDetailReturnsNullForMissingAnnotation() {
        String value = AnnotationHelper.getAnnotationDetail(
                pageCountAttribute,
                AnnotationHelper.EXTENDED_METADATA_SOURCE,
                "name");
        assertNull(value);
    }

    @Test
    @DisplayName("hasAnnotation returns true when annotation exists")
    void hasAnnotationReturnsTrueWhenAnnotationExists() {
        assertTrue(AnnotationHelper.hasAnnotation(
                documentTitleAttribute,
                AnnotationHelper.EXTENDED_METADATA_SOURCE));
    }

    @Test
    @DisplayName("hasAnnotation returns false when annotation missing")
    void hasAnnotationReturnsFalseWhenAnnotationMissing() {
        assertFalse(AnnotationHelper.hasAnnotation(
                pageCountAttribute,
                AnnotationHelper.EXTENDED_METADATA_SOURCE));
    }

    @Test
    @DisplayName("hasAnnotation returns false for null element")
    void hasAnnotationReturnsFalseForNullElement() {
        assertFalse(AnnotationHelper.hasAnnotation(null, AnnotationHelper.EXTENDED_METADATA_SOURCE));
    }
}
