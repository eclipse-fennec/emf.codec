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
package org.eclipse.fennec.codec.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.bridge.AspectToPropertiesConverter;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;

/**
 * Makes the codec annotations of a model visible to a {@link ConfigurationResolver}, also for a
 * package no metadata service knows - such as one just read from a schema (spec 21 §4).
 *
 * @since 1.0
 */
public final class ModelAnnotations {

	private ModelAnnotations() {
		// utility class
	}

	/**
	 * Returns a resolver that also sees the codec annotations of the given package and its
	 * sub-packages. The resolver's own annotation properties win for elements both know.
	 *
	 * @param resolver the resolver to extend, {@code null} for the defaults
	 * @param ePackage the package whose annotations to add, may be {@code null}
	 * @return the extended resolver, or the given one when the package has no codec annotations
	 */
	public static ConfigurationResolver withAnnotationsOf(ConfigurationResolver resolver, EPackage ePackage) {
		ConfigurationResolver base = resolver != null ? resolver : ConfigurationResolver.defaults();
		if (ePackage == null) {
			return base;
		}
		Map<String, Object> packageProperties = annotationProperties(ePackage);
		if (packageProperties.isEmpty()) {
			return base;
		}
		return base.toBuilder()
				.annotationProperties(merge(base.getAnnotationProperties(), packageProperties))
				.build();
	}

	/**
	 * The metadata service needs an nsURI; a package read from a schema without {@code $id} has
	 * none. It gets a temporary one - the properties are keyed by the element instances, so the
	 * nsURI plays no part in them.
	 */
	private static Map<String, Object> annotationProperties(EPackage ePackage) {
		String nsURI = ePackage.getNsURI();
		if (nsURI == null) {
			ePackage.setNsURI("urn:fennec:codec:annotations:" + System.identityHashCode(ePackage));
		}
		try {
			MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
			whiteboard.registerPackage(ePackage);
			return AspectToPropertiesConverter.buildAnnotationProperties(whiteboard);
		} finally {
			if (nsURI == null) {
				ePackage.setNsURI(null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> merge(Map<String, Object> existing, Map<String, Object> added) {
		Map<String, Object> merged = new HashMap<>(added);
		if (existing == null) {
			return merged;
		}
		for (Map.Entry<String, Object> entry : existing.entrySet()) {
			Object addedValue = merged.get(entry.getKey());
			if (addedValue instanceof Map<?, ?> addedMap && entry.getValue() instanceof Map<?, ?> existingMap) {
				Map<Object, Object> nested = new HashMap<>((Map<Object, Object>) addedMap);
				nested.putAll((Map<Object, Object>) existingMap);
				merged.put(entry.getKey(), nested);
			} else {
				merged.put(entry.getKey(), entry.getValue());
			}
		}
		return merged;
	}
}
