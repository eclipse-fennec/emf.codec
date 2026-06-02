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

import java.util.Map;

/**
 * Service interface contributing the codec option keys that a module considers safe for a REST
 * client to override on a per-request basis, together with each value's type.
 * <p>
 * The REST layer ({@code org.eclipse.fennec.codec.rest}) collects every registered implementation
 * and unions their {@link #overridableKeys()} into a single allow-list. A client request may then
 * override only those keys (see the {@code Codec-Options} request header); anything not contributed
 * here is ignored. With no implementations registered the allow-list is empty, so client override
 * is disabled by default (secure by default).
 * <p>
 * Implementations are plain OSGi services — they require no dependency on the REST/Jakarta stack.
 * Each module should reference <em>its own</em> option-key constants (e.g.
 * {@code CodecCsvOptions.OPTION_DELIMITER}) so the allow-list cannot drift from the keys the
 * module's option resolver actually reads.
 * <p>
 * <strong>Security note:</strong> only expose keys that change <em>how</em> data is rendered, not
 * <em>what</em> or <em>how much</em>. Keep options with a real blast radius (e.g. reference
 * expansion, type strategy, custom value reader/writer names) out of the contributed set.
 *
 * @since 2026-06
 */
public interface RestOverridableCodecOptions {

    /**
     * The codec option keys this module allows a REST client to override, mapped to each value's
     * Java type. The type drives parsing of the client-supplied string value (e.g.
     * {@code Boolean.class}, {@code Integer.class}); {@code String.class} (or any unrecognized
     * type) is passed through verbatim.
     *
     * @return an immutable map of option key to value type; never {@code null}
     */
    Map<String, Class<?>> overridableKeys();
}
