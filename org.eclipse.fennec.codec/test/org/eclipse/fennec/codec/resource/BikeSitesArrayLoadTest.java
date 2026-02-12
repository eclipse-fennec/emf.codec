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
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for loading a JSON array of site objects from external data.
 * <p>
 * This test demonstrates loading a real-world JSON file (sites.json) that contains
 * an array of bike counting sites without any type information in the JSON.
 * The test uses CODEC_ROOT_TYPE to specify the expected EClass for all array elements.
 * </p>
 * <p>
 * Test data comes from the docs/example folder:
 * <ul>
 *   <li>bike.ecore - Ecore model defining the 'site' EClass</li>
 *   <li>sites.json - JSON array with 6 site objects</li>
 * </ul>
 * </p>
 * <p>
 * Migrated from {@code org.eclipse.fennec.codec.v2.resource.BikeSitesArrayLoadTest}
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#187-root-element--multiple-contents">Spec 18.7</a>
 */
@DisplayName("Bike Sites Array Load Test")
class BikeSitesArrayLoadTest {

    private static final String BIKE_ECORE = "bike.ecore";
    private static final String SITES_JSON = "sites.json";

    private EcoreHelper ecoreHelper;
    private EPackage bikePackage;
    private MetadataWhiteboard metadataService;

    private EClass siteClass;
    private EClass locationClass;

    // Site attributes
    private EAttribute idAttribute;
    private EAttribute nameAttribute;
    private EAttribute descriptionAttribute;
    private EAttribute firstDataAttribute;
    private EAttribute granularityAttribute;
    private EAttribute directionalAttribute;
    private EAttribute hasTimestampedDataAttribute;
    private EAttribute hasWeatherAttribute;

    // Site references
    private EReference locationRef;

    // Location attributes
    private EAttribute latAttribute;
    private EAttribute lonAttribute;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(BikeSitesArrayLoadTest.class);
        bikePackage = ecoreHelper.loadEcoreAbsolute("/org/eclipse/fennec/codec/resource/" + BIKE_ECORE);
        EPackage.Registry.INSTANCE.put(bikePackage.getNsURI(), bikePackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(bikePackage);

        // Load site EClass (note: lowercase 'site' in the ecore)
        siteClass = ecoreHelper.getEClass(bikePackage, "site");
        locationClass = ecoreHelper.getEClass(bikePackage, "Location");

        // Load site attributes
        idAttribute = (EAttribute) ecoreHelper.getFeature(siteClass, "id");
        nameAttribute = (EAttribute) ecoreHelper.getFeature(siteClass, "name");
        descriptionAttribute = (EAttribute) ecoreHelper.getFeature(siteClass, "description");
        firstDataAttribute = (EAttribute) ecoreHelper.getFeature(siteClass, "firstData");
        granularityAttribute = (EAttribute) ecoreHelper.getFeature(siteClass, "granularity");
        directionalAttribute = (EAttribute) ecoreHelper.getFeature(siteClass, "directional");
        hasTimestampedDataAttribute = (EAttribute) ecoreHelper.getFeature(siteClass, "hasTimestampedData");
        hasWeatherAttribute = (EAttribute) ecoreHelper.getFeature(siteClass, "hasWeather");

        // Load site references
        locationRef = (EReference) ecoreHelper.getFeature(siteClass, "location");

        // Load location attributes
        latAttribute = (EAttribute) ecoreHelper.getFeature(locationClass, "lat");
        lonAttribute = (EAttribute) ecoreHelper.getFeature(locationClass, "lon");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(bikePackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private CodecResource createResource() {
        ConfigurationResolver config = ConfigurationResolver.defaults();
        return new CodecResource(
                URI.createURI("test://sites.json"),
                metadataService,
                config,
                null);
    }

    @Test
    @DisplayName("loads JSON array using CODEC_ROOT_TYPE hint")
    void loadsJsonArrayWithRootObjectHint() throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, siteClass);

        try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + SITES_JSON)) {
            assertNotNull(is, "sites.json should be found on classpath");
            resource.load(is, options);
        }

        // Verify 6 sites were loaded
        assertEquals(6, resource.getContents().size(),
                "Should load 6 site objects from array");

        // Verify all are instances of site EClass
        for (EObject obj : resource.getContents()) {
            assertEquals(siteClass, obj.eClass(),
                    "All objects should be instances of 'site' EClass");
        }
    }

    @Test
    @DisplayName("verifies first site data - Jena-Goldbergrampe")
    void verifiesFirstSiteData() throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, siteClass);

        try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + SITES_JSON)) {
            resource.load(is, options);
        }

        EObject firstSite = resource.getContents().get(0);

        // Verify attributes
        assertEquals(new BigInteger("100046725"), firstSite.eGet(idAttribute));
        assertEquals("Jena-Goldbergrampe", firstSite.eGet(nameAttribute));
        assertEquals("", firstSite.eGet(descriptionAttribute));
        assertEquals("2018-08-03T00:00:00+02:00", firstSite.eGet(firstDataAttribute));
        assertEquals("PT15M", firstSite.eGet(granularityAttribute));
        assertEquals(Boolean.TRUE, firstSite.eGet(directionalAttribute));
        assertEquals(Boolean.FALSE, firstSite.eGet(hasTimestampedDataAttribute));
        assertEquals(Boolean.TRUE, firstSite.eGet(hasWeatherAttribute));

        // Verify location (contained object)
        EObject location = (EObject) firstSite.eGet(locationRef);
        assertNotNull(location, "Location should be deserialized");
        assertEquals(locationClass, location.eClass());
        assertEquals(50.88864952669239, (Double) location.eGet(latAttribute), 0.00001);
        assertEquals(11.61251798272133, (Double) location.eGet(lonAttribute), 0.00001);
    }

    @Test
    @DisplayName("verifies all site names are loaded correctly")
    void verifiesAllSiteNames() throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, siteClass);

        try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + SITES_JSON)) {
            resource.load(is, options);
        }

        String[] expectedNames = {
            "Jena-Goldbergrampe",
            "Jena-Oberaue",
            "Bibliotheksplatz",
            "Burgauer Weg",
            "Jena-Griesbrücke",
            "Jena-Erfurter Straße"
        };

        assertEquals(expectedNames.length, resource.getContents().size());

        for (int i = 0; i < expectedNames.length; i++) {
            EObject site = resource.getContents().get(i);
            assertEquals(expectedNames[i], site.eGet(nameAttribute),
                    "Site " + i + " should have correct name");
        }
    }

    @Test
    @DisplayName("verifies all site IDs are loaded correctly")
    void verifiesAllSiteIds() throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, siteClass);

        try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + SITES_JSON)) {
            resource.load(is, options);
        }

        BigInteger[] expectedIds = {
            new BigInteger("100046725"),
            new BigInteger("100046726"),
            new BigInteger("300031319"),
            new BigInteger("300034768"),
            new BigInteger("300044830"),
            new BigInteger("300047555")
        };

        for (int i = 0; i < expectedIds.length; i++) {
            EObject site = resource.getContents().get(i);
            assertEquals(expectedIds[i], site.eGet(idAttribute),
                    "Site " + i + " should have correct ID");
        }
    }

    @Test
    @DisplayName("verifies all sites have location data")
    void verifiesAllSitesHaveLocation() throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, siteClass);

        try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + SITES_JSON)) {
            resource.load(is, options);
        }

        for (int i = 0; i < resource.getContents().size(); i++) {
            EObject site = resource.getContents().get(i);
            EObject location = (EObject) site.eGet(locationRef);

            assertNotNull(location, "Site " + i + " should have location");
            assertEquals(locationClass, location.eClass());

            Double lat = (Double) location.eGet(latAttribute);
            Double lon = (Double) location.eGet(lonAttribute);

            assertNotNull(lat, "Site " + i + " should have latitude");
            assertNotNull(lon, "Site " + i + " should have longitude");

            // All sites are in Jena, Germany area
            assertTrue(lat > 50.8 && lat < 51.0,
                    "Latitude should be in Jena area: " + lat);
            assertTrue(lon > 11.5 && lon < 11.7,
                    "Longitude should be in Jena area: " + lon);
        }
    }

    @Test
    @DisplayName("verifies last site data - Jena-Erfurter Straße")
    void verifiesLastSiteData() throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, siteClass);

        try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + SITES_JSON)) {
            resource.load(is, options);
        }

        EObject lastSite = resource.getContents().get(5);

        // Verify attributes
        assertEquals(new BigInteger("300047555"), lastSite.eGet(idAttribute));
        assertEquals("Jena-Erfurter Straße", lastSite.eGet(nameAttribute));
        assertEquals("2024-11-15T00:00:00+01:00", lastSite.eGet(firstDataAttribute));
        assertEquals("PT15M", lastSite.eGet(granularityAttribute));
        assertEquals(Boolean.TRUE, lastSite.eGet(directionalAttribute));
        assertEquals(Boolean.FALSE, lastSite.eGet(hasTimestampedDataAttribute));
        assertEquals(Boolean.TRUE, lastSite.eGet(hasWeatherAttribute));

        // Verify location
        EObject location = (EObject) lastSite.eGet(locationRef);
        assertNotNull(location, "Location should be deserialized");
        assertEquals(50.93663745057116, (Double) location.eGet(latAttribute), 0.00001);
        assertEquals(11.55916213989258, (Double) location.eGet(lonAttribute), 0.00001);
    }

    @Test
    @DisplayName("reports no errors when loading valid JSON array")
    void reportsNoErrorsForValidJson() throws IOException {
        CodecResource resource = createResource();

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, siteClass);

        try (InputStream is = getClass().getResourceAsStream("/org/eclipse/fennec/codec/resource/" + SITES_JSON)) {
            resource.load(is, options);
        }

        assertTrue(resource.getErrors().isEmpty(),
                "Should have no errors: " + resource.getErrors());

        // Note: There are warnings because the JSON has travelModes as strings ["bike"]
        // but the model expects TravelModeLabel objects. This is expected behavior
        // when the JSON doesn't fully match the model structure.
        // The codec correctly warns about this mismatch.
    }
}
