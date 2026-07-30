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
package org.eclipse.fennec.codec.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.format.TokenType;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;

/**
 * Demonstrates implementing a complete custom format using
 * {@code Map<String, Object>} as an in-memory representation,
 * following the BsonFormatProvider pattern.
 *
 * @see <a href="docs/codec-v2-spec/17-format-abstraction.md">Spec: Format Abstraction</a>
 */
@DisplayName("Custom Format Delegate Example")
class CustomFormatDelegateExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/examples/example-basic.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private MetadataWhiteboard metadataService;

    private EClass personClass;
    private EAttribute personId;
    private EAttribute nameAttr;
    private EAttribute ageAttr;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, CustomFormatDelegateExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(pkg);

        personClass = EcoreHelper.getEClass(pkg, "Person");
        personId = (EAttribute) EcoreHelper.getFeature(personClass, "personId");
        nameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttr = (EAttribute) EcoreHelper.getFeature(personClass, "age");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Custom FormatDelegate: writes to Map<String, Object>
    // ========================================================================

    static class MapFormatDelegate implements FormatDelegate<OutputStream> {

        private OutputStream target;
        private final Map<String, Object> rootDocument = new LinkedHashMap<>();
        private final Deque<Object> containerStack = new ArrayDeque<>();
        private String pendingName;

        MapFormatDelegate(OutputStream target) {
            this.target = target;
        }

        @Override
        public void setTarget(OutputStream target) {
            this.target = target;
        }

        @Override
        public OutputStream getTarget() {
            return target;
        }

        @Override
        public void writeStartObject() {
            Map<String, Object> obj = new LinkedHashMap<>();
            if (containerStack.isEmpty()) {
                // Root object
                containerStack.push(rootDocument);
            } else {
                addValue(obj);
                containerStack.push(obj);
            }
        }

        @Override
        public void writeEndObject() {
            containerStack.pop();
        }

        @Override
        public void writeStartArray() {
            List<Object> arr = new ArrayList<>();
            addValue(arr);
            containerStack.push(arr);
        }

        @Override
        public void writeEndArray() {
            containerStack.pop();
        }

        @Override
        public void writeName(String name) {
            pendingName = name;
        }

        @Override
        public void writeString(String value) {
            addValue(value);
        }

        @Override
        public void writeInt(int value) {
            addValue(value);
        }

        @Override
        public void writeLong(long value) {
            addValue(value);
        }

        @Override
        public void writeFloat(float value) {
            addValue((double) value);
        }

        @Override
        public void writeDouble(double value) {
            addValue(value);
        }

        @Override
        public void writeBigInteger(BigInteger value) {
            addValue(value);
        }

        @Override
        public void writeBigDecimal(BigDecimal value) {
            addValue(value);
        }

        @Override
        public void writeBoolean(boolean value) {
            addValue(value);
        }

        @Override
        public void writeNull() {
            addValue(null);
        }

        @Override
        public void writeBinary(byte[] data) {
            addValue(data);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws IOException {
            // Convert map to JSON bytes and write to target stream
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            byte[] bytes = mapper.writeValueAsBytes(rootDocument);
            target.write(bytes);
            target.flush();
        }

        @SuppressWarnings("unchecked")
        private void addValue(Object value) {
            Object current = containerStack.peek();
            if (current instanceof Map) {
                ((Map<String, Object>) current).put(pendingName, value);
                pendingName = null;
            } else if (current instanceof List) {
                ((List<Object>) current).add(value);
            }
        }
    }

    // ========================================================================
    // Custom FormatReaderDelegate: reads from Map<String, Object>
    // ========================================================================

    static class MapFormatReaderDelegate implements FormatReaderDelegate<InputStream> {

        private InputStream source;
        private TokenType currentToken = TokenType.NOT_AVAILABLE;
        private String currentName;
        private Object currentValue;

        private final Deque<Iterator<?>> iteratorStack = new ArrayDeque<>();
        private final Deque<Object> containerStack = new ArrayDeque<>();
        private boolean inMapEntry = false;

        @SuppressWarnings("unchecked")
        MapFormatReaderDelegate(InputStream source) throws IOException {
            this.source = source;
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            Map<String, Object> root = mapper.readValue(source, Map.class);
            containerStack.push(root);
            iteratorStack.push(root.entrySet().iterator());
        }

        @Override
        public void setSource(InputStream source) {
            this.source = source;
        }

        @Override
        public InputStream getSource() {
            return source;
        }

        @Override
        @SuppressWarnings("unchecked")
        public TokenType nextToken() {
            // First call: emit START_OBJECT for root
            if (currentToken == TokenType.NOT_AVAILABLE) {
                currentToken = TokenType.START_OBJECT;
                return currentToken;
            }

            if (iteratorStack.isEmpty()) {
                currentToken = TokenType.NOT_AVAILABLE;
                return currentToken;
            }

            Iterator<?> it = iteratorStack.peek();

            if (inMapEntry) {
                inMapEntry = false;
                Object val = currentValue;
                return emitValue(val);
            }

            if (!it.hasNext()) {
                iteratorStack.pop();
                Object container = containerStack.pop();
                currentToken = container instanceof Map
                        ? TokenType.END_OBJECT
                        : TokenType.END_ARRAY;
                return currentToken;
            }

            Object next = it.next();

            if (next instanceof Map.Entry) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) next;
                currentName = entry.getKey();
                currentValue = entry.getValue();
                inMapEntry = true;
                currentToken = TokenType.FIELD_NAME;
                return currentToken;
            }

            // Array element
            return emitValue(next);
        }

        @SuppressWarnings("unchecked")
        private TokenType emitValue(Object val) {
            if (val == null) {
                currentValue = null;
                currentToken = TokenType.VALUE_NULL;
            } else if (val instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) val;
                containerStack.push(map);
                iteratorStack.push(map.entrySet().iterator());
                currentToken = TokenType.START_OBJECT;
            } else if (val instanceof List) {
                List<Object> list = (List<Object>) val;
                containerStack.push(list);
                iteratorStack.push(list.iterator());
                currentToken = TokenType.START_ARRAY;
            } else if (val instanceof String) {
                currentValue = val;
                currentToken = TokenType.VALUE_STRING;
            } else if (val instanceof Integer) {
                currentValue = val;
                currentToken = TokenType.VALUE_NUMBER_INT;
            } else if (val instanceof Long) {
                currentValue = val;
                currentToken = TokenType.VALUE_NUMBER_INT;
            } else if (val instanceof Double || val instanceof Float) {
                currentValue = val;
                currentToken = TokenType.VALUE_NUMBER_FLOAT;
            } else if (val instanceof Boolean) {
                currentValue = val;
                currentToken = TokenType.VALUE_BOOLEAN;
            } else {
                currentValue = val.toString();
                currentToken = TokenType.VALUE_STRING;
            }
            return currentToken;
        }

        @Override
        public TokenType currentToken() {
            return currentToken;
        }

        @Override
        public String currentName() {
            return currentName;
        }

        @Override
        public void skipChildren() {
            if (currentToken == TokenType.START_OBJECT || currentToken == TokenType.START_ARRAY) {
                iteratorStack.pop();
                containerStack.pop();
                currentToken = currentToken == TokenType.START_OBJECT
                        ? TokenType.END_OBJECT : TokenType.END_ARRAY;
            }
        }

        @Override
        public String readString() {
            return currentValue != null ? currentValue.toString() : null;
        }

        @Override
        public int readInt() {
            if (currentValue instanceof Number) {
                return ((Number) currentValue).intValue();
            }
            return Integer.parseInt(currentValue.toString());
        }

        @Override
        public long readLong() {
            if (currentValue instanceof Number) {
                return ((Number) currentValue).longValue();
            }
            return Long.parseLong(currentValue.toString());
        }

        @Override
        public float readFloat() {
            if (currentValue instanceof Number) {
                return ((Number) currentValue).floatValue();
            }
            return Float.parseFloat(currentValue.toString());
        }

        @Override
        public double readDouble() {
            if (currentValue instanceof Number) {
                return ((Number) currentValue).doubleValue();
            }
            return Double.parseDouble(currentValue.toString());
        }

        @Override
        public BigInteger readBigInteger() {
            return new BigInteger(currentValue.toString());
        }

        @Override
        public BigDecimal readBigDecimal() {
            return new BigDecimal(currentValue.toString());
        }

        @Override
        public boolean readBoolean() {
            if (currentValue instanceof Boolean) {
                return (Boolean) currentValue;
            }
            return Boolean.parseBoolean(currentValue.toString());
        }

        @Override
        public byte[] readBinary() {
            if (currentValue instanceof byte[]) {
                return (byte[]) currentValue;
            }
            return currentValue != null ? currentValue.toString().getBytes(StandardCharsets.UTF_8) : new byte[0];
        }

        @Override
        public void close() {
        }
    }

    // ========================================================================
    // Custom FormatProvider: Map-based in-memory format
    // ========================================================================

    static class MapFormatProvider implements CodecFormatProvider<InputStream, OutputStream> {

        @Override
        public String getFormatId() {
            return "map";
        }

        @Override
        public FormatDelegate<OutputStream> createWriter(OutputStream target) {
            return new MapFormatDelegate(target);
        }

        @Override
        public FormatReaderDelegate<InputStream> createReader(InputStream source) throws IOException {
            return new MapFormatReaderDelegate(source);
        }

        @Override
        public String[] getFileExtensions() {
            return new String[] { "map" };
        }

        @Override
        public boolean supportsArrayRoot() {
            return false;
        }
    }

    // ========================================================================
    // Test
    // ========================================================================

    @Test
    @DisplayName("Round-trip with custom Map-based format")
    void roundTripWithCustomMapFormat() throws IOException {
        MapFormatProvider provider = new MapFormatProvider();

        EObject person = pkg.getEFactoryInstance().create(personClass);
        person.eSet(personId, "p1");
        person.eSet(nameAttr, "Alice");
        person.eSet(ageAttr, 30);

        // Serialize through custom format
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://custom.map"), metadataService,
                ConfigurationResolver.defaults(), null, null, provider);
        saveResource.getContents().add(person);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        byte[] bytes = out.toByteArray();

        // Deserialize through custom format
        CodecResource loadResource = new CodecResource(
                URI.createURI("test://custom.map"), metadataService,
                ConfigurationResolver.defaults(), null, null, provider);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        loadResource.load(new ByteArrayInputStream(bytes), options);

        EObject loaded = loadResource.getContents().get(0);

        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
        assertEquals(30, loaded.eGet(ageAttr));
    }
}
