/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Operation;
import org.eclipse.fennec.model.openapi.SecurityRequirement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Regression test for <a href="https://github.com/eclipse-fennec/emf.codec/issues/44">#44</a>:
 * security requirements lost their dynamic scheme names — {@code {"api_key": []}} deserialized
 * to a {@link SecurityRequirement} with an empty {@code schemes} EMap, because the JSON object
 * IS the map (dynamic scheme names as field names) while the model wraps it in a
 * {@code schemes} feature the generic deserializer cannot match. Fixed by
 * {@link SecurityRequirementValueReader}/{@link SecurityRequirementValueWriter}, bound via
 * {@code valueReaderName}/{@code valueWriterName} annotations on {@code OpenAPI.security} and
 * {@code Operation.security}.
 */
@DisplayName("Security requirements: scheme names + scopes must survive loading (#44)")
class OpenApiSecurityRequirementReproTest {

	private static final String DOC = """
		{
			"openapi": "3.0.3",
			"info": { "title": "Security Test", "version": "1.0.0" },
			"security": [
				{ "api_key": [] },
				{ "oauth2": ["read:pets", "write:pets"] }
			],
			"paths": {
				"/pets": { "get": {
					"operationId": "listPets",
					"security": [ { "oauth2": ["read:pets"] } ],
					"responses": { "200": { "description": "ok" } }
				} }
			},
			"components": { "securitySchemes": {
				"api_key": { "type": "apiKey", "name": "key", "in": "header" },
				"oauth2": { "type": "oauth2", "flows": { "implicit": {
					"authorizationUrl": "https://example.com/auth",
					"scopes": { "read:pets": "Read", "write:pets": "Write" } } } }
			} }
		}
		""";

	@Test
	@DisplayName("global security requirements keep scheme names and scopes")
	void globalSecurityRequirementsKeepSchemes() throws IOException {
		OpenAPI openApi = load();

		assertEquals(2, openApi.getSecurity().size());

		SecurityRequirement apiKey = openApi.getSecurity().get(0);
		// currently empty — the dynamic scheme names are dropped
		assertEquals(1, apiKey.getSchemes().size(), "requirement must keep its scheme entry");
		assertNotNull(apiKey.getSchemes().get("api_key"));
		assertEquals(List.of(), apiKey.getSchemes().get("api_key"));

		SecurityRequirement oauth = openApi.getSecurity().get(1);
		assertEquals(1, oauth.getSchemes().size());
		assertEquals(List.of("read:pets", "write:pets"), oauth.getSchemes().get("oauth2"));
	}

	@Test
	@DisplayName("per-operation security requirements keep scheme names and scopes")
	void operationSecurityRequirementsKeepSchemes() throws IOException {
		OpenAPI openApi = load();

		Operation listPets = openApi.getPaths().get("/pets").getGet();
		assertEquals(1, listPets.getSecurity().size());
		assertEquals(List.of("read:pets"), listPets.getSecurity().get(0).getSchemes().get("oauth2"));
	}

	@Test
	@DisplayName("security requirements round-trip in OpenAPI shape")
	void securityRequirementsRoundTrip() throws IOException {
		CodecResource resource = loadResource();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, null);
		String savedJson = out.toString(StandardCharsets.UTF_8);

		JsonNode saved = JsonMapper.builder().build().readTree(savedJson);
		JsonNode security = saved.get("security");
		assertNotNull(security, "saved document must contain the security array");
		assertEquals(2, security.size());
		assertEquals(0, security.get(0).get("api_key").size());
		assertEquals("read:pets", security.get(1).get("oauth2").get(0).asString());
		assertEquals("write:pets", security.get(1).get("oauth2").get(1).asString());

		JsonNode operationSecurity = saved.at("/paths/~1pets/get/security");
		assertEquals("read:pets", operationSecurity.get(0).get("oauth2").get(0).asString());
	}

	private OpenAPI load() throws IOException {
		return (OpenAPI) loadResource().getContents().get(0);
	}

	private CodecResource loadResource() throws IOException {
		OpenApiResourceFactoryImpl factory = new OpenApiResourceFactoryImpl();
		CodecResource resource = (CodecResource) factory.createResource(URI.createURI("test://security.json"));
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		resource.load(new ByteArrayInputStream(DOC.getBytes(StandardCharsets.UTF_8)), options);
		return resource;
	}
}
