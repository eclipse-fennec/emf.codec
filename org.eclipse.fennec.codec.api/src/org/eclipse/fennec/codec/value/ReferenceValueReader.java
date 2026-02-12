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
 * Specialized value reader for EReference containment references.
 * <p>
 * This interface extends {@link CodecValueReader} for reading contained EObjects
 * from JSON. Use this for custom deserialization of containment references where
 * the JSON structure differs from the standard EMF object format.
 * <p>
 * The {@link #canHandle(EReference)} method enables type-safe validation at
 * construction time - incompatible readers are rejected early with clear errors.
 * <p>
 * Example use case: Reading embedded JSON Schema as an EPackage within OpenAPI:
 * <pre>
 * public class EPackageValueReader implements ReferenceValueReader&lt;EPackage&gt; {
 *     &#64;Override
 *     public String getName() {
 *         return "jsonSchemaToEPackage";
 *     }
 *
 *     &#64;Override
 *     public boolean canHandle(EReference reference) {
 *         return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(reference.getEReferenceType());
 *     }
 *
 *     &#64;Override
 *     public EPackage read(CodecReaderContext ctx, EReference reference) throws IOException {
 *         TreeNode tree = ctx.getParser().readValueAsTree();
 *         EPackage result = converter.convert((JsonNode) tree);
 *         if (result == null) {
 *             ctx.addWarning("Failed to convert JSON Schema to EPackage");
 *         }
 *         return result;
 *     }
 * }
 * </pre>
 *
 * @param <T> the type of EObject to read (must extend EObject)
 * @see CodecValueReader
 * @see ReferenceValueWriter
 * @see CodecReaderContext
 */
public interface ReferenceValueReader<T extends EObject> extends CodecValueReader<T, EReference> {

    /**
     * Checks if this reader can handle the given reference.
     * <p>
     * Implementations should check if the reference type is compatible with
     * the type this reader produces. For example, an EPackage reader should
     * verify that the reference's type is EPackage or a supertype.
     * <p>
     * This method is called at construction time to validate that the reader
     * is appropriate for the reference. If it returns false, a warning is
     * logged and the reader falls back to default deserialization.
     *
     * @param reference the EReference to check
     * @return true if this reader can handle the reference, false otherwise
     */
    boolean canHandle(EReference reference);
}
