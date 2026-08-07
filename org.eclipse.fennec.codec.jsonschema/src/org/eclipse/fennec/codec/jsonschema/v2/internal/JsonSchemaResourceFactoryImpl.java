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
package org.eclipse.fennec.codec.jsonschema.v2.internal;

import org.eclipse.fennec.codec.jsonschema.v2.JsonSchemaResourceImpl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

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
 * @since 2025
 */
@Component(
		name = "JsonSchemaResourceFactory",
		service = Resource.Factory.class, property = {
		EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + "application/schema+json",
		EMFNamespaces.EMF_MODEL_FILE_EXT + "=" + "jsonschema",
		EMFNamespaces.EMF_MODEL_VERSION + "=" + "1.0"
})
public class JsonSchemaResourceFactoryImpl extends ResourceFactoryImpl {
	
	private final MetadataService metadataService;
	private volatile CodecValueRegistry valueRegistry;

	/**
	 * OSGi DS constructor with injected MetadataService.
	 *
	 * @param metadataService the metadata service
	 */
	@Activate
	public JsonSchemaResourceFactoryImpl(@Reference MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	/**
	 * Non-OSGi constructor for standalone usage.
	 */
	public JsonSchemaResourceFactoryImpl() {
		MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
		this.metadataService = whiteboard;
	}

	@Reference(
			cardinality = ReferenceCardinality.OPTIONAL,
			policy = ReferencePolicy.DYNAMIC,
			unbind = "unsetValueRegistry"
	)
	void setValueRegistry(CodecValueRegistry registry) {
		this.valueRegistry = registry;
	}

	void unsetValueRegistry(CodecValueRegistry registry) {
		this.valueRegistry = null;
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
