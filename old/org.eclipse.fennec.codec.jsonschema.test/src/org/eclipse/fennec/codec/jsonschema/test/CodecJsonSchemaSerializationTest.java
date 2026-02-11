/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.jsonschema.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.options.CodecModuleOptions;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.eclipse.fennec.codec.options.CodecResourceOptions;
//import org.eclipse.fennec.openapi.model.OpenApi;
//import org.eclipse.fennec.openapi.model.OpenApiPackage;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class CodecJsonSchemaSerializationTest {

	@InjectService(filter="("+EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/schema+json)")
	ResourceSet resourceSet;

	@InjectService
	CodecModelInfo codecModelInfo;

	@InjectBundleContext
	BundleContext ctx;
	
	private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";

	private String file2;


	@BeforeEach()
	public void beforeEach() throws InterruptedException {
		assertNotNull(resourceSet);
	}

	@AfterEach()
	public void afterEach() throws IOException {
//		if(file2 != null) Files.deleteIfExists(Path.of(file2));
	}
	
	
//	@Test
	public void topLevelEClass() throws IOException {
		String file1 = System.getProperty("data")+"top-level-eclass.json";
		file2 = System.getProperty("data")+"ser_top-level-eclass.json";
		Resource res = resourceSet.createResource(URI.createURI(file1), "application/schema+json");
		Map<String, Object> options = CodecOptionsBuilder.create(). 
				rootObject(EcorePackage.Literals.EPACKAGE).
				serializeType(false).
				serializeEmptyValue(true).
				serializeNullValue(true).
				forClass(EcorePackage.Literals.EPACKAGE).
				withExtraProperties(Map.of("jsonschema", "true", "jsonschema.feature.key", "definitions")).
				build();
		
		res.load(options);		
		assertFalse(res.getContents().isEmpty());
		EObject obj = res.getContents().get(0);
		assertNotNull(obj);
		assertThat(obj).isInstanceOf(EPackage.class);
		EPackage ePackage = (EPackage) res.getContents().get(0);
		
		res = resourceSet.createResource(URI.createURI(file2));
		res.getContents().add(ePackage);
		res.save(options);
		
		assertTrue(areJsonFilesTheSame(file1, file2));
	}
	
	@Test
	public void meterReading() throws IOException {
		String file1 = System.getProperty("data")+"meter-reading.json";
		file2 = System.getProperty("data")+"ser_meter-reading.ecore";
		Resource res = resourceSet.createResource(URI.createURI(file1), "application/schema+json");

		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(EcorePackage.Literals.EPACKAGE).
				serializeType(false).
				serializeEmptyValue(true).
				serializeNullValue(true).
				forClass(EcorePackage.Literals.EPACKAGE).
				withExtraProperties(Map.of("jsonschema", "true", "jsonschema.feature.key", "definitions")).
				build();

		res.load(options);
		assertFalse(res.getContents().isEmpty());
		EObject obj = res.getContents().get(0);
		assertNotNull(obj);
		assertThat(obj).isInstanceOf(EPackage.class);
		EPackage ePackage = (EPackage) res.getContents().get(0);

		res = resourceSet.createResource(URI.createURI(file2));
		res.getContents().add(ePackage);
		res.save(options);
	}

	@Test
	public void meterReadingDataDeserialization() throws IOException {
		// Step 1: Load JSON Schema and convert to EPackage
		String schemaFile = System.getProperty("data")+"meter-reading.json";
		Resource schemaRes = resourceSet.createResource(URI.createURI(schemaFile), "application/schema+json");

		Map<String, Object> schemaOptions = CodecOptionsBuilder.create().
				rootObject(EcorePackage.Literals.EPACKAGE).
				serializeType(false).
				serializeEmptyValue(true).
				serializeNullValue(true).
				forClass(EcorePackage.Literals.EPACKAGE).
				withExtraProperties(Map.of("jsonschema", "true", "jsonschema.feature.key", "definitions")).
				build();

		schemaRes.load(schemaOptions);
		assertFalse(schemaRes.getContents().isEmpty());
		EPackage ePackage = (EPackage) schemaRes.getContents().get(0);
		assertNotNull(ePackage);

		// Step 2: Register the generated EPackage
		// a) Register in ResourceSet's package registry so it can be found by URI
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		// b) Register in CodecModelInfo so CodecInfo metadata is generated
		codecModelInfo.put(ePackage.getNsURI(), ePackage);

		// Step 3: Find the MeterReading EClass from the generated package
		EClassifier meterReadingClassifier = ePackage.getEClassifier("MeterReading");
		assertNotNull(meterReadingClassifier, "MeterReading EClass should exist in generated package");
		assertThat(meterReadingClassifier).isInstanceOf(EClass.class);
		EClass meterReadingEClass = (EClass) meterReadingClassifier;

		// Step 4: Load JSON data that conforms to the schema
		String dataFile = System.getProperty("data")+"meter-reading-data.json";
		Resource dataRes = resourceSet.createResource(URI.createURI(dataFile));

		Map<String, Object> dataOptions = CodecOptionsBuilder.create().
				rootObject(meterReadingEClass).
				serializeType(false).
				build();

		dataRes.load(dataOptions);

		// Step 5: Verify the deserialized EObject
		assertFalse(dataRes.getContents().isEmpty());
		EObject meterReading = dataRes.getContents().get(0);
		assertNotNull(meterReading);
		assertThat(meterReading.eClass()).isEqualTo(meterReadingEClass);

		// Verify the attribute values
		EAttribute idAttr = (EAttribute) meterReadingEClass.getEStructuralFeature("id");
		EAttribute meterIdAttr = (EAttribute) meterReadingEClass.getEStructuralFeature("meter_id");
		EAttribute valueAttr = (EAttribute) meterReadingEClass.getEStructuralFeature("value");
		EAttribute timestampAttr = (EAttribute) meterReadingEClass.getEStructuralFeature("timestamp");

		assertNotNull(idAttr);
		assertNotNull(meterIdAttr);
		assertNotNull(valueAttr);
		assertNotNull(timestampAttr);

		assertThat(meterReading.eGet(idAttr)).isEqualTo(12345);
		assertThat(meterReading.eGet(meterIdAttr)).isEqualTo("METER_001");
		assertThat(meterReading.eGet(valueAttr)).isEqualTo(123.45);
		assertThat(meterReading.eGet(timestampAttr)).isEqualTo("2025-01-29T10:30:00Z");
	}
	
	@Test
	public void pipeline() throws IOException {
		String file1 = System.getProperty("data")+"pipeline_schema.json";
		file2 = System.getProperty("data")+"pipeline.ecore";
		Resource res = resourceSet.createResource(URI.createURI(file1), "application/schema+json");

		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(EcorePackage.Literals.EPACKAGE).
				serializeType(false).
				serializeEmptyValue(true).
				serializeNullValue(true).
				forClass(EcorePackage.Literals.EPACKAGE).
				withExtraProperties(Map.of("jsonschema", "true", "jsonschema.feature.key", "$defs")).
				build();

		res.load(options);
		assertFalse(res.getContents().isEmpty());
		EObject obj = res.getContents().get(0);
		assertNotNull(obj);
		assertThat(obj).isInstanceOf(EPackage.class);
		EPackage ePackage = (EPackage) res.getContents().get(0);

		res = resourceSet.createResource(URI.createURI(file2));
		res.getContents().add(ePackage);
		res.save(options);
	}

	@Test
	@Disabled("This is disabled because w/o generating code for the pipeline model I do not know how to make it work")
	public void pipelineDataDeserializationWithOneOf() throws IOException {
		// Step 1: Load JSON Schema and convert to EPackage
		String schemaFile = System.getProperty("data")+"pipeline_schema.json";
		Resource schemaRes = resourceSet.createResource(URI.createURI(schemaFile), "application/schema+json");

		Map<String, Object> schemaOptions = CodecOptionsBuilder.create().
				rootObject(EcorePackage.Literals.EPACKAGE).
				serializeType(false).
				serializeEmptyValue(true).
				serializeNullValue(true).
				forClass(EcorePackage.Literals.EPACKAGE).
				withExtraProperties(Map.of("jsonschema", "true", "jsonschema.feature.key", "$defs")).
				build();

		schemaRes.load(schemaOptions);
		assertFalse(schemaRes.getContents().isEmpty());
		EPackage ePackage = (EPackage) schemaRes.getContents().get(0);
		assertNotNull(ePackage);
		

		// Log the generated EClasses to understand the structure
		System.out.println("Generated EPackage: " + ePackage.getName());
		System.out.println("NsURI: " + ePackage.getNsURI());
		System.out.println("EClasses:");
		ePackage.getEClassifiers().forEach(classifier -> {
			System.out.println("  - " + classifier.getName() + " (" + classifier.getClass().getSimpleName() + ")");
			if (classifier instanceof EClass eClass) {
				eClass.getEStructuralFeatures().forEach(feature -> {
					System.out.println("    * " + feature.getName() + " : " + feature.getEType().getName());
				});
			}
		});

		// Step 2: Register the generated EPackage
		ctx.registerService(EPackage.class, ePackage, null);
		// Register the EPackage in the Global EPackage Registry
		EPackage.Registry.INSTANCE.put(
		    ePackage.getNsURI(), // The Namespace URI from your Ecore model
		    ePackage
		);
//		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
//		codecModelInfo.put(ePackage.getNsURI(), ePackage);

		// Step 3: Find the root EClass (should be something like RedpandaConnectPipelineSchema or similar)
		// The root is defined by the top-level "type": "object" with "properties"
		EClass rootEClass = null;
		for (EClassifier classifier : ePackage.getEClassifiers()) {
			if (classifier instanceof EClass eClass) {
				// Look for the class that has input, pipeline, and output features
				if (eClass.getEStructuralFeature("input") != null &&
					eClass.getEStructuralFeature("pipeline") != null &&
					eClass.getEStructuralFeature("output") != null) {
					rootEClass = eClass;
					break;
				}
			}
		}
		assertNotNull(rootEClass, "Root EClass with input/pipeline/output should exist");
		System.out.println("Using root EClass: " + rootEClass.getName());

		// Step 3.5: Find the EClasses for input, pipeline, and output to configure type mappings
		EStructuralFeature inputFeature = rootEClass.getEStructuralFeature("input");
		EStructuralFeature pipelineFeature = rootEClass.getEStructuralFeature("pipeline");
		EStructuralFeature outputFeature = rootEClass.getEStructuralFeature("output");

		assertNotNull(inputFeature, "input feature should exist");
		assertNotNull(pipelineFeature, "pipeline feature should exist");
		assertNotNull(outputFeature, "output feature should exist");

		// Get the type of each feature - these should be the oneOf wrapper classes
		EClass inputNodeClass = (EClass) inputFeature.getEType();
		EClass pipelineClass = (EClass) pipelineFeature.getEType();
		EClass outputNodeClass = (EClass) outputFeature.getEType();

		System.out.println("\nInput Node Class: " + inputNodeClass.getName());
		System.out.println("  Features: " + inputNodeClass.getEStructuralFeatures().stream()
				.map(f -> f.getName() + ":" + f.getEType().getName()).toList());

		System.out.println("\nPipeline Class: " + pipelineClass.getName());
		System.out.println("  Features: " + pipelineClass.getEStructuralFeatures().stream()
				.map(f -> f.getName() + ":" + f.getEType().getName()).toList());

		System.out.println("\nOutput Node Class: " + outputNodeClass.getName());
		System.out.println("  Features: " + outputNodeClass.getEStructuralFeatures().stream()
				.map(f -> f.getName() + ":" + f.getEType().getName()).toList());

		// For processors, we need to find the array item type
		EStructuralFeature processorsFeature = pipelineClass.getEStructuralFeature("processors");
		assertNotNull(processorsFeature, "processors feature should exist");
		EClass processorNodeClass = (EClass) processorsFeature.getEType();

		System.out.println("\nProcessor Node Class: " + processorNodeClass.getName());
		System.out.println("  Features: " + processorNodeClass.getEStructuralFeatures().stream()
				.map(f -> f.getName() + ":" + f.getEType().getName()).toList());

		// Step 4: Load JSON data that conforms to the schema
		// The codec.type annotations are now embedded in the EPackage model.
		// CodecModelInfoService will automatically parse and apply them during deserialization.
		String dataFile = System.getProperty("data")+"pipeline-data.json";
		Resource dataRes = resourceSet.createResource(URI.createURI(dataFile));

		Map<String, Object> dataOptions = CodecOptionsBuilder.create().
				rootObject(rootEClass).
				build();

		dataRes.load(dataOptions);

		// Step 6: Verify the deserialized EObject
		assertFalse(dataRes.getContents().isEmpty());
		EObject pipelineConfig = dataRes.getContents().get(0);
		assertNotNull(pipelineConfig);
		assertThat(pipelineConfig.eClass()).isEqualTo(rootEClass);

		// Step 5: Verify the structure - particularly the oneOf types
		// Check that input exists and is of the correct type
		Object inputValue = pipelineConfig.eGet(inputFeature);
		assertNotNull(inputValue, "input should have a value");
		assertThat(inputValue).isInstanceOf(EObject.class);

		EObject inputNode = (EObject) inputValue;
		System.out.println("\nDeserialized Input node type: " + inputNode.eClass().getName());
		System.out.println("Input node is instance of InputNode: " + inputNodeClass.isInstance(inputNode));

		// The inputNode should be a KafkaInputNode (discriminated based on "kafka" property presence)
		assertThat(inputNode.eClass().getName()).isEqualTo("KafkaInputNode");

		// The inputNode should have a "config" feature pointing to the Kafka configuration
		EStructuralFeature configFeature = inputNode.eClass().getEStructuralFeature("config");
		assertNotNull(configFeature, "config feature should exist in KafkaInputNode");
		Object configValue = inputNode.eGet(configFeature);
		assertNotNull(configValue, "config should have a value");
		assertThat(configValue).isInstanceOf(EObject.class);

		// Verify kafka configuration
		EObject kafkaConfig = (EObject) configValue;
		System.out.println("Kafka config type: " + kafkaConfig.eClass().getName());

		// Check addresses attribute
		EStructuralFeature addressesFeature = kafkaConfig.eClass().getEStructuralFeature("addresses");
		assertNotNull(addressesFeature, "addresses feature should exist");
		Object addresses = kafkaConfig.eGet(addressesFeature);
		assertNotNull(addresses, "addresses should have a value");
		System.out.println("Addresses: " + addresses);

		// Check topics attribute
		EStructuralFeature topicsFeature = kafkaConfig.eClass().getEStructuralFeature("topics");
		assertNotNull(topicsFeature, "topics feature should exist");
		Object topics = kafkaConfig.eGet(topicsFeature);
		assertNotNull(topics, "topics should have a value");
		System.out.println("Topics: " + topics);

		// Step 8: Verify processors array (another oneOf scenario)
		Object pipelineValue = pipelineConfig.eGet(pipelineFeature);
		assertNotNull(pipelineValue);
		assertThat(pipelineValue).isInstanceOf(EObject.class);

		EObject pipelineObj = (EObject) pipelineValue;
		EStructuralFeature processorsFeatureCheck = pipelineObj.eClass().getEStructuralFeature("processors");
		assertNotNull(processorsFeatureCheck, "processors feature should exist");
		assertThat(processorsFeatureCheck).isEqualTo(processorsFeature);

		System.out.println("\nTest completed successfully!");
		System.out.println("This validates that:");
		System.out.println("1. JSON Schema with oneOf is converted to EPackage with proper structure");
		System.out.println("2. Dynamic EClasses handle union types correctly");
		System.out.println("3. Feature-based type discrimination allows deserialization based on property presence");
		System.out.println("4. JSON data conforming to the schema can be deserialized into EObjects");
	}

	@Test
	public void pipelineRoundTrip() throws IOException {
		String file1 = System.getProperty("data")+"pipeline_schema.json";
		file2 = System.getProperty("data")+"pipeline_schema2.json";
		Resource res = resourceSet.createResource(URI.createURI(file1), "application/schema+json");

		Map<String, Object> options = CodecOptionsBuilder.create().
				rootObject(EcorePackage.Literals.EPACKAGE).
				serializeType(false).
				serializeEmptyValue(true).
				serializeNullValue(true).
				serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT).
				forClass(EcorePackage.Literals.EPACKAGE).
				withExtraProperties(Map.of("jsonschema", "true", "jsonschema.feature.key", "$defs")).
				build();

		// Load JSON Schema → EPackage
		res.load(options);
		assertFalse(res.getContents().isEmpty());
		EObject obj = res.getContents().get(0);
		assertNotNull(obj);
		assertThat(obj).isInstanceOf(EPackage.class);
		EPackage ePackage = (EPackage) res.getContents().get(0);

		// Serialize EPackage → JSON Schema
		res = resourceSet.createResource(URI.createURI(file2), "application/schema+json");
		res.getContents().add(ePackage);
		res.save(options);

		// Compare the two JSON Schema files
		assertTrue(areJsonFilesTheSame(file1, file2));
	}
	
	
//	@Test
	public void openAPIJsonSchema() throws IOException {
		String file1 = System.getProperty("data")+"open-api.json";
		file2 = System.getProperty("data")+"ser_open-api.json";
		
		Resource res = resourceSet.createResource(URI.createURI(file1), "application/schema+json");
		Map<String, Object> options = CodecOptionsBuilder.create(). 
				rootObject(EcorePackage.Literals.EPACKAGE).
				serializeType(false).
				serializeEmptyValue(true).
				serializeNullValue(true).
				forClass(EcorePackage.Literals.EPACKAGE).
				withExtraProperties(Map.of("jsonschema", "true", "jsonschema.feature.key", "schemas")).
				build();
		
		res.load(options);		
		assertFalse(res.getContents().isEmpty());
		EObject obj = res.getContents().get(0);
		assertNotNull(obj);
		assertThat(obj).isInstanceOf(EPackage.class);
		EPackage ePackage = (EPackage) res.getContents().get(0);
		
		res = resourceSet.createResource(URI.createURI(file2));
		res.getContents().add(ePackage);
		res.save(options);
		
		assertTrue(areJsonFilesTheSame(file1, file2));
	}
	
//	@Disabled("This does not fully work, because of the emf model we have. Some features are deserialized as String and then serialized back as String. This is no jsonschema (de)serializer fault. It depends on the model!")
//	@Test
//	public void openAPIComplete() throws IOException {
//		String file1 = System.getProperty("data")+"openapi-complete.json";
//		file2 = System.getProperty("data")+"ser_openapi-complete.json";
//		
//		Resource res = resourceSet.createResource(URI.createURI(file1), "application/json");
//		Map<String, Object> options = new HashMap<>();
//		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, OpenApiPackage.Literals.OPEN_API);
//		options.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, true);
//		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_TYPE, false);
//		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_EMPTY_VALUE, true);
//		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_NULL_VALUE, true);
//		
//		res.load(options);		
//		assertFalse(res.getContents().isEmpty());
//		EObject obj = res.getContents().get(0);
//		assertNotNull(obj);
//		assertThat(obj).isInstanceOf(OpenApi.class);
//		OpenApi openApi = (OpenApi) res.getContents().get(0);
//
//		res = resourceSet.createResource(URI.createURI(file2));
//		res.getContents().add(openApi);
//		res.save(options);
//		
//		assertTrue(areJsonFilesTheSame(file1, file2));
//	}

//	@Test
//	public void topLevelEEnum() throws IOException {
//
//		String file1 = System.getProperty("data")+"top-level-enum.json";
//		file2 = System.getProperty("data")+"ser_top-level-enum.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void topLevelArray() throws IOException {
//
//		String file1 = System.getProperty("data")+"top-level-array.json";
//		file2 = System.getProperty("data")+"ser_top-level-array.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void topLevelSimpleEDataType() throws IOException {
//
//		String file1 = System.getProperty("data")+"top-level-simple-edatatype.json";
//		file2 = System.getProperty("data")+"ser_top-level-simple-edatatype.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void topLevelObjectEDataType() throws IOException {
//
//		String file1 = System.getProperty("data")+"top-level-object-edatatype.json";
//		file2 = System.getProperty("data")+"ser_top-level-object-edatatype.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void singleAttribute() throws IOException {
//
//		String file1 = System.getProperty("data")+"single-attribute.json";
//		file2 = System.getProperty("data")+"ser_single-attribute.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void singleAttributeArrayType() throws IOException {
//
//		String file1 = System.getProperty("data")+"single-attribute-array-type.json";
//		file2 = System.getProperty("data")+"ser_single-attribute-array-type.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void singleAttributeEnum() throws IOException {
//
//		String file1 = System.getProperty("data")+"single-attribute-enum.json";
//		file2 = System.getProperty("data")+"ser_single-attribute-enum.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void singleAttributeEnumWOType() throws IOException {
//
//		String file1 = System.getProperty("data")+"single-attribute-enum-wo-type.json";
//		file2 = System.getProperty("data")+"ser_single-attribute-enum-wo-type.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void manyAttribute() throws IOException {
//
//		String file1 = System.getProperty("data")+"many-attribute.json";
//		file2 = System.getProperty("data")+"ser_many-attribute.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void manyAttributeConst() throws IOException {
//
//		String file1 = System.getProperty("data")+"many-attribute-const.json";
//		file2 = System.getProperty("data")+"ser_many-attribute-const.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void manyAttributeConstWOType() throws IOException {
//
//		String file1 = System.getProperty("data")+"many-attribute-const-wo-type.json";
//		file2 = System.getProperty("data")+"ser_many-attribute-const-wo-type.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void manyAttributeEnumWOType() throws IOException {
//
//		String file1 = System.getProperty("data")+"many-attribute-enum-wo-type.json";
//		file2 = System.getProperty("data")+"ser_many-attribute-enum-wo-type.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void manyAttributeEnum() throws IOException {
//
//		String file1 = System.getProperty("data")+"many-attribute-enum.json";
//		file2 = System.getProperty("data")+"ser_many-attribute-enum.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void singleAttributeConst() throws IOException {
//
//		String file1 = System.getProperty("data")+"single-attribute-const.json";
//		file2 = System.getProperty("data")+"ser_single-attribute-const.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void singleAttributeConstWOType() throws IOException {
//
//		String file1 = System.getProperty("data")+"single-attribute-const-wo-type.json";
//		file2 = System.getProperty("data")+"ser_single-attribute-const-wo-type.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void containedRef() throws IOException {
//
//		String file1 = System.getProperty("data")+"contained-ref.json";
//		file2 = System.getProperty("data")+"ser_contained-ref.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void manyContainedRef() throws IOException {
//
//		String file1 = System.getProperty("data")+"many-contained-ref.json";
//		file2 = System.getProperty("data")+"ser_many-contained-ref.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void containedReusedRef() throws IOException {
//
//		String file1 = System.getProperty("data")+"contained-reused-ref.json";
//		file2 = System.getProperty("data")+"ser_many-contained-reused-ref.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void nonContainedRef() throws IOException {
//
//		String file1 = System.getProperty("data")+"non-contained-ref.json";
//		file2 = System.getProperty("data")+"ser_non-contained-ref.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void manyNonContainedRef() throws IOException {
//
//		String file1 = System.getProperty("data")+"many-non-contained-ref.json";
//		file2 = System.getProperty("data")+"ser_many-non-contained-ref.json";
//		executeTest(file1, file2);
//	}
//	
//	@Test
//	public void anyOfWithRef() throws IOException {
//
//		String file1 = System.getProperty("data")+"anyOf-with-ref.json";
//		file2 = System.getProperty("data")+"ser_anyOf-with-ref.json";
//		executeTest(file1, file2);
//	}
	
	private void executeTest(String file1, String file2) throws IOException {
		Resource res = resourceSet.createResource(URI.createURI(file1));
		res.load(getLoadOptions());		
		EPackage ePackage = extractEPackageFromLoadedResource(res);

		res = resourceSet.createResource(URI.createURI(file2));
		res.getContents().add(ePackage);
		res.save(getSaveOptions());
		
		assertTrue(areJsonFilesTheSame(file1, file2));
	}
	
	
	
	
//	@Test
//	public void ePackageSimpleAttribute() throws IOException {
//		
//		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
//		ePackage.setName("test");
//		ePackage.setNsURI("www.test.org");
//		addEAnnotation(ePackage, JSONSCHEMA_ANNOTATION_SOURCE, "schema", "jsonschema/json.org");
//		
//		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
//		eClass.setName("Person");
//		EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
//		eAttribute.setName("firstName");
//		eAttribute.setEType(EcorePackage.Literals.ESTRING);
//		eClass.getEStructuralFeatures().add(eAttribute);
//		ePackage.getEClassifiers().add(eClass);
//		
//		Resource res = resourceSet.createResource(URI.createURI("epackage.json"));
//		res.getContents().add(ePackage);
//		res.save(getSaveOptions());
//		
//		res = resourceSet.createResource(URI.createURI("epackage.json"));
//		res.load(getLoadOptions());
//		
//		assertThat(res.getContents()).isNotEmpty();
//		assertThat(res.getContents().get(0)).isInstanceOf(EPackage.class);
//		EPackage desEpackage = (EPackage) res.getContents().get(0);
//		assertTrue(areEPackagesTheSame(ePackage, desEpackage));
//		
//	}
//	
//	@Test
//	public void ePackageAllOf() throws IOException {
//		
//		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
//		ePackage.setName("test");
//		ePackage.setNsURI("www.test.org");
//		addEAnnotation(ePackage, JSONSCHEMA_ANNOTATION_SOURCE, "schema", "jsonschema/json.org");
//		
//		EClass eClass = createEClass("Parent");
//		eClass.getEStructuralFeatures().add(createEAttribute("firstName", EcorePackage.Literals.ESTRING));
//		ePackage.getEClassifiers().add(eClass);
//		
//		EClass eClass2 = createEClass("Child1");
//		eClass2.getESuperTypes().add(eClass);
//		eClass2.getEStructuralFeatures().add(createEAttribute("lastName", EcorePackage.Literals.ESTRING));
//		ePackage.getEClassifiers().add(eClass2);
//		
//		EClass eClass3 = createEClass("Child2");
//		eClass3.getESuperTypes().add(eClass);
//		eClass3.getEStructuralFeatures().add(createEAttribute("middleName", EcorePackage.Literals.ESTRING));
//		ePackage.getEClassifiers().add(eClass3);
//		
//		Resource res = resourceSet.createResource(URI.createURI("epackage.json"));
//		res.getContents().add(ePackage);
//		res.save(getSaveOptions());
//		
//		res = resourceSet.createResource(URI.createURI("epackage.json"));
//		res.load(getLoadOptions());
//		
//		assertThat(res.getContents()).isNotEmpty();
//		assertThat(res.getContents().get(0)).isInstanceOf(EPackage.class);
//		EPackage desEpackage = (EPackage) res.getContents().get(0);
//		assertTrue(areEPackagesTheSame(ePackage, desEpackage));		
//	}
	
	@Disabled("We need to understand which behaviour we want here!")
	@Test
	public void ePackageAnyOf() throws IOException {
		
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("test");
		ePackage.setNsURI("www.test.org");
		addEAnnotation(ePackage, JSONSCHEMA_ANNOTATION_SOURCE, "schema", "jsonschema/json.org");
		
		EClass eClass = createEClass("Parent");
		eClass.getEStructuralFeatures().add(createEAttribute("firstName", EcorePackage.Literals.ESTRING));
		ePackage.getEClassifiers().add(eClass);
		
		EClass eClass2 = createEClass("Child1");
		eClass2.getESuperTypes().add(eClass);
		eClass2.getEStructuralFeatures().add(createEAttribute("lastName", EcorePackage.Literals.ESTRING));
		ePackage.getEClassifiers().add(eClass2);
		
		EClass eClass3 = createEClass("Child2");
		eClass3.getESuperTypes().add(eClass);
		eClass3.getEStructuralFeatures().add(createEAttribute("middleName", EcorePackage.Literals.ESTRING));
		ePackage.getEClassifiers().add(eClass3);
		
		EClass eClass4 = createEClass("Test");
		eClass4.getEStructuralFeatures().add(createEReference("ref", eClass));
		ePackage.getEClassifiers().add(eClass4);
		
		Resource res = resourceSet.createResource(URI.createURI("epackage.json"));
		res.getContents().add(ePackage);
		res.save(getSaveOptions());
		
		res = resourceSet.createResource(URI.createURI("epackage.json"));
		res.load(getLoadOptions());
		
		assertThat(res.getContents()).isNotEmpty();
		assertThat(res.getContents().get(0)).isInstanceOf(EPackage.class);
		EPackage desEpackage = (EPackage) res.getContents().get(0);
		assertTrue(areEPackagesTheSame(ePackage, desEpackage));		
	}
	
	
	private Map<String, Object> getSaveOptions() {
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, EcorePackage.eINSTANCE.getEPackage());
		return options;
	}

	private Map<String, Object> getLoadOptions() {
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, EcorePackage.eINSTANCE.getEPackage());
		return options;
	}
	

	private EPackage extractEPackageFromLoadedResource(Resource resource) {
		assertFalse(resource.getContents().isEmpty());
		EObject obj = resource.getContents().get(0);
		assertNotNull(obj);
		assertThat(obj).isInstanceOf(EPackage.class);
		EPackage ePackage = (EPackage) obj;
		assertThat(ePackage).isNotNull();
		return ePackage;
	}
	
	private EClass createEClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		return eClass;
	}
	
	private EAttribute createEAttribute(String name, EDataType type) {
		EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
		eAttribute.setName(name);
		eAttribute.setEType(type);
		return eAttribute;
	}
	
	private EReference createEReference(String name, EClassifier type) {
		EReference eReference = EcoreFactory.eINSTANCE.createEReference();
		eReference.setName(name);
		eReference.setEType(type);
		return eReference;
	}
	
	private EAnnotation createEAnnotation(String source, String key, String value) {
		EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
		eAnnotation.setSource(source);
		eAnnotation.getDetails().put(key, value);
		return eAnnotation;
	}
	
	private void addEAnnotation(EModelElement element, String source, String detailKey, String detailValue) {
		if(element.getEAnnotation(source) != null) {
			element.getEAnnotation(source).getDetails().put(detailKey, detailValue);
		} else {
			element.getEAnnotations().add(createEAnnotation(source, detailKey, detailValue));
		}
	}


	private boolean areJsonFilesTheSame(String file1, String file2) {
		ObjectMapper mapper = new ObjectMapper();
		mapper = mapper.rebuild().disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();
		// Load JSON files as tree structures
		JsonNode json1 = mapper.readTree(new File(file1));
		JsonNode json2 = mapper.readTree(new File(file2));
		// Compare the two JSON objects
		return JsonSchemaComparator.schemaEquals(json1, json2);
//		return json1.equals(json2);
	}
	
	private boolean areEPackagesTheSame(EPackage ePackage1, EPackage ePackage2) {
		Comparison comparison = EMFCompare.builder().build().compare(
				new DefaultComparisonScope(ePackage1, ePackage2, null)
			);
		return comparison.getDifferences().stream().filter(d -> !DifferenceKind.MOVE.equals(d.getKind())).toList().isEmpty();
	}

	/**
	 * Build a type map for oneOf structures by finding all child EClasses and mapping their unique feature names
	 * to the child EClass names.
	 *
	 * For JSON Schema oneOf, the parent is abstract with no features, and each variant is a child class
	 * with features. We need to find the feature that is UNIQUE to each child (appears in only one variant).
	 * This is typically the discriminating property like "kafka", "file", "mapping", "log", etc.
	 *
	 * @param ePackage The EPackage containing all classifiers
	 * @param parentClass The abstract parent EClass (e.g., InputNode, ProcessorNode)
	 * @return Map from unique feature name to child EClass name
	 */
	private Map<String, String> buildTypeMapForOneOf(EPackage ePackage, EClass parentClass) {
		Map<String, String> typeMap = new java.util.HashMap<>();

		// First, collect all children and their features
		java.util.List<EClass> children = new java.util.ArrayList<>();
		for (EClassifier classifier : ePackage.getEClassifiers()) {
			if (classifier instanceof EClass childClass) {
				// Check if this is a direct subtype of the parent (not the parent itself)
				if (!childClass.equals(parentClass) && childClass.getEAllSuperTypes().contains(parentClass)) {
					children.add(childClass);
				}
			}
		}

		// Count how many children have each feature name
		Map<String, Integer> featureCounts = new java.util.HashMap<>();
		Map<String, String> featureToClass = new java.util.HashMap<>();

		for (EClass childClass : children) {
			// Only consider direct features, not inherited ones
			for (EStructuralFeature feature : childClass.getEStructuralFeatures()) {
				String featureName = feature.getName();
				featureCounts.put(featureName, featureCounts.getOrDefault(featureName, 0) + 1);
				featureToClass.put(featureName, childClass.getName());
			}
		}

		// Only add features that appear in exactly one child (unique discriminators)
		for (EClass childClass : children) {
			for (EStructuralFeature feature : childClass.getEStructuralFeatures()) {
				String featureName = feature.getName();
				if (featureCounts.get(featureName) == 1) {
					typeMap.put(featureName, childClass.getName());
					System.out.println("  Mapping: " + featureName + " -> " + childClass.getName());
				}
			}
		}

		return typeMap;
	}


}