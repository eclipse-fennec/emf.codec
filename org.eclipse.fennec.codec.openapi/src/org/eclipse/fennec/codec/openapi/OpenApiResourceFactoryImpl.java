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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.jsonschema.v2.value.EPackageValueReader;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
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
 * @since 2025
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
public class OpenApiResourceFactoryImpl extends ResourceFactoryImpl {

	private final MetadataService metadataService;
	private final CodecValueRegistry valueRegistry;

	/**
	 * OSGi DS constructor — MetadataService and CodecValueRegistry are injected.
	 * The registry already contains all {@code @Component}-annotated readers/writers,
	 * including {@link OperationValueReader} and {@link OpenApiSchemasValueWriter}.
	 */
	@Activate
	public OpenApiResourceFactoryImpl(@Reference MetadataService metadataService,
			@Reference CodecValueRegistry valueRegistry) {
		this.metadataService = metadataService;
		this.valueRegistry = valueRegistry;
		// Keyed by the EPackage instance, not the nsURI: the nsURI overload is best-effort and
		// answers with the most recently registered version (issue #89).
		this.metadataService.getPackageMetadata(OpenApiPackage.eINSTANCE)
				.orElseThrow(() -> new IllegalStateException(
						"The OpenApi Model is required to get this resource factory work"));
	}

	/**
	 * Non-OSGi constructor for standalone usage.
	 * Builds a local registry with the readers/writers needed by OpenAPI resources.
	 */
	public OpenApiResourceFactoryImpl() {
		MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
		whiteboard.registerPackage(OpenApiPackage.eINSTANCE);
		this.metadataService = whiteboard;
		CodecValueRegistry registry = new CodecValueRegistry();
		registry.register(new OperationValueReader());
		registry.register(new SecurityRequirementValueReader());
		registry.register(new EPackageValueReader());
		registry.register(new OpenApiSchemasValueWriter());
		registry.register(new SecurityRequirementValueWriter());
		this.valueRegistry = registry;
	}

	@Override
	public Resource createResource(URI uri) {
		return new OpenApiResourceImpl(uri, metadataService, valueRegistry);
	}
}
