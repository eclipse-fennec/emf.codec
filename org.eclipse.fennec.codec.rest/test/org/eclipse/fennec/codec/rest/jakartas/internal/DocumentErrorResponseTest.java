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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResourceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;

/**
 * A document that cannot be read because of its own content is answered with {@code 400} and a
 * description of the problem; a server fault keeps the generic {@code 500} (issue #265).
 */
@DisplayName("REST reader: document errors are 400 (issue #265)")
class DocumentErrorResponseTest {

	private static final MediaType XMI = new MediaType("application", "xmi");
	private static final MediaType JSON = new MediaType("application", "json");
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
	private static final String NS_URI = "http://example.org/issue265";
	private static final String UNKNOWN_NS_URI = "https://fennec.eclipse.org/unknown/1.0";

	private TestHandler handler;

	static class TestHandler extends EObjectMessageBodyHandler<EObject, EObject> {

		private final ResourceSet resourceSet;
		private Map<String, Object> clientOptions = Map.of();

		TestHandler(ResourceSet resourceSet) {
			this.resourceSet = resourceSet;
		}

		@Override
		protected ResourceSet getResourceSet() {
			return resourceSet;
		}

		@Override
		protected Map<String, Object> getClientCodecOptions() {
			return clientOptions;
		}
	}

	@BeforeEach
	void setUp() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("issue265");
		ePackage.setNsPrefix("i265");
		ePackage.setNsURI(NS_URI);
		EClass person = EcoreFactory.eINSTANCE.createEClass();
		person.setName("Person");
		EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		person.getEStructuralFeatures().add(name);
		ePackage.getEClassifiers().add(person);

		MetadataWhiteboard metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(ePackage);
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(NS_URI, ePackage);
		Map<String, Object> factories = resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap();
		factories.put("application/xmi", new XMIResourceFactoryImpl());
		factories.put("application/json", new CodecResourceFactory(metadataService));
		handler = new TestHandler(resourceSet);
		handler.metadataService = metadataService;
	}

	@Test
	@DisplayName("XMI with a namespace the server has no package for names the namespace, line and column")
	void unknownPackage() {
		String body = badRequest(XMI, """
				<?xml version="1.0" encoding="UTF-8"?>
				<u:Thing xmlns:xmi="http://www.omg.org/XMI" xmi:version="2.0"
				    xmlns:u="%s" name="x"/>
				""".formatted(UNKNOWN_NS_URI));

		assertTrue(body.contains(UNKNOWN_NS_URI), body);
		assertTrue(body.contains("line"), body);
		assertServerInternalsHidden(body);
	}

	@Test
	@DisplayName("malformed XML is a bad request")
	void malformedXml() {
		assertServerInternalsHidden(badRequest(XMI, "<i265:Person xmlns:i265=\"" + NS_URI + "\" name=\"x\">"));
	}

	@Test
	@DisplayName("malformed JSON is a bad request with the position")
	void malformedJson() {
		String body = badRequest(JSON, "{ \"_type\": \"" + NS_URI + "#//Person\", \"name\": ");

		assertTrue(body.contains("line"), body);
		assertServerInternalsHidden(body);
	}

	@Test
	@DisplayName("an unknown field under strictOnUnknown is a bad request naming the field")
	void strictOnUnknown() {
		handler.clientOptions = Map.of(CodecOptions.CODEC_STRICT_ON_UNKNOWN, Boolean.TRUE);

		String body = badRequest(JSON, "{ \"_type\": \"" + NS_URI + "#//Person\", \"nickname\": \"x\" }");

		assertTrue(body.contains("nickname"), body);
		assertServerInternalsHidden(body);
	}

	@Test
	@DisplayName("a document beyond a read limit is a bad request")
	void readLimit() {
		handler.clientOptions = Map.of(CodecOptions.CODEC_MAX_NESTING_DEPTH, 2);

		assertServerInternalsHidden(badRequest(JSON,
				"{ \"_type\": \"" + NS_URI + "#//Person\", \"extra\": { \"a\": { \"b\": { } } } }"));
	}

	@Test
	@DisplayName("a type the server does not know is a bad request, also when read leniently")
	void unresolvableType() {
		String body = badRequest(JSON, "{ \"_type\": \"" + UNKNOWN_NS_URI + "#//Thing\", \"name\": \"x\" }");

		assertTrue(body.contains(UNKNOWN_NS_URI), body);
		assertServerInternalsHidden(body);
	}

	@Test
	@DisplayName("a failing request stream is not the document's fault and keeps the generic 500")
	void serverFaultStaysGeneric() {
		InputStream failing = new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("connection reset - internal detail");
			}
		};

		WebApplicationException thrown = assertThrows(WebApplicationException.class, () -> handler.readFrom(
				EObject.class, EObject.class, NO_ANNOTATIONS, JSON, new MultivaluedHashMap<>(), failing));

		assertEquals(500, thrown.getResponse().getStatus());
		String body = String.valueOf(thrown.getResponse().getEntity());
		assertTrue(body.contains("Error de-serializing incoming data"), body);
		assertFalse(body.contains("internal detail"), body);
	}

	private String badRequest(MediaType mediaType, String document) {
		WebApplicationException thrown = assertThrows(WebApplicationException.class, () -> handler.readFrom(
				EObject.class, EObject.class, NO_ANNOTATIONS, mediaType, new MultivaluedHashMap<>(),
				new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8))));
		assertEquals(400, thrown.getResponse().getStatus(), String.valueOf(thrown.getResponse().getEntity()));
		String body = String.valueOf(thrown.getResponse().getEntity());
		assertTrue(body.contains("Invalid document"), body);
		return body;
	}

	private static void assertServerInternalsHidden(String body) {
		assertFalse(body.contains("temp/id"), "the server-side resource URI must not leak: " + body);
		assertFalse(body.contains("Exception"), "no exception class names: " + body);
		assertFalse(body.contains("\tat "), "no stack trace: " + body);
	}
}
