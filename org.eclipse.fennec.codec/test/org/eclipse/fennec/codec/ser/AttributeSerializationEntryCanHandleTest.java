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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.value.AttributeValueWriter;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for canHandle() validation in AttributeSerializationEntry.
 *
 * @see <a href="docs/codec-v2-spec/10-custom-values.md#24-the-canhandle-method">Spec: canHandle()</a>
 */
@DisplayName("AttributeSerializationEntry canHandle() validation")
class AttributeSerializationEntryCanHandleTest extends SerializationEntryTestBase {

    private TestLogHandler logHandler;
    private Logger logger;

    @BeforeEach
    void setUpLogger() {
        logger = Logger.getLogger(AttributeSerializationEntry.class.getName());
        logHandler = new TestLogHandler();
        logger.addHandler(logHandler);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    void tearDownLogger() {
        logger.removeHandler(logHandler);
    }

    @Nested
    @DisplayName("AttributeValueWriter canHandle validation")
    class AttributeValueWriterCanHandle {

        @Test
        @DisplayName("accepts writer when canHandle returns true")
        void acceptsWriterWhenCanHandleReturnsTrue() {
            // Writer that accepts String attributes
            AttributeValueWriter<String> stringWriter = new AttributeValueWriter<>() {
                @Override
                public String getName() {
                    return "stringWriter";
                }

                @Override
                public boolean canHandle(EAttribute attribute) {
                    return attribute.getEAttributeType().getInstanceClass() == String.class;
                }

                @Override
                public void write(String value, EAttribute attr, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeString("custom:" + value);
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("stringWriter", stringWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .valueWriterName("stringWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should not log warning - writer is compatible
            AttributeSerializationEntry entry = new AttributeSerializationEntry(
                    config, nameAttribute, entryContext);

            assertNotNull(entry);
            assertEquals(0, logHandler.getWarningCount(),
                    "No warning should be logged when canHandle returns true");
        }

        @Test
        @DisplayName("rejects writer and logs warning when canHandle returns false")
        void rejectsWriterWhenCanHandleReturnsFalse() {
            // Writer that only handles Integer attributes
            AttributeValueWriter<Integer> intWriter = new AttributeValueWriter<>() {
                @Override
                public String getName() {
                    return "intWriter";
                }

                @Override
                public boolean canHandle(EAttribute attribute) {
                    Class<?> type = attribute.getEAttributeType().getInstanceClass();
                    return type == Integer.class || type == int.class;
                }

                @Override
                public void write(Integer value, EAttribute attr, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeNumber(value * 2);
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("intWriter", intWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .valueWriterName("intWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should log warning - writer cannot handle String attribute
            AttributeSerializationEntry entry = new AttributeSerializationEntry(
                    config, nameAttribute, entryContext);

            assertNotNull(entry);
            assertEquals(1, logHandler.getWarningCount(),
                    "Warning should be logged when canHandle returns false");
            String message = logHandler.getLastWarningMessage();
            assertNotNull(message);
            assertTrue(message.contains("intWriter") || message.contains("name"),
                    "Warning should mention writer or attribute name");
        }

        @Test
        @DisplayName("logs warning when writer cannot handle attribute type")
        void logsWarningWhenWriterCannotHandle() {
            // Writer that rejects all attributes
            AttributeValueWriter<Object> rejectingWriter = new AttributeValueWriter<>() {
                @Override
                public String getName() {
                    return "rejectingWriter";
                }

                @Override
                public boolean canHandle(EAttribute attribute) {
                    return false;
                }

                @Override
                public void write(Object value, EAttribute attr, CodecWriterContext ctx) {
                    throw new AssertionError("Should not be called");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("rejectingWriter", rejectingWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("age")
                    .valueWriterName("rejectingWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Creating the entry should log a warning because canHandle returns false
            AttributeSerializationEntry entry = new AttributeSerializationEntry(
                    config, ageAttribute, entryContext);

            assertNotNull(entry);
            assertEquals(1, logHandler.getWarningCount(),
                    "Warning should be logged when canHandle returns false");
        }
    }

    @Nested
    @DisplayName("Generic CodecValueWriter handling")
    class GenericCodecValueWriterHandling {

        @Test
        @DisplayName("accepts generic CodecValueWriter without canHandle check")
        void acceptsGenericWriterWithoutCanHandleCheck() {
            // Generic writer (not AttributeValueWriter) - no canHandle() method
            org.eclipse.fennec.codec.value.CodecValueWriter<Object, EAttribute> genericWriter =
                    new org.eclipse.fennec.codec.value.CodecValueWriter<>() {
                        @Override
                        public String getName() {
                            return "genericWriter";
                        }

                        @Override
                        public void write(Object value, EAttribute attr, CodecWriterContext ctx) throws IOException {
                            ctx.getGenerator().writeString("generic:" + value);
                        }
                    };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("genericWriter", genericWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("name")
                    .valueWriterName("genericWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should not log warning - generic writers have no canHandle() to check
            AttributeSerializationEntry entry = new AttributeSerializationEntry(
                    config, nameAttribute, entryContext);

            assertNotNull(entry);
            assertEquals(0, logHandler.getWarningCount(),
                    "No warning should be logged for generic writer");
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
