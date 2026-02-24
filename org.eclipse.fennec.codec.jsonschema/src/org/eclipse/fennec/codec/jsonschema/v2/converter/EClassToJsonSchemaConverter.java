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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;

/**
 * Converts a single EMF EClass to a standalone JSON Schema document.
 * <p>
 * The produced document follows the shape:
 * <pre>
 * {
 *   "$schema": "...",
 *   "$id": "http://my.ns#MyClass",
 *   "title": "MyClass",
 *   "description": "...",
 *   "type": "object",
 *   "properties": { ... },
 *   "required": [...]
 * }
 * </pre>
 * </p>
 * <p>
 * Use {@link EPackageToJsonSchemaConverter#OPTION_ALL_FIELDS_REQUIRED} to mark
 * every property as required (e.g. for AI structured-output schemas):
 * <pre>
 * converter.convert(eClass, out, true, true);
 * </pre>
 * </p>
 *
 * @see JsonSchemaToEClassConverter
 * @see EPackageToJsonSchemaConverter#OPTION_ALL_FIELDS_REQUIRED
 */
public class EClassToJsonSchemaConverter {

	private final EPackageToJsonSchemaConverter delegate = new EPackageToJsonSchemaConverter();

	/**
	 * Converts the given EClass to a JSON Schema document.
	 *
	 * @param eClass the EClass to convert
	 * @param out the output stream to write to
	 * @throws IOException if writing fails
	 */
	public void convert(EClass eClass, OutputStream out) throws IOException {
		convert(eClass, out, false, false);
	}

	/**
	 * Converts the given EClass to a JSON Schema document.
	 *
	 * @param eClass the EClass to convert
	 * @param out the output stream to write to
	 * @param prettyPrint whether to format the output with indentation
	 * @throws IOException if writing fails
	 */
	public void convert(EClass eClass, OutputStream out, boolean prettyPrint) throws IOException {
		convert(eClass, out, prettyPrint, false);
	}

	/**
	 * Converts the given EClass to a JSON Schema document.
	 *
	 * @param eClass the EClass to convert
	 * @param out the output stream to write to
	 * @param prettyPrint whether to format the output with indentation
	 * @param allFieldsRequired when {@code true}, every property is added to
	 *        {@code required} regardless of its lowerBound — useful for AI
	 *        structured-output schemas where all fields must be populated
	 * @throws IOException if writing fails
	 */
	public void convert(EClass eClass, OutputStream out, boolean prettyPrint, boolean allFieldsRequired) throws IOException {
		Map<String, Object> options = allFieldsRequired
				? Map.of(EPackageToJsonSchemaConverter.OPTION_ALL_FIELDS_REQUIRED, Boolean.TRUE)
				: Map.of();
		delegate.convertEClass(eClass, out, prettyPrint, options);
	}

	/**
	 * Converts the given EClass to a JSON Schema document with full option control.
	 *
	 * @param eClass the EClass to convert
	 * @param out the output stream to write to
	 * @param prettyPrint whether to format the output with indentation
	 * @param options conversion options (see {@link EPackageToJsonSchemaConverter} constants)
	 * @throws IOException if writing fails
	 */
	public void convert(EClass eClass, OutputStream out, boolean prettyPrint, Map<String, Object> options) throws IOException {
		delegate.convertEClass(eClass, out, prettyPrint, options);
	}
}
