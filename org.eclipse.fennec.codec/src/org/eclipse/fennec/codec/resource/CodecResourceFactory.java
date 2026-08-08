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

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

import tools.jackson.databind.json.JsonMapper;

/**
 * Factory for creating {@link CodecResource} instances.
 * <p>
 * Uses {@link ConfigurationResolver} for spec-compliant on-demand
 * configuration resolution.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public class CodecResourceFactory extends ResourceFactoryImpl {

    private MetadataService metadataService;
    private ConfigurationResolver resolver = ConfigurationResolver.defaults();
    private JsonMapper.Builder mapperBuilder;

    private Map<Object, Object> defaultSaveOptions = Collections.emptyMap();
    private Map<Object, Object> defaultLoadOptions = Collections.emptyMap();

    /**
     * Creates a new CodecResourceFactory for dependency injection.
     */
    public CodecResourceFactory() {
        // DI-friendly constructor
    }

    /**
     * Creates a new CodecResourceFactory with default configuration.
     *
     * @param metadataService the metadata service
     */
    public CodecResourceFactory(MetadataService metadataService) {
        this(metadataService, ConfigurationResolver.defaults());
    }

    /**
     * Creates a new CodecResourceFactory with custom resolver.
     *
     * @param metadataService the metadata service
     * @param resolver the configuration resolver
     */
    public CodecResourceFactory(MetadataService metadataService, ConfigurationResolver resolver) {
        this(metadataService, resolver, null);
    }

    /**
     * Creates a new CodecResourceFactory with full configuration.
     *
     * @param metadataService the metadata service
     * @param resolver the configuration resolver
     * @param mapperBuilder optional pre-configured JsonMapper builder
     */
    public CodecResourceFactory(MetadataService metadataService, ConfigurationResolver resolver,
            JsonMapper.Builder mapperBuilder) {
        this.metadataService = metadataService;
        this.resolver = resolver != null ? resolver : ConfigurationResolver.defaults();
        this.mapperBuilder = mapperBuilder;
    }

    @Override
    public Resource createResource(URI uri) {
        if (metadataService == null) {
            throw new IllegalStateException(
                "MetadataService not set. Call setMetadataService() before creating resources, " +
                "or use a constructor that accepts MetadataService.");
        }
        return new CodecResource(uri, metadataService, resolver, mapperBuilder);
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public ConfigurationResolver getResolver() {
        return resolver;
    }

    public void setMetadataService(MetadataService metadataService) {
        if (metadataService == null) {
            throw new IllegalArgumentException("MetadataService must not be null");
        }
        this.metadataService = metadataService;
    }

    public void setResolver(ConfigurationResolver resolver) {
        this.resolver = resolver != null ? resolver : ConfigurationResolver.defaults();
    }

    public void setMapperBuilder(JsonMapper.Builder mapperBuilder) {
        this.mapperBuilder = mapperBuilder;
    }

    public Map<Object, Object> getDefaultSaveOptions() {
        return defaultSaveOptions;
    }

    public void setDefaultSaveOptions(Map<Object, Object> defaultSaveOptions) {
        this.defaultSaveOptions = defaultSaveOptions != null ? defaultSaveOptions : Collections.emptyMap();
    }

    public Map<Object, Object> getDefaultLoadOptions() {
        return defaultLoadOptions;
    }

    public void setDefaultLoadOptions(Map<Object, Object> defaultLoadOptions) {
        this.defaultLoadOptions = defaultLoadOptions != null ? defaultLoadOptions : Collections.emptyMap();
    }
}
