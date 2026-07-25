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
package org.eclipse.fennec.codec.config.spec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.codec.config.ConfigDirection;
import org.eclipse.fennec.codec.config.ConfigLevel;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.metadata.model.codec.FingerprintMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Spec-based tests for the in-band EPackage fingerprint configuration
 * (issue #73, B.1).
 * <p>
 * Derived from {@code docs/codec-v2-spec/06-type.md} section 8 ("In-Band EPackage
 * Fingerprint"), {@code 03-naming-conventions.md} section 2 (one config key, two
 * placements) and {@code 16-annotation-reference.md}.
 */
@DisplayName("Fingerprint Config Spec Tests (#73 B.1)")
class FingerprintConfigSpecTest {

    // ========================================================================
    // Section 1: Defaults - R1 rests on these
    // Spec: 06-type.md section 8.1, 8.7
    // ========================================================================

    @Nested
    @DisplayName("1. Defaults (spec 8.1 / 8.7)")
    class Defaults {

        @Test
        @DisplayName("1.1 fingerprintMode defaults to NONE - nothing is written unless opted in")
        void fingerprintModeDefaultsToNone() {
            assertSame(FingerprintMode.NONE, TypeConfig.defaults().getFingerprintMode());
        }

        @Test
        @DisplayName("1.2 fingerprintKey defaults to the unprefixed inner key 'fingerprint'")
        void fingerprintKeyDefaultsToInnerForm() {
            assertEquals("fingerprint", TypeConfig.defaults().getFingerprintKey());
        }

        @Test
        @DisplayName("1.3 the PLAIN sibling derives as '_fingerprint'")
        void plainFingerprintKeyDerivesUnderscoreForm() {
            assertEquals("_fingerprint", TypeConfig.defaults().getPlainFingerprintKey());
        }

        @Test
        @DisplayName("1.4 default config writes no fingerprint (R1: existing output unchanged)")
        void defaultConfigWritesNoFingerprint() {
            assertFalse(TypeConfig.defaults().isFingerprintWriteEnabled());
        }
    }

    // ========================================================================
    // Section 2: One config key, two placements
    // Spec: 03-naming-conventions.md section 2, 06-type.md section 8.2
    // ========================================================================

    @Nested
    @DisplayName("2. One config key, two placements (spec 03 section 2)")
    class KeyPlacement {

        @Test
        @DisplayName("2.1 a custom inner key derives the underscore-prefixed PLAIN sibling")
        void customKeyDerivesPlainSibling() {
            TypeConfig config = TypeConfig.builder().fingerprintKey("fp").build();

            assertEquals("fp", config.getFingerprintKey());
            assertEquals("_fp", config.getPlainFingerprintKey());
        }

        @Test
        @DisplayName("2.2 an already underscore-prefixed key is taken as-is in both placements")
        void underscorePrefixedKeyIsNotDoublePrefixed() {
            TypeConfig config = TypeConfig.builder().fingerprintKey("_fp").build();

            assertEquals("_fp", config.getFingerprintKey());
            assertEquals("_fp", config.getPlainFingerprintKey());
        }

        @Test
        @DisplayName("2.3 an @-prefixed key is taken as-is (JSON-LD style)")
        void atPrefixedKeyIsNotPrefixed() {
            TypeConfig config = TypeConfig.builder().fingerprintKey("@fingerprint").build();

            assertEquals("@fingerprint", config.getFingerprintKey());
            assertEquals("@fingerprint", config.getPlainFingerprintKey());
        }
    }

    // ========================================================================
    // Section 3: Merge from a property map (cascading config layers)
    // Spec: 02-config-resolution.md section 11.3
    // ========================================================================

    @Nested
    @DisplayName("3. Merge behavior (spec 02 section 11.3)")
    class Merge {

        @Test
        @DisplayName("3.1 fingerprintMode merges from a property map")
        void fingerprintModeMerges() {
            Map<String, Object> source = new HashMap<>();
            source.put("fingerprintMode", "FIRST_TOUCH");

            TypeConfig merged = TypeConfig.defaults().mergeWith(source);

            assertSame(FingerprintMode.FIRST_TOUCH, merged.getFingerprintMode());
            assertTrue(merged.isFingerprintWriteEnabled());
        }

        @Test
        @DisplayName("3.2 fingerprintKey merges from a property map")
        void fingerprintKeyMerges() {
            Map<String, Object> source = new HashMap<>();
            source.put("fingerprintKey", "modelVersion");

            TypeConfig merged = TypeConfig.defaults().mergeWith(source);

            assertEquals("modelVersion", merged.getFingerprintKey());
            assertEquals("_modelVersion", merged.getPlainFingerprintKey());
        }

        @Test
        @DisplayName("3.3 an absent fingerprint entry leaves the previous layer untouched")
        void absentEntryKeepsPreviousLayer() {
            TypeConfig base = TypeConfig.builder()
                    .fingerprintMode(FingerprintMode.FIRST_TOUCH)
                    .fingerprintKey("fp")
                    .build();

            Map<String, Object> source = new HashMap<>();
            source.put("typeKey", "@type");

            TypeConfig merged = base.mergeWith(source);

            assertSame(FingerprintMode.FIRST_TOUCH, merged.getFingerprintMode());
            assertEquals("fp", merged.getFingerprintKey());
        }

        @Test
        @DisplayName("3.4 toBuilder round-trips both fingerprint values")
        void toBuilderRoundTrips() {
            TypeConfig original = TypeConfig.builder()
                    .fingerprintMode(FingerprintMode.FIRST_TOUCH)
                    .fingerprintKey("fp")
                    .build();

            TypeConfig copy = original.toBuilder().build();

            assertSame(FingerprintMode.FIRST_TOUCH, copy.getFingerprintMode());
            assertEquals("fp", copy.getFingerprintKey());
        }
    }

    // ========================================================================
    // Section 4: ConfigProperty metadata
    // Spec: 02-config-resolution.md section 11.3, 16-annotation-reference.md
    // ========================================================================

    @Nested
    @DisplayName("4. ConfigProperty registration (spec 02 section 11.3)")
    class Registration {

        @Test
        @DisplayName("4.1 fingerprintMode is registered with its spec key and default")
        void fingerprintModeProperty() {
            assertEquals("fingerprintMode", ConfigProperty.FINGERPRINT_MODE.getKey());
            assertEquals("NONE", ConfigProperty.FINGERPRINT_MODE.getDefaultValue());
        }

        @Test
        @DisplayName("4.2 fingerprintKey is registered with its spec key and inner-form default")
        void fingerprintKeyProperty() {
            assertEquals("fingerprintKey", ConfigProperty.FINGERPRINT_KEY.getKey());
            assertEquals("fingerprint", ConfigProperty.FINGERPRINT_KEY.getDefaultValue());
        }

        @Test
        @DisplayName("4.3 both are configurable at GLOBAL and ECLASS level")
        void configurableLevels() {
            assertTrue(ConfigProperty.FINGERPRINT_MODE.getValidLevels().contains(ConfigLevel.GLOBAL));
            assertTrue(ConfigProperty.FINGERPRINT_MODE.getValidLevels().contains(ConfigLevel.ECLASS));
            assertTrue(ConfigProperty.FINGERPRINT_KEY.getValidLevels().contains(ConfigLevel.GLOBAL));
            assertTrue(ConfigProperty.FINGERPRINT_KEY.getValidLevels().contains(ConfigLevel.ECLASS));
        }

        @Test
        @DisplayName("4.4 fingerprintMode is write-only - reading is always liberal (spec 8.4)")
        void fingerprintModeIsWriteOnly() {
            assertTrue(ConfigProperty.FINGERPRINT_MODE.getDirections().contains(ConfigDirection.WRITE));
            assertFalse(ConfigProperty.FINGERPRINT_MODE.getDirections().contains(ConfigDirection.READ),
                    "fingerprintMode must not be a read property: reading accepts a fingerprint "
                    + "wherever it appears, independent of the local write configuration");
        }

        @Test
        @DisplayName("4.5 fingerprintKey applies in both directions (read: caller-side only, spec 8.5)")
        void fingerprintKeyIsBidirectional() {
            assertTrue(ConfigProperty.FINGERPRINT_KEY.getDirections().contains(ConfigDirection.WRITE));
            assertTrue(ConfigProperty.FINGERPRINT_KEY.getDirections().contains(ConfigDirection.READ));
        }
    }
}
