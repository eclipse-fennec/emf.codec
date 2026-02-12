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
package org.eclipse.fennec.codec.jackson;

import static org.mockito.Mockito.mock;

import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for {@link CodecJsonReadContext} tests.
 * <p>
 * Provides common setup for creating a root context with a mocked metadata service.
 * </p>
 */
abstract class CodecJsonReadContextTestBase {

    protected MetadataService metadataService;
    protected EffectiveCodecConfig effectiveConfig;
    protected CodecJsonReadContext context;

    @BeforeEach
    void setUp() {
        metadataService = mock(MetadataService.class);
        effectiveConfig = createTestEffectiveConfig(metadataService);
        context = CodecJsonReadContext.createRootContext(null, effectiveConfig);
    }

    /**
     * Creates an EffectiveCodecConfig for testing with the given MetadataService.
     */
    protected static EffectiveCodecConfig createTestEffectiveConfig(MetadataService metadataService) {
        return EffectiveCodecConfig.builder()
                .resolver(ConfigurationResolver.defaults())
                .diagnostics(new DiagnosticCollector())
                .metadataService(metadataService)
                .build();
    }
}
