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
package org.eclipse.fennec.codec.value;

import org.eclipse.emf.ecore.EAttribute;

/**
 * Specialized value writer for EAttribute values.
 * <p>
 * This interface extends {@link CodecValueWriter} for writing attribute values
 * to JSON. Use this for custom serialization of attribute values where
 * the JSON format should differ from the standard representation.
 * <p>
 * The {@link #canHandle(EAttribute)} method enables type-safe validation at
 * construction time - incompatible writers are rejected early with clear errors.
 * <p>
 * Example use case: Writing Date objects as ISO 8601 date strings:
 * <pre>
 * public class ISO8601DateWriter implements AttributeValueWriter&lt;Date&gt; {
 *     &#64;Override
 *     public String getName() {
 *         return "isoDate";
 *     }
 *
 *     &#64;Override
 *     public boolean canHandle(EAttribute attribute) {
 *         return attribute.getEAttributeType().getInstanceClass() == Date.class;
 *     }
 *
 *     &#64;Override
 *     public void write(Date value, EAttribute attribute, CodecWriterContext ctx) throws IOException {
 *         ctx.getGenerator().writeString(ISO8601_FORMAT.format(value));
 *     }
 * }
 * </pre>
 *
 * @param <T> the type of value to write
 * @see CodecValueWriter
 * @see AttributeValueReader
 * @see CodecWriterContext
 */
public interface AttributeValueWriter<T> extends CodecValueWriter<T, EAttribute> {

    /**
     * Checks if this writer can handle the given attribute.
     * <p>
     * Implementations should check if the attribute type is compatible with
     * the type this writer accepts. For example, a Date writer should
     * verify that the attribute's type is Date or compatible.
     * <p>
     * This method is called at construction time to validate that the writer
     * is appropriate for the attribute. If it returns false, a warning is
     * logged and the writer falls back to default serialization.
     *
     * @param attribute the EAttribute to check
     * @return true if this writer can handle the attribute, false otherwise
     */
    boolean canHandle(EAttribute attribute);
}
