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
package org.eclipse.fennec.codec.rest.jakartas.internal;

import java.util.function.Supplier;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.rest.jakartas.feature.CodecResourceSetCleanupFilter;
import org.eclipse.fennec.codec.rest.jakartas.feature.CodecResourceSetFeature;
import org.eclipse.fennec.codec.rest.jakartas.spi.ResourceSetProvider;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;

/**
 * Jersey {@link Supplier} that resolves a per-request {@link ResourceSet}
 * by delegating to the currently bound {@link ResourceSetProvider}.
 *
 * <p>Registered by {@link CodecResourceSetFeature} as
 * {@code bindFactory(...).in(RequestScoped.class)} so that {@link #get()}
 * runs once per request. The resolved {@link ResourceSet} and the
 * {@link ResourceSetProvider} that produced it are stashed on the
 * {@link ContainerRequestContext} under
 * {@link CodecResourceSetCleanupFilter#ACTIVE_PROVIDER_PROPERTY} /
 * {@link CodecResourceSetCleanupFilter#ACTIVE_RESOURCE_SET_PROPERTY} so
 * that {@link CodecResourceSetCleanupFilter} can release the
 * {@link ResourceSet} after the response has been written. (Jersey's
 * {@code DisposableSupplier.dispose()} is unreliable for proxied bindings,
 * so the cleanup is implemented as a {@code ContainerResponseFilter}
 * instead.)
 *
 * @author Data In Motion
 * @since 1.0
 */
public class CodecResourceSetSupplier implements Supplier<ResourceSet> {

	@Inject
	CodecResourceSetFeature feature;

	@Context
	Provider<ContainerRequestContext> requestContextProvider;

	@Override
	public ResourceSet get() {
		ContainerRequestContext ctx = requestContextProvider.get();
		ResourceSetProvider provider = feature.getResourceSetProvider();
		ResourceSet rs = provider.getResourceSet(ctx);
		ctx.setProperty(CodecResourceSetCleanupFilter.ACTIVE_PROVIDER_PROPERTY, provider);
		ctx.setProperty(CodecResourceSetCleanupFilter.ACTIVE_RESOURCE_SET_PROPERTY, rs);
		return rs;
	}
}
