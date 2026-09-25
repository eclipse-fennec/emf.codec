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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.DeserializationContext;

/**
 * Type Mapping Registry and Inline Mapping configured through options instead of annotations
 * (issue #239, spec 08 §4.4).
 * <p>
 * The models are built without any codec annotation: a {@code shapes} package whose
 * {@code Geometry} stands in for a class from a model the caller cannot annotate (GeoJSON), and
 * a {@code drawing} package that contains geometries across the package boundary (CQL2).
 * </p>
 */
@DisplayName("CodecResource type mappings from options")
class CodecResourceOptionTypeMappingTest {

    private static final String SHAPES_NS = "http://test.example.org/optshapes/1.0";
    private static final String DRAWING_NS = "http://test.example.org/optdrawing/1.0";

    private MetadataWhiteboard metadataService;

    private EClass geometryClass;
    private EClass pointClass;
    private EClass polygonClass;
    private EClass genericClass;
    private EClass drawingClass;
    private EReference shapesRef;
    private EReference focusRef;

    @BeforeEach
    void setUp() {
        EcoreFactory factory = EcoreFactory.eINSTANCE;

        EPackage shapes = createPackage("optshapes", SHAPES_NS);
        geometryClass = createClass(shapes, "Geometry");
        geometryClass.setAbstract(true);
        geometryClass.getEStructuralFeatures().add(createAttribute("label", EcorePackage.Literals.ESTRING));
        pointClass = createClass(shapes, "Point");
        pointClass.getESuperTypes().add(geometryClass);
        pointClass.getEStructuralFeatures().add(createAttribute("x", EcorePackage.Literals.EDOUBLE));
        polygonClass = createClass(shapes, "Polygon");
        polygonClass.getESuperTypes().add(geometryClass);
        genericClass = createClass(shapes, "GenericGeometry");
        genericClass.getESuperTypes().add(geometryClass);

        EPackage drawing = createPackage("optdrawing", DRAWING_NS);
        drawingClass = createClass(drawing, "Drawing");
        drawingClass.getEStructuralFeatures().add(createAttribute("title", EcorePackage.Literals.ESTRING));
        shapesRef = factory.createEReference();
        shapesRef.setName("shapes");
        shapesRef.setEType(geometryClass);
        shapesRef.setContainment(true);
        shapesRef.setUpperBound(-1);
        drawingClass.getEStructuralFeatures().add(shapesRef);
        focusRef = factory.createEReference();
        focusRef.setName("focus");
        focusRef.setEType(geometryClass);
        focusRef.setContainment(true);
        drawingClass.getEStructuralFeatures().add(focusRef);

        metadataService = MetadataServiceFactory.create();
        for (EPackage pkg : List.of(shapes, drawing)) {
            EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
            metadataService.registerPackage(pkg);
        }
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(SHAPES_NS);
        EPackage.Registry.INSTANCE.remove(DRAWING_NS);
    }

    // ========================================================================
    // Type Mapping Registry from codec.eClassConfig
    // ========================================================================

    @Nested
    @DisplayName("Type Mapping Registry")
    class TypeMappingRegistry {

        @Test
        @DisplayName("reads values mapped by URI string and by EClass instance")
        void readsMappedValues() throws IOException {
            Map<String, Object> options = geometryMapping("ERROR", null);

            EObject result = load("""
                    { "title": "t", "shapes": [
                        { "type": "point", "x": 1.5 },
                        { "type": "polygon" } ] }
                    """, options);

            List<EObject> shapes = shapes(result);
            assertEquals(pointClass, shapes.get(0).eClass());
            assertEquals(1.5, (Double) shapes.get(0).eGet(pointClass.getEStructuralFeature("x")), 0.001);
            assertEquals(polygonClass, shapes.get(1).eClass());
        }

        @Test
        @DisplayName("an unmapped value fails with the configured ERROR fallback")
        void unmappedValueFails() {
            Map<String, Object> options = geometryMapping("ERROR", null);

            IOException error = assertThrows(IOException.class, () -> load("""
                    { "title": "t", "shapes": [ { "type": "line" } ] }
                    """, options));
            assertTrue(error.getMessage().contains("[geojson]"), error.getMessage());
        }

        @Test
        @DisplayName("an unmapped value takes the configured fallbackEClass")
        void unmappedValueFallsBack() throws IOException {
            Map<String, Object> options = geometryMapping("FALLBACK", uri(genericClass));

            EObject result = load("""
                    { "title": "t", "shapes": [ { "type": "line", "label": "l" } ] }
                    """, options);

            EObject shape = shapes(result).get(0);
            assertEquals(genericClass, shape.eClass());
            assertEquals("l", shape.eGet(geometryClass.getEStructuralFeature("label")));
        }

        @Test
        @DisplayName("writes the mapped value and reads it back")
        void writesMappedValue() throws IOException {
            Map<String, Object> options = geometryMapping("ERROR", null);
            EObject drawing = EcoreUtil.create(drawingClass);
            @SuppressWarnings("unchecked")
            List<EObject> shapes = (List<EObject>) drawing.eGet(shapesRef);
            shapes.add(EcoreUtil.create(polygonClass));

            String json = save(drawing, options);

            assertTrue(json.replace(" ", "").contains("\"type\":\"polygon\""), json);
            assertEquals(polygonClass, shapes(load(json, options)).get(0).eClass());
        }

        @Test
        @DisplayName("a class registers its own value through typeDiscriminator")
        void distributedRegistration() throws IOException {
            Map<EClass, Object> classConfig = typeKeyForAll();
            classConfig.put(geometryClass, Map.of(
                    CodecOptions.CODEC_TYPE_KEY, "type",
                    CodecOptions.CODEC_TYPE_MAP_ID, "geojson",
                    CodecOptions.CODEC_TYPE_DISCRIMINATOR_PATH, "type"));
            classConfig.put(pointClass, Map.of(
                    CodecOptions.CODEC_TYPE_KEY, "type",
                    CodecOptions.CODEC_TYPE_MAP_ID, "geojson",
                    CodecOptions.CODEC_TYPE_DISCRIMINATOR, "pt"));
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_ECLASS_CONFIG, classConfig);

            EObject result = load("""
                    { "title": "t", "shapes": [ { "type": "pt" } ] }
                    """, options);

            assertEquals(pointClass, shapes(result).get(0).eClass());
        }

        @Test
        @DisplayName("mappings stay with the load that configured them")
        void mappingsStayInTheirLoad() throws IOException {
            load("""
                    { "title": "t", "shapes": [ { "type": "point" } ] }
                    """, geometryMapping("ERROR", null));

            Map<String, Object> withoutMappings = new HashMap<>();
            withoutMappings.put(CodecOptions.CODEC_ECLASS_CONFIG, typeKeyForAll());

            assertThrows(IOException.class, () -> load("""
                    { "title": "t", "shapes": [ { "type": "point" } ] }
                    """, withoutMappings),
                    "a later load without the options must not see the earlier load's mappings");
        }

        @Test
        @DisplayName("an externally managed reader ignores option mappings and says so")
        void externalReaderReportsIgnoredMappings() {
            TypeDiscriminatorService external = new TypeDiscriminatorService();
            CodecResource resource = new CodecResource(URI.createURI("test://external.json"),
                    metadataService, ConfigurationResolver.defaults(), null, null, null, external);

            assertThrows(IOException.class, () -> load(resource, """
                    { "title": "t", "shapes": [ { "type": "point" } ] }
                    """, geometryMapping("ERROR", null)));
            assertTrue(resource.getWarnings().stream().map(Resource.Diagnostic::getMessage)
                    .anyMatch(m -> m.contains("externally managed")),
                    "expected a warning about the ignored mappings, got " + resource.getWarnings());
        }
    }

    // ========================================================================
    // Inline Mapping from codec.eReferenceConfig
    // ========================================================================

    @Nested
    @DisplayName("Inline Mapping")
    class InlineMapping {

        @Test
        @DisplayName("resolves a value mapped on the reference")
        void readsInlineMapping() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_ECLASS_CONFIG, typeKeyForAll());
            options.put(CodecOptions.CODEC_EREFERENCE_CONFIG, Map.of(focusRef, Map.of(
                    CodecOptions.CODEC_INLINE_MAPPINGS, Map.of("p", pointClass))));

            EObject result = load("""
                    { "title": "t", "focus": { "type": "p", "x": 2.0 } }
                    """, options);

            EObject focus = (EObject) result.eGet(focusRef);
            assertEquals(pointClass, focus.eClass());
        }

        @Test
        @DisplayName("writes the value mapped on the reference")
        void writesInlineMapping() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_ECLASS_CONFIG, typeKeyForAll());
            options.put(CodecOptions.CODEC_EREFERENCE_CONFIG, Map.of(focusRef, Map.of(
                    CodecOptions.CODEC_INLINE_MAPPINGS, Map.of("p", pointClass))));
            EObject drawing = EcoreUtil.create(drawingClass);
            drawing.eSet(focusRef, EcoreUtil.create(pointClass));

            String json = save(drawing, options);

            assertTrue(json.replace(" ", "").contains("\"type\":\"p\""), json);
            assertNotEquals(-1, json.indexOf("focus"), json);
        }
    }

    // ========================================================================
    // Embedded object handed to the codec by a ReferenceValueReader (issue #244)
    // ========================================================================

    /**
     * The delegation of emf.ogc.features: a reader bound to the reference sets the expected type
     * and hands the nested object to the codec's root deserializer. The context schema is the
     * root's package ({@code drawing}), the object belongs to {@code shapes} (spec 06 §6.4.7).
     */
    @Nested
    @DisplayName("Embedded through a ReferenceValueReader")
    class EmbeddedThroughValueReader {

        private static final String POLYGON = """
                { "title": "t", "focus": { "type": "Polygon", "label": "p" } }
                """;

        @Test
        @DisplayName("NAME alone does not reach the embedded package")
        void nameAloneFails() {
            Map<String, Object> options = withReader(nameForAll(), false);

            IOException error = assertThrows(IOException.class, () -> load(POLYGON, options));
            assertTrue(error.getMessage().contains("abstract"), error.getMessage());
        }

        @Test
        @DisplayName("a type mapping from options resolves the embedded object")
        void typeMappingResolves() throws IOException {
            Map<EClass, Object> classConfig = nameForAll();
            classConfig.put(geometryClass, Map.of(
                    CodecOptions.CODEC_TYPE_KEY, "type",
                    CodecOptions.CODEC_TYPE_MAP_ID, "geojson",
                    CodecOptions.CODEC_TYPE_DISCRIMINATOR_PATH, "type",
                    CodecOptions.CODEC_TYPE_MAPPINGS, Map.of("Point", pointClass, "Polygon", polygonClass),
                    CodecOptions.CODEC_FALLBACK_STRATEGY, "ERROR"));

            EObject focus = (EObject) load(POLYGON, withReader(classConfig, false)).eGet(focusRef);

            assertEquals(polygonClass, focus.eClass());
        }

        @Test
        @DisplayName("switching the context schema around the nested read resolves the embedded object")
        void contextSchemaSwitchResolves() throws IOException {
            EObject drawing = load(POLYGON, withReader(nameForAll(), true));

            assertEquals(polygonClass, ((EObject) drawing.eGet(focusRef)).eClass());
        }

        /** Every geometry class reads its type by simple name under the key "type". */
        private Map<EClass, Object> nameForAll() {
            Map<EClass, Object> classConfig = new HashMap<>();
            for (EClass eClass : List.of(geometryClass, pointClass, polygonClass, genericClass)) {
                classConfig.put(eClass, Map.of(
                        CodecOptions.CODEC_TYPE_KEY, "type",
                        CodecOptions.CODEC_TYPE_STRATEGY, "NAME"));
            }
            return classConfig;
        }

        private Map<String, Object> withReader(Map<EClass, Object> classConfig, boolean switchSchema) {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_ECLASS_CONFIG, classConfig);
            options.put(CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES,
                    Map.of(focusRef, delegatingReader(switchSchema)));
            return options;
        }

        private ReferenceValueReader<EObject> delegatingReader(boolean switchSchema) {
            return new ReferenceValueReader<>() {
                @Override
                public String getName() {
                    return "geometry";
                }

                @Override
                public boolean canHandle(EReference reference) {
                    return reference == focusRef;
                }

                @Override
                public EObject read(CodecReaderContext ctx, EReference reference) throws IOException {
                    DeserializationContext ctxt = ctx.getJacksonContext();
                    String previous = ContextHelper.getContextSchemaUri(ctxt);
                    if (switchSchema) {
                        ContextHelper.setContextSchemaUri(ctxt, SHAPES_NS);
                    }
                    try {
                        ContextHelper.setExpectedType(ctxt, geometryClass);
                        return (EObject) ctxt.findRootValueDeserializer(ctxt.constructType(EObject.class))
                                .deserialize(ctx.getParser(), ctxt);
                    } finally {
                        ContextHelper.setContextSchemaUri(ctxt, previous);
                    }
                }
            };
        }
    }

    // ========================================================================
    // Options next to a model's own typeMapping annotations
    // ========================================================================

    @Nested
    @DisplayName("Options over annotations")
    class OptionsOverAnnotations {

        private EcoreHelper ecoreHelper;
        private EPackage annotated;
        private MetadataWhiteboard annotatedService;
        private EClass sensorClass;
        private EClass tempSensorClass;

        @BeforeEach
        void setUpAnnotated() throws IOException {
            ecoreHelper = new EcoreHelper();
            annotated = ecoreHelper.loadEcore("/org/eclipse/fennec/codec/resource/test-inline-mapping.ecore",
                    CodecResourceOptionTypeMappingTest.class);
            EPackage.Registry.INSTANCE.put(annotated.getNsURI(), annotated);
            annotatedService = MetadataServiceFactory.create();
            annotatedService.registerPackage(annotated);
            sensorClass = EcoreHelper.getEClass(annotated, "Sensor");
            tempSensorClass = EcoreHelper.getEClass(annotated, "TempSensor");
        }

        @AfterEach
        void tearDownAnnotated() {
            EPackage.Registry.INSTANCE.remove(annotated.getNsURI());
            ecoreHelper.releaseAll();
        }

        @Test
        @DisplayName("an option adds a value to an annotated registry")
        void optionExtendsAnnotatedRegistry() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_ECLASS_CONFIG, Map.of(sensorClass, Map.of(
                    CodecOptions.CODEC_TYPE_MAPPINGS, Map.of("hot", tempSensorClass))));

            assertEquals(tempSensorClass, loadSensor("hot", options).eClass());
            assertEquals(tempSensorClass, loadSensor("temp", options).eClass(),
                    "the annotated value keeps working");
        }

        @Test
        @DisplayName("an option overrides the annotated fallback strategy")
        void optionOverridesAnnotatedFallback() throws IOException {
            Map<String, Object> options = new HashMap<>();
            options.put(CodecOptions.CODEC_ECLASS_CONFIG, Map.of(sensorClass, Map.of(
                    CodecOptions.CODEC_FALLBACK_STRATEGY, "FALLBACK",
                    CodecOptions.CODEC_FALLBACK_ECLASS, uri(tempSensorClass))));

            assertEquals(tempSensorClass, loadSensor("unknown-sensor", options).eClass(),
                    "without the option, strict-sensors' ERROR fails this load");
        }

        private EObject loadSensor(String discriminator, Map<String, Object> options) throws IOException {
            CodecResource resource = new CodecResource(URI.createURI("test://sensor.json"),
                    annotatedService, ConfigurationResolver.defaults(), null);
            Map<String, Object> loadOptions = new HashMap<>(options);
            loadOptions.put(CodecResource.CODEC_ROOT_TYPE, sensorClass);
            String json = "{ \"_type\": \"" + discriminator + "\", \"sensorId\": \"s\" }";
            resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), loadOptions);
            return resource.getContents().get(0);
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    /** Geometry as a closed registry: point by URI string, polygon by EClass instance. */
    private Map<String, Object> geometryMapping(String fallbackStrategy, String fallbackEClass) {
        Map<String, Object> geometry = new HashMap<>();
        geometry.put(CodecOptions.CODEC_TYPE_KEY, "type");
        geometry.put(CodecOptions.CODEC_TYPE_MAP_ID, "geojson");
        geometry.put(CodecOptions.CODEC_TYPE_DISCRIMINATOR_PATH, "type");
        geometry.put(CodecOptions.CODEC_TYPE_MAPPINGS, Map.of(
                "point", uri(pointClass),
                "polygon", polygonClass));
        geometry.put(CodecOptions.CODEC_FALLBACK_STRATEGY, fallbackStrategy);
        if (fallbackEClass != null) {
            geometry.put(CodecOptions.CODEC_FALLBACK_ECLASS, fallbackEClass);
        }
        Map<EClass, Object> classConfig = typeKeyForAll();
        classConfig.put(geometryClass, geometry);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecOptions.CODEC_ECLASS_CONFIG, classConfig);
        return options;
    }

    /** Class options are not inherited, so every geometry class gets the type key. */
    private Map<EClass, Object> typeKeyForAll() {
        Map<EClass, Object> classConfig = new HashMap<>();
        for (EClass eClass : List.of(geometryClass, pointClass, polygonClass, genericClass)) {
            classConfig.put(eClass, Map.of(CodecOptions.CODEC_TYPE_KEY, "type"));
        }
        return classConfig;
    }

    private EObject load(String json, Map<String, Object> options) throws IOException {
        return load(new CodecResource(URI.createURI("test://drawing.json"), metadataService,
                ConfigurationResolver.defaults(), null), json, options);
    }

    private EObject load(CodecResource resource, String json, Map<String, Object> options) throws IOException {
        Map<String, Object> loadOptions = new HashMap<>(options);
        loadOptions.put(CodecResource.CODEC_ROOT_TYPE, drawingClass);
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), loadOptions);
        return resource.getContents().get(0);
    }

    private String save(EObject object, Map<String, Object> options) throws IOException {
        CodecResource resource = new CodecResource(URI.createURI("test://drawing.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        resource.getContents().add(object);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toString(StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private List<EObject> shapes(EObject drawing) {
        return (List<EObject>) drawing.eGet(shapesRef);
    }

    private static String uri(EClass eClass) {
        return EcoreUtil.getURI(eClass).toString();
    }

    private static EPackage createPackage(String name, String nsUri) {
        EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
        pkg.setName(name);
        pkg.setNsPrefix(name);
        pkg.setNsURI(nsUri);
        // Resource-backed, so EcoreUtil.getURI yields nsURI#//Class
        new ResourceImpl(URI.createURI(nsUri)).getContents().add(pkg);
        return pkg;
    }

    private static EClass createClass(EPackage pkg, String name) {
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(name);
        pkg.getEClassifiers().add(eClass);
        return eClass;
    }

    private static EAttribute createAttribute(String name, EDataType type) {
        EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
        attribute.setName(name);
        attribute.setEType(type);
        return attribute;
    }
}
