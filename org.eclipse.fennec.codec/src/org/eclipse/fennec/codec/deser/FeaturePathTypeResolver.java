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

import tools.jackson.databind.DeserializationContext;
import org.eclipse.fennec.codec.util.PackageResolver;
import org.eclipse.fennec.codec.context.ContextHelper;
import java.util.function.Function;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.buffer.CodecTokenBuffer;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorReader;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;

/**
 * Resolves EClass from a discriminator value located at a feature path in JSON content.
 * <p>
 * This class implements the TokenBuffer-based scanning approach for featurePath-based
 * type discrimination. Instead of looking for a dedicated "_type" field, it scans
 * the JSON object to find the discriminator value at a specified path (e.g., "info.profileName").
 * </p>
 * <p>
 * The scanning algorithm:
 * <ol>
 *   <li>Split the feature path by "." to get path segments</li>
 *   <li>Track nesting depth while scanning JSON tokens</li>
 *   <li>Buffer all tokens for later replay</li>
 *   <li>When a path segment matches at the correct depth, navigate deeper or extract value</li>
 *   <li>Resolve EClass via TypeDiscriminatorReader once discriminator value is found</li>
 * </ol>
 * </p>
 *
 * @see TypeDiscriminatorReader
 * @author Mark Hoffmann
 * @since 2025-12-28
 */
public class FeaturePathTypeResolver {

    private static final Logger LOGGER = Logger.getLogger(FeaturePathTypeResolver.class.getName());

    private final String discriminatorPath;
    private final TypeDiscriminatorReader typeDiscriminatorService;
    private final String mapId;

    /** The resolved EClass (set after scanning) */
    private EClass resolvedEClass;

    /** The discriminator value found at the path (for debugging) */
    private String foundDiscriminatorValue;

    /** Buffer containing all scanned tokens for replay */
    private CodecTokenBuffer buffer;

    /**
     * Creates a new FeaturePathTypeResolver.
     *
     * @param discriminatorPath the feature path (e.g., "info.profileName" or "messageType")
     * @param typeDiscriminatorService the service for resolving discriminator values to EClasses
     */
    public FeaturePathTypeResolver(String discriminatorPath, TypeDiscriminatorReader typeDiscriminatorService) {
        this(discriminatorPath, typeDiscriminatorService, null);
    }

    /**
     * Creates a new FeaturePathTypeResolver with a specific registry mapId.
     * <p>
     * When mapId is provided, resolution uses the targeted registry instead of
     * searching all registries. This ensures the correct fallback strategy is applied.
     * </p>
     *
     * @param discriminatorPath the feature path (e.g., "info.profileName" or "messageType")
     * @param typeDiscriminatorService the service for resolving discriminator values to EClasses
     * @param mapId the registry map ID (from DiscriminatorConfig), or null to search all registries
     */
    public FeaturePathTypeResolver(String discriminatorPath, TypeDiscriminatorReader typeDiscriminatorService, String mapId) {
        this.discriminatorPath = Objects.requireNonNull(discriminatorPath, "discriminatorPath must not be null");
        this.typeDiscriminatorService = Objects.requireNonNull(typeDiscriminatorService, "typeDiscriminatorService must not be null");
        this.mapId = mapId;
    }

    /**
     * Scans the JSON content to find the discriminator value and resolve the EClass.
     * <p>
     * After calling this method:
     * <ul>
     *   <li>{@link #getResolvedEClass()} returns the resolved EClass (or null if not found)</li>
     *   <li>{@link #getBufferedParser(ObjectReadContext)} returns a parser to replay the buffered content</li>
     * </ul>
     * </p>
     *
     * @param parser the JSON parser positioned at START_OBJECT
     * @param readContext the object read context for buffer creation
     */
    public void scan(JsonParser parser, ObjectReadContext readContext) {
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            LOGGER.warning("FeaturePathTypeResolver expects START_OBJECT, got: " + parser.currentToken());
            return;
        }

        String[] pathSegments = discriminatorPath.split("\\.");
        int targetDepth = pathSegments.length;

        // Create buffer to store all tokens
        buffer = CodecTokenBuffer.forBuffering(parser, readContext);

        // Track current position in path and depth
        int currentPathIndex = 0;
        int currentDepth = 0;

        // Copy the START_OBJECT token
        buffer.copyCurrentEvent(parser);
        currentDepth = 1;

        // Scan through the object
        JsonToken token;
        while ((token = parser.nextToken()) != null && currentDepth > 0) {
            buffer.copyCurrentEvent(parser);

            // Update depth
            if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                currentDepth++;
            } else if (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY) {
                currentDepth--;
                // Reset path index when leaving a nested object at wrong depth
                if (currentDepth < currentPathIndex + 1) {
                    currentPathIndex = Math.max(0, currentDepth - 1);
                }
            } else if (token == JsonToken.PROPERTY_NAME) {
                String fieldName = parser.currentName();

                // Check if this field matches the current path segment at the right depth
                if (currentDepth == currentPathIndex + 1 && fieldName.equals(pathSegments[currentPathIndex])) {
                    if (currentPathIndex == targetDepth - 1) {
                        // We're at the final segment - next token is the discriminator value
                        token = parser.nextToken();
                        buffer.copyCurrentEvent(parser);

                        if (token == JsonToken.VALUE_STRING) {
                            foundDiscriminatorValue = parser.getString();
                            resolvedEClass = resolveDiscriminator(foundDiscriminatorValue, readContext);

                            if (resolvedEClass != null) {
                                LOGGER.fine("Resolved type via featurePath '" + discriminatorPath +
                                        "': " + foundDiscriminatorValue + " -> " + resolvedEClass.getName());
                            } else {
                                LOGGER.warning("No EClass found for discriminator: " + foundDiscriminatorValue);
                            }
                        } else {
                            LOGGER.warning("Expected VALUE_STRING for discriminator at path '" +
                                    discriminatorPath + "', got: " + token);
                        }
                    } else {
                        // Move to next path segment
                        currentPathIndex++;
                    }
                }
            }
        }

        // Continue buffering remaining tokens if we haven't reached the end
        while (currentDepth > 0 && (token = parser.nextToken()) != null) {
            buffer.copyCurrentEvent(parser);
            if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                currentDepth++;
            } else if (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY) {
                currentDepth--;
            }
        }

        if (resolvedEClass == null) {
            LOGGER.warning("Could not resolve EClass from featurePath '" + discriminatorPath +
                    "'. Discriminator value found: " + foundDiscriminatorValue);
        }
    }

    /**
     * Resolves a discriminator value to an EClass using either a targeted registry
     * (when mapId is available) or all registries.
     * <p>
     * The class-URI resolver handed to the registry goes through the per-load
     * {@link PackageResolver} when one is available (issue #54, B.5), so a stored class URI
     * selects the version-correct {@code EClass} instance instead of whatever the global,
     * last-wins registry happens to hold for that nsURI.
     * </p>
     *
     * @param discriminatorValue the discriminator value to resolve
     * @param readContext the read context, used to reach the per-load package resolver
     * @return the resolved EClass, or null if not found
     */
    private EClass resolveDiscriminator(String discriminatorValue, ObjectReadContext readContext) {
        PackageResolver packageResolver = readContext instanceof DeserializationContext ctxt
                ? ContextHelper.getPackageResolver(ctxt)
                : null;
        Function<String, EClass> eClassResolver = uri -> resolveEClassFromUri(uri, packageResolver);

        if (mapId != null) {
            return typeDiscriminatorService.resolve(mapId, discriminatorValue, eClassResolver);
        }
        return typeDiscriminatorService.resolveFromAny(discriminatorValue, eClassResolver);
    }

    /**
     * Returns the resolved EClass, or null if not found.
     */
    public EClass getResolvedEClass() {
        return resolvedEClass;
    }

    /**
     * Returns the discriminator value found at the path, or null if not found.
     */
    public String getFoundDiscriminatorValue() {
        return foundDiscriminatorValue;
    }

    /**
     * Returns a parser to replay the buffered content.
     * <p>
     * The parser is positioned before the first token. Call {@code nextToken()}
     * to get the START_OBJECT token.
     * </p>
     *
     * @param readContext the object read context
     * @return a parser for the buffered content, or null if no content was buffered
     */
    public JsonParser getBufferedParser(ObjectReadContext readContext) {
        if (buffer == null) {
            return null;
        }
        return buffer.asParserOnFirstToken(readContext);
    }

    /**
     * Returns a parser to replay the buffered content using the provided source parser's context.
     *
     * @param readContext the object read context
     * @param sourceParser the original source parser (for location info)
     * @return a parser for the buffered content
     */
    public JsonParser getBufferedParser(ObjectReadContext readContext, JsonParser sourceParser) {
        if (buffer == null) {
            return null;
        }
        return buffer.asParserOnFirstToken(readContext, sourceParser);
    }

    /**
     * Checks if a discriminator path is configured (non-null and non-empty).
     *
     * @param path the discriminator path to check
     * @return true if the path is configured
     */
    public static boolean hasDiscriminatorPath(String path) {
        return path != null && !path.isEmpty();
    }

    /**
     * Resolves an EClass from a URI string using the global package registry.
     * <p>
     * Used as the {@code eClassResolver} function for fallback strategy resolution
     * via {@link TypeDiscriminatorReader#resolveFromAny}.
     * </p>
     *
     * @param uriStr the EClass URI (e.g., "http://example.org/1.0#//ClassName")
     * @return the resolved EClass, or null if not found
     */
    /**
     * Resolves an EClass from a class URI, selecting the version through the per-load
     * {@link PackageResolver} when one is available (issue #54, B.5).
     * <p>
     * The resolver applies the binding source order (pin, ResourceSet registry, MetadataService
     * candidate query) and the count-based candidate rule: exactly one candidate is used, more
     * than one is an error naming them. That error matters here — the global registry holds a
     * single EPackage per nsURI and would otherwise answer with whichever version was registered
     * last, turning a genuine ambiguity into a silently wrong class.
     * </p>
     * <p>
     * With no resolver at hand (no deserialization context) the global registry is used, which
     * is also the resolver's own last tier for nsURIs the MetadataService does not know at all —
     * foreign or plain-EMF packages.
     * </p>
     *
     * @param uriStr the EClass URI (e.g., "http://example.org/1.0#//ClassName")
     * @param packageResolver the per-load resolver, or {@code null} if none is available
     * @return the resolved EClass, or null if not found
     * @throws IllegalStateException if the nsURI has more than one registered version and no
     *         pin selects one
     */
    static EClass resolveEClassFromUri(String uriStr, PackageResolver packageResolver) {
        if (uriStr == null || uriStr.isEmpty()) {
            return null;
        }
        if (packageResolver != null) {
            try {
                return packageResolver.resolveEClassFromTypeUri(uriStr, null);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return resolveEClassViaGlobalRegistry(uriStr);
    }

    /**
     * Legacy resolution straight from {@link EPackage.Registry#INSTANCE}, used only when no
     * per-load resolver is available.
     *
     * @param uriStr the EClass URI
     * @return the resolved EClass, or null if not found
     */
    private static EClass resolveEClassViaGlobalRegistry(String uriStr) {
        try {
            URI uri = URI.createURI(uriStr);
            String fragment = uri.fragment();
            if (fragment != null && fragment.startsWith("//")) {
                String nsUri = uri.trimFragment().toString();
                EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsUri);
                if (ePackage != null) {
                    EClassifier classifier = ePackage.getEClassifier(fragment.substring(2));
                    if (classifier instanceof EClass eClass) {
                        return eClass;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to resolve EClass URI: " + uriStr + " \u2014 " + e.getMessage());
        }
        return null;
    }
}
