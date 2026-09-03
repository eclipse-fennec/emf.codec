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
package org.eclipse.fennec.codec.config.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.ReferenceConfig;
import org.eclipse.fennec.codec.config.TypeConfig;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.SerializationFormat;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Feature-level codec annotations must reach the property bridge (issue #175).
 * <p>
 * {@link org.eclipse.fennec.codec.config.ConfigurationResolver} implements the feature scope of
 * the hierarchy in 02-config-resolution.md, and it works for runtime sources. What did not work
 * is the annotation input for that level: the parser fills a {@code ReferenceCodecAspect} with
 * the type and reference config it read from an {@code EReference} annotation, and
 * {@link AspectToPropertiesConverter} - the only producer of annotation properties - dropped
 * all of it. Nine documented {@code F}-level properties reached the metadata model and died
 * there, which is why the two that happen to be covered end to end ({@code ignore},
 * {@code key}) passed and masked the rest.
 * </p>
 */
@DisplayName("Feature-level annotations reach the bridge")
class FeatureAspectBridgeTest {

    private static final String NS_URI = "urn:codec:feature:bridge:test";
    private static final String CODEC_SOURCE = "http://eclipse.org/fennec/codec";

    private EPackage testPackage;
    private EClass orderClass;
    private EClass customerClass;
    private EReference customerRef;
    private EAttribute noteAttribute;
    private MetadataWhiteboard service;

    @BeforeEach
    void setUp() {
        EcoreFactory ecore = EcoreFactory.eINSTANCE;

        customerClass = ecore.createEClass();
        customerClass.setName("Customer");

        orderClass = ecore.createEClass();
        orderClass.setName("Order");

        customerRef = ecore.createEReference();
        customerRef.setName("customer");
        customerRef.setEType(customerClass);
        // Containment: the place a reference-scoped idKey/idFormat applies (09-id.md §4.4). On a
        // non-containment reference the parser drops them before the bridge sees them (#189).
        customerRef.setContainment(true);
        orderClass.getEStructuralFeatures().add(customerRef);

        noteAttribute = ecore.createEAttribute();
        noteAttribute.setName("note");
        noteAttribute.setEType(EcorePackage.Literals.ESTRING);
        orderClass.getEStructuralFeatures().add(noteAttribute);

        testPackage = ecore.createEPackage();
        testPackage.setName("featurebridge");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("featurebridge");
        testPackage.getEClassifiers().add(orderClass);
        testPackage.getEClassifiers().add(customerClass);

        service = MetadataServiceFactory.create();
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    @Test
    @DisplayName("the type keys of a reference annotation reach the bridge")
    void referenceTypeKeysReachTheBridge() {
        annotate(customerRef,
                "typeStrategy", "NAME",
                "typeKey", "_refType",
                "typeFormat", "STRUCTURED",
                "typeSchemaKey", "sch",
                "typeNameKey", "nm");

        Map<String, Object> props = referenceProperties();

        assertEquals("NAME", props.get("typeStrategy"));
        assertEquals("_refType", props.get("typeKey"));
        assertEquals("STRUCTURED", props.get("typeFormat"));
        assertEquals("sch", props.get("typeSchemaKey"));
        assertEquals("nm", props.get("typeNameKey"));
    }

    @Test
    @DisplayName("the reference keys of a reference annotation reach the bridge")
    void referenceKeysReachTheBridge() {
        annotate(customerRef,
                "refFormat", "STRUCTURED",
                "refKey", "$reference",
                "refTypeKey", "_kind",
                "expand", "true");

        Map<String, Object> props = referenceProperties();

        assertEquals("STRUCTURED", props.get("refFormat"));
        assertEquals("$reference", props.get("refKey"));
        assertEquals("_kind", props.get("refTypeKey"));
        assertEquals(Boolean.TRUE, props.get("expand"));
    }

    @Test
    @DisplayName("a key the annotation did not set is not forwarded")
    void unsetKeysStayAbsent() {
        // Forwarding a default would be worse than forwarding nothing: the feature is the
        // most specific scope, so an invented typeStrategy=URI here silently overrides
        // whatever the class configured.
        annotate(customerRef, "refKey", "$reference");

        Map<String, Object> props = referenceProperties();

        assertEquals("$reference", props.get("refKey"));
        assertNull(props.get("typeStrategy"), "the class's strategy must not be shadowed");
        assertNull(props.get("typeKey"));
        assertNull(props.get("refFormat"));
        assertNull(props.get("expand"), "the default (false) carries no information");
    }

    @Test
    @DisplayName("a value equal to the model default is forwarded")
    void aValueEqualToTheModelDefaultIsForwarded() {
        // The model defaults of BaseReferenceConfig are not the codec's: it defaults to _ref
        // and PLAIN where the codec defaults to $ref and STRUCTURED. Comparing against the
        // model default therefore dropped exactly the values a user writes to ask for the
        // model's own documented behaviour (issue #175).
        annotate(customerRef, "refKey", "_ref", "refFormat", "PLAIN");

        Map<String, Object> props = referenceProperties();

        assertEquals("_ref", props.get("refKey"), "the annotation asked for it explicitly");
        assertEquals("PLAIN", props.get("refFormat"));
    }

    @Test
    @DisplayName("an explicit false is forwarded, not omitted")
    void anExplicitFalseIsForwarded() {
        // Emitting a key only for `true` left `false` unrepresentable, so the most specific
        // scope could not opt out of what a wider one turned on.
        annotate(noteAttribute, "serializeNull", "false", "ignoreRead", "false");

        Map<String, Object> props = attributeProperties();

        assertEquals(Boolean.FALSE, props.get("serializeNull"));
        assertEquals(Boolean.FALSE, props.get("ignoreRead"));
    }

    @Test
    @DisplayName("an explicit type strategy equal to the default is forwarded")
    void anExplicitDefaultTypeStrategyIsForwarded() {
        // The class whose config the resolution starts from is the reference's target.
        annotate(customerClass, "typeStrategy", "NAME");
        annotate(customerRef, "typeStrategy", "URI");

        TypeConfig config = resolver().resolveTypeConfig(customerClass, customerRef, diagnostics());

        assertEquals(TypeStrategy.URI, config.getStrategy(),
                "the feature said URI; that it equals the default does not make it silence");
    }

    @Test
    @DisplayName("the id keys a reference may carry reach the bridge")
    void referenceIdKeysReachTheBridge() {
        annotate(customerRef, "idKey", "customerId", "idFormat", "STRUCTURED");

        Map<String, Object> props = referenceProperties();

        assertEquals("customerId", props.get("idKey"));
        assertEquals("STRUCTURED", props.get("idFormat"));
    }

    @Test
    @DisplayName("an id value equal to the model default is forwarded")
    void idValueEqualToTheModelDefaultIsForwarded() {
        annotate(customerRef, "idKey", "_id", "idFormat", "PLAIN");

        Map<String, Object> props = referenceProperties();

        assertEquals("_id", props.get("idKey"), "restating the default is still a statement");
        assertEquals("PLAIN", props.get("idFormat"));
    }

    @Test
    @DisplayName("a class-only id key on a reference is not forwarded")
    void classOnlyIdKeyOnAReferenceIsNotForwarded() {
        // The parser already reports these as errors on an EReference; the bridge must not
        // undo that by forwarding them anyway.
        annotate(customerRef, "idKey", "customerId", "idKeyMode", "BOTH");

        Map<String, Object> props = referenceProperties();

        assertEquals("customerId", props.get("idKey"));
        assertNull(props.get("idKeyMode"), "an identity's mode is class-intrinsic");
    }

    @Test
    @DisplayName("id keys on a non-containment reference never reach the bridge")
    void idKeysOnANonContainmentReferenceAreNotForwarded() {
        // A non-containment reference writes a $ref, not the target's body; the parser reports
        // idKey/idFormat there as inert and parses nothing, so there is nothing to forward (#189).
        customerRef.setContainment(false);
        annotate(customerRef, "idKey", "customerId", "idFormat", "STRUCTURED", "refKey", "cust");

        Map<String, Object> props = referenceProperties();

        assertNull(props.get("idKey"), "no id key is written in a $ref position");
        assertNull(props.get("idFormat"));
        assertEquals("cust", props.get("refKey"), "the reference's own keys still flow");
    }

    @Test
    @DisplayName("a class-level idKey equal to the default is forwarded")
    void classLevelIdKeyEqualToTheDefaultIsForwarded() {
        annotate(orderClass, "idKey", "_id");

        Map<String, Object> props = classProperties();

        assertEquals("_id", props.get("idKey"),
                "the class level had the same compare-to-default flaw (issue #175/#176)");
    }

    @Test
    @DisplayName("every emitted feature-level key is canonical")
    void emittedKeysAreCanonical() {
        annotate(customerRef,
                "typeStrategy", "NAME", "typeKey", "_refType", "typeFormat", "STRUCTURED",
                "typeSchemaKey", "sch", "typeNameKey", "nm",
                "refFormat", "STRUCTURED", "refKey", "$reference", "refTypeKey", "_kind",
                "expand", "true", "key", "cust", "ignoreRead", "true");

        for (String key : referenceProperties().keySet()) {
            assertNotNull(ConfigProperty.byKey(key),
                    "bridge emits non-canonical key '" + key + "' — it can never be consumed");
        }
    }

    @Test
    @DisplayName("the resolver serves the feature-level reference key")
    void resolverServesTheFeatureLevelRefKey() {
        annotate(customerRef, "refKey", "$reference", "refFormat", "STRUCTURED");

        ReferenceConfig config = resolver().resolveReferenceConfig(customerRef, diagnostics());

        assertEquals("$reference", config.getRefKey());
        assertEquals(SerializationFormat.STRUCTURED, config.getFormat());
    }

    @Test
    @DisplayName("the resolver serves the feature-level type strategy")
    void resolverServesTheFeatureLevelTypeStrategy() {
        annotate(customerRef, "typeStrategy", "NAME");

        TypeConfig config = resolver().resolveTypeConfig(customerClass, customerRef, diagnostics());

        assertEquals(TypeStrategy.NAME, config.getStrategy());
    }

    @Test
    @DisplayName("a feature-level annotation overrides its class")
    void featureOverridesClass() {
        annotate(orderClass, "typeStrategy", "URI", "typeKey", "_type");
        annotate(customerRef, "typeKey", "_refType");

        TypeConfig config = resolver().resolveTypeConfig(customerClass, customerRef, diagnostics());

        assertEquals("_refType", config.getTypeKey(),
                "the feature is the most specific scope of the hierarchy");
    }

    @Test
    @DisplayName("an attribute annotation still reaches the bridge unchanged")
    void attributeKeysStillReachTheBridge() {
        annotate(noteAttribute, "key", "remark", "ignoreRead", "true");

        Map<String, Object> props = attributeProperties();

        assertEquals("remark", props.get("key"));
        assertEquals(Boolean.TRUE, props.get("ignoreRead"));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void annotate(EModelElement target, String... keyValuePairs) {
        EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
        annotation.setSource(CODEC_SOURCE);
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            annotation.getDetails().put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        target.getEAnnotations().add(annotation);
    }

    private Map<String, Object> annotationProperties() {
        service.registerPackage(testPackage).orElseThrow();
        return AspectToPropertiesConverter.buildAnnotationProperties(service);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> referenceProperties() {
        Object config = annotationProperties().get(ConfigProperty.EREFERENCE_CONFIG.getKey());
        assertNotNull(config, "converter must emit an eReferenceConfig map");
        Map<String, Object> props =
                ((Map<EReference, Map<String, Object>>) config).get(customerRef);
        assertNotNull(props, "converter must emit properties for the annotated reference");
        return props;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> classProperties() {
        Object config = annotationProperties().get(ConfigProperty.ECLASS_CONFIG.getKey());
        assertNotNull(config, "converter must emit an eClassConfig map");
        Map<String, Object> props = ((Map<EClass, Map<String, Object>>) config).get(orderClass);
        assertNotNull(props, "converter must emit properties for the annotated class");
        return props;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> attributeProperties() {
        Object config = annotationProperties().get(ConfigProperty.EATTRIBUTE_CONFIG.getKey());
        assertNotNull(config, "converter must emit an eAttributeConfig map");
        Map<String, Object> props =
                ((Map<EAttribute, Map<String, Object>>) config).get(noteAttribute);
        assertNotNull(props, "converter must emit properties for the annotated attribute");
        return props;
    }

    private ConfigurationResolver resolver() {
        return ConfigurationResolver.builder()
                .annotationProperties(annotationProperties())
                .build();
    }

    private DiagnosticCollector diagnostics() {
        return new DiagnosticCollector();
    }
}
