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

import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.exc.StreamReadException;

/**
 * 
 * @author ilenia
 * @since Apr 28, 2025
 */
public class CodecReadContext extends TokenStreamContext implements EMFCodecReadContext{

	private EMFContextHolder holder;

	protected CodecReadContext _child;
	protected CodecReadContext _parent;
	protected String _currentName;
	protected Object _currentValue;

	public CodecReadContext() {
		super(TYPE_ROOT, -1);
		holder = new EMFContextHolder();
	}

	public CodecReadContext(CodecReadContext parent, int nestingDepth, int type) {
		super(type, -1);
		_parent = parent;
		holder = new EMFContextHolder();

	}

	public static CodecReadContext createRootContext() {
		return new CodecReadContext();
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
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setCurrentFeature(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public void setCurrentFeature(EStructuralFeature _feature) {
		holder.setCurrentFeature(_feature);
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
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setCurrentEObject(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void setCurrentEObject(EObject _eObj) {
		holder.setCurrentEObject(_eObj);
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
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#createChildArrayContext(int, int)
	 */
	@Override
	public CodecReadContext createChildArrayContext(int lineNr, int colNr) {
		CodecReadContext ctxt = _child;
		if (ctxt == null) {
			_child = ctxt = new CodecReadContext(this, _nestingDepth+1, TYPE_ARRAY);
		} else {
			ctxt.reset(TYPE_ARRAY, lineNr, colNr);
		}
		return ctxt;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#createChildObjectContext(int, int)
	 */
	@Override
	public CodecReadContext createChildObjectContext(int lineNr, int colNr) {
		CodecReadContext ctxt = _child;
		if (ctxt == null) {
			_child = ctxt = new CodecReadContext(this, _nestingDepth+1, TYPE_OBJECT);
			return ctxt;
		}
		ctxt.reset(TYPE_OBJECT, lineNr, colNr);
		return ctxt;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#reset(int, int, int)
	 */
	@Override
	public CodecReadContext reset(int type, int lineNr, int colNr) {
		_type = type;
		_index = -1;
		_currentName = null;
		_currentValue = null;
		return this;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#clearAndGetParent()
	 */
	@Override
	public CodecReadContext clearAndGetParent() {
		_currentValue = null;
		return getParent();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#setCurrentName(java.lang.String)
	 */
	@Override
	public void setCurrentName(String name) throws StreamReadException {
		_currentName = name;		
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamContext#hasCurrentName()
	 */
	@Override 
	public boolean hasCurrentName() { 
		return _currentName != null; 
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamContext#getParent()
	 */
	@Override
	public CodecReadContext getParent() {
		return _parent;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamContext#currentName()
	 */
	@Override
	public String currentName() {
		return _currentName;
	}


	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamContext#currentValue()
	 */
	@Override
	public Object currentValue() {
		return _currentValue;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamContext#assignCurrentValue(java.lang.Object)
	 */
	@Override
	public void assignCurrentValue(Object v) {
		_currentValue = v;
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
