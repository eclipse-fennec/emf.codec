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
 * @since 1.0
 */
public interface JakartaRestConstants {

	/**
	 * Request-context property carrying the per-request codec options: a
	 * {@code Map<String, Object>} of option keys to typed values. The codec message body
	 * reader/writer reads it once, right before {@code load} or {@code save}, and lays it over the
	 * options derived from the endpoint's Java annotations ({@code @ResourceOption},
	 * {@code @EMFResourceOptions}, {@code @CodecConfig}). Keys in this map therefore win over the
	 * annotations, and - like every load/save option - over the codec's own configuration
	 * hierarchy below.
	 * <p>
	 * <b>This is the general per-request channel, not a client-only one</b> (issue #170). Two
	 * kinds of code fill it:
	 * <ul>
	 * <li>{@code ClientCodecOptionsFilter}, with the whitelisted options a client sent in the
	 * {@link #CODEC_OPTIONS_HEADER} header.</li>
	 * <li>Server-side code that knows an endpoint's options only at runtime - a
	 * {@code ContainerRequestFilter} of its own, or the resource method itself - for example a
	 * generic resource that serves many configured data sets, each with its own CSV dialect.
	 * Writing the property from there is supported and will stay supported.</li>
	 * </ul>
	 * <p>
	 * <b>Ordering.</b> The whiteboard runs request filters before the resource method;
	 * {@code ClientCodecOptionsFilter} carries no {@code @Priority}, so it runs at
	 * {@code Priorities.USER}. Whoever writes second must not overwrite the other side's keys:
	 * <ul>
	 * <li>{@code ClientCodecOptionsFilter} merges onto a map it finds, client keys winning.</li>
	 * <li>Server-side code running after the filter (the resource method, or a filter with a lower
	 * priority) should do the same in reverse: start from its own values and lay the existing map
	 * on top, so a whitelisted client override still wins. Server-side code running before the
	 * filter can simply set the property.</li>
	 * </ul>
	 * Client wins on a shared key in both orders - whitelisting a key <em>is</em> the decision to
	 * let clients set it. A server that wants a key to be final must not whitelist it.
	 * <p>
	 * The value must be a {@code Map<String, Object>}; anything else is ignored by the reader/writer.
	 */
	String CLIENT_CODEC_OPTIONS = "client.codec.options";

	/**
	 * Name of the request header carrying client-supplied codec options as comma-separated
	 * {@code key=value} pairs, e.g.
	 * {@code Codec-Options: codec.tabular.referenceMode=FLAT, codec.csv.dataTypeInSecondRow=false}.
	 */
	String CODEC_OPTIONS_HEADER = "Codec-Options";

}
