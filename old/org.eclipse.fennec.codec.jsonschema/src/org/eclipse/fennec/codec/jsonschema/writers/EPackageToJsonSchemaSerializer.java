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

import java.util.LinkedList;
import java.util.List;

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

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 
 * @author ilenia
 * @since Aug 7, 2025
 */
public class EPackageToJsonSchemaSerializer extends ValueSerializer<EPackage> {
	
	private static final String GEN_MODEL_ANNOTATION_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";
	private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";

	private EPackage ePackage;
	private String schemaFeature;
	
	public EPackageToJsonSchemaSerializer() {
		
	}
	
	public EPackageToJsonSchemaSerializer(String schemaFeature) {
		this.schemaFeature = schemaFeature;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueSerializer#serialize(java.lang.Object, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@Override
	public void serialize(EPackage ePackage, JsonGenerator gen, SerializationContext ctxt) {
		this.ePackage = ePackage;
		gen.writeStartObject();
		if(extractAnnotationDetail(ePackage, JSONSCHEMA_ANNOTATION_SOURCE, "schema") != null) {
			gen.writeStringProperty("$schema", extractAnnotationDetail(ePackage, JSONSCHEMA_ANNOTATION_SOURCE, "schema"));
		}
		if(ePackage.getNsURI() != null) gen.writeStringProperty("$id", ePackage.getNsURI()); // Or a more suitable ID
		if(ePackage.getName() != null) gen.writeStringProperty("title", ePackage.getName());
		if(extractAnnotationDetail(ePackage, GEN_MODEL_ANNOTATION_SOURCE, "documentation") != null) {
			gen.writeStringProperty("description", extractAnnotationDetail(ePackage, GEN_MODEL_ANNOTATION_SOURCE, "documentation"));
		}

		if(schemaFeature != null) gen.writeObjectPropertyStart(schemaFeature); // For EClasses

		for (EClassifier classifier : ePackage.getEClassifiers()) {
			serializeEClassifier(classifier, gen, ctxt);
		}
		gen.writeEndObject(); // End definitions


	}

	private void serializeEClassifier(EClassifier classifier, JsonGenerator gen, SerializationContext ctxt) {
		String artificial = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "artificial");
		String topLevelArray = extractAnnotationDetail(classifier, JSONSCHEMA_ANNOTATION_SOURCE, "source");
		if("true".equals(artificial) && !"TopLevelArray".equals(topLevelArray)) return;
		gen.writeName(classifier.getName()); // EClassifier name
		if(classifier instanceof EClass eClass) {
			serializeEClass(eClass, gen, ctxt);
		} else if(classifier instanceof EEnum eEnum) {
			serializeEEnum(eEnum, gen, ctxt);
		} else if(classifier instanceof EDataType eDataType) {
			serializeEDataType(eDataType, gen, ctxt);
		}
	}

	private void serializeEClass(EClass eClass, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();
		String topLevelArray = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "source");
		String additionalProperties = extractAnnotationDetail(eClass, JSONSCHEMA_ANNOTATION_SOURCE, "additionalProperties");
		String description = extractAnnotationDetail(eClass, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		
		if(description != null) gen.writeStringProperty("description", description);
		if(additionalProperties != null) {
			if(additionalProperties.contains("{")) {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readTree(additionalProperties);
				gen.writeName("additionalProperties");
				gen.writeTree(node);
			} else {
				gen.writeBooleanProperty("additionalProperties", Boolean.valueOf(additionalProperties));
			}
		}

		if("TopLevelArray".equals(topLevelArray)) {
			gen.writeStringProperty("type", "array");
			EStructuralFeature items = eClass.getEStructuralFeature("items");
			if(items != null) {
				if(items instanceof EAttribute eAttribute) {
					serializeEAttribute(eAttribute, gen, ctxt);
				} else if(items instanceof EReference eReference) {
					serializeEReference(eReference, gen, ctxt);
				}
			}
		}
		else {
			
			List<EClass> artificialParents = eClass.getESuperTypes().stream().filter(st -> extractAnnotationDetail(st, JSONSCHEMA_ANNOTATION_SOURCE, "artificial") != null).toList();
			List<EClass> nonArtificialParents = eClass.getESuperTypes().stream().filter(st -> extractAnnotationDetail(st, JSONSCHEMA_ANNOTATION_SOURCE, "artificial") == null).toList();
			
			if(nonArtificialParents.isEmpty()) {
				gen.writeStringProperty("type", "object");
				boolean isPropertiesWritten = false;			
				List<String> requiredProperties = new LinkedList<>();
				for(EStructuralFeature feature : eClass.getEStructuralFeatures()) {			
					if(!isPropertiesWritten) {
						 gen.writeObjectPropertyStart("properties");
						 isPropertiesWritten = true;
					}
					if(feature.isRequired()) requiredProperties.add(feature.getName());
					if(feature instanceof EAttribute eAttribute) {
						serializeEAttribute(eAttribute, gen, ctxt);
					} else if(feature instanceof EReference eReference) {
						serializeEReference(eReference, gen, ctxt);
					}
				}
//				We check if the EClass inherits from an artificial EClass: if yes we write also the properties of the parent, 
				for(EClass st : artificialParents) {
					for(EStructuralFeature feature : st.getEStructuralFeatures()) {
						if(!isPropertiesWritten) {
							 gen.writeObjectPropertyStart("properties");
							 isPropertiesWritten = true;
						}
						if(feature.isRequired()) requiredProperties.add(feature.getName());
						if(feature instanceof EAttribute eAttribute) {
							serializeEAttribute(eAttribute, gen, ctxt);
						} else if(feature instanceof EReference eReference) {
							serializeEReference(eReference, gen, ctxt);
						}
					}				 
				};	
				
				if(isPropertiesWritten) gen.writeEndObject();
				if(!requiredProperties.isEmpty()) {
					gen.writeArrayPropertyStart("required");
					requiredProperties.forEach(r -> {
						gen.writeString(r);
					});
					gen.writeEndArray();
				}
			} else {
//				if not we translate it into an allOf
				gen.writeName("allOf");
				gen.writeStartArray();
				for(EClass st : nonArtificialParents) {
					gen.writeStartObject();
					gen.writeStringProperty("$ref", "#/"+schemaFeature+"/"+st.getName());
					gen.writeEndObject();
				}
				gen.writeStartObject();
				gen.writeStringProperty("type", "object");
				boolean isPropertiesWritten = false;			
				List<String> requiredProperties = new LinkedList<>();
				for(EStructuralFeature feature : eClass.getEStructuralFeatures()) {			
					if(!isPropertiesWritten) {
						 gen.writeObjectPropertyStart("properties");
						 isPropertiesWritten = true;
					}
					if(feature.isRequired()) requiredProperties.add(feature.getName());
					if(feature instanceof EAttribute eAttribute) {
						serializeEAttribute(eAttribute, gen, ctxt);
					} else if(feature instanceof EReference eReference) {
						serializeEReference(eReference, gen, ctxt);
					}
				}
//				We check if the EClass inherits from an artificial EClass: if yes we write also the properties of the parent, 
				for(EClass st : artificialParents) {
					for(EStructuralFeature feature : st.getEStructuralFeatures()) {
						if(!isPropertiesWritten) {
							 gen.writeObjectPropertyStart("properties");
							 isPropertiesWritten = true;
						}
						if(feature.isRequired()) requiredProperties.add(feature.getName());
						if(feature instanceof EAttribute eAttribute) {
							serializeEAttribute(eAttribute, gen, ctxt);
						} else if(feature instanceof EReference eReference) {
							serializeEReference(eReference, gen, ctxt);
						}
					}				 
				};	
				
				if(isPropertiesWritten) gen.writeEndObject();
				if(!requiredProperties.isEmpty()) {
					gen.writeArrayPropertyStart("required");
					requiredProperties.forEach(r -> {
						gen.writeString(r);
					});
					gen.writeEndArray();
				}
				
				gen.writeEndObject();
				gen.writeEndArray();
				
			}
		
		}
		gen.writeEndObject();
	}
	

	
	private void serializeEAttribute(EAttribute eAttribute, JsonGenerator gen, SerializationContext ctxt) {
		String documentation = extractAnnotationDetail(eAttribute, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		String noTypeInfo = extractAnnotationDetail(eAttribute, JSONSCHEMA_ANNOTATION_SOURCE, "noTypeInfo");
		String format = extractAnnotationDetail(eAttribute, JSONSCHEMA_ANNOTATION_SOURCE, "format");
		String writeOnly = extractAnnotationDetail(eAttribute, JSONSCHEMA_ANNOTATION_SOURCE, "writeOnly");
		String uniqueItems = extractAnnotationDetail(eAttribute, JSONSCHEMA_ANNOTATION_SOURCE, "uniqueItems");
		EDataType type = eAttribute.getEAttributeType();
		gen.writeName(eAttribute.getName());
		if(eAttribute.isMany()) {
			gen.writeStartObject();
			if(documentation != null) {
				gen.writeStringProperty("description", documentation);
			}					
			if(noTypeInfo == null || !("true".equals(noTypeInfo))) gen.writeStringProperty("type", "array");
			if(writeOnly != null) gen.writeBooleanProperty("writeOnly", Boolean.valueOf(writeOnly));
			if(uniqueItems != null) gen.writeBooleanProperty("uniqueItems", Boolean.valueOf(uniqueItems));
			serializeConstValue(eAttribute, true, gen, ctxt);
			String itemsAnnotation = extractAnnotationDetail(eAttribute, JSONSCHEMA_ANNOTATION_SOURCE, "items");
			if(itemsAnnotation != null && "true".equals(itemsAnnotation)) {
				serializeArrayItems(eAttribute, type, gen, ctxt);
			}
			gen.writeEndObject();
		} else {
			if(type instanceof EEnum eEnum) {
				gen.writeStartObject();
				if(documentation != null) {
					gen.writeStringProperty("description", documentation);
				}
				serializeEEnumLiterals(eEnum.getELiterals(), gen, ctxt);
				if(noTypeInfo == null || !("true".equals(noTypeInfo)))  gen.writeStringProperty("type", "string");
				serializeConstValue(eAttribute, false, gen, ctxt);
				if(format != null) gen.writeStringProperty("format", format);
				if(writeOnly != null) gen.writeBooleanProperty("writeOnly", Boolean.valueOf(writeOnly));
				if(uniqueItems != null) gen.writeBooleanProperty("uniqueItems", Boolean.valueOf(uniqueItems));
				gen.writeEndObject();
			} else {
				gen.writeStartObject();
				if(documentation != null) {
					gen.writeStringProperty("description", documentation);
				}
				serializeConstValue(eAttribute, false, gen, ctxt);
				if(format != null) gen.writeStringProperty("format", format);
				if(writeOnly != null) gen.writeBooleanProperty("writeOnly", Boolean.valueOf(writeOnly));
				if(uniqueItems != null) gen.writeBooleanProperty("uniqueItems", Boolean.valueOf(uniqueItems));
				if(noTypeInfo == null || !("true".equals(noTypeInfo)))  {
					String jsonType = getJsonTypeFromEDataType(type);
					if("javaObject".equals(jsonType)) {
						String dataTypeStr = extractAnnotationDetail(eAttribute, JSONSCHEMA_ANNOTATION_SOURCE, "dataType");
						if(dataTypeStr != null) {
							String[] dataTypeSplit = dataTypeStr.split(",");
							gen.writeArrayPropertyStart("type");
							for(String dt : dataTypeSplit) {
								gen.writeString(dt);
							}
							gen.writeEndArray();
						}						
					} else {
						gen.writeStringProperty("type", jsonType);
					}
				}
			
				gen.writeEndObject();
			}
		}
	}
	
	private void serializeEReference(EReference eReference, JsonGenerator gen, SerializationContext ctxt) {
		String documentation = extractAnnotationDetail(eReference, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		String noTypeInfo = extractAnnotationDetail(eReference, JSONSCHEMA_ANNOTATION_SOURCE, "noTypeInfo");
		String writeOnly = extractAnnotationDetail(eReference, JSONSCHEMA_ANNOTATION_SOURCE, "writeOnly");
		String uniqueItems = extractAnnotationDetail(eReference, JSONSCHEMA_ANNOTATION_SOURCE, "uniqueItems");
		EClassifier type = eReference.getEType();	
		gen.writeName(eReference.getName());
		if(eReference.isMany()) {
			gen.writeStartObject();
			if(documentation != null) gen.writeStringProperty("description", documentation);
			if(noTypeInfo == null || !("true".equals(noTypeInfo)))  gen.writeStringProperty("type", "array");
			if(writeOnly != null) gen.writeBooleanProperty("writeOnly", Boolean.valueOf(writeOnly));
			if(uniqueItems != null) gen.writeBooleanProperty("uniqueItems", Boolean.valueOf(uniqueItems));
			gen.writeName("items");
			if(eReference.isContainment()) {				
				serializeEClass((EClass) type, gen, ctxt);									
			} else {			
				gen.writeStartObject();
				String source = extractAnnotationDetail(eReference, JSONSCHEMA_ANNOTATION_SOURCE, "source");
				String refs = extractAnnotationDetail(eReference, JSONSCHEMA_ANNOTATION_SOURCE, "ref");
				if(source != null) {					
					gen.writeName(source);
					gen.writeStartArray();
					if(refs != null) {
						String[] refSplit = refs.split(",");
						for(String ref : refSplit) {
							gen.writeStartObject();
							gen.writeStringProperty("$ref", ref);
							gen.writeEndObject();
						}
					} else {
//						If we do not have any ref info, we put in the anyOf/oneOf block all the sub classes of the reference type
						ePackage.getEClassifiers().stream().
							filter(c -> c instanceof EClass).
							map(c -> (EClass) c).
							filter(c -> c.getESuperTypes().contains((EClass) type)).
							forEach(c -> {
								gen.writeStartObject();
								gen.writeStringProperty("$ref", "#/"+schemaFeature+"/"+c.getName());
								gen.writeEndObject();
							});						
					}					
					gen.writeEndArray();
				} else {
					if(refs != null) {
						gen.writeStringProperty("$ref", refs);
					} else {
//						we do not have neither a source nor a ref annotations. So we serialize as an anyOf using all the sub classes of the reference type
						gen.writeName("anyOf");
						gen.writeStartArray();
						ePackage.getEClassifiers().stream().
						filter(c -> c instanceof EClass).
						map(c -> (EClass) c).
						filter(c -> c.getESuperTypes().contains((EClass) type)).
						forEach(c -> {
							gen.writeStartObject();
							gen.writeStringProperty("$ref", "#/"+schemaFeature+"/"+c.getName());
							gen.writeEndObject();
						});		
						gen.writeEndArray();
					}
				}
				gen.writeEndObject();
			}
			gen.writeEndObject();
		} else {
			if(eReference.isContainment()) {				
				serializeEClass((EClass) type, gen, ctxt);
			} else {
				gen.writeStartObject();
				String ref = extractAnnotationDetail(eReference, JSONSCHEMA_ANNOTATION_SOURCE, "ref");
				if(ref != null) {
					gen.writeStringProperty("$ref", ref);
				} else {
					gen.writeStringProperty("$ref", "#/"+schemaFeature+"/"+type.getName());
				}
				gen.writeEndObject();
			}
		}
	}
	
	private void serializeConstValue(ENamedElement element, boolean isInArray, JsonGenerator gen, SerializationContext ctxt) {
		String constAnnotation = extractAnnotationDetail(element, JSONSCHEMA_ANNOTATION_SOURCE, "const");
		String constType = extractAnnotationDetail(element, JSONSCHEMA_ANNOTATION_SOURCE, "constType");
		if(constAnnotation != null && constType != null) {
			if(isInArray) {
				gen.writeArrayPropertyStart("const");
				String[] parts = constAnnotation.replaceAll("\\[|\\]", "").split(",");
				for(String part : parts) {
					part = part.trim();
					switch(constType) {
					case "NUMBER":
						try {
							gen.writeNumber(Integer.parseInt(part));
						} catch(Exception e) {
							gen.writeNumber(Float.parseFloat(part));
						}								
						break;
					case "STRING":
						gen.writeString(part);
						break;
					case "BOOLEAN":
						gen.writeBoolean(Boolean.parseBoolean(part));
						break;
					default:
						throw new IllegalArgumentException(String.format("constType %s found in EAttribute %s currently not supported", constType, element.getName()));
					
					}
				}
				gen.writeEndArray();
			} else {
				gen.writeName("const");
				switch(constType) {
				case "NUMBER":
					try {
						gen.writeNumber(Integer.parseInt(constAnnotation));
					} catch(Exception e) {
						gen.writeNumber(Float.parseFloat(constAnnotation));
					}								
					break;
				case "STRING":
					gen.writeString(constAnnotation);
					break;
				case "BOOLEAN":
					gen.writeBoolean(Boolean.parseBoolean(constAnnotation));
					break;
				default:
					throw new IllegalArgumentException(String.format("constType %s found in EAttribute %s currently not supported", constType, element.getName()));
				
				}
			}			
		}
	}
	
	private void serializeArrayItems(EStructuralFeature feature, EDataType type, JsonGenerator gen, SerializationContext ctxt) {
		String noArrayItemsTypeInfo = extractAnnotationDetail(feature, JSONSCHEMA_ANNOTATION_SOURCE, "noArrayItemsTypeInfo");
		String format = extractAnnotationDetail(feature, JSONSCHEMA_ANNOTATION_SOURCE, "format");
		gen.writeObjectPropertyStart("items");
		if(format != null) gen.writeStringProperty("format", format);
		if(type instanceof EEnum eEnum) {
			serializeEEnumLiterals(eEnum.getELiterals(), gen, ctxt);
			if(noArrayItemsTypeInfo == null || !"true".equals(noArrayItemsTypeInfo)) gen.writeStringProperty("type", "string");
		} else if(noArrayItemsTypeInfo == null || !"true".equals(noArrayItemsTypeInfo)){
			String jsonType = getJsonTypeFromEDataType(type);
			if("javaObject".equals(jsonType)) {
				String dataTypeStr = extractAnnotationDetail(feature, JSONSCHEMA_ANNOTATION_SOURCE, "dataType");
				if(dataTypeStr != null) {
					String[] dataTypeSplit = dataTypeStr.split(",");
					gen.writeArrayPropertyStart("type");
					for(String dt : dataTypeSplit) {
						gen.writeString(dt);
					}
					gen.writeEndArray();
				}						
			} else {
				gen.writeStringProperty("type", jsonType);
			}
		}
		gen.writeEndObject();
	}

	private String getJsonTypeFromEDataType(EDataType eDataType) {
		if(EcorePackage.Literals.ESTRING.equals(eDataType)) return "string";
		if(EcorePackage.Literals.EFLOAT.equals(eDataType) || EcorePackage.Literals.EFLOAT_OBJECT.equals(eDataType)) return "number";
		if(EcorePackage.Literals.EDOUBLE.equals(eDataType) || EcorePackage.Literals.EDOUBLE_OBJECT.equals(eDataType)) return "number";
		if(EcorePackage.Literals.EINT.equals(eDataType) || EcorePackage.Literals.EINTEGER_OBJECT.equals(eDataType)) return "integer";
		if(EcorePackage.Literals.EBIG_DECIMAL.equals(eDataType)) return "number";
		if(EcorePackage.Literals.EBIG_INTEGER.equals(eDataType)) return "integer";
		if(EcorePackage.Literals.EBYTE.equals(eDataType)) return "binary";
		if(EcorePackage.Literals.EBOOLEAN.equals(eDataType) || EcorePackage.Literals.EBOOLEAN_OBJECT.equals(eDataType)) return "boolean";
		return "javaObject";
	}

	private void serializeEEnum(EEnum eEnum, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeStartObject();
		if(extractAnnotationDetail(eEnum, GEN_MODEL_ANNOTATION_SOURCE, "documentation") != null) {
			gen.writeStringProperty("description", extractAnnotationDetail(eEnum, GEN_MODEL_ANNOTATION_SOURCE, "documentation"));
		}
		serializeEEnumLiterals(eEnum.getELiterals(), gen, ctxt);
		gen.writeStringProperty("type", "string");
		gen.writeEndObject();
	}
	
	private void serializeEEnumLiterals(List<EEnumLiteral> literals, JsonGenerator gen, SerializationContext ctxt) {
		gen.writeArrayPropertyStart("enum");
		for(EEnumLiteral literal : literals) {
			gen.writeString(literal.getName());
		}
		gen.writeEndArray();
	}

	private void serializeEDataType(EDataType eDataType, JsonGenerator gen, SerializationContext ctxt) {
		String documentation = extractAnnotationDetail(eDataType, GEN_MODEL_ANNOTATION_SOURCE, "documentation");
		String dataType = extractAnnotationDetail(eDataType, JSONSCHEMA_ANNOTATION_SOURCE, "dataType");
		gen.writeStartObject();
		if(documentation != null) gen.writeStringProperty("description", documentation);
		if(dataType != null) {
			gen.writeName("type");
			gen.writeStartArray();
			String[] dataTypeSplit = dataType.split(",");
			for(String dt : dataTypeSplit) {
				gen.writeString(dt);
			}
			gen.writeEndArray();
		} else {
			gen.writeStringProperty("type", getJsonTypeFromEDataTypeInstanceClassName(eDataType.getInstanceClassName()));
		}
		gen.writeEndObject();
	}
	
	private String getJsonTypeFromEDataTypeInstanceClassName(String instanceClassName) {
		switch(instanceClassName) {
			case "java.lang.String": return "string";
			case "java.lang.Integer", "java.math.BigInteger": return "integer";
			case "java.lang.Boolean": return "boolean";
			case "java.lang.Double", "java.lang.Float", "java.math.BigDecimal": return "number";
			default: throw new IllegalArgumentException(String.format("Case not supported for EDataType instanceClassName %s", instanceClassName));
		}
	}

	private String extractAnnotationDetail(EModelElement modelElement, String source, String detailKey) {
		EAnnotation annotation = modelElement.getEAnnotation(source);
		if(annotation == null) return null;
		if(!annotation.getDetails().containsKey(detailKey)) return null;
		return annotation.getDetails().get(detailKey);
	}

}
