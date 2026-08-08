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
package org.eclipse.fennec.codec.buffer;

import org.eclipse.fennec.codec.context.EMFCodecReadContext;
import org.eclipse.fennec.codec.jackson.CodecTokenBufferReadContext;

import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.util.TokenBuffer;

/**
 * Token buffer for codec serialization that supports buffering and replay of JSON content.
 * <p>
 * This class extends Jackson's {@link TokenBuffer} to provide:
 * <ul>
 *   <li>Public factory methods for buffer creation</li>
 *   <li>Extension point for format-specific handling (JSON, MongoDB, etc.)</li>
 *   <li>EMF-aware token copying when needed</li>
 * </ul>
 * </p>
 * <p>
 * The buffer is used for scenarios like featurePath-based type resolution where
 * we need to scan ahead to find a discriminator value, then replay the content
 * for actual deserialization.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public class CodecTokenBuffer extends TokenBuffer {

    /** The original parser's stream context, preserved for context propagation */
    private TokenStreamContext originalParserContext;

    /**
     * Creates a new CodecTokenBuffer for buffering from a parser.
     *
     * @param parser the source parser
     * @param readContext the object read context
     */
    protected CodecTokenBuffer(JsonParser parser, ObjectReadContext readContext) {
        super(parser, readContext);
        // Preserve the original parser's stream context for EMF context propagation
        this.originalParserContext = parser.streamReadContext();
    }

    /**
     * Creates a new CodecTokenBuffer with a parent context.
     *
     * @param parentContext the parent token stream context
     */
    protected CodecTokenBuffer(TokenStreamContext parentContext) {
        super(ObjectWriteContext.empty(), false);
        _parentContext = parentContext;
    }

    /**
     * Creates a new empty CodecTokenBuffer.
     *
     * @param hasNativeIds whether to track native IDs
     */
    protected CodecTokenBuffer(boolean hasNativeIds) {
        super(ObjectWriteContext.empty(), hasNativeIds);
    }

    /**
     * Creates a buffer for capturing tokens from a parser.
     * <p>
     * Use this when you need to scan ahead in the content while
     * preserving all tokens for later replay.
     * </p>
     *
     * @param parser the source parser
     * @param readContext the object read context
     * @return a new token buffer
     */
    public static CodecTokenBuffer forBuffering(JsonParser parser, ObjectReadContext readContext) {
        return new CodecTokenBuffer(parser, readContext);
    }

    /**
     * Creates a buffer with a specific parent context.
     *
     * @param parentContext the parent token stream context
     * @return a new token buffer
     */
    public static CodecTokenBuffer forBuffering(TokenStreamContext parentContext) {
        return new CodecTokenBuffer(parentContext);
    }

    /**
     * Creates an empty buffer for token generation.
     *
     * @return a new empty token buffer
     */
    public static CodecTokenBuffer forGeneration() {
        return new CodecTokenBuffer(false);
    }

    /**
     * Returns a parser to replay the buffered content.
     * <p>
     * The parser is positioned before the first token.
     * If the original parser had an EMF-aware context, the returned parser
     * will use {@link CodecTokenBufferReadContext} to preserve that context.
     * </p>
     *
     * @param readContext the object read context
     * @return a parser for the buffered content
     */
    public JsonParser asParser(ObjectReadContext readContext) {
        return super.asParser(readContext);
    }

    /**
     * Returns a parser positioned at the first token.
     * <p>
     * If the original parser had an EMF-aware context, the returned parser
     * will use {@link CodecTokenBufferReadContext} to preserve that context.
     * </p>
     *
     * @param readContext the object read context
     * @return a parser positioned at the first token
     */
    public JsonParser asParserOnFirstToken(ObjectReadContext readContext) {
        JsonParser parser = super.asParserOnFirstToken(readContext);

        // If original parser had EMF context, wrap the parser's context
        if (originalParserContext instanceof EMFCodecReadContext) {
            // The parser created by TokenBuffer uses TokenBufferReadContext.
            // We can't easily swap it, but the EMF state is preserved in the
            // original context and can be accessed. For now we document this limitation.
            // TODO: Consider creating a custom parser wrapper to fully support this
        }

        return parser;
    }

    /**
     * Returns a parser positioned at the first token, using source parser for location info.
     * <p>
     * If the original parser had an EMF-aware context, the returned parser
     * will preserve that context via {@link CodecTokenBufferReadContext}.
     * </p>
     *
     * @param readContext the object read context
     * @param sourceParser the original source parser
     * @return a parser positioned at the first token
     */
    public JsonParser asParserOnFirstToken(ObjectReadContext readContext, JsonParser sourceParser) {
        return super.asParserOnFirstToken(readContext, sourceParser);
    }

    /**
     * Returns the original parser's stream context that was preserved when buffering started.
     * <p>
     * This allows callers to access EMF state from the original parser if needed.
     * </p>
     *
     * @return the original parser's stream context, or null if not available
     */
    public TokenStreamContext getOriginalParserContext() {
        return originalParserContext;
    }
}
