/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.codec.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecResourceHelper#isInstantiable(org.eclipse.emf.ecore.EClass)}.
 * <p>
 * Validates instantiability check for abstract classes and interfaces.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#154-codec_root_object-option">Spec 15.4: Abstract and Interface EClass Handling</a>
 */
@DisplayName("CodecResourceHelper.isInstantiable - Spec 15.4: Instantiability Check")
class CodecResourceHelperIsInstantiableTest extends CodecResourceHelperTestBase {

    @Test
    @DisplayName("returns true for concrete class")
    void returnsTrueForConcreteClass() {
        boolean result = helper.isInstantiable(personClass);
        assertTrue(result);
    }

    @Test
    @DisplayName("returns true for subclass")
    void returnsTrueForSubclass() {
        boolean result = helper.isInstantiable(employeeClass);
        assertTrue(result);
    }

    @Test
    @DisplayName("returns false for abstract class")
    void returnsFalseForAbstractClass() {
        boolean result = helper.isInstantiable(abstractEntityClass);
        assertFalse(result);
    }

    @Test
    @DisplayName("returns false for interface")
    void returnsFalseForInterface() {
        boolean result = helper.isInstantiable(namedInterface);
        assertFalse(result);
    }

    @Test
    @DisplayName("returns false for null")
    void returnsFalseForNull() {
        boolean result = helper.isInstantiable(null);
        assertFalse(result);
    }
}
