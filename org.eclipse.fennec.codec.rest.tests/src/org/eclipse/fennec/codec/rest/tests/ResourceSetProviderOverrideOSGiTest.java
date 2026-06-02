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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.codec.rest.jakartas.feature.CodecResourceSetFeature;
import org.eclipse.fennec.codec.rest.jakartas.spi.ResourceSetProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Feature;

/**
 * Verifies the self-assembling override mechanism: publishing a
 * higher-ranked {@link ResourceSetProvider} causes the codec's
 * {@link CodecResourceSetFeature} to rebind to it via its
 * {@code DYNAMIC}/{@code GREEDY} reference. Unregistering the override
 * falls back to the codec's default provider.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("ResourceSetProvider override (service ranking)")
public class ResourceSetProviderOverrideOSGiTest {

	private static final String FEATURE_FILTER = "("
			+ JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "="
			+ CodecResourceSetFeature.NAME + ")";

	@Test
	@DisplayName("higher-ranked ResourceSetProvider replaces the default; unregistering falls back")
	void higherRankedProviderShadowsDefault(
			@InjectBundleContext BundleContext ctx,
			@InjectService(filter = FEATURE_FILTER) Feature featureService)
			throws Exception {
		CodecResourceSetFeature feature = (CodecResourceSetFeature) featureService;
		ResourceSetProvider beforeOverride = feature.getResourceSetProvider();
		assertNotNull(beforeOverride);

		ResourceSet overrideRs = new ResourceSetImpl();
		StubProvider override = new StubProvider(overrideRs);

		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		ServiceRegistration<ResourceSetProvider> reg = ctx.registerService(
				ResourceSetProvider.class, override, props);
		try {
			ResourceSetProvider rebound = awaitRebind(feature, beforeOverride);
			assertSame(override, rebound, "feature should pick up the higher-ranked provider via GREEDY rebind");
			assertSame(overrideRs, rebound.getResourceSet(null));
		} finally {
			reg.unregister();
		}

		ResourceSetProvider afterUnregister = awaitRebind(feature, override);
		assertNotSame(override, afterUnregister,
				"feature should fall back to a different provider once the override goes away");
		assertNotNull(afterUnregister.getResourceSet(null));
	}

	@Test
	@DisplayName("higher-ranked override is visible at the OSGi service registry as the highest-ranked ResourceSetProvider")
	void overrideWinsServiceRanking(@InjectBundleContext BundleContext ctx) throws Exception {
		StubProvider override = new StubProvider(new ResourceSetImpl());
		Hashtable<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
		ServiceRegistration<ResourceSetProvider> reg = ctx.registerService(
				ResourceSetProvider.class, override, props);
		try {
			ServiceReference<ResourceSetProvider> top = ctx.getServiceReference(ResourceSetProvider.class);
			assertNotNull(top);
			Integer ranking = (Integer) top.getProperty(Constants.SERVICE_RANKING);
			assertEquals(Integer.MAX_VALUE, ranking,
					"highest-ranked provider should be the override we just registered");
			assertSame(override, ctx.getService(top));
		} finally {
			reg.unregister();
		}
	}

	/**
	 * Waits up to a few seconds for the feature's bound provider to
	 * change away from {@code previous}. The DS framework rebinds
	 * asynchronously when a higher-ranked service appears/disappears.
	 */
	private static ResourceSetProvider awaitRebind(CodecResourceSetFeature feature,
			ResourceSetProvider previous) throws InterruptedException {
		long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
		ResourceSetProvider current = feature.getResourceSetProvider();
		while (current == previous && System.currentTimeMillis() < deadline) {
			Thread.sleep(50);
			current = feature.getResourceSetProvider();
		}
		if (current == previous) {
			fail("feature did not rebind away from " + previous + " within timeout");
		}
		return current;
	}

	private static final class StubProvider implements ResourceSetProvider {
		private final ResourceSet rs;

		StubProvider(ResourceSet rs) {
			this.rs = rs;
		}

		@Override
		public ResourceSet getResourceSet(ContainerRequestContext requestContext) {
			return rs;
		}
	}
}
