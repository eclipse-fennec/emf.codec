/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.java;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.fennec.codec.CodecEMFDeserializers;
import org.eclipse.fennec.codec.CodecEMFSerializers;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoPackage;
import org.eclipse.fennec.codec.info.helper.CodecInfoHolderHelper;
import org.eclipse.fennec.codec.info.impl.CodecModelInfoImpl;
import org.eclipse.fennec.codec.jackson.DefaultCodecFactoryConfigurator;
import org.eclipse.fennec.codec.jackson.DefaultObjectMapperConfigurator;
import org.eclipse.fennec.codec.jackson.databind.deser.DefaultCodecEMFDeserializers;
import org.eclipse.fennec.codec.jackson.databind.ser.DefaultCodecEMFSerializers;
import org.eclipse.fennec.codec.jackson.module.DefaultCodecModuleConfigurator;
import org.eclipse.fennec.codec.json.resource.JsonResourceFactory;
import org.eclipse.fennec.codec.options.CodecModuleConfig;
import org.osgi.service.component.ComponentServiceObjects;

/**
 * Helper class to set up the codec framework in non-OSGi environments.
 * Wires all necessary components together and provides a configured {@link JsonResourceFactory}.
 *
 * <p>Usage example:
 * <pre>
 * CodecSetup setup = new CodecSetup();
 * setup.registerEPackage(PersonPackage.eINSTANCE);
 *
 * ResourceSet rs = setup.createResourceSet();
 * Resource resource = rs.createResource(URI.createURI("test.json"));
 * resource.getContents().add(person);
 * resource.save(null);
 * </pre>
 *
 * @author Mark Hoffmann
 * @since 02.12.2025
 */
public class CodecSetup {

	private final ResourceSet referenceResourceSet;
	private final ComponentServiceObjects<ResourceSet> rsServiceObjects;
	private final CodecInfoHolderHelper codecInfoHolderHelper;
	private final CodecModelInfoImpl codecModelInfo;
	private final DefaultCodecFactoryConfigurator codecFactoryConfigurator;
	private final DefaultObjectMapperConfigurator objectMapperConfigurator;
	private final DefaultCodecModuleConfigurator codecModuleConfigurator;
	private final CodecEMFSerializers serializers;
	private final CodecEMFDeserializers deserializers;
	private final JsonResourceFactory jsonResourceFactory;

	/**
	 * Creates a new CodecSetup with default configuration.
	 */
	public CodecSetup() {
		this(DefaultCodecModuleConfig.create());
	}

	/**
	 * Creates a new CodecSetup with custom configuration.
	 *
	 * @param moduleConfig the codec module configuration
	 */
	public CodecSetup(CodecModuleConfig moduleConfig) {
		this(moduleConfig, Collections.emptyMap());
	}

	/**
	 * Creates a new CodecSetup with custom configuration and ObjectMapper properties.
	 *
	 * @param moduleConfig the codec module configuration
	 * @param objectMapperProperties additional ObjectMapper properties
	 */
	public CodecSetup(CodecModuleConfig moduleConfig, Map<String, Object> objectMapperProperties) {
		// 1. Create reference ResourceSet for ComponentServiceObjects
		this.referenceResourceSet = new ResourceSetImpl();
		this.rsServiceObjects = new ResourceSetComponentServiceObject(referenceResourceSet);

		// 2. Create CodecInfoHolderHelper
		this.codecInfoHolderHelper = new CodecInfoHolderHelper(rsServiceObjects);

		// 3. Create and configure CodecModelInfo
		this.codecModelInfo = new CodecModelInfoImpl();
		this.codecModelInfo.setCodecInfoHolderHelper(codecInfoHolderHelper);
		this.codecModelInfo.activate();

		// 4. Create and configure CodecFactoryConfigurator
		this.codecFactoryConfigurator = new DefaultCodecFactoryConfigurator();
		// activate() is called in constructor with null properties

		// 5. Create and configure ObjectMapperConfigurator
		this.objectMapperConfigurator = new DefaultObjectMapperConfigurator();
		this.objectMapperConfigurator.setCodecFactoryConfigurator(codecFactoryConfigurator);
		this.objectMapperConfigurator.activate(objectMapperProperties);

		// 6. Create serializers and deserializers
		this.serializers = new DefaultCodecEMFSerializers();
		this.deserializers = new DefaultCodecEMFDeserializers();

		// 7. Create and configure CodecModuleConfigurator
		this.codecModuleConfigurator = new DefaultCodecModuleConfigurator();
		this.codecModuleConfigurator.setSerializers(serializers);
		this.codecModuleConfigurator.setDeserializers(deserializers);
		this.codecModuleConfigurator.activate(moduleConfig);

		// 8. Create and configure JsonResourceFactory
		this.jsonResourceFactory = new JsonResourceFactory();
		this.jsonResourceFactory.setModelInfo(codecModelInfo);
		this.jsonResourceFactory.setObjMapperConfigurator(objectMapperConfigurator);
		this.jsonResourceFactory.setCodecModuleConfigurator(codecModuleConfigurator);
		
		initializeDefaults();
	}

	/**
	 * Initializes the default {@link EPackage} 
	 */
	private void initializeDefaults() {
		registerEPackage(EcorePackage.eINSTANCE);
		registerEPackage(XMLTypePackage.eINSTANCE);
		registerEPackage(XMLNamespacePackage.eINSTANCE);
		registerEPackage(CodecInfoPackage.eINSTANCE);
		XMIResourceFactoryImpl xmiResourceFactory = new XMIResourceFactoryImpl();
		referenceResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", xmiResourceFactory);
		referenceResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", xmiResourceFactory);
		referenceResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", xmiResourceFactory);
		referenceResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl());
		
		referenceResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("json", jsonResourceFactory);
		referenceResourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("application/json", jsonResourceFactory);
	}

	/**
	 * Registers an EPackage with the codec framework.
	 * This is necessary for the codec to properly handle objects from this package.
	 *
	 * @param ePackage the EPackage to register
	 * @return this setup instance for method chaining
	 */
	public CodecSetup registerEPackage(EPackage ePackage) {
		codecModelInfo.bindEPackage(ePackage);
		referenceResourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		return this;
	}

	/**
	 * Creates a new ResourceSet configured with the JSON codec.
	 *
	 * @return a new configured ResourceSet
	 */
	public ResourceSet createResourceSet() {
		ResourceSet rs = rsServiceObjects.getService();
		
		return rs;
	}

	/**
	 * Returns the JsonResourceFactory for direct use.
	 *
	 * @return the configured JsonResourceFactory
	 */
	public Resource.Factory getJsonResourceFactory() {
		return jsonResourceFactory;
	}

	/**
	 * Returns the CodecModelInfo for advanced configuration.
	 *
	 * @return the CodecModelInfo instance
	 */
	public CodecModelInfo getCodecModelInfo() {
		return codecModelInfo;
	}

	/**
	 * Returns the ObjectMapperConfigurator for advanced configuration.
	 *
	 * @return the ObjectMapperConfigurator instance
	 */
	public ObjectMapperConfigurator getObjectMapperConfigurator() {
		return objectMapperConfigurator;
	}

	/**
	 * Returns the CodecModuleConfigurator for advanced configuration.
	 *
	 * @return the CodecModuleConfigurator instance
	 */
	public CodecModuleConfigurator getCodecModuleConfigurator() {
		return codecModuleConfigurator;
	}
}
