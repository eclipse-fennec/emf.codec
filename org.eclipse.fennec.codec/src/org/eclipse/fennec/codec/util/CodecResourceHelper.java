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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.emf.osgi.model.metadata.ClassMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

/**
 * Helper class for codec resource operations.
 * <p>
 * Provides utility methods for option resolution, type handling, and
 * configuration merging. Extracted for testability.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
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

        ClassMetadata metadata = metadataService.getClassMetadataByURI(uriString).orElse(null);
        if (nonNull(metadata)) {
            return metadata.getEClass();
        }

        LOGGER.warning(() -> "Could not resolve EClass from URI: " + uriString);
        return null;
    }

    /**
     * Reads the optional {@code CODEC_ROOT_FINGERPRINT} load option (issue #54, A.2).
     *
     * @param options the options map
     * @return the fingerprint string, or {@code null} if absent/blank
     */
    public String rootFingerprint(Map<?, ?> options) {
        if (isNull(options)) {
            return null;
        }
        Object value = options.get(CodecOptions.CODEC_ROOT_FINGERPRINT);
        return value instanceof String s && !s.isEmpty() ? s : null;
    }

    /**
     * Fingerprint-aware root type resolution (issue #54, A.2/A.4).
     * <p>
     * When {@code CODEC_ROOT_FINGERPRINT} is present it selects a concrete package
     * version; a String {@code CODEC_ROOT_TYPE} is then resolved <b>by name within that
     * version</b> (bypassing the global, last-wins type-URI index), and an {@code EClass}
     * {@code CODEC_ROOT_TYPE} is <b>verified</b> against the selected version. Without a
     * fingerprint this delegates to {@link #resolveRootEClass(Map)} (unchanged behavior).
     * </p>
     * <p>Explicit-signal rules apply in every strictness mode:</p>
     * <ul>
     *   <li>unknown option fingerprint &rarr; {@link IOException}</li>
     *   <li>fingerprint vs. {@code EClass} root type from another version &rarr; {@link IOException}</li>
     *   <li>String root type whose class is absent from the selected version &rarr; {@link IOException}</li>
     * </ul>
     *
     * @param options the options map
     * @return the resolved root EClass, or {@code null} if none is determinable
     * @throws IOException on an unknown or conflicting fingerprint, or an unresolvable class
     */
    public EClass resolveRootType(Map<?, ?> options) throws IOException {
        if (isNull(options)) {
            return null;
        }

        String fingerprint = rootFingerprint(options);
        if (isNull(fingerprint)) {
            // A.3: a String root type whose nsURI has more than one registered version is
            // ambiguous without a fingerprint -> error listing candidates (both modes), never
            // a silent last-wins pick.
            Object rootObject = options.get(CODEC_ROOT_TYPE);
            if (rootObject instanceof String uriString && !uriString.isEmpty()) {
                int hash = uriString.indexOf('#');
                if (hash > 0) {
                    String nsURI = uriString.substring(0, hash);
                    // Null-guard: a real MetadataService never returns null here (empty list for
                    // an unknown nsURI), but Mockito mocks / alternate impls may — treat null as
                    // "no version info" so the ambiguity check is simply skipped (R1-safe).
                    List<PackageMetadata> versions = metadataService.getPackageMetadataVersions(nsURI);
                    if (versions != null && versions.size() > 1) {
                        throw new IOException(PackageResolver.ambiguityMessage(nsURI, versions));
                    }
                }
            }
            return resolveRootEClass(options);
        }

        PackageMetadata pkg = metadataService.getPackageMetadataByFingerprint(fingerprint).orElse(null);
        if (isNull(pkg)) {
            throw new IOException("Unknown root fingerprint: " + fingerprint);
        }

        Object rootObject = options.get(CODEC_ROOT_TYPE);
        if (rootObject instanceof EClass eClass) {
            // Checkable case: the explicit instance must belong to the named version.
            verifyPackageFingerprint(eClass.getEPackage(), fingerprint, CODEC_ROOT_TYPE);
            return eClass;
        }
        if (rootObject instanceof String uriString && !uriString.isEmpty()) {
            return resolveEClassInPackage(pkg, uriString);
        }
        // Fingerprint alone: the version is used for the context schema (resolveContextSchema).
        return null;
    }

    /**
     * Verifies that {@code ePackage}'s model fingerprint equals the given option
     * fingerprint (A.4 checkable case). A mismatch is a caller bug and fails in every mode.
     *
     * @param ePackage the package instance from an explicit root option
     * @param fingerprint the option fingerprint to check against
     * @param optionName the option name (for the error message)
     * @throws IOException if the fingerprints do not match
     */
    public void verifyPackageFingerprint(EPackage ePackage, String fingerprint, String optionName)
            throws IOException {
        if (isNull(ePackage)) {
            return;
        }
        PackageMetadata pm = metadataService.getPackageMetadata(ePackage).orElse(null);
        String actual = nonNull(pm) ? pm.getModelFingerprint() : null;
        if (!fingerprint.equals(actual)) {
            throw new IOException(String.format(
                "Root fingerprint %s does not match %s package %s (%s)",
                fingerprint, optionName, ePackage.getNsURI(), actual));
        }
    }

    /**
     * Resolves a class by name within a specific package version, bypassing the global
     * type-URI index. Accepts a bare class name, an EMF type URI
     * ({@code nsURI#//Name}), or a qualified name.
     */
    private EClass resolveEClassInPackage(PackageMetadata pkg, String typeString) throws IOException {
        String name = simpleTypeName(typeString);
        EPackage ePackage = pkg.getEPackage();
        EClassifier classifier = nonNull(ePackage) ? ePackage.getEClassifier(name) : null;
        if (classifier instanceof EClass eClass) {
            return eClass;
        }
        throw new IOException(String.format(
            "Root type '%s' (class '%s') not found in package version %s [%s]",
            typeString, name, nonNull(ePackage) ? ePackage.getNsURI() : "?", pkg.getModelFingerprint()));
    }

    /**
     * Extracts the simple classifier name from a bare name, an EMF type URI
     * ({@code nsURI#//Name} or {@code #//pkg/Name}), or a qualified name.
     */
    static String simpleTypeName(String typeString) {
        String s = typeString;
        int hash = s.indexOf('#');
        if (hash >= 0) {
            s = s.substring(hash + 1);
        }
        while (s.startsWith("/")) {
            s = s.substring(1);
        }
        int slash = s.lastIndexOf('/');
        if (slash >= 0) {
            s = s.substring(slash + 1);
        }
        int dot = s.lastIndexOf('.');
        if (dot >= 0) {
            s = s.substring(dot + 1);
        }
        return s;
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
