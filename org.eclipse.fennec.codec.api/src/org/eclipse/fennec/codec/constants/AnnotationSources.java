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
package org.eclipse.fennec.codec.constants;

/**
 * Common EAnnotation source URIs used across the codec framework.
 * <p>
 * These constants define the URIs used to identify different annotation sources
 * on EMF model elements:
 * <ul>
 *   <li>{@link #JSONSCHEMA} - JSON Schema metadata annotations</li>
 *   <li>{@link #GEN_MODEL} - EMF GenModel documentation annotations</li>
 *   <li>{@link #EXTENDED_METADATA} - EMF ExtendedMetaData annotations</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public final class AnnotationSources {

    private AnnotationSources() {
        // Utility class - no instantiation
    }

    /**
     * Source URI for codec configuration annotations.
     * <p>
     * All codec-related configuration (ID, type, supertype, reference, feature)
     * is placed in a single EAnnotation with this source URI. Configuration is
     * specified through detail key-value pairs.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>{@code
     * <eAnnotations source="http://eclipse.org/fennec/codec">
     *   <details key="idStrategy" value="ID_FIELD"/>
     *   <details key="typeStrategy" value="URI"/>
     * </eAnnotations>
     * }</pre>
     */
    public static final String CODEC = "http://eclipse.org/fennec/codec";

    /**
     * Source URI prefix for type mapping registry annotations.
     * <p>
     * The full source is: {@code http://eclipse.org/fennec/codec/typeMapping/{mapId}}
     * </p>
     */
    public static final String TYPE_MAPPING_PREFIX = "http://eclipse.org/fennec/codec/typeMapping/";

    /**
     * Source URI for inline type mapping annotations on EReferences.
     */
    public static final String INLINE_MAPPING = "http://eclipse.org/fennec/codec/inlineMapping";

    /**
     * Source URI for JSON Schema metadata annotations.
     * <p>
     * Used to store JSON Schema-specific information on EMF elements,
     * such as format, pattern, contentEncoding, and other schema metadata.
     * </p>
     */
    public static final String JSONSCHEMA = "http://fennec.eclipse.org/jsonschema";

    /**
     * Source URI for EMF GenModel annotations.
     * <p>
     * Used for documentation and deprecated markers on model elements.
     * This is the standard EMF annotation source for generated model metadata.
     * </p>
     */
    public static final String GEN_MODEL = "http://www.eclipse.org/emf/2002/GenModel";

    /**
     * Source URI for EMF ExtendedMetaData annotations.
     * <p>
     * Used for XML Schema to EMF mapping metadata, including element names,
     * namespace information, and content kind specifications.
     * </p>
     */
    public static final String EXTENDED_METADATA = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
}
