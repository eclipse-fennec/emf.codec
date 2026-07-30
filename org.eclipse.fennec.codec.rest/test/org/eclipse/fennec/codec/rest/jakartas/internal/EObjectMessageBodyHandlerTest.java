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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;

/**
 * Tests for {@link EObjectMessageBodyHandler#writeTo(EObject, Class, java.lang.reflect.Type, Annotation[], MediaType, jakarta.ws.rs.core.MultivaluedMap, OutputStream)}
 * covering issue #93: the writer must never (permanently or transiently)
 * re-parent a resource-less live object into the temporary response resource,
 * and must not leak temporary resources into the per-request {@link ResourceSet}.
 */
@DisplayName("EObjectMessageBodyHandler — writeTo (issue #93)")
class EObjectMessageBodyHandlerTest {

	private static final String CONTENT_TYPE = "application/xml";
	private static final MediaType MEDIA_TYPE = new MediaType("application", "xml");
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

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
		ePackage.setName("issue93");
		ePackage.setNsPrefix("i93");
		ePackage.setNsURI("http://example.org/issue93");
		return ePackage;
	}

	@Test
	@DisplayName("a failed serialization must not leave the resource-less object in the temp resource")
	void failedWriteRestoresResourceLessObject() {
		EPackage ePackage = createPackage();
		assertNull(ePackage.eResource(), "precondition: object has no resource");

		OutputStream failing = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				throw new IOException("simulated broken response stream");
			}
		};

		assertThrows(WebApplicationException.class, () -> handler.writeTo(ePackage, EPackage.class,
				EPackage.class, NO_ANNOTATIONS, MEDIA_TYPE, new MultivaluedHashMap<>(), failing));

		assertNull(ePackage.eResource(),
				"live object must not remain captured in the temporary response resource after a failed write");
	}

	@Test
	@DisplayName("the live object is never re-parented, not even while the response is being written")
	void liveObjectNeverReparentedDuringWrite() throws IOException {
		EPackage ePackage = createPackage();

		List<Resource> observedDuringWrite = new ArrayList<>();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStream observing = new OutputStream() {
			private boolean recorded = false;

			@Override
			public void write(int b) {
				if (!recorded) {
					recorded = true;
					observedDuringWrite.add(ePackage.eResource());
				}
				out.write(b);
			}
		};

		handler.writeTo(ePackage, EPackage.class, EPackage.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), observing);

		assertTrue(out.size() > 0, "something must have been serialized");
		assertNull(ePackage.eResource(), "live object must not have a resource after the write");
		assertNull(observedDuringWrite.get(0),
				"live object must not be re-parented into the response resource while writing");
	}

	@Test
	@DisplayName("a successful write of a resource-less object leaves the ResourceSet empty")
	void successfulWriteLeavesResourceSetClean() throws IOException {
		EPackage ePackage = createPackage();

		handler.writeTo(ePackage, EPackage.class, EPackage.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), new ByteArrayOutputStream());

		assertTrue(resourceSet.getResources().isEmpty(),
				"no temporary resource may be left behind in the per-request ResourceSet, but found: "
						+ resourceSet.getResources());
	}

	@Test
	@DisplayName("writeResourceTo of a foreign resource type does not leak the reference resource")
	void writeResourceOfForeignTypeLeavesResourceSetClean() throws IOException {
		EPackage ePackage = createPackage();
		Resource resource = new ResourceImpl(URI.createURI("issue93.xmi"));
		resource.getContents().add(EcoreUtil.copy(ePackage));
		resourceSet.getResources().add(resource);

		handler.writeResourceTo(resource, Resource.class, Resource.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), new ByteArrayOutputStream());

		assertTrue(resourceSet.getResources().size() == 1 && resourceSet.getResources().contains(resource),
				"only the original resource may remain in the ResourceSet, but found: " + resourceSet.getResources());
	}
}
