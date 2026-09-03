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
package org.eclipse.fennec.codec.context;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.util.FingerprintPins;
import org.eclipse.fennec.codec.util.PackageResolver;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;

/**
 * Helper class for managing codec context attributes.
 * <p>
 * Provides type-safe access to context attributes used during
 * EObject serialization and deserialization.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public final class ContextHelper {

    /** Context attribute key for expected type hint (must be EClass) */
    public static final String EXPECTED_TYPE = "CODEC_EXPECTED_TYPE";

    /** Context attribute key for collecting unresolved references */
    public static final String UNRESOLVED_REFERENCES = "CODEC_UNRESOLVED_REFERENCES";

    /** Context attribute key for the diagnostic collector */
    public static final String DIAGNOSTIC_COLLECTOR = "CODEC_DIAGNOSTIC_COLLECTOR";

    /**
     * Context attribute key for suppressing type serialization.
     * <p>
     * When set to true, the next EObject serialization should skip writing the _type field.
     * This is used by smart compression when instance type == reference type.
     * </p>
     */
    public static final String SUPPRESS_TYPE = "CODEC_SUPPRESS_TYPE";

    /**
     * Context attribute key for the context schema URI (root namespace).
     * <p>
     * Used by same-schema smart compression to determine if a type belongs
     * to the same schema as the root object. When set, types from this
     * schema will be written as simple names instead of full URIs.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/04-global-options.md#1-smart-compression">Spec: Smart Compression</a>
     */
    public static final String CONTEXT_SCHEMA_URI = "CODEC_CONTEXT_SCHEMA_URI";

    /**
     * Context attribute key holding the per-load {@link PackageResolver} (issue #54, B.5).
     * Provides the binding nsURI -&gt; package-version resolution order (pin, ResourceSet
     * registry, MetadataService candidate query, global registry) and the A.3 count rule.
     */
    public static final String PACKAGE_RESOLVER = "CODEC_PACKAGE_RESOLVER";

    /**
     * Context attribute key holding the per-save {@link FingerprintPins} (issue #73, B.1).
     * <p>
     * Tracks which EPackage version has already been announced for each nsURI, so the
     * in-band fingerprint is written at first touch only.
     * </p>
     */
    public static final String FINGERPRINT_PINS = "CODEC_FINGERPRINT_PINS";

    /**
     * Context attribute key holding the set of field names accepted as the in-band
     * fingerprint while reading (issue #73, B.1).
     * <p>
     * Value: {@code Set<String>}, seeded from <b>caller-side</b> configuration only, always
     * including the default keys. This attribute is what makes the chicken-and-egg break of
     * spec §8.5 structural rather than merely documented: reading has to know the key before
     * the model version is selected, so the read key can never come from the model. The
     * deserializer sees this set and has no path back to an annotation.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/06-type.md#85--the-fingerprintkey-chicken-and-egg-problem">Spec 06 §8.5</a>
     */
    public static final String FINGERPRINT_READ_KEYS = "CODEC_FINGERPRINT_READ_KEYS";

    /**
     * Context attribute key indicating if root object serialization is complete.
     * <p>
     * Used by smart compression to ensure the root object always uses a full URI
     * (to establish the context schema), while contained objects can use simple names.
     * </p>
     */
    public static final String ROOT_SERIALIZED = "CODEC_ROOT_SERIALIZED";

    /**
     * Context attribute key for feature-specific type hints.
     * <p>
     * Value: {@code Map<EStructuralFeature, EClass>}
     * </p>
     * <p>
     * Provides EClass type hints for specific EObject-typed features where
     * the concrete type cannot be determined from the JSON alone.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/18-feature-type-hints.md">Spec: Feature Type Hints</a>
     */
    public static final String FEATURE_TYPE_HINTS = "CODEC_FEATURE_TYPE_HINTS";

    /**
     * Context attribute key for feature-specific value readers.
     * <p>
     * Value: {@code Map<EStructuralFeature, String>}
     * </p>
     * <p>
     * Maps features to ValueReader names. Takes priority over FEATURE_TYPE_HINTS.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/18-feature-type-hints.md">Spec: Feature Type Hints</a>
     */
    public static final String FEATURE_VALUE_READERS = "CODEC_FEATURE_VALUE_READERS";

    /**
     * Context attribute key for feature-specific value writers.
     * <p>
     * Value: {@code Map<EStructuralFeature, String>}
     * </p>
     * <p>
     * Maps features to ValueWriter names for serialization.
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/18-feature-type-hints.md">Spec: Feature Type Hints</a>
     */
    public static final String FEATURE_VALUE_WRITERS = "CODEC_FEATURE_VALUE_WRITERS";

    /**
     * Context attribute key for feature-specific value reader instances.
     * <p>
     * Value: {@code Map<EStructuralFeature, CodecValueReader>}
     * </p>
     * <p>
     * Maps features directly to ValueReader instances (bypasses registry lookup).
     * Takes priority over FEATURE_VALUE_READERS (by name).
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec: Custom Value Readers/Writers</a>
     */
    public static final String FEATURE_VALUE_READER_INSTANCES = "CODEC_FEATURE_VALUE_READER_INSTANCES";

    /**
     * Context attribute key for feature-specific value writer instances.
     * <p>
     * Value: {@code Map<EStructuralFeature, CodecValueWriter>}
     * </p>
     * <p>
     * Maps features directly to ValueWriter instances (bypasses registry lookup).
     * Takes priority over FEATURE_VALUE_WRITERS (by name).
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec: Custom Value Readers/Writers</a>
     */
    public static final String FEATURE_VALUE_WRITER_INSTANCES = "CODEC_FEATURE_VALUE_WRITER_INSTANCES";

    /**
     * Attribute key for {@code Map<String, CodecPrefixReader>} - prefix reader instances bound
     * per document key for this load (issue #193); wins over the registry per key.
     */
    public static final String PREFIX_READER_INSTANCES = "CODEC_PREFIX_READER_INSTANCES";

    /**
     * Attribute key for {@code Map<String, CodecPrefixWriter>} - prefix writer instances bound
     * per document key for this save (issue #193); wins over the registry per key.
     */
    public static final String PREFIX_WRITER_INSTANCES = "CODEC_PREFIX_WRITER_INSTANCES";

    /**
     * Context attribute key for the current feature's type hint.
     * <p>
     * Set temporarily during deserialization when a type hint is available
     * for the current feature. Allows ValueReaders to access the hint.
     * </p>
     */
    public static final String FEATURE_TYPE_HINT = "CODEC_FEATURE_TYPE_HINT";

    /**
     * Context attribute key for the current containment EReference being serialized.
     * <p>
     * Set by {@link org.eclipse.fennec.codec.ser.ReferenceSerializationEntry} before
     * serializing contained objects. Used by {@link org.eclipse.fennec.codec.ser.TypeSerializationEntry}
     * to perform inline mapping reverse lookup: resolving the correct discriminator
     * value for the contained object's EClass within the reference's scope.
     * </p>
     */
    public static final String CURRENT_SERIALIZATION_REFERENCE = "CODEC_CURRENT_SERIALIZATION_REFERENCE";

    /**
     * Context attribute key for the EMF Resource.
     * <p>
     * Used as a fallback when the parser's stream read context does not
     * provide the resource (e.g., when using {@code FormatDelegateParser}
     * instead of {@code CodecJsonParser}).
     * </p>
     */
    public static final String RESOURCE = "CODEC_RESOURCE";

    /**
     * Context attribute key for the deserialization mode.
     * <p>
     * Value: {@code DeserializationMode} enum (LENIENT, STRICT, AUTO_DETECT)
     * </p>
     * <p>
     * Controls how strictly the deserializer follows the configured type strategy:
     * <ul>
     *   <li>LENIENT (default): Try configured strategy first, then fallback resolution</li>
     *   <li>STRICT: Type field MUST match configured strategy exactly; missing/malformed → ERROR</li>
     *   <li>AUTO_DETECT: Ignore configured strategy; probe JSON structure to determine format</li>
     * </ul>
     * </p>
     *
     * @see <a href="docs/codec-v2-spec/06-type.md#652-deserialization-mode">Spec: Deserialization Mode</a>
     */
    public static final String DESERIALIZATION_MODE = "CODEC_DESERIALIZATION_MODE";

    /**
     * Context attribute key for the type hint mode, i.e. how {@code CODEC_ROOT_TYPE} resolves a
     * collision with the document's own type (issue #173).
     * <ul>
     *   <li>HINT (default): the document wins; the hint fills in where it says nothing</li>
     *   <li>OVERRIDE: the hint wins for the root object; the document's type is discarded</li>
     * </ul>
     *
     * @see <a href="docs/codec-v2-spec/06-type.md#651-type-hint-mode">Spec: Type Hint Mode</a>
     */
    public static final String TYPE_HINT_MODE = "CODEC_TYPE_HINT_MODE";

    /**
     * Context attribute key for how deeply nested the EObject currently being read is.
     * <p>
     * Maintained by the one entry point every EObject passes through, root and contained
     * alike, so depth 1 means "top-level value of the document". The stream context cannot
     * answer this: a contained object's current feature is reset when its context is reused
     * for an array element, and the expected type is repurposed per nesting level.
     * </p>
     */
    private static final String EOBJECT_DEPTH = "CODEC_EOBJECT_DEPTH";

    /**
     * Context attribute key for the EReference whose value is currently being read (issue #176).
     * <p>
     * The read-side counterpart of {@link #CURRENT_SERIALIZATION_REFERENCE}. It lives on the
     * deserialization context rather than on the stream context because the stream context is
     * reused - and its current feature reset - for each element of an array, which would lose
     * the reference for exactly the multi-valued containments that need it.
     * </p>
     */
    public static final String CURRENT_DESERIALIZATION_REFERENCE =
            "CODEC_CURRENT_DESERIALIZATION_REFERENCE";

    private ContextHelper() {
        // Static helper class
    }

    /**
     * Gets the expected EClass type from the deserialization context.
     * <p>
     * The expected type is used as a fallback when no explicit _type field
     * is present in the JSON. It can be set from:
     * <ul>
     *   <li>CODEC_ROOT_TYPE option (resolved before deserialization starts)</li>
     *   <li>EReference.eType (for nested containment objects)</li>
     * </ul>
     * </p>
     *
     * @param ctxt the deserialization context
     * @return the expected EClass, or null if not set
     * @throws IllegalStateException if the attribute is set but not an EClass
     */
    public static EClass getExpectedType(DeserializationContext ctxt) {
        Object value = ctxt.getAttribute(EXPECTED_TYPE);
        return validateExpectedType(value);
    }

    /**
     * Gets the expected EClass type from the serialization context.
     *
     * @param ctxt the serialization context
     * @return the expected EClass, or null if not set
     * @throws IllegalStateException if the attribute is set but not an EClass
     */
    public static EClass getExpectedType(SerializationContext ctxt) {
        Object value = ctxt.getAttribute(EXPECTED_TYPE);
        return validateExpectedType(value);
    }

    /**
     * Sets the expected EClass type in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param eClass the expected EClass (must not be null)
     * @throws IllegalArgumentException if eClass is null
     */
    public static void setExpectedType(DeserializationContext ctxt, EClass eClass) {
        if (eClass == null) {
            throw new IllegalArgumentException("eClass must not be null");
        }
        ctxt.setAttribute(EXPECTED_TYPE, eClass);
    }

    /**
     * Sets the expected EClass type in the serialization context.
     *
     * @param ctxt the serialization context
     * @param eClass the expected EClass (must not be null)
     * @throws IllegalArgumentException if eClass is null
     */
    public static void setExpectedType(SerializationContext ctxt, EClass eClass) {
        if (eClass == null) {
            throw new IllegalArgumentException("eClass must not be null");
        }
        ctxt.setAttribute(EXPECTED_TYPE, eClass);
    }

    /**
     * Clears the expected type from the deserialization context.
     *
     * @param ctxt the deserialization context
     */
    public static void clearExpectedType(DeserializationContext ctxt) {
        ctxt.setAttribute(EXPECTED_TYPE, null);
    }

    /**
     * Clears the expected type from the serialization context.
     *
     * @param ctxt the serialization context
     */
    public static void clearExpectedType(SerializationContext ctxt) {
        ctxt.setAttribute(EXPECTED_TYPE, null);
    }

    // ========================================================================
    // Suppress Type Methods (for smart compression)
    // ========================================================================

    /**
     * Checks if type serialization should be suppressed.
     * <p>
     * Returns true if smart compression determined that _type should be omitted
     * because instance type == reference type.
     * </p>
     *
     * @param ctxt the serialization context
     * @return true if type should be suppressed
     */
    public static boolean isSuppressType(SerializationContext ctxt) {
        Object value = ctxt.getAttribute(SUPPRESS_TYPE);
        return Boolean.TRUE.equals(value);
    }

    /**
     * Sets whether type serialization should be suppressed.
     *
     * @param ctxt the serialization context
     * @param suppress true to suppress type serialization
     */
    public static void setSuppressType(SerializationContext ctxt, boolean suppress) {
        ctxt.setAttribute(SUPPRESS_TYPE, suppress);
    }

    /**
     * Clears the suppress type flag.
     *
     * @param ctxt the serialization context
     */
    public static void clearSuppressType(SerializationContext ctxt) {
        ctxt.setAttribute(SUPPRESS_TYPE, null);
    }

    // ========================================================================
    // Current Serialization Reference (for inline mapping reverse lookup)
    // ========================================================================

    /**
     * Gets the current containment EReference being serialized.
     *
     * @param ctxt the serialization context
     * @return the current EReference, or null if not in a containment context
     */
    public static EReference getCurrentSerializationReference(SerializationContext ctxt) {
        Object value = ctxt.getAttribute(CURRENT_SERIALIZATION_REFERENCE);
        return value instanceof EReference ref ? ref : null;
    }

    /**
     * Sets the current containment EReference being serialized.
     *
     * @param ctxt the serialization context
     * @param reference the containment EReference (may be null to clear)
     */
    public static void setCurrentSerializationReference(SerializationContext ctxt, EReference reference) {
        ctxt.setAttribute(CURRENT_SERIALIZATION_REFERENCE, reference);
    }

    /**
     * Clears the current serialization reference.
     *
     * @param ctxt the serialization context
     */
    public static void clearCurrentSerializationReference(SerializationContext ctxt) {
        ctxt.setAttribute(CURRENT_SERIALIZATION_REFERENCE, null);
    }

    // ========================================================================
    // Context Schema Methods (for smart compression)
    // ========================================================================

    /**
     * Gets the context schema URI from the serialization context.
     * <p>
     * The context schema is derived from the root object and used by smart
     * compression to determine if a type belongs to the same schema.
     * </p>
     *
     * @param ctxt the serialization context
     * @return the context schema URI, or null if not set
     */
    public static String getContextSchemaUri(SerializationContext ctxt) {
        Object value = ctxt.getAttribute(CONTEXT_SCHEMA_URI);
        return value instanceof String ? (String) value : null;
    }

    /**
     * Gets the context schema URI from the deserialization context.
     *
     * @param ctxt the deserialization context
     * @return the context schema URI, or null if not set
     */
    public static String getContextSchemaUri(DeserializationContext ctxt) {
        Object value = ctxt.getAttribute(CONTEXT_SCHEMA_URI);
        return value instanceof String ? (String) value : null;
    }

    /**
     * Gets the per-load {@link PackageResolver} from the deserialization context (B.5).
     *
     * @param ctxt the deserialization context
     * @return the resolver, or null if not set
     */
    /**
     * Gets the per-save {@link FingerprintPins} from the serialization context, creating and
     * installing it on first use (B.1).
     * <p>
     * Lazily created so that saves which never write a fingerprint carry no extra state.
     * </p>
     *
     * @param ctxt the serialization context
     * @return the pins for this save, never {@code null}
     */
    public static FingerprintPins getFingerprintPins(SerializationContext ctxt) {
        Object value = ctxt.getAttribute(FINGERPRINT_PINS);
        if (value instanceof FingerprintPins pins) {
            return pins;
        }
        FingerprintPins pins = new FingerprintPins();
        ctxt.setAttribute(FINGERPRINT_PINS, pins);
        return pins;
    }

    /**
     * The fingerprint keys accepted by every reader regardless of configuration: the inner
     * form used inside a STRUCTURED type object and its PLAIN sibling.
     * <p>
     * Always accepted <b>in addition</b> to any caller-configured key, so a document written
     * with the defaults stays readable by a caller who configured something else.
     * </p>
     */
    public static final Set<String> DEFAULT_FINGERPRINT_KEYS = Set.of(
            ConfigProperty.FINGERPRINT_KEY.getDefaultValue(),
            TypeConfig.toPlainKey(ConfigProperty.FINGERPRINT_KEY.getDefaultValue()));

    /**
     * Builds the set of field names a reader accepts as the in-band fingerprint: the always
     * accepted defaults plus, if configured, a caller-supplied key in both placements.
     *
     * @param configuredKey the caller-configured inner key, or {@code null} for defaults only
     * @return the accepted key set, never empty
     */
    public static Set<String> fingerprintReadKeys(String configuredKey) {
        if (configuredKey == null || configuredKey.isBlank()) {
            return DEFAULT_FINGERPRINT_KEYS;
        }
        Set<String> keys = new HashSet<>(DEFAULT_FINGERPRINT_KEYS);
        keys.add(configuredKey);
        keys.add(TypeConfig.toPlainKey(configuredKey));
        return Set.copyOf(keys);
    }

    /**
     * Gets the field names accepted as the in-band fingerprint for this load.
     *
     * @param ctxt the deserialization context, may be {@code null}
     * @return the accepted key set, falling back to {@link #DEFAULT_FINGERPRINT_KEYS}
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getFingerprintReadKeys(DeserializationContext ctxt) {
        if (ctxt == null) {
            return DEFAULT_FINGERPRINT_KEYS;
        }
        Object value = ctxt.getAttribute(FINGERPRINT_READ_KEYS);
        return value instanceof Set ? (Set<String>) value : DEFAULT_FINGERPRINT_KEYS;
    }

    /**
     * Reports whether a field name carries the in-band fingerprint.
     *
     * @param ctxt the deserialization context, may be {@code null}
     * @param fieldName the JSON field name
     * @return true if the field is the fingerprint carrier
     */
    public static boolean isFingerprintKey(DeserializationContext ctxt, String fieldName) {
        return fieldName != null && getFingerprintReadKeys(ctxt).contains(fieldName);
    }

    /**
     * Puts a {@link PackageResolver} into the context, unless one is already there.
     * <p>
     * {@code CodecResource} seeds the resolver for a load it drives itself. A caller that
     * drives the mapper directly cannot, and without one the type plane resolves against the
     * global {@code EPackage.Registry} instead of the {@link
     * org.eclipse.fennec.emf.osgi.metadata.MetadataService} it was handed — so the
     * deserializer seeds it from its own configuration (issue #163). First one wins, because
     * the resolver pins package versions and a second one would start over.
     * </p>
     *
     * @param ctxt the deserialization context, may be {@code null}
     * @param resolver the resolver to install
     * @return {@code true} when this call installed it
     */
    public static boolean setPackageResolverIfAbsent(DeserializationContext ctxt, PackageResolver resolver) {
        if (ctxt == null || resolver == null || getPackageResolver(ctxt) != null) {
            return false;
        }
        ctxt.setAttribute(PACKAGE_RESOLVER, resolver);
        return true;
    }

    public static PackageResolver getPackageResolver(DeserializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(PACKAGE_RESOLVER);
        return value instanceof PackageResolver ? (PackageResolver) value : null;
    }

    /**
     * Sets the context schema URI in the serialization context.
     *
     * @param ctxt the serialization context
     * @param schemaUri the context schema URI
     */
    public static void setContextSchemaUri(SerializationContext ctxt, String schemaUri) {
        ctxt.setAttribute(CONTEXT_SCHEMA_URI, schemaUri);
    }

    /**
     * Sets the context schema URI in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param schemaUri the context schema URI
     */
    public static void setContextSchemaUri(DeserializationContext ctxt, String schemaUri) {
        ctxt.setAttribute(CONTEXT_SCHEMA_URI, schemaUri);
    }

    /**
     * Checks if root object serialization is complete.
     * <p>
     * Smart compression should only apply simple names after the root object
     * has been serialized (root must use full URI to establish context).
     * </p>
     *
     * @param ctxt the serialization context
     * @return true if root object serialization is complete
     */
    public static boolean isRootSerialized(SerializationContext ctxt) {
        Object value = ctxt.getAttribute(ROOT_SERIALIZED);
        return Boolean.TRUE.equals(value);
    }

    /**
     * Marks root object serialization as complete.
     * <p>
     * Called after the root object's type is serialized to enable
     * smart compression for contained objects.
     * </p>
     *
     * @param ctxt the serialization context
     */
    public static void setRootSerialized(SerializationContext ctxt) {
        ctxt.setAttribute(ROOT_SERIALIZED, Boolean.TRUE);
    }

    /**
     * Resets the smart-compression context so the object about to be written establishes a
     * fresh one (issue #76).
     * <p>
     * Smart compression is defined relative to <b>the root object</b>: a root establishes the
     * context schema and writes a full URI, nested objects consume it and may be compressed to
     * bare names. A resource with several roots has several roots — each has to establish its
     * own context rather than being treated as nested content of the first one.
     * </p>
     * <p>
     * Without this reset the writer compressed later roots against the first root's schema,
     * while the reader — which processes each root in a separate call and therefore starts with
     * no schema — could not resolve the resulting bare names, and dropped those objects.
     * </p>
     *
     * @param ctxt the serialization context
     */
    public static void resetRootContext(SerializationContext ctxt) {
        ctxt.setAttribute(ROOT_SERIALIZED, null);
        ctxt.setAttribute(CONTEXT_SCHEMA_URI, null);
    }

    /**
     * Checks if a type URI belongs to the context schema.
     * <p>
     * Used by smart compression to determine if a type should be written
     * as a simple name instead of a full URI.
     * </p>
     *
     * @param ctxt the serialization context
     * @param typeUri the full type URI (e.g., "http://example.org/1.0#//Person")
     * @return true if the type belongs to the context schema
     */
    public static boolean isSameSchema(SerializationContext ctxt, String typeUri) {
        String contextSchema = getContextSchemaUri(ctxt);
        if (contextSchema == null || typeUri == null) {
            return false;
        }
        // Type URI format: "http://example.org/1.0#//Person"
        // Context schema: "http://example.org/1.0"
        return typeUri.startsWith(contextSchema + "#");
    }

    /**
     * Extracts the simple type name from a full URI.
     * <p>
     * Example: "http://example.org/1.0#//Person" → "Person"
     * </p>
     *
     * @param typeUri the full type URI
     * @return the simple type name, or the original URI if not in expected format
     */
    public static String extractSimpleName(String typeUri) {
        if (typeUri == null) {
            return null;
        }
        int fragmentIndex = typeUri.indexOf("#//");
        if (fragmentIndex >= 0) {
            return typeUri.substring(fragmentIndex + 3);
        }
        return typeUri;
    }

    /**
     * Extracts the schema URI from a full type URI.
     * <p>
     * Example: "http://example.org/1.0#//Person" → "http://example.org/1.0"
     * </p>
     *
     * @param typeUri the full type URI
     * @return the schema URI, or null if not in expected format
     */
    public static String extractSchemaUri(String typeUri) {
        if (typeUri == null) {
            return null;
        }
        int fragmentIndex = typeUri.indexOf("#//");
        if (fragmentIndex >= 0) {
            return typeUri.substring(0, fragmentIndex);
        }
        return null;
    }

    /**
     * Validates that the expected type value is an EClass or null.
     *
     * @param value the value to validate
     * @return the EClass, or null if value is null
     * @throws IllegalStateException if value is not null and not an EClass
     */
    private static EClass validateExpectedType(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof EClass) {
            return (EClass) value;
        }
        throw new IllegalStateException(
                EXPECTED_TYPE + " must be of type EClass, but was: " + value.getClass().getName());
    }

    // ========================================================================
    // Resource Methods (for FormatDelegate-based parsers)
    // ========================================================================

    /**
     * Gets the EMF Resource from the deserialization context.
     * <p>
     * This provides a fallback for parsers that do not use
     * {@code CodecJsonReadContext} (e.g., {@code FormatDelegateParser}).
     * </p>
     *
     * @param ctxt the deserialization context
     * @return the EMF Resource, or null if not set
     */
    public static Resource getResource(DeserializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(RESOURCE);
        return value instanceof Resource ? (Resource) value : null;
    }

    /**
     * Gets the EMF Resource from the serialization context.
     * <p>
     * This provides a fallback for generators that do not use
     * {@code CodecWriteContext} (e.g., {@code FormatDelegateGenerator}), so the
     * serializer can still detect cross-document references and build the
     * correct reference URI.
     * </p>
     *
     * @param ctxt the serialization context
     * @return the EMF Resource, or null if not set
     */
    public static Resource getResource(SerializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(RESOURCE);
        return value instanceof Resource ? (Resource) value : null;
    }

    /**
     * Sets the EMF Resource in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param resource the EMF Resource
     */
    public static void setResource(DeserializationContext ctxt, Resource resource) {
        if (ctxt != null) {
            ctxt.setAttribute(RESOURCE, resource);
        }
    }

    /**
     * Sets the EMF Resource in the serialization context.
     *
     * @param ctxt the serialization context
     * @param resource the EMF Resource
     */
    public static void setResource(SerializationContext ctxt, Resource resource) {
        if (ctxt != null) {
            ctxt.setAttribute(RESOURCE, resource);
        }
    }

    // ========================================================================
    // Diagnostic Collector Methods
    // ========================================================================

    /**
     * Gets the diagnostic collector from the deserialization context.
     *
     * @param ctxt the deserialization context, may be null where no context is in play
     * @return the diagnostic collector, or null if not set
     */
    public static DiagnosticCollector getDiagnosticCollector(DeserializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(DIAGNOSTIC_COLLECTOR);
        return value instanceof DiagnosticCollector ? (DiagnosticCollector) value : null;
    }

    /**
     * Gets the diagnostic collector from the serialization context.
     *
     * @param ctxt the serialization context
     * @return the diagnostic collector, or null if not set
     */
    public static DiagnosticCollector getDiagnosticCollector(SerializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(DIAGNOSTIC_COLLECTOR);
        return value instanceof DiagnosticCollector ? (DiagnosticCollector) value : null;
    }

    /**
     * Sets the diagnostic collector in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param collector the diagnostic collector
     */
    public static void setDiagnosticCollector(DeserializationContext ctxt, DiagnosticCollector collector) {
        ctxt.setAttribute(DIAGNOSTIC_COLLECTOR, collector);
    }

    /**
     * Sets the diagnostic collector in the serialization context.
     *
     * @param ctxt the serialization context
     * @param collector the diagnostic collector
     */
    public static void setDiagnosticCollector(SerializationContext ctxt, DiagnosticCollector collector) {
        ctxt.setAttribute(DIAGNOSTIC_COLLECTOR, collector);
    }

    /**
     * Adds a warning diagnostic to the deserialization context.
     * <p>
     * If context is null or no diagnostic collector is set, this method does nothing.
     * This allows safe usage in unit tests where context may not be available.
     * </p>
     *
     * @param ctxt the deserialization context (may be null)
     * @param message the warning message
     * @param parser the JSON parser for location info (may be null)
     * @param source the source component name (e.g., "TypeDeserializationEntry")
     */
    public static void addWarning(DeserializationContext ctxt, String message, JsonParser parser, String source) {
        if (ctxt == null) {
            return;
        }
        DiagnosticCollector collector = getDiagnosticCollector(ctxt);
        if (collector != null) {
            collector.addWarning(message, parser != null ? parser.currentLocation() : null, source);
        }
    }

    /**
     * Adds an error diagnostic to the deserialization context.
     * <p>
     * If context is null or no diagnostic collector is set, this method does nothing.
     * This allows safe usage in unit tests where context may not be available.
     * </p>
     *
     * @param ctxt the deserialization context (may be null)
     * @param message the error message
     * @param parser the JSON parser for location info (may be null)
     * @param source the source component name (e.g., "TypeDeserializationEntry")
     */
    public static void addError(DeserializationContext ctxt, String message, JsonParser parser, String source) {
        if (ctxt == null) {
            return;
        }
        DiagnosticCollector collector = getDiagnosticCollector(ctxt);
        if (collector != null) {
            collector.addError(message, parser != null ? parser.currentLocation() : null, source);
        }
    }

    /**
     * Adds a warning diagnostic to the serialization context.
     * <p>
     * If context is null or no diagnostic collector is set, this method does nothing.
     * </p>
     *
     * @param ctxt the serialization context (may be null)
     * @param message the warning message
     * @param source the source component name (e.g., "TypeSerializationEntry")
     */
    public static void addWarning(SerializationContext ctxt, String message, String source) {
        if (ctxt == null) {
            return;
        }
        DiagnosticCollector collector = getDiagnosticCollector(ctxt);
        if (collector != null) {
            collector.addWarning(message, source);
        }
    }

    /**
     * Adds an error diagnostic to the serialization context.
     * <p>
     * If context is null or no diagnostic collector is set, this method does nothing.
     * </p>
     *
     * @param ctxt the serialization context (may be null)
     * @param message the error message
     * @param source the source component name (e.g., "TypeSerializationEntry")
     */
    public static void addError(SerializationContext ctxt, String message, String source) {
        if (ctxt == null) {
            return;
        }
        DiagnosticCollector collector = getDiagnosticCollector(ctxt);
        if (collector != null) {
            collector.addError(message, source);
        }
    }

    /**
     * Adds a warning diagnostic without location information.
     * <p>
     * Convenience method for cases where parser location is not available.
     * </p>
     *
     * @param ctxt the deserialization context (may be null)
     * @param message the warning message
     * @param source the source component name
     */
    public static void addWarning(DeserializationContext ctxt, String message, String source) {
        addWarning(ctxt, message, (JsonParser) null, source);
    }

    /**
     * Adds an error diagnostic without location information.
     * <p>
     * Convenience method for cases where parser location is not available.
     * </p>
     *
     * @param ctxt the deserialization context (may be null)
     * @param message the error message
     * @param source the source component name
     */
    public static void addError(DeserializationContext ctxt, String message, String source) {
        addError(ctxt, message, (JsonParser) null, source);
    }

    // ========================================================================
    // Feature Type Hints and Value Readers/Writers Methods
    // ========================================================================

    /**
     * Gets the feature type hints map from the deserialization context.
     *
     * @param ctxt the deserialization context
     * @return the feature type hints map, or null if not set
     */
    @SuppressWarnings("unchecked")
    public static Map<EStructuralFeature, EClass> getFeatureTypeHints(DeserializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(FEATURE_TYPE_HINTS);
        return value instanceof Map ? (Map<EStructuralFeature, EClass>) value : null;
    }

    /**
     * Gets the feature value readers map from the deserialization context.
     *
     * @param ctxt the deserialization context
     * @return the feature value readers map, or null if not set
     */
    @SuppressWarnings("unchecked")
    public static Map<EStructuralFeature, String> getFeatureValueReaders(DeserializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(FEATURE_VALUE_READERS);
        return value instanceof Map ? (Map<EStructuralFeature, String>) value : null;
    }

    /**
     * Gets the feature value writers map from the serialization context.
     *
     * @param ctxt the serialization context
     * @return the feature value writers map, or null if not set
     */
    @SuppressWarnings("unchecked")
    public static Map<EStructuralFeature, String> getFeatureValueWriters(SerializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(FEATURE_VALUE_WRITERS);
        return value instanceof Map ? (Map<EStructuralFeature, String>) value : null;
    }

    /**
     * Sets the feature type hints map in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param hints the feature type hints map
     */
    public static void setFeatureTypeHints(DeserializationContext ctxt, Map<EStructuralFeature, EClass> hints) {
        if (ctxt != null) {
            ctxt.setAttribute(FEATURE_TYPE_HINTS, hints);
        }
    }

    /**
     * Sets the feature value readers map in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param readers the feature value readers map
     */
    public static void setFeatureValueReaders(DeserializationContext ctxt, Map<EStructuralFeature, String> readers) {
        if (ctxt != null) {
            ctxt.setAttribute(FEATURE_VALUE_READERS, readers);
        }
    }

    /**
     * Sets the feature value writers map in the serialization context.
     *
     * @param ctxt the serialization context
     * @param writers the feature value writers map
     */
    public static void setFeatureValueWriters(SerializationContext ctxt, Map<EStructuralFeature, String> writers) {
        if (ctxt != null) {
            ctxt.setAttribute(FEATURE_VALUE_WRITERS, writers);
        }
    }

    /**
     * Gets the type hint for a specific feature from the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param feature the feature to get the hint for
     * @return the EClass type hint, or null if no hint is available
     */
    public static EClass getFeatureTypeHint(DeserializationContext ctxt, EStructuralFeature feature) {
        Map<EStructuralFeature, EClass> hints = getFeatureTypeHints(ctxt);
        return hints != null ? hints.get(feature) : null;
    }

    /**
     * Gets the value reader name for a specific feature from the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param feature the feature to get the reader for
     * @return the value reader name, or null if no reader is configured
     */
    public static String getFeatureValueReader(DeserializationContext ctxt, EStructuralFeature feature) {
        Map<EStructuralFeature, String> readers = getFeatureValueReaders(ctxt);
        return readers != null ? readers.get(feature) : null;
    }

    /**
     * Gets the value writer name for a specific feature from the serialization context.
     *
     * @param ctxt the serialization context
     * @param feature the feature to get the writer for
     * @return the value writer name, or null if no writer is configured
     */
    public static String getFeatureValueWriter(SerializationContext ctxt, EStructuralFeature feature) {
        Map<EStructuralFeature, String> writers = getFeatureValueWriters(ctxt);
        return writers != null ? writers.get(feature) : null;
    }

    /**
     * Gets the current feature's type hint from the deserialization context.
     * <p>
     * This is set temporarily during deserialization of a feature and can be
     * accessed by ValueReaders to use the hint when available.
     * </p>
     *
     * @param ctxt the deserialization context
     * @return the current feature's type hint, or null if not set
     */
    public static EClass getCurrentFeatureTypeHint(DeserializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(FEATURE_TYPE_HINT);
        return value instanceof EClass ? (EClass) value : null;
    }

    /**
     * Sets the current feature's type hint in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param eClass the type hint EClass
     */
    public static void setCurrentFeatureTypeHint(DeserializationContext ctxt, EClass eClass) {
        if (ctxt != null) {
            ctxt.setAttribute(FEATURE_TYPE_HINT, eClass);
        }
    }

    /**
     * Clears the current feature's type hint from the deserialization context.
     *
     * @param ctxt the deserialization context
     */
    public static void clearCurrentFeatureTypeHint(DeserializationContext ctxt) {
        if (ctxt != null) {
            ctxt.setAttribute(FEATURE_TYPE_HINT, null);
        }
    }

    // ========================================================================
    // Deserialization Mode Methods
    // ========================================================================

    /**
     * Gets the deserialization mode from the deserialization context.
     * <p>
     * Returns the mode as a String to avoid direct dependency on the generated
     * DeserializationMode enum in the context helper.
     * </p>
     *
     * @param ctxt the deserialization context
     * @return the mode string ("LENIENT", "STRICT", or "AUTO_DETECT"), defaults to "LENIENT"
     */
    public static String getDeserializationMode(DeserializationContext ctxt) {
        if (ctxt == null) {
            return "LENIENT";
        }
        Object value = ctxt.getAttribute(DESERIALIZATION_MODE);
        if (value instanceof String mode) {
            return mode;
        }
        // Handle DeserializationMode enum directly if set
        if (value != null) {
            return value.toString();
        }
        return "LENIENT";
    }

    /**
     * Checks if the deserializer is in STRICT mode.
     *
     * @param ctxt the deserialization context
     * @return true if STRICT mode is enabled
     */
    public static boolean isStrictMode(DeserializationContext ctxt) {
        return "STRICT".equals(getDeserializationMode(ctxt));
    }

    /**
     * Checks if the deserializer is in LENIENT mode (the default).
     *
     * @param ctxt the deserialization context
     * @return true if LENIENT mode is enabled (or no mode is set)
     */
    public static boolean isLenientMode(DeserializationContext ctxt) {
        return "LENIENT".equals(getDeserializationMode(ctxt));
    }

    /**
     * Checks if the deserializer is in AUTO_DETECT mode.
     *
     * @param ctxt the deserialization context
     * @return true if AUTO_DETECT mode is enabled
     */
    public static boolean isAutoDetectMode(DeserializationContext ctxt) {
        return "AUTO_DETECT".equals(getDeserializationMode(ctxt));
    }

    /**
     * Sets the deserialization mode in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param mode the mode string ("LENIENT", "STRICT", or "AUTO_DETECT")
     */
    public static void setDeserializationMode(DeserializationContext ctxt, String mode) {
        if (ctxt != null && mode != null) {
            ctxt.setAttribute(DESERIALIZATION_MODE, mode);
        }
    }

    /**
     * Returns the type hint mode (issue #173).
     * <p>
     * Reported as a String for the same reason as {@link #getDeserializationMode}: the context
     * helper stays free of the generated {@code TypeHintMode} enum, and a caller may pass
     * either the enum or its name.
     * </p>
     *
     * @param ctxt the deserialization context
     * @return "HINT" or "OVERRIDE", defaulting to "HINT"
     */
    public static String getTypeHintMode(DeserializationContext ctxt) {
        if (ctxt == null) {
            return "HINT";
        }
        Object value = ctxt.getAttribute(TYPE_HINT_MODE);
        if (value == null) {
            return "HINT";
        }
        return value.toString();
    }

    /**
     * Tells whether the caller declared {@code CODEC_ROOT_TYPE} a directive rather than a hint.
     *
     * @param ctxt the deserialization context
     * @return true if the type hint mode is OVERRIDE
     */
    public static boolean isTypeHintOverride(DeserializationContext ctxt) {
        return "OVERRIDE".equals(getTypeHintMode(ctxt));
    }

    /**
     * Sets the type hint mode in the deserialization context.
     *
     * @param ctxt the deserialization context
     * @param mode the mode ("HINT" or "OVERRIDE")
     */
    public static void setTypeHintMode(DeserializationContext ctxt, String mode) {
        if (ctxt != null && mode != null) {
            ctxt.setAttribute(TYPE_HINT_MODE, mode);
        }
    }

    /**
     * Returns the EReference whose value is currently being read, or {@code null} at a root.
     *
     * @param ctxt the deserialization context
     * @return the current reference, or null
     */
    public static EReference getCurrentDeserializationReference(DeserializationContext ctxt) {
        if (ctxt == null) {
            return null;
        }
        Object value = ctxt.getAttribute(CURRENT_DESERIALIZATION_REFERENCE);
        return value instanceof EReference reference ? reference : null;
    }

    /**
     * Sets the EReference whose value is being read, returning the one it replaces.
     * <p>
     * The previous value is returned rather than cleared so the caller can restore it: a
     * containment inside a containment must not leave its parent's reference lost for the
     * properties that follow it.
     * </p>
     *
     * @param ctxt the deserialization context
     * @param reference the reference now being read, may be null to clear
     * @return the reference that was set before, to be restored by the caller
     */
    public static EReference setCurrentDeserializationReference(DeserializationContext ctxt,
            EReference reference) {
        if (ctxt == null) {
            return null;
        }
        EReference previous = getCurrentDeserializationReference(ctxt);
        ctxt.setAttribute(CURRENT_DESERIALIZATION_REFERENCE, reference);
        return previous;
    }

    /**
     * Records that deserialization of an EObject has begun and returns its nesting depth.
     * <p>
     * Must be paired with {@link #exitEObject} in a {@code finally} block. A return value of
     * {@code 1} identifies a top-level value of the document: the root object, or one element
     * of a root array - a contained object is always read while its container's own call is
     * still on the stack.
     * </p>
     *
     * @param ctxt the deserialization context
     * @return the depth of the object now being read, starting at 1
     */
    public static int enterEObject(DeserializationContext ctxt) {
        if (ctxt == null) {
            return 1;
        }
        int depth = currentEObjectDepth(ctxt) + 1;
        ctxt.setAttribute(EOBJECT_DEPTH, depth);
        return depth;
    }

    /**
     * Records that deserialization of an EObject has finished.
     *
     * @param ctxt the deserialization context
     */
    public static void exitEObject(DeserializationContext ctxt) {
        if (ctxt == null) {
            return;
        }
        int depth = currentEObjectDepth(ctxt);
        ctxt.setAttribute(EOBJECT_DEPTH, depth > 0 ? depth - 1 : 0);
    }

    /**
     * Tells whether the EObject currently being read is a top-level value of the document.
     * <p>
     * Valid only between {@link #enterEObject} and {@link #exitEObject} for that object.
     * </p>
     *
     * @param ctxt the deserialization context
     * @return true for the root object or an element of a root array
     */
    public static boolean isRootEObject(DeserializationContext ctxt) {
        return ctxt != null && currentEObjectDepth(ctxt) == 1;
    }

    private static int currentEObjectDepth(DeserializationContext ctxt) {
        Object value = ctxt.getAttribute(EOBJECT_DEPTH);
        return value instanceof Integer depth ? depth : 0;
    }
}
