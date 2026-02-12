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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for OpenAPI Schema 'default' attribute handling.
 *
 * In JSON Schema / OpenAPI, the 'default' field can be ANY JSON value:
 * - string: "default": "hello"
 * - number: "default": 42
 * - boolean: "default": true
 * - object: "default": {"key": "value"}
 * - array: "default": [1, 2, 3]
 * - null: "default": null
 *
 * The current OpenAPI model defines 'default' as EString, which only handles
 * string values directly. Object and array defaults cause warnings.
 */
@DisplayName("OpenAPI Schema Default Values")
class OpenApiSchemaDefaultTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("String Default Values")
	class StringDefaults {

		@Test
		@DisplayName("deserializes string default value")
		void deserializesStringDefault() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Config": {
								"type": "object",
								"properties": {
									"name": {
										"type": "string",
										"default": "defaultName"
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
			Schema configSchema = openApi.getComponents().getSchemas().get("Config");
			assertNotNull(configSchema);

			Schema nameSchema = configSchema.getProperties().get("name");
			assertNotNull(nameSchema);
			assertEquals("string", nameSchema.getType());
			assertEquals("defaultName", nameSchema.getDefault());
		}

		@Test
		@DisplayName("round-trips string default value")
		void roundTripsStringDefault() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Config": {
								"type": "object",
								"properties": {
									"status": {
										"type": "string",
										"default": "active"
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

			// Verify default is in saved JSON
			assertTrue(savedJson.contains("\"default\""), "Saved JSON should contain default");
			assertTrue(savedJson.contains("active"), "Saved JSON should contain default value");

			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(savedJson), loadOptions());

			OpenAPI openApi = (OpenAPI) resource2.getContents().get(0);
			Schema statusSchema = openApi.getComponents().getSchemas().get("Config")
					.getProperties().get("status");
			assertEquals("active", statusSchema.getDefault());
		}
	}

	@Nested
	@DisplayName("Number Default Values")
	class NumberDefaults {

		@Test
		@DisplayName("deserializes integer default value")
		void deserializesIntegerDefault() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Config": {
								"type": "object",
								"properties": {
									"count": {
										"type": "integer",
										"default": 10
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
			Schema countSchema = openApi.getComponents().getSchemas().get("Config")
					.getProperties().get("count");
			assertNotNull(countSchema);
			assertEquals("integer", countSchema.getType());
			// With EJavaObject, number defaults are native Number types
			Object defaultValue = countSchema.getDefault();
			assertNotNull(defaultValue);
			assertTrue(defaultValue instanceof Number,
					"Default should be Number, was: " + defaultValue.getClass().getName());
			assertEquals(10, ((Number) defaultValue).intValue());
		}

		@Test
		@DisplayName("deserializes decimal default value")
		void deserializesDecimalDefault() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Config": {
								"type": "object",
								"properties": {
									"rate": {
										"type": "number",
										"default": 3.14
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
			Schema rateSchema = openApi.getComponents().getSchemas().get("Config")
					.getProperties().get("rate");
			assertNotNull(rateSchema);
			assertEquals("number", rateSchema.getType());
			// With EJavaObject, decimal defaults are native Number types
			Object defaultValue = rateSchema.getDefault();
			assertNotNull(defaultValue);
			assertTrue(defaultValue instanceof Number,
					"Default should be Number, was: " + defaultValue.getClass().getName());
			assertEquals(3.14, ((Number) defaultValue).doubleValue(), 0.001);
		}
	}

	@Nested
	@DisplayName("Boolean Default Values")
	class BooleanDefaults {

		@Test
		@DisplayName("deserializes boolean default value")
		void deserializesBooleanDefault() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Config": {
								"type": "object",
								"properties": {
									"enabled": {
										"type": "boolean",
										"default": true
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
			Schema enabledSchema = openApi.getComponents().getSchemas().get("Config")
					.getProperties().get("enabled");
			assertNotNull(enabledSchema);
			assertEquals("boolean", enabledSchema.getType());
			// With EJavaObject, boolean defaults are native Boolean
			Object defaultValue = enabledSchema.getDefault();
			assertNotNull(defaultValue);
			assertTrue(defaultValue instanceof Boolean,
					"Default should be Boolean, was: " + defaultValue.getClass().getName());
			assertEquals(Boolean.TRUE, defaultValue);
		}
	}

	@Nested
	@DisplayName("Object Default Values")
	class ObjectDefaults {

		@Test
		@DisplayName("deserializes object default value as Map")
		void deserializesObjectDefaultAsMap() throws IOException {
			// With EJavaObject type, object defaults are now properly handled
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Config": {
								"type": "object",
								"properties": {
									"settings": {
										"type": "object",
										"default": {
											"timeout": 30,
											"retries": 3
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
			assertTrue(resource.getErrors().isEmpty(), "Should have no errors");

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			Schema settingsSchema = openApi.getComponents().getSchemas().get("Config")
					.getProperties().get("settings");
			assertNotNull(settingsSchema);
			assertEquals("object", settingsSchema.getType());

			// With EJavaObject, the default is now a Map
			Object defaultValue = settingsSchema.getDefault();
			assertNotNull(defaultValue, "Default should not be null");
			assertTrue(defaultValue instanceof Map,
					"Default should be a Map, but was: " + defaultValue.getClass().getName());

			@SuppressWarnings("unchecked")
			Map<String, Object> defaultMap = (Map<String, Object>) defaultValue;
			assertEquals(30, ((Number) defaultMap.get("timeout")).intValue());
			assertEquals(3, ((Number) defaultMap.get("retries")).intValue());
		}
	}

	@Nested
	@DisplayName("Array Default Values")
	class ArrayDefaults {

		@Test
		@DisplayName("deserializes array default value as List")
		void deserializesArrayDefaultAsList() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Test", "version": "1.0.0" },
					"components": {
						"schemas": {
							"Config": {
								"type": "object",
								"properties": {
									"tags": {
										"type": "array",
										"items": { "type": "string" },
										"default": ["tag1", "tag2"]
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
			Schema tagsSchema = openApi.getComponents().getSchemas().get("Config")
					.getProperties().get("tags");
			assertNotNull(tagsSchema);
			assertEquals("array", tagsSchema.getType());

			// With EJavaObject, the default is now a List
			Object defaultValue = tagsSchema.getDefault();
			assertNotNull(defaultValue, "Default should not be null");
			assertTrue(defaultValue instanceof List,
					"Default should be a List, but was: " + defaultValue.getClass().getName());

			@SuppressWarnings("unchecked")
			List<String> defaultList = (List<String>) defaultValue;
			assertEquals(2, defaultList.size());
			assertEquals("tag1", defaultList.get(0));
			assertEquals("tag2", defaultList.get(1));
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
