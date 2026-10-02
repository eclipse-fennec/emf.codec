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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Locates the OSCAL files of the BSI Stand-der-Technik-Bibliothek
 * (https://github.com/BSI-Bund/Stand-der-Technik-Bibliothek, CC BY-SA 4.0).
 * <p>
 * They are not committed. {@code test-data/README.md} describes how to put them into
 * {@code test-data/bsi}; the system property {@code oscal.bsi.dir} points elsewhere. Without them
 * the tests that need them are skipped.
 * </p>
 */
final class BsiTestData {

	private BsiTestData() {
	}

	static Path dir() {
		return Path.of(System.getProperty("oscal.bsi.dir", "test-data/bsi"));
	}

	/** Condition for {@code @EnabledIf}: the BSI files are there. */
	static boolean present() {
		return !files().isEmpty();
	}

	/** All OSCAL JSON files below {@link #dir()}, sorted by path. */
	static List<Path> files() {
		if (!Files.isDirectory(dir())) {
			return List.of();
		}
		try (Stream<Path> paths = Files.walk(dir())) {
			return paths.filter(p -> p.toString().endsWith(".json")).sorted().toList();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/** The file with the given name. */
	static Path file(String fileName) {
		return files().stream().filter(p -> p.getFileName().toString().equals(fileName)).findFirst()
				.orElseThrow(() -> new IllegalStateException("BSI file missing: " + fileName + " in " + dir()));
	}
}
