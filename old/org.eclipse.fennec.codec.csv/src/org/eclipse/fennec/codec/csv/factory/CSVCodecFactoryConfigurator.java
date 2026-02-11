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
package org.eclipse.fennec.codec.csv.factory;

import java.util.Map;

import org.eclipse.fennec.codec.CodecGeneratorFactory;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.csv.parser.CSVParserFactory;
import org.eclipse.fennec.codec.jackson.configurator.AbstractCodecFactoryConfigurator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * 
 * @author ilenia
 * @since Sep 22, 2025
 */
@Component(immediate = true, service = CodecFactoryConfigurator.class, property = "type=csv")
public class CSVCodecFactoryConfigurator extends AbstractCodecFactoryConfigurator {
	
	@Activate
	public void activate(Map<String, Object> properties) {
		initialize(properties);
		setParserFactory(new CSVParserFactory());
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
