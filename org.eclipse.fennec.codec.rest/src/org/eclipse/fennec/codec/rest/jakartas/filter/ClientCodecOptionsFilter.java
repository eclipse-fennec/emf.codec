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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
 * <p>
 * <strong>Key spelling (issue #223).</strong> A codec option answers to two spellings - its bare
 * key and the same key under the {@code codec.} namespace - and the contributions are not uniform
 * about which one they publish. A header key is therefore looked up verbatim first and, failing
 * that, under its other spelling; a match stores the value under the <em>contributed</em> key,
 * which is the one the module's resolver reads. Normalising a spelling never widens the allow-list:
 * a key whitelisted in neither form is still dropped.
 * <p>
 * A dropped key is logged (one record per request, naming the keys) instead of vanishing. Silence
 * was the real defect behind #223: the request succeeded, the option was ignored, and the only
 * symptom was wrong output.
 *
 * @since 1.0
 */
@Component
@JakartarsExtension
@JakartarsName("ClientCodecOptionsFilter")
@JakartarsApplicationSelect("(|(emf=true)(" + JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "=.default))")
public class ClientCodecOptionsFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = Logger.getLogger(ClientCodecOptionsFilter.class.getName());

	/** The namespace a codec option key may carry; both spellings reach the option resolver. */
	private static final String CODEC_PREFIX = "codec.";

	/** How many dropped keys a single log record names, and how long each may be. */
	static final int MAX_REPORTED_KEYS = 10;
	static final int MAX_REPORTED_KEY_LENGTH = 64;

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
	 * Parses the {@code Codec-Options} header value(s) into a map of whitelisted, typed options,
	 * reporting every dropped key to the log. Package visible for testing.
	 */
	static Map<String, Object> parseClientOptions(Map<String, Class<?>> whitelist, List<String> headerValues) {
		List<String> dropped = new ArrayList<>();
		Map<String, Object> clientOptions = parseClientOptions(whitelist, headerValues, dropped::add);
		if (!dropped.isEmpty() && LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning(() -> describeDropped(dropped));
		}
		return clientOptions;
	}

	/**
	 * Parses the {@code Codec-Options} header value(s) into a map of whitelisted, typed options.
	 * Comma-separated {@code key=value} pairs; a key the allow-list does not hold in either
	 * spelling is dropped and handed to {@code onDropped} instead. Package visible for testing.
	 */
	static Map<String, Object> parseClientOptions(Map<String, Class<?>> whitelist, List<String> headerValues,
			Consumer<String> onDropped) {
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
				String whitelisted = resolveKey(whitelist, key);
				if (whitelisted != null) {
					clientOptions.put(whitelisted, CodecOptionValues.parse(raw, whitelist.get(whitelisted)));
				} else {
					// Never forwarded to the codec - but said out loud, see describeDropped.
					onDropped.accept(key);
				}
			}
		}
		return clientOptions;
	}

	/**
	 * Returns the allow-list entry a client key stands for, or {@code null} when no module offers
	 * it. The key as sent wins; only when it is unknown is its other {@code codec.} spelling tried,
	 * so a whitelist holding both spellings under different types stays unambiguous. Package
	 * visible for testing.
	 */
	static String resolveKey(Map<String, Class<?>> whitelist, String key) {
		if (whitelist.containsKey(key)) {
			return key;
		}
		String alternate = key.startsWith(CODEC_PREFIX) ? key.substring(CODEC_PREFIX.length())
				: CODEC_PREFIX + key;
		return whitelist.containsKey(alternate) ? alternate : null;
	}

	/**
	 * Builds the log record for the keys this request lost. The keys come from a remote caller, so
	 * the record is bounded in both directions: at most {@link #MAX_REPORTED_KEYS} keys, each cut
	 * to {@link #MAX_REPORTED_KEY_LENGTH} characters with its control characters replaced, so a
	 * crafted header cannot forge or flood log lines. Values are never logged. Package visible for
	 * testing.
	 */
	static String describeDropped(List<String> droppedKeys) {
		String named = droppedKeys.stream().limit(MAX_REPORTED_KEYS)
				.map(ClientCodecOptionsFilter::sanitize)
				.collect(Collectors.joining(", "));
		if (droppedKeys.size() > MAX_REPORTED_KEYS) {
			named = named + ", ... (" + (droppedKeys.size() - MAX_REPORTED_KEYS) + " more)";
		}
		return "Codec-Options: ignoring key(s) no module offers for client override: " + named
				+ ". Only a key contributed by a RestOverridableCodecOptions service can be set per"
				+ " request; the 'codec.' prefix is optional on either side.";
	}

	private static String sanitize(String key) {
		String safe = key.replaceAll("\\p{Cntrl}", "?");
		return safe.length() <= MAX_REPORTED_KEY_LENGTH ? safe
				: safe.substring(0, MAX_REPORTED_KEY_LENGTH) + "...";
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
