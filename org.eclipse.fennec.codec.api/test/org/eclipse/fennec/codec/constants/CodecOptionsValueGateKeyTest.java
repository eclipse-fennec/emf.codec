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
package org.eclipse.fennec.codec.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.eclipse.fennec.codec.config.ConfigMergeHelper;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * The three value-gate options are one family and each must answer to one spelling: the
 * {@link CodecOptions} constant a caller writes has to be the key {@link ConfigMergeHelper}
 * reads, or the option is dropped in silence.
 * <p>
 * {@code codec.serializeDefaults} was plural while {@link ConfigProperty#SERIALIZE_DEFAULT} is
 * singular, so every caller using the constant was ignored - no warning, no diagnostic, no
 * exception. Its two siblings were always correct, which is what made the odd one out easy to
 * miss.
 */
@DisplayName("CodecOptions - value-gate option keys resolve to their ConfigProperty")
class CodecOptionsValueGateKeyTest {

    /**
     * A constant is only usable if the resolver reads the key it holds; asserting the string
     * alone would not prove that, and asserting the lookup alone would not say which spelling
     * is canonical. Both are checked for each option.
     */
    private static void assertResolves(String name, String constant, ConfigProperty property) {
        assertEquals(property.getPropertyKey(), constant,
                () -> "the CodecOptions constant for " + name
                        + " must be the key the resolver reads");

        Boolean value = ConfigMergeHelper.getValue(Map.of(constant, Boolean.TRUE), property);
        assertEquals(Boolean.TRUE, value,
                () -> "an option written as '" + constant + "' must resolve to " + name
                        + "; a key the resolver does not know is dropped in silence");
    }

    @Nested
    @DisplayName("each value-gate constant is the key the resolver reads")
    class ValueGateConstants {

        @Test
        @DisplayName("serializeNull")
        void serializeNull() {
            assertResolves("serializeNull", CodecOptions.CODEC_SERIALIZE_NULL,
                    ConfigProperty.SERIALIZE_NULL);
        }

        @Test
        @DisplayName("serializeEmpty")
        void serializeEmpty() {
            assertResolves("serializeEmpty", CodecOptions.CODEC_SERIALIZE_EMPTY,
                    ConfigProperty.SERIALIZE_EMPTY);
        }

        @Test
        @DisplayName("serializeDefault")
        void serializeDefault() {
            assertResolves("serializeDefault", CodecOptions.CODEC_SERIALIZE_DEFAULT,
                    ConfigProperty.SERIALIZE_DEFAULT);
        }
    }

    @Test
    @DisplayName("the prefixed key is the short key with the codec. prefix")
    void prefixedKeyIsShortKeyWithPrefix() {
        assertEquals(CodecOptions.CODEC_PREFIX + ConfigProperty.SERIALIZE_DEFAULT.getKey(),
                CodecOptions.CODEC_SERIALIZE_DEFAULT,
                "the two spellings of one option must agree");
    }

    /**
     * The misnamed constant stays as a source-compatible alias. An alias that drifts is worse
     * than no alias - a caller on the old name would be silently ignored again - so it is
     * pinned to the same value rather than to a string of its own.
     */
    @Test
    @DisplayName("the deprecated constant is an alias, not a second spelling")
    @SuppressWarnings("deprecation")
    void deprecatedConstantIsAnAlias() {
        assertEquals(CodecOptions.CODEC_SERIALIZE_DEFAULT, CodecOptions.CODEC_SERIALIZE_DEFAULTS,
                "CODEC_SERIALIZE_DEFAULTS must carry the same key as CODEC_SERIALIZE_DEFAULT");

        assertResolves("serializeDefault (deprecated alias)",
                CodecOptions.CODEC_SERIALIZE_DEFAULTS, ConfigProperty.SERIALIZE_DEFAULT);
    }
}
