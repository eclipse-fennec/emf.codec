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
import java.io.OutputStream;

import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;

import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.TokenStreamFactory;

/**
 * {@link CodecFormatProvider} implementation for Jackson-based streaming formats.
 * <p>
 * This provider works with any Jackson {@link TokenStreamFactory} — including
 * {@code JsonFactory}, {@code CBORFactory}, {@code SmileFactory}, {@code YAMLFactory},
 * etc. The specific format is determined by the factory passed to the constructor.
 * <p>
 * Usage:
 * <pre>
 * // JSON
 * JacksonFormatProvider jsonProvider = new JacksonFormatProvider("json", new JsonFactory());
 *
 * // CBOR (when jackson-dataformat-cbor is on classpath)
 * JacksonFormatProvider cborProvider = new JacksonFormatProvider("cbor", new CBORFactory());
 *
 * FormatDelegate&lt;OutputStream&gt; writer = jsonProvider.createWriter(outputStream);
 * FormatReaderDelegate&lt;InputStream&gt; reader = jsonProvider.createReader(inputStream);
 * </pre>
 *
 * @see CodecFormatProvider
 * @see JacksonStreamFormatDelegate
 * @see JacksonStreamFormatReaderDelegate
 * @since 2026-02-16
 */
public class JacksonFormatProvider implements CodecFormatProvider<InputStream, OutputStream> {

    private final String formatId;
    private final TokenStreamFactory factory;
    private final String[] fileExtensions;
    private final String[] contentTypes;

    /**
     * Creates a provider with the given format ID and Jackson factory.
     *
     * @param formatId the format identifier (e.g., "json", "cbor", "yaml")
     * @param factory the Jackson token stream factory
     */
    public JacksonFormatProvider(String formatId, TokenStreamFactory factory) {
        this(formatId, factory, new String[]{formatId}, new String[0]);
    }

    /**
     * Creates a provider with full configuration.
     *
     * @param formatId the format identifier
     * @param factory the Jackson token stream factory
     * @param fileExtensions the file extensions for this format
     * @param contentTypes the content types for this format
     */
    public JacksonFormatProvider(String formatId, TokenStreamFactory factory,
            String[] fileExtensions, String[] contentTypes) {
        this.formatId = formatId;
        this.factory = factory;
        this.fileExtensions = fileExtensions;
        this.contentTypes = contentTypes;
    }

    /**
     * Returns the Jackson factory used by this provider.
     *
     * @return the token stream factory
     */
    public TokenStreamFactory getFactory() {
        return factory;
    }

    // ========================================================================
    // CodecFormatProvider
    // ========================================================================

    @Override
    public String getFormatId() {
        return formatId;
    }

    @Override
    public FormatDelegate<OutputStream> createWriter(OutputStream target) throws IOException {
        JsonGenerator gen = factory.createGenerator(
                ObjectWriteContext.empty(), target, JsonEncoding.UTF8);
        JacksonStreamFormatDelegate delegate = new JacksonStreamFormatDelegate(gen);
        delegate.setTarget(target);
        return delegate;
    }

    @Override
    public FormatReaderDelegate<InputStream> createReader(InputStream source) throws IOException {
        JsonParser parser = factory.createParser(
                ObjectReadContext.empty(), source);
        JacksonStreamFormatReaderDelegate delegate = new JacksonStreamFormatReaderDelegate(parser);
        delegate.setSource(source);
        return delegate;
    }

    @Override
    public String[] getFileExtensions() {
        return fileExtensions;
    }

    @Override
    public String[] getContentTypes() {
        return contentTypes;
    }
}
