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

/**
 * Factory interface for creating format-specific reader and writer delegates.
 * <p>
 * Each format (JSON, BSON, CBOR, Lucene, etc.) provides an implementation
 * of this interface that creates the appropriate {@link FormatDelegate} and
 * {@link FormatReaderDelegate} instances.
 * <p>
 * The generic types define the I/O targets for the format:
 * <ul>
 *   <li>{@code S} — the source type for reading (e.g., {@code InputStream}, {@code BsonDocument})</li>
 *   <li>{@code T} — the target type for writing (e.g., {@code OutputStream}, {@code BsonDocument})</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>
 * // JSON format (streaming)
 * CodecFormatProvider&lt;InputStream, OutputStream&gt; jsonFormat = new JacksonFormatProvider();
 *
 * // BSON format (in-memory)
 * CodecFormatProvider&lt;BsonDocument, BsonDocument&gt; bsonFormat = new BsonFormatProvider();
 *
 * // Create writer and serialize
 * FormatDelegate&lt;OutputStream&gt; writer = jsonFormat.createWriter(outputStream);
 * // ... use writer to serialize EObject ...
 * </pre>
 *
 * @param <S> the input source type for reading
 * @param <T> the output target type for writing
 * @see FormatDelegate
 * @see FormatReaderDelegate
 * @since 2026-02-16
 */
public interface CodecFormatProvider<S, T> {

    /**
     * Returns the unique identifier for this format.
     * <p>
     * Examples: "json", "cbor", "bson", "yaml", "lucene"
     *
     * @return the format identifier, never null
     */
    String getFormatId();

    /**
     * Creates a new writer delegate for the given output target.
     *
     * @param target the output target
     * @return a new writer delegate, never null
     * @throws IOException if the delegate cannot be created
     */
    FormatDelegate<T> createWriter(T target) throws IOException;

    /**
     * Creates a new reader delegate for the given input source.
     *
     * @param source the input source
     * @return a new reader delegate, never null
     * @throws IOException if the delegate cannot be created
     */
    FormatReaderDelegate<S> createReader(S source) throws IOException;

    /**
     * Returns the file extensions associated with this format.
     * <p>
     * Examples: {@code ["json"]}, {@code ["cbor"]}, {@code ["bson"]}
     *
     * @return array of file extensions (without dots), never null
     */
    default String[] getFileExtensions() {
        return new String[] { getFormatId() };
    }

    /**
     * Returns the content types associated with this format.
     * <p>
     * Examples: {@code ["application/json"]}, {@code ["application/cbor"]}
     *
     * @return array of content types, never null
     */
    default String[] getContentTypes() {
        return new String[0];
    }
}
