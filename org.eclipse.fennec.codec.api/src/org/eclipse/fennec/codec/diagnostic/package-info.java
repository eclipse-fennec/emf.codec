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
 * Diagnostic support for codec operations.
 * <p>
 * This package provides classes for collecting and reporting errors, warnings,
 * and informational messages during serialization and deserialization.
 * <p>
 * Key classes:
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.diagnostic.CodecDiagnostic} - Individual diagnostic entry</li>
 *   <li>{@link org.eclipse.fennec.codec.diagnostic.DiagnosticCollector} - Aggregates diagnostics</li>
 * </ul>
 *
 * @see <a href="docs/codec-v2-spec/15-error-handling.md">Spec 15: Error Handling</a>
 */
@org.osgi.annotation.bundle.Export
@org.osgi.annotation.versioning.Version("1.0.0")
package org.eclipse.fennec.codec.diagnostic;
