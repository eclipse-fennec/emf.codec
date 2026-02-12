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
package org.eclipse.fennec.codec.jackson;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.context.EMFCodecReadContext;
import org.eclipse.fennec.codec.context.EMFContextHolder;

import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.io.ContentReference;
import tools.jackson.databind.util.TokenBufferReadContext;

/**
 * Token buffer read context that implements {@link EMFCodecReadContext}.
 * <p>
 * This context is used when replaying buffered tokens (e.g., after scanning
 * for featurePath-based type resolution). It preserves the EMF context from
 * the original parser context.
 * </p>
 * <p>
 * Key features:
 * <ul>
 *   <li>Preserves EMFContextHolder from original context when available</li>
 *   <li>Supports child context creation for nested objects</li>
 *   <li>Maintains type hints and effective configuration reference</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public class CodecTokenBufferReadContext extends TokenBufferReadContext implements EMFCodecReadContext {

    private EMFContextHolder holder;
    private TokenStreamContext _parent;
    private TokenStreamLocation _startLocation;

    /**
     * Creates a context from an existing token stream context.
     *
     * @param base the base context to copy from
     * @param startLoc the start location
     */
    protected CodecTokenBufferReadContext(TokenStreamContext base, TokenStreamLocation startLoc) {
        super(base, startLoc);
        _parent = base.getParent();
        _currentName = base.currentName();
        _currentValue = base.currentValue();
        _startLocation = startLoc;

        // Preserve EMF context from original if available
        if (base instanceof EMFCodecReadContext emfCtx) {
            holder = emfCtx.getEMFContextHolder();
        } else if (base.getParent() instanceof EMFCodecReadContext emfParent) {
            // Try parent context - create new holder with same effective config
            holder = new EMFContextHolder(emfParent.getEffectiveConfig());
            holder.setCurrentEObject(emfParent.getCurrentEObject());
            holder.setCurrentFeature(emfParent.getCurrentFeature());
            holder.setResource(emfParent.getResource());
            holder.setCurrentTypeHint(emfParent.getCurrentTypeHint());
        } else {
            holder = new EMFContextHolder();
        }
    }

    /**
     * Creates a child context from a parent CodecTokenBufferReadContext.
     *
     * @param parent the parent context
     * @param type the context type (TYPE_ARRAY or TYPE_OBJECT)
     * @param index the current index
     */
    protected CodecTokenBufferReadContext(CodecTokenBufferReadContext parent, int type, int index) {
        super(parent, type, index);
        _parent = parent;
        _startLocation = parent._startLocation;

        // Child inherits effective config but gets fresh state
        holder = new EMFContextHolder(parent.getEffectiveConfig());
    }

    /**
     * Creates an empty root context.
     */
    protected CodecTokenBufferReadContext() {
        super();
        _parent = null;
        _startLocation = TokenStreamLocation.NA;
        holder = new EMFContextHolder();
    }

    /**
     * Creates a context from an original context with content reference.
     *
     * @param origContext the original context
     * @param contentRef the content reference for location info
     */
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

        // Preserve EMF context from original
        if (origContext instanceof EMFCodecReadContext emfCtx) {
            holder = emfCtx.getEMFContextHolder();
        } else {
            holder = new EMFContextHolder();
        }
    }

    /**
     * Creates a root context from an original context.
     *
     * @param origContext the original context (may be null)
     * @return a new root context
     */
    public static CodecTokenBufferReadContext createRootContext(TokenStreamContext origContext) {
        if (origContext == null) {
            return new CodecTokenBufferReadContext();
        }
        return new CodecTokenBufferReadContext(origContext, ContentReference.unknown());
    }

    // ========== EMFCodecContext implementation ==========

    @Override
    public void setCurrentFeature(EStructuralFeature feature) {
        holder.setCurrentFeature(feature);
    }

    @Override
    public EStructuralFeature getCurrentFeature() {
        return holder.getCurrentFeature();
    }

    @Override
    public void setCurrentEObject(EObject eObj) {
        holder.setCurrentEObject(eObj);
    }

    @Override
    public EObject getCurrentEObject() {
        return holder.getCurrentEObject();
    }

    @Override
    public void setResource(Resource resource) {
        holder.setResource(resource);
    }

    @Override
    public Resource getResource() {
        return holder.getResource();
    }

    @Override
    public void resetFeature() {
        holder.resetFeature();
    }

    @Override
    public EffectiveCodecConfig getEffectiveConfig() {
        return holder.getEffectiveConfig();
    }

    @Override
    public EMFContextHolder getEMFContextHolder() {
        return holder;
    }

    @Override
    public void setEMFContextHolder(EMFContextHolder holder) {
        this.holder = holder;
    }

    // ========== EMFCodecReadContext implementation ==========

    @Override
    public CodecTokenBufferReadContext createChildArrayContext(int lineNr, int colNr) {
        ++_index;
        return new CodecTokenBufferReadContext(this, TYPE_ARRAY, -1);
    }

    @Override
    public CodecTokenBufferReadContext createChildArrayContext() {
        ++_index;
        return new CodecTokenBufferReadContext(this, TYPE_ARRAY, -1);
    }

    @Override
    public CodecTokenBufferReadContext createChildObjectContext(int lineNr, int colNr) {
        ++_index;
        return new CodecTokenBufferReadContext(this, TYPE_OBJECT, -1);
    }

    @Override
    public CodecTokenBufferReadContext createChildObjectContext() {
        ++_index;
        return new CodecTokenBufferReadContext(this, TYPE_OBJECT, -1);
    }

    @Override
    public CodecTokenBufferReadContext parentOrCopy() {
        if (_parent instanceof CodecTokenBufferReadContext) {
            return (CodecTokenBufferReadContext) _parent;
        }
        if (_parent == null) {
            return new CodecTokenBufferReadContext();
        }
        return new CodecTokenBufferReadContext(_parent, _startLocation);
    }

    @Override
    public CodecTokenBufferReadContext reset(int type, int lineNr, int colNr) {
        _type = type;
        _index = -1;
        _currentName = null;
        _currentValue = null;
        return this;
    }

    @Override
    public TokenStreamContext clearAndGetParent() {
        _currentValue = null;
        return getParent();
    }

    @Override
    public boolean hasParentContext() {
        return _parent != null;
    }

    @Override
    public TokenStreamContext getParent() {
        return _parent;
    }

    @Override
    public EClass getCurrentTypeHint() {
        return holder.getCurrentTypeHint();
    }

    @Override
    public void setCurrentTypeHint(EClass typeHint) {
        holder.setCurrentTypeHint(typeHint);
    }

    @Override
    public EClass resolveEClass(String typeValue) {
        if (typeValue == null || typeValue.isEmpty()) {
            return null;
        }

        EffectiveCodecConfig config = getEffectiveConfig();
        if (config == null) {
            return null;
        }

        // Try as full URI first using the effective config
        return config.resolveEClassByURI(typeValue);
    }

    @Override
    public EClass resolveEClass(String typeValue, EClass hint) {
        if (typeValue != null && !typeValue.isEmpty()) {
            EClass resolved = resolveEClass(typeValue);
            if (resolved != null) {
                return resolved;
            }
        }
        return hint;
    }
}
