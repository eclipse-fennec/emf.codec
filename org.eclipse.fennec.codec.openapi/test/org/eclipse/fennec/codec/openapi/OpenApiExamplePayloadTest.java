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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * Payloads of real-world APIs survive load and save through the EPackage read from their
 * {@code components/schemas} (spec 21 §2.2, issues #277, #271).
 * <p>
 * The documents carry no complete payloads, but many properties have an {@code example}: a payload
 * per schema is assembled from them, following {@code $ref}s and array items. Examples that do not
 * match their own property schema (sevdesk has {@code 0} / {@code 1} for booleans and {@code null}
 * for non-nullable properties) are left out, and numbers compare by value - {@code 100} written
 * back as {@code 100.0} is formatting.
 * </p>
 */
@DisplayName("Example payloads of real-world OpenAPI documents (#277)")
class OpenApiExamplePayloadTest {

	private static final int MAX_DEPTH = 4;

	private final JsonMapper mapper = JsonMapper.builder().build();

	@TestFactory
	@DisplayName("petstore")
	Stream<DynamicTest> petstore() throws IOException {
		return payloadTests(Path.of("test-data/petstore.json"));
	}

	@TestFactory
	@DisplayName("sevdesk")
	Stream<DynamicTest> sevdesk() throws IOException {
		return payloadTests(Path.of("test-data/sevdesk.json"));
	}

	private Stream<DynamicTest> payloadTests(Path document) throws IOException {
		JsonNode raw = mapper.readTree(document.toFile());
		JsonNode schemas = raw.at("/components/schemas");
		EPackage ePackage = loadSchemasPackage(document);
		MetadataWhiteboard metadataService = MetadataServiceFactory.create();
		metadataService.registerPackage(ePackage);

		List<DynamicTest> tests = new ArrayList<>();
		for (String name : schemas.propertyNames()) {
			JsonNode payload = examplePayload(schemas, schemas.get(name), new HashSet<>(), 0);
			EClassifier classifier = classifier(ePackage, name);
			if (payload instanceof ObjectNode object && !object.isEmpty() && classifier instanceof EClass eClass
					&& !eClass.isAbstract()) {
				tests.add(DynamicTest.dynamicTest(name, () -> assertRoundTrip(metadataService, eClass, payload)));
			}
		}
		assertTrue(!tests.isEmpty(), "no payload could be assembled from " + document);
		return tests.stream();
	}

	private void assertRoundTrip(MetadataWhiteboard metadataService, EClass eClass, JsonNode payload)
			throws IOException {
		CodecResource resource = new CodecResource(URI.createURI("test://payload.json"), metadataService,
				ConfigurationResolver.defaults(), null);
		resource.load(new ByteArrayInputStream(mapper.writeValueAsBytes(payload)),
				Map.of(CodecResource.CODEC_ROOT_TYPE, eClass));
		assertTrue(resource.getErrors().isEmpty(), resource.getErrors().toString());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, null);
		assertEquals(normalized(payload), normalized(mapper.readTree(out.toString(StandardCharsets.UTF_8))));
	}

	private EPackage loadSchemasPackage(Path document) throws IOException {
		Resource resource = new OpenApiResourceFactoryImpl().createResource(URI.createURI("test://openapi.json"));
		try (InputStream in = Files.newInputStream(document)) {
			resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API));
		}
		EPackage ePackage = ((OpenAPI) resource.getContents().get(0)).getComponents().getSchemasPackage();
		ePackage.setNsURI("https://example.org/" + document.getFileName());
		return ePackage;
	}

	private static EClassifier classifier(EPackage ePackage, String schemaName) {
		for (EClassifier classifier : ePackage.getEClassifiers()) {
			String originalName = EcoreUtil.getAnnotation(classifier, AnnotationSources.JSONSCHEMA, "originalName");
			if (schemaName.equals(originalName) || schemaName.equals(classifier.getName())) {
				return classifier;
			}
		}
		return null;
	}

	/** Whether an example is valid against the type, nullability and enum of its schema. */
	private static boolean matches(JsonNode schema, JsonNode example) {
		if (example.isNull()) {
			return schema.path("nullable").asBoolean(false);
		}
		if (schema.has("enum")) {
			boolean listed = false;
			for (JsonNode value : schema.get("enum")) {
				listed |= value.equals(example);
			}
			if (!listed) {
				return false;
			}
		}
		return switch (schema.path("type").asString("")) {
			case "string" -> example.isString();
			case "integer" -> example.isIntegralNumber();
			case "number" -> example.isNumber();
			case "boolean" -> example.isBoolean();
			case "array" -> example.isArray();
			case "object" -> example.isObject();
			default -> true;
		};
	}

	/** Numbers by value, so a different literal of the same number is not a difference. */
	private JsonNode normalized(JsonNode node) {
		if (node.isNumber()) {
			return mapper.getNodeFactory().numberNode(node.decimalValue().stripTrailingZeros());
		}
		if (node instanceof ObjectNode object) {
			ObjectNode copy = mapper.createObjectNode();
			for (String name : object.propertyNames()) {
				copy.set(name, normalized(object.get(name)));
			}
			return copy;
		}
		if (node instanceof ArrayNode array) {
			ArrayNode copy = mapper.createArrayNode();
			array.forEach(element -> copy.add(normalized(element)));
			return copy;
		}
		return node;
	}

	/** A payload from the {@code example}s of a schema, or {@code null} when it has none. */
	private JsonNode examplePayload(JsonNode schemas, JsonNode schema, Set<String> visiting, int depth) {
		if (schema == null || depth > MAX_DEPTH) {
			return null;
		}
		if (schema.has("example")) {
			return matches(schema, schema.get("example")) ? schema.get("example") : null;
		}
		if (schema.has("$ref")) {
			String name = schema.get("$ref").asString().substring("#/components/schemas/".length());
			if (!visiting.add(name)) {
				return null;
			}
			JsonNode value = examplePayload(schemas, schemas.get(name), visiting, depth + 1);
			visiting.remove(name);
			return value;
		}
		if (schema.has("items")) {
			JsonNode item = examplePayload(schemas, schema.get("items"), visiting, depth + 1);
			if (item == null) {
				return null;
			}
			ArrayNode array = mapper.createArrayNode();
			array.add(item);
			return array;
		}
		JsonNode properties = schema.get("properties");
		if (properties == null) {
			return null;
		}
		ObjectNode object = mapper.createObjectNode();
		for (String property : properties.propertyNames()) {
			JsonNode value = examplePayload(schemas, properties.get(property), visiting, depth + 1);
			if (value != null) {
				object.set(property, value);
			}
		}
		return object.isEmpty() ? null : object;
	}
}
