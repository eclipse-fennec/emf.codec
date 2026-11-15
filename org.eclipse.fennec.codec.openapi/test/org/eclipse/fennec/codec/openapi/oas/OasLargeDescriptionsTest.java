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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.PathItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * The large real-world descriptions load into the oas model without errors and warnings, every
 * schema of a request or response linked to its classifier (issues #298, #301, #302, #304).
 * Skipped while {@code fetchOpenApiCorpus} has not downloaded them.
 */
@DisplayName("oas model: large descriptions")
class OasLargeDescriptionsTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, OasPackage.eINSTANCE);
	}

	@ParameterizedTest(name = "{0}")
	@CsvSource({ "openai-openapi.yaml, 3.1, OpenAI API, 200", "github-api.next.yaml, 3.1, GitHub v3 REST API, 800",
			"github-api.2022-11-28.json, 3.0, GitHub v3 REST API, 800" })
	@DisplayName("loads clean, every media type schema typed")
	void loadsClean(String file, String version, String title, int minPaths) throws IOException {
		Path path = Path.of("test-data/external").resolve(file);
		assumeTrue(Files.exists(path), "missing " + path + " - run fetchOpenApiCorpus");
		Resource resource = new OasResourceFactoryImpl(OasVersion.of(version),
				file.endsWith(".yaml") ? new YamlFormatProvider() : null).createResource(URI.createURI("test://" + file));
		try (InputStream in = Files.newInputStream(path)) {
			resource.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		}

		assertTrue(resource.getErrors().isEmpty(), resource.getErrors().toString());
		assertTrue(resource.getWarnings().isEmpty(), resource.getWarnings().toString());
		OpenAPI api = (OpenAPI) resource.getContents().get(0);
		assertEquals(title, api.getInfo().getTitle());
		assertTrue(api.getPaths().size() >= minPaths, "paths: " + api.getPaths().size());
		int typed = 0;
		for (Map.Entry<String, PathItem> path2 : api.getPaths()) {
			for (Operation operation : path2.getValue().getAllOperations()) {
				for (var response : operation.getResponses()) {
					if (response.getValue().getContent() == null) {
						continue;
					}
					for (var media : response.getValue().getContent()) {
						Schema schema = media.getValue().getSchema();
						if (schema != null) {
							assertNotNull(schema.getClassifier(), operation.getOperationId() + " " + response.getKey() + " "
									+ media.getKey() + " has no type");
							typed++;
						}
					}
				}
			}
		}
		assertTrue(typed > minPaths / 2, "typed response schemas: " + typed);
	}

	@Test
	@DisplayName("GitHub 3.0.3 written as 3.1 reads back as 3.1 with the same Ecore (#303, #304)")
	void github30As31() throws IOException {
		Path path = Path.of("test-data/external/github-api.2022-11-28.json");
		assumeTrue(Files.exists(path), "missing " + path + " - run fetchOpenApiCorpus");
		Resource v30 = new OasResourceFactoryImpl(OasVersion.V3_0, null).createResource(URI.createURI("test://v30.json"));
		try (InputStream in = Files.newInputStream(path)) {
			v30.load(in, Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		}
		OpenAPI api30 = (OpenAPI) v30.getContents().get(0);

		Resource out = new OasResourceFactoryImpl(OasVersion.V3_1, null).createResource(URI.createURI("test://v31.json"));
		out.getContents().add(api30);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		out.save(bytes, Map.of());
		assertEquals(List.of("the document is OpenAPI 3.0.3, written as OpenAPI 3.1.1"),
				out.getWarnings().stream().map(Resource.Diagnostic::getMessage).toList());
		String written = bytes.toString(StandardCharsets.UTF_8);
		assertFalse(written.contains("\"nullable\""), "nullable is rewritten into type arrays");

		Resource v31 = new OasResourceFactoryImpl(OasVersion.V3_1, null).createResource(URI.createURI("test://v31.json"));
		v31.load(new ByteArrayInputStream(bytes.toByteArray()), Map.of(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API));
		assertTrue(v31.getErrors().isEmpty(), v31.getErrors().toString());
		assertTrue(v31.getWarnings().isEmpty(), v31.getWarnings().toString());
		EPackage before = api30.getComponents().getSchemasPackage();
		EPackage after = ((OpenAPI) v31.getContents().get(0)).getComponents().getSchemasPackage();
		List<String> expected = shape(before);
		List<String> actual = shape(after);
		List<String> lost = new ArrayList<>(expected);
		actual.forEach(lost::remove);
		List<String> added = new ArrayList<>(actual);
		expected.forEach(added::remove);
		assertEquals(List.of(), lost, () -> "added: " + added);
		assertEquals(List.of(), added);
	}

	/**
	 * Each classifier with its features, their types and bounds - sorted, the numbers of artificial
	 * classifiers left out: the converter numbers them in the order it meets them.
	 */
	private static List<String> shape(EPackage ePackage) {
		List<String> shape = new ArrayList<>();
		for (EClassifier classifier : ePackage.getEClassifiers()) {
			StringBuilder line = new StringBuilder(classifier.eClass().getName() + " " + classifier.getName());
			if (classifier instanceof EClass eClass) {
				for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
					line.append(", ").append(feature.getName()).append(": ")
							.append(feature.getEType() == null ? null : feature.getEType().getName())
							.append(" [").append(feature.getLowerBound()).append("..").append(feature.getUpperBound()).append("]");
				}
			}
			shape.add(line.toString().replaceAll("ArtificialClassifier\\d+", "Artificial"));
		}
		for (EPackage sub : ePackage.getESubpackages()) {
			shape.addAll(shape(sub));
		}
		shape.sort(null);
		return shape;
	}

}
