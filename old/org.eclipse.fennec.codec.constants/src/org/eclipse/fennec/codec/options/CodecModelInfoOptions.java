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

import java.util.List;
import java.util.Map;


/**
 * These are the options that should be used with Resource#load/save in the options arguments
 * to overwrite the properties set through model annotations and used to build the ModelInfo objects
 * 
 * @author ilenia
 * @since Nov 11, 2024
 */
public interface CodecModelInfoOptions {
	
	/** CODEC_IGNORE_FEATURES_LIST
	 * to specify a {@link List} of {@link EStructuralFeature} that should be ignored
	 * during serialization or deserialization. If an {@link EStructuralFeature} is marked as
	 * transient in the model or has been annotated with the {@link org.gecko.codec.constants.CodecAnnotations.CODEC_TRANSIENT}
	 * annotation, it will still be ignored even is it is not present in this list.
	 * */
	String CODEC_IGNORE_FEATURES_LIST = "codec.ignore.features.list";

	/** CODEC_GLOBAL_IGNORE_FEATURES_LIST
	 * to specify a {@link List} of {@link EStructuralFeature} that should be ignored globally
	 * across all EClasses during serialization or deserialization. This is useful for excluding
	 * features from metamodel classes (like Ecore's eGenericType) without having to specify
	 * them for each individual EClass.
	 * */
	String CODEC_GLOBAL_IGNORE_FEATURES_LIST = "codec.global.ignore.features.list";
	
	/** CODEC_IGNORE_NOT_FEATURES_LIST 
	 * to specify a {@link List} of {@link EStructuralFeature} that should NOT be ignored 
	 * during serialization or deserialization. If an {@link EStructuralFeature} is marked as 
	 * transient in the model or has been annotated with the {@link org.gecko.codec.constants.CodecAnnotations.CODEC_TRANSIENT} 
	 * annotation, it will then be taken into account if present in this list.
	 * */
	String CODEC_IGNORE_NOT_FEATURES_LIST = "codec.ignore.not.feature.list";
	
	/** CODEC_ID_KEY 
	 * to overwrite the {@link org.gecko.codec.constants.CodecAnnotations.CODEC_ID} key detail annotation;
	 * */
	String CODEC_ID_KEY = "key";
	
	/** CODEC_ID_STRATEGY 
	 *  to overwrite the {@link org.gecko.codec.constants.CodecAnnotations.CODEC_ID} strategy detail annotation;
	 * */
	String CODEC_ID_STRATEGY = "strategy";
	
	/** CODEC_ID_SEPARATOR 
	 * to overwrite the {@link org.gecko.codec.constants.CodecAnnotations.CODEC_ID} separator detail annotation;
	 * */
	String CODEC_ID_SEPARATOR = "separator";
	
	/** CODEC_ID_FEATURES_LIST 
	 * to specify an ordered {@link List} of {@link EStructuralFeature} to be used when constructing the id, 
	 * if the id strategy is set to COMBINED. Otherwise it will be ignored.
	 * */
	String CODEC_ID_FEATURES_LIST = "idFeatures";
	
	/** CODEC_ID_VALUE_READER
	 * to specify a {@link CodecValueReader} object to be used when deserializing the id information;
	 * */
	String CODEC_ID_VALUE_READER = "codec.id.value.reader";
	
	/** CODEC_ID_VALUE_WRITER 
	 * to specify a {@link CodecValueWriter} object to be used when serializing the id information;
	 * */
	String CODEC_ID_VALUE_WRITER = "codec.id.value.writer";
	
	
	/** CODEC_VALUE_READERS_MAP 
	  * a {@link Map} where the keys are of type {@link EStructuralFeature} and the values 
	 * are of type {@link CodecValueReader}, to specify the {@link CodecValueReader} to use 
	 * when serializing the corresponding {@link EStructuralFeature}
	 * */
	String CODEC_VALUE_READERS_MAP = "codec.value.readers.map";
	
	
	/** CODEC_VALUE_WRITERS_MAP 
	 * a {@link Map} where the keys are of type {@link EStructuralFeature} and the values 
	 * are of type {@link CodecValueWriter}, to specify the {@link CodecValueWriter} to use 
	 * when serializing the corresponding {@link EStructuralFeature}
	 *  */
	String CODEC_VALUE_WRITERS_MAP = "codec.value.writers.map";
	
	/** CODEC_TYPE_STRATEGY 
	 * to overwrite the "strategy" detail of the {@link org.gecko.codec.constants.CodecAnnotations.CODEC_TYPE} annotation;
	 * */
	String CODEC_TYPE_STRATEGY = "strategy";
	
	/** CODEC_TYPE_INCLUDE
	 * to overwrite the  "include" detail of {@link org.gecko.codec.constants.CodecAnnotations.CODEC_TYPE} annotation;
	 * */
	String CODEC_TYPE_INCLUDE = "include";
	
	/** CODEC_TYPE_KEY
	 * to overwrite the "typeKey" detail of {@link org.gecko.codec.constants.CodecAnnotations.CODEC_TYPE} annotation.
	 *
	 * <p>Type Discrimination Modes:</p>
	 * <ul>
	 *   <li><b>Value-based (default):</b> Set typeKey to a field path (e.g., "deviceInfo.deviceProfileName").
	 *       The deserializer will look for this field in the JSON and use its <i>value</i> to determine the type
	 *       via {@link #CODEC_TYPE_MAP}.</li>
	 *   <li><b>Feature-based:</b> Set typeKey to {@link #CODEC_TYPE_KEY_FEATURE_BASED} or null.
	 *       The deserializer will check which <i>properties exist</i> in the JSON object and use the property name
	 *       to determine the type via {@link #CODEC_TYPE_MAP}. This is useful for JSON Schema oneOf patterns
	 *       where different variants have different property structures (e.g., {"kafka": {...}} vs {"file": {...}}).</li>
	 * </ul>
	 * */
	String CODEC_TYPE_KEY = "typeKey";

	/** CODEC_TYPE_KEY_FEATURE_BASED
	 * Special marker value for {@link #CODEC_TYPE_KEY} to enable feature-based type discrimination.
	 * When typeKey is set to this value (or null), the deserializer will determine the type based on
	 * which property names are present in the JSON object, rather than looking at a specific field's value.
	 *
	 * <p>Example usage for JSON Schema oneOf patterns:</p>
	 * <pre>
	 * typeKey = "*"  // or null
	 * typeMap = {
	 *   "kafka": "KafkaInput",
	 *   "file": "FileInput",
	 *   "mqtt": "MqttInput"
	 * }
	 *
	 * JSON: {"kafka": {"addresses": [...], "topics": [...]}}
	 * Result: Deserializes as KafkaInput type because "kafka" property is present
	 * </pre>
	 * */
	String CODEC_TYPE_KEY_FEATURE_BASED = "*";

	/** CODEC_TYPE_MAP
	 * to overwrite or merge (based on the {@link CODEC_TYPE_MAP_STRATEGY}) the type mapping in the details of {@link org.gecko.codec.constants.CodecAnnotations.CODEC_TYPE} annotation.
	 *
	 * <p>The semantics of the type map depend on the type discrimination mode:</p>
	 * <ul>
	 *   <li><b>Value-based mode:</b> Maps field <i>values</i> to types (e.g., "Dragino_LSE01" → "DraginoLSE01Uplink")</li>
	 *   <li><b>Feature-based mode:</b> Maps <i>property names</i> to types (e.g., "kafka" → "KafkaInput")</li>
	 * </ul>
	 * */
	String CODEC_TYPE_MAP = "typeMap";

	/** CODEC_TYPE_MAP_STRATEGY
	 * whether to overwrite or merge the type mapping passed via options with the one provided via model annotation.
	 * The value has to be either a {@link org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType} or a
	 * String compatible with the {@link org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType} values.
	 * Default is to {@link org.eclipse.fennec.codec.info.codecinfo.TypeMapStrategyType#OVERWRITE}
	 *
	 * */
	String CODEC_TYPE_MAP_STRATEGY = "codec.type.map.strategy";

	/** CODEC_TYPE_INHERITS_FROM_PARENT
	 * Controls whether an EReference should inherit codec.type annotation from its target EClass.
	 *
	 * <p>By default, when an EReference has no codec.type annotation or has one without
	 * inherits.from.parent="false", it automatically inherits the codec.type annotation from
	 * the target EClass. This option allows overriding this behavior at runtime via load/save options.</p>
	 *
	 * <p>Valid values:</p>
	 * <ul>
	 *   <li>"true" (default) - Inherit codec.type from target EClass and merge with reference's own annotation</li>
	 *   <li>"false" - Do not inherit from target EClass, use only the reference's annotation</li>
	 * </ul>
	 *
	 * <p>Example usage:</p>
	 * <pre>
	 * CodecOptionsBuilder.create()
	 *   .forClass(MeetingClass)
	 *     .forReference("responsiblePerson")
	 *       .inheritsTypeFromParent(false)  // Disable inheritance for this reference
	 *     .and()
	 *   .build();
	 * </pre>
	 * */
	String CODEC_TYPE_INHERITS_FROM_PARENT = "inherits.from.parent";
	
	/** CODEC_TYPE_INFO 
	 * to overwrite the entire typeInfo object that comes out of the {@link org.gecko.codec.constants.CodecAnnotations.CODEC_TYPE} annotation;
	 * */
	String CODEC_TYPE_INFO = "typeInfo";
	
	/** CODEC_TYPE_VALUE_READER
	 * to specify a {@link CodecValueReader} object to be used when deserializing the type information;
	 * */
	String CODEC_TYPE_VALUE_READER = "codec.type.value.reader";
	
	/** CODEC_TYPE_VALUE_WRITER 
	 * to specify a {@link CodecValueWriter} object to be used when serializing the type information;
	 * */
	String CODEC_TYPE_VALUE_WRITER = "codec.type.value.writer";
	
	/** CODEC_CUSTOM_VALUE_READER 
	 * to specify a {@link CodecValueReader} object to be used when deserializing the object with that option. 
	 * This will overwrite any other behavior and the custom value reader will be used.
	 * */
	String CODEC_CUSTOM_VALUE_READER = "codec.custom.value.reader";
	
	/** CODEC_CUSTOM_VALUE_WRITER 
	 *  to specify a {@link CodecValueWriter} object to be used when serializing the object with that option. 
	 * This will overwrite any other behavior and the custom value writer will be used.
	 * */
	String CODEC_CUSTOM_VALUE_WRITER = "codec.custom.value.writer";
	
	
	/** 
	 * CODEC_EXTRAS 
	 * To add extras properties to either an EObjectCodecInfo or a FeatureCodecInfo via load/save options
	 * */
	String CODEC_EXTRAS = "codec.extras";

}
