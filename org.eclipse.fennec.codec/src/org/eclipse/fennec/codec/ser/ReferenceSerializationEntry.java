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
package org.eclipse.fennec.codec.ser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.CodecWriteContext;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.util.EMapHelper;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

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
 * @since 1.0
 */
public class ReferenceSerializationEntry implements SerializationEntry {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ReferenceSerializationEntry.class.getName());

    private final FeatureConfig config;
    private final EReference reference;
    private final String refKey;
    private final SerializationFormat refFormat;
    private final boolean smartCompression;
    private final EffectiveCodecConfig codecConfig;
    private final ReferenceValueWriter<?> referenceWriter;
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
                refConfig != null ? refConfig.getRefKey() : "_ref",
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
                    this.referenceWriter = refWriter;
                    this.uriWriter = null;
                } else {
                    report("ReferenceValueWriter '" + writerName + "' cannot handle reference '" +
                            reference.getName() + "' of type " + reference.getEReferenceType().getName()
                            + " (canHandle returned false) - using default reference serialization");
                    this.referenceWriter = null;
                    this.uriWriter = null;
                }
            } else if (writer != null) {
                @SuppressWarnings("unchecked")
                CodecValueWriter<EObject, EReference> refUriWriter =
                        (CodecValueWriter<EObject, EReference>) writer;
                this.referenceWriter = null;
                this.uriWriter = refUriWriter;
            } else {
                this.referenceWriter = null;
                this.uriWriter = null;
            }
        } else {
            this.referenceWriter = null;
            this.uriWriter = null;
        }
    }

    /**
     * Reports a fallback the codec had to make while writing this reference (issue #184).
     * <p>
     * Warnings, not errors: each case describes the codec writing the reference a different way
     * rather than refusing to write it. See {@code IdSerializationEntry.report} for the same
     * reasoning.
     * </p>
     */
    private void report(String message) {
        LOGGER.warning(message);
        if (entryContext != null && entryContext.getDiagnostics() != null) {
            entryContext.getDiagnostics().addWarning(message, "ReferenceSerializationEntry");
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

        if (config.isFlatten() && reference.isMany() && value instanceof EList<?> list
                && EMapHelper.isMapEntryReference(reference)) {
            serializeFlattenedEMap(list, gen, ctxt);
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
                        serializeReference(state.getEObject(), target, gen, ctxt);
                    }
                }
                gen.writeEndArray();
            }
        } else if (value instanceof EObject target) {
            serializeReference(state.getEObject(), target, gen, ctxt);
        }
    }

    private void serializeReference(EObject source, EObject target, JsonGenerator gen,
            SerializationContext ctxt) {
        ReferenceValueWriter<?> effectiveWriter = resolveEffectiveReferenceWriter(ctxt);
        if (reference.isContainment() && isCrossDocument(gen, source, target, ctxt)) {
            writeReferenceObject(source, target, gen, true, ctxt);
        } else if (reference.isContainment()) {
            // Set the current reference for inline mapping reverse lookup.
            // TypeSerializationEntry uses this to find the correct discriminator value.
            ContextHelper.setCurrentSerializationReference(ctxt, reference);
            try {
                if (effectiveWriter != null) {
                    writeWithReferenceWriter(effectiveWriter, target, gen, ctxt);
                } else {
                    ctxt.writeValue(gen, target);
                }
            } finally {
                ContextHelper.clearCurrentSerializationReference(ctxt);
            }
        } else if (effectiveWriter != null) {
            // Non-containment with explicit ReferenceValueWriter (e.g., JSON Schema):
            // the user bound a custom writer to this feature, honour it
            // regardless of containment mode.
            writeWithReferenceWriter(effectiveWriter, target, gen, ctxt);
        } else if (shouldExpandReference(target)) {
            serializeExpandedReference(target, gen, ctxt);
        } else {
            writeReferenceObject(source, target, gen, false, ctxt);
        }
    }

    /**
     * Resolves the effective custom writer for this reference.
     * <p>
     * Priority order:
     * <ol>
     *   <li>Instance binding from options (CODEC_FEATURE_VALUE_WRITER_INSTANCES) —
     *       bypasses the registry</li>
     *   <li>Pre-resolved writer from registry (via valueWriterName config/annotation)</li>
     * </ol>
     * The deprecated name-based option CODEC_FEATURE_VALUE_WRITERS is not supported
     * for references; a binding for this reference only produces a warning.
     * </p>
     *
     * @param ctxt the serialization context (may be null)
     * @return the effective writer, or null if none configured
     */
    private ReferenceValueWriter<?> resolveEffectiveReferenceWriter(SerializationContext ctxt) {
        if (ctxt != null) {
            Object instancesAttr = ctxt.getAttribute(ContextHelper.FEATURE_VALUE_WRITER_INSTANCES);
            if (instancesAttr instanceof Map<?, ?> instancesMap) {
                Object writer = instancesMap.get(reference);
                if (writer instanceof ReferenceValueWriter<?> refWriter) {
                    if (refWriter.canHandle(reference)) {
                        return refWriter;
                    }
                    report("ReferenceValueWriter instance '" + refWriter.getName() +
                            "' cannot handle reference '" + reference.getName() + "' of type " +
                            reference.getEReferenceType().getName() + ", falling back");
                }
            }

            String runtimeWriterName = ContextHelper.getFeatureValueWriter(ctxt, reference);
            if (runtimeWriterName != null && !runtimeWriterName.isEmpty() && referenceWriter == null) {
                report("CODEC_FEATURE_VALUE_WRITERS is deprecated and not supported for references — " +
                        "writer '" + runtimeWriterName + "' for reference '" + reference.getName() +
                        "' is ignored. Use CODEC_FEATURE_VALUE_WRITER_INSTANCES or the valueWriterName " +
                        "config property instead.");
            }
        }
        return referenceWriter;
    }

    @SuppressWarnings("unchecked")
    private void writeWithReferenceWriter(ReferenceValueWriter<?> writer, EObject target,
            JsonGenerator gen, SerializationContext ctxt) {
        if (entryContext == null) {
            throw new IllegalStateException("CodecEntryContext required for custom reference writer");
        }
        try {
            CodecWriterContext writerCtx = entryContext.createWriterContext(gen, ctxt);
            ((ReferenceValueWriter<EObject>) writer).write(target, reference, writerCtx);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Custom reference writer failed for reference: " + reference.getName(), e);
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

    /**
     * Resolves the source EMF Resource for the object currently being serialized.
     * <p>
     * Primary source is the {@link CodecWriteContext} (used by the direct
     * {@code CodecJsonFactory} path), then the {@link ContextHelper#RESOURCE} attribute
     * (set by the format-provider path). Neither exists on the plain JSON save path, so
     * the object being serialized is asked last — it always knows its own resource
     * (issue #113).
     * </p>
     *
     * @param gen the JSON generator
     * @param ctxt the serialization context (may be null)
     * @param source the object whose reference is being written (may be null)
     * @return the source resource, or null if it cannot be determined
     */
    private Resource resolveSourceResource(JsonGenerator gen, SerializationContext ctxt,
            EObject source) {
        TokenStreamContext ctx = gen.streamWriteContext();
        if (ctx instanceof CodecWriteContext codecCtx) {
            return codecCtx.getResource();
        }
        Resource fromContext = ContextHelper.getResource(ctxt);
        if (fromContext != null) {
            return fromContext;
        }
        return source != null ? source.eResource() : null;
    }

    /**
     * Tells whether the target has to be written as a reference instead of being inlined.
     * <p>
     * Follows EMF's rule in {@code XMLSaveImpl.saveElement}: a contained object that owns a
     * direct resource, or is a proxy, is written as a reference. That check needs no context
     * at all — the object itself carries the answer.
     * </p>
     */
    private boolean isCrossDocument(JsonGenerator gen, EObject source, EObject target,
            SerializationContext ctxt) {
        if (target.eIsProxy()) {
            return true;
        }
        if (target instanceof InternalEObject internalEObject
                && internalEObject.eDirectResource() != null) {
            Resource sourceResource = resolveSourceResource(gen, ctxt, source);
            return sourceResource != internalEObject.eDirectResource();
        }

        Resource targetResource = target.eResource();
        if (targetResource == null) {
            // No resource of its own, so nothing to reference: the object is part of the
            // document being written. Models generated with suppressNotification="true" end
            // up here for every contained child - their containment features are backed by a
            // BasicInternalEList, which never sets the child's container, so the child reports
            // neither container nor resource (issue #94's lists, seen via the Model Atlas
            // Scope.registries).
            return false;
        }

        Resource sourceResource = resolveSourceResource(gen, ctxt, source);
        if (sourceResource == null) {
            return false;
        }

        return sourceResource != targetResource;
    }

    /**
     * Writes a reference value in either PLAIN or STRUCTURED format.
     * <p>
     * PLAIN format: bare URI string (no type information)
     * <pre>
     * "employer": "//@employees.0"
     * </pre>
     *
     * STRUCTURED format: object with _type and _ref
     * <pre>
     * "employer": { "_type": "...", "_ref": "//@employees.0" }
     * </pre>
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/10-reference.md#11-plain-strategy">Spec: PLAIN Strategy</a>
     */
    private void writeReferenceObject(EObject source, EObject target, JsonGenerator gen, boolean crossDocument,
            SerializationContext ctxt) {
        if (refFormat == SerializationFormat.PLAIN) {
            // PLAIN format: just write the URI string directly
            writeReferenceValue(source, target, gen, crossDocument, ctxt);
        } else {
            // STRUCTURED format: object with _type and _ref
            gen.writeStartObject();

            String typeUri = EcoreUtil.getURI(target.eClass()).toString();
            String effectiveType = applySmartCompressionToRef(typeUri, ctxt);
            gen.writeStringProperty("_type", effectiveType);

            // The reference's type object is type context, so the fingerprint attaches here
            // too - sparsely, per the same first-touch rule (issue #73, B.3). This is what
            // makes cross-resource proxy creation version-correct: the reader has to pick the
            // right EClass instance at ref-read time to build the proxy at all.
            writeFingerprintIfDue(target, gen, ctxt);

            gen.writeName(refKey);
            writeReferenceValue(source, target, gen, crossDocument, ctxt);

            gen.writeEndObject();
        }
    }

    /**
     * Writes the in-band EPackage fingerprint into a STRUCTURED reference's type object when
     * one is due for the target's package (issue #73, B.3).
     * <p>
     * Shares the per-save pins with the object writer, so a reference into an already
     * announced package stays silent and only a package first entering the document - or one
     * deviating from its established pin - is marked. The key used is the unprefixed inner
     * form, because this sits inside the type object.
     * </p>
     *
     * @param target the referenced object
     * @param gen the JSON generator, positioned inside the reference's type object
     * @param ctxt the serialization context holding the per-save pins
     */
    private void writeFingerprintIfDue(EObject target, JsonGenerator gen, SerializationContext ctxt) {
        if (codecConfig == null || ctxt == null || target == null) {
            return;
        }
        TypeConfig typeConfig = codecConfig.resolveTypeConfig(target.eClass());
        if (typeConfig == null || !typeConfig.isFingerprintWriteEnabled()) {
            return;
        }
        EPackage ePackage = target.eClass().getEPackage();
        if (ePackage == null || !ContextHelper.getFingerprintPins(ctxt).isDue(ePackage)) {
            return;
        }
        MetadataService metadataService = codecConfig.getMetadataService();
        if (metadataService == null) {
            return;
        }
        PackageMetadata metadata = metadataService.getPackageMetadata(ePackage).orElse(null);
        if (metadata != null && metadata.getModelFingerprint() != null) {
            gen.writeStringProperty(typeConfig.getFingerprintKey(), metadata.getModelFingerprint());
        }
    }

    private void writeReferenceValue(EObject source, EObject target, JsonGenerator gen, boolean crossDocument,
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

        String uri = getReferenceUri(gen, source, target, crossDocument, ctxt);
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

    private String getReferenceUri(JsonGenerator gen, EObject source, EObject target,
            boolean crossDocument, SerializationContext ctxt) {
        Resource sourceResource = resolveSourceResource(gen, ctxt, source);

        // Unresolved proxy: preserve the proxy URI (which carries the id),
        // never fall back to the type URI. eResource() is null for proxies,
        // so this must be handled before the same-resource fragment branch.
        if (target.eIsProxy() && target instanceof InternalEObject internalEObject
                && internalEObject.eProxyURI() != null) {
            return deresolve(internalEObject.eProxyURI(), sourceResource).toString();
        }

        Resource targetResource = target.eResource();

        // Cross-document: explicit cross-document containment, or a non-containment
        // target that lives in a different resource. Write the full EMF URI so the
        // resource can be resolved on load; relative-ize it against the source URI.
        boolean crossResource = targetResource != null && sourceResource != null
                && targetResource != sourceResource;
        if (crossDocument || crossResource) {
            return deresolve(EcoreUtil.getURI(target), sourceResource).toString();
        }

        // Same-resource: write just the fragment.
        if (targetResource != null) {
            return targetResource.getURIFragment(target);
        }
        return target.eClass().getEPackage().getNsURI() + "#//" + target.eClass().getName();
    }

    /**
     * Relative-izes the given URI against the source resource URI when available.
     *
     * @param uri the URI to deresolve
     * @param sourceResource the source resource (may be null)
     * @return the deresolved URI, or the original URI if no source URI is available
     */
    private URI deresolve(URI uri, Resource sourceResource) {
        if (sourceResource != null && sourceResource.getURI() != null) {
            return uri.deresolve(sourceResource.getURI());
        }
        return uri;
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

    private void serializeFlattenedEMap(List<?> entries, JsonGenerator gen, SerializationContext ctxt) {
        EClass entryClass = reference.getEReferenceType();
        var keyFeature = EMapHelper.getKeyFeature(entryClass);
        var valueFeature = EMapHelper.getValueFeature(entryClass);

        for (Object item : entries) {
            if (item instanceof EObject entry) {
                Object keyValue = entry.eGet(keyFeature);
                String key = keyValue != null ? keyValue.toString() : "";

                gen.writeName(key);

                Object value = entry.eGet(valueFeature);
                serializeMapEntryValue(value, valueFeature, gen, ctxt);
            }
        }
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
