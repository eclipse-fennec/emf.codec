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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Time and heap for the large OSCAL documents: the Grundschutz++ catalog (5.2 MB), the IT-Grundschutz
 * mapping (2.7 MB) and NIST SP 800-53 rev5 (10 MB).
 * <p>
 * The files are local test data (see {@code test-data/README.md}); a missing one is skipped. The
 * limits are generous, so that a slow build machine does not fail the test; the numbers are
 * printed for comparison.
 * </p>
 */
@DisplayName("OSCAL performance")
class OscalPerformanceTest {

	private static final int RUNS = 5;
	private static final long MAX_LOAD_MILLIS = 10_000;

	static Stream<Path> files() {
		List<Path> files = new ArrayList<>();
		if (BsiTestData.present()) {
			files.add(BsiTestData.file("Grundschutz++-resolved_catalog.json"));
			files.add(BsiTestData.file("ITGS-to-GS++-mapping_collection.json"));
		}
		files.add(Path.of("test-data/nist-large/NIST_SP-800-53_rev5_catalog.json"));
		return files.stream();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("files")
	void loadAndSave(Path file) throws IOException {
		Assumptions.assumeTrue(Files.exists(file), "local test data missing: " + file);
		byte[] bytes = Files.readAllBytes(file);
		byte[] firstWritten = null;
		long bestLoad = Long.MAX_VALUE;
		long bestSave = Long.MAX_VALUE;
		long heap = 0;
		for (int run = 0; run < RUNS; run++) {
			System.gc();
			long usedBefore = usedHeap();
			long t0 = System.nanoTime();
			Resource resource = RoundTrip.load(file.toString(), bytes);
			long t1 = System.nanoTime();
			heap = Math.max(heap, usedHeap() - usedBefore);
			byte[] written = RoundTrip.save(resource);
			long t2 = System.nanoTime();
			bestLoad = Math.min(bestLoad, (t1 - t0) / 1_000_000);
			bestSave = Math.min(bestSave, (t2 - t1) / 1_000_000);
			if (firstWritten == null) {
				firstWritten = written;
				assertEquals(List.of(), JsonDiff.diff(RoundTrip.JSON.readTree(bytes), RoundTrip.JSON.readTree(written)),
						"round trip");
			}
		}
		System.out.printf("PERF %s: %.1f MB, load %d ms, save %d ms, model heap ~%d MB (best of %d)%n",
				file.getFileName(), bytes.length / 1e6, bestLoad, bestSave, heap / 1_000_000, RUNS);
		assertTrue(bestLoad < MAX_LOAD_MILLIS, "load took " + bestLoad + " ms");
	}

	private static long usedHeap() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();
	}
}
