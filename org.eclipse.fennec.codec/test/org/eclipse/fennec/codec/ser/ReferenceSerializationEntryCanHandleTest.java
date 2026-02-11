/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.codec.ser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.FeatureConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for canHandle() validation in ReferenceSerializationEntry.
 *
 * @see <a href="docs/codec-v2-spec/10-custom-values.md#24-the-canhandle-method">Spec: canHandle()</a>
 */
@DisplayName("ReferenceSerializationEntry canHandle() validation")
class ReferenceSerializationEntryCanHandleTest extends SerializationEntryTestBase {

    private static final String DEFAULT_REF_KEY = "_ref";
    private TestLogHandler logHandler;
    private Logger logger;

    @BeforeEach
    void setUpLogger() {
        logger = Logger.getLogger(ReferenceSerializationEntry.class.getName());
        logHandler = new TestLogHandler();
        logger.addHandler(logHandler);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    void tearDownLogger() {
        logger.removeHandler(logHandler);
    }

    @Nested
    @DisplayName("ReferenceValueWriter canHandle validation")
    class ReferenceValueWriterCanHandle {

        @Test
        @DisplayName("accepts writer when canHandle returns true")
        void acceptsWriterWhenCanHandleReturnsTrue() {
            // Writer that accepts all references
            ReferenceValueWriter<EObject> compatibleWriter = new ReferenceValueWriter<>() {
                @Override
                public String getName() {
                    return "testWriter";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return true;
                }

                @Override
                public void write(EObject value, EReference ref, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeString("custom-output");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("testWriter", compatibleWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("contained")
                    .valueWriterName("testWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should not log warning - writer is compatible
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, addressRef, DEFAULT_REF_KEY, false, null, entryContext);

            assertNotNull(entry);
            assertEquals(0, logHandler.getWarningCount(),
                    "No warning should be logged when canHandle returns true");
        }

        @Test
        @DisplayName("rejects writer and logs warning when canHandle returns false")
        void rejectsWriterWhenCanHandleReturnsFalse() {
            // Writer that only handles EPackage references
            ReferenceValueWriter<EPackage> incompatibleWriter = new ReferenceValueWriter<>() {
                @Override
                public String getName() {
                    return "epackageWriter";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    // Only accept EPackage references
                    return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(
                            reference.getEReferenceType());
                }

                @Override
                public void write(EPackage value, EReference ref, CodecWriterContext ctx) throws IOException {
                    ctx.getGenerator().writeString("epackage-output");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("epackageWriter", incompatibleWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("contained")
                    .valueWriterName("epackageWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should log warning - writer cannot handle Person containment
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, addressRef, DEFAULT_REF_KEY, false, null, entryContext);

            assertNotNull(entry);
            assertEquals(1, logHandler.getWarningCount(),
                    "Warning should be logged when canHandle returns false");
            String message = logHandler.getLastWarningMessage();
            assertNotNull(message);
            assertTrue(message.contains("epackageWriter") || message.contains("contained"),
                    "Warning should mention writer or reference name");
        }

        @Test
        @DisplayName("logs warning when writer cannot handle reference type")
        void logsWarningWhenWriterCannotHandle() {
            // Writer that rejects all references
            ReferenceValueWriter<EObject> rejectingWriter = new ReferenceValueWriter<>() {
                @Override
                public String getName() {
                    return "rejectingWriter";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return false;
                }

                @Override
                public void write(EObject value, EReference ref, CodecWriterContext ctx) {
                    throw new AssertionError("Should not be called");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerWriter("rejectingWriter", rejectingWriter);

            FeatureConfig config = FeatureConfig.builder()
                    .key("address")
                    .valueWriterName("rejectingWriter")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Creating the entry should log a warning because canHandle returns false
            ReferenceSerializationEntry entry = new ReferenceSerializationEntry(
                    config, addressRef, DEFAULT_REF_KEY, false, null, entryContext);

            assertNotNull(entry);
            // Warning should have been logged during construction
            assertEquals(1, logHandler.getWarningCount(),
                    "Warning should be logged when canHandle returns false");
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
