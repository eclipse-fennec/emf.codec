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
package org.eclipse.fennec.codec.jsonschema.v2.converter.ocl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.jsonschema.v2.JsonSchemaResourceImpl;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaConversionDiagnostic;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link JsonSchemaOclConstraintGenerator}: compiling JSON Schema
 * assertion keywords (Phase 2) and {@code format} (Phase 4) into OCL
 * invariants, wired via EMF's standard validation-delegate annotation
 * convention.
 */
@DisplayName("JSON Schema OCL Constraint Generation")
class JsonSchemaOclConstraintGenerationTest {

	private static final String ECORE_SOURCE = EcorePackage.eNS_URI;
	private static final String DEFAULT_DELEGATE_URI = CodecJsonSchemaOptions.DEFAULT_OCL_DELEGATE_URI;

	private final EcoreFactory factory = EcoreFactory.eINSTANCE;

	// ========================================================================
	// Fixture helpers
	// ========================================================================

	private EPackage newPackage(String name) {
		EPackage ePackage = factory.createEPackage();
		ePackage.setName(name);
		ePackage.setNsURI("http://example.org/" + name);
		ePackage.setNsPrefix(name);
		return ePackage;
	}

	private EClass newClass(EPackage ePackage, String name) {
		EClass eClass = factory.createEClass();
		eClass.setName(name);
		if (ePackage != null) {
			ePackage.getEClassifiers().add(eClass);
		}
		return eClass;
	}

	private EAttribute newAttribute(EClass eClass, String name, org.eclipse.emf.ecore.EDataType type) {
		EAttribute attribute = factory.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		eClass.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private void annotate(org.eclipse.emf.ecore.EModelElement element, String key, String rawValue) {
		EAnnotation annotation = element.getEAnnotation(AnnotationSources.JSONSCHEMA);
		if (annotation == null) {
			annotation = factory.createEAnnotation();
			annotation.setSource(AnnotationSources.JSONSCHEMA);
			element.getEAnnotations().add(annotation);
		}
		annotation.getDetails().put(key, rawValue);
	}

	private String constraintExpr(EClass eClass, String delegateUri, String invariantName) {
		EAnnotation ann = eClass.getEAnnotation(delegateUri);
		assertNotNull(ann, "Expected delegate annotation with source " + delegateUri);
		return ann.getDetails().get(invariantName);
	}

	// ========================================================================
	// Phase 2: assertion keywords
	// ========================================================================

	@Nested
	@DisplayName("Phase 2 assertion keywords")
	class Phase2Keywords {

		@Test
		@DisplayName("minLength / maxLength compile to size() invariants")
		void minMaxLength() {
			EPackage pkg = newPackage("p");
			EClass person = newClass(pkg, "Person");
			EAttribute name = newAttribute(person, "name", EcorePackage.Literals.ESTRING);
			annotate(name, "minLength", "2");
			annotate(name, "maxLength", "50");

			List<JsonSchemaConversionDiagnostic> diagnostics =
					new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertTrue(diagnostics.isEmpty());
			assertEquals("self.name.size() >= 2", constraintExpr(person, DEFAULT_DELEGATE_URI, "name_minLength"));
			assertEquals("self.name.size() <= 50", constraintExpr(person, DEFAULT_DELEGATE_URI, "name_maxLength"));
		}

		@Test
		@DisplayName("pattern compiles to matches() invariant")
		void pattern() {
			EPackage pkg = newPackage("p");
			EClass person = newClass(pkg, "Person");
			EAttribute email = newAttribute(person, "email", EcorePackage.Literals.ESTRING);
			annotate(email, "pattern", "\"^[A-Z]+$\"");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals("self.email.matches('^[A-Z]+$')",
					constraintExpr(person, DEFAULT_DELEGATE_URI, "email_pattern"));
		}

		@Test
		@DisplayName("pattern containing a single quote is escaped as \\' (m2x grammar has no doubled-quote escape)")
		void patternWithQuoteIsEscaped() {
			EPackage pkg = newPackage("p");
			EClass person = newClass(pkg, "Person");
			EAttribute nickname = newAttribute(person, "nickname", EcorePackage.Literals.ESTRING);
			annotate(nickname, "pattern", "\"^don't panic$\"");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals("self.nickname.matches('^don\\'t panic$')",
					constraintExpr(person, DEFAULT_DELEGATE_URI, "nickname_pattern"));
		}

		@Test
		@DisplayName("pattern containing a regex backslash escape is doubled for the OCL string literal")
		void patternWithBackslashIsEscaped() {
			EPackage pkg = newPackage("p");
			EClass person = newClass(pkg, "Person");
			EAttribute code = newAttribute(person, "code", EcorePackage.Literals.ESTRING);
			// JSON-encoded "^\d+$" (a regex matching one-or-more digits)
			annotate(code, "pattern", "\"^\\\\d+$\"");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals("self.code.matches('^\\\\d+$')",
					constraintExpr(person, DEFAULT_DELEGATE_URI, "code_pattern"));
		}

		@Test
		@DisplayName("minimum / maximum compile to comparison invariants")
		void minMax() {
			EPackage pkg = newPackage("p");
			EClass person = newClass(pkg, "Person");
			EAttribute age = newAttribute(person, "age", EcorePackage.Literals.EINT);
			annotate(age, "minimum", "0");
			annotate(age, "maximum", "150");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals("self.age >= 0", constraintExpr(person, DEFAULT_DELEGATE_URI, "age_minimum"));
			assertEquals("self.age <= 150", constraintExpr(person, DEFAULT_DELEGATE_URI, "age_maximum"));
		}

		@Test
		@DisplayName("exclusiveMinimum / exclusiveMaximum (Draft 2020-12 numeric form)")
		void exclusiveMinMax() {
			EPackage pkg = newPackage("p");
			EClass measurement = newClass(pkg, "Measurement");
			EAttribute value = newAttribute(measurement, "value", EcorePackage.Literals.EDOUBLE);
			annotate(value, "exclusiveMinimum", "0");
			annotate(value, "exclusiveMaximum", "100.5");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals("self.value > 0", constraintExpr(measurement, DEFAULT_DELEGATE_URI, "value_exclusiveMinimum"));
			assertEquals("self.value < 100.5", constraintExpr(measurement, DEFAULT_DELEGATE_URI, "value_exclusiveMaximum"));
		}

		@Test
		@DisplayName("exclusiveMinimum in legacy Draft-04 boolean form is skipped with a diagnostic")
		void exclusiveMinimumBooleanFormSkipped() {
			EPackage pkg = newPackage("p");
			EClass measurement = newClass(pkg, "Measurement");
			EAttribute value = newAttribute(measurement, "value", EcorePackage.Literals.EDOUBLE);
			annotate(value, "exclusiveMinimum", "true");

			List<JsonSchemaConversionDiagnostic> diagnostics =
					new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals(1, diagnostics.size());
			assertEquals(JsonSchemaConversionDiagnostic.Code.OCL_GENERATION_SKIPPED, diagnostics.get(0).getCode());
			assertNull(measurement.getEAnnotation(DEFAULT_DELEGATE_URI));
		}

		@Test
		@DisplayName("multipleOf on an integer feature compiles to mod() invariant")
		void multipleOfInteger() {
			EPackage pkg = newPackage("p");
			EClass order = newClass(pkg, "Order");
			EAttribute quantity = newAttribute(order, "quantity", EcorePackage.Literals.EINT);
			annotate(quantity, "multipleOf", "5");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals("self.quantity.mod(5) = 0", constraintExpr(order, DEFAULT_DELEGATE_URI, "quantity_multipleOf"));
		}

		@Test
		@DisplayName("multipleOf on a non-integer feature is skipped with a diagnostic")
		void multipleOfNonIntegerSkipped() {
			EPackage pkg = newPackage("p");
			EClass order = newClass(pkg, "Order");
			EAttribute price = newAttribute(order, "price", EcorePackage.Literals.EDOUBLE);
			annotate(price, "multipleOf", "0.01");

			List<JsonSchemaConversionDiagnostic> diagnostics =
					new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals(1, diagnostics.size());
			assertEquals(JsonSchemaConversionDiagnostic.Code.OCL_GENERATION_SKIPPED, diagnostics.get(0).getCode());
			assertNull(order.getEAnnotation(DEFAULT_DELEGATE_URI));
		}

		@Test
		@DisplayName("uniqueItems on a multi-valued feature compiles to isUnique() invariant")
		void uniqueItemsOnCollection() {
			EPackage pkg = newPackage("p");
			EClass team = newClass(pkg, "Team");
			EAttribute tags = newAttribute(team, "tags", EcorePackage.Literals.ESTRING);
			tags.setUpperBound(-1);
			annotate(tags, "uniqueItems", "true");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals("self.tags->isUnique(e | e)", constraintExpr(team, DEFAULT_DELEGATE_URI, "tags_uniqueItems"));
		}

		@Test
		@DisplayName("uniqueItems=false generates no invariant and no diagnostic")
		void uniqueItemsFalseIsNoOp() {
			EPackage pkg = newPackage("p");
			EClass team = newClass(pkg, "Team");
			EAttribute tags = newAttribute(team, "tags", EcorePackage.Literals.ESTRING);
			tags.setUpperBound(-1);
			annotate(tags, "uniqueItems", "false");

			List<JsonSchemaConversionDiagnostic> diagnostics =
					new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertTrue(diagnostics.isEmpty());
			assertNull(team.getEAnnotation(DEFAULT_DELEGATE_URI));
		}

		@Test
		@DisplayName("uniqueItems on a single-valued feature is skipped with a diagnostic")
		void uniqueItemsOnSingleValuedSkipped() {
			EPackage pkg = newPackage("p");
			EClass team = newClass(pkg, "Team");
			EAttribute tag = newAttribute(team, "tag", EcorePackage.Literals.ESTRING);
			annotate(tag, "uniqueItems", "true");

			List<JsonSchemaConversionDiagnostic> diagnostics =
					new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertEquals(1, diagnostics.size());
			assertEquals(JsonSchemaConversionDiagnostic.Code.OCL_GENERATION_SKIPPED, diagnostics.get(0).getCode());
		}
	}

	// ========================================================================
	// Phase 4: format (uuid / email)
	// ========================================================================

	@Nested
	@DisplayName("Phase 4 format keyword")
	class Phase4Format {

		@Test
		@DisplayName("format: uuid compiles to a regex invariant")
		void formatUuid() {
			EPackage pkg = newPackage("p");
			EClass entity = newClass(pkg, "Entity");
			EAttribute id = newAttribute(entity, "id", EcorePackage.Literals.ESTRING);
			annotate(id, "format", "uuid");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			String expr = constraintExpr(entity, DEFAULT_DELEGATE_URI, "id_format");
			assertTrue(expr.startsWith("self.id.matches('"), expr);
			assertNoUnescapedBackslash(expr);
		}

		@Test
		@DisplayName("format: email compiles to a regex invariant")
		void formatEmail() {
			EPackage pkg = newPackage("p");
			EClass contact = newClass(pkg, "Contact");
			EAttribute email = newAttribute(contact, "email", EcorePackage.Literals.ESTRING);
			annotate(email, "format", "email");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			String expr = constraintExpr(contact, DEFAULT_DELEGATE_URI, "email_format");
			assertTrue(expr.startsWith("self.email.matches('"), expr);
			// The email regex contains \s and \. - every backslash must be doubled for the
			// m2x OCL grammar's STRING_LITERAL rule (Ocl.g4), which has no bare \s/\. escape.
			assertNoUnescapedBackslash(expr);
		}

		/**
		 * Asserts every backslash in an OCL string literal is part of a doubled
		 * escape pair ({@code \\}), matching the m2x grammar's STRING_LITERAL rule.
		 */
		private void assertNoUnescapedBackslash(String oclExpression) {
			String withoutEscapedBackslashes = oclExpression.replace("\\\\", "");
			assertFalse(withoutEscapedBackslashes.contains("\\"),
					"Found an unescaped backslash in generated OCL: " + oclExpression);
		}

		@Test
		@DisplayName("unrecognized format is left alone: no invariant, no diagnostic")
		void unrecognizedFormatIsNoOp() {
			EPackage pkg = newPackage("p");
			EClass entity = newClass(pkg, "Entity");
			EAttribute note = newAttribute(entity, "note", EcorePackage.Literals.ESTRING);
			annotate(note, "format", "some-custom-format");

			List<JsonSchemaConversionDiagnostic> diagnostics =
					new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertTrue(diagnostics.isEmpty());
			assertNull(entity.getEAnnotation(DEFAULT_DELEGATE_URI));
		}
	}

	// ========================================================================
	// Annotation wiring / EMF validation-delegate convention
	// ========================================================================

	@Nested
	@DisplayName("Validation delegate annotation wiring")
	class DelegateWiring {

		@Test
		@DisplayName("writes constraints list and package-level validationDelegates")
		void writesFullWiringTriple() {
			EPackage pkg = newPackage("p");
			EClass person = newClass(pkg, "Person");
			EAttribute age = newAttribute(person, "age", EcorePackage.Literals.EINT);
			annotate(age, "minimum", "0");

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			EAnnotation classEcoreAnn = person.getEAnnotation(ECORE_SOURCE);
			assertNotNull(classEcoreAnn);
			assertEquals("age_minimum", classEcoreAnn.getDetails().get("constraints"));

			EAnnotation pkgEcoreAnn = pkg.getEAnnotation(ECORE_SOURCE);
			assertNotNull(pkgEcoreAnn);
			assertEquals(DEFAULT_DELEGATE_URI, pkgEcoreAnn.getDetails().get("validationDelegates"));
		}

		@Test
		@DisplayName("merges into pre-existing constraints / validationDelegates rather than overwriting")
		void mergesWithExistingAnnotations() {
			EPackage pkg = newPackage("p");
			EClass person = newClass(pkg, "Person");
			EAttribute age = newAttribute(person, "age", EcorePackage.Literals.EINT);
			annotate(age, "minimum", "0");

			EAnnotation existingClassAnn = factory.createEAnnotation();
			existingClassAnn.setSource(ECORE_SOURCE);
			existingClassAnn.getDetails().put("constraints", "someOtherInvariant");
			person.getEAnnotations().add(existingClassAnn);

			EAnnotation existingPkgAnn = factory.createEAnnotation();
			existingPkgAnn.setSource(ECORE_SOURCE);
			existingPkgAnn.getDetails().put("validationDelegates", "http://example.org/other-delegate");
			pkg.getEAnnotations().add(existingPkgAnn);

			new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			String constraints = person.getEAnnotation(ECORE_SOURCE).getDetails().get("constraints");
			assertTrue(constraints.contains("someOtherInvariant"));
			assertTrue(constraints.contains("age_minimum"));

			String delegates = pkg.getEAnnotation(ECORE_SOURCE).getDetails().get("validationDelegates");
			assertTrue(delegates.contains("http://example.org/other-delegate"));
			assertTrue(delegates.contains(DEFAULT_DELEGATE_URI));
		}

		@Test
		@DisplayName("custom delegate URI is used instead of the default")
		void customDelegateUri() {
			EPackage pkg = newPackage("p");
			EClass person = newClass(pkg, "Person");
			EAttribute age = newAttribute(person, "age", EcorePackage.Literals.EINT);
			annotate(age, "minimum", "0");

			String customUri = "http://www.eclipse.org/fennec/m2x/ocl/1.0";
			new JsonSchemaOclConstraintGenerator().generate(pkg, customUri);

			assertEquals("self.age >= 0", constraintExpr(person, customUri, "age_minimum"));
			assertEquals(customUri, pkg.getEAnnotation(ECORE_SOURCE).getDetails().get("validationDelegates"));
		}

		@Test
		@DisplayName("a class with no assertion keywords gets no annotations at all")
		void classWithNoKeywordsUntouched() {
			EPackage pkg = newPackage("p");
			EClass plain = newClass(pkg, "Plain");
			newAttribute(plain, "name", EcorePackage.Literals.ESTRING);

			List<JsonSchemaConversionDiagnostic> diagnostics =
					new JsonSchemaOclConstraintGenerator().generate(pkg, DEFAULT_DELEGATE_URI);

			assertTrue(diagnostics.isEmpty());
			assertNull(plain.getEAnnotation(ECORE_SOURCE));
			assertNull(pkg.getEAnnotation(ECORE_SOURCE));
		}

		@Test
		@DisplayName("EClass with no owning EPackage still gets class-level annotations, plus a diagnostic")
		void eClassWithoutPackage() {
			EClass standalone = newClass(null, "Standalone");
			EAttribute age = newAttribute(standalone, "age", EcorePackage.Literals.EINT);
			annotate(age, "minimum", "0");

			List<JsonSchemaConversionDiagnostic> diagnostics =
					new JsonSchemaOclConstraintGenerator().generate(standalone, DEFAULT_DELEGATE_URI);

			assertEquals("self.age >= 0", constraintExpr(standalone, DEFAULT_DELEGATE_URI, "age_minimum"));
			assertEquals(1, diagnostics.size());
			assertEquals(JsonSchemaConversionDiagnostic.Code.OCL_GENERATION_SKIPPED, diagnostics.get(0).getCode());
		}
	}

	// ========================================================================
	// End-to-end wiring through JsonSchemaResourceImpl
	// ========================================================================

	@Nested
	@DisplayName("JsonSchemaResourceImpl.doLoad wiring")
	class ResourceIntegration {

		private EPackage loadJsonSchema(String json, Map<String, Object> extraOptions) throws IOException {
			MetadataWhiteboard metadataService = MetadataServiceFactory.create();
			JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
					URI.createURI("schema.jsonschema"), metadataService);

			Map<String, Object> options = new HashMap<>(extraOptions);
			try (var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
				resource.load(is, options);
			}
			return (EPackage) resource.getContents().get(0);
		}

		private static final String SCHEMA = """
			{
				"$id": "http://example.org/test",
				"$defs": {
					"Person": {
						"type": "object",
						"properties": {
							"age": { "type": "integer", "minimum": 0 }
						}
					}
				}
			}
			""";

		@Test
		@DisplayName("option off by default: no OCL annotations are generated")
		void optionOffByDefault() throws IOException {
			EPackage pkg = loadJsonSchema(SCHEMA, Map.of());

			EClass person = (EClass) pkg.getEClassifier("Person");
			assertNotNull(person);
			assertNull(person.getEAnnotation(DEFAULT_DELEGATE_URI));
			assertNull(pkg.getEAnnotation(ECORE_SOURCE));
		}

		@Test
		@DisplayName("option on: minimum keyword is compiled into an OCL invariant end-to-end")
		void optionOnGeneratesInvariant() throws IOException {
			EPackage pkg = loadJsonSchema(SCHEMA,
					Map.of(CodecJsonSchemaOptions.OPTION_GENERATE_OCL_CONSTRAINTS, Boolean.TRUE));

			EClass person = (EClass) pkg.getEClassifier("Person");
			EAnnotation delegateAnn = person.getEAnnotation(DEFAULT_DELEGATE_URI);
			assertNotNull(delegateAnn);
			assertEquals("self.age >= 0", delegateAnn.getDetails().get("age_minimum"));
		}

		@Test
		@DisplayName("custom delegate URI option is honored end-to-end")
		void customDelegateUriOptionHonored() throws IOException {
			String customUri = "http://www.eclipse.org/fennec/m2x/ocl/1.0";
			EPackage pkg = loadJsonSchema(SCHEMA, Map.of(
					CodecJsonSchemaOptions.OPTION_GENERATE_OCL_CONSTRAINTS, Boolean.TRUE,
					CodecJsonSchemaOptions.OPTION_OCL_DELEGATE_URI, customUri));

			EClass person = (EClass) pkg.getEClassifier("Person");
			assertNotNull(person.getEAnnotation(customUri));
			assertNull(person.getEAnnotation(DEFAULT_DELEGATE_URI));
		}
	}
}
