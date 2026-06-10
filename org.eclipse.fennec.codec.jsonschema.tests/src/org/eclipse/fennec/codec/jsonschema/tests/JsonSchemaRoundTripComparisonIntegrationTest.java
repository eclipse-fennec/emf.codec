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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.test.common.annotation.InjectService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * OSGi integration test that round-trips the bundled JSON Schema documents
 * ({@code core-ir.schema.json}, {@code mapping.schema.json}) through EMF and
 * back — JSON Schema &rarr; {@link EPackage} &rarr; JSON Schema — then compares
 * the regenerated schema against the original to surface <em>gaps in the reverse
 * (EPackage &rarr; JSON Schema) conversion</em>.
 * <p>
 * The comparison is a <em>semantic</em> JSON-Schema diff, not a byte/tree
 * equality: keywords that the converter legitimately rewrites for cosmetic or
 * representational reasons are normalised or ignored so that the report shows
 * structural losses rather than formatting churn. Specifically ignored:
 * </p>
 * <ul>
 *   <li>documentation keywords: {@code description}, {@code title},
 *       {@code $comment}, {@code examples}, {@code deprecated}</li>
 *   <li>identity keywords: {@code $schema}, {@code $id}, {@code $anchor}</li>
 *   <li>vendor extensions (any {@code x-*} key) and converter-injected
 *       {@code default} values</li>
 *   <li>nullability decoration: {@code "type": ["string","null"]} is normalised
 *       to {@code "string"} before comparison</li>
 * </ul>
 * <p>
 * Everything else ({@code type}, {@code required}, {@code properties},
 * {@code items}, {@code enum}, {@code const}, {@code $ref} targets,
 * {@code oneOf}/{@code anyOf}/{@code allOf}, {@code additionalProperties},
 * {@code $defs} membership, numeric bounds, &hellip;) is compared. Each diff is
 * collected into a human-readable report keyed by JSON-pointer-ish path.
 * </p>
 * <p>
 * Because the forward conversion is intentionally lossy/transforming (enum
 * renaming, {@code oneOf}-of-{@code $ref} &rarr; abstract supertype,
 * {@code additionalProperties} maps &rarr; {@code …MapEntry} classes), the
 * round-trip is <em>not</em> expected to reproduce the source verbatim. The
 * {@code @Test} methods therefore assert the round-trip is well-formed and the
 * report is attached for inspection; the {@link #reportGaps} helper prints the
 * full diff so the reverse-converter gaps are visible in the test output.
 * </p>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("JSON Schema round-trip (schema → Ecore → schema) vs original")
public class JsonSchemaRoundTripComparisonIntegrationTest {

	private static final String BASE = "/org/eclipse/fennec/codec/jsonschema/tests/";

	private static final String CORE_IR_SCHEMA = BASE + "core-ir.schema.json";
	private static final String MAPPING_SCHEMA = BASE + "mapping.schema.json";

	/** Keywords that carry no structural meaning for the gap analysis. */
	private static final Set<String> IGNORED_KEYWORDS = Set.of(
			"description", "title", "$comment", "examples", "example", "deprecated",
			"$schema", "$id", "$anchor", "default");

	/**
	 * The known reverse-conversion gaps for {@code core-ir.schema.json}, grouped by
	 * cause. These are <em>not</em> defects in this test — they document where the
	 * {@code EPackage → JSON Schema} path currently loses information that the
	 * source schema carried. When the reverse converter is improved, the matching
	 * entries here must be removed (a shrinking list is the goal); a brand-new
	 * entry signals a regression. See the class javadoc for what is normalised away
	 * before this comparison (allOf inheritance, additionalProperties:false policy,
	 * documentation/identity keywords, nullability decoration).
	 */
	private static final List<String> EXPECTED_CORE_IR_GAPS = List.of(
			// (1) Synthetic classifiers (inline enums, additionalProperties map-entry
			//     classes, the anonymous dataStructures value type) leak into $defs as
			//     named definitions the source never had.
			"#/$defs: unexpected entries [DataSinkConnectionType, DataSinkMethod, DataSourceConnectionType, DataStructure, DataStructuresMapEntry, FieldsMapEntry, PipelineEdgeKind]",

			// (2) additionalProperties maps round-trip as type:array (the …MapEntry
			//     transformation is not reversed): the map value-schema is dropped, the
			//     type flips object→array, an items schema appears, and the property is
			//     no longer required.
			"#/$defs/Mapping/properties/fields/additionalProperties: missing additionalProperties (expected {\"$ref\":\"#/$defs/MappingField\"})",
			"#/$defs/Mapping/properties/fields/items: present on round-trip only",
			"#/$defs/Mapping/properties/fields/type: type expected [object] but was [array]",
			"#/$defs/Mapping/required: required expected [fields, id, source, target] but was [id, source, target]",
			"#/properties/dataStructures/additionalProperties: missing additionalProperties (expected {\"type\":\"object\"})",
			"#/properties/dataStructures/items: present on round-trip only",
			"#/properties/dataStructures/type: type expected [object] but was [array]",

			// (3) oneOf unions are not reconstructed: the abstract base loses its oneOf
			//     and becomes a bare object; the MappingField string|$ref variants are
			//     mangled (string variant becomes an object, the $ref variant becomes an
			//     inline {value} object).
			"#/$defs/MappingField/oneOf[0]/type: type expected [string] but was [object]",
			"#/$defs/MappingField/oneOf[1]/$ref: missing '$ref' (expected \"#/$defs/MappingOperation\")",
			"#/$defs/MappingField/oneOf[1]/properties: unexpected entries [value]",
			"#/$defs/MappingField/oneOf[1]/required: required expected [] but was [value]",
			"#/$defs/MappingField/oneOf[1]/type: type expected [] but was [object]",
			"#/$defs/MappingOperation/oneOf: present on original only",
			"#/$defs/MappingOperation/type: type expected [] but was [object]",
			"#/$defs/PipelineNode/oneOf: present on original only",
			"#/$defs/PipelineNode/type: type expected [] but was [object]",

			// (4) $ref collections emitted as non-containment lose type:array and their
			//     required membership.
			"#/$defs/Pipeline/properties/edges/type: type expected [array] but was []",
			"#/$defs/Pipeline/properties/nodes/type: type expected [array] but was []",
			"#/$defs/Pipeline/required: required expected [edges, id, nodes] but was [id]",
			"#/properties/dataSinks/type: type expected [array] but was []",
			"#/properties/dataSources/type: type expected [array] but was []",
			"#/properties/mappings/type: type expected [array] but was []",
			"#/properties/pipelines/type: type expected [array] but was []");

	/** The known reverse-conversion gaps for {@code mapping.schema.json}; see {@link #EXPECTED_CORE_IR_GAPS}. */
	private static final List<String> EXPECTED_MAPPING_GAPS = List.of(
			// (1) Synthetic map-entry class leaks into $defs.
			"#/$defs: unexpected entries [FieldsMapEntry]",

			// (2) additionalProperties map round-trips as type:array.
			"#/properties/fields/additionalProperties: missing additionalProperties (expected {\"$ref\":\"#/$defs/MappingField\"})",
			"#/properties/fields/items: present on round-trip only",
			"#/properties/fields/type: type expected [object] but was [array]",
			"#/required: required expected [fields, id, source, target] but was [id, source, target]",

			// (3) oneOf unions are not reconstructed.
			"#/$defs/MappingField/oneOf[0]/type: type expected [string] but was [object]",
			"#/$defs/MappingField/oneOf[1]/$ref: missing '$ref' (expected \"#/$defs/MappingOperation\")",
			"#/$defs/MappingField/oneOf[1]/properties: unexpected entries [value]",
			"#/$defs/MappingField/oneOf[1]/required: required expected [] but was [value]",
			"#/$defs/MappingField/oneOf[1]/type: type expected [] but was [object]",
			"#/$defs/MappingOperation/oneOf: present on original only",
			"#/$defs/MappingOperation/type: type expected [] but was [object]");

	@InjectService(filter = "(emf.contentType=application/schema+json)")
	ResourceSet resourceSet;

	private final ObjectMapper mapper = JsonMapper.builder().build();

	/** Root documents for the comparison in progress, used to resolve local {@code $ref}s. */
	private JsonNode expectedRoot;
	private JsonNode actualRoot;

	// ========================================================================
	// Round-trip plumbing
	// ========================================================================

	/** Reads a JSON document from the test classpath into a tree. */
	private JsonNode loadJson(String classpathResource) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(classpathResource)) {
			assertNotNull(in, "JSON resource not found on classpath: " + classpathResource);
			return mapper.readTree(in);
		}
	}

	/**
	 * Round-trips a JSON Schema document: load it into an {@link EPackage} via the
	 * registered {@code application/schema+json} resource, then save that EPackage
	 * back out as JSON Schema and parse the result into a tree.
	 */
	private JsonNode roundTrip(String classpathResource) throws IOException {
		Resource resource = resourceSet.createResource(URI.createURI("roundtrip.jsonschema"));
		try (InputStream in = getClass().getResourceAsStream(classpathResource)) {
			assertNotNull(in, "JSON Schema resource not found on classpath: " + classpathResource);
			resource.load(in, null);
		}
		assertFalse(resource.getContents().isEmpty(),
				"conversion of " + classpathResource + " produced no content");
		assertTrue(resource.getContents().get(0) instanceof EPackage,
				"conversion root should be an EPackage, was: " + resource.getContents().get(0));

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, null);
		return mapper.readTree(out.toByteArray());
	}

	// ========================================================================
	// Semantic JSON-Schema diff
	// ========================================================================

	/** Normalises a {@code type} node, stripping the {@code null} nullability marker. */
	private static Set<String> normalizedTypes(JsonNode typeNode) {
		Set<String> types = new TreeSet<>();
		if (typeNode == null || typeNode.isNull()) {
			return types;
		}
		if (typeNode.isArray()) {
			for (JsonNode t : typeNode) {
				if (!"null".equals(t.asString())) {
					types.add(t.asString());
				}
			}
		} else {
			types.add(typeNode.asString());
		}
		return types;
	}

	private static Set<String> stringSet(JsonNode arrayNode) {
		Set<String> result = new TreeSet<>();
		if (arrayNode != null && arrayNode.isArray()) {
			for (JsonNode n : arrayNode) {
				result.add(n.asString());
			}
		}
		return result;
	}

	private static Set<String> fieldNames(JsonNode objectNode) {
		Set<String> names = new TreeSet<>();
		if (objectNode != null && objectNode.isObject()) {
			names.addAll(objectNode.propertyNames());
		}
		return names;
	}

	/**
	 * Resolves a local JSON pointer {@code $ref} (e.g. {@code #/$defs/Foo}) against
	 * the given root document, or returns {@code null} for non-local / unresolvable refs.
	 */
	private static JsonNode resolveRef(JsonNode root, String ref) {
		if (root == null || ref == null || !ref.startsWith("#/")) {
			return null;
		}
		JsonNode current = root;
		for (String segment : ref.substring(2).split("/")) {
			if (current == null) {
				return null;
			}
			current = current.get(segment.replace("~1", "/").replace("~0", "~"));
		}
		return current;
	}

	/**
	 * Returns the <em>effective</em> schema for a node, merging any {@code allOf}
	 * members (and the local {@code $ref}s they point to) into a single object so
	 * that inheritance expressed as {@code allOf:[{$ref:parent},{…own…}]} compares
	 * equal to the flat definition it was derived from. Idempotent for nodes
	 * without {@code allOf}.
	 */
	private JsonNode effective(JsonNode node, JsonNode root, Set<String> seenRefs) {
		if (node == null || !node.isObject() || !node.has("allOf")) {
			return node;
		}
		ObjectNode merged = mapper.createObjectNode();
		// Start from the node's own keywords (minus allOf).
		node.properties().forEach(e -> {
			if (!"allOf".equals(e.getKey())) {
				merged.set(e.getKey(), e.getValue());
			}
		});
		for (JsonNode member : node.get("allOf")) {
			JsonNode resolved = member;
			if (member.isObject() && member.has("$ref")) {
				String ref = member.get("$ref").asString();
				if (!seenRefs.add(ref)) {
					continue; // cycle guard
				}
				resolved = resolveRef(root, ref);
			}
			JsonNode eff = effective(resolved, root, seenRefs);
			mergeSchema(merged, eff);
		}
		return merged;
	}

	/** Merges schema {@code source} into {@code target} (union of properties/required, fill-if-absent for the rest). */
	private void mergeSchema(ObjectNode target, JsonNode source) {
		if (source == null || !source.isObject()) {
			return;
		}
		source.properties().forEach(e -> {
			String key = e.getKey();
			JsonNode value = e.getValue();
			if ("properties".equals(key)) {
				ObjectNode props = target.has("properties") && target.get("properties").isObject()
						? (ObjectNode) target.get("properties")
						: target.putObject("properties");
				value.properties().forEach(p -> {
					if (!props.has(p.getKey())) {
						props.set(p.getKey(), p.getValue());
					}
				});
			} else if ("required".equals(key) && value.isArray()) {
				ArrayNode req = target.has("required") && target.get("required").isArray()
						? (ArrayNode) target.get("required")
						: target.putArray("required");
				Set<String> have = stringSet(req);
				for (JsonNode r : value) {
					if (have.add(r.asString())) {
						req.add(r.asString());
					}
				}
			} else if (!target.has(key)) {
				target.set(key, value);
			}
		});
	}

	/** Compares two schema-position nodes, accumulating human-readable diffs. */
	private void diffNode(String path, JsonNode expected, JsonNode actual, List<String> diffs) {
		if (expected == null || actual == null) {
			diffs.add(path + ": present on " + (expected != null ? "original" : "round-trip") + " only");
			return;
		}
		expected = effective(expected, expectedRoot, new java.util.HashSet<>());
		actual = effective(actual, actualRoot, new java.util.HashSet<>());
		if (!expected.isObject() || !actual.isObject()) {
			if (!expected.equals(actual)) {
				diffs.add(path + ": value expected " + expected + " but was " + actual);
			}
			return;
		}

		Set<String> keys = new LinkedHashSet<>();
		fieldNames(expected).forEach(keys::add);
		fieldNames(actual).forEach(keys::add);

		for (String key : keys) {
			if (IGNORED_KEYWORDS.contains(key) || key.startsWith("x-")) {
				continue;
			}
			JsonNode exp = expected.get(key);
			JsonNode act = actual.get(key);
			String p = path + "/" + key;

			switch (key) {
			case "type": {
				Set<String> e = normalizedTypes(exp);
				Set<String> a = normalizedTypes(act);
				if (!e.equals(a)) {
					diffs.add(p + ": type expected " + e + " but was " + a);
				}
				break;
			}
			case "required":
			case "enum": {
				Set<String> e = stringSet(exp);
				Set<String> a = stringSet(act);
				if (!e.equals(a)) {
					diffs.add(p + ": " + key + " expected " + e + " but was " + a);
				}
				break;
			}
			case "properties":
			case "$defs":
			case "definitions": {
				diffSchemaMap(p, exp, act, diffs);
				break;
			}
			case "oneOf":
			case "anyOf":
			case "allOf": {
				diffSchemaArray(p, exp, act, diffs);
				break;
			}
			case "items": {
				diffNode(p, exp, act, diffs);
				break;
			}
			case "additionalProperties": {
				// The converter writes additionalProperties:false on every closed object as
				// a policy; that is not a reverse-conversion gap, so ignore it when the
				// original is silent. A map value-schema being dropped/changed IS a gap.
				if (exp == null) {
					if (act != null && !(act.isBoolean() && !act.booleanValue())) {
						diffs.add(p + ": unexpected additionalProperties = " + act);
					}
				} else if (act == null) {
					diffs.add(p + ": missing additionalProperties (expected " + exp + ")");
				} else if (exp.isObject() && act.isObject()) {
					diffNode(p, exp, act, diffs);
				} else if (!exp.equals(act)) {
					diffs.add(p + ": additionalProperties expected " + exp + " but was " + act);
				}
				break;
			}
			default: {
				diffGeneric(p, key, exp, act, diffs);
				break;
			}
			}
		}
	}

	private void diffGeneric(String path, String key, JsonNode exp, JsonNode act, List<String> diffs) {
		if (exp == null) {
			diffs.add(path + ": unexpected '" + key + "' = " + act);
			return;
		}
		if (act == null) {
			diffs.add(path + ": missing '" + key + "' (expected " + exp + ")");
			return;
		}
		if (exp.isObject() && act.isObject()) {
			diffNode(path, exp, act, diffs);
		} else if (!exp.equals(act)) {
			diffs.add(path + ": " + key + " expected " + exp + " but was " + act);
		}
	}

	/** Compares a {@code name -> schema} map (properties/$defs/definitions). */
	private void diffSchemaMap(String path, JsonNode expected, JsonNode actual, List<String> diffs) {
		Set<String> expNames = fieldNames(expected);
		Set<String> actNames = fieldNames(actual);

		Set<String> missing = new TreeSet<>(expNames);
		missing.removeAll(actNames);
		if (!missing.isEmpty()) {
			diffs.add(path + ": missing entries " + missing);
		}
		Set<String> extra = new TreeSet<>(actNames);
		extra.removeAll(expNames);
		if (!extra.isEmpty()) {
			diffs.add(path + ": unexpected entries " + extra);
		}

		Set<String> common = new TreeSet<>(expNames);
		common.retainAll(actNames);
		for (String name : common) {
			diffNode(path + "/" + name, expected.get(name), actual.get(name), diffs);
		}
	}

	/** Compares positional schema arrays (oneOf/anyOf/allOf). */
	private void diffSchemaArray(String path, JsonNode expected, JsonNode actual, List<String> diffs) {
		if (expected == null || actual == null) {
			diffs.add(path + ": present on " + (expected != null ? "original" : "round-trip") + " only");
			return;
		}
		if (!(expected instanceof ArrayNode expArr) || !(actual instanceof ArrayNode actArr)) {
			diffs.add(path + ": expected array on both sides (expected="
					+ expected.getNodeType() + ", actual=" + actual.getNodeType() + ")");
			return;
		}
		if (expArr.size() != actArr.size()) {
			diffs.add(path + ": size expected " + expArr.size() + " but was " + actArr.size());
		}
		int n = Math.min(expArr.size(), actArr.size());
		for (int i = 0; i < n; i++) {
			diffNode(path + "[" + i + "]", expArr.get(i), actArr.get(i), diffs);
		}
	}

	// ========================================================================
	// Reporting
	// ========================================================================

	/**
	 * Computes the gap report between an original schema and its round-tripped
	 * counterpart, prints it to stdout, and returns the diff list.
	 */
	private List<String> reportGaps(String label, JsonNode original, JsonNode roundTripped) {
		this.expectedRoot = original;
		this.actualRoot = roundTripped;
		List<String> diffs = new ArrayList<>();
		diffNode("#", original, roundTripped, diffs);
		diffs.sort(String::compareTo);

		StringBuilder sb = new StringBuilder();
		sb.append("\n==== Reverse-conversion gap report: ").append(label).append(" ====\n");
		if (diffs.isEmpty()) {
			sb.append("  (no structural gaps — round-trip reproduced the original)\n");
		} else {
			sb.append("  ").append(diffs.size()).append(" structural gap(s):\n");
			for (String d : diffs) {
				sb.append("  - ").append(d).append('\n');
			}
		}
		System.out.println(sb);
		return diffs;
	}

	// ========================================================================
	// core-ir.schema.json
	// ========================================================================

	@Nested
	@DisplayName("core-ir.schema.json round-trip")
	class CoreDataSetRoundTrip {

		private JsonNode original;
		private JsonNode roundTripped;

		@org.junit.jupiter.api.BeforeEach
		void prepare() throws IOException {
			original = loadJson(CORE_IR_SCHEMA);
			roundTripped = roundTrip(CORE_IR_SCHEMA);
		}

		@Test
		@DisplayName("round-trip is well-formed (object root, same root type, $defs present)")
		void wellFormed() {
			assertTrue(roundTripped.isObject(), "round-tripped schema should be a JSON object");
			assertEquals(normalizedTypes(original.get("type")), normalizedTypes(roundTripped.get("type")),
					"root type should survive the round-trip");
			assertTrue(roundTripped.has("$defs") || !original.has("$defs"),
					"round-tripped schema should keep a $defs section when the original had one");
		}

		@Test
		@DisplayName("reverse-conversion gaps match the documented baseline")
		void gapBaseline() {
			List<String> diffs = reportGaps("core-ir.schema.json", original, roundTripped);
			assertGapsMatchBaseline("core-ir.schema.json", EXPECTED_CORE_IR_GAPS, diffs);
		}
	}

	// ========================================================================
	// mapping.schema.json
	// ========================================================================

	@Nested
	@DisplayName("mapping.schema.json round-trip")
	class CoreMappingRoundTrip {

		private JsonNode original;
		private JsonNode roundTripped;

		@org.junit.jupiter.api.BeforeEach
		void prepare() throws IOException {
			original = loadJson(MAPPING_SCHEMA);
			roundTripped = roundTrip(MAPPING_SCHEMA);
		}

		@Test
		@DisplayName("round-trip is well-formed (object root, same root type, $defs present)")
		void wellFormed() {
			assertTrue(roundTripped.isObject(), "round-tripped schema should be a JSON object");
			assertEquals(normalizedTypes(original.get("type")), normalizedTypes(roundTripped.get("type")),
					"root type should survive the round-trip");
			assertTrue(roundTripped.has("$defs") || !original.has("$defs"),
					"round-tripped schema should keep a $defs section when the original had one");
		}

		@Test
		@DisplayName("reverse-conversion gaps match the documented baseline")
		void gapBaseline() {
			List<String> diffs = reportGaps("mapping.schema.json", original, roundTripped);
			assertGapsMatchBaseline("mapping.schema.json", EXPECTED_MAPPING_GAPS, diffs);
		}
	}

	private static void assertEquals(Object expected, Object actual, String message) {
		org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
	}

	/**
	 * Asserts the observed reverse-conversion gaps exactly match the documented
	 * baseline. A failure means the reverse converter changed: either a gap was
	 * closed (remove the matching baseline entries) or a new one appeared (a
	 * regression to investigate). The message spells out both directions.
	 */
	private static void assertGapsMatchBaseline(String label, List<String> baseline, List<String> actual) {
		Set<String> expectedSet = new TreeSet<>(baseline);
		Set<String> actualSet = new TreeSet<>(actual);
		if (expectedSet.equals(actualSet)) {
			return;
		}
		Set<String> closed = new TreeSet<>(expectedSet);
		closed.removeAll(actualSet);
		Set<String> appeared = new TreeSet<>(actualSet);
		appeared.removeAll(expectedSet);

		StringBuilder sb = new StringBuilder();
		sb.append(label).append(": reverse-conversion gaps no longer match the baseline.\n");
		if (!closed.isEmpty()) {
			sb.append("  Gaps CLOSED (remove these from the baseline):\n");
			closed.forEach(g -> sb.append("    - ").append(g).append('\n'));
		}
		if (!appeared.isEmpty()) {
			sb.append("  NEW gaps (regression — investigate, or add to the baseline if intended):\n");
			appeared.forEach(g -> sb.append("    + ").append(g).append('\n'));
		}
		org.junit.jupiter.api.Assertions.fail(sb.toString());
	}
}
