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
import java.util.ArrayDeque;
import java.util.Deque;

import org.bson.AbstractBsonReader;
import org.bson.AbstractBsonReader.State;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonType;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.format.TokenType;

/**
 * {@link FormatReaderDelegate} implementation for BSON using {@link BsonDocumentReader}.
 * <p>
 * Reads EMF data from a {@link BsonDocument} in memory. Maps BSON's state-machine
 * based reading (readBsonType → readName → readValue) to the token-stream model
 * used by the codec framework.
 * <p>
 * BSON type mapping:
 * <ul>
 *   <li>{@code DOCUMENT} → {@code START_OBJECT}</li>
 *   <li>{@code END_OF_DOCUMENT} → {@code END_OBJECT}</li>
 *   <li>{@code ARRAY} → {@code START_ARRAY}</li>
 *   <li>{@code STRING}, {@code OBJECT_ID} → {@code VALUE_STRING}</li>
 *   <li>{@code INT32}, {@code INT64} → {@code VALUE_NUMBER_INT}</li>
 *   <li>{@code DOUBLE}, {@code DECIMAL128} → {@code VALUE_NUMBER_FLOAT}</li>
 *   <li>{@code BOOLEAN} → {@code VALUE_BOOLEAN}</li>
 *   <li>{@code NULL} → {@code VALUE_NULL}</li>
 *   <li>{@code BINARY} → {@code VALUE_BINARY}</li>
 * </ul>
 *
 * @see BsonFormatDelegate
 * @see BsonFormatProvider
 * @since 2026-02-16
 */
public class BsonFormatReaderDelegate implements FormatReaderDelegate<BsonDocument> {

    private enum ContextType { DOCUMENT, ARRAY }

    private BsonDocument source;
    private AbstractBsonReader reader;
    private TokenType currentToken = TokenType.NOT_AVAILABLE;
    private String currentName;
    private final Deque<ContextType> contextStack = new ArrayDeque<>();

    /**
     * Creates a new delegate. Call {@link #setSource(BsonDocument)} before reading.
     */
    public BsonFormatReaderDelegate() {
    }

    /**
     * Creates a new delegate with the given source document.
     *
     * @param source the BSON document to read from
     */
    public BsonFormatReaderDelegate(BsonDocument source) {
        setSource(source);
    }

    @Override
    public void setSource(BsonDocument source) {
        this.source = source;
        this.reader = new BsonDocumentReader(source);
    }

    @Override
    public BsonDocument getSource() {
        return source;
    }

    // ========================================================================
    // Token Navigation
    // ========================================================================

    @Override
    public TokenType nextToken() {
        State state = reader.getState();

        switch (state) {
            case INITIAL:
                reader.readStartDocument();
                contextStack.push(ContextType.DOCUMENT);
                currentToken = TokenType.START_OBJECT;
                return currentToken;

            case TYPE:
                BsonType bsonType = reader.readBsonType();
                if (bsonType == BsonType.END_OF_DOCUMENT) {
                    ContextType ctx = contextStack.poll();
                    if (ctx == ContextType.ARRAY) {
                        currentToken = TokenType.END_ARRAY;
                        reader.readEndArray();
                    } else {
                        currentToken = TokenType.END_OBJECT;
                        reader.readEndDocument();
                    }
                    return currentToken;
                }
                // After readBsonType: in document context → NAME state, in array context → VALUE state
                if (reader.getState() == State.NAME) {
                    currentName = reader.readName();
                    currentToken = TokenType.FIELD_NAME;
                    return currentToken;
                }
                // Array context — handle the value directly
                return handleBsonType(bsonType);

            case NAME:
                currentName = reader.readName();
                currentToken = TokenType.FIELD_NAME;
                return currentToken;

            case VALUE:
                bsonType = reader.getCurrentBsonType();
                return handleBsonType(bsonType);

            case END_OF_DOCUMENT:
                currentToken = TokenType.END_OBJECT;
                reader.readEndDocument();
                return currentToken;

            case END_OF_ARRAY:
                currentToken = TokenType.END_ARRAY;
                reader.readEndArray();
                return currentToken;

            case DONE:
                currentToken = TokenType.NOT_AVAILABLE;
                return currentToken;

            default:
                currentToken = TokenType.NOT_AVAILABLE;
                return currentToken;
        }
    }

    private TokenType handleBsonType(BsonType bsonType) {
        switch (bsonType) {
            case DOCUMENT:
                reader.readStartDocument();
                contextStack.push(ContextType.DOCUMENT);
                currentToken = TokenType.START_OBJECT;
                break;
            case ARRAY:
                reader.readStartArray();
                contextStack.push(ContextType.ARRAY);
                currentToken = TokenType.START_ARRAY;
                break;
            case STRING:
                currentToken = TokenType.VALUE_STRING;
                break;
            case INT32:
            case INT64:
                currentToken = TokenType.VALUE_NUMBER_INT;
                break;
            case DOUBLE:
            case DECIMAL128:
                currentToken = TokenType.VALUE_NUMBER_FLOAT;
                break;
            case BOOLEAN:
                currentToken = TokenType.VALUE_BOOLEAN;
                break;
            case NULL:
                reader.readNull();
                currentToken = TokenType.VALUE_NULL;
                break;
            case BINARY:
                currentToken = TokenType.VALUE_BINARY;
                break;
            case OBJECT_ID:
                currentToken = TokenType.VALUE_STRING;
                break;
            default:
                currentToken = TokenType.NOT_AVAILABLE;
                break;
        }
        return currentToken;
    }

    @Override
    public TokenType currentToken() {
        return currentToken;
    }

    @Override
    public String currentName() {
        return currentName;
    }

    @Override
    public void skipChildren() {
        reader.skipValue();
    }

    // ========================================================================
    // Value Reading
    // ========================================================================

    @Override
    public String readString() {
        BsonType type = reader.getCurrentBsonType();
        if (type == BsonType.OBJECT_ID) {
            return reader.readObjectId().toHexString();
        }
        return reader.readString();
    }

    @Override
    public int readInt() {
        return reader.readInt32();
    }

    @Override
    public long readLong() {
        BsonType type = reader.getCurrentBsonType();
        if (type == BsonType.INT32) {
            return reader.readInt32();
        }
        return reader.readInt64();
    }

    @Override
    public float readFloat() {
        return (float) reader.readDouble();
    }

    @Override
    public double readDouble() {
        BsonType type = reader.getCurrentBsonType();
        if (type == BsonType.DECIMAL128) {
            return reader.readDecimal128().bigDecimalValue().doubleValue();
        }
        return reader.readDouble();
    }

    @Override
    public BigInteger readBigInteger() {
        BsonType type = reader.getCurrentBsonType();
        if (type == BsonType.STRING) {
            return new BigInteger(reader.readString());
        }
        if (type == BsonType.INT64) {
            return BigInteger.valueOf(reader.readInt64());
        }
        return BigInteger.valueOf(reader.readInt32());
    }

    @Override
    public BigDecimal readBigDecimal() {
        BsonType type = reader.getCurrentBsonType();
        if (type == BsonType.DECIMAL128) {
            return reader.readDecimal128().bigDecimalValue();
        }
        return BigDecimal.valueOf(reader.readDouble());
    }

    @Override
    public boolean readBoolean() {
        return reader.readBoolean();
    }

    @Override
    public byte[] readBinary() {
        return reader.readBinaryData().getData();
    }

    // ========================================================================
    // Format-Specific: Native ObjectId
    // ========================================================================

    @Override
    public boolean supportsNativeObjectId() {
        return true;
    }

    @Override
    public Object readObjectId() {
        return reader.readObjectId();
    }

    // ========================================================================
    // Lifecycle
    // ========================================================================

    @Override
    public void close() {
        reader.close();
    }
}
