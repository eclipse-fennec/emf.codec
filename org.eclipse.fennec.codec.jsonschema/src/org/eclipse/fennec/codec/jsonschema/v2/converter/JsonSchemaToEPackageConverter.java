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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.fennec.codec.constants.AnnotationSources;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;

/**
 * Enhanced converter from JSON Schema to EMF EPackage.
 * <p>
 * Supports:
 * <ul>
 *   <li>JSON Schema document → EPackage with $id → nsURI, title → name</li>
 *   <li>object type → EClass</li>
 *   <li>string type with enum → EEnum</li>
 *   <li>primitive types → EDataType / EAttribute</li>
 *   <li>$ref → EReference</li>
 *   <li>allOf → ESuperTypes (inheritance)</li>
 *   <li>anyOf/oneOf → abstract parent with subclasses</li>
 *   <li>Discriminated unions (minProperties/maxProperties = 1)</li>
 *   <li>Context-specific variants</li>
 *   <li>Namespace paths (e.g., configs/kafka)</li>
 *   <li>Multi-type properties</li>
 *   <li>Top-level properties → rootClass</li>
 * </ul>
 * </p>
 *
 * @author Data In Motion
 * @since 2025
 */
public class JsonSchemaToEPackageConverter {

	private static final String ARTIFICIAL_CLASSIFIER_PREFIX = "ArtificialClassifier";

	/** Threshold for determining if properties are "similar enough" to extract to common base */
	private static final double SIMILARITY_THRESHOLD = 0.3;

	private final EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;

	private Map<String, EClassifier> classifierMap;
	private Map<String, JsonNode> schemaDefinitions;
	private Map<EReference, String> missingRefMap;
	private Map<EClassifier, List<String>> anyOfRefMap;
	private Map<EClass, List<String>> allOfRefMap;
	private Map<String, EClassifier> cachedClassifiers;
	private Map<String, EClassifier> anchorMap;  // Maps $anchor names to EClassifiers
	private List<DeferredReference> deferredReferences;
	private List<DeferredAnyOfReference> deferredAnyOfReferences;
	private List<JsonSchemaConversionDiagnostic> diagnostics;
	private int artificialClassifierCounter;
	private String schemaFeature;

	/**
	 * Converts JSON Schema from input stream to EPackage.
	 *
	 * @param inputStream the input stream containing JSON Schema
	 * @param schemaFeature the key for definitions (e.g., "definitions", "$defs", "schemas")
	 * @return the created EPackage
	 * @throws IOException if reading fails
	 */
	public EPackage convert(InputStream inputStream, String schemaFeature) throws IOException {
		this.schemaFeature = schemaFeature;
		resetState();

		ObjectMapper mapper = JsonMapper.builder().build();
		JsonNode rootNode = mapper.readTree(inputStream);

		return convertNode(rootNode);
	}

	/**
	 * Converts JSON Schema from JsonNode to EPackage.
	 *
	 * @param rootNode the root JSON Schema node
	 * @param schemaFeature the key for definitions
	 * @return the created EPackage
	 */
	public EPackage convert(JsonNode rootNode, String schemaFeature) {
		this.schemaFeature = schemaFeature;
		resetState();
		return convertNode(rootNode);
	}

	/**
	 * Converts an EMap of Schema objects to an EPackage.
	 * <p>
	 * This method is used by OpenAPI resource to convert the deserialized
	 * schemas map into an EMF-native EPackage representation.
	 * </p>
	 * <p>
	 * Each entry has a key (schema name) and value (Schema object).
	 * The Schema object contains the JSON Schema properties that can be
	 * converted to EClassifiers.
	 * </p>
	 *
	 * @param schemaMap the map of schema entries from OpenAPI components/schemas
	 * @return the created EPackage, or null if conversion fails
	 */
	public EPackage convertFromSchemaMap(EMap<String, ? extends EObject> schemaMap) {
		if (schemaMap == null || schemaMap.isEmpty()) {
			return null;
		}

		resetState();

		EPackage ePackage = ecoreFactory.createEPackage();
		ePackage.setName("schemas");
		ePackage.setNsURI("http://generated/schemas");
		ePackage.setNsPrefix("schemas");

		// Process each schema entry
		for (var entry : schemaMap.entrySet()) {
			String schemaName = entry.getKey();
			EObject schemaValue = entry.getValue();

			if (schemaName == null || schemaValue == null) {
				continue;
			}

			// Convert Schema EObject to EClass
			EClassifier classifier = convertSchemaObjectToClassifier(schemaValue, schemaName);
			if (classifier != null) {
				classifierMap.put(schemaName, classifier);
				ePackage.getEClassifiers().add(classifier);
			}
		}

		// Resolve deferred references
		resolveDeferredReferences();
		resolveMissingReferences();
		resolveAnyOfReferences();
		resolveAllOfReferences();
		resolveAnyOfPropertyReferences();

		return ePackage;
	}

	/**
	 * Converts a Schema EObject to an EClassifier.
	 * <p>
	 * The Schema EObject comes from the OpenAPI model and contains
	 * properties like type, properties, items, etc.
	 * </p>
	 */
	private EClassifier convertSchemaObjectToClassifier(EObject schema, String name) {
		// Get the type feature
		EStructuralFeature typeFeature = schema.eClass().getEStructuralFeature("type");
		String type = typeFeature != null ? (String) schema.eGet(typeFeature) : null;

		// Check for enum
		EStructuralFeature enumFeature = schema.eClass().getEStructuralFeature("enum");
		if (enumFeature != null) {
			@SuppressWarnings("unchecked")
			EList<String> enumValues = (EList<String>) schema.eGet(enumFeature);
			if (enumValues != null && !enumValues.isEmpty()) {
				return createEEnumFromValues(enumValues, name);
			}
		}

		// Handle object type -> EClass
		if ("object".equals(type) || type == null) {
			return createEClassFromSchemaObject(schema, name);
		}

		return null;
	}

	/**
	 * Creates an EEnum from a list of enum values.
	 */
	private EEnum createEEnumFromValues(EList<String> enumValues, String name) {
		EEnum eEnum = ecoreFactory.createEEnum();
		eEnum.setName(capitalizeFirst(name));

		int ordinal = 0;
		for (String value : enumValues) {
			EEnumLiteral literal = ecoreFactory.createEEnumLiteral();
			literal.setLiteral(value);
			literal.setName(value);
			literal.setValue(ordinal++);
			eEnum.getELiterals().add(literal);
		}

		return eEnum;
	}

	/**
	 * Creates an EClass from a Schema EObject.
	 */
	private EClass createEClassFromSchemaObject(EObject schema, String name) {
		EClass eClass = ecoreFactory.createEClass();
		eClass.setName(capitalizeFirst(name));

		// Get description
		EStructuralFeature descFeature = schema.eClass().getEStructuralFeature("description");
		if (descFeature != null) {
			String description = (String) schema.eGet(descFeature);
			if (description != null && !description.isEmpty()) {
				addEAnnotation(eClass, AnnotationSources.GEN_MODEL, "documentation", description);
			}
		}

		// Get required fields
		EStructuralFeature requiredFeature = schema.eClass().getEStructuralFeature("required");
		Set<String> requiredFields = new HashSet<>();
		if (requiredFeature != null) {
			@SuppressWarnings("unchecked")
			EList<String> required = (EList<String>) schema.eGet(requiredFeature);
			if (required != null) {
				requiredFields.addAll(required);
			}
		}

		// Get properties - can be EMap or EList with key/value features
		EStructuralFeature propsFeature = schema.eClass().getEStructuralFeature("properties");
		if (propsFeature != null) {
			Object propsValue = schema.eGet(propsFeature);

			if (propsValue instanceof EMap<?, ?> propsMap) {
				// Handle EMap<String, Schema> directly
				for (var entry : propsMap.entrySet()) {
					String propName = (String) entry.getKey();
					EObject propSchema = (EObject) entry.getValue();

					if (propName != null && propSchema != null) {
						EStructuralFeature feature = createFeatureFromSchemaObject(propSchema, propName);
						if (feature != null) {
							if (requiredFields.contains(propName)) {
								feature.setLowerBound(1);
							}
							eClass.getEStructuralFeatures().add(feature);
						}
					}
				}
			} else if (propsValue instanceof EList<?> propsList) {
				// Handle EList of map entries (for test compatibility)
				for (Object propEntry : propsList) {
					if (propEntry instanceof Map.Entry<?, ?> entry) {
						String propName = (String) entry.getKey();
						EObject propSchema = (EObject) entry.getValue();

						if (propName != null && propSchema != null) {
							EStructuralFeature feature = createFeatureFromSchemaObject(propSchema, propName);
							if (feature != null) {
								if (requiredFields.contains(propName)) {
									feature.setLowerBound(1);
								}
								eClass.getEStructuralFeatures().add(feature);
							}
						}
					} else if (propEntry instanceof EObject entryObj) {
						// Handle EObject with key/value structural features
						String propName = (String) entryObj.eGet(entryObj.eClass().getEStructuralFeature("key"));
						EObject propSchema = (EObject) entryObj.eGet(
							entryObj.eClass().getEStructuralFeature("value"));

						if (propName != null && propSchema != null) {
							EStructuralFeature feature = createFeatureFromSchemaObject(propSchema, propName);
							if (feature != null) {
								if (requiredFields.contains(propName)) {
									feature.setLowerBound(1);
								}
								eClass.getEStructuralFeatures().add(feature);
							}
						}
					}
				}
			}
		}

		return eClass;
	}

	/**
	 * Creates an EStructuralFeature from a Schema EObject property.
	 */
	private EStructuralFeature createFeatureFromSchemaObject(EObject propSchema, String name) {
		EStructuralFeature typeFeature = propSchema.eClass().getEStructuralFeature("type");
		String type = typeFeature != null ? (String) propSchema.eGet(typeFeature) : null;

		// Check for $ref
		EStructuralFeature refFeature = propSchema.eClass().getEStructuralFeature("ref");
		if (refFeature != null) {
			String ref = (String) propSchema.eGet(refFeature);
			if (ref != null && !ref.isEmpty()) {
				EReference reference = ecoreFactory.createEReference();
				reference.setName(name);
				reference.setContainment(true);
				String refName = extractSchemaNameFromRef(ref);
				if (classifierMap.containsKey(refName)) {
					reference.setEType(classifierMap.get(refName));
				} else {
					deferredReferences.add(new DeferredTypeReference(reference, refName));
				}
				return reference;
			}
		}

		// Handle array type
		if ("array".equals(type)) {
			EStructuralFeature itemsFeature = propSchema.eClass().getEStructuralFeature("items");
			if (itemsFeature != null) {
				EObject items = (EObject) propSchema.eGet(itemsFeature);
				if (items != null) {
					EStructuralFeature feature = createFeatureFromSchemaObject(items, name);
					if (feature != null) {
						feature.setUpperBound(-1);
						feature.setLowerBound(0);
					}
					return feature;
				}
			}
			// Fallback: array of objects
			EAttribute attr = ecoreFactory.createEAttribute();
			attr.setName(name);
			attr.setEType(EcorePackage.Literals.EJAVA_OBJECT);
			attr.setUpperBound(-1);
			return attr;
		}

		// Handle object type - create reference
		if ("object".equals(type)) {
			String nestedClassName = capitalizeFirst(name);
			EClass nestedClass = createEClassFromSchemaObject(propSchema, nestedClassName);
			classifierMap.put(nestedClassName, nestedClass);

			EReference reference = ecoreFactory.createEReference();
			reference.setName(name);
			reference.setEType(nestedClass);
			reference.setContainment(true);
			return reference;
		}

		// Handle primitive types - create attribute
		EAttribute attribute = ecoreFactory.createEAttribute();
		attribute.setName(name);
		attribute.setEType(mapJsonTypeToEcore(type != null ? type : "string"));
		return attribute;
	}

	private void resetState() {
		classifierMap = new HashMap<>();
		schemaDefinitions = new HashMap<>();
		missingRefMap = new HashMap<>();
		anyOfRefMap = new HashMap<>();
		allOfRefMap = new HashMap<>();
		cachedClassifiers = new HashMap<>();
		anchorMap = new HashMap<>();
		deferredReferences = new ArrayList<>();
		deferredAnyOfReferences = new ArrayList<>();
		diagnostics = new ArrayList<>();
		artificialClassifierCounter = 0;
	}

	/**
	 * Returns the diagnostics collected during the last conversion.
	 * <p>
	 * This list contains warnings about unsupported features, partial support,
	 * unresolved references, and other conversion issues.
	 * </p>
	 *
	 * @return list of diagnostics (never null, may be empty)
	 */
	public List<JsonSchemaConversionDiagnostic> getDiagnostics() {
		return diagnostics != null ? new ArrayList<>(diagnostics) : new ArrayList<>();
	}
	
	/**
	 * Converts a JSON Schema document to a single EClass.
	 *
	 * @param schemaNode the JSON Schema node representing the class
	 * @param name the name for the EClass; if null, derived from the "title" field or defaults to "EClass"
	 * @return the created EClass, or null if conversion yields a non-class classifier
	 */
	public EClass convertToEClass(InputStream inputStream, String name) {
		resetState();

		ObjectMapper mapper = JsonMapper.builder().build();
		JsonNode rootNode = mapper.readTree(inputStream);
		return convertToEClass(rootNode, name);
	}

	/**
	 * Converts a JSON Schema document to a single EClass.
	 *
	 * @param schemaNode the JSON Schema node representing the class
	 * @param name the name for the EClass; if null, derived from the "title" field or defaults to "EClass"
	 * @return the created EClass, or null if conversion yields a non-class classifier
	 */
	public EClass convertToEClass(JsonNode schemaNode, String name) {
		resetState();
		if (name == null) {
			name = schemaNode.has("title") ? schemaNode.get("title").asString() : "EClass";
		}
		// Process $defs first so $ref entries can be resolved
		JsonNode defsNode = schemaFeature != null ? schemaNode.get(schemaFeature) : null;
		if (defsNode == null) {
			for (String feature : new String[]{"$defs", "definitions", "schemas"}) {
				defsNode = schemaNode.get(feature);
				if (defsNode != null) {
					this.schemaFeature = feature;
					break;
				}
			}
		}
		if (defsNode != null) {
			for (String defName : defsNode.propertyNames()) {
				JsonNode defNode = defsNode.get(defName);
				EClassifier defClassifier = processSchemaDefinition(defNode, defName, defName);
				if (defClassifier != null) {
					classifierMap.put(defName, defClassifier);
				}
			}
		}
		EClassifier classifier = processSchemaDefinition(schemaNode, name, name);
		resolveDeferredReferences();
		resolveMissingReferences();
		resolveAnyOfReferences();
		resolveAllOfReferences();
		resolveAnyOfPropertyReferences();
		if (!(classifier instanceof EClass eClass)) {
			return null;
		}

		// Capture document-level metadata as EClass annotations
		if (schemaNode.has("$schema")) {
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "schema", schemaNode.get("$schema").asString());
		}
		if (schemaNode.has("$id")) {
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "id", schemaNode.get("$id").asString());
		}
		// Preserve title as originalTitle so writeEClassDocumentMetadata can round-trip it.
		// createEClass already stores the name in originalName/ExtendedMetaData when it
		// differs from the capitalized form, but originalTitle is the dedicated writer key.
		if (schemaNode.has("title")) {
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "originalTitle", schemaNode.get("title").asString());
		}
		// Note: description → GEN_MODEL annotation is already handled by processSchemaDefinition → createEClass

		return eClass;
	}

	private EPackage convertNode(JsonNode rootNode) {
		EPackage ePackage = ecoreFactory.createEPackage();

		// $schema annotation
		if (rootNode.has("$schema")) {
			addEAnnotation(ePackage, AnnotationSources.JSONSCHEMA, "schema", rootNode.get("$schema").asString());
		}

		// $id → nsURI
		if (rootNode.has("$id")) {
			ePackage.setNsURI(rootNode.get("$id").asString());
		}

		// title → name
		if (rootNode.has("title")) {
			String originalTitle = rootNode.get("title").asString();
			addEAnnotation(ePackage, AnnotationSources.JSONSCHEMA, "originalTitle", originalTitle);
			ePackage.setName(sanitizeName(originalTitle));
			if (ePackage.getNsPrefix() == null || ePackage.getNsPrefix().isEmpty()) {
				ePackage.setNsPrefix(deriveNsPrefix(originalTitle));
			}
		}

		// description → documentation
		if (rootNode.has("description")) {
			addEAnnotation(ePackage, AnnotationSources.GEN_MODEL, "documentation", rootNode.get("description").asString());
		}

		// Process definitions section
		if (schemaFeature != null) {
			JsonNode defsNode = rootNode.get(schemaFeature);
			if (defsNode != null) {
				processDefinitions(defsNode, ePackage, "");
			}
		} else {
			// Try common schema feature names
			boolean foundDefinitions = false;
			for (String feature : new String[]{"definitions", "$defs", "schemas"}) {
				JsonNode defsNode = rootNode.get(feature);
				if (defsNode != null) {
					this.schemaFeature = feature;
					processDefinitions(defsNode, ePackage, "");
					foundDefinitions = true;
					break;
				}
			}

			// If no definitions section found, check if rootNode IS the definitions
			// This handles embedded schemas like OpenAPI components/schemas content
			if (!foundDefinitions && isDirectDefinitionsNode(rootNode)) {
				processDefinitions(rootNode, ePackage, "");
			}
		}

		// Process top-level properties (creates rootClass)
		JsonNode propertiesNode = rootNode.get("properties");
		if (propertiesNode != null) {
			processTopLevelProperties(propertiesNode, rootNode, ePackage);
		}

		// Resolve deferred references
		resolveDeferredReferences();
		resolveMissingReferences();
		resolveAnyOfReferences();
		resolveAllOfReferences();
		// Must run after resolveAllOfReferences so inheritance hierarchy is fully resolved
		resolveAnyOfPropertyReferences();

		// Add all classifiers from classifierMap to the package
		for (EClassifier classifier : classifierMap.values()) {
			if (!ePackage.getEClassifiers().contains(classifier)) {
				ePackage.getEClassifiers().add(classifier);
			}
		}

		return ePackage;
	}

	/**
	 * Process the definitions section, handling both organizational namespaces
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
					if (!parentPackage.getEClassifiers().contains(classifier)) {
						parentPackage.getEClassifiers().add(classifier);
					}
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
	 * Determines if a node represents direct schema definitions (without a wrapper key).
	 * <p>
	 * This handles cases like OpenAPI's components/schemas content where the JSON
	 * passed IS the schemas definitions directly:
	 * <pre>
	 * {
	 *   "Person": { "type": "object", "properties": {...} },
	 *   "Address": { "type": "object", "properties": {...} }
	 * }
	 * </pre>
	 * </p>
	 * <p>
	 * Returns true if:
	 * <ul>
	 *   <li>The node is an object</li>
	 *   <li>It has at least one child</li>
	 *   <li>All children are objects that look like schema definitions</li>
	 *   <li>The node itself is NOT a schema (no top-level schema keywords)</li>
	 * </ul>
	 * </p>
	 */
	private boolean isDirectDefinitionsNode(JsonNode node) {
		if (!node.isObject()) {
			return false;
		}

		// Must have at least one child
		if (node.propertyNames().isEmpty()) {
			return false;
		}

		// Check that the root doesn't look like a single schema
		// (i.e., doesn't have schema keywords at root level)
		String[] rootSchemaKeywords = {"$schema", "$id", "title", "description"};
		for (String keyword : rootSchemaKeywords) {
			if (node.has(keyword)) {
				return false;
			}
		}

		// Check if all children look like schema definitions
		for (String childName : node.propertyNames()) {
			JsonNode child = node.get(childName);
			if (!child.isObject()) {
				return false;
			}
			// A schema definition should have at least one schema keyword
			if (!looksLikeSchemaDefinition(child)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if a node looks like a schema definition (has schema keywords).
	 */
	private boolean looksLikeSchemaDefinition(JsonNode node) {
		String[] schemaKeywords = {"type", "properties", "oneOf", "anyOf", "allOf",
								   "$ref", "enum", "items", "required", "additionalProperties"};
		for (String keyword : schemaKeywords) {
			if (node.has(keyword)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Process a single schema definition and create appropriate EClassifier
	 */
	private EClassifier processSchemaDefinition(JsonNode schemaNode, String name, String qualifiedName) {
		// Check for unsupported keywords and emit diagnostics
		diagnostics.addAll(JsonSchemaKeywords.checkForUnsupportedKeywords(schemaNode, qualifiedName));

		// Handle oneOf - discriminated union or variants
		if (schemaNode.has("oneOf")) {
			// A oneOf whose options are all $refs to independently-defined classes
			// models an abstract supertype: the named definition becomes an abstract
			// EClass and each referenced class becomes a concrete subtype.
			if (isAllRefOneOf(schemaNode.get("oneOf"))) {
				return createAbstractRefUnion(schemaNode, name, qualifiedName);
			}
			return processOneOf(schemaNode, name, qualifiedName);
		}

		// Handle enum
		if (schemaNode.has("enum")) {
			return createEEnum(schemaNode, name, qualifiedName);
		}

		// Handle allOf composition
		if (schemaNode.has("allOf")) {
			return createClassWithAllOf(schemaNode, name, qualifiedName);
		}

		// Handle type-based schemas
		if (schemaNode.has("type")) {
			JsonNode typeNode = schemaNode.get("type");

			if (typeNode.isString()) {
				String type = typeNode.asString();
				if ("object".equals(type)) {
					return createEClass(schemaNode, name, qualifiedName);
				} else if ("array".equals(type)) {
					return createArrayWrapperClass(schemaNode, name, qualifiedName);
				} else {
					return createEDataType(schemaNode, name);
				}
			} else {
				// Array of types → EDataType with multiple types
				return createEDataType(schemaNode, name);
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

		if (isDiscriminatedUnion(schemaNode)) {
			return createDiscriminatedUnion(schemaNode, oneOfArray, name, qualifiedName);
		} else {
			return createContextSpecificVariants(schemaNode, oneOfArray, name, qualifiedName);
		}
	}

	/**
	 * Returns {@code true} when every entry of the given oneOf array is a bare
	 * {@code $ref} (no inline schema). Such a oneOf models an abstract supertype
	 * over independently-defined classes.
	 */
	private boolean isAllRefOneOf(JsonNode oneOfArray) {
		if (oneOfArray == null || !oneOfArray.isArray() || oneOfArray.isEmpty()) {
			return false;
		}
		for (JsonNode entry : oneOfArray) {
			if (!entry.has("$ref")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates an abstract EClass for a {@code oneOf} of {@code $ref}s and records
	 * the referenced classes as deferred subtypes (resolved in
	 * {@link #resolveAnyOfReferences()} once all definitions exist).
	 */
	private EClass createAbstractRefUnion(JsonNode schemaNode, String name, String qualifiedName) {
		EClass abstractBase = ecoreFactory.createEClass();
		abstractBase.setName(capitalizeFirst(name));
		abstractBase.setAbstract(true);

		if (schemaNode.has("description")) {
			addEAnnotation(abstractBase, AnnotationSources.GEN_MODEL,
					"documentation", schemaNode.get("description").asString());
		}
		addEAnnotation(abstractBase, AnnotationSources.JSONSCHEMA, "oneOfRefUnion", "true");

		classifierMap.put(qualifiedName, abstractBase);

		List<String> subTypeNames = new ArrayList<>();
		for (JsonNode entry : schemaNode.get("oneOf")) {
			subTypeNames.add(extractSchemaNameFromRef(entry.get("$ref").asString()));
		}
		anyOfRefMap.put(abstractBase, subTypeNames);

		return abstractBase;
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
	 */
	private EClass createDiscriminatedUnion(JsonNode schemaNode, ArrayNode oneOfArray,
											 String name, String qualifiedName) {
		EClass abstractBase = ecoreFactory.createEClass();
		String capitalizedName = capitalizeFirst(name);
		abstractBase.setName(capitalizedName);
		abstractBase.setAbstract(true);
		abstractBase.setInterface(false);

		// Store original name if different
		if (!name.equals(capitalizedName)) {
			addEAnnotation(abstractBase, AnnotationSources.JSONSCHEMA, "originalName", name);
			addEAnnotation(abstractBase, AnnotationSources.EXTENDED_METADATA, "name", name);
		}

		// Store namespace path
		String namespacePath = extractNamespacePath(qualifiedName);
		if (namespacePath != null && !namespacePath.isEmpty()) {
			addEAnnotation(abstractBase, AnnotationSources.JSONSCHEMA, "namespacePath", namespacePath);
		}

		if (schemaNode.has("description")) {
			addEAnnotation(abstractBase, AnnotationSources.GEN_MODEL,
						  "documentation", schemaNode.get("description").asString());
		}

		addEAnnotation(abstractBase, AnnotationSources.JSONSCHEMA, "discriminatedUnion", "true");

		classifierMap.put(qualifiedName, abstractBase);

		// Process each oneOf option as a concrete subclass
		for (int i = 0; i < oneOfArray.size(); i++) {
			JsonNode option = oneOfArray.get(i);
			String discriminatorKey = extractDiscriminatorKey(option);
			if (discriminatorKey == null) {
				continue;
			}

			JsonNode propertiesNode = option.get("properties");
			if (propertiesNode == null || !propertiesNode.has(discriminatorKey)) {
				continue;
			}

			JsonNode propertySchema = propertiesNode.get(discriminatorKey);
			String subclassName = capitalizeFirst(discriminatorKey) + capitalizeFirst(name);

			EClass subclass = createConcreteSubclass(propertySchema, subclassName,
													discriminatorKey, abstractBase, qualifiedName);
			if (subclass != null) {
				String subclassQualifiedName = qualifiedName + "/" + discriminatorKey;
				classifierMap.put(subclassQualifiedName, subclass);
			}
		}

		// Add codec.type annotations for feature-based type discrimination
		addCodecTypeAnnotationsForDiscriminatedUnion(abstractBase);

		return abstractBase;
	}

	/**
	 * Extracts the discriminator key from a oneOf option.
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
										  String discriminatorKey, EClass abstractBase,
										  String parentQualifiedName) {
		if (propertySchema.has("$ref")) {
			String refPath = propertySchema.get("$ref").asString();
			String referencedSchemaName = extractSchemaNameFromRef(refPath);

			EClass subclass = ecoreFactory.createEClass();
			subclass.setName(subclassName);
			subclass.getESuperTypes().add(abstractBase);

			addEAnnotation(subclass, AnnotationSources.JSONSCHEMA, "discriminatorKey", discriminatorKey);
			addEAnnotation(subclass, AnnotationSources.JSONSCHEMA, "configRef", refPath);

			// Defer creating the config reference
			deferredReferences.add(new DeferredConfigReference(subclass, referencedSchemaName, "config"));

			classifierMap.put(subclassName, subclass);
			return subclass;
		}

		return null;
	}

	/**
	 * Adds codec.type annotations to a discriminated union EClass.
	 */
	private void addCodecTypeAnnotationsForDiscriminatedUnion(EClass unionClass) {
		EAnnotation discriminatedAnnotation = unionClass.getEAnnotation(AnnotationSources.JSONSCHEMA);
		if (discriminatedAnnotation == null ||
			!"true".equals(discriminatedAnnotation.getDetails().get("discriminatedUnion"))) {
			return;
		}

		Map<String, String> typeMap = new HashMap<>();
		for (EClassifier classifier : classifierMap.values()) {
			if (classifier instanceof EClass childClass) {
				if (childClass.getESuperTypes().contains(unionClass)) {
					EAnnotation childAnnotation = childClass.getEAnnotation(AnnotationSources.JSONSCHEMA);
					if (childAnnotation != null) {
						String discriminatorKey = childAnnotation.getDetails().get("discriminatorKey");
						if (discriminatorKey != null) {
							typeMap.put(discriminatorKey, childClass.getName());
						}
					}
				}
			}
		}

		if (!typeMap.isEmpty()) {
			EAnnotation codecTypeAnnotation = ecoreFactory.createEAnnotation();
			codecTypeAnnotation.setSource("codec.type");
			codecTypeAnnotation.getDetails().put("typeKey", "*");
			codecTypeAnnotation.getDetails().put("strategy", "NAME");

			for (Map.Entry<String, String> entry : typeMap.entrySet()) {
				codecTypeAnnotation.getDetails().put(entry.getKey(), entry.getValue());
			}

			unionClass.getEAnnotations().add(codecTypeAnnotation);
		}
	}

	/**
	 * Creates classes for context-specific variants.
	 */
	private EClass createContextSpecificVariants(JsonNode schemaNode, ArrayNode oneOfArray,
												  String name, String qualifiedName) {
		List<VariantSchema> variants = new ArrayList<>();

		for (int i = 0; i < oneOfArray.size(); i++) {
			JsonNode variantNode = oneOfArray.get(i);
			String title = variantNode.has("title") ? variantNode.get("title").asString() : "Variant" + i;
			variants.add(new VariantSchema(title, variantNode));
		}

		StructuralAnalysis analysis = analyzeStructuralSimilarity(variants);

		// Create base class
		EClass baseClass = ecoreFactory.createEClass();
		String baseClassName = capitalizeFirst(name) + "Base";
		baseClass.setName(baseClassName);
		baseClass.setAbstract(true);

		String originalBaseName = name;
		if (!originalBaseName.equals(baseClassName)) {
			addEAnnotation(baseClass, AnnotationSources.JSONSCHEMA, "originalName", originalBaseName);
			addEAnnotation(baseClass, AnnotationSources.EXTENDED_METADATA, "name", originalBaseName);
		}

		String namespacePath = extractNamespacePath(qualifiedName);
		if (namespacePath != null && !namespacePath.isEmpty()) {
			addEAnnotation(baseClass, AnnotationSources.JSONSCHEMA, "namespacePath", namespacePath);
		}

		// Add common properties to base if there are enough
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

		addEAnnotation(baseClass, AnnotationSources.JSONSCHEMA, "commonBase", "true");
		classifierMap.put(qualifiedName + "Base", baseClass);

		// Create variant-specific classes
		for (VariantSchema variant : variants) {
			EClass variantClass = ecoreFactory.createEClass();
			String variantName = capitalizeFirst(name) + sanitizeName(variant.title);
			variantClass.setName(variantName);

			String originalVariantName = name;
			if (!originalVariantName.equals(variantName)) {
				addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA, "originalName", originalVariantName);
				addEAnnotation(variantClass, AnnotationSources.EXTENDED_METADATA, "name", originalVariantName);
			}

			if (namespacePath != null && !namespacePath.isEmpty()) {
				addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA, "namespacePath", namespacePath);
			}

			variantClass.getESuperTypes().add(baseClass);

			JsonNode propertiesNode = variant.schema.get("properties");
			if (propertiesNode != null) {
				for (String propName : propertiesNode.propertyNames()) {
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
				handleVariantWithoutProperties(variant.schema, variantClass, qualifiedName);
			}

			if (variant.schema.has("additionalProperties")) {
				JsonNode additionalProps = variant.schema.get("additionalProperties");
				addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA, "additionalProperties",
							  additionalProps.toString());
			}

			addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA, "variant", variant.title);
			classifierMap.put(qualifiedName + "/" + variant.title, variantClass);
		}

		return baseClass;
	}

	/**
	 * Handles variant schemas that don't have a properties node.
	 */
	private void handleVariantWithoutProperties(JsonNode schema, EClass variantClass, String contextPath) {
		if (schema.has("type")) {
			JsonNode typeNode = schema.get("type");
			if (typeNode.isString()) {
				String type = typeNode.asString();

				if ("object".equals(type)) {
					if (schema.has("additionalProperties")) {
						JsonNode additionalPropsNode = schema.get("additionalProperties");
						addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA,
									  "additionalProperties", additionalPropsNode.toString());

						EAttribute mapAttribute = ecoreFactory.createEAttribute();
						mapAttribute.setName("entries");
						mapAttribute.setEType(EcorePackage.eINSTANCE.getEJavaObject());
						mapAttribute.setLowerBound(0);
						mapAttribute.setUpperBound(-1);
						addEAnnotation(mapAttribute, AnnotationSources.JSONSCHEMA,
									  "mapEntryType", additionalPropsNode.toString());
						variantClass.getEStructuralFeatures().add(mapAttribute);
					}
				} else if ("array".equals(type)) {
					if (schema.has("items")) {
						JsonNode itemsNode = schema.get("items");
						addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA,
									  "arrayItems", itemsNode.toString());
					}

					EAttribute arrayAttribute = ecoreFactory.createEAttribute();
					arrayAttribute.setName("items");
					arrayAttribute.setEType(EcorePackage.eINSTANCE.getEJavaObject());
					arrayAttribute.setLowerBound(0);
					arrayAttribute.setUpperBound(-1);
					variantClass.getEStructuralFeatures().add(arrayAttribute);

					addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA, "arrayType", "true");
				} else {
					EAttribute valueAttribute = ecoreFactory.createEAttribute();
					valueAttribute.setName("value");
					valueAttribute.setEType(mapJsonTypeToEcore(type));
					valueAttribute.setLowerBound(1);
					variantClass.getEStructuralFeatures().add(valueAttribute);

					if (schema.has("default")) {
						addEAnnotation(valueAttribute, AnnotationSources.JSONSCHEMA,
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

		List<Map<String, PropertySchema>> allProperties = new ArrayList<>();
		for (VariantSchema variant : variants) {
			Map<String, PropertySchema> props = extractProperties(variant.schema);
			allProperties.add(props);
		}

		Map<String, PropertySchema> intersection = allProperties.get(0);
		for (int i = 1; i < allProperties.size(); i++) {
			intersection = findPropertyIntersection(intersection, allProperties.get(i));
		}

		analysis.commonProperties = intersection;

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

		int totalPropertyCount = allProperties.stream()
			.mapToInt(Map::size)
			.sum();
		int commonPropertyCount = intersection.size() * variants.size();
		analysis.commonPropertyRatio = totalPropertyCount > 0 ?
			(double) commonPropertyCount / totalPropertyCount : 0.0;

		return analysis;
	}

	/**
	 * Extracts properties from a schema
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
	 * Finds the intersection of two property maps
	 */
	private Map<String, PropertySchema> findPropertyIntersection(
			Map<String, PropertySchema> map1, Map<String, PropertySchema> map2) {
		Map<String, PropertySchema> intersection = new LinkedHashMap<>();

		for (Map.Entry<String, PropertySchema> entry : map1.entrySet()) {
			String key = entry.getKey();
			if (map2.containsKey(key)) {
				PropertySchema prop1 = entry.getValue();
				PropertySchema prop2 = map2.get(key);

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
		return prop1.schema.equals(prop2.schema);
	}

	private EClass createEClass(JsonNode classNode, String name, String qualifiedName) {
		if (cachedClassifiers.containsKey(classNode.toString()) && name.startsWith(ARTIFICIAL_CLASSIFIER_PREFIX)) {
			return (EClass) cachedClassifiers.get(classNode.toString());
		}

		EClass eClass = ecoreFactory.createEClass();
		String capitalizedName = capitalizeFirst(name);
		eClass.setName(capitalizedName);

		if (!name.equals(capitalizedName)) {
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "originalName", name);
			addEAnnotation(eClass, AnnotationSources.EXTENDED_METADATA, "name", name);
		}

		String namespacePath = extractNamespacePath(qualifiedName);
		if (namespacePath != null && !namespacePath.isEmpty()) {
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "namespacePath", namespacePath);
		}

		if (classNode.has("description")) {
			addEAnnotation(eClass, AnnotationSources.GEN_MODEL, "documentation", classNode.get("description").asString());
		}

		// Handle x-abstract extension
		if (classNode.has("x-abstract") && classNode.get("x-abstract").asBoolean()) {
			eClass.setAbstract(true);
		}

		// Handle x-interface extension
		if (classNode.has("x-interface") && classNode.get("x-interface").asBoolean()) {
			eClass.setInterface(true);
			eClass.setAbstract(true); // Interfaces are implicitly abstract in EMF
		}

		JsonNode requiredNode = classNode.get("required");
		JsonNode propertiesNode = classNode.get("properties");

		if (propertiesNode != null) {
			for (String property : propertiesNode.propertyNames()) {
				EStructuralFeature feature = createStructuralFeature(propertiesNode.get(property), property, qualifiedName);
				if (feature != null) {
					// 'required' only raises the lower bound of single-valued features; for
					// multi-valued features the lower bound is governed by minItems.
					if (requiredNode != null && arrayContains(requiredNode, feature.getName())
							&& feature.getUpperBound() == 1) {
						feature.setLowerBound(1);
					}
					eClass.getEStructuralFeatures().add(feature);
				}
			}
		}

		if (classNode.has("additionalProperties")) {
			JsonNode additionalProps = classNode.get("additionalProperties");
			String value = additionalProps.isBoolean() ? String.valueOf(additionalProps.asBoolean()) : additionalProps.toPrettyString();
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "additionalProperties", value);
		}

		// Handle anyOf constraint
		if (classNode.has("anyOf")) {
			handleAnyOf(classNode, eClass, qualifiedName);
		}

		// Preserve additional schema properties ($comment, deprecated, etc.)
		preserveAdditionalSchemaProperties(classNode, eClass);

		// Capture $anchor for later reference resolution
		if (classNode.has("$anchor")) {
			String anchor = classNode.get("$anchor").asString();
			anchorMap.put(anchor, eClass);
			// Also add to classifierMap so refs can be resolved by anchor name
			classifierMap.put(anchor, eClass);
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "anchor", anchor);
		}

		cachedClassifiers.put(classNode.toString(), eClass);
		return eClass;
	}

	private EClass createClassWithAllOf(JsonNode classNode, String name, String qualifiedName) {
		JsonNode allOfNode = classNode.get("allOf");
		List<String> parentNames = new LinkedList<>();
		EClass eClass = null;
		Set<String> allRequiredProps = new HashSet<>();

		if (allOfNode.isArray()) {
			for (int i = 0; i < allOfNode.size(); i++) {
				JsonNode allOf = allOfNode.get(i);

				// Collect $ref entries as parent supertypes
				if (allOf.has("$ref")) {
					String refPath = allOf.get("$ref").asString();
					String referencedSchemaName = extractSchemaNameFromRef(refPath);
					parentNames.add(referencedSchemaName);
				}

				// Merge properties from ALL inline schemas (not just the first one)
				if (allOf.has("properties")) {
					if (eClass == null) {
						// First inline schema with properties — create the EClass from it
						eClass = createEClass(allOf, name, qualifiedName);
					} else {
						// Subsequent inline schemas — merge their properties into the existing class
						JsonNode propsNode = allOf.get("properties");
						for (String propName : propsNode.propertyNames()) {
							// Skip if property already exists (avoid duplicates)
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
				}

				// Accumulate required properties from all allOf elements
				if (allOf.has("required")) {
					ArrayNode reqArray = (ArrayNode) allOf.get("required");
					for (JsonNode req : reqArray) {
						allRequiredProps.add(req.asString());
					}
				}
			}
		}

		if (eClass == null) {
			eClass = ecoreFactory.createEClass();
			String capitalizedName = capitalizeFirst(name);
			eClass.setName(capitalizedName);

			if (!name.equals(capitalizedName)) {
				addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "originalName", name);
				addEAnnotation(eClass, AnnotationSources.EXTENDED_METADATA, "name", name);
			}

			String namespacePath = extractNamespacePath(qualifiedName);
			if (namespacePath != null && !namespacePath.isEmpty()) {
				addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "namespacePath", namespacePath);
			}
		}

		// Handle x-abstract extension on the top-level classNode
		if (classNode.has("x-abstract") && classNode.get("x-abstract").asBoolean()) {
			eClass.setAbstract(true);
		}

		// Handle x-interface extension on the top-level classNode
		if (classNode.has("x-interface") && classNode.get("x-interface").asBoolean()) {
			eClass.setInterface(true);
			eClass.setAbstract(true);
		}

		// Apply combined required properties from all allOf elements
		for (String requiredProp : allRequiredProps) {
			for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
				if (feature.getName().equals(requiredProp)) {
					feature.setLowerBound(1);
					break;
				}
			}
		}

		if (!parentNames.isEmpty()) {
			allOfRefMap.put(eClass, parentNames);
		}

		// Store allOf annotation
		addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "allOf", allOfNode.toString());

		return eClass;
	}

	private EClass createArrayWrapperClass(JsonNode schemaNode, String name, String qualifiedName) {
		EClass eClass = ecoreFactory.createEClass();
		eClass.setName(capitalizeFirst(name));

		addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "arrayWrapper", "true");
		addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "source", "TopLevelArray");
		addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "artificial", "true");

		if (schemaNode.has("description")) {
			addEAnnotation(eClass, AnnotationSources.GEN_MODEL, "documentation", schemaNode.get("description").asString());
		}

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

	private EEnum createEEnum(JsonNode enumNode, String name, String qualifiedName) {
		if (cachedClassifiers.containsKey(enumNode.toString())) {
			return (EEnum) cachedClassifiers.get(enumNode.toString());
		}

		EEnum eEnum = ecoreFactory.createEEnum();
		String capitalizedName = capitalizeFirst(name);
		eEnum.setName(capitalizedName);

		if (!name.equals(capitalizedName)) {
			addEAnnotation(eEnum, AnnotationSources.JSONSCHEMA, "originalName", name);
			addEAnnotation(eEnum, AnnotationSources.EXTENDED_METADATA, "name", name);
		}

		String namespacePath = extractNamespacePath(qualifiedName);
		if (namespacePath != null && !namespacePath.isEmpty()) {
			addEAnnotation(eEnum, AnnotationSources.JSONSCHEMA, "namespacePath", namespacePath);
		}

		JsonNode enumValues = enumNode.get("enum");
		String defaultValue = enumNode.has("default") ? enumNode.get("default").asString() : null;

		// Collect literal names, reordering so the default value comes first
		List<String> literalNames = new ArrayList<>();
		for (int e = 0; e < enumValues.size(); e++) {
			literalNames.add(enumValues.get(e).asString());
		}
		if (defaultValue != null && literalNames.contains(defaultValue)) {
			literalNames.remove(defaultValue);
			literalNames.add(0, defaultValue);
		}

		for (int e = 0; e < literalNames.size(); e++) {
			EEnumLiteral literal = ecoreFactory.createEEnumLiteral();
			String literalName = literalNames.get(e);
			literal.setLiteral(literalName);
			literal.setName(literalName);
			literal.setValue(e);
			eEnum.getELiterals().add(literal);
		}

		if (enumNode.has("description")) {
			addEAnnotation(eEnum, AnnotationSources.GEN_MODEL, "documentation", enumNode.get("description").asString());
		}

		cachedClassifiers.put(enumNode.toString(), eEnum);
		return eEnum;
	}

	private EDataType createEDataType(JsonNode dtNode, String name) {
		if (cachedClassifiers.containsKey(dtNode.toString())) {
			return (EDataType) cachedClassifiers.get(dtNode.toString());
		}

		EDataType dt = ecoreFactory.createEDataType();
		dt.setName(capitalizeFirst(name));

		if (dtNode.has("description")) {
			addEAnnotation(dt, AnnotationSources.GEN_MODEL, "documentation", dtNode.get("description").asString());
		}

		JsonNode typeNode = dtNode.get("type");
		if (typeNode != null && typeNode.isArray()) {
			StringBuilder sb = new StringBuilder();
			for (int t = 0; t < typeNode.size(); t++) {
				if (t > 0) sb.append(",");
				sb.append(typeNode.get(t).asString());
			}
			addEAnnotation(dt, AnnotationSources.JSONSCHEMA, "dataType", sb.toString());
			dt.setInstanceClass(Object.class);
			dt.setInstanceClassName("java.lang.Object");
		} else if (typeNode != null) {
			String jsonType = typeNode.asString();
			switch (jsonType) {
				case "string":
					dt.setInstanceClass(String.class);
					dt.setInstanceClassName("java.lang.String");
					break;
				case "integer":
					dt.setInstanceClass(Integer.class);
					dt.setInstanceClassName("java.lang.Integer");
					break;
				case "number":
					dt.setInstanceClass(Double.class);
					dt.setInstanceClassName("java.lang.Double");
					break;
				case "boolean":
					dt.setInstanceClass(Boolean.class);
					dt.setInstanceClassName("java.lang.Boolean");
					break;
				default:
					dt.setInstanceClass(Object.class);
					dt.setInstanceClassName("java.lang.Object");
					break;
			}
		}

		cachedClassifiers.put(dtNode.toString(), dt);
		return dt;
	}

	/**
	 * Creates an EStructuralFeature (attribute or reference) from a property schema
	 */
	private EStructuralFeature createStructuralFeature(JsonNode propertyNode, String name, String contextPath) {
		EStructuralFeature feature = null;

		// Handle $ref
		if (propertyNode.has("$ref")) {
			feature = createRefFeature(propertyNode, name);
		}
		// Handle type-based feature
		else if (propertyNode.has("type")) {
			feature = createTypedFeature(propertyNode, name, contextPath);
		}
		// Handle enum without explicit type
		else if (propertyNode.has("enum")) {
			feature = createEnumFeature(propertyNode, name, contextPath);
		}
		// Handle const without type
		else if (propertyNode.has("const")) {
			String inferredType = getJsonTypeFromConstNode(propertyNode.get("const"));
			feature = createFeatureFromJsonType(inferredType, name, propertyNode, contextPath);
		}
		// Handle anyOf/oneOf at property level
		else if (propertyNode.has("anyOf")) {
			feature = createMultiValueReference(propertyNode.get("anyOf"), name, contextPath);
			if (feature != null) {
				addEAnnotation(feature, AnnotationSources.JSONSCHEMA, "source", "anyOf");
			}
		}
		else if (propertyNode.has("oneOf")) {
			feature = createOneOfFeature(propertyNode, name, contextPath);
		}

		// Fallback: a property with no type/$ref/enum/const/anyOf/oneOf accepts any
		// JSON value → model it as an EJavaObject attribute rather than dropping it.
		if (feature == null) {
			EAttribute attribute = ecoreFactory.createEAttribute();
			attribute.setName(name);
			attribute.setEType(EcorePackage.Literals.EJAVA_OBJECT);
			preserveAdditionalSchemaProperties(propertyNode, attribute);
			feature = attribute;
		}

		// Handle x-containment extension
		if (feature instanceof EReference ref && propertyNode.has("x-containment")) {
			ref.setContainment(propertyNode.get("x-containment").asBoolean());
		}

		// Add common annotations
		if (feature != null) {
			addCommonAnnotations(feature, propertyNode, false);
		}

		return feature;
	}

	private EStructuralFeature createRefFeature(JsonNode propertyNode, String name) {
		EReference reference = ecoreFactory.createEReference();
		reference.setName(name);
		reference.setContainment(false); // $ref typically means non-containment reference

		String refPath = propertyNode.get("$ref").asString();
		addEAnnotation(reference, AnnotationSources.JSONSCHEMA, "ref", refPath);

		String refName = extractSchemaNameFromRef(refPath);

		if (classifierMap.containsKey(refName)) {
			reference.setEType(classifierMap.get(refName));
		} else {
			deferredReferences.add(new DeferredTypeReference(reference, refName));
		}

		return reference;
	}

	private EStructuralFeature createTypedFeature(JsonNode propertyNode, String name, String contextPath) {
		JsonNode typeNode = propertyNode.get("type");

		if (typeNode.isString()) {
			return createFeatureFromJsonType(typeNode.asString(), name, propertyNode, contextPath);
		} else {
			// Check for nullable pattern: ["someType", "null"] or ["null", "someType"]
			String nullableType = extractNullableType(typeNode);
			if (nullableType != null) {
				EStructuralFeature feature = createFeatureFromJsonType(nullableType, name, propertyNode, contextPath);
				if (feature != null) {
					feature.setLowerBound(0);
				}
				return feature;
			}
			// Multiple types → multi-type property
			return handleMultiTypeProperty(propertyNode, typeNode, name, contextPath);
		}
	}

	/**
	 * Checks if a type array represents a nullable type pattern: exactly two entries
	 * where one is "null" and the other is a concrete type.
	 *
	 * @return the non-null type name, or null if this is not a nullable pattern
	 */
	private String extractNullableType(JsonNode typeArray) {
		if (!typeArray.isArray() || typeArray.size() != 2) {
			return null;
		}
		String first = typeArray.get(0).asString();
		String second = typeArray.get(1).asString();
		if ("null".equals(first)) {
			return second;
		}
		if ("null".equals(second)) {
			return first;
		}
		return null;
	}

	private EStructuralFeature createEnumFeature(JsonNode propertyNode, String name, String contextPath) {
		return createFeatureFromJsonType("string", name, propertyNode, contextPath);
	}

	private EStructuralFeature createOneOfFeature(JsonNode propertyNode, String name, String contextPath) {
		// Check if all oneOf entries are $ref to known classes with a common abstract supertype
		EClass commonSuperType = resolveCommonSuperTypeFromRefs(propertyNode.get("oneOf"));
		if (commonSuperType != null) {
			EReference reference = ecoreFactory.createEReference();
			reference.setName(name);
			reference.setEType(commonSuperType);
			// Containment will be set by x-containment handling in createStructuralFeature
			return reference;
		}

		String unionName = ARTIFICIAL_CLASSIFIER_PREFIX + (artificialClassifierCounter++);
		EClass unionClass = (EClass) processOneOf(propertyNode, unionName, contextPath + "/" + unionName);
		if (unionClass != null) {
			addEAnnotation(unionClass, AnnotationSources.JSONSCHEMA, "artificial", "true");

			if (propertyNode.has("default")) {
				addEAnnotation(unionClass, AnnotationSources.JSONSCHEMA, "default",
							  propertyNode.get("default").toString());
			}

			// Mark variant subclasses as artificial too
			for (EClassifier classifier : classifierMap.values()) {
				if (classifier instanceof EClass eClass) {
					if (eClass.getESuperTypes().contains(unionClass)) {
						addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "artificial", "true");
					}
				}
			}

			EReference reference = ecoreFactory.createEReference();
			reference.setName(name);
			reference.setEType(unionClass);
			reference.setContainment(true);
			return reference;
		}
		return null;
	}

	/**
	 * Checks if all entries in a oneOf/anyOf array are $ref to known classes
	 * that share a common abstract supertype. Returns that supertype, or null.
	 * <p>
	 * Checks both resolved ESuperTypes and unresolved allOfRefMap entries,
	 * since supertypes may not be resolved yet during definition processing.
	 */
	private EClass resolveCommonSuperTypeFromRefs(JsonNode arrayNode) {
		if (arrayNode == null || !arrayNode.isArray() || arrayNode.isEmpty()) {
			return null;
		}

		List<EClass> referencedClasses = new ArrayList<>();
		for (JsonNode entry : arrayNode) {
			if (!entry.has("$ref")) {
				return null; // Not all entries are $ref
			}
			String refPath = entry.get("$ref").asString();
			String refName = extractSchemaNameFromRef(refPath);
			EClassifier classifier = classifierMap.get(refName);
			if (!(classifier instanceof EClass eClass)) {
				return null; // Referenced class not found or not an EClass
			}
			referencedClasses.add(eClass);
		}

		if (referencedClasses.size() < 2) {
			return null;
		}

		// Collect parent names for each class (from allOfRefMap or resolved supertypes)
		Set<String> commonParentNames = null;
		for (EClass eClass : referencedClasses) {
			Set<String> parentNames = new HashSet<>();
			// Check unresolved allOf references
			List<String> allOfParents = allOfRefMap.get(eClass);
			if (allOfParents != null) {
				parentNames.addAll(allOfParents);
			}
			// Check already resolved supertypes
			for (EClass superType : eClass.getESuperTypes()) {
				parentNames.add(superType.getName());
			}

			if (commonParentNames == null) {
				commonParentNames = parentNames;
			} else {
				commonParentNames.retainAll(parentNames);
			}
		}

		if (commonParentNames == null || commonParentNames.isEmpty()) {
			return null;
		}

		// Find the first common parent that is abstract in classifierMap
		for (String parentName : commonParentNames) {
			EClassifier parent = classifierMap.get(parentName);
			if (parent instanceof EClass parentClass && parentClass.isAbstract()) {
				return parentClass;
			}
		}

		return null;
	}

	private EStructuralFeature createFeatureFromJsonType(String type, String name, JsonNode propertyNode, String contextPath) {
		EStructuralFeature feature = null;

		switch (type) {
			case "array":
				feature = createArrayFeature(propertyNode, name, contextPath);
				break;
			case "string":
				feature = createStringFeature(propertyNode, name, contextPath);
				break;
			case "object":
				feature = createObjectFeature(propertyNode, name, contextPath);
				break;
			default:
				feature = ecoreFactory.createEAttribute();
				feature.setName(name);
				feature.setEType(mapJsonTypeToEcore(type));
				preserveAdditionalSchemaProperties(propertyNode, feature);
				break;
		}

		return feature;
	}

	private EStructuralFeature createArrayFeature(JsonNode propertyNode, String name, String contextPath) {
		EStructuralFeature feature = null;

		if (propertyNode.has("items")) {
			feature = createStructuralFeature(propertyNode.get("items"), name, contextPath);
			if (feature != null) {
				addEAnnotation(feature, AnnotationSources.JSONSCHEMA, "items", "true");
			}
		} else if (propertyNode.has("const")) {
			if (propertyNode.get("const").isArray()) {
				String inferredType = getJsonTypeFromConstNode(propertyNode.get("const").get(0));
				feature = createFeatureFromJsonType(inferredType, name, propertyNode.get("const"), contextPath);
			}
		}

		if (feature != null) {
			if (propertyNode.has("minItems")) {
				feature.setLowerBound(propertyNode.get("minItems").asInt());
			} else {
				feature.setLowerBound(0);
			}
			if (propertyNode.has("maxItems")) {
				feature.setUpperBound(propertyNode.get("maxItems").asInt());
			} else {
				feature.setUpperBound(-1);
			}
		}

		return feature;
	}

	private EStructuralFeature createStringFeature(JsonNode propertyNode, String name, String contextPath) {
		if (propertyNode.has("enum")) {
			// Name the enum after its owning class and property (e.g. DataSource.connectionType
			// → DataSourceConnectionType) rather than an artificial placeholder.
			String enumName = deriveEnumName(contextPath, name);
			EEnum eEnum = createEEnum(propertyNode, enumName, contextPath);
			classifierMap.put(eEnum.getName(), eEnum);

			var feature = ecoreFactory.createEAttribute();
			feature.setName(name);
			feature.setEType(eEnum);
			preserveAdditionalSchemaProperties(propertyNode, feature);
			return feature;
		} else {
			var feature = ecoreFactory.createEAttribute();
			feature.setName(name);
			feature.setEType(EcorePackage.Literals.ESTRING);
			preserveAdditionalSchemaProperties(propertyNode, feature);
			return feature;
		}
	}

	private EStructuralFeature createObjectFeature(JsonNode propertyNode, String name, String contextPath) {
		// An object with (object) additionalProperties and no fixed properties models
		// a string-keyed map → an EMF MapEntry class plus a multi-valued containment ref.
		JsonNode additionalProps = propertyNode.get("additionalProperties");
		if (!propertyNode.has("properties") && additionalProps != null && additionalProps.isObject()) {
			return createMapEntryFeature(propertyNode, name, contextPath, additionalProps);
		}

		String artificialName = ARTIFICIAL_CLASSIFIER_PREFIX + (artificialClassifierCounter++);
		EClass eClass = createEClass(propertyNode, artificialName, contextPath + "/" + artificialName);
		addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "artificial", "true");
		classifierMap.put(contextPath + "/" + artificialName, eClass);

		EReference reference = ecoreFactory.createEReference();
		reference.setName(name);
		reference.setEType(eClass);
		reference.setContainment(true);

		if (propertyNode.has("default")) {
			addEAnnotation(reference, AnnotationSources.JSONSCHEMA, "default",
						  propertyNode.get("default").toString());
		}

		return reference;
	}

	/**
	 * Creates a string-keyed EMF map entry for a JSON Schema {@code object} with
	 * {@code additionalProperties}.
	 * <p>
	 * Produces a {@code <Property>MapEntry} EClass (with
	 * {@code instanceClassName = java.util.Map$Entry}, a {@code key:EString} and a
	 * containment {@code value} reference) and returns a multi-valued containment
	 * reference to it for the owning class. The value type is the referenced class
	 * when {@code additionalProperties} is a {@code $ref}, otherwise a freshly
	 * created (de-pluralized) value class.
	 */
	private EStructuralFeature createMapEntryFeature(JsonNode propertyNode, String name,
													 String contextPath, JsonNode additionalProps) {
		String entryName = capitalizeFirst(name) + "MapEntry";

		EClass entryClass = ecoreFactory.createEClass();
		entryClass.setName(entryName);
		entryClass.setInstanceClassName("java.util.Map$Entry");

		EAttribute keyAttr = ecoreFactory.createEAttribute();
		keyAttr.setName("key");
		keyAttr.setEType(EcorePackage.Literals.ESTRING);
		entryClass.getEStructuralFeatures().add(keyAttr);

		EReference valueRef = ecoreFactory.createEReference();
		valueRef.setName("value");
		valueRef.setContainment(true);
		entryClass.getEStructuralFeatures().add(valueRef);

		if (additionalProps.has("$ref")) {
			String refName = extractSchemaNameFromRef(additionalProps.get("$ref").asString());
			if (classifierMap.containsKey(refName)) {
				valueRef.setEType(classifierMap.get(refName));
			} else {
				deferredReferences.add(new DeferredTypeReference(valueRef, refName));
			}
		} else {
			// additionalProperties is an inline (object) schema → dedicated value class
			String valueClassName = capitalizeFirst(depluralize(name));
			EClass valueClass = createEClass(additionalProps, valueClassName, valueClassName);
			classifierMap.put(valueClassName, valueClass);
			valueRef.setEType(valueClass);
		}

		classifierMap.put(contextPath + "/" + entryName, entryClass);

		EReference mapRef = ecoreFactory.createEReference();
		mapRef.setName(name);
		mapRef.setEType(entryClass);
		mapRef.setContainment(true);
		mapRef.setUpperBound(-1);
		return mapRef;
	}

	private EReference createMultiValueReference(JsonNode jsonNode, String name, String contextPath) {
		if (!jsonNode.isArray()) {
			throw new IllegalArgumentException("anyOf node for property " + name + " expected to be an array");
		}

		EReference reference = ecoreFactory.createEReference();
		reference.setName(name);
		reference.setContainment(false);

		List<String> refTargetNames = new ArrayList<>();
		StringBuilder refAnnotation = new StringBuilder();

		for (int i = 0; i < jsonNode.size(); i++) {
			JsonNode subNode = jsonNode.get(i);
			if (subNode.has("$ref")) {
				String refPath = subNode.get("$ref").asString();
				refAnnotation.append(refPath).append(",");
				refTargetNames.add(extractSchemaNameFromRef(refPath));
			}
		}

		if (refAnnotation.length() > 0) {
			addEAnnotation(reference, AnnotationSources.JSONSCHEMA, "ref", refAnnotation.substring(0, refAnnotation.length() - 1));
		}

		// Defer type resolution — we need allOf supertypes to be resolved first
		// so we can find a common supertype instead of creating an artificial class
		if (!refTargetNames.isEmpty()) {
			deferredAnyOfReferences.add(new DeferredAnyOfReference(reference, refTargetNames));
		}

		return reference;
	}

	/**
	 * Handles multi-type properties like "type": ["integer", "string"]
	 */
	private EStructuralFeature handleMultiTypeProperty(JsonNode propertySchema, JsonNode typeArray,
													   String name, String contextPath) {
		String unionBaseName = ARTIFICIAL_CLASSIFIER_PREFIX + (artificialClassifierCounter++);
		String unionQualifiedName = contextPath + "/" + unionBaseName;

		EClass unionBase = ecoreFactory.createEClass();
		unionBase.setName(unionBaseName);
		unionBase.setAbstract(true);
		unionBase.setInterface(false);

		addEAnnotation(unionBase, AnnotationSources.JSONSCHEMA, "artificial", "true");
		addEAnnotation(unionBase, AnnotationSources.JSONSCHEMA, "multiType", "true");
		addEAnnotation(unionBase, AnnotationSources.JSONSCHEMA, "typeArray", typeArray.toString());

		if (propertySchema.has("default")) {
			addEAnnotation(unionBase, AnnotationSources.JSONSCHEMA, "default",
						  propertySchema.get("default").toString());
		}

		preserveAdditionalSchemaProperties(propertySchema, unionBase);

		classifierMap.put(unionQualifiedName, unionBase);

		// Create a variant class for each type
		ArrayNode typeArrayNode = (ArrayNode) typeArray;
		for (int i = 0; i < typeArrayNode.size(); i++) {
			String variantType = typeArrayNode.get(i).asString();
			String variantName = unionBaseName + "Variant" + i;

			EClass variantClass = ecoreFactory.createEClass();
			variantClass.setName(variantName);
			variantClass.getESuperTypes().add(unionBase);

			addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA, "artificial", "true");
			addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA, "variant", variantType);
			addEAnnotation(variantClass, AnnotationSources.JSONSCHEMA, "variantIndex", String.valueOf(i));

			EAttribute valueAttr = ecoreFactory.createEAttribute();
			valueAttr.setName("value");
			valueAttr.setEType(mapJsonTypeToEcore(variantType));
			valueAttr.setLowerBound(1);
			variantClass.getEStructuralFeatures().add(valueAttr);

			classifierMap.put(unionQualifiedName + "/Variant" + i, variantClass);
		}

		EReference reference = ecoreFactory.createEReference();
		reference.setName(name);
		reference.setEType(unionBase);
		reference.setContainment(true);

		return reference;
	}

	/**
	 * Process top-level properties (creates a rootClass)
	 */
	private void processTopLevelProperties(JsonNode propertiesNode, JsonNode rootSchema, EPackage ePackage) {
		String rootClassName = ePackage.getName();
		if (rootClassName == null || rootClassName.isEmpty()) {
			rootClassName = "Root";
		}

		EClass rootClass = ecoreFactory.createEClass();
		rootClass.setName(capitalizeFirst(rootClassName));

		Set<String> requiredProps = new HashSet<>();
		if (rootSchema.has("required")) {
			ArrayNode reqArray = (ArrayNode) rootSchema.get("required");
			for (JsonNode req : reqArray) {
				requiredProps.add(req.asString());
			}
		}

		if (rootSchema.has("description")) {
			addEAnnotation(rootClass, AnnotationSources.GEN_MODEL,
						  "documentation", rootSchema.get("description").asString());
		}

		if (rootSchema.has("additionalProperties")) {
			addEAnnotation(rootClass, AnnotationSources.JSONSCHEMA,
						  "additionalProperties",
						  rootSchema.get("additionalProperties").toString());
		}

		for (String propName : propertiesNode.propertyNames()) {
			JsonNode propertySchema = propertiesNode.get(propName);
			EStructuralFeature feature = createStructuralFeature(propertySchema, propName, "");
			if (feature != null) {
				// 'required' only raises the lower bound of single-valued features.
				if (requiredProps.contains(propName) && feature.getUpperBound() == 1) {
					feature.setLowerBound(1);
				}
				rootClass.getEStructuralFeatures().add(feature);
			}
		}

		addEAnnotation(rootClass, AnnotationSources.JSONSCHEMA, "rootClass", "true");

		ePackage.getEClassifiers().add(rootClass);
		classifierMap.put("", rootClass);
	}

	private void handleAnyOf(JsonNode schemaNode, EClass eClass, String qualifiedName) {
		ArrayNode anyOfArray = (ArrayNode) schemaNode.get("anyOf");

		boolean onlyRequiredConstraints = true;
		for (JsonNode option : anyOfArray) {
			if (option.has("properties") || option.has("type") || option.has("$ref")) {
				onlyRequiredConstraints = false;
				break;
			}
		}

		if (onlyRequiredConstraints) {
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "anyOf", anyOfArray.toString());
		} else {
			addEAnnotation(eClass, AnnotationSources.JSONSCHEMA, "anyOf", anyOfArray.toString());
			diagnostics.add(JsonSchemaConversionDiagnostic.complexAnyOf(qualifiedName));
		}
	}

	// Resolution methods

	private void resolveDeferredReferences() {
		for (DeferredReference deferredRef : deferredReferences) {
			EClassifier referencedType = classifierMap.get(deferredRef.targetSchemaName);

			if (referencedType != null) {
				if (deferredRef instanceof DeferredConfigReference configRef) {
					if (configRef.owningClass != null && configRef.featureName != null) {
						EReference configReference = ecoreFactory.createEReference();
						configReference.setName(configRef.featureName);
						configReference.setEType(referencedType);
						configReference.setContainment(true);
						configReference.setLowerBound(1);
						configReference.setUpperBound(1);
						configRef.owningClass.getEStructuralFeatures().add(configReference);
					}
				} else if (deferredRef instanceof DeferredTypeReference typeRef) {
					if (typeRef.feature != null) {
						typeRef.feature.setEType(referencedType);
					}
				}
			} else {
				diagnostics.add(JsonSchemaConversionDiagnostic.unresolvedReference(deferredRef.targetSchemaName));
			}
		}
	}

	private void resolveMissingReferences() {
		missingRefMap.forEach((ref, refTypeName) -> {
			if (classifierMap.containsKey(refTypeName)) {
				ref.setEType(classifierMap.get(refTypeName));
			}
		});
	}

	private void resolveAnyOfReferences() {
		anyOfRefMap.forEach((superType, subTypesClassNames) -> {
			subTypesClassNames.forEach(clName -> {
				if (classifierMap.containsKey(clName) && classifierMap.get(clName) instanceof EClass cl) {
					cl.getESuperTypes().add((EClass) superType);
				}
			});
		});
	}

	private void resolveAllOfReferences() {
		allOfRefMap.forEach((eClass, superTypeNames) -> {
			superTypeNames.forEach(superType -> {
				if (classifierMap.containsKey(superType) && classifierMap.get(superType) instanceof EClass cl) {
					eClass.getESuperTypes().add(cl);
				}
			});
		});
	}

	/**
	 * Resolves deferred anyOf property references.
	 * <p>
	 * This must run AFTER {@link #resolveAllOfReferences()} so that the full
	 * inheritance hierarchy is available. For each deferred anyOf reference, it:
	 * <ol>
	 *   <li>Resolves the target EClasses from the classifierMap</li>
	 *   <li>Walks up their supertype chains to find the lowest common supertype</li>
	 *   <li>If a common supertype exists, uses it as the reference type</li>
	 *   <li>If no common supertype exists, creates an artificial parent and wires
	 *       the referenced classes as subtypes</li>
	 * </ol>
	 */
	private void resolveAnyOfPropertyReferences() {
		// Cache: sorted ref target names → resolved EClass, to reuse across identical anyOf sets
		Map<List<String>, EClass> resolvedAnyOfCache = new HashMap<>();

		for (DeferredAnyOfReference deferred : deferredAnyOfReferences) {
			// Build a canonical (sorted) key so order of refs doesn't matter
			List<String> sortedTargets = new ArrayList<>(deferred.targetSchemaNames);
			sortedTargets.sort(String::compareTo);

			// Check cache first
			EClass resolved = resolvedAnyOfCache.get(sortedTargets);
			if (resolved != null) {
				deferred.reference.setEType(resolved);
				continue;
			}

			// Resolve target EClasses
			List<EClass> targetClasses = new ArrayList<>();
			for (String targetName : deferred.targetSchemaNames) {
				EClassifier classifier = classifierMap.get(targetName);
				if (classifier instanceof EClass eClass) {
					targetClasses.add(eClass);
				}
			}

			if (targetClasses.isEmpty()) {
				diagnostics.add(JsonSchemaConversionDiagnostic.unresolvedReference(
					String.join(", ", deferred.targetSchemaNames)));
				continue;
			}

			// Find the lowest common supertype
			EClass commonSupertype = findLowestCommonSupertype(targetClasses);

			if (commonSupertype != null) {
				// Use the existing common supertype — no artificial class needed
				deferred.reference.setEType(commonSupertype);
				resolvedAnyOfCache.put(sortedTargets, commonSupertype);
			} else {
				// No common supertype — create an artificial parent
				EClass artificialParent = createArtificialAnyOfParent(targetClasses);
				deferred.reference.setEType(artificialParent);
				resolvedAnyOfCache.put(sortedTargets, artificialParent);
			}
		}
	}

	/**
	 * Finds the lowest common supertype of the given EClasses by walking up
	 * each class's supertype chain and finding the deepest shared ancestor.
	 *
	 * @return the lowest common supertype, or null if no common supertype exists
	 */
	private EClass findLowestCommonSupertype(List<EClass> classes) {
		if (classes.size() < 2) {
			return classes.isEmpty() ? null : classes.get(0);
		}

		// Collect the full ancestor chain for the first class (ordered from most specific to most general)
		List<EClass> firstAncestors = collectAncestors(classes.get(0));

		// For each ancestor of the first class (starting from the most specific),
		// check if it is an ancestor of ALL other classes
		for (EClass candidate : firstAncestors) {
			boolean isCommonAncestor = true;
			for (int i = 1; i < classes.size(); i++) {
				if (!isAncestorOf(candidate, classes.get(i))) {
					isCommonAncestor = false;
					break;
				}
			}
			if (isCommonAncestor) {
				return candidate;
			}
		}

		return null;
	}

	/**
	 * Collects all ancestors of the given class in breadth-first order
	 * (direct supertypes first, then their supertypes, etc.).
	 */
	private List<EClass> collectAncestors(EClass eClass) {
		List<EClass> ancestors = new ArrayList<>();
		List<EClass> queue = new ArrayList<>(eClass.getESuperTypes());
		Set<EClass> visited = new HashSet<>();

		while (!queue.isEmpty()) {
			EClass current = queue.remove(0);
			if (visited.add(current)) {
				ancestors.add(current);
				queue.addAll(current.getESuperTypes());
			}
		}

		return ancestors;
	}

	/**
	 * Checks whether {@code candidate} is an ancestor (direct or transitive supertype) of {@code eClass}.
	 */
	private boolean isAncestorOf(EClass candidate, EClass eClass) {
		List<EClass> queue = new ArrayList<>(eClass.getESuperTypes());
		Set<EClass> visited = new HashSet<>();

		while (!queue.isEmpty()) {
			EClass current = queue.remove(0);
			if (current == candidate) {
				return true;
			}
			if (visited.add(current)) {
				queue.addAll(current.getESuperTypes());
			}
		}

		return false;
	}

	/**
	 * Creates an artificial parent class for an anyOf where no common supertype exists.
	 * Extracts common features from the target classes into the parent and wires
	 * the target classes as subtypes.
	 */
	private EClass createArtificialAnyOfParent(List<EClass> targetClasses) {
		String parentName = ARTIFICIAL_CLASSIFIER_PREFIX + (artificialClassifierCounter++);

		EClass parent = ecoreFactory.createEClass();
		parent.setName(parentName);
		parent.setAbstract(true);
		addEAnnotation(parent, AnnotationSources.JSONSCHEMA, "artificial", "true");
		addEAnnotation(parent, AnnotationSources.JSONSCHEMA, "source", "anyOf");

		// Extract common features: features that exist (by name and type) in ALL target classes
		if (targetClasses.size() >= 2) {
			List<EStructuralFeature> firstFeatures = targetClasses.get(0).getEStructuralFeatures();
			for (EStructuralFeature candidate : firstFeatures) {
				boolean commonToAll = true;
				for (int i = 1; i < targetClasses.size(); i++) {
					EStructuralFeature match = targetClasses.get(i).getEStructuralFeature(candidate.getName());
					if (match == null || match.getEType() != candidate.getEType()) {
						commonToAll = false;
						break;
					}
				}
				if (commonToAll) {
					// Move the common feature to the parent: create a copy on the parent
					// and remove originals from each target class
					EStructuralFeature parentFeature;
					if (candidate instanceof EReference ref) {
						EReference copy = ecoreFactory.createEReference();
						copy.setName(ref.getName());
						copy.setEType(ref.getEType());
						copy.setContainment(ref.isContainment());
						copy.setLowerBound(ref.getLowerBound());
						copy.setUpperBound(ref.getUpperBound());
						parentFeature = copy;
					} else {
						EAttribute copy = ecoreFactory.createEAttribute();
						copy.setName(candidate.getName());
						copy.setEType(candidate.getEType());
						copy.setLowerBound(candidate.getLowerBound());
						copy.setUpperBound(candidate.getUpperBound());
						parentFeature = copy;
					}
					parent.getEStructuralFeatures().add(parentFeature);
				}
			}

			// Remove the now-inherited features from each target class
			for (EClass targetClass : targetClasses) {
				List<EStructuralFeature> toRemove = new ArrayList<>();
				for (EStructuralFeature parentFeature : parent.getEStructuralFeatures()) {
					EStructuralFeature existing = targetClass.getEStructuralFeature(parentFeature.getName());
					if (existing != null) {
						toRemove.add(existing);
					}
				}
				targetClass.getEStructuralFeatures().removeAll(toRemove);
			}
		}

		// Wire target classes as subtypes
		for (EClass targetClass : targetClasses) {
			if (!targetClass.getESuperTypes().contains(parent)) {
				targetClass.getESuperTypes().add(parent);
			}
		}

		classifierMap.put(parentName, parent);
		return parent;
	}

	// Helper methods

	private void addCommonAnnotations(EStructuralFeature feature, JsonNode propertyNode, boolean isArrayItems) {
		if (propertyNode.has("description")) {
			addEAnnotation(feature, AnnotationSources.GEN_MODEL, "documentation", propertyNode.get("description").asString());
		}
		if (propertyNode.has("format")) {
			addEAnnotation(feature, AnnotationSources.JSONSCHEMA, "format", propertyNode.get("format").asString());
		}
		if (propertyNode.has("const")) {
			JsonNode constNode = propertyNode.get("const");
			String constValue = constNode.isString() ? constNode.asString() : constNode.toPrettyString();
			addEAnnotation(feature, AnnotationSources.JSONSCHEMA, "const", constValue);
			String constType = constNode.isArray() ? constNode.get(0).getNodeType().toString() : constNode.getNodeType().toString();
			addEAnnotation(feature, AnnotationSources.JSONSCHEMA, "constType", constType);
		}
		if (!propertyNode.has("type")) {
			addEAnnotation(feature, AnnotationSources.JSONSCHEMA, isArrayItems ? "noArrayItemsTypeInfo" : "noTypeInfo", "true");
		}
		if (propertyNode.has("uniqueItems")) {
			addEAnnotation(feature, AnnotationSources.JSONSCHEMA, "uniqueItems", propertyNode.get("uniqueItems").asString());
		}
		if (propertyNode.has("writeOnly")) {
			addEAnnotation(feature, AnnotationSources.JSONSCHEMA, "writeOnly", propertyNode.get("writeOnly").asString());
		}
		if (propertyNode.has("readOnly")) {
			addEAnnotation(feature, AnnotationSources.JSONSCHEMA, "readOnly", propertyNode.get("readOnly").asString());
		}
	}

	private void preserveAdditionalSchemaProperties(JsonNode schema, EModelElement element) {
		String[] schemaProperties = {
			"default", "minItems", "maxItems", "uniqueItems",
			"minLength", "maxLength", "pattern", "format",
			"minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum",
			"multipleOf", "const", "title", "description", "examples",
			// Content keywords
			"contentEncoding", "contentMediaType"
		};

		for (String propertyName : schemaProperties) {
			if (schema.has(propertyName)) {
				JsonNode propertyValue = schema.get(propertyName);
				addEAnnotation(element, AnnotationSources.JSONSCHEMA, propertyName, propertyValue.toString());
			}
		}

		// $comment - preserve as annotation
		if (schema.has("$comment")) {
			addEAnnotation(element, AnnotationSources.JSONSCHEMA, "comment", schema.get("$comment").asString());
		}

		// deprecated - use GenModel annotation for better tooling support
		if (schema.has("deprecated") && schema.get("deprecated").asBoolean()) {
			addEAnnotation(element, AnnotationSources.GEN_MODEL, "deprecated", "true");
		}
	}

	private void addEAnnotation(EModelElement element, String source, String detailKey, String detailValue) {
		EAnnotation annotation = element.getEAnnotation(source);
		if (annotation == null) {
			annotation = ecoreFactory.createEAnnotation();
			annotation.setSource(source);
			element.getEAnnotations().add(annotation);
		}
		annotation.getDetails().put(detailKey, detailValue);
	}

	private boolean arrayContains(JsonNode jsonNode, String value) {
		if (!jsonNode.isArray()) return false;
		for (JsonNode node : jsonNode) {
			if (node.isString() && value.equals(node.asString())) {
				return true;
			}
		}
		return false;
	}

	private String getJsonTypeFromConstNode(JsonNode jsonNode) {
		if (jsonNode.isString()) return "string";
		if (jsonNode.isBigDecimal()) return "bigDecimal";
		if (jsonNode.isBigInteger()) return "bigInteger";
		if (jsonNode.isBinary()) return "binary";
		if (jsonNode.isBoolean()) return "boolean";
		if (jsonNode.isDouble()) return "number";
		if (jsonNode.isInt()) return "integer";
		if (jsonNode.isArray()) return "array";
		return "javaObject";
	}

	private EDataType mapJsonTypeToEcore(String jsonType) {
		EcorePackage ecorePackage = EcorePackage.eINSTANCE;
		return switch (jsonType) {
			case "string" -> ecorePackage.getEString();
			case "integer" -> ecorePackage.getEInt();
			case "number" -> ecorePackage.getEDouble();
			case "boolean" -> ecorePackage.getEBoolean();
			case "bigDecimal" -> ecorePackage.getEBigDecimal();
			case "bigInteger" -> ecorePackage.getEBigInteger();
			case "binary" -> ecorePackage.getEByte();
			default -> ecorePackage.getEJavaObject();
		};
	}

	private String extractSchemaNameFromRef(String refPath) {
		// Handle anchor refs: #anchorName (no slash after #)
		if (refPath.startsWith("#") && !refPath.startsWith("#/")) {
			String anchorName = refPath.substring(1);
			// Look up in anchorMap - the anchor name IS the schema name
			return anchorName;
		}

		// Handle JSON Pointer refs: #/definitions/Name
		if (refPath.startsWith("#/")) {
			refPath = refPath.substring(2);
		}

		if (schemaFeature != null && refPath.startsWith(schemaFeature + "/")) {
			refPath = refPath.substring(schemaFeature.length() + 1);
		}

		return refPath;
	}

	private String extractNamespacePath(String qualifiedName) {
		if (qualifiedName == null || !qualifiedName.contains("/")) {
			return null;
		}
		int lastSlash = qualifiedName.lastIndexOf('/');
		return qualifiedName.substring(0, lastSlash);
	}

	private String sanitizeName(String name) {
		return name.replaceAll("[^a-zA-Z0-9_]", "_");
	}

	/**
	 * Derives a camelCase nsPrefix from a schema title, e.g.
	 * {@code "CORE DataSet"} → {@code "coreDataSet"}. The first token is
	 * lower-cased, subsequent tokens have their first letter capitalized.
	 */
	private String deriveNsPrefix(String title) {
		if (title == null) {
			return "schema";
		}
		String[] tokens = title.split("[^a-zA-Z0-9]+");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String token : tokens) {
			if (token.isEmpty()) {
				continue;
			}
			if (first) {
				sb.append(token.toLowerCase());
				first = false;
			} else {
				sb.append(Character.toUpperCase(token.charAt(0))).append(token.substring(1));
			}
		}
		return sb.length() == 0 ? "schema" : sb.toString();
	}

	/**
	 * Derives an enum classifier name from its owning definition and property,
	 * e.g. owning class {@code DataSource} + property {@code connectionType} →
	 * {@code DataSourceConnectionType}. Falls back to the capitalized property
	 * name when there is no owning context (e.g. root-level properties).
	 */
	private String deriveEnumName(String contextPath, String property) {
		String owner = contextPath == null ? "" : contextPath;
		int slash = owner.lastIndexOf('/');
		if (slash >= 0) {
			owner = owner.substring(slash + 1);
		}
		return capitalizeFirst(owner) + capitalizeFirst(property);
	}

	/**
	 * Removes a trailing plural {@code "s"} from a (lower-camel) name, e.g.
	 * {@code "dataStructures"} → {@code "dataStructure"}. Returns the name
	 * unchanged when it does not end in {@code "s"}.
	 */
	private String depluralize(String name) {
		if (name != null && name.length() > 1 && name.endsWith("s")) {
			return name.substring(0, name.length() - 1);
		}
		return name;
	}

	private String capitalizeFirst(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	private String getCommonSuffix(String... strings) {
		if (strings == null || strings.length == 0) return null;

		String first = strings[0];
		if (first == null) return null;

		int minLength = first.length();
		for (String str : strings) {
			if (str == null) return null;
			minLength = Math.min(minLength, str.length());
		}

		int suffixLength = 0;
		while (suffixLength < minLength) {
			char currentChar = first.charAt(first.length() - 1 - suffixLength);
			boolean allMatch = true;
			for (String str : strings) {
				if (str.charAt(str.length() - 1 - suffixLength) != currentChar) {
					allMatch = false;
					break;
				}
			}
			if (!allMatch) break;
			suffixLength++;
		}

		return suffixLength == 0 ? null : first.substring(first.length() - suffixLength);
	}

	private List<String> getCommonRequiredFields(List<ArrayNode> requiredNodes) {
		if (requiredNodes == null || requiredNodes.isEmpty()) return new ArrayList<>();

		Set<String> commonSet = null;
		for (ArrayNode arrayNode : requiredNodes) {
			Set<String> currentSet = new HashSet<>();
			for (JsonNode fieldNode : arrayNode) {
				if (fieldNode.isString()) {
					currentSet.add(fieldNode.asString());
				}
			}
			if (commonSet == null) {
				commonSet = currentSet;
			} else {
				commonSet.retainAll(currentSet);
			}
		}

		return commonSet != null ? new ArrayList<>(commonSet) : new ArrayList<>();
	}

	private Map<String, JsonNode> getCommonSubNodes(List<JsonNode> nodes) {
		Map<String, JsonNode> commonFields = new HashMap<>();
		if (nodes == null || nodes.isEmpty()) return commonFields;

		JsonNode reference = nodes.get(0);
		for (String fieldName : reference.propertyNames()) {
			JsonNode referenceValue = reference.get(fieldName);
			boolean isCommon = true;
			for (int i = 1; i < nodes.size(); i++) {
				JsonNode otherValue = nodes.get(i).get(fieldName);
				if (otherValue == null || !referenceValue.equals(otherValue)) {
					isCommon = false;
					break;
				}
			}
			if (isCommon) {
				commonFields.put(fieldName, referenceValue);
			}
		}

		return commonFields;
	}

	// Inner classes

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

	private static abstract class DeferredReference {
		String targetSchemaName;

		DeferredReference(String targetSchemaName) {
			this.targetSchemaName = targetSchemaName;
		}
	}

	private static class DeferredTypeReference extends DeferredReference {
		EStructuralFeature feature;

		DeferredTypeReference(EStructuralFeature feature, String targetSchemaName) {
			super(targetSchemaName);
			this.feature = feature;
		}
	}

	private static class DeferredConfigReference extends DeferredReference {
		EClass owningClass;
		String featureName;

		DeferredConfigReference(EClass owningClass, String targetSchemaName, String featureName) {
			super(targetSchemaName);
			this.owningClass = owningClass;
			this.featureName = featureName;
		}
	}

	private static class DeferredAnyOfReference {
		EReference reference;
		List<String> targetSchemaNames;

		DeferredAnyOfReference(EReference reference, List<String> targetSchemaNames) {
			this.reference = reference;
			this.targetSchemaNames = targetSchemaNames;
		}
	}
}
