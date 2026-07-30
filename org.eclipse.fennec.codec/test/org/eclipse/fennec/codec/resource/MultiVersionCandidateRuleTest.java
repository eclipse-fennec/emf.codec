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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests for the A.3 count-based candidate rule (issue #54): resolving a
 * String root type URI under same-nsURI multi-version.
 * <ul>
 *   <li>exactly one registered version &rarr; resolves (R1, no behavior change);</li>
 *   <li>more than one version and no fingerprint &rarr; error listing the candidate
 *       fingerprints (no silent last-wins);</li>
 *   <li>more than one version disambiguated by {@code codec.rootFingerprint} &rarr; resolves.</li>
 * </ul>
 */
@DisplayName("Multi-version candidate rule (A.3)")
class MultiVersionCandidateRuleTest {

    private static final String NS_URI = "http://example.org/entity/1.0";
    private static final String TYPE_URI = NS_URI + "#//Entity";

    private MetadataWhiteboard metadataService;
    private EPackage packageA;
    private EPackage packageB;

    @BeforeEach
    void setUp() {
        metadataService = MetadataServiceFactory.create();
        packageA = buildVersion("alpha");
        packageB = buildVersion("beta");
    }

    private static EPackage buildVersion(String jsonKey) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName("entity");
        pkg.setNsPrefix("entity");
        pkg.setNsURI(NS_URI);
        EClass entity = EcoreFactory.eINSTANCE.createEClass();
        entity.setName("Entity");
        pkg.getEClassifiers().add(entity);
        EAttribute value = EcoreFactory.eINSTANCE.createEAttribute();
        value.setName("value");
        value.setEType(EcorePackage.eINSTANCE.getEString());
        entity.getEStructuralFeatures().add(value);
        EAnnotation ann = EcoreFactory.eINSTANCE.createEAnnotation();
        ann.setSource(AnnotationSources.CODEC);
        ann.getDetails().put("key", jsonKey);
        value.getEAnnotations().add(ann);
        return pkg;
    }

    private EObject load(String json, Map<String, Object> options) throws IOException {
        CodecResource resource = new CodecResource(
                URI.createURI("test://multiversion-candidate.json"),
                metadataService, ConfigurationResolver.defaults(), null);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        resource.load(in, options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    @Test
    @DisplayName("more than one version without a fingerprint: ambiguity error listing candidates")
    void ambiguousWithoutFingerprintIsError() {
        String fpA = metadataService.registerPackage(packageA).orElseThrow().getModelFingerprint();
        String fpB = metadataService.registerPackage(packageB).orElseThrow().getModelFingerprint();

        IOException ex = assertThrows(IOException.class,
                () -> load("{\"alpha\":\"X\"}", Map.of(CodecResource.CODEC_ROOT_TYPE, TYPE_URI)),
                "an ambiguous String root type must fail rather than pick the last-registered version");
        assertTrue(ex.getMessage().contains(fpA) && ex.getMessage().contains(fpB),
                "the error must list the candidate fingerprints: " + ex.getMessage());
    }

    @Test
    @DisplayName("more than one version disambiguated by rootFingerprint: resolves")
    void ambiguousResolvedByFingerprint() throws IOException {
        String fpA = metadataService.registerPackage(packageA).orElseThrow().getModelFingerprint();
        metadataService.registerPackage(packageB);

        EObject a = load("{\"alpha\":\"X\"}", Map.of(
                CodecResource.CODEC_ROOT_TYPE, TYPE_URI,
                CodecResource.CODEC_ROOT_FINGERPRINT, fpA));
        assertNotNull(a);
        assertSame(packageA.getEClassifier("Entity"), a.eClass());
        assertEquals("X", a.eGet(a.eClass().getEStructuralFeature("value")));
    }
}
