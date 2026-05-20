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
package org.eclipse.fennec.codec.csv;

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
 * {@code csv} file extension, backed by {@link CsvFormatProvider}.
 * <p>
 * Once this component is active, any {@code ResourceSetImpl} backed by the
 * Fennec EMF whiteboard will automatically create a {@link CodecResource}
 * with the CSV format provider when given a URI ending in {@code .csv}.
 * <p>
 * The CSV provider is instantiated stateless (no root {@code EClass} bound at
 * construction); the root {@code EClass} is auto-discovered from the resource
 * contents at save time via the {@link org.eclipse.fennec.codec.format.CodecFormatProvider#createWriter(Object, org.eclipse.emf.ecore.EObject, java.util.Map)
 * createWriter(target, rootObject, saveOptions)} overload.
 *
 * @since 2026-05
 */
@Component(
        name = "CsvResourceFactory",
        service = Resource.Factory.class,
        property = {
                EMFNamespaces.EMF_MODEL_FILE_EXT + "=csv",
                EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=text/csv"
        })
public class CsvResourceFactoryComponent extends ResourceFactoryImpl {

    private final MetadataService metadataService;
    private final CsvFormatProvider formatProvider = new CsvFormatProvider();

    @Activate
    public CsvResourceFactoryComponent(@Reference MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public Resource createResource(URI uri) {
        return new CodecResource(
                uri, metadataService,
                ConfigurationResolver.defaults(),
                null, null,
                formatProvider);
    }
}
