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
package org.eclipse.fennec.codec.rest.jakartas;

import java.lang.annotation.Annotation;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.rest.annotations.ContentNotEmpty;
import org.eclipse.fennec.codec.rest.annotations.ResourceEClass;
import org.eclipse.fennec.codec.rest.annotations.json.RootElement;
import org.eclipse.fennec.codec.rest.common.AbstractCodecAnnotationHandler;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;

/**
 * Base class to handle Codec resource annotation and turn them into Codec load or save options
 * @author ilenia
 * @since 1.0
 */
public class AbstractJakartaCodecAnnotationHandler extends AbstractCodecAnnotationHandler {

	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.rest.common.AbstractCodecAnnotationHandler#handleValidateContent(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	protected void handleValidateContent(Resource resource) {
		if (resource == null) {
			return;
		}
		for (EObject eObject : resource.getContents()) {
			Diagnostic diagnostic = Diagnostician.INSTANCE.validate(eObject);
			if (diagnostic.getSeverity() == Diagnostic.ERROR) {
				Response res = Response.status(400).entity(buildDiagnosticMessage(diagnostic)).build();
				throw new WebApplicationException(res);
			}
		}
	}

	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.rest.common.AbstractCodecAnnotationHandler#handleResourceEClass(org.eclipse.emf.ecore.resource.Resource, java.lang.annotation.Annotation)
	 */
	@Override
	protected void handleResourceEClass(Resource resource, Annotation annotation) {
		if (resource == null || annotation == null) {
			return;
		}
		String eClassName = ((ResourceEClass) annotation).value();
		for (EObject eObject : resource.getContents()) {
			if (!eObject.eClass().getName().equals(eClassName)) {
				List<Variant> encoded = Variant.encodings(eClassName).build();
				Response res = Response.notAcceptable(encoded).build();
				throw new WebApplicationException(res);
			}
		}
	}


	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.rest.common.AbstractCodecAnnotationHandler#handleContentNotEmpty(org.eclipse.emf.ecore.resource.Resource, org.eclipse.fennec.codec.rest.annotations.ContentNotEmpty)
	 */
	@Override
	protected void handleContentNotEmpty(Resource resource, ContentNotEmpty annotation) {
		if (resource == null || annotation == null) {
			return;
		}
		if (resource.getContents().isEmpty()) {
			List<Variant> encoded = Variant.encodings(annotation.message()).build();
			Response res = Response.notAcceptable(encoded).build();
			throw new WebApplicationException(res);
		}
	}


	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.rest.common.AbstractCodecAnnotationHandler#handleResourceRootType(org.eclipse.emf.ecore.resource.Resource, java.lang.annotation.Annotation)
	 */
	@Override
	protected void handleResourceRootType(Resource resource, Annotation annotation) {
		if (resource == null || annotation == null) {
			return;
		}
		String rootType = ((RootElement) annotation).rootType();
		if(rootType.isBlank()) return;
		for (EObject eObject : resource.getContents()) {
			if (!rootType.equals(EcoreUtil.getURI(eObject.eClass()).toString())) {
				List<Variant> encoded = Variant.encodings(rootType).build();
				Response res = Response.notAcceptable(encoded).build();
				throw new WebApplicationException(res);
			}
		}
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.rest.common.AbstractCodecAnnotationHandler#handleResourceRootSchema(org.eclipse.emf.ecore.resource.Resource, java.lang.annotation.Annotation)
	 */
	@Override
	protected void handleResourceRootSchema(Resource resource, Annotation annotation) {
		if (resource == null || annotation == null) {
			return;
		}
		String rootSchema = ((RootElement) annotation).rootSchema();
		if(rootSchema.isBlank()) return;
		for (EObject eObject : resource.getContents()) {
			if (!rootSchema.equals(eObject.eClass().getEPackage().getNsURI())) {
				List<Variant> encoded = Variant.encodings(rootSchema).build();
				Response res = Response.notAcceptable(encoded).build();
				throw new WebApplicationException(res);
			}
		}
	}

}
