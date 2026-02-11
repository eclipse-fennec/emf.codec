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

/**
 * 
 * @author ilenia
 * @since May 7, 2025
 */
public interface EMFCodecContext {

	void setCurrentFeature(EStructuralFeature feature);

	EStructuralFeature getCurrentFeature();

	void setCurrentEObject(EObject eObj);

	EObject getCurrentEObject();

	void setResource(Resource resource);

	Resource getResource();

	void resetFeature();

	EMFContextHolder getEMFContextHolder();
	
	void setEMFContextHolder(EMFContextHolder holder);
}
