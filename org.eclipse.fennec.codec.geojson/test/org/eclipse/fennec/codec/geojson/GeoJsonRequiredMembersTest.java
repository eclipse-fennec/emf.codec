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
package org.eclipse.fennec.codec.geojson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.geojson.Coordinates;
import org.geojson.Feature;
import org.geojson.GeoJsonFactory;
import org.geojson.GeoJsonPackage;
import org.geojson.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Members RFC 7946 requires are written even when they carry no value (issue #228).
 * <p>
 * The codec's value gate drops {@code null} values and empty lists by default. For GeoJSON that
 * turns a valid object into an invalid one: a Feature "MUST have a member with the name
 * geometry" (§3.2) and one named properties, a FeatureCollection one named features (§3.3), a
 * GeometryCollection one named geometries (§3.1.8), and every other geometry one named
 * coordinates, with an empty array for an empty geometry (§3.1).
 * </p>
 */
@DisplayName("GeoJSON members required by RFC 7946")
class GeoJsonRequiredMembersTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final GeoJsonFactory FACTORY = GeoJsonFactory.eINSTANCE;

    private MetadataWhiteboard metadataService;

    @BeforeEach
    void setUp() {
        EPackage.Registry.INSTANCE.put(GeoJsonPackage.eNS_URI, GeoJsonPackage.eINSTANCE);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(GeoJsonPackage.eINSTANCE);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(GeoJsonPackage.eNS_URI);
    }

    @Nested
    @DisplayName("Writing")
    class Writing {

        @Test
        @DisplayName("a Feature without geometry writes \"geometry\": null (unlocated feature)")
        void featureWithoutGeometry() throws IOException {
            Feature feature = FACTORY.createFeature();

            JsonNode written = JSON.readTree(save(feature));

            assertTrue(written.has("geometry"), () -> "written: " + written);
            assertTrue(written.get("geometry").isNull(), () -> "written: " + written);
        }

        @Test
        @DisplayName("a Feature without properties writes \"properties\": null")
        void featureWithoutProperties() throws IOException {
            Feature feature = FACTORY.createFeature();
            feature.setGeometry(point(1.5, 2.5));

            JsonNode written = JSON.readTree(save(feature));

            assertTrue(written.has("properties"), () -> "written: " + written);
            assertTrue(written.get("properties").isNull(), () -> "written: " + written);
        }

        @Test
        @DisplayName("an empty FeatureCollection writes \"features\": []")
        void emptyFeatureCollection() throws IOException {
            JsonNode written = JSON.readTree(save(FACTORY.createFeatureCollection()));

            assertEmptyArray(written, "features");
        }

        @Test
        @DisplayName("an empty GeometryCollection writes \"geometries\": []")
        void emptyGeometryCollection() throws IOException {
            JsonNode written = JSON.readTree(save(FACTORY.createGeometryCollection()));

            assertEmptyArray(written, "geometries");
        }

        static Stream<Arguments> emptyGeometries() {
            return Stream.of(
                    Arguments.of("Point", (Supplier<EObject>) FACTORY::createPoint),
                    Arguments.of("MultiPoint", (Supplier<EObject>) FACTORY::createMultiPoint),
                    Arguments.of("LineString", (Supplier<EObject>) FACTORY::createLineString),
                    Arguments.of("Polygon", (Supplier<EObject>) FACTORY::createPolygon),
                    Arguments.of("MultiLineString", (Supplier<EObject>) FACTORY::createMultiLineString),
                    Arguments.of("MultiPolygon", (Supplier<EObject>) FACTORY::createMultiPolygon));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("emptyGeometries")
        @DisplayName("an empty geometry writes \"coordinates\": []")
        void emptyGeometryWritesEmptyCoordinates(String name, Supplier<EObject> geometry) throws IOException {
            JsonNode written = JSON.readTree(save(geometry.get()));

            assertEmptyArray(written, "coordinates");
        }

        @Test
        @DisplayName("a Feature with geometry and properties still writes no extra members")
        void locatedFeatureIsUnchanged() throws IOException {
            Feature feature = FACTORY.createFeature();
            feature.setId("f1");
            feature.setGeometry(point(1.5, 2.5));

            JsonNode written = JSON.readTree(save(feature));

            assertEquals(JSON.readTree("""
                    {"type":"Feature","id":"f1","geometry":{"type":"Point","coordinates":[1.5,2.5]},
                     "properties":null}"""), written);
        }
    }

    @Nested
    @DisplayName("Reading")
    class Reading {

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {
                "{\"type\":\"Feature\",\"geometry\":null,\"properties\":null}",
                "{\"type\":\"Feature\",\"id\":\"f1\",\"geometry\":null,\"properties\":null}",
                "{\"type\":\"FeatureCollection\",\"features\":[]}",
                "{\"type\":\"GeometryCollection\",\"geometries\":[]}",
                "{\"type\":\"Point\",\"coordinates\":[]}",
                "{\"type\":\"MultiPoint\",\"coordinates\":[]}",
                "{\"type\":\"LineString\",\"coordinates\":[]}",
                "{\"type\":\"Polygon\",\"coordinates\":[]}",
                "{\"type\":\"MultiLineString\",\"coordinates\":[]}",
                "{\"type\":\"MultiPolygon\",\"coordinates\":[]}" })
        @DisplayName("a document with the required members empty round-trips unchanged")
        void roundTrip(String json) throws IOException {
            String written = save(load(json));

            assertEquals(JSON.readTree(json), JSON.readTree(written), () -> "written: " + written);
        }

        @Test
        @DisplayName("\"geometry\": null loads as an unlocated feature")
        void nullGeometryLoads() throws IOException {
            Feature feature = (Feature) load("{\"type\":\"Feature\",\"geometry\":null,\"properties\":null}");

            assertNull(feature.getGeometry());
            assertNull(feature.getProperties());
        }

        @Test
        @DisplayName("a number id is read into the string id (RFC 7946 §3.2 allows both)")
        void numberIdIsReadAsString() throws IOException {
            Feature feature = (Feature) load(
                    "{\"type\":\"Feature\",\"id\":42,\"geometry\":null,\"properties\":null}");

            assertEquals("42", feature.getId());
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static Point point(double longitude, double latitude) {
        Coordinates coordinates = FACTORY.createCoordinates();
        coordinates.setLongitude(longitude);
        coordinates.setLatitude(latitude);
        Point point = FACTORY.createPoint();
        point.setCoordinates(coordinates);
        return point;
    }

    private static void assertEmptyArray(JsonNode written, String key) {
        assertTrue(written.has(key), () -> "missing \"" + key + "\", written: " + written);
        assertTrue(written.get(key).isArray() && written.get(key).isEmpty(),
                () -> "\"" + key + "\" is not an empty array, written: " + written);
    }

    private EObject load(String json) throws IOException {
        GeoJsonResourceImpl resource = new GeoJsonResourceImpl(URI.createURI("test://required.geojson"),
                metadataService);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), new HashMap<>());
        assertTrue(resource.getErrors().isEmpty(), () -> "errors=" + resource.getErrors());
        assertEquals(1, resource.getContents().size());
        return resource.getContents().get(0);
    }

    private String save(EObject object) throws IOException {
        GeoJsonResourceImpl resource = new GeoJsonResourceImpl(URI.createURI("test://required.geojson"),
                metadataService);
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());
        assertTrue(resource.getErrors().isEmpty(), () -> "errors=" + resource.getErrors());
        return out.toString(StandardCharsets.UTF_8);
    }
}
