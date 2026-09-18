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
package org.eclipse.fennec.codec.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.fennec.codec.constants.CodecOptions;

/**
 * The option keys the codec answers to, so that one it does not answer to can be reported
 * rather than dropped (issue #220).
 * <p>
 * A key in the {@code codec.} namespace asserts that the codec owns it. When no reader knows
 * that spelling the value is discarded silently, and the result is indistinguishable from the
 * option never having been set - which is how {@code codec.serializeDefaults} stayed in
 * {@link CodecOptions} being read by nobody. Keys outside the namespace are a caller's own
 * business and are never judged.
 * </p>
 * <p>
 * The set is derived, not hand-written: a list maintained by hand is exactly the thing that
 * drifts from the constants it is supposed to mirror. It is the union of
 * </p>
 * <ul>
 *   <li>every {@link ConfigProperty} key,</li>
 *   <li>every {@code codec.}-prefixed constant declared on {@link CodecOptions}, which covers
 *       the runtime-only options that are not {@link ConfigProperty}s, and</li>
 *   <li>whatever {@link #register(String...)} has added.</li>
 * </ul>
 * <p>
 * A key carrying a further segment ({@code codec.<namespace>.<name>}, such as
 * {@code codec.ods.styleHeader}) belongs to a format bundle this one cannot see, so it is
 * accepted without inspection. Format bundles should prefer that namespace; the few legacy
 * keys that predate it are listed in {@link #LEGACY_FORMAT_KEYS}.
 * </p>
 *
 * @since 1.0
 */
public final class KnownOptionKeys {

    /**
     * Format option keys that live directly under {@code codec.} instead of under a format
     * namespace. They cannot be discovered from this bundle, and renaming them would break
     * callers, so they are named here.
     */
    private static final Set<String> LEGACY_FORMAT_KEYS = Set.of(
            // org.eclipse.fennec.codec.jsonschema, JsonSchemaResourceImpl
            "jsonschemaClassName");

    /** Short keys (no {@code codec.} prefix), derived once. */
    private static final Set<String> DERIVED = deriveKnownKeys();

    /** Short keys contributed at runtime by bundles this one does not know about. */
    private static final Set<String> REGISTERED = ConcurrentHashMap.newKeySet();

    /**
     * Beyond this edit distance a known key is not a plausible correction, and a suggestion
     * that is merely the least-bad match is worse than none.
     */
    private static final int MAX_SUGGESTION_DISTANCE = 2;

    private KnownOptionKeys() {
        // utility class
    }

    /**
     * Declares option keys owned by a bundle this one cannot see, so they are not reported as
     * unknown. Keys may be given prefixed or bare; re-registering is harmless.
     *
     * @param keys the option keys to accept, may be empty
     */
    public static void register(String... keys) {
        if (keys == null) {
            return;
        }
        for (String key : keys) {
            if (key != null && !key.isBlank()) {
                REGISTERED.add(strip(key.trim()));
            }
        }
    }

    /**
     * Whether the codec reads this key.
     * <p>
     * Anything outside the {@code codec.} namespace answers {@code true}: it is not the
     * codec's to judge. So does a namespaced format key, which another bundle owns.
     * </p>
     *
     * @param key the key as written in the property map, may be {@code null}
     * @return {@code true} when the key is read, or is none of this bundle's business
     */
    public static boolean isKnown(String key) {
        if (key == null || key.isBlank() || !key.startsWith(CodecOptions.CODEC_PREFIX)) {
            return true;
        }
        String shortKey = strip(key);
        if (shortKey.isEmpty() || shortKey.indexOf('.') >= 0) {
            // codec.<namespace>.<name>: owned by a format bundle, not visible from here.
            return true;
        }
        return DERIVED.contains(shortKey) || REGISTERED.contains(shortKey);
    }

    /**
     * The known key an unknown one was most likely meant to be.
     *
     * @param key the unknown key, may be {@code null}
     * @return the suggestion in its {@code codec.} prefixed form, or empty when no known key is
     *         close enough to be worth naming
     */
    public static Optional<String> suggest(String key) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        String shortKey = strip(key);
        if (shortKey.isEmpty()) {
            return Optional.empty();
        }

        // A short key tolerates fewer edits than a long one: at two edits, "idKey" is as close
        // to "idKeyMode" as to several others, and naming one of them would be a guess.
        int budget = Math.min(MAX_SUGGESTION_DISTANCE, Math.max(1, shortKey.length() / 4));

        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String candidate : candidates()) {
            int distance = distanceAtMost(shortKey, candidate, budget);
            if (distance >= 0 && distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }
        return Optional.ofNullable(best).map(k -> CodecOptions.CODEC_PREFIX + k);
    }

    private static Set<String> candidates() {
        if (REGISTERED.isEmpty()) {
            return DERIVED;
        }
        Set<String> all = new HashSet<>(DERIVED);
        all.addAll(REGISTERED);
        return all;
    }

    /** Property maps accept both the bare key and the {@code codec.} prefixed spelling. */
    private static String strip(String key) {
        return key.startsWith(CodecOptions.CODEC_PREFIX)
                ? key.substring(CodecOptions.CODEC_PREFIX.length())
                : key;
    }

    private static Set<String> deriveKnownKeys() {
        Set<String> keys = new HashSet<>(LEGACY_FORMAT_KEYS);
        for (ConfigProperty property : ConfigProperty.values()) {
            keys.add(property.getKey());
        }
        keys.addAll(codecOptionConstants());
        return Collections.unmodifiableSet(keys);
    }

    /**
     * The {@code codec.}-prefixed String constants on {@link CodecOptions}, read from the class
     * rather than copied, so a constant added there is known here without a second edit.
     */
    private static Set<String> codecOptionConstants() {
        Set<String> keys = new HashSet<>();
        for (Field field : CodecOptions.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)
                    || field.getType() != String.class) {
                continue;
            }
            try {
                Object value = field.get(null);
                if (value instanceof String key && key.startsWith(CodecOptions.CODEC_PREFIX)) {
                    keys.add(strip(key));
                }
            } catch (IllegalAccessException e) {
                // A public field of a public class; unreachable in practice, and a key missing
                // from the set only costs a spurious report, so there is nothing to escalate.
                continue;
            }
        }
        return keys;
    }

    /**
     * Levenshtein distance, abandoned once it cannot come in at or under {@code budget}.
     *
     * @return the distance, or {@code -1} when it exceeds the budget
     */
    private static int distanceAtMost(String a, String b, int budget) {
        if (Math.abs(a.length() - b.length()) > budget) {
            return -1;
        }
        int[] previous = new int[b.length() + 1];
        int[] current = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            int rowBest = current[0];
            for (int j = 1; j <= b.length(); j++) {
                int substitution = previous[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
                current[j] = Math.min(substitution, Math.min(previous[j] + 1, current[j - 1] + 1));
                rowBest = Math.min(rowBest, current[j]);
            }
            if (rowBest > budget) {
                return -1;
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        int distance = previous[b.length()];
        return distance <= budget ? distance : -1;
    }
}
