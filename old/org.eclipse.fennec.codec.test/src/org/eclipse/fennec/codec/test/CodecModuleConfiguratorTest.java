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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Scalar;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
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
@ExtendWith(MockitoExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class CodecModuleConfiguratorTest {

	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test")
	@Test
	public void testCodecModuleConfigCodecDefault(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("json", module.getCodecType());	
		assertEquals("_supertype", module.getSuperTypeKey());		
		assertEquals("$ref", module.getRefKey());		
		assertEquals("_proxy", module.getProxyKey());		
		assertEquals("_timestamp", module.getTimestampKey());		
		assertEquals("eclipse-fennec-codec-module", module.getModuleName());		
		assertFalse(module.isSerializeDefaultValue());		
		assertFalse(module.isSerializeNullValue());		
		assertFalse(module.isSerializeEmptyValue());		
		assertFalse(module.isUseNamesFromExtendedMetaData());
		assertTrue(module.isUseId());
		assertTrue(module.isIdOnTop());
		assertFalse(module.isSerializeIdField());
		assertTrue(module.isIdFeatureAsPrimaryKey());
		assertTrue(module.isSerializeType());
		assertFalse(module.isSerializeSuperTypes());
		assertTrue(module.isSerializeSuperTypesAsArray());		
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="test")
	})
	@Test
	public void testCodecModuleConfigCodecType(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("test", module.getCodecType());		
	}
	
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "superTypeKey", value="test")
	})
	@Test
	public void testCodecModuleConfigCodecSuperTypeKey(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("test", module.getSuperTypeKey());		
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "refKey", value="test")
	})
	@Test
	public void testCodecModuleConfigCodecRefKey(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("test", module.getRefKey());		
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "proxyKey", value="test")
	})
	@Test
	public void testCodecModuleConfigCodecProxyKey(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("test", module.getProxyKey());		
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "timestampKey", value="test")
	})
	@Test
	public void testCodecModuleConfigCodecTimestampKey(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("test", module.getTimestampKey());		
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "codecModuleName", value="test")
	})
	@Test
	public void testCodecModuleConfigCodecModuleName(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertEquals("test", module.getModuleName());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "serializeDefaultValue", value="true", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigSerDefValue(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isSerializeDefaultValue());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "serializeEmptyValue", value="true", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigSerEmptyValue(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isSerializeEmptyValue());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "serializeNullValue", value="true", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigSerNullValue(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isSerializeNullValue());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "serializeIdField", value="true", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigSerIdField(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isSerializeIdField());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "serializeSuperTypes", value="true", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigSerSuperTypes(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertTrue(module.isSerializeSuperTypes());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "useNamesFromExtendedMetaData", value="false", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigUseNamesFromExtendedMetadata(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isUseNamesFromExtendedMetaData());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "useId", value="false", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigUseId(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isUseId());
	}

	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "idOnTop", value="false", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigIdOnTop(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isIdOnTop());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "idFeatureAsPrimaryKey", value="false", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigIdFeatureAsPrimaryKey(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isIdFeatureAsPrimaryKey());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "serializeType", value="false", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigSerType(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isSerializeType());
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "test", properties = {
			@Property(key = "serializeSuperTypesAsArray", value="false", scalar = Scalar.Boolean)
	})
	@Test
	public void testCodecModuleConfigSerSuperTypesAsArray(@InjectService(timeout = 2000l) CodecModuleConfigurator codecModuleConfigurator) throws InterruptedException, IOException {
	
		assertNotNull(codecModuleConfigurator);
		CodecModule module = codecModuleConfigurator.getCodecModuleBuilder().build();
		assertFalse(module.isSerializeSuperTypesAsArray());
	}
}
