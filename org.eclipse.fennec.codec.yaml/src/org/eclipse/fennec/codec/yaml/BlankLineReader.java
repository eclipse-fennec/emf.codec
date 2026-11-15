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
package org.eclipse.fennec.codec.yaml;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Empties lines that hold only spaces and tabs before the YAML parser sees them (issue #298).
 * <p>
 * snakeyaml-engine (3.0.1 to 3.2) refuses a block scalar whose only line is blank but deeper
 * indented than the line after it - {@code example: |+} followed by a line of twelve spaces - with
 * "the leading empty lines contain more spaces than the first non-empty line", although YAML 1.2.2
 * §8.1.1.1 takes the longest line as the indentation when there is no non-empty line. The OpenAI
 * description contains such a scalar. Emptying blank lines reads it as the line break it means,
 * as other YAML parsers do.
 * </p>
 * <p>
 * The only other effect: spaces on a blank line inside a literal block scalar, beyond its
 * indentation, are no longer part of the value. Line numbers are kept.
 * </p>
 */
class BlankLineReader extends FilterReader {

	/** Spaces and tabs read at the start of a line, not yet known to be followed by content. */
	private final StringBuilder pending = new StringBuilder();
	private int pendingPos;
	private boolean lineStart = true;
	private int lookahead = -2;
	/** Read ahead in blocks: single-character reads lose characters on Jackson's UTF8Reader. */
	private final char[] buffer = new char[8192];
	private int bufferPos;
	private int bufferEnd;

	BlankLineReader(Reader in) {
		super(in);
	}

	@Override
	public int read() throws IOException {
		if (pendingPos < pending.length()) {
			return pending.charAt(pendingPos++);
		}
		pending.setLength(0);
		pendingPos = 0;
		int c = next();
		if (lineStart && (c == ' ' || c == '\t')) {
			while (c == ' ' || c == '\t') {
				pending.append((char) c);
				c = next();
			}
			if (c == '\n' || c == '\r' || c == -1) {
				pending.setLength(0); // a blank line: drop its spaces
			} else {
				lookahead = c; // content follows: hand out the spaces first
				return pending.charAt(pendingPos++);
			}
		}
		lineStart = c == '\n' || c == '\r';
		return c;
	}

	@Override
	public int read(char[] buf, int off, int len) throws IOException {
		if (len == 0) {
			return 0;
		}
		int count = 0;
		while (count < len) {
			int c = read();
			if (c == -1) {
				return count == 0 ? -1 : count;
			}
			buf[off + count++] = (char) c;
			if (!ready()) {
				break; // return what is there rather than block for more
			}
		}
		return count;
	}

	@Override
	public boolean ready() throws IOException {
		return pendingPos < pending.length() || lookahead != -2 || bufferPos < bufferEnd;
	}

	@Override
	public long skip(long n) throws IOException {
		long skipped = 0;
		while (skipped < n && read() != -1) {
			skipped++;
		}
		return skipped;
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	private int next() throws IOException {
		if (lookahead != -2) {
			int c = lookahead;
			lookahead = -2;
			return c;
		}
		if (bufferPos >= bufferEnd) {
			int n;
			do {
				n = in.read(buffer, 0, buffer.length);
			} while (n == 0);
			if (n < 0) {
				return -1;
			}
			bufferPos = 0;
			bufferEnd = n;
		}
		return buffer[bufferPos++];
	}
}
