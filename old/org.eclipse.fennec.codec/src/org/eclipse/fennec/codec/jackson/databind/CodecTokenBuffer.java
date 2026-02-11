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
package org.eclipse.fennec.codec.jackson.databind;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.fennec.codec.jackson.databind.deser.CodecParserBaseImpl;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.Version;
import tools.jackson.core.base.ParserMinimalBase;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.io.NumberInput;
import tools.jackson.core.io.NumberOutput;
import tools.jackson.core.sym.PropertyNameMatcher;
import tools.jackson.core.util.ByteArrayBuilder;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.databind.util.ClassUtil;
import tools.jackson.databind.util.TokenBuffer;

/**
 * 
 * @author ilenia
 * @since Jul 8, 2025
 */
public class CodecTokenBuffer extends TokenBuffer {
	/**
	 * Creates a new instance.
	 * @param p
	 * @param ctxt
	 */
	protected CodecTokenBuffer(JsonParser p, ObjectReadContext ctxt) {
		super(p, ctxt);	
	}
	
	protected CodecTokenBuffer(TokenStreamContext parentContext) {
		super(ObjectWriteContext.empty(), false);
		_parentContext = parentContext;
	}
	
    public static CodecTokenBuffer forBuffering(JsonParser p, ObjectReadContext ctxt) {
        return new CodecTokenBuffer(p, ctxt);
    }
    
    public static CodecTokenBuffer forBuffering(TokenStreamContext parentContext) {
    	return new CodecTokenBuffer(parentContext);
    	
    }
    
    public static CodecTokenBuffer forGeneration() {
    	return new CodecTokenBuffer(false);
    }
    
    public CodecTokenBuffer(boolean hasNativeIds)
    {
    	super(ObjectWriteContext.empty(), hasNativeIds);
    }
    
    /* 
     * (non-Javadoc)
     * @see tools.jackson.databind.util.TokenBuffer#asParser()
     */
    @Override
    public JsonParser asParser() {
    	return new Parser(ObjectReadContext.empty(), this,
                _first, _hasNativeTypeIds, _hasNativeObjectIds,
                _parentContext, _streamReadConstraints);
    }
    
    public JsonParser asParser(ObjectReadContext readCtxt)
    {
        return new Parser(readCtxt, this,
                _first, _hasNativeTypeIds, _hasNativeObjectIds,
                _parentContext, _streamReadConstraints);
    }


    /* 
     * (non-Javadoc)
     * @see tools.jackson.databind.util.TokenBuffer#asParser(tools.jackson.core.ObjectReadContext, tools.jackson.core.JsonParser)
     */
    @Override
    public JsonParser asParser(ObjectReadContext readCtxt, JsonParser p0)
    {
        StreamReadConstraints src = (p0 == null)
                ? _streamReadConstraints : p0.streamReadConstraints();
        Parser p = new Parser(readCtxt, this,
                _first, _hasNativeTypeIds, _hasNativeObjectIds,
                _parentContext, src);
        if (p0 != null) {
            p.setLocation(p0.currentTokenLocation());
        }
        return p;
    }

   
    /* 
     * (non-Javadoc)
     * @see tools.jackson.databind.util.TokenBuffer#asParserOnFirstToken(tools.jackson.core.ObjectReadContext)
     */
    @Override
    public JsonParser asParserOnFirstToken(ObjectReadContext readCtxt)
        throws JacksonException
    {
        JsonParser p = asParser(readCtxt);
        p.nextToken();
        return p;
    }

    /* 
     * (non-Javadoc)
     * @see tools.jackson.databind.util.TokenBuffer#asParserOnFirstToken(tools.jackson.core.ObjectReadContext, tools.jackson.core.JsonParser)
     */
    @Override
    public JsonParser asParserOnFirstToken(ObjectReadContext readCtxt,
            JsonParser src) throws JacksonException
    {
        JsonParser p = asParser(readCtxt, src);
        p.nextToken();
        return p;
    }
    
    public void writeObject(Object obj) {
    	if(obj instanceof String str) writeString(str);
    	else if(obj instanceof Integer num) writeNumber(num);
    	else if(obj instanceof Double num) writeNumber(num);
    	else if(obj instanceof BigInteger num) writeNumber(num);
    	else if(obj instanceof BigDecimal num) writeNumber(num);
    	else if(obj instanceof Float num) writeNumber(num);
    	else if(obj instanceof byte[] binary) writeBinary(binary);
    }
    
    /* 
     * (non-Javadoc)
     * @see tools.jackson.databind.util.TokenBuffer#copyCurrentEvent(tools.jackson.core.JsonParser)
     */
    @Override
    public void copyCurrentEvent(JsonParser p)
    {
    	if ((_typeId = p.getTypeId()) != null) {
            _hasNativeId = true;
        }
        if ((_objectId = p.getObjectId()) != null) {
            _hasNativeId = true;
        }
        switch (p.currentToken()) {
        case START_OBJECT:
            writeStartObject();
            break;
        case END_OBJECT:
            writeEndObject();
            break;
        case START_ARRAY:
            writeStartArray();
            break;
        case END_ARRAY:
            writeEndArray();
            break;
        case PROPERTY_NAME:
            writeName(p.currentName());
            break;
        case VALUE_STRING:
            if (p.hasStringCharacters()) {
                writeString(p.getStringCharacters(), p.getStringOffset(), p.getStringLength());
            } else {         
            	if(p.currentValue() instanceof String) {
            		writeString(p.getString());
            	} else if (p.currentValue() instanceof byte[]){
            		writeBinary(p.getBinaryValue());
            	} else {
            		if(p instanceof CodecParserBaseImpl codecParser) {
            			writeObject(codecParser.getStringValueObject()); //needed to properly store byte[] in mongo
            		} else {
            			writeString(p.getString());
            		}
            	}
            }
            break;
        case VALUE_NUMBER_INT:
            switch (p.getNumberType()) {
            case INT:
                writeNumber(p.getIntValue());
                break;
            case BIG_INTEGER:
            	writeNumber(p.getBigIntegerValue());
                break;
            default:
                writeNumber(p.getLongValue());
            }
            break;
        case VALUE_NUMBER_FLOAT:
        	switch(p.getNumberType()) {
        	case BIG_DECIMAL:
        		writeNumber(p.getDecimalValue());
        		break;        	
        	case FLOAT:
        		writeNumber(p.getFloatValue());
        		break;
        	case DOUBLE: default:
        		writeNumber(p.getDoubleValue());
        		break;
        	}
            break;
        case VALUE_TRUE:
            writeBoolean(true);
            break;
        case VALUE_FALSE:
            writeBoolean(false);
            break;
        case VALUE_NULL:
            writeNull();
            break;
        case VALUE_EMBEDDED_OBJECT:
            writePOJO(p.getEmbeddedObject());
            break;
        default:
            throw new RuntimeException("Internal error: unexpected token: "+p.currentToken());
        }
    }
    
    
    
    /*
    /**********************************************************************
    /* Supporting classes
    /**********************************************************************
     */

    public final static class Parser
        extends ParserMinimalBase
    {
        /*
        /******************************************************************
        /* Configuration
        /******************************************************************
         */

        protected StreamReadConstraints _streamReadConstraints;

        protected final TokenBuffer _source;

        protected final boolean _hasNativeTypeIds;

        protected final boolean _hasNativeObjectIds;

        protected final boolean _hasNativeIds;

        /*
        /******************************************************************
        /* Parsing state
        /******************************************************************
         */

        /**
         * Currently active segment
         */
        protected Segment _segment;

        /**
         * Pointer to current token within current segment
         */
        protected int _segmentPtr;

        /**
         * Information about parser context, context in which
         * the next token is to be parsed (root, array, object).
         */
        protected CodecTokenBufferReadContext _parsingContext;

        protected boolean _closed;

        protected transient ByteArrayBuilder _byteBuilder;

        protected TokenStreamLocation _location = null;

        /*
        /******************************************************************
        /* Construction, init
        /******************************************************************
         */

        public Parser(ObjectReadContext readCtxt, TokenBuffer source,
                Segment firstSeg, boolean hasNativeTypeIds, boolean hasNativeObjectIds,
                TokenStreamContext parentContext,
                StreamReadConstraints streamReadConstraints)
        {
            // 25-Jun-2022, tatu: This should pass stream read features as
            //    per [databind#3528]) but for now at very least should get
            //    sane defaults
            super(readCtxt);
            _source = source;
            _segment = firstSeg;
            _segmentPtr = -1; // not yet read
            _streamReadConstraints = streamReadConstraints;
            _parsingContext = CodecTokenBufferReadContext.createRootContext(parentContext);
            _hasNativeTypeIds = hasNativeTypeIds;
            _hasNativeObjectIds = hasNativeObjectIds;
            _hasNativeIds = (hasNativeTypeIds || hasNativeObjectIds);
        }

        public void setLocation(TokenStreamLocation l) {
            _location = l;
        }

        /*
        /**********************************************************
        /* Public API, config access, capability introspection
        /**********************************************************
         */

        @Override
        public Version version() {
            return tools.jackson.databind.cfg.PackageVersion.VERSION;
        }

        // 20-May-2020, tatu: This may or may not be enough -- ideally access is
        //    via `DeserializationContext`, not parser, but if latter is needed
        //    then we'll need to pass this from parser contents if which were
        //    buffered.
        @Override
        public JacksonFeatureSet<StreamReadCapability> streamReadCapabilities() {
            return DEFAULT_READ_CAPABILITIES;
        }

        @Override
        public TokenBuffer streamReadInputSource() {
            return _source;
        }

        @Override
        public StreamReadConstraints streamReadConstraints() {
            return _streamReadConstraints;
        }

        /*
        /******************************************************************
        /* Extended API beyond JsonParser
        /******************************************************************
         */

        public JsonToken peekNextToken()
        {
            // closed? nothing more to peek, either
            if (_closed) return null;
            Segment seg = _segment;
            int ptr = _segmentPtr+1;
            if (ptr >= Segment.TOKENS_PER_SEGMENT) {
                ptr = 0;
                seg = (seg == null) ? null : seg.next();
            }
            return (seg == null) ? null : seg.type(ptr);
        }

        /*
        /******************************************************************
        /* Closing, related
        /******************************************************************
         */

        @Override
        public void close() {
            _closed = true;
        }

        @Override
        protected void _closeInput() throws IOException { }

        @Override
        protected void _releaseBuffers() { }

        /*
        /******************************************************************
        /* Public API, traversal
        /******************************************************************
         */

        @Override
        public JsonToken nextToken()
        {
            // If we are closed, nothing more to do
            if (_closed || (_segment == null)) {
                _updateTokenToNull();
                return null;
            }

            // Ok, then: any more tokens?
            if (++_segmentPtr >= Segment.TOKENS_PER_SEGMENT) {
                _segmentPtr = 0;
                _segment = _segment.next();
                if (_segment == null) {
                    _updateTokenToNull();
                    return null;
                }
            }
            _updateToken(_segment.type(_segmentPtr));
            // Property name? Need to update context
            if (_currToken == JsonToken.PROPERTY_NAME) {
                Object ob = _currentObject();
                String name = (ob instanceof String) ? ((String) ob) : ob.toString();
                _parsingContext.setCurrentName(name);
            } else if (_currToken == JsonToken.START_OBJECT) {
                _parsingContext = _parsingContext.createChildObjectContext();
            } else if (_currToken == JsonToken.START_ARRAY) {
                _parsingContext = _parsingContext.createChildArrayContext();
            } else if (_currToken == JsonToken.END_OBJECT
                    || _currToken == JsonToken.END_ARRAY) {
                // Closing JSON Object/Array? Close matching context
                _parsingContext = _parsingContext.parentOrCopy();
            } else {
                _parsingContext.updateForValue();
            }
            return _currToken;
        }

        @Override
        public String nextName()
        {
            // inlined common case from nextToken()
            if (_closed || (_segment == null)) {
                return null;
            }

            int ptr = _segmentPtr+1;
            if ((ptr < Segment.TOKENS_PER_SEGMENT) && (_segment.type(ptr) == JsonToken.PROPERTY_NAME)) {
                _segmentPtr = ptr;
                _updateToken(JsonToken.PROPERTY_NAME);
                Object ob = _segment.get(ptr); // inlined _currentObject();
                String name = (ob instanceof String) ? ((String) ob) : ob.toString();
                _parsingContext.setCurrentName(name);
                return name;
            }
            return (nextToken() == JsonToken.PROPERTY_NAME) ? currentName() : null;
        }

        // NOTE: since we know there's no native matching just use simpler way:
        @Override // since 3.0
        public int nextNameMatch(PropertyNameMatcher matcher) {
            String str = nextName();
            if (str != null) {
                // 15-Nov-2017, tatu: Can not assume name given is intern()ed
                return matcher.matchName(str);
            }
            if (hasToken(JsonToken.END_OBJECT)) {
                return PropertyNameMatcher.MATCH_END_OBJECT;
            }
            return PropertyNameMatcher.MATCH_ODD_TOKEN;
        }

        @Override
        public boolean isClosed() { return _closed; }

        /*
        /******************************************************************
        /* Public API, token accessors
        /******************************************************************
         */

        @Override public TokenStreamContext streamReadContext() { return _parsingContext; }
        @Override public void assignCurrentValue(Object v) { _parsingContext.assignCurrentValue(v); }
        @Override public Object currentValue() { return _parsingContext.currentValue(); }

        @Override
        public TokenStreamLocation currentTokenLocation() { return currentLocation(); }

        @Override
        public TokenStreamLocation currentLocation() {
            return (_location == null) ? TokenStreamLocation.NA : _location;
        }

        @Override
        public String currentName() {
            // 25-Jun-2015, tatu: as per [databind#838], needs to be same as ParserBase
            if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
                TokenStreamContext parent = _parsingContext.getParent();
                return parent.currentName();
            }
            return _parsingContext.currentName();
        }

        /*
        /******************************************************************
        /* Public API, access to token information, text
        /******************************************************************
         */

        @Override
        public String getString()
        {
            // common cases first:
            if (_currToken == JsonToken.VALUE_STRING
                    || _currToken == JsonToken.PROPERTY_NAME) {
                Object ob = _currentObject();
                if (ob instanceof String) {
                    return (String) ob;
                }
                return ClassUtil.nullOrToString(ob);
            }
            if (_currToken == null) {
                return null;
            }
            switch (_currToken) {
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return ClassUtil.nullOrToString(_currentObject());
            default:
            	return _currToken.asString();
            }
        }

        @Override
        public char[] getStringCharacters() {
            String str = getString();
            return (str == null) ? null : str.toCharArray();
        }

        @Override
        public int getStringLength() {
            String str = getString();
            return (str == null) ? 0 : str.length();
        }

        @Override
        public int getStringOffset() { return 0; }

        @Override
        public boolean hasStringCharacters() {
            // We never have raw buffer available, so:
            return false;
        }

        /*
        /******************************************************************
        /* Public API, access to token information, numeric
        /******************************************************************
         */

        @Override
        public boolean isNaN() {
            // can only occur for floating-point numbers
            if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
                Object value = _currentObject();
                if (value instanceof Double) {
                    return NumberOutput.notFinite((Double) value);
                }
                if (value instanceof Float) {
                    return NumberOutput.notFinite((Float) value);
                }
            }
            return false;
        }

        @Override
        public BigInteger getBigIntegerValue()
        {
            Number n = _numberValue(NR_BIGINT, true);
            if (n instanceof BigInteger) {
                return (BigInteger) n;
            } else if (n instanceof BigDecimal) {
                final BigDecimal bd = (BigDecimal) n;
                streamReadConstraints().validateBigIntegerScale(bd.scale());
                return bd.toBigInteger();
            }
            // int/long is simple, but let's also just truncate float/double:
            return BigInteger.valueOf(n.longValue());
        }

        @Override
        public BigDecimal getDecimalValue()
        {
            Number n = _numberValue(NR_BIGDECIMAL, true);
            if (n instanceof BigDecimal) {
                return (BigDecimal) n;
            } else if (n instanceof Integer) {
                return BigDecimal.valueOf(n.intValue());
            } else if (n instanceof Long) {
                return BigDecimal.valueOf(n.longValue());
            } else if (n instanceof BigInteger) {
                return new BigDecimal((BigInteger) n);
            }
            // float or double
            return BigDecimal.valueOf(n.doubleValue());
        }

        @Override
        public double getDoubleValue() {
            return _numberValue(NR_DOUBLE, false).doubleValue();
        }

        @Override
        public float getFloatValue() {
            return _numberValue(NR_FLOAT, false).floatValue();
        }

        @Override
        public int getIntValue()
        {
            final Number n = _numberValue(NR_INT, false);
            if ((n instanceof Integer) || _smallerThanInt(n)) {
                return n.intValue();
            }
            return _convertNumberToInt(n);
        }

        @Override
        public long getLongValue() {
            final Number n = _numberValue(NR_LONG, false);
            if ((n instanceof Long) || _smallerThanLong(n)) {
                return n.longValue();
            }
            return _convertNumberToLong(n);
        }

        @Override
        public NumberType getNumberType()
        {
            // 2021-01-12, tatu: Avoid throwing exception by not calling accessor
            if (_currToken == null) {
                return null;
            }
            final Object value = _currentObject();
            if (value instanceof Number) {
                Number n = (Number) value;
                if (n instanceof Integer) return NumberType.INT;
                if (n instanceof Long) return NumberType.LONG;
                if (n instanceof Double) return NumberType.DOUBLE;
                if (n instanceof BigDecimal) return NumberType.BIG_DECIMAL;
                if (n instanceof BigInteger) return NumberType.BIG_INTEGER;
                if (n instanceof Float) return NumberType.FLOAT;
                if (n instanceof Short) return NumberType.INT;       // should be SHORT
            } else if (value instanceof String) {
                return (_currToken == JsonToken.VALUE_NUMBER_FLOAT)
                        ? NumberType.BIG_DECIMAL : NumberType.BIG_INTEGER;
            }
            return null;
        }

        @Override
        public NumberTypeFP getNumberTypeFP()
        {
            if (_currToken == JsonToken.VALUE_NUMBER_FLOAT) {
                Object n = _currentObject();
                if (n instanceof Double) return NumberTypeFP.DOUBLE64;
                if (n instanceof BigDecimal) return NumberTypeFP.BIG_DECIMAL;
                if (n instanceof Float) return NumberTypeFP.FLOAT32;
            }
            return NumberTypeFP.UNKNOWN;
        }

        @Override
        public final Number getNumberValue() {
            return _numberValue(-1, false);
        }

        @Override
        public Object getNumberValueDeferred() {
            // Former "_checkIsNumber()"
            if (_currToken == null || !_currToken.isNumeric()) {
                throw _constructNotNumericType(_currToken, 0);
            }
            return _currentObject();
        }

        private Number _numberValue(final int targetNumType, final boolean preferBigNumbers) {
            // Former "_checkIsNumber()"
            if (_currToken == null || !_currToken.isNumeric()) {
                throw _constructNotNumericType(_currToken, targetNumType);
            }
            Object value = _currentObject();
            if (value instanceof Number) {
                return (Number) value;
            }
            // Difficult to really support numbers-as-Strings; but let's try.
            // NOTE: no access to DeserializationConfig, unfortunately, so cannot
            // try to determine Double/BigDecimal preference...

            // 12-Jan-2021, tatu: Is this really needed, and for what? CSV, XML?
            if (value instanceof String) {
                String str = (String) value;
                final int len = str.length();
                if (_currToken == JsonToken.VALUE_NUMBER_INT) {
                    // 08-Dec-2023, tatu: Note -- deferred numbers' validity (wrt input token)
                    //    has been verified by underlying `JsonParser`: no need to check again
                    if (preferBigNumbers
                            // 01-Feb-2023, tatu: Not really accurate but we'll err on side
                            //   of not losing accuracy (should really check 19-char case,
                            //   or, with minus sign, 20-char)
                            || (len >= 19)) {
                        return NumberInput.parseBigInteger(str, isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                    }
                    // Otherwise things get trickier; here, too, we should use more accurate
                    // boundary checks
                    if (len >= 10) {
                        return NumberInput.parseLong(str);
                    }
                    return NumberInput.parseInt(str);
                }
                if (preferBigNumbers) {
                    BigDecimal dec = NumberInput.parseBigDecimal(str,
                            isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                    // 01-Feb-2023, tatu: This is... weird. Seen during tests, only
                    if (dec == null) {
                        throw new IllegalStateException("Internal error: failed to parse number '"+str+"'");
                    }
                    return dec;
                }
                return NumberInput.parseDouble(str, isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
            }
            throw new IllegalStateException("Internal error: entry should be a Number, but is of type "
                    +ClassUtil.classNameOf(value));
        }

        private final boolean _smallerThanInt(Number n) {
            return (n instanceof Short) || (n instanceof Byte);
        }

        private final boolean _smallerThanLong(Number n) {
            return (n instanceof Integer) || (n instanceof Short) || (n instanceof Byte);
        }

        // 02-Jan-2017, tatu: Modified from method(s) in `ParserBase`

        protected int _convertNumberToInt(Number n) throws InputCoercionException
        {
            if (n instanceof Long) {
                long l = n.longValue();
                int result = (int) l;
                if (((long) result) != l) {
                    _reportOverflowInt();
                }
                return result;
            }
            if (n instanceof BigInteger) {
                BigInteger big = (BigInteger) n;
                if (BI_MIN_INT.compareTo(big) > 0
                        || BI_MAX_INT.compareTo(big) < 0) {
                    _reportOverflowInt();
                }
            } else if ((n instanceof Double) || (n instanceof Float)) {
                double d = n.doubleValue();
                // Need to check boundaries
                if (d < MIN_INT_D || d > MAX_INT_D) {
                    _reportOverflowInt();
                }
                return (int) d;
            } else if (n instanceof BigDecimal) {
                BigDecimal big = (BigDecimal) n;
                if (BD_MIN_INT.compareTo(big) > 0
                    || BD_MAX_INT.compareTo(big) < 0) {
                    _reportOverflowInt();
                }
            } else {
                _throwInternal();
            }
            return n.intValue();
        }

        protected long _convertNumberToLong(Number n) throws InputCoercionException
        {
            if (n instanceof BigInteger) {
                BigInteger big = (BigInteger) n;
                if (BI_MIN_LONG.compareTo(big) > 0
                        || BI_MAX_LONG.compareTo(big) < 0) {
                    _reportOverflowLong();
                }
            } else if ((n instanceof Double) || (n instanceof Float)) {
                double d = n.doubleValue();
                // Need to check boundaries
                if (d < MIN_LONG_D || d > MAX_LONG_D) {
                    _reportOverflowLong();
                }
                return (long) d;
            } else if (n instanceof BigDecimal) {
                BigDecimal big = (BigDecimal) n;
                if (BD_MIN_LONG.compareTo(big) > 0
                    || BD_MAX_LONG.compareTo(big) < 0) {
                    _reportOverflowLong();
                }
            } else {
                _throwInternal();
            }
            return n.longValue();
        }

        /*
        /******************************************************************
        /* Public API, access to token information, other
        /******************************************************************
         */

        @Override
        public Object getEmbeddedObject()
        {
            if (_currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                return _currentObject();
            }
            return null;
        }

        @Override
        public byte[] getBinaryValue(Base64Variant b64variant) throws JacksonException
        {
            // First: maybe we some special types?
            if (_currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                // Embedded byte array would work nicely...
                Object ob = _currentObject();
                if (ob instanceof byte[]) {
                    return (byte[]) ob;
                }
                // fall through to error case
            }
            if (_currToken != JsonToken.VALUE_STRING) {
                throw _constructReadException("Current token ("+_currToken+") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), cannot access as binary");
            }
            final String str = getString();
            if (str == null) {
                return null;
            }
            ByteArrayBuilder builder = _byteBuilder;
            if (builder == null) {
                _byteBuilder = builder = new ByteArrayBuilder(100);
            } else {
                _byteBuilder.reset();
            }
            
            _decodeBase64(str, builder, b64variant);
            return builder.toByteArray();
        }

        @Override
        public int readBinaryValue(Base64Variant b64variant, OutputStream out)
            throws JacksonException
        {
            byte[] data = getBinaryValue(b64variant);
            if (data != null) {
                try {
                    out.write(data, 0, data.length);
                } catch (IOException e) {
                    throw _wrapIOFailure(e);
                }
                return data.length;
            }
            return 0;
        }

        /*
        /******************************************************************
        /* Public API, native ids
        /******************************************************************
         */

        @Override
        public boolean canReadObjectId() {
            return _hasNativeObjectIds;
        }

        @Override
        public boolean canReadTypeId() {
            return _hasNativeTypeIds;
        }

        @Override
        public Object getTypeId() {
            return _segment.findTypeId(_segmentPtr);
        }

        @Override
        public Object getObjectId() {
            return _segment.findObjectId(_segmentPtr);
        }

        /*
        /******************************************************************
        /* Internal methods
        /******************************************************************
         */

        protected final Object _currentObject() {
            return _segment.get(_segmentPtr);
        }

        @Override
        protected void _handleEOF() {
            _throwInternal();
        }
        
  
    }
}
