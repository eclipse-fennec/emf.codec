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
 * These are the annotations which can be used directly on an EMF model to specify the general
 * codec properties that the model should follow. Accroding to these annotations, the ModelInfo
 * objects are created.
 * 
 * @author ilenia
 * @since Aug 5, 2024
 */
public interface CodecAnnotations {
	
	/** CODEC_INHERIT 
	 * Annotation for specifying that codec annotations on the direct parent should be inherited, even if the parent 
	 * comes from another {@link EPackage}. By default only if the parent belongs to the same {@link EPackage} then the 
	 * annotations are inherited. 
	 * */
	String CODEC_INHERIT = "codec.inherit";
	
	/** CODEC_TRANSIENT 
	 *  Annotation for specifying that the feature should not be serialized.
	 *  This is supposed to be used when annotating the model.
	 *  If this has to be overwritten by {@link Resource}{@link #save} and {@link #load}
	 *  options, use {@link CodecModelInfoOptions.CODEC_IGNORE_FEATURES_LIST} and 
	 *  {@link CodecModelInfoOptions.CODEC_IGNORE_NOT_FEATURES_LIST}
	 * */
	String CODEC_TRANSIENT = "codec.transient";
	
	/** CODEC_ID 
	 * Annotation to specify, at the level of an EClass how to treat the id field during (de-)serialization.
	 * The details map may contain:
	 * - key: the property name to be used when serializing the id info
	 * - strategy: either ID_FIELD or COMBINED
	 * - separator: when the strategy is COMBINED we can specify with this property the separator character to be used
	 * - idValueReaderName: a name for a CodecValueReader to be used when deserializing the id info
	 * - idValueWriterName: a name for a CodecValueWriter to be used when serializing the id info
	 * - idFeatures: a comma separated String, with the uri of the features to be used as id 
	 * */
	String CODEC_ID = "codec.id";
	
	
	/** CODEC_VALUE_WRITER_NAME 
	 * annotation at the {@link EStructuralFeature} level, to specify a {@link CodecValueWriter} name to be used 
	 * when serializing the annotated {@link EStructuralFeature}. The actual {@link CodecValueWriter} object should 
	 * then be one of the automatically registered ones or should be passed through the options when saving a {@link Resource}. 
	 * */
	String CODEC_VALUE_WRITER_NAME = "codec.value.writer.name";
	
	/** CODEC_VALUE_READER_NAME 
	 * annotation at the {@link EStructuralFeature} level, to specify a {@link CodecValueReader} name to be used 
	 * when deserializing the annotated {@link EStructuralFeature}. The actual {@link CodecValueReader} object should 
	 * then be one of the automatically registered ones or should be passed through the options when loading a {@link Resource}. 
	 * */
	String CODEC_VALUE_READER_NAME = "codec.value.reader.name";
	
	/** CODEC_TYPE 
	 * Annotation used for specifying how to treat the type information of the object marked this way.
	 * This annotation can be either put at the {@link EClass} level or at the {@link EReference} level.
	 * The details map keys are:
	 * - strategy: to specify a strategy for the type value reader/writer (supported are NAME, CLASS, URI)
	 * - include: to specify weather the type information should be considered or not (default is true)
	 * - typeKey: the String to be looked for retrieving the type of the object and the property name to be used when serializing the type (default is _type)
	 * - typeValueWriterName: a name for a CodecValueWriter to serialize the type info
	 * - typeValueReaderName: a name for a CodecValueReader to deserialize the type info
	 * - additional <key, value> pairs in the details map should represent string to look for when deserializing the object, to decide which type of object it is
	 * */
	String CODEC_TYPE = "codec.type";
}
