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

import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonWriteContext;

/**
 * 
 * @author ilenia
 * @since May 6, 2025
 */
public class CodecJsonWriteContext extends JsonWriteContext implements EMFCodecWriteContext {

	private EMFContextHolder holder;


	protected CodecJsonWriteContext(int type, CodecJsonWriteContext parent, DupDetector dups, Object currValue) {
		super(type, parent, dups, currValue);
		this.holder = new EMFContextHolder();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#setCurrentFeature(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public void setCurrentFeature(EStructuralFeature feature) {
		holder.setCurrentFeature(feature);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#getCurrentFeature()
	 */
	@Override
	public EStructuralFeature getCurrentFeature() {
		return holder.getCurrentFeature();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#setCurrentEObject(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void setCurrentEObject(EObject eObj) {
		holder.setCurrentEObject(eObj);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#getCurrentEObject()
	 */
	@Override
	public EObject getCurrentEObject() {
		return holder.getCurrentEObject();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#setResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void setResource(Resource resource) {
		holder.setResource(resource);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#getResource()
	 */
	@Override
	public Resource getResource() {
		return holder.getResource();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#resetFeature()
	 */
	@Override
	public void resetFeature() {
		holder.resetFeature();		
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#writeFeatureAndFieldName(org.eclipse.emf.ecore.EStructuralFeature, java.lang.String)
	 */
	@Override
	public int writeFeatureAndFieldName(EStructuralFeature feature, String name) {
		int r = writeName(name);
		if (r == STATUS_OK_AS_IS || r == STATUS_OK_AFTER_COMMA) {
			holder.setCurrentFeature(feature);
		}
		return r;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#createChildArrayContext()
	 */
	@Override
	public CodecJsonWriteContext createChildArrayContext() {
		CodecJsonWriteContext ctxt = (CodecJsonWriteContext) _child;
		if (ctxt == null) {
			_child = ctxt = new CodecJsonWriteContext(TYPE_ARRAY, this,
					(_dups == null) ? null : _dups.child(), null);
			return ctxt;
		}
		return (CodecJsonWriteContext) ctxt.reset(TYPE_ARRAY, null);
	}

	public static CodecJsonWriteContext createRootCodecContext(DupDetector dd) {
		return new CodecJsonWriteContext(TYPE_ROOT, null, dd, null);
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonWriteContext#createChildArrayContext(java.lang.Object)
	 */
	@Override
	public CodecJsonWriteContext createChildArrayContext(Object currValue) {
		CodecJsonWriteContext ctxt = (CodecJsonWriteContext) _child;
		if (ctxt == null) {
			_child = ctxt = new CodecJsonWriteContext(TYPE_ARRAY, this,
					(_dups == null) ? null : _dups.child(), currValue);
			return ctxt;
		}
		return (CodecJsonWriteContext) ctxt.reset(TYPE_ARRAY, currValue);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#createChildObjectContext()
	 */
	@Override
	public CodecJsonWriteContext createChildObjectContext() {
		CodecJsonWriteContext ctxt = (CodecJsonWriteContext) _child;
		if (ctxt == null) {
			_child = ctxt = new CodecJsonWriteContext(TYPE_OBJECT, this,
					(_dups == null) ? null : _dups.child(), null);
			return (CodecJsonWriteContext) ctxt;
		}
		return (CodecJsonWriteContext) ctxt.reset(TYPE_OBJECT, null);
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonWriteContext#createChildObjectContext(java.lang.Object)
	 */
	@Override
	public CodecJsonWriteContext createChildObjectContext(Object currValue) {
		CodecJsonWriteContext ctxt = (CodecJsonWriteContext) _child;
		if (ctxt == null) {
			_child = ctxt = new CodecJsonWriteContext(TYPE_OBJECT, this,
					(_dups == null) ? null : _dups.child(), currValue);
			return (CodecJsonWriteContext) ctxt;
		}
		return (CodecJsonWriteContext) ctxt.reset(TYPE_OBJECT, currValue);
	}



	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonWriteContext#reset(int, java.lang.Object)
	 */
	@Override
	public CodecJsonWriteContext reset(int type, Object currValue) {
		_type = type;
		_index = -1;
		_currentName = null;
		_gotName = false;
		_currentValue = currValue;
		if (_dups != null) { _dups.reset(); }
		return this;
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



}
