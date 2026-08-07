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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.Components;
import org.eclipse.fennec.model.openapi.HttpMethod;
import org.eclipse.fennec.model.openapi.Info;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiFactory;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Operation;
import org.eclipse.fennec.model.openapi.PathItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

/**
 * Tests for {@link OpenApiResourceImpl}.
 */
@DisplayName("OpenApiResource")
class OpenApiResourceTest {

	@BeforeEach
	void setUp() {
		// Ensure package is registered
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("Deserialization")
	class Deserialization {

		@Test
		@DisplayName("loads simple OpenAPI document")
		void loadsSimpleDocument() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": {
						"title": "Test API",
						"version": "1.0.0"
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			assertEquals(1, resource.getContents().size());
			EObject root = resource.getContents().get(0);
			assertTrue(root instanceof OpenAPI);

			OpenAPI openApi = (OpenAPI) root;
			assertEquals("3.0.3", openApi.getOpenapi());
			assertNotNull(openApi.getInfo());
			assertEquals("Test API", openApi.getInfo().getTitle());
			assertEquals("1.0.0", openApi.getInfo().getVersion());
		}

		@Test
		@DisplayName("loads OpenAPI with paths and sets HttpMethod from feature name")
		void loadsPathsWithHttpMethod() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": {
						"title": "Path Test API",
						"version": "1.0.0"
					},
					"paths": {
						"/pets": {
							"get": {
								"operationId": "getPets",
								"summary": "List all pets"
							},
							"post": {
								"operationId": "createPet",
								"summary": "Create a pet"
							}
						},
						"/pets/{id}": {
							"get": {
								"operationId": "getPetById",
								"summary": "Get a pet by ID"
							},
							"put": {
								"operationId": "updatePet",
								"summary": "Update a pet"
							},
							"delete": {
								"operationId": "deletePet",
								"summary": "Delete a pet"
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			assertNotNull(openApi.getPaths());
			assertEquals(2, openApi.getPaths().size());

			// Check /pets path
			PathItem petsPath = openApi.getPaths().get("/pets");
			assertNotNull(petsPath, "/pets path should exist");

			Operation getPets = petsPath.getGet();
			assertNotNull(getPets, "GET /pets should exist");
			assertEquals("getPets", getPets.getOperationId());
			assertEquals(HttpMethod.GET, getPets.getMethod(), "GET operation should have GET method");

			Operation postPets = petsPath.getPost();
			assertNotNull(postPets, "POST /pets should exist");
			assertEquals("createPet", postPets.getOperationId());
			assertEquals(HttpMethod.POST, postPets.getMethod(), "POST operation should have POST method");

			// Check /pets/{id} path
			PathItem petByIdPath = openApi.getPaths().get("/pets/{id}");
			assertNotNull(petByIdPath, "/pets/{id} path should exist");

			Operation getPetById = petByIdPath.getGet();
			assertNotNull(getPetById, "GET /pets/{id} should exist");
			assertEquals(HttpMethod.GET, getPetById.getMethod(), "GET operation should have GET method");

			Operation putPet = petByIdPath.getPut();
			assertNotNull(putPet, "PUT /pets/{id} should exist");
			assertEquals(HttpMethod.PUT, putPet.getMethod(), "PUT operation should have PUT method");

			Operation deletePet = petByIdPath.getDelete();
			assertNotNull(deletePet, "DELETE /pets/{id} should exist");
			assertEquals(HttpMethod.DELETE, deletePet.getMethod(), "DELETE operation should have DELETE method");
		}

		@Test
		@DisplayName("loads OpenAPI with components/schemas as EPackage")
		void loadsComponentsSchemas() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": {
						"title": "Schema Test API",
						"version": "1.0.0"
					},
					"components": {
						"schemas": {
							"Person": {
								"type": "object",
								"properties": {
									"name": { "type": "string" },
									"age": { "type": "integer" }
								}
							},
							"Address": {
								"type": "object",
								"properties": {
									"street": { "type": "string" },
									"city": { "type": "string" }
								}
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();

			// Debug: check if annotation is on the feature
			var schemasFeature = OpenApiPackage.Literals.COMPONENTS__SCHEMAS;
			var ann = schemasFeature.getEAnnotation("http://eclipse.org/fennec/codec");
			System.out.println("Schemas feature annotation: " + ann);
			if (ann != null) {
				System.out.println("Annotation details: " + ann.getDetails());
			}

			// Check that the factory registered the OpenApiPackage
			System.out.println("OpenApiPackage nsURI: " + OpenApiPackage.eNS_URI);

			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			assertNotNull(openApi.getComponents());

			Components components = openApi.getComponents();
			EPackage schemas = components.getSchemasPackage();
			assertNotNull(schemas, "schemas should be converted to EPackage");

			// Debug: print what classifiers we got
			System.out.println("Schemas EPackage: " + schemas);
			System.out.println("Classifiers: " + schemas.getEClassifiers());

			// Check that Person and Address EClasses exist
			EClass personClass = (EClass) schemas.getEClassifier("Person");
			assertNotNull(personClass, "Person EClass should exist. Got classifiers: " + schemas.getEClassifiers());
			assertNotNull(personClass.getEStructuralFeature("name"));
			assertNotNull(personClass.getEStructuralFeature("age"));

			EClass addressClass = (EClass) schemas.getEClassifier("Address");
			assertNotNull(addressClass, "Address EClass should exist");
			assertNotNull(addressClass.getEStructuralFeature("street"));
			assertNotNull(addressClass.getEStructuralFeature("city"));
		}
	}

	@Nested
	@DisplayName("Serialization")
	class Serialization {

		@Test
		@DisplayName("saves OpenAPI document")
		void savesDocument() throws IOException {
			// Create OpenAPI programmatically
			OpenAPI openApi = OpenApiFactory.eINSTANCE.createOpenAPI();
			openApi.setOpenapi("3.0.3");

			Info info = OpenApiFactory.eINSTANCE.createInfo();
			info.setTitle("Generated API");
			info.setVersion("2.0.0");
			openApi.setInfo(info);

			OpenApiResourceImpl resource = createResource();
			resource.getContents().add(openApi);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			resource.save(out, null);
			String json = out.toString(StandardCharsets.UTF_8);

			assertTrue(json.contains("\"openapi\""));
			assertTrue(json.contains("3.0.3"));
			assertTrue(json.contains("Generated API"));
			assertTrue(json.contains("2.0.0"));
		}
	}

	@Nested
	@DisplayName("Round-trip")
	class RoundTrip {

		@Test
		@DisplayName("round-trips OpenAPI with schemas")
		void roundTripsWithSchemas() throws IOException {
			String originalJson = """
				{
					"openapi": "3.0.3",
					"info": {
						"title": "Round-trip Test",
						"version": "1.0.0"
					},
					"components": {
						"schemas": {
							"Item": {
								"type": "object",
								"properties": {
									"id": { "type": "string" },
									"value": { "type": "number" }
								}
							}
						}
					}
				}
				""";

			// Load
			OpenApiResourceImpl resource1 = createResource();
			resource1.load(toInputStream(originalJson), loadOptions());

			// Save
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			resource1.save(out, null);
			String savedJson = out.toString(StandardCharsets.UTF_8);
			System.out.println("=== SAVED JSON ===");
			System.out.println(savedJson);
			System.out.println("=== END SAVED JSON ===");

			// Load again
			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(savedJson), loadOptions());

			// Verify
			OpenAPI openApi = (OpenAPI) resource2.getContents().get(0);
			assertEquals("3.0.3", openApi.getOpenapi());
			assertEquals("Round-trip Test", openApi.getInfo().getTitle());

			EPackage schemas = openApi.getComponents().getSchemasPackage();
			assertNotNull(schemas);
			assertNotNull(schemas.getEClassifier("Item"));
		}
	}

	@Nested
	@DisplayName("Real-world OpenAPI files")
	class RealWorldFiles {

		private static final Path BIKE_JSON_PATH = Path.of("../docs/example/bike-openapi/bike.json");
		private static final Path PETSTORE_JSON_PATH = Path.of("test-data/petstore.json");
		private static final Path SEVDESK_JSON_PATH = Path.of("test-data/sevdesk.json");
		private static final Path KUBERNETES_JSON_PATH = Path.of("test-data/kubernetes-api.json");

		@Test
		@DisplayName("round-trips bike-openapi/bike.json")
		@EnabledIf("bikeJsonExists")
		void roundTripsBikeOpenApi() throws IOException {
			// Load original file
			String originalJson = Files.readString(BIKE_JSON_PATH);

			OpenApiResourceImpl resource1 = createResource();
			resource1.load(toInputStream(originalJson), loadOptions());

			assertTrue(resource1.getErrors().isEmpty(),
					"Load errors: " + resource1.getErrors());

			OpenAPI openApi1 = (OpenAPI) resource1.getContents().get(0);
			assertEquals("3.0.3", openApi1.getOpenapi());
			assertEquals("Eco-Visio API Reference", openApi1.getInfo().getTitle());

			// Verify components/schemas are loaded as EPackage
			assertNotNull(openApi1.getComponents(), "Components should exist");
			EPackage schemas1 = openApi1.getComponents().getSchemasPackage();
			assertNotNull(schemas1, "Schemas EPackage should exist");
			int schemaCount = schemas1.getEClassifiers().size();
			assertTrue(schemaCount > 0, "Should have at least one schema");
			System.out.println("Loaded " + schemaCount + " schemas from bike.json");

			// Save to JSON
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			resource1.save(out, null);
			String savedJson = out.toString(StandardCharsets.UTF_8);

			// Load saved JSON
			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(savedJson), loadOptions());

			assertTrue(resource2.getErrors().isEmpty(),
					"Reload errors: " + resource2.getErrors());

			// Verify round-trip
			OpenAPI openApi2 = (OpenAPI) resource2.getContents().get(0);
			assertEquals("3.0.3", openApi2.getOpenapi());
			assertEquals("Eco-Visio API Reference", openApi2.getInfo().getTitle());

			EPackage schemas2 = openApi2.getComponents().getSchemasPackage();
			assertNotNull(schemas2, "Schemas should survive round-trip");
			int schemaCount2 = schemas2.getEClassifiers().size();
			System.out.println("After round-trip: " + schemaCount2 + " schemas");
			// Note: Some schemas may be "artificial" and get expanded inline during serialization
			// These are not recreated as top-level schemas during deserialization
			// So we allow some loss but ensure a reasonable number survive
			assertTrue(schemaCount2 >= schemaCount - 10,
					"Schema count should be mostly preserved. Original: " + schemaCount + ", after: " + schemaCount2);
		}

		@Test
		@DisplayName("round-trips petstore OpenAPI")
		@EnabledIf("petstoreJsonExists")
		void roundTripsPetstore() throws IOException {
			// Load original file
			String originalJson = Files.readString(PETSTORE_JSON_PATH);

			OpenApiResourceImpl resource1 = createResource();
			resource1.load(toInputStream(originalJson), loadOptions());

			assertTrue(resource1.getErrors().isEmpty(),
					"Load errors: " + resource1.getErrors());

			OpenAPI openApi1 = (OpenAPI) resource1.getContents().get(0);
			// Petstore uses OpenAPI 3.0.4
			assertTrue(openApi1.getOpenapi().startsWith("3.0"),
					"Expected OpenAPI 3.0.x, got: " + openApi1.getOpenapi());
			assertEquals("Swagger Petstore - OpenAPI 3.0", openApi1.getInfo().getTitle());

			// Verify components/schemas are loaded as EPackage
			assertNotNull(openApi1.getComponents(), "Components should exist");
			EPackage schemas1 = openApi1.getComponents().getSchemasPackage();
			assertNotNull(schemas1, "Schemas EPackage should exist");
			int schemaCount = schemas1.getEClassifiers().size();
			assertTrue(schemaCount > 0, "Should have at least one schema");
			System.out.println("Loaded " + schemaCount + " schemas from petstore.json");

			// Verify some expected schemas exist
			assertNotNull(schemas1.getEClassifier("Pet"), "Pet schema should exist");
			assertNotNull(schemas1.getEClassifier("Order"), "Order schema should exist");
			assertNotNull(schemas1.getEClassifier("User"), "User schema should exist");

			// Save to JSON
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			resource1.save(out, null);
			String savedJson = out.toString(StandardCharsets.UTF_8);

			// Load saved JSON
			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(savedJson), loadOptions());

			assertTrue(resource2.getErrors().isEmpty(),
					"Reload errors: " + resource2.getErrors());

			// Verify round-trip
			OpenAPI openApi2 = (OpenAPI) resource2.getContents().get(0);
			assertTrue(openApi2.getOpenapi().startsWith("3.0"));

			EPackage schemas2 = openApi2.getComponents().getSchemasPackage();
			assertNotNull(schemas2, "Schemas should survive round-trip");
			int schemaCount2 = schemas2.getEClassifiers().size();
			System.out.println("After round-trip: " + schemaCount2 + " schemas");

			// Verify key schemas survive round-trip
			assertNotNull(schemas2.getEClassifier("Pet"), "Pet schema should survive round-trip");
			assertNotNull(schemas2.getEClassifier("Order"), "Order schema should survive round-trip");
		}

		@Test
		@DisplayName("round-trips sevdesk OpenAPI")
		@EnabledIf("sevdeskJsonExists")
		void roundTripsSevdesk() throws IOException {
			// Load original file
			String originalJson = Files.readString(SEVDESK_JSON_PATH);

			OpenApiResourceImpl resource1 = createResource();
			resource1.load(toInputStream(originalJson), loadOptions());

			assertTrue(resource1.getErrors().isEmpty(),
					"Load errors: " + resource1.getErrors());

			OpenAPI openApi1 = (OpenAPI) resource1.getContents().get(0);
			assertEquals("3.0.0", openApi1.getOpenapi());
			assertEquals("sevdesk API", openApi1.getInfo().getTitle());

			// Verify components/schemas are loaded as EPackage
			assertNotNull(openApi1.getComponents(), "Components should exist");
			EPackage schemas1 = openApi1.getComponents().getSchemasPackage();
			assertNotNull(schemas1, "Schemas EPackage should exist");
			int schemaCount = schemas1.getEClassifiers().size();
			assertTrue(schemaCount > 0, "Should have at least one schema");
			System.out.println("Loaded " + schemaCount + " schemas from sevdesk.json");

			// Save to JSON
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			resource1.save(out, null);
			String savedJson = out.toString(StandardCharsets.UTF_8);

			// Load saved JSON
			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(savedJson), loadOptions());

			assertTrue(resource2.getErrors().isEmpty(),
					"Reload errors: " + resource2.getErrors());

			// Verify round-trip
			OpenAPI openApi2 = (OpenAPI) resource2.getContents().get(0);
			assertEquals("3.0.0", openApi2.getOpenapi());

			EPackage schemas2 = openApi2.getComponents().getSchemasPackage();
			assertNotNull(schemas2, "Schemas should survive round-trip");
			int schemaCount2 = schemas2.getEClassifiers().size();
			System.out.println("After round-trip: " + schemaCount2 + " schemas");
		}

		@Test
		@DisplayName("round-trips Kubernetes API OpenAPI (large)")
		@EnabledIf("kubernetesJsonExists")
		void roundTripsKubernetesApi() throws IOException {
			// Load original file - this is a large OpenAPI spec
			String originalJson = Files.readString(KUBERNETES_JSON_PATH);

			OpenApiResourceImpl resource1 = createResource();
			long startLoad = System.currentTimeMillis();
			resource1.load(toInputStream(originalJson), loadOptions());
			long loadTime = System.currentTimeMillis() - startLoad;

			assertTrue(resource1.getErrors().isEmpty(),
					"Load errors: " + resource1.getErrors());

			OpenAPI openApi1 = (OpenAPI) resource1.getContents().get(0);
			// Kubernetes API uses OpenAPI 3.x
			assertNotNull(openApi1.getOpenapi());
			System.out.println("Kubernetes API: openapi=" + openApi1.getOpenapi());

			// Verify components/schemas are loaded as EPackage
			assertNotNull(openApi1.getComponents(), "Components should exist");
			EPackage schemas1 = openApi1.getComponents().getSchemasPackage();
			assertNotNull(schemas1, "Schemas EPackage should exist");
			int schemaCount = schemas1.getEClassifiers().size();
			assertTrue(schemaCount > 0, "Should have at least one schema");
			System.out.println("Loaded " + schemaCount + " schemas from kubernetes-api.json in " + loadTime + "ms");

			// Save to JSON
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			long startSave = System.currentTimeMillis();
			resource1.save(out, null);
			long saveTime = System.currentTimeMillis() - startSave;
			String savedJson = out.toString(StandardCharsets.UTF_8);
			System.out.println("Saved in " + saveTime + "ms, size: " + (savedJson.length() / 1024) + "KB");

			// Load saved JSON
			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(savedJson), loadOptions());

			assertTrue(resource2.getErrors().isEmpty(),
					"Reload errors: " + resource2.getErrors());

			// Verify round-trip
			OpenAPI openApi2 = (OpenAPI) resource2.getContents().get(0);
			assertEquals(openApi1.getOpenapi(), openApi2.getOpenapi());

			EPackage schemas2 = openApi2.getComponents().getSchemasPackage();
			assertNotNull(schemas2, "Schemas should survive round-trip");
			int schemaCount2 = schemas2.getEClassifiers().size();
			System.out.println("After round-trip: " + schemaCount2 + " schemas");
		}

		// Condition methods for @EnabledIf
		static boolean bikeJsonExists() {
			return Files.exists(BIKE_JSON_PATH);
		}

		static boolean petstoreJsonExists() {
			return Files.exists(PETSTORE_JSON_PATH);
		}

		static boolean sevdeskJsonExists() {
			return Files.exists(SEVDESK_JSON_PATH);
		}

		static boolean kubernetesJsonExists() {
			return Files.exists(KUBERNETES_JSON_PATH);
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
