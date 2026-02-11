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
package org.eclipse.fennec.codec.jackson;

import java.util.Map;

import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperBuilderFactory;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * Default implementation of {@link ObjectMapperConfigurator}
 * @author ilenia
 * @since Aug 14, 2024
 */
@Component(name = "DefaultObjectMapperConfigurator", service = ObjectMapperConfigurator.class, 
configurationPolicy = ConfigurationPolicy.REQUIRE, property = "type=json")
public class DefaultObjectMapperConfigurator implements ObjectMapperConfigurator {
	
	private CodecFactoryConfigurator codecFactoryConfigurator;
	private Map<String, Object> properties;
	
	@Activate
	public void activate(Map<String, Object> properties) {
		this.properties = properties;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.configurator.ObjectMapperConfigurator#getObjMapperBuilderFactory()
	 */
	@Override
	public ObjectMapperBuilderFactory getObjMapperBuilderFactory() {
		DefaultObjectMapperBuilderFactory factory = new DefaultObjectMapperBuilderFactory(properties, codecFactoryConfigurator);
		return factory;
	}

	@Reference(name="codecFactoryConfigurator", target="(type=json)")
	public void setCodecFactoryConfigurator(CodecFactoryConfigurator codecFactoryConfigurator) {
		this.codecFactoryConfigurator = codecFactoryConfigurator;
	}
}
