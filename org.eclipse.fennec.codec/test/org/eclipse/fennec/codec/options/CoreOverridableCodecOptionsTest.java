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
package org.eclipse.fennec.codec.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.fennec.codec.config.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The core contribution publishes the same key spelling as every module contribution (issue #223).
 * <p>
 * {@code codec.csv.*}, {@code codec.ods.*} and {@code codec.tabular.*} all reach the client as
 * prefixed keys, because that is how their constants are written. The core options were published
 * bare, so a caller assembling a {@code Codec-Options} header out of the public
 * {@code CodecOptions.CODEC_*} constants sent a key the filter did not know.
 * </p>
 */
@DisplayName("CoreOverridableCodecOptions — the published key spelling")
class CoreOverridableCodecOptionsTest {

    private final Map<String, Class<?>> published = new CoreOverridableCodecOptions().overridableKeys();

    @Test
    @DisplayName("every published key carries the codec. prefix, like every module contribution")
    void keysArePrefixed() {
        published.keySet()
                .forEach(key -> assertTrue(key.startsWith("codec."),
                        () -> "'" + key + "' is published bare; a client assembling the header from "
                                + "CodecOptions.CODEC_* constants would send the prefixed spelling"));
    }

    @Test
    @DisplayName("the keys are exactly the constants the REST caller has in hand")
    void keysAreThePublicConstants() {
        assertEquals(Set.of("codec.serializeNull", "codec.serializeEmpty", "codec.serializeDefault",
                "codec.enumSerialization", "codec.fieldOrder", "codec.idOnTop", "codec.dateFormat"),
                published.keySet());
    }

    @Test
    @DisplayName("each published key is a ConfigProperty's property key, so a resolver reads it")
    void keysAreConfigPropertyKeys() {
        Set<String> propertyKeys = Arrays.stream(ConfigProperty.values())
                .map(ConfigProperty::getPropertyKey)
                .collect(Collectors.toSet());

        published.keySet()
                .forEach(key -> assertTrue(propertyKeys.contains(key),
                        () -> "no ConfigProperty answers to '" + key + "'"));
    }

    @Test
    @DisplayName("the declared value type is the property's own type, so parsing matches the reader")
    void typesMatchTheProperty() {
        published.forEach((key, type) -> {
            Optional<ConfigProperty> property = Arrays.stream(ConfigProperty.values())
                    .filter(p -> p.getPropertyKey().equals(key))
                    .findFirst();
            assertTrue(property.isPresent(), () -> "no ConfigProperty answers to '" + key + "'");
            assertEquals(property.get().getType(), type, () -> "declared type of '" + key + "'");
        });
    }

    @Test
    @DisplayName("nothing with a real blast radius slipped onto the list")
    void staysPresentational() {
        assertNotNull(published);
        Set.of("codec.typeStrategy", "codec.expand", "codec.expandDepth", "codec.idValueReaderName",
                "codec.typeValueWriterName")
                .forEach(risky -> assertTrue(!published.containsKey(risky),
                        () -> "'" + risky + "' is not presentational and must stay off the allow-list"));
    }
}
