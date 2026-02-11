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
package org.eclipse.fennec.codec.json.resource;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.fennec.codec.configurator.ObjectMapperBuilderFactory;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.resource.CodecResource;

import tools.jackson.databind.cfg.ContextAttributes;

/**
 * 
 * @author mark
 * @since 02.08.2024
 */
public class CodecJsonResource extends CodecResource {
	
	public CodecJsonResource(URI uri, CodecModelInfo modelInfo, 
			CodecModule.Builder moduleBuilder, ObjectMapperBuilderFactory objMapperBuilderFactory) {
		super(uri, modelInfo, moduleBuilder, objMapperBuilderFactory);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		super.doSave(outputStream, options);

		if (options == null) {
			options = Collections.<String, Object> emptyMap();
		}

		if (outputStream instanceof URIConverter.Saveable) {

			((URIConverter.Saveable) outputStream).saveResource(this);

		} else {

			mapper.writer()
			.with(from(options))
			.writeValue(outputStream, this);

		}		
	}

	@Override
	   protected void doLoad(final InputStream inputStream, Map<?, ?> options) throws IOException {
	      super.doLoad(inputStream, options);
	      
	      if (options == null) {
				options = Collections.<String, Object> emptyMap();
			}

	      if (inputStream instanceof URIConverter.Loadable) {

	         ((URIConverter.Loadable) inputStream).loadResource(this);

	      } else {

	         ContextAttributes attributes = from(options);
//	            .withPerCallAttribute(RESOURCE_SET, getResourceSet())
//	            .withPerCallAttribute(RESOURCE, this);

	         mapper.reader()
	            .with(attributes)
	            .forType(Resource.class)
	            .withValueToUpdate(this)
	            .readValue(inputStream);

	      }
	   }
}
