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
package org.eclipse.fennec.codec.rest.jakartas.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * Root-type resolution for a Java entity type, as used when a request carries no explicit
 * {@code CODEC_ROOT_TYPE}.
 * <p>
 * The lookup goes through the metadata index, which keys classes by
 * {@code EClass.getInstanceClassName()}. It replaced the deprecated {@code EMFModelInfo}, whose
 * {@code Map<Class, EClassifier>} silently kept the last registration when several model
 * versions declared the same Java class. That coin flip is now an error, and these tests pin
 * the difference.
 * </p>
 */
@DisplayName("REST root-type resolution via the metadata index")
class RootTypeResolutionTest {

    /** Stands in for a generated model class; only its name reaches the index. */
    private static final class Person {
    }

    private MetadataWhiteboard metadataService;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServices.createWhiteboard();
    }

    /**
     * Builds a package with one EClass whose instance class name is the one the resolver looks
     * up — the same property {@code EMFModelInfo} indexed.
     */
    private static EPackage packageDeclaring(String nsURI, String instanceClassName) {
        EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
        ePackage.setName("model");
        ePackage.setNsPrefix("model");
        ePackage.setNsURI(nsURI);

        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName("Person");
        eClass.setInstanceClassName(instanceClassName);
        ePackage.getEClassifiers().add(eClass);

        return ePackage;
    }

    @Test
    @DisplayName("no registered model declares the type ⇒ empty, the option stays unset")
    void unknownTypeResolvesToNothing() {
        metadataService.registerPackage(packageDeclaring("http://test/other/1.0", "com.example.Other"));

        Optional<EClass> resolved = BaseJakartaCodecMessageBodyReaderWriter
                .resolveRootEClass(metadataService, Person.class);

        assertTrue(resolved.isEmpty(), "an unknown entity type must not produce a root type");
    }

    @Test
    @DisplayName("exactly one model version declares the type ⇒ that EClass")
    void singleMatchResolves() {
        EPackage ePackage = packageDeclaring("http://test/person/1.0", Person.class.getName());
        metadataService.registerPackage(ePackage);

        Optional<EClass> resolved = BaseJakartaCodecMessageBodyReaderWriter
                .resolveRootEClass(metadataService, Person.class);

        assertSame(ePackage.getEClassifier("Person"), resolved.orElseThrow(),
                "the EClass of the one registered version must be returned");
    }

    @Test
    @DisplayName("two model versions declare the type ⇒ ambiguity error, not a silent pick")
    void ambiguousMatchFails() {
        EPackage first = packageDeclaring("http://test/person/1.0", Person.class.getName());
        EPackage second = packageDeclaring("http://test/person/2.0", Person.class.getName());
        metadataService.registerPackage(first);
        metadataService.registerPackage(second);

        WebApplicationException thrown = assertThrows(WebApplicationException.class,
                () -> BaseJakartaCodecMessageBodyReaderWriter.resolveRootEClass(metadataService, Person.class));

        Response response = thrown.getResponse();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        String message = String.valueOf(response.getEntity());
        assertTrue(message.contains(Person.class.getName()), "the message must name the entity type: " + message);
        assertTrue(message.contains("http://test/person/1.0"), "the message must list both candidates: " + message);
        assertTrue(message.contains("http://test/person/2.0"), "the message must list both candidates: " + message);
        assertTrue(message.contains(CodecResource.CODEC_ROOT_TYPE),
                "the message must say how to disambiguate: " + message);
    }

    @Test
    @DisplayName("same type in one package registered twice ⇒ still one candidate")
    void repeatedRegistrationOfOneVersionStaysUnambiguous() {
        EPackage ePackage = packageDeclaring("http://test/person/1.0", Person.class.getName());
        metadataService.registerPackage(ePackage);
        metadataService.registerPackage(ePackage);

        Optional<EClass> resolved = BaseJakartaCodecMessageBodyReaderWriter
                .resolveRootEClass(metadataService, Person.class);

        assertSame(ePackage.getEClassifier("Person"), resolved.orElseThrow(),
                "re-registering the same model version must not look like two versions");
    }
}
