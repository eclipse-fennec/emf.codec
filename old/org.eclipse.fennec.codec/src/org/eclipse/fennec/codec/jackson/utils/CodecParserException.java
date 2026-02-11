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
package org.eclipse.fennec.codec.jackson.utils;

import org.eclipse.emf.ecore.resource.Resource;

import tools.jackson.core.TokenStreamLocation;

/**
 * 
 * @author ilenia
 * @since Apr 25, 2025
 */
public class CodecParserException  extends Exception implements Resource.Diagnostic {

	   private static final long serialVersionUID = -7658549711611092935L;
	   private final String location;
	   private final int line;
	   private final int column;

	   public CodecParserException(final String message, final TokenStreamLocation location) {
	      super(message);
	      this.location = location.toString();
	      this.line = location.getLineNr();
	      this.column = location.getColumnNr();
	   }

	   public CodecParserException(final Exception e, final TokenStreamLocation location) {
	      super(e);
	      this.location = location.toString();
	      this.line = location.getLineNr();
	      this.column = location.getColumnNr();
	   }

	   /* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic#getLocation()
	 */
	@Override
	   public String getLocation() { return location; }

	   /* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic#getLine()
	 */
	@Override
	   public int getLine() { return line; }

	   /* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.Resource.Diagnostic#getColumn()
	 */
	@Override
	   public int getColumn() { return column; }


}
