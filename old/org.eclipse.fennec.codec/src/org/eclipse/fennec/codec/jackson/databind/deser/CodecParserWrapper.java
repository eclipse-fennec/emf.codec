///**
// * Copyright (c) 2012 - 2025 Data In Motion and others.
// * All rights reserved. 
// * 
// * This program and the accompanying materials are made
// * available under the terms of the Eclipse Public License 2.0
// * which is available at https://www.eclipse.org/legal/epl-2.0/
// *
// * SPDX-License-Identifier: EPL-2.0
// * 
// * Contributors:
// *     Data In Motion - initial API and implementation
// */
//package org.eclipse.fennec.codec.jackson.databind.deser;
//
//import tools.jackson.core.JacksonException;
//import tools.jackson.core.JsonToken;
//import tools.jackson.core.ObjectReadContext;
//import tools.jackson.core.Version;
//import tools.jackson.core.base.ParserBase;
//import tools.jackson.core.exc.InputCoercionException;
//import tools.jackson.core.io.IOContext;
//
///**
// * 
// * @author ilenia
// * @since May 5, 2025
// */
//public class CodecParserWrapper extends CodecParserBaseImpl {
//
//	private ParserBase originalParser;
//
//	public CodecParserWrapper(ParserBase originalParser) {
//		this(originalParser.objectReadContext(), null, originalParser.streamReadFeatures(), originalParser.objectReadContext().getFormatReadFeatures(0));
//		this.originalParser = originalParser;		
//	}
//
//	protected CodecParserWrapper(ObjectReadContext readCtxt, IOContext ctxt, int streamReadFeatures,
//			int formatReadFeatures) {
//		super(readCtxt, ctxt, streamReadFeatures, formatReadFeatures);
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#nextToken()
//	 */
//	@Override
//	public JsonToken nextToken() {
//		originalParser.nextToken();
//		return super.nextToken();
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#closeInput()
//	 */
//	@Override
//	public void closeInput() {
//		originalParser.close();
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isBeginDocument()
//	 */
//	@Override
//	public boolean isBeginDocument() {
//		return originalParser.isExpectedStartObjectToken();
//
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doBeginDocument()
//	 */
//	@Override
//	public void doBeginDocument() {
//
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isEndDocument()
//	 */
//	@Override
//	public boolean isEndDocument() {
//		return originalParser.currentToken() == JsonToken.END_OBJECT;
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doEndDocument()
//	 */
//	@Override
//	public void doEndDocument() {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#isBeginArray()
//	 */
//	@Override
//	public boolean isBeginArray() {
//		return originalParser.isExpectedStartArrayToken();
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doBeginArray()
//	 */
//	@Override
//	public void doBeginArray() {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doEndArray()
//	 */
//	@Override
//	public void doEndArray() {
//
//
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetNextToken()
//	 */
//	@Override
//	public JsonToken doGetNextToken() {
//		return originalParser.nextToken();
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetCurrentToken()
//	 */
//	@Override
//	public JsonToken doGetCurrentToken() {
//		return originalParser.currentToken();
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doReadName()
//	 */
//	@Override
//	public String doReadName() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl#doGetCurrentValue()
//	 */
//	@Override
//	public Object doGetCurrentValue() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.core.base.ParserBase#_parseNumericValue(int)
//	 */
//	@Override
//	protected void _parseNumericValue(int expType) throws JacksonException, InputCoercionException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.core.base.ParserBase#_parseIntValue()
//	 */
//	@Override
//	protected int _parseIntValue() throws JacksonException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.core.JsonParser#version()
//	 */
//	@Override
//	public Version version() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
