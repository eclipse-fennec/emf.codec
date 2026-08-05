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
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;

/**
 * Named id value reader that restores an EMF id from a BSON {@link ObjectId} (issue #104).
 * <p>
 * Registered under the name {@value #NAME} and wired through the id plane via
 * {@code idValueReaderName}. The BSON parser already surfaces a native ObjectId as its hex
 * string form, so this reader returns the parser's string value — it exists as the symmetric
 * counterpart of {@link ObjectIdValueWriter} and keeps working when the id was stored as a
 * plain string (JSON, non-hex ids).
 * </p>
 *
 * @see ObjectIdValueWriter
 * @author Mark Hoffmann
 * @since 2026-08-05
 */
public class ObjectIdValueReader implements CodecValueReader<Object, EAttribute> {

    /** The registry name of this reader. */
    public static final String NAME = "objectId";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object read(CodecReaderContext ctx, EAttribute feature) throws IOException {
        return ctx.getParser().getString();
    }
}
