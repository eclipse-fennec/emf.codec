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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Operation;
import org.eclipse.fennec.model.openapi.Parameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * YAML scalars and keys in OpenAPI descriptions (issue #297): YAML 1.2 core schema, as the
 * OpenAPI specification recommends; keys are always strings; written values read back as the same
 * strings.
 */
@DisplayName("OpenAPI YAML scalars")
class OpenApiYamlScalarsTest {

	private static final String DOC = """
			openapi: 3.0.3
			info:
			  title: Scalars
			  version: 1.0
			  description: |
			    First line
			    second line
			paths:
			  /switch:
			    get:
			      # a comment, dropped
			      parameters:
			        - name: state
			          in: query
			          required: true
			          schema:
			            type: string
			            enum: [on, off, yes, no, y, n, ~null]
			      responses:
			        200:
			          description: ok
			        2XX:
			          description: other success
			        default:
			          description: >
			            folded
			            text
			""";

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("read")
	class Read {

		@Test
		@DisplayName("response codes are string keys, also unquoted 200")
		void responseCodes() throws IOException {
			Operation get = load(DOC).getPaths().get("/switch").getGet();

			// the entry list keeps the document order; EMap.keySet() iterates in hash order
			assertEquals(List.of("200", "2XX", "default"),
					get.getResponses().stream().map(Map.Entry::getKey).toList());
		}

		@Test
		@DisplayName("on, off, yes, no, y, n stay strings")
		void yaml11BooleansAreStrings() throws IOException {
			Parameter state = load(DOC).getPaths().get("/switch").getGet().getParameters().get(0);

			assertEquals(List.of("on", "off", "yes", "no", "y", "n", "~null"), state.getSchema().getEnum());
		}

		@Test
		@DisplayName("a number in a string feature keeps its text: version 1.0")
		void numberAsString() throws IOException {
			assertEquals("1.0", load(DOC).getInfo().getVersion());
		}

		@Test
		@DisplayName("literal and folded block scalars")
		void blockScalars() throws IOException {
			OpenAPI api = load(DOC);

			assertEquals("First line\nsecond line\n", api.getInfo().getDescription());
			assertEquals("folded text\n", api.getPaths().get("/switch").getGet().getResponses().get("default")
					.getDescription());
		}
	}

	@Nested
	@DisplayName("YAML 1.1 booleans where the model expects a boolean")
	class Booleans {

		private static final String PARAMS = """
				openapi: 3.0.3
				info: { title: B, version: '1' }
				paths:
				  /p:
				    get:
				      parameters:
				        - { name: a, in: query, required: yes }
				        - { name: b, in: query, required: on, deprecated: off }
				        - { name: c, in: query, required: true, deprecated: no }
				      responses: {}
				""";

		@Test
		@DisplayName("yes / on read as true, no / off as false, with a warning")
		void coerced() throws IOException {
			Resource resource = factory().createResource(URI.createURI("test://openapi"));
			Map<String, Object> options = new HashMap<>();
			options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
			resource.load(new ByteArrayInputStream(PARAMS.getBytes(StandardCharsets.UTF_8)), options);
			List<Parameter> params = ((OpenAPI) resource.getContents().get(0)).getPaths().get("/p").getGet()
					.getParameters();

			assertTrue(params.get(0).isRequired());
			assertTrue(params.get(1).isRequired());
			assertFalse(params.get(1).isDeprecated());
			assertFalse(params.get(2).isDeprecated());
			assertEquals(4, resource.getWarnings().size(), resource.getWarnings().toString());
			assertTrue(resource.getErrors().isEmpty(), resource.getErrors().toString());
		}

		@Test
		@DisplayName("strictOnConversion refuses them")
		void strict() {
			Resource resource = factory().createResource(URI.createURI("test://openapi"));
			Map<String, Object> options = new HashMap<>();
			options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
			options.put(CodecOptions.CODEC_STRICT_ON_CONVERSION, Boolean.TRUE);

			assertThrows(IOException.class, () -> resource.load(
					new ByteArrayInputStream(PARAMS.getBytes(StandardCharsets.UTF_8)), options));
		}
	}

	@Nested
	@DisplayName("write")
	class Write {

		@Test
		@DisplayName("strings that look like numbers, booleans or null read back as strings")
		void ambiguousStrings() throws IOException {
			OpenAPI api = load(DOC);
			api.getInfo().setTitle("true");
			api.getInfo().setVersion("1.10");

			OpenAPI reread = load(save(api));

			assertEquals("true", reread.getInfo().getTitle());
			assertEquals("1.10", reread.getInfo().getVersion());
			assertEquals(api.getPaths().get("/switch").getGet().getParameters().get(0).getSchema().getEnum(),
					reread.getPaths().get("/switch").getGet().getParameters().get(0).getSchema().getEnum());
			assertTrue(EcoreUtil.equals(api, reread), ModelDiff.firstDifference(api, reread));
		}

		@Test
		@DisplayName("quotes only where needed")
		void minimalQuotes() throws IOException {
			String yaml = save(load(DOC));

			assertTrue(yaml.contains("title: Scalars"), yaml);
			assertTrue(yaml.contains("\"200\":") || yaml.contains("'200':"), yaml);
			assertTrue(yaml.contains("\"on\"") || yaml.contains("'on'"), yaml);
			assertTrue(yaml.contains("version: \"1.0\"") || yaml.contains("version: '1.0'"), yaml);
		}

		@Test
		@DisplayName("multi-line strings as literal blocks")
		void literalBlocks() throws IOException {
			String yaml = save(load(DOC));

			assertTrue(yaml.contains("description: |"), yaml);
			assertFalse(yaml.contains("First line\\n"), yaml);
		}
	}

	@Test
	@DisplayName("the YAML option names match the YAML provider's")
	void optionNames() {
		assertEquals(YamlFormatProvider.OPTION_MINIMIZE_QUOTES, OpenApiResourceImpl.YAML_MINIMIZE_QUOTES);
		assertEquals(YamlFormatProvider.OPTION_LITERAL_BLOCK_STYLE, OpenApiResourceImpl.YAML_LITERAL_BLOCK_STYLE);
	}

	private static OpenAPI load(String yaml) throws IOException {
		Resource resource = factory().createResource(URI.createURI("test://openapi"));
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		resource.load(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), options);
		return (OpenAPI) resource.getContents().get(0);
	}

	private static String save(OpenAPI api) throws IOException {
		Resource resource = factory().createResource(URI.createURI("test://openapi"));
		resource.getContents().add(EcoreUtil.copy(api));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());
		return out.toString(StandardCharsets.UTF_8);
	}

	private static OpenApiResourceFactoryImpl factory() {
		return new OpenApiResourceFactoryImpl(new YamlFormatProvider(),
				OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_YAML);
	}
}
