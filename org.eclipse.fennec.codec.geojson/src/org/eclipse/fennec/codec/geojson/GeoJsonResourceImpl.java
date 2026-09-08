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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.constants.RootOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.geojson.GeoJsonPackage;

import tools.jackson.databind.json.JsonMapper;

/**
 * EMF Resource implementation for GeoJSON format.
 * <p>
 * This resource is pre-configured for GeoJSON serialization/deserialization:
 * <ul>
 *   <li>Type key: "type" (GeoJSON standard)</li>
 *   <li>Type strategy: NAME (simple class names like "Point", "Feature")</li>
 *   <li>Uses ExtendedMetaData names for "coordinates" mapping</li>
 *   <li>Force serializes volatile "data" and "bbox" attributes</li>
 *   <li>No ID serialization (GeoJSON uses "id" as a regular property)</li>
 * </ul>
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 * @see <a href="https://geojson.org/">GeoJSON Specification</a>
 */
public class GeoJsonResourceImpl extends CodecResource {

	/**
	 * Collects all volatile "data" and "bbox" features from GeoJsonPackage.
	 * These need to be force-serialized for proper GeoJSON output.
	 */
	private static List<EStructuralFeature> collectVolatileFeatures() {
		List<EStructuralFeature> features = new ArrayList<>();
		GeoJsonPackage pkg = GeoJsonPackage.eINSTANCE;

		for (EClassifier classifier : pkg.getEClassifiers()) {
			if (classifier instanceof EClass eClass) {
				EStructuralFeature dataFeature = eClass.getEStructuralFeature("data");
				if (dataFeature != null && dataFeature.isVolatile()) {
					features.add(dataFeature);
				}

				EStructuralFeature bboxFeature = eClass.getEStructuralFeature("bbox");
				if (bboxFeature != null && bboxFeature.isVolatile()) {
					features.add(bboxFeature);
				}
			}
		}
		return features;
	}

	/**
	 * Creates the default GeoJSON configuration resolver.
	 */
	private static ConfigurationResolver createGeoJsonResolver() {
		List<EStructuralFeature> volatileFeatures = collectVolatileFeatures();

		return ConfigurationResolver.builder()
				.typeKey("type")
				.typeStrategy(TypeStrategy.NAME)
				.useNamesFromExtendedMetaData(true)
				.useId(false)
				.typeInclude(true)
				.forceWrite(volatileFeatures.toArray(new EStructuralFeature[0]))
				.forceRead(volatileFeatures.toArray(new EStructuralFeature[0]))
				.build();
	}

	/**
	 * Creates a GeoJSON resource with the given URI and metadata service.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service for EPackage metadata
	 */
	public GeoJsonResourceImpl(URI uri, MetadataService metadataService) {
		this(uri, metadataService, null, null);
	}

	/**
	 * Creates a GeoJSON resource with custom value registry.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service for EPackage metadata
	 * @param valueRegistry custom value readers/writers (may be null)
	 */
	public GeoJsonResourceImpl(URI uri, MetadataService metadataService, CodecValueRegistry valueRegistry) {
		this(uri, metadataService, valueRegistry, null);
	}

	/**
	 * Creates a GeoJSON resource with full customization.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service for EPackage metadata
	 * @param valueRegistry custom value readers/writers (may be null)
	 * @param mapperBuilder pre-configured Jackson mapper builder (may be null)
	 */
	public GeoJsonResourceImpl(URI uri, MetadataService metadataService,
			CodecValueRegistry valueRegistry, JsonMapper.Builder mapperBuilder) {
		super(uri, metadataService, createGeoJsonResolver(), valueRegistry, mapperBuilder);
	}

	/**
	 * Loads GeoJSON content, automatically setting the GeoJSON package as context schema.
	 */
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		Map<Object, Object> effectiveOptions = createEffectiveOptions(options);

		// Set GeoJSON package as the context schema for type resolution. The caller may have
		// named one under either of the option's two keys (issue #208), so ask for both before
		// defaulting - a bare containsKey would overwrite a schema passed under the other key.
		if (!RootOptions.hasRootSchema(effectiveOptions)) {
			effectiveOptions.put(CodecOptions.CODEC_ROOT_SCHEMA, GeoJsonPackage.eNS_URI);
		}

		super.doLoad(inputStream, effectiveOptions);
	}

	/**
	 * Creates effective options by merging provided options with GeoJSON defaults.
	 *
	 * @param options user-provided options (may be null)
	 * @return merged options map
	 */
	private Map<Object, Object> createEffectiveOptions(Map<?, ?> options) {
		Map<Object, Object> effectiveOptions = new HashMap<>();

		if (options != null) {
			options.forEach(effectiveOptions::put);
		}

		return effectiveOptions;
	}
}
