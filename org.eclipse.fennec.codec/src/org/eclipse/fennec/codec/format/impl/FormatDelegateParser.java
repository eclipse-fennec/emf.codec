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
import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.format.TokenType;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.Version;
import tools.jackson.core.base.ParserBase;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.SimpleStreamReadContext;

/**
 * Jackson {@link tools.jackson.core.JsonParser} implementation that delegates
 * all read operations to a {@link FormatReaderDelegate}.
 * <p>
 * This bridge allows existing codec entry classes (which use Jackson's
 * {@code JsonParser} API) to transparently read from any format that
 * provides a {@code FormatReaderDelegate} implementation.
 * <p>
 * Token navigation ({@link #nextToken()}) delegates to the reader and uses
 * {@link TokenTypeMapper} to convert between format-agnostic {@link TokenType}
 * and Jackson's {@link JsonToken}. Value accessors ({@code getString()},
 * {@code getIntValue()}, etc.) read directly from the delegate, bypassing
 * {@code ParserBase}'s text-buffer-based number parsing.
 *
 * @param <S> the input source type of the wrapped delegate
 * @see FormatReaderDelegate
 * @see TokenTypeMapper
 * @since 2026-02-16
 */
public class FormatDelegateParser<S> extends ParserBase {

    private static final Version VERSION = new Version(1, 0, 0, "SNAPSHOT",
            "org.eclipse.fennec", "fennec-codec");

    private final FormatReaderDelegate<S> delegate;
    private SimpleStreamReadContext _readContext;

    // ========================================================================
    // Construction
    // ========================================================================

    /**
     * Creates a new parser wrapping the given delegate.
     *
     * @param readCtxt the Jackson object read context
     * @param ioCtxt the Jackson I/O context
     * @param streamReadFeatures the stream read feature flags
     * @param delegate the format reader delegate to wrap
     */
    protected FormatDelegateParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            int streamReadFeatures, FormatReaderDelegate<S> delegate) {
        super(readCtxt, ioCtxt, streamReadFeatures);
        this.delegate = delegate;
        _readContext = SimpleStreamReadContext.createRootContext(
                -1, -1, null);
    }

    /**
     * Factory method to create a parser with default stream read features.
     *
     * @param <S> the input source type
     * @param readCtxt the Jackson object read context
     * @param ioCtxt the Jackson I/O context
     * @param delegate the format reader delegate to wrap
     * @return a new parser instance
     */
    public static <S> FormatDelegateParser<S> create(ObjectReadContext readCtxt,
            IOContext ioCtxt, FormatReaderDelegate<S> delegate) {
        return new FormatDelegateParser<>(readCtxt, ioCtxt, 0, delegate);
    }

    /**
     * Returns the wrapped format reader delegate.
     *
     * @return the delegate
     */
    public FormatReaderDelegate<S> getDelegate() {
        return delegate;
    }

    /**
     * Replaces the stream read context. This allows external code
     * (e.g., codec infrastructure) to install a custom context such as
     * {@code CodecReadContext}.
     *
     * @param context the new stream read context
     */
    public void setStreamReadContext(SimpleStreamReadContext context) {
        _readContext = context;
    }

    // ========================================================================
    // JsonParser — metadata / context
    // ========================================================================

    @Override
    public Version version() {
        return VERSION;
    }

    @Override
    public TokenStreamContext streamReadContext() {
        return _readContext;
    }

    @Override
    public Object streamReadInputSource() {
        return delegate.getSource();
    }

    @Override
    public JacksonFeatureSet<StreamReadCapability> streamReadCapabilities() {
        return DEFAULT_READ_CAPABILITIES;
    }

    // ========================================================================
    // JsonParser — token navigation
    // ========================================================================

    @Override
    public JsonToken nextToken() throws JacksonException {
        try {
            TokenType tokenType = delegate.nextToken();
            if (tokenType == null) {
                _currToken = null;
                return null;
            }

            // Update read context based on token type
            switch (tokenType) {
                case START_OBJECT:
                    _readContext = _readContext.createChildObjectContext(
                            -1, -1);
                    break;
                case START_ARRAY:
                    _readContext = _readContext.createChildArrayContext(
                            -1, -1);
                    break;
                case END_OBJECT:
                case END_ARRAY:
                    _readContext = _readContext.clearAndGetParent();
                    break;
                case FIELD_NAME:
                    String name = delegate.currentName();
                    _readContext.setCurrentName(name);
                    break;
                default:
                    break;
            }

            // Map TokenType to JsonToken
            if (tokenType == TokenType.VALUE_BOOLEAN) {
                boolean value = delegate.readBoolean();
                _currToken = TokenTypeMapper.toJsonToken(tokenType, value);
            } else {
                _currToken = TokenTypeMapper.toJsonToken(tokenType);
            }
            return _currToken;
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public String currentName() {
        if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
            SimpleStreamReadContext parent = _readContext.getParent();
            if (parent != null) {
                return parent.currentName();
            }
        }
        return _readContext.currentName();
    }

    // ========================================================================
    // JsonParser — location
    // ========================================================================

    @Override
    public TokenStreamLocation currentTokenLocation() {
        return new TokenStreamLocation(_contentReference(),
                -1L, -1L, -1, -1);
    }

    @Override
    public TokenStreamLocation currentLocation() {
        return new TokenStreamLocation(_contentReference(),
                -1L, -1L, -1, -1);
    }

    // ========================================================================
    // JsonParser — string access
    // ========================================================================

    @Override
    public String getString() throws JacksonException {
        if (_currToken == JsonToken.PROPERTY_NAME) {
            return _readContext.currentName();
        }
        if (_currToken == JsonToken.START_OBJECT) return "{";
        if (_currToken == JsonToken.END_OBJECT) return "}";
        if (_currToken == JsonToken.START_ARRAY) return "[";
        if (_currToken == JsonToken.END_ARRAY) return "]";
        if (_currToken == JsonToken.VALUE_TRUE) return "true";
        if (_currToken == JsonToken.VALUE_FALSE) return "false";
        if (_currToken == JsonToken.VALUE_NULL) return "null";
        try {
            return delegate.readString();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public char[] getStringCharacters() throws JacksonException {
        String str = getString();
        return str != null ? str.toCharArray() : null;
    }

    @Override
    public int getStringLength() throws JacksonException {
        String str = getString();
        return str != null ? str.length() : 0;
    }

    @Override
    public int getStringOffset() throws JacksonException {
        return 0;
    }

    // ========================================================================
    // JsonParser — numeric value access (bypass ParserBase number parsing)
    // ========================================================================

    @Override
    public Number getNumberValue() throws JacksonException {
        if (_currToken == JsonToken.VALUE_NUMBER_INT) {
            return getLongValue();
        }
        if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            return getDoubleValue();
        }
        return null;
    }

    @Override
    public NumberType getNumberType() {
        if (_currToken == JsonToken.VALUE_NUMBER_INT) {
            return NumberType.LONG;
        }
        if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            return NumberType.DOUBLE;
        }
        return null;
    }

    @Override
    public int getIntValue() throws JacksonException {
        try {
            return delegate.readInt();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public long getLongValue() throws JacksonException {
        try {
            return delegate.readLong();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public BigInteger getBigIntegerValue() throws JacksonException {
        try {
            return delegate.readBigInteger();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public float getFloatValue() throws JacksonException {
        try {
            return delegate.readFloat();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public double getDoubleValue() throws JacksonException {
        try {
            return delegate.readDouble();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public BigDecimal getDecimalValue() throws JacksonException {
        try {
            return delegate.readBigDecimal();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    // ========================================================================
    // JsonParser — boolean access
    // ========================================================================

    @Override
    public boolean getBooleanValue() throws JacksonException {
        if (_currToken == JsonToken.VALUE_TRUE) return true;
        if (_currToken == JsonToken.VALUE_FALSE) return false;
        throw _constructReadException("Current token (%s) not of boolean type", _currToken);
    }

    // ========================================================================
    // JsonParser — binary access
    // ========================================================================

    @Override
    public byte[] getBinaryValue(Base64Variant variant) throws JacksonException {
        try {
            return delegate.readBinary();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    public Object getEmbeddedObject() {
        if (_currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
            try {
                return delegate.readBinary();
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            }
        }
        return null;
    }

    // ========================================================================
    // ParserBase — abstract method implementations
    // ========================================================================

    @Override
    protected void _parseNumericValue(int expType) throws JacksonException {
        // Not used — we override all numeric accessors directly
        if (_currToken == JsonToken.VALUE_NUMBER_INT) {
            try {
                _numberLong = delegate.readLong();
                _numTypesValid = NR_LONG;
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            }
        } else if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            try {
                _numberDouble = delegate.readDouble();
                _numTypesValid = NR_DOUBLE;
            } catch (IOException e) {
                throw _wrapIOFailure(e);
            }
        } else {
            _reportError("Current token (%s) not numeric", _currToken);
        }
    }

    @Override
    protected int _parseIntValue() throws JacksonException {
        try {
            _numberInt = delegate.readInt();
            _numTypesValid = NR_INT;
            return _numberInt;
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    protected void _closeInput() throws IOException {
        delegate.close();
    }
}
