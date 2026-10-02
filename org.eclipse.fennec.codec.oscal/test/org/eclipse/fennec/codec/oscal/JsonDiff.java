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
package org.eclipse.fennec.codec.oscal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import tools.jackson.databind.JsonNode;

/**
 * Semantic comparison of two JSON trees, with a path for every difference.
 * <p>
 * Member order is ignored and numbers compare by value. The one normalization is for date-times
 * with a zone offset: {@code +00:00} and {@code Z} denote the same instant, and the model keeps
 * the instant (an {@code XMLGregorianCalendar}), not its spelling. They count as equal only if
 * instant and fractional seconds agree, so a lost digit still shows. Everything else is compared
 * as is.
 * </p>
 */
final class JsonDiff {

	private static final Pattern DATE_TIME = Pattern.compile(
			"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?(Z|[+-]\\d{2}:\\d{2})");

	private JsonDiff() {
	}

	/**
	 * Returns the differences between {@code expected} and {@code actual}, empty if they are
	 * semantically equal.
	 */
	static List<String> diff(JsonNode expected, JsonNode actual) {
		List<String> out = new ArrayList<>();
		diff("", expected, actual, out);
		return out;
	}

	private static void diff(String path, JsonNode a, JsonNode b, List<String> out) {
		if (b == null || b.isMissingNode()) {
			out.add("MISSING " + path + " = " + abbrev(a));
			return;
		}
		if (a.isObject() && b.isObject()) {
			for (String k : a.propertyNames()) {
				diff(path + "/" + k, a.get(k), b.get(k), out);
			}
			for (String k : b.propertyNames()) {
				if (!a.has(k)) {
					out.add("EXTRA " + path + "/" + k + " = " + abbrev(b.get(k)));
				}
			}
		} else if (a.isArray() && b.isArray()) {
			if (a.size() != b.size()) {
				out.add("SIZE " + path + " " + a.size() + " vs " + b.size());
			}
			for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
				diff(path + "[" + i + "]", a.get(i), b.get(i), out);
			}
		} else if (a.isNumber() && b.isNumber()) {
			if (a.decimalValue().compareTo(b.decimalValue()) != 0) {
				out.add("NUMBER " + path + " " + a + " vs " + b);
			}
		} else if (a.isString() && b.isString() && sameDateTime(a.asString(), b.asString())) {
			// same instant, same precision
		} else if (!a.equals(b)) {
			out.add("VALUE " + path + " " + abbrev(a) + " vs " + abbrev(b));
		}
	}

	/** {@code true} if both are date-times with zone, denoting the same instant with the same fraction. */
	static boolean sameDateTime(String a, String b) {
		if (!DATE_TIME.matcher(a).matches() || !DATE_TIME.matcher(b).matches()) {
			return false;
		}
		XMLGregorianCalendar ca = DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar(a);
		XMLGregorianCalendar cb = DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar(b);
		return ca.toGregorianCalendar().getTimeInMillis() == cb.toGregorianCalendar().getTimeInMillis()
				&& fraction(a).equals(fraction(b));
	}

	private static String fraction(String dateTime) {
		int dot = dateTime.indexOf('.');
		if (dot < 0) {
			return "";
		}
		int end = dot + 1;
		while (end < dateTime.length() && Character.isDigit(dateTime.charAt(end))) {
			end++;
		}
		return dateTime.substring(dot, end);
	}

	static String abbrev(JsonNode n) {
		String s = String.valueOf(n);
		return s.length() > 160 ? s.substring(0, 160) + "…" : s;
	}
}
