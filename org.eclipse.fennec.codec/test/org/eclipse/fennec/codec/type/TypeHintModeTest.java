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
package org.eclipse.fennec.codec.type;

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
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.metadata.model.codec.TypeHintMode;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@code codec.typeHintMode}: whether {@code CODEC_ROOT_TYPE} is a hint or a directive
 * (issue #173, spec 06-type.md §6.5.1, 13-load-save-options.md §5.2 and §2.11).
 * <p>
 * {@code HINT} is the default and the subject of {@link TypeHintPrecedenceTest}: the document
 * decides, because a subtype in the data must be able to beat a root hint. {@code OVERRIDE} is
 * the deliberate opposite, for the caller who is re-reading data against a type they have
 * chosen - schema migration being the case the spec names. It only ever applies to the root
 * object: nested objects keep resolving against their reference type, so a container read under
 * OVERRIDE does not flatten its polymorphic contents.
 * </p>
 */
@DisplayName("Type hint mode: hint or directive")
class TypeHintModeTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/type/test-hint.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass carClass;
    private EClass motorcycleClass;
    private EClass garageClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, TypeHintModeTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        carClass = EcoreHelper.getEClass(testPackage, "Car");
        motorcycleClass = EcoreHelper.getEClass(testPackage, "Motorcycle");
        garageClass = EcoreHelper.getEClass(testPackage, "Garage");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("HINT is the default: the document still decides")
    void hintIsTheDefault() throws IOException {
        CodecResource resource = load(vehicle("Motorcycle", "\"engineCC\": 900"), carClass, null);

        assertEquals(motorcycleClass, single(resource).eClass(),
                "without OVERRIDE nothing about the documented precedence may change");
    }

    @Test
    @DisplayName("OVERRIDE makes the caller's root type win over the document")
    void overrideMakesTheRootTypeWin() throws IOException {
        CodecResource resource = load(vehicle("Motorcycle", "\"engineCC\": 900"), carClass,
                TypeHintMode.OVERRIDE);

        assertEquals(carClass, single(resource).eClass(),
                "OVERRIDE is the caller saying: read this as the type I named");
    }

    @Test
    @DisplayName("OVERRIDE accepts the mode as a plain string too")
    void overrideAcceptsAStringValue() throws IOException {
        CodecResource resource = load(vehicle("Motorcycle", "\"engineCC\": 900"), carClass,
                "OVERRIDE");

        assertEquals(carClass, single(resource).eClass(),
                "the option is documented as a String key; both spellings have to work");
    }

    @Test
    @DisplayName("OVERRIDE says which type it discarded")
    void overrideNamesWhatItDiscarded() throws IOException {
        CodecResource resource = load(vehicle("Motorcycle", "\"engineCC\": 900"), carClass,
                TypeHintMode.OVERRIDE);

        assertTrue(resource.getWarnings().stream()
                        .anyMatch(w -> w.getMessage().contains("Motorcycle")
                                && w.getMessage().contains("Car")),
                "overruling the document is legitimate but never silent, was: "
                        + resource.getWarnings());
    }

    @Test
    @DisplayName("OVERRIDE agreeing with the document is silent")
    void overrideAgreeingWithTheDocumentIsSilent() throws IOException {
        CodecResource resource = load(vehicle("Car", "\"doors\": 5"), carClass,
                TypeHintMode.OVERRIDE);

        assertEquals(carClass, single(resource).eClass());
        assertTrue(resource.getWarnings().isEmpty(),
                "nothing was overruled, so there is nothing to report, was: "
                        + resource.getWarnings());
    }

    @Test
    @DisplayName("OVERRIDE does not reach nested objects")
    @SuppressWarnings("unchecked")
    void overrideDoesNotReachNestedObjects() throws IOException {
        String json = "{ \"name\": \"Bob's\", \"vehicles\": ["
                + "{ \"_type\": \"" + uriOf("Car") + "\", \"doors\": 3 },"
                + "{ \"_type\": \"" + uriOf("Motorcycle") + "\", \"engineCC\": 600 }"
                + "] }";

        CodecResource resource = load(json, garageClass, TypeHintMode.OVERRIDE);

        EObject garage = single(resource);
        assertEquals(garageClass, garage.eClass());
        List<EObject> vehicles = (List<EObject>) garage.eGet(
                garageClass.getEStructuralFeature("vehicles"));
        assertEquals(2, vehicles.size());
        assertEquals(carClass, vehicles.get(0).eClass(),
                "CODEC_ROOT_TYPE names the root; contained objects keep their own types");
        assertEquals(motorcycleClass, vehicles.get(1).eClass(),
                "a container read under OVERRIDE must not flatten its polymorphic contents");
    }

    @Test
    @DisplayName("OVERRIDE without a root type is inert and reported")
    void overrideWithoutARootTypeIsReported() throws IOException {
        CodecResource resource = load(vehicle("Car", "\"doors\": 3"), null, TypeHintMode.OVERRIDE);

        assertEquals(carClass, single(resource).eClass(),
                "there is nothing to override with, so the document stands");
        assertTrue(resource.getWarnings().stream()
                        .anyMatch(w -> w.getMessage().contains("OVERRIDE")
                                && w.getMessage().contains("CODEC_ROOT_TYPE")),
                "an option that cannot do anything has to say so, was: " + resource.getWarnings());
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private String uriOf(String className) {
        return testPackage.getNsURI() + "#//" + className;
    }

    private String vehicle(String className, String attribute) {
        return "{ \"_type\": \"" + uriOf(className) + "\", \"brand\": \"VW\", " + attribute + " }";
    }

    private CodecResource load(String json, EClass rootType, Object hintMode) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("hint-mode.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        Map<Object, Object> options = new HashMap<>();
        if (rootType != null) {
            options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        }
        if (hintMode != null) {
            options.put(CodecOptions.CODEC_TYPE_HINT_MODE, hintMode);
        }
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options);
        return resource;
    }

    private EObject single(CodecResource resource) {
        assertEquals(1, resource.getContents().size(),
                "expected exactly one root, errors were: " + resource.getErrors());
        return resource.getContents().get(0);
    }
}
