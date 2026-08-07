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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.PathItem;
import org.eclipse.fennec.model.openapi.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests that verify unsupported OpenAPI features are gracefully ignored.
 *
 * The OpenAPI model is intentionally simplified and does not support all OpenAPI 3.x features.
 * This test verifies that files with unsupported features still load without errors,
 * and the supported features are correctly parsed.
 *
 * Known model limitations:
 * - HTTP methods: only GET, POST, PUT, DELETE (not PATCH, OPTIONS, HEAD, TRACE)
 * - Server variables: only url and description (no variables)
 * - x-* extensions: not supported
 */
@DisplayName("OpenAPI Unsupported Features (Graceful Handling)")
class OpenApiUnsupportedFeaturesTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("Unsupported HTTP Methods")
	class UnsupportedHttpMethods {

		@Test
		@DisplayName("loads file with PATCH operation without errors")
		void loadsFileWithPatchOperation() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/items/{id}": {
							"get": {
								"operationId": "getItem",
								"responses": { "200": { "description": "OK" } }
							},
							"patch": {
								"operationId": "updateItem",
								"responses": { "200": { "description": "Updated" } }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			// Should load without errors
			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			PathItem pathItem = openApi.getPaths().get("/items/{id}");

			// GET should be loaded
			assertNotNull(pathItem.getGet(), "GET operation should be loaded");
			assertEquals("getItem", pathItem.getGet().getOperationId());

			// PATCH is not in the model, should be null/ignored
			// Note: The model doesn't have getPatch() method
		}

		@Test
		@DisplayName("loads file with OPTIONS operation without errors")
		void loadsFileWithOptionsOperation() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/items": {
							"get": {
								"operationId": "listItems",
								"responses": { "200": { "description": "OK" } }
							},
							"options": {
								"operationId": "optionsItems",
								"responses": { "200": { "description": "Options" } }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			assertNotNull(openApi.getPaths().get("/items").getGet());
		}

		@Test
		@DisplayName("loads file with HEAD operation without errors")
		void loadsFileWithHeadOperation() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/items/{id}": {
							"get": {
								"operationId": "getItem",
								"responses": { "200": { "description": "OK" } }
							},
							"head": {
								"operationId": "headItem",
								"responses": { "200": { "description": "OK" } }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			assertNotNull(openApi.getPaths().get("/items/{id}").getGet());
		}

		@Test
		@DisplayName("loads file with TRACE operation without errors")
		void loadsFileWithTraceOperation() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/debug": {
							"get": {
								"operationId": "debugGet",
								"responses": { "200": { "description": "OK" } }
							},
							"trace": {
								"operationId": "debugTrace",
								"responses": { "200": { "description": "Trace" } }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			assertNotNull(openApi.getPaths().get("/debug").getGet());
		}

		@Test
		@DisplayName("loads file with all HTTP methods, supported ones are parsed")
		void loadsFileWithAllHttpMethods() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"paths": {
						"/resource": {
							"get": { "operationId": "getResource", "responses": { "200": { "description": "OK" } } },
							"post": { "operationId": "createResource", "responses": { "201": { "description": "Created" } } },
							"put": { "operationId": "updateResource", "responses": { "200": { "description": "Updated" } } },
							"delete": { "operationId": "deleteResource", "responses": { "204": { "description": "Deleted" } } },
							"patch": { "operationId": "patchResource", "responses": { "200": { "description": "Patched" } } },
							"options": { "operationId": "optionsResource", "responses": { "200": { "description": "Options" } } },
							"head": { "operationId": "headResource", "responses": { "200": { "description": "Head" } } },
							"trace": { "operationId": "traceResource", "responses": { "200": { "description": "Trace" } } }
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			PathItem pathItem = openApi.getPaths().get("/resource");

			// Supported methods should be loaded
			assertNotNull(pathItem.getGet(), "GET should be loaded");
			assertNotNull(pathItem.getPost(), "POST should be loaded");
			assertNotNull(pathItem.getPut(), "PUT should be loaded");
			assertNotNull(pathItem.getDelete(), "DELETE should be loaded");

			assertEquals("getResource", pathItem.getGet().getOperationId());
			assertEquals("createResource", pathItem.getPost().getOperationId());
			assertEquals("updateResource", pathItem.getPut().getOperationId());
			assertEquals("deleteResource", pathItem.getDelete().getOperationId());
		}
	}

	@Nested
	@DisplayName("Unsupported Server Features")
	class UnsupportedServerFeatures {

		@Test
		@DisplayName("loads file with server variables without errors")
		void loadsFileWithServerVariables() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"servers": [
						{
							"url": "https://{environment}.example.com/v1",
							"description": "Multi-environment server",
							"variables": {
								"environment": {
									"default": "api",
									"enum": ["api", "api-staging", "api-dev"],
									"description": "Server environment"
								}
							}
						}
					],
					"paths": {}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			assertEquals(1, openApi.getServers().size());

			Server server = openApi.getServers().get(0);
			assertEquals("https://{environment}.example.com/v1", server.getUrl());
			assertEquals("Multi-environment server", server.getDescription());
			// Variables are not in the model, should be ignored
		}
	}

	@Nested
	@DisplayName("Unsupported Extensions (x-*)")
	class UnsupportedExtensions {

		@Test
		@DisplayName("loads file with x-* extensions without errors")
		void loadsFileWithExtensions() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": {
						"title": "Test",
						"version": "1.0.0",
						"x-custom-info": "custom value"
					},
					"x-custom-root": {
						"key": "value"
					},
					"paths": {
						"/items": {
							"x-custom-path": true,
							"get": {
								"operationId": "listItems",
								"x-custom-operation": ["one", "two"],
								"responses": { "200": { "description": "OK" } }
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			assertEquals("Test", openApi.getInfo().getTitle());
			assertNotNull(openApi.getPaths().get("/items").getGet());
		}

		@Test
		@DisplayName("loads file with x-tagGroups without errors")
		void loadsFileWithXTagGroups() throws IOException {
			// This is a common extension (e.g., used by Redoc)
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"tags": [
						{ "name": "users", "description": "User operations" },
						{ "name": "items", "description": "Item operations" }
					],
					"x-tagGroups": [
						{
							"name": "User Management",
							"tags": ["users"]
						},
						{
							"name": "Item Management",
							"tags": ["items"]
						}
					],
					"paths": {}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			assertEquals(2, openApi.getTags().size());
			assertEquals("users", openApi.getTags().get(0).getName());
		}
	}

	@Nested
	@DisplayName("Mixed Supported and Unsupported Content")
	class MixedContent {

		@Test
		@DisplayName("preserves supported content when mixed with unsupported")
		void preservesSupportedContentWithUnsupported() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": {
						"title": "Mixed API",
						"version": "2.0.0",
						"description": "API with mixed features",
						"x-logo": { "url": "https://example.com/logo.png" }
					},
					"servers": [
						{
							"url": "https://api.example.com",
							"description": "Production",
							"variables": { "version": { "default": "v1" } }
						}
					],
					"paths": {
						"/users": {
							"get": {
								"operationId": "listUsers",
								"tags": ["users"],
								"responses": { "200": { "description": "OK" } }
							},
							"patch": {
								"operationId": "patchUsers",
								"responses": { "200": { "description": "Patched" } }
							}
						}
					},
					"tags": [
						{ "name": "users", "description": "User operations", "x-display-name": "Users" }
					],
					"x-generators": ["typescript", "python"]
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertTrue(resource.getErrors().isEmpty(), "Should load without errors: " + resource.getErrors());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);

			// Verify supported features are preserved
			assertEquals("Mixed API", openApi.getInfo().getTitle());
			assertEquals("2.0.0", openApi.getInfo().getVersion());
			assertEquals("API with mixed features", openApi.getInfo().getDescription());

			assertEquals(1, openApi.getServers().size());
			assertEquals("https://api.example.com", openApi.getServers().get(0).getUrl());

			assertNotNull(openApi.getPaths().get("/users").getGet());
			assertEquals("listUsers", openApi.getPaths().get("/users").getGet().getOperationId());

			assertEquals(1, openApi.getTags().size());
			assertEquals("users", openApi.getTags().get(0).getName());
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
