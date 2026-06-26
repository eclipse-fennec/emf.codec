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

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * OSGi DS component that registers a {@link Resource.Factory} for CSV-style
 * file extensions, backed by {@link CsvFormatProvider}.
 * <p>
 * Two extensions are supported:
 * <ul>
 *   <li>{@code .csv} — single-{@code EObject} output (default reference mode
 *       {@link ReferenceMode#IGNORE IGNORE}).</li>
 *   <li>{@code .csvz} — multi-table ZIP output, one CSV per visited
 *       {@code EClass} (default reference mode
 *       {@link ReferenceMode#SQL_TABLES SQL_TABLES}).</li>
 * </ul>
 * <p>
 * The caller can override the mode at save time by passing
 * {@link CodecTabularOptions#OPTION_REFERENCE_MODE} in the save-options map.
 * <p>
 * Note on {@code .csv.zip} URIs: EMF's {@code URI.fileExtension()} returns only
 * the segment after the last dot, so a file named {@code out.csv.zip} would
 * resolve to extension {@code zip}, not {@code csv.zip}. Use {@code .csvz} (or
 * construct the resource manually with a {@link CsvFormatProvider} pre-configured
 * for SQL_TABLES) if you need a different file name.
 *
 * @since 2026-05
 */
@Component(
        name = "CsvResourceFactory",
        service = Resource.Factory.class,
        property = {
                EMFNamespaces.EMF_MODEL_FILE_EXT + "=csv",
                EMFNamespaces.EMF_MODEL_FILE_EXT + "=csvz",
                EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=text/csv",
                EMFNamespaces.EMF_MODEL_CONTENT_TYPE + "=application/x-csv-zip"
        })
public class CsvResourceFactoryComponent extends ResourceFactoryImpl {

    private static final String SQL_TABLES_EXTENSION = "csvz";

    private final MetadataService metadataService;
    private volatile CodecValueRegistry valueRegistry;

    @Activate
    public CsvResourceFactoryComponent(@Reference MetadataService metadataService) {
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

    @Override
    public Resource createResource(URI uri) {
        CsvFormatProvider provider = isSqlTablesUri(uri)
                ? new CsvFormatProvider(null,
                        Map.of(CodecTabularOptions.OPTION_REFERENCE_MODE,
                                ReferenceMode.SQL_TABLES))
                : new CsvFormatProvider();
        CodecValueRegistry reg = valueRegistry;
        return new CodecResource(
                uri, metadataService,
                ConfigurationResolver.defaults(),
                reg != null ? reg.copy() : null, null,
                provider);
    }

    private static boolean isSqlTablesUri(URI uri) {
        return uri != null && SQL_TABLES_EXTENSION.equalsIgnoreCase(uri.fileExtension());
    }
}
