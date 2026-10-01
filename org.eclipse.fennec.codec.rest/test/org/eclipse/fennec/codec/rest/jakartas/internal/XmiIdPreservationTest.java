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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;

/**
 * Tests for issue #246: the reader must not drop the {@code xmi:id}s of an uploaded
 * document, and the writer must emit them again for objects that still carry a
 * resource. Documents without ids must behave exactly as before.
 */
@DisplayName("EObjectMessageBodyHandler — xmi:id preservation (issue #246)")
class XmiIdPreservationTest {

	private static final String CONTENT_TYPE = "application/xmi";
	private static final MediaType MEDIA_TYPE = new MediaType("application", "xmi");
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

	private static final String XMI_WITH_IDS = """
			<?xml version="1.0" encoding="UTF-8"?>
			<ecore:EPackage xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
			    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			    xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore"
			    xmi:id="_pkg" name="issue246" nsURI="http://example.org/issue246" nsPrefix="i246">
			  <eClassifiers xsi:type="ecore:EClass" xmi:id="_person" name="Person">
			    <eStructuralFeatures xsi:type="ecore:EReference" xmi:id="_friend" name="friend" eType="_person"/>
			  </eClassifiers>
			</ecore:EPackage>
			""";

	private static final String XMI_WITHOUT_IDS = """
			<?xml version="1.0" encoding="UTF-8"?>
			<ecore:EPackage xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
			    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			    xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore"
			    name="issue246" nsURI="http://example.org/issue246" nsPrefix="i246">
			  <eClassifiers xsi:type="ecore:EClass" name="Person">
			    <eStructuralFeatures xsi:type="ecore:EReference" name="friend" eType="#//Person"/>
			  </eClassifiers>
			</ecore:EPackage>
			""";

	private static final String XMI_WITH_CROSS_DOCUMENT_REFERENCE = """
			<?xml version="1.0" encoding="UTF-8"?>
			<ecore:EPackage xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
			    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			    xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore"
			    xmi:id="_pkg" name="issue246" nsURI="http://example.org/issue246" nsPrefix="i246">
			  <eClassifiers xsi:type="ecore:EClass" xmi:id="_person" name="Person">
			    <eStructuralFeatures xsi:type="ecore:EReference" xmi:id="_remoteRef" name="remote">
			      <eType xsi:type="ecore:EClass" href="other.ecore#_remote"/>
			    </eStructuralFeatures>
			  </eClassifiers>
			</ecore:EPackage>
			""";

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
		logger = Logger.getLogger(EObjectMessageBodyHandler.class.getName());
		capture = new CapturingHandler();
		logger.addHandler(capture);
		logger.setLevel(Level.ALL);
	}

	@AfterEach
	void tearDown() {
		logger.removeHandler(capture);
	}

	private EObject read(String xmi) throws IOException {
		return handler.readFrom(EObject.class, EObject.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8)));
	}

	private String write(EObject eObject) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		handler.writeTo(eObject, EObject.class, EObject.class, NO_ANNOTATIONS, MEDIA_TYPE,
				new MultivaluedHashMap<>(), out);
		return out.toString(StandardCharsets.UTF_8);
	}

	private static EClass personOf(EObject root) {
		return (EClass) ((EPackage) root).getEClassifiers().get(0);
	}

	private static EReference friendOf(EObject root) {
		return (EReference) personOf(root).getEStructuralFeatures().get(0);
	}

	// --- reading -----------------------------------------------------------

	@Test
	@DisplayName("readFrom keeps the uploaded xmi:ids reachable through the object's resource")
	void readKeepsIds() throws IOException {
		EObject root = read(XMI_WITH_IDS);

		XMLResource resource = assertInstanceOf(XMLResource.class, root.eResource(),
				"the object must stay attached to the XML resource that holds its ids");
		assertEquals("_pkg", resource.getID(root));
		assertEquals("_person", resource.getID(personOf(root)));
		assertEquals("_friend", resource.getID(friendOf(root)));
		assertSame(personOf(root), friendOf(root).getEType(), "the intra-document id reference must be resolved");
	}

	@Test
	@DisplayName("readFrom detaches the loading resource from the per-request ResourceSet")
	void readDetachesResourceFromResourceSet() throws IOException {
		EObject root = read(XMI_WITH_IDS);

		assertNotNull(root.eResource());
		assertNull(root.eResource().getResourceSet(), "the loading resource must not stay in the ResourceSet");
		assertTrue(resourceSet.getResources().isEmpty(),
				"no resource may be left behind in the per-request ResourceSet, but found: " + resourceSet.getResources());
	}

	@Test
	@DisplayName("readFrom of a document without xmi:ids invents none")
	void readWithoutIdsInventsNone() throws IOException {
		EObject root = read(XMI_WITHOUT_IDS);

		XMLResource resource = assertInstanceOf(XMLResource.class, root.eResource());
		assertNull(resource.getID(root));
		assertNull(resource.getID(personOf(root)));
		assertNull(resource.getID(friendOf(root)));
		assertSame(personOf(root), friendOf(root).getEType(), "the path reference must be resolved");
	}

	@Test
	@DisplayName("readFrom leaves cross-document references as proxies")
	void readLeavesCrossDocumentReferencesUnresolved() throws IOException {
		EObject root = read(XMI_WITH_CROSS_DOCUMENT_REFERENCE);

		EReference remote = (EReference) personOf(root).getEStructuralFeatures().get(0);
		EObject eType = (EObject) remote.eGet(remote.eClass().getEStructuralFeature("eType"), false);
		assertNotNull(eType, "the reference target must be present");
		assertTrue(eType.eIsProxy(), "the cross-document target must remain a proxy");
		URI proxyURI = ((InternalEObject) eType).eProxyURI();
		assertEquals("_remote", proxyURI.fragment(), "the proxy must keep the referenced id: " + proxyURI);
		assertEquals("_remoteRef", ((XMLResource) root.eResource()).getID(remote));
	}

	// --- round trip --------------------------------------------------------

	@Test
	@DisplayName("readFrom -> writeTo emits the same xmi:ids and id-based references")
	void roundTripKeepsIds() throws IOException {
		EObject root = read(XMI_WITH_IDS);

		String xmi = write(root);

		assertTrue(xmi.contains("xmi:id=\"_pkg\""), xmi);
		assertTrue(xmi.contains("xmi:id=\"_person\""), xmi);
		assertTrue(xmi.contains("xmi:id=\"_friend\""), xmi);
		assertTrue(xmi.contains("eType=\"_person\""), "the reference must be written by id: " + xmi);
		assertFalse(xmi.contains("//@"), "no containment-path references may appear: " + xmi);
		assertTrue(resourceSet.getResources().isEmpty(),
				"no temporary resource may be left behind, but found: " + resourceSet.getResources());
	}

	@Test
	@DisplayName("readFrom -> writeTo of a document without xmi:ids emits none")
	void roundTripWithoutIdsEmitsNone() throws IOException {
		EObject root = read(XMI_WITHOUT_IDS);

		String xmi = write(root);

		assertFalse(xmi.contains("xmi:id="), "no ids may be invented: " + xmi);
		assertTrue(xmi.contains("eType=\"//Person\""), "the reference must be written as a path: " + xmi);
	}

	// --- writing -----------------------------------------------------------

	@Test
	@DisplayName("writeTo transfers ids when the object's resource is of a different type than the response")
	void writeTransfersIdsFromForeignResourceType() throws IOException {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("issue246");
		ePackage.setNsURI("http://example.org/issue246");
		ePackage.setNsPrefix("i246");
		EClass person = EcoreFactory.eINSTANCE.createEClass();
		person.setName("Person");
		ePackage.getEClassifiers().add(person);
		// XMLResourceImpl, not XMIResourceImpl: forces the copy branch of writeResourceTo
		XMLResourceImpl source = new XMLResourceImpl(URI.createURI("issue246.xml"));
		source.getContents().add(ePackage);
		source.setID(ePackage, "_pkg");
		source.setID(person, "_person");

		String xmi = write(ePackage);

		assertTrue(xmi.contains("xmi:id=\"_pkg\""), xmi);
		assertTrue(xmi.contains("xmi:id=\"_person\""), xmi);
		assertSame(source, ePackage.eResource(), "the live object must stay in its own resource");
		assertEquals("_pkg", source.getID(ePackage), "the source ids must survive the write");
		assertTrue(resourceSet.getResources().isEmpty(),
				"no temporary resource may be left behind, but found: " + resourceSet.getResources());
	}

	@Test
	@DisplayName("writeTo of a resource-less object works as before and tells so at INFO level")
	void writeOfDetachedObjectLogsInfo() throws IOException {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("issue246");
		ePackage.setNsURI("http://example.org/issue246");
		ePackage.setNsPrefix("i246");

		String xmi = write(ePackage);

		assertTrue(xmi.contains("name=\"issue246\""), xmi);
		assertFalse(xmi.contains("xmi:id="), "a detached object has no ids to emit: " + xmi);
		assertNull(ePackage.eResource(), "live object must not have a resource after the write");
		assertEquals(1, capture.records.size(), "exactly one record must be logged, got: " + capture.records);
		LogRecord record = capture.records.get(0);
		assertEquals(Level.INFO, record.getLevel());
		assertTrue(record.getMessage().toLowerCase().contains("id"),
				"the message must explain that ids cannot be preserved: " + record.getMessage());
	}

	@Test
	@DisplayName("writeTo of an attached object logs nothing")
	void writeOfAttachedObjectLogsNothing() throws IOException {
		EObject root = read(XMI_WITH_IDS);

		write(root);

		assertTrue(capture.records.isEmpty(), "no record expected, got: " + capture.records);
	}
}
