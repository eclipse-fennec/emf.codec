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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The official {@code pass} documents of the specification (OAI/OpenAPI-Specification,
 * {@code tests/schema/pass}, branches {@code v3.1-dev} and {@code v3.2-dev}) load without errors
 * or warnings (issues #304, #308) - except for a {@code $ref} into {@code components} the document
 * does not have: most of them are fragments that reference components they leave out. That they
 * are written back as read is checked by {@link OasLosslessTest}.
 */
@DisplayName("oas model: official pass documents")
class OasConformanceTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, OasPackage.eINSTANCE);
	}

	static Stream<String> passDocuments() throws IOException {
		try (Stream<Path> files = Files.walk(Path.of("test-data/oas/conformance"))) {
			return files.map(OasLosslessTest::slashed).filter(f -> f.contains("/pass/") && f.endsWith(".yaml"))
					.sorted().toList().stream();
		}
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("passDocuments")
	@DisplayName("loads without errors or warnings")
	void loads(String file) throws IOException {
		Path path = Path.of(file);
		Resource resource = new OasResourceFactoryImpl(OasVersion.of(OasLosslessTest.version(path)),
				new YamlFormatProvider()).createResource(URI.createURI("test://pass.yaml"));
		try (InputStream stream = Files.newInputStream(path)) {
			resource.load(stream, Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		}
		assertEquals(List.of(), resource.getErrors(), file);
		List<String> warnings = resource.getWarnings().stream().map(Resource.Diagnostic::getMessage)
				.filter(message -> !message.matches("\\$ref '#/components/[^']*' has no target in this document"))
				.toList();
		assertEquals(List.of(), warnings, file);
		assertTrue(resource.getContents().get(0) instanceof OpenAPI, file);
	}
}
