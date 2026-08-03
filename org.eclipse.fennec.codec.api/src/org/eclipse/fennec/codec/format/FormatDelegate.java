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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Format-specific writer delegate for codec serialization.
 * <p>
 * This interface abstracts the low-level writing operations for a specific
 * output format. The generic type {@code T} represents the output target,
 * which varies by format:
 * <ul>
 *   <li>{@code OutputStream} — for streaming formats (JSON, CBOR, YAML)</li>
 *   <li>{@code BsonDocument} — for MongoDB BSON (in-memory)</li>
 *   <li>{@code Document} — for Lucene indexing</li>
 * </ul>
 * <p>
 * Implementations are wrapped by {@code FormatDelegateGenerator} (which extends
 * Jackson's {@code GeneratorBase}) so that existing codec entry classes can use
 * them transparently through the standard {@code JsonGenerator} API.
 * <p>
 * Example implementation for BSON:
 * <pre>
 * public class BsonFormatDelegate implements FormatDelegate&lt;BsonDocument&gt; {
 *     private BsonDocument target;
 *     private BsonDocumentWriter writer;
 *
 *     &#64;Override
 *     public void setTarget(BsonDocument target) {
 *         this.target = target;
 *         this.writer = new BsonDocumentWriter(target);
 *     }
 *
 *     &#64;Override
 *     public void writeString(String value) {
 *         writer.writeString(value);
 *     }
 *     // ...
 * }
 * </pre>
 *
 * @param <T> the output target type
 * @see FormatReaderDelegate
 * @see CodecFormatProvider
 * @see TokenType
 * @since 2026-02-16
 */
public interface FormatDelegate<T> {

    // ========================================================================
    // Target Management
    // ========================================================================

    /**
     * Sets the output target for this delegate.
     *
     * @param target the output target (e.g., OutputStream, BsonDocument)
     */
    void setTarget(T target);

    /**
     * Returns the current output target.
     *
     * @return the output target
     */
    T getTarget();

    // ========================================================================
    // Document / Object Lifecycle
    // ========================================================================

    /**
     * Writes the start of an object/document.
     *
     * @throws IOException if an I/O error occurs
     */
    void writeStartObject() throws IOException;

    /**
     * Writes the end of an object/document.
     *
     * @throws IOException if an I/O error occurs
     */
    void writeEndObject() throws IOException;

    // ========================================================================
    // Array Lifecycle
    // ========================================================================

    /**
     * Writes the start of an array.
     *
     * @throws IOException if an I/O error occurs
     */
    void writeStartArray() throws IOException;

    /**
     * Writes the end of an array.
     *
     * @throws IOException if an I/O error occurs
     */
    void writeEndArray() throws IOException;

    // ========================================================================
    // Field Name
    // ========================================================================

    /**
     * Writes a field/property name.
     *
     * @param name the field name
     * @throws IOException if an I/O error occurs
     */
    void writeName(String name) throws IOException;

    // ========================================================================
    // Value Writing
    // ========================================================================

    /**
     * Writes a string value.
     *
     * @param value the string value
     * @throws IOException if an I/O error occurs
     */
    void writeString(String value) throws IOException;

    /**
     * Writes an integer value.
     *
     * @param value the int value
     * @throws IOException if an I/O error occurs
     */
    void writeInt(int value) throws IOException;

    /**
     * Writes a long value.
     *
     * @param value the long value
     * @throws IOException if an I/O error occurs
     */
    void writeLong(long value) throws IOException;

    /**
     * Writes a float value.
     *
     * @param value the float value
     * @throws IOException if an I/O error occurs
     */
    void writeFloat(float value) throws IOException;

    /**
     * Writes a double value.
     *
     * @param value the double value
     * @throws IOException if an I/O error occurs
     */
    void writeDouble(double value) throws IOException;

    /**
     * Writes a BigInteger value.
     *
     * @param value the BigInteger value
     * @throws IOException if an I/O error occurs
     */
    void writeBigInteger(BigInteger value) throws IOException;

    /**
     * Writes a BigDecimal value.
     *
     * @param value the BigDecimal value
     * @throws IOException if an I/O error occurs
     */
    void writeBigDecimal(BigDecimal value) throws IOException;

    /**
     * Writes a boolean value.
     *
     * @param value the boolean value
     * @throws IOException if an I/O error occurs
     */
    void writeBoolean(boolean value) throws IOException;

    /**
     * Writes a null value.
     *
     * @throws IOException if an I/O error occurs
     */
    void writeNull() throws IOException;

    /**
     * Writes a binary (byte array) value.
     *
     * @param data the byte array
     * @throws IOException if an I/O error occurs
     */
    void writeBinary(byte[] data) throws IOException;

    // ========================================================================
    // Format-Specific Features
    // ========================================================================

    /**
     * Returns whether this format supports native ObjectId types.
     * <p>
     * When {@code true}, {@link #writeObjectId(Object)} writes native ObjectIds
     * (e.g., BSON ObjectId). When {@code false}, ObjectIds are written as strings.
     *
     * @return {@code true} if native ObjectId is supported
     */
    default boolean supportsNativeObjectId() {
        return false;
    }

    /**
     * Writes a native ObjectId value.
     * <p>
     * Default implementation writes the value as a string via {@link #writeString(String)}.
     * Formats with native ObjectId support (e.g., BSON) should override this.
     *
     * @param value the ObjectId value
     * @throws IOException if an I/O error occurs
     */
    default void writeObjectId(Object value) throws IOException {
        writeString(value != null ? value.toString() : null);
    }

    /**
     * Returns whether this format supports a native date-time type.
     * <p>
     * When {@code true}, {@link #writeDateTime(long)} writes a native date-time
     * value (e.g., BSON DateTime). When {@code false}, temporal values are written
     * through the string/number vocabulary.
     *
     * @return {@code true} if native date-time is supported
     */
    default boolean supportsNativeDateTime() {
        return false;
    }

    /**
     * Writes a native date-time value as epoch milliseconds (UTC).
     * <p>
     * Default implementation writes the value via {@link #writeLong(long)}.
     * Formats with a native date-time type (e.g., BSON) should override this.
     *
     * @param epochMillis the instant as milliseconds since the epoch, UTC
     * @throws IOException if an I/O error occurs
     */
    default void writeDateTime(long epochMillis) throws IOException {
        writeLong(epochMillis);
    }

    // ========================================================================
    // Lifecycle
    // ========================================================================

    /**
     * Flushes any buffered output to the target.
     *
     * @throws IOException if an I/O error occurs
     */
    void flush() throws IOException;

    /**
     * Closes this delegate and releases resources.
     *
     * @throws IOException if an I/O error occurs
     */
    void close() throws IOException;
}
