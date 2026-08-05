
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Guard tests for issue #106: every id/supertype key the annotation parser fills must
 * survive {@link AspectToPropertiesConverter} into the property bridge, and every emitted
 * key must be canonical (resolvable via {@link ConfigProperty}).
 */
class AspectToPropertiesConverterTest {

    private EPackage testPackage;
    private EClass entityClass;
    private MetadataWhiteboard service;

    @BeforeEach
    void setUp() {
        EcoreFactory ecore = EcoreFactory.eINSTANCE;
        entityClass = ecore.createEClass();
        entityClass.setName("Entity");
        var nameAttr = ecore.createEAttribute();
        nameAttr.setName("name");
        nameAttr.setEType(EcorePackage.Literals.ESTRING);
        entityClass.getEStructuralFeatures().add(nameAttr);

        testPackage = ecore.createEPackage();
        testPackage.setName("bridgetest");
        testPackage.setNsURI("urn:codec:bridge:test");
        testPackage.setNsPrefix("bridgetest");
        testPackage.getEClassifiers().add(entityClass);

        service = MetadataServiceFactory.create();
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
    }

    private void annotate(String... keyValuePairs) {
        EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
        annotation.setSource("http://eclipse.org/fennec/codec");
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            annotation.getDetails().put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        entityClass.getEAnnotations().add(annotation);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> classProperties() {
        service.registerPackage(testPackage).orElseThrow();
        Map<String, Object> properties = AspectToPropertiesConverter.buildAnnotationProperties(service);
        Object eClassConfig = properties.get(ConfigProperty.ECLASS_CONFIG.getKey());
        assertNotNull(eClassConfig, "converter must emit an eClassConfig map");
        Map<String, Object> classProps = ((Map<EClass, Map<String, Object>>) eClassConfig).get(entityClass);
        assertNotNull(classProps, "converter must emit properties for the annotated class");
        return classProps;
    }

    @Test
    @DisplayName("all annotation-fillable id keys reach the property bridge")
    void idKeysReachTheBridge() {
        annotate(
                "idStrategy", "COMBINED",
                "idKey", "myId",
                "idFormat", "STRUCTURED",
                "idKeyMode", "BOTH",
                "idValueKey", "val",
                "idSeparator", ":",
                "idSeparatorKey", "sep",
                "idSeparatorSerialize", "false",
                "idOnTop", "false",
                "idValueReaderName", "customReader",
                "idValueWriterName", "customWriter",
                "idFeatures", "a,b");

        Map<String, Object> props = classProperties();

        assertEquals("COMBINED", props.get("idStrategy"));
        assertEquals("myId", props.get("idKey"));
        assertEquals("STRUCTURED", props.get("idFormat"));
        assertEquals("BOTH", props.get("idKeyMode"));
        assertEquals("val", props.get("idValueKey"));
        assertEquals(":", props.get("idSeparator"));
        assertEquals("sep", props.get("idSeparatorKey"));
        assertEquals(Boolean.FALSE, props.get("idSeparatorSerialize"),
                "idSeparatorSerialize was dropped by the bridge (issue #106)");
        assertEquals(Boolean.FALSE, props.get("idOnTop"),
                "idOnTop was dropped by the bridge (issue #106)");
        assertEquals("customReader", props.get("idValueReaderName"),
                "idValueReaderName was dropped by the bridge (issue #106)");
        assertEquals("customWriter", props.get("idValueWriterName"),
                "idValueWriterName was dropped by the bridge (issue #106)");
        assertEquals(List.of("a", "b"), props.get("idFeatures"));
    }

    @Test
    @DisplayName("all annotation-fillable supertype keys reach the property bridge")
    void superTypeKeysReachTheBridge() {
        annotate(
                "superTypeSerialize", "true",
                "superTypeStrategy", "SINGLE",
                "superTypeKey", "parents",
                "superTypeAsArray", "false",
                "superTypeSeparator", ";",
                "superTypeFormat", "STRUCTURED");

        Map<String, Object> props = classProperties();

        assertEquals(Boolean.TRUE, props.get("superTypeSerialize"));
        assertEquals("SINGLE", props.get("superTypeStrategy"));
        assertEquals("parents", props.get("superTypeKey"));
        assertEquals(Boolean.FALSE, props.get("superTypeAsArray"),
                "superTypeAsArray was dropped by the bridge (issue #106)");
        assertEquals(";", props.get("superTypeSeparator"),
                "superTypeSeparator was dropped by the bridge (issue #106)");
        assertEquals("STRUCTURED", props.get("superTypeFormat"),
                "superTypeFormat was dropped by the bridge (issue #106)");
    }

    @Test
    @DisplayName("an explicitly restated superTypeFormat=PLAIN is forwarded (needs unsettable format)")
    void explicitPlainSuperTypeFormatIsForwarded() {
        // PLAIN equals the model default; only an unsettable attribute (codec.ecore, issue
        // #106) lets eIsSet distinguish it from "inherit from typeFormat". Red until the
        // model is regenerated.
        annotate("superTypeSerialize", "true", "superTypeFormat", "PLAIN");

        Map<String, Object> props = classProperties();

        assertEquals("PLAIN", props.get("superTypeFormat"),
                "explicit superTypeFormat=PLAIN must be forwarded, not confused with the inherit default");
    }

    @Test
    @DisplayName("unset optional keys are not emitted")
    void unsetKeysStayAbsent() {
        annotate("idKey", "myId");

        Map<String, Object> props = classProperties();

        assertNull(props.get("idValueReaderName"));
        assertNull(props.get("idValueWriterName"));
        assertNull(props.get("idOnTop"), "the default (true) carries no information");
        assertNull(props.get("idSeparatorSerialize"), "the default (true) carries no information");
        assertNull(props.get("superTypeFormat"), "unset format must not shadow typeFormat inheritance");
    }

    @Test
    @DisplayName("every emitted class-level key is canonical")
    void emittedKeysAreCanonical() {
        annotate(
                "idStrategy", "COMBINED", "idKey", "myId", "idFormat", "STRUCTURED",
                "idKeyMode", "BOTH", "idValueKey", "val", "idSeparator", ":",
                "idSeparatorKey", "sep", "idSeparatorSerialize", "false", "idOnTop", "false",
                "idValueReaderName", "r", "idValueWriterName", "w", "idFeatures", "a,b",
                "superTypeSerialize", "true", "superTypeStrategy", "SINGLE",
                "superTypeKey", "parents", "superTypeAsArray", "false",
                "superTypeSeparator", ";", "superTypeFormat", "STRUCTURED",
                "typeStrategy", "NAME", "typeKey", "kind");

        for (String key : classProperties().keySet()) {
            assertNotNull(ConfigProperty.byKey(key),
                    "bridge emits non-canonical key '" + key + "' — it can never be consumed");
        }
    }
}
