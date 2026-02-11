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
package org.eclipse.fennec.codec.ecowitt.factory;

import java.util.Map;

import org.eclipse.fennec.codec.CodecGeneratorFactory;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.ecowitt.parser.EcoWittParserFactory;
import org.eclipse.fennec.codec.jackson.configurator.AbstractCodecFactoryConfigurator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;


/**
 * Default implementation of {@link CodecFactoryConfigurator} 
 * 
 * @author ilenia
 * @since Nov 14, 2024
 */
@Component(immediate = true, service = CodecFactoryConfigurator.class, property = {"type=ecowitt"})
public class EcowittCodecFactoryConfigurator extends AbstractCodecFactoryConfigurator {

	@Activate
	public void activate(Map<String, Object> properties) {
		initialize(properties);
		setParserFactory(new EcoWittParserFactory());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.configurator.AbstractCodecFactoryConfigurator#getGenFactory()
	 */
	@Override
	protected CodecGeneratorFactory<?, ?> getGenFactory() {
		// No generation supported
		return null;
	}

}