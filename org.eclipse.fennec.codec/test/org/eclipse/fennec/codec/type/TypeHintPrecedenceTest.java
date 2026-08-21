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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Who decides the type: the document or the caller's hint (issue #160).
 * <p>
 * The document decides. A hint says what the caller <b>expects</b> — the collection it is
 * reading, the declared type of a feature — and a discriminator says what the object
 * <b>is</b>. Letting the expectation win would narrow a subtype to its supertype on a read
 * path that reports success, so the hint only fills in where the document says nothing.
 * </p>
 * <p>
 * The two last cases are the ones worth keeping side by side: a type value that does not
 * resolve produces the <i>same visible outcome</i> as a hint that outranked the discriminator
 * — the hint's type, or nothing at all when there is no hint. What tells them apart is the
 * diagnostic, so it is asserted rather than assumed.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-type.md#63-type-resolution">Spec 06 §6.3</a>
 * @see <a href="docs/codec-v2-spec/06-type.md#632-unknown-type-handling">Spec 06 §6.3.2</a>
 */
@DisplayName("Type resolution: document over hint")
class TypeHintPrecedenceTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/type/test-hint.ecore";
    private static final String GHOST_TYPE = "http://ghost.example.org/1.0#//Ghost";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass vehicleClass;
    private EClass carClass;
    private EClass motorcycleClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, TypeHintPrecedenceTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        vehicleClass = EcoreHelper.getEClass(testPackage, "Vehicle");
        carClass = EcoreHelper.getEClass(testPackage, "Car");
        motorcycleClass = EcoreHelper.getEClass(testPackage, "Motorcycle");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("a discriminator outranks a hint naming a different concrete type")
    void discriminatorOutranksConcreteHint() throws IOException {
        Resource resource = load(document("Motorcycle", "\"engineCC\": 900"), carClass);

        EObject root = single(resource);
        assertEquals("Motorcycle", root.eClass().getName(),
                "the document says what the object is; the hint only says what was expected");
        assertEquals(900, root.eGet(motorcycleClass.getEStructuralFeature("engineCC")),
                "the subtype's own attribute must survive");
    }

    @Test
    @DisplayName("a discriminator outranks a hint naming the supertype")
    void discriminatorOutranksSupertypeHint() throws IOException {
        // The shape a collection-per-supertype store produces: every document is read with the
        // collection's type as the hint, and each one carries its own concrete type.
        Resource resource = load(document("Car", "\"doors\": 5"), vehicleClass);

        EObject root = single(resource);
        assertEquals("Car", root.eClass().getName(), "reading through the supertype must not narrow");
        assertEquals(5, root.eGet(carClass.getEStructuralFeature("doors")));
    }

    @Test
    @DisplayName("a discriminator alone is enough, no hint needed")
    void discriminatorAloneIsEnough() throws IOException {
        Resource resource = load(document("Car", "\"doors\": 3"), null);

        assertEquals("Car", single(resource).eClass().getName());
    }

    @Test
    @DisplayName("the hint fills in when the document carries no type")
    void hintFillsInWithoutDiscriminator() throws IOException {
        Resource resource = load("{ \"brand\": \"VW\", \"doors\": 3 }", carClass);

        assertEquals("Car", single(resource).eClass().getName(),
                "without a discriminator the hint is all there is");
    }

    @Test
    @DisplayName("a short type name resolves against the hint's package")
    void shortNameResolvesAgainstTheHintsPackage() throws IOException {
        Resource resource = load("{ \"_type\": \"Car\", \"brand\": \"VW\", \"doors\": 5 }", vehicleClass);

        assertEquals("Car", single(resource).eClass().getName());
    }

    @Test
    @DisplayName("an unresolvable type value falls back to the hint and says so")
    void unresolvableTypeFallsBackAndReports() throws IOException {
        Resource resource = load("{ \"_type\": \"" + GHOST_TYPE + "\", \"brand\": \"VW\" }", carClass);

        assertEquals("Car", single(resource).eClass().getName(),
                "spec 06 §6.3.2: unknown type value with a hint available uses the hint");
        assertTrue(resource.getWarnings().stream()
                        .anyMatch(w -> w.getMessage().contains(GHOST_TYPE)),
                "the fallback must be reported, otherwise it is indistinguishable from a document"
                        + " that named the hint's type, warnings were: " + resource.getWarnings());
    }

    @Test
    @DisplayName("an unresolvable type value without a hint fails, it does not guess")
    void unresolvableTypeWithoutHintFails() throws IOException {
        Resource resource = load("{ \"_type\": \"" + GHOST_TYPE + "\", \"brand\": \"VW\" }", null);

        assertTrue(resource.getContents().isEmpty(), "nothing may be invented for an unknown type");
        assertFalse(resource.getErrors().isEmpty(),
                "spec 06 §6.3.2: unknown type value without a hint is an ERROR");
    }

    private String document(String className, String attribute) {
        return "{ \"_type\": \"" + testPackage.getNsURI() + "#//" + className + "\","
                + " \"brand\": \"VW\", " + attribute + " }";
    }

    private Resource load(String json, EClass hint) throws IOException {
        Resource resource = new CodecResource(URI.createURI("test/hint-precedence.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        Map<Object, Object> options = hint == null
                ? Map.of()
                : Map.of(CodecResource.CODEC_ROOT_TYPE, hint);
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options);
        return resource;
    }

    private EObject single(Resource resource) {
        assertEquals(1, resource.getContents().size(),
                "expected exactly one root, errors were: " + resource.getErrors());
        return resource.getContents().get(0);
    }
}
