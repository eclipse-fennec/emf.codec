/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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
package org.eclipse.fennec.codec.jsonschema.v2.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.fennec.codec.constants.AnnotationSources;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Converts EMF EPackage to JSON Schema.
 * <p>
 * Mapping rules:
 * <ul>
 *   <li>EPackage → JSON Schema document with $id, title, definitions</li>
 *   <li>EClass → object type with properties</li>
 *   <li>EEnum → string type with enum values</li>
 *   <li>EDataType → primitive JSON types</li>
 *   <li>EAttribute → property with type mapping</li>
 *   <li>EReference (containment) → nested object/array</li>
 *   <li>EReference (non-containment) → $ref</li>
 *   <li>ESuperTypes → allOf with $ref</li>
 * </ul>
 * </p>
 * <p>
 * Enhanced features:
 * <ul>
 *   <li>rootClass annotation → top-level properties</li>
 *   <li>discriminatedUnion annotation → oneOf with minProperties/maxProperties</li>
 *   <li>namespacePath annotation → organizational structure (e.g., configs/)</li>
 *   <li>variants → context-specific oneOf variants</li>
 *   <li>originalName / ExtendedMetaData → preserve original JSON names</li>
 * </ul>
 * </p>
 *
 * @author Data In Motion
 * @since 2025
 */
public class EPackageToJsonSchemaConverter {


	/**
	 * Option key to enable anchor-based references instead of JSON Pointer references.
	 * <p>
	 * When set to {@code true}, the converter will:
	 * <ul>
	 *   <li>Generate {@code $anchor} for each EClass definition</li>
	 *   <li>Use {@code #anchorName} instead of {@code #/definitions/Name} for references</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Default: {@code false} (use JSON Pointer references)
	 * </p>
	 * <p>
	 * Can be overridden per EClass using the annotation:
	 * {@code @http://fennec.eclipse.org/jsonschema(useAnchor="true")}
	 * </p>
	 */
	public static final String OPTION_USE_ANCHOR_REFS = "useAnchorRefs";

	/**
	 * When set to {@code true}, every structural feature is added to the
	 * {@code required} array regardless of its lowerBound.
	 * <p>
	 * Useful when generating schemas for AI structured-output requests, where all
	 * fields must be declared required so the model is forced to populate them.
	 * </p>
	 * <p>
	 * Default: {@code false} (only features with lowerBound &gt;= 1 are required)
	 * </p>
	 */
	public static final String OPTION_ALL_FIELDS_REQUIRED = "allFieldsRequired";

	private EPackage currentPackage;
	private String schemaFeature;
	private Map<String, EClassifier> processedClassifiers = new HashMap<>();
	private Map<String, Object> options = new HashMap<>();
	private Map<EClassifier, String> anchorNames = new HashMap<>();  // Track generated anchors

	/**
	 * Converts an EPackage to JSON Schema and writes to output stream.
	 *
	 * @param ePackage the EPackage to convert
	 * @param outputStream the output stream to write to
	 * @param schemaFeature the key for definitions (e.g., "definitions", "$defs", "schemas")
	 * @throws IOException if writing fails
	 */
	public void convert(EPackage ePackage, OutputStream outputStream, String schemaFeature) throws IOException {
		convert(ePackage, outputStream, schemaFeature, true, null);
	}

	/**
	 * Converts an EPackage to JSON Schema and writes to output stream.
	 *
	 * @param ePackage the EPackage to convert
	 * @param outputStream the output stream to write to
	 * @param schemaFeature the key for definitions (e.g., "definitions", "$defs", "schemas")
	 * @param prettyPrint whether to format the output with indentation
	 * @throws IOException if writing fails
	 */
	public void convert(EPackage ePackage, OutputStream outputStream, String schemaFeature, boolean prettyPrint) throws IOException {
		convert(ePackage, outputStream, schemaFeature, prettyPrint, null);
	}

	/**
	 * Converts an EPackage to JSON Schema and writes to output stream.
	 *
	 * @param ePackage the EPackage to convert
	 * @param outputStream the output stream to write to
	 * @param schemaFeature the key for definitions (e.g., "definitions", "$defs", "schemas")
	 * @param prettyPrint whether to format the output with indentation
	 * @param options conversion options (e.g., {@link #OPTION_USE_ANCHOR_REFS})
	 * @throws IOException if writing fails
	 */
	public void convert(EPackage ePackage, OutputStream outputStream, String schemaFeature,
			boolean prettyPrint, Map<String, Object> options) throws IOException {
		this.currentPackage = ePackage;
		this.schemaFeature = schemaFeature;
		this.processedClassifiers.clear();
		this.options = options != null ? options : new HashMap<>();
		this.anchorNames.clear();

		JsonMapper.Builder mapperBuilder = JsonMapper.builder();
		if (prettyPrint) {
			mapperBuilder.enable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT);
		}
		ObjectMapper mapper = mapperBuilder.build();

		try (JsonGenerator gen = mapper.createGenerator(outputStream)) {
			writePackage(ePackage, gen);
		}
	}

	private void writePackage(EPackage ePackage, JsonGenerator gen) throws IOException {
		// Pre-compute anchors for all classes before writing
		// This ensures anchor refs work even for forward references
		precomputeAnchors(ePackage);

		gen.writeStartObject();

		// Write package metadata
		writePackageMetadata(ePackage, gen);

		// Find root class (has "rootClass" annotation)
		EClass rootClass = findRootClass(ePackage);

		// Write top-level properties if root class exists
		if (rootClass != null) {
			writeTopLevelProperties(rootClass, gen);
			processedClassifiers.put(rootClass.getName(), rootClass);
		}

		// Write definitions section
		if (schemaFeature != null) {
			gen.writeObjectPropertyStart(schemaFeature);
			writeDefinitions(ePackage, gen);
			gen.writeEndObject();
		}

		gen.writeEndObject();
	}

	/**
	 * Pre-computes anchor names for all EClasses that should have anchors.
	 * This must be done before writing to ensure forward references work.
	 */
	private void precomputeAnchors(EPackage ePackage) {
		for (EClassifier classifier : ePackage.getEClassifiers()) {
			if (classifier instanceof EClass eClass) {
				String anchor = determineAnchor(eClass);
				if (anchor != null) {
					anchorNames.put(eClass, anchor);
				}
			}
		}
	}

	/**
	 * Writes package-level metadata ($schema, $id, title, description)
	 */
	private void writePackageMetadata(EPackage ePackage, JsonGenerator gen) throws IOException {
		String schema = extractAnnotationDetail(ePackage, AnnotationSources.JSONSCHEMA, "schema");
		if (schema != null) {
			gen.writeStringProperty("$schema", schema);
		}

		if (ePackage.getNsURI() != null) {
			gen.writeStringProperty("$id", ePackage.getNsURI());
		}

		// Use the original title from annotation if available
		String title = extractAnnotationDetail(ePackage, AnnotationSources.JSONSCHEMA, "originalTitle");
		if (title == null && ePackage.getName() != null) {
			title = ePackage.getName();
		}
		if (title != null) {
			gen.writeStringProperty("title", title);
		}

		String description = extractAnnotationDetail(ePackage, AnnotationSources.GEN_MODEL, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}
	}

	/**
	 * Finds the root class (annotated with "rootClass")
	 */
	private EClass findRootClass(EPackage ePackage) {
		for (EClassifier classifier : ePackage.getEClassifiers()) {
			if (classifier instanceof EClass eClass) {
				String rootAnnotation = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "rootClass");
				if ("true".equals(rootAnnotation)) {
					return eClass;
				}
			}
		}
		return null;
	}

	/**
	 * Writes top-level properties from the root class
	 */
	private void writeTopLevelProperties(EClass rootClass, JsonGenerator gen) throws IOException {
		gen.writeStringProperty("type", "object");

		String additionalProperties = extractAnnotationDetail(rootClass, AnnotationSources.JSONSCHEMA, "additionalProperties");
		if (additionalProperties != null) {
			writeAdditionalProperties(additionalProperties, gen);
		}

		// Collect required properties
		List<String> requiredProps = new ArrayList<>();
		for (EStructuralFeature feature : rootClass.getEStructuralFeatures()) {
			if (feature.getLowerBound() >= 1) {
				requiredProps.add(feature.getName());
			}
		}

		// Write required array
		if (!requiredProps.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String propName : requiredProps) {
				gen.writeString(propName);
			}
			gen.writeEndArray();
		}

		// Write properties
		gen.writeObjectPropertyStart("properties");
		for (EStructuralFeature feature : rootClass.getEStructuralFeatures()) {
			writeFeature(feature, gen);
		}
		gen.writeEndObject();
	}

	/**
	 * Writes all definitions (classifiers that are not the root class)
	 */
	private void writeDefinitions(EPackage ePackage, JsonGenerator gen) throws IOException {
		// Group classifiers by namespace
		Map<String, List<EClassifier>> namespaceMap = groupClassifiersByNamespace(ePackage);

		// Build tree structure for nested namespaces
		NamespaceNode root = buildNamespaceTree(namespaceMap);

		// Write the tree structure
		writeNamespaceNode(root, gen, true);
	}

	/**
	 * Groups classifiers by namespace (extracted from namespacePath annotations)
	 */
	private Map<String, List<EClassifier>> groupClassifiersByNamespace(EPackage ePackage) {
		Map<String, List<EClassifier>> namespaceMap = new LinkedHashMap<>();
		namespaceMap.put("", new ArrayList<>());

		for (EClassifier classifier : ePackage.getEClassifiers()) {
			// Skip artificial classifiers
			String artificial = extractAnnotationDetail(classifier, AnnotationSources.JSONSCHEMA, "artificial");
			if ("true".equals(artificial)) {
				continue;
			}

			// Skip variant classes (written inline)
			String variant = extractAnnotationDetail(classifier, AnnotationSources.JSONSCHEMA, "variant");
			if (variant != null) {
				continue;
			}

			// Skip discriminated union subclasses
			if (classifier instanceof EClass eClass && isDiscriminatedUnionSubclass(eClass)) {
				continue;
			}

			String namespacePath = extractAnnotationDetail(classifier, AnnotationSources.JSONSCHEMA, "namespacePath");

			if (namespacePath != null && !namespacePath.isEmpty()) {
				namespaceMap.computeIfAbsent(namespacePath, k -> new ArrayList<>()).add(classifier);
			} else {
				namespaceMap.get("").add(classifier);
			}
		}

		return namespaceMap;
	}

	/**
	 * Builds namespace tree from flat namespace map
	 */
	private NamespaceNode buildNamespaceTree(Map<String, List<EClassifier>> namespaceMap) {
		NamespaceNode root = new NamespaceNode("");

		for (Map.Entry<String, List<EClassifier>> entry : namespaceMap.entrySet()) {
			String namespacePath = entry.getKey();
			List<EClassifier> classifiers = entry.getValue();

			if (namespacePath.isEmpty()) {
				root.classifiers.addAll(classifiers);
			} else {
				String[] pathParts = namespacePath.split("/");
				NamespaceNode current = root;
				for (String part : pathParts) {
					current = current.children.computeIfAbsent(part, NamespaceNode::new);
				}
				current.classifiers.addAll(classifiers);
			}
		}

		return root;
	}

	/**
	 * Writes a namespace node and its children recursively
	 */
	private void writeNamespaceNode(NamespaceNode node, JsonGenerator gen, boolean isRoot) throws IOException {
		// Write classifiers at this level
		for (EClassifier classifier : node.classifiers) {
			if (processedClassifiers.containsKey(classifier.getName())) {
				continue;
			}

			String discriminatedUnion = extractAnnotationDetail(classifier, AnnotationSources.JSONSCHEMA, "discriminatedUnion");
			if ("true".equals(discriminatedUnion) && classifier instanceof EClass eClass) {
				writeDiscriminatedUnion(eClass, gen);
			} else {
				writeClassifier(classifier, gen);
			}
		}

		// Write child namespaces
		for (Map.Entry<String, NamespaceNode> entry : node.children.entrySet()) {
			gen.writeObjectPropertyStart(entry.getKey());
			writeNamespaceNode(entry.getValue(), gen, false);
			gen.writeEndObject();
		}
	}

	/**
	 * Checks if a class is a subclass of a discriminated union base
	 */
	private boolean isDiscriminatedUnionSubclass(EClass eClass) {
		for (EClass superType : eClass.getESuperTypes()) {
			String discriminatedUnion = extractAnnotationDetail(superType, AnnotationSources.JSONSCHEMA, "discriminatedUnion");
			if ("true".equals(discriminatedUnion)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Writes a discriminated union as a oneOf schema
	 */
	private void writeDiscriminatedUnion(EClass abstractBase, JsonGenerator gen) throws IOException {
		String name = getOriginalName(abstractBase);
		gen.writeName(name);
		gen.writeStartObject();

		String description = extractAnnotationDetail(abstractBase, AnnotationSources.GEN_MODEL, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		gen.writeStringProperty("type", "object");
		gen.writeNumberProperty("minProperties", 1);
		gen.writeNumberProperty("maxProperties", 1);
		gen.writeBooleanProperty("unevaluatedProperties", false);

		// Find all concrete subclasses
		List<EClass> subclasses = currentPackage.getEClassifiers().stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.filter(c -> c.getESuperTypes().contains(abstractBase))
				.toList();

		// Write oneOf array
		gen.writeArrayPropertyStart("oneOf");
		for (EClass subclass : subclasses) {
			String discriminatorKey = extractAnnotationDetail(subclass, AnnotationSources.JSONSCHEMA, "discriminatorKey");
			String configRef = extractAnnotationDetail(subclass, AnnotationSources.JSONSCHEMA, "configRef");

			if (discriminatorKey != null && configRef != null) {
				gen.writeStartObject();
				gen.writeArrayPropertyStart("required");
				gen.writeString(discriminatorKey);
				gen.writeEndArray();

				gen.writeObjectPropertyStart("properties");
				gen.writeObjectPropertyStart(discriminatorKey);
				gen.writeStringProperty("$ref", configRef);
				gen.writeEndObject();
				gen.writeEndObject();

				gen.writeEndObject();
			}
		}
		gen.writeEndArray();

		gen.writeEndObject();
	}

	private void writeClassifier(EClassifier classifier, JsonGenerator gen) throws IOException {
		// Skip artificial classifiers unless they are TopLevelArray
		String artificial = extractAnnotationDetail(classifier, AnnotationSources.JSONSCHEMA, "artificial");
		String topLevelArray = extractAnnotationDetail(classifier, AnnotationSources.JSONSCHEMA, "source");
		if ("true".equals(artificial) && !"TopLevelArray".equals(topLevelArray)) {
			return;
		}

		String name = getOriginalName(classifier);
		gen.writeName(name);

		if (classifier instanceof EClass eClass) {
			// Check for commonBase (context-specific variants)
			String commonBase = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "commonBase");
			if ("true".equals(commonBase)) {
				writeContextSpecificVariantsAsOneOf(eClass, gen);
			} else {
				writeEClass(eClass, gen);
			}
		} else if (classifier instanceof EEnum eEnum) {
			writeEEnum(eEnum, gen);
		} else if (classifier instanceof EDataType eDataType) {
			writeEDataType(eDataType, gen);
		}
	}

	/**
	 * Writes a common base class and its variants as a oneOf structure
	 */
	private void writeContextSpecificVariantsAsOneOf(EClass baseClass, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		String description = extractAnnotationDetail(baseClass, AnnotationSources.GEN_MODEL, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		// Find all variant subclasses
		List<EClass> variants = findVariantSubclasses(baseClass);

		gen.writeArrayPropertyStart("oneOf");
		for (EClass variant : variants) {
			writeVariantAsFullSchema(variant, baseClass, gen);
		}
		gen.writeEndArray();

		gen.writeEndObject();
	}

	/**
	 * Finds all variant subclasses of a parent class
	 */
	private List<EClass> findVariantSubclasses(EClass parent) {
		return currentPackage.getEClassifiers().stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.filter(c -> {
					String variant = extractAnnotationDetail(c, AnnotationSources.JSONSCHEMA, "variant");
					return variant != null && c.getESuperTypes().contains(parent);
				})
				.toList();
	}

	/**
	 * Writes a variant as a full schema (with all properties from base + variant)
	 */
	private void writeVariantAsFullSchema(EClass variant, EClass baseClass, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		String title = extractAnnotationDetail(variant, AnnotationSources.JSONSCHEMA, "variant");
		if (title != null) {
			gen.writeStringProperty("title", title);
		}

		String description = extractAnnotationDetail(variant, AnnotationSources.GEN_MODEL, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		gen.writeStringProperty("type", "object");

		// Collect all properties (from base + variant)
		List<EStructuralFeature> allFeatures = new ArrayList<>();
		allFeatures.addAll(baseClass.getEStructuralFeatures());
		allFeatures.addAll(variant.getEStructuralFeatures());

		// Collect required properties
		List<String> requiredProps = new ArrayList<>();
		for (EStructuralFeature feature : allFeatures) {
			if (feature.getLowerBound() >= 1) {
				requiredProps.add(feature.getName());
			}
		}

		// Write properties
		if (!allFeatures.isEmpty()) {
			gen.writeObjectPropertyStart("properties");
			for (EStructuralFeature feature : allFeatures) {
				writeFeature(feature, gen);
			}
			gen.writeEndObject();
		}

		// Write required array
		if (!requiredProps.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String propName : requiredProps) {
				gen.writeString(propName);
			}
			gen.writeEndArray();
		}

		String additionalProperties = extractAnnotationDetail(variant, AnnotationSources.JSONSCHEMA, "additionalProperties");
		if (additionalProperties != null) {
			writeAdditionalProperties(additionalProperties, gen);
		}

		gen.writeEndObject();
	}

	private void writeEClass(EClass eClass, JsonGenerator gen) throws IOException {
		gen.writeStartObject();
		writeEClassContent(eClass, gen);
		gen.writeEndObject();
	}

	private void writeEClassContent(EClass eClass, JsonGenerator gen) throws IOException {
		String topLevelArray = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "source");
		String additionalProperties = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "additionalProperties");
		String description = extractAnnotationDetail(eClass, AnnotationSources.GEN_MODEL, "documentation");
		String comment = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "comment");
		String deprecated = extractAnnotationDetail(eClass, AnnotationSources.GEN_MODEL, "deprecated");

		// Write $anchor if pre-computed (either from annotation or option)
		String anchor = anchorNames.get(eClass);
		if (anchor != null) {
			gen.writeStringProperty("$anchor", anchor);
		}

		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		// Write $comment if present
		if (comment != null) {
			gen.writeStringProperty("$comment", comment);
		}

		// Write deprecated if true
		if ("true".equals(deprecated)) {
			gen.writeBooleanProperty("deprecated", true);
		}

		if (additionalProperties != null) {
			writeAdditionalProperties(additionalProperties, gen);
		}

		if ("TopLevelArray".equals(topLevelArray)) {
			writeTopLevelArrayClass(eClass, gen);
		} else {
			writeObjectClass(eClass, gen);
		}
	}

	/**
	 * Converts a single EClass to a JSON Schema document and writes to output stream.
	 *
	 * @param eClass the EClass to convert
	 * @param outputStream the output stream to write to
	 * @param prettyPrint whether to format the output with indentation
	 * @throws IOException if writing fails
	 */
	public void convertEClass(EClass eClass, OutputStream outputStream, boolean prettyPrint) throws IOException {
		convertEClass(eClass, outputStream, prettyPrint, null);
	}

	/**
	 * Converts a single EClass to a JSON Schema document and writes to output stream.
	 *
	 * @param eClass the EClass to convert
	 * @param outputStream the output stream to write to
	 * @param prettyPrint whether to format the output with indentation
	 * @param options conversion options (e.g., {@link #OPTION_ALL_FIELDS_REQUIRED}, {@link #OPTION_USE_ANCHOR_REFS})
	 * @throws IOException if writing fails
	 */
	public void convertEClass(EClass eClass, OutputStream outputStream, boolean prettyPrint, Map<String, Object> options) throws IOException {
		this.currentPackage = eClass.getEPackage();
		this.processedClassifiers.clear();
		this.options = options != null ? options : new HashMap<>();
		this.anchorNames.clear();
		if (currentPackage != null) {
			precomputeAnchors(currentPackage);
		}
		JsonMapper.Builder b = JsonMapper.builder();
		if (prettyPrint) {
			b.enable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT);
		}
		try (JsonGenerator gen = b.build().createGenerator(outputStream)) {
			gen.writeStartObject();
			writeEClassDocumentMetadata(eClass, gen);
			writeEClassContent(eClass, gen);
			gen.writeEndObject();
		}
	}

	private void writeEClassDocumentMetadata(EClass eClass, JsonGenerator gen) throws IOException {
		// $schema: from EClass annotation, fallback to EPackage annotation
		String schema = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "schema");
		if (schema == null && currentPackage != null) {
			schema = extractAnnotationDetail(currentPackage, AnnotationSources.JSONSCHEMA, "schema");
		}
		if (schema != null) {
			gen.writeStringProperty("$schema", schema);
		}

		// $id: from EClass annotation, fallback to derived URI
		String id = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "id");
		if (id == null && currentPackage != null && currentPackage.getNsURI() != null) {
			id = currentPackage.getNsURI() + "#" + eClass.getName();
		}
		if (id != null) {
			gen.writeStringProperty("$id", id);
		}

		// title: from originalTitle annotation, ExtendedMetaData name, originalName, or class name
		String title = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "originalTitle");
		if (title == null) {
			title = extractAnnotationDetail(eClass, AnnotationSources.EXTENDED_METADATA, "name");
		}
		if (title == null) {
			title = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "originalName");
		}
		if (title == null) {
			title = eClass.getName();
		}
		if (title != null) {
			gen.writeStringProperty("title", title);
		}
	}

	private void writeAdditionalProperties(String additionalProperties, JsonGenerator gen) throws IOException {
		if (additionalProperties.contains("{")) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(additionalProperties);
			gen.writeName("additionalProperties");
			gen.writeTree(node);
		} else {
			gen.writeBooleanProperty("additionalProperties", Boolean.valueOf(additionalProperties));
		}
	}

	private void writeTopLevelArrayClass(EClass eClass, JsonGenerator gen) throws IOException {
		gen.writeStringProperty("type", "array");

		EStructuralFeature items = eClass.getEStructuralFeature("items");
		if (items != null) {
			writeFeature(items, gen);
		}
	}

	private void writeObjectClass(EClass eClass, JsonGenerator gen) throws IOException {
		List<EClass> artificialParents = eClass.getESuperTypes().stream()
				.filter(st -> extractAnnotationDetail(st, AnnotationSources.JSONSCHEMA, "artificial") != null)
				.toList();
		List<EClass> nonArtificialParents = eClass.getESuperTypes().stream()
				.filter(st -> extractAnnotationDetail(st, AnnotationSources.JSONSCHEMA, "artificial") == null)
				.toList();

		if (nonArtificialParents.isEmpty()) {
			writeSimpleObjectClass(eClass, artificialParents, gen);
		} else {
			writeInheritedObjectClass(eClass, nonArtificialParents, artificialParents, gen);
		}
	}

	private void writeSimpleObjectClass(EClass eClass, List<EClass> artificialParents, JsonGenerator gen) throws IOException {
		gen.writeStringProperty("type", "object");

		List<String> requiredProperties = new LinkedList<>();
		boolean isPropertiesWritten = false;

		// Write own features
		for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
			if (!isPropertiesWritten) {
				gen.writeObjectPropertyStart("properties");
				isPropertiesWritten = true;
			}
			if (isAllFieldsRequired() || feature.isRequired()) {
				requiredProperties.add(feature.getName());
			}
			writeFeature(feature, gen);
		}

		// Write inherited features from artificial parents
		for (EClass parent : artificialParents) {
			for (EStructuralFeature feature : parent.getEStructuralFeatures()) {
				if (!isPropertiesWritten) {
					gen.writeObjectPropertyStart("properties");
					isPropertiesWritten = true;
				}
				if (isAllFieldsRequired() || feature.isRequired()) {
					requiredProperties.add(feature.getName());
				}
				writeFeature(feature, gen);
			}
		}

		if (isPropertiesWritten) {
			gen.writeEndObject();
		}

		writeRequiredArray(requiredProperties, gen);
	}

	private void writeInheritedObjectClass(EClass eClass, List<EClass> nonArtificialParents,
			List<EClass> artificialParents, JsonGenerator gen) throws IOException {
		gen.writeName("allOf");
		gen.writeStartArray();

		// Write $ref for each parent
		for (EClass parent : nonArtificialParents) {
			gen.writeStartObject();
			gen.writeStringProperty("$ref", buildRef(getOriginalName(parent), null, parent));
			gen.writeEndObject();
		}

		// Write own properties
		gen.writeStartObject();
		gen.writeStringProperty("type", "object");

		List<String> requiredProperties = new LinkedList<>();
		boolean isPropertiesWritten = false;

		for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
			if (!isPropertiesWritten) {
				gen.writeObjectPropertyStart("properties");
				isPropertiesWritten = true;
			}
			if (isAllFieldsRequired() || feature.isRequired()) {
				requiredProperties.add(feature.getName());
			}
			writeFeature(feature, gen);
		}

		// Include artificial parent features
		for (EClass parent : artificialParents) {
			for (EStructuralFeature feature : parent.getEStructuralFeatures()) {
				if (!isPropertiesWritten) {
					gen.writeObjectPropertyStart("properties");
					isPropertiesWritten = true;
				}
				if (isAllFieldsRequired() || feature.isRequired()) {
					requiredProperties.add(feature.getName());
				}
				writeFeature(feature, gen);
			}
		}

		if (isPropertiesWritten) {
			gen.writeEndObject();
		}

		writeRequiredArray(requiredProperties, gen);

		gen.writeEndObject();
		gen.writeEndArray();
	}

	private void writeFeature(EStructuralFeature feature, JsonGenerator gen) throws IOException {
		if (feature instanceof EAttribute eAttribute) {
			writeEAttribute(eAttribute, gen);
		} else if (feature instanceof EReference eReference) {
			writeEReference(eReference, gen);
		}
	}

	private void writeEAttribute(EAttribute eAttribute, JsonGenerator gen) throws IOException {
		String documentation = extractAnnotationDetail(eAttribute, AnnotationSources.GEN_MODEL, "documentation");
		String noTypeInfo = extractAnnotationDetail(eAttribute, AnnotationSources.JSONSCHEMA, "noTypeInfo");
		String format = extractAnnotationDetail(eAttribute, AnnotationSources.JSONSCHEMA, "format");
		String writeOnly = extractAnnotationDetail(eAttribute, AnnotationSources.JSONSCHEMA, "writeOnly");
		String uniqueItems = extractAnnotationDetail(eAttribute, AnnotationSources.JSONSCHEMA, "uniqueItems");

		EDataType type = eAttribute.getEAttributeType();
		gen.writeName(eAttribute.getName());

		if (eAttribute.isMany()) {
			writeMultiValuedAttribute(eAttribute, type, documentation, noTypeInfo, format, writeOnly, uniqueItems, gen);
		} else {
			writeSingleValuedAttribute(eAttribute, type, documentation, noTypeInfo, format, writeOnly, uniqueItems, gen);
		}
	}

	private void writeMultiValuedAttribute(EAttribute eAttribute, EDataType type, String documentation,
			String noTypeInfo, String format, String writeOnly, String uniqueItems, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		if (documentation != null) {
			gen.writeStringProperty("description", documentation);
		}
		if (!"true".equals(noTypeInfo)) {
			gen.writeStringProperty("type", "array");
		}
		if (writeOnly != null) {
			gen.writeBooleanProperty("writeOnly", Boolean.valueOf(writeOnly));
		}
		if (uniqueItems != null) {
			gen.writeBooleanProperty("uniqueItems", Boolean.valueOf(uniqueItems));
		}

		// Write minItems/maxItems from EMF bounds
		writeArrayBounds(eAttribute, gen);

		writeConstValue(eAttribute, true, gen);

		String itemsAnnotation = extractAnnotationDetail(eAttribute, AnnotationSources.JSONSCHEMA, "items");
		if ("true".equals(itemsAnnotation)) {
			writeArrayItems(eAttribute, type, gen);
		}

		// Write additional schema properties
		writeAdditionalSchemaProperties(eAttribute, gen);

		gen.writeEndObject();
	}

	/**
	 * Writes minItems/maxItems from EMF bounds.
	 */
	private void writeArrayBounds(EStructuralFeature feature, JsonGenerator gen) throws IOException {
		int lowerBound = feature.getLowerBound();
		int upperBound = feature.getUpperBound();

		// Write minItems if lowerBound > 0
		if (lowerBound > 0) {
			gen.writeNumberProperty("minItems", lowerBound);
		}

		// Write maxItems if upperBound is not unbounded (-1)
		if (upperBound > 0 && upperBound != -1) {
			gen.writeNumberProperty("maxItems", upperBound);
		}
	}

	private void writeSingleValuedAttribute(EAttribute eAttribute, EDataType type, String documentation,
			String noTypeInfo, String format, String writeOnly, String uniqueItems, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		if (documentation != null) {
			gen.writeStringProperty("description", documentation);
		}

		if (type instanceof EEnum eEnum) {
			writeEnumLiterals(eEnum.getELiterals(), gen);
			if (!"true".equals(noTypeInfo)) {
				gen.writeStringProperty("type", "string");
			}
		}

		writeConstValue(eAttribute, false, gen);

		if (format != null) {
			gen.writeStringProperty("format", format);
		}
		if (writeOnly != null) {
			gen.writeBooleanProperty("writeOnly", Boolean.valueOf(writeOnly));
		}
		if (uniqueItems != null) {
			gen.writeBooleanProperty("uniqueItems", Boolean.valueOf(uniqueItems));
		}

		if (!(type instanceof EEnum) && !"true".equals(noTypeInfo)) {
			String jsonType = getJsonTypeFromEDataType(type);
			if ("javaObject".equals(jsonType)) {
				String dataTypeStr = extractAnnotationDetail(eAttribute, AnnotationSources.JSONSCHEMA, "dataType");
				if (dataTypeStr != null) {
					writeTypeArray(dataTypeStr, gen);
				}
			} else {
				gen.writeStringProperty("type", jsonType);
			}
		}

		// Write additional schema properties
		writeAdditionalSchemaProperties(eAttribute, gen);

		gen.writeEndObject();
	}

	private void writeEReference(EReference eReference, JsonGenerator gen) throws IOException {
		String documentation = extractAnnotationDetail(eReference, AnnotationSources.GEN_MODEL, "documentation");
		String noTypeInfo = extractAnnotationDetail(eReference, AnnotationSources.JSONSCHEMA, "noTypeInfo");
		String writeOnly = extractAnnotationDetail(eReference, AnnotationSources.JSONSCHEMA, "writeOnly");
		String uniqueItems = extractAnnotationDetail(eReference, AnnotationSources.JSONSCHEMA, "uniqueItems");

		EClassifier type = eReference.getEType();
		gen.writeName(eReference.getName());

		// Check if this references an artificial classifier that should be expanded inline
		if (type instanceof EClass eClass) {
			String artificial = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "artificial");
			if ("true".equals(artificial)) {
				// Check for variant parent (oneOf)
				if (isArtificialVariantParent(eClass)) {
					writePropertyLevelOneOf(eClass, gen);
					return;
				}
				// Regular artificial - expand inline
				if (eReference.isMany()) {
					gen.writeStartObject();
					gen.writeStringProperty("type", "array");
					gen.writeName("items");
					writeEClassInline(eClass, gen);
					gen.writeEndObject();
				} else {
					writeEClassInline(eClass, gen);
				}
				return;
			}
		}

		if (eReference.isMany()) {
			writeMultiValuedReference(eReference, type, documentation, noTypeInfo, writeOnly, uniqueItems, gen);
		} else {
			writeSingleValuedReference(eReference, type, gen);
		}
	}

	/**
	 * Checks if an EClass is an artificial classifier that serves as a parent for variants
	 */
	private boolean isArtificialVariantParent(EClass eClass) {
		String artificial = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "artificial");
		if (!"true".equals(artificial)) {
			return false;
		}
		return !findVariantSubclasses(eClass).isEmpty();
	}

	/**
	 * Writes a property-level oneOf (artificial variants)
	 */
	private void writePropertyLevelOneOf(EClass parent, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		List<EClass> variants = findVariantSubclasses(parent);

		gen.writeArrayPropertyStart("oneOf");
		for (EClass variant : variants) {
			writeVariantInline(variant, gen);
		}
		gen.writeEndArray();

		String defaultValue = extractAnnotationDetail(parent, AnnotationSources.JSONSCHEMA, "default");
		if (defaultValue != null) {
			writeDefaultValue(defaultValue, gen);
		}

		gen.writeEndObject();
	}

	/**
	 * Writes a variant inline
	 */
	private void writeVariantInline(EClass variant, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		if (variant.getEStructuralFeatures().size() == 1) {
			EStructuralFeature feature = variant.getEStructuralFeatures().get(0);

			if ("value".equals(feature.getName()) && feature instanceof EAttribute attr) {
				// Primitive type variant
				String jsonType = getJsonTypeFromEDataType(attr.getEAttributeType());
				gen.writeStringProperty("type", jsonType);
			} else {
				writeVariantWithProperties(variant, gen);
			}
		} else if (variant.getEStructuralFeatures().isEmpty()) {
			gen.writeStringProperty("type", "object");
		} else {
			writeVariantWithProperties(variant, gen);
		}

		gen.writeEndObject();
	}

	/**
	 * Writes a variant that has normal properties
	 */
	private void writeVariantWithProperties(EClass variant, JsonGenerator gen) throws IOException {
		gen.writeStringProperty("type", "object");

		List<String> requiredProps = new ArrayList<>();
		for (EStructuralFeature feature : variant.getEStructuralFeatures()) {
			if (feature.getLowerBound() >= 1) {
				requiredProps.add(feature.getName());
			}
		}

		if (!variant.getEStructuralFeatures().isEmpty()) {
			gen.writeObjectPropertyStart("properties");
			for (EStructuralFeature feature : variant.getEStructuralFeatures()) {
				writeFeature(feature, gen);
			}
			gen.writeEndObject();
		}

		if (!requiredProps.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String propName : requiredProps) {
				gen.writeString(propName);
			}
			gen.writeEndArray();
		}
	}

	/**
	 * Writes an EClass inline (for artificial classifiers)
	 */
	private void writeEClassInline(EClass eClass, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		String description = extractAnnotationDetail(eClass, AnnotationSources.GEN_MODEL, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		gen.writeStringProperty("type", "object");

		List<String> requiredProps = new ArrayList<>();
		for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
			if (feature.getLowerBound() >= 1) {
				requiredProps.add(feature.getName());
			}
		}

		if (!eClass.getEStructuralFeatures().isEmpty()) {
			gen.writeObjectPropertyStart("properties");
			for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
				writeFeature(feature, gen);
			}
			gen.writeEndObject();
		}

		if (!requiredProps.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String propName : requiredProps) {
				gen.writeString(propName);
			}
			gen.writeEndArray();
		}

		String additionalProperties = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "additionalProperties");
		if (additionalProperties != null) {
			writeAdditionalProperties(additionalProperties, gen);
		}

		gen.writeEndObject();
	}

	private void writeMultiValuedReference(EReference eReference, EClassifier type, String documentation,
			String noTypeInfo, String writeOnly, String uniqueItems, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		if (documentation != null) {
			gen.writeStringProperty("description", documentation);
		}
		if (!"true".equals(noTypeInfo)) {
			gen.writeStringProperty("type", "array");
		}
		if (writeOnly != null) {
			gen.writeBooleanProperty("writeOnly", Boolean.valueOf(writeOnly));
		}
		if (uniqueItems != null) {
			gen.writeBooleanProperty("uniqueItems", Boolean.valueOf(uniqueItems));
		}

		// Write minItems/maxItems from EMF bounds
		writeArrayBounds(eReference, gen);

		gen.writeName("items");
		if (eReference.isContainment()) {
			writeEClass((EClass) type, gen);
		} else {
			writeNonContainmentReferenceItems(eReference, type, gen);
		}

		// Write additional schema properties
		writeAdditionalSchemaProperties(eReference, gen);

		gen.writeEndObject();
	}

	private void writeSingleValuedReference(EReference eReference, EClassifier type, JsonGenerator gen) throws IOException {
		if (eReference.isContainment()) {
			writeEClass((EClass) type, gen);
		} else {
			gen.writeStartObject();
			// Check if we should use anchor-based refs (option or annotation)
			// If so, ignore stored ref annotation and generate new one
			String refPath = buildRefForType(eReference, type);
			gen.writeStringProperty("$ref", refPath);
			gen.writeEndObject();
		}
	}

	/**
	 * Builds the reference path for a type, considering anchor options.
	 */
	private String buildRefForType(EReference eReference, EClassifier type) {
		// If anchors are enabled and target has an anchor, use anchor-based ref
		if (hasAnchor(type)) {
			String namespacePath = extractAnnotationDetail(type, AnnotationSources.JSONSCHEMA, "namespacePath");
			return buildRef(getOriginalName(type), namespacePath, type);
		}

		// Otherwise, use stored ref annotation if available (preserves original)
		String ref = extractAnnotationDetail(eReference, AnnotationSources.JSONSCHEMA, "ref");
		if (ref != null) {
			return ref;
		}

		// Fall back to generating JSON Pointer ref
		String namespacePath = extractAnnotationDetail(type, AnnotationSources.JSONSCHEMA, "namespacePath");
		return buildRef(getOriginalName(type), namespacePath, type);
	}

	/**
	 * Checks if a classifier has an anchor (either pre-computed or from annotation).
	 */
	private boolean hasAnchor(EClassifier classifier) {
		if (anchorNames.containsKey(classifier)) {
			return true;
		}
		// Check by name in case of different instances
		for (EClassifier c : anchorNames.keySet()) {
			if (c.getName().equals(classifier.getName())) {
				return true;
			}
		}
		return false;
	}

	private void writeNonContainmentReferenceItems(EReference eReference, EClassifier type, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		String source = extractAnnotationDetail(eReference, AnnotationSources.JSONSCHEMA, "source");
		String refs = extractAnnotationDetail(eReference, AnnotationSources.JSONSCHEMA, "ref");

		if (source != null) {
			gen.writeName(source);
			gen.writeStartArray();
			if (refs != null) {
				for (String ref : refs.split(",")) {
					gen.writeStartObject();
					gen.writeStringProperty("$ref", ref.trim());
					gen.writeEndObject();
				}
			} else {
				writeSubclassRefs(type, gen);
			}
			gen.writeEndArray();
		} else if (refs != null) {
			gen.writeStringProperty("$ref", refs);
		} else {
			gen.writeName("anyOf");
			gen.writeStartArray();
			writeSubclassRefs(type, gen);
			gen.writeEndArray();
		}

		gen.writeEndObject();
	}

	private void writeSubclassRefs(EClassifier type, JsonGenerator gen) throws IOException {
		for (EClassifier classifier : currentPackage.getEClassifiers()) {
			if (classifier instanceof EClass eClass) {
				if (eClass.getESuperTypes().contains(type)) {
					gen.writeStartObject();
					gen.writeStringProperty("$ref", buildRef(getOriginalName(eClass), null, eClass));
					gen.writeEndObject();
				}
			}
		}
	}

	private void writeConstValue(ENamedElement element, boolean isInArray, JsonGenerator gen) throws IOException {
		String constAnnotation = extractAnnotationDetail(element, AnnotationSources.JSONSCHEMA, "const");
		String constType = extractAnnotationDetail(element, AnnotationSources.JSONSCHEMA, "constType");

		if (constAnnotation == null || constType == null) {
			return;
		}

		if (isInArray) {
			gen.writeArrayPropertyStart("const");
			String[] parts = constAnnotation.replaceAll("\\[|\\]", "").split(",");
			for (String part : parts) {
				writeConstValueByType(part.trim(), constType, gen);
			}
			gen.writeEndArray();
		} else {
			gen.writeName("const");
			writeConstValueByType(constAnnotation, constType, gen);
		}
	}

	private void writeConstValueByType(String value, String constType, JsonGenerator gen) throws IOException {
		switch (constType) {
			case "NUMBER":
				try {
					gen.writeNumber(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					gen.writeNumber(Float.parseFloat(value));
				}
				break;
			case "STRING":
				gen.writeString(value);
				break;
			case "BOOLEAN":
				gen.writeBoolean(Boolean.parseBoolean(value));
				break;
			default:
				gen.writeString(value);
				break;
		}
	}

	private void writeArrayItems(EStructuralFeature feature, EDataType type, JsonGenerator gen) throws IOException {
		String noArrayItemsTypeInfo = extractAnnotationDetail(feature, AnnotationSources.JSONSCHEMA, "noArrayItemsTypeInfo");
		String format = extractAnnotationDetail(feature, AnnotationSources.JSONSCHEMA, "format");

		gen.writeObjectPropertyStart("items");

		if (format != null) {
			gen.writeStringProperty("format", format);
		}

		if (type instanceof EEnum eEnum) {
			writeEnumLiterals(eEnum.getELiterals(), gen);
			if (!"true".equals(noArrayItemsTypeInfo)) {
				gen.writeStringProperty("type", "string");
			}
		} else if (!"true".equals(noArrayItemsTypeInfo)) {
			String jsonType = getJsonTypeFromEDataType(type);
			if ("javaObject".equals(jsonType)) {
				String dataTypeStr = extractAnnotationDetail(feature, AnnotationSources.JSONSCHEMA, "dataType");
				if (dataTypeStr != null) {
					writeTypeArray(dataTypeStr, gen);
				}
			} else {
				gen.writeStringProperty("type", jsonType);
			}
		}

		gen.writeEndObject();
	}

	private void writeTypeArray(String dataTypeStr, JsonGenerator gen) throws IOException {
		gen.writeArrayPropertyStart("type");
		for (String dt : dataTypeStr.split(",")) {
			gen.writeString(dt.trim());
		}
		gen.writeEndArray();
	}

	private void writeEEnum(EEnum eEnum, JsonGenerator gen) throws IOException {
		gen.writeStartObject();

		String description = extractAnnotationDetail(eEnum, AnnotationSources.GEN_MODEL, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		writeEnumLiterals(eEnum.getELiterals(), gen);
		gen.writeStringProperty("type", "string");

		gen.writeEndObject();
	}

	private void writeEnumLiterals(List<EEnumLiteral> literals, JsonGenerator gen) throws IOException {
		gen.writeArrayPropertyStart("enum");
		for (EEnumLiteral literal : literals) {
			gen.writeString(literal.getName());
		}
		gen.writeEndArray();
	}

	private void writeEDataType(EDataType eDataType, JsonGenerator gen) throws IOException {
		String documentation = extractAnnotationDetail(eDataType, AnnotationSources.GEN_MODEL, "documentation");
		String dataType = extractAnnotationDetail(eDataType, AnnotationSources.JSONSCHEMA, "dataType");

		gen.writeStartObject();

		if (documentation != null) {
			gen.writeStringProperty("description", documentation);
		}

		if (dataType != null) {
			writeTypeArray(dataType, gen);
		} else {
			gen.writeStringProperty("type", getJsonTypeFromInstanceClassName(eDataType.getInstanceClassName()));
		}

		gen.writeEndObject();
	}

	private void writeRequiredArray(List<String> requiredProperties, JsonGenerator gen) throws IOException {
		if (!requiredProperties.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String prop : requiredProperties) {
				gen.writeString(prop);
			}
			gen.writeEndArray();
		}
	}

	/**
	 * Writes additional JSON Schema properties from annotations
	 */
	private void writeAdditionalSchemaProperties(EStructuralFeature feature, JsonGenerator gen) throws IOException {
		String[] schemaProperties = {
			"default", "minItems", "maxItems", "minLength", "maxLength",
			"pattern", "minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum",
			"multipleOf", "examples",
			// Content keywords
			"contentEncoding", "contentMediaType"
		};

		String[] jsonProperties = {"default", "examples"};
		String[] booleanProperties = {"exclusiveMinimum", "exclusiveMaximum"};
		String[] numberProperties = {"minItems", "maxItems", "minLength", "maxLength", "minimum", "maximum", "multipleOf"};

		for (String propertyName : schemaProperties) {
			String value = extractAnnotationDetail(feature, AnnotationSources.JSONSCHEMA, propertyName);
			if (value != null) {
				try {
					if (contains(jsonProperties, propertyName)) {
						ObjectMapper mapper = new ObjectMapper();
						JsonNode node = mapper.readTree(value);
						gen.writeName(propertyName);
						gen.writeTree(node);
					} else if (contains(booleanProperties, propertyName)) {
						gen.writeBooleanProperty(propertyName, Boolean.parseBoolean(value));
					} else if (contains(numberProperties, propertyName)) {
						if (value.contains(".")) {
							gen.writeNumberProperty(propertyName, Double.parseDouble(value));
						} else {
							gen.writeNumberProperty(propertyName, Integer.parseInt(value));
						}
					} else {
						// String value - remove quotes if present (stored as JSON string)
						String cleanValue = value.startsWith("\"") && value.endsWith("\"")
							? value.substring(1, value.length() - 1)
							: value;
						gen.writeStringProperty(propertyName, cleanValue);
					}
				} catch (Exception e) {
					gen.writeStringProperty(propertyName, value);
				}
			}
		}

		// $comment - stored as "comment" annotation, write as "$comment"
		String comment = extractAnnotationDetail(feature, AnnotationSources.JSONSCHEMA, "comment");
		if (comment != null) {
			gen.writeStringProperty("$comment", comment);
		}

		// deprecated - check GenModel annotation
		String deprecated = extractAnnotationDetail(feature, AnnotationSources.GEN_MODEL, "deprecated");
		if ("true".equals(deprecated)) {
			gen.writeBooleanProperty("deprecated", true);
		}
	}

	private boolean contains(String[] array, String value) {
		for (String item : array) {
			if (item.equals(value)) {
				return true;
			}
		}
		return false;
	}

	private void writeDefaultValue(String defaultValue, JsonGenerator gen) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(defaultValue);
		gen.writeName("default");
		gen.writeTree(node);
	}

	/**
	 * Builds a reference to a classifier. Uses anchor-based reference if the target
	 * has an anchor, otherwise uses JSON Pointer reference.
	 *
	 * @param name the classifier name
	 * @param namespacePath optional namespace path
	 * @param targetClassifier the target classifier (for anchor lookup), may be null
	 * @return the reference string (e.g., "#anchor" or "#/definitions/Name")
	 */
	private String buildRef(String name, String namespacePath, EClassifier targetClassifier) {
		// Check if target has an anchor (either from annotation or generated)
		// First try direct lookup, then try by name (in case of different instances)
		if (targetClassifier != null) {
			if (anchorNames.containsKey(targetClassifier)) {
				return "#" + anchorNames.get(targetClassifier);
			}
			// Try to find by name in current package (handles cross-reference scenarios)
			// The targetClassifier might be from a different context but reference the same type
			for (Map.Entry<EClassifier, String> entry : anchorNames.entrySet()) {
				if (entry.getKey().getName().equals(targetClassifier.getName())) {
					return "#" + entry.getValue();
				}
			}
		}

		// Fall back to JSON Pointer reference
		StringBuilder ref = new StringBuilder("#/");
		if (schemaFeature != null) {
			ref.append(schemaFeature).append("/");
		}
		if (namespacePath != null && !namespacePath.isEmpty()) {
			ref.append(namespacePath).append("/");
		}
		ref.append(name);
		return ref.toString();
	}

	/**
	 * Determines the anchor name for an EClass.
	 * <p>
	 * Priority:
	 * <ol>
	 *   <li>Existing anchor annotation (from round-trip)</li>
	 *   <li>useAnchor="true" annotation on the class</li>
	 *   <li>Global OPTION_USE_ANCHOR_REFS option</li>
	 * </ol>
	 * </p>
	 *
	 * @param eClass the EClass to check
	 * @return the anchor name to use, or null if no anchor should be generated
	 */
	private String determineAnchor(EClass eClass) {
		// Check for existing anchor annotation (from round-trip)
		String existingAnchor = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "anchor");
		if (existingAnchor != null) {
			return existingAnchor;
		}

		// Check for per-class useAnchor annotation
		String useAnchorAnnotation = extractAnnotationDetail(eClass, AnnotationSources.JSONSCHEMA, "useAnchor");
		if ("true".equals(useAnchorAnnotation)) {
			return generateAnchorName(eClass);
		}

		// Check global option
		if (Boolean.TRUE.equals(options.get(OPTION_USE_ANCHOR_REFS))) {
			return generateAnchorName(eClass);
		}

		return null;
	}

	/**
	 * Generates an anchor name for an EClass.
	 * Uses camelCase version of the original name.
	 */
	private String generateAnchorName(EClass eClass) {
		String name = getOriginalName(eClass);
		// Convert to camelCase (first letter lowercase)
		if (name != null && !name.isEmpty()) {
			return Character.toLowerCase(name.charAt(0)) + name.substring(1);
		}
		return name;
	}

	/**
	 * Gets the original name from annotation or falls back to classifier name.
	 */
	private String getOriginalName(EClassifier classifier) {
		// Check ExtendedMetaData first (EMF standard)
		String originalName = extractAnnotationDetail(classifier, AnnotationSources.EXTENDED_METADATA, "name");
		if (originalName != null) {
			return originalName;
		}

		// Fall back to JSONSCHEMA annotation
		originalName = extractAnnotationDetail(classifier, AnnotationSources.JSONSCHEMA, "originalName");
		if (originalName != null) {
			return originalName;
		}

		return classifier.getName();
	}

	private String getJsonTypeFromEDataType(EDataType eDataType) {
		if (EcorePackage.Literals.ESTRING.equals(eDataType)) return "string";
		if (EcorePackage.Literals.EFLOAT.equals(eDataType) || EcorePackage.Literals.EFLOAT_OBJECT.equals(eDataType)) return "number";
		if (EcorePackage.Literals.EDOUBLE.equals(eDataType) || EcorePackage.Literals.EDOUBLE_OBJECT.equals(eDataType)) return "number";
		if (EcorePackage.Literals.EINT.equals(eDataType) || EcorePackage.Literals.EINTEGER_OBJECT.equals(eDataType)) return "integer";
		if (EcorePackage.Literals.EBIG_DECIMAL.equals(eDataType)) return "number";
		if (EcorePackage.Literals.EBIG_INTEGER.equals(eDataType)) return "integer";
		if (EcorePackage.Literals.EBYTE.equals(eDataType)) return "binary";
		if (EcorePackage.Literals.EBOOLEAN.equals(eDataType) || EcorePackage.Literals.EBOOLEAN_OBJECT.equals(eDataType)) return "boolean";
		return "javaObject";
	}

	private String getJsonTypeFromInstanceClassName(String instanceClassName) {
		if (instanceClassName == null) return "string";
		return switch (instanceClassName) {
			case "java.lang.String" -> "string";
			case "java.lang.Integer", "java.math.BigInteger", "int" -> "integer";
			case "java.lang.Boolean", "boolean" -> "boolean";
			case "java.lang.Double", "java.lang.Float", "java.math.BigDecimal", "double", "float" -> "number";
			default -> "string";
		};
	}

	private boolean isAllFieldsRequired() {
		return Boolean.TRUE.equals(options.get(OPTION_ALL_FIELDS_REQUIRED));
	}

	private String extractAnnotationDetail(EModelElement modelElement, String source, String detailKey) {
		EAnnotation annotation = modelElement.getEAnnotation(source);
		if (annotation == null) return null;
		return annotation.getDetails().get(detailKey);
	}

	/**
	 * Helper class to represent namespace tree structure
	 */
	private static class NamespaceNode {
		@SuppressWarnings("unused")
		final String name;
		final List<EClassifier> classifiers = new ArrayList<>();
		final Map<String, NamespaceNode> children = new LinkedHashMap<>();

		NamespaceNode(String name) {
			this.name = name;
		}
	}
}
