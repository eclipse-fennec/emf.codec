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
package org.eclipse.fennec.codec.metadata.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.emf.osgi.model.metadata.AspectEntry;
import org.eclipse.fennec.emf.osgi.model.metadata.AttributeMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.ClassMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.DiagnosticSeverity;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataDiagnostic;
import org.eclipse.fennec.emf.osgi.model.metadata.MetadataFactory;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.emf.osgi.model.metadata.ReferenceMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A codec annotation detail key that nothing reads is reported (issue #236).
 * <p>
 * The option side has reported unknown {@code codec.} keys since #220; the annotation side
 * dropped them in silence. That hid a misspelt key, a key from an older dialect, and - since the
 * plural {@code serializeDefaults} was renamed without an alias - a setting a model had lost.
 * The known keys are derived from {@code CodecAnnotationConstants.KEY_*}, not kept by hand.
 * </p>
 */
@DisplayName("Unknown codec annotation keys")
class UnknownAnnotationKeyTest {

    private static final String CODEC = CodecAnnotationConstants.CODEC_SOURCE;

    private CodecAspectProvider provider;
    private EPackage ePackage;
    private EClass eClass;
    private EAttribute attribute;
    private EReference reference;

    @BeforeEach
    void setUp() {
        provider = new CodecAspectProvider();
        ePackage = EcoreFactory.eINSTANCE.createEPackage();
        ePackage.setName("unknownkeys");
        ePackage.setNsPrefix("unknownkeys");
        ePackage.setNsURI("http://test.org/unknownkeys/1.0");
        eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName("Device");
        ePackage.getEClassifiers().add(eClass);
        attribute = EcoreFactory.eINSTANCE.createEAttribute();
        attribute.setName("count");
        attribute.setEType(EcorePackage.Literals.EINT);
        eClass.getEStructuralFeatures().add(attribute);
        reference = EcoreFactory.eINSTANCE.createEReference();
        reference.setName("parts");
        reference.setEType(eClass);
        reference.setUpperBound(-1);
        eClass.getEStructuralFeatures().add(reference);
    }

    @Test
    @DisplayName("the removed plural serializeDefaults is reported, with the singular suggested")
    void pluralSerializeDefaults() {
        annotate(attribute, CODEC, "serializeDefaults", "true");

        MetadataDiagnostic warning = single(unknownKeyWarnings(featureEntry(attribute)));

        assertEquals("serializeDefaults", warning.getKey());
        assertTrue(warning.getMessage().contains("'serializeDefault'"), warning::getMessage);
    }

    @Test
    @DisplayName("a misspelt class key is reported with the key it probably means")
    void misspeltClassKey() {
        annotate(eClass, CODEC, "typeStrategyy", "NAME");

        MetadataDiagnostic warning = single(unknownKeyWarnings(classEntry()));

        assertTrue(warning.getMessage().contains("'typeStrategy'"), warning::getMessage);
        assertTrue(warning.getMessage().contains("Device"), warning::getMessage);
    }

    @Test
    @DisplayName("an unknown key with nothing close is reported without a suggestion")
    void unknownWithoutSuggestion() {
        annotate(reference, CODEC, "completelyMadeUp", "x");

        MetadataDiagnostic warning = single(unknownKeyWarnings(featureEntry(reference)));

        assertTrue(!warning.getMessage().contains("did you mean"), warning::getMessage);
    }

    @Test
    @DisplayName("an unknown key on the EPackage is reported on the package profile")
    void unknownPackageKey() {
        annotate(ePackage, CODEC, "idStrategyy", "ID_FIELD");

        MetadataDiagnostic warning = single(unknownKeyWarnings(packageEntry()));

        assertTrue(warning.getMessage().contains("'idStrategy'"), warning::getMessage);
    }

    @Test
    @DisplayName("known keys are not reported")
    void knownKeysAreSilent() {
        annotate(eClass, CODEC, "typeStrategy", "NAME");
        annotate(attribute, CODEC, "serializeDefault", "true");
        annotate(reference, CODEC, "expand", "true");

        assertTrue(unknownKeyWarnings(classEntry()).isEmpty());
        assertTrue(unknownKeyWarnings(featureEntry(attribute)).isEmpty());
        assertTrue(unknownKeyWarnings(featureEntry(reference)).isEmpty());
    }

    @Test
    @DisplayName("a known key on the wrong element is left to the placement checks, not reported as unknown")
    void misplacedKnownKeyIsNotUnknown() {
        annotate(attribute, CODEC, "typeStrategy", "NAME");

        assertTrue(unknownKeyWarnings(featureEntry(attribute)).isEmpty());
    }

    @Test
    @DisplayName("a typeMapping key close to a configuration key is reported, a mapping entry is not")
    void typeMappingKeys() {
        String source = CodecAnnotationConstants.TYPE_MAPPING_SOURCE_PREFIX + "devices";
        annotate(eClass, source, "typeDiscriminatorPth", "info.profile");
        annotate(eClass, source, "Dragino_LSE01", "http://example.org#//Dragino");

        List<MetadataDiagnostic> warnings = unknownKeyWarnings(classEntry());

        MetadataDiagnostic warning = single(warnings);
        assertEquals("typeDiscriminatorPth", warning.getKey());
        assertTrue(warning.getMessage().contains("'typeDiscriminatorPath'"), warning::getMessage);
    }

    @Test
    @DisplayName("an annotation source that looks like a codec source but is none is reported")
    void lookAlikeSource() {
        annotate(eClass, "codec", "typeStrategy", "NAME");
        annotate(eClass, "codec.type.lorawan", "typeDiscriminator", "X");
        annotate(eClass, "http://example.org/other", "whatever", "x");

        List<MetadataDiagnostic> warnings = classEntry().getDiagnostics().stream()
                .filter(d -> d.getMessage().contains("not a codec annotation source")).toList();

        assertEquals(2, warnings.size(), () -> "warnings=" + warnings.stream().map(MetadataDiagnostic::getMessage).toList());
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static void annotate(EModelElement element, String source, String key, String value) {
        EAnnotation annotation = element.getEAnnotation(source);
        if (annotation == null) {
            annotation = EcoreFactory.eINSTANCE.createEAnnotation();
            annotation.setSource(source);
            element.getEAnnotations().add(annotation);
        }
        annotation.getDetails().put(key, value);
    }

    private static List<MetadataDiagnostic> unknownKeyWarnings(AspectEntry entry) {
        return entry.getDiagnostics().stream()
                .filter(d -> d.getSeverity() == DiagnosticSeverity.WARNING)
                .filter(d -> d.getMessage().contains("nothing reads it"))
                .toList();
    }

    private static MetadataDiagnostic single(List<MetadataDiagnostic> diagnostics) {
        assertEquals(1, diagnostics.size(),
                () -> "expected one warning, got " + diagnostics.stream().map(MetadataDiagnostic::getMessage).toList());
        return diagnostics.get(0);
    }

    private PackageMetadata buildTree() {
        PackageMetadata packageMetadata = MetadataFactory.eINSTANCE.createPackageMetadata();
        packageMetadata.setEPackage(ePackage);
        ClassMetadata classMetadata = MetadataFactory.eINSTANCE.createClassMetadata();
        classMetadata.setEClass(eClass);
        classMetadata.setName(eClass.getName());
        packageMetadata.getClasses().add(classMetadata);
        for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
            if (feature instanceof EAttribute a) {
                AttributeMetadata md = MetadataFactory.eINSTANCE.createAttributeMetadata();
                md.setEFeature(a);
                md.setEAttribute(a);
                md.setName(a.getName());
                classMetadata.getFeatures().add(md);
            } else {
                ReferenceMetadata md = MetadataFactory.eINSTANCE.createReferenceMetadata();
                md.setEFeature(feature);
                md.setEReference((EReference) feature);
                md.setName(feature.getName());
                classMetadata.getFeatures().add(md);
            }
        }
        provider.onPackageRegistered(packageMetadata);
        return packageMetadata;
    }

    private AspectEntry classEntry() {
        return buildTree().getClasses().get(0).getAspects().get(0);
    }

    private AspectEntry featureEntry(EStructuralFeature feature) {
        return buildTree().getClasses().get(0).getFeatures().stream()
                .filter(f -> f.getEFeature() == feature)
                .findFirst().orElseThrow().getAspects().get(0);
    }

    private AspectEntry packageEntry() {
        return Stream.of(buildTree()).flatMap(p -> p.getAspects().stream()).findFirst().orElseThrow();
    }
}
