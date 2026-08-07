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
package org.eclipse.fennec.codec.format.jackson;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.format.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.json.JsonFactory;

/**
 * Tests for {@link JacksonFormatProvider}.
 */
@DisplayName("JacksonFormatProvider")
class JacksonFormatProviderTest {

    private JacksonFormatProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JacksonFormatProvider("json", new JsonFactory());
    }

    @Nested
    @DisplayName("metadata")
    class MetadataTests {

        @Test
        @DisplayName("returns format ID")
        void formatId() {
            assertEquals("json", provider.getFormatId());
        }

        @Test
        @DisplayName("returns default file extensions")
        void fileExtensions() {
            assertArrayEquals(new String[]{"json"}, provider.getFileExtensions());
        }

        @Test
        @DisplayName("returns empty content types by default")
        void contentTypes() {
            assertEquals(0, provider.getContentTypes().length);
        }

        @Test
        @DisplayName("returns custom file extensions and content types")
        void customMetadata() {
            JacksonFormatProvider custom = new JacksonFormatProvider(
                    "json", new JsonFactory(),
                    new String[]{"json", "geojson"},
                    new String[]{"application/json"});
            assertArrayEquals(new String[]{"json", "geojson"}, custom.getFileExtensions());
            assertArrayEquals(new String[]{"application/json"}, custom.getContentTypes());
        }

        @Test
        @DisplayName("factory is accessible")
        void factory() {
            assertNotNull(provider.getFactory());
        }
    }

    @Nested
    @DisplayName("createWriter")
    class WriterTests {

        @Test
        @DisplayName("creates a working writer delegate")
        void createWriter() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FormatDelegate<java.io.OutputStream> writer = provider.createWriter(out);
            assertNotNull(writer);
            assertEquals(out, writer.getTarget());

            writer.writeStartObject();
            writer.writeName("key");
            writer.writeString("value");
            writer.writeEndObject();
            writer.close();

            assertEquals("{\"key\":\"value\"}", out.toString(StandardCharsets.UTF_8));
        }
    }

    @Nested
    @DisplayName("createReader")
    class ReaderTests {

        @Test
        @DisplayName("creates a working reader delegate")
        void createReader() throws IOException {
            ByteArrayInputStream in = new ByteArrayInputStream(
                    "{\"key\":\"value\"}".getBytes(StandardCharsets.UTF_8));
            FormatReaderDelegate<java.io.InputStream> reader = provider.createReader(in);
            assertNotNull(reader);
            assertEquals(in, reader.getSource());

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("key", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("value", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }
    }

    @Nested
    @DisplayName("round-trip")
    class RoundTripTests {

        @Test
        @DisplayName("write then read produces same values")
        void roundTrip() throws IOException {
            // Write
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FormatDelegate<java.io.OutputStream> writer = provider.createWriter(out);
            writer.writeStartObject();
            writer.writeName("name");
            writer.writeString("Alice");
            writer.writeName("age");
            writer.writeInt(30);
            writer.writeName("active");
            writer.writeBoolean(true);
            writer.writeName("score");
            writer.writeDouble(9.5);
            writer.writeName("tags");
            writer.writeStartArray();
            writer.writeString("a");
            writer.writeString("b");
            writer.writeEndArray();
            writer.writeEndObject();
            writer.close();

            // Read
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            FormatReaderDelegate<java.io.InputStream> reader = provider.createReader(in);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());

            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("name", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("Alice", reader.readString());

            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("age", reader.currentName());
            assertEquals(TokenType.VALUE_NUMBER_INT, reader.nextToken());
            assertEquals(30, reader.readInt());

            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("active", reader.currentName());
            assertEquals(TokenType.VALUE_BOOLEAN, reader.nextToken());
            assertTrue(reader.readBoolean());

            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("score", reader.currentName());
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, reader.nextToken());
            assertEquals(9.5, reader.readDouble());

            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("tags", reader.currentName());
            assertEquals(TokenType.START_ARRAY, reader.nextToken());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("a", reader.readString());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("b", reader.readString());
            assertEquals(TokenType.END_ARRAY, reader.nextToken());

            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("null values round-trip correctly")
        void nullRoundTrip() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FormatDelegate<java.io.OutputStream> writer = provider.createWriter(out);
            writer.writeStartObject();
            writer.writeName("nothing");
            writer.writeNull();
            writer.writeEndObject();
            writer.close();

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            FormatReaderDelegate<java.io.InputStream> reader = provider.createReader(in);
            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("nothing", reader.currentName());
            assertEquals(TokenType.VALUE_NULL, reader.nextToken());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("boolean false round-trips correctly")
        void booleanFalseRoundTrip() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FormatDelegate<java.io.OutputStream> writer = provider.createWriter(out);
            writer.writeStartObject();
            writer.writeName("flag");
            writer.writeBoolean(false);
            writer.writeEndObject();
            writer.close();

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            FormatReaderDelegate<java.io.InputStream> reader = provider.createReader(in);
            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_BOOLEAN, reader.nextToken());
            assertFalse(reader.readBoolean());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }
    }
}
