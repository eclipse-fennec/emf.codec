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
package org.eclipse.fennec.codec.csv.parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.eclipse.fennec.codec.CodecReaderProvider;
import org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonToken;
import tools.jackson.core.Version;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.VersionUtil;

/**
 * 
 * @author ilenia
 * @since Sep 22, 2025
 */
public class CodecCSVParser extends CodecParserBaseImpl {
	
	private enum State {
		BEGIN,
		NAME,
		VALUE,
		END;
	}
	
	private InputStream input;
	private Map<String, Object> dataMap;
	private String currentName = null;
	private State state = State.BEGIN;
	

	/**
	 * Creates a new instance.
	 * 
	 * @param context
	 * @param reader
	 * @param objectCodec 
	 */
	public CodecCSVParser(IOContext context, CodecReaderProvider<InputStream> reader) {
		super(null, context, -1, -1, reader.getObjectCodec());
		this.input = reader.getReader();
		try {
			this.dataMap = QueryStringParser.parse(input);
		} catch (Exception e) {
			this.dataMap = Collections.emptyMap();
		}
	}
	
	public CodecCSVParser(IOContext context, InputStream is) {
		super(null, context, -1, -1);
		this.input = is;
		try {
			this.dataMap = QueryStringParser.parse(input);
		} catch (Exception e) {
			this.dataMap = Collections.emptyMap();
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#closeInput()
	 */
	@Override
	public void closeInput() {
		dataMap.clear();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isEndDocument()
	 */
	@Override
	public boolean isEndDocument() {
		return State.END.equals(state);
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isBeginDocument()
	 */
	@Override
	public boolean isBeginDocument() {
		return State.BEGIN.equals(state);
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isBeginArray()
	 */
	@Override
	public boolean isBeginArray() {
		return false;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doBeginArray()
	 */
	@Override
	public void doBeginArray() {
	}



	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doEndArray()
	 */
	@Override
	public void doEndArray() {
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doEndDocument()
	 */
	@Override
	public void doEndDocument() {
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doReadName()
	 */
	@Override
	public String doReadName() {
		currentName = dataMap.keySet().iterator().next();
		state = State.VALUE;
		return currentName;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doBeginDocument()
	 */
	@Override
	public void doBeginDocument() {
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetCurrentToken()
	 */
	@Override
	public JsonToken doGetCurrentToken() {
		return switch (state) {
		case BEGIN -> {
			yield JsonToken.START_OBJECT;
		}
		case NAME -> {
			yield JsonToken.PROPERTY_NAME;
		}
		case VALUE -> {
			yield JsonToken.VALUE_STRING;
		}
		case END -> {
			yield JsonToken.END_OBJECT;
		}
	};
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetNextToken()
	 */
	@Override
	public JsonToken doGetNextToken() {
		switch (state) {
		case BEGIN:
			state = State.NAME;
			break;
		case NAME:
			state = State.VALUE;
			break;
		case VALUE:
			state = dataMap.isEmpty() ? State.END : State.NAME;
			break;
		case END:
			state = State.END;
			break;
		}
		return doGetCurrentToken();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetCurrentValue()
	 */
	@Override
	public Object doGetCurrentValue() {
		return getCurrentValue(currentName);
	}




	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonParser#canReadObjectId()
	 */
	@Override
	public boolean canReadObjectId() {
		return false;
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonParser#getObjectId()
	 */
	@Override
	public Object getObjectId()  {
		return currentValue();
	}

	private Object getCurrentValue(String name) {
		return dataMap.remove(name);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.JsonParser#version()
	 */
	@Override
	public Version version() {
		return VersionUtil.parseVersion(
				"1.0.0-SNAPSHOT", "org.eclipse.fennec.codec", "codec-csv");
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#getStringValueObject()
	 */
	@Override
	public Object getStringValueObject() {
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.ParserBase#_parseNumericValue(int)
	 */
	@Override
	protected void _parseNumericValue(int expType) throws JacksonException, InputCoercionException {
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.base.ParserBase#_parseIntValue()
	 */
	@Override
	protected int _parseIntValue() throws JacksonException {
		return 0;
	}



}
