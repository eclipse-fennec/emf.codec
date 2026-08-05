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
package org.eclipse.fennec.codec.bson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.RawBsonDocument;
import org.bson.types.ObjectId;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The "objectId" id value handler pair stores an EMF id as native BSON {@link ObjectId}
 * and restores the hex string on load (issue #104). Non-hex ids degrade to plain strings.
 */
@DisplayName("BSON native ObjectId id round trip")
class BsonObjectIdRoundTripTest {

    private EPackage testPackage;
    private EClass entityClass;
    private EAttribute idAttr;
    private EAttribute nameAttr;
    private MetadataWhiteboard metadataService;
    private CodecValueRegistry registry;

    @BeforeEach
    void setUp() {
        EcoreFactory ecore = EcoreFactory.eINSTANCE;
        entityClass = ecore.createEClass();
        entityClass.setName("Entity");
        idAttr = ecore.createEAttribute();
        idAttr.setName("entityId");
        idAttr.setEType(EcorePackage.Literals.ESTRING);
        idAttr.setID(true);
        nameAttr = ecore.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        entityClass.getEStructuralFeatures().add(idAttr);
        entityClass.getEStructuralFeatures().add(nameAttr);

        testPackage = ecore.createEPackage();
        testPackage.setName("oidtest");
        testPackage.setNsURI("urn:bson:objectid:test");
        testPackage.setNsPrefix("oidtest");
        testPackage.getEClassifiers().add(entityClass);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        registry = new CodecValueRegistry();
        registry.register(new ObjectIdValueWriter());
        registry.register(new ObjectIdValueReader());
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
    }

    private ConfigurationResolver objectIdResolver() {
        return ConfigurationResolver.builder()
                .moduleProperties(Map.of(
                        "idValueWriterName", ObjectIdValueWriter.NAME,
                        "idValueReaderName", ObjectIdValueReader.NAME))
                .build();
    }

    private CodecResource createResource(ConfigurationResolver resolver) {
        return new CodecResource(
                URI.createURI("test://objectid.bson"),
                metadataService,
                resolver,
                registry, null, new BsonFormatProvider());
    }

    private EObject entity(String id) {
        EObject entity = testPackage.getEFactoryInstance().create(entityClass);
        entity.eSet(idAttr, id);
        entity.eSet(nameAttr, "Alice");
        return entity;
    }

    private byte[] serialize(EObject object, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = createResource(resolver);
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toByteArray();
    }

    private EObject deserialize(byte[] data, ConfigurationResolver resolver) throws IOException {
        CodecResource resource = createResource(resolver);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, entityClass);
        resource.load(new ByteArrayInputStream(data), options);
        return resource.getContents().get(0);
    }

    @Test
    @DisplayName("stores a hex id as native ObjectId and restores the hex string")
    void nativeObjectIdRoundTrip() throws IOException {
        String hex = new ObjectId().toHexString();
        ConfigurationResolver resolver = objectIdResolver();

        byte[] bytes = serialize(entity(hex), resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        assertInstanceOf(BsonObjectId.class, document.get("_id"),
                "a valid hex id must be stored as native ObjectId");
        assertEquals(hex, document.getObjectId("_id").getValue().toHexString());

        EObject loaded = deserialize(bytes, resolver);
        assertEquals(hex, loaded.eGet(idAttr), "the hex string must be restored from the ObjectId");
        assertEquals("Alice", loaded.eGet(nameAttr));
    }

    @Test
    @DisplayName("non-hex id degrades to a plain string")
    void nonHexIdStaysString() throws IOException {
        ConfigurationResolver resolver = objectIdResolver();

        byte[] bytes = serialize(entity("john-123"), resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        assertInstanceOf(BsonString.class, document.get("_id"),
                "an id that is no valid ObjectId hex must stay a string");
        assertEquals("john-123", document.getString("_id").getValue());

        EObject loaded = deserialize(bytes, resolver);
        assertEquals("john-123", loaded.eGet(idAttr));
    }

    @Test
    @DisplayName("without the handlers the id stays a plain string")
    void withoutHandlersIdStaysString() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        byte[] bytes = serialize(entity(new ObjectId().toHexString()), resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        assertInstanceOf(BsonString.class, document.get("_id"),
                "the ObjectId transport is opt-in via idValueWriterName");
    }
}
