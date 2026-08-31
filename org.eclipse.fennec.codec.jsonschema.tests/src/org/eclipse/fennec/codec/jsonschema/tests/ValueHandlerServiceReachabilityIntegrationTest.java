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
package org.eclipse.fennec.codec.jsonschema.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * The JSON Schema value handlers must be reachable as services (issue #186).
 * <p>
 * A handler is referenced by name from a model annotation - {@code valueWriterName=
 * "eClassToJsonSchema"} - and that name is resolved against the <b>shared</b> registry, the one
 * {@code CodecValueRegistryComponent} builds from every {@code CodecValueWriter} and
 * {@code CodecValueReader} service in the framework. So a handler that is not a service cannot
 * be found from an ordinary {@code application/json} resource, however well it works inside its
 * own factory.
 * </p>
 * <p>
 * Issue #147 turned these eight handlers into plain objects that only their own factory
 * registries instantiate, to stop plain objects registered into the shared singleton from
 * outliving the bundle that provided them. That concern is real, and it is what the registry
 * component's {@code DYNAMIC} references and {@code unbind} methods already handle - a service
 * leaves with its bundle. What was lost instead was reachability.
 * </p>
 * <p>
 * This test registers nothing. That is the whole point: the existing round-trip test hands the
 * handlers to the registry itself, which is why it kept passing through the regression - it
 * proves the handlers <em>work</em>, never that they can be <em>found</em>.
 * </p>
 */
@DisplayName("JSON Schema value handlers are reachable as services")
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
class ValueHandlerServiceReachabilityIntegrationTest {

    @InjectService
    CodecValueRegistry sharedRegistry;

    @ParameterizedTest
    @ValueSource(strings = {"eClassToJsonSchema", "ePackageToJsonSchema"})
    @DisplayName("the writers resolve by name without being registered by hand")
    void writersAreReachable(String name) {
        assertTrue(sharedRegistry.hasWriter(name),
                "a model annotation naming '" + name + "' has to resolve against the shared"
                        + " registry, which is built from services");
    }

    @ParameterizedTest
    @ValueSource(strings = {"jsonSchemaToEClass", "jsonSchemaToEPackage"})
    @DisplayName("the readers resolve by name without being registered by hand")
    void readersAreReachable(String name) {
        assertTrue(sharedRegistry.hasReader(name),
                "a model annotation naming '" + name + "' has to resolve against the shared"
                        + " registry, which is built from services");
    }

    @Test
    @DisplayName("the injected registry is the shared one, not a factory's copy")
    void theRegistryIsTheSharedOne() {
        // Guards the premise of the tests above: a factory hands its resource a copy, so a
        // reachability claim only means something about the registry published as a service.
        assertTrue(sharedRegistry.getClass().getSimpleName().contains("Component")
                        || CodecValueRegistry.class.equals(sharedRegistry.getClass()),
                "expected the registry published as a service, got: " + sharedRegistry.getClass());
    }
}
