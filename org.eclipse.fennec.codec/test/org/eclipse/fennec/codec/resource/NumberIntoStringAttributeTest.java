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
package org.eclipse.fennec.codec.resource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * A JSON number read into a string attribute becomes its text (issue #228).
 * <p>
 * The number paths returned a {@code Long} or {@code Double} whatever the target, so
 * {@code eSet} on an {@code EString} failed with a {@code ClassCastException} - a GeoJSON
 * {@code "id": 42}, which RFC 7946 §3.2 allows, could not be read. Converting a string into a
 * number attribute was always silent; the reverse now is too.
 * </p>
 * <p>
 * An integer keeps its digits exactly, which is what an id needs. A floating-point number may
 * arrive normalised ({@code 1.50} as {@code "1.5"}), since the parser can hand it over already
 * decoded; a model that needs the literal should type the attribute as a number.
 * </p>
 */
@DisplayName("A number into a string attribute")
class NumberIntoStringAttributeTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, NumberIntoStringAttributeTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @ParameterizedTest(name = "{0} -> \"{1}\"")
    @CsvSource(delimiter = '|', value = { "42|42", "-7|-7", "9007199254740993|9007199254740993", "1.50|1.5", "1e3|1000.0" })
    @DisplayName("a single-valued string attribute takes the number's text")
    void singleValued(String number, String expected) throws IOException {
        CodecResource resource = load("{\"name\":" + number + "}");

        assertEquals(expected, person(resource).eGet(personClass.getEStructuralFeature("name")));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', value = { "[1, 2.5, \"x\"]" })
    @DisplayName("a many-valued string attribute takes each number's text")
    void multiValued(String array) throws IOException {
        CodecResource resource = load("{\"tags\":" + array + "}");

        assertEquals(List.of("1", "2.5", "x"), person(resource).eGet(personClass.getEStructuralFeature("tags")));
    }

    private EObject person(CodecResource resource) {
        assertTrue(resource.getErrors().isEmpty(), () -> "errors=" + resource.getErrors());
        return resource.getContents().get(0);
    }

    private CodecResource load(String json) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://numbers.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options);
        return resource;
    }
}
