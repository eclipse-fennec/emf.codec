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
package org.eclipse.fennec.codec.jsonschema.v2.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.jsonschema.model.Dependency;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaFactory;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

/**
 * Reads a JSON Schema into the JSON Schema model and writes it back, without loss (issue #317):
 * the written JSON equals the read one, up to the order of the keywords of an object.
 * <p>
 * Keywords map to the features of the model by their JSON key (the codec {@code key} annotation, or
 * the feature name). The keywords whose JSON form depends on the draft are handled explicitly:
 * boolean schemas, {@code type} as a string or a list, {@code items} as a schema or a list
 * (draft-04 to 2019-09), {@code exclusiveMinimum} / {@code exclusiveMaximum} as a boolean (draft-04,
 * OpenAPI 3.0) or a number, {@code dependencies} as a schema or a list of names. A value whose JSON
 * form does not fit its feature, and a keyword without a feature, is kept in {@code keywords} as it
 * is - nothing is dropped.
 * </p>
 */
public final class SchemaJson {

	private static final JsonMapper MAPPER = JsonMapper.builder()
			.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
			.build();
	private static final JsonNodeFactory NODES = JsonNodeFactory.instance;
	private static final String CODEC = "http://eclipse.org/fennec/codec";
	private static final JsonSchemaPackage P = JsonSchemaPackage.eINSTANCE;
	private static final Map<EClass, Map<String, EStructuralFeature>> BY_KEY = new HashMap<>();

	private SchemaJson() {
	}

	// ---------------------------------------------------------------- reading

	/**
	 * @param json a schema: an object or a boolean
	 * @return the schema, {@code null} for {@code null}
	 */
	public static Schema read(JsonNode json) {
		if (json == null || json.isNull() || json.isMissingNode()) {
			return null;
		}
		Schema schema = JsonSchemaFactory.eINSTANCE.createSchema();
		if (json.isBoolean()) {
			schema.setBooleanSchema(json.asBoolean());
			return schema;
		}
		if (!json.isObject()) {
			schema.getKeywords().put("", toJava(json)); // not a schema at all; kept
			return schema;
		}
		for (Map.Entry<String, JsonNode> member : json.properties()) {
			String key = member.getKey();
			JsonNode value = member.getValue();
			if (!readSpecial(schema, key, value) && !readFeature(schema, key, value)) {
				schema.getKeywords().put(key, toJava(value));
			}
		}
		return schema;
	}

	private static boolean readSpecial(Schema schema, String key, JsonNode value) {
		switch (key) {
			case "type":
				if (value.isString() && simpleType(value.asString()) != null) {
					schema.getType().add(simpleType(value.asString()));
					return true;
				}
				if (value.isArray()) {
					for (JsonNode t : value) {
						if (!t.isString() || simpleType(t.asString()) == null) {
							schema.getType().clear();
							return false;
						}
						schema.getType().add(simpleType(t.asString()));
					}
					schema.setTypeAsList(true);
					markSet(schema, P.getSchema_Type());
					return true;
				}
				return false;
			case "items":
				if (value.isArray()) {
					for (JsonNode item : value) {
						schema.getItemsList().add(read(item));
					}
					markSet(schema, P.getSchema_ItemsList());
					return true;
				}
				if (value.isObject() || value.isBoolean()) {
					schema.setItems(read(value));
					return true;
				}
				return false;
			case "exclusiveMinimum":
				if (value.isBoolean()) {
					schema.setExclusiveMinimumFlag(value.asBoolean());
					return true;
				}
				if (value.isNumber()) {
					schema.setExclusiveMinimum(value.decimalValue());
					return true;
				}
				return false;
			case "exclusiveMaximum":
				if (value.isBoolean()) {
					schema.setExclusiveMaximumFlag(value.asBoolean());
					return true;
				}
				if (value.isNumber()) {
					schema.setExclusiveMaximum(value.decimalValue());
					return true;
				}
				return false;
			case "dependencies":
				if (!value.isObject()) {
					return false;
				}
				for (Map.Entry<String, JsonNode> entry : value.properties()) {
					Dependency dependency = JsonSchemaFactory.eINSTANCE.createDependency();
					if (entry.getValue().isArray()) {
						for (JsonNode name : entry.getValue()) {
							if (!name.isString()) {
								return false;
							}
							dependency.getProperties().add(name.asString());
						}
					} else {
						dependency.setSchema(read(entry.getValue()));
					}
					schema.getDependencies().put(entry.getKey(), dependency);
				}
				markSet(schema, P.getSchema_Dependencies());
				return true;
			default:
				return false;
		}
	}

	/** A keyword of a feature of the model; {@code false} if there is none or the JSON form does not fit. */
	@SuppressWarnings("unchecked")
	private static boolean readFeature(EObject owner, String key, JsonNode value) {
		EStructuralFeature feature = featureFor(owner.eClass(), key);
		if (feature == null) {
			return false;
		}
		if (feature instanceof EAttribute attribute) {
			if (attribute.isMany()) {
				if (!value.isArray()) {
					return false;
				}
				EList<Object> list = (EList<Object>) owner.eGet(attribute);
				for (JsonNode item : value) {
					Object converted = attributeValue(attribute, item);
					if (converted == null && !(attribute.getEType().getInstanceClass() == Object.class)) {
						list.clear();
						return false;
					}
					list.add(converted);
				}
				markSet(owner, attribute);
				return true;
			}
			Object converted = attributeValue(attribute, value);
			if (converted == null && !value.isNull()) {
				return false;
			}
			owner.eSet(attribute, converted);
			return true;
		}
		EReference reference = (EReference) feature;
		EClass type = reference.getEReferenceType();
		if (type == P.getSchema()) {
			if (reference.isMany()) {
				if (!value.isArray()) {
					return false;
				}
				EList<Schema> list = (EList<Schema>) owner.eGet(reference);
				value.forEach(item -> list.add(read(item)));
				markSet(owner, reference);
			} else {
				owner.eSet(reference, read(value));
			}
			return true;
		}
		if (type.getInstanceClass() == Map.Entry.class) {
			return readMap(owner, reference, value);
		}
		if (!value.isObject()) {
			return false;
		}
		owner.eSet(reference, readObject(type, value));
		return true;
	}

	@SuppressWarnings("unchecked")
	private static boolean readMap(EObject owner, EReference map, JsonNode value) {
		if (!value.isObject()) {
			return false;
		}
		EClass entryClass = map.getEReferenceType();
		EStructuralFeature valueFeature = entryClass.getEStructuralFeature("value");
		EMap<String, Object> target = (EMap<String, Object>) owner.eGet(map);
		Map<String, Object> converted = new LinkedHashMap<>();
		for (Map.Entry<String, JsonNode> entry : value.properties()) {
			JsonNode item = entry.getValue();
			Object v;
			if (valueFeature.getEType() == P.getSchema()) {
				v = read(item);
			} else if (valueFeature instanceof EAttribute attribute && attribute.isMany()) {
				if (!item.isArray()) {
					return false;
				}
				EList<Object> list = new BasicEList<>();
				for (JsonNode element : item) {
					Object e = attributeValue(attribute, element);
					if (e == null) {
						return false;
					}
					list.add(e);
				}
				v = list;
			} else if (valueFeature instanceof EAttribute attribute) {
				v = attributeValue(attribute, item);
				if (v == null && !item.isNull()) {
					return false;
				}
			} else {
				if (!item.isObject()) {
					return false;
				}
				v = readObject(((EReference) valueFeature).getEReferenceType(), item);
			}
			converted.put(entry.getKey(), v);
		}
		target.putAll(converted);
		markSet(owner, map);
		return true;
	}

	/**
	 * Marks an empty many-valued feature as set: an empty list or object that was written ({@code []},
	 * {@code {}}) is written again. The features are unsettable; a list becomes set by a change.
	 */
	@SuppressWarnings("unchecked")
	private static void markSet(EObject owner, EStructuralFeature feature) {
		if (owner.eIsSet(feature)) {
			return;
		}
		EList<Object> list = (EList<Object>) owner.eGet(feature);
		Object placeholder;
		if (feature instanceof EReference reference) {
			placeholder = EcoreUtil.create(reference.getEReferenceType());
		} else if (feature.getEType() instanceof EEnum eEnum) {
			placeholder = eEnum.getELiterals().get(0).getInstance();
		} else {
			placeholder = "";
		}
		list.add(placeholder);
		list.remove(list.size() - 1);
	}

	/** Discriminator, Xml, ExternalDocumentation: known members, the rest into their catch-all map. */
	@SuppressWarnings("unchecked")
	private static EObject readObject(EClass type, JsonNode json) {
		EObject object = EcoreUtil.create(type);
		EStructuralFeature extensions = type.getEStructuralFeature("extensions");
		for (Map.Entry<String, JsonNode> member : json.properties()) {
			if (!readFeature(object, member.getKey(), member.getValue()) && extensions != null) {
				((EMap<String, Object>) object.eGet(extensions)).put(member.getKey(), toJava(member.getValue()));
			}
		}
		return object;
	}

	/** The value for an attribute, {@code null} if the JSON form does not fit. */
	private static Object attributeValue(EAttribute attribute, JsonNode value) {
		Class<?> type = attribute.getEType().getInstanceClass();
		if (type == Object.class) {
			return toJava(value);
		}
		if (type == String.class) {
			return value.isString() ? value.asString() : null;
		}
		if (type == Boolean.class || type == boolean.class) {
			return value.isBoolean() ? value.asBoolean() : null;
		}
		if (type == Integer.class) {
			return value.isIntegralNumber() && value.canConvertToInt() ? value.asInt() : null;
		}
		if (type == BigDecimal.class) {
			return value.isNumber() ? value.decimalValue() : null;
		}
		return null;
	}

	private static SimpleType simpleType(String name) {
		return SimpleType.get(name);
	}

	private static EStructuralFeature featureFor(EClass type, String key) {
		Map<String, EStructuralFeature> features = BY_KEY.computeIfAbsent(type, SchemaJson::index);
		return features.get(key);
	}

	/** The features by their JSON key; the ones read only explicitly are left out. */
	private static Map<String, EStructuralFeature> index(EClass type) {
		Map<String, EStructuralFeature> features = new HashMap<>();
		List<EStructuralFeature> explicit = List.of(P.getSchema_BooleanSchema(), P.getSchema_TypeAsList(),
				P.getSchema_Type(), P.getSchema_Items(), P.getSchema_ItemsList(), P.getSchema_ExclusiveMinimum(),
				P.getSchema_ExclusiveMinimumFlag(), P.getSchema_ExclusiveMaximum(), P.getSchema_ExclusiveMaximumFlag(),
				P.getSchema_Dependencies(), P.getSchema_Keywords(), P.getSchema_Classifier());
		for (EStructuralFeature feature : type.getEAllStructuralFeatures()) {
			if (explicit.contains(feature) || feature.isTransient() || "extensions".equals(feature.getName())) {
				continue;
			}
			String key = EcoreUtil.getAnnotation(feature, CODEC, "key");
			features.put(key != null ? key : feature.getName(), feature);
		}
		return features;
	}

	private static Object toJava(JsonNode value) {
		return MAPPER.treeToValue(value, Object.class);
	}

	// ---------------------------------------------------------------- writing

	/**
	 * @param schema a schema of the model
	 * @return its JSON: an object, or a boolean for a boolean schema
	 */
	public static JsonNode write(Schema schema) {
		if (schema == null) {
			return NODES.nullNode();
		}
		if (schema.getBooleanSchema() != null) {
			return NODES.booleanNode(schema.getBooleanSchema());
		}
		if (schema.getKeywords().containsKey("") && schema.getKeywords().size() == 1) {
			return MAPPER.valueToTree(schema.getKeywords().get(""));
		}
		ObjectNode json = NODES.objectNode();
		for (EStructuralFeature feature : schema.eClass().getEAllStructuralFeatures()) {
			writeFeature(schema, feature, json);
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	private static void writeFeature(Schema schema, EStructuralFeature feature, ObjectNode json) {
		if (feature == P.getSchema_Type()) {
			if (schema.eIsSet(feature)) {
				if (schema.getType().size() == 1 && !schema.isTypeAsList()) {
					json.put("type", schema.getType().get(0).getLiteral());
				} else {
					ArrayNode types = json.putArray("type");
					schema.getType().forEach(t -> types.add(t.getLiteral()));
				}
			}
		} else if (feature == P.getSchema_Items()) {
			if (schema.getItems() != null) {
				json.set("items", write(schema.getItems()));
			}
		} else if (feature == P.getSchema_ItemsList()) {
			if (schema.eIsSet(feature)) {
				ArrayNode items = json.putArray("items");
				schema.getItemsList().forEach(item -> items.add(write(item)));
			}
		} else if (feature == P.getSchema_ExclusiveMinimum() || feature == P.getSchema_ExclusiveMaximum()) {
			if (schema.eIsSet(feature)) {
				json.put(feature.getName(), (BigDecimal) schema.eGet(feature));
			}
		} else if (feature == P.getSchema_ExclusiveMinimumFlag()) {
			if (schema.getExclusiveMinimumFlag() != null) {
				json.put("exclusiveMinimum", schema.getExclusiveMinimumFlag());
			}
		} else if (feature == P.getSchema_ExclusiveMaximumFlag()) {
			if (schema.getExclusiveMaximumFlag() != null) {
				json.put("exclusiveMaximum", schema.getExclusiveMaximumFlag());
			}
		} else if (feature == P.getSchema_Dependencies()) {
			if (schema.eIsSet(feature)) {
				ObjectNode dependencies = json.putObject("dependencies");
				for (Map.Entry<String, Dependency> entry : schema.getDependencies()) {
					Dependency dependency = entry.getValue();
					if (dependency.getSchema() != null) {
						dependencies.set(entry.getKey(), write(dependency.getSchema()));
					} else {
						ArrayNode names = dependencies.putArray(entry.getKey());
						dependency.getProperties().forEach(names::add);
					}
				}
			}
		} else if (feature == P.getSchema_Keywords()) {
			for (Map.Entry<String, Object> keyword : schema.getKeywords()) {
				json.set(keyword.getKey(), MAPPER.valueToTree(keyword.getValue()));
			}
		} else if (feature != P.getSchema_BooleanSchema() && feature != P.getSchema_TypeAsList() && !feature.isTransient()) {
			writeGeneric(schema, feature, json);
		}
	}

	@SuppressWarnings("unchecked")
	private static void writeGeneric(EObject owner, EStructuralFeature feature, ObjectNode json) {
		if (!owner.eIsSet(feature)) {
			return;
		}
		String key = EcoreUtil.getAnnotation(feature, CODEC, "key");
		key = key != null ? key : feature.getName();
		Object value = owner.eGet(feature);
		if (feature instanceof EAttribute) {
			json.set(key, MAPPER.valueToTree(value));
			return;
		}
		EReference reference = (EReference) feature;
		EClass type = reference.getEReferenceType();
		if (type == P.getSchema()) {
			if (reference.isMany()) {
				ArrayNode list = json.putArray(key);
				((List<Schema>) value).forEach(s -> list.add(write(s)));
			} else {
				json.set(key, write((Schema) value));
			}
		} else if (type.getInstanceClass() == Map.Entry.class) {
			ObjectNode map = json.putObject(key);
			for (Map.Entry<String, Object> entry : (EMap<String, Object>) value) {
				Object v = entry.getValue();
				if (v instanceof Schema s) {
					map.set(entry.getKey(), write(s));
				} else if (v instanceof EObject o) {
					map.set(entry.getKey(), writeObject(o));
				} else {
					map.set(entry.getKey(), MAPPER.valueToTree(v));
				}
			}
		} else {
			json.set(key, writeObject((EObject) value));
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjectNode writeObject(EObject object) {
		ObjectNode json = NODES.objectNode();
		for (EStructuralFeature feature : object.eClass().getEAllStructuralFeatures()) {
			if ("extensions".equals(feature.getName())) {
				for (Map.Entry<String, Object> entry : (EMap<String, Object>) object.eGet(feature)) {
					json.set(entry.getKey(), MAPPER.valueToTree(entry.getValue()));
				}
			} else {
				writeGeneric(object, feature, json);
			}
		}
		return json;
	}
}
