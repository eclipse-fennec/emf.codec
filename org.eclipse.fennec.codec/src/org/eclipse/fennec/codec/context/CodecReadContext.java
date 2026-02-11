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
package org.eclipse.fennec.codec.context;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;

import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.exc.StreamReadException;

/**
 * Read context implementation for EMF codec deserialization operations.
 * <p>
 * This class extends {@link TokenStreamContext} and implements {@link EMFCodecReadContext}
 * to provide EMF-aware context during Jackson deserialization. It tracks the current
 * EObject, feature, resource, and provides type resolution capabilities.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public class CodecReadContext extends TokenStreamContext implements EMFCodecReadContext {

    private EMFContextHolder holder;

    /** Cached child context for reuse */
    protected CodecReadContext child;

    /** Parent context */
    protected CodecReadContext parent;

    /** Current field name */
    protected String currentName;

    /** Current value being deserialized */
    protected Object currentValue;

    /**
     * Creates a root read context.
     *
     * @param effectiveConfig the effective codec configuration
     */
    public CodecReadContext(EffectiveCodecConfig effectiveConfig) {
        super(TYPE_ROOT, -1);
        this.holder = new EMFContextHolder(effectiveConfig);
    }

    /**
     * Creates a child read context.
     *
     * @param parent the parent context
     * @param nestingDepth the nesting depth
     * @param type the context type (TYPE_ARRAY, TYPE_OBJECT)
     */
    protected CodecReadContext(CodecReadContext parent, int nestingDepth, int type) {
        super(type, -1);
        this.parent = parent;
        // Share the effective config from parent
        this.holder = new EMFContextHolder(parent.holder.getEffectiveConfig());
    }

    /**
     * Creates a root read context.
     *
     * @param effectiveConfig the effective codec configuration
     * @return the root context
     */
    public static CodecReadContext createRootContext(EffectiveCodecConfig effectiveConfig) {
        return new CodecReadContext(effectiveConfig);
    }

    @Override
    public EStructuralFeature getCurrentFeature() {
        return holder.getCurrentFeature();
    }

    @Override
    public void setCurrentFeature(EStructuralFeature feature) {
        holder.setCurrentFeature(feature);
    }

    @Override
    public EObject getCurrentEObject() {
        return holder.getCurrentEObject();
    }

    @Override
    public void setCurrentEObject(EObject eObject) {
        holder.setCurrentEObject(eObject);
    }

    @Override
    public void resetFeature() {
        holder.resetFeature();
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
    public EffectiveCodecConfig getEffectiveConfig() {
        return holder.getEffectiveConfig();
    }

    @Override
    public CodecReadContext createChildArrayContext(int lineNr, int colNr) {
        CodecReadContext ctxt = child;
        if (ctxt == null) {
            child = ctxt = new CodecReadContext(this, _nestingDepth + 1, TYPE_ARRAY);
        } else {
            ctxt.reset(TYPE_ARRAY, lineNr, colNr);
        }
        return ctxt;
    }

    @Override
    public CodecReadContext createChildObjectContext(int lineNr, int colNr) {
        CodecReadContext ctxt = child;
        if (ctxt == null) {
            child = ctxt = new CodecReadContext(this, _nestingDepth + 1, TYPE_OBJECT);
            return ctxt;
        }
        ctxt.reset(TYPE_OBJECT, lineNr, colNr);
        return ctxt;
    }

    @Override
    public CodecReadContext reset(int type, int lineNr, int colNr) {
        _type = type;
        _index = -1;
        currentName = null;
        currentValue = null;
        return this;
    }

    @Override
    public CodecReadContext clearAndGetParent() {
        currentValue = null;
        return getParent();
    }

    @Override
    public void setCurrentName(String name) throws StreamReadException {
        currentName = name;
    }

    @Override
    public boolean hasCurrentName() {
        return currentName != null;
    }

    @Override
    public CodecReadContext getParent() {
        return parent;
    }

    @Override
    public String currentName() {
        return currentName;
    }

    @Override
    public Object currentValue() {
        return currentValue;
    }

    @Override
    public void assignCurrentValue(Object v) {
        currentValue = v;
    }

    @Override
    public boolean hasParentContext() {
        return parent != null;
    }

    @Override
    public EMFContextHolder getEMFContextHolder() {
        return holder;
    }

    @Override
    public void setEMFContextHolder(EMFContextHolder holder) {
        this.holder = holder;
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
        // Priority 1: Try to resolve from typeValue
        if (typeValue != null && !typeValue.isEmpty()) {
            EClass resolved = resolveEClass(typeValue);
            if (resolved != null) {
                return resolved;
            }
        }

        // Priority 2: Return hint (caller must check instantiability)
        return hint;
    }
}
