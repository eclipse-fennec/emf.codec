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
package org.eclipse.fennec.codec.deser;

import java.util.Objects;

import org.eclipse.fennec.codec.config.IdConfig;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;

/**
 * Reads the separator a document carries for its compound id (spec 09-id.md §9.3).
 * <p>
 * The separator in the document takes precedence over the configured one. The document
 * states how it was actually written; a configured value can come from anywhere, and
 * splitting an id with the wrong separator moves parts of it into the wrong features.
 * </p>
 * <p>
 * It is written after the id, so the id has already been split with the configured
 * separator by the time this entry runs — hence the re-split through
 * {@link DeserializationState#applyDocumentSeparator(String)}.
 * </p>
 *
 * @see IdDeserializationEntry
 * @since 2026-08-06
 */
public class IdSeparatorDeserializationEntry implements DeserializationEntry {

    private final String key;

    /**
     * Creates an entry for the separator key a PLAIN compound id uses.
     *
     * @param config the effective ID configuration
     */
    public IdSeparatorDeserializationEntry(IdConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        String separatorKey = config.getSeparatorKey();
        // PLAIN prefixes the key with an underscore, mirroring IdSerializationEntry
        this.key = separatorKey.startsWith("_") || separatorKey.startsWith("@")
                ? separatorKey
                : "_" + separatorKey;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void deserialize(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt) {
        if (parser.currentToken() != JsonToken.VALUE_STRING) {
            return;
        }
        state.applyDocumentSeparator(parser.getString());
    }
}
