/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.cbor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * 
 * @author ilenia
 * @since Feb 27, 2026
 */
@Component(
		name = "CborResourceFactory",
		service = Resource.Factory.class,
		property = {
				EMFNamespaces.EMF_MODEL_FILE_EXT + "=cbor",
				EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/cbor"
		}
		)
public class CborResourceFactoryComponent extends ResourceFactoryImpl {

	private final MetadataService metadataService;
	private final CborFormatProvider formatProvider = new CborFormatProvider();
	private volatile CodecValueRegistry valueRegistry;

	@Activate
	public CborResourceFactoryComponent(
			@Reference MetadataService metadataService) {
		this.metadataService = metadataService;
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

	@Override
	public Resource createResource(URI uri) {
		CodecValueRegistry reg = valueRegistry;
		return new CodecResource(
				uri, metadataService,
				ConfigurationResolver.defaults(),
				reg != null ? reg.copy() : null, null,
				formatProvider
				);
	}
	
	/**
	 * Returns OSGi service properties for this resource factory.
	 *
	 * @return map of service properties
	 */
	public Map<String, Object> getServiceProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(EMFNamespaces.EMF_MODEL_FILE_EXT, "cbor");
		properties.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/cbor");
		return properties;
	}
}
