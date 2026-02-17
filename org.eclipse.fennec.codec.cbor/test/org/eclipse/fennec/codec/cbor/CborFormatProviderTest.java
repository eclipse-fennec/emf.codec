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
package org.eclipse.fennec.codec.cbor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Provider metadata tests for {@link CborFormatProvider}.
 * Round-trip tests are covered by {@link CborCoreRoundTripTCKTest}.
 */
@DisplayName("CborFormatProvider")
class CborFormatProviderTest {

    @Nested
    @DisplayName("Provider metadata")
    class ProviderMetadata {

        @Test
        @DisplayName("returns correct format ID")
        void formatId() {
            assertEquals("cbor", new CborFormatProvider().getFormatId());
        }

        @Test
        @DisplayName("returns correct file extensions")
        void fileExtensions() {
            assertArrayEquals(new String[] { "cbor" }, new CborFormatProvider().getFileExtensions());
        }

        @Test
        @DisplayName("returns correct content types")
        void contentTypes() {
            assertArrayEquals(new String[] { "application/cbor" }, new CborFormatProvider().getContentTypes());
        }
    }
}
