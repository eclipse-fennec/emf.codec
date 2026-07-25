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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
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
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Supertype compression must compare package <b>instances</b>, not nsURI strings.
 * <p>
 * Two versions of one model share an nsURI, so a string comparison calls them the same schema
 * and compresses a cross-version supertype to a bare name. The reader then resolves that name
 * against the version pinned for the nsURI — which is the other version. A bare name is only
 * safe when the supertype really lives in the same package object.
 * </p>
 */
@DisplayName("SuperType compression under multi-version (W4/S5)")
class SuperTypeMultiVersionCompressionTest {

    private static final String NS = "http://example.org/supertype/1.0";

    private MetadataWhiteboard metadataService;
    private EPackage versionOne;
    private EPackage versionTwo;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();
        versionOne = buildVersion("baseV1");
        versionTwo = buildVersion("baseV2");
        metadataService.registerPackage(versionOne);
        metadataService.registerPackage(versionTwo);
    }

    /** A package with Base and Derived, where Derived extends Base of the same package. */
    private static EPackage buildVersion(String baseAttributeName) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("supertype");
        pkg.setNsPrefix("st");
        pkg.setNsURI(NS);

        EClass base = EcoreFactory.eINSTANCE.createEClass();
        base.setName("Base");
        pkg.getEClassifiers().add(base);
        EAttribute baseAttr = EcoreFactory.eINSTANCE.createEAttribute();
        baseAttr.setName(baseAttributeName);
        baseAttr.setEType(EcorePackage.eINSTANCE.getEString());
        base.getEStructuralFeatures().add(baseAttr);

        EClass derived = EcoreFactory.eINSTANCE.createEClass();
        derived.setName("Derived");
        derived.getESuperTypes().add(base);
        pkg.getEClassifiers().add(derived);

        new ResourceImpl(URI.createURI(NS)).getContents().add(pkg);
        return pkg;
    }

    private String serialize(EObject object) throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(Map.of(
                        "smartCompression", true,
                        "superTypeSerialize", true))
                .build();

        CodecResource resource = new CodecResource(
                URI.createURI("test://supertype-multiversion.json"),
                metadataService, resolver, null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("a supertype in the same package instance is compressed to a bare name")
    void sameInstanceIsCompressed() throws IOException {
        EClass derived = (EClass) versionOne.getEClassifier("Derived");
        EObject object = versionOne.getEFactoryInstance().create(derived);

        String json = serialize(object);

        assertTrue(json.contains("\"_supertype\":[\"Base\"]"),
                "same package instance is genuinely the same schema: " + json);
    }

    @Test
    @DisplayName("a supertype in another version of the same nsURI keeps its full URI")
    void crossVersionSuperTypeIsNotCompressed() throws IOException {
        // Derived from version two inherits Base from version one: identical nsURI, different
        // package object. This is what a string comparison cannot tell apart.
        EClass derivedV2 = (EClass) versionTwo.getEClassifier("Derived");
        EClass baseV1 = (EClass) versionOne.getEClassifier("Base");
        derivedV2.getESuperTypes().clear();
        derivedV2.getESuperTypes().add(baseV1);
        metadataService.registerPackage(versionTwo);

        EObject object = versionTwo.getEFactoryInstance().create(derivedV2);

        String json = serialize(object);

        assertFalse(json.contains("\"_supertype\":[\"Base\"]"),
                "a bare name would be resolved against the pinned version, which is the wrong "
                + "package here: " + json);
        assertTrue(json.contains("\"_supertype\":[\"" + NS + "#//Base\"]"),
                "the supertype must be named by its full URI: " + json);
    }
}
