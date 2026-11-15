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
package org.eclipse.fennec.codec.openapi.oas;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.openapi.OpenApiResourceFactoryImpl;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.model.oas.OasPackage;

/**
 * Resource factory for one OpenAPI version and syntax of the {@code org.eclipse.fennec.model.oas}
 * model (issue #300), registered by the content type with the {@code version} parameter, e.g.
 * {@code application/vnd.oai.openapi+json;version=3.1}.
 */
public class OasResourceFactoryImpl extends ResourceFactoryImpl {

	private final MetadataService metadataService;
	private final CodecValueRegistry valueRegistry;
	private final OasVersion version;
	private final CodecFormatProvider<?, ?> formatProvider;

	/**
	 * Plain Java: brings its own metadata service and value handlers.
	 *
	 * @param version the OpenAPI version
	 * @param formatProvider the syntax, {@code null} for JSON
	 */
	public OasResourceFactoryImpl(OasVersion version, CodecFormatProvider<?, ?> formatProvider) {
		MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
		whiteboard.registerPackage(OasPackage.eINSTANCE);
		this.metadataService = whiteboard;
		this.valueRegistry = new CodecValueRegistry();
		registerDefaultValueHandlers(valueRegistry);
		this.version = requireNonNull(version, "version");
		this.formatProvider = formatProvider;
	}

	/**
	 * @param metadataService the metadata service, which has to know the oas model
	 * @param valueRegistry the registry to use, filled with the OpenAPI value handlers it lacks
	 * @param version the OpenAPI version
	 * @param formatProvider the syntax, {@code null} for JSON
	 */
	public OasResourceFactoryImpl(MetadataService metadataService, CodecValueRegistry valueRegistry, OasVersion version,
			CodecFormatProvider<?, ?> formatProvider) {
		this.metadataService = requireNonNull(metadataService, "metadataService");
		registerDefaultValueHandlers(valueRegistry);
		this.valueRegistry = valueRegistry;
		this.version = requireNonNull(version, "version");
		this.formatProvider = formatProvider;
	}

	/**
	 * Registers the value handlers the oas model needs and the registry does not carry yet: those of
	 * the 3.0 codec (security requirements, components/schemas writer) and the schema readers and
	 * writer of the oas model.
	 *
	 * @param registry the registry to fill
	 */
	public static void registerDefaultValueHandlers(CodecValueRegistry registry) {
		OpenApiResourceFactoryImpl.registerDefaultValueHandlers(registry);
		if (!registry.hasReader(ComponentsSchemasValueReader.NAME)) {
			registry.register(new ComponentsSchemasValueReader());
		}
		if (!registry.hasReader(SchemaReferenceValueReader.NAME)) {
			registry.register(new SchemaReferenceValueReader());
		}
		if (!registry.hasWriter(SchemaReferenceValueReader.NAME)) {
			registry.register(new SchemaReferenceValueWriter());
		}
	}

	@Override
	public Resource createResource(URI uri) {
		return new OasResourceImpl(uri, metadataService, valueRegistry, version, formatProvider);
	}

	/** @return the content type this factory is registered for */
	public String getContentType() {
		return formatProvider == null ? version.jsonContentType() : version.yamlContentType();
	}

	/** @return the OSGi service properties of this factory */
	public Map<String, Object> getServiceProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, OpenApiResourceFactoryImpl.CONFIGURATOR_NAME);
		properties.put(EMFNamespaces.EMF_MODEL_VERSION, "1.0");
		properties.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, getContentType());
		return properties;
	}
}
