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
package org.eclipse.fennec.codec.csv;

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
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.eclipse.fennec.codec.tests.tck.AbstractCoreRoundTripTCK;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Validates {@link CsvFormatProvider#validateSaveOptions(URI, Map)} and the
 * {@link CodecOptions#CODEC_THROW_ON_VALIDATION_WARNINGS} escalation toggle in
 * {@code CodecResource.doSave(...)}.
 * <p>
 * Two layers:
 * <ul>
 *   <li>{@link Hook} — pure unit tests on the provider's validation hook,
 *       no EMF setup needed.</li>
 *   <li>{@link EndToEnd} — round-trip {@code resource.save(...)} checks that
 *       confirm the throw/log toggle behaves as advertised.</li>
 * </ul>
 */
@DisplayName("CSV save-option validation")
class CsvValidationTest {

    // ========================================================================
    // Hook-level tests — no EMF needed
    // ========================================================================

    @Nested
    @DisplayName("validateSaveOptions hook")
    class Hook {

        private final CsvFormatProvider provider = new CsvFormatProvider();

        @Test
        @DisplayName(".csv URI + SQL_TABLES mode → returns one warning")
        void csvUriWithSqlTables() {
            URI uri = URI.createURI("out.csv");
            Map<String, Object> options = Map.of(
                    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
            List<String> warnings = provider.validateSaveOptions(uri, options);
            assertEquals(1, warnings.size(), () -> "warnings: " + warnings);
            assertTrue(warnings.get(0).contains("SQL_TABLES"));
            assertTrue(warnings.get(0).contains(".csv"));
        }

        @Test
        @DisplayName(".csvz URI + IGNORE mode → returns one warning")
        void csvzUriWithIgnore() {
            URI uri = URI.createURI("out.csvz");
            Map<String, Object> options = Map.of(
                    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.IGNORE);
            List<String> warnings = provider.validateSaveOptions(uri, options);
            assertEquals(1, warnings.size(), () -> "warnings: " + warnings);
            assertTrue(warnings.get(0).contains(".csvz"));
        }

        @Test
        @DisplayName(".csvz URI + FLAT mode → returns one warning (still single-CSV output)")
        void csvzUriWithFlat() {
            URI uri = URI.createURI("out.csvz");
            Map<String, Object> options = Map.of(
                    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.FLAT);
            List<String> warnings = provider.validateSaveOptions(uri, options);
            assertEquals(1, warnings.size(), () -> "warnings: " + warnings);
        }

        @Test
        @DisplayName("matching .csv + IGNORE → no warning")
        void csvUriWithIgnoreOk() {
            URI uri = URI.createURI("out.csv");
            Map<String, Object> options = Map.of(
                    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.IGNORE);
            assertTrue(provider.validateSaveOptions(uri, options).isEmpty());
        }

        @Test
        @DisplayName("matching .csvz + SQL_TABLES → no warning")
        void csvzUriWithSqlTablesOk() {
            URI uri = URI.createURI("out.csvz");
            Map<String, Object> options = Map.of(
                    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
            assertTrue(provider.validateSaveOptions(uri, options).isEmpty());
        }

        @Test
        @DisplayName("null URI → no warning (programmatic save without a Resource URI)")
        void nullUriOk() {
            Map<String, Object> options = Map.of(
                    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
            assertTrue(provider.validateSaveOptions(null, options).isEmpty());
        }

        @Test
        @DisplayName("unknown extension → no warning")
        void unknownExtensionOk() {
            URI uri = URI.createURI("out.txt");
            Map<String, Object> options = Map.of(
                    CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
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
            CsvFormatProvider provider = new CsvFormatProvider();
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
        @DisplayName("throw=true + .csv + SQL_TABLES → throws IllegalStateException")
        void throwsWhenMismatchAndThrowEnabled() {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
            options.put(CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS, Boolean.TRUE);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> save("out.csv", options));
            assertTrue(ex.getMessage().contains("SQL_TABLES"),
                    () -> "exception message: " + ex.getMessage());
            assertTrue(ex.getMessage().contains("csv"),
                    () -> "exception message: " + ex.getMessage());
        }

        @Test
        @DisplayName("throw=false (default) + .csv + SQL_TABLES → save proceeds with ZIP bytes")
        void proceedsWhenMismatchAndThrowDisabled() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
            // throwOnValidationWarnings omitted → default false

            byte[] bytes = save("out.csv", options);
            assertTrue(bytes.length > 0, "save should produce output bytes");
            // SQL_TABLES output starts with the ZIP local-file-header magic "PK\003\004".
            assertEquals(0x50, bytes[0] & 0xFF, "expected ZIP magic byte 0 = 'P'");
            assertEquals(0x4B, bytes[1] & 0xFF, "expected ZIP magic byte 1 = 'K'");
        }

        @Test
        @DisplayName("throw=true + .csvz + FLAT → throws IllegalStateException")
        void throwsForCsvzWithFlat() {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.FLAT);
            options.put(CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS, Boolean.TRUE);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> save("out.csvz", options));
            assertTrue(ex.getMessage().contains("csvz"),
                    () -> "exception message: " + ex.getMessage());
        }

        @Test
        @DisplayName("throw=true + matching URI/mode → save proceeds normally")
        void noThrowWhenConsistent() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);
            options.put(CodecOptions.CODEC_THROW_ON_VALIDATION_WARNINGS, Boolean.TRUE);

            byte[] bytes = save("out.csvz", options);
            assertTrue(bytes.length > 0, "save should produce output bytes");
        }

        @Test
        @DisplayName("throw=false + mismatch → warning is also attached to resource.getWarnings()")
        void warningIsAttachedAsDiagnostic() throws IOException {
            CsvFormatProvider provider = new CsvFormatProvider();
            CodecResource resource = new CodecResource(
                    URI.createURI("out.csv"),
                    metadataService, ConfigurationResolver.defaults(),
                    null, null, provider);
            resource.getContents().add(createProduct("p-001", "Widget"));

            Map<String, Object> options = new HashMap<>();
            options.put(CodecTabularOptions.OPTION_REFERENCE_MODE, ReferenceMode.SQL_TABLES);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            resource.save(out, options);

            assertTrue(out.size() > 0, "save should still produce bytes");
            assertEquals(1, resource.getWarnings().size(),
                    () -> "expected exactly one warning diagnostic; got "
                            + resource.getWarnings());
            String message = resource.getWarnings().get(0).getMessage();
            assertTrue(message.contains("SQL_TABLES"),
                    () -> "diagnostic message should mention SQL_TABLES; got: " + message);
            assertTrue(message.contains(".csv"),
                    () -> "diagnostic message should mention .csv; got: " + message);
        }
    }
}
