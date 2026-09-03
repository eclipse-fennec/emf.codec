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
package org.eclipse.fennec.codec.value.impl;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.prefix.CodecPrefixReaderContext;
import org.eclipse.fennec.codec.value.EffectiveCodecConfig;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

/**
 * Default {@link CodecPrefixReaderContext}: a parser positioned at the value of the key - the
 * codec always hands the reader a parser over the buffered value, so a reader failure can never
 * leave the main stream half consumed (issue #193).
 */
public class CodecPrefixReaderContextImpl implements CodecPrefixReaderContext {

    private final JsonParser parser;
    private final DeserializationContext jacksonContext;
    private final EffectiveCodecConfig config;
    private final DiagnosticCollector diagnostics;

    public CodecPrefixReaderContextImpl(JsonParser parser, DeserializationContext jacksonContext,
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
