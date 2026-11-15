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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.openapi.constants.CodecOpenApiOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.openapi.MediaType;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Operation;
import org.eclipse.fennec.model.openapi.Parameter;
import org.eclipse.fennec.model.openapi.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * The package built from {@code components/schemas} has an identity (issue #311), and internal
 * {@code $ref}s lead to their targets (issue #312).
 */
@DisplayName("OpenAPI schemas package and references")
class OpenApiReferencesTest {

	private static final Path PETSTORE = Path.of("test-data/oas/v3.0/petstore.json");

	private static final String DOC = """
			openapi: 3.0.3
			info:
			  title: Pet Shop API
			  version: 2.1.0
			paths:
			  /pets:
			    get:
			      parameters:
			        - $ref: '#/components/parameters/limit'
			        - name: filter
			          in: query
			          schema:
			            type: array
			            items:
			              $ref: '#/components/schemas/petFilter'
			      responses:
			        '200':
			          $ref: '#/components/responses/PetList'
			        '404':
			          description: missing
			          content:
			            application/json:
			              schema:
			                $ref: '#/components/schemas/Missing'
			        '500':
			          description: elsewhere
			          content:
			            application/json:
			              schema:
			                $ref: 'common.yaml#/components/schemas/Error'
			components:
			  parameters:
			    limit:
			      name: limit
			      in: query
			      schema: { type: integer }
			  responses:
			    PetList:
			      description: pets
			      content:
			        application/json:
			          schema:
			            $ref: '#/components/schemas/Pet'
			  schemas:
			    Pet:
			      type: object
			      properties:
			        name: { type: string }
			    petFilter:
			      type: object
			      properties:
			        tag: { type: string }
			""";

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Nested
	@DisplayName("schemas package identity (#311)")
	class Identity {

		@Test
		@DisplayName("nsURI, name and prefix are derived from info.title and info.version")
		void derived() throws IOException {
			EPackage pkg = load(DOC, Map.of()).getComponents().getSchemasPackage();

			assertEquals("urn:openapi:pet-shop-api:2.1.0", pkg.getNsURI());
			assertEquals("petShopApi", pkg.getName());
			assertEquals("petShopApi", pkg.getNsPrefix());
		}

		@Test
		@DisplayName("the same API loaded twice, as JSON and as YAML, gets the same nsURI")
		void deterministic() throws IOException {
			OpenAPI fromJson;
			try (InputStream in = Files.newInputStream(PETSTORE)) {
				Resource resource = new OpenApiResourceFactoryImpl().createResource(URI.createURI("file:/a/petstore.json"));
				resource.load(in, rootOption());
				fromJson = (OpenAPI) resource.getContents().get(0);
			}
			OpenAPI fromYaml;
			try (InputStream in = Files.newInputStream(Path.of("test-data/oas/v3.0/petstore.yaml"))) {
				Resource resource = yamlFactory().createResource(URI.createURI("https://example.org/petstore.yaml"));
				resource.load(in, rootOption());
				fromYaml = (OpenAPI) resource.getContents().get(0);
			}

			assertEquals("urn:openapi:swagger-petstore:1.0.0", fromJson.getComponents().getSchemasPackage().getNsURI());
			assertEquals(fromJson.getComponents().getSchemasPackage().getNsURI(),
					fromYaml.getComponents().getSchemasPackage().getNsURI());
		}

		@Test
		@DisplayName("the load option codec.openapi.schemasNsURI wins")
		void option() throws IOException {
			EPackage pkg = load(DOC, Map.of(CodecOpenApiOptions.OPTION_SCHEMAS_NS_URI, "https://example.org/pets/2"))
					.getComponents().getSchemasPackage();

			assertEquals("https://example.org/pets/2", pkg.getNsURI());
			assertEquals("petShopApi", pkg.getName());
		}

		@ParameterizedTest
		@CsvSource(delimiter = '|', value = {
				"Swagger Petstore - OpenAPI 3.0 | 1.0.27 | urn:openapi:swagger-petstore-openapi-3.0:1.0.27 | swaggerPetstoreOpenapi30",
				"  ÄPI über alles!  | v1 | urn:openapi:api-uber-alles:v1 | apiUberAlles",
				"123 Go | 1 | urn:openapi:123-go:1 | _123Go",
				"'' | '' | urn:openapi:openapi | openapi" })
		@DisplayName("title and version are reduced to URI- and identifier-safe parts")
		void derivation(String title, String version, String nsURI, String name) {
			assertEquals(nsURI, SchemasPackageIdentity.nsURI(title, version));
			assertEquals(name, SchemasPackageIdentity.name(title));
		}
	}

	@Nested
	@DisplayName("references (#312)")
	class References {

		@Test
		@DisplayName("a schema $ref into components/schemas leads to its classifier, also nested in items")
		void schemaClassifier() throws IOException {
			OpenAPI api = load(DOC, Map.of());
			EPackage pkg = api.getComponents().getSchemasPackage();
			Operation get = api.getPaths().get("/pets").getGet();

			Schema items = get.getParameters().get(1).getSchema().getItems();
			assertSame(pkg.getEClassifier("PetFilter"), items.getClassifier(), "key petFilter, class PetFilter");

			Schema listSchema = api.getComponents().getResponses().get("PetList").getContent()
					.get("application/json").getSchema();
			assertSame(pkg.getEClassifier("Pet"), listSchema.getClassifier());
		}

		@Test
		@DisplayName("the petstore response schemas lead to Pets and Error")
		void petstore() throws IOException {
			OpenAPI api;
			try (InputStream in = Files.newInputStream(PETSTORE)) {
				Resource resource = new OpenApiResourceFactoryImpl().createResource(URI.createURI("test://petstore"));
				resource.load(in, rootOption());
				api = (OpenAPI) resource.getContents().get(0);
			}
			EPackage pkg = api.getComponents().getSchemasPackage();
			Operation list = api.getPaths().get("/pets").getGet();

			MediaType ok = list.getResponses().get("200").getContent().get("application/json");
			assertSame(pkg.getEClassifier("Pets"), ok.getSchema().getClassifier());
			MediaType error = list.getResponses().get("default").getContent().get("application/json");
			assertSame(pkg.getEClassifier("Error"), error.getSchema().getClassifier());
		}

		@Test
		@DisplayName("references to other component sections resolve through OpenApiReferences")
		void componentReferences() throws IOException {
			OpenAPI api = load(DOC, Map.of());
			Operation get = api.getPaths().get("/pets").getGet();

			Parameter limit = get.getParameters().get(0);
			assertSame(api.getComponents().getParameters().get("limit"), OpenApiReferences.resolve(api, limit.getRef()));
			assertSame(api.getComponents().getResponses().get("PetList"),
					OpenApiReferences.resolve(api, get.getResponses().get("200").getRef()));
			assertSame(api.getComponents().getSchemasPackage().getEClassifier("Pet"),
					OpenApiReferences.resolve(api, "#/components/schemas/Pet"));
		}

		@Test
		@DisplayName("external references stay strings, without a diagnostic")
		void external() throws IOException {
			Resource resource = resource(DOC, Map.of());
			OpenAPI api = (OpenAPI) resource.getContents().get(0);
			Schema schema = api.getPaths().get("/pets").getGet().getResponses().get("500").getContent()
					.get("application/json").getSchema();

			assertNull(schema.getClassifier());
			assertNull(OpenApiReferences.resolve(api, schema.getRef()));
			assertFalse(resource.getWarnings().stream().anyMatch(d -> d.getMessage().contains("common.yaml")),
					resource.getWarnings().toString());
		}

		@Test
		@DisplayName("an internal reference without target is a warning")
		void unresolved() throws IOException {
			Resource resource = resource(DOC, Map.of());

			assertTrue(resource.getWarnings().stream()
					.anyMatch(d -> d.getMessage().contains("#/components/schemas/Missing")), resource.getWarnings().toString());
		}

		@Test
		@DisplayName("the link is not written; the $ref is")
		void notWritten() throws IOException {
			OpenAPI api = load(DOC, Map.of());
			Resource out = new OpenApiResourceFactoryImpl().createResource(URI.createURI("test://out"));
			out.getContents().add(api);
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			out.save(bytes, Map.of());
			String json = bytes.toString(StandardCharsets.UTF_8);

			assertFalse(json.contains("classifier"), json);
			assertTrue(json.contains("#/components/schemas/petFilter"), json);
		}

		@ParameterizedTest
		@CsvSource(delimiter = '|', value = {
				"#/components/schemas/Pet | components,schemas,Pet",
				"#/components/schemas/a~1b~0c | components,schemas,a/b~c",
				"#/components/schemas/with%20space | components,schemas,with space",
				"#/paths/~1pets~1{id} | paths,/pets/{id}" })
		@DisplayName("JSON pointers are unescaped")
		void pointer(String ref, String segments) {
			assertEquals(List.of(segments.split(",")), OpenApiReferences.pointer(ref));
		}

		@Test
		@DisplayName("a pointer into another document is no internal pointer")
		void externalPointer() {
			assertNull(OpenApiReferences.pointer("other.yaml#/components/schemas/X"));
			assertNull(OpenApiReferences.pointer(null));
			assertNotNull(OpenApiReferences.pointer("#/components"));
		}
	}

	private static OpenAPI load(String yaml, Map<String, Object> extra) throws IOException {
		return (OpenAPI) resource(yaml, extra).getContents().get(0);
	}

	private static Resource resource(String yaml, Map<String, Object> extra) throws IOException {
		Resource resource = yamlFactory().createResource(URI.createURI("test://openapi"));
		Map<String, Object> options = new HashMap<>(rootOption());
		options.putAll(extra);
		resource.load(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), options);
		return resource;
	}

	private static Map<String, Object> rootOption() {
		return Map.of(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
	}

	private static OpenApiResourceFactoryImpl yamlFactory() {
		return new OpenApiResourceFactoryImpl(new YamlFormatProvider(),
				OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_YAML);
	}
}
