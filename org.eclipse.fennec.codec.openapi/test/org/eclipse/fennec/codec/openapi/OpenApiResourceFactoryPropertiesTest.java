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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The service properties an OpenAPI factory registers with (issue #268).
 * <p>
 * The REST message body reader/writer resolves the factory for a request through the content
 * type map alone, so a factory that registers only a file extension can never serve its media
 * type. OpenAPI JSON documents have one: {@code application/vnd.oai.openapi+json}.
 * </p>
 */
@DisplayName("OpenApiResourceFactory service properties")
class OpenApiResourceFactoryPropertiesTest {

    @Test
    @DisplayName("registers the OpenAPI JSON media type as content type")
    void registersTheOpenApiContentType() {
        Map<String, Object> properties = new OpenApiResourceFactoryImpl().getServiceProperties();

        assertEquals("application/vnd.oai.openapi+json", OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_JSON);
        assertEquals(OpenApiResourceFactoryImpl.CONTENT_TYPE_OPENAPI_JSON,
                properties.get(EMFNamespaces.EMF_MODEL_CONTENT_TYPE));
    }

    @Test
    @DisplayName("keeps the file extension and names the configurator the capability announces")
    void keepsTheFileExtensionAndConfiguratorName() {
        Map<String, Object> properties = new OpenApiResourceFactoryImpl().getServiceProperties();

        assertEquals("FennecCodecOpenApi", properties.get(EMFNamespaces.EMF_CONFIGURATOR_NAME));
        assertEquals("openapi", properties.get(EMFNamespaces.EMF_MODEL_FILE_EXT));
        assertEquals("1.0", properties.get(EMFNamespaces.EMF_MODEL_VERSION));
    }
}
