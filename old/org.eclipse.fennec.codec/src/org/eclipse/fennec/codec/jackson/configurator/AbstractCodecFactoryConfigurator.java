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
package org.eclipse.fennec.codec.jackson.configurator;

import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.fennec.codec.CodecFactory;
import org.eclipse.fennec.codec.CodecGeneratorFactory;
import org.eclipse.fennec.codec.CodecParserFactory;
import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.jackson.DefaultCodecJsonFactory;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonFactoryBuilder;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.json.JsonWriteFeature;

/**
 * Abstract implemenation of the {@link CodecFactoryConfigurator}
 * @author Mark Hoffmann
 * @since 11.08.2025
 */
public abstract class AbstractCodecFactoryConfigurator implements CodecFactoryConfigurator {

	final static Logger LOGGER = Logger.getLogger(AbstractCodecFactoryConfigurator.class.getName());
	protected CodecGeneratorFactory<?,?> genFactory;
	protected CodecParserFactory<?,?> parserFactory;
	JsonFactoryBuilder factoryBuilder;

	private class CodecFactoryBuilder<F extends JsonFactory, B extends JsonFactoryBuilder> extends JsonFactoryBuilder{


		/**
		 * Creates a new instance.
		 * @param base
		 */
		protected CodecFactoryBuilder(JsonFactory base) {
			super(base);
		}

		/* 
		 * (non-Javadoc)
		 * @see com.fasterxml.jackson.core.TSFBuilder#build()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public F build() {
			if(getGenFactory() == null && getParserFactory() == null) {
				return (F) new DefaultCodecJsonFactory(this);
			} else {
				return (F) new CodecFactory(this, getGenFactory(), getParserFactory());
			}	
		}
	}

	/**
	 * Creates a new instance.
	 */
	public AbstractCodecFactoryConfigurator() {
		super();
	}
	
	public void initialize(Map<String, Object> properties) {
		factoryBuilder = new CodecFactoryBuilder(JsonFactory.builder().build());
		buildAndConfigureCodecFactory(properties);
		
	}

	public JsonFactoryBuilder getFactoryBuilder() {
		return factoryBuilder;
	}

	/**
	 * Sets the genFactory.
	 * @param genFactory the genFactory to set
	 */
	public void setGenFactory(CodecGeneratorFactory<?,?> genFactory) {
		this.genFactory = genFactory;
	}
	
	/**
	 * Un-sets the genFactory.
	 * @param genFactory the genFactory to un-set
	 */
	public void unsetGenFactory(CodecGeneratorFactory<?,?> genFactory) {
		this.genFactory = genFactory;
	}

	/**
	 * Returns the genFactory.
	 * @return the genFactory
	 */
	protected CodecGeneratorFactory<?, ?> getGenFactory() {
		return genFactory;
	}

	/**
	 * Sets the parserFactory.
	 * @param parserFactory the parserFactory to set
	 */
	public void setParserFactory(CodecParserFactory<?,?> parserFactory) {
		this.parserFactory = parserFactory;
	}
	
	/**
	 * Un-sets the parserFactory.
	 * @param parserFactory the parserFactory to un-set
	 */
	public void unsetParserFactory(CodecParserFactory<?,?> parserFactory) {
		this.parserFactory = parserFactory;
	}

	/**
	 * Returns the parserFactory.
	 * @return the parserFactory
	 */
	protected CodecParserFactory<?, ?> getParserFactory() {
		return parserFactory;
	}

	protected void buildAndConfigureCodecFactory(Map<String, Object> properties) {
		if(properties == null || properties.isEmpty()) {
			return;
		}
		String[] enableFeatures = (String[]) properties.getOrDefault("enableFeatures", new String[0]);
		for(String f : enableFeatures) {
			setFeature(f, true);
		}
		String[] disableFeatures = (String[]) properties.getOrDefault("disableFeatures", new String[0]);
		for(String f : disableFeatures) {
			setFeature(f, false);
		}
	}

	private void setFeature(String featureString, boolean state) {
		if(featureString.contains(".")) {
			String prefix = featureString.substring(0, featureString.lastIndexOf("."));
			featureString = featureString.substring(featureString.lastIndexOf(".")+1);
			switch(prefix) {
			case "TokenStreamFactory.Feature":
				setTokenStreamFactoryFeature(featureString, state);
				break;
			case "StreamWriteFeature":
				setStreamWriteFeature(featureString, state);
				break;
			case "JsonWriteFeature":
				setJsonWriteFeature(featureString, state);
				break;
			case "StreamReadFeature":
				setStreamReadFeature(featureString, state);
				break;
			case "JsonReadFeature":
				setJsonReadFeature(featureString, state);
				break;			
			default:
				LOGGER.warning(String.format("Feature prefix %s not supported", prefix));
			}
		} else {
			setTokenStreamFactoryFeature(featureString, state);
			setStreamWriteFeature(featureString, state);
			setJsonWriteFeature(featureString, state);
			setStreamReadFeature(featureString, state);
			setJsonReadFeature(featureString, state);			
		}
	}

	private void setTokenStreamFactoryFeature(String featureString, boolean state) {
		try {
			if(state) factoryBuilder.enable(TokenStreamFactory.Feature.valueOf(featureString));
			else factoryBuilder.disable(TokenStreamFactory.Feature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No TokenStreamFactory feature with name %s has been found", featureString));
		} 

	}

	private void setStreamWriteFeature(String featureString, boolean state) {
		try {
			if(state) factoryBuilder.enable(StreamWriteFeature.valueOf(featureString));
			else factoryBuilder.disable(StreamWriteFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No StreamWriteFeature feature with name %s has been found", featureString));
		} 
	}

	private void setJsonWriteFeature(String featureString, boolean state) {
		try {
			if(state) factoryBuilder.enable(JsonWriteFeature.valueOf(featureString));
			else factoryBuilder.disable(JsonWriteFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No JsonWriteFeature feature with name %s has been found", featureString));
		} 
	}

	private void setStreamReadFeature(String featureString, boolean state) {
		try {
			if(state) factoryBuilder.enable(StreamReadFeature.valueOf(featureString));
			else factoryBuilder.disable(StreamReadFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No StreamReadFeature feature with name %s has been found", featureString));
		} 
	}

	private void setJsonReadFeature(String featureString, boolean state) {
		try {
			if(state) factoryBuilder.enable(JsonReadFeature.valueOf(featureString));
			else factoryBuilder.disable(JsonReadFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No JsonReadFeature feature with name %s has been found", featureString));
		} 
	}

}