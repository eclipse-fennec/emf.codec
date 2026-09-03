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
package org.eclipse.fennec.codec.context;

import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.impl.CodecPrefixReaderContextImpl;
import org.eclipse.fennec.codec.value.impl.CodecPrefixWriterContextImpl;
import org.eclipse.fennec.codec.value.impl.CodecReaderContextImpl;
import org.eclipse.fennec.codec.prefix.CodecPrefixReaderContext;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriterContext;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.impl.CodecWriterContextImpl;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;

/**
 * Shared context for serialization and deserialization entries.
 * <p>
 * This context holds stable configuration that doesn't change during a
 * serialization or deserialization operation:
 * <ul>
 *   <li>{@link CodecValueRegistry} - for looking up custom value readers/writers</li>
 *   <li>{@link EffectiveCodecConfig} - merged configuration from all sources</li>
 *   <li>{@link DiagnosticCollector} - for collecting warnings/errors</li>
 * </ul>
 * <p>
 * The context is created once per Resource.save() or Resource.load() operation
 * and passed to all entry constructors. When a custom reader/writer needs to be
 * invoked, the entry calls {@link #createWriterContext} or {@link #createReaderContext}
 * to create the appropriate context with the Jackson parser/generator.
 * <p>
 * Example usage for serialization:
 * <pre>
 * // At Resource.save() time - create once
 * CodecEntryContext entryCtx = CodecEntryContext.builder()
 *     .valueRegistry(registry)
 *     .effectiveConfig(config)
 *     .diagnostics(diagnostics)
 *     .build();
 *
 * // Pass to entry constructor
 * new AttributeSerializationEntry(featureConfig, attribute, entryCtx);
 *
 * // In entry.serialize() - create writer context with generator
 * CodecWriterContext writerCtx = entryCtx.createWriterContext(gen, ctxt);
 * customWriter.write(value, attribute, writerCtx);
 * </pre>
 * <p>
 * Example usage for deserialization:
 * <pre>
 * // At Resource.load() time - create once
 * CodecEntryContext entryCtx = CodecEntryContext.builder()
 *     .valueRegistry(registry)
 *     .effectiveConfig(config)
 *     .diagnostics(diagnostics)
 *     .build();
 *
 * // Pass to entry constructor
 * new AttributeDeserializationEntry(featureConfig, attribute, entryCtx);
 *
 * // In entry.deserialize() - create reader context with parser
 * CodecReaderContext readerCtx = entryCtx.createReaderContext(parser, ctxt);
 * Object value = customReader.read(readerCtx, attribute);
 * </pre>
 *
 * @see CodecWriterContext
 * @see CodecReaderContext
 */
public final class CodecEntryContext {

    private final CodecValueRegistry valueRegistry;
    private final CodecPrefixRegistry prefixRegistry;
    private final EffectiveCodecConfig effectiveConfig;
    private final DiagnosticCollector diagnostics;

    private CodecEntryContext(Builder builder) {
        this.valueRegistry = builder.valueRegistry;
        this.prefixRegistry = builder.prefixRegistry != null ? builder.prefixRegistry : new CodecPrefixRegistry();
        // Allow null for tests - these are only needed when actually invoking custom readers/writers
        this.effectiveConfig = builder.effectiveConfig;
        this.diagnostics = builder.diagnostics != null ? builder.diagnostics : new DiagnosticCollector();
    }

    /**
     * Returns the value registry for looking up custom readers/writers.
     *
     * @return the value registry, may be null if no custom readers/writers are registered
     */
    public CodecValueRegistry getValueRegistry() {
        return valueRegistry;
    }

    /** The prefix registry of this operation (issue #193); never null. */
    public CodecPrefixRegistry getPrefixRegistry() {
        return prefixRegistry;
    }

    /**
     * Returns the effective codec configuration.
     *
     * @return the effective configuration, never null
     */
    public EffectiveCodecConfig getEffectiveConfig() {
        return effectiveConfig;
    }

    /**
     * Returns the diagnostic collector.
     *
     * @return the diagnostic collector, never null
     */
    public DiagnosticCollector getDiagnostics() {
        return diagnostics;
    }

    // ========================================================================
    // Factory methods for direction-specific contexts
    // ========================================================================

    /**
     * Creates a {@link CodecWriterContext} for use with custom value writers.
     * <p>
     * This combines the stable configuration from this context with the
     * Jackson generator and serialization context from the current write operation.
     *
     * @param generator the JSON generator for writing
     * @param jacksonContext the Jackson serialization context (may be null in tests)
     * @return a writer context for custom value writers
     */
    public CodecWriterContext createWriterContext(JsonGenerator generator, SerializationContext jacksonContext) {
        return new CodecWriterContextImpl(generator, jacksonContext, effectiveConfig, diagnostics);
    }

    /**
     * Creates a {@link CodecReaderContext} for use with custom value readers.
     * <p>
     * This combines the stable configuration from this context with the
     * Jackson parser and deserialization context from the current read operation.
     *
     * @param parser the JSON parser for reading
     * @param jacksonContext the Jackson deserialization context (may be null in tests)
     * @return a reader context for custom value readers
     */
    public CodecReaderContext createReaderContext(JsonParser parser, DeserializationContext jacksonContext) {
        return new CodecReaderContextImpl(parser, jacksonContext, effectiveConfig, diagnostics);
    }

    public CodecPrefixWriterContext createPrefixWriterContext(JsonGenerator generator, SerializationContext jacksonContext) {
        return new CodecPrefixWriterContextImpl(generator, jacksonContext, effectiveConfig, diagnostics);
    }

    public CodecPrefixReaderContext createPrefixReaderContext(JsonParser parser, DeserializationContext jacksonContext) {
        return new CodecPrefixReaderContextImpl(parser, jacksonContext, effectiveConfig, diagnostics);
    }

    // ========================================================================
    // Builder
    // ========================================================================

    /**
     * Creates a new builder for CodecEntryContext.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for CodecEntryContext.
     */
    public static final class Builder {
        private CodecValueRegistry valueRegistry;
        private CodecPrefixRegistry prefixRegistry;
        private EffectiveCodecConfig effectiveConfig;
        private DiagnosticCollector diagnostics;

        private Builder() {}

        /**
         * Sets the value registry for custom readers/writers.
         *
         * @param valueRegistry the registry (may be null)
         * @return this builder
         */
        public Builder valueRegistry(CodecValueRegistry valueRegistry) {
            this.valueRegistry = valueRegistry;
            return this;
        }

        public Builder prefixRegistry(CodecPrefixRegistry prefixRegistry) {
            this.prefixRegistry = prefixRegistry;
            return this;
        }

        /**
         * Sets the effective codec configuration.
         *
         * @param effectiveConfig the configuration (required)
         * @return this builder
         */
        public Builder effectiveConfig(EffectiveCodecConfig effectiveConfig) {
            this.effectiveConfig = effectiveConfig;
            return this;
        }

        /**
         * Sets the diagnostic collector.
         *
         * @param diagnostics the collector (required)
         * @return this builder
         */
        public Builder diagnostics(DiagnosticCollector diagnostics) {
            this.diagnostics = diagnostics;
            return this;
        }

        /**
         * Builds the context.
         *
         * @return the built context
         * @throws NullPointerException if effectiveConfig or diagnostics is null
         */
        public CodecEntryContext build() {
            return new CodecEntryContext(this);
        }
    }
}
