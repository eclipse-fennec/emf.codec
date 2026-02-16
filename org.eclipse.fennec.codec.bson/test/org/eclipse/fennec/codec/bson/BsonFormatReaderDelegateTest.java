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

import org.bson.BsonBinary;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonObjectId;
import org.bson.BsonString;
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
}
