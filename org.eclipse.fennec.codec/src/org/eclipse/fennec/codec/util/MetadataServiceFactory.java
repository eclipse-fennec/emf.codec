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
package org.eclipse.fennec.codec.util;

import static java.util.Objects.requireNonNull;

import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.service.MetadataServiceImpl;

/**
 * Factory for creating {@link MetadataWhiteboard} instances configured for the codec.
 * <p>
 * This factory ensures that the {@link CodecAspectProvider} is registered
 * with the MetadataWhiteboard so that EAnnotations from Ecore models are
 * properly parsed into codec aspects.
 * </p>
 * <p>
 * Returns {@link MetadataWhiteboard} so callers can register packages.
 * Pass as {@link org.eclipse.fennec.model.metadata.api.MetadataService MetadataService}
 * to consumers that only need read access (e.g., {@code CodecResource}).
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2025-12-17
 */
public final class MetadataServiceFactory {

    private MetadataServiceFactory() {
        // Utility class
    }

    /**
     * Creates a new MetadataWhiteboard with the CodecAspectProvider registered.
     * <p>
     * This is the recommended way to create a MetadataWhiteboard for the codec
     * when not running in an OSGi environment. In OSGi, the MetadataWhiteboard
     * is typically provided via DS and the CodecAspectProvider is registered
     * separately.
     * </p>
     *
     * @return a new MetadataWhiteboard configured for codec serialization
     */
    @SuppressWarnings("restriction")
    public static MetadataWhiteboard create() {
        MetadataServiceImpl service = new MetadataServiceImpl();
        service.registerAspectProvider(new CodecAspectProvider());
        return service;
    }

    /**
     * Registers the CodecAspectProvider with an existing MetadataWhiteboard.
     * <p>
     * Use this method when you have an existing MetadataWhiteboard and need
     * to add codec aspect support to it.
     * </p>
     *
     * @param whiteboard the MetadataWhiteboard to configure
     * @return the same MetadataWhiteboard for chaining
     */
    public static MetadataWhiteboard configureForCodec(MetadataWhiteboard whiteboard) {
        requireNonNull(whiteboard, "whiteboard must not be null");
        whiteboard.registerAspectProvider(new CodecAspectProvider());
        return whiteboard;
    }
}
