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
package org.eclipse.fennec.codec.rest.jakartas.spi;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.annotation.versioning.ConsumerType;

import jakarta.ws.rs.container.ContainerRequestContext;

/**
 * SPI that resolves the per-request {@link ResourceSet} consumed by the
 * codec's JAX-RS message body handlers.
 *
 * <p>The codec ships a default implementation that wraps the OSGi
 * {@code ResourceSetFactory} and produces a fresh {@code ResourceSet} per
 * request. Downstream bundles can replace it transparently by publishing a
 * higher-ranked {@code ResourceSetProvider} OSGi service &mdash; the
 * codec's JAX-RS feature picks the highest-ranked one via
 * {@code DYNAMIC}/{@code GREEDY} binding.
 *
 * <p>Implementations that lease a {@code ResourceSet} from a
 * {@code ComponentServiceObjects} (or similar pooled source) should release
 * it from {@link #releaseResourceSet(ResourceSet, ContainerRequestContext)},
 * which is invoked once per request after the response has been written. The
 * {@link ContainerRequestContext} passed to both methods is identical, so
 * implementations may stash bookkeeping state via
 * {@link ContainerRequestContext#setProperty(String, Object)}.
 *
 * @author Data In Motion
 * @since 1.0
 */
@ConsumerType
public interface ResourceSetProvider {

	/**
	 * Resolves the {@link ResourceSet} for the given request. Called once per
	 * request, lazily, when the {@link ResourceSet} is first injected.
	 *
	 * @param requestContext the current JAX-RS request context; never
	 *        {@code null}
	 * @return the {@link ResourceSet} to expose for this request; never
	 *         {@code null}
	 */
	ResourceSet getResourceSet(ContainerRequestContext requestContext);

	/**
	 * Releases the {@link ResourceSet} returned by
	 * {@link #getResourceSet(ContainerRequestContext)}. Called once per
	 * request after the response has been written, only when
	 * {@code getResourceSet} actually produced an instance. The default
	 * implementation is a no-op.
	 *
	 * @param resourceSet the {@link ResourceSet} previously returned; never
	 *        {@code null}
	 * @param requestContext the current JAX-RS request context; never
	 *        {@code null}
	 */
	default void releaseResourceSet(ResourceSet resourceSet, ContainerRequestContext requestContext) {
		// no-op by default
	}
}
