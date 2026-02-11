/*
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
package org.eclipse.fennec.codec.config;

/**
 * Direction of codec operation.
 * <p>
 * Some configuration properties only apply to one direction:
 * <ul>
 *   <li>{@link #READ} - Deserialization (JSON → EObject)</li>
 *   <li>{@link #WRITE} - Serialization (EObject → JSON)</li>
 * </ul>
 * Properties with both directions apply to both operations.
 *
 * @see ConfigProperty#getDirections()
 */
public enum ConfigDirection {

    /**
     * Read direction (deserialization).
     * Configuration affects how JSON is parsed into EObjects.
     */
    READ,

    /**
     * Write direction (serialization).
     * Configuration affects how EObjects are written to JSON.
     */
    WRITE
}
