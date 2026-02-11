/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.codec.mongo.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.gecko.mongo.osgi.MongoDatabaseProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, name= "MongoRF", service = Resource.Factory.class, property = { EMFNamespaces.EMF_CONFIGURATOR_NAME + "=mongo",
		EMFNamespaces.EMF_MODEL_PROTOCOL + "=mongodb" })
public class MongoResourceFactory extends ResourceFactoryImpl {

	private MongoDatabaseProvider provider;
	private CodecModelInfo modelInfo;
	private ObjectMapperConfigurator objMapperConfigurator;
	private CodecModuleConfigurator codecModuleConfigurator;

	@Override
	public Resource createResource(URI uri) {
		return new CodecMongoResource(uri, modelInfo, codecModuleConfigurator.getCodecModuleBuilder(), objMapperConfigurator.getObjMapperBuilderFactory(), provider);
	}

	@Reference
	public void setProvider(MongoDatabaseProvider provider) {
		this.provider = provider;
	}

	@Reference
	public void setModelInfo(CodecModelInfo modelInfo) {
		this.modelInfo = modelInfo;
	}

	@Reference(target="(type=mongo)")
	public void setObjMapperConfigurator(ObjectMapperConfigurator objMapperConfigurator) {
		this.objMapperConfigurator = objMapperConfigurator;
	}

	@Reference(target="(type=mongo)")
	public void setCodecModuleConfigurator(CodecModuleConfigurator codecModuleConfigurator) {
		this.codecModuleConfigurator = codecModuleConfigurator;
	}

}
