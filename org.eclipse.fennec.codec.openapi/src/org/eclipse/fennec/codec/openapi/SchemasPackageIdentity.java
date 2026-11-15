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
package org.eclipse.fennec.codec.openapi;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.openapi.constants.CodecOpenApiOptions;
import org.eclipse.fennec.model.openapi.Info;
import org.eclipse.fennec.model.openapi.OpenAPI;

/**
 * Gives the {@code EPackage} built from {@code components/schemas} an identity (issue #311): an
 * nsURI, a name and a prefix, so that the schema classes can be referred to from outside the
 * description (e.g. a service contract saved as XMI).
 * <p>
 * The nsURI is {@code urn:openapi:<title>:<version>} from {@code info}, reduced to URI-safe
 * characters, unless the load option {@link CodecOpenApiOptions#OPTION_SCHEMAS_NS_URI} names one.
 * It does not depend on where the document was loaded from: the same API read from its URL, a
 * mirror or a local copy, as JSON or YAML, gets the same nsURI. Values already set are kept.
 * </p>
 */
final class SchemasPackageIdentity {

	private static final String DEFAULT_NAME = "openapi";
	private static final Pattern WORD = Pattern.compile("[A-Za-z0-9]+");

	private SchemasPackageIdentity() {
	}

	static void apply(OpenAPI api, Map<?, ?> options) {
		if (api.getComponents() == null || api.getComponents().getSchemasPackage() == null) {
			return;
		}
		EPackage pkg = api.getComponents().getSchemasPackage();
		Info info = api.getInfo();
		String title = info == null ? null : info.getTitle();
		String version = info == null ? null : info.getVersion();
		Object option = options == null ? null : options.get(CodecOpenApiOptions.OPTION_SCHEMAS_NS_URI);
		if (option != null && !option.toString().isBlank()) {
			pkg.setNsURI(option.toString().trim());
		} else if (isBlank(pkg.getNsURI())) {
			pkg.setNsURI(nsURI(title, version));
		}
		if (isBlank(pkg.getName())) {
			pkg.setName(name(title));
		}
		if (isBlank(pkg.getNsPrefix())) {
			pkg.setNsPrefix(pkg.getName());
		}
	}

	/** {@code urn:openapi:<title>[:<version>]}, both reduced to {@code [a-z0-9.-]}. */
	static String nsURI(String title, String version) {
		String t = slug(title);
		String v = slug(version);
		return "urn:openapi:" + (t.isEmpty() ? DEFAULT_NAME : t) + (v.isEmpty() ? "" : ":" + v);
	}

	/** A lower camel case identifier from the words of the title, e.g. {@code petShopApi}. */
	static String name(String title) {
		Matcher words = WORD.matcher(ascii(title));
		StringBuilder name = new StringBuilder();
		while (words.find()) {
			String word = words.group().toLowerCase(Locale.ROOT);
			name.append(name.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1));
		}
		if (name.isEmpty()) {
			return DEFAULT_NAME;
		}
		return Character.isDigit(name.charAt(0)) ? "_" + name : name.toString();
	}

	private static String slug(String value) {
		return ascii(value).toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9.]+", "-")
				.replaceAll("^-+|-+$", "");
	}

	/** Strips diacritics ({@code Ä} → {@code A}); other non-ASCII characters are left to the callers. */
	private static String ascii(String value) {
		if (value == null) {
			return "";
		}
		return Normalizer.normalize(value.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
