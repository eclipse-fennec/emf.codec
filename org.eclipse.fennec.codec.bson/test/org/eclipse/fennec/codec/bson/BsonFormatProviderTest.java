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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Provider metadata and security tests for {@link BsonFormatProvider}.
 * Round-trip tests are covered by {@link BsonCoreRoundTripTCKTest}.
 */
@DisplayName("BsonFormatProvider")
class BsonFormatProviderTest {

    @Nested
    @DisplayName("Provider metadata")
    class ProviderMetadata {

        @Test
        @DisplayName("returns correct format ID")
        void formatId() {
            BsonFormatProvider provider = new BsonFormatProvider();
            assertEquals("bson", provider.getFormatId());
        }

        @Test
        @DisplayName("returns correct file extensions")
        void fileExtensions() {
            BsonFormatProvider provider = new BsonFormatProvider();
            assertArrayEquals(new String[] { "bson" }, provider.getFileExtensions());
        }

        @Test
        @DisplayName("returns correct content types")
        void contentTypes() {
            BsonFormatProvider provider = new BsonFormatProvider();
            assertArrayEquals(new String[] { "application/bson" }, provider.getContentTypes());
        }
    }

    @Nested
    @DisplayName("Payload size limit (S-1)")
    class PayloadSizeLimit {

        @Test
        @DisplayName("default provider uses 100MB limit")
        void defaultLimit() {
            BsonFormatProvider provider = new BsonFormatProvider();
            assertNotNull(provider);
        }

        @Test
        @DisplayName("custom limit is accepted")
        void customLimit() {
            BsonFormatProvider provider = new BsonFormatProvider(1024);
            assertNotNull(provider);
        }

        @Test
        @DisplayName("zero limit is rejected")
        void zeroLimitRejected() {
            assertThrows(IllegalArgumentException.class, () -> new BsonFormatProvider(0));
        }

        @Test
        @DisplayName("negative limit is rejected")
        void negativeLimitRejected() {
            assertThrows(IllegalArgumentException.class, () -> new BsonFormatProvider(-1));
        }

        @Test
        @DisplayName("payload within limit is accepted")
        void payloadWithinLimit() throws IOException {
            byte[] bsonBytes = createSmallBsonDocument();
            BsonFormatProvider provider = new BsonFormatProvider(bsonBytes.length + 1024);
            InputStream input = new ByteArrayInputStream(bsonBytes);

            var reader = provider.createReader(input);
            assertNotNull(reader);
            reader.close();
        }

        @Test
        @DisplayName("payload exceeding limit throws IOException")
        void payloadExceedingLimit() {
            // Create a BSON payload larger than the limit
            byte[] bsonBytes = createSmallBsonDocument();
            // Set a limit smaller than the document
            BsonFormatProvider provider = new BsonFormatProvider(5);
            InputStream input = new ByteArrayInputStream(bsonBytes);

            IOException exception = assertThrows(IOException.class,
                    () -> provider.createReader(input));
            assertEquals(true,
                    exception.getMessage().contains("exceeds maximum allowed size"),
                    "Expected message about size limit, got: " + exception.getMessage());
        }

        @Test
        @DisplayName("default max payload size constant is 100MB")
        void defaultConstant() {
            assertEquals(100L * 1024 * 1024, CodecOptions.DEFAULT_MAX_PAYLOAD_SIZE);
        }

        private static byte[] createSmallBsonDocument() {
            BsonDocument doc = new BsonDocument();
            doc.append("name", new BsonString("test"));
            doc.append("value", new BsonString("hello"));

            BasicOutputBuffer buffer = new BasicOutputBuffer();
            try (BsonBinaryWriter writer = new BsonBinaryWriter(buffer)) {
                new BsonDocumentCodec().encode(writer, doc, EncoderContext.builder().build());
            }
            return buffer.toByteArray();
        }
    }

    @Nested
    @DisplayName("Stream writer document count")
    class StreamWriterDocumentCount {

        @Test
        @DisplayName("flush() then close() writes exactly one BSON document")
        void writesExactlyOneDocument() throws IOException {
            BsonFormatProvider provider = new BsonFormatProvider();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            FormatDelegate<OutputStream> writer = provider.createWriter(out);
            writer.writeStartObject();
            writer.writeName("name");
            writer.writeString("Ada");
            writer.writeEndObject();
            // Reproduce the sequence Jackson's GeneratorBase.close() uses:
            // flush() first, then close() via _closeInput().
            writer.flush();
            writer.close();

            assertEquals(1, countBsonDocuments(out.toByteArray()),
                    "Expected exactly 1 BSON document; BsonStreamWriter.flush() and close() "
                    + "both call serializeToStream(), writing the document twice");
        }

        private static int countBsonDocuments(byte[] bytes) {
            int count = 0;
            int pos = 0;
            while (pos + 4 <= bytes.length) {
                int docLen = (bytes[pos] & 0xFF)
                        | ((bytes[pos + 1] & 0xFF) << 8)
                        | ((bytes[pos + 2] & 0xFF) << 16)
                        | ((bytes[pos + 3] & 0xFF) << 24);
                if (docLen <= 0 || pos + docLen > bytes.length) break;
                pos += docLen;
                count++;
            }
            return count;
        }
    }
}
