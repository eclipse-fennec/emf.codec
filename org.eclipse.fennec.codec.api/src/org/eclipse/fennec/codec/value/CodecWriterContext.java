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

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Context for custom value writers during serialization.
 * <p>
 * This context provides access to:
 * <ul>
 *   <li>The Jackson generator for writing JSON tokens</li>
 *   <li>The Jackson serialization context</li>
 *   <li>The effective codec configuration (merged from all sources)</li>
 *   <li>Diagnostic collector for warnings and errors</li>
 * </ul>
 * <p>
 * Example usage in a custom writer:
 * <pre>
 * public class ConfigAwareDateWriter implements AttributeValueWriter&lt;Date&gt; {
 *     &#64;Override
 *     public void write(Date value, EAttribute attr, CodecWriterContext ctx) throws IOException {
 *         // Access configuration to determine format
 *         EffectiveCodecConfig config = ctx.getConfig();
 *         String format = config.getDateFormat().orElse("yyyy-MM-dd'T'HH:mm:ss'Z'");
 *         SimpleDateFormat sdf = new SimpleDateFormat(format);
 *         ctx.getGenerator().writeString(sdf.format(value));
 *     }
 * }
 * </pre>
 *
 * @see CodecValueWriter
 * @see CodecReaderContext
 */
public interface CodecWriterContext {

    /**
     * Returns the Jackson generator for writing JSON tokens.
     *
     * @return the JSON generator, never null
     */
    JsonGenerator getGenerator();

    /**
     * Returns the Jackson serialization context.
     *
     * @return the serialization context, may be null in test scenarios
     */
    SerializationContext getJacksonContext();

    /**
     * Returns the effective codec configuration.
     * <p>
     * The effective configuration is the result of merging all configuration
     * sources: EAnnotations, factory defaults, resource options, and save options.
     * Custom writers can use this to access configuration settings.
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
        getDiagnostics().addWarning(message, "CodecValueWriter");
    }

    /**
     * Convenience method to add an error diagnostic.
     *
     * @param message the error message
     */
    default void addError(String message) {
        getDiagnostics().addError(message, "CodecValueWriter");
    }
}
