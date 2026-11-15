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
import org.eclipse.fennec.codec.openapi.OpenApiResourceFactoryImpl;
import org.eclipse.fennec.codec.openapi.oas.OasResourceFactoryImpl;
import org.eclipse.fennec.codec.openapi.oas.OasVersion;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * Resource factory for OpenAPI 3.2 descriptions in JSON with the oas model (issue #300). Only 3.1 and 3.2
 * are registered; 3.0 stays with the 3.0 model until emf.services has moved (#299).
 */
@Component(
		name = "OasResourceFactory.3.2.json",
		service = Resource.Factory.class,
		scope = ServiceScope.SINGLETON,
		property = {
				"emf.resource.name=openapi-3.2-json",
				EMFNamespaces.EMF_CONFIGURATOR_NAME + "=" + OpenApiResourceFactoryImpl.CONFIGURATOR_NAME,
				EMFNamespaces.EMF_MODEL_VERSION + "=1.0",
				EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/vnd.oai.openapi+json;version=3.2"
		}
)
public class Oas32JsonResourceFactoryComponent extends OasResourceFactoryImpl {

	/** The shared value registry is copied, as for the 3.0 factory. */
	@Activate
	public Oas32JsonResourceFactoryComponent(@Reference MetadataService metadataService, @Reference CodecValueRegistry valueRegistry) {
		super(metadataService, valueRegistry.copy(), OasVersion.V3_2, null);
	}
}
