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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.openapi.constants.CodecOpenApiOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.oas.OasPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** Optional validation on load, tested with the official fail and pass documents (issue #305). */
@DisplayName("oas model: validation")
class OasValidationTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, OasPackage.eINSTANCE);
	}

	static Stream<String> failDocuments() throws IOException {
		return files("/fail/");
	}

	/** The pass documents and the examples: valid descriptions. */
	static Stream<String> validDocuments() throws IOException {
		return Stream.concat(files("/pass/"), files("/v3.").filter(f -> !f.contains("/conformance/")));
	}

	private static Stream<String> files(String part) throws IOException {
		try (Stream<Path> files = Files.walk(Path.of("test-data/oas"))) {
			return files.map(Path::toString).filter(f -> f.contains(part) && (f.endsWith(".yaml") || f.endsWith(".json")))
					.sorted().toList().stream();
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("failDocuments")
	@DisplayName("each fail document is reported")
	void failReported(String file) {
		Resource resource = resource(file);
		try {
			load(resource, file, Map.of(CodecOpenApiOptions.OPTION_VALIDATE, true));
		} catch (IOException e) {
			return; // not even readable
		}
		assertFalse(resource.getErrors().isEmpty(), () -> file + " is not reported; warnings: " + resource.getWarnings());
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("validDocuments")
	@DisplayName("a valid description has no finding")
	void validClean(String file) throws IOException {
		Resource resource = resource(file);
		load(resource, file, Map.of(CodecOpenApiOptions.OPTION_VALIDATE, true));
		assertEquals(List.of(), resource.getErrors().stream().map(Resource.Diagnostic::getMessage).toList(), file);
	}

	@Test
	@DisplayName("without the option a fail document is read as far as it goes")
	void withoutOption() throws IOException {
		String file = "test-data/oas/conformance/v3.2/fail/example-examples.yaml";
		Resource resource = resource(file);
		load(resource, file, Map.of());
		assertTrue(resource.getErrors().isEmpty(), resource.getErrors()::toString);
	}

	@Test
	@DisplayName("a finding names the place and the rule")
	void message() throws IOException {
		String file = "test-data/oas/conformance/v3.2/fail/example-examples.yaml";
		Resource resource = resource(file);
		load(resource, file, Map.of(CodecOpenApiOptions.OPTION_VALIDATE, true));
		assertEquals(List.of("/components/parameters/animal: 'example' and 'examples' exclude each other"),
				resource.getErrors().stream().map(Resource.Diagnostic::getMessage).toList());
	}

	@Test
	@DisplayName("STRICT fails the load")
	void strict() {
		String file = "test-data/oas/conformance/v3.2/fail/example-examples.yaml";
		Map<String, Object> options = new HashMap<>();
		options.put(CodecOpenApiOptions.OPTION_VALIDATE, true);
		options.put(CodecOptions.CODEC_DESERIALIZATION_MODE, "STRICT");
		IOException failure = assertThrows(IOException.class, () -> load(resource(file), file, options));
		assertTrue(failure.getMessage().contains("exclude each other"), failure::getMessage);
	}

	private static Resource resource(String file) {
		return new OasResourceFactoryImpl(OasVersion.of(OasLosslessTest.version(Path.of(file))),
				file.endsWith(".yaml") ? new YamlFormatProvider() : null).createResource(URI.createURI("test://" + file));
	}

	private static void load(Resource resource, String file, Map<String, Object> options) throws IOException {
		Map<String, Object> all = new HashMap<>(options);
		all.put(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API);
		try (InputStream in = Files.newInputStream(Path.of(file))) {
			resource.load(in, all);
		}
	}
}
