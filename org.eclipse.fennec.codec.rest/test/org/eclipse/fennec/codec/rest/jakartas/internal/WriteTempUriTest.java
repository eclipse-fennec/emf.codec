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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;

/**
 * Issue #166: every write uses a unique URI for its temporary resource, so it
 * can never collide with another temporary resource in the same shared request
 * {@link ResourceSet} (e.g. from the reader side of the same request).
 */
@DisplayName("REST writer temp resource URIs (issue #166)")
class WriteTempUriTest {

	private static final String CONTENT_TYPE = "application/xml";
	private static final MediaType MEDIA_TYPE = new MediaType("application", "xml");
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
	private static final String LEGACY_FIXED_URI = "http://test.test";

	private ResourceSet resourceSet;
	private TestHandler handler;

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

	@BeforeEach
	void setUp() {
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap()
				.put(CONTENT_TYPE, new XMIResourceFactoryImpl());
		handler = new TestHandler(resourceSet);
	}

	private EPackage createPackage() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("issue166");
		ePackage.setNsPrefix("i166");
		ePackage.setNsURI("http://example.org/issue166");
		return ePackage;
	}

	/**
	 * Runs one write of a resource-less object and returns the URIs of all
	 * resources observed in the shared set while the entity stream was written.
	 */
	private Set<String> urisObservedDuringWrite() throws IOException {
		Set<String> observed = new LinkedHashSet<>();
		OutputStream capturing = new OutputStream() {
			@Override
			public void write(int b) {
				resourceSet.getResources().forEach(r -> observed.add(String.valueOf(r.getURI())));
			}
		};
		handler.writeTo(createPackage(), EPackage.class, EPackage.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), capturing);
		return observed;
	}

	@Test
	@DisplayName("two writes use two different temp URIs, neither the old fixed one")
	void tempUriIsUniquePerWrite() throws IOException {
		Set<String> firstWrite = urisObservedDuringWrite();
		Set<String> secondWrite = urisObservedDuringWrite();

		assertEquals(1, firstWrite.size(), "exactly the temp resource must be in the set during the write");
		assertEquals(1, secondWrite.size(), "exactly the temp resource must be in the set during the write");

		String firstUri = firstWrite.iterator().next();
		String secondUri = secondWrite.iterator().next();
		assertNotEquals(firstUri, secondUri, "every write must use its own temp URI");
		assertNotEquals(LEGACY_FIXED_URI, firstUri, "the fixed legacy URI must not be used any more");
		assertNotEquals(LEGACY_FIXED_URI, secondUri, "the fixed legacy URI must not be used any more");
	}

	@Test
	@DisplayName("a pre-existing resource with the old fixed URI does not clash with a write")
	void writeDoesNotClashWithResourceCarryingLegacyUri() throws IOException {
		Resource preExisting = new XMIResourceFactoryImpl().createResource(URI.createURI(LEGACY_FIXED_URI));
		preExisting.getContents().add(createPackage());
		resourceSet.getResources().add(preExisting);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		handler.writeTo(createPackage(), EPackage.class, EPackage.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), out);

		assertTrue(out.size() > 0, "something must have been serialized");
		assertEquals(1, resourceSet.getResources().size(),
				"only the pre-existing resource may remain in the set, but found: " + resourceSet.getResources());
		assertSame(preExisting, resourceSet.getResources().get(0),
				"the pre-existing resource must be untouched by the write");
		assertEquals(1, preExisting.getContents().size(),
				"the pre-existing resource's contents must be untouched by the write");
	}

	@Test
	@DisplayName("same-class path: the caller's resource stays in its set after a successful write")
	void sameClassPathKeepsResourceInSetAfterSuccess() throws IOException {
		Resource sameClass = new XMIResourceFactoryImpl().createResource(URI.createURI("issue166.xmi"));
		sameClass.getContents().add(createPackage());
		resourceSet.getResources().add(sameClass);

		handler.writeResourceTo(sameClass, Resource.class, Resource.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), new ByteArrayOutputStream());

		assertSame(resourceSet, sameClass.getResourceSet(),
				"the caller's resource must still be in the shared ResourceSet after the write");
	}

	@Test
	@DisplayName("same-class path: the caller's resource stays in its set after a failed write")
	void sameClassPathKeepsResourceInSetAfterFailure() {
		Resource sameClass = new XMIResourceFactoryImpl().createResource(URI.createURI("issue166.xmi"));
		sameClass.getContents().add(createPackage());
		resourceSet.getResources().add(sameClass);

		OutputStream failing = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				throw new IOException("simulated broken response stream");
			}
		};

		assertThrows(WebApplicationException.class, () -> handler.writeResourceTo(sameClass, Resource.class,
				Resource.class, NO_ANNOTATIONS, MEDIA_TYPE, new MultivaluedHashMap<>(), failing));

		assertSame(resourceSet, sameClass.getResourceSet(),
				"the caller's resource must still be in the shared ResourceSet after a failed write");
	}
}
