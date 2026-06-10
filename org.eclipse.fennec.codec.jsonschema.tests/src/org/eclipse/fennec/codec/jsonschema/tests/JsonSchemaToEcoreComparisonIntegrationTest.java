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
package org.eclipse.fennec.codec.jsonschema.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.test.common.annotation.InjectService;

/**
 * OSGi integration tests that take the bundled JSON Schema documents
 * ({@code core-ir.schema.json} and {@code mapping.schema.json}), convert them
 * to EMF {@link EPackage}s via the registered {@code application/schema+json}
 * resource, and compare the result against the hand-authored reference Ecore
 * models ({@code CORE_DataSet.ecore} and {@code CORE_Mapping.ecore}).
 * <p>
 * The comparison is structural: package metadata, the set of classifiers, and
 * per-{@link EClass}/{@link EEnum} details (abstract flag, instance class name,
 * super types, structural features with their type/bounds/containment, and enum
 * literals). Each {@code @Test} aggregates every divergence for its scope and
 * fails with a full report, so a failure pinpoints exactly where the converter
 * output differs from the reference model.
 * </p>
 * <p>
 * The reference {@code .ecore} models capture what the current converter can
 * actually infer from the schemas (e.g. enum names derived as
 * {@code <OwningClass><Property>}, {@code oneOf}-of-{@code $ref}s modeled as an
 * abstract supertype, {@code additionalProperties} maps as {@code …MapEntry}
 * classes). Modeling choices that are <em>not</em> expressible in the schema —
 * curated enum names like {@code HttpMethod}/{@code EdgeKind}, collapsing
 * {@code MappingField} into {@code MappingOperation}, or treating {@code $ref}
 * collections as containment — are intentionally absent from the references.
 * </p>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("JSON Schema → Ecore conversion vs reference Ecore")
public class JsonSchemaToEcoreComparisonIntegrationTest {

	private static final String BASE = "/org/eclipse/fennec/codec/jsonschema/tests/";

	private static final String CORE_IR_SCHEMA = BASE + "core-ir.schema.json";
	private static final String CORE_DATASET_ECORE = BASE + "CORE_DataSet.ecore";

	private static final String MAPPING_SCHEMA = BASE + "mapping.schema.json";
	private static final String CORE_MAPPING_ECORE = BASE + "CORE_Mapping.ecore";

	@InjectService(filter = "(emf.contentType=application/schema+json)")
	ResourceSet resourceSet;

	private EcoreHelper ecoreHelper;

	@BeforeEach
	public void setUp() {
		ecoreHelper = new EcoreHelper();
	}

	@AfterEach
	public void tearDown() {
		ecoreHelper.releaseAll();
	}

	// ========================================================================
	// Loading helpers
	// ========================================================================

	/**
	 * Loads a JSON Schema document from the test classpath and converts it to an
	 * {@link EPackage} through the registered {@code application/schema+json}
	 * resource (the same path production code uses).
	 */
	private EPackage convertSchema(String classpathResource) throws IOException {
		Resource resource = resourceSet.createResource(URI.createURI("converted.jsonschema"));
		try (InputStream in = getClass().getResourceAsStream(classpathResource)) {
			assertNotNull(in, "JSON Schema resource not found on classpath: " + classpathResource);
			resource.load(in, null);
		}
		assertFalse(resource.getContents().isEmpty(),
				"conversion of " + classpathResource + " produced no content");
		assertTrue(resource.getContents().get(0) instanceof EPackage,
				"conversion root should be an EPackage, was: " + resource.getContents().get(0));
		return (EPackage) resource.getContents().get(0);
	}

	/** Loads a reference Ecore model from the test classpath. */
	private EPackage loadExpected(String classpathResource) throws IOException {
		EPackage pkg = ecoreHelper.loadEcore(classpathResource, JsonSchemaToEcoreComparisonIntegrationTest.class);
		assertNotNull(pkg, "reference Ecore not found on classpath: " + classpathResource);
		return pkg;
	}

	// ========================================================================
	// Structural comparison helpers — collect human readable differences
	// ========================================================================

	private static Set<String> classifierNames(EPackage pkg) {
		return pkg.getEClassifiers().stream()
				.map(EClassifier::getName)
				.collect(Collectors.toCollection(TreeSet::new));
	}

	private static EClassifier classifier(EPackage pkg, String name) {
		return pkg.getEClassifier(name);
	}

	private static Set<String> superTypeNames(EClass eClass) {
		return eClass.getESuperTypes().stream()
				.map(EClass::getName)
				.collect(Collectors.toCollection(TreeSet::new));
	}

	private static String typeName(EStructuralFeature feature) {
		EClassifier type = feature.getEType();
		return type == null ? "<null>" : type.getName();
	}

	private List<String> diffPackage(EPackage expected, EPackage actual) {
		List<String> diffs = new ArrayList<>();
		if (!eq(expected.getName(), actual.getName())) {
			diffs.add("package name: expected '" + expected.getName() + "' but was '" + actual.getName() + "'");
		}
		if (!eq(expected.getNsURI(), actual.getNsURI())) {
			diffs.add("package nsURI: expected '" + expected.getNsURI() + "' but was '" + actual.getNsURI() + "'");
		}
		if (!eq(expected.getNsPrefix(), actual.getNsPrefix())) {
			diffs.add("package nsPrefix: expected '" + expected.getNsPrefix() + "' but was '" + actual.getNsPrefix() + "'");
		}
		return diffs;
	}

	private List<String> diffClassifierSet(EPackage expected, EPackage actual) {
		List<String> diffs = new ArrayList<>();
		Set<String> expectedNames = classifierNames(expected);
		Set<String> actualNames = classifierNames(actual);

		Set<String> missing = new TreeSet<>(expectedNames);
		missing.removeAll(actualNames);
		if (!missing.isEmpty()) {
			diffs.add("missing classifiers: " + missing);
		}

		Set<String> extra = new TreeSet<>(actualNames);
		extra.removeAll(expectedNames);
		if (!extra.isEmpty()) {
			diffs.add("unexpected classifiers: " + extra);
		}
		return diffs;
	}

	/** Compares a single expected classifier against its counterpart in {@code actualPkg}. */
	private List<String> diffClassifier(EClassifier expected, EPackage actualPkg) {
		List<String> diffs = new ArrayList<>();
		EClassifier actual = classifier(actualPkg, expected.getName());
		if (actual == null) {
			diffs.add("missing classifier: " + expected.getName());
			return diffs;
		}
		if (expected instanceof EClass expClass) {
			if (!(actual instanceof EClass actClass)) {
				diffs.add(expected.getName() + ": expected EClass but was " + actual.eClass().getName());
				return diffs;
			} else {
				diffs.addAll(diffClass(expClass, actClass));
			}
		} else if (expected instanceof EEnum expEnum) {
			if (!(actual instanceof EEnum actEnum)) {
				diffs.add(expected.getName() + ": expected EEnum but was " + actual.eClass().getName());
				return diffs;
			} else {
				diffs.addAll(diffEnum(expEnum, actEnum));
			}
		}
		return diffs;
	}

	private List<String> diffClass(EClass expected, EClass actual) {
		List<String> diffs = new ArrayList<>();
		String ctx = expected.getName();

		if (expected.isAbstract() != actual.isAbstract()) {
			diffs.add(ctx + ": abstract expected " + expected.isAbstract() + " but was " + actual.isAbstract());
		}
		if (!eq(expected.getInstanceClassName(), actual.getInstanceClassName())) {
			diffs.add(ctx + ": instanceClassName expected '" + expected.getInstanceClassName()
					+ "' but was '" + actual.getInstanceClassName() + "'");
		}

		Set<String> expSuper = superTypeNames(expected);
		Set<String> actSuper = superTypeNames(actual);
		if (!expSuper.equals(actSuper)) {
			diffs.add(ctx + ": superTypes expected " + expSuper + " but was " + actSuper);
		}

		for (EStructuralFeature expFeature : expected.getEStructuralFeatures()) {
			EStructuralFeature actFeature = actual.getEStructuralFeature(expFeature.getName());
			diffs.addAll(diffFeature(ctx, expFeature, actFeature));
		}
		for (EStructuralFeature actFeature : actual.getEStructuralFeatures()) {
			if (expected.getEStructuralFeature(actFeature.getName()) == null) {
				diffs.add(ctx + ": unexpected feature '" + actFeature.getName() + "'");
			}
		}
		return diffs;
	}

	private List<String> diffFeature(String ctx, EStructuralFeature expected, EStructuralFeature actual) {
		List<String> diffs = new ArrayList<>();
		String fctx = ctx + "." + expected.getName();
		if (actual == null) {
			diffs.add(fctx + ": missing feature");
			return diffs;
		}

		boolean expRef = expected instanceof EReference;
		boolean actRef = actual instanceof EReference;
		if (expRef != actRef) {
			diffs.add(fctx + ": kind expected " + (expRef ? "EReference" : "EAttribute")
					+ " but was " + (actRef ? "EReference" : "EAttribute"));
			return diffs;
		}

		if (!eq(typeName(expected), typeName(actual))) {
			diffs.add(fctx + ": type expected '" + typeName(expected) + "' but was '" + typeName(actual) + "'");
		}
		if (expected.getLowerBound() != actual.getLowerBound()) {
			diffs.add(fctx + ": lowerBound expected " + expected.getLowerBound() + " but was " + actual.getLowerBound());
		}
		if (expected.getUpperBound() != actual.getUpperBound()) {
			diffs.add(fctx + ": upperBound expected " + expected.getUpperBound() + " but was " + actual.getUpperBound());
		}
		if (expRef) {
			EReference expReference = (EReference) expected;
			EReference actReference = (EReference) actual;
			if (expReference.isContainment() != actReference.isContainment()) {
				diffs.add(fctx + ": containment expected " + expReference.isContainment()
						+ " but was " + actReference.isContainment());
			}
		}
		return diffs;
	}

	private List<String> diffEnum(EEnum expected, EEnum actual) {
		List<String> diffs = new ArrayList<>();
		String ctx = expected.getName();
		List<String> expLiterals = expected.getELiterals().stream()
				.map(EEnumLiteral::getName).collect(Collectors.toList());
		List<String> actLiterals = actual.getELiterals().stream()
				.map(EEnumLiteral::getName).collect(Collectors.toList());
		if (!expLiterals.equals(actLiterals)) {
			diffs.add(ctx + ": literals expected " + expLiterals + " but was " + actLiterals);
		}
		return diffs;
	}

	private static boolean eq(Object a, Object b) {
		return a == null ? b == null : a.equals(b);
	}

	private void assertNoDiffs(String label, List<String> diffs) {
		assertTrue(diffs.isEmpty(),
				() -> label + " — " + diffs.size() + " difference(s):\n  " + String.join("\n  ", diffs));
	}

	/** Asserts a single expected classifier matches its converted counterpart. */
	private void assertClassifierMatches(EClassifier expected, EPackage actualPkg) {
		assertNoDiffs(expected.getName(), diffClassifier(expected, actualPkg));
	}

	// ========================================================================
	// CORE DataSet
	// ========================================================================

	@Nested
	@DisplayName("core-ir.schema.json vs CORE_DataSet.ecore")
	class CoreDataSetSchema {

		private EPackage expected;
		private EPackage converted;

		@BeforeEach
		void load() throws IOException {
			expected = loadExpected(CORE_DATASET_ECORE);
			converted = convertSchema(CORE_IR_SCHEMA);
		}

		@Test
		@DisplayName("package metadata (name, nsURI, nsPrefix)")
		void packageMetadata() {
			assertNoDiffs("package metadata", diffPackage(expected, converted));
		}

		@Test
		@DisplayName("all classifiers present and none extra")
		void classifierSet() {
			assertNoDiffs("classifier set", diffClassifierSet(expected, converted));
		}

		@Test
		@DisplayName("root class CORE_DataSet")
		void rootClass() {
			assertClassifierMatches(classifier(expected, "CORE_DataSet"), converted);
		}

		@Test
		@DisplayName("DataStructuresMapEntry / DataStructure")
		void dataStructures() {
			assertClassifierMatches(classifier(expected, "DataStructuresMapEntry"), converted);
			assertClassifierMatches(classifier(expected, "DataStructure"), converted);
		}

		@Test
		@DisplayName("DataSource")
		void dataSource() {
			assertClassifierMatches(classifier(expected, "DataSource"), converted);
		}

		@Test
		@DisplayName("DataSink")
		void dataSink() {
			assertClassifierMatches(classifier(expected, "DataSink"), converted);
		}

		@Test
		@DisplayName("Mapping / FieldsMapEntry")
		void mapping() {
			assertClassifierMatches(classifier(expected, "Mapping"), converted);
			assertClassifierMatches(classifier(expected, "FieldsMapEntry"), converted);
		}

		@Test
		@DisplayName("MappingOperation hierarchy (abstract + Copy/Concat)")
		void mappingOperationHierarchy() {
			assertClassifierMatches(classifier(expected, "MappingOperation"), converted);
			assertClassifierMatches(classifier(expected, "CopyFieldOperation"), converted);
			assertClassifierMatches(classifier(expected, "ConcatFieldOperation"), converted);
		}

		@Test
		@DisplayName("Pipeline / Position / PipelineEdge")
		void pipeline() {
			assertClassifierMatches(classifier(expected, "Pipeline"), converted);
			assertClassifierMatches(classifier(expected, "Position"), converted);
			assertClassifierMatches(classifier(expected, "PipelineEdge"), converted);
		}

		@Test
		@DisplayName("PipelineNode hierarchy (abstract + all node subtypes)")
		void pipelineNodeHierarchy() {
			List<String> diffs = new ArrayList<>();
			for (String name : List.of("PipelineNode", "StartNode", "EndNode", "SourceNode",
					"MappingNode", "SinkNode", "FilterNode", "EnrichNode", "SplitNode")) {
				diffs.addAll(diffClassifier(classifier(expected, name), converted));
			}
			assertNoDiffs("PipelineNode hierarchy", diffs);
		}

		@Test
		@DisplayName("enums (connection types + property-derived enums)")
		void enums() {
			List<String> diffs = new ArrayList<>();
			// Enum names are derived as <OwningClass><Property>; the schema carries no
			// hint for the curated names (HttpMethod/EdgeKind) the hand model used.
			for (String name : List.of("DataSourceConnectionType", "DataSinkConnectionType",
					"DataSinkMethod", "PipelineEdgeKind")) {
				diffs.addAll(diffClassifier(classifier(expected, name), converted));
			}
			assertNoDiffs("enums", diffs);
		}

		@Test
		@DisplayName("MappingField variants (oneOf string|operation → base + variants)")
		void mappingFieldVariants() {
			List<String> diffs = new ArrayList<>();
			for (String name : List.of("MappingFieldBase", "MappingFieldVariant0", "MappingFieldVariant1")) {
				diffs.addAll(diffClassifier(classifier(expected, name), converted));
			}
			assertNoDiffs("MappingField variants", diffs);
		}
	}

	// ========================================================================
	// CORE Mapping
	// ========================================================================

	@Nested
	@DisplayName("mapping.schema.json vs CORE_Mapping.ecore")
	class CoreMappingSchema {

		private EPackage expected;
		private EPackage converted;

		@BeforeEach
		void load() throws IOException {
			expected = loadExpected(CORE_MAPPING_ECORE);
			converted = convertSchema(MAPPING_SCHEMA);
		}

		@Test
		@DisplayName("package metadata (name, nsURI, nsPrefix)")
		void packageMetadata() {
			assertNoDiffs("package metadata", diffPackage(expected, converted));
		}

		@Test
		@DisplayName("all classifiers present and none extra")
		void classifierSet() {
			assertNoDiffs("classifier set", diffClassifierSet(expected, converted));
		}

		@Test
		@DisplayName("root class CORE_Mapping")
		void rootClass() {
			assertClassifierMatches(classifier(expected, "CORE_Mapping"), converted);
		}

		@Test
		@DisplayName("FieldsMapEntry")
		void fieldsMapEntry() {
			assertClassifierMatches(classifier(expected, "FieldsMapEntry"), converted);
		}

		@Test
		@DisplayName("MappingOperation hierarchy (abstract + Copy/Concat/Const)")
		void mappingOperationHierarchy() {
			List<String> diffs = new ArrayList<>();
			for (String name : List.of("MappingOperation", "CopyFieldOperation",
					"ConcatFieldOperation", "ConstFieldOperation")) {
				diffs.addAll(diffClassifier(classifier(expected, name), converted));
			}
			assertNoDiffs("MappingOperation hierarchy", diffs);
		}
	}
}
