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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext;
import org.eclipse.fennec.codec.jackson.utils.URIHandler;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * 
 * @author ilenia
 * @since Apr 24, 2025
 */
public class CodecResourceDeserializer extends ValueDeserializer<Resource> {


	   public CodecResourceDeserializer(final URIHandler uriHandler) {
	   }

	   @Override
	   public boolean isCachable() { return true; }

	   @Override
	   public Resource deserialize(final JsonParser jp, final DeserializationContext ctxt)  {
	      return deserialize(jp, ctxt, null);
	   }

	   /* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueDeserializer#deserialize(tools.jackson.core.JsonParser, tools.jackson.databind.DeserializationContext, java.lang.Object)
	 */
	@Override
	   public Resource deserialize(final JsonParser jp, final DeserializationContext ctxt, final Resource resource){
//	      final Resource resource = getResource(ctxt, intoValue);
	     
		  if (resource == null) {
	         throw new IllegalArgumentException("Invalid resource");
	      }
		  if(jp.streamReadContext() instanceof EMFCodecReadContext crc) {
			 crc.setResource(resource);
		  }

	      if (!jp.hasCurrentToken()) {
	    	  jp.nextToken();
	      }

	      ValueDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(ctxt.constructType(EObject.class));

	      if (jp.currentToken() == JsonToken.START_ARRAY) {

	         while (jp.nextToken() != JsonToken.END_ARRAY) {

	            EObject value = (EObject) deserializer.deserialize(jp, ctxt);
	            if (value != null) {
	               resource.getContents().add(value);
	            }
//	            EMFContext.setParent(ctxt, null);
	         }

	      } else if (jp.currentToken() == JsonToken.START_OBJECT) {
	         EObject value = (EObject) deserializer.deserialize(jp, ctxt);
	         if (value != null) {
	            resource.getContents().add(value);
	         }
	      }

	      return resource;
	   }

//	   private Resource getResource(final DeserializationContext context, Resource resource) {
//	      if (resource == null) {
//	         resource = EMFContext.getResource(context);
//
//	         if (resource == null) {
//	            ResourceSet resourceSet = getResourceSet(context);
//	            URI uri = getURI(context);
//	            resource = resourceSet.createResource(uri);
//	            // no factory found for uri
//	            if (resource == null) {
//	               throw new RuntimeException("Cannot create resource for uri " + uri);
//	            }
//	         }
//	      } else {
//	         ResourceSet resourceSet = resource.getResourceSet();
//	         if (resourceSet == null) {
//	            resourceSet = getResourceSet(context);
//	            resourceSet.getResources().add(resource);
//	         }
//
//	         return resource;
//	      }
//
//	      return resource;
//	   }
//
//	   protected ResourceSet getResourceSet(final DeserializationContext context) {
//		  
//	      ResourceSet resourceSet = EMFContext.getResourceSet(context);
//	      if (resourceSet == null) {
//	         resourceSet = new ResourceSetImpl();
//	         context.setAttribute(RESOURCE_SET, resourceSet);
//	      }
//
//	      return resourceSet;
//	   }

//	   private URI getURI(final DeserializationContext ctxt) {
//	      URI uri = EMFContext.getURI(ctxt);
//	      if (uri == null) {
//	         uri = URI.createURI("default");
//	      }
//
//	      return uri;
//	   }

	   @Override
	   public Class<Resource> handledType() {
	      return Resource.class;
	   }

}
