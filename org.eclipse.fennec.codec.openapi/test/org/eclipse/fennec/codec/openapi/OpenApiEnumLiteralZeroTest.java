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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.ApiKeyLocation;
import org.eclipse.fennec.model.openapi.Components;
import org.eclipse.fennec.model.openapi.Info;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiFactory;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Parameter;
import org.eclipse.fennec.model.openapi.ParameterLocation;
import org.eclipse.fennec.model.openapi.ParameterStyle;
import org.eclipse.fennec.model.openapi.SecurityScheme;
import org.eclipse.fennec.model.openapi.SecuritySchemeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Enum attributes whose value was literal 0 of their enum (issue #269).
 * <p>
 * The codec omits a value equal to the feature default, and the default of an EEnum attribute is
 * its literal 0 - EMF does not allow {@code null} there, whatever the lower bound. So
 * {@code in: query} and {@code type: apiKey}, both REQUIRED in OpenAPI 3.0, were lost on write.
 * The enums now start with a {@code NULL} literal for "not given". That matters in the other
 * direction too: OpenAPI derives an absent {@code style} and {@code explode} from {@code in},
 * so they must stay absent rather than read as {@code matrix} / {@code false};
 * {@code explode} is a {@code Boolean} for the same reason.
 * </p>
 */
@DisplayName("OpenAPI enum attributes at literal 0 (#269)")
class OpenApiEnumLiteralZeroTest {

    @Test
    @DisplayName("writes in: query of a parameter built in code")
    void writesQueryLocationOfBuiltParameter() throws IOException {
        Parameter limit = OpenApiFactory.eINSTANCE.createParameter();
        limit.setName("limit");
        limit.setIn(ParameterLocation.QUERY);
        OpenAPI openApi = createDocument();
        openApi.getComponents().getParameters().put("limit", limit);

        JsonNode parameter = save(openApi).at("/components/parameters/limit");

        assertEquals("query", parameter.path("in").asString(), parameter.toString());
        assertNull(parameter.get("style"), "an unset style must not be written as matrix");
    }

    @Test
    @DisplayName("writes type: apiKey and in: query of a security scheme built in code")
    void writesApiKeyInQueryOfBuiltSecurityScheme() throws IOException {
        SecurityScheme apiKey = OpenApiFactory.eINSTANCE.createSecurityScheme();
        apiKey.setType(SecuritySchemeType.API_KEY);
        apiKey.setName("api_key");
        apiKey.setIn(ApiKeyLocation.QUERY);
        OpenAPI openApi = createDocument();
        openApi.getComponents().getSecuritySchemes().put("api_key", apiKey);

        JsonNode scheme = save(openApi).at("/components/securitySchemes/api_key");

        assertEquals("apiKey", scheme.path("type").asString(), scheme.toString());
        assertEquals("query", scheme.path("in").asString(), scheme.toString());
    }

    @Test
    @DisplayName("keeps in: query, type: apiKey and an explicit style: matrix through a round-trip")
    void keepsLiteralZeroValuesThroughRoundTrip() throws IOException {
        String json = """
            {
                "openapi": "3.0.3",
                "info": { "title": "Literal 0", "version": "1.0.0" },
                "components": {
                    "parameters": {
                        "limit": { "name": "limit", "in": "query" },
                        "id": { "name": "id", "in": "path", "required": true, "style": "matrix" }
                    },
                    "securitySchemes": {
                        "api_key": { "type": "apiKey", "name": "api_key", "in": "query" }
                    }
                }
            }
            """;

        JsonNode saved = save(load(json));

        JsonNode limit = saved.at("/components/parameters/limit");
        assertEquals("query", limit.path("in").asString(), limit.toString());
        assertNull(limit.get("style"), limit.toString());
        assertEquals("matrix", saved.at("/components/parameters/id/style").asString());
        JsonNode scheme = saved.at("/components/securitySchemes/api_key");
        assertEquals("apiKey", scheme.path("type").asString(), scheme.toString());
        assertEquals("query", scheme.path("in").asString(), scheme.toString());
    }

    @Test
    @DisplayName("reads an absent style or explode as NULL / null, not as matrix / false")
    void readsAbsentValuesAsNull() throws IOException {
        String json = """
            {
                "openapi": "3.0.3",
                "info": { "title": "Literal 0", "version": "1.0.0" },
                "components": {
                    "parameters": {
                        "limit": { "name": "limit", "in": "query" },
                        "id": { "name": "id", "in": "path", "required": true, "style": "matrix", "explode": false }
                    },
                    "securitySchemes": {
                        "bearer": { "type": "http", "scheme": "bearer" }
                    }
                }
            }
            """;

        Components components = load(json).getComponents();

        Parameter limit = components.getParameters().get("limit");
        assertNotNull(limit);
        assertEquals(ParameterLocation.QUERY, limit.getIn());
        assertEquals(ParameterStyle.NULL, limit.getStyle());
        assertNull(limit.getExplode());
        Parameter id = components.getParameters().get("id");
        assertEquals(ParameterStyle.MATRIX, id.getStyle());
        assertEquals(Boolean.FALSE, id.getExplode());
        assertEquals(ApiKeyLocation.NULL, components.getSecuritySchemes().get("bearer").getIn());
    }

    @Test
    @DisplayName("writes an explicit explode: false and leaves an unset explode and in of an http scheme out")
    void writesExplicitExplodeOnly() throws IOException {
        Parameter tags = OpenApiFactory.eINSTANCE.createParameter();
        tags.setName("tags");
        tags.setIn(ParameterLocation.QUERY);
        tags.setExplode(Boolean.FALSE);
        Parameter limit = OpenApiFactory.eINSTANCE.createParameter();
        limit.setName("limit");
        limit.setIn(ParameterLocation.QUERY);
        SecurityScheme bearer = OpenApiFactory.eINSTANCE.createSecurityScheme();
        bearer.setType(SecuritySchemeType.HTTP);
        bearer.setScheme("bearer");
        OpenAPI openApi = createDocument();
        openApi.getComponents().getParameters().put("tags", tags);
        openApi.getComponents().getParameters().put("limit", limit);
        openApi.getComponents().getSecuritySchemes().put("bearer", bearer);

        JsonNode components = save(openApi).path("components");

        JsonNode tagsNode = components.at("/parameters/tags");
        assertTrue(tagsNode.has("explode"), tagsNode.toString());
        assertFalse(tagsNode.get("explode").asBoolean());
        JsonNode limitNode = components.at("/parameters/limit");
        assertNull(limitNode.get("explode"), "an unset explode depends on style and must not be written");
        JsonNode bearerNode = components.at("/securitySchemes/bearer");
        assertNull(bearerNode.get("in"), "in applies to apiKey schemes only");
    }

    private OpenAPI createDocument() {
        OpenAPI openApi = OpenApiFactory.eINSTANCE.createOpenAPI();
        openApi.setOpenapi("3.0.3");
        Info info = OpenApiFactory.eINSTANCE.createInfo();
        info.setTitle("Literal 0");
        info.setVersion("1.0.0");
        openApi.setInfo(info);
        openApi.setComponents(OpenApiFactory.eINSTANCE.createComponents());
        return openApi;
    }

    private OpenAPI load(String json) throws IOException {
        Resource resource = createResource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return (OpenAPI) resource.getContents().get(0);
    }

    private JsonNode save(OpenAPI openApi) throws IOException {
        Resource resource = createResource();
        resource.getContents().add(openApi);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return JsonMapper.builder().build().readTree(out.toString(StandardCharsets.UTF_8));
    }

    private Resource createResource() {
        return new OpenApiResourceFactoryImpl().createResource(URI.createURI("test://openapi.json"));
    }
}
