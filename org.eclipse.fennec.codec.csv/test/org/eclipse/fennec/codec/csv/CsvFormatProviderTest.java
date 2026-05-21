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
package org.eclipse.fennec.codec.csv;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Provider metadata + read-not-supported tests for {@link CsvFormatProvider}.
 * <p>
 * End-to-end writing is covered by {@link CsvWriterTest}.
 */
@DisplayName("CsvFormatProvider")
class CsvFormatProviderTest {

    @Nested
    @DisplayName("Provider metadata")
    class ProviderMetadata {

        @Test
        @DisplayName("returns correct format ID")
        void formatId() {
            assertEquals("csv", new CsvFormatProvider().getFormatId());
        }

        @Test
        @DisplayName("returns correct file extensions")
        void fileExtensions() {
            assertArrayEquals(new String[] { "csv" }, new CsvFormatProvider().getFileExtensions());
        }

        @Test
        @DisplayName("returns correct content types")
        void contentTypes() {
            assertArrayEquals(new String[] { "text/csv" }, new CsvFormatProvider().getContentTypes());
        }

        @Test
        @DisplayName("supports array root (multiple EObjects in one Resource)")
        void supportsArrayRoot() {
            assertTrue(new CsvFormatProvider().supportsArrayRoot());
        }
    }

    @Nested
    @DisplayName("Reading")
    class Reading {

        @Test
        @DisplayName("createReader throws UnsupportedOperationException")
        void readerNotSupported() {
            CsvFormatProvider provider = new CsvFormatProvider();
            assertThrows(UnsupportedOperationException.class,
                    () -> provider.createReader(new ByteArrayInputStream(new byte[0])));
        }
    }
}
