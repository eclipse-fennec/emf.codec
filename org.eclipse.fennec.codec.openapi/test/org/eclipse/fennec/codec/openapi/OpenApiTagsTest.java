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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.ExternalDocumentation;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for OpenAPI tags and externalDocs serialization/deserialization.
 */
@DisplayName("OpenAPI Tags and ExternalDocs")
class OpenApiTagsTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Test
	@DisplayName("deserializes tags with externalDocs")
	void deserializesTagsWithExternalDocs() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": { "title": "Tags Test", "version": "1.0.0" },
				"tags": [
					{
						"name": "pets",
						"description": "Everything about your Pets",
						"externalDocs": {
							"description": "Find out more",
							"url": "http://example.com/pets"
						}
					},
					{
						"name": "store",
						"description": "Access to Petstore orders"
					},
					{
						"name": "user",
						"description": "Operations about user"
					}
				]
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
		assertEquals(3, openApi.getTags().size());

		Tag petsTag = openApi.getTags().get(0);
		assertEquals("pets", petsTag.getName());
		assertEquals("Everything about your Pets", petsTag.getDescription());
		assertNotNull(petsTag.getExternalDocs());
		assertEquals("Find out more", petsTag.getExternalDocs().getDescription());
		assertEquals("http://example.com/pets", petsTag.getExternalDocs().getUrl());

		Tag storeTag = openApi.getTags().get(1);
		assertEquals("store", storeTag.getName());
		assertEquals("Access to Petstore orders", storeTag.getDescription());
		assertNull(storeTag.getExternalDocs());

		Tag userTag = openApi.getTags().get(2);
		assertEquals("user", userTag.getName());
	}

	@Test
	@DisplayName("deserializes root externalDocs")
	void deserializesRootExternalDocs() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": { "title": "ExternalDocs Test", "version": "1.0.0" },
				"externalDocs": {
					"description": "Find out more about our API",
					"url": "http://example.com/docs"
				}
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
		ExternalDocumentation extDocs = openApi.getExternalDocs();

		assertNotNull(extDocs);
		assertEquals("Find out more about our API", extDocs.getDescription());
		assertEquals("http://example.com/docs", extDocs.getUrl());
	}

	@Test
	@DisplayName("round-trips tags and externalDocs")
	void roundTripsTagsAndExternalDocs() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": { "title": "Round-trip Test", "version": "1.0.0" },
				"tags": [
					{
						"name": "api",
						"description": "API operations",
						"externalDocs": {
							"description": "API docs",
							"url": "http://docs.example.com"
						}
					}
				],
				"externalDocs": {
					"description": "Main documentation",
					"url": "http://example.com/main-docs"
				}
			}
			""";

		// Load
		OpenApiResourceImpl resource1 = createResource();
		resource1.load(toInputStream(json), loadOptions());

		// Save
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource1.save(out, null);
		String savedJson = out.toString(StandardCharsets.UTF_8);

		// Reload
		OpenApiResourceImpl resource2 = createResource();
		resource2.load(toInputStream(savedJson), loadOptions());

		OpenAPI openApi = (OpenAPI) resource2.getContents().get(0);

		// Verify tags
		assertEquals(1, openApi.getTags().size());
		Tag tag = openApi.getTags().get(0);
		assertEquals("api", tag.getName());
		assertEquals("API operations", tag.getDescription());
		assertNotNull(tag.getExternalDocs());
		assertEquals("http://docs.example.com", tag.getExternalDocs().getUrl());

		// Verify root externalDocs
		assertNotNull(openApi.getExternalDocs());
		assertEquals("Main documentation", openApi.getExternalDocs().getDescription());
		assertEquals("http://example.com/main-docs", openApi.getExternalDocs().getUrl());
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
