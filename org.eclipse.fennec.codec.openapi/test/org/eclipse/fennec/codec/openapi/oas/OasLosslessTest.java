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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.oas.OasPackage;
import org.junit.jupiter.api.BeforeEach;
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
 * A description read into the oas model and written back gives the same JSON tree - nothing of the
 * OpenAPI document nor of its schemas is lost (issues #299, #319) - and no field of the
 * specification lands in the {@code extensions} of an object (it would be one the model lacks).
 * Includes the large descriptions when {@code fetchOpenApiCorpus} has run.
 */
@DisplayName("oas model: lossless")
class OasLosslessTest {

	private static final JsonMapper JSON = JsonMapper.builder()
			.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).build();
	private static final YAMLMapper YAML = new YAMLMapper(new YAMLFactory().rebuild()
			.loadSettings(LoadSettings.builder().setCodePointLimit(50_000_000).build()).build()).rebuild()
			.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).build();
	/** Numbers by value. */
	private static final Comparator<JsonNode> SAME = (a, b) -> a.isNumber() && b.isNumber()
			? a.decimalValue().compareTo(b.decimalValue()) : (a.equals(b) ? 0 : 1);

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, OasPackage.eINSTANCE);
	}

	static Stream<String[]> descriptions() throws IOException {
		Stream.Builder<String[]> all = Stream.builder();
		try (Stream<Path> files = Files.walk(Path.of("test-data/oas"))) {
			files.filter(p -> p.toString().endsWith(".json") || p.toString().endsWith(".yaml"))
					.filter(p -> !p.toString().contains("/fail/")).sorted()
					.forEach(p -> all.add(new String[] { p.toString(), version(p) }));
		}
		all.add(new String[] { "test-data/external/github-api.2022-11-28.json", "3.0" });
		all.add(new String[] { "test-data/external/github-api.next.yaml", "3.1" });
		all.add(new String[] { "test-data/external/openai-openapi.yaml", "3.1" });
		return all.build();
	}

	/** The version of the directory {@code v3.x} the file lies in. */
	static String version(Path path) {
		for (Path segment : path) {
			if (segment.toString().matches("v3\\.\\d")) {
				return segment.toString().substring(1);
			}
		}
		throw new IllegalArgumentException("no version directory: " + path);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("descriptions")
	@DisplayName("written back as read")
	void lossless(String file, String version) throws IOException {
		Path path = Path.of(file);
		assumeTrue(Files.exists(path), "missing " + path + " - run fetchOpenApiCorpus");
		boolean yaml = file.endsWith(".yaml");
		Resource in = new OasResourceFactoryImpl(OasVersion.of(version), yaml ? new YamlFormatProvider() : null)
				.createResource(URI.createURI("test://in"));
		try (InputStream stream = Files.newInputStream(path)) {
			in.load(stream, Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		}
		assertNoUnknownFields(in.getContents().get(0));

		Resource out = new OasResourceFactoryImpl(OasVersion.of(version), null).createResource(URI.createURI("test://out"));
		out.getContents().addAll(in.getContents());
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		out.save(bytes, Map.of());
		assertTrue(out.getWarnings().isEmpty(), () -> file + ": " + out.getWarnings()); // in its own version (#303)

		// blank lines emptied as the codec does (snakeyaml-engine block scalar bug, #298)
		JsonNode source = yaml ? YAML.readTree(Files.readString(path).replaceAll("(?m)^[ \\t]+$", ""))
				: JSON.readTree(path.toFile());
		JsonNode written = JSON.readTree(bytes.toByteArray());
		assertTrue(source.equals(SAME, written), () -> file + ": " + where(source, written, ""));
	}

	@SuppressWarnings("unchecked")
	private static void assertNoUnknownFields(EObject root) {
		for (TreeIterator<EObject> it = root.eAllContents(); it.hasNext();) {
			EObject object = it.next();
			if (object instanceof EPackage || object instanceof Schema) {
				it.prune(); // the derived Ecore; schemas keep unknown keywords as JSON Schema allows
				continue;
			}
			EStructuralFeature extensions = object.eClass().getEStructuralFeature("extensions");
			if (extensions != null) {
				for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>) object.eGet(extensions)) {
					assertTrue(entry.getKey().startsWith("x-"),
							object.eClass().getName() + "." + entry.getKey() + " is a field the model does not know");
				}
			}
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
		if (a.isArray() && b.isArray() && a.size() == b.size()) {
			for (int i = 0; i < a.size(); i++) {
				if (!a.get(i).equals(SAME, b.get(i))) {
					return where(a.get(i), b.get(i), path + "[" + i + "]");
				}
			}
		}
		return "changed " + path + ": " + a + " -> " + b;
	}
}
