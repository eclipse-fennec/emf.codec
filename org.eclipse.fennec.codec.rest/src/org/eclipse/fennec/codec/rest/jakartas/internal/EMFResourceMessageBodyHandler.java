/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.codec.rest.jakartas.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.rest.annotations.AnnotationConverter;
import org.eclipse.fennec.codec.rest.jakartas.JakartaRestConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsApplicationSelect;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsExtension;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsName;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

/**
 * {@link MessageBodyReader} and {@link MessageBodyWriter} that handle {@link Resource}.
 * This readers read and write XMI from a {@link org.eclipse.emf.ecore.resource.Resource}
 * @author Juergen Albert
 * @param <R> the reader type, must be an {@link Resource}
 * @param <W> the writer type, must be an {@link Resource}}
 * @since 30.05.2012
 */
@Component(
		service = {MessageBodyReader.class, MessageBodyWriter.class},
		enabled = true,
		scope = ServiceScope.PROTOTYPE
		)
@JakartarsExtension
@JakartarsName("EMFResourcesMessageBodyReaderWriter")
@JakartarsApplicationSelect("(|(emf=true)("+ JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "=.default))")
@Provider
@Produces(MediaType.WILDCARD)
@Consumes(MediaType.WILDCARD)
public class EMFResourceMessageBodyHandler<R extends Resource, W extends Resource> extends BaseJakartaCodecMessageBodyReaderWriter<R, W>{

	@Context
	private jakarta.inject.Provider<ResourceSet> resourceSetProvider;

	@Context
	private jakarta.inject.Provider<ContainerRequestContext> requestContextProvider;

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return isReadWritable(type, mediaType);
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType)
	 */
	@Override
	public long getSize(W t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return isReadWritable(type, mediaType);
	}

	/**
	 * Checks if the MessageBodyReader / -Writer can be handled
	 * @param type the class type
	 * @param mediaType the media type
	 * @return <code>true</code>, if the MBR/MBW can be used, otherwise <code>false</code>
	 */
	private boolean isReadWritable(Class<?> type, MediaType mediaType) {
		ResourceSet resourceSet = getResourceSet();
		return Resource.class.isAssignableFrom(type) && resourceSet.getResourceFactoryRegistry()
				.getContentTypeToFactoryMap().containsKey(mediaType.getType() + "/" + mediaType.getSubtype());
	}

	/* (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType, jakarta.ws.rs.core.MultivaluedMap, java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public R readFrom(Class<R> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
		return (R) super.readResourceFrom( (Class<Resource>) type, genericType, annotations, mediaType, httpHeaders, entityStream);
	}

	/* (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType, jakarta.ws.rs.core.MultivaluedMap, java.io.OutputStream)
	 */
	@Override
	public void writeTo(W t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		super.writeResourceTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
	}

	@Override
	protected ResourceSet getResourceSet() {
		return resourceSetProvider.get();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getClientCodecOptions() {
		Object value = requestContextProvider.get().getProperty(JakartaRestConstants.CLIENT_CODEC_OPTIONS);
		return value instanceof Map ? (Map<String, Object>) value : Map.of();
	}

	@Reference(unbind = "removeAnnotationConverter", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addAnnotationConverter(AnnotationConverter converter) {
		annotationConverters.add(converter);
	}

	public void removeAnnotationConverter(AnnotationConverter converter) {
		annotationConverters.add(converter);
	}
}
