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
package org.eclipse.fennec.codec.jackson.databind;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;

/**
 * 
 * @author mark
 * @since 06.05.2025
 */
public class EMFContextHolder {
	
	private EStructuralFeature _currentFeature;
	private EObject _currentEObj;
	private Resource _resource;
	private TypeInfo _currentTypeInfo;
	

	public void setCurrentEObject(EObject eObj) {
		_currentEObj = eObj;
	}

	
	public EObject getCurrentEObject() {
		return _currentEObj;
	}


	public void setCurrentFeature(EStructuralFeature feature) {
		_currentFeature = feature;
	}
	

	public EStructuralFeature getCurrentFeature() {
		return _currentFeature;
	}
	

	public void setResource(Resource resource) {
		_resource = resource;		
	}


	public Resource getResource() {
		return _resource;
	}

	public void resetFeature() {
		_currentFeature = null;		
	}
	
	public void setCurrentTypeInfo(TypeInfo currentTypeInfo) {
		_currentTypeInfo = currentTypeInfo;
	}
	
	public TypeInfo getCurrentTypeInfo() {
		return _currentTypeInfo;
	}
}
