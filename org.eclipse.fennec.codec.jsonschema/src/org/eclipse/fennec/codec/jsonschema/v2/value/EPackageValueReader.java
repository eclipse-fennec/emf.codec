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

import java.io.IOException;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaToEPackageConverter;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.ReferenceValueReader;

import tools.jackson.core.TreeNode;
import tools.jackson.databind.JsonNode;

/**
 * Value reader for embedded JSON Schema that converts to EPackage.
 * <p>
 * This reader is used when JSON Schema is embedded within another format
 * (e.g., OpenAPI's components/schemas section). It reads a JSON object
 * containing JSON Schema and converts it to an EPackage.
 * </p>
 * <p>
 * Example usage in OpenAPI:
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
 * To register this reader for an EReference of type EPackage:
 * <pre>
 * CodecValueRegistry registry = new CodecValueRegistry();
 * registry.registerReader("schemas", new EPackageValueReader());
 * </pre>
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2025
 * @see EPackageValueWriter
 */
public class EPackageValueReader implements ReferenceValueReader<EPackage> {

	private final JsonSchemaToEPackageConverter converter;

	/**
	 * Creates a reader that auto-detects the schema structure.
	 * Will auto-detect "definitions", "$defs", or "schemas", or treat
	 * the JSON as direct schema definitions.
	 */
	public EPackageValueReader() {
		this.converter = new JsonSchemaToEPackageConverter();
	}

	@Override
	public String getName() {
		return "jsonSchemaToEPackage";
	}

	/**
	 * Checks if this reader can handle the given reference.
	 * <p>
	 * Returns true if the reference type is EPackage or a supertype of EPackage.
	 * </p>
	 *
	 * @param reference the EReference to check
	 * @return true if this reader can handle the reference
	 */
	@Override
	public boolean canHandle(EReference reference) {
		return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(reference.getEReferenceType());
	}

	/**
	 * Reads embedded JSON Schema and converts to EPackage.
	 * <p>
	 * When used for embedded schemas (like OpenAPI components/schemas), the JSON content
	 * IS the schema definitions directly. The converter will auto-detect this case.
	 * </p>
	 *
	 * @param ctx the reader context providing parser and diagnostics
	 * @param reference the EReference being deserialized
	 * @return the converted EPackage, or null if parsing fails
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public EPackage read(CodecReaderContext ctx, EReference reference) throws IOException {
		// Read the JSON tree at current position
		TreeNode treeNode = ctx.getParser().readValueAsTree();

		if (treeNode == null || !treeNode.isObject()) {
			return null;
		}

		// Convert TreeNode to JsonNode for the converter
		JsonNode jsonNode = (JsonNode) treeNode;

		// Use the converter to transform JSON Schema to EPackage
		// Pass null for schemaFeature - the converter will auto-detect:
		// - If the JSON has a definitions/$defs/schemas key, it uses that
		// - Otherwise, it treats the JSON as direct schema definitions
		return converter.convert(jsonNode, null);
	}
}
