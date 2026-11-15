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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.openapi.ModelDiff;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * read → write → read is stable for every example, in its version and syntax (issues #299, #301).
 */
@DisplayName("oas model: round trip")
class OasRoundTripTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, OasPackage.eINSTANCE);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("org.eclipse.fennec.codec.openapi.oas.OasReadTest#allFiles")
	@DisplayName("a written description reads back equal")
	void roundTrip(String file) throws IOException {
		OasVersion version = OasVersion.of(file.substring(1, 4));
		boolean yaml = file.endsWith(".yaml");
		OpenAPI original = OasReadTest.load(file);

		String written = save(original, version, yaml);
		Resource reread = load(written, version, yaml);

		assertTrue(reread.getWarnings().isEmpty(), reread.getWarnings() + "\n" + written);
		OpenAPI copy = (OpenAPI) reread.getContents().get(0);
		if (!EcoreUtil.equals(original, copy)) {
			fail(file + " differs at " + ModelDiff.firstDifference(original, copy) + "\n" + written);
		}
	}

	@Test
	@DisplayName("3.0 writes nullable and a boolean exclusiveMinimum, 3.1 a type array and a number")
	void versionForms() throws IOException {
		String doc = """
				openapi: %s
				info: { title: T, version: '1' }
				paths:
				  /a:
				    get:
				      parameters:
				        - name: q
				          in: query
				          schema: %s
				      responses: {}
				""";
		String v30 = save(OasReadTest.parse(doc.formatted("3.0.3",
				"{ type: number, nullable: true, minimum: 0, exclusiveMinimum: true }"), OasVersion.V3_0), OasVersion.V3_0, false);
		String v31 = save(OasReadTest.parse(doc.formatted("3.1.0",
				"{ type: [number, 'null'], exclusiveMinimum: 0 }"), OasVersion.V3_1), OasVersion.V3_1, false);

		assertTrue(v30.contains("\"nullable\":true"), v30);
		assertTrue(v30.contains("\"exclusiveMinimum\":true"), v30);
		assertTrue(v31.contains("\"type\":[\"number\",\"null\"]"), v31);
		assertTrue(v31.contains("\"exclusiveMinimum\":0"), v31);
		assertFalse(v31.contains("nullable"), v31);
	}

	@Test
	@DisplayName("an inline object is written inline, its component references as $ref")
	void inlineWritten() throws IOException {
		String yaml = """
				openapi: 3.1.0
				info: { title: T, version: '1' }
				paths:
				  /a:
				    post:
				      operationId: createA
				      requestBody:
				        content:
				          application/json:
				            schema:
				              type: object
				              properties:
				                pet: { $ref: '#/components/schemas/Pet' }
				                note: { type: string }
				      responses: {}
				components:
				  schemas:
				    Pet:
				      type: object
				      properties:
				        name: { type: string }
				""";
		String json = save(OasReadTest.parse(yaml, OasVersion.V3_1), OasVersion.V3_1, false);

		assertTrue(json.contains("\"#/components/schemas/Pet\""), json);
		assertFalse(json.contains("CreateARequestApplicationJson"), "the inline name is not written: " + json);
		assertEquals(1, json.split("\"Pet\"").length - 1, "Pet is a component once: " + json);
	}

	static String save(OpenAPI api, OasVersion version, boolean yaml) throws IOException {
		Resource resource = new OasResourceFactoryImpl(version, yaml ? new YamlFormatProvider() : null)
				.createResource(URI.createURI("test://out"));
		resource.getContents().add(EcoreUtil.copy(api));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());
		return out.toString(StandardCharsets.UTF_8);
	}

	static Resource load(String text, OasVersion version, boolean yaml) throws IOException {
		Resource resource = new OasResourceFactoryImpl(version, yaml ? new YamlFormatProvider() : null)
				.createResource(URI.createURI("test://in"));
		resource.load(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)),
				Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		return resource;
	}
}
