/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
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
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.openapi.OpenApiResourceFactoryImpl;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Resource factory for OpenAPI documents in YAML (issue #296), registered under the media type
 * {@code application/vnd.oai.openapi} only; the file extensions {@code yaml} / {@code yml} belong to
 * the generic YAML factory.
 * <p>
 * The YAML syntax comes from the {@link CodecFormatProvider} service with
 * {@code codec.format.id=yaml}, which {@code org.eclipse.fennec.codec.yaml} registers. Without that
 * bundle this component stays unsatisfied and the OpenAPI bundle offers JSON only.
 * </p>
 */
@Component(
		name = "OpenApiYamlResourceFactory",
		service = Resource.Factory.class,
		scope = ServiceScope.SINGLETON,
		property = {
				"emf.resource.name=openapi-yaml",
				EMFNamespaces.EMF_CONFIGURATOR_NAME + "=" + OpenApiResourceFactoryImpl.CONFIGURATOR_NAME,
				EMFNamespaces.EMF_MODEL_VERSION + "=" + "1.0",
				EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_YAML
		}
)
public class OpenApiYamlResourceFactoryComponent extends OpenApiResourceFactoryImpl {

	/**
	 * OSGi DS constructor. The shared value registry is copied, as in
	 * {@link OpenApiResourceFactoryComponent}.
	 */
	@Activate
	public OpenApiYamlResourceFactoryComponent(@Reference MetadataService metadataService,
			@Reference CodecValueRegistry valueRegistry,
			@Reference(target = "(" + CodecFormatProvider.PROPERTY_FORMAT_ID + "=yaml)")
			CodecFormatProvider<?, ?> formatProvider) {
		super(metadataService, valueRegistry.copy(), formatProvider, CONTENT_TYPE_OPENAPI_YAML);
	}
}
