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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
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
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test for loading HistoricalRawTraffic JSON data.
 * <p>
 * This test demonstrates the issue where JSON has primitive strings for
 * travelMode and direction (e.g., "bike", "in"), but the original OpenAPI-generated
 * Ecore model incorrectly defines these as EReferences to empty EClasses.
 * </p>
 * <p>
 * The corrected model (bike-rawtraffic.ecore) defines travelMode and direction
 * as EAttributes of type EString, which correctly matches the JSON structure.
 * </p>
 * <p>
 * Test data structure:
 * <pre>
 * [
 *   {
 *     "travelMode": "bike",      // String, not object!
 *     "direction": "in",         // String, not object!
 *     "flowID": 101046725,
 *     "flowName": "...",
 *     "data": [
 *       { "timestamp": "...", "granularity": "PT15M", "counts": 0.0 },
 *       ...
 *     ]
 *   },
 *   ...
 * ]
 * </pre>
 * </p>
 *
 * @see <a href="https://github.com/eclipse/fennec-codec/issues/48">Issue #48</a>
 */
@DisplayName("Bike Raw Traffic Load Test")
class BikeRawTrafficLoadTest {

    private static final String BIKE_RAW_ECORE = "bike-rawtraffic.ecore";
    private static final String RAW_TRAFFIC_JSON = "rawTraffic.json";

    private EcoreHelper ecoreHelper;
    private EPackage bikeRawPackage;
    private MetadataWhiteboard metadataService;

    // Classes
    private EClass historicalRawTrafficClass;
    private EClass historicalRawTrafficDataClass;
    private EClass baseSerieClass;

    // HistoricalRawTraffic attributes (inherited from BaseSerie + own)
    private EAttribute travelModeAttr;
    private EAttribute directionAttr;
    private EAttribute flowIDAttr;
    private EAttribute flowNameAttr;
    private EReference dataRef;

    // HistoricalRawTraffic_data attributes
    private EAttribute timestampAttr;
    private EAttribute granularityAttr;
    private EAttribute countsAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        bikeRawPackage = ecoreHelper.loadEcore("/org/eclipse/fennec/codec/resource/" + BIKE_RAW_ECORE, BikeRawTrafficLoadTest.class);
        EPackage.Registry.INSTANCE.put(bikeRawPackage.getNsURI(), bikeRawPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(bikeRawPackage);

        // Load classes
        historicalRawTrafficClass = EcoreHelper.getEClass(bikeRawPackage, "HistoricalRawTraffic");
        historicalRawTrafficDataClass = EcoreHelper.getEClass(bikeRawPackage, "HistoricalRawTraffic_data");
        baseSerieClass = EcoreHelper.getEClass(bikeRawPackage, "BaseSerie");

        // Load BaseSerie attributes (inherited by HistoricalRawTraffic)
        travelModeAttr = (EAttribute) EcoreHelper.getFeature(baseSerieClass, "travelMode");
        directionAttr = (EAttribute) EcoreHelper.getFeature(baseSerieClass, "direction");

        // Load HistoricalRawTraffic own attributes
        flowIDAttr = (EAttribute) EcoreHelper.getFeature(historicalRawTrafficClass, "flowID");
        flowNameAttr = (EAttribute) EcoreHelper.getFeature(historicalRawTrafficClass, "flowName");
        dataRef = (EReference) EcoreHelper.getFeature(historicalRawTrafficClass, "data");

        // Load HistoricalRawTraffic_data attributes
        timestampAttr = (EAttribute) EcoreHelper.getFeature(historicalRawTrafficDataClass, "timestamp");
        granularityAttr = (EAttribute) EcoreHelper.getFeature(historicalRawTrafficDataClass, "granularity");
        countsAttr = (EAttribute) EcoreHelper.getFeature(historicalRawTrafficDataClass, "counts");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(bikeRawPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
        ConfigurationResolver config = ConfigurationResolver.defaults();
        return new CodecResource(
                URI.createURI("test://rawTraffic.json"),
                metadataService,
                config,
                null);
    }

    @Nested
    @DisplayName("Model Structure Tests")
    class ModelStructureTests {

        @Test
        @DisplayName("travelMode is an EAttribute, not EReference")
        void travelModeIsAttribute() {
            assertNotNull(travelModeAttr, "travelMode should exist");
            assertTrue(travelModeAttr instanceof EAttribute, "travelMode should be EAttribute");
            assertEquals("EString", travelModeAttr.getEType().getName(),
                    "travelMode should be of type EString");
        }

        @Test
        @DisplayName("direction is an EAttribute, not EReference")
        void directionIsAttribute() {
            assertNotNull(directionAttr, "direction should exist");
            assertTrue(directionAttr instanceof EAttribute, "direction should be EAttribute");
            assertEquals("EString", directionAttr.getEType().getName(),
                    "direction should be of type EString");
        }

        @Test
        @DisplayName("HistoricalRawTraffic extends BaseSerie")
        void historicalRawTrafficExtendBaseSerie() {
            assertTrue(historicalRawTrafficClass.getESuperTypes().contains(baseSerieClass),
                    "HistoricalRawTraffic should extend BaseSerie");
        }

        @Test
        @DisplayName("data is a containment reference to HistoricalRawTraffic_data")
        void dataIsContainmentReference() {
            assertNotNull(dataRef, "data reference should exist");
            assertTrue(dataRef.isContainment(), "data should be containment");
            assertTrue(dataRef.isMany(), "data should be multi-valued");
            assertEquals(historicalRawTrafficDataClass, dataRef.getEReferenceType(),
                    "data should reference HistoricalRawTraffic_data");
        }
    }

    @Nested
    @DisplayName("JSON Loading Tests")
    class JsonLoadingTests {

        @Test
        @DisplayName("loads JSON array with CODEC_ROOT_TYPE hint")
        void loadsJsonArrayWithRootObjectHint() throws IOException {
            CodecResource resource = createResource();

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, historicalRawTrafficClass);

            try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + RAW_TRAFFIC_JSON)) {
                assertNotNull(is, "rawTraffic.json should be found on classpath");
                resource.load(is, options);
            }

            // Verify 3 HistoricalRawTraffic objects were loaded
            assertEquals(3, resource.getContents().size(),
                    "Should load 3 HistoricalRawTraffic objects from array");

            // Verify all are instances of HistoricalRawTraffic EClass
            for (EObject obj : resource.getContents()) {
                assertEquals(historicalRawTrafficClass, obj.eClass(),
                        "All objects should be instances of 'HistoricalRawTraffic' EClass");
            }
        }

        @Test
        @DisplayName("travelMode and direction are correctly loaded as strings")
        void travelModeAndDirectionLoadedAsStrings() throws IOException {
            CodecResource resource = createResource();

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, historicalRawTrafficClass);

            try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + RAW_TRAFFIC_JSON)) {
                resource.load(is, options);
            }

            // First entry: travelMode=bike, direction=in
            EObject first = resource.getContents().get(0);
            assertEquals("bike", first.eGet(travelModeAttr),
                    "First entry travelMode should be 'bike'");
            assertEquals("in", first.eGet(directionAttr),
                    "First entry direction should be 'in'");

            // Second entry: travelMode=bike, direction=out
            EObject second = resource.getContents().get(1);
            assertEquals("bike", second.eGet(travelModeAttr),
                    "Second entry travelMode should be 'bike'");
            assertEquals("out", second.eGet(directionAttr),
                    "Second entry direction should be 'out'");

            // Third entry: travelMode=pedestrian, direction=in
            EObject third = resource.getContents().get(2);
            assertEquals("pedestrian", third.eGet(travelModeAttr),
                    "Third entry travelMode should be 'pedestrian'");
            assertEquals("in", third.eGet(directionAttr),
                    "Third entry direction should be 'in'");
        }

        @Test
        @DisplayName("flowID and flowName are correctly loaded")
        void flowIdAndFlowNameLoaded() throws IOException {
            CodecResource resource = createResource();

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, historicalRawTrafficClass);

            try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + RAW_TRAFFIC_JSON)) {
                resource.load(is, options);
            }

            EObject first = resource.getContents().get(0);
            assertEquals(new BigInteger("101046725"), first.eGet(flowIDAttr),
                    "First entry flowID should be 101046725");
            assertEquals("Jena-Goldbergrampe Fahrräder Ri. Zentrum", first.eGet(flowNameAttr),
                    "First entry flowName should match");

            EObject second = resource.getContents().get(1);
            assertEquals(new BigInteger("101046726"), second.eGet(flowIDAttr),
                    "Second entry flowID should be 101046726");

            EObject third = resource.getContents().get(2);
            assertEquals(new BigInteger("101046727"), third.eGet(flowIDAttr),
                    "Third entry flowID should be 101046727");
        }

        @Test
        @DisplayName("nested data array is correctly loaded")
        @SuppressWarnings("unchecked")
        void nestedDataArrayLoaded() throws IOException {
            CodecResource resource = createResource();

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, historicalRawTrafficClass);

            try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + RAW_TRAFFIC_JSON)) {
                resource.load(is, options);
            }

            // First entry has 4 data items
            EObject first = resource.getContents().get(0);
            List<EObject> firstData = (List<EObject>) first.eGet(dataRef);
            assertEquals(4, firstData.size(), "First entry should have 4 data items");

            // Verify first data item
            EObject firstDataItem = firstData.get(0);
            assertEquals("2026-01-08T00:00:00+01:00", firstDataItem.eGet(timestampAttr));
            assertEquals("PT15M", firstDataItem.eGet(granularityAttr));
            assertEquals(0.0, firstDataItem.eGet(countsAttr));

            // Verify third data item (has counts = 1.0)
            EObject thirdDataItem = firstData.get(2);
            assertEquals("2026-01-08T00:30:00+01:00", thirdDataItem.eGet(timestampAttr));
            assertEquals(1.0, thirdDataItem.eGet(countsAttr));

            // Verify fourth data item (has counts = 2.0)
            EObject fourthDataItem = firstData.get(3);
            assertEquals("2026-01-08T06:00:00+01:00", fourthDataItem.eGet(timestampAttr));
            assertEquals(2.0, fourthDataItem.eGet(countsAttr));
        }

        @Test
        @DisplayName("second entry data is correctly loaded")
        @SuppressWarnings("unchecked")
        void secondEntryDataLoaded() throws IOException {
            CodecResource resource = createResource();

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, historicalRawTrafficClass);

            try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + RAW_TRAFFIC_JSON)) {
                resource.load(is, options);
            }

            // Second entry has 2 data items
            EObject second = resource.getContents().get(1);
            List<EObject> secondData = (List<EObject>) second.eGet(dataRef);
            assertEquals(2, secondData.size(), "Second entry should have 2 data items");

            // Verify second data item (has counts = 3.0)
            EObject secondDataItem = secondData.get(1);
            assertEquals("2026-01-08T07:30:00+01:00", secondDataItem.eGet(timestampAttr));
            assertEquals(3.0, secondDataItem.eGet(countsAttr));
        }

        @Test
        @DisplayName("third entry data is correctly loaded")
        @SuppressWarnings("unchecked")
        void thirdEntryDataLoaded() throws IOException {
            CodecResource resource = createResource();

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, historicalRawTrafficClass);

            try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + RAW_TRAFFIC_JSON)) {
                resource.load(is, options);
            }

            // Third entry has 1 data item
            EObject third = resource.getContents().get(2);
            List<EObject> thirdData = (List<EObject>) third.eGet(dataRef);
            assertEquals(1, thirdData.size(), "Third entry should have 1 data item");

            // Verify data item (has counts = 5.0)
            EObject dataItem = thirdData.get(0);
            assertEquals("2026-01-08T08:00:00+01:00", dataItem.eGet(timestampAttr));
            assertEquals(5.0, dataItem.eGet(countsAttr));
        }

        @Test
        @DisplayName("no errors when loading valid JSON")
        void noErrorsForValidJson() throws IOException {
            CodecResource resource = createResource();

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, historicalRawTrafficClass);

            try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + RAW_TRAFFIC_JSON)) {
                resource.load(is, options);
            }

            assertTrue(resource.getErrors().isEmpty(),
                    "Should have no errors: " + resource.getErrors());
        }
    }
}
