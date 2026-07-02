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
package org.eclipse.fennec.codec.jsonschema.v2.converter.ocl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaConversionDiagnostic;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Compiles JSON Schema assertion keywords (Phase 2 of the JSON Schema
 * Validation to EMF/OCL Mapping Guide) and the {@code format} keyword
 * (Phase 4, limited to {@code uuid}/{@code email}) into OCL invariants,
 * attached to the generated {@code EClass}es using EMF's standard
 * validation-delegate annotation convention:
 * <ul>
 *   <li>{@code EPackage} annotation, source {@link EcorePackage#eNS_URI},
 *       detail {@code validationDelegates}</li>
 *   <li>{@code EClass} annotation, same source, detail {@code constraints}
 *       (space-separated invariant names)</li>
 *   <li>{@code EClass} annotation, source = the configured delegate URI,
 *       with {@code invariantName -> oclExpression} details</li>
 * </ul>
 * <p>
 * This class only reads the {@link AnnotationSources#JSONSCHEMA} details
 * already written by {@code JsonSchemaToEPackageConverter} — it has no
 * dependency on any OCL engine. Whichever {@code EValidator.ValidationDelegate}
 * is registered for the configured delegate URI at runtime does the actual
 * evaluation.
 * </p>
 * <p>
 * {@code minItems}/{@code maxItems} are not handled here: they are already
 * enforced structurally via {@code lowerBound}/{@code upperBound}.
 * Discriminator invariants (also part of Phase 4) are intentionally not
 * generated — see {@code docs/OCL-Constraint-Generation-Implementation-Plan.md}
 * for why: this converter's discriminated-union patterns are purely
 * structural (the concrete {@code EClass} itself is the type tag) and never
 * retain a literal discriminator field to assert against.
 * </p>
 *
 * @since 2026
 */
public class JsonSchemaOclConstraintGenerator {

	private static final Set<String> INTEGER_TYPE_NAMES = Set.of(
			"EInt", "EIntegerObject", "ELong", "ELongObject", "EShort", "EShortObject", "EBigInteger");

	private static final Map<String, String> FORMAT_PATTERNS = Map.of(
			"uuid", "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
			"email", "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private final EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;

	/**
	 * Generates OCL invariants for {@code root} (an {@code EPackage} or a
	 * standalone {@code EClass}) and attaches them using the EMF
	 * validation-delegate annotation convention.
	 *
	 * @param root the converted model root, as produced by
	 *             {@code JsonSchemaToEPackageConverter}
	 * @param delegateUri the validation-delegate URI to register invariants
	 *                     under
	 * @return diagnostics for keywords that could not be compiled into OCL
	 *         (never {@code null}, may be empty)
	 */
	public List<JsonSchemaConversionDiagnostic> generate(EObject root, String delegateUri) {
		List<JsonSchemaConversionDiagnostic> diagnostics = new ArrayList<>();
		if (root instanceof EPackage ePackage) {
			for (EClassifier classifier : ePackage.getEClassifiers()) {
				if (classifier instanceof EClass eClass) {
					processClass(eClass, delegateUri, diagnostics);
				}
			}
		} else if (root instanceof EClass eClass) {
			processClass(eClass, delegateUri, diagnostics);
		}
		return diagnostics;
	}

	private void processClass(EClass eClass, String delegateUri, List<JsonSchemaConversionDiagnostic> diagnostics) {
		Map<String, String> invariants = new LinkedHashMap<>();
		for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
			EAnnotation jsonSchemaAnnotation = feature.getEAnnotation(AnnotationSources.JSONSCHEMA);
			if (jsonSchemaAnnotation == null) {
				continue;
			}
			collectFeatureInvariants(feature, jsonSchemaAnnotation.getDetails().map(), invariants, diagnostics, eClass.getName());
		}
		if (!invariants.isEmpty()) {
			applyInvariants(eClass, invariants, delegateUri, diagnostics);
		}
	}

	private void collectFeatureInvariants(EStructuralFeature feature, Map<String, String> details,
			Map<String, String> invariants, List<JsonSchemaConversionDiagnostic> diagnostics, String location) {
		String featureName = feature.getName();

		putIfPresent(details, "minLength", raw ->
				invariants.put(featureName + "_minLength", "self." + featureName + ".size() >= " + raw));
		putIfPresent(details, "maxLength", raw ->
				invariants.put(featureName + "_maxLength", "self." + featureName + ".size() <= " + raw));
		putIfPresent(details, "minimum", raw ->
				invariants.put(featureName + "_minimum", "self." + featureName + " >= " + raw));
		putIfPresent(details, "maximum", raw ->
				invariants.put(featureName + "_maximum", "self." + featureName + " <= " + raw));

		if (details.containsKey("pattern")) {
			String raw = unquote(details.get("pattern"));
			invariants.put(featureName + "_pattern",
					"self." + featureName + ".matches(" + oclStringLiteral(raw) + ")");
		}

		handleExclusiveBound(details, "exclusiveMinimum", ">", featureName, invariants, diagnostics, location);
		handleExclusiveBound(details, "exclusiveMaximum", "<", featureName, invariants, diagnostics, location);

		if (details.containsKey("multipleOf")) {
			String raw = details.get("multipleOf");
			if (isIntegerType(feature)) {
				invariants.put(featureName + "_multipleOf", "self." + featureName + ".mod(" + raw + ") = 0");
			} else {
				diagnostics.add(JsonSchemaConversionDiagnostic.oclGenerationSkipped("multipleOf", location,
						"'" + featureName + "' is not an integer-typed feature; OCL mod() is only defined for integers"));
			}
		}

		if (details.containsKey("uniqueItems")) {
			handleUniqueItems(feature, details.get("uniqueItems"), invariants, diagnostics, location);
		}

		if (details.containsKey("format")) {
			String format = unquote(details.get("format"));
			String pattern = FORMAT_PATTERNS.get(format);
			if (pattern != null) {
				invariants.put(featureName + "_format",
						"self." + featureName + ".matches(" + oclStringLiteral(pattern) + ")");
			}
		}
	}

	private void handleExclusiveBound(Map<String, String> details, String keyword, String operator,
			String featureName, Map<String, String> invariants, List<JsonSchemaConversionDiagnostic> diagnostics,
			String location) {
		if (!details.containsKey(keyword)) {
			return;
		}
		String raw = details.get(keyword);
		if (isNumericLiteral(raw)) {
			invariants.put(featureName + "_" + keyword, "self." + featureName + " " + operator + " " + raw);
		} else {
			diagnostics.add(JsonSchemaConversionDiagnostic.oclGenerationSkipped(keyword, location,
					"legacy Draft-04 boolean form is not supported; only the Draft 2020-12 numeric form of '"
							+ keyword + "' can be compiled to OCL"));
		}
	}

	private void handleUniqueItems(EStructuralFeature feature, String raw, Map<String, String> invariants,
			List<JsonSchemaConversionDiagnostic> diagnostics, String location) {
		if (!Boolean.parseBoolean(raw)) {
			return;
		}
		String featureName = feature.getName();
		if (feature.isMany()) {
			invariants.put(featureName + "_uniqueItems", "self." + featureName + "->isUnique(e | e)");
		} else {
			diagnostics.add(JsonSchemaConversionDiagnostic.oclGenerationSkipped("uniqueItems", location,
					"'" + featureName + "' is not a multi-valued feature"));
		}
	}

	private boolean isIntegerType(EStructuralFeature feature) {
		if (!(feature instanceof EAttribute attribute)) {
			return false;
		}
		EDataType type = attribute.getEAttributeType();
		return type != null && INTEGER_TYPE_NAMES.contains(type.getName());
	}

	private boolean isNumericLiteral(String raw) {
		try {
			Double.parseDouble(raw);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void putIfPresent(Map<String, String> details, String key, Consumer<String> consumer) {
		if (details.containsKey(key)) {
			consumer.accept(details.get(key));
		}
	}

	/**
	 * Unquotes and JSON-unescapes an annotation detail value.
	 * <p>
	 * Most {@code JSONSCHEMA}-source detail values were stored via Jackson
	 * {@code JsonNode.toString()}, so a string keyword (e.g. {@code pattern}) is
	 * JSON-quoted with JSON escaping applied (e.g. {@code \\d} for a literal
	 * backslash-d). {@code format} is the exception — it is stored unquoted via
	 * {@code JsonNode.asString()}. Parsing as JSON and falling back to the raw
	 * value on failure handles both cases without needing to know which applies.
	 * </p>
	 */
	private String unquote(String raw) {
		if (raw == null) {
			return null;
		}
		try {
			ObjectMapper mapper = JsonMapper.builder().build();
			JsonNode node = mapper.readTree(raw);
			if (node.isTextual()) {
				return node.asString();
			}
		} catch (Exception e) {
			// Not valid JSON (e.g. an already-unquoted `format` value) - use as-is.
		}
		return raw;
	}

	/**
	 * Builds an OCL single-quoted string literal.
	 * <p>
	 * The m2x OCL grammar ({@code Ocl.g4}'s {@code STRING_LITERAL} rule) only
	 * recognizes a fixed whitelist of backslash escapes and has no doubled-quote
	 * escape (unlike the classic OMG OCL/SQL convention) — a literal backslash
	 * must be doubled ({@code \\}) and a literal quote escaped as {@code \'}.
	 * Backslashes are escaped first so the quote-escaping step doesn't double the
	 * backslash it just introduced.
	 * </p>
	 */
	private String oclStringLiteral(String raw) {
		String escaped = raw.replace("\\", "\\\\").replace("'", "\\'");
		return "'" + escaped + "'";
	}

	private void applyInvariants(EClass eClass, Map<String, String> invariants, String delegateUri,
			List<JsonSchemaConversionDiagnostic> diagnostics) {
		EAnnotation classEcoreAnnotation = getOrCreateAnnotation(eClass, EcorePackage.eNS_URI);
		mergeSpaceSeparated(classEcoreAnnotation, "constraints", invariants.keySet());

		EAnnotation delegateAnnotation = getOrCreateAnnotation(eClass, delegateUri);
		delegateAnnotation.getDetails().putAll(invariants);

		EPackage ePackage = eClass.getEPackage();
		if (ePackage != null) {
			EAnnotation packageEcoreAnnotation = getOrCreateAnnotation(ePackage, EcorePackage.eNS_URI);
			mergeSpaceSeparated(packageEcoreAnnotation, "validationDelegates", Set.of(delegateUri));
		} else {
			diagnostics.add(JsonSchemaConversionDiagnostic.oclGenerationSkipped("validationDelegates",
					eClass.getName(),
					"EClass has no owning EPackage; invariants were written but will not self-activate "
							+ "without a package-level validationDelegates registration"));
		}
	}

	private EAnnotation getOrCreateAnnotation(EModelElement element, String source) {
		EAnnotation annotation = element.getEAnnotation(source);
		if (annotation == null) {
			annotation = ecoreFactory.createEAnnotation();
			annotation.setSource(source);
			element.getEAnnotations().add(annotation);
		}
		return annotation;
	}

	private void mergeSpaceSeparated(EAnnotation annotation, String detailKey, Set<String> toAdd) {
		String existing = annotation.getDetails().get(detailKey);
		LinkedHashSet<String> values = new LinkedHashSet<>();
		if (existing != null && !existing.isBlank()) {
			for (String value : existing.split("\\s+")) {
				if (!value.isBlank()) {
					values.add(value);
				}
			}
		}
		values.addAll(toAdd);
		annotation.getDetails().put(detailKey, String.join(" ", values));
	}
}
