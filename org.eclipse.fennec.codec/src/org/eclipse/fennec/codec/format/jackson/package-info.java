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
 * The Jackson-based building blocks other bundles extend (issue #59).
 * <p>
 * These three types are a deliberate cross-bundle extension surface, not implementation
 * detail: {@code JacksonFormatProvider} is the base class the cbor and yaml providers derive
 * from, and {@code FormatDelegateGenerator}/{@code FormatDelegateParser} bridge a format that
 * is not Jackson-based into the codec pipeline without a byte stream in between - which is how
 * the BSON provider and the MongoDB persistence layer plug in.
 * </p>
 * <p>
 * They used to live in {@code ...format.impl}, whose name told readers and tooling that
 * changes were free while downstream providers actually depended on them. The abstract
 * contracts stay in {@code org.eclipse.fennec.codec.format} in the API bundle; this package
 * holds what you extend to satisfy them.
 * </p>
 */
@org.osgi.annotation.bundle.Export
@org.osgi.annotation.versioning.Version("1.0.0")
package org.eclipse.fennec.codec.format.jackson;
