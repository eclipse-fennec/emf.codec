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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Reproducer for <a href="https://github.com/eclipse-fennec/emf.codec/issues/44">#44</a>:
 * security requirements lose their dynamic scheme names — {@code {"api_key": []}} deserializes
 * to a {@link SecurityRequirement} with an <b>empty</b> {@code schemes} EMap (the JSON object
 * IS the map, the model wraps it in a {@code schemes} feature the generic deserializer cannot
 * match). Affects {@code OpenAPI.security} and {@code Operation.security} alike;
 * {@code components/securitySchemes} works.
 * <p>
 * Suggested fix (see issue): a {@code SecurityRequirementValueReader} next to
 * {@link OperationValueReader}, registered in {@link OpenApiResourceFactoryImpl} and bound via
 * {@code valueReaderName} annotations on the two {@code security} features, plus the mirror
 * writer for round-trips. {@code OpenApiSecurityTest.deserializesGlobalSecurityRequirements}
 * should be sharpened along with the fix (it currently only asserts the list size).
 * <p>
 * Enable when fixing #44.
 */
@Disabled("Reproducer for https://github.com/eclipse-fennec/emf.codec/issues/44 — enable when fixing")
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

	private OpenAPI load() throws IOException {
		OpenApiResourceFactoryImpl factory = new OpenApiResourceFactoryImpl();
		CodecResource resource = (CodecResource) factory.createResource(URI.createURI("test://security.json"));
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		resource.load(new ByteArrayInputStream(DOC.getBytes(StandardCharsets.UTF_8)), options);
		return (OpenAPI) resource.getContents().get(0);
	}
}
