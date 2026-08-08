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
package org.eclipse.fennec.codec.rlang.internal;

import org.eclipse.fennec.codec.rlang.CodecRLangOptions;

import java.util.Map;

import org.eclipse.fennec.codec.config.RestOverridableCodecOptions;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.osgi.service.component.annotations.Component;

/**
 * Exposes the R-lang knobs (and the shared reference mode) for REST client override.
 *
 * @since 1.0
 */
@Component
public class RLangOverridableCodecOptions implements RestOverridableCodecOptions {

    @Override
    public Map<String, Class<?>> overridableKeys() {
        return Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, String.class,
                CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.class);
    }
}
