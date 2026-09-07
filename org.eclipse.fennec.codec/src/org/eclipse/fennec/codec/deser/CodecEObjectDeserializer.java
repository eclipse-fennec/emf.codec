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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.ClassConfig;
import org.eclipse.fennec.codec.config.DiscriminatorConfig;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.prefix.CodecPrefixReader;
import org.eclipse.fennec.codec.context.EMFCodecReadContext;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorReader;
import org.eclipse.fennec.codec.util.ConversionFailures;
import org.eclipse.fennec.codec.util.EMapHelper;
import org.eclipse.fennec.codec.util.TokenLoops;
import org.eclipse.fennec.codec.util.PackageResolver;
import org.eclipse.fennec.codec.util.TypeResolutionHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Main Jackson deserializer for EObjects.
 * <p>
 * Orchestrates the deserialization process using the entry-based pattern:
 * <ol>
 *   <li>Read type information first (if present)</li>
 *   <li>Resolve EClass (from type info or CODEC_ROOT_TYPE option)</li>
 *   <li>Create EObject instance</li>
 *   <li>Build deserialization entries for the EClass</li>
 *   <li>Read remaining properties and deserialize</li>
 *   <li>Handle unresolved references</li>
 * </ol>
 * </p>
 * <p>
 * Uses {@link EffectiveCodecConfig} which wraps
 * {@link org.eclipse.fennec.codec.config.ConfigurationResolver} for on-demand
 * configuration resolution with caching.
 * </p>
 *
 * @see EffectiveCodecConfig
 * @see DeserializationEntry
 * @see <a href="docs/codec-v2-spec/15-deserialization.md">Spec 15: Deserialization</a>
 * @author Mark Hoffmann
 * @since 1.0
 */
public class CodecEObjectDeserializer extends ValueDeserializer<EObject> {

    private static final Logger LOGGER = Logger.getLogger(CodecEObjectDeserializer.class.getName());

    /** Context attribute key for CODEC_ROOT_TYPE option (user-provided root hint) */
    public static final String CODEC_ROOT_TYPE = "CODEC_ROOT_TYPE";

    /** Default reference key for non-containment references */
    private static final String DEFAULT_REF_KEY = "$ref";

    /** Default type key */
    private static final String DEFAULT_TYPE_KEY = "_type";

    /** Default schema key for PLAIN SCHEMA_AND_TYPE format */
    private static final String DEFAULT_SCHEMA_KEY = "_schema";

    /**
     * Maximum nesting depth for recursive value reading (readCurrentValue, readObjectAsMap, readArrayAsList).
     * Protects against StackOverflowError from deeply nested JSON structures.
     * <p>
     * Security: CWE-674 (Uncontrolled Recursion), CWE-400 (Resource Exhaustion).
     * </p>
     */
    static final int MAX_NESTING_DEPTH = 200;

    /**
     * Maximum number of elements allowed in a single collection (array or object)
     * during recursive value reading (readArrayAsList, readObjectAsMap).
     * Protects against OutOfMemoryError from payloads with millions of elements.
     * <p>
     * Security: CWE-400 (Resource Exhaustion), CWE-770 (Allocation Without Limits).
     * </p>
     */
    static final int MAX_COLLECTION_SIZE = 100_000;

    private final EffectiveCodecConfig config;
    private final CodecEntryContext entryContext;

    /**
     * Creates a new CodecEObjectDeserializer.
     *
     * @param config the effective codec configuration
     */
    public CodecEObjectDeserializer(EffectiveCodecConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.entryContext = CodecEntryContext.builder()
                .effectiveConfig(config)
                .diagnostics(config.getDiagnostics())
                .valueRegistry(config.getValueRegistry())
                .prefixRegistry(config.getPrefixRegistry())
                .build();
    }

    /** Prefix-key/feature clashes already reported, as {@code EClass#key}, per deserializer (= per operation). */
    private final Set<String> reportedPrefixClashes = ConcurrentHashMap.newKeySet();

    private static final String PREFIX_SOURCE = "PrefixDeserializationEntry";

    @Override
    public Class<?> handledType() {
        return EObject.class;
    }

    @Override
    public EObject deserialize(JsonParser parser, DeserializationContext ctxt) {
        JsonToken token = parser.currentToken();

        // Handle null
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        // Must be at START_OBJECT
        if (token != JsonToken.START_OBJECT) {
            String msg = "Expected START_OBJECT, got: " + token;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
            return null;
        }

        // Every EObject - root or contained - is read through this method, so this is the one
        // place that can tell the two apart: a contained object is read while its container's
        // call is still on the stack (issue #173).
        ContextHelper.enterEObject(ctxt);
        try {
            return deserializeObject(parser, ctxt);
        } finally {
            ContextHelper.exitEObject(ctxt);
        }
    }

    /**
     * Reads one EObject, with its nesting depth recorded on the context.
     */
    private EObject deserializeObject(JsonParser parser, DeserializationContext ctxt) {
        // Try to get EMF context from parser's stream context (preferred)
        // This provides access to resource, type hints, and metadata service
        EMFCodecReadContext emfContext = null;
        if (parser.streamReadContext() instanceof EMFCodecReadContext ctx) {
            emfContext = ctx;
        }

        // Create deserialization state with resource from context
        // Primary: EMFCodecReadContext (for CodecJsonParser path)
        // Fallback: ContextHelper.RESOURCE attribute (for FormatDelegateParser path)
        Resource resource = emfContext != null ? emfContext.getResource() : null;
        if (resource == null) {
            resource = ContextHelper.getResource(ctxt);
        }
        DeserializationState state = new DeserializationState(resource);

        // The type plane resolves through the PackageResolver, which carries the
        // MetadataService this codec was configured with. CodecResource seeds one per load; a
        // caller driving the mapper itself has no way to, and the resolver-less fallback path
        // reads the global EPackage.Registry instead - so a package published only to the
        // MetadataService would not be found (issue #163). Seed it from the configuration.
        ensurePackageResolver(ctxt, resource);

        // Get or create shared unresolved references list from context
        @SuppressWarnings("unchecked")
        List<DeserializationState.UnresolvedReference> unresolvedRefs =
                (List<DeserializationState.UnresolvedReference>) ctxt.getAttribute(ContextHelper.UNRESOLVED_REFERENCES);
        if (unresolvedRefs != null) {
            // Use shared list for collecting unresolved references
            state.setSharedUnresolvedReferences(unresolvedRefs);
        }

        // Get the expected type hint (must be EClass if set)
        // Priority:
        // 1. Parser's stream context (CodecJsonReadContext) - for nested objects
        // 2. Jackson's DeserializationContext.getAttribute() - for backwards compatibility
        EClass hintEClass = null;
        if (emfContext != null) {
            hintEClass = emfContext.getCurrentTypeHint();
        }
        if (hintEClass == null) {
            // Fall back to ContextHelper for backwards compatibility
            hintEClass = ContextHelper.getExpectedType(ctxt);
        }

        // Check if we need to use featurePath-based type resolution.
        // FeaturePathTypeResolver is only needed when the discriminator path is a
        // nested/non-standard path (e.g., "info.profileName") that differs from the
        // type key. When discriminatorPath matches the type key (e.g., both "_type"),
        // the standard flow handles it — TypeDeserializationEntry.resolveEClass()
        // performs targeted discriminator lookup via resolve(mapId, ...).
        // See spec 08-discriminator-mapping.md §7.1.1 vs §7.1.2.
        String discriminatorPath = getDiscriminatorPath(hintEClass);

        // If no hint provided, try to find ANY discriminatorPath from registered types
        if (!FeaturePathTypeResolver.hasDiscriminatorPath(discriminatorPath)
                && hintEClass == null
                && config.getTypeDiscriminatorReader() != null) {
            discriminatorPath = config.getTypeDiscriminatorReader().getAnyDiscriminatorPath();
        }

        if (FeaturePathTypeResolver.hasDiscriminatorPath(discriminatorPath)
                && config.getTypeDiscriminatorReader() != null
                && !isTypeKey(discriminatorPath, hintEClass)) {
            return deserializeWithFeaturePath(parser, ctxt, state, hintEClass, discriminatorPath);
        }

        // Standard deserialization flow
        Map<String, Object> deferredProperties = new HashMap<>();
        EClass resolvedEClass = null;
        EObject eObject = null;
        boolean typeFieldProcessed = false;
        String schemaValue = null;  // For PLAIN SCHEMA_AND_TYPE format

        // Type context held back until it is complete (issue #73, B.1): in PLAIN format the
        // fingerprint is a sibling of the type key, so the type alone is not yet enough to
        // pick a version. Both are collected first, then the resolver runs. STRUCTURED needs
        // no holding back - there the fingerprint is inside the type object.
        boolean typeContextSeen = false;
        String pendingTypeValue = null;
        // Kept beyond resolution, unlike pendingTypeValue: it is what tells "the document said
        // nothing about its type" apart from "the document said something unresolvable".
        String seenTypeValue = null;
        String pendingTypeProperty = null;
        String streamFingerprint = null;

        // Read properties
        while (TokenLoops.hasNextField(parser)) {
            String propertyName = parser.currentName();
            parser.nextToken(); // Move to value

            // Check if this is the schema property (for PLAIN SCHEMA_AND_TYPE)
            if (isSchemaKey(propertyName)) {
                schemaValue = parser.getString();
                continue;
            }

            // In-band EPackage fingerprint (B.1): read liberally, wherever it appears.
            if (ContextHelper.isFingerprintKey(ctxt, propertyName)) {
                streamFingerprint = parser.getString();
                continue;
            }

            // Check if this is the type property - ALWAYS process it when present
            if (isTypeKey(propertyName, hintEClass)) {
                // Read the raw type value; resolution waits until the type context is complete
                TypeContext typeContext = readTypeContext(parser, ctxt);
                pendingTypeValue = typeContext.typeValue;
                seenTypeValue = typeContext.typeValue;
                pendingTypeProperty = propertyName;
                typeContextSeen = true;
                if (typeContext.fingerprint != null) {
                    // STRUCTURED carries the fingerprint as an inner key of the type object
                    streamFingerprint = typeContext.fingerprint;
                }
                continue;
            }

            // A data property forces the pending type context to be resolved now: everything
            // that could still refine the version has been seen.
            if (typeContextSeen) {
                resolvedEClass = resolveTypeFromValue(pendingTypeValue, state, hintEClass,
                        schemaValue, ctxt, emfContext, streamFingerprint);
                state.setResolvedEClass(resolvedEClass);
                typeFieldProcessed = true;
                if (resolvedEClass != null) {
                    eObject = state.createEObject();
                    processDeferredProperties(state, deferredProperties, ctxt);
                    setTypeAsAttributeIfExists(eObject, pendingTypeProperty, pendingTypeValue, ctxt);
                }
                typeContextSeen = false;
                pendingTypeValue = null;
            }

            // If we don't have the type yet, defer this property
            if (resolvedEClass == null) {
                deferredProperties.put(propertyName, readCurrentValue(parser, ctxt));
                continue;
            }

            // Create object if not yet created
            if (eObject == null) {
                eObject = state.createEObject();
                processDeferredProperties(state, deferredProperties, ctxt);
            }

            // Deserialize the property
            deserializeProperty(state, propertyName, parser, ctxt);
        }

        // Object with nothing but a type context: resolve it at the end.
        if (typeContextSeen) {
            resolvedEClass = resolveTypeFromValue(pendingTypeValue, state, hintEClass,
                    schemaValue, ctxt, emfContext, streamFingerprint);
            state.setResolvedEClass(resolvedEClass);
            typeFieldProcessed = true;
            if (resolvedEClass != null) {
                eObject = state.createEObject();
                processDeferredProperties(state, deferredProperties, ctxt);
                setTypeAsAttributeIfExists(eObject, pendingTypeProperty, pendingTypeValue, ctxt);
            }
        }

        // If no _type field was found, fall back to hint
        if (!typeFieldProcessed && hintEClass != null) {
            resolvedEClass = hintEClass;
            state.setResolvedEClass(resolvedEClass);
        }

        // Handle case where type was never found
        if (eObject == null && resolvedEClass != null) {
            eObject = state.createEObject();
            processDeferredProperties(state, deferredProperties, ctxt);
        }

        if (eObject == null) {
            // Two different failures used to share one message, and the shared wording described
            // only the first: a document that carried a type value which could not be resolved
            // was reported as carrying none. That sends a reader looking for a missing
            // discriminator instead of an unresolvable one - spec 06 §6.3.2 keeps them apart.
            // A third case joins them: under typeStrategy=NONE nothing was unresolvable and
            // nothing was missing from the document - the configuration says the type is not
            // transported, so the root hint is the only source there ever was (issue #171).
            String msg;
            if (isTypeStrategyNone()) {
                msg = "Cannot deserialize: typeStrategy=NONE transports no type information,"
                        + " so a CODEC_ROOT_TYPE hint is required for the root object";
            } else if (seenTypeValue != null) {
                msg = String.format(
                        "Cannot deserialize: type value '%s' could not be resolved and no"
                                + " CODEC_ROOT_TYPE hint was given",
                        seenTypeValue);
            } else {
                msg = "Cannot deserialize: no type information found and no CODEC_ROOT_TYPE hint";
            }
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
        }

        // Check strictOnMissing for required features
        if (eObject != null) {
            checkStrictOnMissing(eObject, state, ctxt, parser);
        }

        return eObject;
    }

    /**
     * Applies {@code codec.typeHintMode=OVERRIDE} to the root object (issue #173).
     * <p>
     * {@code CODEC_ROOT_TYPE} is a hint by default, and deliberately so: a subtype in the data
     * must be able to beat it, or a collection read through its supertype could never
     * deserialize its own members (spec 13 §2.11). {@code OVERRIDE} is the caller stating the
     * opposite for their load - re-reading data against a type they have chosen, schema
     * migration being the case the spec names.
     * </p>
     * <p>
     * It applies to the root object only, which is why it asks the EObject nesting depth rather
     * than the stream context: a contained object's current feature is reset when its context is
     * reused for an array element, so that signal would silently let the override through.
     * Extending it to contained objects would flatten a container's polymorphic contents to the
     * container's own type, which is never what naming a <em>root</em> type asks for.
     * </p>
     *
     * @return the type to use instead of the document's, or {@code null} to resolve normally
     */
    private EClass applyRootTypeOverride(String typeValue, DeserializationContext ctxt) {
        if (!ContextHelper.isTypeHintOverride(ctxt) || !ContextHelper.isRootEObject(ctxt)) {
            return null;
        }
        // At depth 1 the expected type is still the caller's root option: the per-nesting-level
        // reuse of that attribute has not happened yet.
        EClass rootType = ContextHelper.getExpectedType(ctxt);
        if (rootType == null) {
            // An option that cannot do anything says so. Behaving like HINT in silence would
            // leave the caller believing a directive is in force when none is.
            String msg = "codec.typeHintMode=OVERRIDE has no effect without a CODEC_ROOT_TYPE"
                    + " hint to override with; the document's own type is used";
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, null, "CodecEObjectDeserializer");
            return null;
        }
        if (!namesTheSameType(typeValue, rootType)) {
            // Overruling the document is legitimate but never silent (spec 13 §2.11).
            String msg = String.format(
                    "codec.typeHintMode=OVERRIDE: reading the root as '%s' as configured,"
                            + " the type '%s' stated by the document is discarded",
                    rootType.getName(), typeValue);
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, null, "CodecEObjectDeserializer");
        }
        return rootType;
    }

    /**
     * Tells whether a raw type value names the given EClass, comparing simple names.
     * <p>
     * Comparing names rather than resolving keeps this usable for every strategy that
     * transports one - a full URI, a bare name, a schema-scoped name - and it only has to
     * decide whether the override actually discarded anything, not what the value means.
     * </p>
     */
    private static boolean namesTheSameType(String typeValue, EClass eClass) {
        if (typeValue == null) {
            return true;
        }
        int separator = typeValue.lastIndexOf("#//");
        String simpleName = separator < 0 ? typeValue : typeValue.substring(separator + 3);
        return simpleName.equals(eClass.getName());
    }

    /**
     * Tells whether the configuration declares that no type information is transported.
     * <p>
     * {@code typeStrategy=NONE} is a statement about the document (spec 06-type.md §1.4), so
     * the read side must not treat a value sitting under the type key as a discriminator and
     * must not report its own refusal to resolve one as a failure (issue #171).
     * </p>
     */
    private boolean isTypeStrategyNone() {
        TypeConfig globalTypeConfig = config.resolveGlobalTypeConfig();
        return globalTypeConfig != null && globalTypeConfig.getStrategy() == TypeStrategy.NONE;
    }

    /**
     * Makes sure the context carries a {@link PackageResolver} built from the configured
     * {@link MetadataService}, so type resolution consults the service the caller supplied
     * rather than the global registry (issue #163).
     * <p>
     * Does nothing when a resolver is already there — {@code CodecResource} seeds one for the
     * whole load and its version pins must not be reset mid-document — and nothing when no
     * MetadataService is configured, which is the only case where the global registry stays
     * the last resort.
     * </p>
     *
     * @param ctxt the deserialization context
     * @param resource the resource being read, may be {@code null}
     */
    private void ensurePackageResolver(DeserializationContext ctxt, Resource resource) {
        if (ctxt == null || ContextHelper.getPackageResolver(ctxt) != null) {
            return;
        }
        MetadataService metadataService = config.getMetadataService();
        if (metadataService == null) {
            return;
        }
        EPackage.Registry registry = resource != null && resource.getResourceSet() != null
                ? resource.getResourceSet().getPackageRegistry()
                : null;
        ContextHelper.setPackageResolverIfAbsent(ctxt, new PackageResolver(metadataService, registry));
    }

    /**
     * Checks if the property name is a type key.
     * <p>
     * Priority order (most specific first):
     * <ol>
     *   <li>Class-level typeKey from hint EClass annotation</li>
     *   <li>Global typeKey from configuration</li>
     *   <li>Hardcoded defaults ({@code _type}, {@code _class}, {@code @type}, {@code eClass})</li>
     * </ol>
     */
    private boolean isTypeKey(String propertyName, EClass hintEClass) {
        // 0. Any key configured anywhere. The write side may have used a class- or
        // reference-scoped key, and which one applies is only known once the type is
        // resolved - which in turn needs the key. This has to be checked before the
        // hint's own config, because that one merges in the global key and would
        // answer "false" for a scoped key (issue #116).
        if (config.collectConfiguredTypeKeys().contains(propertyName)) {
            return true;
        }
        // 1. Check class-level type key from hint (most specific)
        if (hintEClass != null) {
            TypeConfig classTypeConfig = config.resolveTypeConfig(hintEClass);
            if (classTypeConfig != null) {
                String classTypeKey = classTypeConfig.getTypeKey();
                if (classTypeKey != null) {
                    return classTypeKey.equals(propertyName);
                }
            }
        }
        // 2. Check global type key from configuration
        TypeConfig globalTypeConfig = config.resolveGlobalTypeConfig();
        String configuredTypeKey = globalTypeConfig != null ? globalTypeConfig.getTypeKey() : null;
        if (configuredTypeKey != null) {
            return configuredTypeKey.equals(propertyName);
        }
        // 3. Fall back to hardcoded defaults
        return DEFAULT_TYPE_KEY.equals(propertyName)
            || "_class".equals(propertyName)
            || "@type".equals(propertyName)
            || "eClass".equals(propertyName);
    }

    /**
     * Tells whether a property is one the codec knowingly does not read back.
     * <p>
     * Three kinds: keys the writer emits for consumers rather than for reading (the supertype
     * key, the id separator), and features the configuration excludes from reading via
     * {@code ignoreRead}. All three are legitimate - what is not legitimate is reporting them
     * as unknown, because then a clean round trip of the codec's own output still produces
     * warnings and no one can use the diagnostics to spot a real problem (issue #131).
     * </p>
     */
    /**
     * Finds a feature that carries this key but cannot be set, so the value has to be dropped
     * (issue #131).
     * <p>
     * EMF refuses {@code eSet} on a feature that is not changeable, which a caller may well
     * have asked to read via {@code forceRead}. Reporting it as an <i>unknown</i> feature is
     * wrong on both counts - the feature exists, and the reason it was skipped is not that
     * nobody knows it - and under {@code strictOnUnknown} it would fail the load for a field
     * the model itself declares.
     * </p>
     *
     * @param propertyName the key found in the document
     * @param eClass the class being read
     * @return the feature that key belongs to, or null if none matches
     */
    private EStructuralFeature unsettableFeature(String propertyName, EClass eClass) {
        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            if (feature.isChangeable()) {
                continue;
            }
            FeatureConfig featureConfig = config.resolveFeatureConfig(feature);
            String key = featureConfig != null && featureConfig.getKey() != null
                    ? featureConfig.getKey()
                    : feature.getName();
            if (propertyName.equals(key)) {
                return feature;
            }
        }
        return null;
    }

    /** The diagnostic for a value that names a real feature EMF cannot set. */
    private String unsettableMessage(String propertyName, EClass eClass) {
        return "Feature '" + propertyName + "' of EClass " + eClass.getName()
                + " is not changeable, its value was dropped";
    }

    private boolean isDeliberatelyNotRead(String propertyName, EClass eClass) {
        SuperTypeConfig superTypeConfig = config.resolveSuperTypeConfig(eClass);
        if (superTypeConfig != null && superTypeConfig.isSerialize()
                && propertyName.equals(superTypeConfig.getEffectiveSuperTypeKey(
                        superTypeConfig.getFormat()))) {
            return true;
        }

        IdConfig idConfig = config.resolveIdConfig(eClass);
        if (idConfig != null) {
            // Match on the name alone: a model feature of that name would have an entry and
            // never reach this point, so anything left here is the codec's own separator
            String separatorKey = idConfig.getSeparatorKey();
            // PLAIN prefixes the key with an underscore, STRUCTURED writes it as configured
            boolean prefixed = separatorKey.startsWith("_") || separatorKey.startsWith("@");
            if (propertyName.equals(separatorKey)
                    || (!prefixed && propertyName.equals("_" + separatorKey))) {
                return true;
            }
        }

        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            FeatureConfig featureConfig = config.resolveFeatureConfig(feature);
            if (featureConfig == null) {
                continue;
            }
            if (!featureConfig.shouldDeserialize() && propertyName.equals(featureConfig.getKey())) {
                return true;
            }
            // A flattened EMap writes its entries directly into this object, so its keys are
            // arbitrary and cannot be recognised individually. Since flatten is a write-only
            // export feature (issue #121), any unknown key here is expected rather than wrong
            if (featureConfig.isFlatten() && feature instanceof EReference mapRef
                    && EMapHelper.isMapEntryReference(mapRef)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the property name is a schema key (for PLAIN SCHEMA_AND_TYPE format).
     */
    private boolean isSchemaKey(String propertyName) {
        return DEFAULT_SCHEMA_KEY.equals(propertyName)
            || "@vocab".equals(propertyName);
    }

    /**
     * The type context read from a document: the type value plus, if the document carries
     * one, the in-band EPackage fingerprint that pins the version (issue #73, B.1).
     * <p>
     * Held together because neither alone identifies an {@code EClass} instance when several
     * versions share an {@code nsURI} — the resolver needs both.
     * </p>
     */
    private static final class TypeContext {
        private final String typeValue;
        private final String fingerprint;

        private TypeContext(String typeValue, String fingerprint) {
            this.typeValue = typeValue;
            this.fingerprint = fingerprint;
        }

        private static TypeContext of(String typeValue) {
            return new TypeContext(typeValue, null);
        }
    }

    /**
     * Reads the type context from the current parser position.
     * <p>
     * In PLAIN format only the type value lives here — its fingerprint sibling is read by the
     * caller's property loop. In STRUCTURED format the fingerprint is an inner key of the
     * type object and comes back alongside the type value.
     * </p>
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context (for adding diagnostics)
     * @return the type context; its type value is null if the token was not a string,
     *         number or structured object
     */
    private TypeContext readTypeContext(JsonParser parser, DeserializationContext ctxt) {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_STRING) {
            return TypeContext.of(parser.getString());
        }
        if (token == JsonToken.VALUE_NUMBER_INT) {
            return TypeContext.of(String.valueOf(parser.getIntValue()));
        }
        if (token == JsonToken.START_OBJECT) {
            // STRUCTURED format: parse inner object to extract type value.
            // Expected shapes:
            //   URI/NAME/CLASS: {"type": "<value>"}
            //   SCHEMA_AND_TYPE: {"schema": "<nsURI>", "type": "<name>"}
            //   NUMERIC: {"schema": "<nsURI>", "classifier": <id>}
            //   any of the above may additionally carry {"fingerprint": "<fp>"}
            return readStructuredTypeObject(parser, ctxt);
        }
        // Unexpected token (e.g., boolean, null, etc.) - report based on mode
        String msg = "Unexpected token for _type field: " + token + ". Expected STRING or OBJECT.";
        if (ContextHelper.isStrictMode(ctxt)) {
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
        } else {
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
        }
        return TypeContext.of(null);
    }

    /**
     * Reads a STRUCTURED format type object and extracts the type value.
     * <p>
     * Recognizes inner keys from TypeConfig defaults:
     * <ul>
     *   <li>{@code "type"} (nameKey) — the type value (URI, name, class name)</li>
     *   <li>{@code "schema"} (schemaKey) — the schema URI</li>
     *   <li>{@code "classifier"} — numeric classifier ID</li>
     * </ul>
     * When both schema and type/classifier are present, composes a full URI.
     * Consumes the entire nested object so the parser is correctly positioned.
     */
    private TypeContext readStructuredTypeObject(JsonParser parser, DeserializationContext ctxt) {
        TypeConfig globalTypeConfig = config.resolveGlobalTypeConfig();
        String nameKey = globalTypeConfig != null ? globalTypeConfig.getNameKey() : "type";
        String schemaKey = globalTypeConfig != null ? globalTypeConfig.getSchemaKey() : "schema";

        String typeValue = null;
        String schemaValue = null;
        String classifierValue = null;
        String fingerprint = null;

        while (TokenLoops.hasNextField(parser)) {
            String fieldName = parser.currentName();
            parser.nextToken(); // move to value

            if (nameKey.equals(fieldName)) {
                typeValue = parser.getString();
            } else if (schemaKey.equals(fieldName)) {
                schemaValue = parser.getString();
            } else if (ContextHelper.isFingerprintKey(ctxt, fieldName)) {
                // In-band EPackage fingerprint (B.1): read liberally, wherever it appears
                fingerprint = parser.getString();
            } else if ("classifier".equals(fieldName)) {
                classifierValue = parser.currentToken() == JsonToken.VALUE_NUMBER_INT
                        ? String.valueOf(parser.getIntValue())
                        : parser.getString();
            }
            // ignore unknown inner keys
        }

        // Compose full URI when schema is present
        if (schemaValue != null && !schemaValue.isEmpty()) {
            if (classifierValue != null) {
                // NUMERIC STRUCTURED: resolve classifier ID within the embedded schema package.
                // Look up the EClass directly and return its full URI for downstream resolution.
                EClass resolved = TypeResolutionHelper.resolveFromNumeric(classifierValue, null, schemaValue,
                        ContextHelper.getPackageResolver(ctxt),
                        ContextHelper.getDiagnosticCollector(ctxt));
                if (resolved != null && resolved.getEPackage() != null) {
                    return new TypeContext(
                            resolved.getEPackage().getNsURI() + "#//" + resolved.getName(), fingerprint);
                }
                return new TypeContext(classifierValue, fingerprint);
            }
            if (typeValue != null && !typeValue.contains("#//")) {
                // SCHEMA_AND_TYPE or NAME with schema: compose full URI
                return new TypeContext(schemaValue + "#//" + typeValue, fingerprint);
            }
        }

        return new TypeContext(typeValue, fingerprint);
    }

    /**
     * Resolves an EClass from a pre-read type value string.
     *
     * @param typeValue the raw type value from JSON
     * @param state the deserialization state
     * @param hintEClass optional hint EClass
     * @param schemaValue optional schema value for SCHEMA_AND_TYPE format
     * @param ctxt the Jackson deserialization context
     * @param emfContext optional EMF context for inline mapping resolution (may be null)
     */
    private EClass resolveTypeFromValue(String typeValue, DeserializationState state,
            EClass hintEClass, String schemaValue, DeserializationContext ctxt,
            EMFCodecReadContext emfContext, String streamFingerprint) {
        if (typeValue == null) {
            return hintEClass;
        }

        // OVERRIDE turns CODEC_ROOT_TYPE from a hint into a directive for the root object
        // (issue #173, spec 06-type.md §6.5.1). Checked before any resolution: the point of the
        // mode is that the document's type is not consulted, so resolving it first would be
        // work whose only possible outcome is being thrown away.
        EClass overridden = applyRootTypeOverride(typeValue, ctxt);
        if (overridden != null) {
            state.setResolvedEClass(overridden);
            return overridden;
        }

        // Build type config from resolved global config
        TypeConfig globalTypeConfig = config.resolveGlobalTypeConfig();
        TypeConfig typeConfig = TypeConfig.builder()
                .include(true)
                .typeKey(globalTypeConfig != null ? globalTypeConfig.getTypeKey() : DEFAULT_TYPE_KEY)
                .strategy(globalTypeConfig != null ? globalTypeConfig.getStrategy() : null)
                .build();

        // Build supertype config for validation (null means no validation)
        SuperTypeConfig globalSuperTypeConfig = config.resolveGlobalSuperTypeConfig();

        // Extract mapId from the hint EClass's DiscriminatorConfig for targeted
        // registry resolution. When a mapId is present, the TypeDeserializationEntry
        // uses resolve(mapId, ...) instead of resolveFromAny(), ensuring the correct
        // fallback strategy is applied. See spec 08-discriminator-mapping.md §7.1.
        String discriminatorMapId = getDiscriminatorMapId(hintEClass);

        TypeDeserializationEntry typeEntry = new TypeDeserializationEntry(
                typeConfig, config.getTypeDiscriminatorReader(),
                globalSuperTypeConfig, discriminatorMapId);

        // Compose type value with schema if needed (SCHEMA_AND_TYPE format)
        String effectiveTypeValue = typeValue;
        if (schemaValue != null && !schemaValue.isEmpty() && !typeValue.contains("#//")) {
            effectiveTypeValue = schemaValue + "#//" + typeValue;
        }

        // Extract current reference from EMF context for inline mapping resolution.
        // When deserializing a contained object under an EReference with an inlineMapping
        // annotation, this allows the type resolver to check the reference-scoped registry first.
        EReference currentReference = null;
        if (emfContext != null && emfContext.getCurrentFeature() instanceof EReference ref) {
            currentReference = ref;
        }

        // Delegate to TypeDeserializationEntry for consistent resolution logic
        EClass resolved = typeEntry.resolveEClass(
                effectiveTypeValue, hintEClass, ctxt, currentReference, streamFingerprint);
        if (resolved != null) {
            state.setResolvedEClass(resolved);
            return resolved;
        }

        // Nothing failed when the strategy is NONE: the caller declared that the document
        // carries no type, so falling back to the hint - the declared root type or the
        // reference type - is the documented outcome, not a degradation (issue #171).
        if (typeConfig.getStrategy() == TypeStrategy.NONE) {
            return hintEClass;
        }

        // Type resolution failed - behavior depends on DeserializationMode
        if (ContextHelper.isStrictMode(ctxt)) {
            // STRICT mode: Error when type cannot be resolved
            String msg = "Could not resolve EClass from type value: " + typeValue;
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, null, "CodecEObjectDeserializer");
            // Still return hint to allow partial parsing, but error is recorded
            return hintEClass;
        } else {
            // LENIENT/AUTO_DETECT mode: Warning and fall back to hint
            if (hintEClass != null) {
                String msg = String.format(
                        "Type resolved via fallback. Could not resolve '%s', using hint '%s'",
                        typeValue, hintEClass.getName());
                LOGGER.warning(msg);
                ContextHelper.addWarning(ctxt, msg, null, "CodecEObjectDeserializer");
            }
            return hintEClass;
        }
    }

    /**
     * Sets the type value as an attribute if a matching feature exists.
     */
    private void setTypeAsAttributeIfExists(EObject eObject, String propertyName,
            String typeValue, DeserializationContext ctxt) {
        if (eObject == null || propertyName == null || typeValue == null) {
            return;
        }

        EClass eClass = eObject.eClass();
        EStructuralFeature feature = eClass.getEStructuralFeature(propertyName);

        if (feature instanceof EAttribute attr) {
            if (attr.getEType().getInstanceClass() == String.class ||
                "EString".equals(attr.getEType().getName())) {
                try {
                    eObject.eSet(feature, typeValue);
                    LOGGER.fine(() -> "Set type key '" + propertyName + "' as attribute with value: " + typeValue);
                } catch (Exception e) {
                    // The model declares this feature (GeoJSON's "type", spec 06-type.md
                    // §6.1) and we could not fill it - a caller inspecting diagnostics has
                    // to see that, not only the log (issue #134)
                    String msg = "Could not store the type value in feature '" + propertyName
                            + "' of " + eClass.getName() + ": " + e.getMessage();
                    LOGGER.warning(msg);
                    ContextHelper.addWarning(ctxt, msg, null, "CodecEObjectDeserializer");
                }
            }
        }
    }

    /**
     * Reads the current value from the parser into a simple Java object.
     * Entry point that starts depth tracking at 0.
     */
    private Object readCurrentValue(JsonParser parser, DeserializationContext ctxt) {
        return readCurrentValue(parser, ctxt, 0);
    }

    /**
     * Reads the current value from the parser into a simple Java object,
     * tracking nesting depth to prevent stack overflow from malicious input.
     * <p>
     * When the depth limit is exceeded, the remaining nested content is skipped
     * via {@code parser.skipChildren()} to keep the parser in a consistent state,
     * and {@code null} is returned. The recursion then unwinds naturally as each
     * level finds its matching END_ARRAY/END_OBJECT.
     * </p>
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context (for diagnostics)
     * @param depth current nesting depth
     * @return the parsed value, or null if depth exceeded or unsupported token
     */
    private Object readCurrentValue(JsonParser parser, DeserializationContext ctxt, int depth) {
        if (depth > MAX_NESTING_DEPTH) {
            String msg = "Maximum nesting depth exceeded: " + MAX_NESTING_DEPTH;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
            parser.skipChildren();
            return null;
        }

        JsonToken token = parser.currentToken();

        switch (token) {
            case VALUE_STRING:
                return parser.getString();
            case VALUE_NUMBER_INT:
                return parser.getLongValue();
            case VALUE_NUMBER_FLOAT:
                return parser.getDoubleValue();
            case VALUE_TRUE:
                return Boolean.TRUE;
            case VALUE_FALSE:
                return Boolean.FALSE;
            case VALUE_NULL:
                return null;
            case START_OBJECT:
                return readObjectAsMap(parser, ctxt, depth + 1);
            case START_ARRAY:
                return readArrayAsList(parser, ctxt, depth + 1);
            default:
                return null;
        }
    }

    /**
     * Reads a JSON object into a Map, tracking nesting depth.
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context (for diagnostics)
     * @param depth current nesting depth
     */
    private Map<String, Object> readObjectAsMap(JsonParser parser, DeserializationContext ctxt, int depth) {
        Map<String, Object> result = new LinkedHashMap<>();
        boolean limitExceeded = false;

        while (TokenLoops.hasNextField(parser)) {
            if (!limitExceeded && result.size() >= MAX_COLLECTION_SIZE) {
                String msg = "Object exceeds maximum size: " + MAX_COLLECTION_SIZE;
                LOGGER.warning(msg);
                ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
                limitExceeded = true;
            }
            if (limitExceeded) {
                // Skip remaining entries without accumulating
                parser.nextToken(); // Move to value
                parser.skipChildren();
                continue;
            }
            String fieldName = parser.currentName();
            parser.nextToken(); // Move to value
            Object value = readCurrentValue(parser, ctxt, depth);
            result.put(fieldName, value);
        }

        return result;
    }

    /**
     * Reads a JSON array into a List, tracking nesting depth and collection size.
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context (for diagnostics)
     * @param depth current nesting depth
     */
    private List<Object> readArrayAsList(JsonParser parser, DeserializationContext ctxt, int depth) {
        List<Object> result = new ArrayList<>();
        boolean limitExceeded = false;

        while (TokenLoops.hasNextElement(parser)) {
            if (!limitExceeded && result.size() >= MAX_COLLECTION_SIZE) {
                String msg = "Array exceeds maximum size: " + MAX_COLLECTION_SIZE;
                LOGGER.warning(msg);
                ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
                limitExceeded = true;
            }
            if (limitExceeded) {
                // Skip remaining elements without accumulating
                parser.skipChildren();
                continue;
            }
            Object value = readCurrentValue(parser, ctxt, depth);
            result.add(value);
        }

        return result;
    }

    /**
     * Processes deferred properties after the type is resolved.
     * <p>
     * Checks {@code strictOnUnknown} configuration for deferred properties that
     * don't match any known feature.
     * </p>
     */
    private void processDeferredProperties(DeserializationState state,
            Map<String, Object> deferredProperties, DeserializationContext ctxt) {
        EClass eClass = state.getResolvedEClass();
        if (eClass == null) {
            return;
        }

        Map<String, DeserializationEntry> entries = buildDeserializationEntries(eClass, ctxt);

        for (Map.Entry<String, Object> entry : deferredProperties.entrySet()) {
            String propertyName = entry.getKey();
            Object value = entry.getValue();

            DeserializationEntry deserEntry = entries.get(propertyName);
            CodecPrefixReader prefixReader = deserEntry == null ? null : resolvePrefixReader(propertyName, ctxt);
            if (deserEntry != null && prefixReader != null) {
                reportPrefixClash(propertyName, eClass, ctxt);
            }
            if (deserEntry != null && value != null) {
                replayDeferredValue(state, deserEntry, value, ctxt);
            } else if (deserEntry == null && isDeliberatelyNotRead(propertyName, eClass)) {
                // Written by the codec itself, or excluded from reading - not unknown.
                // Deferred properties need the same treatment as direct ones (issue #131)
                LOGGER.fine("Skipping deliberately unread deferred property: " + propertyName);
            } else if (deserEntry == null && (prefixReader = resolvePrefixReader(propertyName, ctxt)) != null) {
                // A backend-owned prefix key, met before the type was known (issue #193)
                readPrefix(state, propertyName, prefixReader, value, ctxt);
            } else if (deserEntry == null && unsettableFeature(propertyName, eClass) != null) {
                ContextHelper.addWarning(ctxt, unsettableMessage(propertyName, eClass), null,
                        "CodecEObjectDeserializer");
            } else if (deserEntry == null) {
                // Unknown property in deferred list - check strictOnUnknown config
                ClassConfig classConfig = config.resolveClassConfig(eClass);
                if (classConfig != null && classConfig.isStrictOnUnknown()) {
                    String msg = "Unknown feature '" + propertyName + "' for EClass " + eClass.getName();
                    LOGGER.warning(msg);
                    ContextHelper.addError(ctxt, msg, null, "CodecEObjectDeserializer");
                    throw new IllegalStateException(msg);
                } else {
                    LOGGER.fine("Skipping unknown deferred property: " + propertyName);
                    ContextHelper.addWarning(ctxt,
                        "Unknown feature '" + propertyName + "' for EClass " + eClass.getName(),
                        null, "CodecEObjectDeserializer");
                }
            }
        }
    }

    /**
     * Replays a deferred value through a TokenBuffer for proper deserialization.
     */
    /**
     * The prefix reader for a key: an instance bound through the load options wins, then the
     * registry; {@code null} when the key has no reader and is therefore unknown (issue #193).
     */
    private CodecPrefixReader resolvePrefixReader(String key, DeserializationContext ctxt) {
        if (ctxt != null && ctxt.getAttribute(ContextHelper.PREFIX_READER_INSTANCES) instanceof Map<?, ?> instances
                && instances.get(key) instanceof CodecPrefixReader bound) {
            return bound;
        }
        return entryContext.getPrefixRegistry().getReader(key).orElse(null);
    }

    /** The feature wins a name clash with a prefix key; says so once per class and key. */
    private void reportPrefixClash(String key, EClass eClass, DeserializationContext ctxt) {
        if (eClass != null && reportedPrefixClashes.add(eClass.getName() + "#" + key)) {
            ContextHelper.addWarning(ctxt, "Prefix key '" + key + "' collides with feature "
                    + eClass.getName() + "." + key + "; read as the feature", PREFIX_SOURCE);
        }
    }

    /**
     * Hands a buffered prefix value to its reader with the EObject already created (spec
     * 14-custom-values.md §13.5). A reader that throws follows the strictness hierarchy: an
     * error diagnostic, and under STRICT the load fails.
     */
    private void readPrefix(DeserializationState state, String key, CodecPrefixReader reader,
            Object value, DeserializationContext ctxt) {
        try {
            tools.jackson.databind.util.TokenBuffer buffer = bufferFor(value, ctxt);
            try (tools.jackson.core.JsonParser bufferParser = buffer.asParser(ctxt)) {
                bufferParser.nextToken();
                reader.read(key, state.getEObject(),
                        entryContext.createPrefixReaderContext(bufferParser, ctxt));
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            String msg = "Prefix reader for '" + key + "' failed on "
                    + (state.getResolvedEClass() != null ? state.getResolvedEClass().getName() : "?")
                    + ": " + e.getMessage();
            LOGGER.warning(msg);
            ContextHelper.addError(ctxt, msg, null, PREFIX_SOURCE);
            if (ContextHelper.isStrictMode(ctxt)) {
                throw new IllegalStateException(msg, e);
            }
        }
    }

    /** Writes a value read by {@link #readCurrentValue} into a token buffer, so it can be parsed again. */
    private tools.jackson.databind.util.TokenBuffer bufferFor(Object value, DeserializationContext ctxt) {
        tools.jackson.databind.util.TokenBuffer buffer = ctxt.bufferForInputBuffering(ctxt.getParser());
        writeValueToBuffer(buffer, value);
        return buffer;
    }

    private void replayDeferredValue(DeserializationState state, DeserializationEntry entry,
            Object value, DeserializationContext ctxt) {
        try {
            tools.jackson.databind.util.TokenBuffer buffer = ctxt.bufferForInputBuffering(ctxt.getParser());

            if (value instanceof String s) {
                buffer.writeString(s);
            } else if (value instanceof Number n) {
                if (n instanceof Integer i) {
                    buffer.writeNumber(i);
                } else if (n instanceof Long l) {
                    buffer.writeNumber(l);
                } else if (n instanceof Double d) {
                    buffer.writeNumber(d);
                } else {
                    buffer.writeNumber(n.doubleValue());
                }
            } else if (value instanceof Boolean b) {
                buffer.writeBoolean(b);
            } else if (value instanceof Map<?, ?> map) {
                buffer.writeStartObject();
                for (var e : map.entrySet()) {
                    buffer.writeName(String.valueOf(e.getKey()));
                    writeValueToBuffer(buffer, e.getValue());
                }
                buffer.writeEndObject();
            } else if (value instanceof List<?> list) {
                buffer.writeStartArray();
                for (Object item : list) {
                    writeValueToBuffer(buffer, item);
                }
                buffer.writeEndArray();
            } else if (value == null) {
                buffer.writeNull();
            } else {
                buffer.writeString(value.toString());
            }

            try (tools.jackson.core.JsonParser bufferParser = buffer.asParser(ctxt)) {
                bufferParser.nextToken();
                entry.deserialize(state, bufferParser, ctxt);
            }
        } catch (IllegalStateException e) {
            // Propagate IllegalStateException (e.g., from discriminator ERROR strategy)
            throw e;
        } catch (Exception e) {
            // A deferred value that cannot be replayed is simply gone. Reporting it at FINE
            // level made it invisible even to a caller who inspects the diagnostics - the
            // quietest variant of the problem in issue #131
            ConversionFailures.report(entryContext, state.getResolvedEClass(), ctxt, null,
                    "CodecEObjectDeserializer",
                    "Could not replay deferred value for '" + entry.getKey() + "': "
                            + e.getMessage());
        }
    }

    /**
     * Writes a value to the TokenBuffer.
     */
    private void writeValueToBuffer(tools.jackson.databind.util.TokenBuffer buffer, Object value) {
        if (value instanceof String s) {
            buffer.writeString(s);
        } else if (value instanceof Number n) {
            if (n instanceof Integer i) {
                buffer.writeNumber(i);
            } else if (n instanceof Long l) {
                buffer.writeNumber(l);
            } else if (n instanceof Double d) {
                buffer.writeNumber(d);
            } else {
                buffer.writeNumber(n.doubleValue());
            }
        } else if (value instanceof Boolean b) {
            buffer.writeBoolean(b);
        } else if (value instanceof Map<?, ?> map) {
            buffer.writeStartObject();
            for (var e : map.entrySet()) {
                buffer.writeName(String.valueOf(e.getKey()));
                writeValueToBuffer(buffer, e.getValue());
            }
            buffer.writeEndObject();
        } else if (value instanceof List<?> list) {
            buffer.writeStartArray();
            for (Object item : list) {
                writeValueToBuffer(buffer, item);
            }
            buffer.writeEndArray();
        } else if (value == null) {
            buffer.writeNull();
        } else {
            buffer.writeString(value.toString());
        }
    }

    /**
     * Deserializes a single property.
     * <p>
     * Checks {@code strictOnUnknown} configuration: when true, unknown fields
     * cause an error; when false (default), unknown fields are skipped with a warning.
     * </p>
     */
    private void deserializeProperty(DeserializationState state, String propertyName,
            JsonParser parser, DeserializationContext ctxt) {
        EClass eClass = state.getResolvedEClass();
        Map<String, DeserializationEntry> entries = buildDeserializationEntries(eClass, ctxt);

        DeserializationEntry entry = entries.get(propertyName);
        CodecPrefixReader prefixReader;
        if (entry != null) {
            if (resolvePrefixReader(propertyName, ctxt) != null) {
                reportPrefixClash(propertyName, eClass, ctxt);
            }
            entry.deserialize(state, parser, ctxt);
        } else if (isDeliberatelyNotRead(propertyName, eClass)) {
            // The codec wrote this itself, or was told not to read it. Reporting it as
            // unknown means the codec cannot read its own output without complaining, which
            // makes the diagnostics useless as a signal (issue #131).
            parser.skipChildren();
        } else if ((prefixReader = resolvePrefixReader(propertyName, ctxt)) != null) {
            // A backend-owned prefix key with a registered reader is known (issue #193). The
            // value is buffered first so a failing reader can never leave the stream half read.
            readPrefix(state, propertyName, prefixReader, readCurrentValue(parser, ctxt), ctxt);
        } else if (unsettableFeature(propertyName, eClass) != null) {
            ContextHelper.addWarning(ctxt, unsettableMessage(propertyName, eClass), parser,
                    "CodecEObjectDeserializer");
            parser.skipChildren();
        } else {
            // Unknown property - check strictOnUnknown config
            ClassConfig classConfig = config.resolveClassConfig(eClass);
            if (classConfig != null && classConfig.isStrictOnUnknown()) {
                // Strict mode: throw error on unknown field
                String msg = "Unknown feature '" + propertyName + "' for EClass " + eClass.getName();
                LOGGER.warning(msg);
                ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
                throw new IllegalStateException(msg);
            } else {
                // Lenient mode (default): skip with warning
                LOGGER.fine("Unknown property: " + propertyName);
                ContextHelper.addWarning(ctxt,
                    "Unknown feature '" + propertyName + "' for EClass " + eClass.getName(),
                    parser, "CodecEObjectDeserializer");
                parser.skipChildren();
            }
        }
    }

    /**
     * Checks strictOnMissing for required features.
     * <p>
     * When {@code strictOnMissing=true}, this method checks all required features
     * (lowerBound >= 1) and throws an error if any are not set.
     * When {@code strictOnMissing=false} (default), missing required features
     * generate a warning and use the EMF default value.
     * </p>
     *
     * @param eObject the deserialized object to check
     * @param state the deserialization state
     * @param ctxt the Jackson deserialization context
     * @param parser the JSON parser (for location info)
     */
    private void checkStrictOnMissing(EObject eObject, DeserializationState state,
            DeserializationContext ctxt, JsonParser parser) {
        EClass eClass = eObject.eClass();
        ClassConfig classConfig = config.resolveClassConfig(eClass);

        // Only check if strictOnMissing is enabled
        if (classConfig == null || !classConfig.isStrictOnMissing()) {
            return;
        }

        List<String> missingFeatures = new ArrayList<>();

        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            // Check if feature is required (lowerBound >= 1)
            if (feature.getLowerBound() < 1) {
                continue;
            }

            // Skip features that are not deserialized (transient, derived, ignored)
            FeatureConfig featureConfig = config.resolveFeatureConfig(feature);
            if (!featureConfig.shouldDeserialize()) {
                continue;
            }

            // Skip non-changeable features
            if (!feature.isChangeable()) {
                continue;
            }

            // Check if the feature is set
            if (!eObject.eIsSet(feature)) {
                missingFeatures.add(feature.getName());
            }
        }

        if (!missingFeatures.isEmpty()) {
            String msg = "Missing required feature(s) for EClass " + eClass.getName() + ": "
                    + String.join(", ", missingFeatures);
            LOGGER.warning(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Builds deserialization entries for the given EClass.
     *
     * @param eClass the EClass to build entries for
     * @return map of property name to deserialization entry
     */
    private Map<String, DeserializationEntry> buildDeserializationEntries(EClass eClass,
            DeserializationContext ctxt) {
        Map<String, DeserializationEntry> entries = new HashMap<>();

        // Resolve configs for this EClass. The id config takes the reference this object is
        // read through, so a reference-scoped idKey registers the entry under the key the
        // writer actually used - otherwise a renamed id is read as an unknown field and the
        // identity is lost (issue #176).
        TypeConfig typeConfig = config.resolveTypeConfig(eClass);
        IdConfig idConfig = config.resolveIdConfig(eClass,
                ContextHelper.getCurrentDeserializationReference(ctxt));
        SuperTypeConfig superTypeConfig = config.resolveSuperTypeConfig(eClass);

        // Add type entry (with supertype config for STRUCTURED format validation)
        if (typeConfig != null && typeConfig.isInclude()) {
            TypeDeserializationEntry typeEntry = new TypeDeserializationEntry(
                    typeConfig, config.getTypeDiscriminatorReader(),
                    superTypeConfig);
            entries.put(typeEntry.getKey(), typeEntry);
        }

        // Add ID entry (the entry handles FEATURE_ONLY mode internally)
        if (idConfig != null) {
            IdDeserializationEntry idEntry = new IdDeserializationEntry(idConfig, eClass, entryContext);
            entries.put(idEntry.getKey(), idEntry);

            // A document that carries its own separator overrules the configured one
            // (spec §9.3) - it states how the id was actually written
            IdSeparatorDeserializationEntry separatorEntry =
                    new IdSeparatorDeserializationEntry(idConfig);
            entries.putIfAbsent(separatorEntry.getKey(), separatorEntry);
        }

        // Add supertype entry (parses supertype info; validation depends on config)
        if (superTypeConfig != null) {
            SuperTypeDeserializationEntry superTypeEntry = new SuperTypeDeserializationEntry(superTypeConfig);
            entries.put(superTypeEntry.getKey(), superTypeEntry);
        }

        // Add feature entries
        for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
            FeatureConfig featureConfig = config.resolveFeatureConfig(feature);

            // Skip features that should not be deserialized
            // shouldDeserialize() considers ignore, ignoreRead, forceRead flags
            if (!featureConfig.shouldDeserialize()) {
                continue;
            }

            // Skip non-changeable features (can't set values on them)
            if (!feature.isChangeable()) {
                continue;
            }

            DeserializationEntry entry;
            if (feature instanceof EAttribute) {
                entry = new AttributeDeserializationEntry(featureConfig, (EAttribute) feature,
                        entryContext);
            } else if (feature instanceof EReference) {
                // Resolve per-reference refKey from ReferenceConfig
                ReferenceConfig refConfig = config.resolveReferenceConfig(feature);
                String refKey = refConfig != null ? refConfig.getRefKey() : DEFAULT_REF_KEY;

                entry = new ReferenceDeserializationEntry(featureConfig, (EReference) feature,
                        refKey, entryContext);
            } else {
                continue;
            }

            entries.put(entry.getKey(), entry);
        }

        return entries;
    }

    // ========================================================================
    // FeaturePath-based deserialization
    // ========================================================================

    /**
     * Gets the discriminator path from the EClass's type configuration.
     */
    private String getDiscriminatorPath(EClass eClass) {
        if (eClass == null) {
            return null;
        }
        TypeConfig typeConfig = config.resolveTypeConfig(eClass);
        if (typeConfig == null) {
            return null;
        }
        return typeConfig.getDiscriminatorPath();
    }

    /**
     * Gets the typeMapId from the EClass's discriminator configuration.
     * <p>
     * Used to target a specific registry during featurePath-based type resolution,
     * ensuring the correct fallback strategy is applied.
     * </p>
     */
    private String getDiscriminatorMapId(EClass eClass) {
        if (eClass == null) {
            return null;
        }
        // Try DiscriminatorConfig first (from ConfigurationResolver)
        DiscriminatorConfig discriminatorConfig = config.resolveDiscriminatorConfig(eClass);
        if (discriminatorConfig != null) {
            String mapId = discriminatorConfig.getTypeMapId();
            if (mapId != null) {
                return mapId;
            }
        }
        // Fall back to direct annotation scanning via TypeDiscriminatorReader
        // (walks up supertypes looking for typeMapping/{mapId} annotations)
        TypeDiscriminatorReader tds = config.getTypeDiscriminatorReader();
        if (tds != null) {
            return tds.getMapIdForEClass(eClass);
        }
        return null;
    }

    /**
     * Deserializes an EObject using featurePath-based type resolution.
     */
    private EObject deserializeWithFeaturePath(
            JsonParser parser,
            DeserializationContext ctxt,
            DeserializationState state,
            EClass hintEClass,
            String discriminatorPath) {

        LOGGER.fine("Using featurePath-based type resolution: " + discriminatorPath);

        // Extract mapId from DiscriminatorConfig for targeted registry resolution
        String mapId = getDiscriminatorMapId(hintEClass);

        FeaturePathTypeResolver resolver = new FeaturePathTypeResolver(
                discriminatorPath, config.getTypeDiscriminatorReader(), mapId);
        resolver.scan(parser, ctxt);

        EClass resolvedEClass = resolver.getResolvedEClass();
        if (resolvedEClass == null) {
            if (hintEClass != null && !hintEClass.isAbstract()) {
                LOGGER.fine("FeaturePath resolution failed, using hint class: " + hintEClass.getName());
                resolvedEClass = hintEClass;
            } else {
                String msg = "Cannot deserialize: featurePath resolution failed for '" +
                        discriminatorPath + "' and no concrete fallback available";
                LOGGER.severe(msg);
                ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
                return null;
            }
        }

        state.setResolvedEClass(resolvedEClass);

        JsonParser bufferedParser = resolver.getBufferedParser(ctxt, parser);
        if (bufferedParser == null) {
            String msg = "No buffered content available for deserialization";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
            return null;
        }

        return deserializeFromBufferedParser(bufferedParser, ctxt, state);
    }

    /**
     * Deserializes an EObject from a buffered parser.
     */
    private EObject deserializeFromBufferedParser(
            JsonParser parser,
            DeserializationContext ctxt,
            DeserializationState state) {

        EClass resolvedEClass = state.getResolvedEClass();
        if (resolvedEClass == null) {
            String msg = "No resolved EClass in deserialization state";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
            return null;
        }

        EObject eObject = state.createEObject();
        if (eObject == null) {
            String msg = "Failed to create EObject for: " + resolvedEClass.getName();
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "CodecEObjectDeserializer");
            return null;
        }

        JsonToken token = parser.currentToken();
        if (token != JsonToken.START_OBJECT) {
            String msg = "Expected START_OBJECT in buffered parser, got: " + token;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "CodecEObjectDeserializer");
            return eObject;
        }

        while ((token = parser.nextToken()) != JsonToken.END_OBJECT && token != null) {
            if (token == JsonToken.PROPERTY_NAME) {
                String propertyName = parser.currentName();
                parser.nextToken();
                deserializeProperty(state, propertyName, parser, ctxt);
            }
        }

        // Check strictOnMissing for required features
        checkStrictOnMissing(eObject, state, ctxt, parser);

        return eObject;
    }
}
