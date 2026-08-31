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
package org.eclipse.fennec.codec.jsonschema.v2;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.codec.jsonschema.v2.value.EClassValueWriter;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The non-OSGi registration path keeps working (issue #186, AC 6).
 * <p>
 * Issue #147 added {@code registerDefaultValueHandlers} so the handlers can be wired without a
 * framework, and issue #186 restored their {@code @Component} annotations so they are reachable
 * as services again. Both paths have to coexist: the annotation is inert outside OSGi, and this
 * static path is what a plain-Java caller has.
 * </p>
 * <p>
 * Nothing here asserted anything before #186 - the path was untested, which is why it is easy
 * to break while fixing the other one.
 * </p>
 */
@DisplayName("Default JSON Schema value handler registration")
class DefaultValueHandlerRegistrationTest {

    @Test
    @DisplayName("all four handlers land in a fresh registry")
    void allFourHandlersAreRegistered() {
        CodecValueRegistry registry = new CodecValueRegistry();

        JsonSchemaResourceFactoryImpl.registerDefaultValueHandlers(registry);

        assertTrue(registry.hasWriter("eClassToJsonSchema"));
        assertTrue(registry.hasWriter("ePackageToJsonSchema"));
        assertTrue(registry.hasReader("jsonSchemaToEClass"));
        assertTrue(registry.hasReader("jsonSchemaToEPackage"));
    }

    @Test
    @DisplayName("a handler registered beforehand is left alone")
    void anAlreadyRegisteredHandlerWins() {
        // The documented contract: registerDefaultValueHandlers only fills gaps, so a caller
        // that installed its own handler before the call keeps it.
        CodecValueRegistry registry = new CodecValueRegistry();
        ReferenceValueWriter<EClass> mine = new NamedEClassWriter();
        registry.register(mine);

        JsonSchemaResourceFactoryImpl.registerDefaultValueHandlers(registry);

        assertSame(mine, registry.getWriter("eClassToJsonSchema").orElseThrow(),
                "the default must not overwrite a handler the caller already chose");
    }

    /** A stand-in carrying the same name as {@link EClassValueWriter}. */
    private static final class NamedEClassWriter implements ReferenceValueWriter<EClass> {

        @Override
        public String getName() {
            return "eClassToJsonSchema";
        }

        @Override
        public boolean canHandle(org.eclipse.emf.ecore.EReference feature) {
            return true;
        }

        @Override
        public void write(EClass value, org.eclipse.emf.ecore.EReference feature,
                CodecWriterContext context) {
            // never called: this test only asks which instance the registry holds
        }
    }
}
