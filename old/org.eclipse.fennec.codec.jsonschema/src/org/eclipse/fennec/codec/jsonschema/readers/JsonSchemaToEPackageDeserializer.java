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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAnnotation;
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
 * 
 * @author ilenia
 * @since Aug 7, 2025
 */
public class JsonSchemaToEPackageDeserializer extends ValueDeserializer<EPackage> {

	private final EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
	private static final String GEN_MODEL_ANNOTATION_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";
	private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";

	private Map<String, EClassifier> classifierMap = new HashMap<>();
	private Map<EReference, String> missingRefMap = new HashMap<>();
	int artificialClassifierCounter = 0;
	private static final String ARTIFICIAL_CLASSIFIER_PREFIX = "ArtificialClassifier";
	private Map<EClassifier, List<String>> anyOfRefMap = new HashMap<>();
	private Map<EClass, List<String>> allOfRefMap = new HashMap<>();
	private Map<String, EClassifier> cachedClassifiers = new HashMap<>();
	private Map<Map<String, JsonNode>, EClass> parentClassMaps = new HashMap<>(); 
	
	private String schemaFeature;
	
	public JsonSchemaToEPackageDeserializer() {
		
	}

	public JsonSchemaToEPackageDeserializer(String schemaFeature) {
		this.schemaFeature = schemaFeature;		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueDeserializer#deserialize(tools.jackson.core.JsonParser, tools.jackson.databind.DeserializationContext)
	 */
	@Override
	public EPackage deserialize(JsonParser parser, DeserializationContext ctxt) {
		JsonNode node = ctxt.readTree(parser);
		JsonNode defNode = node.get(schemaFeature);
		if(defNode == null) defNode = node;
//		if(defNode == null) {
//			throw new IllegalArgumentException(String.format("Expecting document to have a \"%s\" node", SCHEMA_FEATURE));
//		} 
//		if(!defNode.isObject()) {
//			throw new IllegalArgumentException(String.format("Expecting \"%s\" node to be an object node", SCHEMA_FEATURE));
//		}
		EPackage ePackage = ecoreFactory.createEPackage();
		if(node.get("$schema") != null) {
			addEAnnotation(ePackage, JSONSCHEMA_ANNOTATION_SOURCE, "schema", node.get("$schema").asString());
		}
		if(node.get("$id") != null) {
			ePackage.setNsURI(node.get("$id").asString());
		}
		if(node.get("title") != null) {
			ePackage.setName(node.get("title").asString());
		}

		for(String property : defNode.propertyNames()) {
			JsonNode classifierNode = defNode.get(property);
			EClassifier eClassifier = null;
			//			1. EEnum
			if(classifierNode.get("enum") != null) {
				eClassifier = createEEnum(classifierNode, property);
			} 
			else if(classifierNode.get("allOf") != null) {
				//				2.0 EClass with inheritance
				eClassifier = createEClass(classifierNode, property, defNode);
			}
			//			2. EClass/Array/EDataType
			else if(classifierNode.get("type") != null) {
				JsonNode typeNode = classifierNode.get("type");
				//				2.1 EClass
				if(typeNode.isString() && "object".equals(typeNode.asString())) {
					eClassifier = createEClass(classifierNode, property, defNode);
				}
				//				2.2. Array
				else if(typeNode.isString() && "array".equals(typeNode.asString())) {
					eClassifier = createFakeArrayClass(classifierNode, property, defNode);
				} 
				//				2.3 EDataType
				else {
					eClassifier = createEDataTypeFromJsonType(classifierNode, property);
				}
			} 
			if(eClassifier != null) {
				classifierMap.put(eClassifier.getName(), eClassifier);
			}
		}
		missingRefMap.forEach((ref, refTypeName) -> {
			if(classifierMap.containsKey(refTypeName)) {
				ref.setEType(classifierMap.get(refTypeName));
			} else {
				System.out.println(String.format("No EClassifier Name for Reference %s and Classifier %s", ref.getName(), refTypeName));
			}
		});

		anyOfRefMap.forEach((superType, subTypesClassNames) -> {
			subTypesClassNames.forEach(clName -> {
				if(classifierMap.containsKey(clName) && classifierMap.get(clName) instanceof EClass cl) {
					cl.getESuperTypes().add((EClass)superType);
				} else {
					System.out.println(String.format("No EClassifier Name for anyOf %s and Classifier %s", clName, superType.getName()));

				}
			});
		});

		allOfRefMap.forEach((eClass,superTypeNames) -> {
			superTypeNames.forEach(superType -> {
				if(classifierMap.containsKey(superType) && classifierMap.get(superType) instanceof EClass cl) {
					eClass.getESuperTypes().add(cl);
				} else {
					System.out.println(String.format("No EClassifier Name for allOf %s and EClass %s", superType, eClass.getName()));

				}
			});
		});

		ePackage.getEClassifiers().addAll(classifierMap.values());
		return ePackage;
	}


	private EClass createFakeArrayClass(JsonNode classNode, String name, JsonNode rootNode) {
		if(cachedClassifiers.containsKey(classNode.toString())) {
			return (EClass)cachedClassifiers.get(classNode.toString());
		} 
		EClass eClass = ecoreFactory.createEClass();
		eClass.setName(name);
		addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "source", "TopLevelArray");
		addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE	, "artificial", "true");
		if(classNode.get("description") != null) {
			addEAnnotation(eClass, GEN_MODEL_ANNOTATION_SOURCE, "documentation", classNode.get("description").asString());
		}
		if(classNode.get("const") != null) {
			//			TODO: const as array

		} else if(classNode.get("items") != null) {
			EStructuralFeature feature = createEStructuralFeature(classNode.get("items"), "items", rootNode, true);
			if(feature != null) {				
				eClass.getEStructuralFeatures().add(feature);
			}
		}
		cachedClassifiers.put(classNode.toString(), eClass);
		return eClass;
	}

	private EAnnotation createEAnnotation(String source, String key, String value) {
		EAnnotation eAnnotation = ecoreFactory.createEAnnotation();
		eAnnotation.setSource(source);
		eAnnotation.getDetails().put(key, value);
		return eAnnotation;
	}

	private EClass createEClass(JsonNode classNode, String name, JsonNode rootNode) {
		if(cachedClassifiers.containsKey(classNode.toString()) && name.startsWith(ARTIFICIAL_CLASSIFIER_PREFIX)) { //TODO: do we really want to do it only for the artificial??
			System.out.println("Found existing class for " + name);
			return (EClass)cachedClassifiers.get(classNode.toString());
		} 
		EClass eClass = null;
		JsonNode allOfNode = classNode.get("allOf");
		if(allOfNode != null) {
			List<String> parentNames = new LinkedList<>();
			if(allOfNode.isArray()) {				
				for(int i = 0; i < allOfNode.size(); i++) {
					JsonNode allOf = allOfNode.get(i);
					if(allOf.get("$ref") != null) {
						int indexOfSlash = allOf.get("$ref").asString().lastIndexOf("/");
						parentNames.add(allOf.get("$ref").asString().substring(indexOfSlash+1));
					} else {
						if(eClass == null) {
							eClass = createEClass(allOf, name, rootNode);
						} else {
							throw new IllegalArgumentException(String.format("allOf node with multiple inner objects. Case not supported for eClass %s", name));
						}
					}
				}
			} else {
				throw new IllegalArgumentException(String.format("allOf node is not an array. Case not supported for eClass %s", name));
			}
			//			if only references were present in allOf we create an empty EClass
			if(eClass == null) {
				eClass = ecoreFactory.createEClass();
				eClass.setName(name);
			}
			allOfRefMap.put(eClass, parentNames);			
		} else {
			eClass = ecoreFactory.createEClass();
			eClass.setName(name);
			if(classNode.get("description") != null) {
				addEAnnotation(eClass, GEN_MODEL_ANNOTATION_SOURCE, "documentation", classNode.get("description").asString());
			}
			JsonNode requiredNode = classNode.get("required");
			JsonNode propertiesNode = classNode.get("properties");
			if(propertiesNode != null) {
				for(String property : propertiesNode.propertyNames()) {
					EStructuralFeature feature = createEStructuralFeature(propertiesNode.get(property), property, rootNode, false);
					if(feature != null) {
						if(requiredNode != null && arrayNodeContains(requiredNode, feature.getName())) {
							feature.setLowerBound(1);
						}
						eClass.getEStructuralFeatures().add(feature);
					}
				}
			}
			if(classNode.get("additionalProperties") != null) {
				addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "additionalProperties", (classNode.get("additionalProperties").isBoolean() ? String.valueOf(classNode.get("additionalProperties")) : classNode.get("additionalProperties").toPrettyString()));
			}
		}


		cachedClassifiers.put(classNode.toString(), eClass);
		classifierMap.put(eClass.getName(), eClass);			
		return eClass;		
	}

	private EClass createEClass(JsonNode classNode, String name, JsonNode rootNode, List<String> ignorePropertiesList) {
		if(cachedClassifiers.containsKey(classNode.toString()) && name.startsWith(ARTIFICIAL_CLASSIFIER_PREFIX)) {
			System.out.println("Found existing class");
			return (EClass)cachedClassifiers.get(classNode.toString());
		} 
		if(ignorePropertiesList == null) ignorePropertiesList = Collections.emptyList();
		EClass eClass = ecoreFactory.createEClass();
		eClass.setName(name);
		if(classNode.get("description") != null) {
			addEAnnotation(eClass, GEN_MODEL_ANNOTATION_SOURCE, "documentation", classNode.get("description").asString());
		}
		JsonNode requiredNode = classNode.get("required");
		JsonNode propertiesNode = classNode.get("properties");
		if(propertiesNode != null) {
			for(String property : propertiesNode.propertyNames()) {
				if(ignorePropertiesList.contains(property)) continue;
				EStructuralFeature feature = createEStructuralFeature(propertiesNode.get(property), property, rootNode, false);
				if(feature != null) {
					if(requiredNode != null && arrayNodeContains(requiredNode, feature.getName())) {
						feature.setLowerBound(1);
					}
					eClass.getEStructuralFeatures().add(feature);
				}
			}
		}
		if(classNode.get("additionalProperties") != null) {
			addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "additionalProperties", classNode.get("additionalProperties").isBoolean() ? String.valueOf(classNode.get("additionalProperties")) : classNode.get("additionalProperties").toPrettyString());
		}
		cachedClassifiers.put(classNode.toString(), eClass);
		classifierMap.put(eClass.getName(), eClass);			
		return eClass;		
	}

	private EStructuralFeature createEStructuralFeature(JsonNode propertyNode, String name, JsonNode rootNode, boolean isArrayItems) {
		EStructuralFeature feature = null;
		//		1. Non-contained EReference
		if(propertyNode.get("$ref") != null) {
			feature = ecoreFactory.createEReference();
			feature.setName(name);
			addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "ref", propertyNode.get("$ref").asString());
			((EReference)feature).setContainment(false);
			int lastIndexOfSlash = propertyNode.get("$ref").asString().lastIndexOf("/");
			String refName = propertyNode.get("$ref").asString().substring(lastIndexOfSlash+1);
			if(classifierMap.containsKey(refName.toUpperCase())) {
				feature.setEType(classifierMap.get(refName));
			} else {
				missingRefMap.put(((EReference)feature), refName);
			}
		} 
		else if(propertyNode.get("type") != null) {
			if(propertyNode.get("type").isString()) {
				String type = propertyNode.get("type").asString();
				feature = createEStructuralFeatureFromJsonType(type, name, propertyNode, rootNode);
			} else {
				feature = createEStructuralFeatureFromJsonType("javaObject", name, propertyNode, rootNode);
				StringBuilder sb = new StringBuilder();
				for(int t = 0; t < propertyNode.get("type").size(); t++) {
					sb.append(propertyNode.get("type").get(t).asString());
					if(t < (propertyNode.get("type").size()-1)) {
						sb.append(",");
					}
				}
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "dataType", sb.toString());
			}			
		} 
		else if(propertyNode.get("enum") != null) {
			//			No type info but we know it should be string becuase it's an enum
			feature = createEStructuralFeatureFromJsonType("string", name, propertyNode, rootNode);
		}
		else if(propertyNode.get("const") != null) {
			//if there is no type info but a const we should be able to infer the type from the value of const
			String inferredType = getJsonTypeFromConstNode(propertyNode.get("const"));
			feature = createEStructuralFeatureFromJsonType(inferredType, name, propertyNode, rootNode);
		}
		else if(propertyNode.get("anyOf") != null) {
			feature = createMultiValueReference(propertyNode.get("anyOf"), name, rootNode);
			addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "source", "anyOf");
		} 
		else if(propertyNode.get("oneOf") != null) {
			feature = createMultiValueReference(propertyNode.get("oneOf"), name, rootNode);
			addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "source", "oneOf");
		} else {
			throw new IllegalArgumentException(String.format("Not supported case for property %s", name));
		}
		if(feature != null) {
			if(propertyNode.get("description") != null) {
				addEAnnotation(feature, GEN_MODEL_ANNOTATION_SOURCE, "documentation", propertyNode.get("description").asString());
			}
			if(propertyNode.get("format") != null) {
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "format", propertyNode.get("format").asString());
			}
			if(propertyNode.get("const") != null) {
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "const", propertyNode.get("const").isString() ? propertyNode.get("const").asString() : propertyNode.get("const").toPrettyString());
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "constType", propertyNode.get("const").isArray() ?  propertyNode.get("const").get(0).getNodeType().toString() : propertyNode.get("const").getNodeType().toString());
			}
			if(propertyNode.get("type") == null) {
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, isArrayItems? "noArrayItemsTypeInfo" : "noTypeInfo", "true");
			}
			if(propertyNode.get("uniqueItems") != null) {
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "uniqueItems", propertyNode.get("uniqueItems").asString());
			}
			if(propertyNode.get("writeOnly") != null) {
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "writeOnly", propertyNode.get("writeOnly").asString());
			}

		}
		return feature;
	}

	String getJsonTypeFromConstNode(JsonNode jsonNode) {
		if(jsonNode.isString()) return "string";
		if(jsonNode.isBigDecimal()) return "bigDecimal";
		if(jsonNode.isBigInteger()) return "bigInteger";
		if(jsonNode.isBinary()) return "binary"; 
		if(jsonNode.isBoolean()) return "boolean";
		if(jsonNode.isDouble()) return "number";
		if(jsonNode.isInt()) return "integer";
		if(jsonNode.isArray()) return "array";
		return "javaObject";
	}

	private EStructuralFeature createEStructuralFeatureFromJsonType(String type, String name, JsonNode propertyNode, JsonNode rootNode) {
		EStructuralFeature feature = null;
		switch(type) {
		case "array":
			//				We have an items schema
			if(propertyNode.get("items") != null) {
				feature = createEStructuralFeature(propertyNode.get("items"), name, rootNode, true);	
				addEAnnotation(feature, JSONSCHEMA_ANNOTATION_SOURCE, "items", "true");
			}
			//				We have const wo items: so we need to infer the type of the items from const
			else if(propertyNode.get("const") != null) {
				if(!propertyNode.get("const").isArray()) {
					throw new IllegalArgumentException(String.format("Expected ArrayNode for const attribute in array property %s. Instead got %s ", name, propertyNode.get("const").getNodeType()));
				}
				//				We look into the first element to try to infer the type
				String inferredArrayType = getJsonTypeFromConstNode(propertyNode.get("const").get(0));
				feature = createEStructuralFeatureFromJsonType(inferredArrayType, name, propertyNode.get("const"), rootNode);

			} else {
				throw new IllegalArgumentException(String.format("Not supported case for property %s", name));
			}
			if(propertyNode.get("minItems") != null) {
				feature.setLowerBound(propertyNode.get("minItems").asInt());
			} else {
				feature.setLowerBound(0);
			}
			if(propertyNode.get("maxItems") != null) {
				feature.setUpperBound(propertyNode.get("maxItems").asInt());
			} else {
				feature.setUpperBound(-1);

			}
			break;
		case "string":
			//				EAttribute of type Enum
			if(propertyNode.get("enum") != null) {
				System.out.println("Creating artificial class " + artificialClassifierCounter + " for property " + name);

				EEnum eEnum = createEEnum(propertyNode, ARTIFICIAL_CLASSIFIER_PREFIX+(artificialClassifierCounter++));
				addEAnnotation(eEnum, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");

				feature = ecoreFactory.createEAttribute();
				feature.setName(name);
				feature.setEType(eEnum);
			} else {
				//					EAttribute of type EString 
				feature = ecoreFactory.createEAttribute();
				feature.setName(name);
				feature.setEType(EcorePackage.Literals.ESTRING);
			}
			break;
		case "object":
			//				We have a contained EReference and we have to create the EClass for the EDataType of the reference
			EClass eClass = createEClass(propertyNode, ARTIFICIAL_CLASSIFIER_PREFIX+(artificialClassifierCounter++), rootNode);	
			addEAnnotation(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");
			feature = ecoreFactory.createEReference();
			feature.setName(name);
			feature.setEType(eClass);
			((EReference)feature).setContainment(true);
			break;
		default:
			//				EAttribute
			feature = ecoreFactory.createEAttribute();
			feature.setName(name);
			feature.setEType(getEcoreTypeForJsonType(type));
			break;
		}
		return feature;
	}

	private void addEAnnotation(EModelElement element, String source, String detailKey, String detailValue) {
		if(element.getEAnnotation(source) != null) {
			element.getEAnnotation(source).getDetails().put(detailKey, detailValue);
		} else {
			element.getEAnnotations().add(createEAnnotation(source, detailKey, detailValue));
		}
	}

	private EReference createMultiValueReference(JsonNode jsonNode, String name, JsonNode rootNode) {

		//		jsonNode is the anyOf node -> we expect that to be an array
		if(!jsonNode.isArray()) throw new IllegalArgumentException(String.format("anyOf node for property %s expected to be an ArrayNode. Instead is %s!", name, jsonNode.getNodeType().toString()));

		EReference reference = ecoreFactory.createEReference();
		String refAnnotation = "";
		Map<String, JsonNode> refClassesNodes = new HashMap<>();
		for(int i = 0; i < jsonNode.size(); i++) {
			JsonNode subNode = jsonNode.get(i);
			//			We have a reference to another class
			if(subNode.get("$ref") != null) {
				refAnnotation += subNode.get("$ref").asString() + ",";
				String refClassName = subNode.get("$ref").asString().replace("#/"+schemaFeature+"/", "");
				if(rootNode.get(refClassName) != null) {
					refClassesNodes.put(refClassName, rootNode.get(refClassName));
				} else {
					throw new IllegalArgumentException(String.format("Cannot find $ref class %s in root schema", refClassName));

				}
			} else {
				throw new IllegalArgumentException(String.format("anyOf with no $ref element for property %s. Case not supported yet!", name));
			}
		}
		if(!refAnnotation.isEmpty()) {
			refAnnotation = refAnnotation.substring(0, refAnnotation.length()-1); //get rid of last ","
			addEAnnotation(reference, JSONSCHEMA_ANNOTATION_SOURCE, "ref", refAnnotation);
		}

		//		Now we check if all the ref classes have a properties fields. If not we just create a marker interface
		boolean haveAllProperties = true;
		List<String> commonRequiredProperties = getCommonRequiredFields(refClassesNodes.values().stream().map(jn-> jn.get("required")).map(n -> (ArrayNode) n).toList());
		for(JsonNode jn : refClassesNodes.values()) {
			if(jn.get("properties") == null) haveAllProperties = false;
		}
		Map<String, JsonNode> commonProperties = Collections.emptyMap();
		if(haveAllProperties) {
			commonProperties = getCommonSubNodes(refClassesNodes.values().stream().map(jn-> jn.get("properties")).toList());
		} 
		EClass parent = null;
		if(!commonProperties.isEmpty()) {
			if(parentClassMaps.containsKey(commonProperties)) {
				parent = parentClassMaps.get(commonProperties);
			} 
		}
		if(parent == null) {
			EClass newParent = ecoreFactory.createEClass();			
			String parentName = getCommonSuffix(refClassesNodes.keySet().toArray(new String[0]));
			if(parentName == null) parentName = ARTIFICIAL_CLASSIFIER_PREFIX+(artificialClassifierCounter++);
			newParent.setName(parentName);
			addEAnnotation(newParent, JSONSCHEMA_ANNOTATION_SOURCE, "artificial", "true");
			if(!commonProperties.isEmpty()) {
				commonProperties.forEach((k,v) -> {
					EStructuralFeature feature = createEStructuralFeature(v, k, rootNode, false);
					if(feature != null) {
						if(commonRequiredProperties.contains(feature.getName())) {
							feature.setLowerBound(1);
						}
						newParent.getEStructuralFeatures().add(feature);
					}

				});
			}
			if(!commonProperties.isEmpty()) {
				parentClassMaps.put(commonProperties, newParent);
			}
			classifierMap.put(newParent.getName(), newParent);
			parent = newParent;
		}

		//		Now we have to remove the common properties from the ref classes
		if(!commonProperties.isEmpty()) {
			EClass p = parent;
			List<String> commonPropertiesNames = commonProperties.keySet().stream().toList();
			refClassesNodes.forEach((k,v) ->  {
				EClass subClass = createEClass(v, k, rootNode, commonPropertiesNames);
				if(subClass != null) {
					subClass.getESuperTypes().add(p);
				}
			});
		}


		reference.setName(name);
		reference.setEType(parent);
		reference.setContainment(false);		
		return reference;
	}

	public String getCommonSuffix(String... strings) {
		if (strings == null || strings.length == 0) {
			return null; // no input
		}

		String first = strings[0];
		if (first == null) {
			return null; // if first string is null, can't process
		}

		int minLength = first.length();

		// Find the shortest string length
		for (String str : strings) {
			if (str == null) {
				return null; // or handle as empty if you prefer
			}
			minLength = Math.min(minLength, str.length());
		}

		// Check from the end character by character
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

			if (!allMatch) {
				break;
			}
			suffixLength++;
		}

		if (suffixLength == 0) {
			return null; // no common suffix
		}

		return first.substring(first.length() - suffixLength);
	}

	public static List<String> getCommonRequiredFields(List<ArrayNode> requiredNodes) {
		List<String> commonRequired = new ArrayList<>();

		if (requiredNodes == null || requiredNodes.isEmpty()) {
			return commonRequired; // empty input = empty output
		}

		Set<String> commonSet = null;

		for (ArrayNode arrayNode : requiredNodes) {
			Set<String> currentSet = new HashSet<>();

			for (JsonNode fieldNode : arrayNode) {
				if (fieldNode.isString()) {
					currentSet.add(fieldNode.asString());
				}
			}

			if (commonSet == null) {
				// First node: initialize the set
				commonSet = currentSet;
			} else {
				// Intersect with previous sets
				commonSet.retainAll(currentSet);
			}
		}

		if (commonSet != null) {
			commonRequired.addAll(commonSet);
		}

		return commonRequired;
	}

	public static Map<String, JsonNode> getCommonSubNodes(List<JsonNode> nodes) {
		Map<String, JsonNode> commonFields = new HashMap<>();

		if (nodes == null || nodes.isEmpty()) {
			return commonFields; // empty result
		}

		// Take the first node as reference
		JsonNode reference = nodes.get(0);
		for(String fieldName : reference.propertyNames()) {

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

	private EEnum createEEnum(JsonNode enumNode, String name) {
		if(cachedClassifiers.containsKey(enumNode.toString())) {
			System.out.println("Found existing EEnum");
			return (EEnum)cachedClassifiers.get(enumNode.toString());
		} 
		EEnum eEnum = ecoreFactory.createEEnum();
		eEnum.setName(name);
		for(int e = 0; e < enumNode.get("enum").size(); e++) {
			EEnumLiteral literal = ecoreFactory.createEEnumLiteral();
			literal.setLiteral(enumNode.get("enum").get(e).asString());
			literal.setName(enumNode.get("enum").get(e).asString());
			literal.setValue(e);
			eEnum.getELiterals().add(literal);
		}	
		if(enumNode.get("description") != null) {
			addEAnnotation(eEnum, GEN_MODEL_ANNOTATION_SOURCE, "documentation", enumNode.get("description").asString());
		}
		cachedClassifiers.put(enumNode.toString(), eEnum);
		classifierMap.put(eEnum.getName(), eEnum);
		return eEnum;
	}

	public boolean arrayNodeContains(JsonNode jsonNode, String value) {
		if(!jsonNode.isArray()) return false;
		ArrayNode arrayNode = (ArrayNode) jsonNode;
		for (JsonNode node : arrayNode) {
			if (node.isString() && value.equals(node.asString())) {
				return true;
			}
		}
		return false;
	}

	private EDataType createEDataTypeFromJsonType(JsonNode dtNode, String name) {
		if(cachedClassifiers.containsKey(dtNode.toString())) {
			return (EDataType)cachedClassifiers.get(dtNode.toString());
		}

		EDataType dt = ecoreFactory.createEDataType();
		dt.setName(name);
		if(dtNode.get("description") != null) {
			addEAnnotation(dt, GEN_MODEL_ANNOTATION_SOURCE, "documentation",  dtNode.get("description").asString());
		}
		if(dtNode.get("type").isArray()) {
			StringBuilder sb = new StringBuilder();
			for(int t = 0; t < dtNode.get("type").size(); t++) {
				sb.append(dtNode.get("type").get(t).asString());
				if(t < (dtNode.get("type").size()-1)) {
					sb.append(",");
				}
			}
			addEAnnotation(dt, JSONSCHEMA_ANNOTATION_SOURCE, "dataType", sb.toString());
			dt.setInstanceClass(Object.class);
			dt.setInstanceClassName("java.lang.Object");
		} else {
			String jsonType = dtNode.get("type").asString();
			switch(jsonType) {
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

		return dt;
	}

	private EDataType getEcoreTypeForJsonType(String jsonType) {
		EcorePackage ecorePackage = EcorePackage.eINSTANCE;
		switch(jsonType) {
		case "string":
			return ecorePackage.getEString();
		case "integer":
			return ecorePackage.getEInt();
		case "number":
			return ecorePackage.getEDouble();
		case "boolean":
			return ecorePackage.getEBoolean();
		case "bigDecimal":
			return ecorePackage.getEBigDecimal();
		case "bigInteger":
			return ecorePackage.getEBigInteger();
		case "binary":
			return ecorePackage.getEByte(); //TODO: double check that
		default:
			return ecorePackage.getEJavaObject();
		}
	}

}
