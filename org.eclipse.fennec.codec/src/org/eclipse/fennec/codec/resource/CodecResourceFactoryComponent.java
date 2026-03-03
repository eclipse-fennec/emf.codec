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
package org.eclipse.fennec.codec.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
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
		name = "CodecResourceFactory",
		immediate = true,
		service = Resource.Factory.class,
		property = {
				EMFNamespaces.EMF_MODEL_FILE_EXT + "=json",
				EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/json"
		}
		)
public class CodecResourceFactoryComponent extends ResourceFactoryImpl {

	private final MetadataService metadataService;
	private final CodecValueRegistry valueRegistry = new CodecValueRegistry();
	private volatile ConfigurationResolver resolver = ConfigurationResolver.defaults();

	@Activate
	public CodecResourceFactoryComponent(
			@Reference MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	// --- Whiteboard: Custom Value Writers ---
	@Reference(
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC,
			unbind = "removeValueWriter"
			)
	void addValueWriter(CodecValueWriter<?, ?> writer) {
		valueRegistry.register(writer);
	}

	void removeValueWriter(CodecValueWriter<?, ?> writer) {
		valueRegistry.unregisterWriter(writer.getName());
	}

	// --- Whiteboard: Custom Value Readers ---
	@Reference(
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC,
			unbind = "removeValueReader"
			)
	void addValueReader(CodecValueReader<?, ?> reader) {
		valueRegistry.register(reader);
	}

	void removeValueReader(CodecValueReader<?, ?> reader) {
		valueRegistry.unregisterReader(reader.getName());
	}

	@Override
	public Resource createResource(URI uri) {
		return new CodecResource(
				uri,
				metadataService,
				resolver,
				valueRegistry.copy(),  // snapshot to avoid concurrent modification
				null,                  // mapperBuilder (default)
				null,                  // formatProvider (JSON = null)
				null                   // typeDiscriminatorReader (built internally)
				);
	}
	
	/**
	 * Returns OSGi service properties for this resource factory.
	 *
	 * @return map of service properties
	 */
	public Map<String, Object> getServiceProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(EMFNamespaces.EMF_MODEL_FILE_EXT, "json");
		properties.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/json");
		return properties;
	}
	
	
}