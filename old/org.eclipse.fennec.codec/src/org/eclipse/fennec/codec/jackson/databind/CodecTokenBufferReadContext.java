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
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.io.ContentReference;
import tools.jackson.databind.util.TokenBufferReadContext;

/**
 * 
 * @author ilenia
 * @since Jul 8, 2025
 */
public class CodecTokenBufferReadContext extends TokenBufferReadContext implements EMFCodecReadContext {

	private EMFContextHolder holder;


	private TokenStreamContext _parent;


	private TokenStreamLocation _startLocation;



	protected CodecTokenBufferReadContext(TokenStreamContext base, TokenStreamLocation startLoc) {
		super(base, startLoc);
		_parent = base.getParent();
		_currentName = base.currentName();
		_currentValue = base.currentValue();
		_startLocation = startLoc;
		holder = new EMFContextHolder();
		if(base.getParent() instanceof EMFCodecContext codecCtxt) {
			holder.setCurrentEObject(codecCtxt.getCurrentEObject());
			holder.setCurrentFeature(codecCtxt.getCurrentFeature());
			holder.setResource(codecCtxt.getResource());
		}

	} 





	protected CodecTokenBufferReadContext(CodecTokenBufferReadContext parent, int type, int index) {
		super(parent, type, index);
		_parent = parent;
		_startLocation = parent._startLocation;
		holder = new EMFContextHolder();
		holder.setCurrentEObject(parent.getCurrentEObject());
		holder.setCurrentFeature(parent.getCurrentFeature());
		holder.setResource(parent.getResource());

	}


	protected CodecTokenBufferReadContext() {
		super();
		_parent = null;
		_startLocation = TokenStreamLocation.NA;
		holder = new EMFContextHolder();
	}


	public CodecTokenBufferReadContext(TokenStreamContext origContext, ContentReference contentRef) {
		super(origContext, contentRef);
		_parent = origContext.getParent();
		_currentName = origContext.currentName();
		_currentValue = origContext.currentValue();

		if (origContext instanceof CodecJsonReadContext rc) {
			_startLocation = rc.startLocation(contentRef);
		} else {
			_startLocation = TokenStreamLocation.NA;
		}
		if(origContext instanceof EMFCodecReadContext codecReadCtxt) {
			holder = codecReadCtxt.getEMFContextHolder();
		} else {
			holder = new EMFContextHolder();
		}

	}



	public static CodecTokenBufferReadContext createRootContext(TokenStreamContext origContext) {
		// First: possible to have no current context; if so, just create bogus ROOT context
		if (origContext == null) {
			return new CodecTokenBufferReadContext();
		}
		return new CodecTokenBufferReadContext(origContext, ContentReference.unknown());
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
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#createChildArrayContext(int, int)
	 */
	@Override
	public CodecTokenBufferReadContext createChildArrayContext(int lineNr, int colNr) {
		++_index;
		return new CodecTokenBufferReadContext(this, TYPE_ARRAY, -1);
	}



	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.util.TokenBufferReadContext#createChildArrayContext()
	 */
	@Override
	public CodecTokenBufferReadContext createChildArrayContext() {
		++_index;
		return new CodecTokenBufferReadContext(this, TYPE_ARRAY, -1);
	}



	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#createChildObjectContext(int, int)
	 */
	@Override
	public CodecTokenBufferReadContext createChildObjectContext(int lineNr, int colNr) {
		++_index;
		return new CodecTokenBufferReadContext(this, TYPE_OBJECT, -1);
	}





	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.util.TokenBufferReadContext#createChildObjectContext()
	 */
	@Override
	public CodecTokenBufferReadContext createChildObjectContext() {
		++_index;
		return new CodecTokenBufferReadContext(this, TYPE_OBJECT, -1);
	}



	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.util.TokenBufferReadContext#parentOrCopy()
	 */
	@Override
	public CodecTokenBufferReadContext parentOrCopy() {
		// 30-Apr-2017, tatu: This is bit awkward since part on ancestor stack is of different
		//     type (usually `JsonReadContext`)... and so for unbalanced buffers (with extra
		//     END_OBJECT / END_ARRAY), we may need to create
		if (_parent instanceof CodecTokenBufferReadContext) {
			return (CodecTokenBufferReadContext) _parent;
		}
		if (_parent == null) { // unlikely, but just in case let's support
			return new CodecTokenBufferReadContext();
		}
		return new CodecTokenBufferReadContext(_parent, _startLocation);
	}



	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecReadContext#reset(int, int, int)
	 */
	@Override
	public CodecTokenBufferReadContext reset(int type, int lineNr, int colNr) {
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
	public TokenStreamContext clearAndGetParent() {
		_currentValue = null;
		return getParent();
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
	 * @see tools.jackson.databind.util.TokenBufferReadContext#getParent()
	 */
	@Override
	public TokenStreamContext getParent() {
		return _parent;
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
