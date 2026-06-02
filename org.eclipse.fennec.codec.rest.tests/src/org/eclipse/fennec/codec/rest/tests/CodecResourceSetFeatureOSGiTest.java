/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.rest.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.fennec.codec.rest.jakartas.feature.CodecResourceSetFeature;
import org.eclipse.fennec.codec.rest.jakartas.spi.ResourceSetProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import jakarta.ws.rs.core.Feature;

/**
 * Verifies the OSGi registration shape of
 * {@link CodecResourceSetFeature}: it is exposed as a JAX-RS
 * {@code Feature} via the OSGi Jakarta RS Whiteboard with the expected
 * extension property, application selector, and name; and binds the
 * default {@link ResourceSetProvider} out of the box.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("CodecResourceSetFeature registration")
public class CodecResourceSetFeatureOSGiTest {

	private static final String FEATURE_FILTER = "("
			+ JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "="
			+ CodecResourceSetFeature.NAME + ")";

	@Test
	@DisplayName("registered as a JAX-RS Feature service with the codec's whiteboard properties")
	void featureIsRegisteredAsJaxrsExtension(@InjectBundleContext BundleContext ctx) throws Exception {
		ServiceReference<?>[] refs = ctx.getServiceReferences(Feature.class.getName(), FEATURE_FILTER);

		assertNotNull(refs, "Feature with name=" + CodecResourceSetFeature.NAME + " should be registered");
		assertEquals(1, refs.length);

		ServiceReference<?> ref = refs[0];
		assertEquals(Boolean.TRUE, ref.getProperty(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION),
				"feature should be marked as a JAX-RS extension");
		Object appSelect = ref.getProperty(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT);
		assertNotNull(appSelect, "feature should declare an application selector");
		String rendered = appSelect instanceof String[] arr ? String.join(",", arr) : appSelect.toString();
		assertTrue(rendered.contains("emf=true"),
				"application selector should target EMF-flagged applications, was: " + rendered);
	}

	@Test
	@DisplayName("getResourceSetProvider returns the default ResourceSetProvider out of the box")
	void featureBindsDefaultProvider(@InjectService(filter = FEATURE_FILTER) Feature featureService) {
		CodecResourceSetFeature feature = (CodecResourceSetFeature) featureService;
		ResourceSetProvider bound = feature.getResourceSetProvider();
		assertNotNull(bound);
		assertNotNull(bound.getResourceSet(null));
	}
}
