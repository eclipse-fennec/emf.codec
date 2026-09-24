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
package org.eclipse.fennec.codec.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.metadata.model.codec.TypeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The builder's property maps belong to the builder, and a built resolver to nobody (issue #230).
 * <p>
 * {@code resourceProperties(map)} used to adopt the caller's map by reference, the map the
 * convenience setters write into. A setter called before it was lost without a trace, the
 * caller's map was written to, and {@code toBuilder()} handed a built resolver's maps to a new
 * builder that could change them behind the resolver's caches.
 * </p>
 */
@DisplayName("ConfigurationResolver.Builder property maps")
class BuilderPropertyMapsTest {

    private DiagnosticCollector diagnostics;
    private EClass personClass;
    private EAttribute nameAttribute;

    @BeforeEach
    void setUp() {
        diagnostics = new DiagnosticCollector();
        EPackage testPackage = EcoreFactory.eINSTANCE.createEPackage();
        testPackage.setName("test");
        testPackage.setNsPrefix("test");
        testPackage.setNsURI("http://test.org/builder/1.0");
        personClass = EcoreFactory.eINSTANCE.createEClass();
        personClass.setName("Person");
        testPackage.getEClassifiers().add(personClass);
        nameAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        nameAttribute.setName("name");
        nameAttribute.setEType(EcorePackage.Literals.ESTRING);
        personClass.getEStructuralFeatures().add(nameAttribute);
    }

    @Test
    @DisplayName("a convenience setter before resourceProperties(map) survives it")
    void setterBeforeMapSurvives() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .typeKey("type")
                .typeStrategy(TypeStrategy.NAME)
                .resourceProperties(Map.of("serializeNull", true))
                .build();

        assertEquals("type", resolver.resolveTypeConfig(personClass, diagnostics).getTypeKey());
        assertEquals(TypeStrategy.NAME, resolver.resolveTypeConfig(personClass, diagnostics).getStrategy());
        assertTrue(resolver.resolveFeatureConfig(nameAttribute, diagnostics).isSerializeNull());
    }

    @Test
    @DisplayName("a convenience setter after an immutable map does not throw")
    void setterAfterImmutableMap() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of("serializeNull", true))
                .typeKey("type")
                .build();

        assertEquals("type", resolver.resolveTypeConfig(personClass, diagnostics).getTypeKey());
    }

    @Test
    @DisplayName("the caller's map is never written to")
    void callerMapUntouched() {
        Map<String, Object> callerMap = new HashMap<>(Map.of("serializeNull", true));

        ConfigurationResolver.builder().resourceProperties(callerMap).typeKey("type").build();

        assertEquals(Map.of("serializeNull", true), callerMap);
    }

    @Test
    @DisplayName("changing the caller's map after build does not change the resolver")
    void callerMapChangedAfterBuild() {
        Map<String, Object> callerMap = new HashMap<>(Map.of("typeKey", "before"));
        ConfigurationResolver resolver = ConfigurationResolver.builder().resourceProperties(callerMap).build();

        callerMap.put("typeKey", "after");

        assertEquals("before", resolver.resolveTypeConfig(personClass, diagnostics).getTypeKey());
    }

    @Test
    @DisplayName("toBuilder() cannot change the resolver it came from")
    void toBuilderLeavesOriginalAlone() {
        ConfigurationResolver original = ConfigurationResolver.builder().typeKey("original").build();

        ConfigurationResolver derived = original.toBuilder().typeKey("derived").build();

        assertEquals("original", original.resolveTypeConfig(personClass, new DiagnosticCollector()).getTypeKey());
        assertEquals("derived", derived.resolveTypeConfig(personClass, diagnostics).getTypeKey());
    }

    @Test
    @DisplayName("for the same key, the later call wins")
    void laterCallWins() {
        ConfigurationResolver mapLast = ConfigurationResolver.builder()
                .typeKey("setter")
                .resourceProperties(Map.of("typeKey", "map"))
                .build();
        ConfigurationResolver setterLast = ConfigurationResolver.builder()
                .resourceProperties(Map.of("typeKey", "map"))
                .typeKey("setter")
                .build();

        assertEquals("map", mapLast.resolveTypeConfig(personClass, diagnostics).getTypeKey());
        assertEquals("setter", setterLast.resolveTypeConfig(personClass, new DiagnosticCollector()).getTypeKey());
    }

    @Test
    @DisplayName("two property maps for the same level are merged")
    void mapsMerge() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .resourceProperties(Map.of("typeKey", "type"))
                .resourceProperties(Map.of("serializeNull", true))
                .build();

        assertEquals("type", resolver.resolveTypeConfig(personClass, diagnostics).getTypeKey());
        assertTrue(resolver.resolveFeatureConfig(nameAttribute, diagnostics).isSerializeNull());
    }

    @Test
    @DisplayName("a null map changes nothing")
    void nullMapIsNoOp() {
        ConfigurationResolver resolver = ConfigurationResolver.builder()
                .typeKey("type")
                .resourceProperties(null)
                .optionsProperties(null)
                .build();

        assertEquals("type", resolver.resolveTypeConfig(personClass, diagnostics).getTypeKey());
    }

    @Test
    @DisplayName("every level copies: an options map is not written to either")
    void otherLevelsCopyToo() {
        Map<String, Object> options = new HashMap<>(Map.of("typeKey", "options"));
        ConfigurationResolver resolver = ConfigurationResolver.builder().optionsProperties(options).build();

        options.put("typeKey", "changed");

        assertEquals("options", resolver.resolveTypeConfig(personClass, diagnostics).getTypeKey());
    }
}
