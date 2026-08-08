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
package org.eclipse.fennec.codec.yaml;

import org.eclipse.fennec.codec.format.jackson.JacksonFormatProvider;

import tools.jackson.dataformat.yaml.YAMLFactory;

/**
 * {@link org.eclipse.fennec.codec.format.CodecFormatProvider CodecFormatProvider}
 * for YAML format.
 * <p>
 * YAML is a human-readable data serialization format. This provider
 * delegates to {@link JacksonFormatProvider} with a {@link YAMLFactory},
 * so all serialization/deserialization is handled by Jackson's YAML module.
 * <p>
 * Usage:
 * <pre>
 * YamlFormatProvider provider = new YamlFormatProvider();
 * CodecResource resource = new CodecResource(uri, metadataService,
 *         resolver, null, null, provider);
 * </pre>
 *
 * @see JacksonFormatProvider
 * @since 1.0
 */
public class YamlFormatProvider extends JacksonFormatProvider {

    /**
     * Creates a new YAML format provider.
     */
    public YamlFormatProvider() {
        super("yaml", new YAMLFactory(),
                new String[] { "yaml", "yml" },
                new String[] { "application/yaml", "text/yaml" });
    }
}
