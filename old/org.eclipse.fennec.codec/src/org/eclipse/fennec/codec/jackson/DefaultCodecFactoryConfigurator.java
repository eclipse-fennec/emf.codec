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

import org.eclipse.fennec.codec.CodecGeneratorFactory;
import org.eclipse.fennec.codec.CodecParserFactory;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.jackson.configurator.AbstractCodecFactoryConfigurator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;


/**
 * Default implementation of {@link CodecFactoryConfigurator} 
 * 
 * @author ilenia
 * @since Nov 14, 2024
 */
@Component(name = "DefaultCodecFactoryConfigurator", service = CodecFactoryConfigurator.class, 
configurationPolicy = ConfigurationPolicy.REQUIRE, property = {"type=json"})
public class DefaultCodecFactoryConfigurator extends AbstractCodecFactoryConfigurator {

	public DefaultCodecFactoryConfigurator() {
		initialize(null);
	}

	@Activate
	public void activate(Map<String, Object> properties) {
		initialize(properties);

	}

	/**
	 * Sets the genFactory.
	 * @param genFactory the genFactory to set
	 */
	@Reference(target="(type=json)", cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
	public void setGenFactory(CodecGeneratorFactory<?,?> genFactory) {
		super.setGenFactory(genFactory);
	}
	
	public void unsetGenFactory(CodecGeneratorFactory<?,?> genFactory) {
		super.unsetGenFactory(genFactory);
	}
	

	/**
	 * Sets the parserFactory.
	 * @param parserFactory the parserFactory to set
	 */
	@Reference(target="(type=json)", cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
	public void setParserFactory(CodecParserFactory<?,?> parserFactory) {
		super.setParserFactory(parserFactory);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.configurator.AbstractCodecFactoryConfigurator#unsetParserFactory(org.eclipse.fennec.codec.CodecParserFactory)
	 */
	@Override
	public void unsetParserFactory(CodecParserFactory<?, ?> parserFactory) {
		super.unsetParserFactory(parserFactory);
	}

}