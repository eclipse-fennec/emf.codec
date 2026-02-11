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

import java.util.Set;

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
 * @since Jul 24, 2025
 */
public class EClassReaderByQualifiedName implements CodecValueReader<String, EClass>{
	
//	private ResourceSet resourceSet;
	private ComponentServiceObjects<ResourceSet> rsFactory;
	
	public EClassReaderByQualifiedName(ComponentServiceObjects<ResourceSet> rsFactory) {
		this.rsFactory = rsFactory;		
	}
	
//	public EClassReaderByQualifiedName(ResourceSet resourceSet) {
//		this.resourceSet = resourceSet;
//	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecValueReader#getName()
	 */
	@Override
	public String getName() {
		return CodecValueReaderConstants.READER_BY_INSTANCE_CLASS_NAME;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.info.codecinfo.CodecValueReader#readValue(java.lang.Object, tools.jackson.databind.DeserializationContext)
	 */
	@Override
	public EClass readValue(String value, DeserializationContext context) {
		ResourceSet resourceSet = rsFactory.getService();
		try {
			Set<EClass> types = CodecIOHelper.getAllTypes(resourceSet);
			return types.stream().filter(CodecIOHelper.findByQualifiedName(value)).findFirst().orElse(null);
		} finally {
			rsFactory.ungetService(resourceSet);
		}
	}

}
