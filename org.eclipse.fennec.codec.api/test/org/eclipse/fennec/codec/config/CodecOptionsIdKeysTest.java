
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
package org.eclipse.fennec.codec.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.fennec.codec.constants.CodecOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Guard test for issue #103: every {@code CodecOptions.CODEC_ID_*} constant
 * must resolve to a canonical {@link ConfigProperty} via its property key.
 * <p>
 * A constant whose value does not match {@code "codec." + ConfigProperty.key}
 * is a silent no-op when passed through load/save options.
 */
public class CodecOptionsIdKeysTest {

    @Test
    @DisplayName("all CODEC_ID_* constants resolve to a ConfigProperty")
    void idConstantsResolveToConfigProperty() throws Exception {
        List<String> unresolved = new ArrayList<>();
        for (Field field : CodecOptions.class.getFields()) {
            if (!field.getName().startsWith("CODEC_ID_")
                    || !Modifier.isStatic(field.getModifiers())
                    || field.getType() != String.class) {
                continue;
            }
            String propertyKey = (String) field.get(null);
            if (ConfigProperty.byPropertyKey(propertyKey) == null) {
                unresolved.add(field.getName() + " = \"" + propertyKey + "\"");
            }
        }
        assertEquals(List.of(), unresolved,
                "CodecOptions id constants without a matching ConfigProperty (silent no-op)");
    }

    @Test
    @DisplayName("separator serialize constant matches the canonical key")
    void separatorSerializeConstantMatchesCanonicalKey() {
        ConfigProperty property = ConfigProperty.byPropertyKey(CodecOptions.CODEC_ID_SERIALIZE_SEPARATOR);
        assertNotNull(property, "CODEC_ID_SERIALIZE_SEPARATOR does not resolve");
        assertEquals(ConfigProperty.ID_SEPARATOR_SERIALIZE, property);
    }
}
