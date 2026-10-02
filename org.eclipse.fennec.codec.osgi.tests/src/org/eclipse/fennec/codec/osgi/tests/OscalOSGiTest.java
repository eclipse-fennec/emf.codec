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
package org.eclipse.fennec.codec.osgi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.oscal.OscalResourceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import gov.nist.csrc.ns.oscal.Catalog;
import gov.nist.csrc.ns.oscal.DocumentRoot;

/**
 * The OSCAL codec in OSGi (issue #251): the DS component registers the resource factory for
 * {@code application/oscal+json} once the OSCAL model is there, and a resource set picks it by
 * content type.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("OSCAL codec in OSGi")
public class OscalOSGiTest {

	private static final String CATALOG = "/org/eclipse/fennec/codec/osgi/tests/oscal-catalog.json";

	@InjectService(filter = "(emf.contentType=application/oscal+json)")
	Resource.Factory oscalFactory;

	// the resource set service carries the properties of the factories it knows
	@InjectService(filter = "(emf.contentType=application/oscal+json)")
	ResourceSet resourceSet;

	private static void assertCatalog(Resource resource) {
		assertEquals(List.of(), resource.getErrors());
		assertEquals(List.of(), resource.getWarnings());
		Catalog catalog = assertInstanceOf(DocumentRoot.class, resource.getContents().get(0)).getCatalog();
		assertEquals("6f1d2a4e-8c3b-4e5f-9a7b-1c2d3e4f5a6b", catalog.getUuid());
		assertEquals("The organization reviews the policy {{ insert: param, ctl-1_prm_1 }}.",
				catalog.getControl().get(0).getPart().get(0).getProse());
	}

	@Test
	@DisplayName("the factory service reads and writes an OSCAL catalog")
	void factoryService() throws IOException {
		Resource resource = oscalFactory.createResource(URI.createURI("test://catalog.json"));
		assertInstanceOf(OscalResourceImpl.class, resource);
		try (InputStream in = OscalOSGiTest.class.getResourceAsStream(CATALOG)) {
			resource.load(in, Map.of());
		}
		assertCatalog(resource);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());
		Resource reloaded = oscalFactory.createResource(URI.createURI("test://catalog2.json"));
		reloaded.load(new ByteArrayInputStream(out.toByteArray()), Map.of());
		assertCatalog(reloaded);
	}

	@Test
	@DisplayName("a resource set creates the OSCAL resource by content type")
	void resourceSetByContentType() throws IOException {
		Resource resource = resourceSet.createResource(URI.createURI("test://by-content-type.json"),
				"application/oscal+json");
		assertInstanceOf(OscalResourceImpl.class, resource);
		try (InputStream in = OscalOSGiTest.class.getResourceAsStream(CATALOG)) {
			resource.load(in, Map.of());
		}
		assertCatalog(resource);
	}
}
