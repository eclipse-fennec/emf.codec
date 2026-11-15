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
package org.eclipse.fennec.codec.openapi.oas;

import java.io.IOException;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.eclipse.fennec.model.oas.OasFactory;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.SchemaReference;

import tools.jackson.databind.JsonNode;

/**
 * Reads a Schema Object at a use site into a {@link SchemaReference} (issue #301). {@code $ref},
 * simple types and their facets are mapped right away; the type itself is set by {@link OasSchemas}
 * once the description is read, because an inline schema becomes a classifier converted together
 * with {@code components/schemas}.
 */
public class SchemaReferenceValueReader implements ReferenceValueReader<SchemaReference> {

	/** The reader name the model's schema features name. */
	public static final String NAME = "oasSchema";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean canHandle(EReference reference) {
		return reference.getEReferenceType() == OasPackage.Literals.SCHEMA_REFERENCE;
	}

	@Override
	public SchemaReference read(CodecReaderContext ctx, EReference reference) throws IOException {
		JsonNode json = (JsonNode) ctx.getParser().readValueAsTree();
		SchemaReference schema = OasFactory.eINSTANCE.createSchemaReference();
		OasSchemaSource.attach(schema, json);
		return schema;
	}
}
