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

import org.eclipse.emf.ecore.EClass;

import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.exc.StreamReadException;

/**
 * Context interface for EMF codec read (deserialization) operations.
 * <p>
 * This interface extends {@link EMFCodecContext} with methods specific to
 * deserialization, including type resolution and child context creation.
 * </p>
 * <p>
 * Implementations are used with custom Jackson parsers to track EMF state
 * during deserialization. Deserializers can access the context via:
 * </p>
 * <pre>
 * if (parser.streamReadContext() instanceof EMFCodecReadContext ctx) {
 *     EClass resolved = ctx.resolveEClass(typeValue, hintClass);
 *     // ...
 * }
 * </pre>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public interface EMFCodecReadContext extends EMFCodecContext {

    /**
     * Creates a child array context at the specified location.
     *
     * @param lineNr the line number
     * @param colNr the column number
     * @return the child array context
     */
    TokenStreamContext createChildArrayContext(int lineNr, int colNr);

    /**
     * Creates a child object context at the specified location.
     *
     * @param lineNr the line number
     * @param colNr the column number
     * @return the child object context
     */
    TokenStreamContext createChildObjectContext(int lineNr, int colNr);

    /**
     * Resets this context with the specified type and location.
     *
     * @param type the context type (TYPE_ROOT, TYPE_ARRAY, TYPE_OBJECT)
     * @param lineNr the line number
     * @param colNr the column number
     * @return this context
     */
    TokenStreamContext reset(int type, int lineNr, int colNr);

    /**
     * Clears the current context and returns the parent context.
     *
     * @return the parent context
     */
    TokenStreamContext clearAndGetParent();

    /**
     * Sets the current field name being parsed.
     *
     * @param name the field name
     * @throws StreamReadException if the name cannot be set
     */
    void setCurrentName(String name) throws StreamReadException;

    /**
     * Checks if this context has a parent context.
     *
     * @return true if there is a parent context
     */
    boolean hasParentContext();

    /**
     * Returns the current type hint for deserialization.
     * <p>
     * The type hint is typically set from CODEC_ROOT_TYPE option or
     * from the containing reference's EType.
     * </p>
     *
     * @return the type hint EClass, or null if none
     */
    EClass getCurrentTypeHint();

    /**
     * Sets the current type hint for deserialization.
     *
     * @param typeHint the type hint EClass
     */
    void setCurrentTypeHint(EClass typeHint);

    /**
     * Resolves an EClass from a type value (URI, name, discriminator, etc.).
     * <p>
     * This method handles the type resolution strategies defined in Section 3
     * of the spec (URI, SIMPLE_NAME, MAPPED).
     * </p>
     *
     * @param typeValue the type value from JSON
     * @return the resolved EClass, or null if not found
     */
    EClass resolveEClass(String typeValue);

    /**
     * Resolves an EClass from a type value with a hint.
     * <p>
     * This method follows the type resolution priority defined in Section 15.4:
     * <ol>
     *   <li>If typeValue is non-null and resolves to a valid EClass, use it</li>
     *   <li>Otherwise, if hint is non-null and instantiable, use it</li>
     *   <li>Otherwise, return the hint (caller must check instantiability)</li>
     * </ol>
     * </p>
     *
     * @param typeValue the type value from JSON (may be null)
     * @param hint the CODEC_ROOT_TYPE or reference type hint (may be null)
     * @return the resolved EClass, or null if neither typeValue nor hint provided
     * @see <a href="docs/codec-v2-serialization-spec.md#154-codec_root_object-option">Spec 15.4: Type Resolution</a>
     */
    EClass resolveEClass(String typeValue, EClass hint);
}
