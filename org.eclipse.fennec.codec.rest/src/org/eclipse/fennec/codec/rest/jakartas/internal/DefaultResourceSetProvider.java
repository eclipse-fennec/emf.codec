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

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.rest.jakartas.spi.ResourceSetProvider;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.ws.rs.container.ContainerRequestContext;

/**
 * Default {@link ResourceSetProvider} that delegates to the OSGi
 * {@link ResourceSetFactory} service and returns a fresh
 * {@link ResourceSet} per request.
 *
 * <p>Registered with no explicit service ranking so any
 * {@link ResourceSetProvider} published by a downstream bundle (with a
 * higher {@code service.ranking}) wins via the codec's feature greedy
 * binding.
 *
 * @author Data In Motion
 * @since 1.0
 */
@Component(service = ResourceSetProvider.class)
public class DefaultResourceSetProvider implements ResourceSetProvider {

	private final ResourceSetFactory resourceSetFactory;

	@org.osgi.service.component.annotations.Activate
	public DefaultResourceSetProvider(@Reference ResourceSetFactory resourceSetFactory) {
		this.resourceSetFactory = resourceSetFactory;
	}

	@Override
	public ResourceSet getResourceSet(ContainerRequestContext requestContext) {
		return resourceSetFactory.createResourceSet();
	}
}
