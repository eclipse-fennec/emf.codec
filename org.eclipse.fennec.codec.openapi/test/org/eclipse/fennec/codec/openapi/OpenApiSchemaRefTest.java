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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.Info;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiFactory;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * {@code $ref}s between the schemas generated into {@code components/schemas} (issue #273).
 * <p>
 * The schemas are embedded at {@code #/components/schemas}, so every reference between them
 * has to point there; {@code #/definitions/...} does not exist in an OpenAPI document.
 * </p>
 */
@DisplayName("components/schemas $ref paths (#273)")
class OpenApiSchemaRefTest {

    private static final String SCHEMAS = "#/components/schemas/";

    @Test
    @DisplayName("references and supertypes of generated schemas point into components/schemas")
    void generatedRefsPointIntoComponentsSchemas() throws IOException {
        JsonNode schemas = save(createDocument()).at("/components/schemas");

        assertEquals(SCHEMAS + "Address", schemas.at("/Person/properties/address/$ref").asString(),
                schemas.toString());
        assertEquals(SCHEMAS + "Person", schemas.at("/Person/properties/friend/$ref").asString(),
                schemas.toString());
        assertEquals(SCHEMAS + "Person", schemas.at("/Employee/allOf/0/$ref").asString(),
                schemas.toString());
        assertFalse(schemas.toString().contains("#/definitions/"), schemas.toString());
    }

    @Test
    @DisplayName("a loaded document keeps its components/schemas refs when saved again")
    void roundTripKeepsComponentsSchemasRefs() throws IOException {
        String json = """
            {
                "openapi": "3.0.3",
                "info": { "title": "Refs", "version": "1.0.0" },
                "paths": {},
                "components": {
                    "schemas": {
                        "Person": {
                            "type": "object",
                            "properties": {
                                "address": { "$ref": "#/components/schemas/Address" }
                            }
                        },
                        "Address": {
                            "type": "object",
                            "properties": { "city": { "type": "string" } }
                        }
                    }
                }
            }
            """;
        Resource loaded = createResource();
        loaded.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
                Map.of(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API));

        JsonNode schemas = save((OpenAPI) loaded.getContents().get(0)).at("/components/schemas");

        assertEquals(SCHEMAS + "Address", schemas.at("/Person/properties/address/$ref").asString(),
                schemas.toString());
    }

    private OpenAPI createDocument() {
        OpenAPI openApi = OpenApiFactory.eINSTANCE.createOpenAPI();
        openApi.setOpenapi("3.0.3");
        Info info = OpenApiFactory.eINSTANCE.createInfo();
        info.setTitle("Refs");
        info.setVersion("1.0.0");
        openApi.setInfo(info);
        openApi.setComponents(OpenApiFactory.eINSTANCE.createComponents());
        openApi.getComponents().setSchemasPackage(createPackage());
        return openApi;
    }

    private EPackage createPackage() {
        EcoreFactory factory = EcoreFactory.eINSTANCE;
        EPackage ePackage = factory.createEPackage();
        ePackage.setName("person");
        ePackage.setNsPrefix("person");
        ePackage.setNsURI("https://example.org/person/1.0.0");
        EClass person = factory.createEClass();
        person.setName("Person");
        EClass address = factory.createEClass();
        address.setName("Address");
        EClass employee = factory.createEClass();
        employee.setName("Employee");
        employee.getESuperTypes().add(person);
        ePackage.getEClassifiers().add(person);
        ePackage.getEClassifiers().add(address);
        ePackage.getEClassifiers().add(employee);
        person.getEStructuralFeatures().add(reference("address", address, true));
        person.getEStructuralFeatures().add(reference("friend", person, false));
        return ePackage;
    }

    private EReference reference(String name, EClass type, boolean containment) {
        EReference reference = EcoreFactory.eINSTANCE.createEReference();
        reference.setName(name);
        reference.setEType(type);
        reference.setContainment(containment);
        return reference;
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
