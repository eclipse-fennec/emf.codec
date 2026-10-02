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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

import gov.nist.csrc.ns.oscal.OSCALPackage;
import tools.jackson.databind.JsonNode;

/**
 * Checks a loaded model against the JSON it was read from, without the codec's writer.
 * <p>
 * The round trip alone cannot tell whether a value landed in the right feature: a reader and a
 * writer that agree on the same wrong feature still produce the original JSON. This oracle walks
 * the JSON and the EObject tree side by side and resolves every JSON member to its feature on its
 * own, from the model annotations (codec {@code key}, else the ExtendedMetaData name). It reports
 * <ul>
 * <li>JSON members without a feature,</li>
 * <li>values that differ from the feature value, compared by type (strings exactly, date-times as
 * instants with their full fraction, numbers by value, base64 by its bytes),</li>
 * <li>features set in the model that the JSON does not have.</li>
 * </ul>
 * </p>
 */
final class ModelJsonOracle {

	private static final String CODEC_ANNOTATION = "http://eclipse.org/fennec/codec";

	private final Set<EStructuralFeature> ignored;
	private final List<String> findings = new ArrayList<>();
	private int checkedValues;

	private ModelJsonOracle() {
		OSCALPackage pkg = OSCALPackage.eINSTANCE;
		ignored = Set.of(pkg.getDocumentRoot_Mixed(), pkg.getDocumentRoot_XMLNSPrefixMap(),
				pkg.getDocumentRoot_XSISchemaLocation());
	}

	/** The result of a check: the findings and how many leaf values were compared. */
	record Result(List<String> findings, int checkedValues) {
	}

	static Result check(JsonNode json, EObject root) {
		ModelJsonOracle oracle = new ModelJsonOracle();
		oracle.compareObject("", json, root);
		return new Result(oracle.findings, oracle.checkedValues);
	}

	/** The JSON member name of a feature, resolved from the model annotations alone. */
	static String jsonName(EStructuralFeature feature) {
		String key = EcoreUtil.getAnnotation(feature, CODEC_ANNOTATION, "key");
		if (key != null) {
			return key;
		}
		return ExtendedMetaData.INSTANCE.getName(feature);
	}

	private EStructuralFeature featureFor(EClass eClass, String name) {
		for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
			if (!ignored.contains(feature) && name.equals(jsonName(feature))) {
				return feature;
			}
		}
		return null;
	}

	private void compareObject(String path, JsonNode json, EObject eObject) {
		if (!json.isObject()) {
			findings.add("NOT AN OBJECT " + path + " for " + eObject.eClass().getName());
			return;
		}
		EClass eClass = eObject.eClass();
		for (String name : json.propertyNames()) {
			String memberPath = path + "/" + name;
			EStructuralFeature feature = featureFor(eClass, name);
			if (feature == null) {
				findings.add("NO FEATURE " + memberPath + " in " + eClass.getName());
				continue;
			}
			JsonNode value = json.get(name);
			if (feature instanceof EReference reference) {
				compareReference(memberPath, value, eObject, reference);
			} else {
				compareAttribute(memberPath, value, eObject, (EAttribute) feature);
			}
		}
		for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
			if (!ignored.contains(feature) && eObject.eIsSet(feature) && !json.has(jsonName(feature))) {
				findings.add("NOT IN JSON " + path + "/" + jsonName(feature) + " (" + eClass.getName() + "."
						+ feature.getName() + " = " + eObject.eGet(feature) + ")");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void compareReference(String path, JsonNode json, EObject owner, EReference reference) {
		if (!reference.isContainment()) {
			findings.add("NON-CONTAINMENT " + path);
			return;
		}
		if (reference.isMany()) {
			List<EObject> values = (List<EObject>) owner.eGet(reference);
			// a single object where OSCAL wants an array is read as an array of one
			List<JsonNode> items = json.isArray() ? json.valueStream().toList() : List.of(json);
			if (items.size() != values.size()) {
				findings.add("SIZE " + path + " json " + items.size() + " model " + values.size());
			}
			for (int i = 0; i < Math.min(items.size(), values.size()); i++) {
				compareObject(path + "[" + i + "]", items.get(i), values.get(i));
			}
		} else {
			EObject value = (EObject) owner.eGet(reference);
			if (value == null) {
				findings.add("NULL " + path);
			} else {
				compareObject(path, json, value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void compareAttribute(String path, JsonNode json, EObject owner, EAttribute attribute) {
		if (attribute.isMany()) {
			List<Object> values = (List<Object>) owner.eGet(attribute);
			if (!json.isArray()) {
				findings.add("NOT AN ARRAY " + path);
				return;
			}
			if (json.size() != values.size()) {
				findings.add("SIZE " + path + " json " + json.size() + " model " + values.size());
			}
			for (int i = 0; i < Math.min(json.size(), values.size()); i++) {
				compareValue(path + "[" + i + "]", json.get(i), values.get(i));
			}
		} else {
			compareValue(path, json, owner.eGet(attribute));
		}
	}

	private void compareValue(String path, JsonNode json, Object value) {
		checkedValues++;
		if (value == null) {
			findings.add("NULL " + path + " json " + JsonDiff.abbrev(json));
			return;
		}
		boolean equal;
		if (value instanceof String s) {
			equal = json.isString() && s.equals(json.asString());
		} else if (value instanceof XMLGregorianCalendar calendar) {
			equal = json.isString() && sameCalendar(json.asString(), calendar);
		} else if (value instanceof BigDecimal d) {
			equal = json.isNumber() && json.decimalValue().compareTo(d) == 0;
		} else if (value instanceof BigInteger i) {
			equal = json.isNumber() && json.bigIntegerValue().equals(i);
		} else if (value instanceof Boolean b) {
			equal = json.isBoolean() && json.booleanValue() == b;
		} else if (value instanceof byte[] bytes) {
			equal = json.isString() && Arrays.equals(Base64.getDecoder().decode(json.asString()), bytes);
		} else {
			equal = String.valueOf(value).equals(json.isString() ? json.asString() : json.toString());
		}
		if (!equal) {
			findings.add("VALUE " + path + " json " + JsonDiff.abbrev(json) + " model " + value + " ("
					+ value.getClass().getSimpleName() + ")");
		}
	}

	/** Same instant, same zone offset and the same fractional seconds, digit by digit. */
	private static boolean sameCalendar(String text, XMLGregorianCalendar calendar) {
		XMLGregorianCalendar parsed = DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar(text);
		// equals() of the JDK implementation casts the other calendar to its own class, EMF has
		// another one; the instant is compared in milliseconds, the fraction below digit by digit
		if (parsed.toGregorianCalendar().getTimeInMillis() != calendar.toGregorianCalendar().getTimeInMillis()
				|| parsed.getTimezone() != calendar.getTimezone()) {
			return false;
		}
		BigDecimal a = parsed.getFractionalSecond();
		BigDecimal b = calendar.getFractionalSecond();
		return a == null ? b == null : b != null && a.toPlainString().equals(b.toPlainString());
	}
}
