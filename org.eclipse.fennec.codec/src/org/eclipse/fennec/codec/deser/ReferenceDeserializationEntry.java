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
package org.eclipse.fennec.codec.deser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.context.EMFCodecReadContext;
import org.eclipse.fennec.codec.deser.DeserializationState.UnresolvedReference;
import org.eclipse.fennec.codec.jackson.CodecJsonReadContext;
import org.eclipse.fennec.codec.util.EMapHelper;
import org.eclipse.fennec.codec.util.TypeResolutionHelper;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.ReferenceValueReader;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.TokenBuffer;

/**
 * Deserialization entry that handles EReference values.
 * <p>
 * Supports:
 * <ul>
 *   <li>Containment references (inline objects)</li>
 *   <li>Non-containment references ($ref objects) - creates proxies</li>
 *   <li>Expanded non-containment references (no $ref) - creates orphan objects</li>
 *   <li>Multi-valued references (arrays)</li>
 *   <li>Null values</li>
 * </ul>
 * </p>
 * <p>
 * Non-containment reference handling depends on the presence of the {@code _ref} field:
 * <ul>
 *   <li>With {@code _ref}: Creates a proxy that can be resolved later</li>
 *   <li>Without {@code _ref}: Deserializes as an orphan object (expanded reference)</li>
 * </ul>
 * Orphan objects are fully deserialized but not contained - they have no resource assigned.
 * </p>
 *
 * @see FeatureConfig
 * @see <a href="docs/codec-v2-spec/07-reference.md">Spec: Reference Serialization</a>
 * @author Mark Hoffmann
 * @since 2025-12-16
 */
public class ReferenceDeserializationEntry implements DeserializationEntry {

    private static final Logger LOGGER = Logger.getLogger(ReferenceDeserializationEntry.class.getName());

    private final FeatureConfig config;
    private final EReference reference;
    private final String refKey;
    /** Custom reader for references (containment or non-containment) - returns EObject */
    private final ReferenceValueReader<?> referenceReader;
    /** Custom reader for non-containment reference URIs - returns String */
    private final CodecValueReader<String, EReference> uriReader;
    private final CodecEntryContext entryContext;

    /**
     * Creates a new ReferenceDeserializationEntry.
     *
     * @param config the effective feature configuration
     * @param reference the EReference to deserialize
     * @param refKey the key used for non-containment references (e.g., "$ref")
     */
    public ReferenceDeserializationEntry(FeatureConfig config, EReference reference, String refKey) {
        this(config, reference, refKey, null);
    }

    /**
     * Creates a new ReferenceDeserializationEntry with custom reader support.
     *
     * @param config the effective feature configuration
     * @param reference the EReference to deserialize
     * @param refKey the key used for non-containment references (e.g., "$ref")
     * @param entryContext the codec entry context for custom readers (may be null)
     */
    public ReferenceDeserializationEntry(FeatureConfig config, EReference reference,
            String refKey, CodecEntryContext entryContext) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.reference = Objects.requireNonNull(reference, "reference must not be null");
        this.refKey = Objects.requireNonNull(refKey, "refKey must not be null");
        this.entryContext = entryContext;

        // Pre-resolve the custom reader at construction time
        // We support two types of readers:
        // 1. ReferenceValueReader<T extends EObject> for inline object references (containment or non-containment)
        // 2. CodecValueReader<String, EReference> for non-containment URI transformation
        String readerName = config.getValueReaderName();
        CodecValueRegistry valueRegistry = entryContext != null ? entryContext.getValueRegistry() : null;
        if (readerName != null && !readerName.isEmpty() && valueRegistry != null) {
            CodecValueReader<?, ?> reader = valueRegistry.getReader(readerName).orElse(null);

            if (reader instanceof ReferenceValueReader<?> refReader) {
                // ReferenceValueReader for inline object references - returns EObject
                if (refReader.canHandle(reference)) {
                    this.referenceReader = refReader;
                    this.uriReader = null;
                } else {
                    LOGGER.warning("ReferenceValueReader '" + readerName + "' cannot handle reference '" +
                            reference.getName() + "' of type " + reference.getEReferenceType().getName());
                    this.referenceReader = null;
                    this.uriReader = null;
                }
            } else if (reader != null) {
                // Generic CodecValueReader for URI transformation - assume it returns String
                @SuppressWarnings("unchecked")
                CodecValueReader<String, EReference> stringReader =
                        (CodecValueReader<String, EReference>) reader;
                this.referenceReader = null;
                this.uriReader = stringReader;
            } else {
                this.referenceReader = null;
                this.uriReader = null;
            }
        } else {
            this.referenceReader = null;
            this.uriReader = null;
        }
    }

    @Override
    public String getKey() {
        return config.getKey();
    }

    @Override
    public void deserialize(DeserializationState state, JsonParser parser, DeserializationContext ctxt) {
        EObject eObject = state.getEObject();
        if (eObject == null) {
            String msg = "Cannot set reference '" + reference.getName() + "': EObject not yet created";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
            return;
        }

        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_NULL) {
            // Null reference - only set if changeable and single-valued
            if (reference.isChangeable() && !reference.isMany()) {
                eObject.eSet(reference, null);
            }
            return;
        }

        if (reference.isMany()) {
            // Check if this is an EMap (reference to Map.Entry types)
            if (EMapHelper.isMapEntryReference(reference) && parser.currentToken() == JsonToken.START_OBJECT) {
                deserializeEMap(state, parser, ctxt, eObject);
            } else {
                deserializeMultiValued(state, parser, ctxt, eObject);
            }
        } else {
            deserializeSingleValued(state, parser, ctxt, eObject);
        }
    }

    /**
     * Deserializes a single-valued reference.
     * <p>
     * Supports both STRUCTURED and PLAIN formats:
     * <ul>
     *   <li>STRUCTURED: {@code {"_type": "...", "$ref": "uri"}} - object with $ref</li>
     *   <li>PLAIN: {@code "uri"} - bare string (non-containment only)</li>
     * </ul>
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/10-reference.md#11-plain-strategy">Spec: PLAIN Strategy</a>
     */
    private void deserializeSingleValued(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EObject eObject) {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.START_OBJECT) {
            if (reference.isContainment() || referenceReader != null) {
                // Containment, or non-containment with explicit ReferenceValueReader:
                // deserialize inline object using the custom reader if available
                EObject child = deserializeContainedObject(state, parser, ctxt);
                if (child != null && reference.isChangeable()) {
                    eObject.eSet(reference, child);
                }
            } else {
                // Non-containment: check for $ref to determine proxy vs orphan
                deserializeNonContainmentObject(state, parser, ctxt, eObject, -1);
            }
        } else if (token == JsonToken.VALUE_STRING && !reference.isContainment()) {
            // PLAIN format: bare URI string for non-containment reference
            // Type resolution uses: 1) CODEC_FEATURE_TYPE_HINTS, 2) EReference.getEReferenceType()
            String refUri = readReferenceValue(parser, ctxt);
            state.addUnresolvedReference(new UnresolvedReference(eObject, reference, refUri, -1));
        } else {
            String msg = "Expected START_OBJECT" + (reference.isContainment() ? "" : " or VALUE_STRING") +
                    " for reference '" + reference.getName() + "', got: " + token;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "ReferenceDeserializationEntry");
        }
    }

    /**
     * Deserializes a multi-valued reference (array).
     */
    @SuppressWarnings("unchecked")
    private void deserializeMultiValued(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EObject eObject) {
        JsonToken token = parser.currentToken();

        if (token != JsonToken.START_ARRAY) {
            // Single object provided for multi-valued - treat as single element
            deserializeSingleElement(state, parser, ctxt, eObject, 0);
            return;
        }

        List<EObject> values = (List<EObject>) eObject.eGet(reference);
        int index = 0;

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (reference.isContainment() || referenceReader != null) {
                EObject child = deserializeContainedObject(state, parser, ctxt);
                if (child != null) {
                    values.add(child);
                }
            } else {
                // Non-containment: check for $ref to determine proxy vs orphan
                deserializeNonContainmentElement(state, parser, ctxt, eObject, values, index);
            }
            index++;
        }
    }

    /**
     * Deserializes a single element for a multi-valued reference.
     */
    @SuppressWarnings("unchecked")
    private void deserializeSingleElement(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EObject eObject, int index) {
        if (reference.isContainment() || referenceReader != null) {
            EObject child = deserializeContainedObject(state, parser, ctxt);
            if (child != null) {
                ((List<EObject>) eObject.eGet(reference)).add(child);
            }
        } else {
            List<EObject> values = (List<EObject>) eObject.eGet(reference);
            deserializeNonContainmentElement(state, parser, ctxt, eObject, values, index);
        }
    }

    /**
     * Deserializes a contained object.
     * <p>
     * Creates a child context for nested deserialization and recursively deserializes
     * the contained object. The EReference's eType is passed as a hint via the child
     * context to allow deserialization of nested objects without explicit _type.
     * </p>
     * <p>
     * Priority for type resolution (see spec 18-feature-type-hints.md):
     * <ol>
     *   <li>_type field in JSON (explicit type in data)</li>
     *   <li>CODEC_FEATURE_VALUE_READERS option (runtime value reader)</li>
     *   <li>CODEC_FEATURE_TYPE_HINTS option (runtime type hint)</li>
     *   <li>valueReaderName EAnnotation (static model config)</li>
     *   <li>EReference.eType (declared reference type)</li>
     * </ol>
     * </p>
     * <p>
     * This method uses proper context isolation:
     * <ul>
     *   <li>If using CodecJsonReadContext: creates child context with type hint</li>
     *   <li>Falls back to ContextHelper for backwards compatibility</li>
     * </ul>
     * </p>
     *
     * @param parentState the parent state
     * @param parser the JSON parser at START_OBJECT
     * @param ctxt the deserialization context
     * @return the deserialized EObject, or null on error
     */
    private EObject deserializeContainedObject(DeserializationState parentState, JsonParser parser,
            DeserializationContext ctxt) {
        try {
            // Priority 1: Check for runtime value reader from CODEC_FEATURE_VALUE_READERS option
            String runtimeReaderName = ContextHelper.getFeatureValueReader(ctxt, reference);
            if (runtimeReaderName != null && !runtimeReaderName.isEmpty()) {
                // Delegate to runtime-specified value reader
                ReferenceValueReader<?> runtimeReader = resolveRuntimeValueReader(runtimeReaderName, ctxt);
                if (runtimeReader != null && entryContext != null) {
                    // Make type hint available to the reader via context
                    EClass typeHint = ContextHelper.getFeatureTypeHint(ctxt, reference);
                    if (typeHint != null) {
                        ContextHelper.setCurrentFeatureTypeHint(ctxt, typeHint);
                    }
                    try {
                        CodecReaderContext readerCtx = entryContext.createReaderContext(parser, ctxt);
                        return runtimeReader.read(readerCtx, reference);
                    } finally {
                        ContextHelper.clearCurrentFeatureTypeHint(ctxt);
                    }
                } else if (runtimeReader == null) {
                    String msg = "ValueReader '" + runtimeReaderName + "' not found for reference '" +
                            reference.getName() + "', falling back to default";
                    LOGGER.warning(msg);
                    ContextHelper.addWarning(ctxt, msg, parser, "ReferenceDeserializationEntry");
                }
            }

            // Priority 2: Check for runtime type hint from CODEC_FEATURE_TYPE_HINTS option
            EClass runtimeTypeHint = ContextHelper.getFeatureTypeHint(ctxt, reference);

            // Priority 3: Check for custom reference reader from EAnnotation (valueReaderName)
            // (e.g., JSON Schema to EPackage for OpenAPI components/schemas)
            if (referenceReader != null && entryContext != null) {
                // Make type hint available to the reader via context
                if (runtimeTypeHint != null) {
                    ContextHelper.setCurrentFeatureTypeHint(ctxt, runtimeTypeHint);
                }
                try {
                    CodecReaderContext readerCtx = entryContext.createReaderContext(parser, ctxt);
                    return referenceReader.read(readerCtx, reference);
                } finally {
                    ContextHelper.clearCurrentFeatureTypeHint(ctxt);
                }
            }

            // Determine effective type hint: runtime > declared reference type
            EClass effectiveTypeHint = runtimeTypeHint != null ? runtimeTypeHint : reference.getEReferenceType();

            // Check if the effective type hint is abstract or is EObject itself
            // Per spec 18-feature-type-hints.md Section 10.1:
            // "EObject-typed feature without hint and without _type" -> skip feature, value is null
            if (isUninstantiableType(effectiveTypeHint)) {
                String msg = "Cannot deserialize feature '" + reference.getName() + "' of type " +
                        effectiveTypeHint.getName() + ": no type information found and no " +
                        "CODEC_FEATURE_TYPE_HINTS provided. Feature will be skipped.";
                LOGGER.warning(msg);
                ContextHelper.addWarning(ctxt, msg, parser, "ReferenceDeserializationEntry");
                // Skip the JSON content for this feature
                skipJsonValue(parser);
                return null;
            }

            // Check if we have an EMF-aware context from the parser
            TokenStreamContext streamContext = parser.streamReadContext();

            if (streamContext instanceof CodecJsonReadContext codecContext) {
                // Create a child context for this nested object
                // The child context inherits the metadata service but gets its own type hint
                CodecJsonReadContext childContext = codecContext.createChildObjectContext(
                        parser.currentLocation().getLineNr(),
                        parser.currentLocation().getColumnNr());

                // Set the effective type hint for the nested deserialization
                // Priority: runtime type hint > declared reference type
                childContext.setCurrentTypeHint(effectiveTypeHint);

                // Set the current feature being deserialized
                childContext.setCurrentFeature(reference);

                // Note: The parser's _streamReadContext is managed internally by Jackson
                // We're setting up the context but the actual context switching happens
                // when the parser processes tokens. For now we also use ContextHelper
                // for the actual hint passing until we fully integrate context management.

                // Also set via ContextHelper for backwards compatibility
                EClass previousExpectedType = ContextHelper.getExpectedType(ctxt);
                ContextHelper.setExpectedType(ctxt, effectiveTypeHint);

                try {
                    tools.jackson.databind.ValueDeserializer<Object> deser = ctxt.findRootValueDeserializer(
                            ctxt.constructType(EObject.class));
                    if (deser != null) {
                        return (EObject) deser.deserialize(parser, ctxt);
                    }
                    String msg = "No deserializer found for EObject";
                    LOGGER.severe(msg);
                    ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
                    return null;
                } finally {
                    // Restore the previous hint
                    if (previousExpectedType != null) {
                        ContextHelper.setExpectedType(ctxt, previousExpectedType);
                    } else {
                        ContextHelper.clearExpectedType(ctxt);
                    }
                }
            } else if (streamContext instanceof EMFCodecReadContext emfContext) {
                // Generic EMF context (non-JSON format) - set hint directly
                EClass previousHint = emfContext.getCurrentTypeHint();
                emfContext.setCurrentTypeHint(effectiveTypeHint);

                try {
                    tools.jackson.databind.ValueDeserializer<Object> deser = ctxt.findRootValueDeserializer(
                            ctxt.constructType(EObject.class));
                    if (deser != null) {
                        return (EObject) deser.deserialize(parser, ctxt);
                    }
                    String msg = "No deserializer found for EObject";
                    LOGGER.severe(msg);
                    ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
                    return null;
                } finally {
                    emfContext.setCurrentTypeHint(previousHint);
                }
            } else {
                // Fall back to ContextHelper for non-EMF-aware parsers
                return deserializeWithContextHelper(parser, ctxt, effectiveTypeHint);
            }
        } catch (IllegalStateException e) {
            // Propagate IllegalStateException (e.g., from discriminator ERROR strategy)
            // so callers can handle it appropriately. See TypeDiscriminatorRegistry.resolve().
            throw e;
        } catch (Exception e) {
            String msg = "Error deserializing contained object for " + reference.getName() + ": " + e.getMessage();
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
            return null;
        }
    }

    /**
     * Deserializes a non-containment reference object (single-valued).
     * <p>
     * Determines whether this is a proxy reference (has {@code _ref}) or an orphan object
     * (expanded reference without {@code _ref}) by buffering and inspecting the content.
     * </p>
     *
     * @param state the deserialization state
     * @param parser the JSON parser at START_OBJECT
     * @param ctxt the deserialization context
     * @param eObject the parent EObject
     * @param index the index for multi-valued references (-1 for single-valued)
     */
    private void deserializeNonContainmentObject(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EObject eObject, int index) {
        // If no DeserializationContext, use simple approach (for backwards compatibility with tests)
        if (ctxt == null) {
            String refUri = readRefUri(parser);
            if (refUri != null) {
                state.addUnresolvedReference(new UnresolvedReference(eObject, reference, refUri, index));
            }
            return;
        }

        TokenBuffer buffer = null;
        JsonParser bufferParser = null;
        JsonParser replayParser = null;
        try {
            // Buffer the object to inspect for _ref and check for additional fields
            buffer = ctxt.bufferForInputBuffering(parser);
            buffer.copyCurrentStructure(parser);

            // Parse the buffered content to check for _ref and count other fields
            bufferParser = buffer.asParser(ctxt, parser);
            bufferParser.nextToken(); // START_OBJECT

            String refUri = null;
            EClass typeFromContent = null;
            boolean hasOtherFields = false;

            while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = bufferParser.currentName();
                bufferParser.nextToken(); // Move to value

                if (refKey.equals(fieldName)) {
                    refUri = readReferenceValue(bufferParser, ctxt);
                } else if ("_type".equals(fieldName)) {
                    // Try to resolve the type from _type field
                    String typeValue = bufferParser.getString();
                    typeFromContent = resolveTypeFromValue(typeValue, ctxt);
                } else if (!"_id".equals(fieldName)) {
                    // Has fields other than _ref, _type, _id -> potential projection
                    hasOtherFields = true;
                    bufferParser.skipChildren();
                } else {
                    bufferParser.skipChildren();
                }
            }
            bufferParser.close();
            bufferParser = null; // Mark as closed

            if (refUri != null && hasOtherFields) {
                // Has _ref AND other fields: proxy with projection
                // Deserialize the full object and then set proxy URI
                replayParser = buffer.asParser(ctxt, parser);
                replayParser.nextToken(); // Move to START_OBJECT
                EObject proxyWithProjection = deserializeFullObject(state, replayParser, ctxt);
                replayParser.close();
                replayParser = null; // Mark as closed

                if (proxyWithProjection != null) {
                    // Set the proxy URI on the deserialized object
                    URI uri = URI.createURI(refUri);
                    ((InternalEObject) proxyWithProjection).eSetProxyURI(uri);

                    if (reference.isChangeable()) {
                        eObject.eSet(reference, proxyWithProjection);
                    }
                }
            } else if (refUri != null) {
                // Has _ref only: create simple proxy reference (resolve later)
                state.addUnresolvedReference(new UnresolvedReference(eObject, reference, refUri, index, typeFromContent));
            } else {
                // No _ref: deserialize as orphan object (expanded reference)
                replayParser = buffer.asParser(ctxt, parser);
                replayParser.nextToken(); // Move to START_OBJECT
                EObject orphan = deserializeOrphanObject(state, replayParser, ctxt);
                replayParser.close();
                replayParser = null; // Mark as closed

                if (orphan != null && reference.isChangeable()) {
                    eObject.eSet(reference, orphan);
                }
            }
        } catch (Exception e) {
            String msg = "Error deserializing non-containment reference '" + reference.getName() + "': " + e.getMessage();
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
        } finally {
            // Ensure all resources are closed even on exception
            closeQuietly(replayParser);
            closeQuietly(bufferParser);
            closeQuietly(buffer);
        }
    }

    /**
     * Deserializes a non-containment reference element for multi-valued references.
     * <p>
     * Similar to {@link #deserializeNonContainmentObject} but adds to a list instead of setting directly.
     * Supports:
     * <ul>
     *   <li>Simple proxy (_ref only)</li>
     *   <li>Proxy with projection (_ref + additional fields)</li>
     *   <li>Orphan object (no _ref, expanded reference)</li>
     * </ul>
     * </p>
     */
    private void deserializeNonContainmentElement(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EObject eObject, List<EObject> values, int index) {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_STRING) {
            // Direct URI string (PLAIN format)
            String refUri = readReferenceValue(parser, ctxt);
            state.addUnresolvedReference(new UnresolvedReference(eObject, reference, refUri, index));
            return;
        }

        if (token != JsonToken.START_OBJECT) {
            String msg = "Expected START_OBJECT or VALUE_STRING for non-containment ref element '" + reference.getName() + "', got: " + token;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "ReferenceDeserializationEntry");
            return;
        }

        // If no DeserializationContext, use simple approach (for backwards compatibility with tests)
        if (ctxt == null) {
            String refUri = readRefUri(parser);
            if (refUri != null) {
                state.addUnresolvedReference(new UnresolvedReference(eObject, reference, refUri, index));
            }
            return;
        }

        TokenBuffer buffer = null;
        JsonParser bufferParser = null;
        JsonParser replayParser = null;
        try {
            // Buffer the object to inspect for _ref and check for additional fields
            buffer = ctxt.bufferForInputBuffering(parser);
            buffer.copyCurrentStructure(parser);

            // Parse the buffered content to check for _ref and count other fields
            bufferParser = buffer.asParser(ctxt, parser);
            bufferParser.nextToken(); // START_OBJECT

            String refUri = null;
            EClass typeFromContent = null;
            boolean hasOtherFields = false;

            while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = bufferParser.currentName();
                bufferParser.nextToken(); // Move to value

                if (refKey.equals(fieldName)) {
                    refUri = readReferenceValue(bufferParser, ctxt);
                } else if ("_type".equals(fieldName)) {
                    String typeValue = bufferParser.getString();
                    typeFromContent = resolveTypeFromValue(typeValue, ctxt);
                } else if (!"_id".equals(fieldName)) {
                    // Has fields other than _ref, _type, _id -> potential projection
                    hasOtherFields = true;
                    bufferParser.skipChildren();
                } else {
                    bufferParser.skipChildren();
                }
            }
            bufferParser.close();
            bufferParser = null; // Mark as closed

            if (refUri != null && hasOtherFields) {
                // Has _ref AND other fields: proxy with projection
                replayParser = buffer.asParser(ctxt, parser);
                replayParser.nextToken(); // Move to START_OBJECT
                EObject proxyWithProjection = deserializeFullObject(state, replayParser, ctxt);
                replayParser.close();
                replayParser = null; // Mark as closed

                if (proxyWithProjection != null) {
                    // Set the proxy URI on the deserialized object
                    URI uri = URI.createURI(refUri);
                    ((InternalEObject) proxyWithProjection).eSetProxyURI(uri);
                    values.add(proxyWithProjection);
                }
            } else if (refUri != null) {
                // Has _ref only: create simple proxy reference (resolve later)
                state.addUnresolvedReference(new UnresolvedReference(eObject, reference, refUri, index, typeFromContent));
            } else {
                // No _ref: deserialize as orphan object (expanded reference)
                replayParser = buffer.asParser(ctxt, parser);
                replayParser.nextToken(); // Move to START_OBJECT
                EObject orphan = deserializeOrphanObject(state, replayParser, ctxt);
                replayParser.close();
                replayParser = null; // Mark as closed

                if (orphan != null) {
                    values.add(orphan);
                }
            }
        } catch (Exception e) {
            String msg = "Error deserializing non-containment reference element '" + reference.getName() + "': " + e.getMessage();
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
        } finally {
            // Ensure all resources are closed even on exception
            closeQuietly(replayParser);
            closeQuietly(bufferParser);
            closeQuietly(buffer);
        }
    }

    /**
     * Deserializes an orphan object (expanded non-containment reference).
     * <p>
     * Creates a fully populated EObject that is not contained by its parent.
     * The object has no resource assigned and exists only in memory.
     * </p>
     *
     * @param parentState the parent deserialization state
     * @param parser the JSON parser at START_OBJECT
     * @param ctxt the deserialization context
     * @return the deserialized orphan EObject, or null on error
     */
    private EObject deserializeOrphanObject(DeserializationState parentState, JsonParser parser,
            DeserializationContext ctxt) {
        // Orphan objects are deserialized similarly to contained objects,
        // but they are not added to the parent's containment.
        // They just have the non-containment reference set to them.
        return deserializeFullObject(parentState, parser, ctxt);
    }

    /**
     * Deserializes a full EObject using the standard deserializer.
     */
    private EObject deserializeFullObject(DeserializationState parentState, JsonParser parser,
            DeserializationContext ctxt) {
        try {
            // Set the reference type as hint for the nested deserialization
            EClass previousExpectedType = ContextHelper.getExpectedType(ctxt);
            ContextHelper.setExpectedType(ctxt, reference.getEReferenceType());

            try {
                tools.jackson.databind.ValueDeserializer<Object> deser = ctxt.findRootValueDeserializer(
                        ctxt.constructType(EObject.class));
                if (deser != null) {
                    return (EObject) deser.deserialize(parser, ctxt);
                }
                String msg = "No deserializer found for EObject";
                LOGGER.severe(msg);
                ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
                return null;
            } finally {
                // Restore the previous hint
                if (previousExpectedType != null) {
                    ContextHelper.setExpectedType(ctxt, previousExpectedType);
                } else {
                    ContextHelper.clearExpectedType(ctxt);
                }
            }
        } catch (Exception e) {
            String msg = "Error deserializing object for " + reference.getName() + ": " + e.getMessage();
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
            return null;
        }
    }

    /**
     * Resolves an EClass from a type value string.
     *
     * @param typeValue the type value (URI or discriminator)
     * @param ctxt the deserialization context
     * @return the resolved EClass, or null if not found
     */
    private EClass resolveTypeFromValue(String typeValue, DeserializationContext ctxt) {
        if (TypeResolutionHelper.isUri(typeValue)) {
            return TypeResolutionHelper.resolveFromUri(typeValue);
        }
        return null;
    }

    /**
     * Fallback deserialization using ContextHelper for non-EMF-aware parsers.
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context
     * @param typeHint the effective type hint to use
     */
    private EObject deserializeWithContextHelper(JsonParser parser, DeserializationContext ctxt, EClass typeHint) {
        EClass previousExpectedType = ContextHelper.getExpectedType(ctxt);
        ContextHelper.setExpectedType(ctxt, typeHint);

        try {
            tools.jackson.databind.ValueDeserializer<Object> deser = ctxt.findRootValueDeserializer(
                    ctxt.constructType(EObject.class));
            if (deser != null) {
                return (EObject) deser.deserialize(parser, ctxt);
            }
            String msg = "No deserializer found for EObject";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
            return null;
        } finally {
            if (previousExpectedType != null) {
                ContextHelper.setExpectedType(ctxt, previousExpectedType);
            } else {
                ContextHelper.clearExpectedType(ctxt);
            }
        }
    }

    /**
     * Resolves a ReferenceValueReader from the runtime value registry by name.
     * <p>
     * This method looks up the reader from the CodecValueRegistry that was
     * passed via the DeserializationContext.
     * </p>
     *
     * @param readerName the name of the value reader
     * @param ctxt the deserialization context
     * @return the resolved ReferenceValueReader, or null if not found
     */
    private ReferenceValueReader<?> resolveRuntimeValueReader(String readerName, DeserializationContext ctxt) {
        // The value registry should be available via the CodecModule configuration
        // For now we rely on the registry that was passed to the constructor
        // TODO: In future, we could look this up from a context attribute if needed
        // For runtime readers, we'd need the registry to be accessible at deserialization time
        LOGGER.fine(() -> "Looking up runtime value reader: " + readerName);

        // Note: Runtime value readers require the CodecValueRegistry to be accessible
        // This is a limitation - currently we only support readers configured via constructor
        // A future enhancement would be to pass the registry via context
        return null; // Placeholder - requires registry access enhancement
    }

    /**
     * Reads a reference URI from a $ref object.
     * <p>
     * Expected format: {@code {"$ref": "uri"}}
     * </p>
     *
     * @param parser the JSON parser at START_OBJECT
     * @return the reference URI, or null if not found
     */
    private String readRefUri(JsonParser parser) {
        return readRefUri(parser, null);
    }

    /**
     * Reads a reference URI from a $ref object with optional custom reader support.
     * <p>
     * Expected format: {@code {"$ref": "uri"}}
     * </p>
     * <p>
     * If a custom value reader is configured, it reads the reference value
     * (the _ref field content) instead of the default parser.getString() logic.
     * </p>
     *
     * @param parser the JSON parser at START_OBJECT
     * @param ctxt the deserialization context (may be null)
     * @return the reference URI, or null if not found
     * @see <a href="docs/codec-v2-spec/10-custom-values.md#5-reference-value-readerswriters">Spec: Reference Value Readers</a>
     */
    private String readRefUri(JsonParser parser, DeserializationContext ctxt) {
        String uri = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            parser.nextToken(); // Move to value

            if (refKey.equals(fieldName)) {
                uri = readReferenceValue(parser, ctxt);
            }
            // Skip other fields
        }

        return uri;
    }

    /**
     * Reads the reference value (the _ref field content).
     * <p>
     * If a URI reader is configured, it is used to transform the value.
     * Otherwise, the default parser.getString() is used.
     * </p>
     *
     * @param parser the JSON parser positioned at the value token
     * @param ctxt the deserialization context (may be null)
     * @return the reference value string
     */
    private String readReferenceValue(JsonParser parser, DeserializationContext ctxt) {
        // Use URI reader for non-containment reference transformation if configured
        if (uriReader != null && entryContext != null) {
            try {
                CodecReaderContext readerCtx = entryContext.createReaderContext(parser, ctxt);
                return uriReader.read(readerCtx, reference);
            } catch (IOException e) {
                throw new UncheckedIOException(
                        "Custom URI reader failed for reference: " + reference.getName(), e);
            }
        }

        // Default: read string directly
        return parser.getString();
    }

    /**
     * Returns the reference being deserialized.
     *
     * @return the EReference
     */
    public EReference getReference() {
        return reference;
    }

    /**
     * Returns the key used for non-containment references.
     *
     * @return the ref key (e.g., "$ref")
     */
    public String getRefKey() {
        return refKey;
    }

    /**
     * Checks if the given EClass is the base EObject type from Ecore.
     * <p>
     * This method specifically checks for the base EObject class from the Ecore
     * package. Abstract domain classes (like "Geometry") are NOT considered here
     * because they typically work with _type polymorphism in JSON.
     * </p>
     * <p>
     * Per spec 18-feature-type-hints.md Section 10.1:
     * "EObject-typed feature without hint and without _type" should be skipped.
     * However, abstract domain classes with _type in JSON should still work.
     * </p>
     *
     * @param eClass the EClass to check
     * @return true if it's the base EObject type that cannot be instantiated
     */
    private boolean isUninstantiableType(EClass eClass) {
        if (eClass == null) {
            return true;
        }
        // Only skip for the base EObject from Ecore package
        // Abstract domain classes (like "Geometry") can work with _type in JSON
        // The deserializer will handle those cases
        if ("EObject".equals(eClass.getName()) &&
                "http://www.eclipse.org/emf/2002/Ecore".equals(eClass.getEPackage().getNsURI())) {
            return true;
        }
        return false;
    }

    /**
     * Skips the current JSON value (object, array, or primitive).
     * <p>
     * This is used when a feature cannot be deserialized (e.g., no type hint for EObject-typed feature)
     * but we still need to consume the JSON tokens to continue parsing.
     * </p>
     *
     * @param parser the JSON parser positioned at the start of the value
     */
    private void skipJsonValue(JsonParser parser) {
        try {
            parser.skipChildren();
        } catch (Exception e) {
            LOGGER.warning("Failed to skip JSON value: " + e.getMessage());
        }
    }

    /**
     * Closes an AutoCloseable resource quietly, suppressing any exceptions.
     * <p>
     * Used in finally blocks to ensure resources are closed even if
     * they've already been closed or are null.
     * </p>
     *
     * @param closeable the resource to close (may be null)
     */
    private void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOGGER.fine("Failed to close resource: " + e.getMessage());
            }
        }
    }

    // ========================================================================
    // EMap Support
    // ========================================================================

    /**
     * Deserializes an EMap from a JSON object.
     * <p>
     * JSON format for EMaps:
     * <pre>
     * {
     *   "key1": value1,
     *   "key2": value2,
     *   ...
     * }
     * </pre>
     * Where each JSON field name becomes the map entry key, and the field value
     * becomes the map entry value.
     * </p>
     *
     * @param state the deserialization state
     * @param parser the JSON parser at START_OBJECT
     * @param ctxt the deserialization context
     * @param eObject the parent EObject
     */
    @SuppressWarnings("unchecked")
    private void deserializeEMap(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EObject eObject) {
        EClass entryClass = reference.getEReferenceType();
        org.eclipse.emf.ecore.EStructuralFeature keyFeature = EMapHelper.getKeyFeature(entryClass);
        org.eclipse.emf.ecore.EStructuralFeature valueFeature = EMapHelper.getValueFeature(entryClass);

        if (keyFeature == null || valueFeature == null) {
            String msg = "EMap entry class '" + entryClass.getName() + "' missing key or value feature";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
            return;
        }

        List<EObject> entries = (List<EObject>) eObject.eGet(reference);

        try {
            // Iterate over JSON object fields
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String key = parser.currentName();
                parser.nextToken(); // Move to value

                // Create a new map entry
                EObject entry = entryClass.getEPackage().getEFactoryInstance().create(entryClass);

                // Set the key
                entry.eSet(keyFeature, key);

                // Deserialize the value based on value feature type
                Object value = deserializeMapEntryValue(state, parser, ctxt, valueFeature);
                if (value != null) {
                    entry.eSet(valueFeature, value);
                }

                entries.add(entry);
            }
        } catch (Exception e) {
            String msg = "Error deserializing EMap for '" + reference.getName() + "': " + e.getMessage();
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "ReferenceDeserializationEntry");
        }
    }

    /**
     * Deserializes the value part of a map entry.
     * <p>
     * The value can be:
     * <ul>
     *   <li>A primitive/string (for EAttribute value features)</li>
     *   <li>An object (for EReference value features)</li>
     * </ul>
     * </p>
     *
     * @param state the deserialization state
     * @param parser the JSON parser positioned at the value token
     * @param ctxt the deserialization context
     * @param valueFeature the value feature of the map entry class
     * @return the deserialized value
     */
    private Object deserializeMapEntryValue(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, org.eclipse.emf.ecore.EStructuralFeature valueFeature) {
        try {
            if (valueFeature instanceof org.eclipse.emf.ecore.EAttribute) {
                // Simple value - let Jackson deserialize it
                return deserializeAttributeValue(parser, (org.eclipse.emf.ecore.EAttribute) valueFeature);
            } else if (valueFeature instanceof EReference valueRef) {
                // Reference value - deserialize as EObject
                if (parser.currentToken() == JsonToken.START_OBJECT) {
                    // Set the reference type as hint for the nested deserialization
                    EClass previousExpectedType = ContextHelper.getExpectedType(ctxt);
                    ContextHelper.setExpectedType(ctxt, valueRef.getEReferenceType());

                    try {
                        tools.jackson.databind.ValueDeserializer<Object> deser = ctxt.findRootValueDeserializer(
                                ctxt.constructType(EObject.class));
                        if (deser != null) {
                            return deser.deserialize(parser, ctxt);
                        }
                    } finally {
                        if (previousExpectedType != null) {
                            ContextHelper.setExpectedType(ctxt, previousExpectedType);
                        } else {
                            ContextHelper.clearExpectedType(ctxt);
                        }
                    }
                } else if (parser.currentToken() == JsonToken.VALUE_NULL) {
                    return null;
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Error deserializing map entry value: " + e.getMessage());
        }
        return null;
    }

    /**
     * Deserializes a simple attribute value from JSON.
     *
     * @param parser the JSON parser positioned at the value token
     * @param attribute the EAttribute to deserialize to
     * @return the deserialized value
     */
    private Object deserializeAttributeValue(JsonParser parser, org.eclipse.emf.ecore.EAttribute attribute) {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        EClassifier type = attribute.getEType();
        String instanceClassName = type.getInstanceClassName();

        try {
            if ("java.lang.String".equals(instanceClassName) || "String".equals(type.getName())) {
                return parser.getString();
            } else if ("int".equals(instanceClassName) || "java.lang.Integer".equals(instanceClassName)) {
                return parser.getIntValue();
            } else if ("long".equals(instanceClassName) || "java.lang.Long".equals(instanceClassName)) {
                return parser.getLongValue();
            } else if ("double".equals(instanceClassName) || "java.lang.Double".equals(instanceClassName)) {
                return parser.getDoubleValue();
            } else if ("float".equals(instanceClassName) || "java.lang.Float".equals(instanceClassName)) {
                return parser.getFloatValue();
            } else if ("boolean".equals(instanceClassName) || "java.lang.Boolean".equals(instanceClassName)) {
                return parser.getBooleanValue();
            } else {
                // Default: try to get as string
                return parser.getString();
            }
        } catch (Exception e) {
            LOGGER.warning("Error deserializing attribute value: " + e.getMessage());
            return null;
        }
    }
}
