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
package org.eclipse.fennec.codec.jackson.module;

import org.eclipse.fennec.codec.CodecEMFDeserializers;
import org.eclipse.fennec.codec.CodecEMFSerializers;
import org.eclipse.fennec.codec.configurator.CodecModuleConfigurator;
import org.eclipse.fennec.codec.options.CodecModuleConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * Default implementation of {@link CodecModuleConfigurator}
 * @author ilenia
 * @since Aug 15, 2024
 */
@Component(name = "DefaultCodecModuleConfigurator", service = CodecModuleConfigurator.class, 
	configurationPolicy = ConfigurationPolicy.REQUIRE, property = "type=json")
public class DefaultCodecModuleConfigurator implements CodecModuleConfigurator {
	
	private CodecEMFSerializers serializers;
	private CodecEMFDeserializers deserializers;
	private CodecModule.Builder moduleBuilder;
	private CodecModuleConfig codecConfig; 

	@Activate
	public void activate(CodecModuleConfig codecConfig) {
		this.codecConfig = codecConfig;
		moduleBuilder = new CodecModule.Builder();
		configureModuleBuilder(codecConfig);
	}
	
	private void configureModuleBuilder(CodecModuleConfig codecConfig) {
		moduleBuilder
			.withIdOnTop(codecConfig.idOnTop())
			.withCodecModuleName(codecConfig.codecModuleName())
			.withCodecType(codecConfig.type())
			.withIdFeatureAsPrimaryKey(codecConfig.idFeatureAsPrimaryKey())
			.withProxyKey(codecConfig.proxyKey())
			.withRefKey(codecConfig.refKey())
			.withSerailizeSuperTypesAsArray(codecConfig.serializeSuperTypesAsArray())
			.withSerializeDefaultValue(codecConfig.serializeDefaultValue())
			.withSerializeEmptyValue(codecConfig.serializeEmptyValue())
			.withSerializeNullValue(codecConfig.serializeNullValue())
			.withSerializeIdField(codecConfig.serializeIdField())
			.withSerializeSuperTypes(codecConfig.serializeSuperTypes())
			.withSerializeAllSuperTypes(codecConfig.serializeAllSuperTypes())
			.withSerializeType(codecConfig.serializeType())
			.withTimestampKey(codecConfig.timestampKey())
			.withUseId(codecConfig.useId())
			.withUseNamesFromExtendedMetaData(codecConfig.useNamesFromExtendedMetaData())
			.withSuperTypeKey(codecConfig.superTypeKey())
			.withCodecEMFDeserializers(deserializers)
			.withCodecEMFSerializers(serializers);
	}
	
	public CodecModule.Builder getCodecModuleBuilder() {
		moduleBuilder = new CodecModule.Builder();
		configureModuleBuilder(codecConfig);
		return moduleBuilder;
	}

	@Reference
	public void setSerializers(CodecEMFSerializers serializers) {
		this.serializers = serializers;
	}

	@Reference
	public void setDeserializers(CodecEMFDeserializers deserializers) {
		this.deserializers = deserializers;
	}

}
