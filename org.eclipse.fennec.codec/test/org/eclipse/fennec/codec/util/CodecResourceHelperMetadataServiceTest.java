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

import static org.junit.jupiter.api.Assertions.assertSame;

import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecResourceHelper#getMetadataService()}.
 */
@DisplayName("CodecResourceHelper.getMetadataService")
class CodecResourceHelperMetadataServiceTest extends CodecResourceHelperTestBase {

    @Test
    @DisplayName("returns the metadata service")
    void returnsMetadataService() {
        MetadataService result = helper.getMetadataService();
        assertSame(metadataService, result);
    }
}
