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

import java.io.IOException;

import org.eclipse.emf.ecore.EObject;

/**
 * Writes one backend-owned document key into the prefix of an object - after the codec's
 * metadata fields, before the first feature.
 * <p>
 * The key is a parameter, not a property of the writer: one instance may be registered under
 * several keys in a {@link CodecPrefixRegistry} and is told which one it is serving. The writer
 * writes the field name itself ({@code ctx.getGenerator().writeName(key)}) followed by the
 * value, and writes <b>that one field or nothing</b>. Returning {@code false} means the field is
 * absent for this object; that is how a writer restricts itself to roots, to cross-document
 * children, or to whatever it decides.
 * </p>
 * <p>
 * A writer that throws is reported as a write-side warning and its field is skipped; the save
 * does not fail on it. Spec: 14-custom-values.md §13.2, §13.6.
 * </p>
 *
 * @since 1.0
 */
@FunctionalInterface
public interface CodecPrefixWriter {

    /**
     * Writes the field for {@code key}, or writes nothing and returns {@code false}.
     *
     * @param key    the document key this writer is registered under
     * @param object the EObject being serialized; may be a contained object, not only the root
     * @param ctx    generator, configuration and diagnostics
     * @return {@code true} if the field was written, {@code false} if nothing was written
     * @throws IOException on generator failure
     */
    boolean write(String key, EObject object, CodecPrefixWriterContext ctx) throws IOException;
}
