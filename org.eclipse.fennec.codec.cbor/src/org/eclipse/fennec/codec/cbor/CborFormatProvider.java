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
package org.eclipse.fennec.codec.cbor;

import org.eclipse.fennec.codec.format.jackson.JacksonFormatProvider;

import tools.jackson.dataformat.cbor.CBORFactory;

/**
 * {@link org.eclipse.fennec.codec.format.CodecFormatProvider CodecFormatProvider}
 * for CBOR (Concise Binary Object Representation) format.
 * <p>
 * CBOR is a binary data serialization format based on JSON concepts.
 * It provides compact encoding and is defined in RFC 7049.
 * <p>
 * This provider delegates to {@link JacksonFormatProvider} with a
 * {@link CBORFactory}, so all serialization/deserialization is handled
 * by Jackson's CBOR module.
 * <p>
 * Usage:
 * <pre>
 * CborFormatProvider provider = new CborFormatProvider();
 * CodecResource resource = new CodecResource(uri, metadataService,
 *         resolver, null, null, provider);
 * </pre>
 *
 * @see JacksonFormatProvider
 * @since 2026-02-16
 */
public class CborFormatProvider extends JacksonFormatProvider {

    /**
     * Creates a new CBOR format provider.
     */
    public CborFormatProvider() {
        super("cbor", new CBORFactory(),
                new String[] { "cbor" },
                new String[] { "application/cbor" });
    }
}
