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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.emf.ecore.EObject;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.io.IOContext;

/**
 * 
 * @author ilenia
 * @since May 5, 2025
 */
public class CodecGeneratorWrapper extends CodecGeneratorBaseImpl {

	
	private GeneratorBase originalGen;

	public CodecGeneratorWrapper(GeneratorBase originalGen) {
		this(originalGen.objectWriteContext(), originalGen.ioContext(), originalGen.streamWriteFeatures());
		this.originalGen = originalGen;		
	}
	
	
	protected CodecGeneratorWrapper(ObjectWriteContext writeCtxt, IOContext ioCtxt, int streamWriteFeatures) {
		super(writeCtxt, ioCtxt, streamWriteFeatures);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doStartWriteRootEObject(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public JsonGenerator doStartWriteRootEObject(EObject object) {
		doStartWriteEObject(0, "", object);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doEndWriteRootEObject(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public JsonGenerator doEndWriteRootEObject(EObject object) {
		originalGen.writeEndObject();
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteType(int, java.lang.String, java.lang.Object)
	 */
	@Override
	public JsonGenerator doWriteType(int index, String fieldName, Object object) {
		originalGen.writeStringProperty(fieldName, object.toString());
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteSuperTypes(int, java.lang.String, java.lang.String[])
	 */
	@Override
	public JsonGenerator doWriteSuperTypes(int index, String fieldName, String[] superTypes) {
		String types = String.join(",", superTypes);
		originalGen.writeStringProperty(fieldName, types);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteObjectId(int, java.lang.String, java.lang.Object)
	 */
	@Override
	public JsonGenerator doWriteObjectId(int index, String fieldName, Object object) {
		originalGen.writeStringProperty(fieldName, (String) object);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWritePropertyId(int, java.lang.String, long)
	 */
	@Override
	public JsonGenerator doWritePropertyId(int index, String fieldName, long value) {
		// TODO check!!
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doStartWriteEObject(int, java.lang.String, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public JsonGenerator doStartWriteEObject(int index, String fieldName, EObject object) {
		if (fieldName != null && !fieldName.isEmpty()) {
			originalGen.writeName(fieldName);
		}
		originalGen.writeStartObject();
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doEndWriteEObject(int, java.lang.String, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public JsonGenerator doEndWriteEObject(int index, String fieldName, EObject object) {
		originalGen.writeEndObject();
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doStartWriteArray(int, java.lang.String, java.lang.Object)
	 */
	@Override
	public JsonGenerator doStartWriteArray(int index, String fieldName, Object object) {
		if(fieldName == null) {
			originalGen.writeStartArray();
		}
		else {
			originalGen.writeArrayPropertyStart(fieldName);
		}
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doEndWriteArray(int, java.lang.String, java.lang.Object)
	 */
	@Override
	public JsonGenerator doEndWriteArray(int index, String fieldName, Object object) {
		originalGen.writeEndArray();
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteString(int, java.lang.String, java.lang.String)
	 */
	@Override
	public JsonGenerator doWriteString(int index, String fieldName, String value) {
		if(streamWriteContext().inArray()) originalGen.writeString(value);
		else originalGen.writeStringProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteShort(int, java.lang.String, short)
	 */
	@Override
	public JsonGenerator doWriteShort(int index, String fieldName, short value) {
		if(fieldName == null) originalGen.writeNumber(value);
		else originalGen.writeNumberProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteLong(int, java.lang.String, long)
	 */
	@Override
	public JsonGenerator doWriteLong(int index, String fieldName, long value) {
		if(fieldName == null) originalGen.writeNumber(value);
		else originalGen.writeNumberProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteInt(int, java.lang.String, int)
	 */
	@Override
	public JsonGenerator doWriteInt(int index, String fieldName, int value) {
		if(fieldName == null) originalGen.writeNumber(value);
		else originalGen.writeNumberProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteBigInt(int, java.lang.String, java.math.BigInteger)
	 */
	@Override
	public JsonGenerator doWriteBigInt(int index, String fieldName, BigInteger value) {
		if(fieldName == null) originalGen.writeNumber(value);
		else originalGen.writeNumberProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteBigDecimal(int, java.lang.String, java.math.BigDecimal)
	 */
	@Override
	public JsonGenerator doWriteBigDecimal(int index, String fieldName, BigDecimal value) {
		if(fieldName == null) originalGen.writeNumber(value);
		else originalGen.writeNumberProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteFloat(int, java.lang.String, float)
	 */
	@Override
	public JsonGenerator doWriteFloat(int index, String fieldName, float value) {
		if(fieldName == null) originalGen.writeNumber(value);
		else originalGen.writeNumberProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteDouble(int, java.lang.String, double)
	 */
	@Override
	public JsonGenerator doWriteDouble(int index, String fieldName, double value) {
		if(fieldName == null) originalGen.writeNumber(value);
		else originalGen.writeNumberProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteChar(int, java.lang.String, char)
	 */
	@Override
	public JsonGenerator doWriteChar(int index, String fieldName, char value) {
		if(fieldName == null) originalGen.writeString(""+value);
		else originalGen.writeStringProperty(fieldName, ""+value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteChars(int, java.lang.String, char[])
	 */
	@Override
	public JsonGenerator doWriteChars(int index, String fieldName, char[] values) {
		if(fieldName == null) originalGen.writeString(new String(values));
		else originalGen.writeStringProperty(fieldName, new String(values));
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteBoolean(int, java.lang.String, boolean)
	 */
	@Override
	public JsonGenerator doWriteBoolean(int index, String fieldName, boolean value) {
		if(fieldName == null) originalGen.writeBoolean(value);
		else originalGen.writeBooleanProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteStringNumber(int, java.lang.String, java.lang.String)
	 */
	@Override
	public JsonGenerator doWriteStringNumber(int index, String fieldName, String value) {
		if(fieldName == null) originalGen.writeString(value);
		else originalGen.writeStringProperty(fieldName, value);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteBinary(int, java.lang.String, tools.jackson.core.Base64Variant, byte[], int, int)
	 */
	@Override
	public JsonGenerator doWriteBinary(int index, String fieldName, Base64Variant b64variant, byte[] values, int offset,
			int len) {
		if(fieldName == null) originalGen.writeBinary(b64variant, values, offset, len);
		else originalGen.writeBinaryProperty(fieldName, values);
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGenerator#doWriteNull(int, java.lang.String)
	 */
	@Override
	public JsonGenerator doWriteNull(int index, String fieldName) {
		if(fieldName == null) originalGen.writeNull();
		else originalGen.writeNullProperty(fieldName);
		return this;
	}

}
