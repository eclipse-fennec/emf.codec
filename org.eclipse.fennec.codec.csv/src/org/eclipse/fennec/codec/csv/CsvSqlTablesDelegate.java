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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.format.FormatDelegate;

import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import de.siegmar.fastcsv.writer.QuoteStrategies;

/**
 * CSV writer delegate that produces a ZIP archive containing one CSV per visited
 * {@code EClass} — the layout suitable for relational-DB import (one table per
 * EClass, foreign-key columns named {@code <refname>_id}, pseudo {@code _id}
 * primary key).
 * <p>
 * Unlike {@link CsvFormatDelegate}, this delegate <em>does not</em> participate
 * in the Jackson-driven streaming pipeline: all the {@code writeXxx} methods are
 * no-ops. The full export happens on {@link #close()} via a direct EMF graph
 * walk from the root {@code EObject}, mirroring the approach the legacy gecko
 * {@code EMFCSVExporter} takes.
 * <p>
 * Scope (MVP):
 * <ul>
 *   <li>Single root {@code EObject}.</li>
 *   <li>Single-valued {@code EReference}s — containment or non-containment — are
 *       emitted as a foreign-key column on the source table.</li>
 *   <li>Multi-valued {@code EReference}s are <em>not yet</em> supported and are
 *       silently skipped (a {@code FINE} log line is emitted).</li>
 *   <li>Multi-valued {@code EAttribute}s are joined with {@code ';'} in a single
 *       cell (same convention as {@link CsvFormatDelegate}).</li>
 * </ul>
 * <p>
 * The delegate writes the ZIP to the supplied {@code OutputStream} but never
 * closes it — stream ownership stays with the caller.
 *
 * @since 2026-05
 */
public class CsvSqlTablesDelegate implements FormatDelegate<OutputStream> {

    private static final Logger LOGGER = Logger.getLogger(CsvSqlTablesDelegate.class.getName());

    private static final String PK_COLUMN = "_id";
    private static final String FK_COLUMN_SUFFIX = "_id";
    private static final String PK_FK_TYPE = "BIGINT";
    private static final String ARRAY_VALUE_SEPARATOR = ";";

    private OutputStream target;
    private final EObject rootObject;
    private final Map<String, Object> options;

    private boolean emitted;

    public CsvSqlTablesDelegate(OutputStream target, EObject rootObject, Map<String, Object> options) {
        this.target = target;
        this.rootObject = rootObject;
        this.options = options != null ? options : Collections.emptyMap();
    }

    // ========================================================================
    // FormatDelegate API — all writes are no-ops; emit happens on close().
    // ========================================================================

    @Override public void setTarget(OutputStream target) { this.target = target; }
    @Override public OutputStream getTarget() { return target; }

    @Override public void writeStartObject() throws IOException { /* no-op */ }
    @Override public void writeEndObject() throws IOException { /* no-op */ }
    @Override public void writeStartArray() throws IOException { /* no-op */ }
    @Override public void writeEndArray() throws IOException { /* no-op */ }
    @Override public void writeName(String name) throws IOException { /* no-op */ }
    @Override public void writeString(String value) throws IOException { /* no-op */ }
    @Override public void writeInt(int value) throws IOException { /* no-op */ }
    @Override public void writeLong(long value) throws IOException { /* no-op */ }
    @Override public void writeFloat(float value) throws IOException { /* no-op */ }
    @Override public void writeDouble(double value) throws IOException { /* no-op */ }
    @Override public void writeBigInteger(BigInteger value) throws IOException { /* no-op */ }
    @Override public void writeBigDecimal(BigDecimal value) throws IOException { /* no-op */ }
    @Override public void writeBoolean(boolean value) throws IOException { /* no-op */ }
    @Override public void writeNull() throws IOException { /* no-op */ }
    @Override public void writeBinary(byte[] data) throws IOException { /* no-op */ }

    @Override
    public void flush() throws IOException {
        if (target != null) {
            target.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (emitted) {
            return;
        }
        emitted = true;
        if (rootObject == null) {
            LOGGER.fine("CSV SQL_TABLES: no root object — nothing to emit");
            return;
        }
        emit();
        if (target != null) {
            target.flush();
        }
    }

    // ========================================================================
    // Graph walk
    // ========================================================================

    private static final class WalkResult {
        final Map<EClass, List<EObject>> byClass = new LinkedHashMap<>();
        final IdentityHashMap<EObject, Long> pseudoIds = new IdentityHashMap<>();
    }

    private WalkResult walkGraph(EObject root) {
        WalkResult result = new WalkResult();
        Set<EObject> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<EObject> queue = new ArrayDeque<>();
        Map<EClass, Long> counters = new HashMap<>();

        queue.add(root);
        while (!queue.isEmpty()) {
            EObject obj = queue.poll();
            if (obj == null || visited.contains(obj)) {
                continue;
            }
            visited.add(obj);

            EClass eClass = obj.eClass();
            result.byClass.computeIfAbsent(eClass, k -> new ArrayList<>()).add(obj);
            long id = counters.merge(eClass, 1L, Long::sum);
            result.pseudoIds.put(obj, id);

            for (EReference ref : eClass.getEAllReferences()) {
                Object value = obj.eGet(ref);
                if (value == null) {
                    continue;
                }
                if (ref.isMany()) {
                    if (value instanceof List<?> list) {
                        for (Object item : list) {
                            if (item instanceof EObject child) {
                                queue.add(child);
                            }
                        }
                    }
                } else if (value instanceof EObject child) {
                    queue.add(child);
                }
            }
        }
        return result;
    }

    // ========================================================================
    // Emit
    // ========================================================================

    private void emit() throws IOException {
        WalkResult walk = walkGraph(rootObject);

        Charset charset = resolveCharset();
        Map<?, ?> columnTypeOverrides = optionAsMap(CodecCsvOptions.OPTION_COLUMN_TYPES);

        ZipOutputStream zip = new ZipOutputStream(target);
        for (Map.Entry<EClass, List<EObject>> entry : walk.byClass.entrySet()) {
            emitCsv(zip, entry.getKey(), entry.getValue(), walk.pseudoIds, columnTypeOverrides, charset);
        }
        // Write the ZIP's central directory without closing the underlying OutputStream.
        zip.finish();
    }

    private void emitCsv(ZipOutputStream zip, EClass eClass, List<EObject> objects,
            IdentityHashMap<EObject, Long> pseudoIds, Map<?, ?> columnTypeOverrides, Charset charset)
            throws IOException {

        List<String> headers = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<EAttribute> attrColumns = new ArrayList<>();
        List<EReference> fkColumns = new ArrayList<>();

        headers.add(PK_COLUMN);
        types.add(PK_FK_TYPE);

        for (EStructuralFeature feat : eClass.getEAllStructuralFeatures()) {
            if (feat instanceof EAttribute attr) {
                headers.add(attr.getName());
                types.add(resolveAttributeType(attr, columnTypeOverrides));
                attrColumns.add(attr);
            } else if (feat instanceof EReference ref) {
                if (ref.isMany()) {
                    LOGGER.fine(() -> "CSV SQL_TABLES: skipping multi-valued reference "
                            + eClass.getName() + "." + ref.getName()
                            + " (mapping-table support not yet implemented)");
                    continue;
                }
                headers.add(ref.getName() + FK_COLUMN_SUFFIX);
                types.add(PK_FK_TYPE);
                fkColumns.add(ref);
            }
        }

        zip.putNextEntry(new ZipEntry(eClass.getName() + ".csv"));
        // Suppress close of the underlying stream — CsvWriter.close() would otherwise close
        // the ZipOutputStream's entry stream, which is not what we want.
        OutputStream guarded = new FilterOutputStream(zip) {
            @Override public void close() { /* deliberate no-op */ }
        };
        CsvWriter csv = CsvWriter.builder()
                .fieldSeparator(',')
                .quoteStrategy(QuoteStrategies.REQUIRED)
                .lineDelimiter(LineDelimiter.LF)
                .build(guarded, charset);

        csv.writeRecord(headers);
        csv.writeRecord(types);
        for (EObject obj : objects) {
            List<String> row = new ArrayList<>(headers.size());
            row.add(Long.toString(pseudoIds.get(obj)));
            for (EAttribute attr : attrColumns) {
                row.add(formatAttributeValue(obj, attr));
            }
            for (EReference ref : fkColumns) {
                Object value = obj.eGet(ref);
                if (value instanceof EObject child) {
                    Long fkId = pseudoIds.get(child);
                    row.add(fkId != null ? Long.toString(fkId) : "");
                } else {
                    row.add("");
                }
            }
            csv.writeRecord(row);
        }
        csv.flush();
        zip.closeEntry();
    }

    // ========================================================================
    // Type and value resolution
    // ========================================================================

    private String resolveAttributeType(EAttribute attr, Map<?, ?> overrides) {
        if (overrides != null) {
            // Prefer EStructuralFeature key (unambiguous across EClasses)
            Object byFeature = overrides.get(attr);
            if (byFeature instanceof String s && !s.isBlank()) {
                return s;
            }
            // Fall back to feature-name key (single-EClass case)
            Object byName = overrides.get(attr.getName());
            if (byName instanceof String s && !s.isBlank()) {
                return s;
            }
        }
        return SqlTypeMapper.toSqlType(attr.getEType());
    }

    private String formatAttributeValue(EObject obj, EAttribute attr) {
        Object value = obj.eGet(attr);
        if (value == null) {
            return "";
        }
        if (attr.isMany() && value instanceof List<?> list) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Object item : list) {
                if (!first) {
                    sb.append(ARRAY_VALUE_SEPARATOR);
                }
                sb.append(formatScalar(item));
                first = false;
            }
            return sb.toString();
        }
        return formatScalar(value);
    }

    private String formatScalar(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal bd) {
            return bd.toPlainString();
        }
        if (value instanceof byte[] bytes) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        if (value instanceof Date date) {
            return date.toString();
        }
        return value.toString();
    }

    // ========================================================================
    // Options
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

    private Map<?, ?> optionAsMap(String key) {
        Object value = options.get(key);
        return value instanceof Map<?, ?> map ? map : null;
    }
}
