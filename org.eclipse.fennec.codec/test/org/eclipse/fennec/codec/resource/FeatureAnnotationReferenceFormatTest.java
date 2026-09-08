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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A feature-level reference annotation has to reach the wire (issue #175).
 * <p>
 * The two values below are the ones that used to be lost, and they are lost for a reason that
 * has nothing to do with the codec: they equal the <i>model's</i> default. Asking for the
 * model's own documented behaviour was exactly the request that got dropped - and what the
 * wire then carried contradicted the {@code .ecore} that a consumer reads.
 * </p>
 * <p>
 * Since issue #211 the two layers agree on {@code refKey}: both {@code BaseReferenceConfig}
 * and {@code ConfigProperty.REF_KEY} default to {@code _ref}. {@code refFormat} still
 * diverges - the model defaults to {@code PLAIN}, the codec to {@code STRUCTURED} - so that
 * case still shows a dropped annotation as a visibly different wire format.
 * </p>
 */
@DisplayName("Feature-level reference annotation on the wire")
class FeatureAnnotationReferenceFormatTest {

    private static final String NS_URI = "urn:codec:feature:refformat:test";
    private static final String CODEC_SOURCE = "http://eclipse.org/fennec/codec";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;
    private EClass companyClass;
    private EAttribute personName;
    private EReference employees;
    private EReference ceo;

    @BeforeEach
    void setUp() {
        EcoreFactory f = EcoreFactory.eINSTANCE;

        personClass = f.createEClass();
        personClass.setName("Person");
        personName = f.createEAttribute();
        personName.setName("name");
        personName.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(personName);

        companyClass = f.createEClass();
        companyClass.setName("Company");

        employees = f.createEReference();
        employees.setName("employees");
        employees.setEType(personClass);
        employees.setContainment(true);
        employees.setUpperBound(-1);
        companyClass.getEStructuralFeatures().add(employees);

        ceo = f.createEReference();
        ceo.setName("ceo");
        ceo.setEType(personClass);
        companyClass.getEStructuralFeatures().add(ceo);

        testPackage = f.createEPackage();
        testPackage.setName("refformat");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("refformat");
        testPackage.getEClassifiers().add(personClass);
        testPackage.getEClassifiers().add(companyClass);

        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(testPackage);
        EPackage.Registry.INSTANCE.put(NS_URI, testPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    @Test
    @DisplayName("refFormat=PLAIN on the reference writes a bare reference value")
    void plainRefFormatFromAnnotation() throws IOException {
        annotate(ceo, "refFormat", "PLAIN");

        String json = save();

        assertTrue(json.contains("\"ceo\":\"//@employees.0\""),
                "PLAIN writes the reference as a bare string, was: " + json);
        assertFalse(json.contains("\"_ref\""),
                "the object wrapper belongs to STRUCTURED, which is not what was asked for");
    }

    @Test
    @DisplayName("refKey=_ref on the reference writes _ref")
    void modelDefaultRefKeyFromAnnotation() throws IOException {
        annotate(ceo, "refKey", "_ref");

        String json = save();

        assertTrue(json.contains("\"_ref\":\"//@employees.0\""),
                "the annotation asked for _ref, was: " + json);
        assertFalse(json.contains("\"$ref\""),
                "nothing may write the pre-#211 default any more, was: " + json);
    }

    @Test
    @DisplayName("a PLAIN reference written from the annotation reads back")
    @SuppressWarnings("unchecked")
    void plainRefFormatRoundTrips() throws IOException {
        annotate(ceo, "refFormat", "PLAIN");

        String json = save();
        EObject loaded = load(json);

        List<EObject> loadedEmployees = (List<EObject>) loaded.eGet(employees);
        assertEquals(1, loadedEmployees.size());
        assertEquals(loadedEmployees.get(0), loaded.eGet(ceo),
                "the reference has to resolve back to the contained Person");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void annotate(EReference reference, String... keyValuePairs) {
        EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
        annotation.setSource(CODEC_SOURCE);
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            annotation.getDetails().put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        reference.getEAnnotations().add(annotation);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @SuppressWarnings("unchecked")
    private String save() throws IOException {
        EObject alice = testPackage.getEFactoryInstance().create(personClass);
        alice.eSet(personName, "Alice");
        EObject acme = testPackage.getEFactoryInstance().create(companyClass);
        ((List<EObject>) acme.eGet(employees)).add(alice);
        acme.eSet(ceo, alice);

        CodecResource resource = newResource();
        resource.getContents().add(acme);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return out.toString(UTF_8);
    }

    private EObject load(String json) throws IOException {
        CodecResource resource = newResource();
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)),
                Map.of(CodecResource.CODEC_ROOT_TYPE, companyClass));
        return resource.getContents().get(0);
    }

    private CodecResource newResource() {
        return new CodecResource(URI.createURI("refformat.json"), metadataService,
                ConfigurationResolver.defaults(), null);
    }
}
