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
package org.eclipse.fennec.codec.metadata.type;

/**
 * A discriminator value of the document has no mapping, and the fallback strategy is
 * {@code ERROR}: the document names a type the model does not have (issue #265).
 *
 * @since 1.0
 */
public class UnknownDiscriminatorException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message which value of which registry is unknown
     */
    public UnknownDiscriminatorException(String message) {
        super(message);
    }
}
