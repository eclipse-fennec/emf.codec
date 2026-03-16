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
package org.eclipse.fennec.codec.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TypeResolutionHelper}.
 */
@DisplayName("TypeResolutionHelper")
class TypeResolutionHelperTest {

    private static final String TEST_ECORE = "../ser/test-serialization.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private EClass personClass;
    private EClass companyClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, TypeResolutionHelperTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
        personClass = EcoreHelper.getEClass(testPackage, "Person");
        companyClass = EcoreHelper.getEClass(testPackage, "Company");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // resolveFromSimpleName
    // ========================================================================

    @Nested
    @DisplayName("resolveFromSimpleName")
    class ResolveFromSimpleName {

        @Test
        @DisplayName("resolves EClass by simple name")
        void resolvesBySimpleName() {
            EClass resolved = TypeResolutionHelper.resolveFromSimpleName("Company");
            assertNotNull(resolved);
            assertEquals("Company", resolved.getName());
            assertSame(companyClass, resolved);
        }

        @Test
        @DisplayName("resolves different EClass by simple name")
        void resolvesDifferentClass() {
            EClass resolved = TypeResolutionHelper.resolveFromSimpleName("Company");
            assertNotNull(resolved);
            assertEquals("Company", resolved.getName());
            assertSame(companyClass, resolved);
        }

        @Test
        @DisplayName("returns null for unknown name")
        void returnsNullForUnknown() {
            EClass resolved = TypeResolutionHelper.resolveFromSimpleName("NonExistentClass");
            assertNull(resolved);
        }

        @Test
        @DisplayName("returns null for null input")
        void returnsNullForNull() {
            assertNull(TypeResolutionHelper.resolveFromSimpleName(null));
        }

        @Test
        @DisplayName("returns null for empty string")
        void returnsNullForEmpty() {
            assertNull(TypeResolutionHelper.resolveFromSimpleName(""));
        }
    }

    // ========================================================================
    // resolveFromUri
    // ========================================================================

    @Nested
    @DisplayName("resolveFromUri")
    class ResolveFromUri {

        @Test
        @DisplayName("resolves EClass from full URI")
        void resolvesFromFullUri() {
            String uri = testPackage.getNsURI() + "#//Person";
            EClass resolved = TypeResolutionHelper.resolveFromUri(uri);
            assertNotNull(resolved);
            assertSame(personClass, resolved);
        }

        @Test
        @DisplayName("resolves different EClass from URI")
        void resolvesDifferentClassFromUri() {
            String uri = testPackage.getNsURI() + "#//Company";
            EClass resolved = TypeResolutionHelper.resolveFromUri(uri);
            assertNotNull(resolved);
            assertSame(companyClass, resolved);
        }

        @Test
        @DisplayName("returns null for unknown nsURI")
        void returnsNullForUnknownNsUri() {
            EClass resolved = TypeResolutionHelper.resolveFromUri("http://unknown.org/1.0#//Person");
            assertNull(resolved);
        }

        @Test
        @DisplayName("returns null for unknown class in valid package")
        void returnsNullForUnknownClass() {
            String uri = testPackage.getNsURI() + "#//NonExistent";
            EClass resolved = TypeResolutionHelper.resolveFromUri(uri);
            assertNull(resolved);
        }

        @Test
        @DisplayName("returns null for invalid fragment (no //)")
        void returnsNullForInvalidFragment() {
            String uri = testPackage.getNsURI() + "#Person";
            EClass resolved = TypeResolutionHelper.resolveFromUri(uri);
            assertNull(resolved);
        }

        @Test
        @DisplayName("returns null for null input")
        void returnsNullForNull() {
            assertNull(TypeResolutionHelper.resolveFromUri(null));
        }

        @Test
        @DisplayName("returns null for empty string")
        void returnsNullForEmpty() {
            assertNull(TypeResolutionHelper.resolveFromUri(""));
        }
    }

    // ========================================================================
    // resolveFromNumeric
    // ========================================================================

    @Nested
    @DisplayName("resolveFromNumeric")
    class ResolveFromNumeric {

        @Test
        @DisplayName("resolves EClass by classifier ID with hint")
        void resolvesByIdWithHint() {
            int classifierId = personClass.getClassifierID();
            EClass resolved = TypeResolutionHelper.resolveFromNumeric(
                    String.valueOf(classifierId), personClass);
            assertNotNull(resolved);
            assertSame(personClass, resolved);
        }

        @Test
        @DisplayName("returns null without hint — no global scan (S-4)")
        void resolvesByIdWithoutHint() {
            int classifierId = companyClass.getClassifierID();
            EClass resolved = TypeResolutionHelper.resolveFromNumeric(
                    String.valueOf(classifierId), null);
            // S-4: Without a hint, numeric resolution must fail (no global EPackage scan)
            assertNull(resolved, "Without hint, numeric resolution should return null (S-4)");
        }

        @Test
        @DisplayName("hint package takes priority over global search")
        void hintPackageTakesPriority() {
            int classifierId = companyClass.getClassifierID();
            // With hint, must resolve to Company from our test package — not some other package's class
            EClass resolved = TypeResolutionHelper.resolveFromNumeric(
                    String.valueOf(classifierId), companyClass);
            assertNotNull(resolved);
            assertSame(companyClass, resolved, "Hint package should take priority");
        }

        @Test
        @DisplayName("returns null for invalid numeric")
        void returnsNullForInvalidNumeric() {
            assertNull(TypeResolutionHelper.resolveFromNumeric("not-a-number", personClass));
        }

        @Test
        @DisplayName("returns null for null input")
        void returnsNullForNull() {
            assertNull(TypeResolutionHelper.resolveFromNumeric(null, null));
        }

        @Test
        @DisplayName("returns null for empty string")
        void returnsNullForEmpty() {
            assertNull(TypeResolutionHelper.resolveFromNumeric("", null));
        }
    }

    // ========================================================================
    // resolveFromClassName
    // ========================================================================

    @Nested
    @DisplayName("resolveFromClassName")
    class ResolveFromClassName {

        @Test
        @DisplayName("falls back to simple name for unresolvable class name")
        void fallsBackToSimpleName() {
            // "com.example.Person" won't match instanceClass, but "Person" should match via fallback
            EClass resolved = TypeResolutionHelper.resolveFromClassName("com.example.Person");
            assertNotNull(resolved);
            assertEquals("Person", resolved.getName());
        }

        @Test
        @DisplayName("returns null for null input")
        void returnsNullForNull() {
            assertNull(TypeResolutionHelper.resolveFromClassName(null));
        }

        @Test
        @DisplayName("returns null for empty string")
        void returnsNullForEmpty() {
            assertNull(TypeResolutionHelper.resolveFromClassName(""));
        }
    }

    // ========================================================================
    // findClassifierInPackage
    // ========================================================================

    @Nested
    @DisplayName("findClassifierInPackage")
    class FindClassifierInPackage {

        @Test
        @DisplayName("finds EClass by classifier ID in package")
        void findsClassifier() {
            EClass resolved = TypeResolutionHelper.findClassifierInPackage(
                    testPackage, personClass.getClassifierID());
            assertNotNull(resolved);
            assertSame(personClass, resolved);
        }

        @Test
        @DisplayName("returns null for non-existent ID")
        void returnsNullForNonExistentId() {
            assertNull(TypeResolutionHelper.findClassifierInPackage(testPackage, 9999));
        }

        @Test
        @DisplayName("returns null for null package")
        void returnsNullForNullPackage() {
            assertNull(TypeResolutionHelper.findClassifierInPackage(null, 0));
        }
    }

    // ========================================================================
    // isUri
    // ========================================================================

    @Nested
    @DisplayName("isUri")
    class IsUri {

        @Test
        @DisplayName("returns true for URI with fragment")
        void returnsTrueForUri() {
            assertTrue(TypeResolutionHelper.isUri("http://example.org/1.0#//Person"));
        }

        @Test
        @DisplayName("returns false for simple name")
        void returnsFalseForSimpleName() {
            assertFalse(TypeResolutionHelper.isUri("Person"));
        }

        @Test
        @DisplayName("returns false for null")
        void returnsFalseForNull() {
            assertFalse(TypeResolutionHelper.isUri(null));
        }
    }
}
