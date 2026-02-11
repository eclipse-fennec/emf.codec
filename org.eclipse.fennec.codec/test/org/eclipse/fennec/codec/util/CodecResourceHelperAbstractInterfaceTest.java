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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.ecore.EClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for abstract and interface EClass handling.
 * <p>
 * Validates behavior when CODEC_ROOT_TYPE points to abstract/interface types.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#154-codec_root_object-option">Spec 15.4: Abstract and Interface EClass Handling</a>
 * @see <a href="docs/codec-v2-serialization-spec.md#175-type-resolution-tests">Spec 17.5: T8-T11</a>
 */
@DisplayName("CodecResourceHelper Abstract/Interface Handling - Spec 15.4, 17.5: T8-T11")
class CodecResourceHelperAbstractInterfaceTest extends CodecResourceHelperTestBase {

    /**
     * Spec 17.5 T8: Abstract hint + content subtype -> Use content type (Person)
     */
    @Test
    @DisplayName("T8: abstract hint with content subtype - use content type")
    void abstractHintWithContentSubtype() {
        // AbstractEntity is abstract, Person is concrete subtype
        EClass result = helper.resolveEffectiveType(abstractEntityClass, personClass);
        assertSame(personClass, result);
    }

    /**
     * Spec 17.5 T9: Abstract hint + no content type -> ERROR (cannot instantiate)
     * The helper returns the abstract class; caller must check instantiability.
     */
    @Test
    @DisplayName("T9: abstract hint with no content type - returns abstract (caller checks)")
    void abstractHintWithNoContentType() {
        EClass result = helper.resolveEffectiveType(abstractEntityClass, null);
        assertSame(abstractEntityClass, result);
        // Caller should check: abstractEntityClass.isAbstract() == true -> ERROR
        assertTrue(abstractEntityClass.isAbstract());
    }

    /**
     * Spec 17.5 T10: Interface hint + content impl -> Use content type (Person)
     */
    @Test
    @DisplayName("T10: interface hint with content implementation - use content type")
    void interfaceHintWithContentImplementation() {
        // Named is interface, Person implements Named (via AbstractEntity)
        EClass result = helper.resolveEffectiveType(namedInterface, personClass);
        assertSame(personClass, result);
    }

    /**
     * Spec 17.5 T11: Interface hint + no content type -> ERROR (cannot instantiate)
     * The helper returns the interface; caller must check instantiability.
     */
    @Test
    @DisplayName("T11: interface hint with no content type - returns interface (caller checks)")
    void interfaceHintWithNoContentType() {
        EClass result = helper.resolveEffectiveType(namedInterface, null);
        assertSame(namedInterface, result);
        // Caller should check: namedInterface.isInterface() == true -> ERROR
        assertTrue(namedInterface.isInterface());
    }

    /**
     * Verify isAbstract/isInterface for test classes loaded from ecore.
     */
    @Test
    @DisplayName("verify test model: abstract and interface flags")
    void verifyTestModelFlags() {
        assertFalse(personClass.isAbstract());
        assertFalse(personClass.isInterface());
        assertTrue(abstractEntityClass.isAbstract());
        assertFalse(abstractEntityClass.isInterface());
        assertTrue(namedInterface.isInterface());
        // Note: in EMF, interface implies abstract
        assertTrue(namedInterface.isAbstract());
    }

    /**
     * Verify inheritance hierarchy loaded correctly from ecore.
     */
    @Test
    @DisplayName("verify test model: inheritance hierarchy")
    void verifyTestModelHierarchy() {
        // Employee extends Person
        assertTrue(employeeClass.getESuperTypes().contains(personClass));
        // Person extends AbstractEntity
        assertTrue(personClass.getESuperTypes().contains(abstractEntityClass));
        // AbstractEntity extends Named (interface)
        assertTrue(abstractEntityClass.getESuperTypes().contains(namedInterface));
        // Address is unrelated
        assertFalse(addressClass.getESuperTypes().contains(personClass));
        assertFalse(addressClass.getESuperTypes().contains(abstractEntityClass));
    }
}
