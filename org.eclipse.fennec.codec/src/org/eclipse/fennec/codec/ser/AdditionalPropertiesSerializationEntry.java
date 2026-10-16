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
package org.eclipse.fennec.codec.ser;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.util.EMapHelper;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.json.JsonMapper;

/**
 * Writes the entries of a catch-all map as members of the object (spec 21 §6.2, issue #278).
 * <p>
 * The map feature itself is never a key: each entry becomes one member, its key the entry key,
 * its value the raw JSON text the reader kept - or, for a map of a class, the object written
 * like any contained object.
 * </p>
 *
 * @since 1.0
 */
public class AdditionalPropertiesSerializationEntry implements SerializationEntry {

	private static final JsonMapper RAW_JSON = JsonMapper.builder().build();

	private final EReference map;
	private final EStructuralFeature keyFeature;
	private final EStructuralFeature valueFeature;

	/**
	 * @param map the catch-all map feature, a multi-valued containment of map entries
	 */
	public AdditionalPropertiesSerializationEntry(EReference map) {
		this.map = map;
		this.keyFeature = EMapHelper.getKeyFeature(map.getEReferenceType());
		this.valueFeature = EMapHelper.getValueFeature(map.getEReferenceType());
	}

	/** Not a JSON key: the entries are written in its place. */
	@Override
	public String getKey() {
		return "\u0000" + map.getName();
	}

	@Override
	public boolean shouldSerialize(SerializationState state) {
		return !entries(state).isEmpty();
	}

	@Override
	public void serialize(SerializationState state, JsonGenerator gen, SerializationContext ctxt) {
		for (EObject entry : entries(state)) {
			Object value = entry.eGet(valueFeature);
			gen.writeName(String.valueOf(entry.eGet(keyFeature)));
			if (value == null) {
				gen.writeNull();
			} else if (valueFeature instanceof EReference valueReference) {
				ContextHelper.setCurrentSerializationReference(ctxt, valueReference);
				try {
					ctxt.writeValue(gen, value);
				} finally {
					ContextHelper.clearCurrentSerializationReference(ctxt);
				}
			} else {
				// Parsed again so every format writes the value, not only a JSON generator
				JsonNode node = RAW_JSON.readTree(value.toString());
				gen.writeTree(node);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<EObject> entries(SerializationState state) {
		return (List<EObject>) state.getValue(map);
	}
}
