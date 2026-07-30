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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
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
	@DisplayName("a successful write detaches the resource-less object again")
	void successfulWriteDetachesResourceLessObject() throws IOException {
		EPackage ePackage = createPackage();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		handler.writeTo(ePackage, EPackage.class, EPackage.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), out);

		assertTrue(out.size() > 0, "something must have been serialized");
		assertNull(ePackage.eResource(), "live object must not have a resource after the write");
	}

	@Test
	@DisplayName("writeTo works for models generated with suppressed notifications (issue #94)")
	void writeToSupportsSuppressedNotificationModels() throws IOException {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("suppressed");
		ePackage.setNsPrefix("sup");
		ePackage.setNsURI("http://example.org/suppressed");
		EClass nodeClass = EcoreFactory.eINSTANCE.createEClass();
		nodeClass.setName("Node");
		EReference refs = EcoreFactory.eINSTANCE.createEReference();
		refs.setName("refs");
		refs.setEType(EcorePackage.Literals.ECLASS);
		refs.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
		nodeClass.getEStructuralFeatures().add(refs);
		ePackage.getEClassifiers().add(nodeClass);
		ePackage.setEFactoryInstance(new EFactoryImpl() {
			@Override
			protected EObject basicCreate(EClass eClass) {
				return new SuppressedNotificationObject(eClass);
			}
		});

		EObject node = ePackage.getEFactoryInstance().create(nodeClass);
		@SuppressWarnings("unchecked")
		List<EObject> refValues = (List<EObject>) node.eGet(refs);
		refValues.add(EcorePackage.Literals.ECLASS);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		handler.writeTo(node, EObject.class, EObject.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), out);

		assertTrue(out.size() > 0, "something must have been serialized");
		assertNull(node.eResource(), "live object must not have a resource after the write");
		assertTrue(resourceSet.getResources().isEmpty(),
				"no temporary resource may be left behind in the per-request ResourceSet");
	}

	/**
	 * Mimics the code EMF generates when notifications are suppressed: many-features
	 * are backed by {@link BasicInternalEList}, which implements {@link InternalEList}
	 * but not {@code EStructuralFeature.Setting} — the pattern that made
	 * {@code EcoreUtil.copy} fail (issue #94).
	 */
	static class SuppressedNotificationObject extends EObjectImpl {

		private final EClass staticClass;
		private final EList<EObject> refs = new BasicInternalEList<>(EObject.class);

		SuppressedNotificationObject(EClass staticClass) {
			this.staticClass = staticClass;
		}

		@Override
		protected EClass eStaticClass() {
			return staticClass;
		}

		@Override
		public Object eGet(int featureID, boolean resolve, boolean coreType) {
			if (featureID == 0) {
				return refs;
			}
			return super.eGet(featureID, resolve, coreType);
		}

		@Override
		public boolean eIsSet(int featureID) {
			if (featureID == 0) {
				return !refs.isEmpty();
			}
			return super.eIsSet(featureID);
		}
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
