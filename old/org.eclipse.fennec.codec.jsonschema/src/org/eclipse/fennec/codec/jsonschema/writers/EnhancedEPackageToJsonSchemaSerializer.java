/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.jsonschema.writers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Enhanced EPackage to JSON Schema serializer with support for:
 * - Root class with top-level properties
 * - Discriminated unions (oneOf with minProperties/maxProperties)
 * - Property-level oneOf variants
 * - Organizational namespaces (e.g., configs/)
 *
 * @author Claude Code
 * @since Oct 16, 2025
 */
public class EnhancedEPackageToJsonSchemaSerializer extends ValueSerializer<EPackage> {

	private static final String GEN_MODEL_ANNOTATION_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";
	private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";
	private static final String EXTENDED_METADATA_ANNOTATION_SOURCE = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";

	private EPackage ePackage;
	private String schemaFeature;
	private Map<String, EClassifier> processedClassifiers = new HashMap<>();

	public EnhancedEPackageToJsonSchemaSerializer() {
		this.schemaFeature = "definitions";
	}

	public EnhancedEPackageToJsonSchemaSerializer(String schemaFeature) {
		this.schemaFeature = schemaFeature;
	}

	@Override
	public void serialize(EPackage ePackage, JsonGenerator gen, SerializationContext ctxt) {
		this.ePackage = ePackage;
		this.processedClassifiers.clear();

		gen.writeStartObject();

		// Write package metadata
		writePackageMetadata(ePackage, gen);

		// Find root class (has "rootClass" annotation)
		EClass rootClass = findRootClass(ePackage);

		// Write top-level properties if root class exists
		if (rootClass != null) {
			writeTopLevelProperties(rootClass, gen, ctxt);
			processedClassifiers.put(rootClass.getName(), rootClass);
		}

		// Write definitions section
		if (schemaFeature != null) {
			gen.writeObjectPropertyStart(schemaFeature);
			writeDefinitions(ePackage, gen, ctxt);
			gen.writeEndObject(); // End definitions
		}

		gen.writeEndObject(); // End root object
	}

	/**
	 * Writes package-level metadata ($schema, $id, title, description)
	 */
	private void writePackageMetadata(EPackage ePackage, JsonGenerator gen) {
		String schema = extractAnnotationDetail(ePackage, JSONSCHEMA_ANNOTATION_SOURCE, "schema");
		if (schema != null) {
			gen.writeStringProperty("$schema", schema);
		}

		if (ePackage.getNsURI() != null) {
			gen.writeStringProperty("$id", ePackage.getNsURI());
		}

		// Use the original title from the EPackage annotation if available
		String title = extractAnnotationDetail(ePackage, JSONSCHEMA_ANNOTATION_SOURCE, "originalTitle");
		// Fall back to package name if no original title found
		if (title == null && ePackage.getName() != null) {
			title = ePackage.getName();
		}
		if (title != null) {
			gen.writeStringProperty("title", title);
		}

		String description = extractAnnotationDetail(ePackage, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}
	}

	/**
	 * Finds the root class (annotated with "rootClass")
	 */
	private EClass findRootClass(EPackage ePackage) {
		for (EClassifier classifier : ePackage.getEClassifiers()) {
			if (classifier instanceof EClass) {
				String rootAnnotation = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "rootClass");
				if ("true".equals(rootAnnotation)) {
					return (EClass) classifier;
				}
			}
		}
		return null;
	}

	/**
	 * Writes top-level properties from the root class
	 */
	private void writeTopLevelProperties(EClass rootClass, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStringProperty("type", "object");

		String additionalProperties = extractAnnotationDetail(rootClass, JSONSCHEMA_ANNOTATION_SOURCE, "additionalProperties");
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
			if (feature instanceof EAttribute) {
				serializeEAttribute((EAttribute) feature, gen, ctxt);
			} else if (feature instanceof EReference) {
				serializeEReference((EReference) feature, gen, ctxt);
			}
		}
		gen.writeEndObject(); // End properties
	}

	/**
	 * Writes all definitions (classifiers that are not the root class)
	 */
	private void writeDefinitions(EPackage ePackage, JsonGenerator gen, SerializationContext ctxt) {
		// Group classifiers by namespace (for organizational structure like configs/)
		Map<String, List<EClassifier>> namespaceMap = groupClassifiersByNamespace(ePackage);

		// Build a tree structure from flat namespace map to handle nested namespaces
		NamespaceNode root = new NamespaceNode("");
		for (Map.Entry<String, List<EClassifier>> entry : namespaceMap.entrySet()) {
			String namespacePath = entry.getKey();
			List<EClassifier> classifiers = entry.getValue();

			if (namespacePath.isEmpty()) {
				// Root level classifiers
				root.classifiers.addAll(classifiers);
			} else {
				// Nested namespace - split path and build tree
				String[] pathParts = namespacePath.split("/");
				NamespaceNode current = root;
				for (String part : pathParts) {
					if (!current.children.containsKey(part)) {
						current.children.put(part, new NamespaceNode(part));
					}
					current = current.children.get(part);
				}
				current.classifiers.addAll(classifiers);
			}
		}

		// Write the tree structure
		writeNamespaceNode(root, gen, ctxt, true);
	}

	/**
	 * Writes a namespace node and its children recursively
	 */
	private void writeNamespaceNode(NamespaceNode node, JsonGenerator gen, SerializationContext ctxt, boolean isRoot) {
		// Write classifiers at this level
		for (EClassifier classifier : node.classifiers) {
			if (processedClassifiers.containsKey(classifier.getName())) {
				continue; // Skip root class
			}

			String discriminatedUnion = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "discriminatedUnion");
			if ("true".equals(discriminatedUnion)) {
				// This is a discriminated union - serialize as oneOf
				serializeDiscriminatedUnion((EClass) classifier, gen, ctxt);
			} else {
				// Regular classifier
				serializeClassifier(classifier, gen, ctxt);
			}
		}

		// Write child namespaces
		for (Map.Entry<String, NamespaceNode> entry : node.children.entrySet()) {
			String namespaceName = entry.getKey();
			NamespaceNode childNode = entry.getValue();

			gen.writeObjectPropertyStart(namespaceName);
			writeNamespaceNode(childNode, gen, ctxt, false);
			gen.writeEndObject(); // End namespace
		}
	}

	/**
	 * Helper class to represent namespace tree structure
	 */
	private static class NamespaceNode {
		String name;
		List<EClassifier> classifiers = new ArrayList<>();
		Map<String, NamespaceNode> children = new LinkedHashMap<>();

		NamespaceNode(String name) {
			this.name = name;
		}
	}

	/**
	 * Groups classifiers by namespace (extracted from namespacePath annotations)
	 */
	private Map<String, List<EClassifier>> groupClassifiersByNamespace(EPackage ePackage) {
		Map<String, List<EClassifier>> namespaceMap = new LinkedHashMap<>();
		namespaceMap.put("", new ArrayList<>()); // Default namespace

		for (EClassifier classifier : ePackage.getEClassifiers()) {
			// Skip artificial classifiers that are handled inline
			String artificial = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "artificial");
			if ("true".equals(artificial)) {
				continue;
			}

			// Skip variant classes (part of oneOf, written inline with their base)
			String variant = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "variant");
			if (variant != null) {
				continue;
			}

			// Skip discriminated union subclasses (they're written inline within their parent's oneOf)
			if (classifier instanceof EClass) {
				EClass eClass = (EClass) classifier;
				if (isDiscriminatedUnionSubclass(eClass)) {
					continue;
				}
			}

			// Extract the namespace path from annotations
			String namespacePath = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "namespacePath");

			if (namespacePath != null && !namespacePath.isEmpty()) {
				// Add to the specific namespace
				if (!namespaceMap.containsKey(namespacePath)) {
					namespaceMap.put(namespacePath, new ArrayList<>());
				}
				namespaceMap.get(namespacePath).add(classifier);
			} else {
				// Add to default namespace (root level)
				namespaceMap.get("").add(classifier);
			}
		}

		return namespaceMap;
	}

	/**
	 * Checks if a class is a subclass of a discriminated union base
	 */
	private boolean isDiscriminatedUnionSubclass(EClass eClass) {
		for (EClass superType : eClass.getESuperTypes()) {
			String discriminatedUnion = extractAnnotationDetail(superType, JSONSCHEMA_ANNOTATION_SOURCE, "discriminatedUnion");
			if ("true".equals(discriminatedUnion)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Serializes a discriminated union as a oneOf schema
	 */
	private void serializeDiscriminatedUnion(EClass abstractBase, JsonGenerator gen, SerializationContext ctxt) {
		String name = getOriginalName(abstractBase);
		gen.writeName(name);
		gen.writeStartObject();

		String description = extractAnnotationDetail(abstractBase, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		gen.writeStringProperty("type", "object");
		gen.writeNumberProperty("minProperties", 1);
		gen.writeNumberProperty("maxProperties", 1);
		gen.writeBooleanProperty("unevaluatedProperties", false);

		// Find all concrete subclasses
		List<EClass> subclasses = ePackage.getEClassifiers().stream()
				.filter(c -> c instanceof EClass)
				.map(c -> (EClass) c)
				.filter(c -> c.getESuperTypes().contains(abstractBase))
				.collect(Collectors.toList());

		// Write oneOf array
		gen.writeArrayPropertyStart("oneOf");
		for (EClass subclass : subclasses) {
			String discriminatorKey = extractAnnotationDetail(subclass, JSONSCHEMA_ANNOTATION_SOURCE, "discriminatorKey");
			String configRef = extractAnnotationDetail(subclass, JSONSCHEMA_ANNOTATION_SOURCE, "configRef");

			if (discriminatorKey != null && configRef != null) {
				gen.writeStartObject();
				gen.writeArrayPropertyStart("required");
				gen.writeString(discriminatorKey);
				gen.writeEndArray();

				gen.writeObjectPropertyStart("properties");
				gen.writeObjectPropertyStart(discriminatorKey);
				gen.writeStringProperty("$ref", configRef);
				gen.writeEndObject(); // End discriminatorKey property
				gen.writeEndObject(); // End properties

				gen.writeEndObject(); // End oneOf option
			}
		}
		gen.writeEndArray(); // End oneOf

		gen.writeEndObject(); // End schema
	}

	/**
	 * Serializes a regular classifier
	 */
	private void serializeClassifier(EClassifier classifier, JsonGenerator gen, SerializationContext ctxt) {
		// Double-check: This should never be called with an artificial classifier
		String artificial = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "artificial");
		if ("true".equals(artificial)) {
			System.err.println("ERROR: Attempting to serialize artificial classifier: " + classifier.getName());
			return; // Skip it
		}

		String name = getOriginalName(classifier);
		gen.writeName(name);

		if (classifier instanceof EClass) {
			EClass eClass = (EClass) classifier;

			// Check if this is a common base class (context-specific variants)
			String commonBase = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "commonBase");
			if ("true".equals(commonBase)) {
				// Serialize as oneOf with variants inline
				serializeContextSpecificVariantsAsOneOf(eClass, gen, ctxt);
			} else {
				serializeEClass(eClass, gen, ctxt);
			}
		} else if (classifier instanceof EEnum) {
			serializeEEnum((EEnum) classifier, gen, ctxt);
		} else if (classifier instanceof EDataType) {
			serializeEDataType((EDataType) classifier, gen, ctxt);
		}
	}

	/**
	 * Serializes a common base class and its variants as a oneOf structure
	 */
	private void serializeContextSpecificVariantsAsOneOf(EClass baseClass, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		String description = extractAnnotationDetail(baseClass, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		// Find all variant subclasses
		List<EClass> variants = ePackage.getEClassifiers().stream()
				.filter(c -> c instanceof EClass)
				.map(c -> (EClass) c)
				.filter(c -> {
					String variant = extractAnnotationDetail(c, JSONSCHEMA_ANNOTATION_SOURCE, "variant");
					return variant != null && c.getESuperTypes().contains(baseClass);
				})
				.collect(Collectors.toList());

		// Write oneOf array with variants inline
		gen.writeArrayPropertyStart("oneOf");
		for (EClass variant : variants) {
			serializeVariantAsFullSchema(variant, baseClass, gen, ctxt);
		}
		gen.writeEndArray(); // End oneOf

		gen.writeEndObject(); // End schema
	}

	/**
	 * Serializes a variant as a full schema (with all properties from base + variant)
	 */
	private void serializeVariantAsFullSchema(EClass variant, EClass baseClass, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		// Write title if present
		String title = extractAnnotationDetail(variant, JSONSCHEMA_ANNOTATION_SOURCE, "variant");
		if (title != null) {
			gen.writeStringProperty("title", title);
		}

		String description = extractAnnotationDetail(variant, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
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
				if (feature instanceof EAttribute) {
					serializeEAttribute((EAttribute) feature, gen, ctxt);
				} else if (feature instanceof EReference) {
					serializeEReference((EReference) feature, gen, ctxt);
				}
			}
			gen.writeEndObject(); // End properties
		}

		// Write required array
		if (!requiredProps.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String propName : requiredProps) {
				gen.writeString(propName);
			}
			gen.writeEndArray();
		}

		// Write additionalProperties
		String additionalProperties = extractAnnotationDetail(variant, JSONSCHEMA_ANNOTATION_SOURCE, "additionalProperties");
		if (additionalProperties != null) {
			writeAdditionalProperties(additionalProperties, gen);
		}

		gen.writeEndObject(); // End variant schema
	}

	/**
	 * Serializes an EClass
	 */
	private void serializeEClass(EClass eClass, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		String description = extractAnnotationDetail(eClass, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		gen.writeStringProperty("type", "object");

		// Collect required properties
		List<String> requiredProps = new ArrayList<>();
		for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
			if (feature.getLowerBound() >= 1) {
				requiredProps.add(feature.getName());
			}
		}

		// Write properties
		if (!eClass.getEStructuralFeatures().isEmpty()) {
			gen.writeObjectPropertyStart("properties");
			for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
				if (feature instanceof EAttribute) {
					serializeEAttribute((EAttribute) feature, gen, ctxt);
				} else if (feature instanceof EReference) {
					serializeEReference((EReference) feature, gen, ctxt);
				}
			}
			gen.writeEndObject(); // End properties
		}

		// Write required array
		if (!requiredProps.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String propName : requiredProps) {
				gen.writeString(propName);
			}
			gen.writeEndArray();
		}

		// Write additionalProperties
		String additionalProperties = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "additionalProperties");
		if (additionalProperties != null) {
			writeAdditionalProperties(additionalProperties, gen);
		}

		// Write anyOf constraint if present
		String anyOf = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "anyOf");
		if (anyOf != null) {
			writeJsonArrayOrObject(anyOf, "anyOf", gen);
		}

		// Write allOf constraint if present
		String allOf = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "allOf");
		if (allOf != null) {
			writeJsonArrayOrObject(allOf, "allOf", gen);
		}

		gen.writeEndObject(); // End object
	}

	/**
	 * Serializes an EAttribute
	 */
	private void serializeEAttribute(EAttribute eAttribute, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeName(eAttribute.getName());
		gen.writeStartObject();

		String description = extractAnnotationDetail(eAttribute, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		EDataType type = eAttribute.getEAttributeType();

		if (type instanceof EEnum) {
			EEnum eEnum = (EEnum) type;
			serializeEEnumLiterals(eEnum.getELiterals(), gen);
			gen.writeStringProperty("type", "string");
		} else {
			String jsonType = mapEDataTypeToJsonType(type);
			gen.writeStringProperty("type", jsonType);
		}

		// Write additional JSON Schema properties
		writeAdditionalSchemaProperties(eAttribute, gen);

		gen.writeEndObject(); // End attribute
	}

	/**
	 * Serializes an EReference
	 */
	private void serializeEReference(EReference eReference, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeName(eReference.getName());

		EClassifier type = eReference.getEReferenceType();

		// Check if this references an artificial classifier that should be expanded inline
		if (type instanceof EClass) {
			EClass eClass = (EClass) type;
			String artificial = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "artificial");

			if ("true".equals(artificial)) {
				// Check if this is a multi-type union base
				String multiType = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "multiType");
				if ("true".equals(multiType)) {
					serializeMultiTypeProperty(eClass, gen, ctxt);
					return;
				}

				// Check if this is a variant (child of a oneOf) - should reference parent instead
				String variant = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "variant");
				if (variant != null) {
					// This is a variant class - should serialize as the parent's oneOf
					// Find the parent class
					if (!eClass.getESuperTypes().isEmpty()) {
						EClass parentClass = eClass.getESuperTypes().get(0);
						// Serialize the parent's oneOf structure
						serializePropertyLevelOneOf(parentClass, gen, ctxt);
						return;
					} else {
						System.err.println("ERROR: Variant class " + eClass.getName() + " has no parent!");
						return;
					}
				}

				// Check if this is a variant parent (oneOf base)
				if (isArtificialVariantParent(eClass)) {
					// Serialize as inline oneOf
					serializePropertyLevelOneOf(eClass, gen, ctxt);
					return;
				} else {
					// Regular artificial classifier - expand inline as object
					if (eReference.isMany()) {
						gen.writeStartObject();
						gen.writeStringProperty("type", "array");
						gen.writeObjectPropertyStart("items");
						serializeEClassInline(eClass, null, gen, ctxt);
						gen.writeEndObject(); // End items
						gen.writeEndObject(); // End array
					} else {
						serializeEClassInline(eClass, eReference, gen, ctxt);
					}
					return;
				}
			}
		}

		String refAnnotation = extractAnnotationDetail(eReference, JSONSCHEMA_ANNOTATION_SOURCE, "ref");

		if (eReference.isMany()) {
			gen.writeStartObject();
			gen.writeStringProperty("type", "array");
			gen.writeObjectPropertyStart("items");

			if (refAnnotation != null) {
				gen.writeStringProperty("$ref", refAnnotation);
			} else {
				// Build the $ref path, using original name if available
				String refName = getOriginalName(type);

				// Get namespace path if present
				String namespacePath = extractAnnotationDetail(type, JSONSCHEMA_ANNOTATION_SOURCE, "namespacePath");
				String refPath;
				if (namespacePath != null && !namespacePath.isEmpty()) {
					refPath = "#/" + schemaFeature + "/" + namespacePath + "/" + refName;
				} else {
					refPath = "#/" + schemaFeature + "/" + refName;
				}

				gen.writeStringProperty("$ref", refPath);
			}

			gen.writeEndObject(); // End items

			// Write array-specific properties (minItems, maxItems, etc.)
			writeAdditionalSchemaProperties(eReference, gen);

			gen.writeEndObject(); // End array
		} else {
			gen.writeStartObject();

			if (refAnnotation != null) {
				gen.writeStringProperty("$ref", refAnnotation);
			} else {
				// Build the $ref path, using original name if available
				String refName = getOriginalName(type);

				// Get namespace path if present
				String namespacePath = extractAnnotationDetail(type, JSONSCHEMA_ANNOTATION_SOURCE, "namespacePath");
				String refPath;
				if (namespacePath != null && !namespacePath.isEmpty()) {
					refPath = "#/" + schemaFeature + "/" + namespacePath + "/" + refName;
				} else {
					refPath = "#/" + schemaFeature + "/" + refName;
				}

				gen.writeStringProperty("$ref", refPath);
			}

			gen.writeEndObject(); // End reference
		}
	}

	/**
	 * Serializes an EClass inline (for artificial classifiers)
	 * @param eClass The class to serialize
	 * @param eReference Optional reference that points to this class (for extracting default values)
	 */
	private void serializeEClassInline(EClass eClass, EReference eReference, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		String description = extractAnnotationDetail(eClass, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		gen.writeStringProperty("type", "object");

		// Collect required properties
		List<String> requiredProps = new ArrayList<>();
		for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
			if (feature.getLowerBound() >= 1) {
				requiredProps.add(feature.getName());
			}
		}

		// Write properties
		if (!eClass.getEStructuralFeatures().isEmpty()) {
			gen.writeObjectPropertyStart("properties");
			for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
				if (feature instanceof EAttribute) {
					serializeEAttribute((EAttribute) feature, gen, ctxt);
				} else if (feature instanceof EReference) {
					serializeEReference((EReference) feature, gen, ctxt);
				}
			}
			gen.writeEndObject(); // End properties
		}

		// Write required array
		if (!requiredProps.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String propName : requiredProps) {
				gen.writeString(propName);
			}
			gen.writeEndArray();
		}

		// Write additionalProperties
		String additionalProperties = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "additionalProperties");
		if (additionalProperties != null) {
			writeAdditionalProperties(additionalProperties, gen);
		}

		// Write default value from the reference if present
		if (eReference != null) {
			String defaultValue = extractAnnotationDetail(eReference, JSONSCHEMA_ANNOTATION_SOURCE, "default");
			if (defaultValue != null) {
				writeDefaultValue(defaultValue, gen);
			}
		}

		gen.writeEndObject(); // End object
	}

	/**
	 * Checks if an EClass is an artificial classifier that serves as a parent for variants
	 */
	private boolean isArtificialVariantParent(EClass eClass) {
		// Check if this is an artificial classifier (oneOf parent)
		String artificial = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "artificial");
		if (!"true".equals(artificial)) {
			return false;
		}

		// Check if there are variant subclasses
		List<EClass> variants = findVariantSubclasses(eClass);
		return !variants.isEmpty();
	}

	/**
	 * Finds all variant subclasses of a parent class
	 */
	private List<EClass> findVariantSubclasses(EClass parent) {
		return ePackage.getEClassifiers().stream()
				.filter(c -> c instanceof EClass)
				.map(c -> (EClass) c)
				.filter(c -> {
					String variant = extractAnnotationDetail(c, JSONSCHEMA_ANNOTATION_SOURCE, "variant");
					return variant != null && c.getESuperTypes().contains(parent);
				})
				.collect(Collectors.toList());
	}

	/**
	 * Serializes a multi-type property (e.g., "type": ["integer", "string"])
	 */
	private void serializeMultiTypeProperty(EClass unionBase, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		// Read the type array from annotation
		String typeArrayStr = extractAnnotationDetail(unionBase, JSONSCHEMA_ANNOTATION_SOURCE, "typeArray");
		if (typeArrayStr != null) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode typeArrayNode = mapper.readTree(typeArrayStr);
			gen.writeName("type");
			gen.writeTree(typeArrayNode);
		}

		// Write default value if present
		String defaultValue = extractAnnotationDetail(unionBase, JSONSCHEMA_ANNOTATION_SOURCE, "default");
		if (defaultValue != null) {
			writeDefaultValue(defaultValue, gen);
		}

		// Write other schema properties that were preserved
		String[] schemaProperties = {
			"minLength", "maxLength", "pattern", "format",
			"minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum",
			"multipleOf", "const", "title", "description", "examples"
		};

		for (String propertyName : schemaProperties) {
			String value = extractAnnotationDetail(unionBase, JSONSCHEMA_ANNOTATION_SOURCE, propertyName);
			if (value != null) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode node = mapper.readTree(value);
					gen.writeName(propertyName);
					gen.writeTree(node);
				} catch (Exception e) {
					// If parsing fails, write as string
					gen.writeStringProperty(propertyName, value);
				}
			}
		}

		gen.writeEndObject(); // End multi-type property
	}

	/**
	 * Serializes a property-level oneOf (artificial variants)
	 */
	private void serializePropertyLevelOneOf(EClass parent, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		List<EClass> variants = findVariantSubclasses(parent);

		// Write oneOf array
		gen.writeArrayPropertyStart("oneOf");
		for (EClass variant : variants) {
			serializeVariantInline(variant, gen, ctxt);
		}
		gen.writeEndArray(); // End oneOf

		// Write default if present on parent
		String defaultValue = extractAnnotationDetail(parent, JSONSCHEMA_ANNOTATION_SOURCE, "default");
		if (defaultValue != null) {
			writeDefaultValue(defaultValue, gen);
		}

		gen.writeEndObject(); // End oneOf container
	}

	/**
	 * Serializes a variant inline (not as a $ref)
	 */
	private void serializeVariantInline(EClass variant, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		// Check if this variant represents a primitive type or object
		if (variant.getEStructuralFeatures().size() == 1) {
			EStructuralFeature feature = variant.getEStructuralFeatures().get(0);

			if ("value".equals(feature.getName()) && feature instanceof EAttribute) {
				// This is a primitive type variant
				EAttribute attr = (EAttribute) feature;
				String jsonType = mapEDataTypeToJsonType(attr.getEAttributeType());
				gen.writeStringProperty("type", jsonType);
			} else if ("entries".equals(feature.getName())) {
				// This is an object with additionalProperties
				gen.writeStringProperty("type", "object");
				String mapEntryType = extractAnnotationDetail(feature, JSONSCHEMA_ANNOTATION_SOURCE, "mapEntryType");
				if (mapEntryType != null) {
					writeAdditionalProperties(mapEntryType, gen);
				}
			} else if ("items".equals(feature.getName())) {
				// This is an array type variant
				String arrayType = extractAnnotationDetail(variant, JSONSCHEMA_ANNOTATION_SOURCE, "arrayType");
				if ("true".equals(arrayType)) {
					gen.writeStringProperty("type", "array");
					String arrayItems = extractAnnotationDetail(variant, JSONSCHEMA_ANNOTATION_SOURCE, "arrayItems");
					if (arrayItems != null) {
						ObjectMapper mapper = new ObjectMapper();
						JsonNode itemsNode = mapper.readTree(arrayItems);
						gen.writeName("items");
						gen.writeTree(itemsNode);
					}
				} else {
					// Regular variant with properties
					serializeVariantWithProperties(variant, gen, ctxt);
				}
			} else {
				// Regular variant with properties
				serializeVariantWithProperties(variant, gen, ctxt);
			}
		} else if (variant.getEStructuralFeatures().isEmpty()) {
			// Empty variant - just write type object
			gen.writeStringProperty("type", "object");
		} else {
			// Variant with multiple properties
			serializeVariantWithProperties(variant, gen, ctxt);
		}

		gen.writeEndObject(); // End variant
	}

	/**
	 * Serializes a variant that has normal properties
	 */
	private void serializeVariantWithProperties(EClass variant, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStringProperty("type", "object");

		// Collect required properties
		List<String> requiredProps = new ArrayList<>();
		for (EStructuralFeature feature : variant.getEStructuralFeatures()) {
			if (feature.getLowerBound() >= 1) {
				requiredProps.add(feature.getName());
			}
		}

		// Write properties
		if (!variant.getEStructuralFeatures().isEmpty()) {
			gen.writeObjectPropertyStart("properties");
			for (EStructuralFeature feature : variant.getEStructuralFeatures()) {
				if (feature instanceof EAttribute) {
					serializeEAttribute((EAttribute) feature, gen, ctxt);
				} else if (feature instanceof EReference) {
					serializeEReference((EReference) feature, gen, ctxt);
				}
			}
			gen.writeEndObject(); // End properties
		}

		// Write required array
		if (!requiredProps.isEmpty()) {
			gen.writeArrayPropertyStart("required");
			for (String propName : requiredProps) {
				gen.writeString(propName);
			}
			gen.writeEndArray();
		}
	}

	/**
	 * Serializes an EEnum
	 */
	private void serializeEEnum(EEnum eEnum, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		String description = extractAnnotationDetail(eEnum, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		serializeEEnumLiterals(eEnum.getELiterals(), gen);
		gen.writeStringProperty("type", "string");

		gen.writeEndObject();
	}

	/**
	 * Serializes enum literals
	 */
	private void serializeEEnumLiterals(List<EEnumLiteral> literals, JsonGenerator gen) {
		gen.writeArrayPropertyStart("enum");
		for (EEnumLiteral literal : literals) {
			gen.writeString(literal.getLiteral());
		}
		gen.writeEndArray();
	}

	/**
	 * Serializes an EDataType
	 */
	private void serializeEDataType(EDataType eDataType, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();

		String description = extractAnnotationDetail(eDataType, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		if (description != null) {
			gen.writeStringProperty("description", description);
		}

		String jsonType = mapEDataTypeToJsonType(eDataType);
		gen.writeStringProperty("type", jsonType);

		gen.writeEndObject();
	}

	/**
	 * Maps EDataType to JSON Schema type
	 */
	private String mapEDataTypeToJsonType(EDataType eDataType) {
		if (EcorePackage.Literals.ESTRING.equals(eDataType)) {
			return "string";
		} else if (EcorePackage.Literals.EINT.equals(eDataType)
				|| EcorePackage.Literals.EINTEGER_OBJECT.equals(eDataType)
				|| EcorePackage.Literals.EBIG_INTEGER.equals(eDataType)) {
			return "integer";
		} else if (EcorePackage.Literals.EDOUBLE.equals(eDataType)
				|| EcorePackage.Literals.EDOUBLE_OBJECT.equals(eDataType)
				|| EcorePackage.Literals.EFLOAT.equals(eDataType)
				|| EcorePackage.Literals.EFLOAT_OBJECT.equals(eDataType)
				|| EcorePackage.Literals.EBIG_DECIMAL.equals(eDataType)) {
			return "number";
		} else if (EcorePackage.Literals.EBOOLEAN.equals(eDataType)
				|| EcorePackage.Literals.EBOOLEAN_OBJECT.equals(eDataType)) {
			return "boolean";
		}
		return "string"; // Default fallback
	}

	/**
	 * Writes additionalProperties (can be boolean or object)
	 */
	private void writeAdditionalProperties(String additionalProperties, JsonGenerator gen) {
		if (additionalProperties.contains("{")) {
			// It's a JSON object
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(additionalProperties);
			gen.writeName("additionalProperties");
			gen.writeTree(node);
		} else {
			// It's a boolean
			gen.writeBooleanProperty("additionalProperties", Boolean.parseBoolean(additionalProperties));
		}
	}

	/**
	 * Writes a JSON array or object property (used for anyOf, allOf, etc.)
	 */
	private void writeJsonArrayOrObject(String jsonValue, String propertyName, JsonGenerator gen) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(jsonValue);
		gen.writeName(propertyName);
		gen.writeTree(node);
	}

	/**
	 * Writes a default value (can be string, number, boolean, or object)
	 */
	private void writeDefaultValue(String defaultValue, JsonGenerator gen) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(defaultValue);
		gen.writeName("default");
		gen.writeTree(node);
	}

	/**
	 * Writes additional JSON Schema properties from annotations
	 * (minItems, maxItems, minLength, maxLength, pattern, format, minimum, maximum, etc.)
	 */
	private void writeAdditionalSchemaProperties(EStructuralFeature feature, JsonGenerator gen) {
		// List of JSON Schema properties to write back
		String[] schemaProperties = {
			"default", "minItems", "maxItems", "uniqueItems",
			"minLength", "maxLength", "pattern", "format",
			"minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum",
			"multipleOf", "const", "examples"
		};

		// Special handling for properties that need parsing
		String[] jsonProperties = {"default", "const", "examples"};
		String[] booleanProperties = {"uniqueItems", "exclusiveMinimum", "exclusiveMaximum"};
		String[] numberProperties = {"minItems", "maxItems", "minLength", "maxLength",
									 "minimum", "maximum", "multipleOf"};

		for (String propertyName : schemaProperties) {
			String value = extractAnnotationDetail(feature, JSONSCHEMA_ANNOTATION_SOURCE, propertyName);
			if (value != null) {
				try {
					if (contains(jsonProperties, propertyName)) {
						// Parse as JSON and write as tree
						ObjectMapper mapper = new ObjectMapper();
						JsonNode node = mapper.readTree(value);
						gen.writeName(propertyName);
						gen.writeTree(node);
					} else if (contains(booleanProperties, propertyName)) {
						// Write as boolean
						gen.writeBooleanProperty(propertyName, Boolean.parseBoolean(value));
					} else if (contains(numberProperties, propertyName)) {
						// Parse as number (could be int or double)
						if (value.contains(".")) {
							gen.writeNumberProperty(propertyName, Double.parseDouble(value));
						} else {
							gen.writeNumberProperty(propertyName, Integer.parseInt(value));
						}
					} else {
						// Write as string
						gen.writeStringProperty(propertyName, value);
					}
				} catch (Exception e) {
					// If parsing fails, write as string
					gen.writeStringProperty(propertyName, value);
				}
			}
		}

		// Special handling for title and description from json schema annotations
		String title = extractAnnotationDetail(feature, JSONSCHEMA_ANNOTATION_SOURCE, "title");
		if (title != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readTree(title);
				if (node.isString()) {
					gen.writeStringProperty("title", node.asString());
				} else {
					gen.writeName("title");
					gen.writeTree(node);
				}
			} catch (Exception e) {
				gen.writeStringProperty("title", title);
			}
		}

		String description = extractAnnotationDetail(feature, JSONSCHEMA_ANNOTATION_SOURCE, "description");
		if (description != null && extractAnnotationDetail(feature, GEN_MODEL_ANNOTATION_SOURCE, "documentation") == null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readTree(description);
				if (node.isString()) {
					gen.writeStringProperty("description", node.asString());
				} else {
					gen.writeName("description");
					gen.writeTree(node);
				}
			} catch (Exception e) {
				gen.writeStringProperty("description", description);
			}
		}
	}

	/**
	 * Helper method to check if an array contains a value
	 */
	private boolean contains(String[] array, String value) {
		for (String item : array) {
			if (item.equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Extracts annotation detail from model element
	 */
	private String extractAnnotationDetail(EModelElement modelElement, String source, String detailKey) {
		EAnnotation annotation = modelElement.getEAnnotation(source);
		if (annotation == null) {
			return null;
		}
		return annotation.getDetails().get(detailKey);
	}

	/**
	 * Gets the original name from annotation or falls back to classifier name.
	 * Checks ExtendedMetaData first (EMF standard), then JSONSCHEMA annotation (backwards compatibility).
	 */
	private String getOriginalName(EClassifier classifier) {
		// Check ExtendedMetaData first (EMF standard way)
		String originalName = extractAnnotationDetail(classifier, EXTENDED_METADATA_ANNOTATION_SOURCE, "name");
		if (originalName != null) {
			return originalName;
		}

		// Fall back to JSONSCHEMA annotation for backwards compatibility
		originalName = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "originalName");
		if (originalName != null) {
			return originalName;
		}

		// Finally fall back to the classifier name itself
		return classifier.getName();
	}
}
