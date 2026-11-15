/*
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
 */
package org.eclipse.fennec.codec.jsonschema.model.configuration;

import java.util.Hashtable;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.resource.Resource.Factory;

import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaFactory;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;

import org.eclipse.fennec.codec.jsonschema.model.impl.JsonSchemaPackageImpl;

import org.eclipse.fennec.codec.jsonschema.model.util.JsonSchemaResourceFactoryImpl;

import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;

import org.osgi.annotation.bundle.Capability;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import org.osgi.service.condition.Condition;
/**
 * The <b>PackageConfiguration</b> for the model.
 * The package will be registered into a OSGi base model registry.
 * 
 * @generated
 */
@Component(name = "JsonSchemaConfigurator")
@Capability( namespace = "osgi.service", attribute = { "objectClass:List<String>=\"org.eclipse.fennec.codec.jsonschema.model.util.JsonSchemaResourceFactoryImpl, org.eclipse.emf.ecore.resource.Resource$Factory\"" , "uses:=\"org.eclipse.emf.ecore.resource,org.eclipse.fennec.codec.jsonschema.model.util\"" })
@Capability( namespace = "osgi.service", attribute = { "objectClass:List<String>=\"org.eclipse.fennec.codec.jsonschema.model.JsonSchemaFactory, org.eclipse.emf.ecore.EFactory\"" , "uses:=\"org.eclipse.emf.ecore,org.eclipse.fennec.codec.jsonschema.model\"" })
@Capability( namespace = "osgi.service", attribute = { "objectClass:List<String>=\"org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage, org.eclipse.emf.ecore.EPackage\"" , "uses:=\"org.eclipse.emf.ecore,org.eclipse.fennec.codec.jsonschema.model\"" })
@Capability( namespace = "osgi.service", attribute = { "objectClass:List<String>=\"org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator\"" , "uses:=\"org.eclipse.emf.ecore,org.eclipse.fennec.codec.jsonschema.model\"" })
@Capability( namespace = "osgi.service", attribute = { "objectClass:List<String>=\"org.osgi.service.condition.Condition\"" , "uses:=org.osgi.service.condition" })
public class JsonSchemaConfigurationComponent {
	
	private ServiceRegistration<?> packageRegistration = null;
	private ServiceRegistration<EPackageConfigurator> ePackageConfiguratorRegistration = null;
	private ServiceRegistration<?> eFactoryRegistration = null;
	private ServiceRegistration<?> conditionRegistration = null;
	private ServiceRegistration<?> resourceFactoryRegistration = null;

	/**
	 * Activates the Configuration Component.
	 *
	 * @generated
	 */
	@Activate
	public void activate(BundleContext ctx) {
	
		checkEMFEcore(ctx);
		JsonSchemaPackage ePackage = JsonSchemaPackageImpl.eINSTANCE;
		
		if(!EPackage.Registry.INSTANCE.containsKey(JsonSchemaPackage.eNS_URI)){
			EPackage.Registry.INSTANCE.put(JsonSchemaPackage.eNS_URI, ePackage);
		}
		
		JsonSchemaEPackageConfigurator packageConfigurator = registerEPackageConfiguratorService(ePackage, ctx);
		registerResourceFactoryService(ctx);
		registerEPackageService(ePackage, packageConfigurator, ctx);
		registerEFactoryService(ePackage, packageConfigurator, ctx);
		registerConditionService(packageConfigurator, ctx);
	}
	
	/**
	 * We have to make sure that org.eclipse.emf.ecore is started, so we don't run 
	 * into start order issues due to the use of static access in EMF 
	 * @param ctx the {@link BundleContext} to use
	 */
	private void checkEMFEcore(BundleContext ctx) {
		Bundle[] bundles = ctx.getBundles();
		
		for(Bundle bundle : bundles) {
			if("org.eclipse.emf.ecore".equals(bundle.getSymbolicName())) {
				try {
					bundle.start();
				} catch (BundleException e) {
					System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Could not start Bundle org.eclipse.emf.ecore, something seems seriously wrong", e);
				}
				break;
			}
		}
	}
	
	/**
	 * Registers the JsonSchemaEPackageConfigurator as a service.
	 *
	 * @generated
	 */
	private JsonSchemaEPackageConfigurator registerEPackageConfiguratorService(JsonSchemaPackage ePackage, BundleContext ctx){
		JsonSchemaEPackageConfigurator packageConfigurator = new JsonSchemaEPackageConfigurator(ePackage);
		// register the EPackageConfigurator
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.putAll(packageConfigurator.getServiceProperties());
		ePackageConfiguratorRegistration = ctx.registerService(EPackageConfigurator.class, packageConfigurator, properties);

		return packageConfigurator;
	}

	/**
	 * Registers the JsonSchemaResourceFactoryImpl as a service.
	 *
	 * @generated
	 */
	private void registerResourceFactoryService(BundleContext ctx){
		JsonSchemaResourceFactoryImpl factory = new JsonSchemaResourceFactoryImpl();
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.putAll(factory.getServiceProperties());
		String[] serviceClasses = new String[] {JsonSchemaResourceFactoryImpl.class.getName(), Factory.class.getName()};
		resourceFactoryRegistration = ctx.registerService(serviceClasses, factory, properties);
	}

	/**
	 * Registers the JsonSchemaPackage as a service.
	 *
	 * @generated
	 */
	private void registerEPackageService(JsonSchemaPackage ePackage, JsonSchemaEPackageConfigurator packageConfigurator, BundleContext ctx){
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.putAll(packageConfigurator.getServiceProperties());
		String[] serviceClasses = new String[] {JsonSchemaPackage.class.getName(), EPackage.class.getName()};
		packageRegistration = ctx.registerService(serviceClasses, ePackage, properties);
	}

	/**
	 * Registers the JsonSchemaFactory as a service.
	 *
	 * @generated
	 */
	private void registerEFactoryService(JsonSchemaPackage ePackage, JsonSchemaEPackageConfigurator packageConfigurator, BundleContext ctx){
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.putAll(packageConfigurator.getServiceProperties());
		String[] serviceClasses = new String[] {JsonSchemaFactory.class.getName(), EFactory.class.getName()};
		eFactoryRegistration = ctx.registerService(serviceClasses, ePackage.getJsonSchemaFactory(), properties);
	}

	private void registerConditionService(JsonSchemaEPackageConfigurator packageConfigurator, BundleContext ctx){
		// register the EPackage
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.putAll(packageConfigurator.getServiceProperties());
		properties.put(Condition.CONDITION_ID, JsonSchemaPackage.eNS_URI);
		conditionRegistration = ctx.registerService(Condition.class, Condition.INSTANCE, properties);
	}

	/**
	 * Deactivates and unregisters everything.
	 *
	 * @generated
	 */
	@Deactivate
	public void deactivate() {
		conditionRegistration.unregister();
		eFactoryRegistration.unregister();
		packageRegistration.unregister();
		resourceFactoryRegistration.unregister();

		ePackageConfiguratorRegistration.unregister();
		EPackage.Registry.INSTANCE.remove(JsonSchemaPackage.eNS_URI);
	}
}
