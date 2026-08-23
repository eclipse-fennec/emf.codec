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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
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
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.model.metadata.ClassMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
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
 * @since 1.0
 */
public abstract class BaseJakartaCodecMessageBodyReaderWriter<R, W> extends AbstractJakartaCodecAnnotationHandler
		implements MessageBodyReader<R>, MessageBodyWriter<W> {

	private static final Logger LOGGER = Logger.getLogger(BaseJakartaCodecMessageBodyReaderWriter.class.getName());

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	MetadataService metadataService;

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
			Resource referenceResource = factory.createResource(createTempWriteURI());
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

			try {
				referenceResource.save(entityStream, options);
			} finally {
				if (removeFromResourceSet) {
					resourceSet.getResources().remove(referenceResource);
				}
			}
		} catch (WebApplicationException wae) {
			throw wae;
		} catch (Exception e) {
			// The Jakarta REST runtime treats a WAE with a prepared response as handled and
			// logs nothing, so this is the only place the real cause can surface (issue #165).
			LOGGER.log(Level.SEVERE, String.format("[%s] Error serializing outgoing object as [%s]",
					genericType.getTypeName(), determinContentType(mediaType, annotations)), e);
			// The body stays generic: exception details in the response would leak internals.
			String errorText = String.format("[%s] Error serializing outgoing object", genericType.getTypeName());
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
				resolveRootEClass(metadataService, type)
						.ifPresent(eClass -> options.put(CodecResource.CODEC_ROOT_TYPE, eClass));
			}

			resource.load(entityStream, options);
			checkResourceByAnnotation(resource, annotations);
			return resource;
		} catch (WebApplicationException wae) {
			throw wae;
		} catch (Exception e) {
			// Same reasoning as in writeResourceTo: log the cause here, keep the body generic
			// (issue #165).
			LOGGER.log(Level.SEVERE, String.format("[%s] Error de-serializing incoming data as [%s]",
					genericType.getTypeName(), determinContentType(mediaType, annotations)), e);
			String errorText = String.format("[%s] Error de-serializing incoming data", genericType.getTypeName());
			Response r = Response.serverError().entity(errorText).type(MediaType.TEXT_PLAIN).build();
			throw new WebApplicationException(e, r);
		}
	}

	/**
	 * Resolves the root {@link EClass} for a Java entity type through the metadata index.
	 * <p>
	 * Replaces the deprecated {@code EMFModelInfo}: the index keeps the same mapping, built
	 * from {@code EClass.getInstanceClassName()}, and the metadata service installs one by
	 * default - so no additional service is involved.
	 * </p>
	 * <p>
	 * More than one match means the same Java class is registered for several model versions.
	 * Picking one would be a coin flip between them, so the request fails instead; the caller
	 * disambiguates by passing {@code CODEC_ROOT_TYPE} itself, which takes precedence over
	 * this lookup.
	 * </p>
	 *
	 * Package-visible and static so it can be unit-tested without a JAX-RS runtime.
	 *
	 * @param metadataService the service whose index is consulted
	 * @param type the Java entity type
	 * @return the root EClass, or empty if no registered model version declares this type
	 * @throws WebApplicationException if several model versions declare it
	 */
	static Optional<EClass> resolveRootEClass(MetadataService metadataService, Class<?> type) {
		List<ClassMetadata> matches = metadataService.getIndexReader()
				.map(reader -> reader.findAllByInstanceClassName(type.getName()))
				.orElseGet(List::of);

		if (matches.size() > 1) {
			// nsURI plus fingerprint, the same way PackageResolver reports an ambiguous nsURI: the
			// type URI alone does not tell two registered versions apart.
			String candidates = matches.stream()
					.map(BaseJakartaCodecMessageBodyReaderWriter::describe)
					.collect(Collectors.joining(", "));
			String errorText = String.format(
					"Ambiguous root type for %s: %d registered model versions [%s]; pass %s explicitly to select one",
					type.getName(), matches.size(), candidates, CodecResource.CODEC_ROOT_TYPE);
			throw new WebApplicationException(
					Response.serverError().entity(errorText).type(MediaType.TEXT_PLAIN).build());
		}

		return matches.stream().findFirst().map(ClassMetadata::getEClass);
	}

	/**
	 * Identifies the model version a class belongs to, for the ambiguity message.
	 */
	private static String describe(ClassMetadata classMetadata) {
		PackageMetadata packageMetadata = classMetadata.getPackage();
		if (packageMetadata == null) {
			return String.valueOf(classMetadata.getTypeURI());
		}
		return packageMetadata.getNsURI() + "@" + packageMetadata.getModelFingerprint();
	}

	/**
	 * Creates a unique URI for a temporary write resource. The fixed
	 * {@code http://test.test} used before could collide with another temporary
	 * resource in the same request {@link ResourceSet}, e.g. from the reader
	 * side of the same request (issue #166).
	 *
	 * @return a URI no other resource in the set can carry
	 */
	static URI createTempWriteURI() {
		return URI.createURI("http://codec.temp/" + UUID.randomUUID());
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
