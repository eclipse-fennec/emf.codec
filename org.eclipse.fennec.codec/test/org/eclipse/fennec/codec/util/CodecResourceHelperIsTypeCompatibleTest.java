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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecResourceHelper#isTypeCompatible(org.eclipse.emf.ecore.EClass, org.eclipse.emf.ecore.EClass)}.
 * <p>
 * Validates type collision detection as specified in Spec 15.6.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#156-type-resolution-priority">Spec 15.6: Type Resolution Priority</a>
 */
@DisplayName("CodecResourceHelper.isTypeCompatible - Spec 15.6: Type Collision Detection")
class CodecResourceHelperIsTypeCompatibleTest extends CodecResourceHelperTestBase {

    @Test
    @DisplayName("returns true when hint is null (no collision possible)")
    void returnsTrueWhenHintIsNull() {
        boolean result = helper.isTypeCompatible(null, personClass);
        assertTrue(result);
    }

    @Test
    @DisplayName("returns true when contentType is null (no collision possible)")
    void returnsTrueWhenContentTypeIsNull() {
        boolean result = helper.isTypeCompatible(personClass, null);
        assertTrue(result);
    }

    @Test
    @DisplayName("returns true when both are null")
    void returnsTrueWhenBothAreNull() {
        boolean result = helper.isTypeCompatible(null, null);
        assertTrue(result);
    }

    /**
     * Spec 15.6: "No collision (content type equals [...] CODEC_ROOT_TYPE)"
     */
    @Test
    @DisplayName("returns true when types are the same (Spec 15.6: no collision)")
    void returnsTrueWhenTypesAreSame() {
        boolean result = helper.isTypeCompatible(personClass, personClass);
        assertTrue(result);
    }

    /**
     * Spec 15.6: "No collision (content type [...] is subtype of CODEC_ROOT_TYPE)"
     * Example: "CODEC_ROOT_TYPE = Person (supertype), Content _type = Employee (subtype)"
     */
    @Test
    @DisplayName("returns true when contentType is subtype of hint (Spec 15.6: subtype - no collision)")
    void returnsTrueWhenContentTypeIsSubtype() {
        boolean result = helper.isTypeCompatible(personClass, employeeClass);
        assertTrue(result);
    }

    /**
     * Spec 15.6: "Collision (content type differs from CODEC_ROOT_TYPE)"
     * When hint is subtype and content is supertype - this is a collision.
     */
    @Test
    @DisplayName("returns false when contentType is supertype of hint (collision)")
    void returnsFalseWhenContentTypeIsSupertype() {
        boolean result = helper.isTypeCompatible(employeeClass, personClass);
        assertFalse(result);
    }

    /**
     * Spec 15.6: "Collision (content type differs from CODEC_ROOT_TYPE)"
     * Example: "CODEC_ROOT_TYPE = Person, Content _type = Address (unrelated type)"
     */
    @Test
    @DisplayName("returns false when types are unrelated (Spec 15.6: collision)")
    void returnsFalseWhenTypesAreUnrelated() {
        boolean result = helper.isTypeCompatible(personClass, addressClass);
        assertFalse(result);
    }
}
