/**
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
package org.eclipse.fennec.codec.jackson;

import java.io.InputStream;

import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonFactoryBuilder;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;

/**
 * Custom JSON factory that creates {@link CodecJsonParser} instances.
 * <p>
 * This factory extends {@link JsonFactory} to produce parsers that use
 * {@link CodecJsonReadContext} as their stream context, enabling EMF-aware
 * deserialization with proper type resolution and context tracking.
 * </p>
 * <p>
 * Usage:
 * </p>
 * <pre>
 * EffectiveCodecConfig config = EffectiveCodecConfig.builder()
 *     .resolver(resolver)
 *     .diagnostics(diagnostics)
 *     .metadataService(metadataService)
 *     .build();
 * CodecJsonFactory factory = CodecJsonFactory.builder()
 *     .effectiveConfig(config)
 *     .build();
 * JsonParser parser = factory.createParser(inputStream);
 * </pre>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public class CodecJsonFactory extends JsonFactory {

    private static final long serialVersionUID = 1L;

    /**
     * The effective codec configuration used during deserialization.
     */
    private final EffectiveCodecConfig effectiveConfig;

    /**
     * Creates a new codec JSON factory.
     *
     * @param builder the factory builder
     * @param effectiveConfig the effective codec configuration
     */
    protected CodecJsonFactory(JsonFactoryBuilder builder, EffectiveCodecConfig effectiveConfig) {
        super(builder);
        this.effectiveConfig = effectiveConfig;
    }

    /**
     * Creates a new codec JSON factory with default settings.
     *
     * @param effectiveConfig the effective codec configuration
     */
    public CodecJsonFactory(EffectiveCodecConfig effectiveConfig) {
        super();
        this.effectiveConfig = effectiveConfig;
    }

    /**
     * Returns a new builder for creating CodecJsonFactory instances.
     *
     * @return the builder
     */
    public static CodecJsonFactoryBuilder builder() {
        return new CodecJsonFactoryBuilder();
    }

    /**
     * Returns the effective codec configuration used by this factory.
     *
     * @return the effective configuration
     */
    public EffectiveCodecConfig getEffectiveConfig() {
        return effectiveConfig;
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
            InputStream in) throws JacksonException {
        try {
            ByteQuadsCanonicalizer can = _byteSymbolCanonicalizer.makeChild(_factoryFeatures);
            return new CodecJsonParser(readCtxt, ioCtxt,
                    _streamReadFeatures, _formatReadFeatures, in, can,
                    ioCtxt.allocReadIOBuffer(), 0, 0, 0, true, effectiveConfig);
        } catch (RuntimeException e) {
            // For [core#763] may need to close InputStream here
            if (ioCtxt.isResourceManaged()) {
                try {
                    in.close();
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                }
            }
            throw e;
        }
    }

    /**
     * Builder for {@link CodecJsonFactory}.
     */
    public static class CodecJsonFactoryBuilder extends JsonFactoryBuilder {

        private EffectiveCodecConfig effectiveConfig;

        /**
         * Sets the effective codec configuration.
         *
         * @param effectiveConfig the effective configuration
         * @return this builder
         */
        public CodecJsonFactoryBuilder effectiveConfig(EffectiveCodecConfig effectiveConfig) {
            this.effectiveConfig = effectiveConfig;
            return this;
        }

        @Override
        public CodecJsonFactory build() {
            return new CodecJsonFactory(this, effectiveConfig);
        }
    }
}
