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
package org.eclipse.fennec.codec.constants;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * Reads the two root load options that answer to two keys each.
 * <p>
 * {@code CODEC_ROOT_TYPE} and {@code CODEC_ROOT_SCHEMA} historically grew two spellings:
 * the canonical dotted key from the spec ({@code codec.rootType}, {@code codec.rootSchema},
 * declared by {@link CodecOptions}) and the literal key that {@code CodecResource} exposes
 * ({@code "CODEC_ROOT_TYPE"}, {@code "CODEC_ROOT_SCHEMA"}). Both are supported and both are
 * read at every reading site — a caller may use either (issue #208). Going through this
 * class is what keeps that promise: a bare {@code options.get(key)} sees only one of the two.
 * </p>
 * <p>
 * Passing both keys with values that disagree is not a preference to be resolved by
 * precedence but two contradictory statements from the same caller, so it is an error in
 * every strictness mode — the same rule the fingerprint options follow (Spec 13 §2.9).
 * Values are compared by {@link Objects#equals}: the same {@code EClass} instance or the
 * same String under both keys is redundant but fine, an {@code EClass} under one key and a
 * String under the other is a conflict, because the codec cannot tell without resolving
 * whether the two name the same type.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/13-load-save-options.md">Spec 13 §2.1: Root Element Options</a>
 * @since 1.0
 */
public final class RootOptions {

    private RootOptions() {
        // Utility class - no instantiation
    }

    /**
     * Reads the root type option under either of its keys.
     *
     * @param options the load options (may be {@code null})
     * @return the option value ({@code EClass} or String, as the caller passed it), or
     *         {@code null} if neither key carries one
     * @throws IOException if both keys are present with values that disagree
     */
    public static Object rootType(Map<?, ?> options) throws IOException {
        return read(options, CodecOptions.CODEC_ROOT_TYPE, CodecOptions.CODEC_ROOT_TYPE_LITERAL);
    }

    /**
     * Reads the root schema option under either of its keys.
     *
     * @param options the load options (may be {@code null})
     * @return the option value ({@code EPackage} or nsURI String, as the caller passed it),
     *         or {@code null} if neither key carries one
     * @throws IOException if both keys are present with values that disagree
     */
    public static Object rootSchema(Map<?, ?> options) throws IOException {
        return read(options, CodecOptions.CODEC_ROOT_SCHEMA, CodecOptions.CODEC_ROOT_SCHEMA_LITERAL);
    }

    /**
     * Tells whether the caller set a root type under either key. Unlike
     * {@link #rootType(Map)} this does not judge a conflict — it answers the
     * "did the caller already decide the root type?" question that guards a default.
     *
     * @param options the load options (may be {@code null})
     * @return {@code true} if either key carries a non-null value
     */
    public static boolean hasRootType(Map<?, ?> options) {
        return contains(options, CodecOptions.CODEC_ROOT_TYPE, CodecOptions.CODEC_ROOT_TYPE_LITERAL);
    }

    /**
     * Tells whether the caller set a root schema under either key.
     *
     * @param options the load options (may be {@code null})
     * @return {@code true} if either key carries a non-null value
     */
    public static boolean hasRootSchema(Map<?, ?> options) {
        return contains(options, CodecOptions.CODEC_ROOT_SCHEMA, CodecOptions.CODEC_ROOT_SCHEMA_LITERAL);
    }

    private static Object read(Map<?, ?> options, String canonicalKey, String literalKey) throws IOException {
        if (options == null) {
            return null;
        }
        Object canonical = options.get(canonicalKey);
        Object literal = options.get(literalKey);

        if (canonical == null) {
            return literal;
        }
        if (literal == null || Objects.equals(canonical, literal)) {
            return canonical;
        }
        throw new IOException(String.format(
                "Contradictory root options: %s=%s but %s=%s. Both keys name the same option; "
                + "pass one of them, or pass the same value under both.",
                canonicalKey, describe(canonical), literalKey, describe(literal)));
    }

    private static boolean contains(Map<?, ?> options, String canonicalKey, String literalKey) {
        return options != null
                && (options.get(canonicalKey) != null || options.get(literalKey) != null);
    }

    /**
     * Names an option value the way a caller recognizes it: EMF's own {@code toString} on an
     * {@code EClass} is a debug dump, and the conflict message has to be readable.
     */
    private static String describe(Object value) {
        if (value instanceof EClass eClass) {
            EPackage ePackage = eClass.getEPackage();
            return ePackage == null ? eClass.getName() : ePackage.getNsURI() + "#//" + eClass.getName();
        }
        if (value instanceof EPackage ePackage) {
            return ePackage.getNsURI();
        }
        return String.valueOf(value);
    }
}
