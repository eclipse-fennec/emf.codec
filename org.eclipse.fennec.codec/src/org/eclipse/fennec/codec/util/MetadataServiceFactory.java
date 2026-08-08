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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;
import org.eclipse.fennec.emf.osgi.metadata.MetadataHandler;
import org.eclipse.fennec.emf.osgi.metadata.MetadataServices;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;

/**
 * Factory for creating {@link MetadataWhiteboard} instances configured for the codec.
 * <p>
 * This factory ensures that the {@link CodecAspectProvider} is registered
 * with the MetadataWhiteboard so that EAnnotations from Ecore models are
 * properly parsed into codec aspects.
 * </p>
 * <p>
 * Returns {@link MetadataWhiteboard} so callers can register packages.
 * Pass as {@link org.eclipse.fennec.emf.osgi.metadata.MetadataService MetadataService}
 * to consumers that only need read access (e.g., {@code CodecResource}).
 * </p>
 * <p>
 * This factory is the non-OSGi bootstrap only: it delegates to
 * {@link MetadataServices#createWhiteboard(MetadataHandler...)}, which installs the default
 * {@code FingerprintService}. In OSGi the whiteboard arrives as a service and DS wires both
 * the fingerprint service and every handler, so nothing here is needed.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
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
     * <p>
     * Optional {@link MetadataHandler} instances can be provided. They will be
     * registered on the whiteboard and automatically receive callbacks when
     * packages are registered or unregistered. For example, pass a
     * {@link org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService TypeDiscriminatorService}
     * to get an incrementally managed type discriminator instead of rebuilding
     * it on every save()/load() call.
     * </p>
     *
     * @param handlers optional metadata handlers to register on the whiteboard
     * @return a new MetadataWhiteboard configured for codec serialization
     */
    public static MetadataWhiteboard create(MetadataHandler... handlers) {
        List<MetadataHandler> all = new ArrayList<>();
        all.add(new CodecAspectProvider());
        if (handlers != null) {
            for (MetadataHandler handler : handlers) {
                if (handler != null) {
                    all.add(handler);
                }
            }
        }
        return MetadataServices.createWhiteboard(all.toArray(MetadataHandler[]::new));
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
        whiteboard.addMetadataHandler(new CodecAspectProvider());
        return whiteboard;
    }
}
