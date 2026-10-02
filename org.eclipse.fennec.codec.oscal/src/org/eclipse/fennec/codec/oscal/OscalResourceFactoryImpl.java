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
package org.eclipse.fennec.codec.oscal;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;

import gov.nist.csrc.ns.oscal.OSCALPackage;

/**
 * Resource factory for OSCAL JSON documents.
 * <p>
 * Creates {@link OscalResourceImpl} instances. Usable as plain Java: the no-arg constructor brings
 * its own metadata whiteboard with the OSCAL model registered. Inside OSGi the factory is provided
 * as {@code Resource.Factory} service by a DS component that extends this class.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public class OscalResourceFactoryImpl extends ResourceFactoryImpl {

	/** The media type for OSCAL JSON documents. */
	public static final String CONTENT_TYPE_OSCAL_JSON = "application/oscal+json";

	private final MetadataService metadataService;

	/**
	 * Creates a factory on a given metadata service, which has to know the OSCAL model.
	 *
	 * @param metadataService the metadata service
	 */
	public OscalResourceFactoryImpl(MetadataService metadataService) {
		this.metadataService = requireNonNull(metadataService, "metadataService must not be null");
	}

	/**
	 * Non-OSGi constructor for standalone usage.
	 */
	public OscalResourceFactoryImpl() {
		MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
		whiteboard.registerPackage(OSCALPackage.eINSTANCE);
		this.metadataService = whiteboard;
	}

	/**
	 * Creates an OSCAL resource for the given URI.
	 *
	 * @param uri the resource URI
	 * @return a new OscalResourceImpl
	 */
	@Override
	public Resource createResource(URI uri) {
		return new OscalResourceImpl(uri, metadataService);
	}

	/**
	 * Returns OSGi service properties for this resource factory.
	 *
	 * @return map of service properties
	 */
	public Map<String, Object> getServiceProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, "FennecCodecOscal");
		properties.put(EMFNamespaces.EMF_MODEL_VERSION, "1.0");
		properties.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, CONTENT_TYPE_OSCAL_JSON);
		return properties;
	}
}
