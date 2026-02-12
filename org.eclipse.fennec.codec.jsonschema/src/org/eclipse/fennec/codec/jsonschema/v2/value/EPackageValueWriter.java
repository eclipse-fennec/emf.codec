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
package org.eclipse.fennec.codec.jsonschema.v2.value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.jsonschema.v2.converter.EPackageToJsonSchemaConverter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Value writer for embedded JSON Schema from EPackage.
 * <p>
 * This writer is used when JSON Schema should be embedded within another format
 * (e.g., OpenAPI's components/schemas section). It converts an EPackage to
 * JSON Schema and writes it as an embedded object.
 * </p>
 * <p>
 * Example output for OpenAPI:
 * <pre>
 * {
 *   "openapi": "3.0.0",
 *   "components": {
 *     "schemas": {
 *       "Person": {
 *         "type": "object",
 *         "properties": {
 *           "name": { "type": "string" }
 *         }
 *       }
 *     }
 *   }
 * }
 * </pre>
 * </p>
 * <p>
 * To register this writer for an EReference of type EPackage:
 * <pre>
 * CodecValueRegistry registry = new CodecValueRegistry();
 * registry.registerWriter("schemas", new EPackageValueWriter("schemas", true));
 * </pre>
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2025
 * @see EPackageValueReader
 */
public class EPackageValueWriter implements ReferenceValueWriter<EPackage> {

	private final EPackageToJsonSchemaConverter converter;
	private final String schemaFeature;
	private final boolean embedInFeature;

	/**
	 * Creates a writer that outputs the full JSON Schema document.
	 * Will use "definitions" as the default schema feature.
	 */
	public EPackageValueWriter() {
		this("definitions", false);
	}

	/**
	 * Creates a writer with a specific schema feature key.
	 *
	 * @param schemaFeature the key for schema definitions
	 *        (e.g., "definitions", "$defs", "schemas")
	 */
	public EPackageValueWriter(String schemaFeature) {
		this(schemaFeature, false);
	}

	/**
	 * Creates a writer with full control over output format.
	 *
	 * @param schemaFeature the key for schema definitions
	 * @param embedInFeature if true, only output the definitions content
	 *        without the wrapper object (for embedding in parent structure)
	 */
	public EPackageValueWriter(String schemaFeature, boolean embedInFeature) {
		this.converter = new EPackageToJsonSchemaConverter();
		this.schemaFeature = schemaFeature != null ? schemaFeature : "definitions";
		this.embedInFeature = embedInFeature;
	}

	@Override
	public String getName() {
		return "ePackageToJsonSchema";
	}

	/**
	 * Checks if this writer can handle the given reference.
	 * <p>
	 * Returns true if the reference type is EPackage or a supertype of EPackage.
	 * </p>
	 *
	 * @param reference the EReference to check
	 * @return true if this writer can handle the reference
	 */
	@Override
	public boolean canHandle(EReference reference) {
		return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(reference.getEReferenceType());
	}

	/**
	 * Writes an EPackage as embedded JSON Schema.
	 *
	 * @param value the EPackage to convert and write
	 * @param reference the EReference being serialized
	 * @param ctx the writer context providing generator and diagnostics
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void write(EPackage value, EReference reference, CodecWriterContext ctx) throws IOException {
		if (value == null) {
			ctx.getGenerator().writeNull();
			return;
		}

		// Convert EPackage to JSON Schema
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		converter.convert(value, baos, schemaFeature, false);

		String jsonSchema = baos.toString(StandardCharsets.UTF_8);

		// Parse the JSON Schema string and write it as raw JSON
		ObjectMapper mapper = JsonMapper.builder().build();
		Object schemaTree = mapper.readValue(jsonSchema, Object.class);

		if (embedInFeature) {
			// Only write the definitions/schemas content, not the full document
			@SuppressWarnings("unchecked")
			Map<String, Object> schemaMap = (Map<String, Object>) schemaTree;
			Object definitions = schemaMap.get(schemaFeature);
			if (definitions != null) {
				ctx.getGenerator().writePOJO(definitions);
			} else {
				ctx.getGenerator().writeStartObject();
				ctx.getGenerator().writeEndObject();
			}
		} else {
			// Write the full JSON Schema document
			ctx.getGenerator().writePOJO(schemaTree);
		}
	}
}
