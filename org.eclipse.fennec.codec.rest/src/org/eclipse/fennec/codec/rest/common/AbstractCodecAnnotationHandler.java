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
package org.eclipse.fennec.codec.rest.common;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.rest.annotations.AnnotationConverter;
import org.eclipse.fennec.codec.rest.annotations.ContentNotEmpty;
import org.eclipse.fennec.codec.rest.annotations.EMFResourceOptions;
import org.eclipse.fennec.codec.rest.annotations.ResourceEClass;
import org.eclipse.fennec.codec.rest.annotations.ResourceOption;
import org.eclipse.fennec.codec.rest.annotations.ValidateContent;
import org.eclipse.fennec.codec.rest.annotations.json.RootElement;

/**
 * 
 * @author ilenia
 * @since 1.0
 */
public abstract class AbstractCodecAnnotationHandler {

	protected List<AnnotationConverter> annotationConverters = new LinkedList<>();

	/**
	 * Reads the annotations for {@link EMFResourceOptions} and adds them to the
	 * given {@link Map}
	 * 
	 * @param annotations the {@link Annotation} Array to parse
	 * @param options     the options {@link Map} to add the annotations content to
	 * @param resourceSet the {@link ResourceSet} to use
	 */
	protected void handleAnnotedOptions(Annotation[] annotations, Map<Object, Object> options, ResourceSet resourceSet,
			boolean serialize) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof ResourceOption) {
				addEMFResourceOption(options, (ResourceOption) annotation, resourceSet);
			} else if (annotation instanceof EMFResourceOptions) {
				EMFResourceOptions opts = (EMFResourceOptions) annotation;
				for (ResourceOption opt : opts.options()) {
					addEMFResourceOption(options, opt, resourceSet);
				}
			} else {
				// Lets look if we have an annotation converter
				for (AnnotationConverter annotationConverter : annotationConverters) {
					if (annotationConverter.canHandle(annotation, serialize)) {
						annotationConverter.convertAnnotation(annotation, serialize, options);
					}
				}
			}

		}
	}

	/**
	 * Adds a {@link ResourceOption} to the given options {@link Map};
	 * 
	 * @param options     the options {@link HMap} to add the annotations content to
	 * @param annotation  the {@link ResourceOption} to parse
	 * @param resourceSet the {@link ResourceSet} to use
	 */
	protected void addEMFResourceOption(Map<Object, Object> options, ResourceOption annotation,
			ResourceSet resourceSet) {
		options.put(annotation.key(), createValue(annotation, resourceSet));
	}

	/**
	 * Checks and executes checks according to the annotations
	 * 
	 * @param resource    the {@link Resource} to check
	 * @param annotations tha available {@link Annotation}s
	 */
	protected void checkResourceByAnnotation(Resource resource, Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof ContentNotEmpty) {
				handleContentNotEmpty(resource, (ContentNotEmpty) annotation);
			}
			if (annotation instanceof ResourceEClass) {
				handleResourceEClass(resource, annotation);
			}
			if (annotation instanceof ValidateContent) {
				handleValidateContent(resource);
			}
			if (annotation instanceof RootElement) {
				handleResourceRootType(resource, annotation);
				handleResourceRootSchema(resource, annotation);
			}
		}

	}

	/**
	 * Handles the content validation
	 * 
	 * @param resource the EMF resource to validate
	 */
	abstract protected void handleValidateContent(Resource resource);

	/**
	 * Handles the {@link ResourceEClass} annotation
	 * 
	 * @param resource   the EMF resource
	 * @param annotation the annotation
	 */
	abstract protected void handleResourceEClass(Resource resource, Annotation annotation);

	/**
	 * Handles the content not empty annotation
	 * 
	 * @param resource   the EMF resource
	 * @param annotation the content not empty annotation
	 */
	abstract protected void handleContentNotEmpty(Resource resource, ContentNotEmpty annotation);
	
	/**
	 * Handles the {@link RootElement} - rootType annotation
	 * 
	 * @param resource   the EMF resource
	 * @param annotation the annotation
	 */
	abstract protected void handleResourceRootType(Resource resource, Annotation annotation);
	
	/**
	 * Handles the {@link RootElement} - rootSchema annotation
	 * 
	 * @param resource   the EMF resource
	 * @param annotation the annotation
	 */
	abstract protected void handleResourceRootSchema(Resource resource, Annotation annotation);
	

	/**
	 * Writes the diagnostic content to a String
	 * 
	 * @param diagnostic the {@link Diagnostic} to serialize
	 * @return the message as a {@link String}
	 */
	protected String buildDiagnosticMessage(Diagnostic diagnostic) {
		StringBuilder builder = new StringBuilder();
		buildDiagnosticMessage(diagnostic, builder, "");
		return builder.toString();
	}

	/**
	 * Recursively writes the given {@link Diagnostic} and all
	 * {@link Diagnostic#getChildren()} to the given {@link StringBuilder}
	 * 
	 * @param diagnostic    the current {@link Diagnostic}
	 * @param stringBuilder the {@link StringBuilder} to use
	 * @param intend        the intent to use before the message
	 */
	protected void buildDiagnosticMessage(Diagnostic diagnostic, StringBuilder stringBuilder, String intend) {
		stringBuilder.append(intend);
		stringBuilder.append(diagnostic.getMessage());
		stringBuilder.append(System.lineSeparator());
		for (Diagnostic diagnosticChild : diagnostic.getChildren()) {
			buildDiagnosticMessage(diagnosticChild, stringBuilder, intend + "  ");
		}
	}

	/**
	 * Parses the String representation of the value into the desired type.
	 * 
	 * @param opt         the {@link ResourceOption} to create the value from
	 * @param resourceSet the {@link ResourceSet} to use
	 * @return the {@link ResourceOption#value()} as the type specified by
	 *         {@link ResourceOption#valueType()}
	 */
	private Object createValue(ResourceOption opt, ResourceSet resourceSet) {
		// EClass needs URI resolution against the ResourceSet; the scalar types are shared with
		// the client-override path via CodecOptionValues.
		if (opt.valueType().equals(EClass.class)) {
			return resourceSet.getEObject(URI.createURI(opt.value()), Boolean.TRUE);
		}
		return CodecOptionValues.parse(opt.value(), opt.valueType());
	}
}
