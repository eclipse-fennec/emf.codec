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
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Large real-world descriptions load with the default options (issue #298): the GitHub REST API
 * (14 MB JSON, 11 MB YAML) and the OpenAI API (4.3 MB YAML) - all above SnakeYAML's former 3 MB
 * limit.
 * <p>
 * The files are not checked in: {@code ./gradlew :org.eclipse.fennec.codec.openapi:fetchOpenApiCorpus}
 * downloads them, pinned to a commit, into {@code test-data/external}. Without them the tests are
 * skipped. The 3.1 descriptions are read with the 3.0 model for now - this checks the syntax and
 * the limits; their content is checked with the 3.1 model (#302, #304). The OpenAI description needs the
 * blank-line workaround of {@code BlankLineReader} (snakeyaml-engine block scalar bug).
 * </p>
 */
@DisplayName("Large OpenAPI descriptions")
class OpenApiLargeDescriptionsTest {

	private static final Path EXTERNAL = Path.of("test-data/external");

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Test
	@DisplayName("GitHub REST API, OpenAPI 3.0.3, 14 MB JSON")
	void github30() throws IOException {
		OpenAPI api = load(new OpenApiResourceFactoryImpl(), "github-api.2022-11-28.json", true);

		assertEquals("3.0.3", api.getOpenapi());
		assertEquals("GitHub v3 REST API", api.getInfo().getTitle());
		assertTrue(api.getPaths().size() > 500, "paths: " + api.getPaths().size());
		EPackage schemas = api.getComponents().getSchemasPackage();
		assertNotNull(schemas);
		assertTrue(schemas.getEClassifiers().size() > 500, "schemas: " + schemas.getEClassifiers().size());
		assertEquals("urn:openapi:github-v3-rest-api:1.1.4", schemas.getNsURI());
	}

	@Test
	@DisplayName("GitHub REST API, OpenAPI 3.1.0, 11 MB YAML")
	void github31Yaml() throws IOException {
		OpenAPI api = load(yamlFactory(), "github-api.next.yaml", false);

		assertEquals("GitHub v3 REST API", api.getInfo().getTitle());
		assertTrue(api.getPaths().size() > 500, "paths: " + api.getPaths().size());
	}

	@Test
	@DisplayName("OpenAI API, OpenAPI 3.1.0, 4.3 MB YAML")
	void openAiYaml() throws IOException {
		OpenAPI api = load(yamlFactory(), "openai-openapi.yaml", false);

		assertEquals("OpenAI API", api.getInfo().getTitle());
		assertTrue(api.getPaths().size() > 50, "paths: " + api.getPaths().size());
	}

	/**
	 * @param noErrors whether the load must be free of errors; not yet for the 3.1 descriptions,
	 *        whose JSON Schema 2020-12 schemas the converter does not handle yet (#302)
	 */
	private static OpenAPI load(Resource.Factory factory, String name, boolean noErrors) throws IOException {
		Path file = EXTERNAL.resolve(name);
		assumeTrue(Files.exists(file), "missing " + file + " - run fetchOpenApiCorpus");
		Resource resource = factory.createResource(URI.createURI("test://" + name));
		try (InputStream in = Files.newInputStream(file)) {
			resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API));
		}
		if (noErrors) {
			assertTrue(resource.getErrors().isEmpty(), () -> name + ": " + resource.getErrors().subList(0,
					Math.min(5, resource.getErrors().size())));
		}
		return (OpenAPI) resource.getContents().get(0);
	}

	private static OpenApiResourceFactoryImpl yamlFactory() {
		return new OpenApiResourceFactoryImpl(new YamlFormatProvider(),
				OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_YAML);
	}
}
