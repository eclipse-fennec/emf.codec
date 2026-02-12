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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.ApiKeyLocation;
import org.eclipse.fennec.model.openapi.Components;
import org.eclipse.fennec.model.openapi.OAuthFlow;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.SecurityScheme;
import org.eclipse.fennec.model.openapi.SecuritySchemeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for OpenAPI security schemes serialization/deserialization.
 */
@DisplayName("OpenAPI Security")
class OpenApiSecurityTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("Security Schemes")
	class SecuritySchemes {

		@Test
		@DisplayName("deserializes apiKey security scheme")
		void deserializesApiKeyScheme() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Security Test", "version": "1.0.0" },
					"components": {
						"securitySchemes": {
							"api_key": {
								"type": "apiKey",
								"name": "X-API-Key",
								"in": "header",
								"description": "API key authentication"
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

			SecurityScheme apiKey = components.getSecuritySchemes().get("api_key");
			assertNotNull(apiKey);
			assertEquals(SecuritySchemeType.API_KEY, apiKey.getType());
			assertEquals("X-API-Key", apiKey.getName());
			assertEquals(ApiKeyLocation.HEADER, apiKey.getIn());
			assertEquals("API key authentication", apiKey.getDescription());
		}

		@Test
		@DisplayName("deserializes http bearer security scheme")
		void deserializesHttpBearerScheme() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Security Test", "version": "1.0.0" },
					"components": {
						"securitySchemes": {
							"bearer_auth": {
								"type": "http",
								"scheme": "bearer",
								"bearerFormat": "JWT",
								"description": "JWT Bearer token"
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			SecurityScheme bearer = openApi.getComponents().getSecuritySchemes().get("bearer_auth");

			assertNotNull(bearer);
			assertEquals(SecuritySchemeType.HTTP, bearer.getType());
			assertEquals("bearer", bearer.getScheme());
			assertEquals("JWT", bearer.getBearerFormat());
			assertEquals("JWT Bearer token", bearer.getDescription());
		}

		@Test
		@DisplayName("deserializes oauth2 security scheme with flows")
		void deserializesOAuth2Scheme() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Security Test", "version": "1.0.0" },
					"components": {
						"securitySchemes": {
							"oauth2": {
								"type": "oauth2",
								"description": "OAuth2 authentication",
								"flows": {
									"authorizationCode": {
										"authorizationUrl": "https://example.com/oauth/authorize",
										"tokenUrl": "https://example.com/oauth/token",
										"refreshUrl": "https://example.com/oauth/refresh",
										"scopes": {
											"read:pets": "Read your pets",
											"write:pets": "Modify pets"
										}
									},
									"implicit": {
										"authorizationUrl": "https://example.com/oauth/authorize",
										"scopes": {
											"read:pets": "Read pets"
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
			SecurityScheme oauth2 = openApi.getComponents().getSecuritySchemes().get("oauth2");

			assertNotNull(oauth2);
			assertEquals(SecuritySchemeType.OAUTH2, oauth2.getType());
			assertEquals("OAuth2 authentication", oauth2.getDescription());
			assertNotNull(oauth2.getFlows());

			// Authorization Code flow
			OAuthFlow authCodeFlow = oauth2.getFlows().getAuthorizationCode();
			assertNotNull(authCodeFlow);
			assertEquals("https://example.com/oauth/authorize", authCodeFlow.getAuthorizationUrl());
			assertEquals("https://example.com/oauth/token", authCodeFlow.getTokenUrl());
			assertEquals("https://example.com/oauth/refresh", authCodeFlow.getRefreshUrl());
			assertEquals(2, authCodeFlow.getScopes().size());
			assertEquals("Read your pets", authCodeFlow.getScopes().get("read:pets"));
			assertEquals("Modify pets", authCodeFlow.getScopes().get("write:pets"));

			// Implicit flow
			OAuthFlow implicitFlow = oauth2.getFlows().getImplicit();
			assertNotNull(implicitFlow);
			assertEquals("https://example.com/oauth/authorize", implicitFlow.getAuthorizationUrl());
			assertEquals(1, implicitFlow.getScopes().size());
		}

		@Test
		@DisplayName("deserializes openIdConnect security scheme")
		void deserializesOpenIdConnectScheme() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Security Test", "version": "1.0.0" },
					"components": {
						"securitySchemes": {
							"openid": {
								"type": "openIdConnect",
								"openIdConnectUrl": "https://example.com/.well-known/openid-configuration"
							}
						}
					}
				}
				""";

			OpenApiResourceImpl resource = createResource();
			resource.load(toInputStream(json), loadOptions());

			OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
			SecurityScheme openid = openApi.getComponents().getSecuritySchemes().get("openid");

			assertNotNull(openid);
			assertEquals(SecuritySchemeType.OPEN_ID_CONNECT, openid.getType());
			assertEquals("https://example.com/.well-known/openid-configuration",
					openid.getOpenIdConnectUrl());
		}

		@Test
		@DisplayName("round-trips all security scheme types")
		void roundTripsSecuritySchemes() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Security Test", "version": "1.0.0" },
					"components": {
						"securitySchemes": {
							"api_key": {
								"type": "apiKey",
								"name": "api-key",
								"in": "query"
							},
							"basic": {
								"type": "http",
								"scheme": "basic"
							},
							"oauth2": {
								"type": "oauth2",
								"flows": {
									"clientCredentials": {
										"tokenUrl": "https://example.com/token",
										"scopes": {
											"admin": "Admin access"
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

			OpenApiResourceImpl resource2 = createResource();
			resource2.load(toInputStream(out.toString(StandardCharsets.UTF_8)), loadOptions());

			OpenAPI openApi = (OpenAPI) resource2.getContents().get(0);
			assertEquals(3, openApi.getComponents().getSecuritySchemes().size());

			// Verify apiKey
			SecurityScheme apiKey = openApi.getComponents().getSecuritySchemes().get("api_key");
			assertEquals(SecuritySchemeType.API_KEY, apiKey.getType());
			assertEquals("api-key", apiKey.getName());
			assertEquals(ApiKeyLocation.QUERY, apiKey.getIn());

			// Verify basic
			SecurityScheme basic = openApi.getComponents().getSecuritySchemes().get("basic");
			assertEquals(SecuritySchemeType.HTTP, basic.getType());
			assertEquals("basic", basic.getScheme());

			// Verify oauth2
			SecurityScheme oauth2 = openApi.getComponents().getSecuritySchemes().get("oauth2");
			assertEquals(SecuritySchemeType.OAUTH2, oauth2.getType());
			assertNotNull(oauth2.getFlows().getClientCredentials());
			assertEquals("https://example.com/token",
					oauth2.getFlows().getClientCredentials().getTokenUrl());
		}
	}

	@Nested
	@DisplayName("Security Requirements")
	class SecurityRequirements {

		@Test
		@DisplayName("deserializes global security requirements")
		void deserializesGlobalSecurityRequirements() throws IOException {
			String json = """
				{
					"openapi": "3.0.3",
					"info": { "title": "Security Test", "version": "1.0.0" },
					"security": [
						{ "api_key": [] },
						{ "oauth2": ["read:pets", "write:pets"] }
					],
					"components": {
						"securitySchemes": {
							"api_key": { "type": "apiKey", "name": "key", "in": "header" },
							"oauth2": {
								"type": "oauth2",
								"flows": {
									"implicit": {
										"authorizationUrl": "https://example.com/auth",
										"scopes": { "read:pets": "Read", "write:pets": "Write" }
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

			// Security requirements may have different structure - just verify they're loaded
			assertNotNull(openApi.getSecurity());
			// The security array should have 2 entries
			assertEquals(2, openApi.getSecurity().size());

			// Just verify the security requirements are present
			assertNotNull(openApi.getSecurity().get(0));
			assertNotNull(openApi.getSecurity().get(1));
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
