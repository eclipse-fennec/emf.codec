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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.value.AttributeValueReader;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;

/**
 * Tests for canHandle() validation in AttributeDeserializationEntry.
 *
 * @see <a href="docs/codec-v2-spec/10-custom-values.md#24-the-canhandle-method">Spec: canHandle()</a>
 */
@DisplayName("AttributeDeserializationEntry canHandle() validation")
class AttributeDeserializationEntryCanHandleTest extends DeserializationEntryTestBase {

    private TestLogHandler logHandler;
    private Logger logger;

    @BeforeEach
    void setUpLogger() {
        logger = Logger.getLogger(AttributeDeserializationEntry.class.getName());
        logHandler = new TestLogHandler();
        logger.addHandler(logHandler);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    void tearDownLogger() {
        logger.removeHandler(logHandler);
    }

    @Nested
    @DisplayName("AttributeValueReader canHandle validation")
    class AttributeValueReaderCanHandle {

        @Test
        @DisplayName("accepts reader when canHandle returns true")
        void acceptsReaderWhenCanHandleReturnsTrue() {
            // Reader that accepts String attributes
            AttributeValueReader<String> stringReader = new AttributeValueReader<>() {
                @Override
                public String getName() {
                    return "stringReader";
                }

                @Override
                public boolean canHandle(EAttribute attribute) {
                    return attribute.getEAttributeType().getInstanceClass() == String.class;
                }

                @Override
                public String read(CodecReaderContext ctx, EAttribute attr)
                        throws IOException {
                    return "custom:" + ctx.getParser().getString();
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("stringReader", stringReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .valueReaderName("stringReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should not log warning - reader is compatible
            AttributeDeserializationEntry entry = new AttributeDeserializationEntry(
                    config, nameAttribute, entryContext);

            assertNotNull(entry);
            assertEquals(0, logHandler.getWarningCount(),
                    "No warning should be logged when canHandle returns true");
        }

        @Test
        @DisplayName("rejects reader and logs warning when canHandle returns false")
        void rejectsReaderWhenCanHandleReturnsFalse() {
            // Reader that only handles Integer attributes
            AttributeValueReader<Integer> intReader = new AttributeValueReader<>() {
                @Override
                public String getName() {
                    return "intReader";
                }

                @Override
                public boolean canHandle(EAttribute attribute) {
                    Class<?> type = attribute.getEAttributeType().getInstanceClass();
                    return type == Integer.class || type == int.class;
                }

                @Override
                public Integer read(CodecReaderContext ctx, EAttribute attr)
                        throws IOException {
                    return ctx.getParser().getIntValue() * 2;
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("intReader", intReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .valueReaderName("intReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should log warning - reader cannot handle String attribute
            AttributeDeserializationEntry entry = new AttributeDeserializationEntry(
                    config, nameAttribute, entryContext);

            assertNotNull(entry);
            assertEquals(1, logHandler.getWarningCount(),
                    "Warning should be logged when canHandle returns false");
            String message = logHandler.getLastWarningMessage();
            assertNotNull(message);
            assertTrue(message.contains("intReader") || message.contains("name"),
                    "Warning should mention reader or attribute name");
        }

        @Test
        @DisplayName("falls back to default when reader incompatible")
        void fallsBackToDefaultWhenIncompatible() {
            // Reader that rejects all attributes
            AttributeValueReader<Object> rejectingReader = new AttributeValueReader<>() {
                @Override
                public String getName() {
                    return "rejectingReader";
                }

                @Override
                public boolean canHandle(EAttribute attribute) {
                    return false;
                }

                @Override
                public Object read(CodecReaderContext ctx, EAttribute attr) {
                    throw new AssertionError("Should not be called");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("rejectingReader", rejectingReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .valueReaderName("rejectingReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            AttributeDeserializationEntry entry = new AttributeDeserializationEntry(
                    config, nameAttribute, entryContext);

            // The entry should still work - it falls back to default behavior
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // Deserialize a string value - should use default (not custom reader)
            try (JsonParser parser = createParser("\"John Doe\"")) {
                entry.deserialize(state, parser, null);

                // Value should be set using default deserialization
                assertEquals("John Doe", person.eGet(nameAttribute));
            }
        }
    }

    @Nested
    @DisplayName("Generic CodecValueReader handling")
    class GenericCodecValueReaderHandling {

        @Test
        @DisplayName("accepts generic CodecValueReader and uses it for transformation")
        void acceptsGenericReaderAndUsesIt() throws IOException {
            // Generic reader (not AttributeValueReader) - transforms the value
            org.eclipse.fennec.codec.value.CodecValueReader<String, EAttribute> prefixReader =
                    new org.eclipse.fennec.codec.value.CodecValueReader<>() {
                        @Override
                        public String getName() {
                            return "prefixReader";
                        }

                        @Override
                        public String read(CodecReaderContext ctx, EAttribute attr) throws IOException {
                            return "PREFIX_" + ctx.getParser().getString();
                        }
                    };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("prefixReader", prefixReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .valueReaderName("prefixReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should not log warning - generic readers have no canHandle() to check
            AttributeDeserializationEntry entry = new AttributeDeserializationEntry(
                    config, nameAttribute, entryContext);

            assertNotNull(entry);
            assertEquals(0, logHandler.getWarningCount(),
                    "No warning should be logged for generic reader");

            // Verify the reader is actually used
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("\"John\"")) {
                entry.deserialize(state, parser, null);
                assertEquals("PREFIX_John", person.eGet(nameAttribute));
            }
        }
    }

    /**
     * Test handler to capture log messages.
     */
    private static class TestLogHandler extends Handler {
        private int warningCount = 0;
        private String lastWarningMessage;

        @Override
        public void publish(LogRecord record) {
            if (record.getLevel() == Level.WARNING) {
                warningCount++;
                lastWarningMessage = record.getMessage();
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

        public int getWarningCount() {
            return warningCount;
        }

        public String getLastWarningMessage() {
            return lastWarningMessage;
        }
    }
}
