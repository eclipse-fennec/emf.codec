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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Issue #215 - {@code $id} and {@code $schema} must honour
 * {@link CodecJsonSchemaOptions#OPTION_SUPPRESS_KEYWORDS} like every other keyword.
 * <p>
 * Both were written unconditionally, so a consumer that cannot accept them (several AI
 * structured-output endpoints reject unknown or absolute identifiers) had no way to get
 * rid of them.
 * </p>
 */
@DisplayName("Issue #215 - $id and $schema honour OPTION_SUPPRESS_KEYWORDS")
class SuppressedSchemaKeywordsTest {

	private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";

	/**
	 * {@code $schema} is only written when a draft is configured (or annotated), so every
	 * case here starts from a draft to have something to suppress.
	 */
	private static final Map<String, Object> DRAFT = Map.of(
			CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "2020-12");

	private static Map<String, Object> draftSuppressing(String... keywords) {
		return Map.of(CodecJsonSchemaOptions.OPTION_SCHEMA_DRAFT, "2020-12",
				CodecJsonSchemaOptions.OPTION_SUPPRESS_KEYWORDS, Set.of(keywords));
	}

	@Nested
	@DisplayName("EPackage document")
	class PackageDocument {

		@Test
		@DisplayName("both keywords are written when nothing is suppressed")
		void writtenByDefault() throws IOException {
			JsonNode schema = convertPackage(samplePackage(), DRAFT);

			assertTrue(schema.has("$schema"), "$schema should be written by default");
			assertTrue(schema.has("$id"), "$id should be written by default");
		}

		@Test
		@DisplayName("suppressing $id removes it and keeps the rest")
		void suppressId() throws IOException {
			JsonNode schema = convertPackage(samplePackage(), draftSuppressing("$id"));

			assertFalse(schema.has("$id"), "$id should be suppressed");
			assertTrue(schema.has("$schema"), "$schema should still be written");
			assertEquals("test", schema.get("title").asString(), "title should still be written");
		}

		@Test
		@DisplayName("suppressing $schema removes it and keeps the rest")
		void suppressSchema() throws IOException {
			JsonNode schema = convertPackage(samplePackage(), draftSuppressing("$schema"));

			assertFalse(schema.has("$schema"), "$schema should be suppressed");
			assertTrue(schema.has("$id"), "$id should still be written");
		}

		@Test
		@DisplayName("suppressing both removes both")
		void suppressBoth() throws IOException {
			JsonNode schema = convertPackage(samplePackage(), draftSuppressing("$id", "$schema"));

			assertFalse(schema.has("$id"), "$id should be suppressed");
			assertFalse(schema.has("$schema"), "$schema should be suppressed");
		}

		@Test
		@DisplayName("an explicit annotation does not defeat the suppression")
		void annotationDoesNotDefeatSuppression() throws IOException {
			EPackage ePackage = samplePackage();
			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(JSONSCHEMA_ANNOTATION_SOURCE);
			annotation.getDetails().put("schema", "https://json-schema.org/draft/2020-12/schema");
			ePackage.getEAnnotations().add(annotation);

			JsonNode schema = convertPackage(ePackage, draftSuppressing("$schema"));

			assertFalse(schema.has("$schema"), "$schema should be suppressed even when annotated");
		}
	}

	@Nested
	@DisplayName("single-EClass document")
	class EClassDocument {

		@Test
		@DisplayName("both keywords are written when nothing is suppressed")
		void writtenByDefault() throws IOException {
			JsonNode schema = convertEClass(sampleEClass(), DRAFT);

			assertTrue(schema.has("$schema"), "$schema should be written by default");
			assertTrue(schema.has("$id"), "$id should be written by default");
		}

		@Test
		@DisplayName("suppressing $id removes it and keeps the rest")
		void suppressId() throws IOException {
			JsonNode schema = convertEClass(sampleEClass(), draftSuppressing("$id"));

			assertFalse(schema.has("$id"), "$id should be suppressed");
			assertTrue(schema.has("$schema"), "$schema should still be written");
			assertEquals("Person", schema.get("title").asString(), "title should still be written");
		}

		@Test
		@DisplayName("suppressing $schema removes it and keeps the rest")
		void suppressSchema() throws IOException {
			JsonNode schema = convertEClass(sampleEClass(), draftSuppressing("$schema"));

			assertFalse(schema.has("$schema"), "$schema should be suppressed");
			assertTrue(schema.has("$id"), "$id should still be written");
		}

		@Test
		@DisplayName("an explicit id annotation does not defeat the suppression")
		void annotationDoesNotDefeatSuppression() throws IOException {
			EClass person = sampleEClass();
			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(JSONSCHEMA_ANNOTATION_SOURCE);
			annotation.getDetails().put("id", "https://schemas.example.com/person.json");
			person.getEAnnotations().add(annotation);

			JsonNode schema = convertEClass(person, draftSuppressing("$id"));

			assertFalse(schema.has("$id"), "$id should be suppressed even when annotated");
		}
	}

	private static EPackage samplePackage() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("test");
		ePackage.setNsPrefix("test");
		ePackage.setNsURI("http://example.org/test");
		ePackage.getEClassifiers().add(person());
		return ePackage;
	}

	private static EClass sampleEClass() {
		return samplePackage().getEClassifiers().stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.findFirst()
				.orElseThrow();
	}

	private static EClass person() {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName("Person");

		EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(name);
		return eClass;
	}

	private static JsonNode convertPackage(EPackage ePackage, Map<String, Object> options) throws IOException {
		EPackageToJsonSchemaConverter converter = new EPackageToJsonSchemaConverter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		converter.convert(ePackage, baos, "$defs", false, options);
		return JsonMapper.builder().build().readTree(baos.toByteArray());
	}

	private static JsonNode convertEClass(EClass eClass, Map<String, Object> options) throws IOException {
		EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		converter.convert(eClass, baos, false, options);
		return JsonMapper.builder().build().readTree(baos.toByteArray());
	}
}
