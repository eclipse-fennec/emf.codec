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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.geojson.Feature;
import org.geojson.GeoJsonPackage;
import org.geojson.MultiPolygon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Every geometry type round-trips unchanged through {@link GeoJsonResourceImpl} (issue #226).
 * <p>
 * common.models#27 made the computed {@code data} attributes and {@code bbox} derived, and
 * common.models#30 made {@code MultiPolygon.polygons} transient. The codec selects
 * {@code data}/{@code bbox} by name and {@code isVolatile()} and forces them, so neither
 * should change the output - this matrix pins that down, 2D and 3D, with and without a bbox.
 * Derived computed attributes are also what makes {@link EcoreUtil#equals} usable on
 * geometries, which the reload check relies on.
 * </p>
 */
@DisplayName("GeoJSON round-trip matrix")
class GeoJsonRoundTripMatrixTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

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

    static Stream<Arguments> documents() {
        return Stream.of(
                Arguments.of("Point 2D", """
                        {"type":"Point","coordinates":[1.5,2.5]}"""),
                Arguments.of("Point 3D", """
                        {"type":"Point","coordinates":[1.5,2.5,3.5]}"""),
                Arguments.of("Point with bbox", """
                        {"type":"Point","bbox":[1.5,2.5,1.5,2.5],"coordinates":[1.5,2.5]}"""),
                Arguments.of("MultiPoint 2D", """
                        {"type":"MultiPoint","coordinates":[[1.5,2.5],[3.5,4.5]]}"""),
                Arguments.of("MultiPoint 3D", """
                        {"type":"MultiPoint","coordinates":[[1.5,2.5,9.5],[3.5,4.5,9.5]]}"""),
                Arguments.of("LineString 2D", """
                        {"type":"LineString","coordinates":[[1.5,2.5],[3.5,4.5]]}"""),
                Arguments.of("LineString 3D with bbox", """
                        {"type":"LineString","bbox":[1.5,2.5,9.5,3.5,4.5,9.5],\
                        "coordinates":[[1.5,2.5,9.5],[3.5,4.5,9.5]]}"""),
                Arguments.of("Polygon 2D", """
                        {"type":"Polygon","coordinates":[[[0.5,0.5],[4.5,0.5],[4.5,4.5],[0.5,0.5]]]}"""),
                Arguments.of("Polygon with hole, bbox", """
                        {"type":"Polygon","bbox":[0.5,0.5,4.5,4.5],"coordinates":[\
                        [[0.5,0.5],[4.5,0.5],[4.5,4.5],[0.5,0.5]],\
                        [[1.5,1.5],[2.5,1.5],[2.5,2.5],[1.5,1.5]]]}"""),
                Arguments.of("Polygon 3D", """
                        {"type":"Polygon","coordinates":[[[0.5,0.5,7.5],[4.5,0.5,7.5],[4.5,4.5,7.5],[0.5,0.5,7.5]]]}"""),
                Arguments.of("MultiLineString 2D", """
                        {"type":"MultiLineString","coordinates":[[[1.5,2.5],[3.5,4.5]],[[5.5,6.5],[7.5,8.5]]]}"""),
                Arguments.of("MultiLineString 3D", """
                        {"type":"MultiLineString","coordinates":[[[1.5,2.5,9.5],[3.5,4.5,9.5]]]}"""),
                Arguments.of("MultiPolygon 2D", """
                        {"type":"MultiPolygon","coordinates":[\
                        [[[0.5,0.5],[4.5,0.5],[4.5,4.5],[0.5,0.5]]],\
                        [[[5.5,5.5],[8.5,5.5],[8.5,8.5],[5.5,5.5]]]]}"""),
                Arguments.of("MultiPolygon 3D with bbox", """
                        {"type":"MultiPolygon","bbox":[0.5,0.5,7.5,4.5,4.5,7.5],"coordinates":[\
                        [[[0.5,0.5,7.5],[4.5,0.5,7.5],[4.5,4.5,7.5],[0.5,0.5,7.5]]]]}"""),
                Arguments.of("GeometryCollection", """
                        {"type":"GeometryCollection","geometries":[\
                        {"type":"Point","coordinates":[1.5,2.5]},\
                        {"type":"LineString","coordinates":[[1.5,2.5],[3.5,4.5]]}]}"""),
                Arguments.of("Feature", """
                        {"type":"Feature","id":"f1","geometry":{"type":"Point","coordinates":[1.5,2.5]},"properties":null}"""),
                Arguments.of("Feature with bbox", """
                        {"type":"Feature","bbox":[1.5,2.5,1.5,2.5],"id":"f1",\
                        "geometry":{"type":"Point","coordinates":[1.5,2.5]},"properties":null}"""),
                Arguments.of("FeatureCollection", """
                        {"type":"FeatureCollection","features":[\
                        {"type":"Feature","id":"f1","geometry":{"type":"Point","coordinates":[1.5,2.5]},"properties":null},\
                        {"type":"Feature","id":"f2","geometry":{"type":"MultiPolygon","coordinates":[\
                        [[[0.5,0.5],[4.5,0.5],[4.5,4.5],[0.5,0.5]]]]},"properties":null}]}"""));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("documents")
    @DisplayName("the written document equals the one read")
    void outputEqualsInput(String name, String json) throws IOException {
        EObject loaded = load(json);

        String written = save(loaded);

        assertEquals(JSON.readTree(json), JSON.readTree(written), () -> name + " was written as " + written);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("documents")
    @DisplayName("reloading the written document gives an equal model")
    void reloadIsEqual(String name, String json) throws IOException {
        EObject loaded = load(json);

        EObject reloaded = load(save(loaded));

        assertTrue(EcoreUtil.equals(loaded, reloaded), () -> name + ": the reload differs from the first load");
    }

    @Test
    @DisplayName("MultiPolygon writes no polygons key and keeps its polygon count")
    void multiPolygonHasNoPolygonsKey() throws IOException {
        String json = documents().filter(a -> "MultiPolygon 2D".equals(a.get()[0]))
                .map(a -> (String) a.get()[1]).findFirst().orElseThrow();
        MultiPolygon loaded = (MultiPolygon) load(json);

        String written = save(loaded);
        MultiPolygon reloaded = (MultiPolygon) load(written);

        assertFalse(containsKey(JSON.readTree(written), "polygons"),
                () -> "the object view of the coordinates is transient, was: " + written);
        assertEquals(2, loaded.getPolygons().size());
        assertEquals(2, reloaded.getPolygons().size(), "a reload must not read the polygons twice");
    }

    /**
     * {@code bbox} is optional (RFC 7946 §5, common.models#32). The required-feature check reads
     * the model's multiplicity, so {@code strictOnMissing} accepts a geometry without one - and
     * still takes one that has it.
     */
    @Test
    @DisplayName("strictOnMissing accepts a geometry without bbox")
    void strictOnMissingAcceptsMissingBbox() throws IOException {
        Map<String, Object> strict = Map.of("codec.strictOnMissing", true);

        EObject withoutBbox = load("{\"type\":\"Point\",\"coordinates\":[1.5,2.5]}", strict);
        load("{\"type\":\"Point\",\"bbox\":[1.5,2.5,1.5,2.5],\"coordinates\":[1.5,2.5]}", strict);

        assertFalse(withoutBbox.eIsSet(GeoJsonPackage.Literals.GEO_JSON_OBJECT__BBOX));
    }

    /**
     * {@code Feature.id} is optional (RFC 7946 §3.2, common.models#33).
     */
    @Test
    @DisplayName("strictOnMissing accepts a Feature without id")
    void strictOnMissingAcceptsMissingFeatureId() throws IOException {
        Map<String, Object> strict = Map.of("codec.strictOnMissing", true);

        Feature feature = (Feature) load(
                "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[1.5,2.5]}}", strict);

        assertNull(feature.getId());
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject load(String json) throws IOException {
        return load(json, Map.of());
    }

    private EObject load(String json, Map<String, Object> options) throws IOException {
        GeoJsonResourceImpl resource = new GeoJsonResourceImpl(URI.createURI("test://matrix.geojson"),
                metadataService);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), new HashMap<>(options));
        assertTrue(resource.getErrors().isEmpty(), () -> "errors=" + resource.getErrors());
        assertEquals(1, resource.getContents().size());
        return resource.getContents().get(0);
    }

    private String save(EObject object) throws IOException {
        GeoJsonResourceImpl resource = new GeoJsonResourceImpl(URI.createURI("test://matrix.geojson"),
                metadataService);
        resource.getContents().add(EcoreUtil.copy(object));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Map.of());
        assertTrue(resource.getErrors().isEmpty(), () -> "errors=" + resource.getErrors());
        return out.toString(StandardCharsets.UTF_8);
    }

    private static boolean containsKey(JsonNode node, String key) {
        if (node.isObject() && node.has(key)) {
            return true;
        }
        for (JsonNode child : node) {
            if (containsKey(child, key)) {
                return true;
            }
        }
        return false;
    }
}
