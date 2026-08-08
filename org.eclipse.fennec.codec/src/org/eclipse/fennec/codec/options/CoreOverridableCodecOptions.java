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
 *
 * @since 1.0
 */
@Component
public class CoreOverridableCodecOptions implements RestOverridableCodecOptions {

    @Override
    public Map<String, Class<?>> overridableKeys() {
        return Map.of(
                ConfigProperty.SERIALIZE_NULL.getKey(), Boolean.class,
                ConfigProperty.SERIALIZE_EMPTY.getKey(), Boolean.class,
                ConfigProperty.SERIALIZE_DEFAULT.getKey(), Boolean.class,
                ConfigProperty.ENUM_SERIALIZATION.getKey(), String.class,
                ConfigProperty.FIELD_ORDER.getKey(), String.class,
                ConfigProperty.ID_ON_TOP.getKey(), Boolean.class,
                ConfigProperty.DATE_FORMAT.getKey(), String.class);
    }
}
