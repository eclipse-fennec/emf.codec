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
package org.eclipse.fennec.codec.openapi;

import java.util.List;
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Test helper: names the first place two models differ, so a failing equality assertion says
 * where instead of only that.
 */
final class ModelDiff {

	private ModelDiff() {
	}

	/**
	 * @return a path and the two differing values, or {@code null} when the models are equal in
	 *         the containment tree and the attribute values
	 */
	static String firstDifference(EObject a, EObject b) {
		return diff(a, b, "/" + (a == null ? "null" : a.eClass().getName()));
	}

	private static String diff(EObject a, EObject b, String path) {
		if (a == null || b == null) {
			return a == b ? null : path + ": " + a + " <> " + b;
		}
		if (a.eClass() != b.eClass()) {
			return path + ": " + a.eClass().getName() + " <> " + b.eClass().getName();
		}
		for (EStructuralFeature feature : a.eClass().getEAllStructuralFeatures()) {
			if (feature.isDerived() || feature.isTransient()) {
				continue;
			}
			Object va = a.eGet(feature);
			Object vb = b.eGet(feature);
			String featurePath = path + "/" + feature.getName();
			if (feature instanceof EReference ref && ref.isContainment()) {
				if (feature.isMany()) {
					List<?> la = (List<?>) va;
					List<?> lb = (List<?>) vb;
					if (la.size() != lb.size()) {
						return featurePath + ": size " + la.size() + " <> " + lb.size();
					}
					for (int i = 0; i < la.size(); i++) {
						String d = diff((EObject) la.get(i), (EObject) lb.get(i), featurePath + "[" + i + "]");
						if (d != null) {
							return d;
						}
					}
				} else {
					String d = diff((EObject) va, (EObject) vb, featurePath);
					if (d != null) {
						return d;
					}
				}
			} else if (!(feature instanceof EReference) && !Objects.equals(va, vb)) {
				return featurePath + ": " + va + " <> " + vb;
			}
		}
		return null;
	}
}
