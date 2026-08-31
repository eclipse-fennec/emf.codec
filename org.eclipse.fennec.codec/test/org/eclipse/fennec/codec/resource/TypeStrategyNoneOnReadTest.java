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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@code typeStrategy=NONE} on the read side (issue #171, spec 06-type.md §1.4).
 */
@DisplayName("TypeStrategy.NONE on read")
class TypeStrategyNoneOnReadTest {

    private static final String NS_URI = "http://test.example.org/batch/1.0";

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass batchClass;
    private EClass entryClass;
    private EAttribute typeAttribute;
    private EAttribute nameAttribute;
    private EAttribute entryTypeAttribute;
    private EReference entriesReference;

    @BeforeEach
    void setUp() {
        testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("batch");
        testPackage.setNsPrefix("batch");
        testPackage.setNsURI(NS_URI);

        batchClass = EcoreFactory.eINSTANCE.createEClass();
        batchClass.setName("MessageBatch");
        testPackage.getEClassifiers().add(batchClass);

        typeAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        typeAttribute.setName("type");
        typeAttribute.setEType(EcorePackage.Literals.ESTRING);
        batchClass.getEStructuralFeatures().add(typeAttribute);

        nameAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttribute.setName("name");
        nameAttribute.setEType(EcorePackage.Literals.ESTRING);
        batchClass.getEStructuralFeatures().add(nameAttribute);

        entryClass = EcoreFactory.eINSTANCE.createEClass();
        entryClass.setName("Entry");
        testPackage.getEClassifiers().add(entryClass);

        entryTypeAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        entryTypeAttribute.setName("type");
        entryTypeAttribute.setEType(EcorePackage.Literals.ESTRING);
        entryClass.getEStructuralFeatures().add(entryTypeAttribute);

        entriesReference = EcoreFactory.eINSTANCE.createEReference();
        entriesReference.setName("entries");
        entriesReference.setEType(entryClass);
        entriesReference.setContainment(true);
        entriesReference.setUpperBound(-1);
        batchClass.getEStructuralFeatures().add(entriesReference);

        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(testPackage);
        EPackage.Registry.INSTANCE.put(NS_URI, testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    @Test
    @DisplayName("no type resolution diagnostics when the strategy is NONE")
    void noneSkipsBodyTypeResolution() throws IOException {
        CodecResource resource = loadInto("{\"type\":\"message_batch\",\"name\":\"nightly\"}");

        assertTrue(resource.getWarnings().isEmpty(),
                "NONE declares that no type discriminator exists, was: " + resource.getWarnings());
        assertTrue(resource.getErrors().isEmpty(),
                "NONE declares that no type discriminator exists, was: " + resource.getErrors());
    }

    @Test
    @DisplayName("the declared root type determines the root class")
    void declaredRootTypeWins() throws IOException {
        EObject loaded = loadInto("{\"type\":\"message_batch\",\"name\":\"nightly\"}")
                .getContents().get(0);

        assertEquals(batchClass, loaded.eClass());
        assertEquals("message_batch", loaded.eGet(typeAttribute),
                "with NONE the value is plain data, not a discriminator");
        assertEquals("nightly", loaded.eGet(nameAttribute));
    }

    @Test
    @DisplayName("a nested object takes its class from the reference type")
    @SuppressWarnings("unchecked")
    void nestedObjectUsesTheReferenceType() throws IOException {
        CodecResource resource = loadInto(
                "{\"type\":\"message_batch\",\"entries\":[{\"type\":\"message\"}]}");

        assertTrue(resource.getWarnings().isEmpty(),
                "the reference type is the documented fallback, was: " + resource.getWarnings());

        EObject loaded = resource.getContents().get(0);
        List<EObject> entries = (List<EObject>) loaded.eGet(entriesReference);
        assertEquals(1, entries.size());
        assertEquals(entryClass, entries.get(0).eClass());
        assertEquals("message", entries.get(0).eGet(entryTypeAttribute));
    }

    @Test
    @DisplayName("a value shaped like a type URI stays data under NONE")
    void aUriShapedValueIsNotResolved() throws IOException {
        CodecResource resource = loadInto(
                "{\"type\":\"" + NS_URI + "#//Entry\",\"name\":\"nightly\"}");

        assertTrue(resource.getWarnings().isEmpty(),
                "NONE means no lookup at all, was: " + resource.getWarnings());
        assertEquals(batchClass, resource.getContents().get(0).eClass(),
                "the declared root type decides, not a value that happens to look resolvable");
    }

    @Test
    @DisplayName("without a declared root type the root cannot be determined")
    void noneWithoutRootTypeIsAnError() throws IOException {
        CodecResource resource = loadWithoutRootType(
                "{\"type\":\"message_batch\",\"name\":\"nightly\"}");

        assertTrue(resource.getContents().isEmpty(), "nothing can be instantiated");
        assertTrue(resource.getErrors().stream()
                        .anyMatch(e -> e.getMessage().contains("typeStrategy=NONE transports no"
                                + " type information")),
                "the reason has to name NONE, not an unresolvable value, was: "
                        + resource.getErrors());
    }

    private CodecResource loadInto(String json) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("batch.json"),
                metadataService, noneResolver(), null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, batchClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource;
    }

    private CodecResource loadWithoutRootType(String json) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("batch.json"),
                metadataService, noneResolver(), null);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
                new HashMap<>());
        return resource;
    }

    private static ConfigurationResolver noneResolver() {
        return ConfigurationResolver.builder()
                .resourceProperties(Map.of("typeKey", "type", "typeStrategy", "NONE"))
                .build();
    }
}
