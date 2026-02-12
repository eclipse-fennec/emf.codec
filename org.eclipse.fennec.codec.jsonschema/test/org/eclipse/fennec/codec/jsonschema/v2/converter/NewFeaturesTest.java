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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for newly implemented JSON Schema features:
 * - $comment
 * - deprecated
 * - contentEncoding
 * - contentMediaType
 * - $anchor (local schema references)
 */
@DisplayName("New JSON Schema Features")
class NewFeaturesTest {

	private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";
	private static final String GEN_MODEL_ANNOTATION_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";

	// ========================================================================
	// $comment Tests
	// ========================================================================

	@Nested
	@DisplayName("$comment keyword")
	class CommentTests {

		@Test
		@DisplayName("reads $comment from schema")
		void readsComment() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Person": {
							"type": "object",
							"$comment": "This is an internal schema for person data",
							"properties": {
								"name": {
									"type": "string",
									"$comment": "Full name of the person"
								}
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage result = converter.convert(toInputStream(json), "definitions");

			assertNotNull(result);
			EClass person = (EClass) result.getEClassifier("Person");
			assertNotNull(person);

			// Check class-level comment
			String classComment = getAnnotationValue(person, JSONSCHEMA_ANNOTATION_SOURCE, "comment");
			assertEquals("This is an internal schema for person data", classComment);

			// Check property-level comment
			EAttribute nameAttr = (EAttribute) person.getEStructuralFeature("name");
			assertNotNull(nameAttr);
			String propComment = getAnnotationValue(nameAttr, JSONSCHEMA_ANNOTATION_SOURCE, "comment");
			assertEquals("Full name of the person", propComment);
		}

		@Test
		@DisplayName("writes $comment to schema")
		void writesComment() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Item": {
							"type": "object",
							"$comment": "Internal use only",
							"properties": {
								"id": {
									"type": "string",
									"$comment": "Unique identifier"
								}
							}
						}
					}
				}
				""";

			// Read
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write back
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", false);
			String output = baos.toString(StandardCharsets.UTF_8);

			assertTrue(output.contains("\"$comment\""), "Output should contain $comment");
			assertTrue(output.contains("Internal use only"), "Output should contain class comment");
			assertTrue(output.contains("Unique identifier"), "Output should contain property comment");
		}

		@Test
		@DisplayName("no diagnostics for $comment (now fully supported)")
		void noDiagnosticsForComment() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Test": {
							"type": "object",
							"$comment": "This should not trigger a warning"
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			converter.convert(toInputStream(json), "definitions");

			// $comment should no longer trigger a warning
			boolean hasCommentWarning = converter.getDiagnostics().stream()
				.anyMatch(d -> d.getMessage().contains("$comment"));
			assertTrue(!hasCommentWarning, "$comment should not trigger warnings anymore");
		}
	}

	// ========================================================================
	// deprecated Tests
	// ========================================================================

	@Nested
	@DisplayName("deprecated keyword")
	class DeprecatedTests {

		@Test
		@DisplayName("reads deprecated from schema")
		void readsDeprecated() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"LegacyItem": {
							"type": "object",
							"deprecated": true,
							"properties": {
								"oldField": {
									"type": "string",
									"deprecated": true
								},
								"newField": {
									"type": "string"
								}
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage result = converter.convert(toInputStream(json), "definitions");

			assertNotNull(result);
			EClass legacyItem = (EClass) result.getEClassifier("LegacyItem");
			assertNotNull(legacyItem);

			// Check class-level deprecated (stored in GenModel)
			String classDeprecated = getAnnotationValue(legacyItem, GEN_MODEL_ANNOTATION_SOURCE, "deprecated");
			assertEquals("true", classDeprecated);

			// Check deprecated property
			EAttribute oldField = (EAttribute) legacyItem.getEStructuralFeature("oldField");
			assertNotNull(oldField);
			String propDeprecated = getAnnotationValue(oldField, GEN_MODEL_ANNOTATION_SOURCE, "deprecated");
			assertEquals("true", propDeprecated);

			// Check non-deprecated property
			EAttribute newField = (EAttribute) legacyItem.getEStructuralFeature("newField");
			assertNotNull(newField);
			String newFieldDeprecated = getAnnotationValue(newField, GEN_MODEL_ANNOTATION_SOURCE, "deprecated");
			assertNull(newFieldDeprecated, "newField should not be deprecated");
		}

		@Test
		@DisplayName("writes deprecated to schema")
		void writesDeprecated() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"OldApi": {
							"type": "object",
							"properties": {
								"legacyId": {
									"type": "string",
									"deprecated": true
								}
							}
						}
					}
				}
				""";

			// Read
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write back
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", false);
			String output = baos.toString(StandardCharsets.UTF_8);

			assertTrue(output.contains("\"deprecated\""), "Output should contain deprecated");
			assertTrue(output.contains("\"deprecated\":true") || output.contains("\"deprecated\": true"),
				"Output should have deprecated:true");
		}

		@Test
		@DisplayName("deprecated:false is not preserved")
		void deprecatedFalseNotPreserved() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Current": {
							"type": "object",
							"deprecated": false,
							"properties": {
								"active": { "type": "boolean" }
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage result = converter.convert(toInputStream(json), "definitions");

			EClass current = (EClass) result.getEClassifier("Current");
			String deprecated = getAnnotationValue(current, GEN_MODEL_ANNOTATION_SOURCE, "deprecated");
			assertNull(deprecated, "deprecated:false should not create annotation");
		}
	}

	// ========================================================================
	// contentEncoding Tests
	// ========================================================================

	@Nested
	@DisplayName("contentEncoding keyword")
	class ContentEncodingTests {

		@Test
		@DisplayName("reads contentEncoding from schema")
		void readsContentEncoding() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"BinaryData": {
							"type": "object",
							"properties": {
								"imageData": {
									"type": "string",
									"contentEncoding": "base64"
								}
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage result = converter.convert(toInputStream(json), "definitions");

			assertNotNull(result);
			EClass binaryData = (EClass) result.getEClassifier("BinaryData");
			EAttribute imageData = (EAttribute) binaryData.getEStructuralFeature("imageData");

			String encoding = getAnnotationValue(imageData, JSONSCHEMA_ANNOTATION_SOURCE, "contentEncoding");
			// Note: stored as JSON string with quotes
			assertTrue(encoding != null && encoding.contains("base64"));
		}

		@Test
		@DisplayName("writes contentEncoding to schema")
		void writesContentEncoding() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"FileContent": {
							"type": "object",
							"properties": {
								"data": {
									"type": "string",
									"contentEncoding": "base64"
								}
							}
						}
					}
				}
				""";

			// Read
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write back
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", false);
			String output = baos.toString(StandardCharsets.UTF_8);

			assertTrue(output.contains("\"contentEncoding\""), "Output should contain contentEncoding");
			assertTrue(output.contains("base64"), "Output should contain base64 value");
		}
	}

	// ========================================================================
	// contentMediaType Tests
	// ========================================================================

	@Nested
	@DisplayName("contentMediaType keyword")
	class ContentMediaTypeTests {

		@Test
		@DisplayName("reads contentMediaType from schema")
		void readsContentMediaType() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"ImageHolder": {
							"type": "object",
							"properties": {
								"thumbnail": {
									"type": "string",
									"contentEncoding": "base64",
									"contentMediaType": "image/png"
								}
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage result = converter.convert(toInputStream(json), "definitions");

			assertNotNull(result);
			EClass imageHolder = (EClass) result.getEClassifier("ImageHolder");
			EAttribute thumbnail = (EAttribute) imageHolder.getEStructuralFeature("thumbnail");

			String mediaType = getAnnotationValue(thumbnail, JSONSCHEMA_ANNOTATION_SOURCE, "contentMediaType");
			assertTrue(mediaType != null && mediaType.contains("image/png"));
		}

		@Test
		@DisplayName("writes contentMediaType to schema")
		void writesContentMediaType() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Document": {
							"type": "object",
							"properties": {
								"pdfContent": {
									"type": "string",
									"contentEncoding": "base64",
									"contentMediaType": "application/pdf"
								}
							}
						}
					}
				}
				""";

			// Read
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write back
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", false);
			String output = baos.toString(StandardCharsets.UTF_8);

			assertTrue(output.contains("\"contentMediaType\""), "Output should contain contentMediaType");
			assertTrue(output.contains("application/pdf"), "Output should contain application/pdf value");
		}
	}

	// ========================================================================
	// Round-Trip Tests
	// ========================================================================

	@Nested
	@DisplayName("Round-trip")
	class RoundTripTests {

		@Test
		@DisplayName("round-trips all new features")
		void roundTripsAllNewFeatures() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"title": "TestPackage",
					"definitions": {
						"CompleteExample": {
							"type": "object",
							"$comment": "A complete example with all new features",
							"deprecated": true,
							"properties": {
								"binaryField": {
									"type": "string",
									"$comment": "Contains binary data",
									"contentEncoding": "base64",
									"contentMediaType": "application/octet-stream"
								},
								"oldField": {
									"type": "string",
									"deprecated": true,
									"$comment": "Use newField instead"
								}
							}
						}
					}
				}
				""";

			// Read
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", false);
			String output = baos.toString(StandardCharsets.UTF_8);

			// Verify all features are present in output
			assertTrue(output.contains("$comment"), "Should preserve $comment");
			assertTrue(output.contains("deprecated"), "Should preserve deprecated");
			assertTrue(output.contains("contentEncoding"), "Should preserve contentEncoding");
			assertTrue(output.contains("contentMediaType"), "Should preserve contentMediaType");
			assertTrue(output.contains("base64"), "Should preserve base64 value");
			assertTrue(output.contains("application/octet-stream"), "Should preserve media type value");
		}
	}

	// ========================================================================
	// JsonSchemaKeywords Tests
	// ========================================================================

	@Nested
	@DisplayName("JsonSchemaKeywords classification")
	class KeywordsClassificationTests {

		@Test
		@DisplayName("$comment is now fully supported")
		void commentIsFullySupported() {
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("$comment"));
			assertTrue(JsonSchemaKeywords.isFullySupported("$comment"));
		}

		@Test
		@DisplayName("deprecated is fully supported")
		void deprecatedIsFullySupported() {
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("deprecated"));
			assertTrue(JsonSchemaKeywords.isFullySupported("deprecated"));
		}

		@Test
		@DisplayName("contentEncoding is fully supported")
		void contentEncodingIsFullySupported() {
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("contentEncoding"));
			assertTrue(JsonSchemaKeywords.isFullySupported("contentEncoding"));
		}

		@Test
		@DisplayName("contentMediaType is fully supported")
		void contentMediaTypeIsFullySupported() {
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("contentMediaType"));
			assertTrue(JsonSchemaKeywords.isFullySupported("contentMediaType"));
		}

		@Test
		@DisplayName("contentSchema is still unsupported")
		void contentSchemaIsUnsupported() {
			assertEquals(JsonSchemaKeywords.SupportLevel.NONE,
				JsonSchemaKeywords.getSupportLevel("contentSchema"));
			assertTrue(JsonSchemaKeywords.isUnsupported("contentSchema"));
		}

		@Test
		@DisplayName("$anchor is fully supported")
		void anchorIsFullySupported() {
			assertEquals(JsonSchemaKeywords.SupportLevel.FULL,
				JsonSchemaKeywords.getSupportLevel("$anchor"));
			assertTrue(JsonSchemaKeywords.isFullySupported("$anchor"));
		}
	}

	// ========================================================================
	// $anchor Tests
	// ========================================================================

	@Nested
	@DisplayName("$anchor keyword")
	class AnchorTests {

		@Test
		@DisplayName("reads $anchor from schema")
		void readsAnchor() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Address": {
							"type": "object",
							"$anchor": "address",
							"properties": {
								"street": { "type": "string" },
								"city": { "type": "string" }
							}
						},
						"Person": {
							"type": "object",
							"properties": {
								"name": { "type": "string" },
								"home": { "$ref": "#address" }
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage result = converter.convert(toInputStream(json), "definitions");

			assertNotNull(result);

			// Check that Address has anchor annotation
			EClass address = (EClass) result.getEClassifier("Address");
			assertNotNull(address);
			String anchorValue = getAnnotationValue(address, JSONSCHEMA_ANNOTATION_SOURCE, "anchor");
			assertEquals("address", anchorValue);

			// Check that Person.home reference resolved correctly via anchor
			EClass person = (EClass) result.getEClassifier("Person");
			assertNotNull(person);
			EReference homeRef = (EReference) person.getEStructuralFeature("home");
			assertNotNull(homeRef, "home reference should exist");
			assertEquals(address, homeRef.getEType(), "home should reference Address via anchor");
		}

		@Test
		@DisplayName("writes $anchor to schema")
		void writesAnchor() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Location": {
							"type": "object",
							"$anchor": "loc",
							"properties": {
								"lat": { "type": "number" },
								"lon": { "type": "number" }
							}
						}
					}
				}
				""";

			// Read
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write back
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", false);
			String output = baos.toString(StandardCharsets.UTF_8);

			assertTrue(output.contains("\"$anchor\""), "Output should contain $anchor");
			assertTrue(output.contains("\"loc\""), "Output should contain anchor value");
		}

		@Test
		@DisplayName("anchor reference works alongside JSON pointer reference")
		void anchorAndPointerRefsBothWork() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Address": {
							"type": "object",
							"$anchor": "addr",
							"properties": {
								"street": { "type": "string" }
							}
						},
						"PersonWithAnchorRef": {
							"type": "object",
							"properties": {
								"home": { "$ref": "#addr" }
							}
						},
						"PersonWithPointerRef": {
							"type": "object",
							"properties": {
								"work": { "$ref": "#/definitions/Address" }
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage result = converter.convert(toInputStream(json), "definitions");

			EClass address = (EClass) result.getEClassifier("Address");
			EClass personAnchor = (EClass) result.getEClassifier("PersonWithAnchorRef");
			EClass personPointer = (EClass) result.getEClassifier("PersonWithPointerRef");

			EReference homeRef = (EReference) personAnchor.getEStructuralFeature("home");
			EReference workRef = (EReference) personPointer.getEStructuralFeature("work");

			assertNotNull(homeRef);
			assertNotNull(workRef);
			assertEquals(address, homeRef.getEType(), "Anchor ref should resolve to Address");
			assertEquals(address, workRef.getEType(), "Pointer ref should resolve to Address");
		}

		@Test
		@DisplayName("no diagnostics for $anchor (fully supported)")
		void noDiagnosticsForAnchor() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Test": {
							"type": "object",
							"$anchor": "myAnchor",
							"properties": {
								"value": { "type": "string" }
							}
						}
					}
				}
				""";

			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			converter.convert(toInputStream(json), "definitions");

			// $anchor should not trigger any warnings
			boolean hasAnchorWarning = converter.getDiagnostics().stream()
				.anyMatch(d -> d.getMessage().contains("$anchor"));
			assertTrue(!hasAnchorWarning, "$anchor should not trigger warnings");
		}

		@Test
		@DisplayName("OPTION_USE_ANCHOR_REFS generates anchors for all classes")
		void optionGeneratesAnchorsForAllClasses() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Address": {
							"type": "object",
							"properties": {
								"street": { "type": "string" }
							}
						},
						"Person": {
							"type": "object",
							"properties": {
								"name": { "type": "string" },
								"home": { "$ref": "#/definitions/Address" }
							}
						}
					}
				}
				""";

			// Read without anchor
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write back WITH anchor option
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Map<String, Object> options = Map.of(EPackageToJsonSchemaConverter.OPTION_USE_ANCHOR_REFS, true);
			writer.convert(ePackage, baos, "definitions", false, options);
			String output = baos.toString(StandardCharsets.UTF_8);

			// Both classes should have $anchor
			assertTrue(output.contains("\"$anchor\":\"address\"") || output.contains("\"$anchor\": \"address\""),
				"Address should have $anchor");
			assertTrue(output.contains("\"$anchor\":\"person\"") || output.contains("\"$anchor\": \"person\""),
				"Person should have $anchor");

			// Reference should use anchor format
			assertTrue(output.contains("\"$ref\":\"#address\"") || output.contains("\"$ref\": \"#address\""),
				"Reference should use anchor format #address");
		}

		@Test
		@DisplayName("default serialization uses JSON Pointer refs (no anchors)")
		void defaultUsesJsonPointerRefs() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Address": {
							"type": "object",
							"properties": {
								"street": { "type": "string" }
							}
						},
						"Person": {
							"type": "object",
							"properties": {
								"home": { "$ref": "#/definitions/Address" }
							}
						}
					}
				}
				""";

			// Read
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write back WITHOUT anchor option (default)
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", false);
			String output = baos.toString(StandardCharsets.UTF_8);

			// Should NOT have $anchor (Address had no anchor in input)
			assertTrue(!output.contains("\"$anchor\""),
				"Default should not generate $anchor");

			// Reference should use JSON Pointer format
			assertTrue(output.contains("#/definitions/Address"),
				"Reference should use JSON Pointer format");
		}

		@Test
		@DisplayName("round-trip preserves existing anchors without option")
		void roundTripPreservesExistingAnchors() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Address": {
							"type": "object",
							"$anchor": "addr",
							"properties": {
								"street": { "type": "string" }
							}
						},
						"Person": {
							"type": "object",
							"properties": {
								"home": { "$ref": "#addr" }
							}
						}
					}
				}
				""";

			// Read
			JsonSchemaToEPackageConverter reader = new JsonSchemaToEPackageConverter();
			EPackage ePackage = reader.convert(toInputStream(json), "definitions");

			// Write back without option
			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", false);
			String output = baos.toString(StandardCharsets.UTF_8);

			// Original anchor should be preserved
			assertTrue(output.contains("\"$anchor\":\"addr\"") || output.contains("\"$anchor\": \"addr\""),
				"Existing anchor should be preserved");

			// Reference should use the anchor
			assertTrue(output.contains("\"$ref\":\"#addr\"") || output.contains("\"$ref\": \"#addr\""),
				"Reference should use preserved anchor");
		}
	}

	// ========================================================================
	// Helper Methods
	// ========================================================================

	private ByteArrayInputStream toInputStream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}

	private String getAnnotationValue(org.eclipse.emf.ecore.EModelElement element, String source, String key) {
		EAnnotation annotation = element.getEAnnotation(source);
		if (annotation != null) {
			return annotation.getDetails().get(key);
		}
		return null;
	}
}
