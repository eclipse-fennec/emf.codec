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
package org.eclipse.fennec.codec.module;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecModule} factory methods.
 *
 * @see <a href="docs/codec-v2-spec/09-jackson-module.md">Spec 9: Jackson Module Integration</a>
 */
@DisplayName("CodecModule factory methods")
class CodecModuleFactoryMethodsTest {

    @Test
    @DisplayName("withDefaults creates module with default resolver")
    void withDefaultsCreatesModuleWithDefaultResolver() {
        CodecModule module = CodecModule.withDefaults();
        assertNotNull(module);
        assertNotNull(module.getResolver());
        assertNotNull(module.getValueRegistry());
    }

    @Test
    @DisplayName("builder returns new builder instance")
    void builderReturnsNewBuilderInstance() {
        CodecModule.Builder builder = CodecModule.builder();
        assertNotNull(builder);
    }

    @Test
    @DisplayName("createEffectiveConfig returns valid config")
    void createEffectiveConfigReturnsValidConfig() {
        CodecModule module = CodecModule.withDefaults();
        assertNotNull(module.createEffectiveConfig());
    }
}
