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
package org.eclipse.fennec.codec.rest.jakartas.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.fennec.codec.config.RestOverridableCodecOptions;
import org.eclipse.fennec.codec.rest.common.CodecOptionValues;
import org.eclipse.fennec.codec.rest.jakartas.JakartaRestConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsApplicationSelect;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsExtension;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsName;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

/**
 * Reads client-supplied codec options from the {@code Codec-Options} request header, keeps only
 * the keys whitelisted by the registered {@link RestOverridableCodecOptions} services, parses each
 * value to its declared type, and stores the result as the
 * {@link JakartaRestConstants#CLIENT_CODEC_OPTIONS} request-context property. The codec message-body
 * reader/writer then merges it into the EMF load/save options.
 * <p>
 * Secure by default: with no {@link RestOverridableCodecOptions} services (empty whitelist) the
 * header is ignored entirely.
 * <p>
 * The request property is a shared channel (issue #170): server-side code may fill it as well,
 * see {@link JakartaRestConstants#CLIENT_CODEC_OPTIONS}. This filter therefore <em>merges</em>
 * onto a map already present rather than replacing it, with the client's whitelisted keys
 * winning on a collision - whitelisting a key is the decision to let the client set it.
 * <p>
 * Attaches to the same applications as the codec's message body handlers ({@code emf=true} or
 * {@code .default}), so the header works by default wherever the handlers do. The selector is a
 * component property and can be widened through Config Admin.
 *
 * @since 1.0
 */
@Component
@JakartarsExtension
@JakartarsName("ClientCodecOptionsFilter")
@JakartarsApplicationSelect("(|(emf=true)(" + JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "=.default))")
public class ClientCodecOptionsFilter implements ContainerRequestFilter {

	/** All modules' contributions; the union of their keys forms the allow-list. */
	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private volatile List<RestOverridableCodecOptions> contributions;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Map<String, Class<?>> whitelist = collectWhitelist();
		if (whitelist.isEmpty()) {
			return;
		}

		List<String> headerValues = requestContext.getHeaders().get(JakartaRestConstants.CODEC_OPTIONS_HEADER);
		Map<String, Object> clientOptions = parseClientOptions(whitelist, headerValues);

		if (!clientOptions.isEmpty()) {
			Object existing = requestContext.getProperty(JakartaRestConstants.CLIENT_CODEC_OPTIONS);
			requestContext.setProperty(JakartaRestConstants.CLIENT_CODEC_OPTIONS,
					mergeClientOptions(existing, clientOptions));
		}
	}

	/**
	 * Lays the client's options over whatever the request property already holds (issue #170).
	 * <p>
	 * A server-side filter running earlier may have filled the property; its keys survive, and
	 * the client's whitelisted keys win where both set the same one. A value that is not a map
	 * is not a channel and is replaced. The earlier map is left untouched. Package visible for
	 * testing.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	static Map<String, Object> mergeClientOptions(Object existing, Map<String, Object> clientOptions) {
		if (!(existing instanceof Map)) {
			return clientOptions;
		}
		Map<String, Object> merged = new HashMap<>((Map<String, Object>) existing);
		merged.putAll(clientOptions);
		return merged;
	}

	/**
	 * Parses the {@code Codec-Options} header value(s) into a map of whitelisted, typed options.
	 * Comma-separated {@code key=value} pairs; keys not in {@code whitelist} are ignored. Package
	 * visible for testing.
	 */
	static Map<String, Object> parseClientOptions(Map<String, Class<?>> whitelist, List<String> headerValues) {
		Map<String, Object> clientOptions = new HashMap<>();
		if (whitelist.isEmpty() || headerValues == null) {
			return clientOptions;
		}
		for (String headerValue : headerValues) {
			if (headerValue == null) {
				continue;
			}
			for (String pair : headerValue.split(",")) {
				int eq = pair.indexOf('=');
				if (eq <= 0) {
					continue;
				}
				String key = pair.substring(0, eq).trim();
				String raw = pair.substring(eq + 1).trim();
				Class<?> type = whitelist.get(key);
				if (type != null) {
					clientOptions.put(key, CodecOptionValues.parse(raw, type));
				}
				// Non-whitelisted keys are silently ignored — never forwarded to the codec.
			}
		}
		return clientOptions;
	}

	private Map<String, Class<?>> collectWhitelist() {
		Map<String, Class<?>> whitelist = new HashMap<>();
		List<RestOverridableCodecOptions> current = contributions;
		if (current != null) {
			for (RestOverridableCodecOptions contribution : current) {
				Map<String, Class<?>> keys = contribution.overridableKeys();
				if (keys != null) {
					whitelist.putAll(keys);
				}
			}
		}
		return whitelist;
	}
}
