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
package org.eclipse.fennec.codec.rest.jakartas.filter;

import java.io.IOException;

import org.eclipse.fennec.codec.rest.jakartas.JakartaRestConstants;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsExtension;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsName;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/**
 * 
 * @author ilenia
 * @since May 19, 2026
 */

@Component
@JakartarsExtension
@JakartarsName("ResourceSetFilter")
@ServiceRanking(1) 
public class BasicResourceSetFilter implements ContainerRequestFilter {

	@Reference
	private ResourceSetFactory resourceSetFactory;
	
	/* 
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.container.ContainerRequestFilter#filter(jakarta.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		requestContext.setProperty(JakartaRestConstants.RESOLVED_RESOURCE_SET_FACTORY, resourceSetFactory);
	}
	

}
