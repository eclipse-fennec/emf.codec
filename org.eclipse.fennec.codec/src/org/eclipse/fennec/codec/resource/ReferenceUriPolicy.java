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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;

/**
 * What the codec is allowed to do with a reference URI that came out of a document.
 * <p>
 * A reference URI is data. The document says which object a feature points at, and reading it
 * must not turn into the codec opening whatever location the document named — otherwise the
 * document, not the embedder, decides which locations the process talks to. So two things are
 * configurable and both are safe when left alone:
 * </p>
 * <ul>
 *   <li>{@link ConfigProperty#LOAD_REFERENCED_RESOURCES} — off by default: resolution finds
 *       what is already in memory, everything else stays a proxy for the embedder to resolve.
 *       On, the ResourceSet may load the named resource, which is what a trusted-input setup
 *       with genuinely external documents needs.</li>
 *   <li>{@link ConfigProperty#REF_URI_SCHEMES} — empty by default, meaning unrestricted: every
 *       URI is accepted, and one whose scheme can leave the process is reported so it is
 *       visible instead of silent. Name schemes and it becomes an allowlist.</li>
 * </ul>
 *
 * @see <a href="docs/codec-v2-spec/10-reference.md#93-cross-resource-references">Spec 10 §9.3</a>
 */
final class ReferenceUriPolicy {

    /**
     * Schemes whose resolution leaves the process — EMF's default URI converter opens all of
     * them. The list is what the warning is about; it is deliberately not an enforcement list,
     * because enforcement is the embedder's allowlist.
     */
    private static final Set<String> REACHES_OUT_OF_PROCESS = Set.of(
            "http", "https", "ftp", "ftps", "sftp", "file", "jar", "archive", "zip");

    private final boolean loadReferencedResources;
    private final Set<String> allowedSchemes;

    private ReferenceUriPolicy(boolean loadReferencedResources, Set<String> allowedSchemes) {
        this.loadReferencedResources = loadReferencedResources;
        this.allowedSchemes = allowedSchemes;
    }

    /**
     * Reads the policy off the resolver used for this load operation.
     *
     * @param resolver the operation's configuration resolver, may be {@code null}
     * @return the policy, defaults when nothing is configured
     */
    static ReferenceUriPolicy from(ConfigurationResolver resolver) {
        if (resolver == null) {
            return new ReferenceUriPolicy(ConfigProperty.LOAD_REFERENCED_RESOURCES.getDefaultValue(), Set.of());
        }
        return new ReferenceUriPolicy(
                toBoolean(resolver.getGlobalProperty(ConfigProperty.LOAD_REFERENCED_RESOURCES)),
                normalize(resolver.getGlobalProperty(ConfigProperty.REF_URI_SCHEMES)));
    }

    /** OSGi configuration and annotations hand booleans over as strings. */
    private static boolean toBoolean(Object value) {
        if (value instanceof Boolean flag) {
            return flag;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }

    /**
     * A scheme list arrives as a {@code List} from the options map, but OSGi configuration and
     * annotations hand over a {@code String[]} or a single comma-separated string, so all three
     * are accepted. Schemes are compared lower-case, like URI schemes are.
     */
    private static Set<String> normalize(Object configured) {
        if (configured == null) {
            return Set.of();
        }
        Set<String> schemes = new LinkedHashSet<>();
        if (configured instanceof Collection<?> collection) {
            collection.forEach(entry -> addScheme(schemes, entry));
        } else if (configured instanceof Object[] array) {
            for (Object entry : array) {
                addScheme(schemes, entry);
            }
        } else {
            addScheme(schemes, configured);
        }
        return Set.copyOf(schemes);
    }

    private static void addScheme(Set<String> target, Object entry) {
        if (entry == null) {
            return;
        }
        for (String token : entry.toString().split(",")) {
            String scheme = token.trim().toLowerCase(Locale.ROOT);
            if (!scheme.isEmpty()) {
                target.add(scheme);
            }
        }
    }

    /**
     * @return {@code true} when resolution may load the resource a reference names
     */
    boolean loadsReferencedResources() {
        return loadReferencedResources;
    }

    /**
     * @return {@code true} when an allowlist is configured, so refusal is in effect
     */
    boolean isEnforcing() {
        return !allowedSchemes.isEmpty();
    }

    /**
     * Decides whether a reference URI may be looked at at all.
     * <p>
     * A URI without a scheme is relative or a bare fragment: it addresses the document being
     * read or a sibling next to it, never a location of its own, and is always allowed.
     * </p>
     *
     * @param scheme the URI's scheme, {@code null} for a relative or fragment-only URI
     * @return {@code true} when the URI passes the policy
     */
    boolean allows(String scheme) {
        if (scheme == null || scheme.isEmpty()) {
            return true;
        }
        return !isEnforcing() || allowedSchemes.contains(scheme.toLowerCase(Locale.ROOT));
    }

    /**
     * @param scheme the URI's scheme, {@code null} for a relative or fragment-only URI
     * @return {@code true} for a scheme whose resolution would leave the process
     */
    boolean reachesOutOfProcess(String scheme) {
        return scheme != null && REACHES_OUT_OF_PROCESS.contains(scheme.toLowerCase(Locale.ROOT));
    }

    /**
     * @return the configured allowlist, empty when unrestricted
     */
    List<String> allowedSchemes() {
        return List.copyOf(allowedSchemes);
    }
}
