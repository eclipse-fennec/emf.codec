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
package org.eclipse.fennec.codec.oscal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.MapEntry;
import gov.nist.csrc.ns.oscal.Mapping;
import gov.nist.csrc.ns.oscal.MappingCollection;
import gov.nist.csrc.ns.oscal.MappingItem;
import tools.jackson.databind.JsonNode;

/**
 * Mapping collections through the generated API (issue #254). A map entry reads
 * <em>source</em> {@code relationship} <em>target</em> (NIST IR 8477): {@code subset-of} says the
 * source is a subset of the target. The codec keeps sources, targets and the relationship literal
 * as they are, so the direction is the one of the document.
 */
@DisplayName("OSCAL mapping-collection through the API")
class MappingCollectionTest {

	static Stream<Path> files() {
		List<Path> files = new ArrayList<>(List.of(Path.of("test-data/fixtures/mapping-single-mapping.json")));
		BsiTestData.files().stream().filter(p -> p.toString().contains("Mappings")).forEach(files::add);
		return files.stream();
	}

	private static MappingCollection load(Path file) throws IOException {
		return ((DocumentRoot) RoundTrip.load(file.toString(), Files.readAllBytes(file)).getContents().get(0))
				.getMappingCollection();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("files")
	void mapsKeepSourceRelationshipTarget(Path file) throws IOException {
		JsonNode json = RoundTrip.JSON.readTree(file.toFile()).get("mapping-collection");
		MappingCollection collection = load(file);

		List<JsonNode> mappings = ComponentDefinitionTest.list(json, "mappings");
		assertEquals(mappings.size(), collection.getMapping().size());
		for (int i = 0; i < mappings.size(); i++) {
			JsonNode mapping = mappings.get(i);
			Mapping model = collection.getMapping().get(i);
			assertEquals(mapping.get("source-resource").get("href").asString(), model.getSourceResource().getHref());
			assertEquals(mapping.get("source-resource").get("type").asString(), model.getSourceResource().getType());
			assertEquals(mapping.get("target-resource").get("href").asString(), model.getTargetResource().getHref());
			assertEquals(mapping.get("target-resource").get("type").asString(), model.getTargetResource().getType());

			List<JsonNode> maps = ComponentDefinitionTest.list(mapping, "maps");
			assertEquals(maps.size(), model.getMap().size());
			for (int m = 0; m < maps.size(); m++) {
				JsonNode map = maps.get(m);
				MapEntry entry = model.getMap().get(m);
				String where = entry.getUuid();
				assertEquals(map.get("uuid").asString(), entry.getUuid());
				assertEquals(map.get("relationship").asString(), entry.getRelationship(), where);
				assertEquals(items(map, "sources"), refs(entry.getSource()), where);
				assertEquals(items(map, "targets"), refs(entry.getTarget()), where);
				assertEquals(ComponentDefinitionTest.text(map, "remarks"), entry.getRemarks(), where);
				assertEquals(ComponentDefinitionTest.list(map, "qualifiers").size(), entry.getQualifier().size(), where);
			}
		}
	}

	/** One entry of the ISO 27001 mapping as anchor: ISO 5.1 is a subset of four GS++ controls. */
	@Test
	@EnabledIf("org.eclipse.fennec.codec.oscal.BsiTestData#present")
	void iso27001Direction() throws IOException {
		Mapping mapping = load(BsiTestData.file("ISO27001-AnnexA-to-GS++-mapping_collection.json")).getMapping().get(0);
		MapEntry entry = mapping.getMap().get(0);
		assertEquals("subset-of", entry.getRelationship());
		assertEquals(List.of("control:5.1"), refs(entry.getSource()));
		assertEquals(List.of("control:GC.6.1.3", "control:GC.6.1.4", "control:GC.9.1", "control:PERF.7.1"),
				refs(entry.getTarget()));
		assertEquals("ISO27001-AnnexA-catalog.json", mapping.getSourceResource().getHref());
		assertEquals("BSI-Methodik-Grundschutz++-catalog.json", mapping.getTargetResource().getHref());
	}

	private static List<String> items(JsonNode map, String name) {
		return ComponentDefinitionTest.list(map, name).stream()
				.map(item -> item.get("type").asString() + ":" + item.get("id-ref").asString()).toList();
	}

	private static List<String> refs(List<MappingItem> items) {
		return items.stream().map(item -> item.getType() + ":" + item.getIdRef()).toList();
	}
}
