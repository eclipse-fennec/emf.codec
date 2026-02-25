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
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataHandler;
import org.eclipse.fennec.model.metadata.api.MetadataService;
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
 * OSGi integration tests for externally managed TypeDiscriminatorService.
 * <p>
 * Demonstrates the recommended OSGi approach: the {@link TypeDiscriminatorService}
 * is registered as a {@link MetadataHandler} OSGi service so that the
 * {@code MetadataServiceComponent} whiteboard picks it up automatically and notifies
 * it when EPackages are registered or unregistered.
 * </p>
 *
 * @see DiscriminatorMappingExample
 * @see TypeDiscriminatorService
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("External TypeDiscriminator OSGi Examples")
public class ExternalTypeDiscriminatorExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-discriminator.ecore";

    @InjectService
    MetadataService metadataService;

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;
    private ServiceRegistration<MetadataHandler> typeHandlerReg;
    private TypeDiscriminatorReader typeDiscriminatorReader;

    private EClass sensorNetworkClass;
    private EClass temperatureSensorClass;
    private EClass humiditySensorClass;

    private EAttribute networkNameAttr;
    private EReference sensorsRef;

    @BeforeEach
    public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, ExternalTypeDiscriminatorExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        // Register TypeDiscriminatorService as MetadataHandler OSGi service.
        // MetadataServiceComponent.addHandler() picks it up and will notify it
        // of package registration events automatically.
        TypeDiscriminatorService typeService = new TypeDiscriminatorService();
        typeDiscriminatorReader = typeService;
        typeHandlerReg = ctx.registerService(MetadataHandler.class, typeService, null);

        // Register the EPackage — MetadataServiceComponent notifies all handlers,
        // including our TypeDiscriminatorService, via onPackageRegistered().
        pkgReg = ctx.registerService(EPackage.class, pkg, null);

        sensorNetworkClass = EcoreHelper.getEClass(pkg, "SensorNetwork");
        temperatureSensorClass = EcoreHelper.getEClass(pkg, "TemperatureSensor");
        humiditySensorClass = EcoreHelper.getEClass(pkg, "HumiditySensor");

        networkNameAttr = (EAttribute) EcoreHelper.getFeature(sensorNetworkClass, "name");
        sensorsRef = (EReference) EcoreHelper.getFeature(sensorNetworkClass, "sensors");
    }

    @AfterEach
    public void tearDown() {
        pkgReg.unregister();
        typeHandlerReg.unregister();
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
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

        String json = serialize(network);

        assertTrue(json.contains("\"temp\""), "Should contain discriminator 'temp'");
        assertTrue(json.contains("\"humidity\""), "Should contain discriminator 'humidity'");
        assertFalse(json.contains("TemperatureSensor"),
                "Should NOT contain EClass name 'TemperatureSensor' as type");

        EObject loaded = deserialize(json, sensorNetworkClass);

        assertNotNull(loaded);
        assertEquals("IoT Hub", loaded.eGet(networkNameAttr));

        List<EObject> loadedSensors = (List<EObject>) loaded.eGet(sensorsRef);
        assertEquals(2, loadedSensors.size());

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
        assertNotNull(typeDiscriminatorReader.getDiscriminatorValueFromAny(temperatureSensorClass),
                "Should have discriminator for TemperatureSensor");
        assertEquals("temp", typeDiscriminatorReader.getDiscriminatorValueFromAny(temperatureSensorClass));
        assertEquals("humidity", typeDiscriminatorReader.getDiscriminatorValueFromAny(humiditySensorClass));
    }
}
