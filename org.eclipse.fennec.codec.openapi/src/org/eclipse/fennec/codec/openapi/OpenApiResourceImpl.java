/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.openapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaToEPackageConverter;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.eclipse.fennec.model.openapi.Components;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.Schema;

/**
 * EMF Resource implementation for OpenAPI documents.
 * <p>
 * Handles {@code components/schemas} with dual representation:
 * <ul>
 *   <li>{@code schemas} - EMap&lt;String, Schema&gt; (OpenAPI-conformant, serialized)</li>
 *   <li>{@code schemasPackage} - EPackage (EMF-native, derived, not serialized)</li>
 * </ul>
 * </p>
 * <p>
 * On deserialization:
 * <ol>
 *   <li>JSON {@code schemas} object → {@code schemas} EMap (via EMap deserialization)</li>
 *   <li>Post-process: {@code schemas} EMap → {@code schemasPackage} EPackage conversion</li>
 * </ol>
 * </p>
 * <p>
 * On serialization:
 * <ul>
 *   <li>{@code schemas} EMap → JSON {@code schemas} object</li>
 *   <li>{@code schemasPackage} is NOT serialized (has {@code serialize=false} annotation)</li>
 * </ul>
 * </p>
 *
 * @author Data In Motion
 * @since 2025
 */
public class OpenApiResourceImpl extends CodecResource {

	private static final Logger LOGGER = Logger.getLogger(OpenApiResourceImpl.class.getName());

	/**
	 * Creates an OpenAPI resource with the given URI and metadata service.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service for codec configuration
	 */
	public OpenApiResourceImpl(URI uri, MetadataService metadataService) {
		super(uri, metadataService, createResolver(), createValueRegistry(), null);
	}

	private static ConfigurationResolver createResolver() {
		return ConfigurationResolver.builder()
				.typeInclude(false)  // OpenAPI doesn't use _type for root
				.globalIgnore("schemasPackage")  // Derived from schemas, not serialized
				.globalIgnore("method")  // Set by OperationValueReader, not serialized
				.build();
	}

	private static CodecValueRegistry createValueRegistry() {
		CodecValueRegistry registry = new CodecValueRegistry();

		// Register reader for Operation that sets HttpMethod from PathItem feature name.
		// All HTTP method features (get, put, post, etc.) have valueReaderName="operation" annotation.
		registry.register(new OperationValueReader());

		return registry;
	}

	/**
	 * Loads the OpenAPI document and performs post-processing.
	 * <p>
	 * After standard deserialization (which populates {@code schemas} as EMap),
	 * this method converts the schemas to an EPackage for EMF-native access.
	 * </p>
	 */
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		// Standard deserialization - populates schemas as EMap<String, Schema>
		super.doLoad(inputStream, options);

		// Post-process: convert schemas EMap → schemasPackage EPackage
		postProcessSchemas();
	}

	/**
	 * Converts the schemas EMap to an EPackage and sets it on Components.
	 * <p>
	 * This provides EMF-native access to the schemas as EClasses.
	 * </p>
	 */
	private void postProcessSchemas() {
		for (EObject root : getContents()) {
			if (root instanceof OpenAPI openApi) {
				Components components = openApi.getComponents();
				if (components != null) {
					convertSchemasToEPackage(components);
				}
			}
		}
	}

	/**
	 * Converts the schemas EMap to an EPackage.
	 *
	 * @param components the Components object containing schemas
	 */
	private void convertSchemasToEPackage(Components components) {
		EMap<String, Schema> schemas = components.getSchemas();
		if (schemas == null || schemas.isEmpty()) {
			LOGGER.fine("No schemas to convert to EPackage");
			return;
		}

		try {
			// Use the JSON Schema converter to create an EPackage from the schema map
			JsonSchemaToEPackageConverter converter = new JsonSchemaToEPackageConverter();
			EPackage ePackage = converter.convertFromSchemaMap(schemas);

			if (ePackage != null) {
				components.setSchemasPackage(ePackage);
				LOGGER.fine(() -> "Converted " + schemas.size() + " schemas to EPackage with " +
						ePackage.getEClassifiers().size() + " classifiers");
			} else {
				LOGGER.warning("Failed to convert schemas to EPackage - converter returned null");
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to convert schemas to EPackage: " + e.getMessage(), e);
			// Don't fail the load - schemas are still available in the schemas EMap
		}
	}
}
