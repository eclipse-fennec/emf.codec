/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.codec.rest.jakartas.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.rest.annotations.AnnotationConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The dynamic {@link AnnotationConverter} reference of the message body handlers: an unbound
 * converter must be gone, the unbind used to add it a second time.
 */
@DisplayName("Annotation converter bind/unbind of the message body handlers")
class AnnotationConverterBindingTest {

	private static final AnnotationConverter CONVERTER = new AnnotationConverter() {

		@Override
		public boolean canHandle(Annotation annotation, boolean serialize) {
			return false;
		}

		@Override
		public void convertAnnotation(Annotation annotation, boolean serialize, Map<Object, Object> options) {
			// not called
		}
	};

	static class EObjectHandler extends EObjectMessageBodyHandler<EObject, EObject> {
		List<AnnotationConverter> converters() {
			return annotationConverters;
		}

		@Override
		protected ResourceSet getResourceSet() {
			return null;
		}
	}

	static class ResourceHandler extends EMFResourceMessageBodyHandler<Resource, Resource> {
		List<AnnotationConverter> converters() {
			return annotationConverters;
		}

		@Override
		protected ResourceSet getResourceSet() {
			return null;
		}
	}

	static class CollectionHandler extends EObjectCollectionMessageBodyHandler {
		List<AnnotationConverter> converters() {
			return annotationConverters;
		}

		@Override
		protected ResourceSet getResourceSet() {
			return null;
		}
	}

	private static void assertUnbindRemoves(Consumer<AnnotationConverter> bind, Consumer<AnnotationConverter> unbind,
			List<AnnotationConverter> converters) {
		bind.accept(CONVERTER);
		assertEquals(List.of(CONVERTER), converters);
		unbind.accept(CONVERTER);
		assertTrue(converters.isEmpty(), "unbind must remove the converter, was: " + converters);
	}

	@Test
	@DisplayName("EObjectMessageBodyHandler")
	void eObjectHandler() {
		EObjectHandler handler = new EObjectHandler();
		assertUnbindRemoves(handler::addAnnotationConverter, handler::removeAnnotationConverter, handler.converters());
	}

	@Test
	@DisplayName("EMFResourceMessageBodyHandler")
	void resourceHandler() {
		ResourceHandler handler = new ResourceHandler();
		assertUnbindRemoves(handler::addAnnotationConverter, handler::removeAnnotationConverter, handler.converters());
	}

	@Test
	@DisplayName("EObjectCollectionMessageBodyHandler")
	void collectionHandler() {
		CollectionHandler handler = new CollectionHandler();
		assertUnbindRemoves(handler::addAnnotationConverter, handler::removeAnnotationConverter, handler.converters());
	}
}
