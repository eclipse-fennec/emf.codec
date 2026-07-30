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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

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

    /**
     * How a fingerprint carried by the data stream related to what the load already knew
     * (issue #73, B.2).
     * <p>
     * Returned instead of decided internally, because the reaction depends on the strictness
     * mode and needs a diagnostic collector — both of which live with the caller, not here.
     * </p>
     */
    public enum StreamFingerprintOutcome {
        /** Resolved to a version; it is now pinned (or matched an existing inferred pin). */
        RESOLVED,
        /** Identical to the version the caller pinned — redundant but consistent, no diagnostic. */
        REDUNDANT,
        /**
         * The caller pinned a different version for this nsURI. The caller's choice wins: the
         * fingerprint is a directive, and whoever orchestrates the load is the closer authority.
         */
        OVERRIDDEN_BY_CALLER,
        /** Resolves to nothing — foreign data, older data, or a canonicalization-scheme bump. */
        UNKNOWN
    }

    /**
     * Outcome of applying a stream fingerprint, together with everything a caller needs to
     * phrase a diagnostic without repeating the lookups.
     *
     * @param outcome what happened
     * @param metadata the version to use, or {@code null} when nothing resolved
     * @param callerFingerprint the caller-pinned fingerprint when it differs, else {@code null}
     * @param firstReport whether this is the first time this stream fingerprint caused a
     *        diagnostic in this load — used to cap flooding on repetitive documents
     */
    public record StreamFingerprintResult(
            StreamFingerprintOutcome outcome,
            PackageMetadata metadata,
            String callerFingerprint,
            boolean firstReport) {
    }

    private final MetadataService metadataService;
    private final EPackage.Registry resourceSetRegistry;
    private final Map<String, PackageMetadata> pins = new HashMap<>();
    /** nsURIs whose version the caller decided explicitly (root option / rootFingerprint). */
    private final Set<String> callerPinnedNsUris = new HashSet<>();
    /** Stream fingerprints already reported, so one bad document does not flood diagnostics. */
    private final Set<String> reportedFingerprints = new HashSet<>();

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
        PackageMetadata pm = metadataService.getPackageMetadata(ePackage).orElse(null);
        if (nonNull(pm)) {
            pins.putIfAbsent(ePackage.getNsURI(), pm);
            callerPinnedNsUris.add(ePackage.getNsURI());
        }
    }

    /**
     * Applies a fingerprint carried by the data stream to an nsURI (issue #73, B.2).
     * <p>
     * Never throws and never degrades a signal silently: the outcome is reported so the caller
     * can react according to the strictness mode. Strictness governs tolerance towards
     * <b>data</b>, so the same stream fingerprint may be a warning in LENIENT and an error in
     * STRICT — that decision does not belong here.
     * </p>
     * <p>
     * The precedence encoded here is deliberately the opposite of the type hint's: a
     * fingerprint is a <b>directive</b> about which model world the data is read in, and a
     * caller who set one explicitly is the closer authority — a migration reader deliberately
     * re-reading old data against a chosen version must be able to overrule the document.
     * </p>
     *
     * @param nsURI the namespace URI the fingerprint applies to
     * @param fingerprint the fingerprint read from the document
     * @return the outcome, never {@code null}
     * @see <a href="docs/codec-v2-spec/13-load-save-options.md">Spec 13 §2.11 signal contract</a>
     */
    public StreamFingerprintResult applyStreamFingerprint(String nsURI, String fingerprint) {
        if (isNull(nsURI) || nsURI.isEmpty() || isNull(fingerprint) || fingerprint.isEmpty()) {
            return new StreamFingerprintResult(StreamFingerprintOutcome.UNKNOWN, null, null, false);
        }

        PackageMetadata fromStream = metadataService.getPackageMetadataByFingerprint(fingerprint).orElse(null);

        if (callerPinnedNsUris.contains(nsURI)) {
            PackageMetadata callerPin = pins.get(nsURI);
            String callerFingerprint = nonNull(callerPin) ? callerPin.getModelFingerprint() : null;
            if (fingerprint.equals(callerFingerprint)) {
                return new StreamFingerprintResult(
                        StreamFingerprintOutcome.REDUNDANT, callerPin, callerFingerprint, false);
            }
            return new StreamFingerprintResult(StreamFingerprintOutcome.OVERRIDDEN_BY_CALLER,
                    callerPin, callerFingerprint, firstReportOf(fingerprint));
        }

        if (isNull(fromStream)) {
            return new StreamFingerprintResult(
                    StreamFingerprintOutcome.UNKNOWN, null, null, firstReportOf(fingerprint));
        }

        // Pin the first version established for this nsURI; see resolvePackage for why a later
        // explicitly identified object must not move it.
        pins.putIfAbsent(nsURI, fromStream);
        return new StreamFingerprintResult(StreamFingerprintOutcome.RESOLVED, fromStream, null, false);
    }

    /**
     * Reports whether this fingerprint has not been reported yet in this load, marking it as
     * reported. Caps diagnostic flooding when a document repeats the same unresolvable value
     * on thousands of objects.
     */
    private boolean firstReportOf(String fingerprint) {
        return reportedFingerprints.add(fingerprint);
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

        // Explicit fingerprint short-circuits to a direct resolve.
        if (nonNull(fingerprint) && !fingerprint.isEmpty()) {
            PackageMetadata pm = metadataService.getPackageMetadataByFingerprint(fingerprint).orElse(null);
            if (isNull(pm)) {
                throw new IOException("Unknown fingerprint: " + fingerprint);
            }
            // The pin keeps the FIRST version established for this nsURI and is never moved by
            // a later, explicitly identified object. That mirrors the write side: a writer
            // marks every object deviating from the pin, but leaves objects that match the pin
            // unmarked. Moving the pin here would silently reinterpret those unmarked objects
            // as belonging to the deviating version.
            pins.putIfAbsent(nsURI, pm);
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
                PackageMetadata pm = metadataService.getPackageMetadata(ePackage).orElse(null);
                if (nonNull(pm)) {
                    pins.put(nsURI, pm);
                    return pm;
                }
            }
        }

        // Tier 3: MetadataService candidate query + count rule.
        List<PackageMetadata> versions = metadataService.getPackageMetadataVersions(nsURI);
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
    public static String ambiguityMessage(String nsURI, List<PackageMetadata> versions) {
        String fingerprints = versions.stream()
                .map(PackageMetadata::getModelFingerprint)
                .collect(Collectors.joining(", "));
        return String.format(
            "Ambiguous nsURI %s: %d versions registered [%s]; pass codec.rootFingerprint to select one",
            nsURI, versions.size(), fingerprints);
    }
}
