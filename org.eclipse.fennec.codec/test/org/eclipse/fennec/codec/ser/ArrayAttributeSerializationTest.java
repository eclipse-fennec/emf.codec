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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for array attribute serialization.
 * <p>
 * Tests serialization of EAttributes with array data types:
 * <ul>
 *   <li>{@code double[]} - 1D double array</li>
 *   <li>{@code double[][]} - 2D double array</li>
 *   <li>{@code double[][][]} - 3D double array</li>
 *   <li>{@code int[]}, {@code long[]}, {@code float[]} - other numeric arrays</li>
 *   <li>{@code boolean[]} - boolean array</li>
 *   <li>{@code String[]} - string array</li>
 * </ul>
 * </p>
 */
@DisplayName("Array Attribute Serialization Tests")
class ArrayAttributeSerializationTest {

    private static final String TEST_NS_URI = "http://test.fennec/arrays";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    // EClasses
    private EClass arrayHolderClass;

    // Attributes
    private EAttribute doubleArrayAttr;
    private EAttribute doubleArray2DAttr;
    private EAttribute doubleArray3DAttr;
    private EAttribute intArrayAttr;
    private EAttribute longArrayAttr;
    private EAttribute floatArrayAttr;
    private EAttribute booleanArrayAttr;
    private EAttribute stringArrayAttr;
    private EAttribute bigDecimalArrayAttr;

    @BeforeEach
    void setUp() {
        // Create test package dynamically
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("arrays");
        testPackage.setNsPrefix("arr");
        testPackage.setNsURI(TEST_NS_URI);

        // Create ArrayHolder class
        arrayHolderClass = EcoreFactory.eINSTANCE.createEClass();
        arrayHolderClass.setName("ArrayHolder");
        testPackage.getEClassifiers().add(arrayHolderClass);

        // Create double[] attribute
        doubleArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        doubleArrayAttr.setName("doubleArray");
        doubleArrayAttr.setEType(createArrayDataType("DoubleArray1D", double[].class));
        arrayHolderClass.getEStructuralFeatures().add(doubleArrayAttr);

        // Create double[][] attribute
        doubleArray2DAttr = EcoreFactory.eINSTANCE.createEAttribute();
        doubleArray2DAttr.setName("doubleArray2D");
        doubleArray2DAttr.setEType(createArrayDataType("DoubleArray2D", double[][].class));
        arrayHolderClass.getEStructuralFeatures().add(doubleArray2DAttr);

        // Create double[][][] attribute
        doubleArray3DAttr = EcoreFactory.eINSTANCE.createEAttribute();
        doubleArray3DAttr.setName("doubleArray3D");
        doubleArray3DAttr.setEType(createArrayDataType("DoubleArray3D", double[][][].class));
        arrayHolderClass.getEStructuralFeatures().add(doubleArray3DAttr);

        // Create int[] attribute
        intArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        intArrayAttr.setName("intArray");
        intArrayAttr.setEType(createArrayDataType("IntArray", int[].class));
        arrayHolderClass.getEStructuralFeatures().add(intArrayAttr);

        // Create long[] attribute
        longArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        longArrayAttr.setName("longArray");
        longArrayAttr.setEType(createArrayDataType("LongArray", long[].class));
        arrayHolderClass.getEStructuralFeatures().add(longArrayAttr);

        // Create float[] attribute
        floatArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        floatArrayAttr.setName("floatArray");
        floatArrayAttr.setEType(createArrayDataType("FloatArray", float[].class));
        arrayHolderClass.getEStructuralFeatures().add(floatArrayAttr);

        // Create boolean[] attribute
        booleanArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        booleanArrayAttr.setName("booleanArray");
        booleanArrayAttr.setEType(createArrayDataType("BooleanArray", boolean[].class));
        arrayHolderClass.getEStructuralFeatures().add(booleanArrayAttr);

        // Create String[] attribute
        stringArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        stringArrayAttr.setName("stringArray");
        stringArrayAttr.setEType(createArrayDataType("StringArray", String[].class));
        arrayHolderClass.getEStructuralFeatures().add(stringArrayAttr);

        // Create BigDecimal[] attribute
        bigDecimalArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        bigDecimalArrayAttr.setName("bigDecimalArray");
        bigDecimalArrayAttr.setEType(createArrayDataType("BigDecimalArray", BigDecimal[].class));
        arrayHolderClass.getEStructuralFeatures().add(bigDecimalArrayAttr);

        // Register package
        EPackage.Registry.INSTANCE.put(TEST_NS_URI, testPackage);

        // Create metadata service
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    private EDataType createArrayDataType(String name, Class<?> instanceClass) {
        EDataType dataType = EcoreFactory.eINSTANCE.createEDataType();
        dataType.setName(name);
        dataType.setInstanceClass(instanceClass);
        testPackage.getEClassifiers().add(dataType);
        return dataType;
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(TEST_NS_URI);
    }

    private EObject createHolder() {
        return testPackage.getEFactoryInstance().create(arrayHolderClass);
    }

    private String saveJson(EObject object) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://arrays.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);

        resource.getContents().add(object);

        Map<String, Object> options = new HashMap<>();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            resource.save(os, options);

            if (!resource.getErrors().isEmpty()) {
                fail("Serialization errors: " + resource.getErrors());
            }

            return os.toString(StandardCharsets.UTF_8);
        }
    }

    // ========================================================================
    // 1D Array Tests
    // ========================================================================

    @Nested
    @DisplayName("1D Arrays")
    class OneDimensionalArrayTests {

        @Test
        @DisplayName("double[] - simple array")
        void doubleArray() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArrayAttr, new double[]{1.1, 2.2, 3.3, 4.4, 5.5});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray\""));
            assertTrue(json.contains("1.1"));
            assertTrue(json.contains("5.5"));
        }

        @Test
        @DisplayName("double[] - empty array")
        void doubleArrayEmpty() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArrayAttr, new double[0]);

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray\":[]") || json.contains("\"doubleArray\": []"));
        }

        @Test
        @DisplayName("double[] - single element")
        void doubleArraySingleElement() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArrayAttr, new double[]{42.5});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("42.5"));
        }

        @Test
        @DisplayName("double[] - negative values")
        void doubleArrayNegative() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArrayAttr, new double[]{-1.5, -2.5, 0.0, 2.5, 1.5});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("-1.5"));
            assertTrue(json.contains("-2.5"));
        }

        @Test
        @DisplayName("int[] - simple array")
        void intArray() throws IOException {
            EObject holder = createHolder();
            holder.eSet(intArrayAttr, new int[]{1, 2, 3, 4, 5});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"intArray\""));
            assertTrue(json.contains("[1,2,3,4,5]") || json.contains("[1, 2, 3, 4, 5]"));
        }

        @Test
        @DisplayName("long[] - simple array")
        void longArray() throws IOException {
            EObject holder = createHolder();
            holder.eSet(longArrayAttr, new long[]{1000000000000L, 2000000000000L, 3000000000000L});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"longArray\""));
            assertTrue(json.contains("1000000000000"));
            assertTrue(json.contains("3000000000000"));
        }

        @Test
        @DisplayName("float[] - simple array")
        void floatArray() throws IOException {
            EObject holder = createHolder();
            holder.eSet(floatArrayAttr, new float[]{1.1f, 2.2f, 3.3f});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"floatArray\""));
        }

        @Test
        @DisplayName("boolean[] - simple array")
        void booleanArray() throws IOException {
            EObject holder = createHolder();
            holder.eSet(booleanArrayAttr, new boolean[]{true, false, true, true, false});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"booleanArray\""));
            assertTrue(json.contains("true"));
            assertTrue(json.contains("false"));
        }

        @Test
        @DisplayName("String[] - simple array")
        void stringArray() throws IOException {
            EObject holder = createHolder();
            holder.eSet(stringArrayAttr, new String[]{"hello", "world", "test"});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"stringArray\""));
            assertTrue(json.contains("\"hello\""));
            assertTrue(json.contains("\"world\""));
            assertTrue(json.contains("\"test\""));
        }

        @Test
        @DisplayName("String[] - empty strings")
        void stringArrayWithEmptyStrings() throws IOException {
            EObject holder = createHolder();
            holder.eSet(stringArrayAttr, new String[]{"", "hello", "", "world", ""});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"stringArray\""));
            assertTrue(json.contains("\"hello\""));
            // Empty strings should be present as ""
        }
    }

    // ========================================================================
    // 2D Array Tests
    // ========================================================================

    @Nested
    @DisplayName("2D Arrays")
    class TwoDimensionalArrayTests {

        @Test
        @DisplayName("double[][] - simple 2D array")
        void doubleArray2D() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArray2DAttr, new double[][]{
                {1.0, 2.0},
                {3.0, 4.0},
                {5.0, 6.0}
            });

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray2D\""));
            assertTrue(json.contains("1.0") || json.contains("1,") || json.contains("1]"));
            assertTrue(json.contains("6.0") || json.contains("6,") || json.contains("6]"));
        }

        @Test
        @DisplayName("double[][] - GeoJSON LineString style")
        void doubleArray2DLineString() throws IOException {
            // LineString coordinates: [[lng, lat], [lng, lat], ...]
            EObject holder = createHolder();
            holder.eSet(doubleArray2DAttr, new double[][]{
                {8.6821, 50.1109},
                {8.6831, 50.1115},
                {8.6845, 50.1120}
            });

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray2D\""));
            assertTrue(json.contains("8.6821"));
            assertTrue(json.contains("50.1109"));
        }

        @Test
        @DisplayName("double[][] - empty outer array")
        void doubleArray2DEmpty() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArray2DAttr, new double[0][]);

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray2D\":[]") || json.contains("\"doubleArray2D\": []"));
        }

        @Test
        @DisplayName("double[][] - jagged array (different lengths)")
        void doubleArray2DJagged() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArray2DAttr, new double[][]{
                {1.0},
                {2.0, 3.0},
                {4.0, 5.0, 6.0}
            });

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray2D\""));
        }
    }

    // ========================================================================
    // 3D Array Tests
    // ========================================================================

    @Nested
    @DisplayName("3D Arrays")
    class ThreeDimensionalArrayTests {

        @Test
        @DisplayName("double[][][] - simple 3D array")
        void doubleArray3D() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArray3DAttr, new double[][][]{
                {
                    {1.0, 2.0},
                    {3.0, 4.0}
                },
                {
                    {5.0, 6.0},
                    {7.0, 8.0}
                }
            });

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray3D\""));
            assertTrue(json.contains("1.0") || json.contains("1,") || json.contains("1]"));
            assertTrue(json.contains("8.0") || json.contains("8,") || json.contains("8]"));
        }

        @Test
        @DisplayName("double[][][] - GeoJSON Polygon style")
        void doubleArray3DPolygon() throws IOException {
            // Polygon coordinates: [[[lng, lat], ...]]
            EObject holder = createHolder();
            holder.eSet(doubleArray3DAttr, new double[][][]{
                {
                    {0.0, 0.0},
                    {10.0, 0.0},
                    {10.0, 10.0},
                    {0.0, 10.0},
                    {0.0, 0.0}
                }
            });

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray3D\""));
            assertTrue(json.contains("10.0") || json.contains("10,") || json.contains("10]"));
        }

        @Test
        @DisplayName("double[][][] - GeoJSON Polygon with hole")
        void doubleArray3DPolygonWithHole() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArray3DAttr, new double[][][]{
                {
                    {0.0, 0.0},
                    {20.0, 0.0},
                    {20.0, 20.0},
                    {0.0, 20.0},
                    {0.0, 0.0}
                },
                {
                    {5.0, 5.0},
                    {15.0, 5.0},
                    {15.0, 15.0},
                    {5.0, 15.0},
                    {5.0, 5.0}
                }
            });

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray3D\""));
            assertTrue(json.contains("15.0") || json.contains("15,") || json.contains("15]"));
        }

        @Test
        @DisplayName("double[][][] - empty")
        void doubleArray3DEmpty() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArray3DAttr, new double[0][][]);

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray3D\":[]") || json.contains("\"doubleArray3D\": []"));
        }
    }

    // ========================================================================
    // Object Array Tests (BigDecimal[], etc.)
    // ========================================================================

    @Nested
    @DisplayName("Object Arrays")
    class ObjectArrayTests {

        @Test
        @DisplayName("BigDecimal[] - decimal numbers")
        void bigDecimalArray() throws IOException {
            EObject holder = createHolder();
            holder.eSet(bigDecimalArrayAttr, new BigDecimal[]{
                new BigDecimal("123.456"),
                new BigDecimal("789.012"),
                new BigDecimal("0.001")
            });

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"bigDecimalArray\""));
            assertTrue(json.contains("123.456"));
            assertTrue(json.contains("789.012"));
            assertTrue(json.contains("0.001"));
        }

        @Test
        @DisplayName("BigDecimal[] - empty array")
        void bigDecimalArrayEmpty() throws IOException {
            EObject holder = createHolder();
            holder.eSet(bigDecimalArrayAttr, new BigDecimal[0]);

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"bigDecimalArray\":[]") || json.contains("\"bigDecimalArray\": []"));
        }

        @Test
        @DisplayName("BigDecimal[] - large precision numbers")
        void bigDecimalArrayLargePrecision() throws IOException {
            EObject holder = createHolder();
            holder.eSet(bigDecimalArrayAttr, new BigDecimal[]{
                new BigDecimal("12345678901234567890.12345678901234567890"),
                new BigDecimal("-0.00000000000000001")
            });

            String json = saveJson(holder);
            System.out.println("BigDecimal large precision JSON: " + json);

            assertNotNull(json);
            assertTrue(json.contains("\"bigDecimalArray\""));
            // Jackson may represent very large/small numbers in different formats
            assertTrue(json.contains("12345678901234567890.12345678901234567890")
                    || json.contains("1.2345678901234567890E+19"), "Large number should be present");
            assertTrue(json.contains("-0.00000000000000001")
                    || json.contains("-1E-17") || json.contains("-1.0E-17"), "Small number should be present");
        }
    }

    // ========================================================================
    // Multiple Attributes Test
    // ========================================================================

    @Nested
    @DisplayName("Multiple Attributes")
    class MultipleAttributesTests {

        @Test
        @DisplayName("multiple array attributes in one object")
        void multipleArrayAttributes() throws IOException {
            EObject holder = createHolder();
            holder.eSet(doubleArrayAttr, new double[]{1.1, 2.2, 3.3});
            holder.eSet(intArrayAttr, new int[]{1, 2, 3});
            holder.eSet(stringArrayAttr, new String[]{"a", "b", "c"});
            holder.eSet(booleanArrayAttr, new boolean[]{true, false});

            String json = saveJson(holder);

            assertNotNull(json);
            assertTrue(json.contains("\"doubleArray\""));
            assertTrue(json.contains("\"intArray\""));
            assertTrue(json.contains("\"stringArray\""));
            assertTrue(json.contains("\"booleanArray\""));
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("double[] round-trip")
        void doubleArrayRoundTrip() throws IOException {
            double[] original = {1.1, 2.2, 3.3, 4.4, 5.5};

            EObject holder = createHolder();
            holder.eSet(doubleArrayAttr, original);

            // Serialize
            String json = saveJson(holder);

            // Deserialize
            CodecResource resource = new CodecResource(
                    URI.createURI("test://arrays.json"),
                    metadataService,
                    ConfigurationResolver.defaults(),
                    null);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, arrayHolderClass);

            try (var is = new java.io.ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                resource.load(is, options);
            }

            if (!resource.getErrors().isEmpty()) {
                fail("Deserialization errors: " + resource.getErrors());
            }

            EObject loaded = resource.getContents().get(0);
            double[] result = (double[]) loaded.eGet(doubleArrayAttr);

            assertEquals(original.length, result.length);
            for (int i = 0; i < original.length; i++) {
                assertEquals(original[i], result[i], 0.0001);
            }
        }

        @Test
        @DisplayName("double[][] round-trip (GeoJSON LineString)")
        void doubleArray2DRoundTrip() throws IOException {
            double[][] original = {
                {8.6821, 50.1109},
                {8.6831, 50.1115},
                {8.6845, 50.1120}
            };

            EObject holder = createHolder();
            holder.eSet(doubleArray2DAttr, original);

            // Serialize
            String json = saveJson(holder);

            // Deserialize
            CodecResource resource = new CodecResource(
                    URI.createURI("test://arrays.json"),
                    metadataService,
                    ConfigurationResolver.defaults(),
                    null);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, arrayHolderClass);

            try (var is = new java.io.ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                resource.load(is, options);
            }

            if (!resource.getErrors().isEmpty()) {
                fail("Deserialization errors: " + resource.getErrors());
            }

            EObject loaded = resource.getContents().get(0);
            double[][] result = (double[][]) loaded.eGet(doubleArray2DAttr);

            assertEquals(original.length, result.length);
            for (int i = 0; i < original.length; i++) {
                assertEquals(original[i].length, result[i].length);
                for (int j = 0; j < original[i].length; j++) {
                    assertEquals(original[i][j], result[i][j], 0.0001);
                }
            }
        }

        @Test
        @DisplayName("double[][][] round-trip (GeoJSON Polygon)")
        void doubleArray3DRoundTrip() throws IOException {
            double[][][] original = {
                {
                    {0.0, 0.0},
                    {10.0, 0.0},
                    {10.0, 10.0},
                    {0.0, 10.0},
                    {0.0, 0.0}
                }
            };

            EObject holder = createHolder();
            holder.eSet(doubleArray3DAttr, original);

            // Serialize
            String json = saveJson(holder);

            // Deserialize
            CodecResource resource = new CodecResource(
                    URI.createURI("test://arrays.json"),
                    metadataService,
                    ConfigurationResolver.defaults(),
                    null);

            Map<String, Object> options = new HashMap<>();
            options.put(CodecResource.CODEC_ROOT_TYPE, arrayHolderClass);

            try (var is = new java.io.ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                resource.load(is, options);
            }

            if (!resource.getErrors().isEmpty()) {
                fail("Deserialization errors: " + resource.getErrors());
            }

            EObject loaded = resource.getContents().get(0);
            double[][][] result = (double[][][]) loaded.eGet(doubleArray3DAttr);

            assertEquals(original.length, result.length);
            for (int i = 0; i < original.length; i++) {
                assertEquals(original[i].length, result[i].length);
                for (int j = 0; j < original[i].length; j++) {
                    assertEquals(original[i][j].length, result[i][j].length);
                    for (int k = 0; k < original[i][j].length; k++) {
                        assertEquals(original[i][j][k], result[i][j][k], 0.0001);
                    }
                }
            }
        }
    }
}
