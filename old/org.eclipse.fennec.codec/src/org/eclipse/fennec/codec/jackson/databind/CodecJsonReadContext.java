/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.jackson.databind;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;

import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonReadContext;

/**
 * 
 * @author ilenia
 * @since May 7, 2025
 */
public class CodecJsonReadContext extends JsonReadContext implements EMFCodecReadContext {

	private EMFContextHolder holder;

	public CodecJsonReadContext(JsonReadContext parent, int nestingDepth, DupDetector dups, int type, int lineNr,
			int colNr) {
		super(parent, nestingDepth, dups, type, lineNr, colNr);
		holder = new EMFContextHolder();
	}

	public static CodecJsonReadContext createRootContext(int lineNr, int colNr, DupDetector dups) {
		return new CodecJsonReadContext(null, 0, dups, TYPE_ROOT, lineNr, colNr);
	}

	public static CodecJsonReadContext createRootContext(DupDetector dups) {
		return new CodecJsonReadContext(null, 0, dups, TYPE_ROOT, 1, 0);
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonReadContext#createChildArrayContext(int, int)
	 */
	@Override
	public CodecJsonReadContext createChildArrayContext(int lineNr, int colNr) {
		CodecJsonReadContext ctxt = (CodecJsonReadContext)_child;
		if (ctxt == null) {
			_child = ctxt = new CodecJsonReadContext(this, _nestingDepth+1,
					(_dups == null) ? null : _dups.child(), TYPE_ARRAY, lineNr, colNr);
		} else {
			ctxt.reset(TYPE_ARRAY, lineNr, colNr);
		}
		return ctxt;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonReadContext#createChildObjectContext(int, int)
	 */
	@Override
	public CodecJsonReadContext createChildObjectContext(int lineNr, int colNr) {
		CodecJsonReadContext ctxt = (CodecJsonReadContext)_child;
		if (ctxt == null) {
			_child = ctxt = new CodecJsonReadContext(this, _nestingDepth+1,
					(_dups == null) ? null : _dups.child(), TYPE_OBJECT, lineNr, colNr);
			return ctxt;
		}
		ctxt.reset(TYPE_OBJECT, lineNr, colNr);
		return ctxt;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonReadContext#getParent()
	 */
	@Override 
	public CodecJsonReadContext getParent() { 
		return (CodecJsonReadContext)_parent; 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonReadContext#clearAndGetParent()
	 */
	@Override
	public CodecJsonReadContext clearAndGetParent() {
		_currentValue = null;
		return getParent();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setCurrentFeature(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public void setCurrentFeature(EStructuralFeature feature) {
		holder.setCurrentFeature(feature);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#getCurrentFeature()
	 */
	@Override
	public EStructuralFeature getCurrentFeature() {
		return holder.getCurrentFeature();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setCurrentEObject(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void setCurrentEObject(EObject eObj) {
		holder.setCurrentEObject(eObj);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#getCurrentEObject()
	 */
	@Override
	public EObject getCurrentEObject() {
		return holder.getCurrentEObject();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void setResource(Resource resource) {
		holder.setResource(resource);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#getResource()
	 */
	@Override
	public Resource getResource() {
		return holder.getResource();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#resetFeature()
	 */
	@Override
	public void resetFeature() {
		holder.resetFeature();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#hasParentContext()
	 */
	@Override
	public boolean hasParentContext() {
		return _parent != null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#getEMFContextHolder()
	 */
	@Override
	public EMFContextHolder getEMFContextHolder() {
		return holder;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setEMFContextHolder(org.eclipse.fennec.codec.jackson.databind.EMFContextHolder)
	 */
	@Override
	public void setEMFContextHolder(EMFContextHolder holder) {
		this.holder = holder;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#setCurrentTypeInfo(org.eclipse.fennec.codec.info.codecinfo.TypeInfo)
	 */
	@Override
	public void setCurrentTypeInfo(TypeInfo typeInfo) {
		holder.setCurrentTypeInfo(typeInfo);
		
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#getCurrentTypeInfo()
	 */
	@Override
	public TypeInfo getCurrentTypeInfo() {
		return holder.getCurrentTypeInfo();
	}

}
