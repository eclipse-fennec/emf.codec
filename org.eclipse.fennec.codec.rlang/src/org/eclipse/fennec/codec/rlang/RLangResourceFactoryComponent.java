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
package org.eclipse.fennec.codec.rlang;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
 * @since 2026-06
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

    @Activate
    public RLangResourceFactoryComponent(@Reference MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public Resource createResource(URI uri) {
        RLangFormatProvider provider = isZipUri(uri)
                ? new RLangFormatProvider(null,
                        Map.of(CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE))
                : new RLangFormatProvider();
        return new CodecResource(
                uri, metadataService,
                ConfigurationResolver.defaults(),
                null, null,
                provider);
    }

    private static boolean isZipUri(URI uri) {
        return uri != null && ZIP_EXTENSION.equalsIgnoreCase(uri.fileExtension());
    }
}
