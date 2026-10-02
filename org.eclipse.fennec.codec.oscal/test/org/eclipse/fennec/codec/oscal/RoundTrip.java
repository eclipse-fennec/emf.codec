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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;

import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.OSCALPackage;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * One OSCAL file through the codec and back, with everything the round-trip tests look at.
 *
 * @param original the file as JSON tree
 * @param errors the load errors
 * @param warnings the load warnings
 * @param root the loaded document root
 * @param oracle the model compared with the original JSON
 * @param written the written document as JSON tree
 * @param diffs the written document compared with the original
 * @param reloadDiagnostics errors and warnings of loading the written document
 * @param stable whether writing the reloaded document gives the same bytes
 */
record RoundTrip(JsonNode original, List<String> errors, List<String> warnings, DocumentRoot root,
		ModelJsonOracle.Result oracle, JsonNode written, List<String> diffs, List<String> reloadDiagnostics, boolean stable) {

	/** Reads numbers exactly: a double would hide a decimal or a big integer the codec cut short. */
	static final JsonMapper JSON = JsonMapper.builder()
			.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
			.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
			.build();

	private static MetadataWhiteboard metadataService;

	static synchronized MetadataWhiteboard metadataService() {
		if (metadataService == null) {
			EPackage.Registry.INSTANCE.put(OSCALPackage.eNS_URI, OSCALPackage.eINSTANCE);
			metadataService = MetadataServiceFactory.create();
			metadataService.registerPackage(OSCALPackage.eINSTANCE);
		}
		return metadataService;
	}

	static RoundTrip of(Path file) throws IOException {
		byte[] bytes = Files.readAllBytes(file);
		JsonNode original = JSON.readTree(bytes);
		Resource resource = load(file.toString(), bytes);
		// EMF clears the diagnostics of a resource when it is saved
		List<String> errors = messages(resource.getErrors());
		List<String> warnings = messages(resource.getWarnings());
		DocumentRoot root = (DocumentRoot) resource.getContents().get(0);
		ModelJsonOracle.Result oracle = ModelJsonOracle.check(original, root);
		byte[] written = save(resource);
		JsonNode writtenTree = JSON.readTree(written);
		List<String> diffs = JsonDiff.diff(original, writtenTree);
		Resource reloaded = load(file.toString(), written);
		List<String> reloadDiagnostics = new ArrayList<>(messages(reloaded.getErrors()));
		reloadDiagnostics.addAll(messages(reloaded.getWarnings()));
		boolean stable = Arrays.equals(written, save(reloaded));
		return new RoundTrip(original, errors, warnings, root,
				oracle, writtenTree, diffs, reloadDiagnostics, stable);
	}

	static Resource load(String name, byte[] bytes) {
		OscalResourceImpl resource = new OscalResourceImpl(URI.createFileURI(name), metadataService());
		try {
			resource.load(new ByteArrayInputStream(bytes), Map.of());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return resource;
	}

	static byte[] save(Resource resource) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());
		return out.toByteArray();
	}

	static List<String> messages(List<Resource.Diagnostic> diagnostics) {
		return diagnostics.stream().map(Resource.Diagnostic::getMessage).toList();
	}

	/** The number of strings, numbers and booleans in a JSON tree, array elements counted one by one. */
	static int scalarCount(JsonNode node) {
		if (node.isObject() || node.isArray()) {
			return node.valueStream().mapToInt(RoundTrip::scalarCount).sum();
		}
		return node.isNull() ? 0 : 1;
	}
}
