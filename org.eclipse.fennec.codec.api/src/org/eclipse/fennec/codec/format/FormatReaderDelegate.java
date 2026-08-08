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
 * Format-specific reader delegate for codec deserialization.
 * <p>
 * This interface abstracts the low-level reading operations for a specific
 * input format. The generic type {@code S} represents the input source,
 * which varies by format:
 * <ul>
 *   <li>{@code InputStream} — for streaming formats (JSON, CBOR, YAML)</li>
 *   <li>{@code BsonDocument} — for MongoDB BSON (in-memory)</li>
 *   <li>{@code Document} — for Lucene documents</li>
 * </ul>
 * <p>
 * Implementations are wrapped by {@code FormatDelegateParser} (which extends
 * Jackson's {@code ParserBase}) so that existing codec entry classes can use
 * them transparently through the standard {@code JsonParser} API.
 * <p>
 * Example implementation for BSON:
 * <pre>
 * public class BsonFormatReaderDelegate implements FormatReaderDelegate&lt;BsonDocument&gt; {
 *     private BsonDocumentReader reader;
 *
 *     &#64;Override
 *     public void setSource(BsonDocument source) {
 *         this.reader = new BsonDocumentReader(source);
 *     }
 *
 *     &#64;Override
 *     public TokenType nextToken() {
 *         BsonType bsonType = reader.readBsonType();
 *         return mapBsonTypeToTokenType(bsonType);
 *     }
 *     // ...
 * }
 * </pre>
 *
 * @param <S> the input source type
 * @see FormatDelegate
 * @see CodecFormatProvider
 * @see TokenType
 * @since 1.0
 */
public interface FormatReaderDelegate<S> {

    // ========================================================================
    // Source Management
    // ========================================================================

    /**
     * Sets the input source for this delegate.
     *
     * @param source the input source (e.g., InputStream, BsonDocument)
     */
    void setSource(S source);

    /**
     * Returns the current input source.
     *
     * @return the input source
     */
    S getSource();

    // ========================================================================
    // Token Navigation
    // ========================================================================

    /**
     * Advances to the next token and returns its type.
     *
     * @return the type of the next token, or {@link TokenType#NOT_AVAILABLE} at end of input
     * @throws IOException if an I/O error occurs
     */
    TokenType nextToken() throws IOException;

    /**
     * Returns the type of the current token without advancing.
     *
     * @return the type of the current token, or {@link TokenType#NOT_AVAILABLE} if no token
     */
    TokenType currentToken();

    /**
     * Returns the name of the current field.
     * <p>
     * Only valid when the current or most recent token is {@link TokenType#FIELD_NAME}.
     *
     * @return the current field name, or {@code null} if not at a field
     * @throws IOException if an I/O error occurs
     */
    String currentName() throws IOException;

    /**
     * Skips the current value and all its children (for objects/arrays).
     *
     * @throws IOException if an I/O error occurs
     */
    void skipChildren() throws IOException;

    // ========================================================================
    // Value Reading
    // ========================================================================

    /**
     * Reads the current value as a string.
     *
     * @return the string value
     * @throws IOException if an I/O error occurs
     */
    String readString() throws IOException;

    /**
     * Reads the current value as an int.
     *
     * @return the int value
     * @throws IOException if an I/O error occurs
     */
    int readInt() throws IOException;

    /**
     * Reads the current value as a long.
     *
     * @return the long value
     * @throws IOException if an I/O error occurs
     */
    long readLong() throws IOException;

    /**
     * Reads the current value as a float.
     *
     * @return the float value
     * @throws IOException if an I/O error occurs
     */
    float readFloat() throws IOException;

    /**
     * Reads the current value as a double.
     *
     * @return the double value
     * @throws IOException if an I/O error occurs
     */
    double readDouble() throws IOException;

    /**
     * Reads the current value as a BigInteger.
     *
     * @return the BigInteger value
     * @throws IOException if an I/O error occurs
     */
    BigInteger readBigInteger() throws IOException;

    /**
     * Reads the current value as a BigDecimal.
     *
     * @return the BigDecimal value
     * @throws IOException if an I/O error occurs
     */
    BigDecimal readBigDecimal() throws IOException;

    /**
     * Reads the current value as a boolean.
     *
     * @return the boolean value
     * @throws IOException if an I/O error occurs
     */
    boolean readBoolean() throws IOException;

    /**
     * Reads the current value as a byte array (binary data).
     *
     * @return the byte array
     * @throws IOException if an I/O error occurs
     */
    byte[] readBinary() throws IOException;

    // ========================================================================
    // Format-Specific Features
    // ========================================================================

    /**
     * Returns whether this format supports native ObjectId types.
     *
     * @return {@code true} if native ObjectId is supported
     */
    default boolean supportsNativeObjectId() {
        return false;
    }

    /**
     * Reads the current value as a native ObjectId.
     * <p>
     * Default implementation reads the value as a string.
     * Formats with native ObjectId support (e.g., BSON) should override this.
     *
     * @return the ObjectId value
     * @throws IOException if an I/O error occurs
     */
    default Object readObjectId() throws IOException {
        return readString();
    }

    // ========================================================================
    // Lifecycle
    // ========================================================================

    /**
     * Closes this delegate and releases resources.
     *
     * @throws IOException if an I/O error occurs
     */
    void close() throws IOException;
}
