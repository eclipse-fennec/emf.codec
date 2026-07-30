/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.geojson.BoundingBox;
import org.geojson.Coordinates;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonFactory;
import org.geojson.GeoJsonPackage;
import org.geojson.Hole;
import org.geojson.LineString;
import org.geojson.MultiLineString;
import org.geojson.MultiPoint;
import org.geojson.Point;
import org.geojson.Polygon;
import org.geojson.Ring;
import org.geojson.SimpleLineString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for GeoJsonResourceImpl using the v2 codec infrastructure.
 * <p>
 * Tests GeoJSON serialization and deserialization using the pre-configured
 * GeoJsonResourceImpl which handles:
 * <ul>
 *   <li>Type key as "type" (GeoJSON standard)</li>
 *   <li>Simple type names (Point, Feature, etc.)</li>
 *   <li>Coordinates as arrays</li>
 * </ul>
 * </p>
 */
@DisplayName("GeoJsonResource Tests")
class GeoJsonResourceTest {

    private MetadataWhiteboard metadataService;
    private GeoJsonPackage geoPackage;
    private GeoJsonFactory geoFactory;

    @BeforeEach
    void setUp() {
        geoPackage = GeoJsonPackage.eINSTANCE;
        geoFactory = GeoJsonFactory.eINSTANCE;
        EPackage.Registry.INSTANCE.put(geoPackage.getNsURI(), geoPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(geoPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(geoPackage.getNsURI());
    }

    /**
     * Loads JSON using GeoJsonResourceImpl.
     */
    private <T extends EObject> T loadGeoJson(String json, Class<T> expectedType) throws IOException {
        GeoJsonResourceImpl resource = new GeoJsonResourceImpl(
                URI.createURI("test://geo.geojson"),
                metadataService);

        try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            resource.load(is, Collections.emptyMap());
        }

        if (!resource.getErrors().isEmpty()) {
            fail("Deserialization errors: " + resource.getErrors());
        }

        assertEquals(1, resource.getContents().size(), "Should have exactly one root object");
        EObject result = resource.getContents().get(0);

        if (!expectedType.isInstance(result)) {
            fail("Expected " + expectedType.getSimpleName() + " but got " + result.eClass().getName());
        }

        return expectedType.cast(result);
    }

    /**
     * Saves an EObject to JSON using GeoJsonResourceImpl.
     */
    private String saveGeoJson(EObject object) throws IOException {
        GeoJsonResourceImpl resource = new GeoJsonResourceImpl(
                URI.createURI("test://geo.geojson"),
                metadataService);

        resource.getContents().add(object);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            resource.save(os, Collections.emptyMap());

            if (!resource.getErrors().isEmpty()) {
                fail("Serialization errors: " + resource.getErrors());
            }

            return os.toString(StandardCharsets.UTF_8);
        }
    }

    /**
     * Creates a Coordinates object with longitude and latitude.
     */
    private Coordinates createCoordinates(double longitude, double latitude) {
        Coordinates coords = geoFactory.createCoordinates();
        coords.setLongitude(longitude);
        coords.setLatitude(latitude);
        return coords;
    }

    /**
     * Creates a Coordinates object with longitude, latitude, and elevation.
     */
    private Coordinates createCoordinates(double longitude, double latitude, double elevation) {
        Coordinates coords = createCoordinates(longitude, latitude);
        coords.setElevation(elevation);
        return coords;
    }

    // ========================================================================
    // Deserialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Deserialization")
    class DeserializationTests {

        @Nested
        @DisplayName("Point Geometry")
        class PointTests {

            @Test
            @DisplayName("2D point with array coordinates")
            void point2D() throws IOException {
                String json = """
                    {
                        "type": "Point",
                        "coordinates": [8.6821, 50.1109]
                    }
                    """;

                Point point = loadGeoJson(json, Point.class);
                assertNotNull(point);

                Coordinates coords = point.getCoordinates();
                assertNotNull(coords, "Coordinates should be set");
                assertEquals(8.6821, coords.getLongitude(), 0.0001);
                assertEquals(50.1109, coords.getLatitude(), 0.0001);
            }

            @Test
            @DisplayName("3D point with elevation")
            void point3D() throws IOException {
                String json = """
                    {
                        "type": "Point",
                        "coordinates": [13.405, 52.52, 34.5]
                    }
                    """;

                Point point = loadGeoJson(json, Point.class);

                Coordinates coords = point.getCoordinates();
                assertEquals(13.405, coords.getLongitude(), 0.0001);
                assertEquals(52.52, coords.getLatitude(), 0.0001);
                assertEquals(34.5, coords.getElevation(), 0.0001);
            }

            @Test
            @DisplayName("point with negative coordinates")
            void pointNegativeCoordinates() throws IOException {
                String json = """
                    {
                        "type": "Point",
                        "coordinates": [-122.4194, 37.7749]
                    }
                    """;

                Point point = loadGeoJson(json, Point.class);

                Coordinates coords = point.getCoordinates();
                assertEquals(-122.4194, coords.getLongitude(), 0.0001);
                assertEquals(37.7749, coords.getLatitude(), 0.0001);
            }
        }

        @Nested
        @DisplayName("LineString Geometry")
        class LineStringTests {

            @Test
            @DisplayName("simple linestring with 3 points")
            void simpleLineString() throws IOException {
                String json = """
                    {
                        "type": "LineString",
                        "coordinates": [
                            [0.0, 0.0],
                            [10.0, 10.0],
                            [20.0, 20.0]
                        ]
                    }
                    """;

                LineString line = loadGeoJson(json, LineString.class);
                assertNotNull(line);

                assertEquals(3, line.getCoordinates().size());

                Coordinates c0 = line.getCoordinates().get(0);
                assertEquals(0.0, c0.getLongitude(), 0.0001);
                assertEquals(0.0, c0.getLatitude(), 0.0001);

                Coordinates c2 = line.getCoordinates().get(2);
                assertEquals(20.0, c2.getLongitude(), 0.0001);
                assertEquals(20.0, c2.getLatitude(), 0.0001);
            }
        }

        @Nested
        @DisplayName("MultiPoint Geometry")
        class MultiPointTests {

            @Test
            @DisplayName("multipoint with multiple coordinates")
            void multiPoint() throws IOException {
                String json = """
                    {
                        "type": "MultiPoint",
                        "coordinates": [ [ 52.0, 12.0, 120.0 ], [ 52.0, 12.0, 120.0 ] ]
                    }
                    """;

                MultiPoint result = loadGeoJson(json, MultiPoint.class);
                assertNotNull(result);
                assertEquals(2, result.getCoordinates().size());

                Coordinates c0 = result.getCoordinates().get(0);
                assertEquals(52.0, c0.getLongitude(), 0.0001);
                assertEquals(12.0, c0.getLatitude(), 0.0001);
                assertEquals(120.0, c0.getElevation(), 0.0001);
            }
        }

        @Nested
        @DisplayName("MultiLineString Geometry")
        class MultiLineStringTests {

            @Test
            @DisplayName("multilinestring with multiple lines")
            void multiLineString() throws IOException {
                String json = """
                    {
                        "type": "MultiLineString",
                        "coordinates": [
                            [ [52.0, 12.0, 120.0], [53.0, 11.0, 123.0] ],
                            [ [52.0, 12.0, 120.0], [53.0, 11.0, 123.0] ]
                        ]
                    }
                    """;

                MultiLineString result = loadGeoJson(json, MultiLineString.class);
                assertNotNull(result);
                assertEquals(2, result.getLinesStrings().size());

                SimpleLineString line1 = result.getLinesStrings().get(0);
                assertEquals(2, line1.getCoordinates().size());
                assertEquals(52.0, line1.getCoordinates().get(0).getLongitude(), 0.0001);
                assertEquals(12.0, line1.getCoordinates().get(0).getLatitude(), 0.0001);
            }
        }

        @Nested
        @DisplayName("Polygon Geometry")
        class PolygonTests {

            @Test
            @DisplayName("simple polygon (triangle)")
            void simplePolygon() throws IOException {
                String json = """
                    {
                        "type": "Polygon",
                        "coordinates": [
                            [
                                [0.0, 0.0],
                                [10.0, 0.0],
                                [5.0, 10.0],
                                [0.0, 0.0]
                            ]
                        ]
                    }
                    """;

                Polygon polygon = loadGeoJson(json, Polygon.class);
                assertNotNull(polygon);

                assertNotNull(polygon.getExteriorRing());
                assertEquals(4, polygon.getExteriorRing().getCoordinates().size());

                Coordinates first = polygon.getExteriorRing().getCoordinates().get(0);
                Coordinates last = polygon.getExteriorRing().getCoordinates().get(3);
                assertEquals(first.getLongitude(), last.getLongitude(), 0.0001);
                assertEquals(first.getLatitude(), last.getLatitude(), 0.0001);
            }

            @Test
            @DisplayName("polygon with hole")
            void polygonWithHole() throws IOException {
                String json = """
                    {
                        "type": "Polygon",
                        "coordinates": [
                            [
                                [0.0, 0.0],
                                [20.0, 0.0],
                                [20.0, 20.0],
                                [0.0, 20.0],
                                [0.0, 0.0]
                            ],
                            [
                                [5.0, 5.0],
                                [15.0, 5.0],
                                [15.0, 15.0],
                                [5.0, 15.0],
                                [5.0, 5.0]
                            ]
                        ]
                    }
                    """;

                Polygon polygon = loadGeoJson(json, Polygon.class);

                assertNotNull(polygon.getExteriorRing());
                assertEquals(5, polygon.getExteriorRing().getCoordinates().size());

                assertEquals(1, polygon.getInteriorHoles().size());
                assertEquals(5, polygon.getInteriorHoles().get(0).getCoordinates().size());

                Coordinates holeFirst = polygon.getInteriorHoles().get(0).getCoordinates().get(0);
                assertEquals(5.0, holeFirst.getLongitude(), 0.0001);
                assertEquals(5.0, holeFirst.getLatitude(), 0.0001);
            }
        }

        @Nested
        @DisplayName("Feature")
        class FeatureTests {

            @Test
            @DisplayName("feature with point geometry")
            void featureWithPoint() throws IOException {
                String json = """
                    {
                        "type": "Feature",
                        "id": "berlin",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [13.405, 52.52]
                        },
                        "properties": null
                    }
                    """;

                Feature feature = loadGeoJson(json, Feature.class);
                assertEquals("berlin", feature.getId());

                assertNotNull(feature.getGeometry());
                assertInstanceOf(Point.class, feature.getGeometry());

                Point point = (Point) feature.getGeometry();
                assertEquals(13.405, point.getCoordinates().getLongitude(), 0.0001);
            }
        }

        @Nested
        @DisplayName("FeatureCollection")
        class FeatureCollectionTests {

            @Test
            @DisplayName("collection with multiple features")
            void featureCollection() throws IOException {
                String json = """
                    {
                        "type": "FeatureCollection",
                        "features": [
                            {
                                "type": "Feature",
                                "id": "city-1",
                                "geometry": {
                                    "type": "Point",
                                    "coordinates": [2.3522, 48.8566]
                                },
                                "properties": null
                            },
                            {
                                "type": "Feature",
                                "id": "city-2",
                                "geometry": {
                                    "type": "Point",
                                    "coordinates": [-0.1276, 51.5074]
                                },
                                "properties": null
                            }
                        ]
                    }
                    """;

                FeatureCollection collection = loadGeoJson(json, FeatureCollection.class);
                assertEquals(2, collection.getFeatures().size());

                Feature paris = collection.getFeatures().get(0);
                assertEquals("city-1", paris.getId());

                Feature london = collection.getFeatures().get(1);
                assertEquals("city-2", london.getId());

                Point londonPoint = (Point) london.getGeometry();
                assertEquals(-0.1276, londonPoint.getCoordinates().getLongitude(), 0.0001);
                assertEquals(51.5074, londonPoint.getCoordinates().getLatitude(), 0.0001);
            }
        }

        @Nested
        @DisplayName("BoundingBox")
        class BBoxTests {

            @Test
            @DisplayName("polygon with bbox")
            void polygonWithBbox() throws IOException {
                String json = """
                    {
                        "type": "Polygon",
                        "bbox": [0.0, 0.0, 10.0, 10.0],
                        "coordinates": [
                            [
                                [0.0, 0.0],
                                [10.0, 0.0],
                                [10.0, 10.0],
                                [0.0, 10.0],
                                [0.0, 0.0]
                            ]
                        ]
                    }
                    """;

                Polygon polygon = loadGeoJson(json, Polygon.class);

                assertNotNull(polygon.getBoundingBox());
                assertNotNull(polygon.getBoundingBox().getSouthwest());
                assertNotNull(polygon.getBoundingBox().getNortheast());

                assertEquals(0.0, polygon.getBoundingBox().getSouthwest().getLongitude(), 0.0001);
                assertEquals(0.0, polygon.getBoundingBox().getSouthwest().getLatitude(), 0.0001);

                assertEquals(10.0, polygon.getBoundingBox().getNortheast().getLongitude(), 0.0001);
                assertEquals(10.0, polygon.getBoundingBox().getNortheast().getLatitude(), 0.0001);
            }

            @Test
            @DisplayName("point with 3D bbox")
            void pointWithBbox3D() throws IOException {
                String json = """
                    {
                        "type": "Point",
                        "bbox": [ 52.0, 12.0, 120.0, 52.0, 12.0, 120.0 ],
                        "coordinates": [ 52.0, 12.0, 120.0 ]
                    }
                    """;

                Point result = loadGeoJson(json, Point.class);
                assertNotNull(result);
                assertNotNull(result.getBoundingBox());
                assertNotNull(result.getBoundingBox().getNortheast());
                assertNotNull(result.getBoundingBox().getSouthwest());

                assertEquals(52.0, result.getBoundingBox().getSouthwest().getLongitude(), 0.0001);
                assertEquals(12.0, result.getBoundingBox().getSouthwest().getLatitude(), 0.0001);
                assertEquals(120.0, result.getBoundingBox().getSouthwest().getElevation(), 0.0001);
            }

            @Test
            @DisplayName("feature with bbox")
            void featureWithBbox() throws IOException {
                String json = """
                    {
                        "type": "Feature",
                        "bbox": [ 52.0, 12.0, 120.0, 52.0, 12.0, 120.0 ]
                    }
                    """;

                Feature result = loadGeoJson(json, Feature.class);
                assertNotNull(result);
                assertNotNull(result.getBoundingBox());
                assertNotNull(result.getBoundingBox().getNortheast());
                assertNotNull(result.getBoundingBox().getSouthwest());
            }
        }

        @Nested
        @DisplayName("Real-World Data")
        class RealWorldTests {

            @Test
            @DisplayName("Jena city polygon (real GeoJSON data)")
            void jenaPolygon() throws IOException {
                // Real polygon data from the original test - Jena city boundary
                String json = """
                    {
                        "type": "Polygon",
                        "bbox": [11.504092, 50.895368, 11.566935, 50.913474],
                        "coordinates": [[
                            [11.554532, 50.90168], [11.554127, 50.901214], [11.554528, 50.900991],
                            [11.554196, 50.900806], [11.553101, 50.899564], [11.552797, 50.898849],
                            [11.552712, 50.896565], [11.552282, 50.896421], [11.551836, 50.896126],
                            [11.551464, 50.896398], [11.550654, 50.895844], [11.550172, 50.89619],
                            [11.550001, 50.89586], [11.549749, 50.895793], [11.548798, 50.896242],
                            [11.548026, 50.896414], [11.547431, 50.89628], [11.544954, 50.895368],
                            [11.542608, 50.895993], [11.541526, 50.895805], [11.540404, 50.89597],
                            [11.540749, 50.896473], [11.540567, 50.896535], [11.54203, 50.897826],
                            [11.54066, 50.898225], [11.540999, 50.898748], [11.541198, 50.899531],
                            [11.54108, 50.900253], [11.541148, 50.901313], [11.540578, 50.901684],
                            [11.53719, 50.90164], [11.535231, 50.901289], [11.530252, 50.901846],
                            [11.529464, 50.902023], [11.526186, 50.902341], [11.522933, 50.903903],
                            [11.523349, 50.904504], [11.522543, 50.904434], [11.522427, 50.90488],
                            [11.521146, 50.905783], [11.51895, 50.905191], [11.517241, 50.904858],
                            [11.513599, 50.903797], [11.51147, 50.905539], [11.511694, 50.906269],
                            [11.510561, 50.906379], [11.510615, 50.906564], [11.50856, 50.906702],
                            [11.507924, 50.906704], [11.507696, 50.906601], [11.506708, 50.906938],
                            [11.505393, 50.906239], [11.504333, 50.905819], [11.504098, 50.906495],
                            [11.504185, 50.906948], [11.504092, 50.907269], [11.504276, 50.907497],
                            [11.505382, 50.908132], [11.505481, 50.908525], [11.50528, 50.908865],
                            [11.507127, 50.910712], [11.507056, 50.911474], [11.507383, 50.913114],
                            [11.508475, 50.913437], [11.50914, 50.913474], [11.509967, 50.913332],
                            [11.511671, 50.912817], [11.513102, 50.912643], [11.513641, 50.912461],
                            [11.516359, 50.913023], [11.518141, 50.913258], [11.519234, 50.913261],
                            [11.523311, 50.912463], [11.526255, 50.91212], [11.526344, 50.912095],
                            [11.525979, 50.911704], [11.52578, 50.91163], [11.525263, 50.911087],
                            [11.524727, 50.910095], [11.52471, 50.909722], [11.526668, 50.909192],
                            [11.528186, 50.90914], [11.528868, 50.908662], [11.530769, 50.909893],
                            [11.532192, 50.910479], [11.536595, 50.911412], [11.538296, 50.911976],
                            [11.539526, 50.912041], [11.542292, 50.912972], [11.549484, 50.91145],
                            [11.556112, 50.910784], [11.557463, 50.91089], [11.560743, 50.911368],
                            [11.56291, 50.911518], [11.564749, 50.912197], [11.56533, 50.912274],
                            [11.565757, 50.912226], [11.566935, 50.912521], [11.566636, 50.912208],
                            [11.565377, 50.911506], [11.564079, 50.909826], [11.562613, 50.90921],
                            [11.563746, 50.907839], [11.563981, 50.907932], [11.564818, 50.906646],
                            [11.563154, 50.906306], [11.564127, 50.904987], [11.563114, 50.904886],
                            [11.563125, 50.903904], [11.563138, 50.903743], [11.563784, 50.90367],
                            [11.56375, 50.902355], [11.564245, 50.902265], [11.564213, 50.901758],
                            [11.56298, 50.901862], [11.561666, 50.901597], [11.559324, 50.9015],
                            [11.558811, 50.901636], [11.557151, 50.901813], [11.556607, 50.901962],
                            [11.554532, 50.90168]
                        ]]
                    }
                    """;

                Polygon result = loadGeoJson(json, Polygon.class);
                assertNotNull(result);
                assertNotNull(result.getExteriorRing());
                assertTrue(result.getExteriorRing().getCoordinates().size() > 100,
                        "Should have many coordinates");

                // Verify bbox
                assertNotNull(result.getBoundingBox());
                assertNotNull(result.getBoundingBox().getSouthwest());
                assertNotNull(result.getBoundingBox().getNortheast());

                assertEquals(11.504092, result.getBoundingBox().getSouthwest().getLongitude(), 0.0001);
                assertEquals(50.895368, result.getBoundingBox().getSouthwest().getLatitude(), 0.0001);
                assertEquals(11.566935, result.getBoundingBox().getNortheast().getLongitude(), 0.0001);
                assertEquals(50.913474, result.getBoundingBox().getNortheast().getLatitude(), 0.0001);

                // Verify first coordinate
                Coordinates first = result.getExteriorRing().getCoordinates().get(0);
                assertEquals(11.554532, first.getLongitude(), 0.0001);
                assertEquals(50.90168, first.getLatitude(), 0.0001);
            }
        }
    }

    // ========================================================================
    // Serialization Tests
    // ========================================================================

    @Nested
    @DisplayName("Serialization")
    class SerializationTests {

        @Test
        @DisplayName("Point serialization")
        void serializePoint() throws IOException {
            Point point = geoFactory.createPoint();
            point.setCoordinates(createCoordinates(8.6821, 50.1109));

            String json = saveGeoJson(point);

            assertNotNull(json);
            System.out.println("Serialized Point JSON: " + json);
            assertTrue(json.contains("\"type\""), "Should contain type field, got: " + json);
            assertTrue(json.contains("\"Point\""), "Should contain Point value, got: " + json);
            assertTrue(json.contains("\"coordinates\""), "Should contain coordinates field, got: " + json);
            assertTrue(json.contains("8.6821"), "Should contain longitude, got: " + json);
            assertTrue(json.contains("50.1109"), "Should contain latitude, got: " + json);
        }

        @Test
        @DisplayName("LineString serialization")
        void serializeLineString() throws IOException {
            LineString line = geoFactory.createLineString();
            line.getCoordinates().add(createCoordinates(0.0, 0.0));
            line.getCoordinates().add(createCoordinates(10.0, 10.0));
            line.getCoordinates().add(createCoordinates(20.0, 20.0));

            String json = saveGeoJson(line);

            assertNotNull(json);
            assertTrue(json.contains("\"type\""));
            assertTrue(json.contains("\"LineString\""));
            assertTrue(json.contains("\"coordinates\""));
        }

        @Test
        @DisplayName("MultiPoint serialization")
        void serializeMultiPoint() throws IOException {
            MultiPoint multiPoint = geoFactory.createMultiPoint();
            multiPoint.getCoordinates().add(createCoordinates(52.0, 12.0, 120.0));
            multiPoint.getCoordinates().add(createCoordinates(53.0, 13.0, 130.0));

            String json = saveGeoJson(multiPoint);

            assertNotNull(json);
            assertTrue(json.contains("\"type\""));
            assertTrue(json.contains("\"MultiPoint\""));
            assertTrue(json.contains("\"coordinates\""));
        }

        @Test
        @DisplayName("MultiLineString serialization")
        void serializeMultiLineString() throws IOException {
            LineString line1 = geoFactory.createLineString();
            line1.getCoordinates().add(createCoordinates(52.0, 12.0, 120.0));
            line1.getCoordinates().add(createCoordinates(53.0, 11.0, 123.0));

            LineString line2 = geoFactory.createLineString();
            line2.getCoordinates().add(createCoordinates(54.0, 14.0, 140.0));
            line2.getCoordinates().add(createCoordinates(55.0, 15.0, 150.0));

            MultiLineString multiLine = geoFactory.createMultiLineString();
            multiLine.getLinesStrings().add(line1);
            multiLine.getLinesStrings().add(line2);

            String json = saveGeoJson(multiLine);

            assertNotNull(json);
            assertTrue(json.contains("\"type\""));
            assertTrue(json.contains("\"MultiLineString\""));
            assertTrue(json.contains("\"coordinates\""));
        }

        @Test
        @DisplayName("Point with BoundingBox serialization")
        void serializePointWithBoundingBox() throws IOException {
            Point point = geoFactory.createPoint();
            Coordinates coords = createCoordinates(52.0, 12.0, 120.0);
            point.setCoordinates(coords);

            BoundingBox bbox = geoFactory.createBoundingBox();
            bbox.setSouthwest(EcoreUtil.copy(coords));
            bbox.setNortheast(EcoreUtil.copy(coords));
            point.setBoundingBox(bbox);

            String json = saveGeoJson(point);

            assertNotNull(json);
            assertTrue(json.contains("\"type\""));
            assertTrue(json.contains("\"Point\""));
            assertTrue(json.contains("\"bbox\""));
            assertTrue(json.contains("\"coordinates\""));
        }

        @Test
        @DisplayName("Feature with Point serialization")
        void serializeFeatureWithPoint() throws IOException {
            Point point = geoFactory.createPoint();
            point.setCoordinates(createCoordinates(13.405, 52.52));

            Feature feature = geoFactory.createFeature();
            feature.setId("berlin");
            feature.setGeometry(point);

            String json = saveGeoJson(feature);

            assertNotNull(json);
            assertTrue(json.contains("\"type\""));
            assertTrue(json.contains("\"Feature\""));
            assertTrue(json.contains("\"id\""));
            assertTrue(json.contains("\"berlin\""));
            assertTrue(json.contains("\"geometry\""));
        }

        @Test
        @DisplayName("FeatureCollection serialization")
        void serializeFeatureCollection() throws IOException {
            Point parisPoint = geoFactory.createPoint();
            parisPoint.setCoordinates(createCoordinates(2.3522, 48.8566));

            Feature paris = geoFactory.createFeature();
            paris.setId("paris");
            paris.setGeometry(parisPoint);

            Point londonPoint = geoFactory.createPoint();
            londonPoint.setCoordinates(createCoordinates(-0.1276, 51.5074));

            Feature london = geoFactory.createFeature();
            london.setId("london");
            london.setGeometry(londonPoint);

            FeatureCollection collection = geoFactory.createFeatureCollection();
            collection.getFeatures().add(paris);
            collection.getFeatures().add(london);

            String json = saveGeoJson(collection);

            assertNotNull(json);
            assertTrue(json.contains("\"type\""));
            assertTrue(json.contains("\"FeatureCollection\""));
            assertTrue(json.contains("\"features\""));
            assertTrue(json.contains("\"paris\""));
            assertTrue(json.contains("\"london\""));
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip")
    class RoundTripTests {

        @Test
        @DisplayName("Point round-trip")
        void pointRoundTrip() throws IOException {
            // Create
            Point original = geoFactory.createPoint();
            original.setCoordinates(createCoordinates(8.6821, 50.1109));

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            Point loaded = loadGeoJson(json, Point.class);

            // Verify
            assertNotNull(loaded.getCoordinates());
            assertEquals(original.getCoordinates().getLongitude(), loaded.getCoordinates().getLongitude(), 0.0001);
            assertEquals(original.getCoordinates().getLatitude(), loaded.getCoordinates().getLatitude(), 0.0001);
        }

        @Test
        @DisplayName("Point with elevation round-trip")
        void pointWithElevationRoundTrip() throws IOException {
            // Create
            Point original = geoFactory.createPoint();
            original.setCoordinates(createCoordinates(13.405, 52.52, 34.5));

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            Point loaded = loadGeoJson(json, Point.class);

            // Verify
            assertNotNull(loaded.getCoordinates());
            assertEquals(original.getCoordinates().getLongitude(), loaded.getCoordinates().getLongitude(), 0.0001);
            assertEquals(original.getCoordinates().getLatitude(), loaded.getCoordinates().getLatitude(), 0.0001);
            assertEquals(original.getCoordinates().getElevation(), loaded.getCoordinates().getElevation(), 0.0001);
        }

        @Test
        @DisplayName("LineString round-trip")
        void lineStringRoundTrip() throws IOException {
            // Create
            LineString original = geoFactory.createLineString();
            original.getCoordinates().add(createCoordinates(8.6821, 50.1109));
            original.getCoordinates().add(createCoordinates(8.6831, 50.1115));
            original.getCoordinates().add(createCoordinates(8.6845, 50.1120));

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            LineString loaded = loadGeoJson(json, LineString.class);

            // Verify
            assertEquals(original.getCoordinates().size(), loaded.getCoordinates().size());
            for (int i = 0; i < original.getCoordinates().size(); i++) {
                assertEquals(original.getCoordinates().get(i).getLongitude(),
                        loaded.getCoordinates().get(i).getLongitude(), 0.0001);
                assertEquals(original.getCoordinates().get(i).getLatitude(),
                        loaded.getCoordinates().get(i).getLatitude(), 0.0001);
            }
        }

        @Test
        @DisplayName("MultiPoint round-trip")
        void multiPointRoundTrip() throws IOException {
            // Create
            MultiPoint original = geoFactory.createMultiPoint();
            original.getCoordinates().add(createCoordinates(52.0, 12.0, 120.0));
            original.getCoordinates().add(createCoordinates(53.0, 13.0, 130.0));

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            MultiPoint loaded = loadGeoJson(json, MultiPoint.class);

            // Verify
            assertEquals(original.getCoordinates().size(), loaded.getCoordinates().size());
            for (int i = 0; i < original.getCoordinates().size(); i++) {
                assertEquals(original.getCoordinates().get(i).getLongitude(),
                        loaded.getCoordinates().get(i).getLongitude(), 0.0001);
                assertEquals(original.getCoordinates().get(i).getLatitude(),
                        loaded.getCoordinates().get(i).getLatitude(), 0.0001);
                assertEquals(original.getCoordinates().get(i).getElevation(),
                        loaded.getCoordinates().get(i).getElevation(), 0.0001);
            }
        }

        @Test
        @DisplayName("MultiLineString round-trip")
        void multiLineStringRoundTrip() throws IOException {
            // Create
            LineString line1 = geoFactory.createLineString();
            line1.getCoordinates().add(createCoordinates(52.0, 12.0, 120.0));
            line1.getCoordinates().add(createCoordinates(53.0, 11.0, 123.0));

            LineString line2 = geoFactory.createLineString();
            line2.getCoordinates().add(createCoordinates(54.0, 14.0, 140.0));
            line2.getCoordinates().add(createCoordinates(55.0, 15.0, 150.0));

            MultiLineString original = geoFactory.createMultiLineString();
            original.getLinesStrings().add(line1);
            original.getLinesStrings().add(line2);

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            MultiLineString loaded = loadGeoJson(json, MultiLineString.class);

            // Verify
            assertEquals(original.getLinesStrings().size(), loaded.getLinesStrings().size());

            for (int i = 0; i < original.getLinesStrings().size(); i++) {
                SimpleLineString origLine = original.getLinesStrings().get(i);
                SimpleLineString loadedLine = loaded.getLinesStrings().get(i);
                assertEquals(origLine.getCoordinates().size(), loadedLine.getCoordinates().size());
            }
        }

        @Test
        @DisplayName("Polygon round-trip")
        void polygonRoundTrip() throws IOException {
            // Create
            Polygon original = geoFactory.createPolygon();

            Ring exteriorRing = geoFactory.createRing();
            exteriorRing.getCoordinates().add(createCoordinates(0.0, 0.0));
            exteriorRing.getCoordinates().add(createCoordinates(10.0, 0.0));
            exteriorRing.getCoordinates().add(createCoordinates(10.0, 10.0));
            exteriorRing.getCoordinates().add(createCoordinates(0.0, 10.0));
            exteriorRing.getCoordinates().add(createCoordinates(0.0, 0.0));

            original.setExteriorRing(exteriorRing);

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            Polygon loaded = loadGeoJson(json, Polygon.class);

            // Verify
            assertNotNull(loaded.getExteriorRing());
            assertEquals(original.getExteriorRing().getCoordinates().size(),
                    loaded.getExteriorRing().getCoordinates().size());

            for (int i = 0; i < original.getExteriorRing().getCoordinates().size(); i++) {
                assertEquals(original.getExteriorRing().getCoordinates().get(i).getLongitude(),
                        loaded.getExteriorRing().getCoordinates().get(i).getLongitude(), 0.0001);
                assertEquals(original.getExteriorRing().getCoordinates().get(i).getLatitude(),
                        loaded.getExteriorRing().getCoordinates().get(i).getLatitude(), 0.0001);
            }
        }

        @Test
        @DisplayName("Polygon with hole round-trip")
        void polygonWithHoleRoundTrip() throws IOException {
            // Create
            Polygon original = geoFactory.createPolygon();

            // Exterior ring
            Ring exteriorRing = geoFactory.createRing();
            exteriorRing.getCoordinates().add(createCoordinates(0.0, 0.0));
            exteriorRing.getCoordinates().add(createCoordinates(20.0, 0.0));
            exteriorRing.getCoordinates().add(createCoordinates(20.0, 20.0));
            exteriorRing.getCoordinates().add(createCoordinates(0.0, 20.0));
            exteriorRing.getCoordinates().add(createCoordinates(0.0, 0.0));
            original.setExteriorRing(exteriorRing);

            // Interior hole
            Hole hole = geoFactory.createHole();
            hole.getCoordinates().add(createCoordinates(5.0, 5.0));
            hole.getCoordinates().add(createCoordinates(15.0, 5.0));
            hole.getCoordinates().add(createCoordinates(15.0, 15.0));
            hole.getCoordinates().add(createCoordinates(5.0, 15.0));
            hole.getCoordinates().add(createCoordinates(5.0, 5.0));
            original.getInteriorHoles().add(hole);

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            Polygon loaded = loadGeoJson(json, Polygon.class);

            // Verify exterior
            assertNotNull(loaded.getExteriorRing());
            assertEquals(original.getExteriorRing().getCoordinates().size(),
                    loaded.getExteriorRing().getCoordinates().size());

            // Verify holes
            assertEquals(original.getInteriorHoles().size(), loaded.getInteriorHoles().size());
            assertEquals(original.getInteriorHoles().get(0).getCoordinates().size(),
                    loaded.getInteriorHoles().get(0).getCoordinates().size());
        }

        @Test
        @DisplayName("Feature with Point round-trip")
        void featureWithPointRoundTrip() throws IOException {
            // Create
            Point point = geoFactory.createPoint();
            point.setCoordinates(createCoordinates(13.405, 52.52));

            Feature original = geoFactory.createFeature();
            original.setId("berlin");
            original.setGeometry(point);

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            Feature loaded = loadGeoJson(json, Feature.class);

            // Verify
            assertEquals(original.getId(), loaded.getId());
            assertNotNull(loaded.getGeometry());
            assertInstanceOf(Point.class, loaded.getGeometry());

            Point loadedPoint = (Point) loaded.getGeometry();
            assertEquals(point.getCoordinates().getLongitude(), loadedPoint.getCoordinates().getLongitude(), 0.0001);
            assertEquals(point.getCoordinates().getLatitude(), loadedPoint.getCoordinates().getLatitude(), 0.0001);
        }

        @Test
        @DisplayName("FeatureCollection round-trip")
        void featureCollectionRoundTrip() throws IOException {
            // Create
            Point parisPoint = geoFactory.createPoint();
            parisPoint.setCoordinates(createCoordinates(2.3522, 48.8566));
            Feature paris = geoFactory.createFeature();
            paris.setId("paris");
            paris.setGeometry(parisPoint);

            Point londonPoint = geoFactory.createPoint();
            londonPoint.setCoordinates(createCoordinates(-0.1276, 51.5074));
            Feature london = geoFactory.createFeature();
            london.setId("london");
            london.setGeometry(londonPoint);

            FeatureCollection original = geoFactory.createFeatureCollection();
            original.getFeatures().add(paris);
            original.getFeatures().add(london);

            // Serialize
            String json = saveGeoJson(original);

            // Deserialize
            FeatureCollection loaded = loadGeoJson(json, FeatureCollection.class);

            // Verify
            assertEquals(original.getFeatures().size(), loaded.getFeatures().size());

            Feature loadedParis = loaded.getFeatures().get(0);
            assertEquals("paris", loadedParis.getId());
            assertInstanceOf(Point.class, loadedParis.getGeometry());

            Feature loadedLondon = loaded.getFeatures().get(1);
            assertEquals("london", loadedLondon.getId());
            assertInstanceOf(Point.class, loadedLondon.getGeometry());

            Point loadedLondonPoint = (Point) loadedLondon.getGeometry();
            assertEquals(-0.1276, loadedLondonPoint.getCoordinates().getLongitude(), 0.0001);
            assertEquals(51.5074, loadedLondonPoint.getCoordinates().getLatitude(), 0.0001);
        }
    }
}
