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
package org.eclipse.fennec.codec.jackson;

import java.io.InputStream;

import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;

import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.UTF8StreamJsonParser;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;

/**
 * Custom JSON parser that uses {@link CodecJsonReadContext} as its stream context.
 * <p>
 * This parser extends {@link UTF8StreamJsonParser} to provide EMF-aware parsing
 * with proper context tracking for type resolution and nested object handling.
 * </p>
 * <p>
 * Deserializers can access the EMF context via:
 * </p>
 * <pre>
 * if (parser.streamReadContext() instanceof CodecJsonReadContext ctx) {
 *     EClass typeHint = ctx.getCurrentTypeHint();
 *     EClass resolved = ctx.resolveEClass(typeValue, typeHint);
 * }
 * </pre>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public class CodecJsonParser extends UTF8StreamJsonParser {

    /**
     * Creates a new codec JSON parser.
     *
     * @param readCtxt the object read context
     * @param ctxt the IO context
     * @param streamReadFeatures stream read features
     * @param formatReadFeatures format read features
     * @param in the input stream
     * @param sym the byte quads canonicalizer
     * @param inputBuffer the input buffer
     * @param start the start position in buffer
     * @param end the end position in buffer
     * @param bytesPreProcessed bytes already processed
     * @param bufferRecyclable whether the buffer is recyclable
     * @param effectiveConfig the effective codec configuration
     */
    public CodecJsonParser(ObjectReadContext readCtxt, IOContext ctxt, int streamReadFeatures,
            int formatReadFeatures, InputStream in, ByteQuadsCanonicalizer sym, byte[] inputBuffer,
            int start, int end, int bytesPreProcessed, boolean bufferRecyclable,
            EffectiveCodecConfig effectiveConfig) {
        super(readCtxt, ctxt, streamReadFeatures, formatReadFeatures, in, sym, inputBuffer,
                start, end, bytesPreProcessed, bufferRecyclable);

        // Create duplicate detector if enabled
        DupDetector dups = StreamReadFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamReadFeatures)
                ? DupDetector.rootDetector(this) : null;

        // Replace the default stream context with our EMF-aware context
        _streamReadContext = CodecJsonReadContext.createRootContext(dups, effectiveConfig);
    }

    @Override
    public CodecJsonReadContext streamReadContext() {
        return (CodecJsonReadContext) _streamReadContext;
    }
}
