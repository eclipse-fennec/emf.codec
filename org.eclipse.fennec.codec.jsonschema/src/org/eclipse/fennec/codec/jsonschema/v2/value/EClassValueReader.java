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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaToEClassConverter;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.ReferenceValueReader;

import tools.jackson.core.TreeNode;
import tools.jackson.databind.JsonNode;

/**
 * Value reader that converts an embedded JSON Schema object to an EClass.
 * <p>
 * Reads a JSON object at the current parser position and converts it to an
 * EClass using {@link JsonSchemaToEClassConverter}. The class name is derived
 * from the {@code "title"} field of the schema, or defaults to {@code "EClass"}.
 * </p>
 *
 * @see EClassValueWriter
 */
public class EClassValueReader implements ReferenceValueReader<EClass> {

	private final JsonSchemaToEClassConverter converter = new JsonSchemaToEClassConverter();

	@Override
	public String getName() {
		return "jsonSchemaToEClass";
	}

	/**
	 * Returns true if the reference type is EClass or a supertype of EClass.
	 *
	 * @param reference the EReference to check
	 * @return true if this reader can handle the reference
	 */
	@Override
	public boolean canHandle(EReference reference) {
		return EcorePackage.Literals.ECLASS.isSuperTypeOf(reference.getEReferenceType());
	}

	/**
	 * Reads the current JSON object and converts it to an EClass.
	 *
	 * @param ctx the reader context providing parser and diagnostics
	 * @param reference the EReference being deserialized
	 * @return the converted EClass, or null if parsing fails
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public EClass read(CodecReaderContext ctx, EReference reference) throws IOException {
		TreeNode treeNode = ctx.getParser().readValueAsTree();

		if (treeNode == null || !treeNode.isObject()) {
			return null;
		}

		JsonNode jsonNode = (JsonNode) treeNode;
		return converter.convert(jsonNode);
	}
}
