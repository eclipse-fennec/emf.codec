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
package org.eclipse.fennec.codec.rlang.internal;

import org.eclipse.fennec.codec.rlang.CodecRLangOptions;
import org.eclipse.fennec.codec.rlang.RLangFormatProvider;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * OSGi DS component that registers a {@link Resource.Factory} for R-Language
 * file extensions, backed by {@link RLangFormatProvider}.
 * <ul>
 *   <li>{@code .RData} — single-file output (default mode).</li>
 *   <li>{@code .rdataz} — ZIP archive of one-data-frame-per-file
 *       (auto-sets {@link CodecRLangOptions#OPTION_DATAFRAME_PER_FILE} to
 *       {@code true}).</li>
 * </ul>
 *
 * @since 1.0
 */
@Component(
        name = "RLangResourceFactory",
        service = Resource.Factory.class,
        property = {
                EMFNamespaces.EMF_MODEL_FILE_EXT + "=RData",
                EMFNamespaces.EMF_MODEL_FILE_EXT + "=rdataz",
                EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/x-rdata",
                EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/x-rdata-zip"
        })
public class RLangResourceFactoryComponent extends ResourceFactoryImpl {

    private static final String ZIP_EXTENSION = "rdataz";

    private final MetadataService metadataService;
    private volatile CodecValueRegistry valueRegistry;

    @Activate
    public RLangResourceFactoryComponent(@Reference MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetValueRegistry"
    )
    void setValueRegistry(CodecValueRegistry registry) {
        this.valueRegistry = registry;
    }

    void unsetValueRegistry(CodecValueRegistry registry) {
        this.valueRegistry = null;
    }

    /** The prefix registry (issue #193): backend-owned document keys; copied per resource like the value registry. */
    private volatile CodecPrefixRegistry prefixRegistry;

    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetPrefixRegistry"
    )
    void setPrefixRegistry(CodecPrefixRegistry registry) {
        this.prefixRegistry = registry;
    }

    void unsetPrefixRegistry(CodecPrefixRegistry registry) {
        this.prefixRegistry = null;
    }

    @Override
    public Resource createResource(URI uri) {
        RLangFormatProvider provider = isZipUri(uri)
                ? new RLangFormatProvider(null,
                        Map.of(CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE))
                : new RLangFormatProvider();
        CodecValueRegistry reg = valueRegistry;
        CodecPrefixRegistry prefixReg = prefixRegistry;
        return new CodecResource(
        uri, metadataService,
        ConfigurationResolver.defaults(),
        reg != null ? reg.copy() : null,
        prefixReg != null ? prefixReg.copy() : null,
        null,
        provider,
        null);
    }

    private static boolean isZipUri(URI uri) {
        return uri != null && ZIP_EXTENSION.equalsIgnoreCase(uri.fileExtension());
    }
}
