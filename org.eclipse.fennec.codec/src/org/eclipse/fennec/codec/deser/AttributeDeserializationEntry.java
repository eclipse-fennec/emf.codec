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
package org.eclipse.fennec.codec.deser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.value.AttributeValueReader;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueReader;
import org.eclipse.fennec.codec.value.CodecValueRegistry;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;

/**
 * Deserialization entry that handles EAttribute values.
 * <p>
 * Supports:
 * <ul>
 *   <li>Primitive types (String, int, long, double, boolean, etc.)</li>
 *   <li>EMF data types with custom conversion</li>
 *   <li>Multi-valued attributes (arrays)</li>
 *   <li>Null values</li>
 * </ul>
 * </p>
 *
 * @see FeatureConfig
 * @see <a href="docs/codec-v2-serialization-spec.md#9-feature-serialization">Spec 9: Feature Serialization</a>
 * @author Mark Hoffmann
 * @since 2025-12-16
 */
public class AttributeDeserializationEntry implements DeserializationEntry {

    private static final Logger LOGGER = Logger.getLogger(AttributeDeserializationEntry.class.getName());

    private final FeatureConfig config;
    private final EAttribute attribute;
    private final CodecValueReader<Object, EAttribute> customReader;
    private final CodecEntryContext entryContext;

    /**
     * Creates a new AttributeDeserializationEntry.
     *
     * @param config the effective feature configuration
     * @param attribute the EAttribute to deserialize
     */
    public AttributeDeserializationEntry(FeatureConfig config, EAttribute attribute) {
        this(config, attribute, null);
    }

    /**
     * Creates a new AttributeDeserializationEntry with custom value reader support.
     * <p>
     * If the configured reader implements {@link AttributeValueReader} and its
     * {@code canHandle()} method returns false for this attribute, a warning
     * is logged and the reader is not used (falls back to default deserialization).
     * </p>
     *
     * @param config the effective feature configuration
     * @param attribute the EAttribute to deserialize
     * @param entryContext the codec entry context for custom readers (may be null)
     */
    @SuppressWarnings("unchecked")
    public AttributeDeserializationEntry(FeatureConfig config, EAttribute attribute,
            CodecEntryContext entryContext) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.attribute = Objects.requireNonNull(attribute, "attribute must not be null");
        this.entryContext = entryContext;

        // Pre-resolve the custom reader at construction time
        String readerName = config.getValueReaderName();
        CodecValueRegistry valueRegistry = entryContext != null ? entryContext.getValueRegistry() : null;
        if (readerName != null && !readerName.isEmpty() && valueRegistry != null) {
            CodecValueReader<?, ?> reader = valueRegistry.getReader(readerName).orElse(null);

            // Check canHandle() for AttributeValueReader implementations
            if (reader instanceof AttributeValueReader<?> attributeReader) {
                if (attributeReader.canHandle(attribute)) {
                    this.customReader = (CodecValueReader<Object, EAttribute>) reader;
                } else {
                    LOGGER.warning("AttributeValueReader '" + readerName
                            + "' cannot handle attribute '" + attribute.getName()
                            + "' (canHandle returned false). Using default deserialization.");
                    this.customReader = null;
                }
            } else {
                // Generic CodecValueReader - no canHandle() check needed
                this.customReader = (CodecValueReader<Object, EAttribute>) reader;
            }
        } else {
            this.customReader = null;
        }
    }

    @Override
    public String getKey() {
        return config.getKey();
    }

    @Override
    public void deserialize(DeserializationState state, JsonParser parser, DeserializationContext ctxt) {
        EObject eObject = state.getEObject();
        if (eObject == null) {
            String msg = "Cannot set attribute '" + attribute.getName() + "': EObject not yet created";
            LOGGER.severe(msg);
            ContextHelper.addError(ctxt, msg, parser, "AttributeDeserializationEntry");
            return;
        }

        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_NULL) {
            // Null value - only set if the attribute is changeable
            if (attribute.isChangeable() && !attribute.isMany()) {
                eObject.eSet(attribute, null);
            }
            return;
        }

        if (attribute.isMany()) {
            deserializeMultiValued(state, parser, ctxt, eObject);
        } else {
            deserializeSingleValued(parser, ctxt, eObject);
        }
    }

    /**
     * Deserializes a single-valued attribute.
     */
    private void deserializeSingleValued(JsonParser parser, DeserializationContext ctxt, EObject eObject) {
        Object value = readValue(parser, ctxt, attribute.getEAttributeType());
        if (value != null && attribute.isChangeable()) {
            eObject.eSet(attribute, value);
        }
    }

    /**
     * Deserializes a multi-valued attribute (array).
     */
    @SuppressWarnings("unchecked")
    private void deserializeMultiValued(DeserializationState state, JsonParser parser,
            DeserializationContext ctxt, EObject eObject) {
        JsonToken token = parser.currentToken();

        if (token != JsonToken.START_ARRAY) {
            // Single value provided for multi-valued - wrap it
            Object value = readValue(parser, ctxt, attribute.getEAttributeType());
            if (value != null) {
                ((List<Object>) eObject.eGet(attribute)).add(value);
            }
            return;
        }

        List<Object> values = (List<Object>) eObject.eGet(attribute);

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            Object value = readValue(parser, ctxt, attribute.getEAttributeType());
            if (value != null) {
                values.add(value);
            }
        }
    }

    /**
     * Reads a value from the parser and converts it to the appropriate type.
     * <p>
     * If a custom value reader is configured, it will be used instead of
     * the default deserialization logic.
     * </p>
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context (may be null)
     * @param dataType the target EMF data type
     * @return the converted value, or null if conversion fails
     */
    private Object readValue(JsonParser parser, DeserializationContext ctxt, EDataType dataType) {
        JsonToken token = parser.currentToken();

        // Handle null first - custom readers don't handle null
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        // Priority 1: Check for runtime instance binding via options (highest priority)
        CodecValueReader<Object, EAttribute> effectiveReader = resolveEffectiveReader(ctxt);

        // Use custom reader if configured
        if (effectiveReader != null && entryContext != null) {
            try {
                CodecReaderContext readerCtx = entryContext.createReaderContext(parser, ctxt);
                return effectiveReader.read(readerCtx, attribute);
            } catch (IOException e) {
                throw new UncheckedIOException("Custom value reader failed for attribute: " + attribute.getName(), e);
            }
        }

        // Default deserialization
        Class<?> instanceClass = dataType.getInstanceClass();

        try {
            // Special handling for EJavaObject (Object.class) - preserve native JSON types
            if (instanceClass == Object.class) {
                return readAnyJsonValue(parser, ctxt);
            }

            if (token == JsonToken.VALUE_STRING) {
                String stringValue = parser.getString();
                return convertFromString(stringValue, dataType, instanceClass);
            }

            if (token == JsonToken.VALUE_NUMBER_INT) {
                // Check if this is an enum with VALUE strategy
                if (dataType instanceof EEnum eEnum) {
                    return convertEnumFromInteger(parser, eEnum);
                }
                return convertFromInteger(parser, instanceClass);
            }

            if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                return convertFromFloat(parser, instanceClass);
            }

            if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                boolean boolValue = parser.getBooleanValue();
                if (instanceClass == Boolean.class || instanceClass == boolean.class) {
                    return boolValue;
                }
                return EcoreUtil.createFromString(dataType, String.valueOf(boolValue));
            }

            // Handle array data types (e.g., double[], double[][], double[][][])
            if (token == JsonToken.START_ARRAY) {
                // Special case: if target is String, serialize array to JSON string
                if (instanceClass == String.class) {
                    return readJsonStructureAsString(parser);
                }
                return readArrayValue(parser, ctxt, instanceClass);
            }

            // Handle JSON objects: if target is String, serialize to JSON string
            if (token == JsonToken.START_OBJECT) {
                if (instanceClass == String.class) {
                    return readJsonStructureAsString(parser);
                }
                String msg = "Unexpected START_OBJECT for attribute '" + attribute.getName()
                        + "' with type " + instanceClass.getName();
                LOGGER.warning(msg);
                ContextHelper.addWarning(ctxt, msg, parser, "AttributeDeserializationEntry");
                skipJsonValue(parser);
                return null;
            }

            String msg = "Unexpected token type for attribute '" + attribute.getName() + "': " + token;
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "AttributeDeserializationEntry");
            return null;

        } catch (Exception e) {
            String msg = "Error converting value for attribute '" + attribute.getName() + "': " + e.getMessage();
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "AttributeDeserializationEntry");
            return null;
        }
    }

    /**
     * Reads an array value from the parser.
     * <p>
     * Supports:
     * <ul>
     *   <li>{@code double[]} - 1D array of doubles (e.g., GeoJSON Point coordinates)</li>
     *   <li>{@code double[][]} - 2D array (e.g., GeoJSON LineString coordinates)</li>
     *   <li>{@code double[][][]} - 3D array (e.g., GeoJSON Polygon coordinates)</li>
     *   <li>{@code double[][][][]} - 4D array (e.g., GeoJSON MultiPolygon coordinates)</li>
     *   <li>{@code int[]}, {@code long[]}, {@code float[]} - other primitive arrays</li>
     *   <li>{@code String[]} - string arrays</li>
     * </ul>
     * </p>
     *
     * @param parser the JSON parser positioned at START_ARRAY
     * @param ctxt the deserialization context
     * @param instanceClass the target array class
     * @return the array value, or null if not supported
     */
    private Object readArrayValue(JsonParser parser, DeserializationContext ctxt, Class<?> instanceClass) {
        if (!instanceClass.isArray()) {
            String msg = "Expected array type but got: " + instanceClass.getName();
            LOGGER.warning(msg);
            ContextHelper.addWarning(ctxt, msg, parser, "AttributeDeserializationEntry");
            return null;
        }

        Class<?> componentType = instanceClass.getComponentType();

        // Handle multi-dimensional arrays recursively
        if (componentType.isArray()) {
            return readNestedArray(parser, ctxt, componentType);
        }

        // Handle 1D primitive arrays
        if (componentType == double.class) {
            return readDoubleArray(parser);
        }
        if (componentType == int.class) {
            return readIntArray(parser);
        }
        if (componentType == long.class) {
            return readLongArray(parser);
        }
        if (componentType == float.class) {
            return readFloatArray(parser);
        }
        if (componentType == boolean.class) {
            return readBooleanArray(parser);
        }
        if (componentType == String.class) {
            return readStringArray(parser);
        }

        // Handle object arrays (Date[], BigDecimal[], UUID[], etc.) via EMF string conversion
        return readObjectArray(parser, ctxt, componentType);
    }

    /**
     * Reads a nested (multi-dimensional) array.
     */
    private Object readNestedArray(JsonParser parser, DeserializationContext ctxt, Class<?> componentType) {
        List<Object> elements = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.START_ARRAY) {
                Object nested = readArrayValue(parser, ctxt, componentType);
                if (nested != null) {
                    elements.add(nested);
                }
            }
        }

        // Convert List to array of the correct type
        Object array = Array.newInstance(componentType, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Array.set(array, i, elements.get(i));
        }
        return array;
    }

    /**
     * Reads a double[] from the parser.
     */
    private double[] readDoubleArray(JsonParser parser) {
        List<Double> values = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            JsonToken token = parser.currentToken();
            if (token == JsonToken.VALUE_NUMBER_FLOAT || token == JsonToken.VALUE_NUMBER_INT) {
                values.add(parser.getDoubleValue());
            }
        }

        double[] result = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    /**
     * Reads an int[] from the parser.
     */
    private int[] readIntArray(JsonParser parser) {
        List<Integer> values = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                values.add(parser.getIntValue());
            }
        }

        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    /**
     * Reads a long[] from the parser.
     */
    private long[] readLongArray(JsonParser parser) {
        List<Long> values = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                values.add(parser.getLongValue());
            }
        }

        long[] result = new long[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    /**
     * Reads a float[] from the parser.
     */
    private float[] readFloatArray(JsonParser parser) {
        List<Float> values = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            JsonToken token = parser.currentToken();
            if (token == JsonToken.VALUE_NUMBER_FLOAT || token == JsonToken.VALUE_NUMBER_INT) {
                values.add(parser.getFloatValue());
            }
        }

        float[] result = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    /**
     * Reads a boolean[] from the parser.
     */
    private boolean[] readBooleanArray(JsonParser parser) {
        List<Boolean> values = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            JsonToken token = parser.currentToken();
            if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                values.add(parser.getBooleanValue());
            }
        }

        boolean[] result = new boolean[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    /**
     * Reads a String[] from the parser.
     */
    private String[] readStringArray(JsonParser parser) {
        List<String> values = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.VALUE_STRING) {
                values.add(parser.getString());
            }
        }

        return values.toArray(new String[0]);
    }

    /**
     * Reads an array of objects using direct type conversion.
     * <p>
     * Supports any object type that can be constructed from a string,
     * such as {@code Date[]}, {@code BigDecimal[]}, {@code UUID[]}, etc.
     * </p>
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context
     * @param componentType the array component type
     * @return the object array, or null if conversion fails
     */
    private Object readObjectArray(JsonParser parser, DeserializationContext ctxt, Class<?> componentType) {
        List<Object> values = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            JsonToken token = parser.currentToken();
            Object value = null;

            try {
                if (token == JsonToken.VALUE_STRING) {
                    value = convertObjectFromString(parser.getString(), componentType);
                } else if (token == JsonToken.VALUE_NUMBER_INT) {
                    value = convertObjectFromString(String.valueOf(parser.getLongValue()), componentType);
                } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                    value = convertObjectFromString(String.valueOf(parser.getDoubleValue()), componentType);
                } else if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                    value = convertObjectFromString(String.valueOf(parser.getBooleanValue()), componentType);
                } else if (token != JsonToken.VALUE_NULL) {
                    String msg = "Unexpected token in object array: " + token;
                    LOGGER.warning(msg);
                    ContextHelper.addWarning(ctxt, msg, parser, "AttributeDeserializationEntry");
                }
            } catch (Exception e) {
                String msg = "Failed to convert array element to " + componentType.getName() + ": " + e.getMessage();
                LOGGER.warning(msg);
                ContextHelper.addWarning(ctxt, msg, parser, "AttributeDeserializationEntry");
            }

            if (value != null) {
                values.add(value);
            }
        }

        // Create array of correct type
        Object array = Array.newInstance(componentType, values.size());
        for (int i = 0; i < values.size(); i++) {
            Array.set(array, i, values.get(i));
        }
        return array;
    }

    /**
     * Converts a string value to an object of the specified type.
     * <p>
     * Uses reflection to find a suitable constructor or static factory method.
     * </p>
     */
    private Object convertObjectFromString(String stringValue, Class<?> targetType) throws Exception {
        // Try common types first
        if (targetType == BigDecimal.class) {
            return new BigDecimal(stringValue);
        }
        if (targetType == BigInteger.class) {
            return new BigInteger(stringValue);
        }
        if (targetType == Date.class) {
            // Try ISO date format first (yyyy-MM-dd)
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.parse(stringValue);
            } catch (ParseException e) {
                // Try ISO datetime format
                SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                return sdfTime.parse(stringValue);
            }
        }
        if (targetType == UUID.class) {
            return UUID.fromString(stringValue);
        }

        // Try String constructor
        try {
            return targetType.getConstructor(String.class).newInstance(stringValue);
        } catch (NoSuchMethodException e) {
            // Try valueOf static method
            try {
                return targetType.getMethod("valueOf", String.class).invoke(null, stringValue);
            } catch (NoSuchMethodException e2) {
                // Try parse static method
                return targetType.getMethod("parse", String.class).invoke(null, stringValue);
            }
        }
    }

    /**
     * Converts a string value to the target type.
     */
    private Object convertFromString(String stringValue, EDataType dataType, Class<?> instanceClass) {
        if (instanceClass == String.class) {
            return stringValue;
        }
        // For enums, try both name and literal lookup for better compatibility
        if (dataType instanceof EEnum eEnum) {
            return convertEnumFromString(stringValue, eEnum);
        }
        // Use EMF's conversion mechanism for other data types
        return EcoreUtil.createFromString(dataType, stringValue);
    }

    /**
     * Converts a string value to an enum literal.
     * Tries name lookup first (for NAME strategy), then literal lookup (for LITERAL strategy).
     *
     * @param stringValue the string value
     * @param eEnum the target EEnum type
     * @return the enum literal's instance, or null if not found
     */
    private Object convertEnumFromString(String stringValue, EEnum eEnum) {
        // Try name lookup first (for NAME strategy serialization)
        EEnumLiteral literal = eEnum.getEEnumLiteral(stringValue);
        if (literal != null) {
            return literal.getInstance();
        }
        // Try literal lookup (for LITERAL strategy serialization)
        literal = eEnum.getEEnumLiteralByLiteral(stringValue);
        if (literal != null) {
            return literal.getInstance();
        }
        LOGGER.warning("No enum literal found for '" + stringValue + "' in " + eEnum.getName());
        return null;
    }

    /**
     * Converts an integer value to the target type.
     */
    private Object convertFromInteger(JsonParser parser, Class<?> instanceClass) {
        if (instanceClass == Integer.class || instanceClass == int.class) {
            return parser.getIntValue();
        }
        if (instanceClass == Long.class || instanceClass == long.class) {
            return parser.getLongValue();
        }
        if (instanceClass == Short.class || instanceClass == short.class) {
            return (short) parser.getIntValue();
        }
        if (instanceClass == Byte.class || instanceClass == byte.class) {
            return (byte) parser.getIntValue();
        }
        if (instanceClass == BigInteger.class) {
            return parser.getBigIntegerValue();
        }
        if (instanceClass == BigDecimal.class) {
            return new BigDecimal(parser.getLongValue());
        }
        if (instanceClass == Double.class || instanceClass == double.class) {
            return (double) parser.getLongValue();
        }
        if (instanceClass == Float.class || instanceClass == float.class) {
            return (float) parser.getLongValue();
        }
        // Default: return as long
        return parser.getLongValue();
    }

    /**
     * Converts an integer value to an enum literal.
     * Used when enums are serialized with VALUE strategy.
     *
     * @param parser the JSON parser
     * @param eEnum the target EEnum type
     * @return the enum literal's instance, or null if not found
     */
    private Object convertEnumFromInteger(JsonParser parser, EEnum eEnum) {
        int value = parser.getIntValue();
        EEnumLiteral literal = eEnum.getEEnumLiteral(value);
        if (literal != null) {
            return literal.getInstance();
        }
        LOGGER.warning("No enum literal found for value " + value + " in " + eEnum.getName());
        return null;
    }

    /**
     * Converts a float value to the target type.
     */
    private Object convertFromFloat(JsonParser parser, Class<?> instanceClass) {
        if (instanceClass == Double.class || instanceClass == double.class) {
            return parser.getDoubleValue();
        }
        if (instanceClass == Float.class || instanceClass == float.class) {
            return parser.getFloatValue();
        }
        if (instanceClass == BigDecimal.class) {
            return parser.getDecimalValue();
        }
        // Default: return as double
        return parser.getDoubleValue();
    }

    /**
     * Returns the attribute being deserialized.
     *
     * @return the EAttribute
     */
    public EAttribute getAttribute() {
        return attribute;
    }

    /**
     * Resolves the effective reader for this attribute.
     * <p>
     * Priority order:
     * <ol>
     *   <li>Instance binding from options (FEATURE_VALUE_READER_INSTANCES)</li>
     *   <li>Pre-resolved reader from registry (via valueReaderName config)</li>
     * </ol>
     * </p>
     *
     * @param ctxt the deserialization context (may be null)
     * @return the effective reader, or null if none configured
     */
    @SuppressWarnings("unchecked")
    private CodecValueReader<Object, EAttribute> resolveEffectiveReader(DeserializationContext ctxt) {
        // Priority 1: Check for instance binding from options
        if (ctxt != null) {
            Object instancesAttr = ctxt.getAttribute(ContextHelper.FEATURE_VALUE_READER_INSTANCES);
            if (instancesAttr instanceof Map<?, ?> instancesMap) {
                Object reader = instancesMap.get(attribute);
                if (reader instanceof CodecValueReader<?, ?>) {
                    return (CodecValueReader<Object, EAttribute>) reader;
                }
            }
        }

        // Priority 2: Use pre-resolved reader from registry
        return customReader;
    }

    /**
     * Reads any JSON value and returns it as a native Java object.
     * <p>
     * Used for EJavaObject (Object.class) attributes that can hold any value.
     * </p>
     * <ul>
     *   <li>String → String</li>
     *   <li>Integer → Long</li>
     *   <li>Float → Double</li>
     *   <li>Boolean → Boolean</li>
     *   <li>Null → null</li>
     *   <li>Array → List&lt;Object&gt;</li>
     *   <li>Object → Map&lt;String, Object&gt;</li>
     * </ul>
     *
     * @param parser the JSON parser
     * @param ctxt the deserialization context
     * @return the native Java representation of the JSON value
     */
    private Object readAnyJsonValue(JsonParser parser, DeserializationContext ctxt) {
        JsonToken token = parser.currentToken();

        switch (token) {
            case VALUE_STRING:
                return parser.getString();
            case VALUE_NUMBER_INT:
                return parser.getLongValue();
            case VALUE_NUMBER_FLOAT:
                return parser.getDoubleValue();
            case VALUE_TRUE:
                return Boolean.TRUE;
            case VALUE_FALSE:
                return Boolean.FALSE;
            case VALUE_NULL:
                return null;
            case START_ARRAY:
                return readJsonArrayAsCollection(parser, ctxt);
            case START_OBJECT:
                return readJsonObjectAsMap(parser, ctxt);
            default:
                String msg = "Unexpected token for EJavaObject attribute '" + attribute.getName() + "': " + token;
                LOGGER.warning(msg);
                ContextHelper.addWarning(ctxt, msg, parser, "AttributeDeserializationEntry");
                return null;
        }
    }

    /**
     * Reads a JSON array and returns it as a List.
     */
    private List<Object> readJsonArrayAsCollection(JsonParser parser, DeserializationContext ctxt) {
        List<Object> list = new ArrayList<>();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            list.add(readAnyJsonValue(parser, ctxt));
        }

        return list;
    }

    /**
     * Reads a JSON object and returns it as a Map.
     */
    private Map<String, Object> readJsonObjectAsMap(JsonParser parser, DeserializationContext ctxt) {
        Map<String, Object> map = new LinkedHashMap<>();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            parser.nextToken(); // Move to value
            map.put(fieldName, readAnyJsonValue(parser, ctxt));
        }

        return map;
    }

    /**
     * Reads a JSON structure (object or array) and returns it as a JSON string.
     * <p>
     * This is used when a String-typed attribute encounters a JSON object or array.
     * The entire structure is serialized to a JSON string representation.
     * </p>
     * <p>
     * Example: {@code {"timeout": 30, "retries": 3}} becomes {@code "{\"timeout\":30,\"retries\":3}"}
     * </p>
     *
     * @param parser the JSON parser positioned at START_OBJECT or START_ARRAY
     * @return the JSON string representation of the structure
     */
    private String readJsonStructureAsString(JsonParser parser) {
        StringBuilder sb = new StringBuilder();
        JsonToken startToken = parser.currentToken();

        if (startToken == JsonToken.START_OBJECT) {
            readJsonObjectToString(parser, sb);
        } else if (startToken == JsonToken.START_ARRAY) {
            readJsonArrayToString(parser, sb);
        }

        return sb.toString();
    }

    /**
     * Reads a JSON object and appends it to the StringBuilder.
     */
    private void readJsonObjectToString(JsonParser parser, StringBuilder sb) {
        sb.append("{");
        boolean first = true;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            // Current token should be PROPERTY_NAME
            String fieldName = parser.currentName();
            sb.append("\"").append(escapeJson(fieldName)).append("\":");

            // Move to value
            parser.nextToken();
            appendJsonValue(parser, sb);
        }

        sb.append("}");
    }

    /**
     * Reads a JSON array and appends it to the StringBuilder.
     */
    private void readJsonArrayToString(JsonParser parser, StringBuilder sb) {
        sb.append("[");
        boolean first = true;

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            appendJsonValue(parser, sb);
        }

        sb.append("]");
    }

    /**
     * Appends the current JSON value to the StringBuilder.
     */
    private void appendJsonValue(JsonParser parser, StringBuilder sb) {
        JsonToken token = parser.currentToken();

        switch (token) {
            case START_OBJECT:
                readJsonObjectToString(parser, sb);
                break;
            case START_ARRAY:
                readJsonArrayToString(parser, sb);
                break;
            case VALUE_STRING:
                sb.append("\"").append(escapeJson(parser.getString())).append("\"");
                break;
            case VALUE_NUMBER_INT:
                sb.append(parser.getLongValue());
                break;
            case VALUE_NUMBER_FLOAT:
                sb.append(parser.getDecimalValue().toPlainString());
                break;
            case VALUE_TRUE:
                sb.append("true");
                break;
            case VALUE_FALSE:
                sb.append("false");
                break;
            case VALUE_NULL:
                sb.append("null");
                break;
            default:
                // Skip unexpected tokens
                break;
        }
    }

    /**
     * Escapes special characters in a JSON string value.
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * Skips the current JSON value (object, array, or primitive).
     * <p>
     * Used when an unexpected structure is encountered but we need to
     * continue parsing the rest of the document.
     * </p>
     *
     * @param parser the JSON parser
     */
    private void skipJsonValue(JsonParser parser) {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.START_OBJECT) {
            int depth = 1;
            while (depth > 0) {
                token = parser.nextToken();
                if (token == JsonToken.START_OBJECT) {
                    depth++;
                } else if (token == JsonToken.END_OBJECT) {
                    depth--;
                }
            }
        } else if (token == JsonToken.START_ARRAY) {
            int depth = 1;
            while (depth > 0) {
                token = parser.nextToken();
                if (token == JsonToken.START_ARRAY) {
                    depth++;
                } else if (token == JsonToken.END_ARRAY) {
                    depth--;
                }
            }
        }
        // Primitive values are already consumed
    }
}
