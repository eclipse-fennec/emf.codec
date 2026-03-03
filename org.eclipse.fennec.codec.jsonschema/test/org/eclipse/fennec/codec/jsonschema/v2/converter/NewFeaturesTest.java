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
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.jsonschema.v2.JsonSchemaResourceImpl;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

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
			Map<String, Object> options = Map.of(CodecJsonSchemaOptions.OPTION_USE_ANCHOR_REFS, true);
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

	// ========================================================================
	// Date/Time type serialization
	// ========================================================================

	@Nested
	@DisplayName("Date/time type serialization")
	class DateTimeTests {

		@Test
		@DisplayName("EDate serializes as type:string with format:date-time")
		void eDateSerializesWithFormat() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Event");

			EAttribute dateAttr = EcoreFactory.eINSTANCE.createEAttribute();
			dateAttr.setName("createdAt");
			dateAttr.setEType(EcorePackage.Literals.EDATE);
			eClass.getEStructuralFeatures().add(dateAttr);

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos, true);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode createdAt = root.get("properties").get("createdAt");
			assertNotNull(createdAt, "createdAt property should exist");
			assertEquals("string", createdAt.get("type").asString(),
				"EDate should serialize as type:string");
			assertEquals("date-time", createdAt.get("format").asString(),
				"EDate should have format:date-time");
		}

		@Test
		@DisplayName("EDate with explicit format annotation preserves annotation value")
		void eDateWithExplicitFormatPreservesAnnotation() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Event");

			EAttribute dateAttr = EcoreFactory.eINSTANCE.createEAttribute();
			dateAttr.setName("eventDate");
			dateAttr.setEType(EcorePackage.Literals.EDATE);
			// Add explicit format annotation
			EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
			ann.setSource("http://fennec.eclipse.org/jsonschema");
			ann.getDetails().put("format", "date");
			dateAttr.getEAnnotations().add(ann);
			eClass.getEStructuralFeatures().add(dateAttr);

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos, true);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode eventDate = root.get("properties").get("eventDate");
			assertEquals("string", eventDate.get("type").asString());
			assertEquals("date", eventDate.get("format").asString(),
				"Explicit format annotation should take precedence");
		}

		@Test
		@DisplayName("EDate with description still gets type and format")
		void eDateWithDescriptionGetsTypeAndFormat() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Document");

			EAttribute dateAttr = EcoreFactory.eINSTANCE.createEAttribute();
			dateAttr.setName("analysisDate");
			dateAttr.setEType(EcorePackage.Literals.EDATE);
			// Add documentation
			EAnnotation genModel = EcoreFactory.eINSTANCE.createEAnnotation();
			genModel.setSource("http://www.eclipse.org/emf/2002/GenModel");
			genModel.getDetails().put("documentation",
				"The date the analysis was performed in strict ISO-8601 format");
			dateAttr.getEAnnotations().add(genModel);
			eClass.getEStructuralFeatures().add(dateAttr);

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos, true);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode analysisDate = root.get("properties").get("analysisDate");
			assertNotNull(analysisDate.get("description"), "description should be present");
			assertEquals("string", analysisDate.get("type").asString(),
				"EDate should have type:string even with description");
			assertEquals("date-time", analysisDate.get("format").asString(),
				"EDate should have format:date-time even with description");
		}
	}

	// ========================================================================
	// $defs for referenced classes in single EClass conversion
	// ========================================================================

	@Nested
	@DisplayName("$defs for referenced classes")
	class DefsForReferencedClassesTests {

		@Test
		@DisplayName("EClass converter emits $defs for non-containment references in same package")
		void emitsDefsForNonContainmentRef() throws IOException {
			EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
			ePackage.setName("test");
			ePackage.setNsPrefix("test");
			ePackage.setNsURI("http://example.org/test");

			EClass address = EcoreFactory.eINSTANCE.createEClass();
			address.setName("Address");
			EAttribute street = EcoreFactory.eINSTANCE.createEAttribute();
			street.setName("street");
			street.setEType(EcorePackage.Literals.ESTRING);
			address.getEStructuralFeatures().add(street);
			ePackage.getEClassifiers().add(address);

			EClass person = EcoreFactory.eINSTANCE.createEClass();
			person.setName("Person");
			EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
			name.setName("name");
			name.setEType(EcorePackage.Literals.ESTRING);
			person.getEStructuralFeatures().add(name);

			EReference homeRef = EcoreFactory.eINSTANCE.createEReference();
			homeRef.setName("home");
			homeRef.setEType(address);
			homeRef.setContainment(false);
			person.getEStructuralFeatures().add(homeRef);
			ePackage.getEClassifiers().add(person);

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(person, baos, true);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());

			// $defs should contain Address
			JsonNode defs = root.get("$defs");
			assertNotNull(defs, "$defs should be present");
			JsonNode addressDef = defs.get("Address");
			assertNotNull(addressDef, "Address should be defined in $defs");
			assertEquals("object", addressDef.get("type").asString());

			// $ref should point to $defs/Address
			JsonNode homeProperty = root.get("properties").get("home");
			assertNotNull(homeProperty);
			String ref = homeProperty.get("$ref").asString();
			assertEquals("#/$defs/Address", ref);
		}

		@Test
		@DisplayName("EClass converter emits $defs for supertype references (allOf)")
		void emitsDefsForSupertypeRef() throws IOException {
			EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
			ePackage.setName("test");
			ePackage.setNsPrefix("test");
			ePackage.setNsURI("http://example.org/test");

			EClass base = EcoreFactory.eINSTANCE.createEClass();
			base.setName("CompactTrend");
			EAttribute title = EcoreFactory.eINSTANCE.createEAttribute();
			title.setName("title");
			title.setEType(EcorePackage.Literals.ESTRING);
			base.getEStructuralFeatures().add(title);
			ePackage.getEClassifiers().add(base);

			EClass extended = EcoreFactory.eINSTANCE.createEClass();
			extended.setName("DetailedTrend");
			extended.getESuperTypes().add(base);
			EAttribute description = EcoreFactory.eINSTANCE.createEAttribute();
			description.setName("description");
			description.setEType(EcorePackage.Literals.ESTRING);
			extended.getEStructuralFeatures().add(description);
			ePackage.getEClassifiers().add(extended);

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(extended, baos, true);
			String output = baos.toString(StandardCharsets.UTF_8);

			JsonNode root = JsonMapper.builder().build().readTree(output);

			// $defs should contain CompactTrend
			JsonNode defs = root.get("$defs");
			assertNotNull(defs, "$defs should be present for supertype");
			JsonNode compactDef = defs.get("CompactTrend");
			assertNotNull(compactDef, "CompactTrend should be defined in $defs");

			// allOf should contain a $ref to $defs/CompactTrend
			JsonNode allOf = root.get("allOf");
			assertNotNull(allOf, "allOf should be present for inherited class");
			boolean hasRef = false;
			for (JsonNode entry : allOf) {
				JsonNode refNode = entry.get("$ref");
				if (refNode != null && refNode.asString().equals("#/$defs/CompactTrend")) {
					hasRef = true;
					break;
				}
			}
			assertTrue(hasRef, "allOf should contain $ref to #/$defs/CompactTrend");
		}

		@Test
		@DisplayName("EClassValueWriter emits $defs for referenced classes")
		void eClassValueWriterEmitsDefs() throws IOException {
			EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
			ePackage.setName("test");
			ePackage.setNsPrefix("test");
			ePackage.setNsURI("http://example.org/test");

			// CompactTrend base class
			EClass compactTrend = EcoreFactory.eINSTANCE.createEClass();
			compactTrend.setName("CompactTrend");
			EAttribute trendTitle = EcoreFactory.eINSTANCE.createEAttribute();
			trendTitle.setName("title");
			trendTitle.setEType(EcorePackage.Literals.ESTRING);
			compactTrend.getEStructuralFeatures().add(trendTitle);
			ePackage.getEClassifiers().add(compactTrend);

			// DetailedTrend extends CompactTrend
			EClass detailedTrend = EcoreFactory.eINSTANCE.createEClass();
			detailedTrend.setName("DetailedTrend");
			detailedTrend.getESuperTypes().add(compactTrend);
			EAttribute desc = EcoreFactory.eINSTANCE.createEAttribute();
			desc.setName("description");
			desc.setEType(EcorePackage.Literals.ESTRING);
			detailedTrend.getEStructuralFeatures().add(desc);
			ePackage.getEClassifiers().add(detailedTrend);

			// TrendAnalysis root with containment ref to DetailedTrend list
			EClass trendAnalysis = EcoreFactory.eINSTANCE.createEClass();
			trendAnalysis.setName("TrendAnalysis");
			EReference trendsRef = EcoreFactory.eINSTANCE.createEReference();
			trendsRef.setName("trends");
			trendsRef.setEType(detailedTrend);
			trendsRef.setContainment(true);
			trendsRef.setUpperBound(-1);
			trendAnalysis.getEStructuralFeatures().add(trendsRef);
			ePackage.getEClassifiers().add(trendAnalysis);

			// Use EClassToJsonSchemaConverter (same path as EClassValueWriter)
			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(trendAnalysis, baos, true);
			String output = baos.toString(StandardCharsets.UTF_8);

			JsonNode root = JsonMapper.builder().build().readTree(output);

			// $defs should contain CompactTrend (referenced via allOf in DetailedTrend)
			JsonNode defs = root.get("$defs");
			assertNotNull(defs, "$defs should be present: " + output);
			assertNotNull(defs.get("CompactTrend"),
				"CompactTrend should be in $defs since DetailedTrend extends it: " + output);

			// The allOf in the inlined DetailedTrend should reference $defs/CompactTrend
			assertTrue(output.contains("#/$defs/CompactTrend"),
				"$ref should point to #/$defs/CompactTrend: " + output);
		}

		@Test
		@DisplayName("EClass converter does not emit $defs when no external references")
		void noDefsWhenNoExternalRefs() throws IOException {
			EClass simple = EcoreFactory.eINSTANCE.createEClass();
			simple.setName("Simple");
			EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
			attr.setName("value");
			attr.setEType(EcorePackage.Literals.ESTRING);
			simple.getEStructuralFeatures().add(attr);

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(simple, baos, true);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			assertNull(root.get("$defs"), "$defs should not be present when no references");
		}
	}

	// ========================================================================
	// additionalProperties Tests
	// ========================================================================

	@Nested
	@DisplayName("additionalProperties: false by default")
	class AdditionalPropertiesTests {

		@Test
		@DisplayName("EClass converter emits additionalProperties:false")
		void eClassConverterEmitsAdditionalPropertiesFalse() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Simple");

			EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
			name.setName("name");
			name.setEType(EcorePackage.Literals.ESTRING);
			eClass.getEStructuralFeatures().add(name);

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos, true);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			assertEquals("object", root.get("type").asString());
			assertNotNull(root.get("additionalProperties"), "additionalProperties should be present");
			assertFalse(root.get("additionalProperties").asBoolean(), "additionalProperties should be false");
		}

		@Test
		@DisplayName("EPackage converter emits additionalProperties:false on all object definitions")
		void ePackageConverterEmitsAdditionalPropertiesFalse() throws IOException {
			String json = """
				{
					"$id": "http://example.org/test",
					"definitions": {
						"Person": {
							"type": "object",
							"properties": {
								"name": { "type": "string" },
								"age": { "type": "integer" }
							}
						},
						"Address": {
							"type": "object",
							"properties": {
								"street": { "type": "string" }
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
			writer.convert(ePackage, baos, "definitions", true);
			String output = baos.toString(StandardCharsets.UTF_8);

			JsonNode root = JsonMapper.builder().build().readTree(output);
			JsonNode definitions = root.get("definitions");

			JsonNode person = definitions.get("Person");
			assertNotNull(person.get("additionalProperties"),
				"Person should have additionalProperties");
			assertFalse(person.get("additionalProperties").asBoolean(),
				"Person additionalProperties should be false");

			JsonNode address = definitions.get("Address");
			assertNotNull(address.get("additionalProperties"),
				"Address should have additionalProperties");
			assertFalse(address.get("additionalProperties").asBoolean(),
				"Address additionalProperties should be false");
		}

		@Test
		@DisplayName("nested object references also get additionalProperties:false")
		void nestedObjectsGetAdditionalPropertiesFalse() throws IOException {
			EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
			ePackage.setName("test");
			ePackage.setNsPrefix("test");
			ePackage.setNsURI("http://example.org/test");

			EClass address = EcoreFactory.eINSTANCE.createEClass();
			address.setName("Address");
			EAttribute street = EcoreFactory.eINSTANCE.createEAttribute();
			street.setName("street");
			street.setEType(EcorePackage.Literals.ESTRING);
			address.getEStructuralFeatures().add(street);
			ePackage.getEClassifiers().add(address);

			EClass person = EcoreFactory.eINSTANCE.createEClass();
			person.setName("Person");
			EAttribute personName = EcoreFactory.eINSTANCE.createEAttribute();
			personName.setName("name");
			personName.setEType(EcorePackage.Literals.ESTRING);
			person.getEStructuralFeatures().add(personName);
			EReference homeRef = EcoreFactory.eINSTANCE.createEReference();
			homeRef.setName("home");
			homeRef.setEType(address);
			homeRef.setContainment(true);
			person.getEStructuralFeatures().add(homeRef);
			ePackage.getEClassifiers().add(person);

			EPackageToJsonSchemaConverter writer = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.convert(ePackage, baos, "definitions", true);
			String output = baos.toString(StandardCharsets.UTF_8);

			JsonNode root = JsonMapper.builder().build().readTree(output);
			JsonNode definitions = root.get("definitions");

			// Both top-level classes should have additionalProperties:false
			assertFalse(definitions.get("Person").get("additionalProperties").asBoolean());
			assertFalse(definitions.get("Address").get("additionalProperties").asBoolean());
		}
	}

	// ========================================================================
	// allFieldsRequired Tests
	// ========================================================================

	// ========================================================================
	// flatAllOf Tests
	// ========================================================================

	@Nested
	@DisplayName("OPTION_FLAT_ALL_OF")
	class FlatAllOfTests {

		@Test
		@DisplayName("without option: inheritance uses allOf with $ref")
		void withoutOption_usesAllOfWithRef() throws IOException {
			EPackage pkg = createInheritancePackage();

			EPackageToJsonSchemaConverter converter = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(pkg, baos, "definitions", false);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode childDef = root.path("definitions").path("Child");
			assertNotNull(childDef);
			assertTrue(childDef.has("allOf"), "Child should have allOf for inheritance");
			assertFalse(childDef.has("type"), "Child should not have top-level type (it's inside allOf)");
		}

		@Test
		@DisplayName("with flatAllOf=true: no allOf, no $ref, parent properties inlined")
		void withOption_flattenedInheritance() throws IOException {
			EPackage pkg = createInheritancePackage();

			EPackageToJsonSchemaConverter converter = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(pkg, baos, "definitions", false,
					Map.of(CodecJsonSchemaOptions.OPTION_FLAT_ALL_OF, Boolean.TRUE));

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode childDef = root.path("definitions").path("Child");
			assertNotNull(childDef);
			assertFalse(childDef.has("allOf"), "Child should NOT have allOf when flatAllOf=true");
			assertEquals("object", childDef.path("type").asString());

			// Check that parent property is inlined
			JsonNode props = childDef.path("properties");
			assertTrue(props.has("parentName"), "Parent property 'parentName' should be inlined");
			assertTrue(props.has("childAge"), "Child property 'childAge' should be present");
		}

		@Test
		@DisplayName("with flatAllOf=true: deep inheritance flattens all ancestor properties")
		void withOption_deepInheritanceFlattened() throws IOException {
			EPackage pkg = createDeepInheritancePackage();

			EPackageToJsonSchemaConverter converter = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(pkg, baos, "definitions", false,
					Map.of(CodecJsonSchemaOptions.OPTION_FLAT_ALL_OF, Boolean.TRUE));

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode grandChildDef = root.path("definitions").path("GrandChild");
			assertNotNull(grandChildDef);
			assertFalse(grandChildDef.has("allOf"), "GrandChild should NOT have allOf");

			JsonNode props = grandChildDef.path("properties");
			assertTrue(props.has("baseProp"), "GrandParent property 'baseProp' should be inlined");
			assertTrue(props.has("middleProp"), "Parent property 'middleProp' should be inlined");
			assertTrue(props.has("leafProp"), "GrandChild property 'leafProp' should be present");
		}

		private EPackage createInheritancePackage() {
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("test");
			pkg.setNsPrefix("test");
			pkg.setNsURI("http://example.org/test");

			EClass parent = EcoreFactory.eINSTANCE.createEClass();
			parent.setName("Parent");
			EAttribute parentAttr = EcoreFactory.eINSTANCE.createEAttribute();
			parentAttr.setName("parentName");
			parentAttr.setEType(EcorePackage.Literals.ESTRING);
			parent.getEStructuralFeatures().add(parentAttr);
			pkg.getEClassifiers().add(parent);

			EClass child = EcoreFactory.eINSTANCE.createEClass();
			child.setName("Child");
			child.getESuperTypes().add(parent);
			EAttribute childAttr = EcoreFactory.eINSTANCE.createEAttribute();
			childAttr.setName("childAge");
			childAttr.setEType(EcorePackage.Literals.EINT);
			child.getEStructuralFeatures().add(childAttr);
			pkg.getEClassifiers().add(child);

			return pkg;
		}

		private EPackage createDeepInheritancePackage() {
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("test");
			pkg.setNsPrefix("test");
			pkg.setNsURI("http://example.org/test");

			EClass grandParent = EcoreFactory.eINSTANCE.createEClass();
			grandParent.setName("GrandParent");
			EAttribute gpAttr = EcoreFactory.eINSTANCE.createEAttribute();
			gpAttr.setName("baseProp");
			gpAttr.setEType(EcorePackage.Literals.ESTRING);
			grandParent.getEStructuralFeatures().add(gpAttr);
			pkg.getEClassifiers().add(grandParent);

			EClass parent = EcoreFactory.eINSTANCE.createEClass();
			parent.setName("MiddleParent");
			parent.getESuperTypes().add(grandParent);
			EAttribute pAttr = EcoreFactory.eINSTANCE.createEAttribute();
			pAttr.setName("middleProp");
			pAttr.setEType(EcorePackage.Literals.EINT);
			parent.getEStructuralFeatures().add(pAttr);
			pkg.getEClassifiers().add(parent);

			EClass grandChild = EcoreFactory.eINSTANCE.createEClass();
			grandChild.setName("GrandChild");
			grandChild.getESuperTypes().add(parent);
			EAttribute gcAttr = EcoreFactory.eINSTANCE.createEAttribute();
			gcAttr.setName("leafProp");
			gcAttr.setEType(EcorePackage.Literals.EBOOLEAN);
			grandChild.getEStructuralFeatures().add(gcAttr);
			pkg.getEClassifiers().add(grandChild);

			return pkg;
		}
	}

	@Nested
	@DisplayName("OPTION_ALL_FIELDS_REQUIRED")
	class AllFieldsRequiredTests {

		@Test
		@DisplayName("without option: only lowerBound>=1 features appear in required")
		void withoutOption_onlyMandatoryFeaturesRequired() throws IOException {
			EClass eClass = createClass();

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode required = root.get("required");
			assertNotNull(required, "required array should be present");
			assertTrue(requiredContains(required, "mandatory"), "mandatory should be required");
			assertTrue(!requiredContains(required, "optional"), "optional should NOT be required");
		}

		@Test
		@DisplayName("with allFieldsRequired=true: every feature appears in required")
		void withOption_allFeaturesRequired() throws IOException {
			EClass eClass = createClass();

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos, false, true);

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode required = root.get("required");
			assertNotNull(required, "required array should be present");
			assertTrue(requiredContains(required, "mandatory"), "mandatory should be required");
			assertTrue(requiredContains(required, "optional"), "optional should also be required");
		}

		@Test
		@DisplayName("with OPTION_ALL_FIELDS_REQUIRED via Map: every feature required")
		void withOptionMap_allFeaturesRequired() throws IOException {
			EClass eClass = createClass();

			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos, false,
					Map.of(CodecJsonSchemaOptions.OPTION_ALL_FIELDS_REQUIRED, Boolean.TRUE));

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode required = root.get("required");
			assertNotNull(required, "required array should be present");
			assertTrue(requiredContains(required, "optional"), "optional should be required");
		}

		private boolean requiredContains(JsonNode required, String name) {
			for (JsonNode item : required) {
				if (name.equals(item.asString())) return true;
			}
			return false;
		}

		private EClass createClass() {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Item");

			EAttribute mandatory = EcoreFactory.eINSTANCE.createEAttribute();
			mandatory.setName("mandatory");
			mandatory.setEType(EcorePackage.Literals.ESTRING);
			mandatory.setLowerBound(1); // required by lowerBound
			eClass.getEStructuralFeatures().add(mandatory);

			EAttribute optional = EcoreFactory.eINSTANCE.createEAttribute();
			optional.setName("optional");
			optional.setEType(EcorePackage.Literals.ESTRING);
			optional.setLowerBound(0); // not required by lowerBound
			eClass.getEStructuralFeatures().add(optional);

			return eClass;
		}
	}

	// ========================================================================
	// Multi-valued attribute (array) tests
	// ========================================================================

	@Nested
	@DisplayName("Multi-valued attributes (arrays)")
	class MultiValuedAttributeTests {

		@Test
		@DisplayName("multi-valued EString attribute produces array with items type")
		void multiValuedString_hasItemsType() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Document");

			EAttribute tags = EcoreFactory.eINSTANCE.createEAttribute();
			tags.setName("tags");
			tags.setEType(EcorePackage.Literals.ESTRING);
			tags.setUpperBound(-1); // multi-valued
			eClass.getEStructuralFeatures().add(tags);

			JsonNode root = convertEClass(eClass);
			JsonNode tagsProp = root.get("properties").get("tags");

			assertNotNull(tagsProp, "tags property should exist");
			assertEquals("array", tagsProp.get("type").asString());
			assertNotNull(tagsProp.get("items"), "items must be present for array");
			assertEquals("string", tagsProp.get("items").get("type").asString());
		}

		@Test
		@DisplayName("multi-valued EInt attribute produces array with integer items")
		void multiValuedInt_hasItemsType() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Report");

			EAttribute pageReference = EcoreFactory.eINSTANCE.createEAttribute();
			pageReference.setName("pageReference");
			pageReference.setEType(EcorePackage.Literals.EINT);
			pageReference.setUpperBound(-1);
			pageReference.setLowerBound(1);
			eClass.getEStructuralFeatures().add(pageReference);

			JsonNode root = convertEClass(eClass);
			JsonNode prop = root.get("properties").get("pageReference");

			assertNotNull(prop, "pageReference property should exist");
			assertEquals("array", prop.get("type").asString());
			assertEquals(1, prop.get("minItems").asInt());
			assertNotNull(prop.get("items"), "items must be present for array");
			assertEquals("integer", prop.get("items").get("type").asString());
		}

		@Test
		@DisplayName("multi-valued EBoolean attribute produces array with boolean items")
		void multiValuedBoolean_hasItemsType() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Flags");

			EAttribute flags = EcoreFactory.eINSTANCE.createEAttribute();
			flags.setName("values");
			flags.setEType(EcorePackage.Literals.EBOOLEAN);
			flags.setUpperBound(-1);
			eClass.getEStructuralFeatures().add(flags);

			JsonNode root = convertEClass(eClass);
			JsonNode prop = root.get("properties").get("values");

			assertNotNull(prop, "values property should exist");
			assertEquals("array", prop.get("type").asString());
			assertNotNull(prop.get("items"), "items must be present for array");
			assertEquals("boolean", prop.get("items").get("type").asString());
		}

		@Test
		@DisplayName("multi-valued EDouble attribute produces array with number items")
		void multiValuedDouble_hasItemsType() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Measurements");

			EAttribute scores = EcoreFactory.eINSTANCE.createEAttribute();
			scores.setName("scores");
			scores.setEType(EcorePackage.Literals.EDOUBLE);
			scores.setUpperBound(-1);
			eClass.getEStructuralFeatures().add(scores);

			JsonNode root = convertEClass(eClass);
			JsonNode prop = root.get("properties").get("scores");

			assertNotNull(prop, "scores property should exist");
			assertEquals("array", prop.get("type").asString());
			assertNotNull(prop.get("items"), "items must be present for array");
			assertEquals("number", prop.get("items").get("type").asString());
		}

		@Test
		@DisplayName("multi-valued EEnum attribute produces array with enum items")
		void multiValuedEnum_hasItemsWithEnum() throws IOException {
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("test");
			pkg.setNsPrefix("test");
			pkg.setNsURI("http://example.org/test");

			org.eclipse.emf.ecore.EEnum statusEnum = EcoreFactory.eINSTANCE.createEEnum();
			statusEnum.setName("Status");
			org.eclipse.emf.ecore.EEnumLiteral active = EcoreFactory.eINSTANCE.createEEnumLiteral();
			active.setName("ACTIVE");
			active.setLiteral("ACTIVE");
			statusEnum.getELiterals().add(active);
			org.eclipse.emf.ecore.EEnumLiteral inactive = EcoreFactory.eINSTANCE.createEEnumLiteral();
			inactive.setName("INACTIVE");
			inactive.setLiteral("INACTIVE");
			statusEnum.getELiterals().add(inactive);
			pkg.getEClassifiers().add(statusEnum);

			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("History");

			EAttribute statuses = EcoreFactory.eINSTANCE.createEAttribute();
			statuses.setName("statuses");
			statuses.setEType(statusEnum);
			statuses.setUpperBound(-1);
			eClass.getEStructuralFeatures().add(statuses);
			pkg.getEClassifiers().add(eClass);

			JsonNode root = convertEClass(eClass);
			JsonNode prop = root.get("properties").get("statuses");

			assertNotNull(prop, "statuses property should exist");
			assertEquals("array", prop.get("type").asString());
			assertNotNull(prop.get("items"), "items must be present for enum array");
			JsonNode items = prop.get("items");
			assertEquals("string", items.get("type").asString());
			assertNotNull(items.get("enum"), "items should have enum constraint");
			assertEquals(2, items.get("enum").size());
		}

		@Test
		@DisplayName("multi-valued with bounds produces minItems and maxItems")
		void multiValuedWithBounds_hasMinMaxItems() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Team");

			EAttribute members = EcoreFactory.eINSTANCE.createEAttribute();
			members.setName("members");
			members.setEType(EcorePackage.Literals.ESTRING);
			members.setLowerBound(2);
			members.setUpperBound(10);
			eClass.getEStructuralFeatures().add(members);

			JsonNode root = convertEClass(eClass);
			JsonNode prop = root.get("properties").get("members");

			assertNotNull(prop, "members property should exist");
			assertEquals("array", prop.get("type").asString());
			assertEquals(2, prop.get("minItems").asInt());
			assertEquals(10, prop.get("maxItems").asInt());
			assertNotNull(prop.get("items"), "items must be present for array");
			assertEquals("string", prop.get("items").get("type").asString());
		}

		@Test
		@DisplayName("multi-valued containment reference produces array with inlined object items")
		void multiValuedContainmentRef_hasInlinedItems() throws IOException {
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("test");
			pkg.setNsPrefix("test");
			pkg.setNsURI("http://example.org/test");

			EClass child = EcoreFactory.eINSTANCE.createEClass();
			child.setName("Child");
			EAttribute childName = EcoreFactory.eINSTANCE.createEAttribute();
			childName.setName("name");
			childName.setEType(EcorePackage.Literals.ESTRING);
			child.getEStructuralFeatures().add(childName);
			pkg.getEClassifiers().add(child);

			EClass parent = EcoreFactory.eINSTANCE.createEClass();
			parent.setName("Parent");

			EReference children = EcoreFactory.eINSTANCE.createEReference();
			children.setName("children");
			children.setEType(child);
			children.setContainment(true);
			children.setUpperBound(-1);
			parent.getEStructuralFeatures().add(children);
			pkg.getEClassifiers().add(parent);

			EPackageToJsonSchemaConverter converter = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(pkg, baos, "definitions");

			JsonNode schema = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode parentDef = schema.get("definitions").get("Parent");
			JsonNode childrenProp = parentDef.get("properties").get("children");

			assertNotNull(childrenProp, "children property should exist");
			assertEquals("array", childrenProp.get("type").asString());
			assertNotNull(childrenProp.get("items"), "items must be present for array reference");
			// Containment references inline the child object, not $ref
			assertEquals("object", childrenProp.get("items").get("type").asString(),
					"containment items should be inlined object, not $ref");
		}

		private JsonNode convertEClass(EClass eClass) throws IOException {
			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos);
			return JsonMapper.builder().build().readTree(baos.toByteArray());
		}
	}

	// ========================================================================
	// Enum description Tests
	// ========================================================================

	@Nested
	@DisplayName("Enum literal descriptions")
	class EnumLiteralDescriptionTests {

		@Test
		@DisplayName("enum with per-literal documentation combines into description")
		void enumWithLiteralDocs_combinesDescription() throws IOException {
			EPackage pkg = createPackageWithDocumentedEnum(
					"Color options",
					new String[]{"RED", "GREEN", "BLUE"},
					new String[]{"Primary red", "Primary green", "Primary blue"});

			JsonNode schema = convertPackage(pkg);
			JsonNode colorDef = schema.get("definitions").get("Color");

			assertNotNull(colorDef, "Color definition should exist");
			String description = colorDef.get("description").asString();
			assertEquals("Color options. RED=Primary red, GREEN=Primary green, BLUE=Primary blue", description);
		}

		@Test
		@DisplayName("enum with only some literals documented includes only those")
		void enumWithPartialLiteralDocs() throws IOException {
			EPackage pkg = createPackageWithDocumentedEnum(
					"Status values",
					new String[]{"ACTIVE", "INACTIVE", "PENDING"},
					new String[]{"Currently active", null, "Awaiting approval"});

			JsonNode schema = convertPackage(pkg);
			JsonNode statusDef = schema.get("definitions").get("Color");

			String description = statusDef.get("description").asString();
			assertEquals("Status values. ACTIVE=Currently active, PENDING=Awaiting approval", description);
		}

		@Test
		@DisplayName("enum with no literal docs uses only enum-level description")
		void enumWithNoLiteralDocs_usesEnumDescription() throws IOException {
			EPackage pkg = createPackageWithDocumentedEnum(
					"Simple enum",
					new String[]{"A", "B"},
					new String[]{null, null});

			JsonNode schema = convertPackage(pkg);
			JsonNode def = schema.get("definitions").get("Color");

			assertEquals("Simple enum", def.get("description").asString());
		}

		@Test
		@DisplayName("enum with literal docs but no enum description uses only literal docs")
		void enumWithLiteralDocsOnly() throws IOException {
			EPackage pkg = createPackageWithDocumentedEnum(
					null,
					new String[]{"ON", "OFF"},
					new String[]{"Enabled", "Disabled"});

			JsonNode schema = convertPackage(pkg);
			JsonNode def = schema.get("definitions").get("Color");

			assertEquals("ON=Enabled, OFF=Disabled", def.get("description").asString());
		}

		@Test
		@DisplayName("enum with no documentation at all has no description")
		void enumWithNoDocs() throws IOException {
			EPackage pkg = createPackageWithDocumentedEnum(
					null,
					new String[]{"X", "Y"},
					new String[]{null, null});

			JsonNode schema = convertPackage(pkg);
			JsonNode def = schema.get("definitions").get("Color");

			assertNull(def.get("description"), "no description when no documentation at all");
		}

		private EPackage createPackageWithDocumentedEnum(String enumDoc, String[] literalNames, String[] literalDocs) {
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("test");
			pkg.setNsPrefix("test");
			pkg.setNsURI("http://example.org/test");

			org.eclipse.emf.ecore.EEnum colorEnum = EcoreFactory.eINSTANCE.createEEnum();
			colorEnum.setName("Color");

			if (enumDoc != null) {
				EAnnotation genModel = EcoreFactory.eINSTANCE.createEAnnotation();
				genModel.setSource(GEN_MODEL_ANNOTATION_SOURCE);
				genModel.getDetails().put("documentation", enumDoc);
				colorEnum.getEAnnotations().add(genModel);
			}

			for (int i = 0; i < literalNames.length; i++) {
				org.eclipse.emf.ecore.EEnumLiteral literal = EcoreFactory.eINSTANCE.createEEnumLiteral();
				literal.setName(literalNames[i]);
				literal.setLiteral(literalNames[i]);
				if (literalDocs[i] != null) {
					EAnnotation litAnnot = EcoreFactory.eINSTANCE.createEAnnotation();
					litAnnot.setSource(GEN_MODEL_ANNOTATION_SOURCE);
					litAnnot.getDetails().put("documentation", literalDocs[i]);
					literal.getEAnnotations().add(litAnnot);
				}
				colorEnum.getELiterals().add(literal);
			}

			pkg.getEClassifiers().add(colorEnum);
			return pkg;
		}

		private JsonNode convertPackage(EPackage pkg) throws IOException {
			EPackageToJsonSchemaConverter converter = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(pkg, baos, "definitions");
			return JsonMapper.builder().build().readTree(baos.toByteArray());
		}
	}

	// ========================================================================
	// suppressKeywords Tests
	// ========================================================================

	@Nested
	@DisplayName("OPTION_SUPPRESS_KEYWORDS")
	class SuppressKeywordsTests {

		@Test
		@DisplayName("suppress maxItems removes maxItems from bounded array")
		void suppressMaxItems() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Team");

			EAttribute members = EcoreFactory.eINSTANCE.createEAttribute();
			members.setName("members");
			members.setEType(EcorePackage.Literals.ESTRING);
			members.setLowerBound(1);
			members.setUpperBound(5);
			eClass.getEStructuralFeatures().add(members);

			// Without suppression: both minItems and maxItems present
			JsonNode withoutSuppression = convertEClass(eClass, Map.of());
			JsonNode prop = withoutSuppression.get("properties").get("members");
			assertNotNull(prop.get("minItems"), "minItems should be present without suppression");
			assertNotNull(prop.get("maxItems"), "maxItems should be present without suppression");

			// With suppression: maxItems removed, minItems kept
			JsonNode withSuppression = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS, java.util.Set.of("maxItems")));
			JsonNode suppProp = withSuppression.get("properties").get("members");
			assertNotNull(suppProp.get("minItems"), "minItems should still be present");
			assertNull(suppProp.get("maxItems"), "maxItems should be suppressed");
		}

		@Test
		@DisplayName("suppress minItems removes minItems from bounded array")
		void suppressMinItems() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Team");

			EAttribute members = EcoreFactory.eINSTANCE.createEAttribute();
			members.setName("members");
			members.setEType(EcorePackage.Literals.ESTRING);
			members.setLowerBound(2);
			members.setUpperBound(10);
			eClass.getEStructuralFeatures().add(members);

			JsonNode result = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS, java.util.Set.of("minItems")));
			JsonNode prop = result.get("properties").get("members");
			assertNull(prop.get("minItems"), "minItems should be suppressed");
			assertNotNull(prop.get("maxItems"), "maxItems should still be present");
		}

		@Test
		@DisplayName("suppress description removes description from all levels")
		void suppressDescription() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Item");

			// Add GenModel documentation annotation to the class
			EAnnotation genModelAnnot = EcoreFactory.eINSTANCE.createEAnnotation();
			genModelAnnot.setSource(GEN_MODEL_ANNOTATION_SOURCE);
			genModelAnnot.getDetails().put("documentation", "A documented class");
			eClass.getEAnnotations().add(genModelAnnot);

			EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
			name.setName("name");
			name.setEType(EcorePackage.Literals.ESTRING);
			// Add documentation to the attribute
			EAnnotation attrAnnot = EcoreFactory.eINSTANCE.createEAnnotation();
			attrAnnot.setSource(GEN_MODEL_ANNOTATION_SOURCE);
			attrAnnot.getDetails().put("documentation", "The item name");
			name.getEAnnotations().add(attrAnnot);
			eClass.getEStructuralFeatures().add(name);

			// Without suppression: descriptions present
			JsonNode withoutSuppression = convertEClass(eClass, Map.of());
			assertNotNull(withoutSuppression.get("description"), "class description should be present");

			// With suppression: descriptions removed
			JsonNode withSuppression = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS, java.util.Set.of("description")));
			assertNull(withSuppression.get("description"), "class description should be suppressed");
			assertNull(withSuppression.get("properties").get("name").get("description"),
					"attribute description should be suppressed");
		}

		@Test
		@DisplayName("suppress additionalProperties removes additionalProperties")
		void suppressAdditionalProperties() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Item");

			EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
			name.setName("name");
			name.setEType(EcorePackage.Literals.ESTRING);
			eClass.getEStructuralFeatures().add(name);

			// Without suppression: additionalProperties present
			JsonNode withoutSuppression = convertEClass(eClass, Map.of());
			assertNotNull(withoutSuppression.get("additionalProperties"),
					"additionalProperties should be present without suppression");

			// With suppression: additionalProperties removed
			JsonNode withSuppression = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS, java.util.Set.of("additionalProperties")));
			assertNull(withSuppression.get("additionalProperties"),
					"additionalProperties should be suppressed");
		}

		@Test
		@DisplayName("suppress multiple keywords at once")
		void suppressMultipleKeywords() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Data");

			EAnnotation genModelAnnot = EcoreFactory.eINSTANCE.createEAnnotation();
			genModelAnnot.setSource(GEN_MODEL_ANNOTATION_SOURCE);
			genModelAnnot.getDetails().put("documentation", "Some data");
			eClass.getEAnnotations().add(genModelAnnot);

			EAttribute values = EcoreFactory.eINSTANCE.createEAttribute();
			values.setName("values");
			values.setEType(EcorePackage.Literals.EINT);
			values.setLowerBound(1);
			values.setUpperBound(100);
			eClass.getEStructuralFeatures().add(values);

			JsonNode result = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS,
							java.util.Set.of("description", "minItems", "maxItems", "additionalProperties")));

			assertNull(result.get("description"), "description should be suppressed");
			assertNull(result.get("additionalProperties"), "additionalProperties should be suppressed");

			JsonNode prop = result.get("properties").get("values");
			assertNull(prop.get("minItems"), "minItems should be suppressed");
			assertNull(prop.get("maxItems"), "maxItems should be suppressed");
			// items and type should still be present
			assertNotNull(prop.get("type"), "type should not be suppressed");
			assertNotNull(prop.get("items"), "items should not be suppressed");
		}

		@Test
		@DisplayName("suppress $comment removes $comment from schema")
		void suppressComment() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Item");

			EAnnotation commentAnnot = EcoreFactory.eINSTANCE.createEAnnotation();
			commentAnnot.setSource(JSONSCHEMA_ANNOTATION_SOURCE);
			commentAnnot.getDetails().put("comment", "Internal use only");
			eClass.getEAnnotations().add(commentAnnot);

			// Without suppression
			JsonNode withoutSuppression = convertEClass(eClass, Map.of());
			assertNotNull(withoutSuppression.get("$comment"), "$comment should be present");

			// With suppression
			JsonNode withSuppression = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS, java.util.Set.of("$comment")));
			assertNull(withSuppression.get("$comment"), "$comment should be suppressed");
		}

		private JsonNode convertEClass(EClass eClass, Map<String, Object> options) throws IOException {
			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos, false, options);
			return JsonMapper.builder().build().readTree(baos.toByteArray());
		}
	}

	// ========================================================================
	// $schema draft option tests
	// ========================================================================

	@Nested
	@DisplayName("OPTION_SCHEMA_DRAFT")
	class SchemaDraftTests {

		@Test
		@DisplayName("EClass: no annotation and no option → $schema absent")
		void eClass_noAnnotationNoOption_schemaAbsent() throws IOException {
			EClass eClass = createSimpleClass();

			JsonNode root = convertEClass(eClass, Map.of());
			assertNull(root.get("$schema"), "$schema should not be present");
		}

		@Test
		@DisplayName("EClass: draft shorthand '2020-12' → canonical URI")
		void eClass_draftShorthand_canonicalUri() throws IOException {
			EClass eClass = createSimpleClass();

			JsonNode root = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "2020-12"));
			assertNotNull(root.get("$schema"), "$schema should be present");
			assertEquals("https://json-schema.org/draft/2020-12/schema", root.get("$schema").asString());
		}

		@Test
		@DisplayName("EClass: draft shorthand 'draft-07' → canonical URI")
		void eClass_draft07_canonicalUri() throws IOException {
			EClass eClass = createSimpleClass();

			JsonNode root = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "draft-07"));
			assertNotNull(root.get("$schema"), "$schema should be present");
			assertEquals("http://json-schema.org/draft-07/schema#", root.get("$schema").asString());
		}

		@Test
		@DisplayName("EClass: full custom URI passed through as-is")
		void eClass_fullUri_passedThrough() throws IOException {
			EClass eClass = createSimpleClass();
			String customUri = "https://example.com/my-custom-schema";

			JsonNode root = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, customUri));
			assertNotNull(root.get("$schema"), "$schema should be present");
			assertEquals(customUri, root.get("$schema").asString());
		}

		@Test
		@DisplayName("EClass: annotation takes precedence over option")
		void eClass_annotationOverOption() throws IOException {
			EClass eClass = createSimpleClass();
			String annotationSchema = "http://json-schema.org/draft-04/schema#";
			EAnnotation annot = EcoreFactory.eINSTANCE.createEAnnotation();
			annot.setSource(JSONSCHEMA_ANNOTATION_SOURCE);
			annot.getDetails().put("schema", annotationSchema);
			eClass.getEAnnotations().add(annot);

			JsonNode root = convertEClass(eClass,
					Map.of(CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "2020-12"));
			assertEquals(annotationSchema, root.get("$schema").asString(),
					"annotation should take precedence over option");
		}

		@Test
		@DisplayName("EPackage: draft shorthand '2019-09' → canonical URI")
		void ePackage_draftShorthand_canonicalUri() throws IOException {
			EPackage pkg = createSimplePackage();

			JsonNode root = convertEPackage(pkg,
					Map.of(CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "2019-09"));
			assertNotNull(root.get("$schema"), "$schema should be present");
			assertEquals("https://json-schema.org/draft/2019-09/schema", root.get("$schema").asString());
		}

		@Test
		@DisplayName("EPackage: annotation takes precedence over option")
		void ePackage_annotationOverOption() throws IOException {
			EPackage pkg = createSimplePackage();
			String annotationSchema = "http://json-schema.org/draft-06/schema#";
			EAnnotation annot = EcoreFactory.eINSTANCE.createEAnnotation();
			annot.setSource(JSONSCHEMA_ANNOTATION_SOURCE);
			annot.getDetails().put("schema", annotationSchema);
			pkg.getEAnnotations().add(annot);

			JsonNode root = convertEPackage(pkg,
					Map.of(CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "2020-12"));
			assertEquals(annotationSchema, root.get("$schema").asString(),
					"annotation should take precedence over option");
		}

		private EClass createSimpleClass() {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Thing");
			EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
			attr.setName("name");
			attr.setEType(EcorePackage.Literals.ESTRING);
			eClass.getEStructuralFeatures().add(attr);
			return eClass;
		}

		private EPackage createSimplePackage() {
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("testpkg");
			pkg.setNsURI("http://example.org/testpkg");
			pkg.setNsPrefix("tp");
			pkg.getEClassifiers().add(createSimpleClass());
			return pkg;
		}

		private JsonNode convertEClass(EClass eClass, Map<String, Object> options) throws IOException {
			EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(eClass, baos, false, options);
			return JsonMapper.builder().build().readTree(baos.toByteArray());
		}

		private JsonNode convertEPackage(EPackage pkg, Map<String, Object> options) throws IOException {
			EPackageToJsonSchemaConverter converter = new EPackageToJsonSchemaConverter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			converter.convert(pkg, baos, "$defs", false, options);
			return JsonMapper.builder().build().readTree(baos.toByteArray());
		}
	}

	// ========================================================================
	// Resource-level option flow tests
	// ========================================================================

	@Nested
	@DisplayName("Options flow through JsonSchemaResourceImpl")
	class ResourceOptionFlowTests {

		@Test
		@DisplayName("allFieldsRequired flows through resource save options")
		void allFieldsRequired_flowsThroughResource() throws IOException {
			EClass eClass = createClassWithOptionalField();
			EPackage pkg = wrapInPackage(eClass);

			// Save via resource with allFieldsRequired option
			JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
					org.eclipse.emf.common.util.URI.createURI("test.jsonschema"),
					org.eclipse.fennec.codec.util.MetadataServiceFactory.create());
			resource.getContents().add(pkg);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			resource.save(baos, Map.of(
					CodecJsonSchemaOptions.OPTION_ALL_FIELDS_REQUIRED, Boolean.TRUE));

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode defs = root.get("$defs");
			assertNotNull(defs, "$defs should be present");
			JsonNode classDef = defs.get("Item");
			assertNotNull(classDef, "Item definition should be present");
			JsonNode required = classDef.get("required");
			assertNotNull(required, "required array should be present");
			assertTrue(requiredContains(required, "optional"),
					"optional field should be required when allFieldsRequired is set");
		}

		@Test
		@DisplayName("schemaDraft flows through resource save options")
		void schemaDraft_flowsThroughResource() throws IOException {
			EClass eClass = createClassWithOptionalField();
			EPackage pkg = wrapInPackage(eClass);

			JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
					org.eclipse.emf.common.util.URI.createURI("test.jsonschema"),
					org.eclipse.fennec.codec.util.MetadataServiceFactory.create());
			resource.getContents().add(pkg);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			resource.save(baos, Map.of(
					CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "2020-12"));

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			assertNotNull(root.get("$schema"), "$schema should be present");
			assertEquals("https://json-schema.org/draft/2020-12/schema",
					root.get("$schema").asString());
		}

		@Test
		@DisplayName("suppressKeywords flows through resource save options")
		void suppressKeywords_flowsThroughResource() throws IOException {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Documented");
			EAnnotation docAnnot = EcoreFactory.eINSTANCE.createEAnnotation();
			docAnnot.setSource(GEN_MODEL_ANNOTATION_SOURCE);
			docAnnot.getDetails().put("documentation", "This should be suppressed");
			eClass.getEAnnotations().add(docAnnot);

			EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
			attr.setName("value");
			attr.setEType(EcorePackage.Literals.ESTRING);
			eClass.getEStructuralFeatures().add(attr);

			EPackage pkg = wrapInPackage(eClass);

			JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
					org.eclipse.emf.common.util.URI.createURI("test.jsonschema"),
					org.eclipse.fennec.codec.util.MetadataServiceFactory.create());
			resource.getContents().add(pkg);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			resource.save(baos, Map.of(
					CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS,
					java.util.Set.of("description")));

			JsonNode root = JsonMapper.builder().build().readTree(baos.toByteArray());
			JsonNode classDef = root.get("$defs").get("Documented");
			assertNotNull(classDef, "Documented definition should be present");
			assertNull(classDef.get("description"),
					"description should be suppressed via resource options");
		}

		private boolean requiredContains(JsonNode required, String name) {
			for (JsonNode item : required) {
				if (name.equals(item.asString())) return true;
			}
			return false;
		}

		private EClass createClassWithOptionalField() {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName("Item");
			EAttribute optional = EcoreFactory.eINSTANCE.createEAttribute();
			optional.setName("optional");
			optional.setEType(EcorePackage.Literals.ESTRING);
			optional.setLowerBound(0);
			eClass.getEStructuralFeatures().add(optional);
			return eClass;
		}

		private EPackage wrapInPackage(EClass eClass) {
			EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
			pkg.setName("testpkg");
			pkg.setNsURI("http://example.org/testpkg");
			pkg.setNsPrefix("tp");
			pkg.getEClassifiers().add(eClass);
			return pkg;
		}
	}
}
