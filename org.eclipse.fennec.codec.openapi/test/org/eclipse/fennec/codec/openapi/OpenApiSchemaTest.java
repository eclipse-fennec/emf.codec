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
package org.eclipse.fennec.codec.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.Components;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Operation;
import org.eclipse.fennec.model.openapi.Parameter;
import org.eclipse.fennec.model.openapi.ParameterLocation;
import org.eclipse.fennec.model.openapi.PathItem;
import org.eclipse.fennec.model.openapi.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for OpenAPI Schema serialization/deserialization.
 */
@DisplayName("OpenAPI Schema")
class OpenApiSchemaTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("Parameter Schema")
	class ParameterSchema {

		@Test
		@DisplayName("deserializes schema inside operation parameter")
		void deserializesSchemaInOperationParameter() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/test": {
							"get": {
								"operationId": "test",
								"parameters": [
									{
										"name": "id",
										"in": "path",
										"schema": { "type": "string" }
									}
								],
								"responses": { "200": { "description": "OK" } }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Operation op = openApi.getPaths().get("/test").getGet();

			assertEquals(1, op.getParameters().size());
			Parameter param = op.getParameters().get(0);
			assertEquals("id", param.getName());
			assertEquals(ParameterLocation.PATH, param.getIn());

			assertNotNull(param.getSchema(), "Schema should not be null");
			assertEquals("string", param.getSchema().getType());
		}

		@Test
		@DisplayName("deserializes schema with type and format")
		void deserializesSchemaWithTypeAndFormat() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/items/{id}": {
							"get": {
								"operationId": "getItem",
								"parameters": [
									{
										"name": "id",
										"in": "path",
										"required": true,
										"schema": { "type": "integer", "format": "int64" }
									}
								],
								"responses": { "200": { "description": "OK" } }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Parameter param = openApi.getPaths().get("/items/{id}").getGet().getParameters().get(0);

			assertNotNull(param.getSchema());
			assertEquals("integer", param.getSchema().getType());
			assertEquals("int64", param.getSchema().getFormat());
		}

		@Test
		@DisplayName("deserializes schema in PathItem-level parameter")
		void deserializesSchemaInPathItemParameter() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/test": {
							"parameters": [
								{
									"name": "testParam",
									"in": "query",
									"schema": { "type": "boolean" }
								}
							]
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			PathItem pathItem = openApi.getPaths().get("/test");

			assertEquals(1, pathItem.getParameters().size());
			Parameter param = pathItem.getParameters().get(0);
			assertEquals("testParam", param.getName());
			assertEquals(ParameterLocation.QUERY, param.getIn());

			assertNotNull(param.getSchema(), "Schema should not be null");
			assertEquals("boolean", param.getSchema().getType());
		}
	}

	@Nested
	@DisplayName("Component Schemas")
	class ComponentSchemas {

		@Test
		@DisplayName("deserializes schemas in components")
		void deserializesSchemasInComponents() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Pet": {
								"type": "object",
								"properties": {
									"name": { "type": "string" },
									"age": { "type": "integer" }
								}
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Components components = openApi.getComponents();
			assertNotNull(components);

			assertEquals(1, components.getSchemas().size());
			Schema petSchema = components.getSchemas().get("Pet");

			assertNotNull(petSchema);
			assertEquals("object", petSchema.getType());
			assertEquals(2, petSchema.getProperties().size());

			Schema nameSchema = petSchema.getProperties().get("name");
			assertNotNull(nameSchema);
			assertEquals("string", nameSchema.getType());

			Schema ageSchema = petSchema.getProperties().get("age");
			assertNotNull(ageSchema);
			assertEquals("integer", ageSchema.getType());
		}

		@Test
		@DisplayName("deserializes schema with nested properties")
		void deserializesNestedProperties() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Address": {
								"type": "object",
								"properties": {
									"street": { "type": "string" },
									"city": { "type": "string" },
									"location": {
										"type": "object",
										"properties": {
											"lat": { "type": "number" },
											"lon": { "type": "number" }
										}
									}
								}
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Schema addressSchema = openApi.getComponents().getSchemas().get("Address");

			assertNotNull(addressSchema);
			assertEquals("object", addressSchema.getType());

			Schema locationSchema = addressSchema.getProperties().get("location");
			assertNotNull(locationSchema);
			assertEquals("object", locationSchema.getType());

			Schema latSchema = locationSchema.getProperties().get("lat");
			assertNotNull(latSchema);
			assertEquals("number", latSchema.getType());
		}
	}

	@Nested
	@DisplayName("Schema Round-trip")
	class SchemaRoundTrip {

		@Test
		@DisplayName("round-trips parameter schema")
		void roundTripsParameterSchema() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/test": {
							"get": {
								"operationId": "test",
								"parameters": [
									{
										"name": "id",
										"in": "path",
										"schema": { "type": "integer", "format": "int32" }
									}
								],
								"responses": { "200": { "description": "OK" } }
							}
						}
					}
				}
				""";

			// Load -> Save -> Reload
			OpenApiResourceImpl resource1 = createResource();
			resource1.load(toInputStream(json), loadOptions());

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			resource1.save(out, null);

			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(out.toString(StandardCharsets.UTF_8)), loadOptions());

			OpenAPI openApi = (OpenAPI) resource2.getContents().get(0);
			Parameter param = openApi.getPaths().get("/test").getGet().getParameters().get(0);

			assertNotNull(param.getSchema(), "Schema should survive round-trip");
			assertEquals("integer", param.getSchema().getType());
			assertEquals("int32", param.getSchema().getFormat());
		}

		@Test
		@DisplayName("round-trips component schemas")
		void roundTripsComponentSchemas() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Pet": {
								"type": "object",
								"properties": {
									"name": { "type": "string" },
									"tag": { "type": "string" }
								}
							}
						}
					}
				}
				""";

			// Load -> Save -> Reload
			OpenApiResourceImpl resource1 = createResource();
			resource1.load(toInputStream(json), loadOptions());

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			resource1.save(out, null);

			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(out.toString(StandardCharsets.UTF_8)), loadOptions());

			OpenAPI openApi = (OpenAPI) resource2.getContents().get(0);
			Schema petSchema = openApi.getComponents().getSchemas().get("Pet");

			assertNotNull(petSchema, "Component schema should survive round-trip");
			assertEquals("object", petSchema.getType());
			assertEquals(2, petSchema.getProperties().size());
			assertEquals("string", petSchema.getProperties().get("name").getType());
			assertEquals("string", petSchema.getProperties().get("tag").getType());
		}
	}

	private OpenApiResourceImpl createResource() {
		OpenApiResourceFactoryImpl factory = new OpenApiResourceFactoryImpl();
		return (OpenApiResourceImpl) factory.createResource(URI.createURI("test://openapi.json"));
	}

	private Map<String, Object> loadOptions() {
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		return options;
	}

	private ByteArrayInputStream toInputStream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}
}
