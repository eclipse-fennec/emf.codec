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
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates ID serialization strategies: ID_FIELD, COMBINED, custom key,
 * and IdKeyMode (BOTH, NONE).
 *
 * @see <a href="docs/codec-v2-spec/09-id.md">Spec: ID Serialization</a>
 */
@DisplayName("ID Strategy Examples")
class IdStrategyExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-id.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass entityClass;
    private EClass compositeEntityClass;

    private EAttribute entityIdAttr;
    private EAttribute labelAttr;
    private EAttribute firstNameAttr;
    private EAttribute lastNameAttr;
    private EAttribute sequenceAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(IdStrategyExample.class);
        pkg = ecoreHelper.loadEcoreAbsolute(ECORE);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        entityClass = ecoreHelper.getEClass(pkg, "Entity");
        compositeEntityClass = ecoreHelper.getEClass(pkg, "CompositeEntity");

        entityIdAttr = (EAttribute) ecoreHelper.getFeature(entityClass, "entityId");
        labelAttr = (EAttribute) ecoreHelper.getFeature(entityClass, "label");
        firstNameAttr = (EAttribute) ecoreHelper.getFeature(compositeEntityClass, "firstName");
        lastNameAttr = (EAttribute) ecoreHelper.getFeature(compositeEntityClass, "lastName");
        sequenceAttr = (EAttribute) ecoreHelper.getFeature(compositeEntityClass, "sequence");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private String serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://id.json"), metadataService, resolver, null);
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootType, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://id.json"), metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    @Test
    @DisplayName("ID_FIELD strategy — serializes iD=true attribute as _id")
    void idFieldStrategy() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .idStrategy("ID_FIELD")
                .build();

        EObject entity = pkg.getEFactoryInstance().create(entityClass);
        entity.eSet(entityIdAttr, "entity-001");
        entity.eSet(labelAttr, "Test Entity");

        String json = serialize(entity, resolver);
        assertTrue(json.contains("\"_id\""), "Should contain _id field");
        assertTrue(json.contains("\"entity-001\""), "Should contain ID value");

        EObject loaded = deserialize(json, entityClass, resolver);
        assertNotNull(loaded);
        assertEquals("entity-001", loaded.eGet(entityIdAttr));
        assertEquals("Test Entity", loaded.eGet(labelAttr));
    }

    @Test
    @DisplayName("COMBINED ID strategy — multiple features combined into _id")
    void combinedIdStrategy() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .idStrategy("COMBINED")
                .build();

        EObject entity = pkg.getEFactoryInstance().create(compositeEntityClass);
        entity.eSet(firstNameAttr, "John");
        entity.eSet(lastNameAttr, "Doe");
        entity.eSet(sequenceAttr, 42);

        String json = serialize(entity, resolver);

        EObject loaded = deserialize(json, compositeEntityClass, resolver);
        assertNotNull(loaded);
        assertEquals("John", loaded.eGet(firstNameAttr));
        assertEquals("Doe", loaded.eGet(lastNameAttr));
        assertEquals(42, loaded.eGet(sequenceAttr));
    }

    @Test
    @DisplayName("IdKeyMode BOTH — ID appears as _id AND as regular attribute")
    void idKeyModeBoth() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .idStrategy("ID_FIELD")
                .idKeyMode("BOTH")
                .build();

        EObject entity = pkg.getEFactoryInstance().create(entityClass);
        entity.eSet(entityIdAttr, "entity-both");
        entity.eSet(labelAttr, "Both Mode");

        String json = serialize(entity, resolver);
        assertTrue(json.contains("\"_id\""), "Should contain _id");
        assertTrue(json.contains("\"entityId\""), "Should also contain entityId attribute");

        EObject loaded = deserialize(json, entityClass, resolver);
        assertEquals("entity-both", loaded.eGet(entityIdAttr));
    }

    @Test
    @DisplayName("IdKeyMode NONE — no _id field, ID only as regular attribute")
    void idKeyModeNone() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .idStrategy("ID_FIELD")
                .idKeyMode("NONE")
                .build();

        EObject entity = pkg.getEFactoryInstance().create(entityClass);
        entity.eSet(entityIdAttr, "entity-none");
        entity.eSet(labelAttr, "None Mode");

        String json = serialize(entity, resolver);
        assertFalse(json.contains("\"_id\""), "Should NOT contain _id field");

        EObject loaded = deserialize(json, entityClass, resolver);
        assertNotNull(loaded);
        assertEquals("entity-none", loaded.eGet(entityIdAttr));
    }

    @Test
    @DisplayName("Custom ID key — changes _id to custom key name")
    void customIdKey() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .idStrategy("ID_FIELD")
                .idKey("identifier")
                .build();

        EObject entity = pkg.getEFactoryInstance().create(entityClass);
        entity.eSet(entityIdAttr, "entity-custom");
        entity.eSet(labelAttr, "Custom Key");

        String json = serialize(entity, resolver);
        assertTrue(json.contains("\"identifier\""), "Should contain custom ID key 'identifier'");

        EObject loaded = deserialize(json, entityClass, resolver);
        assertEquals("entity-custom", loaded.eGet(entityIdAttr));
    }
}
