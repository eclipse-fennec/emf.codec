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
package org.eclipse.fennec.codec.info.value.readers;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.helper.CodecIOHelper;
import org.eclipse.fennec.codec.options.CodecValueReaderConstants;
import org.osgi.service.component.ComponentServiceObjects;

import tools.jackson.databind.DeserializationContext;

/**
 * 
 * @author ilenia
 * @since Jul 18, 2025
 */
public class EClassReaderByName implements CodecValueReader<String, EClass>{

//	private ResourceSet resourceSet;
	private ComponentServiceObjects<ResourceSet> rsFactory;

	public EClassReaderByName() {

	}
	
	public EClassReaderByName(ComponentServiceObjects<ResourceSet> rsFactory) {
		this.rsFactory = rsFactory;
		
	}

//	public EClassReaderByName(ResourceSet resourceSet) {
//		this.resourceSet = resourceSet;
//	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecValueReader#getName()
	 */
	@Override
	public String getName() {

		return CodecValueReaderConstants.READER_BY_ECLASS_NAME;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecValueReader#readValue(java.lang.Object, tools.jackson.databind.DeserializationContext)
	 */
	@Override
	public EClass readValue(String value, DeserializationContext context) {
		ResourceSet resourceSet = rsFactory.getService();
		try {
			return CodecIOHelper.findEClassByName(value, resourceSet);
		} finally {
			rsFactory.ungetService(resourceSet);
		}
		
	}
}
