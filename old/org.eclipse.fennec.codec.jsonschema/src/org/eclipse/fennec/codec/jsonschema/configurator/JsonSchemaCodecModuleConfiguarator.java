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

import org.eclipse.fennec.codec.CodecEMFDeserializers;
import org.eclipse.fennec.codec.CodecEMFSerializers;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.module.CodecModule.Builder;
import org.eclipse.fennec.codec.jsonschema.JsonSchemaCodecEMFDeserializers;
import org.eclipse.fennec.codec.jsonschema.JsonSchemaCodecEMFSerializers;

/**
 * 
 * @author ilenia
 * @since Oct 1, 2025
 */
public class JsonSchemaCodecModuleConfiguarator implements CodecModuleConfigurator {

	private CodecEMFSerializers serializers;
	private CodecEMFDeserializers deserializers;
	private CodecModule.Builder moduleBuilder;
	
	public JsonSchemaCodecModuleConfiguarator() {
		serializers = new JsonSchemaCodecEMFSerializers();
		deserializers = new JsonSchemaCodecEMFDeserializers();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.configurator.CodecModuleConfigurator#getCodecModuleBuilder()
	 */
	@Override
	public Builder getCodecModuleBuilder() {
		moduleBuilder = new CodecModule.Builder();
		configureModuleBuilder();
		return moduleBuilder;
	}
	
	private void configureModuleBuilder() {
		moduleBuilder
			.withIdOnTop(true)
			.withCodecModuleName("fennec-codec-jsonschema-module")
			.withCodecType("jsonschema")
			.withIdFeatureAsPrimaryKey(false)
			.withProxyKey("_proxy")
			.withRefKey("$ref")
			.withSerailizeSuperTypesAsArray(true)
			.withSerializeDefaultValue(false)
			.withSerializeEmptyValue(false)
			.withSerializeNullValue(false)
			.withSerializeIdField(false)
			.withSerializeSuperTypes(false)
			.withSerializeAllSuperTypes(false)
			.withSerializeType(true)
			.withTimestampKey("_timestamp")
			.withUseId(true)
			.withUseNamesFromExtendedMetaData(false)
			.withSuperTypeKey("_supertype")
			.withCodecEMFDeserializers(deserializers)
			.withCodecEMFSerializers(serializers);
	}

}
