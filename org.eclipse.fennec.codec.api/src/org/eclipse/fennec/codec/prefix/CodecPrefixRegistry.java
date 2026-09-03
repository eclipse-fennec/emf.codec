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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Registry of prefix writers and readers, keyed by document key.
 * <p>
 * Registration is by key, lookup is a map. A key may have one writer and one reader, registered
 * independently; a second writer or reader for a key already taken is rejected with an
 * {@link IllegalArgumentException} - there is no "first wins" and no priority. The writer order
 * is the registration order and is the order the fields appear in the prefix.
 * </p>
 * <p>
 * A sister of {@code CodecValueRegistry}, not part of it: different handler shape, different
 * lookup, and a prefix key must never be confused with a value handler name. Spec:
 * 14-custom-values.md §13.3.
 * </p>
 *
 * @since 1.0
 */
public class CodecPrefixRegistry {

    private final Map<String, CodecPrefixWriter> writers = new LinkedHashMap<>();
    private final Map<String, CodecPrefixReader> readers = new LinkedHashMap<>();

    public CodecPrefixRegistry() {
    }

    /**
     * Registers a writer under a key.
     *
     * @throws IllegalArgumentException if the key is null or empty, the writer is null, or the
     *                                  key already has a writer
     */
    public synchronized CodecPrefixRegistry register(String key, CodecPrefixWriter writer) {
        requireKey(key);
        Objects.requireNonNull(writer, "writer must not be null");
        if (writers.containsKey(key)) {
            throw new IllegalArgumentException("Prefix key '" + key + "' already has a writer");
        }
        writers.put(key, writer);
        return this;
    }

    /**
     * Registers a reader under a key.
     *
     * @throws IllegalArgumentException if the key is null or empty, the reader is null, or the
     *                                  key already has a reader
     */
    public synchronized CodecPrefixRegistry register(String key, CodecPrefixReader reader) {
        requireKey(key);
        Objects.requireNonNull(reader, "reader must not be null");
        if (readers.containsKey(key)) {
            throw new IllegalArgumentException("Prefix key '" + key + "' already has a reader");
        }
        readers.put(key, reader);
        return this;
    }

    public synchronized CodecPrefixRegistry unregisterWriter(String key) {
        if (key != null) {
            writers.remove(key);
        }
        return this;
    }

    public synchronized CodecPrefixRegistry unregisterReader(String key) {
        if (key != null) {
            readers.remove(key);
        }
        return this;
    }

    public synchronized Optional<CodecPrefixWriter> getWriter(String key) {
        return Optional.ofNullable(key == null ? null : writers.get(key));
    }

    public synchronized Optional<CodecPrefixReader> getReader(String key) {
        return Optional.ofNullable(key == null ? null : readers.get(key));
    }

    public synchronized boolean hasWriter(String key) {
        return key != null && writers.containsKey(key);
    }

    public synchronized boolean hasReader(String key) {
        return key != null && readers.containsKey(key);
    }

    /** The writer keys in registration order - the order of the fields in the prefix. */
    public synchronized List<String> writerKeys() {
        return Collections.unmodifiableList(new ArrayList<>(writers.keySet()));
    }

    /** The reader keys, in registration order. */
    public synchronized Set<String> readerKeys() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(readers.keySet()));
    }

    public synchronized boolean isEmpty() {
        return writers.isEmpty() && readers.isEmpty();
    }

    /**
     * An independent copy: later registrations on either side do not affect the other. Resource
     * factories hand a copy to every resource, so nothing registered per resource outlives it.
     */
    public synchronized CodecPrefixRegistry copy() {
        CodecPrefixRegistry copy = new CodecPrefixRegistry();
        copy.writers.putAll(writers);
        copy.readers.putAll(readers);
        return copy;
    }

    private static void requireKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Prefix key must not be null or empty");
        }
    }
}
