/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.util;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.emf.ecore.EClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecResourceHelper#resolveEClassFromUri(String)}.
 * <p>
 * Validates URI-based EClass resolution as specified in Spec 15.3 and 15.4.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#153-type-resolution">Spec 15.3: Type Resolution</a>
 */
@DisplayName("CodecResourceHelper.resolveEClassFromUri - Spec 15.3: Type Resolution")
class CodecResourceHelperResolveEClassFromUriTest extends CodecResourceHelperTestBase {

    @Test
    @DisplayName("returns null for null URI")
    void returnsNullForNullUri() {
        EClass result = helper.resolveEClassFromUri(null);
        assertNull(result);
    }

    @Test
    @DisplayName("returns null for empty URI")
    void returnsNullForEmptyUri() {
        EClass result = helper.resolveEClassFromUri("");
        assertNull(result);
    }

    /**
     * Spec 15.3: "Lookup EPackage in MetadataService by namespace URI,
     * Resolve EClass by name within the package"
     */
    @Test
    @DisplayName("resolves Person class from URI (Spec 15.3: EClass resolution)")
    void resolvesPersonClassFromUri() {
        EClass result = helper.resolveEClassFromUri("http://test.example.org/1.0#//Person");
        assertSame(personClass, result);
    }

    @Test
    @DisplayName("resolves Employee class from URI")
    void resolvesEmployeeClassFromUri() {
        EClass result = helper.resolveEClassFromUri("http://test.example.org/1.0#//Employee");
        assertSame(employeeClass, result);
    }

    @Test
    @DisplayName("returns null for unknown class name in valid package")
    void returnsNullForUnknownClassName() {
        EClass result = helper.resolveEClassFromUri("http://test.example.org/1.0#//NonExistent");
        assertNull(result);
    }

    @Test
    @DisplayName("returns null for unknown package URI")
    void returnsNullForUnknownPackageUri() {
        EClass result = helper.resolveEClassFromUri("http://unknown.org/1.0#//Person");
        assertNull(result);
    }
}
