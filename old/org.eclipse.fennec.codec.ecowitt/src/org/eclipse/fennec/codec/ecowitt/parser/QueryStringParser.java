/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.codec.ecowitt.parser;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert the input stream into a map
 * @author Mark Hoffmann
 * @since 08.08.2025
 */
public class QueryStringParser {

    /**
     * Converts an {@link InputStream} with URL-query information (e.g. "key=value&key2=value2")
     * into a Map<String, Object>.
     *
     * @param inputStream The input stream. Must not be <code>null</code>
     * @return a {@link Map} with the key value pairs. Returns an empty map if the stream is empty.
     * @throws IOException when error during reading happen.
     */
    public static Map<String, Object> parse(InputStream inputStream) throws IOException {
    	requireNonNull(inputStream);
        String dataString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        if (dataString == null || dataString.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> resultMap = new HashMap<>();

        String[] pairs = dataString.split("&");

        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                // Schritt 3: Schlüssel und Wert trennen und URL-dekodieren
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                resultMap.put(key, value);
            }
        }

        return resultMap;
    }
}