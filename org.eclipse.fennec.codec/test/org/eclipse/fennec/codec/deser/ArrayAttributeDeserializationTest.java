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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for array attribute deserialization.
 * <p>
 * Tests deserialization of EAttributes with array data types:
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
@DisplayName("Array Attribute Deserialization Tests")
class ArrayAttributeDeserializationTest {

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
    private EAttribute dateArrayAttr;
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
        doubleArrayAttr.setEType(createDoubleArrayDataType("DoubleArray1D", double[].class));
        arrayHolderClass.getEStructuralFeatures().add(doubleArrayAttr);

        // Create double[][] attribute
        doubleArray2DAttr = EcoreFactory.eINSTANCE.createEAttribute();
        doubleArray2DAttr.setName("doubleArray2D");
        doubleArray2DAttr.setEType(createDoubleArrayDataType("DoubleArray2D", double[][].class));
        arrayHolderClass.getEStructuralFeatures().add(doubleArray2DAttr);

        // Create double[][][] attribute
        doubleArray3DAttr = EcoreFactory.eINSTANCE.createEAttribute();
        doubleArray3DAttr.setName("doubleArray3D");
        doubleArray3DAttr.setEType(createDoubleArrayDataType("DoubleArray3D", double[][][].class));
        arrayHolderClass.getEStructuralFeatures().add(doubleArray3DAttr);

        // Create int[] attribute
        intArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        intArrayAttr.setName("intArray");
        intArrayAttr.setEType(createDoubleArrayDataType("IntArray", int[].class));
        arrayHolderClass.getEStructuralFeatures().add(intArrayAttr);

        // Create long[] attribute
        longArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        longArrayAttr.setName("longArray");
        longArrayAttr.setEType(createDoubleArrayDataType("LongArray", long[].class));
        arrayHolderClass.getEStructuralFeatures().add(longArrayAttr);

        // Create float[] attribute
        floatArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        floatArrayAttr.setName("floatArray");
        floatArrayAttr.setEType(createDoubleArrayDataType("FloatArray", float[].class));
        arrayHolderClass.getEStructuralFeatures().add(floatArrayAttr);

        // Create boolean[] attribute
        booleanArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        booleanArrayAttr.setName("booleanArray");
        booleanArrayAttr.setEType(createDoubleArrayDataType("BooleanArray", boolean[].class));
        arrayHolderClass.getEStructuralFeatures().add(booleanArrayAttr);

        // Create String[] attribute
        stringArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        stringArrayAttr.setName("stringArray");
        stringArrayAttr.setEType(createArrayDataType("StringArray", String[].class));
        arrayHolderClass.getEStructuralFeatures().add(stringArrayAttr);

        // Create Date[] attribute
        dateArrayAttr = EcoreFactory.eINSTANCE.createEAttribute();
        dateArrayAttr.setName("dateArray");
        dateArrayAttr.setEType(createArrayDataType("DateArray", Date[].class));
        arrayHolderClass.getEStructuralFeatures().add(dateArrayAttr);

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

    // Keep old method name for backward compatibility
    private EDataType createDoubleArrayDataType(String name, Class<?> instanceClass) {
        return createArrayDataType(name, instanceClass);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(TEST_NS_URI);
    }

    private EObject loadJson(String json) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://arrays.json"),
                metadataService,
                ConfigurationResolver.defaults(),
                null);

        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, arrayHolderClass);

        try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            resource.load(is, options);
        }

        if (!resource.getErrors().isEmpty()) {
            fail("Deserialization errors: " + resource.getErrors());
        }

        assertEquals(1, resource.getContents().size(), "Should have exactly one root object");
        return resource.getContents().get(0);
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
            String json = """
                {
                    "doubleArray": [1.1, 2.2, 3.3, 4.4, 5.5]
                }
                """;

            EObject holder = loadJson(json);
            double[] result = (double[]) holder.eGet(doubleArrayAttr);

            assertNotNull(result);
            assertEquals(5, result.length);
            assertArrayEquals(new double[]{1.1, 2.2, 3.3, 4.4, 5.5}, result, 0.0001);
        }

        @Test
        @DisplayName("double[] - empty array")
        void doubleArrayEmpty() throws IOException {
            String json = """
                {
                    "doubleArray": []
                }
                """;

            EObject holder = loadJson(json);
            double[] result = (double[]) holder.eGet(doubleArrayAttr);

            assertNotNull(result);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("double[] - single element")
        void doubleArraySingleElement() throws IOException {
            String json = """
                {
                    "doubleArray": [42.5]
                }
                """;

            EObject holder = loadJson(json);
            double[] result = (double[]) holder.eGet(doubleArrayAttr);

            assertNotNull(result);
            assertEquals(1, result.length);
            assertEquals(42.5, result[0], 0.0001);
        }

        @Test
        @DisplayName("double[] - integers as doubles")
        void doubleArrayWithIntegers() throws IOException {
            String json = """
                {
                    "doubleArray": [1, 2, 3, 4, 5]
                }
                """;

            EObject holder = loadJson(json);
            double[] result = (double[]) holder.eGet(doubleArrayAttr);

            assertNotNull(result);
            assertArrayEquals(new double[]{1.0, 2.0, 3.0, 4.0, 5.0}, result, 0.0001);
        }

        @Test
        @DisplayName("double[] - negative values")
        void doubleArrayNegative() throws IOException {
            String json = """
                {
                    "doubleArray": [-1.5, -2.5, 0.0, 2.5, 1.5]
                }
                """;

            EObject holder = loadJson(json);
            double[] result = (double[]) holder.eGet(doubleArrayAttr);

            assertNotNull(result);
            assertArrayEquals(new double[]{-1.5, -2.5, 0.0, 2.5, 1.5}, result, 0.0001);
        }

        @Test
        @DisplayName("int[] - simple array")
        void intArray() throws IOException {
            String json = """
                {
                    "intArray": [1, 2, 3, 4, 5]
                }
                """;

            EObject holder = loadJson(json);
            int[] result = (int[]) holder.eGet(intArrayAttr);

            assertNotNull(result);
            assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result);
        }

        @Test
        @DisplayName("long[] - simple array")
        void longArray() throws IOException {
            String json = """
                {
                    "longArray": [1000000000000, 2000000000000, 3000000000000]
                }
                """;

            EObject holder = loadJson(json);
            long[] result = (long[]) holder.eGet(longArrayAttr);

            assertNotNull(result);
            assertArrayEquals(new long[]{1000000000000L, 2000000000000L, 3000000000000L}, result);
        }

        @Test
        @DisplayName("float[] - simple array")
        void floatArray() throws IOException {
            String json = """
                {
                    "floatArray": [1.1, 2.2, 3.3]
                }
                """;

            EObject holder = loadJson(json);
            float[] result = (float[]) holder.eGet(floatArrayAttr);

            assertNotNull(result);
            assertEquals(3, result.length);
            assertEquals(1.1f, result[0], 0.01f);
            assertEquals(2.2f, result[1], 0.01f);
            assertEquals(3.3f, result[2], 0.01f);
        }

        @Test
        @DisplayName("boolean[] - simple array")
        void booleanArray() throws IOException {
            String json = """
                {
                    "booleanArray": [true, false, true, true, false]
                }
                """;

            EObject holder = loadJson(json);
            boolean[] result = (boolean[]) holder.eGet(booleanArrayAttr);

            assertNotNull(result);
            assertEquals(5, result.length);
            assertEquals(true, result[0]);
            assertEquals(false, result[1]);
            assertEquals(true, result[2]);
            assertEquals(true, result[3]);
            assertEquals(false, result[4]);
        }

        @Test
        @DisplayName("String[] - simple array")
        void stringArray() throws IOException {
            String json = """
                {
                    "stringArray": ["hello", "world", "test"]
                }
                """;

            EObject holder = loadJson(json);
            String[] result = (String[]) holder.eGet(stringArrayAttr);

            assertNotNull(result);
            assertArrayEquals(new String[]{"hello", "world", "test"}, result);
        }

        @Test
        @DisplayName("String[] - empty strings")
        void stringArrayWithEmptyStrings() throws IOException {
            String json = """
                {
                    "stringArray": ["", "hello", "", "world", ""]
                }
                """;

            EObject holder = loadJson(json);
            String[] result = (String[]) holder.eGet(stringArrayAttr);

            assertNotNull(result);
            assertArrayEquals(new String[]{"", "hello", "", "world", ""}, result);
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
            String json = """
                {
                    "doubleArray2D": [
                        [1.0, 2.0],
                        [3.0, 4.0],
                        [5.0, 6.0]
                    ]
                }
                """;

            EObject holder = loadJson(json);
            double[][] result = (double[][]) holder.eGet(doubleArray2DAttr);

            assertNotNull(result);
            assertEquals(3, result.length);
            assertArrayEquals(new double[]{1.0, 2.0}, result[0], 0.0001);
            assertArrayEquals(new double[]{3.0, 4.0}, result[1], 0.0001);
            assertArrayEquals(new double[]{5.0, 6.0}, result[2], 0.0001);
        }

        @Test
        @DisplayName("double[][] - GeoJSON LineString style")
        void doubleArray2DLineString() throws IOException {
            // LineString coordinates: [[lng, lat], [lng, lat], ...]
            String json = """
                {
                    "doubleArray2D": [
                        [8.6821, 50.1109],
                        [8.6831, 50.1115],
                        [8.6845, 50.1120]
                    ]
                }
                """;

            EObject holder = loadJson(json);
            double[][] result = (double[][]) holder.eGet(doubleArray2DAttr);

            assertNotNull(result);
            assertEquals(3, result.length);
            assertEquals(8.6821, result[0][0], 0.0001);
            assertEquals(50.1109, result[0][1], 0.0001);
        }

        @Test
        @DisplayName("double[][] - empty outer array")
        void doubleArray2DEmpty() throws IOException {
            String json = """
                {
                    "doubleArray2D": []
                }
                """;

            EObject holder = loadJson(json);
            double[][] result = (double[][]) holder.eGet(doubleArray2DAttr);

            assertNotNull(result);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("double[][] - jagged array (different lengths)")
        void doubleArray2DJagged() throws IOException {
            String json = """
                {
                    "doubleArray2D": [
                        [1.0],
                        [2.0, 3.0],
                        [4.0, 5.0, 6.0]
                    ]
                }
                """;

            EObject holder = loadJson(json);
            double[][] result = (double[][]) holder.eGet(doubleArray2DAttr);

            assertNotNull(result);
            assertEquals(3, result.length);
            assertEquals(1, result[0].length);
            assertEquals(2, result[1].length);
            assertEquals(3, result[2].length);
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
            String json = """
                {
                    "doubleArray3D": [
                        [
                            [1.0, 2.0],
                            [3.0, 4.0]
                        ],
                        [
                            [5.0, 6.0],
                            [7.0, 8.0]
                        ]
                    ]
                }
                """;

            EObject holder = loadJson(json);
            double[][][] result = (double[][][]) holder.eGet(doubleArray3DAttr);

            assertNotNull(result);
            assertEquals(2, result.length);
            assertEquals(2, result[0].length);
            assertEquals(2, result[0][0].length);
            assertEquals(1.0, result[0][0][0], 0.0001);
            assertEquals(8.0, result[1][1][1], 0.0001);
        }

        @Test
        @DisplayName("double[][][] - GeoJSON Polygon style")
        void doubleArray3DPolygon() throws IOException {
            // Polygon coordinates: [[[lng, lat], ...], [[lng, lat], ...]]
            // First array is exterior ring, subsequent arrays are holes
            String json = """
                {
                    "doubleArray3D": [
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

            EObject holder = loadJson(json);
            double[][][] result = (double[][][]) holder.eGet(doubleArray3DAttr);

            assertNotNull(result);
            assertEquals(1, result.length);  // 1 ring (exterior)
            assertEquals(5, result[0].length);  // 5 coordinates
            assertEquals(2, result[0][0].length);  // [lng, lat]

            // First coordinate
            assertEquals(0.0, result[0][0][0], 0.0001);
            assertEquals(0.0, result[0][0][1], 0.0001);

            // Last coordinate (same as first - closed ring)
            assertEquals(0.0, result[0][4][0], 0.0001);
            assertEquals(0.0, result[0][4][1], 0.0001);
        }

        @Test
        @DisplayName("double[][][] - GeoJSON Polygon with hole")
        void doubleArray3DPolygonWithHole() throws IOException {
            String json = """
                {
                    "doubleArray3D": [
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

            EObject holder = loadJson(json);
            double[][][] result = (double[][][]) holder.eGet(doubleArray3DAttr);

            assertNotNull(result);
            assertEquals(2, result.length);  // exterior + 1 hole
            assertEquals(5, result[0].length);  // exterior ring
            assertEquals(5, result[1].length);  // hole

            // Hole first coordinate
            assertEquals(5.0, result[1][0][0], 0.0001);
            assertEquals(5.0, result[1][0][1], 0.0001);
        }

        @Test
        @DisplayName("double[][][] - empty")
        void doubleArray3DEmpty() throws IOException {
            String json = """
                {
                    "doubleArray3D": []
                }
                """;

            EObject holder = loadJson(json);
            double[][][] result = (double[][][]) holder.eGet(doubleArray3DAttr);

            assertNotNull(result);
            assertEquals(0, result.length);
        }
    }

    // ========================================================================
    // Object Array Tests (Date[], BigDecimal[], etc.)
    // ========================================================================

    @Nested
    @DisplayName("Object Arrays")
    class ObjectArrayTests {

        @Test
        @DisplayName("Date[] - ISO date strings")
        void dateArray() throws IOException {
            String json = """
                {
                    "dateArray": ["2025-01-14", "2024-12-25", "2023-06-15"]
                }
                """;

            EObject holder = loadJson(json);
            Date[] result = (Date[]) holder.eGet(dateArrayAttr);

            assertNotNull(result);
            assertEquals(3, result.length);
            assertNotNull(result[0]);
            assertNotNull(result[1]);
            assertNotNull(result[2]);
        }

        @Test
        @DisplayName("BigDecimal[] - decimal numbers as strings")
        void bigDecimalArray() throws IOException {
            String json = """
                {
                    "bigDecimalArray": ["123.456", "789.012", "0.001"]
                }
                """;

            EObject holder = loadJson(json);
            BigDecimal[] result = (BigDecimal[]) holder.eGet(bigDecimalArrayAttr);

            assertNotNull(result);
            assertEquals(3, result.length);
            assertEquals(new BigDecimal("123.456"), result[0]);
            assertEquals(new BigDecimal("789.012"), result[1]);
            assertEquals(new BigDecimal("0.001"), result[2]);
        }

        @Test
        @DisplayName("BigDecimal[] - numbers (not strings)")
        void bigDecimalArrayFromNumbers() throws IOException {
            String json = """
                {
                    "bigDecimalArray": [123.456, 789.012, 0.001]
                }
                """;

            EObject holder = loadJson(json);
            BigDecimal[] result = (BigDecimal[]) holder.eGet(bigDecimalArrayAttr);

            assertNotNull(result);
            assertEquals(3, result.length);
            // Note: floating point representation may differ slightly
            assertNotNull(result[0]);
            assertNotNull(result[1]);
            assertNotNull(result[2]);
        }

        @Test
        @DisplayName("BigDecimal[] - empty array")
        void bigDecimalArrayEmpty() throws IOException {
            String json = """
                {
                    "bigDecimalArray": []
                }
                """;

            EObject holder = loadJson(json);
            BigDecimal[] result = (BigDecimal[]) holder.eGet(bigDecimalArrayAttr);

            assertNotNull(result);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("BigDecimal[] - large precision numbers")
        void bigDecimalArrayLargePrecision() throws IOException {
            String json = """
                {
                    "bigDecimalArray": ["12345678901234567890.12345678901234567890", "-0.00000000000000001"]
                }
                """;

            EObject holder = loadJson(json);
            BigDecimal[] result = (BigDecimal[]) holder.eGet(bigDecimalArrayAttr);

            assertNotNull(result);
            assertEquals(2, result.length);
            assertEquals(new BigDecimal("12345678901234567890.12345678901234567890"), result[0]);
            assertEquals(new BigDecimal("-0.00000000000000001"), result[1]);
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
            String json = """
                {
                    "doubleArray": [1.1, 2.2, 3.3],
                    "intArray": [1, 2, 3],
                    "stringArray": ["a", "b", "c"],
                    "booleanArray": [true, false]
                }
                """;

            EObject holder = loadJson(json);

            double[] doubles = (double[]) holder.eGet(doubleArrayAttr);
            int[] ints = (int[]) holder.eGet(intArrayAttr);
            String[] strings = (String[]) holder.eGet(stringArrayAttr);
            boolean[] booleans = (boolean[]) holder.eGet(booleanArrayAttr);

            assertArrayEquals(new double[]{1.1, 2.2, 3.3}, doubles, 0.0001);
            assertArrayEquals(new int[]{1, 2, 3}, ints);
            assertArrayEquals(new String[]{"a", "b", "c"}, strings);
            assertEquals(true, booleans[0]);
            assertEquals(false, booleans[1]);
        }
    }
}
