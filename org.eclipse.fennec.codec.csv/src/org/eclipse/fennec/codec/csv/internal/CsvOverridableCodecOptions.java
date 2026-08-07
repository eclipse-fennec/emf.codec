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
package org.eclipse.fennec.codec.csv.internal;

import org.eclipse.fennec.codec.csv.CodecCsvOptions;

import java.util.Map;

import org.eclipse.fennec.codec.config.RestOverridableCodecOptions;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.osgi.service.component.annotations.Component;

/**
 * Exposes the CSV dialect knobs (and the shared reference mode) for REST client override.
 *
 * @since 2026-06
 */
@Component
public class CsvOverridableCodecOptions implements RestOverridableCodecOptions {

    @Override
    public Map<String, Class<?>> overridableKeys() {
        return Map.of(
                CodecTabularOptions.OPTION_REFERENCE_MODE, String.class,
                CodecCsvOptions.OPTION_DATA_TYPE_IN_SECOND_ROW, Boolean.class,
                CodecCsvOptions.OPTION_DELIMITER, String.class,
                CodecCsvOptions.OPTION_QUOTE_MODE, String.class,
                CodecCsvOptions.OPTION_LINE_ENDING, String.class,
                CodecCsvOptions.OPTION_CHARSET, String.class);
    }
}
