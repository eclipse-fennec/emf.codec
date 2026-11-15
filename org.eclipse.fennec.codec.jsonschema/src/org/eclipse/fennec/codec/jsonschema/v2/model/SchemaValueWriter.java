/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
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
package org.eclipse.fennec.codec.jsonschema.v2.model;

import java.io.IOException;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.osgi.service.component.annotations.Component;

/**
 * Writes a {@link Schema} of the JSON Schema model as it was read (issues #317, #319), via
 * {@link SchemaJson}.
 */
@Component(service = CodecValueWriter.class)
public class SchemaValueWriter implements ReferenceValueWriter<Schema> {

	@Override
	public String getName() {
		return SchemaValueReader.NAME;
	}

	@Override
	public boolean canHandle(EReference reference) {
		return JsonSchemaPackage.Literals.SCHEMA.isSuperTypeOf(reference.getEReferenceType());
	}

	@Override
	public void write(Schema value, EReference reference, CodecWriterContext ctx) throws IOException {
		ctx.getGenerator().writeTree(SchemaJson.write(value));
	}
}
