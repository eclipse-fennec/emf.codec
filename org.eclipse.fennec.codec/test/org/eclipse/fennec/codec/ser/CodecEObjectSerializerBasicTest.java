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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Basic tests for {@link CodecEObjectSerializer}.
 *
 * @see <a href="docs/codec-v2-spec/06-eobject-serialization.md">Spec 6: EObject Serialization</a>
 */
@DisplayName("CodecEObjectSerializer basic operations")
class CodecEObjectSerializerBasicTest extends CodecEObjectSerializerTestBase {

    @Test
    @DisplayName("handledType returns EObject class")
    void handledTypeReturnsEObjectClass() {
        CodecEObjectSerializer serializer = new CodecEObjectSerializer(effectiveConfig);
        assertEquals(EObject.class, serializer.handledType());
    }

    @Test
    @DisplayName("serializer is not null")
    void serializerIsNotNull() {
        CodecEObjectSerializer serializer = new CodecEObjectSerializer(effectiveConfig);
        assertNotNull(serializer);
    }
}
