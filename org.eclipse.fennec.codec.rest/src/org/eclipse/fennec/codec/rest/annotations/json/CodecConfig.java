/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.codec.rest.annotations.json;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.fennec.codec.constants.CodecOptions;

/**
 * Codec configuration annotation for Jakarta REST endpoints.
 * <p>
 * Maps to {@link CodecOptions} constants which are passed as load/save options
 * to the codec {@code Resource}. Each field corresponds to a {@code CodecOptions.CODEC_*}
 * constant. Empty string ({@code ""}) means "use codec default".
 * </p>
 *
 * @author ilenia
 * @since Mar 18, 2026
 */
@Documented
@Target({METHOD, PARAMETER, CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodecConfig {

	// ========================================================================
	// Feature Configuration
	// ========================================================================

	/**
	 * Date format pattern for serializing/deserializing Date values.
	 * @return the date format pattern
	 * @see CodecOptions#CODEC_DATE_FORMAT
	 */
	String dateFormat() default "";

	/**
	 * Whether to serialize default values.
	 * @return true to serialize default values
	 * @see CodecOptions#CODEC_SERIALIZE_DEFAULTS
	 */
	boolean serializeDefaultValues() default false;

	/**
	 * Whether to serialize empty collections.
	 * @return true to serialize empty collections
	 * @see CodecOptions#CODEC_SERIALIZE_EMPTY
	 */
	boolean serializeEmptyValues() default false;

	/**
	 * Whether to serialize null values.
	 * @return true to serialize null values
	 * @see CodecOptions#CODEC_SERIALIZE_NULL
	 */
	boolean serializeNullValues() default false;

	/**
	 * Enum serialization strategy.
	 * @return the strategy name ("LITERAL", "VALUE", "NAME")
	 * @see CodecOptions#CODEC_ENUM_SERIALIZATION
	 */
	String enumSerialization() default "LITERAL";

	/**
	 * Custom value reader name.
	 * @return the reader name, or empty string for default
	 * @see CodecOptions#CODEC_VALUE_READER_NAME
	 */
	String valueReaderName() default "";

	/**
	 * Custom value writer name.
	 * @return the writer name, or empty string for default
	 * @see CodecOptions#CODEC_VALUE_WRITER_NAME
	 */
	String valueWriterName() default "";

	// ========================================================================
	// Type Configuration
	// ========================================================================

	/**
	 * Type serialization strategy.
	 * @return the strategy name ("URI", "NAME", "CLASS", "SCHEMA_AND_TYPE", "NUMERIC", "NONE")
	 * @see CodecOptions#CODEC_TYPE_STRATEGY
	 */
	String typeStrategy() default "URI";

	/**
	 * Type serialization format.
	 * @return the format ("PLAIN", "STRUCTURED")
	 * @see CodecOptions#CODEC_TYPE_FORMAT
	 */
	String typeFormat() default "PLAIN";

	/**
	 * JSON key for the type field.
	 * @return the type key, or empty string for default ("_type")
	 * @see CodecOptions#CODEC_TYPE_KEY
	 */
	String typeKey() default "";

	/**
	 * Whether to include type information.
	 * @return true to include type information
	 * @see CodecOptions#CODEC_TYPE_INCLUDE
	 */
	boolean typeInclude() default true;

	/**
	 * Inner name key in STRUCTURED format.
	 * @return the name key, or empty string for default ("type")
	 * @see CodecOptions#CODEC_TYPE_NAME_KEY
	 */
	String typeNameKey() default "";

	/**
	 * Inner schema key in STRUCTURED format.
	 * @return the schema key, or empty string for default ("schema")
	 * @see CodecOptions#CODEC_TYPE_SCHEMA_KEY
	 */
	String typeSchemaKey() default "";

	// ========================================================================
	// ID Configuration
	// ========================================================================

	/**
	 * ID serialization strategy.
	 * @return the strategy name ("ID_FIELD", "COMBINED", "NONE")
	 * @see CodecOptions#CODEC_ID_STRATEGY
	 */
	String idStrategy() default "ID_FIELD";

	/**
	 * ID serialization format.
	 * @return the format ("PLAIN", "STRUCTURED")
	 * @see CodecOptions#CODEC_ID_FORMAT
	 */
	String idFormat() default "PLAIN";

	/**
	 * JSON key for the ID field.
	 * @return the ID key, or empty string for default ("_id")
	 * @see CodecOptions#CODEC_ID_KEY
	 */
	String idKey() default "";

	/**
	 * Inner value key in STRUCTURED ID format.
	 * @return the value key, or empty string for default ("id")
	 * @see CodecOptions#CODEC_ID_VALUE_KEY
	 */
	String idValueKey() default "";

	/**
	 * ID key mode.
	 * @return the mode ("ID_ONLY", "BOTH", "FEATURE_ONLY", "NONE")
	 * @see CodecOptions#CODEC_ID_KEY_MODE
	 */
	String idKeyMode() default "ID_ONLY";

	/**
	 * Whether ID field appears before type in output.
	 * @return true to place ID before type
	 * @see CodecOptions#CODEC_ID_ON_TOP
	 */
	boolean idOnTop() default false;

	// ========================================================================
	// Reference Configuration
	// ========================================================================

	/**
	 * Reference serialization format.
	 * @return the format ("STRUCTURED", "PLAIN")
	 * @see CodecOptions#CODEC_REF_FORMAT
	 */
	String refFormat() default "STRUCTURED";

	/**
	 * JSON key for reference values.
	 * @return the ref key, or empty string for default ("$ref")
	 * @see CodecOptions#CODEC_REF_KEY
	 */
	String refKey() default "";

	/**
	 * Reference type key in STRUCTURED format.
	 * @return the ref type key, or empty string for default ("_type")
	 * @see CodecOptions#CODEC_REF_TYPE_KEY
	 */
	String refTypeKey() default "";

	// ========================================================================
	// SuperType Configuration
	// ========================================================================

	/**
	 * Enable supertype serialization.
	 * @return true to serialize supertypes
	 * @see CodecOptions#CODEC_SUPERTYPE_SERIALIZE
	 */
	boolean superTypeSerialize() default false;

	/**
	 * JSON key for supertype field.
	 * @return the supertype key, or empty string for default
	 * @see CodecOptions#CODEC_SUPERTYPE_KEY
	 */
	String superTypeKey() default "";

	/**
	 * Which supertypes to include.
	 * @return the strategy ("ALL", "ALL_EMF", "SINGLE", "NONE")
	 * @see CodecOptions#CODEC_SUPERTYPE_STRATEGY
	 */
	String superTypeStrategy() default "ALL";

	// ========================================================================
	// Global Options
	// ========================================================================

	/**
	 * Enable smart compression (omit type when inferable).
	 * @return true to enable smart compression
	 */
	boolean smartCompression() default false;

}
