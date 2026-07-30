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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.geojson.Coordinates;
import org.geojson.GeoJsonFactory;
import org.geojson.GeoJsonPackage;
import org.geojson.Point;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the new CodecResource API with forceWrite on volatile features.
 * <p>
 * These tests verify that the {@code forceWrite(EStructuralFeature...)} convenience method
 * on {@link ConfigurationResolver.Builder} correctly enables serialization of volatile
 * attributes in the GeoJSON model.
 * </p>
 */
@DisplayName("ForceWrite Integration Tests")
class ForceWriteTest {

    private static List<EStructuralFeature> volatileDataFeatures;
    private static List<EStructuralFeature> volatileBboxFeatures;

    private MetadataWhiteboard metadataService;

    @BeforeAll
    static void collectVolatileFeatures() {
        volatileDataFeatures = new ArrayList<>();
        volatileBboxFeatures = new ArrayList<>();

        GeoJsonPackage pkg = GeoJsonPackage.eINSTANCE;
        for (EClassifier classifier : pkg.getEClassifiers()) {
            if (classifier instanceof EClass eClass) {
                // Collect volatile "data" features
                EStructuralFeature dataFeature = eClass.getEStructuralFeature("data");
                if (dataFeature != null && dataFeature.isVolatile()) {
                    volatileDataFeatures.add(dataFeature);
                }

                // Collect volatile "bbox" features
                EStructuralFeature bboxFeature = eClass.getEStructuralFeature("bbox");
                if (bboxFeature != null && bboxFeature.isVolatile()) {
                    volatileBboxFeatures.add(bboxFeature);
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(GeoJsonPackage.eINSTANCE);
    }

    @Nested
    @DisplayName("ConfigurationResolver forceWrite")
    class ConfigurationResolverForceWriteTests {

        @Test
        @DisplayName("volatile data features are found in GeoJSON model")
        void volatileDataFeaturesExist() {
            assertTrue(volatileDataFeatures.size() > 0,
                    "GeoJSON model should have volatile 'data' features");

            // Point should have a volatile data feature
            EStructuralFeature pointData = GeoJsonPackage.Literals.POINT.getEStructuralFeature("data");
            assertNotNull(pointData, "Point should have 'data' feature");
            assertTrue(pointData.isVolatile(), "Point.data should be volatile");
        }

        @Test
        @DisplayName("forceWrite enables volatile feature in FeatureConfig")
        void forceWriteEnablesVolatileFeature() {
            EStructuralFeature pointData = GeoJsonPackage.Literals.POINT.getEStructuralFeature("data");

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .forceWrite(pointData)
                    .build();

            DiagnosticCollector diag = new DiagnosticCollector();
            FeatureConfig config = resolver.resolveFeatureConfig(pointData, diag);

            assertTrue(config.isForceWrite(), "forceWrite should be true");
            assertTrue(config.shouldSerialize(), "shouldSerialize should return true when forceWrite is set");
        }

        @Test
        @DisplayName("volatile feature without forceWrite is ignored")
        void volatileFeatureWithoutForceWriteIsIgnored() {
            EStructuralFeature pointData = GeoJsonPackage.Literals.POINT.getEStructuralFeature("data");

            ConfigurationResolver resolver = ConfigurationResolver.defaults();

            DiagnosticCollector diag = new DiagnosticCollector();
            FeatureConfig config = resolver.resolveFeatureConfig(pointData, diag);

            assertTrue(config.isIgnore(), "volatile feature should be ignored by default");
        }
    }

    @Nested
    @DisplayName("CodecResource Serialization with forceWrite")
    class CodecResourceSerializationTests {

        @Test
        @DisplayName("Point serializes with coordinates when forceWrite is set")
        void pointSerializesWithCoordinates() throws Exception {
            // Create Point with coordinates
            Point point = GeoJsonFactory.eINSTANCE.createPoint();
            Coordinates coords = GeoJsonFactory.eINSTANCE.createCoordinates();
            coords.setLongitude(8.6821);
            coords.setLatitude(50.1109);
            point.setCoordinates(coords);

            // Verify the volatile data feature returns the expected array via eGet
            EStructuralFeature dataFeature = GeoJsonPackage.Literals.POINT.getEStructuralFeature("data");
            Object dataValue = point.eGet(dataFeature);
            assertNotNull(dataValue, "Point.data should return array");
            assertTrue(dataValue instanceof double[], "data should be double[]");
            double[] data = (double[]) dataValue;
            // GeoJSON model always returns [lon, lat, elevation] even when elevation not set (defaults to 0.0)
            assertEquals(3, data.length, "coordinates should have 3 elements (lon, lat, elevation)");
            assertEquals(8.6821, data[0], 0.0001, "longitude");
            assertEquals(50.1109, data[1], 0.0001, "latitude");
            assertEquals(0.0, data[2], 0.0001, "elevation (default)");

            // Create resolver with forceWrite for volatile features
            List<EStructuralFeature> allVolatile = new ArrayList<>();
            allVolatile.addAll(volatileDataFeatures);
            allVolatile.addAll(volatileBboxFeatures);

            ConfigurationResolver resolver = ConfigurationResolver.builder()
                    .typeKey("type")
                    .typeStrategy(TypeStrategy.NAME)
                    .useNamesFromExtendedMetaData(true)
                    .useId(false)
                    .typeInclude(true)
                    .forceWrite(allVolatile.toArray(new EStructuralFeature[0]))
                    .forceRead(allVolatile.toArray(new EStructuralFeature[0]))
                    .build();

            // Create resource and serialize
            CodecResource resource = new CodecResource(
                    URI.createURI("test.geojson"),
                    metadataService,
                    resolver,
                    null,
                    null);
            resource.getContents().add(point);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, null);
            String json = out.toString();

            System.out.println("=== Serialized JSON ===");
            System.out.println(json);
            System.out.println("=======================");

            // Verify output contains coordinates
            assertTrue(json.contains("coordinates"),
                    "JSON should contain 'coordinates' key (from ExtendedMetaData name for 'data')");
            assertTrue(json.contains("8.6821") || json.contains("8.682"),
                    "JSON should contain longitude value");
            assertTrue(json.contains("50.1109") || json.contains("50.110"),
                    "JSON should contain latitude value");
            assertTrue(json.contains("\"type\"") && json.contains("\"Point\""),
                    "JSON should contain type information");
        }
    }
}
