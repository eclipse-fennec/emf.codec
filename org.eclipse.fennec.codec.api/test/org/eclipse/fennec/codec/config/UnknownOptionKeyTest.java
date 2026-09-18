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
package org.eclipse.fennec.codec.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.diagnostic.CodecDiagnostic;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * A key in the {@code codec.} namespace claims the codec owns it, so one the codec does not
 * know is a typo or a stale spelling - not a caller's private runtime option (issue #220).
 * <p>
 * Issue #174 deliberately left unknown keys alone, because a property map legitimately carries
 * keys that are not {@code ConfigProperty} keys. That exclusion is what let
 * {@code codec.serializeDefaults} sit in {@code CodecOptions} being read by nobody: the value
 * was dropped, the behaviour was the default, and nothing anywhere said so. Narrowing the check
 * to the codec's own namespace keeps the #174 rationale intact and still catches that class of
 * bug.
 * </p>
 */
@DisplayName("Unknown codec. option keys are reported")
class UnknownOptionKeyTest {

    private EClass personClass;
    private DiagnosticCollector diagnostics;

    @BeforeEach
    void setUp() {
        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        diagnostics = new DiagnosticCollector();
    }

    private void resolveWith(Map<String, Object> options) {
        ConfigurationResolver.builder()
                .optionsProperties(options)
                .build()
                .resolveTypeConfig(personClass, diagnostics);
    }

    private List<String> messages() {
        return diagnostics.getWarnings().stream().map(CodecDiagnostic::getMessage).toList();
    }

    @Nested
    @DisplayName("reported")
    class Reported {

        @Test
        @DisplayName("the key that started this: codec.serializeDefaults")
        void staleSerializeDefaults() {
            resolveWith(Map.of("codec.serializeDefaults", Boolean.TRUE));

            assertTrue(messages().stream().anyMatch(m -> m.contains("codec.serializeDefaults")),
                    () -> "an unknown codec. key must be named in a warning, was: " + messages());
        }

        @Test
        @DisplayName("the report suggests the key that was probably meant")
        void suggestsTheNearMiss() {
            resolveWith(Map.of("codec.serializeDefaults", Boolean.TRUE));

            assertTrue(messages().stream().anyMatch(m -> m.contains("codec.serializeDefault")),
                    () -> "a near-miss should be suggested, was: " + messages());
        }

        @Test
        @DisplayName("an outright typo is reported even with nothing to suggest")
        void plainTypo() {
            resolveWith(Map.of("codec.thisIsNotAnOption", "x"));

            assertTrue(messages().stream().anyMatch(m -> m.contains("codec.thisIsNotAnOption")),
                    () -> "an unknown codec. key must be reported, was: " + messages());
        }

        @Test
        @DisplayName("the source is named, as the value reports do")
        void namesTheSource() {
            resolveWith(Map.of("codec.thisIsNotAnOption", "x"));

            assertTrue(messages().stream().anyMatch(m -> m.contains("load/save options")),
                    () -> "the warning should say where the key came from, was: " + messages());
        }
    }

    @Nested
    @DisplayName("not reported")
    class NotReported {

        @Test
        @DisplayName("a known option key")
        void knownKey() {
            resolveWith(Map.of("codec.typeStrategy", "NAME"));

            assertTrue(messages().isEmpty(),
                    () -> "a known key must not be reported, was: " + messages());
        }

        @Test
        @DisplayName("the short, unprefixed form of a known key")
        void shortKey() {
            resolveWith(Map.of("typeStrategy", "NAME"));

            assertTrue(messages().isEmpty(),
                    () -> "the short form is a legal spelling, was: " + messages());
        }

        @Test
        @DisplayName("a format-namespaced key, which another bundle owns")
        void formatNamespacedKey() {
            resolveWith(Map.of(
                    "codec.ods.styleHeader", Boolean.TRUE,
                    "codec.csv.delimiter", ";",
                    "codec.tabular.referenceMode", "FLAT"));

            assertTrue(messages().isEmpty(),
                    () -> "codec.<format>.* belongs to a format bundle this one cannot see, was: "
                            + messages());
        }

        @Test
        @DisplayName("a key outside the codec namespace entirely")
        void foreignKey() {
            resolveWith(Map.of("myApp.somethingElse", "x", "ClassName.featureName", Map.of()));

            assertTrue(messages().isEmpty(),
                    () -> "a caller's own key is none of the codec's business, was: " + messages());
        }
    }

    /**
     * The known set is derived from {@link ConfigProperty} and {@link CodecOptions} rather than
     * listed by hand, so that a constant added to either is known here without a second edit -
     * a hand-kept list drifting from the constants it mirrors being the very bug this reports.
     */
    @Nested
    @DisplayName("the known set follows the constants")
    class KnownSet {

        @Test
        @DisplayName("a runtime-only CodecOptions key is known, though it is no ConfigProperty")
        void runtimeOnlyOptionIsKnown() {
            assertTrue(KnownOptionKeys.isKnown(CodecOptions.CODEC_ROOT_TYPE),
                    "codec.rootType is a load option, not a ConfigProperty, and is still read");
        }

        @Test
        @DisplayName("a constant whose declaration wraps lines is known too")
        void wrappedDeclarationIsKnown() {
            // Read off the field, so how the source happens to be wrapped cannot matter.
            assertTrue(KnownOptionKeys.isKnown(CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS),
                    "the set is read from the fields of CodecOptions, not parsed from its source");
        }

        @Test
        @DisplayName("every ConfigProperty is known under both spellings")
        void everyConfigPropertyIsKnown() {
            for (ConfigProperty property : ConfigProperty.values()) {
                assertTrue(KnownOptionKeys.isKnown(property.getPropertyKey()),
                        () -> property.getPropertyKey() + " must be known");
                assertTrue(KnownOptionKeys.isKnown(property.getKey()),
                        () -> property.getKey() + " must be known");
            }
        }

        @Test
        @DisplayName("a bundle can declare a key this one cannot see")
        void registeredKeyIsKnown() {
            assertTrue(!KnownOptionKeys.isKnown("codec.someDownstreamKey"),
                    "unknown before registering");

            KnownOptionKeys.register("codec.someDownstreamKey");

            assertTrue(KnownOptionKeys.isKnown("codec.someDownstreamKey"),
                    "a registered key must not be reported");
        }

        @Test
        @DisplayName("no suggestion is offered when nothing is close")
        void noSuggestionWhenNothingIsClose() {
            assertTrue(KnownOptionKeys.suggest("codec.thisIsNotAnOption").isEmpty(),
                    () -> "a least-bad guess is worse than no guess, got: "
                            + KnownOptionKeys.suggest("codec.thisIsNotAnOption"));
        }
    }
}
