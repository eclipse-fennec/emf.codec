/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.jsonschema.test;

import java.util.*;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * Utility for comparing JSON Schema documents with order-insensitive oneOf/anyOf/allOf arrays.
 *
 * This comparator treats certain JSON Schema array properties (oneOf, anyOf, allOf, enum) as unordered sets,
 * since the order of variants in these arrays doesn't affect semantic equivalence.
 *
 * @author Claude Code
 * @since Oct 27, 2025
 */
public class JsonSchemaComparator {

    // Properties where array order doesn't matter (treat as sets)
    private static final Set<String> UNORDERED_ARRAY_PROPERTIES = Set.of(
        "oneOf", "anyOf", "allOf", "enum"
    );

    // Thread-local to suppress error messages during matching attempts
    private static final ThreadLocal<Boolean> SILENT_MODE = ThreadLocal.withInitial(() -> false);

    /**
     * Compares two JsonNodes with special handling for JSON Schema structures
     *
     * @param node1 First JsonNode
     * @param node2 Second JsonNode
     * @return true if the nodes are semantically equivalent
     */
    public static boolean schemaEquals(JsonNode node1, JsonNode node2) {
        return schemaEquals(node1, node2, "root");
    }

    /**
     * Compares two JsonNodes with path tracking for better error messages
     */
    private static boolean schemaEquals(JsonNode node1, JsonNode node2, String path) {
        // Null checks
        if (node1 == null && node2 == null) return true;
        if (node1 == null || node2 == null) return false;

        // Type check
        if (node1.getNodeType() != node2.getNodeType()) {
            if (!SILENT_MODE.get()) {
                System.err.println("Type mismatch at " + path + ": " +
                    node1.getNodeType() + " vs " + node2.getNodeType());
            }
            return false;
        }

        // Handle different node types
        if (node1.isObject()) {
            return compareObjects((ObjectNode) node1, (ObjectNode) node2, path);
        } else if (node1.isArray()) {
            return compareArrays((ArrayNode) node1, (ArrayNode) node2, path);
        } else {
            // Primitive values (string, number, boolean, null)
            if (!node1.equals(node2)) {
                if (!SILENT_MODE.get()) {
                    System.err.println("Value mismatch at " + path + ": " +
                        node1 + " vs " + node2);
                }
                return false;
            }
            return true;
        }
    }

    /**
     * Compare two ObjectNodes
     */
    private static boolean compareObjects(ObjectNode obj1, ObjectNode obj2, String path) {
        // Check if keys match
        Set<String> keys1 = new HashSet<>();
        obj1.propertyNames().stream().forEach(keys1::add);

        Set<String> keys2 = new HashSet<>();
        obj2.propertyNames().stream().forEach(keys2::add);

        if (!keys1.equals(keys2)) {
            if (!SILENT_MODE.get()) {
                Set<String> missing = new HashSet<>(keys1);
                missing.removeAll(keys2);
                Set<String> extra = new HashSet<>(keys2);
                extra.removeAll(keys1);

                if (!missing.isEmpty()) {
                    System.err.println("Keys missing in second at " + path + ": " + missing);
                }
                if (!extra.isEmpty()) {
                    System.err.println("Extra keys in second at " + path + ": " + extra);
                }
            }
            return false;
        }

        // Compare all key-value pairs
        for (String key : keys1) {
            String childPath = path + "." + key;
            JsonNode value1 = obj1.get(key);
            JsonNode value2 = obj2.get(key);

            // Special handling for unordered array properties
            if (UNORDERED_ARRAY_PROPERTIES.contains(key) &&
                value1.isArray() && value2.isArray()) {
                if (!compareUnorderedArrays((ArrayNode) value1, (ArrayNode) value2, childPath)) {
                    return false;
                }
            } else {
                if (!schemaEquals(value1, value2, childPath)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Compare two ArrayNodes (ordered comparison)
     */
    private static boolean compareArrays(ArrayNode arr1, ArrayNode arr2, String path) {
        if (arr1.size() != arr2.size()) {
            if (!SILENT_MODE.get()) {
                System.err.println("Array size mismatch at " + path + ": " +
                    arr1.size() + " vs " + arr2.size());
            }
            return false;
        }

        for (int i = 0; i < arr1.size(); i++) {
            if (!schemaEquals(arr1.get(i), arr2.get(i), path + "[" + i + "]")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compare two arrays as unordered sets (for oneOf, anyOf, allOf, enum)
     */
    private static boolean compareUnorderedArrays(ArrayNode arr1, ArrayNode arr2, String path) {
        if (arr1.size() != arr2.size()) {
            if (!SILENT_MODE.get()) {
                System.err.println("Array size mismatch at " + path + ": " +
                    arr1.size() + " vs " + arr2.size());
            }
            return false;
        }

        // Convert to lists
        List<JsonNode> list1 = new ArrayList<>();
        arr1.forEach(list1::add);

        List<JsonNode> list2 = new ArrayList<>();
        arr2.forEach(list2::add);

        // Try to match each element in list1 to an element in list2
        List<JsonNode> matched = new ArrayList<>();

        for (JsonNode node1 : list1) {
            boolean found = false;
            for (JsonNode node2 : list2) {
                if (!matched.contains(node2)) {
                    // Try matching in silent mode to avoid spurious error messages
                    SILENT_MODE.set(true);
                    try {
                        if (schemaEquals(node1, node2, path + "[?]")) {
                            matched.add(node2);
                            found = true;
                            break;
                        }
                    } finally {
                        SILENT_MODE.set(false);
                    }
                }
            }
            if (!found) {
                if (!SILENT_MODE.get()) {
                    System.err.println("No match found in second array at " + path + " for: " + node1);
                }
                return false;
            }
        }

        return true;
    }

    /**
     * Convenience method for comparing two JSON strings
     */
    public static boolean schemaEquals(String json1, String json2) throws Exception {
        tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
        JsonNode node1 = mapper.readTree(json1);
        JsonNode node2 = mapper.readTree(json2);
        return schemaEquals(node1, node2);
    }
}
