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
package org.eclipse.fennec.codec.openapi;

import java.io.IOException;
import java.util.Locale;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.eclipse.fennec.model.openapi.HttpMethod;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Operation;
import org.osgi.service.component.annotations.Component;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Value reader for Operation that sets the HttpMethod based on the PathItem feature name.
 * <p>
 * In OpenAPI, the HTTP method is determined by the field name in PathItem (get, put, post, etc.),
 * not by a field within the Operation itself. This reader sets {@code Operation.method}
 * based on the EReference name during deserialization.
 * </p>
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "paths": {
 *     "/pets": {
 *       "get": { "operationId": "getPets", ... },
 *       "post": { "operationId": "createPet", ... }
 *     }
 *   }
 * }
 * </pre>
 * The "get" Operation will have method=GET, the "post" Operation will have method=POST.
 * </p>
 *
 * @author Data In Motion
 * @since 2025
 */
@Component(service = CodecValueReader.class)
public class OperationValueReader implements ReferenceValueReader<Operation> {

	@Override
	public String getName() {
		return "operation";
	}

	@Override
	public boolean canHandle(EReference reference) {
		return OpenApiPackage.Literals.OPERATION.isSuperTypeOf(reference.getEReferenceType());
	}

	@Override
	public Operation read(CodecReaderContext ctx, EReference reference) throws IOException {
		DeserializationContext jacksonCtx = ctx.getJacksonContext();

		// Set the expected type to Operation for the EMF deserializer
		EClass previousExpectedType = ContextHelper.getExpectedType(jacksonCtx);
		ContextHelper.setExpectedType(jacksonCtx, OpenApiPackage.Literals.OPERATION);

		try {
			// Deserialize the Operation using the EMF-aware deserializer
			ValueDeserializer<Object> deser = jacksonCtx.findRootValueDeserializer(
					jacksonCtx.constructType(EObject.class));

			if (deser == null) {
				throw new IOException("No deserializer found for EObject");
			}

			EObject eObject = (EObject) deser.deserialize(ctx.getParser(), jacksonCtx);

			if (eObject instanceof Operation operation) {
				// Set the HTTP method based on the feature name (get, put, post, etc.)
				HttpMethod method = httpMethodFromFeatureName(reference.getName());
				if (method != null) {
					operation.setMethod(method);
				}
				return operation;
			}

			return null;
		} finally {
			// Restore the previous expected type
			if (previousExpectedType != null) {
				ContextHelper.setExpectedType(jacksonCtx, previousExpectedType);
			} else {
				ContextHelper.clearExpectedType(jacksonCtx);
			}
		}
	}

	/**
	 * Maps a PathItem feature name to the corresponding HttpMethod.
	 *
	 * @param featureName the feature name (get, put, post, delete, options, head, patch, trace)
	 * @return the corresponding HttpMethod, or null if not a valid HTTP method feature
	 */
	private HttpMethod httpMethodFromFeatureName(String featureName) {
		if (featureName == null) {
			return null;
		}
		try {
			return HttpMethod.valueOf(featureName.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			// Not a valid HTTP method feature name
			return null;
		}
	}
}
