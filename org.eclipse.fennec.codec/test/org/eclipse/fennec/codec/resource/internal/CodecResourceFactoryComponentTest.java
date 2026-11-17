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
package org.eclipse.fennec.codec.resource.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The component hands its {@code codec.jackson.*} configuration to every resource it creates
 * as default load and save options (issue #324, spec 13-load-save-options.md §11.1).
 */
@DisplayName("CodecResourceFactoryComponent")
class CodecResourceFactoryComponentTest {

    private static final String INDENT = "codec.jackson.SerializationFeature.INDENT_OUTPUT";
    private static final String COMMENTS = "codec.jackson.JsonReadFeature.ALLOW_JAVA_COMMENTS";

    @Test
    @DisplayName("codec.jackson properties become the resource's default options")
    void jacksonPropertiesBecomeDefaults() {
        CodecResourceFactoryComponent component = new CodecResourceFactoryComponent(
                MetadataServiceFactory.create(),
                Map.of(INDENT, true, COMMENTS, "true", "component.name", "CodecResourceFactory"));

        CodecResource resource = (CodecResource) component.createResource(URI.createURI("test://a.json"));

        Map<Object, Object> expected = Map.of(INDENT, true, COMMENTS, "true");
        assertEquals(expected, resource.getDefaultSaveOptions());
        assertEquals(expected, resource.getDefaultLoadOptions());
    }

    @Test
    @DisplayName("without codec.jackson properties the defaults stay empty")
    void noJacksonProperties() {
        CodecResourceFactoryComponent component = new CodecResourceFactoryComponent(
                MetadataServiceFactory.create(), Map.of("component.name", "CodecResourceFactory"));

        CodecResource resource = (CodecResource) component.createResource(URI.createURI("test://a.json"));

        assertTrue(resource.getDefaultSaveOptions().isEmpty());
        assertTrue(resource.getDefaultLoadOptions().isEmpty());
    }
}
