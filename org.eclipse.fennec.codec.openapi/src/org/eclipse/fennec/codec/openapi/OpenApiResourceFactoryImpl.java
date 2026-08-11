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
import org.eclipse.fennec.codec.openapi.value.OpenApiSchemasValueWriter;
import org.eclipse.fennec.codec.openapi.value.OperationValueReader;
import org.eclipse.fennec.codec.openapi.value.SecurityRequirementValueReader;
import org.eclipse.fennec.codec.openapi.value.SecurityRequirementValueWriter;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.model.openapi.OpenApiPackage;

/**
 * Resource factory for OpenAPI documents.
 * <p>
 * Usable as plain Java: the no-arg constructor brings its own metadata whiteboard and a
 * registry holding the value handlers an OpenAPI document needs (issue #147). Inside OSGi
 * the factory is provided as {@code Resource.Factory} service by a DS component that
 * extends this class.
 * </p>
 * <p>
 * Subclasses can override {@link #initializeValueRegistry(CodecValueRegistry)} to swap
 * individual handlers; a handler registered before the {@code super} call is kept.
 * </p>
 *
 * @author Data In Motion
 * @since 1.0
 */
public class OpenApiResourceFactoryImpl extends ResourceFactoryImpl {

	private final MetadataService metadataService;
	private final CodecValueRegistry valueRegistry;

	/**
	 * Creates a factory on a given metadata service and value registry.
	 * <p>
	 * The registry is filled with the OpenAPI value handlers it does not carry yet and is
	 * then handed to every resource this factory creates — it is not copied, so the caller
	 * decides whether the factory works on a shared or on a private registry.
	 * </p>
	 *
	 * @param metadataService the metadata service, which has to know the OpenAPI model
	 * @param valueRegistry the registry to use and to fill with the OpenAPI value handlers
	 */
	public OpenApiResourceFactoryImpl(MetadataService metadataService,
			CodecValueRegistry valueRegistry) {
		this.metadataService = metadataService;
		// Keyed by the EPackage instance, not the nsURI: the nsURI overload is best-effort and
		// answers with the most recently registered version (issue #89).
		this.metadataService.getPackageMetadata(OpenApiPackage.eINSTANCE)
				.orElseThrow(() -> new IllegalStateException(
						"The OpenApi Model is required to get this resource factory work"));
		initializeValueRegistry(valueRegistry);
		this.valueRegistry = valueRegistry;
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
		initializeValueRegistry(registry);
		this.valueRegistry = registry;
	}
	
	/**
	 * Fills the registry with the value handlers OpenAPI resources need.
	 * <p>
	 * Override to use own handlers: register them first, then call {@code super} to have the
	 * remaining defaults filled in.
	 * </p>
	 *
	 * @param registry the registry to fill
	 */
	protected void initializeValueRegistry(CodecValueRegistry registry) {
		registerDefaultValueHandlers(registry);
	}

	/**
	 * Registers the value handlers an OpenAPI document needs into the given registry.
	 * <p>
	 * Handlers that the registry already carries under the same name are left alone. Use this
	 * when wiring an {@link OpenApiResourceImpl} without this factory (issue #147).
	 * </p>
	 *
	 * @param registry the registry to fill
	 */
	public static void registerDefaultValueHandlers(CodecValueRegistry registry) {
		registerIfAbsent(registry, new OperationValueReader());
		registerIfAbsent(registry, new SecurityRequirementValueReader());
		registerIfAbsent(registry, new EPackageValueReader());
		registerIfAbsent(registry, new OpenApiSchemasValueWriter());
		registerIfAbsent(registry, new SecurityRequirementValueWriter());
	}

	private static void registerIfAbsent(CodecValueRegistry registry, CodecValueReader<?, ?> reader) {
		if (!registry.hasReader(reader.getName())) {
			registry.register(reader);
		}
	}

	private static void registerIfAbsent(CodecValueRegistry registry, CodecValueWriter<?, ?> writer) {
		if (!registry.hasWriter(writer.getName())) {
			registry.register(writer);
		}
	}

	@Override
	public Resource createResource(URI uri) {
		return new OpenApiResourceImpl(uri, metadataService, valueRegistry);
	}
}
