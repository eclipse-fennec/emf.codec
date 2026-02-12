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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.codec.config.SuperTypeConfig;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;

/**
 * Deserialization entry for EObject supertype information.
 * <p>
 * Handles parsing of supertype information from JSON and optional validation
 * against the resolved EClass's actual type hierarchy.
 * </p>
 * <p>
 * Supertype information is typically **not needed** for deserialization since
 * the concrete type ({@code _type}) fully determines the EClass. However,
 * when {@code validateSuperTypeHierarchy=true}, this entry validates that
 * declared supertypes match the actual hierarchy and fails if they don't.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-supertype.md#8-deserialization">Spec: SuperType Deserialization</a>
 * @author Mark Hoffmann
 * @since 2025-01-09
 */
public class SuperTypeDeserializationEntry implements DeserializationEntry {

    private static final Logger LOGGER = Logger.getLogger(SuperTypeDeserializationEntry.class.getName());

    private final SuperTypeConfig config;
    private final boolean validateSuperTypeHierarchy;

    /**
     * Creates a new SuperTypeDeserializationEntry.
     *
     * @param config the effective supertype configuration
     */
    public SuperTypeDeserializationEntry(SuperTypeConfig config) {
        this(config, false);
    }

    /**
     * Creates a new SuperTypeDeserializationEntry with validation control.
     *
     * @param config the effective supertype configuration
     * @param validateSuperTypeHierarchy whether to validate the supertype hierarchy
     */
    public SuperTypeDeserializationEntry(SuperTypeConfig config, boolean validateSuperTypeHierarchy) {
        this.config = config;
        this.validateSuperTypeHierarchy = validateSuperTypeHierarchy;
    }

    @Override
    public String getKey() {
        return config.getSuperTypeKey();
    }

    @Override
    public void deserialize(DeserializationState state, JsonParser parser, DeserializationContext ctxt) {
        // Parse declared supertypes from JSON
        List<String> declaredSuperTypes = parseSuperTypes(parser);

        // Validation is controlled by validateSuperTypeHierarchy flag (opt-in)
        // DeserializationMode (STRICT/LENIENT) controls error handling, not whether to validate
        if (!validateSuperTypeHierarchy) {
            LOGGER.fine("SuperType validation disabled, ignoring declared supertypes: " + declaredSuperTypes);
            return;
        }

        // Get the resolved EClass for validation
        EClass resolvedEClass = state.getResolvedEClass();
        if (resolvedEClass == null) {
            LOGGER.fine("Cannot validate supertypes: no resolved EClass");
            return;
        }

        // Validate the hierarchy - throws SuperTypeValidationException on mismatch
        validateSuperTypeHierarchy(resolvedEClass, declaredSuperTypes);
    }

    /**
     * Parses supertype values from the current parser position.
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
    private List<String> parseSuperTypes(JsonParser parser) {
        List<String> superTypes = new ArrayList<>();
        JsonToken token = parser.currentToken();

        if (token == JsonToken.START_ARRAY) {
            // ARRAY presentation
            JsonToken arrayToken;
            while ((arrayToken = parser.nextToken()) != JsonToken.END_ARRAY) {
                if (arrayToken == JsonToken.VALUE_STRING) {
                    superTypes.add(parser.getString());
                }
            }
        } else if (token == JsonToken.VALUE_STRING) {
            // STRING presentation - split by separator
            String value = parser.getString();
            if (value != null && !value.isEmpty()) {
                String separator = config.getSeparator();
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
     * Validates that declared supertypes match the actual EClass hierarchy.
     * <p>
     * Validation rules:
     * <ul>
     *   <li>Each declared supertype must exist in {@code getEAllSuperTypes()}</li>
     *   <li>Supertypes can be simple names (same namespace) or full URIs</li>
     *   <li>Order is not significant</li>
     *   <li>Missing supertypes (subset) is acceptable</li>
     *   <li>Extra supertypes (not in hierarchy) causes failure</li>
     * </ul>
     * </p>
     *
     * @param resolvedEClass the resolved EClass
     * @param declaredSuperTypes the supertypes declared in JSON
     * @throws SuperTypeValidationException if validation fails
     */
    private void validateSuperTypeHierarchy(EClass resolvedEClass, List<String> declaredSuperTypes) {
        validateSuperTypeHierarchyStatic(resolvedEClass, declaredSuperTypes, config);
    }

    /**
     * Static validation method for supertype hierarchy validation.
     * <p>
     * This static method is used by both PLAIN format (via instance method) and
     * STRUCTURED format (via {@link TypeDeserializationEntry}).
     * </p>
     *
     * @param resolvedEClass the resolved EClass
     * @param declaredSuperTypes the supertypes declared in JSON
     * @param config the supertype configuration (for logging context)
     * @throws SuperTypeValidationException if validation fails
     */
    public static void validateSuperTypeHierarchyStatic(EClass resolvedEClass, List<String> declaredSuperTypes,
            SuperTypeConfig config) {
        if (declaredSuperTypes == null || declaredSuperTypes.isEmpty()) {
            return;
        }

        // Build set of actual supertype identifiers (both simple names and full URIs)
        Set<String> actualSuperTypes = new HashSet<>();
        String rootNamespaceUri = resolvedEClass.getEPackage() != null
                ? resolvedEClass.getEPackage().getNsURI()
                : null;

        for (EClass superType : resolvedEClass.getEAllSuperTypes()) {
            // Add simple name
            actualSuperTypes.add(superType.getName());
            // Add full URI
            actualSuperTypes.add(getEClassUriStatic(superType));
            // Add namespace-relative name if different namespace
            String superTypeNsUri = superType.getEPackage() != null
                    ? superType.getEPackage().getNsURI()
                    : null;
            if (superTypeNsUri != null && !superTypeNsUri.equals(rootNamespaceUri)) {
                actualSuperTypes.add(superTypeNsUri + "#//" + superType.getName());
            }
        }

        // Check each declared supertype
        List<String> invalidSuperTypes = new ArrayList<>();
        for (String declared : declaredSuperTypes) {
            if (!actualSuperTypes.contains(declared)) {
                // Try to extract simple name from URI
                String simpleName = extractSimpleNameStatic(declared);
                if (!actualSuperTypes.contains(simpleName)) {
                    invalidSuperTypes.add(declared);
                }
            }
        }

        if (!invalidSuperTypes.isEmpty()) {
            throw new SuperTypeValidationException(
                    resolvedEClass.getName(),
                    declaredSuperTypes,
                    new ArrayList<>(actualSuperTypes),
                    invalidSuperTypes);
        }

        LOGGER.fine("SuperType hierarchy validated for " + resolvedEClass.getName());
    }

    /**
     * Gets the full URI for an EClass.
     *
     * @param eClass the EClass
     * @return the EClass URI (nsURI#//className)
     */
    private static String getEClassUriStatic(EClass eClass) {
        return eClass.getEPackage().getNsURI() + "#//" + eClass.getName();
    }

    /**
     * Extracts the simple class name from a URI.
     *
     * @param value the URI or simple name
     * @return the simple class name
     */
    private static String extractSimpleNameStatic(String value) {
        if (value == null) {
            return null;
        }
        int idx = value.lastIndexOf("#//");
        if (idx >= 0) {
            return value.substring(idx + 3);
        }
        return value;
    }

    /**
     * Exception thrown when supertype hierarchy validation fails.
     */
    public static class SuperTypeValidationException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private final String eClassName;
        private final List<String> declaredSuperTypes;
        private final List<String> expectedSuperTypes;
        private final List<String> invalidSuperTypes;

        public SuperTypeValidationException(String eClassName, List<String> declaredSuperTypes,
                List<String> expectedSuperTypes, List<String> invalidSuperTypes) {
            super(buildMessage(eClassName, declaredSuperTypes, expectedSuperTypes, invalidSuperTypes));
            this.eClassName = eClassName;
            this.declaredSuperTypes = declaredSuperTypes;
            this.expectedSuperTypes = expectedSuperTypes;
            this.invalidSuperTypes = invalidSuperTypes;
        }

        private static String buildMessage(String eClassName, List<String> declaredSuperTypes,
                List<String> expectedSuperTypes, List<String> invalidSuperTypes) {
            StringBuilder sb = new StringBuilder();
            sb.append("SuperType hierarchy validation failed for EClass '").append(eClassName).append("':\n");
            sb.append("  Declared supertypes: ").append(declaredSuperTypes).append("\n");
            sb.append("  Expected supertypes: ").append(expectedSuperTypes).append("\n");
            sb.append("  Invalid supertype(s): ").append(invalidSuperTypes)
                    .append(" not found in hierarchy");
            return sb.toString();
        }

        public String getEClassName() {
            return eClassName;
        }

        public List<String> getDeclaredSuperTypes() {
            return declaredSuperTypes;
        }

        public List<String> getExpectedSuperTypes() {
            return expectedSuperTypes;
        }

        public List<String> getInvalidSuperTypes() {
            return invalidSuperTypes;
        }
    }
}
