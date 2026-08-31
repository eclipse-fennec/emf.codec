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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.diagnostic.CodecDiagnostic;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A configured value that cannot be parsed has to be reported, not swallowed (issue #174).
 * <p>
 * Every conversion in {@link ConfigMergeHelper} used to fall back in silence, so a typo in an
 * {@code .ecore} annotation or a load option produced a codec that quietly behaved as if the
 * setting had never been written. That is indistinguishable from a setting that is not
 * implemented - which is exactly what sent the reporter of issue #171 looking in the wrong
 * place - and for {@code getBoolean} it was worse than silence: {@code Boolean.parseBoolean}
 * turned every unrecognised string into {@code false}, inverting the intent of
 * {@code typeInclude="yes"}.
 * </p>
 */
@DisplayName("Invalid config values are reported")
class ConfigValueValidationTest {

    private EClass personClass;
    private DiagnosticCollector diagnostics;

    @BeforeEach
    void setUp() {
        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        diagnostics = new DiagnosticCollector();
    }

    @Test
    @DisplayName("an unparseable enum value is reported and ignored")
    void unparseableEnumIsReported() {
        TypeConfig config = resolverWith(Map.of("typeStrategy", "NOPE"))
                .resolveTypeConfig(personClass, diagnostics);

        assertEquals(TypeStrategy.URI, config.getStrategy(), "the default still applies");
        assertTrue(messages().stream().anyMatch(m -> m.contains("typeStrategy")
                        && m.contains("NOPE") && m.contains("URI") && m.contains("NAME")),
                "the report has to name the property, the value and the legal values, was: "
                        + messages());
    }

    @Test
    @DisplayName("an enum value in the wrong case is accepted and not reported")
    void wrongCaseEnumIsAccepted() {
        TypeConfig config = resolverWith(Map.of("typeStrategy", "name"))
                .resolveTypeConfig(personClass, diagnostics);

        assertEquals(TypeStrategy.NAME, config.getStrategy());
        assertTrue(messages().isEmpty(), "case-insensitive matching is intended, was: " + messages());
    }

    @Test
    @DisplayName("a valid value is not reported")
    void validValueIsNotReported() {
        TypeConfig config = resolverWith(Map.of("typeStrategy", "NAME", "typeKey", "kind"))
                .resolveTypeConfig(personClass, diagnostics);

        assertEquals(TypeStrategy.NAME, config.getStrategy());
        assertTrue(messages().isEmpty(), "was: " + messages());
    }

    @Test
    @DisplayName("an unparseable boolean is reported and does not become false")
    void unparseableBooleanIsReported() {
        TypeConfig config = resolverWith(Map.of("typeInclude", "yes"))
                .resolveTypeConfig(personClass, diagnostics);

        assertTrue(config.isInclude(),
                "'yes' is not 'false' - the default (true) has to survive an unparseable value");
        assertTrue(messages().stream().anyMatch(m -> m.contains("typeInclude") && m.contains("yes")),
                "was: " + messages());
    }

    @Test
    @DisplayName("an unparseable integer is reported")
    void unparseableIntegerIsReported() {
        ReferenceConfig config = resolverWith(Map.of("expandDepth", "lots"))
                .resolveReferenceConfig(reference(), diagnostics);

        assertEquals(1, config.getExpandDepth(), "the default still applies");
        assertTrue(messages().stream().anyMatch(m -> m.contains("expandDepth") && m.contains("lots")),
                "was: " + messages());
    }

    @Test
    @DisplayName("a bad value is reported wherever it is configured, not only in load options")
    void classScopedBadValueIsReported() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .annotationProperties(Map.of(ConfigProperty.ECLASS_CONFIG.getKey(),
                        Map.of(personClass, Map.of("typeStrategy", "NOPE"))))
                .build();

        resolver.resolveTypeConfig(personClass, diagnostics);

        assertTrue(messages().stream().anyMatch(m -> m.contains("typeStrategy") && m.contains("NOPE")),
                "an annotation is the level where a typo is least visible afterwards, was: "
                        + messages());
    }

    @Test
    @DisplayName("the same bad value is reported once, not once per resolution")
    void reportedOnce() {
        ConfigurationResolver resolver = resolverWith(Map.of("typeStrategy", "NOPE"));

        resolver.resolveTypeConfig(personClass, diagnostics);
        resolver.resolveTypeConfig(personClass, diagnostics);

        assertEquals(1, messages().stream().filter(m -> m.contains("typeStrategy")).count(),
                "a repeated report buries the diagnostics it sits among, was: " + messages());
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private ConfigurationResolver resolverWith(Map<String, Object> resourceProperties) {
        return ConfigurationResolver.builder()
                .resourceProperties(resourceProperties)
                .build();
    }

    private org.eclipse.emf.ecore.EReference reference() {
        org.eclipse.emf.ecore.EReference ref = EcoreFactory.eINSTANCE.createEReference();
        ref.setName("friend");
        ref.setEType(personClass);
        personClass.getEStructuralFeatures().add(ref);
        return ref;
    }

    private java.util.List<String> messages() {
        return diagnostics.getWarnings().stream().map(CodecDiagnostic::getMessage).toList();
    }
}
