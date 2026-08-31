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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * What the codec notices while writing has to reach the caller (issue #184).
 * <p>
 * {@code ContextHelper} has had {@code addWarning(SerializationContext, ...)} and its error
 * twin all along, and neither ever had a caller. The write side reported to the JUL logger
 * instead, so a caller inspecting the resource after a save learned nothing - not even that the
 * document just written will not read back correctly. 15-error-handling.md §5.2 describes a
 * whole table of serialization diagnostics; none of it could be observed.
 * </p>
 * <p>
 * All of these are warnings rather than errors: every one of them describes the codec falling
 * back to something workable, not refusing to write. So a save that succeeded before still
 * succeeds - what changes is that it says what it did.
 * </p>
 */
@DisplayName("Serialization diagnostics reach the resource")
class SerializationDiagnosticsTest {

    private static final String NS_URI = "urn:codec:serdiag:test";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;
    private EAttribute firstName;
    private EAttribute lastName;
    private EReference friend;

    @BeforeEach
    void setUp() {
        EcoreFactory f = EcoreFactory.eINSTANCE;

        personClass = f.createEClass();
        personClass.setName("Person");
        firstName = f.createEAttribute();
        firstName.setName("firstName");
        firstName.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(firstName);
        lastName = f.createEAttribute();
        lastName.setName("lastName");
        lastName.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(lastName);
        friend = f.createEReference();
        friend.setName("friend");
        friend.setEType(personClass);
        friend.setContainment(true);
        personClass.getEStructuralFeatures().add(friend);

        testPackage = f.createEPackage();
        testPackage.setName("serdiag");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("serdiag");
        testPackage.getEClassifiers().add(personClass);

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
    @DisplayName("an id component containing the separator is reported")
    void idComponentContainingTheSeparatorIsReported() throws IOException {
        // A combined id whose component carries the separator cannot be split back apart. The
        // codec knows the document will not round-trip and used to tell only the log (#101).
        CodecResource resource = newResource(Map.of(
                "idStrategy", "COMBINED",
                "idFeatures", List.of("firstName", "lastName")));
        resource.getContents().add(person("Ada", "Smith-Jones"));

        resource.save(new ByteArrayOutputStream(), null);

        assertTrue(warnings(resource).stream()
                        .anyMatch(m -> m.contains("round-trip") && m.contains("lastName")),
                "the caller is the only one who can fix this, was: " + warnings(resource));
    }

    @Test
    @DisplayName("an unregistered id value writer is reported")
    void unregisteredIdValueWriterIsReported() throws IOException {
        CodecResource resource = newResource(Map.of("idValueWriterName", "noSuchWriter"));
        resource.getContents().add(person("Ada", "Lovelace"));

        resource.save(new ByteArrayOutputStream(), null);

        assertTrue(warnings(resource).stream()
                        .anyMatch(m -> m.contains("noSuchWriter")),
                "a configured writer that is not there must not be a silent no-op, was: "
                        + warnings(resource));
    }

    @Test
    @DisplayName("a deprecated per-feature writer option ignored for a reference is reported")
    void deprecatedFeatureWriterOptionIsReported() throws IOException {
        CodecResource resource = newResource(Map.of());
        resource.getContents().add(personWithFriend());

        // The deprecated map is keyed by the feature instance, not by "Class.feature".
        Map<Object, Object> options = Map.of(
                CodecOptions.CODEC_FEATURE_VALUE_WRITERS, Map.of(friend, "someWriter"));
        resource.save(new ByteArrayOutputStream(), options);

        assertTrue(warnings(resource).stream()
                        .anyMatch(m -> m.contains("deprecated") || m.contains("ignored")),
                "an option the codec refuses to honour has to say so, was: "
                        + warnings(resource));
    }

    @Test
    @DisplayName("a clean save reports nothing")
    void aCleanSaveIsSilent() throws IOException {
        CodecResource resource = newResource(Map.of());
        resource.getContents().add(person("Ada", "Lovelace"));

        resource.save(new ByteArrayOutputStream(), null);

        assertTrue(warnings(resource).isEmpty(),
                "nothing was degraded, so nothing is reported, was: " + warnings(resource));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private EObject person(String first, String last) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(firstName, first);
        person.eSet(lastName, last);
        return person;
    }

    private EObject personWithFriend() {
        EObject ada = person("Ada", "Lovelace");
        ada.eSet(friend, person("Charles", "Babbage"));
        return ada;
    }

    private CodecResource newResource(Map<String, Object> resourceProperties) {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(resourceProperties)
                .build();
        return new CodecResource(URI.createURI("serdiag.json"), metadataService, resolver, null);
    }

    private static List<String> warnings(Resource resource) {
        return resource.getWarnings().stream().map(Resource.Diagnostic::getMessage).toList();
    }
}
