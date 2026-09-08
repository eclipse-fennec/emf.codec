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
package org.eclipse.fennec.codec.rest.common.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.rest.annotations.AnnotationConverter;
import org.eclipse.fennec.codec.rest.annotations.json.CodecConfig;
import org.eclipse.fennec.codec.rest.annotations.json.RootElement;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.ComponentServiceObjects;
import org.eclipse.fennec.codec.constants.CodecOptions;

/**
 * 
 * @author ilenia
 * @since 1.0
 */
@Component
public class CodecAnnotationConverter implements AnnotationConverter {

	@Reference
	ComponentServiceObjects<ResourceSet> setServiceObjects;
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.rest.annotations.AnnotationConverter#canHandle(java.lang.annotation.Annotation, boolean)
	 */
	@Override
	public boolean canHandle(Annotation annotation, boolean serialize) {
		return annotation instanceof RootElement || annotation instanceof CodecConfig;

	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.rest.annotations.AnnotationConverter#convertAnnotation(java.lang.annotation.Annotation, boolean, java.util.Map)
	 */
	@Override
	public void convertAnnotation(Annotation annotation, boolean serialize, Map<Object, Object> options) {
		if(annotation instanceof RootElement) {
			RootElement element = (RootElement) annotation;
			
			// The canonical, dotted keys - both spellings are read on the way in (issue #208).
			if(!element.rootType().isBlank()) options.put(CodecOptions.CODEC_ROOT_TYPE , element.rootType());
			if(!element.rootSchema().isBlank()) options.put(CodecOptions.CODEC_ROOT_SCHEMA, element.rootSchema());
			
		} else if(annotation instanceof CodecConfig) {
			CodecConfig config = (CodecConfig) annotation;
			convertCodecConfig(config, options);
		}

	}

	private void convertCodecConfig(CodecConfig config, Map<Object, Object> options) {
		// Feature Configuration
		putIfNotBlank(options, CodecOptions.CODEC_DATE_FORMAT, config.dateFormat());
		options.put(CodecOptions.CODEC_SERIALIZE_DEFAULTS, config.serializeDefaultValues());
		options.put(CodecOptions.CODEC_SERIALIZE_EMPTY, config.serializeEmptyValues());
		options.put(CodecOptions.CODEC_SERIALIZE_NULL, config.serializeNullValues());
		options.put(CodecOptions.CODEC_ENUM_SERIALIZATION, config.enumSerialization());
		putIfNotBlank(options, CodecOptions.CODEC_VALUE_READER_NAME, config.valueReaderName());
		putIfNotBlank(options, CodecOptions.CODEC_VALUE_WRITER_NAME, config.valueWriterName());

		// Type Configuration
		options.put(CodecOptions.CODEC_TYPE_STRATEGY, config.typeStrategy());
		options.put(CodecOptions.CODEC_TYPE_FORMAT, config.typeFormat());
		putIfNotBlank(options, CodecOptions.CODEC_TYPE_KEY, config.typeKey());
		options.put(CodecOptions.CODEC_TYPE_INCLUDE, config.typeInclude());
		putIfNotBlank(options, CodecOptions.CODEC_TYPE_NAME_KEY, config.typeNameKey());
		putIfNotBlank(options, CodecOptions.CODEC_TYPE_SCHEMA_KEY, config.typeSchemaKey());
		// The fingerprint mode's default is NONE, which is a real value rather than blank.
		// Forwarding it unconditionally would make the options level (the highest one) clobber
		// a model annotation that opted in, so only an actual opt-in is forwarded.
		putIfNotDefault(options, CodecOptions.CODEC_FINGERPRINT_MODE, config.fingerprintMode(), "NONE");
		putIfNotBlank(options, CodecOptions.CODEC_FINGERPRINT_KEY, config.fingerprintKey());

		// ID Configuration
		options.put(CodecOptions.CODEC_ID_STRATEGY, config.idStrategy());
		options.put(CodecOptions.CODEC_ID_FORMAT, config.idFormat());
		putIfNotBlank(options, CodecOptions.CODEC_ID_KEY, config.idKey());
		putIfNotBlank(options, CodecOptions.CODEC_ID_VALUE_KEY, config.idValueKey());
		options.put(CodecOptions.CODEC_ID_KEY_MODE, config.idKeyMode());
		options.put(CodecOptions.CODEC_ID_ON_TOP, config.idOnTop());

		// Reference Configuration
		options.put(CodecOptions.CODEC_REF_FORMAT, config.refFormat());
		putIfNotBlank(options, CodecOptions.CODEC_REF_KEY, config.refKey());
		putIfNotBlank(options, CodecOptions.CODEC_REF_TYPE_KEY, config.refTypeKey());

		// SuperType Configuration
		options.put(CodecOptions.CODEC_SUPERTYPE_SERIALIZE, config.superTypeSerialize());
		putIfNotBlank(options, CodecOptions.CODEC_SUPERTYPE_KEY, config.superTypeKey());
		options.put(CodecOptions.CODEC_SUPERTYPE_STRATEGY, config.superTypeStrategy());

		// Global Options
		options.put(CodecOptions.CODEC_SMART_COMPRESSION, config.smartCompression());
	}

	private void putIfNotBlank(Map<Object, Object> options, String key, String value) {
		if (value != null && !value.isBlank()) {
			options.put(key, value);
		}
	}

	/**
	 * Forwards a value only when it deviates from the annotation default, so that leaving an
	 * annotation attribute untouched does not override a lower configuration level.
	 */
	private void putIfNotDefault(Map<Object, Object> options, String key, String value, String defaultValue) {
		if (value != null && !value.isBlank() && !value.equals(defaultValue)) {
			options.put(key, value);
		}
	}

}
