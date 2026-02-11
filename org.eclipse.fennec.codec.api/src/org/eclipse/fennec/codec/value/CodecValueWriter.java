/*
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.value;

import java.io.IOException;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Interface for custom value writers that transform values during serialization.
 * <p>
 * Value writers allow you to:
 * <ul>
 *   <li>Transform values before serialization</li>
 *   <li>Handle special data types (dates, binary, custom formats)</li>
 *   <li>Implement domain-specific encoding</li>
 *   <li>Handle both attribute values (EAttribute) and reference URIs (EReference)</li>
 *   <li>Access the effective codec configuration via context</li>
 *   <li>Report warnings and errors via diagnostics</li>
 * </ul>
 * <p>
 * Example implementation for attributes:
 * <pre>
 * public class ISO8601DateWriter implements CodecValueWriter&lt;Date, EAttribute&gt; {
 *     &#64;Override
 *     public String getName() {
 *         return "isoDate";
 *     }
 *
 *     &#64;Override
 *     public void write(Date value, EAttribute feature, CodecWriterContext ctx) throws IOException {
 *         ctx.getGenerator().writeString(ISO8601_FORMAT.format(value));
 *     }
 * }
 * </pre>
 * <p>
 * Example accessing configuration:
 * <pre>
 * public class ConfigAwareDateWriter implements CodecValueWriter&lt;Date, EAttribute&gt; {
 *     &#64;Override
 *     public void write(Date value, EAttribute feature, CodecWriterContext ctx) throws IOException {
 *         // Access effective configuration
 *         EffectiveCodecConfig config = ctx.getConfig();
 *         String format = config.getDateFormat().orElse("yyyy-MM-dd'T'HH:mm:ss'Z'");
 *         SimpleDateFormat sdf = new SimpleDateFormat(format);
 *         ctx.getGenerator().writeString(sdf.format(value));
 *     }
 * }
 * </pre>
 *
 * @param <T> the type of value to write (value type for attributes, EObject for references)
 * @param <F> the feature type (EAttribute or EReference)
 * @see CodecWriterContext
 * @see CodecValueReader
 * @see AttributeValueWriter
 * @see ReferenceValueWriter
 */
public interface CodecValueWriter<T, F extends EStructuralFeature> {

    /**
     * Returns the unique name for this writer.
     * <p>
     * The name is used for registration in {@link CodecValueRegistry} and
     * for referencing this writer in configuration (EAnnotations, options).
     *
     * @return the unique writer name, never null or empty
     */
    String getName();

    /**
     * Writes the value to the generator context.
     *
     * @param value the value to write (never null)
     * @param feature the EStructuralFeature being serialized (EAttribute or EReference)
     * @param ctx the writer context providing generator, config, and diagnostics
     * @throws IOException if an I/O error occurs
     */
    void write(T value, F feature, CodecWriterContext ctx) throws IOException;
}
