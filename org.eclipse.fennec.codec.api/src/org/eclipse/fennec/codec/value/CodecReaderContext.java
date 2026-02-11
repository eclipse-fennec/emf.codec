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

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

/**
 * Context for custom value readers during deserialization.
 * <p>
 * This context provides access to:
 * <ul>
 *   <li>The Jackson parser for reading JSON tokens</li>
 *   <li>The Jackson deserialization context</li>
 *   <li>The effective codec configuration (merged from all sources)</li>
 *   <li>Diagnostic collector for warnings and errors</li>
 * </ul>
 * <p>
 * Example usage in a custom reader:
 * <pre>
 * public class ISODateReader implements AttributeValueReader&lt;Date&gt; {
 *     &#64;Override
 *     public Date read(CodecReaderContext ctx, EAttribute attr) throws IOException {
 *         try {
 *             return ISO_FORMAT.parse(ctx.getParser().getString());
 *         } catch (ParseException e) {
 *             ctx.addWarning("Invalid date format: " + e.getMessage());
 *             return null;
 *         }
 *     }
 * }
 * </pre>
 *
 * @see CodecValueReader
 * @see CodecWriterContext
 */
public interface CodecReaderContext {

    /**
     * Returns the Jackson parser for reading JSON tokens.
     *
     * @return the JSON parser, never null
     */
    JsonParser getParser();

    /**
     * Returns the Jackson deserialization context.
     *
     * @return the deserialization context, may be null in test scenarios
     */
    DeserializationContext getJacksonContext();

    /**
     * Returns the effective codec configuration.
     * <p>
     * The effective configuration is the result of merging all configuration
     * sources: EAnnotations, factory defaults, resource options, and load options.
     * Custom readers can use this to access configuration settings.
     *
     * @return the effective codec configuration, never null
     */
    EffectiveCodecConfig getConfig();

    /**
     * Returns the diagnostic collector for warnings and errors.
     *
     * @return the diagnostic collector, never null
     */
    DiagnosticCollector getDiagnostics();

    /**
     * Convenience method to add a warning diagnostic.
     *
     * @param message the warning message
     */
    default void addWarning(String message) {
        getDiagnostics().addWarning(message, "CodecValueReader");
    }

    /**
     * Convenience method to add an error diagnostic.
     *
     * @param message the error message
     */
    default void addError(String message) {
        getDiagnostics().addError(message, "CodecValueReader");
    }
}
