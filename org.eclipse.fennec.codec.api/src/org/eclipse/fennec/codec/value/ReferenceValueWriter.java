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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * Specialized value writer for EReference containment references.
 * <p>
 * This interface extends {@link CodecValueWriter} for writing contained EObjects
 * to JSON. Use this for custom serialization of containment references where
 * the JSON structure should differ from the standard EMF object format.
 * <p>
 * The {@link #canHandle(EReference)} method enables type-safe validation at
 * construction time - incompatible writers are rejected early with clear errors.
 * <p>
 * Example use case: Writing an EPackage as embedded JSON Schema within OpenAPI:
 * <pre>
 * public class EPackageValueWriter implements ReferenceValueWriter&lt;EPackage&gt; {
 *     &#64;Override
 *     public String getName() {
 *         return "ePackageToJsonSchema";
 *     }
 *
 *     &#64;Override
 *     public boolean canHandle(EReference reference) {
 *         return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(reference.getEReferenceType());
 *     }
 *
 *     &#64;Override
 *     public void write(EPackage value, EReference reference, CodecWriterContext ctx) throws IOException {
 *         JsonNode schema = converter.convert(value);
 *         ctx.getGenerator().writePOJO(schema);
 *     }
 * }
 * </pre>
 *
 * @param <T> the type of EObject to write (must extend EObject)
 * @see CodecValueWriter
 * @see ReferenceValueReader
 * @see CodecWriterContext
 */
public interface ReferenceValueWriter<T extends EObject> extends CodecValueWriter<T, EReference> {

    /**
     * Checks if this writer can handle the given reference.
     * <p>
     * Implementations should check if the reference type is compatible with
     * the type this writer accepts. For example, an EPackage writer should
     * verify that the reference's type is EPackage or a supertype.
     * <p>
     * This method is called at construction time to validate that the writer
     * is appropriate for the reference. If it returns false, a warning is
     * logged and the writer falls back to default serialization.
     *
     * @param reference the EReference to check
     * @return true if this writer can handle the reference, false otherwise
     */
    boolean canHandle(EReference reference);
}
