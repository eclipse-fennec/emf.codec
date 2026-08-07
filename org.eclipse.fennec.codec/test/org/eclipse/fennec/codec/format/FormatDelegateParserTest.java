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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.fennec.codec.format.jackson.FormatDelegateParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;

/**
 * Tests for {@link FormatDelegateParser}.
 */
@DisplayName("FormatDelegateParser")
class FormatDelegateParserTest {

    @SuppressWarnings("unchecked")
    private final FormatReaderDelegate<Object> delegate = mock(FormatReaderDelegate.class);
    private FormatDelegateParser<Object> parser;

    @BeforeEach
    void setUp() {
        IOContext ioContext = new IOContext(
                StreamReadConstraints.defaults(),
                StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                new BufferRecycler(), ContentReference.unknown(), false,
                JsonEncoding.UTF8);
        parser = FormatDelegateParser.create(
                ObjectReadContext.empty(), ioContext, delegate);
    }

    @Nested
    @DisplayName("metadata")
    class MetadataTests {

        @Test
        @DisplayName("returns version")
        void version() {
            assertNotNull(parser.version());
            assertEquals("org.eclipse.fennec", parser.version().getGroupId());
        }

        @Test
        @DisplayName("returns delegate as input source")
        void inputSource() {
            Object source = new Object();
            when(delegate.getSource()).thenReturn(source);
            assertSame(source, parser.streamReadInputSource());
        }

        @Test
        @DisplayName("returns delegate via getDelegate()")
        void getDelegate() {
            assertSame(delegate, parser.getDelegate());
        }

        @Test
        @DisplayName("returns read context")
        void readContext() {
            assertNotNull(parser.streamReadContext());
        }

        @Test
        @DisplayName("returns read capabilities")
        void readCapabilities() {
            assertNotNull(parser.streamReadCapabilities());
        }
    }

    @Nested
    @DisplayName("numeric tokens (issue #129)")
    class NumericTokenTests {

        @Test
        @DisplayName("getString on a float token returns the number, not a delegate string read")
        void floatTokenHasText() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_NUMBER_FLOAT);
            when(delegate.readDouble()).thenReturn(1.5);
            parser.nextToken();

            assertEquals("1.5", parser.getString());
            verify(delegate, never()).readString();
        }

        @Test
        @DisplayName("getString on an int token returns the number")
        void intTokenHasText() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_NUMBER_INT);
            when(delegate.readLong()).thenReturn(42L);
            parser.nextToken();

            assertEquals("42", parser.getString());
            verify(delegate, never()).readString();
        }

        @Test
        @DisplayName("a deferred number is materialized, not left to the textual form")
        void deferredNumberIsEager() throws IOException {
            // Jackson's TokenBuffer takes numbers deferred; leaving that to the textual
            // form is what made buffered decimals come back empty (issue #128)
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_NUMBER_FLOAT);
            when(delegate.readDouble()).thenReturn(2.25);
            parser.nextToken();

            assertEquals(2.25, ((Number) parser.getNumberValueDeferred()).doubleValue(), 0.0001);
        }
    }

    @Nested
    @DisplayName("token navigation")
    class TokenNavigationTests {

        @Test
        @DisplayName("nextToken maps START_OBJECT")
        void startObject() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.START_OBJECT);
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps END_OBJECT")
        void endObject() throws IOException {
            when(delegate.nextToken())
                    .thenReturn(TokenType.START_OBJECT)
                    .thenReturn(TokenType.END_OBJECT);
            parser.nextToken(); // START_OBJECT
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps START_ARRAY")
        void startArray() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.START_ARRAY);
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps END_ARRAY")
        void endArray() throws IOException {
            when(delegate.nextToken())
                    .thenReturn(TokenType.START_ARRAY)
                    .thenReturn(TokenType.END_ARRAY);
            parser.nextToken(); // START_ARRAY
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps FIELD_NAME to PROPERTY_NAME")
        void fieldName() throws IOException {
            when(delegate.nextToken())
                    .thenReturn(TokenType.START_OBJECT)
                    .thenReturn(TokenType.FIELD_NAME);
            when(delegate.currentName()).thenReturn("myField");
            parser.nextToken(); // START_OBJECT
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("myField", parser.currentName());
        }

        @Test
        @DisplayName("nextToken maps VALUE_STRING")
        void valueString() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_STRING);
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps VALUE_NUMBER_INT")
        void valueNumberInt() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_NUMBER_INT);
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps VALUE_NUMBER_FLOAT")
        void valueNumberFloat() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_NUMBER_FLOAT);
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps VALUE_BOOLEAN true to VALUE_TRUE")
        void valueBooleanTrue() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_BOOLEAN);
            when(delegate.readBoolean()).thenReturn(true);
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps VALUE_BOOLEAN false to VALUE_FALSE")
        void valueBooleanFalse() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_BOOLEAN);
            when(delegate.readBoolean()).thenReturn(false);
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps VALUE_NULL")
        void valueNull() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_NULL);
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken maps VALUE_BINARY to VALUE_EMBEDDED_OBJECT")
        void valueBinary() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_BINARY);
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
        }

        @Test
        @DisplayName("nextToken returns null for null delegate result")
        void nullToken() throws IOException {
            when(delegate.nextToken()).thenReturn(null);
            assertNull(parser.nextToken());
        }
    }

    @Nested
    @DisplayName("string access")
    class StringTests {

        @Test
        @DisplayName("getString reads from delegate for VALUE_STRING")
        void getStringValue() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_STRING);
            when(delegate.readString()).thenReturn("hello");
            parser.nextToken();
            assertEquals("hello", parser.getString());
        }

        @Test
        @DisplayName("getString returns field name for PROPERTY_NAME")
        void getStringFieldName() throws IOException {
            when(delegate.nextToken())
                    .thenReturn(TokenType.START_OBJECT)
                    .thenReturn(TokenType.FIELD_NAME);
            when(delegate.currentName()).thenReturn("myField");
            parser.nextToken();
            parser.nextToken();
            assertEquals("myField", parser.getString());
        }

        @Test
        @DisplayName("getString returns structural markers")
        void getStringStructural() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.START_OBJECT);
            parser.nextToken();
            assertEquals("{", parser.getString());
        }

        @Test
        @DisplayName("getString returns boolean strings")
        void getStringBoolean() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_BOOLEAN);
            when(delegate.readBoolean()).thenReturn(true);
            parser.nextToken();
            assertEquals("true", parser.getString());
        }
    }

    @Nested
    @DisplayName("numeric access")
    class NumericTests {

        @Test
        @DisplayName("getIntValue delegates")
        void getInt() throws IOException {
            when(delegate.readInt()).thenReturn(42);
            assertEquals(42, parser.getIntValue());
        }

        @Test
        @DisplayName("getLongValue delegates")
        void getLong() throws IOException {
            when(delegate.readLong()).thenReturn(123456789L);
            assertEquals(123456789L, parser.getLongValue());
        }

        @Test
        @DisplayName("getFloatValue delegates")
        void getFloat() throws IOException {
            when(delegate.readFloat()).thenReturn(1.5f);
            assertEquals(1.5f, parser.getFloatValue());
        }

        @Test
        @DisplayName("getDoubleValue delegates")
        void getDouble() throws IOException {
            when(delegate.readDouble()).thenReturn(3.14);
            assertEquals(3.14, parser.getDoubleValue());
        }

        @Test
        @DisplayName("getBigIntegerValue delegates")
        void getBigInteger() throws IOException {
            BigInteger big = new BigInteger("99999999999999999999");
            when(delegate.readBigInteger()).thenReturn(big);
            assertEquals(big, parser.getBigIntegerValue());
        }

        @Test
        @DisplayName("getDecimalValue delegates")
        void getDecimal() throws IOException {
            BigDecimal big = new BigDecimal("3.14159");
            when(delegate.readBigDecimal()).thenReturn(big);
            assertEquals(big, parser.getDecimalValue());
        }

        @Test
        @DisplayName("getNumberValue returns long for int token")
        void getNumberValueInt() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_NUMBER_INT);
            when(delegate.readLong()).thenReturn(42L);
            parser.nextToken();
            assertEquals(42L, parser.getNumberValue());
        }

        @Test
        @DisplayName("getNumberValue returns double for float token")
        void getNumberValueFloat() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_NUMBER_FLOAT);
            when(delegate.readDouble()).thenReturn(3.14);
            parser.nextToken();
            assertEquals(3.14, parser.getNumberValue());
        }
    }

    @Nested
    @DisplayName("boolean access")
    class BooleanTests {

        @Test
        @DisplayName("getBooleanValue returns true for VALUE_TRUE")
        void booleanTrue() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_BOOLEAN);
            when(delegate.readBoolean()).thenReturn(true);
            parser.nextToken();
            assertTrue(parser.getBooleanValue());
        }

        @Test
        @DisplayName("getBooleanValue returns false for VALUE_FALSE")
        void booleanFalse() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_BOOLEAN);
            when(delegate.readBoolean()).thenReturn(false);
            parser.nextToken();
            assertFalse(parser.getBooleanValue());
        }

        @Test
        @DisplayName("getBooleanValue throws for non-boolean token")
        void booleanNonBoolean() throws IOException {
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_STRING);
            parser.nextToken();
            assertThrows(Exception.class, () -> parser.getBooleanValue());
        }
    }

    @Nested
    @DisplayName("binary access")
    class BinaryTests {

        @Test
        @DisplayName("getBinaryValue delegates")
        void getBinary() throws IOException {
            byte[] data = {1, 2, 3};
            when(delegate.readBinary()).thenReturn(data);
            assertArrayEquals(data, parser.getBinaryValue(null));
        }

        @Test
        @DisplayName("getEmbeddedObject returns binary for EMBEDDED_OBJECT token")
        void getEmbeddedObject() throws IOException {
            byte[] data = {4, 5, 6};
            when(delegate.nextToken()).thenReturn(TokenType.VALUE_BINARY);
            when(delegate.readBinary()).thenReturn(data);
            parser.nextToken();
            assertArrayEquals(data, (byte[]) parser.getEmbeddedObject());
        }
    }

    @Nested
    @DisplayName("lifecycle")
    class LifecycleTests {

        @Test
        @DisplayName("close delegates")
        void close() throws IOException {
            parser.close();
            verify(delegate).close();
        }
    }

    @Nested
    @DisplayName("context tracking")
    class ContextTests {

        @Test
        @DisplayName("context tracks object nesting")
        void objectNesting() throws IOException {
            when(delegate.nextToken())
                    .thenReturn(TokenType.START_OBJECT)
                    .thenReturn(TokenType.FIELD_NAME)
                    .thenReturn(TokenType.VALUE_STRING)
                    .thenReturn(TokenType.END_OBJECT);
            when(delegate.currentName()).thenReturn("key");

            parser.nextToken(); // START_OBJECT
            assertTrue(parser.streamReadContext().inObject());

            parser.nextToken(); // FIELD_NAME
            assertEquals("key", parser.currentName());

            parser.nextToken(); // VALUE_STRING
            parser.nextToken(); // END_OBJECT
        }

        @Test
        @DisplayName("context tracks array nesting")
        void arrayNesting() throws IOException {
            when(delegate.nextToken())
                    .thenReturn(TokenType.START_ARRAY)
                    .thenReturn(TokenType.VALUE_STRING)
                    .thenReturn(TokenType.END_ARRAY);

            parser.nextToken(); // START_ARRAY
            assertTrue(parser.streamReadContext().inArray());

            parser.nextToken(); // VALUE_STRING
            parser.nextToken(); // END_ARRAY
        }
    }
}
