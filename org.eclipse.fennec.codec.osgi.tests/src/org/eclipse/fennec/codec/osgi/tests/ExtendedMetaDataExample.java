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
package org.eclipse.fennec.codec.osgi.tests;

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
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * OSGi integration tests for ExtendedMetaData name resolution.
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Serialization</a>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("ExtendedMetaData OSGi Examples")
public class ExtendedMetaDataExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-extmetadata.ecore";

    @InjectService
    MetadataService metadataService;

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;

    private EClass articleClass;
    private EAttribute articleTitleAttr;
    private EAttribute articleBodyAttr;
    private EAttribute pageCountAttr;

    @BeforeEach
    public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, ExtendedMetaDataExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        pkgReg = ctx.registerService(EPackage.class, pkg, null);

        articleClass = EcoreHelper.getEClass(pkg, "Article");
        articleTitleAttr = (EAttribute) EcoreHelper.getFeature(articleClass, "articleTitle");
        articleBodyAttr = (EAttribute) EcoreHelper.getFeature(articleClass, "articleBody");
        pageCountAttr = (EAttribute) EcoreHelper.getFeature(articleClass, "pageCount");
    }

    @AfterEach
    public void tearDown() {
        pkgReg.unregister();
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

        assertTrue(json.contains("\"title\""), "Should use ExtendedMetaData name 'title'");
        assertTrue(json.contains("\"body\""), "Should use ExtendedMetaData name 'body'");
        assertTrue(json.contains("\"pageCount\""), "Should use feature name for pageCount");

        assertFalse(json.contains("\"articleTitle\""), "Should NOT use EMF feature name 'articleTitle'");
        assertFalse(json.contains("\"articleBody\""), "Should NOT use EMF feature name 'articleBody'");

        EObject loaded = deserialize(json, resolver);
        assertNotNull(loaded);
        assertEquals("EMF Codec Guide", loaded.eGet(articleTitleAttr));
        assertEquals("A comprehensive guide to codec features.", loaded.eGet(articleBodyAttr));
        assertEquals(42, loaded.eGet(pageCountAttr));
    }

    @Test
    @DisplayName("useExtendedMetaData via save options — uses annotation names as JSON keys")
    void useExtendedMetaDataNamesViaSaveOptions() throws IOException {
        // Use defaults() on the resolver — pass the flag via save options instead
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://extmeta.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        saveResource.getContents().add(createArticle());

        Map<String, Object> saveOptions = new HashMap<>();
        saveOptions.put("useNamesFromExtendedMetadata", true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, saveOptions);
        String json = out.toString(StandardCharsets.UTF_8);

        assertTrue(json.contains("\"title\""), "Should use ExtendedMetaData name 'title'");
        assertTrue(json.contains("\"body\""), "Should use ExtendedMetaData name 'body'");
        assertFalse(json.contains("\"articleTitle\""), "Should NOT use EMF feature name 'articleTitle'");
        assertFalse(json.contains("\"articleBody\""), "Should NOT use EMF feature name 'articleBody'");
    }

    @Test
    @DisplayName("useExtendedMetaData via load options — deserializes annotation-keyed JSON")
    void useExtendedMetaDataNamesViaLoadOptions() throws IOException {
        // JSON uses ExtendedMetaData names
        String json = """
                {
                  "title": "EMF Codec Guide",
                  "body": "A comprehensive guide to codec features.",
                  "pageCount": 42
                }""";

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://extmeta.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        Map<String, Object> loadOptions = new HashMap<>();
        loadOptions.put(CodecResource.CODEC_ROOT_TYPE, articleClass);
        loadOptions.put("useNamesFromExtendedMetadata", true);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), loadOptions);

        EObject loaded = loadResource.getContents().get(0);
        assertNotNull(loaded);
        assertEquals("EMF Codec Guide", loaded.eGet(articleTitleAttr));
        assertEquals("A comprehensive guide to codec features.", loaded.eGet(articleBodyAttr));
        assertEquals(42, loaded.eGet(pageCountAttr));
    }

    @Test
    @DisplayName("useExtendedMetaData via options — full round-trip")
    void useExtendedMetaDataNamesViaOptionsRoundTrip() throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put("useNamesFromExtendedMetadata", true);

        // Serialize with option
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://extmeta.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        saveResource.getContents().add(createArticle());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, options);
        String json = out.toString(StandardCharsets.UTF_8);

        assertTrue(json.contains("\"title\""), "Should use ExtendedMetaData name 'title'");

        // Deserialize with same option
        Map<String, Object> loadOptions = new HashMap<>(options);
        loadOptions.put(CodecResource.CODEC_ROOT_TYPE, articleClass);

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://extmeta.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), loadOptions);

        EObject loaded = loadResource.getContents().get(0);
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

        assertTrue(json.contains("\"articleTitle\""), "Should use EMF feature name 'articleTitle'");
        assertTrue(json.contains("\"articleBody\""), "Should use EMF feature name 'articleBody'");
        assertTrue(json.contains("\"pageCount\""), "Should use EMF feature name 'pageCount'");

        EObject loaded = deserialize(json, resolver);
        assertNotNull(loaded);
        assertEquals("EMF Codec Guide", loaded.eGet(articleTitleAttr));
    }
}
