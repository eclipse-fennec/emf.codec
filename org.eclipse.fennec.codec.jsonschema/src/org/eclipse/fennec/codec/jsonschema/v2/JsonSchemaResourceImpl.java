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
package org.eclipse.fennec.codec.jsonschema.v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.codec.jsonschema.v2.converter.EPackageToJsonSchemaConverter;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaConversionDiagnostic;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaToEPackageConverter;
import org.eclipse.fennec.codec.jsonschema.v2.converter.ocl.JsonSchemaOclConstraintGenerator;
import org.eclipse.fennec.codec.jsonschema.v2.value.EPackageValueReader;
import org.eclipse.fennec.codec.jsonschema.v2.value.EPackageValueWriter;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

/**
 * EMF Resource implementation for standalone JSON Schema files.
 * <p>
 * This resource handles bidirectional conversion between JSON Schema and EMF EPackage:
 * <ul>
 *   <li>Load: JSON Schema → EPackage (with EClasses, EAttributes, EReferences)</li>
 *   <li>Save: EPackage → JSON Schema</li>
 * </ul>
 * </p>
 * <p>
 * Extends {@link CodecResource} to reuse the {@code customProperties} mechanism for
 * format-specific options (e.g., {@code codec.jsonschema.allFieldsRequired}). Options
 * passed to {@code load()} / {@code save()} are extracted via
 * {@link CodecResource#extractCustomProperties(Map)} and forwarded to the converters.
 * </p>
 * <p>
 * For embedded JSON Schema support within other formats (e.g., OpenAPI), use the
 * value handlers ({@link EPackageValueReader}, {@link EPackageValueWriter}) which
 * integrate with the codec v2 value transformation layer.
 * </p>
 * <p>
 * Supports various JSON Schema conventions:
 * <ul>
 *   <li>"definitions" - JSON Schema Draft-04/06/07</li>
 *   <li>"$defs" - JSON Schema Draft 2019-09/2020-12</li>
 *   <li>"schemas" - OpenAPI components/schemas</li>
 * </ul>
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 * @see <a href="https://json-schema.org/">JSON Schema Specification</a>
 */
public class JsonSchemaResourceImpl extends CodecResource {

	

	private final EPackageToJsonSchemaConverter ePackageToSchemaConverter;
	private final JsonSchemaToEPackageConverter schemaToEPackageConverter;
	

	/**
	 * Creates a JSON Schema resource with the given URI.
	 *
	 * @param uri the resource URI
	 */
	public JsonSchemaResourceImpl(URI uri, MetadataService metadataService) {
		this(uri, metadataService, null);
	}

	public JsonSchemaResourceImpl(URI uri, MetadataService metadataService, CodecValueRegistry registry) {
		super(uri, metadataService, createResolver(),
				registry != null ? registry : createFallbackValueRegistry(), null);
		this.ePackageToSchemaConverter = new EPackageToJsonSchemaConverter();
		this.schemaToEPackageConverter = new JsonSchemaToEPackageConverter();
	}

	private static ConfigurationResolver createResolver() {
		return ConfigurationResolver.builder()
				.typeInclude(false)  // jsonschema doesn't use _type for root
				.build();
	}

	private static CodecValueRegistry createFallbackValueRegistry() {
		CodecValueRegistry registry = new CodecValueRegistry();
		JsonSchemaResourceFactoryImpl.registerDefaultValueHandlers(registry);
		return registry;
	}

	/**
	 * Loads JSON Schema content and converts it to an EPackage.
	 * <p>
	 * After loading, check {@link #getWarnings()} for any conversion diagnostics
	 * about unsupported or partially supported JSON Schema features.
	 * </p>
	 *
	 * @param inputStream the input stream containing JSON Schema
	 * @param options load options (supports {@link CodecJsonSchemaOptions#OPTION_SCHEMA_FEATURE},
	 *                {@link CodecJsonSchemaOptions#OPTION_GENERATE_OCL_CONSTRAINTS} and
	 *                {@link CodecJsonSchemaOptions#OPTION_OCL_DELEGATE_URI})
	 * @throws IOException if loading fails
	 */
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		EClass rootObj = extractOption(options, CodecResource.CODEC_ROOT_TYPE, EcorePackage.Literals.EPACKAGE);
		String schemaFeature = extractOption(options, CodecJsonSchemaOptions.OPTION_SCHEMA_FEATURE, "$defs");
		
		EObject eObj = null;
		if(rootObj == EcorePackage.Literals.EPACKAGE) {
			eObj = schemaToEPackageConverter.convert(inputStream, schemaFeature);

			
		} else if(rootObj == EcorePackage.Literals.ECLASS) {
			String className = extractOption(options, "codec.jsonschemaClassName", null);
			eObj = schemaToEPackageConverter.convertToEClass(inputStream, className);
			
		}
		if (eObj != null) {
			getContents().add(eObj);
		}

		if (eObj != null && extractOption(options, CodecJsonSchemaOptions.OPTION_GENERATE_OCL_CONSTRAINTS, Boolean.FALSE)) {
			String delegateUri = extractOption(options, CodecJsonSchemaOptions.OPTION_OCL_DELEGATE_URI,
					CodecJsonSchemaOptions.DEFAULT_OCL_DELEGATE_URI);
			getWarnings().addAll(new JsonSchemaOclConstraintGenerator().generate(eObj, delegateUri));
		}

		// Transfer conversion diagnostics to resource warnings
		List<JsonSchemaConversionDiagnostic> conversionDiagnostics = schemaToEPackageConverter.getDiagnostics();
		getWarnings().addAll(conversionDiagnostics);
	}

	/**
	 * Saves EPackage content as JSON Schema.
	 *
	 * @param outputStream the output stream to write to
	 * @param options save options (supports {@link #OPTION_SCHEMA_FEATURE}, {@link #OPTION_PRETTY_PRINT})
	 * @throws IOException if saving fails
	 */
	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		if (getContents().isEmpty()) {
			return;
		}

		if (!(getContents().get(0) instanceof EPackage) && !(getContents().get(0) instanceof EClass)) {
			throw new IOException("JSON Schema resource can only save EPackage or EClass instances, " +
					"found: " + getContents().get(0).getClass().getName());
		}
		String schemaFeature = extractOption(options, CodecJsonSchemaOptions.OPTION_SCHEMA_FEATURE, "$defs");
		boolean prettyPrint = extractOption(options, CodecJsonSchemaOptions.OPTION_PRETTY_PRINT, Boolean.TRUE);
		Map<String, Object> customProperties = extractCustomProperties(toStringKeyMap(options));
		if(getContents().get(0) instanceof EPackage ePackage) {
			ePackageToSchemaConverter.convert(ePackage, outputStream, schemaFeature, prettyPrint, customProperties);
		} else if(getContents().get(0) instanceof EClass eClass){
			ePackageToSchemaConverter.convertEClass(eClass, outputStream, prettyPrint, customProperties);
		}

	}

	/**
	 * Extracts a typed option value from the options map.
	 *
	 * @param <T> the option type
	 * @param options the options map (may be null)
	 * @param key the option key
	 * @param defaultValue the default value if option is not set
	 * @return the option value or default
	 */
	@SuppressWarnings("unchecked")
	private <T> T extractOption(Map<?, ?> options, String key, T defaultValue) {
		if (options == null) {
			return defaultValue;
		}

		Object value = options.get(key);
		if (value == null) {
			return defaultValue;
		}

		try {
			return (T) value;
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts a {@code Map<?, ?>} to {@code Map<String, Object>} by filtering
	 * for entries whose key is a String.
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> toStringKeyMap(Map<?, ?> options) {
		if (options == null || options.isEmpty()) {
			return Map.of();
		}
		// EMF option maps always have String keys in practice
		if (options.keySet().stream().allMatch(k -> k instanceof String)) {
			return (Map<String, Object>) options;
		}
		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<?, ?> entry : options.entrySet()) {
			if (entry.getKey() instanceof String key) {
				result.put(key, entry.getValue());
			}
		}
		return result;
	}
}
