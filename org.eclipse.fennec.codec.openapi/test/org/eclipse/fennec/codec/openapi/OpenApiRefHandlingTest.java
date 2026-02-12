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
package org.eclipse.fennec.codec.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.eclipse.fennec.model.openapi.Header;
import org.eclipse.fennec.model.openapi.MediaType;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Parameter;
import org.eclipse.fennec.model.openapi.PathItem;
import org.eclipse.fennec.model.openapi.RequestBody;
import org.eclipse.fennec.model.openapi.Response;
import org.eclipse.fennec.model.openapi.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for $ref handling in OpenAPI objects.
 *
 * The OpenAPI model supports $ref on multiple classes:
 * - Schema
 * - PathItem
 * - Parameter
 * - Header
 * - Response
 * - RequestBody
 * - Example
 * - Callback
 * - Link
 */
@DisplayName("OpenAPI $ref Handling")
class OpenApiRefHandlingTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("Schema $ref")
	class SchemaRef {

		@Test
		@DisplayName("deserializes $ref in schema")
		void deserializesSchemaRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/pets": {
							"get": {
								"operationId": "listPets",
								"responses": {
									"200": {
										"description": "List of pets",
										"content": {
											"application/json": {
												"schema": {
													"$ref": "#/components/schemas/Pet"
												}
											}
										}
									}
								}
							}
						}
					},
					"components": {
						"schemas": {
							"Pet": {
								"type": "object",
								"properties": {
									"name": { "type": "string" }
								}
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Response response = openApi.getPaths().get("/pets").getGet().getResponses().get("200");
			MediaType mediaType = response.getContent().get("application/json");
			Schema schema = mediaType.getSchema();

			assertNotNull(schema, "Schema should not be null");
			assertEquals("#/components/schemas/Pet", schema.getRef());
			// When $ref is set, other properties should be null/empty
			assertNull(schema.getType(), "Type should be null when $ref is set");
		}

		@Test
		@DisplayName("deserializes $ref in array items")
		void deserializesRefInArrayItems() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/pets": {
							"get": {
								"operationId": "listPets",
								"responses": {
									"200": {
										"description": "List of pets",
										"content": {
											"application/json": {
												"schema": {
													"type": "array",
													"items": {
														"$ref": "#/components/schemas/Pet"
													}
												}
											}
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
			Response response = openApi.getPaths().get("/pets").getGet().getResponses().get("200");
			Schema schema = response.getContent().get("application/json").getSchema();

			assertEquals("array", schema.getType());
			assertNotNull(schema.getItems(), "Items should not be null");
			assertEquals("#/components/schemas/Pet", schema.getItems().getRef());
		}

		@Test
		@DisplayName("round-trips $ref in schema")
		void roundTripsSchemaRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/pets": {
							"get": {
								"operationId": "listPets",
								"responses": {
									"200": {
										"description": "OK",
										"content": {
											"application/json": {
												"schema": {
													"$ref": "#/components/schemas/Pet"
												}
											}
										}
									}
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
			String savedJson = out.toString(StandardCharsets.UTF_8);

			// Verify $ref is in the output
			assertTrue(savedJson.contains("\"$ref\""), "Saved JSON should contain $ref");
			assertTrue(savedJson.contains("#/components/schemas/Pet"), "Saved JSON should contain ref value");

			// Reload
			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(savedJson), loadOptions());

			OpenAPI openApi = (OpenAPI) resource2.getContents().get(0);
			Schema schema = openApi.getPaths().get("/pets").getGet().getResponses().get("200")
					.getContent().get("application/json").getSchema();

			assertEquals("#/components/schemas/Pet", schema.getRef());
		}
	}

	@Nested
	@DisplayName("Parameter $ref")
	class ParameterRef {

		@Test
		@DisplayName("deserializes $ref in parameter")
		void deserializesParameterRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/items/{id}": {
							"get": {
								"operationId": "getItem",
								"parameters": [
									{ "$ref": "#/components/parameters/ItemId" }
								],
								"responses": { "200": { "description": "OK" } }
							}
						}
					},
					"components": {
						"parameters": {
							"ItemId": {
								"name": "id",
								"in": "path",
								"required": true,
								"schema": { "type": "string" }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Parameter param = openApi.getPaths().get("/items/{id}").getGet().getParameters().get(0);

			assertNotNull(param, "Parameter should not be null");
			assertEquals("#/components/parameters/ItemId", param.getRef());
		}

		@Test
		@DisplayName("round-trips $ref in parameter")
		void roundTripsParameterRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/items/{id}": {
							"get": {
								"operationId": "getItem",
								"parameters": [
									{ "$ref": "#/components/parameters/ItemId" }
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
			Parameter param = openApi.getPaths().get("/items/{id}").getGet().getParameters().get(0);

			assertEquals("#/components/parameters/ItemId", param.getRef());
		}
	}

	@Nested
	@DisplayName("Response $ref")
	class ResponseRef {

		@Test
		@DisplayName("deserializes $ref in response")
		void deserializesResponseRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/pets": {
							"get": {
								"operationId": "listPets",
								"responses": {
									"404": { "$ref": "#/components/responses/NotFound" }
								}
							}
						}
					},
					"components": {
						"responses": {
							"NotFound": {
								"description": "Resource not found"
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Response response = openApi.getPaths().get("/pets").getGet().getResponses().get("404");

			assertNotNull(response, "Response should not be null");
			assertEquals("#/components/responses/NotFound", response.getRef());
		}
	}

	@Nested
	@DisplayName("RequestBody $ref")
	class RequestBodyRef {

		@Test
		@DisplayName("deserializes $ref in requestBody")
		void deserializesRequestBodyRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/pets": {
							"post": {
								"operationId": "createPet",
								"requestBody": { "$ref": "#/components/requestBodies/PetBody" },
								"responses": { "201": { "description": "Created" } }
							}
						}
					},
					"components": {
						"requestBodies": {
							"PetBody": {
								"description": "Pet to add",
								"content": {
									"application/json": {
										"schema": { "type": "object" }
									}
								}
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			RequestBody requestBody = openApi.getPaths().get("/pets").getPost().getRequestBody();

			assertNotNull(requestBody, "RequestBody should not be null");
			assertEquals("#/components/requestBodies/PetBody", requestBody.getRef());
		}
	}

	@Nested
	@DisplayName("Header $ref")
	class HeaderRef {

		@Test
		@DisplayName("deserializes $ref in header")
		void deserializesHeaderRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/pets": {
							"get": {
								"operationId": "listPets",
								"responses": {
									"200": {
										"description": "OK",
										"headers": {
											"X-Rate-Limit": { "$ref": "#/components/headers/RateLimit" }
										}
									}
								}
							}
						}
					},
					"components": {
						"headers": {
							"RateLimit": {
								"description": "Rate limit",
								"schema": { "type": "integer" }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Response response = openApi.getPaths().get("/pets").getGet().getResponses().get("200");
			Header header = response.getHeaders().get("X-Rate-Limit");

			assertNotNull(header, "Header should not be null");
			assertEquals("#/components/headers/RateLimit", header.getRef());
		}
	}

	@Nested
	@DisplayName("PathItem $ref")
	class PathItemRef {

		@Test
		@DisplayName("deserializes $ref in pathItem")
		void deserializesPathItemRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/pets": { "$ref": "common.json#/paths/pets" }
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			PathItem pathItem = openApi.getPaths().get("/pets");

			assertNotNull(pathItem, "PathItem should not be null");
			assertEquals("common.json#/paths/pets", pathItem.getRef());
		}
	}

	@Nested
	@DisplayName("allOf/oneOf/anyOf with $ref")
	class CompositionWithRef {

		@Test
		@DisplayName("deserializes allOf with $ref schemas")
		void deserializesAllOfWithRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Dog": {
								"allOf": [
									{ "$ref": "#/components/schemas/Pet" },
									{
										"type": "object",
										"properties": {
											"bark": { "type": "boolean" }
										}
									}
								]
							},
							"Pet": {
								"type": "object",
								"properties": {
									"name": { "type": "string" }
								}
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Schema dogSchema = openApi.getComponents().getSchemas().get("Dog");

			assertNotNull(dogSchema, "Dog schema should not be null");
			assertEquals(2, dogSchema.getAllOf().size());
			assertEquals("#/components/schemas/Pet", dogSchema.getAllOf().get(0).getRef());
			assertEquals("object", dogSchema.getAllOf().get(1).getType());
		}

		@Test
		@DisplayName("deserializes oneOf with $ref schemas")
		void deserializesOneOfWithRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Pet": {
								"oneOf": [
									{ "$ref": "#/components/schemas/Cat" },
									{ "$ref": "#/components/schemas/Dog" }
								]
							},
							"Cat": { "type": "object" },
							"Dog": { "type": "object" }
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Schema petSchema = openApi.getComponents().getSchemas().get("Pet");

			assertNotNull(petSchema, "Pet schema should not be null");
			assertEquals(2, petSchema.getOneOf().size());
			assertEquals("#/components/schemas/Cat", petSchema.getOneOf().get(0).getRef());
			assertEquals("#/components/schemas/Dog", petSchema.getOneOf().get(1).getRef());
		}

		@Test
		@DisplayName("round-trips allOf with $ref")
		void roundTripsAllOfWithRef() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Dog": {
								"allOf": [
									{ "$ref": "#/components/schemas/Pet" },
									{ "type": "object" }
								]
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
			Schema dogSchema = openApi.getComponents().getSchemas().get("Dog");

			assertEquals(2, dogSchema.getAllOf().size());
			assertEquals("#/components/schemas/Pet", dogSchema.getAllOf().get(0).getRef());
		}
	}

	// ========================================================================
	// Helper Methods
	// ========================================================================

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
