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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Round trips for array component types the writer emits (issue #117).
 * <p>
 * Spec 11-feature.md §7.1 listed a smaller set than the writer actually produces:
 * {@code byte[]} and {@code short[]} have their own write branch, and boxed arrays go
 * through the object branch. All of them came back as zero-length arrays, because the
 * reader's component dispatch knew only {@code double}, {@code int}, {@code long},
 * {@code float}, {@code boolean} and {@code String}, and the reflection allowlist behind it
 * rejects the rest.
 * </p>
 * <p>
 * What the writer emits has to be readable — the written output is the contract.
 * </p>
 */
@DisplayName("Array component types round trip")
class ArrayComponentTypeRoundTripTest {

    private static final String NS_URI = "http://test.example.org/arraytypes/1.0";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass holderClass;

    private EAttribute byteArray;
    private EAttribute shortArray;
    private EAttribute boxedIntArray;
    private EAttribute boxedDoubleArray;
    private EAttribute boxedBooleanArray;
    private EAttribute stringArray;
    private EAttribute bigDecimalArray;
    private EAttribute bigIntegerArray;
    private EAttribute dateArray;
    private EAttribute instantArray;
    private EAttribute localDateArray;
    private EAttribute charArray;
    private EAttribute boxedCharArray;
    private EAttribute singleDate;

    @BeforeEach
    void setUp() {
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("arraytypes");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("at");

        holderClass = EcoreFactory.eINSTANCE.createEClass();
        holderClass.setName("ArrayHolder");
        testPackage.getEClassifiers().add(holderClass);

        byteArray = arrayAttribute("byteArray", "ByteArray", byte[].class);
        shortArray = arrayAttribute("shortArray", "ShortArray", short[].class);
        boxedIntArray = arrayAttribute("boxedIntArray", "BoxedIntArray", Integer[].class);
        boxedDoubleArray = arrayAttribute("boxedDoubleArray", "BoxedDoubleArray", Double[].class);
        boxedBooleanArray = arrayAttribute("boxedBooleanArray", "BoxedBooleanArray", Boolean[].class);
        stringArray = arrayAttribute("stringArray", "StringArray", String[].class);
        bigDecimalArray = arrayAttribute("bigDecimalArray", "BigDecimalArray", java.math.BigDecimal[].class);
        bigIntegerArray = arrayAttribute("bigIntegerArray", "BigIntegerArray", java.math.BigInteger[].class);
        dateArray = arrayAttribute("dateArray", "DateArray", java.util.Date[].class);
        instantArray = arrayAttribute("instantArray", "InstantArray", java.time.Instant[].class);
        localDateArray = arrayAttribute("localDateArray", "LocalDateArray", java.time.LocalDate[].class);
        charArray = arrayAttribute("charArray", "CharArray", char[].class);

        singleDate = EcoreFactory.eINSTANCE.createEAttribute();
        singleDate.setName("singleDate");
        singleDate.setEType(org.eclipse.emf.ecore.EcorePackage.Literals.EDATE);
        holderClass.getEStructuralFeatures().add(singleDate);
        boxedCharArray = arrayAttribute("boxedCharArray", "BoxedCharArray", Character[].class);

        // Give the package a resource, otherwise the written type URI has no schema part
        // and the reader can only fall back to the hint
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
    @DisplayName("byte[] survives the round trip")
    void byteArrayRoundTrips() throws IOException {
        EObject holder = holder();
        holder.eSet(byteArray, new byte[] {1, 2, 3});

        EObject loaded = roundTrip(holder);
        assertArrayEquals(new byte[] {1, 2, 3}, (byte[]) loaded.eGet(byteArray));
    }

    @Test
    @DisplayName("short[] survives the round trip")
    void shortArrayRoundTrips() throws IOException {
        EObject holder = holder();
        holder.eSet(shortArray, new short[] {10, 20, 30});

        EObject loaded = roundTrip(holder);
        assertArrayEquals(new short[] {10, 20, 30}, (short[]) loaded.eGet(shortArray));
    }

    @Test
    @DisplayName("boxed number arrays survive the round trip")
    void boxedNumberArraysRoundTrip() throws IOException {
        EObject holder = holder();
        holder.eSet(boxedIntArray, new Integer[] {1, 2, 3});
        holder.eSet(boxedDoubleArray, new Double[] {1.5, 2.5});

        EObject loaded = roundTrip(holder);
        assertArrayEquals(new Integer[] {1, 2, 3}, (Integer[]) loaded.eGet(boxedIntArray));
        assertArrayEquals(new Double[] {1.5, 2.5}, (Double[]) loaded.eGet(boxedDoubleArray));
    }

    @Test
    @DisplayName("Boolean[] survives the round trip")
    void boxedBooleanArrayRoundTrips() throws IOException {
        EObject holder = holder();
        holder.eSet(boxedBooleanArray, new Boolean[] {true, false, true});

        EObject loaded = roundTrip(holder);
        assertArrayEquals(new Boolean[] {true, false, true},
                (Boolean[]) loaded.eGet(boxedBooleanArray));
    }

    @Test
    @DisplayName("an empty array stays empty rather than turning into null")
    void emptyArrayStaysEmpty() throws IOException {
        EObject holder = holder();
        holder.eSet(byteArray, new byte[0]);

        EObject loaded = roundTrip(holder);
        assertEquals(0, ((byte[]) loaded.eGet(byteArray)).length);
    }

    @Test
    @DisplayName("char[] survives the round trip")
    void charArrayRoundTrips() throws IOException {
        EObject holder = holder();
        holder.eSet(charArray, new char[] {'a', 'b'});

        EObject loaded = roundTrip(holder);
        assertArrayEquals(new char[] {'a', 'b'}, (char[]) loaded.eGet(charArray));
    }

    @Test
    @DisplayName("Character[] survives the round trip")
    void boxedCharArrayRoundTrips() throws IOException {
        EObject holder = holder();
        holder.eSet(boxedCharArray, new Character[] {'a', 'b'});

        EObject loaded = roundTrip(holder);
        assertArrayEquals(new Character[] {'a', 'b'}, (Character[]) loaded.eGet(boxedCharArray));
    }

    @Test
    @DisplayName("Date[] survives the round trip (issue #118)")
    void dateArrayRoundTrips() throws IOException {
        EObject holder = holder();
        java.util.Date date = new java.util.Date(86400000L);
        holder.eSet(dateArray, new java.util.Date[] {date});

        EObject loaded = roundTrip(holder);
        assertArrayEquals(new java.util.Date[] {date}, (java.util.Date[]) loaded.eGet(dateArray));
    }

    @Test
    @DisplayName("a single EDate survives the round trip in plain JSON (issue #118)")
    void singleDateRoundTrips() throws IOException {
        // Without a configured dateFormat the writer used Date.toString(), which nothing can
        // parse back - the attribute silently stayed unset
        EObject holder = holder();
        java.util.Date date = new java.util.Date(1_700_000_000_000L);
        holder.eSet(singleDate, date);

        EObject loaded = roundTrip(holder);
        assertEquals(date, loaded.eGet(singleDate));
    }

    @Test
    @DisplayName("the types that already worked keep working")
    void previouslyWorkingTypesStillRoundTrip() throws IOException {
        EObject holder = holder();
        holder.eSet(stringArray, new String[] {"a", "b"});
        holder.eSet(bigDecimalArray, new java.math.BigDecimal[] {new java.math.BigDecimal("1.5")});
        holder.eSet(bigIntegerArray, new java.math.BigInteger[] {java.math.BigInteger.TEN});
        holder.eSet(instantArray, new java.time.Instant[] {java.time.Instant.ofEpochMilli(86400000L)});
        holder.eSet(localDateArray, new java.time.LocalDate[] {java.time.LocalDate.of(2026, 1, 2)});

        EObject loaded = roundTrip(holder);
        assertArrayEquals(new String[] {"a", "b"}, (String[]) loaded.eGet(stringArray));
        assertEquals(1, ((java.math.BigDecimal[]) loaded.eGet(bigDecimalArray)).length);
        assertEquals(1, ((java.math.BigInteger[]) loaded.eGet(bigIntegerArray)).length);
        assertEquals(1, ((java.time.Instant[]) loaded.eGet(instantArray)).length);
        assertEquals(java.time.LocalDate.of(2026, 1, 2),
                ((java.time.LocalDate[]) loaded.eGet(localDateArray))[0]);
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EAttribute arrayAttribute(String name, String typeName, Class<?> instanceClass) {
        EDataType dataType = EcoreFactory.eINSTANCE.createEDataType();
        dataType.setName(typeName);
        dataType.setInstanceClass(instanceClass);
        testPackage.getEClassifiers().add(dataType);

        EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
        attribute.setName(name);
        attribute.setEType(dataType);
        holderClass.getEStructuralFeatures().add(attribute);
        return attribute;
    }

    private EObject holder() {
        return testPackage.getEFactoryInstance().create(holderClass);
    }

    private CodecResource resource() {
        return new CodecResource(URI.createURI("test://arraytypes.json"),
                metadataService, ConfigurationResolver.defaults(), null);
    }

    private EObject roundTrip(EObject object) throws IOException {
        CodecResource out = resource();
        out.getContents().add(object);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        out.save(bytes, Collections.emptyMap());
        String json = bytes.toString(StandardCharsets.UTF_8);

        CodecResource in = resource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, holderClass);
        in.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        assertTrue(in.getErrors().isEmpty(), "round trip reported errors: " + in.getErrors());
        assertTrue(in.getWarnings().isEmpty(),
                "round trip reported warnings: " + in.getWarnings() + " json=" + json);
        return in.getContents().get(0);
    }
}
