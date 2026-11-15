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

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.ReferenceValueReader;

import tools.jackson.databind.JsonNode;

/**
 * Reads {@code components/schemas} of the oas model (issue #301): keeps the JSON on an empty package,
 * which {@link OasSchemas} replaces with the converted one once the whole description is read -
 * together with the inline schemas, so that both refer to each other.
 */
public class ComponentsSchemasValueReader implements ReferenceValueReader<EPackage> {

	/** The reader name the model's {@code Components.schemas} names. */
	public static final String NAME = "oasSchemas";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean canHandle(EReference reference) {
		return reference.getEReferenceType() == EcorePackage.Literals.EPACKAGE;
	}

	@Override
	public EPackage read(CodecReaderContext ctx, EReference reference) throws IOException {
		JsonNode json = (JsonNode) ctx.getParser().readValueAsTree();
		EPackage placeholder = EcoreFactory.eINSTANCE.createEPackage();
		OasSchemaSource.attach(placeholder, json);
		return placeholder;
	}
}
