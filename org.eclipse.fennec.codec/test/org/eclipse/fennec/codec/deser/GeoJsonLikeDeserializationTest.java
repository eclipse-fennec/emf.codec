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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for GeoJSON-like structures with multi-dimensional arrays.
 * <p>
 * GeoJSON is a standard format for encoding geographic data structures.
 * It uses deeply nested coordinate arrays:
 * <ul>
 *   <li>Point: single coordinate [lng, lat] or [lng, lat, elev]</li>
 *   <li>LineString: 1D array of coordinates [[lng,lat], [lng,lat], ...]</li>
 *   <li>Polygon: 2D array (exterior ring + holes) [[[lng,lat],...], [[lng,lat],...]]</li>
 *   <li>MultiLineString: 2D array of lines</li>
 *   <li>MultiPolygon: 3D array of polygons</li>
 *   <li>GeometryCollection: heterogeneous array of geometry objects</li>
 * </ul>
 * </p>
 * <p>
 * This test class verifies that the codec can handle these complex structures,
 * especially when properties appear before _type (deferred processing).
 * </p>
 */
@DisplayName("GeoJSON-like Deserialization Tests")
class GeoJsonLikeDeserializationTest {

    private static final String TEST_ECORE = "test-geojson-like.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage geoPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass coordinateClass;
    private EClass propertiesClass;
    private EClass geometryClass;
    private EClass pointClass;
    private EClass lineStringClass;
    private EClass ringClass;
    private EClass polygonClass;
    private EClass multiLineStringClass;
    private EClass multiPolygonClass;
    private EClass geometryCollectionClass;
    private EClass featureClass;
    private EClass featureCollectionClass;
    private EClass boundingBoxClass;
    private EClass geoDocumentClass;

    // Coordinate attributes
    private EAttribute longitudeAttr;
    private EAttribute latitudeAttr;
    private EAttribute elevationAttr;

    // Properties attributes
    private EAttribute propNameAttr;
    private EAttribute propDescAttr;
    private EAttribute propTagsAttr;
    private EAttribute propPopulationAttr;

    // Geometry attributes
    private EAttribute geomTypeAttr;

    // References
    private EReference pointCoordinatesRef;
    private EReference lineStringCoordinatesRef;
    private EReference ringCoordinatesRef;
    private EReference polygonExteriorRef;
    private EReference polygonHolesRef;
    private EReference multiLineStringLinesRef;
    private EReference multiPolygonPolygonsRef;
    private EReference geometryCollectionGeometriesRef;
    private EReference featureGeometryRef;
    private EReference featurePropertiesRef;
    private EAttribute featureIdAttr;
    private EAttribute featureTypeAttr;
    private EReference featureCollectionFeaturesRef;
    private EAttribute featureCollectionTypeAttr;
    private EReference geoDocBboxRef;
    private EReference geoDocFeatureCollectionRef;
    private EReference geoDocGeometryRef;
    private EAttribute geoDocNameAttr;
    private EReference bboxSouthwestRef;
    private EReference bboxNortheastRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        geoPackage = ecoreHelper.loadEcore(TEST_ECORE, GeoJsonLikeDeserializationTest.class);
        EPackage.Registry.INSTANCE.put(geoPackage.getNsURI(), geoPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(geoPackage);

        // Load EClasses
        coordinateClass = EcoreHelper.getEClass(geoPackage, "Coordinate");
        propertiesClass = EcoreHelper.getEClass(geoPackage, "Properties");
        geometryClass = EcoreHelper.getEClass(geoPackage, "Geometry");
        pointClass = EcoreHelper.getEClass(geoPackage, "Point");
        lineStringClass = EcoreHelper.getEClass(geoPackage, "LineString");
        ringClass = EcoreHelper.getEClass(geoPackage, "Ring");
        polygonClass = EcoreHelper.getEClass(geoPackage, "Polygon");
        multiLineStringClass = EcoreHelper.getEClass(geoPackage, "MultiLineString");
        multiPolygonClass = EcoreHelper.getEClass(geoPackage, "MultiPolygon");
        geometryCollectionClass = EcoreHelper.getEClass(geoPackage, "GeometryCollection");
        featureClass = EcoreHelper.getEClass(geoPackage, "Feature");
        featureCollectionClass = EcoreHelper.getEClass(geoPackage, "FeatureCollection");
        boundingBoxClass = EcoreHelper.getEClass(geoPackage, "BoundingBox");
        geoDocumentClass = EcoreHelper.getEClass(geoPackage, "GeoDocument");

        // Coordinate attributes
        longitudeAttr = (EAttribute) EcoreHelper.getFeature(coordinateClass, "longitude");
        latitudeAttr = (EAttribute) EcoreHelper.getFeature(coordinateClass, "latitude");
        elevationAttr = (EAttribute) EcoreHelper.getFeature(coordinateClass, "elevation");

        // Properties attributes
        propNameAttr = (EAttribute) EcoreHelper.getFeature(propertiesClass, "name");
        propDescAttr = (EAttribute) EcoreHelper.getFeature(propertiesClass, "description");
        propTagsAttr = (EAttribute) EcoreHelper.getFeature(propertiesClass, "tags");
        propPopulationAttr = (EAttribute) EcoreHelper.getFeature(propertiesClass, "population");

        // Geometry type attribute
        geomTypeAttr = (EAttribute) EcoreHelper.getFeature(geometryClass, "type");

        // Point
        pointCoordinatesRef = (EReference) EcoreHelper.getFeature(pointClass, "coordinates");

        // LineString
        lineStringCoordinatesRef = (EReference) EcoreHelper.getFeature(lineStringClass, "coordinates");

        // Ring
        ringCoordinatesRef = (EReference) EcoreHelper.getFeature(ringClass, "coordinates");

        // Polygon
        polygonExteriorRef = (EReference) EcoreHelper.getFeature(polygonClass, "exterior");
        polygonHolesRef = (EReference) EcoreHelper.getFeature(polygonClass, "holes");

        // MultiLineString
        multiLineStringLinesRef = (EReference) EcoreHelper.getFeature(multiLineStringClass, "lines");

        // MultiPolygon
        multiPolygonPolygonsRef = (EReference) EcoreHelper.getFeature(multiPolygonClass, "polygons");

        // GeometryCollection
        geometryCollectionGeometriesRef = (EReference) EcoreHelper.getFeature(geometryCollectionClass, "geometries");

        // Feature
        featureIdAttr = (EAttribute) EcoreHelper.getFeature(featureClass, "id");
        featureTypeAttr = (EAttribute) EcoreHelper.getFeature(featureClass, "type");
        featureGeometryRef = (EReference) EcoreHelper.getFeature(featureClass, "geometry");
        featurePropertiesRef = (EReference) EcoreHelper.getFeature(featureClass, "properties");

        // FeatureCollection
        featureCollectionTypeAttr = (EAttribute) EcoreHelper.getFeature(featureCollectionClass, "type");
        featureCollectionFeaturesRef = (EReference) EcoreHelper.getFeature(featureCollectionClass, "features");

        // BoundingBox
        bboxSouthwestRef = (EReference) EcoreHelper.getFeature(boundingBoxClass, "southwest");
        bboxNortheastRef = (EReference) EcoreHelper.getFeature(boundingBoxClass, "northeast");

        // GeoDocument
        geoDocNameAttr = (EAttribute) EcoreHelper.getFeature(geoDocumentClass, "name");
        geoDocBboxRef = (EReference) EcoreHelper.getFeature(geoDocumentClass, "bbox");
        geoDocFeatureCollectionRef = (EReference) EcoreHelper.getFeature(geoDocumentClass, "featureCollection");
        geoDocGeometryRef = (EReference) EcoreHelper.getFeature(geoDocumentClass, "geometry");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(geoPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject loadJson(String json, EClass rootClass) throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();
        CodecResource resource = new CodecResource(
                URI.createURI("test://geo.json"),
                metadataService,
                resolver,
                null);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootClass);

        try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            resource.load(is, options);
        }

        if (!resource.getErrors().isEmpty()) {
            fail("Deserialization errors: " + resource.getErrors());
        }

        assertEquals(1, resource.getContents().size(), "Should have exactly one root object");
        return resource.getContents().get(0);
    }

    private void assertCoordinate(EObject coord, double lng, double lat) {
        assertCoordinate(coord, lng, lat, null);
    }

    private void assertCoordinate(EObject coord, double lng, double lat, Double elev) {
        assertNotNull(coord, "Coordinate should not be null");
        assertEquals(lng, (double) coord.eGet(longitudeAttr), 0.0001, "Longitude mismatch");
        assertEquals(lat, (double) coord.eGet(latitudeAttr), 0.0001, "Latitude mismatch");
        if (elev != null) {
            assertEquals(elev, (Double) coord.eGet(elevationAttr), 0.0001, "Elevation mismatch");
        }
    }

    // ========================================================================
    // Point Tests
    // ========================================================================

    @Nested
    @DisplayName("Point Geometry")
    class PointGeometryTests {

        @Test
        @DisplayName("simple 2D point")
        void simple2DPoint() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//Point",
                    "type": "Point",
                    "coordinates": {
                        "longitude": 8.6821,
                        "latitude": 50.1109
                    }
                }
                """;

            EObject point = loadJson(json, pointClass);
            assertEquals("Point", point.eGet(geomTypeAttr));

            EObject coord = (EObject) point.eGet(pointCoordinatesRef);
            assertCoordinate(coord, 8.6821, 50.1109);
        }

        @Test
        @DisplayName("3D point with elevation")
        void point3DWithElevation() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//Point",
                    "type": "Point",
                    "coordinates": {
                        "longitude": 8.6821,
                        "latitude": 50.1109,
                        "elevation": 112.5
                    }
                }
                """;

            EObject point = loadJson(json, pointClass);
            EObject coord = (EObject) point.eGet(pointCoordinatesRef);
            assertCoordinate(coord, 8.6821, 50.1109, 112.5);
        }

        @Test
        @DisplayName("point with coordinates before _type (deferred)")
        void pointCoordinatesDeferred() throws IOException {
            String json = """
                {
                    "coordinates": {
                        "longitude": -122.4194,
                        "latitude": 37.7749
                    },
                    "type": "Point",
                    "_type": "http://test.fennec/geojsonlike#//Point"
                }
                """;

            EObject point = loadJson(json, pointClass);
            EObject coord = (EObject) point.eGet(pointCoordinatesRef);
            assertCoordinate(coord, -122.4194, 37.7749);
        }
    }

    // ========================================================================
    // LineString Tests
    // ========================================================================

    @Nested
    @DisplayName("LineString Geometry (1D Coordinate Array)")
    class LineStringTests {

        @Test
        @DisplayName("simple line with 3 points")
        @SuppressWarnings("unchecked")
        void simpleLineString() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//LineString",
                    "type": "LineString",
                    "coordinates": [
                        {"longitude": 8.0, "latitude": 50.0},
                        {"longitude": 9.0, "latitude": 51.0},
                        {"longitude": 10.0, "latitude": 52.0}
                    ]
                }
                """;

            EObject line = loadJson(json, lineStringClass);
            assertEquals("LineString", line.eGet(geomTypeAttr));

            EList<EObject> coords = (EList<EObject>) line.eGet(lineStringCoordinatesRef);
            assertEquals(3, coords.size());
            assertCoordinate(coords.get(0), 8.0, 50.0);
            assertCoordinate(coords.get(1), 9.0, 51.0);
            assertCoordinate(coords.get(2), 10.0, 52.0);
        }

        @Test
        @DisplayName("linestring coordinates before _type (deferred)")
        @SuppressWarnings("unchecked")
        void lineStringDeferred() throws IOException {
            String json = """
                {
                    "coordinates": [
                        {"longitude": 0.0, "latitude": 0.0},
                        {"longitude": 1.0, "latitude": 1.0},
                        {"longitude": 2.0, "latitude": 2.0},
                        {"longitude": 3.0, "latitude": 3.0}
                    ],
                    "_type": "http://test.fennec/geojsonlike#//LineString",
                    "type": "LineString"
                }
                """;

            EObject line = loadJson(json, lineStringClass);
            EList<EObject> coords = (EList<EObject>) line.eGet(lineStringCoordinatesRef);
            assertEquals(4, coords.size());
            assertCoordinate(coords.get(3), 3.0, 3.0);
        }

        @Test
        @DisplayName("long linestring (many coordinates)")
        @SuppressWarnings("unchecked")
        void longLineString() throws IOException {
            // Create a line with 100 points
            StringBuilder json = new StringBuilder();
            json.append("{\n\"_type\": \"http://test.fennec/geojsonlike#//LineString\",\n\"coordinates\": [\n");
            for (int i = 0; i < 100; i++) {
                if (i > 0) json.append(",\n");
                json.append(String.format("{\"longitude\": %d.0, \"latitude\": %d.0}", i, i * 2));
            }
            json.append("\n]\n}");

            EObject line = loadJson(json.toString(), lineStringClass);
            EList<EObject> coords = (EList<EObject>) line.eGet(lineStringCoordinatesRef);
            assertEquals(100, coords.size());
            assertCoordinate(coords.get(99), 99.0, 198.0);
        }
    }

    // ========================================================================
    // Polygon Tests
    // ========================================================================

    @Nested
    @DisplayName("Polygon Geometry (2D Structure)")
    class PolygonTests {

        @Test
        @DisplayName("simple polygon (exterior ring only)")
        @SuppressWarnings("unchecked")
        void simplePolygon() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//Polygon",
                    "type": "Polygon",
                    "exterior": {
                        "coordinates": [
                            {"longitude": 0.0, "latitude": 0.0},
                            {"longitude": 10.0, "latitude": 0.0},
                            {"longitude": 10.0, "latitude": 10.0},
                            {"longitude": 0.0, "latitude": 10.0},
                            {"longitude": 0.0, "latitude": 0.0}
                        ]
                    }
                }
                """;

            EObject polygon = loadJson(json, polygonClass);
            assertEquals("Polygon", polygon.eGet(geomTypeAttr));

            EObject exterior = (EObject) polygon.eGet(polygonExteriorRef);
            assertNotNull(exterior);

            EList<EObject> coords = (EList<EObject>) exterior.eGet(ringCoordinatesRef);
            assertEquals(5, coords.size()); // Closed ring
            assertCoordinate(coords.get(0), 0.0, 0.0);
            assertCoordinate(coords.get(4), 0.0, 0.0); // Same as first (closed)
        }

        @Test
        @DisplayName("polygon with one hole")
        @SuppressWarnings("unchecked")
        void polygonWithHole() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//Polygon",
                    "type": "Polygon",
                    "exterior": {
                        "coordinates": [
                            {"longitude": 0.0, "latitude": 0.0},
                            {"longitude": 20.0, "latitude": 0.0},
                            {"longitude": 20.0, "latitude": 20.0},
                            {"longitude": 0.0, "latitude": 20.0},
                            {"longitude": 0.0, "latitude": 0.0}
                        ]
                    },
                    "holes": [
                        {
                            "coordinates": [
                                {"longitude": 5.0, "latitude": 5.0},
                                {"longitude": 15.0, "latitude": 5.0},
                                {"longitude": 15.0, "latitude": 15.0},
                                {"longitude": 5.0, "latitude": 15.0},
                                {"longitude": 5.0, "latitude": 5.0}
                            ]
                        }
                    ]
                }
                """;

            EObject polygon = loadJson(json, polygonClass);

            EObject exterior = (EObject) polygon.eGet(polygonExteriorRef);
            EList<EObject> extCoords = (EList<EObject>) exterior.eGet(ringCoordinatesRef);
            assertEquals(5, extCoords.size());

            EList<EObject> holes = (EList<EObject>) polygon.eGet(polygonHolesRef);
            assertEquals(1, holes.size());

            EObject hole = holes.get(0);
            EList<EObject> holeCoords = (EList<EObject>) hole.eGet(ringCoordinatesRef);
            assertEquals(5, holeCoords.size());
            assertCoordinate(holeCoords.get(0), 5.0, 5.0);
        }

        @Test
        @DisplayName("polygon with multiple holes")
        @SuppressWarnings("unchecked")
        void polygonWithMultipleHoles() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//Polygon",
                    "exterior": {
                        "coordinates": [
                            {"longitude": 0.0, "latitude": 0.0},
                            {"longitude": 100.0, "latitude": 0.0},
                            {"longitude": 100.0, "latitude": 100.0},
                            {"longitude": 0.0, "latitude": 100.0},
                            {"longitude": 0.0, "latitude": 0.0}
                        ]
                    },
                    "holes": [
                        {
                            "coordinates": [
                                {"longitude": 10.0, "latitude": 10.0},
                                {"longitude": 30.0, "latitude": 10.0},
                                {"longitude": 30.0, "latitude": 30.0},
                                {"longitude": 10.0, "latitude": 30.0},
                                {"longitude": 10.0, "latitude": 10.0}
                            ]
                        },
                        {
                            "coordinates": [
                                {"longitude": 50.0, "latitude": 50.0},
                                {"longitude": 70.0, "latitude": 50.0},
                                {"longitude": 70.0, "latitude": 70.0},
                                {"longitude": 50.0, "latitude": 70.0},
                                {"longitude": 50.0, "latitude": 50.0}
                            ]
                        }
                    ]
                }
                """;

            EObject polygon = loadJson(json, polygonClass);
            EList<EObject> holes = (EList<EObject>) polygon.eGet(polygonHolesRef);
            assertEquals(2, holes.size());

            // Check first hole
            EList<EObject> hole1Coords = (EList<EObject>) holes.get(0).eGet(ringCoordinatesRef);
            assertCoordinate(hole1Coords.get(0), 10.0, 10.0);

            // Check second hole
            EList<EObject> hole2Coords = (EList<EObject>) holes.get(1).eGet(ringCoordinatesRef);
            assertCoordinate(hole2Coords.get(0), 50.0, 50.0);
        }

        @Test
        @DisplayName("polygon deferred (exterior and holes before _type)")
        @SuppressWarnings("unchecked")
        void polygonDeferred() throws IOException {
            String json = """
                {
                    "exterior": {
                        "coordinates": [
                            {"longitude": 0.0, "latitude": 0.0},
                            {"longitude": 5.0, "latitude": 0.0},
                            {"longitude": 5.0, "latitude": 5.0},
                            {"longitude": 0.0, "latitude": 5.0},
                            {"longitude": 0.0, "latitude": 0.0}
                        ]
                    },
                    "holes": [
                        {
                            "coordinates": [
                                {"longitude": 1.0, "latitude": 1.0},
                                {"longitude": 4.0, "latitude": 1.0},
                                {"longitude": 4.0, "latitude": 4.0},
                                {"longitude": 1.0, "latitude": 4.0},
                                {"longitude": 1.0, "latitude": 1.0}
                            ]
                        }
                    ],
                    "_type": "http://test.fennec/geojsonlike#//Polygon"
                }
                """;

            EObject polygon = loadJson(json, polygonClass);

            EObject exterior = (EObject) polygon.eGet(polygonExteriorRef);
            assertNotNull(exterior);

            EList<EObject> holes = (EList<EObject>) polygon.eGet(polygonHolesRef);
            assertEquals(1, holes.size());
        }
    }

    // ========================================================================
    // MultiLineString Tests (2D Array)
    // ========================================================================

    @Nested
    @DisplayName("MultiLineString Geometry (2D Array)")
    class MultiLineStringTests {

        @Test
        @DisplayName("multilinestring with 2 lines")
        @SuppressWarnings("unchecked")
        void multiLineString() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//MultiLineString",
                    "type": "MultiLineString",
                    "lines": [
                        {
                            "coordinates": [
                                {"longitude": 0.0, "latitude": 0.0},
                                {"longitude": 1.0, "latitude": 1.0}
                            ]
                        },
                        {
                            "coordinates": [
                                {"longitude": 2.0, "latitude": 2.0},
                                {"longitude": 3.0, "latitude": 3.0},
                                {"longitude": 4.0, "latitude": 4.0}
                            ]
                        }
                    ]
                }
                """;

            EObject multiLine = loadJson(json, multiLineStringClass);
            assertEquals("MultiLineString", multiLine.eGet(geomTypeAttr));

            EList<EObject> lines = (EList<EObject>) multiLine.eGet(multiLineStringLinesRef);
            assertEquals(2, lines.size());

            // First line has 2 points
            EList<EObject> line1Coords = (EList<EObject>) lines.get(0).eGet(lineStringCoordinatesRef);
            assertEquals(2, line1Coords.size());

            // Second line has 3 points
            EList<EObject> line2Coords = (EList<EObject>) lines.get(1).eGet(lineStringCoordinatesRef);
            assertEquals(3, line2Coords.size());
            assertCoordinate(line2Coords.get(2), 4.0, 4.0);
        }

        @Test
        @DisplayName("multilinestring deferred")
        @SuppressWarnings("unchecked")
        void multiLineStringDeferred() throws IOException {
            String json = """
                {
                    "lines": [
                        {
                            "coordinates": [
                                {"longitude": 10.0, "latitude": 20.0},
                                {"longitude": 30.0, "latitude": 40.0}
                            ]
                        }
                    ],
                    "_type": "http://test.fennec/geojsonlike#//MultiLineString"
                }
                """;

            EObject multiLine = loadJson(json, multiLineStringClass);
            EList<EObject> lines = (EList<EObject>) multiLine.eGet(multiLineStringLinesRef);
            assertEquals(1, lines.size());
        }
    }

    // ========================================================================
    // MultiPolygon Tests (3D Array Structure)
    // ========================================================================

    @Nested
    @DisplayName("MultiPolygon Geometry (3D Array Structure)")
    class MultiPolygonTests {

        @Test
        @DisplayName("multipolygon with 2 simple polygons")
        @SuppressWarnings("unchecked")
        void multiPolygonSimple() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//MultiPolygon",
                    "type": "MultiPolygon",
                    "polygons": [
                        {
                            "exterior": {
                                "coordinates": [
                                    {"longitude": 0.0, "latitude": 0.0},
                                    {"longitude": 1.0, "latitude": 0.0},
                                    {"longitude": 1.0, "latitude": 1.0},
                                    {"longitude": 0.0, "latitude": 1.0},
                                    {"longitude": 0.0, "latitude": 0.0}
                                ]
                            }
                        },
                        {
                            "exterior": {
                                "coordinates": [
                                    {"longitude": 10.0, "latitude": 10.0},
                                    {"longitude": 11.0, "latitude": 10.0},
                                    {"longitude": 11.0, "latitude": 11.0},
                                    {"longitude": 10.0, "latitude": 11.0},
                                    {"longitude": 10.0, "latitude": 10.0}
                                ]
                            }
                        }
                    ]
                }
                """;

            EObject multiPoly = loadJson(json, multiPolygonClass);
            assertEquals("MultiPolygon", multiPoly.eGet(geomTypeAttr));

            EList<EObject> polygons = (EList<EObject>) multiPoly.eGet(multiPolygonPolygonsRef);
            assertEquals(2, polygons.size());

            // Check first polygon
            EObject poly1Exterior = (EObject) polygons.get(0).eGet(polygonExteriorRef);
            EList<EObject> poly1Coords = (EList<EObject>) poly1Exterior.eGet(ringCoordinatesRef);
            assertEquals(5, poly1Coords.size());
            assertCoordinate(poly1Coords.get(0), 0.0, 0.0);

            // Check second polygon
            EObject poly2Exterior = (EObject) polygons.get(1).eGet(polygonExteriorRef);
            EList<EObject> poly2Coords = (EList<EObject>) poly2Exterior.eGet(ringCoordinatesRef);
            assertCoordinate(poly2Coords.get(0), 10.0, 10.0);
        }

        @Test
        @DisplayName("multipolygon with polygons containing holes")
        @SuppressWarnings("unchecked")
        void multiPolygonWithHoles() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//MultiPolygon",
                    "polygons": [
                        {
                            "exterior": {
                                "coordinates": [
                                    {"longitude": 0.0, "latitude": 0.0},
                                    {"longitude": 10.0, "latitude": 0.0},
                                    {"longitude": 10.0, "latitude": 10.0},
                                    {"longitude": 0.0, "latitude": 10.0},
                                    {"longitude": 0.0, "latitude": 0.0}
                                ]
                            },
                            "holes": [
                                {
                                    "coordinates": [
                                        {"longitude": 2.0, "latitude": 2.0},
                                        {"longitude": 8.0, "latitude": 2.0},
                                        {"longitude": 8.0, "latitude": 8.0},
                                        {"longitude": 2.0, "latitude": 8.0},
                                        {"longitude": 2.0, "latitude": 2.0}
                                    ]
                                }
                            ]
                        }
                    ]
                }
                """;

            EObject multiPoly = loadJson(json, multiPolygonClass);
            EList<EObject> polygons = (EList<EObject>) multiPoly.eGet(multiPolygonPolygonsRef);
            assertEquals(1, polygons.size());

            EList<EObject> holes = (EList<EObject>) polygons.get(0).eGet(polygonHolesRef);
            assertEquals(1, holes.size());
        }

        @Test
        @DisplayName("multipolygon fully deferred")
        @SuppressWarnings("unchecked")
        void multiPolygonDeferred() throws IOException {
            String json = """
                {
                    "polygons": [
                        {
                            "exterior": {
                                "coordinates": [
                                    {"longitude": 100.0, "latitude": 100.0},
                                    {"longitude": 200.0, "latitude": 100.0},
                                    {"longitude": 200.0, "latitude": 200.0},
                                    {"longitude": 100.0, "latitude": 200.0},
                                    {"longitude": 100.0, "latitude": 100.0}
                                ]
                            }
                        }
                    ],
                    "_type": "http://test.fennec/geojsonlike#//MultiPolygon"
                }
                """;

            EObject multiPoly = loadJson(json, multiPolygonClass);
            EList<EObject> polygons = (EList<EObject>) multiPoly.eGet(multiPolygonPolygonsRef);
            assertEquals(1, polygons.size());

            EObject exterior = (EObject) polygons.get(0).eGet(polygonExteriorRef);
            EList<EObject> coords = (EList<EObject>) exterior.eGet(ringCoordinatesRef);
            assertCoordinate(coords.get(0), 100.0, 100.0);
        }
    }

    // ========================================================================
    // GeometryCollection Tests (Heterogeneous)
    // ========================================================================

    @Nested
    @DisplayName("GeometryCollection (Heterogeneous Array)")
    class GeometryCollectionTests {

        @Test
        @DisplayName("collection with point and linestring")
        @SuppressWarnings("unchecked")
        void collectionWithMixedGeometries() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//GeometryCollection",
                    "type": "GeometryCollection",
                    "geometries": [
                        {
                            "_type": "http://test.fennec/geojsonlike#//Point",
                            "type": "Point",
                            "coordinates": {"longitude": 5.0, "latitude": 5.0}
                        },
                        {
                            "_type": "http://test.fennec/geojsonlike#//LineString",
                            "type": "LineString",
                            "coordinates": [
                                {"longitude": 0.0, "latitude": 0.0},
                                {"longitude": 10.0, "latitude": 10.0}
                            ]
                        }
                    ]
                }
                """;

            EObject collection = loadJson(json, geometryCollectionClass);
            assertEquals("GeometryCollection", collection.eGet(geomTypeAttr));

            EList<EObject> geometries = (EList<EObject>) collection.eGet(geometryCollectionGeometriesRef);
            assertEquals(2, geometries.size());

            // First geometry is a Point
            EObject point = geometries.get(0);
            assertEquals(pointClass, point.eClass());
            assertEquals("Point", point.eGet(geomTypeAttr));

            // Second geometry is a LineString
            EObject line = geometries.get(1);
            assertEquals(lineStringClass, line.eClass());
            assertEquals("LineString", line.eGet(geomTypeAttr));
        }

        @Test
        @DisplayName("nested geometry collection")
        @SuppressWarnings("unchecked")
        void nestedGeometryCollection() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//GeometryCollection",
                    "geometries": [
                        {
                            "_type": "http://test.fennec/geojsonlike#//Point",
                            "coordinates": {"longitude": 1.0, "latitude": 1.0}
                        },
                        {
                            "_type": "http://test.fennec/geojsonlike#//GeometryCollection",
                            "geometries": [
                                {
                                    "_type": "http://test.fennec/geojsonlike#//Point",
                                    "coordinates": {"longitude": 2.0, "latitude": 2.0}
                                },
                                {
                                    "_type": "http://test.fennec/geojsonlike#//Point",
                                    "coordinates": {"longitude": 3.0, "latitude": 3.0}
                                }
                            ]
                        }
                    ]
                }
                """;

            EObject collection = loadJson(json, geometryCollectionClass);
            EList<EObject> geometries = (EList<EObject>) collection.eGet(geometryCollectionGeometriesRef);
            assertEquals(2, geometries.size());

            // Second item is a nested GeometryCollection
            EObject nested = geometries.get(1);
            assertEquals(geometryCollectionClass, nested.eClass());

            EList<EObject> nestedGeometries = (EList<EObject>) nested.eGet(geometryCollectionGeometriesRef);
            assertEquals(2, nestedGeometries.size());
        }
    }

    // ========================================================================
    // Feature and FeatureCollection Tests
    // ========================================================================

    @Nested
    @DisplayName("Feature and FeatureCollection")
    class FeatureTests {

        @Test
        @DisplayName("feature with point geometry and properties")
        void featureWithPointAndProperties() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//Feature",
                    "id": "F001",
                    "type": "Feature",
                    "geometry": {
                        "_type": "http://test.fennec/geojsonlike#//Point",
                        "coordinates": {"longitude": 13.4050, "latitude": 52.5200}
                    },
                    "properties": {
                        "name": "Berlin",
                        "description": "Capital of Germany",
                        "tags": ["capital", "city", "europe"],
                        "population": 3645000
                    }
                }
                """;

            EObject feature = loadJson(json, featureClass);
            assertEquals("F001", feature.eGet(featureIdAttr));
            assertEquals("Feature", feature.eGet(featureTypeAttr));

            // Check geometry
            EObject geometry = (EObject) feature.eGet(featureGeometryRef);
            assertNotNull(geometry);
            assertEquals(pointClass, geometry.eClass());

            // Check properties
            EObject props = (EObject) feature.eGet(featurePropertiesRef);
            assertNotNull(props);
            assertEquals("Berlin", props.eGet(propNameAttr));
            assertEquals("Capital of Germany", props.eGet(propDescAttr));
            assertEquals(3645000L, props.eGet(propPopulationAttr));

            @SuppressWarnings("unchecked")
            EList<String> tags = (EList<String>) props.eGet(propTagsAttr);
            assertEquals(3, tags.size());
            assertTrue(tags.contains("capital"));
        }

        @Test
        @DisplayName("feature collection with multiple features")
        @SuppressWarnings("unchecked")
        void featureCollection() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//FeatureCollection",
                    "type": "FeatureCollection",
                    "features": [
                        {
                            "id": "city-1",
                            "geometry": {
                                "_type": "http://test.fennec/geojsonlike#//Point",
                                "coordinates": {"longitude": 2.3522, "latitude": 48.8566}
                            },
                            "properties": {"name": "Paris"}
                        },
                        {
                            "id": "city-2",
                            "geometry": {
                                "_type": "http://test.fennec/geojsonlike#//Point",
                                "coordinates": {"longitude": -0.1276, "latitude": 51.5074}
                            },
                            "properties": {"name": "London"}
                        },
                        {
                            "id": "city-3",
                            "geometry": {
                                "_type": "http://test.fennec/geojsonlike#//Point",
                                "coordinates": {"longitude": 12.4964, "latitude": 41.9028}
                            },
                            "properties": {"name": "Rome"}
                        }
                    ]
                }
                """;

            EObject collection = loadJson(json, featureCollectionClass);
            assertEquals("FeatureCollection", collection.eGet(featureCollectionTypeAttr));

            EList<EObject> features = (EList<EObject>) collection.eGet(featureCollectionFeaturesRef);
            assertEquals(3, features.size());

            // Check Paris
            EObject paris = features.get(0);
            assertEquals("city-1", paris.eGet(featureIdAttr));
            EObject parisProps = (EObject) paris.eGet(featurePropertiesRef);
            assertEquals("Paris", parisProps.eGet(propNameAttr));

            // Check Rome
            EObject rome = features.get(2);
            assertEquals("city-3", rome.eGet(featureIdAttr));
        }

        @Test
        @DisplayName("feature collection deferred")
        @SuppressWarnings("unchecked")
        void featureCollectionDeferred() throws IOException {
            String json = """
                {
                    "features": [
                        {
                            "id": "deferred-1",
                            "geometry": {
                                "_type": "http://test.fennec/geojsonlike#//Point",
                                "coordinates": {"longitude": 0.0, "latitude": 0.0}
                            },
                            "properties": {"name": "Deferred Feature"}
                        }
                    ],
                    "type": "FeatureCollection",
                    "_type": "http://test.fennec/geojsonlike#//FeatureCollection"
                }
                """;

            EObject collection = loadJson(json, featureCollectionClass);
            EList<EObject> features = (EList<EObject>) collection.eGet(featureCollectionFeaturesRef);
            assertEquals(1, features.size());
            assertEquals("deferred-1", features.get(0).eGet(featureIdAttr));
        }
    }

    // ========================================================================
    // GeoDocument Tests (Complex Root Object)
    // ========================================================================

    @Nested
    @DisplayName("GeoDocument (Complex Root)")
    class GeoDocumentTests {

        @Test
        @DisplayName("geodocument with bbox and feature collection")
        @SuppressWarnings("unchecked")
        void geoDocumentComplete() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//GeoDocument",
                    "name": "European Cities",
                    "bbox": {
                        "southwest": {"longitude": -10.0, "latitude": 35.0},
                        "northeast": {"longitude": 40.0, "latitude": 70.0}
                    },
                    "featureCollection": {
                        "type": "FeatureCollection",
                        "features": [
                            {
                                "id": "madrid",
                                "geometry": {
                                    "_type": "http://test.fennec/geojsonlike#//Point",
                                    "coordinates": {"longitude": -3.7038, "latitude": 40.4168}
                                },
                                "properties": {"name": "Madrid", "population": 3223000}
                            }
                        ]
                    }
                }
                """;

            EObject doc = loadJson(json, geoDocumentClass);
            assertEquals("European Cities", doc.eGet(geoDocNameAttr));

            // Check bbox
            EObject bbox = (EObject) doc.eGet(geoDocBboxRef);
            assertNotNull(bbox);
            EObject sw = (EObject) bbox.eGet(bboxSouthwestRef);
            EObject ne = (EObject) bbox.eGet(bboxNortheastRef);
            assertCoordinate(sw, -10.0, 35.0);
            assertCoordinate(ne, 40.0, 70.0);

            // Check feature collection
            EObject fc = (EObject) doc.eGet(geoDocFeatureCollectionRef);
            assertNotNull(fc);
            EList<EObject> features = (EList<EObject>) fc.eGet(featureCollectionFeaturesRef);
            assertEquals(1, features.size());
        }

        @Test
        @DisplayName("geodocument all deferred")
        void geoDocumentDeferred() throws IOException {
            String json = """
                {
                    "name": "Deferred Document",
                    "bbox": {
                        "southwest": {"longitude": 0.0, "latitude": 0.0},
                        "northeast": {"longitude": 100.0, "latitude": 100.0}
                    },
                    "geometry": {
                        "_type": "http://test.fennec/geojsonlike#//Polygon",
                        "exterior": {
                            "coordinates": [
                                {"longitude": 0.0, "latitude": 0.0},
                                {"longitude": 50.0, "latitude": 0.0},
                                {"longitude": 50.0, "latitude": 50.0},
                                {"longitude": 0.0, "latitude": 50.0},
                                {"longitude": 0.0, "latitude": 0.0}
                            ]
                        }
                    },
                    "_type": "http://test.fennec/geojsonlike#//GeoDocument"
                }
                """;

            EObject doc = loadJson(json, geoDocumentClass);
            assertEquals("Deferred Document", doc.eGet(geoDocNameAttr));

            EObject bbox = (EObject) doc.eGet(geoDocBboxRef);
            assertNotNull(bbox);

            EObject geom = (EObject) doc.eGet(geoDocGeometryRef);
            assertNotNull(geom);
            assertEquals(polygonClass, geom.eClass());
        }
    }

    // ========================================================================
    // Edge Cases and Stress Tests
    // ========================================================================

    @Nested
    @DisplayName("Edge Cases and Stress Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("empty coordinate arrays")
        @SuppressWarnings("unchecked")
        void emptyCoordinateArrays() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//LineString",
                    "coordinates": []
                }
                """;

            EObject line = loadJson(json, lineStringClass);
            EList<EObject> coords = (EList<EObject>) line.eGet(lineStringCoordinatesRef);
            assertTrue(coords.isEmpty());
        }

        @Test
        @DisplayName("deeply nested deferred structure")
        @SuppressWarnings("unchecked")
        void deeplyNestedDeferred() throws IOException {
            // MultiPolygon with polygon with holes, all before _type
            String json = """
                {
                    "polygons": [
                        {
                            "exterior": {
                                "coordinates": [
                                    {"longitude": 0.0, "latitude": 0.0},
                                    {"longitude": 100.0, "latitude": 0.0},
                                    {"longitude": 100.0, "latitude": 100.0},
                                    {"longitude": 0.0, "latitude": 100.0},
                                    {"longitude": 0.0, "latitude": 0.0}
                                ]
                            },
                            "holes": [
                                {
                                    "coordinates": [
                                        {"longitude": 10.0, "latitude": 10.0},
                                        {"longitude": 20.0, "latitude": 10.0},
                                        {"longitude": 20.0, "latitude": 20.0},
                                        {"longitude": 10.0, "latitude": 20.0},
                                        {"longitude": 10.0, "latitude": 10.0}
                                    ]
                                },
                                {
                                    "coordinates": [
                                        {"longitude": 30.0, "latitude": 30.0},
                                        {"longitude": 40.0, "latitude": 30.0},
                                        {"longitude": 40.0, "latitude": 40.0},
                                        {"longitude": 30.0, "latitude": 40.0},
                                        {"longitude": 30.0, "latitude": 30.0}
                                    ]
                                }
                            ]
                        },
                        {
                            "exterior": {
                                "coordinates": [
                                    {"longitude": 200.0, "latitude": 200.0},
                                    {"longitude": 300.0, "latitude": 200.0},
                                    {"longitude": 300.0, "latitude": 300.0},
                                    {"longitude": 200.0, "latitude": 300.0},
                                    {"longitude": 200.0, "latitude": 200.0}
                                ]
                            }
                        }
                    ],
                    "_type": "http://test.fennec/geojsonlike#//MultiPolygon"
                }
                """;

            EObject multiPoly = loadJson(json, multiPolygonClass);
            EList<EObject> polygons = (EList<EObject>) multiPoly.eGet(multiPolygonPolygonsRef);
            assertEquals(2, polygons.size());

            // First polygon has 2 holes
            EList<EObject> holes = (EList<EObject>) polygons.get(0).eGet(polygonHolesRef);
            assertEquals(2, holes.size());

            // Second polygon has no holes
            EList<EObject> holes2 = (EList<EObject>) polygons.get(1).eGet(polygonHolesRef);
            assertTrue(holes2.isEmpty());
        }

        @Test
        @DisplayName("large feature collection (100 features)")
        @SuppressWarnings("unchecked")
        void largeFeatureCollection() throws IOException {
            StringBuilder json = new StringBuilder();
            json.append("{\n\"_type\": \"http://test.fennec/geojsonlike#//FeatureCollection\",\n");
            json.append("\"type\": \"FeatureCollection\",\n\"features\": [\n");

            for (int i = 0; i < 100; i++) {
                if (i > 0) json.append(",\n");
                json.append(String.format("""
                    {
                        "id": "feature-%d",
                        "geometry": {
                            "_type": "http://test.fennec/geojsonlike#//Point",
                            "coordinates": {"longitude": %d.0, "latitude": %d.0}
                        },
                        "properties": {"name": "Feature %d"}
                    }
                    """, i, i, i * 2, i));
            }

            json.append("\n]\n}");

            EObject collection = loadJson(json.toString(), featureCollectionClass);
            EList<EObject> features = (EList<EObject>) collection.eGet(featureCollectionFeaturesRef);
            assertEquals(100, features.size());

            // Verify last feature
            EObject last = features.get(99);
            assertEquals("feature-99", last.eGet(featureIdAttr));
        }

        @Test
        @DisplayName("null geometry in feature")
        void nullGeometryInFeature() throws IOException {
            String json = """
                {
                    "_type": "http://test.fennec/geojsonlike#//Feature",
                    "id": "null-geom",
                    "geometry": null,
                    "properties": {"name": "No Geometry"}
                }
                """;

            EObject feature = loadJson(json, featureClass);
            assertEquals("null-geom", feature.eGet(featureIdAttr));
            assertNull(feature.eGet(featureGeometryRef));

            EObject props = (EObject) feature.eGet(featurePropertiesRef);
            assertEquals("No Geometry", props.eGet(propNameAttr));
        }
    }

    // ========================================================================
    // NAME Strategy with Context Schema Tests
    // ========================================================================

    @Nested
    @DisplayName("NAME Strategy with Context Schema")
    class NameStrategyTests {

        /**
         * Helper that uses CODEC_ROOT_SCHEMA option.
         */
        private EObject loadJsonWithSchema(String json, EClass rootClass, String schemaUri) throws IOException {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .typeKey("type")  // Use "type" as type key (like GeoJSON)
                    .build();
            CodecResource resource = new CodecResource(
                    URI.createURI("test://geo.json"),
                    metadataService,
                    resolver,
                    null);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, rootClass);
            options.put(CodecResource.CODEC_ROOT_SCHEMA, schemaUri);

            try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                resource.load(is, options);
            }

            if (!resource.getErrors().isEmpty()) {
                fail("Deserialization errors: " + resource.getErrors());
            }

            assertEquals(1, resource.getContents().size(), "Should have exactly one root object");
            return resource.getContents().get(0);
        }

        /**
         * Helper that uses only CODEC_ROOT_TYPE (implicit schema).
         */
        private EObject loadJsonWithRootHint(String json, EClass rootClass) throws IOException {
            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .typeKey("type")  // Use "type" as type key (like GeoJSON)
                    .build();
            CodecResource resource = new CodecResource(
                    URI.createURI("test://geo.json"),
                    metadataService,
                    resolver,
                    null);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, rootClass);

            try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                resource.load(is, options);
            }

            if (!resource.getErrors().isEmpty()) {
                fail("Deserialization errors: " + resource.getErrors());
            }

            assertEquals(1, resource.getContents().size(), "Should have exactly one root object");
            return resource.getContents().get(0);
        }

        @Test
        @DisplayName("simple name with explicit CODEC_ROOT_SCHEMA")
        void simpleNameWithExplicitSchema() throws IOException {
            // GeoJSON-like: uses "type" instead of "_type"
            String json = """
                {
                    "type": "Point",
                    "coordinates": {
                        "longitude": 8.6821,
                        "latitude": 50.1109
                    }
                }
                """;

            EObject point = loadJsonWithSchema(json, pointClass, "http://test.fennec/geojsonlike");
            assertEquals("Point", point.eGet(geomTypeAttr));
            assertEquals(pointClass, point.eClass());

            EObject coord = (EObject) point.eGet(pointCoordinatesRef);
            assertCoordinate(coord, 8.6821, 50.1109);
        }

        @Test
        @DisplayName("simple name with implicit schema from CODEC_ROOT_TYPE")
        void simpleNameWithImplicitSchema() throws IOException {
            // No explicit schema - should be derived from pointClass.getEPackage().getNsURI()
            String json = """
                {
                    "type": "Point",
                    "coordinates": {
                        "longitude": -122.4194,
                        "latitude": 37.7749
                    }
                }
                """;

            EObject point = loadJsonWithRootHint(json, pointClass);
            assertEquals("Point", point.eGet(geomTypeAttr));
            assertEquals(pointClass, point.eClass());

            EObject coord = (EObject) point.eGet(pointCoordinatesRef);
            assertCoordinate(coord, -122.4194, 37.7749);
        }

        @Test
        @DisplayName("nested polymorphic types with simple names")
        @SuppressWarnings("unchecked")
        void nestedPolymorphicWithSimpleNames() throws IOException {
            // FeatureCollection with Features containing different geometry types
            String json = """
                {
                    "type": "FeatureCollection",
                    "features": [
                        {
                            "id": "point-feature",
                            "type": "Feature",
                            "geometry": {
                                "type": "Point",
                                "coordinates": {"longitude": 0.0, "latitude": 0.0}
                            }
                        },
                        {
                            "id": "line-feature",
                            "type": "Feature",
                            "geometry": {
                                "type": "LineString",
                                "coordinates": [
                                    {"longitude": 0.0, "latitude": 0.0},
                                    {"longitude": 10.0, "latitude": 10.0}
                                ]
                            }
                        }
                    ]
                }
                """;

            EObject collection = loadJsonWithRootHint(json, featureCollectionClass);
            assertEquals("FeatureCollection", collection.eGet(featureCollectionTypeAttr));

            EList<EObject> features = (EList<EObject>) collection.eGet(featureCollectionFeaturesRef);
            assertEquals(2, features.size());

            // First feature has Point geometry
            EObject feat1 = features.get(0);
            assertEquals("point-feature", feat1.eGet(featureIdAttr));
            EObject geom1 = (EObject) feat1.eGet(featureGeometryRef);
            assertEquals(pointClass, geom1.eClass());

            // Second feature has LineString geometry
            EObject feat2 = features.get(1);
            assertEquals("line-feature", feat2.eGet(featureIdAttr));
            EObject geom2 = (EObject) feat2.eGet(featureGeometryRef);
            assertEquals(lineStringClass, geom2.eClass());
        }

        @Test
        @DisplayName("geometry collection with simple names")
        @SuppressWarnings("unchecked")
        void geometryCollectionWithSimpleNames() throws IOException {
            String json = """
                {
                    "type": "GeometryCollection",
                    "geometries": [
                        {
                            "type": "Point",
                            "coordinates": {"longitude": 1.0, "latitude": 2.0}
                        },
                        {
                            "type": "Polygon",
                            "exterior": {
                                "coordinates": [
                                    {"longitude": 0.0, "latitude": 0.0},
                                    {"longitude": 10.0, "latitude": 0.0},
                                    {"longitude": 10.0, "latitude": 10.0},
                                    {"longitude": 0.0, "latitude": 0.0}
                                ]
                            }
                        }
                    ]
                }
                """;

            EObject collection = loadJsonWithRootHint(json, geometryCollectionClass);
            assertEquals("GeometryCollection", collection.eGet(geomTypeAttr));

            EList<EObject> geometries = (EList<EObject>) collection.eGet(geometryCollectionGeometriesRef);
            assertEquals(2, geometries.size());
            assertEquals(pointClass, geometries.get(0).eClass());
            assertEquals(polygonClass, geometries.get(1).eClass());
        }

        @Test
        @DisplayName("no type in root when CODEC_ROOT_TYPE is set")
        void noTypeInRootWithHint() throws IOException {
            // When root type is known from hint, no "type" field is needed
            String json = """
                {
                    "coordinates": {
                        "longitude": 13.405,
                        "latitude": 52.52
                    }
                }
                """;

            EObject point = loadJsonWithRootHint(json, pointClass);
            assertEquals(pointClass, point.eClass());

            EObject coord = (EObject) point.eGet(pointCoordinatesRef);
            assertCoordinate(coord, 13.405, 52.52);
        }
    }
}
