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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.resource.CodecResourceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;

/**
 * A collection of EObjects as entity (issue #334).
 */
@DisplayName("REST reader/writer for collections of EObjects (issue #334)")
class EObjectCollectionMessageBodyHandlerTest {

	private static final MediaType JSON = new MediaType("application", "json");
	private static final MediaType XMI = new MediaType("application", "xmi");
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

	private static final Type LIST_OF_EOBJECT = new GenericType<List<EObject>>() {}.getType();

	private ResourceSet resourceSet;
	private CodecResourceFactory jsonFactory;
	private TestHandler handler;
	private EClass nodeClass;
	private EAttribute nameAttribute;
	private EReference refReference;

	static class TestHandler extends EObjectCollectionMessageBodyHandler {

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

	/** A collection type that fixes its element type in a supertype. */
	@SuppressWarnings("serial")
	static class Nodes extends ArrayList<EObject> {
	}

	@BeforeEach
	void setUp() {
		EPackage ePackage = createModel();
		MetadataWhiteboard metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(ePackage);

		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		handler = new TestHandler(resourceSet);
		handler.metadataService = metadataService;
		jsonFactory = new CodecResourceFactory(metadataService);
		Map<String, Object> factories = resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap();
		factories.put("application/json", jsonFactory);
		factories.put("application/xmi", new XMIResourceFactoryImpl());
	}

	private EPackage createModel() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("issue334");
		ePackage.setNsPrefix("i334");
		ePackage.setNsURI("http://example.org/issue334");
		nodeClass = EcoreFactory.eINSTANCE.createEClass();
		nodeClass.setName("Node");
		nameAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		nameAttribute.setName("name");
		nameAttribute.setEType(EcorePackage.Literals.ESTRING);
		refReference = EcoreFactory.eINSTANCE.createEReference();
		refReference.setName("ref");
		refReference.setEType(nodeClass);
		nodeClass.getEStructuralFeatures().addAll(List.of(nameAttribute, refReference));
		ePackage.getEClassifiers().add(nodeClass);
		return ePackage;
	}

	private EObject node(String name) {
		EObject node = EcoreUtil.create(nodeClass);
		node.eSet(nameAttribute, name);
		return node;
	}

	private String write(Collection<? extends EObject> collection, MediaType mediaType) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		handler.writeTo(collection, ArrayList.class, LIST_OF_EOBJECT, NO_ANNOTATIONS, mediaType,
				new MultivaluedHashMap<>(), out);
		return out.toString(StandardCharsets.UTF_8).trim();
	}

	private Resource reloadJson(String json) throws IOException {
		Resource reloaded = jsonFactory.createResource(URI.createURI("http://client/response.json"));
		reloaded.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
				Map.of(CodecResource.CODEC_ROOT_TYPE, nodeClass));
		return reloaded;
	}

	@Nested
	@DisplayName("Selection")
	class Selection {

		@Test
		@DisplayName("a collection declared with an EObject element type is writeable")
		void declaredElementType() {
			assertTrue(handler.isWriteable(ArrayList.class, LIST_OF_EOBJECT, NO_ANNOTATIONS, JSON));
			assertTrue(handler.isWriteable(ArrayList.class,
					new GenericType<Collection<? extends EObject>>() {}.getType(), NO_ANNOTATIONS, JSON));
			assertTrue(handler.isWriteable(Nodes.class, Nodes.class, NO_ANNOTATIONS, JSON));
		}

		@Test
		@DisplayName("a raw collection or one of other elements is left to other providers")
		void notClaimed() {
			assertFalse(handler.isWriteable(ArrayList.class, ArrayList.class, NO_ANNOTATIONS, JSON));
			assertFalse(handler.isWriteable(ArrayList.class, new GenericType<List<String>>() {}.getType(),
					NO_ANNOTATIONS, JSON));
			assertFalse(handler.isWriteable(EObject.class, EObject.class, NO_ANNOTATIONS, JSON));
		}

		@Test
		@DisplayName("a media type without a resource factory is not writeable")
		void unknownMediaType() {
			assertFalse(handler.isWriteable(ArrayList.class, LIST_OF_EOBJECT, NO_ANNOTATIONS,
					MediaType.TEXT_PLAIN_TYPE));
		}

		@Test
		@DisplayName("readable as List, Collection and Set, not as a concrete type it cannot create")
		void readable() {
			assertTrue(handler.isReadable(List.class, LIST_OF_EOBJECT, NO_ANNOTATIONS, JSON));
			assertTrue(handler.isReadable(Collection.class,
					new GenericType<Collection<EObject>>() {}.getType(), NO_ANNOTATIONS, JSON));
			assertTrue(handler.isReadable(Set.class, new GenericType<Set<EObject>>() {}.getType(),
					NO_ANNOTATIONS, JSON));
			assertFalse(handler.isReadable(Nodes.class, Nodes.class, NO_ANNOTATIONS, JSON));
		}
	}

	@Nested
	@DisplayName("Writing")
	class Writing {

		@Test
		@DisplayName("elements of a resource are written as an array and stay in their resource")
		void attachedElements() throws IOException {
			Resource storage = new XMIResourceImpl(URI.createURI("file:/storage/nodes.xmi"));
			EObject first = node("first");
			EObject second = node("second");
			storage.getContents().addAll(List.of(first, second));

			String json = write(List.of(first, second), JSON);

			assertTrue(json.startsWith("["), json);
			Resource reloaded = reloadJson(json);
			assertEquals(2, reloaded.getContents().size(), json);
			assertEquals("first", reloaded.getContents().get(0).eGet(nameAttribute));
			assertEquals("second", reloaded.getContents().get(1).eGet(nameAttribute));
			assertSame(storage, first.eResource(), "an element must not be re-parented");
			assertEquals(2, storage.getContents().size());
			assertTrue(resourceSet.getResources().isEmpty(), "the temporary resource must be removed");
		}

		@Test
		@DisplayName("a reference between two elements stays inside the document")
		void referenceBetweenElements() throws IOException {
			Resource storage = new XMIResourceImpl(URI.createURI("file:/storage/nodes.xmi"));
			EObject first = node("first");
			EObject second = node("second");
			storage.getContents().addAll(List.of(first, second));
			first.eSet(refReference, second);

			Resource reloaded = reloadJson(write(List.of(first, second), JSON));

			EObject target = (EObject) reloaded.getContents().get(0).eGet(refReference);
			assertSame(reloaded.getContents().get(1), target, "ref must resolve to the second element");
		}

		@Test
		@DisplayName("one element is a one-element array, none an empty array")
		void oneAndNone() throws IOException {
			String one = write(List.of(node("only")), JSON);
			assertTrue(one.startsWith("[") && one.endsWith("]"), one);
			assertEquals(1, reloadJson(one).getContents().size());

			assertEquals("[]", write(List.of(), JSON));
		}

		@Test
		@DisplayName("detached elements are written and detached again")
		void detachedElements() throws IOException {
			EObject first = node("first");
			EObject parentless = node("second");

			write(List.of(first, parentless), JSON);

			assertNull(first.eResource(), "a detached element must be detached again");
			assertNull(parentless.eResource());
			assertTrue(resourceSet.getResources().isEmpty());
		}

		@Test
		@DisplayName("xmi:ids of the elements are kept")
		void idsKept() throws IOException {
			XMIResourceImpl storage = new XMIResourceImpl(URI.createURI("file:/storage/nodes.xmi"));
			EObject first = node("first");
			storage.getContents().add(first);
			storage.setID(first, "id-first");

			String xmi = write(List.of(first, node("second")), XMI);

			assertTrue(xmi.contains("id-first"), xmi);
			assertEquals("id-first", storage.getID(first), "the original keeps its id");
		}
	}

	@Nested
	@DisplayName("Reading")
	class Reading {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Collection<EObject> read(Class type, Type genericType, String json) throws IOException {
			return handler.readFrom(type, genericType, NO_ANNOTATIONS, JSON, new MultivaluedHashMap<>(),
					new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
		}

		@Test
		@DisplayName("a JSON array is read as a list, typed by the element type")
		void array() throws IOException {
			Collection<EObject> nodes = read(List.class, LIST_OF_EOBJECT,
					"[{\"_type\":\"http://example.org/issue334#//Node\",\"name\":\"a\"},"
							+ "{\"_type\":\"http://example.org/issue334#//Node\",\"name\":\"b\"}]");

			assertInstanceOf(List.class, nodes);
			assertEquals(List.of("a", "b"), nodes.stream().map(n -> n.eGet(nameAttribute)).toList());
			assertTrue(resourceSet.getResources().isEmpty(), "the loading resource must be unhooked");
			assertTrue(nodes.stream().allMatch(n -> n.eResource() != null),
					"the elements stay in the resource they were loaded into (issue #246)");
		}

		@Test
		@DisplayName("an empty array is an empty set")
		void emptySet() throws IOException {
			Collection<EObject> nodes = read(Set.class, new GenericType<Set<EObject>>() {}.getType(), "[]");

			assertInstanceOf(Set.class, nodes);
			assertTrue(nodes.isEmpty());
		}
	}
}
