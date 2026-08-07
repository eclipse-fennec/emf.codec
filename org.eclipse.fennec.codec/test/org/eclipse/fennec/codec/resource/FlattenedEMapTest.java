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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@code codec.flatten} is a write-only export feature (issue #121).
 * <p>
 * A flattened EMap writes its entries straight into the parent object, so the keys are
 * whatever the map holds. Reassembling them on read is ambiguous as soon as the object
 * carries other unknown fields — there is no way to tell which key belonged to the map.
 * Flatten is therefore one-directional by design, like the tabular exports.
 * </p>
 * <p>
 * What must not happen is the reader complaining about the codec's own output: reporting
 * those keys as unknown features filled the diagnostics with noise and, under
 * {@code strictOnUnknown}, turned a self-produced document into a load failure.
 * </p>
 */
@DisplayName("Flattened EMap")
class FlattenedEMapTest {

    private static final String TEST_ECORE = "test-emap.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass holderClass;
    private EReference mapReference;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, FlattenedEMapTest.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        holderClass = EcoreHelper.getEClass(testPackage, "Container");
        // metadata is Map<String,String>; items maps to objects and would not flatten to scalars
        mapReference = (EReference) EcoreHelper.getFeature(holderClass, "metadata");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("entries are written into the parent object")
    void flattenWritesEntriesIntoParent() throws IOException {
        EObject holder = holderWithEntries();

        String json = serialize(holder);

        assertTrue(json.contains("\"alpha\""),
                "a flattened map writes its keys into the parent, was: " + json);
        assertTrue(json.contains("\"one\""), "values must be written too, was: " + json);
    }

    @Test
    @DisplayName("reading back reports no diagnostics about the flattened keys")
    void readingBackIsQuiet() throws IOException {
        String json = serialize(holderWithEntries());

        CodecResource resource = resource();
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, holderClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        // The map itself is not restored - that is the accepted trade-off. What must not
        // happen is the reader flagging its own output as unknown.
        assertTrue(resource.getErrors().isEmpty(),
                "reading back must not error: " + resource.getErrors());
        assertTrue(resource.getWarnings().isEmpty(),
                "the codec must not report its own flattened keys as unknown: "
                        + resource.getWarnings());
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    @SuppressWarnings("unchecked")
    private EObject holderWithEntries() {
        EObject holder = testPackage.getEFactoryInstance().create(holderClass);
        List<EObject> entries = (List<EObject>) holder.eGet(mapReference);

        EClass entryClass = mapReference.getEReferenceType();
        EObject entry = testPackage.getEFactoryInstance().create(entryClass);
        entry.eSet(entryClass.getEStructuralFeature("key"), "alpha");
        entry.eSet(entryClass.getEStructuralFeature("value"), "one");
        entries.add(entry);
        return holder;
    }

    private CodecResource resource() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of("codec.eReferenceConfig",
                        Map.of(mapReference, Map.of("flatten", true))))
                .build();
        return new CodecResource(URI.createURI("test://flatten.json"),
                metadataService, resolver, null);
    }

    private String serialize(EObject object) throws IOException {
        CodecResource resource = resource();
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, Collections.emptyMap());
        return out.toString(StandardCharsets.UTF_8);
    }
}
