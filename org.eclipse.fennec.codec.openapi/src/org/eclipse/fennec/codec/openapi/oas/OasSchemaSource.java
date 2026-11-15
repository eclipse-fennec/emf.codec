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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

import tools.jackson.databind.JsonNode;

/**
 * The Schema Object JSON a reader saw, kept on the model object until the description is loaded
 * completely (issue #301): inline schemas refer to {@code components/schemas}, which usually comes
 * later in the document, so all of them are converted together afterwards. Removed after the
 * conversion; never written.
 */
final class OasSchemaSource extends AdapterImpl {

	private final JsonNode json;

	private OasSchemaSource(JsonNode json) {
		this.json = json;
	}

	JsonNode json() {
		return json;
	}

	static void attach(Notifier target, JsonNode json) {
		target.eAdapters().add(new OasSchemaSource(json));
	}

	/** @return the JSON and detaches it, {@code null} if there is none */
	static JsonNode take(Notifier target) {
		for (var adapter : target.eAdapters()) {
			if (adapter instanceof OasSchemaSource source) {
				target.eAdapters().remove(source);
				return source.json;
			}
		}
		return null;
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return type == OasSchemaSource.class;
	}
}
