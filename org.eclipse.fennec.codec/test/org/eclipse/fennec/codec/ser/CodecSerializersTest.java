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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecSerializers}.
 *
 * @see <a href="docs/codec-v2-spec/09-jackson-module.md">Spec 9: Jackson Module Integration</a>
 */
@DisplayName("CodecSerializers")
class CodecSerializersTest {

    private EffectiveCodecConfig effectiveConfig;
    private CodecSerializers serializers;

    @BeforeEach
    void setUp() {
        effectiveConfig = EffectiveCodecConfig.builder()
                .resolver(ConfigurationResolver.defaults())
                .diagnostics(new DiagnosticCollector())
                .build();
        serializers = new CodecSerializers(effectiveConfig);
    }

    @Test
    @DisplayName("constructor creates serializers")
    void constructorCreatesSerializers() {
        assertNotNull(serializers);
    }

    @Test
    @DisplayName("can be created with custom effective config")
    void canBeCreatedWithCustomEffectiveConfig() {
        EffectiveCodecConfig customConfig = EffectiveCodecConfig.builder()
                .resolver(ConfigurationResolver.defaults())
                .diagnostics(new DiagnosticCollector())
                .smartCompression(true)
                .build();
        CodecSerializers customSerializers = new CodecSerializers(customConfig);
        assertNotNull(customSerializers);
    }
}
