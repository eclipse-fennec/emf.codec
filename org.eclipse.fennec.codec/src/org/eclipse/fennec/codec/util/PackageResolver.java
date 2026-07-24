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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataService;

/**
 * Per-load resolver of an {@code nsURI} to a concrete {@link EPackage}/{@link PackageMetadata}
 * version, implementing the binding source order and the count-based candidate rule of
 * issue #54 (B.5 / A.3).
 * <p>
 * <b>Binding source order</b> for an nsURI without an explicit fingerprint:
 * </p>
 * <ol>
 *   <li><b>Pin</b> — a version already selected for this nsURI in this load is reused
 *       (consistency; no repeated lookup).</li>
 *   <li><b>ResourceSet package registry</b> — a caller-supplied concrete {@link EPackage}
 *       instance (instance-precise, unambiguous).</li>
 *   <li><b>MetadataService candidate query</b> — {@link MetadataService#getPackageMetadataVersions(String)}
 *       with the count rule: 0 &rarr; unknown to the service (fall through), exactly 1 &rarr;
 *       use it, &gt; 1 &rarr; error listing the candidate fingerprints (no trial, no
 *       auto-pick, in every strictness mode).</li>
 *   <li><b>{@link EPackage.Registry#INSTANCE}</b> — <b>only</b> for an nsURI the
 *       MetadataService does not know at all (true foreign / plain-EMF packages). For an
 *       nsURI the service knows, the global registry is never consulted.</li>
 * </ol>
 * <p>
 * An explicit fingerprint (from {@code codec.rootFingerprint}) short-circuits to a direct,
 * pinned resolve via {@link MetadataService#getPackageMetadataByFingerprint(String)}.
 * </p>
 * <p>
 * This class is <b>not thread-safe</b>; one instance is created per load operation.
 * </p>
 */
public final class PackageResolver {

    private final MetadataService metadataService;
    private final EPackage.Registry resourceSetRegistry;
    private final Map<String, PackageMetadata> pins = new HashMap<>();

    /**
     * @param metadataService the metadata service (required)
     * @param resourceSetRegistry the ResourceSet's package registry, or {@code null} if none
     */
    public PackageResolver(MetadataService metadataService, EPackage.Registry resourceSetRegistry) {
        this.metadataService = requireNonNull(metadataService, "metadataService must not be null");
        this.resourceSetRegistry = resourceSetRegistry;
    }

    /**
     * Returns the version pinned for the given nsURI in this load, or {@code null} if none is
     * pinned yet. Used by discriminator view composition (B.6) to scope to the selected version.
     *
     * @param nsURI the namespace URI
     * @return the pinned {@link PackageMetadata}, or {@code null}
     */
    public PackageMetadata pinnedVersion(String nsURI) {
        return nsURI != null ? pins.get(nsURI) : null;
    }

    /**
     * Seeds the pin for a concrete package instance already selected by the caller (e.g. an
     * {@code EClass}/{@code EPackage} root option). Subsequent resolutions of that nsURI in
     * this load reuse this exact version.
     *
     * @param ePackage the package instance, or {@code null} (no-op)
     */
    public void pin(EPackage ePackage) {
        if (isNull(ePackage)) {
            return;
        }
        PackageMetadata pm = metadataService.getPackageMetadata(ePackage);
        if (nonNull(pm)) {
            pins.putIfAbsent(ePackage.getNsURI(), pm);
        }
    }

    /**
     * Resolves an nsURI to a {@link PackageMetadata} version via the binding order (tiers
     * 1-3). Returns {@code null} when the MetadataService does not know the nsURI (the caller
     * applies tier 4).
     *
     * @param nsURI the namespace URI
     * @param fingerprint an explicit package fingerprint, or {@code null}
     * @return the resolved metadata, or {@code null} if unknown to the MetadataService
     * @throws IOException on an unknown fingerprint or an ambiguous nsURI (&gt; 1 candidate)
     */
    public PackageMetadata resolvePackage(String nsURI, String fingerprint) throws IOException {
        if (isNull(nsURI) || nsURI.isEmpty()) {
            return null;
        }

        // Explicit fingerprint short-circuits to a direct, pinned resolve.
        if (nonNull(fingerprint) && !fingerprint.isEmpty()) {
            PackageMetadata pm = metadataService.getPackageMetadataByFingerprint(fingerprint);
            if (isNull(pm)) {
                throw new IOException("Unknown fingerprint: " + fingerprint);
            }
            pins.put(nsURI, pm);
            return pm;
        }

        // Tier 1: pin.
        PackageMetadata pinned = pins.get(nsURI);
        if (nonNull(pinned)) {
            return pinned;
        }

        // Tier 2: ResourceSet package registry (instance-precise).
        if (nonNull(resourceSetRegistry)) {
            EPackage ePackage = resourceSetRegistry.getEPackage(nsURI);
            if (nonNull(ePackage)) {
                PackageMetadata pm = metadataService.getPackageMetadata(ePackage);
                if (nonNull(pm)) {
                    pins.put(nsURI, pm);
                    return pm;
                }
            }
        }

        // Tier 3: MetadataService candidate query + count rule.
        EList<PackageMetadata> versions = metadataService.getPackageMetadataVersions(nsURI);
        if (versions.size() == 1) {
            PackageMetadata pm = versions.get(0);
            pins.put(nsURI, pm);
            return pm;
        }
        if (versions.size() > 1) {
            throw new IOException(ambiguityMessage(nsURI, versions));
        }

        // 0 candidates: unknown to the MetadataService -> caller falls through to tier 4.
        return null;
    }

    /**
     * Resolves an nsURI to a concrete {@link EPackage} via the full binding order, including
     * tier 4 ({@link EPackage.Registry#INSTANCE}) for nsURIs the MetadataService does not know.
     *
     * @param nsURI the namespace URI
     * @param fingerprint an explicit package fingerprint, or {@code null}
     * @return the resolved package instance, or {@code null} if unresolvable
     * @throws IOException on an unknown fingerprint or an ambiguous nsURI
     */
    public EPackage resolveEPackage(String nsURI, String fingerprint) throws IOException {
        PackageMetadata pm = resolvePackage(nsURI, fingerprint);
        if (nonNull(pm)) {
            return pm.getEPackage();
        }
        // Tier 4: global registry only for nsURIs unknown to the MetadataService.
        return nonNull(nsURI) ? EPackage.Registry.INSTANCE.getEPackage(nsURI) : null;
    }

    /**
     * Resolves a full EMF type URI ({@code nsURI#//ClassName}) to an {@link EClass}, selecting
     * the correct package version via the binding order.
     *
     * @param typeUri the type URI
     * @param fingerprint an explicit package fingerprint, or {@code null}
     * @return the resolved EClass, or {@code null} if unresolvable
     * @throws IOException on an unknown fingerprint or an ambiguous nsURI
     */
    public EClass resolveEClassFromTypeUri(String typeUri, String fingerprint) throws IOException {
        if (isNull(typeUri) || typeUri.isEmpty()) {
            return null;
        }
        int hash = typeUri.indexOf('#');
        String nsURI = hash >= 0 ? typeUri.substring(0, hash) : typeUri;
        String className = CodecResourceHelper.simpleTypeName(typeUri);
        EPackage ePackage = resolveEPackage(nsURI, fingerprint);
        if (isNull(ePackage)) {
            return null;
        }
        EClassifier classifier = ePackage.getEClassifier(className);
        return classifier instanceof EClass eClass ? eClass : null;
    }

    /**
     * Builds the standard A.3 ambiguity error message listing the candidate fingerprints.
     * Shared by all resolution sites so the message is uniform.
     */
    public static String ambiguityMessage(String nsURI, EList<PackageMetadata> versions) {
        String fingerprints = versions.stream()
                .map(PackageMetadata::getModelFingerprint)
                .collect(Collectors.joining(", "));
        return String.format(
            "Ambiguous nsURI %s: %d versions registered [%s]; pass codec.rootFingerprint to select one",
            nsURI, versions.size(), fingerprints);
    }
}
