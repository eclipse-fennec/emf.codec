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
package org.eclipse.fennec.codec.geojson.internal;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.geojson.GeoJsonResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.geojson.GeoJsonPackage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Registers {@link GeoJsonResourceFactoryImpl} as {@code Resource.Factory} service for the
 * {@code geojson} file extension and the GeoJSON media types.
 * <p>
 * The content types matter for the REST layer: the message body reader/writer looks a factory up
 * through the resource set's content type map alone, so without them
 * {@code application/geo+json} could never reach this codec (issue #168). {@code application/geo+json}
 * is the type RFC 7946 registers; {@code application/vnd.geo+json} is the pre-RFC alias still
 * found in clients.
 * </p>
 * <p>
 * The static {@code geojsonPackage} reference keeps the component unsatisfied until the GeoJSON
 * model is present.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
@Component(service = Resource.Factory.class,
	property = {
		EMFNamespaces.EMF_CONFIGURATOR_NAME + "=" + GeoJsonPackage.eNAME,
		EMFNamespaces.EMF_MODEL_FILE_EXT + "=" + "geojson",
		EMFNamespaces.EMF_MODEL_VERSION + "=" + "1.0",
		EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + GeoJsonResourceFactoryImpl.CONTENT_TYPE_GEO_JSON,
		EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + GeoJsonResourceFactoryImpl.CONTENT_TYPE_GEO_JSON_LEGACY
	},
	reference = {
		@Reference(name = "geojsonPackage", service = GeoJsonPackage.class)
	}
)
public class GeoJsonResourceFactoryComponent extends GeoJsonResourceFactoryImpl {

	/**
	 * OSGi DS constructor - MetadataService is injected.
	 *
	 * @param metadataService the metadata service
	 */
	@Activate
	public GeoJsonResourceFactoryComponent(@Reference MetadataService metadataService) {
		super(metadataService);
	}
}
