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
package org.eclipse.fennec.codec.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.csv.resource.CSVResource;
import org.eclipse.fennec.codec.json.resource.CodecJsonResource;
import org.eclipse.fennec.codec.jsonschema.resource.CodecJsonSchemaResource;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
		@Property(key = "type", value="json")
})
@WithFactoryConfiguration(factoryPid = "CodecFactoryConfigurator", location = "?", name = "csv")
@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "csv", properties = {
		@Property(key = "type", value="csv"),
		@Property(key = "codecFactoryConfigurator.target", value="(type=csv)")
})
public class CodecResourceTest {
	
	@InjectService(cardinality = 0, filter = "(&(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson)(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecCSV))")
	ServiceAware<ResourceSet> rsAware;
	
	@InjectService(cardinality = 0, filter = "(" + EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/schema+json)")
	ServiceAware<Resource.Factory> rfAware;
	
	private ResourceSet resourceSet;
	private Resource.Factory resourceFactory;
	
	@BeforeEach()
	public void beforeEach() throws InterruptedException{
		resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
		
		resourceFactory = rfAware.waitForService(2000l);
		assertNotNull(resourceFactory);
	}
	
	@Test
	public void testCodecResourceDiffFileExtensionPlusContentType()  {
	
		Resource jsonRes = resourceSet.createResource(URI.createURI("test.json"), "application/json");
		assertInstanceOf(CodecJsonResource.class, jsonRes);
		
		Resource csvResource = resourceSet.createResource(URI.createURI("test.csv"), "application/csv");
		assertInstanceOf(CSVResource.class, csvResource);
	}
	
	@Test
	public void testCodecResourceSameFileExtensionPlusContentType()  {
	
		Resource jsonRes = resourceSet.createResource(URI.createURI("test.json"), "application/json");
		assertInstanceOf(CodecJsonResource.class, jsonRes);
		
//		The ResourceFactoryRegistry looks always in the file extension map first. So it finds a proper CodecJsonResource
//		for the file extension json, and so it does not even care about our specified content-type.
		Resource jsonSchemaRes = resourceSet.createResource(URI.createURI("test.json"), "application/schema+json");
		assertFalse(jsonSchemaRes instanceof CodecJsonSchemaResource);
		
//		That's why we have to explicitly look in the ResourceFactory
		jsonSchemaRes = resourceFactory.createResource(URI.createURI("test.json"));
		assertInstanceOf(CodecJsonSchemaResource.class, jsonSchemaRes);
	}

	@Test
	public void testCodecResourceOnlyContentType()  {
	
		Resource jsonRes = resourceSet.createResource(URI.createURI("test"), "application/json");
		assertInstanceOf(CodecJsonResource.class, jsonRes);
		
		Resource jsonSchemaRes = resourceSet.createResource(URI.createURI("test"), "application/schema+json");
		assertInstanceOf(CodecJsonSchemaResource.class, jsonSchemaRes);
	}
}
