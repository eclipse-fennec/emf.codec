/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.ser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.codec.config.SuperTypeConfig;
import org.eclipse.fennec.model.metadata.SuperTypeSelection;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serialization entry for EObject supertype information.
 * <p>
 * Handles the serialization of supertype information based on the
 * supertype configuration. SuperType format follows Type format:
 * <ul>
 *   <li>Type PLAIN → SuperType as standalone {@code _supertype} field</li>
 *   <li>Type STRUCTURED → SuperType inside {@code _type} object (handled by TypeSerializationEntry)</li>
 * </ul>
 * </p>
 * <p>
 * Presentation options:
 * <ul>
 *   <li>ARRAY (asArray=true): {@code ["Entity", "http://audit.org/1.0#//Auditable"]}</li>
 *   <li>STRING (asArray=false): {@code "Entity,http://audit.org/1.0#//Auditable"}</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/06-supertype.md">Spec: SuperType Serialization</a>
 * @author Mark Hoffmann
 * @since 2025-12-16
 */
public class SuperTypeSerializationEntry implements SerializationEntry {

    private final SuperTypeConfig config;
    private final EClass eClass;
    private final String rootNamespaceUri;
    private final boolean smartCompression;

    /**
     * Creates a new SuperTypeSerializationEntry with the supertype configuration.
     *
     * @param config the supertype configuration
     * @param eClass the EClass being serialized
     * @param smartCompression whether smart compression is enabled (from global config)
     */
    public SuperTypeSerializationEntry(SuperTypeConfig config, EClass eClass, boolean smartCompression) {
        this.config = config;
        this.eClass = eClass;
        this.rootNamespaceUri = eClass.getEPackage() != null ? eClass.getEPackage().getNsURI() : null;
        this.smartCompression = smartCompression;
    }

    /**
     * Creates a new SuperTypeSerializationEntry without smart compression.
     *
     * @param config the supertype configuration
     * @param eClass the EClass being serialized
     */
    public SuperTypeSerializationEntry(SuperTypeConfig config, EClass eClass) {
        this(config, eClass, false);
    }

    @Override
    public String getKey() {
        return config.getEffectiveSuperTypeKey(config.getFormat());
    }

    @Override
    public boolean shouldSerialize(SerializationState state) {
        if (!config.isSerialize()) {
            return false;
        }
        List<String> superTypes = resolveSuperTypes();
        return !superTypes.isEmpty();
    }

    @Override
    public void serialize(SerializationState state, JsonGenerator gen, SerializationContext ctxt) {
        List<String> superTypes = resolveSuperTypes();
        if (superTypes.isEmpty()) {
            return;
        }

        String effectiveKey = config.getEffectiveSuperTypeKey(config.getFormat());

        if (config.isAsArray()) {
            // ARRAY presentation: ["Entity", "http://audit.org/1.0#//Auditable"]
            gen.writeArrayPropertyStart(effectiveKey);
            for (String superType : superTypes) {
                gen.writeString(superType);
            }
            gen.writeEndArray();
        } else {
            // STRING presentation: "Entity,http://audit.org/1.0#//Auditable"
            String joined = String.join(config.getSeparator(), superTypes);
            gen.writeStringProperty(effectiveKey, joined);
        }
    }

    /**
     * Returns the resolved supertype values for use by TypeSerializationEntry
     * when embedding supertypes in STRUCTURED format.
     *
     * @return list of supertype values (URIs or simple names based on smart compression)
     */
    public List<String> getSuperTypeValues() {
        return resolveSuperTypes();
    }

    /**
     * Returns the supertype configuration.
     * <p>
     * Used by TypeSerializationEntry when embedding supertypes in STRUCTURED format.
     * </p>
     *
     * @return the supertype configuration
     */
    public SuperTypeConfig getConfig() {
        return config;
    }

    /**
     * Resolves the list of supertypes to serialize.
     * <p>
     * Applies namespace matching rules:
     * <ul>
     *   <li>Same namespace as root → simple EClass name</li>
     *   <li>Different namespace → full EClass URI</li>
     *   <li>Smart compression OFF → always full URI</li>
     * </ul>
     * </p>
     *
     * @return list of supertype values (URIs or simple names)
     */
    private List<String> resolveSuperTypes() {
        List<String> superTypes = new ArrayList<>();
        SuperTypeSelection selection = config.getStrategy();

        if (selection == SuperTypeSelection.NONE) {
            return superTypes;
        }

        boolean includeEmf = (selection == SuperTypeSelection.ALL_EMF);

        for (EClass superType : eClass.getESuperTypes()) {
            // Skip EMF base types unless selection is ALL_EMF
            if (!includeEmf && isEmfBaseType(superType)) {
                continue;
            }
            superTypes.add(getSuperTypeValue(superType));

            // For SINGLE, only include the first matching supertype
            if (selection == SuperTypeSelection.SINGLE && !superTypes.isEmpty()) {
                break;
            }
        }
        return superTypes;
    }

    /**
     * Gets the value for a supertype EClass.
     * <p>
     * Applies namespace matching: same namespace as root type returns simple name,
     * different namespace returns full URI. Smart compression must be enabled
     * for simple names; otherwise always returns full URI.
     * </p>
     *
     * @param superType the supertype EClass
     * @return the supertype value (simple name or full URI)
     */
    private String getSuperTypeValue(EClass superType) {
        String superTypeNsUri = superType.getEPackage() != null ? superType.getEPackage().getNsURI() : null;

        // Smart compression: same namespace → simple name
        if (smartCompression && rootNamespaceUri != null && rootNamespaceUri.equals(superTypeNsUri)) {
            return superType.getName();
        }

        // Different namespace or no smart compression → full URI
        return getEClassUri(superType);
    }

    /**
     * Gets the full URI for an EClass.
     *
     * @param eClass the EClass
     * @return the EClass URI (nsURI#//className)
     */
    private String getEClassUri(EClass eClass) {
        return eClass.getEPackage().getNsURI() + "#//" + eClass.getName();
    }

    /**
     * Checks if the given EClass is an EMF base type.
     *
     * @param eClass the EClass to check
     * @return true if it's an EMF base type
     */
    private boolean isEmfBaseType(EClass eClass) {
        String nsURI = eClass.getEPackage().getNsURI();
        return nsURI != null && (
                nsURI.startsWith("http://www.eclipse.org/emf/") ||
                nsURI.equals("http://www.eclipse.org/emf/2002/Ecore"));
    }
}
