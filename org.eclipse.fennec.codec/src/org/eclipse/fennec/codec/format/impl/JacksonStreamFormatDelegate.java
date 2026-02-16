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
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.fennec.codec.format.FormatDelegate;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamFactory;

/**
 * {@link FormatDelegate} implementation that wraps a Jackson {@link JsonGenerator}.
 * <p>
 * This allows any Jackson-based streaming format (JSON, CBOR, Smile, YAML, etc.)
 * to be used through the format-agnostic {@code FormatDelegate} interface.
 * The specific format is determined by the {@link TokenStreamFactory} used to
 * create the generator.
 * <p>
 * Usage:
 * <pre>
 * JsonFactory factory = new JsonFactory();
 * OutputStream out = ...;
 * JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), out, JsonEncoding.UTF8);
 *
 * JacksonStreamFormatDelegate delegate = new JacksonStreamFormatDelegate(gen);
 * delegate.setTarget(out);
 * delegate.writeStartObject();
 * delegate.writeName("key");
 * delegate.writeString("value");
 * delegate.writeEndObject();
 * delegate.close();
 * </pre>
 *
 * @see FormatDelegate
 * @since 2026-02-16
 */
public class JacksonStreamFormatDelegate implements FormatDelegate<OutputStream> {

    private final JsonGenerator generator;
    private OutputStream target;

    /**
     * Creates a delegate wrapping the given Jackson generator.
     *
     * @param generator the Jackson generator to wrap
     */
    public JacksonStreamFormatDelegate(JsonGenerator generator) {
        this.generator = generator;
    }

    /**
     * Returns the wrapped Jackson generator.
     *
     * @return the generator
     */
    public JsonGenerator getGenerator() {
        return generator;
    }

    // ========================================================================
    // Target Management
    // ========================================================================

    @Override
    public void setTarget(OutputStream target) {
        this.target = target;
    }

    @Override
    public OutputStream getTarget() {
        return target;
    }

    // ========================================================================
    // Structural writes
    // ========================================================================

    @Override
    public void writeStartObject() throws IOException {
        generator.writeStartObject();
    }

    @Override
    public void writeEndObject() throws IOException {
        generator.writeEndObject();
    }

    @Override
    public void writeStartArray() throws IOException {
        generator.writeStartArray();
    }

    @Override
    public void writeEndArray() throws IOException {
        generator.writeEndArray();
    }

    // ========================================================================
    // Field name
    // ========================================================================

    @Override
    public void writeName(String name) throws IOException {
        generator.writeName(name);
    }

    // ========================================================================
    // Value writes
    // ========================================================================

    @Override
    public void writeString(String value) throws IOException {
        generator.writeString(value);
    }

    @Override
    public void writeInt(int value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void writeLong(long value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void writeBigInteger(BigInteger value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void writeBigDecimal(BigDecimal value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        generator.writeBoolean(value);
    }

    @Override
    public void writeNull() throws IOException {
        generator.writeNull();
    }

    @Override
    public void writeBinary(byte[] data) throws IOException {
        generator.writeBinary(data);
    }

    // ========================================================================
    // Lifecycle
    // ========================================================================

    @Override
    public void flush() throws IOException {
        generator.flush();
    }

    @Override
    public void close() throws IOException {
        generator.close();
    }
}
