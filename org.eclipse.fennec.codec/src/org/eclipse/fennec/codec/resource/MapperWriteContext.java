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
package org.eclipse.fennec.codec.resource;

import static java.util.Objects.requireNonNull;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Object write context of the generator that bridges to a non-JSON format.
 * <p>
 * A generator hands {@code writePOJO} and {@code writeTree} to its object write context. A
 * generator created by a mapper gets the mapper's context; the {@code FormatDelegateGenerator} is
 * created by the resource, and {@link ObjectWriteContext#empty()} refuses both operations. Value
 * writers that write a POJO or a tree (e.g. the JSON Schema of an {@code EPackage}) then failed for
 * YAML, CBOR, BSON, ... (issue #310). This context delegates both to the codec mapper.
 * </p>
 */
final class MapperWriteContext extends ObjectWriteContext.Base {

    private final ObjectMapper mapper;

    MapperWriteContext(ObjectMapper mapper) {
        this.mapper = requireNonNull(mapper, "mapper must not be null");
    }

    @Override
    public void writeValue(JsonGenerator g, Object value) {
        mapper.writeValue(g, value);
    }

    @Override
    public void writeTree(JsonGenerator g, TreeNode tree) {
        mapper.writeValue(g, tree);
    }
}
