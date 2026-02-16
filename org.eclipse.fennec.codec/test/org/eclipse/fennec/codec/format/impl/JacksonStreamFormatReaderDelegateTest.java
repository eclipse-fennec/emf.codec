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
package org.eclipse.fennec.codec.format.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.eclipse.fennec.codec.format.TokenType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.json.JsonFactory;

/**
 * Tests for {@link JacksonStreamFormatReaderDelegate}.
 * <p>
 * These tests parse actual JSON through the delegate and verify
 * that token types and values are correctly mapped.
 */
@DisplayName("JacksonStreamFormatReaderDelegate")
class JacksonStreamFormatReaderDelegateTest {

    private final JsonFactory factory = new JsonFactory();

    private JacksonStreamFormatReaderDelegate createDelegate(String json) {
        InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        JsonParser parser = factory.createParser(ObjectReadContext.empty(), in);
        JacksonStreamFormatReaderDelegate delegate = new JacksonStreamFormatReaderDelegate(parser);
        delegate.setSource(in);
        return delegate;
    }

    @Nested
    @DisplayName("metadata")
    class MetadataTests {

        @Test
        @DisplayName("source is set and returned")
        void source() {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{}");
            assertNotNull(delegate.getSource());
        }

        @Test
        @DisplayName("parser is accessible")
        void parser() {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{}");
            assertNotNull(delegate.getParser());
        }
    }

    @Nested
    @DisplayName("structural tokens")
    class StructuralTests {

        @Test
        @DisplayName("reads empty object")
        void emptyObject() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{}");
            assertEquals(TokenType.START_OBJECT, delegate.nextToken());
            assertEquals(TokenType.END_OBJECT, delegate.nextToken());
            assertNull(delegate.nextToken());
            delegate.close();
        }

        @Test
        @DisplayName("reads empty array")
        void emptyArray() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("[]");
            assertEquals(TokenType.START_ARRAY, delegate.nextToken());
            assertEquals(TokenType.END_ARRAY, delegate.nextToken());
            delegate.close();
        }

        @Test
        @DisplayName("reads nested structure")
        void nested() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"a\":[1]}");
            assertEquals(TokenType.START_OBJECT, delegate.nextToken());
            assertEquals(TokenType.FIELD_NAME, delegate.nextToken());
            assertEquals("a", delegate.currentName());
            assertEquals(TokenType.START_ARRAY, delegate.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_INT, delegate.nextToken());
            assertEquals(TokenType.END_ARRAY, delegate.nextToken());
            assertEquals(TokenType.END_OBJECT, delegate.nextToken());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("field names")
    class FieldNameTests {

        @Test
        @DisplayName("reads field name")
        void fieldName() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"myField\":1}");
            delegate.nextToken(); // START_OBJECT
            assertEquals(TokenType.FIELD_NAME, delegate.nextToken());
            assertEquals("myField", delegate.currentName());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("string values")
    class StringTests {

        @Test
        @DisplayName("reads string value")
        void string() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":\"hello\"}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_STRING, delegate.nextToken());
            assertEquals("hello", delegate.readString());
            delegate.close();
        }

        @Test
        @DisplayName("reads empty string")
        void emptyString() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":\"\"}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_STRING, delegate.nextToken());
            assertEquals("", delegate.readString());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("numeric values")
    class NumericTests {

        @Test
        @DisplayName("reads int value")
        void intValue() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":42}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_NUMBER_INT, delegate.nextToken());
            assertEquals(42, delegate.readInt());
            delegate.close();
        }

        @Test
        @DisplayName("reads long value")
        void longValue() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":9999999999}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_NUMBER_INT, delegate.nextToken());
            assertEquals(9999999999L, delegate.readLong());
            delegate.close();
        }

        @Test
        @DisplayName("reads float value")
        void floatValue() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":1.5}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, delegate.nextToken());
            assertEquals(1.5f, delegate.readFloat());
            delegate.close();
        }

        @Test
        @DisplayName("reads double value")
        void doubleValue() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":3.14}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, delegate.nextToken());
            assertEquals(3.14, delegate.readDouble());
            delegate.close();
        }

        @Test
        @DisplayName("reads BigInteger value")
        void bigInteger() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":99999999999999999999}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_NUMBER_INT, delegate.nextToken());
            assertEquals(new BigInteger("99999999999999999999"), delegate.readBigInteger());
            delegate.close();
        }

        @Test
        @DisplayName("reads BigDecimal value")
        void bigDecimal() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":3.14159}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, delegate.nextToken());
            assertEquals(new BigDecimal("3.14159"), delegate.readBigDecimal());
            delegate.close();
        }

        @Test
        @DisplayName("reads negative int")
        void negativeInt() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":-7}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_NUMBER_INT, delegate.nextToken());
            assertEquals(-7, delegate.readInt());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("boolean values")
    class BooleanTests {

        @Test
        @DisplayName("reads true")
        void boolTrue() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":true}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_BOOLEAN, delegate.nextToken());
            assertTrue(delegate.readBoolean());
            delegate.close();
        }

        @Test
        @DisplayName("reads false")
        void boolFalse() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":false}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_BOOLEAN, delegate.nextToken());
            assertFalse(delegate.readBoolean());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("null value")
    class NullTests {

        @Test
        @DisplayName("reads null")
        void nullValue() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":null}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            assertEquals(TokenType.VALUE_NULL, delegate.nextToken());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("binary values")
    class BinaryTests {

        @Test
        @DisplayName("reads base64-encoded binary")
        void binary() throws IOException {
            byte[] data = {1, 2, 3};
            String b64 = Base64.getEncoder().encodeToString(data);
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":\"" + b64 + "\"}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME
            delegate.nextToken(); // VALUE_STRING
            assertArrayEquals(data, delegate.readBinary());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("currentToken")
    class CurrentTokenTests {

        @Test
        @DisplayName("currentToken returns current state")
        void currentToken() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate("{\"k\":1}");
            assertNull(delegate.currentToken());
            delegate.nextToken();
            assertEquals(TokenType.START_OBJECT, delegate.currentToken());
            delegate.nextToken();
            assertEquals(TokenType.FIELD_NAME, delegate.currentToken());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("skipChildren")
    class SkipChildrenTests {

        @Test
        @DisplayName("skips nested object")
        void skipObject() throws IOException {
            JacksonStreamFormatReaderDelegate delegate = createDelegate(
                    "{\"a\":{\"nested\":true},\"b\":2}");
            delegate.nextToken(); // START_OBJECT
            delegate.nextToken(); // FIELD_NAME "a"
            delegate.nextToken(); // START_OBJECT (nested)
            delegate.skipChildren(); // skip nested content
            assertEquals(TokenType.END_OBJECT, delegate.currentToken());
            assertEquals(TokenType.FIELD_NAME, delegate.nextToken());
            assertEquals("b", delegate.currentName());
            delegate.close();
        }
    }

    @Nested
    @DisplayName("round-trip with JacksonStreamFormatDelegate")
    class RoundTripTests {

        @Test
        @DisplayName("write then read produces same values")
        void roundTrip() throws IOException {
            // Write
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            tools.jackson.core.JsonGenerator gen = factory.createGenerator(
                    tools.jackson.core.ObjectWriteContext.empty(), out,
                    tools.jackson.core.JsonEncoding.UTF8);
            JacksonStreamFormatDelegate writer = new JacksonStreamFormatDelegate(gen);

            writer.writeStartObject();
            writer.writeName("name");
            writer.writeString("test");
            writer.writeName("count");
            writer.writeInt(42);
            writer.writeName("active");
            writer.writeBoolean(true);
            writer.writeEndObject();
            writer.close();

            // Read
            JacksonStreamFormatReaderDelegate reader = createDelegate(
                    out.toString(StandardCharsets.UTF_8));

            assertEquals(TokenType.START_OBJECT, reader.nextToken());

            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("name", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("test", reader.readString());

            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("count", reader.currentName());
            assertEquals(TokenType.VALUE_NUMBER_INT, reader.nextToken());
            assertEquals(42, reader.readInt());

            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("active", reader.currentName());
            assertEquals(TokenType.VALUE_BOOLEAN, reader.nextToken());
            assertTrue(reader.readBoolean());

            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }
    }
}
