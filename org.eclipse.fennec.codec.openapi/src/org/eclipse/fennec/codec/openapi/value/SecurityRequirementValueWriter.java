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
package org.eclipse.fennec.codec.openapi.value;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.SecurityRequirement;

import tools.jackson.core.JsonGenerator;

/**
 * Value writer for {@link SecurityRequirement} objects — the mirror image of
 * {@link SecurityRequirementValueReader}.
 * <p>
 * Writes the {@code schemes} EMap as the requirement object itself, with scheme
 * names as dynamic field names and scope lists as array values:
 * <pre>
 * { "oauth2": ["read:pets", "write:pets"] }
 * </pre>
 * Without this writer, the default serializer would emit the internal wrapped
 * model shape instead of the OpenAPI requirement-object shape.
 * </p>
 *
 * @author Data In Motion
 * @since 1.0
 */
public class SecurityRequirementValueWriter implements ReferenceValueWriter<SecurityRequirement> {

	@Override
	public String getName() {
		return "securityRequirement";
	}

	@Override
	public boolean canHandle(EReference reference) {
		return OpenApiPackage.Literals.SECURITY_REQUIREMENT.isSuperTypeOf(reference.getEReferenceType());
	}

	@Override
	public void write(SecurityRequirement value, EReference reference, CodecWriterContext ctx) throws IOException {
		JsonGenerator gen = ctx.getGenerator();
		gen.writeStartObject();
		for (Map.Entry<String, EList<String>> entry : value.getSchemes().entrySet()) {
			gen.writeName(entry.getKey());
			gen.writeStartArray();
			if (entry.getValue() != null) {
				for (String scope : entry.getValue()) {
					gen.writeString(scope);
				}
			}
			gen.writeEndArray();
		}
		gen.writeEndObject();
	}
}
