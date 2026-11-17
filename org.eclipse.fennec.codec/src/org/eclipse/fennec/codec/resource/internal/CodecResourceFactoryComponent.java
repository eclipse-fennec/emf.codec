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
package org.eclipse.fennec.codec.resource.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * 
 * @author ilenia
 * @since 1.0
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
	private volatile CodecValueRegistry valueRegistry;
	private volatile ConfigurationResolver resolver = ConfigurationResolver.defaults();

	/** The {@code codec.jackson.*} properties of the configuration (issue #324). */
	private final Map<String, Object> jacksonOptions;

	@Activate
	public CodecResourceFactoryComponent(
			@Reference MetadataService metadataService, Map<String, Object> properties) {
		this.metadataService = metadataService;
		Map<String, Object> jackson = new HashMap<>();
		if (properties != null) {
			properties.forEach((key, value) -> {
				if (key.startsWith(CodecOptions.CODEC_JACKSON_PREFIX)) {
					jackson.put(key, value);
				}
			});
		}
		this.jacksonOptions = Map.copyOf(jackson);
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

	/** The prefix registry (issue #193): backend-owned document keys; copied per resource like the value registry. */
	private volatile CodecPrefixRegistry prefixRegistry;

	@Reference(
			cardinality = ReferenceCardinality.OPTIONAL,
			policy = ReferencePolicy.DYNAMIC,
			unbind = "unsetPrefixRegistry"
	)
	void setPrefixRegistry(CodecPrefixRegistry registry) {
		this.prefixRegistry = registry;
	}

	void unsetPrefixRegistry(CodecPrefixRegistry registry) {
		this.prefixRegistry = null;
	}

	@Override
	public Resource createResource(URI uri) {
		CodecValueRegistry reg = valueRegistry;
		CodecPrefixRegistry prefixReg = prefixRegistry;
		CodecResource resource = new CodecResource(
				uri,
				metadataService,
				resolver,
				reg != null ? reg.copy() : null,
				prefixReg != null ? prefixReg.copy() : null,   // prefix registry (issue #193)
				null,                  // mapperBuilder (default)
				null,                  // formatProvider (JSON = null)
				null                   // typeDiscriminatorReader (built internally)
				);
		// Defaults, so a load's or save's own options still win (spec 13 §11.1)
		resource.getDefaultLoadOptions().putAll(jacksonOptions);
		resource.getDefaultSaveOptions().putAll(jacksonOptions);
		return resource;
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