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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A class that merely has features named {@code key} and {@code value} is not a map entry
 * (issue #291).
 * <p>
 * The codec used to guess map entries from those two feature names. A containment list of such a
 * class was then written as a JSON object of key/value pairs, and every other feature was dropped.
 * </p>
 */
@DisplayName("A class with key and value features is not a map entry")
class KeyValueClassRoundTripTest {

    private static final String NS_URI = "urn:codec:keyvalue:test";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass changeClass;
    private EClass slotClass;
    private EAttribute featureUri;
    private EAttribute key;
    private EAttribute value;
    private EReference state;

    @BeforeEach
    void setUp() {
        EcoreFactory f = EcoreFactory.eINSTANCE;

        slotClass = f.createEClass();
        slotClass.setName("SlotValue");
        featureUri = attribute("featureUri");
        key = attribute("key");
        value = attribute("value");
        slotClass.getEStructuralFeatures().addAll(List.of(featureUri, key, value));

        changeClass = f.createEClass();
        changeClass.setName("Change");
        state = f.createEReference();
        state.setName("state");
        state.setEType(slotClass);
        state.setContainment(true);
        state.setUpperBound(-1);
        changeClass.getEStructuralFeatures().add(state);

        testPackage = f.createEPackage();
        testPackage.setName("keyvalue");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("keyvalue");
        testPackage.getEClassifiers().addAll(List.of(changeClass, slotClass));

        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(testPackage);
        EPackage.Registry.INSTANCE.put(NS_URI, testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    @Test
    @DisplayName("all features of a key/value class survive a round trip")
    @SuppressWarnings("unchecked")
    void allFeaturesSurviveRoundTrip() throws IOException {
        EObject change = testPackage.getEFactoryInstance().create(changeClass);
        EObject slot = testPackage.getEFactoryInstance().create(slotClass);
        slot.eSet(featureUri, "urn:sensor#//Reading/temperature");
        slot.eSet(value, "24.5");
        ((List<EObject>) change.eGet(state)).add(slot);

        CodecResource out = newResource();
        out.getContents().add(change);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        out.save(bytes, null);

        CodecResource in = newResource();
        in.load(new ByteArrayInputStream(bytes.toByteArray()), null);

        EObject read = ((List<EObject>) in.getContents().get(0).eGet(state)).get(0);
        assertEquals("urn:sensor#//Reading/temperature", read.eGet(featureUri),
                "featureUri was dropped, written: " + bytes);
        assertEquals("24.5", read.eGet(value), "written: " + bytes);
    }

    private static EAttribute attribute(String name) {
        EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
        attribute.setName(name);
        attribute.setEType(EcorePackage.Literals.ESTRING);
        return attribute;
    }

    private CodecResource newResource() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of())
                .build();
        return new CodecResource(URI.createURI("keyvalue.json"), metadataService, resolver, null);
    }
}
