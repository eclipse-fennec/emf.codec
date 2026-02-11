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
package org.eclipse.fennec.codec.jackson.databind.deser;

import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.CodecEMFDeserializers;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.jackson.utils.URIHandler;
import org.osgi.service.component.annotations.Component;

import tools.jackson.databind.BeanDescription.Supplier;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.Deserializers;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapLikeType;
import tools.jackson.databind.type.ReferenceType;

/**
 * 
 * @author ilenia
 * @since Apr 23, 2025
 */
@Component(immediate = true, name = "DefaultCodecEMFDeserializers", service = CodecEMFDeserializers.class)
public class DefaultCodecEMFDeserializers extends Deserializers.Base implements CodecEMFDeserializers{

	protected ValueDeserializer<EObject> referenceDeserializer;
	protected ValueDeserializer<EList<Map.Entry<?, ?>>> mapDeserializer;
	protected ValueDeserializer<Enumerator> enumDeserializer;
	protected CodecResourceDeserializer resourceDeserializer;
	protected URIHandler handler;
	protected CodecModelInfo codecModelInfoService;
	protected CodecModule module;


//	public CodecEMFDeserializers(CodecModule module) {
//		this.module = module;
//		this.codecModelInfoService = module.getCodecModelInfoService();
//		this.referenceDeserializer = module.getReferenceDeserializer();
//		this.mapDeserializer = new EMapDeserializer();
//		this.handler = module.getUriHandler();
//		this.enumDeserializer = new EnumDeserializer(module);
//		this.resourceDeserializer = new CodecResourceDeserializer(module.getUriHandler());
//
//	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecEMFDeserializers#bindCodecModule(org.eclipse.fennec.codec.jackson.module.CodecModule)
	 */
	public void bindCodecModule(CodecModule module) {
		this.module = module;
		this.codecModelInfoService = module.getCodecModelInfoService();
		this.referenceDeserializer = module.getReferenceDeserializer();
		this.mapDeserializer = new EMapDeserializer();
		this.handler = module.getUriHandler();
		this.enumDeserializer = new EnumDeserializer(module);
		this.resourceDeserializer = new CodecResourceDeserializer(module.getUriHandler());
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.deser.Deserializers.Base#findCollectionDeserializer(tools.jackson.databind.type.CollectionType, tools.jackson.databind.DeserializationConfig, tools.jackson.databind.BeanDescription.Supplier, tools.jackson.databind.jsontype.TypeDeserializer, tools.jackson.databind.ValueDeserializer)
	 */
	@Override
	public ValueDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config,
			Supplier beanDescRef, TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) {
		if (type.getContentType().isTypeOrSubTypeOf(EObject.class)) {
			return new CodecCollectionDeserializer(type, new CodecEObjectDeserializer(type.getContentType().getRawClass(), module, codecModelInfoService),
					referenceDeserializer);
		}
		return super.findCollectionDeserializer(type, config, beanDescRef, elementTypeDeserializer, elementDeserializer);
	}
	

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.deser.Deserializers.Base#findBeanDeserializer(tools.jackson.databind.JavaType, tools.jackson.databind.DeserializationConfig, tools.jackson.databind.BeanDescription.Supplier)
	 */
	@Override
	public ValueDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
			Supplier beanDescRef) {
		if (type.isTypeOrSubTypeOf(Resource.class)) {
			return resourceDeserializer;
		}

		if (type.isReferenceType()) {
			return referenceDeserializer;
		}

		if (type.isTypeOrSubTypeOf(EObject.class)) {
			return new CodecEObjectDeserializer(type.getRawClass(), module, codecModelInfoService);
		}
		return super.findBeanDeserializer(type, config, beanDescRef);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.deser.Deserializers.Base#findMapLikeDeserializer(tools.jackson.databind.type.MapLikeType, tools.jackson.databind.DeserializationConfig, tools.jackson.databind.BeanDescription.Supplier, tools.jackson.databind.KeyDeserializer, tools.jackson.databind.jsontype.TypeDeserializer, tools.jackson.databind.ValueDeserializer)
	 */
	@Override
	public ValueDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config,
			Supplier beanDescRef, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer,
			ValueDeserializer<?> elementDeserializer) {
		if (type.isTypeOrSubTypeOf(EMap.class)) {
			return mapDeserializer;
		}
		return super.findMapLikeDeserializer(type, config, beanDescRef, keyDeserializer, elementTypeDeserializer,
				elementDeserializer);
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.deser.Deserializers#findEnumDeserializer(tools.jackson.databind.JavaType, tools.jackson.databind.DeserializationConfig, tools.jackson.databind.BeanDescription.Supplier)
	 */
	@Override
	public ValueDeserializer<?> findEnumDeserializer(JavaType type, DeserializationConfig config,
			Supplier beanDescRef) {
		if (Enumerator.class.isAssignableFrom(type.getRawClass())) {
			return enumDeserializer;
		}
		return super.findEnumDeserializer(type, config, beanDescRef);
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.deser.Deserializers.Base#findReferenceDeserializer(tools.jackson.databind.type.ReferenceType, tools.jackson.databind.DeserializationConfig, tools.jackson.databind.BeanDescription.Supplier, tools.jackson.databind.jsontype.TypeDeserializer, tools.jackson.databind.ValueDeserializer)
	 */
	@Override
	public ValueDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config,
			Supplier beanDescRef, TypeDeserializer contentTypeDeserializer, ValueDeserializer<?> contentDeserializer) {
		if (referenceDeserializer != null) {
	         return referenceDeserializer;
	      }
		return super.findReferenceDeserializer(refType, config, beanDescRef, contentTypeDeserializer, contentDeserializer);
	}
	  


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.deser.Deserializers#hasDeserializerFor(tools.jackson.databind.DeserializationConfig, java.lang.Class)
	 */
	@Override
	public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
		// TODO Auto-generated method stub
		return false;
	}

}
