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
package org.eclipse.fennec.codec.diagnostic;

/**
 * Ends a load because of the document's own content, not because of the codec or the server:
 * a field the class does not know under {@code strictOnUnknown}, a value that cannot be
 * converted under {@code strictOnConversion}, a missing required feature under
 * {@code strictOnMissing}, any error under {@code STRICT} (issue #265).
 * <p>
 * A caller - the REST message body reader is one - can answer it as the client's fault and show
 * its message; it describes the document. It stays an {@link IllegalStateException}, which is
 * what these failures were before.
 * </p>
 *
 * @since 1.0
 */
public class CodecDocumentException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message what is wrong with the document
     */
    public CodecDocumentException(String message) {
        super(message);
    }

    /**
     * @param message what is wrong with the document
     * @param cause the failure behind it
     */
    public CodecDocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
