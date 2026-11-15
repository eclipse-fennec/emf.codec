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

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.eclipse.fennec.codec.util.UnsettableLists;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;

/**
 * Prepares a description for writing in the version of the resource (issue #303):
 * <ul>
 *   <li>a field or value the version does not know (the {@code since} of the oas model) is reported;</li>
 *   <li>the schemas are rewritten into the dialect of the version where that is unambiguous -
 *       {@code nullable} ⇄ a {@code "null"} type, the boolean ⇄ the numeric
 *       {@code exclusiveMinimum}/{@code exclusiveMaximum}, and for 3.0 {@code const} → {@code enum},
 *       {@code examples} → {@code example}; a keyword without a form in the version is reported and
 *       written as it is;</li>
 *   <li>an {@code openapi} member of another version is reported and written as the resource's
 *       version.</li>
 * </ul>
 * The changes are made in place and {@link #revert() reverted} after writing, so the model stays as
 * it was.
 */
final class OasVersionWriter {

	private static final String OAS = "http://eclipse.org/fennec/openapi";
	private static final String JSONSCHEMA = "http://eclipse.org/fennec/jsonschema/model";
	private static final JsonSchemaPackage JS = JsonSchemaPackage.eINSTANCE;

	/** The keywords of the OpenAPI 3.0 Schema Object. */
	private static final Set<String> OAS30_KEYWORDS = Set.of("title", "multipleOf", "maximum", "exclusiveMaximum",
			"minimum", "exclusiveMinimum", "maxLength", "minLength", "pattern", "maxItems", "minItems", "uniqueItems",
			"maxProperties", "minProperties", "required", "enum", "type", "allOf", "oneOf", "anyOf", "not", "items",
			"properties", "additionalProperties", "description", "format", "default", "nullable", "discriminator",
			"readOnly", "writeOnly", "xml", "externalDocs", "example", "deprecated", "$ref");

	/** The {@code openapi} member written for a version. */
	private static final Map<OasVersion, String> LATEST = Map.of(OasVersion.V3_0, "3.0.4", OasVersion.V3_1, "3.1.1",
			OasVersion.V3_2, "3.2.0");

	private final OasVersion target;
	private boolean rewrite;
	private final Map<String, Integer> problems = new LinkedHashMap<>();
	private final Deque<Runnable> undo = new ArrayDeque<>();

	private OasVersionWriter(OasVersion target) {
		this.target = target;
	}

	/**
	 * @param api the description to write
	 * @param target the version of the resource
	 * @return the prepared writer: its {@link #problems()}, and {@link #revert()} after writing
	 */
	static OasVersionWriter prepare(OpenAPI api, OasVersion target) {
		OasVersionWriter writer = new OasVersionWriter(target);
		writer.run(api);
		return writer;
	}

	/** @return what the version cannot express, each with the number of places */
	List<String> problems() {
		List<String> messages = new ArrayList<>();
		problems.forEach((message, count) -> messages.add(count == 1 ? message : message + " (" + count + " times)"));
		return messages;
	}

	/** Restores the model as it was before {@link #prepare(OpenAPI, OasVersion)}. */
	void revert() {
		while (!undo.isEmpty()) {
			undo.pop().run();
		}
	}

	private void run(OpenAPI api) {
		// the schemas are rewritten only into another version: in its own version a description is
		// written as it was read, also where it does not follow its dialect
		OasVersion source = OasVersion.of(api.getOpenapi());
		rewrite = source != null && source != target;
		if (api.getOpenapi() != null && !target.matches(api.getOpenapi())) {
			report("the document is OpenAPI " + api.getOpenapi() + ", written as OpenAPI " + LATEST.get(target));
			set(api, OasPackage.Literals.OPEN_API__OPENAPI, LATEST.get(target));
		}
		checkOas(api);
		for (TreeIterator<EObject> it = api.eAllContents(); it.hasNext();) {
			EObject object = it.next();
			if (object instanceof EPackage) {
				it.prune(); // the derived Ecore
			} else if (object instanceof Schema schema) {
				if (!rewrite) {
					checkSchemaPart(schema);
				} else if (target == OasVersion.V3_0) {
					toOas30(schema);
				} else {
					toJsonSchema2020(schema);
				}
			} else if (object.eClass().getEPackage() == JS) {
				checkSchemaPart(object); // discriminator, xml
			} else {
				checkOas(object);
			}
		}
	}

	// ------------------------------------------------------------------ fields of the oas model

	private void checkOas(EObject object) {
		for (EStructuralFeature feature : object.eClass().getEAllStructuralFeatures()) {
			if (feature.isTransient() || feature.isDerived() || !object.eIsSet(feature)) {
				continue;
			}
			if (!known(EcoreUtil.getAnnotation(feature, OAS, "since"))) {
				report(object.eClass().getName() + "." + key(feature) + " is not part of OpenAPI " + target.version());
			} else if (feature instanceof EAttribute attribute && attribute.getEAttributeType() instanceof EEnum eEnum) {
				Object value = object.eGet(feature);
				for (Object v : feature.isMany() ? (List<?>) value : List.of(value)) {
					EEnumLiteral literal = v instanceof Enumerator e ? eEnum.getEEnumLiteral(e.getValue()) : null;
					if (literal != null && !known(EcoreUtil.getAnnotation(literal, OAS, "since"))) {
						report(object.eClass().getName() + "." + key(feature) + " '" + literal.getLiteral()
								+ "' is not part of OpenAPI " + target.version());
					}
				}
			}
		}
	}

	/** @return whether a field {@code since} that version is part of the target version */
	private boolean known(String since) {
		return since == null || since.compareTo(target.version()) <= 0;
	}

	// ------------------------------------------------------------------ schemas

	private void toOas30(Schema schema) {
		if (schema.getBooleanSchema() != null) {
			if (schema.eContainingFeature() != JS.getSchema_AdditionalProperties()) {
				report("a boolean schema has no form in OpenAPI 3.0"); // additionalProperties: true|false has
			}
			return;
		}
		List<SimpleType> types = new ArrayList<>(schema.getType());
		if (schema.isTypeAsList() || types.size() > 1) {
			boolean nullable = types.remove(SimpleType.NULL);
			if (types.size() <= 1) {
				set(schema, JS.getSchema_Type(), types);
				set(schema, JS.getSchema_TypeAsList(), Boolean.FALSE);
				if (nullable) {
					set(schema, JS.getSchema_Nullable(), Boolean.TRUE);
				}
			} else {
				report("'type' with several types has no form in OpenAPI 3.0");
			}
		}
		exclusiveTo30(schema, JS.getSchema_ExclusiveMinimum(), JS.getSchema_Minimum(), JS.getSchema_ExclusiveMinimumFlag(),
				"exclusiveMinimum");
		exclusiveTo30(schema, JS.getSchema_ExclusiveMaximum(), JS.getSchema_Maximum(), JS.getSchema_ExclusiveMaximumFlag(),
				"exclusiveMaximum");
		if (schema.eIsSet(JS.getSchema_Const())) {
			if (schema.eIsSet(JS.getSchema_Enum())) {
				report("'const' next to 'enum' has no form in OpenAPI 3.0");
			} else {
				set(schema, JS.getSchema_Enum(), List.of(schema.getConst()));
				unset(schema, JS.getSchema_Const());
			}
		}
		if (schema.eIsSet(JS.getSchema_Examples()) && !schema.eIsSet(JS.getSchema_Example())) {
			if (schema.getExamples().size() > 1) {
				report("of several 'examples' OpenAPI 3.0 keeps the first as 'example'");
			}
			if (!schema.getExamples().isEmpty()) {
				set(schema, JS.getSchema_Example(), schema.getExamples().get(0));
			}
			unset(schema, JS.getSchema_Examples());
		}
		for (EStructuralFeature feature : schema.eClass().getEAllStructuralFeatures()) {
			if (feature.isTransient() || !schema.eIsSet(feature) || feature == JS.getSchema_TypeAsList()
					|| feature == JS.getSchema_Keywords() || feature == JS.getSchema_ExclusiveMinimum()
					|| feature == JS.getSchema_ExclusiveMaximum() || feature == JS.getSchema_Const()
					|| feature == JS.getSchema_Examples()) {
				continue; // not a keyword, or reported above
			}
			if (!OAS30_KEYWORDS.contains(key(feature))) {
				report("'" + key(feature) + "' is not a keyword of the OpenAPI 3.0 Schema Object");
			}
		}
	}

	private void exclusiveTo30(Schema schema, EStructuralFeature exclusive, EStructuralFeature bound,
			EStructuralFeature flag, String keyword) {
		if (!schema.eIsSet(exclusive)) {
			return;
		}
		if (schema.eIsSet(bound)) {
			report("numeric '" + keyword + "' next to the bound has no form in OpenAPI 3.0");
			return;
		}
		set(schema, bound, schema.eGet(exclusive));
		set(schema, flag, Boolean.TRUE);
		unset(schema, exclusive);
	}

	private void toJsonSchema2020(Schema schema) {
		if (schema.eIsSet(JS.getSchema_Nullable())) {
			// 3.0: nullable adds null to an explicit type, and has no effect without one
			if (Boolean.TRUE.equals(schema.getNullable()) && schema.getType().size() == 1
					&& !schema.getType().contains(SimpleType.NULL)) {
				List<SimpleType> types = new ArrayList<>(schema.getType());
				types.add(SimpleType.NULL);
				set(schema, JS.getSchema_Type(), types);
				set(schema, JS.getSchema_TypeAsList(), Boolean.TRUE);
			}
			unset(schema, JS.getSchema_Nullable());
		}
		exclusiveTo2020(schema, JS.getSchema_ExclusiveMinimumFlag(), JS.getSchema_Minimum(), JS.getSchema_ExclusiveMinimum());
		exclusiveTo2020(schema, JS.getSchema_ExclusiveMaximumFlag(), JS.getSchema_Maximum(), JS.getSchema_ExclusiveMaximum());
		checkSchemaPart(schema);
	}

	private void exclusiveTo2020(Schema schema, EStructuralFeature flag, EStructuralFeature bound,
			EStructuralFeature exclusive) {
		if (!schema.eIsSet(flag)) {
			return;
		}
		if (Boolean.TRUE.equals(schema.eGet(flag)) && schema.eIsSet(bound) && !schema.eIsSet(exclusive)) {
			set(schema, exclusive, (BigDecimal) schema.eGet(bound));
			unset(schema, bound);
		}
		unset(schema, flag);
	}

	/**
	 * Keywords of OpenAPI 3.2 below 3.2; after a rewrite into 2020-12 also the draft-04 and 3.0
	 * keywords left over.
	 */
	private void checkSchemaPart(EObject object) {
		for (EStructuralFeature feature : object.eClass().getEAllStructuralFeatures()) {
			if (feature.isTransient() || !object.eIsSet(feature)) {
				continue;
			}
			String since = EcoreUtil.getAnnotation(feature, JSONSCHEMA, "since");
			String until = EcoreUtil.getAnnotation(feature, JSONSCHEMA, "until");
			if (since != null && since.startsWith("oas-") && !known(since.substring(4))) {
				report("'" + key(feature) + "' of " + object.eClass().getName() + " is not part of OpenAPI " + target.version());
			} else if (rewrite && target != OasVersion.V3_0 && ("draft-04".equals(until) || "oas-3.0".equals(until))) {
				report("'" + key(feature) + "' is not a keyword of the OpenAPI " + target.version() + " Schema Object");
			}
		}
	}

	// ------------------------------------------------------------------ helpers

	private void report(String message) {
		problems.merge(message, 1, Integer::sum);
	}

	private static String key(EStructuralFeature feature) {
		String key = EcoreUtil.getAnnotation(feature, AnnotationSources.CODEC, "key");
		return key != null ? key : feature.getName();
	}

	private void set(EObject object, EStructuralFeature feature, Object value) {
		remember(object, feature);
		object.eSet(feature, value);
	}

	private void unset(EObject object, EStructuralFeature feature) {
		remember(object, feature);
		object.eUnset(feature);
	}

	private void remember(EObject object, EStructuralFeature feature) {
		boolean wasSet = object.eIsSet(feature);
		Object old = feature.isMany() ? new ArrayList<>((List<?>) object.eGet(feature)) : object.eGet(feature);
		undo.push(() -> {
			if (wasSet) {
				object.eSet(feature, old);
				UnsettableLists.markSetIfEmpty(object, feature);
			} else {
				object.eUnset(feature);
			}
		});
	}
}
