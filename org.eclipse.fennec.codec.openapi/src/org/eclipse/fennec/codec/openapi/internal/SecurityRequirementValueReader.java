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
package org.eclipse.fennec.codec.openapi.internal;

import java.io.IOException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.eclipse.fennec.model.openapi.OpenApiFactory;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.SecurityRequirement;
import org.osgi.service.component.annotations.Component;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

/**
 * Value reader for {@link SecurityRequirement} objects.
 * <p>
 * In OpenAPI, a security requirement object IS the map — its field names are dynamic
 * security-scheme names, its values are scope arrays:
 * <pre>
 * "security": [
 *   { "api_key": [] },
 *   { "oauth2": ["read:pets", "write:pets"] }
 * ]
 * </pre>
 * The model wraps these entries in the {@code schemes} EMap feature, which has no
 * counterpart field in the JSON, so the generic deserializer cannot map them. This
 * reader bridges the shape mismatch by reading each field as a scheme→scopes entry.
 * </p>
 *
 * @author Data In Motion
 * @since 1.0
 */
@Component(service = CodecValueReader.class)
public class SecurityRequirementValueReader implements ReferenceValueReader<SecurityRequirement> {

	@Override
	public String getName() {
		return "securityRequirement";
	}

	@Override
	public boolean canHandle(EReference reference) {
		return OpenApiPackage.Literals.SECURITY_REQUIREMENT.isSuperTypeOf(reference.getEReferenceType());
	}

	@Override
	public SecurityRequirement read(CodecReaderContext ctx, EReference reference) throws IOException {
		JsonParser parser = ctx.getParser();
		if (parser.currentToken() != JsonToken.START_OBJECT) {
			ctx.addWarning("Security requirement is not a JSON object: " + parser.currentToken());
			parser.skipChildren();
			return null;
		}
		SecurityRequirement requirement = OpenApiFactory.eINSTANCE.createSecurityRequirement();
		JsonToken fieldToken;
		while ((fieldToken = parser.nextToken()) != null && fieldToken != JsonToken.END_OBJECT) {
			String scheme = parser.currentName();
			parser.nextToken();
			EList<String> scopes = new BasicEList<>();
			if (parser.currentToken() == JsonToken.START_ARRAY) {
				JsonToken itemToken;
				while ((itemToken = parser.nextToken()) != null && itemToken != JsonToken.END_ARRAY) {
					scopes.add(parser.getString());
				}
			} else {
				ctx.addWarning("Scopes of security scheme '" + scheme + "' are not a JSON array");
				parser.skipChildren();
			}
			requirement.getSchemes().put(scheme, scopes);
		}
		return requirement;
	}
}
