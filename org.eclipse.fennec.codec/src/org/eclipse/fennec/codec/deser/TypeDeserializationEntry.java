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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorReader;
import org.eclipse.fennec.codec.util.PackageResolver;
import org.eclipse.fennec.codec.util.TypeResolutionHelper;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.codec.util.TokenLoops;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;

/**
 * Deserialization entry that handles type information (_type property).
 * <p>
 * Supports multiple type formats:
 * <ul>
 *   <li>PLAIN string: {@code "_type": "http://example.org/1.0#//Person"}</li>
 *   <li>STRUCTURED object: {@code "_type": {"schema": "...", "name": "..."}}</li>
 * </ul>
 * </p>
 * <p>
 * The resolved EClass is stored in the {@link DeserializationState} for use
 * by the deserializer when creating the EObject.
 * </p>
 *
 * @see TypeConfig
 * @see <a href="docs/codec-v2-serialization-spec.md#15-deserialization-requirements">Spec 15: Deserialization</a>
 * @author Mark Hoffmann
 * @since 1.0
 */
public class TypeDeserializationEntry implements DeserializationEntry {

    private static final Logger LOGGER = Logger.getLogger(TypeDeserializationEntry.class.getName());

    private final TypeConfig config;
    private final TypeDiscriminatorReader typeDiscriminatorService;
    private final SuperTypeConfig superTypeConfig;
    private final String discriminatorMapId;

    /**
     * Creates a new TypeDeserializationEntry.
     *
     * @param config the effective type configuration
     */
    public TypeDeserializationEntry(TypeConfig config) {
        this(config, null, null, null);
    }

    /**
     * Creates a new TypeDeserializationEntry with a TypeDiscriminatorReader.
     *
     * @param config the effective type configuration
     * @param typeDiscriminatorService the service for MAPPED strategy type resolution (may be null)
     */
    public TypeDeserializationEntry(TypeConfig config, TypeDiscriminatorReader typeDiscriminatorService) {
        this(config, typeDiscriminatorService, null, null);
    }

    /**
     * Creates a new TypeDeserializationEntry with SuperType configuration for STRUCTURED format.
     *
     * @param config the effective type configuration
     * @param typeDiscriminatorService the service for MAPPED strategy type resolution (may be null)
     * @param superTypeConfig the supertype configuration for validation (may be null)
     */
    public TypeDeserializationEntry(TypeConfig config, TypeDiscriminatorReader typeDiscriminatorService,
            SuperTypeConfig superTypeConfig) {
        this(config, typeDiscriminatorService, superTypeConfig, null);
    }

    /**
     * Creates a new TypeDeserializationEntry with targeted discriminator registry.
     * <p>
     * When {@code discriminatorMapId} is provided, discriminator resolution uses
     * {@link TypeDiscriminatorReader#resolve(String, String, java.util.function.Function)}
     * targeting the specific registry. This ensures the correct fallback strategy
     * (ERROR, SKIP, FALLBACK) is applied per the registry configuration.
     * </p>
     * <p>
     * See spec 08-discriminator-mapping.md §7.1 for the deserialization flow.
     * </p>
     *
     * @param config the effective type configuration
     * @param typeDiscriminatorService the service for discriminator type resolution (may be null)
     * @param superTypeConfig the supertype configuration for validation (may be null)
     * @param discriminatorMapId the targeted registry mapId (may be null for untargeted resolution)
     */
    public TypeDeserializationEntry(TypeConfig config, TypeDiscriminatorReader typeDiscriminatorService,
            SuperTypeConfig superTypeConfig, String discriminatorMapId) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.typeDiscriminatorService = typeDiscriminatorService;
        this.superTypeConfig = superTypeConfig;
        this.discriminatorMapId = discriminatorMapId;
    }

    @Override
    public String getKey() {
        return config.getTypeKey();
    }

    @Override
    public void deserialize(DeserializationState state, JsonParser parser, DeserializationContext ctxt) {
        deserializeWithHint(state, parser, ctxt, null);
    }

    /**
     * Deserializes type information with an optional hint EClass for context.
     * <p>
     * The hint EClass is used to provide context for MAPPED type resolution.
     * If a hint is provided and has a mapId, that mapId is used to narrow
     * the discriminator lookup.
     * </p>
     *
     * @param state the deserialization state
     * @param parser the JSON parser
     * @param ctxt the deserialization context
     * @param hintEClass optional hint EClass for MAPPED context (may be null)
     */
    public void deserializeWithHint(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EClass hintEClass) {
        deserializeWithSchemaHint(state, parser, ctxt, hintEClass, null);
    }

    /**
     * Deserializes type information with optional hint EClass and schema value.
     * <p>
     * The hint EClass is used to provide context for MAPPED type resolution.
     * The schemaValue is used for PLAIN SCHEMA_AND_TYPE format where schema
     * is provided as a separate field.
     * </p>
     * <p>
     * Supports smart compression: when encountering a simple name (e.g., "Person"),
     * the method first tries to resolve it using the context schema (if set).
     * The context schema is established from the root object's full type URI.
     * </p>
     *
     * @param state the deserialization state
     * @param parser the JSON parser
     * @param ctxt the deserialization context
     * @param hintEClass optional hint EClass for MAPPED context (may be null)
     * @param schemaValue optional schema value for PLAIN SCHEMA_AND_TYPE (may be null)
     */
    public void deserializeWithSchemaHint(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EClass hintEClass, String schemaValue) {
        JsonToken token = parser.currentToken();

        String typeValue = null;

        if (token == JsonToken.VALUE_STRING) {
            // PLAIN format: "_type": "http://example.org/1.0#//Person" or "_type": "Person"
            String rawTypeValue = parser.getString();

            // If we have a schema value (PLAIN SCHEMA_AND_TYPE), combine them
            if (schemaValue != null && !schemaValue.isEmpty() && !rawTypeValue.contains("#//")) {
                // Schema + simple name -> compose URI
                typeValue = schemaValue + "#//" + rawTypeValue;
            } else {
                typeValue = rawTypeValue;
            }
        } else if (token == JsonToken.START_OBJECT) {
            // STRUCTURED format: "_type": {"schema": "...", "type": "...", "supertype": [...]}
            StructuredTypeResult result = parseStructuredType(parser);
            typeValue = result.typeValue;

            // Store parsed supertypes for validation after EClass is resolved
            if (result.superTypes != null && !result.superTypes.isEmpty()) {
                // We'll validate after resolving the EClass
                if (typeValue != null) {
                    EClass resolvedClass = resolveEClass(typeValue, hintEClass, ctxt);
                    if (resolvedClass != null) {
                        state.setResolvedEClass(resolvedClass);
                        // Validate supertype hierarchy if enabled
                        validateSuperTypes(resolvedClass, result.superTypes, ctxt);
                    } else {
                        String msg = "Could not resolve EClass from type value: " + typeValue;
                        handleTypeResolutionFailure(ctxt, parser, msg);
                    }
                }
                return;
            }
        } else {
            String msg = "Unexpected token for _type: " + token;
            handleTypeResolutionFailure(ctxt, parser, msg);
            return;
        }

        if (typeValue != null) {
            EClass resolvedClass = resolveEClass(typeValue, hintEClass, ctxt);
            if (resolvedClass != null) {
                state.setResolvedEClass(resolvedClass);
            } else {
                String msg = "Could not resolve EClass from type value: " + typeValue;
                handleTypeResolutionFailure(ctxt, parser, msg);
            }
        }
    }

    /**
     * Handles type resolution failures based on DeserializationMode.
     * <p>
     * In STRICT mode, adds an ERROR diagnostic.
     * In LENIENT/AUTO_DETECT mode, adds a WARNING diagnostic.
     * </p>
     *
     * @param ctxt the deserialization context
     * @param parser the JSON parser for location info
     * @param message the error/warning message
     */
    private void handleTypeResolutionFailure(DeserializationContext ctxt, JsonParser parser, String message) {
        if (ContextHelper.isStrictMode(ctxt)) {
            LOGGER.severe(message);
            ContextHelper.addError(ctxt, message, parser, "TypeDeserializationEntry");
        } else {
            LOGGER.warning(message);
            ContextHelper.addWarning(ctxt, message, parser, "TypeDeserializationEntry");
        }
    }

    /**
     * Validates supertype hierarchy if validation is enabled.
     * <p>
     * Validation is triggered when {@code superTypeConfig} is provided.
     * The DeserializationMode controls error handling:
     * <ul>
     *   <li>STRICT: Validation failure throws exception (breaks deserialization)</li>
     *   <li>LENIENT: Validation failure logs warning (continues deserialization)</li>
     * </ul>
     * </p>
     *
     * @param resolvedEClass the resolved EClass
     * @param declaredSuperTypes the supertypes declared in JSON
     * @param ctxt the deserialization context for mode checking
     */
    private void validateSuperTypes(EClass resolvedEClass, List<String> declaredSuperTypes,
            DeserializationContext ctxt) {
        if (superTypeConfig == null) {
            LOGGER.fine("SuperType validation disabled (no superTypeConfig)");
            return;
        }

        if (declaredSuperTypes.isEmpty()) {
            return;
        }

        // Delegate to SuperTypeDeserializationEntry's validation logic
        // This will throw SuperTypeValidationException if validation fails
        SuperTypeDeserializationEntry.validateSuperTypeHierarchyStatic(
                resolvedEClass, declaredSuperTypes, superTypeConfig);
    }

    /**
     * Result holder for parsed structured type information.
     */
    private static class StructuredTypeResult {
        String typeValue;
        List<String> superTypes = new ArrayList<>();
    }

    /**
     * Parses a structured type object.
     * <p>
     * Supports multiple formats based on strategy (all use unified "type" key except NUMERIC):
     * <ul>
     *   <li>URI: {@code {"type": "http://example.org/1.0#//Person"}}</li>
     *   <li>NAME: {@code {"type": "Person"}}</li>
     *   <li>CLASS: {@code {"type": "org.example.Person"}}</li>
     *   <li>MAPPED: {@code {"type": "customer"}}</li>
     *   <li>NUMERIC: {@code {"schema": "http://...", "classifier": 3}}</li>
     *   <li>SCHEMA_AND_TYPE: {@code {"schema": "http://...", "type": "Person"}}</li>
     * </ul>
     * Also extracts optional supertype field if present:
     * <ul>
     *   <li>Array: {@code "supertype": ["Entity", "http://audit.org/1.0#//Auditable"]}</li>
     *   <li>String: {@code "supertype": "Entity,Auditable"}</li>
     * </ul>
     * </p>
     *
     * @param parser the JSON parser positioned at START_OBJECT
     * @return the structured type result containing type value and optional supertypes
     */
    private StructuredTypeResult parseStructuredType(JsonParser parser) {
        StructuredTypeResult result = new StructuredTypeResult();
        String schema = null;
        String typeValue = null;
        Integer classifier = null;

        while (TokenLoops.hasNextField(parser)) {
            String fieldName = parser.currentName();
            parser.nextToken(); // Move to value

            if (config.getSchemaKey().equals(fieldName)) {
                schema = parser.getString();
            } else if (config.getNameKey().equals(fieldName)) {
                // "type" key - contains the type value (URI, name, class, discriminator)
                typeValue = parser.getString();
            } else if ("classifier".equals(fieldName)) {
                // NUMERIC strategy: classifier ID
                classifier = parser.getIntValue();
            } else if (getSuperTypeKey().equals(fieldName)) {
                // Parse supertype (ARRAY or STRING presentation)
                result.superTypes = parseSuperTypesFromStructured(parser);
            }
        }

        // NUMERIC strategy: schema + classifier
        if (classifier != null && schema != null) {
            result.typeValue = buildNumericTypeValue(schema, classifier);
            return result;
        }

        // SCHEMA_AND_TYPE: schema + type name -> compose URI
        if (schema != null && typeValue != null && !typeValue.contains("#//")) {
            result.typeValue = schema + "#//" + typeValue;
            return result;
        }

        // URI, NAME, CLASS, MAPPED: just return the type value
        if (typeValue != null) {
            result.typeValue = typeValue;
            return result;
        }

        LOGGER.warning("Could not parse structured type: schema=" + schema + ", type=" + typeValue + ", classifier=" + classifier);
        return result;
    }

    /**
     * Gets the supertype key for STRUCTURED format.
     * <p>
     * In STRUCTURED format, the key is "supertype" (without underscore prefix)
     * since it's nested inside the _type object.
     * </p>
     *
     * @return the supertype key for STRUCTURED format
     */
    private String getSuperTypeKey() {
        // In STRUCTURED format, use "supertype" (not "_supertype")
        // as it's inside the _type object
        return superTypeConfig != null ? superTypeConfig.getSuperTypeKey().replace("_", "") : "supertype";
    }

    /**
     * Parses supertype values from within a structured type object.
     * <p>
     * Handles both ARRAY and STRING presentation:
     * <ul>
     *   <li>ARRAY: {@code ["Entity", "http://audit.org/1.0#//Auditable"]}</li>
     *   <li>STRING: {@code "Entity,http://audit.org/1.0#//Auditable"}</li>
     * </ul>
     * </p>
     *
     * @param parser the JSON parser positioned at the supertype value
     * @return list of declared supertype values
     */
    private List<String> parseSuperTypesFromStructured(JsonParser parser) {
        List<String> superTypes = new ArrayList<>();
        JsonToken token = parser.currentToken();

        if (token == JsonToken.START_ARRAY) {
            // ARRAY presentation
            JsonToken arrayToken;
            while ((arrayToken = parser.nextToken()) != null && arrayToken != JsonToken.END_ARRAY) {
                if (arrayToken == JsonToken.VALUE_STRING) {
                    superTypes.add(parser.getString());
                }
            }
        } else if (token == JsonToken.VALUE_STRING) {
            // STRING presentation - split by separator
            String value = parser.getString();
            if (value != null && !value.isEmpty()) {
                String separator = superTypeConfig != null ? superTypeConfig.getSeparator() : ",";
                for (String part : value.split(java.util.regex.Pattern.quote(separator))) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        superTypes.add(trimmed);
                    }
                }
            }
        }

        return superTypes;
    }

    /**
     * Builds a type value for NUMERIC strategy (schema + classifier).
     * <p>
     * Looks up the EPackage and finds the EClass by classifier ID.
     * Returns the composed URI if found.
     * </p>
     *
     * @param schema the EPackage nsURI
     * @param classifier the classifier ID
     * @return the composed URI string, or the classifier as string if not found
     */
    private String buildNumericTypeValue(String schema, int classifier) {
        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(schema);
        if (ePackage != null) {
            EClass resolved = TypeResolutionHelper.findClassifierInPackage(ePackage, classifier);
            if (resolved != null) {
                return schema + "#//" + resolved.getName();
            }
        }
        LOGGER.warning("Could not find EClass for schema=" + schema + ", classifier=" + classifier);
        return String.valueOf(classifier);
    }

    /**
     * Resolves an EClass from a type value based on the configured strategy.
     * <p>
     * Delegates to {@link #resolveEClass(String, EClass, DeserializationContext, EReference)}
     * with no reference context.
     * </p>
     *
     * @param typeValue the type value (format depends on strategy)
     * @param hintEClass optional hint EClass for MAPPED context (may be null)
     * @param ctxt the deserialization context (for smart compression context schema)
     * @return the resolved EClass, or null if not found
     */
    public EClass resolveEClass(String typeValue, EClass hintEClass, DeserializationContext ctxt) {
        return resolveEClass(typeValue, hintEClass, ctxt, null);
    }

    /**
     * Resolves an EClass from a type value based on the configured strategy.
     * <p>
     * Supports formats based on strategy:
     * <ul>
     *   <li>URI: {@code http://example.org/1.0#//Person} - Full URI</li>
     *   <li>NAME: {@code Person} - Simple class name</li>
     *   <li>CLASS: {@code com.example.Person} - Java class name</li>
     *   <li>NUMERIC: {@code 1} - Classifier ID</li>
     *   <li>MAPPED: Uses discriminator value mapping</li>
     * </ul>
     * </p>
     * <p>
     * Supports smart compression: when encountering a full URI, the context schema
     * is established (if not already set). When encountering a simple name, it is
     * first resolved using the context schema before falling back to searching all packages.
     * </p>
     * <p>
     * When a {@code currentReference} is provided, inline mapping resolution is attempted
     * first via {@link TypeDiscriminatorReader#resolveForReference} before falling back to
     * the global discriminator registries.
     * </p>
     *
     * @param typeValue the type value (format depends on strategy)
     * @param hintEClass optional hint EClass for MAPPED context (may be null)
     * @param ctxt the deserialization context (for smart compression context schema)
     * @param currentReference optional EReference for inline mapping context (may be null)
     * @return the resolved EClass, or null if not found
     */
    public EClass resolveEClass(String typeValue, EClass hintEClass, DeserializationContext ctxt,
            EReference currentReference) {
        return resolveEClass(typeValue, hintEClass, ctxt, currentReference, null);
    }

    /**
     * Resolves an EClass from a type value, using an in-band EPackage fingerprint read from
     * the document to select the package version (issue #73, B.1).
     * <p>
     * The fingerprint short-circuits version selection: instead of asking how many versions
     * are registered for the nsURI - which is ambiguous by definition when more than one is -
     * the resolver goes straight to the named version and pins it for the rest of the load.
     * A {@code null} fingerprint leaves resolution exactly as it was before this feature.
     * </p>
     *
     * @param typeValue the type value (format depends on strategy)
     * @param hintEClass optional hint EClass for MAPPED context (may be null)
     * @param ctxt the deserialization context
     * @param currentReference optional EReference for inline mapping context (may be null)
     * @param streamFingerprint the fingerprint carried by the document, or {@code null}
     * @return the resolved EClass, or null if not found
     */
    public EClass resolveEClass(String typeValue, EClass hintEClass, DeserializationContext ctxt,
            EReference currentReference, String streamFingerprint) {
        if (typeValue == null || typeValue.isEmpty()) {
            return null;
        }

        // Per-load package resolver (B.5): binding version-resolution order + A.3 count rule.
        PackageResolver packageResolver = ContextHelper.getPackageResolver(ctxt);

        // B.2: apply a fingerprint carried by the document before resolving, so version
        // selection happens by name instead of by counting candidates. An unresolvable or
        // overruled fingerprint may reduce this back to null - deliberately, and loudly.
        String effectiveFingerprint = applyStreamFingerprint(
                packageResolver, typeValue, streamFingerprint, ctxt);

        // First: check if it's a full URI (always highest priority)
        if (typeValue.contains("#//")) {
            EClass resolved = resolveUriVia(packageResolver, typeValue, effectiveFingerprint,
                    ContextHelper.getDiagnosticCollector(ctxt));
            if (resolved != null) {
                // Establish context schema for smart compression (root object)
                initializeContextSchemaIfNeeded(typeValue, ctxt);
                return resolved;
            }
        }

        // Second: try smart compression - resolve simple name using context schema
        if (!typeValue.contains("#//") && ctxt != null) {
            String contextSchema = ContextHelper.getContextSchemaUri(ctxt);
            if (contextSchema != null) {
                // Try to resolve using context schema first
                String composedUri = contextSchema + "#//" + typeValue;
                EClass resolved = resolveUriVia(packageResolver, composedUri, effectiveFingerprint,
                        ContextHelper.getDiagnosticCollector(ctxt));
                if (resolved != null) {
                    LOGGER.fine("Resolved type via smart compression: " + typeValue + " -> " + resolved.getName());
                    return resolved;
                }
            }
        }

        // Third: try inline mapping for reference-scoped discriminator resolution.
        // If we are deserializing a contained object under a specific EReference
        // that has an inlineMapping annotation, try to resolve using that reference's
        // dedicated registry first.
        if (typeDiscriminatorService != null && currentReference != null) {
            EClass resolved = typeDiscriminatorService.resolveForReference(
                    currentReference, typeValue, TypeResolutionHelper::resolveFromUri);
            if (resolved != null) {
                LOGGER.fine("Resolved type via inline mapping for reference '" +
                        currentReference.getName() + "': " + typeValue + " -> " + resolved.getName());
                return resolved;
            }
        }

        // Fourth: try discriminator lookup via TypeDiscriminatorReader.
        // When a discriminatorMapId is set, use targeted resolution via resolve(mapId, ...)
        // which applies the correct fallback strategy (ERROR/SKIP/FALLBACK) for that registry.
        // Otherwise fall back to resolveFromAny() which searches all registries.
        // See spec 08-discriminator-mapping.md §7.1 and §7.3.
        if (typeDiscriminatorService != null) {
            EClass resolved;
            if (discriminatorMapId != null) {
                resolved = typeDiscriminatorService.resolve(discriminatorMapId, typeValue, TypeResolutionHelper::resolveFromUri);
                if (resolved != null) {
                    LOGGER.fine("Resolved type via discriminator registry '" + discriminatorMapId +
                            "': " + typeValue + " -> " + resolved.getName());
                    return resolved;
                }
            } else {
                resolved = typeDiscriminatorService.resolveFromAny(typeValue, TypeResolutionHelper::resolveFromUri);
                if (resolved != null) {
                    LOGGER.fine("Resolved type via discriminator: " + typeValue + " -> " + resolved.getName());
                    return resolved;
                }
            }
        }

        // Fourth: handle based on configured strategy
        TypeStrategy strategy = config.getStrategy();
        if (strategy == null) {
            strategy = TypeStrategy.URI;
        }

        // Derive context package from context schema URI for scoped resolution (S-4).
        // B.5: resolve the version via the binding order (pin -> ResourceSet -> MetadataService
        // candidate query -> global registry), not directly from the global registry.
        EPackage contextPackage = null;
        if (ctxt != null) {
            String contextSchema = ContextHelper.getContextSchemaUri(ctxt);
            if (contextSchema != null && !contextSchema.isEmpty()) {
                contextPackage = resolvePackageVia(packageResolver, contextSchema);
            }
        }
        if (contextPackage == null && hintEClass != null) {
            contextPackage = hintEClass.getEPackage();
        }

        EClass resolved;
        switch (strategy) {
            case NAME:
                resolved = TypeResolutionHelper.resolveFromSimpleName(typeValue, contextPackage,
                        ContextHelper.getDiagnosticCollector(ctxt));
                break;
            case CLASS:
                resolved = TypeResolutionHelper.resolveFromClassName(typeValue, contextPackage,
                        ContextHelper.getDiagnosticCollector(ctxt));
                break;
            case NUMERIC:
                String numericContextSchema = ctxt != null ? ContextHelper.getContextSchemaUri(ctxt) : null;
                resolved = TypeResolutionHelper.resolveFromNumeric(typeValue, hintEClass, numericContextSchema,
                        ContextHelper.getDiagnosticCollector(ctxt));
                break;
            case SCHEMA_AND_TYPE:
                // TODO: Implement SCHEMA_AND_TYPE resolution
                resolved = TypeResolutionHelper.resolveFromSimpleName(typeValue, contextPackage,
                        ContextHelper.getDiagnosticCollector(ctxt));
                break;
            case URI:
            default:
                // Fallback to simple name resolution (scoped if context available)
                resolved = TypeResolutionHelper.resolveFromSimpleName(typeValue, contextPackage,
                        ContextHelper.getDiagnosticCollector(ctxt));
                break;
        }

        // A class-scoped strategy override is invisible here: the type is not resolved yet,
        // so only the global strategy is known. When that one fails and the value looks like
        // a classifier id, try NUMERIC before giving up (issue #116).
        if (resolved == null && strategy != TypeStrategy.NUMERIC && isClassifierId(typeValue)) {
            String numericContextSchema = ctxt != null ? ContextHelper.getContextSchemaUri(ctxt) : null;
            resolved = TypeResolutionHelper.resolveFromNumeric(typeValue, hintEClass, numericContextSchema,
                        ContextHelper.getDiagnosticCollector(ctxt));
        }

        // S-4: Add resource diagnostic when resolution fails due to missing context
        if (resolved == null && contextPackage == null && ctxt != null) {
            String msg = "Type resolution for '" + typeValue + "' failed: "
                    + strategy + " strategy requires a schema hint "
                    + "(CODEC_ROOT_SCHEMA or CODEC_ROOT_TYPE) to scope resolution";
            ContextHelper.addWarning(ctxt, msg, "TypeDeserializationEntry");
        }

        return resolved;
    }

    /** Tells whether a type value is a plain non-negative integer, i.e. a classifier id. */
    private static boolean isClassifierId(String typeValue) {
        if (typeValue == null || typeValue.isEmpty()) {
            return false;
        }
        for (int i = 0; i < typeValue.length(); i++) {
            if (!Character.isDigit(typeValue.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Applies a fingerprint read from the document and turns the outcome into the reaction the
     * strictness mode calls for (issue #73, B.2).
     * <p>
     * The full case matrix, all of it loud - an explicit signal is never silently degraded:
     * </p>
     * <table border="1">
     *   <caption>Stream fingerprint outcomes</caption>
     *   <tr><th>Situation</th><th>LENIENT</th><th>STRICT</th></tr>
     *   <tr><td>Resolves to a version</td><td colspan="2">Used and pinned, no diagnostic</td></tr>
     *   <tr><td>Equals the caller's fingerprint</td><td colspan="2">No-op, no diagnostic</td></tr>
     *   <tr><td>Differs from the caller's fingerprint</td>
     *       <td>Caller wins + WARNING naming both</td><td>ERROR</td></tr>
     *   <tr><td>Unknown while the caller pinned one</td>
     *       <td>Caller wins + WARNING</td><td>ERROR</td></tr>
     *   <tr><td>Unknown, no caller fingerprint</td>
     *       <td>WARNING + fall back to nsURI resolution</td><td>ERROR</td></tr>
     * </table>
     * <p>
     * Falling back to nsURI resolution in LENIENT is what keeps data readable across a
     * canonicalization-scheme bump, where an unchanged model's old fingerprint no longer
     * matches anything. The nsURI path then applies its own candidate rule: one candidate is
     * taken, several remain an error.
     * </p>
     *
     * @return the fingerprint to resolve with, or {@code null} to fall back to the nsURI path
     */
    private static String applyStreamFingerprint(PackageResolver resolver, String typeValue,
            String streamFingerprint, DeserializationContext ctxt) {
        if (resolver == null || streamFingerprint == null || streamFingerprint.isEmpty()) {
            return null;
        }
        String nsURI = nsUriOf(typeValue);
        if (nsURI == null) {
            // A bare name carries no nsURI to attach a version to; the fingerprint is applied
            // once the context schema composes a full URI.
            return streamFingerprint;
        }

        PackageResolver.StreamFingerprintResult result =
                resolver.applyStreamFingerprint(nsURI, streamFingerprint);

        switch (result.outcome()) {
            case RESOLVED, REDUNDANT:
                return streamFingerprint;

            case OVERRIDDEN_BY_CALLER: {
                String message = String.format(
                        "Fingerprint in the data (%s) contradicts the caller's fingerprint (%s) for "
                        + "nsURI %s; the caller's choice wins", streamFingerprint,
                        result.callerFingerprint(), nsURI);
                report(ctxt, message, result.firstReport());
                // Resolve against the caller's pin, not the document's claim.
                return null;
            }

            case UNKNOWN:
            default: {
                String message = String.format(
                        "Unknown fingerprint %s in the data for nsURI %s; falling back to nsURI "
                        + "resolution", streamFingerprint, nsURI);
                report(ctxt, message, result.firstReport());
                return null;
            }
        }
    }

    /**
     * Emits the diagnostic for a stream-fingerprint problem: an error in STRICT, a warning in
     * LENIENT. Repeat occurrences of the same fingerprint are suppressed so a single bad value
     * on thousands of objects does not bury every other diagnostic.
     */
    private static void report(DeserializationContext ctxt, String message, boolean firstReport) {
        if (ContextHelper.isStrictMode(ctxt)) {
            LOGGER.severe(message);
            ContextHelper.addError(ctxt, message, "TypeDeserializationEntry");
            throw new IllegalStateException(message);
        }
        if (firstReport) {
            LOGGER.warning(message);
            ContextHelper.addWarning(ctxt, message, "TypeDeserializationEntry");
        }
    }

    /** Extracts the nsURI part of a full type URI, or {@code null} for a bare name. */
    private static String nsUriOf(String typeValue) {
        if (typeValue == null) {
            return null;
        }
        int hash = typeValue.indexOf('#');
        return hash > 0 ? typeValue.substring(0, hash) : null;
    }

    /**
     * Resolves a full type URI via the per-load {@link PackageResolver} (B.5 binding order +
     * A.3 count rule) when available, else the legacy global-registry lookup. An ambiguous
     * nsURI (&gt; 1 candidate, no pin/fingerprint) surfaces as a hard error in every mode.
     */
    private static EClass resolveUriVia(PackageResolver resolver, String typeUri,
            String streamFingerprint, DiagnosticCollector diagnostics) {
        if (resolver == null) {
            return TypeResolutionHelper.resolveFromUri(typeUri, diagnostics);
        }
        try {
            return resolver.resolveEClassFromTypeUri(typeUri, streamFingerprint);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Resolves an nsURI to a concrete {@link EPackage} via the per-load {@link PackageResolver}
     * (B.5) when available, else the legacy global registry. Ambiguity is a hard error.
     */
    private static EPackage resolvePackageVia(PackageResolver resolver, String nsURI) {
        if (resolver == null) {
            return EPackage.Registry.INSTANCE.getEPackage(nsURI);
        }
        try {
            return resolver.resolveEPackage(nsURI, null);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Initializes the context schema for smart compression.
     * <p>
     * The context schema is extracted from the first full URI encountered (root object).
     * It is used to resolve simple names in subsequent (contained) objects.
     * </p>
     *
     * @param fullUri the full type URI (e.g., "http://example.org/1.0#//Company")
     * @param ctxt the deserialization context
     */
    private void initializeContextSchemaIfNeeded(String fullUri, DeserializationContext ctxt) {
        if (ctxt == null) {
            return;
        }

        // Only set context schema once (for root object)
        if (ContextHelper.getContextSchemaUri(ctxt) != null) {
            return;
        }

        // Extract schema from full URI
        String schemaUri = ContextHelper.extractSchemaUri(fullUri);
        if (schemaUri != null) {
            ContextHelper.setContextSchemaUri(ctxt, schemaUri);
            LOGGER.fine("Established context schema for smart compression: " + schemaUri);
        }
    }

}
