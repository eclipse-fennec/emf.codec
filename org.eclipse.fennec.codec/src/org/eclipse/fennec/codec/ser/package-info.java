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

/**
 * Serializers for EMF codec.
 * <p>
 * This package contains Jackson serializers for EMF objects, using the
 * aspect-based MetadataService architecture for serialization decisions.
 * </p>
 * <p>
 * The serialization is organized following the separation of concerns principle:
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.ser.SerializationEntry} - Interface for serialization entries</li>
 *   <li>{@link org.eclipse.fennec.codec.ser.SerializationState} - State holder with value caching</li>
 *   <li>{@link org.eclipse.fennec.codec.ser.IdSerializationEntry} - ID field serialization</li>
 *   <li>{@link org.eclipse.fennec.codec.ser.TypeSerializationEntry} - Type information serialization</li>
 *   <li>{@link org.eclipse.fennec.codec.ser.SuperTypeSerializationEntry} - Supertype information serialization</li>
 *   <li>{@link org.eclipse.fennec.codec.ser.AttributeSerializationEntry} - EAttribute value serialization</li>
 *   <li>{@link org.eclipse.fennec.codec.ser.ReferenceSerializationEntry} - EReference value serialization</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#6-eobject-serialization">Spec 6: EObject Serialization</a>
 */
package org.eclipse.fennec.codec.ser;
