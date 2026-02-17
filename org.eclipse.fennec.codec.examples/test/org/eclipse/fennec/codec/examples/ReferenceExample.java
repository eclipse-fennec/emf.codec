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
package org.eclipse.fennec.codec.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates reference serialization formats: PLAIN, STRUCTURED, and expand.
 *
 * @see <a href="docs/codec-v2-spec/10-reference.md">Spec: Reference Serialization</a>
 */
@DisplayName("Reference Format Examples")
class ReferenceExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-basic.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EClass teamClass;
    private EAttribute personId;
    private EAttribute nameAttr;
    private EAttribute teamNameAttr;
    private EReference membersRef;
    private EReference leadRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper(ReferenceExample.class);
        pkg = ecoreHelper.loadEcoreAbsolute(ECORE);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        personClass = ecoreHelper.getEClass(pkg, "Person");
        teamClass = ecoreHelper.getEClass(pkg, "Team");
        personId = (EAttribute) ecoreHelper.getFeature(personClass, "personId");
        nameAttr = (EAttribute) ecoreHelper.getFeature(personClass, "name");
        teamNameAttr = (EAttribute) ecoreHelper.getFeature(teamClass, "name");
        membersRef = (EReference) ecoreHelper.getFeature(teamClass, "members");
        leadRef = (EReference) ecoreHelper.getFeature(teamClass, "lead");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    @SuppressWarnings("unchecked")
    private EObject createTeam() {
        EObject team = pkg.getEFactoryInstance().create(teamClass);
        team.eSet(teamNameAttr, "Alpha");

        EObject alice = pkg.getEFactoryInstance().create(personClass);
        alice.eSet(personId, "p1");
        alice.eSet(nameAttr, "Alice");

        EObject bob = pkg.getEFactoryInstance().create(personClass);
        bob.eSet(personId, "p2");
        bob.eSet(nameAttr, "Bob");

        List<EObject> members = (List<EObject>) team.eGet(membersRef);
        members.add(alice);
        members.add(bob);
        team.eSet(leadRef, alice);

        return team;
    }

    private EObject roundTrip(EObject object, EClass rootType, ConfigurationResolver resolver) throws IOException {
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://ref.json"), metadataService, resolver, null);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://ref.json"), metadataService, resolver, null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    @Test
    @DisplayName("PLAIN reference format — reference as bare string")
    @SuppressWarnings("unchecked")
    void plainReferenceFormat() throws IOException {
        Map<String, Object> moduleProps = Map.of(
                ConfigProperty.REF_FORMAT.getKey(), "PLAIN"
        );
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .moduleProperties(moduleProps)
                .build();

        EObject team = createTeam();
        EObject loaded = roundTrip(team, teamClass, resolver);

        assertNotNull(loaded);
        List<EObject> loadedMembers = (List<EObject>) loaded.eGet(membersRef);
        assertEquals(2, loadedMembers.size());

        EObject loadedLead = (EObject) loaded.eGet(leadRef);
        assertNotNull(loadedLead);
        assertSame(loadedMembers.get(0), loadedLead);
    }

    @Test
    @DisplayName("STRUCTURED reference format — reference as {$ref: ...} object")
    void structuredReferenceFormat() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();

        EObject team = createTeam();
        EObject loaded = roundTrip(team, teamClass, resolver);

        assertNotNull(loaded);
        EObject loadedLead = (EObject) loaded.eGet(leadRef);
        assertNotNull(loadedLead);
        assertEquals("Alice", loadedLead.eGet(nameAttr));
    }

    @Test
    @DisplayName("Expand reference — inlines referenced object")
    void expandReference() throws IOException {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .expand("lead")
                .build();

        EObject team = createTeam();

        // Serialize to verify expand output
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://expand.json"), metadataService, resolver, null);
        saveResource.getContents().add(team);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);

        // Expanded reference should contain the referenced object's data inline
        assertTrue(json.contains("\"lead\""), "Should contain lead field");
        assertTrue(json.contains("\"Alice\""), "Expanded lead should contain Alice's data");
    }
}
