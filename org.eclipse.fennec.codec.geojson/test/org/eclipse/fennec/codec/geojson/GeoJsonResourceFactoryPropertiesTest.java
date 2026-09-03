/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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
package org.eclipse.fennec.codec.geojson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.geojson.GeoJsonPackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The service properties a GeoJSON factory registers with (issue #168).
 * <p>
 * The REST message body reader/writer resolves the factory for a request through the content
 * type map alone, so a factory that registers only a file extension can never serve its media
 * type. GeoJSON has one: {@code application/geo+json} (RFC 7946), with the pre-RFC
 * {@code application/vnd.geo+json} still in use.
 * </p>
 */
@DisplayName("GeoJsonResourceFactory service properties")
class GeoJsonResourceFactoryPropertiesTest {

    @Test
    @DisplayName("registers the GeoJSON media type and its legacy alias as content types")
    void registersTheGeoJsonContentTypes() {
        Map<String, Object> properties = new GeoJsonResourceFactoryImpl().getServiceProperties();

        Object contentTypes = properties.get(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
        assertInstanceOf(Collection.class, contentTypes,
                "content types are multi-valued, like the CSV factory's");
        assertEquals(List.of("application/geo+json", "application/vnd.geo+json"), contentTypes);
    }

    @Test
    @DisplayName("keeps the file extension, configurator name and version")
    void keepsTheExistingProperties() {
        Map<String, Object> properties = new GeoJsonResourceFactoryImpl().getServiceProperties();

        assertEquals(GeoJsonPackage.eNAME, properties.get(EMFNamespaces.EMF_CONFIGURATOR_NAME));
        assertEquals("geojson", properties.get(EMFNamespaces.EMF_MODEL_FILE_EXT));
        assertEquals("1.0", properties.get(EMFNamespaces.EMF_MODEL_VERSION));
    }
}
