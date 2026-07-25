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
package org.eclipse.fennec.codec.ods;

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
import org.eclipse.fennec.codec.tabular.TabularDocumentDelegate;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;

/**
 * {@link CodecFormatProvider} for ODS (OpenDocument Spreadsheet).
 * <p>
 * Writer-only: {@link #createReader(InputStream)} throws
 * {@link UnsupportedOperationException}.
 * <p>
 * All three {@link ReferenceMode}s flow through the shared tabular pipeline
 * ({@link TabularDocumentDelegate} + {@link OdsRenderer}) — unlike CSV's IGNORE
 * mode, ODS has no need to preserve a Jackson-stream path for custom value
 * writers, because spreadsheet cells carry their type natively.
 *
 * @since 2026-06
 */
public class OdsFormatProvider implements CodecFormatProvider<InputStream, OutputStream> {

    private final EClass rootEClass;
    private final Map<String, Object> options;

    public OdsFormatProvider() {
        this(null, Collections.emptyMap());
    }

    public OdsFormatProvider(EClass rootEClass) {
        this(rootEClass, Collections.emptyMap());
    }

    public OdsFormatProvider(EClass rootEClass, Map<String, Object> options) {
        this.rootEClass = rootEClass;
        this.options = options != null ? options : Collections.emptyMap();
    }

    @Override
    public String getFormatId() {
        return "ods";
    }

    @Override
    public FormatDelegate<OutputStream> createWriter(OutputStream target) {
        return createWriter(target, List.<EObject>of(), Collections.emptyMap(), null);
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

        Map<String, Object> effectiveOptions;
        if (saveOptions == null || saveOptions.isEmpty()) {
            effectiveOptions = options;
        } else if (options.isEmpty()) {
            effectiveOptions = saveOptions;
        } else {
            effectiveOptions = new HashMap<>(options);
            effectiveOptions.putAll(saveOptions);
        }

        return new TabularDocumentDelegate<>(target, roots, effectiveOptions, resolver,
                new OdsRenderer());
    }

    @Override
    public FormatReaderDelegate<InputStream> createReader(InputStream source) {
        throw new UnsupportedOperationException("ODS reading is not supported yet");
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] { "ods" };
    }

    @Override
    public String[] getContentTypes() {
        return new String[] { "application/vnd.oasis.opendocument.spreadsheet" };
    }

    /**
     * {@inheritDoc}
     * <p>
     * A column format has no in-band fingerprint carrier (issue #73, S7): the value would have
     * to become a column repeated on every row, and there is no per-object type context to
     * attach it to. Callers needing version selection for ODS use
     * {@code codec.rootFingerprint} instead.
     */
    @Override
    public boolean supportsInBandFingerprint() {
        return false;
    }

    @Override
    public boolean supportsArrayRoot() {
        return true;
    }

    /** Exposed for the resource-factory component (root EClass not needed). */
    public EClass getRootEClass() {
        return rootEClass;
    }
}
