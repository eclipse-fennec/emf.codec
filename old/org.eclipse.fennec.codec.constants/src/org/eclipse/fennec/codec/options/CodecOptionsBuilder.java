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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.jackson.core.FormatFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType;

/**
 * Builder for creating codec load/save options in a fluent and type-safe manner.
 *
 * <p>Usage example:</p>
 * <pre>
 * Map&lt;String, Object&gt; options = CodecOptionsBuilder.create()
 *     .rootObject(PersonPackage.eINSTANCE.getPerson())
 *     .serializationFeaturesWith(SerializationFeature.INDENT_OUTPUT)
 *     .forClass(PersonPackage.eINSTANCE.getPerson())
 *         .idStrategy("ID_FIELD")
 *         .idKey("_id")
 *         .and()
 *     .forClass(PersonPackage.eINSTANCE.getAddress())
 *         .idStrategy("COMBINED")
 *         .idSeparator("-")
 *     .build();
 *
 * resource.save(options);
 * </pre>
 *
 * @author ilenia
 * @since Oct 1, 2025
 */
public class CodecOptionsBuilder {

	protected final Map<String, Object> options = new HashMap<>();
	protected final Map<EClass, Map<String, Object>> classOptions = new HashMap<>();
	protected ClassOptionsBuilder currentClassBuilder;

	protected CodecOptionsBuilder() {
	}

	/**
	 * Creates a new options builder.
	 *
	 * @return a new CodecOptionsBuilder instance
	 */
	public static CodecOptionsBuilder create() {
		return new CodecOptionsBuilder();
	}

	// ========== Resource-level options ==========

	/**
	 * Sets the root object for deserialization.
	 * Mandatory if type information is not available in the serialized document.
	 *
	 * @param rootObject the EClass to use as root object
	 * @return this builder
	 */
	public CodecOptionsBuilder rootObject(EClass rootObject) {
		options.put(CodecResourceOptions.CODEC_ROOT_OBJECT, rootObject);
		return this;
	}

	// ========== ObjectMapper options ==========

	/**
	 * Sets a custom date format.
	 *
	 * @param dateFormat the date format
	 * @return this builder
	 */
	public CodecOptionsBuilder dateFormat(DateFormat dateFormat) {
		options.put(ObjectMapperOptions.OBJ_MAPPER_DATE_FORMAT, dateFormat);
		return this;
	}

	/**
	 * Sets the locale.
	 *
	 * @param locale the locale
	 * @return this builder
	 */
	public CodecOptionsBuilder locale(Locale locale) {
		options.put(ObjectMapperOptions.OBJ_MAPPER_LOCALE, locale);
		return this;
	}

	/**
	 * Sets the time zone.
	 *
	 * @param timeZone the time zone
	 * @return this builder
	 */
	public CodecOptionsBuilder timeZone(TimeZone timeZone) {
		options.put(ObjectMapperOptions.OBJ_MAPPER_TIME_ZONE, timeZone);
		return this;
	}

	/**
	 * Enables serialization features.
	 *
	 * @param features the serialization features to enable
	 * @return this builder
	 */
	@SafeVarargs
	public final CodecOptionsBuilder serializationFeaturesWith(SerializationFeature... features) {
		@SuppressWarnings("unchecked")
		List<SerializationFeature> featureList = (List<SerializationFeature>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITH,
			k -> new ArrayList<SerializationFeature>()
		);
		for (SerializationFeature feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Disables serialization features.
	 *
	 * @param features the serialization features to disable
	 * @return this builder
	 */
	@SafeVarargs
	public final CodecOptionsBuilder serializationFeaturesWithout(SerializationFeature... features) {
		@SuppressWarnings("unchecked")
		List<SerializationFeature> featureList = (List<SerializationFeature>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_SERIALIZATION_FEATURES_WITHOUT,
			k -> new ArrayList<SerializationFeature>()
		);
		for (SerializationFeature feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Enables deserialization features.
	 *
	 * @param features the deserialization features to enable
	 * @return this builder
	 */
	@SafeVarargs
	public final CodecOptionsBuilder deserializationFeaturesWith(DeserializationFeature... features) {
		@SuppressWarnings("unchecked")
		List<DeserializationFeature> featureList = (List<DeserializationFeature>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_DESERIALIZATION_FEATURES_WITH,
			k -> new ArrayList<DeserializationFeature>()
		);
		for (DeserializationFeature feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Disables deserialization features.
	 *
	 * @param features the deserialization features to disable
	 * @return this builder
	 */
	@SafeVarargs
	public final CodecOptionsBuilder deserializationFeaturesWithout(DeserializationFeature... features) {
		@SuppressWarnings("unchecked")
		List<DeserializationFeature> featureList = (List<DeserializationFeature>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_DESERIALIZATION_FEATURES_WITHOUT,
			k -> new ArrayList<DeserializationFeature>()
		);
		for (DeserializationFeature feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Enables mapper features.
	 *
	 * @param features the mapper features to enable
	 * @return this builder
	 */
	@SafeVarargs
	public final CodecOptionsBuilder mapperFeaturesWith(MapperFeature... features) {
		@SuppressWarnings("unchecked")
		List<MapperFeature> featureList = (List<MapperFeature>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_FEATURES_WITH,
			k -> new ArrayList<MapperFeature>()
		);
		for (MapperFeature feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Disables mapper features.
	 *
	 * @param features the mapper features to disable
	 * @return this builder
	 */
	@SafeVarargs
	public final CodecOptionsBuilder mapperFeaturesWithout(MapperFeature... features) {
		@SuppressWarnings("unchecked")
		List<MapperFeature> featureList = (List<MapperFeature>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_FEATURES_WITHOUT,
			k -> new ArrayList<MapperFeature>()
		);
		for (MapperFeature feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Enables format serialization features.
	 *
	 * @param features the format serialization features to enable
	 * @return this builder
	 */
	@SafeVarargs
	public final <T extends FormatFeature> CodecOptionsBuilder formatSerFeaturesWith(T... features) {
		@SuppressWarnings("unchecked")
		List<T> featureList = (List<T>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_FORMAT_SER_FEATURES_WITH,
			k -> new ArrayList<>()
		);
		for (T feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Disables format serialization features.
	 *
	 * @param features the format serialization features to disable
	 * @return this builder
	 */
	@SafeVarargs
	public final <T extends FormatFeature> CodecOptionsBuilder formatSerFeaturesWithout(T... features) {
		@SuppressWarnings("unchecked")
		List<T> featureList = (List<T>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_FORMAT_SER_FEATURES_WITHOUT,
			k -> new ArrayList<>()
		);
		for (T feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Enables format deserialization features.
	 *
	 * @param features the format deserialization features to enable
	 * @return this builder
	 */
	@SafeVarargs
	public final <T extends FormatFeature> CodecOptionsBuilder formatDeserFeaturesWith(T... features) {
		@SuppressWarnings("unchecked")
		List<T> featureList = (List<T>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_FORMAT_DESER_FEATURES_WITH,
			k -> new ArrayList<>()
		);
		for (T feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	/**
	 * Disables format deserialization features.
	 *
	 * @param features the format deserialization features to disable
	 * @return this builder
	 */
	@SafeVarargs
	public final <T extends FormatFeature> CodecOptionsBuilder formatDeserFeaturesWithout(T... features) {
		@SuppressWarnings("unchecked")
		List<T> featureList = (List<T>) options.computeIfAbsent(
			ObjectMapperOptions.OBJ_MAPPER_FORMAT_DESER_FEATURES_WITHOUT,
			k -> new ArrayList<>()
		);
		for (T feature : features) {
			featureList.add(feature);
		}
		return this;
	}

	// ========== CodecModule options ==========

	/**
	 * Sets whether to serialize default values.
	 *
	 * @param serialize true to serialize default values
	 * @return this builder
	 */
	public CodecOptionsBuilder serializeDefaultValue(boolean serialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_DEFAULT_VALUE, serialize);
		return this;
	}

	/**
	 * Sets whether to serialize empty values.
	 *
	 * @param serialize true to serialize empty values
	 * @return this builder
	 */
	public CodecOptionsBuilder serializeEmptyValue(boolean serialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_EMPTY_VALUE, serialize);
		return this;
	}

	/**
	 * Sets whether to serialize null values.
	 *
	 * @param serialize true to serialize null values
	 * @return this builder
	 */
	public CodecOptionsBuilder serializeNullValue(boolean serialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_NULL_VALUE, serialize);
		return this;
	}

	/**
	 * Sets whether to use names from extended metadata.
	 *
	 * @param useNames true to use names from extended metadata
	 * @return this builder
	 */
	public CodecOptionsBuilder useNamesFromExtendedMetadata(boolean useNames) {
		options.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, useNames);
		return this;
	}

	/**
	 * Sets whether to use ID.
	 *
	 * @param useId true to use ID
	 * @return this builder
	 */
	public CodecOptionsBuilder useId(boolean useId) {
		options.put(CodecModuleOptions.CODEC_MODULE_USE_ID, useId);
		return this;
	}

	/**
	 * Sets whether to place ID on top in serialized output.
	 *
	 * @param onTop true to place ID on top
	 * @return this builder
	 */
	public CodecOptionsBuilder idOnTop(boolean onTop) {
		options.put(CodecModuleOptions.CODEC_MODULE_ID_ON_TOP, onTop);
		return this;
	}

	/**
	 * Sets whether to serialize the ID field.
	 *
	 * @param serialize true to serialize the ID field
	 * @return this builder
	 */
	public CodecOptionsBuilder serializeIdField(boolean serialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_ID_FIELD, serialize);
		return this;
	}

	/**
	 * Sets whether to use ID feature as primary key.
	 *
	 * @param usePrimaryKey true to use ID feature as primary key
	 * @return this builder
	 */
	public CodecOptionsBuilder idFeatureAsPrimaryKey(boolean usePrimaryKey) {
		options.put(CodecModuleOptions.CODEC_MODULE_ID_FEATURE_AS_PRIMARY_KEY, usePrimaryKey);
		return this;
	}

	/**
	 * Sets whether to deserialize type information.
	 *
	 * @param deserialize true to deserialize type information
	 * @return this builder
	 */
	public CodecOptionsBuilder deserializeType(boolean deserialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_DESERIALIZE_TYPE, deserialize);
		return this;
	}

	/**
	 * Sets whether to serialize type information.
	 *
	 * @param serialize true to serialize type information
	 * @return this builder
	 */
	public CodecOptionsBuilder serializeType(boolean serialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_TYPE, serialize);
		return this;
	}

	/**
	 * Sets whether to serialize super types.
	 *
	 * @param serialize true to serialize super types
	 * @return this builder
	 */
	public CodecOptionsBuilder serializeSuperTypes(boolean serialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_SUPER_TYPES, serialize);
		return this;
	}

	/**
	 * Sets whether to serialize all super types.
	 *
	 * @param serialize true to serialize all super types
	 * @return this builder
	 */
	public CodecOptionsBuilder serializeAllSuperTypes(boolean serialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_ALL_SUPER_TYPES, serialize);
		return this;
	}

	/**
	 * Sets whether to serialize super types as array.
	 *
	 * @param serialize true to serialize super types as array
	 * @return this builder
	 */
	public CodecOptionsBuilder serializeSuperTypesAsArray(boolean serialize) {
		options.put(CodecModuleOptions.CODEC_MODULE_SERIALIZE_SUPER_TYPES_AS_ARRAY, serialize);
		return this;
	}

	/**
	 * Sets the reference key.
	 *
	 * @param key the reference key
	 * @return this builder
	 */
	public CodecOptionsBuilder referenceKey(String key) {
		options.put(CodecModuleOptions.CODEC_MODULE_REFERENCE_KEY, key);
		return this;
	}

	/**
	 * Sets the proxy key.
	 *
	 * @param key the proxy key
	 * @return this builder
	 */
	public CodecOptionsBuilder proxyKey(String key) {
		options.put(CodecModuleOptions.CODEC_MODULE_PROXY_KEY, key);
		return this;
	}

	/**
	 * Sets the timestamp key.
	 *
	 * @param key the timestamp key
	 * @return this builder
	 */
	public CodecOptionsBuilder timestampKey(String key) {
		options.put(CodecModuleOptions.CODEC_MODULE_TIMESTAMP_KEY, key);
		return this;
	}

	/**
	 * Sets the supertype key.
	 *
	 * @param key the supertype key
	 * @return this builder
	 */
	public CodecOptionsBuilder supertypeKey(String key) {
		options.put(CodecModuleOptions.CODEC_MODULE_SUPERTYPE_KEY, key);
		return this;
	}

	/**
	 * Sets whether to write enum literal.
	 *
	 * @param write true to write enum literal
	 * @return this builder
	 */
	public CodecOptionsBuilder writeEnumLiteral(boolean write) {
		options.put(CodecModuleOptions.CODEC_MODULE_WRITE_ENUM_LITERAL, write);
		return this;
	}

	/**
	 * Sets the reference deserializer.
	 *
	 * @param deserializer the reference deserializer
	 * @return this builder
	 */
	public CodecOptionsBuilder referenceDeserializer(Object deserializer) {
		options.put(CodecModuleOptions.CODEC_MODULE_REFERENCE_DESERIALIZER, deserializer);
		return this;
	}

	/**
	 * Sets the proxy factory.
	 *
	 * @param factory the proxy factory
	 * @return this builder
	 */
	public CodecOptionsBuilder proxyFactory(Object factory) {
		options.put(CodecModuleOptions.CODEC_PROXY_FACTORY, factory);
		return this;
	}

	// ========== Per-class options ==========

	/**
	 * Starts configuring options for a specific EClass.
	 * Can be called multiple times to configure different classes.
	 *
	 * @param eClass the EClass to configure
	 * @return a ClassOptionsBuilder for the specified class
	 */
	public ClassOptionsBuilder forClass(EClass eClass) {
		finalizeCurrentClassBuilder();
		currentClassBuilder = new ClassOptionsBuilder(this, eClass);
		return currentClassBuilder;
	}

	/**
	 * Builds and returns the options map.
	 *
	 * @return the options map ready to be used with resource.save() or resource.load()
	 */
	public Map<String, Object> build() {
		finalizeCurrentClassBuilder();
		if (!classOptions.isEmpty()) {
			options.put(CodecResourceOptions.CODEC_OPTIONS, classOptions);
		}
		return options;
	}

	private void finalizeCurrentClassBuilder() {
		if (currentClassBuilder != null) {
			currentClassBuilder.finalizeClass();
			currentClassBuilder = null;
		}
	}

	/**
	 * Builder for per-class options.
	 */
	public static class ClassOptionsBuilder {
		private final CodecOptionsBuilder parent;
		private final EClass eClass;
		private final Map<String, Object> classOptions = new HashMap<>();
		private final Map<EReference, Map<String, Object>> referenceOptions = new HashMap<>();

		private ClassOptionsBuilder(CodecOptionsBuilder parent, EClass eClass) {
			this.parent = parent;
			this.eClass = eClass;
		}

		// ========== ID options ==========

		/**
		 * Sets the ID key for this class.
		 *
		 * @param key the ID key
		 * @return this builder
		 */
		public ClassOptionsBuilder idKey(String key) {
			classOptions.put(CodecModelInfoOptions.CODEC_ID_KEY, key);
			return this;
		}

		/**
		 * Sets the ID strategy for this class.
		 *
		 * @param strategy the ID strategy (e.g., "ID_FIELD", "COMBINED")
		 * @return this builder
		 */
		public ClassOptionsBuilder idStrategy(String strategy) {
			classOptions.put(CodecModelInfoOptions.CODEC_ID_STRATEGY, strategy);
			return this;
		}

		/**
		 * Sets the ID separator for this class (used with COMBINED strategy).
		 *
		 * @param separator the separator string
		 * @return this builder
		 */
		public ClassOptionsBuilder idSeparator(String separator) {
			classOptions.put(CodecModelInfoOptions.CODEC_ID_SEPARATOR, separator);
			return this;
		}

		/**
		 * Sets the features to use for constructing the ID (used with COMBINED strategy).
		 *
		 * @param features the features to use for the ID
		 * @return this builder
		 */
		public ClassOptionsBuilder idFeatures(EStructuralFeature... features) {
			classOptions.put(CodecModelInfoOptions.CODEC_ID_FEATURES_LIST, List.of(features));
			return this;
		}

		/**
		 * Sets the ID value reader.
		 *
		 * @param reader the ID value reader
		 * @return this builder
		 */
		public ClassOptionsBuilder idValueReader(Object reader) {
			classOptions.put(CodecModelInfoOptions.CODEC_ID_VALUE_READER, reader);
			return this;
		}

		/**
		 * Sets the ID value writer.
		 *
		 * @param writer the ID value writer
		 * @return this builder
		 */
		public ClassOptionsBuilder idValueWriter(Object writer) {
			classOptions.put(CodecModelInfoOptions.CODEC_ID_VALUE_WRITER, writer);
			return this;
		}

		// ========== Type options ==========

		/**
		 * Sets the type strategy for this class.
		 *
		 * @param strategy the type strategy
		 * @return this builder
		 */
		public ClassOptionsBuilder typeStrategy(String strategy) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_STRATEGY, strategy);
			return this;
		}

		/**
		 * Sets the type include setting for this class.
		 *
		 * @param include the include setting
		 * @return this builder
		 */
		public ClassOptionsBuilder typeInclude(boolean include) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_INCLUDE, include);
			return this;
		}

		/**
		 * Sets the type key for this class.
		 *
		 * @param key the type key
		 * @return this builder
		 */
		public ClassOptionsBuilder typeKey(String key) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_KEY, key);
			return this;
		}

		/**
		 * Sets the type map for this class.
		 *
		 * @param typeMap the type map
		 * @return this builder
		 */
		public ClassOptionsBuilder typeMap(Map<String, String> typeMap) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_MAP, typeMap);
			return this;
		}
		
		/**
		 * Sets the type map strategy for this class.
		 *
		 * @param typeMapStrategy the type map strategy
		 * @return this builder
		 */
		public ClassOptionsBuilder typeMapStrategy(TypeMapStrategyType typeMapStrategy) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_MAP_STRATEGY, typeMapStrategy);
			return this;
		}
		
		/**
		 * Sets the type map strategy for this class.
		 *
		 * @param typeMapStrategy the type map strategy
		 * @return this builder
		 */
		public ClassOptionsBuilder typeMapStrategy(String typeMapStrategy) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_MAP_STRATEGY, typeMapStrategy);
			return this;
		}

		/**
		 * Sets the type info for this class.
		 *
		 * @param typeInfo the type info object
		 * @return this builder
		 */
		public ClassOptionsBuilder typeInfo(Object typeInfo) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_INFO, typeInfo);
			return this;
		}

		/**
		 * Sets the type value reader.
		 *
		 * @param reader the type value reader
		 * @return this builder
		 */
		public ClassOptionsBuilder typeValueReader(Object reader) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_VALUE_READER, reader);
			return this;
		}

		/**
		 * Sets the type value writer.
		 *
		 * @param writer the type value writer
		 * @return this builder
		 */
		public ClassOptionsBuilder typeValueWriter(Object writer) {
			classOptions.put(CodecModelInfoOptions.CODEC_TYPE_VALUE_WRITER, writer);
			return this;
		}
		
		/**
		 * Sets the extras property
		 *
		 * @param the Map with extras properties (keys are the name of the extra properties and values are the actual values)
		 * @return this builder
		 */
		public ClassOptionsBuilder withExtraProperties(Map<String, String> extraPropertiesMap) {
			classOptions.put(CodecModelInfoOptions.CODEC_EXTRAS, extraPropertiesMap);
			return this;
		}

		// ========== Feature options ==========

		/**
		 * Sets the list of features to ignore during serialization/deserialization.
		 *
		 * @param features the features to ignore
		 * @return this builder
		 */
		public ClassOptionsBuilder ignoreFeatures(EStructuralFeature... features) {
			classOptions.put(CodecModelInfoOptions.CODEC_IGNORE_FEATURES_LIST, List.of(features));
			return this;
		}

		/**
		 * Sets the list of features to NOT ignore during serialization/deserialization.
		 *
		 * @param features the features to not ignore
		 * @return this builder
		 */
		public ClassOptionsBuilder ignoreNotFeatures(EStructuralFeature... features) {
			classOptions.put(CodecModelInfoOptions.CODEC_IGNORE_NOT_FEATURES_LIST, List.of(features));
			return this;
		}

		/**
		 * Sets custom value readers for specific features.
		 *
		 * @param readersMap map of feature to reader
		 * @return this builder
		 */
		public ClassOptionsBuilder valueReaders(Map<EStructuralFeature, Object> readersMap) {
			classOptions.put(CodecModelInfoOptions.CODEC_VALUE_READERS_MAP, readersMap);
			return this;
		}

		/**
		 * Sets custom value writers for specific features.
		 *
		 * @param writersMap map of feature to writer
		 * @return this builder
		 */
		public ClassOptionsBuilder valueWriters(Map<EStructuralFeature, Object> writersMap) {
			classOptions.put(CodecModelInfoOptions.CODEC_VALUE_WRITERS_MAP, writersMap);
			return this;
		}

		// ========== Reference-specific options ==========

		/**
		 * Starts configuring options for a specific reference within this class.
		 *
		 * @param reference the reference to configure
		 * @return a ReferenceOptionsBuilder for the specified reference
		 */
		public ReferenceOptionsBuilder forReference(EReference reference) {
			return new ReferenceOptionsBuilder(this, reference);
		}

		/**
		 * Returns to the parent builder to continue configuring resource-level options
		 * or to configure another class.
		 *
		 * @return the parent CodecOptionsBuilder
		 */
		public CodecOptionsBuilder and() {
			return parent;
		}

		/**
		 * Builds and returns the options map.
		 *
		 * @return the options map ready to be used with resource.save() or resource.load()
		 */
		public Map<String, Object> build() {
			return parent.build();
		}

		private void finalizeClass() {
			if (!referenceOptions.isEmpty()) {
				classOptions.put(CodecResourceOptions.CODEC_OPTIONS, referenceOptions);
			}
			if (!classOptions.isEmpty()) {
				parent.classOptions.put(eClass, classOptions);
			}
		}

		/**
		 * Builder for per-reference options.
		 */
		public static class ReferenceOptionsBuilder {
			private final ClassOptionsBuilder parent;
			private final EReference reference;
			private final Map<String, Object> refOptions = new HashMap<>();

			private ReferenceOptionsBuilder(ClassOptionsBuilder parent, EReference reference) {
				this.parent = parent;
				this.reference = reference;
			}

			/**
			 * Sets the type strategy for this reference.
			 *
			 * @param strategy the type strategy
			 * @return this builder
			 */
			public ReferenceOptionsBuilder typeStrategy(String strategy) {
				refOptions.put(CodecModelInfoOptions.CODEC_TYPE_STRATEGY, strategy);
				return this;
			}

			/**
			 * Sets the type key for this reference.
			 *
			 * @param key the type key
			 * @return this builder
			 */
			public ReferenceOptionsBuilder typeKey(String key) {
				refOptions.put(CodecModelInfoOptions.CODEC_TYPE_KEY, key);
				return this;
			}
			
			/**
			 * Sets the type map for this class.
			 *
			 * @param typeMap the type map
			 * @return this builder
			 */
			public ReferenceOptionsBuilder typeMap(Map<String, String> typeMap) {
				refOptions.put(CodecModelInfoOptions.CODEC_TYPE_MAP, typeMap);
				return this;
			}
			
			/**
			 * Sets the type map strategy for this class.
			 *
			 * @param typeMapStrategy the type map strategy
			 * @return this builder
			 */
			public ReferenceOptionsBuilder typeMapStrategy(TypeMapStrategyType typeMapStrategy) {
				refOptions.put(CodecModelInfoOptions.CODEC_TYPE_MAP_STRATEGY, typeMapStrategy);
				return this;
			}
			
			/**
			 * Sets the type map strategy for this class.
			 *
			 * @param typeMapStrategy the type map strategy
			 * @return this builder
			 */
			public ReferenceOptionsBuilder typeMapStrategy(String typeMapStrategy) {
				refOptions.put(CodecModelInfoOptions.CODEC_TYPE_MAP_STRATEGY, typeMapStrategy);
				return this;
			}

			/**
			 * Controls whether this reference should inherit codec.type annotation from its target EClass.
			 * By default, references inherit type information from their target EClass and merge it with
			 * their own annotations (if any).
			 *
			 * @param inherits true to inherit from target EClass (default), false to disable inheritance
			 * @return this builder
			 */
			public ReferenceOptionsBuilder inheritsTypeFromParent(boolean inherits) {
				refOptions.put(CodecModelInfoOptions.CODEC_TYPE_INHERITS_FROM_PARENT, inherits);
				return this;
			}

			/**
			 * Sets custom value readers for this reference.
			 *
			 * @param readersMap map of feature to reader
			 * @return this builder
			 */
			public ReferenceOptionsBuilder valueReaders(Map<EStructuralFeature, Object> readersMap) {
				refOptions.put(CodecModelInfoOptions.CODEC_VALUE_READERS_MAP, readersMap);
				return this;
			}

			/**
			 * Sets custom value writers for this reference.
			 *
			 * @param writersMap map of feature to writer
			 * @return this builder
			 */
			public ReferenceOptionsBuilder valueWriters(Map<EStructuralFeature, Object> writersMap) {
				refOptions.put(CodecModelInfoOptions.CODEC_VALUE_WRITERS_MAP, writersMap);
				return this;
			}
			
			/**
			 * Sets the extras property
			 *
			 * @param the Map with extras properties (keys are the name of the extra properties and values are the actual values)
			 * @return this builder
			 */
			public ReferenceOptionsBuilder withExtraProperties(Map<String, String> extraPropertiesMap) {
				refOptions.put(CodecModelInfoOptions.CODEC_EXTRAS, extraPropertiesMap);
				return this;
			}

			/**
			 * Returns to the parent class builder to continue configuring the class.
			 *
			 * @return the parent ClassOptionsBuilder
			 */
			public ClassOptionsBuilder and() {
				finalizeReference();
				return parent;
			}

			/**
			 * Builds and returns the options map.
			 *
			 * @return the options map ready to be used with resource.save() or resource.load()
			 */
			public Map<String, Object> build() {
				finalizeReference();
				return parent.build();
			}

			private void finalizeReference() {
				if (!refOptions.isEmpty()) {
					parent.referenceOptions.put(reference, refOptions);
				}
			}
		}
	}
}
