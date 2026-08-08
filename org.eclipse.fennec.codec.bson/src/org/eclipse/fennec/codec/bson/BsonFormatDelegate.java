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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.eclipse.fennec.codec.format.FormatDelegate;

/**
 * {@link FormatDelegate} implementation for BSON using {@link BsonDocumentWriter}.
 * <p>
 * Writes EMF data directly into a {@link BsonDocument} in memory, without
 * needing a MongoDB connection. This allows round-trip serialization/deserialization
 * of EMF EObjects to BSON format.
 * <p>
 * Supports native BSON types:
 * <ul>
 *   <li>{@code ObjectId} — via {@link #writeObjectId(Object)}</li>
 *   <li>{@code DateTime} — via {@link #writeDateTime(long)}</li>
 *   <li>{@code Decimal128} — via {@link #writeBigDecimal(BigDecimal)}</li>
 *   <li>{@code BsonBinary} — via {@link #writeBinary(byte[])}</li>
 *   <li>{@code Int32}, {@code Int64}, {@code Double} — native numeric types</li>
 * </ul>
 *
 * @see BsonFormatReaderDelegate
 * @see BsonFormatProvider
 * @since 1.0
 */
public class BsonFormatDelegate implements FormatDelegate<BsonDocument> {

    private BsonDocument target;
    private BsonDocumentWriter writer;

    /**
     * Creates a new delegate. Call {@link #setTarget(BsonDocument)} before writing.
     */
    public BsonFormatDelegate() {
    }

    /**
     * Creates a new delegate with the given target document.
     *
     * @param target the BSON document to write to
     */
    public BsonFormatDelegate(BsonDocument target) {
        setTarget(target);
    }

    @Override
    public void setTarget(BsonDocument target) {
        this.target = target;
        this.writer = new BsonDocumentWriter(target);
    }

    @Override
    public BsonDocument getTarget() {
        return target;
    }

    // ========================================================================
    // Document / Object Lifecycle
    // ========================================================================

    @Override
    public void writeStartObject() {
        writer.writeStartDocument();
    }

    @Override
    public void writeEndObject() {
        writer.writeEndDocument();
    }

    // ========================================================================
    // Array Lifecycle
    // ========================================================================

    @Override
    public void writeStartArray() {
        writer.writeStartArray();
    }

    @Override
    public void writeEndArray() {
        writer.writeEndArray();
    }

    // ========================================================================
    // Field Name
    // ========================================================================

    @Override
    public void writeName(String name) {
        writer.writeName(name);
    }

    // ========================================================================
    // Value Writing
    // ========================================================================

    @Override
    public void writeString(String value) {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeString(value);
        }
    }

    @Override
    public void writeInt(int value) {
        writer.writeInt32(value);
    }

    @Override
    public void writeLong(long value) {
        writer.writeInt64(value);
    }

    @Override
    public void writeFloat(float value) {
        writer.writeDouble(value);
    }

    @Override
    public void writeDouble(double value) {
        writer.writeDouble(value);
    }

    @Override
    public void writeBigInteger(BigInteger value) {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeString(value.toString());
        }
    }

    @Override
    public void writeBigDecimal(BigDecimal value) {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeDecimal128(new Decimal128(value));
        }
    }

    @Override
    public void writeBoolean(boolean value) {
        writer.writeBoolean(value);
    }

    @Override
    public void writeNull() {
        writer.writeNull();
    }

    @Override
    public void writeBinary(byte[] data) {
        if (data == null) {
            writer.writeNull();
        } else {
            writer.writeBinaryData(new BsonBinary(data));
        }
    }

    // ========================================================================
    // Format-Specific: Native DateTime
    // ========================================================================

    @Override
    public boolean supportsNativeDateTime() {
        return true;
    }

    @Override
    public void writeDateTime(long epochMillis) {
        writer.writeDateTime(epochMillis);
    }

    // ========================================================================
    // Format-Specific: Native ObjectId
    // ========================================================================

    @Override
    public boolean supportsNativeObjectId() {
        return true;
    }

    @Override
    public void writeObjectId(Object value) {
        if (value instanceof ObjectId objectId) {
            writer.writeObjectId(objectId);
        } else if (value instanceof String str) {
            if (ObjectId.isValid(str)) {
                writer.writeObjectId(new ObjectId(str));
            } else {
                writer.writeString(str);
            }
        } else if (value != null) {
            writer.writeString(value.toString());
        } else {
            writer.writeNull();
        }
    }

    // ========================================================================
    // Lifecycle
    // ========================================================================

    @Override
    public void flush() {
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }
}
