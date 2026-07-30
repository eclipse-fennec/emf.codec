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
package org.eclipse.fennec.codec.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecModule.Builder}.
 *
 * @see <a href="docs/codec-v2-spec/09-jackson-module.md">Spec 9: Jackson Module Integration</a>
 */
@DisplayName("CodecModule.Builder")
class CodecModuleBuilderTest {

    @Test
    @DisplayName("builder creates module with default resolver")
    void builderCreatesModuleWithDefaultResolver() {
        CodecModule module = CodecModule.builder().build();
        assertNotNull(module);
        assertNotNull(module.getResolver());
    }

    @Test
    @DisplayName("builder accepts custom module name")
    void builderAcceptsCustomModuleName() {
        CodecModule module = CodecModule.builder()
                .moduleName("custom-module")
                .build();
        assertEquals("custom-module", module.getModuleName());
    }

    @Test
    @DisplayName("builder rejects null module name")
    void builderRejectsNullModuleName() {
        assertThrows(NullPointerException.class, () -> CodecModule.builder().moduleName(null));
    }

    @Test
    @DisplayName("builder accepts custom resolver")
    void builderAcceptsCustomResolver() {
        ConfigurationResolver resolver = ConfigurationResolver.defaults();
        CodecModule module = CodecModule.builder()
                .resolver(resolver)
                .build();
        assertEquals(resolver, module.getResolver());
    }

    @Test
    @DisplayName("builder accepts MetadataService")
    void builderAcceptsMetadataService() {
        MetadataService metadataService = mock(MetadataService.class);
        CodecModule module = CodecModule.builder()
                .metadataService(metadataService)
                .build();
        assertEquals(metadataService, module.getMetadataService());
    }

    @Test
    @DisplayName("builder accepts custom CodecValueRegistry")
    void builderAcceptsCustomCodecValueRegistry() {
        CodecValueRegistry registry = new CodecValueRegistry();
        CodecModule module = CodecModule.builder()
                .valueRegistry(registry)
                .build();
        assertEquals(registry, module.getValueRegistry());
    }

    @Test
    @DisplayName("builder accepts global ignore features")
    void builderAcceptsGlobalIgnoreFeatures() {
        CodecModule module = CodecModule.builder()
                .globalIgnoreFeatures(List.of("createdAt", "updatedAt"))
                .build();
        assertEquals(2, module.getGlobalIgnoreFeatureNames().size());
        assertTrue(module.isGloballyIgnored("createdAt"));
        assertTrue(module.isGloballyIgnored("updatedAt"));
    }

    @Test
    @DisplayName("builder accepts smartCompression flag")
    void builderAcceptsSmartCompression() {
        CodecModule module = CodecModule.builder()
                .smartCompression(true)
                .build();
        assertTrue(module.isSmartCompression());
    }

    @Test
    @DisplayName("builder accepts sortPropertiesAlphabetically flag")
    void builderAcceptsSortPropertiesAlphabetically() {
        CodecModule module = CodecModule.builder()
                .sortPropertiesAlphabetically(true)
                .build();
        assertTrue(module.isSortPropertiesAlphabetically());
    }
}
