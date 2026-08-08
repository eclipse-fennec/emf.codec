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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.tabular.TabularDocumentDelegate;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;

/**
 * {@link CodecFormatProvider} for R Language ({@code .RData} / {@code .rdataz}).
 * <p>
 * Writer-only: {@link #createReader(InputStream)} throws
 * {@link UnsupportedOperationException}. All {@link ReferenceMode}s flow
 * through {@link TabularDocumentDelegate} + {@link RLangRenderer}.
 *
 * @since 1.0
 */
public class RLangFormatProvider implements CodecFormatProvider<InputStream, OutputStream> {

    private final EClass rootEClass;
    private final Map<String, Object> options;

    public RLangFormatProvider() {
        this(null, Collections.emptyMap());
    }

    public RLangFormatProvider(EClass rootEClass) {
        this(rootEClass, Collections.emptyMap());
    }

    public RLangFormatProvider(EClass rootEClass, Map<String, Object> options) {
        this.rootEClass = rootEClass;
        this.options = options != null ? options : Collections.emptyMap();
    }

    @Override
    public String getFormatId() {
        return "rlang";
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
                new RLangRenderer());
    }

    @Override
    public FormatReaderDelegate<InputStream> createReader(InputStream source) {
        throw new UnsupportedOperationException("RData reading is not supported");
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] { "RData", "rdataz" };
    }

    @Override
    public String[] getContentTypes() {
        return new String[] {
                "application/x-rdata",
                "application/x-rdata-zip"
        };
    }

    @Override
    public boolean supportsArrayRoot() {
        return true;
    }

    public EClass getRootEClass() {
        return rootEClass;
    }

    @Override
    public List<String> validateSaveOptions(URI uri, Map<String, Object> saveOptions) {
        if (uri == null) {
            return List.of();
        }
        String ext = uri.fileExtension();
        if (ext == null) {
            return List.of();
        }
        boolean zipMode = resolveBoolean(saveOptions,
                CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, false);
        if (zipMode && "RData".equalsIgnoreCase(ext)) {
            return List.of(
                    "codec.rlang.dataframePerFile=true produces a ZIP archive, but the "
                  + "resource URI ends in .RData — consider using .rdataz or setting the "
                  + "option to false.");
        }
        if (!zipMode && "rdataz".equalsIgnoreCase(ext)) {
            return List.of(
                    "URI ends in .rdataz (ZIP shape) but codec.rlang.dataframePerFile=false "
                  + "— the output will be a single RData stream inside a file named .rdataz.");
        }
        return List.of();
    }

    private static boolean resolveBoolean(Map<String, Object> options, String key,
            boolean defaultValue) {
        Object value = options.get(key);
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s && !s.isBlank()) {
            return Boolean.parseBoolean(s);
        }
        return defaultValue;
    }
}
