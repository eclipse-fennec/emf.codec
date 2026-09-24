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

import org.eclipse.fennec.codec.format.impl.JacksonStreamFormatReaderDelegate;

import org.eclipse.fennec.codec.format.impl.JacksonStreamFormatDelegate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;

import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadConstraints;
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
 * @since 1.0
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
        return createReader(factory, source);
    }

    /**
     * Creates a reader bounded by the read limits of this load (issue #232).
     * <p>
     * The parser comes from this provider's own factory, so the limits {@code CodecResource}
     * resolved never reached it before: CBOR and YAML ran on Jackson's defaults. The factory is
     * rebuilt with the handed-over {@code StreamReadConstraints} for this one reader.
     * </p>
     */
    @Override
    public FormatReaderDelegate<InputStream> createReader(InputStream source, Map<String, Object> loadOptions)
            throws IOException {
        Object limits = loadOptions == null ? null : loadOptions.get(CodecOptions.INTERNAL_STREAM_READ_CONSTRAINTS);
        TokenStreamFactory readFactory = limits instanceof StreamReadConstraints constraints
                ? limitedFactory(constraints) : factory;
        return createReader(readFactory, source);
    }

    /**
     * Returns a factory like this provider's, bounded by the given limits. A format whose parser
     * enforces a limit through its own setting overrides this to map it there as well - YAML
     * bounds the document size by its code point limit, not by {@code maxDocumentLength}.
     *
     * @param constraints the read limits of this load
     * @return the factory to parse this load with
     */
    protected TokenStreamFactory limitedFactory(StreamReadConstraints constraints) {
        return factory.rebuild().streamReadConstraints(constraints).build();
    }

    private static FormatReaderDelegate<InputStream> createReader(TokenStreamFactory readFactory, InputStream source) {
        JsonParser parser = readFactory.createParser(ObjectReadContext.empty(), source);
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
