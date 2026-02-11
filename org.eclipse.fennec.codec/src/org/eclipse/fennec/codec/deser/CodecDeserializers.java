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
package org.eclipse.fennec.codec.deser;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.Deserializers;

/**
 * Jackson Deserializers provider for EMF codec.
 * <p>
 * Registers the {@link CodecEObjectDeserializer} for all EObject types.
 * Uses the effective configuration pattern for on-demand config resolution.
 * </p>
 *
 * @see CodecEObjectDeserializer
 * @see EffectiveCodecConfig
 * @author Mark Hoffmann
 * @since 2026-02-01
 */
public class CodecDeserializers extends Deserializers.Base {

    private final EffectiveCodecConfig config;
    private final CodecEObjectDeserializer eObjectDeserializer;

    /**
     * Creates a new CodecDeserializers with the given configuration.
     *
     * @param config the effective codec configuration
     */
    public CodecDeserializers(EffectiveCodecConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.eObjectDeserializer = new CodecEObjectDeserializer(config);
    }

    @Override
    public ValueDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig deserConfig,
            BeanDescription.Supplier beanDescRef) {

        Class<?> rawClass = type.getRawClass();

        // Check if this is an EObject type
        if (EObject.class.isAssignableFrom(rawClass)) {
            return eObjectDeserializer;
        }

        return null;
    }

    @Override
    public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
        return EObject.class.isAssignableFrom(valueType);
    }

    /**
     * Returns the effective configuration.
     *
     * @return the effective codec configuration
     */
    public EffectiveCodecConfig getConfig() {
        return config;
    }

    /**
     * Returns the EObject deserializer.
     *
     * @return the CodecEObjectDeserializer instance
     */
    public CodecEObjectDeserializer getEObjectDeserializer() {
        return eObjectDeserializer;
    }
}
