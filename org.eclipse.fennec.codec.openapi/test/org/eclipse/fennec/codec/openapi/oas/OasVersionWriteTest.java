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
package org.eclipse.fennec.codec.openapi.oas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Writing follows the version of the resource (issue #303). */
@DisplayName("oas model: writing in the version of the resource")
class OasVersionWriteTest {

	private static final JsonMapper JSON = JsonMapper.builder().build();

	private static final String V32 = """
			openapi: 3.2.0
			$self: https://example.org/api.yaml
			info: { title: T, version: '1', summary: S }
			tags:
			  - name: a
			  - name: b
			    parent: a
			paths:
			  /x:
			    get:
			      parameters:
			        - name: q
			          in: querystring
			          content:
			            application/json:
			              schema: { type: object }
			      responses: {}
			webhooks:
			  ping:
			    post:
			      responses: {}
			""";

	private static final String V30_SCHEMAS = """
			openapi: 3.0.3
			info: { title: T, version: '1' }
			paths: {}
			components:
			  schemas:
			    Price:
			      type: number
			      nullable: true
			      minimum: 0
			      exclusiveMinimum: true
			    Untyped:
			      nullable: true
			""";

	private static final String V31_SCHEMAS = """
			openapi: 3.1.0
			info: { title: T, version: '1' }
			paths: {}
			components:
			  schemas:
			    Name:
			      type: [string, 'null']
			      const: fixed
			      examples: [fixed, other]
			    Price:
			      type: number
			      exclusiveMinimum: 0
			    Pair:
			      type: array
			      prefixItems: [{ type: string }, { type: integer }]
			    Many:
			      type: [string, integer]
			""";

	private static final String V32_SCHEMAS = """
			openapi: 3.2.0
			info: { title: T, version: '1' }
			paths: {}
			components:
			  schemas:
			    Pet:
			      type: object
			      discriminator: { propertyName: kind, defaultMapping: '#/components/schemas/Pet' }
			      properties:
			        kind: { type: string }
			""";

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, OasPackage.eINSTANCE);
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(OasVersion.class)
	@DisplayName("a field or value the version does not know is reported")
	void unknownFields(OasVersion target) throws IOException {
		List<String> warnings = save(OasReadTest.parse(V32, OasVersion.V3_2), target).warnings();

		boolean v30 = target == OasVersion.V3_0;
		boolean before32 = target != OasVersion.V3_2;
		assertEquals(v30, warnings.contains("OpenAPI.webhooks is not part of OpenAPI 3.0"), warnings::toString);
		assertEquals(v30, warnings.contains("Info.summary is not part of OpenAPI 3.0"), warnings::toString);
		assertEquals(before32, warnings.contains("OpenAPI.$self is not part of OpenAPI " + target.version()), warnings::toString);
		assertEquals(before32, warnings.contains("Tag.parent is not part of OpenAPI " + target.version()), warnings::toString);
		assertEquals(before32, warnings.contains("Parameter.in 'querystring' is not part of OpenAPI " + target.version()),
				warnings::toString);
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(OasVersion.class)
	@DisplayName("an openapi member of another version is reported and written as the resource's version")
	void openapiMember(OasVersion target) throws IOException {
		Saved saved = save(OasReadTest.parse(V32, OasVersion.V3_2), target);

		String expected = Map.of(OasVersion.V3_0, "3.0.4", OasVersion.V3_1, "3.1.1", OasVersion.V3_2, "3.2.0").get(target);
		assertEquals(expected, saved.json().get("openapi").asString());
		assertEquals(target != OasVersion.V3_2,
				saved.warnings().contains("the document is OpenAPI 3.2.0, written as OpenAPI " + expected));
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(value = OasVersion.class, names = { "V3_1", "V3_2" })
	@DisplayName("3.0 schemas are rewritten for 2020-12: nullable, boolean exclusiveMinimum")
	void schemasFrom30(OasVersion target) throws IOException {
		Saved saved = save(OasReadTest.parse(V30_SCHEMAS, OasVersion.V3_0), target);
		JsonNode schemas = saved.json().at("/components/schemas");

		assertEquals(JSON.readTree("{\"type\": [\"number\", \"null\"], \"exclusiveMinimum\": 0}"), schemas.get("Price"));
		assertEquals(JSON.readTree("{}"), schemas.get("Untyped"), "nullable without type has no effect");
		assertEquals(List.of("the document is OpenAPI 3.0.3, written as OpenAPI " + (target == OasVersion.V3_1 ? "3.1.1" : "3.2.0")),
				saved.warnings());
	}

	@Test
	@DisplayName("3.1 schemas are rewritten for 3.0; what 3.0 cannot express is reported and kept")
	void schemasTo30() throws IOException {
		Saved saved = save(OasReadTest.parse(V31_SCHEMAS, OasVersion.V3_1), OasVersion.V3_0);
		JsonNode schemas = saved.json().at("/components/schemas");

		assertEquals(JSON.readTree("{\"type\": \"string\", \"nullable\": true, \"enum\": [\"fixed\"], \"example\": \"fixed\"}"),
				schemas.get("Name"));
		assertEquals(JSON.readTree("{\"type\": \"number\", \"minimum\": 0, \"exclusiveMinimum\": true}"), schemas.get("Price"));
		assertTrue(schemas.get("Pair").has("prefixItems"));
		List<String> warnings = saved.warnings();
		assertTrue(warnings.contains("of several 'examples' OpenAPI 3.0 keeps the first as 'example'"), warnings::toString);
		assertTrue(warnings.contains("'prefixItems' is not a keyword of the OpenAPI 3.0 Schema Object"), warnings::toString);
		assertTrue(warnings.contains("'type' with several types has no form in OpenAPI 3.0"), warnings::toString);
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(OasVersion.class)
	@DisplayName("a 3.2 schema keyword is reported below 3.2")
	void schemasFrom32(OasVersion target) throws IOException {
		List<String> warnings = save(OasReadTest.parse(V32_SCHEMAS, OasVersion.V3_2), target).warnings();
		assertEquals(target != OasVersion.V3_2,
				warnings.contains("'defaultMapping' of Discriminator is not part of OpenAPI " + target.version()),
				warnings::toString);
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(OasVersion.class)
	@DisplayName("the model stays as it was")
	void modelUnchanged(OasVersion target) throws IOException {
		for (String yaml : List.of(V32, V30_SCHEMAS, V31_SCHEMAS, V32_SCHEMAS)) {
			OpenAPI api = OasReadTest.parse(yaml, OasVersion.of(yaml.substring(9, 12)));
			EObject before = EcoreUtil.copy(api);
			save(api, target);
			assertTrue(EcoreUtil.equals(before, api), yaml);
		}
	}

	@Test
	@DisplayName("STRICT fails the save")
	void strict() {
		IOException failure = assertThrows(IOException.class, () -> save(OasReadTest.parse(V32, OasVersion.V3_2),
				OasVersion.V3_1, Map.of(CodecOptions.CODEC_DESERIALIZATION_MODE, "STRICT")));
		assertTrue(failure.getMessage().contains("Tag.parent"), failure::getMessage);
	}

	private record Saved(JsonNode json, List<String> warnings) {
	}

	private static Saved save(OpenAPI api, OasVersion target) throws IOException {
		return save(api, target, Map.of());
	}

	private static Saved save(OpenAPI api, OasVersion target, Map<String, Object> options) throws IOException {
		Resource source = api.eResource();
		Resource out = new OasResourceFactoryImpl(target, null).createResource(URI.createURI("test://out.json"));
		out.getContents().add(api);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			out.save(bytes, options);
		} finally {
			source.getContents().add(api);
		}
		return new Saved(JSON.readTree(bytes.toByteArray()),
				out.getWarnings().stream().map(Resource.Diagnostic::getMessage).toList());
	}
}
