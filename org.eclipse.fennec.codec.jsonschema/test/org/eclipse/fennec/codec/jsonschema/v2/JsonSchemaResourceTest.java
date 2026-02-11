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
package org.eclipse.fennec.codec.jsonschema.v2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for JsonSchemaResourceImpl - bidirectional JSON Schema ↔ EPackage conversion.
 */
@DisplayName("JsonSchema Resource Tests")
class JsonSchemaResourceTest {

    private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";

    /**
     * Loads JSON Schema and converts to EPackage.
     */
    private EPackage loadJsonSchema(String json, String schemaFeature) throws IOException {
        JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
                URI.createURI("test://schema.jsonschema"));

        Map<String, Object> options = new HashMap<>();
        if (schemaFeature != null) {
            options.put(JsonSchemaResourceImpl.OPTION_SCHEMA_FEATURE, schemaFeature);
        }

        try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            resource.load(is, options);
        }

        assertFalse(resource.getContents().isEmpty(), "Resource should have contents");
        assertInstanceOf(EPackage.class, resource.getContents().get(0));
        return (EPackage) resource.getContents().get(0);
    }

    /**
     * Saves EPackage as JSON Schema.
     */
    private String saveJsonSchema(EPackage ePackage, String schemaFeature) throws IOException {
        JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
                URI.createURI("test://schema.jsonschema"));

        resource.getContents().add(ePackage);

        Map<String, Object> options = new HashMap<>();
        if (schemaFeature != null) {
            options.put(JsonSchemaResourceImpl.OPTION_SCHEMA_FEATURE, schemaFeature);
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            resource.save(os, options);
            return os.toString(StandardCharsets.UTF_8);
        }
    }

    // ========================================================================
    // Deserialization Tests (JSON Schema → EPackage)
    // ========================================================================

    @Nested
    @DisplayName("Deserialization")
    class DeserializationTests {

        @Test
        @DisplayName("simple schema with one class")
        void simpleSchema() throws IOException {
            String json = """
                {
                    "$schema": "https://json-schema.org/draft/2020-12/schema",
                    "$id": "http://example.org/person",
                    "title": "PersonPackage",
                    "definitions": {
                        "Person": {
                            "type": "object",
                            "properties": {
                                "firstName": { "type": "string" },
                                "lastName": { "type": "string" },
                                "age": { "type": "integer" }
                            },
                            "required": ["firstName", "lastName"]
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            assertEquals("PersonPackage", ePackage.getName());
            assertEquals("http://example.org/person", ePackage.getNsURI());

            EClassifier personClassifier = ePackage.getEClassifier("Person");
            assertNotNull(personClassifier);
            assertInstanceOf(EClass.class, personClassifier);

            EClass person = (EClass) personClassifier;
            assertEquals(3, person.getEStructuralFeatures().size());

            EAttribute firstName = (EAttribute) person.getEStructuralFeature("firstName");
            assertNotNull(firstName);
            assertEquals(EcorePackage.Literals.ESTRING, firstName.getEAttributeType());
            assertEquals(1, firstName.getLowerBound()); // required

            EAttribute age = (EAttribute) person.getEStructuralFeature("age");
            assertNotNull(age);
            assertEquals(EcorePackage.Literals.EINT, age.getEAttributeType());
            assertEquals(0, age.getLowerBound()); // not required
        }

        @Test
        @DisplayName("schema with $defs (draft 2019-09)")
        void schemaWithDefs() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/address",
                    "title": "AddressPackage",
                    "$defs": {
                        "Address": {
                            "type": "object",
                            "properties": {
                                "street": { "type": "string" },
                                "city": { "type": "string" },
                                "zipCode": { "type": "string" }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "$defs");

            assertEquals("AddressPackage", ePackage.getName());

            EClass address = (EClass) ePackage.getEClassifier("Address");
            assertNotNull(address);
            assertEquals(3, address.getEStructuralFeatures().size());
        }

        @Test
        @DisplayName("schema with enum")
        void schemaWithEnum() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/status",
                    "title": "StatusPackage",
                    "definitions": {
                        "Status": {
                            "type": "string",
                            "enum": ["ACTIVE", "INACTIVE", "PENDING"]
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClassifier statusClassifier = ePackage.getEClassifier("Status");
            assertNotNull(statusClassifier);
            assertInstanceOf(EEnum.class, statusClassifier);

            EEnum status = (EEnum) statusClassifier;
            assertEquals(3, status.getELiterals().size());
            assertNotNull(status.getEEnumLiteral("ACTIVE"));
            assertNotNull(status.getEEnumLiteral("INACTIVE"));
            assertNotNull(status.getEEnumLiteral("PENDING"));
        }

        @Test
        @DisplayName("schema with nested object (containment)")
        void schemaWithNestedObject() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/order",
                    "title": "OrderPackage",
                    "definitions": {
                        "Order": {
                            "type": "object",
                            "properties": {
                                "id": { "type": "string" },
                                "customer": {
                                    "type": "object",
                                    "properties": {
                                        "name": { "type": "string" },
                                        "email": { "type": "string" }
                                    }
                                }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass order = (EClass) ePackage.getEClassifier("Order");
            assertNotNull(order);

            EReference customer = (EReference) order.getEStructuralFeature("customer");
            assertNotNull(customer);
            assertTrue(customer.isContainment());
            assertNotNull(customer.getEReferenceType());
        }

        @Test
        @DisplayName("schema with $ref")
        void schemaWithRef() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/company",
                    "title": "CompanyPackage",
                    "definitions": {
                        "Employee": {
                            "type": "object",
                            "properties": {
                                "name": { "type": "string" }
                            }
                        },
                        "Company": {
                            "type": "object",
                            "properties": {
                                "name": { "type": "string" },
                                "ceo": { "$ref": "#/definitions/Employee" }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass company = (EClass) ePackage.getEClassifier("Company");
            assertNotNull(company);

            EReference ceo = (EReference) company.getEStructuralFeature("ceo");
            assertNotNull(ceo);
            assertFalse(ceo.isContainment());

            EClass employee = (EClass) ePackage.getEClassifier("Employee");
            assertEquals(employee, ceo.getEReferenceType());
        }

        @Test
        @DisplayName("schema with allOf (inheritance)")
        void schemaWithAllOf() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/animal",
                    "title": "AnimalPackage",
                    "definitions": {
                        "Animal": {
                            "type": "object",
                            "properties": {
                                "name": { "type": "string" }
                            }
                        },
                        "Dog": {
                            "allOf": [
                                { "$ref": "#/definitions/Animal" },
                                {
                                    "type": "object",
                                    "properties": {
                                        "breed": { "type": "string" }
                                    }
                                }
                            ]
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass animal = (EClass) ePackage.getEClassifier("Animal");
            assertNotNull(animal);

            EClass dog = (EClass) ePackage.getEClassifier("Dog");
            assertNotNull(dog);

            assertTrue(dog.getESuperTypes().contains(animal));
            assertNotNull(dog.getEStructuralFeature("breed"));
        }

        @Test
        @DisplayName("schema with array property")
        void schemaWithArrayProperty() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/tags",
                    "title": "TagsPackage",
                    "definitions": {
                        "Item": {
                            "type": "object",
                            "properties": {
                                "name": { "type": "string" },
                                "tags": {
                                    "type": "array",
                                    "items": { "type": "string" }
                                }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass item = (EClass) ePackage.getEClassifier("Item");
            assertNotNull(item);

            EAttribute tags = (EAttribute) item.getEStructuralFeature("tags");
            assertNotNull(tags);
            assertTrue(tags.isMany());
            assertEquals(-1, tags.getUpperBound());
        }

        @Test
        @DisplayName("schema with different data types")
        void schemaWithDataTypes() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/datatypes",
                    "title": "DataTypesPackage",
                    "definitions": {
                        "AllTypes": {
                            "type": "object",
                            "properties": {
                                "stringVal": { "type": "string" },
                                "intVal": { "type": "integer" },
                                "numberVal": { "type": "number" },
                                "boolVal": { "type": "boolean" }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass allTypes = (EClass) ePackage.getEClassifier("AllTypes");
            assertNotNull(allTypes);

            assertEquals(EcorePackage.Literals.ESTRING,
                    ((EAttribute) allTypes.getEStructuralFeature("stringVal")).getEAttributeType());
            assertEquals(EcorePackage.Literals.EINT,
                    ((EAttribute) allTypes.getEStructuralFeature("intVal")).getEAttributeType());
            assertEquals(EcorePackage.Literals.EDOUBLE,
                    ((EAttribute) allTypes.getEStructuralFeature("numberVal")).getEAttributeType());
            assertEquals(EcorePackage.Literals.EBOOLEAN,
                    ((EAttribute) allTypes.getEStructuralFeature("boolVal")).getEAttributeType());
        }
    }

    // ========================================================================
    // Serialization Tests (EPackage → JSON Schema)
    // ========================================================================

    @Nested
    @DisplayName("Serialization")
    class SerializationTests {

        @Test
        @DisplayName("simple EClass to JSON Schema")
        void serializeSimpleEClass() throws IOException {
            EPackage ePackage = createTestPackage("TestPackage", "http://test.org/simple");

            EClass person = EcoreFactory.eINSTANCE.createEClass();
            person.setName("Person");

            EAttribute firstName = EcoreFactory.eINSTANCE.createEAttribute();
            firstName.setName("firstName");
            firstName.setEType(EcorePackage.Literals.ESTRING);
            person.getEStructuralFeatures().add(firstName);

            EAttribute age = EcoreFactory.eINSTANCE.createEAttribute();
            age.setName("age");
            age.setEType(EcorePackage.Literals.EINT);
            person.getEStructuralFeatures().add(age);

            ePackage.getEClassifiers().add(person);

            String json = saveJsonSchema(ePackage, "definitions");

            assertNotNull(json);
            assertTrue(json.contains("\"$id\""));
            assertTrue(json.contains("http://test.org/simple"));
            assertTrue(json.contains("\"definitions\""));
            assertTrue(json.contains("\"Person\""));
            assertTrue(json.contains("\"firstName\""));
            assertTrue(json.contains("\"string\""));
            assertTrue(json.contains("\"age\""));
            assertTrue(json.contains("\"integer\""));
        }

        @Test
        @DisplayName("EEnum to JSON Schema")
        void serializeEEnum() throws IOException {
            EPackage ePackage = createTestPackage("StatusPackage", "http://test.org/status");

            EEnum status = EcoreFactory.eINSTANCE.createEEnum();
            status.setName("Status");
            addEnumLiteral(status, "ACTIVE", 0);
            addEnumLiteral(status, "INACTIVE", 1);
            addEnumLiteral(status, "PENDING", 2);

            ePackage.getEClassifiers().add(status);

            String json = saveJsonSchema(ePackage, "definitions");

            assertTrue(json.contains("\"Status\""));
            assertTrue(json.contains("\"enum\""));
            assertTrue(json.contains("\"ACTIVE\""));
            assertTrue(json.contains("\"INACTIVE\""));
            assertTrue(json.contains("\"PENDING\""));
            assertTrue(json.contains("\"type\"") && json.contains("\"string\""));
        }

        @Test
        @DisplayName("EClass with inheritance to JSON Schema (allOf)")
        void serializeWithInheritance() throws IOException {
            EPackage ePackage = createTestPackage("AnimalPackage", "http://test.org/animal");

            EClass animal = EcoreFactory.eINSTANCE.createEClass();
            animal.setName("Animal");
            EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
            name.setName("name");
            name.setEType(EcorePackage.Literals.ESTRING);
            animal.getEStructuralFeatures().add(name);
            ePackage.getEClassifiers().add(animal);

            EClass dog = EcoreFactory.eINSTANCE.createEClass();
            dog.setName("Dog");
            dog.getESuperTypes().add(animal);
            EAttribute breed = EcoreFactory.eINSTANCE.createEAttribute();
            breed.setName("breed");
            breed.setEType(EcorePackage.Literals.ESTRING);
            dog.getEStructuralFeatures().add(breed);
            ePackage.getEClassifiers().add(dog);

            String json = saveJsonSchema(ePackage, "definitions");

            assertTrue(json.contains("\"allOf\""));
            assertTrue(json.contains("\"$ref\""));
            assertTrue(json.contains("#/definitions/Animal"));
        }

        @Test
        @DisplayName("EClass with containment reference")
        void serializeWithContainment() throws IOException {
            EPackage ePackage = createTestPackage("OrderPackage", "http://test.org/order");

            EClass address = EcoreFactory.eINSTANCE.createEClass();
            address.setName("Address");
            EAttribute street = EcoreFactory.eINSTANCE.createEAttribute();
            street.setName("street");
            street.setEType(EcorePackage.Literals.ESTRING);
            address.getEStructuralFeatures().add(street);
            ePackage.getEClassifiers().add(address);

            EClass order = EcoreFactory.eINSTANCE.createEClass();
            order.setName("Order");
            EReference shippingAddress = EcoreFactory.eINSTANCE.createEReference();
            shippingAddress.setName("shippingAddress");
            shippingAddress.setEType(address);
            shippingAddress.setContainment(true);
            order.getEStructuralFeatures().add(shippingAddress);
            ePackage.getEClassifiers().add(order);

            String json = saveJsonSchema(ePackage, "definitions");

            assertTrue(json.contains("\"Order\""));
            assertTrue(json.contains("\"shippingAddress\""));
            assertTrue(json.contains("\"type\"") && json.contains("\"object\""));
        }

        @Test
        @DisplayName("EClass with many-valued attribute")
        void serializeWithManyValuedAttribute() throws IOException {
            EPackage ePackage = createTestPackage("TagsPackage", "http://test.org/tags");

            EClass item = EcoreFactory.eINSTANCE.createEClass();
            item.setName("Item");

            EAttribute tags = EcoreFactory.eINSTANCE.createEAttribute();
            tags.setName("tags");
            tags.setEType(EcorePackage.Literals.ESTRING);
            tags.setUpperBound(-1); // many
            item.getEStructuralFeatures().add(tags);

            ePackage.getEClassifiers().add(item);

            String json = saveJsonSchema(ePackage, "definitions");

            assertTrue(json.contains("\"tags\""));
            assertTrue(json.contains("\"type\"") && json.contains("\"array\""));
        }

        private EPackage createTestPackage(String name, String nsURI) {
            EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
            ePackage.setName(name);
            ePackage.setNsURI(nsURI);
            return ePackage;
        }

        private void addEnumLiteral(EEnum eEnum, String name, int value) {
            var literal = EcoreFactory.eINSTANCE.createEEnumLiteral();
            literal.setName(name);
            literal.setLiteral(name);
            literal.setValue(value);
            eEnum.getELiterals().add(literal);
        }
    }

    // ========================================================================
    // Enhanced Features Tests
    // ========================================================================

    @Nested
    @DisplayName("Enhanced Features")
    class EnhancedFeaturesTests {

        @Test
        @DisplayName("discriminated union with minProperties/maxProperties")
        void discriminatedUnion() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/config",
                    "title": "ConfigPackage",
                    "$defs": {
                        "configs": {
                            "kafka": {
                                "type": "object",
                                "properties": {
                                    "brokers": { "type": "string" },
                                    "topic": { "type": "string" }
                                },
                                "required": ["brokers"]
                            },
                            "file": {
                                "type": "object",
                                "properties": {
                                    "path": { "type": "string" },
                                    "format": { "type": "string" }
                                },
                                "required": ["path"]
                            }
                        },
                        "inputNode": {
                            "description": "Select one input type",
                            "minProperties": 1,
                            "maxProperties": 1,
                            "oneOf": [
                                {
                                    "required": ["kafka"],
                                    "properties": {
                                        "kafka": { "$ref": "#/$defs/configs/kafka" }
                                    }
                                },
                                {
                                    "required": ["file"],
                                    "properties": {
                                        "file": { "$ref": "#/$defs/configs/file" }
                                    }
                                }
                            ]
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "$defs");

            // Check that InputNode is created as abstract base
            EClass inputNode = (EClass) ePackage.getEClassifier("InputNode");
            assertNotNull(inputNode, "InputNode should be created");
            assertTrue(inputNode.isAbstract(), "InputNode should be abstract");

            // Check discriminatedUnion annotation
            EAnnotation jsAnnotation = inputNode.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("true", jsAnnotation.getDetails().get("discriminatedUnion"));

            // Check codec.type annotation for feature-based discrimination
            EAnnotation codecTypeAnnotation = inputNode.getEAnnotation("codec.type");
            assertNotNull(codecTypeAnnotation, "Should have codec.type annotation");
            assertEquals("*", codecTypeAnnotation.getDetails().get("typeKey"));
        }

        @Test
        @DisplayName("namespace path support (configs/kafka)")
        void namespacePathSupport() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/namespaced",
                    "title": "NamespacedPackage",
                    "$defs": {
                        "configs": {
                            "database": {
                                "type": "object",
                                "properties": {
                                    "connectionString": { "type": "string" }
                                }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "$defs");

            // The class should be created with namespace path annotation
            EClass database = (EClass) ePackage.getEClassifier("Database");
            assertNotNull(database, "Database should be created from configs/database");

            EAnnotation jsAnnotation = database.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("configs", jsAnnotation.getDetails().get("namespacePath"));
        }

        @Test
        @DisplayName("top-level properties create rootClass")
        void topLevelPropertiesRootClass() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/root",
                    "title": "RootSchema",
                    "type": "object",
                    "properties": {
                        "version": { "type": "string" },
                        "enabled": { "type": "boolean" }
                    },
                    "required": ["version"],
                    "$defs": {
                        "Item": {
                            "type": "object",
                            "properties": {
                                "name": { "type": "string" }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "$defs");

            // Check for rootClass
            EClass rootClass = (EClass) ePackage.getEClassifier("RootSchema");
            assertNotNull(rootClass, "RootClass should be created from top-level properties");

            EAnnotation jsAnnotation = rootClass.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("true", jsAnnotation.getDetails().get("rootClass"));

            // Check properties
            EAttribute version = (EAttribute) rootClass.getEStructuralFeature("version");
            assertNotNull(version);
            assertEquals(1, version.getLowerBound()); // required

            EAttribute enabled = (EAttribute) rootClass.getEStructuralFeature("enabled");
            assertNotNull(enabled);
            assertEquals(0, enabled.getLowerBound()); // not required
        }

        @Test
        @DisplayName("context-specific variants (oneOf without discriminated union)")
        void contextSpecificVariants() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/variants",
                    "title": "VariantsPackage",
                    "definitions": {
                        "Response": {
                            "oneOf": [
                                {
                                    "title": "Success",
                                    "type": "object",
                                    "properties": {
                                        "data": { "type": "string" },
                                        "timestamp": { "type": "string" }
                                    }
                                },
                                {
                                    "title": "Error",
                                    "type": "object",
                                    "properties": {
                                        "error": { "type": "string" },
                                        "code": { "type": "integer" }
                                    }
                                }
                            ]
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            // Should create a base class (ResponseBase) and variant classes
            EClass responseBase = (EClass) ePackage.getEClassifier("ResponseBase");
            assertNotNull(responseBase, "ResponseBase should be created");
            assertTrue(responseBase.isAbstract());

            // Check commonBase annotation
            EAnnotation jsAnnotation = responseBase.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("true", jsAnnotation.getDetails().get("commonBase"));

            // Check variant classes exist
            EClass successVariant = (EClass) ePackage.getEClassifier("ResponseSuccess");
            assertNotNull(successVariant, "ResponseSuccess variant should be created");
            assertTrue(successVariant.getESuperTypes().contains(responseBase));

            EClass errorVariant = (EClass) ePackage.getEClassifier("ResponseError");
            assertNotNull(errorVariant, "ResponseError variant should be created");
            assertTrue(errorVariant.getESuperTypes().contains(responseBase));
        }

        @Test
        @DisplayName("multi-type property (type: [string, integer])")
        void multiTypeProperty() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/multitype",
                    "title": "MultiTypePackage",
                    "definitions": {
                        "FlexibleValue": {
                            "type": "object",
                            "properties": {
                                "value": {
                                    "type": ["string", "integer"],
                                    "description": "Can be string or integer"
                                }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass flexibleValue = (EClass) ePackage.getEClassifier("FlexibleValue");
            assertNotNull(flexibleValue);

            // The 'value' property should be an EReference to an artificial union class
            EReference value = (EReference) flexibleValue.getEStructuralFeature("value");
            assertNotNull(value, "value property should exist");
            assertTrue(value.isContainment());

            // The type should be an artificial class with multiType annotation
            EClass valueType = (EClass) value.getEReferenceType();
            assertNotNull(valueType);

            EAnnotation jsAnnotation = valueType.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("true", jsAnnotation.getDetails().get("multiType"));
            assertTrue(jsAnnotation.getDetails().get("typeArray").contains("string"));
            assertTrue(jsAnnotation.getDetails().get("typeArray").contains("integer"));
        }

        @Test
        @DisplayName("schema properties preserved as annotations")
        void schemaPropertiesPreserved() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/constraints",
                    "title": "ConstraintsPackage",
                    "definitions": {
                        "ConstrainedType": {
                            "type": "object",
                            "properties": {
                                "name": {
                                    "type": "string",
                                    "minLength": 1,
                                    "maxLength": 100,
                                    "pattern": "^[A-Z].*"
                                },
                                "count": {
                                    "type": "integer",
                                    "minimum": 0,
                                    "maximum": 1000
                                },
                                "tags": {
                                    "type": "array",
                                    "items": { "type": "string" },
                                    "minItems": 1,
                                    "maxItems": 10,
                                    "uniqueItems": true
                                }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            EClass constrainedType = (EClass) ePackage.getEClassifier("ConstrainedType");
            assertNotNull(constrainedType);

            // Check name constraints preserved
            EAttribute name = (EAttribute) constrainedType.getEStructuralFeature("name");
            EAnnotation nameAnnotation = name.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(nameAnnotation);
            assertEquals("1", nameAnnotation.getDetails().get("minLength"));
            assertEquals("100", nameAnnotation.getDetails().get("maxLength"));
            assertEquals("\"^[A-Z].*\"", nameAnnotation.getDetails().get("pattern"));

            // Check count constraints preserved
            EAttribute count = (EAttribute) constrainedType.getEStructuralFeature("count");
            EAnnotation countAnnotation = count.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(countAnnotation);
            assertEquals("0", countAnnotation.getDetails().get("minimum"));
            assertEquals("1000", countAnnotation.getDetails().get("maximum"));

            // Check tags constraints - minItems/maxItems affect bounds
            EAttribute tags = (EAttribute) constrainedType.getEStructuralFeature("tags");
            assertEquals(1, tags.getLowerBound()); // minItems: 1
            assertEquals(10, tags.getUpperBound()); // maxItems: 10
        }

        @Test
        @DisplayName("originalName preserved via ExtendedMetaData")
        void originalNamePreserved() throws IOException {
            String json = """
                {
                    "$id": "http://example.org/casing",
                    "title": "CasingPackage",
                    "definitions": {
                        "myEntity": {
                            "type": "object",
                            "properties": {
                                "firstName": { "type": "string" }
                            }
                        }
                    }
                }
                """;

            EPackage ePackage = loadJsonSchema(json, "definitions");

            // Class name should be capitalized
            EClass myEntity = (EClass) ePackage.getEClassifier("MyEntity");
            assertNotNull(myEntity, "Class should be capitalized to MyEntity");

            // Original name should be preserved in annotation
            EAnnotation jsAnnotation = myEntity.getEAnnotation(JSONSCHEMA_ANNOTATION_SOURCE);
            assertNotNull(jsAnnotation);
            assertEquals("myEntity", jsAnnotation.getDetails().get("originalName"));

            // ExtendedMetaData should also have the name
            EAnnotation extAnnotation = myEntity.getEAnnotation("http:///org/eclipse/emf/ecore/util/ExtendedMetaData");
            assertNotNull(extAnnotation);
            assertEquals("myEntity", extAnnotation.getDetails().get("name"));
        }
    }

    // ========================================================================
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip")
    class RoundTripTests {

        @Test
        @DisplayName("simple class round-trip")
        void simpleClassRoundTrip() throws IOException {
            String originalJson = """
                {
                    "$id": "http://example.org/test",
                    "title": "TestPackage",
                    "definitions": {
                        "Person": {
                            "type": "object",
                            "properties": {
                                "name": { "type": "string" },
                                "age": { "type": "integer" }
                            }
                        }
                    }
                }
                """;

            // Load
            EPackage ePackage = loadJsonSchema(originalJson, "definitions");

            // Verify loaded
            assertEquals("TestPackage", ePackage.getName());
            EClass person = (EClass) ePackage.getEClassifier("Person");
            assertNotNull(person);
            assertEquals(2, person.getEStructuralFeatures().size());

            // Save
            String savedJson = saveJsonSchema(ePackage, "definitions");

            // Reload
            EPackage reloadedPackage = loadJsonSchema(savedJson, "definitions");

            // Verify reloaded matches original
            assertEquals(ePackage.getName(), reloadedPackage.getName());
            EClass reloadedPerson = (EClass) reloadedPackage.getEClassifier("Person");
            assertNotNull(reloadedPerson);
            assertEquals(person.getEStructuralFeatures().size(),
                    reloadedPerson.getEStructuralFeatures().size());
        }

        @Test
        @DisplayName("enum round-trip")
        void enumRoundTrip() throws IOException {
            String originalJson = """
                {
                    "$id": "http://example.org/status",
                    "title": "StatusPackage",
                    "definitions": {
                        "Status": {
                            "type": "string",
                            "enum": ["ACTIVE", "INACTIVE", "PENDING"]
                        }
                    }
                }
                """;

            // Load
            EPackage ePackage = loadJsonSchema(originalJson, "definitions");
            EEnum status = (EEnum) ePackage.getEClassifier("Status");
            assertNotNull(status);
            assertEquals(3, status.getELiterals().size());

            // Save and reload
            String savedJson = saveJsonSchema(ePackage, "definitions");
            EPackage reloadedPackage = loadJsonSchema(savedJson, "definitions");

            EEnum reloadedStatus = (EEnum) reloadedPackage.getEClassifier("Status");
            assertNotNull(reloadedStatus);
            assertEquals(status.getELiterals().size(), reloadedStatus.getELiterals().size());
        }

        @Test
        @DisplayName("inheritance round-trip")
        void inheritanceRoundTrip() throws IOException {
            String originalJson = """
                {
                    "$id": "http://example.org/animal",
                    "title": "AnimalPackage",
                    "definitions": {
                        "Animal": {
                            "type": "object",
                            "properties": {
                                "name": { "type": "string" }
                            }
                        },
                        "Dog": {
                            "allOf": [
                                { "$ref": "#/definitions/Animal" },
                                {
                                    "type": "object",
                                    "properties": {
                                        "breed": { "type": "string" }
                                    }
                                }
                            ]
                        }
                    }
                }
                """;

            // Load
            EPackage ePackage = loadJsonSchema(originalJson, "definitions");
            EClass dog = (EClass) ePackage.getEClassifier("Dog");
            assertNotNull(dog);
            assertFalse(dog.getESuperTypes().isEmpty());

            // Save and reload
            String savedJson = saveJsonSchema(ePackage, "definitions");
            EPackage reloadedPackage = loadJsonSchema(savedJson, "definitions");

            EClass reloadedDog = (EClass) reloadedPackage.getEClassifier("Dog");
            assertNotNull(reloadedDog);
            assertFalse(reloadedDog.getESuperTypes().isEmpty());
        }
    }
}
