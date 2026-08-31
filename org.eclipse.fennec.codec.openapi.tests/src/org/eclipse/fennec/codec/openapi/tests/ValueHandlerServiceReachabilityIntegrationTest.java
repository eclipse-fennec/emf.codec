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
package org.eclipse.fennec.codec.openapi.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * The OpenAPI value handlers must be reachable as services (issue #186).
 * <p>
 * The counterpart of the JSON Schema test of the same name, and the reason this bundle exists:
 * the four OpenAPI handlers lost their service registrations in issue #147 along with the JSON
 * Schema ones, and had no OSGi test coverage at all to notice.
 * </p>
 * <p>
 * Nothing is registered here by hand. A handler named from a model annotation is resolved
 * against the shared registry, which {@code CodecValueRegistryComponent} builds from the
 * {@code CodecValueWriter} and {@code CodecValueReader} services in the framework - so being a
 * service is what makes a handler findable outside its own factory.
 * </p>
 */
@DisplayName("OpenAPI value handlers are reachable as services")
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
class ValueHandlerServiceReachabilityIntegrationTest {

    @InjectService
    CodecValueRegistry sharedRegistry;

    @ParameterizedTest
    @ValueSource(strings = {"ePackageToOpenApiSchemas", "securityRequirement"})
    @DisplayName("the writers resolve by name without being registered by hand")
    void writersAreReachable(String name) {
        assertTrue(sharedRegistry.hasWriter(name),
                "a model annotation naming '" + name + "' has to resolve against the shared"
                        + " registry, which is built from services");
    }

    @ParameterizedTest
    @ValueSource(strings = {"operation", "securityRequirement"})
    @DisplayName("the readers resolve by name without being registered by hand")
    void readersAreReachable(String name) {
        assertTrue(sharedRegistry.hasReader(name),
                "a model annotation naming '" + name + "' has to resolve against the shared"
                        + " registry, which is built from services");
    }
}
