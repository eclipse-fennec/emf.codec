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
package org.eclipse.fennec.codec;

import static java.util.Objects.nonNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonFactoryBuilder;

/**
 * Codec specific JsonFactory to create CodecGenerators and CodecParsers.
 * 
 * @author grune
 * @since Apr 10, 2024
 */
public class CodecFactory<R, W, P extends JsonParser, G extends JsonGenerator> extends JsonFactory {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final CodecGeneratorFactory<W, G> genFactory;
	private final CodecParserFactory<R, P> parserFactory;

	/**
	 * Creates a new instance.
	 */
	public CodecFactory(JsonFactoryBuilder builder, CodecGeneratorFactory<W, G> genFactory, CodecParserFactory<R, P> parserFactory) {
		super(builder);
		this.genFactory = genFactory;
		this.parserFactory = parserFactory;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamFactory#createGenerator(java.io.OutputStream)
	 */
	@Override
	public JsonGenerator createGenerator(OutputStream out)  {

		return internalCreateGenerator(out, null);
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.TextualTSFactory#createGenerator(tools.jackson.core.ObjectWriteContext, java.io.OutputStream, tools.jackson.core.JsonEncoding)
	 */
	@Override
	public JsonGenerator createGenerator(ObjectWriteContext writeCtxt,
			OutputStream out, JsonEncoding enc) {
		if (nonNull(genFactory)) {
			return internalCreateGenerator(out, null);
		} else {
			return super.createGenerator(writeCtxt, out, enc);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonFactory#_createUTF8Generator(tools.jackson.core.ObjectWriteContext, tools.jackson.core.io.IOContext, java.io.OutputStream)
	 */
	@Override
	public JsonGenerator _createUTF8Generator(ObjectWriteContext writeCtxt,
			IOContext ioCtxt, OutputStream out) {
		if (nonNull(genFactory)) {
			return internalCreateGenerator(out, ioCtxt);
		} else {
			return super._createUTF8Generator(writeCtxt, ioCtxt, out); 
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonFactory#_createParser(tools.jackson.core.ObjectReadContext, tools.jackson.core.io.IOContext, java.io.DataInput)
	 */
	@Override
	public JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
			DataInput input) {
		if (nonNull(parserFactory)) {
			return internalCreateParser(input, ioCtxt);
		} else {
			return super.createParser(readCtxt, input);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamFactory#createParser(java.io.InputStream)
	 */
	@Override
	public JsonParser createParser(InputStream in) {
		if (nonNull(parserFactory)) {
			return internalCreateParser(in, null);
		} else {
			return super.createParser(ObjectReadContext.empty(), in);
		}
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.TextualTSFactory#createParser(tools.jackson.core.ObjectReadContext, java.io.InputStream)
	 */
	@Override
	public JsonParser createParser(ObjectReadContext ctx, InputStream in) {
		if (nonNull(parserFactory)) {
			return internalCreateParser(in, null);
		} else {
			return super.createParser(ctx, in);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private P internalCreateParser(Object in, IOContext ioCtxt) {
		if (in instanceof CodecReaderProvider) {
			CodecReaderProvider<R> readerProvider = (CodecReaderProvider<R>) in;
			if(ioCtxt == null) {
				ioCtxt = _createContext(
						ContentReference.construct(false, readerProvider.getReader(), ErrorReportConfiguration.defaults()),
						true);
			}			
			return parserFactory.createParser(ioCtxt, readerProvider);
		} else if (in instanceof InputStream is) {
			if(ioCtxt == null) {
				ioCtxt = _createContext(
						ContentReference.construct(false, is, ErrorReportConfiguration.defaults()),
						true);
			}		
			return parserFactory.createParser(ioCtxt, (R)is);
		} else {
			throw new UnsupportedOperationException("The createParser call is only supported with a CodecReaderProvider as parameter.");
		}
	}

	@SuppressWarnings("unchecked")
	private G internalCreateGenerator(Object in, IOContext ioCtxt) {

		if(in instanceof CodecDataOutputAsStream doas) {
			if(ioCtxt == null) {
				ioCtxt = _createContext(
						ContentReference.construct(false, doas.getCodecDataOutput().getWriter(), ErrorReportConfiguration.defaults()),
						true);
			}
			return (G) genFactory.createGenerator(doas.getCodecDataOutput(), ioCtxt);
		}

		if (in instanceof CodecWriterProvider provider) {
			if(ioCtxt == null) {
				ioCtxt = _createContext(
						ContentReference.construct(false, provider.getWriter(), ErrorReportConfiguration.defaults()),
						true);
			}
			return (G) genFactory.createGenerator(provider, ioCtxt);
		} else {
			throw new UnsupportedOperationException("The createGenerator call is only supported with a CodecWriterProvider as parameter.");
		}
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamFactory#_createDataOutputWrapper(java.io.DataOutput)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected OutputStream _createDataOutputWrapper(DataOutput out) {
		return new CodecDataOutputAsStream((CodecDataOutput<W>) out);
	}

}
