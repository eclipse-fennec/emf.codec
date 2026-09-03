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
package org.eclipse.fennec.codec.ods.internal;

import org.eclipse.fennec.codec.ods.OdsFormatProvider;

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
 * OSGi DS component that registers a {@link Resource.Factory} for the {@code .ods}
 * file extension, backed by {@link OdsFormatProvider}.
 *
 * @since 1.0
 */
@Component(
        name = "OdsResourceFactory",
        service = Resource.Factory.class,
        property = {
                EMFNamespaces.EMF_MODEL_FILE_EXT + "=ods",
                EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/vnd.oasis.opendocument.spreadsheet"
        })
public class OdsResourceFactoryComponent extends ResourceFactoryImpl {

    private final MetadataService metadataService;
    private volatile CodecValueRegistry valueRegistry;

    @Activate
    public OdsResourceFactoryComponent(@Reference MetadataService metadataService) {
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
        CodecValueRegistry reg = valueRegistry;
        CodecPrefixRegistry prefixReg = prefixRegistry;
        return new CodecResource(
        uri, metadataService,
        ConfigurationResolver.defaults(),
        reg != null ? reg.copy() : null,
        prefixReg != null ? prefixReg.copy() : null,
        null,
        new OdsFormatProvider(),
        null);
    }
}
