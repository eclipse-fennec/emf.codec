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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecResourceHelper#resolveRootEClass(Map)}.
 * <p>
 * Validates CODEC_ROOT_TYPE option resolution as specified in Spec 15.4.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#154-codec_root_object-option">Spec 15.4: CODEC_ROOT_TYPE Option</a>
 */
@DisplayName("CodecResourceHelper.resolveRootEClass - Spec 15.4: CODEC_ROOT_TYPE Option")
class CodecResourceHelperResolveRootEClassTest extends CodecResourceHelperTestBase {

    @Test
    @DisplayName("returns null for null options")
    void returnsNullForNullOptions() {
        EClass result = helper.resolveRootEClass(null);
        assertNull(result);
    }

    @Test
    @DisplayName("returns null for empty options")
    void returnsNullForEmptyOptions() {
        EClass result = helper.resolveRootEClass(new HashMap<>());
        assertNull(result);
    }

    @Test
    @DisplayName("returns null when CODEC_ROOT_TYPE not set")
    void returnsNullWhenOptionNotSet() {
        Map<String, Object> options = new HashMap<>();
        options.put("OTHER_OPTION", "value");

        EClass result = helper.resolveRootEClass(options);
        assertNull(result);
    }

    /**
     * Spec 15.4: "Supported Value Types: EClass - Direct EClass reference (preferred)"
     */
    @Test
    @DisplayName("returns EClass when passed directly (Spec 15.4: EClass value type)")
    void returnsEClassWhenPassedDirectly() {
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResourceHelper.CODEC_ROOT_TYPE, personClass);

        EClass result = helper.resolveRootEClass(options);
        assertSame(personClass, result);
    }

    /**
     * Spec 15.4: "Supported Value Types: String (URI) - EClass URI for cross-bundle scenarios"
     */
    @Test
    @DisplayName("resolves EClass from URI string (Spec 15.4: String URI value type)")
    void resolvesEClassFromUriString() {
        // Get the actual URI from the registered ClassMetadata
        String uri = metadataService.getClassMetadata(personClass).getTypeURI();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResourceHelper.CODEC_ROOT_TYPE, uri);

        EClass result = helper.resolveRootEClass(options);
        assertSame(personClass, result);
    }

    @Test
    @DisplayName("returns null for unknown URI string")
    void returnsNullForUnknownUri() {
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResourceHelper.CODEC_ROOT_TYPE, "http://unknown.org/1.0#//Unknown");

        EClass result = helper.resolveRootEClass(options);
        assertNull(result);
    }

    @Test
    @DisplayName("returns null for invalid option type (not EClass or String)")
    void returnsNullForInvalidOptionType() {
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResourceHelper.CODEC_ROOT_TYPE, Integer.valueOf(42));

        EClass result = helper.resolveRootEClass(options);
        assertNull(result);
    }
}
