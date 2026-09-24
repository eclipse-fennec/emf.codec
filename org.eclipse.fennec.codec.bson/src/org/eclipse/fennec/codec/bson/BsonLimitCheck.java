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
package org.eclipse.fennec.codec.bson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import org.bson.BSONException;
import org.bson.BsonBinaryReader;
import org.bson.BsonType;
import org.eclipse.fennec.codec.constants.CodecOptions;

import tools.jackson.core.StreamReadConstraints;

/**
 * Checks a raw BSON document against the read limits before it is decoded (issue #232).
 * <p>
 * The BSON decoder recurses once per nesting level and knows no limits of its own: a document
 * of 120 KB with 10 000 nested levels ended in a {@code StackOverflowError}, and a string above
 * {@code codec.maxStringLength} was accepted. This walk is iterative - it keeps its own stack of
 * open documents and arrays - so it cannot overflow, and it stops at the first limit exceeded.
 * </p>
 */
final class BsonLimitCheck {

    private BsonLimitCheck() {
    }

    /**
     * Walks the document and fails on the first limit it exceeds.
     *
     * @param bytes the raw BSON document, already bounded by the document size limit
     * @param limits the read limits of this load
     * @throws IOException when a limit is exceeded or the document is malformed
     */
    static void check(byte[] bytes, StreamReadConstraints limits) throws IOException {
        // true = array, false = document (a code-with-scope document counts as a document)
        Deque<Boolean> open = new ArrayDeque<>();
        try (BsonBinaryReader reader = new BsonBinaryReader(ByteBuffer.wrap(bytes))) {
            reader.readStartDocument();
            enter(open, false, limits);
            while (!open.isEmpty()) {
                BsonType type = reader.readBsonType();
                if (type == BsonType.END_OF_DOCUMENT) {
                    if (open.pop()) {
                        reader.readEndArray();
                    } else {
                        reader.readEndDocument();
                    }
                    continue;
                }
                if (!open.peek()) {
                    // array elements are named by their index, which the reader consumes itself
                    checkLength("property name", reader.readName(), limits.getMaxNameLength(),
                            CodecOptions.CODEC_MAX_NAME_LENGTH);
                }
                switch (type) {
                    case DOCUMENT -> {
                        reader.readStartDocument();
                        enter(open, false, limits);
                    }
                    case ARRAY -> {
                        reader.readStartArray();
                        enter(open, true, limits);
                    }
                    case JAVASCRIPT_WITH_SCOPE -> {
                        checkString(reader.readJavaScriptWithScope(), limits);
                        reader.readStartDocument();
                        enter(open, false, limits);
                    }
                    case STRING -> checkString(reader.readString(), limits);
                    case JAVASCRIPT -> checkString(reader.readJavaScript(), limits);
                    case SYMBOL -> checkString(reader.readSymbol(), limits);
                    default -> reader.skipValue();
                }
            }
        } catch (BSONException e) {
            throw new IOException("Malformed BSON document: " + e.getMessage(), e);
        }
    }

    private static void enter(Deque<Boolean> open, boolean array, StreamReadConstraints limits) throws IOException {
        open.push(array);
        if (open.size() > limits.getMaxNestingDepth()) {
            throw new IOException(String.format(
                    "BSON nesting depth (%d) exceeds the maximum allowed (%d, from %s)",
                    open.size(), limits.getMaxNestingDepth(), CodecOptions.CODEC_MAX_NESTING_DEPTH));
        }
    }

    private static void checkString(String value, StreamReadConstraints limits) throws IOException {
        checkLength("string value", value, limits.getMaxStringLength(), CodecOptions.CODEC_MAX_STRING_LENGTH);
    }

    private static void checkLength(String what, String value, int max, String option) throws IOException {
        if (value.length() > max) {
            throw new IOException(String.format("BSON %s length (%d) exceeds the maximum allowed (%d, from %s)",
                    what, value.length(), max, option));
        }
    }
}
