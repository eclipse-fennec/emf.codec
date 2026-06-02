/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.codec.rest.tests;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

/**
 * Minimal {@link ContainerRequestContext} stand-in used by the OSGi
 * integration tests. Only the property API is meaningful — everything
 * else throws to make accidental misuse obvious.
 */
class FakeContainerRequestContext implements ContainerRequestContext {

	private final Map<String, Object> properties = new HashMap<>();

	@Override
	public Object getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public Collection<String> getPropertyNames() {
		return properties.keySet();
	}

	@Override
	public void setProperty(String name, Object object) {
		if (object == null) {
			properties.remove(name);
		} else {
			properties.put(name, object);
		}
	}

	@Override
	public void removeProperty(String name) {
		properties.remove(name);
	}

	@Override
	public UriInfo getUriInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRequestUri(java.net.URI requestUri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRequestUri(java.net.URI baseUri, java.net.URI requestUri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Request getRequest() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMethod(String method) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MultivaluedMap<String, String> getHeaders() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHeaderString(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLanguage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLength() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MediaType getMediaType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<MediaType> getAcceptableMediaTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Locale> getAcceptableLanguages() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Cookie> getCookies() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getEntityStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEntityStream(InputStream input) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SecurityContext getSecurityContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSecurityContext(SecurityContext context) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void abortWith(Response response) {
		throw new UnsupportedOperationException();
	}
}
