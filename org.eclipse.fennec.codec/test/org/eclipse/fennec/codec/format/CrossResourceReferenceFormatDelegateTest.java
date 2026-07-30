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
package org.eclipse.fennec.codec.format;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.impl.JacksonFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.core.json.JsonFactory;

/**
 * Reproduces issue #50: cross-document (cross-resource) non-containment
 * references must be serialized with the full EMF URI on the FormatDelegate
 * path, not just the bare fragment.
 * <p>
 * The FormatDelegate path uses {@code FormatDelegateGenerator}, whose stream
 * write context is a plain Jackson {@code SimpleStreamWriteContext} rather than
 * a {@code CodecWriteContext}. The serializer must therefore fall back to the
 * {@code ContextHelper.RESOURCE} attribute (mirroring the reader side) to detect
 * cross-document references and to build the correct reference URI.
 * </p>
 *
 * @see <a href="https://github.com/eclipse-fennec/emf.codec/issues/50">Issue #50</a>
 */
@DisplayName("Cross-resource references on the FormatDelegate path (issue #50)")
class CrossResourceReferenceFormatDelegateTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private JacksonFormatProvider formatProvider;

    private EClass personClass;
    private EAttribute nameAttribute;
    private EReference managerRef;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, CrossResourceReferenceFormatDelegateTest.class);

        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        formatProvider = new JacksonFormatProvider("json", new JsonFactory());

        personClass = EcoreHelper.getEClass(testPackage, "Person");
        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        managerRef = (EReference) EcoreHelper.getFeature(personClass, "manager");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createPerson(String name) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttribute, name);
        return person;
    }

    private CodecResource createResource(ResourceSet rs, String uri) {
        CodecResource resource = new CodecResource(
                URI.createURI(uri),
                metadataService,
                ConfigurationResolver.defaults(),
                null, null, formatProvider);
        rs.getResources().add(resource);
        return resource;
    }

    private String serialize(CodecResource resource) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("writes the target resource URI for a reference into another resource")
    void writesResourceUriForCrossResourceReference() throws IOException {
        ResourceSet rs = new ResourceSetImpl();
        CodecResource source = createResource(rs, "test://source.json");
        CodecResource target = createResource(rs, "test://target.json");

        EObject john = createPerson("John");
        EObject boss = createPerson("Boss");

        source.getContents().add(john);
        target.getContents().add(boss);

        // Non-containment reference into the other resource.
        john.eSet(managerRef, boss);

        String json = serialize(source);

        assertTrue(json.contains("\"manager\""), "manager reference must be present: " + json);
        // The reference must carry the target resource URI, not just the bare fragment.
        assertTrue(json.contains("target.json"),
                "cross-resource reference must include the target resource URI, but was: " + json);
    }

    @Test
    @DisplayName("writes the proxy URI for an unresolved proxy reference")
    void writesProxyUriForUnresolvedProxy() throws IOException {
        ResourceSet rs = new ResourceSetImpl();
        CodecResource source = createResource(rs, "test://source.json");

        EObject john = createPerson("John");
        source.getContents().add(john);

        // An unresolved proxy target: eResource() == null, eProxyURI() set.
        EObject bossProxy = testPackage.getEFactoryInstance().create(personClass);
        URI proxyUri = URI.createURI("test://other.json#/id-42");
        ((InternalEObject) bossProxy).eSetProxyURI(proxyUri);

        john.eSet(managerRef, bossProxy);

        String json = serialize(source);

        assertTrue(json.contains("\"manager\""), "manager reference must be present: " + json);
        // The proxy URI (resource + id), deresolved against the source, must be
        // written as the reference value, not the type-URI fallback.
        assertTrue(json.contains("other.json#/id-42"),
                "unresolved proxy reference must preserve the proxy URI, but was: " + json);
    }
}
