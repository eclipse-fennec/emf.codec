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
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates discriminator-based type mapping (MAPPED TypeStrategy).
 * <p>
 * Instead of using full EMF URIs as type identifiers, discriminator values
 * like "temp" and "humidity" are used, defined via EAnnotations.
 *
 * @see <a href="docs/codec-v2-spec/08-discriminator-mapping.md">Spec: Discriminator Mapping</a>
 */
@DisplayName("Discriminator Mapping Examples")
class DiscriminatorMappingExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-discriminator.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass sensorNetworkClass;
    private EClass temperatureSensorClass;
    private EClass humiditySensorClass;

    private EAttribute networkNameAttr;
    private EReference sensorsRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(DiscriminatorMappingExample.class);
        pkg = ecoreHelper.loadEcoreAbsolute(ECORE);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        sensorNetworkClass = ecoreHelper.getEClass(pkg, "SensorNetwork");
        temperatureSensorClass = ecoreHelper.getEClass(pkg, "TemperatureSensor");
        humiditySensorClass = ecoreHelper.getEClass(pkg, "HumiditySensor");

        networkNameAttr = (EAttribute) ecoreHelper.getFeature(sensorNetworkClass, "name");
        sensorsRef = (EReference) ecoreHelper.getFeature(sensorNetworkClass, "sensors");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
        return new CodecResource(
                URI.createURI("test://discriminator.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);
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
    @DisplayName("Discriminator-based type resolution — round-trip with mixed sensor types")
    @SuppressWarnings("unchecked")
    void discriminatorBasedTypeResolution() throws IOException {
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
    @DisplayName("Discriminator values in JSON — _type contains 'temp'/'humidity' not URIs")
    @SuppressWarnings("unchecked")
    void discriminatorWithCustomTypeKey() throws IOException {
        EObject network = pkg.getEFactoryInstance().create(sensorNetworkClass);
        network.eSet(networkNameAttr, "Test Network");

        EObject tempSensor = pkg.getEFactoryInstance().create(temperatureSensorClass);
        tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("sensorId"), "t1");
        tempSensor.eSet(temperatureSensorClass.getEStructuralFeature("temperature"), 20.0);

        EObject humiditySensor = pkg.getEFactoryInstance().create(humiditySensorClass);
        humiditySensor.eSet(humiditySensorClass.getEStructuralFeature("sensorId"), "h1");
        humiditySensor.eSet(humiditySensorClass.getEStructuralFeature("humidity"), 55.0);

        List<EObject> sensors = (List<EObject>) network.eGet(sensorsRef);
        sensors.add(tempSensor);
        sensors.add(humiditySensor);

        String json = serialize(network);

        // Verify discriminator values are used for sensor types
        assertTrue(json.contains("\"temp\""), "Should contain discriminator 'temp'");
        assertTrue(json.contains("\"humidity\""), "Should contain discriminator 'humidity'");
        // Verify _type values are discriminators, not full EClass URIs
        assertFalse(json.contains("TemperatureSensor"),
                "Should NOT contain EClass name 'TemperatureSensor' as type");
        assertFalse(json.contains("HumiditySensor"),
                "Should NOT contain EClass name 'HumiditySensor' as type");
    }
}
