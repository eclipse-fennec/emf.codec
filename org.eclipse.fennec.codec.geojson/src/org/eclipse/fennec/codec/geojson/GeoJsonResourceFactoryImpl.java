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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.geojson.GeoJsonPackage;

/**
 * Resource factory for GeoJSON resources.
 * <p>
 * Creates {@link GeoJsonResourceImpl} instances pre-configured for
 * GeoJSON serialization/deserialization.
 * </p>
 * <p>
 * Usable as plain Java: the no-arg constructor brings its own metadata whiteboard with the
 * GeoJSON model registered (issue #147). Inside OSGi the factory is provided as
 * {@code Resource.Factory} service by a DS component that extends this class.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public class GeoJsonResourceFactoryImpl extends ResourceFactoryImpl {

	/** The GeoJSON media type registered by RFC 7946. */
	public static final String CONTENT_TYPE_GEO_JSON = "application/geo+json";

	/** The pre-RFC GeoJSON media type, still used by some clients. */
	public static final String CONTENT_TYPE_GEO_JSON_LEGACY = "application/vnd.geo+json";

	private final MetadataService metadataService;

	/**
	 * Creates a factory on a given metadata service, which has to know the GeoJSON model.
	 *
	 * @param metadataService the metadata service
	 */
	public GeoJsonResourceFactoryImpl(MetadataService metadataService) {
		this.metadataService = requireNonNull(metadataService, "metadataService must not be null");
	}

	/**
	 * Non-OSGi constructor for standalone usage.
	 */
	public GeoJsonResourceFactoryImpl() {
		MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
		whiteboard.registerPackage(GeoJsonPackage.eINSTANCE);
		this.metadataService = whiteboard;
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
		properties.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE,
				List.of(CONTENT_TYPE_GEO_JSON, CONTENT_TYPE_GEO_JSON_LEGACY));
		return properties;
	}
}
