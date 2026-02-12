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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.emf.ecore.EClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecResourceHelper#resolveEffectiveType(org.eclipse.emf.ecore.EClass, org.eclipse.emf.ecore.EClass)}.
 * <p>
 * Validates type resolution priority as specified in Spec 15.6.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#156-type-resolution-priority">Spec 15.6: Type Resolution Priority</a>
 */
@DisplayName("CodecResourceHelper.resolveEffectiveType - Spec 15.6: Type Resolution Priority")
class CodecResourceHelperResolveEffectiveTypeTest extends CodecResourceHelperTestBase {

    @Test
    @DisplayName("returns null when both hint and contentType are null")
    void returnsNullWhenBothAreNull() {
        EClass result = helper.resolveEffectiveType(null, null);
        assertNull(result);
    }

    /**
     * Spec 15.6: "Only content type (no CODEC_ROOT_TYPE) - Use content type info"
     */
    @Test
    @DisplayName("returns contentType when hint is null (Spec 15.6: only content type)")
    void returnsContentTypeWhenHintIsNull() {
        EClass result = helper.resolveEffectiveType(null, personClass);
        assertSame(personClass, result);
    }

    /**
     * Spec 15.6: "Only CODEC_ROOT_TYPE (no type in content) - Use CODEC_ROOT_TYPE"
     */
    @Test
    @DisplayName("returns hint when contentType is null (Spec 15.6: only CODEC_ROOT_TYPE)")
    void returnsHintWhenContentTypeIsNull() {
        EClass result = helper.resolveEffectiveType(personClass, null);
        assertSame(personClass, result);
    }

    /**
     * Spec 15.6: "No collision (content type equals [...] CODEC_ROOT_TYPE) - Use CODEC_ROOT_TYPE"
     */
    @Test
    @DisplayName("returns contentType when types are the same (Spec 15.6: no collision)")
    void returnsContentTypeWhenTypesAreSame() {
        EClass result = helper.resolveEffectiveType(personClass, personClass);
        assertSame(personClass, result);
    }

    /**
     * Spec 15.6: "No collision (content type [...] is subtype of CODEC_ROOT_TYPE)"
     * Example: "CODEC_ROOT_TYPE = Person, Content _type = Employee, Result: deserialize as Employee"
     */
    @Test
    @DisplayName("returns contentType (subtype) when no collision (Spec 15.6: subtype compatible)")
    void returnsContentTypeWhenSubtype() {
        EClass result = helper.resolveEffectiveType(personClass, employeeClass);
        assertSame(employeeClass, result);
    }

    /**
     * Spec 15.6: "Collision (content type differs) - Raise warning, use content type info"
     */
    @Test
    @DisplayName("returns contentType with warning when collision - supertype (Spec 15.6: collision)")
    void returnsContentTypeWithWarningWhenSupertype() {
        EClass result = helper.resolveEffectiveType(employeeClass, personClass);
        assertSame(personClass, result);
    }

    /**
     * Spec 15.6: "Collision (content type differs from CODEC_ROOT_TYPE)"
     * Example: "CODEC_ROOT_TYPE = Person, Content _type = Address, Result: WARNING, deserialize as Address"
     */
    @Test
    @DisplayName("returns contentType with warning when collision - unrelated (Spec 15.6: collision)")
    void returnsContentTypeWithWarningWhenUnrelated() {
        EClass result = helper.resolveEffectiveType(personClass, addressClass);
        assertSame(addressClass, result);
    }
}
