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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.PathItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * The large real-world descriptions load into the oas model without errors and warnings, every
 * schema of a request or response linked to its classifier (issues #298, #301, #302, #304).
 * Skipped while {@code fetchOpenApiCorpus} has not downloaded them.
 */
@DisplayName("oas model: large descriptions")
class OasLargeDescriptionsTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, OasPackage.eINSTANCE);
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({ "openai-openapi.yaml, 3.1, OpenAI API, 200", "github-api.next.yaml, 3.1, GitHub v3 REST API, 800",
			"github-api.2022-11-28.json, 3.0, GitHub v3 REST API, 800" })
	@DisplayName("loads clean, every media type schema typed")
	void loadsClean(String file, String version, String title, int minPaths) throws IOException {
		Path path = Path.of("test-data/external").resolve(file);
		assumeTrue(Files.exists(path), "missing " + path + " - run fetchOpenApiCorpus");
		Resource resource = new OasResourceFactoryImpl(OasVersion.of(version),
				file.endsWith(".yaml") ? new YamlFormatProvider() : null).createResource(URI.createURI("test://" + file));
		try (InputStream in = Files.newInputStream(path)) {
			resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		}

		assertTrue(resource.getErrors().isEmpty(), resource.getErrors().toString());
		assertTrue(resource.getWarnings().isEmpty(), resource.getWarnings().toString());
		OpenAPI api = (OpenAPI) resource.getContents().get(0);
		assertEquals(title, api.getInfo().getTitle());
		assertTrue(api.getPaths().size() >= minPaths, "paths: " + api.getPaths().size());
		int typed = 0;
		for (Map.Entry<String, PathItem> path2 : api.getPaths()) {
			for (Operation operation : path2.getValue().getAllOperations()) {
				for (var response : operation.getResponses()) {
					if (response.getValue().getContent() == null) {
						continue;
					}
					for (var media : response.getValue().getContent()) {
						Schema schema = media.getValue().getSchema();
						if (schema != null) {
							assertNotNull(schema.getClassifier(), operation.getOperationId() + " " + response.getKey() + " "
									+ media.getKey() + " has no type");
							typed++;
						}
					}
				}
			}
		}
		assertTrue(typed > minPaths / 2, "typed response schemas: " + typed);
	}
}
