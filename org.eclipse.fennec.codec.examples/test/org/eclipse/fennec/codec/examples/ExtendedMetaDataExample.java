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
package org.eclipse.fennec.codec.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates ExtendedMetaData name resolution: when enabled, JSON keys use
 * the names from ExtendedMetaData annotations instead of the EMF feature names.
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Serialization</a>
 */
@DisplayName("ExtendedMetaData Examples")
class ExtendedMetaDataExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-extmetadata.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass articleClass;
    private EAttribute articleTitleAttr;
    private EAttribute articleBodyAttr;
    private EAttribute pageCountAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, ExtendedMetaDataExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        articleClass = EcoreHelper.getEClass(pkg, "Article");
        articleTitleAttr = (EAttribute) EcoreHelper.getFeature(articleClass, "articleTitle");
        articleBodyAttr = (EAttribute) EcoreHelper.getFeature(articleClass, "articleBody");
        pageCountAttr = (EAttribute) EcoreHelper.getFeature(articleClass, "pageCount");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://extmeta.json"), metadataService, resolver, null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://extmeta.json"), metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, articleClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private EObject createArticle() {
        EObject article = pkg.getEFactoryInstance().create(articleClass);
        article.eSet(articleTitleAttr, "EMF Codec Guide");
        article.eSet(articleBodyAttr, "A comprehensive guide to codec features.");
        article.eSet(pageCountAttr, 42);
        return article;
    }

    @Test
    @DisplayName("useExtendedMetaData — uses annotation names as JSON keys")
    void useExtendedMetaDataNames() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .useNamesFromExtendedMetaData(true)
                .build();

        EObject article = createArticle();
        String json = serialize(article, resolver);

        // Should use ExtendedMetaData names
        assertTrue(json.contains("\"title\""), "Should use ExtendedMetaData name 'title'");
        assertTrue(json.contains("\"body\""), "Should use ExtendedMetaData name 'body'");
        // pageCount has no ExtendedMetaData, so uses feature name
        assertTrue(json.contains("\"pageCount\""), "Should use feature name for pageCount");

        // Should NOT use EMF feature names
        assertFalse(json.contains("\"articleTitle\""), "Should NOT use EMF feature name 'articleTitle'");
        assertFalse(json.contains("\"articleBody\""), "Should NOT use EMF feature name 'articleBody'");

        // Round-trip
        EObject loaded = deserialize(json, resolver);
        assertNotNull(loaded);
        assertEquals("EMF Codec Guide", loaded.eGet(articleTitleAttr));
        assertEquals("A comprehensive guide to codec features.", loaded.eGet(articleBodyAttr));
        assertEquals(42, loaded.eGet(pageCountAttr));
    }

    @Test
    @DisplayName("Default names (no ExtendedMetaData) — uses EMF feature names")
    void defaultNamesWithoutExtendedMetaData() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        EObject article = createArticle();
        String json = serialize(article, resolver);

        // Should use EMF feature names
        assertTrue(json.contains("\"articleTitle\""), "Should use EMF feature name 'articleTitle'");
        assertTrue(json.contains("\"articleBody\""), "Should use EMF feature name 'articleBody'");
        assertTrue(json.contains("\"pageCount\""), "Should use EMF feature name 'pageCount'");

        EObject loaded = deserialize(json, resolver);
        assertNotNull(loaded);
        assertEquals("EMF Codec Guide", loaded.eGet(articleTitleAttr));
    }
}
