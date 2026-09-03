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
import org.eclipse.fennec.codec.prefix.CodecPrefixWriterContext;
import org.eclipse.fennec.codec.value.EffectiveCodecConfig;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Default {@link CodecPrefixWriterContext}: the generator positioned inside the object after the
 * metadata fields, plus configuration and diagnostics (issue #193).
 */
public class CodecPrefixWriterContextImpl implements CodecPrefixWriterContext {

    private final JsonGenerator generator;
    private final SerializationContext jacksonContext;
    private final EffectiveCodecConfig config;
    private final DiagnosticCollector diagnostics;

    public CodecPrefixWriterContextImpl(JsonGenerator generator, SerializationContext jacksonContext,
            EffectiveCodecConfig config, DiagnosticCollector diagnostics) {
        if (generator == null) {
            throw new IllegalArgumentException("Generator must not be null");
        }
        this.generator = generator;
        this.jacksonContext = jacksonContext;
        this.config = config;
        this.diagnostics = diagnostics;
    }

    @Override
    public JsonGenerator getGenerator() {
        return generator;
    }

    @Override
    public SerializationContext getJacksonContext() {
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
