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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.diagnostic.CodecDiagnostic;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A config's own validation has to be heard by every caller, not only the first (issue #183).
 * <p>
 * The resolved configs are cached per EClass, and each {@code validate(diagnostics)} call sat
 * inside that cache's {@code computeIfAbsent}. Validation therefore ran once per EClass per
 * resolver, reporting into whichever collector happened to arrive first; every later caller got
 * the cached config and heard nothing. A collector belongs to one operation while a resolver is
 * built once and shared, so this silenced Layer 2 of spec 15-error-handling.md for every
 * operation after the first.
 * </p>
 * <p>
 * The cache itself is the point and stays: id config is resolved once per object on write and
 * once per property on read. So the validation is run once and what it reported is captured,
 * then replayed into each caller's collector - once per collector, so a repeated resolution
 * within one operation stays quiet.
 * </p>
 */
@DisplayName("Config validation is replayed for every collector")
class ConfigValidationReplayTest {

    private EClass personClass;
    private DiagnosticCollector first;

    @BeforeEach
    void setUp() {
        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        first = new DiagnosticCollector();
    }

    @Test
    @DisplayName("a class-scoped validation warning reaches a second collector")
    void validationReachesASecondCollector() {
        // TypeConfig.validate: typeNameKey is meaningless unless the format is STRUCTURED.
        ConfigurationResolver resolver = resolverWith(Map.of("typeNameKey", "kind"));

        resolver.resolveTypeConfig(personClass, first);
        assertTrue(messages(first).stream().anyMatch(m -> m.contains("typeNameKey")),
                "the first collector, was: " + messages(first));

        DiagnosticCollector second = new DiagnosticCollector();
        resolver.resolveTypeConfig(personClass, second);

        assertTrue(messages(second).stream().anyMatch(m -> m.contains("typeNameKey")),
                "the cache must not silence the rule for a later operation, was: "
                        + messages(second));
    }

    @Test
    @DisplayName("a validation error reaches a second collector too")
    void validationErrorReachesASecondCollector() {
        // IdConfig.validate: COMBINED without idFeatures is an error, not a warning.
        ConfigurationResolver resolver = resolverWith(Map.of("idStrategy", "COMBINED"));

        resolver.resolveIdConfig(personClass, first);
        assertTrue(errorMessages(first).stream().anyMatch(m -> m.contains("idFeatures")),
                "the first collector, was: " + errorMessages(first));

        DiagnosticCollector second = new DiagnosticCollector();
        resolver.resolveIdConfig(personClass, second);

        assertTrue(errorMessages(second).stream().anyMatch(m -> m.contains("idFeatures")),
                "an error is the last thing that may be dropped, was: " + errorMessages(second));
    }

    @Test
    @DisplayName("a global validation warning reaches a second collector")
    void globalValidationReachesASecondCollector() {
        ConfigurationResolver resolver = resolverWith(Map.of("typeNameKey", "kind"));

        resolver.resolveGlobalTypeConfig(first);
        assertTrue(messages(first).stream().anyMatch(m -> m.contains("typeNameKey")),
                "the first collector, was: " + messages(first));

        DiagnosticCollector second = new DiagnosticCollector();
        resolver.resolveGlobalTypeConfig(second);

        assertTrue(messages(second).stream().anyMatch(m -> m.contains("typeNameKey")),
                "the lazily built global configs have the same problem, was: "
                        + messages(second));
    }

    @Test
    @DisplayName("one collector hears a validation warning once, not once per resolution")
    void validationIsNotRepeatedForOneCollector() {
        ConfigurationResolver resolver = resolverWith(Map.of("typeNameKey", "kind"));

        resolver.resolveTypeConfig(personClass, first);
        resolver.resolveTypeConfig(personClass, first);
        resolver.resolveTypeConfig(personClass, first);

        assertEquals(1, messages(first).stream().filter(m -> m.contains("typeNameKey")).count(),
                "config is resolved per object on write and per property on read, was: "
                        + messages(first));
    }

    @Test
    @DisplayName("the resolved config stays cached as one instance")
    void theConfigInstanceStaysCached() {
        ConfigurationResolver resolver = resolverWith(Map.of("typeNameKey", "kind"));

        TypeConfig once = resolver.resolveTypeConfig(personClass, first);
        TypeConfig twice = resolver.resolveTypeConfig(personClass, new DiagnosticCollector());

        assertSame(once, twice,
                "this is a hot path; replaying the diagnostics must not cost a new config");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static ConfigurationResolver resolverWith(Map<String, Object> resourceProperties) {
        return ConfigurationResolver.builder()
                .resourceProperties(resourceProperties)
                .build();
    }

    private static List<String> messages(DiagnosticCollector collector) {
        return collector.getWarnings().stream().map(CodecDiagnostic::getMessage).toList();
    }

    private static List<String> errorMessages(DiagnosticCollector collector) {
        return collector.getErrors().stream().map(CodecDiagnostic::getMessage).toList();
    }
}
