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
package org.eclipse.fennec.codec.ser;

import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for {@link CodecEObjectSerializer} tests.
 *
 * @see <a href="docs/codec-v2-spec/06-eobject-serialization.md">Spec 6: EObject Serialization</a>
 */
abstract class CodecEObjectSerializerTestBase {

    protected EffectiveCodecConfig effectiveConfig;

    @BeforeEach
    void setUp() {
        effectiveConfig = EffectiveCodecConfig.builder()
                .resolver(ConfigurationResolver.defaults())
                .diagnostics(new DiagnosticCollector())
                .build();
    }
}
