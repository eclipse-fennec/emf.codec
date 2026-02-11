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
package org.eclipse.fennec.codec.context;

import org.eclipse.emf.ecore.EStructuralFeature;

import tools.jackson.core.TokenStreamContext;

/**
 * Context interface for EMF codec write (serialization) operations.
 * <p>
 * This interface extends {@link EMFCodecContext} with methods specific to
 * serialization, including child context creation and field name writing.
 * </p>
 * <p>
 * Implementations are used with custom Jackson generators to track EMF state
 * during serialization. Serializers can access the context via:
 * </p>
 * <pre>
 * if (gen.streamWriteContext() instanceof EMFCodecWriteContext ctx) {
 *     EObject parent = ctx.getCurrentEObject();
 *     ClassMetadata metadata = ctx.getClassMetadata(value.eClass());
 *     // ...
 * }
 * </pre>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public interface EMFCodecWriteContext extends EMFCodecContext {

    /**
     * Creates a child array context for serializing array/list values.
     *
     * @return the child array context
     */
    TokenStreamContext createChildArrayContext();

    /**
     * Creates a child array context for serializing array/list values with
     * the specified current value.
     *
     * @param currentValue the current value being serialized
     * @return the child array context
     */
    TokenStreamContext createChildArrayContext(Object currentValue);

    /**
     * Creates a child object context for serializing object values.
     *
     * @return the child object context
     */
    TokenStreamContext createChildObjectContext();

    /**
     * Creates a child object context for serializing object values with
     * the specified current value.
     *
     * @param currentValue the current value being serialized
     * @return the child object context
     */
    TokenStreamContext createChildObjectContext(Object currentValue);

    /**
     * Clears the current context and returns the parent context.
     *
     * @return the parent context
     */
    TokenStreamContext clearAndGetParent();

    /**
     * Resets this context with the specified type and current value.
     *
     * @param type the context type (TYPE_ROOT, TYPE_ARRAY, TYPE_OBJECT)
     * @param currentValue the current value
     * @return this context
     */
    TokenStreamContext reset(int type, Object currentValue);

    /**
     * Writes a feature and field name to the output.
     * <p>
     * This method combines setting the current feature with writing the
     * field name, ensuring the feature is tracked correctly.
     * </p>
     *
     * @param feature the EStructuralFeature being written
     * @param name the field name in the output
     * @return the write status (STATUS_OK_AS_IS, STATUS_OK_AFTER_COMMA, etc.)
     */
    int writeFeatureAndFieldName(EStructuralFeature feature, String name);
}
