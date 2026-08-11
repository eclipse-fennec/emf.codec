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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jsonschema.v2.JsonSchemaResourceFactoryImpl;
import org.eclipse.fennec.codec.jsonschema.v2.JsonSchemaResourceImpl;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
@Component(
		name = "JsonSchemaResourceFactory",
		service = Resource.Factory.class, property = {
		EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + "application/schema+json",
		EMFNamespaces.EMF_MODEL_FILE_EXT + "=" + "jsonschema",
		EMFNamespaces.EMF_MODEL_VERSION + "=" + "1.0"
})
public class JsonSchemaResourceFactoryComponent extends JsonSchemaResourceFactoryImpl {
	
	/**
	 * OSGi DS constructor with injected MetadataService.
	 * <p>
	 * The shared registry is copied: the JSON Schema value handlers are plain objects rather
	 * than services, so registering them into the shared instance would outlive this bundle.
	 * Handlers the shared registry carries are taken over and win over the defaults.
	 * </p>
	 *
	 * @param metadataService the metadata service
	 * @param registry the shared value registry
	 */
	@Activate
	public JsonSchemaResourceFactoryComponent(@Reference MetadataService metadataService,
			@Reference CodecValueRegistry registry) {
		super(metadataService, registry.copy());
	}

}
