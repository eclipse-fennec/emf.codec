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

import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonWriteContext;

/**
 * Write context that provides codec information. We want to give information about the underlying structure
 * @author Mark Hoffmann
 * @since 26.01.2024
 */
public class CodecWriteContextOld extends JsonWriteContext {
	
	private EStructuralFeature feature;
	private EObject currentEObject;
	private Resource resource;
	
	protected CodecWriteContextOld(int type, CodecWriteContextOld parent, DupDetector dups, Object currentObject) {
		super(type, parent, dups, currentObject);
	}
	
    public static CodecWriteContextOld createRootCodecContext(DupDetector dd) {
        return new CodecWriteContextOld(TYPE_ROOT, null, dd, null);
    }
    
    public static boolean isCodecContext(TokenStreamContext ctx) {
    	return ctx == null ? false : ctx instanceof CodecWriteContextOld;
    }
    
    public static int writeFeatureAndFieldName(TokenStreamContext ctx, EStructuralFeature feature, String fieldName)  {
    	if (isNull(ctx) || !isCodecContext(ctx)) {
    		if (ctx instanceof JsonWriteContext) {
    			return ((JsonWriteContext)ctx).writeName(fieldName);
    		}
    		throw new IllegalArgumentException("A non null CodecGeneratorWriteContext must be provided");
    	}
    	if (nonNull(feature) && nonNull(fieldName)) {
    		return ((CodecWriteContextOld)ctx).writeFeatureAndFieldName(feature, fieldName);
    	} else {
    		return JsonWriteContext.STATUS_EXPECT_NAME;
    	}
    }
    
    public static void resetFeature(TokenStreamContext ctx) {
    	if (isCodecContext(ctx)) {
    		((CodecWriteContextOld)ctx).setFeature(null);
    	}
    }
	
    /* 
     * (non-Javadoc)
     * @see tools.jackson.core.json.JsonWriteContext#reset(int, java.lang.Object)
     */
    @Override
    public JsonWriteContext reset(int type, Object currValue) {
    	return super.reset(type, currValue);
    }
    
	/**
	 * Sets the feature.
	 * @param feature the feature to set
	 */
	public void setFeature(EStructuralFeature feature) {
		this.feature = feature;
	}
	
	/**
	 * Returns the feature.
	 * @return the feature
	 */
	public EStructuralFeature getFeature() {
		return feature;
	}
	
	public void setCurrentEObject(EObject eObject) {
		this.currentEObject = eObject;
	}
	
	public EObject getCurrentEObject() {
		return this.currentEObject;	
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public int writeFeatureAndFieldName(EStructuralFeature feature, String name) {
		int r = super.writeName(name);
		if (r == STATUS_OK_AS_IS || r == STATUS_OK_AFTER_COMMA) {
			this.feature = feature;
		}
		return r;
	}
	
	
	public CodecWriteContextOld createChildArrayContext() {
		CodecWriteContextOld ctxt = (CodecWriteContextOld) _child;
        if (ctxt == null) {
            _child = ctxt = new CodecWriteContextOld(TYPE_ARRAY, this,
                    (_dups == null) ? null : _dups.child(), null);
            return ctxt;
        }
        return (CodecWriteContextOld) ctxt.reset(TYPE_ARRAY, null);
    }

    /* @since 2.10 */
    public CodecWriteContextOld createChildArrayContext(Object currValue) {
    	CodecWriteContextOld ctxt = (CodecWriteContextOld) _child;
        if (ctxt == null) {
            _child = ctxt = new CodecWriteContextOld(TYPE_ARRAY, this,
                    (_dups == null) ? null : _dups.child(), currValue);
            return ctxt;
        }
        return (CodecWriteContextOld) ctxt.reset(TYPE_ARRAY, currValue);
    }

    public CodecWriteContextOld createChildObjectContext() {
    	CodecWriteContextOld ctxt = (CodecWriteContextOld) _child;
        if (ctxt == null) {
            _child = ctxt = new CodecWriteContextOld(TYPE_OBJECT, this,
                    (_dups == null) ? null : _dups.child(), null);
            return (CodecWriteContextOld) ctxt;
        }
        return (CodecWriteContextOld) ctxt.reset(TYPE_OBJECT, null);
    }

    /* @since 2.10 */
    public CodecWriteContextOld createChildObjectContext(Object currValue) {
    	CodecWriteContextOld ctxt = (CodecWriteContextOld) _child;
        if (ctxt == null) {
            _child = ctxt = new CodecWriteContextOld(TYPE_OBJECT, this,
                    (_dups == null) ? null : _dups.child(), currValue);
            return (CodecWriteContextOld) ctxt;
        }
        return (CodecWriteContextOld) ctxt.reset(TYPE_OBJECT, currValue);
    }
    
}
