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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

/**
 * Holds EMF state during serialization/deserialization operations.
 * <p>
 * This class stores the current EObject, feature, resource, and effective codec
 * configuration that are needed by serializers and deserializers to properly
 * process EMF objects.
 * </p>
 * <p>
 * The {@link EffectiveCodecConfig} provides a single source of truth for all
 * configuration, including access to the {@link MetadataService} for type
 * resolution and aspect lookups.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public class EMFContextHolder {

    private EStructuralFeature currentFeature;
    private EObject currentEObject;
    private Resource resource;
    private EffectiveCodecConfig effectiveConfig;
    private EClass currentTypeHint;
    private String contextSchemaUri;

    /**
     * Creates a new context holder.
     */
    public EMFContextHolder() {
    }

    /**
     * Creates a new context holder with the specified effective configuration.
     *
     * @param effectiveConfig the effective codec configuration
     */
    public EMFContextHolder(EffectiveCodecConfig effectiveConfig) {
        this.effectiveConfig = effectiveConfig;
    }

    /**
     * Returns the current EObject being processed.
     *
     * @return the current EObject, or null if none
     */
    public EObject getCurrentEObject() {
        return currentEObject;
    }

    /**
     * Sets the current EObject being processed.
     *
     * @param eObject the current EObject
     */
    public void setCurrentEObject(EObject eObject) {
        this.currentEObject = eObject;
    }

    /**
     * Returns the current EStructuralFeature being processed.
     *
     * @return the current feature, or null if none
     */
    public EStructuralFeature getCurrentFeature() {
        return currentFeature;
    }

    /**
     * Sets the current EStructuralFeature being processed.
     *
     * @param feature the current feature
     */
    public void setCurrentFeature(EStructuralFeature feature) {
        this.currentFeature = feature;
    }

    /**
     * Resets the current feature to null.
     */
    public void resetFeature() {
        this.currentFeature = null;
    }

    /**
     * Returns the EMF Resource being processed.
     *
     * @return the resource, or null if none
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets the EMF Resource being processed.
     *
     * @param resource the resource
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Returns the effective codec configuration.
     *
     * @return the effective configuration, or null if none
     */
    public EffectiveCodecConfig getEffectiveConfig() {
        return effectiveConfig;
    }

    /**
     * Sets the effective codec configuration.
     *
     * @param effectiveConfig the effective configuration
     */
    public void setEffectiveConfig(EffectiveCodecConfig effectiveConfig) {
        this.effectiveConfig = effectiveConfig;
    }

    /**
     * Returns the MetadataService for aspect lookups.
     * <p>
     * This is a convenience method that delegates to the {@link EffectiveCodecConfig}.
     * </p>
     *
     * @return the metadata service, or null if no config or no service configured
     */
    public MetadataService getMetadataService() {
        return effectiveConfig != null ? effectiveConfig.getMetadataService() : null;
    }

    /**
     * Returns the current type hint for deserialization.
     * <p>
     * The type hint is typically set from CODEC_ROOT_TYPE option or
     * from the containing reference's EType.
     * </p>
     *
     * @return the type hint EClass, or null if none
     */
    public EClass getCurrentTypeHint() {
        return currentTypeHint;
    }

    /**
     * Sets the current type hint for deserialization.
     *
     * @param typeHint the type hint EClass
     */
    public void setCurrentTypeHint(EClass typeHint) {
        this.currentTypeHint = typeHint;
    }

    /**
     * Returns the context schema URI for NAME strategy type resolution.
     * <p>
     * The context schema is used to resolve simple type names (e.g., "Person")
     * to full EClass URIs (e.g., "http://example.org/1.0#//Person").
     * </p>
     * <p>
     * Sources (priority order):
     * <ol>
     *   <li>CODEC_ROOT_SCHEMA option (explicit)</li>
     *   <li>CODEC_ROOT_TYPE option (implicit from EClass package)</li>
     *   <li>First full URI in content (smart compression)</li>
     * </ol>
     * </p>
     *
     * @return the context schema URI, or null if not set
     */
    public String getContextSchemaUri() {
        return contextSchemaUri;
    }

    /**
     * Sets the context schema URI for NAME strategy type resolution.
     *
     * @param contextSchemaUri the context schema URI (e.g., "http://example.org/1.0")
     */
    public void setContextSchemaUri(String contextSchemaUri) {
        this.contextSchemaUri = contextSchemaUri;
    }
}
