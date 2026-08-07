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
package org.eclipse.fennec.codec.format.jackson;

import org.eclipse.fennec.codec.format.impl.TokenTypeMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.eclipse.fennec.codec.format.FormatDelegate;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteCapability;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.Version;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.SimpleStreamWriteContext;

/**
 * Jackson {@link JsonGenerator} implementation that delegates all write
 * operations to a {@link FormatDelegate}.
 * <p>
 * This bridge allows existing codec entry classes (which use Jackson's
 * {@code JsonGenerator} API) to transparently write to any format that
 * provides a {@code FormatDelegate} implementation — without any changes
 * to the entry classes themselves.
 * <p>
 * Usage:
 * <pre>
 * FormatDelegate&lt;OutputStream&gt; delegate = new MyFormatDelegate();
 * delegate.setTarget(outputStream);
 *
 * FormatDelegateGenerator&lt;OutputStream&gt; gen = FormatDelegateGenerator.create(
 *         ObjectWriteContext.empty(), ioContext, delegate);
 *
 * // Use as a standard JsonGenerator
 * gen.writeStartObject();
 * gen.writeName("name");
 * gen.writeString("value");
 * gen.writeEndObject();
 * gen.close();
 * </pre>
 *
 * @param <T> the output target type of the wrapped delegate
 * @see FormatDelegate
 * @see TokenTypeMapper
 * @since 2026-02-16
 */
public class FormatDelegateGenerator<T> extends GeneratorBase {

    private static final Version VERSION = new Version(1, 0, 0, "SNAPSHOT",
            "org.eclipse.fennec", "fennec-codec");

    private final FormatDelegate<T> delegate;
    private SimpleStreamWriteContext _writeContext;

    // ========================================================================
    // Construction
    // ========================================================================

    /**
     * Creates a new generator wrapping the given delegate.
     *
     * @param writeCtxt the Jackson object write context
     * @param ioCtxt the Jackson I/O context
     * @param streamWriteFeatures the stream write feature flags
     * @param delegate the format delegate to wrap
     */
    protected FormatDelegateGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt,
            int streamWriteFeatures, FormatDelegate<T> delegate) {
        super(writeCtxt, ioCtxt, streamWriteFeatures);
        this.delegate = delegate;
        DupDetector dups = StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamWriteFeatures)
                ? DupDetector.rootDetector(this) : null;
        _writeContext = SimpleStreamWriteContext.createRootContext(dups);
    }

    /**
     * Factory method to create a generator with default stream write features.
     *
     * @param <T> the output target type
     * @param writeCtxt the Jackson object write context
     * @param ioCtxt the Jackson I/O context
     * @param delegate the format delegate to wrap
     * @return a new generator instance
     */
    public static <T> FormatDelegateGenerator<T> create(ObjectWriteContext writeCtxt,
            IOContext ioCtxt, FormatDelegate<T> delegate) {
        return new FormatDelegateGenerator<>(writeCtxt, ioCtxt, 0, delegate);
    }

    /**
     * Returns the wrapped format delegate.
     *
     * @return the delegate
     */
    public FormatDelegate<T> getDelegate() {
        return delegate;
    }

    /**
     * Returns whether the wrapped delegate writes a native ObjectId type.
     *
     * @return {@code true} if {@link #writeObjectId(Object)} produces a native value
     */
    public boolean supportsNativeObjectId() {
        return delegate.supportsNativeObjectId();
    }

    /**
     * Writes a native ObjectId value through the delegate.
     *
     * @param value the ObjectId value (delegate-specific type or its string form)
     */
    public JsonGenerator writeObjectId(Object value) throws JacksonException {
        try {
            delegate.writeObjectId(value);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    /**
     * Returns whether the wrapped delegate writes a native date-time type.
     *
     * @return {@code true} if {@link #writeDateTime(long)} produces a native value
     */
    public boolean supportsNativeDateTime() {
        return delegate.supportsNativeDateTime();
    }

    /**
     * Writes a native date-time value (epoch milliseconds, UTC) through the delegate.
     *
     * @param epochMillis the instant as milliseconds since the epoch, UTC
     * @return this generator
     */
    public JsonGenerator writeDateTime(long epochMillis) throws JacksonException {
        try {
            delegate.writeDateTime(epochMillis);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    // ========================================================================
    // JsonGenerator — metadata / context
    // ========================================================================

    @Override
    public Version version() {
        return VERSION;
    }

    @Override
    public TokenStreamContext streamWriteContext() {
        return _writeContext;
    }

    @Override
    public Object currentValue() {
        return _writeContext.currentValue();
    }

    @Override
    public void assignCurrentValue(Object v) {
        _writeContext.assignCurrentValue(v);
    }

    @Override
    public Object streamWriteOutputTarget() {
        return delegate.getTarget();
    }

    @Override
    public int streamWriteOutputBuffered() {
        return 0;
    }

    @Override
    public JacksonFeatureSet<StreamWriteCapability> streamWriteCapabilities() {
        return DEFAULT_WRITE_CAPABILITIES;
    }

    // ========================================================================
    // JsonGenerator — structural write methods
    // ========================================================================

    @Override
    public JsonGenerator writeStartObject() throws JacksonException {
        _writeContext = _writeContext.createChildObjectContext(null);
        try {
            delegate.writeStartObject();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(Object currentValue) throws JacksonException {
        _writeContext = _writeContext.createChildObjectContext(currentValue);
        try {
            delegate.writeStartObject();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeEndObject() throws JacksonException {
        _writeContext = _writeContext.clearAndGetParent();
        try {
            delegate.writeEndObject();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeStartArray() throws JacksonException {
        _writeContext = _writeContext.createChildArrayContext(null);
        try {
            delegate.writeStartArray();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(Object currentValue) throws JacksonException {
        _writeContext = _writeContext.createChildArrayContext(currentValue);
        try {
            delegate.writeStartArray();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeEndArray() throws JacksonException {
        _writeContext = _writeContext.clearAndGetParent();
        try {
            delegate.writeEndArray();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    // ========================================================================
    // JsonGenerator — field name
    // ========================================================================

    @Override
    public JsonGenerator writeName(String name) throws JacksonException {
        _writeContext.writeName(name);
        try {
            delegate.writeName(name);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writePropertyId(long id) throws JacksonException {
        return writeName(Long.toString(id));
    }

    // ========================================================================
    // JsonGenerator — string / raw write methods
    // ========================================================================

    @Override
    public JsonGenerator writeString(String value) throws JacksonException {
        try {
            delegate.writeString(value);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeString(char[] buffer, int offset, int len) throws JacksonException {
        return writeString(new String(buffer, offset, len));
    }

    @Override
    public JsonGenerator writeRawUTF8String(byte[] buffer, int offset, int len) throws JacksonException {
        return writeString(new String(buffer, offset, len, StandardCharsets.UTF_8));
    }

    @Override
    public JsonGenerator writeUTF8String(byte[] buffer, int offset, int len) throws JacksonException {
        return writeString(new String(buffer, offset, len, StandardCharsets.UTF_8));
    }

    @Override
    public JsonGenerator writeRaw(String text) throws JacksonException {
        try {
            delegate.writeString(text);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeRaw(String text, int offset, int len) throws JacksonException {
        return writeRaw(text.substring(offset, offset + len));
    }

    @Override
    public JsonGenerator writeRaw(char[] buffer, int offset, int len) throws JacksonException {
        return writeRaw(new String(buffer, offset, len));
    }

    @Override
    public JsonGenerator writeRaw(char c) throws JacksonException {
        return writeRaw(String.valueOf(c));
    }

    // ========================================================================
    // JsonGenerator — number write methods
    // ========================================================================

    @Override
    public JsonGenerator writeNumber(short v) throws JacksonException {
        try {
            delegate.writeInt(v);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(int v) throws JacksonException {
        try {
            delegate.writeInt(v);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(long v) throws JacksonException {
        try {
            delegate.writeLong(v);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(BigInteger v) throws JacksonException {
        try {
            delegate.writeBigInteger(v);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(double v) throws JacksonException {
        try {
            delegate.writeDouble(v);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(float v) throws JacksonException {
        try {
            delegate.writeFloat(v);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(BigDecimal v) throws JacksonException {
        try {
            delegate.writeBigDecimal(v);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNumber(String encodedValue) throws JacksonException {
        // Number encoded as string — delegate as string
        try {
            delegate.writeString(encodedValue);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    // ========================================================================
    // JsonGenerator — boolean / null / binary
    // ========================================================================

    @Override
    public JsonGenerator writeBoolean(boolean state) throws JacksonException {
        try {
            delegate.writeBoolean(state);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeNull() throws JacksonException {
        try {
            delegate.writeNull();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    @Override
    public JsonGenerator writeBinary(Base64Variant bv, byte[] data, int offset, int len)
            throws JacksonException {
        byte[] actual;
        if (offset == 0 && len == data.length) {
            actual = data;
        } else {
            actual = new byte[len];
            System.arraycopy(data, offset, actual, 0, len);
        }
        try {
            delegate.writeBinary(actual);
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
        return this;
    }

    // ========================================================================
    // JsonGenerator — flush / lifecycle
    // ========================================================================

    @Override
    public void flush() {
        try {
            delegate.flush();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    // ========================================================================
    // GeneratorBase — abstract method implementations
    // ========================================================================

    @Override
    protected void _closeInput() throws IOException {
        delegate.close();
    }

    @Override
    protected void _releaseBuffers() {
        // no internal buffers to release
    }

    @Override
    protected void _verifyValueWrite(String typeMsg) throws JacksonException {
        // no-op — the delegate handles its own state
    }
}
