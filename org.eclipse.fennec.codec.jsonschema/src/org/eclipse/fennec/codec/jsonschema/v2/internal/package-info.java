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
 * DS components and implementation classes - deliberately <b>not</b> exported (issue #58).
 * <p>
 * Components are wiring, not API: they are obtained from the service registry, never
 * instantiated or extended by a client. Exporting them would let callers bypass the DS
 * lifecycle and would make every internal change a breaking API change under semantic
 * versioning.
 * </p>
 */
package org.eclipse.fennec.codec.jsonschema.v2.internal;
