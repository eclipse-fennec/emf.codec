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
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Server;
import org.eclipse.fennec.model.openapi.ServerVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for OpenAPI servers serialization/deserialization.
 */
@DisplayName("OpenAPI Servers")
class OpenApiServersTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Test
	@DisplayName("deserializes servers with variables")
	void deserializesServersWithVariables() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": { "title": "Server Test", "version": "1.0.0" },
				"servers": [
					{
						"url": "https://{environment}.example.com:{port}/v1",
						"description": "Production server",
						"variables": {
							"environment": {
								"default": "api",
								"enum": ["api", "staging", "dev"],
								"description": "Server environment"
							},
							"port": {
								"default": "443",
								"enum": ["443", "8443"]
							}
						}
					},
					{
						"url": "http://localhost:3000",
						"description": "Local development"
					}
				]
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
		assertEquals(2, openApi.getServers().size());

		Server server1 = openApi.getServers().get(0);
		assertEquals("https://{environment}.example.com:{port}/v1", server1.getUrl());
		assertEquals("Production server", server1.getDescription());
		assertEquals(2, server1.getVariables().size());

		ServerVariable envVar = server1.getVariables().get("environment");
		assertNotNull(envVar, "environment variable should exist");
		assertEquals("api", envVar.getDefault());
		assertEquals(3, envVar.getEnum().size());
		assertTrue(envVar.getEnum().contains("api"));
		assertTrue(envVar.getEnum().contains("staging"));
		assertTrue(envVar.getEnum().contains("dev"));
		assertEquals("Server environment", envVar.getDescription());

		ServerVariable portVar = server1.getVariables().get("port");
		assertNotNull(portVar);
		assertEquals("443", portVar.getDefault());
		assertEquals(2, portVar.getEnum().size());

		Server server2 = openApi.getServers().get(1);
		assertEquals("http://localhost:3000", server2.getUrl());
		assertEquals("Local development", server2.getDescription());
	}

	@Test
	@DisplayName("round-trips servers with variables")
	void roundTripsServersWithVariables() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": { "title": "Server Test", "version": "1.0.0" },
				"servers": [
					{
						"url": "https://{env}.example.com/api",
						"description": "Main server",
						"variables": {
							"env": {
								"default": "prod",
								"enum": ["prod", "staging"],
								"description": "Environment"
							}
						}
					}
				]
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
		assertEquals(1, openApi.getServers().size());

		Server server = openApi.getServers().get(0);
		assertEquals("https://{env}.example.com/api", server.getUrl());
		assertEquals("Main server", server.getDescription());

		ServerVariable envVar = server.getVariables().get("env");
		assertNotNull(envVar);
		assertEquals("prod", envVar.getDefault());
		assertEquals(2, envVar.getEnum().size());
		assertEquals("Environment", envVar.getDescription());
	}

	@Test
	@DisplayName("deserializes simple server without variables")
	void deserializesSimpleServer() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": { "title": "Simple Server", "version": "1.0.0" },
				"servers": [
					{ "url": "https://api.example.com" }
				]
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
		assertEquals(1, openApi.getServers().size());

		Server server = openApi.getServers().get(0);
		assertEquals("https://api.example.com", server.getUrl());
	}

	private OpenApiResourceImpl createResource() {
		Resource.Factory factory = new OpenApiResourceFactoryImpl();
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
