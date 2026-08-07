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
package org.eclipse.fennec.codec.openapi.internal;

import java.io.IOException;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.jsonschema.v2.value.EPackageValueWriter;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.osgi.service.component.annotations.Component;

/**
 * Value writer for OpenAPI {@code components/schemas}.
 * <p>
 * Wraps {@link EPackageValueWriter} with {@code embedInFeature=true} so the
 * output is a flat schema map (e.g. {@code {"Pet": {...}}}) rather than the full
 * JSON Schema document (e.g. {@code {"definitions": {"Pet": {...}}}}).
 * </p>
 *
 * @author Data In Motion
 * @since 2025
 */
@Component(service = CodecValueWriter.class)
public class OpenApiSchemasValueWriter implements ReferenceValueWriter<EPackage> {

	private final EPackageValueWriter delegate = new EPackageValueWriter("definitions", true);

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
		delegate.write(value, reference, ctx);
	}
}
