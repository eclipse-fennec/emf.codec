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
package org.eclipse.fennec.codec.rest.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.fennec.codec.rest.jakartas.spi.ResourceSetProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Verifies that the codec publishes a default
 * {@link ResourceSetProvider} as an OSGi service and that it produces a
 * non-null {@code ResourceSet} for a request.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("ResourceSetProvider default")
public class ResourceSetProviderOSGiTest {

	@Test
	@DisplayName("default ResourceSetProvider is registered as an OSGi service")
	void defaultProviderIsPublished(@InjectService ResourceSetProvider provider) {
		assertNotNull(provider, "ResourceSetProvider should be available as an OSGi service");
	}

	@Test
	@DisplayName("default provider produces a non-null ResourceSet")
	void defaultProviderProducesResourceSet(@InjectService ResourceSetProvider provider) {
		// The default impl ignores the ContainerRequestContext, so null is fine here.
		assertNotNull(provider.getResourceSet(null));
	}
}
