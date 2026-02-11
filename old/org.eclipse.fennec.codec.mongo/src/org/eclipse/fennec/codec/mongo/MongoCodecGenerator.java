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
package org.eclipse.fennec.codec.mongo;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.bson.BsonBinary;
import org.bson.BsonWriter;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.jackson.databind.ser.CodecGeneratorBaseImpl;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.io.IOContext;

/**
 * 
 * @author grune
 * @since Jan 29, 2024
 */
public class MongoCodecGenerator extends CodecGeneratorBaseImpl {

	private BsonWriter writer;

	/**
	 * Creates a new instance.
	 * @param objectCodec 
	 * 
	 * @param writer2
	 */
	public MongoCodecGenerator(BsonWriter writer, IOContext ioCtxt) {
		super(null, ioCtxt, -1);
		this.writer = writer;
	}
	
	public MongoCodecGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt,
            int streamWriteFeatures) {
		super(writeCtxt, ioCtxt, streamWriteFeatures);
	}

	@Override
	public JsonGenerator doStartWriteRootEObject(EObject eObject) {
		doStartWriteEObject(0, "", eObject);
		return this;
	}

	@Override
	public JsonGenerator doEndWriteRootEObject(EObject object) {
		writer.writeEndDocument();
		writer.flush();
		return this;
	}

	@Override
	public JsonGenerator doWriteType(int index, String fieldName, Object object) {
		writer.writeString(fieldName, object.toString());
		return this;
	}

	@Override
	public JsonGenerator doWriteSuperTypes(int index, String fieldName, String[] superTypes) {
		String types = String.join(",", superTypes);
		writer.writeString(fieldName, types);
		return this;
	}

	@Override
	public JsonGenerator doWriteObjectId(int index, String fieldName, Object object) {
		if (object instanceof ObjectId) {
			writer.writeObjectId(fieldName, (ObjectId) object);
		} else if (object instanceof String) {
			if (ObjectId.isValid((String) object)) {
				ObjectId objectId = new ObjectId((String) object);
				writer.writeObjectId(fieldName, objectId);
			} else {
				writer.writeString(fieldName, (String) object);
			}
		} else if (object == null) {
			ObjectId objectId = new ObjectId();
			streamWriteContext().assignCurrentValue(objectId);
			writer.writeObjectId(fieldName, objectId);
		} else {
			System.out.println("???" + object);
		}
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.jackson.CodecGenerator#doStartWriteEObject(int, java.lang.String, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public JsonGenerator doStartWriteEObject(int index, String fieldName, EObject object) {
		if (fieldName != null && !fieldName.isEmpty()) {
			writer.writeName(fieldName);
		}
		writer.writeStartDocument();
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.CodecGenerator#doEndWriteEObject(int, java.lang.String, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public JsonGenerator doEndWriteEObject(int index, String fieldName, EObject object) {
		writer.writeEndDocument();
		return this;
	}

	@Override
	public JsonGenerator doStartWriteArray(int index, String fieldName, Object object) {
		if(fieldName == null) writer.writeStartArray();
		else writer.writeStartArray(fieldName);
		return this;
	}

	@Override
	public JsonGenerator doEndWriteArray(int index, String fieldName, Object object) {
		writer.writeEndArray();
		return this;
	}

	@Override
	public JsonGenerator doWriteString(int index, String fieldName, String value) {
		if(streamWriteContext().inArray()) writer.writeString(value);
		else writer.writeString(fieldName, value);
		return this;
	}

	@Override
	public JsonGenerator doWriteShort(int index, String fieldName, short value) {
		if(fieldName == null) writer.writeInt32(value);
		else writer.writeInt32(fieldName, value);
		return this;
	}

	@Override
	public JsonGenerator doWriteLong(int index, String fieldName, long value) {
		if(fieldName == null) writer.writeInt64(value);
		else writer.writeInt64(fieldName, value);
		return this;
	}

	@Override
	public JsonGenerator doWriteInt(int index, String fieldName, int value) {
		if(fieldName == null) writer.writeInt32(value);
		else writer.writeInt32(fieldName, value);
		return this;
	}

	@Override
	public JsonGenerator doWriteBigInt(int index, String fieldName, BigInteger value) {
		if(fieldName == null) writer.writeString(value.toString());
		else writer.writeString(fieldName, value.toString());
		return this;
	}

	@Override
	public JsonGenerator doWriteBigDecimal(int index, String fieldName, BigDecimal value) {
		if(fieldName == null) writer.writeDecimal128(new Decimal128(value));
		else writer.writeDecimal128(fieldName, new Decimal128(value));
		return this;
	}

	@Override
	public JsonGenerator doWriteFloat(int index, String fieldName, float value) {
		// https://stackoverflow.com/questions/7682714/does-mongodb-support-floating-point-types
		if(fieldName == null) writer.writeDouble(value);
		else writer.writeDouble(fieldName, value);
		return this;
	}

	@Override
	public JsonGenerator doWriteDouble(int index, String fieldName, double value) {
		if(fieldName == null) writer.writeDouble(value);
		else writer.writeDouble(fieldName, value);
		return this;
	}

	@Override
	public JsonGenerator doWriteChar(int index, String fieldName, char value) {
		if(fieldName == null) writer.writeString(""+value);
		else writer.writeString(fieldName, "" + value);
		return this;
	}

	@Override
	public JsonGenerator doWriteChars(int index, String fieldName, char[] values) {
		if(fieldName == null) writer.writeString(new String(values));
		else writer.writeString(fieldName, new String(values));
		return this;
	}

	@Override
	public JsonGenerator doWriteBoolean(int index, String fieldName, boolean value) {
		if(fieldName == null) writer.writeBoolean(value);
		else writer.writeBoolean(fieldName, value);
		return this;
	}

	@Override
	public JsonGenerator doWriteStringNumber(int index, String fieldName, String value) {
		if(fieldName == null) writer.writeString(value);
		else writer.writeString(fieldName, value);
		return this;
	}

	@Override
	public JsonGenerator doWriteBinary(int index, String fieldName, Base64Variant b64variant, byte[] values, int offset, int len) {
		if(fieldName == null) writer.writeBinaryData(new BsonBinary(values));
		else writer.writeBinaryData(fieldName, new BsonBinary(values));
		return this;
	}

	@Override
	public JsonGenerator doWriteNull(int index, String fieldName) {
		if(fieldName == null) writer.writeNull();
		else writer.writeNull(fieldName);
		return this;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.CodecGenerator#doWritePropertyId(int, java.lang.String, long)
	 */
	@Override
	public JsonGenerator doWritePropertyId(int index, String fieldName, long value) {
		// TODO: check this!!
		return doWriteObjectId(index, fieldName, value);
	}

	
	

}
