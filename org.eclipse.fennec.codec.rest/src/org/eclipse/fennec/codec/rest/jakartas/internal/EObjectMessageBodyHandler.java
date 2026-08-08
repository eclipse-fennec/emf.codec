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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
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
 * {@link MessageBodyReader} and {@link MessageBodyWriter} that handle {@link EObject}.
 * This readers read and write XMI from a {@link org.eclipse.emf.ecore.resource.Resource}
 * @author Mark Hoffmann
 * @param <R> the reader type, must be an {@link EObject}
 * @param <W> the writer type, must be an {@link EObject}
 * @since 1.0
 */
@Component(
		service = {MessageBodyReader.class, MessageBodyWriter.class},
		enabled = true,
		scope = ServiceScope.PROTOTYPE
	)
@JakartarsExtension
@JakartarsName("EMFEObjectMessagebodyReaderWriter")
@JakartarsApplicationSelect("(|(emf=true)("+ JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "=.default))")
@Provider
@Produces(MediaType.WILDCARD)
@Consumes(MediaType.WILDCARD)
public class EObjectMessageBodyHandler<R extends EObject, W extends EObject> extends BaseJakartaCodecMessageBodyReaderWriter<R, W>{

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
		ResourceSet resourceSet = getResourceSet();
		return EObject.class.isAssignableFrom(type) && resourceSet.getResourceFactoryRegistry()
				.getContentTypeToFactoryMap().containsKey(mediaType.getType() + "/" + mediaType.getSubtype());
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType, jakarta.ws.rs.core.MultivaluedMap, java.io.OutputStream)
	 */
	@Override
	public void writeTo(W t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		ResourceSet resourceSet = getResourceSet();
		Resource resource = t.eResource();
		if(resource == null){
			// The live object is re-parented into the temporary response resource, so it
			// must be detached in a finally: a failed write would otherwise leave it
			// captured there (issue #93). Serializing an EcoreUtil.copy instead is not an
			// option — copying fails for models generated with suppressed notifications,
			// whose many-features are not Setting-implementing lists (issue #94).
			ResourceFactoryImpl factory = (ResourceFactoryImpl) resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().get(mediaType.getType() + "/" + mediaType.getSubtype());
			resource = factory.createResource(URI.createURI("http://test.test"));
			resourceSet.getResources().add(resource);
			resource.getContents().add(t);
			try {
				super.writeResourceTo(resource, Resource.class, genericType, annotations, mediaType, httpHeaders, entityStream);
			} finally {
				resource.getContents().remove(t);
				resourceSet.getResources().remove(resource);
			}
		} else {
			super.writeResourceTo(resource, Resource.class, genericType, annotations, mediaType, httpHeaders, entityStream);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		ResourceSet resourceSet = getResourceSet();
		return EObject.class.isAssignableFrom(type) && resourceSet.getResourceFactoryRegistry()
				.getContentTypeToFactoryMap().containsKey(mediaType.getType() + "/" + mediaType.getSubtype());
	}

	/*
	 * (non-Javadoc)
	 * @see jakarta.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], jakarta.ws.rs.core.MediaType, jakarta.ws.rs.core.MultivaluedMap, java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public R readFrom(Class<R> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		Resource resource = super.readResourceFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);

		if(resource.getContents().size() > 0){
			try {
				R result = (R) resource.getContents().get(0);
				return result;
			} finally {
				resource.getContents().clear();
				ResourceSet rs = resource.getResourceSet();
				rs.getResources().remove(resource);
			}
		}

		return null;
	}

	@Override
	public long getSize(W t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Reference(unbind = "removeAnnotationConverter", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addAnnotationConverter(AnnotationConverter converter) {
		annotationConverters.add(converter);
	}

	public void removeAnnotationConverter(AnnotationConverter converter) {
		annotationConverters.add(converter);
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
}
