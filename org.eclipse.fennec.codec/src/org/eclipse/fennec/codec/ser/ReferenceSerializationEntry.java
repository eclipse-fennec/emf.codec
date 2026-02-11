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
package org.eclipse.fennec.codec.ser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.CodecWriteContext;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.util.EMapHelper;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.eclipse.fennec.model.metadata.SerializationFormat;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.SerializationContext;

/**
 * Serialization entry for EReference values.
 * <p>
 * Handles the serialization of reference values based on the
 * feature configuration. Distinguishes between containment references
 * (serialized inline) and non-containment references (serialized as refs).
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#65-reference-serialization">Spec 6.5: Reference Serialization</a>
 * @author Mark Hoffmann
 * @since 2025-12-16
 */
public class ReferenceSerializationEntry implements SerializationEntry {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ReferenceSerializationEntry.class.getName());

    private final FeatureConfig config;
    private final EReference reference;
    private final String refKey;
    private final SerializationFormat refFormat;
    private final boolean smartCompression;
    private final EffectiveCodecConfig codecConfig;
    private final ReferenceValueWriter<?> containmentWriter;
    private final CodecValueWriter<EObject, EReference> uriWriter;
    private final CodecEntryContext entryContext;

    /**
     * Creates a new ReferenceSerializationEntry with the feature configuration.
     *
     * @param config the feature configuration
     * @param reference the EReference to serialize
     * @param refKey the JSON key to use for non-containment reference URIs
     */
    public ReferenceSerializationEntry(FeatureConfig config, EReference reference, String refKey) {
        this(config, reference, null, refKey, SerializationFormat.STRUCTURED, false, null, null);
    }

    /**
     * Creates a new ReferenceSerializationEntry with smart compression support.
     *
     * @param config the feature configuration
     * @param reference the EReference to serialize
     * @param refKey the JSON key to use for non-containment reference URIs
     * @param smartCompression whether smart compression is enabled
     */
    public ReferenceSerializationEntry(FeatureConfig config, EReference reference,
            String refKey, boolean smartCompression) {
        this(config, reference, null, refKey, SerializationFormat.STRUCTURED, smartCompression, null, null);
    }

    /**
     * Creates a new ReferenceSerializationEntry with full configuration support (legacy signature).
     *
     * @param config the feature configuration
     * @param reference the EReference to serialize
     * @param refKey the JSON key to use for non-containment reference URIs
     * @param smartCompression whether smart compression is enabled
     * @param codecConfig the effective codec configuration (for expand settings)
     * @param entryContext the codec entry context for custom writers (may be null)
     */
    public ReferenceSerializationEntry(FeatureConfig config, EReference reference,
            String refKey, boolean smartCompression, EffectiveCodecConfig codecConfig,
            CodecEntryContext entryContext) {
        this(config, reference, null, refKey, SerializationFormat.STRUCTURED, smartCompression, codecConfig, entryContext);
    }

    /**
     * Creates a new ReferenceSerializationEntry with full configuration support.
     *
     * @param config the feature configuration
     * @param reference the EReference to serialize
     * @param refConfig the reference-specific configuration (may be null for defaults)
     * @param smartCompression whether smart compression is enabled
     * @param codecConfig the effective codec configuration (for expand settings)
     * @param entryContext the codec entry context for custom writers (may be null)
     */
    public ReferenceSerializationEntry(FeatureConfig config, EReference reference,
            ReferenceConfig refConfig, boolean smartCompression, EffectiveCodecConfig codecConfig,
            CodecEntryContext entryContext) {
        this(config, reference, refConfig,
                refConfig != null ? refConfig.getRefKey() : "$ref",
                refConfig != null ? refConfig.getFormat() : SerializationFormat.STRUCTURED,
                smartCompression, codecConfig, entryContext);
    }

    /**
     * Internal constructor with all parameters.
     */
    private ReferenceSerializationEntry(FeatureConfig config, EReference reference,
            ReferenceConfig refConfig, String refKey, SerializationFormat refFormat,
            boolean smartCompression, EffectiveCodecConfig codecConfig,
            CodecEntryContext entryContext) {
        this.config = config;
        this.reference = reference;
        this.refKey = refKey;
        this.refFormat = refFormat;
        this.smartCompression = smartCompression;
        this.codecConfig = codecConfig;
        this.entryContext = entryContext;

        String writerName = config.getValueWriterName();
        CodecValueRegistry valueRegistry = entryContext != null ? entryContext.getValueRegistry() : null;
        if (writerName != null && !writerName.isEmpty() && valueRegistry != null) {
            CodecValueWriter<?, ?> writer = valueRegistry.getWriter(writerName).orElse(null);

            if (writer instanceof ReferenceValueWriter<?> refWriter) {
                if (refWriter.canHandle(reference)) {
                    this.containmentWriter = refWriter;
                    this.uriWriter = null;
                } else {
                    LOGGER.warning("ReferenceValueWriter '" + writerName + "' cannot handle reference '" +
                            reference.getName() + "' of type " + reference.getEReferenceType().getName());
                    this.containmentWriter = null;
                    this.uriWriter = null;
                }
            } else if (writer != null) {
                @SuppressWarnings("unchecked")
                CodecValueWriter<EObject, EReference> refUriWriter =
                        (CodecValueWriter<EObject, EReference>) writer;
                this.containmentWriter = null;
                this.uriWriter = refUriWriter;
            } else {
                this.containmentWriter = null;
                this.uriWriter = null;
            }
        } else {
            this.containmentWriter = null;
            this.uriWriter = null;
        }
    }

    @Override
    public String getKey() {
        return config.getKey();
    }

    @Override
    public boolean shouldSerialize(SerializationState state) {
        // VISIBILITY GATE (step 1 in spec):
        // Uses shouldSerialize() from FeatureConfig which handles:
        // - ignore, ignoreWrite, ignoreFeatures
        // - transient/volatile/derived + forceWrite
        // Note: forceWrite only affects the visibility gate, NOT the value gate below
        if (!config.shouldSerialize()) {
            return false;
        }

        // VALUE GATE (step 4 in spec):
        // These checks apply regardless of forceWrite.
        // forceWrite allows volatile features to pass visibility gate,
        // but value-based conditions (null, empty) still apply.
        Object value = state.getValue(reference);

        if (value == null) {
            return config.isSerializeNull();
        }

        if (reference.isMany() && value instanceof EList<?> list && list.isEmpty()) {
            return config.isSerializeEmpty();
        }

        return true;
    }

    @Override
    public void serialize(SerializationState state, JsonGenerator gen, SerializationContext ctxt) {
        Object value = state.getValue(reference);

        if (value == null) {
            gen.writeNullProperty(config.getKey());
            return;
        }

        gen.writeName(config.getKey());

        if (reference.isMany() && value instanceof EList<?> list) {
            if (EMapHelper.isMapEntryReference(reference)) {
                serializeEMap(list, gen, ctxt);
            } else {
                gen.writeStartArray();
                for (Object item : list) {
                    if (item instanceof EObject target) {
                        serializeReference(target, gen, ctxt);
                    }
                }
                gen.writeEndArray();
            }
        } else if (value instanceof EObject target) {
            serializeReference(target, gen, ctxt);
        }
    }

    private void serializeReference(EObject target, JsonGenerator gen, SerializationContext ctxt) {
        if (reference.isContainment() && isCrossDocument(gen, target)) {
            writeReferenceObject(target, gen, true, ctxt);
        } else if (reference.isContainment()) {
            // Set the current reference for inline mapping reverse lookup.
            // TypeSerializationEntry uses this to find the correct discriminator value.
            ContextHelper.setCurrentSerializationReference(ctxt, reference);
            try {
                if (containmentWriter != null) {
                    writeWithContainmentWriter(target, gen, ctxt);
                } else {
                    ctxt.writeValue(gen, target);
                }
            } finally {
                ContextHelper.clearCurrentSerializationReference(ctxt);
            }
        } else if (shouldExpandReference(target)) {
            serializeExpandedReference(target, gen, ctxt);
        } else {
            writeReferenceObject(target, gen, false, ctxt);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeWithContainmentWriter(EObject target, JsonGenerator gen, SerializationContext ctxt) {
        if (entryContext == null) {
            throw new IllegalStateException("CodecEntryContext required for custom containment writer");
        }
        try {
            CodecWriterContext writerCtx = entryContext.createWriterContext(gen, ctxt);
            ((ReferenceValueWriter<EObject>) containmentWriter).write(target, reference, writerCtx);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Custom containment writer failed for reference: " + reference.getName(), e);
        }
    }

    private boolean shouldExpandReference(EObject target) {
        if (codecConfig == null) {
            return false;
        }
        if (target.eIsProxy()) {
            return false;
        }
        if (!codecConfig.shouldExpand(reference)) {
            return false;
        }
        if (codecConfig.isExpandIgnoreBidirectional() && reference.getEOpposite() != null) {
            return false;
        }
        return true;
    }

    private void serializeExpandedReference(EObject target, JsonGenerator gen, SerializationContext ctxt) {
        ctxt.writeValue(gen, target);
    }

    private boolean isCrossDocument(JsonGenerator gen, EObject target) {
        TokenStreamContext ctx = gen.streamWriteContext();
        if (!(ctx instanceof CodecWriteContext codecCtx)) {
            return false;
        }

        Resource sourceResource = codecCtx.getResource();

        if (target.eIsProxy() && target instanceof InternalEObject internalEObject) {
            URI proxyUri = internalEObject.eProxyURI();
            return sourceResource != null && sourceResource.getURI() != null
                    && !sourceResource.getURI().equals(proxyUri.trimFragment());
        }

        Resource targetResource = target.eResource();
        return sourceResource == null || sourceResource != targetResource;
    }

    /**
     * Writes a reference value in either PLAIN or STRUCTURED format.
     * <p>
     * PLAIN format: bare URI string (no type information)
     * <pre>
     * "employer": "//@employees.0"
     * </pre>
     *
     * STRUCTURED format: object with _type and $ref
     * <pre>
     * "employer": { "_type": "...", "$ref": "//@employees.0" }
     * </pre>
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/10-reference.md#11-plain-strategy">Spec: PLAIN Strategy</a>
     */
    private void writeReferenceObject(EObject target, JsonGenerator gen, boolean crossDocument,
            SerializationContext ctxt) {
        if (refFormat == SerializationFormat.PLAIN) {
            // PLAIN format: just write the URI string directly
            writeReferenceValue(target, gen, crossDocument, ctxt);
        } else {
            // STRUCTURED format: object with _type and $ref
            gen.writeStartObject();

            String typeUri = EcoreUtil.getURI(target.eClass()).toString();
            String effectiveType = applySmartCompressionToRef(typeUri, ctxt);
            gen.writeStringProperty("_type", effectiveType);

            gen.writeName(refKey);
            writeReferenceValue(target, gen, crossDocument, ctxt);

            gen.writeEndObject();
        }
    }

    private void writeReferenceValue(EObject target, JsonGenerator gen, boolean crossDocument,
            SerializationContext ctxt) {
        if (uriWriter != null && entryContext != null) {
            try {
                CodecWriterContext writerCtx = entryContext.createWriterContext(gen, ctxt);
                uriWriter.write(target, reference, writerCtx);
            } catch (IOException e) {
                throw new UncheckedIOException(
                        "Custom URI writer failed for reference: " + reference.getName(), e);
            }
            return;
        }

        String uri = getReferenceUri(gen, target, crossDocument);
        gen.writeString(uri);
    }

    private String applySmartCompressionToRef(String typeUri, SerializationContext ctxt) {
        if (!smartCompression) {
            return typeUri;
        }
        if (ContextHelper.isSameSchema(ctxt, typeUri)) {
            return ContextHelper.extractSimpleName(typeUri);
        }
        return typeUri;
    }

    private String getReferenceUri(JsonGenerator gen, EObject target, boolean crossDocument) {
        if (crossDocument) {
            TokenStreamContext ctx = gen.streamWriteContext();
            if (ctx instanceof CodecWriteContext codecCtx) {
                Resource sourceResource = codecCtx.getResource();
                if (sourceResource != null && sourceResource.getURI() != null) {
                    URI targetUri = EcoreUtil.getURI(target);
                    URI sourceUri = sourceResource.getURI();
                    URI relativeUri = targetUri.deresolve(sourceUri);
                    return relativeUri.toString();
                }
            }
            return EcoreUtil.getURI(target).toString();
        }

        if (target.eResource() != null) {
            return target.eResource().getURIFragment(target);
        }
        return target.eClass().getEPackage().getNsURI() + "#//" + target.eClass().getName();
    }

    // ========================================================================
    // EMap Serialization Support
    // ========================================================================

    private void serializeEMap(List<?> entries, JsonGenerator gen, SerializationContext ctxt) {
        EClass entryClass = reference.getEReferenceType();
        var keyFeature = EMapHelper.getKeyFeature(entryClass);
        var valueFeature = EMapHelper.getValueFeature(entryClass);

        gen.writeStartObject();

        for (Object item : entries) {
            if (item instanceof EObject entry) {
                Object keyValue = entry.eGet(keyFeature);
                String key = keyValue != null ? keyValue.toString() : "";

                gen.writeName(key);

                Object value = entry.eGet(valueFeature);
                serializeMapEntryValue(value, valueFeature, gen, ctxt);
            }
        }

        gen.writeEndObject();
    }

    private void serializeMapEntryValue(Object value, org.eclipse.emf.ecore.EStructuralFeature valueFeature,
            JsonGenerator gen, SerializationContext ctxt) {
        if (value == null) {
            gen.writeNull();
        } else if (value instanceof EObject eObject) {
            ctxt.writeValue(gen, eObject);
        } else if (valueFeature instanceof EAttribute) {
            serializeAttributeValue(value, gen);
        } else {
            gen.writeString(value.toString());
        }
    }

    private void serializeAttributeValue(Object value, JsonGenerator gen) {
        if (value instanceof String s) {
            gen.writeString(s);
        } else if (value instanceof Integer i) {
            gen.writeNumber(i);
        } else if (value instanceof Long l) {
            gen.writeNumber(l);
        } else if (value instanceof Double d) {
            gen.writeNumber(d);
        } else if (value instanceof Float f) {
            gen.writeNumber(f);
        } else if (value instanceof Boolean b) {
            gen.writeBoolean(b);
        } else if (value instanceof Number n) {
            gen.writeNumber(n.doubleValue());
        } else {
            gen.writeString(value.toString());
        }
    }
}
