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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EMFContextHolder}.
 */
@DisplayName("EMFContextHolder")
class EMFContextHolderTest {

    private EMFContextHolder holder;
    private MetadataService metadataService;
    private EffectiveCodecConfig effectiveConfig;

    @BeforeEach
    void setUp() {
        metadataService = mock(MetadataService.class);
        effectiveConfig = EffectiveCodecConfig.builder()
                .resolver(ConfigurationResolver.defaults())
                .diagnostics(new DiagnosticCollector())
                .metadataService(metadataService)
                .build();
        holder = new EMFContextHolder(effectiveConfig);
    }

    @Test
    @DisplayName("default constructor creates holder with null effective config")
    void defaultConstructorNullEffectiveConfig() {
        EMFContextHolder defaultHolder = new EMFContextHolder();
        assertNull(defaultHolder.getEffectiveConfig());
        assertNull(defaultHolder.getMetadataService());
    }

    @Test
    @DisplayName("constructor with effective config stores config")
    void constructorWithEffectiveConfig() {
        assertSame(effectiveConfig, holder.getEffectiveConfig());
        assertSame(metadataService, holder.getMetadataService());
    }

    @Test
    @DisplayName("setCurrentEObject and getCurrentEObject")
    void setAndGetCurrentEObject() {
        EObject eObject = mock(EObject.class);
        holder.setCurrentEObject(eObject);
        assertSame(eObject, holder.getCurrentEObject());
    }

    @Test
    @DisplayName("getCurrentEObject returns null by default")
    void getCurrentEObjectDefaultNull() {
        assertNull(holder.getCurrentEObject());
    }

    @Test
    @DisplayName("setCurrentFeature and getCurrentFeature")
    void setAndGetCurrentFeature() {
        EStructuralFeature feature = mock(EStructuralFeature.class);
        holder.setCurrentFeature(feature);
        assertSame(feature, holder.getCurrentFeature());
    }

    @Test
    @DisplayName("getCurrentFeature returns null by default")
    void getCurrentFeatureDefaultNull() {
        assertNull(holder.getCurrentFeature());
    }

    @Test
    @DisplayName("resetFeature sets current feature to null")
    void resetFeatureSetsNull() {
        EStructuralFeature feature = mock(EStructuralFeature.class);
        holder.setCurrentFeature(feature);
        holder.resetFeature();
        assertNull(holder.getCurrentFeature());
    }

    @Test
    @DisplayName("setResource and getResource")
    void setAndGetResource() {
        Resource resource = mock(Resource.class);
        holder.setResource(resource);
        assertSame(resource, holder.getResource());
    }

    @Test
    @DisplayName("getResource returns null by default")
    void getResourceDefaultNull() {
        assertNull(holder.getResource());
    }

    @Test
    @DisplayName("setEffectiveConfig and getEffectiveConfig")
    void setAndGetEffectiveConfig() {
        EffectiveCodecConfig newConfig = EffectiveCodecConfig.builder()
                .resolver(ConfigurationResolver.defaults())
                .diagnostics(new DiagnosticCollector())
                .build();
        holder.setEffectiveConfig(newConfig);
        assertSame(newConfig, holder.getEffectiveConfig());
    }

    @Test
    @DisplayName("setCurrentTypeHint and getCurrentTypeHint")
    void setAndGetCurrentTypeHint() {
        EClass typeHint = mock(EClass.class);
        holder.setCurrentTypeHint(typeHint);
        assertSame(typeHint, holder.getCurrentTypeHint());
    }

    @Test
    @DisplayName("getCurrentTypeHint returns null by default")
    void getCurrentTypeHintDefaultNull() {
        assertNull(holder.getCurrentTypeHint());
    }
}
