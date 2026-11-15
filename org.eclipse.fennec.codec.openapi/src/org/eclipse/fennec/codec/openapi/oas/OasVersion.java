/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.codec.openapi.oas;

/**
 * The OpenAPI versions the {@code org.eclipse.fennec.model.oas} model covers. A resource reads and
 * writes one of them; patch releases (3.2.0, 3.2.1) share a version.
 */
public enum OasVersion {

	V3_0("3.0"),
	V3_1("3.1"),
	V3_2("3.2");

	/** The IANA media type of OpenAPI in JSON; the version goes into its {@code version} parameter. */
	public static final String MEDIA_TYPE_JSON = "application/vnd.oai.openapi+json";

	/** The IANA media type of OpenAPI in YAML. */
	public static final String MEDIA_TYPE_YAML = "application/vnd.oai.openapi";

	private final String version;

	OasVersion(String version) {
		this.version = version;
	}

	/** @return {@code major.minor}, e.g. {@code 3.1} */
	public String version() {
		return version;
	}

	/** @return e.g. {@code application/vnd.oai.openapi+json;version=3.1} */
	public String jsonContentType() {
		return MEDIA_TYPE_JSON + ";version=" + version;
	}

	/** @return e.g. {@code application/vnd.oai.openapi;version=3.1} */
	public String yamlContentType() {
		return MEDIA_TYPE_YAML + ";version=" + version;
	}

	/**
	 * @param openapi the {@code openapi} member of a document
	 * @return whether the document is of this version ({@code 3.1}, {@code 3.1.0}, {@code 3.1.1}, …)
	 */
	public boolean matches(String openapi) {
		return openapi != null && (openapi.trim().equals(version) || openapi.trim().startsWith(version + "."));
	}

	/**
	 * @param openapi the {@code openapi} member of a document
	 * @return the version, {@code null} for none the model covers
	 */
	public static OasVersion of(String openapi) {
		for (OasVersion v : values()) {
			if (v.matches(openapi)) {
				return v;
			}
		}
		return null;
	}
}
