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
package org.eclipse.fennec.codec.rest.jakartas.internal;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIException;
import org.eclipse.fennec.codec.diagnostic.CodecDiagnostic;
import org.eclipse.fennec.codec.diagnostic.CodecDiagnosticException;
import org.eclipse.fennec.codec.diagnostic.CodecDocumentException;
import org.eclipse.fennec.codec.metadata.type.UnknownDiscriminatorException;
import org.xml.sax.SAXParseException;

import tools.jackson.core.JacksonException;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.exc.JacksonIOException;

/**
 * Tells a load failure caused by the document from one of the server, and describes it for the
 * client (issue #265).
 * <p>
 * Caused by the document: EMF's XML load errors ({@link XMIException} and its subclasses, such as
 * a namespace without a registered package) and malformed XML; the codec's syntax and read-limit
 * errors ({@link JacksonException}, but not a failing stream), its {@code STRICT} and
 * {@code strictOn*} failures and an unknown discriminator value. The description names the
 * problem with line and column, never the server-side resource URI, and carries no stack trace
 * (issue #165).
 * </p>
 */
final class DocumentErrors {

	private static final int MAX_DIAGNOSTICS = 5;

	private DocumentErrors() {
		// utility class
	}

	/**
	 * Describes a load failure caused by the document.
	 *
	 * @param failure the load failure
	 * @return the description, or {@code null} when the document did not cause the failure
	 */
	static String describe(Throwable failure) {
		for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
			if (cause instanceof XMIException xmi) {
				return withPosition(withoutLocation(xmi.getMessage(), xmi.getLocation(), xmi.getLine(), xmi.getColumn()),
						xmi.getLine(), xmi.getColumn());
			}
			if (cause instanceof SAXParseException sax) {
				return withPosition(sax.getMessage(), sax.getLineNumber(), sax.getColumnNumber());
			}
			if (cause instanceof CodecDiagnosticException diagnostics) {
				return describeDiagnostics(diagnostics.getDiagnostics());
			}
			if (cause instanceof CodecDocumentException || cause instanceof UnknownDiscriminatorException) {
				return cause.getMessage();
			}
			if (cause instanceof JacksonIOException) {
				return null;
			}
			if (cause instanceof JacksonException jackson) {
				TokenStreamLocation location = jackson.getLocation();
				return location == null ? jackson.getOriginalMessage()
						: withPosition(jackson.getOriginalMessage(), location.getLineNr(), location.getColumnNr());
			}
		}
		return null;
	}

	/**
	 * Describes the errors a lenient load left on the resource.
	 *
	 * @param errors the resource's errors
	 * @return the description
	 */
	static String describe(List<Resource.Diagnostic> errors) {
		return errors.stream()
				.limit(MAX_DIAGNOSTICS)
				.map(error -> error.getLine() > 0
						? withPosition(withoutLocation(error.getMessage(), error.getLocation(), error.getLine(),
								error.getColumn()), error.getLine(), error.getColumn())
						: error.getMessage())
				.collect(Collectors.joining("; "));
	}

	private static String describeDiagnostics(List<CodecDiagnostic> diagnostics) {
		return diagnostics.stream()
				.limit(MAX_DIAGNOSTICS)
				.map(diagnostic -> diagnostic.getLine() > 0
						? withPosition(diagnostic.getMessage(), diagnostic.getLine(), diagnostic.getColumn())
						: diagnostic.getMessage())
				.collect(Collectors.joining("; "));
	}

	/** EMF appends {@code (location, line, column)} to its messages; the location is the server's. */
	private static String withoutLocation(String message, String location, int line, int column) {
		if (message == null || location == null) {
			return message;
		}
		return message.replaceFirst("\\s*\\(" + Pattern.quote(location) + ", " + line + ", " + column + "\\)$", "");
	}

	private static String withPosition(String message, long line, long column) {
		return line > 0 ? message + " (line " + line + ", column " + column + ")" : message;
	}
}
