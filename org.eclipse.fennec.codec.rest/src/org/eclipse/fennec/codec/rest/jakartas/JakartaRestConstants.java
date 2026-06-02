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
package org.eclipse.fennec.codec.rest.jakartas;

/**
 * Request-context property keys and header names shared between the codec's JAX-RS extensions.
 *
 * @author ilenia
 * @since May 19, 2026
 */
public interface JakartaRestConstants {

	/**
	 * Request-context property under which {@code ClientCodecOptionsFilter} stores the whitelisted,
	 * parsed codec options supplied by the client (a {@code Map<String, Object>}). Read by the codec
	 * message-body reader/writer and merged into the load/save options.
	 */
	String CLIENT_CODEC_OPTIONS = "client.codec.options";

	/**
	 * Name of the request header carrying client-supplied codec options as comma-separated
	 * {@code key=value} pairs, e.g.
	 * {@code Codec-Options: codec.tabular.referenceMode=FLAT, codec.csv.dataTypeInSecondRow=false}.
	 */
	String CODEC_OPTIONS_HEADER = "Codec-Options";

}
