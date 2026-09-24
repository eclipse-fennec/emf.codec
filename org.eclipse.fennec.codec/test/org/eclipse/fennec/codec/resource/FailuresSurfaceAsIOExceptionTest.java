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
package org.eclipse.fennec.codec.resource;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.format.jackson.JacksonFormatProvider;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import tools.jackson.core.JacksonException;
import tools.jackson.core.json.JsonFactory;

/**
 * A load or save that cannot complete fails with an {@link IOException} (issue #225).
 * <p>
 * {@link Resource#load(InputStream, Map)} and {@link Resource#save(OutputStream, Map)} declare
 * {@code IOException}, and callers catch exactly that. Jackson 3 exceptions are unchecked, as
 * are the ones the STRICT deserializer entries throw, so a truncated document or a strict
 * violation escaped past such a caller - and took the diagnostics collected up to that point
 * with it, since they were transferred to the resource only after a successful parse.
 * </p>
 * <p>
 * Malformed syntax fails in every mode: LENIENT relaxes how the codec treats data it can read,
 * it cannot make an unreadable document readable.
 * </p>
 */
@DisplayName("Load and save failures surface as IOException")
class FailuresSurfaceAsIOExceptionTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/resource/test-roundtrip.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass personClass;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, FailuresSurfaceAsIOExceptionTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        personClass = EcoreHelper.getEClass(testPackage, "Person");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Nested
    @DisplayName("Malformed syntax on load")
    class MalformedSyntax {

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = { "{\"name\":", "{\"name\":\"A\"", "{\"name\" \"A\"}", "{\"name\":tru}" })
        @DisplayName("the JSON path throws an IOException wrapping the parser exception")
        void jsonPathThrowsIOException(String json) {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), false);

            IOException failure = assertThrows(IOException.class, () -> load(resource, json, Map.of()));

            assertInstanceOf(Resource.IOWrappedException.class, failure);
            assertInstanceOf(JacksonException.class, failure.getCause());
        }

        @Test
        @DisplayName("the format-provider path throws an IOException as well")
        void formatPathThrowsIOException() {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), true);

            IOException failure = assertThrows(IOException.class,
                    () -> load(resource, "{\"name\":", Map.of()));

            assertInstanceOf(JacksonException.class, failure.getCause());
        }

        @Test
        @DisplayName("LENIENT does not turn a syntax error into a warning")
        void lenientStillFails() {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), false);

            assertThrows(IOException.class, () -> load(resource, "{\"name\":",
                    Map.of(CodecOptions.CODEC_DESERIALIZATION_MODE, "LENIENT")));
        }

        @Test
        @DisplayName("the syntax error is recorded on the resource, with its position")
        void syntaxErrorReachesTheResource() {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), false);

            assertThrows(IOException.class, () -> load(resource, "{\"name\":\"A\",\n\"age\":", Map.of()));

            assertEquals(1, resource.getErrors().size(), () -> "errors=" + resource.getErrors());
            Diagnostic error = resource.getErrors().get(0);
            assertEquals(2, error.getLine(), "the parser knows where it stopped: " + error.getMessage());
        }

        @Test
        @DisplayName("a well-formed document still loads")
        void wellFormedStillLoads() throws IOException {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), false);

            load(resource, "{\"name\":\"A\"}", Map.of());

            assertEquals(1, resource.getContents().size());
            assertTrue(resource.getErrors().isEmpty(), () -> "errors=" + resource.getErrors());
        }
    }

    @Nested
    @DisplayName("STRICT violations on load")
    class StrictViolations {

        @Test
        @DisplayName("strictOnUnknown fails with an IOException, not an IllegalStateException")
        void strictOnUnknownThrowsIOException() {
            CodecResource resource = newResource(
                    ConfigurationResolver.builder().strictOnUnknown(true).build(), false);

            assertThrows(IOException.class,
                    () -> load(resource, "{\"name\":\"A\",\"noSuchFeature\":1}", Map.of()));
        }

        @Test
        @DisplayName("the diagnostic behind the failure reaches the resource")
        void strictDiagnosticReachesTheResource() {
            CodecResource resource = newResource(
                    ConfigurationResolver.builder().strictOnUnknown(true).build(), false);

            assertThrows(IOException.class,
                    () -> load(resource, "{\"name\":\"A\",\"noSuchFeature\":1}", Map.of()));

            assertTrue(resource.getErrors().stream().anyMatch(d -> d.getMessage().contains("noSuchFeature")),
                    () -> "the reason has to be on record, errors=" + resource.getErrors());
            assertEquals(1, resource.getErrors().stream()
                    .filter(d -> d.getMessage().contains("noSuchFeature")).count(),
                    () -> "reported once, not again for the abort, errors=" + resource.getErrors());
        }
    }

    @Nested
    @DisplayName("Stream failures")
    class StreamFailures {

        @Test
        @DisplayName("an IOException from the input stream arrives as itself")
        void inputStreamFailureIsTheOriginalIOException() {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), false);
            IOException broken = new IOException("disk gone");

            IOException failure = assertThrows(IOException.class,
                    () -> resource.load(failingInput(broken), new HashMap<>()));

            assertSame(broken, failure, "Jackson's unchecked wrapper is unwrapped again");
        }

        @Test
        @DisplayName("an IOException from the output stream arrives as itself")
        void outputStreamFailureIsTheOriginalIOException() {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), false);
            resource.getContents().add(person("A"));
            IOException broken = new IOException("disk full");

            IOException failure = assertThrows(IOException.class,
                    () -> resource.save(failingOutput(broken), new HashMap<>()));

            assertSame(broken, failure, "Jackson's unchecked wrapper is unwrapped again");
        }

        @Test
        @DisplayName("the format-provider save path unwraps it the same way")
        void formatSaveFailureIsTheOriginalIOException() {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), true);
            resource.getContents().add(person("A"));
            IOException broken = new IOException("disk full");

            IOException failure = assertThrows(IOException.class,
                    () -> resource.save(failingOutput(broken), new HashMap<>()));

            assertSame(broken, failure);
        }

        @Test
        @DisplayName("a successful save still writes and reports nothing")
        void successfulSaveIsUntouched() throws IOException {
            CodecResource resource = newResource(ConfigurationResolver.defaults(), false);
            resource.getContents().add(person("A"));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            resource.save(out, new HashMap<>());

            assertTrue(out.toString(UTF_8).contains("\"A\""));
            assertFalse(resource.getErrors().stream().findAny().isPresent());
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource newResource(ConfigurationResolver resolver, boolean viaFormatProvider) {
        URI uri = URI.createURI("test://failures.json");
        if (viaFormatProvider) {
            return new CodecResource(uri, metadataService, resolver, null, null,
                    new JacksonFormatProvider("json", new JsonFactory()));
        }
        return new CodecResource(uri, metadataService, resolver, null);
    }

    private void load(CodecResource resource, String json, Map<String, Object> extra) throws IOException {
        Map<String, Object> options = new HashMap<>(extra);
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        resource.load(new ByteArrayInputStream(json.getBytes(UTF_8)), options);
    }

    private EObject person(String name) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(personClass.getEStructuralFeature("name"), name);
        return person;
    }

    private static InputStream failingInput(IOException failure) {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                throw failure;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                throw failure;
            }
        };
    }

    private static OutputStream failingOutput(IOException failure) {
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw failure;
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                throw failure;
            }
        };
    }
}
