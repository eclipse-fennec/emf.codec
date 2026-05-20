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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;

/**
 * {@link CodecFormatProvider} for CSV.
 * <p>
 * Writer-only for now: {@link #createReader(InputStream)} throws
 * {@link UnsupportedOperationException}.
 * <p>
 * The provider needs the root {@link EClass} to populate the SQL-type row from
 * the default EMF -&gt; SQL mapping ({@link SqlTypeMapper}). If no {@code EClass}
 * is supplied, every column's type must be provided explicitly via the
 * {@link CodecCsvOptions#OPTION_COLUMN_TYPES} option, otherwise the fallback
 * {@code VARCHAR} is emitted.
 * <p>
 * Usage:
 * <pre>
 * CsvFormatProvider csv = new CsvFormatProvider(person.eClass());
 * CodecResource resource = new CodecResource(uri, metadataService,
 *         resolver, null, null, csv);
 * resource.getContents().add(person);
 * resource.save(outputStream, options);
 * </pre>
 *
 * @since 2026-05
 */
public class CsvFormatProvider implements CodecFormatProvider<InputStream, OutputStream> {

    private final EClass rootEClass;
    private final Map<String, Object> options;

    public CsvFormatProvider() {
        this(null, Collections.emptyMap());
    }

    public CsvFormatProvider(EClass rootEClass) {
        this(rootEClass, Collections.emptyMap());
    }

    public CsvFormatProvider(EClass rootEClass, Map<String, Object> options) {
        this.rootEClass = rootEClass;
        this.options = options != null ? options : Collections.emptyMap();
    }

    @Override
    public String getFormatId() {
        return "csv";
    }

    @Override
    public FormatDelegate<OutputStream> createWriter(OutputStream target) {
        return new CsvFormatDelegate(target, rootEClass, options);
    }

    @Override
    public FormatDelegate<OutputStream> createWriter(OutputStream target, EObject rootObject,
            Map<String, Object> saveOptions) {
        EClass effectiveEClass = rootEClass != null
                ? rootEClass
                : (rootObject != null ? rootObject.eClass() : null);

        Map<String, Object> effectiveOptions;
        if (saveOptions == null || saveOptions.isEmpty()) {
            effectiveOptions = options;
        } else if (options.isEmpty()) {
            effectiveOptions = saveOptions;
        } else {
            effectiveOptions = new HashMap<>(options);
            effectiveOptions.putAll(saveOptions);
        }

        return new CsvFormatDelegate(target, effectiveEClass, effectiveOptions);
    }

    @Override
    public FormatReaderDelegate<InputStream> createReader(InputStream source) {
        throw new UnsupportedOperationException("CSV reading is not supported yet");
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] { "csv" };
    }

    @Override
    public String[] getContentTypes() {
        return new String[] { "text/csv" };
    }

    @Override
    public boolean supportsArrayRoot() {
        return false;
    }
}
