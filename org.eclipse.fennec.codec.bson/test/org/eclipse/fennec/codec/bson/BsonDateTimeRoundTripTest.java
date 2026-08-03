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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
 * Temporal attributes round-trip as native {@link BsonDateTime}: without a configured
 * date format the BSON format stores {@code java.util.Date} (issue #97) and the
 * instant-like {@code java.time} types (issue #98) natively; zoned types keep the ISO
 * string form (a native instant cannot restore the zone), and a configured
 * {@code dateFormat} keeps the established string representation for everything.
 */
@DisplayName("BSON native DateTime round trip")
class BsonDateTimeRoundTripTest {

    private EPackage testPackage;
    private EClass personClass;
    private EAttribute nameAttr;
    private EAttribute bornAttr;
    private EAttribute checkupsAttr;
    private EAttribute lastSeenAttr;
    private EAttribute localBornAttr;
    private EAttribute birthdayAttr;
    private EAttribute meetingAttr;
    private EAttribute checkinsAttr;
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

        testPackage = ecore.createEPackage();
        testPackage.setName("datetest");
        testPackage.setNsURI("urn:bson:datetime:test");
        testPackage.setNsPrefix("datetest");
        testPackage.getEClassifiers().add(personClass);

        checkupsAttr = addAttribute("checkups", "DateArray", Date[].class);
        lastSeenAttr = addAttribute("lastSeen", "InstantType", Instant.class);
        localBornAttr = addAttribute("localBorn", "LocalDateTimeType", LocalDateTime.class);
        birthdayAttr = addAttribute("birthday", "LocalDateType", LocalDate.class);
        meetingAttr = addAttribute("meeting", "ZonedDateTimeType", ZonedDateTime.class);
        checkinsAttr = addAttribute("checkins", "InstantArray", Instant[].class);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    private EAttribute addAttribute(String attributeName, String typeName, Class<?> instanceClass) {
        EcoreFactory ecore = EcoreFactory.eINSTANCE;
        EDataType dataType = ecore.createEDataType();
        dataType.setName(typeName);
        dataType.setInstanceClass(instanceClass);
        testPackage.getEClassifiers().add(dataType);
        EAttribute attribute = ecore.createEAttribute();
        attribute.setName(attributeName);
        attribute.setEType(dataType);
        personClass.getEStructuralFeatures().add(attribute);
        return attribute;
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
    @DisplayName("stores instant-like java.time values natively and reads them back")
    void javaTimeRoundTrip() throws IOException {
        Instant lastSeen = Instant.ofEpochMilli(1234567890123L);
        LocalDateTime localBorn = LocalDateTime.of(2009, 2, 13, 23, 31, 30);
        LocalDate birthday = LocalDate.of(2009, 2, 13);
        EObject person = person(new Date(1234567890123L));
        person.eSet(lastSeenAttr, lastSeen);
        person.eSet(localBornAttr, localBorn);
        person.eSet(birthdayAttr, birthday);
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        byte[] bytes = serialize(person, resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        assertInstanceOf(BsonDateTime.class, document.get("lastSeen"),
                "Instant must be stored as native BSON date-time");
        assertEquals(1234567890123L, document.getDateTime("lastSeen").getValue());
        assertInstanceOf(BsonDateTime.class, document.get("localBorn"),
                "LocalDateTime must be stored as native BSON date-time (UTC convention)");
        assertInstanceOf(BsonDateTime.class, document.get("birthday"),
                "LocalDate must be stored as native BSON date-time (UTC start of day)");

        EObject loaded = deserialize(bytes, resolver);
        assertEquals(lastSeen, loaded.eGet(lastSeenAttr));
        assertEquals(localBorn, loaded.eGet(localBornAttr));
        assertEquals(birthday, loaded.eGet(birthdayAttr));
    }

    @Test
    @DisplayName("keeps zoned types on the ISO string path — a native instant cannot restore the zone")
    void zonedDateTimeStaysString() throws IOException {
        ZonedDateTime meeting = ZonedDateTime.of(2009, 2, 13, 23, 31, 30, 0,
                ZoneId.of("Europe/Berlin"));
        EObject person = person(new Date(1234567890123L));
        person.eSet(meetingAttr, meeting);
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        byte[] bytes = serialize(person, resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        assertInstanceOf(BsonString.class, document.get("meeting"),
                "zoned types must keep the ISO string form to round-trip the zone");

        EObject loaded = deserialize(bytes, resolver);
        assertEquals(meeting, loaded.eGet(meetingAttr));
    }

    @Test
    @DisplayName("stores Instant[] elements natively and reads them back")
    void instantArrayRoundTrip() throws IOException {
        Instant first = Instant.ofEpochMilli(1234567890123L);
        Instant second = Instant.ofEpochMilli(981173106789L);
        EObject person = person(new Date(1234567890123L));
        person.eSet(checkinsAttr, new Instant[] { first, second });
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        byte[] bytes = serialize(person, resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        BsonArray checkins = document.getArray("checkins");
        assertInstanceOf(BsonDateTime.class, checkins.get(0),
                "Instant array elements must use the native BSON date-time type as well");
        assertInstanceOf(BsonDateTime.class, checkins.get(1));

        EObject loaded = deserialize(bytes, resolver);
        Instant[] loadedCheckins = (Instant[]) loaded.eGet(checkinsAttr);
        assertEquals(2, loadedCheckins.length);
        assertEquals(first, loadedCheckins[0]);
        assertEquals(second, loadedCheckins[1]);
    }

    @Test
    @DisplayName("a configured dateFormat keeps the string representation")
    void configuredDateFormatStaysString() throws IOException {
        Date born = new Date(1234567890123L);
        Instant lastSeen = Instant.ofEpochMilli(1234567890123L);
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .optionsProperties(Map.of(CodecOptions.CODEC_DATE_FORMAT, "yyyy-MM-dd"))
                .build();

        EObject person = person(born);
        person.eSet(lastSeenAttr, lastSeen);
        byte[] bytes = serialize(person, resolver);
        BsonDocument document = new RawBsonDocument(bytes);
        assertInstanceOf(BsonString.class, document.get("born"),
                "an explicitly configured dateFormat must keep the string form");
        assertTrue(document.getString("born").getValue().startsWith("2009-02-1"));
        assertInstanceOf(BsonString.class, document.get("lastSeen"),
                "a configured dateFormat opts java.time values out of the native path too");

        EObject loaded = deserialize(bytes, resolver);
        assertEquals(lastSeen, loaded.eGet(lastSeenAttr),
                "the ISO toString form must still round-trip");
    }
}
