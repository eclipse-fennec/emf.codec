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
package org.eclipse.fennec.codec.ser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriter;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriterContext;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.databind.SerializationContext;

/**
 * Writes one backend-owned prefix key through its {@link CodecPrefixWriter} (issue #193, spec
 * 14-custom-values.md §13).
 * <p>
 * The orchestrator places these entries after the metadata entries and before the first feature
 * entry, in registration order. The writer writes field name and value itself and may decline;
 * a writer that throws is a write-side warning and its field is skipped. If it threw after
 * writing the name but before a value, a {@code null} is written so the document stays
 * well-formed - the one repair the codec can make from outside.
 * </p>
 */
public class PrefixSerializationEntry implements SerializationEntry {

    private static final Logger LOGGER = Logger.getLogger(PrefixSerializationEntry.class.getName());
    static final String SOURCE = "PrefixSerializationEntry";

    private final String key;
    private final CodecPrefixWriter writer;
    private final CodecEntryContext entryContext;

    public PrefixSerializationEntry(String key, CodecPrefixWriter writer, CodecEntryContext entryContext) {
        this.key = key;
        this.writer = writer;
        this.entryContext = entryContext;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void serialize(SerializationState state, JsonGenerator gen, SerializationContext ctxt) {
        CodecPrefixWriterContext writerContext = entryContext.createPrefixWriterContext(gen, ctxt);
        try {
            writer.write(key, state.getEObject(), writerContext);
        } catch (Exception e) {
            String message = "Prefix writer for '" + key + "' failed on "
                    + state.getEObject().eClass().getName() + ": " + e.getMessage();
            LOGGER.log(Level.WARNING, message, e);
            warn(entryContext, message);
            repairDanglingName(gen);
        }
    }

    /** The write side reports through the entry context's collector, which reaches the resource (#184). */
    static void warn(CodecEntryContext entryContext, String message) {
        if (entryContext != null && entryContext.getDiagnostics() != null) {
            entryContext.getDiagnostics().addWarning(message, SOURCE);
        }
    }

    /**
     * A writer that threw between {@code writeName(key)} and the value leaves the object
     * expecting a value; supplying {@code null} keeps the document parseable. If no name is
     * pending the generator refuses the null and nothing changes.
     */
    private void repairDanglingName(JsonGenerator gen) {
        TokenStreamContext streamContext = gen.streamWriteContext();
        if (streamContext == null || !streamContext.hasCurrentName()
                || !key.equals(streamContext.currentName())) {
            return;
        }
        try {
            gen.writeNull();
        } catch (StreamWriteException notExpectingAValue) {
            // the name already has a value - nothing to repair
        }
    }
}
