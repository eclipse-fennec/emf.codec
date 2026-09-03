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
package org.eclipse.fennec.codec.resource.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.fennec.codec.prefix.CodecPrefixReader;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Shared {@link CodecPrefixRegistry} assembled from the whiteboard (issue #193, spec
 * 14-custom-values.md §13.7).
 * <p>
 * Collects {@link CodecPrefixWriter} and {@link CodecPrefixReader} services and registers each
 * under the key(s) in its {@value CodecPrefixRegistry#SERVICE_PROPERTY_KEY} service property
 * ({@code String} or {@code String[]}). A service without the property is ignored with a log
 * warning; a service for a key already taken is ignored with a log warning, the first stays -
 * the registry's {@code IllegalArgumentException} is for programmatic misuse and must not tear
 * down this component. The resource factory components bind this registry optionally and hand a
 * {@link CodecPrefixRegistry#copy() copy} to every resource, as they do for the value registry.
 * </p>
 */
@Component(
        name = "CodecPrefixRegistryComponent",
        service = CodecPrefixRegistry.class,
        immediate = true
)
public class CodecPrefixRegistryComponent extends CodecPrefixRegistry {

    private static final Logger LOGGER = Logger.getLogger(CodecPrefixRegistryComponent.class.getName());

    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removePrefixWriter"
    )
    void addPrefixWriter(CodecPrefixWriter writer, Map<String, Object> properties) {
        for (String key : keysOf(properties, writer)) {
            if (hasWriter(key)) {
                LOGGER.warning(() -> "Prefix key '" + key + "' already has a writer; ignoring "
                        + writer.getClass().getName());
                continue;
            }
            register(key, writer);
        }
    }

    void removePrefixWriter(CodecPrefixWriter writer, Map<String, Object> properties) {
        for (String key : keysOf(properties, null)) {
            if (getWriter(key).filter(w -> w == writer).isPresent()) {
                unregisterWriter(key);
            }
        }
    }

    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removePrefixReader"
    )
    void addPrefixReader(CodecPrefixReader reader, Map<String, Object> properties) {
        for (String key : keysOf(properties, reader)) {
            if (hasReader(key)) {
                LOGGER.warning(() -> "Prefix key '" + key + "' already has a reader; ignoring "
                        + reader.getClass().getName());
                continue;
            }
            register(key, reader);
        }
    }

    void removePrefixReader(CodecPrefixReader reader, Map<String, Object> properties) {
        for (String key : keysOf(properties, null)) {
            if (getReader(key).filter(r -> r == reader).isPresent()) {
                unregisterReader(key);
            }
        }
    }

    /** The keys a service declares; empty (with a warning, if {@code service} is given) when it declares none. */
    static List<String> keysOf(Map<String, Object> properties, Object service) {
        List<String> keys = new ArrayList<>();
        Object value = properties == null ? null : properties.get(CodecPrefixRegistry.SERVICE_PROPERTY_KEY);
        if (value instanceof String s) {
            keys.add(s);
        } else if (value instanceof String[] array) {
            for (String s : array) {
                keys.add(s);
            }
        } else if (value instanceof Collection<?> collection) {
            for (Object o : collection) {
                keys.add(String.valueOf(o));
            }
        }
        keys.removeIf(k -> k == null || k.isEmpty());
        if (keys.isEmpty() && service != null) {
            LOGGER.warning(() -> service.getClass().getName() + " carries no '"
                    + CodecPrefixRegistry.SERVICE_PROPERTY_KEY + "' service property; not registered");
        }
        return keys;
    }
}
