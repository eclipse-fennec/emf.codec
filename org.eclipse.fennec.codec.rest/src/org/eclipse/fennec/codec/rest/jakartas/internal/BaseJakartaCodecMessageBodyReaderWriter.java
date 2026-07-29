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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.rest.annotations.ResourceOverwriteContentType;
import org.eclipse.fennec.codec.rest.common.internal.XMLURIHandler;
import org.eclipse.fennec.codec.rest.jakartas.AbstractJakartaCodecAnnotationHandler;
import org.eclipse.fennec.emf.osgi.model.info.EMFModelInfo;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

/**
 * Base class for codec-backed {@link MessageBodyReader} /
 * {@link MessageBodyWriter} pairs.
 *
 * <p>Subclasses obtain the per-request {@link ResourceSet} from
 * {@link #getResourceSet()}, which delegates to the request-scoped
 * {@code @Context ResourceSet} injection wired up by
 * {@link org.eclipse.fennec.codec.rest.jakartas.feature.CodecResourceSetFeature}.
 * Client-supplied (whitelisted) codec options for the current request are
 * exposed through {@link #getClientCodecOptions()}; the default
 * implementation returns an empty map, subclasses with access to the
 * {@code ContainerRequestContext} override it.
 *
 * @author ilenia
 * @since Mar 18, 2026
 */
public abstract class BaseJakartaCodecMessageBodyReaderWriter<R, W> extends AbstractJakartaCodecAnnotationHandler
		implements MessageBodyReader<R>, MessageBodyWriter<W> {

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	EMFModelInfo modelInfo;

	/**
	 * default constructor
	 */
	public BaseJakartaCodecMessageBodyReaderWriter() {
	}

	/**
	 * @param t
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @param httpHeaders
	 * @param entityStream
	 * @throws IOException
	 * @throws WebApplicationException
	 */
	public void writeResourceTo(Resource t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		try {

			String contentType = determinContentType(mediaType, annotations);
			ResourceSet resourceSet = getResourceSet();
			ResourceFactoryImpl factory = (ResourceFactoryImpl) resourceSet.getResourceFactoryRegistry()
					.getContentTypeToFactoryMap().get(contentType);
			Resource referenceResource = factory.createResource(URI.createURI("http://test.test"));
			resourceSet.getResources().add(referenceResource);
			boolean removeFromResourceSet = true;
			if (t.getClass().equals(referenceResource.getClass())) {
				referenceResource = t;
				removeFromResourceSet = false;
			} else {

				resourceSet.getResources().add(referenceResource);
				for (EObject eObject : t.getContents()) {
					referenceResource.getContents().add(EcoreUtil.copy(eObject));
				}
			}

			HashMap<Object, Object> options = new HashMap<>();
			options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
			options.put(XMLResource.OPTION_URI_HANDLER, new XMLURIHandler(t.getURI()));

			handleAnnotedOptions(annotations, options, resourceSet, true);
			// Client-supplied (whitelisted) options win over endpoint annotations.
			options.putAll(getClientCodecOptions());

			referenceResource.save(entityStream, options);

			if (removeFromResourceSet) {
				referenceResource.getResourceSet().getResources().remove(referenceResource);
			}
		} catch (WebApplicationException wae) {
			throw wae;
		} catch (Exception e) {
			String errorText = String.format("[%s] Error serializing outgoing object. Cause is [%s]", genericType.getTypeName(), e.getCause() != null ? e.getCause().getMessage() : "UNKNOWN");
			Response r = Response.serverError().entity(errorText).type(MediaType.TEXT_PLAIN).build();
			throw new WebApplicationException(e, r);
		}
	}

	/**
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @param httpHeaders
	 * @param entityStream
	 * @return
	 * @throws IOException
	 * @throws WebApplicationException
	 */
	public Resource readResourceFrom(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		try {
			ResourceSet resourceSet = getResourceSet();
			String contentType = determinContentType(mediaType, annotations);
			ResourceFactoryImpl factory = (ResourceFactoryImpl) resourceSet.getResourceFactoryRegistry()
					.getContentTypeToFactoryMap().get(contentType);
			Resource resource = factory.createResource(URI.createURI("temp/id"));
			resourceSet.getResources().add(resource);
			Map<Object, Object> options = new HashMap<>();

			XMLURIHandler xmluriHandler = new XMLURIHandler(resource.getURI());
			options.put(XMLResource.OPTION_URI_HANDLER, xmluriHandler);

			handleAnnotedOptions(annotations, options, resourceSet, false);
			// Client-supplied (whitelisted) options win over endpoint annotations.
			options.putAll(getClientCodecOptions());

			if (!options.containsKey(CodecResource.CODEC_ROOT_TYPE)) {
				modelInfo.getEClassifierForClass(type).ifPresent(ec -> options.put(CodecResource.CODEC_ROOT_TYPE, ec));
			}

			resource.load(entityStream, options);
			checkResourceByAnnotation(resource, annotations);
			return resource;
		} catch (WebApplicationException wae) {
			throw wae;
		} catch (Exception e) {
			String errorText = String.format("[%s] Error de-serializing incoming data. Cause is [%s]", genericType.getTypeName(), e.getCause() != null ? e.getCause().getMessage() : "UNKNOWN");
			Response r = Response.serverError().entity(errorText).type(MediaType.TEXT_PLAIN).build();
			throw new WebApplicationException(e, r);
		}
	}

	/**
	 * Returns the per-request {@link ResourceSet} resolved by the codec's
	 * {@code CodecResourceSetFeature}. Subclasses inject it via
	 * {@code @Context ResourceSet}.
	 */
	protected abstract ResourceSet getResourceSet();

	/**
	 * Returns the whitelisted codec options the client supplied for this
	 * request (set by {@code ClientCodecOptionsFilter}), to be merged into
	 * the load/save options. Defaults to an empty map; subclasses with
	 * access to the {@code ContainerRequestContext} override it.
	 *
	 * @return the client codec options; never {@code null}
	 */
	protected Map<String, Object> getClientCodecOptions() {
		return Map.of();
	}

	private String determinContentType(MediaType mediatype, Annotation[] annotations) {
		return Arrays.asList(annotations).stream().filter(ResourceOverwriteContentType.class::isInstance)
				.map(ResourceOverwriteContentType.class::cast)
				.map(ResourceOverwriteContentType::value).findFirst()
				.orElseGet(() -> mediatype.getType() + "/" + mediatype.getSubtype());

	}

}
