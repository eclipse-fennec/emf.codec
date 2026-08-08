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
package org.eclipse.fennec.codec.util;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

/**
 * Loop conditions for reading JSON structures that terminate on stream end (issue #132).
 * <p>
 * A loop written as {@code while (parser.nextToken() != END_ARRAY)} never leaves once the
 * parser returns {@code null}: the comparison stays true forever. That happens with a
 * truncated buffer, or after an element failed to consume its own tokens and left the parser
 * desynchronized. The result is not a wrong value but a <b>hang</b> — no error, no output,
 * nothing to look at.
 * </p>
 * <p>
 * Using these conditions, a broken document ends the loop and lets the caller report the
 * problem instead of spinning.
 * </p>
 *
 * @since 1.0
 */
public final class TokenLoops {

    private TokenLoops() {
        // utility
    }

    /**
     * Advances to the next array element.
     *
     * @param parser the parser, positioned inside an array
     * @return true if another element follows, false at {@code END_ARRAY} or stream end
     */
    public static boolean hasNextElement(JsonParser parser) {
        JsonToken token = parser.nextToken();
        return token != null && token != JsonToken.END_ARRAY;
    }

    /**
     * Advances to the next object field.
     *
     * @param parser the parser, positioned inside an object
     * @return true if another field follows, false at {@code END_OBJECT} or stream end
     */
    public static boolean hasNextField(JsonParser parser) {
        JsonToken token = parser.nextToken();
        return token != null && token != JsonToken.END_OBJECT;
    }
}
