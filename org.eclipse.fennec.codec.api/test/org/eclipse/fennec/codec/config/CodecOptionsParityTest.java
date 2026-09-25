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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.fennec.codec.constants.CodecOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Every configuration property has a Java constant in {@link CodecOptions} (issue #242), so a
 * caller never has to spell a {@code "codec.<key>"} string by hand.
 */
@DisplayName("CodecOptions / ConfigProperty parity")
class CodecOptionsParityTest {

    /**
     * Properties without a constant, on purpose: the metadata merge is parsed from annotations
     * but not applied by the serializer yet, so offering an option would promise behavior that
     * does not exist. They get their constants when the feature is built.
     */
    private static final Set<String> WITHOUT_CONSTANT = Set.of("metadataMerge", "metadataKey");

    @Test
    @DisplayName("every ConfigProperty has a CodecOptions constant")
    void everyPropertyHasAConstant() throws IllegalAccessException {
        Set<String> constantValues = new HashSet<>();
        for (Field field : CodecOptions.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                constantValues.add((String) field.get(null));
            }
        }
        List<String> missing = new ArrayList<>();
        for (ConfigProperty property : ConfigProperty.values()) {
            if (!WITHOUT_CONSTANT.contains(property.getKey())
                    && !constantValues.contains(property.getPropertyKey())) {
                missing.add(property.name() + " (" + property.getPropertyKey() + ")");
            }
        }
        assertEquals(List.of(), missing, "ConfigProperty entries without a CodecOptions constant");
    }

    @Test
    @DisplayName("properties that nothing reads are gone")
    void unreadPropertiesAreGone() {
        List<String> present = new ArrayList<>();
        for (String key : List.of("discriminatorPath", "discriminatorValue", "valueReaders", "valueWriters",
                "metadataFieldsFirst")) {
            if (ConfigProperty.byKey(key) != null) {
                present.add(key);
            }
        }
        assertEquals(List.of(), present,
                "a property nobody reads must not look configurable; an unknown key is reported instead");
    }
}
