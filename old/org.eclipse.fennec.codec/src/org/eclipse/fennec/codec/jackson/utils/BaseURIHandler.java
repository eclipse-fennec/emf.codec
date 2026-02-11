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
public class BaseURIHandler implements URIHandler {

	   protected boolean resolve(final URI baseURI) {
	      return baseURI != null && baseURI.isHierarchical() && !baseURI.isRelative();
	   }

	   @Override
	   public URI resolve(final URI baseURI, final URI uri) {
	      return resolve(baseURI) && uri.isRelative() && uri.hasRelativePath() ? uri.resolve(baseURI, false) : uri;
	   }

	   @Override
	   public URI deresolve(final URI baseURI, URI uri) {
	      if (resolve(baseURI) && !uri.isRelative()) {
	         URI deresolvedURI = uri.deresolve(baseURI, true, true, false);
	         if (deresolvedURI.hasRelativePath()) {
	            uri = deresolvedURI;
	         }
	      }
	      return uri;
	   }
}
