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
/**
 * EMF Codec Context classes for tracking state during serialization and deserialization.
 * <p>
 * This package provides the context infrastructure needed by custom Jackson generators
 * and parsers for EMF serialization. The key classes are:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.context.EMFCodecContext} - Base interface for codec contexts</li>
 *   <li>{@link org.eclipse.fennec.codec.context.EMFCodecWriteContext} - Interface for serialization contexts</li>
 *   <li>{@link org.eclipse.fennec.codec.context.EMFCodecReadContext} - Interface for deserialization contexts</li>
 *   <li>{@link org.eclipse.fennec.codec.context.CodecWriteContext} - Implementation for serialization</li>
 *   <li>{@link org.eclipse.fennec.codec.context.CodecReadContext} - Implementation for deserialization</li>
 *   <li>{@link org.eclipse.fennec.codec.context.EMFContextHolder} - Holds EMF state (current EObject, feature, etc.)</li>
 * </ul>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
@org.osgi.annotation.bundle.Export
package org.eclipse.fennec.codec.context;
