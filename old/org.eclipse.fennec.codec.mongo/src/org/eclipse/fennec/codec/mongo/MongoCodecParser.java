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
package org.eclipse.fennec.codec.mongo;

import java.math.BigDecimal;

import org.bson.AbstractBsonReader;
import org.bson.BsonBinary;
import org.bson.BsonType;
import org.eclipse.fennec.codec.CodecReaderProvider;
import org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TreeCodec;
import tools.jackson.core.Version;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.VersionUtil;

/**
 * 
 * @author ilenia
 * @since Apr 25, 2025
 */
public class MongoCodecParser extends CodecParserBaseImpl {

	private AbstractBsonReader reader;

	/**
	 * Creates a new instance.
	 * 
	 * @param context
	 * @param reader
	 * @param objectCodec 
	 */
	public MongoCodecParser(IOContext context, CodecReaderProvider<AbstractBsonReader> reader) {
		super(null, context, -1, -1, reader.getObjectCodec());
		this.reader = reader.getReader();
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param context
	 * @param reader
	 * @param objectCodec 
	 */
	public MongoCodecParser(IOContext context, AbstractBsonReader reader, TreeCodec objectCodec) {
		super(null, context, -1, -1, objectCodec);
		this.reader = reader;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#closeInput()
	 */
	@Override
	public void closeInput() {
		// Do not close the mongo reader here!
		// reader.close();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isEndDocument()
	 */
	@Override
	public boolean isEndDocument() {
		return reader.getCurrentBsonType() == BsonType.END_OF_DOCUMENT;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isBeginDocument()
	 */
	@Override
	public boolean isBeginDocument() {
		return reader.getCurrentBsonType() == BsonType.DOCUMENT;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isBeginArray()
	 */
	@Override
	public boolean isBeginArray() {
		return reader.getCurrentBsonType() == BsonType.ARRAY;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doBeginArray()
	 */
	@Override
	public void doBeginArray() {
		reader.readStartArray();
	}



	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doEndArray()
	 */
	@Override
	public void doEndArray() {
		reader.readEndArray();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doEndDocument()
	 */
	@Override
	public void doEndDocument() {
		reader.readEndDocument();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doReadName()
	 */
	@Override
	public String doReadName() {
		return reader.readName();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doBeginDocument()
	 */
	@Override
	public void doBeginDocument() {
		reader.readStartDocument();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetCurrentToken()
	 */
	@Override
	public JsonToken doGetCurrentToken() {
		BsonType currentType = reader.getCurrentBsonType();
		return map(currentType);
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetNextToken()
	 */
	@Override
	public JsonToken doGetNextToken() {
		BsonType nextType = reader.readBsonType();
		return map(nextType);
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetCurrentValue()
	 */
	@Override
	public Object doGetCurrentValue() {
		return getCurrentValue(reader.getCurrentBsonType());
	}




	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonParser#canReadObjectId()
	 */
	@Override
	public boolean canReadObjectId() {
		return true;
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonParser#getObjectId()
	 */
	@Override
	public Object getObjectId()  {
		return currentValue();
	}

	private Object getCurrentValue(BsonType bsonType) {
		switch (bsonType) { 
		case STRING:
			return reader.readString();
		case BOOLEAN:
			return reader.readBoolean();
		case INT32:
			return reader.readInt32();
		case INT64:
			return reader.readInt64();
		case DOUBLE:
			return reader.readDouble();
		case OBJECT_ID:
			return reader.readObjectId().toHexString();
		case BINARY:
			return reader.readBinaryData();
		case DECIMAL128:
			return reader.readDecimal128();
		case NULL:
			reader.readNull();
			//go through
		default:
			return null;
		}
	}

	private JsonToken map(BsonType nextBsonType) {
		switch (nextBsonType) {
		case DOCUMENT:
			return JsonToken.START_OBJECT;
		case END_OF_DOCUMENT:
			return streamReadContext().inArray() ? JsonToken.END_ARRAY : JsonToken.END_OBJECT;
		case ARRAY:
			return JsonToken.START_ARRAY;
		case INT32:
		case INT64:
			return JsonToken.VALUE_NUMBER_INT;
		case DOUBLE:
			return JsonToken.VALUE_NUMBER_FLOAT;
		case STRING:
		case OBJECT_ID:
			return JsonToken.VALUE_STRING;
		case BOOLEAN:
			return JsonToken.VALUE_TRUE; //not sure here because in principle also JsonToken.VALUE_FALSE is a valid one...?
		case BINARY:
			return JsonToken.VALUE_STRING; // what to put here???
		case NULL:
			return JsonToken.VALUE_NULL;
		case DECIMAL128:
			return JsonToken.VALUE_NUMBER_FLOAT;
		default:
			return JsonToken.PROPERTY_NAME;
		}
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#getDecimalValue()
	 */
	@Override
	public BigDecimal getDecimalValue() {
		return ((org.bson.types.Decimal128) streamReadContext().currentValue()).bigDecimalValue();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#getBinaryValue()
	 */
	@Override
	public byte[] getBinaryValue()  {
		return ((org.bson.BsonBinary) streamReadContext().currentValue()).getData();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#getBinaryValue(tools.jackson.core.Base64Variant)
	 */
	@Override
	public byte[] getBinaryValue(Base64Variant variant)  {		
		return((org.bson.BsonBinary) streamReadContext().currentValue()).getData();
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonParserBase#hasStringCharacters()
	 */
	@Override
	public boolean hasStringCharacters() {		
		//		Overridden to fix issue while resolving proxy for single non contained reference. See org.eclipse.fennec.codec.mongo.test.testDeserializationReference
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.ParserBase#_parseNumericValue(int)
	 */
	@Override
	protected void _parseNumericValue(int expType) throws JacksonException, InputCoercionException {
		this._numTypesValid = NR_INT;

	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.ParserBase#_parseIntValue()
	 */
	@Override
	protected int _parseIntValue() throws JacksonException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonParser#version()
	 */
	@Override
	public Version version() {
		return VersionUtil.parseVersion(
				"1.0.0-SNAPSHOT", "org.eclipse.fennec.codec", "codec-mongo");
	}
	
	

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.ParserBase#getNumberType()
	 */
	@Override
	public NumberType getNumberType() { //needed to properly pass the right number type when we have a Decimal128

		if(streamReadContext().currentValue() instanceof Number n) {
			if (n instanceof org.bson.types.Decimal128) return NumberType.BIG_DECIMAL;	
		}
		return super.getNumberType();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#getStringValueObject()
	 */
	@Override
	public Object getStringValueObject() {
		if((_currToken == JsonToken.VALUE_STRING)) {
			if(streamReadContext().currentValue() instanceof BsonBinary bsonBinary) {
				return bsonBinary.getData();
			}
		}
		return streamReadContext().currentValue();		
	}

}
