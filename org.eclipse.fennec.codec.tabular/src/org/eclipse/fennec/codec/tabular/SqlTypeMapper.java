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

import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * Default mapping from EMF data types to SQL type names, used to populate the
 * {@code Column.sqlType} field of a {@code TabularDocument}.
 * <p>
 * The fallback for unknown classifiers is {@code VARCHAR}. The mapping is
 * intentionally conservative and dialect-portable; users can override per
 * feature via {@link CodecTabularOptions#OPTION_COLUMN_TYPES}.
 *
 * @since 2026-05
 */
public final class SqlTypeMapper {

    private static final String DEFAULT_FALLBACK = "VARCHAR";

    private static final Map<EDataType, String> DEFAULT_MAPPING = buildDefaultMapping();

    private SqlTypeMapper() {
        // utility class
    }

    /**
     * Resolves the SQL type for the given classifier using the built-in
     * mapping table.
     *
     * @param classifier the classifier of an {@code EAttribute}, may be {@code null}
     * @return the SQL type name; {@code VARCHAR} for unknown classifiers
     */
    public static String toSqlType(EClassifier classifier) {
        if (classifier == null) {
            return DEFAULT_FALLBACK;
        }
        if (classifier instanceof EEnum) {
            return "VARCHAR";
        }
        if (classifier instanceof EDataType dataType) {
            String mapped = DEFAULT_MAPPING.get(dataType);
            if (mapped != null) {
                return mapped;
            }
        }
        return DEFAULT_FALLBACK;
    }

    private static Map<EDataType, String> buildDefaultMapping() {
        EcorePackage ecore = EcorePackage.eINSTANCE;
        return Map.ofEntries(
                Map.entry(ecore.getEString(), "VARCHAR"),
                Map.entry(ecore.getEBoolean(), "BOOLEAN"),
                Map.entry(ecore.getEBooleanObject(), "BOOLEAN"),
                Map.entry(ecore.getEInt(), "INTEGER"),
                Map.entry(ecore.getEIntegerObject(), "INTEGER"),
                Map.entry(ecore.getELong(), "BIGINT"),
                Map.entry(ecore.getELongObject(), "BIGINT"),
                Map.entry(ecore.getEShort(), "SMALLINT"),
                Map.entry(ecore.getEShortObject(), "SMALLINT"),
                Map.entry(ecore.getEFloat(), "REAL"),
                Map.entry(ecore.getEFloatObject(), "REAL"),
                Map.entry(ecore.getEDouble(), "DOUBLE"),
                Map.entry(ecore.getEDoubleObject(), "DOUBLE"),
                Map.entry(ecore.getEBigDecimal(), "DECIMAL"),
                Map.entry(ecore.getEBigInteger(), "NUMERIC"),
                Map.entry(ecore.getEDate(), "TIMESTAMP"),
                Map.entry(ecore.getEByteArray(), "BLOB"),
                Map.entry(ecore.getEChar(), "CHAR(1)"),
                Map.entry(ecore.getECharacterObject(), "CHAR(1)"),
                Map.entry(ecore.getEByte(), "SMALLINT"),
                Map.entry(ecore.getEByteObject(), "SMALLINT"));
    }
}
