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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.types.Decimal128;
import org.bson.BsonDecimal128;
import org.bson.types.ObjectId;
import org.eclipse.fennec.codec.format.TokenType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BsonFormatReaderDelegate}.
 */
@DisplayName("BsonFormatReaderDelegate")
class BsonFormatReaderDelegateTest {

    private BsonFormatReaderDelegate createReader(BsonDocument doc) {
        return new BsonFormatReaderDelegate(doc);
    }

    @Nested
    @DisplayName("scalar values")
    class ScalarValues {

        @Test
        @DisplayName("reads string value")
        void readString() throws IOException {
            BsonDocument doc = new BsonDocument("name", new BsonString("Alice"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("name", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("Alice", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("reads int value")
        void readInt() throws IOException {
            BsonDocument doc = new BsonDocument("count", new BsonInt32(42));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_INT, reader.nextToken());
            assertEquals(42, reader.readInt());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("reads long value")
        void readLong() throws IOException {
            BsonDocument doc = new BsonDocument("amount", new BsonInt64(9876543210L));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_INT, reader.nextToken());
            assertEquals(9876543210L, reader.readLong());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("reads double value")
        void readDouble() throws IOException {
            BsonDocument doc = new BsonDocument("score", new BsonDouble(95.5));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, reader.nextToken());
            assertEquals(95.5, reader.readDouble());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("reads boolean true")
        void readBooleanTrue() throws IOException {
            BsonDocument doc = new BsonDocument("active", BsonBoolean.TRUE);
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_BOOLEAN, reader.nextToken());
            assertTrue(reader.readBoolean());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("reads boolean false")
        void readBooleanFalse() throws IOException {
            BsonDocument doc = new BsonDocument("active", BsonBoolean.FALSE);
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_BOOLEAN, reader.nextToken());
            assertFalse(reader.readBoolean());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("reads null value")
        void readNull() throws IOException {
            BsonDocument doc = new BsonDocument("nothing", BsonNull.VALUE);
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NULL, reader.nextToken());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("reads binary data")
        void readBinary() throws IOException {
            byte[] data = {1, 2, 3, 4, 5};
            BsonDocument doc = new BsonDocument("data", new BsonBinary(data));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_BINARY, reader.nextToken());
            assertArrayEquals(data, reader.readBinary());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }
    }

    @Nested
    @DisplayName("ObjectId")
    class ObjectIdTests {

        @Test
        @DisplayName("reads ObjectId as string")
        void readObjectIdAsString() throws IOException {
            ObjectId oid = new ObjectId();
            BsonDocument doc = new BsonDocument("_id", new BsonObjectId(oid));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals(oid.toHexString(), reader.readString());
            reader.close();
        }

        @Test
        @DisplayName("reads native ObjectId")
        void readNativeObjectId() throws IOException {
            ObjectId oid = new ObjectId();
            BsonDocument doc = new BsonDocument("_id", new BsonObjectId(oid));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertTrue(reader.supportsNativeObjectId());
            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals(oid, reader.readObjectId());
            reader.close();
        }
    }

    @Nested
    @DisplayName("structure")
    class Structure {

        @Test
        @DisplayName("reads nested object")
        void nestedObject() throws IOException {
            BsonDocument nested = new BsonDocument("street", new BsonString("123 Main St"));
            BsonDocument doc = new BsonDocument("address", nested);
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("address", reader.currentName());
            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("street", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("123 Main St", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("reads array")
        void array() throws IOException {
            BsonDocument doc = new BsonDocument("tags",
                    new org.bson.BsonArray(java.util.List.of(
                            new BsonString("a"),
                            new BsonString("b"),
                            new BsonString("c"))));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("tags", reader.currentName());
            assertEquals(TokenType.START_ARRAY, reader.nextToken());

            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("a", reader.readString());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("b", reader.readString());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("c", reader.readString());

            assertEquals(TokenType.END_ARRAY, reader.nextToken());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }
    }

    @Nested
    @DisplayName("round-trip with BsonFormatDelegate")
    class RoundTrip {

        @Test
        @DisplayName("write then read produces same values")
        void roundTrip() throws IOException {
            // Write
            BsonDocument doc = new BsonDocument();
            BsonFormatDelegate writer = new BsonFormatDelegate(doc);
            writer.writeStartObject();
            writer.writeName("name");
            writer.writeString("Alice");
            writer.writeName("age");
            writer.writeInt(30);
            writer.writeName("active");
            writer.writeBoolean(true);
            writer.writeName("score");
            writer.writeDouble(9.5);
            writer.writeEndObject();
            writer.close();

            // Read
            BsonFormatReaderDelegate reader = createReader(doc);

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

            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }
    }

    @Nested
    @DisplayName("unconsumed values")
    class UnconsumedValues {

        @Test
        @DisplayName("skips unconsumed string when advancing to next token")
        void skipsUnconsumedString() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", new BsonString("skip-me"))
                    .append("b", new BsonInt32(42));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("a", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            // Do NOT call readString() — simulate codec skipping unknown field
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("b", reader.currentName());
            assertEquals(TokenType.VALUE_NUMBER_INT, reader.nextToken());
            assertEquals(42, reader.readInt());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed int when advancing to next token")
        void skipsUnconsumedInt() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", new BsonInt32(99))
                    .append("b", new BsonString("hello"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_INT, reader.nextToken());
            // Do NOT call readInt()
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("b", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("hello", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed long when advancing to next token")
        void skipsUnconsumedLong() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", new BsonInt64(123456789L))
                    .append("b", new BsonString("next"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_INT, reader.nextToken());
            // Do NOT call readLong()
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("b", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("next", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed double when advancing to next token")
        void skipsUnconsumedDouble() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", new BsonDouble(3.14))
                    .append("b", new BsonString("after"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, reader.nextToken());
            // Do NOT call readDouble()
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("b", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("after", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed decimal128 when advancing to next token")
        void skipsUnconsumedDecimal128() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", new BsonDecimal128(new Decimal128(99)))
                    .append("b", new BsonString("end"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, reader.nextToken());
            // Do NOT call readBigDecimal()
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("b", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("end", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed boolean when advancing to next token")
        void skipsUnconsumedBoolean() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", BsonBoolean.TRUE)
                    .append("b", new BsonString("found"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_BOOLEAN, reader.nextToken());
            // Do NOT call readBoolean()
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("b", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("found", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed binary when advancing to next token")
        void skipsUnconsumedBinary() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", new BsonBinary(new byte[]{1, 2, 3}))
                    .append("b", new BsonString("ok"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_BINARY, reader.nextToken());
            // Do NOT call readBinary()
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("b", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("ok", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed ObjectId when advancing to next token")
        void skipsUnconsumedObjectId() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", new BsonObjectId(new ObjectId()))
                    .append("b", new BsonString("done"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            // Do NOT call readString() or readObjectId()
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("b", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("done", reader.readString());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips multiple consecutive unconsumed values")
        void skipsMultipleUnconsumedValues() throws IOException {
            BsonDocument doc = new BsonDocument()
                    .append("a", new BsonString("skip1"))
                    .append("b", new BsonInt32(123))
                    .append("c", new BsonDouble(4.5))
                    .append("d", new BsonString("read-me"));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());

            // Skip "a" (string)
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());

            // Skip "b" (int)
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_INT, reader.nextToken());

            // Skip "c" (double)
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, reader.nextToken());

            // Read "d" (string)
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("d", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("read-me", reader.readString());

            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed values in array context")
        void skipsUnconsumedValuesInArray() throws IOException {
            BsonDocument doc = new BsonDocument("items",
                    new BsonArray(List.of(
                            new BsonString("skip1"),
                            new BsonString("skip2"),
                            new BsonString("read-me"))));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals(TokenType.START_ARRAY, reader.nextToken());

            // Skip first two values without consuming
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());

            // Read the third
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("read-me", reader.readString());

            assertEquals(TokenType.END_ARRAY, reader.nextToken());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }

        @Test
        @DisplayName("skips unconsumed value in nested object — the SuperType bug scenario")
        void skipsUnconsumedValueInNestedObject() throws IOException {
            // Reproduces the exact pattern that caused the SuperType hang:
            // An object inside an array with an unknown field whose value is not consumed
            BsonDocument inner = new BsonDocument()
                    .append("_supertype", new BsonString("Manager"))
                    .append("name", new BsonString("Alice"));
            BsonDocument doc = new BsonDocument("staff",
                    new BsonArray(List.of(inner)));
            BsonFormatReaderDelegate reader = createReader(doc);

            assertEquals(TokenType.START_OBJECT, reader.nextToken());
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("staff", reader.currentName());
            assertEquals(TokenType.START_ARRAY, reader.nextToken());
            assertEquals(TokenType.START_OBJECT, reader.nextToken());

            // Read _supertype field name but skip its value (the bug scenario)
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("_supertype", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            // Do NOT call readString() — this is what caused the infinite loop

            // The fix should allow advancing to the next field
            assertEquals(TokenType.FIELD_NAME, reader.nextToken());
            assertEquals("name", reader.currentName());
            assertEquals(TokenType.VALUE_STRING, reader.nextToken());
            assertEquals("Alice", reader.readString());

            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            assertEquals(TokenType.END_ARRAY, reader.nextToken());
            assertEquals(TokenType.END_OBJECT, reader.nextToken());
            reader.close();
        }
    }
}
