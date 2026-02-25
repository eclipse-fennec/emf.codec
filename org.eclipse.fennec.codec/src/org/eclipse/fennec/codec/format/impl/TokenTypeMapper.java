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
package org.eclipse.fennec.codec.format.impl;

import org.eclipse.fennec.codec.format.TokenType;

import tools.jackson.core.JsonToken;

/**
 * Bidirectional mapper between format-agnostic {@link TokenType} and
 * Jackson's {@link JsonToken}.
 * <p>
 * This utility enables the bridge between {@code FormatDelegate}/{@code FormatReaderDelegate}
 * (which use {@code TokenType}) and Jackson's streaming API (which uses {@code JsonToken}).
 * <p>
 * Notable Jackson 3 differences:
 * <ul>
 *   <li>{@code JsonToken.PROPERTY_NAME} maps to {@code TokenType.FIELD_NAME}</li>
 *   <li>{@code JsonToken.VALUE_TRUE} and {@code JsonToken.VALUE_FALSE} both map to {@code TokenType.VALUE_BOOLEAN}</li>
 * </ul>
 *
 * @see TokenType
 * @since 2026-02-16
 */
public final class TokenTypeMapper {

    private TokenTypeMapper() {
        // utility class
    }

    /**
     * Converts a format-agnostic {@link TokenType} to a Jackson {@link JsonToken}.
     * <p>
     * Note: {@code TokenType.VALUE_BOOLEAN} maps to {@code JsonToken.VALUE_TRUE}.
     * Use {@link #toJsonToken(TokenType, boolean)} to specify the boolean value.
     *
     * @param tokenType the format-agnostic token type
     * @return the corresponding Jackson token, or {@code null} if input is {@code null}
     */
    public static JsonToken toJsonToken(TokenType tokenType) {
        if (tokenType == null) {
            return null;
        }
        return switch (tokenType) {
            case START_OBJECT -> JsonToken.START_OBJECT;
            case END_OBJECT -> JsonToken.END_OBJECT;
            case START_ARRAY -> JsonToken.START_ARRAY;
            case END_ARRAY -> JsonToken.END_ARRAY;
            case FIELD_NAME -> JsonToken.PROPERTY_NAME;
            case VALUE_STRING -> JsonToken.VALUE_STRING;
            case VALUE_NUMBER_INT -> JsonToken.VALUE_NUMBER_INT;
            case VALUE_NUMBER_FLOAT -> JsonToken.VALUE_NUMBER_FLOAT;
            case VALUE_BOOLEAN -> JsonToken.VALUE_TRUE;
            case VALUE_NULL -> JsonToken.VALUE_NULL;
            case VALUE_BINARY -> JsonToken.VALUE_EMBEDDED_OBJECT;
            case NOT_AVAILABLE -> JsonToken.NOT_AVAILABLE;
        };
    }

    /**
     * Converts a {@link TokenType#VALUE_BOOLEAN} to the correct Jackson boolean token.
     *
     * @param tokenType the token type (must be {@code VALUE_BOOLEAN})
     * @param value the boolean value
     * @return {@code JsonToken.VALUE_TRUE} or {@code JsonToken.VALUE_FALSE}
     * @throws IllegalArgumentException if tokenType is not {@code VALUE_BOOLEAN}
     */
    public static JsonToken toJsonToken(TokenType tokenType, boolean value) {
        if (tokenType != TokenType.VALUE_BOOLEAN) {
            throw new IllegalArgumentException("Expected VALUE_BOOLEAN, got: " + tokenType);
        }
        return value ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
    }

    /**
     * Converts a Jackson {@link JsonToken} to a format-agnostic {@link TokenType}.
     *
     * @param jsonToken the Jackson token
     * @return the corresponding format-agnostic token type, or {@code null} if input is {@code null}
     */
    public static TokenType toTokenType(JsonToken jsonToken) {
        if (jsonToken == null) {
            return null;
        }
        return switch (jsonToken) {
            case START_OBJECT -> TokenType.START_OBJECT;
            case END_OBJECT -> TokenType.END_OBJECT;
            case START_ARRAY -> TokenType.START_ARRAY;
            case END_ARRAY -> TokenType.END_ARRAY;
            case PROPERTY_NAME -> TokenType.FIELD_NAME;
            case VALUE_STRING -> TokenType.VALUE_STRING;
            case VALUE_NUMBER_INT -> TokenType.VALUE_NUMBER_INT;
            case VALUE_NUMBER_FLOAT -> TokenType.VALUE_NUMBER_FLOAT;
            case VALUE_TRUE, VALUE_FALSE -> TokenType.VALUE_BOOLEAN;
            case VALUE_NULL -> TokenType.VALUE_NULL;
            case VALUE_EMBEDDED_OBJECT -> TokenType.VALUE_BINARY;
            case NOT_AVAILABLE -> TokenType.NOT_AVAILABLE;
        };
    }
}
