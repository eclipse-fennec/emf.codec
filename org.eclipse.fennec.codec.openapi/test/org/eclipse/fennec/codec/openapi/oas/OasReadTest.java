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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.oas.Components;
import org.eclipse.fennec.model.oas.Example;
import org.eclipse.fennec.model.oas.MediaType;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.Parameter;
import org.eclipse.fennec.model.oas.ParameterLocation;
import org.eclipse.fennec.model.oas.PathItem;
import org.eclipse.fennec.model.oas.SecurityScheme;
import org.eclipse.fennec.model.oas.SecuritySchemeType;
import org.eclipse.fennec.model.oas.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Reading OpenAPI 3.0, 3.1 and 3.2 descriptions into the oas model (issues #299, #300, #301, #313).
 */
@DisplayName("oas model: reading")
class OasReadTest {

	private static final Path DATA = Path.of("test-data/oas");

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, OasPackage.eINSTANCE);
	}

	/**
	 * The JSON/YAML pairs that describe the same API. Upstream, tictactoe.json lacks the webhooks,
	 * callbacks and links of tictactoe.yaml, and 3.2-query-example differs in a description.
	 */
	static Stream<String> pairs() {
		return Stream.of("v3.0/api-with-examples", "v3.0/callback-example", "v3.0/link-example",
				"v3.0/petstore-expanded", "v3.0/petstore", "v3.0/uspto", "v3.1/non-oauth-scopes",
				"v3.1/webhook-example", "v3.2/3.2-tags-example");
	}

	static Stream<String> allFiles() throws IOException {
		try (Stream<Path> files = Files.walk(DATA)) {
			return files.filter(p -> p.toString().endsWith(".json") || p.toString().endsWith(".yaml"))
					.map(p -> DATA.relativize(p).toString())
					.filter(p -> !p.startsWith("conformance")) // OasConformanceTest
					.sorted().toList().stream();
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("allFiles")
	@DisplayName("every example loads without errors and warnings")
	void loadsClean(String file) throws IOException {
		Resource resource = resource(file);

		assertTrue(resource.getErrors().isEmpty(), resource.getErrors().toString());
		assertTrue(resource.getWarnings().isEmpty(), resource.getWarnings().toString());
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("pairs")
	@DisplayName("YAML loads into the same model as JSON")
	void yamlEqualsJson(String name) throws IOException {
		OpenAPI json = load(name + ".json");
		OpenAPI yaml = load(name + ".yaml");

		assertTrue(EcoreUtil.equals(json, yaml), name);
	}

	@Nested
	@DisplayName("3.0 petstore")
	class Petstore {

		@Test
		@DisplayName("operations know their method, also through allOperations")
		void methods() throws IOException {
			PathItem pets = load("v3.0/petstore.json").getPaths().get("/pets");

			assertEquals("GET", pets.getGet().getMethod());
			assertEquals("POST", pets.getPost().getMethod());
			assertEquals(List.of(pets.getGet(), pets.getPost()), pets.getAllOperations());
		}

		@Test
		@DisplayName("a simple parameter schema is an Ecore data type with its facets")
		void simpleSchema() throws IOException {
			Parameter limit = load("v3.0/petstore.json").getPaths().get("/pets").getGet().getParameters().get(0);

			assertEquals(ParameterLocation.QUERY, limit.getIn());
			Schema schema = limit.getSchema();
			assertSame(EcorePackage.Literals.EINTEGER_OBJECT, schema.getClassifier());
			assertEquals("int32", schema.getFormat());
			assertEquals(new BigDecimal(100), schema.getMaximum());
			assertEquals(List.of(SimpleType.INTEGER), schema.getType());
		}

		@Test
		@DisplayName("a $ref leads to the components classifier")
		void reference() throws IOException {
			OpenAPI api = load("v3.0/petstore.yaml");
			Schema schema = api.getPaths().get("/pets").getGet().getResponses().get("200").getContent()
					.get("application/json").getSchema();

			assertEquals("#/components/schemas/Pets", schema.getRef());
			assertSame(api.getComponents().getSchemasPackage().getEClassifier("Pets"), schema.getClassifier());
			assertEquals("urn:openapi:swagger-petstore:1.0.0", api.getComponents().getSchemasPackage().getNsURI());
		}
	}

	@Nested
	@DisplayName("inline schemas")
	class Inline {

		@Test
		@DisplayName("an inline object becomes a classifier of the inline sub-package, named after its place")
		void inlineObject() throws IOException {
			OpenAPI api = load("v3.2/3.2-query-example.yaml");
			Operation search = api.getPaths().get("/flights/search").getQuery();
			Schema schema = search.getRequestBody().getContent().get("application/json").getSchema();

			EClass request = assertInstanceOf(EClass.class, schema.getClassifier());
			assertEquals("SearchFlightsRequestApplicationJson", request.getName());
			EPackage inline = request.getEPackage();
			assertEquals(OasSchemas.INLINE, inline.getName());
			assertSame(api.getComponents().getSchemasPackage(), inline.getESuperPackage());
			assertNotNull(request.getEStructuralFeature("origin"));
			assertInstanceOf(EEnum.class, request.getEStructuralFeature("cabinClass").getEType());
			assertEquals(1, request.getEStructuralFeature("origin").getLowerBound());
		}

		@Test
		@DisplayName("an inline map schema gets a catch-all map, not an empty class (#322)")
		void inlineMap() throws IOException {
			// Swagger Petstore, GET /store/inventory
			String yaml = """
					openapi: 3.1.0
					info: { title: T, version: '1' }
					paths:
					  /store/inventory:
					    get:
					      operationId: getInventory
					      responses:
					        '200':
					          description: successful operation
					          content:
					            application/json:
					              schema:
					                type: object
					                additionalProperties: { type: integer, format: int32 }
					""";
			Schema schema = parse(yaml, OasVersion.V3_1).getPaths().get("/store/inventory").getGet().getResponses()
					.get("200").getContent().get("application/json").getSchema();

			EClass inventory = assertInstanceOf(EClass.class, schema.getClassifier());
			assertEquals("GetInventory200ApplicationJson", inventory.getName());
			String feature = EcoreUtil.getAnnotation(inventory, AnnotationSources.CODEC, "additionalPropertiesFeature");
			assertNotNull(feature);
			assertTrue(inventory.getEStructuralFeature(feature).isMany());
		}

		@Test
		@DisplayName("an array of inline objects is many, its classifier the item type")
		void arrayOfObjects() throws IOException {
			String yaml = """
					openapi: 3.1.0
					info: { title: T, version: '1' }
					paths:
					  /a:
					    get:
					      operationId: listA
					      responses:
					        '200':
					          description: ok
					          content:
					            application/json:
					              schema:
					                type: array
					                maxItems: 10
					                items:
					                  type: object
					                  properties:
					                    id: { type: string }
					""";
			Schema schema = parse(yaml, OasVersion.V3_1).getPaths().get("/a").getGet().getResponses()
					.get("200").getContent().get("application/json").getSchema();

			assertEquals(List.of(SimpleType.ARRAY), schema.getType());
			assertEquals(10, schema.getMaxItems());
			EClass item = assertInstanceOf(EClass.class, schema.getClassifier());
			assertEquals("ListA200ApplicationJson", item.getName());
			assertNotNull(item.getEStructuralFeature("id"));
		}

		@Test
		@DisplayName("an inline object refers to components classifiers, not to copies")
		void refersToComponents() throws IOException {
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
					      responses: {}
					components:
					  schemas:
					    Pet:
					      type: object
					      properties:
					        name: { type: string }
					""";
			OpenAPI api = parse(yaml, OasVersion.V3_1);
			EClass request = (EClass) api.getPaths().get("/a").getPost().getRequestBody().getContent()
					.get("application/json").getSchema().getClassifier();

			assertSame(api.getComponents().getSchemasPackage().getEClassifier("Pet"),
					request.getEStructuralFeature("pet").getEType());
			assertNull(OasSchemas.INLINE.equals(api.getComponents().getSchemasPackage().getEClassifier("Pet").getEPackage().getName())
					? "Pet must not be copied into inline" : null);
			assertEquals(1, api.getComponents().getSchemasPackage().getESubpackages().get(0).getEClassifiers().size());
		}

		@Test
		@DisplayName("3.1 type arrays and 3.0 nullable are kept as written, the classifier is the type")
		void nullable() throws IOException {
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
			Schema v31 = parse(doc.formatted("3.1.0", "{ type: [string, 'null'] }"), OasVersion.V3_1)
					.getPaths().get("/a").getGet().getParameters().get(0).getSchema();
			Schema v30 = parse(doc.formatted("3.0.3", "{ type: string, nullable: true }"), OasVersion.V3_0)
					.getPaths().get("/a").getGet().getParameters().get(0).getSchema();

			assertEquals(List.of(SimpleType.STRING, SimpleType.NULL), v31.getType());
			assertEquals(Boolean.TRUE, v30.getNullable());
			assertSame(EcorePackage.Literals.ESTRING, v31.getClassifier());
			assertSame(EcorePackage.Literals.ESTRING, v30.getClassifier());
		}

		@Test
		@DisplayName("a 3.0 boolean exclusiveMinimum is kept as written")
		void exclusiveMinimum() throws IOException {
			String yaml = """
					openapi: 3.0.3
					info: { title: T, version: '1' }
					paths:
					  /a:
					    get:
					      parameters:
					        - name: q
					          in: query
					          schema: { type: number, minimum: 0, exclusiveMinimum: true }
					      responses: {}
					""";
			Schema schema = parse(yaml, OasVersion.V3_0).getPaths().get("/a").getGet().getParameters().get(0)
					.getSchema();

			assertEquals(Boolean.TRUE, schema.getExclusiveMinimumFlag());
			assertEquals(BigDecimal.ZERO, schema.getMinimum());
			assertNull(schema.getExclusiveMinimum());
		}
	}

	@Nested
	@DisplayName("3.1 and 3.2 additions")
	class Additions {

		@Test
		@DisplayName("webhooks")
		void webhooks() throws IOException {
			OpenAPI api = load("v3.1/webhook-example.yaml");

			assertEquals(1, api.getWebhooks().size());
			assertEquals("POST", api.getWebhooks().get(0).getValue().getPost().getMethod());
		}

		@Test
		@DisplayName("the QUERY method")
		void query() throws IOException {
			PathItem search = load("v3.2/3.2-query-example.json").getPaths().get("/flights/search");

			assertEquals("QUERY", search.getQuery().getMethod());
			assertEquals(List.of(search.getQuery()), search.getAllOperations());
		}

		@Test
		@DisplayName("the tag hierarchy is linked")
		void tags() throws IOException {
			List<Tag> tags = load("v3.2/3.2-tags-example.yaml").getTags();
			Tag flights = tags.get(0);
			Tag international = tags.get(1);

			assertEquals("flights", international.getParent());
			assertSame(flights, international.getParentTag());
			assertEquals("nav", international.getKind());
			assertNull(flights.getParentTag());
		}

		@Test
		@DisplayName("additionalOperations, querystring, $self, mutualTLS, extensions")
		void more() throws IOException {
			String yaml = """
					openapi: 3.2.0
					$self: https://example.org/api.yaml
					info: { title: T, version: '1', x-audience: internal }
					paths:
					  /a:
					    additionalOperations:
					      LINK:
					        operationId: linkA
					        responses: {}
					    get:
					      parameters:
					        - name: filter
					          in: querystring
					          content:
					            application/json:
					              schema: { type: object, properties: { q: { type: string } } }
					      responses: {}
					components:
					  securitySchemes:
					    mtls: { type: mutualTLS }
					""";
			OpenAPI api = parse(yaml, OasVersion.V3_2);
			PathItem a = api.getPaths().get("/a");

			assertEquals("https://example.org/api.yaml", api.getSelf());
			assertEquals("LINK", a.getAdditionalOperations().get("LINK").getMethod());
			assertEquals(List.of(a.getGet(), a.getAdditionalOperations().get("LINK")), a.getAllOperations());
			assertEquals(ParameterLocation.QUERYSTRING, a.getGet().getParameters().get(0).getIn());
			assertEquals(SecuritySchemeType.MUTUAL_TLS, api.getComponents().getSecuritySchemes().get("mtls").getType());
			assertEquals("internal", api.getInfo().getExtensions().get("x-audience"));
		}

		@Test
		@DisplayName("each 3.2 addition is in the model (#306)")
		void additions32() throws IOException {
			OpenAPI api = load("v3.2/fennec-3.2-additions.yaml");
			Components components = api.getComponents();

			assertEquals("production", api.getServers().get(0).getName());
			assertEquals("Orders", api.getTags().get(1).getSummary());
			assertSame(api.getTags().get(0), api.getTags().get(1).getParentTag());
			assertEquals("The matching orders",
					api.getPaths().get("/orders").getQuery().getResponses().get("200").getSummary());
			assertEquals("PURGE", api.getPaths().get("/orders").getAdditionalOperations().get("PURGE").getMethod());
			assertEquals(ParameterLocation.QUERYSTRING,
					api.getPaths().get("/orders/search").getGet().getParameters().get(0).getIn());

			Example example = components.getExamples().get("Order");
			assertEquals(Map.of("id", "1", "kind", "Order"), example.getDataValue());
			assertEquals("{\"id\":\"1\",\"kind\":\"Order\"}", example.getSerializedValue());
			assertNotNull(components.getMediaTypes().get("OrderJson").getSchema().getClassifier());

			SecurityScheme device = components.getSecuritySchemes().get("device");
			assertEquals(Boolean.TRUE, device.getDeprecated());
			assertEquals("https://example.com/.well-known/oauth-authorization-server", device.getOauth2MetadataUrl());
			assertEquals("https://example.com/device",
					device.getFlows().getDeviceAuthorization().getDeviceAuthorizationUrl());

			Schema order = components.getSchemas().get("Order");
			assertEquals("#/components/schemas/Order", order.getDiscriminator().getDefaultMapping());
			assertEquals("cdata", order.getProperties().get("note").getXml().getNodeType());
		}

		@Test
		@DisplayName("the item type of a streamed response is its itemSchema's classifier (#307)")
		void streamedItems() throws IOException {
			OpenAPI api = load("v3.2/fennec-3.2-additions.yaml");
			EMap<String, MediaType> content = api.getPaths().get("/orders/events").getGet().getResponses()
					.get("200").getContent();
			EPackage schemas = api.getComponents().getSchemasPackage();

			// a $ref: the component's class
			assertSame(schemas.getEClassifier("OrderEvent"), content.get("text/event-stream").getItemSchema().getClassifier());
			// inline: a class of the inline package, named after its place
			EClassifier line = content.get("application/jsonl").getItemSchema().getClassifier();
			assertTrue(line instanceof EClass);
			assertSame(schemas.getESubpackages().get(0), line.getEPackage());
			assertNotNull(((EClass) line).getEStructuralFeature("status"));
			// multipart: positional and item encodings
			MediaType multipart = content.get("multipart/mixed");
			assertEquals("application/json", multipart.getPrefixEncoding().get(0).getContentType());
			assertEquals("application/octet-stream", multipart.getItemEncoding().getContentType());
		}
	}

	@Nested
	@DisplayName("references (#313)")
	class References {

		private static final String DOC = """
				openapi: 3.1.0
				info: { title: T, version: '1' }
				paths:
				  /a:
				    get:
				      parameters:
				        - $ref: '#/components/parameters/limit'
				          description: overrides the target's
				      responses:
				        '200': { $ref: '#/components/responses/Ok' }
				        '404': { $ref: '#/components/responses/Missing' }
				        '500': { $ref: 'common.yaml#/components/responses/Error' }
				components:
				  parameters:
				    limit: { name: limit, in: query, schema: { type: integer } }
				  responses:
				    Ok: { description: ok }
				""";

		@Test
		@DisplayName("internal references are linked to their targets; the override stays on the reference")
		void linked() throws IOException {
			OpenAPI api = parse(DOC, OasVersion.V3_1);
			Operation get = api.getPaths().get("/a").getGet();

			Parameter reference = get.getParameters().get(0);
			assertSame(api.getComponents().getParameters().get("limit"), reference.getTarget());
			assertEquals("overrides the target's", reference.getDescription());
			assertSame(api.getComponents().getResponses().get("Ok"), get.getResponses().get("200").getTarget());
			assertNull(get.getResponses().get("500").getTarget(), "another document");
		}

		@Test
		@DisplayName("an internal reference without target is a warning")
		void missing() throws IOException {
			Resource resource = resource(DOC, OasVersion.V3_1);

			assertEquals(1, resource.getWarnings().size(), resource.getWarnings().toString());
			assertTrue(resource.getWarnings().get(0).getMessage().contains("#/components/responses/Missing"));
		}
	}

	@Nested
	@DisplayName("versions (#300)")
	class Versions {

		@Test
		@DisplayName("a document of another covered version is read with a warning")
		void otherVersion() throws IOException {
			Resource resource = resource("openapi: 3.0.3\ninfo: { title: T, version: '1' }\npaths: {}\n", OasVersion.V3_1);

			assertEquals(1, resource.getWarnings().size());
			assertTrue(resource.getWarnings().get(0).getMessage().contains("3.0.3"));
		}

		@Test
		@DisplayName("a version the model does not cover is refused")
		void unknownVersion() {
			assertThrows(IOException.class,
					() -> resource("openapi: 2.0\ninfo: { title: T, version: '1' }\n", OasVersion.V3_1));
			assertThrows(IOException.class,
					() -> resource("openapi: 3.3.0\ninfo: { title: T, version: '1' }\n", OasVersion.V3_2));
		}

		@Test
		@DisplayName("content types carry the version parameter")
		void contentTypes() {
			assertEquals("application/vnd.oai.openapi+json;version=3.1",
					new OasResourceFactoryImpl(OasVersion.V3_1, null).getContentType());
			assertEquals("application/vnd.oai.openapi;version=3.2",
					new OasResourceFactoryImpl(OasVersion.V3_2, new YamlFormatProvider()).getContentType());
			assertTrue(OasVersion.V3_2.matches("3.2.1"));
			assertFalse(OasVersion.V3_2.matches("3.20.0"));
		}
	}

	static OpenAPI load(String file) throws IOException {
		return (OpenAPI) resource(file).getContents().get(0);
	}

	static Resource resource(String file) throws IOException {
		OasVersion version = OasVersion.of(file.substring(1, 4));
		Resource resource = new OasResourceFactoryImpl(version, file.endsWith(".yaml") ? new YamlFormatProvider() : null)
				.createResource(URI.createURI("test://" + file));
		try (InputStream in = Files.newInputStream(DATA.resolve(file))) {
			resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		}
		return resource;
	}

	static OpenAPI parse(String yaml, OasVersion version) throws IOException {
		return (OpenAPI) resource(yaml, version).getContents().get(0);
	}

	static Resource resource(String yaml, OasVersion version) throws IOException {
		Resource resource = new OasResourceFactoryImpl(version, new YamlFormatProvider())
				.createResource(URI.createURI("test://inline.yaml"));
		resource.load(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
				Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		return resource;
	}
}
