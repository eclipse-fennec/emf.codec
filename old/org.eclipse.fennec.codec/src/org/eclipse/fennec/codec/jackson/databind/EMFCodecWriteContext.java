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

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.jackson.core.TokenStreamContext;

/**
 * 
 * @author mark
 * @since 06.05.2025
 */
public interface EMFCodecWriteContext extends EMFCodecContext{


	TokenStreamContext createChildArrayContext();

	TokenStreamContext createChildArrayContext(Object currValue);

	TokenStreamContext createChildObjectContext();

	TokenStreamContext createChildObjectContext(Object currValue);
	
	TokenStreamContext clearAndGetParent();
	
	TokenStreamContext reset(int type, Object currValue);
	
	int writeFeatureAndFieldName(EStructuralFeature feature, String name);

}