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
 * Implementation detail of the Jackson stream path - deliberately <b>not</b> exported
 * (issue #58/#59).
 * <p>
 * What other bundles extend lives in {@code org.eclipse.fennec.codec.format.jackson}. Nothing
 * here is referenced outside this package, and keeping it private means changing it is not a
 * breaking API change.
 * </p>
 */
package org.eclipse.fennec.codec.format.impl;
