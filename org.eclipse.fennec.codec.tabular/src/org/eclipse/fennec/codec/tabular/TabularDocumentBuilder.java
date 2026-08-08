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
package org.eclipse.fennec.codec.tabular;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.config.IdConfig;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.EffectiveCodecConfig;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.json.JsonMapper;
import org.eclipse.fennec.codec.metadata.model.codec.EnumSerializationStrategy;
import org.eclipse.fennec.codec.tabular.model.tabular.BigDecimalCell;
import org.eclipse.fennec.codec.tabular.model.tabular.BinaryCell;
import org.eclipse.fennec.codec.tabular.model.tabular.BooleanCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Cell;
import org.eclipse.fennec.codec.tabular.model.tabular.Column;
import org.eclipse.fennec.codec.tabular.model.tabular.ColumnSource;
import org.eclipse.fennec.codec.tabular.model.tabular.DateCell;
import org.eclipse.fennec.codec.tabular.model.tabular.DoubleCell;
import org.eclipse.fennec.codec.tabular.model.tabular.FkCell;
import org.eclipse.fennec.codec.tabular.model.tabular.JoinTable;
import org.eclipse.fennec.codec.tabular.model.tabular.JoinTableRow;
import org.eclipse.fennec.codec.tabular.model.tabular.LongCell;
import org.eclipse.fennec.codec.tabular.model.tabular.MultiValuedRefStrategy;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tabular.model.tabular.Row;
import org.eclipse.fennec.codec.tabular.model.tabular.StringCell;
import org.eclipse.fennec.codec.tabular.model.tabular.Table;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularFactory;

/**
 * Builds an in-memory {@link TabularDocument} from a list of root {@link EObject}s,
 * codec save-options, and the operation {@link ConfigurationResolver}.
 * <p>
 * Behaviour is driven by {@link CodecTabularOptions#OPTION_REFERENCE_MODE}:
 * <ul>
 *   <li>{@link ReferenceMode#IGNORE} — single {@link Table} from the first root's
 *       {@code EClass}, attributes only. One row per root.</li>
 *   <li>{@link ReferenceMode#FLAT} — single {@code Table} with dotted column names
 *       (references flattened). One row per root, union-of-columns alignment.</li>
 *   <li>{@link ReferenceMode#SQL_TABLES} — per-{@code EClass} tables, FK columns on the
 *       parent for single-valued refs, FK-on-child or separate join-table CSV for
 *       multi-valued refs depending on
 *       {@link CodecTabularOptions#OPTION_MULTI_VALUED_REF_STRATEGY}.</li>
 * </ul>
 * <p>
 * All {@link FeatureConfig} resolution happens here: column keys (resolved via
 * {@code useNamesFromExtendedMetaData}, {@code @codec(key="...")}, etc.),
 * ignore/forceWrite gating, and {@code dateFormat} application. Renderers receive
 * already-resolved column names and typed {@link Cell}s.
 *
 * @since 1.0
 */
public final class TabularDocumentBuilder {

    private static final Logger LOGGER = Logger.getLogger(TabularDocumentBuilder.class.getName());
    private static final String ARRAY_VALUE_SEPARATOR = ";";
    private static final String PK_HEADER = "_id";
    private static final String PK_FK_SQL_TYPE = "BIGINT";
    private static final String DEFAULT_FK_COLUMN_SUFFIX = "_id";

    private TabularDocumentBuilder() {
        // static use
    }

    /**
     * Builds a {@link TabularDocument} from the given inputs. Never returns null;
     * an empty roots list yields a document with only the {@code referenceMode}
     * attribute populated.
     */
    public static TabularDocument build(List<? extends EObject> roots,
            Map<String, Object> options, ConfigurationResolver resolver) {

        Map<String, Object> opts = options != null ? options : Collections.emptyMap();
        List<? extends EObject> rootList = roots != null ? roots : Collections.emptyList();

        ReferenceMode mode = resolveReferenceMode(opts);
        TabularDocument doc = TabularFactory.eINSTANCE.createTabularDocument();
        doc.setReferenceMode(mode);

        DiagnosticCollector diagnostics = new DiagnosticCollector();

        CodecValueRegistry registry = opts.get(CodecOptions.INTERNAL_VALUE_REGISTRY) instanceof CodecValueRegistry r ? r : null;
        // Deprecated key, still honoured: this is its implementation, not a use of it.
        @SuppressWarnings({ "unchecked", "deprecation" })
        Map<EStructuralFeature, String> featureWriters = opts.get(CodecOptions.CODEC_FEATURE_VALUE_WRITERS) instanceof Map<?, ?> m
                ? (Map<EStructuralFeature, String>) m : null;

        switch (mode) {
            case IGNORE -> buildIgnore(rootList, opts, resolver, diagnostics, doc, registry, featureWriters);
            case FLAT -> buildFlat(rootList, opts, resolver, diagnostics, doc, registry, featureWriters);
            case SQL_TABLES -> buildSqlTables(rootList, opts, resolver, diagnostics, doc, registry, featureWriters);
        }
        return doc;
    }

    // ========================================================================
    // Mode: IGNORE — single Table from the first root's EClass, attributes only.
    // ========================================================================

    private static void buildIgnore(List<? extends EObject> roots, Map<String, Object> opts,
            ConfigurationResolver resolver, DiagnosticCollector diagnostics, TabularDocument doc,
            CodecValueRegistry registry, Map<EStructuralFeature, String> featureWriters) {

        if (roots.isEmpty()) {
            return;
        }
        EObject first = roots.get(0);
        if (first == null) {
            return;
        }
        EClass eClass = first.eClass();
        Map<?, ?> columnTypeOverrides = optionAsMap(opts, CodecTabularOptions.OPTION_COLUMN_TYPES);

        Table table = TabularFactory.eINSTANCE.createTable();
        table.setEClass(eClass);
        table.setName(eClass.getName());

        // Collect attribute columns (in EClass-declared order), filtered via FeatureConfig.
        List<EAttribute> attrColumns = new ArrayList<>();
        for (EStructuralFeature feat : eClass.getEAllStructuralFeatures()) {
            if (!(feat instanceof EAttribute attr)) {
                continue;
            }
            FeatureConfig fc = resolveFeatureConfig(feat, resolver, diagnostics);
            if (fc != null && !fc.shouldSerialize()) {
                continue;
            }
            // Value gate: include the column only if at least one root would populate it.
            if (!anyValueQualifies(roots, attr, fc)) {
                continue;
            }
            attrColumns.add(attr);
        }

        // Apply column ordering (alphabetical / idOnTop) before materializing columns and rows.
        EAttribute idAttr = eClass.getEIDAttribute();
        orderColumns(attrColumns,
                attr -> resolvedKey(attr, resolveFeatureConfig(attr, resolver, diagnostics)),
                attr -> attr == idAttr,
                resolveAlphabetical(opts), resolveIdOnTop(eClass, resolver, diagnostics));

        for (EAttribute attr : attrColumns) {
            FeatureConfig fc = resolveFeatureConfig(attr, resolver, diagnostics);
            String resolvedKey = resolvedKey(attr, fc);
            Column col = TabularFactory.eINSTANCE.createColumn();
            col.setHeader(resolvedKey);
            col.setSource(ColumnSource.ATTRIBUTE);
            col.setSqlType(resolveAttributeSqlType(attr, resolvedKey, columnTypeOverrides));
            col.setFeature(attr);
            table.getColumns().add(col);
        }

        // One row per root.
        for (EObject root : roots) {
            if (root == null) {
                continue;
            }
            Row row = TabularFactory.eINSTANCE.createRow();
            for (EAttribute attr : attrColumns) {
                FeatureConfig fc = resolveFeatureConfig(attr, resolver, diagnostics);
                row.getCells().add(passesValueGate(root, attr, fc)
                        ? makeAttributeCell(root, attr, fc, registry, featureWriters)
                        : TabularFactory.eINSTANCE.createEmptyCell());
            }
            table.getRows().add(row);
        }
        doc.getTables().add(table);
    }

    // ========================================================================
    // Mode: FLAT — single Table with dotted column names; one row per root.
    // ========================================================================

    private static void buildFlat(List<? extends EObject> roots, Map<String, Object> opts,
            ConfigurationResolver resolver, DiagnosticCollector diagnostics, TabularDocument doc,
            CodecValueRegistry registry, Map<EStructuralFeature, String> featureWriters) {

        Map<?, ?> columnTypeOverrides = optionAsMap(opts, CodecTabularOptions.OPTION_COLUMN_TYPES);

        // We don't know all columns up front (sparse alignment across rows). Build:
        //   - rowMaps[i] : column-name → Cell, for each root.
        //   - columnOrder: union of column names in first-appearance order.
        //   - columnAttributes: column-name → producing EAttribute (for SQL-type resolution).
        List<LinkedHashMap<String, Cell>> rowMaps = new ArrayList<>(roots.size());
        LinkedHashSet<String> columnOrder = new LinkedHashSet<>();
        Map<String, EAttribute> columnAttributes = new LinkedHashMap<>();

        for (EObject root : roots) {
            if (root == null) {
                continue;
            }
            LinkedHashMap<String, Cell> rowMap = new LinkedHashMap<>();
            Deque<EObject> pathStack = new ArrayDeque<>();
            flatten(root, "", rowMap, columnOrder, columnAttributes, pathStack, resolver, diagnostics, registry, featureWriters);
            rowMaps.add(rowMap);
        }

        // Materialize the Table.
        Table table = TabularFactory.eINSTANCE.createTable();
        EObject firstRoot = roots.isEmpty() ? null : roots.get(0);
        // FLAT's table has no canonical EClass — name borrowed from the first root, if any.
        if (firstRoot != null) {
            table.setName(firstRoot.eClass().getName());
        }

        // Apply column ordering (alphabetical / idOnTop). idOnTop floats the root EClass's
        // eID attribute (a top-level, unprefixed column) to the front.
        List<String> orderedHeaders = new ArrayList<>(columnOrder);
        String idHeader = null;
        if (firstRoot != null) {
            EAttribute idAttr = firstRoot.eClass().getEIDAttribute();
            if (idAttr != null) {
                idHeader = resolvedKey(idAttr, resolveFeatureConfig(idAttr, resolver, diagnostics));
            }
        }
        final String floatHeader = idHeader;
        boolean idOnTop = firstRoot != null
                && floatHeader != null
                && resolveIdOnTop(firstRoot.eClass(), resolver, diagnostics);
        orderColumns(orderedHeaders, h -> h, h -> h.equals(floatHeader),
                resolveAlphabetical(opts), idOnTop);

        for (String header : orderedHeaders) {
            EAttribute attr = columnAttributes.get(header);
            Column col = TabularFactory.eINSTANCE.createColumn();
            col.setHeader(header);
            col.setSource(ColumnSource.FLAT_NESTED);
            col.setSqlType(resolveColumnSqlType(attr, header, columnTypeOverrides));
            if (attr != null) {
                col.setFeature(attr);
            }
            table.getColumns().add(col);
        }
        for (LinkedHashMap<String, Cell> rowMap : rowMaps) {
            Row row = TabularFactory.eINSTANCE.createRow();
            for (String header : orderedHeaders) {
                Cell cell = rowMap.get(header);
                row.getCells().add(cell != null ? cell : TabularFactory.eINSTANCE.createEmptyCell());
            }
            table.getRows().add(row);
        }
        doc.getTables().add(table);
    }

    /** Recursive flatten walk with path-stack cycle protection. */
    private static void flatten(EObject obj, String prefix,
            LinkedHashMap<String, Cell> rowMap, LinkedHashSet<String> columnOrder,
            Map<String, EAttribute> columnAttributes, Deque<EObject> path,
            ConfigurationResolver resolver, DiagnosticCollector diagnostics,
            CodecValueRegistry registry, Map<EStructuralFeature, String> featureWriters) {

        // Identity-based cycle guard.
        for (EObject ancestor : path) {
            if (ancestor == obj) {
                LOGGER.fine(() -> "TabularDocumentBuilder FLAT: cycle at "
                        + obj.eClass().getName() + " (prefix=" + prefix + "), skipping");
                return;
            }
        }
        path.push(obj);
        try {
            EClass eClass = obj.eClass();
            for (EStructuralFeature feat : eClass.getEAllStructuralFeatures()) {
                FeatureConfig fc = resolveFeatureConfig(feat, resolver, diagnostics);
                if (fc != null && !fc.shouldSerialize()) {
                    continue;
                }
                String resolvedKey = resolvedKey(feat, fc);

                if (feat instanceof EAttribute attr) {
                    // Value gate: a gated-out value contributes no column; rows lacking the
                    // column are filled with EmptyCell at materialization time.
                    if (!passesValueGate(obj, attr, fc)) {
                        continue;
                    }
                    String column = prefix + resolvedKey;
                    rowMap.put(column, makeAttributeCell(obj, attr, fc, registry, featureWriters));
                    columnOrder.add(column);
                    columnAttributes.putIfAbsent(column, attr);
                } else if (feat instanceof EReference ref) {
                    Object value = obj.eGet(ref);
                    if (value == null) {
                        continue;
                    }
                    if (ref.isMany()) {
                        if (value instanceof List<?> list) {
                            int idx = 0;
                            for (Object item : list) {
                                if (item instanceof EObject child) {
                                    flatten(child, prefix + resolvedKey + "." + idx + ".",
                                            rowMap, columnOrder, columnAttributes, path,
                                            resolver, diagnostics, registry, featureWriters);
                                }
                                idx++;
                            }
                        }
                    } else if (value instanceof EObject child) {
                        flatten(child, prefix + resolvedKey + ".",
                                rowMap, columnOrder, columnAttributes, path, resolver, diagnostics,
                                registry, featureWriters);
                    }
                }
            }
        } finally {
            path.pop();
        }
    }

    // ========================================================================
    // Mode: SQL_TABLES — per-EClass tables + join tables.
    // ========================================================================

    /** Per-child mapping back to its containment parent (FK-on-child path). */
    private record IncomingFk(EObject parent, EReference ref) {}

    /** Type-level descriptor for an incoming-FK column on a child EClass. */
    private record IncomingFkColumn(EClass parentEClass, EReference ref) {}

    /** Identifies a single join-table CSV: one per (owner-EClass, ref) pair. */
    private record JoinTableKey(EClass ownerEClass, EReference ref) {}

    /** One pending join-table row entry (resolved to ids during finalization). */
    private record JoinTableEntry(EObject owner, EObject child) {}

    private static final class WalkResult {
        final Map<EClass, List<EObject>> byClass = new LinkedHashMap<>();
        final IdentityHashMap<EObject, Long> pseudoIds = new IdentityHashMap<>();
        final IdentityHashMap<EObject, IncomingFk> instanceIncomingFks = new IdentityHashMap<>();
        final Map<EClass, LinkedHashSet<IncomingFkColumn>> incomingFkColumns = new LinkedHashMap<>();
        final Map<JoinTableKey, List<JoinTableEntry>> joinTableEntries = new LinkedHashMap<>();
    }

    private static void buildSqlTables(List<? extends EObject> roots, Map<String, Object> opts,
            ConfigurationResolver resolver, DiagnosticCollector diagnostics, TabularDocument doc,
            CodecValueRegistry registry, Map<EStructuralFeature, String> featureWriters) {

        MultiValuedRefStrategy strategy = resolveMultiValuedRefStrategy(opts);
        Map<?, ?> columnTypeOverrides = optionAsMap(opts, CodecTabularOptions.OPTION_COLUMN_TYPES);
        Map<?, ?> schemas = optionAsMap(opts, CodecTabularOptions.OPTION_SCHEMAS);
        String fkSuffix = resolveFkColumnSuffix(opts);

        WalkResult walk = walkGraph(roots, strategy);

        boolean alphabetical = resolveAlphabetical(opts);
        for (Map.Entry<EClass, List<EObject>> entry : walk.byClass.entrySet()) {
            Table table = buildTableForEClass(entry.getKey(), entry.getValue(), walk,
                    columnTypeOverrides, fkSuffix, schemas, resolver, diagnostics, alphabetical,
                    registry, featureWriters);
            doc.getTables().add(table);
        }
        for (Map.Entry<JoinTableKey, List<JoinTableEntry>> entry : walk.joinTableEntries.entrySet()) {
            JoinTable jt = buildJoinTable(entry.getKey(), entry.getValue(), walk.pseudoIds,
                    fkSuffix, schemas);
            doc.getJoinTables().add(jt);
        }
    }

    private static WalkResult walkGraph(List<? extends EObject> roots, MultiValuedRefStrategy strategy) {
        WalkResult result = new WalkResult();
        Set<EObject> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<EObject> queue = new ArrayDeque<>();
        Map<EClass, Long> counters = new HashMap<>();
        Deque<Runnable> deferredLinks = new ArrayDeque<>();

        for (EObject root : roots) {
            if (root != null) {
                queue.add(root);
            }
        }
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
                                recordMultiValuedLink(result, strategy, obj, ref, child, deferredLinks);
                            }
                        }
                    }
                } else if (value instanceof EObject child) {
                    queue.add(child);
                }
            }
        }
        while (!deferredLinks.isEmpty()) {
            deferredLinks.poll().run();
        }
        return result;
    }

    private static void recordMultiValuedLink(WalkResult result, MultiValuedRefStrategy strategy,
            EObject owner, EReference ref, EObject child, Deque<Runnable> deferred) {
        boolean fkOnChild = strategy == MultiValuedRefStrategy.PREFER_FK_COLUMN && ref.isContainment();
        if (fkOnChild) {
            EClass childEClass = child.eClass();
            result.incomingFkColumns
                    .computeIfAbsent(childEClass, k -> new LinkedHashSet<>())
                    .add(new IncomingFkColumn(owner.eClass(), ref));
            deferred.add(() -> result.instanceIncomingFks.put(child, new IncomingFk(owner, ref)));
        } else {
            JoinTableKey key = new JoinTableKey(owner.eClass(), ref);
            deferred.add(() -> result.joinTableEntries
                    .computeIfAbsent(key, k -> new ArrayList<>())
                    .add(new JoinTableEntry(owner, child)));
        }
    }

    private static Table buildTableForEClass(EClass eClass, List<EObject> objects, WalkResult walk,
            Map<?, ?> columnTypeOverrides, String fkSuffix, Map<?, ?> schemas,
            ConfigurationResolver resolver, DiagnosticCollector diagnostics, boolean alphabetical,
            CodecValueRegistry registry, Map<EStructuralFeature, String> featureWriters) {

        Table table = TabularFactory.eINSTANCE.createTable();
        table.setEClass(eClass);
        table.setName(eClass.getName());
        String schema = resolveSchema(eClass, schemas);
        if (schema != null) {
            table.setSchema(schema);
        }

        // PK column.
        Column pkCol = TabularFactory.eINSTANCE.createColumn();
        pkCol.setHeader(PK_HEADER);
        pkCol.setSource(ColumnSource.PK);
        pkCol.setSqlType(PK_FK_SQL_TYPE);
        table.getColumns().add(pkCol);

        // Attribute + single-valued-ref columns (in EClass-declared order), each carrying its
        // materialized Column and resolved header so ordering can reorder both at once.
        record OwnColumn(EStructuralFeature feature, FeatureConfig config, boolean isReference,
                Column column, String header) {}
        List<OwnColumn> ownColumns = new ArrayList<>();
        for (EStructuralFeature feat : eClass.getEAllStructuralFeatures()) {
            FeatureConfig fc = resolveFeatureConfig(feat, resolver, diagnostics);
            if (fc != null && !fc.shouldSerialize()) {
                continue;
            }
            String resolvedKey = resolvedKey(feat, fc);

            if (feat instanceof EAttribute attr) {
                // Value gate: include the column only if some row would populate it.
                if (!anyValueQualifies(objects, attr, fc)) {
                    continue;
                }
                Column col = TabularFactory.eINSTANCE.createColumn();
                col.setHeader(resolvedKey);
                col.setSource(ColumnSource.ATTRIBUTE);
                col.setSqlType(resolveAttributeSqlType(attr, resolvedKey, columnTypeOverrides));
                col.setFeature(attr);
                ownColumns.add(new OwnColumn(attr, fc, false, col, resolvedKey));
            } else if (feat instanceof EReference ref) {
                if (ref.isMany()) {
                    // Handled out-of-band (FK-on-child or join-table).
                    continue;
                }
                String fkHeader = resolvedKey + fkSuffix;
                Column col = TabularFactory.eINSTANCE.createColumn();
                col.setHeader(fkHeader);
                col.setSource(ColumnSource.FK_PARENT);
                col.setSqlType(PK_FK_SQL_TYPE);
                col.setFeature(ref);
                ownColumns.add(new OwnColumn(ref, fc, true, col, fkHeader));
            }
        }

        // Apply column ordering (alphabetical / idOnTop). The synthetic PK stays first; own
        // attribute/FK columns are reordered, with the eID attribute floated ahead of them.
        EAttribute idAttr = eClass.getEIDAttribute();
        orderColumns(ownColumns, OwnColumn::header, oc -> oc.feature() == idAttr,
                alphabetical, resolveIdOnTop(eClass, resolver, diagnostics));
        for (OwnColumn oc : ownColumns) {
            table.getColumns().add(oc.column());
        }

        // Incoming-FK columns (multi-valued containment refs under PREFER_FK_COLUMN).
        LinkedHashSet<IncomingFkColumn> incoming = walk.incomingFkColumns.get(eClass);
        Map<IncomingFkColumn, String> incomingHeaderNames = computeIncomingFkColumnNames(incoming, fkSuffix);
        for (Map.Entry<IncomingFkColumn, String> e : incomingHeaderNames.entrySet()) {
            Column col = TabularFactory.eINSTANCE.createColumn();
            col.setHeader(e.getValue());
            col.setSource(ColumnSource.FK_CHILD);
            col.setSqlType(PK_FK_SQL_TYPE);
            col.setFeature(e.getKey().ref());
            table.getColumns().add(col);
        }

        // Rows.
        for (EObject obj : objects) {
            Row row = TabularFactory.eINSTANCE.createRow();
            // PK cell.
            LongCell pk = TabularFactory.eINSTANCE.createLongCell();
            pk.setValue(walk.pseudoIds.get(obj));
            row.getCells().add(pk);

            // Own attribute + FK columns.
            for (OwnColumn oc : ownColumns) {
                if (oc.isReference()) {
                    Object value = obj.eGet(oc.feature());
                    if (value instanceof EObject child) {
                        Long fkId = walk.pseudoIds.get(child);
                        if (fkId != null) {
                            FkCell fkCell = TabularFactory.eINSTANCE.createFkCell();
                            fkCell.setTargetId(fkId);
                            fkCell.setTargetEClass(child.eClass());
                            row.getCells().add(fkCell);
                        } else {
                            row.getCells().add(TabularFactory.eINSTANCE.createEmptyCell());
                        }
                    } else {
                        row.getCells().add(TabularFactory.eINSTANCE.createEmptyCell());
                    }
                } else {
                    EAttribute attr = (EAttribute) oc.feature();
                    row.getCells().add(passesValueGate(obj, attr, oc.config())
                            ? makeAttributeCell(obj, attr, oc.config(), registry, featureWriters)
                            : TabularFactory.eINSTANCE.createEmptyCell());
                }
            }

            // Incoming-FK cells (zero or one match per row — EMF's one-container rule).
            IncomingFk fk = walk.instanceIncomingFks.get(obj);
            for (IncomingFkColumn col : incomingHeaderNames.keySet()) {
                if (fk != null && fk.ref() == col.ref() && fk.parent().eClass() == col.parentEClass()) {
                    Long parentId = walk.pseudoIds.get(fk.parent());
                    if (parentId != null) {
                        FkCell fkCell = TabularFactory.eINSTANCE.createFkCell();
                        fkCell.setTargetId(parentId);
                        fkCell.setTargetEClass(fk.parent().eClass());
                        row.getCells().add(fkCell);
                    } else {
                        row.getCells().add(TabularFactory.eINSTANCE.createEmptyCell());
                    }
                } else {
                    row.getCells().add(TabularFactory.eINSTANCE.createEmptyCell());
                }
            }
            table.getRows().add(row);
        }
        return table;
    }

    private static Map<IncomingFkColumn, String> computeIncomingFkColumnNames(
            LinkedHashSet<IncomingFkColumn> incoming, String fkSuffix) {
        if (incoming == null || incoming.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<EClass, Integer> countsByParent = new HashMap<>();
        for (IncomingFkColumn col : incoming) {
            countsByParent.merge(col.parentEClass(), 1, Integer::sum);
        }
        Map<IncomingFkColumn, String> names = new LinkedHashMap<>();
        for (IncomingFkColumn col : incoming) {
            String parent = lowercaseFirst(col.parentEClass().getName());
            boolean collides = countsByParent.getOrDefault(col.parentEClass(), 0) > 1;
            String name = collides
                    ? parent + "_" + col.ref().getName() + fkSuffix
                    : parent + fkSuffix;
            names.put(col, name);
        }
        return names;
    }

    private static JoinTable buildJoinTable(JoinTableKey key, List<JoinTableEntry> entries,
            IdentityHashMap<EObject, Long> pseudoIds, String fkSuffix, Map<?, ?> schemas) {

        String ownerName = lowercaseFirst(key.ownerEClass().getName());
        String refName = key.ref().getName();

        JoinTable jt = TabularFactory.eINSTANCE.createJoinTable();
        jt.setOwnerEClass(key.ownerEClass());
        jt.setRef(key.ref());
        jt.setFileName(ownerName + "_" + refName);
        jt.setOwnerCol(ownerName + fkSuffix);
        jt.setTargetCol(refName + fkSuffix);
        String schema = resolveSchema(key.ownerEClass(), schemas);
        if (schema != null) {
            jt.setSchema(schema);
        }

        for (JoinTableEntry entry : entries) {
            Long ownerId = pseudoIds.get(entry.owner());
            Long childId = pseudoIds.get(entry.child());
            if (ownerId == null || childId == null) {
                continue;
            }
            JoinTableRow row = TabularFactory.eINSTANCE.createJoinTableRow();
            row.setOwnerId(ownerId);
            row.setTargetId(childId);
            row.setTargetEClass(entry.child().eClass());
            jt.getRows().add(row);
        }
        return jt;
    }

    // ========================================================================
    // Cell construction — typed cells dispatched on the EAttribute's EType.
    // ========================================================================

    @SuppressWarnings("unchecked")
    private static Cell makeAttributeCell(EObject obj, EAttribute attr, FeatureConfig fc,
            CodecValueRegistry registry, Map<EStructuralFeature, String> featureWriters) {
        Object value = obj.eGet(attr);
        if (value == null) {
            return TabularFactory.eINSTANCE.createEmptyCell();
        }

        // Apply a named CodecValueWriter when one is configured for this attribute.
        if (registry != null && featureWriters != null) {
            String writerName = featureWriters.get(attr);
            if (writerName != null) {
                CodecValueWriter<Object, EAttribute> writer =
                        (CodecValueWriter<Object, EAttribute>) registry.getWriter(writerName).orElse(null);
                if (writer != null) {
                    Cell cell = invokeWriterAsCell(writer, value, attr);
                    if (cell != null) {
                        return cell;
                    }
                }
            }
        }

        String dateFormat = fc != null ? fc.getDateFormat() : null;
        EnumSerializationStrategy enumStrategy = fc != null ? fc.getEnumSerialization() : null;

        if (attr.isMany() && value instanceof List<?> list) {
            // Multi-valued attribute: stringify items, join with ';', return as StringCell
            // (preserves existing CSV behaviour; renderers can refine if needed).
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Object item : list) {
                if (!first) {
                    sb.append(ARRAY_VALUE_SEPARATOR);
                }
                sb.append(stringifyScalar(item, dateFormat, enumStrategy));
                first = false;
            }
            StringCell cell = TabularFactory.eINSTANCE.createStringCell();
            cell.setValue(sb.toString());
            return cell;
        }
        return makeScalarCell(value, dateFormat, enumStrategy);
    }

    /**
     * Invokes a {@link CodecValueWriter} by routing it through a temporary Jackson generator,
     * then reads the produced JSON token back as a typed {@link Cell}.
     */
    private static Cell invokeWriterAsCell(CodecValueWriter<Object, EAttribute> writer,
            Object value, EAttribute attr) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectMapper mapper = JsonMapper.builder().build();
            try (JsonGenerator gen = mapper.createGenerator(baos)) {
                writer.write(value, attr, new MinimalWriterContext(gen));
            }
            try (JsonParser parser = mapper.createParser(baos.toByteArray())) {
                JsonToken token = parser.nextToken();
                if (token == null) {
                    return null;
                }
                switch (token) {
                    case VALUE_STRING: {
                        StringCell cell = TabularFactory.eINSTANCE.createStringCell();
                        cell.setValue(parser.getString());
                        return cell;
                    }
                    case VALUE_NUMBER_INT: {
                        LongCell cell = TabularFactory.eINSTANCE.createLongCell();
                        cell.setValue(parser.getLongValue());
                        return cell;
                    }
                    case VALUE_NUMBER_FLOAT: {
                        DoubleCell cell = TabularFactory.eINSTANCE.createDoubleCell();
                        cell.setValue(parser.getDoubleValue());
                        return cell;
                    }
                    case VALUE_TRUE: case VALUE_FALSE: {
                        BooleanCell cell = TabularFactory.eINSTANCE.createBooleanCell();
                        cell.setValue(parser.getBooleanValue());
                        return cell;
                    }
                    default:
                        return null;
                }
            }
        } catch (IOException e) {
            LOGGER.warning(() -> "TabularDocumentBuilder: value writer failed for attribute '"
                    + attr.getName() + "': " + e.getMessage());
            return null;
        }
    }

    /** Minimal {@link CodecWriterContext} backed by a capturing {@link JsonGenerator}. */
    private static final class MinimalWriterContext implements CodecWriterContext {
        private final JsonGenerator gen;
        private final DiagnosticCollector diagnostics = new DiagnosticCollector();

        MinimalWriterContext(JsonGenerator gen) {
            this.gen = gen;
        }

        @Override public JsonGenerator getGenerator() { return gen; }
        @Override public SerializationContext getJacksonContext() { return null; }
        @Override public EffectiveCodecConfig getConfig() { return null; }
        @Override public DiagnosticCollector getDiagnostics() { return diagnostics; }
    }

    private static Cell makeScalarCell(Object value, String dateFormat,
            EnumSerializationStrategy enumStrategy) {
        if (value == null) {
            return TabularFactory.eINSTANCE.createEmptyCell();
        }
        if (value instanceof String s) {
            StringCell cell = TabularFactory.eINSTANCE.createStringCell();
            cell.setValue(s);
            return cell;
        }
        if (value instanceof Boolean b) {
            BooleanCell cell = TabularFactory.eINSTANCE.createBooleanCell();
            cell.setValue(b);
            return cell;
        }
        if (value instanceof Long || value instanceof Integer
                || value instanceof Short || value instanceof Byte) {
            LongCell cell = TabularFactory.eINSTANCE.createLongCell();
            cell.setValue(((Number) value).longValue());
            return cell;
        }
        if (value instanceof Double || value instanceof Float) {
            DoubleCell cell = TabularFactory.eINSTANCE.createDoubleCell();
            cell.setValue(((Number) value).doubleValue());
            return cell;
        }
        if (value instanceof BigDecimal bd) {
            BigDecimalCell cell = TabularFactory.eINSTANCE.createBigDecimalCell();
            cell.setValue(bd);
            return cell;
        }
        if (value instanceof BigInteger bi) {
            // No BigIntegerCell in the model; promote to BigDecimal (lossless).
            BigDecimalCell cell = TabularFactory.eINSTANCE.createBigDecimalCell();
            cell.setValue(new BigDecimal(bi));
            return cell;
        }
        if (value instanceof Date d) {
            DateCell cell = TabularFactory.eINSTANCE.createDateCell();
            cell.setValue(d);
            if (dateFormat != null && !dateFormat.isBlank()) {
                cell.setDateFormat(dateFormat);
            }
            return cell;
        }
        if (value instanceof byte[] bytes) {
            BinaryCell cell = TabularFactory.eINSTANCE.createBinaryCell();
            cell.setValue(bytes);
            return cell;
        }
        // Enums honour the configured enumSerialization strategy (LITERAL/NAME/VALUE).
        if (value instanceof Enumerator e) {
            return makeEnumCell(e, enumStrategy);
        }
        if (value instanceof Enum<?> e) {
            return makeJavaEnumCell(e, enumStrategy);
        }
        // Characters and anything else → toString into a StringCell.
        StringCell cell = TabularFactory.eINSTANCE.createStringCell();
        cell.setValue(value.toString());
        return cell;
    }

    /** Cell for an EMF enum literal, per the configured strategy (default LITERAL). */
    private static Cell makeEnumCell(Enumerator e, EnumSerializationStrategy strategy) {
        if (strategy == EnumSerializationStrategy.VALUE) {
            LongCell cell = TabularFactory.eINSTANCE.createLongCell();
            cell.setValue((long) e.getValue());
            return cell;
        }
        StringCell cell = TabularFactory.eINSTANCE.createStringCell();
        cell.setValue(strategy == EnumSerializationStrategy.NAME ? e.getName() : e.getLiteral());
        return cell;
    }

    /** Cell for a plain Java enum, per the configured strategy (default name). */
    private static Cell makeJavaEnumCell(Enum<?> e, EnumSerializationStrategy strategy) {
        if (strategy == EnumSerializationStrategy.VALUE) {
            LongCell cell = TabularFactory.eINSTANCE.createLongCell();
            cell.setValue((long) e.ordinal());
            return cell;
        }
        StringCell cell = TabularFactory.eINSTANCE.createStringCell();
        cell.setValue(e.name());
        return cell;
    }

    private static String stringifyScalar(Object value, String dateFormat,
            EnumSerializationStrategy enumStrategy) {
        if (value == null) {
            return "";
        }
        if (value instanceof Date d && dateFormat != null && !dateFormat.isBlank()) {
            return new java.text.SimpleDateFormat(dateFormat).format(d);
        }
        if (value instanceof Enumerator e) {
            if (enumStrategy == EnumSerializationStrategy.VALUE) {
                return Integer.toString(e.getValue());
            }
            return enumStrategy == EnumSerializationStrategy.NAME ? e.getName() : e.getLiteral();
        }
        if (value instanceof Enum<?> e) {
            return enumStrategy == EnumSerializationStrategy.VALUE
                    ? Integer.toString(e.ordinal())
                    : e.name();
        }
        if (value instanceof BigDecimal bd) {
            return bd.toPlainString();
        }
        if (value instanceof byte[] bytes) {
            return java.util.Base64.getEncoder().encodeToString(bytes);
        }
        return value.toString();
    }

    // ========================================================================
    // FeatureConfig + key resolution
    // ========================================================================

    private static FeatureConfig resolveFeatureConfig(EStructuralFeature feat,
            ConfigurationResolver resolver, DiagnosticCollector diagnostics) {
        if (resolver == null) {
            return null;
        }
        try {
            return resolver.resolveFeatureConfig(feat, diagnostics);
        } catch (RuntimeException e) {
            LOGGER.fine(() -> "TabularDocumentBuilder: failed to resolve FeatureConfig for "
                    + feat.getEContainingClass().getName() + "." + feat.getName()
                    + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Value gate for a single (object, attribute) pair, mirroring the Jackson serialization
     * pipeline ({@code AttributeSerializationEntry.shouldSerialize}) via the shared
     * {@link FeatureConfig#shouldSerializeValue}. When no {@link FeatureConfig} is available
     * (no resolver), the prior behaviour is preserved: the value always passes.
     */
    private static boolean passesValueGate(EObject obj, EAttribute attr, FeatureConfig fc) {
        if (fc == null) {
            return true;
        }
        Object value = obj.eGet(attr);
        boolean manyEmpty = attr.isMany() && value instanceof List<?> list && list.isEmpty();
        return fc.shouldSerializeValue(value, attr.getDefaultValue(), manyEmpty);
    }

    /**
     * Whether at least one object in {@code objects} has a value for {@code attr} that passes the
     * {@link #passesValueGate value gate}. Used for column inclusion so a column is emitted iff
     * some row would populate it — matching the IGNORE/Jackson "union of written names" contract.
     */
    private static boolean anyValueQualifies(List<? extends EObject> objects, EAttribute attr,
            FeatureConfig fc) {
        if (fc == null) {
            return true;
        }
        for (EObject obj : objects) {
            if (obj != null && passesValueGate(obj, attr, fc)) {
                return true;
            }
        }
        return false;
    }

    private static final String FIELD_ORDER_ALPHABETICAL = "ALPHABETICAL";

    /**
     * Whether columns should be sorted alphabetically by header — driven by the
     * {@code fieldOrder=ALPHABETICAL} codec option (default {@code DECLARATION}).
     */
    private static boolean resolveAlphabetical(Map<String, Object> opts) {
        Object value = opts.get(ConfigProperty.FIELD_ORDER.getKey());
        if (value == null) {
            value = opts.get("codec." + ConfigProperty.FIELD_ORDER.getKey());
        }
        return value != null && FIELD_ORDER_ALPHABETICAL.equalsIgnoreCase(value.toString());
    }

    /**
     * Whether the id column should float to the front — driven by the {@code idOnTop} codec option,
     * resolved through the {@link IdConfig} for the EClass so annotation/module/options layers apply.
     */
    private static boolean resolveIdOnTop(EClass eClass, ConfigurationResolver resolver,
            DiagnosticCollector diagnostics) {
        if (resolver == null || eClass == null) {
            return false;
        }
        try {
            IdConfig idConfig = resolver.resolveIdConfig(eClass, diagnostics);
            return idConfig != null && idConfig.isOnTop();
        } catch (RuntimeException e) {
            LOGGER.fine(() -> "TabularDocumentBuilder: failed to resolve IdConfig for "
                    + eClass.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Applies column ordering in place: alphabetical sort by header first (when requested), then the
     * id item floated to the front (when requested). Both are stable; the id float preserves the
     * relative order of the remaining items. The id is the EClass {@code eIDAttribute}.
     */
    private static <T> void orderColumns(List<T> items, Function<T, String> header,
            Predicate<T> isId, boolean alphabetical, boolean idOnTop) {
        if (alphabetical) {
            items.sort(Comparator.comparing(header, String.CASE_INSENSITIVE_ORDER));
        }
        if (idOnTop) {
            for (int i = 0; i < items.size(); i++) {
                if (isId.test(items.get(i))) {
                    items.add(0, items.remove(i));
                    break;
                }
            }
        }
    }

    private static String resolvedKey(EStructuralFeature feat, FeatureConfig fc) {
        if (fc != null && fc.getKey() != null && !fc.getKey().isEmpty()) {
            return fc.getKey();
        }
        return feat.getName();
    }

    private static String resolveAttributeSqlType(EAttribute attr, String resolvedKey,
            Map<?, ?> overrides) {
        if (overrides != null) {
            Object byFeature = overrides.get(attr);
            if (byFeature instanceof String s && !s.isBlank()) {
                return s;
            }
            if (resolvedKey != null) {
                Object byResolvedKey = overrides.get(resolvedKey);
                if (byResolvedKey instanceof String s && !s.isBlank()) {
                    return s;
                }
            }
            Object byName = overrides.get(attr.getName());
            if (byName instanceof String s && !s.isBlank()) {
                return s;
            }
        }
        return SqlTypeMapper.toSqlType(attr.getEType());
    }

    /** SQL-type lookup for a FLAT column whose source attribute we may or may not have. */
    private static String resolveColumnSqlType(EAttribute attr, String column, Map<?, ?> overrides) {
        if (overrides != null) {
            if (attr != null) {
                Object byFeature = overrides.get(attr);
                if (byFeature instanceof String s && !s.isBlank()) {
                    return s;
                }
            }
            Object byColumn = overrides.get(column);
            if (byColumn instanceof String s && !s.isBlank()) {
                return s;
            }
            if (attr != null) {
                Object byName = overrides.get(attr.getName());
                if (byName instanceof String s && !s.isBlank()) {
                    return s;
                }
            }
        }
        if (attr != null) {
            return SqlTypeMapper.toSqlType(attr.getEType());
        }
        return SqlTypeMapper.toSqlType(null);
    }

    private static String lowercaseFirst(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        char c = s.charAt(0);
        char low = Character.toLowerCase(c);
        if (c == low) {
            return s;
        }
        return low + s.substring(1);
    }

    // ========================================================================
    // Option resolution
    // ========================================================================

    private static ReferenceMode resolveReferenceMode(Map<String, Object> opts) {
        Object value = opts.get(CodecTabularOptions.OPTION_REFERENCE_MODE);
        if (value instanceof ReferenceMode m) {
            return m;
        }
        if (value instanceof String s && !s.isBlank()) {
            try {
                return ReferenceMode.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                LOGGER.fine(() -> "TabularDocumentBuilder: unknown ReferenceMode '" + s + "', defaulting to IGNORE");
            }
        }
        return ReferenceMode.IGNORE;
    }

    private static MultiValuedRefStrategy resolveMultiValuedRefStrategy(Map<String, Object> opts) {
        Object value = opts.get(CodecTabularOptions.OPTION_MULTI_VALUED_REF_STRATEGY);
        if (value instanceof MultiValuedRefStrategy s) {
            return s;
        }
        if (value instanceof String s && !s.isBlank()) {
            try {
                return MultiValuedRefStrategy.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                LOGGER.fine(() -> "TabularDocumentBuilder: unknown MultiValuedRefStrategy '"
                        + s + "', defaulting to PREFER_FK_COLUMN");
            }
        }
        return MultiValuedRefStrategy.PREFER_FK_COLUMN;
    }

    private static String resolveFkColumnSuffix(Map<String, Object> opts) {
        Object value = opts.get(CodecTabularOptions.OPTION_FK_COLUMN_SUFFIX);
        if (value instanceof String s && !s.isEmpty()) {
            return s;
        }
        return DEFAULT_FK_COLUMN_SUFFIX;
    }

    private static String resolveSchema(EClass eClass, Map<?, ?> schemas) {
        if (schemas == null || eClass == null) {
            return null;
        }
        Object byClass = schemas.get(eClass);
        if (byClass instanceof String s && !s.isEmpty()) {
            return s;
        }
        if (eClass.getEPackage() != null) {
            Object byPackage = schemas.get(eClass.getEPackage());
            if (byPackage instanceof String s && !s.isEmpty()) {
                return s;
            }
        }
        return null;
    }

    private static Map<?, ?> optionAsMap(Map<String, Object> opts, String key) {
        Object value = opts.get(key);
        return value instanceof Map<?, ?> map ? map : null;
    }
}
