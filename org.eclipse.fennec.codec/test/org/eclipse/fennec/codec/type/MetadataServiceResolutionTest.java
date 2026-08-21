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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.module.CodecModule;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

/**
 * The supplied MetadataService is what type resolution consults — also when the caller drives
 * the mapper itself (issue #163).
 * <p>
 * The whiteboard is how a model gets published in Fennec: a component registers its EPackage
 * with a {@code MetadataService} and hands that service to the codec. Nothing in that path
 * touches the JVM-global {@code EPackage.Registry}, so a codec that consulted the global
 * registry could only read documents whose packages something else had also registered there —
 * invisible in the API, and a matter of luck at runtime.
 * </p>
 * <p>
 * The package is therefore <b>deliberately absent</b> from {@code EPackage.Registry.INSTANCE}
 * in every case below; the assertion in {@link #globalRegistryStaysOutOfIt()} states that as a
 * precondition rather than trusting it.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-type.md#63-type-resolution">Spec 06 §6.3</a>
 */
@DisplayName("Type resolution consults the supplied MetadataService")
class MetadataServiceResolutionTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/type/test-hint.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass vehicleClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, MetadataServiceResolutionTest.class);
        // No EPackage.Registry.INSTANCE registration: the whiteboard is the only publisher here
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        vehicleClass = EcoreHelper.getEClass(testPackage, "Vehicle");
    }

    @AfterEach
    void tearDown() {
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("the global registry really does not know this package")
    void globalRegistryStaysOutOfIt() {
        assertNull(EPackage.Registry.INSTANCE.getEPackage(testPackage.getNsURI()),
                "test precondition: the package must be published through the service only");
    }

    @Test
    @DisplayName("a mapper built from a CodecModule resolves the type through the service")
    void mapperPathResolvesThroughTheService() {
        // How an adapter embeds the codec when it owns the stream itself - no CodecResource,
        // so nothing seeds the resolution context on its behalf.
        CodecModule module = CodecModule.builder()
                .metadataService(metadataService)
                .resolver(ConfigurationResolver.defaults())
                .build();
        JsonMapper mapper = JsonMapper.builder().addModule(module).build();

        EObject decoded = mapper.readerFor(EObject.class).readValue(document("Car"));

        assertNotNull(decoded, "the document names its type, so it must decode");
        assertEquals("Car", decoded.eClass().getName(),
                "the type came from the service the mapper was configured with");
    }

    @Test
    @DisplayName("the same document through CodecResource resolves as well")
    void resourcePathResolvesThroughTheService() throws IOException {
        Resource resource = new CodecResource(URI.createURI("test/metadata-only.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        resource.load(new ByteArrayInputStream(document("Car").getBytes(UTF_8)), Map.of());

        assertEquals(1, resource.getContents().size(),
                "errors were: " + resource.getErrors());
        assertEquals("Car", resource.getContents().get(0).eClass().getName());
    }

    @Test
    @DisplayName("an expected supertype does not mask a resolvable subtype")
    void supertypeHintDoesNotMaskTheSubtype() throws IOException {
        Resource resource = new CodecResource(URI.createURI("test/metadata-only.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        resource.load(new ByteArrayInputStream(document("Car").getBytes(UTF_8)),
                Map.of(CodecResource.CODEC_ROOT_TYPE, vehicleClass));

        assertEquals("Car", resource.getContents().get(0).eClass().getName(),
                "with the type resolvable there is nothing for the hint to fall back to");
    }

    private String document(String className) {
        return "{ \"_type\": \"" + testPackage.getNsURI() + "#//" + className + "\","
                + " \"brand\": \"VW\", \"doors\": 5 }";
    }
}
