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

import tools.jackson.core.json.DupDetector;
import tools.jackson.core.json.JsonReadContext;

/**
 * JSON-specific read context for EMF codec deserialization.
 * <p>
 * This class extends {@link JsonReadContext} and implements {@link EMFCodecReadContext}
 * to provide EMF-aware context during JSON deserialization. It tracks the current
 * EObject, feature, resource, type hints, and provides type resolution capabilities.
 * </p>
 * <p>
 * For non-JSON formats (MongoDB, CSV, etc.), use {@link org.eclipse.fennec.codec.context.CodecReadContext}
 * which extends {@link tools.jackson.core.TokenStreamContext} directly.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public class CodecJsonReadContext extends JsonReadContext implements EMFCodecReadContext {

    private EMFContextHolder holder;

    /**
     * Creates a new JSON read context.
     *
     * @param parent the parent context, or null for root
     * @param nestingDepth the nesting depth (0 for root)
     * @param dups duplicate detector, or null if not enabled
     * @param type the context type (TYPE_ROOT, TYPE_ARRAY, TYPE_OBJECT)
     * @param lineNr the line number
     * @param colNr the column number
     */
    public CodecJsonReadContext(JsonReadContext parent, int nestingDepth, DupDetector dups,
            int type, int lineNr, int colNr) {
        super(parent, nestingDepth, dups, type, lineNr, colNr);
        this.holder = new EMFContextHolder();
    }

    /**
     * Creates a new JSON read context with an effective configuration.
     *
     * @param parent the parent context, or null for root
     * @param nestingDepth the nesting depth (0 for root)
     * @param dups duplicate detector, or null if not enabled
     * @param type the context type (TYPE_ROOT, TYPE_ARRAY, TYPE_OBJECT)
     * @param lineNr the line number
     * @param colNr the column number
     * @param effectiveConfig the effective codec configuration
     */
    public CodecJsonReadContext(JsonReadContext parent, int nestingDepth, DupDetector dups,
            int type, int lineNr, int colNr, EffectiveCodecConfig effectiveConfig) {
        super(parent, nestingDepth, dups, type, lineNr, colNr);
        this.holder = new EMFContextHolder(effectiveConfig);
    }

    /**
     * Creates a root context with duplicate detection.
     *
     * @param lineNr the line number
     * @param colNr the column number
     * @param dups duplicate detector, or null if not enabled
     * @return the root context
     */
    public static CodecJsonReadContext createRootContext(int lineNr, int colNr, DupDetector dups) {
        return new CodecJsonReadContext(null, 0, dups, TYPE_ROOT, lineNr, colNr);
    }

    /**
     * Creates a root context with duplicate detection.
     *
     * @param dups duplicate detector, or null if not enabled
     * @return the root context
     */
    public static CodecJsonReadContext createRootContext(DupDetector dups) {
        return new CodecJsonReadContext(null, 0, dups, TYPE_ROOT, 1, 0);
    }

    /**
     * Creates a root context with effective configuration.
     *
     * @param dups duplicate detector, or null if not enabled
     * @param effectiveConfig the effective codec configuration
     * @return the root context
     */
    public static CodecJsonReadContext createRootContext(DupDetector dups, EffectiveCodecConfig effectiveConfig) {
        return new CodecJsonReadContext(null, 0, dups, TYPE_ROOT, 1, 0, effectiveConfig);
    }

    @Override
    public CodecJsonReadContext createChildArrayContext(int lineNr, int colNr) {
        CodecJsonReadContext ctxt = (CodecJsonReadContext) _child;
        if (ctxt == null) {
            _child = ctxt = new CodecJsonReadContext(this, _nestingDepth + 1,
                    (_dups == null) ? null : _dups.child(), TYPE_ARRAY, lineNr, colNr);
            // Child inherits effective config from parent
            ctxt.holder = new EMFContextHolder(this.holder.getEffectiveConfig());
        } else {
            ctxt.reset(TYPE_ARRAY, lineNr, colNr);
            // Reset holder state but keep effective config
            ctxt.holder.setCurrentEObject(null);
            ctxt.holder.setCurrentFeature(null);
            ctxt.holder.setCurrentTypeHint(null);
        }
        return ctxt;
    }

    @Override
    public CodecJsonReadContext createChildObjectContext(int lineNr, int colNr) {
        CodecJsonReadContext ctxt = (CodecJsonReadContext) _child;
        if (ctxt == null) {
            _child = ctxt = new CodecJsonReadContext(this, _nestingDepth + 1,
                    (_dups == null) ? null : _dups.child(), TYPE_OBJECT, lineNr, colNr);
            // Child inherits effective config from parent
            ctxt.holder = new EMFContextHolder(this.holder.getEffectiveConfig());
        } else {
            ctxt.reset(TYPE_OBJECT, lineNr, colNr);
            // Reset holder state but keep effective config
            ctxt.holder.setCurrentEObject(null);
            ctxt.holder.setCurrentFeature(null);
            ctxt.holder.setCurrentTypeHint(null);
        }
        return ctxt;
    }

    @Override
    public CodecJsonReadContext getParent() {
        return (CodecJsonReadContext) _parent;
    }

    @Override
    public CodecJsonReadContext clearAndGetParent() {
        _currentValue = null;
        return getParent();
    }

    // ========== EMFCodecContext implementation ==========

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
    public Resource getResource() {
        return holder.getResource();
    }

    @Override
    public void setResource(Resource resource) {
        holder.setResource(resource);
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
    public boolean hasParentContext() {
        return _parent != null;
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
        EClass resolved = config.resolveEClassByURI(typeValue);
        if (resolved != null) {
            return resolved;
        }

        // TODO: Support SIMPLE_NAME and MAPPED strategies
        // This will require additional configuration from EffectiveClassConfig

        return null;
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

    // ========== Context Schema Methods (for NAME strategy) ==========

    /**
     * Gets the context schema URI for NAME strategy type resolution.
     *
     * @return the context schema URI, or null if not set
     */
    public String getContextSchemaUri() {
        return holder.getContextSchemaUri();
    }

    /**
     * Sets the context schema URI for NAME strategy type resolution.
     *
     * @param contextSchemaUri the context schema URI
     */
    public void setContextSchemaUri(String contextSchemaUri) {
        holder.setContextSchemaUri(contextSchemaUri);
    }
}
