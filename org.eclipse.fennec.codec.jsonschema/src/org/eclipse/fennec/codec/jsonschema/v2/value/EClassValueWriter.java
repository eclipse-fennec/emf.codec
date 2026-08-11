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
import java.util.Collections;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.jsonschema.v2.converter.EClassToJsonSchemaConverter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Value writer that converts an EClass to an embedded JSON Schema object.
 * <p>
 * Converts an EClass to a standalone JSON Schema document and writes it as
 * an inline JSON object at the current generator position.
 * </p>
 *
 * @see EClassValueReader
 */
public class EClassValueWriter implements ReferenceValueWriter<EClass> {

	private final EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();

	@Override
	public String getName() {
		return "eClassToJsonSchema";
	}

	/**
	 * Returns true if the reference type is EClass or a supertype of EClass.
	 *
	 * @param reference the EReference to check
	 * @return true if this writer can handle the reference
	 */
	@Override
	public boolean canHandle(EReference reference) {
		return EcorePackage.Literals.ECLASS.isSuperTypeOf(reference.getEReferenceType());
	}

	/**
	 * Converts the EClass to a JSON Schema document and writes it inline.
	 *
	 * @param value the EClass to convert and write
	 * @param reference the EReference being serialized
	 * @param ctx the writer context providing generator and diagnostics
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void write(EClass value, EReference reference, CodecWriterContext ctx) throws IOException {
		if (value == null) {
			ctx.getGenerator().writeNull();
			return;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(ctx.getConfig() != null) {
			converter.convert(value, baos, false, ctx.getConfig().getCustomProperties());
		}
		else {
			converter.convert(value, baos, false, Collections.emptyMap());
		}
		ObjectMapper mapper = JsonMapper.builder().build();
		Object tree = mapper.readValue(baos.toString(StandardCharsets.UTF_8), Object.class);
		ctx.getGenerator().writePOJO(tree);
	}
}
