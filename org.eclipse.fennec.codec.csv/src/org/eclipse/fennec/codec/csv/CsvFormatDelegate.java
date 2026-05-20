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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.util.AnnotationHelper;

import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import de.siegmar.fastcsv.writer.QuoteStrategies;
import de.siegmar.fastcsv.writer.QuoteStrategy;

/**
 * CSV writer delegate.
 * <p>
 * Buffers the (column-name, value) pairs emitted by the Jackson-style writer
 * pipeline and, on {@link #close()}, produces a three-row CSV: a header row of
 * column names, a type row of SQL types, and a single data row of values.
 * <p>
 * Scope (MVP):
 * <ul>
 *   <li>Single root {@code EObject}.</li>
 *   <li>{@code EAttribute}s only. Nested {@code EObject}s (contained or referenced) are
 *       silently skipped.</li>
 *   <li>Multi-valued {@code EAttribute}s are joined with {@code ';'} in a single cell.</li>
 * </ul>
 * <p>
 * The delegate writes to (and flushes) the underlying {@code OutputStream} but
 * never closes it — stream ownership stays with the caller.
 *
 * @since 2026-05
 */
public class CsvFormatDelegate implements FormatDelegate<OutputStream> {

    private static final Logger LOGGER = Logger.getLogger(CsvFormatDelegate.class.getName());

    private static final char DEFAULT_DELIMITER = ',';
    private static final QuoteStrategy DEFAULT_QUOTE_STRATEGY = QuoteStrategies.REQUIRED;
    private static final LineDelimiter DEFAULT_LINE_DELIMITER = LineDelimiter.LF;
    private static final String ARRAY_VALUE_SEPARATOR = ";";

    private OutputStream target;
    private final EClass rootEClass;
    private final Map<String, Object> options;

    private final LinkedHashMap<String, String> row = new LinkedHashMap<>();
    private String currentName;
    private int objectDepth;
    private int arrayDepth;
    private List<String> pendingArrayValues;
    private boolean emitted;

    public CsvFormatDelegate(OutputStream target, EClass rootEClass, Map<String, Object> options) {
        this.target = target;
        this.rootEClass = rootEClass;
        this.options = options != null ? options : Collections.emptyMap();
    }

    // ========================================================================
    // Target
    // ========================================================================

    @Override
    public void setTarget(OutputStream target) {
        this.target = target;
    }

    @Override
    public OutputStream getTarget() {
        return target;
    }

    // ========================================================================
    // Object / Array structure
    // ========================================================================

    @Override
    public void writeStartObject() throws IOException {
        objectDepth++;
        if (objectDepth > 1) {
            LOGGER.fine(() -> "CSV: skipping nested object at depth " + objectDepth);
        }
    }

    @Override
    public void writeEndObject() throws IOException {
        if (objectDepth > 0) {
            objectDepth--;
        }
    }

    @Override
    public void writeStartArray() throws IOException {
        arrayDepth++;
        if (arrayDepth == 1 && objectDepth == 1) {
            pendingArrayValues = new ArrayList<>();
        }
    }

    @Override
    public void writeEndArray() throws IOException {
        if (arrayDepth == 1 && objectDepth == 1 && pendingArrayValues != null && currentName != null) {
            row.put(currentName, String.join(ARRAY_VALUE_SEPARATOR, pendingArrayValues));
            pendingArrayValues = null;
            currentName = null;
        }
        if (arrayDepth > 0) {
            arrayDepth--;
        }
    }

    // ========================================================================
    // Name + values
    // ========================================================================

    @Override
    public void writeName(String name) throws IOException {
        if (objectDepth == 1 && arrayDepth == 0) {
            currentName = name;
        }
    }

    @Override
    public void writeString(String value) throws IOException {
        putValue(value == null ? "" : value);
    }

    @Override
    public void writeInt(int value) throws IOException {
        putValue(Integer.toString(value));
    }

    @Override
    public void writeLong(long value) throws IOException {
        putValue(Long.toString(value));
    }

    @Override
    public void writeFloat(float value) throws IOException {
        putValue(Float.toString(value));
    }

    @Override
    public void writeDouble(double value) throws IOException {
        putValue(Double.toString(value));
    }

    @Override
    public void writeBigInteger(BigInteger value) throws IOException {
        putValue(value == null ? "" : value.toString());
    }

    @Override
    public void writeBigDecimal(BigDecimal value) throws IOException {
        putValue(value == null ? "" : value.toPlainString());
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        putValue(Boolean.toString(value));
    }

    @Override
    public void writeNull() throws IOException {
        putValue("");
    }

    @Override
    public void writeBinary(byte[] data) throws IOException {
        putValue(data == null ? "" : Base64.getEncoder().encodeToString(data));
    }

    private void putValue(String value) {
        if (objectDepth != 1) {
            // Nested EObject or pre/post-document: ignore for MVP.
            return;
        }
        if (arrayDepth > 0) {
            if (pendingArrayValues != null) {
                pendingArrayValues.add(value);
            }
            return;
        }
        if (currentName != null) {
            row.put(currentName, value);
            currentName = null;
        }
    }

    // ========================================================================
    // Lifecycle
    // ========================================================================

    @Override
    public void flush() throws IOException {
        if (target != null) {
            target.flush();
        }
    }

    @Override
    public void close() throws IOException {
        emit();
        if (target != null) {
            target.flush();
        }
    }

    private void emit() throws IOException {
        if (emitted) {
            return;
        }
        emitted = true;

        Charset charset = resolveCharset();
        CsvWriter csv = CsvWriter.builder()
                .fieldSeparator(optionAsChar(CodecCsvOptions.OPTION_DELIMITER, DEFAULT_DELIMITER))
                .quoteStrategy(resolveQuoteStrategy())
                .lineDelimiter(resolveLineDelimiter())
                .build(target, charset);

        List<String> headers = new ArrayList<>(row.keySet());
        List<String> types = resolveTypes(headers);
        List<String> values = new ArrayList<>(headers.size());
        for (String h : headers) {
            values.add(row.getOrDefault(h, ""));
        }
        try {
            csv.writeRecord(headers);
            csv.writeRecord(types);
            csv.writeRecord(values);
            csv.flush();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
        // Intentionally not calling csv.close(): that would close the
        // underlying OutputStream, which is owned by the caller.
    }

    // ========================================================================
    // Type-row resolution
    // ========================================================================

    private List<String> resolveTypes(List<String> headers) {
        Map<?, ?> columnTypes = optionAsMap(CodecCsvOptions.OPTION_COLUMN_TYPES);
        List<String> resolved = new ArrayList<>(headers.size());
        for (String header : headers) {
            resolved.add(resolveTypeFor(header, columnTypes));
        }
        return resolved;
    }

    private String resolveTypeFor(String header, Map<?, ?> columnTypes) {
        EStructuralFeature feature = lookupFeature(header);
        if (columnTypes != null) {
            Object byName = columnTypes.get(header);
            if (byName instanceof String s && !s.isBlank()) {
                return s;
            }
            if (feature != null) {
                Object byFeature = columnTypes.get(feature);
                if (byFeature instanceof String s && !s.isBlank()) {
                    return s;
                }
            }
        }
        if (feature instanceof EAttribute attribute) {
            return SqlTypeMapper.toSqlType(attribute.getEType());
        }
        return SqlTypeMapper.toSqlType(null);
    }

    private EStructuralFeature lookupFeature(String columnName) {
        if (rootEClass == null || columnName == null) {
            return null;
        }
        EStructuralFeature direct = rootEClass.getEStructuralFeature(columnName);
        if (direct != null) {
            return direct;
        }
        // Handle useNamesFromExtendedMetaData via ExtendedMetaData "name" annotation.
        for (EStructuralFeature f : rootEClass.getEAllStructuralFeatures()) {
            String emdName = AnnotationHelper.getExtendedMetaDataName(f);
            if (columnName.equals(emdName)) {
                return f;
            }
        }
        return null;
    }

    // ========================================================================
    // Option resolution
    // ========================================================================

    private Charset resolveCharset() {
        Object value = options.get(CodecCsvOptions.OPTION_CHARSET);
        if (value instanceof Charset c) {
            return c;
        }
        if (value instanceof String s && !s.isBlank()) {
            return Charset.forName(s);
        }
        return StandardCharsets.UTF_8;
    }

    private QuoteStrategy resolveQuoteStrategy() {
        Object value = options.get(CodecCsvOptions.OPTION_QUOTE_MODE);
        if (value instanceof QuoteStrategy q) {
            return q;
        }
        if (value instanceof String s && !s.isBlank()) {
            return QuoteStrategies.valueOf(s.toUpperCase());
        }
        return DEFAULT_QUOTE_STRATEGY;
    }

    private LineDelimiter resolveLineDelimiter() {
        Object value = options.get(CodecCsvOptions.OPTION_LINE_ENDING);
        if (value instanceof LineDelimiter d) {
            return d;
        }
        if (value instanceof String s && !s.isBlank()) {
            return LineDelimiter.valueOf(s.toUpperCase());
        }
        return DEFAULT_LINE_DELIMITER;
    }

    private char optionAsChar(String key, char defaultValue) {
        Object value = options.get(key);
        if (value instanceof Character c) {
            return c;
        }
        if (value instanceof String s && s.length() == 1) {
            return s.charAt(0);
        }
        return defaultValue;
    }

    private Map<?, ?> optionAsMap(String key) {
        Object value = options.get(key);
        return value instanceof Map<?, ?> map ? map : null;
    }
}
