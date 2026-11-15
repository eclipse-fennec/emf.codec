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
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.osgi.service.component.annotations.Component;

import tools.jackson.core.JsonGenerator;

/**
 * Value writer for {@code SecurityRequirement} objects — the mirror image of
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
@Component(service = CodecValueWriter.class)
public class SecurityRequirementValueWriter implements ReferenceValueWriter<EObject> {

	@Override
	public String getName() {
		return "securityRequirement";
	}

	@Override
	public boolean canHandle(EReference reference) {
		return SecurityRequirementValueReader.CLASS_NAME.equals(reference.getEReferenceType().getName())
				&& reference.getEReferenceType().getEStructuralFeature(SecurityRequirementValueReader.SCHEMES) != null;
	}

	@Override
	public void write(EObject value, EReference reference, CodecWriterContext ctx) throws IOException {
		JsonGenerator gen = ctx.getGenerator();
		@SuppressWarnings("unchecked")
		EMap<String, EList<String>> schemes = (EMap<String, EList<String>>) value
				.eGet(value.eClass().getEStructuralFeature(SecurityRequirementValueReader.SCHEMES));
		gen.writeStartObject();
		for (Map.Entry<String, EList<String>> entry : schemes.entrySet()) {
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
