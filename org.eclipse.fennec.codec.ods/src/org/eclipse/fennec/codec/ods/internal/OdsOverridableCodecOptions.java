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
package org.eclipse.fennec.codec.ods.internal;

import org.eclipse.fennec.codec.ods.CodecOdsOptions;

import java.util.Map;

import org.eclipse.fennec.codec.config.RestOverridableCodecOptions;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.osgi.service.component.annotations.Component;

/**
 * Exposes the ODS rendering knobs (and the shared reference mode) for REST client override.
 *
 * @since 2026-06
 */
@Component
public class OdsOverridableCodecOptions implements RestOverridableCodecOptions {

    @Override
    public Map<String, Class<?>> overridableKeys() {
        return Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, String.class,
                CodecOdsOptions.OPTION_STYLE_HEADER, Boolean.class,
                CodecOdsOptions.OPTION_ADJUST_COLUMN_WIDTH, Boolean.class,
                CodecOdsOptions.OPTION_GENERATE_LINKS, Boolean.class);
    }
}
