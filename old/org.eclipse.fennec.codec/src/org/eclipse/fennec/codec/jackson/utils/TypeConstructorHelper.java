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
package org.eclipse.fennec.codec.jackson.utils;

import static org.eclipse.emf.ecore.EcorePackage.Literals.EBYTE_ARRAY;
import static org.eclipse.emf.ecore.EcorePackage.Literals.EJAVA_CLASS;
import static org.eclipse.emf.ecore.EcorePackage.Literals.EJAVA_OBJECT;

import java.util.Collection;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

/**
 * 
 * @author ilenia
 * @since Apr 25, 2025
 */
public class TypeConstructorHelper {
	
	public static JavaType constructJavaTypeFromFeature(EStructuralFeature feature, DatabindContext ctxt) {
		EGenericType genericType = feature.eClass().getFeatureType(feature);
	    EClassifier realType = genericType.getERawType();
	    JavaType javaType = null;
	    switch(FeatureKind.get(feature)) {
	    case MANY_ATTRIBUTE, MANY_CONTAINMENT, MANY_REFERENCE:
	    	javaType = ctxt.getTypeFactory().constructCollectionType(Collection.class, realType.getInstanceClass());
	    	break;
	    case MAP:
	    	javaType = constructMapType(ctxt.getTypeFactory(), (EClass) realType);
	    	break;
	    default:
	    	if(realType.getInstanceClass() == null) {
	    		Class<?> rawType = rawType(realType);
	    		javaType =  ctxt.getTypeFactory().constructType(rawType);
	    	} else {
	    		javaType = ctxt.getTypeFactory().constructType(realType.getInstanceClass());
	    	}	    	
	    	break;
	    }  
	    return javaType;
	}
	
	 public static JavaType constructReferenceType(final TypeFactory factory, final EClassifier realType) {
	      Class<?> rawType = rawType(realType);
	      return factory.constructReferenceType(rawType, factory.constructType(rawType));
	   }
	
	public static JavaType constructMapType(final TypeFactory factory, final EClass type) {
	      EStructuralFeature key = type.getEStructuralFeature("key");
	      EStructuralFeature value = type.getEStructuralFeature("value");

	      if (key == null || value == null) {
	         return null;
	      }

	      EClassifier keyType = key.getEType();
	      EClassifier valueType = value.getEType();

	      Class<?> keyClass = rawType(keyType);
	      if (String.class.isAssignableFrom(keyClass)) {
	         return factory.constructMapLikeType(EMap.class, keyClass, rawType(valueType));
	      }
	      return factory.constructCollectionType(Collection.class, factory.constructType(EObject.class));
	   }
	
	public static Class<?> rawType(final EClassifier classifier) {
	      Class<?> rawType = classifier.getInstanceClass();

	      if (classifier instanceof EDataType) {
	         if (rawType == null || classifier == EJAVA_CLASS || classifier == EJAVA_OBJECT || classifier == EBYTE_ARRAY) {
	            rawType = EDataType.class;
	         }
	      } else if (rawType == null) {
	         rawType = EObject.class;
	      }

	      return rawType;
	   }

}
