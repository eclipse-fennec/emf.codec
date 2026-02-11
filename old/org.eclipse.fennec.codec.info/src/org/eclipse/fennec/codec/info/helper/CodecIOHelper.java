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
package org.eclipse.fennec.codec.info.helper;


import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.options.CodecValueReaderConstants;
import org.eclipse.fennec.codec.options.CodecValueWriterConstants;
import org.osgi.service.component.annotations.Component;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;

/**
 * 
 * @author ilenia
 * @since Aug 2, 2024
 */
@Component(immediate = true, name = "CodecIOHelper")
public class CodecIOHelper {	

	public static final CodecValueReader<Object, String> DEFAULT_ID_VALUE_READER = new CodecValueReader<>() {

		@Override
		public String getName() {
			return CodecValueReaderConstants.OBJECT_TO_STRING_READER;
		}

		@Override
		public String readValue(Object value, DeserializationContext context) {
			return value.toString();	
		}
	};


	public static final CodecValueWriter<Object, String> DEFAULT_ID_VALUE_WRITER = new CodecValueWriter<>() {
		@Override
		public String getName() {
			return CodecValueWriterConstants.OBJECT_TO_STRING_WRITER;
		}

		@Override
		public String writeValue(Object value, SerializationContext provider) {
			if(value == null) return null;
			return value.toString();
		}
	};


	public static final CodecValueWriter<EClass, String[]> ALL_SUPERTYPE_WRITER = new CodecValueWriter<>() {

		@Override
		public String getName() {
			return CodecValueWriterConstants.ALL_SUPERTYPE_WRITER;
		}

		@Override
		public String[] writeValue(EClass value, SerializationContext provider) {
			return getAllSuperTypeURIs(provider, value);
		}		
	};

	public static final CodecValueWriter<EClass, String[]> SINGLE_SUPERTYPE_WRITER = new CodecValueWriter<>() {

		@Override
		public String getName() {
			return CodecValueWriterConstants.SINGLE_SUPERTYPE_WRITER;
		}

		@Override
		public String[] writeValue(EClass value, SerializationContext provider) {
			String[] superTypesArr = getAllSuperTypeURIs(provider, value);
			if(superTypesArr != null && superTypesArr.length > 0) return new String[] {superTypesArr[superTypesArr.length-1]}; 
			return null;
		}		
	};


	public static final CodecValueWriter<EClass, String> URI_WRITER = new CodecValueWriter<>() {

		@Override
		public String getName() {
			return CodecValueWriterConstants.URI_WRITER;
		}

		@Override
		public String writeValue(EClass value, SerializationContext provider) {
			URI uri = EcoreUtil.getURI(value);
			if(uri == null) return null;
			return uri.toString();
		}		
	};
	
	public static final CodecValueWriter<EClass, String> WRITE_BY_CLASS_NAME = new CodecValueWriter<>() {

		@Override
		public String getName() {
			return CodecValueWriterConstants.WRITER_BY_ECLASS_NAME;
		}

		@Override
		public String writeValue(EClass value, SerializationContext provider) {
			return value != null ? value.getName() : null;
		}		
	};


	public static final CodecValueWriter<EClass, String> WRITE_BY_INSTANCE_CLASS_NAME = new CodecValueWriter<>() {

		@Override
		public String getName() {
			return CodecValueWriterConstants.WRITER_BY_INSTANCE_CLASS_NAME;
		}

		@Override
		public String writeValue(EClass value, SerializationContext provider) {
			return value != null ? value.getInstanceClassName() : null;
		}

	};
	
	public static Set<EClass> getAllTypes(ResourceSet resourceSet) {
		EPackage.Registry global = resourceSet == null ? EPackage.Registry.INSTANCE : resourceSet.getPackageRegistry();
		Map<String, Object> registry = new HashMap<>();
		registry.putAll(global);

		return registry.values().stream()
				.map(e -> {
					if (e instanceof EPackage.Descriptor) {
						return ((EPackage.Descriptor) e).getEPackage();
					} else if (e instanceof EPackage) {						
						return (EPackage) e;
					} else {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.flatMap(e -> stream(spliteratorUnknownSize(e.eAllContents(), ORDERED), false))
				.filter(e -> e instanceof EClass)
				.map(e -> (EClass) e)
				.collect(Collectors.toSet());

	}
	
	
	
	public static EClass findEClassByName(String name, ResourceSet resourceSet) {
		Set<EClass> types = getAllTypes(resourceSet);
		return types.stream().filter(findByName(name)).findFirst().orElse(null);
	}
	
	public static Predicate<EObject> findByURI(final String value) {
		return e -> value != null && e instanceof EClass && EcoreUtil.getURI(e) != null && value.equals(EcoreUtil.getURI(e).toString());
	}

	private static Predicate<EObject> findByName(final String value) {
		return e -> value != null && e instanceof EClass && value.equals(((EClass) e).getName());
	}

	public static Predicate<EObject> findByQualifiedName(final String value) {
		return e -> value != null && e instanceof EClass && value.equals(((EClass) e).getInstanceClassName());
	}

	
	private static String[] getAllSuperTypeURIs(final DatabindContext ctxt, final EObject object) {
		if (object == null) {
			return null;
		}

		if (object instanceof EClass) {
			EClass eclass = (EClass) object;
			return eclass.getEAllSuperTypes().
					stream().
					map(EcoreUtil::getURI).
					map(Object::toString).
					collect(Collectors.toList()).
					toArray(new String[0]);
		}
		return new String[0];
	}

}
