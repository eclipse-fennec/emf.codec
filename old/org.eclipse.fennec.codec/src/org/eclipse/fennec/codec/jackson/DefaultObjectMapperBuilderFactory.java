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

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperBuilderFactory;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.json.JsonMapper.Builder;

/**
 * Default implementation of {@link ObjectMapperBuilderFactory}
 * @author ilenia
 * @since Oct 30, 2024
 */
public class DefaultObjectMapperBuilderFactory implements ObjectMapperBuilderFactory {

	private final Map<String, Object> properties;
	private CodecFactoryConfigurator codecFactoryConfigurator;
	private static final Logger LOGGER = Logger.getLogger(DefaultObjectMapperBuilderFactory.class.getName());

	
	public DefaultObjectMapperBuilderFactory(Map<String, Object> properties, CodecFactoryConfigurator codecFactoryConfigurator) {
		this.properties = properties;
		this.codecFactoryConfigurator = codecFactoryConfigurator;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.demo.jackson.ObjectMapperBuilderFactory#createObjectMapperBuilder()
	 */
	@Override
	public Builder createObjectMapperBuilder() {
		Builder objMapperBuilder = JsonMapper.builder(codecFactoryConfigurator.getFactoryBuilder().build());		
		return buildAndConfigureObjectMapper(objMapperBuilder);
	}
	
	private Builder buildAndConfigureObjectMapper(Builder objMapperBuilder) {
		if(properties == null || properties.isEmpty()) {
			return objMapperBuilder;
		}
		String[] enableFeatures = (String[]) properties.getOrDefault("enableFeatures", new String[0]);
		for(String f : enableFeatures) {
			setFeature(f, true, objMapperBuilder);
		}
		String[] disableFeatures = (String[]) properties.getOrDefault("disableFeatures", new String[0]);
		for(String f : disableFeatures) {
			setFeature(f, false, objMapperBuilder);
		}
		String dfStr = (String) properties.getOrDefault("dateFormat", "yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dfStr);
		objMapperBuilder.defaultDateFormat(simpleDateFormat);
		
		String localeStr = (String) properties.getOrDefault("locale", "en-US");
		Locale loc = new Locale.Builder().setLanguageTag(localeStr).build();
		objMapperBuilder.defaultLocale(loc);
		
		String tzStr = (String) properties.getOrDefault("timeZone", "Europe/Berlin");
		TimeZone tz = TimeZone.getTimeZone(tzStr);
		objMapperBuilder.defaultTimeZone(tz);
		
		return objMapperBuilder;
	}
	
	private void setFeature(String featureString, boolean state, Builder objMapperBuilder) {
		if(featureString.contains(".")) {
			String prefix = featureString.substring(0, featureString.lastIndexOf("."));
			featureString = featureString.substring(featureString.lastIndexOf(".")+1);
			switch(prefix) {
			case "MapperFeature":
				setMapperFeature(featureString, state, objMapperBuilder);
				break;
			case "SerializationFeature":
				setSerializationFeature(featureString, state, objMapperBuilder);
				break;
			case "DeserializationFeature":
				setDeserializationFeature(featureString, state, objMapperBuilder);
				break;
			case "StreamWriteFeature":
				setStreamWriteFeature(featureString, state, objMapperBuilder);
				break;
			case "JsonWriteFeature":
				setJsonWriteFeature(featureString, state, objMapperBuilder);
				break;
			case "StreamReadFeature":
				setStreamReadFeature(featureString, state, objMapperBuilder);
				break;
			case "JsonReadFeature":
				setJsonReadFeature(featureString, state, objMapperBuilder);
				break;
			case "DateTimeFeature":
				setDateTimeFeature(featureString, state, objMapperBuilder);
				break;
			case "EnumFeature":
				setEnumFeature(featureString, state, objMapperBuilder);
				break;
			default:
				LOGGER.warning(String.format("Feature prefix %s not supported", prefix));
			}
		} else {
			setMapperFeature(featureString, state, objMapperBuilder);
			setSerializationFeature(featureString, state, objMapperBuilder);
			setDeserializationFeature(featureString, state, objMapperBuilder);
			setStreamReadFeature(featureString, state, objMapperBuilder);
			setStreamWriteFeature(featureString, state, objMapperBuilder);
			setJsonReadFeature(featureString, state, objMapperBuilder);
			setJsonWriteFeature(featureString, state, objMapperBuilder);
			setDateTimeFeature(featureString, state, objMapperBuilder);
			setEnumFeature(featureString, state, objMapperBuilder);
		}
	}
	
	
	private void setEnumFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(EnumFeature.valueOf(featureString));
			else objMapperBuilder.disable(EnumFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No EnumFeature with name %s has been found", featureString));
		} 		
	}

	
	private void setDateTimeFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(DateTimeFeature.valueOf(featureString));
			else objMapperBuilder.disable(DateTimeFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No DateTimeFeature with name %s has been found", featureString));
		} 		
	}

	private void setMapperFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(MapperFeature.valueOf(featureString));
			else objMapperBuilder.disable(MapperFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No MapperFeature with name %s has been found", featureString));
		} 
	}
	
	private void setSerializationFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(SerializationFeature.valueOf(featureString));
			else objMapperBuilder.disable(SerializationFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No SerializationFeature with name %s has been found", featureString));
		} 
	}
	
	private void setDeserializationFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(DeserializationFeature.valueOf(featureString));
			else objMapperBuilder.disable(DeserializationFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No DeserializationFeature feature with name %s has been found", featureString));
		} 
	}
	
	private void setStreamWriteFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(StreamWriteFeature.valueOf(featureString));
			else objMapperBuilder.disable(StreamWriteFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No StreamWriteFeature feature with name %s has been found", featureString));
		} 
	}
	
	private void setJsonWriteFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(JsonWriteFeature.valueOf(featureString));
			else objMapperBuilder.disable(JsonWriteFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No JsonWriteFeature feature with name %s has been found", featureString));
		} 
	}
	
	private void setStreamReadFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(StreamReadFeature.valueOf(featureString));
			else objMapperBuilder.disable(StreamReadFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No StreamReadFeature feature with name %s has been found", featureString));
		} 
	}
	
	private void setJsonReadFeature(String featureString, boolean state, Builder objMapperBuilder) {
		try {
			if(state) objMapperBuilder.enable(JsonReadFeature.valueOf(featureString));
			else objMapperBuilder.disable(JsonReadFeature.valueOf(featureString));
			return;
		} catch(Exception e) {
			LOGGER.warning(String.format("No JsonReadFeature feature with name %s has been found", featureString));
		} 
	}

}
