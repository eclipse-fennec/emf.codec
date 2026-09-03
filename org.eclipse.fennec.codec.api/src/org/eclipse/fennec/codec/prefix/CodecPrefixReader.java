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
 * Consumes one backend-owned document key when an object is read back.
 * <p>
 * Invoked once the EObject exists, with the context's parser positioned at the value - whether
 * that is the live stream or a replay of a value buffered before the type was known is invisible
 * to the reader. The reader consumes exactly that value (scalar, object or array, whole) and
 * leaves the parser after it. What it does with the value is its business: ignore it (a
 * "reserved key" is an empty reader), attach an adapter to {@code target}, fill an index on the
 * resource, validate. It must not set features on {@code target} that the document also carries.
 * </p>
 * <p>
 * A key with a registered reader is <em>known</em>: no diagnostic, also under
 * {@code strictOnUnknown}. A reader that throws follows the strictness hierarchy - STRICT fails
 * the load, otherwise an error diagnostic and the value is dropped. Spec: 14-custom-values.md
 * §13.2, §13.5, §13.6.
 * </p>
 *
 * @since 1.0
 */
@FunctionalInterface
public interface CodecPrefixReader {

    /**
     * Consumes the value found under {@code key}.
     *
     * @param key    the document key this reader is registered under
     * @param target the EObject being built, already created
     * @param ctx    parser positioned at the value, configuration and diagnostics
     * @throws IOException on parser failure or an unacceptable value
     */
    void read(String key, EObject target, CodecPrefixReaderContext ctx) throws IOException;
}
