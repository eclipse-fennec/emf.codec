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
package org.eclipse.fennec.codec.jsonschema.v2;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.jsonschema.v2.value.EClassValueReader;
import org.eclipse.fennec.codec.jsonschema.v2.value.EClassValueWriter;
import org.eclipse.fennec.codec.jsonschema.v2.value.EPackageValueReader;
import org.eclipse.fennec.codec.jsonschema.v2.value.EPackageValueWriter;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;

/**
 * Resource factory for JSON Schema resources.
 * <p>
 * Creates {@link JsonSchemaResourceImpl} instances for bidirectional
 * JSON Schema ↔ EPackage conversion.
 * </p>
 * <p>
 * Supported file extensions: .jsonschema, .schema.json
 * Content type: application/schema+json
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public class JsonSchemaResourceFactoryImpl extends ResourceFactoryImpl {
	
	private final MetadataService metadataService;
	private final CodecValueRegistry valueRegistry;

	/**
	 * Creates a factory on a given metadata service and value registry.
	 * <p>
	 * The registry is filled with the JSON Schema value handlers it does not carry yet and is
	 * then handed to every resource this factory creates — it is not copied, so the caller
	 * decides whether the factory works on a shared or on a private registry.
	 * </p>
	 *
	 * @param metadataService the metadata service
	 * @param valueRegistry the registry to use and to fill with the JSON Schema value handlers
	 */
	public JsonSchemaResourceFactoryImpl(MetadataService metadataService, CodecValueRegistry valueRegistry) {
		this.metadataService = metadataService;
		initializeValueRegistry(valueRegistry);
		this.valueRegistry = valueRegistry;
	}

	/**
	 * Non-OSGi constructor for standalone usage.
	 */
	public JsonSchemaResourceFactoryImpl() {
		MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
		this.metadataService = whiteboard;
		CodecValueRegistry registry = new CodecValueRegistry();
		initializeValueRegistry(registry);
		this.valueRegistry = registry;
	}

	/**
	 * Fills the registry with the value handlers JSON Schema resources need.
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
	 * Registers the value handlers a JSON Schema document needs into the given registry.
	 * <p>
	 * Handlers that the registry already carries under the same name are left alone. Use this
	 * when wiring a {@link JsonSchemaResourceImpl} without this factory.
	 * </p>
	 *
	 * @param registry the registry to fill
	 */
	public static void registerDefaultValueHandlers(CodecValueRegistry registry) {
		registerIfAbsent(registry, new EPackageValueReader());
		registerIfAbsent(registry, new EPackageValueWriter());
		registerIfAbsent(registry, new EClassValueReader());
		registerIfAbsent(registry, new EClassValueWriter());
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

	/**
	 * Creates a JSON Schema resource for the given URI.
	 *
	 * @param uri the resource URI
	 * @return a new JsonSchemaResourceImpl
	 */
	@Override
	public Resource createResource(URI uri) {
		return new JsonSchemaResourceImpl(uri, metadataService, valueRegistry);
	}
	
	/**
	 * Returns OSGi service properties for this resource factory.
	 *
	 * @return map of service properties
	 */
	public Map<String, Object> getServiceProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/schema+json");
		properties.put(EMFNamespaces.EMF_MODEL_FILE_EXT, "jsonschema");
		properties.put(EMFNamespaces.EMF_MODEL_VERSION, "1.0");
		return properties;
	}
}
