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

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * What a {@link CodecPrefixWriter} gets to work with. Mirrors
 * {@link org.eclipse.fennec.codec.value.CodecWriterContext}; the generator is positioned inside
 * the object being written, after the metadata fields.
 *
 * @since 1.0
 */
public interface CodecPrefixWriterContext {

    /** The generator, positioned inside the object after the codec's metadata fields. */
    JsonGenerator getGenerator();

    /** The Jackson serialization context. */
    SerializationContext getJacksonContext();

    /** The effective codec configuration of this save. */
    EffectiveCodecConfig getConfig();

    /** Collector for warnings and errors; they reach the resource. */
    DiagnosticCollector getDiagnostics();

    default void addWarning(String message) {
        getDiagnostics().addWarning(message, "CodecPrefixWriter");
    }

    default void addError(String message) {
        getDiagnostics().addError(message, "CodecPrefixWriter");
    }
}
