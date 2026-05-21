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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.eclipse.fennec.codec.tabular.TabularDocumentDelegate;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;

/**
 * {@link CodecFormatProvider} for CSV.
 * <p>
 * Writer-only for now: {@link #createReader(InputStream)} throws
 * {@link UnsupportedOperationException}.
 * <p>
 * Three reference modes via
 * {@link CodecTabularOptions#OPTION_REFERENCE_MODE}:
 * <ul>
 *   <li>{@link ReferenceMode#IGNORE} (default) — single CSV, attributes only.
 *       Driven by the Jackson serialization pipeline through
 *       {@link CsvFormatDelegate} so that custom value writers and other
 *       Jackson-pipeline mechanisms continue to apply.</li>
 *   <li>{@link ReferenceMode#FLAT} — single CSV with dotted column names
 *       (references flattened). Driven by the shared
 *       {@link org.eclipse.fennec.codec.tabular.TabularDocumentBuilder} via
 *       {@link TabularDocumentDelegate} + {@link CsvRenderer}.</li>
 *   <li>{@link ReferenceMode#SQL_TABLES} — ZIP of per-{@code EClass} CSVs with
 *       FK columns and optional join tables. Same shared infrastructure.</li>
 * </ul>
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
        return createWriter(target,
                rootObject == null ? List.<EObject>of() : List.of(rootObject),
                saveOptions, null);
    }

    @Override
    public FormatDelegate<OutputStream> createWriter(OutputStream target,
            List<? extends EObject> rootObjects, Map<String, Object> saveOptions) {
        return createWriter(target, rootObjects, saveOptions, null);
    }

    @Override
    public FormatDelegate<OutputStream> createWriter(OutputStream target,
            List<? extends EObject> rootObjects, Map<String, Object> saveOptions,
            ConfigurationResolver resolver) {

        List<? extends EObject> roots = rootObjects != null ? rootObjects : List.<EObject>of();
        EObject first = roots.isEmpty() ? null : roots.get(0);
        EClass effectiveEClass = rootEClass != null
                ? rootEClass
                : (first != null ? first.eClass() : null);

        Map<String, Object> effectiveOptions;
        if (saveOptions == null || saveOptions.isEmpty()) {
            effectiveOptions = options;
        } else if (options.isEmpty()) {
            effectiveOptions = saveOptions;
        } else {
            effectiveOptions = new HashMap<>(options);
            effectiveOptions.putAll(saveOptions);
        }

        ReferenceMode mode = resolveReferenceMode(effectiveOptions);
        if (mode == ReferenceMode.SQL_TABLES || mode == ReferenceMode.FLAT) {
            return new TabularDocumentDelegate<>(target, roots, effectiveOptions, resolver,
                    new CsvRenderer());
        }
        // IGNORE mode: stay on the Jackson pipeline (preserves custom value writers).
        return new CsvFormatDelegate(target, effectiveEClass, effectiveOptions);
    }

    private static ReferenceMode resolveReferenceMode(Map<String, Object> options) {
        Object value = options.get(CodecTabularOptions.OPTION_REFERENCE_MODE);
        if (value instanceof ReferenceMode m) {
            return m;
        }
        if (value instanceof String s && !s.isBlank()) {
            return ReferenceMode.valueOf(s.toUpperCase());
        }
        return ReferenceMode.IGNORE;
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
        return true;
    }
}
