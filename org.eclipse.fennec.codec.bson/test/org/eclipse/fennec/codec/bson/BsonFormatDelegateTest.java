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
package org.eclipse.fennec.codec.bson;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BsonFormatDelegate}.
 */
@DisplayName("BsonFormatDelegate")
class BsonFormatDelegateTest {

    private BsonDocument document;
    private BsonFormatDelegate delegate;

    @BeforeEach
    void setUp() {
        document = new BsonDocument();
        delegate = new BsonFormatDelegate(document);
    }

    @Nested
    @DisplayName("target management")
    class TargetManagement {

        @Test
        @DisplayName("returns the target document")
        void getTarget() {
            assertSame(document, delegate.getTarget());
        }

        @Test
        @DisplayName("supports native ObjectId")
        void supportsNativeObjectId() {
            assertTrue(delegate.supportsNativeObjectId());
        }

        @Test
        @DisplayName("supports native DateTime")
        void supportsNativeDateTime() {
            assertTrue(delegate.supportsNativeDateTime());
        }
    }

    @Nested
    @DisplayName("scalar values")
    class ScalarValues {

        @Test
        @DisplayName("writes string value")
        void writeString() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("name");
            delegate.writeString("Alice");
            delegate.writeEndObject();

            assertEquals("Alice", document.getString("name").getValue());
        }

        @Test
        @DisplayName("writes native BSON DateTime")
        void writeDateTime() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("born");
            delegate.writeDateTime(1234567890123L);
            delegate.writeEndObject();

            assertEquals(1234567890123L, document.getDateTime("born").getValue());
        }

        @Test
        @DisplayName("writes null string as BsonNull")
        void writeNullString() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("name");
            delegate.writeString(null);
            delegate.writeEndObject();

            assertTrue(document.get("name").isNull());
        }

        @Test
        @DisplayName("writes int value")
        void writeInt() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("count");
            delegate.writeInt(42);
            delegate.writeEndObject();

            assertEquals(42, document.getInt32("count").getValue());
        }

        @Test
        @DisplayName("writes long value")
        void writeLong() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("amount");
            delegate.writeLong(9876543210L);
            delegate.writeEndObject();

            assertEquals(9876543210L, document.getInt64("amount").getValue());
        }

        @Test
        @DisplayName("writes float value as double")
        void writeFloat() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("ratio");
            delegate.writeFloat(3.14f);
            delegate.writeEndObject();

            assertEquals(3.14f, (float) document.getDouble("ratio").getValue(), 0.001f);
        }

        @Test
        @DisplayName("writes double value")
        void writeDouble() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("score");
            delegate.writeDouble(95.5);
            delegate.writeEndObject();

            assertEquals(95.5, document.getDouble("score").getValue());
        }

        @Test
        @DisplayName("writes boolean true")
        void writeBooleanTrue() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("active");
            delegate.writeBoolean(true);
            delegate.writeEndObject();

            assertTrue(document.getBoolean("active").getValue());
        }

        @Test
        @DisplayName("writes boolean false")
        void writeBooleanFalse() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("active");
            delegate.writeBoolean(false);
            delegate.writeEndObject();

            assertEquals(false, document.getBoolean("active").getValue());
        }

        @Test
        @DisplayName("writes null value")
        void writeNull() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("nothing");
            delegate.writeNull();
            delegate.writeEndObject();

            assertTrue(document.get("nothing").isNull());
        }

        @Test
        @DisplayName("writes BigDecimal as Decimal128")
        void writeBigDecimal() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("price");
            delegate.writeBigDecimal(new BigDecimal("19.99"));
            delegate.writeEndObject();

            assertEquals(new Decimal128(new BigDecimal("19.99")),
                    document.getDecimal128("price").getValue());
        }

        @Test
        @DisplayName("writes BigInteger as string")
        void writeBigInteger() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("bignum");
            delegate.writeBigInteger(new BigInteger("12345678901234567890"));
            delegate.writeEndObject();

            assertEquals("12345678901234567890", document.getString("bignum").getValue());
        }

        @Test
        @DisplayName("writes binary data")
        void writeBinary() throws IOException {
            byte[] data = {1, 2, 3, 4, 5};
            delegate.writeStartObject();
            delegate.writeName("data");
            delegate.writeBinary(data);
            delegate.writeEndObject();

            BsonBinary binary = document.getBinary("data");
            assertArrayEquals(data, binary.getData());
        }
    }

    @Nested
    @DisplayName("ObjectId")
    class ObjectIdTests {

        @Test
        @DisplayName("writes native ObjectId")
        void writeNativeObjectId() throws IOException {
            ObjectId oid = new ObjectId();
            delegate.writeStartObject();
            delegate.writeName("_id");
            delegate.writeObjectId(oid);
            delegate.writeEndObject();

            assertEquals(oid, document.getObjectId("_id").getValue());
        }

        @Test
        @DisplayName("writes valid ObjectId string as native ObjectId")
        void writeObjectIdString() throws IOException {
            ObjectId oid = new ObjectId();
            delegate.writeStartObject();
            delegate.writeName("_id");
            delegate.writeObjectId(oid.toHexString());
            delegate.writeEndObject();

            assertEquals(oid, document.getObjectId("_id").getValue());
        }

        @Test
        @DisplayName("writes invalid ObjectId string as string")
        void writeInvalidObjectIdString() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("_id");
            delegate.writeObjectId("not-an-objectid");
            delegate.writeEndObject();

            assertEquals("not-an-objectid", document.getString("_id").getValue());
        }

        @Test
        @DisplayName("writes null ObjectId as BsonNull")
        void writeNullObjectId() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("_id");
            delegate.writeObjectId(null);
            delegate.writeEndObject();

            assertTrue(document.get("_id").isNull());
        }
    }

    @Nested
    @DisplayName("structure")
    class Structure {

        @Test
        @DisplayName("writes nested object")
        void nestedObject() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("address");
            delegate.writeStartObject();
            delegate.writeName("street");
            delegate.writeString("123 Main St");
            delegate.writeEndObject();
            delegate.writeEndObject();

            BsonDocument nested = document.getDocument("address");
            assertNotNull(nested);
            assertEquals("123 Main St", nested.getString("street").getValue());
        }

        @Test
        @DisplayName("writes array")
        void array() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("tags");
            delegate.writeStartArray();
            delegate.writeString("a");
            delegate.writeString("b");
            delegate.writeString("c");
            delegate.writeEndArray();
            delegate.writeEndObject();

            BsonArray arr = document.getArray("tags");
            assertEquals(3, arr.size());
            assertEquals("a", arr.get(0).asString().getValue());
            assertEquals("b", arr.get(1).asString().getValue());
            assertEquals("c", arr.get(2).asString().getValue());
        }

        @Test
        @DisplayName("writes complex document")
        void complexDocument() throws IOException {
            delegate.writeStartObject();
            delegate.writeName("name");
            delegate.writeString("Test");
            delegate.writeName("count");
            delegate.writeInt(5);
            delegate.writeName("items");
            delegate.writeStartArray();
            delegate.writeStartObject();
            delegate.writeName("label");
            delegate.writeString("first");
            delegate.writeEndObject();
            delegate.writeStartObject();
            delegate.writeName("label");
            delegate.writeString("second");
            delegate.writeEndObject();
            delegate.writeEndArray();
            delegate.writeEndObject();

            assertEquals("Test", document.getString("name").getValue());
            assertEquals(5, document.getInt32("count").getValue());
            BsonArray items = document.getArray("items");
            assertEquals(2, items.size());
            assertEquals("first", items.get(0).asDocument().getString("label").getValue());
            assertEquals("second", items.get(1).asDocument().getString("label").getValue());
        }
    }
}
