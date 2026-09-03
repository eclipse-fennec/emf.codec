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
package org.eclipse.fennec.codec.prefix;

import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.value.EffectiveCodecConfig;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

/**
 * What a {@link CodecPrefixReader} gets to work with. Mirrors
 * {@link org.eclipse.fennec.codec.value.CodecReaderContext}; the parser is positioned at the
 * value of the key, live or replayed from a buffer - the reader cannot and need not tell.
 *
 * @since 1.0
 */
public interface CodecPrefixReaderContext {

    /** The parser, positioned at the value of the key. */
    JsonParser getParser();

    /** The Jackson deserialization context. */
    DeserializationContext getJacksonContext();

    /** The effective codec configuration of this load. */
    EffectiveCodecConfig getConfig();

    /** Collector for warnings and errors; they reach the resource. */
    DiagnosticCollector getDiagnostics();

    default void addWarning(String message) {
        getDiagnostics().addWarning(message, "CodecPrefixReader");
    }

    default void addError(String message) {
        getDiagnostics().addError(message, "CodecPrefixReader");
    }
}
