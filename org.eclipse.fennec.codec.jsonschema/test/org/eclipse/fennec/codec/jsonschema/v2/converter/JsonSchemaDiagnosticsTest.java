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
package org.eclipse.fennec.codec.jsonschema.v2.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jsonschema.v2.JsonSchemaResourceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for JSON Schema conversion diagnostics.
 * <p>
 * These tests verify that unsupported and partially supported JSON Schema
 * features are properly reported through the diagnostics mechanism.
 * </p>
 */
@DisplayName("JSON Schema Diagnostics Tests")
class JsonSchemaDiagnosticsTest {

	// ========================================================================
	// JsonSchemaKeywords Tests
	// ========================================================================

	@Nested
	@DisplayName("JsonSchemaKeywords")
	class KeywordsTests {

		@Test
		@DisplayName("classifies fully supported keywords correctly")
		void classifiesFullySupportedKeywords() {
			// Core
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("$id"));
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("$ref"));
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("definitions"));

			// Type
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("type"));
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("enum"));

			// Object
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("properties"));
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("required"));

			// Composition
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("allOf"));
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("oneOf"));
		}

		@Test
		@DisplayName("classifies unsupported keywords correctly")
		void classifiesUnsupportedKeywords() {
			assertEquals(JsonSchemaKeywords.SupportLevel.NONE,
				JsonSchemaKeywords.getSupportLevel("not"));
			assertEquals(JsonSchemaKeywords.SupportLevel.NONE,
				JsonSchemaKeywords.getSupportLevel("if"));
			assertEquals(JsonSchemaKeywords.SupportLevel.NONE,
				JsonSchemaKeywords.getSupportLevel("then"));
			assertEquals(JsonSchemaKeywords.SupportLevel.NONE,
				JsonSchemaKeywords.getSupportLevel("else"));
			assertEquals(JsonSchemaKeywords.SupportLevel.NONE,
				JsonSchemaKeywords.getSupportLevel("prefixItems"));
			assertEquals(JsonSchemaKeywords.SupportLevel.NONE,
				JsonSchemaKeywords.getSupportLevel("contains"));
			assertEquals(JsonSchemaKeywords.SupportLevel.NONE,
				JsonSchemaKeywords.getSupportLevel("$dynamicRef"));
		}

		@Test
		@DisplayName("classifies partially supported keywords correctly")
		void classifiesPartiallySupportedKeywords() {
			assertEquals(JsonSchemaKeywords.SupportLevel.PARTIAL,
				JsonSchemaKeywords.getSupportLevel("patternProperties"));
			assertEquals(JsonSchemaKeywords.SupportLevel.PARTIAL,
				JsonSchemaKeywords.getSupportLevel("unevaluatedProperties"));
		}

		@Test
		@DisplayName("returns UNKNOWN for unknown keywords")
		void returnsUnknownForUnknownKeywords() {
			assertEquals(JsonSchemaKeywords.SupportLevel.UNKNOWN,
				JsonSchemaKeywords.getSupportLevel("customKeyword"));
			assertEquals(JsonSchemaKeywords.SupportLevel.UNKNOWN,
				JsonSchemaKeywords.getSupportLevel("x-extension"));
		}
	}

	// ========================================================================
	// Converter Diagnostics Tests
	// ========================================================================

	@Nested
	@DisplayName("Converter Diagnostics")
	class ConverterDiagnosticsTests {

		@Test
		@DisplayName("reports unsupported 'not' keyword")
		void reportsUnsupportedNotKeyword() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"title": "TestPackage",
					"definitions": {
						"NotString": {
							"type": "object",
							"not": { "type": "null" },
							"properties": {
								"value": { "type": "string" }
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			converter.convert(toInputStream(json), "definitions");

			List<JsonSchemaConversionDiagnostic> diagnostics = converter.getDiagnostics();
			assertFalse(diagnostics.isEmpty(), "Should have diagnostics for 'not' keyword");

			boolean foundNotWarning = diagnostics.stream()
				.anyMatch(d -> d.getMessage().contains("'not'")
					&& d.getCode() == JsonSchemaConversionDiagnostic.Code.UNSUPPORTED_FEATURE);
			assertTrue(foundNotWarning, "Should warn about 'not' keyword");
		}

		@Test
		@DisplayName("reports unsupported conditional keywords")
		void reportsUnsupportedConditionalKeywords() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Conditional": {
							"type": "object",
							"if": { "properties": { "type": { "const": "A" } } },
							"then": { "required": ["a"] },
							"else": { "required": ["b"] }
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			converter.convert(toInputStream(json), "definitions");

			List<JsonSchemaConversionDiagnostic> diagnostics = converter.getDiagnostics();

			assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("'if'")));
			assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("'then'")));
			assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("'else'")));
		}

		@Test
		@DisplayName("reports unsupported prefixItems (Draft 2020-12)")
		void reportsUnsupportedPrefixItems() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Tuple": {
							"type": "array",
							"prefixItems": [
								{ "type": "string" },
								{ "type": "integer" }
							]
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			converter.convert(toInputStream(json), "definitions");

			List<JsonSchemaConversionDiagnostic> diagnostics = converter.getDiagnostics();
			assertTrue(diagnostics.stream().anyMatch(d -> d.getMessage().contains("'prefixItems'")));
		}

		@Test
		@DisplayName("reports partially supported patternProperties")
		void reportsPartiallySupportedPatternProperties() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"DynamicProps": {
							"type": "object",
							"patternProperties": {
								"^x-": { "type": "string" }
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			converter.convert(toInputStream(json), "definitions");

			List<JsonSchemaConversionDiagnostic> diagnostics = converter.getDiagnostics();

			boolean foundPartialWarning = diagnostics.stream()
				.anyMatch(d -> d.getMessage().contains("patternProperties")
					&& d.getCode() == JsonSchemaConversionDiagnostic.Code.PARTIAL_SUPPORT);
			assertTrue(foundPartialWarning, "Should warn about partial support for patternProperties");
		}

		@Test
		@DisplayName("reports complex anyOf")
		void reportsComplexAnyOf() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Mixed": {
							"type": "object",
							"anyOf": [
								{ "properties": { "a": { "type": "string" } } },
								{ "properties": { "b": { "type": "integer" } } }
							]
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			converter.convert(toInputStream(json), "definitions");

			List<JsonSchemaConversionDiagnostic> diagnostics = converter.getDiagnostics();

			boolean foundAnyOfWarning = diagnostics.stream()
				.anyMatch(d -> d.getCode() == JsonSchemaConversionDiagnostic.Code.COMPLEX_ANYOF);
			assertTrue(foundAnyOfWarning, "Should warn about complex anyOf");
		}

		@Test
		@DisplayName("no diagnostics for fully supported schema")
		void noDiagnosticsForFullySupported() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"title": "SimplePackage",
					"definitions": {
						"Person": {
							"type": "object",
							"properties": {
								"name": { "type": "string" },
								"age": { "type": "integer" }
							},
							"required": ["name"]
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage result = converter.convert(toInputStream(json), "definitions");

			assertNotNull(result);
			List<JsonSchemaConversionDiagnostic> diagnostics = converter.getDiagnostics();
			assertTrue(diagnostics.isEmpty(), "Should have no diagnostics for fully supported schema");
		}
	}

	// ========================================================================
	// Resource Diagnostics Tests
	// ========================================================================

	@Nested
	@DisplayName("Resource Diagnostics")
	class ResourceDiagnosticsTests {

		@Test
		@DisplayName("diagnostics available via resource.getWarnings()")
		void diagnosticsAvailableViaResource() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Invalid": {
							"type": "object",
							"not": { "type": "null" },
							"if": { "const": true }
						}
					}
				}
				""";

			JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
				URI.createURI("test.jsonschema"));
			resource.load(toInputStream(json), null);

			// Diagnostics should be in resource warnings
			assertFalse(resource.getWarnings().isEmpty(),
				"Resource should have warnings for unsupported features");

			// Verify diagnostics are proper Resource.Diagnostic instances
			for (Resource.Diagnostic diagnostic : resource.getWarnings()) {
				assertNotNull(diagnostic.getMessage());
				assertNotNull(diagnostic.getLocation());
			}
		}

		@Test
		@DisplayName("no warnings for valid schema")
		void noWarningsForValidSchema() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"title": "ValidPackage",
					"definitions": {
						"Item": {
							"type": "object",
							"properties": {
								"id": { "type": "string" }
							}
						}
					}
				}
				""";

			JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
				URI.createURI("test.jsonschema"));
			resource.load(toInputStream(json), null);

			assertTrue(resource.getWarnings().isEmpty(),
				"Resource should have no warnings for valid schema");
		}
	}

	// ========================================================================
	// Diagnostic Message Tests
	// ========================================================================

	@Nested
	@DisplayName("Diagnostic Messages")
	class DiagnosticMessageTests {

		@Test
		@DisplayName("diagnostic toString includes all information")
		void diagnosticToStringIncludesAllInfo() {
			JsonSchemaConversionDiagnostic diag = JsonSchemaConversionDiagnostic
				.unsupportedFeature("not", "definitions/MyType");

			String str = diag.toString();
			assertTrue(str.contains("UNSUPPORTED_FEATURE"));
			assertTrue(str.contains("not"));
			assertTrue(str.contains("definitions/MyType"));
		}

		@Test
		@DisplayName("factory methods create correct diagnostics")
		void factoryMethodsCreateCorrectDiagnostics() {
			JsonSchemaConversionDiagnostic unresolved =
				JsonSchemaConversionDiagnostic.unresolvedReference("#/definitions/Missing");
			assertEquals(JsonSchemaConversionDiagnostic.Code.UNRESOLVED_REFERENCE, unresolved.getCode());
			assertTrue(unresolved.getMessage().contains("Missing"));

			JsonSchemaConversionDiagnostic anyOf =
				JsonSchemaConversionDiagnostic.complexAnyOf("MyClass");
			assertEquals(JsonSchemaConversionDiagnostic.Code.COMPLEX_ANYOF, anyOf.getCode());

			JsonSchemaConversionDiagnostic partial =
				JsonSchemaConversionDiagnostic.partialSupport("patternProperties", "MyClass", "no EMF equivalent");
			assertEquals(JsonSchemaConversionDiagnostic.Code.PARTIAL_SUPPORT, partial.getCode());
			assertTrue(partial.getMessage().contains("no EMF equivalent"));
		}
	}

	// ========================================================================
	// Helper Methods
	// ========================================================================

	private ByteArrayInputStream toInputStream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}
}
