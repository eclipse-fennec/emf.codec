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
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The factory's default load and save options reach the resources it creates (issue #324).
 */
@DisplayName("CodecResourceFactory default options")
class CodecResourceFactoryDefaultOptionsTest {

    @Test
    @DisplayName("are the default options of each created resource")
    void reachTheResource() {
        CodecResourceFactory factory = new CodecResourceFactory(MetadataServiceFactory.create());
        factory.setDefaultLoadOptions(new HashMap<>(Map.of("codec.a", 1)));
        factory.setDefaultSaveOptions(new HashMap<>(Map.of("codec.b", 2)));

        CodecResource resource = (CodecResource) factory.createResource(URI.createURI("test://a.json"));

        assertEquals(Map.of("codec.a", 1), resource.getDefaultLoadOptions());
        assertEquals(Map.of("codec.b", 2), resource.getDefaultSaveOptions());
    }

    @Test
    @DisplayName("are copied, so changing a resource's defaults leaves the factory alone")
    void copied() {
        CodecResourceFactory factory = new CodecResourceFactory(MetadataServiceFactory.create());
        factory.setDefaultSaveOptions(new HashMap<>(Map.of("codec.b", 2)));

        CodecResource resource = (CodecResource) factory.createResource(URI.createURI("test://a.json"));
        resource.getDefaultSaveOptions().put("codec.c", 3);

        assertEquals(Map.of("codec.b", 2), factory.getDefaultSaveOptions());
        assertTrue(((CodecResource) factory.createResource(URI.createURI("test://b.json")))
                .getDefaultLoadOptions().isEmpty());
    }
}
