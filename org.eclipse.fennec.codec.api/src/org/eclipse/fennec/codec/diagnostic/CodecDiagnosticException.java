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

import java.util.Collections;
import java.util.List;

/**
 * Carries the diagnostics that made a strict load fail (issue #134).
 * <p>
 * {@code DeserializationMode.STRICT} reports an error <b>and</b> fails the load: the
 * diagnostics stay on the resource, and the {@code IOException} thrown from
 * {@code Resource.load} carries this exception as its cause so a caller can reach them
 * without going back to the resource - which, having failed to load, it may not want to
 * consult at all.
 * </p>
 * <p>
 * The two {@code strictOn*} options are subsets of the same idea: they fail the load for one
 * kind of problem each, where {@code STRICT} fails for any.
 * </p>
 *
 * @since 1.0
 */
public class CodecDiagnosticException extends Exception {

    private static final long serialVersionUID = 1L;

    private final transient List<CodecDiagnostic> diagnostics;

    /**
     * Creates an exception for the given diagnostics.
     *
     * @param message the summary shown in the stack trace
     * @param diagnostics the diagnostics that caused the failure
     */
    public CodecDiagnosticException(String message, List<CodecDiagnostic> diagnostics) {
        super(message);
        this.diagnostics = diagnostics == null
                ? Collections.emptyList()
                : List.copyOf(diagnostics);
    }

    /**
     * The diagnostics that caused the load to fail.
     *
     * @return the diagnostics, never null
     */
    public List<CodecDiagnostic> getDiagnostics() {
        return diagnostics;
    }
}
