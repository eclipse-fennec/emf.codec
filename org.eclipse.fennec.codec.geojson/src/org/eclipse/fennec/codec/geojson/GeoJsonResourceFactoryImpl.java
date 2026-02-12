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
package org.eclipse.fennec.codec.geojson;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.geojson.GeoJsonPackage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Resource factory for GeoJSON resources.
 * <p>
 * Creates {@link GeoJsonResourceImpl} instances pre-configured for
 * GeoJSON serialization/deserialization.
 * </p>
 * <p>
 * In OSGi environments, the {@link MetadataService} is injected via DS.
 * For non-OSGi usage, use {@link #GeoJsonResourceFactoryImpl(MetadataService)}.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2025
 */
@Component(service = Resource.Factory.class, 
	property = {
		EMFNamespaces.EMF_CONFIGURATOR_NAME + "=" + GeoJsonPackage.eNAME,
		EMFNamespaces.EMF_MODEL_FILE_EXT + "=" + "geojson",
		EMFNamespaces.EMF_MODEL_VERSION + "=" + "1.0"
	}, 
	reference = {
		@Reference(name = "geojsonPackage", service = GeoJsonPackage.class)
	}
)
public class GeoJsonResourceFactoryImpl extends ResourceFactoryImpl {

	private final MetadataService metadataService;

	/**
	 * OSGi DS constructor - MetadataService is injected.
	 *
	 * @param metadataService the metadata service
	 */
	@Activate
	public GeoJsonResourceFactoryImpl(@Reference MetadataService metadataService) {
		this.metadataService = requireNonNull(metadataService, "metadataService must not be null");
	}

	/**
	 * Creates a GeoJSON resource for the given URI.
	 *
	 * @param uri the resource URI
	 * @return a new GeoJsonResourceImpl
	 */
	@Override
	public Resource createResource(URI uri) {
		return new GeoJsonResourceImpl(uri, metadataService);
	}

	/**
	 * Returns OSGi service properties for this resource factory.
	 *
	 * @return map of service properties
	 */
	public Map<String, Object> getServiceProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, GeoJsonPackage.eNAME);
		properties.put(EMFNamespaces.EMF_MODEL_FILE_EXT, "geojson");
		properties.put(EMFNamespaces.EMF_MODEL_VERSION, "1.0");
		return properties;
	}
}
