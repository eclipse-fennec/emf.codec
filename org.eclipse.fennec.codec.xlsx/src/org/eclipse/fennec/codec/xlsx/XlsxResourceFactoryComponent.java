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
package org.eclipse.fennec.codec.xlsx;

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
 * OSGi DS component that registers a {@link Resource.Factory} for the
 * {@code .xlsx} file extension, backed by {@link XlsxFormatProvider}.
 *
 * @since 2026-06
 */
@Component(
        name = "XlsxResourceFactory",
        service = Resource.Factory.class,
        property = {
                EMFNamespaces.EMF_MODEL_FILE_EXT + "=xlsx",
                EMFNamespaces.EMF_MODEL_CONTENT_TYPE
                        + "=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        })
public class XlsxResourceFactoryComponent extends ResourceFactoryImpl {

    private final MetadataService metadataService;

    @Activate
    public XlsxResourceFactoryComponent(@Reference MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public Resource createResource(URI uri) {
        return new CodecResource(
                uri, metadataService,
                ConfigurationResolver.defaults(),
                null, null,
                new XlsxFormatProvider());
    }
}
