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
import java.util.Map;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.jsonschema.v2.converter.EPackageToJsonSchemaConverter;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaConversionDiagnostic;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaToEPackageConverter;

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
 * <b>Note:</b> This resource extends {@link ResourceImpl} directly, not {@code CodecResource},
 * because JSON Schema conversion is a meta-format operation (converting between metamodels)
 * rather than standard EObject serialization.
 * </p>
 * <p>
 * For embedded JSON Schema support within other formats (e.g., OpenAPI), use
 * {@link EPackageValueHandler} which integrates with the codec v2 value transformation layer.
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
 * @since 2025
 * @see EPackageValueHandler
 * @see <a href="https://json-schema.org/">JSON Schema Specification</a>
 */
public class JsonSchemaResourceImpl extends ResourceImpl {

	/**
	 * Option key for the schema feature/definitions key.
	 * Values: "definitions", "$defs", "schemas", or null for auto-detection.
	 */
	public static final String OPTION_SCHEMA_FEATURE = "jsonschema.feature.key";

	/**
	 * Option key to enable pretty printing of output.
	 */
	public static final String OPTION_PRETTY_PRINT = "jsonschema.pretty.print";

	/**
	 * Option key for the JSON Schema draft version to use when serializing.
	 * Values: "draft-04", "draft-06", "draft-07", "2019-09", "2020-12"
	 */
	public static final String OPTION_SCHEMA_DRAFT = "jsonschema.draft";

	private final EPackageToJsonSchemaConverter toSchemaConverter;
	private final JsonSchemaToEPackageConverter fromSchemaConverter;

	/**
	 * Creates a JSON Schema resource with the given URI.
	 *
	 * @param uri the resource URI
	 */
	public JsonSchemaResourceImpl(URI uri) {
		super(uri);
		this.toSchemaConverter = new EPackageToJsonSchemaConverter();
		this.fromSchemaConverter = new JsonSchemaToEPackageConverter();
	}

	/**
	 * Loads JSON Schema content and converts it to an EPackage.
	 * <p>
	 * After loading, check {@link #getWarnings()} for any conversion diagnostics
	 * about unsupported or partially supported JSON Schema features.
	 * </p>
	 *
	 * @param inputStream the input stream containing JSON Schema
	 * @param options load options (supports {@link #OPTION_SCHEMA_FEATURE})
	 * @throws IOException if loading fails
	 */
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		String schemaFeature = extractOption(options, OPTION_SCHEMA_FEATURE, null);

		EPackage ePackage = fromSchemaConverter.convert(inputStream, schemaFeature);

		if (ePackage != null) {
			getContents().add(ePackage);
		}

		// Transfer conversion diagnostics to resource warnings
		List<JsonSchemaConversionDiagnostic> conversionDiagnostics = fromSchemaConverter.getDiagnostics();
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

		if (!(getContents().get(0) instanceof EPackage ePackage)) {
			throw new IOException("JSON Schema resource can only save EPackage instances, " +
					"found: " + getContents().get(0).getClass().getName());
		}

		String schemaFeature = extractOption(options, OPTION_SCHEMA_FEATURE, null);
		boolean prettyPrint = extractOption(options, OPTION_PRETTY_PRINT, Boolean.TRUE);

		toSchemaConverter.convert(ePackage, outputStream, schemaFeature, prettyPrint);
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
}
