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
package org.eclipse.fennec.codec.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.fennec.codec.format.impl.FormatDelegateGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;

/**
 * Tests for {@link FormatDelegateGenerator}.
 */
@DisplayName("FormatDelegateGenerator")
class FormatDelegateGeneratorTest {

    @SuppressWarnings("unchecked")
    private final FormatDelegate<Object> delegate = mock(FormatDelegate.class);
    private FormatDelegateGenerator<Object> generator;

    @BeforeEach
    void setUp() {
        IOContext ioContext = new IOContext(
                StreamReadConstraints.defaults(),
                StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                new BufferRecycler(), ContentReference.unknown(), false,
                JsonEncoding.UTF8);
        generator = FormatDelegateGenerator.create(
                ObjectWriteContext.empty(), ioContext, delegate);
    }

    @Nested
    @DisplayName("metadata")
    class MetadataTests {

        @Test
        @DisplayName("returns version")
        void version() {
            assertNotNull(generator.version());
            assertEquals("org.eclipse.fennec", generator.version().getGroupId());
        }

        @Test
        @DisplayName("returns delegate as output target")
        void outputTarget() {
            Object target = new Object();
            when(delegate.getTarget()).thenReturn(target);
            assertSame(target, generator.streamWriteOutputTarget());
        }

        @Test
        @DisplayName("returns delegate via getDelegate()")
        void getDelegate() {
            assertSame(delegate, generator.getDelegate());
        }

        @Test
        @DisplayName("returns write context")
        void writeContext() {
            assertNotNull(generator.streamWriteContext());
        }

        @Test
        @DisplayName("returns write capabilities")
        void writeCapabilities() {
            assertNotNull(generator.streamWriteCapabilities());
        }
    }

    @Nested
    @DisplayName("structural writes")
    class StructuralTests {

        @Test
        @DisplayName("writeStartObject delegates")
        void startObject() throws IOException {
            generator.writeStartObject();
            verify(delegate).writeStartObject();
        }

        @Test
        @DisplayName("writeEndObject delegates")
        void endObject() throws IOException {
            generator.writeStartObject();
            generator.writeEndObject();
            verify(delegate).writeEndObject();
        }

        @Test
        @DisplayName("writeStartArray delegates")
        void startArray() throws IOException {
            generator.writeStartArray();
            verify(delegate).writeStartArray();
        }

        @Test
        @DisplayName("writeEndArray delegates")
        void endArray() throws IOException {
            generator.writeStartArray();
            generator.writeEndArray();
            verify(delegate).writeEndArray();
        }

        @Test
        @DisplayName("writeStartObject with currentValue delegates")
        void startObjectWithValue() throws IOException {
            Object value = "test";
            generator.writeStartObject(value);
            verify(delegate).writeStartObject();
            assertEquals(value, generator.streamWriteContext().currentValue());
        }

        @Test
        @DisplayName("writeStartArray with currentValue delegates")
        void startArrayWithValue() throws IOException {
            Object value = "test";
            generator.writeStartArray(value);
            verify(delegate).writeStartArray();
            assertEquals(value, generator.streamWriteContext().currentValue());
        }
    }

    @Nested
    @DisplayName("field name")
    class FieldNameTests {

        @Test
        @DisplayName("writeName delegates")
        void writeName() throws IOException {
            generator.writeStartObject();
            generator.writeName("field");
            verify(delegate).writeName("field");
        }

        @Test
        @DisplayName("writePropertyId converts to string name")
        void writePropertyId() throws IOException {
            generator.writeStartObject();
            generator.writePropertyId(42L);
            verify(delegate).writeName("42");
        }
    }

    @Nested
    @DisplayName("string writes")
    class StringTests {

        @Test
        @DisplayName("writeString delegates")
        void writeString() throws IOException {
            generator.writeString("hello");
            verify(delegate).writeString("hello");
        }

        @Test
        @DisplayName("writeString(char[]) delegates as string")
        void writeStringCharArray() throws IOException {
            generator.writeString(new char[]{'a', 'b', 'c'}, 0, 3);
            verify(delegate).writeString("abc");
        }

        @Test
        @DisplayName("writeString(char[]) with offset delegates correctly")
        void writeStringCharArrayOffset() throws IOException {
            generator.writeString(new char[]{'a', 'b', 'c', 'd'}, 1, 2);
            verify(delegate).writeString("bc");
        }
    }

    @Nested
    @DisplayName("number writes")
    class NumberTests {

        @Test
        @DisplayName("writeNumber(int) delegates")
        void writeInt() throws IOException {
            generator.writeNumber(42);
            verify(delegate).writeInt(42);
        }

        @Test
        @DisplayName("writeNumber(short) delegates as int")
        void writeShort() throws IOException {
            generator.writeNumber((short) 7);
            verify(delegate).writeInt(7);
        }

        @Test
        @DisplayName("writeNumber(long) delegates")
        void writeLong() throws IOException {
            generator.writeNumber(123456789L);
            verify(delegate).writeLong(123456789L);
        }

        @Test
        @DisplayName("writeNumber(float) delegates")
        void writeFloat() throws IOException {
            generator.writeNumber(1.5f);
            verify(delegate).writeFloat(1.5f);
        }

        @Test
        @DisplayName("writeNumber(double) delegates")
        void writeDouble() throws IOException {
            generator.writeNumber(3.14);
            verify(delegate).writeDouble(3.14);
        }

        @Test
        @DisplayName("writeNumber(BigInteger) delegates")
        void writeBigInteger() throws IOException {
            BigInteger big = new BigInteger("123456789012345678901234567890");
            generator.writeNumber(big);
            verify(delegate).writeBigInteger(big);
        }

        @Test
        @DisplayName("writeNumber(BigDecimal) delegates")
        void writeBigDecimal() throws IOException {
            BigDecimal big = new BigDecimal("3.14159265358979");
            generator.writeNumber(big);
            verify(delegate).writeBigDecimal(big);
        }

        @Test
        @DisplayName("writeNumber(String) delegates as string")
        void writeNumberString() throws IOException {
            generator.writeNumber("42.0");
            verify(delegate).writeString("42.0");
        }
    }

    @Nested
    @DisplayName("boolean / null / binary")
    class ValueTests {

        @Test
        @DisplayName("writeBoolean(true) delegates")
        void writeBooleanTrue() throws IOException {
            generator.writeBoolean(true);
            verify(delegate).writeBoolean(true);
        }

        @Test
        @DisplayName("writeBoolean(false) delegates")
        void writeBooleanFalse() throws IOException {
            generator.writeBoolean(false);
            verify(delegate).writeBoolean(false);
        }

        @Test
        @DisplayName("writeNull delegates")
        void writeNull() throws IOException {
            generator.writeNull();
            verify(delegate).writeNull();
        }

        @Test
        @DisplayName("writeBinary delegates full array")
        void writeBinaryFull() throws IOException {
            byte[] data = {1, 2, 3, 4, 5};
            generator.writeBinary(null, data, 0, data.length);
            verify(delegate).writeBinary(data);
        }

        @Test
        @DisplayName("writeBinary delegates subarray")
        void writeBinarySubarray() throws IOException {
            byte[] data = {1, 2, 3, 4, 5};
            generator.writeBinary(null, data, 1, 3);
            verify(delegate).writeBinary(new byte[]{2, 3, 4});
        }
    }

    @Nested
    @DisplayName("lifecycle")
    class LifecycleTests {

        @Test
        @DisplayName("flush delegates")
        void flush() throws IOException {
            generator.flush();
            verify(delegate).flush();
        }

        @Test
        @DisplayName("close delegates")
        void close() throws IOException {
            generator.close();
            verify(delegate).close();
        }
    }

    @Nested
    @DisplayName("write context tracking")
    class ContextTests {

        @Test
        @DisplayName("context tracks object nesting")
        void objectNesting() throws IOException {
            assertNotNull(generator.streamWriteContext());

            generator.writeStartObject();
            assertNotNull(generator.streamWriteContext());

            generator.writeName("nested");
            generator.writeStartObject();

            generator.writeEndObject();
            generator.writeEndObject();
        }

        @Test
        @DisplayName("currentValue / assignCurrentValue work")
        void currentValue() {
            Object val = "myValue";
            generator.assignCurrentValue(val);
            assertSame(val, generator.currentValue());
        }
    }

    @Nested
    @DisplayName("returns this for chaining")
    class ChainingTests {

        @Test
        @DisplayName("write methods return generator for chaining")
        void chainingWorks() throws IOException {
            JsonGenerator result = generator.writeStartObject();
            assertSame(generator, result);

            result = generator.writeName("key");
            assertSame(generator, result);

            result = generator.writeString("value");
            assertSame(generator, result);

            result = generator.writeEndObject();
            assertSame(generator, result);
        }
    }
}
