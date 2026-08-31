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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The non-OSGi registration path keeps working (issue #186, AC 6).
 * <p>
 * The counterpart of the JSON Schema test of the same name. Issue #147 added
 * {@code registerDefaultValueHandlers} for callers without a framework, issue #186 restored the
 * {@code @Component} annotations so the handlers are reachable as services again, and both have
 * to keep working - the annotation is inert outside OSGi, and this is the plain-Java path.
 * </p>
 */
@DisplayName("Default OpenAPI value handler registration")
class DefaultValueHandlerRegistrationTest {

    @Test
    @DisplayName("the handlers an OpenAPI document needs land in a fresh registry")
    void handlersAreRegistered() {
        CodecValueRegistry registry = new CodecValueRegistry();

        OpenApiResourceFactoryImpl.registerDefaultValueHandlers(registry);

        assertTrue(registry.hasWriter("ePackageToOpenApiSchemas"));
        assertTrue(registry.hasWriter("securityRequirement"));
        assertTrue(registry.hasReader("operation"));
        assertTrue(registry.hasReader("securityRequirement"));
        assertTrue(registry.hasReader("jsonSchemaToEPackage"),
                "the OpenAPI factory also installs the JSON Schema EPackage reader");
    }

    @Test
    @DisplayName("a handler registered beforehand is left alone")
    void anAlreadyRegisteredHandlerWins() {
        CodecValueRegistry registry = new CodecValueRegistry();
        ReferenceValueWriter<EPackage> mine = new NamedSchemasWriter();
        registry.register(mine);

        OpenApiResourceFactoryImpl.registerDefaultValueHandlers(registry);

        assertSame(mine, registry.getWriter("ePackageToOpenApiSchemas").orElseThrow(),
                "the default must not overwrite a handler the caller already chose");
    }

    /** A stand-in carrying the same name as {@link org.eclipse.fennec.codec.openapi.value.OpenApiSchemasValueWriter}. */
    private static final class NamedSchemasWriter implements ReferenceValueWriter<EPackage> {

        @Override
        public String getName() {
            return "ePackageToOpenApiSchemas";
        }

        @Override
        public boolean canHandle(EReference feature) {
            return true;
        }

        @Override
        public void write(EPackage value, EReference feature, CodecWriterContext context) {
            // never called: this test only asks which instance the registry holds
        }
    }
}
