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
package org.eclipse.fennec.codec.lorawan.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.options.CodecOptionsBuilder;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.options.CodecModelInfoOptions;
import org.eclipse.fennec.dragino.message.model.dragino.DraginoFactory;
import org.eclipse.fennec.dragino.message.model.dragino.DraginoLSE01Uplink;
import org.eclipse.fennec.dragino.message.model.dragino.DraginoPackage;
import org.eclipse.fennec.lorawan.uplink.model.lorawan.DeviceInfo;
import org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanFactory;
import org.eclipse.fennec.lorawan.uplink.model.lorawan.LorawanPackage;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json"),
		@Property(key = "enableFeatures", value = "SerializationFeature.INDENT_OUTPUT", type = Type.Array),
		@Property(key = "disableFeatures", value={"JsonWriteFeature.ESCAPE_FORWARD_SLASHES"}, type = Type.Array)

})
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
public class CodecLorawanSerializeTest extends JsonTestSetting{

	@InjectService(cardinality = 0, filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)")
	ServiceAware<ResourceSet> rsAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecFactoryConfigurator> codecFactoryAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<ObjectMapperConfigurator> mapperAware;
	
	@InjectService(cardinality = 0, filter = "(type=json)")
	ServiceAware<CodecModuleConfigurator> codecModuleAware;
	
	private ResourceSet resourceSet;	
	
	@BeforeEach()
	@Override
	public void beforeEach() throws Exception{
		super.beforeEach();
		codecFactoryAware.waitForService(2000l);
		mapperAware.waitForService(2000l);
		codecModuleAware.waitForService(2000l);	
		resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
	}
	
	@AfterEach() 
	@Override
	public void afterEach() throws IOException {
		super.afterEach();
	}
	
	@Test
	public void testSerializeParentPackage(@InjectService ServiceAware<LorawanPackage> packageAware) throws IOException, InterruptedException {

		LorawanPackage model = packageAware.waitForService(2000l);
		assertNotNull(model);

		Resource resource = resourceSet.createResource(URI.createURI(fileName));
		resource.getContents().add(EcoreUtil.copy(model));

		// Create options to exclude eGenericType from serialization for all Ecore classes
		Map<String, Object> options = new HashMap<>();
		options.put(CodecModelInfoOptions.CODEC_GLOBAL_IGNORE_FEATURES_LIST,
			java.util.List.of(EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE));
		resource.save(options);
		
	}
	

	@Test
	public void testSerializationDraginoParentOptions(@InjectService ServiceAware<DraginoPackage> packageAware) throws IOException {
		
		Resource resource = resourceSet.createResource(URI.createURI(fileName));	
		DraginoLSE01Uplink dragino = getDraginoUplink();
		resource.getContents().add(dragino);
		Map<String, Object> options = new HashMap<>();		
		resource.save(options);
		
		assertTrue(areJsonFilesTheSame(fileName, System.getProperty("test-data") +"dragino-ser.json"));		
	}
	
	@Test
	public void testSerializationDraginoTypeKey(@InjectService ServiceAware<DraginoPackage> packageAware) throws IOException {
		
		Resource resource = resourceSet.createResource(URI.createURI(fileName));	
		DraginoLSE01Uplink dragino = getDraginoUplink();
		resource.getContents().add(dragino);
		Map<String, Object> options = CodecOptionsBuilder.create().
				forClass(DraginoPackage.Literals.DRAGINO_LSE01_UPLINK).
				typeKey("eClass").build();
				
		resource.save(options);
		
		assertTrue(areJsonFilesTheSame(fileName, System.getProperty("test-data") +"dragino-ser-type-key.json"));		
	}
	
	@Test
	public void testSerializationDraginoTypeStrategy(@InjectService ServiceAware<DraginoPackage> packageAware) throws IOException {
		
		Resource resource = resourceSet.createResource(URI.createURI(fileName));	
		DraginoLSE01Uplink dragino = getDraginoUplink();
		resource.getContents().add(dragino);
		Map<String, Object> options = CodecOptionsBuilder.create().
				forClass(DraginoPackage.Literals.DRAGINO_LSE01_UPLINK).
				typeKey("eClass").
				typeStrategy("URI").
				build();
		
		resource.save(options);
		
		assertTrue(areJsonFilesTheSame(fileName, System.getProperty("test-data") +"dragino-ser-type-strategy.json"));		
	}
	
	private DraginoLSE01Uplink getDraginoUplink() {
		DraginoLSE01Uplink dragino = DraginoFactory.eINSTANCE.createDraginoLSE01Uplink();
		dragino.setDeduplicationId("ecd7dfa3-cbe9-40d9-9692-5fd83b9931ea");
		DeviceInfo deviceInfo = LorawanFactory.eINSTANCE.createDeviceInfo();
		deviceInfo.setApplicationId("d953cbf6-a1ae-4f18-b220-22ac369a473f");
		deviceInfo.setApplicationName("Test");
		deviceInfo.setDeviceProfileId("2937556d-6a1e-4d95-8171-edb4dd42c5a0");
		deviceInfo.setDeviceProfileName("Dragino_LSE01");
		dragino.setDeviceInfo(deviceInfo);		
		return dragino;
	}
	
	private boolean areJsonFilesTheSame(String file1, String file2) {
		ObjectMapper mapper = new ObjectMapper();
		// Load JSON files as tree structures
		JsonNode json1 = mapper.readTree(new File(file1));
		JsonNode json2 = mapper.readTree(new File(file2));
		// Compare the two JSON objects
		return json1.equals(json2);
	}
}
