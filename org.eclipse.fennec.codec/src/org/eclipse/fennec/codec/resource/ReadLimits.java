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
package org.eclipse.fennec.codec.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.fennec.codec.constants.CodecOptions;

import tools.jackson.core.StreamReadConstraints;

/**
 * The read limits of one load, resolved from the load options (issue #232).
 * <p>
 * Secure by default: an option that is absent takes its {@code CodecOptions.DEFAULT_*} value.
 * An option that is present but unusable fails the load - a limit someone meant to set must
 * not silently fall back to a different one.
 * </p>
 *
 * @param constraints the parser limits: document size, nesting, string and name length
 * @param maxCollectionSize the maximum size of an untyped collection
 */
record ReadLimits(StreamReadConstraints constraints, int maxCollectionSize) {

    /** The limits of a load that sets none. */
    static final ReadLimits DEFAULTS = new ReadLimits(StreamReadConstraints.builder()
            .maxDocumentLength(CodecOptions.DEFAULT_MAX_PAYLOAD_SIZE)
            .maxNestingDepth(CodecOptions.DEFAULT_MAX_NESTING_DEPTH)
            .maxStringLength(CodecOptions.DEFAULT_MAX_STRING_LENGTH)
            .maxNameLength(CodecOptions.DEFAULT_MAX_NAME_LENGTH)
            .build(), CodecOptions.DEFAULT_MAX_COLLECTION_SIZE);

    /**
     * Resolves the limits from the merged load options.
     *
     * @param options the merged load options, may be {@code null}
     * @return the limits of this load
     * @throws IOException when a limit option is present but not a positive number
     */
    static ReadLimits resolve(Map<?, ?> options) throws IOException {
        if (options == null) {
            return DEFAULTS;
        }
        long payload = positive(options, CodecOptions.CODEC_MAX_PAYLOAD_SIZE, CodecOptions.DEFAULT_MAX_PAYLOAD_SIZE);
        int depth = positiveInt(options, CodecOptions.CODEC_MAX_NESTING_DEPTH, CodecOptions.DEFAULT_MAX_NESTING_DEPTH);
        int string = positiveInt(options, CodecOptions.CODEC_MAX_STRING_LENGTH, CodecOptions.DEFAULT_MAX_STRING_LENGTH);
        int name = positiveInt(options, CodecOptions.CODEC_MAX_NAME_LENGTH, CodecOptions.DEFAULT_MAX_NAME_LENGTH);
        int collection = positiveInt(options, CodecOptions.CODEC_MAX_COLLECTION_SIZE,
                CodecOptions.DEFAULT_MAX_COLLECTION_SIZE);
        return new ReadLimits(StreamReadConstraints.builder()
                .maxDocumentLength(payload)
                .maxNestingDepth(depth)
                .maxStringLength(string)
                .maxNameLength(name)
                .build(), collection);
    }

    private static int positiveInt(Map<?, ?> options, String key, int defaultValue) throws IOException {
        long value = positive(options, key, defaultValue);
        if (value > Integer.MAX_VALUE) {
            throw unusable(key, options.get(key));
        }
        return (int) value;
    }

    private static long positive(Map<?, ?> options, String key, long defaultValue) throws IOException {
        Object value = options.get(key);
        if (value == null) {
            return defaultValue;
        }
        long parsed;
        if (value instanceof Number number) {
            parsed = number.longValue();
        } else if (value instanceof String text) {
            try {
                parsed = Long.parseLong(text.trim());
            } catch (NumberFormatException e) {
                throw unusable(key, value);
            }
        } else {
            throw unusable(key, value);
        }
        if (parsed <= 0) {
            throw unusable(key, value);
        }
        return parsed;
    }

    private static IOException unusable(String key, Object value) {
        return new IOException(String.format(
                "Load option %s must be a positive number, but is '%s'", key, value));
    }
}
