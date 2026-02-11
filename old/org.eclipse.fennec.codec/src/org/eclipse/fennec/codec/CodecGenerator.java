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
package org.eclipse.fennec.codec;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.emf.ecore.EObject;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JsonGenerator;

/**
 * 
 * @author mark
 * @since 12.01.2024
 */
public interface CodecGenerator {
	
	JsonGenerator doStartWriteRootEObject(EObject object) ;
	JsonGenerator doEndWriteRootEObject(EObject object) ;

	JsonGenerator doWriteType(int index, String fieldName, Object object) ;
	JsonGenerator doWriteSuperTypes(int index, String fieldName, String[] superTypes) ;
	JsonGenerator doWriteObjectId(int index, String fieldName, Object object) ;
	
	JsonGenerator doWritePropertyId(int index, String fieldName, long value) ;

	JsonGenerator doStartWriteEObject(int index, String fieldName, EObject object) ;
	JsonGenerator doEndWriteEObject(int index, String fieldName, EObject object) ;

	JsonGenerator doStartWriteArray(int index, String fieldName, Object object) ;
	JsonGenerator doEndWriteArray(int index, String fieldName, Object object) ;
	
	JsonGenerator doWriteString(int index, String fieldName, String value) ;
	JsonGenerator doWriteShort(int index, String fieldName, short value) ;
	JsonGenerator doWriteLong(int index, String fieldName, long value) ;
	JsonGenerator doWriteInt(int index, String fieldName, int value) ;
	JsonGenerator doWriteBigInt(int index, String fieldName, BigInteger value) ;
	JsonGenerator doWriteBigDecimal(int index, String fieldName, BigDecimal value) ;
	JsonGenerator doWriteFloat(int index, String fieldName, float value) ;
	JsonGenerator doWriteDouble(int index, String fieldName, double value) ;
	JsonGenerator doWriteChar(int index, String fieldName, char value) ;
	JsonGenerator doWriteChars(int index, String fieldName, char[] values) ;
	JsonGenerator doWriteBoolean(int index, String fieldName, boolean value) ;
	JsonGenerator doWriteStringNumber(int index, String fieldName, String value) ;
	JsonGenerator doWriteBinary(int index, String fieldName, Base64Variant b64variant,
            byte[] values, int offset, int len) ;
	JsonGenerator doWriteNull(int index, String fieldName) ;
}
