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
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates feature-level configuration: custom key names, ignore directions,
 * forceWrite for derived features, serializeNull, and globalIgnoreFeatures.
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Serialization</a>
 */
@DisplayName("Feature Configuration Examples")
class FeatureConfigExample {

    private static final String BASIC_ECORE = "/org/eclipse/fennec/codec/examples/example-basic.ecore";
    private static final String FORCE_ECORE = "/org/eclipse/fennec/codec/examples/example-force.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage basicPkg;
    private EPackage forcePkg;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EClass cachedItemClass;

    private EAttribute personId;
    private EAttribute nameAttr;
    private EAttribute ageAttr;
    private EAttribute salaryAttr;
    private EAttribute tagsAttr;

    private EAttribute itemNameAttr;
    private EAttribute computedHashAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(FeatureConfigExample.class);
        basicPkg = ecoreHelper.loadEcoreAbsolute(BASIC_ECORE);
        forcePkg = ecoreHelper.loadEcoreAbsolute(FORCE_ECORE);
        EPackage.Registry.INSTANCE.put(basicPkg.getNsURI(), basicPkg);
        EPackage.Registry.INSTANCE.put(forcePkg.getNsURI(), forcePkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(basicPkg);
        metadataService.registerPackage(forcePkg);

        personClass = ecoreHelper.getEClass(basicPkg, "Person");
        cachedItemClass = ecoreHelper.getEClass(forcePkg, "CachedItem");

        personId = (EAttribute) ecoreHelper.getFeature(personClass, "personId");
        nameAttr = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        ageAttr = (EAttribute) ecoreHelper.getFeature(personClass, "age");
        salaryAttr = (EAttribute) ecoreHelper.getFeature(personClass, "salary");
        tagsAttr = (EAttribute) ecoreHelper.getFeature(personClass, "tags");

        itemNameAttr = (EAttribute) ecoreHelper.getFeature(cachedItemClass, "name");
        computedHashAttr = (EAttribute) ecoreHelper.getFeature(cachedItemClass, "computedHash");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(basicPkg.getNsURI());
        EPackage.Registry.INSTANCE.remove(forcePkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://feature.json"), metadataService, resolver, null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootType, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://feature.json"), metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private EObject createPerson() {
        EObject person = basicPkg.getEFactoryInstance().create(personClass);
        person.eSet(personId, "p1");
        person.eSet(nameAttr, "Alice");
        person.eSet(ageAttr, 30);
        person.eSet(salaryAttr, 75000L);
        return person;
    }

    @Test
    @DisplayName("Custom key name — rename 'name' → 'fullName' via moduleProperties")
    void customKeyName() throws IOException {
        Map<String, Object> featureProps = Map.of(
                ConfigProperty.KEY.getKey(), "fullName"
        );
        Map<String, Object> classProps = Map.of("name", featureProps);
        Map<String, Object> moduleProps = Map.of("Person", classProps);

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();

        EObject person = createPerson();
        String json = serialize(person, resolver);

        assertTrue(json.contains("\"fullName\""), "Should use custom key 'fullName'");
        assertFalse(json.contains("\"name\"") && !json.contains("\"fullName\""),
                "Should not use original key 'name' for the feature");

        EObject loaded = deserialize(json, personClass, resolver);
        assertEquals("Alice", loaded.eGet(nameAttr));
    }

    @Test
    @DisplayName("ignoreWrite — feature excluded from serialization")
    void ignoreWriteFeature() throws IOException {
        Map<String, Object> featureProps = Map.of(
                ConfigProperty.IGNORE_WRITE.getKey(), true
        );
        Map<String, Object> classProps = Map.of("age", featureProps);
        Map<String, Object> moduleProps = Map.of("Person", classProps);

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();

        EObject person = createPerson();
        String json = serialize(person, resolver);

        assertFalse(json.contains("\"age\""), "age should not be serialized");
        assertTrue(json.contains("\"Alice\""), "name should still be serialized");
    }

    @Test
    @DisplayName("ignoreRead — feature excluded from deserialization")
    void ignoreReadFeature() throws IOException {
        Map<String, Object> featureProps = Map.of(
                ConfigProperty.IGNORE_READ.getKey(), true
        );
        Map<String, Object> classProps = Map.of("age", featureProps);
        Map<String, Object> moduleProps = Map.of("Person", classProps);

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();

        EObject person = createPerson();
        String json = serialize(person, resolver);
        assertTrue(json.contains("\"age\""), "age should be serialized (only ignoreRead)");

        EObject loaded = deserialize(json, personClass, resolver);
        assertEquals(0, loaded.eGet(ageAttr), "age should be default (ignored on read)");
    }

    @Test
    @DisplayName("ignore both directions — feature excluded from serialization and deserialization")
    void ignoreBothDirections() throws IOException {
        Map<String, Object> featureProps = Map.of(
                ConfigProperty.IGNORE.getKey(), true
        );
        Map<String, Object> classProps = Map.of("salary", featureProps);
        Map<String, Object> moduleProps = Map.of("Person", classProps);

        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();

        EObject person = createPerson();
        String json = serialize(person, resolver);

        assertFalse(json.contains("\"salary\""), "salary should not be serialized");
    }

    @Test
    @DisplayName("forceWrite — serialize derived/transient feature")
    void forceWriteDerivedFeature() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .forceWrite(computedHashAttr)
                .forceRead(computedHashAttr)
                .serializeNull(true)
                .build();

        EObject item = forcePkg.getEFactoryInstance().create(cachedItemClass);
        item.eSet(itemNameAttr, "Widget");
        item.eSet(computedHashAttr, "abc123hash");

        String json = serialize(item, resolver);
        assertTrue(json.contains("\"computedHash\""), "Derived feature should be serialized with forceWrite");
        assertTrue(json.contains("\"abc123hash\""), "Hash value should be present");

        EObject loaded = deserialize(json, cachedItemClass, resolver);
        assertNotNull(loaded);
        assertEquals("Widget", loaded.eGet(itemNameAttr));
        assertEquals("abc123hash", loaded.eGet(computedHashAttr));
    }

    @Test
    @DisplayName("serializeNull — include null-valued attributes in output")
    void serializeNullValues() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .serializeNull(true)
                .build();

        EObject person = basicPkg.getEFactoryInstance().create(personClass);
        person.eSet(personId, "p1");
        // name is not set (null)

        String json = serialize(person, resolver);
        assertTrue(json.contains("\"name\""), "null-valued name should be serialized");
    }

    @Test
    @DisplayName("globalIgnoreFeatures — ignore specific features globally")
    @SuppressWarnings("unchecked")
    void globalIgnoreFeatures() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .globalIgnoreFeatures("tags", "salary")
                .build();

        EObject person = createPerson();
        java.util.List<String> tags = (java.util.List<String>) person.eGet(tagsAttr);
        tags.add("java");

        String json = serialize(person, resolver);

        assertFalse(json.contains("\"tags\""), "tags should be globally ignored");
        assertFalse(json.contains("\"salary\""), "salary should be globally ignored");
        assertTrue(json.contains("\"Alice\""), "name should still be present");
    }
}
