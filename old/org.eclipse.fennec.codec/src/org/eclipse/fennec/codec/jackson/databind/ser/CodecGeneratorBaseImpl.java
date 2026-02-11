/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.CodecGenerator;
import org.eclipse.fennec.codec.CodecGeneratorBase;
import org.eclipse.fennec.codec.jackson.databind.CodecWriteContext;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteCapability;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.Version;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonWriteContext;
import tools.jackson.core.util.JacksonFeatureSet;

/**
 * A basic implementation of the Generator
 * 
 * @author mark
 * @since 09.01.2024
 */
public abstract class CodecGeneratorBaseImpl extends GeneratorBase implements CodecGenerator, CodecGeneratorBase {
	
	private CodecWriteContext _writeContext;

	protected CodecGeneratorBaseImpl(ObjectWriteContext writeCtxt, IOContext ioCtxt,
            int streamWriteFeatures) {
		super(writeCtxt, ioCtxt, streamWriteFeatures);	
		DupDetector dups = StreamWriteFeature.STRICT_DUPLICATE_DETECTION.enabledIn(streamWriteFeatures)
                ? DupDetector.rootDetector(this) : null;
		_writeContext = CodecWriteContext.createRootCodecContext(dups);
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#streamWriteContext
	 */
	@Override
	public CodecWriteContext streamWriteContext() {
		return _writeContext;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#assignCurrentValue(java.lang.Object)
	 */
	@Override
	public void assignCurrentValue(Object v) {
		_writeContext.assignCurrentValue(v);		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#currentValue()
	 */
	@Override
	public Object currentValue() {
		return _writeContext.currentValue();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#version()
	 */
	@Override
	public Version version() {
		return new Version(1, 0, 0, "rc1", "org.eclipse.fennec", "gecko-codec");
	}
	
	
	
	protected EObject getCurrentEObject() {
		
		if (_writeContext.inRoot()) {
			return ((EObject) (_writeContext.currentValue()));
		}
		if (_writeContext.inObject() || _writeContext.inArray()) {
			return (EObject) _writeContext.getParent().currentValue();
		}
		return null;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#flush()
	 */
	@Override
	public void flush() {
		_writeContext.reset(JsonWriteContext.TYPE_ROOT, null);
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.GeneratorBase#_releaseBuffers()
	 */
	@Override
	final protected void _releaseBuffers() {
//		This is called so if we let the unsupportedOperation we get the Exception
//		_reportUnsupportedOperation();
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.GeneratorBase#_verifyValueWrite(java.lang.String)
	 */
	@Override
	final protected void _verifyValueWrite(String typeMsg)  {
		// We have no use for this method. It does nothing
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeStartArray()
	 */
	@Override
	public JsonGenerator writeStartArray() {
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.GeneratorBase#writeStartArray(java.lang.Object, int)
	 */
	@Override
	public JsonGenerator writeStartArray(Object forValue, int size)  {
		writeStartArray(forValue);
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeStartArray(java.lang.Object)
	 */
	@Override
	public JsonGenerator writeStartArray(Object forValue)  {
		assignCurrentValue(forValue);
		int index = _writeContext.getCurrentIndex();
		String name = _writeContext.currentName();
		
		EStructuralFeature feature = _writeContext.getCurrentFeature();
		CodecWriteContext ctx = _writeContext.createChildArrayContext(forValue);
		ctx.setCurrentFeature(feature);
		_writeContext = ctx;
		writeStartArray();
		doStartWriteArray(index + 1, name, forValue);
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeEndArray()
	 */
	@Override
	public JsonGenerator writeEndArray() {
		if (_writeContext.inRoot()) {
			return null;
		}
		if (!_writeContext.inArray()) {
            _reportError("Current context is not array but " + _writeContext.typeDesc());
        }
		_writeContext = _writeContext.clearAndGetParent();
		Object result = _writeContext.currentValue();
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
		int currIndex = _writeContext.getCurrentIndex();
		String currName = _writeContext.currentName();
		doEndWriteArray(currIndex, currName, result);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeStartObject()
	 */
	@Override
	public JsonGenerator writeStartObject()  {
		// Here we still have the root context. It will become the parent after this call
		_verifyValueWrite("Start a new EObject: " + getCurrentEObject());
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeStartObject(java.lang.Object)
	 */
	@Override
	public JsonGenerator writeStartObject(Object forValue)  {
		/* 
		 * We keep the original EObject in the parent context.
		 * The field of the object are serialized in a child context.
		 */
		assignCurrentValue(forValue);
		/*
		 * If we come from a EReference, we want to signal the start of reading an
		 * EObject for a field. So, the name should already be set in the context vie setField.
		 * So we are not in the state STATUS_EXPECT_NAME. If we start a new EObject, no field name 
		 * has been set before  
		 */
		boolean inRoot = _writeContext.inRoot();
		int index = _writeContext.getCurrentIndex();
		String name = _writeContext.currentName();
		/*
		 * We create the sub / child context for the fields of the EObject
		 */
		EStructuralFeature feature = _writeContext.getCurrentFeature();
		CodecWriteContext ctx = _writeContext.createChildObjectContext(forValue);
		ctx.setCurrentFeature(feature);
		_writeContext = ctx;
		writeStartObject();
		if (inRoot) {
			doStartWriteRootEObject(getCurrentEObject());
		} else {
			doStartWriteEObject(index + 1, name, getCurrentEObject());
		}
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeEndObject()
	 */
	@Override
	public JsonGenerator writeEndObject() {
		if (!_writeContext.inObject()) {
            _reportError("Current context is not Object but " + _writeContext.typeDesc());
        }
		EObject result = getCurrentEObject();
		_writeContext = (CodecWriteContext) _writeContext.clearAndGetParent();
		/*
		 * If we have a root object, the we have no field name
		 */
		int currIndex = _writeContext.getCurrentIndex();
		String currName = _writeContext.currentName();
		
		if (_writeContext.inRoot()) {
			doEndWriteRootEObject(result);
		} else {
			if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
				_reportError("Expect a value to write, but a field name is expected");
			}
			doEndWriteEObject(currIndex, currName, result);
		}		
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeName(java.lang.String)
	 */
	@Override
	public JsonGenerator writeName(String name)  {
		if (_writeContext.writeName(name) == JsonWriteContext.STATUS_EXPECT_VALUE) {
			_reportError(String.format("Expected to retrieve a value instead of setting a field name for %s", name));
		}
		_writeContext.writeName(name);
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeString(java.lang.String)
	 */
	@Override
	public JsonGenerator writeString(String text) {
		assignCurrentValue(text);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
//		the _writeContext has no currentName set because it is on the parent context and not on the array child context
		doWriteString(_writeContext.getCurrentIndex(), _writeContext.currentName(), text);
		_writeContext.resetFeature();
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeArray(java.lang.String[], int, int)
	 */
	@Override
	public JsonGenerator writeArray(String[] array, int offset, int length)  {
		super.writeArray(array, offset, length);
		_writeContext.resetFeature();
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeString(char[], int, int)
	 */
	@Override
	public JsonGenerator writeString(char[] buffer, int offset, int len) {
		writeRaw(buffer, offset, len);
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeRawUTF8String(byte[], int, int)
	 */
	@Override
	public JsonGenerator writeRawUTF8String(byte[] buffer, int offset, int len) {
		writeRaw(new String(buffer, StandardCharsets.UTF_8));
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeUTF8String(byte[], int, int)
	 */
	@Override
	public JsonGenerator writeUTF8String(byte[] buffer, int offset, int len) {
		writeString(new String(buffer, StandardCharsets.UTF_8));
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeRaw(java.lang.String)
	 */
	@Override
	public JsonGenerator writeRaw(String text)  {
		writeString(text);
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeRaw(java.lang.String, int, int)
	 */
	@Override
	public JsonGenerator writeRaw(String text, int offset, int len)  {
		writeString(text);
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeRaw(char[], int, int)
	 */
	@Override
	public JsonGenerator writeRaw(char[] text, int offset, int len) {
		assignCurrentValue(text);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
		doWriteChars(_writeContext.getCurrentIndex(), _writeContext.currentName(), text);
		_writeContext.resetFeature();
		return this;
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeRaw(char)
	 */
	@Override
	public JsonGenerator writeRaw(char c) {
		assignCurrentValue(c);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}		
		doWriteChar(_writeContext.getCurrentIndex(), _writeContext.currentName(), c);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeBinary(tools.jackson.core.Base64Variant, byte[], int, int)
	 */
	@Override
	public JsonGenerator writeBinary(Base64Variant bv, byte[] data, int offset, int len)  {
		assignCurrentValue(data);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}		
		doWriteBinary(_writeContext.getCurrentIndex(), _writeContext.currentName(), bv, data, offset, len);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNumber(int)
	 */
	@Override
	public JsonGenerator writeNumber(int v)  {
		assignCurrentValue(v);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}		
		doWriteInt(_writeContext.getCurrentIndex(), _writeContext.currentName(), v);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNumber(long)
	 */
	@Override
	public JsonGenerator writeNumber(long v)  {
		assignCurrentValue(v);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}		
		doWriteLong(_writeContext.getCurrentIndex(), _writeContext.currentName(), v);
		_writeContext.resetFeature();
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNumber(short)
	 */
	@Override
	public JsonGenerator writeNumber(short v) throws JacksonException {
		assignCurrentValue(v);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}		
		doWriteShort(_writeContext.getCurrentIndex(), _writeContext.currentName(), v);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNumber(java.math.BigInteger)
	 */
	@Override
	public JsonGenerator writeNumber(BigInteger v) {
		assignCurrentValue(v);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}		
		doWriteBigInt(_writeContext.getCurrentIndex(), _writeContext.currentName(), v);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNumber(double)
	 */
	@Override
	public JsonGenerator writeNumber(double v)  {
		assignCurrentValue(v);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
		doWriteDouble(_writeContext.getCurrentIndex(), _writeContext.currentName(), v);
		_writeContext.resetFeature();
		return this;
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNumber(float)
	 */
	@Override
	public JsonGenerator writeNumber(float v) {
		assignCurrentValue(v);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
		doWriteFloat(_writeContext.getCurrentIndex(), _writeContext.currentName(), v);
		_writeContext.resetFeature();
		return this;
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNumber(java.math.BigDecimal)
	 */
	@Override
	public JsonGenerator writeNumber(BigDecimal v)  {
		assignCurrentValue(v);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
		doWriteBigDecimal(_writeContext.getCurrentIndex(), _writeContext.currentName(), v);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNumber(java.lang.String)
	 */
	@Override
	public JsonGenerator writeNumber(String encodedValue) {
		assignCurrentValue(encodedValue);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
		doWriteStringNumber(_writeContext.getCurrentIndex(), _writeContext.currentName(), encodedValue);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeBoolean(boolean)
	 */
	@Override
	public JsonGenerator writeBoolean(boolean state) {
		assignCurrentValue(state);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
		doWriteBoolean(_writeContext.getCurrentIndex(), _writeContext.currentName(), state);
		_writeContext.resetFeature();
		return this;
	}

	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeNull()
	 */
	@Override
	public JsonGenerator writeNull()  {
		assignCurrentValue(null);
		if (_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Expect a value to write, but a field name is expected");
		}
		doWriteNull(_writeContext.getCurrentIndex(), _writeContext.currentName());
		_writeContext.resetFeature();
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#canWriteObjectId()
	 */
	@Override
	public boolean canWriteObjectId() {
		return true;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeObjectId(java.lang.Object)
	 */
	@Override
	public JsonGenerator writeObjectId(Object id)  {
		assignCurrentValue(id);
		if(_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Error writing object id while expecting a value");
		}
		doWriteObjectId(_writeContext.getCurrentIndex(), _writeContext.currentName(), id);
		_writeContext.resetFeature();
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writePropertyId(long)
	 */
	@Override
	public JsonGenerator writePropertyId(long id) throws JacksonException {
		assignCurrentValue(id);
		if(_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Error writing property id while expecting a value");
		}
		doWritePropertyId(_writeContext.getCurrentIndex(), _writeContext.currentName(), id);
		_writeContext.resetFeature();
		return this;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#canWriteTypeId()
	 */
	@Override
	public boolean canWriteTypeId() {
		return true;
	}
	

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#writeTypeId(java.lang.Object)
	 */
	@Override
	public JsonGenerator writeTypeId(Object id) {
		assignCurrentValue(id);
		if(_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Error writing type information while expecting a value");
		}
		doWriteType(_writeContext.getCurrentIndex(), _writeContext.currentName(), id);
		_writeContext.resetFeature();
		return this;		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.jackson.CodeGeneratorBase#canWriteSuperTypes()
	 */
	@Override
	public boolean canWriteSuperTypes() {
		return true;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.CodecGeneratorBase#writeSuperTypes(java.lang.String[])
	 */
	@Override
	public JsonGenerator writeSuperTypes(String[] supertypes)  {
		assignCurrentValue(supertypes);
		if(_writeContext.writeValue() == JsonWriteContext.STATUS_EXPECT_NAME) {
			_reportError("Error writing supertype information while expecting a value");
		}
		doWriteSuperTypes(_writeContext.getCurrentIndex(), _writeContext.currentName(), supertypes);
		return this;
	}
	

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.GeneratorBase#_closeInput()
	 */
	@Override
	protected void _closeInput() throws IOException {
		// TODO Auto-generated method stub
		
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#streamWriteOutputTarget()
	 */
	@Override
	public Object streamWriteOutputTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#streamWriteOutputBuffered()
	 */
	@Override
	public int streamWriteOutputBuffered() {
		// TODO Auto-generated method stub
		return 0;
	}

	

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonGenerator#streamWriteCapabilities()
	 */
	@Override
	public JacksonFeatureSet<StreamWriteCapability> streamWriteCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	

	
}
