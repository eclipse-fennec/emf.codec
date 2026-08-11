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
package org.eclipse.fennec.codec.openapi.internal;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.openapi.OpenApiResourceFactoryImpl;
import org.eclipse.fennec.codec.openapi.value.OpenApiSchemasValueWriter;
import org.eclipse.fennec.codec.openapi.value.OperationValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Resource factory for OpenAPI documents.
 * <p>
 * Registered as OSGi DS component for file extensions: json, yaml, openapi
 * </p>
 *
 * @author Data In Motion
 * @since 1.0
 */
@Component(
		name = "OpenApiResourceFactory",
		service = Resource.Factory.class,
		scope = ServiceScope.SINGLETON,
		property = {
				"emf.resource.name=openapi",
				"emf.model.fileExtension=openapi"
		}
)
public class OpenApiResourceFactoryComponent extends OpenApiResourceFactoryImpl {

	/**
	 * OSGi DS constructor — MetadataService and CodecValueRegistry are injected.
	 * <p>
	 * The shared registry is copied: the OpenAPI value handlers — {@link OperationValueReader},
	 * {@link OpenApiSchemasValueWriter} and friends — are plain objects rather than services,
	 * so registering them into the shared instance would outlive this bundle. Handlers the
	 * shared registry carries are taken over and win over the OpenAPI defaults.
	 * </p>
	 */
	@Activate
	public OpenApiResourceFactoryComponent(@Reference MetadataService metadataService,
			@Reference CodecValueRegistry valueRegistry) {
		super(metadataService, valueRegistry.copy());
	}

}
