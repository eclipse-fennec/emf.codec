/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.codec.openapi;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

/**
 * EMF Resource implementation for OpenAPI documents.
 * <p>
 * The {@code components/schemas} JSON key is handled entirely by the
 * {@code schemasPackage} EReference via codec annotations:
 * <ul>
 *   <li>Read: {@code EPackageValueReader} ("jsonSchemaToEPackage") converts the
 *       raw JSON schemas object directly to an {@code EPackage}.</li>
 *   <li>Write: {@code OpenApiSchemasValueWriter} ("ePackageToOpenApiSchemas") converts
 *       the {@code EPackage} back to a flat JSON schemas map.</li>
 * </ul>
 * The legacy {@code schemas} EMap feature is ignored by the codec ({@code ignore=true})
 * and will be removed in a future release.
 * </p>
 * <p>
 * The required readers/writers ({@link OperationValueReader},
 * {@link OpenApiSchemasValueWriter}) are OSGi {@code @Component} services collected
 * into the injected {@link CodecValueRegistry}.
 * </p>
 *
 * @author Data In Motion
 * @since 1.0
 */
public class OpenApiResourceImpl extends CodecResource {

	/**
	 * Creates an OpenAPI resource.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service for codec configuration
	 * @param valueRegistry registry containing the OpenAPI-specific readers/writers
	 */
	public OpenApiResourceImpl(URI uri, MetadataService metadataService, CodecValueRegistry valueRegistry) {
		super(uri, metadataService, createResolver(), valueRegistry, null);
	}

	private static ConfigurationResolver createResolver() {
		return ConfigurationResolver.builder()
				.typeInclude(false)  // OpenAPI doesn't use _type for root
				.globalIgnore("method")  // Set by OperationValueReader, not serialized
				.build();
	}
}
