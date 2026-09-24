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
package org.eclipse.fennec.codec.geojson;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JacksonException;

/**
 * Malformed GeoJSON fails the load with an {@link IOException} (issue #225).
 * <p>
 * The reproduction from the issue: a caller following the {@code Resource.load} contract
 * catches {@code IOException}, and a truncated document escaped it as Jackson's unchecked
 * {@code UnexpectedEndOfInputException}.
 * </p>
 */
@DisplayName("Malformed GeoJSON input")
class GeoJsonMalformedInputTest {

    @Test
    @DisplayName("a truncated document fails with an IOException and an error on the resource")
    void truncatedDocumentThrowsIOException() {
        Resource resource = new GeoJsonResourceFactoryImpl().createResource(URI.createURI("x.geojson"));
        byte[] truncated = "{\"type\":".getBytes(StandardCharsets.UTF_8);

        IOException failure = assertThrows(IOException.class,
                () -> resource.load(new ByteArrayInputStream(truncated), null));

        assertInstanceOf(JacksonException.class, failure.getCause());
        assertFalse(resource.getErrors().isEmpty(), "the reason is on record as well");
    }
}
