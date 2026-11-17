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
package org.eclipse.fennec.codec.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.JsonFactoryBuilder;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DatatypeFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.cfg.JsonNodeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * The Jackson features of one load or save, read from its
 * {@code codec.jackson.<FeatureType>.<FEATURE_NAME>} options (issue #324, spec
 * 13-load-save-options.md §11).
 * <p>
 * Features the codec sets itself are codec-owned: an option for one is ignored and reported
 * unless {@link CodecOptions#CODEC_JACKSON_ALLOW_OVERRIDE} is set.
 * </p>
 */
final class JacksonFeatureOptions {

    private static final String SOURCE = "JacksonFeatureOptions";

    /** The feature types a {@code codec.jackson.} key may name, by their simple name. */
    private static final Map<String, Class<? extends Enum<?>>> TYPES = types();

    /** The features the codec sets itself, and the state it sets them to (§11.2). */
    private static final Map<Enum<?>, Boolean> CODEC_OWNED =
            Map.of(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, Boolean.FALSE);

    private static final JacksonFeatureOptions NONE =
            new JacksonFeatureOptions(Collections.emptyMap(), false, List.of(), List.of());

    private final Map<Enum<?>, Boolean> features;
    private final boolean allowOverride;
    private final List<String> warnings;
    private final List<String> infos;

    private JacksonFeatureOptions(Map<Enum<?>, Boolean> features, boolean allowOverride,
            List<String> warnings, List<String> infos) {
        this.features = features;
        this.allowOverride = allowOverride;
        this.warnings = warnings;
        this.infos = infos;
    }

    /**
     * Reads the {@code codec.jackson.} options.
     *
     * @param options the merged load/save options, may be {@code null}
     * @return the features, never {@code null}
     */
    static JacksonFeatureOptions from(Map<?, ?> options) {
        if (options == null || options.isEmpty()) {
            return NONE;
        }
        List<String> warnings = new ArrayList<>();
        List<String> infos = new ArrayList<>();
        Map<Enum<?>, Boolean> features = new LinkedHashMap<>();
        Map<Enum<?>, String> keys = new LinkedHashMap<>();
        Boolean allowOverride = Boolean.FALSE;

        // Sorted, so the diagnostics come in the same order on every run
        Map<String, Object> jacksonOptions = new TreeMap<>();
        options.forEach((key, value) -> {
            if (key instanceof String k && k.startsWith(CodecOptions.CODEC_JACKSON_PREFIX)) {
                jacksonOptions.put(k, value);
            }
        });

        for (Map.Entry<String, Object> option : jacksonOptions.entrySet()) {
            String key = option.getKey();
            Boolean state = parseBoolean(option.getValue());
            if (state == null) {
                warnings.add("Invalid value '" + option.getValue() + "' for option '" + key
                        + "'; legal values are [true, false]. The option is ignored.");
                continue;
            }
            if (key.equals(CodecOptions.CODEC_JACKSON_ALLOW_OVERRIDE)) {
                allowOverride = state;
                continue;
            }
            Enum<?> feature = resolve(key, warnings);
            if (feature != null) {
                features.put(feature, state);
                keys.put(feature, key);
            }
        }

        for (Map.Entry<Enum<?>, Boolean> owned : CODEC_OWNED.entrySet()) {
            Enum<?> feature = owned.getKey();
            if (!features.containsKey(feature)) {
                continue;
            }
            String name = nameOf(feature);
            if (allowOverride) {
                infos.add("Option '" + keys.get(feature) + "' overrides the Jackson feature " + name
                        + " the codec sets itself (" + owned.getValue()
                        + "); the codec may no longer behave as specified.");
            } else {
                features.remove(feature);
                warnings.add("Jackson feature " + name + " is set by the codec (" + owned.getValue()
                        + "); the option '" + keys.get(feature) + "' is ignored. Set '"
                        + CodecOptions.CODEC_JACKSON_ALLOW_OVERRIDE + "' to true to override it.");
            }
        }

        if (features.isEmpty() && warnings.isEmpty()) {
            return NONE;
        }
        return new JacksonFeatureOptions(features, allowOverride, warnings, infos);
    }

    /**
     * Reports unusable options and overrides of codec-owned features.
     *
     * @param diagnostics the operation's collector, may be {@code null}
     */
    void report(DiagnosticCollector diagnostics) {
        if (diagnostics == null) {
            return;
        }
        warnings.forEach(w -> diagnostics.addWarning(w, SOURCE));
        infos.forEach(i -> diagnostics.addInfo(i, SOURCE));
    }

    /**
     * Sets the features on a mapper builder. Codec-owned features are only among them when the
     * caller allowed the override.
     *
     * @param builder the operation's mapper builder
     * @return the builder
     */
    JsonMapper.Builder applyTo(JsonMapper.Builder builder) {
        features.forEach((feature, state) -> {
            if (feature instanceof MapperFeature f) {
                builder.configure(f, state);
            } else if (feature instanceof SerializationFeature f) {
                builder.configure(f, state);
            } else if (feature instanceof DeserializationFeature f) {
                builder.configure(f, state);
            } else if (feature instanceof StreamReadFeature f) {
                builder.configure(f, state);
            } else if (feature instanceof StreamWriteFeature f) {
                builder.configure(f, state);
            } else if (feature instanceof JsonReadFeature f) {
                builder.configure(f, state);
            } else if (feature instanceof JsonWriteFeature f) {
                builder.configure(f, state);
            } else if (feature instanceof DatatypeFeature f) {
                builder.configure(f, state);
            } else {
                throw new IllegalStateException("Unhandled Jackson feature " + feature);
            }
        });
        return builder;
    }

    /**
     * Sets the read features on the factory the codec parses JSON with: it creates its parsers
     * from its own features, not from the mapper's.
     *
     * @param builder the factory builder
     * @return the builder
     */
    <B extends JsonFactoryBuilder> B applyTo(B builder) {
        features.forEach((feature, state) -> {
            if (feature instanceof StreamReadFeature f) {
                builder.configure(f, state);
            } else if (feature instanceof JsonReadFeature f) {
                builder.configure(f, state);
            }
        });
        return builder;
    }

    /**
     * Sets the codec-owned deserialization features on a reader: the codec's state, or the
     * caller's where the override is allowed.
     *
     * @param reader the operation's reader
     * @return the reader
     */
    ObjectReader applyCodecOwned(ObjectReader reader) {
        ObjectReader result = reader;
        for (Map.Entry<Enum<?>, Boolean> owned : CODEC_OWNED.entrySet()) {
            if (owned.getKey() instanceof DeserializationFeature f) {
                boolean state = features.getOrDefault(f, owned.getValue());
                result = state ? result.with(f) : result.without(f);
            }
        }
        return result;
    }

    private static Enum<?> resolve(String key, List<String> warnings) {
        String rest = key.substring(CodecOptions.CODEC_JACKSON_PREFIX.length());
        int dot = rest.indexOf('.');
        String typeName = dot < 0 ? rest : rest.substring(0, dot);
        Class<? extends Enum<?>> type = TYPES.get(typeName);
        if (type == null) {
            warnings.add("Unknown Jackson feature type '" + typeName + "' in option '" + key
                    + "'; known types are " + TYPES.keySet() + ". The option is ignored.");
            return null;
        }
        String featureName = dot < 0 ? "" : rest.substring(dot + 1);
        for (Enum<?> constant : type.getEnumConstants()) {
            if (constant.name().equals(featureName)) {
                return constant;
            }
        }
        warnings.add("Unknown Jackson feature '" + typeName + "." + featureName + "' in option '" + key
                + "'. The option is ignored.");
        return null;
    }

    private static Boolean parseBoolean(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s) {
            if ("true".equalsIgnoreCase(s.trim())) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(s.trim())) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    private static String nameOf(Enum<?> feature) {
        return feature.getDeclaringClass().getSimpleName() + "." + feature.name();
    }

    private static Map<String, Class<? extends Enum<?>>> types() {
        Map<String, Class<? extends Enum<?>>> types = new LinkedHashMap<>();
        for (Class<? extends Enum<?>> type : List.<Class<? extends Enum<?>>>of(MapperFeature.class,
                SerializationFeature.class, DeserializationFeature.class, JsonNodeFeature.class,
                EnumFeature.class, DateTimeFeature.class, StreamReadFeature.class,
                StreamWriteFeature.class, JsonReadFeature.class, JsonWriteFeature.class)) {
            types.put(type.getSimpleName(), type);
        }
        return Collections.unmodifiableMap(types);
    }
}
