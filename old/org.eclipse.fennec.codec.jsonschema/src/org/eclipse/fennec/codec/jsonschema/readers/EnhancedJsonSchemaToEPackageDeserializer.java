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
package org.eclipse.fennec.codec.jsonschema.readers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.ArrayNode;

/**
 * Enhanced JSON Schema to EPackage deserializer with support for:
 * - oneOf discriminated unions (creates abstract base class + concrete subclasses)
 * - Context-specific variant detection (Input/Output/Processor forms)
 * - Structural comparison to detect shared properties
 * - Namespace handling (e.g., $defs/configs)
 *
 * @author Claude Code
 * @since Oct 10, 2025
 */
public class EnhancedJsonSchemaToEPackageDeserializer extends ValueDeserializer<EPackage> {

	private final EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
	private static final String GEN_MODEL_ANNOTATION_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";
	private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";
	private static final String EXTENDED_METADATA_ANNOTATION_SOURCE = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";

	// Threshold for determining if properties are "similar enough" to extract to common base
	private static final double SIMILARITY_THRESHOLD = 0.3; // 30% common properties

	private Map<String, EClassifier> classifierMap = new HashMap<>();
	private Map<String, JsonNode> schemaDefinitions = new HashMap<>();
	private List<DeferredReference> deferredReferences = new ArrayList<>();

	private int artificialClassifierCounter = 0;
	private static final String ARTIFICIAL_CLASSIFIER_PREFIX = "ArtificialClassifier";
	
	
	private final String schemaFeature;
    
    public EnhancedJsonSchemaToEPackageDeserializer(String schemaFeature) {
		this.schemaFeature = schemaFeature;
    	
    }
    
    public EnhancedJsonSchemaToEPackageDeserializer() {
    	this.schemaFeature = "definitions";
    }
    
//    public EnhancedJsonSchemaToEPackageDeserializer(Class<?> targetClass, CodecModule module, CodecModelInfo codecModelInfoService) {
//        this.targetClass = targetClass;
//        this.module = module;
//        this.codecModelInfoService = codecModelInfoService;
//    }

	@Override
	public EPackage deserialize(JsonParser parser, DeserializationContext ctxt) {
		JsonNode rootNode = ctxt.readTree(parser);

		EPackage rootPackage = ecoreFactory.createEPackage();

		// Set package metadata
		if (rootNode.get("$schema") != null) {
			addEAnnotation(rootPackage, JSONSCHEMA_ANNOTATION_SOURCE, "schema", rootNode.get("$schema").asString());
		}
		if (rootNode.get("$id") != null) {
			rootPackage.setNsURI(rootNode.get("$id").asString());
		}
		if (rootNode.get("title") != null) {
			String originalTitle = rootNode.get("title").asString();
			// Store original title in annotation before sanitization
			addEAnnotation(rootPackage, JSONSCHEMA_ANNOTATION_SOURCE, "originalTitle", originalTitle);
			// Use sanitized version for package name
			rootPackage.setName(sanitizeName(originalTitle));
		}

		// Process definitions section (could be $defs, definitions, schemas, etc.)
		if (schemaFeature != null) {
			JsonNode defsNode = rootNode.get(schemaFeature);
			if (defsNode != null) {
				processDefinitions(defsNode, rootPackage, "");
			}
		}

		// Process top-level properties
		JsonNode propertiesNode = rootNode.get("properties");
		if (propertiesNode != null) {
			processTopLevelProperties(propertiesNode, rootNode, rootPackage);
		}

		// Resolve deferred references
		resolveDeferredReferences();

		// Add all classifiers from classifierMap to the package
		// This ensures that subclasses, variant classes, and artificial classes are included
		for (EClassifier classifier : classifierMap.values()) {
			if (!rootPackage.getEClassifiers().contains(classifier)) {
				rootPackage.getEClassifiers().add(classifier);
			}
		}

		return rootPackage;
	}

	/**
	 * Process the $defs section, handling both organizational namespaces
	 * (like "configs") and actual schema definitions.
	 */
	private void processDefinitions(JsonNode defsNode, EPackage parentPackage, String namespacePrefix) {
		for (String defName : defsNode.propertyNames()) {
			JsonNode defNode = defsNode.get(defName);
			String qualifiedName = namespacePrefix.isEmpty() ? defName : namespacePrefix + "/" + defName;

			// Detect if this is an organizational namespace or actual schema
			if (isOrganizationalNamespace(defNode)) {
				// This is a namespace like "configs" - process its children
				processDefinitions(defNode, parentPackage, qualifiedName);
			} else {
				// This is an actual schema definition
				schemaDefinitions.put(qualifiedName, defNode);
				EClassifier classifier = processSchemaDefinition(defNode, defName, qualifiedName);
				if (classifier != null) {
					classifierMap.put(qualifiedName, classifier);
					parentPackage.getEClassifiers().add(classifier);
				}
			}
		}
	}

	/**
	 * Determines if a node is an organizational namespace (contains only object children,
	 * no schema keywords like "type", "properties", "oneOf", etc.)
	 */
	private boolean isOrganizationalNamespace(JsonNode node) {
		if (!node.isObject()) {
			return false;
		}

		// Check if it has any schema keywords
		String[] schemaKeywords = {"type", "properties", "oneOf", "anyOf", "allOf",
									"$ref", "enum", "items", "required", "additionalProperties"};
		for (String keyword : schemaKeywords) {
			if (node.has(keyword)) {
				return false;
			}
		}

		// Check if all children are objects (potential schemas)
		for (String childName : node.propertyNames()) {
			if (!node.get(childName).isObject()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Process a single schema definition and create appropriate EClassifier
	 */
	private EClassifier processSchemaDefinition(JsonNode schemaNode, String name, String qualifiedName) {
		// Handle oneOf - discriminated union pattern
		if (schemaNode.has("oneOf")) {
			return processOneOf(schemaNode, name, qualifiedName);
		}

		// Handle enum
		if (schemaNode.has("enum")) {
			return createEEnum(schemaNode, name, qualifiedName);
		}

		// Handle type-based schemas
		if (schemaNode.has("type")) {
			String type = schemaNode.get("type").asString();
			if ("object".equals(type)) {
				return createEClass(schemaNode, name, qualifiedName);
			} else if ("array".equals(type)) {
				// Top-level arrays are wrapped in a class
				return createArrayWrapperClass(schemaNode, name, qualifiedName);
			}
		}

		return null;
	}

	/**
	 * Process oneOf - handles two patterns:
	 * 1. Discriminated union (minProperties/maxProperties = 1)
	 * 2. Context-specific variants (different schemas for same component)
	 */
	private EClassifier processOneOf(JsonNode schemaNode, String name, String qualifiedName) {
		ArrayNode oneOfArray = (ArrayNode) schemaNode.get("oneOf");

		// Check if this is a discriminated union pattern
		if (isDiscriminatedUnion(schemaNode)) {
			return createDiscriminatedUnion(schemaNode, oneOfArray, name, qualifiedName);
		} else {
			// Context-specific variants - analyze structure
			return createContextSpecificVariants(schemaNode, oneOfArray, name, qualifiedName);
		}
	}

	/**
	 * Detects if oneOf represents a discriminated union
	 * (minProperties = 1, maxProperties = 1, with required keys)
	 */
	private boolean isDiscriminatedUnion(JsonNode schemaNode) {
		return schemaNode.has("minProperties") &&
			   schemaNode.get("minProperties").asInt() == 1 &&
			   schemaNode.has("maxProperties") &&
			   schemaNode.get("maxProperties").asInt() == 1;
	}

	/**
	 * Creates abstract base class + concrete subclasses for discriminated union.
	 * Example: InputNode (abstract) with KafkaInput, FileInput, etc.
	 */
	private EClass createDiscriminatedUnion(JsonNode schemaNode, ArrayNode oneOfArray,
											 String name, String qualifiedName) {
		// Create abstract base class
		EClass abstractBase = ecoreFactory.createEClass();
		String capitalizedName = capitalizeFirst(name);
		abstractBase.setName(capitalizedName);
		abstractBase.setAbstract(true);
		abstractBase.setInterface(false);

		// Store original name if different from capitalized name
		if (!name.equals(capitalizedName)) {
			addEAnnotation(abstractBase, JSONSCHEMA_ANNOTATION_SOURCE, "originalName", name);
			addEAnnotation(abstractBase, EXTENDED_METADATA_ANNOTATION_SOURCE, "name", name);
		}

		// Store the namespace path (e.g., "configs" from "configs/inputNode")
		String namespacePath = extractNamespacePath(qualifiedName);
		if (namespacePath != null && !namespacePath.isEmpty()) {
			addEAnnotation(abstractBase, JSONSCHEMA_ANNOTATION_SOURCE, "namespacePath", namespacePath);
		}

		if (schemaNode.has("description")) {
			addEAnnotation(abstractBase, GEN_MODEL_ANNOTATION_SOURCE,
						  "documentation", schemaNode.get("description").asString());
		}

		addEAnnotation(abstractBase, JSONSCHEMA_ANNOTATION_SOURCE,
					  "discriminatedUnion", "true");

		// Register the abstract base
		classifierMap.put(qualifiedName, abstractBase);

		// Process each oneOf option as a concrete subclass
		for (int i = 0; i < oneOfArray.size(); i++) {
			JsonNode option = oneOfArray.get(i);

			// Extract the discriminator key (the required property name)
			String discriminatorKey = extractDiscriminatorKey(option);
			if (discriminatorKey == null) {
				continue;
			}

			// Get the referenced schema
			JsonNode propertiesNode = option.get("properties");
			if (propertiesNode == null || !propertiesNode.has(discriminatorKey)) {
				continue;
			}

			JsonNode propertySchema = propertiesNode.get(discriminatorKey);
			String subclassName = capitalizeFirst(discriminatorKey) + capitalizeFirst(name);

			// Create concrete subclass
			EClass subclass = createConcreteSubclass(propertySchema, subclassName,
													discriminatorKey, abstractBase);
			if (subclass != null) {
				String subclassQualifiedName = qualifiedName + "/" + discriminatorKey;
				classifierMap.put(subclassQualifiedName, subclass);
			}
		}

		// Add codec.type annotations for feature-based type discrimination
		// This must be done after all subclasses are created and in the classifierMap
		addCodecTypeAnnotationsForDiscriminatedUnion(abstractBase);

		return abstractBase;
	}

	/**
	 * Extracts the discriminator key from a oneOf option.
	 * Example: { "required": ["kafka"], "properties": { "kafka": {...} } } -> "kafka"
	 */
	private String extractDiscriminatorKey(JsonNode option) {
		if (!option.has("required")) {
			return null;
		}

		ArrayNode requiredArray = (ArrayNode) option.get("required");
		if (requiredArray.size() != 1) {
			return null;
		}

		return requiredArray.get(0).asString();
	}

	/**
	 * Creates a concrete subclass for a discriminated union option
	 */
	private EClass createConcreteSubclass(JsonNode propertySchema, String subclassName,
										  String discriminatorKey, EClass abstractBase) {
		// Handle $ref
		if (propertySchema.has("$ref")) {
			String refPath = propertySchema.get("$ref").asString();
			String referencedSchemaName = extractSchemaNameFromRef(refPath);

			// Create the subclass
			EClass subclass = ecoreFactory.createEClass();
			subclass.setName(subclassName);
			subclass.getESuperTypes().add(abstractBase);

			addEAnnotation(subclass, JSONSCHEMA_ANNOTATION_SOURCE,
						  "discriminatorKey", discriminatorKey);
			addEAnnotation(subclass, JSONSCHEMA_ANNOTATION_SOURCE,
						  "configRef", refPath);

			// Defer creating the config reference until all schemas are processed
			deferredReferences.add(new DeferredReference(subclass, referencedSchemaName, "config"));

			classifierMap.put(subclassName, subclass);
			return subclass;
		}

		return null;
	}

	/**
	 * Adds codec.type annotations to a discriminated union EClass.
	 * This enables feature-based type discrimination during deserialization.
	 *
	 * For each discriminated union (e.g., InputNode with children KafkaInputNode, FileInputNode),
	 * this method extracts the discriminator keys (kafka, file, etc.) and builds a type map
	 * that maps property names to EClass names.
	 *
	 * @param unionClass The discriminated union EClass (abstract parent) to annotate
	 */
	private void addCodecTypeAnnotationsForDiscriminatedUnion(EClass unionClass) {
		// Check if this is a discriminated union
		EAnnotation discriminatedAnnotation = unionClass.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
		if (discriminatedAnnotation == null ||
			!"true".equals(discriminatedAnnotation.getDetails().get("discriminatedUnion"))) {
			return;
		}

		// Find all child EClasses of this union
		Map<String, String> typeMap = new HashMap<>();
		for (EClassifier classifier : classifierMap.values()) {
			if (classifier instanceof EClass) {
				EClass childClass = (EClass) classifier;
				// Check if this is a direct child of the union class
				if (childClass.getESuperTypes().contains(unionClass)) {
					// Extract the discriminator key from the child's annotation
					EAnnotation childAnnotation = childClass.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
					if (childAnnotation != null) {
						String discriminatorKey = childAnnotation.getDetails().get("discriminatorKey");
						if (discriminatorKey != null) {
							// Map the discriminator key to the child class name
							typeMap.put(discriminatorKey, childClass.getName());
						}
					}
				}
			}
		}

		// Only add annotations if we found discriminator mappings
		if (!typeMap.isEmpty()) {
			// Create codec.type annotation with feature-based discrimination
			EAnnotation codecTypeAnnotation = ecoreFactory.createEAnnotation();
			codecTypeAnnotation.setSource("codec.type");

			// Set typeKey to "*" for feature-based discrimination
			codecTypeAnnotation.getDetails().put("typeKey", "*");
			
			// Set the type Strategy to NAME since we are adding just the class name			
			codecTypeAnnotation.getDetails().put("strategy", "NAME");

			// Add all type mappings to the annotation details
			for (Map.Entry<String, String> entry : typeMap.entrySet()) {
				codecTypeAnnotation.getDetails().put(entry.getKey(), entry.getValue());
			}

			unionClass.getEAnnotations().add(codecTypeAnnotation);
		}
	}

	/**
	 * Creates classes for context-specific variants.
	 * Analyzes structural similarity and extracts common base if appropriate.
	 */
	private EClass createContextSpecificVariants(JsonNode schemaNode, ArrayNode oneOfArray,
												  String name, String qualifiedName) {
		List<VariantSchema> variants = new ArrayList<>();

		// Parse all variants
		for (int i = 0; i < oneOfArray.size(); i++) {
			JsonNode variantNode = oneOfArray.get(i);
			String title = variantNode.has("title") ? variantNode.get("title").asString() : "Variant" + i;
			variants.add(new VariantSchema(title, variantNode));
		}

		// Analyze structural similarity
		StructuralAnalysis analysis = analyzeStructuralSimilarity(variants);

		// Always create a base class for property-level oneOf (even if empty)
		// This ensures variants have a parent to reference during serialization
		EClass baseClass = ecoreFactory.createEClass();
		String baseClassName = capitalizeFirst(name) + "Base";
		baseClass.setName(baseClassName);
		baseClass.setAbstract(true);

		// Store original name if different from generated name
		String originalBaseName = name;
		if (!originalBaseName.equals(baseClassName)) {
			addEAnnotation(baseClass, JSONSCHEMA_ANNOTATION_SOURCE, "originalName", originalBaseName);
			addEAnnotation(baseClass, EXTENDED_METADATA_ANNOTATION_SOURCE, "name", originalBaseName);
		}

		// Store the namespace path (e.g., "configs" from "configs/kafka")
		String namespacePath = extractNamespacePath(qualifiedName);
		if (namespacePath != null && !namespacePath.isEmpty()) {
			addEAnnotation(baseClass, JSONSCHEMA_ANNOTATION_SOURCE, "namespacePath", namespacePath);
		}

		// Add common properties to base if there are any with significant similarity
		boolean hasCommonProperties = analysis.commonPropertyRatio >= SIMILARITY_THRESHOLD && !analysis.commonProperties.isEmpty();
		if (hasCommonProperties) {
			for (Map.Entry<String, PropertySchema> entry : analysis.commonProperties.entrySet()) {
				EStructuralFeature feature = createStructuralFeature(
					entry.getValue().schema, entry.getKey(), qualifiedName);
				if (feature != null) {
					if (analysis.commonRequiredProperties.contains(entry.getKey())) {
						feature.setLowerBound(1);
					}
					baseClass.getEStructuralFeatures().add(feature);
				}
			}
		}

		addEAnnotation(baseClass, JSONSCHEMA_ANNOTATION_SOURCE, "commonBase", "true");
		classifierMap.put(qualifiedName + "Base", baseClass);

		// Create variant-specific classes
		for (VariantSchema variant : variants) {
			EClass variantClass = ecoreFactory.createEClass();
			String variantName = capitalizeFirst(name) + sanitizeName(variant.title);
			variantClass.setName(variantName);

			// Store original name
			String originalVariantName = name;
			if (!originalVariantName.equals(variantName)) {
				addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE, "originalName", originalVariantName);
				addEAnnotation(variantClass, EXTENDED_METADATA_ANNOTATION_SOURCE, "name", originalVariantName);
			}

			// Store the namespace path (e.g., "configs" from "configs/kafka")
			String namespacePath2 = extractNamespacePath(qualifiedName);
			if (namespacePath2 != null && !namespacePath2.isEmpty()) {
				addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE, "namespacePath", namespacePath2);
			}

			if (baseClass != null) {
				variantClass.getESuperTypes().add(baseClass);
			}

			// Add variant-specific properties (excluding common ones if they were moved to base)
			JsonNode propertiesNode = variant.schema.get("properties");
			if (propertiesNode != null) {
				for (String propName : propertiesNode.propertyNames()) {
					// Only skip if this property was actually moved to the common base
					if (hasCommonProperties && analysis.commonProperties.containsKey(propName)) {
						continue;
					}

					EStructuralFeature feature = createStructuralFeature(
						propertiesNode.get(propName), propName, qualifiedName);
					if (feature != null) {
						JsonNode requiredNode = variant.schema.get("required");
						if (requiredNode != null && arrayContains(requiredNode, propName)) {
							feature.setLowerBound(1);
						}
						variantClass.getEStructuralFeatures().add(feature);
					}
				}
			} else {
				// Handle variants without properties node (e.g., primitive types or objects with additionalProperties)
				handleVariantWithoutProperties(variant.schema, variantClass, qualifiedName);
			}

			// Preserve schema-level attributes from the variant
			if (variant.schema.has("additionalProperties")) {
				JsonNode additionalProps = variant.schema.get("additionalProperties");
				addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE, "additionalProperties",
							  additionalProps.toString());
			}

			addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE, "variant", variant.title);
			classifierMap.put(qualifiedName + "/" + variant.title, variantClass);
		}

		// Always return the base class (we always create it now)
		return baseClass;
	}

	/**
	 * Handles variant schemas that don't have a properties node.
	 * This includes primitive types and objects with additionalProperties.
	 */
	private void handleVariantWithoutProperties(JsonNode schema, EClass variantClass, String contextPath) {
		// Check if this is a primitive type variant (e.g., { "type": "string" })
		if (schema.has("type")) {
			JsonNode typeNode = schema.get("type");
			if (typeNode.isString()) {
				String type = typeNode.asString();

				if ("object".equals(type)) {
					// Object with additionalProperties (map-like structure)
					if (schema.has("additionalProperties")) {
						JsonNode additionalPropsNode = schema.get("additionalProperties");

						// Create an EAttribute or EReference for the map entries
						// For now, we'll add an annotation to indicate this is a map-like structure
						addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE,
									  "additionalProperties", additionalPropsNode.toString());

						// Create a placeholder attribute to represent the dynamic map
						// In a real implementation, this might use EMap or a custom data type
						EAttribute mapAttribute = ecoreFactory.createEAttribute();
						mapAttribute.setName("entries");
						mapAttribute.setEType(EcorePackage.eINSTANCE.getEJavaObject());
						mapAttribute.setLowerBound(0);
						mapAttribute.setUpperBound(-1); // Many values
						addEAnnotation(mapAttribute, JSONSCHEMA_ANNOTATION_SOURCE,
									  "mapEntryType", additionalPropsNode.toString());
						variantClass.getEStructuralFeatures().add(mapAttribute);
					}
				} else if ("array".equals(type)) {
					// Array type - store the items schema
					if (schema.has("items")) {
						JsonNode itemsNode = schema.get("items");
						addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE,
									  "arrayItems", itemsNode.toString());
					}

					// Create a placeholder attribute to represent the array
					EAttribute arrayAttribute = ecoreFactory.createEAttribute();
					arrayAttribute.setName("items");
					arrayAttribute.setEType(EcorePackage.eINSTANCE.getEJavaObject());
					arrayAttribute.setLowerBound(0);
					arrayAttribute.setUpperBound(-1); // Many values
					variantClass.getEStructuralFeatures().add(arrayAttribute);

					// Mark this as an array type variant
					addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE, "arrayType", "true");
				} else {
					// Primitive type - create a "value" attribute
					EAttribute valueAttribute = ecoreFactory.createEAttribute();
					valueAttribute.setName("value");
					valueAttribute.setEType(mapJsonTypeToEcore(type));
					valueAttribute.setLowerBound(1);
					variantClass.getEStructuralFeatures().add(valueAttribute);

					if (schema.has("default")) {
						addEAnnotation(valueAttribute, JSONSCHEMA_ANNOTATION_SOURCE,
									  "default", schema.get("default").toString());
					}
				}
			}
		}
	}

	/**
	 * Analyzes structural similarity across multiple variant schemas
	 */
	private StructuralAnalysis analyzeStructuralSimilarity(List<VariantSchema> variants) {
		StructuralAnalysis analysis = new StructuralAnalysis();

		if (variants.isEmpty()) {
			return analysis;
		}

		// Extract properties from all variants
		List<Map<String, PropertySchema>> allProperties = new ArrayList<>();
		for (VariantSchema variant : variants) {
			Map<String, PropertySchema> props = extractProperties(variant.schema);
			allProperties.add(props);
		}

		// Find intersection of properties (structurally identical)
		Map<String, PropertySchema> intersection = allProperties.get(0);
		for (int i = 1; i < allProperties.size(); i++) {
			intersection = findPropertyIntersection(intersection, allProperties.get(i));
		}

		analysis.commonProperties = intersection;

		// Find common required properties
		List<Set<String>> requiredSets = new ArrayList<>();
		for (VariantSchema variant : variants) {
			Set<String> required = new HashSet<>();
			if (variant.schema.has("required")) {
				ArrayNode reqArray = (ArrayNode) variant.schema.get("required");
				for (JsonNode req : reqArray) {
					required.add(req.asString());
				}
			}
			requiredSets.add(required);
		}

		if (!requiredSets.isEmpty()) {
			Set<String> commonRequired = new HashSet<>(requiredSets.get(0));
			for (int i = 1; i < requiredSets.size(); i++) {
				commonRequired.retainAll(requiredSets.get(i));
			}
			analysis.commonRequiredProperties = commonRequired;
		}

		// Calculate similarity ratio
		int totalPropertyCount = allProperties.stream()
			.mapToInt(Map::size)
			.sum();
		int commonPropertyCount = intersection.size() * variants.size();
		analysis.commonPropertyRatio = totalPropertyCount > 0 ?
			(double) commonPropertyCount / totalPropertyCount : 0.0;

		return analysis;
	}

	/**
	 * Extracts properties from a schema as a map of PropertySchema objects
	 */
	private Map<String, PropertySchema> extractProperties(JsonNode schema) {
		Map<String, PropertySchema> properties = new LinkedHashMap<>();

		if (!schema.has("properties")) {
			return properties;
		}

		JsonNode propertiesNode = schema.get("properties");
		for (String propName : propertiesNode.propertyNames()) {
			properties.put(propName, new PropertySchema(propertiesNode.get(propName)));
		}

		return properties;
	}

	/**
	 * Finds the intersection of two property maps (properties that are structurally identical)
	 */
	private Map<String, PropertySchema> findPropertyIntersection(
			Map<String, PropertySchema> map1, Map<String, PropertySchema> map2) {
		Map<String, PropertySchema> intersection = new LinkedHashMap<>();

		for (Map.Entry<String, PropertySchema> entry : map1.entrySet()) {
			String key = entry.getKey();
			if (map2.containsKey(key)) {
				PropertySchema prop1 = entry.getValue();
				PropertySchema prop2 = map2.get(key);

				// Check if structurally identical
				if (arePropertiesStructurallyEqual(prop1, prop2)) {
					intersection.put(key, prop1);
				}
			}
		}

		return intersection;
	}

	/**
	 * Checks if two property schemas are structurally equal
	 */
	private boolean arePropertiesStructurallyEqual(PropertySchema prop1, PropertySchema prop2) {
		// Simple equality check - can be enhanced for more sophisticated comparison
		return prop1.schema.equals(prop2.schema);
	}

	/**
	 * Creates an EClass from a schema node
	 */
	private EClass createEClass(JsonNode schemaNode, String name, String qualifiedName) {
		EClass eClass = ecoreFactory.createEClass();
		String capitalizedName = capitalizeFirst(name);
		eClass.setName(capitalizedName);

		// Store original name if different from capitalized name
		if (!name.equals(capitalizedName)) {
			addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "originalName", name);
			addEAnnotation(eClass, EXTENDED_METADATA_ANNOTATION_SOURCE, "name", name);
		}

		// Store the namespace path (e.g., "configs" from "configs/kafka")
		String namespacePath = extractNamespacePath(qualifiedName);
		if (namespacePath != null && !namespacePath.isEmpty()) {
			addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "namespacePath", namespacePath);
		}

		if (schemaNode.has("description")) {
			addEAnnotation(eClass, GEN_MODEL_ANNOTATION_SOURCE,
						  "documentation", schemaNode.get("description").asString());
		}

		// Process properties
		JsonNode propertiesNode = schemaNode.get("properties");
		if (propertiesNode != null) {
			Set<String> requiredProps = new HashSet<>();
			if (schemaNode.has("required")) {
				ArrayNode reqArray = (ArrayNode) schemaNode.get("required");
				for (JsonNode req : reqArray) {
					requiredProps.add(req.asString());
				}
			}

			for (String propName : propertiesNode.propertyNames()) {
				EStructuralFeature feature = createStructuralFeature(
					propertiesNode.get(propName), propName, qualifiedName);
				if (feature != null) {
					if (requiredProps.contains(propName)) {
						feature.setLowerBound(1);
					}
					eClass.getEStructuralFeatures().add(feature);
				}
			}
		}

		if (schemaNode.has("additionalProperties")) {
			addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE,
						  "additionalProperties",
						  schemaNode.get("additionalProperties").toString());
		}

		// Handle anyOf constraint
		if (schemaNode.has("anyOf")) {
			handleAnyOf(schemaNode, eClass, qualifiedName);
		}

		// Handle allOf composition
		if (schemaNode.has("allOf")) {
			handleAllOf(schemaNode, eClass, qualifiedName);
		}

		return eClass;
	}

	/**
	 * Creates a wrapper class for top-level arrays
	 */
	private EClass createArrayWrapperClass(JsonNode schemaNode, String name, String qualifiedName) {
		EClass eClass = ecoreFactory.createEClass();
		eClass.setName(capitalizeFirst(name));

		addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "arrayWrapper", "true");

		if (schemaNode.has("items")) {
			EStructuralFeature itemsFeature = createStructuralFeature(
				schemaNode.get("items"), "items", qualifiedName);
			if (itemsFeature != null) {
				itemsFeature.setLowerBound(0);
				itemsFeature.setUpperBound(-1);
				eClass.getEStructuralFeatures().add(itemsFeature);
			}
		}

		return eClass;
	}

	/**
	 * Creates an EEnum from a schema node
	 */
	private EEnum createEEnum(JsonNode schemaNode, String name, String qualifiedName) {
		EEnum eEnum = ecoreFactory.createEEnum();
		String capitalizedName = capitalizeFirst(name);
		eEnum.setName(capitalizedName);

		// Store original name if different from capitalized name
		if (!name.equals(capitalizedName)) {
			addEAnnotation(eEnum, JSONSCHEMA_ANNOTATION_SOURCE, "originalName", name);
			addEAnnotation(eEnum, EXTENDED_METADATA_ANNOTATION_SOURCE, "name", name);
		}

		// Store the namespace path (e.g., "configs" from "configs/someEnum")
		String namespacePath = extractNamespacePath(qualifiedName);
		if (namespacePath != null && !namespacePath.isEmpty()) {
			addEAnnotation(eEnum, JSONSCHEMA_ANNOTATION_SOURCE, "namespacePath", namespacePath);
		}

		ArrayNode enumArray = (ArrayNode) schemaNode.get("enum");
		for (int i = 0; i < enumArray.size(); i++) {
			EEnumLiteral literal = ecoreFactory.createEEnumLiteral();
			String literalValue = enumArray.get(i).asString();
			literal.setName(literalValue);
			literal.setLiteral(literalValue);
			literal.setValue(i);
			eEnum.getELiterals().add(literal);
		}

		if (schemaNode.has("description")) {
			addEAnnotation(eEnum, GEN_MODEL_ANNOTATION_SOURCE,
						  "documentation", schemaNode.get("description").asString());
		}

		return eEnum;
	}

	/**
	 * Creates an EStructuralFeature (attribute or reference) from a property schema
	 */
	private EStructuralFeature createStructuralFeature(JsonNode propertySchema,
													   String name, String contextPath) {
		// Handle $ref
		if (propertySchema.has("$ref")) {
			String refPath = propertySchema.get("$ref").asString();
			String referencedSchema = extractSchemaNameFromRef(refPath);

			EReference reference = ecoreFactory.createEReference();
			reference.setName(name);
			reference.setContainment(true); // Assume containment for now

			addEAnnotation(reference, JSONSCHEMA_ANNOTATION_SOURCE, "ref", refPath);

			// Defer type resolution
			deferredReferences.add(new DeferredReference(reference, referencedSchema, null));

			return reference;
		}

		// Handle arrays and multi-type
		if (propertySchema.has("type")) {
			JsonNode typeNode = propertySchema.get("type");

			// Handle multi-type arrays like ["integer", "string"]
			if (typeNode.isArray()) {
				return handleMultiTypeProperty(propertySchema, typeNode, name, contextPath);
			} else if (typeNode.isString()) {
				String type = typeNode.asString();

				if ("array".equals(type)) {
					if (propertySchema.has("items")) {
						EStructuralFeature itemsFeature = createStructuralFeature(
							propertySchema.get("items"), name, contextPath);
						if (itemsFeature != null) {
							itemsFeature.setLowerBound(0);
							itemsFeature.setUpperBound(-1);
							// Preserve array-specific properties (minItems, maxItems, etc.)
							preserveAdditionalSchemaProperties(propertySchema, itemsFeature);
							return itemsFeature;
						}
					}
				} else if ("object".equals(type)) {
					// Inline object - create artificial class
					String artificialName = ARTIFICIAL_CLASSIFIER_PREFIX + (artificialClassifierCounter++);
					EClass artificialClass = createEClass(propertySchema, artificialName,
														 contextPath + "/" + artificialName);
					addEAnnotation(artificialClass, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");
					classifierMap.put(contextPath + "/" + artificialName, artificialClass);

					EReference reference = ecoreFactory.createEReference();
					reference.setName(name);
					reference.setEType(artificialClass);
					reference.setContainment(true);

					// Preserve default value if present
					if (propertySchema.has("default")) {
						addEAnnotation(reference, JSONSCHEMA_ANNOTATION_SOURCE, "default",
									  propertySchema.get("default").toString());
					}

					return reference;
				} else {
					// Primitive type
					EAttribute attribute = ecoreFactory.createEAttribute();
					attribute.setName(name);
					attribute.setEType(mapJsonTypeToEcore(type));

					// Handle enum
					if (propertySchema.has("enum")) {
						String enumName = ARTIFICIAL_CLASSIFIER_PREFIX + (artificialClassifierCounter++);
						String enumQualifiedName = contextPath + "/" + enumName;
						EEnum eEnum = createEEnum(propertySchema, enumName, enumQualifiedName);
						addEAnnotation(eEnum, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");
						classifierMap.put(enumQualifiedName, eEnum);
						attribute.setEType(eEnum);
					}

					// Preserve additional JSON Schema properties
					preserveAdditionalSchemaProperties(propertySchema, attribute);

					return attribute;
				}
			}
		}

		// Handle oneOf at property level
		if (propertySchema.has("oneOf")) {
			// Create artificial union type
			String unionName = ARTIFICIAL_CLASSIFIER_PREFIX + (artificialClassifierCounter++);
			EClass unionClass = (EClass) processOneOf(propertySchema, unionName,
													  contextPath + "/" + unionName);
			if (unionClass != null) {
				// Mark the union class as artificial
				addEAnnotation(unionClass, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");

				// Preserve property-level schema attributes on the base class
				if (propertySchema.has("default")) {
					addEAnnotation(unionClass, JSONSCHEMA_ANNOTATION_SOURCE, "default",
								  propertySchema.get("default").toString());
				}

				// Mark all variant subclasses as artificial too
				for (EClassifier classifier : classifierMap.values()) {
					if (classifier instanceof EClass) {
						EClass eClass = (EClass) classifier;
						// Check if this is a variant of the union class
						if (eClass.getESuperTypes().contains(unionClass)) {
							addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");
						}
					}
				}

				EReference reference = ecoreFactory.createEReference();
				reference.setName(name);
				reference.setEType(unionClass);
				reference.setContainment(true);
				return reference;
			}
		}

		return null;
	}

	/**
	 * Process top-level properties (creates a main class if needed)
	 */
	private void processTopLevelProperties(JsonNode propertiesNode, JsonNode rootSchema, EPackage ePackage) {
		// Create a root class to hold top-level properties
		String rootClassName = ePackage.getName();
		if (rootClassName == null || rootClassName.isEmpty()) {
			rootClassName = "Root";
		}

		EClass rootClass = ecoreFactory.createEClass();
		rootClass.setName(capitalizeFirst(rootClassName));

		// Extract required properties from root schema
		Set<String> requiredProps = new HashSet<>();
		if (rootSchema.has("required")) {
			ArrayNode reqArray = (ArrayNode) rootSchema.get("required");
			for (JsonNode req : reqArray) {
				requiredProps.add(req.asString());
			}
		}

		// Add description if present
		if (rootSchema.has("description")) {
			addEAnnotation(rootClass, GEN_MODEL_ANNOTATION_SOURCE,
						  "documentation", rootSchema.get("description").asString());
		}

		// Add additionalProperties annotation if present
		if (rootSchema.has("additionalProperties")) {
			addEAnnotation(rootClass, JSONSCHEMA_ANNOTATION_SOURCE,
						  "additionalProperties",
						  rootSchema.get("additionalProperties").toString());
		}

		// Process each top-level property
		for (String propName : propertiesNode.propertyNames()) {
			JsonNode propertySchema = propertiesNode.get(propName);
			EStructuralFeature feature = createStructuralFeature(propertySchema, propName, "");
			if (feature != null) {
				// Set lowerBound to 1 if the property is required
				if (requiredProps.contains(propName)) {
					feature.setLowerBound(1);
				}
				rootClass.getEStructuralFeatures().add(feature);
			}
		}

		addEAnnotation(rootClass, JSONSCHEMA_ANNOTATION_SOURCE, "rootClass", "true");

		// Add the root class to the package
		ePackage.getEClassifiers().add(rootClass);
		classifierMap.put("", rootClass);
	}

	/**
	 * Resolves all deferred references after all schemas have been processed
	 */
	private void resolveDeferredReferences() {
		for (DeferredReference deferredRef : deferredReferences) {
			EClassifier referencedType = classifierMap.get(deferredRef.targetSchemaName);

			if (referencedType != null) {
				// Handle allOf inheritance
				if (deferredRef instanceof DeferredAllOfReference) {
					if (referencedType instanceof EClass && deferredRef.owningClass != null) {
						EClass superType = (EClass) referencedType;
						// Add as supertype if not already present
						if (!deferredRef.owningClass.getESuperTypes().contains(superType)) {
							deferredRef.owningClass.getESuperTypes().add(superType);
						}
					}
					continue;
				}

				// Handle regular EReference type resolution
				if (deferredRef.feature instanceof EReference) {
					EReference reference = (EReference) deferredRef.feature;
					reference.setEType(referencedType);
					// Note: codec.type annotations are NOT copied here.
					// CodecModelInfoService automatically inherits them from the target EClass
				}

				// If this is a config reference for a discriminated union subclass,
				// we might want to copy properties instead of just referencing
				if (deferredRef.owningClass != null && deferredRef.featureName != null) {
					// Create the config reference
					EReference configRef = ecoreFactory.createEReference();
					configRef.setName(deferredRef.featureName);
					configRef.setEType(referencedType);
					configRef.setContainment(true);
					configRef.setLowerBound(1);
					configRef.setUpperBound(1);
					deferredRef.owningClass.getEStructuralFeatures().add(configRef);
				}
			} else {
				System.err.println("Warning: Could not resolve reference to schema: " +
								 deferredRef.targetSchemaName);
			}
		}
	}

	/**
	 * Extracts schema name from a $ref path
	 * Example: "#/$defs/configs/kafka" -> "configs/kafka"
	 * Example: "#/definitions/Person" -> "Person"
	 */
	private String extractSchemaNameFromRef(String refPath) {
		if (refPath.startsWith("#/")) {
			refPath = refPath.substring(2);
		}

		// Remove the schema feature prefix if present
		if (schemaFeature != null && refPath.startsWith(schemaFeature + "/")) {
			refPath = refPath.substring(schemaFeature.length() + 1);
		}

		return refPath;
	}

	/**
	 * Handles multi-type properties like "type": ["integer", "string"]
	 * Creates an artificial union class with variants for each type
	 */
	private EStructuralFeature handleMultiTypeProperty(JsonNode propertySchema, JsonNode typeArray,
													   String name, String contextPath) {
		// Create artificial base class for the union
		String unionBaseName = ARTIFICIAL_CLASSIFIER_PREFIX + (artificialClassifierCounter++);
		String unionQualifiedName = contextPath + "/" + unionBaseName;

		EClass unionBase = ecoreFactory.createEClass();
		unionBase.setName(unionBaseName);
		unionBase.setAbstract(true);
		unionBase.setInterface(false);

		addEAnnotation(unionBase, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");
		addEAnnotation(unionBase, JSONSCHEMA_ANNOTATION_SOURCE, "multiType", "true");

		// Store the original type array as annotation
		addEAnnotation(unionBase, JSONSCHEMA_ANNOTATION_SOURCE, "typeArray", typeArray.toString());

		// Store default value on the base class if present
		if (propertySchema.has("default")) {
			addEAnnotation(unionBase, JSONSCHEMA_ANNOTATION_SOURCE, "default",
						  propertySchema.get("default").toString());
		}

		// Preserve other schema properties on the base class
		String[] schemaProperties = {
			"minLength", "maxLength", "pattern", "format",
			"minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum",
			"multipleOf", "const", "title", "description", "examples"
		};

		for (String propertyName : schemaProperties) {
			if (propertySchema.has(propertyName)) {
				addEAnnotation(unionBase, JSONSCHEMA_ANNOTATION_SOURCE, propertyName,
							  propertySchema.get(propertyName).toString());
			}
		}

		classifierMap.put(unionQualifiedName, unionBase);

		// Create a variant class for each type in the array
		ArrayNode typeArrayNode = (ArrayNode) typeArray;
		for (int i = 0; i < typeArrayNode.size(); i++) {
			String variantType = typeArrayNode.get(i).asString();
			String variantName = unionBaseName + "Variant" + i;

			EClass variantClass = ecoreFactory.createEClass();
			variantClass.setName(variantName);
			variantClass.getESuperTypes().add(unionBase);

			addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");
			addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE, "variant", variantType);
			addEAnnotation(variantClass, JSONSCHEMA_ANNOTATION_SOURCE, "variantIndex", String.valueOf(i));

			// Create a "value" attribute with the variant's type
			EAttribute valueAttr = ecoreFactory.createEAttribute();
			valueAttr.setName("value");
			valueAttr.setEType(mapJsonTypeToEcore(variantType));
			valueAttr.setLowerBound(1);
			variantClass.getEStructuralFeatures().add(valueAttr);

			classifierMap.put(unionQualifiedName + "/Variant" + i, variantClass);
		}

		// Create the reference to the union base
		EReference reference = ecoreFactory.createEReference();
		reference.setName(name);
		reference.setEType(unionBase);
		reference.setContainment(true);

		return reference;
	}

	/**
	 * Maps JSON Schema type to Ecore EDataType
	 */
	private EDataType mapJsonTypeToEcore(String jsonType) {
		EcorePackage ecore = EcorePackage.eINSTANCE;
		switch (jsonType) {
			case "string": return ecore.getEString();
			case "integer": return ecore.getEInt();
			case "number": return ecore.getEDouble();
			case "boolean": return ecore.getEBoolean();
			default: return ecore.getEJavaObject();
		}
	}

	/**
	 * Preserves additional JSON Schema properties as annotations
	 * (minItems, maxItems, minLength, maxLength, pattern, format, minimum, maximum, etc.)
	 */
	private void preserveAdditionalSchemaProperties(JsonNode schema, EStructuralFeature feature) {
		// List of JSON Schema properties to preserve
		String[] schemaProperties = {
			"default", "minItems", "maxItems", "uniqueItems",
			"minLength", "maxLength", "pattern", "format",
			"minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum",
			"multipleOf", "const", "title", "description", "examples"
		};

		for (String propertyName : schemaProperties) {
			if (schema.has(propertyName)) {
				JsonNode propertyValue = schema.get(propertyName);
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, propertyName, propertyValue.toString());
			}
		}
	}

	/**
	 * Adds an annotation to an EModelElement
	 */
	private void addEAnnotation(EModelElement element, String source, String key, String value) {
		EAnnotation annotation = element.getEAnnotation(source);
		if (annotation == null) {
			annotation = ecoreFactory.createEAnnotation();
			annotation.setSource(source);
			element.getEAnnotations().add(annotation);
		}
		annotation.getDetails().put(key, value);
	}

	/**
	 * Checks if an array node contains a specific string value
	 */
	private boolean arrayContains(JsonNode arrayNode, String value) {
		if (!arrayNode.isArray()) {
			return false;
		}
		for (JsonNode element : arrayNode) {
			if (element.isString() && element.asString().equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sanitizes a name for use as an Ecore identifier
	 */
	private String sanitizeName(String name) {
		return name.replaceAll("[^a-zA-Z0-9_]", "_");
	}

	/**
	 * Capitalizes the first letter of a string
	 */
	private String capitalizeFirst(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	/**
	 * Extracts the namespace path from a qualified name.
	 * Example: "configs/kafka" -> "configs"
	 * Example: "schemas/input/kafka" -> "schemas/input"
	 * Example: "kafka" -> null
	 */
	private String extractNamespacePath(String qualifiedName) {
		if (qualifiedName == null || !qualifiedName.contains("/")) {
			return null;
		}
		int lastSlash = qualifiedName.lastIndexOf('/');
		return qualifiedName.substring(0, lastSlash);
	}

	/**
	 * Handles anyOf constraint in JSON Schema.
	 * If anyOf only specifies different required combinations, store as annotation.
	 * If anyOf has different schemas, create variants (like oneOf but less strict).
	 */
	private void handleAnyOf(JsonNode schemaNode, EClass eClass, String qualifiedName) {
		ArrayNode anyOfArray = (ArrayNode) schemaNode.get("anyOf");

		// Check if anyOf only contains required constraints (no additional properties/type changes)
		boolean onlyRequiredConstraints = true;
		for (JsonNode option : anyOfArray) {
			if (option.has("properties") || option.has("type") || option.has("$ref")) {
				onlyRequiredConstraints = false;
				break;
			}
		}

		if (onlyRequiredConstraints) {
			// This is just specifying alternative required combinations
			// Store as annotation for validation/documentation
			addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "anyOf", anyOfArray.toString());
		} else {
			// This defines alternative schemas - we need to handle this structurally
			// For now, store as annotation and log a warning
			addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "anyOf", anyOfArray.toString());
			System.err.println("Warning: Complex anyOf with different schemas detected in " + qualifiedName +
							 ". This may require manual modeling.");
		}
	}

	/**
	 * Handles allOf composition in JSON Schema.
	 * Merges all schemas into the current class, using inheritance for $refs.
	 */
	private void handleAllOf(JsonNode schemaNode, EClass eClass, String qualifiedName) {
		ArrayNode allOfArray = (ArrayNode) schemaNode.get("allOf");

		Set<String> allRequiredProps = new HashSet<>();

		for (JsonNode allOfOption : allOfArray) {
			// Handle $ref - add as supertype
			if (allOfOption.has("$ref")) {
				String refPath = allOfOption.get("$ref").asString();
				String referencedSchemaName = extractSchemaNameFromRef(refPath);

				// Defer the supertype resolution
				deferredReferences.add(new DeferredAllOfReference(eClass, referencedSchemaName));
			}

			// Handle inline schema - merge properties
			if (allOfOption.has("properties")) {
				JsonNode propsNode = allOfOption.get("properties");
				for (String propName : propsNode.propertyNames()) {
					// Check if property already exists (from parent schema's properties)
					boolean exists = false;
					for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
						if (feature.getName().equals(propName)) {
							exists = true;
							break;
						}
					}

					if (!exists) {
						EStructuralFeature feature = createStructuralFeature(
							propsNode.get(propName), propName, qualifiedName);
						if (feature != null) {
							eClass.getEStructuralFeatures().add(feature);
						}
					}
				}
			}

			// Collect required properties from all allOf schemas
			if (allOfOption.has("required")) {
				ArrayNode reqArray = (ArrayNode) allOfOption.get("required");
				for (JsonNode req : reqArray) {
					allRequiredProps.add(req.asString());
				}
			}
		}

		// Apply combined required properties
		for (String requiredProp : allRequiredProps) {
			for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
				if (feature.getName().equals(requiredProp)) {
					feature.setLowerBound(1);
					break;
				}
			}
		}

		// Store the original allOf for serialization
		addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "allOf", allOfArray.toString());
	}

	// Helper classes

	private static class VariantSchema {
		String title;
		JsonNode schema;

		VariantSchema(String title, JsonNode schema) {
			this.title = title;
			this.schema = schema;
		}
	}

	private static class PropertySchema {
		JsonNode schema;

		PropertySchema(JsonNode schema) {
			this.schema = schema;
		}
	}

	private static class StructuralAnalysis {
		Map<String, PropertySchema> commonProperties = new LinkedHashMap<>();
		Set<String> commonRequiredProperties = new HashSet<>();
		double commonPropertyRatio = 0.0;
	}

	private static class DeferredReference {
		EClass owningClass;
		EStructuralFeature feature;
		String targetSchemaName;
		String featureName;

		DeferredReference(EReference feature, String targetSchemaName, String featureName) {
			this.feature = feature;
			this.targetSchemaName = targetSchemaName;
			this.featureName = featureName;
		}

		DeferredReference(EClass owningClass, String targetSchemaName, String featureName) {
			this.owningClass = owningClass;
			this.targetSchemaName = targetSchemaName;
			this.featureName = featureName;
		}
	}

	/**
	 * Deferred reference for allOf inheritance
	 */
	private static class DeferredAllOfReference extends DeferredReference {
		DeferredAllOfReference(EClass owningClass, String targetSchemaName) {
			super(owningClass, targetSchemaName, null);
		}
	}
}
