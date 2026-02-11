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
package org.eclipse.fennec.codec.jsonschema.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.configurator.ObjectMapperBuilderFactory;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.jackson.module.CodecModule.Builder;
import org.eclipse.fennec.codec.json.resource.CodecJsonResource;

/**
 * 
 * @author ilenia
 * @since Sep 29, 2025
 */
public class CodecJsonSchemaResource extends CodecJsonResource {

	/**
	 * Creates a new instance.
	 * @param uri
	 * @param modelInfo
	 * @param moduleBuilder
	 * @param objMapperBuilderFactory
	 */
	public CodecJsonSchemaResource(URI uri, CodecModelInfo modelInfo, Builder moduleBuilder,
			ObjectMapperBuilderFactory objMapperBuilderFactory) {
		super(uri, modelInfo, moduleBuilder, objMapperBuilderFactory);
	}

}
