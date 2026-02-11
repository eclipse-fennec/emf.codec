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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.core.Version;

/**
 * Tests for {@link CodecModule} version and name.
 *
 * @see <a href="docs/codec-v2-spec/09-jackson-module.md">Spec 9: Jackson Module Integration</a>
 */
@DisplayName("CodecModule version and name")
class CodecModuleVersionTest extends CodecModuleTestBase {

    @Test
    @DisplayName("getModuleName returns default name")
    void getModuleNameReturnsDefaultName() {
        assertEquals("fennec-codec-module", module.getModuleName());
    }

    @Test
    @DisplayName("getModuleName returns custom name")
    void getModuleNameReturnsCustomName() {
        CodecModule custom = CodecModule.builder()
                .moduleName("custom-module")
                .build();
        assertEquals("custom-module", custom.getModuleName());
    }

    @Test
    @DisplayName("version returns 2.0.0")
    void versionReturnsCorrectVersion() {
        Version version = module.version();
        assertEquals(2, version.getMajorVersion());
        assertEquals(0, version.getMinorVersion());
        assertEquals(0, version.getPatchLevel());
    }

    @Test
    @DisplayName("version includes correct groupId and artifactId")
    void versionIncludesCorrectIds() {
        Version version = module.version();
        assertEquals("org.eclipse.fennec", version.getGroupId());
        assertEquals("org.eclipse.fennec.codec", version.getArtifactId());
    }
}
