/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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
package org.eclipse.fennec.codec.rest.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.junit5.context.BundleContextExtension;

import jakarta.ws.rs.container.ContainerRequestFilter;

/**
 * The {@code ClientCodecOptionsFilter} must attach to the same applications as the message body
 * handlers it feeds (issue #170). Without an application select it lands on {@code .default}
 * only, and a whiteboard application flagged {@code emf=true} gets the handlers but silently
 * ignores the {@code Codec-Options} header.
 */
@ExtendWith(BundleContextExtension.class)
@DisplayName("ClientCodecOptionsFilter registration")
public class ClientCodecOptionsFilterOSGiTest {

	private static final String FILTER = "(" + JakartarsWhiteboardConstants.JAKARTA_RS_NAME
			+ "=ClientCodecOptionsFilter)";

	@Test
	@DisplayName("registered as a JAX-RS extension with the handlers' application selector")
	void filterSelectsTheSameApplicationsAsTheHandlers(@InjectBundleContext BundleContext ctx) throws Exception {
		ServiceReference<?>[] refs = ctx.getServiceReferences(ContainerRequestFilter.class.getName(), FILTER);

		assertNotNull(refs, "ClientCodecOptionsFilter should be registered as a ContainerRequestFilter");
		assertEquals(1, refs.length);

		ServiceReference<?> ref = refs[0];
		assertEquals(Boolean.TRUE, ref.getProperty(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION));
		Object appSelect = ref.getProperty(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT);
		assertNotNull(appSelect, "the filter must declare the application selector the handlers use");
		String rendered = appSelect instanceof String[] arr ? String.join(",", arr) : appSelect.toString();
		assertTrue(rendered.contains("emf=true") && rendered.contains(".default"),
				"selector should target EMF-flagged applications and .default, was: " + rendered);
	}
}
