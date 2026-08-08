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
package org.eclipse.fennec.codec.xlsx.internal;

import org.eclipse.fennec.codec.xlsx.CodecXlsxOptions;

import java.util.Map;

import org.eclipse.fennec.codec.config.RestOverridableCodecOptions;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.osgi.service.component.annotations.Component;

/**
 * Exposes the XLSX rendering knobs (and the shared reference mode) for REST client override.
 *
 * @since 1.0
 */
@Component
public class XlsxOverridableCodecOptions implements RestOverridableCodecOptions {

    @Override
    public Map<String, Class<?>> overridableKeys() {
        return Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, String.class,
                CodecXlsxOptions.OPTION_STYLE_HEADER, Boolean.class,
                CodecXlsxOptions.OPTION_ADJUST_COLUMN_WIDTH, Boolean.class,
                CodecXlsxOptions.OPTION_FREEZE_HEADER_ROW, Boolean.class,
                CodecXlsxOptions.OPTION_GENERATE_LINKS, Boolean.class,
                CodecXlsxOptions.OPTION_DEFAULT_DATE_FORMAT, String.class);
    }
}
