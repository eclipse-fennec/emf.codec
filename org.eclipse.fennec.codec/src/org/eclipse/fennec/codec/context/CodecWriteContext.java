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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonWriteContext;

/**
 * Write context implementation for EMF codec serialization operations.
 * <p>
 * This class extends {@link TokenStreamContext} and implements {@link EMFCodecWriteContext}
 * to provide EMF-aware context during Jackson serialization. It tracks the current
 * EObject, feature, resource, and provides access to the effective configuration.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public class CodecWriteContext extends TokenStreamContext implements EMFCodecWriteContext {

    /** Write status: OK as is, no separator needed */
    public static final int STATUS_OK_AS_IS = 0;
    /** Write status: OK, but need comma before */
    public static final int STATUS_OK_AFTER_COMMA = 1;
    /** Write status: OK, but need colon before (after field name) */
    public static final int STATUS_OK_AFTER_COLON = 2;
    /** Write status: OK, but need space before (root context) */
    public static final int STATUS_OK_AFTER_SPACE = 3;
    /** Write status: expecting a value */
    public static final int STATUS_EXPECT_VALUE = 4;
    /** Write status: expecting a field name */
    public static final int STATUS_EXPECT_NAME = 5;

    private EMFContextHolder holder;

    /** Parent context for this context; null for root context */
    protected final CodecWriteContext parent;

    /** Duplicate detection for field names */
    protected DupDetector dups;

    /** Cached child context for reuse */
    protected CodecWriteContext child;

    /** Current field name (only for OBJECT contexts) */
    protected String currentName;

    /** Current value being serialized */
    protected Object currentValue;

    /** Flag indicating we just wrote a field name */
    protected boolean gotName;

    /**
     * Creates a new codec write context.
     *
     * @param type the context type (TYPE_ROOT, TYPE_ARRAY, TYPE_OBJECT)
     * @param parent the parent context, or null for root
     * @param dups the duplicate detector, or null
     * @param currentValue the current value being serialized
     * @param effectiveConfig the effective codec configuration
     */
    protected CodecWriteContext(int type, CodecWriteContext parent, DupDetector dups,
                                 Object currentValue, EffectiveCodecConfig effectiveConfig) {
        super();
        _type = type;
        this.parent = parent;
        _nestingDepth = parent == null ? 0 : parent._nestingDepth + 1;
        this.dups = dups;
        _index = -1;
        this.currentValue = currentValue;
        this.holder = new EMFContextHolder(effectiveConfig);
    }

    /**
     * Creates a root codec write context.
     *
     * @param dd the duplicate detector, or null
     * @param effectiveConfig the effective codec configuration
     * @return the root context
     */
    public static CodecWriteContext createRootContext(DupDetector dd, EffectiveCodecConfig effectiveConfig) {
        return new CodecWriteContext(TYPE_ROOT, null, dd, null, effectiveConfig);
    }

    /**
     * Checks if the given context is a CodecWriteContext.
     *
     * @param ctx the context to check
     * @return true if ctx is a CodecWriteContext
     */
    public static boolean isCodecContext(TokenStreamContext ctx) {
        return ctx instanceof CodecWriteContext;
    }

    /**
     * Writes a feature and field name to the output, handling both codec and
     * standard contexts.
     *
     * @param ctx the context
     * @param feature the feature being written
     * @param fieldName the field name
     * @return the write status
     */
    public static int writeFeatureAndFieldName(TokenStreamContext ctx, EStructuralFeature feature, String fieldName) {
        if (isNull(ctx) || !isCodecContext(ctx)) {
            if (ctx instanceof JsonWriteContext jsonCtx) {
                return jsonCtx.writeName(fieldName);
            }
            throw new IllegalArgumentException("A non-null CodecWriteContext must be provided");
        }
        if (nonNull(feature) && nonNull(fieldName)) {
            return ((CodecWriteContext) ctx).writeFeatureAndFieldName(feature, fieldName);
        } else {
            return STATUS_EXPECT_NAME;
        }
    }

    @Override
    public CodecWriteContext reset(int type, Object currValue) {
        _type = type;
        _index = -1;
        currentName = null;
        gotName = false;
        currentValue = currValue;
        if (dups != null) {
            dups.reset();
        }
        return this;
    }

    @Override
    public void setCurrentFeature(EStructuralFeature feature) {
        holder.setCurrentFeature(feature);
    }

    @Override
    public EStructuralFeature getCurrentFeature() {
        return holder.getCurrentFeature();
    }

    @Override
    public void resetFeature() {
        holder.resetFeature();
    }

    @Override
    public void setCurrentEObject(EObject eObject) {
        holder.setCurrentEObject(eObject);
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
    public EffectiveCodecConfig getEffectiveConfig() {
        return holder.getEffectiveConfig();
    }

    @Override
    public int writeFeatureAndFieldName(EStructuralFeature feature, String name) {
        int r = writeName(name);
        if (r == STATUS_OK_AS_IS || r == STATUS_OK_AFTER_COMMA) {
            holder.setCurrentFeature(feature);
        }
        return r;
    }

    /**
     * Writes a field name.
     *
     * @param name the field name
     * @return the write status
     * @throws StreamWriteException if duplicate detected
     */
    public int writeName(String name) throws StreamWriteException {
        if ((_type != TYPE_OBJECT) || gotName) {
            return STATUS_EXPECT_VALUE;
        }
        gotName = true;
        currentName = name;
        if (dups != null) {
            checkDup(dups, name);
        }
        return (_index < 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_COMMA;
    }

    @Override
    public CodecWriteContext createChildArrayContext() {
        CodecWriteContext ctxt = child;
        if (ctxt == null) {
            child = ctxt = new CodecWriteContext(TYPE_ARRAY, this,
                    (dups == null) ? null : dups.child(), null, holder.getEffectiveConfig());
            return ctxt;
        }
        return ctxt.reset(TYPE_ARRAY, null);
    }

    @Override
    public CodecWriteContext createChildArrayContext(Object currValue) {
        CodecWriteContext ctxt = child;
        if (ctxt == null) {
            child = ctxt = new CodecWriteContext(TYPE_ARRAY, this,
                    (dups == null) ? null : dups.child(), currValue, holder.getEffectiveConfig());
            return ctxt;
        }
        return ctxt.reset(TYPE_ARRAY, currValue);
    }

    @Override
    public CodecWriteContext createChildObjectContext() {
        CodecWriteContext ctxt = child;
        if (ctxt == null) {
            child = ctxt = new CodecWriteContext(TYPE_OBJECT, this,
                    (dups == null) ? null : dups.child(), null, holder.getEffectiveConfig());
            return ctxt;
        }
        return ctxt.reset(TYPE_OBJECT, null);
    }

    @Override
    public CodecWriteContext createChildObjectContext(Object currValue) {
        CodecWriteContext ctxt = child;
        if (ctxt == null) {
            child = ctxt = new CodecWriteContext(TYPE_OBJECT, this,
                    (dups == null) ? null : dups.child(), currValue, holder.getEffectiveConfig());
            return ctxt;
        }
        return ctxt.reset(TYPE_OBJECT, currValue);
    }

    @Override
    public CodecWriteContext getParent() {
        return parent;
    }

    @Override
    public String currentName() {
        return currentName;
    }

    @Override
    public boolean hasCurrentName() {
        return gotName;
    }

    @Override
    public CodecWriteContext clearAndGetParent() {
        currentValue = null;
        return parent;
    }

    /**
     * Writes a value and updates the index.
     *
     * @return the write status
     */
    public int writeValue() {
        if (_type == TYPE_OBJECT) {
            if (!gotName) {
                return STATUS_EXPECT_NAME;
            }
            gotName = false;
            ++_index;
            return STATUS_OK_AFTER_COLON;
        }

        if (_type == TYPE_ARRAY) {
            int ix = _index;
            ++_index;
            return (ix < 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_COMMA;
        }

        // Root context
        ++_index;
        return (_index == 0) ? STATUS_OK_AS_IS : STATUS_OK_AFTER_SPACE;
    }

    private void checkDup(DupDetector dd, String name) throws StreamWriteException {
        if (dd.isDup(name)) {
            Object src = dd.getSource();
            throw new StreamWriteException(
                    ((src instanceof JsonGenerator) ? ((JsonGenerator) src) : null),
                    "Duplicate Object property \"" + name + "\"");
        }
    }

    @Override
    public void assignCurrentValue(Object v) {
        currentValue = v;
    }

    @Override
    public Object currentValue() {
        return currentValue;
    }

    @Override
    public EMFContextHolder getEMFContextHolder() {
        return holder;
    }

    @Override
    public void setEMFContextHolder(EMFContextHolder holder) {
        this.holder = holder;
    }
}
