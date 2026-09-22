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
package org.eclipse.fennec.codec.options;

import java.util.Map;

import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.RestOverridableCodecOptions;
import org.osgi.service.component.annotations.Component;

/**
 * Exposes the harmless, presentational core codec options for REST client override: which values
 * are written (null/default/empty), enum serialization strategy, field ordering, and id-on-top.
 * These apply across all formats (JSON and the tabular exporters).
 * <p>
 * The keys are published in their prefixed form ({@code codec.serializeDefault}, …), the same
 * spelling every format contribution uses and the same spelling the public
 * {@code CodecOptions.CODEC_*} constants carry, so a caller assembling a {@code Codec-Options}
 * header out of those constants is understood (issue #223). Both spellings reach the resolver -
 * {@code ConfigMergeHelper} looks a property up under its bare and its prefixed key alike - and the
 * REST filter accepts either on the wire, so the bare form a client sent before keeps working.
 * </p>
 *
 * @since 1.0
 */
@Component
public class CoreOverridableCodecOptions implements RestOverridableCodecOptions {

    @Override
    public Map<String, Class<?>> overridableKeys() {
        return Map.of(
                ConfigProperty.SERIALIZE_NULL.getPropertyKey(), Boolean.class,
                ConfigProperty.SERIALIZE_EMPTY.getPropertyKey(), Boolean.class,
                ConfigProperty.SERIALIZE_DEFAULT.getPropertyKey(), Boolean.class,
                ConfigProperty.ENUM_SERIALIZATION.getPropertyKey(), String.class,
                ConfigProperty.FIELD_ORDER.getPropertyKey(), String.class,
                ConfigProperty.ID_ON_TOP.getPropertyKey(), Boolean.class,
                ConfigProperty.DATE_FORMAT.getPropertyKey(), String.class);
    }
}
