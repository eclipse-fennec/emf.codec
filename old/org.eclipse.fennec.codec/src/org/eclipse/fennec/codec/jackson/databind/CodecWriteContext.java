/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonWriteContext;

/**
 * Write context that provides codec information. We want to give information about the underlying structure
 * @author Mark Hoffmann
 * @since 26.01.2024
 */
public class CodecWriteContext extends TokenStreamContext implements EMFCodecWriteContext {

	public final static int STATUS_OK_AS_IS = 0;
	public final static int STATUS_OK_AFTER_COMMA = 1;
	public final static int STATUS_OK_AFTER_COLON = 2;
	public final static int STATUS_OK_AFTER_SPACE = 3; // in root context
	public final static int STATUS_EXPECT_VALUE = 4;
	public final static int STATUS_EXPECT_NAME = 5;

	private EMFContextHolder holder;

	/**
	 * Parent context for this context; null for root context.
	 */
	protected final CodecWriteContext _parent;

	// // // Optional duplicate detection

	protected DupDetector _dups;

	/*
    /**********************************************************************
    /* Simple instance reuse slots; speed up things a bit (10-15%)
    /* for docs with lots of small arrays/objects
    /**********************************************************************
	 */

	protected CodecWriteContext _child;

	/*
    /**********************************************************************
    /* Location/state information (minus source reference)
    /**********************************************************************
	 */

	/**
	 * Name of the Object property of which value is to be written; only
	 * used for OBJECT contexts
	 */
	protected String _currentName;

	protected Object _currentValue;

	/**
	 * Marker used to indicate that we just wrote a name, and
	 * now expect a value to write
	 */
	protected boolean _gotName;

	protected CodecWriteContext(int type, CodecWriteContext parent, DupDetector dups, Object currentObject) {
		super();
		_type = type;
		_parent = parent;
		_nestingDepth = parent == null ? 0 : parent._nestingDepth + 1;
		_dups = dups;
		_index = -1;
		_currentValue = currentObject;
		holder = new EMFContextHolder();
	}

	public static CodecWriteContext createRootCodecContext(DupDetector dd) {
		return new CodecWriteContext(TYPE_ROOT, null, dd, null);
	}

	public static boolean isCodecContext(TokenStreamContext ctx) {
		return ctx == null ? false : ctx instanceof CodecWriteContext;
	}

	public static int writeFeatureAndFieldName(TokenStreamContext ctx, EStructuralFeature feature, String fieldName)  {
		if (isNull(ctx) || !isCodecContext(ctx)) {
			if (ctx instanceof JsonWriteContext) {
				return ((JsonWriteContext)ctx).writeName(fieldName);
			}
			throw new IllegalArgumentException("A non null CodecGeneratorWriteContext must be provided");
		}
		if (nonNull(feature) && nonNull(fieldName)) {
			return ((CodecWriteContext)ctx).writeFeatureAndFieldName(feature, fieldName);
		} else {
			return JsonWriteContext.STATUS_EXPECT_NAME;
		}
	}



	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.json.JsonWriteContext#reset(int, java.lang.Object)
	 */
	@Override
	public CodecWriteContext reset(int type, Object currValue) {
		_type = type;
		_index = -1;
		_currentName = null;
		_gotName = false;
		_currentValue = currValue;
		if (_dups != null) { _dups.reset(); }
		return this;
	}

	/**
	 * Sets the feature.
	 * @param feature the feature to set
	 */
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
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#resetFeature()
	 */
	@Override
	public void resetFeature() {
		holder.setCurrentFeature(null);		
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#setCurrentEObject(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void setCurrentEObject(EObject eObject) {
		holder.setCurrentEObject(eObject);
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


	public int writeName(String name) throws StreamWriteException
	{
		if ((_type != TYPE_OBJECT) || _gotName) {
			return STATUS_EXPECT_VALUE;
		}
		_gotName = true;
		_currentName = name;
		if (_dups != null) { _checkDup(_dups, name); }
		return (_index < 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_COMMA;
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#createChildArrayContext()
	 */
	@Override
	public CodecWriteContext createChildArrayContext() {
		CodecWriteContext ctxt = _child;
		if (ctxt == null) {
			_child = ctxt = new CodecWriteContext(TYPE_ARRAY, this,
					(_dups == null) ? null : _dups.child(), null);
			return ctxt;
		}
		return ctxt.reset(TYPE_ARRAY, null);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#createChildArrayContext(java.lang.Object)
	 */
	@Override
	public CodecWriteContext createChildArrayContext(Object currValue) {
		CodecWriteContext ctxt = _child;
		if (ctxt == null) {
			_child = ctxt = new CodecWriteContext(TYPE_ARRAY, this,
					(_dups == null) ? null : _dups.child(), currValue);
			return ctxt;
		}
		return ctxt.reset(TYPE_ARRAY, currValue);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#createChildObjectContext()
	 */
	@Override
	public CodecWriteContext createChildObjectContext() {
		CodecWriteContext ctxt = _child;
		if (ctxt == null) {
			_child = ctxt = new CodecWriteContext(TYPE_OBJECT, this,
					(_dups == null) ? null : _dups.child(), null);
			return ctxt;
		}
		return ctxt.reset(TYPE_OBJECT, null);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#createChildObjectContext(java.lang.Object)
	 */
	@Override
	public CodecWriteContext createChildObjectContext(Object currValue) {
		CodecWriteContext ctxt = _child;
		if (ctxt == null) {
			_child = ctxt = new CodecWriteContext(TYPE_OBJECT, this,
					(_dups == null) ? null : _dups.child(), currValue);
			return ctxt;
		}
		return ctxt.reset(TYPE_OBJECT, currValue);
	}



	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.core.TokenStreamContext#getParent()
	 */
	@Override
	public CodecWriteContext getParent() {
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
	 * @see tools.jackson.core.TokenStreamContext#hasCurrentName()
	 */
	@Override
	public boolean hasCurrentName() {
		return _gotName;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecWriteContext#clearAndGetParent()
	 */
	@Override
	public CodecWriteContext clearAndGetParent() {
		_currentValue = null;
		// could also clear the current name, but seems cheap enough to leave?
		return _parent;
	}

	
	public int writeValue() {
        // Most likely, object:
        if (_type == TYPE_OBJECT) {
            if (!_gotName) {
                return STATUS_EXPECT_NAME;
            }
            _gotName = false;
            ++_index;
            return STATUS_OK_AFTER_COLON;
        }

        // Ok, array?
        if (_type == TYPE_ARRAY) {
            int ix = _index;
            ++_index;
            return (ix < 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_COMMA;
        }

        // Nope, root context
        // No commas within root context, but need space
        ++_index;
        return (_index == 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_SPACE;
    }
	
	private final void _checkDup(DupDetector dd, String name) throws StreamWriteException
	{
		if (dd.isDup(name)) {
			Object src = dd.getSource();
			throw new StreamWriteException(((src instanceof JsonGenerator) ? ((JsonGenerator) src) : null),
					"Duplicate Object property \""+name+"\"");
		}
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
	 * @see tools.jackson.core.TokenStreamContext#currentValue()
	 */
	@Override
	public Object currentValue() {
		return _currentValue;
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
