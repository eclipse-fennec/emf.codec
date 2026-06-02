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
package org.eclipse.fennec.codec.rest.jakartas.feature;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.rest.jakartas.spi.ResourceSetProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsApplicationSelect;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsExtension;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsName;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

/**
 * Releases the per-request {@link ResourceSet} produced by the bound
 * {@link ResourceSetProvider} after the response has been generated. Pairs
 * with {@link CodecResourceSetFeature}, whose request-scoped supplier
 * stashes the active provider and {@link ResourceSet} under
 * {@link #ACTIVE_PROVIDER_PROPERTY} / {@link #ACTIVE_RESOURCE_SET_PROPERTY}
 * on first resolution.
 *
 * <p>Jersey's {@code DisposableSupplier.dispose()} is not reliably invoked
 * when the produced binding is proxied; this response filter is the
 * deterministic counterpart that guarantees the provider's
 * {@link ResourceSetProvider#releaseResourceSet} hook runs exactly once
 * per request &mdash; and only when a {@link ResourceSet} was actually
 * requested.
 *
 * @author Data In Motion
 * @since 1.0
 */
@Component
@JakartarsExtension
@JakartarsName(CodecResourceSetCleanupFilter.NAME)
@JakartarsApplicationSelect("(|(emf=true)("+ JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "=.default))")
public class CodecResourceSetCleanupFilter implements ContainerResponseFilter {

	/**
	 * Whiteboard name under which this filter is published
	 * ({@code osgi.jakartars.name}).
	 */
	public static final String NAME = "CodecResourceSetCleanupFilter";

	/**
	 * Request-context property under which the active
	 * {@link ResourceSetProvider} is stashed by the supplier; read here to
	 * invoke release.
	 */
	public static final String ACTIVE_PROVIDER_PROPERTY = "codecResourceSet.activeProvider";

	/**
	 * Request-context property under which the active {@link ResourceSet}
	 * instance is stashed by the supplier; read here to release back to
	 * the provider.
	 */
	public static final String ACTIVE_RESOURCE_SET_PROPERTY = "codecResourceSet.activeInstance";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
		Object providerProp = requestContext.getProperty(ACTIVE_PROVIDER_PROPERTY);
		Object rsProp = requestContext.getProperty(ACTIVE_RESOURCE_SET_PROPERTY);
		try {
			if (providerProp instanceof ResourceSetProvider provider && rsProp instanceof ResourceSet rs) {
				provider.releaseResourceSet(rs, requestContext);
			}
		} finally {
			requestContext.removeProperty(ACTIVE_PROVIDER_PROPERTY);
			requestContext.removeProperty(ACTIVE_RESOURCE_SET_PROPERTY);
		}
	}
}
