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
package org.eclipse.fennec.codec.config.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.metadata.model.codec.CodecFactory;
import org.eclipse.fennec.codec.metadata.model.codec.ReferenceSerializationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The two configuration layers have to agree on {@code refKey} (issue #211).
 * <p>
 * The Options layer defaulted to {@code $ref} while {@code codec.ecore} defaulted to
 * {@code _ref}. A STRUCTURED reference written through one path could then not be read back
 * through the other, and nothing in the output said which convention had been used. On top of
 * that, {@code $ref} is unusable in BSON: MongoDB reserves it for DBRef and rejects a
 * subdocument carrying it without a sibling {@code $id}, which made every non-containment
 * reference unwritable by default.
 * </p>
 * <p>
 * This test is the guard that keeps the two in step. It lives here because this is the module
 * that sees both layers - {@code org.eclipse.fennec.codec.api} cannot see the generated model,
 * and {@code org.eclipse.fennec.codec.metadata} cannot see {@code ConfigProperty}.
 * </p>
 */
@DisplayName("refKey default agreement across the config layers")
class RefKeyDefaultAgreementTest {

    private static final String EXPECTED = "_ref";

    @Test
    @DisplayName("the Options layer defaults to _ref")
    void optionsLayerDefault() {
        assertEquals(EXPECTED, ConfigProperty.REF_KEY.<String>getDefaultValue());
        assertEquals(EXPECTED, ReferenceConfig.defaults().getRefKey());
    }

    @Test
    @DisplayName("the model layer defaults to _ref")
    void modelLayerDefault() {
        ReferenceSerializationConfig config = CodecFactory.eINSTANCE.createReferenceSerializationConfig();

        assertEquals(EXPECTED, config.getRefKey());
        assertFalse(config.isSetRefKey(), "an untouched config must still read as 'not configured'");
    }

    @Test
    @DisplayName("both layers agree, so a STRUCTURED reference round-trips across them")
    void layersAgree() {
        ReferenceSerializationConfig modelConfig = CodecFactory.eINSTANCE.createReferenceSerializationConfig();

        assertEquals(ConfigProperty.REF_KEY.<String>getDefaultValue(), modelConfig.getRefKey(),
                "codec.ecore's defaultValueLiteral and ConfigProperty.REF_KEY must not drift apart");
    }

    @Test
    @DisplayName("the default is safe to write into BSON")
    void defaultIsBsonSafe() {
        assertFalse(ConfigProperty.REF_KEY.<String>getDefaultValue().startsWith("$"),
                "MongoDB rejects $-prefixed field names, so the default must not carry one");
    }
}
