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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.model.openapi.Info;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiFactory;
import org.eclipse.fennec.model.openapi.PathItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * The {@code paths} member of an OpenAPI document (issue #276).
 * <p>
 * OpenAPI 3.0 requires {@code paths} on the OpenAPI Object, an empty Paths Object is allowed.
 * </p>
 */
@DisplayName("OpenAPI paths member (#276)")
class OpenApiEmptyPathsTest {

    @Test
    @DisplayName("a document without paths is written with an empty paths object")
    void writesEmptyPaths() throws IOException {
        JsonNode document = save(createDocument());

        assertTrue(document.has("paths"), document.toString());
        assertTrue(document.get("paths").isObject(), document.toString());
        assertEquals(0, document.get("paths").size(), document.toString());
    }

    @Test
    @DisplayName("a document with paths writes them as before")
    void writesPaths() throws IOException {
        OpenAPI openApi = createDocument();
        PathItem pets = OpenApiFactory.eINSTANCE.createPathItem();
        pets.setSummary("Pets");
        openApi.getPaths().put("/pets", pets);

        JsonNode document = save(openApi);

        assertEquals("Pets", document.at("/paths/~1pets/summary").asString(), document.toString());
    }

    private OpenAPI createDocument() {
        OpenAPI openApi = OpenApiFactory.eINSTANCE.createOpenAPI();
        openApi.setOpenapi("3.0.3");
        Info info = OpenApiFactory.eINSTANCE.createInfo();
        info.setTitle("Paths");
        info.setVersion("1.0.0");
        openApi.setInfo(info);
        return openApi;
    }

    private JsonNode save(OpenAPI openApi) throws IOException {
        Resource resource = new OpenApiResourceFactoryImpl().createResource(URI.createURI("test://openapi.json"));
        resource.getContents().add(openApi);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, null);
        return JsonMapper.builder().build().readTree(out.toString(StandardCharsets.UTF_8));
    }
}
