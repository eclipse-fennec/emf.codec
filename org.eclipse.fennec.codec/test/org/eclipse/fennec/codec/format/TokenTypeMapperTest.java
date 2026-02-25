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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.fennec.codec.format.impl.TokenTypeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;

/**
 * Tests for {@link TokenTypeMapper}.
 */
@DisplayName("TokenTypeMapper")
class TokenTypeMapperTest {

    @Nested
    @DisplayName("toJsonToken")
    class ToJsonTokenTests {

        @Test
        @DisplayName("maps structural tokens correctly")
        void structuralTokens() {
            assertEquals(JsonToken.START_OBJECT, TokenTypeMapper.toJsonToken(TokenType.START_OBJECT));
            assertEquals(JsonToken.END_OBJECT, TokenTypeMapper.toJsonToken(TokenType.END_OBJECT));
            assertEquals(JsonToken.START_ARRAY, TokenTypeMapper.toJsonToken(TokenType.START_ARRAY));
            assertEquals(JsonToken.END_ARRAY, TokenTypeMapper.toJsonToken(TokenType.END_ARRAY));
        }

        @Test
        @DisplayName("maps FIELD_NAME to PROPERTY_NAME (Jackson 3)")
        void fieldName() {
            assertEquals(JsonToken.PROPERTY_NAME, TokenTypeMapper.toJsonToken(TokenType.FIELD_NAME));
        }

        @Test
        @DisplayName("maps value tokens correctly")
        void valueTokens() {
            assertEquals(JsonToken.VALUE_STRING, TokenTypeMapper.toJsonToken(TokenType.VALUE_STRING));
            assertEquals(JsonToken.VALUE_NUMBER_INT, TokenTypeMapper.toJsonToken(TokenType.VALUE_NUMBER_INT));
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, TokenTypeMapper.toJsonToken(TokenType.VALUE_NUMBER_FLOAT));
            assertEquals(JsonToken.VALUE_NULL, TokenTypeMapper.toJsonToken(TokenType.VALUE_NULL));
        }

        @Test
        @DisplayName("maps VALUE_BOOLEAN to VALUE_TRUE by default")
        void booleanDefaultsToTrue() {
            assertEquals(JsonToken.VALUE_TRUE, TokenTypeMapper.toJsonToken(TokenType.VALUE_BOOLEAN));
        }

        @Test
        @DisplayName("maps VALUE_BINARY to VALUE_EMBEDDED_OBJECT")
        void binary() {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, TokenTypeMapper.toJsonToken(TokenType.VALUE_BINARY));
        }

        @Test
        @DisplayName("maps NOT_AVAILABLE")
        void notAvailable() {
            assertEquals(JsonToken.NOT_AVAILABLE, TokenTypeMapper.toJsonToken(TokenType.NOT_AVAILABLE));
        }

        @Test
        @DisplayName("returns null for null input")
        void nullInput() {
            assertNull(TokenTypeMapper.toJsonToken(null));
        }
    }

    @Nested
    @DisplayName("toJsonToken with boolean value")
    class ToJsonTokenBooleanTests {

        @Test
        @DisplayName("maps VALUE_BOOLEAN true to VALUE_TRUE")
        void booleanTrue() {
            assertEquals(JsonToken.VALUE_TRUE, TokenTypeMapper.toJsonToken(TokenType.VALUE_BOOLEAN, true));
        }

        @Test
        @DisplayName("maps VALUE_BOOLEAN false to VALUE_FALSE")
        void booleanFalse() {
            assertEquals(JsonToken.VALUE_FALSE, TokenTypeMapper.toJsonToken(TokenType.VALUE_BOOLEAN, false));
        }

        @Test
        @DisplayName("throws for non-boolean token type")
        void nonBooleanThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> TokenTypeMapper.toJsonToken(TokenType.VALUE_STRING, true));
        }
    }

    @Nested
    @DisplayName("toTokenType")
    class ToTokenTypeTests {

        @Test
        @DisplayName("maps structural tokens correctly")
        void structuralTokens() {
            assertEquals(TokenType.START_OBJECT, TokenTypeMapper.toTokenType(JsonToken.START_OBJECT));
            assertEquals(TokenType.END_OBJECT, TokenTypeMapper.toTokenType(JsonToken.END_OBJECT));
            assertEquals(TokenType.START_ARRAY, TokenTypeMapper.toTokenType(JsonToken.START_ARRAY));
            assertEquals(TokenType.END_ARRAY, TokenTypeMapper.toTokenType(JsonToken.END_ARRAY));
        }

        @Test
        @DisplayName("maps PROPERTY_NAME to FIELD_NAME")
        void propertyName() {
            assertEquals(TokenType.FIELD_NAME, TokenTypeMapper.toTokenType(JsonToken.PROPERTY_NAME));
        }

        @Test
        @DisplayName("maps value tokens correctly")
        void valueTokens() {
            assertEquals(TokenType.VALUE_STRING, TokenTypeMapper.toTokenType(JsonToken.VALUE_STRING));
            assertEquals(TokenType.VALUE_NUMBER_INT, TokenTypeMapper.toTokenType(JsonToken.VALUE_NUMBER_INT));
            assertEquals(TokenType.VALUE_NUMBER_FLOAT, TokenTypeMapper.toTokenType(JsonToken.VALUE_NUMBER_FLOAT));
            assertEquals(TokenType.VALUE_NULL, TokenTypeMapper.toTokenType(JsonToken.VALUE_NULL));
        }

        @Test
        @DisplayName("maps both VALUE_TRUE and VALUE_FALSE to VALUE_BOOLEAN")
        void booleanTokens() {
            assertEquals(TokenType.VALUE_BOOLEAN, TokenTypeMapper.toTokenType(JsonToken.VALUE_TRUE));
            assertEquals(TokenType.VALUE_BOOLEAN, TokenTypeMapper.toTokenType(JsonToken.VALUE_FALSE));
        }

        @Test
        @DisplayName("maps VALUE_EMBEDDED_OBJECT to VALUE_BINARY")
        void embeddedObject() {
            assertEquals(TokenType.VALUE_BINARY, TokenTypeMapper.toTokenType(JsonToken.VALUE_EMBEDDED_OBJECT));
        }

        @Test
        @DisplayName("maps NOT_AVAILABLE")
        void notAvailable() {
            assertEquals(TokenType.NOT_AVAILABLE, TokenTypeMapper.toTokenType(JsonToken.NOT_AVAILABLE));
        }

        @Test
        @DisplayName("returns null for null input")
        void nullInput() {
            assertNull(TokenTypeMapper.toTokenType(null));
        }
    }

    @Nested
    @DisplayName("round-trip consistency")
    class RoundTripTests {

        @Test
        @DisplayName("all TokenType values survive round-trip (except VALUE_BOOLEAN)")
        void tokenTypeRoundTrip() {
            for (TokenType type : TokenType.values()) {
                if (type == TokenType.VALUE_BOOLEAN) {
                    // VALUE_BOOLEAN -> VALUE_TRUE -> VALUE_BOOLEAN (lossy: true/false collapsed)
                    assertEquals(TokenType.VALUE_BOOLEAN,
                            TokenTypeMapper.toTokenType(TokenTypeMapper.toJsonToken(type)));
                    continue;
                }
                TokenType roundTripped = TokenTypeMapper.toTokenType(TokenTypeMapper.toJsonToken(type));
                assertEquals(type, roundTripped, "Round-trip failed for: " + type);
            }
        }

        @Test
        @DisplayName("all relevant JsonToken values survive round-trip")
        void jsonTokenRoundTrip() {
            JsonToken[] relevantTokens = {
                JsonToken.START_OBJECT, JsonToken.END_OBJECT,
                JsonToken.START_ARRAY, JsonToken.END_ARRAY,
                JsonToken.PROPERTY_NAME,
                JsonToken.VALUE_STRING,
                JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT,
                JsonToken.VALUE_NULL,
                JsonToken.VALUE_EMBEDDED_OBJECT,
                JsonToken.NOT_AVAILABLE
            };
            for (JsonToken token : relevantTokens) {
                JsonToken roundTripped = TokenTypeMapper.toJsonToken(TokenTypeMapper.toTokenType(token));
                assertEquals(token, roundTripped, "Round-trip failed for: " + token);
            }
        }
    }
}
