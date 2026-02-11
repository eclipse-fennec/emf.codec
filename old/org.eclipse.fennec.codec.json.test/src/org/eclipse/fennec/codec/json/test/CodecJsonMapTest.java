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
package org.eclipse.fennec.codec.json.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.gecko.codec.demo.model.person.PersonFactory;
import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.codec.demo.model.person.SimpleMap;
import org.gecko.codec.demo.model.person.SimpleValue;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import tools.jackson.databind.SerializationFeature;

@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "json") })
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "json") })
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value = "json") })
public class CodecJsonMapTest {

	@InjectService(filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ResourceSet resourceSet;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;

	private String mapFileName;
	
	@InjectBundleContext
	BundleContext ctx;
	
	@BeforeEach()
	public void beforeEach() throws InterruptedException {
		mapFileName = "map_".concat(UUID.randomUUID().toString()).concat(".json");
		codecFactoryAware.waitForService(2000l);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);
	}

	@AfterEach
	public void afterEach() throws IOException {
		if (mapFileName != null) {
			Path path = Paths.get(mapFileName);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}
	}
	@Test
	public void testLoadMapWithStringKeyWithMapValue() throws IOException {
		Resource resource = resourceSet.createResource(URI.createURI(ctx.getBundle().getEntry("test-data/test-map.json").toString()));
		Map<String, Object> options = CodecOptionsBuilder.create()
			.rootObject(PersonPackage.eINSTANCE.getSimpleMap())
			.build();
		resource.load(options);

		assertThat(resource.getContents()).hasSize(1);
		assertThat(resource.getContents().get(0)).isInstanceOf(SimpleMap.class);

		SimpleMap types = (SimpleMap) resource.getContents().get(0);

		assertThat(types.getStringMapValues()).hasSize(2);

		EMap<String, SimpleValue> mapValues = types.getStringMapValues();

		SimpleValue hello = mapValues.get("Hello");
		assertThat(hello.getValue()).isEqualTo(1);
		SimpleValue world = mapValues.get("World");
		assertThat(world.getValue()).isEqualTo(2);
	}
	@Test
	public void testDynamic() throws IOException {

		// load ecore
		Resource ecoreResource = resourceSet.createResource(URI.createURI(ctx.getBundle().getEntry("test-data/map.ecore").toString()));
		ecoreResource.load(null);
		EPackage epackage = (EPackage) ecoreResource.getContents().get(0);
		EClass simpleMapClassifier = (EClass) epackage.getEClassifier("SimpleMap");
		EClass simpleValueClassifier = (EClass) epackage.getEClassifier("SimpleValue");
		EStructuralFeature simpleValueFeature = simpleValueClassifier.getEStructuralFeature("value");

		// load dynamic eobjects from json with classifier from ecore
		Resource resource = resourceSet.createResource(URI.createURI(ctx.getBundle().getEntry("test-data/test-map.json").toString()));

		Map<String, Object> options = CodecOptionsBuilder.create()
			.rootObject(simpleMapClassifier)
			.build();
		resource.load(options);

		assertThat(resource.getContents()).hasSize(1);

		EObject simpleMap = resource.getContents().get(0);
		assertSimpleMap(simpleValueFeature, simpleMap);

		// save dynamic eobjects
		Resource saveResource = resourceSet.createResource(URI.createURI(mapFileName));
		saveResource.getContents().add(simpleMap);
		Map<String, Object> saveOptions = CodecOptionsBuilder.create()
			.serializeIdField(false)
			.serializeType(false)
			.build();
		saveResource.save(saveOptions);
		saveResource.unload();

		// load from saved json
		Resource loadResource = resourceSet.createResource(URI.createURI(mapFileName));
		Map<String, Object> loadOptions = CodecOptionsBuilder.create()
			.rootObject(simpleMapClassifier)
			.build();
		loadResource.load(loadOptions);

		assertThat(loadResource.getContents()).hasSize(1);
		assertSimpleMap(simpleValueFeature, loadResource.getContents().get(0));
	}

	private void assertSimpleMap(EStructuralFeature simpleValueFeature, EObject simpleMap) {
		assertThat(simpleMap.eClass().getName()).isEqualTo("SimpleMap");
		EStructuralFeature stringMapValuesFeature = simpleMap.eClass().getEStructuralFeature("stringMapValues");
		assertThat(stringMapValuesFeature).isNotNull();
		@SuppressWarnings("unchecked")
		EMap<String, EObject> mapValues = (EMap<String, EObject>) simpleMap.eGet(stringMapValuesFeature);

		EObject simple1 = mapValues.get("Hello");
		assertThat(simple1.eGet(simpleValueFeature)).isEqualTo(1);

		EObject simple2 = mapValues.get("World");
		assertThat(simple2.eGet(simpleValueFeature)).isEqualTo(2);
	}

	@Test
	void testNullValue() throws IOException {

		Resource saveResource = resourceSet.createResource(URI.createURI(mapFileName));
		SimpleMap simpleMap = PersonFactory.eINSTANCE.createSimpleMap();
		EMap<String, SimpleValue> map = simpleMap.getStringMapValues();
		map.put("1", null);

		saveResource.getContents().add(simpleMap);
		Map<String, Object> options = CodecOptionsBuilder.create()
			.serializeDefaultValue(true)
			.serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
			.build();
		saveResource.save(options);
		
		assertFileContains("\"1\" : null");

		Resource loadResource = resourceSet.createResource(URI.createURI(mapFileName));
		Map<String, Object> loadOptions = CodecOptionsBuilder.create()
			.rootObject(PersonPackage.eINSTANCE.getSimpleMap())
			.build();
		loadResource.load(loadOptions);

		assertThat(loadResource.getContents()).hasSize(1);
		SimpleMap simpleMap2 = (SimpleMap) loadResource.getContents().get(0);
		EMap<String, SimpleValue> map2 = simpleMap2.getStringMapValues();
		assertThat(map2).hasSize(1);
		assertThat(map2.get("1")).isNull();
	}

	private void assertFileContains(String expected) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(mapFileName))) {
			String line = reader.readLine();
			boolean found = false;
			while (line != null) {
				if (line.contains(expected)) {
					found = true;
				}
				line = reader.readLine();
			}
			assertTrue(found);
		}
	}
}
