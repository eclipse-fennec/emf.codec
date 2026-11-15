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
package org.eclipse.fennec.codec.util;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * An empty JSON array or object read into an unsettable many-valued feature sets it (spec 11
 * #277, issue #319): {@code "parameters": []} is written again, an absent member stays absent. EMF
 * marks an unsettable list as set by a change only, so an element is added and removed.
 */
public final class UnsettableLists {

	private UnsettableLists() {
	}

	/**
	 * Marks an empty unsettable many-valued feature as set; does nothing otherwise.
	 *
	 * @param owner the object
	 * @param feature the many-valued feature just read
	 */
	@SuppressWarnings("unchecked")
	public static void markSetIfEmpty(EObject owner, EStructuralFeature feature) {
		if (!feature.isMany() || !feature.isUnsettable() || owner.eIsSet(feature)) {
			return;
		}
		List<Object> list = (List<Object>) owner.eGet(feature);
		if (!list.isEmpty()) {
			return;
		}
		Object placeholder = placeholder(feature);
		if (placeholder == null) {
			return;
		}
		list.add(placeholder);
		list.remove(list.size() - 1);
	}

	private static Object placeholder(EStructuralFeature feature) {
		if (feature instanceof EReference reference) {
			EClass type = reference.getEReferenceType();
			return type.isAbstract() || type.isInterface() ? null : EcoreUtil.create(type);
		}
		EDataType type = ((EAttribute) feature).getEAttributeType();
		if (type instanceof EEnum eEnum) {
			return eEnum.getELiterals().isEmpty() ? null : eEnum.getELiterals().get(0).getInstance();
		}
		Class<?> instanceClass = type.getInstanceClass();
		if (instanceClass == String.class || instanceClass == Object.class) {
			return "";
		}
		Object defaultValue = type.getDefaultValue();
		return defaultValue;
	}
}
