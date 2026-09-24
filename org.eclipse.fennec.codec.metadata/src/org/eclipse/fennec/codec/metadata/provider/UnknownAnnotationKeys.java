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
package org.eclipse.fennec.codec.metadata.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.fennec.emf.osgi.model.metadata.DiagnosticSeverity;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataDiagnostic;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataFactory;

/**
 * Reports codec annotation detail keys that nothing reads (issue #236).
 * <p>
 * The counterpart of the option side's unknown-key warning (#220). The known keys are read off
 * the {@code KEY_*} constants of {@link CodecAnnotationConstants} rather than kept in a second
 * list - a hand-kept list drifting from the constants it mirrors is how #220 happened. A known key
 * on the wrong element is not reported here; the placement checks of {@code CodecAspectProvider}
 * handle that with a sharper message.
 * </p>
 */
final class UnknownAnnotationKeys {

    /** Edit distance up to which a key counts as a misspelling of a known one. */
    private static final int MAX_SUGGESTION_DISTANCE = 2;

    private static final Set<String> KNOWN_KEYS = readKnownKeys();

    private UnknownAnnotationKeys() {
    }

    /**
     * Reports every detail key of the main codec annotation that is no known key.
     *
     * @param diagnostics where to report
     * @param details the details of the {@code http://eclipse.org/fennec/codec} annotation
     * @param element a description of the annotated element, e.g. {@code EClass 'Device'}
     */
    static void checkCodecKeys(EList<MetadataDiagnostic> diagnostics, Map<String, String> details, String element) {
        for (String key : details.keySet()) {
            if (KNOWN_KEYS.contains(key)) {
                continue;
            }
            String message = "Unknown codec annotation key '" + key + "' on " + element
                    + "; nothing reads it" + suggestion(key, KNOWN_KEYS);
            add(diagnostics, message, key);
        }
    }

    /**
     * Reports detail keys of a {@code typeMapping/{mapId}} annotation that look like a misspelt
     * configuration key. Any other key is a discriminator value mapped to an EClass, so only a key
     * close to a configuration key is suspicious.
     *
     * @param diagnostics where to report
     * @param details the details of the type mapping annotation
     * @param element a description of the annotated element
     */
    static void checkTypeMappingKeys(EList<MetadataDiagnostic> diagnostics, Map<String, String> details,
            String element) {
        Set<String> known = CodecAnnotationConstants.TYPE_MAPPING_KNOWN_KEYS;
        for (String key : details.keySet()) {
            if (known.contains(key)) {
                continue;
            }
            Optional<String> meant = closest(key, known);
            meant.ifPresent(k -> add(diagnostics, "Type mapping key '" + key + "' on " + element
                    + " looks like '" + k + "'; nothing reads it as such - it is taken as a"
                    + " discriminator value mapped to an EClass", key));
        }
    }

    /**
     * Reports annotation sources that look like a codec source but are none, such as
     * {@code codec} or {@code codec.type.<id>} from an earlier dialect. None of their details is read.
     *
     * @param diagnostics where to report
     * @param modelElement the annotated element
     * @param element a description of the annotated element
     */
    static void checkSources(EList<MetadataDiagnostic> diagnostics, EModelElement modelElement, String element) {
        for (EAnnotation annotation : modelElement.getEAnnotations()) {
            String source = annotation.getSource();
            if (source != null && (source.equals("codec") || source.startsWith("codec.") || source.startsWith("codec/"))) {
                add(diagnostics, "Annotation source '" + source + "' on " + element
                        + " is not a codec annotation source, none of its details is read; codec annotations use '"
                        + CodecAnnotationConstants.CODEC_SOURCE + "' (type mappings '"
                        + CodecAnnotationConstants.TYPE_MAPPING_SOURCE_PREFIX + "<mapId>')", source);
            }
        }
    }

    private static String suggestion(String key, Collection<String> candidates) {
        return closest(key, candidates).map(k -> " - did you mean '" + k + "'?").orElse("");
    }

    /**
     * The closest candidate within the edit budget: a short key tolerates fewer edits than a long
     * one, like the option side's {@code KnownOptionKeys.suggest}.
     */
    private static Optional<String> closest(String key, Collection<String> candidates) {
        int budget = Math.min(MAX_SUGGESTION_DISTANCE, Math.max(1, key.length() / 4));
        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String candidate : candidates) {
            int distance = distance(key, candidate);
            if (distance <= budget && distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }
        return Optional.ofNullable(best);
    }

    private static int distance(String a, String b) {
        int[] previous = new int[b.length() + 1];
        int[] current = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1), previous[j - 1] + cost);
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return previous[b.length()];
    }

    private static void add(EList<MetadataDiagnostic> diagnostics, String message, String key) {
        MetadataDiagnostic diagnostic = MetadataFactory.eINSTANCE.createMetadataDiagnostic();
        diagnostic.setSeverity(DiagnosticSeverity.WARNING);
        diagnostic.setMessage(message);
        diagnostic.setKey(key);
        diagnostics.add(diagnostic);
    }

    private static Set<String> readKnownKeys() {
        Set<String> keys = new HashSet<>();
        for (Field field : CodecAnnotationConstants.class.getFields()) {
            if (field.getName().startsWith("KEY_") && field.getType() == String.class
                    && Modifier.isStatic(field.getModifiers())) {
                try {
                    keys.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Cannot read " + field.getName(), e);
                }
            }
        }
        return Set.copyOf(keys);
    }
}
