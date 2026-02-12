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

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

/**
 * Implementation of {@link CodecReaderContext} for custom value readers.
 * <p>
 * This context wraps the Jackson parser and deserialization context,
 * along with the effective codec configuration and diagnostic collector.
 * </p>
 *
 * @see CodecReaderContext
 * @see CodecValueReader
 */
public class CodecReaderContextImpl implements CodecReaderContext {

    private final JsonParser parser;
    private final DeserializationContext jacksonContext;
    private final EffectiveCodecConfig config;
    private final DiagnosticCollector diagnostics;

    /**
     * Creates a new reader context.
     *
     * @param parser the JSON parser, must not be null
     * @param jacksonContext the Jackson deserialization context (may be null in tests)
     * @param config the effective codec configuration (may be null in tests)
     * @param diagnostics the diagnostic collector (may be null in tests)
     */
    public CodecReaderContextImpl(JsonParser parser, DeserializationContext jacksonContext,
            EffectiveCodecConfig config, DiagnosticCollector diagnostics) {
        if (parser == null) {
            throw new IllegalArgumentException("Parser must not be null");
        }
        this.parser = parser;
        this.jacksonContext = jacksonContext;
        this.config = config;
        this.diagnostics = diagnostics;
    }

    @Override
    public JsonParser getParser() {
        return parser;
    }

    @Override
    public DeserializationContext getJacksonContext() {
        return jacksonContext;
    }

    @Override
    public EffectiveCodecConfig getConfig() {
        return config;
    }

    @Override
    public DiagnosticCollector getDiagnostics() {
        return diagnostics;
    }
}
