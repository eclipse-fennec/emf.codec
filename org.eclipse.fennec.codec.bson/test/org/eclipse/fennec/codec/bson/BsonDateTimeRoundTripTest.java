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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.RawBsonDocument;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Date attributes round-trip as native {@link BsonDateTime} (issue #97): without a
 * configured date format the BSON format stores the instant natively; a configured
 * {@code dateFormat} keeps the established string representation.
 */
@DisplayName("BSON native DateTime round trip")
class BsonDateTimeRoundTripTest {

    private EPackage testPackage;
    private EClass personClass;
    private EAttribute nameAttr;
    private EAttribute bornAttr;
    private EAttribute checkupsAttr;
    private MetadataWhiteboard metadataService;

    @BeforeEach
    void setUp() {
        EcoreFactory ecore = EcoreFactory.eINSTANCE;
        personClass = ecore.createEClass();
        personClass.setName("Person");
        nameAttr = ecore.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        bornAttr = ecore.createEAttribute();
        bornAttr.setName("born");
        bornAttr.setEType(EcorePackage.Literals.EDATE);
        personClass.getEStructuralFeatures().add(nameAttr);
        personClass.getEStructuralFeatures().add(bornAttr);

        EDataType dateArrayType = ecore.createEDataType();
        dateArrayType.setName("DateArray");
        dateArrayType.setInstanceClass(Date[].class);
        checkupsAttr = ecore.createEAttribute();
        checkupsAttr.setName("checkups");
        checkupsAttr.setEType(dateArrayType);
        personClass.getEStructuralFeatures().add(checkupsAttr);

        testPackage = ecore.createEPackage();
        testPackage.setName("datetest");
        testPackage.setNsURI("urn:bson:datetime:test");
        testPackage.setNsPrefix("datetest");
        testPackage.getEClassifiers().add(dateArrayType);
        testPackage.getEClassifiers().add(personClass);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
    }

    private CodecResource createResource(ConfigurationResolver resolver) {
        return new CodecResource(
                URI.createURI("test://datetime.bson"),
                metadataService,
                resolver,
                null, null, new BsonFormatProvider());
    }

    private EObject person(Date born) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttr, "Alice");
        person.eSet(bornAttr, born);
        return person;
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
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        resource.load(new ByteArrayInputStream(data), options);
        return resource.getContents().get(0);
    }

    @Test
    @DisplayName("stores EDate natively and reads it back")
    void nativeDateTimeRoundTrip() throws IOException {
        Date born = new Date(1234567890123L);
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        byte[] bytes = serialize(person(born), resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        assertInstanceOf(BsonDateTime.class, document.get("born"),
                "without a dateFormat the BSON format must store the instant natively");
        assertEquals(1234567890123L, document.getDateTime("born").getValue());

        EObject loaded = deserialize(bytes, resolver);
        assertEquals(born, loaded.eGet(bornAttr));
    }

    @Test
    @DisplayName("stores Date[] elements natively and reads them back")
    void dateArrayRoundTrip() throws IOException {
        Date first = new Date(1234567890123L);
        Date second = new Date(981173106789L);
        EObject person = person(first);
        person.eSet(checkupsAttr, new Date[] { first, second });
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        byte[] bytes = serialize(person, resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        BsonArray checkups = document.getArray("checkups");
        assertInstanceOf(BsonDateTime.class, checkups.get(0),
                "array elements must use the native BSON date-time type as well");
        assertInstanceOf(BsonDateTime.class, checkups.get(1));

        EObject loaded = deserialize(bytes, resolver);
        Date[] loadedCheckups = (Date[]) loaded.eGet(checkupsAttr);
        assertEquals(2, loadedCheckups.length);
        assertEquals(first, loadedCheckups[0]);
        assertEquals(second, loadedCheckups[1]);
    }

    @Test
    @DisplayName("a configured dateFormat keeps the string representation")
    void configuredDateFormatStaysString() throws IOException {
        Date born = new Date(1234567890123L);
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(CodecOptions.CODEC_DATE_FORMAT, "yyyy-MM-dd"))
                .build();

        byte[] bytes = serialize(person(born), resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        assertInstanceOf(BsonString.class, document.get("born"),
                "an explicitly configured dateFormat must keep the string form");
        assertTrue(document.getString("born").getValue().startsWith("2009-02-1"));
    }
}
