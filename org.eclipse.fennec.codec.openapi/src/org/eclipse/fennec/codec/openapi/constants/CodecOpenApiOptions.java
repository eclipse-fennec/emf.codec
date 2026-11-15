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
package org.eclipse.fennec.codec.openapi.constants;

/**
 * Load and save options of the OpenAPI codec.
 */
public final class CodecOpenApiOptions {

	/**
	 * Load option: the nsURI of the {@code EPackage} built from {@code components/schemas} (issue
	 * #311). Without it the nsURI is derived from {@code info.title} and {@code info.version}:
	 * {@code urn:openapi:<title>:<version>}. Value: {@code String}.
	 */
	public static final String OPTION_SCHEMAS_NS_URI = "codec.openapi.schemasNsURI";

	private CodecOpenApiOptions() {
	}
}
