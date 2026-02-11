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

import org.eclipse.emf.common.util.URI;

/**
 * 
 * @author ilenia
 * @since Apr 24, 2025
 */
public interface URIHandler {
	
	/**
	    * Returns the URI {@link URI#resolve(URI) resolved} against the base URI.
	    *
	    * @param baseURI the base URI against which the URI is resolved.
	    * @param uri     the URI to resolve.
	    * @return the URI resolved against the base URI.
	    * @see URI#resolve(URI)
	    */
	   URI resolve(URI baseURI, URI uri);

	   /**
	    * Returns the URI {@link URI#deresolve(URI) deresolved} against the base URI.
	    *
	    * @param baseURI the base URI against which the URI is deresolved.
	    * @param uri     the URI to resolve.
	    * @return the URI resolved against the base URI.
	    * @see URI#deresolve(URI)
	    */
	   URI deresolve(URI baseURI, URI uri);

}
