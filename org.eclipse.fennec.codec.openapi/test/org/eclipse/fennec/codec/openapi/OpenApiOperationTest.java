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

import org.eclipse.fennec.codec.openapi.internal.OpenApiResourceFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.eclipse.fennec.model.openapi.HttpMethod;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Operation;
import org.eclipse.fennec.model.openapi.Parameter;
import org.eclipse.fennec.model.openapi.ParameterLocation;
import org.eclipse.fennec.model.openapi.PathItem;
import org.eclipse.fennec.model.openapi.RequestBody;
import org.eclipse.fennec.model.openapi.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for OpenAPI operations (paths, parameters, requestBody, responses).
 */
@DisplayName("OpenAPI Operations")
class OpenApiOperationTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("HTTP Methods")
	class HttpMethods {

		@Test
		@DisplayName("sets HttpMethod from feature name for all methods")
		void setsHttpMethodFromFeatureName() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Methods Test", "version": "1.0.0" },
					"paths": {
						"/resource": {
							"get": { "operationId": "getResource" },
							"post": { "operationId": "createResource" },
							"put": { "operationId": "updateResource" },
							"delete": { "operationId": "deleteResource" },
							"patch": { "operationId": "patchResource" },
							"options": { "operationId": "optionsResource" },
							"head": { "operationId": "headResource" }
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			PathItem pathItem = openApi.getPaths().get("/resource");

			assertEquals(HttpMethod.GET, pathItem.getGet().getMethod());
			assertEquals(HttpMethod.POST, pathItem.getPost().getMethod());
			assertEquals(HttpMethod.PUT, pathItem.getPut().getMethod());
			assertEquals(HttpMethod.DELETE, pathItem.getDelete().getMethod());
			assertEquals(HttpMethod.PATCH, pathItem.getPatch().getMethod());
			assertEquals(HttpMethod.OPTIONS, pathItem.getOptions().getMethod());
			assertEquals(HttpMethod.HEAD, pathItem.getHead().getMethod());
		}

		@Test
		@DisplayName("method field is not serialized")
		void methodFieldNotSerialized() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/test": {
							"get": { "operationId": "testGet" }
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			resource.save(out, null);
			String savedJson = out.toString(StandardCharsets.UTF_8);

			// method should NOT appear in serialized JSON
			assertFalse(savedJson.contains("\"method\""),
					"method field should not be serialized. JSON: " + savedJson);
		}
	}

	@Nested
	@DisplayName("Parameters")
	class Parameters {

		@Test
		@DisplayName("deserializes path and query parameters")
		void deserializesParameters() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Params Test", "version": "1.0.0" },
					"paths": {
						"/pets/{petId}": {
							"get": {
								"operationId": "getPet",
								"parameters": [
									{
										"name": "petId",
										"in": "path",
										"description": "ID of pet",
										"required": true
									},
									{
										"name": "includeDetails",
										"in": "query",
										"description": "Include extra details",
										"required": false
									},
									{
										"name": "X-Request-ID",
										"in": "header"
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
			Operation op = openApi.getPaths().get("/pets/{petId}").getGet();

			assertEquals(3, op.getParameters().size());

			Parameter petIdParam = op.getParameters().get(0);
			assertEquals("petId", petIdParam.getName());
			assertEquals(ParameterLocation.PATH, petIdParam.getIn());
			assertEquals("ID of pet", petIdParam.getDescription());
			assertTrue(petIdParam.isRequired());

			Parameter queryParam = op.getParameters().get(1);
			assertEquals("includeDetails", queryParam.getName());
			assertEquals(ParameterLocation.QUERY, queryParam.getIn());
			assertFalse(queryParam.isRequired());

			Parameter headerParam = op.getParameters().get(2);
			assertEquals("X-Request-ID", headerParam.getName());
			assertEquals(ParameterLocation.HEADER, headerParam.getIn());
		}

		@Test
		@DisplayName("deserializes parameters with schema")
		void deserializesParametersWithSchema() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Schema Test", "version": "1.0.0" },
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
			Operation op = openApi.getPaths().get("/items/{id}").getGet();

			assertEquals(1, op.getParameters().size());
			Parameter param = op.getParameters().get(0);
			assertEquals("id", param.getName());

			assertNotNull(param.getSchema(), "Schema should not be null");
			assertEquals("integer", param.getSchema().getType());
			assertEquals("int64", param.getSchema().getFormat());
		}

		@Test
		@DisplayName("round-trips parameters")
		void roundTripsParameters() throws IOException {
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
										"schema": { "type": "string" }
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

			Operation op = ((OpenAPI) resource2.getContents().get(0))
					.getPaths().get("/items/{id}").getGet();

			assertEquals(1, op.getParameters().size());
			Parameter param = op.getParameters().get(0);
			assertEquals("id", param.getName());
			assertEquals(ParameterLocation.PATH, param.getIn());
			assertTrue(param.isRequired());

			// Schema should be preserved through round-trip
			assertNotNull(param.getSchema(), "Schema should survive round-trip");
			assertEquals("string", param.getSchema().getType());
		}
	}

	@Nested
	@DisplayName("Request Body")
	class RequestBodyTests {

		@Test
		@DisplayName("deserializes requestBody with content types")
		void deserializesRequestBody() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "RequestBody Test", "version": "1.0.0" },
					"paths": {
						"/pets": {
							"post": {
								"operationId": "addPet",
								"requestBody": {
									"description": "Pet object to add",
									"required": true,
									"content": {
										"application/json": {},
										"application/xml": {}
									}
								},
								"responses": { "201": { "description": "Created" } }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Operation postOp = openApi.getPaths().get("/pets").getPost();

			assertEquals(HttpMethod.POST, postOp.getMethod());

			RequestBody requestBody = postOp.getRequestBody();
			assertNotNull(requestBody);
			assertEquals("Pet object to add", requestBody.getDescription());
			assertTrue(requestBody.isRequired());
			assertEquals(2, requestBody.getContent().size());

			assertNotNull(requestBody.getContent().get("application/json"));
			assertNotNull(requestBody.getContent().get("application/xml"));
		}
	}

	@Nested
	@DisplayName("Responses")
	class Responses {

		@Test
		@DisplayName("deserializes responses")
		void deserializesResponses() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Responses Test", "version": "1.0.0" },
					"paths": {
						"/pets/{id}": {
							"get": {
								"operationId": "getPet",
								"responses": {
									"200": {
										"description": "successful operation"
									},
									"400": {
										"description": "Invalid ID supplied"
									},
									"404": {
										"description": "Pet not found"
									},
									"default": {
										"description": "Unexpected error"
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
			Operation op = openApi.getPaths().get("/pets/{id}").getGet();

			assertEquals(4, op.getResponses().size());

			Response ok = op.getResponses().get("200");
			assertNotNull(ok);
			assertEquals("successful operation", ok.getDescription());

			Response badRequest = op.getResponses().get("400");
			assertNotNull(badRequest);
			assertEquals("Invalid ID supplied", badRequest.getDescription());

			Response notFound = op.getResponses().get("404");
			assertNotNull(notFound);
			assertEquals("Pet not found", notFound.getDescription());

			Response defaultResp = op.getResponses().get("default");
			assertNotNull(defaultResp);
			assertEquals("Unexpected error", defaultResp.getDescription());
		}

		@Test
		@DisplayName("round-trips responses")
		void roundTripsResponses() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/test": {
							"get": {
								"operationId": "test",
								"responses": {
									"200": {
										"description": "Success"
									},
									"500": { "description": "Error" }
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

			Operation op = ((OpenAPI) resource2.getContents().get(0))
					.getPaths().get("/test").getGet();

			assertEquals(2, op.getResponses().size());

			Response ok = op.getResponses().get("200");
			assertEquals("Success", ok.getDescription());

			Response error = op.getResponses().get("500");
			assertEquals("Error", error.getDescription());
		}
	}

	@Nested
	@DisplayName("Full Operation")
	class FullOperation {

		@Test
		@DisplayName("deserializes complete operation with all features")
		void deserializesCompleteOperation() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Full Op Test", "version": "1.0.0" },
					"paths": {
						"/pets/{petId}": {
							"put": {
								"operationId": "updatePet",
								"summary": "Update an existing pet",
								"description": "Update a pet by ID",
								"tags": ["pets", "updates"],
								"deprecated": true,
								"externalDocs": {
									"description": "Pet update docs",
									"url": "http://example.com/pet-update"
								},
								"parameters": [
									{
										"name": "petId",
										"in": "path",
										"required": true,
										"schema": { "type": "integer" }
									}
								],
								"requestBody": {
									"required": true,
									"content": {
										"application/json": {
											"schema": { "type": "object" }
										}
									}
								},
								"responses": {
									"200": { "description": "Updated" },
									"404": { "description": "Not found" }
								}
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Operation op = openApi.getPaths().get("/pets/{petId}").getPut();

			assertEquals(HttpMethod.PUT, op.getMethod());
			assertEquals("updatePet", op.getOperationId());
			assertEquals("Update an existing pet", op.getSummary());
			assertEquals("Update a pet by ID", op.getDescription());
			assertEquals(2, op.getTags().size());
			assertTrue(op.getTags().contains("pets"));
			assertTrue(op.getTags().contains("updates"));
			assertTrue(op.isDeprecated());

			assertNotNull(op.getExternalDocs());
			assertEquals("http://example.com/pet-update", op.getExternalDocs().getUrl());

			assertEquals(1, op.getParameters().size());
			assertNotNull(op.getRequestBody());
			assertEquals(2, op.getResponses().size());
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
