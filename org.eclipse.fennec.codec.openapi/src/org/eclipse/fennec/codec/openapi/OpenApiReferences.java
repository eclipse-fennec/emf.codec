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
package org.eclipse.fennec.codec.openapi;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.model.openapi.Components;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.Schema;

/**
 * Resolves the internal {@code $ref}s of an OpenAPI description (issue #312).
 * <p>
 * A {@code $ref} stays a string in the model - it is what is written back. A reference into the same
 * document ({@code #/components/<section>/<key>}) leads to its target:
 * </p>
 * <ul>
 *   <li>{@code #/components/schemas/X} to the {@link EClassifier} the codec built for {@code X} in
 *       {@code Components.schemasPackage}, found by the schema's original name (the codec
 *       capitalizes class names, {@code pet} becomes {@code Pet});</li>
 *   <li>{@code #/components/parameters/X}, {@code responses}, {@code requestBodies}, {@code headers},
 *       {@code examples}, {@code links}, {@code callbacks}, {@code securitySchemes} to the model
 *       object in that map of {@link Components}.</li>
 * </ul>
 * <p>
 * Loading a description links every {@link Schema} with such a {@code $ref} to its classifier
 * ({@link Schema#getClassifier()}), nested schemas included. References into other documents are
 * not resolved.
 * </p>
 */
public final class OpenApiReferences {

	private static final String SCHEMAS = "schemas";

	private OpenApiReferences() {
	}

	/**
	 * Splits an internal reference into its unescaped JSON pointer segments.
	 *
	 * @param ref a {@code $ref} value
	 * @return the segments, e.g. {@code [components, schemas, Pet]}, or {@code null} for a reference
	 *         into another document or for {@code null}
	 */
	public static List<String> pointer(String ref) {
		if (ref == null || !ref.startsWith("#")) {
			return null;
		}
		String pointer = URLDecoder.decode(ref.substring(1).replace("+", "%2B"), StandardCharsets.UTF_8);
		List<String> segments = new ArrayList<>();
		for (String segment : pointer.split("/", -1)) {
			segments.add(segment.replace("~1", "/").replace("~0", "~"));
		}
		if (!segments.isEmpty() && segments.get(0).isEmpty()) {
			segments.remove(0);
		}
		return segments;
	}

	/**
	 * Resolves an internal reference into {@code components}.
	 *
	 * @param api the description the reference belongs to
	 * @param ref a {@code $ref} value
	 * @return the {@link EClassifier} for {@code #/components/schemas/X}, the model object for the
	 *         other sections, {@code null} if the reference is external or has no target
	 */
	public static Object resolve(OpenAPI api, String ref) {
		List<String> segments = pointer(ref);
		if (api == null || segments == null || segments.size() != 3 || !"components".equals(segments.get(0))) {
			return null;
		}
		Components components = api.getComponents();
		if (components == null) {
			return null;
		}
		String section = segments.get(1);
		String key = segments.get(2);
		if (SCHEMAS.equals(section)) {
			return classifier(components.getSchemasPackage(), key);
		}
		EStructuralFeature feature = components.eClass().getEStructuralFeature(section);
		if (feature != null && components.eGet(feature) instanceof EMap<?, ?> map) {
			return map.get(key);
		}
		return null;
	}

	/**
	 * The classifier a schema points at.
	 *
	 * @param api the description the schema belongs to
	 * @param schema an inline schema
	 * @return its {@link Schema#getClassifier() link}, else the classifier its {@code $ref} names in
	 *         {@code components/schemas}, else {@code null}
	 */
	public static EClassifier classifierOf(OpenAPI api, Schema schema) {
		if (schema == null) {
			return null;
		}
		if (schema.getClassifier() != null) {
			return schema.getClassifier();
		}
		return resolve(api, schema.getRef()) instanceof EClassifier classifier ? classifier : null;
	}

	/**
	 * Links every schema of the description with a {@code $ref} into {@code components/schemas} to
	 * its classifier and checks every other internal reference.
	 *
	 * @param api a loaded description
	 * @return the internal references without target
	 */
	static List<String> link(OpenAPI api) {
		List<String> unresolved = new ArrayList<>();
		for (TreeIterator<EObject> it = api.eAllContents(); it.hasNext();) {
			EObject object = it.next();
			if (object instanceof EPackage) {
				it.prune(); // the schema classes, no references of the description inside
				continue;
			}
			EStructuralFeature refFeature = object.eClass().getEStructuralFeature("ref");
			if (refFeature == null || !(object.eGet(refFeature) instanceof String ref) || pointer(ref) == null) {
				continue;
			}
			Object target = resolve(api, ref);
			if (target == null) {
				unresolved.add(ref);
			} else if (object instanceof Schema schema && target instanceof EClassifier classifier) {
				schema.setClassifier(classifier);
			}
		}
		return unresolved;
	}

	/** The classifier built for the schema {@code key}: by original name, then by name. */
	static EClassifier classifier(EPackage pkg, String key) {
		if (pkg == null || key == null) {
			return null;
		}
		EClassifier byName = null;
		for (EClassifier classifier : pkg.getEClassifiers()) {
			String originalName = EcoreUtil.getAnnotation(classifier, AnnotationSources.JSONSCHEMA, "originalName");
			if (key.equals(originalName)) {
				return classifier;
			}
			if (byName == null && originalName == null && key.equals(classifier.getName())) {
				byName = classifier;
			}
		}
		return byName;
	}
}
