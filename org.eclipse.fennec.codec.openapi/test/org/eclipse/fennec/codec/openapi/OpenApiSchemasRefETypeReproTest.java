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

import org.eclipse.fennec.codec.openapi.internal.OpenApiResourceFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Regression test for <a href="https://github.com/eclipse-fennec/emf.codec/issues/43">#43</a>:
 * schema-to-schema {@code $ref} properties in the generated schemas EPackage ended up as
 * EReferences with {@code eType == null}, because
 * {@code JsonSchemaToEPackageConverter.extractSchemaNameFromRef} did not reduce OpenAPI-style
 * pointers ({@code #/components/schemas/Name}) to the classifier-map key when the converter
 * runs in embedded/direct-definitions mode ({@code schemaFeature == null}).
 */
@DisplayName("Generated schemas package: $ref features must be typed (#43)")
class OpenApiSchemasRefETypeReproTest {

	@Test
	@DisplayName("schema-to-schema $ref resolves to the sibling EClass (single + array items)")
	void refFeaturesAreTyped() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": { "title": "Refs", "version": "1.0" },
				"paths": {},
				"components": { "schemas": {
					"Pet": { "type": "object", "properties": {
						"name": { "type": "string" },
						"category": { "$ref": "#/components/schemas/Category" },
						"tags": { "type": "array", "items": { "$ref": "#/components/schemas/Tag" } } } },
					"Category": { "type": "object", "properties": { "label": { "type": "string" } } },
					"Tag": { "type": "object", "properties": { "label": { "type": "string" } } }
				} }
			}
			""";

		OpenApiResourceFactoryImpl factory = new OpenApiResourceFactoryImpl();
		CodecResource resource = (CodecResource) factory.createResource(URI.createURI("test://refs.json"));
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
		EPackage generated = openApi.getComponents().getSchemasPackage();
		assertNotNull(generated);
		EClass pet = (EClass) generated.getEClassifier("Pet");
		assertNotNull(pet);

		// currently both eTypes are null — the ref is only kept as an annotation
		assertSame(generated.getEClassifier("Category"),
				pet.getEStructuralFeature("category").getEType(),
				"$ref feature must be typed with the referenced EClass");
		assertSame(generated.getEClassifier("Tag"),
				pet.getEStructuralFeature("tags").getEType(),
				"array-items $ref feature must be typed with the referenced EClass");
	}
}
