/*
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
package org.eclipse.fennec.codec.value;

import org.eclipse.emf.ecore.EAttribute;

/**
 * Specialized value reader for EAttribute values.
 * <p>
 * This interface extends {@link CodecValueReader} for reading attribute values
 * from JSON. Use this for custom deserialization of attribute values where
 * the JSON format differs from the standard representation.
 * <p>
 * The {@link #canHandle(EAttribute)} method enables type-safe validation at
 * construction time - incompatible readers are rejected early with clear errors.
 * <p>
 * Example use case: Reading ISO 8601 date strings as Date objects:
 * <pre>
 * public class ISO8601DateReader implements AttributeValueReader&lt;Date&gt; {
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
 *     public Date read(CodecReaderContext ctx, EAttribute attribute) throws IOException {
 *         try {
 *             return ISO8601_FORMAT.parse(ctx.getParser().getString());
 *         } catch (ParseException e) {
 *             ctx.addWarning("Invalid date format: " + e.getMessage());
 *             return null;
 *         }
 *     }
 * }
 * </pre>
 *
 * @param <T> the type of value to read
 * @see CodecValueReader
 * @see AttributeValueWriter
 * @see CodecReaderContext
 */
public interface AttributeValueReader<T> extends CodecValueReader<T, EAttribute> {

    /**
     * Checks if this reader can handle the given attribute.
     * <p>
     * Implementations should check if the attribute type is compatible with
     * the type this reader produces. For example, a Date reader should
     * verify that the attribute's type is Date or compatible.
     * <p>
     * This method is called at construction time to validate that the reader
     * is appropriate for the attribute. If it returns false, a warning is
     * logged and the reader falls back to default deserialization.
     *
     * @param attribute the EAttribute to check
     * @return true if this reader can handle the attribute, false otherwise
     */
    boolean canHandle(EAttribute attribute);
}
