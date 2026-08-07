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
package org.eclipse.fennec.codec.bson;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.fennec.codec.format.jackson.FormatDelegateGenerator;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;

import tools.jackson.core.JsonGenerator;

/**
 * Named id value writer that stores an EMF id as a native BSON {@link ObjectId} (issue #104).
 * <p>
 * Registered under the name {@value #NAME} and wired through the id plane via
 * {@code idValueWriterName} (spec 14-custom-values.md). The value must be a valid 24-character
 * ObjectId hex string ({@link ObjectId#isValid(String)}) and the target format must support a
 * native ObjectId type — otherwise the plain string form is written, so the writer degrades
 * gracefully on JSON and for non-hex ids.
 * </p>
 * <p>
 * {@code ObjectId} is a plain BSON spec type (0x07) from the {@code org.bson} library — no
 * MongoDB driver is involved.
 * </p>
 *
 * @see ObjectIdValueReader
 * @author Mark Hoffmann
 * @since 2026-08-05
 */
public class ObjectIdValueWriter implements CodecValueWriter<Object, EAttribute> {

    /** The registry name of this writer. */
    public static final String NAME = "objectId";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void write(Object value, EAttribute feature, CodecWriterContext ctx) throws IOException {
        String hex = value.toString();
        JsonGenerator gen = ctx.getGenerator();
        if (gen instanceof FormatDelegateGenerator<?> delegating
                && delegating.supportsNativeObjectId()
                && ObjectId.isValid(hex)) {
            delegating.writeObjectId(new ObjectId(hex));
        } else {
            gen.writeString(hex);
        }
    }
}
