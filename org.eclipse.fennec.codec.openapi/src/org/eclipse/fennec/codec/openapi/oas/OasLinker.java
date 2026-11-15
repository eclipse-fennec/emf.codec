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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.openapi.OpenApiReferences;
import org.eclipse.fennec.model.oas.Components;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.PathItem;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.eclipse.fennec.model.oas.Tag;

/**
 * Completes a loaded description in the model, so that consumers need no helper (#313):
 * <ul>
 *   <li>{@link Operation#getMethod()} from the member the operation was read from, or the key in
 *       {@code additionalOperations};</li>
 *   <li>the {@code target} of every Reference Object pointing into {@code components};</li>
 *   <li>the {@code classifier} of every {@link Schema} with a {@code $ref} (or array items with one) into
 *       {@code components/schemas};</li>
 *   <li>{@link Tag#getParentTag()} from {@code parent}.</li>
 * </ul>
 */
final class OasLinker {

	private static final String REF = "ref";
	private static final String TARGET = "target";

	private OasLinker() {
	}

	/**
	 * @param api a loaded description
	 * @return the problems found: internal references without target, unknown parent tags
	 */
	static List<String> link(OpenAPI api) {
		List<String> problems = new ArrayList<>();
		Map<String, Tag> tags = new HashMap<>();
		for (Tag tag : api.getTags()) {
			tags.putIfAbsent(tag.getName(), tag);
		}
		for (TreeIterator<EObject> it = api.eAllContents(); it.hasNext();) {
			EObject object = it.next();
			if (object instanceof EPackage) {
				it.prune(); // the schema classes
			} else if (object instanceof PathItem pathItem) {
				setMethods(pathItem);
				linkReference(api, object, problems);
			} else if (object instanceof Schema schema) {
				linkSchema(api, schema, problems);
			} else if (object instanceof Tag tag && tag.getParent() != null) {
				Tag parent = tags.get(tag.getParent());
				if (parent == null) {
					problems.add("tag '" + tag.getName() + "' names the parent '" + tag.getParent() + "', which is no tag");
				}
				tag.setParentTag(parent);
			} else {
				linkReference(api, object, problems);
			}
		}
		return problems;
	}

	private static void setMethods(PathItem pathItem) {
		for (EReference reference : pathItem.eClass().getEAllContainments()) {
			if (reference.getEType() == OasPackage.Literals.OPERATION && !reference.isMany()
					&& pathItem.eGet(reference) instanceof Operation operation) {
				operation.setMethod(reference.getName().toUpperCase(Locale.ROOT));
			}
		}
		for (Map.Entry<String, Operation> entry : pathItem.getAdditionalOperations()) {
			if (entry.getValue() != null) {
				entry.getValue().setMethod(entry.getKey());
			}
		}
	}

	private static void linkReference(OpenAPI api, EObject object, List<String> problems) {
		EStructuralFeature refFeature = object.eClass().getEStructuralFeature(REF);
		EStructuralFeature targetFeature = object.eClass().getEStructuralFeature(TARGET);
		if (refFeature == null || targetFeature == null || !(object.eGet(refFeature) instanceof String ref)) {
			return;
		}
		List<String> pointer = OpenApiReferences.pointer(ref);
		if (pointer == null) {
			return; // another document
		}
		Object target = component(api.getComponents(), pointer);
		if (target != null && targetFeature.getEType().isInstance(target)) {
			object.eSet(targetFeature, target);
		} else {
			problems.add("$ref '" + ref + "' has no target in this document");
		}
	}

	private static void linkSchema(OpenAPI api, Schema schema, List<String> problems) {
		if (schema.getClassifier() != null) {
			return;
		}
		String ref = schema.getRef();
		if (ref == null && schema.getItems() != null
				&& (schema.getType().contains(SimpleType.ARRAY) || schema.getType().isEmpty())) {
			ref = schema.getItems().getRef(); // an array: its classifier is the item type
		}
		List<String> pointer = OpenApiReferences.pointer(ref);
		if (pointer == null || pointer.isEmpty() || !"components".equals(pointer.get(0))) {
			return; // another document, or a pointer into the schema itself (#/$defs/...)
		}
		Components components = api.getComponents();
		EClassifier classifier = pointer.size() == 3 && "components".equals(pointer.get(0))
				&& "schemas".equals(pointer.get(1)) && components != null
						? OpenApiReferences.classifier(components.getSchemasPackage(), pointer.get(2))
						: null;
		if (classifier == null) {
			problems.add("$ref '" + ref + "' has no target in this document");
		} else {
			schema.setClassifier(classifier);
		}
	}

	/** {@code #/components/<section>/<key>} in the map of that section. */
	private static Object component(Components components, List<String> pointer) {
		if (components == null || pointer.size() != 3 || !"components".equals(pointer.get(0))) {
			return null;
		}
		EStructuralFeature section = components.eClass().getEStructuralFeature(pointer.get(1));
		return section != null && components.eGet(section) instanceof EMap<?, ?> map ? map.get(pointer.get(2)) : null;
	}
}
