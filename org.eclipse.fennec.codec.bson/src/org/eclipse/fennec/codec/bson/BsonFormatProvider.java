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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Map;

import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.format.TokenType;

import tools.jackson.core.StreamReadConstraints;

/**
 * {@link CodecFormatProvider} implementation for BSON format.
 * <p>
 * Provides stream-based BSON serialization/deserialization by internally
 * using {@link BsonFormatDelegate} and {@link BsonFormatReaderDelegate}
 * with {@link BsonDocument} as the in-memory representation, and converting
 * to/from binary BSON for stream I/O.
 * <p>
 * Reading is bounded by the read limits of the load (issue #232): the document size
 * ({@link CodecOptions#CODEC_MAX_PAYLOAD_SIZE}, default 16 MiB) bounds the bytes read, and
 * nesting depth, string length and name length are checked on the raw document by an
 * iterative walk before the recursive BSON decoder runs. Through {@code CodecResource} the
 * limits come from the load options; used directly, the provider applies the defaults with
 * the payload size given to its constructor.
 * <p>
 * Usage:
 * <pre>
 * BsonFormatProvider provider = new BsonFormatProvider();
 * CodecResource resource = new CodecResource(uri, metadataService,
 *         resolver, null, null, provider);
 * resource.save(outputStream, options);
 * resource.load(inputStream, options);
 * </pre>
 *
 * @see BsonFormatDelegate
 * @see BsonFormatReaderDelegate
 * @see CodecOptions#CODEC_MAX_PAYLOAD_SIZE
 * @since 1.0
 */
public class BsonFormatProvider implements CodecFormatProvider<InputStream, OutputStream> {

    private static final BsonDocumentCodec CODEC = new BsonDocumentCodec();

    private final long maxPayloadSize;

    /**
     * Creates a provider with the default maximum payload size (16 MiB).
     */
    public BsonFormatProvider() {
        this(CodecOptions.DEFAULT_MAX_PAYLOAD_SIZE);
    }

    /**
     * Creates a provider with a custom maximum payload size.
     *
     * @param maxPayloadSize the maximum number of bytes to read from the input
     *        stream; must be positive
     * @throws IllegalArgumentException if maxPayloadSize is not positive
     */
    public BsonFormatProvider(long maxPayloadSize) {
        if (maxPayloadSize <= 0) {
            throw new IllegalArgumentException("maxPayloadSize must be positive: " + maxPayloadSize);
        }
        this.maxPayloadSize = maxPayloadSize;
    }

    @Override
    public String getFormatId() {
        return "bson";
    }

    @Override
    public FormatDelegate<OutputStream> createWriter(OutputStream target) {
        return new BsonStreamWriter(target);
    }

    @Override
    public FormatReaderDelegate<InputStream> createReader(InputStream source) throws IOException {
        return new BsonStreamReader(source, ownLimits());
    }

    /**
     * Creates a reader bounded by the read limits {@code CodecResource} resolved for this load
     * (issue #232); without them, by the defaults and this provider's payload size.
     */
    @Override
    public FormatReaderDelegate<InputStream> createReader(InputStream source, Map<String, Object> loadOptions)
            throws IOException {
        Object limits = loadOptions == null ? null : loadOptions.get(CodecOptions.INTERNAL_STREAM_READ_CONSTRAINTS);
        return new BsonStreamReader(source,
                limits instanceof StreamReadConstraints constraints ? constraints : ownLimits());
    }

    private StreamReadConstraints ownLimits() {
        return StreamReadConstraints.builder()
                .maxDocumentLength(maxPayloadSize)
                .maxNestingDepth(CodecOptions.DEFAULT_MAX_NESTING_DEPTH)
                .maxStringLength(CodecOptions.DEFAULT_MAX_STRING_LENGTH)
                .maxNameLength(CodecOptions.DEFAULT_MAX_NAME_LENGTH)
                .build();
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] { "bson" };
    }

    @Override
    public String[] getContentTypes() {
        return new String[] { "application/bson" };
    }

    @Override
    public boolean supportsArrayRoot() {
        return false;
    }

    // ========================================================================
    // Stream-wrapping writer: BsonDocument → OutputStream
    // ========================================================================

    private static class BsonStreamWriter implements FormatDelegate<OutputStream> {

        private OutputStream target;
        private final BsonDocument document = new BsonDocument();
        private final BsonFormatDelegate delegate = new BsonFormatDelegate(document);

        BsonStreamWriter(OutputStream target) {
            this.target = target;
        }

        @Override
        public void setTarget(OutputStream target) {
            this.target = target;
        }

        @Override
        public OutputStream getTarget() {
            return target;
        }

        @Override
        public void writeStartObject() throws IOException {
            delegate.writeStartObject();
        }

        @Override
        public void writeEndObject() throws IOException {
            delegate.writeEndObject();
        }

        @Override
        public void writeStartArray() throws IOException {
            delegate.writeStartArray();
        }

        @Override
        public void writeEndArray() throws IOException {
            delegate.writeEndArray();
        }

        @Override
        public void writeName(String name) throws IOException {
            delegate.writeName(name);
        }

        @Override
        public void writeString(String value) throws IOException {
            delegate.writeString(value);
        }

        @Override
        public void writeInt(int value) throws IOException {
            delegate.writeInt(value);
        }

        @Override
        public void writeLong(long value) throws IOException {
            delegate.writeLong(value);
        }

        @Override
        public void writeFloat(float value) throws IOException {
            delegate.writeFloat(value);
        }

        @Override
        public void writeDouble(double value) throws IOException {
            delegate.writeDouble(value);
        }

        @Override
        public void writeBigInteger(BigInteger value) throws IOException {
            delegate.writeBigInteger(value);
        }

        @Override
        public void writeBigDecimal(BigDecimal value) throws IOException {
            delegate.writeBigDecimal(value);
        }

        @Override
        public void writeBoolean(boolean value) throws IOException {
            delegate.writeBoolean(value);
        }

        @Override
        public void writeNull() throws IOException {
            delegate.writeNull();
        }

        @Override
        public void writeBinary(byte[] data) throws IOException {
            delegate.writeBinary(data);
        }

        @Override
        public boolean supportsNativeObjectId() {
            return delegate.supportsNativeObjectId();
        }

        @Override
        public void writeObjectId(Object value) throws IOException {
            delegate.writeObjectId(value);
        }

        @Override
        public boolean supportsNativeDateTime() {
            return delegate.supportsNativeDateTime();
        }

        @Override
        public void writeDateTime(long epochMillis) throws IOException {
            delegate.writeDateTime(epochMillis);
        }

        @Override
        public void flush() throws IOException {
            delegate.flush();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
            serializeToStream();
        }

        private void serializeToStream() throws IOException {
            BasicOutputBuffer buffer = new BasicOutputBuffer();
            try (BsonBinaryWriter writer = new BsonBinaryWriter(buffer)) {
                CODEC.encode(writer, document, EncoderContext.builder().build());
            }
            target.write(buffer.toByteArray());
            target.flush();
        }
    }

    // ========================================================================
    // Stream-wrapping reader: InputStream → BsonDocument
    // ========================================================================

    private static class BsonStreamReader implements FormatReaderDelegate<InputStream> {

        private InputStream source;
        private final BsonFormatReaderDelegate delegate;

        BsonStreamReader(InputStream source, StreamReadConstraints limits) throws IOException {
            this.source = source;
            long maxPayloadSize = limits.getMaxDocumentLength();
            int readLimit = (int) Math.min(maxPayloadSize, Integer.MAX_VALUE);
            byte[] bytes = source.readNBytes(readLimit);
            if (bytes.length == readLimit && source.read() != -1) {
                throw new IOException("BSON payload exceeds maximum allowed size: " + maxPayloadSize
                        + " bytes (from " + CodecOptions.CODEC_MAX_PAYLOAD_SIZE + ")");
            }
            // Iteratively, before the recursive decode below can overflow the stack
            BsonLimitCheck.check(bytes, limits);
            try (BsonBinaryReader reader = new BsonBinaryReader(ByteBuffer.wrap(bytes))) {
                BsonDocument document = CODEC.decode(reader, DecoderContext.builder().build());
                this.delegate = new BsonFormatReaderDelegate(document);
            }
        }

        @Override
        public void setSource(InputStream source) {
            this.source = source;
        }

        @Override
        public InputStream getSource() {
            return source;
        }

        @Override
        public TokenType nextToken() throws IOException {
            return delegate.nextToken();
        }

        @Override
        public TokenType currentToken() {
            return delegate.currentToken();
        }

        @Override
        public String currentName() throws IOException {
            return delegate.currentName();
        }

        @Override
        public void skipChildren() throws IOException {
            delegate.skipChildren();
        }

        @Override
        public String readString() throws IOException {
            return delegate.readString();
        }

        @Override
        public int readInt() throws IOException {
            return delegate.readInt();
        }

        @Override
        public long readLong() throws IOException {
            return delegate.readLong();
        }

        @Override
        public float readFloat() throws IOException {
            return delegate.readFloat();
        }

        @Override
        public double readDouble() throws IOException {
            return delegate.readDouble();
        }

        @Override
        public BigInteger readBigInteger() throws IOException {
            return delegate.readBigInteger();
        }

        @Override
        public BigDecimal readBigDecimal() throws IOException {
            return delegate.readBigDecimal();
        }

        @Override
        public boolean readBoolean() throws IOException {
            return delegate.readBoolean();
        }

        @Override
        public byte[] readBinary() throws IOException {
            return delegate.readBinary();
        }

        @Override
        public boolean supportsNativeObjectId() {
            return delegate.supportsNativeObjectId();
        }

        @Override
        public Object readObjectId() throws IOException {
            return delegate.readObjectId();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }
}
