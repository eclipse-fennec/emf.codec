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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.resource.CodecResourceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;

/**
 * Issue #266: when the response is written with another resource class than the one
 * the object is held in, references within the document must stay relative to the
 * served document and must not point into the temporary write resource.
 */
@DisplayName("REST writer same-document references (issue #266)")
class SameDocumentReferenceTest {

	private static final MediaType XML = new MediaType("application", "xml");
	private static final MediaType JSON = new MediaType("application", "json");
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
	private static final URI STORAGE_URI = URI.createURI("file:/storage/mapping.xmi");

	private ResourceSet resourceSet;
	private CodecResourceFactory jsonFactory;
	private TestHandler handler;
	private EPackage ePackage;
	private EClass nodeClass;
	private EAttribute nameAttribute;
	private EReference childrenReference;
	private EReference refReference;

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
		createModel();
		MetadataWhiteboard metadataService = MetadataServices.createWhiteboard();
		metadataService.registerPackage(ePackage);

		resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		handler = new TestHandler(resourceSet);
		handler.metadataService = metadataService;
		jsonFactory = new CodecResourceFactory(metadataService);
		Map<String, Object> factories = resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap();
		factories.put("application/xml", new XMLResourceFactoryImpl());
		factories.put("application/json", jsonFactory);
	}

	/**
	 * {@code Node} with a name, contained {@code children} and a non-containment {@code ref}.
	 */
	private void createModel() {
		ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("issue266");
		ePackage.setNsPrefix("i266");
		ePackage.setNsURI("http://example.org/issue266");
		nodeClass = EcoreFactory.eINSTANCE.createEClass();
		nodeClass.setName("Node");
		nameAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		nameAttribute.setName("name");
		nameAttribute.setEType(EcorePackage.Literals.ESTRING);
		childrenReference = EcoreFactory.eINSTANCE.createEReference();
		childrenReference.setName("children");
		childrenReference.setEType(nodeClass);
		childrenReference.setContainment(true);
		childrenReference.setUpperBound(-1);
		refReference = EcoreFactory.eINSTANCE.createEReference();
		refReference.setName("ref");
		refReference.setEType(nodeClass);
		nodeClass.getEStructuralFeatures().addAll(List.of(nameAttribute, childrenReference, refReference));
		ePackage.getEClassifiers().add(nodeClass);
	}

	private EObject node(String name) {
		EObject node = EcoreUtil.create(nodeClass);
		node.eSet(nameAttribute, name);
		return node;
	}

	@SuppressWarnings("unchecked")
	private List<EObject> childrenOf(EObject node) {
		return (List<EObject>) node.eGet(childrenReference);
	}

	/**
	 * A root held in an XMI resource; its first child references the second child
	 * within the same document.
	 */
	private EObject storedRoot() {
		EObject root = node("root");
		EObject first = node("first");
		EObject second = node("second");
		childrenOf(root).addAll(List.of(first, second));
		first.eSet(refReference, second);
		new XMIResourceImpl(STORAGE_URI).getContents().add(root);
		return root;
	}

	private String write(EObject eObject, MediaType mediaType) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		handler.writeTo(eObject, EObject.class, EObject.class, NO_ANNOTATIONS, mediaType,
				new MultivaluedHashMap<>(), out);
		return out.toString(StandardCharsets.UTF_8);
	}

	private void assertRefResolvesWithinDocument(Resource reloaded) {
		assertTrue(reloaded.getErrors().isEmpty(), "reload errors: " + reloaded.getErrors());
		EObject root = reloaded.getContents().get(0);
		List<EObject> children = childrenOf(root);
		assertEquals(2, children.size(), "both children must be reloaded");
		EObject target = (EObject) children.get(0).eGet(refReference);
		assertNotNull(target, "the reference target must be present");
		assertFalse(target.eIsProxy(), "the reference must resolve: " + target);
		assertSame(children.get(1), target, "the reference must resolve within the served document");
	}

	@Test
	@DisplayName("XML, encoded attribute style: a same-document reference is written relative to the document")
	void xmlEncodedAttributeStyleWritesRelativeReference() throws IOException {
		handler.clientOptions = Map.of(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);

		String xml = write(storedRoot(), XML);

		assertFalse(xml.contains("codec.temp"), "no reference may point into the temp resource: " + xml);
		assertTrue(xml.contains("ref=\"#//@children.1\""), "the reference must be relative to the document: " + xml);

		Resource reloaded = new XMLResourceFactoryImpl().createResource(URI.createURI("http://client/response.xml"));
		resourceSet.getResources().add(reloaded);
		reloaded.load(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), Map.of());
		assertRefResolvesWithinDocument(reloaded);
	}

	@Test
	@DisplayName("XML, default style: a same-document reference is written relative to the document")
	void xmlDefaultStyleWritesRelativeReference() throws IOException {
		String xml = write(storedRoot(), XML);

		assertFalse(xml.contains("codec.temp"), "no reference may point into the temp resource: " + xml);

		Resource reloaded = new XMLResourceFactoryImpl().createResource(URI.createURI("http://client/response.xml"));
		resourceSet.getResources().add(reloaded);
		reloaded.load(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), Map.of());
		assertRefResolvesWithinDocument(reloaded);
	}

	@Test
	@DisplayName("JSON: a same-document reference is written relative to the document")
	void jsonWritesRelativeReference() throws IOException {
		String json = write(storedRoot(), JSON);

		assertFalse(json.contains("codec.temp"), "no reference may point into the temp resource: " + json);
		assertTrue(json.contains("\"ref\""), "the reference must be written: " + json);

		Resource reloaded = jsonFactory.createResource(URI.createURI("http://client/response.json"));
		resourceSet.getResources().add(reloaded);
		reloaded.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
				Map.of(CodecResource.CODEC_ROOT_TYPE, nodeClass));
		assertRefResolvesWithinDocument(reloaded);
	}
}
