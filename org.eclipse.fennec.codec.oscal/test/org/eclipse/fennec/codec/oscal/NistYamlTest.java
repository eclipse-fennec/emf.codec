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
package org.eclipse.fennec.codec.oscal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import gov.nist.csrc.ns.oscal.DocumentRoot;
import tools.jackson.databind.JsonNode;

/**
 * OSCAL YAML: the NIST examples come as JSON and as YAML with the same content (usnistgov/oscal-content,
 * CC0, {@code test-data/nist-yaml}). The YAML file is loaded and checked with the oracle against the
 * JSON file, written as YAML, loaded and checked again, and written as JSON, which has to equal the
 * JSON file.
 */
@DisplayName("NIST OSCAL examples in YAML")
class NistYamlTest {

	private static final CodecFormatProvider<?, ?> YAML = new YamlFormatProvider();

	static Stream<Path> files() throws IOException {
		try (Stream<Path> paths = Files.list(Path.of("test-data/nist-yaml"))) {
			return paths.filter(p -> p.toString().endsWith(".yaml")).sorted().toList().stream();
		}
	}

	private static Resource load(byte[] bytes, CodecFormatProvider<?, ?> format) throws IOException {
		OscalResourceImpl resource = new OscalResourceImpl(URI.createURI("test://document"),
				RoundTrip.metadataService(), null, format);
		resource.load(new ByteArrayInputStream(bytes), Map.of());
		assertEquals(List.of(), RoundTrip.messages(resource.getErrors()), "errors");
		assertEquals(List.of(), RoundTrip.messages(resource.getWarnings()), "warnings");
		return resource;
	}

	private static byte[] save(Resource loaded, CodecFormatProvider<?, ?> format) throws IOException {
		OscalResourceImpl resource = new OscalResourceImpl(URI.createURI("test://out"), RoundTrip.metadataService(),
				null, format);
		resource.getContents().addAll(loaded.getContents());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());
		loaded.getContents().addAll(resource.getContents());
		return out.toByteArray();
	}

	private static void assertMatches(JsonNode json, Resource resource) {
		ModelJsonOracle.Result oracle = ModelJsonOracle.check(json, (DocumentRoot) resource.getContents().get(0));
		assertEquals(List.of(), oracle.findings(), "model vs. JSON");
		assertEquals(RoundTrip.scalarCount(json), oracle.checkedValues(), "not every value was compared");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("files")
	void yaml(Path yamlFile) throws IOException {
		String name = yamlFile.getFileName().toString();
		JsonNode json = RoundTrip.JSON.readTree(Path.of("test-data/nist", name.replace(".yaml", ".json")).toFile());

		Resource fromYaml = load(Files.readAllBytes(yamlFile), YAML);
		assertMatches(json, fromYaml);

		byte[] yaml = save(fromYaml, YAML);
		Resource reloaded = load(yaml, YAML);
		assertMatches(json, reloaded);
		assertArrayEquals(yaml, save(reloaded, YAML), "second YAML round trip is not stable");

		assertEquals(List.of(), JsonDiff.diff(json, RoundTrip.JSON.readTree(save(fromYaml, null))), "YAML -> JSON");
	}
}
