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

import static java.util.Objects.requireNonNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

import tools.jackson.databind.json.JsonMapper;

/**
 * Convenience factory that creates {@link CodecResource} instances with a
 * specific {@link CodecFormatProvider} for non-JSON formats.
 * <p>
 * Usage:
 * <pre>
 * CodecFormatProvider&lt;InputStream, OutputStream&gt; cborProvider =
 *         new JacksonFormatProvider("cbor", new CBORFactory());
 *
 * CodecFormatResourceFactory factory =
 *         new CodecFormatResourceFactory(metadataService, cborProvider);
 *
 * Resource resource = factory.createResource(URI.createURI("test.cbor"));
 * </pre>
 *
 * @see CodecResource
 * @see CodecFormatProvider
 * @since 1.0
 */
public class CodecFormatResourceFactory extends ResourceFactoryImpl {

    private final MetadataService metadataService;
    private final CodecFormatProvider<?, ?> formatProvider;
    private ConfigurationResolver resolver;
    private JsonMapper.Builder mapperBuilder;

    /**
     * Creates a new factory with default configuration.
     *
     * @param metadataService the metadata service
     * @param formatProvider the format provider
     */
    public CodecFormatResourceFactory(MetadataService metadataService,
            CodecFormatProvider<?, ?> formatProvider) {
        this(metadataService, formatProvider, ConfigurationResolver.defaults(), null);
    }

    /**
     * Creates a new factory with custom resolver.
     *
     * @param metadataService the metadata service
     * @param formatProvider the format provider
     * @param resolver the configuration resolver
     */
    public CodecFormatResourceFactory(MetadataService metadataService,
            CodecFormatProvider<?, ?> formatProvider, ConfigurationResolver resolver) {
        this(metadataService, formatProvider, resolver, null);
    }

    /**
     * Creates a new factory with full configuration.
     *
     * @param metadataService the metadata service
     * @param formatProvider the format provider
     * @param resolver the configuration resolver
     * @param mapperBuilder optional pre-configured JsonMapper builder
     */
    public CodecFormatResourceFactory(MetadataService metadataService,
            CodecFormatProvider<?, ?> formatProvider, ConfigurationResolver resolver,
            JsonMapper.Builder mapperBuilder) {
        this.metadataService = requireNonNull(metadataService, "metadataService must not be null");
        this.formatProvider = requireNonNull(formatProvider, "formatProvider must not be null");
        this.resolver = resolver != null ? resolver : ConfigurationResolver.defaults();
        this.mapperBuilder = mapperBuilder;
    }

    @Override
    public Resource createResource(URI uri) {
        return new CodecResource(uri, metadataService, resolver, null, mapperBuilder, formatProvider);
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public CodecFormatProvider<?, ?> getFormatProvider() {
        return formatProvider;
    }

    public ConfigurationResolver getResolver() {
        return resolver;
    }

    public void setResolver(ConfigurationResolver resolver) {
        this.resolver = resolver != null ? resolver : ConfigurationResolver.defaults();
    }

    public void setMapperBuilder(JsonMapper.Builder mapperBuilder) {
        this.mapperBuilder = mapperBuilder;
    }
}
