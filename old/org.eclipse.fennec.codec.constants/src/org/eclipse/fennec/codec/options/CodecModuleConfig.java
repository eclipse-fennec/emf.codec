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
 * This is the CodecModule configuration, which should be used to configure 
 * general serialization/deserialization properties
 * 
 * @author ilenia
 * @since Aug 2, 2024
 */
public @interface CodecModuleConfig {
	
	/**
	 * The Type of the Module. Default is "json".
	 * @return
	 */
	String type() default "json";
	
	/**
	 * A name for the Module.
	 * @return
	 */
	String codecModuleName() default "eclipse-fennec-codec-module";
	
	/**
	 * Option to indicate the default key used for supertype
	 * @return
	 */
	String superTypeKey() default "_supertype";
	
	/**
	 * Option to indicate the default key to be used for references
	 * Default is "$ref"
	 * @return
	 */
	String refKey() default "$ref";
	
	/**
	 * Option to indicate the default key for proxies
	 * Default is "_proxy"
	 * @return
	 */
	String proxyKey() default "_proxy";
	
	/**
	 * Option to indicate the default key for timestamp
	 * Default is "_timestamp"
	 * @return
	 */
	String timestampKey() default "_timestamp";
	
	/**
	 * Option used to indicate the module to serialize default attributes values.
     * Default values are not serialized by default.
	 * @return
	 */
	boolean serializeDefaultValue() default false;
	
	/**
	 * Option used to indicate the module to serialize null values.
	 * This is used for objects in general.
	 * Default is false
	 * @return
	 */
	boolean serializeNullValue() default false;
	
	/**
	 * Option to specify weather or not to serialize empty values.
	 * This is used for arrays, lists, or empty String.
	 * Default is false.
	 * @return
	 */
	boolean serializeEmptyValue() default false;
	
	/**
	 * Option used to indicate whether feature names specified in
     * {@link org.eclipse.emf.ecore.util.ExtendedMetaData} annotations should
     * be respected.
     * Default is Boolean.FALSE
	 * @return
	 */
	boolean useNamesFromExtendedMetaData() default false;
	
	/**
	 * Option used to indicate the module to serialize the id information.
	 * 
     * Default is Boolean.TRUE
	 * @return
	 */
	boolean useId() default true;
	
	/**
	 * Option used to indicate the module to serialize the id field 
	 * on top of the document.
	 * Default is Boolean.TRUE
	 * @return
	 */
	boolean idOnTop() default true;
	
	/**
	 * Option used to indicate the module to additionally serialize the id field of an EObject as it is.
     * This might be superfluous when using as id strategy the one that uses the id field itself, but it 
     * might be useful when the id strategy is set to COMBINED.
     * 
	 * Default is Boolean.FALSE
	 * @return
	 */
	boolean serializeIdField() default false;
	
	/**
	 * If it is set to Boolean.TRUE the value of the ID information will
	 * be used as the primary key if it exists.
	 * Default is Boolean.TRUE
	 * @return
	 */
	boolean idFeatureAsPrimaryKey() default true;
	
	/**
	 * option to specify weather `Enumerator` values should be serialized by 
	 * literals or by name. When deserializing the option should be consistent 
	 * with what was used during serialization (as it should always be the case).
	 * If, for whatever reason, it is not, then the deserialization mechanism 
	 * will fall back and try to deserialize in both ways.  
	 * Default is `FALSE`, namely enumerators are serialized by name.
	 * @return
	 */
	boolean writeEnumLiterals() default false;
	
	/**
	 * Option used to indicate the module to deserialize the type information.
	 * To be used when the type key is also a feature of the object to be 
	 * deserialized.
	 * Default is Boolean.FALSE
	 * @return
	 */
	boolean deserializeType() default false;
	
	/**
	 * Option used to indicate the module to serialize the type information.
	 * Default is Boolean.TRUE
	 * @return
	 */
	boolean serializeType() default true;
	
	/**
	 * Option used to indicate the module to serialize the supertype information.
	 * If this is set to TRUE but serializeType is set to FALSE, then this option
	 * is ignored.
	 * If this is set to TRUE, only the direct parent will be listed as supertype.
	 * For the whole inheritance chain, look at serializeAllSuperTypes option.
	 * Default is Boolean.FALSE
	 * @return
	 */
	boolean serializeSuperTypes() default false;
	
	/**
	 * Option used to indicate the module to serialize the whole chain of 
	 * inheritance.
	 * This option is ignored if either `serializeSuperTypes` or `serializeType` 
	 * is set to `FALSE`. 
	 * Default is Boolean.FALSE
	 * @return
	 */
	boolean serializeAllSuperTypes() default false;
	
	/**
	 * By setting this to Boolean.TRUE the supertypes are written as an array of URIs (or
	 * class names, depending on the type strategy).
	 * If this is set to FALSE, the supertypes are written as a comma separated String.
	 * Default is Boolean.TRUE
	 * @return
	 */
	boolean serializeSuperTypesAsArray() default true;
}
