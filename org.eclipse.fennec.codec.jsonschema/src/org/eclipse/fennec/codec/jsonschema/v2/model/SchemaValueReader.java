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
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.osgi.service.component.annotations.Component;

import tools.jackson.databind.JsonNode;

/**
 * Reads a JSON Schema at a feature of type {@link Schema} - an object or a boolean - into the JSON
 * Schema model without loss (issues #317, #319), via {@link SchemaJson}.
 */
@Component(service = CodecValueReader.class)
public class SchemaValueReader implements ReferenceValueReader<Schema> {

	/** The reader and writer name features of type {@link Schema} name. */
	public static final String NAME = "jsonSchema";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean canHandle(EReference reference) {
		return JsonSchemaPackage.Literals.SCHEMA.isSuperTypeOf(reference.getEReferenceType());
	}

	@Override
	public Schema read(CodecReaderContext ctx, EReference reference) throws IOException {
		return SchemaJson.read((JsonNode) ctx.getParser().readValueAsTree());
	}
}
