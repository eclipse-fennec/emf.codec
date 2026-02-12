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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JsonSchemaToEPackageConverter#convertFromSchemaMap(EMap)}.
 * <p>
 * This method is used by OpenAPI resource to convert deserialized Schema objects
 * (from components/schemas) into an EMF-native EPackage representation.
 * </p>
 */
@DisplayName("Schema Map Conversion Tests")
class SchemaMapConversionTest {

	private JsonSchemaToEPackageConverter converter;
	private EClass schemaClass;
	private EPackage schemaPackage;

	@BeforeEach
	void setUp() {
		converter = new JsonSchemaToEPackageConverter();
		schemaPackage = createMockSchemaPackage();
		schemaClass = (EClass) schemaPackage.getEClassifier("Schema");
	}

	/**
	 * Creates a mock Schema EClass that mimics the OpenAPI Schema structure.
	 * This allows us to test conversion without depending on the OpenAPI model.
	 */
	private EPackage createMockSchemaPackage() {
		EcoreFactory factory = EcoreFactory.eINSTANCE;

		EPackage pkg = factory.createEPackage();
		pkg.setName("mockOpenapi");
		pkg.setNsURI("http://test/mock/openapi");
		pkg.setNsPrefix("mock");

		EClass schema = factory.createEClass();
		schema.setName("Schema");

		// type attribute
		EAttribute typeAttr = factory.createEAttribute();
		typeAttr.setName("type");
		typeAttr.setEType(EcorePackage.Literals.ESTRING);
		schema.getEStructuralFeatures().add(typeAttr);

		// description attribute
		EAttribute descAttr = factory.createEAttribute();
		descAttr.setName("description");
		descAttr.setEType(EcorePackage.Literals.ESTRING);
		schema.getEStructuralFeatures().add(descAttr);

		// ref attribute ($ref)
		EAttribute refAttr = factory.createEAttribute();
		refAttr.setName("ref");
		refAttr.setEType(EcorePackage.Literals.ESTRING);
		schema.getEStructuralFeatures().add(refAttr);

		// enum attribute (multi-valued)
		EAttribute enumAttr = factory.createEAttribute();
		enumAttr.setName("enum");
		enumAttr.setEType(EcorePackage.Literals.ESTRING);
		enumAttr.setUpperBound(-1);
		schema.getEStructuralFeatures().add(enumAttr);

		// required attribute (multi-valued)
		EAttribute requiredAttr = factory.createEAttribute();
		requiredAttr.setName("required");
		requiredAttr.setEType(EcorePackage.Literals.ESTRING);
		requiredAttr.setUpperBound(-1);
		schema.getEStructuralFeatures().add(requiredAttr);

		// items reference (for array type)
		EReference itemsRef = factory.createEReference();
		itemsRef.setName("items");
		itemsRef.setEType(schema);
		itemsRef.setContainment(true);
		schema.getEStructuralFeatures().add(itemsRef);

		// Create PropertyEntry class for EMap
		EClass propertyEntry = factory.createEClass();
		propertyEntry.setName("PropertyEntry");
		propertyEntry.setInstanceClassName("java.util.Map$Entry");

		EAttribute keyAttr = factory.createEAttribute();
		keyAttr.setName("key");
		keyAttr.setEType(EcorePackage.Literals.ESTRING);
		propertyEntry.getEStructuralFeatures().add(keyAttr);

		EReference valueRef = factory.createEReference();
		valueRef.setName("value");
		valueRef.setEType(schema);
		valueRef.setContainment(true);
		propertyEntry.getEStructuralFeatures().add(valueRef);

		// properties reference (EMap-like list)
		EReference propsRef = factory.createEReference();
		propsRef.setName("properties");
		propsRef.setEType(propertyEntry);
		propsRef.setContainment(true);
		propsRef.setUpperBound(-1);
		schema.getEStructuralFeatures().add(propsRef);

		pkg.getEClassifiers().add(schema);
		pkg.getEClassifiers().add(propertyEntry);

		return pkg;
	}

	private EObject createSchema(String type) {
		EObject schema = schemaPackage.getEFactoryInstance().create(schemaClass);
		if (type != null) {
			schema.eSet(schemaClass.getEStructuralFeature("type"), type);
		}
		return schema;
	}

	private EObject createSchema(String type, String description) {
		EObject schema = createSchema(type);
		if (description != null) {
			schema.eSet(schemaClass.getEStructuralFeature("description"), description);
		}
		return schema;
	}

	private void addProperty(EObject schema, String name, EObject propertySchema) {
		EClass propertyEntryClass = (EClass) schemaPackage.getEClassifier("PropertyEntry");
		EObject entry = schemaPackage.getEFactoryInstance().create(propertyEntryClass);
		entry.eSet(propertyEntryClass.getEStructuralFeature("key"), name);
		entry.eSet(propertyEntryClass.getEStructuralFeature("value"), propertySchema);

		@SuppressWarnings("unchecked")
		var properties = (org.eclipse.emf.common.util.EList<EObject>) schema.eGet(schemaClass.getEStructuralFeature("properties"));
		properties.add(entry);
	}

	private void setRequired(EObject schema, String... fields) {
		@SuppressWarnings("unchecked")
		var required = (java.util.List<String>) schema.eGet(schemaClass.getEStructuralFeature("required"));
		for (String field : fields) {
			required.add(field);
		}
	}

	private void setEnumValues(EObject schema, String... values) {
		@SuppressWarnings("unchecked")
		var enumList = (java.util.List<String>) schema.eGet(schemaClass.getEStructuralFeature("enum"));
		for (String value : values) {
			enumList.add(value);
		}
	}

	// ========================================================================
	// Basic Conversion Tests
	// ========================================================================

	@Nested
	@DisplayName("Basic Conversion")
	class BasicConversion {

		@Test
		@DisplayName("returns null for null input")
		void returnsNullForNullInput() {
			EPackage result = converter.convertFromSchemaMap(null);
			assertNull(result);
		}

		@Test
		@DisplayName("returns null for empty map")
		void returnsNullForEmptyMap() {
			EMap<String, EObject> emptyMap = new BasicEMap<>();
			EPackage result = converter.convertFromSchemaMap(emptyMap);
			assertNull(result);
		}

		@Test
		@DisplayName("creates EPackage with default values")
		void createsEPackageWithDefaultValues() {
			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", createSchema("object"));

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			assertNotNull(result);
			assertEquals("schemas", result.getName());
			assertEquals("http://generated/schemas", result.getNsURI());
			assertEquals("schemas", result.getNsPrefix());
		}

		@Test
		@DisplayName("converts simple object schema to EClass")
		void convertsSimpleObjectSchemaToEClass() {
			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", createSchema("object", "A person entity"));

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			assertNotNull(result);
			assertEquals(1, result.getEClassifiers().size());

			EClass personClass = (EClass) result.getEClassifier("Person");
			assertNotNull(personClass);
			assertEquals("Person", personClass.getName());
		}

		@Test
		@DisplayName("capitalizes first letter of class name")
		void capitalizesFirstLetterOfClassName() {
			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("person", createSchema("object"));

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass personClass = (EClass) result.getEClassifier("Person");
			assertNotNull(personClass, "Should create EClass with capitalized name");
		}
	}

	// ========================================================================
	// Property Conversion Tests
	// ========================================================================

	@Nested
	@DisplayName("Property Conversion")
	class PropertyConversion {

		@Test
		@DisplayName("converts string property to EAttribute")
		void convertsStringPropertyToEAttribute() {
			EObject personSchema = createSchema("object");
			addProperty(personSchema, "name", createSchema("string"));

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", personSchema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass personClass = (EClass) result.getEClassifier("Person");
			EAttribute nameAttr = (EAttribute) personClass.getEStructuralFeature("name");
			assertNotNull(nameAttr);
			assertEquals(EcorePackage.Literals.ESTRING, nameAttr.getEType());
		}

		@Test
		@DisplayName("converts integer property to EAttribute")
		void convertsIntegerPropertyToEAttribute() {
			EObject personSchema = createSchema("object");
			addProperty(personSchema, "age", createSchema("integer"));

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", personSchema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass personClass = (EClass) result.getEClassifier("Person");
			EAttribute ageAttr = (EAttribute) personClass.getEStructuralFeature("age");
			assertNotNull(ageAttr);
			assertEquals(EcorePackage.Literals.EINT, ageAttr.getEType());
		}

		@Test
		@DisplayName("converts boolean property to EAttribute")
		void convertsBooleanPropertyToEAttribute() {
			EObject schema = createSchema("object");
			addProperty(schema, "active", createSchema("boolean"));

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Item", schema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass itemClass = (EClass) result.getEClassifier("Item");
			EAttribute activeAttr = (EAttribute) itemClass.getEStructuralFeature("active");
			assertNotNull(activeAttr);
			assertEquals(EcorePackage.Literals.EBOOLEAN, activeAttr.getEType());
		}

		@Test
		@DisplayName("converts number property to EAttribute")
		void convertsNumberPropertyToEAttribute() {
			EObject schema = createSchema("object");
			addProperty(schema, "price", createSchema("number"));

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Product", schema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass productClass = (EClass) result.getEClassifier("Product");
			EAttribute priceAttr = (EAttribute) productClass.getEStructuralFeature("price");
			assertNotNull(priceAttr);
			assertEquals(EcorePackage.Literals.EDOUBLE, priceAttr.getEType());
		}

		@Test
		@DisplayName("marks required properties with lowerBound=1")
		void marksRequiredPropertiesWithLowerBound() {
			EObject personSchema = createSchema("object");
			addProperty(personSchema, "name", createSchema("string"));
			addProperty(personSchema, "email", createSchema("string"));
			setRequired(personSchema, "name");

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", personSchema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass personClass = (EClass) result.getEClassifier("Person");
			EStructuralFeature nameFeature = personClass.getEStructuralFeature("name");
			EStructuralFeature emailFeature = personClass.getEStructuralFeature("email");

			assertEquals(1, nameFeature.getLowerBound(), "Required field should have lowerBound=1");
			assertEquals(0, emailFeature.getLowerBound(), "Optional field should have lowerBound=0");
		}
	}

	// ========================================================================
	// Enum Conversion Tests
	// ========================================================================

	@Nested
	@DisplayName("Enum Conversion")
	class EnumConversion {

		@Test
		@DisplayName("converts string schema with enum to EEnum")
		void convertsStringSchemaWithEnumToEEnum() {
			EObject statusSchema = createSchema("string");
			setEnumValues(statusSchema, "ACTIVE", "INACTIVE", "PENDING");

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Status", statusSchema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EEnum statusEnum = (EEnum) result.getEClassifier("Status");
			assertNotNull(statusEnum);
			assertEquals(3, statusEnum.getELiterals().size());
			assertNotNull(statusEnum.getEEnumLiteral("ACTIVE"));
			assertNotNull(statusEnum.getEEnumLiteral("INACTIVE"));
			assertNotNull(statusEnum.getEEnumLiteral("PENDING"));
		}
	}

	// ========================================================================
	// Array Conversion Tests
	// ========================================================================

	@Nested
	@DisplayName("Array Conversion")
	class ArrayConversion {

		@Test
		@DisplayName("converts array property with items to multi-valued feature")
		void convertsArrayPropertyWithItemsToMultiValuedFeature() {
			EObject tagsSchema = createSchema("string");
			EObject itemsSchema = createSchema("array");
			itemsSchema.eSet(schemaClass.getEStructuralFeature("items"), tagsSchema);

			EObject containerSchema = createSchema("object");
			addProperty(containerSchema, "tags", itemsSchema);

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Container", containerSchema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass containerClass = (EClass) result.getEClassifier("Container");
			EStructuralFeature tagsFeature = containerClass.getEStructuralFeature("tags");
			assertNotNull(tagsFeature);
			assertEquals(-1, tagsFeature.getUpperBound(), "Array should be multi-valued");
		}
	}

	// ========================================================================
	// Multiple Schema Tests
	// ========================================================================

	@Nested
	@DisplayName("Multiple Schemas")
	class MultipleSchemas {

		@Test
		@DisplayName("converts multiple schemas to multiple EClassifiers")
		void convertsMultipleSchemasToMultipleEClassifiers() {
			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", createSchema("object"));
			schemaMap.put("Address", createSchema("object"));
			schemaMap.put("Status", createSchema("string"));

			// Set enum values for Status
			setEnumValues(schemaMap.get("Status"), "ACTIVE", "INACTIVE");

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			assertNotNull(result);
			assertTrue(result.getEClassifiers().size() >= 2, "Should have at least 2 classifiers");

			assertNotNull(result.getEClassifier("Person"), "Should have Person EClass");
			assertNotNull(result.getEClassifier("Address"), "Should have Address EClass");
		}

		@Test
		@DisplayName("skips null entries in schema map")
		void skipsNullEntriesInSchemaMap() {
			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", createSchema("object"));
			schemaMap.put("NullSchema", null);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			assertNotNull(result);
			assertEquals(1, result.getEClassifiers().size());
			assertNotNull(result.getEClassifier("Person"));
		}
	}

	// ========================================================================
	// Nested Object Tests
	// ========================================================================

	@Nested
	@DisplayName("Nested Objects")
	class NestedObjects {

		@Test
		@DisplayName("converts nested object property to EReference")
		void convertsNestedObjectPropertyToEReference() {
			EObject addressSchema = createSchema("object");
			addProperty(addressSchema, "street", createSchema("string"));
			addProperty(addressSchema, "city", createSchema("string"));

			EObject personSchema = createSchema("object");
			addProperty(personSchema, "name", createSchema("string"));
			addProperty(personSchema, "address", addressSchema);

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", personSchema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass personClass = (EClass) result.getEClassifier("Person");
			assertNotNull(personClass);

			EStructuralFeature addressFeature = personClass.getEStructuralFeature("address");
			assertNotNull(addressFeature);
			assertTrue(addressFeature instanceof EReference, "Nested object should be EReference");

			EReference addressRef = (EReference) addressFeature;
			assertTrue(addressRef.isContainment(), "Nested object should be containment");
		}
	}

	// ========================================================================
	// Null Type Tests
	// ========================================================================

	@Nested
	@DisplayName("Null Type Handling")
	class NullTypeHandling {

		@Test
		@DisplayName("treats schema without type as object")
		void treatsSchemaWithoutTypeAsObject() {
			EObject schema = createSchema(null); // No type specified
			addProperty(schema, "name", createSchema("string"));

			EMap<String, EObject> schemaMap = new BasicEMap<>();
			schemaMap.put("Person", schema);

			EPackage result = converter.convertFromSchemaMap(schemaMap);

			EClass personClass = (EClass) result.getEClassifier("Person");
			assertNotNull(personClass, "Schema without type should be treated as object -> EClass");
			assertNotNull(personClass.getEStructuralFeature("name"));
		}
	}
}
