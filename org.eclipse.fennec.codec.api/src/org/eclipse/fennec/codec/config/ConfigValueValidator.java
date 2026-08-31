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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Finds configured values that the config layer cannot consume (issue #174).
 * <p>
 * Every conversion in {@link ConfigMergeHelper} falls back when a value does not parse, and a
 * fallback is invisible: a codec configured with {@code typeStrategy="NOPE"} behaves exactly
 * like one where nobody wrote the setting at all. That is indistinguishable from a setting that
 * is not implemented, which is a bad thing to have to tell apart by reading source.
 * </p>
 * <p>
 * This validator answers one question per configured key - would this value be dropped? - and
 * says so once, naming the property, the offending value and the legal values. It deliberately
 * does <b>not</b> report unknown keys: a property map legitimately carries runtime options that
 * are not {@link ConfigProperty} keys, and telling those apart is the caller's job.
 * </p>
 */
final class ConfigValueValidator {

    /** The nested maps that carry a scope of their own rather than a value. */
    private static final List<ConfigProperty> SCOPED_MAPS = List.of(
            ConfigProperty.ECLASS_CONFIG,
            ConfigProperty.EREFERENCE_CONFIG,
            ConfigProperty.EATTRIBUTE_CONFIG);

    /** A scope nests at most twice (class then feature); beyond that it is not our map. */
    private static final int MAX_SCOPE_DEPTH = 3;

    private ConfigValueValidator() {
        // utility class
    }

    /**
     * Collects a message for every value in {@code source} that would be silently dropped.
     *
     * @param source the property map of one configuration source, may be {@code null}
     * @param sourceName how to name that source in a message, e.g. "resource properties"
     * @return the messages, empty when every configured value is usable
     */
    static List<String> findUnusableValues(Map<String, Object> source, String sourceName) {
        List<String> problems = new ArrayList<>();
        collect(source, sourceName, "", problems, 0);
        return problems;
    }

    /**
     * Walks one property map, judging the values of keys it recognises and descending into the
     * nested maps that carry a scope.
     * <p>
     * Everything here is defensive by necessity: a property map also carries runtime options
     * whose values are arbitrary objects - maps keyed by {@code EPackage}, reader instances,
     * handlers - so a key is only judged when it names a {@link ConfigProperty}, and a nested
     * map is only descended into when its keys are Strings, which is what a property map is.
     * </p>
     *
     * @param depth guards against a self-referencing option map; scopes nest twice at most
     */
    private static void collect(Map<?, ?> source, String sourceName, String scope,
            List<String> problems, int depth) {
        if (source == null || source.isEmpty() || depth > MAX_SCOPE_DEPTH) {
            return;
        }
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (!(entry.getKey() instanceof String key)) {
                continue;
            }
            Object value = entry.getValue();

            if (isScopedMap(key)) {
                // Instance-keyed config: { eReferenceConfig: { <EReference> -> { ... } } }.
                collectFromScopedMap(value, sourceName, problems, depth + 1);
                continue;
            }

            ConfigProperty property = ConfigProperty.byKey(strip(key));
            if (property == null) {
                // A nested map under a class or feature name (the name-keyed patterns), or a
                // runtime option that is not a ConfigProperty at all. Descend into the former;
                // the latter has no property values to judge.
                if (value instanceof Map<?, ?> nested) {
                    collect(nested, sourceName, scopeOf(scope, key), problems, depth + 1);
                }
                continue;
            }
            if (!property.accepts(value)) {
                problems.add(describe(property, value, sourceName, scope));
            }
        }
    }

    private static void collectFromScopedMap(Object value, String sourceName,
            List<String> problems, int depth) {
        if (!(value instanceof Map<?, ?> byInstance)) {
            return;
        }
        for (Map.Entry<?, ?> entry : byInstance.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> nested) {
                collect(nested, sourceName, nameOf(entry.getKey()), problems, depth);
            }
        }
    }

    private static String describe(ConfigProperty property, Object value, String sourceName,
            String scope) {
        StringBuilder message = new StringBuilder("Invalid value '")
                .append(value)
                .append("' for config property '")
                .append(property.getKey())
                .append("'");
        if (!scope.isEmpty()) {
            message.append(" on ").append(scope);
        }
        message.append(" (").append(sourceName).append(")");

        List<String> legal = property.getLegalValues();
        if (!legal.isEmpty()) {
            message.append("; legal values are ").append(legal);
        } else if (property.getType() == Integer.class) {
            message.append("; expected an integer");
        }

        message.append(". The value is ignored, so a wider scope or the default (")
                .append(String.valueOf(property.<Object>getDefaultValue()))
                .append(") applies.");
        return message.toString();
    }

    private static boolean isScopedMap(String key) {
        String stripped = strip(key);
        return SCOPED_MAPS.stream().anyMatch(property -> property.getKey().equals(stripped));
    }

    /** Property maps accept both the bare key and the {@code codec.} prefixed spelling. */
    private static String strip(String key) {
        return key != null && key.startsWith("codec.") ? key.substring("codec.".length()) : key;
    }

    private static String scopeOf(String outer, String key) {
        return outer.isEmpty() ? key : outer + "." + key;
    }

    private static String nameOf(Object instance) {
        if (instance instanceof org.eclipse.emf.ecore.ENamedElement named) {
            return named.getName();
        }
        return String.valueOf(instance);
    }
}
