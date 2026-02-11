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
package org.eclipse.fennec.codec.util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataService;

/**
 * Helper class for codec resource operations.
 * <p>
 * Provides utility methods for option resolution, type handling, and
 * configuration merging. Extracted for testability.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 2025-12-11
 */
public class CodecResourceHelper {

    private static final Logger LOGGER = Logger.getLogger(CodecResourceHelper.class.getName());

    /** Option key for specifying the root EClass during deserialization. */
    public static final String CODEC_ROOT_TYPE = "CODEC_ROOT_TYPE";

    private final MetadataService metadataService;

    /**
     * Creates a new helper with the given metadata service.
     *
     * @param metadataService the metadata service for lookups
     */
    public CodecResourceHelper(MetadataService metadataService) {
        this.metadataService = requireNonNull(metadataService, "metadataService must not be null");
    }

    /**
     * Resolves the root EClass from load options.
     * <p>
     * Supports both direct EClass reference and URI string.
     * </p>
     *
     * @param options the options map
     * @return the root EClass, or null if not specified or not resolvable
     */
    public EClass resolveRootEClass(Map<?, ?> options) {
        if (isNull(options)) {
            return null;
        }

        Object rootObject = options.get(CODEC_ROOT_TYPE);

        if (rootObject instanceof EClass eClass) {
            return eClass;
        }

        if (rootObject instanceof String uriString) {
            return resolveEClassFromUri(uriString);
        }

        return null;
    }

    /**
     * Resolves an EClass from a URI string.
     *
     * @param uriString the EClass URI (e.g., "http://example.org/1.0#//Person")
     * @return the resolved EClass, or null if not found
     */
    public EClass resolveEClassFromUri(String uriString) {
        if (isNull(uriString) || uriString.isEmpty()) {
            return null;
        }

        ClassMetadata metadata = metadataService.getClassMetadataByURI(uriString);
        if (nonNull(metadata)) {
            return metadata.getEClass();
        }

        LOGGER.warning(() -> "Could not resolve EClass from URI: " + uriString);
        return null;
    }

    /**
     * Checks if two EClasses are compatible (same or subtype relationship).
     * <p>
     * Used for type collision detection when both CODEC_ROOT_TYPE and
     * content type information are present.
     * </p>
     *
     * @param hint the expected type (from CODEC_ROOT_TYPE)
     * @param contentType the actual type (from content)
     * @return true if compatible (no collision), false if collision
     */
    public boolean isTypeCompatible(EClass hint, EClass contentType) {
        if (isNull(hint) || isNull(contentType)) {
            return true; // No collision if either is missing
        }

        // Same type
        if (hint.equals(contentType)) {
            return true;
        }

        // Content type is subtype of hint
        if (hint.isSuperTypeOf(contentType)) {
            return true;
        }

        // Collision - content type is different/unrelated
        return false;
    }

    /**
     * Resolves the effective EClass for deserialization considering both
     * CODEC_ROOT_TYPE hint and content type information.
     * <p>
     * Resolution rules:
     * <ul>
     *   <li>No collision (content type equals or is subtype of hint): use hint</li>
     *   <li>Collision (content type differs): warn and use content type</li>
     *   <li>Only hint provided: use hint</li>
     *   <li>Only content type provided: use content type</li>
     * </ul>
     * </p>
     *
     * @param hint the EClass from CODEC_ROOT_TYPE (may be null)
     * @param contentType the EClass from content type info (may be null)
     * @return the effective EClass to use, or null if neither is available
     */
    public EClass resolveEffectiveType(EClass hint, EClass contentType) {
        if (isNull(hint) && isNull(contentType)) {
            return null;
        }

        if (isNull(hint)) {
            return contentType;
        }

        if (isNull(contentType)) {
            return hint;
        }

        // Both present - check for collision
        if (isTypeCompatible(hint, contentType)) {
            // No collision - use the more specific type (contentType if it's a subtype)
            return contentType;
        }

        // Collision - warn and use content type
        LOGGER.warning(() -> String.format(
            "Type collision: CODEC_ROOT_TYPE=%s but content type=%s. Using content type.",
            hint.getName(), contentType.getName()));
        return contentType;
    }

    /**
     * Checks if an EClass can be instantiated.
     * <p>
     * An EClass is instantiable if it is not null, not abstract, and not an interface.
     * This check is required when resolving CODEC_ROOT_TYPE to ensure the
     * effective type can be used to create an EObject instance.
     * </p>
     *
     * @param eClass the EClass to check (may be null)
     * @return true if the EClass can be instantiated, false otherwise
     * @see <a href="docs/codec-v2-serialization-spec.md#154-codec_root_object-option">Spec 15.4: Abstract and Interface EClass Handling</a>
     */
    public boolean isInstantiable(EClass eClass) {
        if (isNull(eClass)) {
            return false;
        }
        return !eClass.isAbstract() && !eClass.isInterface();
    }

    /**
     * Gets the metadata service.
     *
     * @return the MetadataService
     */
    public MetadataService getMetadataService() {
        return metadataService;
    }
}
