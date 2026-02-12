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
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;

/**
 * Tests for canHandle() validation in ReferenceDeserializationEntry.
 *
 * @see <a href="docs/codec-v2-spec/10-custom-values.md#24-the-canhandle-method">Spec: canHandle()</a>
 */
@DisplayName("ReferenceDeserializationEntry canHandle() validation")
class ReferenceDeserializationEntryCanHandleTest extends DeserializationEntryTestBase {

    private static final String DEFAULT_REF_KEY = "_ref";
    private TestLogHandler logHandler;
    private Logger logger;

    @BeforeEach
    void setUpLogger() {
        logger = Logger.getLogger(ReferenceDeserializationEntry.class.getName());
        logHandler = new TestLogHandler();
        logger.addHandler(logHandler);
        logger.setLevel(Level.ALL);
    }

    @AfterEach
    void tearDownLogger() {
        logger.removeHandler(logHandler);
    }

    @Nested
    @DisplayName("ReferenceValueReader canHandle validation")
    class ReferenceValueReaderCanHandle {

        @Test
        @DisplayName("accepts reader when canHandle returns true")
        void acceptsReaderWhenCanHandleReturnsTrue() {
            // Reader that accepts Person references
            ReferenceValueReader<EObject> compatibleReader = new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "testReader";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return true; // Accept all references
                }

                @Override
                public EObject read(CodecReaderContext ctx, EReference ref) {
                    return null;
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("testReader", compatibleReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("testReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should not log warning - reader is compatible
            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            assertNotNull(entry);
            assertEquals(0, logHandler.getWarningCount(),
                    "No warning should be logged when canHandle returns true");
        }

        @Test
        @DisplayName("rejects reader and logs warning when canHandle returns false")
        void rejectsReaderWhenCanHandleReturnsFalse() {
            // Reader that only handles EPackage references
            ReferenceValueReader<EPackage> incompatibleReader = new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "epackageReader";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    // Only accept EPackage references, not Person
                    return EcorePackage.Literals.EPACKAGE.isSuperTypeOf(
                            reference.getEReferenceType());
                }

                @Override
                public EPackage read(CodecReaderContext ctx, EReference ref) {
                    return null;
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("epackageReader", incompatibleReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("epackageReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should log warning - reader cannot handle Person reference
            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            assertNotNull(entry);
            assertEquals(1, logHandler.getWarningCount(),
                    "Warning should be logged when canHandle returns false");
            String message = logHandler.getLastWarningMessage();
            assertNotNull(message);
            // Check that warning mentions the reader name and reference
            org.junit.jupiter.api.Assertions.assertTrue(
                    message.contains("epackageReader") || message.contains("manager"),
                    "Warning should mention reader or reference name");
        }

        @Test
        @DisplayName("falls back to default when reader incompatible")
        void fallsBackToDefaultWhenIncompatible() {
            // Reader that rejects all references
            ReferenceValueReader<EObject> rejectingReader = new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "rejectingReader";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return false; // Reject all
                }

                @Override
                public EObject read(CodecReaderContext ctx, EReference ref) {
                    throw new AssertionError("Should not be called");
                }
            };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("rejectingReader", rejectingReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("rejectingReader")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            // The entry should still work - it falls back to default behavior
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            // Deserialize standard reference - should use default (not custom reader)
            try (JsonParser parser = createParser("{\"_ref\": \"#/persons/1\"}")) {
                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                // Default behavior: URI as-is
                assertEquals("#/persons/1", state.getUnresolvedReferences().get(0).getTargetUri());
            }
        }
    }

    @Nested
    @DisplayName("Non-ReferenceValueReader handling")
    class NonReferenceValueReaderHandling {

        @Test
        @DisplayName("accepts generic CodecValueReader for URI transformation")
        void acceptsGenericReaderForUriTransformation() throws IOException {
            // Generic reader (not ReferenceValueReader) for URI transformation
            org.eclipse.fennec.codec.value.CodecValueReader<String, EReference> uriReader =
                    new org.eclipse.fennec.codec.value.CodecValueReader<>() {
                        @Override
                        public String getName() {
                            return "uriTransformer";
                        }

                        @Override
                        public String read(CodecReaderContext ctx, EReference ref) throws IOException {
                            return "#/transformed/" + ctx.getParser().getString();
                        }
                    };

            CodecValueRegistry registry = new CodecValueRegistry();
            registry.registerReader("uriTransformer", uriReader);

            FeatureConfig config = FeatureConfig.builder()
                    .key("manager")
                    .valueReaderName("uriTransformer")
                    .build();

            CodecEntryContext entryContext = CodecEntryContext.builder()
                    .valueRegistry(registry)
                    .build();

            // Should not log warning - generic readers are accepted for URI transformation
            ReferenceDeserializationEntry entry = new ReferenceDeserializationEntry(
                    config, managerRef, DEFAULT_REF_KEY, entryContext);

            // Should work for non-containment reference
            EObject person = createPerson();
            DeserializationState state = createStateWithObject(person);

            try (JsonParser parser = createParser("{\"_ref\": \"abc123\"}")) {
                entry.deserialize(state, parser, null);

                assertEquals(1, state.getUnresolvedReferences().size());
                assertEquals("#/transformed/abc123",
                        state.getUnresolvedReferences().get(0).getTargetUri());
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
