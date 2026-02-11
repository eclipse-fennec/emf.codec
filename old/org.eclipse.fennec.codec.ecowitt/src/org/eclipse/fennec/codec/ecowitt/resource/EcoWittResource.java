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
package org.eclipse.fennec.codec.ecowitt.resource;


import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.configurator.ObjectMapperBuilderFactory;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.json.resource.CodecJsonResource;
import org.eclipse.fennec.codec.options.CodecModuleOptions;
import org.eclipse.fennec.codec.options.CodecResourceOptions;
import org.eclipse.fennec.codec.options.ObjectMapperOptions;
import org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage;

import tools.jackson.databind.DeserializationFeature;

/**
 * 
 * @author Mark Hoffmann
 * @since 02.08.2024
 */
public class EcoWittResource extends CodecJsonResource {

	public EcoWittResource(URI uri, CodecModelInfo modelInfo, 
			CodecModule.Builder moduleBuilder, ObjectMapperBuilderFactory objMapperBuilderFactory) {
		super(uri, modelInfo, moduleBuilder, objMapperBuilderFactory);
	}

	@Override
	protected void doLoad(final InputStream inputStream, Map<?, ?> options) throws IOException {
		if (options == null) {
			options = new HashMap<>();
		}
		@SuppressWarnings("unchecked") // Often used to suppress the warning
		Map<String, Object> newOptions = (Map<String, Object>) options;
		newOptions.put(CodecResourceOptions.CODEC_ROOT_OBJECT, EcoWittPackage.eINSTANCE.getEcoWittWeather());
		newOptions.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, Boolean.TRUE);
		newOptions.put(ObjectMapperOptions.OBJ_MAPPER_DATE_FORMAT, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		newOptions.put(ObjectMapperOptions.OBJ_MAPPER_TIME_ZONE, TimeZone.getTimeZone("UTC"));
		newOptions.put(ObjectMapperOptions.OBJ_MAPPER_DESERIALIZATION_FEATURES_WITHOUT, List.of(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));

		super.doLoad(inputStream, options);

	}
}
