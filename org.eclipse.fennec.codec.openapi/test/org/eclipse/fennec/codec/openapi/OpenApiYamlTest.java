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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * OpenAPI descriptions in YAML (issue #296): the same model and mapping as JSON, only another
 * syntax, selected by the content type {@code application/vnd.oai.openapi}.
 */
@DisplayName("OpenAPI YAML")
class OpenApiYamlTest {

	private static final Path V30 = Path.of("test-data/oas/v3.0");

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	static Stream<String> v30Examples() throws IOException {
		try (Stream<Path> files = Files.list(V30)) {
			return files.map(p -> p.getFileName().toString())
					.filter(n -> n.endsWith(".yaml"))
					.map(n -> n.substring(0, n.length() - ".yaml".length()))
					.sorted()
					.toList()
					.stream();
		}
	}

	@Nested
	@DisplayName("factory")
	class Factory {

		@Test
		@DisplayName("registers the YAML media type and no file extension")
		void registersTheYamlMediaType() {
			Map<String, Object> properties = yamlFactory().getServiceProperties();
			assertEquals("application/vnd.oai.openapi", OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_YAML);
			assertEquals(OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_YAML,
					properties.get(EMFNamespaces.EMF_MODEL_CONTENT_TYPE));
			assertFalse(properties.containsKey(EMFNamespaces.EMF_MODEL_FILE_EXT),
					"yaml/yml belong to the generic YAML factory");
			assertEquals(OpenApiResourceFactoryImpl.CONFIGURATOR_NAME,
					properties.get(EMFNamespaces.EMF_CONFIGURATOR_NAME));
		}

		@Test
		@DisplayName("the JSON factory keeps its media type and file extension")
		void jsonFactoryUnchanged() {
			Map<String, Object> properties = new OpenApiResourceFactoryImpl().getServiceProperties();
			assertEquals(OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_JSON,
					properties.get(EMFNamespaces.EMF_MODEL_CONTENT_TYPE));
			assertEquals(OpenApiResourceFactoryImpl.FILE_EXTENSION,
					properties.get(EMFNamespaces.EMF_MODEL_FILE_EXT));
		}
	}

	@Nested
	@DisplayName("OpenAPI 3.0 examples")
	class Examples30 {

		@ParameterizedTest(name = "{0}")
		@MethodSource("org.eclipse.fennec.codec.openapi.OpenApiYamlTest#v30Examples")
		@DisplayName("YAML loads into the same model as JSON")
		void yamlEqualsJson(String name) throws IOException {
			OpenAPI fromJson = load(jsonFactory(), V30.resolve(name + ".json"));
			OpenAPI fromYaml = load(yamlFactory(), V30.resolve(name + ".yaml"));

			assertEqualModels(fromJson, fromYaml, name + ": YAML and JSON differ");
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("org.eclipse.fennec.codec.openapi.OpenApiYamlTest#v30Examples")
		@DisplayName("a model written as YAML reads back equal")
		void yamlRoundTrip(String name) throws IOException {
			OpenAPI original = load(yamlFactory(), V30.resolve(name + ".yaml"));

			byte[] yaml = save(yamlFactory(), original);
			OpenAPI reread = load(yamlFactory(), new ByteArrayInputStream(yaml));

			assertEqualModels(original, reread, name + ": round trip differs");
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("org.eclipse.fennec.codec.openapi.OpenApiYamlTest#v30Examples")
		@DisplayName("a model read from JSON written as YAML reads back equal")
		void jsonToYaml(String name) throws IOException {
			OpenAPI fromJson = load(jsonFactory(), V30.resolve(name + ".json"));

			OpenAPI reread = load(yamlFactory(), new ByteArrayInputStream(save(yamlFactory(), fromJson)));

			assertEqualModels(fromJson, reread, name + ": JSON → YAML differs");
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("org.eclipse.fennec.codec.openapi.OpenApiYamlTest#v30Examples")
		@DisplayName("baseline: a model written as JSON reads back equal")
		void jsonRoundTrip(String name) throws IOException {
			OpenAPI original = load(jsonFactory(), V30.resolve(name + ".json"));

			OpenAPI reread = load(jsonFactory(), new ByteArrayInputStream(save(jsonFactory(), original)));

			assertEqualModels(original, reread, name + ": JSON round trip differs");
		}
	}

	@Nested
	@DisplayName("syntax")
	class Syntax {

		@Test
		@DisplayName("the written document is YAML, not JSON")
		void writesYaml() throws IOException {
			OpenAPI api = load(yamlFactory(), V30.resolve("petstore.yaml"));

			String yaml = new String(save(yamlFactory(), api), StandardCharsets.UTF_8);

			assertFalse(yaml.trim().startsWith("{"), yaml);
			assertTrue(yaml.contains("openapi: "), yaml);
		}

		@Test
		@DisplayName("a YAML document is refused by the JSON resource")
		void jsonResourceRefusesYaml() {
			assertThrows(IOException.class, () -> load(jsonFactory(), V30.resolve("petstore.yaml")));
		}

		@Test
		@DisplayName("the payload limit codec.maxPayloadSize applies to YAML")
		void payloadLimit() {
			Map<String, Object> options = loadOptions();
			options.put(CodecOptions.CODEC_MAX_PAYLOAD_SIZE, 100L);

			assertThrows(IOException.class, () -> {
				Resource resource = yamlFactory().createResource(URI.createURI("test://openapi"));
				try (InputStream in = Files.newInputStream(V30.resolve("petstore.yaml"))) {
					resource.load(in, options);
				}
			});
		}

		@Test
		@DisplayName("a document larger than the old SnakeYAML limit of 3 MB loads with the defaults")
		void largeDocument() throws IOException {
			StringBuilder yaml = new StringBuilder("""
					openapi: 3.0.3
					info:
					  title: Large
					  version: 1.0.0
					paths:
					""");
			String description = "x".repeat(1000);
			for (int i = 0; yaml.length() < 4 * 1024 * 1024; i++) {
				yaml.append("  /p").append(i).append(":\n    description: ").append(description).append('\n');
			}

			OpenAPI api = load(yamlFactory(), new ByteArrayInputStream(yaml.toString().getBytes(StandardCharsets.UTF_8)));

			assertEquals("Large", api.getInfo().getTitle());
			assertTrue(api.getPaths().size() > 4000);
		}
	}

	@Nested
	@DisplayName("without a format provider")
	class Defaults {

		@Test
		@DisplayName("the default factory has no format provider, i.e. JSON")
		void defaultIsJson() {
			OpenApiResourceFactoryImpl factory = new OpenApiResourceFactoryImpl();
			assertNull(factory.getFormatProvider());
			assertInstanceOf(OpenApiResourceImpl.class, factory.createResource(URI.createURI("test://openapi")));
		}
	}

	private static void assertEqualModels(EObject expected, EObject actual, String message) {
		if (!EcoreUtil.equals(expected, actual)) {
			fail(message + " at " + ModelDiff.firstDifference(expected, actual));
		}
	}

	private static OpenApiResourceFactoryImpl jsonFactory() {
		return new OpenApiResourceFactoryImpl();
	}

	private static OpenApiResourceFactoryImpl yamlFactory() {
		return new OpenApiResourceFactoryImpl(new YamlFormatProvider(),
				OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_YAML);
	}

	private static OpenAPI load(Resource.Factory factory, Path file) throws IOException {
		try (InputStream in = Files.newInputStream(file)) {
			return load(factory, in);
		}
	}

	private static OpenAPI load(Resource.Factory factory, InputStream in) throws IOException {
		Resource resource = factory.createResource(URI.createURI("test://openapi"));
		resource.load(in, loadOptions());
		List<EObject> contents = resource.getContents();
		assertEquals(1, contents.size());
		return (OpenAPI) contents.get(0);
	}

	private static byte[] save(Resource.Factory factory, OpenAPI api) throws IOException {
		Resource resource = factory.createResource(URI.createURI("test://openapi"));
		resource.getContents().add(EcoreUtil.copy(api));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());
		return out.toByteArray();
	}

	private static Map<String, Object> loadOptions() {
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		return options;
	}
}
