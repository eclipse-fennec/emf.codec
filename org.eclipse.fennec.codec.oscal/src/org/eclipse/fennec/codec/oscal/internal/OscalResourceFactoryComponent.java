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
package org.eclipse.fennec.codec.oscal.internal;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.oscal.OscalResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import gov.nist.csrc.ns.oscal.OSCALPackage;

/**
 * Registers {@link OscalResourceFactoryImpl} as {@code Resource.Factory} service for the OSCAL
 * JSON media type.
 * <p>
 * There is no file extension: OSCAL JSON files end in {@code .json}, which belongs to the generic
 * codec, and EMF maps only the last segment of a file name. A caller picks this factory by content
 * type or registers it for a URI explicitly.
 * </p>
 * <p>
 * The static {@code oscalPackage} reference keeps the component unsatisfied until the OSCAL model
 * is present.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
@Component(service = Resource.Factory.class,
	property = {
		EMFNamespaces.EMF_CONFIGURATOR_NAME + "=" + "FennecCodecOscal",
		EMFNamespaces.EMF_MODEL_VERSION + "=" + "1.0",
		EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=" + OscalResourceFactoryImpl.CONTENT_TYPE_OSCAL_JSON
	},
	reference = {
		@Reference(name = "oscalPackage", service = OSCALPackage.class)
	}
)
public class OscalResourceFactoryComponent extends OscalResourceFactoryImpl {

	/**
	 * OSGi DS constructor - MetadataService is injected.
	 *
	 * @param metadataService the metadata service
	 */
	@Activate
	public OscalResourceFactoryComponent(@Reference MetadataService metadataService) {
		super(metadataService);
	}
}
