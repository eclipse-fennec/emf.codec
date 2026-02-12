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
 * Custom value readers and writers with context-aware interfaces.
 * <p>
 * This package provides the spec-compliant value reader/writer interfaces that
 * receive a context object with access to:
 * <ul>
 *   <li>Jackson parser/generator</li>
 *   <li>Jackson serialization/deserialization context</li>
 *   <li>Effective codec configuration ({@link EffectiveCodecConfig})</li>
 *   <li>Diagnostic collector for warnings and errors</li>
 * </ul>
 * <p>
 * Key interfaces:
 * <ul>
 *   <li>{@link CodecValueReader} - Base reader interface with context</li>
 *   <li>{@link CodecValueWriter} - Base writer interface with context</li>
 *   <li>{@link AttributeValueReader} - Specialized for EAttribute with canHandle()</li>
 *   <li>{@link AttributeValueWriter} - Specialized for EAttribute with canHandle()</li>
 *   <li>{@link ReferenceValueReader} - Specialized for containment EReference</li>
 *   <li>{@link ReferenceValueWriter} - Specialized for containment EReference</li>
 *   <li>{@link CodecReaderContext} - Context for readers</li>
 *   <li>{@link CodecWriterContext} - Context for writers</li>
 *   <li>{@link CodecValueRegistry} - Registry for named readers/writers</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.api.value - Deprecated interfaces without context
 */
@org.osgi.annotation.bundle.Export
@org.osgi.annotation.versioning.Version("1.0.0")
package org.eclipse.fennec.codec.value;
