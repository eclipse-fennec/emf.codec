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
package org.eclipse.fennec.codec.rlang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Validates {@link RLangFormatProvider#validateSaveOptions(URI, Map)} and the
 * {@link CodecOptions#CODEC_THROW_ON_VALIDATION_WARNINGS} escalation toggle.
 *
 * @see CsvValidationTest for the parallel CSV suite — same structure.
 */
@DisplayName("R-Lang save-option validation")
class RLangValidationTest {

    // ========================================================================
    // Hook-level tests — no EMF needed
    // ========================================================================

    @Nested
    @DisplayName("validateSaveOptions hook")
    class Hook {

        private final RLangFormatProvider provider = new RLangFormatProvider();

        @Test
        @DisplayName(".RData URI + dataframePerFile=true → returns one warning")
        void rdataUriWithZipMode() {
            URI uri = URI.createURI("out.RData");
            Map<String, Object> options = Map.of(
                    CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE);
            List<String> warnings = provider.validateSaveOptions(uri, options);
            assertEquals(1, warnings.size(), () -> "warnings: " + warnings);
            assertTrue(warnings.get(0).contains("dataframePerFile"),
                    () -> "warning text: " + warnings.get(0));
            assertTrue(warnings.get(0).contains(".RData"),
                    () -> "warning text: " + warnings.get(0));
        }

        @Test
        @DisplayName(".rdataz URI + dataframePerFile=false (default) → returns one warning")
        void rdatazUriWithoutZipMode() {
            URI uri = URI.createURI("out.rdataz");
            Map<String, Object> options = Map.of(); // default false
            List<String> warnings = provider.validateSaveOptions(uri, options);
            assertEquals(1, warnings.size(), () -> "warnings: " + warnings);
            assertTrue(warnings.get(0).contains("rdataz"),
                    () -> "warning text: " + warnings.get(0));
        }

        @Test
        @DisplayName("matching .RData + default zip=false → no warning")
        void rdataUriWithSingleFileOk() {
            URI uri = URI.createURI("out.RData");
            assertTrue(provider.validateSaveOptions(uri, Map.of()).isEmpty());
        }

        @Test
        @DisplayName("matching .rdataz + zip=true → no warning")
        void rdatazUriWithZipModeOk() {
            URI uri = URI.createURI("out.rdataz");
            Map<String, Object> options = Map.of(
                    CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE);
            assertTrue(provider.validateSaveOptions(uri, options).isEmpty());
        }

        @Test
        @DisplayName("null URI → no warning")
        void nullUriOk() {
            Map<String, Object> options = Map.of(
                    CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE);
            assertTrue(provider.validateSaveOptions(null, options).isEmpty());
        }

        @Test
        @DisplayName("unknown extension → no warning")
        void unknownExtensionOk() {
            URI uri = URI.createURI("out.bin");
            Map<String, Object> options = Map.of(
                    CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE);
            assertTrue(provider.validateSaveOptions(uri, options).isEmpty());
        }
    }

    // ========================================================================
    // End-to-end tests — exercise CodecResource.doSave()
    // ========================================================================

    @Nested
    @DisplayName("CodecResource.save escalation toggle")
    class EndToEnd {

        private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-features.ecore";

        private EcoreHelper ecoreHelper;
        private EPackage testPackage;
        private MetadataWhiteboard metadataService;
        private EClass productClass;
        private EAttribute productIdAttr;
        private EAttribute productNameAttr;

        @BeforeEach
        void setUp() throws IOException {
            ecoreHelper = new EcoreHelper();
            testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractCoreRoundTripTCK.class);
            EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
            metadataService = MetadataServiceFactory.create();
            metadataService.registerPackage(testPackage);

            productClass = EcoreHelper.getEClass(testPackage, "Product");
            productIdAttr = (EAttribute) EcoreHelper.getFeature(productClass, "productId");
            productNameAttr = (EAttribute) EcoreHelper.getFeature(productClass, "name");
        }

        @AfterEach
        void tearDown() {
            EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
            ecoreHelper.releaseAll();
        }

        private EObject createProduct(String id, String name) {
            EObject p = testPackage.getEFactoryInstance().create(productClass);
            p.eSet(productIdAttr, id);
            p.eSet(productNameAttr, name);
            return p;
        }

        private byte[] save(String uriStr, Map<String, Object> options) throws IOException {
            RLangFormatProvider provider = new RLangFormatProvider();
            CodecResource resource = new CodecResource(
                    URI.createURI(uriStr),
                    metadataService, ConfigurationResolver.defaults(),
                    null, null, provider);
            resource.getContents().add(createProduct("p-001", "Widget"));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, options);
            return out.toByteArray();
        }

        @Test
        @DisplayName("throw=true + .RData + dataframePerFile=true → throws IllegalStateException")
        void throwsWhenMismatchAndThrowEnabled() {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE);
            options.put(CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS, Boolean.TRUE);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> save("out.RData", options));
            assertTrue(ex.getMessage().contains("dataframePerFile"),
                    () -> "exception message: " + ex.getMessage());
            assertTrue(ex.getMessage().contains(".RData"),
                    () -> "exception message: " + ex.getMessage());
        }

        @Test
        @DisplayName("throw=false (default) + .RData + dataframePerFile=true → save proceeds with ZIP bytes")
        void proceedsWhenMismatchAndThrowDisabled() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE);
            // throwOnValidationWarnings omitted → default false

            byte[] bytes = save("out.RData", options);
            assertTrue(bytes.length > 0, "save should produce output bytes");
            assertEquals(0x50, bytes[0] & 0xFF, "expected ZIP magic byte 0 = 'P'");
            assertEquals(0x4B, bytes[1] & 0xFF, "expected ZIP magic byte 1 = 'K'");
        }

        @Test
        @DisplayName("throw=true + .rdataz without zip option → throws IllegalStateException")
        void throwsForRdatazWithoutZip() {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS, Boolean.TRUE);
            // dataframePerFile omitted → default false → mismatches .rdataz

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> save("out.rdataz", options));
            assertTrue(ex.getMessage().contains("rdataz"),
                    () -> "exception message: " + ex.getMessage());
        }

        @Test
        @DisplayName("throw=true + matching URI/mode → save proceeds normally")
        void noThrowWhenConsistent() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS, Boolean.TRUE);
            // .RData + default false → consistent

            byte[] bytes = save("out.RData", options);
            assertTrue(bytes.length > 0, "save should produce output bytes");
            // Single-file mode starts with the RData magic "RDX2\nX\n".
            assertEquals('R', (char) bytes[0]);
            assertEquals('D', (char) bytes[1]);
            assertEquals('X', (char) bytes[2]);
            assertEquals('2', (char) bytes[3]);
        }

        @Test
        @DisplayName("throw=false + mismatch → warning is also attached to resource.getWarnings()")
        void warningIsAttachedAsDiagnostic() throws IOException {
            RLangFormatProvider provider = new RLangFormatProvider();
            CodecResource resource = new CodecResource(
                    URI.createURI("out.RData"),
                    metadataService, ConfigurationResolver.defaults(),
                    null, null, provider);
            resource.getContents().add(createProduct("p-001", "Widget"));

            Map<String, Object> options = new HashMap<>();
            options.put(CodecRLangOptions.OPTION_DATAFRAME_PER_FILE, Boolean.TRUE);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, options);

            assertTrue(out.size() > 0, "save should still produce bytes");
            assertEquals(1, resource.getWarnings().size(),
                    () -> "expected exactly one warning diagnostic; got "
                            + resource.getWarnings());
            String message = resource.getWarnings().get(0).getMessage();
            assertTrue(message.contains("dataframePerFile"),
                    () -> "diagnostic message should mention dataframePerFile; got: "
                            + message);
            assertTrue(message.contains(".RData"),
                    () -> "diagnostic message should mention .RData; got: " + message);
        }
    }
}
