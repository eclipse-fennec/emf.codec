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
package org.eclipse.fennec.codec.ecowitt.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * 
 * @author mark
 * @since 02.08.2024
 */
@Component(immediate = true, name = "EcowittRF", service = {Resource.Factory.class, EcoWittResourceFactory.class}, 
		property = {EMFNamespaces.EMF_CONFIGURATOR_NAME + "=CodecEcoWitt", EMFNamespaces.EMF_MODEL_FILE_EXT + "=ecowitt", EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/ecowitt"})
public class EcoWittResourceFactory extends ResourceFactoryImpl {

	private CodecModelInfo modelInfo;
	private ObjectMapperConfigurator objMapperConfigurator;
	private CodecModuleConfigurator codecModuleConfigurator;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
	 */
	@Override
	public Resource createResource(URI uri) {
		return new EcoWittResource(uri, modelInfo, codecModuleConfigurator.getCodecModuleBuilder(), objMapperConfigurator.getObjMapperBuilderFactory());
	}

	@Reference
	public void setModelInfo(CodecModelInfo modelInfo) {
		this.modelInfo = modelInfo;
	}

	@Reference(target="(type=ecowitt)")
	public void setObjMapperConfigurator(ObjectMapperConfigurator objMapperConfigurator) {
		this.objMapperConfigurator = objMapperConfigurator;
	}

	@Reference(target="(type=json)")
	public void setCodecModuleConfigurator(CodecModuleConfigurator codecModuleConfigurator) {
		this.codecModuleConfigurator = codecModuleConfigurator;
	}

}
