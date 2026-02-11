/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.jackson.databind.deser;

import java.io.InputStream;

import org.eclipse.fennec.codec.jackson.databind.CodecJsonReadContext;

import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.UTF8StreamJsonParser;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;

/**
 * 
 * @author ilenia
 * @since May 7, 2025
 */
public class CodecUTF8StreamJsonParser extends UTF8StreamJsonParser {

	
	public CodecUTF8StreamJsonParser(ObjectReadContext readCtxt, IOContext ctxt, int streamReadFeatures,
			int formatReadFeatures, InputStream in, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end,
			int bytesPreProcessed, boolean bufferRecyclable) {
		super(readCtxt, ctxt, streamReadFeatures, formatReadFeatures, in, sym, inputBuffer, start, end, bytesPreProcessed,
				bufferRecyclable);
		DupDetector dups = StreamReadFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamReadFeatures)
                ? DupDetector.rootDetector(this) : null;
		_streamReadContext = CodecJsonReadContext.createRootContext(dups);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonParserBase#streamReadContext()
	 */
	@Override
	public CodecJsonReadContext streamReadContext() {
		return (CodecJsonReadContext) _streamReadContext;
	}

}
