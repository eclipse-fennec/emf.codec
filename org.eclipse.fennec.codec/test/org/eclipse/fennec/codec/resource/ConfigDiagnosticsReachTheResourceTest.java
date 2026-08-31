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
package org.eclipse.fennec.codec.resource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Configuration diagnostics reach the resource (issue #182).
 * <p>
 * The codec keeps two collectors. Runtime diagnostics travel on a context attribute that
 * {@code CodecResource} sets per operation and drains into the resource, and those always
 * worked. Configuration diagnostics travel in the {@code DiagnosticCollector} that
 * {@code EffectiveCodecConfig} holds - and the one the serializers and deserializers actually
 * use came from {@code CodecModule.createEffectiveConfig()}, which built a throwaway nobody
 * ever read. So every rule the configuration layer enforces was enforced in private.
 * </p>
 */
@DisplayName("Config diagnostics reach the resource")
class ConfigDiagnosticsReachTheResourceTest {

    private static final String NS_URI = "urn:codec:configdiag:test";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;
    private EClass teamClass;
    private EAttribute personName;
    private EReference members;

    @BeforeEach
    void setUp() {
        EcoreFactory f = EcoreFactory.eINSTANCE;

        personClass = f.createEClass();
        personClass.setName("Person");
        personName = f.createEAttribute();
        personName.setName("name");
        personName.setEType(EcorePackage.Literals.ESTRING);
        personName.setID(true);
        personClass.getEStructuralFeatures().add(personName);

        teamClass = f.createEClass();
        teamClass.setName("Team");
        members = f.createEReference();
        members.setName("members");
        members.setEType(personClass);
        members.setContainment(true);
        members.setUpperBound(-1);
        teamClass.getEStructuralFeatures().add(members);

        testPackage = f.createEPackage();
        testPackage.setName("configdiag");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("configdiag");
        testPackage.getEClassifiers().add(personClass);
        testPackage.getEClassifiers().add(teamClass);

        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(testPackage);
        EPackage.Registry.INSTANCE.put(NS_URI, testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    @Test
    @DisplayName("an unparseable config value is reported on save")
    void unparseableValueIsReportedOnSave() throws IOException {
        CodecResource resource = newResource(Map.of("typeStrategy", "NOPE"));
        resource.getContents().add(team());

        resource.save(new ByteArrayOutputStream(), null);

        assertTrue(warnings(resource).stream()
                        .anyMatch(m -> m.contains("typeStrategy") && m.contains("NOPE")),
                "issue #174 reports this; the caller has to be able to see it, was: "
                        + warnings(resource));
    }

    @Test
    @DisplayName("an unparseable config value is reported on load")
    void unparseableValueIsReportedOnLoad() throws IOException {
        String json = saveWith(Map.of());

        CodecResource resource = newResource(Map.of("typeStrategy", "NOPE"));
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)),
                Map.of(CodecResource.CODEC_ROOT_TYPE, teamClass));

        assertTrue(warnings(resource).stream()
                        .anyMatch(m -> m.contains("typeStrategy") && m.contains("NOPE")),
                "was: " + warnings(resource));
    }

    @Test
    @DisplayName("a class-only id property scoped to a feature is reported on save")
    void classOnlyIdPropertyIsReportedOnSave() throws IOException {
        Map<String, Object> featureScoped = new HashMap<>();
        featureScoped.put("members", Map.of("idKeyMode", "BOTH"));

        CodecResource resource = newResource(Map.of("Team", featureScoped));
        resource.getContents().add(team());

        resource.save(new ByteArrayOutputStream(), null);

        assertTrue(warnings(resource).stream().anyMatch(m -> m.contains("idKeyMode")),
                "issue #176 reports this at the resolver; it has to arrive here, was: "
                        + warnings(resource));
    }

    @Test
    @DisplayName("a validate() warning reaches the resource")
    void validateWarningReachesTheResource() throws IOException {
        // TypeConfig.validate: typeNameKey is only meaningful in STRUCTURED format.
        CodecResource resource = newResource(Map.of("typeNameKey", "kind"));
        resource.getContents().add(team());

        resource.save(new ByteArrayOutputStream(), null);

        assertTrue(warnings(resource).stream().anyMatch(m -> m.contains("typeNameKey")),
                "spec 15 Layer 2 has to be audible, was: " + warnings(resource));
    }

    @Test
    @DisplayName("a validate() warning reaches the resource on a second operation too")
    void validateWarningReachesASecondOperation() throws IOException {
        // The resolved configs are cached per EClass and validate() runs inside that cache, so
        // the second operation on a shared resolver used to get nothing. A resolver is built
        // once and reused; the collector belongs to one operation.
        ConfigurationResolver shared = ConfigurationResolver.builder()
                .resourceProperties(Map.of("typeNameKey", "kind"))
                .build();

        CodecResource first = resourceWith(shared);
        first.getContents().add(team());
        first.save(new ByteArrayOutputStream(), null);
        assertTrue(warnings(first).stream().anyMatch(m -> m.contains("typeNameKey")),
                "first operation, was: " + warnings(first));

        CodecResource second = resourceWith(shared);
        second.getContents().add(team());
        second.save(new ByteArrayOutputStream(), null);

        assertTrue(warnings(second).stream().anyMatch(m -> m.contains("typeNameKey")),
                "the cache must not silence the rule for every later operation, was: "
                        + warnings(second));
    }

    @Test
    @DisplayName("the cross-config error reaches the resource")
    void crossConfigErrorReachesTheResource() throws IOException {
        // spec 15 Layer 3: STRUCTURED + typeStrategy=NONE + superTypeSerialize=true cannot
        // write a supertype inside a _type object that is never written.
        CodecResource resource = newResource(invalidCrossConfig());
        resource.getContents().add(team());

        resource.save(new ByteArrayOutputStream(), null);

        assertTrue(errors(resource).stream()
                        .anyMatch(m -> m.contains("superTypeSerialize")),
                "the spec calls this configuration invalid; saying nothing is not an option,"
                        + " was: errors=" + errors(resource) + " warnings=" + warnings(resource));
    }

    @Test
    @DisplayName("the cross-config error fails a STRICT load")
    void crossConfigErrorFailsAStrictLoad() throws IOException {
        String json = saveWith(Map.of());

        CodecResource resource = newResource(invalidCrossConfig());
        Map<Object, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, teamClass);
        options.put(CodecOptions.CODEC_DESERIALIZATION_MODE, "STRICT");

        assertThrows(IOException.class,
                () -> resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options),
                "a config error has to fail a strict load the way a runtime error does");
    }

    private Map<String, Object> invalidCrossConfig() {
        return Map.of("typeFormat", "STRUCTURED",
                "typeStrategy", "NONE",
                "superTypeSerialize", "true");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    @SuppressWarnings("unchecked")
    private EObject team() {
        EObject ada = testPackage.getEFactoryInstance().create(personClass);
        ada.eSet(personName, "Ada");
        EObject team = testPackage.getEFactoryInstance().create(teamClass);
        ((List<EObject>) team.eGet(members)).add(ada);
        return team;
    }

    private String saveWith(Map<String, Object> resourceProperties) throws IOException {
        CodecResource resource = newResource(resourceProperties);
        resource.getContents().add(team());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return out.toString(UTF_8);
    }

    private CodecResource newResource(Map<String, Object> resourceProperties) {
        return resourceWith(ConfigurationResolver.builder()
                .resourceProperties(resourceProperties)
                .build());
    }

    private CodecResource resourceWith(ConfigurationResolver resolver) {
        return new CodecResource(URI.createURI("configdiag.json"), metadataService, resolver, null);
    }

    private static List<String> warnings(Resource resource) {
        return resource.getWarnings().stream().map(Resource.Diagnostic::getMessage).toList();
    }

    private static List<String> errors(Resource resource) {
        return resource.getErrors().stream().map(Resource.Diagnostic::getMessage).toList();
    }
}
