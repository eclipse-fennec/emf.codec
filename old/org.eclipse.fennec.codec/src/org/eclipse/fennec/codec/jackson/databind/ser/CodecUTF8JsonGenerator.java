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
package org.eclipse.fennec.codec.jackson.databind.ser;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.jackson.databind.CodecJsonWriteContext;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.PrettyPrinter;
import tools.jackson.core.SerializableString;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.io.CharacterEscapes;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.UTF8JsonGenerator;

/**
 * 
 * @author ilenia
 * @since May 7, 2025
 */
public class CodecUTF8JsonGenerator extends UTF8JsonGenerator  {

	public CodecUTF8JsonGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt, int streamWriteFeatures,
			int formatWriteFeatures, OutputStream out, SerializableString rootValueSep, CharacterEscapes charEsc,
			PrettyPrinter pp, int maxNonEscaped, char quoteChar) {
		super(writeCtxt, ioCtxt, streamWriteFeatures, formatWriteFeatures, out, rootValueSep, charEsc, pp, maxNonEscaped,
				quoteChar);
		DupDetector dups = StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamWriteFeatures)
                ? DupDetector.rootDetector(this) : null;
		_streamWriteContext = CodecJsonWriteContext.createRootCodecContext(dups);
	}
	
	public CodecJsonWriteContext getStreamWriteContext() {
		return (CodecJsonWriteContext) _streamWriteContext;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeStartArray(java.lang.Object)
	 */
	@Override
	public JsonGenerator writeStartArray(Object forValue) throws JacksonException {
		EStructuralFeature feature = ((CodecJsonWriteContext) _streamWriteContext).getCurrentFeature();
		super.writeStartArray(forValue);
		((CodecJsonWriteContext) _streamWriteContext).setCurrentFeature(feature);
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeStartArray(java.lang.Object, int)
	 */
	@Override
	public JsonGenerator writeStartArray(Object forValue, int len) throws JacksonException {
		return writeStartArray(forValue);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeEndArray()
	 */
	@Override
	public JsonGenerator writeEndArray() throws JacksonException {
		super.writeEndArray();
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeStartObject(java.lang.Object)
	 */
	@Override
	public JsonGenerator writeStartObject(Object forValue) throws JacksonException {
		EStructuralFeature feature = ((CodecJsonWriteContext) _streamWriteContext).getCurrentFeature();
		_verifyValueWrite("start an object");
        CodecJsonWriteContext ctxt = ((CodecJsonWriteContext) _streamWriteContext).createChildObjectContext(forValue);
        ctxt.setCurrentFeature(feature);
        streamWriteConstraints().validateNestingDepth(ctxt.getNestingDepth());
        _streamWriteContext = ctxt;
        if (_prettyPrinter != null) {
        	_prettyPrinter.writeStartObject(this);
        } else {
            if (_outputTail >= _outputEnd) {
                _flushBuffer();
            }
            _outputBuffer[_outputTail++] = '{';
        }
        return this;		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeStartObject(java.lang.Object, int)
	 */
	@Override
	public JsonGenerator writeStartObject(Object forValue, int size) throws JacksonException {
		return writeStartObject(forValue);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeEndObject()
	 */
	@Override
	public JsonGenerator writeEndObject() throws JacksonException {
		super.writeEndObject();
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeString(java.lang.String)
	 */
	@Override
	public JsonGenerator writeString(String text) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeString(text);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeString(java.io.Reader, int)
	 */
	@Override
	public JsonGenerator writeString(Reader reader, int len) throws JacksonException {
		_streamWriteContext.assignCurrentValue(reader);
		super.writeString(reader, len);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeString(char[], int, int)
	 */
	@Override
	public JsonGenerator writeString(char[] text, int offset, int len) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeString(text, offset, len);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeString(tools.jackson.core.SerializableString)
	 */
	@Override
	public JsonGenerator writeString(SerializableString text) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeString(text);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeRawUTF8String(byte[], int, int)
	 */
	@Override
	public JsonGenerator writeRawUTF8String(byte[] text, int offset, int len) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeRawUTF8String(text, offset, len);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeUTF8String(byte[], int, int)
	 */
	@Override
	public JsonGenerator writeUTF8String(byte[] text, int offset, int len) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeUTF8String(text, offset, len);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeRaw(java.lang.String)
	 */
	@Override
	public JsonGenerator writeRaw(String text) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeRaw(text);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeRaw(java.lang.String, int, int)
	 */
	@Override
	public JsonGenerator writeRaw(String text, int offset, int len) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeRaw(text, offset, len);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeRaw(tools.jackson.core.SerializableString)
	 */
	@Override
	public JsonGenerator writeRaw(SerializableString text) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeRaw(text);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeRawValue(tools.jackson.core.SerializableString)
	 */
	@Override
	public JsonGenerator writeRawValue(SerializableString text) throws JacksonException {
		_streamWriteContext.assignCurrentValue(text);
		super.writeRawValue(text);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeRaw(char[], int, int)
	 */
	@Override
	public JsonGenerator writeRaw(char[] cbuf, int offset, int len) throws JacksonException {
		_streamWriteContext.assignCurrentValue(cbuf);
		super.writeRaw(cbuf, offset, len);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeRaw(char)
	 */
	@Override
	public JsonGenerator writeRaw(char ch) throws JacksonException {
		_streamWriteContext.assignCurrentValue(ch);
		super.writeRaw(ch);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeBinary(tools.jackson.core.Base64Variant, byte[], int, int)
	 */
	@Override
	public JsonGenerator writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
			throws JacksonException {
		_streamWriteContext.assignCurrentValue(data);
		super.writeBinary(b64variant, data, offset, len);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeBinary(tools.jackson.core.Base64Variant, java.io.InputStream, int)
	 */
	@Override
	public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws JacksonException {
		_streamWriteContext.assignCurrentValue(data);
		int bytes = super.writeBinary(b64variant, data, dataLength);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return bytes; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(short)
	 */
	@Override
	public JsonGenerator writeNumber(short s) throws JacksonException {
		_streamWriteContext.assignCurrentValue(s);
		super.writeNumber(s);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(int)
	 */
	@Override
	public JsonGenerator writeNumber(int i) throws JacksonException {
		_streamWriteContext.assignCurrentValue(i);
		super.writeNumber(i);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(long)
	 */
	@Override
	public JsonGenerator writeNumber(long l) throws JacksonException {
		_streamWriteContext.assignCurrentValue(l);
		super.writeNumber(l);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(java.math.BigInteger)
	 */
	@Override
	public JsonGenerator writeNumber(BigInteger value) throws JacksonException {
		_streamWriteContext.assignCurrentValue(value);
		super.writeNumber(value);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(double)
	 */
	@Override
	public JsonGenerator writeNumber(double d) throws JacksonException {
		_streamWriteContext.assignCurrentValue(d);
		super.writeNumber(d);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(float)
	 */
	@Override
	public JsonGenerator writeNumber(float f) throws JacksonException {
		_streamWriteContext.assignCurrentValue(f);
		super.writeNumber(f);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(java.math.BigDecimal)
	 */
	@Override
	public JsonGenerator writeNumber(BigDecimal value) throws JacksonException {
		_streamWriteContext.assignCurrentValue(value);
		super.writeNumber(value);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(java.lang.String)
	 */
	@Override
	public JsonGenerator writeNumber(String encodedValue) throws JacksonException {
		_streamWriteContext.assignCurrentValue(encodedValue);
		super.writeNumber(encodedValue);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNumber(char[], int, int)
	 */
	@Override
	public JsonGenerator writeNumber(char[] encodedValueBuffer, int offset, int length) throws JacksonException {
		_streamWriteContext.assignCurrentValue(encodedValueBuffer);
		super.writeNumber(encodedValueBuffer, offset, length);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeBoolean(boolean)
	 */
	@Override
	public JsonGenerator writeBoolean(boolean state) throws JacksonException {
		_streamWriteContext.assignCurrentValue(state);
		super.writeBoolean(state);
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.UTF8JsonGenerator#writeNull()
	 */
	@Override
	public JsonGenerator writeNull() throws JacksonException {
		_streamWriteContext.assignCurrentValue(null);
		super.writeNull();
		((CodecJsonWriteContext) _streamWriteContext).resetFeature();
		return this; 
	}
	
	
}
