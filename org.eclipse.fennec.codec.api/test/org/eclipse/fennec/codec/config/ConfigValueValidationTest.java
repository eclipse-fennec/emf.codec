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
import org.eclipse.fennec.codec.metadata.model.codec.IdKeyMode;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
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

    @Test
    @DisplayName("a class-only id property scoped to a feature is reported, not applied")
    void classOnlyIdPropertyOnAFeatureIsReported() {
        // Only idKey and idFormat may be scoped to a reference (issue #176, spec 09-id.md
        // §4.4); an identity is otherwise class-intrinsic. Dropping the rest in silence is the
        // failure that issue was filed about, so dropping it loudly is the fix.
        org.eclipse.emf.ecore.EReference friend = reference();
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of("Person",
                        Map.of("friend", Map.of("idKeyMode", "BOTH", "idKey", "personId"))))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, friend, diagnostics);

        assertEquals("personId", config.getKey(), "idKey is feature-level and applies");
        assertEquals(IdKeyMode.ID_ONLY, config.getKeyMode(),
                "idKeyMode is class-intrinsic and must not be taken from the feature");
        assertTrue(messages().stream().anyMatch(m -> m.contains("idKeyMode")
                        && m.contains("Person.friend")),
                "the refusal has to name the property and the feature, was: " + messages());
    }

    @Test
    @DisplayName("idKey and idFormat on a non-containment reference are reported, not applied")
    void idKeyOnANonContainmentReferenceIsReported() {
        // A non-containment reference writes a _ref, not the target's body, so there is no id
        // key in that position to rename (issue #189, spec 09-id.md §4.4). Applying the value
        // would change nothing; dropping it in silence is the trap.
        org.eclipse.emf.ecore.EReference friend = reference(false);
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of("Person",
                        Map.of("friend", Map.of("idKey", "personId", "idFormat", "STRUCTURED"))))
                .build();

        IdConfig config = resolver.resolveIdConfig(personClass, friend, diagnostics);

        assertEquals("_id", config.getKey(), "the class-level key stands");
        assertEquals(SerializationFormat.PLAIN, config.getFormat(), "the class-level format stands");
        for (String key : new String[] { "idKey", "idFormat" }) {
            assertTrue(messages().stream().anyMatch(m -> m.contains(key)
                            && m.contains("Person.friend") && m.contains("containment")),
                    "the refusal has to name '" + key + "', the feature and the rule, was: "
                            + messages());
        }
    }

    @Test
    @DisplayName("a feature-scoped idKey is reported once, not once per resolution")
    void classOnlyIdPropertyReportedOnce() {
        org.eclipse.emf.ecore.EReference friend = reference();
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of("Person", Map.of("friend", Map.of("idOnTop", "false"))))
                .build();

        resolver.resolveIdConfig(personClass, friend, diagnostics);
        resolver.resolveIdConfig(personClass, friend, diagnostics);

        assertEquals(1, messages().stream().filter(m -> m.contains("idOnTop")).count(),
                "id config is resolved once per property on read; a repeated warning would bury"
                        + " everything around it, was: " + messages());
    }

    @Test
    @DisplayName("a reused resolver reports again for a second collector")
    void reportedAgainForASecondCollector() {
        // A ConfigurationResolver is cached and shared across operations, while the collector
        // belongs to one operation. Deduplicating per resolver would tell the first operation
        // and silently leave every later one in the dark.
        org.eclipse.emf.ecore.EReference friend = reference();
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of("Person", Map.of("friend", Map.of("idOnTop", "false"))))
                .build();

        resolver.resolveIdConfig(personClass, friend, diagnostics);
        DiagnosticCollector second = new DiagnosticCollector();
        resolver.resolveIdConfig(personClass, friend, second);

        assertEquals(1, messages().stream().filter(m -> m.contains("idOnTop")).count());
        assertEquals(1, second.getWarnings().stream()
                        .filter(w -> w.getMessage().contains("idOnTop")).count(),
                "the second operation has to hear about it too, was: " + second.getWarnings());
    }

    @Test
    @DisplayName("an unusable value is reported again for a second collector")
    void unusableValueReportedAgainForASecondCollector() {
        ConfigurationResolver resolver = resolverWith(Map.of("typeStrategy", "NOPE"));

        resolver.resolveTypeConfig(personClass, diagnostics);
        DiagnosticCollector second = new DiagnosticCollector();
        resolver.resolveTypeConfig(personClass, second);

        assertEquals(1, second.getWarnings().stream()
                        .filter(w -> w.getMessage().contains("typeStrategy")).count(),
                "was: " + second.getWarnings());
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private ConfigurationResolver resolverWith(Map<String, Object> resourceProperties) {
        return ConfigurationResolver.builder()
                .resourceProperties(resourceProperties)
                .build();
    }

    /** A containment reference: the place a reference-scoped id key applies (09-id.md §4.4). */
    private org.eclipse.emf.ecore.EReference reference() {
        return reference(true);
    }

    private org.eclipse.emf.ecore.EReference reference(boolean containment) {
        org.eclipse.emf.ecore.EReference ref = EcoreFactory.eINSTANCE.createEReference();
        ref.setName("friend");
        ref.setEType(personClass);
        ref.setContainment(containment);
        personClass.getEStructuralFeatures().add(ref);
        return ref;
    }

    private java.util.List<String> messages() {
        return diagnostics.getWarnings().stream().map(CodecDiagnostic::getMessage).toList();
    }
}
