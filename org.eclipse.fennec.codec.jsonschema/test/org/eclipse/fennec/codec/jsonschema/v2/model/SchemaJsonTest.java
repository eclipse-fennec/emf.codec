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
package org.eclipse.fennec.codec.jsonschema.v2.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** JSON Schema → model → JSON Schema gives the same JSON (issue #317). */
@DisplayName("JSON Schema model: lossless")
class SchemaJsonTest {

	private static final JsonMapper MAPPER = JsonMapper.builder()
			.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).build();

	static Stream<Path> testData() throws IOException {
		try (Stream<Path> files = Files.list(Path.of("test-data"))) {
			return files.filter(p -> p.toString().endsWith(".json")).sorted().toList().stream();
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("testData")
	@DisplayName("the test data reads and writes back the same JSON")
	void testDataLossless(Path file) throws IOException {
		assertLossless(MAPPER.readTree(file.toFile()));
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {
			"true", "false", "{}",
			"{\"type\":\"string\"}", "{\"type\":[\"string\"]}", "{\"type\":[\"string\",\"null\"]}",
			"{\"items\":{\"type\":\"integer\"}}", "{\"items\":[{\"type\":\"integer\"},true],\"additionalItems\":false}",
			"{\"prefixItems\":[{\"type\":\"integer\"}],\"items\":false}",
			"{\"minimum\":0,\"exclusiveMinimum\":true}", "{\"exclusiveMinimum\":0.5,\"exclusiveMaximum\":10}",
			"{\"dependencies\":{\"a\":[\"b\",\"c\"],\"d\":{\"required\":[\"e\"]}}}",
			"{\"dependentRequired\":{\"a\":[\"b\"]},\"dependentSchemas\":{\"c\":{\"minProperties\":1}}}",
			"{\"if\":{\"properties\":{\"k\":{\"const\":1}}},\"then\":{\"required\":[\"x\"]},\"else\":false}",
			"{\"$schema\":\"https://json-schema.org/draft/2020-12/schema\",\"$id\":\"u\",\"$anchor\":\"a\",\"$dynamicAnchor\":\"m\",\"$defs\":{\"x\":{\"$dynamicRef\":\"#m\"}}}",
			"{\"id\":\"http://x\",\"definitions\":{\"a\":{\"$ref\":\"#/definitions/a\",\"description\":\"sibling\"}}}",
			"{\"patternProperties\":{\"^x-\":{}},\"propertyNames\":{\"pattern\":\"^[a-z]+$\"},\"unevaluatedProperties\":false}",
			"{\"enum\":[1,\"a\",null,{\"k\":[1.50]}],\"const\":{\"a\":1},\"default\":[1,2],\"examples\":[{\"x\":1e3}]}",
			"{\"contentMediaType\":\"application/json\",\"contentEncoding\":\"base64\",\"contentSchema\":{\"type\":\"object\"}}",
			"{\"type\":\"object\",\"nullable\":true,\"discriminator\":{\"propertyName\":\"kind\",\"mapping\":{\"a\":\"#/A\"},\"x-y\":1},\"xml\":{\"name\":\"n\",\"wrapped\":true},\"externalDocs\":{\"url\":\"u\"},\"example\":{\"a\":1},\"x-ext\":[1]}",
			"{\"type\":\"strings\",\"minLength\":\"3\",\"items\":5,\"exclusiveMinimum\":\"x\",\"required\":[1],\"properties\":[]}",
			"{\"properties\":{},\"required\":[],\"allOf\":[],\"items\":[],\"type\":[],\"enum\":[],\"dependencies\":{},\"$defs\":{}}",
			"{\"default\":null,\"example\":null,\"const\":null,\"examples\":[0,12,7,0,0],\"enum\":[\"a\",\"a\"],\"required\":[\"x\",\"x\"]}",
			"{\"maxLength\":99999999999,\"multipleOf\":0.01,\"$vocabulary\":{\"https://x\":true},\"$comment\":\"c\"}" })
	@DisplayName("every draft form reads and writes back the same JSON, also invalid values")
	void formsLossless(String json) throws IOException {
		assertLossless(MAPPER.readTree(json));
	}

	@Test
	@DisplayName("the draft forms land in their features")
	void features() throws IOException {
		Schema schema = SchemaJson.read(MAPPER.readTree(
				"{\"type\":[\"string\",\"null\"],\"items\":[{}],\"minimum\":0,\"exclusiveMinimum\":true,\"dependencies\":{\"a\":[\"b\"]}}"));

		assertEquals(List.of(SimpleType.STRING, SimpleType.NULL), schema.getType());
		assertTrue(schema.isTypeAsList());
		assertEquals(1, schema.getItemsList().size());
		assertEquals(BigDecimal.ZERO, schema.getMinimum());
		assertEquals(Boolean.TRUE, schema.getExclusiveMinimumFlag());
		assertEquals(List.of("b"), schema.getDependencies().get("a").getProperties());
		assertTrue(schema.getKeywords().isEmpty(), schema.getKeywords().toString());
	}

	static void assertLossless(JsonNode source) {
		Schema schema = SchemaJson.read(source);
		JsonNode written = SchemaJson.write(schema);
		assertTrue(source.equals(SAME_VALUE, written), () -> "differs:\n" + source + "\n" + written);
	}

	/** Numbers by value (1 and 1.0 and 1E+0 are one number); everything else as usual. */
	static final Comparator<JsonNode> SAME_VALUE = (a, b) -> {
		if (a.isNumber() && b.isNumber()) {
			return a.decimalValue().compareTo(b.decimalValue());
		}
		return a.equals(b) ? 0 : 1;
	};
}
