/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.java;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;

/**
 * 
 * @author Mark Hoffmanns
 * @since 02.12.2025
 */
public class ResourceSetComponentServiceObject implements ComponentServiceObjects<ResourceSet> {
	
	final ResourceSet refResourceSet;

	/**
	 * Creates a new instance.
	 */
	public ResourceSetComponentServiceObject(ResourceSet refResourceSet) {
		this.refResourceSet = requireNonNull(refResourceSet);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.component.ComponentServiceObjects#getService()
	 */
	@Override
	public ResourceSet getService() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getPackageRegistry().putAll(refResourceSet.getPackageRegistry());
		rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().putAll(refResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().putAll(refResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap());
		rs.getResourceFactoryRegistry().getProtocolToFactoryMap().putAll(refResourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap());
		return rs;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.component.ComponentServiceObjects#ungetService(java.lang.Object)
	 */
	@Override
	public void ungetService(ResourceSet service) {
		if (Objects.nonNull(service)) {
			service.getResources().forEach(r->r.getContents().clear());
			service.getResources().clear();
			service.getPackageRegistry().clear();
			service.getResourceFactoryRegistry().getContentTypeToFactoryMap().clear();
			service.getResourceFactoryRegistry().getExtensionToFactoryMap().clear();
			service.getResourceFactoryRegistry().getProtocolToFactoryMap().clear();
		}

	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.service.component.ComponentServiceObjects#getServiceReference()
	 */
	@Override
	public ServiceReference<ResourceSet> getServiceReference() {
		return null;
	}

}
