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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.osgi.service.component.annotations.Component;

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
@Component(service = Resource.Factory.class, property = {
		EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + "application/schema+json",
		EMFNamespaces.EMF_MODEL_FILE_EXT + "=" + "jsonschema",
		EMFNamespaces.EMF_MODEL_VERSION + "=" + "1.0"
})
public class JsonSchemaResourceFactoryImpl extends ResourceFactoryImpl {

	/**
	 * Creates a JSON Schema resource for the given URI.
	 *
	 * @param uri the resource URI
	 * @return a new JsonSchemaResourceImpl
	 */
	@Override
	public Resource createResource(URI uri) {
		return new JsonSchemaResourceImpl(uri);
	}
}
