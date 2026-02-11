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
package org.eclipse.fennec.codec.mongo.codec;

import java.util.Map;

import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.jackson.configurator.AbstractCodecFactoryConfigurator;
import org.eclipse.fennec.codec.mongo.MongoGeneratorFactory;
import org.eclipse.fennec.codec.mongo.MongoParserFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;


/**
 * Default implementation of {@link CodecFactoryConfigurator} 
 * 
 * @author ilenia
 * @since Nov 14, 2024
 */
@Component(name="MongoCodecFactoryConfigurator", service = CodecFactoryConfigurator.class, property = "type=mongo", configurationPolicy = ConfigurationPolicy.REQUIRE)
public class MongoCodecFactoryConfigurator extends AbstractCodecFactoryConfigurator {

	@Activate
	public void activate(Map<String, Object> properties) {
		initialize(properties);
		setParserFactory(new MongoParserFactory());
		setGenFactory(new MongoGeneratorFactory());
	}
	
}