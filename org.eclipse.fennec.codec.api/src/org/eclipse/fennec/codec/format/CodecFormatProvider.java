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
     * Creates a new writer delegate, with optional access to the root
     * {@code EObject} being serialized and the save-time options map.
     * <p>
     * The default implementation simply delegates to {@link #createWriter(Object)},
     * so most format providers do not need to override this method. Providers
     * that need contextual information ahead of writing — for example, the CSV
     * provider, which derives a "type row" from the root {@code EClass} — can
     * override this overload to inspect {@code rootObject} and/or {@code saveOptions}
     * before constructing the delegate.
     *
     * @param target the output target
     * @param rootObject the root {@code EObject} being serialized, may be {@code null}
     * @param saveOptions the save-time option map, never {@code null} (may be empty)
     * @return a new writer delegate, never null
     * @throws IOException if the delegate cannot be created
     * @since 2026-05
     */
    default FormatDelegate<T> createWriter(T target,
            org.eclipse.emf.ecore.EObject rootObject,
            java.util.Map<String, Object> saveOptions) throws IOException {
        return createWriter(target);
    }

    /**
     * Creates a new writer delegate, with optional access to the full list of root
     * {@code EObject}s being serialized and the save-time options map.
     * <p>
     * The default implementation delegates to
     * {@link #createWriter(Object, org.eclipse.emf.ecore.EObject, java.util.Map)} with the
     * first element of {@code rootObjects} (or {@code null} if the list is empty), so
     * single-root providers don't need to override this. Providers that need to know
     * about all roots ahead of time — for example the CSV provider in {@code SQL_TABLES}
     * mode, which walks every root to build per-{@code EClass} matrices — should override
     * this overload.
     *
     * @param target the output target
     * @param rootObjects the root {@code EObject}s being serialized, never {@code null} (may be empty)
     * @param saveOptions the save-time option map, never {@code null} (may be empty)
     * @return a new writer delegate, never null
     * @throws IOException if the delegate cannot be created
     * @since 2026-05
     */
    default FormatDelegate<T> createWriter(T target,
            java.util.List<? extends org.eclipse.emf.ecore.EObject> rootObjects,
            java.util.Map<String, Object> saveOptions) throws IOException {
        org.eclipse.emf.ecore.EObject first = rootObjects.isEmpty() ? null : rootObjects.get(0);
        return createWriter(target, first, saveOptions);
    }

    /**
     * Creates a new writer delegate with access to the full operation
     * {@link org.eclipse.fennec.codec.config.ConfigurationResolver} as well as the
     * root {@code EObject}s and save-time options.
     * <p>
     * Most format providers don't need the resolver: their {@link FormatDelegate}
     * participates in the Jackson-driven serialization pipeline, and all option
     * resolution happens upstream in {@code CodecResource}/{@code CodecEObjectSerializer}.
     * The CSV provider's {@code SQL_TABLES}-mode delegate is the exception — it bypasses
     * the Jackson pipeline and walks the EMF graph directly, so it needs to apply
     * codec options (extended-metadata names, ignore/forceWrite, dateFormat, …)
     * itself.
     * <p>
     * The default implementation delegates to
     * {@link #createWriter(Object, java.util.List, java.util.Map)} and ignores the resolver,
     * keeping every existing provider working unchanged.
     *
     * @param target the output target
     * @param rootObjects the root {@code EObject}s being serialized, never {@code null} (may be empty)
     * @param saveOptions the save-time option map, never {@code null} (may be empty)
     * @param resolver the operation configuration resolver (already enriched with save options); may be {@code null}
     * @return a new writer delegate, never null
     * @throws IOException if the delegate cannot be created
     * @since 2026-05
     */
    default FormatDelegate<T> createWriter(T target,
            java.util.List<? extends org.eclipse.emf.ecore.EObject> rootObjects,
            java.util.Map<String, Object> saveOptions,
            org.eclipse.fennec.codec.config.ConfigurationResolver resolver) throws IOException {
        return createWriter(target, rootObjects, saveOptions);
    }

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

    /**
     * Returns whether this format supports multiple root objects (array root).
     * <p>
     * When {@code true}, the codec can serialize/deserialize resources with
     * multiple root objects as an array. When {@code false}, only single root
     * objects are supported and an error will be raised if the resource
     * contains more than one root object.
     * <p>
     * Most streaming formats (JSON, YAML, CBOR) support array root.
     * Document-based formats (BSON) typically do not.
     *
     * @return {@code true} if array root is supported, {@code false} otherwise
     */
    default boolean supportsArrayRoot() {
        return true;
    }

    /**
     * Validates the save-time options against the resource URI and returns any
     * inconsistency warnings. Default: empty list (no warnings).
     * <p>
     * The caller ({@code CodecResource.doSave(...)}) logs returned messages at
     * WARN level but proceeds with the save. Providers should use this hook to
     * surface mismatches between the URI shape and option values that would
     * produce surprising output — for example, a {@code .RData} URI saved with
     * {@code codec.rlang.dataframePerFile=true} (the bytes will be a ZIP
     * archive named {@code .RData}), or a {@code .csv} URI saved with
     * {@code codec.tabular.referenceMode=SQL_TABLES} (the bytes will be a ZIP
     * of CSVs).
     * <p>
     * The {@code uri} may be {@code null} (e.g. in unit tests or programmatic
     * saves with no Resource); implementations should handle that gracefully
     * and return an empty list in that case.
     *
     * @param uri the resource URI being saved, possibly {@code null}
     * @param options the effective save-time option map (already merged with
     *            any provider-level defaults), never {@code null} (may be empty)
     * @return a list of warning messages; never {@code null}, possibly empty
     * @since 2026-06
     */
    default java.util.List<String> validateSaveOptions(
            org.eclipse.emf.common.util.URI uri,
            java.util.Map<String, Object> options) {
        return java.util.List.of();
    }
}
