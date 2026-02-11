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
package org.eclipse.fennec.codec.jackson;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.fennec.codec.jackson.databind.deser.CodecUTF8StreamJsonParser;
import org.eclipse.fennec.codec.jackson.databind.ser.CodecUTF8JsonGenerator;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.SerializableString;
import tools.jackson.core.io.CharacterEscapes;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonFactoryBuilder;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;

/**
 * 
 * @author ilenia
 * @since May 7, 2025
 */
public class DefaultCodecJsonFactory extends JsonFactory {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	public DefaultCodecJsonFactory(JsonFactoryBuilder builder) {
		super(builder);
	}
	

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonFactory#_createUTF8Generator(tools.jackson.core.ObjectWriteContext, tools.jackson.core.io.IOContext, java.io.OutputStream)
	 */
	@Override
	protected JsonGenerator _createUTF8Generator(ObjectWriteContext writeCtxt, IOContext ioCtxt, OutputStream out)
			throws JacksonException {
		SerializableString rootSep = writeCtxt.getRootValueSeparator(_rootValueSeparator);
		// May get Character-Escape overrides from context; if not, use factory's own
		// (which default to `null`)
		CharacterEscapes charEsc = writeCtxt.getCharacterEscapes();
		if (charEsc == null) {
			charEsc = _characterEscapes;
		}
		// 14-Jan-2019, tatu: Should we make this configurable via databind layer?
		final int maxNonEscaped = _maximumNonEscapedChar;
		// NOTE: JSON generator does not use schema

		return new CodecUTF8JsonGenerator(writeCtxt, ioCtxt,
				writeCtxt.getStreamWriteFeatures(_streamWriteFeatures),
				writeCtxt.getFormatWriteFeatures(_formatWriteFeatures),
				out,
				rootSep, charEsc, writeCtxt.getPrettyPrinter(), maxNonEscaped, _quoteChar);
	}

	@Override
	protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
			InputStream in) throws JacksonException
	{
		try {
			ByteQuadsCanonicalizer can = _byteSymbolCanonicalizer.makeChild(_factoryFeatures);
			return new CodecUTF8StreamJsonParser(readCtxt, ioCtxt,
					_streamReadFeatures, _formatReadFeatures, in, can,
                    ioCtxt.allocReadIOBuffer(), 0, 0, 0, true);
		} catch (RuntimeException e) {
			// 10-Jun-2022, tatu: For [core#763] may need to close InputStream here
			if (ioCtxt.isResourceManaged()) {
				try {
					in.close();
				} catch (Exception e2) {
					e.addSuppressed(e2);
				}
			}
			throw e;
		}
	}


}
