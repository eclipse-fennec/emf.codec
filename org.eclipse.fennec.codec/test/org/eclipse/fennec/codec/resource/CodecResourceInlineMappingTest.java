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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for inlineMapping on EReference and fallback strategy roundtrip behaviour.
 * <p>
 * These tests verify that:
 * <ul>
 *   <li>InlineMapping annotations on EReference are registered as reference-scoped
 *       discriminator registries</li>
 *   <li>Serialization outputs inline discriminator values for contained objects</li>
 *   <li>Deserialization resolves inline discriminator values back to correct EClasses</li>
 *   <li>Fallback ERROR strategy throws on unknown discriminators</li>
 *   <li>Fallback FALLBACK strategy resolves to fallbackEClass for unknown discriminators</li>
 * </ul>
 * </p>
 *
 * @see TypeDiscriminatorService#registerInlineMappings
 * @see TypeDiscriminatorService#resolveForReference
 */
@DisplayName("CodecResource InlineMapping & Fallback Tests")
class CodecResourceInlineMappingTest {

    private static final String TEST_ECORE = "test-inline-mapping.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private TypeDiscriminatorService typeService;

    // InlineMapping classes
    private EClass friendClass;
    private EClass colleagueClass;
    private EClass personContainerClass;

    // Fallback ERROR classes
    private EClass sensorClass;
    private EClass tempSensorClass;
    private EClass sensorHubClass;

    // Fallback FALLBACK classes
    private EClass messageClass;
    private EClass alertMessageClass;
    private EClass genericMessageClass;
    private EClass messageBoxClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore("/org/eclipse/fennec/codec/resource/" + TEST_ECORE, CodecResourceInlineMappingTest.class);

        // Register package in global registry for type resolution
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        // Create metadata service with CodecAspectProvider
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        // Create type discriminator service from metadata
        // (fromMetadataService now automatically registers inline mappings and fallback config)
        typeService = TypeDiscriminatorService.fromMetadataService(metadataService);

        // Load EClasses
        friendClass = EcoreHelper.getEClass(testPackage, "Friend");
        colleagueClass = EcoreHelper.getEClass(testPackage, "Colleague");
        personContainerClass = EcoreHelper.getEClass(testPackage, "PersonContainer");

        sensorClass = EcoreHelper.getEClass(testPackage, "Sensor");
        tempSensorClass = EcoreHelper.getEClass(testPackage, "TempSensor");
        sensorHubClass = EcoreHelper.getEClass(testPackage, "SensorHub");

        messageClass = EcoreHelper.getEClass(testPackage, "Message");
        alertMessageClass = EcoreHelper.getEClass(testPackage, "AlertMessage");
        genericMessageClass = EcoreHelper.getEClass(testPackage, "GenericMessage");
        messageBoxClass = EcoreHelper.getEClass(testPackage, "MessageBox");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://inline-mapping.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
    }

    // ========================================================================
    // InlineMapping Registration Tests
    // ========================================================================

    @Nested
    @DisplayName("InlineMapping Registration")
    class InlineMappingRegistration {

        @Test
        @DisplayName("inline mappings are registered from EReference annotations")
        void inlineMappingsRegistered() {
            EReference contactsRef = (EReference) personContainerClass.getEStructuralFeature("contacts");
            assertNotNull(contactsRef, "contacts reference should exist");

            // The inline mapping should be registered under the EReference URI
            String mapId = org.eclipse.emf.ecore.util.EcoreUtil.getURI(contactsRef).toString();
            assertTrue(typeService.hasRegistry(mapId),
                    "Should have registry for contacts reference URI: " + mapId);

            // Verify discriminator-to-EClass mappings
            assertEquals(friendClass, typeService.getEClass(mapId, "friend"),
                    "friend discriminator should resolve to Friend EClass");
            assertEquals(colleagueClass, typeService.getEClass(mapId, "colleague"),
                    "colleague discriminator should resolve to Colleague EClass");
        }

        @Test
        @DisplayName("static typeMapping registries are registered with fallback config")
        void staticMappingsRegistered() {
            // strict-sensors: ERROR fallback
            assertTrue(typeService.hasRegistry("strict-sensors"),
                    "Should have registry for strict-sensors");
            assertEquals(tempSensorClass, typeService.getEClass("strict-sensors", "temp"));

            // tolerant-messages: FALLBACK strategy
            assertTrue(typeService.hasRegistry("tolerant-messages"),
                    "Should have registry for tolerant-messages");
            assertEquals(alertMessageClass, typeService.getEClass("tolerant-messages", "alert"));
        }
    }

    // ========================================================================
    // InlineMapping Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("InlineMapping Serialization")
    class InlineMappingSerialization {

        @Test
        @DisplayName("serializes PersonContainer with inline discriminators for contacts")
        @SuppressWarnings("unchecked")
        void serializesWithInlineDiscriminators() throws IOException {
            EObject container = testPackage.getEFactoryInstance().create(personContainerClass);
            container.eSet(personContainerClass.getEStructuralFeature("ownerName"), "Alice");

            EObject friend = testPackage.getEFactoryInstance().create(friendClass);
            friend.eSet(friendClass.getEStructuralFeature("name"), "Bob");
            friend.eSet(friendClass.getEStructuralFeature("nickname"), "Bobby");

            EObject colleague = testPackage.getEFactoryInstance().create(colleagueClass);
            colleague.eSet(colleagueClass.getEStructuralFeature("name"), "Carol");
            colleague.eSet(colleagueClass.getEStructuralFeature("department"), "Engineering");

            EReference contactsRef = (EReference) personContainerClass.getEStructuralFeature("contacts");
            List<EObject> contacts = (List<EObject>) container.eGet(contactsRef);
            contacts.add(friend);
            contacts.add(colleague);

            String json = serialize(container);
            System.out.println("InlineMapping Serialization JSON:\n" + json);

            // Should contain inline discriminator values
            assertTrue(json.contains("\"friend\"") || json.contains("\"Friend\""),
                    "JSON should contain discriminator for Friend");
            assertTrue(json.contains("\"colleague\"") || json.contains("\"Colleague\""),
                    "JSON should contain discriminator for Colleague");
            assertTrue(json.contains("\"Alice\""), "JSON should contain ownerName");
            assertTrue(json.contains("\"Bobby\""), "JSON should contain nickname");
            assertTrue(json.contains("\"Engineering\""), "JSON should contain department");
        }
    }

    // ========================================================================
    // InlineMapping Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("InlineMapping Deserialization")
    class InlineMappingDeserialization {

        @Test
        @DisplayName("deserializes PersonContainer with inline discriminators")
        @SuppressWarnings("unchecked")
        void deserializesWithInlineDiscriminators() throws IOException {
            String json = """
                {
                    "ownerName": "Alice",
                    "contacts": [
                        {
                            "_type": "friend",
                            "name": "Bob",
                            "nickname": "Bobby"
                        },
                        {
                            "_type": "colleague",
                            "name": "Carol",
                            "department": "Engineering"
                        }
                    ]
                }
                """;

            EObject result = deserialize(json, personContainerClass);

            assertNotNull(result, "Deserialized object should not be null");
            assertEquals(personContainerClass, result.eClass());
            assertEquals("Alice", result.eGet(personContainerClass.getEStructuralFeature("ownerName")));

            EReference contactsRef = (EReference) personContainerClass.getEStructuralFeature("contacts");
            List<EObject> contacts = (List<EObject>) result.eGet(contactsRef);
            assertEquals(2, contacts.size(), "Should have 2 contacts");

            // First contact: Friend
            EObject firstContact = contacts.get(0);
            assertEquals(friendClass, firstContact.eClass(), "First contact should be Friend");
            assertEquals("Bob", firstContact.eGet(friendClass.getEStructuralFeature("name")));
            assertEquals("Bobby", firstContact.eGet(friendClass.getEStructuralFeature("nickname")));

            // Second contact: Colleague
            EObject secondContact = contacts.get(1);
            assertEquals(colleagueClass, secondContact.eClass(), "Second contact should be Colleague");
            assertEquals("Carol", secondContact.eGet(colleagueClass.getEStructuralFeature("name")));
            assertEquals("Engineering", secondContact.eGet(colleagueClass.getEStructuralFeature("department")));
        }
    }

    // ========================================================================
    // InlineMapping Round-trip Tests
    // ========================================================================

    @Nested
    @DisplayName("InlineMapping Round-trip")
    class InlineMappingRoundTrip {

        @Test
        @DisplayName("round-trips PersonContainer with Friend and Colleague contacts")
        @SuppressWarnings("unchecked")
        void roundTripsPersonContainer() throws IOException {
            // Create
            EObject container = testPackage.getEFactoryInstance().create(personContainerClass);
            container.eSet(personContainerClass.getEStructuralFeature("ownerName"), "Alice");

            EObject friend = testPackage.getEFactoryInstance().create(friendClass);
            friend.eSet(friendClass.getEStructuralFeature("name"), "Bob");
            friend.eSet(friendClass.getEStructuralFeature("nickname"), "Bobby");

            EObject colleague = testPackage.getEFactoryInstance().create(colleagueClass);
            colleague.eSet(colleagueClass.getEStructuralFeature("name"), "Carol");
            colleague.eSet(colleagueClass.getEStructuralFeature("department"), "Engineering");

            EReference contactsRef = (EReference) personContainerClass.getEStructuralFeature("contacts");
            List<EObject> contacts = (List<EObject>) container.eGet(contactsRef);
            contacts.add(friend);
            contacts.add(colleague);

            // Serialize
            String json = serialize(container);
            System.out.println("InlineMapping Round-trip JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, personContainerClass);

            // Verify container
            assertNotNull(loaded);
            assertEquals(personContainerClass, loaded.eClass());
            assertEquals("Alice", loaded.eGet(personContainerClass.getEStructuralFeature("ownerName")));

            // Verify contacts
            List<EObject> loadedContacts = (List<EObject>) loaded.eGet(contactsRef);
            assertEquals(2, loadedContacts.size());

            // First contact: Friend
            EObject loadedFriend = loadedContacts.get(0);
            assertEquals(friendClass, loadedFriend.eClass());
            assertEquals("Bob", loadedFriend.eGet(friendClass.getEStructuralFeature("name")));
            assertEquals("Bobby", loadedFriend.eGet(friendClass.getEStructuralFeature("nickname")));

            // Second contact: Colleague
            EObject loadedColleague = loadedContacts.get(1);
            assertEquals(colleagueClass, loadedColleague.eClass());
            assertEquals("Carol", loadedColleague.eGet(colleagueClass.getEStructuralFeature("name")));
            assertEquals("Engineering", loadedColleague.eGet(colleagueClass.getEStructuralFeature("department")));
        }
    }

    // ========================================================================
    // Fallback ERROR Strategy Tests
    // ========================================================================

    @Nested
    @DisplayName("Fallback ERROR Strategy")
    class FallbackErrorStrategy {

        @Test
        @DisplayName("deserializes known discriminator in strict registry")
        @SuppressWarnings("unchecked")
        void deserializesKnownDiscriminator() throws IOException {
            String json = """
                {
                    "hubName": "Lab Sensors",
                    "sensors": [
                        {
                            "_type": "temp",
                            "sensorId": "sensor-001",
                            "celsius": 22.5
                        }
                    ]
                }
                """;

            EObject result = deserialize(json, sensorHubClass);

            assertNotNull(result);
            assertEquals(sensorHubClass, result.eClass());
            assertEquals("Lab Sensors", result.eGet(sensorHubClass.getEStructuralFeature("hubName")));

            EReference sensorsRef = (EReference) sensorHubClass.getEStructuralFeature("sensors");
            List<EObject> sensors = (List<EObject>) result.eGet(sensorsRef);
            assertEquals(1, sensors.size());

            EObject sensor = sensors.get(0);
            assertEquals(tempSensorClass, sensor.eClass());
            assertEquals("sensor-001", sensor.eGet(tempSensorClass.getEStructuralFeature("sensorId")));
            assertEquals(22.5, (Double) sensor.eGet(tempSensorClass.getEStructuralFeature("celsius")), 0.001);
        }

        @Test
        @DisplayName("throws on unknown discriminator with ERROR strategy")
        void throwsOnUnknownDiscriminator() {
            String json = """
                {
                    "hubName": "Lab Sensors",
                    "sensors": [
                        {
                            "_type": "unknown-sensor",
                            "sensorId": "sensor-999"
                        }
                    ]
                }
                """;

            // With ERROR fallback strategy, unknown discriminators should cause an error
            // This may manifest as an IOException, IllegalStateException, or the object
            // being deserialized as the abstract base type (which would also be an error)
            assertThrows(Exception.class, () -> deserialize(json, sensorHubClass),
                    "Should throw when unknown discriminator encountered with ERROR strategy");
        }

        @Test
        @DisplayName("serializes SensorHub with discriminator values for sensors")
        @SuppressWarnings("unchecked")
        void serializesWithDiscriminatorValues() throws IOException {
            EObject hub = testPackage.getEFactoryInstance().create(sensorHubClass);
            hub.eSet(sensorHubClass.getEStructuralFeature("hubName"), "Lab Sensors");

            EObject temp = testPackage.getEFactoryInstance().create(tempSensorClass);
            temp.eSet(tempSensorClass.getEStructuralFeature("sensorId"), "sensor-001");
            temp.eSet(tempSensorClass.getEStructuralFeature("celsius"), 22.5);

            EReference sensorsRef = (EReference) sensorHubClass.getEStructuralFeature("sensors");
            List<EObject> sensors = (List<EObject>) hub.eGet(sensorsRef);
            sensors.add(temp);

            String json = serialize(hub);
            System.out.println("FallbackError Serialization JSON:\n" + json);

            assertTrue(json.contains("\"temp\""),
                    "JSON should contain discriminator value 'temp' for TempSensor");
            assertTrue(json.contains("\"sensor-001\""), "JSON should contain sensorId");
        }

        @Test
        @DisplayName("round-trips SensorHub with TempSensor")
        @SuppressWarnings("unchecked")
        void roundTripsSensorHub() throws IOException {
            // Create
            EObject hub = testPackage.getEFactoryInstance().create(sensorHubClass);
            hub.eSet(sensorHubClass.getEStructuralFeature("hubName"), "Lab Sensors");

            EObject temp = testPackage.getEFactoryInstance().create(tempSensorClass);
            temp.eSet(tempSensorClass.getEStructuralFeature("sensorId"), "sensor-001");
            temp.eSet(tempSensorClass.getEStructuralFeature("celsius"), 22.5);

            EReference sensorsRef = (EReference) sensorHubClass.getEStructuralFeature("sensors");
            List<EObject> sensors = (List<EObject>) hub.eGet(sensorsRef);
            sensors.add(temp);

            // Serialize
            String json = serialize(hub);
            System.out.println("FallbackError Round-trip JSON:\n" + json);

            // Deserialize
            EObject loaded = deserialize(json, sensorHubClass);

            // Verify
            assertNotNull(loaded);
            assertEquals(sensorHubClass, loaded.eClass());
            assertEquals("Lab Sensors", loaded.eGet(sensorHubClass.getEStructuralFeature("hubName")));

            List<EObject> loadedSensors = (List<EObject>) loaded.eGet(sensorsRef);
            assertEquals(1, loadedSensors.size());

            EObject loadedSensor = loadedSensors.get(0);
            assertEquals(tempSensorClass, loadedSensor.eClass());
            assertEquals("sensor-001", loadedSensor.eGet(tempSensorClass.getEStructuralFeature("sensorId")));
            assertEquals(22.5, (Double) loadedSensor.eGet(tempSensorClass.getEStructuralFeature("celsius")), 0.001);
        }
    }

    // ========================================================================
    // Fallback FALLBACK Strategy Tests
    // ========================================================================

    @Nested
    @DisplayName("Fallback FALLBACK Strategy")
    class FallbackFallbackStrategy {

        @Test
        @DisplayName("deserializes known discriminator in tolerant registry")
        @SuppressWarnings("unchecked")
        void deserializesKnownDiscriminator() throws IOException {
            String json = """
                {
                    "boxName": "Inbox",
                    "messages": [
                        {
                            "_type": "alert",
                            "timestamp": "2025-12-17T10:00:00Z",
                            "severity": "HIGH"
                        }
                    ]
                }
                """;

            EObject result = deserialize(json, messageBoxClass);

            assertNotNull(result);
            assertEquals(messageBoxClass, result.eClass());
            assertEquals("Inbox", result.eGet(messageBoxClass.getEStructuralFeature("boxName")));

            EReference messagesRef = (EReference) messageBoxClass.getEStructuralFeature("messages");
            List<EObject> messages = (List<EObject>) result.eGet(messagesRef);
            assertEquals(1, messages.size());

            EObject message = messages.get(0);
            assertEquals(alertMessageClass, message.eClass());
            assertEquals("2025-12-17T10:00:00Z", message.eGet(alertMessageClass.getEStructuralFeature("timestamp")));
            assertEquals("HIGH", message.eGet(alertMessageClass.getEStructuralFeature("severity")));
        }

        @Test
        @DisplayName("serializes MessageBox with discriminator values for messages")
        @SuppressWarnings("unchecked")
        void serializesWithDiscriminatorValues() throws IOException {
            EObject box = testPackage.getEFactoryInstance().create(messageBoxClass);
            box.eSet(messageBoxClass.getEStructuralFeature("boxName"), "Inbox");

            EObject alert = testPackage.getEFactoryInstance().create(alertMessageClass);
            alert.eSet(alertMessageClass.getEStructuralFeature("timestamp"), "2025-12-17T10:00:00Z");
            alert.eSet(alertMessageClass.getEStructuralFeature("severity"), "HIGH");

            EObject generic = testPackage.getEFactoryInstance().create(genericMessageClass);
            generic.eSet(genericMessageClass.getEStructuralFeature("timestamp"), "2025-12-17T11:00:00Z");
            generic.eSet(genericMessageClass.getEStructuralFeature("rawPayload"), "{\"data\":42}");

            EReference messagesRef = (EReference) messageBoxClass.getEStructuralFeature("messages");
            List<EObject> messages = (List<EObject>) box.eGet(messagesRef);
            messages.add(alert);
            messages.add(generic);

            String json = serialize(box);
            System.out.println("FallbackFallback Serialization JSON:\n" + json);

            assertTrue(json.contains("\"alert\""),
                    "JSON should contain discriminator value 'alert' for AlertMessage");
            assertTrue(json.contains("\"HIGH\""), "JSON should contain severity");
            assertTrue(json.contains("{\"data\":42}") || json.contains("\\\"data\\\":42"),
                    "JSON should contain rawPayload");
        }

        @Test
        @DisplayName("falls back to GenericMessage for unknown discriminator")
        @SuppressWarnings("unchecked")
        void fallsBackForUnknownDiscriminator() throws IOException {
            String json = """
                {
                    "boxName": "Inbox",
                    "messages": [
                        {
                            "_type": "notification",
                            "timestamp": "2025-12-17T11:00:00Z"
                        }
                    ]
                }
                """;

            EObject result = deserialize(json, messageBoxClass);

            assertNotNull(result);
            assertEquals(messageBoxClass, result.eClass());

            EReference messagesRef = (EReference) messageBoxClass.getEStructuralFeature("messages");
            List<EObject> messages = (List<EObject>) result.eGet(messagesRef);
            assertEquals(1, messages.size());

            // Unknown discriminator "notification" should fall back to GenericMessage
            EObject message = messages.get(0);
            assertEquals(genericMessageClass, message.eClass(),
                    "Unknown discriminator should fall back to GenericMessage");
            assertEquals("2025-12-17T11:00:00Z", message.eGet(genericMessageClass.getEStructuralFeature("timestamp")));
        }

        @Test
        @DisplayName("round-trips known and unknown discriminators in tolerant container")
        @SuppressWarnings("unchecked")
        void roundTripsMixedDiscriminators() throws IOException {
            // Create MessageBox with AlertMessage and GenericMessage
            EObject box = testPackage.getEFactoryInstance().create(messageBoxClass);
            box.eSet(messageBoxClass.getEStructuralFeature("boxName"), "Mixed Box");

            EObject alert = testPackage.getEFactoryInstance().create(alertMessageClass);
            alert.eSet(alertMessageClass.getEStructuralFeature("timestamp"), "2025-12-17T10:00:00Z");
            alert.eSet(alertMessageClass.getEStructuralFeature("severity"), "CRITICAL");

            EObject generic = testPackage.getEFactoryInstance().create(genericMessageClass);
            generic.eSet(genericMessageClass.getEStructuralFeature("timestamp"), "2025-12-17T11:00:00Z");
            generic.eSet(genericMessageClass.getEStructuralFeature("rawPayload"), "{\"data\":42}");

            EReference messagesRef = (EReference) messageBoxClass.getEStructuralFeature("messages");
            List<EObject> messages = (List<EObject>) box.eGet(messagesRef);
            messages.add(alert);
            messages.add(generic);

            // Serialize
            String json = serialize(box);
            System.out.println("Fallback Round-trip JSON:\n" + json);

            // Verify JSON contains alert discriminator
            assertTrue(json.contains("\"alert\"") || json.contains("\"AlertMessage\""),
                    "JSON should contain discriminator for AlertMessage");

            // Deserialize
            EObject loaded = deserialize(json, messageBoxClass);

            assertNotNull(loaded);
            assertEquals("Mixed Box", loaded.eGet(messageBoxClass.getEStructuralFeature("boxName")));

            List<EObject> loadedMessages = (List<EObject>) loaded.eGet(messagesRef);
            assertEquals(2, loadedMessages.size());

            // First message: AlertMessage
            assertEquals(alertMessageClass, loadedMessages.get(0).eClass());
            assertEquals("CRITICAL",
                    loadedMessages.get(0).eGet(alertMessageClass.getEStructuralFeature("severity")));

            // Second message: GenericMessage
            assertEquals(genericMessageClass, loadedMessages.get(1).eClass());
            assertEquals("{\"data\":42}",
                    loadedMessages.get(1).eGet(genericMessageClass.getEStructuralFeature("rawPayload")));
        }
    }

    // ========================================================================
    // Root-level Discriminator Mapping Tests
    // ========================================================================

    @Nested
    @DisplayName("Root-level Discriminator Mapping")
    class RootLevelDiscriminatorMapping {

        @Test
        @DisplayName("serializes TempSensor as root with discriminator value")
        void serializesTempSensorAsRoot() throws IOException {
            EObject sensor = testPackage.getEFactoryInstance().create(tempSensorClass);
            sensor.eSet(tempSensorClass.getEStructuralFeature("sensorId"), "sensor-001");
            sensor.eSet(tempSensorClass.getEStructuralFeature("celsius"), 22.5);

            String json = serialize(sensor);
            System.out.println("Root TempSensor Serialization JSON:\n" + json);

            // Should use discriminator value "temp" instead of full URI
            assertTrue(json.contains("\"temp\""),
                    "JSON should contain discriminator value 'temp' for TempSensor");
            assertTrue(json.contains("\"sensor-001\""), "JSON should contain sensorId");
        }

        @Test
        @DisplayName("deserializes TempSensor as root with discriminator value")
        void deserializesTempSensorAsRoot() throws IOException {
            String json = """
                {
                    "_type": "temp",
                    "sensorId": "sensor-001",
                    "celsius": 22.5
                }
                """;

            EObject result = deserialize(json, sensorClass);

            assertNotNull(result, "Deserialized object should not be null");
            assertEquals(tempSensorClass, result.eClass(),
                    "Should resolve to TempSensor via strict-sensors registry");
            assertEquals("sensor-001", result.eGet(tempSensorClass.getEStructuralFeature("sensorId")));
            assertEquals(22.5, (Double) result.eGet(tempSensorClass.getEStructuralFeature("celsius")), 0.001);
        }

        @Test
        @DisplayName("round-trips TempSensor as root")
        void roundTripsTempSensorAsRoot() throws IOException {
            // Create
            EObject sensor = testPackage.getEFactoryInstance().create(tempSensorClass);
            sensor.eSet(tempSensorClass.getEStructuralFeature("sensorId"), "sensor-001");
            sensor.eSet(tempSensorClass.getEStructuralFeature("celsius"), 22.5);

            // Serialize
            String json = serialize(sensor);
            System.out.println("Root TempSensor Round-trip JSON:\n" + json);

            // Deserialize with Sensor as hint (abstract base)
            EObject loaded = deserialize(json, sensorClass);

            // Verify
            assertNotNull(loaded);
            assertEquals(tempSensorClass, loaded.eClass(),
                    "Should resolve to TempSensor via discriminator mapping");
            assertEquals("sensor-001", loaded.eGet(tempSensorClass.getEStructuralFeature("sensorId")));
            assertEquals(22.5, (Double) loaded.eGet(tempSensorClass.getEStructuralFeature("celsius")), 0.001);
        }

        @Test
        @DisplayName("throws on unknown discriminator at root level with ERROR strategy")
        void throwsOnUnknownDiscriminatorAtRoot() {
            String json = """
                {
                    "_type": "unknown-sensor",
                    "sensorId": "sensor-999"
                }
                """;

            assertThrows(Exception.class, () -> deserialize(json, sensorClass),
                    "Should throw when unknown discriminator encountered at root with ERROR strategy");
        }

        @Test
        @DisplayName("deserializes AlertMessage as root with FALLBACK strategy")
        void deserializesAlertMessageAsRoot() throws IOException {
            String json = """
                {
                    "_type": "alert",
                    "timestamp": "2025-12-17T10:00:00Z",
                    "severity": "HIGH"
                }
                """;

            EObject result = deserialize(json, messageClass);

            assertNotNull(result);
            assertEquals(alertMessageClass, result.eClass(),
                    "Should resolve to AlertMessage via tolerant-messages registry");
            assertEquals("HIGH", result.eGet(alertMessageClass.getEStructuralFeature("severity")));
        }

        @Test
        @DisplayName("falls back to GenericMessage at root level for unknown discriminator")
        void fallsBackToGenericMessageAtRoot() throws IOException {
            String json = """
                {
                    "_type": "notification",
                    "timestamp": "2025-12-17T11:00:00Z"
                }
                """;

            EObject result = deserialize(json, messageClass);

            assertNotNull(result);
            assertEquals(genericMessageClass, result.eClass(),
                    "Unknown discriminator at root should fall back to GenericMessage");
            assertEquals("2025-12-17T11:00:00Z",
                    result.eGet(genericMessageClass.getEStructuralFeature("timestamp")));
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String serialize(EObject object) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());

        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootEClass) throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);

        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);

        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }
}
