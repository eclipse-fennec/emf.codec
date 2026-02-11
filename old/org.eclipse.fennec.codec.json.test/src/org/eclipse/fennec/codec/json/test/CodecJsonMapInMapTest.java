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
import org.gecko.codec.demo.model.person.MapInMap;
import org.gecko.codec.demo.model.person.PersonPackage;
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
public class CodecJsonMapInMapTest {

	@InjectService(filter="("+EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ResourceSet resourceSet;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;

	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;

	@InjectBundleContext
	BundleContext ctx;

	private String mapFileName;
	
	@BeforeEach()
	public void beforeEach() throws InterruptedException {
		mapFileName = "map_".concat(UUID.randomUUID().toString()).concat(".json");
		codecFactoryAware.waitForService(2000l);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);
	}
	
	@AfterEach
	public void afterEach() throws IOException {
		if(mapFileName != null) {
			Path path = Paths.get(mapFileName);
			if(Files.exists(path)) {
				Files.delete(path);
			}
		}
	}
			
	@Test
	public void testDynamic() throws IOException {
		
		// load ecore
		Resource ecoreResource = resourceSet.createResource(URI.createURI(ctx.getBundle().getEntry("test-data/map.ecore").toString()));
		ecoreResource.load(null);
		EPackage epackage = (EPackage) ecoreResource.getContents().get(0);
		EClass mapInMapClassifier = (EClass) epackage.getEClassifier("MapInMap");
		EClass simpleValueClassifier = (EClass) epackage.getEClassifier("SimpleValue");
		EStructuralFeature simpleValueFeature = simpleValueClassifier.getEStructuralFeature("value");
		
		// load dynamic eobjects from json with classifier from ecore
		Resource resource = resourceSet.createResource(URI.createURI(ctx.getBundle().getEntry("test-data/test-map-in-map.json").toString()));

		Map<String, Object> options = CodecOptionsBuilder.create()
			.rootObject(mapInMapClassifier)
			.build();
		resource.load(options);

		assertThat(resource.getContents()).hasSize(1);

		EObject mapInMap = resource.getContents().get(0);
		assertEObjects(simpleValueFeature, mapInMap);

		// save dynamic eobjects
		Resource saveResource = resourceSet.createResource(URI.createURI(mapFileName));
		saveResource.getContents().add(mapInMap);
		Map<String, Object> saveOptions = CodecOptionsBuilder.create()
			.serializeIdField(false)
			.serializeType(false)
			.build();
		saveResource.save(saveOptions);
		saveResource.unload();
		
		
		// load from saved json
		Resource loadResource = resourceSet.createResource(URI.createURI(mapFileName));
		Map<String, Object> loadOptions = CodecOptionsBuilder.create()
			.rootObject(mapInMapClassifier)
			.build();
		loadResource.load(loadOptions);

		assertThat(loadResource.getContents()).hasSize(1);
		assertEObjects(simpleValueFeature, loadResource.getContents().get(0));
	}

	@Test
	void testGeneratedClasses() throws IOException {
		Resource resource = resourceSet.createResource(URI.createURI(ctx.getBundle().getEntry("test-data/test-map-in-map.json").toString()));

		Map<String, Object> options = CodecOptionsBuilder.create()
			.rootObject(PersonPackage.eINSTANCE.getMapInMap())
			.build();
		resource.load(options);

		assertThat(resource.getContents()).hasSize(1);
		assertThat(resource.getContents().get(0)).isInstanceOf(MapInMap.class);

		MapInMap mapInMap = (MapInMap) resource.getContents().get(0);

		assertMapInMap(mapInMap);
		
		Resource saveResource = resourceSet.createResource(URI.createURI(mapFileName));
		saveResource.getContents().add(mapInMap);
		Map<String, Object> saveOptions = CodecOptionsBuilder.create()
			.serializeIdField(false)
			.serializeType(false)
			.build();
		saveResource.save(saveOptions);
		saveResource.unload();
		
		
		Resource loadResource = resourceSet.createResource(URI.createURI(mapFileName));
		Map<String, Object> loadOptions = CodecOptionsBuilder.create()
			.rootObject(PersonPackage.eINSTANCE.getMapInMap())
			.build();
		loadResource.load(loadOptions);

		assertThat(loadResource.getContents()).hasSize(1);
		assertThat(loadResource.getContents().get(0)).isInstanceOf(MapInMap.class);

		assertMapInMap((MapInMap) loadResource.getContents().get(0));
	}
	

	private void assertMapInMap(MapInMap mapInMap) {
		assertThat(mapInMap.getStringMapInMapValues()).hasSize(2);

		EMap<String, EMap<String, SimpleValue>> mapValues = mapInMap.getStringMapInMapValues();

		EMap<String, SimpleValue> hello = mapValues.get("Hello");
		assertThat(hello).hasSize(2);
		assertThat(hello.get("1.1").getValue()).isEqualTo(11);
		assertThat(hello.get("1.2").getValue()).isEqualTo(12);

		EMap<String, SimpleValue> world = mapValues.get("World");
		assertThat(world).hasSize(2);
		assertThat(world.get("2.1").getValue()).isEqualTo(21);
		assertThat(world.get("2.2").getValue()).isEqualTo(22);
	}
	
	private void assertEObjects(EStructuralFeature simpleValueFeature, EObject mapInMap) {
		assertThat(mapInMap.eClass().getName()).isEqualTo("MapInMap");
		EStructuralFeature stringMapInMapValuesFeature = mapInMap.eClass().getEStructuralFeature("stringMapInMapValues");
		assertThat(stringMapInMapValuesFeature).isNotNull();
		@SuppressWarnings("unchecked")
		EMap<String,EMap<String,EObject>> mapInMapValues = (EMap<String,EMap<String,EObject>>) mapInMap.eGet(stringMapInMapValuesFeature);
		
		EMap<String,EObject> helloMap = mapInMapValues.get("Hello");
		EObject simple11 = helloMap.get("1.1");
		assertThat(simple11.eGet(simpleValueFeature)).isEqualTo(11);
		EObject simple12 = helloMap.get("1.2");
		assertThat(simple12.eGet(simpleValueFeature)).isEqualTo(12);
		
		EMap<String,EObject> worldMap = mapInMapValues.get("World");
		EObject simple21 = worldMap.get("2.1");
		assertThat(simple21.eGet(simpleValueFeature)).isEqualTo(21);
		EObject simple22 = worldMap.get("2.2");
		assertThat(simple22.eGet(simpleValueFeature)).isEqualTo(22);
	}
}
