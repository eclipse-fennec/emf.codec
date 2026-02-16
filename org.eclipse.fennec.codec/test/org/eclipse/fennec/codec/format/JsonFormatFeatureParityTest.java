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
package org.eclipse.fennec.codec.format;

import org.eclipse.fennec.codec.format.impl.JacksonFormatProvider;
import org.junit.jupiter.api.DisplayName;

import tools.jackson.core.json.JsonFactory;

/**
 * Runs the full feature parity suite against JSON via the FormatDelegate path.
 * <p>
 * This establishes the JSON baseline: if a future format (CBOR, BSON, etc.)
 * passes the same suite, it has feature parity with JSON.
 */
@DisplayName("JSON Format Feature Parity")
class JsonFormatFeatureParityTest extends AbstractFormatFeatureParityTest {

    @Override
    protected CodecFormatProvider<?, ?> createFormatProvider() {
        return new JacksonFormatProvider("json", new JsonFactory());
    }

    @Override
    protected String getFileExtension() {
        return "json";
    }
}
