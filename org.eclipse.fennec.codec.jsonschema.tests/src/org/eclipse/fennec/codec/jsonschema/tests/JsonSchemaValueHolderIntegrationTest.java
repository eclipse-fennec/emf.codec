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
package org.eclipse.fennec.codec.jsonschema.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.codec.jsonschema.v2.value.EClassValueReader;
import org.eclipse.fennec.codec.jsonschema.v2.value.EClassValueWriter;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * OSGi integration tests for the JSON Schema value handler round-trip.
 * <p>
 * Verifies that an EObject with a containment EReference to {@link EClass},
 * annotated with {@code valueWriterName="eClassToJsonSchema"} and
 * {@code valueReaderName="jsonSchemaToEClass"}, serializes the reference as
 * an inline JSON Schema document and deserializes it back to an EClass.
 * </p>
 *
 * @see EClassValueWriter
 * @see EClassValueReader
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("JSON Schema Value Handler Integration Tests")
public class JsonSchemaValueHolderIntegrationTest {

	private static final String ECORE = "/org/eclipse/fennec/codec/jsonschema/tests/example-jsonschema-value-holder.ecore";

	@InjectService
	MetadataService metadataService;

	private EcoreHelper ecoreHelper;
	private EPackage pkg;
	private ServiceRegistration<EPackage> pkgReg;

	private EClass schemaHolderClass;
	private EAttribute nameAttr;
	private EAttribute descriptionAttr;
	private EReference schemaRef;
	private EReference nonContainedSchemaRef;

	private CodecValueRegistry valueRegistry;

	@BeforeEach
	public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
		ecoreHelper = new EcoreHelper();
		pkg = ecoreHelper.loadEcore(ECORE, JsonSchemaValueHolderIntegrationTest.class);
		EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
		pkgReg = ctx.registerService(EPackage.class, pkg, null);

		schemaHolderClass = EcoreHelper.getEClass(pkg, "SchemaHolder");
		nameAttr = (EAttribute) EcoreHelper.getFeature(schemaHolderClass, "name");
		descriptionAttr = (EAttribute) EcoreHelper.getFeature(schemaHolderClass, "description");
		schemaRef = (EReference) EcoreHelper.getFeature(schemaHolderClass, "schema");
		nonContainedSchemaRef = (EReference) EcoreHelper.getFeature(schemaHolderClass, "nonContainedSchema");

		valueRegistry = new CodecValueRegistry();
		valueRegistry.register(new EClassValueWriter());
		valueRegistry.register(new EClassValueReader());
	}

	@AfterEach
	public void tearDown() {
		pkgReg.unregister();
		EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
		ecoreHelper.releaseAll();
	}

	// ========================================================================
	// Helper methods
	// ========================================================================

	private EClass createTestEClass(String className) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(className);

		EAttribute firstName = EcoreFactory.eINSTANCE.createEAttribute();
		firstName.setName("firstName");
		firstName.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(firstName);

		EAttribute age = EcoreFactory.eINSTANCE.createEAttribute();
		age.setName("age");
		age.setEType(EcorePackage.Literals.EINT);
		eClass.getEStructuralFeatures().add(age);

		return eClass;
	}

	/**
	 * Creates a TrendAnalysis EClass structure similar to the user's real model:
	 * <pre>
	 * TrendAnalysis
	 *   ├── id : EString
	 *   └── trends : Trend[*] (containment)
	 *
	 * CompactTrend
	 *   ├── title : EString
	 *   ├── category : EString
	 *   └── timeHorizon : EString
	 *
	 * Trend extends CompactTrend
	 *   ├── description : EString
	 *   └── evidence : EString[*]
	 * </pre>
	 * All classes are in the same EPackage.
	 */
	private EClass createTrendAnalysisEClass() {
		EPackage trendPkg = EcoreFactory.eINSTANCE.createEPackage();
		trendPkg.setName("trendmodel");
		trendPkg.setNsPrefix("trend");
		trendPkg.setNsURI("http://example.org/trend/1.0");

		// CompactTrend (base class)
		EClass compactTrend = EcoreFactory.eINSTANCE.createEClass();
		compactTrend.setName("CompactTrend");

		EAttribute title = EcoreFactory.eINSTANCE.createEAttribute();
		title.setName("title");
		title.setEType(EcorePackage.Literals.ESTRING);
		title.setLowerBound(1);
		compactTrend.getEStructuralFeatures().add(title);

		EAttribute category = EcoreFactory.eINSTANCE.createEAttribute();
		category.setName("category");
		category.setEType(EcorePackage.Literals.ESTRING);
		category.setLowerBound(1);
		compactTrend.getEStructuralFeatures().add(category);

		EAttribute timeHorizon = EcoreFactory.eINSTANCE.createEAttribute();
		timeHorizon.setName("timeHorizon");
		timeHorizon.setEType(EcorePackage.Literals.ESTRING);
		compactTrend.getEStructuralFeatures().add(timeHorizon);

		trendPkg.getEClassifiers().add(compactTrend);

		// Trend extends CompactTrend
		EClass trend = EcoreFactory.eINSTANCE.createEClass();
		trend.setName("Trend");
		trend.getESuperTypes().add(compactTrend);

		EAttribute description = EcoreFactory.eINSTANCE.createEAttribute();
		description.setName("description");
		description.setEType(EcorePackage.Literals.ESTRING);
		description.setLowerBound(1);
		trend.getEStructuralFeatures().add(description);

		EAttribute evidence = EcoreFactory.eINSTANCE.createEAttribute();
		evidence.setName("evidence");
		evidence.setEType(EcorePackage.Literals.ESTRING);
		evidence.setUpperBound(-1);
		evidence.setLowerBound(1);
		trend.getEStructuralFeatures().add(evidence);

		trendPkg.getEClassifiers().add(trend);

		// TrendAnalysis (root)
		EClass trendAnalysis = EcoreFactory.eINSTANCE.createEClass();
		trendAnalysis.setName("TrendAnalysis");

		EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
		id.setName("id");
		id.setEType(EcorePackage.Literals.ESTRING);
		id.setLowerBound(1);
		trendAnalysis.getEStructuralFeatures().add(id);

		EReference trendsRef = EcoreFactory.eINSTANCE.createEReference();
		trendsRef.setName("trends");
		trendsRef.setEType(trend);
		trendsRef.setContainment(true);
		trendsRef.setUpperBound(-1);
		trendsRef.setLowerBound(1);
		trendAnalysis.getEStructuralFeatures().add(trendsRef);

		trendPkg.getEClassifiers().add(trendAnalysis);

		return trendAnalysis;
	}

	private String serialize(EObject object) throws IOException {
		CodecResource resource = new CodecResource(
				URI.createURI("jsonschema.json"), metadataService,
				ConfigurationResolver.builder().typeInclude(false).build(), valueRegistry, null);
		resource.getContents().add(object);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, null);
		return out.toString(StandardCharsets.UTF_8);
	}

	private EObject deserialize(String json) throws IOException {
		CodecResource resource = new CodecResource(
				URI.createURI("jsonschema.json"), metadataService,
				ConfigurationResolver.defaults(), valueRegistry, null);
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, schemaHolderClass);
		resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

		return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
	}

	// ========================================================================
	// Tests
	// ========================================================================

	@Test
	@DisplayName("Serialization — EClass is written as inline JSON Schema")
	void serialization() throws IOException {
		EObject holder = pkg.getEFactoryInstance().create(schemaHolderClass);
		holder.eSet(nameAttr, "person-schema");
		holder.eSet(descriptionAttr, "A schema for Person");
		holder.eSet(schemaRef, createTestEClass("Person"));

		String json = serialize(holder);

		assertNotNull(json);
		assertTrue(json.contains("\"person-schema\""), "Should contain holder name");
		assertTrue(json.contains("\"schema\""), "Should contain schema field");
		assertTrue(json.contains("\"title\""), "Schema should have title");
		assertTrue(json.contains("\"Person\""), "Schema title should be Person");
		assertTrue(json.contains("\"type\""), "Schema should have type");
		assertTrue(json.contains("\"object\""), "Schema type should be object");
		assertTrue(json.contains("\"properties\""), "Schema should have properties");
		assertTrue(json.contains("\"firstName\""), "Schema should have firstName property");
		assertTrue(json.contains("\"age\""), "Schema should have age property");
	}

	@Test
	@DisplayName("Deserialization — inline JSON Schema is read back as EClass")
	void deserialization() throws IOException {
		String json = """
				{
				  "_id":"employee-schema", 
				  "name": "employee-schema",
				  "description": "A schema for Employee",
				  "schema": {
				    "title": "Employee",
				    "type": "object",
				    "properties": {
				      "employeeName": {
				        "type": "string"
				      },
				      "salary": {
				        "type": "integer"
				      }
				    }
				  }
				}""";

		EObject loaded = deserialize(json);

		assertNotNull(loaded);
		assertEquals("employee-schema", loaded.eGet(nameAttr));
		assertEquals("A schema for Employee", loaded.eGet(descriptionAttr));

		EClass loadedSchema = (EClass) loaded.eGet(schemaRef);
		assertNotNull(loadedSchema, "Schema EClass should be deserialized");
		assertEquals("Employee", loadedSchema.getName());
		assertNotNull(loadedSchema.getEStructuralFeature("employeeName"),
				"Should have employeeName attribute");
		assertNotNull(loadedSchema.getEStructuralFeature("salary"),
				"Should have salary attribute");
	}

	@Test
	@DisplayName("Serialization — EClass with inheritance emits $defs for supertype")
	void serializationWithInheritance() throws IOException {
		EClass trendAnalysis = createTrendAnalysisEClass();

		EObject holder = pkg.getEFactoryInstance().create(schemaHolderClass);
		holder.eSet(nameAttr, "trend-analysis-schema");
		holder.eSet(descriptionAttr, "Schema for trend analysis with inheritance");
		holder.eSet(schemaRef, trendAnalysis);

		String json = serialize(holder);

		assertNotNull(json);

		// The schema should contain $defs with CompactTrend definition
		assertTrue(json.contains("\"$defs\""),
				"Schema should contain $defs section for referenced supertypes. Got: " + json);
		assertTrue(json.contains("\"CompactTrend\""),
				"$defs should contain CompactTrend definition. Got: " + json);

		// The Trend items should use allOf with $ref to CompactTrend
		assertTrue(json.contains("\"$ref\""),
				"Schema should contain $ref for inheritance. Got: " + json);
		assertTrue(json.contains("#/$defs/CompactTrend"),
				"$ref should point to #/$defs/CompactTrend. Got: " + json);

		// CompactTrend's own properties should appear in its $defs definition
		assertTrue(json.contains("\"title\""),
				"CompactTrend should have title property. Got: " + json);
		assertTrue(json.contains("\"category\""),
				"CompactTrend should have category property. Got: " + json);

		// Trend's own properties should appear inline (in the allOf)
		assertTrue(json.contains("\"description\""),
				"Trend should have description property. Got: " + json);
		assertTrue(json.contains("\"evidence\""),
				"Trend should have evidence property. Got: " + json);
	}
	
	@Test
	@DisplayName("Serialization — EClass with inheritance but flatAllOf should not have $defs for supertype")
	void serializationWithInheritanceFlatAllEnabled() throws IOException {
		EClass trendAnalysis = createTrendAnalysisEClass();

		EObject holder = pkg.getEFactoryInstance().create(schemaHolderClass);
		holder.eSet(nameAttr, "trend-analysis-schema");
		holder.eSet(descriptionAttr, "Schema for trend analysis with inheritance");
		holder.eSet(schemaRef, trendAnalysis);
		
		CodecResource resource = new CodecResource(
				URI.createURI("jsonschema.json"), metadataService,
				ConfigurationResolver.builder().typeInclude(false).build(), valueRegistry, null);
		resource.getContents().add(holder);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of(CodecJsonSchemaOptions.OPTION_FLAT_ALL_OF, true));
		String json = out.toString(StandardCharsets.UTF_8);

		assertNotNull(json);

		// The schema should NOT contain $defs with CompactTrend definition
		assertFalse(json.contains("\"CompactTrend\""),
				"$defs should NOT contain CompactTrend definition. Got: " + json);


		// CompactTrend's own properties should appear in the Trend object
		assertTrue(json.contains("\"title\""),
				"Trend should have title property. Got: " + json);
		assertTrue(json.contains("\"category\""),
				"Trend should have category property. Got: " + json);

		// Trend's own properties should appear inline (in the allOf)
		assertTrue(json.contains("\"description\""),
				"Trend should have description property. Got: " + json);
		assertTrue(json.contains("\"evidence\""),
				"Trend should have evidence property. Got: " + json);
	}

	@Test
	@DisplayName("Round-trip — serialize then deserialize preserves EClass structure")
	void roundTrip() throws IOException {
		EObject holder = pkg.getEFactoryInstance().create(schemaHolderClass);
		holder.eSet(nameAttr, "product-schema");
		holder.eSet(descriptionAttr, "A schema for Product");
		holder.eSet(schemaRef, createTestEClass("Product"));

		String json = serialize(holder);
		EObject loaded = deserialize(json);

		assertNotNull(loaded);
		assertEquals("product-schema", loaded.eGet(nameAttr));
		assertEquals("A schema for Product", loaded.eGet(descriptionAttr));

		assertTrue(loaded.eIsSet(schemaRef), "Schema EClass should be set");
		EClass loadedSchema = (EClass) loaded.eGet(schemaRef);
		assertNotNull(loadedSchema, "Schema EClass should survive round-trip");
		assertEquals("Product", loadedSchema.getName());

		assertFalse(loadedSchema.getEStructuralFeatures().isEmpty(),
				"Round-tripped EClass should have features");
		assertNotNull(loadedSchema.getEStructuralFeature("firstName"),
				"Should have firstName attribute after round-trip");
		assertNotNull(loadedSchema.getEStructuralFeature("age"),
				"Should have age attribute after round-trip");
	}
	
	@Test
	@DisplayName("Serialization Non Contaiment — EClass with inheritance emits $defs for supertype")
	void serializationNonContainedWithInheritance() throws IOException {
		EClass trendAnalysis = createTrendAnalysisEClass();

		EObject holder = pkg.getEFactoryInstance().create(schemaHolderClass);
		holder.eSet(nameAttr, "trend-analysis-schema");
		holder.eSet(descriptionAttr, "Schema for trend analysis with inheritance");
		holder.eSet(nonContainedSchemaRef, trendAnalysis);

		String json = serialize(holder);

		assertNotNull(json);

		// The schema should contain $defs with CompactTrend definition
		assertTrue(json.contains("\"$defs\""),
				"Schema should contain $defs section for referenced supertypes. Got: " + json);
		assertTrue(json.contains("\"CompactTrend\""),
				"$defs should contain CompactTrend definition. Got: " + json);

		// The Trend items should use allOf with $ref to CompactTrend
		assertTrue(json.contains("\"$ref\""),
				"Schema should contain $ref for inheritance. Got: " + json);
		assertTrue(json.contains("#/$defs/CompactTrend"),
				"$ref should point to #/$defs/CompactTrend. Got: " + json);

		// CompactTrend's own properties should appear in its $defs definition
		assertTrue(json.contains("\"title\""),
				"CompactTrend should have title property. Got: " + json);
		assertTrue(json.contains("\"category\""),
				"CompactTrend should have category property. Got: " + json);

		// Trend's own properties should appear inline (in the allOf)
		assertTrue(json.contains("\"description\""),
				"Trend should have description property. Got: " + json);
		assertTrue(json.contains("\"evidence\""),
				"Trend should have evidence property. Got: " + json);
	}
}
