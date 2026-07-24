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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that a <b>String</b> {@code CODEC_ROOT_TYPE} (a full type URI) resolves for a
 * single registered package version (issue #54, A.3 "exactly one candidate" path, R1).
 * <p>
 * The package is placed in a {@link Resource} whose URI is the package nsURI, so
 * {@code EcoreUtil.getURI(eClass)} — and hence the MetadataService type-URI index — yields the
 * canonical {@code nsURI#//ClassName} form, exactly as for generated/registry-backed models.
 * </p>
 */
@DisplayName("String root type URI resolution (single version)")
class StringRootTypeUriResolutionTest {

    private static final String NS_URI = "http://example.org/entity/1.0";

    private MetadataWhiteboard metadataService;
    private EClass entity;
    private EAttribute value;
    private String typeUri;

    @BeforeEach
    void setUp() {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("entity");
        pkg.setNsPrefix("entity");
        pkg.setNsURI(NS_URI);
        entity = EcoreFactory.eINSTANCE.createEClass();
        entity.setName("Entity");
        pkg.getEClassifiers().add(entity);
        value = EcoreFactory.eINSTANCE.createEAttribute();
        value.setName("value");
        value.setEType(EcorePackage.eINSTANCE.getEString());
        entity.getEStructuralFeatures().add(value);

        // Back the package with a Resource (URI = nsURI) so its type URI is the canonical
        // nsURI#//Entity, matching how the MetadataService indexes classes by URI. A bare
        // ResourceImpl suffices (no Resource.Factory needed for the http-scheme URI).
        Resource res = new ResourceImpl(URI.createURI(NS_URI));
        res.getContents().add(pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        typeUri = EcoreUtil.getURI(entity).toString();
    }

    @Test
    @DisplayName("String CODEC_ROOT_TYPE (type URI) resolves and deserializes")
    void stringRootTypeUriResolves() throws IOException {
        assertEquals(NS_URI + "#//Entity", typeUri, "sanity: canonical type URI");

        CodecResource resource = new CodecResource(
                URI.createURI("test://string-roottype.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        ByteArrayInputStream in = new ByteArrayInputStream(
                "{\"value\":\"X\"}".getBytes(StandardCharsets.UTF_8));
        resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, typeUri));

        assertNotNull(resource.getContents().isEmpty() ? null : resource.getContents().get(0));
        EObject obj = resource.getContents().get(0);
        assertSame(entity, obj.eClass());
        assertEquals("X", obj.eGet(value));
    }
}
