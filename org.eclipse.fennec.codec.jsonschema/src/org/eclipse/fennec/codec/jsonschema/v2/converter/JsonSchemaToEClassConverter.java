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
package org.eclipse.fennec.codec.jsonschema.v2.converter;

import org.eclipse.emf.ecore.EClass;

import tools.jackson.databind.JsonNode;

/**
 * Converts a standalone JSON Schema document to a single EMF EClass.
 * <p>
 * The input schema should represent a single object type, e.g.:
 * <pre>
 * {
 *   "$schema": "...",
 *   "$id": "http://my.ns#MyClass",
 *   "title": "MyClass",
 *   "description": "...",
 *   "type": "object",
 *   "properties": { ... }
 * }
 * </pre>
 * Document-level fields are preserved as EClass annotations:
 * <ul>
 *   <li>{@code $schema} → JSONSCHEMA annotation {@code "schema"}</li>
 *   <li>{@code $id} → JSONSCHEMA annotation {@code "id"}</li>
 *   <li>{@code description} → GEN_MODEL annotation {@code "documentation"}</li>
 * </ul>
 * </p>
 *
 * @see EClassToJsonSchemaConverter
 */
public class JsonSchemaToEClassConverter {

	private final JsonSchemaToEPackageConverter delegate = new JsonSchemaToEPackageConverter();

	/**
	 * Converts a JSON Schema node to an EClass, deriving the class name from the
	 * {@code "title"} field, or defaulting to {@code "EClass"} if absent.
	 *
	 * @param schemaNode the JSON Schema node
	 * @return the created EClass, or null if conversion fails
	 */
	public EClass convert(JsonNode schemaNode) {
		return delegate.convertToEClass(schemaNode, null);
	}

	/**
	 * Converts a JSON Schema node to an EClass with an explicit class name.
	 *
	 * @param schemaNode the JSON Schema node
	 * @param name the name to use for the EClass
	 * @return the created EClass, or null if conversion fails
	 */
	public EClass convert(JsonNode schemaNode, String name) {
		return delegate.convertToEClass(schemaNode, name);
	}
}
