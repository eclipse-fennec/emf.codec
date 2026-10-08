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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.model.openapi.Info;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Nullability of optional attributes in {@code components/schemas} (issue #270).
 * <p>
 * The OpenAPI 3.0 Schema Object allows a single string as {@code type} and has no {@code null}
 * type; nullability is {@code nullable: true}. OpenAPI 3.1 uses JSON Schema 2020-12, where the
 * type array {@code ["string", "null"]} is right. The writer follows the {@code openapi} version
 * of the document the schemas are written into.
 * </p>
 */
@DisplayName("components/schemas nullability (#270)")
class OpenApiSchemaNullableTest {

    @Test
    @DisplayName("OpenAPI 3.0: optional attributes are a single type with nullable: true")
    void openApi30WritesNullableKeyword() throws IOException {
        JsonNode person = save(createDocument("3.0.3")).at("/components/schemas/Person/properties");

        assertEquals("string", person.at("/id/type").asString(), person.toString());
        assertNull(person.at("/id").get("nullable"), "a required attribute is not nullable");
        assertEquals("string", person.at("/firstName/type").asString(), person.toString());
        assertTrue(person.at("/firstName/nullable").asBoolean(), person.toString());
        assertEquals("string", person.at("/birthDate/type").asString(), person.toString());
        assertTrue(person.at("/birthDate/nullable").asBoolean(), person.toString());
        assertEquals("integer", person.at("/age/type").asString(), person.toString());
        assertTrue(person.at("/age/nullable").asBoolean(), person.toString());
    }

    @Test
    @DisplayName("OpenAPI 3.0: an annotated type list without null becomes anyOf, with null nullable")
    void openApi30RewritesAnnotatedTypeLists() throws IOException {
        JsonNode code = save(createDocument("3.0.3")).at("/components/schemas/Person/properties/code");

        assertFalse(code.path("type").isArray(), code.toString());
        assertEquals("string", code.at("/anyOf/0/type").asString(), code.toString());
        assertEquals("integer", code.at("/anyOf/1/type").asString(), code.toString());
        assertTrue(code.path("nullable").asBoolean(), code.toString());
    }

    @Test
    @DisplayName("OpenAPI 3.1: optional attributes keep the JSON Schema type array")
    void openApi31KeepsTypeArray() throws IOException {
        JsonNode firstName = save(createDocument("3.1.0")).at("/components/schemas/Person/properties/firstName");

        assertTrue(firstName.path("type").isArray(), firstName.toString());
        assertEquals("[\"string\",\"null\"]", firstName.path("type").toString());
        assertNull(firstName.get("nullable"));
    }

    @Test
    @DisplayName("an explicit nullableKeyword option wins over the document version")
    void explicitOptionWins() throws IOException {
        JsonNode firstName = save(createDocument("3.0.3"),
                Map.of(CodecJsonSchemaOptions.OPTION_NULLABLE_KEYWORD, Boolean.FALSE))
                .at("/components/schemas/Person/properties/firstName");

        assertTrue(firstName.path("type").isArray(), firstName.toString());
    }

    private OpenAPI createDocument(String openApiVersion) {
        OpenAPI openApi = OpenApiFactory.eINSTANCE.createOpenAPI();
        openApi.setOpenapi(openApiVersion);
        Info info = OpenApiFactory.eINSTANCE.createInfo();
        info.setTitle("Nullable");
        info.setVersion("1.0.0");
        openApi.setInfo(info);
        openApi.setComponents(OpenApiFactory.eINSTANCE.createComponents());
        openApi.getComponents().setSchemasPackage(createPersonPackage());
        return openApi;
    }

    private EPackage createPersonPackage() {
        EcoreFactory factory = EcoreFactory.eINSTANCE;
        EPackage ePackage = factory.createEPackage();
        ePackage.setName("person");
        ePackage.setNsPrefix("person");
        ePackage.setNsURI("https://example.org/person/1.0.0");
        EClass person = factory.createEClass();
        person.setName("Person");
        ePackage.getEClassifiers().add(person);
        EAttribute id = attribute(person, "id", EcorePackage.Literals.ESTRING, 1);
        id.setID(true);
        attribute(person, "firstName", EcorePackage.Literals.ESTRING, 0);
        attribute(person, "birthDate", EcorePackage.Literals.EDATE, 0);
        attribute(person, "age", EcorePackage.Literals.EINTEGER_OBJECT, 0);
        EAttribute code = attribute(person, "code", EcorePackage.Literals.EJAVA_OBJECT, 0);
        code.getEAnnotations().add(dataTypeAnnotation("string,integer,null"));
        return ePackage;
    }

    private EAttribute attribute(EClass owner, String name, EDataType type, int lowerBound) {
        EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
        attribute.setName(name);
        attribute.setEType(type);
        attribute.setLowerBound(lowerBound);
        owner.getEStructuralFeatures().add(attribute);
        return attribute;
    }

    private EAnnotation dataTypeAnnotation(String dataType) {
        EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
        annotation.setSource(AnnotationSources.JSONSCHEMA);
        annotation.getDetails().put("dataType", dataType);
        return annotation;
    }

    private JsonNode save(OpenAPI openApi) throws IOException {
        return save(openApi, Map.of());
    }

    private JsonNode save(OpenAPI openApi, Map<String, Object> options) throws IOException {
        Resource resource = new OpenApiResourceFactoryImpl().createResource(URI.createURI("test://openapi.json"));
        resource.getContents().add(openApi);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return JsonMapper.builder().build().readTree(out.toString(StandardCharsets.UTF_8));
    }
}
