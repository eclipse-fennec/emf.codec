/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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

import org.eclipse.fennec.codec.openapi.internal.OpenApiResourceFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Detailed roundtrip analysis for OpenAPI files.
 * Compares original JSON with roundtripped JSON to measure preservation.
 */
@DisplayName("OpenAPI Roundtrip Analysis")
class OpenApiRoundtripAnalysisTest {

	private static final Path BIKE_JSON_PATH = Path.of("../docs/example/bike-openapi/bike.json");
	private static final Path PETSTORE_JSON_PATH = Path.of("test-data/petstore.json");
	private static final Path SEVDESK_JSON_PATH = Path.of("test-data/sevdesk.json");
	private static final Path KUBERNETES_JSON_PATH = Path.of("test-data/kubernetes-api.json");

	private ObjectMapper mapper;

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
		mapper = JsonMapper.builder().build();
	}

	@Test
	@DisplayName("Analyze petstore.json roundtrip")
	@EnabledIf("petstoreJsonExists")
	void analyzePetstore() throws IOException {
		analyzeRoundtrip("petstore.json", PETSTORE_JSON_PATH);
	}

	@Test
	@DisplayName("Analyze bike.json roundtrip")
	@EnabledIf("bikeJsonExists")
	void analyzeBike() throws IOException {
		analyzeRoundtrip("bike.json", BIKE_JSON_PATH);
	}

	@Test
	@DisplayName("Analyze sevdesk.json roundtrip")
	@EnabledIf("sevdeskJsonExists")
	void analyzeSevdesk() throws IOException {
		analyzeRoundtrip("sevdesk.json", SEVDESK_JSON_PATH);
	}

	@Test
	@DisplayName("Analyze kubernetes-api.json roundtrip")
	@EnabledIf("kubernetesJsonExists")
	void analyzeKubernetes() throws IOException {
		analyzeRoundtrip("kubernetes-api.json", KUBERNETES_JSON_PATH);
	}

	private void analyzeRoundtrip(String name, Path filePath) throws IOException {
		System.out.println("\n========================================");
		System.out.println("ROUNDTRIP ANALYSIS: " + name);
		System.out.println("========================================");

		String originalJson = Files.readString(filePath);
		long fileSize = Files.size(filePath);

		// Parse original JSON
		JsonNode originalNode = mapper.readTree(originalJson);

		// Load via codec
		OpenApiResourceImpl resource = createResource();
		resource.load(new ByteArrayInputStream(originalJson.getBytes(StandardCharsets.UTF_8)), loadOptions());

		if (!resource.getErrors().isEmpty()) {
			System.out.println("LOAD ERRORS: " + resource.getErrors());
		}

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);

		// Get schema count before roundtrip
		int schemasBeforeRoundtrip = 0;
		if (openApi.getComponents() != null && openApi.getComponents().getSchemasPackage() != null) {
			schemasBeforeRoundtrip = openApi.getComponents().getSchemasPackage().getEClassifiers().size();
		}

		// Save to JSON
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, null);
		String savedJson = out.toString(StandardCharsets.UTF_8);

		// Parse saved JSON
		JsonNode savedNode = mapper.readTree(savedJson);

		// Analyze differences
		System.out.println("\n--- File Info ---");
		System.out.println("File size: " + (fileSize / 1024) + " KB");
		System.out.println("OpenAPI version: " + openApi.getOpenapi());

		// Count top-level keys
		System.out.println("\n--- Top-level Keys ---");
		Set<String> originalKeys = new HashSet<>();
		Set<String> savedKeys = new HashSet<>();
		for (String key : originalNode.propertyNames()) {
			originalKeys.add(key);
		}
		for (String key : savedNode.propertyNames()) {
			savedKeys.add(key);
		}

		System.out.println("Original: " + originalKeys);
		System.out.println("Saved:    " + savedKeys);

		Set<String> missingKeys = new HashSet<>(originalKeys);
		missingKeys.removeAll(savedKeys);
		if (!missingKeys.isEmpty()) {
			System.out.println("MISSING:  " + missingKeys);
		}

		Set<String> extraKeys = new HashSet<>(savedKeys);
		extraKeys.removeAll(originalKeys);
		if (!extraKeys.isEmpty()) {
			System.out.println("EXTRA:    " + extraKeys);
		}

		// Analyze paths
		analyzePaths(originalNode, savedNode);

		// Analyze components/schemas
		analyzeSchemas(originalNode, savedNode, schemasBeforeRoundtrip);

		// Analyze info section
		analyzeSection("info", originalNode, savedNode);

		// Analyze servers section
		analyzeSection("servers", originalNode, savedNode);

		// Analyze tags section
		analyzeSection("tags", originalNode, savedNode);

		// Analyze security section
		analyzeSection("security", originalNode, savedNode);

		// Calculate overall preservation
		calculatePreservation(originalNode, savedNode);
	}

	private void analyzePaths(JsonNode original, JsonNode saved) {
		System.out.println("\n--- Paths Analysis ---");

		JsonNode originalPaths = original.get("paths");
		JsonNode savedPaths = saved.get("paths");

		if (originalPaths == null) {
			System.out.println("No paths in original");
			return;
		}

		int originalPathCount = originalPaths.size();
		int savedPathCount = savedPaths != null ? savedPaths.size() : 0;

		System.out.println("Path count: " + originalPathCount + " -> " + savedPathCount);

		if (originalPathCount != savedPathCount) {
			Set<String> originalPathKeys = new HashSet<>();
			Set<String> savedPathKeys = new HashSet<>();
			for (String key : originalPaths.propertyNames()) {
				originalPathKeys.add(key);
			}
			if (savedPaths != null) {
				for (String key : savedPaths.propertyNames()) {
					savedPathKeys.add(key);
				}
			}

			Set<String> missingPaths = new HashSet<>(originalPathKeys);
			missingPaths.removeAll(savedPathKeys);
			if (!missingPaths.isEmpty() && missingPaths.size() <= 10) {
				System.out.println("Missing paths: " + missingPaths);
			} else if (!missingPaths.isEmpty()) {
				System.out.println("Missing paths: " + missingPaths.size() + " paths");
			}
		}

		// Count operations
		int originalOps = countOperations(originalPaths);
		int savedOps = countOperations(savedPaths);
		System.out.println("Operation count: " + originalOps + " -> " + savedOps);
	}

	private int countOperations(JsonNode paths) {
		if (paths == null) return 0;
		int count = 0;
		String[] methods = {"get", "post", "put", "delete", "patch", "options", "head", "trace"};
		for (String pathKey : paths.propertyNames()) {
			JsonNode pathItem = paths.get(pathKey);
			for (String method : methods) {
				if (pathItem.has(method)) {
					count++;
				}
			}
		}
		return count;
	}

	private void analyzeSchemas(JsonNode original, JsonNode saved, int schemasFromEPackage) {
		System.out.println("\n--- Schemas Analysis ---");

		JsonNode originalSchemas = getNestedNode(original, "components", "schemas");
		JsonNode savedSchemas = getNestedNode(saved, "components", "schemas");

		int originalCount = originalSchemas != null ? originalSchemas.size() : 0;
		int savedCount = savedSchemas != null ? savedSchemas.size() : 0;

		System.out.println("Schema count (JSON):     " + originalCount + " -> " + savedCount);
		System.out.println("Schema count (EPackage): " + schemasFromEPackage);

		if (originalCount > 0) {
			double preservation = (double) savedCount / originalCount * 100;
			System.out.println("Schema preservation:     " + String.format("%.1f%%", preservation));
		}

		// Check for missing schemas
		if (originalSchemas != null && savedSchemas != null) {
			Set<String> originalSchemaNames = new HashSet<>();
			Set<String> savedSchemaNames = new HashSet<>();
			for (String key : originalSchemas.propertyNames()) {
				originalSchemaNames.add(key);
			}
			for (String key : savedSchemas.propertyNames()) {
				savedSchemaNames.add(key);
			}

			Set<String> missingSchemas = new HashSet<>(originalSchemaNames);
			missingSchemas.removeAll(savedSchemaNames);

			if (!missingSchemas.isEmpty()) {
				if (missingSchemas.size() <= 10) {
					System.out.println("Missing schemas: " + missingSchemas);
				} else {
					System.out.println("Missing schemas: " + missingSchemas.size() + " schemas");
				}
			}
		}
	}

	private void analyzeSection(String sectionName, JsonNode original, JsonNode saved) {
		JsonNode originalSection = original.get(sectionName);
		JsonNode savedSection = saved.get(sectionName);

		if (originalSection == null && savedSection == null) {
			return; // Neither has this section
		}

		System.out.println("\n--- " + sectionName + " Analysis ---");

		if (originalSection == null) {
			System.out.println("Not in original, but present in saved");
			return;
		}

		if (savedSection == null) {
			System.out.println("Present in original, but MISSING in saved");
			return;
		}

		if (originalSection.isArray()) {
			System.out.println("Count: " + originalSection.size() + " -> " + savedSection.size());
		} else if (originalSection.isObject()) {
			int originalFields = countFields((ObjectNode) originalSection);
			int savedFields = countFields((ObjectNode) savedSection);
			System.out.println("Fields: " + originalFields + " -> " + savedFields);
		}
	}

	private int countFields(ObjectNode node) {
		int count = 0;
		for (@SuppressWarnings("unused") String key : node.propertyNames()) {
			count++;
		}
		return count;
	}

	private JsonNode getNestedNode(JsonNode root, String... path) {
		JsonNode current = root;
		for (String key : path) {
			if (current == null) return null;
			current = current.get(key);
		}
		return current;
	}

	private void calculatePreservation(JsonNode original, JsonNode saved) {
		System.out.println("\n--- Overall Preservation ---");

		// Count total fields recursively
		int originalFieldCount = countTotalFields(original);
		int savedFieldCount = countTotalFields(saved);

		System.out.println("Total fields: " + originalFieldCount + " -> " + savedFieldCount);

		if (originalFieldCount > 0) {
			double preservation = (double) Math.min(savedFieldCount, originalFieldCount) / originalFieldCount * 100;
			System.out.println("Field preservation: " + String.format("%.1f%%", preservation));
		}
	}

	private int countTotalFields(JsonNode node) {
		if (node == null) return 0;
		int count = 0;
		if (node.isObject()) {
			for (String key : node.propertyNames()) {
				count++; // Count this field
				count += countTotalFields(node.get(key)); // Count nested fields
			}
		} else if (node.isArray()) {
			for (int i = 0; i < node.size(); i++) {
				count += countTotalFields(node.get(i));
			}
		}
		return count;
	}

	// ========================================================================
	// Helper Methods
	// ========================================================================

	private OpenApiResourceImpl createResource() {
		OpenApiResourceFactoryImpl factory = new OpenApiResourceFactoryImpl();
		return (OpenApiResourceImpl) factory.createResource(URI.createURI("test://openapi.json"));
	}

	private Map<String, Object> loadOptions() {
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		return options;
	}

	// Condition methods for @EnabledIf
	static boolean bikeJsonExists() {
		return Files.exists(BIKE_JSON_PATH);
	}

	static boolean petstoreJsonExists() {
		return Files.exists(PETSTORE_JSON_PATH);
	}

	static boolean sevdeskJsonExists() {
		return Files.exists(SEVDESK_JSON_PATH);
	}

	static boolean kubernetesJsonExists() {
		return Files.exists(KUBERNETES_JSON_PATH);
	}
}
