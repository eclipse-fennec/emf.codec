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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;

/**
 * Per-save record of which {@link EPackage} version has already been announced for each
 * {@code nsURI}, implementing the <b>first-touch</b> rule of the in-band fingerprint carrier
 * (issue #73, B.1).
 * <p>
 * The fingerprint is written sparsely rather than on every object: once a version is
 * established for an {@code nsURI}, every later object of that package is interpreted under
 * it, so repeating the value would be pure redundancy. A fingerprint is therefore due
 * </p>
 * <ol>
 *   <li>at the <b>first</b> occurrence of an {@code nsURI} — the root for the root's package,
 *       and every site where a new package enters the document (supertype substitution,
 *       reference or containment targets from another package); and</li>
 *   <li>at every site whose package <b>instance deviates</b> from the one already pinned for
 *       that {@code nsURI} — the mixed-version marker without which the deviation would be
 *       invisible.</li>
 * </ol>
 * <p>
 * The pin deliberately keeps the <b>first</b> instance seen and is never overwritten by a
 * deviating one. That is what makes alternating versions work: a reader pins the first
 * version it sees for an {@code nsURI}, so every object of any other version has to
 * self-identify — which is exactly what this class marks. Writer first-touch and reader
 * pinning are the same rule seen from both sides.
 * </p>
 * <p>
 * This class is <b>not thread-safe</b>; one instance is created per save operation.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-type.md#83-write-conservative-at-first-touch">Spec 06 §8.3</a>
 * @see PackageResolver the read-side counterpart holding the same pins
 */
public final class FingerprintPins {

    private final Map<String, EPackage> pins = new HashMap<>();

    /**
     * Reports whether a fingerprint is due for the given package and, if the package's
     * {@code nsURI} is seen for the first time, pins it.
     * <p>
     * Calling this method is what advances the first-touch state, so it must be called
     * exactly once per serialized object whose type context could carry the fingerprint.
     * </p>
     *
     * @param ePackage the package of the object being serialized, may be {@code null}
     * @return {@code true} if the fingerprint has to be written at this site
     */
    public boolean isDue(EPackage ePackage) {
        if (isNull(ePackage)) {
            return false;
        }
        String nsURI = ePackage.getNsURI();
        if (isNull(nsURI) || nsURI.isEmpty()) {
            return false;
        }
        EPackage pinned = pins.putIfAbsent(nsURI, ePackage);
        if (isNull(pinned)) {
            // First touch of this nsURI.
            return true;
        }
        // Already established: only a deviating instance needs the mixed-version marker.
        return pinned != ePackage;
    }

    /**
     * Returns the package instance pinned for an {@code nsURI}, or {@code null} if that
     * {@code nsURI} has not been touched yet.
     *
     * @param nsURI the namespace URI
     * @return the pinned package instance, or {@code null}
     */
    public EPackage pinnedVersion(String nsURI) {
        return isNull(nsURI) ? null : pins.get(nsURI);
    }
}
