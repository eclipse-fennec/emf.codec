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
package org.eclipse.fennec.codec.jsonschema.configurator;

import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperBuilderFactory;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.eclipse.fennec.codec.jackson.DefaultCodecFactoryConfigurator;
import org.eclipse.fennec.codec.jackson.DefaultObjectMapperBuilderFactory;

/**
 * 
 * @author ilenia
 * @since Oct 1, 2025
 */
public class JsonSchemaObjectMapperConfigurator implements ObjectMapperConfigurator {
	
	private CodecFactoryConfigurator codecFactoryConfigurator;;
	
	public JsonSchemaObjectMapperConfigurator() {
		codecFactoryConfigurator = new DefaultCodecFactoryConfigurator();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator#getObjMapperBuilderFactory()
	 */
	@Override
	public ObjectMapperBuilderFactory getObjMapperBuilderFactory() {
		DefaultObjectMapperBuilderFactory factory = new DefaultObjectMapperBuilderFactory(null, codecFactoryConfigurator);
		return factory;
	}

}
