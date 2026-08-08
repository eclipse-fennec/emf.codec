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
package org.eclipse.fennec.codec.metadata.provider.internal;

import org.eclipse.fennec.codec.metadata.provider.CodecAnnotationConstants;

import org.eclipse.fennec.codec.metadata.provider.CodecAspectProvider;

import org.eclipse.fennec.emf.osgi.metadata.MetadataHandler;
import org.osgi.service.component.annotations.Component;

/**
 * OSGi Declarative Services component that registers {@link CodecAspectProvider}
 * as a {@link MetadataHandler} service.
 * <p>
 * When active, the {@code MetadataServiceComponent} whiteboard automatically
 * picks up this service and applies codec aspect parsing to every registered
 * {@code EPackage}. This means codec annotations (
 * {@code @Codec} source: {@link CodecAnnotationConstants#CODEC_SOURCE}) placed on
 * EClasses and EStructuralFeatures are translated into
 * {@link org.eclipse.fennec.codec.metadata.model.codec.ClassCodecAspect} and
 * {@link org.eclipse.fennec.codec.metadata.model.codec.FeatureCodecAspect} objects
 * and attached to the package metadata as aspect entries — without any manual wiring.
 * </p>
 *
 * <h3>OSGi activation order</h3>
 * <ol>
 *   <li>Each model bundle's generated {@code *ConfigurationComponent} registers
 *       its {@code EPackage} as an OSGi service.</li>
 *   <li>{@code MetadataServiceComponent} picks up both the {@code EPackage} and
 *       this {@code CodecAspectProviderComponent} (order is dynamic; late arrivals
 *       are applied retroactively).</li>
 *   <li>Consumers that inject {@code MetadataService} receive a fully populated
 *       service with codec aspects ready.</li>
 * </ol>
 *
 * @author Data In Motion Consulting
 * @since 1.0
 * @see CodecAspectProvider
 * @see MetadataHandler
 */
@Component(
        name = "CodecAspectProviderComponent",
        service = MetadataHandler.class
)
public class CodecAspectProviderComponent extends CodecAspectProvider {
    // All codec aspect parsing logic is inherited from CodecAspectProvider.
    // This class exists solely to expose it as an OSGi DS service so that
    // MetadataServiceComponent can discover and register it automatically.
}
