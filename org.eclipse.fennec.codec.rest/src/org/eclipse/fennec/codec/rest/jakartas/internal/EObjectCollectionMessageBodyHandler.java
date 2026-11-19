/**
 * Copyright (c) 2026 Data In Motion and others.
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.rest.annotations.AnnotationConverter;
import org.eclipse.fennec.codec.rest.jakartas.JakartaRestConstants;
import org.eclipse.fennec.emf.osgi.helper.XMLResourceIDs;
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
 * {@link MessageBodyReader} and {@link MessageBodyWriter} for a {@link Collection} of
 * {@link EObject}s, e.g. a resource method returning {@code List<Booking>} (issue #334).
 * <p>
 * The element type is taken from the generic type, so the handler only claims collections that
 * are declared as collections of {@code EObject}s: a method's return type, or a
 * {@link jakarta.ws.rs.core.GenericEntity} around a {@code Response} entity. A raw collection says
 * nothing about its elements and is left to other providers.
 * </p>
 * <p>
 * The collection is written as the roots of one document, as an array in formats that have one -
 * whatever its size, so a client can always read an array (save option
 * {@link CodecOptions#CODEC_ROOT_AS_ARRAY}). An element that lives in a resource is not
 * re-parented: all such elements are copied together, so references between them stay inside the
 * document, with their {@code xmi:id}s. A detached element is written live and detached again
 * afterwards, as {@link EObjectMessageBodyHandler} does for a single one.
 * </p>
 */
@Component(
		service = {MessageBodyReader.class, MessageBodyWriter.class},
		enabled = true,
		scope = ServiceScope.PROTOTYPE
	)
@JakartarsExtension
@JakartarsName("EMFEObjectCollectionMessagebodyReaderWriter")
@JakartarsApplicationSelect("(|(emf=true)("+ JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "=.default))")
@Provider
@Produces(MediaType.WILDCARD)
@Consumes(MediaType.WILDCARD)
public class EObjectCollectionMessageBodyHandler
		extends BaseJakartaCodecMessageBodyReaderWriter<Collection<EObject>, Collection<? extends EObject>> {

	@Context
	private jakarta.inject.Provider<ResourceSet> resourceSetProvider;

	@Context
	private jakarta.inject.Provider<ContainerRequestContext> requestContextProvider;

	/**
	 * Default constructor, also used by the component runtime.
	 */
	public EObjectCollectionMessageBodyHandler() {
		annotationConverters = new CopyOnWriteArrayList<>();
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Collection.class.isAssignableFrom(type) && elementType(genericType).isPresent()
				&& supportsMediaType(mediaType);
	}

	@Override
	public void writeTo(Collection<? extends EObject> collection, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		ResourceSet resourceSet = getResourceSet();
		Resource document = factoryFor(mediaType).createResource(createTempWriteURI());
		resourceSet.getResources().add(document);
		List<EObject> live = new ArrayList<>();
		try {
			// One copier for all elements that have a resource: a reference from one element to
			// another is then redirected to the other's copy and stays inside the document.
			Copier copier = new Copier();
			Map<EObject, String> ids = new HashMap<>();
			for (EObject element : collection) {
				if (element.eResource() == null && element.eContainer() == null) {
					document.getContents().add(element);
					live.add(element);
				} else {
					// Copied, not moved: the element belongs to its resource (or to its
					// container), and leaving it would also drop its xmi:ids (issue #246).
					ids.putAll(XMLResourceIDs.collectIDs(element));
					document.getContents().add(copier.copy(element));
				}
			}
			copier.copyReferences();
			Map<EObject, String> copiedIds = new HashMap<>();
			ids.forEach((original, id) -> copiedIds.put(copier.get(original), id));
			XMLResourceIDs.applyIDs(copiedIds, document);

			// A collection is an array even with one element or none
			writeResourceTo(document, Resource.class, genericType, annotations, mediaType, httpHeaders,
					entityStream, Map.of(CodecOptions.CODEC_ROOT_AS_ARRAY, Boolean.TRUE));
		} finally {
			// The live elements were only borrowed (issue #93)
			document.getContents().removeAll(live);
			resourceSet.getResources().remove(document);
		}
	}

	@Override
	public long getSize(Collection<? extends EObject> t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Collection.class.isAssignableFrom(type) && newCollection(type).isPresent()
				&& elementType(genericType).isPresent() && supportsMediaType(mediaType);
	}

	/**
	 * Reads the root objects of the request body - the elements of an array, or a single object
	 * as the only element.
	 * <p>
	 * As in {@link EObjectMessageBodyHandler#readFrom}, the objects stay in the resource they were
	 * loaded into, so their {@code xmi:id}s are kept (issue #246), and that resource is removed
	 * from the per-request {@link ResourceSet}.
	 * </p>
	 */
	@Override
	public Collection<EObject> readFrom(Class<Collection<EObject>> type, Type genericType,
			Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream) throws IOException, WebApplicationException {
		// The element type is the root type of every element (spec 13 §2.5)
		Class<?> elementType = elementType(genericType).orElse(EObject.class);
		Resource resource = readResourceFrom(elementType, genericType, annotations, mediaType, httpHeaders,
				entityStream);
		ResourceSet rs = resource.getResourceSet();
		if (rs != null) {
			rs.getResources().remove(resource);
		}
		Collection<EObject> result = newCollection(type).orElseGet(ArrayList::new);
		result.addAll(resource.getContents());
		return result;
	}

	/**
	 * The element class of a collection type, if it is an {@link EObject} type.
	 * <p>
	 * Resolved from a parameterized type ({@code List<Booking>}, {@code Collection<? extends EObject>})
	 * or from a class that fixes the parameter in its supertypes ({@code class Bookings extends
	 * ArrayList<Booking>}). A raw collection has none.
	 * </p>
	 *
	 * @param genericType the generic type of the entity
	 * @return the element class, empty if it is unknown or no EObject type
	 */
	static Optional<Class<?>> elementType(Type genericType) {
		if (genericType instanceof ParameterizedType parameterized
				&& parameterized.getRawType() instanceof Class<?> raw && Collection.class.isAssignableFrom(raw)) {
			Type[] arguments = parameterized.getActualTypeArguments();
			// A collection with one type parameter (List, Set, ArrayList, ...) is parameterized by its element
			if (arguments.length == 1) {
				return eObjectClass(arguments[0]);
			}
			return elementType(raw);
		}
		if (genericType instanceof Class<?> clazz && Collection.class.isAssignableFrom(clazz)) {
			for (Type supertype : supertypes(clazz)) {
				Optional<Class<?>> element = elementType(supertype);
				if (element.isPresent()) {
					return element;
				}
			}
		}
		return Optional.empty();
	}

	private static List<Type> supertypes(Class<?> clazz) {
		List<Type> supertypes = new ArrayList<>(List.of(clazz.getGenericInterfaces()));
		if (clazz.getGenericSuperclass() != null) {
			supertypes.add(clazz.getGenericSuperclass());
		}
		return supertypes;
	}

	private static Optional<Class<?>> eObjectClass(Type type) {
		Type bound = type;
		if (bound instanceof WildcardType wildcard) {
			bound = wildcard.getUpperBounds()[0];
		} else if (bound instanceof TypeVariable<?> variable) {
			bound = variable.getBounds()[0];
		}
		if (bound instanceof ParameterizedType parameterized) {
			bound = parameterized.getRawType();
		}
		return bound instanceof Class<?> clazz && EObject.class.isAssignableFrom(clazz)
				? Optional.of(clazz) : Optional.empty();
	}

	/** A collection the requested type can hold: a list where it allows one, else an ordered set. */
	private static Optional<Collection<EObject>> newCollection(Class<?> type) {
		if (type.isAssignableFrom(ArrayList.class)) {
			return Optional.of(new ArrayList<>());
		}
		if (type.isAssignableFrom(LinkedHashSet.class)) {
			return Optional.of(new LinkedHashSet<>());
		}
		return Optional.empty();
	}

	private boolean supportsMediaType(MediaType mediaType) {
		return getResourceSet().getResourceFactoryRegistry().getContentTypeToFactoryMap()
				.containsKey(mediaType.getType() + "/" + mediaType.getSubtype());
	}

	private ResourceFactoryImpl factoryFor(MediaType mediaType) {
		return (ResourceFactoryImpl) getResourceSet().getResourceFactoryRegistry().getContentTypeToFactoryMap()
				.get(mediaType.getType() + "/" + mediaType.getSubtype());
	}

	@Reference(unbind = "removeAnnotationConverter", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void addAnnotationConverter(AnnotationConverter converter) {
		annotationConverters.add(converter);
	}

	public void removeAnnotationConverter(AnnotationConverter converter) {
		annotationConverters.remove(converter);
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
