/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.jackson.utils;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;

/**
 * 
 * @author ilenia
 * @since Apr 25, 2025
 */
public enum FeatureKind {
	SINGLE_ATTRIBUTE,
	MANY_ATTRIBUTE,
	SINGLE_REFERENCE,
	MANY_REFERENCE,
	SINGLE_CONTAINMENT,
	MANY_CONTAINMENT,
	MAP,
	UNKNOWN;

	public static FeatureKind get(final ETypedElement feature) {
		if (feature == null) {
			return UNKNOWN;
		}

		if (isMap(feature.getEType())) {
			return MAP;
		}

		if (feature instanceof EAttribute) {
			return feature.isMany() ? MANY_ATTRIBUTE : SINGLE_ATTRIBUTE;
		} else if (feature instanceof EReference) {
			return ((EReference) feature).isContainment() ? feature.isMany() ? MANY_CONTAINMENT : SINGLE_CONTAINMENT
					: feature.isMany() ? MANY_REFERENCE : SINGLE_REFERENCE;
		} else if (feature instanceof EOperation) {
			return feature.isMany() ? MANY_ATTRIBUTE : SINGLE_ATTRIBUTE;
		}

		return UNKNOWN;
	}

	private static boolean isMap(final EClassifier type) {
		return "java.util.Map$Entry".equals(type.getInstanceClassName()) ||
				"java.util.Map.Entry".equals(type.getInstanceClassName());
	}
}
