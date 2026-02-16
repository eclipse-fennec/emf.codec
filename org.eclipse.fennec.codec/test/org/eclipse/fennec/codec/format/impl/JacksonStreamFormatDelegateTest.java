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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.json.JsonFactory;

/**
 * Tests for {@link JacksonStreamFormatDelegate}.
 * <p>
 * These tests write through the delegate and verify the resulting JSON output.
 */
@DisplayName("JacksonStreamFormatDelegate")
class JacksonStreamFormatDelegateTest {

    private final JsonFactory factory = new JsonFactory();
    private ByteArrayOutputStream out;
    private JacksonStreamFormatDelegate delegate;

    @BeforeEach
    void setUp() throws IOException {
        out = new ByteArrayOutputStream();
        JsonGenerator gen = factory.createGenerator(
                ObjectWriteContext.empty(), out, JsonEncoding.UTF8);
        delegate = new JacksonStreamFormatDelegate(gen);
        delegate.setTarget(out);
    }

    private String output() {
        return out.toString(StandardCharsets.UTF_8);
    }

    @Nested
    @DisplayName("metadata")
    class MetadataTests {

        @Test
        @DisplayName("target is set and returned")
        void target() {
            assertEquals(out, delegate.getTarget());
        }

        @Test
        @DisplayName("generator is accessible")
        void generator() {
            assertNotNull(delegate.getGenerator());
        }
    }

    @Nested
    @DisplayName("structural writes")
    class StructuralTests {

        @Test
        @DisplayName("writes empty object")
        void emptyObject() throws IOException {
            delegate.writeStartObject();
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{}", output());
        }

        @Test
        @DisplayName("writes empty array")
        void emptyArray() throws IOException {
            delegate.writeStartArray();
            delegate.writeEndArray();
            delegate.close();
            assertEquals("[]", output());
        }

        @Test
        @DisplayName("writes nested structure")
        void nested() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("items");
            delegate.writeStartArray();
            delegate.writeStartObject();
            delegate.writeName("id");
            delegate.writeInt(1);
            delegate.writeEndObject();
            delegate.writeEndArray();
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"items\":[{\"id\":1}]}", output());
        }
    }

    @Nested
    @DisplayName("value writes")
    class ValueTests {

        @Test
        @DisplayName("writes string value")
        void string() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("name");
            delegate.writeString("hello");
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"name\":\"hello\"}", output());
        }

        @Test
        @DisplayName("writes int value")
        void intValue() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("count");
            delegate.writeInt(42);
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"count\":42}", output());
        }

        @Test
        @DisplayName("writes long value")
        void longValue() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("big");
            delegate.writeLong(9999999999L);
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"big\":9999999999}", output());
        }

        @Test
        @DisplayName("writes float value")
        void floatValue() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("f");
            delegate.writeFloat(1.5f);
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"f\":1.5}", output());
        }

        @Test
        @DisplayName("writes double value")
        void doubleValue() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("d");
            delegate.writeDouble(3.14);
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"d\":3.14}", output());
        }

        @Test
        @DisplayName("writes BigInteger value")
        void bigInteger() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("big");
            delegate.writeBigInteger(new BigInteger("99999999999999999999"));
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"big\":99999999999999999999}", output());
        }

        @Test
        @DisplayName("writes BigDecimal value")
        void bigDecimal() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("bd");
            delegate.writeBigDecimal(new BigDecimal("3.14159"));
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"bd\":3.14159}", output());
        }

        @Test
        @DisplayName("writes boolean true")
        void boolTrue() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("flag");
            delegate.writeBoolean(true);
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"flag\":true}", output());
        }

        @Test
        @DisplayName("writes boolean false")
        void boolFalse() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("flag");
            delegate.writeBoolean(false);
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"flag\":false}", output());
        }

        @Test
        @DisplayName("writes null value")
        void nullValue() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("nothing");
            delegate.writeNull();
            delegate.writeEndObject();
            delegate.close();
            assertEquals("{\"nothing\":null}", output());
        }

        @Test
        @DisplayName("writes binary value as base64")
        void binary() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("data");
            delegate.writeBinary(new byte[]{1, 2, 3});
            delegate.writeEndObject();
            delegate.close();
            // Jackson encodes binary as base64
            assertEquals("{\"data\":\"AQID\"}", output());
        }
    }

    @Nested
    @DisplayName("complete document")
    class CompleteDocumentTests {

        @Test
        @DisplayName("writes a realistic JSON document")
        void realisticDocument() throws IOException {
            delegate.writeStartObject();

            delegate.writeName("name");
            delegate.writeString("John");

            delegate.writeName("age");
            delegate.writeInt(30);

            delegate.writeName("active");
            delegate.writeBoolean(true);

            delegate.writeName("score");
            delegate.writeDouble(9.5);

            delegate.writeName("address");
            delegate.writeNull();

            delegate.writeName("tags");
            delegate.writeStartArray();
            delegate.writeString("a");
            delegate.writeString("b");
            delegate.writeEndArray();

            delegate.writeEndObject();
            delegate.close();

            assertEquals(
                    "{\"name\":\"John\",\"age\":30,\"active\":true,"
                    + "\"score\":9.5,\"address\":null,"
                    + "\"tags\":[\"a\",\"b\"]}",
                    output());
        }
    }

    @Nested
    @DisplayName("lifecycle")
    class LifecycleTests {

        @Test
        @DisplayName("flush writes buffered content")
        void flush() throws IOException {
            delegate.writeStartObject();
            delegate.writeEndObject();
            delegate.flush();
            // After flush, content should be in the output stream
            assertEquals("{}", output());
        }
    }
}
