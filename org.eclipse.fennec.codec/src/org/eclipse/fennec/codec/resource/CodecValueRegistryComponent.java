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
package org.eclipse.fennec.codec.resource;

import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * OSGi DS component that exposes a shared {@link CodecValueRegistry} service.
 * <p>
 * Any bundle that registers a {@link CodecValueWriter} or {@link CodecValueReader}
 * as an OSGi service is automatically picked up and added to this registry. All
 * resource factory components ({@code CodecResourceFactoryComponent},
 * {@code BsonResourceFactoryComponent}, etc.) reference this service optionally,
 * so value handlers registered once are available across all formats.
 * </p>
 *
 * <h3>Usage — registering a custom value writer</h3>
 * <pre>
 * &#64;Component(service = CodecValueWriter.class)
 * public class MyDateWriter implements AttributeValueWriter&lt;Date&gt; {
 *     &#64;Override public String getName() { return "isoDate"; }
 *     &#64;Override public void write(Date value, EAttribute f, CodecWriterContext ctx) { ... }
 * }
 * </pre>
 * The writer is automatically registered into this component and available to every
 * format that uses the shared registry.
 */
@Component(
        name = "CodecValueRegistryComponent",
        service = CodecValueRegistry.class,
        immediate = true
)
public class CodecValueRegistryComponent extends CodecValueRegistry {

    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removeValueWriter"
    )
    void addValueWriter(CodecValueWriter<?, ?> writer) {
        register(writer);
    }

    void removeValueWriter(CodecValueWriter<?, ?> writer) {
        unregisterWriter(writer.getName());
    }

    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removeValueReader"
    )
    void addValueReader(CodecValueReader<?, ?> reader) {
        register(reader);
    }

    void removeValueReader(CodecValueReader<?, ?> reader) {
        unregisterReader(reader.getName());
    }
}
