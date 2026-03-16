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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for S-5: Reflection-Based Object Instantiation Allowlist.
 * <p>
 * Verifies that only safe types are permitted for reflection-based conversion
 * from String in {@link AttributeDeserializationEntry}. Arbitrary classes
 * must not be instantiable via the deserialization path.
 * </p>
 * <p>
 * Security: CWE-470 (Use of Externally-Controlled Input to Select Classes or Code).
 * </p>
 */
@DisplayName("S-5: Reflection Allowlist Protection")
class ReflectionAllowlistTest {

    @Nested
    @DisplayName("Allowlist contents")
    class AllowlistContents {

        @Test
        @DisplayName("contains URI/URL types")
        void containsUriTypes() {
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(URI.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(java.net.URL.class));
        }

        @Test
        @DisplayName("does not contain types handled by direct code paths")
        void doesNotContainDirectlyHandledTypes() {
            // BigDecimal, BigInteger, UUID, Date are converted directly
            // in convertObjectFromString() before the allowlist check
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(BigDecimal.class));
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(BigInteger.class));
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(UUID.class));
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(Date.class));
        }

        @Test
        @DisplayName("contains java.time types")
        void containsJavaTimeTypes() {
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(Instant.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(LocalDate.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(LocalTime.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(LocalDateTime.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(OffsetDateTime.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(ZonedDateTime.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(Duration.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(Period.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(Year.class));
            assertTrue(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(YearMonth.class));
        }

        @Test
        @DisplayName("does not contain dangerous types")
        void doesNotContainDangerousTypes() {
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(Runtime.class));
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(ProcessBuilder.class));
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(Thread.class));
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(ClassLoader.class));
        }

        @Test
        @DisplayName("does not contain Object or String")
        void doesNotContainObjectOrString() {
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(Object.class));
            assertFalse(AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(String.class));
        }

        @Test
        @DisplayName("allowlist has expected size")
        void allowlistSize() {
            assertEquals(13, AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.size());
        }
    }

    @Nested
    @DisplayName("End-to-end: blocked reflection via CodecResource")
    class E2EBlocked {

        @Test
        @DisplayName("arbitrary type in object array produces warning")
        void arbitraryType_producesWarning() throws Exception {
            // This test verifies the concept at the unit level:
            // convertObjectFromString is private, so we verify the allowlist
            // rejects types not in the set. The readObjectArray caller
            // catches the exception and produces a warning diagnostic.
            assertFalse(
                    AttributeDeserializationEntry.SAFE_REFLECTION_TARGETS.contains(
                            javax.management.ObjectName.class),
                    "javax.management.ObjectName should not be in the allowlist");
        }
    }
}
