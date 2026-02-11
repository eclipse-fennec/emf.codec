///**
// * Copyright (c) 2012 - 2025 Data In Motion and others.
// * All rights reserved. 
// * 
// * This program and the accompanying materials are made
// * available under the terms of the Eclipse Public License 2.0
// * which is available at https://www.eclipse.org/legal/epl-2.0/
// *
// * SPDX-License-Identifier: EPL-2.0
// * 
// * Contributors:
// *     Data In Motion - initial API and implementation
// */
//package org.eclipse.fennec.codec.jackson.databind;
//
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.EStructuralFeature;
//import org.eclipse.emf.ecore.resource.Resource;
//
//import tools.jackson.core.JsonParser;
//import tools.jackson.core.TokenStreamContext;
//import tools.jackson.core.exc.StreamReadException;
//import tools.jackson.core.json.DupDetector;
//
///**
// * 
// * @author ilenia
// * @since Apr 28, 2025
// */
//public class CodecReadContext extends TokenStreamContext implements EMFCodecReadContext {
//
//	/**
//	 * Parent context for this context; null for root context.
//	 */
//	protected final CodecReadContext _parent;
//
//	// // // Optional duplicate detection
//
//	protected DupDetector _dups;
//
//	/*
//    /**********************************************************************
//    /* Simple instance reuse slots; speeds up things a bit (10-15%)
//    /* for docs with lots of small arrays/objects (for which
//    /* allocation was visible in profile stack frames)
//    /**********************************************************************
//	 */
//
//	protected CodecReadContext _child;
//
//	/*
//    /**********************************************************************
//    /* Location/state information (minus source reference)
//    /**********************************************************************
//	 */
//
//	protected String _currentName;
//
//	protected Object _currentValue;
//
//	protected int _lineNr;
//	protected int _columnNr;
//
//
//
//
//	public CodecReadContextImpl(CodecReadContext parent, int nestingDepth,
//			DupDetector dups, int type, int lineNr, int colNr) {
//		super();
//		_parent = parent;
//		_dups = dups;
//		_type = type;
//		_lineNr = lineNr;
//		_columnNr = colNr;
//		_index = -1;
//		_nestingDepth = nestingDepth;
//	}
//	
//	
//
//
//	public static CodecReadContext createRootContext(int lineNr, int colNr, DupDetector dups) {
//		return new CodecReadContextImpl(null, 0, dups, TYPE_ROOT, lineNr, colNr);
//	}
//
//
//	public static CodecReadContext createRootContext(DupDetector dups) {
//		return new CodecReadContextImpl(null, 0, dups, TYPE_ROOT, 1, 0);
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.CodecReadContext#createChildArrayContext(int, int)
//	 */
//	@Override
//	public CodecReadContext createChildArrayContext(int lineNr, int colNr) {
//		CodecReadContext ctxt = _child;
//		if (ctxt == null) {
//			_child = ctxt = new CodecReadContextImpl(this, _nestingDepth+1,
//					(_dups == null) ? null : _dups.child(), TYPE_ARRAY, lineNr, colNr);
//		} else {
//			ctxt.reset(TYPE_ARRAY, lineNr, colNr);
//		}
//		return ctxt;
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.CodecReadContext#createChildObjectContext(int, int)
//	 */
//	@Override
//	public CodecReadContext createChildObjectContext(int lineNr, int colNr) {
//		CodecReadContext ctxt = _child;
//		if (ctxt == null) {
//			_child = ctxt = new CodecReadContextImpl(this, _nestingDepth+1,
//					(_dups == null) ? null : _dups.child(), TYPE_OBJECT, lineNr, colNr);
//			return ctxt;
//		}
//		ctxt.reset(TYPE_OBJECT, lineNr, colNr);
//		return ctxt;
//	}
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.CodecReadContext#clearAndGetParent()
//	 */
//	public CodecReadContext clearAndGetParent() {
//        _currentValue = null;
//        // could also clear the current name, but seems cheap enough to leave?
//        _currentName = null;
//        return _parent;
//    }
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.CodecReadContext#setCurrentName(java.lang.String)
//	 */
//	public void setCurrentName(String name) throws StreamReadException
//    {
//        _currentName = name;
//        if (_dups != null) { _checkDup(_dups, name); }
//    }
//	
//	  /* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.CodecReadContext#expectComma()
//	 */
//	public boolean expectComma() {
//	        /* Assumption here is that we will be getting a value (at least
//	         * before calling this method again), and
//	         * so will auto-increment index to avoid having to do another call
//	         */
//	        int ix = ++_index; // starts from -1
//	        return (_type != TYPE_ROOT && ix > 0);
//	    }
//
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.CodecReadContext#reset(int, int, int)
//	 */
//	public CodecReadContext reset(int type, int lineNr, int colNr) {
//		_type = type;
//		_index = -1;
//		_lineNr = lineNr;
//		_columnNr = colNr;
//		_currentName = null;
//		_currentValue = null;
//		if (_dups != null) {
//			_dups.reset();
//		}
//		return this;
//	}
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.core.TokenStreamContext#getParent()
//	 */
//	@Override
//	public CodecReadContext getParent() {
//		return _parent;
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.core.TokenStreamContext#currentName()
//	 */
//	@Override
//	public String currentName() {
//		return _currentName;
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.core.TokenStreamContext#currentValue()
//	 */
//	@Override
//	public Object currentValue() {
//		return _currentValue;
//	}
//
//	/* 
//	 * (non-Javadoc)
//	 * @see tools.jackson.core.TokenStreamContext#assignCurrentValue(java.lang.Object)
//	 */
//	@Override
//	public void assignCurrentValue(Object v) {
//		_currentValue = v;
//	}
//
//	private void _checkDup(DupDetector dd, String name) throws StreamReadException
//    {
//        if (dd.isDup(name)) {
//            Object src = dd.getSource();
//            throw new StreamReadException(((src instanceof JsonParser) ? ((JsonParser) src) : null),
//                    "Duplicate Object property \""+name+"\"");
//        }
//    }
//
//
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setCurrentFeature(org.eclipse.emf.ecore.EStructuralFeature)
//	 */
//	@Override
//	public void setCurrentFeature(EStructuralFeature feature) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#getCurrentFeature()
//	 */
//	@Override
//	public EStructuralFeature getCurrentFeature() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setCurrentEObject(org.eclipse.emf.ecore.EObject)
//	 */
//	@Override
//	public void setCurrentEObject(EObject eObj) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#getCurrentEObject()
//	 */
//	@Override
//	public EObject getCurrentEObject() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#setResource(org.eclipse.emf.ecore.resource.Resource)
//	 */
//	@Override
//	public void setResource(Resource resource) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#getResource()
//	 */
//	@Override
//	public Resource getResource() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#writeFeatureAndFieldName(org.eclipse.emf.ecore.EStructuralFeature, java.lang.String)
//	 */
//	@Override
//	public int writeFeatureAndFieldName(EStructuralFeature feature, String name) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//
//
//
//	/* 
//	 * (non-Javadoc)
//	 * @see org.eclipse.fennec.codec.jackson.databind.EMFCodecContext#resetFeature()
//	 */
//	@Override
//	public void resetFeature() {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
