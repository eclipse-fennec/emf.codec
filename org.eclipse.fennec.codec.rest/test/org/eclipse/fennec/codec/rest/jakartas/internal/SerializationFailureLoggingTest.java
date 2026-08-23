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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;

/**
 * Issue #165: a failed (de-)serialization must be fully logged on the server
 * side, while the response body stays a generic message that leaks no
 * exception details to the client.
 */
@DisplayName("REST reader/writer failure logging (issue #165)")
class SerializationFailureLoggingTest {

	private static final String CONTENT_TYPE = "application/xml";
	private static final MediaType MEDIA_TYPE = new MediaType("application", "xml");
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
	private static final String SECRET = "simulated broken response stream - internal detail";

	private ResourceSet resourceSet;
	private TestHandler handler;
	private Logger logger;
	private CapturingHandler capture;

	static class TestHandler extends EObjectMessageBodyHandler<EObject, EObject> {

		private final ResourceSet resourceSet;

		TestHandler(ResourceSet resourceSet) {
			this.resourceSet = resourceSet;
		}

		@Override
		protected ResourceSet getResourceSet() {
			return resourceSet;
		}

		@Override
		protected Map<String, Object> getClientCodecOptions() {
			return Map.of();
		}
	}

	static class CapturingHandler extends Handler {

		final List<LogRecord> records = new CopyOnWriteArrayList<>();

		@Override
		public void publish(LogRecord record) {
			records.add(record);
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() {
		}
	}

	@BeforeEach
	void setUp() {
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap()
				.put(CONTENT_TYPE, new XMIResourceFactoryImpl());
		handler = new TestHandler(resourceSet);
		handler.metadataService = MetadataServices.createWhiteboard();

		logger = Logger.getLogger(BaseJakartaCodecMessageBodyReaderWriter.class.getName());
		capture = new CapturingHandler();
		logger.addHandler(capture);
		logger.setLevel(Level.ALL);
	}

	@AfterEach
	void tearDown() {
		logger.removeHandler(capture);
	}

	private EPackage createPackage() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("issue165");
		ePackage.setNsPrefix("i165");
		ePackage.setNsURI("http://example.org/issue165");
		return ePackage;
	}

	private static Throwable rootCause(Throwable t) {
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t;
	}

	@Test
	@DisplayName("a failed write logs the full cause with type and media type")
	void failedWriteIsLogged() {
		OutputStream failing = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				throw new IOException(SECRET);
			}
		};

		assertThrows(WebApplicationException.class, () -> handler.writeTo(createPackage(), EPackage.class,
				EPackage.class, NO_ANNOTATIONS, MEDIA_TYPE, new MultivaluedHashMap<>(), failing));

		assertEquals(1, capture.records.size(), "exactly one record must be logged");
		LogRecord record = capture.records.get(0);
		assertTrue(record.getLevel().intValue() >= Level.WARNING.intValue(),
				"the failure must be logged as WARNING or more severe, was: " + record.getLevel());
		assertNotNull(record.getThrown(), "the record must carry the exception for the stack trace");
		assertEquals(SECRET, rootCause(record.getThrown()).getMessage(),
				"the logged exception must lead to the original cause");
		assertTrue(record.getMessage().contains(EPackage.class.getName()),
				"the log message must name the entity type: " + record.getMessage());
		assertTrue(record.getMessage().contains(CONTENT_TYPE),
				"the log message must name the content type: " + record.getMessage());
	}

	@Test
	@DisplayName("a failed write responds with a generic body, no exception details")
	void failedWriteBodyStaysGeneric() {
		OutputStream failing = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				throw new IOException(SECRET);
			}
		};

		WebApplicationException thrown = assertThrows(WebApplicationException.class,
				() -> handler.writeTo(createPackage(), EPackage.class, EPackage.class, NO_ANNOTATIONS,
						MEDIA_TYPE, new MultivaluedHashMap<>(), failing));

		String body = String.valueOf(thrown.getResponse().getEntity());
		assertFalse(body.contains(SECRET), "the response body must not leak the exception message: " + body);
		assertFalse(body.contains(IOException.class.getName()),
				"the response body must not leak the exception class: " + body);
		assertTrue(body.contains("Error serializing outgoing object"),
				"the response body must keep the generic message: " + body);
	}

	@Test
	@DisplayName("a failed read logs the full cause and responds with a generic body")
	void failedReadIsLoggedAndBodyStaysGeneric() {
		ByteArrayInputStream broken = new ByteArrayInputStream(
				"this is not xml at all".getBytes(StandardCharsets.UTF_8));

		WebApplicationException thrown = assertThrows(WebApplicationException.class,
				() -> handler.readFrom(EObject.class, EObject.class, NO_ANNOTATIONS, MEDIA_TYPE,
						new MultivaluedHashMap<>(), broken));

		assertEquals(1, capture.records.size(), "exactly one record must be logged");
		LogRecord record = capture.records.get(0);
		assertTrue(record.getLevel().intValue() >= Level.WARNING.intValue(),
				"the failure must be logged as WARNING or more severe, was: " + record.getLevel());
		assertNotNull(record.getThrown(), "the record must carry the exception for the stack trace");
		assertTrue(record.getMessage().contains(CONTENT_TYPE),
				"the log message must name the content type: " + record.getMessage());

		String body = String.valueOf(thrown.getResponse().getEntity());
		assertTrue(body.contains("Error de-serializing incoming data"),
				"the response body must keep the generic message: " + body);
		assertFalse(body.toLowerCase().contains("exception"),
				"the response body must not leak exception details: " + body);
	}
}
