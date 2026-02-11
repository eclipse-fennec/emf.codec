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

import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;

import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.exc.StreamReadException;

/**
 * 
 * @author ilenia
 * @since May 7, 2025
 */
public interface EMFCodecReadContext extends EMFCodecContext {
	
	TokenStreamContext createChildArrayContext(int lineNr, int colNr);

	TokenStreamContext createChildObjectContext(int lineNr, int colNr);
	
	TokenStreamContext reset(int type, int lineNr, int colNr);
	
	TokenStreamContext clearAndGetParent();
	
	void setCurrentName(String name) throws StreamReadException;
	
	boolean hasParentContext();
	
	void setCurrentTypeInfo(TypeInfo typeInfo);
	
	TypeInfo getCurrentTypeInfo();
	
	
	
//	TokenStreamContext getParentContext();

}
