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
package org.eclipse.fennec.codec.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for global ignore feature names functionality.
 *
 * @see <a href="docs/codec-v2-spec/04-global-options.md#global-ignore-features">Spec: Global Ignore Features</a>
 */
@DisplayName("CodecModule global ignore features")
class CodecModuleGlobalIgnoreTest extends CodecModuleTestBase {

    @Test
    @DisplayName("getGlobalIgnoreFeatureNames returns empty list by default")
    void getGlobalIgnoreFeatureNamesReturnsEmptyByDefault() {
        assertTrue(module.getGlobalIgnoreFeatureNames().isEmpty());
    }

    @Test
    @DisplayName("getGlobalIgnoreFeatureNames returns configured names")
    void getGlobalIgnoreFeatureNamesReturnsConfiguredNames() {
        CodecModule customModule = CodecModule.builder()
                .globalIgnoreFeatures(List.of("createdAt", "updatedAt"))
                .build();
        assertEquals(2, customModule.getGlobalIgnoreFeatureNames().size());
        assertTrue(customModule.getGlobalIgnoreFeatureNames().contains("createdAt"));
        assertTrue(customModule.getGlobalIgnoreFeatureNames().contains("updatedAt"));
    }

    @Test
    @DisplayName("getGlobalIgnoreFeatureNames returns unmodifiable list")
    void getGlobalIgnoreFeatureNamesReturnsUnmodifiableList() {
        CodecModule customModule = CodecModule.builder()
                .globalIgnoreFeatures(List.of("createdAt"))
                .build();
        assertThrows(UnsupportedOperationException.class, () ->
                customModule.getGlobalIgnoreFeatureNames().add("hack"));
    }

    @Test
    @DisplayName("isGloballyIgnored returns true for ignored features")
    void isGloballyIgnoredReturnsTrueForIgnoredFeatures() {
        CodecModule customModule = CodecModule.builder()
                .globalIgnoreFeatures(List.of("createdAt", "updatedAt"))
                .build();
        assertTrue(customModule.isGloballyIgnored("createdAt"));
        assertTrue(customModule.isGloballyIgnored("updatedAt"));
    }

    @Test
    @DisplayName("isGloballyIgnored returns false for non-ignored features")
    void isGloballyIgnoredReturnsFalseForNonIgnoredFeatures() {
        CodecModule customModule = CodecModule.builder()
                .globalIgnoreFeatures(List.of("createdAt"))
                .build();
        assertFalse(customModule.isGloballyIgnored("name"));
    }

    @Test
    @DisplayName("isGloballyIgnored returns false for null")
    void isGloballyIgnoredReturnsFalseForNull() {
        assertFalse(module.isGloballyIgnored(null));
    }

    @Test
    @DisplayName("isGloballyIgnored returns false when no ignores configured")
    void isGloballyIgnoredReturnsFalseWhenNoIgnoresConfigured() {
        assertFalse(module.isGloballyIgnored("anything"));
    }
}
