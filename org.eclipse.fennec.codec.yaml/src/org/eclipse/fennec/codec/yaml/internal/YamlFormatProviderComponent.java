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
package org.eclipse.fennec.codec.yaml.internal;

import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.osgi.service.component.annotations.Component;

/**
 * Registers the {@link YamlFormatProvider} as a {@link CodecFormatProvider} service, so that other
 * codecs (e.g. OpenAPI) can offer YAML without importing the YAML packages: they reference the
 * service with the target filter {@code (codec.format.id=yaml)} and become available when this
 * bundle is installed.
 */
@Component(
        name = "YamlFormatProvider",
        service = CodecFormatProvider.class,
        property = CodecFormatProvider.PROPERTY_FORMAT_ID + "=" + YamlFormatProvider.FORMAT_ID)
public class YamlFormatProviderComponent extends YamlFormatProvider {
}
