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
package org.eclipse.fennec.codec.resource;

import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.DiscriminatorConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.FallbackStrategy;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;

/**
 * Lays the Type Mapping Registry and Inline Mapping settings that configuration supplies -
 * {@code codec.eClassConfig} and {@code codec.eReferenceConfig} in load/save options, resource,
 * factory or module properties - over the discriminator registries built from the model
 * (issue #239, spec 08 §4.4).
 * <p>
 * The target is the service built for one operation, so option-supplied mappings never reach
 * the shared, model-derived views (spec 08 §7.4). Configuration ranks above annotations: a
 * configured value replaces an annotated mapping of the same value, and a configured fallback
 * replaces the annotated one.
 * </p>
 */
final class OptionTypeMappings {

    private static final String SOURCE = "OptionTypeMappings";

    private OptionTypeMappings() {
    }

    /**
     * Applies the configured discriminator settings to the operation's service.
     *
     * @param service the per-operation discriminator service
     * @param resolver the operation's configuration
     * @param uriResolver resolves an EClass URI the way this operation resolves types
     * @param diagnostics receives what could not be applied
     */
    static void apply(TypeDiscriminatorService service, ConfigurationResolver resolver,
            Function<String, EClass> uriResolver, DiagnosticCollector diagnostics) {
        for (EClass eClass : resolver.getConfiguredEClasses()) {
            applyClass(service, eClass, resolver.resolveDiscriminatorOverrides(eClass), uriResolver, diagnostics);
        }
        for (EReference reference : resolver.getConfiguredEReferences()) {
            applyReference(service, reference, resolver.resolveInlineDiscriminatorOverrides(reference),
                    uriResolver, diagnostics);
        }
    }

    /**
     * Reports configured mappings that an externally managed reader leaves unused: such a
     * reader is the caller's, and the codec does not change it.
     *
     * @param resolver the operation's configuration
     * @param diagnostics receives the warning
     */
    static void reportIgnored(ConfigurationResolver resolver, DiagnosticCollector diagnostics) {
        boolean configured = resolver.getConfiguredEClasses().stream()
                .anyMatch(eClass -> hasRegistrySettings(resolver.resolveDiscriminatorOverrides(eClass)))
                || resolver.getConfiguredEReferences().stream()
                        .anyMatch(ref -> hasInlineSettings(resolver.resolveInlineDiscriminatorOverrides(ref)));
        if (configured) {
            diagnostics.addWarning("Type or inline mappings from configuration are ignored: this resource"
                    + " uses an externally managed TypeDiscriminatorReader, which the codec does not change",
                    SOURCE);
        }
    }

    private static void applyClass(TypeDiscriminatorService service, EClass eClass, DiscriminatorConfig config,
            Function<String, EClass> uriResolver, DiagnosticCollector diagnostics) {
        if (!hasRegistrySettings(config)) {
            return;
        }
        String mapId = config.getTypeMapId() != null ? config.getTypeMapId() : service.getMapIdForEClass(eClass);
        if (mapId == null) {
            diagnostics.addWarning("Discriminator settings for '" + eClass.getName() + "' are ignored: no"
                    + " typeMapId is configured and the class belongs to no typeMapping registry", SOURCE);
            return;
        }
        if (config.getTypeMapId() != null) {
            service.assignMapId(eClass, mapId);
        }
        service.configureRegistry(mapId, config.getTypeDiscriminatorPath(),
                toModel(config.getFallbackStrategy()), config.getFallbackEClass());
        registerAll(service, mapId, config.getTypeMappings(), uriResolver, diagnostics);
        if (config.getTypeDiscriminator() != null) {
            service.registerOverride(mapId, config.getTypeDiscriminator(), eClass);
        }
    }

    private static void applyReference(TypeDiscriminatorService service, EReference reference,
            DiscriminatorConfig config, Function<String, EClass> uriResolver, DiagnosticCollector diagnostics) {
        if (!hasInlineSettings(config)) {
            return;
        }
        // The same registry id resolveForReference and the write side look up
        String mapId = EcoreUtil.getURI(reference).toString();
        service.configureRegistry(mapId, null, toModel(config.getFallbackStrategy()), config.getFallbackEClass());
        registerAll(service, mapId, config.getInlineMappings(), uriResolver, diagnostics);
    }

    private static void registerAll(TypeDiscriminatorService service, String mapId, Map<String, Object> mappings,
            Function<String, EClass> uriResolver, DiagnosticCollector diagnostics) {
        for (Map.Entry<String, Object> mapping : mappings.entrySet()) {
            EClass target = toEClass(mapping.getValue(), uriResolver);
            if (target == null) {
                diagnostics.addWarning("[" + mapId + "] Mapping '" + mapping.getKey() + "' is ignored: '"
                        + mapping.getValue() + "' is neither an EClass nor the URI of a known class", SOURCE);
                continue;
            }
            service.registerOverride(mapId, mapping.getKey(), target);
        }
    }

    private static EClass toEClass(Object value, Function<String, EClass> uriResolver) {
        if (value instanceof EClass eClass) {
            return eClass;
        }
        if (value instanceof String uri && !uri.isEmpty()) {
            return uriResolver.apply(uri);
        }
        return null;
    }

    private static boolean hasRegistrySettings(DiscriminatorConfig config) {
        return config.getTypeMapId() != null || config.getTypeDiscriminatorPath() != null
                || config.getTypeDiscriminator() != null || !config.getTypeMappings().isEmpty()
                || config.getFallbackStrategy() != null || config.getFallbackEClass() != null;
    }

    private static boolean hasInlineSettings(DiscriminatorConfig config) {
        return !config.getInlineMappings().isEmpty() || config.getFallbackStrategy() != null
                || config.getFallbackEClass() != null;
    }

    private static FallbackStrategy toModel(DiscriminatorConfig.FallbackStrategy strategy) {
        return strategy != null ? FallbackStrategy.valueOf(strategy.name()) : null;
    }
}
