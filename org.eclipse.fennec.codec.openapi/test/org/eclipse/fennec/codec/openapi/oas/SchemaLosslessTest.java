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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.fennec.codec.jsonschema.v2.model.SchemaJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.snakeyaml.engine.v2.api.LoadSettings;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;

/**
 * Every Schema Object of the OpenAPI test descriptions - {@code components/schemas} and every
 * {@code schema} / {@code itemSchema} - reads into the JSON Schema model and writes back to the same
 * JSON (issue #317). Includes the large descriptions when {@code fetchOpenApiCorpus} has run.
 */
@DisplayName("JSON Schema model: OpenAPI schemas lossless")
class SchemaLosslessTest {

	private static final JsonMapper JSON = JsonMapper.builder()
			.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).build();
	private static final YAMLMapper YAML = new YAMLMapper(new YAMLFactory().rebuild()
			.loadSettings(LoadSettings.builder().setCodePointLimit(50_000_000).build()).build()).rebuild()
			.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).build();
	/** Numbers by value. */
	private static final Comparator<JsonNode> SAME = (a, b) -> a.isNumber() && b.isNumber()
			? a.decimalValue().compareTo(b.decimalValue()) : (a.equals(b) ? 0 : 1);

	static Stream<Path> descriptions() throws IOException {
		try (Stream<Path> files = Files.walk(Path.of("test-data"))) {
			return files.filter(p -> p.toString().endsWith(".json") || p.toString().endsWith(".yaml")).sorted()
					.toList().stream();
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("descriptions")
	@DisplayName("every schema reads and writes back the same JSON")
	void lossless(Path file) throws IOException {
		// blank lines emptied as the codec does (snakeyaml-engine block scalar bug, #298)
		JsonNode document = file.toString().endsWith(".yaml")
				? YAML.readTree(Files.readString(file).replaceAll("(?m)^[ \\t]+$", ""))
				: JSON.readTree(file.toFile());
		List<JsonNode> schemas = new ArrayList<>();
		collect(document, "", schemas);
		for (JsonNode schema : schemas) {
			JsonNode written = SchemaJson.write(SchemaJson.read(schema));
			assertTrue(schema.equals(SAME, written), () -> file + ": " + where(schema, written, ""));
		}
	}

	private static void collect(JsonNode node, String path, List<JsonNode> out) {
		if (node.isObject()) {
			for (Map.Entry<String, JsonNode> member : node.properties()) {
				String key = member.getKey();
				JsonNode value = member.getValue();
				if ("/components".equals(path) && "schemas".equals(key)) {
					value.properties().forEach(schema -> out.add(schema.getValue()));
				} else if (("schema".equals(key) || "itemSchema".equals(key)) && (value.isObject() || value.isBoolean())) {
					out.add(value);
				} else {
					collect(value, path + "/" + key, out);
				}
			}
		} else if (node.isArray()) {
			node.forEach(item -> collect(item, path, out));
		}
	}

	private static String where(JsonNode a, JsonNode b, String path) {
		if (a.isObject() && b.isObject()) {
			for (Map.Entry<String, JsonNode> member : a.properties()) {
				if (!b.has(member.getKey())) {
					return "lost " + path + "/" + member.getKey();
				}
				if (!member.getValue().equals(SAME, b.get(member.getKey()))) {
					return where(member.getValue(), b.get(member.getKey()), path + "/" + member.getKey());
				}
			}
			for (Map.Entry<String, JsonNode> member : b.properties()) {
				if (!a.has(member.getKey())) {
					return "added " + path + "/" + member.getKey();
				}
			}
		}
		return "changed " + path + ": " + a + " -> " + b;
	}
}
