/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.rest.common;

import java.text.SimpleDateFormat;

/**
 * Parses a codec option value supplied as a {@link String} into the declared value type.
 * <p>
 * Shared by the annotation path ({@code @ResourceOption}) and the client-override path (the
 * {@code Codec-Options} request header) so both interpret values identically. {@code EClass}
 * values are handled separately by callers that have a {@code ResourceSet} (they need URI
 * resolution); this helper covers the scalar types.
 *
 * @since 1.0
 */
public final class CodecOptionValues {

    private CodecOptionValues() {
        // static use
    }

    /**
     * Parses {@code value} into an instance of {@code valueType}.
     *
     * @param value     the raw string value (may be {@code null})
     * @param valueType the desired type; {@code null} or {@code String.class} returns the value
     *                  verbatim. Supported: {@code String}, {@code Integer}, {@code Double},
     *                  {@code Long}, {@code Boolean}, {@code SimpleDateFormat}. Any other type
     *                  falls back to the raw string.
     * @return the parsed value, or {@code null} if {@code value} is {@code null}
     */
    public static Object parse(String value, Class<?> valueType) {
        if (value == null) {
            return null;
        }
        if (valueType == null || valueType.equals(String.class)) {
            return value;
        } else if (valueType.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (valueType.equals(Double.class)) {
            return Double.parseDouble(value);
        } else if (valueType.equals(Long.class)) {
            return Long.parseLong(value);
        } else if (valueType.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (valueType.equals(SimpleDateFormat.class)) {
            return new SimpleDateFormat(value);
        }
        // Unknown type — pass the string through; downstream option resolvers often parse strings.
        return value;
    }
}
