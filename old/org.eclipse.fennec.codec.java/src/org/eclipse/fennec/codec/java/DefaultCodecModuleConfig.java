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
package org.eclipse.fennec.codec.java;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.codec.options.CodecModuleConfig;
import org.osgi.util.converter.Converters;

/**
 * Factory for creating {@link CodecModuleConfig} instances in non-OSGi environments.
 * Uses the OSGi Converter specification to convert a Map into an annotation proxy.
 *
 * @author Mark Hoffmann
 * @since 02.12.2025
 */
public class DefaultCodecModuleConfig {

	/**
	 * Creates a CodecModuleConfig with all default values.
	 *
	 * @return a CodecModuleConfig instance with defaults
	 */
	public static CodecModuleConfig create() {
		return create(new HashMap<>());
	}

	/**
	 * Creates a CodecModuleConfig from a configuration map.
	 * Any values not specified in the map will use the annotation defaults.
	 *
	 * @param config the configuration map
	 * @return a CodecModuleConfig instance
	 */
	public static CodecModuleConfig create(Map<String, Object> config) {
		return Converters.standardConverter().convert(config).to(CodecModuleConfig.class);
	}

	/**
	 * Creates a builder for constructing a CodecModuleConfig with custom values.
	 *
	 * @return a new Builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder for creating CodecModuleConfig instances with a fluent API.
	 */
	public static class Builder {
		private final Map<String, Object> config = new HashMap<>();

		public Builder type(String type) {
			config.put("type", type);
			return this;
		}

		public Builder codecModuleName(String name) {
			config.put("codecModuleName", name);
			return this;
		}

		public Builder superTypeKey(String key) {
			config.put("superTypeKey", key);
			return this;
		}

		public Builder refKey(String key) {
			config.put("refKey", key);
			return this;
		}

		public Builder proxyKey(String key) {
			config.put("proxyKey", key);
			return this;
		}

		public Builder timestampKey(String key) {
			config.put("timestampKey", key);
			return this;
		}

		public Builder serializeDefaultValue(boolean value) {
			config.put("serializeDefaultValue", value);
			return this;
		}

		public Builder serializeNullValue(boolean value) {
			config.put("serializeNullValue", value);
			return this;
		}

		public Builder serializeEmptyValue(boolean value) {
			config.put("serializeEmptyValue", value);
			return this;
		}

		public Builder useNamesFromExtendedMetaData(boolean value) {
			config.put("useNamesFromExtendedMetaData", value);
			return this;
		}

		public Builder useId(boolean value) {
			config.put("useId", value);
			return this;
		}

		public Builder idOnTop(boolean value) {
			config.put("idOnTop", value);
			return this;
		}

		public Builder serializeIdField(boolean value) {
			config.put("serializeIdField", value);
			return this;
		}

		public Builder idFeatureAsPrimaryKey(boolean value) {
			config.put("idFeatureAsPrimaryKey", value);
			return this;
		}

		public Builder writeEnumLiterals(boolean value) {
			config.put("writeEnumLiterals", value);
			return this;
		}

		public Builder deserializeType(boolean value) {
			config.put("deserializeType", value);
			return this;
		}

		public Builder serializeType(boolean value) {
			config.put("serializeType", value);
			return this;
		}

		public Builder serializeSuperTypes(boolean value) {
			config.put("serializeSuperTypes", value);
			return this;
		}

		public Builder serializeAllSuperTypes(boolean value) {
			config.put("serializeAllSuperTypes", value);
			return this;
		}

		public Builder serializeSuperTypesAsArray(boolean value) {
			config.put("serializeSuperTypesAsArray", value);
			return this;
		}

		/**
		 * Builds the CodecModuleConfig from the configured values.
		 *
		 * @return a CodecModuleConfig instance
		 */
		public CodecModuleConfig build() {
			return create(config);
		}
	}
}
