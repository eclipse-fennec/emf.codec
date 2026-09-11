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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.jsonschema.v2.JsonSchemaResourceImpl;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Issue #214 - transient, derived and volatile features must not appear in the
 * generated schema.
 * <p>
 * The generated schema describes the documents the codec reads and writes, and the
 * codec's visibility gate ({@code FeatureConfig.shouldSerialize()}, spec 11-feature.md
 * §13.1) already excludes EMF transient/derived/volatile features unless {@code forceWrite}
 * / {@code forceRead} says otherwise. The generator emitted them unconditionally, so the
 * schema advertised properties that would never be serialized and every consumer had to
 * strip them by name.
 * </p>
 */
@DisplayName("Issue #214 - transient/derived/volatile features stay out of the schema")
class JsonSchemaFeatureVisibilityTest {

	@Nested
	@DisplayName("single-EClass document")
	class EClassDocument {

		@Test
		@DisplayName("a transient attribute is not a property")
		void transientAttributeExcluded() throws IOException {
			JsonNode schema = convertEClass(sampleEClass());

			JsonNode properties = schema.get("properties");
			assertNotNull(properties, "properties should be written");
			assertTrue(properties.has("name"), "a plain attribute stays");
			assertFalse(properties.has("cachedLabel"), "a transient attribute must be excluded");
		}

		@Test
		@DisplayName("a derived attribute is not a property")
		void derivedAttributeExcluded() throws IOException {
			JsonNode properties = convertEClass(sampleEClass()).get("properties");

			assertFalse(properties.has("fullName"), "a derived attribute must be excluded");
		}

		@Test
		@DisplayName("a volatile attribute is not a property")
		void volatileAttributeExcluded() throws IOException {
			JsonNode properties = convertEClass(sampleEClass()).get("properties");

			assertFalse(properties.has("computed"), "a volatile attribute must be excluded");
		}

		@Test
		@DisplayName("an excluded mandatory feature is not listed as required")
		void excludedFeatureIsNotRequired() throws IOException {
			JsonNode schema = convertEClass(sampleEClass());

			assertFalse(requiredNames(schema).contains("fullName"),
					"a derived feature must not be advertised as required");
			assertTrue(requiredNames(schema).contains("name"),
					"a plain mandatory attribute is still required");
		}

		@Test
		@DisplayName("a transient reference neither becomes a property nor pulls in a $defs entry")
		void transientReferenceExcluded() throws IOException {
			JsonNode schema = convertEClass(sampleEClass());

			assertFalse(schema.get("properties").has("scratch"),
					"a transient reference must be excluded");
			JsonNode defs = schema.get("$defs");
			if (defs != null) {
				assertFalse(defs.has("Scratch"),
						"a transient reference must not pull its target into $defs");
			}
		}
	}

	@Nested
	@DisplayName("EPackage document")
	class PackageDocument {

		@Test
		@DisplayName("excluded features are gone from the definition too")
		void excludedFromDefinitions() throws IOException {
			EPackage ePackage = samplePackage();

			JsonNode properties = convertPackage(ePackage, Map.of())
					.get("$defs").get("Person").get("properties");

			assertTrue(properties.has("name"), "a plain attribute stays");
			assertFalse(properties.has("cachedLabel"), "a transient attribute must be excluded");
			assertFalse(properties.has("fullName"), "a derived attribute must be excluded");
			assertFalse(properties.has("computed"), "a volatile attribute must be excluded");
		}

		@Test
		@DisplayName("an inherited transient feature is not inlined by flatAllOf")
		void excludedWhenInherited() throws IOException {
			EPackage ePackage = samplePackage();
			EClass person = eClass(ePackage, "Person");

			EClass employee = EcoreFactory.eINSTANCE.createEClass();
			employee.setName("Employee");
			employee.getESuperTypes().add(person);
			EAttribute salary = EcoreFactory.eINSTANCE.createEAttribute();
			salary.setName("salary");
			salary.setEType(EcorePackage.Literals.EDOUBLE);
			employee.getEStructuralFeatures().add(salary);
			ePackage.getEClassifiers().add(employee);

			JsonNode properties = convertPackage(ePackage,
					Map.of(CodecJsonSchemaOptions.OPTION_FLAT_ALL_OF, Boolean.TRUE))
					.get("$defs").get("Employee").get("properties");

			assertTrue(properties.has("salary"), "the own attribute stays");
			assertTrue(properties.has("name"), "the inherited plain attribute is inlined");
			assertFalse(properties.has("cachedLabel"),
					"an inherited transient attribute must be excluded");
		}
	}

	@Nested
	@DisplayName("the codec configuration is the only switch")
	class CodecConfiguration {

		@Test
		@DisplayName("forceWrite on a transient feature brings it back - no jsonschema option involved")
		void forceWriteReIncludes() throws IOException {
			EPackage ePackage = samplePackage();
			EClass person = eClass(ePackage, "Person");
			EStructuralFeature cachedLabel = person.getEStructuralFeature("cachedLabel");

			ConfigurationResolver resolver = ConfigurationResolver.builder()
					.forceWrite(cachedLabel)
					.build();

			JsonNode properties = convertPackage(ePackage, Map.of(), resolver)
					.get("$defs").get("Person").get("properties");

			assertTrue(properties.has("cachedLabel"),
					"forceWrite is the codec's existing opt-in and must re-include the feature");
		}

		@Test
		@DisplayName("a globally ignored feature disappears from the schema as well")
		void globalIgnoreExcludes() throws IOException {
			ConfigurationResolver resolver = ConfigurationResolver.builder()
					.globalIgnoreFeatures("name")
					.build();

			JsonNode properties = convertPackage(samplePackage(), Map.of(), resolver)
					.get("$defs").get("Person").get("properties");

			assertTrue(properties.has("email"), "the other plain attribute stays");
			assertFalse(properties.has("name"),
					"the schema follows the codec's feature visibility, not just the EMF markers");
		}

		@Test
		@DisplayName("forceWrite given as a plain save option re-includes a transient feature")
		void forceWriteAsSaveOption() throws IOException {
			JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
					URI.createURI("visibility.jsonschema"), MetadataServiceFactory.create());
			resource.getContents().add(samplePackage());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			resource.save(baos, Map.of("Person.cachedLabel", Map.of("forceWrite", Boolean.TRUE)));

			JsonNode properties = JsonMapper.builder().build().readTree(baos.toByteArray())
					.get("$defs").get("Person").get("properties");

			assertTrue(properties.has("cachedLabel"),
					"forceWrite as a save option must bring the transient attribute back");
			assertFalse(properties.has("fullName"), "the derived attribute is still excluded");
		}

		@Test
		@DisplayName("codec save options reach the schema through the resource")
		void saveOptionsFlowThroughResource() throws IOException {
			JsonSchemaResourceImpl resource = new JsonSchemaResourceImpl(
					URI.createURI("visibility.jsonschema"), MetadataServiceFactory.create());
			resource.getContents().add(samplePackage());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			resource.save(baos, Map.of(ConfigProperty.IGNORE_FEATURES.getKey(), List.of("name")));

			JsonNode properties = JsonMapper.builder().build().readTree(baos.toByteArray())
					.get("$defs").get("Person").get("properties");

			assertTrue(properties.has("email"), "the other plain attribute stays");
			assertFalse(properties.has("name"), "ignoreFeatures given as a save option must apply");
			assertFalse(properties.has("cachedLabel"), "the transient attribute stays excluded");
		}
	}

	/**
	 * A Person with one plain attribute plus one attribute per EMF exclusion marker and a
	 * transient reference.
	 */
	private static EPackage samplePackage() {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("test");
		ePackage.setNsPrefix("test");
		ePackage.setNsURI("http://example.org/test");

		EClass scratch = EcoreFactory.eINSTANCE.createEClass();
		scratch.setName("Scratch");
		EAttribute note = EcoreFactory.eINSTANCE.createEAttribute();
		note.setName("note");
		note.setEType(EcorePackage.Literals.ESTRING);
		scratch.getEStructuralFeatures().add(note);
		ePackage.getEClassifiers().add(scratch);

		EClass person = EcoreFactory.eINSTANCE.createEClass();
		person.setName("Person");

		EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		name.setLowerBound(1);
		person.getEStructuralFeatures().add(name);

		EAttribute email = EcoreFactory.eINSTANCE.createEAttribute();
		email.setName("email");
		email.setEType(EcorePackage.Literals.ESTRING);
		person.getEStructuralFeatures().add(email);

		EAttribute cachedLabel = EcoreFactory.eINSTANCE.createEAttribute();
		cachedLabel.setName("cachedLabel");
		cachedLabel.setEType(EcorePackage.Literals.ESTRING);
		cachedLabel.setTransient(true);
		person.getEStructuralFeatures().add(cachedLabel);

		EAttribute fullName = EcoreFactory.eINSTANCE.createEAttribute();
		fullName.setName("fullName");
		fullName.setEType(EcorePackage.Literals.ESTRING);
		fullName.setLowerBound(1);
		fullName.setDerived(true);
		person.getEStructuralFeatures().add(fullName);

		EAttribute computed = EcoreFactory.eINSTANCE.createEAttribute();
		computed.setName("computed");
		computed.setEType(EcorePackage.Literals.EINT);
		computed.setVolatile(true);
		person.getEStructuralFeatures().add(computed);

		EReference scratchRef = EcoreFactory.eINSTANCE.createEReference();
		scratchRef.setName("scratch");
		scratchRef.setEType(scratch);
		scratchRef.setContainment(true);
		scratchRef.setTransient(true);
		person.getEStructuralFeatures().add(scratchRef);

		ePackage.getEClassifiers().add(person);
		return ePackage;
	}

	private static EClass sampleEClass() {
		return eClass(samplePackage(), "Person");
	}

	private static EClass eClass(EPackage ePackage, String name) {
		return ePackage.getEClassifiers().stream()
				.filter(EClass.class::isInstance)
				.map(EClass.class::cast)
				.filter(c -> name.equals(c.getName()))
				.findFirst()
				.orElseThrow();
	}

	private static List<String> requiredNames(JsonNode schema) {
		List<String> names = new ArrayList<>();
		JsonNode required = schema.get("required");
		if (required != null) {
			required.forEach(node -> names.add(node.asString()));
		}
		return names;
	}

	private static JsonNode convertEClass(EClass eClass) throws IOException {
		EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		converter.convert(eClass, baos, false, Map.of());
		return JsonMapper.builder().build().readTree(baos.toByteArray());
	}

	private static JsonNode convertPackage(EPackage ePackage, Map<String, Object> options) throws IOException {
		return convertPackage(ePackage, options, null);
	}

	private static JsonNode convertPackage(EPackage ePackage, Map<String, Object> options,
			ConfigurationResolver resolver) throws IOException {
		EPackageToJsonSchemaConverter converter = new EPackageToJsonSchemaConverter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		converter.convert(ePackage, baos, "$defs", false, options, resolver);
		return JsonMapper.builder().build().readTree(baos.toByteArray());
	}
}
