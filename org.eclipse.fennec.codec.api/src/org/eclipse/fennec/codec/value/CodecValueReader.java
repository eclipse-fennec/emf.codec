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
package org.eclipse.fennec.codec.value;

import java.io.IOException;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Interface for custom value readers that transform values during deserialization.
 * <p>
 * Value readers allow you to:
 * <ul>
 *   <li>Transform values after deserialization</li>
 *   <li>Parse special data types (dates, binary, custom formats)</li>
 *   <li>Implement domain-specific decoding</li>
 *   <li>Handle both attribute values (EAttribute) and reference URIs (EReference)</li>
 *   <li>Access the effective codec configuration via context</li>
 *   <li>Report warnings and errors via diagnostics</li>
 * </ul>
 * <p>
 * Example implementation for attributes:
 * <pre>
 * public class ISO8601DateReader implements CodecValueReader&lt;Date, EAttribute&gt; {
 *     &#64;Override
 *     public String getName() {
 *         return "isoDate";
 *     }
 *
 *     &#64;Override
 *     public Date read(CodecReaderContext ctx, EAttribute feature) throws IOException {
 *         try {
 *             return ISO8601_FORMAT.parse(ctx.getParser().getString());
 *         } catch (ParseException e) {
 *             ctx.addWarning("Invalid date format: " + e.getMessage());
 *             return null;
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * Example accessing configuration:
 * <pre>
 * public class ConfigAwareReader implements CodecValueReader&lt;String, EAttribute&gt; {
 *     &#64;Override
 *     public String read(CodecReaderContext ctx, EAttribute feature) throws IOException {
 *         // Access effective configuration
 *         EffectiveCodecConfig config = ctx.getConfig();
 *         boolean smartCompression = config.isSmartCompressionEnabled();
 *         // ... use configuration
 *     }
 * }
 * </pre>
 *
 * @param <T> the type of value to read (value type for attributes, String URI for references)
 * @param <F> the feature type (EAttribute or EReference)
 * @see CodecReaderContext
 * @see CodecValueWriter
 * @see AttributeValueReader
 * @see ReferenceValueReader
 */
public interface CodecValueReader<T, F extends EStructuralFeature> {

    /**
     * Returns the unique name for this reader.
     * <p>
     * The name is used for registration in {@link CodecValueRegistry} and
     * for referencing this reader in configuration (EAnnotations, options).
     *
     * @return the unique reader name, never null or empty
     */
    String getName();

    /**
     * Reads a value from the parser context.
     *
     * @param ctx the reader context providing parser, config, and diagnostics
     * @param feature the EStructuralFeature being deserialized (EAttribute or EReference)
     * @return the parsed value (may be null if the JSON value is null)
     * @throws IOException if an I/O error occurs
     */
    T read(CodecReaderContext ctx, F feature) throws IOException;
}
