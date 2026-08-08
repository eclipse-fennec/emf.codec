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
package org.eclipse.fennec.codec.format;

/**
 * Format-agnostic token type enumeration for codec format delegates.
 * <p>
 * This enum represents the structural tokens that any serialization format
 * must support. It decouples format implementations from Jackson's
 * {@code JsonToken} enum, allowing non-Jackson formats (BSON, Lucene, etc.)
 * to participate in the codec without Jackson dependencies.
 *
 * @see FormatDelegate
 * @see FormatReaderDelegate
 * @since 1.0
 */
public enum TokenType {

    /** Start of an object/document structure */
    START_OBJECT,

    /** End of an object/document structure */
    END_OBJECT,

    /** Start of an array/list structure */
    START_ARRAY,

    /** End of an array/list structure */
    END_ARRAY,

    /** A field/property name */
    FIELD_NAME,

    /** A string value */
    VALUE_STRING,

    /** An integer numeric value (int, long, BigInteger) */
    VALUE_NUMBER_INT,

    /** A floating-point numeric value (float, double, BigDecimal) */
    VALUE_NUMBER_FLOAT,

    /** A boolean value (true/false) */
    VALUE_BOOLEAN,

    /** A null value */
    VALUE_NULL,

    /** A binary/byte array value */
    VALUE_BINARY,

    /** No token available (e.g., before first read or after end of input) */
    NOT_AVAILABLE
}
