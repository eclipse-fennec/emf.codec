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
package org.eclipse.fennec.codec.openapi.value;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.codec.jsonschema.v2.value.EPackageValueWriter;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.osgi.service.component.annotations.Component;

/**
 * Value writer for OpenAPI {@code components/schemas}.
 * <p>
 * Wraps {@link EPackageValueWriter} with {@code embedInFeature=true} so the
 * output is a flat schema map (e.g. {@code {"Pet": {...}}}) rather than the full
 * JSON Schema document (e.g. {@code {"components/schemas": {"Pet": {...}}}}).
 * </p>
 * <p>
 * The Schema Object differs between OpenAPI versions: 3.0 allows a single {@code type} string
 * and expresses nullability with {@code nullable: true}, 3.1 is JSON Schema 2020-12 with type
 * arrays. Unless the caller sets {@link CodecJsonSchemaOptions#OPTION_NULLABLE_KEYWORD}, the
 * writer follows the {@code openapi} version of the document the schemas belong to; any version
 * other than 3.1 and later, or none, counts as 3.0 (issue #270).
 * </p>
 *
 * @author Data In Motion
 * @since 1.0
 */
@Component(service = CodecValueWriter.class)
public class OpenApiSchemasValueWriter implements ReferenceValueWriter<EPackage> {

	/**
	 * The schema feature is the path the schemas are embedded at: the delegate writes only the
	 * content below it, but the converter builds every {@code $ref} from it (issue #273).
	 */
	private final EPackageValueWriter delegate = new EPackageValueWriter("components/schemas", true);

	@Override
	public String getName() {
		return "ePackageToOpenApiSchemas";
	}

	@Override
	public boolean canHandle(EReference reference) {
		return delegate.canHandle(reference);
	}

	@Override
	public void write(EPackage value, EReference reference, CodecWriterContext ctx) throws IOException {
		Map<String, Object> options = new HashMap<>();
		if (ctx.getConfig() != null) {
			options.putAll(ctx.getConfig().getCustomProperties());
		}
		options.putIfAbsent(CodecJsonSchemaOptions.OPTION_NULLABLE_KEYWORD,
				!isOpenApi31OrLater(findDocument(value)));
		delegate.write(value, ctx, options);
	}

	private static OpenAPI findDocument(EObject object) {
		for (EObject current = object; current != null; current = current.eContainer()) {
			if (current instanceof OpenAPI openApi) {
				return openApi;
			}
		}
		return null;
	}

	private static boolean isOpenApi31OrLater(OpenAPI document) {
		if (document == null || document.getOpenapi() == null) {
			return false;
		}
		String[] parts = document.getOpenapi().trim().split("\\.");
		try {
			int major = Integer.parseInt(parts[0]);
			int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
			return major > 3 || (major == 3 && minor >= 1);
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
