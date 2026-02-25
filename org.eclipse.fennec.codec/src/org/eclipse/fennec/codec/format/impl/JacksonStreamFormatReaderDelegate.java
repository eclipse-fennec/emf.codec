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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.format.TokenType;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

/**
 * {@link FormatReaderDelegate} implementation that wraps a Jackson {@link JsonParser}.
 * <p>
 * This allows any Jackson-based streaming format (JSON, CBOR, Smile, YAML, etc.)
 * to be read through the format-agnostic {@code FormatReaderDelegate} interface.
 * The specific format is determined by the {@link tools.jackson.core.TokenStreamFactory}
 * used to create the parser.
 *
 * @see FormatReaderDelegate
 * @see TokenTypeMapper
 * @since 2026-02-16
 */
public class JacksonStreamFormatReaderDelegate implements FormatReaderDelegate<InputStream> {

    private final JsonParser parser;
    private InputStream source;

    /**
     * Creates a delegate wrapping the given Jackson parser.
     *
     * @param parser the Jackson parser to wrap
     */
    public JacksonStreamFormatReaderDelegate(JsonParser parser) {
        this.parser = parser;
    }

    /**
     * Returns the wrapped Jackson parser.
     *
     * @return the parser
     */
    public JsonParser getParser() {
        return parser;
    }

    // ========================================================================
    // Source Management
    // ========================================================================

    @Override
    public void setSource(InputStream source) {
        this.source = source;
    }

    @Override
    public InputStream getSource() {
        return source;
    }

    // ========================================================================
    // Token Navigation
    // ========================================================================

    @Override
    public TokenType nextToken() throws IOException {
        JsonToken jsonToken = parser.nextToken();
        return TokenTypeMapper.toTokenType(jsonToken);
    }

    @Override
    public TokenType currentToken() {
        return TokenTypeMapper.toTokenType(parser.currentToken());
    }

    @Override
    public String currentName() throws IOException {
        return parser.currentName();
    }

    @Override
    public void skipChildren() throws IOException {
        parser.skipChildren();
    }

    // ========================================================================
    // Value Reading
    // ========================================================================

    @Override
    public String readString() throws IOException {
        return parser.getString();
    }

    @Override
    public int readInt() throws IOException {
        return parser.getIntValue();
    }

    @Override
    public long readLong() throws IOException {
        return parser.getLongValue();
    }

    @Override
    public float readFloat() throws IOException {
        return parser.getFloatValue();
    }

    @Override
    public double readDouble() throws IOException {
        return parser.getDoubleValue();
    }

    @Override
    public BigInteger readBigInteger() throws IOException {
        return parser.getBigIntegerValue();
    }

    @Override
    public BigDecimal readBigDecimal() throws IOException {
        return parser.getDecimalValue();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return parser.getBooleanValue();
    }

    @Override
    public byte[] readBinary() throws IOException {
        return parser.getBinaryValue();
    }

    // ========================================================================
    // Lifecycle
    // ========================================================================

    @Override
    public void close() throws IOException {
        parser.close();
    }
}
