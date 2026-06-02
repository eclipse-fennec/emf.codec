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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.codec.rest.jakartas.feature.CodecResourceSetCleanupFilter;
import org.eclipse.fennec.codec.rest.jakartas.spi.ResourceSetProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

/**
 * Verifies that the codec cleanup filter is published as a JAX-RS
 * extension and that its release contract drains the per-request
 * properties even when nothing was injected.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("CodecResourceSetCleanupFilter registration")
public class CodecResourceSetCleanupFilterOSGiTest {

	private static final String FILTER_FILTER = "("
			+ JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "="
			+ CodecResourceSetCleanupFilter.NAME + ")";

	@Test
	@DisplayName("registered as a JAX-RS ContainerResponseFilter via the Whiteboard")
	void filterIsRegisteredAsJaxrsExtension(@InjectBundleContext BundleContext ctx) throws Exception {
		ServiceReference<?>[] refs = ctx.getServiceReferences(
				ContainerResponseFilter.class.getName(), FILTER_FILTER);

		assertNotNull(refs, "filter with name=" + CodecResourceSetCleanupFilter.NAME + " should be registered");
		assertEquals(1, refs.length);

		ServiceReference<?> ref = refs[0];
		assertEquals(Boolean.TRUE, ref.getProperty(JakartarsWhiteboardConstants.JAKARTA_RS_EXTENSION),
				"filter should be marked as a JAX-RS extension");
		assertNotNull(ref.getProperty(JakartarsWhiteboardConstants.JAKARTA_RS_APPLICATION_SELECT),
				"filter should declare an application selector");
	}

	@Test
	@DisplayName("filter() invokes the stashed provider's release and clears the request properties")
	void releasesAndClears(@InjectBundleContext BundleContext ctx) throws Exception {
		ServiceReference<?>[] refs = ctx.getServiceReferences(
				ContainerResponseFilter.class.getName(), FILTER_FILTER);
		ContainerResponseFilter filter = (ContainerResponseFilter) ctx.getService(refs[0]);
		try {
			ResourceSet rs = new ResourceSetImpl();
			RecordingProvider provider = new RecordingProvider(rs);
			FakeContainerRequestContext requestCtx = new FakeContainerRequestContext();
			requestCtx.setProperty(CodecResourceSetCleanupFilter.ACTIVE_PROVIDER_PROPERTY, provider);
			requestCtx.setProperty(CodecResourceSetCleanupFilter.ACTIVE_RESOURCE_SET_PROPERTY, rs);

			filter.filter(requestCtx, null);

			assertSame(rs, provider.released);
			assertNull(requestCtx.getProperty(CodecResourceSetCleanupFilter.ACTIVE_PROVIDER_PROPERTY));
			assertNull(requestCtx.getProperty(CodecResourceSetCleanupFilter.ACTIVE_RESOURCE_SET_PROPERTY));
		} finally {
			ctx.ungetService(refs[0]);
		}
	}

	@Test
	@DisplayName("filter() is a no-op (and still drains properties) when no ResourceSet was injected")
	void noopWhenNothingStashed(@InjectBundleContext BundleContext ctx) throws Exception {
		ServiceReference<?>[] refs = ctx.getServiceReferences(
				ContainerResponseFilter.class.getName(), FILTER_FILTER);
		ContainerResponseFilter filter = (ContainerResponseFilter) ctx.getService(refs[0]);
		try {
			FakeContainerRequestContext requestCtx = new FakeContainerRequestContext();

			filter.filter(requestCtx, null);

			assertNull(requestCtx.getProperty(CodecResourceSetCleanupFilter.ACTIVE_PROVIDER_PROPERTY));
			assertNull(requestCtx.getProperty(CodecResourceSetCleanupFilter.ACTIVE_RESOURCE_SET_PROPERTY));
		} finally {
			ctx.ungetService(refs[0]);
		}
	}

	private static final class RecordingProvider implements ResourceSetProvider {
		private final ResourceSet rs;
		ResourceSet released;

		RecordingProvider(ResourceSet rs) {
			this.rs = rs;
		}

		@Override
		public ResourceSet getResourceSet(ContainerRequestContext ctx) {
			return rs;
		}

		@Override
		public void releaseResourceSet(ResourceSet rs, ContainerRequestContext ctx) {
			released = rs;
		}
	}
}
