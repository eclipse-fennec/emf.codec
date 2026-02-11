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
package org.eclipse.fennec.codec.options;

/**
 * These are the options that can be used when loading/saving a Resource, in order
 * to overwrite the ObjectMapper configuration.
 * 
 * @author ilenia
 * @since Aug 6, 2024
 */
public interface ObjectMapperOptions {
	
	/** OBJ_MAPPER_DATE_FORMAT 
	 * to overwrite the "dateFormat" property of {@link org.gecko.codec.configurator.ObjectMapperConfigurator}
	 * */
	String OBJ_MAPPER_DATE_FORMAT = "obj.mapper.date.format";
	
	/** OBJ_MAPPER_LOCALE 
	 * to overwrite the "locale" property of {@link org.gecko.codec.configurator.ObjectMapperConfigurator}
	 * */
	String OBJ_MAPPER_LOCALE = "obj.mapper.locale";
	
	/** OBJ_MAPPER_TIME_ZONE 
	 * to overwrite the "timeZone" property of {@link org.gecko.codec.configurator.ObjectMapperConfigurator}
	 * */
	String OBJ_MAPPER_TIME_ZONE = "obj.mapper.time.zone";
		
	/** OBJ_MAPPER_SERIALIZATION_FEATURES_WITH 
	 * to specify a {@link List} of {@link tools.jackson.databind.SerializationFeature} 
	 * that should be enabled;
	 * */
	String OBJ_MAPPER_SERIALIZATION_FEATURES_WITH = "obj.mapper.serialization.features.with";
	
	/** OBJ_MAPPER_SERIALIZATION_FEATURES_WITHOUT 
	 * to specify a {@link List} of {@link tools.jackson.databind.SerializationFeature} 
	 * that should be disabled;
	 * */
	String OBJ_MAPPER_SERIALIZATION_FEATURES_WITHOUT = "obj.mapper.serialization.features.without";
	
	/** OBJ_MAPPER_DESERIALIZATION_FEATURES_WITH 
	 * to specify a {@link List} of {@link tools.jackson.databind.DeserializationFeature} 
	 * that should be enabled;
	 * */
	String OBJ_MAPPER_DESERIALIZATION_FEATURES_WITH = "obj.mapper.deserialization.features.with";
	
	/** OBJ_MAPPER_DESERIALIZATION_FEATURES_WITHOUT 
	 * to specify a {@link List} of {@link tools.jackson.databind.DeserializationFeature} 
	 * that should be disabled;
	 * */
	String OBJ_MAPPER_DESERIALIZATION_FEATURES_WITHOUT = "obj.mapper.deserialization.features.without";
	
	/** OBJ_MAPPER_FEATURES_WITH 
	 * to specify a {@link List} of {@link tools.jackson.databind.MapperFeature} 
	 * that should be enabled;
	 * */
	String OBJ_MAPPER_FEATURES_WITH = "obj.mapper.features.with";
	
	/** OBJ_MAPPER_FEATURES_WITHOUT 
	 * to specify a {@link List} of {@link tools.jackson.databind.MapperFeature} 
	 * that should be disabled;
	 * */
	String OBJ_MAPPER_FEATURES_WITHOUT = "obj.mapper.features.without";
	
	/** OBJ_MAPPER_FORMAT_SER_FEATURES_WITH 
	 * to specify a {@link List} of {@link tools.jackson.core.FormatFeature} 
	 * that should be enabled;
	 * For now we only supports {@link tools.jackson.core.json.JsonWriteFeature}
	 * */
	String OBJ_MAPPER_FORMAT_SER_FEATURES_WITH = "obj.mapper.format.ser.features.with";
	
	/** OBJ_MAPPER_FORMAT_SER_FEATURES_WITHOUT 
	 * to specify a {@link List} of {@link tools.jackson.core.FormatFeature} 
	 * that should be disabled;
	 * For now we only supports {@link tools.jackson.core.json.JsonWriteFeature}
	 * */
	String OBJ_MAPPER_FORMAT_SER_FEATURES_WITHOUT = "obj.mapper.format.ser.features.without";
	
	/** OBJ_MAPPER_FORMAT_DESER_FEATURES_WITH 
	 * to specify a {@link List} of {@link tools.jackson.core.FormatFeature} 
	 * that should be enabled;
	 * For now we only supports {@link tools.jackson.core.json.JsonReadFeature}
	 * */
	String OBJ_MAPPER_FORMAT_DESER_FEATURES_WITH = "obj.mapper.format.deser.features.with";
	
	/** OBJ_MAPPER_FORMAT_DESER_FEATURES_WITHOUT 
	 * to specify a {@link List} of {@link tools.jackson.core.FormatFeature} 
	 * that should be disabled;
	 * For now we only supports {@link tools.jackson.core.json.JsonReadFeature}
	 * */
	String OBJ_MAPPER_FORMAT_DESER_FEATURES_WITHOUT = "obj.mapper.format.deser.features.without";

}
