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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorReader;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates externally managed TypeDiscriminatorService via MetadataHandler.
 * <p>
 * Instead of creating a fresh TypeDiscriminatorService on every save()/load(),
 * this example registers a TypeDiscriminatorService as a MetadataHandler on the
 * MetadataWhiteboard. The service is then automatically updated when packages
 * are registered or unregistered, and passed to CodecResource as a
 * TypeDiscriminatorReader.
 * </p>
 * <p>
 * This is the recommended approach in OSGi environments where the whiteboard
 * already knows about package lifecycle events.
 * </p>
 *
 * @see DiscriminatorMappingExample
 * @see TypeDiscriminatorService
 * @see org.eclipse.fennec.model.metadata.api.MetadataHandler
 */
@DisplayName("External TypeDiscriminator Examples")
class ExternalTypeDiscriminatorExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-discriminator.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;
    private TypeDiscriminatorReader typeDiscriminatorReader;

    private EClass sensorNetworkClass;
    private EClass temperatureSensorClass;
    private EClass humiditySensorClass;

    private EAttribute networkNameAttr;
    private EReference sensorsRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, ExternalTypeDiscriminatorExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        // Create a TypeDiscriminatorService and register it as MetadataHandler.
        // The factory's varargs parameter registers the handler on the whiteboard.
        TypeDiscriminatorService typeService = new TypeDiscriminatorService();
        metadataService = MetadataServiceFactory.create(typeService);
        typeDiscriminatorReader = typeService;

        // When registerPackage is called, the handler is notified automatically
        metadataService.registerPackage(pkg);

        sensorNetworkClass = EcoreHelper.getEClass(pkg, "SensorNetwork");
        temperatureSensorClass = EcoreHelper.getEClass(pkg, "TemperatureSensor");
        humiditySensorClass = EcoreHelper.getEClass(pkg, "HumiditySensor");

        networkNameAttr = (EAttribute) EcoreHelper.getFeature(sensorNetworkClass, "name");
        sensorsRef = (EReference) EcoreHelper.getFeature(sensorNetworkClass, "sensors");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
        // Pass the externally managed TypeDiscriminatorReader to CodecResource
        return new CodecResource(
                URI.createURI("test://external-discriminator.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null, null, null,
                typeDiscriminatorReader);
    }

    private String serialize(EObject object) throws IOException {
        CodecResource resource = createResource();
        resource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());
        return out.toString(StandardCharsets.UTF_8);
    }

    private EObject deserialize(String json, EClass rootType) throws IOException {
        CodecResource resource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    @Test
    @DisplayName("Externally managed TypeDiscriminator — round-trip with mixed sensor types")
    @SuppressWarnings("unchecked")
    void externalTypeDiscriminatorRoundTrip() throws IOException {
        EObject network = pkg.getEFactoryInstance().create(sensorNetworkClass);
        network.eSet(networkNameAttr, "IoT Hub");

        EObject tempSensor = pkg.getEFactoryInstance().create(temperatureSensorClass);
        tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("sensorId"), "s1");
        tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("location"), "Room A");
        tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("temperature"), 22.5);

        EObject humiditySensor = pkg.getEFactoryInstance().create(humiditySensorClass);
        humiditySensor.eSet(humiditySensorClass.getEStructuralFeature("sensorId"), "s2");
        humiditySensor.eSet(humiditySensorClass.getEStructuralFeature("location"), "Room B");
        humiditySensor.eSet(humiditySensorClass.getEStructuralFeature("humidity"), 65.0);

        List<EObject> sensors = (List<EObject>) network.eGet(sensorsRef);
        sensors.add(tempSensor);
        sensors.add(humiditySensor);

        // Serialize
        String json = serialize(network);

        // Verify discriminator values are used (not full URIs)
        assertTrue(json.contains("\"temp\""), "Should contain discriminator 'temp'");
        assertTrue(json.contains("\"humidity\""), "Should contain discriminator 'humidity'");
        assertFalse(json.contains("TemperatureSensor"),
                "Should NOT contain EClass name 'TemperatureSensor' as type");

        // Deserialize
        EObject loaded = deserialize(json, sensorNetworkClass);

        assertNotNull(loaded);
        assertEquals("IoT Hub", loaded.eGet(networkNameAttr));

        List<EObject> loadedSensors = (List<EObject>) loaded.eGet(sensorsRef);
        assertEquals(2, loadedSensors.size());

        // Verify correct concrete types resolved
        assertEquals(temperatureSensorClass, loadedSensors.get(0).eClass());
        assertEquals(22.5, (Double) loadedSensors.get(0).eGet(
                temperatureSensorClass.getEStructuralFeature("temperature")), 0.001);

        assertEquals(humiditySensorClass, loadedSensors.get(1).eClass());
        assertEquals(65.0, (Double) loadedSensors.get(1).eGet(
                humiditySensorClass.getEStructuralFeature("humidity")), 0.001);
    }

    @Test
    @DisplayName("TypeDiscriminator is populated via handler — verify mappings exist")
    void typeDiscriminatorPopulatedViaHandler() {
        // The TypeDiscriminatorService was registered as a handler BEFORE registerPackage.
        // Verify that the handler received the package registration callback
        // and the discriminator mappings are available.
        assertNotNull(typeDiscriminatorReader.getDiscriminatorValueFromAny(temperatureSensorClass),
                "Should have discriminator for TemperatureSensor");
        assertEquals("temp", typeDiscriminatorReader.getDiscriminatorValueFromAny(temperatureSensorClass));
        assertEquals("humidity", typeDiscriminatorReader.getDiscriminatorValueFromAny(humiditySensorClass));
    }
}
