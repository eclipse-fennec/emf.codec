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
package org.eclipse.fennec.codec.jsonschema.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.jsonschema.configurator.JsonSchemaCodecModuleConfiguarator;
import org.eclipse.fennec.codec.jsonschema.configurator.JsonSchemaObjectMapperConfigurator;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author ilenia
 * @since Sep 29, 2025
 */
@Component(immediate = true, name = "JsonSchemaRF", service = {Resource.Factory.class, JsonSchemaResourceFactory.class}, 
property = {EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecJson", EMFNamespaces.EMF_MODEL_FILE_EXT + "=json", EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/schema+json"})
public class JsonSchemaResourceFactory extends ResourceFactoryImpl {

	private CodecModelInfo modelInfo;
	private ObjectMapperConfigurator objMapperConfigurator;
	private CodecModuleConfigurator codecModuleConfigurator;

	@Activate
	public void activate() {
		codecModuleConfigurator = new JsonSchemaCodecModuleConfiguarator();
		objMapperConfigurator = new JsonSchemaObjectMapperConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Resource createResource(URI uri) {
		return new CodecJsonSchemaResource(uri, modelInfo, codecModuleConfigurator.getCodecModuleBuilder(), objMapperConfigurator.getObjMapperBuilderFactory());
	}

	@Reference
	public void setModelInfo(CodecModelInfo modelInfo) {
		this.modelInfo = modelInfo;
	}

}
