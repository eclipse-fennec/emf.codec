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
package org.eclipse.fennec.codec.oscal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gov.nist.csrc.ns.oscal.OSCALPackage;

/**
 * The OSCAL model is loaded from its {@code .ecore} at runtime ({@code loadInitialization}), so
 * a classifier or feature ID is its position in that file, while the generated code uses the
 * constants of {@link OSCALPackage}. If the two disagree, the generated factory converts a value
 * with the wrong data type and {@code eGet}/{@code eSet} reach the wrong feature - silently. The
 * codec relies on both, so this test guards the model it is built against.
 */
@DisplayName("OSCAL model IDs")
class OscalModelIdsTest {

	private static String constant(String name) {
		return name.replaceAll("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", "_").toUpperCase();
	}

	private static int value(String constant) {
		try {
			Field field = OSCALPackage.class.getField(constant);
			return field.getInt(null);
		} catch (NoSuchFieldException e) {
			return Integer.MIN_VALUE;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	void classifierIdsMatchTheGeneratedConstants() {
		List<String> wrong = new ArrayList<>();
		for (EClassifier classifier : OSCALPackage.eINSTANCE.getEClassifiers()) {
			int expected = value(constant(classifier.getName()));
			if (expected != classifier.getClassifierID()) {
				wrong.add(classifier.getName() + " constant " + expected + " runtime " + classifier.getClassifierID());
			}
		}
		assertEquals(List.of(), wrong);
	}

	@Test
	void featureIdsMatchTheGeneratedConstants() {
		List<String> wrong = new ArrayList<>();
		for (EClassifier classifier : OSCALPackage.eINSTANCE.getEClassifiers()) {
			if (classifier instanceof EClass eClass) {
				for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
					String constant = constant(eClass.getName()) + "__" + constant(feature.getName());
					int expected = value(constant);
					// the XML machinery of DocumentRoot has irregular constant names
					if (expected != Integer.MIN_VALUE && expected != eClass.getFeatureID(feature)) {
						wrong.add(constant + " constant " + expected + " runtime " + eClass.getFeatureID(feature));
					}
				}
			}
		}
		assertEquals(List.of(), wrong);
	}
}
