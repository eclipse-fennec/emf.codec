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
package org.eclipse.fennec.codec.context;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.model.metadata.ClassMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataService;

/**
 * Base context interface for EMF codec operations.
 * <p>
 * This interface tracks EMF state during serialization and deserialization,
 * providing access to the current EObject, feature, resource, and metadata service.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
public interface EMFCodecContext {

    /**
     * Returns the EObject currently being processed.
     *
     * @return the current EObject, or null if none
     */
    EObject getCurrentEObject();

    /**
     * Sets the EObject currently being processed.
     *
     * @param eObject the current EObject
     */
    void setCurrentEObject(EObject eObject);

    /**
     * Returns the EStructuralFeature currently being processed.
     *
     * @return the current feature, or null if none
     */
    EStructuralFeature getCurrentFeature();

    /**
     * Sets the EStructuralFeature currently being processed.
     *
     * @param feature the current feature
     */
    void setCurrentFeature(EStructuralFeature feature);

    /**
     * Resets the current feature to null.
     */
    void resetFeature();

    /**
     * Returns the EMF Resource being processed.
     *
     * @return the resource, or null if none
     */
    Resource getResource();

    /**
     * Sets the EMF Resource being processed.
     *
     * @param resource the resource
     */
    void setResource(Resource resource);

    /**
     * Returns the effective codec configuration.
     * <p>
     * This provides access to all merged configuration including global settings,
     * class configs, feature configs, and the MetadataService.
     * </p>
     *
     * @return the effective configuration, or null if not configured
     */
    EffectiveCodecConfig getEffectiveConfig();

    /**
     * Returns the MetadataService for aspect lookups.
     * <p>
     * This is a convenience method that delegates to the EffectiveCodecConfig.
     * </p>
     *
     * @return the metadata service, or null if not configured
     */
    default MetadataService getMetadataService() {
        EffectiveCodecConfig config = getEffectiveConfig();
        return config != null ? config.getMetadataService() : null;
    }

    /**
     * Convenience method to get ClassMetadata for an EClass.
     *
     * @param eClass the EClass
     * @return the class metadata, or null if not found
     */
    default ClassMetadata getClassMetadata(EClass eClass) {
        EffectiveCodecConfig config = getEffectiveConfig();
        return config != null ? config.getClassMetadata(eClass) : null;
    }

    /**
     * Returns the context holder that stores EMF state.
     *
     * @return the EMF context holder
     */
    EMFContextHolder getEMFContextHolder();

    /**
     * Sets the context holder that stores EMF state.
     *
     * @param holder the EMF context holder
     */
    void setEMFContextHolder(EMFContextHolder holder);
}
