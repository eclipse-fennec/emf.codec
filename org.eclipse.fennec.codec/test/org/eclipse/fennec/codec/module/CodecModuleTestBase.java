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

import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for {@link CodecModule} tests.
 *
 * @see <a href="docs/codec-v2-spec/09-jackson-module.md">Spec 9: Jackson Module Integration</a>
 */
abstract class CodecModuleTestBase {

    protected CodecModule module;

    @BeforeEach
    void setUp() {
        module = CodecModule.withDefaults();
    }
}
